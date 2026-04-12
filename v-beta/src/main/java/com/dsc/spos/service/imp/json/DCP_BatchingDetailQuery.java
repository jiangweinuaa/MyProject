package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_BatchingDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_BatchingDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.*;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BatchingDetailQuery extends SPosBasicService<DCP_BatchingDetailQueryReq, DCP_BatchingDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BatchingDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BatchingDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_BatchingDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_BatchingDetailQueryRes getResponseType() {
        return new DCP_BatchingDetailQueryRes();
    }

    @Override
    protected DCP_BatchingDetailQueryRes processJson(DCP_BatchingDetailQueryReq req) throws Exception {
        DCP_BatchingDetailQueryRes res = getResponseType();
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        List<Map<String, Object>> getDetailData = this.doQueryData(getDetailQuerySql(req), conditionValues1);
        String isBatchNo= PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");

        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            for (Map<String, Object> data : getData) {

                DCP_BatchingDetailQueryRes.Datas oneData = res.new Datas();

                oneData.setBDate(StringUtils.toString(data.get("BDATE"), ""));
                oneData.setBatchNo(StringUtils.toString(data.get("BATCHNO"), ""));
                oneData.setDocType(StringUtils.toString(data.get("DOC_TYPE"), ""));

                oneData.setTotCQty(StringUtils.toString(data.get("TOTCQTY"), ""));
                oneData.setTotPQty(StringUtils.toString(data.get("TOTPQTY"), ""));

                oneData.setCreateBy(StringUtils.toString(data.get("CREATEOPID"), ""));
                oneData.setCreateByName(StringUtils.toString(data.get("CREATEOPNAME"), ""));
                oneData.setCreateTime(StringUtils.toString(data.get("CREATETIME"), ""));

                oneData.setModifyBy(StringUtils.toString(data.get("LASTMODIOPID"), ""));
                oneData.setModifyByName(StringUtils.toString(data.get("LASTMODIOPNAME"), ""));
                oneData.setModifyTime(StringUtils.toString(data.get("LASTMODITIME"), ""));

                oneData.setConfirmBy(StringUtils.toString(data.get("ACCOUNTOPID"), ""));
                oneData.setConfirmByName(StringUtils.toString(data.get("ACCOUNTOPNAME"), ""));
                oneData.setConfirmTime(StringUtils.toString(data.get("ACCOUNTTIME"), ""));

                oneData.setProcessStatus(StringUtils.toString(data.get("PROCESS_STATUS"), ""));
                oneData.setProcessErpNo(StringUtils.toString(data.get("PROCESS_ERP_NO"), ""));
                oneData.setProcessErpOrg(StringUtils.toString(data.get("PROCESS_ERP_ORG"), ""));

                oneData.setAccountDate(data.get("ACCOUNTDATE").toString());
                oneData.setEmployeeId(data.get("EMPLOYEEID").toString());
                oneData.setEmployeeName(data.get("EMPLOYEENAME").toString());
                oneData.setDepartId(data.get("DEPARTID").toString());
                oneData.setDepartName(data.get("DEPARTNAME").toString());
                oneData.setStatus(data.get("STATUS").toString());
                oneData.setOOType(data.get("OOTYPE").toString());

                res.getDatas().add(oneData);
                oneData.setDatas(new ArrayList<>());

                List<String> mPluNos = getDetailData.stream().map(x -> x.get("MPLUNO").toString()).distinct().collect(Collectors.toList());
                String withPlu = "";
                MyCommon mc = new MyCommon();
                if (mPluNos !=null && mPluNos.size()>0 ) {

                    Map<String,String> map = new HashMap<>();
                    String sJoinPlu = "";
                    for(String s :mPluNos) {
                        sJoinPlu += s +",";
                    }
                    map.put("PLU", sJoinPlu);
                    withPlu = mc.getFormatSourceMultiColWith(map);
                }
                String sql = " select a.pluno,a.WAREHOUSE,sum(a.qty) as baseqty,sum(a.qty-a.lockqty-a.onlineqty) as availableqty from dcp_stock a"
                        + " inner join (" + withPlu + ") b on a.pluno=b.plu "
                        + " where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "'  ";


                sql += " group by a.pluno,a.WAREHOUSE";
                List<Map<String, Object>> getStock = this.doQueryData(sql, null);

                if (null != getDetailData && !getDetailData.isEmpty()) {

                    Map<String, Boolean> detailCondition = Maps.newHashMap();
                    detailCondition.put("EID", true);
                    detailCondition.put("ORGANIZATIONNO", true);
                    detailCondition.put("BATCHNO", true);
                    detailCondition.put("ITEM", true);

                    List<Map<String, Object>> detailData = MapDistinct.getMap(getDetailData, detailCondition);
                    for (Map<String, Object> dataDetail : detailData) {

                        DCP_BatchingDetailQueryRes.Detail oneDetail = res.new Detail();

                        oneDetail.setItem(StringUtils.toString(dataDetail.get("ITEM"), ""));
                        oneDetail.setPluNo(StringUtils.toString(dataDetail.get("PLUNO"), ""));
                        oneDetail.setPluName(StringUtils.toString(dataDetail.get("PLUNAME"), ""));
                        oneDetail.setPUnit(StringUtils.toString(dataDetail.get("PUNIT"), ""));
                        oneDetail.setPUName(StringUtils.toString(dataDetail.get("PUNITNAME"), ""));
                        oneDetail.setPQty(StringUtils.toString(dataDetail.get("PQTY"), ""));
                        oneDetail.setBatchTaskNo(StringUtils.toString(dataDetail.get("BATCHTASKNO"), ""));
                        oneDetail.setBenCopies(StringUtils.toString(dataDetail.get("BENCOPIES"), ""));
                        oneDetail.setPItem(StringUtils.toString(dataDetail.get("PITEM"), ""));
                        oneDetail.setProcessNo(StringUtils.toString(dataDetail.get("PROCESSNO"), ""));
                        oneDetail.setProcessName(StringUtils.toString(dataDetail.get("PROCESSNAME"), ""));
                        oneDetail.setSItem(StringUtils.toString(dataDetail.get("SITEM"), ""));
                        oneDetail.setIsReturn(StringUtils.toString(dataDetail.get("ISRETURN"), ""));
                        oneDetail.setOfNo(StringUtils.toString(dataDetail.get("OFNO"), ""));
                        oneDetail.setOItem(StringUtils.toString(dataDetail.get("OITEM"), ""));

                        oneDetail.setOOType(dataDetail.get("OOTYPE").toString());
                        oneDetail.setOOfNo(dataDetail.get("OOFNO").toString());
                        oneDetail.setOOItem(dataDetail.get("OOITEM").toString());
                        oneDetail.setMPluNo(dataDetail.get("MPLUNO").toString());
                        oneDetail.setMPUName(dataDetail.get("MPLUNAME").toString());
                        oneDetail.setBatchQty(dataDetail.get("BATCHQTY").toString());
                        oneDetail.setMPUnit(dataDetail.get("MPUNIT").toString());
                        oneDetail.setMPUnitName(dataDetail.get("MPUNITNAME").toString());
                        oneDetail.setZItem(dataDetail.get("ZITEM").toString());
                        oneDetail.setIsReplace(dataDetail.get("ISREPLACE").toString());
                        oneDetail.setRItem(dataDetail.get("RITEM").toString());
                        oneDetail.setReplaceRatio(dataDetail.get("REPLACERATIO").toString());
                        oneDetail.setIsBuckle(dataDetail.get("ISBUCKLE").toString());
                        //oneDetail.setMaterialIsBatch(dataDetail.get("MATERIALISBATCH").toString());
                        oneDetail.setIsLocation(dataDetail.get("ISLOCATION").toString());

                        oneDetail.setFromWarehouse(dataDetail.get("FROMWAREHOUSE").toString());
                        oneDetail.setToWarehouse(dataDetail.get("TOWAREHOUSE").toString());
                        oneDetail.setToWarehouseName(dataDetail.get("TOWAREHOUSENAME").toString());
                        oneDetail.setFromWarehouseName(dataDetail.get("FROMWAREHOUSENAME").toString());


                        if("Y".equals(isBatchNo)&&"Y".equals(dataDetail.get("ISBATCH").toString())){
                            oneDetail.setMaterialIsBatch("Y");
                        }else{
                            oneDetail.setMaterialIsBatch("N");
                        }

                        List<Map<String, Object>> stockFilterRows = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(oneDetail.getMPluNo()) && x.get("WAREHOUSE").toString().equals(dataDetail.get("TOWAREHOUSE").toString())).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(stockFilterRows)){
                            BigDecimal stockQty = new BigDecimal(stockFilterRows.get(0).get("BASEQTY").toString());
                            BigDecimal mUnitRatio = new BigDecimal(dataDetail.get("MUNITRATIO").toString());
                            int mUdLength = Integer.valueOf(dataDetail.get("MUDLENGTH").toString());
                            BigDecimal divide = stockQty.divide(mUnitRatio, mUdLength, BigDecimal.ROUND_HALF_UP);
                            oneDetail.setStockQty(divide.toString());

                        }


                        //Map<String, Object> condition = Maps.newHashMap();
                        //condition.put("BATCHNO", dataDetail.get("BATCHNO"));
                        //condition.put("MOOITEM", dataDetail.get("ITEM"));

                        List<Map<String, Object>> moData = getDetailData.stream().filter(x -> x.get("BATCHNO").toString().equals(dataDetail.get("BATCHNO").toString())
                                && x.get("MOOITEM").toString().equals(dataDetail.get("ITEM").toString())).collect(Collectors.toList());


                        //List<Map<String, Object>> moData = MapDistinct.getWhereMap(getDetailData, condition, true);
                        oneDetail.setBatchList(new ArrayList<>());
                        if (CollUtil.isNotEmpty(moData)) {
                            for (Map<String, Object> dataDetailMo : moData) {
                                DCP_BatchingDetailQueryRes.BatchList batch = res.new BatchList();
                                batch.setBatch(StringUtils.toString(dataDetailMo.get("BATCH"), ""));
                                batch.setItem(StringUtils.toString(dataDetailMo.get("MOITEM"), ""));
                                batch.setSharePQty(StringUtils.toString(dataDetailMo.get("SHAREPQTY"), ""));
                                batch.setFromWarehouse(dataDetailMo.get("FROMWAREHOUSEBATCH").toString());
                                batch.setFromWarehouseName(StringUtils.toString(dataDetailMo.get("FROMWAREHOUSENAMEBATCH"), ""));
                                batch.setToWarehouse(StringUtils.toString(dataDetailMo.get("TOWAREHOUSEBATCH"), ""));
                                batch.setToWarehouseName(StringUtils.toString(dataDetailMo.get("TOWAREHOUSENAMEBATCH"), ""));
                                batch.setBaseQty(StringUtils.toString(dataDetailMo.get("BASEQTY"), ""));
                                batch.setBaseUnit(StringUtils.toString(dataDetailMo.get("BASEUNIT"), ""));
                                batch.setBaseUName(StringUtils.toString(dataDetailMo.get("BASEUNITNAME"), ""));

                                oneDetail.getBatchList().add(batch);
                            }

                        }

                        oneData.getDatas().add(oneDetail);
                    }

                }

            }
        }
        res.setSuccess(true);
        return res;
    }

    protected String getDetailQuerySql(DCP_BatchingDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT a.*,b.ITEM as MOITEM,b.OITEM as MOOITEM,b.SHAREPQTY ")
                .append(" ,b.BATCH,b.BASEQTY,b.BASEUNIT,b.FROMWAREHOUSE as FROMWAREHOUSEBATCH,b.TOWAREHOUSE TOWAREHOUSEBATCH,i.plu_name as pluname,c.plu_name as mpluname,f.islocation,d.uname as mpunitname,e.isbatch,g.unitratio as munitratio,h.udlength as mudlength," +
                        " j.warehouse_name as fromwarehousename,k.warehouse_name as towarehousename,l.warehouse_name as fromwarehousenamebatch,m.warehouse_name as towarehousenamebatch  ")
                .append(" FROM MES_BATCHING_DETAIL a ")
                .append(" LEFT JOIN MES_BATCHING_DETAIL_MO b ON a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.BATCHNO=b.BATCHNO and a.ITEM=b.OITEM  ")
                .append(" left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.mpluno and c.lang_type='").append(req.getLangType()).append("' ")
                .append( " left join dcp_unit_lang d on d.eid=a.eid and d.unit=a.mpunit and d.lang_type='").append(req.getLangType()).append("'")
                .append(" left join dcp_goods e on e.eid=a.eid and e.pluno=a.mpluno ")
                .append(" left join dcp_warehouse f on f.eid=a.eid and f.warehouse=a.fromwarehouse and a.organizationno=f.organizationno ")
                .append(" left join dcp_goods_unit g on g.eid=a.eid and g.pluno=a.mpluno and g.ounit=a.mpunit ")
                .append(" left join dcp_unit h on h.eid=a.eid and h.unit=a.mpunit ")
                .append(" left join dcp_goods_lang i on i.eid=a.eid and i.pluno=a.pluno and i.lang_type='").append(req.getLangType()).append("' ")

                .append(" left join dcp_warehouse_LANG j on j.eid=a.eid and j.warehouse=a.fromwarehouse and j.lang_type='").append(req.getLangType()).append("' ")
                .append(" left join dcp_warehouse_LANG k on k.eid=a.eid and k.warehouse=a.towarehouse and k.lang_type='").append(req.getLangType()).append("' ")
                .append(" left join dcp_warehouse_LANG l on l.eid=a.eid and l.warehouse=b.fromwarehouse and l.lang_type='").append(req.getLangType()).append("' ")
                .append(" left join dcp_warehouse_LANG m on m.eid=a.eid and m.warehouse=b.towarehouse and m.lang_type='").append(req.getLangType()).append("' ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBatchNo())) {
            sb.append(" AND a.BATCHNO='").append(req.getRequest().getBatchNo()).append("'");
        }
        return sb.toString();
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_BatchingDetailQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.BDATE DESC,a.BATCHNO ASC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.*,sd.TOTPQTY,sd.TOTCQTY ")
                .append(" ,gl.ORG_NAME PROCESS_ERP_NAME,b.name as employeename,c.departname ")
                .append(" FROM MES_BATCHING a ")
                .append(" LEFT JOIN DCP_ORG_LANG gl on gl.EID=a.EID and gl.ORGANIZATIONNO=a.PROCESS_ERP_NO and gl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN ( SELECT EID,ORGANIZATIONNO,BATCHNO,SUM(PQTY) TOTPQTY,COUNT(DISTINCT PLUNO) TOTCQTY " +
                        "   FROM MES_BATCHING_DETAIL" +
                        "   GROUP BY EID,ORGANIZATIONNO,BATCHNO ) sd on a.EID=sd.EID and a.BATCHNO=sd.BATCHNO and a.ORGANIZATIONNO=sd.ORGANIZATIONNO ")
                .append(" left join dcp_employee b on b.eid=a.eid and a.employeeid=b.employeeno " +
                        " left join DCP_DEPARTMENT_LANG c on c.eid=a.eid and a.departid=c.departno and c.lang_type='").append(req.getLangType()).append("'")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBatchNo())) {
            sb.append(" AND a.BATCHNO='").append(req.getRequest().getBatchNo()).append("'");
        }

        sb.append("  ) a ORDER BY a.BDATE DESC,a.BATCHNO ASC ");

        return sb.toString();
    }
}
