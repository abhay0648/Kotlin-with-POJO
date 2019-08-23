package com.lalamove.lalamovetest

import adapterAll.MockListAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import callBacks.DatabaseCallback
import callBacks.ParentApiCallback
import controllerAll.Controller
import exception_handlling.CatchException
import staticClasses.ConfigDB
import viewModel.MockList
import viewModel.MockViewModel
import kotlinx.android.synthetic.main.dashboard_activity.*
import kotlinx.android.synthetic.main.error_layout.*
import staticClasses.Config

// class Dashboard manager as activity which is handling UI of list, swiping, snackbar, error layouts
class DashboardManager : AppCompatActivity(), View.OnClickListener {

    private var mAdapter: MockListAdapter? = null
    private var offsetCount = 0
    private var limitCount = 20
    private var mModel: MockViewModel? = null
    private var mDatabaseCallback: DatabaseCallback? = null
    private var mController: Controller? = null
    private var mGetJobData: List<MockList>? = null
    private var isDatabaseCall: Boolean? = false
    private var isLoading: Boolean = true
    private var isLoadingMore: Boolean = false
    private var isRefreshView: Boolean = false
    private var mParentApiCallback: ParentApiCallback? = null

    // overrided function of activity lifecycle to initiate activity view and initialise controller, callbacks, listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)
        mController = applicationContext as Controller

