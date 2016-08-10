package com.buluoxing.famous.eventmodle;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class BaseEvent {

    protected String status;            // 状态码
    protected String message;           // 结果描述

    public BaseEvent() {

    }

    public BaseEvent(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseEvent{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
