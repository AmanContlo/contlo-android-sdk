package com.contlo.androidsdk.model

import com.google.gson.annotations.SerializedName

data class Event(
    val event: String,
    @SerializedName("fcm_token") val fcmToken: String?,
    val email: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("properties") val property: HashMap<String, String>,
    @SerializedName("mobile_push_consent") val pushConsent: Boolean,
    @SerializedName("profile_properties") val profileProperty: HashMap<String, String>?
) {
    fun removeEmptyValues() = Event(
        event, fcmToken, if(email.isNullOrEmpty()) null else email, if(phoneNumber.isNullOrEmpty()) null else phoneNumber, property, pushConsent, profileProperty
    )
}