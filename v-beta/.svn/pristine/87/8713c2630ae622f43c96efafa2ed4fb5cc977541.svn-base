package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SortingAssignProcessReq;
import com.dsc.spos.json.cust.res.DCP_SortingAssignProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.batchLocation.BatchLocationPlu;
import com.dsc.spos.utils.batchLocation.BatchLocationStockAlloc;
import com.dsc.spos.utils.batchLocation.WarehouseLocationPlu;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DCP_SortingAssignProcess extends SPosAdvanceService<DCP_SortingAssignProcessReq, DCP_SortingAssignProcessRes> {

    //    dispatch下发任务 add追加下发 close结束
    private static final String OPTYPE_DISPATCH = "dispatch";
    private static final String OPTYPE_ADD = "add";
    private static final String OPTYPE_CLOSE = "close";

    @Override
    protected void processDUID(DCP_SortingAssignProcessReq req, DCP_SortingAssignProcessRes res) throws Exception {

        String opType = req.getRequest().getOpType();
        String billNo = req.getRequest().getBillNo();

        List<Map<String, Object>> qDatas = this.doQueryData(getQuerySql(req), null);

        if (qDatas == null || CollectionUtils.isEmpty(qDatas)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到可操作的单据");
        }
        String status = qDatas.get(0).get("STATUS").toString();
        String organizationNo = qDatas.get(0).get("ORGANIZATIONNO").toString();

        if (OPTYPE_DISPATCH.equals(opType)) {
            if (!"0".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "分拣任务已下发!");
            }
        } else if (OPTYPE_ADD.equals(opType)) {
            if ("2".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据已完成!不可追加！");
            }
            qDatas = this.doQueryData(getQuerySqlByAdd(req), null);
        }

        if (OPTYPE_CLOSE.equals(opType)) {
            //① 检查关联分拣任务单状态非全部“2.已完成”，不可结束！
            //② 更新单据状态="2.已结束"（不可追加！）

            ColumnDataValue condition = new ColumnDataValue();
            condition.append("EID", DataValues.newString(req.geteId()));
            condition.append("BILLNO", DataValues.newString(billNo));

            ColumnDataValue dcp_sortingassign = new ColumnDataValue();

            dcp_sortingassign.append("STATUS", DataValues.newString("2"));
            dcp_sortingassign.append("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_sortingassign.append("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            dcp_sortingassign.append("CLOSEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_sortingassign.append("CLOSETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SORTINGASSIGN", condition, dcp_sortingassign)));

        } else {

            List<BatchLocationPlu> batchLocationPlus = new ArrayList<>();
            //按库位分配生成对应的dcp_sortingAssign_batch
            for (Map<String, Object> qData : qDatas) {
                BatchLocationPlu batchLocation = new BatchLocationPlu();

                batchLocation.setId(Integer.parseInt(qData.get("ITEM").toString()));
                batchLocation.setPluNo(qData.get("PLUNO").toString());
                batchLocation.setFeatureNo(qData.get("FEATURENO").toString());
                batchLocation.setPUnit(qData.get("BASEUNIT").toString());
                batchLocation.setPQty(qData.get("BASEQTY").toString());
                batchLocation.setFeatureNo(qData.get("FEATURENO").toString());
                batchLocation.setWarehouse(qData.get("WAREHOUSE").toString());

                batchLocationPlus.add(batchLocation);

                //回写出库通知单状态
                ColumnDataValue dcp_stockoutnotice_detail = new ColumnDataValue();
                ColumnDataValue condition = new ColumnDataValue();
                condition.add("EID", DataValues.newString(req.geteId()));
                condition.add("BILLNO", DataValues.newString(qData.get("OFNO").toString()));
                condition.add("ITEM", DataValues.newString(qData.get("OITEM").toString()));

                dcp_stockoutnotice_detail.add("STATUS", DataValues.newString("3"));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_stockoutnotice_detail", condition, dcp_stockoutnotice_detail)));

                if (OPTYPE_ADD.equals(opType)){
                    //追加回写
                    ColumnDataValue dcp_sortingassign_detail = new ColumnDataValue();
                    ColumnDataValue detailCondition = new ColumnDataValue();

                    detailCondition.add("EID", DataValues.newString(req.geteId()));
                    detailCondition.add("BILLNO", DataValues.newString(qData.get("BILLNO").toString()));
                    detailCondition.add("ITEM", DataValues.newString(qData.get("ITEM").toString()));

                    dcp_sortingassign_detail.append("ADDISDISPATCH",DataValues.newString("U"));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SORTINGASSIGN_DETAIL", detailCondition, dcp_sortingassign_detail)));

                }

            }

            List<WarehouseLocationPlu> warehouseLocationPlus = BatchLocationStockAlloc.batchLocationStockAlloc(batchLocationPlus);

            List<Con> pickList = new ArrayList<>();

            List<Map<String, Object>> pickGroup = doQueryData(getPickGroupSql(req, organizationNo), null);
            List<Map<String, Object>> pickGroupObject = doQueryData(getPickGroupObjectSql(req, organizationNo), null);

            for (WarehouseLocationPlu warehouseLocation : warehouseLocationPlus) { //回写拣货分区和拣货优先序

//                -抓取系统有效的拣货分区数据，然后按照以下优先级顺序排序后，写入DCP_SORTINGASSIGN_CON：
//                ①适用对象 = 指定对象 + 拣货范围类型 = 库位
//                ②适用对象 = 指定对象 + 拣货范围类型 = 品类 / 商品
//                ③适用对象 = 全部对象 + 拣货范围类型 = 库位
//                ④适用对象 = 全部对象 + 拣货范围类型 = 品类 / 商品
//                同一类型的按拣货分区编号升序排序，优先级 = 排序顺序；
                Map<String, Object> condition = new HashMap<>();
                condition.put("ITEM", warehouseLocation.getId());
                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(qDatas, condition, false);
                Map<String, Object> oneData = detailData.get(0);

                Map<String, Object> pickData = new HashMap<>();
                pickData.put("NEWTYPE", oneData.get("OBJECTTYPE").toString());
                pickData.put("NEWCODE", oneData.get("OBJECTID").toString());

                //指定对象
                List<Map<String, Object>> objectSearch = MapDistinct.getWhereMap(pickGroupObject, pickData, true);
                int sortId = 5;
                List<Map<String, Object>> searchData = null;
                boolean sorted = false;

                if (CollectionUtils.isNotEmpty(objectSearch)) {
                    for (Map<String, Object> objectMap : objectSearch) {
                        pickData = new HashMap<>();
                        pickData.put("PICKGROUPNO", objectMap.get("PICKGROUPNO").toString());
                        pickData.put("OBJECTRANGE", "1");
                        pickData.put("TYPE", "3");
                        pickData.put("SEARCH", warehouseLocation.getLocation());
                        searchData = MapDistinct.getWhereMap(pickGroup, pickData, false);
                        sorted = CollectionUtils.isNotEmpty(searchData);
                        if (sorted) {
                            sortId = 1;
                            break;
                        }
                        pickData = new HashMap<>();
                        pickData.put("PICKGROUPNO", objectMap.get("PICKGROUPNO").toString());
                        pickData.put("OBJECTRANGE", "1");
                        pickData.put("SEARCH", warehouseLocation.getPluNo());
                        searchData = MapDistinct.getWhereMap(pickGroup, pickData, false);

                        sorted = CollectionUtils.isNotEmpty(searchData);
                        if (sorted) {
                            sortId = 2;
                            break;
                        }
                    }

                }
                //跳过指定对象
                if (!sorted) {
                    pickData = new HashMap<>();
                    pickData.put("OBJECTRANGE", "0");
                    pickData.put("TYPE", "3");
                    pickData.put("SEARCH", warehouseLocation.getLocation());
                    searchData = MapDistinct.getWhereMap(pickGroup, pickData, false);
                    sorted = CollectionUtils.isNotEmpty(searchData);
                    if (sorted) {
                        sortId = 3;
                    }
                }

                if (!sorted) {
                    pickData = new HashMap<>();
                    pickData.put("OBJECTRANGE", "0");
                    pickData.put("SEARCH", warehouseLocation.getPluNo());
                    searchData = MapDistinct.getWhereMap(pickGroup, pickData, false);
                    sorted = CollectionUtils.isNotEmpty(searchData);
                    if (sorted) {
                        sortId = 4;
                    }
                }
//                if (!sorted){
//                    sortId = 5;
//                }

                warehouseLocation.setSortId(sortId);
                if (CollectionUtils.isNotEmpty(searchData)) {
                    warehouseLocation.setPickGroupNo(searchData.get(0).get("PICKGROUPNO").toString());
                } else {
                    warehouseLocation.setPickGroupNo("");
                }

                Con con = new Con();
                con.setPickGroupNo(warehouseLocation.getPickGroupNo());
                con.setSortId(warehouseLocation.getSortId());
                if (!pickList.contains(con)) {
                    pickList.add(con);
                }
            }

//            //排序
            List<WarehouseLocationPlu> sortedWLP = warehouseLocationPlus.stream()
                    .sorted(Comparator.comparing(WarehouseLocationPlu::getSortId).thenComparing(WarehouseLocationPlu::getPickGroupNo))
                    .collect(Collectors.toList());


            if (OPTYPE_DISPATCH.equals(opType)){
                ColumnDataValue delCondition = new ColumnDataValue();

                delCondition.add("EID", DataValues.newString(req.geteId()));
                delCondition.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                delCondition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SORTINGASSIGN_CON", delCondition)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SORTINGASSIGN_BATCH", delCondition)));

//                ColumnDataValue delCondition2 = new ColumnDataValue();
//                delCondition2.add("EID", DataValues.newString(req.geteId()));
//                delCondition2.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
//                delCondition2.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));


            }

            List<DocNo> docNoList = getDocNo(req, qDatas);
            List<Map<String, Object>> mesSortDataDetail = doQueryData(getMES_SORTDATADETAILSql(req, docNoList), null);

            int item = 0;
            if (OPTYPE_ADD.equals(opType)){
                String querySql = " SELECT MAX(ITEM) MITEM FROM DCP_SORTINGASSIGN_BATCH WHERE EID='"+req.geteId() + "'" +
                        " AND BILLNO='"+req.getRequest().getBillNo()+"'";
                List<Map<String, Object>> mBatch = doQueryData(querySql,null);

                if (CollectionUtils.isNotEmpty(mBatch)){
                    item = Integer.parseInt(mBatch.get(0).get("ITEM").toString());
                }
            }

            String pickGroupNo = null;
            for (WarehouseLocationPlu warehouseLocation : sortedWLP) { //生成
                ColumnDataValue dcp_sortingAssign_batch = new ColumnDataValue();

                Map<String, Object> condition = new HashMap<>();
                condition.put("ITEM", warehouseLocation.getId());
                List<Map<String, Object>> detailData = MapDistinct.getWhereMap(qDatas, condition, false);
                Map<String, Object> oneData = detailData.get(0);

                dcp_sortingAssign_batch.add("EID", DataValues.newString(oneData.get("EID").toString()));
                dcp_sortingAssign_batch.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
                dcp_sortingAssign_batch.add("BILLNO", DataValues.newString(oneData.get("BILLNO").toString()));
                dcp_sortingAssign_batch.add("ITEM", DataValues.newString(++item));
                dcp_sortingAssign_batch.add("OITEM", DataValues.newString(oneData.get("ITEM").toString()));
                dcp_sortingAssign_batch.add("PLUNO", DataValues.newString(warehouseLocation.getPluNo()));
                dcp_sortingAssign_batch.add("FEATURENO", DataValues.newString(oneData.get("FEATURENO").toString()));
                dcp_sortingAssign_batch.add("CATEGORY", DataValues.newString(oneData.get("CATEGORY").toString()));
                dcp_sortingAssign_batch.add("PUNIT", DataValues.newString(oneData.get("PUNIT").toString()));
                dcp_sortingAssign_batch.add("NOQTY", DataValues.newString(oneData.get("NOQTY").toString()));

                dcp_sortingAssign_batch.add("PQTY", DataValues.newString(BigDecimalUtils.div(Double.parseDouble(warehouseLocation.getAllocQty()), Double.parseDouble(oneData.get("UNITRATIO").toString()))));

                dcp_sortingAssign_batch.add("BASEUNIT", DataValues.newString(oneData.get("BASEUNIT").toString()));
                dcp_sortingAssign_batch.add("UNITRATIO", DataValues.newString(oneData.get("UNITRATIO").toString()));

                dcp_sortingAssign_batch.add("BASEQTY", DataValues.newString(warehouseLocation.getAllocQty()));

                dcp_sortingAssign_batch.add("WAREHOUSE", DataValues.newString(warehouseLocation.getWarehouse()));
                dcp_sortingAssign_batch.add("LOCATION", DataValues.newString(warehouseLocation.getLocation()));
                dcp_sortingAssign_batch.add("BATCHNO", DataValues.newString(warehouseLocation.getBatchNo()));
                dcp_sortingAssign_batch.add("PRODDATE", DataValues.newString(DateFormatUtils.getPlainDate(warehouseLocation.getProdDate())));
                dcp_sortingAssign_batch.add("EXPDATE", DataValues.newString(DateFormatUtils.getPlainDate(warehouseLocation.getValidDate())));
                dcp_sortingAssign_batch.add("PICKGROUPNO", DataValues.newString(warehouseLocation.getPickGroupNo()));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SORTINGASSIGN_BATCH", dcp_sortingAssign_batch)));

                if (!warehouseLocation.getPickGroupNo().equals(pickGroupNo)) {
                    List<WarehouseLocationPlu> filterWLP = sortedWLP.stream().filter(e -> warehouseLocation.getPickGroupNo().equals(e.getPickGroupNo()))
                            .collect(Collectors.toList());
                    insertSortTask(req, filterWLP, qDatas, mesSortDataDetail);
                    pickGroupNo = warehouseLocation.getPickGroupNo();
                }

//                ColumnDataValue mes_sortingTask_detail = new ColumnDataValue();
//                mes_sortingTask_detail.add("EID", DataValues.newString(oneData.get("EID")));
//                mes_sortingTask_detail.add("TASKNO", DataValues.newString(taskNo));
//                mes_sortingTask_detail.add("ITEM", DataValues.newString(item));
//                mes_sortingTask_detail.add("DOCNO", DataValues.newString(oneData.get("BILLNO")));
//                mes_sortingTask_detail.add("DOCITEM", DataValues.newString(oneData.get("ITEM")));
//                mes_sortingTask_detail.add("REQUIRENO", DataValues.newString(oneData.get("ORGANIZATIONNO")));
//                mes_sortingTask_detail.add("BATCH", DataValues.newString(warehouseLocation.getBatchNo()));
//                mes_sortingTask_detail.add("OBATCH", DataValues.newString(oneData.get("BATCHNO")));
//                mes_sortingTask_detail.add("LOCATION", DataValues.newString(warehouseLocation.getLocation()));
//                mes_sortingTask_detail.add("PLUNO", DataValues.newString(warehouseLocation.getPluNo()));
//                mes_sortingTask_detail.add("FEATURENO", DataValues.newString(oneData.get("FEATURENO")));
//                mes_sortingTask_detail.add("QTY", DataValues.newString(warehouseLocation.getAllocQty()));
//                mes_sortingTask_detail.add("UNIT", DataValues.newString(warehouseLocation.getPUnit()));
//                mes_sortingTask_detail.add("AQTY", DataValues.newString(warehouseLocation.getAllocQty()));
//                mes_sortingTask_detail.add("TOTAQTY", DataValues.newString(oneData.get("POQTY")));
//                mes_sortingTask_detail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
//                mes_sortingTask_detail.add("ISOPERATE", DataValues.newString("N"));
//                mes_sortingTask_detail.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO")));
//                mes_sortingTask_detail.add("OFNO", DataValues.newString(oneData.get("BILLNO")));
//                mes_sortingTask_detail.add("OITEM", DataValues.newString(oneData.get("ITEM")));
//                mes_sortingTask_detail.add("INFOITEM", DataValues.newString(item));
//
//                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_SORTINGTASK_DETAIL", mes_sortingTask_detail)));
//                GoodsInfo goodsInfo = new GoodsInfo();
//
//                goodsInfo.setPluNo(warehouseLocation.getPluNo());
//                goodsInfo.setRequireNo(oneData.get("ORGANIZATIONNO").toString());
//                goodsInfo.setFeatureNo(warehouseLocation.getFeatureNo());
//                if (goodsInfoList.contains(goodsInfo)) {
//                    goodsInfo = goodsInfoList.get(goodsInfoList.indexOf(goodsInfo));
//                    double qty = Double.parseDouble(warehouseLocation.getAllocQty());
//                    double oQty = Double.parseDouble(goodsInfo.getQty());
//                    goodsInfo.setQty(String.valueOf(qty + oQty));
//                    goodsInfo.setAty(String.valueOf(qty + oQty));
//                } else {
//                    goodsInfoList.add(goodsInfo);
//                    goodsInfo.setQty(warehouseLocation.getAllocQty());
//                    goodsInfo.setAty(warehouseLocation.getAllocQty());
//                    goodsInfo.setTotAQty(oneData.get("POQTY").toString());
//                    goodsInfo.setUnit(warehouseLocation.getPUnit());
//                }
            }

//            int i = 0;
//            for (GoodsInfo goodsInfo : goodsInfoList) {
//
//                ColumnDataValue mes_sortingtask_goodsinfo = new ColumnDataValue();
//                mes_sortingtask_goodsinfo.add("EID", DataValues.newString(req.geteId()));
//                mes_sortingtask_goodsinfo.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
//                mes_sortingtask_goodsinfo.add("TASKNO", DataValues.newString(taskNo));
//                mes_sortingtask_goodsinfo.add("ITEM", DataValues.newString(++i));
//                mes_sortingtask_goodsinfo.add("REQUIRENO", DataValues.newString(goodsInfo.getRequireNo()));
//                mes_sortingtask_goodsinfo.add("PLUNO", DataValues.newString(goodsInfo.getPluNo()));
//                mes_sortingtask_goodsinfo.add("FEATURENO", DataValues.newString(goodsInfo.getFeatureNo()));
//                mes_sortingtask_goodsinfo.add("QTY", DataValues.newString(goodsInfo.getQty()));
//                mes_sortingtask_goodsinfo.add("UNIT", DataValues.newString(goodsInfo.getUnit()));
//                mes_sortingtask_goodsinfo.add("AQTY", DataValues.newString(goodsInfo.getAty()));
//                mes_sortingtask_goodsinfo.add("TOTAQTY", DataValues.newString(goodsInfo.getTotAQty()));
//                mes_sortingtask_goodsinfo.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
//
//                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("mes_sortingtask_goodsinfo", mes_sortingtask_goodsinfo)));
//
//            }


            for (Con con : pickList) {

                if (StringUtils.isNotEmpty(con.getPickGroupNo())) {
                    ColumnDataValue dcp_sortingassign_con = new ColumnDataValue();
                    dcp_sortingassign_con.add("EID", DataValues.newString(req.geteId()));
                    dcp_sortingassign_con.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
                    dcp_sortingassign_con.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
                    dcp_sortingassign_con.add("PICKGROUPNO", DataValues.newString(con.getPickGroupNo()));
                    dcp_sortingassign_con.add("SORITID", DataValues.newString(con.getSortId()));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SORTINGASSIGN_CON", dcp_sortingassign_con)));

                }

            }

//            ColumnDataValue mes_sortingTask = new ColumnDataValue();
//            Map<String, Object> oneData = qDatas.get(0);
//
//            mes_sortingTask.add("EID", DataValues.newString(req.geteId()));
//            mes_sortingTask.add("TASKNO", DataValues.newString(taskNo));
//            mes_sortingTask.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO")));
//            mes_sortingTask.add("WAREHOUSENO", DataValues.newString(oneData.get("WAREHOUSENO")));
//            mes_sortingTask.add("TOTQTY", DataValues.newString(oneData.get("TOTQTY")));
//            mes_sortingTask.add("TOTCQTY", DataValues.newString(oneData.get("TOTCQTY")));
//            mes_sortingTask.add("TYPE", DataValues.newString("3"));
//            mes_sortingTask.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
//            mes_sortingTask.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
//            mes_sortingTask.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
//            mes_sortingTask.add("STATUS", DataValues.newString("0"));
//            mes_sortingTask.add("RDATE", DataValues.newString(oneData.get("RDATE")));
//            mes_sortingTask.add("TOTCQTY", DataValues.newString("0"));
//
//            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_SORTINGTASK", mes_sortingTask)));

            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("BILLNO", DataValues.newString(billNo));

            ColumnDataValue dcp_sortingassign = new ColumnDataValue();

            dcp_sortingassign.add("STATUS", DataValues.newString("1"));
            dcp_sortingassign.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_sortingassign.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            dcp_sortingassign.add("CLOSEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_sortingassign.add("CLOSETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SORTINGASSIGN", condition, dcp_sortingassign)));

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }



    private void insertSortTask(DCP_SortingAssignProcessReq req, List<WarehouseLocationPlu> filterWLP, List<Map<String, Object>> qDatas, List<Map<String, Object>> mesSortDataDetail) throws Exception {

        String taskNo = getOrderNO(req, "FJRW");

        Map<String, Object> oneData = qDatas.get(0);

        List<GoodsInfo> goodsInfoList = new ArrayList<>();
        int item = 0;
        double totQty = 0;
        int totCQty = 0;
        for (WarehouseLocationPlu warehouseLocation : filterWLP) { //生成

            Map<String, Object> condition = new HashMap<>();
            condition.put("ITEM", warehouseLocation.getId());
            List<Map<String, Object>> detailData = MapDistinct.getWhereMap(qDatas, condition, false);
            oneData = detailData.get(0);

            String requiretype = "2".equals(oneData.get("OBJECTTYPE").toString()) ? "1" : "0";

            String docNo = oneData.get("OFNO").toString() + oneData.get("OBJECTID").toString() + oneData.get("PTEMPLATENO").toString();
            String docItem = oneData.get("OITEM").toString();

            Map<String, Object> sortDataCondition = new HashMap<>();
            sortDataCondition.put("DOCNO", docNo);
            sortDataCondition.put("ERPITEM",docItem);
            List<Map<String, Object>> mesDetailData = MapDistinct.getWhereMap(mesSortDataDetail, sortDataCondition, false);

            if (CollectionUtils.isNotEmpty(mesDetailData)) {
                docItem = mesDetailData.get(0).get("ITEM").toString();
            }

            totCQty++;
            ColumnDataValue mes_sortingTask_detail = new ColumnDataValue();
            mes_sortingTask_detail.add("EID", DataValues.newString(oneData.get("EID").toString()));
            mes_sortingTask_detail.add("TASKNO", DataValues.newString(taskNo));
            mes_sortingTask_detail.add("ITEM", DataValues.newString(++item));

//            mes_sortingTask_detail.add("DOCNO", DataValues.newString(oneData.get("BILLNO")));
//            mes_sortingTask_detail.add("DOCITEM", DataValues.newString(oneData.get("ITEM")));

            mes_sortingTask_detail.add("DOCNO", DataValues.newString(docNo));
            mes_sortingTask_detail.add("DOCITEM", DataValues.newString(docItem));

            mes_sortingTask_detail.add("REQUIRENO", DataValues.newString(oneData.get("OBJECTID").toString()));
            mes_sortingTask_detail.add("BATCH", DataValues.newString(warehouseLocation.getBatchNo()));
//            mes_sortingTask_detail.add("OBATCH", DataValues.newString(oneData.get("BATCHNO").toString()));
            mes_sortingTask_detail.add("LOCATION", DataValues.newString(warehouseLocation.getLocation()));
            mes_sortingTask_detail.add("PLUNO", DataValues.newString(warehouseLocation.getPluNo()));
            mes_sortingTask_detail.add("FEATURENO", DataValues.newString(StringUtils.toString(warehouseLocation.getFeatureNo(), " ")));
            double qty = BigDecimalUtils.div(Double.parseDouble(warehouseLocation.getAllocQty()), Double.parseDouble(oneData.get("UNITRATIO").toString()));
            totQty += qty;
            BigDecimalUtils.div(Double.parseDouble(warehouseLocation.getAllocQty()), Double.parseDouble(oneData.get("UNITRATIO").toString()));

            mes_sortingTask_detail.add("QTY", DataValues.newString(qty));
            mes_sortingTask_detail.add("UNIT", DataValues.newString(oneData.get("PUNIT").toString()));
            mes_sortingTask_detail.add("AQTY", DataValues.newString(qty));

            mes_sortingTask_detail.add("TOTAQTY", DataValues.newString(oneData.get("POQTY").toString()));
            mes_sortingTask_detail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            mes_sortingTask_detail.add("ISOPERATE", DataValues.newString("N"));
            mes_sortingTask_detail.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
            mes_sortingTask_detail.add("ERPITEM", DataValues.newString(oneData.get("OITEM").toString()));
            mes_sortingTask_detail.add("OFNO", DataValues.newString(oneData.get("BILLNO").toString()));
            mes_sortingTask_detail.add("OITEM", DataValues.newString(oneData.get("ITEM").toString()));
//            mes_sortingTask_detail.add("INFOITEM", DataValues.newString(oneData.get("ITEM")));
            mes_sortingTask_detail.add("BASEUNIT", DataValues.newString(warehouseLocation.getPUnit()));
            mes_sortingTask_detail.add("BASEQTY", DataValues.newString(warehouseLocation.getAllocQty()));
            mes_sortingTask_detail.add("REQUIRETYPE", DataValues.newString(requiretype));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_SORTINGTASK_DETAIL", mes_sortingTask_detail)));
            GoodsInfo goodsInfo = new GoodsInfo();

            goodsInfo.setPluNo(warehouseLocation.getPluNo());
            goodsInfo.setRequireNo(oneData.get("OBJECTID").toString());
            goodsInfo.setFeatureNo(warehouseLocation.getFeatureNo());
            if (goodsInfoList.contains(goodsInfo)) {
                goodsInfo = goodsInfoList.get(goodsInfoList.indexOf(goodsInfo));
                double oQty = Double.parseDouble(goodsInfo.getQty());
                goodsInfo.setQty(String.valueOf(qty + oQty));
                goodsInfo.setAty(String.valueOf(qty + oQty));

            } else {
                goodsInfoList.add(goodsInfo);
                goodsInfo.setQty(warehouseLocation.getAllocQty());
                goodsInfo.setAty(warehouseLocation.getAllocQty());
                goodsInfo.setTotAQty(oneData.get("POQTY").toString());
                goodsInfo.setUnit(warehouseLocation.getPUnit());
            }
        }

        ColumnDataValue mes_sortingTask = new ColumnDataValue();

        mes_sortingTask.add("EID", DataValues.newString(req.geteId()));
        mes_sortingTask.add("TASKNO", DataValues.newString(taskNo));
        mes_sortingTask.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
        mes_sortingTask.add("WAREHOUSENO", DataValues.newString(oneData.get("WAREHOUSE").toString()));
        mes_sortingTask.add("TOTQTY", DataValues.newString(totQty));
        mes_sortingTask.add("TOTCQTY", DataValues.newString(totCQty));
        mes_sortingTask.add("TYPE", DataValues.newString("1"));
        mes_sortingTask.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
        mes_sortingTask.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
        mes_sortingTask.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
        mes_sortingTask.add("STATUS", DataValues.newString("0"));
        mes_sortingTask.add("RDATE", DataValues.newString(oneData.get("DELIVERYDATE").toString()));


        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_SORTINGTASK", mes_sortingTask)));

        int i = 0;
        for (GoodsInfo goodsInfo : goodsInfoList) {

            ColumnDataValue mes_sortingtask_goodsinfo = new ColumnDataValue();
            mes_sortingtask_goodsinfo.add("EID", DataValues.newString(req.geteId()));
            mes_sortingtask_goodsinfo.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO").toString()));
            mes_sortingtask_goodsinfo.add("TASKNO", DataValues.newString(taskNo));
            mes_sortingtask_goodsinfo.add("ITEM", DataValues.newString(++i));
            mes_sortingtask_goodsinfo.add("REQUIRENO", DataValues.newString(goodsInfo.getRequireNo()));
            mes_sortingtask_goodsinfo.add("PLUNO", DataValues.newString(goodsInfo.getPluNo()));
            mes_sortingtask_goodsinfo.add("FEATURENO", DataValues.newString(goodsInfo.getFeatureNo()));
            mes_sortingtask_goodsinfo.add("QTY", DataValues.newString(goodsInfo.getQty()));
            mes_sortingtask_goodsinfo.add("UNIT", DataValues.newString(goodsInfo.getUnit()));
            mes_sortingtask_goodsinfo.add("AQTY", DataValues.newString(goodsInfo.getAty()));
            mes_sortingtask_goodsinfo.add("TOTAQTY", DataValues.newString(goodsInfo.getTotAQty()));
            mes_sortingtask_goodsinfo.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("mes_sortingtask_goodsinfo", mes_sortingtask_goodsinfo)));

        }

    }


    private String getQueryExistedMes_sortingTask(DCP_SortingAssignProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT b.*,a.STATUS FROM MES_SORTINGTASK a " +
                " INNER JOIN mes_sortingTask_detail b on a.eid=b.eid and a.TASKNO=b.TASKNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO" +
                " ORDER BY b.TASKNO,b.ITEM DESC  ");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" and b.OFNO='").append(req.getRequest().getBillNo()).append("'");
        }
        return querySql.toString();
    }


    protected String getQuerySqlByAdd(DCP_SortingAssignProcessReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.*,a.STATUS,a.OTYPE FROM  ")
                .append(" DCP_SORTINGASSIGN a ")
                .append(" LEFT JOIN DCP_SORTINGASSIGN_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
                .append(" WHERE a.eid='").append(req.geteId()).append("'")
        ;
        querySql.append(" AND b.ISADDITIONAL='Y' ");
        querySql.append(" AND NVL(b.ADDISDISPATCH,'N')='N' ");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" and b.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        return querySql.toString();
    }

    @Override
    protected String getQuerySql(DCP_SortingAssignProcessReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.*,a.STATUS,a.OTYPE FROM  ")
                .append(" DCP_SORTINGASSIGN a ")
                .append(" LEFT JOIN DCP_SORTINGASSIGN_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
                .append(" WHERE a.eid='").append(req.geteId()).append("'")
        ;
//        querySql.append(" AND a.STATUS='0' ");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" and b.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        return querySql.toString();
    }

    private String getPickGroupObjectSql(DCP_SortingAssignProcessReq req, String orgNo) {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT b.PICKGROUPNO, " +
                "       b.TYPE, " +
                "       c.ROUTETYPE, " +
                "       b.CODE, " +
                "       CASE " +
                "           WHEN b.TYPE='1' THEN c.CODE " +
                "       ELSE b.CODE END NEWCODE, " + //转换code
                "       CASE " +    //这里将组织和门店对换，来匹配 OBJECTTYPE
                "           WHEN b.TYPE = '1' AND c.ROUTETYPE = 0 THEN N'3' " +
                "           WHEN b.TYPE = '1' AND c.ROUTETYPE = 1 THEN N'2' " +
                "           WHEN b.TYPE = '2' THEN N'3' " +
                "           WHEN b.TYPE = '3' THEN N'2' END NEWTYPE " +
                " FROM DCP_PICKGROUP a " +
                "         INNER JOIN DCP_PICKGROUP_OBJECT b " +
                "                    on a.EID = b.EID and a.ORGANIZATIONNO = b.ORGANIZATIONNO and a.PICKGROUPNO = b.PICKGROUPNO " +
                "         LEFT JOIN MES_ROUTE_DETAIL c on b.EID = c.EID and c.ROUTENO = b.CODE and b.TYPE = '1'");

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("' AND a.STATUS='100' AND a.ORGANIZATIONNO='").append(orgNo).append("'");

        return querySql.toString();
    }

    private String getMES_SORTDATADETAILSql(DCP_SortingAssignProcessReq req, List<DocNo> docNoList) throws Exception {

        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT * FROM MES_SORTDATADETAIL WHERE eid='").append(req.geteId()).append("'");
        querySql.append(" AND ( 1=2 ");
        if (CollectionUtils.isNotEmpty(docNoList)) {
            for (DocNo docNo : docNoList) {
                querySql.append(" OR DOCNO='").append(docNo.toString()).append("'");
            }
        }
        querySql.append(")");

        return querySql.toString();

    }

    private String getPickGroupSql(DCP_SortingAssignProcessReq req, String orgNo) throws Exception {

        String querySql = " SELECT c.*," +
                "  a.WAREHOUSE, a.WAREREGIONNO, PICKTYPE, PICKRANGETYPE, OBJECTRANGE" +
                "  ,CASE WHEN c.TYPE='1' THEN g.PLUNO " +
                "           ELSE c.CODE END as SEARCH  " +
                " FROM DCP_PICKGROUP a " +
                " INNER JOIN DCP_PICKGROUP_RANGE c on a.EID = c.EID and a.ORGANIZATIONNO = c.ORGANIZATIONNO and a.PICKGROUPNO = c.PICKGROUPNO " +
                " LEFT JOIN DCP_GOODS g ON c.eid=g.EID and c.CODE=g.CATEGORY and c.TYPE='1' " +
                " WHERE a.EID='" + req.geteId() + "' AND a.STATUS='100' AND a.ORGANIZATIONNO='" + orgNo + "'" +
                " ORDER BY a.OBJECTRANGE DESC ,c.TYPE DESC  ";

        return querySql;
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_SortingAssignProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SortingAssignProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SortingAssignProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_SortingAssignProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SortingAssignProcessReq> getRequestType() {
        return new TypeToken<DCP_SortingAssignProcessReq>() {
        };
    }

    @Override
    protected DCP_SortingAssignProcessRes getResponseType() {
        return new DCP_SortingAssignProcessRes();
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = {"pickGroupNo"})
    private class Con {
        String pickGroupNo;
        int sortId;
    }

    @Data
    @EqualsAndHashCode(of = {"requireNo", "pluNo", "featureNo"})
    private class GoodsInfo {
        private String requireNo;
        private String pluNo;
        private String featureNo;

        private String unit;
        private String qty;
        private String aty;
        private String totAQty;
    }

    private List<DocNo> getDocNo(DCP_SortingAssignProcessReq req, List<Map<String, Object>> sortingDetail) {
        List<DocNo> docNoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sortingDetail)) {
            for (Map<String, Object> detail : sortingDetail) {
                DocNo docNo = new DocNo();
                docNo.setBillNo(detail.get("OFNO").toString());
                docNo.setObjectId(detail.get("OBJECTID").toString());
                docNo.setTemplateNo(detail.get("PTEMPLATENO").toString());


                if (!docNoList.contains(docNo)) {
                    docNoList.add(docNo);
                }
            }
        }

        return docNoList;
    }

    @Data
    @EqualsAndHashCode
    private class DocNo {
        private String billNo;
        private String objectId;
        private String templateNo;

        @Override
        public String toString() {
            return billNo + objectId + templateNo;
        }
    }


}
