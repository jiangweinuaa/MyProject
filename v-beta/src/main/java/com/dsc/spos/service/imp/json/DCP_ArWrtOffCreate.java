package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ArWrtOffCreateReq;
import com.dsc.spos.json.cust.res.DCP_ArWrtOffCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ArWrtOffCreate extends SPosAdvanceService<DCP_ArWrtOffCreateReq, DCP_ArWrtOffCreateRes> {

    @Override
    protected void processDUID(DCP_ArWrtOffCreateReq req, DCP_ArWrtOffCreateRes res) throws Exception {


        String bDate = DateFormatUtils.getPlainDate(req.getRequest().getBDate());

        String accSql = "select ACCOUNTID,ARPARAMETER,CURRENCY,ISDEPOSITBYORDER,to_char(a.ARCLOSINGDATE,'yyyyMMdd') as ARCLOSINGDATE " +
                " from DCP_ACOUNT_SETTING a " +
                " where a.eid='" + req.geteId() + "' and a.STATUS='100' and a.corp='" + req.getRequest().getCorp() + "'  and a.ACCTTYPE='1' ";

        List<Map<String, Object>> asData = doQueryData(accSql, null);
        if (CollectionUtils.isEmpty(asData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, req.getRequest().getCorp() + "对应主帐套未配置或未启用！");
        }
        String arCloseDate = asData.get(0).get("ARCLOSINGDATE").toString();
        if (DateFormatUtils.compareDate(bDate, arCloseDate) < 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不可小于应收关账日:" + arCloseDate);
        }

//        List<Map<String, Object>> qData = doQueryData(getQuerySql(req),null);
//        if (CollectionUtils.isEmpty(qData)) {
//            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无数据需要处理");
//        }

        String createDate = DateFormatUtils.getNowPlainDate();
        String createTime = DateFormatUtils.getNowPlainTime();
        for (DCP_ArWrtOffCreateReq.ArRecList oneRec : req.getRequest().getArRecList()) {
            ColumnDataValue dcp_arBillRec = new ColumnDataValue();
            dcp_arBillRec.add("EID", DataValues.newString(req.geteId()));
            dcp_arBillRec.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_arBillRec.add("CREATE_DATE", DataValues.newString(createDate));
            dcp_arBillRec.add("CREATE_TIME", DataValues.newString(createTime));

            dcp_arBillRec.add("STATUS", DataValues.newString("0"));
            dcp_arBillRec.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            dcp_arBillRec.add("ARNO", DataValues.newString(req.getRequest().getArNo()));
            dcp_arBillRec.add("ITEM", DataValues.newString(oneRec.getItem()));
            dcp_arBillRec.add("ACCORG", DataValues.newString(oneRec.getAccOrg()));
            dcp_arBillRec.add("ORGANIZATIONNO", DataValues.newString(oneRec.getOrganizationNo()));
            dcp_arBillRec.add("SOURCETYPE", DataValues.newString(oneRec.getSourceType()));
            dcp_arBillRec.add("WRTOFFTYPE", DataValues.newString(oneRec.getWrtOffType()));
            dcp_arBillRec.add("SOURCENO", DataValues.newString(oneRec.getSourceNo()));
            dcp_arBillRec.add("SOURCEITEM", DataValues.newString(oneRec.getSourceItem()));
            dcp_arBillRec.add("CARDNO", DataValues.newString(oneRec.getCardNo()));
            dcp_arBillRec.add("MEMO", DataValues.newString(oneRec.getMemo()));
            dcp_arBillRec.add("BNKDEPWDRAWCODE", DataValues.newString(oneRec.getBnkDepWdrawCode()));
            dcp_arBillRec.add("CASHCHGCODE", DataValues.newString(oneRec.getCashChgCode()));
            dcp_arBillRec.add("BIZPARTNERNO", DataValues.newString(oneRec.getBizPartnerNo()));
            dcp_arBillRec.add("TRANSINPMTBILLNO", DataValues.newString(oneRec.getTransInPmtBillNo()));
            dcp_arBillRec.add("WRTOFFDIRECTION", DataValues.newString(oneRec.getWrtOffDirection()));
            dcp_arBillRec.add("ARSUBJECTID", DataValues.newString(oneRec.getArSubjectId()));
            dcp_arBillRec.add("EMPLOYEENO", DataValues.newString(oneRec.getEmployeeNo()));
            dcp_arBillRec.add("DEPARTNO", DataValues.newString(oneRec.getDepartId()));
            dcp_arBillRec.add("CATEGORY", DataValues.newString(oneRec.getCateGory()));
            dcp_arBillRec.add("RECEIVER", DataValues.newString(oneRec.getReceiver()));
            dcp_arBillRec.add("FREECHARS1", DataValues.newString(oneRec.getFreeChars1()));
            dcp_arBillRec.add("FREECHARS2", DataValues.newString(oneRec.getFreeChars2()));
            dcp_arBillRec.add("FREECHARS3", DataValues.newString(oneRec.getFreeChars3()));
            dcp_arBillRec.add("FREECHARS4", DataValues.newString(oneRec.getFreeChars4()));
            dcp_arBillRec.add("FREECHARS5", DataValues.newString(oneRec.getFreeChars5()));
            dcp_arBillRec.add("CURRENCY", DataValues.newString(oneRec.getCurrency()));
            dcp_arBillRec.add("EXRATE", DataValues.newString(oneRec.getExRate()));
            dcp_arBillRec.add("FCYREVAMT", DataValues.newString(oneRec.getFCYRevAmt()));
            dcp_arBillRec.add("LCYREVAMT", DataValues.newString(oneRec.getLCYRevAmt()));
            dcp_arBillRec.add("RECDATE", DataValues.newString(oneRec.getRecDate()));
            dcp_arBillRec.add("CLASSNO", DataValues.newString(oneRec.getClassNo()));


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLREC", dcp_arBillRec)));
        }

        for (DCP_ArWrtOffCreateReq.ArWFList oneWf : req.getRequest().getArWFList()) {
            ColumnDataValue dcp_arWrtOff = new ColumnDataValue();
            dcp_arWrtOff.add("EID", DataValues.newString(req.geteId()));
            dcp_arWrtOff.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_arWrtOff.add("CREATE_DATE", DataValues.newString(createDate));
            dcp_arWrtOff.add("CREATE_TIME", DataValues.newString(createTime));

            dcp_arWrtOff.add("ACCOUNTID", DataValues.newString(oneWf.getAccountID()));
            dcp_arWrtOff.add("ARNO", DataValues.newString(req.getRequest().getArNo()));
            dcp_arWrtOff.add("BDATE", DataValues.newString(DateFormatUtils.getDate(req.getRequest().getBDate())));
            dcp_arWrtOff.add("ORGANIZATIONNO", DataValues.newString(oneWf.getOrganizationNo()));
            dcp_arWrtOff.add("ARTYPE", DataValues.newString(req.getRequest().getArType()));
            dcp_arWrtOff.add("ACCEMPLOYEENO", DataValues.newString(req.getRequest().getAccEmployeeNo()));
            dcp_arWrtOff.add("BIZPARTNERNO", DataValues.newString(oneWf.getBizPartnerNo()));
            dcp_arWrtOff.add("RECEIVER", DataValues.newString(req.getRequest().getReceiver()));
            dcp_arWrtOff.add("SOURCENO", DataValues.newString(req.getRequest().getSourceNo()));
            dcp_arWrtOff.add("GRPPMTNO", DataValues.newString(req.getRequest().getGrpPmtNo()));
            dcp_arWrtOff.add("GLNO", DataValues.newString(req.getRequest().getGlNo()));
            dcp_arWrtOff.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
            dcp_arWrtOff.add("FCYDRTATAMT", DataValues.newString(req.getRequest().getFCYDRTATAmt()));
            dcp_arWrtOff.add("FCYCRTATAMT", DataValues.newString(req.getRequest().getFCYCRTATAmt()));
            dcp_arWrtOff.add("LCYDRTATAMT", DataValues.newString(req.getRequest().getLCYDRTATAmt()));
            dcp_arWrtOff.add("LCYCRTATAMT", DataValues.newString(req.getRequest().getLCYCRTATAmt()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLREC", dcp_arWrtOff)));


        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ArWrtOffCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ArWrtOffCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ArWrtOffCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ArWrtOffCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArWrtOffCreateReq> getRequestType() {
        return new TypeToken<DCP_ArWrtOffCreateReq>() {
        };
    }

    @Override
    protected DCP_ArWrtOffCreateRes getResponseType() {
        return new DCP_ArWrtOffCreateRes();
    }

    @Override
    protected String getQuerySql(DCP_ArWrtOffCreateReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT  ")
                .append(" ,a.* ")
                .append(" ,b.TASKID,b.ARTYPE ")
                .append(" FROM DCP_ARPERD a ")
                .append(" LEFT JOIN DCP_ARBILL b on a.EID=b.EID and a.ARNO=b.ARNO ")
        ;
        querySql.append(" WHERE a.eid='").append(req.geteId()).append("' ");

        if (Check.isNotEmpty(req.getRequest().getArType())) {
            querySql.append(" AND a.ARTYPE=' ").append(req.getRequest().getArType()).append("' ");
        }


        return querySql.toString();
    }
}
