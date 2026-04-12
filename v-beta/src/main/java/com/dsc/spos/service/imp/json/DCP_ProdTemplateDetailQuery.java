package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ProdTemplateDetailQueryReq;
import com.dsc.spos.json.cust.req.DCP_ProdTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProdTemplateDetailQueryRes;
import com.dsc.spos.json.cust.res.DCP_ProdTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ProdTemplateDetailQuery extends SPosBasicService<DCP_ProdTemplateDetailQueryReq, DCP_ProdTemplateDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProdTemplateDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProdTemplateDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_ProdTemplateDetailQueryReq>(){};
    }

    @Override
    protected DCP_ProdTemplateDetailQueryRes getResponseType() {
        return new DCP_ProdTemplateDetailQueryRes();
    }

    @Override
    protected DCP_ProdTemplateDetailQueryRes processJson(DCP_ProdTemplateDetailQueryReq req) throws Exception {
        DCP_ProdTemplateDetailQueryRes res = this.getResponse();

            //单头查询
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            res.setDatas(new ArrayList<>());

            if (getQData != null && !getQData.isEmpty()) {
                for (Map<String, Object> row : getQData){
                    DCP_ProdTemplateDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                    level1Elm.setTemplateId(row.get("TEMPLATEID").toString());
                    level1Elm.setTemplateName(row.get("TEMPLATENAME").toString());
                    level1Elm.setStatus(row.get("STATUS").toString());
                    level1Elm.setRestrictOrg(row.get("RESTRICTORG").toString());
                    level1Elm.setMemo(row.get("MEMO").toString());
                    level1Elm.setCreateOpId(row.get("CREATEOPID").toString());
                    level1Elm.setCreateOpName(row.get("CREATEOPNAME").toString());
                    level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                    level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                    level1Elm.setCreateTime(row.get("CREATETIME").toString());
                    level1Elm.setLastModiOpId(row.get("LASTMODIOPID").toString());
                    level1Elm.setLastModiOpName(row.get("LASTMODIOPNAME").toString());
                    level1Elm.setLastModiTime(row.get("LASTMODITIME").toString());

                    level1Elm.setDetail(new ArrayList<>());
                    level1Elm.setOrgList(new ArrayList<>());

                    String goodsSql = this.getGoodsSql(req);
                    List<Map<String, Object>> getGoodsData=this.doQueryData(goodsSql, null);
                    String orgSql = this.getOrgSql(req);
                    List<Map<String, Object>> getOrgData=this.doQueryData(orgSql, null);
                    if(CollUtil.isNotEmpty(getGoodsData)){
                        for (Map<String, Object> goodsRow : getGoodsData){
                            DCP_ProdTemplateDetailQueryRes.Detail detail = res.new Detail();
                            detail.setPluNo(goodsRow.get("PLUNO").toString());
                            detail.setPluName(goodsRow.get("PLUNAME").toString());
                            detail.setCategory(goodsRow.get("CATEGORY").toString());
                            detail.setCategoryName(goodsRow.get("CATEGORYNAME").toString());
                            detail.setSourceType(goodsRow.get("SOURCETYPE").toString());
                            detail.setBaseUnit(goodsRow.get("BASEUNIT").toString());
                            detail.setBaseUnitName(goodsRow.get("BASEUNITNAME").toString());
                            detail.setProdUnit(goodsRow.get("PRODUNIT").toString());
                            detail.setProdUnitName(goodsRow.get("PRODUNITNAME").toString());
                            detail.setProdMinQty(goodsRow.get("PRODMINQTY").toString());
                            detail.setProdMulQty(goodsRow.get("PRODMULQTY").toString());
                            detail.setRemainType(goodsRow.get("REMAINTYPE").toString());
                            detail.setDispType(goodsRow.get("DISPTYPE").toString());
                            detail.setProcRate(goodsRow.get("PROCRATE").toString());
                            detail.setSemiWoType(goodsRow.get("SEMIWOTYPE").toString());
                            detail.setSemiWoDeptType(goodsRow.get("SEMIWODEPTTYPE").toString());
                            detail.setSdLaborTime(goodsRow.get("SDLABORTIME").toString());
                            detail.setSdMachineTime(goodsRow.get("SDMACHINETIME").toString());
                            detail.setFixPreDays(goodsRow.get("FIXPREDAYS").toString());
                            detail.setStatus(goodsRow.get("STATUS").toString());
                            detail.setCreateOpId(goodsRow.get("CREATEOPID").toString());
                            detail.setCreateOpName(goodsRow.get("CREATEOPNAME").toString());
                            detail.setCreateTime(goodsRow.get("CREATETIME").toString());
                            detail.setLastModiOpId(goodsRow.get("LASTMODIOPID").toString());
                            detail.setLastModiOpName(goodsRow.get("LASTMODIOPNAME").toString());
                            detail.setLastModiTime(goodsRow.get("LASTMODITIME").toString());
                            detail.setOddValue(goodsRow.get("ODDVALUE").toString());
                            detail.setProductExceed(goodsRow.get("PRODUCTEXCEED").toString());
                            detail.setStandardHours(goodsRow.get("STANDARDHOURS").toString());
                            level1Elm.getDetail().add(detail);
                        }
                    }

                    if(CollUtil.isNotEmpty(getOrgData)){
                        for (Map<String, Object> orgRow : getOrgData){
                            DCP_ProdTemplateDetailQueryRes.OrgList orgList = res.new OrgList();
                            orgList.setOrganizationNo(orgRow.get("ORGANIZATIONNO").toString());
                            orgList.setOrganizationName(orgRow.get("ORGANIZATIONNAME").toString());
                            orgList.setStatus(orgRow.get("STATUS").toString());
                            orgList.setCreateOpId(orgRow.get("CREATEOPID").toString());
                            orgList.setCreateOpName(orgRow.get("CREATEOPNAME").toString());
                            orgList.setCreateTime(orgRow.get("CREATETIME").toString());
                            orgList.setLastModiOpId(orgRow.get("LASTMODIOPID").toString());
                            orgList.setLastModiOpName(orgRow.get("LASTMODIOPNAME").toString());
                            orgList.setLastModiTime(orgRow.get("LASTMODITIME").toString());
                            level1Elm.getOrgList().add(orgList);
                        }
                    }


                    res.getDatas().add(level1Elm);

                }

            }

            return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ProdTemplateDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String templateId = req.getRequest().getTemplateId();
        StringBuffer sqlbuf=new StringBuffer();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;


        sqlbuf.append(" "
                + " select  "
                + " a.templateid,a.templateName,a.status,a.restrictOrg,a.memo,a.createOpId ,e1.op_name as createopname,a.lastModiOpId,e2.op_name as lastModiOpName,a.createTime,a.lastModiTime,a.CREATEDEPTID,d1.departname as createdeptname  "
                + " from DCP_PRODTEMPLATE a"
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"'"
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.templateid='"+templateId+"' "

                + " ");

        return sqlbuf.toString();
    }

    private String getGoodsSql(DCP_ProdTemplateDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf =  new StringBuffer();

        sqlbuf.append("select a.pluno,b.plu_name as pluname,c.category,d.category_name as categoryname,a.status,c.baseunit," +
                " f.uname as baseunitname,a.PRODUNIT,g.uname as produnitname,a.prodMinQty,a.prodMulQty,a.remainType,a.dispType,a.procRate,a.semiWoType,a.semiWoDeptType,a.sdLaborTime," +
                " a.sdMachineTime,a.sdMachineTime,a.FIXPREDAYS, " +
                " a.createOpId,e1.op_name as createOpName,a.createTime,a.lastModiOpId,e2.op_name as lastModiOpName," +
                " a.lastModiTime,c.sourceType,a.ODDVALUE,a.PRODUCTEXCEED,a.STANDARDHOURS " +
                " from DCP_PRODTEMPLATE_GOODS a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"'" +
                " left join dcp_goods c on a.eid=c.eid and a.pluno=c.pluno " +
                " left join DCP_CATEGORY_LANG d on a.eid=d.eid and c.category=d.category and d.lang_type='"+ req.getLangType()+"'" +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=c.baseunit and f.lang_type='"+req.getLangType()+"'"+
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=a.produnit and g.lang_type='"+req.getLangType()+"'"+
                " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"' "+
                " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"' "+
                " where a.templateid='"+req.getRequest().getTemplateId()+"' " +
                " and a.eid='"+req.geteId()+"'");

        return sqlbuf.toString();
    }

    private String getOrgSql(DCP_ProdTemplateDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf =  new StringBuffer();
        sqlbuf.append("select a.organizationno,b.org_name as organizationname,a.status," +
                " a.createOpId,e1.op_name as createOpName,a.createTime,a.lastModiOpId,e2.op_name as lastModiOpName," +
                " a.lastModiTime from DCP_PRODTEMPLATE_RANGE a " +
                " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"'" +
                " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"'"+
                " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"'"+
                " where a.templateid='"+req.getRequest().getTemplateId()+"' " +
                " and a.eid='"+req.geteId()+"'");

        return sqlbuf.toString();
    }

}


