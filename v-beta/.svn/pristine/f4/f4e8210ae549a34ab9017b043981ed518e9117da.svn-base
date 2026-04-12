package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_SettleDataQueryReq;
import com.dsc.spos.json.cust.res.DCP_SettleDataQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_SettleDataQuery  extends SPosBasicService<DCP_SettleDataQueryReq, DCP_SettleDataQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_SettleDataQueryReq req) throws Exception {
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
    protected TypeToken<DCP_SettleDataQueryReq> getRequestType() {
        return new TypeToken<DCP_SettleDataQueryReq>(){};
    }

    @Override
    protected DCP_SettleDataQueryRes getResponseType() {
        return new DCP_SettleDataQueryRes();
    }

    @Override
    protected DCP_SettleDataQueryRes processJson(DCP_SettleDataQueryReq req) throws Exception {
        DCP_SettleDataQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;

        if(req.getPageNumber()==0){
            req.setPageNumber(1);
        }
        if(req.getPageSize()==0){
            req.setPageSize(10);
        }
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

            //String detailSql = this.getDetailSql(req);
            //List<Map<String, Object>> getQData2=this.doQueryData(detailSql, null);

            for (Map<String, Object> row : getQData){
                DCP_SettleDataQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setOrganizationNo(row.get("ORGANIZATIONNO").toString());
                level1Elm.setOrganizationName(row.get("ORGANIZATIONNAME").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setBizType(row.get("BIZTYPE").toString());
                level1Elm.setBType(row.get("BTYPE").toString());
                level1Elm.setBillNo(row.get("BILLNO").toString());

                level1Elm.setItem(row.get("ITEM").toString());
                level1Elm.setBizType(row.get("BIZTYPE").toString());
                level1Elm.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                level1Elm.setBizPartnerName(row.get("BIZPARTNERNAME").toString());
                level1Elm.setPayOrgNo(row.get("PAYORGNO").toString());
                level1Elm.setPayOrgName(row.get("PAYORGNAME").toString());
                level1Elm.setBillDateNo(row.get("BILLDATENO").toString());
                level1Elm.setBillDateName(row.get("BILLDATENAME").toString());
                level1Elm.setPayDateNo(row.get("PAYDATENO").toString());
                level1Elm.setPayDateName(row.get("PAYDATENAME").toString());
                level1Elm.setInvoiceCode(row.get("INVOICECODE").toString());
                level1Elm.setInvoiceName(row.get("INVOICENAME").toString());
                level1Elm.setBillDate(row.get("BILLDATE").toString());
                level1Elm.setPayDate(row.get("PAYDATE").toString());
                level1Elm.setMonth(row.get("MONTH").toString());
                level1Elm.setYear(row.get("YEAR").toString());
                level1Elm.setCurrency(row.get("CURRENCY").toString());
                level1Elm.setCurrencyName(row.get("CURRENCYNAME").toString());
                level1Elm.setTaxCode(row.get("TAXCODE").toString());
                level1Elm.setTaxName(row.get("TAXNAME").toString());
                level1Elm.setTaxRate(row.get("TAXRATE").toString());
                level1Elm.setDirection(row.get("DIRECTION").toString());
                level1Elm.setPluNo(row.get("PLUNO").toString());
                level1Elm.setPluName(row.get("PLUNAME").toString());
                level1Elm.setPriceUnit(row.get("PRICEUNIT").toString());
                level1Elm.setFeatureNo(row.get("FEATURENO").toString());
                level1Elm.setBillQty(row.get("BILLQTY").toString());
                level1Elm.setFee(row.get("FEE").toString());
                level1Elm.setFeeName(row.get("FEENAME").toString());
                level1Elm.setBillPrice(row.get("BILLPRICE").toString());
                level1Elm.setPreTaxAmt(row.get("PRETAXAMT").toString());
                level1Elm.setTaxAmt(row.get("TAXAMT").toString());
                level1Elm.setBillAmt(row.get("BILLAMT").toString());
                level1Elm.setUnSettleAmt(row.get("UNSETTLEAMT").toString());
                level1Elm.setSettleAmt(row.get("SETTLEAMT").toString());
                level1Elm.setUnPaidAmt(row.get("UNPAIDAMT").toString());
                level1Elm.setPaidAmt(row.get("PAIDAMT").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setCateGory(row.get("CATEGORY").toString());
                level1Elm.setCateGoryName(row.get("CATEGORYNAME").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());

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
    protected String getQuerySql(DCP_SettleDataQueryReq req) throws Exception {
        String eId = req.geteId();
        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with settledata as ("
                + " select a.billno,a.item " +
                " from DCP_SETTLEDATA a " +
                " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.payorgno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_FEE_LANG e on e.eid=a.eid and e.fee=a.fee and e.lang_type='"+req.getLangType()+"'" +
                " left join dcp_bizpartner f on f.eid=a.eid and f.bIzpartnerno=a.bizpartnerno "
                + " where a.eid='"+eId+"' "
        );

        if(Check.NotNull(req.getRequest().getBizType())){
            sqlbuf.append(" and a.biztype='"+req.getRequest().getBizType()+"' ");
        }

        if(Check.NotNull(req.getRequest().getBeginDate())){
            sqlbuf.append(" and a.bdate>='"+req.getRequest().getBeginDate()+"' ");
        }

        if(Check.NotNull(req.getRequest().getEndDate())){
            sqlbuf.append(" and a.bdate<='"+req.getRequest().getEndDate()+"' ");
        }

        if(Check.NotNull(req.getRequest().getOrganizationNo())){
            sqlbuf.append(" and a.organizationno='"+req.getRequest().getOrganizationNo()+"' ");
        }

        if(Check.NotNull(req.getRequest().getPayOrgNo())){
            sqlbuf.append(" and a.payorgno='"+req.getRequest().getPayOrgNo()+"' ");
        }

        if(Check.NotNull(req.getRequest().getKeyTxt())){
            sqlbuf.append(" and ( a.pluno like '%"+req.getRequest().getKeyTxt()+"%'" +
                            " or d.plu_name like '%"+req.getRequest().getKeyTxt()+"%' "+
                            " or e.fee_name like '%"+req.getRequest().getKeyTxt()+"%' "+
                            " or a.fee like '%"+req.getRequest().getKeyTxt()+"%' " +
                    " or  a.billno like '%"+req.getRequest().getKeyTxt()+"%'" +
                    " )");
        }

        if(Check.NotNull(req.getRequest().getBizPartnerNo())){
            sqlbuf.append(" and a.bizpartnerno='"+req.getRequest().getBizPartnerNo()+"' ");
        }
        if(Check.NotNull(req.getRequest().getBizPartnerName())){
            sqlbuf.append(" and f.sname like '%"+req.getRequest().getBizPartnerName()+"%' ");
        }

        if(Check.NotNull(req.getRequest().getStatus())){
            sqlbuf.append(" and a.status='"+req.getRequest().getStatus()+"'");
        }
        if(Check.NotNull(req.getRequest().getBillNo())){
            sqlbuf.append(" and a.billno = '"+req.getRequest().getBillNo()+"' ");
        }
        if(Check.NotNull(req.getRequest().getBType())){
            sqlbuf.append(" and a.btype='"+req.getRequest().getBType()+"' ");
        }


        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" group by a.billno,a.item ");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.bdate ,a.billno,a.item ) as rn, a.* from ("
                + " select distinct a.billno,a.organizationno,b.org_name as ORGANIZATIONNAME,a.btype,a.bdate,a.biztype " +
                " ,a.item,a.bizpartnerno,f.sname as bizpartnername,a.payOrgNo,a.billdateno,a.paydate,a.invoiceCode," +
                " a.status,a.category,a.departId,a.paidAmt,a.settleAmt,a.billAmt,a.taxAmt,a.preTaxAmt,a.billPrice,a.billdate," +
                "a.month,a.year,a.currency,a.fee,e.fee_name as feename,a.BILLQTY,a.featureno,a.priceunit,a.pluno,d.plu_name as pluname,a.direction," +
                "a.taxrate,a.taxcode,a.UNSETTLEAMT,a.UNPAIDAMT,g.taxname as taxname,h.name as currencyName,i.CATEGORY_NAME as categoryname,j.name as paydatename,k.name as billdatename,l.org_name as payorgname,a.PAYDATENO,m.departname," +
                " n.INVOICE_NAME as invoicename   "
                + " from DCP_SETTLEDATA a"
                + " inner join settledata p on a.billno=p.billno and a.item=p.item "+
                " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.payorgno and c.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"' " +
                        " left join DCP_FEE_LANG e on e.eid=a.eid and e.fee=a.fee and e.lang_type='"+req.getLangType()+"'" +
                        " left join dcp_bizpartner f on f.eid=a.eid and f.bIzpartnerno=a.bizpartnerno "+
                " LEFT JOIN DCP_TAXCATEGORY_LANG g on g.eid=a.eid and g.taxcode=a.taxcode and g.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CURRENCY_LANG h on h.eid=a.eid and h.currency=a.currency and h.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG i on i.eid=a.eid and i.category=a.category and i.lang_type='"+req.getLangType()+"' " +
                " left join DCP_PAYDATE_LANG j on j.eid=a.eid and j.paydateno=a.paydateno and j.lang_type='"+req.getLangType()+"' " +
                " left join DCP_BILLDATE_LANG k on k.eid=a.eid and k.billdateno=a.billdateno and k.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang  l on l.eid=a.eid and l.organizationno=a.payorgno and l.lang_type='"+req.getLangType()+"'" +
                " left join dcp_department_lang m on m.eid=a.eid and m.departno=a.departid and m.lang_type='"+req.getLangType()+"' " +
                " left join DCP_INVOICETYPE_LANG n on n.eid=a.eid and n.invoicecode=a.invoicecode and n.lang_type='"+req.getLangType()+"'"
                +  " where a.eid='"+eId+"' " +
                ") a "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }

}


