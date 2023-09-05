package com.contlo.androidsdk.api

import android.util.Log
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.main.ContloApp
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HttpClient {

    fun sendPOSTRequest(url: String, data: String): String {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            addGlobalHeaders(connection)
            try {
                if(data.isNotBlank()) {
                    connection.doOutput = true
                    OutputStreamWriter(connection.outputStream).use {
                        it.write(data)
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
                        ContloUtils.printLog(Contlo.getContext(), "Contlo-API Request", "Error: $error")
                        error
                    } else {
                        ContloUtils.printLog(Contlo.getContext(), "Contlo-API Request", "Error: HTTP error code $responseCode")
                        "Error: HTTP error code $responseCode"
                    }
                }
            } catch (IOE: IOException) {
                IOE.printStackTrace()
                throw IOE
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    fun sendPOSTRequest(url: String, headers: HashMap<String, String>, params: JSONObject?): String {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"

            // Add headers
            headers.forEach { (key, value) ->
                connection.setRequestProperty(key, value)
            }
            try {
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
                        ContloUtils.printLog(Contlo.getContext(), "Contlo-API Request", "Error: $error")
                        error
                    } else {
                        ContloUtils.printLog(Contlo.getContext(), "Contlo-API Request", "Error: HTTP error code $responseCode")
                        "Error: HTTP error code $responseCode"
                    }
                }
            } catch (IOE: IOException) {
                IOE.printStackTrace()
                throw IOE
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }


    private fun addGlobalHeaders(connection: HttpURLConnection) {
        connection.apply {
            setRequestProperty("accept", "application/json")
            setRequestProperty("X-API-KEY", ContloPreference.getInstance(Contlo.getContext()).getApiKey())
            setRequestProperty("content-type", "application/json")
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


