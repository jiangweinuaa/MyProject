package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaMiniCreateReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaMiniCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_ISVWeComMediaMiniCreate
 * 服务说明：小程序页面路径创建
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaMiniCreate extends SPosAdvanceService<DCP_ISVWeComMediaMiniCreateReq, DCP_ISVWeComMediaMiniCreateRes> {

    @Override
    protected void processDUID(DCP_ISVWeComMediaMiniCreateReq req, DCP_ISVWeComMediaMiniCreateRes res) throws Exception {

        try {
            String eId = req.geteId();
            String appId = req.getRequest().getAppId();
            String miniUrl = req.getRequest().getMiniUrl();
            String mediaId = req.getRequest().getMediaId();
            //控制eid+appid+miniurl唯一
            {
                String sql = " select miniurl from dcp_isvwecom_media_mini where eid='"+eId+"' and miniurl='"+miniUrl+"' and appid='"+appId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "资料已存在,无法新增 ");
                }
            }
            //mediaId和企微永久链接URL 是否存在
            String wecomPicUrl = "";
            {
                String sql = " select wecompicurl from crm_media where mediaid='"+mediaId+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "mediaId不存在,无法新增 ");
                }
                wecomPicUrl = getQData.get(0).get("WECOMPICURL").toString();

                if (Check.Null(wecomPicUrl)){

                    DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                    File file = new File("D://resource//image//" + mediaId);
                    wecomPicUrl = dcpWeComUtils.upLoadImg(dao,file);
                    if (Check.Null(wecomPicUrl)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企微上传图片失败 ");
                    }

                    //回写crm_media表
                    UptBean ub = new UptBean("CRM_MEDIA");
                    ub.addUpdateValue("WECOMPICURL", new DataValue(wecomPicUrl, Types.VARCHAR));
                    ub.addCondition("MEDIAID", new DataValue(mediaId, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub));

                }

            }


            //新增 DCP_ISVWECOM_MEDIA_MINI
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String miniId = UUID.randomUUID().toString().replace("-", "");

            String[] columns = {"EID","MINIID","TITLE","APPID","MINIURL","MEDIAID","CREATETIME"};

            InsBean ib = new InsBean("DCP_ISVWECOM_MEDIA_MINI", columns);
            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(miniId, Types.VARCHAR),
                    new DataValue(req.getRequest().getTitle(), Types.VARCHAR),
                    new DataValue(appId, Types.VARCHAR),
                    new DataValue(miniUrl, Types.VARCHAR),
                    new DataValue(mediaId, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),
            };
            ib.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMediaMiniCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMediaMiniCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMediaMiniCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaMiniCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getAppId())){
                errMsg.append("小程序appId不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getTitle())){
                errMsg.append("名称title不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getMiniUrl())){
                errMsg.append("页面路径miniUrl不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getMediaId())){
                errMsg.append("封面图片mediaId不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_ISVWeComMediaMiniCreateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaMiniCreateReq>(){};
    }

    @Override
    protected DCP_ISVWeComMediaMiniCreateRes getResponseType() {
        return new DCP_ISVWeComMediaMiniCreateRes();
    }
}

