package com.dsc.spos.service.imp.json;
import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaMiniUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaMiniUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComMediaMiniUpdate
 * 服务说明：小程序页面路径更新
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaMiniUpdate extends SPosAdvanceService<DCP_ISVWeComMediaMiniUpdateReq, DCP_ISVWeComMediaMiniUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMediaMiniUpdateReq req, DCP_ISVWeComMediaMiniUpdateRes res) throws Exception {
        try {
            String eId = req.geteId();
            String miniId = req.getRequest().getMiniId();
            String mediaId = req.getRequest().getMediaId();
            {
                String sql = " select miniid from dcp_isvwecom_media_mini where eid='"+eId+"' and miniid='"+miniId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "资料不存在,无法修改 ");
                }
            }

            //mediaId和企微永久链接URL 是否存在
            String wecomPicUrl = "";
            {
                String sql = " select wecompicurl from crm_media where mediaid='"+mediaId+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "mediaId不存在,无法修改 ");
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

            //修改 DCP_ISVWECOM_MEDIA_MINI
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            UptBean ub = new UptBean("DCP_ISVWECOM_MEDIA_MINI");
            ub.addUpdateValue("TITLE", new DataValue(req.getRequest().getTitle(), Types.VARCHAR));
            ub.addUpdateValue("APPID", new DataValue(req.getRequest().getAppId(), Types.VARCHAR));
            ub.addUpdateValue("MINIURL", new DataValue(req.getRequest().getMiniUrl(), Types.VARCHAR));
            ub.addUpdateValue("MEDIAID", new DataValue(mediaId, Types.VARCHAR));
            ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));


            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub.addCondition("MINIID", new DataValue(miniId, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub));


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMediaMiniUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMediaMiniUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMediaMiniUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaMiniUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getMiniId())){
                errMsg.append("miniId不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getAppId())){
                errMsg.append("小程序appId不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getTitle())){
                errMsg.append("标题title不可为空值, ");
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
    protected TypeToken<DCP_ISVWeComMediaMiniUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaMiniUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWeComMediaMiniUpdateRes getResponseType() {
        return new DCP_ISVWeComMediaMiniUpdateRes();
    }
}
