package com.example.schoolnet

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Fetches top-level categories (01_Natural Sciences, etc.)
    @GET("macros/s/AKfycbxD1AWY7suWPjASh-7sclewKZsJW9FxhnRPMsC_D3Y_WEo3gAItyJnljl2jFIgWvSA/exec")
    suspend fun getCurriculum(): List<PathwayResponse>

    // Fetches contents inside a folder (Subfolders/Shortcuts/Google Docs)
    @GET("macros/s/AKfycbxD1AWY7suWPjASh-7sclewKZsJW9FxhnRPMsC_D3Y_WEo3gAItyJnljl2jFIgWvSA/exec")
    suspend fun getSchoolData(@Query("folderId") folderId: String?): Response<ResponseBody>
}
