package com.contlo.contlosdk

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import org.junit.Assert.*
import android.content.pm.ApplicationInfo
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.junit.Test


class IdentifyAPITest {

    @Test
    fun sendRequest_shouldMakePostRequestWithCorrectDataAndHeaders() {
//        // Create a mock context and package manager
//        val mockContext = mock(Context::class.java)
//        val mockPackageManager = mock(PackageManager::class.java)
//
//        // Set up the package manager to return the API key metadata
//        val apiKeyMetadata = Bundle()
//        apiKeyMetadata.putString("my_sdk_api_key", "test-api-key")
//        `when`(mockPackageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(ApplicationInfo().apply {
//            metaData = apiKeyMetadata
//        })
//        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
//
//        // Create a mock request queue and add a mock response to it
//        val mockQueue = mock(RequestQueue::class.java)
//        val mockResponse = JSONObject("{\"status\":\"success\"}")
//        `when`(mockQueue.add(any(JsonObjectRequest::class.java))).thenAnswer {
//            val request = it.getArgument<JsonObjectRequest>(0)
//            request.deliverResponse(mockResponse)
//            null
//        }
//
//        // Call the sendRequest method with test data and mocks
//        val sdk = IdentifyAPI(mockContext, mockQueue)
//        sdk.sendRequest("John", "Doe", "john.doe@example.com", "1234567890", "New York", "USA", "12345", "key", "value")
//
//        // Verify that the correct URL, data, and headers were used in the request
//        val argumentCaptor = ArgumentCaptor.forClass(JsonObjectRequest::class.java)
//        verify(mockQueue).add(argumentCaptor.capture())
//        val capturedRequest = argumentCaptor.value
//        assertEquals(Request.Method.POST, capturedRequest.method)
//        assertEquals("https://api.contlo.com/v1/identify", capturedRequest.url)
//        assertEquals("{\"first_name\":\"John\",\"last_name\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone_number\":\"1234567890\",\"city\":\"New York\",\"country\":\"USA\",\"zip\":\"12345\",\"custom_properties\":{\"key\":\"value\"}}", capturedRequest.body.toString())
//        val expectedHeaders = mapOf(
//            "accept" to "application/json",
//            "X-API-KEY" to "test-api-key",
//            "content-type" to "application/json"
//        )
//        assertEquals(expectedHeaders, capturedRequest.headers)
    }

}