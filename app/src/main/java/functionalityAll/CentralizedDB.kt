package functionalityAll

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.ArrayList

import callBacks.DatabaseCallback
import exception_handlling.CatchException
import staticClasses.ConfigDB
import staticClasses.DataBaseKeys
import viewModel.MockList


// class to run database queries and managing them by extending SQLiteOpenHelper
class CentralizedDB(context: Context, version: Int) : SQLiteOpenHelper(context, ConfigDB.DB_NAME, null, version) {
    private var mGetJobData: List<MockList>? = null
    private var tableName: String? = null
    private var mSqLiteDatabase: SQLiteDatabase? = null
    private var mCentralizedDB: CentralizedDB? = null
    private var mDatabaseCallback: DatabaseCallback? = null
    private var mGetJobDataN: ArrayList<MockList>? = null
    private var mGetMockModel: MockList? = null

    // override function lifecycle to create database and table
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ConfigDB.CREATING_DB_MOCK_TABLE)
    }

    // override function lifecycle to upgrade database and table
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + ConfigDB.jobData)
            onCreate(db)
        } catch (e: Exception) {
            Log.e("DB_Version_Exception", "- $e")
            CatchException.ExceptionSend(e)
        }

    }

    //function lifecycle to close database and stop data leak
    fun db_close(db: SQLiteDatabase) {
        db.close()
    }

    // function to insert data in the table
    @Throws(Exception::class)
    fun dataInsertionFunction(vararg args: Any) {
        tableName = args[0] as String
        mCentralizedDB = args[1] as CentralizedDB
        mDatabaseCallback = args[2] as DatabaseCallback
        mSqLiteDatabase = mCentralizedDB!!.writableDatabase

        if (tableName == ConfigDB.jobData) {
            mGetJobData = args[3] as List<MockList>
            for (i in mGetJobData!!.indices) {
                val getJobSub = mGetJobData!![i]
                val cv = ContentValues()

                cv.put(DataBaseKeys.ID, getJobSub.id)
                cv.put(DataBaseKeys.DESCRIPTION, getJobSub.description)
                cv.put(DataBaseKeys.IMAGE_URL, getJobSub.imageUrl)
                cv.put(DataBaseKeys.LAT, getJobSub.location.lat)
                cv.put(DataBaseKeys.LNG, getJobSub.location.lng)
                cv.put(DataBaseKeys.ADDRESS, getJobSub.location.address)
                mSqLiteDatabase!!.insert(tableName, "", cv)
            }
            mDatabaseCallback!!.getDatabaseCallback(ConfigDB.TYPE_SET_DATA)
        }
    }

    // function to retrieve data from the table
    @Throws(Exception::class)
    fun dataRetrievalFunction(vararg args: Any) {
        tableName = args[0] as String
        mCentralizedDB = args[1] as CentralizedDB
        mDatabaseCallback = args[2] as DatabaseCallback
        val requestType = args[3] as String

        mSqLiteDatabase = mCentralizedDB!!.writableDatabase
        val cursor: Cursor

        if (tableName == ConfigDB.jobData) {
            cursor =
                mSqLiteDatabase!!.rawQuery("Select * from " + tableName + " ORDER BY " + ConfigDB.KEY_ID + " asc", null)
            if (cursor.count > 0) {
                mGetJobDataN = ArrayList<MockList>()

                while (cursor.moveToNext()) {
                    mGetMockModel = MockList()
                    mGetMockModel!!.description = cursor.getString(cursor.getColumnIndex(DataBaseKeys.DESCRIPTION))
                    mGetMockModel!!.imageUrl = cursor.getString(cursor.getColumnIndex(DataBaseKeys.IMAGE_URL))
                    mGetMockModel!!.id = cursor.getString(cursor.getColumnIndex(DataBaseKeys.ID))
                    mGetMockModel!!.location.lat = cursor.getString(cursor.getColumnIndex(DataBaseKeys.LAT))
                    mGetMockModel!!.location.lng = cursor.getString(cursor.getColumnIndex(DataBaseKeys.LNG))
                    mGetMockModel!!.location.address = cursor.getString(cursor.getColumnIndex(DataBaseKeys.ADDRESS))

                    mGetJobDataN!!.add(mGetMockModel!!)
                }
                mDatabaseCallback!!.getDatabaseCallback(requestType, mGetJobDataN!!)
            } else {
                mDatabaseCallback!!.getDatabaseCallback(ConfigDB.TYPE_EMPTY_ALL_DATA)
            }
        }
    }


    // function to check to data availability in table
    fun isDataAvailable(tableName: String, centralizedDB: CentralizedDB): Boolean {
        mSqLiteDatabase = centralizedDB.writableDatabase
        val cursorCheck = mSqLiteDatabase!!.rawQuery("Select * from $tableName", null)
        if (cursorCheck.count > 0) {
            cursorCheck.close()
            return true
        } else {
            cursorCheck.close()
            return false
        }
    }

    // function to delete table from Database
    @Throws(Exception::class)
    fun deleteTableDB(tableName: String, centralizedDB: CentralizedDB) {
        mSqLiteDatabase = centralizedDB.writableDatabase
        Log.e("testing_deletetable", "- $tableName")
        val deleteProgress = mSqLiteDatabase!!.delete(tableName, null, null)
    }

    // function to delete database
    fun DB_Delete(vararg args: Any) {
        mCentralizedDB = args[0] as CentralizedDB
        mSqLiteDatabase = mCentralizedDB!!.readableDatabase
        mSqLiteDatabase!!.close()
    }
}