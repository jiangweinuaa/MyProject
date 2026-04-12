package com.dsc.spos.waimai.entity;

import java.io.Serializable;

public class orderGoodsItemMessage implements Serializable
{
    private String msgType;

    private String msgName;

    private String message;

    public void setMsgType(String msgType){
        this.msgType = msgType;
    }
    public String getMsgType(){
        return this.msgType;
    }
    public void setMsgName(String msgName){
        this.msgName = msgName;
    }
    public String getMsgName(){
        return this.msgName;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }

}
