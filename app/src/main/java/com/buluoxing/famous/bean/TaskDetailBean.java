package com.buluoxing.famous.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Administrator on 2016/8/5 0005.
 *
 * 任务详情 Bean  （ 任务列表 TaskListBean  - > 任务详情 ）
 */
public class TaskDetailBean {

    /**
     * endtime : 1471240921
     * is_over : 1
     * surplus_num : 75
     * money_surplus : 0
     * inte_surplus : 750
     * system_share : 0
     * photo_array : []
     * finish_id : 3656
     * provincial_city_str :
     * surplus_price : 0
     * integral : 10
     * id : 1010
     * user_surplus : 75
     * isfinish : 2
     * is_getred : 1
     * user_photo : http://192.168.10.152/Uploads/Home/Avatar/10368/2016-07-29/579adee8be7b5.jpg
     * user_id : 10368
     * surplus : 75
     * mode_array : []
     * starttime : 1469772121
     * user_type : 0
     * user_city :
     * link_url : http://mp.weixin.qq.com/s?__biz=MzIxMjQ4ODkwNw==&mid=2247483658&idx=1&sn=4df8590f6d0efbe232bdca7433a4c295&scene=4#wechat_redirect
     * task_require : 转评不要少于5个字
     * wait_start : 1470029847
     * task_type : 1
     * number : 100
     * user_name : 翠西
     * price : 0
     * user_domain :
     * task_title : 暴雨封城,bored在家，我们一起数数身边那些绿茶婊的套路
     * task_pattern_str : 朋友圈转发
     * share_img : http://192.168.10.152/Uploads/Admin/Task/2016-07-29/579af245dd9af.jpg
     * finishdate : 1470029847
     * is_task : 1
     */

    private String endtime;
    private String is_over;
    private String surplus_num;
    private String money_surplus;
    private String inte_surplus;
    private String system_share;
    private String finish_id;
    private String provincial_city_str;
    private String surplus_price;
    private String integral;
    private String id;
    private String user_surplus;
    private String isfinish;
    private String is_getred;
    private String user_photo;
    private String user_id;
    private String surplus;
    private String starttime;
    private String user_type;
    private String user_city;
    private String link_url;
    private String task_require;
    private String wait_start;
    private String task_type;
    private String number;
    private String user_name;
    private String price;
    private String user_domain;
    private String task_title;
    private String task_pattern_str;
    private String share_img;
    private String finishdate;
    private String is_task;                 // 任务： 0 不可做 1可做
    private List<?> photo_array;
    private List<?> mode_array;

    public static TaskDetailBean objectFromData(String str) {

        return new Gson().fromJson(str, TaskDetailBean.class);
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getIs_over() {
        return is_over;
    }

    public void setIs_over(String is_over) {
        this.is_over = is_over;
    }

    public String getSurplus_num() {
        return surplus_num;
    }

    public void setSurplus_num(String surplus_num) {
        this.surplus_num = surplus_num;
    }

    public String getMoney_surplus() {
        return money_surplus;
    }

    public void setMoney_surplus(String money_surplus) {
        this.money_surplus = money_surplus;
    }

    public String getInte_surplus() {
        return inte_surplus;
    }

    public void setInte_surplus(String inte_surplus) {
        this.inte_surplus = inte_surplus;
    }

    public String getSystem_share() {
        return system_share;
    }

    public void setSystem_share(String system_share) {
        this.system_share = system_share;
    }

    public String getFinish_id() {
        return finish_id;
    }

    public void setFinish_id(String finish_id) {
        this.finish_id = finish_id;
    }

    public String getProvincial_city_str() {
        return provincial_city_str;
    }

    public void setProvincial_city_str(String provincial_city_str) {
        this.provincial_city_str = provincial_city_str;
    }

    public String getSurplus_price() {
        return surplus_price;
    }

    public void setSurplus_price(String surplus_price) {
        this.surplus_price = surplus_price;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_surplus() {
        return user_surplus;
    }

    public void setUser_surplus(String user_surplus) {
        this.user_surplus = user_surplus;
    }

    public String getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(String isfinish) {
        this.isfinish = isfinish;
    }

    public String getIs_getred() {
        return is_getred;
    }

    public void setIs_getred(String is_getred) {
        this.is_getred = is_getred;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSurplus() {
        return surplus;
    }

    public void setSurplus(String surplus) {
        this.surplus = surplus;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getTask_require() {
        return task_require;
    }

    public void setTask_require(String task_require) {
        this.task_require = task_require;
    }

    public String getWait_start() {
        return wait_start;
    }

    public void setWait_start(String wait_start) {
        this.wait_start = wait_start;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUser_domain() {
        return user_domain;
    }

    public void setUser_domain(String user_domain) {
        this.user_domain = user_domain;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_pattern_str() {
        return task_pattern_str;
    }

    public void setTask_pattern_str(String task_pattern_str) {
        this.task_pattern_str = task_pattern_str;
    }

    public String getShare_img() {
        return share_img;
    }

    public void setShare_img(String share_img) {
        this.share_img = share_img;
    }

    public String getFinishdate() {
        return finishdate;
    }

    public void setFinishdate(String finishdate) {
        this.finishdate = finishdate;
    }

    public String getIs_task() {
        return is_task;
    }

    public void setIs_task(String is_task) {
        this.is_task = is_task;
    }

    public List<?> getPhoto_array() {
        return photo_array;
    }

    public void setPhoto_array(List<?> photo_array) {
        this.photo_array = photo_array;
    }

    public List<?> getMode_array() {
        return mode_array;
    }

    public void setMode_array(List<?> mode_array) {
        this.mode_array = mode_array;
    }
}
