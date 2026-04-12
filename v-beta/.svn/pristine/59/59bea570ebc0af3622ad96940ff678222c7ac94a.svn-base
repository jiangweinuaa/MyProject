package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostAccountProcessReq;
import com.dsc.spos.json.cust.res.DCP_CostAccountProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DCP_CostAccountProcess extends SPosAdvanceService<DCP_CostAccountProcessReq, DCP_CostAccountProcessRes> {
    @Override
    protected void processDUID(DCP_CostAccountProcessReq req, DCP_CostAccountProcessRes res) throws Exception {

        String year = req.getRequest().getYear();
        String period = req.getRequest().getPeriod();
        if (period.length() < 2) {
            period = "0" + period;
        }

        String querySql = String.format(" SELECT * FROM DCP_ACOUNT_SETTING WHERE STATUS='100' AND EID='%s' and ACCOUNTID='%s'  ", req.geteId(), req.getRequest().getAccountID());
        List<Map<String, Object>> data = doQueryData(querySql, null);

        if (CollectionUtils.isEmpty(data)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getAccountID() + "帐套不存在或未启用!");
        }

        //现行年月
        String closingDate = data.get(0).get("COSTCURRENTYEAR").toString() + data.get(0).get("COSTCURRENTMTH").toString();

        String currDate = year + period;
        if (StringUtils.isNotEmpty(closingDate) && DateFormatUtils.compareDate(currDate, closingDate) != 0) {
            throw new RuntimeException("当前计算的年度期别:" + currDate + "成本现行年月:" + closingDate + ",不可执行成本计算!");
        }

        List<String> costDoMainList = new ArrayList<>();

        String corp = data.get(0).get("CORP").toString();
        if (StringUtils.isEmpty(req.getRequest().getCostDomainId())) {
            querySql = " SELECT DISTINCT ORGANIZATIONNO FROM DCP_ORG a WHERE a.EID='" + req.geteId() + "' AND a.STATUS='100' AND CORP='" + corp + "' ";
            List<Map<String, Object>> qData = doQueryData(querySql, null);
            if (CollectionUtils.isNotEmpty(qData)) {
                for (Map<String, Object> map : qData) {
                    costDoMainList.add(map.get("ORGANIZATIONNO").toString());
                }
            }
        } else {
            costDoMainList.add(req.getRequest().getCostDomainId());
        }

        querySql = " SELECT * FROM DCP_ACOUNT_SETTING WHERE COSTCURRENTYEAR=" + year + " and COSTCURRENTMTH=" + period;
        data = doQueryData(querySql, null);

        String procName = "SP_DCP_COST_DOMAIN";
        if (CollectionUtils.isNotEmpty(costDoMainList)) {

            for (String costDoMain : costDoMainList) {

                List<Object> execData = new ArrayList<>();
                execData.add(req.geteId());
                execData.add(costDoMain);
                execData.add(year + period);
                if (StringUtils.isNotEmpty(req.getRequest().getPluNo())) {
                    execData.add(req.getRequest().getPluNo());
                } else {
                    execData.add("");
                }

                this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean(procName, execData)));

            }
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_CostAccountProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostAccountProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostAccountProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostAccountProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostAccountProcessReq> getRequestType() {
        return new TypeToken<DCP_CostAccountProcessReq>() {
        };
    }

    @Override
    protected DCP_CostAccountProcessRes getResponseType() {
        return new DCP_CostAccountProcessRes();
    }
}
