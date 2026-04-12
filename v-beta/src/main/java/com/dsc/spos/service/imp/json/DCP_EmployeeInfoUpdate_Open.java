package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EmployeeInfoUpdate_OpenReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_EmployeeInfoUpdate_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.util.List;
/**
 * 服務函數：DCP_EmployeeInfoUpdate_Open
 *   說明：员工信息修改
 * 服务说明：员工信息修改
 * @author wangzyc
 * @since  2021-4-25
 */
public class DCP_EmployeeInfoUpdate_Open extends SPosAdvanceService<DCP_EmployeeInfoUpdate_OpenReq, DCP_EmployeeInfoUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_EmployeeInfoUpdate_OpenReq req, DCP_EmployeeInfoUpdate_OpenRes res) throws Exception {
        String eId = req.geteId();
        DCP_EmployeeInfoUpdate_OpenReq.level1Elm request = req.getRequest();
        List<String> opList = request.getOpList();
        String activation = request.getActivation();
        String machShopNo = request.getMachShopNo();
        String departNo = request.getDepartNo();

        try {
            if(!CollectionUtils.isEmpty(opList)){
                for (String opNo : opList) {
                    //原因码信息
                    UptBean ub1 = null;
                    ub1 = new UptBean("PLATFORM_STAFFS");
                    //add Value
                    ub1.addUpdateValue("ACTIVATION", new DataValue(activation, Types.VARCHAR));

                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(machShopNo, Types.VARCHAR));
                    ub1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                    if(!Check.Null(departNo)){
                        ub1.addCondition("DEPARTNO", new DataValue(departNo, Types.VARCHAR));
                    }
                    this.addProcessData(new DataProcessBean(ub1));
                }
                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!"+e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_EmployeeInfoUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EmployeeInfoUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EmployeeInfoUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EmployeeInfoUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        DCP_EmployeeInfoUpdate_OpenReq.level1Elm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer("");
        if(request==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if (Check.Null(request.getMachShopNo())) {
            errCt++;
            errMsg.append("生产门店编号不可为空值, ");
            isFail = true;
        }
        if (CollectionUtils.isEmpty(request.getOpList())) {
            errCt++;
            errMsg.append("员工编号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getActivation())) {
            errCt++;
            errMsg.append("是否激活Y/N不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_EmployeeInfoUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_EmployeeInfoUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_EmployeeInfoUpdate_OpenRes getResponseType() {
        return new DCP_EmployeeInfoUpdate_OpenRes();
    }
}
