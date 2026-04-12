package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComWelcomeMsgUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComWelcomeMsgUpdateReq.User;
import com.dsc.spos.json.cust.req.DCP_ISVWeComWelcomeMsgUpdateReq.Annex;
import com.dsc.spos.json.cust.res.DCP_ISVWeComWelcomeMsgUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComWelcomeMsgUpdate
 * 服务说明：个人欢迎语更新
 * @author jinzma
 * @since  2024-02-20
 */
public class DCP_ISVWeComWelcomeMsgUpdate extends SPosAdvanceService<DCP_ISVWeComWelcomeMsgUpdateReq, DCP_ISVWeComWelcomeMsgUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComWelcomeMsgUpdateReq req, DCP_ISVWeComWelcomeMsgUpdateRes res) throws Exception {
        try{

            String eId = req.geteId();
            String welMsgId = req.getRequest().getWelMsgId();
            String restrictStaff = req.getRequest().getRestrictStaff();  //0全部用户  1指定用户
            List<User> userList = req.getRequest().getUserList();
            List<Annex> annexList = req.getRequest().getAnnexList();

            //资料检查
            {
                String sql = " select welmsgid from dcp_isvwecom_welmsg where eid='"+eId+"' and welmsgid='"+welMsgId+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "资料不存在,无法修改 ");
                }
            }
            if (restrictStaff.equals("0")) {
                String sql = " select restrictstaff from dcp_isvwecom_welmsg "
                        + " where eid='"+eId+"' and restrictstaff='0' and welmsgid<>'"+welMsgId+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (!CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在适用全部用户的欢迎语,无法修改");
                }
            }else {
                if (CollectionUtil.isNotEmpty(userList)) {
                    for (User user : userList) {
                        String sql = " select * from dcp_isvwecom_welmsg_staff "
                                + " where eid='"+eId+"' and userid='"+user.getUserId()+"' and welmsgid<>'"+welMsgId+"' ";
                        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                        if (!CollectionUtil.isEmpty(getQData)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微用户:" + user.getUserId() + " 已存在适用的欢迎语,无法再新增");
                        }
                    }
                }
            }

            //删除 DCP_ISVWECOM_WELMSG_STAFF
            DelBean db1 = new DelBean("DCP_ISVWECOM_WELMSG_STAFF");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("WELMSGID", new DataValue(welMsgId,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            //删除 DCP_ISVWECOM_WELMSG_ANNEX
            DelBean db2 = new DelBean("DCP_ISVWECOM_WELMSG_ANNEX");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("WELMSGID", new DataValue(welMsgId,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));


            //修改 DCP_ISVWECOM_WELMSG
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            UptBean ub = new UptBean("DCP_ISVWECOM_WELMSG");

            ub.addUpdateValue("NAME", new DataValue(req.getRequest().getName(), Types.VARCHAR));
            ub.addUpdateValue("REMARK", new DataValue(req.getRequest().getRemark(), Types.VARCHAR));
            ub.addUpdateValue("RESTRICTSTAFF", new DataValue(restrictStaff, Types.VARCHAR));
            ub.addUpdateValue("MSG", new DataValue(req.getRequest().getMsg(), Types.VARCHAR));
            ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));

            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub.addCondition("WELMSGID", new DataValue(welMsgId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub));


            //新增 DCP_ISVWECOM_WELMSG_STAFF
            if (CollectionUtil.isNotEmpty(userList)) {
                String[] columns = {"EID","WELMSGID","USERID"};
                for (User user : userList) {
                    InsBean ib = new InsBean("DCP_ISVWECOM_WELMSG_STAFF", columns);
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(welMsgId, Types.VARCHAR),
                            new DataValue(user.getUserId(), Types.VARCHAR),
                    };
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }

            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();

            //新增 DCP_ISVWECOM_WELMSG_ANNEX
            if (CollectionUtil.isNotEmpty(annexList)) {

                //图片地址
                String ISHTTPS = PosPub.getPARA_SMS(dao,eId,"","ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(dao,eId,"","DomainName");
                String imagePath = httpStr + DomainName + "/resource/image/";
                if (DomainName.endsWith("/")) {
                    imagePath = httpStr + DomainName + "resource/image/";
                }

                int i = 1;
                for (Annex annex : annexList) {
                    String msgType = annex.getMsgType();
                    String msgId = annex.getMsgId();

                    switch (msgType) {
                        case "image": {
                            String sql = "select wecompicurl from crm_media where mediaid='" + msgId + "' ";
                            List<Map<String, Object>> getImageQData = dao.executeQuerySQL(sql, null);
                            if (CollectionUtil.isEmpty(getImageQData)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "msgId" + msgId + "在素材库中不存在");
                            }

                            String wecomPicUrl = getImageQData.get(0).get("WECOMPICURL").toString(); //企微图片永久链接
                            if (Check.Null(wecomPicUrl)) {

                                File file = new File("D://resource//image//" + msgId);
                                wecomPicUrl = dcpWeComUtils.upLoadImg(dao, file);
                                if (Check.Null(wecomPicUrl)) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微上传图片失败 ");
                                }

                                //回写crm_media表
                                UptBean ub1 = new UptBean("CRM_MEDIA");
                                ub1.addUpdateValue("WECOMPICURL", new DataValue(wecomPicUrl, Types.VARCHAR));
                                ub1.addCondition("MEDIAID", new DataValue(msgId, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub1));

                            }

                            //新增 DCP_ISVWECOM_GPMSG_ANNEX
                            {
                                String[] columns = {"EID", "WELMSGID", "SERIALNO", "MSGTYPE", "MSGID",
                                        "IMAGEWECOMPICURL", "IMAGEMEDIAID", "IMAGEPICMEDIAID"
                                };

                                InsBean ib = new InsBean("DCP_ISVWECOM_WELMSG_ANNEX", columns);
                                DataValue[] insValue = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(welMsgId, Types.VARCHAR),
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
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "msgId" + msgId + "在素材库中不存在");
                            }

                            String description = getLinkQData.get(0).get("DESCRIPTION").toString();
                            String linkUrl = getLinkQData.get(0).get("LINKURL").toString();
                            String title = getLinkQData.get(0).get("TITLE").toString();
                            String mediaId = getLinkQData.get(0).get("MEDIAID").toString();
                            String linkMediaUrl = "";
                            if (!Check.Null(mediaId)){
                                linkMediaUrl = imagePath + mediaId;
                            }

                            //新增 DCP_ISVWECOM_GPMSG_ANNEX
                            {
                                String[] columns = {"EID", "WELMSGID", "SERIALNO", "MSGTYPE", "MSGID",
                                        "LINKDESCRIPTION", "LINKURL","LINKMEDIAID","LINKMEDIAURL", "LINKTITLE","LINKPICMEDIAID"
                                };

                                InsBean ib = new InsBean("DCP_ISVWECOM_WELMSG_ANNEX", columns);
                                DataValue[] insValue = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(welMsgId, Types.VARCHAR),
                                        new DataValue(i, Types.VARCHAR),
                                        new DataValue(msgType, Types.VARCHAR),
                                        new DataValue(msgId, Types.VARCHAR),
                                        new DataValue(description, Types.VARCHAR),
                                        new DataValue(linkUrl, Types.VARCHAR),
                                        new DataValue(mediaId, Types.VARCHAR),
                                        new DataValue(linkMediaUrl, Types.VARCHAR),
                                        new DataValue(title, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                };
                                ib.addValues(insValue);
                                this.addProcessData(new DataProcessBean(ib));
                            }


                        }
                        break;
                        case "miniprogram":

                            String sql = "select * from dcp_isvwecom_media_mini where eid='"+eId+"' and miniid='"+msgId+"' ";
                            List<Map<String,Object>> getMiniprogramQData = dao.executeQuerySQL(sql,null);
                            if (CollectionUtil.isNotEmpty(getMiniprogramQData)){
                                String title = getMiniprogramQData.get(0).get("TITLE").toString();
                                String appId = getMiniprogramQData.get(0).get("APPID").toString();
                                String miniUrl = getMiniprogramQData.get(0).get("MINIURL").toString();
                                String mediaId = getMiniprogramQData.get(0).get("MEDIAID").toString();

                                //新增 DCP_ISVWECOM_GPMSG_ANNEX
                                {
                                    String[] columns = {"EID", "WELMSGID", "SERIALNO", "MSGTYPE", "MSGID",
                                            "MINIAPPID","MINIURL","MINIMEDIAID","MINIPICMEDIAID","MINITITLE"
                                    };

                                    InsBean ib = new InsBean("DCP_ISVWECOM_WELMSG_ANNEX", columns);
                                    DataValue[] insValue = new DataValue[]{
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(welMsgId, Types.VARCHAR),
                                            new DataValue(i, Types.VARCHAR),
                                            new DataValue(msgType, Types.VARCHAR),
                                            new DataValue(msgId, Types.VARCHAR),
                                            new DataValue(appId, Types.VARCHAR),
                                            new DataValue(miniUrl, Types.VARCHAR),
                                            new DataValue(mediaId, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),    //欢迎语无法保证3天内，等推送的时候才调用
                                            new DataValue(title, Types.VARCHAR),
                                    };
                                    ib.addValues(insValue);
                                    this.addProcessData(new DataProcessBean(ib));
                                }

                            }

                            break;

                    }

                    i++;
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComWelcomeMsgUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComWelcomeMsgUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComWelcomeMsgUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComWelcomeMsgUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            if (Check.Null(req.getRequest().getName())){
                errMsg.append("name不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getRestrictStaff())){
                errMsg.append("restrictStaff不能为空,");
                isFail = true;
            }else{
                if (!"0".equals(req.getRequest().getRestrictStaff())){
                    if (CollectionUtil.isEmpty(req.getRequest().getUserList())){
                        errMsg.append("userList不能为空,");
                        isFail = true;
                    }
                }
            }
            if (!CollectionUtil.isEmpty(req.getRequest().getAnnexList())){
                for (Annex annex:req.getRequest().getAnnexList()){
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
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComWelcomeMsgUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComWelcomeMsgUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWeComWelcomeMsgUpdateRes getResponseType() {
        return new DCP_ISVWeComWelcomeMsgUpdateRes();
    }
}
