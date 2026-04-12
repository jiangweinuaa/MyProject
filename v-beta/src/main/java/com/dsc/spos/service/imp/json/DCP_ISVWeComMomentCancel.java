package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMomentCancelReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMomentCancelRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComMomentCancel
 * 服务说明：朋友圈任务停发
 * @author jinzma
 * @since  2024-03-06
 */
public class DCP_ISVWeComMomentCancel extends SPosAdvanceService<DCP_ISVWeComMomentCancelReq, DCP_ISVWeComMomentCancelRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMomentCancelReq req, DCP_ISVWeComMomentCancelRes res) throws Exception {

        try{
            String eId = req.geteId();
            String momentMsgId = req.getRequest().getMomentMsgId();
            String momentId;
            //资料检查
            {
                String sql = "select status,momentid from dcp_isvwecom_moment where eid='"+eId+"' and momentmsgid='"+momentMsgId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务ID "+momentMsgId+" 不存在 ");
                }
                String status = getQData.get(0).get("STATUS").toString();
                if (!"100".equals(status)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务状态未启用或已停止 ");
                }

                momentId = getQData.get(0).get("MOMENTID").toString();
                if (Check.Null(momentId)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务获取momentId失败 ");
                }
            }

            //调企微
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();

            if (!dcpWeComUtils.cancel_moment_task(dao,momentId)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务停止调用企微接口失败 ");
            }


            {
                //修改 DCP_ISVWECOM_MOMENT
                String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                UptBean ub = new UptBean("DCP_ISVWECOM_MOMENT");

                ub.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));

                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("MOMENTMSGID", new DataValue(momentMsgId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));

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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMomentCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMomentCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMomentCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMomentCancelReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getMomentMsgId())){
                errMsg.append("momentMsgId不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComMomentCancelReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMomentCancelReq>(){};
    }

    @Override
    protected DCP_ISVWeComMomentCancelRes getResponseType() {
        return new DCP_ISVWeComMomentCancelRes();
    }
}
