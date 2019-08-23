package viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import callBacks.Api
import callBacks.ParentApiCallback
import com.lalamove.lalamovetest.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//This class for get all data from API and transfer it to utils model class
class MockViewModel : ViewModel() {

    //this is the data that we will fetch asynchronously
    private var mMockList: MutableLiveData<List<MockList>>? = null
    private var mOffsetCount: Int = 0
    private var mLimitCount: Int = 0
    private var mParentApiCallback: ParentApiCallback? = null
    private var mContext: Context? = null

    //we will call this method to get the data
    fun getMockList(
        mOffsetCount: Int,
        mLimitCount: Int,
        mParentApiCallback: ParentApiCallback,
        mContext: Context
    ): LiveData<List<MockList>> {
        //if the list is null
        this.mParentApiCallback = mParentApiCallback
        this.mContext = mContext
        mMockList = null
        if (mMockList == null) {
            mMockList = MutableLiveData()
            this.mOffsetCount = mOffsetCount
            this.mLimitCount = mLimitCount
            //we will load it asynchronously from server in this method
            loadMockList(mOffsetCount, mLimitCount)
        }
        //finally we will return the list
        return mMockList as MutableLiveData<List<MockList>>
    }


    //This method is using Retrofit to get the JSON data from URL
    private fun loadMockList(mOffsetCount: Int, mLimitCount: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(Api::class.java)
        Log.e("mMockList", "mMockList$mOffsetCount-----$mLimitCount")

        val call = api.getMockList(mOffsetCount, mLimitCount)

        call.enqueue(object : Callback<List<MockList>> {
            override fun onResponse(call: Call<List<MockList>>, response: Response<List<MockList>>) {
                //finally we are setting the list to our MutableLiveData
                mMockList!!.value = response.body()
            }

            override fun onFailure(call: Call<List<MockList>>, t: Throwable) {
                mParentApiCallback!!.dataCallBack(mContext!!.getString(R.string.error))
                Log.e("Failur", "Failur")
            }
        })
    }
}