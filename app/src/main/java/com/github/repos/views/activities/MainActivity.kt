package com.github.repos.views.activities

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.view.Menu
import com.github.repos.R
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import com.github.repos.data.adapers.ReposAdapter
import com.github.repos.utils.AppNetworkState
import com.github.repos.utils.NetworkUtils
import com.github.repos.viewModel.MainActivityViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mViewModel: MainActivityViewModel

    @Inject
    lateinit var mLinearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var mAdapter: ReposAdapter

    private var snackbar: Snackbar? = null

    private var currentQuery = "googleSamples"

    private lateinit var searchView: SearchView
    private lateinit var myActionMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.all_github)

        initUI()
    }

    private fun performDependencyInjection() {
        AndroidInjection.inject(this)
    }

    private fun initUI() {

        sw_home.setOnRefreshListener {
            updateQuery()
        }
        rc_home.layoutManager = mLinearLayoutManager
        rc_home.itemAnimator = DefaultItemAnimator()
        rc_home.setHasFixedSize(true)
        rc_home.adapter = mAdapter
        mAdapter.attachContext(this)

        callSearchApi(currentQuery)

    }

    private fun callSearchApi(value: String) {
        if (NetworkUtils.isNetworkConnected(this)) {
            mViewModel.callGithubRepos(value)
            subscribeObserver()

        } else {
            showSnackBar(getString(R.string.all_no_internet))
        }
    }

    /**
     * set observer on the LiveData objects to be notified for any update
     * */
    private fun subscribeObserver() {

        // Adapter is notified for any data updates
        mViewModel.liveGithubResponse.observe(this, Observer {
            mAdapter.submitList(it)

        })

        // Adapter is notified for any state updates to show or hide progress bar
        mViewModel.networkState.observe(this, Observer {
            mAdapter.updateNetworkState(it!!)
        })

        // listen for the initial state updates to show or hide swipe refresh loader
        mViewModel.initialState.observe(this, Observer {
            when (it!!.state) {
                AppNetworkState.State.LOADING -> sw_home.isRefreshing = true
                AppNetworkState.State.LOADED -> sw_home.isRefreshing = false
                AppNetworkState.State.FAILED -> sw_home.isRefreshing = false
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        myActionMenuItem = menu!!.findItem(R.id.action_search)

        searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                currentQuery = query!!
                updateQuery()

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        return true
    }

    /**
     * called when there is any update in the query string or perform refreshing
     * */
    private fun updateQuery() {
        if (NetworkUtils.isNetworkConnected(this)) {
            rc_home.scrollToPosition(0)
            (rc_home.adapter as? ReposAdapter)?.submitList(null)

            mViewModel.liveGithubDataSource.value!!.invalidate()
            callSearchApi(currentQuery)
        } else {
            showSnackBar(getString(R.string.all_no_internet))
        }
    }


    fun showSnackBar(string: String) {
        snackbar = Snackbar.make(findViewById(android.R.id.content), string, Snackbar.LENGTH_LONG)
        snackbar?.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar?.setAction("DISMISS") {
            snackbar?.dismiss()
        }
        snackbar?.show()
    }
}
