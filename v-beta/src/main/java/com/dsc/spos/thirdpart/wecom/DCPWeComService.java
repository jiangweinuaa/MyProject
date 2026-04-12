package com.dsc.spos.thirdpart.wecom;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.wecom.entity.ExternaContact;
import com.dsc.spos.thirdpart.wecom.entity.GroupChatDetail;
import com.dsc.spos.thirdpart.wecom.entity.MessageSend;
import com.dsc.spos.thirdpart.wecom.entity.WelcomeMsg;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCPWeComService
 * 服务说明：企业微信客户端通用服务
 * @author jinzma
 * @since  2024-01-25
 */
public class DCPWeComService {

    //同步指定客户详情
    public boolean ExternalContactSync(DsmDAO dao,String eId,String external_userid_in) {
        try {
            //调企微（获取客户详情）
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            ExternaContact externaContact = dcpWeComUtils.getExternalContact(dao, external_userid_in);
            if (externaContact==null){
                return false;
            }
            ExternaContact.External_contact external_contact = externaContact.getExternal_contact();
            if (external_contact==null){
                return false;
            }
            List<ExternaContact.Follow_user> follow_user = externaContact.getFollow_user();
            if (follow_user==null){
                return false;
            }

            List<DataProcessBean> dataProcessBean = new ArrayList<>();

            String externalUserId = external_contact.getExternal_userid();
            //删除客户资料
            {
                //删除 DCP_ISVWECOM_CUSTOM
                DelBean db1 = new DelBean("DCP_ISVWECOM_CUSTOM");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("EXTERNALUSERID", new DataValue(externalUserId, Types.VARCHAR));
                dataProcessBean.add(new DataProcessBean(db1));

                //删除 DCP_ISVWECOM_CUSTOM_TAG
                DelBean db2 = new DelBean("DCP_ISVWECOM_CUSTOM_TAG");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("EXTERNALUSERID", new DataValue(externalUserId, Types.VARCHAR));
                dataProcessBean.add(new DataProcessBean(db2));

                //删除 DCP_ISVWECOM_CUSTOM_MOBILE
                DelBean db3 = new DelBean("DCP_ISVWECOM_CUSTOM_MOBILE");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("EXTERNALUSERID", new DataValue(externalUserId, Types.VARCHAR));
                dataProcessBean.add(new DataProcessBean(db3));

                //删除 DCP_ISVWECOM_CUSTOM_FOLLOW
                DelBean db4 = new DelBean("DCP_ISVWECOM_CUSTOM_FOLLOW");
                db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db4.addCondition("EXTERNALUSERID", new DataValue(externalUserId, Types.VARCHAR));
                dataProcessBean.add(new DataProcessBean(db4));
            }

            //新增插入
            String[] columns1 = {"EID","EXTERNALUSERID","NAME","AVATAR","CUSTOMTYPE","GENDER","POSITION","STATUS"};
            String[] columns2 = {"EID","EXTERNALUSERID","TAGID","USERID"};
            String[] columns3 = {"EID","EXTERNALUSERID","REMARKMOBILE","USERID"};
            String[] columns4 = {"EID","EXTERNALUSERID","USERID","FOLLOWREMARK","FOLLOWDESCRIP","FOLLOWTIME","ADDWAY","OPERUSERID","STATE"};

            //新增 DCP_ISVWECOM_CUSTOM
            {
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(externalUserId, Types.VARCHAR),
                        new DataValue(external_contact.getName()==null?"":external_contact.getName(), Types.VARCHAR),
                        new DataValue(external_contact.getAvatar()==null?"":external_contact.getAvatar(), Types.VARCHAR),
                        new DataValue(external_contact.getType()==null?"":external_contact.getType(), Types.VARCHAR),
                        new DataValue(external_contact.getGender()==null?"":external_contact.getGender(), Types.VARCHAR),
                        new DataValue(external_contact.getPosition()==null?"":external_contact.getPosition(), Types.VARCHAR),
                        new DataValue("100", Types.VARCHAR),     //status 查到一定是100
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CUSTOM", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
            }

            for (ExternaContact.Follow_user followUser:follow_user) {
                String userId = followUser.getUserid();
                //新增 DCP_ISVWECOM_CUSTOM_TAG
                {
                    List<ExternaContact.Tag> tags = followUser.getTags();
                    if (CollectionUtil.isNotEmpty(tags)) {
                        for (ExternaContact.Tag tag : tags) {
                            DataValue[] insValue2 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(externalUserId, Types.VARCHAR),
                                    new DataValue(tag.getTag_id()==null?"":tag.getTag_id(), Types.VARCHAR),
                                    new DataValue(userId, Types.VARCHAR),
                            };
                            InsBean ib2 = new InsBean("DCP_ISVWECOM_CUSTOM_TAG", columns2);
                            ib2.addValues(insValue2);
                            dataProcessBean.add(new DataProcessBean(ib2));
                        }
                    }
                }

                //新增 DCP_ISVWECOM_CUSTOM_MOBILE
                {
                    String[] remark_mobiles = followUser.getRemark_mobiles();
                    if (remark_mobiles != null) {
                        for (String remark_mobile : remark_mobiles) {
                            DataValue[] insValue3 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(externalUserId, Types.VARCHAR),
                                    new DataValue(remark_mobile, Types.VARCHAR),
                                    new DataValue(userId, Types.VARCHAR),
                            };
                            InsBean ib3 = new InsBean("DCP_ISVWECOM_CUSTOM_MOBILE", columns3);
                            ib3.addValues(insValue3);
                            dataProcessBean.add(new DataProcessBean(ib3));
                        }
                    }

                }

                //新增 DCP_ISVWECOM_CUSTOM_FOLLOW
                {
                    DataValue[] insValue4 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(externalUserId, Types.VARCHAR),
                            new DataValue(userId, Types.VARCHAR),
                            new DataValue(followUser.getRemark()==null?"":followUser.getRemark(), Types.VARCHAR),
                            new DataValue(followUser.getDescription()==null?"":followUser.getDescription(), Types.VARCHAR),
                            new DataValue(PosPub.timestampToDate(followUser.getCreatetime()), Types.DATE),
                            new DataValue(followUser.getAdd_way()==null?"":followUser.getAdd_way(), Types.VARCHAR),
                            new DataValue(followUser.getOper_userid()==null?"":followUser.getOper_userid(), Types.VARCHAR),
                            new DataValue(followUser.getState()==null?"":followUser.getState(), Types.VARCHAR),
                    };
                    InsBean ib4 = new InsBean("DCP_ISVWECOM_CUSTOM_FOLLOW", columns4);
                    ib4.addValues(insValue4);
                    dataProcessBean.add(new DataProcessBean(ib4));

                }

            }

            dao.useTransactionProcessData(dataProcessBean);

            return true;

        }catch (Exception e){
            return false;
        }
    }

    //同步指定群
    public boolean ExternalChatSync(DsmDAO dao,String eId,String chatId ){

        try {
            //调企微（获取客户群详情）
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            GroupChatDetail groupChatDetail = dcpWeComUtils.getGroupChat(dao,chatId);
            if (groupChatDetail !=null && groupChatDetail.getGroup_chat()!=null){
                GroupChatDetail.Group_chat group_chat = groupChatDetail.getGroup_chat();
                String chat_id = group_chat.getChat_id();
                //同步企微的资料，本地全部删除
                List<DataProcessBean> dataProcessBean = new ArrayList<>();

                //修改 DCP_ISVWECOM_GROUPCHAT
                DelBean db1 = new DelBean("DCP_ISVWECOM_GROUPCHAT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("CHATID", new DataValue(chat_id,Types.VARCHAR));
                dataProcessBean.add(new DataProcessBean(db1));

                //删除 DCP_ISVWECOM_GROUPCHAT_DE
                DelBean db2 = new DelBean("DCP_ISVWECOM_GROUPCHAT_DE");
                db2.addCondition("EID", new DataValue(eId,Types.VARCHAR));
                db2.addCondition("CHATID", new DataValue(chat_id,Types.VARCHAR));
                dataProcessBean.add(new DataProcessBean(db2));

                //新增 DCP_ISVWECOM_GROUPCHAT
                {
                    String[] columns1 = {"EID","CHATID","NAME","STATUS","OWNER","CHATCREATETIME","NOTICE","MEMBERVERSION"};
                    DataValue[] insValue1 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(group_chat.getChat_id(), Types.VARCHAR),
                            new DataValue(group_chat.getName()==null ? "":group_chat.getName(), Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),                //此处企微未返回status，只能默认0 客户群跟进状态0跟进人正常1跟进人离职2离职继承中3离职继承完成
                            new DataValue(group_chat.getOwner()==null ? "":group_chat.getOwner(), Types.VARCHAR),
                            new DataValue(PosPub.timestampToDate(group_chat.getCreate_time()), Types.DATE),
                            new DataValue(group_chat.getNotice()==null ? "":group_chat.getNotice(), Types.VARCHAR),
                            new DataValue(group_chat.getMember_version()==null ? "":group_chat.getMember_version(), Types.VARCHAR),
                    };
                    InsBean ib1 = new InsBean("DCP_ISVWECOM_GROUPCHAT", columns1);
                    ib1.addValues(insValue1);
                    dataProcessBean.add(new DataProcessBean(ib1));
                }

                //新增 DCP_ISVWECOM_GROUPCHAT_DE
                {
                    String[] columns2 = {"EID","CHATID","USERID","NAME","GROUPNICKNAME","TYPE","JOINTIME","JOINSCENE","INVITOR","ISADMIN"};
                    if (CollectionUtil.isNotEmpty(group_chat.getMember_list())) {
                        List<GroupChatDetail.Admin> admins = group_chat.getAdmin_list();
                        for (GroupChatDetail.Member member:group_chat.getMember_list()) {
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
                                    new DataValue(member.getJoin_scene()==null ? "":member.getJoin_scene(), Types.VARCHAR),
                                    new DataValue(invitor, Types.VARCHAR),
                                    new DataValue(isAdmin, Types.VARCHAR),
                            };
                            InsBean ib2 = new InsBean("DCP_ISVWECOM_GROUPCHAT_DE", columns2);
                            ib2.addValues(insValue2);
                            dataProcessBean.add(new DataProcessBean(ib2));
                        }
                    }
                }


                dao.useTransactionProcessData(dataProcessBean);

                return true;

            }else {
                return false;
            }


        }catch (Exception e){
            return false;
        }

    }

    //同步标签库
    public boolean TagSync(DsmDAO dao,String eId){

        try {
            //调企微查询
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            JSONObject resObject = dcpWeComUtils.getCorpTagList(dao);
            if (resObject != null) {

                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                //删除
                {
                    DelBean db1 = new DelBean("DCP_ISVWECOM_TAGGROUP");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    dataProcessBean.add(new DataProcessBean(db1));

                    DelBean db2 = new DelBean("DCP_ISVWECOM_TAG");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    dataProcessBean.add(new DataProcessBean(db2));
                }


                //插入
                String[] group_columns = {"EID", "GROUPID", "GROUPNAME", "GROUPORDER"};
                String[] tag_columns = {"EID", "GROUPID", "TAGID", "TAGNAME", "TAGORDER"};

                JSONArray tag_group = resObject.getJSONArray("tag_group");
                for (int i = 0; i < tag_group.length(); i++) {
                    String group_id = tag_group.getJSONObject(i).optString("group_id","");
                    //新增 DCP_ISVWECOM_TAGGROUP;
                    DataValue[] insValue1 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(group_id, Types.VARCHAR),
                            new DataValue(tag_group.getJSONObject(i).optString("group_name",""), Types.VARCHAR),
                            new DataValue(tag_group.getJSONObject(i).optString("order",""), Types.VARCHAR),
                    };
                    InsBean ib1 = new InsBean("DCP_ISVWECOM_TAGGROUP", group_columns);
                    ib1.addValues(insValue1);
                    dataProcessBean.add(new DataProcessBean(ib1));

                    //标签组内的标签列表
                    JSONArray tag = tag_group.getJSONObject(i).optJSONArray("tag");
                    for (int ii = 0; ii < tag.length(); ii++) {
                        //新增 DCP_ISVWECOM_TAG;
                        DataValue[] insValue2 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(group_id, Types.VARCHAR),
                                new DataValue(tag.getJSONObject(ii).optString("id",""), Types.VARCHAR),
                                new DataValue(tag.getJSONObject(ii).optString("name",""), Types.VARCHAR),
                                new DataValue(tag.getJSONObject(ii).optString("order",""), Types.VARCHAR),
                        };
                        InsBean ib2 = new InsBean("DCP_ISVWECOM_TAG", tag_columns);
                        ib2.addValues(insValue2);
                        dataProcessBean.add(new DataProcessBean(ib2));
                    }
                }

                dao.useTransactionProcessData(dataProcessBean);

                return true;

            }else {
                return false;
            }

        }catch (Exception e){
            return false;
        }

    }

    //企业客户事件回调-添加企业客户事件   https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalContactAdd(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型
            String UserID = jsonObject.optString("UserID","");                   //企业服务人员的UserID
            String ExternalUserID = jsonObject.optString("ExternalUserID","");   //外部联系人的userid，注意不是企业成员的账号
            String State = jsonObject.optString("State","");                     //添加此用户的「联系我」方式配置的state参数，或在获客链接中指定的customer_channel参数，可用于识别添加此用户的渠道
            String WelcomeCode = jsonObject.optString("WelcomeCode","");         //欢迎语code，可用于发送欢迎语

            //新增 DCP_ISVWECOM_CALLBACK_CT
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID", "USERID", "EXTERNALUSERID", "TOUSERNAME", "FROMUSERNAME", "CREATETIME", "MSGTYPE",
                        "EVENT", "CHANGETYPE", "STATE", "WELCOMECODE", "SOURCE", "SERIALNO"};
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(UserID, Types.VARCHAR),
                        new DataValue(ExternalUserID, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue(State, Types.VARCHAR),
                        new DataValue(WelcomeCode, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),     //SOURCE
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_CT", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //刷新本地的客户
            this.ExternalContactSync(dao, eId, ExternalUserID);

            //推送欢迎语
            this.sendWelcome(dao,eId,UserID,WelcomeCode,ExternalUserID);


            //state 就是会员memberid,判断一下会员是否正确，回写
            try{
                if (!Check.Null(State)) {
                    //判断是否是会员编号，避免出错
                    String sql = " select memberid from crm_member where eid='"+eId+"' and memberid='"+State+"' ";
                    List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
                    if (CollectionUtil.isNotEmpty(getQData)) {
                        //回调企微，用于防止A用户把码给B用户去扫，导致会员关联错误
                        sql = " select * from dcp_isvwecom_member where eid='" + eId + "' and memberid='" + State + "' ";
                        List<Map<String, Object>> getMemberQData = dao.executeQuerySQL(sql, null);
                        if (CollectionUtils.isNotEmpty(getMemberQData)) {
                            String union_Id = getMemberQData.get(0).get("UNIONID").toString();
                            String open_Id = getMemberQData.get(0).get("OPENID").toString();

                            //校验外部联系人和本地表中记录的是否一致，此处校验是很危险的，因为没有验证这个场景下，企微是否真的会返回外部客户ID，太危险了！！！！！！！！
                            {
                                DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                                JSONObject jsonCheckObject = dcpWeComUtils.unionid_to_external_userid(StaticInfo.dao, union_Id, open_Id);
                                if (jsonCheckObject != null) {
                                    String external_userid = jsonCheckObject.optString("external_userid", "");
                                    //String pending_id = jsonCheckObject.optString("pending_id","");
                                    if (!Check.Null(external_userid) && external_userid.equals(ExternalUserID)) {
                                        List<DataProcessBean> dataProcessBean1 = new ArrayList<>();
                                        //更新 DCP_ISVWECOM_MEMBER
                                        UptBean ub = new UptBean("DCP_ISVWECOM_MEMBER");
                                        ub.addUpdateValue("EXTERNALUSERID", new DataValue(external_userid, Types.VARCHAR));

                                        //Condition
                                        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        ub.addCondition("MEMBERID", new DataValue(State, Types.VARCHAR));

                                        dataProcessBean1.add(new DataProcessBean(ub));
                                        dao.useTransactionProcessData(dataProcessBean1);

                                    }else{
                                        HelpTools.writelog_fileName("企业微信回调客户关联会员出现异常,调企微校验external_userid失败,企微回调返回的外部客户: " + ExternalUserID + " 校验返回的外部客户:"+external_userid, "dcpisvwecom");
                                    }
                                }
                            }


                            //这段代码先注释，后续如果调企微验证有异常，再启用这段代码
                           /* List<DataProcessBean> dataProcessBean1 = new ArrayList<>();
                            //更新 DCP_ISVWECOM_MEMBER
                            UptBean ub = new UptBean("DCP_ISVWECOM_MEMBER");
                            ub.addUpdateValue("EXTERNALUSERID", new DataValue(ExternalUserID, Types.VARCHAR));

                            //Condition
                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub.addCondition("MEMBERID", new DataValue(State, Types.VARCHAR));

                            dataProcessBean1.add(new DataProcessBean(ub));
                            dao.useTransactionProcessData(dataProcessBean1);*/



                        }else {
                            //查询企微会员表，不存在记录错误日志，现在的代码逻辑里面绝对不会存在这种情形的，存在一定是哪里有BUG了
                            HelpTools.writelog_fileName("企业微信回调客户关联会员出现异常,企微回调返回的STATE: " + State + " 在dcp_isvwecom_member表中找不到指定的会员资料 ", "dcpisvwecom");
                        }
                    }
                }
            }catch(Exception e){
                HelpTools.writelog_fileName("企业微信回调客户关联会员出现异常: " + e.getMessage() + " ", "dcpisvwecom");
            }


            return true;

        }catch (Exception e){
            return false;
        }

    }

    //编辑企业客户事件---调企微客户详情，刷新客户详情   https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalContactEdit(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型
            String UserID = jsonObject.optString("UserID","");                   //企业服务人员的UserID
            String ExternalUserID = jsonObject.optString("ExternalUserID","");   //外部联系人的userid，注意不是企业成员的账号

            //新增 DCP_ISVWECOM_CALLBACK_CT
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID", "USERID", "EXTERNALUSERID", "TOUSERNAME", "FROMUSERNAME", "CREATETIME", "MSGTYPE",
                        "EVENT", "CHANGETYPE", "STATE", "WELCOMECODE", "SOURCE", "SERIALNO"};
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(UserID, Types.VARCHAR),
                        new DataValue(ExternalUserID, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),     //State
                        new DataValue("", Types.VARCHAR),     //WelcomeCode
                        new DataValue("", Types.VARCHAR),     //SOURCE
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_CT", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //刷新本地的客户
            this.ExternalContactSync(dao, eId, ExternalUserID);

            return true;

        }catch (Exception e){
            return false;
        }

    }

    //外部联系人免验证添加成员事件---客人添加员工，推送欢迎语，调企微客户详情，客人已存在则刷新客户详情
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalContactAddHalf(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型
            String UserID = jsonObject.optString("UserID","");                   //企业服务人员的UserID
            String ExternalUserID = jsonObject.optString("ExternalUserID","");   //外部联系人的userid，注意不是企业成员的账号
            String State = jsonObject.optString("State","");                     //添加此用户的「联系我」方式配置的state参数，或在获客链接中指定的customer_channel参数，可用于识别添加此用户的渠道
            String WelcomeCode = jsonObject.optString("WelcomeCode","");         //欢迎语code，可用于发送欢迎语

            //新增 DCP_ISVWECOM_CALLBACK_CT
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID", "USERID", "EXTERNALUSERID", "TOUSERNAME", "FROMUSERNAME", "CREATETIME", "MSGTYPE",
                        "EVENT", "CHANGETYPE", "STATE", "WELCOMECODE", "SOURCE", "SERIALNO"};
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(UserID, Types.VARCHAR),
                        new DataValue(ExternalUserID, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue(State, Types.VARCHAR),     //State
                        new DataValue(WelcomeCode, Types.VARCHAR),     //WelcomeCode
                        new DataValue("", Types.VARCHAR),     //SOURCE
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_CT", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //刷新本地的客户
            this.ExternalContactSync(dao, eId, ExternalUserID);

            //推送欢迎语
            this.sendWelcome(dao,eId,UserID,WelcomeCode,ExternalUserID);


            return true;

        }catch (Exception e){
            return false;
        }

    }

    //删除企业客户事件---员工删除了客人，调企微客户详情，更新客户信息，如果该客人流失了，则变更本地客人状态为流失
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalContactDelExternal(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型
            String UserID = jsonObject.optString("UserID","");                   //企业服务人员的UserID
            String ExternalUserID = jsonObject.optString("ExternalUserID","");   //外部联系人的userid，注意不是企业成员的账号
            String Source = jsonObject.optString("Source","");                   //删除客户的操作来源，DELETE_BY_TRANSFER表示此客户是因在职继承自动被转接成员删除

            //新增 DCP_ISVWECOM_CALLBACK_CT
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID", "USERID", "EXTERNALUSERID", "TOUSERNAME", "FROMUSERNAME", "CREATETIME", "MSGTYPE",
                        "EVENT", "CHANGETYPE", "STATE", "WELCOMECODE", "SOURCE", "SERIALNO"};
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(UserID, Types.VARCHAR),
                        new DataValue(ExternalUserID, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),     //State
                        new DataValue("", Types.VARCHAR),     //WelcomeCode
                        new DataValue(Source, Types.VARCHAR),
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_CT", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //刷新本地的客户
            if (!this.ExternalContactSync(dao, eId, ExternalUserID)){
                String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                List<DataProcessBean> dataProcessBean1 = new ArrayList<>();
                //获取不到客户详情，本地要打标记，状态改成失效
                UptBean ub = new UptBean("DCP_ISVWECOM_CUSTOM");
                ub.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("LOSSTIME", new DataValue(sDate, Types.DATE));

                //条件
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("EXTERNALUSERID", new DataValue(ExternalUserID,Types.VARCHAR));

                dataProcessBean1.add(new DataProcessBean(ub));
                dao.useTransactionProcessData(dataProcessBean1);
            }


            return true;

        }catch (Exception e){
            return false;
        }

    }

    //删除跟进成员事件---客人删除了员工，调企微客户详情，更新客户信息，如果该客人流失了，则变更本地客人状态为流失，根据配置，是否推送信息通知被删员工
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalContactDelFollow(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型
            String UserID = jsonObject.optString("UserID","");                   //企业服务人员的UserID
            String ExternalUserID = jsonObject.optString("ExternalUserID","");   //外部联系人的userid，注意不是企业成员的账号


            //新增 DCP_ISVWECOM_CALLBACK_CT
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID", "USERID", "EXTERNALUSERID", "TOUSERNAME", "FROMUSERNAME", "CREATETIME", "MSGTYPE",
                        "EVENT", "CHANGETYPE", "STATE", "WELCOMECODE", "SOURCE", "SERIALNO"};
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(UserID, Types.VARCHAR),
                        new DataValue(ExternalUserID, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),     //State
                        new DataValue("", Types.VARCHAR),     //WelcomeCode
                        new DataValue("", Types.VARCHAR),     //SOURCE
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_CT", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //刷新本地的客户
            if (!this.ExternalContactSync(dao, eId, ExternalUserID)){
                String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                List<DataProcessBean> dataProcessBean1 = new ArrayList<>();
                //获取不到客户详情，本地要打标记，状态改成失效
                UptBean ub = new UptBean("DCP_ISVWECOM_CUSTOM");
                ub.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("LOSSTIME", new DataValue(sDate, Types.DATE));
                //条件
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("EXTERNALUSERID", new DataValue(ExternalUserID,Types.VARCHAR));

                dataProcessBean1.add(new DataProcessBean(ub));
                dao.useTransactionProcessData(dataProcessBean1);
            }


            //通知被删除的员工    删除好友通知:  客户张先生 在 2024-01-06 13:20:30 删除了和您的好友关系
            if (!Check.Null(UserID)) {
                String sql = " select * from dcp_isvwecom_set where eid='" + eId + "' ";
                List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    String lossNotice = getQData.get(0).get("LOSSNOTICE").toString();  //是否开启客户流失消息通知0否1是
                    if ("1".equals(lossNotice)) {
                        sql = " select agentid from dcp_isvwecom_empower  ";
                        getQData = dao.executeQuerySQL(sql, null);
                        if (CollectionUtil.isNotEmpty(getQData)) {
                            String agentId = getQData.get(0).get("AGENTID").toString();
                            if (!Check.Null(agentId)) {
                                sql = " select name from dcp_isvwecom_custom where externaluserid='" + ExternalUserID + "' ";
                                getQData = dao.executeQuerySQL(sql, null);
                                if (CollectionUtil.isNotEmpty(getQData)) {
                                    String name = getQData.get(0).get("NAME").toString();
                                    if (!Check.Null(name)) {
                                        String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                                        MessageSend messageSend = new MessageSend();
                                        MessageSend.Text text = messageSend.new Text();
                                        text.setContent("删除好友通知: 客户 " + name + " 在 " + sDate + " 删除了和您的好友关系 ");

                                        messageSend.setTouser(UserID);      //指定接收消息的成员，成员ID列表（多个接收者用‘|’分隔，最多支持1000个）。
                                        messageSend.setMsgtype("text");     //消息类型，此时固定为：text
                                        messageSend.setAgentid(agentId);    //企业应用的id，整型。
                                        messageSend.setText(text);

                                        //调企微查询
                                        DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                                        dcpWeComUtils.messageSend(dao,messageSend);

                                    }
                                }
                            }
                        }
                    }

                }

            }





            return true;

        }catch (Exception e){
            return false;
        }

    }

    //客户接替失败事件---企业将客户分配给新的成员接替后，客户添加失败时回调该事件
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalContactTransferFail(DsmDAO dao,String eId,JSONObject jsonObject){

        //暂时不接，等SA规划  20240202
        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型
            String UserID = jsonObject.optString("UserID","");                   //接替失败的企业服务人员的UserID
            String ExternalUserID = jsonObject.optString("ExternalUserID","");   //外部联系人的userid，注意不是企业成员的账号
            String FailReason = jsonObject.optString("FailReason","");           //接替失败的原因, customer_refused-客户拒绝， customer_limit_exceed-接替成员的客户数达到上限


            return true;

        }catch (Exception e){
            return false;
        }

    }

    //客户群创建事件---有新增客户群时，回调该事件。收到该事件后，企业可以调用获取客户群详情接口获取客户群详情。
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalChatCreate(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChatId = jsonObject.optString("ChatId","");                   //群ID
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型

            //新增 DCP_ISVWECOM_CALLBACK_GP
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID","CHATID","TOUSERNAME","FROMUSERNAME","CREATETIME","MSGTYPE","EVENT","CHANGETYPE","UPDATEDETAIL",
                        "JOINSCENE","QUITSCENE","MEMCHANGECNT","LASTMEMVER","CURMEMVER","SERIALNO"};

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(ChatId, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),         //UPDATEDETAIL
                        new DataValue("", Types.VARCHAR),         //JOINSCENE
                        new DataValue("", Types.VARCHAR),         //QUITSCENE
                        new DataValue("", Types.VARCHAR),         //MEMCHANGECNT
                        new DataValue("", Types.VARCHAR),         //LASTMEMVER
                        new DataValue("", Types.VARCHAR),         //CURMEMVER
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),

                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_GP", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //刷新本地的客户群
            this.ExternalChatSync(dao, eId, ChatId);

            return true;

        }catch (Exception e){
            return false;
        }

    }

    //客户群变更事件---客户群被修改后（群名变更，群成员增加或移除，群主变更，群公告变更），回调该事件。收到该事件后，企业需要再调用获取客户群详情接口，以获取最新的群详情。
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalChatUpdate(DsmDAO dao,String eId,JSONObject jsonObject){


        try {
            String ToUserName = jsonObject.optString("ToUserName","");             //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");     //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");         //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");               //消息的类型
            String Event = jsonObject.optString("Event","");                   //事件的类型
            String ChatId = jsonObject.optString("ChatId","");                 //群ID
            String ChangeType = jsonObject.optString("ChangeType","");         //变更类型
            String UpdateDetail = jsonObject.optString("UpdateDetail","");     //变更详情。目前有以下几种：add_member : 成员入群del_member : 成员退群change_owner : 群主变更change_name : 群名变更change_notice : 群公告变更
            String JoinScene = jsonObject.optString("JoinScene","");           //当是成员入群时有值。表示成员的入群方式 0 - 由成员邀请入群（包括直接邀请入群和通过邀请链接入群） 3 - 通过扫描群二维码入群
            String QuitScene = jsonObject.optString("QuitScene","");           //当是成员退群时有值。表示成员的退群方式0 - 自己退群1 - 群主/群管理员移出
            int MemChangeCnt = jsonObject.optInt("MemChangeCnt",0);            //当是成员入群或退群时有值。表示成员变更数量
            String LastMemVer = jsonObject.optString("LastMemVer","");         //当是成员入群或退群时有值。 变更前的群成员版本号
            String CurMemVer = jsonObject.optString("CurMemVer","");           //当是成员入群或退群时有值。变更后的群成员版本号
            JSONObject MemChangeList_obj = jsonObject.optJSONObject("MemChangeList");     //当是成员入群或退群时有值。变更的成员列表


            //新增 DCP_ISVWECOM_CALLBACK_GP
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String serialNo = PosPub.getGUID(false);

                String[] columns1 = {"EID","CHATID","TOUSERNAME","FROMUSERNAME","CREATETIME","MSGTYPE","EVENT","CHANGETYPE","UPDATEDETAIL",
                        "JOINSCENE","QUITSCENE","MEMCHANGECNT","LASTMEMVER","CURMEMVER","SERIALNO"};

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(ChatId, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue(UpdateDetail, Types.VARCHAR),
                        new DataValue(JoinScene, Types.VARCHAR),
                        new DataValue(QuitScene, Types.VARCHAR),
                        new DataValue(MemChangeCnt, Types.VARCHAR),
                        new DataValue(LastMemVer, Types.VARCHAR),
                        new DataValue(CurMemVer, Types.VARCHAR),
                        new DataValue(serialNo, Types.VARCHAR),

                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_GP", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));

                //新增 DCP_ISVWECOM_CALLBACK_GP_DE
                String[] columns2 = {"EID", "SERIALNO","ITEM"};
                if (MemChangeCnt == 1){
                    //	"MemChangeList":{"Item":"woONs8DQAAjDYqHTpbb2O1grb0jNdMrw"}
                    String item = MemChangeList_obj.optString("Item");

                    DataValue[] insValue2 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(serialNo, Types.VARCHAR),
                            new DataValue(item, Types.VARCHAR),
                    };
                    InsBean ib2 = new InsBean("DCP_ISVWECOM_CALLBACK_GP_DE", columns2);
                    ib2.addValues(insValue2);
                    dataProcessBean.add(new DataProcessBean(ib2));

                }
                if (MemChangeCnt > 1){
                    //	"MemChangeList": {"Item": ["woONs8DQAAWXS34s_fyNMCRzoKTMueTg", "woONs8DQAABZ0W1kxpm31sAQi7IaF9Yw", "woONs8DQAAzszyrjtrbDWuPXPSTYDT9A"]},
                    JSONArray MemChangeList_array = MemChangeList_obj.optJSONArray("Item");
                    if (MemChangeList_array != null && MemChangeList_array.length() > 0) {
                        for (int i = 0; i < MemChangeList_array.length(); i++) {
                            DataValue[] insValue2 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(serialNo, Types.VARCHAR),
                                    new DataValue(MemChangeList_array.optString(i), Types.VARCHAR),
                            };
                            InsBean ib2 = new InsBean("DCP_ISVWECOM_CALLBACK_GP_DE", columns2);
                            ib2.addValues(insValue2);
                            dataProcessBean.add(new DataProcessBean(ib2));
                        }
                    }
                }


                dao.useTransactionProcessData(dataProcessBean);

            }


            /*关于客户群变更事件中，群成员版本号的使用技巧
            客户群变更较为频繁，开发者希望直接通过回调事件来更新群成员，因此事件新增变更成员列表信息。
            而变更事件的回调过程并不可靠（接收方可能丢失或乱序），为了让开发者能准确判断本次接收到的客户群变更事件是否可信任，
            变更事件中新增两个版本号：变更前的群成员版本号、变更后的群成员版本号。开发者可以存储每个群的最新版本号，当接收到客户群变更事件后，
            如果是成员入群或成员退群事件，则对比事件中的变更前版本号与自己存储的最新版本号是否一致，如果一致则说明本次事件可信任，并更新最新版本号；
            如果不一致，则说明本次收到的事件不可靠，可以通过拉客户群详情来获取最新信息和最新版本号。以此在不需要增加调用量的前提下，实现数据一致性。
            */

            String sql=" select memberversion from dcp_isvwecom_groupchat where eid='"+eId+"' and chatid='"+ChatId+"' ";
            List<Map<String,Object>> getQData = dao.executeQuerySQL(sql,null);
            if (CollectionUtil.isNotEmpty(getQData)){
                if (LastMemVer.equals(getQData.get(0).get("MEMBERVERSION").toString())){
                    //如果一致则说明本次事件可信任，并更新最新版本号；
                    //只有删除可以在本地来处理，其他都需要重新去刷新同步
                    if ("del_member".equals(UpdateDetail)) {
                        List<DataProcessBean> dataProcessBean1 = new ArrayList<>();
                        if (MemChangeCnt == 1){
                            //	"MemChangeList":{"Item":"woONs8DQAAjDYqHTpbb2O1grb0jNdMrw"}
                            String item = MemChangeList_obj.optString("Item");

                            DelBean db1 = new DelBean("DCP_ISVWECOM_GROUPCHAT_DE");
                            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            db1.addCondition("CHATID", new DataValue(ChatId, Types.VARCHAR));
                            db1.addCondition("USERID", new DataValue(item, Types.VARCHAR));

                            dataProcessBean1.add(new DataProcessBean(db1));
                        }


                        if (MemChangeCnt > 1){
                            //	"MemChangeList": {"Item": ["woONs8DQAAWXS34s_fyNMCRzoKTMueTg", "woONs8DQAABZ0W1kxpm31sAQi7IaF9Yw", "woONs8DQAAzszyrjtrbDWuPXPSTYDT9A"]},
                            JSONArray MemChangeList_array = MemChangeList_obj.optJSONArray("Item");
                            if (MemChangeList_array != null && MemChangeList_array.length() > 0) {
                                for (int i = 0; i < MemChangeList_array.length(); i++) {
                                    //String item = MemChangeList.optString(i);
                                    DelBean db1 = new DelBean("DCP_ISVWECOM_GROUPCHAT_DE");
                                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    db1.addCondition("CHATID", new DataValue(ChatId, Types.VARCHAR));
                                    db1.addCondition("USERID", new DataValue(MemChangeList_array.optString(i), Types.VARCHAR));

                                    dataProcessBean1.add(new DataProcessBean(db1));
                                }
                            }
                        }



                        //更新版本号
                        UptBean ub1 = new UptBean("DCP_ISVWECOM_GROUPCHAT");
                        ub1.addUpdateValue("MEMBERVERSION", new DataValue(CurMemVer, Types.VARCHAR));

                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub1.addCondition("CHATID", new DataValue(ChatId, Types.VARCHAR));

                        dataProcessBean1.add(new DataProcessBean(ub1));

                        dao.useTransactionProcessData(dataProcessBean1);

                        return true;


                    }
                }
            }


            //刷新本地的客户群
            this.ExternalChatSync(dao, eId, ChatId);

            return true;

        }catch (Exception e){
            return false;
        }


    }

    //客户群解散事件---当客户群被群主解散后，回调该事件。 需注意的是，如果发生群信息变动，会立即收到此事件，但是部分信息是异步处理，可能需要等一段时间(例如2秒)调用获取客户群详情接口才能得到最新结果
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalChatDismiss(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String ChatId = jsonObject.optString("ChatId","");                   //群ID
            String ChangeType = jsonObject.optString("ChangeType","");           //变更类型

            //新增 DCP_ISVWECOM_CALLBACK_GP
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID","CHATID","TOUSERNAME","FROMUSERNAME","CREATETIME","MSGTYPE","EVENT","CHANGETYPE","UPDATEDETAIL",
                        "JOINSCENE","QUITSCENE","MEMCHANGECNT","LASTMEMVER","CURMEMVER","SERIALNO"};

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(ChatId, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),         //UPDATEDETAIL
                        new DataValue("", Types.VARCHAR),         //JOINSCENE
                        new DataValue("", Types.VARCHAR),         //QUITSCENE
                        new DataValue("", Types.VARCHAR),         //MEMCHANGECNT
                        new DataValue("", Types.VARCHAR),         //LASTMEMVER
                        new DataValue("", Types.VARCHAR),         //CURMEMVER
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),

                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_GP", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //删除本地的客户群
            {
                List<DataProcessBean> dataProcessBean1 = new ArrayList<>();

                DelBean db1 = new DelBean("DCP_ISVWECOM_GROUPCHAT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("CHATID", new DataValue(ChatId, Types.VARCHAR));

                dataProcessBean1.add(new DataProcessBean(db1));


                DelBean db2 = new DelBean("DCP_ISVWECOM_GROUPCHAT_DE");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("CHATID", new DataValue(ChatId, Types.VARCHAR));

                dataProcessBean1.add(new DataProcessBean(db2));

                dao.useTransactionProcessData(dataProcessBean1);

            }

            return true;

        }catch (Exception e){
            return false;
        }

    }

    ////企业客户标签创建事件---企业/管理员创建客户标签/标签组时（包括规则组的标签），回调此事件。收到该事件后，企业需要调用获取企业标签库来获取标签/标签组的详细信息。
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalTagCreate(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String Id = jsonObject.optString("Id","");                           //标签或标签组的ID
            String TagType = jsonObject.optString("TagType","");                 //创建标签时，此项为tag，创建标签组时，此项为tag_group
            String ChangeType = jsonObject.optString("ChangeType","");           //此时固定为create
            String StrategyId = jsonObject.optString("StrategyId","");           //标签或标签组所属的规则组id，只回调给“客户联系”应用


            //新增 DCP_ISVWECOM_CALLBACK_TAG
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID","TAGID","TOUSERNAME","FROMUSERNAME","CREATETIME","MSGTYPE","EVENT","CHANGETYPE",
                        "TAGTYPE","STRATEGYID","SERIALNO"};

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(Id, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue(TagType, Types.VARCHAR),
                        new DataValue(StrategyId, Types.VARCHAR),
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_TAG", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //重新获取并刷新本地的标签
            this.TagSync(dao, eId);

            return true;

        }catch (Exception e){
            return false;
        }


    }

    //企业客户标签变更事件---当企业客户标签/标签组（包括规则组的标签）被修改时，回调此事件。收到该事件后，企业需要调用获取企业标签库来获取标签/标签组的详细信息。
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalTagUpdate(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String Id = jsonObject.optString("Id","");                           //标签或标签组的ID
            String TagType = jsonObject.optString("TagType","");                 //创建标签时，此项为tag，创建标签组时，此项为tag_group
            String ChangeType = jsonObject.optString("ChangeType","");           //此时固定为create
            String StrategyId = jsonObject.optString("StrategyId","");           //标签或标签组所属的规则组id，只回调给“客户联系”应用


            //新增 DCP_ISVWECOM_CALLBACK_TAG
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID","TAGID","TOUSERNAME","FROMUSERNAME","CREATETIME","MSGTYPE","EVENT","CHANGETYPE",
                        "TAGTYPE","STRATEGYID","SERIALNO"};

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(Id, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue(TagType, Types.VARCHAR),
                        new DataValue(StrategyId, Types.VARCHAR),
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_TAG", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //重新获取并刷新本地的标签
            this.TagSync(dao, eId);

            return true;

        }catch (Exception e){
            return false;
        }

    }

    //企业客户标签删除事件---当企业客户标签/标签组被删除时，回调此事件。删除标签组时，该标签组下的所有标签将被同时删除，但不会进行回调。
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalTagDelete(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String Id = jsonObject.optString("Id","");                           //标签或标签组的ID
            String TagType = jsonObject.optString("TagType","");                 //创建标签时，此项为tag，创建标签组时，此项为tag_group
            String ChangeType = jsonObject.optString("ChangeType","");           //此时固定为create
            String StrategyId = jsonObject.optString("StrategyId","");           //标签或标签组所属的规则组id，只回调给“客户联系”应用

            //新增 DCP_ISVWECOM_CALLBACK_TAG
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID","TAGID","TOUSERNAME","FROMUSERNAME","CREATETIME","MSGTYPE","EVENT","CHANGETYPE",
                        "TAGTYPE","STRATEGYID","SERIALNO"};

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(Id, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue(TagType, Types.VARCHAR),
                        new DataValue(StrategyId, Types.VARCHAR),
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_TAG", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //重新获取并刷新本地的标签
            this.TagSync(dao, eId);

            return true;

        }catch (Exception e){
            return false;
        }

    }

    //企业客户标签重排事件---当企业管理员在终端/管理端调整标签顺序时，可能导致标签顺序整体调整重排，引起大部分标签的order值发生变化，此时会回调此事件，收到此事件后企业应尽快全量同步标签的order值，防止后续调用接口排序出现非预期结果。
    //https://developer.work.weixin.qq.com/document/path/96361
    public boolean ExternalTagShuffle(DsmDAO dao,String eId,JSONObject jsonObject){

        try {
            String ToUserName = jsonObject.optString("ToUserName","");               //企业微信CorpID
            String FromUserName = jsonObject.optString("FromUserName","");       //此事件该值固定为sys，表示该消息由系统生成
            String CreateTime = jsonObject.optString("CreateTime","");           //消息创建时间 （整型）
            String MsgType = jsonObject.optString("MsgType","");                 //消息的类型
            String Event = jsonObject.optString("Event","");                     //事件的类型
            String Id = jsonObject.optString("Id","");                           //标签或标签组的ID
            String ChangeType = jsonObject.optString("ChangeType","");           //此时固定为create
            String StrategyId = jsonObject.optString("StrategyId","");           //标签或标签组所属的规则组id，只回调给“客户联系”应用

            //新增 DCP_ISVWECOM_CALLBACK_TAG
            {
                List<DataProcessBean> dataProcessBean = new ArrayList<>();
                String[] columns1 = {"EID","TAGID","TOUSERNAME","FROMUSERNAME","CREATETIME","MSGTYPE","EVENT","CHANGETYPE",
                        "TAGTYPE","STRATEGYID","SERIALNO"};

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(Id, Types.VARCHAR),
                        new DataValue(ToUserName, Types.VARCHAR),
                        new DataValue(FromUserName, Types.VARCHAR),
                        new DataValue(PosPub.timestampToDate(CreateTime), Types.DATE),
                        new DataValue(MsgType, Types.VARCHAR),
                        new DataValue(Event, Types.VARCHAR),
                        new DataValue(ChangeType, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),       //TagType
                        new DataValue(StrategyId, Types.VARCHAR),
                        new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_ISVWECOM_CALLBACK_TAG", columns1);
                ib1.addValues(insValue1);
                dataProcessBean.add(new DataProcessBean(ib1));
                dao.useTransactionProcessData(dataProcessBean);
            }

            //重新获取并刷新本地的标签
            this.TagSync(dao, eId);

            return true;

        }catch (Exception e){
            return false;
        }


    }

    //推送个人欢迎语
    public void sendWelcome(DsmDAO dao,String eId,String UserID,String WelcomeCode,String ExternalUserID){

        try {
            String sql = " select a.* from dcp_isvwecom_welmsg a"
                    + " left join dcp_isvwecom_welmsg_staff b on a.eid=b.eid and a.welmsgid=b.welmsgid and b.userid ='"+UserID+"'"
                    + " where a.eid='"+eId+"' and (a.restrictstaff='0' or (a.restrictstaff='1' and b.userid ='"+UserID+"')) "
                    + " order by a.restrictstaff desc";
            List<Map<String,Object>> getQData = dao.executeQuerySQL(sql,null);
            if (CollectionUtil.isNotEmpty(getQData)){
                String welMsgId = getQData.get(0).get("WELMSGID").toString();

                WelcomeMsg welcomeMsg = new WelcomeMsg();
                welcomeMsg.setWelcome_code(WelcomeCode);
                WelcomeMsg.Text text = welcomeMsg.new Text();
                String msg = getQData.get(0).get("MSG").toString();
                String name = "";
                sql = " select name from dcp_isvwecom_custom where eid='"+eId+"' and externaluserid='"+ExternalUserID+"' ";
                List<Map<String,Object>> getNameQData = dao.executeQuerySQL(sql,null);
                if (CollectionUtil.isNotEmpty(getNameQData)){
                    name = getNameQData.get(0).get("NAME").toString();
                    msg = msg.replace("<客户名称>", name);
                }

                text.setContent(msg);
                welcomeMsg.setText(text);
                welcomeMsg.setAttachments(new ArrayList<>());

                sql = " select * from dcp_isvwecom_welmsg_annex where eid='"+eId+"' and welmsgid='"+welMsgId+"' order by serialno ";
                getQData = dao.executeQuerySQL(sql,null);
                if (CollectionUtil.isNotEmpty(getQData)){

                    for (Map<String,Object> oneQData : getQData){

                        String msgType = oneQData.get("MSGTYPE").toString(); //图片image 链接 link 小程序miniprogram  视频video 文件 file

                        switch (msgType){
                            case "image": {

                                String wecomPicUrl = oneQData.get("IMAGEWECOMPICURL").toString(); //企微图片永久链接

                                WelcomeMsg.Image image = welcomeMsg.new Image();
                                image.setPic_url(wecomPicUrl);   //必传 否	图片的链接，仅可使用上传图片接口得到的链接

                                WelcomeMsg.Attachment attachment = welcomeMsg.new Attachment();
                                attachment.setMsgtype("image");  //企微附件类型，可选image、link、miniprogram或者video
                                attachment.setImage(image);

                                welcomeMsg.getAttachments().add(attachment);

                            }
                            break;
                            case "link": {
                                String description = oneQData.get("LINKDESCRIPTION").toString();
                                String linkUrl = oneQData.get("LINKURL").toString();
                                String title = oneQData.get("LINKTITLE").toString();
                                String linkMediaUrl = oneQData.get("LINKMEDIAURL").toString();

                                WelcomeMsg.Link link = welcomeMsg.new Link();
                                link.setTitle(title);                //必传 是	图文消息标题，最长为128字节
                                link.setDesc(description);          //必传 否	图文消息的描述，最长为512字节
                                link.setPicurl(linkMediaUrl);  //必传 否	图文消息封面的url
                                link.setUrl(linkUrl);               //必传 是	图文消息的链接

                                WelcomeMsg.Attachment attachment = welcomeMsg.new Attachment();
                                attachment.setMsgtype("link");  //企微附件类型，可选image、link、miniprogram或者video
                                attachment.setLink(link);

                                welcomeMsg.getAttachments().add(attachment);

                            }
                            break;
                            case "miniprogram": {

                                String title = oneQData.get("MINITITLE").toString();
                                String appId = oneQData.get("MINIAPPID").toString();
                                String miniUrl = oneQData.get("MINIURL").toString();
                                String mediaId = oneQData.get("MINIMEDIAID").toString();

                                String pic_media_id = "";
                                if (!Check.Null(mediaId)){
                                    File file = new File("D://resource//image//" + mediaId);
                                    if (file.exists()){
                                        //把图片上传到企微素材库，获取media_id
                                        DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                                        pic_media_id = dcpWeComUtils.upload(dao,"image",file);
                                    }

                                }

                                WelcomeMsg.MiniProgram miniProgram = welcomeMsg.new MiniProgram();
                                miniProgram.setTitle(title);                 //必传 是	小程序消息标题，最长为64字节
                                miniProgram.setPic_media_id(pic_media_id);  //必传 是	小程序消息封面的mediaid，封面图建议尺寸为520*416
                                miniProgram.setAppid(appId);                //必传 是	小程序appid，必须是关联到企业的小程序应用
                                miniProgram.setPage(miniUrl);               //必传 是	小程序page路径

                                WelcomeMsg.Attachment attachment = welcomeMsg.new Attachment();
                                attachment.setMsgtype("miniprogram");    //图片image 链接 link 小程序miniprogram  视频video 文件 file
                                attachment.setMiniprogram(miniProgram);

                                welcomeMsg.getAttachments().add(attachment);

                            }
                            break;
                            case "video":
                                //暂时不支持
                                break;
                            case "file":
                                //暂时不支持
                                break;
                        }

                    }
                }

                DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                dcpWeComUtils.send_welcome_msg(dao, welcomeMsg);
            }

        }catch (Exception ignored){

        }


    }

}
