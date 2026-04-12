package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BankReceipetQueryReq;
import com.dsc.spos.json.cust.res.DCP_BankReceipetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BankReceipetQuery extends SPosBasicService<DCP_BankReceipetQueryReq, DCP_BankReceipetQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BankReceipetQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BankReceipetQueryReq> getRequestType() {
        return new TypeToken<DCP_BankReceipetQueryReq>() {
        };
    }

    @Override
    protected DCP_BankReceipetQueryRes getResponseType() {
        return new DCP_BankReceipetQueryRes();
    }

    @Override
    protected DCP_BankReceipetQueryRes processJson(DCP_BankReceipetQueryReq req) throws Exception {
        DCP_BankReceipetQueryRes res = this.getResponseType();
        List<Map<String, Object>> queryData = doQueryData(this.getQuerySql(req), null);
        int totalRecords = 0;    //总笔数
        int totalPages = 0;        //总页数

        res.setDatas(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(queryData)) {
            String num = queryData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            for (Map<String, Object> oneMaster : queryData) {
                DCP_BankReceipetQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setStatus(oneMaster.get("STATUS").toString());
                oneData.setCorp(oneMaster.get("CORP").toString());
                oneData.setCorpName(oneMaster.get("CORPNAME").toString());
                oneData.setOrganizationNo(oneMaster.get("ORGANIZATIONNO").toString());
                oneData.setOrganizationName(oneMaster.get("ORGANIZATIONNAME").toString());
                oneData.setCmNo(oneMaster.get("CMNO").toString());
                oneData.setBizPartnerNo(oneMaster.get("BIZPARTNERNO").toString());
                oneData.setBizPartnerName(oneMaster.get("BIZPARTNERNAME").toString());
                oneData.setSumFCYAmt(oneMaster.get("FCYAMT").toString());
                oneData.setGlNo(oneMaster.get("GLNO").toString());
                oneData.setCmType(oneMaster.get("CMTYPE").toString());
//                oneData.setSourceNo(oneMaster.get("SOURCENO").toString());
            }
        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }


    @Override
    protected String getQuerySql(DCP_BankReceipetQueryReq req) throws Exception {

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();
        querySql.append("SELECT *  FROM (")
                .append(" SELECT count(1) over () as num,row_number() over (order by a.ARNO desc) as rn ")
                .append(" ,a.* ")
                .append(" ,ol1.SNAME ORGANIZATIONNAME,ol2.SNAME CORPNAME,bz1.SNAME BIZPARTNERNAME ")
                .append(" FROM DCP_BANKRECEIPT a ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.ORGANIZATIONNO and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid=a.eid and ol2.ORGANIZATIONNO=a.CORP and ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_BIZPARTNER bz1 on bz1.eid=a.eid and bz1.BIZPARTNERNO=a.BIZPARTNERNO ")
        ;
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (Check.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" and a.CORP='").append(req.getRequest().getCorp()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getStatus())) {
            querySql.append(" and a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getCmType())) {
            querySql.append(" and a.CMTYPE='").append(req.getRequest().getCmType()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getCmNo())) {
            querySql.append(" and a.CMNO='").append(req.getRequest().getCmNo()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getBizPartnerNo())) {
            querySql.append(" and a.BIZPARTNERNO='").append(req.getRequest().getBizPartnerNo()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getBeginDate())) {
            querySql.append(" and a.BDATE>='").append(req.getRequest().getBeginDate()).append("'");
        }

        if (Check.isNotEmpty(req.getRequest().getEndDate())) {
            querySql.append(" and a.BDATE<='").append(req.getRequest().getEndDate()).append("'");
        }

        querySql.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY CMNO ");

        return querySql.toString();
    }
}
