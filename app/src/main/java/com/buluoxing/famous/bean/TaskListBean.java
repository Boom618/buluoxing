package com.buluoxing.famous.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Administrator on 2016/8/2 0002.
 *  任务列表 - > 任务详情 TaskDetailBean
 */
public class TaskListBean  extends BaseBean{


    /**
     * id : 1016
     * user_id : 10274
     * task_title : 厉害哈哈哈:O:O:O
     * link_url : http://www.baidi.com
     * share_img :
     * number : 1
     * task_pattern_str : 点赞,朋友圈转发
     * price : 2.4
     * integral : 0
     * starttime : 1469790023
     * endtime : 1469207864
     * task_type : 2
     * system_share : 0
     * task_require :
     * surplus_price : 0
     * surplus_num : 0
     * provincial_city_str :
     * is_task : 0
     * user_domain :
     * user_photo : http://192.168.10.152/Uploads/Home/Avatar/10274/2016-07-26/57972b61ae3a5.jpg
     * user_name : 帅到拖网速:O
     * user_type : 0
     * user_city :
     * isfinish : 2
     * finishdate : 1470037485
     * finish_id : 3559
     * wait_start : 1470037485
     * photo_array : []
     * mode_array : []
     * money_surplus : 0
     * inte_surplus : 0
     * user_surplus : 0
     * surplus : 0
     * is_getred : 1
     */

    private List<ResultBean> result;

    public static TaskListBean objectFromData(String str) {

        return new Gson().fromJson(str, TaskListBean.class);
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String id;
        private String user_id;
        private String task_title;
        private String link_url;
        private String share_img;
        private String number;
        private String task_pattern_str;
        private String price;
        private String integral;
        private String starttime;
        private String endtime;
        private String task_type;
        private String system_share;
        private String task_require;
        private String surplus_price;
        private String surplus_num;
        private String provincial_city_str;
        private String is_task;             // 0 不可做 1 可做
        private String user_domain;
        private String user_photo;
        private String user_name;
        private String user_type;
        private String user_city;
        private String isfinish;
        private String finishdate;
        private String finish_id;
        private String wait_start;
        private String money_surplus;
        private String inte_surplus;
        private String user_surplus;
        private String surplus;
        private String is_getred;
        private List<?> photo_array;
        private List<?> mode_array;

        public static ResultBean objectFromData(String str) {

            return new Gson().fromJson(str, ResultBean.class);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getTask_title() {
            return task_title;
        }

        public void setTask_title(String task_title) {
            this.task_title = task_title;
        }

        public String getLink_url() {
            return link_url;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }

        public String getShare_img() {
            return share_img;
        }

        public void setShare_img(String share_img) {
            this.share_img = share_img;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getTask_pattern_str() {
            return task_pattern_str;
        }

        public void setTask_pattern_str(String task_pattern_str) {
            this.task_pattern_str = task_pattern_str;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getIntegral() {
            return integral;
        }

        public void setIntegral(String integral) {
            this.integral = integral;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getTask_type() {
            return task_type;
        }

        public void setTask_type(String task_type) {
            this.task_type = task_type;
        }

        public String getSystem_share() {
            return system_share;
        }

        public void setSystem_share(String system_share) {
            this.system_share = system_share;
        }

        public String getTask_require() {
            return task_require;
        }

        public void setTask_require(String task_require) {
            this.task_require = task_require;
        }

        public String getSurplus_price() {
            return surplus_price;
        }

        public void setSurplus_price(String surplus_price) {
            this.surplus_price = surplus_price;
        }

        public String getSurplus_num() {
            return surplus_num;
        }

        public void setSurplus_num(String surplus_num) {
            this.surplus_num = surplus_num;
        }

        public String getProvincial_city_str() {
            return provincial_city_str;
        }

        public void setProvincial_city_str(String provincial_city_str) {
            this.provincial_city_str = provincial_city_str;
        }

        public String getIs_task() {
            return is_task;
        }

        public void setIs_task(String is_task) {
            this.is_task = is_task;
        }

        public String getUser_domain() {
            return user_domain;
        }

        public void setUser_domain(String user_domain) {
            this.user_domain = user_domain;
        }

        public String getUser_photo() {
            return user_photo;
        }

        public void setUser_photo(String user_photo) {
            this.user_photo = user_photo;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
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

        public String getIsfinish() {
            return isfinish;
        }

        public void setIsfinish(String isfinish) {
            this.isfinish = isfinish;
        }

        public String getFinishdate() {
            return finishdate;
        }

        public void setFinishdate(String finishdate) {
            this.finishdate = finishdate;
        }

        public String getFinish_id() {
            return finish_id;
        }

        public void setFinish_id(String finish_id) {
            this.finish_id = finish_id;
        }

        public String getWait_start() {
            return wait_start;
        }

        public void setWait_start(String wait_start) {
            this.wait_start = wait_start;
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

        public String getUser_surplus() {
            return user_surplus;
        }

        public void setUser_surplus(String user_surplus) {
            this.user_surplus = user_surplus;
        }

        public String getSurplus() {
            return surplus;
        }

        public void setSurplus(String surplus) {
            this.surplus = surplus;
        }

        public String getIs_getred() {
            return is_getred;
        }

        public void setIs_getred(String is_getred) {
            this.is_getred = is_getred;
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
}
