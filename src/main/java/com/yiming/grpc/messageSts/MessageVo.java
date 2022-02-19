package com.yiming.grpc.messageSts;

public class MessageVo {

    private String message;

    /**
     *  0 标识待发送 1 代表已发送
     */
    private String sts;

    private String time;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
    }
}
