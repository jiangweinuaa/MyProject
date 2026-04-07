package com.report.dto;

import java.io.Serializable;

/**
 * 登录请求参数
 */
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 企业编号
     */
    private String eid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    public LoginRequest() {
    }

    public LoginRequest(String eid, String username, String password, String captcha) {
        this.eid = eid;
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "eid='" + eid + '\'' +
                ", username='" + username + '\'' +
                ", password='***'" +
                ", captcha='" + captcha + '\'' +
                '}';
    }
}
