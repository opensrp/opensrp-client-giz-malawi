package org.smartregister.giz.processor;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.MoveToMyCatchmentUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.repository.MonthlyTalliesRepository;
import org.smartregister.giz.util.AppExecutors;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.service.intent.HeightIntentService;
import org.smartregister.growthmonitoring.service.intent.WeightIntentService;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.service.intent.RecurringIntentService;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.opd.processor.OpdMiniClientProcessorForJava;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.pojo.PncRegistrationDetails;
import org.smartregister.pnc.pojo.PncStillBorn;
import org.smartregister.pnc.processor.PncMiniClientProcessorForJava;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

public class GizMalawiProcessorForJava extends ClientProcessorForJava {

    private static GizMalawiProcessorForJava instance;

    private HashMap<String, MiniClientProcessorForJava> processorMap = new HashMap<>();
    private HashMap<MiniClientProcessorForJava, List<Event>> unsyncEventsPerProcessor = new HashMap<>();
    private AppExecutors appExecutors = new AppExecutors();
    private HashMap<String, DateTime> clientsForAlertUpdates = new HashMap<>();
    private List<String> coreProcessedEvents = Arrays.asList(Constants.EventType.BITRH_REGISTRATION, Constants.EventType.UPDATE_BITRH_REGISTRATION,
            Constants.EventType.NEW_WOMAN_REGISTRATION, OpdConstants.EventType.OPD_REGISTRATION, OpdConstants.EventType.UPDATE_OPD_REGISTRATION,
            Constants.EventType.AEFI, Constants.EventType.ARCHIVE_CHILD_RECORD, ConstantsUtils.EventTypeUtils.ANC_MATERNITY_TRANSFER, ConstantsUtils.EventTypeUtils.CLOSE);

    private GizMalawiProcessorForJava(Context context) {
        super(context);

        BaseAncClientProcessorForJava baseAncClientProcessorForJava = new BaseAncClientProcessorForJava(context);
        GizMaternityProcessorForJava maternityMiniClientProcessorForJava = new GizMaternityProcessorForJava(context);
        PncMiniClientProcessorForJava pncMiniClientProcessorForJava = new PncMiniClientProcessorForJava(context);
        OpdMiniClientProcessorForJava opdMiniClientProcessorForJava = new OpdMiniClientProcessorForJava(context);

        addMiniProcessors(baseAncClientProcessorForJava, opdMiniClientProcessorForJava, maternityMiniClientProcessorForJava, pncMiniClientProcessorForJava);
    }

