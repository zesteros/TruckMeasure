package com.mx.vise.cubicaciones.dao;

public final class Contract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Contract() {
    }

    public static class SCRIPT {

        public static final String TABLE_NAME_BRANDS = "brands";

        public static final String COLUMN_ID_BRAND = "id_brand";
        public static final String COLUMN_NAME_BRAND = "brand";


        public static final String TABLE_NAME_SYNDICATES = "syndicates";

        public static final String COLUMN_ID_SYNDICATE = "id_syndicate";
        public static final String COLUMN_NAME_SYNDICATE = "syndicate";


        public static final String COLUMN_ADD_DATE = "add_date";
        public static final String SQL_CREATE_ENTRIES_BRANDS =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_BRANDS + " (" +

                        SCRIPT.COLUMN_ID_BRAND + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_NAME_BRAND + " TEXT," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME );";

        public static final String SQL_CREATE_ENTRIES_SYNDICATES = "CREATE TABLE " + SCRIPT.TABLE_NAME_SYNDICATES + " (" +

                SCRIPT.COLUMN_ID_SYNDICATE + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SCRIPT.COLUMN_NAME_SYNDICATE + " TEXT," +
                SCRIPT.COLUMN_ADD_DATE + " DATETIME );";
        public static final String COLUMN_START_CAPTURE_DATE = "start_date";
        public static final String COLUMN_END_CAPTURE_DATE = "end_date";

        private static final String COLUMN_ID_TAG = "id_tag";

        public static final String SQL_CREATE_ENTRIES_TAGS = "CREATE TABLE " + SCRIPT.TABLE_NAME_TAGS + " (" +

                SCRIPT.COLUMN_ID_TAG + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SCRIPT.COLUMN_ID_ACTIVO_TAG + " INTEGER," +
                SCRIPT.COLUMN_TID_TAG + " TEXT," +
                SCRIPT.COLUMN_ADD_DATE_TAG + " DATETIME, " +
                SCRIPT.COLUMN_SYNC_DATE_TAG + " DATETIME);";

        public static final String SQL_DELETE_ENTRIES_BRANDS =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_BRANDS + ";";

        public static final String SQL_DELETE_ENTRIES_SYNDICATES =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_SYNDICATES + ";";

        public static final String SQL_DELETE_ENTRIES_TAGS =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_TAGS + ";";

        public static final String SQL_DELETE_ENTRIES_CUBAGE =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_CUBAGE + ";";

        public static final String COLUMN_ID_ACTIVO_TAG = "id_activo";
        public static final String COLUMN_TID_TAG = "tid";
        public static final String COLUMN_ADD_DATE_TAG = "add_date";
        public static final String COLUMN_SYNC_DATE_TAG = "sync_date";
        public static final String TABLE_NAME_TAGS = "tags";
        public static final String COLUMN_STATUS_CUBAGE = "estatus";

        public static final String COLUMN_TOTAL_VOLUME = "volume_total";
        public static final String COLUMN_REAR_PLATE_OCR = "rear_ocr";
        public static final String COLUMN_FRONT_PLATE_OCR = "front_ocr";

        public static final String COLUMN_TAG = "tag";
        public static final String SQL_CREATE_ENTRIES_CUBAGE =
                "CREATE TABLE IF NOT EXISTS " + SCRIPT.TABLE_NAME_CUBAGE + " (" +
                        SCRIPT.COLUMN_ID_CUBAGE + " INTEGER PRIMARY KEY AUTOINCREMENT," +//0
                        SCRIPT.COLUMN_SHEET_NUMBER_CUBAGE + " TEXT," +//1
                        SCRIPT.COLUMN_ID_SYNDICATE_CUBAGE + " INTEGER," +//2
                        SCRIPT.COLUMN_NAME_SYNDICATE_CUBAGE + " TEXT," +//3
                        SCRIPT.COLUMN_OPERATOR_NAME_CUBAGE + " TEXT," +//4
                        SCRIPT.COLUMN_ID_BUILDING_CUBAGE + " TEXT," +//5
                        SCRIPT.COLUMN_REAR_PLATE_PATH_CUBAGE + " TEXT," +//6
                        SCRIPT.COLUMN_FRONT_PLATE_PATH_CUBAGE + " TEXT," +//7
                        SCRIPT.COLUMN_LICENSE_CARD_PATH_CUBAGE + " TEXT," +//8
                        SCRIPT.COLUMN_FRONT_PLATE_WRITED_CUBAGE + " TEXT," +//9
                        SCRIPT.COLUMN_REAR_PLATE_WRITED_CUBAGE + " TEXT," +//10
                        SCRIPT.COLUMN_UNIT_TYPE_CUBAGE + " TEXT," +//11
                        SCRIPT.COLUMN_BOX_COLOR_CUBAGE + " TEXT," +//12
                        SCRIPT.COLUMN_TRACTOR_COLOR_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_ID_BRAND_CUBAGE + " INTEGER," +
                        SCRIPT.COLUMN_UNIT_FRONTAL_PHOTO_PATH_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_UNIT_SIDE_PHOTO_PATH_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_UNIT_BACK_PHOTO_PATH_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_CIRCULATION_CARD_WRITED_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_CIRCULATION_CARD_PHOTO_PATH_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_HAS_INCREASE_CUBAGE + " INTEGER," +
                        SCRIPT.COLUMN_L1_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_A1_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_H1_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_L2_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_A2_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_H2_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_LC_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_L_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_A_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_H_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_ADJUSTMENT_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_INCREASE_CUBAGE + " FLOAT," +
                        SCRIPT.COLUMN_OPERATOR_SIGNATURE_PATH_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_USER_SIGNATURE_PATH_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_ADD_USER_CUBAGE + " INTEGER," +
                        SCRIPT.COLUMN_STATUS_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_OBSERVATIONS_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_ADD_DATE_CUBAGE + " DATETIME,"+
                        SCRIPT.COLUMN_BRAND_NAME_CUBAGE + " TEXT," +
                        SCRIPT.COLUMN_TOTAL_VOLUME+" FLOAT,"+
                        SCRIPT.COLUMN_REAR_PLATE_OCR +" TEXT,"+
                        SCRIPT.COLUMN_FRONT_PLATE_OCR +" TEXT," +
                        SCRIPT.COLUMN_TAG +" TEXT," +
                        SCRIPT.COLUMN_START_CAPTURE_DATE + " DATETIME, " +
                        SCRIPT.COLUMN_END_CAPTURE_DATE + " DATETIME);";

        public static final String COLUMN_ID_CUBAGE = "id_cubicacion";
        public static final String COLUMN_ADD_USER_CUBAGE = "add_user";
        public static final String COLUMN_ID_SYNDICATE_CUBAGE = "id_syndicate";
        public static final String COLUMN_NAME_SYNDICATE_CUBAGE = "syndicate_name";
        public static final String COLUMN_OPERATOR_NAME_CUBAGE = "operador";
        public static final String COLUMN_ID_BUILDING_CUBAGE = "id_obra";
        public static final String COLUMN_REAR_PLATE_PATH_CUBAGE = "rear_plate_path";
        public static final String COLUMN_FRONT_PLATE_PATH_CUBAGE = "front_plate_path";
        public static final String COLUMN_LICENSE_CARD_PATH_CUBAGE = "license_card_path";
        public static final String COLUMN_FRONT_PLATE_WRITED_CUBAGE = "front_plate_writed";
        public static final String COLUMN_REAR_PLATE_WRITED_CUBAGE = "rear_plate_writed";
        public static final String COLUMN_ID_BRAND_CUBAGE = "id_brand";
        public static final String COLUMN_UNIT_TYPE_CUBAGE = "unit_type";
        public static final String COLUMN_BOX_COLOR_CUBAGE = "box_color";
        public static final String COLUMN_TRACTOR_COLOR_CUBAGE = "tractor_color";
        public static final String COLUMN_UNIT_FRONTAL_PHOTO_PATH_CUBAGE = "unit_frontal_path";
        public static final String COLUMN_UNIT_SIDE_PHOTO_PATH_CUBAGE = "unit_side_path";
        public static final String COLUMN_UNIT_BACK_PHOTO_PATH_CUBAGE = "back_path";
        public static final String COLUMN_CIRCULATION_CARD_WRITED_CUBAGE = "circulation_card_writed";
        public static final String COLUMN_CIRCULATION_CARD_PHOTO_PATH_CUBAGE = "circulation_card_photo_path";
        public static final String COLUMN_HAS_INCREASE_CUBAGE = "has_increase";
        public static final String COLUMN_L1_CUBAGE = "l1";
        public static final String COLUMN_A1_CUBAGE = "a1";
        public static final String COLUMN_H1_CUBAGE = "h1";
        public static final String COLUMN_L2_CUBAGE = "l2";
        public static final String COLUMN_A2_CUBAGE = "a2";
        public static final String COLUMN_H2_CUBAGE = "h2";
        public static final String COLUMN_LC_CUBAGE = "lc";
        public static final String COLUMN_L_CUBAGE = "l";
        public static final String COLUMN_A_CUBAGE = "a";
        public static final String COLUMN_H_CUBAGE = "h";
        public static final String COLUMN_ADJUSTMENT_CUBAGE = "adjustment";
        public static final String COLUMN_SHEET_NUMBER_CUBAGE = "sheet_number";
        public static final String COLUMN_INCREASE_CUBAGE = "increase";
        public static final String COLUMN_OPERATOR_SIGNATURE_PATH_CUBAGE = "operator_signature_path";
        public static final String COLUMN_USER_SIGNATURE_PATH_CUBAGE = "user_signature_path";
        public static final String COLUMN_OBSERVATIONS_CUBAGE = "observations";
        public static final String COLUMN_ADD_DATE_CUBAGE = "add_date";
        public static final String COLUMN_BRAND_NAME_CUBAGE = "brand_name";
        public static final String TABLE_NAME_CUBAGE = "CUBAGE";


    }
}