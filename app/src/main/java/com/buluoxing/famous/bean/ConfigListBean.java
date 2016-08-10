package com.buluoxing.famous.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1 0001.
 *
 * APP 用户默认配置数据
 */
public class ConfigListBean extends BaseBean{


    /**
     * wechat_conf : {"bind":{"app_secret":"935590281695213883135e7118dd3db5","app_id":"wx270f6cb51a79ff08"},"pay":{"app_secret":"e9b8604ff1b43b205654f175c3f62717","app_id":"wx36834b9528fac4d4"}}
     * task_mode_conf : {"other":[{"id":"18","ischeck":"0","title":"朋友圈转发","sort":"10","inlow":"10","is_zf":"0","molow":"200","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fe1f68684.jpg","is_yc":"2"},{"id":"17","ischeck":"0","title":"微博转发","sort":"9","inlow":"10","is_zf":"0","molow":"200","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/57722774ab0fa.jpg","is_yc":"2"},{"id":"20","ischeck":"0","title":"注册","sort":"7","inlow":"0","is_zf":"0","molow":"300","is_hb":"2","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fe4321652.jpg","is_yc":"1"},{"id":"21","ischeck":"0","title":"投票","sort":"6","inlow":"0","is_zf":"0","molow":"50","is_hb":"2","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/577227843e9c1.jpg","is_yc":"1"},{"id":"10","ischeck":"0","title":"其他","sort":"5","inlow":"2","is_zf":"0","molow":"300","is_hb":"2","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5772275fe9dd6.jpg","is_yc":"1"},{"id":"19","ischeck":"0","title":"微信群转发","sort":"3","inlow":"5","is_zf":"0","molow":"100","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fe63d55b9.jpg","is_yc":"1"}],"weibo":[{"id":"8","ischeck":"1","title":"微博转发","sort":"7","inlow":"10","is_zf":"0","molow":"200","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fad3860bd.jpg","is_yc":"2"},{"id":"9","ischeck":"0","title":"微博点赞","sort":"6","inlow":"1","is_zf":"8","molow":"20","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771faffdbc99.jpg","is_yc":"1"},{"id":"7","ischeck":"0","title":"微博评论","sort":"5","inlow":"2","is_zf":"8","molow":"40","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fb805289a.jpg","is_yc":"1"},{"id":"13","ischeck":"0","title":"朋友圈转发","sort":"4","inlow":"2","is_zf":"0","molow":"2","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fc79805aa.jpg","is_yc":"2"},{"id":"16","ischeck":"0","title":"微信群转发","sort":"0","inlow":"5","is_zf":"0","molow":"100","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fca61b5e7.jpg","is_yc":"1"}],"wechat":[{"id":"5","ischeck":"1","title":"朋友圈转发","sort":"10","inlow":"10","is_zf":"0","molow":"200","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771f7fc2bee4.jpg","is_yc":"2"},{"id":"4","ischeck":"0","title":"点赞","sort":"9","inlow":"1","is_zf":"5","molow":"20","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771f875e7edd.jpg","is_yc":"1"},{"id":"6","ischeck":"0","title":"评论","sort":"8","inlow":"2","is_zf":"5","molow":"40","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771f82a94103.jpg","is_yc":"1"},{"id":"12","ischeck":"0","title":"微博转发","sort":"7","inlow":"10","is_zf":"0","molow":"200","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fa8101ebd.jpg","is_yc":"2"},{"id":"3","ischeck":"0","title":"阅读原文","sort":"5","inlow":"5","is_zf":"0","molow":"100","is_hb":"2","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-05-17/573a7cb6c4996.jpg","is_yc":"1"},{"id":"14","ischeck":"0","title":"微信群转发","sort":"2","inlow":"5","is_zf":"0","molow":"100","is_hb":"1","img":"http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771f76fa5560.jpg","is_yc":"1"}]}
     * task_type_conf : [{"id":"1","title":"红豆任务"},{"id":"2","title":"现金任务"}]
     * prompt_conf : {"min_age":"10","default_avatar":"http://192.168.10.152/Uploads/Home/Avatar/default.png","transfer":"部落星将在24小时内转到您的支付宝账号","yq_text":"注册填写|邀请注册码：#|居然可以拿最高100元现金红包哦~","confirm_upload":"请确保图片正确并符合要求，如不符合会被举报3次以上，将禁止接任务7天","task_area_error":"您不符合任务目标人群","hb_jf":"50","min_money_ali":"最低提现支付宝100元","jf_charge":"任务奖励","min_age_alert":"生日将关系到可接任务的类型，请选择正确的生日","version_id":"1.1","hb_charge":"任务奖励，现金任务收20%手续费，任务结束时间结束后两天内未追加任务，系统自动返还现金","min_money_wx":"最低提现金额1元","version_url":"http://buluoxing.com/Downloads/buluoxing.apk","hb_charge_percent":"20","register":"注册送现金","share_img":"http://192.168.10.152/Uploads/Home/Icon/1.png","yq_code_desc":"天天赚钱，人人网红，自助推广传播APP","wait_seconds":"10","share_short_img":"/Home/Icon/1.png"}
     */

