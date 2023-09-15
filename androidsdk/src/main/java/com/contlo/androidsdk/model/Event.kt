package com.contlo.androidsdk.model

import com.google.gson.annotations.SerializedName

data class Event(
    val event: String,
    @SerializedName("fcm_token") val fcmToken: String?,
    var email: String?,
    @SerializedName("phone_number") var phoneNumber: String?,
    @SerializedName("properties") val property: HashMap<String, String>,
    @SerializedName("mobile_push_consent") val pushConsent: Boolean,
    @SerializedName("profile_properties") val profileProperty: HashMap<String, String>?
) {
    fun processEvent(): Event {
        email = if(email.isNullOrBlank()) null else email
        phoneNumber = if(phoneNumber.isNullOrBlank()) null else phoneNumber
        return this
    }
}