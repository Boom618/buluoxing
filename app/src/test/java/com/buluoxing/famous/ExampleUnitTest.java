package com.buluoxing.famous;

import org.junit.Test;

import okhttp3.MediaType;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Test
    public void addition_isCorrect() throws Exception {

    }



    @Test
    public void sys() throws Exception {

//        String token = Common.signAction("login");
//        System.out.println("----------- token :" + token);
//        Retrofit builder = new Retrofit.Builder()
//                .baseUrl("http://192.168.10.152")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        IRetrofitRequest request = builder.create(IRetrofitRequest.class);
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("action", "login");
//        map.put("phone", "18516597508");
//        map.put("token", "f84d1a76a2e426cdd4595b714f3b3308");
//        map.put("facility_info", "C8815-HUAWEI C8815,4.1.2");
//        map.put("password", "123456");
//        map.put("login_type", "1");
//        map.put("facility_token", "AtqEn6X-HaZipK5lmx1ZFhuziYEhM6Az36Tx86HdONZb");
//
//        Call<UserLogin> call = request.getLogin(map);
//        call.enqueue(new Callback<UserLogin>() {
//            @Override
//            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
//                System.out.println("------onResponse :" + response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<UserLogin> call, Throwable t) {
//                System.out.println("------onFailure :");
//            }
//        });

        System.out.println("------sys()-------- :");


    }

    @Test
    public void Test() throws Exception{

        String format = String.format("任务总额不低于%.2f元", 2.0);
        System.out.println("------format :" + format);


    }
}