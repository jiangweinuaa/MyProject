package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FaPiaoProjEnableReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoProjEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_FaPiaoProjEnable extends SPosAdvanceService<DCP_FaPiaoProjEnableReq, DCP_FaPiaoProjEnableRes> {
    @Override
    protected void processDUID(DCP_FaPiaoProjEnableReq req, DCP_FaPiaoProjEnableRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO(); // 创建/修改人
        String opName = req.getOpName(); // 创建/修改人名称
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 创建/修改人时间

        DCP_FaPiaoProjEnableReq.levelRequest request = req.getRequest();
        String status = "100";//状态：-1未启用100已启用 0已禁用

        if(request.getOprType().equals("1"))//操作类型：1-启用2-禁用
        {
            status = "100";
        }
        else
        {
            status = "0";
        }

        List<DCP_FaPiaoProjEnableReq.project> getIdList = request.getProjectIdList();
        if (!CollectionUtils.isEmpty(getIdList))
        {
            String sql = "";
            for (DCP_FaPiaoProjEnableReq.project par : getIdList)
            {

                String projectId = par.getProjectId();

                UptBean up1 = new UptBean("DCP_FAPIAO_PROJ");
                up1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
                up1.addCondition("PROJECTID",new DataValue(projectId,Types.VARCHAR));

                up1.addUpdateValue("STATUS",new DataValue(status,Types.VARCHAR));
                up1.addUpdateValue("LASTMODIOPID",new DataValue(req.getOpNO(),Types.VARCHAR));
                up1.addUpdateValue("LASTMODIOPNAME",new DataValue(req.getOpName(),Types.VARCHAR));
                up1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime,Types.DATE));
                this.addProcessData(new DataProcessBean(up1));

            }

            this.doExecuteDataToDB();


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }





    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FaPiaoProjEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FaPiaoProjEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FaPiaoProjEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FaPiaoProjEnableReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_FaPiaoProjEnableReq.levelRequest request = req.getRequest();
        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getOprType())) {
            errMsg.append("操作类型不可为空值,");
            isFail = true;
        }

        List<DCP_FaPiaoProjEnableReq.project> params = request.getProjectIdList();
        if (!CollectionUtils.isEmpty(params)) {
            for (DCP_FaPiaoProjEnableReq.project param : params) {
                if (Check.Null(param.getProjectId())) {
                    errMsg.append("编码不能为空值,");
                    isFail = true;
                }
            }
        } else {
            errMsg.append("编码列表不能为空值 ");
            isFail = true;
        }



        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoProjEnableReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoProjEnableReq>(){};
    }

    @Override
    protected DCP_FaPiaoProjEnableRes getResponseType() {
        return new DCP_FaPiaoProjEnableRes();
    }

}
