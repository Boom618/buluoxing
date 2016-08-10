package com.buluoxing.famous.eventmodle;

import com.buluoxing.famous.bean.UserLoginBean;

/**
 * Created by Administrator on 2016/7/22 0022.
 *
 * 用户登录 EventBus
 */
public class LoginEvent extends BaseEvent{

    private UserLoginBean result;

    public LoginEvent(UserLoginBean result) {
        this.result = result;
    }

    public LoginEvent(String status, String message) {
        super(status, message);
    }

    public LoginEvent(String status, String message, UserLoginBean result) {
        super(status, message);
        this.result = result;
    }

    public UserLoginBean getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "result=" + result +
                '}';
    }
}
