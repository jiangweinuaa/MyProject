package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ReturnApplyProDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyProDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReturnApplyProDetailQuery extends SPosBasicService<DCP_ReturnApplyProDetailQueryReq, DCP_ReturnApplyProDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReturnApplyProDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ReturnApplyProDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ReturnApplyProDetailQueryReq>(){};
    }

    @Override
    protected DCP_ReturnApplyProDetailQueryRes getResponseType() {
        return new DCP_ReturnApplyProDetailQueryRes();
    }

    @Override
    protected DCP_ReturnApplyProDetailQueryRes processJson(DCP_ReturnApplyProDetailQueryReq req) throws Exception {
        DCP_ReturnApplyProDetailQueryRes res = this.getResponse();
        if(req.getPageNumber()==0){
            req.setPageNumber(1);
        }

        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            for (Map<String, Object> row : getQData){
                DCP_ReturnApplyProDetailQueryRes.detail detail = res.new detail();

                detail.setBillNo(row.get("BILLNO").toString());
                detail.setOrgNo(row.get("ORGNO").toString());
                detail.setOrgName(row.get("ORGNAME").toString());
                detail.setBDate(row.get("BDATE").toString());
                detail.setItem(row.get("ITEM").toString());
                detail.setApproveStatus(row.get("APPROVESTATUS").toString());
                detail.setPluNo(row.get("PLUNO").toString());
                detail.setPluBarcode(row.get("PLUBARCODE").toString());
                detail.setPluName(row.get("PLUNAME").toString());
                detail.setSpec(row.get("SPEC").toString());
                detail.setFeatureNo(row.get("FEATURENO").toString());
                detail.setFeatureName(row.get("FEATURENAME").toString());
                detail.setPUnit(row.get("PUNIT").toString());
                detail.setPUnitName(row.get("PUNITNAME").toString());
                detail.setPQty(row.get("PQTY").toString());
                detail.setApproveQty(row.get("APPROVEQTY").toString());
                detail.setApprovePrice(row.get("APPROVEPRICE").toString());
                //detail.setApproveAmt(row.get("APPROVEAMT").toString());
                detail.setApproveEmpId(row.get("APPROVEEMPID").toString());
                detail.setApproveEmpName(row.get("APPROVEEMPNAME").toString());
                detail.setApproveDeptId(row.get("APPROVEDEPTID").toString());
                detail.setApproveDeptName(row.get("APPROVEDEPTNAME").toString());
                detail.setApproveDate(row.get("APPROVEDATE").toString());
                detail.setStockOutNo(row.get("STOCKOUTNO").toString());
                detail.setPurType(row.get("PURTYPE").toString());
                detail.setSupplierType(row.get("SUPPLIERTYPE").toString());
                detail.setSupplierId(row.get("SUPPLIERID").toString());
                detail.setSupplierName(row.get("SUPPLIERNAME").toString());
                detail.setReturnType(row.get("RETURNTYPE").toString());
                detail.setReceiptOrgNo(row.get("RECEIPTORGNO").toString());
                detail.setReceiptOrgName(row.get("RECEIPTORGNAME").toString());
                detail.setApproveOrgNo(row.get("APPROVEORGNO").toString());
                detail.setApproveOrgName(row.get("APPROVEORGNAME").toString());
                detail.setReason(row.get("REASON").toString());
                detail.setPrice(row.get("PRICE").toString());
                detail.setAmt(row.get("AMT").toString());
                detail.setDistriPrice(row.get("DISTRIPRICE").toString());
                detail.setDistriAmt(row.get("DISTRIAMT").toString());
                detail.setBaseUnit(row.get("BASEUNIT").toString());
                detail.setBaseUnitName(row.get("BASEUNITNAME").toString());
                detail.setBaseQty(row.get("BASEQTY").toString());
                detail.setBatchNo(row.get("BATCHNO").toString());
                detail.setProdDate(row.get("PRODDATE").toString());
                detail.setExpDate(row.get("EXPDATE").toString());

                BigDecimal approvePrice = new BigDecimal(Check.Null(detail.getApprovePrice())?"0":detail.getApprovePrice());
                BigDecimal approveQty = new BigDecimal(Check.Null(detail.getApproveQty())?"0":detail.getApproveQty());
                BigDecimal approveAmt = approvePrice.multiply(approveQty);
                detail.setApproveAmt(approveAmt.toString());

                res.getDatas().add(detail);


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
    protected String getQuerySql(DCP_ReturnApplyProDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        StringBuffer sqlbuf=new StringBuffer();

        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String approveStatus = req.getRequest().getApproveStatus();
        String returnType = req.getRequest().getReturnType();
        String keyTxt = req.getRequest().getKeyTxt();
        String getType = req.getRequest().getGetType();

        String billNo = req.getRequest().getBillNo();
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.bDate desc, a.billNo DESC ) as rn,"

                + "  a.billno,a.organizationno as orgno,c.org_name as orgname,a.bdate,b.item,b.APPROVESTATUS,b.pluno,b.plubarcode," +
                "   d.plu_name as pluname ,d0.spec,b.featureno,e.featurename,b.punit,f.uname as punitname,b.POQTY as pqty,b.approveQty,b.approvePrice,b.APPROVEEMPID,b.APPROVEDEPTID,g.name as approveempname,h.departname as approvedeptname," +
                " b.approveDate,b.purType,b.supplierType,b.supplierid,case when b.suppliertype='FACTORY' THEN I1.ORG_NAME ELSE i.sname END as supplierName,b.returnType,b.receiptOrgNo,j.org_name as receiptOrgName,b.approveOrgNo,k.org_name as approveorgname,b.reason,b.price,b.amt,b.distriAmt,b.distriprice," +
                " b.baseunit,l.uname as baseunitname,b.PRODDATE,b.expdate,b.baseqty,b.batchno,nvl(sd.stockoutno,ss.SSTOCKOUTNO) as stockoutno  "
                + " from DCP_RETURNAPPLY a " +
                " left join DCP_RETURNAPPLY_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.billno=b.billno " +
                " left join dcp_org_lang c on c.eid=a.eid and a.organizationno=c.organizationno and c.lang_type='"+langType+"' " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=b.pluno and d.lang_type='"+langType+"' " +
                " left join dcp_goods d0 on d0.eid=a.eid and d0.pluno=b.pluno  " +
                " left join dcp_goods_feature_lang e on e.eid=a.eid and e.pluno=b.pluno and e.featureno=b.featureno and e.lang_type='"+langType+"' " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=b.punit and f.lang_type='"+langType+"'" +
                " left join dcp_employee g on g.eid=a.eid and g.employeeno=b.approveempid " +
                " left join dcp_department_lang h on h.eid=a.eid and h.departno=b.approvedeptid " +
                " left join dcp_bizpartner i on i.eid=a.eid and i.BIZPARTNERNO=b.supplierid " +
                " left join dcp_org_lang i1 on i1.eid=a.eid and i1.organizationno=b.supplierid and i1.lang_type='"+langType+"' " +
                " left join dcp_org_lang j on j.eid=a.eid and j.organizationno=b.receiptOrgNo and j.lang_type='"+langType+"' " +
                " left join dcp_org_lang k on k.eid=a.eid and k.organizationno=b.approveorgno and k.lang_type='"+langType+"' " +
                " left join dcp_unit_lang l on l.eid=a.eid and l.unit=b.baseunit and l.lang_type='"+langType+"' " +
                " left join DCP_STOCKOUTNOTICE_detail noticed on noticed.eid=a.eid and noticed.ORGANIZATIONNO=a.ORGANIZATIONNO  and noticed.SOURCEBILLNO=a.billno and noticed.OITEM=b.item and noticed.SOURCETYPE='2' " +
                " left join DCP_SSTOCKOUT_DETAIL ss on ss.eid=a.eid and ss.organizationno=a.organizationno and ss.ofno=noticed.billno and ss.oitem=noticed.item " +
                " left join DCP_STOCKOUT_DETAIL sd on sd.ootype='2' and sd.eid=a.eid and sd.organizationno=a.organizationno and sd.oofno=a.billno and sd.ooitem=b.item " +
                " " +
                ""
                + " where a.eid='"+eId+"'   "
                + " ");

        if(Check.Null(getType)||"0".equals(getType)){
            sqlbuf.append(" and b.organizationno='"+ req.getOrganizationNO()+"' ");
        }else if("1".equals(getType)){
            sqlbuf.append(" and b.approveorgno='"+ req.getOrganizationNO()+"' ");
        }

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.billno like '%"+keyTxt+"%' "
                    + " or b.plubarcode like '%"+keyTxt+"%'" +
                    " or b.pluno like '%"+keyTxt+"%'" +
                    " or d.plu_name like '%"+keyTxt+"%' ) ");
        }
        if(!Check.Null(approveStatus)){
            sqlbuf.append(" and b.approvestatus='"+approveStatus+"'");
        }

        if(!Check.Null(returnType)){
            sqlbuf.append(" and b.returntype='"+returnType+"'");
        }

        if (!Check.Null(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
        }

        if (!Check.Null(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        if (!Check.Null(billNo)){
            sqlbuf.append(" and a.billno='"+billNo+"' ");
        }
        sqlbuf.append(     " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn ");

        return sqlbuf.toString();
    }
}

