package com.example.nft

import com.google.gson.annotations.SerializedName

data class Assets(
    @SerializedName("assets")
    val assets: List<Collection>
)

data class Collection(
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("permalink") val permalink: String
)