        setUpCallBack()
        setUpRecyclerView()
        checkDataBase()
        initScrollListener()
        makeSwipeToRefresh()
    }

    // function to define callbacks and segregate data from it
    private fun setUpCallBack() {
        mDatabaseCallback = object : DatabaseCallback {
            override fun getDatabaseCallback(args: Array<out Any>) {
                val dataType = args[0] as String
                when (dataType) {
                    ConfigDB.TYPE_SET_DATA -> {
                        mController!!.centralizedDBGetData(
                            ConfigDB.jobData,
                            mDatabaseCallback!!,
                            ConfigDB.TYPE_GET_ALL_DATA
                        )
                    }
                    ConfigDB.TYPE_GET_ALL_DATA -> {
                        Log.e("data", "data" + args[1]);
                        mGetJobData = args[1] as List<MockList>

                        mAdapter!!.updateData(mGetJobData!!)
                        isLoading = false

                        if (isDatabaseCall!!) {
                            mController!!.pdStop()
                            swipeRefresh.isRefreshing = false
                        }
                    }
                }
            }
        }

        mParentApiCallback = object : ParentApiCallback {
            override fun dataCallBack(string: String) {
                try {
                    if (string == getString(R.string.error)) {
                        Log.e("thisCall", "thisCall")

                        if (isLoadingMore) {
                            swipeRefresh.isRefreshing = false
                            isLoading = false
                            isLoadingMore = false
                            showSnackBarSerevrError()

                            if (offsetCount != 0) {
                                offsetCount -= limitCount
                            }
                        } else {
                            swipeRefresh.isRefreshing = false
                            mController!!.pdStop()
                            showHideErrorLayout(2)
                        }
                    }
                } catch (e: Exception) {
                    mController!!.pdStop()
                    swipeRefresh.isRefreshing = false
                    isLoading = false
                    isLoadingMore = false
                    showSnackBarSerevrError()
                    CatchException.ExceptionSend(e)
                }
            }
        }
    }

    // function to setup the recycler view by intialising views, attaching adapater and passing empty data model as a skeleton for recycler view
    private fun setUpRecyclerView() {
        mGetJobData = ArrayList<MockList>()
        retry.setOnClickListener(this)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = MockListAdapter(this@DashboardManager, mGetJobData!!)
        recyclerView!!.adapter = mAdapter

    }

    // function to initialise scroll listener
    private fun initScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                Log.e("isLoading", "isLoading" + isLoading)

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mGetJobData!!.size - 1) {
                        offsetCount += limitCount
                        limitCount = 20
                        loadMoreFunction()
                    }
                }
            }
        })
    }

    // function to check the data availability after network check to perform operation over database
    private fun checkDataBase() {

        if (mController!!.isNetWorkStatusAvailable(this)) {
            offsetCount = 0
            limitCount = 20
            mController!!.setInt(Config.OFFSET_STRING, offsetCount)
            mController!!.setInt(Config.LIMIT_STRING, limitCount)
            showHideErrorLayout(1)
            mController!!.pdStart(this, "")
            isDatabaseCall = false
            getDataFromAPI()
        } else {
            val isDBDataAvailable = mController!!.centralizedDataCheck(ConfigDB.jobData)
            if (isDBDataAvailable) {
                offsetCount = mController!!.getInt(Config.OFFSET_STRING)
                limitCount = mController!!.getInt(Config.LIMIT_STRING)

                isDatabaseCall = true
                mController!!.centralizedDBGetData(ConfigDB.jobData, mDatabaseCallback!!, ConfigDB.TYPE_GET_ALL_DATA)
            } else {
                isDatabaseCall = false
                swipeRefresh.isRefreshing = false
                showHideErrorLayout(2)
                mController!!.pdStop()
            }
        }
    }

    // retrofit custom callback to get data and pass it to adapter
    private fun getDataFromAPI() {
        mModel = ViewModelProviders.of(this).get(MockViewModel::class.java)
        mModel!!.getMockList(offsetCount, limitCount, mParentApiCallback!!, this).observe(this, Observer { mockList ->
            if (isLoadingMore) {
                isLoading = false
                isLoadingMore = false
                if (mockList != null && mDatabaseCallback != null) {
                    mController!!.centralizedDBSetData(ConfigDB.jobData, mDatabaseCallback!!, mockList!!)
                } else {
                    showSnackBar()
                }
                mController!!.pdStop()
                swipeRefresh.isRefreshing = false
            } else {
                val isDBDataAvailable = mController!!.centralizedDataCheck(ConfigDB.jobData)
                if (isDBDataAvailable) {
                    mController!!.deleteTableDB(ConfigDB.jobData)
                }
                if (mockList != null && mDatabaseCallback != null) {
                    mController!!.centralizedDBSetData(ConfigDB.jobData, mDatabaseCallback!!, mockList!!)
                } else {
                    showHideErrorLayout(2)
                }
                mController!!.pdStop()
                swipeRefresh.isRefreshing = false
                isLoading = false
                isLoadingMore = false
                showHideErrorLayout(1)
            }
        })
    }

    // function to provide swipe to refresh functionality
    private fun makeSwipeToRefresh() {
        swipeRefresh.setOnRefreshListener {
            if (mController!!.isNetWorkStatusAvailable(this)) {
                offsetCount = 0
                limitCount = 20
                mController!!.setInt(Config.OFFSET_STRING, offsetCount)
                mController!!.setInt(Config.LIMIT_STRING, limitCount)
                isDatabaseCall = false
                swipeRefresh.isRefreshing = true
                getDataFromAPI()
            } else {
                isDatabaseCall = false
                swipeRefresh.isRefreshing = false
                showHideErrorLayout(2)
                mController!!.pdStop()
            }
        }
    }

    // function to provide the load more functionality after limit of 20
    fun loadMoreFunction() {
        isLoading = true
        isLoadingMore = true
        if (mController!!.isNetWorkStatusAvailable(this)) {
            mController!!.setInt(Config.OFFSET_STRING, offsetCount)
            mController!!.setInt(Config.LIMIT_STRING, limitCount)
            isDatabaseCall = false
            swipeRefresh.isRefreshing = true
            getDataFromAPI()
        } else {
            isDatabaseCall = false
            swipeRefresh.isRefreshing = false
            isLoading = false
            isLoadingMore = false
            showSnackBar()
        }
    }


    // provide functionality to retry on error view on full error layout screen
    fun callRetryOnErrorView() {
        if (mController!!.isNetWorkStatusAvailable(this)) {
            offsetCount = 0
            limitCount = 20
            mController!!.setInt(Config.OFFSET_STRING, offsetCount)
            mController!!.setInt(Config.LIMIT_STRING, limitCount)
            mController!!.pdStart(this, "")
            isDatabaseCall = false
            getDataFromAPI()
        } else {
            isLoading = false
            isLoadingMore = false
            isDatabaseCall = false
            swipeRefresh.isRefreshing = false
            showHideErrorLayout(2)
            mController!!.pdStop()
        }
    }

    // function to hide and show layouts like error and list layouts
    fun showHideErrorLayout(typeCall: Int) {
        Log.e("CallErroeLayout", "CallErroeLayout" + typeCall)
        when (typeCall) {
            1 -> {
                errorParentLayout!!.visibility = View.GONE
                parentRelativeLayout!!.visibility = View.VISIBLE
            }

            2 -> {
                errorParentLayout!!.visibility = View.VISIBLE
                parentRelativeLayout!!.visibility = View.GONE
            }
        }
    }

    // function to handle snackbar and show error from the server over protocol base
    fun showSnackBarSerevrError() {
        val snackbar = Snackbar
            .make(coordinatorLayout, getString(R.string.oops_something_went_wrong), Snackbar.LENGTH_LONG)

        snackbar.setActionTextColor(Color.RED)

        val sbView = snackbar.view
        val textView = sbView.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.YELLOW)
        snackbar.show()
    }

    // function to show snack bar fro errors when their is no internet connection
    fun showSnackBar() {
        val snackbar = Snackbar
            .make(coordinatorLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                loadMoreFunction()
            }

// Changing message text color
        snackbar.setActionTextColor(Color.RED)

// Changing action button text color
        val sbView = snackbar.view
        val textView = sbView.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.YELLOW)
        snackbar.show()
    }

    // overrided function which call over click on retry of snackbar and calls refresh method
    override fun onClick(v: View) {
        when (v.id) {
            R.id.retry -> {
                callRetryOnErrorView()
            }
        }
    }
}
