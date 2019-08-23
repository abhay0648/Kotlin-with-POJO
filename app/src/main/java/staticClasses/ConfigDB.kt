package staticClasses

//object to hold constants for configuration of database and keys
object ConfigDB {

    const val DB_NAME = "mock_api_repo_db"
    const val DB_VERSION = 1
    const val TYPE_SET_DATA = "Type Set Data"
    const val TYPE_GET_DATA = "Type Get Data"
    const val TYPE_DELETE_DATA = "Type Delete Data"
    const val TYPE_UPDATE_DATA = "Type Update Data"
    const val TYPE_GET_ALL_DATA = "Type Get All Data"
    const val TYPE_EMPTY_ALL_DATA = "Type Empty All Data"

    const val TYPE_EMPTY_DATA = "TypeEmpty Data"
    const val jobData = "mock_api_repo_table"

    //Music Player Data
    const val KEY_ID = "KEY_ID"


    val CREATING_DB_MOCK_TABLE = ("create table " + jobData + "( " + KEY_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY," +
            DataBaseKeys.ID + " TEXT, " + DataBaseKeys.DESCRIPTION + " TEXT, "
            + DataBaseKeys.IMAGE_URL + " TEXT, " + DataBaseKeys.LAT + " TEXT, " + DataBaseKeys.LNG + " TEXT, "
            + DataBaseKeys.ADDRESS + " TEXT )")

}