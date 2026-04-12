package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TransApplyDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_TransApplyDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_TransApplyDetailQuery extends SPosBasicService<DCP_TransApplyDetailQueryReq, DCP_TransApplyDetailQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_TransApplyDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    protected TypeToken<DCP_TransApplyDetailQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TransApplyDetailQueryReq>(){};
    }

    @Override
    protected DCP_TransApplyDetailQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TransApplyDetailQueryRes();
    }

    @Override
    protected DCP_TransApplyDetailQueryRes processJson(DCP_TransApplyDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        String sql=null;
        DCP_TransApplyDetailQueryRes res = null;
        res = this.getResponse();
        String langType = req.getLangType();

        String DomainName= PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
        String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
        String httpStr=ISHTTPS.equals("1")?"https://":"http://";

        sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        String detailSql = this.getQueryDetailSql(req);
        List<Map<String, Object>> getQDataDetail=this.doQueryData(detailSql, null);

        String querySourceSql = this.getQuerySourceSql(req);
        List<Map<String, Object>> getQDataSource=this.doQueryData(querySourceSql, null);
        if (getQData != null && getQData.isEmpty() == false)
        {
            res.setDatas(new ArrayList<>());
            for (Map<String, Object> singleRow : getQData){
                DCP_TransApplyDetailQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setStatus(singleRow.get("STATUS").toString());
                level1Elm.setBDate(singleRow.get("BDATE").toString());
                level1Elm.setBillNo(singleRow.get("BILLNO").toString());
                level1Elm.setTransType(singleRow.get("TRANSTYPE").toString());
                level1Elm.setTransOutOrgNo(singleRow.get("TRANSOUTORGNO").toString());
                level1Elm.setTransOutOrgName(singleRow.get("TRANSOUTORGNAME").toString());
                level1Elm.setTransOutWarehouse(singleRow.get("TRANSOUTWAREHOUSE").toString());
                level1Elm.setTransOutWarehouseName(singleRow.get("TRANSOUTWAREHOUSENAME").toString());
                level1Elm.setTransInOrgNo(singleRow.get("TRANSINORGNO").toString());
                level1Elm.setTransInOrgName(singleRow.get("TRANSINORGNAME").toString());
                level1Elm.setTransInWarehouse(singleRow.get("TRANSINWAREHOUSE").toString());
                level1Elm.setTransInWarehouseName(singleRow.get("TRANSINWAREHOUSENAME").toString());
                level1Elm.setIsTranInConfirm(singleRow.get("ISTRANINCONFIRM").toString());
                level1Elm.setRDate(singleRow.get("RDATE").toString());
                level1Elm.setPTemplateNo(singleRow.get("PTEMPLATENO").toString());
                level1Elm.setPTemplateName(singleRow.get("PTEMPLATENAME").toString());
                level1Elm.setReason(singleRow.get("REASON").toString());
                level1Elm.setTotCqty(singleRow.get("TOTCQTY").toString());
                level1Elm.setTotPoQty(singleRow.get("TOTPOQTY").toString());
                level1Elm.setTotPqty(singleRow.get("TOTPQTY").toString());
                level1Elm.setTotAmt(singleRow.get("TOTAMT").toString());
                level1Elm.setTotDistriAmt(singleRow.get("TOTDISTRIAMT").toString());
                level1Elm.setEmployeeId(singleRow.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(singleRow.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(singleRow.get("DEPARTID").toString());
                level1Elm.setDepartName(singleRow.get("DEPARTNAME").toString());
                level1Elm.setMemo(singleRow.get("MEMO").toString());
                level1Elm.setCreateBy(singleRow.get("CREATEBY").toString());
                level1Elm.setCreateByName(singleRow.get("CREATEBYNAME").toString());
                level1Elm.setCreateDeptId(singleRow.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(singleRow.get("CREATEDEPTNAME").toString());
                level1Elm.setCreateTime(singleRow.get("CREATETIME").toString());
                level1Elm.setModifyBy(singleRow.get("MODIFYBY").toString());
                level1Elm.setModifyByName(singleRow.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(singleRow.get("MODIFYTIME").toString());
                level1Elm.setSubmitBy(singleRow.get("SUBMITBY").toString());
                level1Elm.setSubmitByName(singleRow.get("SUBMITBYNAME").toString());
                level1Elm.setSubmitTime(singleRow.get("SUBMITTIME").toString());
                level1Elm.setConfirmBy(singleRow.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(singleRow.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(singleRow.get("CONFIRMTIME").toString());
                level1Elm.setCancelBy(singleRow.get("CANCELBY").toString());
                level1Elm.setCancelByName(singleRow.get("CANCELBYNAME").toString());
                level1Elm.setCancelTime(singleRow.get("CANCELTIME").toString());
                level1Elm.setCloseBy(singleRow.get("CLOSEBY").toString());
                level1Elm.setCloseByName(singleRow.get("CLOSEBYNAME").toString());
                level1Elm.setCloseTime(singleRow.get("CLOSETIME").toString());
                level1Elm.setApplyType(singleRow.get("APPLYTYPE").toString());

                level1Elm.setDetail(new ArrayList<>());
                level1Elm.setSource(new ArrayList<>());

                for (Map<String, Object> singleRowDetail : getQDataDetail){
                    DCP_TransApplyDetailQueryRes.Detail detail = res.new Detail();
                    detail.setItem(singleRowDetail.get("ITEM").toString());


                    if (DomainName.endsWith("/")) {
                        detail.setListImage(httpStr+DomainName+"resource/image/" +singleRowDetail.get("LISTIMAGE")==null?"":singleRowDetail.get("LISTIMAGE").toString());
                    } else {
                        detail.setListImage(httpStr+DomainName+"/resource/image/"+singleRowDetail.get("LISTIMAGE")==null?"":singleRowDetail.get("LISTIMAGE").toString());
                    }

                    detail.setPluBarcode(singleRowDetail.get("PLUBARCODE").toString());
                    detail.setPluNo(singleRowDetail.get("PLUNO").toString());
                    detail.setPluName(singleRowDetail.get("PLUNAME").toString());
                    detail.setSpec(singleRowDetail.get("SPEC").toString());
                    detail.setFeatureNo(singleRowDetail.get("FEATURENO").toString());
                    detail.setFeatureName(singleRowDetail.get("FEATURENAME").toString());
                    detail.setPUnit(singleRowDetail.get("PUNIT").toString());
                    detail.setPUnitName(singleRowDetail.get("PUNITNAME").toString());
                    detail.setPoQty(singleRowDetail.get("POQTY").toString());
                    detail.setPQty(singleRowDetail.get("PQTY").toString());
                    detail.setBaseUnit(singleRowDetail.get("BASEUNIT").toString());
                    detail.setBaseQty(singleRowDetail.get("BASEQTY").toString());
                    detail.setUnitRatio(singleRowDetail.get("UNITRATIO").toString());
                    detail.setPrice(singleRowDetail.get("PRICE").toString());
                    detail.setAmt(singleRowDetail.get("AMT").toString());
                    detail.setDistriPrice(singleRowDetail.get("DISTRIPRICE").toString());
                    detail.setDistriAmt(singleRowDetail.get("DISTRIAMT").toString());
                    detail.setMemo(singleRowDetail.get("MEMO").toString());
                    detail.setStatus(singleRowDetail.get("STATUS").toString());
                    detail.setReason(singleRowDetail.get("REASON").toString());
                    detail.setStockOutQty(singleRowDetail.get("STOCKOUTQTY").toString());
                    detail.setStockInQty(singleRowDetail.get("STOCKINQTY").toString());
                    detail.setPickMinQty(singleRowDetail.get("PICKMINQTY").toString());
                    detail.setPickMulQty(singleRowDetail.get("PICKMULQTY").toString());
                    level1Elm.getDetail().add(detail);
                }

                for (Map<String, Object> singleRowSource : getQDataSource){
                    DCP_TransApplyDetailQueryRes.Source source = res.new Source();
                    source.setItem(singleRowSource.get("ITEM").toString());
                    source.setPluNo(singleRowSource.get("PLUNO").toString());
                    source.setPluName(singleRowSource.get("PLUNAME").toString());
                    source.setFeatureNo(singleRowSource.get("FEATURENO").toString());
                    source.setFeatureName(singleRowSource.get("FEATURENAME").toString());
                    source.setOType(singleRowSource.get("OTYPE").toString());
                    source.setOfNo(singleRowSource.get("OFNO").toString());
                    source.setOItem(singleRowSource.get("OITEM").toString());
                    source.setPQty(singleRowSource.get("PQTY").toString());
                    source.setPUnit(singleRowSource.get("PUNIT").toString());
                    source.setPUnitName(singleRowSource.get("PUNITNAME").toString());
                    level1Elm.getSource().add(source);
                }

                res.getDatas().add(level1Elm);
            }

        }


        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        return res;


    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_TransApplyDetailQueryReq req) throws Exception {
        String sql=null;
        StringBuffer sqlbuf=new StringBuffer("");
        String eId = req.geteId();
        DCP_TransApplyDetailQueryReq.LevelElm request = req.getRequest();
        String billNo = request.getBillNo();
        String getType = request.getGetType();

        String langType = req.getLangType();

        sqlbuf.append(
                " SELECT  " +
                " a.status,a.bdate,a.billno,a.transType,a.transOutOrgNo,o1.org_name as transOutOrgName, a.transInOrgNo,o2.org_name as transInOrgName,a.transOutWarehouse,w1.warehouse_name as transOutWarehouseName,a.transInWarehouse,w2.warehouse_name as transInWarehousename," +
                " a.isTranInConfirm,a.rDate,b.ptemplateno,b.ptemplate_name as ptemplatename,a.reason,a.totcqty,a.totpoqty,a.totpqty,a.totamt,a.totdistriamt,a.employeeid,e1.name as employeename,a.memo,a.departid,d1.departname,a.createBy,e2.op_name as createByname,a.createDeptId,d2.departname as createDeptName,a.createtime ," +
                " a.modifyBy,e3.op_name as modifyByname,a.modifyTime, a.submitBy,e4.op_name as submitByname,a.submitTime ,a.confirmBy,e5.op_name as confirmByname,a.confirmtime,a.cancelBy,e6.op_name as cancelByname,a.canceltime,a.closeBy,e7.op_name as closeByname,a.closetime,a.applytype " +
                " FROM DCP_TRANSAPPLY  a" +
                " LEFT JOIN DCP_ORG_LANG o1 ON a.EID = o1.EID AND a.TRANSOUTORGNO = o1.ORGANIZATIONNO " +
                " LEFT JOIN DCP_ORG_LANG o2 ON a.EID = o2.EID AND a.TRANSINORGNO = o2.ORGANIZATIONNO " +
                " LEFT JOIN DCP_WAREHOUSE_LANG w1 on a.eid=w1.eid and a.transoutorgno=w1.organizationno and a.transOutwarehouse=w1.warehouse "+
                " LEFT JOIN DCP_WAREHOUSE_LANG w2 on a.eid=w2.eid and a.transinorgno=w2.organizationno and a.transInwarehouse=w2.warehouse "+
                " LEFT JOIN DCP_PTEMPLATE b ON a.EID = b.EID AND a.ptemplateno = b.ptemplateno " +
                " LEFT JOIN DCP_EMPLOYEE e1 ON a.EID  = e1.EID  AND a.employeeid  = e1.employeeno  " +
                " left join DCP_DEPARTMENT_LANG d1 on a.eid=d1.eid and a.departid=d1.departno and d1.lang_type='"+langType+"'"+
                        " LEFT JOIN platform_staffs_lang e2 ON a.EID  = e2.EID  AND a.createBy  = e2.opno and e2.lang_type='"+langType+"'  " +
                " left join DCP_DEPARTMENT_LANG d2 on a.eid=d2.eid and a.createDeptId=d2.departno and d2.lang_type='"+langType+"'"+
                        " LEFT JOIN platform_staffs_lang e3 ON a.EID  = e3.EID  AND a.modifyBy  = e3.opno and e3.lang_type='"+langType+"' " +
                        " LEFT JOIN platform_staffs_lang e4 ON a.EID  = e4.EID  AND a.submitBy  = e4.opno and e4.lang_type='"+langType+"' " +
                        " LEFT JOIN platform_staffs_lang e5 ON a.EID  = e5.EID  AND a.confirmBy  = e5.opno and e5.lang_type='"+langType+"' " +
                        " LEFT JOIN platform_staffs_lang e6 ON a.EID  = e6.EID  AND a.cancelBy  = e6.opno  and e6.lang_type='"+langType+"' " +
                        " LEFT JOIN platform_staffs_lang e7 ON a.EID  = e7.EID  AND a.closeBy  = e7.opno  and e7.lang_type='"+langType+"' " +
                        " where a.EID = '"+eId+"' and a.billno='"+billNo+"'" +
                " ");
        if(Check.Null(getType)||"0".equals(getType)){
            sqlbuf.append(" and a.organizationno='"+ req.getOrganizationNO()+"' ");
        }else if("1".equals(getType)){
            sqlbuf.append(" and a.APPROVEORGNO='"+ req.getOrganizationNO()+"' ");
        }

        sql=sqlbuf.toString();
        return sql;
    }


    private String getQueryDetailSql(DCP_TransApplyDetailQueryReq req) throws Exception{
        StringBuffer sb = new StringBuffer();
        String billNo = req.getRequest().getBillNo();
        String langType = req.getLangType();
        String eId = req.geteId();

        sb.append(" " +
                "select a.pluno,a.item,a.plubarcode,b.plu_name as pluname,c.spec,f.featureno,f.featurename,a.punit,g.uname as punitname,a.pQty,a.poqty,a.baseunit,h.uname as baseunitname," +
                "a.baseqty,a.unitratio,a.price,a.amt,a.distriprice,a.distriamt,a.memo,a.status,a.stockOutQty,a.stockInQty,im.LISTIMAGE,a.reason,a.pickminqty,a.pickmulqty " +
                "from DCP_TRANSAPPLY_DETAIL a " +
                "left join DCP_GOODS_LANG b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"' " +
                "left join dcp_goods c on c.eid=a.eid and c.pluno=a.pluno " +
                "left join DCP_GOODS_FEATURE_LANG f on f.eid=a.eid and f.pluno=a.pluno and f.featureno=a.featureno and f.lang_type='"+langType+"' " +
                "left join DCP_UNIT_LANG g on g.eid=a.eid and g.unit=a.punit and g.lang_type='"+langType+"' " +
                "left join dcp_unit_lang h on h.eid=a.eid and h.unit=a.baseunit and h.lang_type='"+langType+"' " +
                "left join DCP_GOODSIMAGE im on a.eid=im.eid and a.pluno=im.pluno " +
                "where a.eid='"+eId+"' and a.billNo='"+billNo+"' ");


        return sb.toString();
    }
    private String getQuerySourceSql(DCP_TransApplyDetailQueryReq req) throws Exception{
        StringBuffer sb = new StringBuffer();
        String billNo = req.getRequest().getBillNo();
        String eId = req.geteId();

        sb.append(" " +
                " select a.*,b.plu_name as pluname,c.uname as punitname ,d.featurename " +
                " from DCP_TRANSAPPLY_SOURCE a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.punit and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_feature_lang d on a.eid=d.eid and a.featureno=d.featureno and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+eId+"' and a.billNo='"+billNo+"' ");


        return sb.toString();
    }

}
