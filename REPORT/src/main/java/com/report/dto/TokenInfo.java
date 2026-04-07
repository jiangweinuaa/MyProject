package com.report.dto;

/**
 * Token 信息类
 */
public class TokenInfo {
    private String EID;
    private String OPNO;
    private String userId;
    private String username;
    private String mobile;
    private String ip;
    
    public TokenInfo() {
        // 默认 EID 为 99
        this.EID = "99";
    }
    
    public TokenInfo(String EID) {
        this.EID = EID != null ? EID : "99";
    }
    
    public TokenInfo(String EID, String OPNO) {
        this.EID = EID != null ? EID : "99";
        // 修复：当 OPNO 为空字符串时也使用默认值
        this.OPNO = (OPNO != null && !OPNO.trim().isEmpty()) ? OPNO : "admin";
    }
    
    public TokenInfo(String EID, String OPNO, String ip) {
        this.EID = EID != null ? EID : "99";
        // 修复：当 OPNO 为空字符串时也使用默认值
        this.OPNO = (OPNO != null && !OPNO.trim().isEmpty()) ? OPNO : "admin";
        this.ip = ip;
    }
    
    public String getEID() {
        return EID;
    }
    
    public void setEID(String EID) {
        this.EID = EID != null ? EID : "99";
    }
    
    public String getOPNO() {
        return OPNO;
    }
    
    public void setOPNO(String OPNO) {
        // 修复：当 OPNO 为空字符串时也使用默认值
        this.OPNO = (OPNO != null && !OPNO.trim().isEmpty()) ? OPNO : "admin";
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
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
}
