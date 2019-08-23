package functionalityAll

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo



//Checking Functionality class
class CentraliseCheck {

    //Check internet connectivity
    fun isNetWorkStatusAvailable(applicationContext: Context): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {
                if (networkInfo.isConnected)
                    return true
            }
        }
        return false
    }
}