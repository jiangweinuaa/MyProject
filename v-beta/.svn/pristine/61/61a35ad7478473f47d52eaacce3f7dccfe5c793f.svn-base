package com.dsc.spos.service.imp.json;
import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaMiniDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaMiniDeleteReq.Mini;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaMiniDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComMediaMiniDelete
 * 服务说明：小程序页面路径删除
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaMiniDelete extends SPosAdvanceService<DCP_ISVWeComMediaMiniDeleteReq, DCP_ISVWeComMediaMiniDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMediaMiniDeleteReq req, DCP_ISVWeComMediaMiniDeleteRes res) throws Exception {
        try {
            String eId = req.geteId();
            List<Mini> miniIdList = req.getRequest().getMiniIdList();

            for (Mini mini:miniIdList){
                //删除 DCP_ISVWECOM_MEDIA_MINI
                DelBean delBean = new DelBean("DCP_ISVWECOM_MEDIA_MINI");
                delBean.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                delBean.addCondition("MINIID", new DataValue(mini.getMiniId(), Types.VARCHAR));

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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMediaMiniDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMediaMiniDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMediaMiniDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaMiniDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else {
            List<Mini>miniIdList = req.getRequest().getMiniIdList();
            if (CollectionUtil.isEmpty(miniIdList)){
                errMsg.append("miniIdList不可为空值, ");
                isFail = true;
            }else {
                for (Mini mini:miniIdList){
                    if (Check.Null(mini.getMiniId())){
                        errMsg.append("miniId不可为空值, ");
                        isFail = true;
                    }
                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                };
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_ISVWeComMediaMiniDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaMiniDeleteReq>(){};
    }

    @Override
    protected DCP_ISVWeComMediaMiniDeleteRes getResponseType() {
        return new DCP_ISVWeComMediaMiniDeleteRes();
    }
}
