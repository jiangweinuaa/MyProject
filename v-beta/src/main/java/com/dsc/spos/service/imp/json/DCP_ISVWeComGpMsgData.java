package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgDataReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgDataRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgDataRes.Datas;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.GetGroupMsgSendResult;
import com.dsc.spos.thirdpart.wecom.entity.GetGroupMsgTask;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComGpMsgData
 * 服务说明：群发消息结果查询
 * @author jinzma
 * @since  2024-03-04
 */
public class DCP_ISVWeComGpMsgData extends SPosAdvanceService<DCP_ISVWeComGpMsgDataReq, DCP_ISVWeComGpMsgDataRes> {

    @Override
    protected void processDUID(DCP_ISVWeComGpMsgDataReq req, DCP_ISVWeComGpMsgDataRes res) throws Exception {

        Datas datas = res.new Datas();
        int sendUser = 0;              //已发送的员工数
        int sendExternalUser = 0;      //已发送的客户数
        int sendChat = 0; 	           //已发送的客户群数
        int notSendUser = 0; 	       //未发送的员工数
        int notSendExternalUser = 0;   //未发送的客户数
        int notSendChat = 0; 	       //未发送的客户群数

        try{
            String eId = req.geteId();
            String gpMsgId = req.getRequest().getGpMsgId();
            //资料检查
            {
                String sql = "select status from dcp_isvwecom_gpmsg where eid='"+eId+"' and gpmsgid='"+gpMsgId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+" 不存在 ");
                }
            }

            //删除 DCP_ISVWECOM_GPMSG_ID_USER 重新获取最新资料
            {
                DelBean db = new DelBean("DCP_ISVWECOM_GPMSG_ID_USER");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("GPMSGID", new DataValue(gpMsgId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
            }

            String sql = " select msgid from dcp_isvwecom_gpmsg_id where eid='"+eId+"' and gpmsgid='"+gpMsgId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+" 调企微接口异常 "); //这个表没有资料说明之前调用有异常，
            }

            //调企微
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            for (Map<String, Object> oneQData : getQData){
                String msgId = oneQData.get("MSGID").toString();
                GetGroupMsgTask getGroupMsgTask = dcpWeComUtils.get_groupmsg_task(dao,msgId);

                if (getGroupMsgTask==null){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+" 调企微接口异常 ");
                }

                List<GetGroupMsgTask.Task> task_list = getGroupMsgTask.getTask_list();
                if (CollectionUtil.isNotEmpty(task_list)) {
                    String[] columns = {"EID", "GPMSGID", "MSGID", "USERID", "STATUS", "SENDTIME"};

                    for (GetGroupMsgTask.Task task:task_list) {

                        //新增 DCP_ISVWECOM_GPMSG_ID_USER
                        InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ID_USER", columns);
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(gpMsgId, Types.VARCHAR),
                                new DataValue(msgId, Types.VARCHAR),
                                new DataValue(task.getUserid(), Types.VARCHAR),
                                new DataValue(task.getStatus(), Types.VARCHAR),
                                new DataValue(PosPub.timestampToDate(task.getSend_time()), Types.DATE),
                        };
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                    }
                }
            }

            // 先保存DCP_ISVWECOM_GPMSG_ID_USER ，后续再调用企微接口统计结果
            this.doExecuteDataToDB();

            //统计结果
            List<String> sendUserList = new ArrayList<>();
            List<String> sendExternalUserList = new ArrayList<>();
            List<String> sendChatList = new ArrayList<>();
            List<String> notSendUserList = new ArrayList<>();
            List<String> notSendExternalUserList = new ArrayList<>();
            List<String> notSendChatList = new ArrayList<>();

                    sql = " select msgid,userid,status from dcp_isvwecom_gpmsg_id_user where eid='"+eId+"' and gpmsgid='"+gpMsgId+"' order by msgid,userid ";
            List<Map<String, Object>> getUserQData = this.doQueryData(sql, null);
            if (CollectionUtil.isNotEmpty(getUserQData)){
                for (Map<String, Object>userQData:getUserQData) {
                    String msgId = userQData.get("MSGID").toString();
                    String userId = userQData.get("USERID").toString();
                    GetGroupMsgSendResult getGroupMsgSendResult = dcpWeComUtils.get_groupmsg_send_result(dao,msgId,userId);
                    if (getGroupMsgSendResult!=null){
                        List<GetGroupMsgSendResult.Send> send_list = getGroupMsgSendResult.getSend_list();
                        if (CollectionUtil.isNotEmpty(send_list)){

                            for (GetGroupMsgSendResult.Send send:send_list){
                                String external_userid = send.getExternal_userid(); //外部联系人userid，群发消息到企业的客户群不返回该字段
                                String chat_id = send.getChat_id();                 //外部客户群id，群发消息到客户不返回该字段
                                String userid = send.getUserid();                   //企业服务人员的userid
                                String status = send.getStatus();   //发送状态：0-未发送 1-已发送 2-因客户不是好友导致发送失败 3-因客户已经收到其他群发消息导致发送失败

                                //已发送
                                if ("1".equals(status)){
                                    if (!Check.Null(external_userid)){
                                        sendExternalUserList.add(external_userid);
                                    }
                                    if (!Check.Null(chat_id)){
                                        sendChatList.add(chat_id);
                                    }
                                    if (!Check.Null(userid)){
                                        sendUserList.add(userId);
                                    }

                                }else {

                                    if (!Check.Null(external_userid)){
                                        notSendExternalUserList.add(external_userid);
                                    }
                                    if (!Check.Null(chat_id)){
                                        notSendChatList.add(chat_id);
                                    }
                                    if (!Check.Null(userid)){
                                        notSendUserList.add(userId);
                                    }

                                }

                            }

                        }
                    }
                }

                sendUserList = sendUserList.stream().distinct().collect(Collectors.toList());
                sendExternalUserList = sendExternalUserList.stream().distinct().collect(Collectors.toList());
                sendChatList = sendChatList.stream().distinct().collect(Collectors.toList());

                notSendUserList = notSendUserList.stream().distinct().collect(Collectors.toList());
                notSendExternalUserList = notSendExternalUserList.stream().distinct().collect(Collectors.toList());
                notSendChatList = notSendChatList.stream().distinct().collect(Collectors.toList());

                sendUser = sendUserList.size();
                sendExternalUser = sendExternalUserList.size();
                sendChat = sendChatList.size();

                notSendUser = notSendUserList.size();
                notSendExternalUser = notSendExternalUserList.size();
                notSendChat = notSendChatList.size();

            }




            datas.setSendUser(String.valueOf(sendUser));
            datas.setSendExternalUser(String.valueOf(sendExternalUser));
            datas.setSendChat(String.valueOf(sendChat));
            datas.setNotSendUser(String.valueOf(notSendUser));
            datas.setNotSendExternalUser(String.valueOf(notSendExternalUser));
            datas.setNotSendChat(String.valueOf(notSendChat));


            res.setDatas(datas);

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }



    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGpMsgDataReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGpMsgDataReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGpMsgDataReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpMsgDataReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getGpMsgId())){
                errMsg.append("gpMsgId不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComGpMsgDataReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpMsgDataReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpMsgDataRes getResponseType() {
        return new DCP_ISVWeComGpMsgDataRes();
    }
}
