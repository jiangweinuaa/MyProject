package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_STakeTemplateDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_STakeTemplateDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_STakeTemplateDetailQuery  extends SPosBasicService<DCP_STakeTemplateDetailQueryReq, DCP_STakeTemplateDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_STakeTemplateDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_STakeTemplateDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_STakeTemplateDetailQueryReq>(){};
    }

    @Override
    protected DCP_STakeTemplateDetailQueryRes getResponseType() {
        return new DCP_STakeTemplateDetailQueryRes();
    }

    @Override
    protected DCP_STakeTemplateDetailQueryRes processJson(DCP_STakeTemplateDetailQueryReq req) throws Exception {
        DCP_STakeTemplateDetailQueryRes res = this.getResponse();

        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        if (getQData != null && !getQData.isEmpty()) {
            res.setDatas(new ArrayList<>());
            for (Map<String, Object> oneData : getQData) {
                DCP_STakeTemplateDetailQueryRes.Level1Elm oneLv1 = res.new Level1Elm();

                String isAdjustStock = oneData.get("IS_ADJUST_STOCK").toString();   //是否调整库存Y/N/X Y转库存 N转销售 X不异动
                if (Check.Null(isAdjustStock)){
                    isAdjustStock="Y" ;
                }
                String rangeWay = oneData.get("RANGEWAY").toString();
                if (Check.Null(rangeWay)){
                    rangeWay="0" ;
                }
                String isShowZStock =oneData.get("ISSHOWZSTOCK").toString();
                if (Check.Null(isShowZStock) || !isShowZStock.equals("0")){
                    isShowZStock="1" ;
                }

                oneLv1.setTemplateNo(oneData.get("PTEMPLATENO").toString());
                oneLv1.setTemplateName(oneData.get("PTEMPLATE_NAME").toString());
                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setIsBtake(oneData.get("IS_BTAKE").toString());
                oneLv1.setTaskWay(oneData.get("TASKWAY").toString());
                oneLv1.setTimeType(oneData.get("TIME_TYPE").toString());
                oneLv1.setTimeValue(oneData.get("TIME_VALUE").toString());
                oneLv1.setStockTakeCheck(oneData.get("STOCKTAKECHECK").toString());
                oneLv1.setShopType(oneData.get("SHOPTYPE").toString());
                oneLv1.setIsAdjustStock(isAdjustStock);
                oneLv1.setRangeWay(rangeWay);
                oneLv1.setIsShowZStock(isShowZStock);
                oneLv1.setCreateDate(oneData.get("CREATE_DATE").toString());
                oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                oneLv1.setCreateBy(oneData.get("CREATEBY").toString());
                oneLv1.setCreateByName(oneData.get("CREATEBYNAME").toString());
                oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
                oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
                oneLv1.setModifyDate(oneData.get("MODIFYDATE").toString());
                oneLv1.setModifyTime(oneData.get("MODIFYTIME").toString());
                oneLv1.setModifyBy(oneData.get("MODIFYBY").toString());
                oneLv1.setModifyByName(oneData.get("MODIFYBYNAME").toString());

                oneLv1.setDetail(new ArrayList<>());
                oneLv1.setOrgList(new ArrayList<>());

                String detailSql = this.getDetailSql(req);
                List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);
                if(CollUtil.isNotEmpty(getDetailData)){
                    for (Map<String, Object> oneDetailData : getDetailData){
                        DCP_STakeTemplateDetailQueryRes.Detail detail = res.new Detail();
                        detail.setItem(oneDetailData.get("ITEM").toString());
                        detail.setPluNo(oneDetailData.get("PLUNO").toString());
                        detail.setPluName(oneDetailData.get("PLUNAME").toString());
                        detail.setCategory(oneDetailData.get("CATEGORY").toString());
                        detail.setCategoryName(oneDetailData.get("CATEGORYNAME").toString());
                        detail.setPUnit(oneDetailData.get("PUNIT").toString());
                        detail.setPUnitName(oneDetailData.get("PUNITNAME").toString());
                        detail.setStatus(oneDetailData.get("STATUS").toString());
                        oneLv1.getDetail().add(detail);
                    }
                }

                String orgSql = this.getOrgSql(req);
                List<Map<String, Object>> getOrgData=this.doQueryData(orgSql, null);
                if(CollUtil.isNotEmpty(getOrgData)){
                    for (Map<String, Object> oneOrgData : getOrgData){
                        DCP_STakeTemplateDetailQueryRes.OrgList orgList = res.new OrgList();
                        orgList.setOrganizationNo(oneOrgData.get("ORGANIZATIONNO").toString());
                        orgList.setOrganizationName(oneOrgData.get("ORGANIZATIONNAME").toString());
                        orgList.setStatus(oneOrgData.get("STATUS").toString());
                        orgList.setWarehouse(oneOrgData.get("WAREHOUSE").toString());
                        orgList.setWarehouseName(oneOrgData.get("WAREHOUSENAME").toString());
                        oneLv1.getOrgList().add(orgList);
                    }
                }

                res.getDatas().add(oneLv1);
            }
        }



        return res;


    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_STakeTemplateDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String templateNo = req.getRequest().getTemplateNo();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();


        sqlbuf.append(" "
                + " select  "
                + " a.ptemplateno,a.ptemplate_name,a.is_btake,a.taskway,a.time_type,a.time_value,"
                + " a.status,a.stocktakecheck,a.shoptype,a.is_adjust_stock,a.rangeway,a.isshowzstock,a.create_date,a.CREATE_TIME as createtime,"
                + " a.CREATEBY,e1.name as createbyname,a.CREATEDEPTID,d1.departname as CREATEDEPTNAME,a.modify_date as modifydate,a.modify_time as modifytime ,"
                + " a.MODIFYBY,e2.name as MODIFYBYNAME "
                + " from dcp_ptemplate a"
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.CREATEBY "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.MODIFYBY "
                + " where a.eid='"+eId+"' and a.ptemplateno='"+templateNo+"'"
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_STakeTemplateDetailQueryReq req) throws Exception{
        String eId = req.geteId();
        String templateNo = req.getRequest().getTemplateNo();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("select b.item,b.pluno,c.plu_name as pluname,d.category,d.category_name as categoryname,b.punit,e.uname as punitname,b.status  " +
                " from DCP_PTEMPLATE a " +
                " inner join DCP_PTEMPLATE_DETAIL b on a.eid=b.eid and a.ptemplateno=b.ptemplateno " +
                " left join dcp_goods_lang c on c.eid=b.eid and c.pluno=b.pluno and c.lang_type='"+langType+"' " +
                " left join dcp_category_lang d on d.eid=a.eid and d.category=b.CATEGORYNO " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=b.punit and e.lang_type='"+langType+"' " +
                " where a.ptemplateno='"+templateNo +"' and a.eid='"+eId+"'");


        return sqlbuf.toString();
    }

    private String getOrgSql(DCP_STakeTemplateDetailQueryReq req) throws Exception{
        String eId = req.geteId();
        String templateNo = req.getRequest().getTemplateNo();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append(" select b.organizationno,c.org_name as organizationname,w.warehouse,w.warehouse_name as warehousename,b.status from dcp_ptemplate a " +
                " inner join DCP_PTEMPLATE_SHOP b on a.eid=b.eid and a.ptemplateno=b.ptemplateno " +
                " left join dcp_org_lang c on b.eid=c.eid and b.organizationno=c.organizationno and c.lang_type='"+langType+"' " +
                " left join dcp_warehouse_lang w on w.eid=a.eid and b.organizationno=w.organizationno and w.warehouse=b.warehouse and w.lang_type='"+langType+"'" +
                " where a.eid='"+eId+"' and a.ptemplateno='"+templateNo+"' ");


        return sqlbuf.toString();
    }

}


