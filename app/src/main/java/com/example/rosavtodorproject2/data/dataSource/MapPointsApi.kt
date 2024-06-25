package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.PointPostResponse
import com.example.rosavtodorproject2.data.models.PointsGetResponse
import com.example.rosavtodorproject2.data.models.RequestPoint
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface MapPointsApi {
    @GET("verifiedPoints")
    suspend fun getPoints(
        @Query("Coordinates.Latitude") latitude:Double,
        @Query("Coordinates.Longitude") longitude:Double,
        @Query("Radius") radius:Double = 100.0,
    ): Response<PointsGetResponse>
    @POST("unverifiedPoints")
    suspend fun addPoint(
        @Body requestPoint: RequestPoint
    ):Response<PointPostResponse>
    @Multipart
    @POST("unverifiedPoints/{pointId}/files")
    suspend fun addPhotoToPoint(
        @Path("pointId") pointId: UUID,
        @Part Files: List<MultipartBody.Part>
    )
}
