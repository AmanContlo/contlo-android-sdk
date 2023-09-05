package com.contlo.androidsdk.UserProfile

import com.google.gson.annotations.SerializedName

data class ContloAudi(
    @SerializedName("first_name") val userFirstName: String? = null,
    @SerializedName("last_name") val userLastName: String? = null,
    @SerializedName("city") val userCity: String? = null,
    @SerializedName("country") val userCountry: String? = null,
    @SerializedName("zip") val userZip: String? = null,
    @SerializedName("email") val userEmail: String? = null,
    @SerializedName("phone_number") val userPhone: String? = null,
    @SerializedName("custom_properties") val customProperties: HashMap<String, String>? = null,
    @SerializedName("is_profile_update") var isProfileUpdate: Boolean = false,
    @SerializedName("mobile_push_consent") var isMobilePushConsent: Boolean = false,
    @SerializedName("FCM_Token") var firebaseToken: String? = null,
    @SerializedName("API_KEY") var contloApiKey: String? = null,
    @SerializedName("advertising_id") var adverisingId: String? = null
)
