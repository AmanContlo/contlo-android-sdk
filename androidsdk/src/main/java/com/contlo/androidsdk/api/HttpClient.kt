package com.contlo.androidsdk.api

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HttpClient {

    fun sendPOSTRequest(url: String, headers: HashMap<String, String>, params: JSONObject?): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"

        // Add headers
        headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }

        // Add parameters
        if ( params != null) {
            connection.doOutput = true
            val postData = params.toString()
            OutputStreamWriter(connection.outputStream).use {
                it.write(postData)
            }

        }

        // Get response
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_ACCEPTED ) {
            val inputStream = connection.inputStream
            return convertStreamToString(inputStream)
        }

        else {
            val errorStream = connection.errorStream
            return if (errorStream != null) {
                val error = convertStreamToString(errorStream)
                Log.d("Contlo-API Request", "Error: $error")
                error
            } else {
                Log.d("Contlo-API Request", "Error: HTTP error code $responseCode")
                "Error: HTTP error code $responseCode"
            }
        }
    }


    private fun convertStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append("\n")
        }
        reader.close()
        return stringBuilder.toString()
    }

}