    private ResultBean result;

    public static ConfigListBean objectFromData(String str) {

        return new Gson().fromJson(str, ConfigListBean.class);
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * bind : {"app_secret":"935590281695213883135e7118dd3db5","app_id":"wx270f6cb51a79ff08"}
         * pay : {"app_secret":"e9b8604ff1b43b205654f175c3f62717","app_id":"wx36834b9528fac4d4"}
         */

        private WechatConfBean wechat_conf;
        private TaskModeConfBean task_mode_conf;
        /**
         * min_age : 10
         * default_avatar : http://192.168.10.152/Uploads/Home/Avatar/default.png
         * transfer : 部落星将在24小时内转到您的支付宝账号
         * yq_text : 注册填写|邀请注册码：#|居然可以拿最高100元现金红包哦~
         * confirm_upload : 请确保图片正确并符合要求，如不符合会被举报3次以上，将禁止接任务7天
         * task_area_error : 您不符合任务目标人群
         * hb_jf : 50
         * min_money_ali : 最低提现支付宝100元
         * jf_charge : 任务奖励
         * min_age_alert : 生日将关系到可接任务的类型，请选择正确的生日
         * version_id : 1.1
         * hb_charge : 任务奖励，现金任务收20%手续费，任务结束时间结束后两天内未追加任务，系统自动返还现金
         * min_money_wx : 最低提现金额1元
         * version_url : http://buluoxing.com/Downloads/buluoxing.apk
         * hb_charge_percent : 20
         * register : 注册送现金
         * share_img : http://192.168.10.152/Uploads/Home/Icon/1.png
         * yq_code_desc : 天天赚钱，人人网红，自助推广传播APP
         * wait_seconds : 10
         * share_short_img : /Home/Icon/1.png
         */

        private PromptConfBean prompt_conf;
        /**
         * id : 1
         * title : 红豆任务
         */

        private List<TaskTypeConfBean> task_type_conf;

        public static ResultBean objectFromData(String str) {

            return new Gson().fromJson(str, ResultBean.class);
        }

        public WechatConfBean getWechat_conf() {
            return wechat_conf;
        }

        public void setWechat_conf(WechatConfBean wechat_conf) {
            this.wechat_conf = wechat_conf;
        }

        public TaskModeConfBean getTask_mode_conf() {
            return task_mode_conf;
        }

        public void setTask_mode_conf(TaskModeConfBean task_mode_conf) {
            this.task_mode_conf = task_mode_conf;
        }

        public PromptConfBean getPrompt_conf() {
            return prompt_conf;
        }

        public void setPrompt_conf(PromptConfBean prompt_conf) {
            this.prompt_conf = prompt_conf;
        }

        public List<TaskTypeConfBean> getTask_type_conf() {
            return task_type_conf;
        }

        public void setTask_type_conf(List<TaskTypeConfBean> task_type_conf) {
            this.task_type_conf = task_type_conf;
        }

        public static class WechatConfBean {
            /**
             * app_secret : 935590281695213883135e7118dd3db5
             * app_id : wx270f6cb51a79ff08
             */

            private BindBean bind;
            /**
             * app_secret : e9b8604ff1b43b205654f175c3f62717
             * app_id : wx36834b9528fac4d4
             */

            private PayBean pay;

            public static WechatConfBean objectFromData(String str) {

                return new Gson().fromJson(str, WechatConfBean.class);
            }

            public BindBean getBind() {
                return bind;
            }

            public void setBind(BindBean bind) {
                this.bind = bind;
            }

            public PayBean getPay() {
                return pay;
            }

            public void setPay(PayBean pay) {
                this.pay = pay;
            }

            public static class BindBean {
                private String app_secret;
                private String app_id;

                public static BindBean objectFromData(String str) {

                    return new Gson().fromJson(str, BindBean.class);
                }

                public String getApp_secret() {
                    return app_secret;
                }

                public void setApp_secret(String app_secret) {
                    this.app_secret = app_secret;
                }

                public String getApp_id() {
                    return app_id;
                }

                public void setApp_id(String app_id) {
                    this.app_id = app_id;
                }
            }

            public static class PayBean {
                private String app_secret;
                private String app_id;

                public static PayBean objectFromData(String str) {

                    return new Gson().fromJson(str, PayBean.class);
                }

                public String getApp_secret() {
                    return app_secret;
                }

                public void setApp_secret(String app_secret) {
                    this.app_secret = app_secret;
                }

                public String getApp_id() {
                    return app_id;
                }

                public void setApp_id(String app_id) {
                    this.app_id = app_id;
                }
            }
        }

        public static class TaskModeConfBean {
            /**
             * id : 18
             * ischeck : 0
             * title : 朋友圈转发
             * sort : 10
             * inlow : 10
             * is_zf : 0
             * molow : 200
             * is_hb : 1
             * img : http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fe1f68684.jpg
             * is_yc : 2
             */

            private List<OtherBean> other;
            /**
             * id : 8
             * ischeck : 1
             * title : 微博转发
             * sort : 7
             * inlow : 10
             * is_zf : 0
             * molow : 200
             * is_hb : 1
             * img : http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771fad3860bd.jpg
             * is_yc : 2
             */

            private List<WeiboBean> weibo;
            /**
             * id : 5
             * ischeck : 1
             * title : 朋友圈转发
             * sort : 10
             * inlow : 10
             * is_zf : 0
             * molow : 200
             * is_hb : 1
             * img : http://192.168.10.152/Uploads/Admin/taskType/2016-06-28/5771f7fc2bee4.jpg
             * is_yc : 2
             */

            private List<WechatBean> wechat;

            public static TaskModeConfBean objectFromData(String str) {

                return new Gson().fromJson(str, TaskModeConfBean.class);
            }

            public List<OtherBean> getOther() {
                return other;
            }

            public void setOther(List<OtherBean> other) {
                this.other = other;
            }

            public List<WeiboBean> getWeibo() {
                return weibo;
            }

            public void setWeibo(List<WeiboBean> weibo) {
                this.weibo = weibo;
            }

            public List<WechatBean> getWechat() {
                return wechat;
            }

            public void setWechat(List<WechatBean> wechat) {
                this.wechat = wechat;
            }

            public static class OtherBean {
                private String id;
                private String ischeck;
                private String title;
                private String sort;
                private String inlow;
                private String is_zf;
                private String molow;
                private String is_hb;
                private String img;
                private String is_yc;

                public static OtherBean objectFromData(String str) {

                    return new Gson().fromJson(str, OtherBean.class);
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getIscheck() {
                    return ischeck;
                }

                public void setIscheck(String ischeck) {
                    this.ischeck = ischeck;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getSort() {
                    return sort;
                }

                public void setSort(String sort) {
                    this.sort = sort;
                }

                public String getInlow() {
                    return inlow;
                }

                public void setInlow(String inlow) {
                    this.inlow = inlow;
                }

                public String getIs_zf() {
                    return is_zf;
                }

                public void setIs_zf(String is_zf) {
                    this.is_zf = is_zf;
                }

                public String getMolow() {
                    return molow;
                }

                public void setMolow(String molow) {
                    this.molow = molow;
                }

                public String getIs_hb() {
                    return is_hb;
                }

                public void setIs_hb(String is_hb) {
                    this.is_hb = is_hb;
                }

                public String getImg() {
                    return img;
                }

                public void setImg(String img) {
                    this.img = img;
                }

                public String getIs_yc() {
                    return is_yc;
                }

                public void setIs_yc(String is_yc) {
                    this.is_yc = is_yc;
                }
            }

            public static class WeiboBean {
                private String id;
                private String ischeck;
                private String title;
                private String sort;
                private String inlow;
                private String is_zf;
                private String molow;
                private String is_hb;
                private String img;
                private String is_yc;

                public static WeiboBean objectFromData(String str) {

                    return new Gson().fromJson(str, WeiboBean.class);
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getIscheck() {
                    return ischeck;
                }

                public void setIscheck(String ischeck) {
                    this.ischeck = ischeck;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getSort() {
                    return sort;
                }

                public void setSort(String sort) {
                    this.sort = sort;
                }

                public String getInlow() {
                    return inlow;
                }

                public void setInlow(String inlow) {
                    this.inlow = inlow;
                }

                public String getIs_zf() {
                    return is_zf;
                }

                public void setIs_zf(String is_zf) {
                    this.is_zf = is_zf;
                }

                public String getMolow() {
                    return molow;
                }

                public void setMolow(String molow) {
                    this.molow = molow;
                }

                public String getIs_hb() {
                    return is_hb;
                }

                public void setIs_hb(String is_hb) {
                    this.is_hb = is_hb;
                }

                public String getImg() {
                    return img;
                }

                public void setImg(String img) {
                    this.img = img;
                }

                public String getIs_yc() {
                    return is_yc;
                }

                public void setIs_yc(String is_yc) {
                    this.is_yc = is_yc;
                }
            }

            public static class WechatBean {
                private String id;
                private String ischeck;
                private String title;
                private String sort;
                private String inlow;
                private String is_zf;
                private String molow;
                private String is_hb;
                private String img;
                private String is_yc;

                public static WechatBean objectFromData(String str) {

                    return new Gson().fromJson(str, WechatBean.class);
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getIscheck() {
                    return ischeck;
                }

                public void setIscheck(String ischeck) {
                    this.ischeck = ischeck;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getSort() {
                    return sort;
                }

                public void setSort(String sort) {
                    this.sort = sort;
                }

                public String getInlow() {
                    return inlow;
                }

                public void setInlow(String inlow) {
                    this.inlow = inlow;
                }

                public String getIs_zf() {
                    return is_zf;
                }

                public void setIs_zf(String is_zf) {
                    this.is_zf = is_zf;
                }

                public String getMolow() {
                    return molow;
                }

                public void setMolow(String molow) {
                    this.molow = molow;
                }

                public String getIs_hb() {
                    return is_hb;
                }

                public void setIs_hb(String is_hb) {
                    this.is_hb = is_hb;
                }

                public String getImg() {
                    return img;
                }

                public void setImg(String img) {
                    this.img = img;
                }

                public String getIs_yc() {
                    return is_yc;
                }

                public void setIs_yc(String is_yc) {
                    this.is_yc = is_yc;
                }
            }
        }

        public static class PromptConfBean {
            private String min_age;
            private String default_avatar;
            private String transfer;
            private String yq_text;
            private String confirm_upload;
            private String task_area_error;
            private String hb_jf;
            private String min_money_ali;
            private String jf_charge;
            private String min_age_alert;
            private String version_id;
            private String hb_charge;
            private String min_money_wx;
            private String version_url;
            private String hb_charge_percent;
            private String register;
            private String share_img;
            private String yq_code_desc;
            private String wait_seconds;
            private String share_short_img;

            public static PromptConfBean objectFromData(String str) {

                return new Gson().fromJson(str, PromptConfBean.class);
            }

            public String getMin_age() {
                return min_age;
            }

            public void setMin_age(String min_age) {
                this.min_age = min_age;
            }

            public String getDefault_avatar() {
                return default_avatar;
            }

            public void setDefault_avatar(String default_avatar) {
                this.default_avatar = default_avatar;
            }

            public String getTransfer() {
                return transfer;
            }

            public void setTransfer(String transfer) {
                this.transfer = transfer;
            }

            public String getYq_text() {
                return yq_text;
            }

            public void setYq_text(String yq_text) {
                this.yq_text = yq_text;
            }

            public String getConfirm_upload() {
                return confirm_upload;
            }

            public void setConfirm_upload(String confirm_upload) {
                this.confirm_upload = confirm_upload;
            }

            public String getTask_area_error() {
                return task_area_error;
            }

            public void setTask_area_error(String task_area_error) {
                this.task_area_error = task_area_error;
            }

            public String getHb_jf() {
                return hb_jf;
            }

            public void setHb_jf(String hb_jf) {
                this.hb_jf = hb_jf;
            }

            public String getMin_money_ali() {
                return min_money_ali;
            }

            public void setMin_money_ali(String min_money_ali) {
                this.min_money_ali = min_money_ali;
            }

            public String getJf_charge() {
                return jf_charge;
            }

            public void setJf_charge(String jf_charge) {
                this.jf_charge = jf_charge;
            }

            public String getMin_age_alert() {
                return min_age_alert;
            }

            public void setMin_age_alert(String min_age_alert) {
                this.min_age_alert = min_age_alert;
            }

            public String getVersion_id() {
                return version_id;
            }

            public void setVersion_id(String version_id) {
                this.version_id = version_id;
            }

            public String getHb_charge() {
                return hb_charge;
            }

            public void setHb_charge(String hb_charge) {
                this.hb_charge = hb_charge;
            }

            public String getMin_money_wx() {
                return min_money_wx;
            }

            public void setMin_money_wx(String min_money_wx) {
                this.min_money_wx = min_money_wx;
            }

            public String getVersion_url() {
                return version_url;
            }

            public void setVersion_url(String version_url) {
                this.version_url = version_url;
            }

            public String getHb_charge_percent() {
                return hb_charge_percent;
            }

            public void setHb_charge_percent(String hb_charge_percent) {
                this.hb_charge_percent = hb_charge_percent;
            }

            public String getRegister() {
                return register;
            }

            public void setRegister(String register) {
                this.register = register;
            }

            public String getShare_img() {
                return share_img;
            }

            public void setShare_img(String share_img) {
                this.share_img = share_img;
            }

            public String getYq_code_desc() {
                return yq_code_desc;
            }

            public void setYq_code_desc(String yq_code_desc) {
                this.yq_code_desc = yq_code_desc;
            }

            public String getWait_seconds() {
                return wait_seconds;
            }

            public void setWait_seconds(String wait_seconds) {
                this.wait_seconds = wait_seconds;
            }

            public String getShare_short_img() {
                return share_short_img;
            }

            public void setShare_short_img(String share_short_img) {
                this.share_short_img = share_short_img;
            }
        }

        public static class TaskTypeConfBean {
            private String id;
            private String title;

            public static TaskTypeConfBean objectFromData(String str) {

                return new Gson().fromJson(str, TaskTypeConfBean.class);
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
