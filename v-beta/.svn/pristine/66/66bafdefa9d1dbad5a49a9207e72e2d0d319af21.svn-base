package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CurInvCostStatExportReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostStatExportRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.dsc.spos.utils.excel.ExcelUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DCP_CurInvCostStatExport extends SPosAdvanceService<DCP_CurInvCostStatExportReq, DCP_CurInvCostStatExportRes> {
    @Override
    protected void processDUID(DCP_CurInvCostStatExportReq req, DCP_CurInvCostStatExportRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无对应数据！请先执行");
        }

        Map<String, Object> header = new HashMap<>();
        String exportType = req.getRequest().getExportType();
        if ("2".equals(exportType)) { //明细
            header.put("法人", "CORP");
            header.put("帐套", "ACCOUNTID");
            header.put("年度", "YEAR");
            header.put("期别", "PERIOD");
            header.put("成本域", "COSTDOMAINID");
            header.put("生产领用数量", "CURWOOUTQTY");
            header.put("生产领用金额", "CURWOOUTAMT");
//            header.put("杂项领用数量", "CURMISCINQTY");
//            header.put("杂项领用金额", "CURMISCINAMT");
            header.put("杂项入库数量", "CURMISCINQTY");
            header.put("杂项入库金额", "CURMISCINAMT");
            header.put("其它调整数量", "CURADJINQTY");
            header.put("其它调整金额", "CURADJINAMT");
            header.put("销售数量", "CURSALESQTY");
            header.put("销售成本", "CURSALESAMT");
            header.put("销售费用数量", "CURSALESFEEQTY");
            header.put("销售费用金额", "CURSALESFEEAMT");

//            header.put("销售收入", "COSTDOMAINID");
            header.put("盘点盈亏数量", "CURINVADJQTY");
            header.put("盘点盈亏金额", "CURINVADJAMT");
            header.put("结存调整", "ENDINGBALADJAMT");

        } else {

        }
        qData.add(0, header);
        String mainTaskId = UUID.randomUUID().toString().replace("-", "");

        ExcelUtils.convertToExcelWithDirectory(
                qData,
                mainTaskId,
                "export"
        );

        ColumnDataValue dcp_exportDetail = new ColumnDataValue();
        dcp_exportDetail.add("EID", req.geteId());
        dcp_exportDetail.add("STATUS", "100");
        dcp_exportDetail.add("CREATEOPID", req.getEmployeeNo());
        dcp_exportDetail.add("CREATEDEPTID", req.getDepartmentNo());
        dcp_exportDetail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

        dcp_exportDetail.add("ACCOUNTID", req.getRequest().getAccount_id());
        dcp_exportDetail.add("YEAR", req.getRequest().getYear());
        dcp_exportDetail.add("PERIOD", req.getRequest().getPeriod());
        dcp_exportDetail.add("MAINTASKID", mainTaskId);
        dcp_exportDetail.add("JOBNAME", "DCP_CurInvCostStatExport");
        dcp_exportDetail.add("IMPSTATEINFO", "0");
        dcp_exportDetail.add("DIRECTORY", "export");

    }

    protected String getQuerySql(DCP_CurInvCostStatExportReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        String exportType = req.getRequest().getExportType();
        if ("2".equals(exportType)) { //明细
            querySql.append(" SELECT * FROM DCP_CURINVCOSTSTAT a ");

        } else { //汇总
            querySql.append(" SELECT EID,ACCOUNTID,COSTDOMAINID,YEAR,PERIOD,CORP " +
                    " SUM(CURPURINQTY) " +
                    " FROM DCP_CURINVCOSTSTAT a ");
        }
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            querySql.append(" AND YEAR = '").append(req.getRequest().getYear()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            int period = Integer.parseInt(req.getRequest().getPeriod());
            querySql.append(" AND PERIOD = '").append(period).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND CORP = '").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCostDomainId())) {
            querySql.append(" AND COSTDOMAINID = '").append(req.getRequest().getCostDomainId()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getAccount_id())) {
            querySql.append(" AND ACCOUNTID = '").append(req.getRequest().getAccount_id()).append("'");
        }

        return querySql.toString();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CurInvCostStatExportReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CurInvCostStatExportReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CurInvCostStatExportReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CurInvCostStatExportReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostStatExportReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostStatExportReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostStatExportRes getResponseType() {
        return new DCP_CurInvCostStatExportRes();
    }
}
