package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpMsgDeleteReq.GpMsg;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpMsgDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComGpMsgDelete
 * 服务说明：群发消息删除（批量）
 * @author jinzma
 * @since  2024-03-04
 */
public class DCP_ISVWeComGpMsgDelete extends SPosAdvanceService<DCP_ISVWeComGpMsgDeleteReq, DCP_ISVWeComGpMsgDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComGpMsgDeleteReq req, DCP_ISVWeComGpMsgDeleteRes res) throws Exception {

        try{
            String eId = req.geteId();
            List<GpMsg> gpMsgList = req.getRequest().getGpMsgList();
            //资料检查和删除
            for (GpMsg gpMsg:gpMsgList){
                String gpMsgId = gpMsg.getGpMsgId();
                String sql = "select status from dcp_isvwecom_gpmsg where eid='"+eId+"' and gpmsgid='"+gpMsgId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+" 不存在 ");
                }
                String status = getQData.get(0).get("STATUS").toString();
                if (!"0".equals(status)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "群发消息ID "+gpMsgId+"状态启用中或未停止 ");
                }

                //删除 DCP_ISVWECOM_GPMSG
                DelBean db1 = new DelBean("DCP_ISVWECOM_GPMSG");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //删除 DCP_ISVWECOM_GPMSG_GP
                DelBean db2 = new DelBean("DCP_ISVWECOM_GPMSG_GP");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //删除 DCP_ISVWECOM_GPMSG_TAG
                DelBean db3 = new DelBean("DCP_ISVWECOM_GPMSG_TAG");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

                //删除 DCP_ISVWECOM_GPMSG_USER
                DelBean db4 = new DelBean("DCP_ISVWECOM_GPMSG_USER");
                db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db4.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db4));

                //删除 DCP_ISVWECOM_GPMSG_ANNEX
                DelBean db5 = new DelBean("DCP_ISVWECOM_GPMSG_ANNEX");
                db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db5.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db5));

                //删除 DCP_ISVWECOM_GPMSG_ID
                DelBean db6 = new DelBean("DCP_ISVWECOM_GPMSG_ID");
                db6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db6.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db6));

                //删除 DCP_ISVWECOM_GPMSG_ID_FAIL
                DelBean db7 = new DelBean("DCP_ISVWECOM_GPMSG_ID_FAIL");
                db7.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db7.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db7));

                //删除 DCP_ISVWECOM_GPMSG_ID_USER
                DelBean db8 = new DelBean("DCP_ISVWECOM_GPMSG_ID_USER");
                db8.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db8.addCondition("GPMSGID", new DataValue(gpMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db8));

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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGpMsgDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGpMsgDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGpMsgDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpMsgDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            List<GpMsg> gpMsgList = req.getRequest().getGpMsgList();
            if (CollectionUtil.isEmpty(gpMsgList)){
                errMsg.append("gpMsgList不能为空,");
                isFail = true;
            }else{
                for (GpMsg gpMsg:gpMsgList){
                    if (Check.Null(gpMsg.getGpMsgId())){
                        errMsg.append("gpMsgId不能为空,");
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
    protected TypeToken<DCP_ISVWeComGpMsgDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpMsgDeleteReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpMsgDeleteRes getResponseType() {
        return new DCP_ISVWeComGpMsgDeleteRes();
    }
}
