package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.dsc.spos.json.cust.req.DCP_WOGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_WOGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_WOGoodsQuery extends SPosBasicService<DCP_WOGoodsQueryReq, DCP_WOGoodsQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_WOGoodsQueryReq req) throws Exception {
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
    protected TypeToken<DCP_WOGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_WOGoodsQueryReq>(){};
    }

    @Override
    protected DCP_WOGoodsQueryRes getResponseType() {
        return new DCP_WOGoodsQueryRes();
    }

    @Override
    protected DCP_WOGoodsQueryRes processJson(DCP_WOGoodsQueryReq req) throws Exception {
        DCP_WOGoodsQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;

        String now = DateUtil.format(new Date(),"yyyyMMdd");
        if(Check.Null(req.getRequest().getAccountDate())){
            req.getRequest().setAccountDate(now);
        }
        res.setDatas(new ArrayList<>());
        String beginDates=req.getRequest().getAccountDate().substring(0,6)+"01";
        String endDates=req.getRequest().getAccountDate().substring(0,6)+"31";


        //根据入参stockTakeNo查询DCP_STOCKTAKE_DETAIL获取BASEQTY-REF_BASEQTY<>0的盘点明细;

        String stockTakeSql="select * " +
                " from DCP_STOCKTAKE_DETAIL a where a.eid='"+req.geteId()+"' and a.stocktakeno='"+req.getRequest().getStockTakeNo()+"' " +
                " and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.BASEQTY-a.REF_BASEQTY<>0 ";
        List<Map<String, Object>> getStockTakeData=this.doQueryData(stockTakeSql, null);

        List<String> stockPluNos = getStockTakeData.stream().map(x -> x.get("PLUNO").toString()).collect(Collectors.toList());

        //oofno  ooitem 需要合计
        String pstockinSql="select a.ootype,a.oofno,a.ooitem,a.pluno,case when a.ootype='0' then c.bomno  when a.ootype='1' then d.bomno else '' end as bomno," +
                " case when a.ootype='0' then c.versionnum  when a.ootype='1' then d.versionnum else '' end as versionnum," +
                " case when a.ootype='0' then c.pdate  when a.ootype='1' then d.bdate else '' end as pdate," +
                " case when a.ootype='0' then c.pqty  when a.ootype='1' then d.pqty else '' end as pqty," +
                " e.plu_name as pluname,a.pqty-nvl(a.SCRAP_QTY,0) as prodqty,a.punit,f.uname as punitname  " +
                " from DCP_PSTOCKIN_DETAIL a " +
                " inner join dcp_pstockin b on a.eid=b.eid and a.organizationno=b.organizationno and a.pstockinno=b.pstockinno " +
                " left join DCP_PROCESSTASK_DETAIL c on c.eid=a.eid and a.oofno=c.PROCESSTASKNO and a.ooitem=c.item " +
                " left join MES_BATCHTASK d on d.eid=a.eid and a.organizationno=d.organizationno and d.batchtaskno=a.oofno and " +
                " left join dcp_goods_lang e on e.eid=a.eid and e.pluno=a.pluno and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.punit and f.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"' " +
                " and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.pqty>0 " +
                " and b.account_date>='"+beginDates+"' and b.account_date<='"+endDates+"'";
        if(Check.NotNull(req.getRequest().getDepartId())){
            pstockinSql+=" and b.departid='"+req.getRequest().getDepartId()+"' ";
        }
        List<Map<String, Object>> pstockInList = this.doQueryData(pstockinSql, null);


        if(CollUtil.isNotEmpty(pstockInList)){
            List<Map<String, Object>> filterRows1 = pstockInList.stream().filter(x -> Check.NotNull(x.get("BOMNO").toString())).collect(Collectors.toList());
            if(Check.NotNull(req.getRequest().getBeginDate())){
                filterRows1=filterRows1.stream().filter(x->Integer.valueOf(x.get("PDATE").toString())>=Integer.valueOf(req.getRequest().getBeginDate())).collect(Collectors.toList());
            }
            if(Check.NotNull(req.getRequest().getEndDate())){
                filterRows1=filterRows1.stream().filter(x->Integer.valueOf(x.get("PDATE").toString())<=Integer.valueOf(req.getRequest().getEndDate())).collect(Collectors.toList());
            }


            if(CollUtil.isNotEmpty(filterRows1)){
                String bomNoStr = filterRows1.stream().map(x -> x.get("BOMNO").toString()).collect(Collectors.joining(","));
                String versionNumStr = filterRows1.stream().map(x -> x.get("VERSIONNUM").toString()).collect(Collectors.joining(","));
                bomNoStr+=",";
                versionNumStr+=",";

                Map<String, String> mapBomNo=new HashMap<String, String>();
                mapBomNo.put("BOMNO", bomNoStr);
                mapBomNo.put("VERSIONNUM", versionNumStr);
                MyCommon cm=new MyCommon();
                String withasSql_BOM="";
                withasSql_BOM=cm.getFormatSourceMultiColWith(mapBomNo);
                mapBomNo=null;

                String sql_bom=" with p as ("+withasSql_BOM+")" +
                        " select a.* from dcp_bom_material a " +
                        " inner join p on p.bomno=a.bomno and p.versionnum=a.versionnum " +
                        " where a.eid='"+req.geteId()+"' ";
                List<Map<String, Object>> bomList = this.doQueryData(sql_bom, null);

                String sql_bom_v=" with p as ("+withasSql_BOM+")" +
                        " select a.* from dcp_bom_material_v a " +
                        " inner join p on p.bomno=a.bomno and p.versionnum=a.versionnum " +
                        " where a.eid='"+req.geteId()+"' ";
                List<Map<String, Object>> bomvList = this.doQueryData(sql_bom_v, null);

                String sql_bom_subprocess=" with p as ("+withasSql_BOM+")" +
                        " select a.* from MES_BOM_SUBPROCESS_M a " +
                        " inner join p on p.bomno=a.bomno and p.versionnum=a.versionnum " +
                        " where a.eid='"+req.geteId()+"' ";
                List<Map<String, Object>> bomSubList = this.doQueryData(sql_bom_subprocess, null);


                String sql_bom_subprocess_v=" with p as ("+withasSql_BOM+")" +
                        " select a.* from MES_BOM_SUBPROCESS_M_V a " +
                        " inner join p on p.bomno=a.bomno and p.versionnum=a.versionnum " +
                        " where a.eid='"+req.geteId()+"' ";
                List<Map<String, Object>> bomSubvList = this.doQueryData(sql_bom_subprocess_v, null);

                //filterRows1 过滤原料
                List<Map<String, Object>> collect = filterRows1.stream().filter(x -> {
                    String singleBom = x.get("BOMNO").toString();
                    String singleVersionNum = x.get("VERSIONNUM").toString();
                    String ooType = x.get("OOTYPE").toString();
                    if ("0".equals(ooType)) {
                        //bom_material

                        List<Map<String, Object>> collect1 = bomList.stream().filter(y -> y.get("BOMNO").toString().equals(singleBom) && y.get("VERSIONNUM").toString().equals(singleVersionNum)).collect(Collectors.toList());
                        List<Map<String, Object>> collect2 = bomvList.stream().filter(y -> y.get("BOMNO").toString().equals(singleBom) && y.get("VERSIONNUM").toString().equals(singleVersionNum)).collect(Collectors.toList());

                        if (collect1.size() > 0) {
                            List<Map<String, Object>> material_pluno1 = collect1.stream().filter(y -> stockPluNos.contains(y.get("MATERIAL_PLUNO").toString())).collect(Collectors.toList());
                            if (material_pluno1.size() <= 0) {
                                return false;
                            }
                        }
                        else {
                            List<Map<String, Object>> material_pluno1 = collect2.stream().filter(y -> stockPluNos.contains(y.get("MATERIAL_PLUNO").toString())).collect(Collectors.toList());
                            if (material_pluno1.size() <= 0) {
                                return false;
                            }
                        }


                    } else {
                        //MES_BOM_SUBPROCESS_MATERIAL
                        List<Map<String, Object>> collect1 = bomSubList.stream().filter(y -> y.get("BOMNO").toString().equals(singleBom) && y.get("VERSIONNUM").toString().equals(singleVersionNum)).collect(Collectors.toList());
                        List<Map<String, Object>> collect2 = bomSubvList.stream().filter(y -> y.get("BOMNO").toString().equals(singleBom) && y.get("VERSIONNUM").toString().equals(singleVersionNum)).collect(Collectors.toList());
                        if (collect1.size() > 0) {
                            List<Map<String, Object>> material_pluno1 = collect1.stream().filter(y -> stockPluNos.contains(y.get("MATERIAL_PLUNO").toString())).collect(Collectors.toList());
                            if (material_pluno1.size() <= 0) {
                                return false;
                            }
                        }
                        else {
                            List<Map<String, Object>> material_pluno1 = collect2.stream().filter(y -> stockPluNos.contains(y.get("MATERIAL_PLUNO").toString())).collect(Collectors.toList());
                            if (material_pluno1.size() <= 0) {
                                return false;
                            }
                        }
                    }

                    return true;
                }).collect(Collectors.toList());

                //collect ootype,oofno,ooitem 分组
                List<Map> resultMap = collect.stream().map(y -> {
                    Map map = new HashMap();
                    map.put("oofno", y.get("OOFNO").toString());
                    map.put("ootype", y.get("OOTYPE").toString());
                    map.put("ooitem", y.get("OOITEM").toString());
                    return map;
                }).distinct().collect(Collectors.toList());

                if(collect.size()>0){
                    String oofno = collect.stream().map(x -> x.get("oofno").toString()).collect(Collectors.joining(","));
                    String ootype = collect.stream().map(x -> x.get("ootype").toString()).collect(Collectors.joining(","));
                    String ooitem = collect.stream().map(x -> x.get("ooitem").toString()).collect(Collectors.joining(","));

                    oofno+=",";
                    ootype+=",";
                    ooitem+=",";

                    Map<String, String> mapStockInNo=new HashMap<String, String>();
                    mapStockInNo.put("OOTYPE", oofno);
                    mapStockInNo.put("OOFNO", ootype);
                    mapStockInNo.put("OOITEM", ooitem);

                    String withasSql_StockIn="";
                    withasSql_StockIn=cm.getFormatSourceMultiColWith(mapStockInNo);
                    int pageSize=req.getPageSize();
                    int startRow=(req.getPageNumber()-1) * pageSize;

                    String pstockinNewSql="with p as ("+withasSql_StockIn+")" +
                            " select * from (" +
                            " select count(*) over () num,row_number() over (order by a.ootype,a.oofno,a.ooitem desc) as rn, a.* from ( " +
                            " select a.ootype,a.oofno,a.ooitem,a.pluno,case when a.ootype='0' then c.bomno  when a.ootype='1' then d.bomno else '' end as bomno," +
                            " case when a.ootype='0' then c.versionnum  when a.ootype='1' then d.versionnum else '' end as versionnum," +
                            " case when a.ootype='0' then c.pdate  when a.ootype='1' then d.bdate else '' end as pdate," +
                            " case when a.ootype='0' then c.pqty  when a.ootype='1' then d.pqty else '' end as pqty," +
                            " e.plu_name as pluname,sum(a.pqty-nvl(a.SCRAP_QTY,0)) OVER (PARTITION BY a.oofno, a.ooitem, a.ootype)  as prodqty,a.punit,f.uname as punitname ," +
                            " ROW_NUMBER() OVER (PARTITION BY a.OOTYPE, a.OOFNO, a.OOITEM ORDER BY a.OOFNO) AS rn " +
                            " from DCP_PSTOCKIN_DETAIL a " +
                            " inner join p on p.ootype=a.ootype and p.oofno=a.oofno and p.ooitem=a.ooitem " +
                            " inner join dcp_pstockin b on a.eid=b.eid and a.organizationno=b.organizationno and a.pstockinno=b.pstockinno " +
                            " left join DCP_PROCESSTASK_DETAIL c on c.eid=a.eid and a.oofno=c.PROCESSTASKNO and a.ooitem=c.item " +
                            " left join MES_BATCHTASK d on d.eid=a.eid and a.organizationno=d.organizationno and d.batchtaskno=a.oofno and " +
                            " left join dcp_goods_lang e on e.eid=a.eid and e.pluno=a.pluno and e.lang_type='"+req.getLangType()+"' " +
                            " left join dcp_unit_lang f on f.eid=a.eid and f.unit=a.punit and f.lang_type='"+req.getLangType()+"' " +
                            " where a.eid='"+req.geteId()+"' and rn=1 ) a  ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn    "
                            ;
                    List<Map<String, Object>> getQData=this.doQueryData(pstockinNewSql, null);
                    if (getQData != null && !getQData.isEmpty()) {
                        //算總頁數
                        String num = getQData.get(0).get("NUM").toString();
                        totalRecords=Integer.parseInt(num);
                        totalPages = totalRecords / req.getPageSize();
                        totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                        for (Map<String, Object> row : getQData){
                            DCP_WOGoodsQueryRes.DatasLevel datasLevel = res.new DatasLevel();
                            datasLevel.setOOType(row.get("OOTYPE").toString());
                            datasLevel.setOOfNo(row.get("OOFNO").toString());
                            datasLevel.setOOItem(row.get("OOITEM").toString());
                            datasLevel.setPluNo(row.get("PLUNO").toString());
                            datasLevel.setPluName(row.get("PLUNAME").toString());
                            datasLevel.setPDate(row.get("PDATE").toString());
                            datasLevel.setPQty(row.get("PQTY").toString());
                            datasLevel.setPUnit(row.get("PUNIT").toString());
                            datasLevel.setPUName(row.get("PUNITNAME").toString());
                            datasLevel.setProdQty(row.get("PRODQTY").toString());
                            datasLevel.setBomNo(row.get("BOMNO").toString());
                            datasLevel.setVersionNum(row.get("VERSIONNUM").toString());
                            res.getDatas().add(datasLevel);

                        }

                    }

                }





            }

        }

        //
        //2.
        //查询记账日期当月完工入库单DCP_PSTOCKIN_DETAIL，按上级来源汇总入库数量，去除数量<=0的部分，再关联任务单;
        //
        //3.
        //OOTYPE为0关联DCP_PROCESSTASK_DETAIL，根据配方编号和版本号关联获取配方原料DCP_BOM_MATERIAL/DCP_BOM_MATERIAL_V，将原料清单和盘点结果做对比，仅保留有原料交集的任务单;
        //
        //4.
        //OOTYPE为1关联MES_BATCHTASK，根据配方编号和版本号关联获取配方原料MES_BOM_SUBPROCESS_MATERIAL/MES_BOM_SUBPROCESS_M_V，将原料清单和盘点结果做对比，仅保留有原料交集的任务单

        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());



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
    protected String getQuerySql(DCP_WOGoodsQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();

        return sqlbuf.toString();
    }
}


