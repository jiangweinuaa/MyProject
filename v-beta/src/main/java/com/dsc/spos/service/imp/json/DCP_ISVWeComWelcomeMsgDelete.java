package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComWelcomeMsgDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComWelcomeMsgDeleteReq.WelMsg;
import com.dsc.spos.json.cust.res.DCP_ISVWeComWelcomeMsgDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_ISVWeComWelcomeMsgDelete
 * 服务说明：个人欢迎语删除
 * @author jinzma
 * @since  2024-02-20
 */
public class DCP_ISVWeComWelcomeMsgDelete extends SPosAdvanceService<DCP_ISVWeComWelcomeMsgDeleteReq, DCP_ISVWeComWelcomeMsgDeleteRes> {
    @Override
    protected void processDUID(DCP_ISVWeComWelcomeMsgDeleteReq req, DCP_ISVWeComWelcomeMsgDeleteRes res) throws Exception {

        try {
            String eId = req.geteId();
            List<WelMsg> welMsgList = req.getRequest().getWelMsgList();
            for (WelMsg welMsg:welMsgList){
                String welMsgId = welMsg.getWelMsgId();

                //删除 DCP_ISVWECOM_WELMSG_STAFF
                DelBean db1 = new DelBean("DCP_ISVWECOM_WELMSG_STAFF");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("WELMSGID", new DataValue(welMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //删除 DCP_ISVWECOM_WELMSG_ANNEX
                DelBean db2 = new DelBean("DCP_ISVWECOM_WELMSG_ANNEX");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("WELMSGID", new DataValue(welMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //删除 DCP_ISVWECOM_WELMSG
                DelBean db3 = new DelBean("DCP_ISVWECOM_WELMSG");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("WELMSGID", new DataValue(welMsgId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComWelcomeMsgDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComWelcomeMsgDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComWelcomeMsgDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComWelcomeMsgDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            List<WelMsg> welMsgList = req.getRequest().getWelMsgList();
            if (CollectionUtil.isEmpty(welMsgList)){
                errMsg.append("welMsgList不能为空,");
                isFail = true;
            }else{
                for (WelMsg welMsg:welMsgList){
                    if (Check.Null(welMsg.getWelMsgId())){
                        errMsg.append("welMsgId不能为空,");
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
    protected TypeToken<DCP_ISVWeComWelcomeMsgDeleteReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComWelcomeMsgDeleteReq>(){};
    }

    @Override
    protected DCP_ISVWeComWelcomeMsgDeleteRes getResponseType() {
        return new DCP_ISVWeComWelcomeMsgDeleteRes();
    }
}
