package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_BomVersionDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_BomDetailRes;
import com.dsc.spos.json.cust.res.DCP_BomVersionDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BomVersionDetailQuery extends SPosBasicService<DCP_BomVersionDetailQueryReq, DCP_BomVersionDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_BomVersionDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_BomVersionDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_BomVersionDetailQueryReq>(){};
    }

    @Override
    protected DCP_BomVersionDetailQueryRes getResponseType() {
        return new DCP_BomVersionDetailQueryRes();
    }

    @Override
    protected DCP_BomVersionDetailQueryRes processJson(DCP_BomVersionDetailQueryReq req) throws Exception {
        DCP_BomVersionDetailQueryRes res = this.getResponse();
        
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {

            String versionNumStr = getQData.stream().map(x -> "'" + x.get("VERSIONNUM").toString() + "'").distinct().collect(Collectors.joining(","));
            String materialSql="select a.*,b.plu_name as materialPluName,c.uname as materialUnitName,d.WGROUPNAME as pwgroupname,e.WGROUPNAME as kwgroupname " +
                    " from " +
                    " DCP_BOM_MATERIAL_V a " +
                    " left join dcp_goods_lang b on a.eid=b.eid and a.material_pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_unit_lang c on a.eid=c.eid and a.material_unit=c.unit and c.lang_type='"+req.getLangType()+"' " +
                    " left join MES_WAREHOUSE_GROUP d on d.eid=a.eid and a.PWGROUPNO=d.wgroupno " +
                    " left join MES_WAREHOUSE_GROUP e on e.eid=a.eid and a.KWGROUPNO=e.wgroupno " +
                    " where a.eid='"+req.geteId()+"' and a.bomno='"+req.getRequest().getBomNo()+"' and a.versionNum in ("+versionNumStr+") ";
            List<Map<String, Object>> getMaterialQData=this.doQueryData(materialSql, null);

            String rangeSql=" select a.*,b.org_name as shopname,c.org_name as organizationname" +
                    " from DCP_BOM_RANGE_V a " +
                    " left join dcp_org_lang b on a.eid=b.eid and a.shopid=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_org_lang c on a.eid=c.eid and a.organizationno=c.organizationno and c.lang_type='"+req.getLangType()+"'" +
                    " where a.eid='"+req.geteId()+"' and a.bomno='"+req.getRequest().getBomNo()+"' and a.versionNum in ("+versionNumStr+") ";
            List<Map<String, Object>> getRangeQData=this.doQueryData(rangeSql, null);

            //农副产品
            String coSql="select a.*,b.plu_name as pluname ,c.uname  " +
                    "from DCP_BOM_COBYPRODUCT_v a" +
                    " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.unit and c.lang_type='"+req.getLangType()+"' " +
                    " where a.eid='"+req.geteId()+"' and a.versionNum in ("+versionNumStr+")  " +
                    " ";
            List<Map<String, Object>> coList = this.doQueryData(coSql, null);



            for (Map<String, Object> row : getQData){
                DCP_BomVersionDetailQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setBomNo(row.get("BOMNO").toString());
                level1Elm.setBomType(row.get("BOMTYPE").toString());
                level1Elm.setVersionNum(row.get("VERSIONNUM").toString());
                level1Elm.setPluNo(row.get("PLUNO").toString());
                level1Elm.setPluName(row.get("PLUNAME").toString());
                level1Elm.setUnit(row.get("UNIT").toString());
                level1Elm.setUnitName(row.get("UNITNAME").toString());
                level1Elm.setMulQty(row.get("MULQTY").toString());
                level1Elm.setEffDate(row.get("EFFDATE").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setBatchQty(row.get("BATCHQTY").toString());
                level1Elm.setSpec(row.get("SPEC").toString());
                level1Elm.setCategory(row.get("CATEGORY").toString());
                level1Elm.setCategoryName(row.get("CATEGORYNAME").toString());
                level1Elm.setBaseUnit(row.get("BASEUNIT").toString());
                level1Elm.setBaseUnitName(row.get("BASEUNITNAME").toString());
                level1Elm.setProdType(row.get("PRODTYPE").toString());
                level1Elm.setRestrictShop(row.get("RESTRICTSHOP").toString());
                level1Elm.setFixedLossQty(row.get("FIXEDLOSSQTY").toString());
                level1Elm.setIsProcessEnable(row.get("ISPROCESSENABLE").toString());
                level1Elm.setInWGroupNo(row.get("INWGROUPNO").toString());
                level1Elm.setInWGroupName(row.get("INWGROUPNAME").toString());
                level1Elm.setCreateOpId(row.get("CREATEOPID").toString());
                level1Elm.setCreateOpName(row.get("CREATEOPNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setLastModiTime(row.get("LASTMODITIME").toString());
                level1Elm.setLastModiOpId(row.get("LASTMODIOPID").toString());
                level1Elm.setLastModiOpName(row.get("LASTMODIOPNAME").toString());
                level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                level1Elm.setRemainType(row.get("REMAINTYPE").toString());
                level1Elm.setContainType(row.get("CONTAINTYPE").toString());
                level1Elm.setMinQty(row.get("MINQTY").toString());
                level1Elm.setOddValue(row.get("ODDVALUE").toString());
                level1Elm.setProductExceed(row.get("PRODUCTEXCEED").toString());
                level1Elm.setProcRate(row.get("PROCRATE").toString());
                level1Elm.setDispType(row.get("DISPTYPE").toString());
                level1Elm.setSemiwoType(row.get("SEMIWOTYPE").toString());
                level1Elm.setSemiwoDeptType(row.get("SEMIWODEPTTYPE").toString());
                level1Elm.setFixPreDays(row.get("FIXPREDAYS").toString());
                level1Elm.setSdlaborTime(row.get("SDLABORTIME").toString());
                level1Elm.setSdmachineTime(row.get("SDMACHINETIME").toString());
                level1Elm.setStandardHours(row.get("STANDARDHOURS").toString());
                level1Elm.setChangeOpId(row.get("CHANGEOPID").toString());
                level1Elm.setChangeOpName(row.get("CHANGEOPNAME").toString());
                level1Elm.setChangeTime(row.get("CHANGETIME").toString());

                level1Elm.setMaterialList(new ArrayList<>());
                level1Elm.setRangeList(new ArrayList<>());
                level1Elm.setCoByList(new ArrayList<>());

                if(getMaterialQData.size()>0){
                    List<Map<String, Object>> filterRows = getMaterialQData.stream().filter(x -> x.get("VERSIONNUM").toString().equals(level1Elm.getVersionNum())).collect(Collectors.toList());
                    for (Map<String, Object> materialRow : filterRows){
                        DCP_BomVersionDetailQueryRes.MaterialList materialList = res.new MaterialList();
                        materialList.setMaterialPluNo(materialRow.get("MATERIAL_PLUNO").toString());
                        materialList.setMaterialPluName(materialRow.get("MATERIALPLUNAME").toString());
                        materialList.setQty(materialRow.get("QTY").toString());
                        materialList.setMaterialUnit(materialRow.get("MATERIAL_UNIT").toString());
                        materialList.setMaterialUnitName(materialRow.get("MATERIALUNITNAME").toString());
                        materialList.setMaterialQty(materialRow.get("MATERIAL_QTY").toString());
                        materialList.setLossRate(materialRow.get("LOSS_RATE").toString());
                        materialList.setIsBuckle(materialRow.get("ISBUCKLE").toString());
                        materialList.setIsReplace(materialRow.get("ISREPLACE").toString());
                        materialList.setMaterialBDate(materialRow.get("MATERIAL_BDATE").toString());
                        materialList.setMaterialEDate(materialRow.get("MATERIAL_EDATE").toString());
                        materialList.setSortId(materialRow.get("SORTID").toString());
                        materialList.setIsPick(materialRow.get("ISPICK").toString());
                        materialList.setIsMix(materialRow.get("ISMIX").toString());
                        materialList.setIsBatch(materialRow.get("ISBATCH").toString());
                        materialList.setPWGroupNo(materialRow.get("PWGROUPNO").toString());
                        materialList.setPWGroupName(materialRow.get("PWGROUPNAME").toString());
                        materialList.setMixGroup(materialRow.get("MIXGROUP").toString());
                        materialList.setKWGroupNo(materialRow.get("KWGROUPNO").toString());
                        materialList.setKWGroupName(materialRow.get("KWGROUPNAME").toString());

                        level1Elm.getMaterialList().add(materialList);
                    }
                }

                if(getRangeQData.size()>0){
                    List<Map<String, Object>> filterRows = getRangeQData.stream().filter(x -> x.get("VERSIONNUM").toString().equals(level1Elm.getVersionNum())).collect(Collectors.toList());
                    for (Map<String, Object> rangeRow : filterRows){
                        DCP_BomVersionDetailQueryRes.RangeList rangeList = res.new RangeList();
                        rangeList.setShopId(rangeRow.get("SHOPID").toString());
                        rangeList.setShopName(rangeRow.get("SHOPNAME").toString());
                        rangeList.setOrganizationNo(rangeRow.get("ORGANIZATIONNO").toString());
                        rangeList.setOrganizationName(rangeRow.get("ORGANIZATIONNAME").toString());
                        level1Elm.getRangeList().add(rangeList);
                    }
                }

                if(coList.size()>0) {
                    List<Map<String, Object>> filterRows = coList.stream().filter(x -> x.get("VERSIONNUM").toString().equals(level1Elm.getVersionNum())).collect(Collectors.toList());

                    for (Map<String, Object> map : filterRows) {

                        DCP_BomVersionDetailQueryRes.CoByList coByList = res.new CoByList();
                        coByList.setProductType(map.get("PRODUCTTYPE").toString());
                        coByList.setPluNo(map.get("PLUNO").toString());
                        coByList.setPluName(map.get("PLUNAME").toString());
                        coByList.setUnit(map.get("UNIT").toString());
                        coByList.setUName(map.get("UNAME").toString());
                        coByList.setCostRate(map.get("COSTRATE").toString());
                        coByList.setVersionNum(map.get("VERSIONNUM").toString());
                        level1Elm.getCoByList().add(coByList);

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
    protected String getQuerySql(DCP_BomVersionDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String bomNo = req.getRequest().getBomNo();

        List<DCP_BomVersionDetailQueryReq.versionList> versionList = req.getRequest().getVersionList();

        StringBuffer sqlbuf=new StringBuffer();

        String sqlWhere=" and 1=1 ";
        if(CollUtil.isNotEmpty(versionList)){
            String collect = versionList.stream().map(x -> "'" + x.getVersionNum() + "'").distinct().collect(Collectors.joining(","));
            sqlWhere =" and a.versionnum in ("+collect+")";
        }

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.*,c.plu_name as pluname,d.uname as unitname,e.uname as baseunitname,f.category_name as categoryname,g.WGROUPNAME as inwgroupname,h.op_name as createopnames," +
                " h1.op_name as lastModiOpnames,h2.op_name as changeopname,i.DEPARTNAME as createdeptname,c0.spec,c0.category,c0.baseunit  "
                + " from dcp_bom_v a"+
                " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods c0 on c0.eid=a.eid and c0.pluno=a.pluno  " +
                " left join dcp_unit_lang d on d.eid=a.eid and d.unit=a.unit and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=c0.baseunit and e.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG f on f.eid=a.eid and f.category=c0.category and f.lang_type='"+req.getLangType()+"'  " +
                " left join MES_WAREHOUSE_GROUP g on g.eid=a.eid and a.inwgroupno=g.wgroupno " +
                " left join platform_staffs_lang h on h.eid=a.eid and a.CREATEOPID=h.opno and h.lang_type='"+req.getLangType()+"' "+
                " left join platform_staffs_lang h1 on h1.eid=a.eid and a.lastModiOpId=h1.opno and h1.lang_type='"+req.getLangType()+"' "+
                " left join platform_staffs_lang h2 on h2.eid=a.eid and a.changeOpId=h2.opno and h2.lang_type='"+req.getLangType()+"' " +
                " left join DCP_DEPARTMENT_LANG i on i.eid=a.eid and i.departno=a.createdeptid and i.lang_type='"+req.getLangType()+"' "
                + " where a.eid='"+eId+"' and a.bomno='"+bomNo+"' "+sqlWhere
                + " ) a "
                + " ");

        return sqlbuf.toString();
    }
}


