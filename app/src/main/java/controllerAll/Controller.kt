package controllerAll

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView

import callBacks.DatabaseCallback
import com.lalamove.lalamovetest.R
import exception_handlling.CatchException
import functionalityAll.*
import staticClasses.ConfigDB


//Controller centralised call calling in whole application extended Application
class Controller : Application() {
    private val mCentraliseCheck = CentraliseCheck()
    private var mCentralizedDB: CentralizedDB? = null
    private var mSharedPrefrencesData: SharedPrefrencesData? = null
    private var mPDialog: ProgressDialog? = null

    // overrided function of application cycle and initialise functions for objects
    override fun onCreate() {
        super.onCreate()
        initCentraliseDB()
        initialiseSharedPrefrence(this)
    }

    // function to initialise database object
    fun initCentraliseDB() {
        mCentralizedDB = CentralizedDB(this, ConfigDB.DB_VERSION)
    }

    // function to initialise shared preference object
    fun initialiseSharedPrefrence(context: Context) {
        mSharedPrefrencesData = SharedPrefrencesData(context)
    }


    //Check Internet connections
    fun isNetWorkStatusAvailable(context: Context): Boolean {
        return mCentraliseCheck.isNetWorkStatusAvailable(context)
    }

    // function to check data availability in database
    fun centralizedDataCheck(tableName: String): Boolean {
        return mCentralizedDB!!.isDataAvailable(tableName, mCentralizedDB!!)
    }

    // function to get data from the table of database
    fun centralizedDBGetData(vararg args: Any) {
        try {
            mCentralizedDB!!.dataRetrievalFunction(args[0], mCentralizedDB!!, args[1], args[2])
        } catch (e: Exception) {
            CatchException.ExceptionSend(e)
        }

    }

    // function to insert data in table of database
    fun centralizedDBSetData(tableName: String, databaseCallback: DatabaseCallback, args: Any) {
        try {
            mCentralizedDB!!.dataInsertionFunction(tableName, mCentralizedDB!!, databaseCallback, args)
        } catch (e: Exception) {
            CatchException.ExceptionSend(e)
        }

    }

    // function to delete table from database
    fun deleteTableDB(tableName: String) {
        try {
            mCentralizedDB!!.deleteTableDB(tableName, mCentralizedDB!!)
        } catch (e: Exception) {
            CatchException.ExceptionSend(e)
        }

    }

    // function to set string type of data in shared preference
    fun setString(key: String, value: String) {
        mSharedPrefrencesData!!.setString(key, value)
    }

    // function to set boolean type of data in shared preference
    fun setBoolean(key: String, value: Boolean) {
        mSharedPrefrencesData!!.setBoolean(key, value)
    }

    // function to set integer values in shared preference
    fun setInt(key: String, value: Int) {
        mSharedPrefrencesData!!.setInt(key, value)
    }

    // function to get string type data from shared preference
    fun getString(key: String): String {
        return mSharedPrefrencesData!!.getString(key)
    }

    // function to get boolean type data from shared preference
    fun getBoolean(key: String): Boolean {
        return mSharedPrefrencesData!!.getBoolean(key)
    }

    // function to get integer type data from shared preference
    fun getInt(key: String): Int {
        return mSharedPrefrencesData!!.getInt(key)
    }


    //Stop Progress Dialog
    fun pdStop() {
        if (mPDialog != null) {
            mPDialog!!.dismiss()
        }
    }

    //Start Progress Dialog
    fun pdStart(context: Context, message: String) {
        mPDialog = ProgressDialog(context)
        try {
            mPDialog!!.show()
        } catch (e: WindowManager.BadTokenException) {
        }

        try {
            mPDialog!!.setCancelable(false)
            mPDialog!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            mPDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mPDialog!!.setContentView(R.layout.progress_dialog)

            val loadingText = mPDialog!!.findViewById(R.id.loadingText) as TextView
            loadingText.text = message

        } catch (e: Exception) {
        }

    }

}