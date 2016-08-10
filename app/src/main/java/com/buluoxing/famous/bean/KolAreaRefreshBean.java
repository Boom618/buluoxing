package com.buluoxing.famous.bean;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2016/8/6 0006.
 *
 * 网红领域刷新  （ 显示用户选择的 领域）
 */
public class KolAreaRefreshBean extends BaseBean {

    /**
     * wechat_unionid :
     * domian_id :
     * birthday : 1990-08-05
     * sex : 1
     * phone : 13917123721
     * desc :
     * type : 1
     * weibo_url :
     * integral : 1
     * city : 上海市
     * is_binding : 0
     * id : 10413
     * follow_num : 0
     * balance : 4
     * weibo_name :
     * followed_num : 0
     * regdate : 1470461648
     * money : 4
     * invite_code : 41456604
     * domian_name :
     * reg_code :
     * is_follow : 0
     * modifydate : 1470463424
     * nickname : 嘎嘎嘎嘎滚滚滚
     * wechat_id :
     * photo : http://192.168.10.152/Uploads/Home/Avatar/default.png
     * is_report : 1
     * provincial : 上海
     * domian_str :
     * source : 1
     * is_message : 0
     * grade_id : 1
     * has_publish : 1
     * is_kol : 2
     */

    private ResultBean result;

    public static KolAreaRefreshBean objectFromData(String str) {

        return new Gson().fromJson(str, KolAreaRefreshBean.class);
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String wechat_unionid;
        private String domian_id;
        private String birthday;
        private String sex;
        private String phone;
        private String desc;
        private String type;
        private String weibo_url;
        private String integral;
        private String city;
        private String is_binding;
        private String id;
        private String follow_num;
        private String balance;
        private String weibo_name;
        private String followed_num;
        private String regdate;
        private String money;
        private String invite_code;
        private String domian_name;
        private String reg_code;
        private String is_follow;
        private String modifydate;
        private String nickname;
        private String wechat_id;
        private String photo;
        private String is_report;
        private String provincial;
        private String domian_str;
        private String source;
        private String is_message;
        private String grade_id;
        private String has_publish;
        private String is_kol;

        public static ResultBean objectFromData(String str) {

            return new Gson().fromJson(str, ResultBean.class);
        }

        public String getWechat_unionid() {
            return wechat_unionid;
        }

        public void setWechat_unionid(String wechat_unionid) {
            this.wechat_unionid = wechat_unionid;
        }

        public String getDomian_id() {
            return domian_id;
        }

        public void setDomian_id(String domian_id) {
            this.domian_id = domian_id;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWeibo_url() {
            return weibo_url;
        }

        public void setWeibo_url(String weibo_url) {
            this.weibo_url = weibo_url;
        }

        public String getIntegral() {
            return integral;
        }

        public void setIntegral(String integral) {
            this.integral = integral;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getIs_binding() {
            return is_binding;
        }

        public void setIs_binding(String is_binding) {
            this.is_binding = is_binding;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFollow_num() {
            return follow_num;
        }

        public void setFollow_num(String follow_num) {
            this.follow_num = follow_num;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getWeibo_name() {
            return weibo_name;
        }

        public void setWeibo_name(String weibo_name) {
            this.weibo_name = weibo_name;
        }

        public String getFollowed_num() {
            return followed_num;
        }

        public void setFollowed_num(String followed_num) {
            this.followed_num = followed_num;
        }

        public String getRegdate() {
            return regdate;
        }

        public void setRegdate(String regdate) {
            this.regdate = regdate;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

        public String getDomian_name() {
            return domian_name;
        }

        public void setDomian_name(String domian_name) {
            this.domian_name = domian_name;
        }

        public String getReg_code() {
            return reg_code;
        }

        public void setReg_code(String reg_code) {
            this.reg_code = reg_code;
        }

        public String getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(String is_follow) {
            this.is_follow = is_follow;
        }

        public String getModifydate() {
            return modifydate;
        }

        public void setModifydate(String modifydate) {
            this.modifydate = modifydate;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getWechat_id() {
            return wechat_id;
        }

        public void setWechat_id(String wechat_id) {
            this.wechat_id = wechat_id;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getIs_report() {
            return is_report;
        }

        public void setIs_report(String is_report) {
            this.is_report = is_report;
        }

        public String getProvincial() {
            return provincial;
        }

        public void setProvincial(String provincial) {
            this.provincial = provincial;
        }

        public String getDomian_str() {
            return domian_str;
        }

        public void setDomian_str(String domian_str) {
            this.domian_str = domian_str;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getIs_message() {
            return is_message;
        }

        public void setIs_message(String is_message) {
            this.is_message = is_message;
        }

        public String getGrade_id() {
            return grade_id;
        }

        public void setGrade_id(String grade_id) {
            this.grade_id = grade_id;
        }

        public String getHas_publish() {
            return has_publish;
        }

        public void setHas_publish(String has_publish) {
            this.has_publish = has_publish;
        }

        public String getIs_kol() {
            return is_kol;
        }

        public void setIs_kol(String is_kol) {
            this.is_kol = is_kol;
        }
    }
}
