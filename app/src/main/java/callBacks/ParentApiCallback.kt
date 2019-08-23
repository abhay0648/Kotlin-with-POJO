package callBacks

// interface for API callback and only taking error callback as retrofit is managing the success callback
interface ParentApiCallback {
    fun dataCallBack(string: String)
}
