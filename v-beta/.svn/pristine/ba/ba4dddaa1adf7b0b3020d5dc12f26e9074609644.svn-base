package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_OrgGoodStockQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrgGoodStockQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DCP_OrgGoodsStockQuery extends SPosBasicService<DCP_OrgGoodStockQueryReq, DCP_OrgGoodStockQueryRes> {

    Logger logger = LogManager.getLogger(SPosAdvanceService.class);
    public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();

    @Override
    protected boolean isVerifyFail(DCP_OrgGoodStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_OrgGoodStockQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrgGoodStockQueryReq>() {
        };
    }

    @Override
    protected DCP_OrgGoodStockQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_OrgGoodStockQueryRes();
    }

    @Override
    protected DCP_OrgGoodStockQueryRes processJson(DCP_OrgGoodStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_OrgGoodStockQueryRes res = null;
        res = this.getResponse();
        String sql = null;

        sql = this.getQuerySql(req);

        String[] conditionValues1 = {}; //查詢條件

        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
        List<Map<String, Object>> getQLangDataDetail = null;
        List<Map<String, Object>> getQLangData = null;
        String eID = "";
        String billNo = "";
        String payType = "";
        res.setDatas(new ArrayList<DCP_OrgGoodStockQueryRes.DataDetail>());
        List<DCP_OrgGoodStockQueryReq.PluList> pluList = req.getRequest().getPluList();
        StringBuffer sJoinOrgNo = new StringBuffer("");
        StringBuffer sJoinWarehouseNo = new StringBuffer("");
        if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
            for (Map<String, Object> oneData : getQDataDetail) {
                billNo = oneData.get("ORGANIZATIONNO").toString();
                sJoinOrgNo.append(billNo + ",");
                sJoinWarehouseNo.append(oneData.get("WAREHOUSE").toString() + ",");
            }
        }
        MyCommon cm = new MyCommon();
        Map<String, String> mapOrgNo = new HashMap<String, String>();
        mapOrgNo.put("ORGNO", sJoinOrgNo.toString());
        mapOrgNo.put("WAREHOUSE", sJoinWarehouseNo.toString());

        String withasSql_orgno = "";
        withasSql_orgno = cm.getFormatSourceMultiColWith(mapOrgNo);
        mapOrgNo = null;

        List<Map<String, Object>> batchStockList = new ArrayList<>();
        if (withasSql_orgno.length() > 0) {
            String stockSql = "with p AS ( " + withasSql_orgno + ") " +
                    " select a.pluno,a.featureno,a.BATCHNO,a.prodDate,a.VALIDDATE as expdate,a.location,b.locationname,a.baseunit," +
                    " c.uname as baseunitname ,a.qty,a.lockqty from MES_BATCH_STOCK_DETAIL a " +
                    " inner join p on p.orgno=a.organizationno and a.WAREHOUSE=p.WAREHOUSE  " +
                    " left join DCP_LOCATION b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse and a.location=b.location " +
                    " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.baseunit and c.lang_type='" + req.getLangType() + "'";
            batchStockList = this.doQueryData(stockSql, null);
        }

        if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
            for (Map<String, Object> oneData : getQDataDetail) {
                DCP_OrgGoodStockQueryRes.DataDetail oneLv1 = res.new DataDetail();
                eID = oneData.get("EID").toString();
                billNo = oneData.get("ORGANIZATIONNO").toString();
                sJoinOrgNo.append(billNo + ",");
                String id = eID;
                String bill = billNo;
                String pType = payType;
                //OrgPluList

                String ulSql="select * from DCP_UNIT_LANG a where a.eid='"+req.geteId()+"' " +
                        " and a.lang_type='"+req.getLangType()+"'";
                List<Map<String, Object>> ulDatas = this.doQueryData(ulSql, null);

                oneLv1.setOrgPluList(new ArrayList<DCP_OrgGoodStockQueryRes.OrgPluList>());
                Map<String, Object> oneData2 = oneData;
                //for (Map<String, Object> oneData2 : getQLangData)
                {
                    DCP_OrgGoodStockQueryRes.OrgPluList oneLv2 = res.new OrgPluList();
                    oneLv2.setOrgNo(oneData2.get("ORGANIZATIONNO").toString());

                    sql = this.getOrgQuerySql(req, oneData2.get("ORGANIZATIONNO").toString(), oneData2.get("WAREHOUSE").toString());
                    getQLangDataDetail = this.doQueryData(sql, conditionValues1);
                    //plulist
                    getQLangData = getQLangDataDetail.stream()
                            .filter(LangData -> id.equals(LangData.get("EID").toString())
                                    && bill.equals(LangData.get("ORGANIZATIONNO").toString()))
                            .collect(Collectors.toList());

                    oneLv2.setPluList(new ArrayList<DCP_OrgGoodStockQueryRes.PluList>());
                    for (Map<String, Object> oneData3 : getQLangData) {
                        DCP_OrgGoodStockQueryRes.PluList oneLv3 = res.new PluList();
                        oneLv3.setPluNo(oneData3.get("PLUNO").toString());
                        oneLv3.setFeatureNo(oneData3.get("FEATURENO").toString());
                        oneLv3.setWUnit(oneData3.get("WUNIT").toString());
                        //oneLv3.setWQty(oneData3.get("WQTY").toString());
                        oneLv3.setBaseUnit(oneData3.get("BASEUNIT").toString());
                        oneLv3.setBaseQty(oneData3.get("BASEQTY").toString());
                        oneLv3.setLockQty(oneData3.get("LOCKQTY").toString());
                        oneLv3.setOnLineQty(oneData3.get("ONLINEQTY").toString());
                        oneLv3.setAvailableQty(oneData3.get("AVAILABLEQTY").toString());
                        oneLv3.setPUnit(oneData3.get("PUNIT").toString());
                        oneLv3.setPQty(oneData3.get("PUNITRQTY").toString());
                        oneLv3.setAvailablePqty(oneData3.get("AVAILABLEPQTY").toString());
                        BigDecimal unitRatio = new BigDecimal(oneData3.get("UNITRATIO").toString());
                        List<DCP_OrgGoodStockQueryReq.PluList> filterPlus = pluList.stream().filter(x -> x.getPluNo().equals(oneData3.get("PLUNO").toString()) && x.getFeatureNo().equals(oneData3.get("FEATURENO").toString())).collect(Collectors.toList());
                        List<String> pUnitList = filterPlus.stream().map(x -> x.getPUnit()).distinct().filter(y->Check.NotNull(y)).collect(Collectors.toList());
                        if(pUnitList.size()>0){
                            oneLv3.setPUnit(pUnitList.get(0));
                            Map<String, Object> mapPunit = PosPub.getBaseQty(dao, req.geteId(), oneLv3.getPluNo(), oneLv3.getPUnit(),"1");
                            if (mapPunit == null)
                            {
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+oneLv3.getPluNo()+",OUNIT="+oneLv3.getPUnit()+"无记录！");
                            }
                            int unitdlength=PosPub.getUnitUDLength(dao, req.geteId(), oneLv3.getBaseUnit());
                            unitRatio = new BigDecimal(mapPunit.get("unitRatio").toString());
                            String pQty=(new BigDecimal(oneLv3.getBaseQty())).divide(unitRatio,unitdlength).setScale(unitdlength, RoundingMode.HALF_UP).toString();
                            String availablePqty=(new BigDecimal(oneLv3.getAvailableQty())).divide(unitRatio,unitdlength).setScale(unitdlength, RoundingMode.HALF_UP).toString();

                            oneLv3.setPQty(pQty);
                            oneLv3.setAvailablePqty(availablePqty);

                        }

                        //availableQty_barcode_d = new BigDecimal(unitRatio).multiply(new BigDecimal(availableQty)).setScale(4, RoundingMode.HALF_UP).doubleValue();

                        String pUnitRatio = oneData3.get("PUNITRATIO").toString();
                        String pUdLength = oneData3.get("PUDLENGTH").toString();
//							if(Check.NotNull(pUnitRatio)){
//								BigDecimal pUnitRatioS = new BigDecimal(pUnitRatio);
//								BigDecimal divide = new BigDecimal(oneLv3.getAvailableQty()).divide(pUnitRatioS, Integer.parseInt(pUdLength), BigDecimal.ROUND_HALF_UP);
//								oneLv3.setAvailablePqty(divide.toString());
//							}


                        oneLv3.setBatchList(new ArrayList<>());

                        List<Map<String, Object>> batchList = batchStockList.stream().filter(x -> x.get("PLUNO").toString().equals(oneData3.get("PLUNO").toString())
                                && x.get("FEATURENO").toString().equals(oneData3.get("FEATURENO").toString())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(batchList)) {
                            for (Map<String, Object> batch : batchList) {
                                DCP_OrgGoodStockQueryRes.BatchList singleBatch = res.new BatchList();

                                singleBatch.setBatchNo(batch.get("BATCHNO").toString());
                                singleBatch.setProdDate(DateFormatUtils.getPlainDate(batch.get("PRODDATE").toString()));
                                singleBatch.setExpDate(DateFormatUtils.getPlainDate(batch.get("EXPDATE").toString()));
                                singleBatch.setLocation(batch.get("LOCATION").toString());
                                singleBatch.setLocationName(batch.get("LOCATIONNAME").toString());
                                singleBatch.setPUnit(oneLv3.getPUnit());

                                List<Map<String, Object>> filterUnits = ulDatas.stream().filter(x -> x.get("UNIT").toString().equals(singleBatch.getPUnit())).collect(Collectors.toList());
                                if (filterUnits.size() > 0)
                                {
                                    singleBatch.setPUnitName(filterUnits.get(0).get("UNAME").toString());
                                }

                                //singleBatch.setPUnitName(oneData3.get("PUNITNAME").toString());
                                singleBatch.setBaseUnit(batch.get("BASEUNIT").toString());
                                singleBatch.setBaseUnitName(batch.get("BASEUNITNAME").toString());
                                singleBatch.setUnitRatio(unitRatio.toString());
                                //BigDecimal unitRatio = new BigDecimal(oneData3.get("UNITRATIO").toString());
                                int udLength = Integer.parseInt(oneData3.get("UDLENGTH").toString());
                                BigDecimal stockQty = new BigDecimal(batch.get("QTY").toString());
                                BigDecimal lockQty = new BigDecimal(Check.Null(batch.get("LOCKQTY").toString()) ? "0" : batch.get("LOCKQTY").toString());
                                BigDecimal availableQty = stockQty.subtract(lockQty);
                                BigDecimal pQty = stockQty.divide(unitRatio, udLength, BigDecimal.ROUND_HALF_UP);
                                singleBatch.setStockQty(stockQty.toString());
                                singleBatch.setPQty(pQty.toString());
                                singleBatch.setAvailableQty(availableQty.toString());

                                oneLv3.getBatchList().add(singleBatch);
                            }

                        }

                        oneLv2.getPluList().add(oneLv3);
                        oneLv3 = null;
                    }


                    oneLv1.getOrgPluList().add(oneLv2);
                    oneLv2 = null;
                }
                res.getDatas().add(oneLv1);
                oneLv1 = null;
            }
        }

        //}
        //catch (Exception e) {
        // TODO Auto-generated catch block
        //	res.setDatas(new ArrayList<DCP_OrgGoodStockQueryRes.DataDetail>());
        //	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
        //}

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_OrgGoodStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        //int pageNumber = req.getPageNumber();
        //int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        //int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String type = null;

        String eId = req.geteId();
        List<DCP_OrgGoodStockQueryReq.OrgList> orgList = null;
        if (req.getRequest() != null) {
            orgList = req.getRequest().getOrgList();
            if (!SUtil.EmptyList(orgList)) {
                for (DCP_OrgGoodStockQueryReq.OrgList par : orgList) {
                    if (StringUtils.isEmpty(key)) {
                        key = SUtil.RetTrimStr(par.getOrgNo());
                    } else {
                        key = key + "," + SUtil.RetTrimStr(par.getOrgNo());
                    }

                    if (StringUtils.isEmpty(type)) {
                        type = SUtil.RetTrimStr(par.getWareHouse());
                    } else {
                        type = type + "," + SUtil.RetTrimStr(par.getWareHouse());
                    }

                }
            }
        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM (");
        if (type != null && type.length() > 0) {
            sqlbuf.append(" SELECT distinct a.EID, a.ORGANIZATIONNO,a.WAREHOUSE  ");
        } else {
            sqlbuf.append(" SELECT distinct a.EID, a.ORGANIZATIONNO,'' as WAREHOUSE  ");
        }

        sqlbuf.append(" FROM DCP_STOCK a"

                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  a.ORGANIZATIONNO  in(  " + key + ") ");
        }

        if (type != null && type.length() > 0) {
            sqlbuf.append(" and  a.WAREHOUSE  in(  " + type + ") ");
        }

        sqlbuf.append(" ) DBL   ");
        sqlbuf.append(" order by  ORGANIZATIONNO  , WAREHOUSE    ");


        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }


    /**
     * 新增要處理的資料(先加入的, 先處理)
     *
     * @param row
     */
    protected final void addProcessData(DataProcessBean row) {
        this.pData.add(row);
    }

    /**
     * 计算总数
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected String getCountSql(DCP_OrgGoodStockQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String key = null;
        String type = null;
        String eId = req.geteId();

        List<DCP_OrgGoodStockQueryReq.OrgList> orgList = null;
        if (req.getRequest() != null) {
            orgList = req.getRequest().getOrgList();
            if (!SUtil.EmptyList(orgList)) {
                for (DCP_OrgGoodStockQueryReq.OrgList par : orgList) {
                    if (StringUtils.isEmpty(key)) {
                        key = SUtil.RetTrimStr(par.getOrgNo());
                    } else {
                        key = key + "," + SUtil.RetTrimStr(par.getOrgNo());
                    }

                    if (StringUtils.isEmpty(type)) {
                        type = SUtil.RetTrimStr(par.getWareHouse());
                    } else {
                        type = type + "," + SUtil.RetTrimStr(par.getWareHouse());
                    }

                }
            }
        }

        StringBuffer sqlbuf = new StringBuffer("");
        String sql = "";

        sqlbuf.append("select num from( select count( distinct  a.ORGANIZATIONNO) AS num   "
                + " FROM DCP_STOCK a"

                + " WHERE a.EID = '" + eId + "' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and  a.ORGANIZATIONNO  in(  " + key + ") ");
        }

        if (type != null && type.length() > 0) {
            sqlbuf.append(" and  a.WAREHOUSE  in(  " + type + ") ");
        }
        sqlbuf.append(" )DBL  ");

        sql = sqlbuf.toString();
        return sql;
    }


    protected String getOrgQuerySql(DCP_OrgGoodStockQueryReq req, String orgNo, String wareNo) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        //int pageNumber = req.getPageNumber();
        //int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        //int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String status = null;
        String eId = req.geteId();
        String type = null;
        List<DCP_OrgGoodStockQueryReq.PluList> pluList = null;
        StringBuffer sJoinPluNo = new StringBuffer("");
        StringBuffer sJoinFeatureNo = new StringBuffer("");
        StringBuffer sJoinPunit = new StringBuffer("");
        if (req.getRequest() != null) {
            pluList = req.getRequest().getPluList();
            if (!SUtil.EmptyList(pluList)) {
                for (DCP_OrgGoodStockQueryReq.PluList par : pluList) {

                    sJoinPluNo.append(par.getPluNo() + ",");
                    sJoinFeatureNo.append(par.getFeatureNo() + ",");
                    sJoinPunit.append(par.getPUnit() + ",");

                }
            }
        }

        Map<String, String> mapPlu = new HashMap<String, String>();
        mapPlu.put("PLUNOR", sJoinPluNo.toString());
        mapPlu.put("FEATURENOR", sJoinFeatureNo.toString());
        mapPlu.put("PUNITR", sJoinPunit.toString());
        MyCommon cm = new MyCommon();
        String withasSql_mono = cm.getFormatSourceMultiColWith(mapPlu);


        StringBuffer sqlbuf = new StringBuffer("");

        if (!Check.Null(withasSql_mono)) {
            sqlbuf.append("with p as (" + withasSql_mono + ") ");
        }

        sqlbuf.append("SELECT a.*,c.WUNIT ,c.punit,U.UNITRATIO ,baseQty*U.UNITRATIO as pQty ,round(availableQty/ua1.UNITRATIO ,2) as availablePqty,u1.udlength,u2.uname as punitname,nvl(u3.unitratio,U.UNITRATIO) as punitratio,nvl(u4.udlength,u1.udlength) as pudlength "
                + " ,round(baseQty / ua1.UNITRATIO ,2) as punitrQTY , p.punitr  "
                + " FROM ("
                + " SELECT EID ,ORGANIZATIONNO  "
                + "  , PLUNO,FEATURENO,BASEUNIT,sum(QTY) as BASEQTY,sum(LOCKQTY) as LOCKQTY  "
                + "  ,sum(QTY-LOCKQTY-ONLINEQTY) as availableQty,sum(ONLINEQTY) as ONLINEQTY  " +
                " FROM DCP_STOCK a "
                + "");

        sqlbuf.append(" WHERE a.EID = '" + eId + "' ");
        if (!Check.Null(orgNo)) {
            sqlbuf.append(" AND a.ORGANIZATIONNO = '" + orgNo + "'");
        }
        if (!Check.Null(wareNo)) {
            sqlbuf.append(" AND a.WAREHOUSE = '" + wareNo + "'");
        }

        sqlbuf.append(" group by EID,ORGANIZATIONNO,PLUNO,FEATURENO,BASEUNIT ) a   ");  //这里先查出和主数据一样的数据 然后关联

        if (withasSql_mono.length() > 0) {
            sqlbuf.append(" inner join  p on p.plunoR=a.pluno and p.featurenoR=a.featureno ");
        }
        sqlbuf.append("  inner join dcp_goods c on a.eid=c.eid and a.PLUNO=c.PLUNO and c.status='100' ");
        sqlbuf.append(" left join dcp_goods_unit u on u.eid=c.eid and u.pluno = c.pluno and u.ounit=c.PUNIT ");
        if (!Check.Null(withasSql_mono)) {
            sqlbuf.append(" left join dcp_goods_unit u3 on u3.eid=c.eid and u3.pluno = c.pluno and u3.ounit=p.PUNITR ");
            sqlbuf.append(" left join dcp_unit u4 on u4.eid=c.eid and  u4.unit=p.PUNITR ");
        } else {
            sqlbuf.append(" left join dcp_goods_unit u3 on u3.eid=c.eid and u3.pluno = c.pluno and u3.ounit=c.punit ");
            sqlbuf.append(" left join dcp_unit u4 on u4.eid=c.eid and  u4.unit=c.PUNIT ");
        }
        sqlbuf.append(" LEFT JOIN dcp_goods_unit ua1 ON ua1.eid = c.eid AND ua1.pluno = c.pluno AND ua1.ounit = c.punit  ");

        sqlbuf.append(" left join dcp_unit u1 on u1.eid=c.eid and  u1.unit=c.PUNIT ");
        sqlbuf.append(" left join dcp_unit_lang u2 on u2.eid=c.eid and  u2.unit=c.PUNIT and u2.lang_type='" + langType + "' ");
        //sqlbuf.append("    where  ROWNUM > " + startRow + " AND ROWNUM  <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
    }


}
