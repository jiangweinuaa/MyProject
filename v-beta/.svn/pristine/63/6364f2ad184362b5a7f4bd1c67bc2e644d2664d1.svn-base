package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ProductInDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProductInDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProductInDetailQuery extends SPosBasicService<DCP_ProductInDetailQueryReq, DCP_ProductInDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProductInDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProductInDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ProductInDetailQueryReq>(){};
    }

    @Override
    protected DCP_ProductInDetailQueryRes getResponseType() {
        return new DCP_ProductInDetailQueryRes();
    }

    @Override
    protected DCP_ProductInDetailQueryRes processJson(DCP_ProductInDetailQueryReq req) throws Exception {
        DCP_ProductInDetailQueryRes res = this.getResponse();
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        String detailSql=this.getDetailSql(req);
        String scrapSql = this.getScrapSql(req);
        List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);
        List<Map<String, Object>> getScrapData = this.doQueryData(scrapSql, null);

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_ProductInDetailQueryRes.Level1Elm levelElm=res.new Level1Elm();
                levelElm.setBDate(row.get("BDATE").toString());
                levelElm.setProductInNo(row.get("PRODUCTIONNO").toString());
                levelElm.setPGroupNo(row.get("PGROUPNO").toString());
                levelElm.setPGroupName(row.get("PGROUPNAME").toString());
                levelElm.setCreateBy(row.get("CREATEBY").toString());
                levelElm.setCreateByName(row.get("CREATEBYNAME").toString());
                levelElm.setCreateTime(row.get("CREATETIME").toString());
                levelElm.setModifyBy(row.get("MODIFYBY").toString());
                levelElm.setModifyByName(row.get("MODIFYBYNAME").toString());
                levelElm.setModifyTime(row.get("MODIFYTIME").toString());
                levelElm.setConfirmBy(row.get("CONFIRMBY").toString());
                levelElm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                levelElm.setConfirmTime(row.get("CONFIRMTIME").toString());
                levelElm.setProcessStatus(row.get("PROCESSSTATUS").toString());
                levelElm.setProcessErpNo(row.get("PROCESSERPNO").toString());
                levelElm.setProcessErpOrg(row.get("PROCESSERPORG").toString());
                levelElm.setSourceReportNo(row.get("SOURCEREPORTNO").toString());
                levelElm.setReturnNo(row.get("RETURNNO").toString());
                levelElm.setReturnStatus(row.get("RETURNSTATUS").toString());
                levelElm.setDepartId(row.get("DEPARTID").toString());
                levelElm.setDepartName(row.get("DEPARTNAME").toString());

                levelElm.setDatas(new ArrayList<>());
                if(CollUtil.isNotEmpty(getDetailData)){
                    for (Map<String, Object> detailRow : getDetailData){
                        DCP_ProductInDetailQueryRes.Detail detail = res.new Detail();
                        detail.setItem(detailRow.get("ITEM").toString());
                        detail.setPluNo(detailRow.get("PLUNO").toString());
                        detail.setPluName(detailRow.get("PLUNAME").toString());
                        detail.setSpec(detailRow.get("SPEC").toString());
                        detail.setPUnit(detailRow.get("PUNIT").toString());
                        detail.setPUName(detailRow.get("PUNITNAME").toString());
                        detail.setPQty(detailRow.get("PQTY").toString());
                        detail.setBatchNo(detailRow.get("BATCHNO").toString());
                        detail.setWarehouse(detailRow.get("WAREHOUSE").toString());
                        detail.setWarehouseName(detailRow.get("WAREHOUSENAME").toString());
                        detail.setBaseUnit(detailRow.get("BASEUNIT").toString());
                        detail.setBaseUName(detailRow.get("BASEUNITNAME").toString());
                        detail.setBaseQty(detailRow.get("BASEQTY").toString());
                        detail.setSourceNo(detailRow.get("SOURCENO").toString());
                        detail.setScrapQty(detailRow.get("SCRAPQTY").toString());
                        detail.setFeatureNo(detailRow.get("FEATURENO").toString());
                        detail.setFeatureName(detailRow.get("FEATURENAME").toString());
                        detail.setScrapList(new ArrayList<>());
                        List<Map<String, Object>> scraps = getScrapData.stream().filter(x -> x.get("OITEM").toString().equals(detail.getItem())).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(scraps)){
                            for (Map<String, Object> scrap : scraps){
                                DCP_ProductInDetailQueryRes.ScrapList scrapList = res.new ScrapList();
                                scrapList.setReason(scrap.get("REASON").toString());
                                scrapList.setScrapQty(scrap.get("SCRAPQTY").toString());
                                detail.getScrapList().add(scrapList);
                            }
                        }

                        levelElm.getDatas().add(detail);

                    }
                }

                res.getDatas().add(levelElm);

            }

        }

        return res;

    }

    private String getDetailSql(DCP_ProductInDetailQueryReq req) throws Exception {
        StringBuffer sb=new StringBuffer();

        sb.append("select a.item,a.pluno,b.plu_name as pluname,a.punit,b1.spec,c.uname as punitname,a.pqty,a.batchno,a.warehouse,d.warehouse_name as warehousename,a.baseunit,e.uname as baseunitname," +
                " a.baseqty,a.sourceno,a.scrapQty,a.featureno,f.featurename " +
                " from MES_PRODUCTIN_DETAIL a" +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods b1 on a.eid=b1.eid and a.pluno=b1.pluno  " +
                " left join dcp_unit_lang c on a.eid=b.eid and a.punit=c.unit and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse_lang d on d.eid=a.eid and a.organizationno=d.organizationno and a.warehouse=d.warehouse and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.baseunit and e.lang_type='"+req.getLangType()+"'" +
                " left join DCP_GOODS_FEATURE_LANG f on f.eid=a.eid and f.pluno=a.pluno and f.featureno=a.featureno and f.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.productionno='"+req.getRequest().getProductInNo()+"'");

        return sb.toString();
    }

    private String getScrapSql(DCP_ProductInDetailQueryReq req ) throws Exception {
        StringBuffer sb=new StringBuffer();

        sb.append("select a.item,a.oitem ,a.SCRAPQTY,a.reason" +
                " from MES_PRODUCTIN_DETAIL_SCRAP a " +
                " where a.eid='"+req.geteId()+"' " +
                " and a.organizationno='"+req.getOrganizationNO()+"'" +
                " and a.PRODUCTIONNO='"+req.getRequest().getProductInNo()+"'");

        return sb.toString();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ProductInDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        String productInNo = req.getRequest().getProductInNo();
        //分页处理

        sqlbuf.append(" "
                + " select "
                + " a.bdate,a.PRODUCTIONNO as productionno,a.pgroupno,c.pgroupname,a.CREATEOPID as createby,e1.name as createbyname,to_char(a.createtime,'yyyy-MM-dd HH:mm:ss') as createtime,a.lastModiOpId as modifyby,e2.name as modifybyname,to_char(a.LASTMODITIME,'yyyy-MM-dd HH:mm:ss') as modifyTime,"
                + " a.ACCOUNTOPID as confirmby,e3.name as confirmbyname,to_char(a.ACCOUNTTIME,'yyyy-MM-dd HH:mm:ss') as confirmtime,a.PROCESS_STATUS as processstatus,a.PROCESS_ERP_NO as processerpno,a.PROCESS_ERP_ORG as processerporg,a.SOURCEREPORTNO,a.RETURNNO,a.RETURNSTATUS,a.departId,d1.departname  "
                + " from MES_PRODUCTIN a"
                + " left join MES_PRODUCT_GROUP c on c.eid=a.eid and c.pgroupno=a.pgroupno "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.createOpId "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.lastModiOpId "
                + " left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.ACCOUNTOPID "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departId and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.PRODUCTIONNO='"+productInNo+"' "
               + " ");

        return sqlbuf.toString();
    }
}


