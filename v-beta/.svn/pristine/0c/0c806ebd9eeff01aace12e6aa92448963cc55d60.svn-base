package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMomentCreateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMomentCreateReq.*;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMomentCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.AddMomentTask;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComMomentCreate
 * 服务说明：朋友圈任务创建
 * @author jinzma
 * @since  2024-03-06
 */
public class DCP_ISVWeComMomentCreate extends SPosAdvanceService<DCP_ISVWeComMomentCreateReq, DCP_ISVWeComMomentCreateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMomentCreateReq req, DCP_ISVWeComMomentCreateRes res) throws Exception {


        try {
            String eId = req.geteId();
            String type = req.getRequest().getType();         //类型: 0所有客户 1指定客户

            List<User> userList = req.getRequest().getUserList();
            List<Tag> tagList = req.getRequest().getTagList();
            List<Annex> annexList = req.getRequest().getAnnexList();

            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String momentMsgId = PosPub.getGUID(false);


            //资料检查
            {
                String sql = "select name from dcp_isvwecom_moment where eid='" + eId + "' and name='" + req.getRequest().getName() + "'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "名称已存在,无法新增");
                }
            }

            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            AddMomentTask addMomentTask = new AddMomentTask();

            AddMomentTask.Text text = addMomentTask.new Text();
            text.setContent(req.getRequest().getMsg());
            addMomentTask.setText(text);

