package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BomVersionQueryReq;
import com.dsc.spos.json.cust.res.DCP_BomVersionQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BomVersionQuery extends SPosBasicService<DCP_BomVersionQueryReq, DCP_BomVersionQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_BomVersionQueryReq req) throws Exception {
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
    protected TypeToken<DCP_BomVersionQueryReq> getRequestType() {
        return new TypeToken<DCP_BomVersionQueryReq>(){};
    }

    @Override
    protected DCP_BomVersionQueryRes getResponseType() {
        return new DCP_BomVersionQueryRes();
    }

    @Override
    protected DCP_BomVersionQueryRes processJson(DCP_BomVersionQueryReq req) throws Exception {
        DCP_BomVersionQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
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

            for (Map<String, Object> row : getQData){
                DCP_BomVersionQueryRes.Level1Elm level1Elm = res.new Level1Elm();
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
    protected String getQuerySql(DCP_BomVersionQueryReq req) throws Exception {
        String eId = req.geteId();
        String bomNo = req.getRequest().getBomNo();
        String versionNum = req.getRequest().getVersionNum();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        StringBuffer sqlbuf=new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with bomv as ("
                + " select distinct a.bomno,a.VERSIONNUM from dcp_bom_v a"
                + " where a.eid='"+eId+"' "
        );

        if(Check.NotNull(bomNo)){
            sqlbuf.append(" and a.bomno='"+bomNo+"' ");
        }
        if(Check.NotNull(versionNum)){
            sqlbuf.append(" and a.versionnum='"+versionNum+"' ");
        }
        if(Check.NotNull(beginDate)){
            sqlbuf.append(" and to_char(a.LASTMODITIME,'yyyyMMdd')>='"+beginDate+"' ");
        }
        if(Check.NotNull(endDate)){
            sqlbuf.append(" and to_char(a.LASTMODITIME,'yyyyMMdd')<='"+endDate+"' ");
        }



        //sqlbuf.append(" group by a.bomno,a.versionnum");
        sqlbuf.append(" )");

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.*,c.plu_name as pluname,d.uname as unitname,e.uname as baseunitname,f.category_name as categoryname,g.WGROUPNAME as inwgroupname,h.op_name as createopnames," +
                " h1.op_name as lastModiOpnames,h2.op_name as changeopname,i.DEPARTNAME as createdeptname,c0.spec,c0.category,c0.baseunit  "
                + " from dcp_bom_v a"
                + " inner join bomv b on a.bomno=b.bomno and a.versionnum=b.versionnum " +
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
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


