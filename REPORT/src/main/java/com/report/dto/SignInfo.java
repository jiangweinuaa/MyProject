package com.report.dto;

import java.io.Serializable;

/**
 * 签名信息
 */
public class SignInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 密钥标识
     */
    private String key;

    /**
     * 签名值
     */
    private String sign;

    /**
     * Token
     */
    private String token;

    public SignInfo() {
    }

    public SignInfo(String key, String sign) {
        this.key = key;
        this.sign = sign;
    }

    public SignInfo(String key, String sign, String token) {
        this.key = key;
        this.sign = sign;
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SignInfo{" +
                "key='" + key + '\'' +
                ", sign='" + sign + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
