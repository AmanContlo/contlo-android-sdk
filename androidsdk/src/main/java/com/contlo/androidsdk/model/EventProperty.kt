package com.contlo.androidsdk.model

data class EventProperty(
    var appName: String,
    var appVersion: String,
    var packageName: String,
    var OSVersion: String,
    var modelName: String,
    var manufacturer: String,
    var apiLevel: String,
    var networkType: String,
    var OSType: String,
    var source: String,
    var SDKVersion: String,
    var deviceEventTime: String,
    var timeZone: String,
    var SDKPlatform: Int
)