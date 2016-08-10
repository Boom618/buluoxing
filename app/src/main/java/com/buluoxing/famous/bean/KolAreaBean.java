package com.buluoxing.famous.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Administrator on 2016/8/4 0004.
 *
 * 网红领域
 */
public class KolAreaBean extends BaseBean{
    /**
     * id : 10000
     * name : 美妆
     */

    private List<ResultBean> result;

    public static KolAreaBean objectFromData(String str) {

        return new Gson().fromJson(str, KolAreaBean.class);
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String id;
        private String name;

        public static ResultBean objectFromData(String str) {

            return new Gson().fromJson(str, ResultBean.class);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
