package com.github.repos.views.activities

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.view.Menu
import com.github.repos.R
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import com.github.repos.data.adapers.ReposAdapter
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
        rc_home.layoutManager = mLinearLayoutManager
        rc_home.itemAnimator = DefaultItemAnimator()
        rc_home.setHasFixedSize(true)
        rc_home.adapter = mAdapter
        mAdapter.attachContext(this)

        mViewModel.callGithubRepos("googleSamples")

        subscribeObserver()
    }

    private fun subscribeObserver() {
        mViewModel.liveGithubResponse.observe(this, Observer {
            mAdapter.submitList(it)
        })

        mViewModel.networkState.observe(this, Observer {
            mAdapter.updateNetworkState(it!!)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val myActionMenuItem = menu!!.findItem(R.id.action_search)

        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                mViewModel.liveGithubDataSource.observe(this@MainActivity, Observer {
                    it!!.invalidate()
                    subscribeObserver()
                    mViewModel.callGithubRepos(query!!)
                    if (!searchView.isIconified) {
                        searchView.isIconified = true
                    }
                    myActionMenuItem.collapseActionView()
                })

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        return true
    }
}
