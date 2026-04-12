package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgRemindReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgRemindRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComGpMsgRemind
 * 服务说明：群发消息提醒
 * @author jinzma
 * @since  2024-03-04
 */
public class DCP_ISVWeComGpMsgRemind extends SPosAdvanceService<DCP_ISVWeComGpMsgRemindReq, DCP_ISVWeComGpMsgRemindRes> {
    @Override
    protected void processDUID(DCP_ISVWeComGpMsgRemindReq req, DCP_ISVWeComGpMsgRemindRes res) throws Exception {
        try{
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
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+" 调企微接口异常 ");
            }

            //调企微
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            for (Map<String, Object> oneQData : getQData){
                dcpWeComUtils.remind_groupmsg_send(dao,oneQData.get("MSGID").toString()); //提醒在企微有一些限制，此处只管发送不管返回
            }


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGpMsgRemindReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGpMsgRemindReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGpMsgRemindReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpMsgRemindReq req) throws Exception {
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
    protected TypeToken<DCP_ISVWeComGpMsgRemindReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpMsgRemindReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpMsgRemindRes getResponseType() {
        return new DCP_ISVWeComGpMsgRemindRes();
    }
}
