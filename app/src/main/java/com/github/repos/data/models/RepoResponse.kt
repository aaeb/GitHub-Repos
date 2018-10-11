package com.github.repos.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class RepoResponse(@SerializedName("total_count") var count: Long,
                   @SerializedName("items") var items: ArrayList<Item>
) {

    open class Item(@SerializedName("id") var id: Long,
                    @SerializedName("name")
                    var name: String?,
                    @SerializedName("owner")
                    var owner: Owner?,
                    @SerializedName("html_url")
                    var html_url: String?,
                    @SerializedName("description")
                    var description: String?,
                    @SerializedName("created_at")
                    var created_at: String?,
                    @SerializedName("stargazers_count")
                    var stargazers_count: Int?,
                    @SerializedName("watchers_count")
                    var watchers_count: Long?,
                    @SerializedName("language")
                    var language: String?,
                    @SerializedName("forks_count")
                    var forks_count: Long?,
                    @SerializedName("score")
                    var score: Float?,
                    @SerializedName("watchers")
                    var watchers: Long?
    ){

        override fun equals(other: Any?): Boolean {

            if (other == this)
                return true

            var item: Item = other as Item
            return item.id == this.id

        }

    }

    open class Owner(@SerializedName("avatar_url") var avatar_url: String?,
                     @SerializedName("login") var login: String?
    )
}