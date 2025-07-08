import com.example.evchargingapp.charging.model.ChargingInfoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.GET

interface ChargingApiService {

    @GET("charging/{pileId}/info")
    fun getChargingInfo(@Path("pileId") pileId: String): Call<ChargingInfoResponse>

    @POST("charging/{pileId}/start")
    fun startCharging(@Path("pileId") pileId: String): Call<ResponseBody> // ✅ CHANGED

    @POST("charging/{pileId}/stop")
    fun stopCharging(@Path("pileId") pileId: String): Call<ResponseBody> // ✅ CHANGED
}
