package com.dsc.spos.thirdpart.youzan.response;

/**
 * 响应有赞消息推送(基础)
 * @author LN 08546
 */
public class YouZanNotifyBasicRes {

	public YouZanNotifyBasicRes() {
		
	}

	/**
	 * 返回接收成功标识 接收到消息后，需要返回{"code":0,"msg":"success"}。
	 * 否则，推送服务将认为该条消息没有推送成功，会开启自动重推。
	 * 注意，超时也会被认为是推送失败。超时时间是5s。
	 */
	
	private int code =-1;//
	private String msg="failure";//
	private String description;//描述
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	

}
