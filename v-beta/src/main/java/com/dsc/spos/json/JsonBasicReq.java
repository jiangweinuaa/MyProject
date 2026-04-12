package com.dsc.spos.json;

import java.beans.Transient;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.model.ApiUser;

/**
 * 基礎 JSON 格式
 */
public class JsonBasicReq extends JsonBasic {
	
	private String serviceId;    //服務名稱
	private String token;        //訪問令牌
	private int pageNumber;      //頁碼
	private int pageSize;        //每頁筆數
	//private String user;  //服務使用者 (會從 tokne 反推回來)
	private String oriJson; //傳入的原始 Json
	
	private String apiUserCode;//3.0   api接入帐号，鼎捷提供
	private String requestId;//3.0 请求的唯一标识，调用接口时重新生成
	private String timestamp;//3.0 请求时间戳，到毫秒： YYYYMMDDHHMMSSSSS
	private String version;//3.0 用，版本号
    /**
     * plantType枚举值【各SA查漏补缺】：
     *
     * etailStore 移动门店 、
     *
     * WindowsPos 、AndroidPos、
     *
     * AppDish 掌上云POS、
     *
     * WechatScan 微信扫码点餐、
     *
     * AliScan 支付宝扫码点餐、
     *
     * MiniMall 小程序商城、
     *
     * WechatMall 微信手机商城、
     *
     * BakKds 生产看板、
     *
     * KitchKds 后厨看板
     *
     * PayProgram支付小程序
     *
     * nrc 云中台
     */
    private String plantType;//3.0 用，产品线类型

	
	private String sign;//3.0   加密后的签名字符串
	
	private ApiUser apiUser;
	private String modularNo="";
	
	public String getModularNo() {
		return modularNo;
	}
	public void setModularNo(String modularNo) {
		this.modularNo = modularNo;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Transient
	public String getOriJson() {
		return oriJson;
	}
	public void setOriJson(String oriJson) {
		this.oriJson = oriJson;
	}
	public String getApiUserCode() {
		return apiUserCode;
	}
	public String getSign() {
		return sign;
	}
	public String getRequestId() {
		return requestId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public String getVersion() {
		return version;
	}
	public void setApiUserCode(String apiUserCode) {
		this.apiUserCode = apiUserCode;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public ApiUser getApiUser() {
		return apiUser;
	}
	public void setApiUser(ApiUser apiUser) {
		this.apiUser = apiUser;
	}

    public String getPlantType()
    {
        return plantType;
    }

    public void setPlantType(String plantType)
    {
        this.plantType = plantType;
    }
}
