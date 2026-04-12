package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGroupChatSyncReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGroupChatSyncRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.GroupChatDetail;
import com.dsc.spos.thirdpart.wecom.entity.GroupChatDetail.Member;
import com.dsc.spos.thirdpart.wecom.entity.GroupChatDetail.Admin;
import com.dsc.spos.thirdpart.wecom.entity.GroupChatList;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
/**
 * 服务函数：DCP_ISVWeComGroupChatSync
 * 服务说明：企微客户群同步
 * @author jinzma
 * @since  2024-01-15
 */
public class DCP_ISVWeComGroupChatSync extends SPosAdvanceService<DCP_ISVWeComGroupChatSyncReq, DCP_ISVWeComGroupChatSyncRes> {
    @Override
    protected void processDUID(DCP_ISVWeComGroupChatSyncReq req, DCP_ISVWeComGroupChatSyncRes res) throws Exception {
        try{
            String eId = req.geteId();
            String [] userid_list = new String[0];                //前端目前是未给值，只能查所有群
            String chat_list = req.getRequest().getChatId();      //前端可以给值，但是企微详情里面不返回status，暂时不用

            //调企微（获取客户群列表）     https://developer.work.weixin.qq.com/document/path/96337
            //带离职员工查询才会返回 离职的群 （离职待继承、离职继承中、离职继承完成）
            //不带员工查询不会返回离职的群 （仅指定范围不清楚是否会返回离职的群，未测）
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            GroupChatList group_chat_list = dcpWeComUtils.getGroupChatList(dao,0,userid_list);
            if (group_chat_list != null){
                for (GroupChatList.Group_chat group_char:group_chat_list.getGroup_chat_list()){
                    String chat_id = group_char.getChat_id();
                    String status = group_char.getStatus();

                    //调企微（获取客户群详情）  https://developer.work.weixin.qq.com/document/path/96338
                    //群主离职未分配的，企微不会返回详情
                    GroupChatDetail groupChatDetail = dcpWeComUtils.getGroupChat(dao,chat_id);
                    if (groupChatDetail !=null && groupChatDetail.getGroup_chat()!=null){
                        GroupChatDetail.Group_chat group_chat = groupChatDetail.getGroup_chat();
                        groupChatDelete(eId,chat_id);              //同步企微的资料，本地全部删除
                        groupChatAdd(eId,status,group_chat);       //插入客户群
                    }
                }
            }


            this.doExecuteDataToDB();


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");



        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGroupChatSyncReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGroupChatSyncReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGroupChatSyncReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGroupChatSyncReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_ISVWeComGroupChatSyncReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGroupChatSyncReq>(){};
    }

    @Override
    protected DCP_ISVWeComGroupChatSyncRes getResponseType() {
        return new DCP_ISVWeComGroupChatSyncRes();
    }

    private void groupChatDelete(String eId,String chat_id){

        //删除 DCP_ISVWECOM_GROUPCHAT
        DelBean db1 = new DelBean("DCP_ISVWECOM_GROUPCHAT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("CHATID", new DataValue(chat_id,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        //删除 DCP_ISVWECOM_GROUPCHAT_DE
        DelBean db2 = new DelBean("DCP_ISVWECOM_GROUPCHAT_DE");
        db2.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db2.addCondition("CHATID", new DataValue(chat_id,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

    }

    private void groupChatAdd(String eId,String status,GroupChatDetail.Group_chat group_chat) {

        String[] columns1 = {"EID","CHATID","NAME","STATUS","OWNER","CHATCREATETIME","NOTICE","MEMBERVERSION"};
        String[] columns2 = {"EID","CHATID","USERID","NAME","GROUPNICKNAME","TYPE","JOINTIME","JOINSCENE","INVITOR","ISADMIN"};

        //新增 DCP_ISVWECOM_GROUPCHAT
        {
            DataValue[] insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(group_chat.getChat_id(), Types.VARCHAR),
                    new DataValue(group_chat.getName()==null ? "":group_chat.getName(), Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(group_chat.getOwner()==null ? "":group_chat.getOwner(), Types.VARCHAR),
                    new DataValue(PosPub.timestampToDate(group_chat.getCreate_time()), Types.DATE),
                    new DataValue(group_chat.getNotice()==null ? "":group_chat.getNotice(), Types.VARCHAR),
                    new DataValue(group_chat.getMember_version()==null ? "":group_chat.getMember_version(), Types.VARCHAR),
            };
            InsBean ib1 = new InsBean("DCP_ISVWECOM_GROUPCHAT", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
        }

        //新增 DCP_ISVWECOM_GROUPCHAT_DE
        {
            if (CollectionUtil.isNotEmpty(group_chat.getMember_list())) {
                List<Admin> admins = group_chat.getAdmin_list();
                for (Member member:group_chat.getMember_list()) {
                    String isAdmin="0";
                    if (admins.stream().anyMatch(a->a.getUserid().equals(member.getUserid()))){
                        isAdmin="1";
                    }

                    String invitor = "";
                    if (member.getInvitor()!=null){
                        invitor = member.getInvitor().getUserid();
                    }

                    DataValue[] insValue2 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(group_chat.getChat_id(), Types.VARCHAR),
                            new DataValue(member.getUserid()==null ? "":member.getUserid(), Types.VARCHAR),
                            new DataValue(member.getName()==null ? "":member.getName(), Types.VARCHAR),
                            new DataValue(member.getGroup_nickname()==null ? "":member.getGroup_nickname(), Types.VARCHAR),
                            new DataValue(member.getType()==null ? "":member.getType(), Types.VARCHAR),
                            new DataValue(PosPub.timestampToDate(member.getJoin_time()), Types.DATE),
                            new DataValue(member.getJoin_scene()==null ?"":member.getJoin_scene(), Types.VARCHAR),
                            new DataValue(invitor, Types.VARCHAR),
                            new DataValue(isAdmin, Types.VARCHAR),
                    };
                    InsBean ib2 = new InsBean("DCP_ISVWECOM_GROUPCHAT_DE", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
        }


    }


}
