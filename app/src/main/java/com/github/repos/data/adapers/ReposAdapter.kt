package com.github.repos.data.adapers

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.content.Intent
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.repos.R
import com.github.repos.data.models.RepoResponse
import com.github.repos.databinding.NetworkItemBinding
import com.github.repos.databinding.RepoListItemBinding
import com.github.repos.utils.AppNetworkState
import com.github.repos.views.activities.RepoDetailsActivity
import com.squareup.picasso.Picasso


class ReposAdapter : PagedListAdapter<RepoResponse.Item, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val TYPE_PROGRESS = 0
    private val TYPE_ITEM = 1
    private var networkState: AppNetworkState? = null

    private lateinit var intent: Intent
    lateinit var mContext: Context

    fun attachContext(ctx: Context) {
        this.mContext = ctx
        intent = Intent(mContext, RepoDetailsActivity::class.java)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_PROGRESS) {
            val headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false)
            NetworkStateItemViewHolder(headerBinding)

        } else {
            val itemBinding = RepoListItemBinding.inflate(layoutInflater, parent, false)
            ReposItemViewHolder(itemBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReposItemViewHolder) {
            holder.onBind(position)
        } else {
            (holder as NetworkStateItemViewHolder).onBind(networkState)
        }
    }

    inner class ReposItemViewHolder(private var mViewBinding: RepoListItemBinding) : RecyclerView.ViewHolder(mViewBinding.root) {

        fun onBind(position: Int) {
            val item = getItem(position)
            item!!.name?.let {
                mViewBinding.tvRepoName.text = it
            }

            item.score.let {
                mViewBinding.tvRepoScoreValue.text = it.toString()
            }

            item.owner!!.avatar_url?.let {
                Picasso.with(mContext)
                        .load(it)
                        .placeholder(R.drawable.profile_placeholder)
                        .into(mViewBinding.imgUserAvatar)
            }

            mViewBinding.cardRepoItem.setOnClickListener {
                mContext.startActivity(intent)

            }
            mViewBinding.executePendingBindings()
        }

    }

    inner class NetworkStateItemViewHolder(var networkItemBinding: NetworkItemBinding) : RecyclerView.ViewHolder(networkItemBinding.root) {
        fun onBind(networkState: AppNetworkState?) {
            if (networkState != null && networkState == AppNetworkState.State.LOADING) {
                networkItemBinding.proNetwork.visibility = View.VISIBLE
            } else {
                networkItemBinding.proNetwork.visibility = View.GONE
            }

            if (networkState != null && networkState == AppNetworkState.State.FAILED) {
                networkItemBinding.tvNetworkError.visibility = View.VISIBLE
                networkItemBinding.tvNetworkError.text = (networkState as AppNetworkState.TooliStringState).value!!
            } else {
                networkItemBinding.tvNetworkError.visibility = View.GONE
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_PROGRESS
        } else {
            TYPE_ITEM
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != AppNetworkState.State.LOADED
    }

    fun updateNetworkState(newNetworkState: AppNetworkState) {
        val previousState = this.networkState
        val previousExtraRow = hasExtraRow()
        this.networkState = newNetworkState

        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (newExtraRow && previousState !== newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        var DIFF_CALLBACK = object : DiffUtil.ItemCallback<RepoResponse.Item>() {
            override fun areItemsTheSame(oldItem: RepoResponse.Item, newItem: RepoResponse.Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RepoResponse.Item, newItem: RepoResponse.Item): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }

}


