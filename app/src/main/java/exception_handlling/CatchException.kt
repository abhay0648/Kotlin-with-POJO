package exception_handlling

import android.util.Log

// object to get centralise exception  and later on to develop a function to send this to server for daily issue tickets
object CatchException {
    fun ExceptionSend(exception: Exception) {
        Log.e("exception", "exception ---  $exception")
    }
}