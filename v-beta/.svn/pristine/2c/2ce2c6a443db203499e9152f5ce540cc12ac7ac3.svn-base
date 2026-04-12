package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BankPayDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_BankPayDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BankPayDetailQuery extends SPosBasicService<DCP_BankPayDetailQueryReq, DCP_BankPayDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BankPayDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BankPayDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_BankPayDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_BankPayDetailQueryRes getResponseType() {
        return new DCP_BankPayDetailQueryRes();
    }

    @Override
    protected DCP_BankPayDetailQueryRes processJson(DCP_BankPayDetailQueryReq req) throws Exception {
        DCP_BankPayDetailQueryRes res = this.getResponseType();
        DCP_BankPayDetailQueryRes.Datas oneData = res.new Datas();
        res.setDatas(oneData);

        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        if (CollectionUtils.isEmpty(queryData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "没有详情数据");
        }
        Map<String, Object> oneMaster = queryData.get(0);

        oneData.setStatus(oneMaster.get("STATUS").toString());
        oneData.setCorp(oneMaster.get("CORP").toString());
        oneData.setCmNo(oneMaster.get("CMNO").toString());
        oneData.setBDate(oneMaster.get("BDATE").toString());
        oneData.setOrganizationNo(oneMaster.get("ORGANIZATIONNO").toString());
        oneData.setEmployeeNo(oneMaster.get("EMPLOYEENO").toString());
        oneData.setDepartId(oneMaster.get("DEPARTID").toString());
        oneData.setDepWdrawCode(oneMaster.get("DEPWDRWCODE").toString());
        oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
        oneData.setAccountCode(oneMaster.get("ACCOUNTCODE").toString());
        oneData.setPayEmployee(oneMaster.get("PAYEMPLOYEE").toString());
        oneData.setIsEnty(oneMaster.get("ISENTY").toString());
        oneData.setGlNo(oneMaster.get("GLNO").toString());
        oneData.setMemo(oneMaster.get("MEMO").toString());

        oneData.setCreateOpId(oneMaster.get("CREATEOPID").toString());
        oneData.setCreateTime(oneMaster.get("CREATETIME").toString());
        oneData.setLastmodiopID(oneMaster.get("LASTMODIOPID").toString());
        oneData.setLastmodiTime(oneMaster.get("LASTMODITIME").toString());
        oneData.setSubMitopID(oneMaster.get("SUBMITOPID").toString());
        oneData.setSubMitTime(oneMaster.get("SUBMITTIME").toString());
        oneData.setConfirmopID(oneMaster.get("CONFIRMOPID").toString());
        oneData.setConfirmopTime(oneMaster.get("CONFIRMTIME").toString());
        oneData.setCancelopID(oneMaster.get("CANCELOPID").toString());
        oneData.setCancelTime(oneMaster.get("CANCELLTIME").toString());

        oneData.setCmList(new ArrayList<>());
        for (Map<String, Object> oneDetail : queryData) {
            DCP_BankPayDetailQueryRes.CmList oneDetailData = res.new CmList();
            oneData.getCmList().add(oneDetailData);

            oneDetailData.setItem(oneDetail.get("ITEM").toString());
            oneDetailData.setOrganizationNo(oneDetail.get("ORGANIZATIONNO").toString());
            oneDetailData.setBizPartnerNo(oneDetail.get("BIZPARTNERNO").toString());
            oneDetailData.setClassType(oneDetail.get("CLASSTYPE").toString());
            oneDetailData.setClassNo(oneDetail.get("CLASSNO").toString());
            oneDetailData.setClassName(oneDetail.get("CLASSNAME").toString());
            oneDetailData.setDepWdrawCode(oneDetail.get("DEPWDRWCODE").toString());
            oneDetailData.setDepWdrawName(oneDetail.get("DEPWDRWNAME").toString());
            oneDetailData.setCfCode(oneDetail.get("CFCODE").toString());
            oneDetailData.setCfName(oneDetail.get("CFNAME").toString());
            oneDetailData.setAccountCode(oneDetail.get("ACCOUNTCODE").toString());
            oneDetailData.setAccountName(oneDetail.get("ACCOUNTNAME").toString());
            oneDetailData.setPayEmployee(oneDetail.get("PAYEMPLOYEE").toString());
            oneDetailData.setEmployeeName(oneDetail.get("EMPLOYEENAME").toString());
            oneDetailData.setCurrency(oneDetail.get("CURRENCY").toString());
            oneDetailData.setExRate(oneDetail.get("EXRATE").toString());
            oneDetailData.setFCYAmt(oneDetail.get("FCYAMT").toString());
            oneDetailData.setLCYAmt(oneDetail.get("LCYAMT").toString());
            oneDetailData.setSourceNo(oneDetail.get("SOURCENO").toString());
            oneDetailData.setSourceNoSeq(oneDetail.get("SOURCENOSEQ").toString());
            oneDetailData.setOffsetSubject(oneDetail.get("OFFSETSUBJECT").toString());
            oneDetailData.setOffsetSubjectName(oneDetail.get("OFFSETSUBJECTNAME").toString());
            oneDetailData.setSubjectId(oneDetail.get("SUBJECTID").toString());
            oneDetailData.setSubjectName(oneDetail.get("SUBJECTNAME").toString());
            oneDetailData.setPendOffsetNo(oneDetail.get("PENDOFFSETNO").toString());
            oneDetailData.setMemo(oneDetail.get("MEMO").toString());
            oneDetailData.setRevOrg(oneDetail.get("REVOrg").toString());
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_BankPayDetailQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.* ")
                .append(" FROM DCP_BANKPAY a ")
                .append(" LEFT JOIN DCP_BANKPAYDETAIL b on a.EID=b.EID and a.CMNO=b.CMNO ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (Check.isNotEmpty(req.getRequest().getCmNo())) {
            querySql.append(" and a.CMNO='").append(req.getRequest().getCmNo()).append("'");
        }


        return querySql.toString();
    }
}
