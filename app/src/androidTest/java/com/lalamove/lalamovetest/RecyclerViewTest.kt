package gojeck.gojecktest.com.gojecktest

import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.lalamove.lalamovetest.DashboardManager
import com.lalamove.lalamovetest.R
import viewModel.MockList
import android.content.Intent
import android.support.test.filters.LargeTest
import com.lalamove.lalamovetest.MapActivity
import staticClasses.DataBaseKeys


@RunWith(AndroidJUnit4::class)
@LargeTest
class RecyclerViewTest{
    private var mMockListGetData: MockList? = null

    @get:Rule
    public val activityRule: ActivityTestRule<DashboardManager> = ActivityTestRule(DashboardManager::class.java)

    @get:Rule
    public val mActivityRule: ActivityTestRule<MapActivity> = ActivityTestRule(MapActivity::class.java, false, false)

    private val rvCount: Int
    get() {
        val recyclerView = activityRule.activity.findViewById<View>(R.id.recyclerView) as RecyclerView
        return recyclerView.adapter!!.itemCount
    }

    @Test
    fun fragmentCanBeInstantiated(){
        if (rvCount > 0) {
//            activityRule.activity.runOnUiThread { val trendingRepoFragment = fragmentInstance }
            // Then use Espresso to test the Fragment
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun checkScrollAndClick() {
        if (rvCount > 0) {
            onView(withId(R.id.recyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    12,
                    click()
                )
            )
        }
    }


    @Test
    fun checkRecyclerviewData() {
        if (rvCount > 0) {
            onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(5, click()))

            val openMap = Intent()
            openMap.putExtra(DataBaseKeys.LAT, mMockListGetData!!.location.lat)
            openMap.putExtra(DataBaseKeys.LNG, mMockListGetData!!.location.lng)
            openMap.putExtra(DataBaseKeys.IMAGE_URL, mMockListGetData!!.imageUrl)
            openMap.putExtra(DataBaseKeys.DESCRIPTION, mMockListGetData!!.description)
            mActivityRule.launchActivity(openMap)
        }
    }
}