    public static GizMalawiProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new GizMalawiProcessorForJava(context);
        }
        return instance;
    }

    public void addMiniProcessors(MiniClientProcessorForJava... miniClientProcessorsForJava) {
        for (MiniClientProcessorForJava miniClientProcessorForJava : miniClientProcessorsForJava) {
            unsyncEventsPerProcessor.put(miniClientProcessorForJava, new ArrayList<Event>());

            HashSet<String> eventTypes = miniClientProcessorForJava.getEventTypes();

            for (String eventType : eventTypes) {
                processorMap.put(eventType, miniClientProcessorForJava);
            }
        }
    }

    @Override
    public void processClient(List<EventClient> eventClients) throws Exception {
        ClientClassification clientClassification = assetJsonToJava("ec_client_classification.json",
                ClientClassification.class);
        Table vaccineTable = assetJsonToJava("ec_client_vaccine.json", Table.class);
        Table weightTable = assetJsonToJava("ec_client_weight.json", Table.class);
        Table heightTable = assetJsonToJava("ec_client_height.json", Table.class);
        Table serviceTable = assetJsonToJava("ec_client_service.json", Table.class);

        if (!eventClients.isEmpty()) {
            List<Event> unsyncEvents = new ArrayList<>();
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                String eventType = event.getEventType();
                if (eventType == null) {
                    continue;
                }

                if (eventType.equals(VaccineIntentService.EVENT_TYPE) || eventType
                        .equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT)) {
                    processVaccinationEvent(vaccineTable, eventClient, event, eventType);
                } else if (eventType.equals(WeightIntentService.EVENT_TYPE) || eventType
                        .equals(WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT)) {
                    processWeightEvent(weightTable, heightTable, eventClient, eventType);
                } else if (eventType.equals(RecurringIntentService.EVENT_TYPE)) {
                    if (serviceTable == null) {
                        continue;
                    }
                    processService(eventClient, serviceTable);
                } else if (eventType.equals(JsonFormUtils.BCG_SCAR_EVENT)) {
                    processBCGScarEvent(eventClient);
                } else if (eventType.equals(MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT)) {
                    unsyncEvents.add(event);
                } else if (eventType.equals(Constants.EventType.DEATH)) {
                    if (processDeathEvent(eventClient, clientClassification)) {
                        unsyncEvents.add(event);
                    }
                } else if (eventType.equals(GizConstants.EventType.REPORT_CREATION)) {
                    processReport(event);
                    CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                } else if (eventType.equals(GizConstants.EventType.MATERNITY_PNC_TRANSFER)) {
                    processMaternityPncTransfer(eventClient);
                    CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                } else if (coreProcessedEvents.contains(eventType)) {

                    if (eventType.equals(OpdConstants.EventType.OPD_REGISTRATION) && eventClient.getClient() == null) {
                        Timber.e(new Exception(), "Cannot find client corresponding to %s with base-entity-id %s", OpdConstants.EventType.OPD_REGISTRATION, event.getBaseEntityId());
                        continue;
                    }

                    if (eventType.equals(OpdConstants.EventType.OPD_REGISTRATION) && eventClient.getClient() != null) {
                        GizMalawiApplication.getInstance().registerTypeRepository().addUnique(GizConstants.RegisterType.OPD, event.getBaseEntityId());
                    }

                    if (eventType.equals(Constants.EventType.BITRH_REGISTRATION) && eventClient.getClient() != null &&
                            (GizMalawiApplication.getInstance().context().getEventClientRepository().getEventsByBaseEntityIdAndEventType(event.getBaseEntityId(), Constants.EventType.ARCHIVE_CHILD_RECORD) == null)) {
                        GizMalawiApplication.getInstance().registerTypeRepository().addUnique(GizConstants.RegisterType.CHILD, event.getBaseEntityId());
                    }

                    if (eventType.equals(Constants.EventType.NEW_WOMAN_REGISTRATION) && eventClient.getClient() != null) {
                        GizMalawiApplication.getInstance().registerTypeRepository().addUnique(GizConstants.RegisterType.OPD, event.getBaseEntityId());
                    }

                    if (eventType.equals(Constants.EventType.ARCHIVE_CHILD_RECORD) && eventClient.getClient() != null) {
                        GizMalawiApplication.getInstance().registerTypeRepository().removeAll(event.getBaseEntityId());
                    }

                    if (eventType.equals(ConstantsUtils.EventTypeUtils.CLOSE) && eventClient.getClient() != null) {
                        GizMalawiApplication.getInstance().registerTypeRepository().removeAll(event.getBaseEntityId());
                        if (!GizMalawiApplication.getInstance().gizEventRepository().hasEvent(event.getBaseEntityId(), ConstantsUtils.EventTypeUtils.ANC_MATERNITY_TRANSFER))
                            GizMalawiApplication.getInstance().registerTypeRepository().add(GizConstants.RegisterType.OPD, event.getBaseEntityId());
                        else
                            GizMalawiApplication.getInstance().registerTypeRepository().add(GizConstants.RegisterType.MATERNITY, event.getBaseEntityId());
                    }


                    if (eventType.equals(ConstantsUtils.EventTypeUtils.ANC_MATERNITY_TRANSFER) && eventClient.getClient() != null) {
                        GizMalawiApplication.getInstance().registerTypeRepository().removeAll(event.getBaseEntityId());
                        GizMalawiApplication.getInstance().registerTypeRepository().add(GizConstants.RegisterType.MATERNITY, event.getBaseEntityId());
                    }


                    if (clientClassification == null) {
                        continue;
                    }

                    processEventClient(clientClassification, eventClient, event);
                } else if (processorMap.containsKey(eventType)) {
                    try {
                        if (eventType.equals(ConstantsUtils.EventTypeUtils.REGISTRATION) && eventClient.getClient() != null) {
                            GizMalawiApplication.getInstance().registerTypeRepository().addUnique(GizConstants.RegisterType.ANC, event.getBaseEntityId());
                        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_REGISTRATION) && eventClient.getClient() != null) {
                            GizMalawiApplication.getInstance().registerTypeRepository().addUnique(GizConstants.RegisterType.MATERNITY, event.getBaseEntityId());
                        } else if (eventType.equals(PncConstants.EventTypeConstants.PNC_REGISTRATION) && eventClient.getClient() != null) {
                            GizMalawiApplication.getInstance().registerTypeRepository().addUnique(GizConstants.RegisterType.PNC, event.getBaseEntityId());
                        } else if ((eventType.equals(MaternityConstants.EventType.MATERNITY_CLOSE) || eventType.equals(PncConstants.EventTypeConstants.PNC_CLOSE)) && eventClient.getClient() != null) {

                            if (!event.getObs().isEmpty()) {
                                Obs obs = event.getObs().get(0);
                                if (!obs.getHumanReadableValues().isEmpty()) {
                                    String humanReadableValue = (String) obs.getHumanReadableValues().get(0);
                                    if (!"woman died".equalsIgnoreCase(humanReadableValue) && !"wrong entry".equalsIgnoreCase(humanReadableValue)) {
                                        GizMalawiApplication.getInstance().registerTypeRepository().removeAll(event.getBaseEntityId());
                                        GizMalawiApplication.getInstance().registerTypeRepository().add(GizConstants.RegisterType.OPD, event.getBaseEntityId());
                                    }
                                }
                            }
                        }

                        processEventUsingMiniprocessor(clientClassification, eventClient, eventType);
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                }
            }

            // Unsync events that are should not be in this device
            processUnsyncEvents(unsyncEvents);
            // Process alerts for clients
            Runnable runnable = () -> updateClientAlerts(clientsForAlertUpdates);

            appExecutors.diskIO().execute(runnable);
        }

    }

    private void processMaternityPncTransfer(@NonNull EventClient eventClient) {
        Event event = eventClient.getEvent();
        HashMap<String, String> fieldsMap = GizUtils.generateKeyValuesFromEvent(event);
        String babiesBornMap = fieldsMap.get(MaternityConstants.JSON_FORM_KEY.BABIES_BORN_MAP);
        if (StringUtils.isNotBlank(babiesBornMap)) {
            processBabiesBorn(babiesBornMap, event);
        }

        String stillBornMap = fieldsMap.get(MaternityConstants.JSON_FORM_KEY.BABIES_STILL_BORN_MAP);
        if (StringUtils.isNotBlank(stillBornMap)) {
            processStillBorn(stillBornMap, event);
        }

        PncRegistrationDetails pncDetails = new PncRegistrationDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), fieldsMap);
        pncDetails.setCreatedAt(new Date());

        PncLibrary.getInstance().getPncRegistrationDetailsRepository().saveOrUpdate(pncDetails);

        GizMalawiApplication.getInstance().registerTypeRepository().removeAll(event.getBaseEntityId());
        GizMalawiApplication.getInstance().registerTypeRepository().add(GizConstants.RegisterType.PNC, event.getBaseEntityId());
    }

    //TODO make it available from PNC
    private void processBabiesBorn(@android.support.annotation.Nullable String strBabiesBorn, @NonNull Event event) {
        if (StringUtils.isNotBlank(strBabiesBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strBabiesBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonChildObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncChild pncChild = new PncChild();
                    pncChild.setMotherBaseEntityId(event.getBaseEntityId());
                    pncChild.setDischargedAlive(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.DISCHARGED_ALIVE));
                    pncChild.setChildRegistered(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.CHILD_REGISTERED));
                    pncChild.setBirthRecordDate(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_RECORD));
                    pncChild.setFirstName(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_FIRST_NAME));
                    pncChild.setLastName(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_LAST_NAME));
                    pncChild.setDob(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_DOB));
                    pncChild.setGender(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_GENDER));
                    pncChild.setWeightEntered(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_WEIGHT_ENTERED));
                    pncChild.setWeight(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_WEIGHT));
                    pncChild.setHeightEntered(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BIRTH_HEIGHT_ENTERED));
                    pncChild.setApgar(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.APGAR));
                    pncChild.setFirstCry(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_FIRST_CRY));
                    pncChild.setComplications(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_COMPLICATIONS));
                    pncChild.setComplicationsOther(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_COMPLICATIONS_OTHER));
                    pncChild.setCareMgt(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_CARE_MGMT));
                    pncChild.setCareMgtSpecify(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_CARE_MGMT_SPECIFY));
                    pncChild.setRefLocation(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BABY_REF_LOCATION));
                    pncChild.setBfFirstHour(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.BF_FIRST_HOUR));
                    pncChild.setChildHivStatus(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.CHILD_HIV_STATUS));
                    pncChild.setNvpAdministration(jsonChildObject.optString(PncConstants.JsonFormKeyConstants.NVP_ADMINISTRATION));
                    pncChild.setBaseEntityId(jsonChildObject.optString(PncDbConstants.Column.PncBaby.BASE_ENTITY_ID));
                    pncChild.setEventDate(PncUtils.convertDate(event.getEventDate().toDate(), PncDbConstants.DATE_FORMAT));
                    if (StringUtils.isNotBlank(pncChild.getBaseEntityId()) && StringUtils.isNotBlank(pncChild.getMotherBaseEntityId()))
                        PncLibrary.getInstance().getPncChildRepository().saveOrUpdate(pncChild);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    //TODO make it available from PNC
    private void processStillBorn(@android.support.annotation.Nullable String strStillBorn, @NonNull Event event) {
        if (StringUtils.isNotBlank(strStillBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strStillBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonTestObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    PncStillBorn pncStillBorn = new PncStillBorn();
                    pncStillBorn.setMotherBaseEntityId(event.getBaseEntityId());
                    pncStillBorn.setStillBirthCondition(jsonTestObject.optString(PncConstants.JsonFormKeyConstants.STILL_BIRTH_CONDITION));
                    pncStillBorn.setEventDate(PncUtils.convertDate(event.getEventDate().toDate(), PncDbConstants.DATE_FORMAT));
                    PncLibrary.getInstance().getPncStillBornRepository().saveOrUpdate(pncStillBorn);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }


    private void processReport(@NonNull Event event) {
        try {
            JSONObject jsonObject = new JSONObject(event.getDetails().get("reportJson"));
            String reportMonth = jsonObject.optString("reportDate");
            String reportGrouping = jsonObject.optString("grouping");
            String providerId = jsonObject.optString("providerId");
            String dateCreated = jsonObject.optString("dateCreated");
            DateTime dateSent = new DateTime(dateCreated);
            Date dReportMonth = MonthlyTalliesRepository.DF_YYYYMM.parse(reportMonth);

            JSONArray jsonArray = jsonObject.optJSONArray("hia2Indicators");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                String indicator = jsonObject1.optString("indicatorCode");
                String value = jsonObject1.optString("value");
                MonthlyTally monthlyTally = new MonthlyTally();
                monthlyTally.setEdited(false);
                monthlyTally.setGrouping(reportGrouping);
                monthlyTally.setIndicator(indicator);
                monthlyTally.setProviderId(providerId);
                monthlyTally.setValue(value);
                monthlyTally.setDateSent(dateSent.toDate());
                monthlyTally.setCreatedAt(dateSent.toDate());
                monthlyTally.setMonth(dReportMonth);
                GizMalawiApplication.getInstance().monthlyTalliesRepository().save(monthlyTally);
            }

        } catch (JSONException e) {
            Timber.e(e);
        } catch (ParseException e) {
            Timber.e(e);
        }
    }

    private void updateClientAlerts(@NonNull HashMap<String, DateTime> clientsForAlertUpdates) {
        HashMap<String, DateTime> stringDateTimeHashMap = SerializationUtils.clone(clientsForAlertUpdates);
        for (String baseEntityId : stringDateTimeHashMap.keySet()) {
            DateTime birthDateTime = clientsForAlertUpdates.get(baseEntityId);
            if (birthDateTime != null) {
                VaccineSchedule.updateOfflineAlerts(baseEntityId, birthDateTime, "child");
                ServiceSchedule.updateOfflineAlerts(baseEntityId, birthDateTime);
            }
        }
        clientsForAlertUpdates.clear();
    }

    private boolean processDeathEvent(@NonNull EventClient eventClient, ClientClassification clientClassification) {
        if (eventClient.getEvent().getEntityType().equals(GizConstants.EntityType.CHILD)) {
            try {
                processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }
            return GizUtils.updateChildDeath(eventClient);
        }

        return false;
    }

    private void processUnsyncEvents(@NonNull List<Event> unsyncEvents) {
        if (!unsyncEvents.isEmpty()) {
            unSync(unsyncEvents);
        }

        for (MiniClientProcessorForJava miniClientProcessorForJava : unsyncEventsPerProcessor.keySet()) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            miniClientProcessorForJava.unSync(processorUnsyncEvents);
        }
    }

    private void processEventClient(@NonNull ClientClassification clientClassification, @NonNull EventClient eventClient, @NonNull Event event) {
        Client client = eventClient.getClient();
        if (client != null) {
            try {
                processEvent(event, client, clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void processEventUsingMiniprocessor(ClientClassification clientClassification, EventClient eventClient, String eventType) throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = processorMap.get(eventType);
        if (miniClientProcessorForJava != null) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            if (processorUnsyncEvents == null) {
                processorUnsyncEvents = new ArrayList<Event>();
                unsyncEventsPerProcessor.put(miniClientProcessorForJava, processorUnsyncEvents);
            }

            completeProcessing(eventClient.getEvent());
            miniClientProcessorForJava.processEventClient(eventClient, processorUnsyncEvents, clientClassification);
        }
    }

    private void processWeightEvent(Table weightTable, Table heightTable, EventClient eventClient, String eventType) throws Exception {
        if (weightTable == null) {
            return;
        }

        if (heightTable == null) {
            return;
        }

        processWeight(eventClient, weightTable,
                eventType.equals(WeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
        processHeight(eventClient, heightTable,
                eventType.equals(HeightIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
    }

    private void processVaccinationEvent(Table vaccineTable, EventClient eventClient, Event event, String eventType) throws Exception {
        if (vaccineTable == null) {
            return;
        }

        Client client = eventClient.getClient();
        if (!childExists(client.getBaseEntityId())) {
            List<String> createCase = new ArrayList<>();
            createCase.add(Utils.metadata().childRegister.tableName);
            processCaseModel(event, client, createCase);
        }

        processVaccine(eventClient, vaccineTable,
                eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));

        scheduleUpdatingClientAlerts(client.getBaseEntityId(), client.getBirthdate());
    }

    private boolean childExists(String entityId) {
        return GizMalawiApplication.getInstance().eventClientRepository().checkIfExists(EventClientRepository.Table.client, entityId);
    }

    private Boolean processVaccine(@Nullable EventClient vaccine, @Nullable Table vaccineTable, boolean outOfCatchment) throws Exception {
        try {
            if (vaccine == null || vaccine.getEvent() == null) {
                return false;
            }

            if (vaccineTable == null) {
                return false;
            }

            Timber.d("Starting processVaccine table: %s", vaccineTable.name);

            ContentValues contentValues = processCaseModel(vaccine, vaccineTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(contentValues.getAsString(VaccineRepository.DATE));

                VaccineRepository vaccineRepository = GizMalawiApplication.getInstance().vaccineRepository();
                Vaccine vaccineObj = new Vaccine();
                vaccineObj.setBaseEntityId(contentValues.getAsString(VaccineRepository.BASE_ENTITY_ID));
                vaccineObj.setName(contentValues.getAsString(VaccineRepository.NAME));
                if (contentValues.containsKey(VaccineRepository.CALCULATION)) {
                    vaccineObj.setCalculation(parseInt(contentValues.getAsString(VaccineRepository.CALCULATION)));
                }
                vaccineObj.setDate(date);
                vaccineObj.setAnmId(contentValues.getAsString(VaccineRepository.ANMID));
                vaccineObj.setLocationId(contentValues.getAsString(VaccineRepository.LOCATION_ID));
                vaccineObj.setSyncStatus(VaccineRepository.TYPE_Synced);
                vaccineObj.setFormSubmissionId(vaccine.getEvent().getFormSubmissionId());
                vaccineObj.setEventId(vaccine.getEvent().getEventId());
                vaccineObj.setOutOfCatchment(outOfCatchment ? 1 : 0);

                String createdAtString = contentValues.getAsString(VaccineRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                vaccineObj.setCreatedAt(createdAt);

                Utils.addVaccine(vaccineRepository, vaccineObj);

                Timber.d("Ending processVaccine table: %s", vaccineTable.name);
            }
            return true;

        } catch (Exception e) {
            Timber.e(e, "Process Vaccine Error");
            return null;
        }
    }

    private Boolean processWeight(EventClient weight, Table weightTable, boolean outOfCatchment) throws Exception {

        try {

            if (weight == null || weight.getEvent() == null) {
                return false;
            }

            if (weightTable == null) {
                return false;
            }

            Timber.d("Starting processWeight table: %s", weightTable.name);

            ContentValues contentValues = processCaseModel(weight, weightTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                String eventDateStr = contentValues.getAsString(WeightRepository.DATE);
                Date date = getDate(eventDateStr);

                WeightRepository weightRepository = GizMalawiApplication.getInstance().weightRepository();
                Weight weightObj = new Weight();
                weightObj.setBaseEntityId(contentValues.getAsString(WeightRepository.BASE_ENTITY_ID));
                if (contentValues.containsKey(WeightRepository.KG)) {
                    weightObj.setKg(parseFloat(contentValues.getAsString(WeightRepository.KG)));
                }
                weightObj.setDate(date);
                weightObj.setAnmId(contentValues.getAsString(WeightRepository.ANMID));
                weightObj.setLocationId(contentValues.getAsString(WeightRepository.LOCATIONID));
                weightObj.setSyncStatus(WeightRepository.TYPE_Synced);
                weightObj.setFormSubmissionId(weight.getEvent().getFormSubmissionId());
                weightObj.setEventId(weight.getEvent().getEventId());
                weightObj.setOutOfCatchment(outOfCatchment ? 1 : 0);

                if (contentValues.containsKey(WeightRepository.Z_SCORE)) {
                    String zscoreString = contentValues.getAsString(WeightRepository.Z_SCORE);
                    if (NumberUtils.isNumber(zscoreString)) {
                        weightObj.setZScore(Double.valueOf(zscoreString));
                    }
                }

                String createdAtString = contentValues.getAsString(WeightRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                weightObj.setCreatedAt(createdAt);

                weightRepository.add(weightObj);

                Timber.d("Ending processWeight table: %s", weightTable.name);
            }
            return true;

        } catch (Exception e) {
            Timber.e(e, "Process Weight Error");
            return null;
        }
    }

    private Boolean processHeight(@Nullable EventClient height, @Nullable Table heightTable, boolean outOfCatchment) {

        try {

            if (height == null || height.getEvent() == null) {
                return false;
            }

            if (heightTable == null) {
                return false;
            }

            Timber.d("Starting processWeight table: %s", heightTable.name);

            ContentValues contentValues = processCaseModel(height, heightTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                String eventDateStr = contentValues.getAsString(HeightRepository.DATE);
                Date date = getDate(eventDateStr);

                HeightRepository heightRepository = GizMalawiApplication.getInstance().heightRepository();
                Height heightObject = new Height();
                heightObject.setBaseEntityId(contentValues.getAsString(WeightRepository.BASE_ENTITY_ID));
                if (contentValues.containsKey(HeightRepository.CM)) {
                    heightObject.setCm(parseFloat(contentValues.getAsString(HeightRepository.CM)));
                }
                heightObject.setDate(date);
                heightObject.setAnmId(contentValues.getAsString(HeightRepository.ANMID));
                heightObject.setLocationId(contentValues.getAsString(HeightRepository.LOCATIONID));
                heightObject.setSyncStatus(HeightRepository.TYPE_Synced);
                heightObject.setFormSubmissionId(height.getEvent().getFormSubmissionId());
                heightObject.setEventId(height.getEvent().getEventId());
                heightObject.setOutOfCatchment(outOfCatchment ? 1 : 0);

                if (contentValues.containsKey(HeightRepository.Z_SCORE)) {
                    String zScoreString = contentValues.getAsString(HeightRepository.Z_SCORE);
                    if (NumberUtils.isNumber(zScoreString)) {
                        heightObject.setZScore(Double.valueOf(zScoreString));
                    }
                }

                String createdAtString = contentValues.getAsString(HeightRepository.CREATED_AT);
                Date createdAt = getDate(createdAtString);
                heightObject.setCreatedAt(createdAt);

                heightRepository.add(heightObject);

                Timber.d("Ending processWeight table: %s", heightTable.name);
            }
            return true;

        } catch (Exception e) {
            Timber.e(e, "Process Height Error");
            return null;
        }
    }

    private Boolean processService(EventClient service, Table serviceTable) {
        try {
            if (service == null || service.getEvent() == null) {
                return false;
            }

            if (serviceTable == null) {
                return false;
            }

            Timber.d("Starting processService table: %s", serviceTable.name);

            ContentValues contentValues = processCaseModel(service, serviceTable);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                String name = getServiceTypeName(contentValues);

                String eventDateStr = contentValues.getAsString(RecurringServiceRecordRepository.DATE);
                Date date = getDate(eventDateStr);

                String value = null;

                if (StringUtils.containsIgnoreCase(name, "ITN")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String itnDateString = contentValues.getAsString("itn_date");
                    if (StringUtils.isNotBlank(itnDateString)) {
                        date = simpleDateFormat.parse(itnDateString);
                    }

                    value = getServiceValue(contentValues);

                }

                List<ServiceType> serviceTypeList = getServiceTypes(name);
                if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                    return false;
                }

                if (date == null) {
                    return false;
                }

                recordServiceRecord(service, contentValues, name, date, value, serviceTypeList);

                Timber.d("Ending processService table: %s", serviceTable.name);
            }
            return true;

        } catch (Exception e) {
            Timber.e(e, "Process Service Error");
            return null;
        }
    }

    @NotNull
    private String getServiceValue(ContentValues contentValues) {
        String value;
        value = RecurringIntentService.ITN_PROVIDED;
        if (contentValues.getAsString("itn_has_net") != null) {
            value = RecurringIntentService.CHILD_HAS_NET;
        }
        return value;
    }

    @Nullable
    private String getServiceTypeName(ContentValues contentValues) {
        String name = contentValues.getAsString(RecurringServiceTypeRepository.NAME);
        if (StringUtils.isNotBlank(name)) {
            name = name.replaceAll("_", " ").replace("dose", "").trim();
        }
        return name;
    }

    private void recordServiceRecord(EventClient service, ContentValues contentValues, String name, Date date, String value, List<ServiceType> serviceTypeList) {
        RecurringServiceRecordRepository recurringServiceRecordRepository = GizMalawiApplication.getInstance()
                .recurringServiceRecordRepository();
        ServiceRecord serviceObj = getServiceRecord(service, contentValues, name, date, value, serviceTypeList);
        String createdAtString = contentValues.getAsString(RecurringServiceRecordRepository.CREATED_AT);
        Date createdAt = getDate(createdAtString);
        serviceObj.setCreatedAt(createdAt);

        recurringServiceRecordRepository.add(serviceObj);
    }

    private List<ServiceType> getServiceTypes(String name) {
        RecurringServiceTypeRepository recurringServiceTypeRepository = GizMalawiApplication.getInstance()
                .recurringServiceTypeRepository();
        return recurringServiceTypeRepository.searchByName(name);
    }

    private void processBCGScarEvent(EventClient bcgScarEventClient) {
        if (bcgScarEventClient == null || bcgScarEventClient.getEvent() == null) {
            return;
        }

        Event event = bcgScarEventClient.getEvent();
        String baseEntityId = event.getBaseEntityId();
        DateTime eventDate = event.getEventDate();
        long date = 0;
        if (eventDate != null) {
            date = eventDate.getMillis();
        }

        ChildDbUtils.updateChildDetailsValue(Constants.SHOW_BCG_SCAR, String.valueOf(date), baseEntityId);
    }

    private boolean unSync(List<Event> events) {
        try {

            if (events == null || events.isEmpty()) {
                return false;
            }

            ClientField clientField = assetJsonToJava("ec_client_fields.json", ClientField.class);
            return clientField != null;

        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    @VisibleForTesting
    ContentValues processCaseModel(EventClient eventClient, Table table) {
        try {
            List<Column> columns = table.columns;
            ContentValues contentValues = new ContentValues();

            for (Column column : columns) {
                processCaseModel(eventClient.getEvent(), eventClient.getClient(), column, contentValues);
            }

            return contentValues;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private Integer parseInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            Timber.e(e, e.toString());
        }
        return null;
    }

    @Nullable
    private Date getDate(@Nullable String eventDateStr) {
        Date date = null;
        if (StringUtils.isNotBlank(eventDateStr)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
                date = dateFormat.parse(eventDateStr);
            } catch (ParseException e) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    date = dateFormat.parse(eventDateStr);
                } catch (ParseException pe) {
                    try {
                        date = DateUtil.parseDate(eventDateStr);
                    } catch (ParseException pee) {
                        Timber.e(e);
                    }
                }
            }
        }
        return date;
    }

    private Float parseFloat(String string) {
        try {
            return Float.valueOf(string);
        } catch (NumberFormatException e) {
            Timber.e(e);
        }
        return null;
    }

    @NotNull
    private ServiceRecord getServiceRecord(EventClient service, ContentValues contentValues, String name, Date date,
                                           String value, List<ServiceType> serviceTypeList) {
        ServiceRecord serviceObj = new ServiceRecord();
        serviceObj.setBaseEntityId(contentValues.getAsString(RecurringServiceRecordRepository.BASE_ENTITY_ID));
        serviceObj.setName(name);
        serviceObj.setDate(date);
        serviceObj.setAnmId(contentValues.getAsString(RecurringServiceRecordRepository.ANMID));
        serviceObj.setLocationId(contentValues.getAsString(RecurringServiceRecordRepository.LOCATION_ID));
        serviceObj.setSyncStatus(RecurringServiceRecordRepository.TYPE_Synced);
        serviceObj.setFormSubmissionId(service.getEvent().getFormSubmissionId());
        serviceObj.setEventId(service.getEvent().getEventId()); //FIXME hard coded id
        serviceObj.setValue(value);
        serviceObj.setRecurringServiceId(serviceTypeList.get(0).getId());
        return serviceObj;
    }

    @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {

        Timber.d("Starting updateFTSsearch table: %s", tableName);

        AllCommonsRepository allCommonsRepository = GizMalawiApplication.getInstance().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
        }

        if (contentValues != null && GizConstants.TABLE_NAME.ALL_CLIENTS.equals(tableName) &&
                GizMalawiApplication.getInstance().registerTypeRepository().findByRegisterType(entityId, GizConstants.RegisterType.CHILD)) {
            String dobString = contentValues.getAsString(Constants.KEY.DOB);
            if (StringUtils.isNotBlank(dobString)) {
                DateTime birthDateTime = Utils.dobStringToDateTime(dobString);
                if (birthDateTime != null) {
                    VaccineSchedule.updateOfflineAlerts(entityId, birthDateTime, GizConstants.RegisterType.CHILD);
                    ServiceSchedule.updateOfflineAlerts(entityId, birthDateTime);
                }
            }
        }
        Timber.d("Finished updateFTSsearch table: %s", tableName);
    }

    @Override
    public String[] getOpenmrsGenIds() {
        return new String[]{"zeir_id"};
    }

    private void scheduleUpdatingClientAlerts(@NonNull String baseEntityId, @NonNull DateTime dateTime) {
        if (!clientsForAlertUpdates.containsKey(baseEntityId)) {
            clientsForAlertUpdates.put(baseEntityId, dateTime);
        }
    }
}