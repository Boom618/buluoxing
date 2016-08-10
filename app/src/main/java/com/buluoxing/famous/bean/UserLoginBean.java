package com.buluoxing.famous.bean;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/20 0020.
 *
 * 用户登录 实体
 */
public class UserLoginBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id : 10227
     * photo : http://www.buluoxing.com/Uploads/Home/Avatar/default.png
     * is_kol : 0
     * is_binding : 0
     * phone : 18516597508
     * sex : 1
     * desc :
     * type : 0
     * grade_id : 1
     * nickname : blx992146
     * birthday : 1994-10-01
     * provincial : 上海
     * city : 上海市
     * integral : 25
     * weibo_name :
     * weibo_url :
     * wechat_id :
     * balance : 0
     * follow : 0
     * regdate : 1468376670
     * modifydate : 1468376670
     * invite_code : 30961447
     * reg_code :
     * source : 1
     * wechat_unionid :
     * domian_name :
     * has_publish : 0
     * domian_id :
     * is_follow : 0
     * follow_num : 0
     * followed_num : 0
     */

    private ResultBean result;


    public static UserLoginBean objectFromData(String str) {

        return new Gson().fromJson(str, UserLoginBean.class);
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }


    public static class ResultBean {
        private String id;
        private String photo;
        private String is_kol;
        private String is_binding;
        private String phone;
        private String sex;
        private String desc;
        private String type;
        private String grade_id;
        private String nickname;
        private String birthday;
        private String provincial;
        private String city;
        private String integral;
        private String weibo_name;
        private String weibo_url;
        private String wechat_id;
        private int balance;
        private String follow;
        private String regdate;
        private String modifydate;
        private String invite_code;
        private String reg_code;
        private String source;
        private String wechat_unionid;
        private String domian_name;
        private String has_publish;
        private String domian_id;
        private String is_follow;
        private String follow_num;
        private String followed_num;

        public static ResultBean objectFromData(String str) {

            return new Gson().fromJson(str, ResultBean.class);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getIs_kol() {
            return is_kol;
        }

        public void setIs_kol(String is_kol) {
            this.is_kol = is_kol;
        }

        public String getIs_binding() {
            return is_binding;
        }

        public void setIs_binding(String is_binding) {
            this.is_binding = is_binding;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
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

        public String getGrade_id() {
            return grade_id;
        }

        public void setGrade_id(String grade_id) {
            this.grade_id = grade_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getProvincial() {
            return provincial;
        }

        public void setProvincial(String provincial) {
            this.provincial = provincial;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getIntegral() {
            return integral;
        }

        public void setIntegral(String integral) {
            this.integral = integral;
        }

        public String getWeibo_name() {
            return weibo_name;
        }

        public void setWeibo_name(String weibo_name) {
            this.weibo_name = weibo_name;
        }

        public String getWeibo_url() {
            return weibo_url;
        }

        public void setWeibo_url(String weibo_url) {
            this.weibo_url = weibo_url;
        }

        public String getWechat_id() {
            return wechat_id;
        }

        public void setWechat_id(String wechat_id) {
            this.wechat_id = wechat_id;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public String getFollow() {
            return follow;
        }

        public void setFollow(String follow) {
            this.follow = follow;
        }

        public String getRegdate() {
            return regdate;
        }

        public void setRegdate(String regdate) {
            this.regdate = regdate;
        }

        public String getModifydate() {
            return modifydate;
        }

        public void setModifydate(String modifydate) {
            this.modifydate = modifydate;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

        public String getReg_code() {
            return reg_code;
        }

        public void setReg_code(String reg_code) {
            this.reg_code = reg_code;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getWechat_unionid() {
            return wechat_unionid;
        }

        public void setWechat_unionid(String wechat_unionid) {
            this.wechat_unionid = wechat_unionid;
        }

        public String getDomian_name() {
            return domian_name;
        }

        public void setDomian_name(String domian_name) {
            this.domian_name = domian_name;
        }

        public String getHas_publish() {
            return has_publish;
        }

        public void setHas_publish(String has_publish) {
            this.has_publish = has_publish;
        }

        public String getDomian_id() {
            return domian_id;
        }

        public void setDomian_id(String domian_id) {
            this.domian_id = domian_id;
        }

        public String getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(String is_follow) {
            this.is_follow = is_follow;
        }

        public String getFollow_num() {
            return follow_num;
        }

        public void setFollow_num(String follow_num) {
            this.follow_num = follow_num;
        }

        public String getFollowed_num() {
            return followed_num;
        }

        public void setFollowed_num(String followed_num) {
            this.followed_num = followed_num;
        }
    }
}
