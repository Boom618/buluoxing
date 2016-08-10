package com.buluoxing.famous.api;

import android.util.Log;

import com.buluoxing.famous.bean.UserLoginBean;
import com.buluoxing.famous.eventmodle.LoginEvent;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class APIOkHttp {

    private static final String TAG = "APIOkHttp";

    private static boolean isTest = true;  // 是否是测试环境


    // 正式服务器
    public static final String URL_HOST_UP = "http://www.buluoxing.com";
    // 测式服务器
    public static final String BASE_URL_TEST = "http://192.168.10.152";


    public static final String URL_HOST = isTest?BASE_URL_TEST :URL_HOST_UP;

    // 登录
    public static void userLogin(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "login");
        params.put("phone", "18516597508");
        params.put("token", "f84d1a76a2e426cdd4595b714f3b3308");
        params.put("facility_info", "C8815-HUAWEI C8815,4.1.2");
        params.put("password", "123456");
        params.put("login_type", "1");
        params.put("facility_token", "AtqEn6X-HaZipK5lmx1ZFhuziYEhM6Az36Tx86HdONZb");

        OkHttpUtils.post()
                .url(URL_HOST + "/Home/Members/index")
                .params(params)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        return response.body().string();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "userLogin onError: " + e.toString());
                        LoginEvent event = new LoginEvent(null);
                        EventBus.getDefault().post(event);
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                        try {
                            System.out.println("------id :" + id);
                            System.out.println("------onResponse :" + response.toString());
                            String string = response.toString();
                            Gson gson = new Gson();
                            String s = gson.toJson(string);
                            System.out.println("------s :" + s);
                            System.out.println("------string :" + string);

                            UserLoginBean bean = gson.fromJson(string,UserLoginBean.class);

                            System.out.println("------bean :" + bean);
                            String message = bean.getMessage();
                            System.out.println("------message :" + message);

                            LoginEvent event = new LoginEvent(bean.getMessage(),bean.getStatus(),bean);
                            EventBus.getDefault().post(event);
                            System.out.println("---------event.getStatus:" + event.getStatus());
                            System.out.println("---------event.getMessage:" + event.getMessage());

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
    }

}
