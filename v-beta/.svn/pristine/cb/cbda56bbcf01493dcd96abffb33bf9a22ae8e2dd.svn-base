package com.dsc.spos.waimai.isv;
import com.dsc.spos.waimai.entity.orderLoadDocType;

import java.util.Map;

public class ISV_WM_WebHookThread implements Runnable {

    String clientNo = "";
    Map<String,Object> map_MTResquest = null;
    String msgType = "";
    String loadDocType = "";
    String elmMessage = "";
    String elmMsgId = "";

    public ISV_WM_WebHookThread()
    {

    }

    public void sendMTMsgToClient(String clientNo,String msgType,Map<String,Object> params)
    {
        try {
            this.clientNo = clientNo;
            this.msgType = msgType;
            this.map_MTResquest = params;
            this.loadDocType = orderLoadDocType.MEITUAN;

        }
        catch(Exception ex)
        {
            //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"Goods:" + ex.toString());
        }

    }

    public void sendELMMsgToClient(String clientNo,String msgType,String msgId,String params)
    {
        try {
            this.clientNo = clientNo;
            this.msgType = msgType;
            this.elmMessage = params;
            this.elmMsgId = msgId;
            this.loadDocType = orderLoadDocType.ELEME;

        }
        catch(Exception ex)
        {
            //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"Goods:" + ex.toString());
        }

    }

    public void run() {
        try
        {
            execute();
        }
        catch(Exception ex)
        {


        }

    }

    public void execute() throws Exception
    {
        if (orderLoadDocType.MEITUAN.equals(loadDocType))
        {
            String res = ISV_WMUtils.sendMTMsgToClient(clientNo,msgType,map_MTResquest);
            if (res==null||res.isEmpty())
            {
                ISV_WMUtils.updateMTMsgSendFlag(map_MTResquest.getOrDefault("requestId","").toString(),"N");
            }
            //Thread.sleep(5000);
            //ISV_HelpTools.writelog_fileName("多线程结束 时间:"+System.currentTimeMillis(),"ssss");
        }
        else if (orderLoadDocType.ELEME.equals(loadDocType))
        {
            String res = ISV_WMUtils.sendELMMsgToClient(clientNo,msgType,elmMessage);
            if (res==null||res.isEmpty())
            {
                ISV_WMUtils.updateELMMsgSendFlag(elmMsgId,"N");
            }
            //Thread.sleep(5000);
            //ISV_HelpTools.writelog_fileName("多线程结束 时间:"+System.currentTimeMillis(),"ssss");
        }
        else
        {

        }
    }

}
