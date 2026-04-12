package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaLinkCreateReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaLinkCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_ISVWeComMediaLinkCreate
 * 服务说明：网页链接创建
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaLinkCreate extends SPosAdvanceService<DCP_ISVWeComMediaLinkCreateReq, DCP_ISVWeComMediaLinkCreateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMediaLinkCreateReq req, DCP_ISVWeComMediaLinkCreateRes res) throws Exception {

        try{
            String eId = req.geteId();
            String linkUrl = req.getRequest().getLinkUrl();
            String mediaId = req.getRequest().getMediaId();
            //控制eid+pageUrl 唯一
            {
                String sql = " select mediaid from dcp_isvwecom_media_link where eid='"+eId+"' and linkurl='"+linkUrl+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "linkurl已存在,无法新增 ");
                }
            }
            //mediaId是否存在
            {
                String sql = " select wecompicurl from crm_media where mediaid='"+mediaId+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "mediaId不存在,无法新增 ");
                }
            }


            //新增 DCP_ISVWECOM_MEDIA_LINK
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String linkUrlId = UUID.randomUUID().toString().replace("-", "");

            String[] columns = {"EID","LINKID","DESCRIPTION","LINKURL","MEDIAID","CREATETIME","TITLE"};

            InsBean ib = new InsBean("DCP_ISVWECOM_MEDIA_LINK", columns);
            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(linkUrlId, Types.VARCHAR),
                    new DataValue(req.getRequest().getDesc(), Types.VARCHAR),
                    new DataValue(linkUrl, Types.VARCHAR),
                    new DataValue(mediaId, Types.VARCHAR),
                    new DataValue(sDate, Types.DATE),
                    new DataValue(req.getRequest().getTitle(), Types.VARCHAR),
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMediaLinkCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMediaLinkCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMediaLinkCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaLinkCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getTitle())){
                errMsg.append("标题title不可为空值, ");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getLinkUrl())){
                errMsg.append("linkUrl不可为空值, ");
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
    protected TypeToken<DCP_ISVWeComMediaLinkCreateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaLinkCreateReq>(){};
    }

    @Override
    protected DCP_ISVWeComMediaLinkCreateRes getResponseType() {
        return new DCP_ISVWeComMediaLinkCreateRes();
    }
}
