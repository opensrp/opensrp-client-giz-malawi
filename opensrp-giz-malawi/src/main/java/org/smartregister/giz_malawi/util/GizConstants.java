package org.smartregister.giz_malawi.util;

public class GizConstants {
    public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";
    public static final String ARABIC_LOCALE = "ar";
    public static final String HOME_FACILITY = "Home_Facility";
    public static final String mer_id = "mer_id";
    public static final String CHILD_REGISTER_CARD_NUMBER = "Child_Register_Card_Number";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";
    public static final String BIRTH_WEIGHT = "Birth_Weight";
    public static final String BIRTH_TETANUS_PROTECTION = "Birth_Tetanus_Protection";
    public static final String MOTHER_FIRST_NAME = "mother_first_name";
    public static final String MOTHER_LAST_NAME = "mother_last_name";
    public static final String MOTHER_GUARDIAN_PHONE_NUMBER = "Mother_Guardian_Phone_Number";
    public static final String MOTHER_GUARDIAN_NUMBER = "Mother_Guardian_Number";
    public static final String FATHER_GUARDIAN_FIRST_NAME = "Father_Guardian_First_Name";
    public static final String FATHER_GUARDIAN_LAST_NAME = "Father_Guardian_Last_Name";
    public static final String FATHER_GUARDIAN_PHONE_NUMBER = "Father_Guardian_Phone_Number";
    public static final String FATHER_GUARDIAN_NUMBER = "Father_Guardian_Number";
    public static final String FATHER_GUARDIAN_DATE_BIRTH = "Father_Guardian_Date_Birth";
    public static final String IS_PLACE_BIRTH = "isPlace_Birth";
    public static final String OTHER = "other";
    public static final String BIRTH_FACILITY_NAME_OTHER = "Birth_Facility_Name_Other";
    public static final String ADDRESS_3 = "address3";
    public static final String ADDRESS_5 = "address5";
    public static final String ADDRESS_2 = "address2";
    public static final String ADDRESS_1 = "address1";
    public static final String PREFERRED_LANGUAGE = "Preferred_Language";
    public static final String FIRST_HEALTH_FACILITY_CONTACT = "First_Health_Facility_Contact";
    public static final String MOTHER_DOB = "mother_dob";
    public static final String PLACE_BIRTH = "Place_Birth";
    public static final int ON_KEY_BACK_PRESSED = 610;
    public static final String ITEM_LIST = "ITEM_LIST";
    public static final String TAPCARD_INFO = "TAPCARD_INFO";
    public static final String VOUCHER_DETAILS = "VOUCHER_DETAILS";
    public static final String FRAGMENT = "FRAGMENT";
    public static final String NEWBORN = "NEWBORN";
    public static final String PREV_PAPER = "PREV_PAPER";
    public static final int REQUEST_CODE_LAUNCH_XIP = 100;
    public static String FINGERPRINT_MESSAGE = "FINGERPRINT_MESSAGE";
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    public static final String TAPCARD_OPTION = "TAPCARD_OPTION";
    public static final String PREV_CARD = "PREV_CARD";
    public static final String NEW_CARD = "NEW_CARD";
    public static final String writeMessage = "to write information";
    public static final String readMessage = "to read information";

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String CHILD_REGISTER = "child_register";

    }

    public static final class EventType {
        public static final String CHILD_REGISTRATION = "Birth Registration";

        public static final String UPDATE_CHILD_REGISTRATION = "Update Birth Registration";

    }

    public static class JSON_FORM {

        public static String CHILD_ENROLLMENT = "child_enrollment";
        public static String OUT_OF_CATCHMENT_SERVICE = "out_of_catchment_service";

    }

    public static class RELATIONSHIP {
        public static final String MOTHER = "mother";

    }

    public static class TABLE_NAME {
        public static final String CHILD = "ec_child";
        public static final String MOTHER_TABLE_NAME = "ec_mother";
    }

    public static final class VACCINE {

        public static final String CHILD = "child";
    }


    public static final class EntityType {

        public static final String CHILD = "child";
    }

    public static final class EC_CHILD_TABLE {

        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";
        public static final String DOD = "dod";
        public static final String REG_DATE = "client_reg_date";
        public static final String INACTIVE = "inactive";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";


    }

    public static final class GENDER {

        public static final String MALE = "Male";
        public static final String FEMALE = "Female";
        public static final String TRANSGENDER = "Transgender";
    }

    public static final class GENDER_KEY {

        public static final String MALE = "1";
        public static final String FEMALE = "2";
        public static final String TRANSGENDER = "3";
    }

    public static final class ANSWER {

        public static final String YES = "Yes";
        public static final String NO = "No";
    }

    public static final class ANSWER_KEY {

        public static final String YES = "1";
        public static final String NO = "2";
    }

    public static final class KEY {
        public static final String CHILD = "child";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BIRTHDATE = "birthdate";
        public static final String DEATHDATE = "deathdate";
        public static final String DEATHDATE_ESTIMATED = "deathdate_estimated";
        public static final String BIRTHDATE_ESTIMATED = "birthdate_estimated";
        public static final String EPI_CARD_NUMBER = "epi_card_number";
        public static final String MOTHER_LAST_NAME = "mother_last_name";
        public static final String mer_id = "mer_id";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String GENDER = "gender";
        public static final String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
        public static final String INACTIVE = "inactive";
        public static final String DATE = "date";
        public static final String VACCINE = "vaccine";
        public static final String ALERT = "alert";
        public static final String WEEK = "week";
        public static final String MONTH = "month";
        public static final String DAY = "day";
        public static final String PMTCT_STATUS = "pmtct_status";
        public static final String LOCATION_NAME = "location_name";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String BIRTH_WEIGHT = "Birth_Weight";
        public static final String RELATIONAL_ID = "relational_id";
        public static final String MOTHER = "mother";
        public static final String ENTITY_ID = "entity_id";
        public static final String VALUE = "value";
        public static final String STEPNAME = "stepName";
        public static final String TITLE = "title";
        public static final String ERR = "err";
        public static final String HIA_2_INDICATOR = "hia2_indicator";
        public static final String LOOK_UP = "look_up";
        public static final String NUMBER_PICKER = "number_picker";
    }

    public static final class CONCEPT {
        public final static String VACCINE_DATE = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    }

    public static final class JSONFORM {
        public final static String CHILD_ENROLLMENT = "child_enrollment";
        public final static String OUT_OF_CATCHMENT = "out_of_catchment_service";
    }
}
