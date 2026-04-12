package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgCreateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgCreateReq.*;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.MsgTemplate;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComGpMsgCreate
 * 服务说明：群发消息新增
 * @author jinzma
 * @since  2024-03-01
 */
public class DCP_ISVWeComGpMsgCreate extends SPosAdvanceService<DCP_ISVWeComGpMsgCreateReq, DCP_ISVWeComGpMsgCreateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComGpMsgCreateReq req, DCP_ISVWeComGpMsgCreateRes res) throws Exception {

        try{
            String eId = req.geteId();
            String type = req.getRequest().getType();             // type 类型: 1指定客户 2指定客户群
            String sendType = req.getRequest().getSendType();     // sendType 发送类型: 0所有客户 1指定客户
            List<Tag> tagList = req.getRequest().getTagList();
            List<User> userList = req.getRequest().getUserList();
            List<Chat> chatList = req.getRequest().getChatList();
            List<Annex> annexList = req.getRequest().getAnnexList();

            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String gpMsgId = PosPub.getGUID(false);

            //资料检查一
            {
                String sql = "select name from dcp_isvwecom_gpmsg where eid='"+eId+"' and name='"+req.getRequest().getName()+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "名称已存在,无法新增");
                }
            }

            //资料检查二 && 群发所有客户时重新赋值userList
            {
                String sql = "select userid from dcp_isvwecom_staffs where eid='"+eId+"' and userid is not null";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发失败,企微用户未维护 ");
                }

                if ("0".equals(sendType)) {
                    userList = new ArrayList<>();
                    for (Map<String, Object> oneQData:getQData){
                        User user = req.new User();
                        user.setUserId(oneQData.get("USERID").toString());

                        userList.add(user);
                    }
                }
            }

            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            MsgTemplate msgTemplate = new MsgTemplate();
            //发送内容赋值
            {
                MsgTemplate.Text text = msgTemplate.new Text();
                text.setContent(req.getRequest().getMsg());
                msgTemplate.setText(text);
                msgTemplate.setAttachments(new ArrayList<>());

                //图片地址
                String ISHTTPS = PosPub.getPARA_SMS(dao,eId,"","ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(dao,eId,"","DomainName");
                String imagePath = httpStr + DomainName + "/resource/image/";
                if (DomainName.endsWith("/")) {
                    imagePath = httpStr + DomainName + "resource/image/";
                }

                if (CollectionUtil.isNotEmpty(annexList)) {
                    int i =1;
                    for (Annex annex : annexList) {
                        String msgType = annex.getMsgType(); //图片image 链接 link 小程序miniprogram  视频video 文件 file
                        String msgId = annex.getMsgId();
                        switch (msgType){
                            case "image": {
                                String sql = "select wecompicurl from crm_media where mediaid='" + msgId + "' ";
                                List<Map<String, Object>> getImageQData = dao.executeQuerySQL(sql, null);
                                if (CollectionUtil.isEmpty(getImageQData)) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "msgId"+msgId+"在素材库中不存在");
                                }
                                String wecomPicUrl = getImageQData.get(0).get("WECOMPICURL").toString(); //企微图片永久链接
                                if (Check.Null(wecomPicUrl)){

                                    File file = new File("D://resource//image//" + msgId);
                                    wecomPicUrl = dcpWeComUtils.upLoadImg(dao,file);
                                    if (Check.Null(wecomPicUrl)){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微上传图片失败 ");
                                    }

                                    //回写crm_media表
                                    UptBean ub = new UptBean("CRM_MEDIA");
                                    ub.addUpdateValue("WECOMPICURL", new DataValue(wecomPicUrl, Types.VARCHAR));
                                    ub.addCondition("MEDIAID", new DataValue(msgId, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(ub));

                                }


                                MsgTemplate.Image image = msgTemplate.new Image();
                                image.setPic_url(wecomPicUrl);   //必传 否	图片的链接，仅可使用上传图片接口得到的链接

                                MsgTemplate.Attachment attachment = msgTemplate.new Attachment();
                                attachment.setMsgtype("image");  //企微附件类型，可选image、link、miniprogram或者video
                                attachment.setImage(image);

                                msgTemplate.getAttachments().add(attachment);


                                //新增 DCP_ISVWECOM_GPMSG_ANNEX
                                {
                                    String[] columns = {"EID","GPMSGID","SERIALNO","MSGTYPE","MSGID",
                                            "IMAGEWECOMPICURL","IMAGEMEDIAID","IMAGEPICMEDIAID",
                                    };

                                    InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ANNEX", columns);
                                    DataValue[] insValue = new DataValue[]{
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(gpMsgId, Types.VARCHAR),
                                            new DataValue(i, Types.VARCHAR),
                                            new DataValue(msgType, Types.VARCHAR),
                                            new DataValue(msgId, Types.VARCHAR),
                                            new DataValue(wecomPicUrl, Types.VARCHAR),
                                            new DataValue(msgId, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                    };
                                    ib.addValues(insValue);
                                    this.addProcessData(new DataProcessBean(ib));

                                }



                            }
                            break;
                            case "link": {
                                String sql = " select * from dcp_isvwecom_media_link where eid='" + eId + "' and linkid='" + msgId + "' ";
                                List<Map<String, Object>> getLinkQData = dao.executeQuerySQL(sql, null);
                                if (CollectionUtil.isEmpty(getLinkQData)) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "msgId"+msgId+"在素材库中不存在");
                                }

                                String description = getLinkQData.get(0).get("DESCRIPTION").toString();
                                String linkUrl = getLinkQData.get(0).get("LINKURL").toString();
                                String title = getLinkQData.get(0).get("TITLE").toString();
                                String linkMediaId = getLinkQData.get(0).get("MEDIAID").toString();
                                String linkMediaUrl = "";

                                //图文消息标题，最长为128字节
                                if (!Check.Null(title)) {
                                    if (title.getBytes(StandardCharsets.UTF_8).length>128){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "图文消息标题,最多128个字节");
                                    }
                                }

                                //图文消息的描述，最长为512字节
                                if (!Check.Null(description)) {
                                    if (description.getBytes(StandardCharsets.UTF_8).length>512){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "图文消息的描述,最长为512字节");
                                    }
                                }


                                MsgTemplate.Link link = msgTemplate.new Link();
                                link.setTitle(title);                      //必传 是	图文消息标题，最长为128字节
                                link.setDesc(description);                //必传 否	图文消息的描述，最长为512字节
                                if (Check.Null(linkMediaId)){
                                    link.setPicurl("");
                                }else {
                                    link.setPicurl(imagePath + linkMediaId);  //必传 否	图文消息封面的url
                                    linkMediaUrl = imagePath + linkMediaId;
                                }
                                link.setUrl(linkUrl);                     //必传 是	图文消息的链接

                                MsgTemplate.Attachment attachment = msgTemplate.new Attachment();
                                attachment.setMsgtype("link");  //企微附件类型，可选image、link、miniprogram或者video
                                attachment.setLink(link);

                                msgTemplate.getAttachments().add(attachment);


                                //新增 DCP_ISVWECOM_GPMSG_ANNEX
                                {
                                    String[] columns = {"EID","GPMSGID","SERIALNO","MSGTYPE","MSGID",
                                            "LINKDESCRIPTION","LINKURL","LINKMEDIAID","LINKMEDIAURL","LINKTITLE","LINKPICMEDIAID",
                                    };

                                    InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ANNEX", columns);
                                    DataValue[] insValue = new DataValue[]{
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(gpMsgId, Types.VARCHAR),
                                            new DataValue(i, Types.VARCHAR),
                                            new DataValue(msgType, Types.VARCHAR),
                                            new DataValue(msgId, Types.VARCHAR),
                                            new DataValue(description, Types.VARCHAR),
                                            new DataValue(linkUrl, Types.VARCHAR),
                                            new DataValue(linkMediaId, Types.VARCHAR),
                                            new DataValue(linkMediaUrl, Types.VARCHAR),
                                            new DataValue(title, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                    };
                                    ib.addValues(insValue);
                                    this.addProcessData(new DataProcessBean(ib));

                                }

                            }
                            break;
                            case "miniprogram": {
                                String sql = "select * from dcp_isvwecom_media_mini where eid='" + eId + "' and miniid='" + msgId + "' ";
                                List<Map<String, Object>> getMiniprogramQData = dao.executeQuerySQL(sql, null);
                                if (CollectionUtil.isEmpty(getMiniprogramQData)) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "msgId"+msgId+"在素材库中不存在");
                                }

                                String title = getMiniprogramQData.get(0).get("TITLE").toString();
                                String appId = getMiniprogramQData.get(0).get("APPID").toString();
                                String miniUrl = getMiniprogramQData.get(0).get("MINIURL").toString();
                                String miniMediaId = getMiniprogramQData.get(0).get("MEDIAID").toString();

                                //小程序消息标题，最长为64字节
                                if (!Check.Null(title)) {
                                    if (title.getBytes(StandardCharsets.UTF_8).length>64){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "小程序消息标题,最长为64字节");
                                    }
                                }


                                String pic_media_id;
                                if (!Check.Null(miniMediaId)) {
                                    File file = new File("D://resource//image//" + miniMediaId);

                                    if (!file.exists()){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "文件在目录中不存在 文件名: "+miniMediaId);
                                    }

                                    //把图片上传到企微素材库，获取media_id
                                    pic_media_id = dcpWeComUtils.upload(dao, "image", file);
                                    if (Check.Null(pic_media_id)){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "小程序封面图片必传 ");
                                    }
                                }else {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微上传图片失败 ");
                                }

                                MsgTemplate.MiniProgram miniProgram = msgTemplate.new MiniProgram();
                                miniProgram.setTitle(title);                 //必传 是	小程序消息标题，最长为64字节
                                miniProgram.setPic_media_id(pic_media_id);  //必传 是	小程序消息封面的mediaid，封面图建议尺寸为520*416
                                miniProgram.setAppid(appId);                //必传 是	小程序appid，必须是关联到企业的小程序应用
                                miniProgram.setPage(miniUrl);               //必传 是	小程序page路径

                                MsgTemplate.Attachment attachment = msgTemplate.new Attachment();
                                attachment.setMsgtype("miniprogram");    //图片image 链接 link 小程序miniprogram  视频video 文件 file
                                attachment.setMiniprogram(miniProgram);

                                msgTemplate.getAttachments().add(attachment);


                                //新增 DCP_ISVWECOM_GPMSG_ANNEX
                                {
                                    String[] columns = {"EID","GPMSGID","SERIALNO","MSGTYPE","MSGID",
                                            "MINIAPPID","MINIURL","MINIMEDIAID","MINIPICMEDIAID","MINITITLE",
                                    };

                                    InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ANNEX", columns);
                                    DataValue[] insValue = new DataValue[]{
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(gpMsgId, Types.VARCHAR),
                                            new DataValue(i, Types.VARCHAR),
                                            new DataValue(msgType, Types.VARCHAR),
                                            new DataValue(msgId, Types.VARCHAR),
                                            new DataValue(appId, Types.VARCHAR),
                                            new DataValue(miniUrl, Types.VARCHAR),
                                            new DataValue(miniMediaId, Types.VARCHAR),
                                            new DataValue(pic_media_id, Types.VARCHAR),
                                            new DataValue(title, Types.VARCHAR),
                                    };
                                    ib.addValues(insValue);
                                    this.addProcessData(new DataProcessBean(ib));

                                }


                            }
                            break;
                            case "video":
                                //暂时不支持
                                break;
                            case "file":
                                //暂时不支持
                                break;
                        }

                        i++;
                    }
                }
            }


            //全部客户或者（指定客户 && 指定用户ID）
            if (type.equals("1") && CollectionUtil.isNotEmpty(userList)){
                boolean isFail = true;  //所有都失败才返回失败  3/1 和安驰确认
                msgTemplate.setChat_type("single");  //否	群发任务的类型，默认为single，表示发送给客户，group表示发送给客户群


                for (User user : userList){
                    msgTemplate.setSender(user.getUserId());
                    JSONObject add_msg_template = dcpWeComUtils.add_msg_template(dao,msgTemplate);
                    if (add_msg_template != null){
                        String msgId = add_msg_template.optString("msgid","");
                        if (!Check.Null(msgId)){
                            //新增 DCP_ISVWECOM_GPMSG_ID  记录企微返回的企业群发消息的id
                            {
                                String[] columns = {"EID","GPMSGID","MSGID"};
                                InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ID", columns);
                                DataValue[] insValue = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(gpMsgId, Types.VARCHAR),
                                        new DataValue(msgId, Types.VARCHAR),
                                };
                                ib.addValues(insValue);
                                this.addProcessData(new DataProcessBean(ib));
                            }

                            isFail = false; //只要企微有一个成功就返回成功  3/1 和安驰确认

                            JSONArray fail_list = add_msg_template.getJSONArray("fail_list");
                            if (fail_list!=null){
                                String[] columns = {"EID","GPMSGID","MSGID","FAILID"};
                                for (int i=0; i<fail_list.length(); i++) {
                                    //新增 DCP_ISVWECOM_GPMSG_ID_FAIL 记录企微返回的无效或无法发送的external_userid或chatid列表
                                    {
                                        InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ID_FAIL", columns);
                                        DataValue[] insValue = new DataValue[]{
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(gpMsgId, Types.VARCHAR),
                                                new DataValue(msgId, Types.VARCHAR),
                                                new DataValue(fail_list.get(i).toString(), Types.VARCHAR),
                                        };
                                        ib.addValues(insValue);
                                        this.addProcessData(new DataProcessBean(ib));
                                    }
                                }
                            }
                        }
                    }
                }
                if (isFail){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业群发消息失败: 调企微接口异常,请参阅日志 ");
                }


                //新增 DCP_ISVWECOM_GPMSG_USER
                {
                    String[] columns = {"EID","GPMSGID","USERID"};
                    for (User user : userList) {
                        InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_USER", columns);
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(gpMsgId, Types.VARCHAR),
                                new DataValue(user.getUserId(), Types.VARCHAR),
                        };
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                    }
                }



            }

            //指定客户 && 指定标签
            if (type.equals("1") && CollectionUtil.isNotEmpty(tagList)){
                boolean isFail = true;  //所有都失败才返回失败  3/1 和安驰确认
                msgTemplate.setChat_type("single");  //否	群发任务的类型，默认为single，表示发送给客户，group表示发送给客户群
                MsgTemplate.TagFilter tag_filter = msgTemplate.new TagFilter();
                List<MsgTemplate.Group> group_list = new ArrayList<>();

                //distinct前端传入的标签ID
                List<String> tagId_distinct = tagList.stream().map(Tag::getItem).distinct().collect(Collectors.toList());

                //循环传入的标签组，如果有多个就循环多个
                for (String tag_distinct : tagId_distinct){
                    MsgTemplate.Group group = msgTemplate.new Group();
                    // 创建一个空的集合.用于后续转成数组
                    List<String> tags = new ArrayList<>();

                    for (Tag tag : tagList){
                        if (tag_distinct.equals(tag.getItem())){
                            tags.add(tag.getTagId());
                        }
                    }
                    String[] tag_list = tags.toArray(new String[0]);

                    group.setTag_list(tag_list);
                    group_list.add(group);

                }

                tag_filter.setGroup_list(group_list);
                msgTemplate.setTag_filter(tag_filter);
                JSONObject add_msg_template = dcpWeComUtils.add_msg_template(dao,msgTemplate);
                if (add_msg_template != null){
                    String msgId = add_msg_template.optString("msgid","");
                    if (!Check.Null(msgId)){
                        //新增 DCP_ISVWECOM_GPMSG_ID  记录企微返回的企业群发消息的id
                        {
                            String[] columns = {"EID","GPMSGID","MSGID"};
                            InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ID", columns);
                            DataValue[] insValue = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(gpMsgId, Types.VARCHAR),
                                    new DataValue(msgId, Types.VARCHAR),
                            };
                            ib.addValues(insValue);
                            this.addProcessData(new DataProcessBean(ib));
                        }

                        isFail = false; //只要企微有一个成功就返回成功  3/1 和安驰确认

                        JSONArray fail_list = add_msg_template.getJSONArray("fail_list");
                        if (fail_list!=null){
                            String[] columns = {"EID","GPMSGID","MSGID","FAILID"};
                            for (int i=0; i<fail_list.length(); i++) {
                                //新增 DCP_ISVWECOM_GPMSG_ID_FAIL 记录企微返回的无效或无法发送的external_userid或chatid列表
                                {
                                    InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ID_FAIL", columns);
                                    DataValue[] insValue = new DataValue[]{
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(gpMsgId, Types.VARCHAR),
                                            new DataValue(msgId, Types.VARCHAR),
                                            new DataValue(fail_list.get(i).toString(), Types.VARCHAR),
                                    };
                                    ib.addValues(insValue);
                                    this.addProcessData(new DataProcessBean(ib));
                                }
                            }
                        }
                    }
                }


                if (isFail){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业群发消息失败: 调企微接口异常,请参阅日志 ");
                }


                //新增 DCP_ISVWECOM_GPMSG_TAG
                {
                    String[] columns = {"EID","GPMSGID","ITEM","TAGID"};
                    for (Tag tag : tagList) {
                        InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_TAG", columns);
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(gpMsgId, Types.VARCHAR),
                                new DataValue(tag.getItem(), Types.VARCHAR),
                                new DataValue(tag.getTagId(), Types.VARCHAR),
                        };
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                    }
                }


            }

            //指定客户群
            if (type.equals("2") && CollectionUtil.isNotEmpty(chatList)){
                boolean isFail = true;  //所有都失败才返回失败  3/1 和安驰确认
                msgTemplate.setChat_type("group");  //否	群发任务的类型，默认为single，表示发送给客户，group表示发送给客户群


                for (Chat chat : chatList){
                    msgTemplate.setChat_id_list(new String[]{chat.getChatId()});
                    String sql = " select owner from dcp_isvwecom_groupchat where eid='"+eId+"' and chatid='"+chat.getChatId()+"' ";
                    List<Map<String, Object>> getOwnerQData = dao.executeQuerySQL(sql, null);
                    if (CollectionUtil.isNotEmpty(getOwnerQData)) {
                        msgTemplate.setSender(getOwnerQData.get(0).get("OWNER").toString());
                    }

                    JSONObject add_msg_template = dcpWeComUtils.add_msg_template(dao,msgTemplate);
                    if (add_msg_template != null){
                        String msgId = add_msg_template.optString("msgid","");
                        if (!Check.Null(msgId)){
                            //新增 DCP_ISVWECOM_GPMSG_ID  记录企微返回的企业群发消息的id
                            {
                                String[] columns = {"EID","GPMSGID","MSGID"};
                                InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ID", columns);
                                DataValue[] insValue = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(gpMsgId, Types.VARCHAR),
                                        new DataValue(msgId, Types.VARCHAR),
                                };
                                ib.addValues(insValue);
                                this.addProcessData(new DataProcessBean(ib));
                            }

                            isFail = false; //只要企微有一个成功就返回成功  3/1 和安驰确认

                            JSONArray fail_list = add_msg_template.getJSONArray("fail_list");
                            if (fail_list!=null){
                                String[] columns = {"EID","GPMSGID","MSGID","FAILID"};
                                for (int i=0; i<fail_list.length(); i++) {
                                    //新增 DCP_ISVWECOM_GPMSG_ID_FAIL 记录企微返回的无效或无法发送的external_userid或chatid列表
                                    {
                                        InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_ID_FAIL", columns);
                                        DataValue[] insValue = new DataValue[]{
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(gpMsgId, Types.VARCHAR),
                                                new DataValue(msgId, Types.VARCHAR),
                                                new DataValue(fail_list.get(i).toString(), Types.VARCHAR),
                                        };
                                        ib.addValues(insValue);
                                        this.addProcessData(new DataProcessBean(ib));
                                    }
                                }
                            }
                        }
                    }

                }
                if (isFail){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业群发消息失败: 调企微接口异常,请参阅日志 ");
                }


                //新增 DCP_ISVWECOM_GPMSG_GP
                {
                    String[] columns = {"EID","GPMSGID","CHATID"};
                    for (Chat chat : chatList) {
                        InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG_GP", columns);
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(gpMsgId, Types.VARCHAR),
                                new DataValue(chat.getChatId(), Types.VARCHAR),
                        };
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                    }
                }


            }



            //新增 DCP_ISVWECOM_GPMSG
            {
                String[] columns = {"EID","GPMSGID","NAME","REMARK","MSG","TYPE","SENDTYPE","STATUS","CREATETIME"};
                InsBean ib = new InsBean("DCP_ISVWECOM_GPMSG", columns);
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(gpMsgId, Types.VARCHAR),
                        new DataValue(req.getRequest().getName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getRemark(), Types.VARCHAR),
                        new DataValue(req.getRequest().getMsg(), Types.VARCHAR),
                        new DataValue(type, Types.VARCHAR),
                        new DataValue(sendType, Types.VARCHAR),
                        new DataValue("100", Types.VARCHAR),   //状态-1未启用0已禁用(群发停止)100启用
                        new DataValue(sDate, Types.DATE),
                };
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGpMsgCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGpMsgCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGpMsgCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpMsgCreateReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            List<Tag> tagList = req.getRequest().getTagList();
            List<User> userList = req.getRequest().getUserList();
            List<Chat> chatList = req.getRequest().getChatList();
            List<Annex> annexList = req.getRequest().getAnnexList();

            if (CollectionUtil.isNotEmpty(tagList)){
                if (tagList.size()>100){
                    errMsg.append("tagList不能超过100,");  //企微是每组不能超过100，此处故意控制总数不超过100
                    isFail = true;
                }
                for (Tag tag:tagList){
                    if (!PosPub.isNumeric(tag.getItem())){
                        errMsg.append("item不能为空或非数字,");
                        isFail = true;
                    }
                    if (Check.Null(tag.getTagId())){
                        errMsg.append("tagId不能为空,");
                        isFail = true;
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(userList)){
                for (User user:userList){
                    if (Check.Null(user.getUserId())){
                        errMsg.append("userId不能为空,");
                        isFail = true;
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(chatList)){
                if (chatList.size()>2000){
                    errMsg.append("chatList不能超过2000,");
                    isFail = true;
                }
                for (Chat chat:chatList){
                    if (Check.Null(chat.getChatId())){
                        errMsg.append("chatId不能为空,");
                        isFail = true;
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(annexList)){
                if (annexList.size()>9){
                    errMsg.append("annexList不能超过9,");
                    isFail = true;
                }
                for (Annex annex:annexList){
                    if (Check.Null(annex.getMsgType())){
                        errMsg.append("msgType不能为空,");
                        isFail = true;
                    }
                    if (Check.Null(annex.getMsgId())){
                        errMsg.append("msgId不能为空,");
                        isFail = true;
                    }
                }
            }


            if (Check.Null(req.getRequest().getName())) {
                errMsg.append("name不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getType())) {
                errMsg.append("type不能为空,");
                isFail = true;
            }
            if ("2".equals(req.getRequest().getType())){
                if (CollectionUtil.isEmpty(chatList)){
                    errMsg.append("chatList不能为空,");
                    isFail = true;
                }
            }
            if (!"0".equals(req.getRequest().getSendType())) {
                if ("1".equals(req.getRequest().getType())) {
                    if (CollectionUtil.isEmpty(tagList) && CollectionUtil.isEmpty(userList)) {
                        errMsg.append("tagList和userList不能同时为空,");
                        isFail = true;
                    }
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_ISVWeComGpMsgCreateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpMsgCreateReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpMsgCreateRes getResponseType() {
        return new DCP_ISVWeComGpMsgCreateRes();
    }
}
