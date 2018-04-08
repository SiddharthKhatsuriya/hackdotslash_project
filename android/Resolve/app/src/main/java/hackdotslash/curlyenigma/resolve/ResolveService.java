package hackdotslash.curlyenigma.resolve;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ResolveService {
    public String BASE_URL = "http://192.168.43.126:3000/api/";
    @FormUrlEncoded
    @POST("user/login")
    Call<String> authenticate(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/login")
    Call<String> register(
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("email") String email,
            @Field("password") String password);


    @GET("complaints/categories/all")
    Call<String> categories();

    @GET("complaints/all")
    Call<String> complaints();

    @GET("complaints/{id}/details")
    Call<String> complaint(@Path("id") String id);

    @FormUrlEncoded
    @POST("complaints/create")
    Call<String> createComplaint(
            @Field("category") String category,
            @Field("description") String description,
            @Field("image") String image,
            @Field("token") String token,
            @Field("lat") double lat,
            @Field("lng") double lng);
}
