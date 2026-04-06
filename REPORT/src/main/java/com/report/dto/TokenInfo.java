package com.report.dto;

/**
 * Token 信息类
 */
public class TokenInfo {
    private String EID;
    private String userId;
    private String username;
    private String mobile;
    
    public TokenInfo() {
        // 默认 EID 为 99
        this.EID = "99";
    }
    
    public TokenInfo(String EID) {
        this.EID = EID != null ? EID : "99";
    }
    
    public String getEID() {
        return EID;
    }
    
    public void setEID(String EID) {
        this.EID = EID != null ? EID : "99";
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
