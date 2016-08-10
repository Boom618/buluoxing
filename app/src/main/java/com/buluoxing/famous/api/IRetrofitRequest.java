//package com.buluoxing.famous.api;
//
//
//import com.buluoxing.famous.bean.SysConf;
//import com.buluoxing.famous.bean.UserLogin;
//
//import java.util.HashMap;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.Field;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.GET;
//import retrofit2.http.Header;
//import retrofit2.http.Headers;
//import retrofit2.http.POST;
//import rx.Observable;
//
///**
// * Created by Administrator on 2016/7/18 0018.
// *
// * Retrofit 框架  网络请求
// */
//public interface IRetrofitRequest {
//
//    String BASE_URL = "/Home/System/index";
//
//
//
//
//    @POST( "/login" )
//    @FormUrlEncoded
//    Call<UserLogin> login(@Field("action") String action,
//                          @Field("phone") String phone,
//                          @Field("token") String token,
//                          @Field("facility_info") String facility_info,
//                          @Field("password") String password,
//                          @Field("login_type") String login_type,
//                          @Field("facility_token") String facility_token
//                          );
//
//    @POST("/Home/Members/index")
//    Call<UserLogin> getLogin(@Body HashMap<String ,String> body);
//
//
//
//
//}
