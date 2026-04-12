package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaLinkDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaLinkDeleteReq.Link;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaLinkDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComMediaLinkDelete
 * 服务说明：网页链接删除
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaLinkDelete extends SPosAdvanceService<DCP_ISVWeComMediaLinkDeleteReq, DCP_ISVWeComMediaLinkDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMediaLinkDeleteReq req, DCP_ISVWeComMediaLinkDeleteRes res) throws Exception {

        try{

            String eId = req.geteId();
            List<Link> linkIdList = req.getRequest().getLinkIdList();

            for (Link par:linkIdList){
                //删除 DCP_ISVWECOM_MEDIA_LINK
                DelBean delBean = new DelBean("DCP_ISVWECOM_MEDIA_LINK");
                delBean.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                delBean.addCondition("LINKID", new DataValue(par.getLinkId(), Types.VARCHAR));

                this.addProcessData(new DataProcessBean(delBean));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMediaLinkDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMediaLinkDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMediaLinkDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaLinkDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            List<Link>linkIdList = req.getRequest().getLinkIdList();
            if (CollectionUtil.isEmpty(linkIdList)){
                errMsg.append("linkIdList不可为空值, ");
                isFail = true;
            }else {
                for (Link par:linkIdList){
                    if (Check.Null(par.getLinkId())){
                        errMsg.append("linkId不可为空值, ");
                        isFail = true;
                    }
                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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
    protected TypeToken<DCP_ISVWeComMediaLinkDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaLinkDeleteReq>(){};
    }

    @Override
    protected DCP_ISVWeComMediaLinkDeleteRes getResponseType() {
        return new DCP_ISVWeComMediaLinkDeleteRes();
    }
}

