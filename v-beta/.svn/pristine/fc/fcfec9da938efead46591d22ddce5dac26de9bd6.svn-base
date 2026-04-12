package com.dsc.spos.waimai.isv;

import java.util.Map;

public class ISV_WM_WebHookService {

    public static void sendMTMsgToClient(String clientNo, String msgType, Map<String,Object> params)
    {
        ISV_WM_WebHookThread post = new ISV_WM_WebHookThread();
        post.sendMTMsgToClient(clientNo,msgType,params);
        Thread thread = new Thread(post);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 转发饿了么消息到客户端
     * @param clientNo 客户端编码
     * @param msgType 消息类型
     * @param msgId 消息id
     * @param params 消息内容
     */
    public static void sendELMMsgToClient(String clientNo, String msgType,String msgId, String params)
    {
        ISV_WM_WebHookThread post = new ISV_WM_WebHookThread();
        post.sendELMMsgToClient(clientNo,msgType,msgId,params);
        Thread thread = new Thread(post);
        thread.setDaemon(true);
        thread.start();
    }
}
