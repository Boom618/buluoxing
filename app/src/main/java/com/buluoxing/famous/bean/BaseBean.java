package com.buluoxing.famous.bean;

/**
 * Created by Administrator on 2016/8/1 0001.
 */
public class BaseBean {

    private String message;
    private String status;

    public BaseBean() {
    }

    public BaseBean(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
