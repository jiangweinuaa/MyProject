package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EnterpriseChatSetUpdateReq;
import com.dsc.spos.json.cust.res.DCP_EnterpriseChatSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_EnterpriseChatSetUpdate
 * 服务说明：企业微信基础设置更新
 *
 * @author wangzyc
 * @since 2020-12-28
 */
public class DCP_EnterpriseChatSetUpdate extends SPosAdvanceService<DCP_EnterpriseChatSetUpdateReq, DCP_EnterpriseChatSetUpdateRes> {

    @Override
    protected void processDUID(DCP_EnterpriseChatSetUpdateReq req, DCP_EnterpriseChatSetUpdateRes res) throws Exception {
        String eId = req.geteId();
        DCP_EnterpriseChatSetUpdateReq.level1Elm request = req.getRequest();

        if(isExist(req)){
            UptBean ub1 = new UptBean("DCP_ENTERPRISECHATSET");
            ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));

            ub1.addUpdateValue("CORPID",new DataValue(request.getCorpId(), Types.VARCHAR));
            ub1.addUpdateValue("AGENTID",new DataValue(request.getAgentId(), Types.VARCHAR));
            ub1.addUpdateValue("SECRET",new DataValue(request.getSecret(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub1));

        }else{
            String[] enterprisechatset = {
                    "EID", "CORPID", "AGENTID", "SECRET"
            };
            DataValue[] detailVal = null;
            detailVal = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(request.getCorpId(), Types.VARCHAR),
                    new DataValue(request.getAgentId(), Types.VARCHAR),
                    new DataValue(request.getSecret(), Types.VARCHAR)
            };

            InsBean ib1 = new InsBean("DCP_ENTERPRISECHATSET", enterprisechatset);
            ib1.addValues(detailVal);
            this.addProcessData(new DataProcessBean(ib1));
        }
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_EnterpriseChatSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EnterpriseChatSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EnterpriseChatSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EnterpriseChatSetUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_EnterpriseChatSetUpdateReq.level1Elm request = req.getRequest();

        if(Check.Null(request.getCorpId())){
            errMsg.append("企业id不能为空值 ");
            isFail = true;
        }
        if(Check.Null(request.getAgentId())){
            errMsg.append("应用id不能为空值 ");
            isFail = true;
        }

        if(Check.Null(request.getSecret())){
            errMsg.append("秘钥不能为空值 ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_EnterpriseChatSetUpdateReq> getRequestType() {
        return new TypeToken<DCP_EnterpriseChatSetUpdateReq>(){};
    }

    @Override
    protected DCP_EnterpriseChatSetUpdateRes getResponseType() {
        return new DCP_EnterpriseChatSetUpdateRes();
    }

    /**
     * 验证企业ID 应用ID 是否存在
     * @param req
     * @return
     * @throws Exception
     */
    private Boolean isExist(DCP_EnterpriseChatSetUpdateReq req) throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * FROM DCP_ENTERPRISECHATSET where EID = '"+req.geteId()+"' ");
        sql = sqlbuf.toString();
        List<Map<String, Object>> mapList = this.doQueryData(sql, null);
        if(mapList!=null&&mapList.isEmpty()==false)
        {
            return true;
        }
        return false;
    }
}
