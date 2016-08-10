package com.util;

/**
 * Created by Administrator on 2016/7/22 0022.
 * EventBus
 */
public class EventUtils {
    /**
     * 传递String类型的event
     *
     */
    public static class StringEvent{
        private String mMsg;
        public StringEvent(String msg) {
            // TODO Auto-generated constructor stub
            this.mMsg = msg;
        }
        public String getMsg(){
            return mMsg;
        }
    }

    public static class intEvent{
        private int mMsg;
        public intEvent(int msg) {
            // TODO Auto-generated constructor stub
            this.mMsg = msg;
        }
        public int getMsg(){
            return mMsg;
        }
    }

    /**
     * object类型(即传统的所有类型,都可以强转进行传递事件)
     *
     */

    public static class ObjectEvent{
        private Object object;
        public ObjectEvent(Object object) {
            // TODO Auto-generated constructor stub
            this.object = object;
        }
        public Object getMsg(){
            return object;
        }
    }
}
