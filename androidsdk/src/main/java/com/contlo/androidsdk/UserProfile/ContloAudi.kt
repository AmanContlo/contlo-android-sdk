package com.contlo.androidsdk.UserProfile

import com.google.gson.annotations.SerializedName

data class ContloAudi(
    @SerializedName("first_name") var userFirstName: String? = null,
    @SerializedName("last_name") var userLastName: String? = null,
    @SerializedName("city") var userCity: String? = null,
    @SerializedName("country") var userCountry: String? = null,
    @SerializedName("zip") var userZip: String? = null,
    @SerializedName("email") var userEmail: String? = null,
    @SerializedName("phone_number") var userPhone: String? = null,
    @SerializedName("custom_properties") var customProperties: HashMap<String, String>? = null,
    @SerializedName("is_profile_update") var isProfileUpdate: Boolean = false,
    @SerializedName("mobile_push_consent") var isMobilePushConsent: Boolean = false,
    @SerializedName("FCM_Token") var firebaseToken: String? = null,
    @SerializedName("API_KEY")var contloApiKey: String? = null
)
