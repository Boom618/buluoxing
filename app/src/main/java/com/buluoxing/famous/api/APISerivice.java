//package com.buluoxing.famous.api;
//
//import com.buluoxing.famous.bean.UserLogin;
//import com.util.Common;
//
//import retrofit2.Call;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;
//
///**
// * Created by Administrator on 2016/7/18 0018.
// * 调用后台的接口,架构网络层采用Retroft+Rxjava+gson
// */
//public class APISerivice {
//
//    private static final String TAG = "APISerivice";
//
//    private static boolean isTest = true;  // 是否是测试环境
//
//
//    // 正式服务器
//    public static final String URL_HOST_UP = "http://www.buluoxing.com";
//    // 测式服务器
//    public static final String BASE_URL_TEST = "http://192.168.10.152";
//
//    public static final String BASE_URL = "/Home";
//
//
//    public static final String URL_HOST = isTest?BASE_URL_TEST :URL_HOST_UP;
//
//
//
//    /**
//     * 基础配置
//     * 初始化 Retrofit
//     */
//    private static final Retrofit sRetrofit = new Retrofit.Builder()
//            .baseUrl(URL_HOST + BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //使用RxJava作为回调适配器
//            .build();
//
//    private static final IRetrofitRequest apiManager = sRetrofit.create(IRetrofitRequest.class);
//
//
//
//    public static Call<UserLogin> login(){
//        Call<UserLogin> loginCall = apiManager.login("login","18516597508", "f84d1a76a2e426cdd4595b714f3b3308",
//                "C8815-HUAWEI C8815,4.1.2", "123456", "1", "AtqEn6X-HaZipK5lmx1ZFhuziYEhM6Az36Tx86HdONZb");
//        return loginCall;
//    }
//
//
//
//    public static String get_Token(String userId) {
//        String token = "";
//        if (userId.isEmpty()) {
//            token = Common.signAction("configList");
//            return token;
//        } else {
//            token = Common.signAction("configList", userId);
//            return token;
//        }
//    }
//
//}
