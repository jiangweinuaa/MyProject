package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ReturnApplyDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReturnApplyDetailQueryRes;
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

public class DCP_ReturnApplyDetailQuery extends SPosBasicService<DCP_ReturnApplyDetailQueryReq, DCP_ReturnApplyDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReturnApplyDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ReturnApplyDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ReturnApplyDetailQueryReq>(){};
    }

    @Override
    protected DCP_ReturnApplyDetailQueryRes getResponseType() {
        return new DCP_ReturnApplyDetailQueryRes();
    }

    @Override
    protected DCP_ReturnApplyDetailQueryRes processJson(DCP_ReturnApplyDetailQueryReq req) throws Exception {
        DCP_ReturnApplyDetailQueryRes res = this.getResponse();

        String DomainName= PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
        String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
        String httpStr=ISHTTPS.equals("1")?"https://":"http://";

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數

            for (Map<String, Object> row : getQData){
                DCP_ReturnApplyDetailQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setTotAmt(row.get("TOTAMT").toString());
                level1Elm.setTotDistriAmt(row.get("TOTDISTRIAMT").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("MODIFYTIME").toString());
                level1Elm.setSubmitBy(row.get("SUBMITBY").toString());
                level1Elm.setSubmitByName(row.get("SUBMITBYNAME").toString());
                level1Elm.setSubmitTime(row.get("SUBMITTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setCancelBy(row.get("CANCELBY").toString());
                level1Elm.setCancelByName(row.get("CANCELBYNAME").toString());
                level1Elm.setCancelTime(row.get("CANCELTIME").toString());

                BigDecimal totAppPqty=new BigDecimal(0);
                BigDecimal totAppAmt=new BigDecimal(0);
                BigDecimal totAppDistriAmt=new BigDecimal(0);

                level1Elm.setDetail(new ArrayList<>());
                //查询明细
                String detailSql = this.getDetailSql(req);
                List<Map<String, Object>> getDetailQData=this.doQueryData(detailSql, null);

                String imageSql = this.getImageSql(req);
                List<Map<String, Object>> getImageQData=this.doQueryData(imageSql, null);
                if(CollUtil.isNotEmpty(getDetailQData)){
                    for (Map<String, Object> singleRow : getDetailQData){
                        DCP_ReturnApplyDetailQueryRes.detail detail = res.new detail();
                        detail.setItem(singleRow.get("ITEM").toString());
                        detail.setPluBarcode(singleRow.get("PLUBARCODE").toString());
                        detail.setPluNo(singleRow.get("PLUNO").toString());
                        detail.setPluName(singleRow.get("PLUNAME").toString());
                        detail.setSpec(singleRow.get("SPEC").toString());
                        detail.setFeatureNo(singleRow.get("FEATURENO").toString());
                        detail.setFeatureName(singleRow.get("FEATURENAME").toString());
                        detail.setPUnit(singleRow.get("PUNIT").toString());
                        detail.setPUnitName(singleRow.get("PUNITNAME").toString());
                        detail.setBatchNo(singleRow.get("BATCHNO").toString());
                        detail.setProdDate(singleRow.get("PRODDATE").toString());
                        detail.setExpDate(singleRow.get("EXPDATE").toString());
                        detail.setPQty(singleRow.get("PQTY").toString());
                        detail.setBaseUnit(singleRow.get("BASEUNIT").toString());
                        detail.setBaseUnitName(singleRow.get("BASEUNITNAME").toString());
                        detail.setBaseQty(singleRow.get("BASEQTY").toString());
                        detail.setPrice(singleRow.get("PRICE").toString());
                        detail.setAmt(singleRow.get("AMT").toString());
                        detail.setDistriPrice(singleRow.get("DISTRIPRICE").toString());
                        detail.setDistriAmt(singleRow.get("DISTRIAMT").toString());
                        detail.setSupplierType(singleRow.get("SUPPLIERTYPE").toString());
                        detail.setSupplierId(singleRow.get("SUPPLIERID").toString());
                        detail.setSupplierName(singleRow.get("SUPPLIERNAME").toString());
                        detail.setPurType(singleRow.get("PURTYPE").toString());
                        detail.setReturnType(singleRow.get("RETURNTYPE").toString());
                        detail.setReceiptOrgNo(singleRow.get("RECEIPTORGNO").toString());
                        detail.setApproveStatus(singleRow.get("APPROVESTATUS").toString());
                        detail.setApproveQty(singleRow.get("APPROVEQTY").toString());
                        detail.setApprovePrice(singleRow.get("APPROVEPRICE").toString());
                        detail.setApproveOrgNo(singleRow.get("APPROVEORGNO").toString());
                        detail.setApproveOrgName(singleRow.get("APPROVEORGNAME").toString());
                        detail.setApproveEmpId(singleRow.get("APPROVEEMPID").toString());
                        detail.setApproveEmpName(singleRow.get("APPROVEEMPNAME").toString());
                        detail.setApproveDeptId(singleRow.get("APPROVEDEPTID").toString());
                        detail.setApproveDeptName(singleRow.get("APPROVEDEPTNAME").toString());
                        detail.setApproveDate(singleRow.get("APPROVEDATE").toString());
                        detail.setReason(singleRow.get("REASON").toString());
                        detail.setStockOutNo(singleRow.get("STOCKOUTNO").toString());
                        detail.setMemo(singleRow.get("MEMO").toString());
                        detail.setBsNo(singleRow.get("BSNO").toString());
                        detail.setBsName(singleRow.get("BSNAME").toString());
                        detail.setUnitRatio(singleRow.get("UNITRATIO").toString());
                        detail.setImageList(new ArrayList<>());

                        List<Map<String, Object>> imageRows = getImageQData.stream().filter(x -> x.get("OITEM").toString().equals(detail.getItem())).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(imageRows)) {
                            for (Map<String, Object> imageRow : imageRows) {
                                DCP_ReturnApplyDetailQueryRes.ImageList imageList = res.new ImageList();
                                String image=imageRow.get("IMAGE")==null?"":imageRow.get("IMAGE").toString();
                                if (DomainName.endsWith("/")) {
                                    imageList.setImage(httpStr+DomainName+"resource/image/" +image);
                                } else {
                                    imageList.setImage(httpStr+DomainName+"/resource/image/"+image);
                                }
                                detail.getImageList().add(imageList);
                            }
                        }

                        level1Elm.getDetail().add(detail);

                        BigDecimal approveQty=Check.Null(detail.getApproveQty())?new BigDecimal(0):new BigDecimal(detail.getApproveQty());
                        BigDecimal approvePrice=Check.Null(detail.getApprovePrice())?new BigDecimal(0):new BigDecimal(detail.getApprovePrice());
                        BigDecimal distriPrice=Check.Null(detail.getDistriPrice())?new BigDecimal(0):new BigDecimal(detail.getDistriPrice());


                        totAppPqty=totAppPqty.add(approveQty);
                        totAppAmt=totAppAmt.add(approveQty.multiply(approvePrice));
                        totAppDistriAmt=totAppDistriAmt.add(distriPrice.multiply(approveQty));
                    }

                    level1Elm.setTotAppPqty(totAppPqty.toString());
                    level1Elm.setTotAppAmt(totAppAmt.toString());
                    level1Elm.setTotAppDistriAmt(totAppDistriAmt.toString());

                    List<Map> collect = getDetailQData.stream().filter(x -> x.get("APPROVESTATUS").toString().equals("1")).map(y -> {
                        Map map = new HashMap();
                        map.put("pluno", y.get("PLUNO").toString());
                        map.put("featureno", y.get("FEATURENO").toString());
                        return map;
                    }).distinct().collect(Collectors.toList());
                    level1Elm.setTotAppCqty(String.valueOf(collect.size()));


                }

                res.getDatas().add(level1Elm);
            }

        }


        return res;

    }

    private String getDetailSql(DCP_ReturnApplyDetailQueryReq req) {
        String billNo = req.getRequest().getBillNo();
        String eId = req.geteId();
        String langType = req.getLangType();
        String organizationNO = req.getOrganizationNO();
        StringBuffer sb = new StringBuffer();
        sb.append(" select a.billno,a.item,a.pluno,a.PLUBARCODE,b.plu_name as pluname,c.spec,a.featureno,d.featurename,a.punit,e.uname as punitname," +
                " a.batchno,a.proddate,a.expdate,a.poqty as pqty,a.baseunit,f.uname as baseunitname,a.baseqty,a.price,a.amt,a.distriprice,a.distriamt," +
                " a.suppliertype,a.supplierid,g.sname as suppliername,a.purtype,a.RETURNTYPE,a.RECEIPTORGNO ,h.org_name as returnorgname,a.approvestatus," +
                " a.approveqty,a.approveprice,a.approveorgno,i.org_name as approveorgname,a.approveempid,j.name as approveempname,a.APPROVEDEPTID,k.departname as APPROVEDEPTname," +
                " a.APPROVEDATE,a.reason,nvl(sd.stockoutno,ss.SSTOCKOUTNO) as stockoutno,a.memo,l.bsno,l.reason_name as bsname,a.unitratio  " +
                " from " +
                " DCP_RETURNAPPLY_DETAIL a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"' " +
                " left join dcp_goods c on a.eid=c.eid and a.pluno=c.pluno " +
                " left join dcp_goods_feature_lang d on a.eid=d.eid and a.pluno=d.pluno and a.featureno=d.featureno and d.lang_type='"+langType+"'  " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.punit and e.lang_type='"+langType+"' " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.baseunit and f.lang_type='"+langType+"' " +
                " left join dcp_bizpartner g on g.eid=a.eid and g.BIZPARTNERNO=a.supplierid " +
                " left join dcp_org_lang h on h.eid=a.eid and h.organizationno=a.RECEIPTORGNO " +
                " left join dcp_org_lang i on i.eid=a.eid and i.organizationno=a.approveorgno " +
                " left join dcp_employee j on j.eid=a.eid and j.employeeno=a.approveempid " +
                " left join dcp_department_lang k on k.eid=a.eid and k.departno=a.approvedeptid and k.lang_type='"+langType+"' " +
                " left join DCP_STOCKOUTNOTICE_detail noticed on noticed.eid=a.eid and noticed.ORGANIZATIONNO=a.ORGANIZATIONNO  and noticed.SOURCEBILLNO=a.billno and noticed.OITEM=a.item and noticed.SOURCETYPE='2' " +
                " left join DCP_SSTOCKOUT_DETAIL ss on ss.eid=a.eid and ss.organizationno=a.organizationno and ss.ofno=noticed.billno and ss.oitem=noticed.item " +
                " left join DCP_STOCKOUT_DETAIL sd on sd.ootype='2' and sd.eid=a.eid and sd.organizationno=a.organizationno and sd.oofno=a.billno and sd.ooitem=a.item " +
                " left join DCP_REASON_LANG l on l.eid=a.eid and l.bsno=a.bsno and l.lang_type='"+langType+"'" +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.billno='"+billNo+"'" +
                " order by a.item asc ");

        return sb.toString();
    }

    private String getImageSql(DCP_ReturnApplyDetailQueryReq req){
        String billNo = req.getRequest().getBillNo();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        StringBuffer sb = new StringBuffer();
        sb.append("select * from DCP_RETURNAPPLY_IMAGE a where a.eid='"+eId+"' " +
                " and a.organizationno='"+organizationNO+"' " +
                " and a.billno='"+billNo+"'");

        return sb.toString();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ReturnApplyDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();

        StringBuffer sqlbuf=new StringBuffer();


        String billNo = req.getRequest().getBillNo();
        sqlbuf.append(" "
                + " select  "
                + " a.status,a.bdate,a.billno,a.totCqty,a.totpqty,a.totamt,a.totdistriamt," +
                " a.totCqty  totappCqty,a.totpqty totapppqty,a.totamt totappamt,a.totdistriamt totappdistriamt,"
                + " a.CREATEOPID AS CREATEBY ,e1.op_name as createbyname,a.LASTMODIOPID AS modifyby,e2.op_name as modifybyname,a.createTime,a.LASTMODITIME as MODIFYTIME,a.departId,d1.departname as departNAME,a.confirmby,a.confirmtime,e3.op_name as confirmbyname,"
                + " a.cancelby,e4.op_name as cancelbyname,a.SUBMITBY,e6.op_name as SUBMITBYNAME,a.canceltime,a.SUBMITTIME,a.employeeid,e0.name as employeename,a.memo,a.CREATEDEPTID,d2.departname as createdeptname "
                + " from DCP_RETURNAPPLY a "
                + " left join dcp_employee e0 on e0.eid=a.eid and e0.employeeno=a.EMPLOYEEID "
                + " left join platform_staffs_lang e1 on e1.eid=a.eid and e1.opno=a.CREATEOPID and e1.lang_type='"+req.getLangType()+"' "
                + " left join platform_staffs_lang e2 on e2.eid=a.eid and e2.opno=a.LASTMODIOPID and e2.lang_type='"+langType+"'  "
                + " left join platform_staffs_lang e3 on e3.eid=a.eid and e3.opno=a.CONFIRMBY and e3.lang_type='"+langType+"' "
                + " left join platform_staffs_lang e4 on e4.eid=a.eid and e4.opno=a.cancelby and e4.lang_type='"+langType+"' "
                + " left join platform_staffs_lang e6 on e6.eid=a.eid and e6.opno=a.submitby and e6.lang_type='"+langType+"' "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " left join dcp_department_lang d2 on d2.eid=a.eid and d2.departno=a.createdeptid and d2.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.billno='"+billNo+"' "
                + " ");


        return sqlbuf.toString();
    }
}



