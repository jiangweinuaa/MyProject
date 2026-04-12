package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_BatchStockQueryReq;
import com.dsc.spos.json.cust.res.DCP_BatchStockQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_BatchStockQuery extends SPosBasicService<DCP_BatchStockQueryReq, DCP_BatchStockQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_BatchStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_BatchStockQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BatchStockQueryReq>(){};
    }

    @Override
    protected DCP_BatchStockQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BatchStockQueryRes();
    }

    @Override
    protected DCP_BatchStockQueryRes processJson(DCP_BatchStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_BatchStockQueryRes res = null;
        res = this.getResponse();

        int totalRecords;		//总笔数
        int totalPages;

        String sql = "";
        sql = this.getQuerySql(req);
        List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
        res.setBatchList(new ArrayList<>());
        if (queryDatas != null && queryDatas.isEmpty() == false) {
            String num = queryDatas.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            List<DCP_BatchStockQueryReq.PluList> pluList = req.getRequest().getPluList();
            if(pluList==null){
                pluList=new ArrayList<>();
            }
            //查询单位
            String ulSql="select * from DCP_UNIT_LANG a where a.eid='"+req.geteId()+"' " +
                    " and a.lang_type='"+req.getLangType()+"'";
            List<Map<String, Object>> ulDatas = this.doQueryData(ulSql, null);

            for (Map<String, Object> singleData: queryDatas){
                DCP_BatchStockQueryRes.LevelItem levelItem = res.new LevelItem();
                levelItem.setOrgNo(singleData.get("ORGNO").toString());
                levelItem.setOrgName(singleData.get("ORGNAME").toString());
                levelItem.setWareHouse(singleData.get("WAREHOUSE").toString());
                levelItem.setWareHouseName(singleData.get("WAREHOUSENAME").toString());
                levelItem.setLocation(singleData.get("LOCATION").toString());
                levelItem.setLocationName(singleData.get("LOCATIONNAME").toString());
                levelItem.setPluNo(singleData.get("PLUNO").toString());
                levelItem.setPluName(singleData.get("PLUNAME").toString());
                levelItem.setFeatureNo(singleData.get("FEATURENO").toString());
                levelItem.setFeatureName(singleData.get("FEATURENAME").toString());
                levelItem.setBatchNo(singleData.get("BATCHNO").toString());
                levelItem.setProdDate(singleData.get("PRODDATE").toString());
                levelItem.setExpDate(singleData.get("EXPDATE").toString());

                //String wqty = singleData.get("WQTY").toString();
                String wunit = singleData.get("WUNIT").toString();
                String baseUnit = singleData.get("BASEUNIT").toString();
                String stockQty = singleData.get("STOCKQTY").toString();//库存存的基础单位数量
                String lockqty = Check.Null(singleData.get("LOCKQTY").toString())?"0":singleData.get("LOCKQTY").toString();
                String onlineqty = Check.Null(singleData.get("ONLINEQTY").toString())?"0":singleData.get("ONLINEQTY").toString();

                BigDecimal availableStockQtyDec = new BigDecimal(stockQty).subtract(new BigDecimal(lockqty)).subtract(new BigDecimal(onlineqty));

                List<DCP_BatchStockQueryReq.PluList> unitCollection = pluList.stream().filter(x -> x.getPluNo().equals(levelItem.getPluNo())
                        && (x.getFeatureNo().equals(levelItem.getFeatureNo()) || x.getFeatureNo().equals(" ") )
                ).collect(Collectors.toList());

                levelItem.setUnitStock(new ArrayList<>());
                levelItem.setPStockQty(stockQty);
                levelItem.setPAvailableStockQty(availableStockQtyDec.toString());
                if (unitCollection.size() > 0)
                {
                    List<String> pUnitList = unitCollection.stream().map(x -> x.getPUnit()).distinct().collect(Collectors.toList());
                    for (String pUnit: pUnitList)
                    {
                        Map<String, Object> mapPunit = PosPub.getBaseQty(dao, req.geteId(), levelItem.getPluNo(), pUnit,"1");
                        if (mapPunit == null)
                        {
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+levelItem.getPluNo()+",OUNIT="+pUnit+"无记录！");
                        }

                        int unitdlength=PosPub.getUnitUDLength(dao, req.geteId(), baseUnit);
                        BigDecimal punitRatio = new BigDecimal(mapPunit.get("unitRatio").toString());
                        String pQty=(new BigDecimal(stockQty)).divide(punitRatio,unitdlength).setScale(unitdlength, RoundingMode.HALF_UP).toString();
                        DCP_BatchStockQueryRes.UnitStock unitStock = res.new UnitStock();
                        unitStock.setPUnit(pUnit);
                        List<Map<String, Object>> filterUnits = ulDatas.stream().filter(x -> x.get("UNIT").toString().equals(pUnit)).collect(Collectors.toList());
                        if (filterUnits.size() > 0)
                        {
                            unitStock.setPUnitName(filterUnits.get(0).get("UNAME").toString());
                        }

                        unitStock.setPStockQty(pQty);
                        unitStock.setPAvailableStockQty(availableStockQtyDec.divide(punitRatio,unitdlength).setScale(unitdlength, RoundingMode.HALF_UP).toString());
                        levelItem.getUnitStock().add(unitStock);
                        if(pUnitList.size()==1){
                            levelItem.setPStockQty(unitStock.getPStockQty());
                            levelItem.setPAvailableStockQty(unitStock.getPAvailableStockQty());
                        }
                    }


                }

                levelItem.setWUnit(wunit);
                levelItem.setWUnitName(singleData.get("WUNITNAME").toString());
                levelItem.setStockQty(stockQty);
                levelItem.setAvailableStockQty(availableStockQtyDec.toString());


                res.getBatchList().add(levelItem);
            }
        }
        else
        {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_BatchStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer sqlbuf = new StringBuffer();
        String sql="";
        String withasSql_pfno="";
        List<DCP_BatchStockQueryReq.PluList> pluList = req.getRequest().getPluList();
        MyCommon cm=new MyCommon();
        StringBuffer sJoinPluNo=new StringBuffer("");
        StringBuffer sJoinFeatureNo=new StringBuffer("");
        if(CollUtil.isNotEmpty(pluList)) {
            for (DCP_BatchStockQueryReq.PluList plu : pluList) {

                if(Check.Null(plu.getFeatureNo())){
                    plu.setFeatureNo(" ");
                }

                sJoinPluNo.append(plu.getPluNo() + ",");
                sJoinFeatureNo.append(plu.getFeatureNo() + ",");
            }

            Map<String, String> mappfNo = new HashMap<String, String>();
            mappfNo.put("PLUNO", sJoinPluNo.toString());
            mappfNo.put("FEATURENO", sJoinFeatureNo.toString());


            withasSql_pfno = cm.getFormatSourceMultiColWith(mappfNo);
            mappfNo = null;
        }

        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,a.* from (");

        sqlbuf.append("select a.ORGANIZATIONNO as orgNo,e.org_name as orgname,a.warehouse,c.warehouse_name as warehousename," +
                " a.location,d.LOCATIONNAME,a.batchno,nvl(to_char(a.PRODDATE,'yyyy-MM-dd'),'') as proddate , nvl(to_char(a.VALIDDATE,'yyyy-MM-dd'),'') as EXPDATE ,g1.wunit,a.qty as stockqty,a.baseunit," +
                " 0 as ONLINEQTY,a.LOCKQTY,a.pluno,g.plu_name as pluname,i.featureno,i.featurename,h.uname as wunitname   " +
                " from MES_BATCH_STOCK_DETAIL a " +
                //" left join MES_BATCH_STOCK_DETAIL b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.pluno=b.pluno " +
                " left join dcp_warehouse_lang c on a.eid=c.eid and a.ORGANIZATIONNO=c.ORGANIZATIONNO and a.warehouse=c.warehouse and c.lang_type='"+req.getLangType()+"' " +
                " left join DCP_LOCATION d on d.eid=a.eid and d.ORGANIZATIONNO=a.ORGANIZATIONNO and a.location=d.location " +
                " left join DCP_ORG_LANG e on e.eid=a.eid and a.organizationno=e.organizationno and e.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse f on f.eid=a.eid and f.organizationno=a.organizationno and f.warehouse=a.warehouse " +
                " left join dcp_goods_lang g on g.eid=a.eid and g.pluno=a.pluno and g.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods g1 on g1.eid=a.eid and g1.pluno=a.pluno  " +
                " left join dcp_unit_lang h on h.eid=a.eid and g1.wunit=h.unit and h.lang_type='"+req.getLangType()+"' " +
                " left join DCP_GOODS_FEATURE_LANG i on i.eid=a.eid and i.pluno=a.pluno and i.featureno=a.featureno and i.lang_type='"+req.getLangType()+"' "
               );

        if(!Check.Null(withasSql_pfno)){
            sqlbuf.append( " inner join ("+withasSql_pfno+" ) p on p.pluno=a.pluno and a.featureno=p.featureno "
            );
        }

        sqlbuf.append( " where a.organizationno='"+req.getRequest().getOrgNo()+"' ");

        if(!Check.Null(req.getRequest().getWareHouse())){
            sqlbuf.append(" and a.warehouse='"+req.getRequest().getWareHouse()+"' ");
        }
        if(!Check.Null(req.getRequest().getLocation())&&!" ".equals(req.getRequest().getLocation())){
            sqlbuf.append(" and a.location='"+req.getRequest().getLocation()+"' ");
        }

        String batchNoStr="";
        List<String> batchList = req.getRequest().getBatchList();
        if(CollUtil.isNotEmpty(batchList)){
            for(String batch:batchList){
                if(Check.NotNull(batch)) {
                    batchNoStr += "'" + batch + "',";
                }
            }
            if(batchNoStr.length()>0) {
                batchNoStr = batchNoStr.substring(0, batchNoStr.length() - 1);
                sqlbuf.append(" and a.batchno in (" + batchNoStr + ") ");
            }
        }

        sqlbuf.append(" "
                + " ) a "
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");

        sql = sqlbuf.toString();
        return sql;
    }

}
