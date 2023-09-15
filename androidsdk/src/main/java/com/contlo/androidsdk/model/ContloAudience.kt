package com.contlo.androidsdk.model

import com.google.gson.annotations.SerializedName

data class ContloAudience(
    @SerializedName("first_name") val userFirstName: String? = null,
    @SerializedName("last_name") val userLastName: String? = null,
    @SerializedName("city") val userCity: String? = null,
    @SerializedName("country") val userCountry: String? = null,
    @SerializedName("zip") val userZip: String? = null,
    @SerializedName("email") var userEmail: String? = null,
    @SerializedName("phone_number") var userPhone: String? = null,
    @SerializedName("custom_properties") val customProperties: HashMap<String, String>? = null,
    @SerializedName("is_profile_update") var isProfileUpdate: Boolean = false,
    @SerializedName("mobile_push_consent") var isMobilePushConsent: Boolean = false,
    @SerializedName("fcm_token") var firebaseToken: String? = null,
    @SerializedName("API_KEY") var contloApiKey: String? = null,
    @SerializedName("advertising_id") var advertisingId: String? = null
) {
    fun processAudience(): ContloAudience {
        userEmail = if(userEmail.isNullOrBlank()) null else userEmail
        userPhone = if(userPhone.isNullOrBlank()) null else userPhone
        firebaseToken = if(firebaseToken.isNullOrBlank()) null else firebaseToken
        return this
    }
}
