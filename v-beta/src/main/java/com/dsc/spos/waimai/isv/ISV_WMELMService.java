package com.dsc.spos.waimai.isv;

import com.dsc.spos.dao.*;
import com.dsc.spos.waimai.SWaimaiBasicService;
import org.json.JSONObject;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class ISV_WMELMService extends SWaimaiBasicService {
    String logFileName = "ELMRequestLog";
    @Override
    public String execute(String json) throws Exception {
        if (json == null || json.length() == 0)
        {
            return null;
        }
        String ReqlogFileName = logFileName;
        try
        {
            //ISV_HelpTools.writelog_fileName("【收到饿了么的消息内容】" + json,logFileName);
            JSONObject jsonObject = new JSONObject(json);
            String requestId = jsonObject.optString("requestId");//String 消息的唯一id，用于唯一标记每个消息
            String type = jsonObject.optString("type");//Number 消息类型
            String shopId = jsonObject.optString("shopId");//Number 商户的店铺id
            String timestamp = jsonObject.optString("timestamp");//Number 消息发送的时间戳，每次推送时生成，单位毫秒
            String userId = jsonObject.optString("userId");//Number 授权商户的账号id，商户身份标识
            String clientNo = ISV_WMUtils.getClientNoByUserId(userId);
            if (clientNo != null && !clientNo.isEmpty())
            {
                ReqlogFileName = clientNo+"-"+logFileName;
            }
            ISV_HelpTools.writelog_fileName("【收到饿了么的消息内容】" + json,ReqlogFileName);
            //获取下订单号
            String orderNo = "";
            try
            {
                String message = jsonObject.optString("message");
                JSONObject messageJsonObj = new JSONObject(message);
                orderNo = messageJsonObj.optString("orderId","");
                if (orderNo.isEmpty())
                {
                    orderNo = messageJsonObj.optString("id","");
                }
            }
            catch (Exception e)
            {

            }
            //保存饿了么推送的消息
            saveELMMessage(clientNo,shopId,orderNo,requestId,type,timestamp,json);
            //转发消息给客户端，多线程
            ISV_WM_WebHookService.sendELMMsgToClient(clientNo,type,requestId,json);

        }
        catch (Exception e)
        {
            ISV_HelpTools.writelog_fileName("【收到饿了么的消息内容】处理异常:" + e.getMessage(),logFileName);
        }

        return null;
    }

    @Override
    protected void processDUID(String req, Map<String, Object> res) throws Exception {

    }

    @Override
    protected List<InsBean> prepareInsertData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(String req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(String req) throws Exception {
        return null;
    }

    /**
     * 保存饿了么推送的订单消息
     * @param clientNo 客户编码
     * @param shopId 饿了么店铺id
     * @param orderNo 订单id
     * @param requestId 消息id
     * @param messageType 消息类型
     * @param timestamp 消息时间戳
     * @param originMessage 消息内容
     * @return
     * @throws Exception
     */
    private boolean saveELMMessage(String clientNo,String shopId,String orderNo, String requestId,String messageType,String timestamp,String originMessage) throws Exception
    {
        boolean nRet = false;
        String ReqlogFileName = logFileName;
        if (clientNo != null && !clientNo.isEmpty())
        {
            ReqlogFileName = clientNo+"-"+logFileName;
        }
        if (originMessage==null||originMessage.isEmpty())
        {
            return false;
        }
        try
        {

            String process_status = "Y";//是否推送到客户端(Y是；N否) ，默认Y，推送失败更新N
            String messageTypeStatus = "";

            String[] columns1 =
                    {"ID", "CLIENTNO","SHOPID", "ORDERNO", "MESSAGETYPE", "MESSAGETYPESTATUS", "MESSAGE", "TIMESTAMP",
                            "PROCESS_STATUS"};
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]
                    {
                            new DataValue(requestId, Types.VARCHAR),
                            new DataValue(clientNo, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(messageType, Types.VARCHAR),
                            new DataValue(messageTypeStatus, Types.VARCHAR),
                            new DataValue(originMessage, Types.CLOB),
                            new DataValue(timestamp, Types.VARCHAR),
                            new DataValue(process_status, Types.VARCHAR)
                    };

            InsBean ib1 = new InsBean("ISV_WM_ELM_MESSAGE", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
            this.doExecuteDataToDB();
            nRet = true;
            ISV_HelpTools.writelog_fileName("【饿了么推送消息保存数据库】成功,消息id="+requestId,ReqlogFileName);

        }
        catch (Exception e)
        {
            ISV_HelpTools.writelog_fileName("【饿了么推送消息保存数据库】异常:"+e.getMessage()+",消息id="+requestId,ReqlogFileName);
        }

        return nRet;
    }
}
