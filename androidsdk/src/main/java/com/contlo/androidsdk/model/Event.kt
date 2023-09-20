package com.contlo.androidsdk.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Event(

    @SerializedName("event_id") val eventId: String = generateId(),
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

    companion object {
        private const val prefix = "event-"

        private fun generateId(): String {
            return prefix + UUID.randomUUID().toString()
        }
    }
}