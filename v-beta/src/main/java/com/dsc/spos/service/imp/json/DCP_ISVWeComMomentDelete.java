package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMomentDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMomentDeleteReq.MomentMsg;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMomentDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_ISVWeComMomentDelete
 * 服务说明：朋友圈任务删除
 * @author jinzma
 * @since  2024-03-06
 */
public class DCP_ISVWeComMomentDelete extends SPosAdvanceService<DCP_ISVWeComMomentDeleteReq, DCP_ISVWeComMomentDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMomentDeleteReq req, DCP_ISVWeComMomentDeleteRes res) throws Exception {
        try {
            String eId = req.geteId();
            List<MomentMsg> momentMsgList = req.getRequest().getMomentMsgList();
            for (MomentMsg momentMsg:momentMsgList){

                String momentMsgId = momentMsg.getMomentMsgId();
                String sql = "select status from dcp_isvwecom_moment where eid='"+eId+"' and momentmsgid='"+momentMsgId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务ID "+momentMsgId+" 不存在 ");
                }
                String status = getQData.get(0).get("STATUS").toString();
                if (!"0".equals(status)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务ID "+momentMsgId+"状态启用中或未停止 ");
                }

                //删除 DCP_ISVWECOM_MOMENT
                DelBean db1 = new DelBean("DCP_ISVWECOM_MOMENT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("MOMENTMSGID", new DataValue(momentMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //删除 DCP_ISVWECOM_MOMENT_USER
                DelBean db2 = new DelBean("DCP_ISVWECOM_MOMENT_USER");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("MOMENTMSGID", new DataValue(momentMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //删除 DCP_ISVWECOM_MOMENT_TAG
                DelBean db3 = new DelBean("DCP_ISVWECOM_MOMENT_TAG");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("MOMENTMSGID", new DataValue(momentMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

                //删除 DCP_ISVWECOM_MOMENT_ANNEX
                DelBean db4 = new DelBean("DCP_ISVWECOM_MOMENT_ANNEX");
                db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db4.addCondition("MOMENTMSGID", new DataValue(momentMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db4));

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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMomentDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMomentDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMomentDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMomentDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            List<MomentMsg> momentMsgList = req.getRequest().getMomentMsgList();
            if (CollectionUtil.isEmpty(momentMsgList)){
                errMsg.append("momentMsgList不能为空,");
                isFail = true;
            }else{
                for (MomentMsg momentMsg:momentMsgList){
                    if (Check.Null(momentMsg.getMomentMsgId())){
                        errMsg.append("momentMsgId不能为空,");
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
    protected TypeToken<DCP_ISVWeComMomentDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMomentDeleteReq>(){};
    }

    @Override
    protected DCP_ISVWeComMomentDeleteRes getResponseType() {
        return new DCP_ISVWeComMomentDeleteRes();
    }
}
