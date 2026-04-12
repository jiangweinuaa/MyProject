package com.dsc.spos.thirdpart.xiaoyou;

public class baseEntity {
    private String api_key;//账号信息 下对应的api key
    private String timestamp;//时间戳,格式为yyyy-mm-dd HH:mm:ss,和服务器时间误差 ±5分钟
    private String method;

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
