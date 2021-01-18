package com.app.perk99driver.networking;


import java.util.HashMap;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

 // driver_forgot_password
 // driver_verifyOtp
 // driver_update_password

public interface InterfaceApi
{

    // http://192.168.1.21:7000/
    // String BASE_URL = "http://192.168.1.21:7000/mobile/api/";
    // String BASE_URL = "http://165.22.215.99:7000/mobile/api/";

    String BASE_URL = "http://178.128.116.149/food_app/public/api/";

    @FormUrlEncoded
    @POST("driver_register")
    Observable<ResponseBody> register(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST("driver_forgot_password")
    Observable<ResponseBody> forgetPassword(@FieldMap HashMap<String, String> param);



    @FormUrlEncoded
    @POST("driver_verifyOtp")
    Observable<ResponseBody> verifyOTP(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST("driver_update_password")
    Observable<ResponseBody> resetPassword(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST("contact_us")
    Observable<ResponseBody> contactUs(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST("accept_job")
    Observable<ResponseBody> acceptJob(@Field("job_id") String jobId,@Field("order_id") String order_id,@Field("provider") String provider);

    @FormUrlEncoded
    @POST("reject_job")
    Observable<ResponseBody> rejectJob(@Field("job_id") String jobId,@Field("order_id") String order_id,@Field("provider") String provider);



    @FormUrlEncoded
    @POST("cancel_order")
    Observable<ResponseBody> cancel_order(@Field("order_id") String order_id,@Field("provider") String provider);

    @FormUrlEncoded
    @POST("driver_login")
    Observable<ResponseBody> login(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST("go_online")
    Observable<ResponseBody> online(@Field("provider") String provider);

    @FormUrlEncoded
    @POST("get_earning")
    Observable<ResponseBody> getEarning(@Field("provider") String provider);

    @FormUrlEncoded
    @POST("go_offline")
    Observable<ResponseBody> offline(@Field("provider") String provider);

    @FormUrlEncoded
    @POST("pickup_order")
    Observable<ResponseBody> pickup_order(@Field("order_id") String order_id,@Field("provider") String provider);

    @FormUrlEncoded
    @POST("go_to_dropoff")
    Observable<ResponseBody> go_to_dropoff(@Field("order_id") String order_id,@Field("provider") String provider);

    @FormUrlEncoded
    @POST("dropoff_order")
    Observable<ResponseBody> droppedOff(@Field("order_id") String order_id,@Field("provider") String provider);

    @FormUrlEncoded
    @POST("withdraw_request")
    Observable<ResponseBody> sendTransferRequest(@Field("amount") String amount,@Field("provider") String provider);

    @GET("get_driver")
    Observable<ResponseBody> getProfile(@Query("provider") String provider);

    @FormUrlEncoded
    @POST("update_driver_location")
    Observable<ResponseBody> sendLocation(@Field("lat") String lat, @Field("lng") String lng,@Field("provider") String provider);

    @GET("get_jobs")
    Observable<ResponseBody> getJobs(@Query("provider") String provider);

    @Multipart
    @POST("update_driver_profile")
    Observable<ResponseBody> updateProfile(@Part("first_name") RequestBody username,
                                           @Part("last_name") RequestBody email,
                                           @Part("country_code") RequestBody phone,
                                           @Part("mobile") RequestBody address,
                                           @Part("address") RequestBody lang,
                                           @Part("provider") RequestBody provider,
                                           @Part MultipartBody.Part imageFileBody);

}
