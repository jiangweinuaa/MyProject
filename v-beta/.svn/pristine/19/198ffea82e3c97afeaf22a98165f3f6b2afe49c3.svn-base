package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaLinkUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaLinkUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComMediaLinkUpdate
 * 服务说明：网页链接更新
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaLinkUpdate extends SPosAdvanceService<DCP_ISVWeComMediaLinkUpdateReq, DCP_ISVWeComMediaLinkUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMediaLinkUpdateReq req, DCP_ISVWeComMediaLinkUpdateRes res) throws Exception {

        try{
            String eId = req.geteId();
            String linkId = req.getRequest().getLinkId();
            String mediaId = req.getRequest().getMediaId();
            {
                String sql = " select linkid from dcp_isvwecom_media_link where eid='"+eId+"' and linkid='"+linkId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "资料不存在,无法修改 ");
                }
            }

            //mediaId是否存在
            {
                String sql = " select wecompicurl from crm_media where mediaid='"+mediaId+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "mediaId不存在,无法修改 ");
                }
            }


            //修改 DCP_ISVWECOM_MEDIA_LINK
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            UptBean ub = new UptBean("DCP_ISVWECOM_MEDIA_LINK");
            ub.addUpdateValue("TITLE", new DataValue(req.getRequest().getTitle(), Types.VARCHAR));
            ub.addUpdateValue("DESCRIPTION", new DataValue(req.getRequest().getDesc(), Types.VARCHAR));
            ub.addUpdateValue("LINKURL", new DataValue(req.getRequest().getLinkUrl(), Types.VARCHAR));
            ub.addUpdateValue("MEDIAID", new DataValue(mediaId, Types.VARCHAR));
            ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));


            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub.addCondition("LINKID", new DataValue(linkId, Types.VARCHAR));

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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMediaLinkUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMediaLinkUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMediaLinkUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaLinkUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getLinkId())){
                errMsg.append("linkId不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getTitle())){
                errMsg.append("title不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getLinkUrl())){
                errMsg.append("linkUrl不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getMediaId())){
                errMsg.append("mediaId不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComMediaLinkUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaLinkUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWeComMediaLinkUpdateRes getResponseType() {
        return new DCP_ISVWeComMediaLinkUpdateRes();
    }
}
