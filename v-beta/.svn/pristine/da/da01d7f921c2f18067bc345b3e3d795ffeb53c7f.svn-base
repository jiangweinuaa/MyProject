package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_VendorAdjQueryReq;
import com.dsc.spos.json.cust.res.DCP_VendorAdjQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_VendorAdjQuery extends SPosBasicService<DCP_VendorAdjQueryReq, DCP_VendorAdjQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_VendorAdjQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_VendorAdjQueryReq> getRequestType() {
        return new TypeToken<DCP_VendorAdjQueryReq>(){};
    }

    @Override
    protected DCP_VendorAdjQueryRes getResponseType() {
        return new DCP_VendorAdjQueryRes();
    }

    @Override
    protected DCP_VendorAdjQueryRes processJson(DCP_VendorAdjQueryReq req) throws Exception {
        DCP_VendorAdjQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> row : getQData){
                DCP_VendorAdjQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setOrganizationNo(row.get("ORGANIZATIONNO").toString());
                level1Elm.setOrg_Name(row.get("ORG_NAME").toString());
                level1Elm.setAdjustNO(row.get("ADJUSTNO").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setOtype(row.get("OTYPE").toString());
                level1Elm.setSStockInNo(row.get("SSTOCKINNO").toString());
                level1Elm.setSupplier(row.get("SUPPLIER").toString());
                level1Elm.setSupplierName(row.get("SUPPLIERNAME").toString());
                level1Elm.setPayDateNo(row.get("PAYDATENO").toString());
                level1Elm.setBillDateNo(row.get("BILLDATENO").toString());
                level1Elm.setTaxCode(row.get("TAXCODE").toString());
                level1Elm.setTaxRate(row.get("TAXRATE").toString());
                level1Elm.setCurrency(row.get("CURRENCY").toString());
                level1Elm.setCurrencyName(row.get("CURRENCYNAME").toString());
                level1Elm.setExRate(row.get("EXRATE").toString());
                level1Elm.setTot_Amt(row.get("TOT_AMT").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());

                res.getDatas().add(level1Elm);

            }

        }

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
    protected String getQuerySql(DCP_VendorAdjQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        String bdate = req.getRequest().getBdate();
        String organizationNo = req.getRequest().getOrganizationNo();
        String org_name = req.getRequest().getOrg_Name();
        String supplierNo = req.getRequest().getSupplierNo();
        String supplierName = req.getRequest().getSupplierName();
        String startDate = req.getRequest().getStartDate();
        String endDate = req.getRequest().getEndDate();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with adjust as ("
                + " select a.adjustno from DCP_VENDORADJ a"
                + " where a.eid='"+eId+"' "
        );


        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if (!Check.Null(bdate)){
            sqlbuf.append(" and a.bdate='"+bdate+"' ");
        }

        if(Check.NotNull(startDate)){
            sqlbuf.append(" and a.bdate>='"+startDate+"' ");
        }
        if(Check.NotNull(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        if (!Check.Null(organizationNo)){
            sqlbuf.append(" and a.organizationNo='"+organizationNo+"' ");
        }
        if (!Check.Null(supplierNo)){
            sqlbuf.append(" and a.supplier='"+supplierNo+"' ");
        }

        sqlbuf.append(" group by a.adjustno");
        sqlbuf.append(" )");

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.organizationNo desc) as rn,"
                + " a.organizationNo,c.org_name,a.adjustno,a.bdate,a.otype ,a.sstockinno,a.supplier,d.sname as suppliername,a.payDateNo,a.billDateNo," +
                "  a.taxCode,a.taxRate,a.currency,a.exRate,a.tot_Amt,a.memo,e.name as currencyname,a.status "
                + " from DCP_VENDORADJ a"
                + " inner join adjust b on a.adjustno=b.adjustno " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_bizpartner d on d.BIZPARTNERNO=a.supplier and d.eid=a.eid " +
                " left join DCP_CURRENCY_LANG e on e.eid=a.eid and e.currency=a.currency and e.lang_type='"+req.getLangType()+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


