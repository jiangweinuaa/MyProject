package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DistriOrderDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_DistriOrderDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_DistriOrderDetailQuery extends SPosBasicService<DCP_DistriOrderDetailQueryReq, DCP_DistriOrderDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DistriOrderDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_DistriOrderDetailQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_DistriOrderDetailQueryReq>(){};
    }

    @Override
    protected DCP_DistriOrderDetailQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_DistriOrderDetailQueryRes();
    }

    @Override
    protected DCP_DistriOrderDetailQueryRes processJson(DCP_DistriOrderDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_DistriOrderDetailQueryRes res = this.getResponse();

        String DomainName= PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
        String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
        String httpStr=ISHTTPS.equals("1")?"https://":"http://";

        String isBatchPara = PosPub.getPARA_SMS(dao, req.geteId(), "", "Is_BatchNO");
        if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")){
            isBatchPara="N";
        }
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());
        if (getQData != null && getQData.isEmpty() == false)
        {
            DCP_DistriOrderDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
            Map<String, Object> oneData = getQData.get(0);
            level1Elm.setStatus(oneData.get("STATUS").toString());
            level1Elm.setBillNo(oneData.get("BILLNO").toString());
            level1Elm.setBDate(oneData.get("BDATE").toString());
            level1Elm.setRDate(oneData.get("RDATE").toString());
            level1Elm.setDemandOrgNo(oneData.get("DEMANDORGNO").toString());
            level1Elm.setDemandOrgName(oneData.get("DEMANDORGNAME").toString());
            level1Elm.setMemo(oneData.get("MEMO").toString());
            level1Elm.setEmployeeId(oneData.get("EMPLOYEEID").toString());
            level1Elm.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
            level1Elm.setDepartId(oneData.get("DEPARTID").toString());
            level1Elm.setDepartName(oneData.get("DEPARTNAME").toString());
            level1Elm.setCreateOpId(oneData.get("CREATEOPID").toString());
            level1Elm.setCreateOpName(oneData.get("CREATEOPNAME").toString());
            level1Elm.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
            level1Elm.setCreatDeptName(oneData.get("CREATDEPTNAME").toString());
            level1Elm.setCreateDateTime(oneData.get("CREATEDATETIME").toString());
            level1Elm.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
            level1Elm.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
            level1Elm.setLastModiDateTime(oneData.get("LASTMODIDATETIME").toString());
            level1Elm.setConfirmBy(oneData.get("CONFIRMBY").toString());
            level1Elm.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
            level1Elm.setConfirmDateTime(oneData.get("CONFIRMDATETIME").toString());
            level1Elm.setCancelBy(oneData.get("CANCELBY").toString());
            level1Elm.setCancelByName(oneData.get("CANCELBYNAME").toString());
            level1Elm.setCancelDateTime(oneData.get("CANCELDATETIME").toString());


            level1Elm.setDetailList(new ArrayList<>());

            String detailSql=this.getDetailSql(req);
            List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);
            if (getDetailData != null && getDetailData.isEmpty() == false)
            {
                for (Map<String, Object> oneDetail : getDetailData)
                {
                    DCP_DistriOrderDetailQueryRes.DetailList detailList = res.new DetailList();
                    detailList.setItem(oneDetail.get("ITEM").toString());
                    detailList.setDemandOrgNo(oneDetail.get("DEMANDORGNO").toString());
                    detailList.setDemandOrgName(oneDetail.get("DEMANDORGNAME").toString());
                    detailList.setRDate(oneDetail.get("RDATE").toString());
                    detailList.setPluBarcode(oneDetail.get("PLUBARCODE").toString());
                    detailList.setPluNo(oneDetail.get("PLUNO").toString());
                    detailList.setPluName(oneDetail.get("PLUNAME").toString());
                    detailList.setSpec(oneDetail.get("SPEC").toString());
                    detailList.setFeatureNo(oneDetail.get("FEATURENO").toString());
                    detailList.setFeatureName(oneDetail.get("FEATURENAME").toString());
                    detailList.setPUnit(oneDetail.get("PUNIT").toString());
                    detailList.setPUnitName(oneDetail.get("PUNITNAME").toString());
                    detailList.setPQty(oneDetail.get("PQTY").toString());
                    detailList.setPrice(oneDetail.get("PRICE").toString());
                    detailList.setAmt(oneDetail.get("AMT").toString());
                    detailList.setDistriPrice(oneDetail.get("DISTRIPRICE").toString());
                    detailList.setDistriAmt(oneDetail.get("DISTRIAMT").toString());
                    detailList.setSupplierType(oneDetail.get("SUPPLIERTYPE").toString());
                    detailList.setSupplierId(oneDetail.get("SUPPLIERID").toString());
                    detailList.setSupplierName(oneDetail.get("SUPPLIERNAME").toString());

                    detailList.setBaseUnit(oneDetail.get("BASEUNIT").toString());
                    detailList.setBaseQty(oneDetail.get("BASEQTY").toString());
                    detailList.setUnitRatio(oneDetail.get("UNITRATIO").toString());

                    level1Elm.getDetailList().add(detailList);
                }
            }

            res.getDatas().add(level1Elm);
            level1Elm=null;
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_DistriOrderDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        String billNo = req.getRequest().getBillNo();
        sqlbuf.append(" select a.status,a.billno,a.bdate ,a.rdate,b.organizationno as demandOrgNo,b.org_name as demandOrgName,a.memo," +
                " a.EMPLOYEEID,em0.name as employeeName,a.DEPARTID,dd0.DEPARTNAME,a.createOpId,em1.op_name as createOpName,dd1.DEPARTNO as createDeptId,dd1.DEPARTNAME as creatDeptName,a.CREATETIME as createDateTime," +
                " a.lastModiOpId,em2.op_name as lastModiOpName,a.LASTMODITIME as lastModiDateTime,a.confirmBy,em3.op_name as confirmByName,a.cancelBy,em4.op_name as cancelByName,a.CONFIRMTIME as confirmDateTime,a.CANCELTIME as cancelDateTime   " +
                " from DCP_DITRIORDER a " +
                " left join dcp_org_lang b on a.DEMANDORGNO=b.organizationno and a.eid=b.eid and b.lang_type='"+langType+"' " +
                " left join dcp_employee em0 on a.eid=em0.eid and a.EMPLOYEEID=em0.employeeno " +
                " left join DCP_DEPARTMENT_LANG dd0 on dd0.eid=a.eid and a.DEPARTID=dd0.DEPARTNO and dd0.lang_type='"+langType+"' " +
                " left join platform_staffs_lang em1 on a.eid=em1.eid and a.CREATEOPID=em1.opno and em1.lang_type='"+langType+"' " +
                " left join DCP_DEPARTMENT_LANG dd1 on dd1.eid=a.eid and a.CREATEDEPTID=dd1.DEPARTNO and dd1.lang_type='"+langType+"' " +
                " left join platform_staffs_lang em2 on a.eid=em2.eid and a.LASTMODIOPID=em2.opno and em2.lang_type='"+langType+"' " +
                " left join platform_staffs_lang em3 on a.eid=em3.eid and a.CONFIRMBY=em3.opno  and em3.lang_type='"+langType+"'" +
                " left join platform_staffs_lang em4 on a.eid=em4.eid and a.CANCELBY=em4.opno and em4.lang_type='"+langType+"' " +
                " where a.eid='"+eId+"' and a.billno='"+billNo+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                "");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_DistriOrderDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String langType = req.getLangType();

        sqlbuf.append("select b.item,b.DEMANDORGNO,g.org_name as demandOrgName,b.RDATE,b.PLUBARCODE,b.pluno,d.plu_name as pluname," +
                "  c.spec,f.featureno,f.featurename,b.punit,h.uname as punitname,b.pqty,b.price,b.amt,b.DISTRIPRICE,b.FDISTRIAMOUNT as distriAmt,b.SUPPLIERTYPE,b.SUPPLIERID," +
                " case when b.suppliertype='FACTORY' THEN J.org_name ELSE I.SNAME END as supplierName,b.unitratio,b.baseunit,b.baseqty "+
                " from DCP_DITRIORDER a" +
                " inner join DCP_DITRIORDER_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.BILLNO=b.BILLNO " +
                " left  join dcp_goods c on b.eid=c.eid and c.pluno=b.pluno " +
                " left join DCP_GOODS_LANG d on d.eid=c.eid and c.pluno=d.pluno and d.lang_type='"+langType+"' " +
                //" left join DCP_GOODS_BARCODE e on e.eid =c.eid and e.pluno=c.pluno " +
                " left join DCP_GOODS_FEATURE_lang f on f.eid=b.eid and f.pluno=b.pluno and f.featureno=b.featureno and f.lang_type='"+langType+"' " +
                " left join dcp_org_lang g on b.DEMANDORGNO=g.organizationno and a.eid=g.eid and g.lang_type='"+langType+"' " +
                " left join DCP_UNIT_LANG h on h.unit=b.punit and h.eid=b.eid and h.lang_type='"+langType+"' " +
                " left join DCP_BIZPARTNER i on i.eid=b.eid and i.BIZPARTNERNO=b.supplierid and i.biztype in ('1','3') and b.ORGANIZATIONNO=i.ORGANIZATIONNO " +
                " left join dcp_org_lang j on j.eid=a.eid and j.organizationno=b.supplierid and j.lang_type='"+langType+"'"+
                " where a.billno='"+req.getRequest().getBillNo()+"' and a.eid='"+req.geteId()+"' " +
                " and a.organizationno='"+req.getOrganizationNO()+"'" +
                " order by b.item ");

        return sqlbuf.toString();
    }

}


