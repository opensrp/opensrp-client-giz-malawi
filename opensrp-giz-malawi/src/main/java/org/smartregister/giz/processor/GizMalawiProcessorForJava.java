package org.smartregister.giz.processor;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.anc.library.sync.MiniClientProcessorForJava;
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
import org.smartregister.giz.activity.ChildImmunizationActivity;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
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
import org.smartregister.opd.processor.OpdMiniClientProcessorForJava;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class GizMalawiProcessorForJava extends ClientProcessorForJava {

    private static GizMalawiProcessorForJava instance;

    private HashMap<String, MiniClientProcessorForJava> processorMap = new HashMap<>();
    private HashMap<MiniClientProcessorForJava, List<Event>> unsyncEventsPerProcessor = new HashMap<>();

    private GizMalawiProcessorForJava(Context context) {
        super(context);

        BaseAncClientProcessorForJava baseAncClientProcessorForJava = new BaseAncClientProcessorForJava(context);
        OpdMiniClientProcessorForJava opdMiniClientProcessorForJava = new OpdMiniClientProcessorForJava(context);

        addMiniProcessors(baseAncClientProcessorForJava, opdMiniClientProcessorForJava);
    }

    private void addMiniProcessors(MiniClientProcessorForJava... miniClientProcessorsForJava) {
        for (MiniClientProcessorForJava miniClientProcessorForJava : miniClientProcessorsForJava) {
            unsyncEventsPerProcessor.put(miniClientProcessorForJava, new ArrayList<Event>());

            HashSet<String> eventTypes = miniClientProcessorForJava.getEventTypes();

            for (String eventType : eventTypes) {
                processorMap.put(eventType, miniClientProcessorForJava);
            }
        }
    }


    public static GizMalawiProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new GizMalawiProcessorForJava(context);
        }
        return instance;
    }

    @Override
    public synchronized void processClient(List<EventClient> eventClients) throws Exception {

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
                    processDeathEvent(eventClient);
                    unsyncEvents.add(event);
                } else if (eventType.equals(Constants.EventType.BITRH_REGISTRATION) || eventType
                        .equals(Constants.EventType.UPDATE_BITRH_REGISTRATION) || eventType
                        .equals(Constants.EventType.NEW_WOMAN_REGISTRATION) || eventType.equals(OpdConstants.EventType.OPD_REGISTRATION)) {
                    if (eventType.equals(OpdConstants.EventType.OPD_REGISTRATION) && eventClient.getClient() == null) {
                        Timber.e(new Exception(), "Cannot find client corresponding to %s with base-entity-id %s", OpdConstants.EventType.OPD_REGISTRATION, event.getBaseEntityId());
                        continue;
                    }

                    if (clientClassification == null) {
                        continue;
                    }

                    processBirthAndWomanRegistrationEvent(clientClassification, eventClient, event);
                } else if (processorMap.containsKey(eventType)) {
                    try {
                        processEventUsingMiniprocessor(clientClassification, eventClient, eventType);
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                }
            }

            // Unsync events that are should not be in this device
            processUnsyncEvents(unsyncEvents);
        }
    }

    private void processDeathEvent(@NonNull EventClient eventClient) {
        if (eventClient.getEvent().getEntityType().equals(GizConstants.EntityType.CHILD)) {
            GizUtils.updateChildDeath(eventClient);
        }
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

    private void processBirthAndWomanRegistrationEvent(@NonNull ClientClassification clientClassification, @NonNull EventClient eventClient, @NonNull Event event) {
        Client client = eventClient.getClient();
        //iterate through the events
        if (client != null) {
            try {
                processEvent(event, client, clientClassification);
                processBirthWeight(event, client);
                processBirthHeight(event, client);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void processBirthWeight(@NonNull Event event, @NonNull Client client) {
        // Birth Registration events from the device are processed correctly, events are not processed well
        if (event.getServerVersion() != 0 && !event.getEventType().equals(Constants.EventType.NEW_WOMAN_REGISTRATION)) {
            try {

                String weight = getEventWeight(event);

                if (!TextUtils.isEmpty(weight)) {

                    Weight dbWeight = GizMalawiApplication.getInstance().weightRepository().findUniqueByDate(GizMalawiApplication.getInstance().weightRepository().getWritableDatabase(), event.getBaseEntityId(), client.getBirthdate().toDate());

                    WeightWrapper weightWrapper = new WeightWrapper();
                    weightWrapper.setGender(client.getGender());
                    weightWrapper.setWeight(!TextUtils.isEmpty(weight) ? parseFloat(weight) : null);
                    LocalDate localDate = new LocalDate(client.getBirthdate());
                    weightWrapper.setUpdatedWeightDate(localDate.toDateTime(LocalTime.MIDNIGHT), (new LocalDate()).isEqual(localDate));//This is the weight of birth so reference date should be the DOB
                    weightWrapper.setId(client.getBaseEntityId());
                    weightWrapper.setDob(getChildBirthDate(client.getBirthdate().toString()));
                    if (dbWeight != null && dbWeight.getId() != null && dbWeight.getId() > 0) {
                        weightWrapper.setDbKey(dbWeight.getId());
                    }

                    Utils.recordWeight(GizMalawiApplication.getInstance().weightRepository(), weightWrapper, BaseRepository.TYPE_Synced);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void processBirthHeight(@NonNull Event event, @NonNull Client client) {
        // Birth Registration events from the device are processed correctly, events are not processed well
        if (event.getServerVersion() != 0 && !event.getEventType().equals(Constants.EventType.NEW_WOMAN_REGISTRATION)) {
            try {

                String height = getEventHeight(event);

                if (!TextUtils.isEmpty(height)) {
                    Height heightToCheck = new Height();
                    heightToCheck.setEventId(event.getEventId());
                    heightToCheck.setFormSubmissionId(event.getFormSubmissionId());

                    // Find unique by form_submission_id or event_id
                    Height dbHeight = GizMalawiApplication.getInstance()
                            .heightRepository()
                            .findUnique(GizMalawiApplication.getInstance().weightRepository().getWritableDatabase(), heightToCheck);

                    HeightWrapper heightWrapper = new HeightWrapper();
                    heightWrapper.setGender(client.getGender());
                    heightWrapper.setHeight(!TextUtils.isEmpty(height) ? parseFloat(height) : null);
                    LocalDate localDate = new LocalDate(client.getBirthdate());
                    heightWrapper.setUpdatedHeightDate(localDate.toDateTime(LocalTime.MIDNIGHT), (new LocalDate()).isEqual(localDate));//This is the height of birth so reference date should be the DOB
                    heightWrapper.setId(client.getBaseEntityId());
                    heightWrapper.setDob(getChildBirthDate(client.getBirthdate().toString()));
                    if (dbHeight != null && dbHeight.getId() != null && dbHeight.getId() > 0) {
                        heightWrapper.setDbKey(dbHeight.getId());
                    }

                    Utils.recordHeight(GizMalawiApplication.getInstance().heightRepository(), heightWrapper, BaseRepository.TYPE_Synced);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private String getChildBirthDate(String childBirthDate) {
        return childBirthDate.contains("T") ? childBirthDate.substring(0, childBirthDate.indexOf(84)) : childBirthDate;
    }

    private String getEventWeight(@NonNull Event event) {
        Obs obs = findObs("", true, event, "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return obs != null ? obs.getValue().toString() : null;
    }

    private String getEventHeight(@NonNull Event event) {
        Obs obs = findObs("", true, event, "159429AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return obs != null ? obs.getValue().toString() : null;
    }

    //Duplicates and enhances method in Event.findObs
    public Obs findObs(String parentId, boolean nonEmpty, Event event, String... fieldIds) {
        Obs res = null;
        for (String f : fieldIds) {
            for (Obs o : event.getObs()) {
                // if parent is specified and not matches leave and move forward
                if (StringUtils.isNotBlank(parentId) && !o.getParentCode().equalsIgnoreCase(parentId)) {
                    continue;
                }

                if (f.equalsIgnoreCase(o.getFieldCode()) || f.equalsIgnoreCase(o.getFormSubmissionField())) {
                    // obs is found and first  one.. should throw exception if multiple obs found with same names/ids
                    if (nonEmpty && o.getValues().isEmpty()) {
                        continue;
                    }
                    if (res == null) {
                        res = o;
                    } else
                        throw new RuntimeException("Multiple obs found with name or ids specified ");
                }
            }
        }
        return res;
    }


    private void processEventUsingMiniprocessor(ClientClassification clientClassification, EventClient eventClient, String eventType) throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = processorMap.get(eventType);
        if (miniClientProcessorForJava != null) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            if (processorUnsyncEvents == null) {
                processorUnsyncEvents = new ArrayList<Event>();
                unsyncEventsPerProcessor.put(miniClientProcessorForJava, processorUnsyncEvents);
            }

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

        if (!childExists(eventClient.getClient().getBaseEntityId())) {
            List<String> createCase = new ArrayList<>();
            createCase.add("ec_child");
            processCaseModel(event, eventClient.getClient(), createCase);
        }

        processVaccine(eventClient, vaccineTable,
                eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
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

        DetailsRepository detailsRepository = GizMalawiApplication.getInstance().context().detailsRepository();
        detailsRepository.add(baseEntityId, ChildImmunizationActivity.SHOW_BCG_SCAR, String.valueOf(date), date);
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
//        if (GizConstants.TABLE_NAME.MOTHER_TABLE_NAME.equals(tableName)) {
//            return;
//        }

        Timber.d("Starting updateFTSsearch table: %s", tableName);

        AllCommonsRepository allCommonsRepository = GizMalawiApplication.getInstance().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
        }

        if (contentValues != null && StringUtils.containsIgnoreCase(tableName, "child")) {
            String dobString = contentValues.getAsString(Constants.KEY.DOB);
            DateTime birthDateTime = Utils.dobStringToDateTime(dobString);
            if (birthDateTime != null) {
                VaccineSchedule.updateOfflineAlerts(entityId, birthDateTime, "child");
                ServiceSchedule.updateOfflineAlerts(entityId, birthDateTime);
            }
        }

        Timber.d("Finished updateFTSsearch table: %s", tableName);
    }

    @Override
    public String[] getOpenmrsGenIds() {
        return new String[]{"zeir_id"};
    }
}