            addMomentTask.setAttachments(new ArrayList<>());
            if (CollectionUtil.isNotEmpty(annexList)) {
                //annexList 再做一次资料检查
                String msgType = annexList.get(0).getMsgType();   //或者图片或者链接或者视频 ，此处企微管控只能三选一
                switch (msgType) {
                    case "image":
                        if (annexList.size() > 9) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "图片消息附件 最多支持传入9个");
                        }
                        break;
                    case "link":
                        if (annexList.size() > 1) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "链接消息附件 只支持1个");
                        }
                        break;
                    case "video":
                        //暂时不支持
                        break;
                }

                //图片地址
                String ISHTTPS = PosPub.getPARA_SMS(dao,eId,"","ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(dao,eId,"","DomainName");
                String imagePath = httpStr + DomainName + "/resource/image/";
                if (DomainName.endsWith("/")) {
                    imagePath = httpStr + DomainName + "resource/image/";
                }

                int i = 1;    // 项次
                for (Annex annex : annexList) {
                    AddMomentTask.Attachment attachment = addMomentTask.new Attachment();
                    attachment.setMsgtype(msgType);

                    String msgId = annex.getMsgId();
                    switch (msgType) {
                        case "image": {

                            String sql = "select wecompicurl from crm_media where mediaid='" + msgId + "' ";
                            List<Map<String, Object>> getImageQData = dao.executeQuerySQL(sql, null);
                            if (CollectionUtil.isEmpty(getImageQData)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "msgId" + msgId + "在素材库中不存在");
                            }

                            File file = new File("D://resource//image//" + msgId);
                            if (!file.exists()){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "文件在目录中不存在 文件名: "+msgId);
                            }
                            //把图片上传到企微素材库，获取media_id
                            String pic_media_id = dcpWeComUtils.upload_attachment(dao, "image","1", file);

                            if (Check.Null(pic_media_id)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "图片上传企微素材库异常");
                            }

                            //新增 DCP_ISVWECOM_MOMENT_ANNEX
                            String[] columns = {"EID","MOMENTMSGID","SERIALNO","MSGTYPE","MSGID",
                                    "IMAGEWECOMPICURL","IMAGEMEDIAID","IMAGEPICMEDIAID"
                            };
                            InsBean ib = new InsBean("DCP_ISVWECOM_MOMENT_ANNEX", columns);
                            DataValue[] insValue = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(momentMsgId, Types.VARCHAR),
                                    new DataValue(i, Types.VARCHAR),
                                    new DataValue(msgType, Types.VARCHAR),
                                    new DataValue(msgId, Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(msgId, Types.VARCHAR),
                                    new DataValue(pic_media_id, Types.VARCHAR),
                            };
                            ib.addValues(insValue);
                            this.addProcessData(new DataProcessBean(ib));

                            AddMomentTask.Image image = addMomentTask.new Image();
                            image.setMedia_id(pic_media_id);
                            attachment.setImage(image);
                        }

                        break;
                        case "link":{
                            String sql = " select * from dcp_isvwecom_media_link where eid='" + eId + "' and linkid='" + msgId + "' ";
                            List<Map<String, Object>> getLinkQData = this.doQueryData(sql, null);
                            if (CollectionUtil.isEmpty(getLinkQData)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "msgId" + msgId + "在素材库中不存在");
                            }

                            String mediaId = getLinkQData.get(0).get("MEDIAID").toString();
                            String pic_media_id = "";
                            if (!Check.Null(mediaId)){
                                File file = new File("D://resource//image//" + mediaId);
                                if (!file.exists()){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "文件在目录中不存在 文件名: "+mediaId);
                                }
                                //把图片上传到企微素材库，获取media_id
                                pic_media_id = dcpWeComUtils.upload_attachment(dao, "image","1", file);
                                if (Check.Null(pic_media_id)) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "图片上传企微素材库异常");
                                }
                            }


                            String title = getLinkQData.get(0).get("TITLE").toString();
                            //图文消息标题，最多64个字节
                            if (!Check.Null(title)) {
                                if (title.getBytes(StandardCharsets.UTF_8).length>64){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "图文消息标题,最多64个字节");
                                }
                            }

                            String linkMediaUrl = "";
                            if (!Check.Null(mediaId)){
                                linkMediaUrl =  imagePath+mediaId;
                            }


                            //新增 DCP_ISVWECOM_MOMENT_ANNEX
                            String[] columns = {"EID","MOMENTMSGID","SERIALNO","MSGTYPE","MSGID",
                                    "LINKDESCRIPTION", "LINKURL","LINKMEDIAID","LINKMEDIAURL","LINKTITLE","LINKPICMEDIAID"
                            };
                            InsBean ib = new InsBean("DCP_ISVWECOM_MOMENT_ANNEX", columns);
                            DataValue[] insValue = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(momentMsgId, Types.VARCHAR),
                                    new DataValue(i, Types.VARCHAR),
                                    new DataValue(msgType, Types.VARCHAR),
                                    new DataValue(msgId, Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(getLinkQData.get(0).get("LINKURL").toString(), Types.VARCHAR),
                                    new DataValue(mediaId, Types.VARCHAR),
                                    new DataValue(linkMediaUrl, Types.VARCHAR),
                                    new DataValue(title, Types.VARCHAR),
                                    new DataValue(pic_media_id, Types.VARCHAR),
                            };
                            ib.addValues(insValue);
                            this.addProcessData(new DataProcessBean(ib));


                            AddMomentTask.Link link = addMomentTask.new Link();
                            link.setTitle(title);
                            link.setMedia_id(pic_media_id);
                            link.setUrl(getLinkQData.get(0).get("LINKURL").toString());
                            attachment.setLink(link);
                        }

                        break;
                    }

                    addMomentTask.getAttachments().add(attachment);

                    i++;
                }

            }

            AddMomentTask.VisibleRange visibleRange = addMomentTask.new VisibleRange();
            if (!"0".equals(type)) {
                if (CollectionUtil.isNotEmpty(userList)) {
                    AddMomentTask.SenderList senderList = addMomentTask.new SenderList();

                    String[] user_list = new String[userList.size()];
                    for (int i = 0; i < userList.size(); i++) {
                        user_list[i] = userList.get(i).getUserId();
                    }

                    senderList.setUser_list(user_list);
                    visibleRange.setSender_list(senderList);
                }

                if (CollectionUtil.isNotEmpty(tagList)) {
                    AddMomentTask.ExternalContactList externalContactList = addMomentTask.new ExternalContactList();
                    String[] tag_list = new String[tagList.size()];
                    for (int i = 0; i < tagList.size(); i++) {
                        tag_list[i] = tagList.get(i).getTagId();
                    }

                    externalContactList.setTag_list(tag_list);
                    visibleRange.setExternal_contact_list(externalContactList);
                }

            }
            addMomentTask.setVisible_range(visibleRange);

            //调企微
            String jobid = dcpWeComUtils.add_moment_task(dao, addMomentTask);
            if (Check.Null(jobid)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,请查阅企微日志查找原因");
            }


            //新增 DCP_ISVWECOM_MOMENT
            {
                String[] columns = {"EID","MOMENTMSGID","NAME","REMARK","STATUS","MOMENTID","JOBID","TYPE","MSG","CREATETIME"};
                InsBean ib = new InsBean("DCP_ISVWECOM_MOMENT", columns);
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(momentMsgId, Types.VARCHAR),
                        new DataValue(req.getRequest().getName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getRemark(), Types.VARCHAR),
                        new DataValue("100", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(jobid, Types.VARCHAR),
                        new DataValue(type, Types.VARCHAR),
                        new DataValue(req.getRequest().getMsg(), Types.VARCHAR),
                        new DataValue(sDate, Types.DATE),
                };
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
            }

            //新增 DCP_ISVWECOM_MOMENT_USER
            if (CollectionUtil.isNotEmpty(userList)) {
                String[] columns = {"EID","MOMENTMSGID","USERID"};
                for (User user:userList){
                    InsBean ib = new InsBean("DCP_ISVWECOM_MOMENT_USER", columns);
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(momentMsgId, Types.VARCHAR),
                            new DataValue(user.getUserId(), Types.VARCHAR),
                    };
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }


            //新增 DCP_ISVWECOM_MOMENT_TAG
            if (CollectionUtil.isNotEmpty(tagList)) {
                String[] columns = {"EID","MOMENTMSGID","TAGID"};
                for (Tag tag:tagList){
                    InsBean ib = new InsBean("DCP_ISVWECOM_MOMENT_TAG", columns);
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(momentMsgId, Types.VARCHAR),
                            new DataValue(tag.getTagId(), Types.VARCHAR),
                    };
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMomentCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMomentCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMomentCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMomentCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            List<User> userList = req.getRequest().getUserList();
            List<Tag> tagList = req.getRequest().getTagList();
            List<Annex> annexList = req.getRequest().getAnnexList();

            if (Check.Null(req.getRequest().getName())) {
                errMsg.append("name不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getType())) {
                errMsg.append("type不能为空,");
                isFail = true;
            }
            if (!Check.Null(req.getRequest().getMsg())) {
                if (req.getRequest().getMsg().length()>2000) {
                    errMsg.append("msg不能超过2000个字符,");
                    isFail = true;
                }
            }else {
                if (CollectionUtil.isEmpty(annexList)) {
                    errMsg.append("annexList和msg不能都为空,");
                    isFail = true;
                }
            }



            if (CollectionUtil.isNotEmpty(userList)) {
                for (User user : userList) {
                    if (Check.Null(user.getUserId())) {
                        errMsg.append("userId不能为空,");
                        isFail = true;
                    }
                }
            }


            if (CollectionUtil.isNotEmpty(tagList)) {
                for (Tag tag : tagList) {
                    if (Check.Null(tag.getTagId())) {
                        errMsg.append("tagId不能为空,");
                        isFail = true;
                    }
                }
            }

            if (CollectionUtil.isNotEmpty(annexList)) {
                String msgType = "";
                for (Annex annex : annexList) {
                    if (Check.Null(annex.getMsgType())) {
                        errMsg.append("msgType不能为空,");
                        isFail = true;
                    }else{
                        //&& !annex.getMsgType().equals("video")
                        if (!annex.getMsgType().equals("image") && !annex.getMsgType().equals("link") ) {
                            errMsg.append("msgType只能是image或link或video,");
                            isFail = true;
                        }
                        if (Check.Null(msgType)){
                            msgType = annex.getMsgType();
                        }else {
                            if (!msgType.equals(annex.getMsgType())){
                                errMsg.append("msgType只能存在一种类型,");
                                isFail = true;
                            }
                        }
                    }
                    if (Check.Null(annex.getMsgId())) {
                        errMsg.append("msgId不能为空,");
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
    protected TypeToken<DCP_ISVWeComMomentCreateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMomentCreateReq>(){};
    }

    @Override
    protected DCP_ISVWeComMomentCreateRes getResponseType() {
        return new DCP_ISVWeComMomentCreateRes();
    }
}
