package com.github.repos.views.activities

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.text.util.LinkifyCompat
import android.support.v7.app.AppCompatActivity
import android.text.util.Linkify
import android.view.MenuItem
import com.github.repos.R
import com.github.repos.data.models.RepoResponse
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_repo_details.*
import kotlinx.android.synthetic.main.content_repo_details.*

class RepoDetailsActivity : AppCompatActivity() {
    companion object {
        const val REPO_DATA = "repo_data"
    }

    private lateinit var mReposItem: RepoResponse.Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_details)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        updateUI()

    }

    private fun updateUI() {
        var itemStr = intent.getStringExtra(REPO_DATA)
        mReposItem = Gson().fromJson(itemStr, RepoResponse.Item::class.java)

        tv_repo_details_name.text = mReposItem.name
        tv_repo_details_description.text = mReposItem.description

        tv_repo_details_url.text = mReposItem.html_url
        tv_repo_details_url.autoLinkMask= Linkify.ALL
        tv_repo_details_url.setOnClickListener {
            navigateToBrowser(mReposItem.html_url!!)
        }

        tv_repo_details_lang.text = mReposItem.language
        tv_repo_details_create.text = mReposItem.created_at

        tv_repo_details_score.text = mReposItem.score.toString()
        tv_repo_details_forks.text = mReposItem.forks_count.toString()
        tv_repo_details_watchers.text = mReposItem.watchers.toString()

        Picasso.with(this)
                .load(mReposItem.owner!!.avatar_url)
                .into(img_user_avatar)

        toolbar_layout.title = mReposItem.owner!!.login
    }

    private fun navigateToBrowser(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
