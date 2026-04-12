package com.dsc.spos.waimai.isv;

import com.dsc.spos.dao.*;
import com.dsc.spos.utils.StringUtil;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.SWaimaiBasicService;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dsc.spos.waimai.isv.ISV_HelpTools;
import org.json.JSONObject;

public class ISV_WMJBPService extends SWaimaiBasicService {
    String logFileName = "MTRequestLog";
    @Override
    public String execute(String responseStr) throws Exception {
        if (responseStr == null || responseStr.length() == 0) {
            ISV_HelpTools.writelog_waimaiException("美团外卖发送的请求为空！");
            return null;
        }
        String requestId = UUID.randomUUID().toString().replace("-","");
        String timestamp = System.currentTimeMillis()+"";
        String[] MTResquest = responseStr.split("&");//
        if (MTResquest == null || MTResquest.length == 0) {
            ISV_HelpTools.writelog_waimaiException("解析聚宝盆发送的请求格式有误！");
            return null;
        }

        String ReqlogFileName = logFileName;
        Map<String, Object> map_MTResquest = new HashMap<String, Object>();
        map_MTResquest.put("requestId",requestId);
        map_MTResquest.put("timestamp",timestamp);
        String urlDecodeString = "";
        String ePoiId = "";//ERP方门店id 最大长度100  生成规则=客户标识_企业ID_门店编码
        String clientNo = "";//客户唯一标识
        for (String string_mt : MTResquest) {

            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                if ("ePoiId".equals(s1))
                {
                    ePoiId = s2;
                }
                String s2_decode = ISV_HelpTools.getURLDecoderString(s2);
                map_MTResquest.put(s1, s2_decode);
                urlDecodeString +=s1+"="+s2_decode+"&";
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        if (urlDecodeString.endsWith("&"))
        {
            urlDecodeString = urlDecodeString.substring(0,urlDecodeString.length()-1);
        }

        clientNo = ISV_HelpTools.getJBPClientNo(ePoiId);
        if (clientNo != null && !clientNo.isEmpty())
        {
            ReqlogFileName = clientNo+"-"+logFileName;
        }
        ISV_HelpTools.writelog_fileName("【聚宝盆URL转码后2】" +urlDecodeString+",消息id="+requestId,ReqlogFileName);
        saveMTMessage(clientNo,map_MTResquest,urlDecodeString);
        String msgType = "order";
        if (map_MTResquest.containsKey("shippingStatus"))
        {
            msgType = "shippingStatus";//配送有专门的地址
        }
        //ISV_HelpTools.writelog_fileName("开始多线程 时间:"+System.currentTimeMillis(),"ssss");
        ISV_WM_WebHookService.sendMTMsgToClient(clientNo,msgType,map_MTResquest);
        //ISV_HelpTools.writelog_fileName("主线程结束 时间:"+System.currentTimeMillis(),"ssss");
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
     * 保存美团推送的订单消息
     * @param clientNo 客户编码
     * @param messageMap 推送的消息转map
     * @param originMessage 原始推送信息
     * @return
     * @throws Exception
     */
    private boolean saveMTMessage(String clientNo, Map<String, Object> messageMap,String originMessage) throws Exception
    {
        boolean nRet = false;
        String ReqlogFileName = logFileName;
        if (messageMap==null||messageMap.isEmpty())
        {
            return false;
        }
        if (originMessage==null||originMessage.isEmpty())
        {
            return false;
        }
        if (clientNo != null && !clientNo.isEmpty())
        {
            ReqlogFileName = clientNo+"-"+logFileName;
        }
        String requestId = messageMap.get("requestId").toString();
        String timestamp = messageMap.get("timestamp").toString();
        try
        {

            String messageType = "";
            String messageTypeStatus = "";
            String orderNo = "";
            String process_status = "Y";//是否推送到客户端(Y是；N否) ，默认Y，推送失败更新N
            if (messageMap.containsKey("order"))
            {
                messageType = "order";
                String orderString = messageMap.get("order").toString();
                JSONObject jsonobj = new JSONObject(orderString);
                orderNo = jsonobj.optString("orderId","");
                messageTypeStatus = jsonobj.optString("status","");
            }
            else if (messageMap.containsKey("orderCancel"))
            {
                messageType = "orderCancel";
                String orderCancelString = messageMap.get("orderCancel").toString();
                JSONObject jsonobj = new JSONObject(orderCancelString);
                orderNo = jsonobj.optString("orderId","");
                messageTypeStatus = jsonobj.optString("reasonCode","");//原因码
            }
            else if (messageMap.containsKey("orderRefund"))
            {
                messageType = "orderRefund";
                String orderRefundString = messageMap.get("orderRefund").toString();
                JSONObject jsonobj = new JSONObject(orderRefundString);
                orderNo = jsonobj.optString("orderId","");
                messageTypeStatus = jsonobj.optString("notifyType","");//part：商家/用户发起部分退款 agree：商家同意部分退款 reject ：商家拒绝部分退款
            }
            else if (messageMap.containsKey("partOrderRefund"))
            {
                messageType = "partOrderRefund";
                String partOrderRefundString = messageMap.get("partOrderRefund").toString();
                JSONObject jsonobj = new JSONObject(partOrderRefundString);
                orderNo = jsonobj.optString("orderId","");
                messageTypeStatus = jsonobj.optString("notifyType","");//part：商家/用户发起部分退款 agree：商家同意部分退款 reject ：商家拒绝部分退款
            }
            else if (messageMap.containsKey("shippingStatus"))
            {
                messageType = "shippingStatus";
                String shippingStatusString = messageMap.get("shippingStatus").toString();
                JSONObject jsonobj = new JSONObject(shippingStatusString);
                orderNo = jsonobj.optString("orderId","");
                messageTypeStatus = jsonobj.optString("shippingStatus","");//配送状态
            }
            else
            {

            }

            String[] columns1 =
                    {"ID", "CLIENTNO", "ORDERNO", "MESSAGETYPE", "MESSAGETYPESTATUS", "MESSAGE", "TIMESTAMP",
                            "PROCESS_STATUS"};
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]
                    {
                            new DataValue(requestId, Types.VARCHAR),
                            new DataValue(clientNo, Types.VARCHAR),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(messageType, Types.VARCHAR),
                            new DataValue(messageTypeStatus, Types.VARCHAR),
                            new DataValue(originMessage, Types.CLOB),
                            new DataValue(timestamp, Types.VARCHAR),
                            new DataValue(process_status, Types.VARCHAR)
                    };

            InsBean ib1 = new InsBean("ISV_WM_MT_MESSAGE", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
            this.doExecuteDataToDB();
            nRet = true;
            ISV_HelpTools.writelog_fileName("【聚宝盆请求信息保存数据库】成功,消息id="+requestId,ReqlogFileName);

        }
        catch (Exception e)
        {
            ISV_HelpTools.writelog_fileName("【聚宝盆请求信息保存数据库】异常:"+e.getMessage()+",消息id="+requestId,ReqlogFileName);
        }

        return nRet;
    }
}
