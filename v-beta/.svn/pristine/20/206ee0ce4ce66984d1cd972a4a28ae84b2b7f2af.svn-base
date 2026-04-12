package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_SupplierOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_SupplierOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_SupplierOpenQry extends SPosBasicService<DCP_SupplierOpenQryReq, DCP_SupplierOpenQryRes> {

    @Override
    protected boolean isVerifyFail(DCP_SupplierOpenQryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        } else {
            //分页查询的服务，必须给值，不能为0
            int pageNumber = req.getPageNumber();
            int pageSize = req.getPageSize();
            if (pageNumber == 0) {
                isFail = true;
                errMsg.append("分页查询pageNumber不能为0,");
            }
            if (pageSize == 0) {
                isFail = true;
                errMsg.append("分页查询pageSize不能为0,");
            }

        }


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_SupplierOpenQryReq> getRequestType() {
        return new TypeToken<DCP_SupplierOpenQryReq>(){};
    }

    @Override
    protected DCP_SupplierOpenQryRes getResponseType() {
        return new DCP_SupplierOpenQryRes();
    }

    @Override
    protected DCP_SupplierOpenQryRes processJson(DCP_SupplierOpenQryReq req) throws Exception {
        DCP_SupplierOpenQryRes res = this.getResponse();
        int totalRecords;		//总笔数
        int totalPages;

        //purType为「1统采直供」或「2统采越库」时入参值不可为空
        if("1".equals(req.getRequest().getPurType())||"2".equals(req.getRequest().getPurType())){
            if(Check.Null(req.getRequest().getPurCenter())){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "purType为「1统采直供」或「2统采越库」时purCenter入参值不可为空");
            }
        }

        //查询
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        res.setDatas(new ArrayList<>());

        if(getQData!=null&&getQData.size()>0){

            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> row : getQData) {
                DCP_SupplierOpenQryRes.level1Elm level1Elm =res.new level1Elm();
                level1Elm.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                level1Elm.setSName(row.get("SNAME").toString());
                level1Elm.setFullName(row.get("FULLNAME").toString());
                level1Elm.setMainContact(row.get("MAINCONTACT").toString());
                level1Elm.setMainConMan(row.get("MAINCONMAN").toString());
                level1Elm.setTelephone(row.get("TELEPHONE").toString());
                level1Elm.setTaxCode(row.get("TAXCODE").toString());
                level1Elm.setTaxName(row.get("TAXNAME").toString());
                level1Elm.setTaxRate(row.get("TAXRATE").toString());
                level1Elm.setInclTax(row.get("INCLTAX").toString());
                level1Elm.setTaxCalType(row.get("TAXCALTYPE").toString());
                level1Elm.setPayType(row.get("PAYTYPE").toString());
                level1Elm.setBillDateNo(row.get("BILLDATENO").toString());
                level1Elm.setBillDate_desc(row.get("BILLDATE_DESC").toString());
                level1Elm.setPayDateNo(row.get("PAYDATENO").toString());
                level1Elm.setPayDate_desc(row.get("PAYDATE_DESC").toString());
                level1Elm.setPayCenter(row.get("PAYCENTER").toString());
                level1Elm.setPayCenterName(row.get("PAYCENTERNAME").toString());
                level1Elm.setInvoiceCode(row.get("INVOICECODE").toString());
                level1Elm.setInvoiceName(row.get("INVOICENAME").toString());
                level1Elm.setCurrency(row.get("CURRENCY").toString());
                level1Elm.setCurrencyName(row.get("CURRENCYNAME").toString());
                level1Elm.setPayee(row.get("PAYEE").toString());
                level1Elm.setPayeeName(row.get("PAYEENAME").toString());
                level1Elm.setPayer(row.get("PAYER").toString());
                level1Elm.setPayerName(row.get("PAYERNAME").toString());
                res.getDatas().add(level1Elm);
            }


        }else{
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_SupplierOpenQryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        DCP_SupplierOpenQryReq.levelItem request = req.getRequest();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        Boolean needControl=false;

        String reSql="select distinct a.groupno,c.organizationno,c.status,e.bizpartnerno,e.status as bizStatus" +
                " from DCP_RESTRICTGROUP a " +
                " left join DCP_RESTRICTGROUP_EMP b on a.eid=b.eid and a.grouptype=b.grouptype and a.groupno=b.groupno " +
                " left join DCP_RESTRICTGROUP_ORG c on a.eid=c.eid and a.grouptype=c.grouptype and a.groupno=c.groupno " +
                " left join DCP_RESTRICTGROUP_DEPT d on d.eid=a.eid and d.grouptype=a.grouptype and d.groupno=a.groupno " +
                " left join DCP_RESTRICTGROUP_BIZPARTNER e on e.eid=a.eid and e.grouptype=a.grouptype and e.groupno=a.groupno " +
                " where a.status='100' and a.GROUPTYPE='1' and (( b.status='100' " +
                " and b.employeeno='"+req.getEmployeeNo()+"') or (d.status='100' and d.departmentno='"+req.getDepartmentNo()+"')) " +
                " ";
        List<Map<String, Object>> list = this.doQueryData(reSql, null);
        List<String> bizpartnerNos=new ArrayList<>();
        if(list.size()>0){
            bizpartnerNos = list.stream().filter(x -> {
                String thisOrg = x.get("ORGANIZATIONNO").toString();
                String thisStatus = x.get("STATUS").toString();
                if (Check.Null(x.get("ORGANIZATIONNO").toString())) {
                    return true;
                }
                if (thisOrg.equals(req.getOrganizationNO()) || "100".equals(thisStatus)) {
                    return true;
                }
                return false;
            }).filter(y -> "100".equals(y.get("BIZSTATUS").toString())).map(z -> z.get("BIZPARTNERNO").toString()).distinct().collect(Collectors.toList());
            needControl=true;
        }

        //1.查询统采供应商purType='1'/'2'，根据purType和purCenter关联查询DCP_PURCHASETEMPLATE（统采必须设置采购模板）
        //2.查询自采供应商purType='0'，1️⃣DCP_PURCHASETEMPLATE.PURTYPE=“0” + 2️⃣无DCP_PURCHASETEMPLATE的供应商





        sqlbuf.append("SELECT * FROM ("
                + " SELECT COUNT( a.bizpartnerno ) OVER() NUM ,dense_rank() over(ORDER BY a.bizpartnerno desc) rn,a.*  "
                + " FROM ( "
        );

        sqlbuf.append("select a.bizpartnerno,a.sname,a.fname as fullname,c.CONTYPE as maincontact,c.contact as mainConMan,c.CONTENT as telephone," +
                " a.TAXCODE,d1.TAXNAME,d.TAXRATE,d.INCLTAX,d.taxCalType,a.PAYTYPE,a.PAYDATENO,e.name as paydate_desc,a.BILLDATENO,f.name as billdate_desc," +
                " a.payCenter,g.org_name as paycentername,a.invoicecode,h.invoice_name as invoicename,a.MAINCURRENCY as currency,i.name as currencyname,a.payee,a.payer," +
                " j.sname as payeename,k.sname as payername  " +
                " from dcp_bizpartner a " +
                " left join DCP_BIZPARTNER_ORG b on a.eid=b.eid and a.bizpartnerno=b.bizpartnerno " +
                " left join DCP_BIZPARTNER_CONTACT c on c.eid=a.eid and c.bizpartnerno=a.bizpartnerno and c.ISMAINCONTACT='Y' and c.status='100' " +
                " left join DCP_TAXCATEGORY d on d.eid=a.eid and d.taxcode=a.taxcode " +
                " left join DCP_TAXCATEGORY_lang d1 on d1.eid=a.eid and d1.taxcode=a.taxcode and d1.lang_type='"+req.getLangType()+"' " +
                " left join DCP_PAYDATE_LANG e on e.eid=a.eid and e.paydateno=a.paydateno and e.lang_type='"+req.getLangType()+"' " +
                " left join DCP_BILLDATE_LANG f on f.eid=a.eid and f.billdateno=a.billdateno and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang g on g.eid=a.eid and g.organizationno=a.payCenter and g.lang_type='"+req.getLangType()+"' " +
                " left join DCP_INVOICETYPE_LANG h on h.eid=a.eid and h.invoicecode=a.invoicecode and h.lang_type='"+req.getLangType()+"' " +
                " left join dcp_currency_lang i on i.currency=a.MAINCURRENCY and i.eid=a.eid and i.lang_type='"+req.getLangType()+"'" +
                " left join dcp_bizpartner j on j.eid=a.eid and j.bizpartnerno=a.payee " +
                " left join dcp_bizpartner k on k.eid=a.eid and k.bizpartnerno=a.payer " +
                " ");


        sqlbuf.append(  " where a.eid='"+req.geteId()+"' and a.biztype in ('1','3') " +
                " and a.status='100' and b.organizationno='"+req.getOrganizationNO()+"'  " +  //and c.status='100'
                " and  (to_char(a.BEGINDATE,'yyyyMMdd')<=to_char(sysdate,'yyyyMMdd') or a.BEGINDATE is null or a.BEGINDATE='' )" +
                " and  (to_char(a.ENDDATE,'yyyyMMdd')>=to_char(sysdate,'yyyyMMdd') or a.ENDDATE is null or a.ENDDATE='' )" +
                " "
        );

        if(needControl){
            if(bizpartnerNos.isEmpty()){
                sqlbuf.append(" and 1=2 ");
            }else{
                sqlbuf.append(" and a.bizpartnerno in ('"+String.join("','",bizpartnerNos)+"') ");
            }
        }

        if("1".equals(req.getRequest().getPurType())||"2".equals(req.getRequest().getPurType())){

            String ptSql="select  a.SUPPLIERNO from DCP_PURCHASETEMPLATE a where a.eid='"+req.geteId()+"' " +
                    " and a.purtype='"+req.getRequest().getPurType()+"' " +
                    " and a.purcenter='"+req.getRequest().getPurCenter()+"' ";
            List<Map<String, Object>> ptList = this.doQueryData(ptSql, null);
            if(CollUtil.isNotEmpty(ptList)) {
                String supplierStr = ptList.stream().map(x -> "'" + x.get("SUPPLIERNO").toString() + "'").collect(Collectors.joining(","));
                sqlbuf.append(" and a.bizpartnerno in ("+supplierStr+") ");
            }
            else{
                sqlbuf.append(" and 1=2 ");
            }
        }
        else if("0".equals(req.getRequest().getPurType())){
            String ptSql="select  a.SUPPLIERNO from DCP_PURCHASETEMPLATE a where a.eid='"+req.geteId()+"' " +
                    " and a.purtype='0' " +
                    "  union all " +
                    " select a.BIZPARTNERNO as supplierno from dcp_bizpartner a " +
                    " where a.eid='"+req.geteId()+"' " +
                    " and not exists ( select 1 from DCP_PURCHASETEMPLATE b where b.eid='"+req.geteId()+"' and b.supplierno=a.bizpartnerno )";
            List<Map<String, Object>> ptList = this.doQueryData(ptSql, null);

            if(CollUtil.isNotEmpty(ptList)) {
                String supplierStr = ptList.stream().map(x -> "'" + x.get("SUPPLIERNO").toString() + "'").collect(Collectors.joining(","));
                sqlbuf.append(" and a.bizpartnerno in ("+supplierStr+") ");
            }
            else{
                sqlbuf.append(" and 1=2 ");
            }
        }

        if(Check.NotNull(req.getRequest().getKeyTxt())){
            //keyTxt支持按编号/名称模糊搜索，并且输入字母不区分大小写
            sqlbuf.append("and (upper(a.bizpartnerno) like '%"+req.getRequest().getKeyTxt().toUpperCase()+"%' " +
                    " or a.sname like '%"+req.getRequest().getKeyTxt()+"%' " +
                    " or a.fname like '%"+req.getRequest().getKeyTxt()+"%'" +
                    " ) "
            );
        }


        sqlbuf.append("  ) a  ") ;
        sqlbuf.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + "  ");


        return sqlbuf.toString();

    }

}
