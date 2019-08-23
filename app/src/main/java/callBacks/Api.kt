package callBacks

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import viewModel.MockList
//
interface Api {

    //Add deliveries to get delivery add with base URL
    @GET("deliveries")
    fun getMockList(
        //Add query to get dynamic offset and limit
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<List<MockList>>


    //Add base URL and dynamic keys
    companion object {
        val BASE_URL = "https://mock-api-mobile.dev.lalamove.com/"
        val offset = "offset"
        val limit = "limit"
    }
}