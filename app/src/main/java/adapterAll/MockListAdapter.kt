package adapterAll

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lalamove.lalamovetest.R
import com.lalamove.lalamovetest.MapActivity
import staticClasses.DataBaseKeys
import viewModel.MockList

// Class to create Adapter for the use of recycler view
class MockListAdapter(internal var mCtx: Context, internal var mMockList: List<MockList>) :
    RecyclerView.Adapter<MockListAdapter.MockViewHolder>() {
    private var mMockListGetData: MockList? = null

    // update data in adapater
    fun updateData(mMockList: List<MockList>) {
        this.mMockList = mMockList
        notifyDataSetChanged()
    }

    // overrided function create view holder from adapter view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.mock_repo_adapter_view, parent, false)
        return MockViewHolder(view)
    }

// overrided function to bind view and holder for adapter view
    override fun onBindViewHolder(holder: MockViewHolder, position: Int) {
        val mockListSub = mMockList[position]

        Glide.with(mCtx)
            .load(mockListSub.imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.imageShow)

        holder.descriptionTextView.text = mockListSub.description
    }

    // function to count the number of item in the list
    override fun getItemCount(): Int {
        return mMockList.size
    }

    // inner class to create custom holder to holds particular inner view and data
    inner class MockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentClick: CardView
        var imageShow: ImageView
        var descriptionTextView: TextView

        init {
            parentClick = itemView!!.findViewById(R.id.parentClick)
            imageShow = itemView.findViewById(R.id.imageShow)
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView)

            parentClick.setOnClickListener {
                val pos = adapterPosition
                mMockListGetData = mMockList[pos]

                Log.e("mMockListGetData","mMockListGetData"+mMockListGetData!!.options.lng)

                val openMap = Intent(mCtx, MapActivity::class.java)
                openMap.putExtra(DataBaseKeys.LAT, mMockListGetData!!.location.lat)
                openMap.putExtra(DataBaseKeys.LNG, mMockListGetData!!.location.lng)
                openMap.putExtra(DataBaseKeys.IMAGE_URL, mMockListGetData!!.imageUrl)
                openMap.putExtra(DataBaseKeys.DESCRIPTION, mMockListGetData!!.description)
                mCtx.startActivity(openMap)
            }

        }

    }
}
