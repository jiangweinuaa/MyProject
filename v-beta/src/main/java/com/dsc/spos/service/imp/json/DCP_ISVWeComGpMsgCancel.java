package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgCancelReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgCancelRes;
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
 * 服务函数：DCP_ISVWeComGpMsgCancel
 * 服务说明：群发消息停止
 * @author jinzma
 * @since  2024-03-04
 */
public class DCP_ISVWeComGpMsgCancel extends SPosAdvanceService<DCP_ISVWeComGpMsgCancelReq, DCP_ISVWeComGpMsgCancelRes> {
    @Override
    protected void processDUID(DCP_ISVWeComGpMsgCancelReq req, DCP_ISVWeComGpMsgCancelRes res) throws Exception {

        try {

            String eId = req.geteId();
            String gpMsgId = req.getRequest().getGpMsgId();
            //资料检查
            {
                String sql = "select status from dcp_isvwecom_gpmsg where eid='"+eId+"' and gpmsgid='"+gpMsgId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+" 不存在 ");
                }
                String status = getQData.get(0).get("STATUS").toString();
                if (!"100".equals(status)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息状态未启用或已停止 ");
                }
            }

            String sql = " select msgid from dcp_isvwecom_gpmsg_id where eid='"+eId+"' and gpmsgid='"+gpMsgId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+" 调企微接口异常 "); //这个表没有资料说明之前调用有异常，
            }

            //调企微
            boolean isFale=false;
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            for (Map<String, Object> oneQData : getQData){
                if (!dcpWeComUtils.cancel_groupmsg_send(dao,oneQData.get("MSGID").toString())){
                    isFale = true;
                }
            }

            if (!isFale){
                //修改 DCP_ISVWECOM_GPMSG
                String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                UptBean ub = new UptBean("DCP_ISVWECOM_GPMSG");

                ub.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));

                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("GPMSGID", new DataValue(gpMsgId, Types.VARCHAR));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGpMsgCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGpMsgCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGpMsgCancelReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpMsgCancelReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getGpMsgId())){
                errMsg.append("gpMsgId不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComGpMsgCancelReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpMsgCancelReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpMsgCancelRes getResponseType() {
        return new DCP_ISVWeComGpMsgCancelRes();
    }
}
