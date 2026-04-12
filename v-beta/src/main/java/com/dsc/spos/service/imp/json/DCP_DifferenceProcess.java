package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DifferenceProcessReq;
import com.dsc.spos.json.cust.req.DCP_DifferenceProcessReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_StockInProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockOutProcessReq;
import com.dsc.spos.json.cust.res.DCP_DifferenceProcessRes;
import com.dsc.spos.json.cust.res.DCP_StockInProcessRes;
import com.dsc.spos.json.cust.res.DCP_StockOutProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DCP_DifferenceProcess extends SPosAdvanceService<DCP_DifferenceProcessReq, DCP_DifferenceProcessRes> {

    @Override
    protected void processDUID(DCP_DifferenceProcessReq req, DCP_DifferenceProcessRes res) throws Exception {
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String Create_DATE = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String Create_TIME = df.format(cal.getTime());
        levelElm request = req.getRequest();
        String differenceNO = request.getDifferenceNo();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String docType = request.getDocType();
        String status = request.getStatus();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();

        List<Order> orders = new ArrayList<>();

        //status  0-待提交 1-待审核 2-已审核

        String differenceSql = "select a.status,a.ofno,a.TRANSFER_SHOP,a.TRANSFER_WAREHOUSE,a.WAREHOUSE,b.* from dcp_difference a" +
                " left join dcp_difference_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.differenceno=b.differenceno" +
                " where a.eid='" + eId + "'  and a.differenceno='" + differenceNO + "' ";//and a.organizationno='"+organizationNO+"'
        List<Map<String, Object>> differenceList = this.doQueryData(differenceSql, null);
        if (differenceList.size() == 0) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "差异单不存在");
        }
        String oldStatus = differenceList.get(0).get("STATUS").toString();
        String differenceOrg = differenceList.get(0).get("ORGANIZATIONNO").toString();
        String ofno = differenceList.get(0).get("OFNO").toString();
        String transferShop = differenceList.get(0).get("TRANSFER_SHOP").toString();
        String transferWarehouse = differenceList.get(0).get("TRANSFER_WAREHOUSE").toString();
        String differWarehouse = differenceList.get(0).get("WAREHOUSE").toString();
        String ckNo = "";
        String rkNo = "";
        if ("0".equals(status)) {
            //撤销
//【撤销】：（仅在收货组织视角下可撤销）状态：1.待审核-->0.待提交
//撤销前检查：
//● 单据状态非【1-待审核】状态不可撤销！
//● 当前组织<>【所属组织】不可撤销！
//● 检查时效：判断系统时间已超出【入库单确认日期时间+差异申诉截止时效】，则无法撤销单据！(待确认）
//提交处理：更新单据状态STATUS=【0-待提交】，清空以下字段值：提交人SUBMITBY、提交日期SUBMIT_DATE、提交时间SUBMIT_TIME；更新最近更改人MODIFYBY、最近更改日期MODIFY_DATE、最近更改时间MODIFY_TIME

            if (!"1".equals(oldStatus)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非【待审核】状态不可提交！");
            }
            if (!differenceOrg.equals(organizationNO)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前组织不等于申诉单【所属组织】不可提交！");
            }
            String stockInDetailSql = "select a.CONFIRM_DATE,b.* from dcp_stockin a" +
                    " left join dcp_stockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.stockinno=b.stockinno" +
                    " where a.eid='" + eId + "' " +
                    " and a.organizationno='" + organizationNO + "' and a.stockinno='" + ofno + "'";
            List<Map<String, Object>> stockInDetailList = this.doQueryData(stockInDetailSql, null);
            if (stockInDetailList.size() <= 0) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单不存在！");
            } else {
                String diffAppealTimes = PosPub.getPARA_SMS(dao, req.geteId(), "", "Diff_Appeal_Times");

                String confirmDate = stockInDetailList.get(0).get("CONFIRM_DATE").toString();
                LocalDateTime confirmDateLocal = LocalDateTime.parse(confirmDate + diffAppealTimes, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));//new DateTimeFormatter("yyyyMMddHHmmss")

                if (LocalDateTime.now().isAfter(confirmDateLocal)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单确认日期时间+差异申诉截止时效已经超过，无法撤销！");
                }
            }

            UptBean ub1 = new UptBean("DCP_DIFFERENCE");
            ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
            ub1.addUpdateValue("SUBMITBY", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("SUBMIT_DATE", new DataValue(null, Types.VARCHAR));
            ub1.addUpdateValue("SUBMIT_TIME", new DataValue(null, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_DATE", new DataValue(Create_DATE, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_TIME", new DataValue(Create_TIME, Types.VARCHAR));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("DIFFERENCENO", new DataValue(differenceNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));


        } else if ("1".equals(status)) {
            //提交
//提交前检查：
//● 单据状态非【0-待提交】状态不可提交！
//● 当前组织<>申诉单【所属组织】不可提交！
//● 检查时效：判断系统时间已超出【入库单确认日期时间+差异申诉截止时效】，则无法提交单据！（待确认）
//● 检查是否存在申请调整量=0的明细，存在不可提交！
//提交处理：更新单据状态STATUS=【1-待审核】，更新单据异动资讯：提交人SUBMITBY、提交日期SUBMIT_DATE、提交时间SUBMIT_TIME
            if (!"0".equals(oldStatus)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非【待提交】状态不可提交！");
            }
            if (!differenceOrg.equals(organizationNO)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前组织不等于申诉单【所属组织】不可提交！");
            }
            for (Map<String, Object> map : differenceList) {
                BigDecimal req_qty = new BigDecimal(map.get("REQ_QTY").toString());
                if (req_qty.compareTo(BigDecimal.ZERO) == 0) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "存在申请调整量为0的明细，不可提交！");
                }
            }

            UptBean ub1 = new UptBean("DCP_DIFFERENCE");
            ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
            ub1.addUpdateValue("SUBMITBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("SUBMIT_DATE", new DataValue(Create_DATE, Types.VARCHAR));
            ub1.addUpdateValue("SUBMIT_TIME", new DataValue(Create_TIME, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("DIFFERENCENO", new DataValue(differenceNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        } else if ("2".equals(status)) {
            //【审核】：（仅在发货组织视角下可审核）状态流转：1.待审核-->2.已审核
            //审核前检查：
            //● 单据状态非【1-待审核】状态不可审核！
            //● 当前组织<>【发货组织】不可审核！
            //审核处理：
            //1、【单据类型】=“配送收货差异”：
            // 根据审核明细【核准量】方向判断生成对应收发货组织的出库/入库单调整库存，自动确认完成无需产生收货通知单：
            //（不应产生库存调整单，涉及内部交易结算的明细须走业务交易单据），规则如下：
            //● 核准量>0，即收货量>发货量，收货方多收，发货方需追加出库，分别生成无源配送出库
            //● 核准量<0，即收货量<发货量，收货方少收，发货方需退回入库，分别生成无源退配入库
            //● 核准量=0，即发货方不承担差异，无需做任何调整！
            //
            //备注：中台无在途仓概念，实收与应收的收货差异无归属方，暂无法通过调拨单据的形式调整往来！
            //
            //2、更新单据状态STATUS：所有行【核准量】=0，更新状态=【3.已拒绝】；否则更新状态=【2.已确认】
            //更新异动资讯：最近更改人MODIFYBY、最近更改日期MODIFY_DATE、最近更改时间MODIFY_TIME，确认人、确认日期、确认时间

            if (!"1".equals(oldStatus)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非【待审核】状态不可审核！");
            }

//            String stockInDSql = "select a.STOCKINNO,a.RECEIVING_QTY,c.pluno,c.featureno,c.punit," +
//                    " c.warehouse,c.baseunit,c.price,c.DISTRIPRICE,c.PQTY" +
//                    " c.UNIT_RATIO,a.plu_barcode,c.BATCH_NO,b.INVWAREHOUSE,b.organizationno, " +
//                    " b.TRANSFER_SHOP,b.TRANSFER_WAREHOUSE,c.ITEM " +
//                    "  from dcp_stockin_detail a " +
//                    " inner join dcp_difference b on a.eid=b.eid and a.organizationno=b.organizationno and a.stockinno=b.ofno" +
//                    " inner join dcp_difference_detail c on c.eid=b.eid and c.organizationno=b.organizationno and c.oitem=a.oitem and c.differenceno=b.differenceno" +
//                    " where a.eid='" + eId + "' " +
//                    " and a.stockinno='" + ofno + "'";
////                    " and a.organizationno='" + organizationNO + "' ";
//            List<Map<String, Object>> stockInDList = this.executeQuerySQL_BindSQL(stockInDSql, null);
//
//            if (CollectionUtils.isNotEmpty(stockInDList)) {
//                //配送收货差异
//                //发货组织配送出库
//
//                //TRANSFER_SHOP  发货门店 transferShop
//                //SHOPID 门店 --收货门店？ differenceOrg
//
//                int detailItemCk = 0;
//                int detailItemRk = 0;
//                BigDecimal totCqtyc = BigDecimal.ZERO;
//                BigDecimal totPqtyc = BigDecimal.ZERO;
//                BigDecimal totAmtc = BigDecimal.ZERO;
//                BigDecimal totDistriAmtc = BigDecimal.ZERO;
//                List pfcList = new ArrayList();
//                List pfrList = new ArrayList();
//
//                BigDecimal totCqtyr = BigDecimal.ZERO;
//                BigDecimal totPqtyr = BigDecimal.ZERO;
//                BigDecimal totAmtr = BigDecimal.ZERO;
//                BigDecimal totDistriAmtr = BigDecimal.ZERO;
//
//                Map<String, Object> oneData = stockInDList.get(0);
//
//                for (Map<String, Object> map : stockInDList) {
//                    String stockInNo = map.get("STOCKINNO").toString();
//                    BigDecimal pqty = new BigDecimal(map.get("PQTY").toString());
//                    BigDecimal receivingQty = new BigDecimal(map.get("RECEIVING_QTY").toString());
//                    BigDecimal diffPqty = new BigDecimal(map.get("PQTY").toString());
//                    String pluno = map.get("PLUNO").toString();
//                    String featureno = map.get("FEATURENO").toString();
//                    String punit = map.get("PUNIT").toString();
//                    String warehouse = map.get("WAREHOUSE").toString();
//                    String oItem = map.get("ITEM").toString();
//
//                    String baseunit = map.get("BASEUNIT").toString();
//                    BigDecimal price = new BigDecimal(Check.Null(map.get("PRICE").toString()) ? "0" : map.get("PRICE").toString());
//                    BigDecimal distriprice = new BigDecimal(Check.Null(map.get("DISTRIPRICE").toString()) ? "0" : map.get("DISTRIPRICE").toString());
//
//                    int multiType = 1;
//                    if (diffPqty.compareTo(BigDecimal.ZERO) < 0) {
//                        multiType = -1;
//                    }
//                    BigDecimal absDiffPqty = diffPqty.multiply(BigDecimal.valueOf(multiType));
//
//                    BigDecimal amt = absDiffPqty.multiply(price);
//                    BigDecimal distriAmt = absDiffPqty.multiply(distriprice);
//                    BigDecimal unit_ratio = new BigDecimal(map.get("UNIT_RATIO").toString());
//                    BigDecimal baseQty = absDiffPqty.multiply(unit_ratio);
//                    String pluBarCode = map.get("PLU_BARCODE").toString();
//
//                    int lastDocType = 0;//1、【发货组织】配送出库单（自动确认）
//
//                    //2、【发货组织】退配入库单（自动确认）
//                    if (diffPqty.compareTo(BigDecimal.ZERO) > 0) {
//                        //【发货组织】配送出库单（自动确认）
//                        lastDocType = 1;
//                    }
//                    if (diffPqty.compareTo(BigDecimal.ZERO) < 0) {
//                        //【发货组织】退配入库单（自动确认）
//                        lastDocType = 2;
//                    }
//
//                    //出库
//                    if (lastDocType == 1) {
//                        if (Check.Null(ckNo)) {
//                            ckNo = this.getOrderNO(req, "PSCK");
//                        }
//                        detailItemCk++;
//
//                        totPqtyc = totPqtyc.add(absDiffPqty);
//                        totAmtc = totAmtc.add(amt);
//                        totDistriAmtc = totDistriAmtc.add(distriAmt);
//
//                        if (!pfcList.contains(pluno + featureno)) {
//                            pfcList.add(pluno + featureno);
//                        }
//
//                        ColumnDataValue detailColumns = new ColumnDataValue();
//                        detailColumns.add("EID", DataValues.newString(eId));
//                        detailColumns.add("ORGANIZATIONNO", DataValues.newString(transferShop));
//                        detailColumns.add("SHOPID", DataValues.newString(transferShop));
//                        detailColumns.add("STOCKOUTNO", DataValues.newString(ckNo));
//                        detailColumns.add("ITEM", DataValues.newString(detailItemCk));
//                        detailColumns.add("PLU_BARCODE", DataValues.newString(pluBarCode));
//                        detailColumns.add("PLUNO", DataValues.newString(pluno));
//                        detailColumns.add("FEATURENO", DataValues.newString(featureno));
//                        detailColumns.add("WAREHOUSE", DataValues.newString(warehouse));
//                        detailColumns.add("PUNIT", DataValues.newString(punit));
//                        detailColumns.add("PQTY", DataValues.newString(absDiffPqty));
//                        detailColumns.add("PRICE", DataValues.newString(price));
//                        detailColumns.add("AMT", DataValues.newString(amt));
//                        detailColumns.add("DISTRIPRICE", DataValues.newString(distriprice));
//                        detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt));
//                        detailColumns.add("BASEUNIT", DataValues.newString(baseunit));
//                        detailColumns.add("BASEQTY", DataValues.newString(baseQty));
//                        detailColumns.add("UNIT_RATIO", DataValues.newString(unit_ratio));
//                        detailColumns.add("BDATE", DataValues.newString(Create_DATE));
//                        detailColumns.add("PARTITION_DATE", DataValues.newString(Create_DATE));
//                        //detailColumns.add("POQTY",DataValues.newString(poqty));  mes用
//
//                        detailColumns.add("OFNO", DataValues.newString(differenceNO));
//                        detailColumns.add("OTYPE", DataValues.newString("6"));
//                        detailColumns.add("OITEM", DataValues.newString(oItem));
//
//                        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_DETAIL", detailColumns)));
//                    }
//
//                    //入库
//                    if (lastDocType == 2) {
//                        if (Check.Null(rkNo)) {
//                            rkNo = this.getOrderNO(req, "THRK");
//                        }
//                        detailItemRk++;
//
//                        totPqtyr = totPqtyr.add(absDiffPqty);
//                        totAmtr = totAmtr.add(amt);
//                        totDistriAmtr = totDistriAmtr.add(distriAmt);
//
//
//                        if (!pfrList.contains(pluno + featureno)) {
//                            pfrList.add(pluno + featureno);
//                        }
//
//                        ColumnDataValue detailColumns = new ColumnDataValue();
//                        detailColumns.add("EID", DataValues.newString(eId));
//                        detailColumns.add("ORGANIZATIONNO", DataValues.newString(transferShop));
//                        detailColumns.add("SHOPID", DataValues.newString(transferShop));
//                        detailColumns.add("STOCKINNO", DataValues.newString(rkNo));
//                        detailColumns.add("ITEM", DataValues.newString(detailItemRk));
//                        detailColumns.add("PLU_BARCODE", DataValues.newString(pluBarCode));
//                        detailColumns.add("PLUNO", DataValues.newString(pluno));
//                        detailColumns.add("FEATURENO", DataValues.newString(featureno));
//                        detailColumns.add("WAREHOUSE", DataValues.newString(warehouse));
//                        detailColumns.add("PUNIT", DataValues.newString(punit));
//                        detailColumns.add("POQTY", DataValues.newString(absDiffPqty));
//                        detailColumns.add("PRICE", DataValues.newString(price));
//                        detailColumns.add("AMT", DataValues.newString(amt));
//                        detailColumns.add("DISTRIPRICE", DataValues.newString(distriprice));
//                        detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt));
//                        detailColumns.add("BASEUNIT", DataValues.newString(baseunit));
//                        detailColumns.add("BASEQTY", DataValues.newString(baseQty));
//                        detailColumns.add("UNIT_RATIO", DataValues.newString(unit_ratio));
//                        detailColumns.add("BDATE", DataValues.newString(Create_DATE));
//
//                        detailColumns.add("OFNO", DataValues.newString(differenceNO));
//                        detailColumns.add("OTYPE", DataValues.newString("6"));
//                        detailColumns.add("OITEM", DataValues.newString(oItem));
//
//                        String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
//                        DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
//                        InsBean ib1 = new InsBean("DCP_STOCKIN_DETAIL", detailColumnNames);
//                        ib1.addValues(detailDataValues);
//                        this.addProcessData(new DataProcessBean(ib1));
//                    }
//                }
//
//
//                totCqtyc = new BigDecimal(pfcList.size());
//                totCqtyr = new BigDecimal(pfrList.size());
//                if (!Check.Null(ckNo)) {
//                    ColumnDataValue mainColumns = new ColumnDataValue();
//                    mainColumns.add("EID", DataValues.newString(eId));
//                    mainColumns.add("SHOPID", DataValues.newString(transferShop));
//                    mainColumns.add("ORGANIZATIONNO", DataValues.newString(transferShop));
//                    mainColumns.add("BDATE", DataValues.newString(Create_DATE));
//                    mainColumns.add("STOCKOUTNO", DataValues.newString(ckNo));
//
////                    mainColumns.add("DOC_TYPE", DataValues.newString("5"));  //doc_Type=5-配货出库
//                    mainColumns.add("DOC_TYPE", DataValues.newString("5"));  //doc_Type=5-配货出库
//
//                    mainColumns.add("RECEIPT_ORG", DataValues.newString(""));
//
//                    mainColumns.add("WAREHOUSE", DataValues.newString(differWarehouse));
//                    mainColumns.add("TRANSFER_SHOP", DataValues.newString(transferShop));
//                    mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(transferWarehouse));
//
//                    mainColumns.add("TOT_CQTY", DataValues.newString(totCqtyc));
//                    mainColumns.add("TOT_PQTY", DataValues.newString(totPqtyc));
//                    mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmtc));
//                    mainColumns.add("TOT_AMT", DataValues.newString(totAmtc));
//                    mainColumns.add("CREATEBY", DataValues.newString(employeeNo));
//                    mainColumns.add("CREATE_DATE", DataValues.newString(Create_DATE));
//                    mainColumns.add("CREATE_TIME", DataValues.newString(Create_TIME));
//                    mainColumns.add("STATUS", DataValues.newString("0"));
//                    mainColumns.add("PARTITION_DATE", DataValues.newString(Create_DATE));
//
//                    mainColumns.add("EMPLOYEEID", DataValues.newString(employeeNo));
//                    mainColumns.add("DEPARTID", DataValues.newString(departmentNo));
//                    mainColumns.add("RECEIPTDATE", DataValues.newString(Create_DATE));
//
//                    mainColumns.add("INVWAREHOUSE", DataValues.newString(oneData.get("INVWAREHOUSE")));
//
//                    mainColumns.add("OFNO", DataValues.newString(differenceNO));
//                    mainColumns.add("OTYPE", DataValues.newString("6"));
//
//
//                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT", mainColumns)));
//
//                }
//
//                if (!Check.Null(rkNo)) {
//                    ColumnDataValue mainColumns = new ColumnDataValue();
//                    mainColumns.add("EID", DataValues.newString(eId));
//                    mainColumns.add("SHOPID", DataValues.newString(transferShop));
//                    mainColumns.add("ORGANIZATIONNO", DataValues.newString(transferShop));
//                    mainColumns.add("BDATE", DataValues.newString(Create_DATE));
//                    mainColumns.add("STOCKINNO", DataValues.newString(rkNo));
//                    mainColumns.add("DOC_TYPE", DataValues.newString("5"));//doc_Type=5-配货收货
//
//                    mainColumns.add("WAREHOUSE", DataValues.newString(differWarehouse));
//                    mainColumns.add("TRANSFER_SHOP", DataValues.newString(""));
//                    mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(""));
//                    mainColumns.add("TOT_CQTY", DataValues.newString(totCqtyr));
//                    mainColumns.add("TOT_PQTY", DataValues.newString(totPqtyr));
//                    mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmtr));
//                    mainColumns.add("TOT_AMT", DataValues.newString(totAmtr));
//                    mainColumns.add("CREATEBY", DataValues.newString(employeeNo));
//                    mainColumns.add("CREATE_DATE", DataValues.newString(Create_DATE));
//                    mainColumns.add("CREATE_TIME", DataValues.newString(Create_TIME));
//                    mainColumns.add("STATUS", DataValues.newString("0"));
//
//                    mainColumns.add("EMPLOYEEID", DataValues.newString(employeeNo));
//                    mainColumns.add("DEPARTID", DataValues.newString(departmentNo));
//                    String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
//                    DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
//                    InsBean ib1 = new InsBean("DCP_STOCKIN", mainColumnNames);
//                    ib1.addValues(mainDataValues);
//                    this.addProcessData(new DataProcessBean(ib1));
//
//                    //自动审核
//                }
//
//            }
//
//
            orders = differenceAdjust(req);

            UptBean ub1 = new UptBean("DCP_DIFFERENCE");
            ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", new DataValue(Create_DATE, Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_TIME", new DataValue(Create_TIME, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_DATE", new DataValue(Create_DATE, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_TIME", new DataValue(Create_TIME, Types.VARCHAR));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("DIFFERENCENO", new DataValue(differenceNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
//
        }

        this.doExecuteDataToDB();

        for (Order order : orders) {
            if ("3".equals(order.getType()) || "4".equals(order.getType())) {
                ParseJson pj = new ParseJson();

                DCP_StockInProcessReq inReq = new DCP_StockInProcessReq();
                inReq.setServiceId("DCP_StockInProcess");
                inReq.setToken(req.getToken());

                DCP_StockInProcessReq.levelElm soRequest = inReq.new levelElm();
                soRequest.setStockInNo(order.getBillNo());
                soRequest.setDocType(order.getDocType());
                soRequest.setStatus("2");//确认
                soRequest.setOrgNo(order.getOrgNo()); //组织别切换
                inReq.setRequest(soRequest);

                String jsontemp = pj.beanToJson(inReq);

                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_StockInProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_StockInProcessRes>() {
                });
                if (resserver.isSuccess() == false) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单审核失败！");
                }
            } else {
                ParseJson pj = new ParseJson();

                DCP_StockOutProcessReq outReq = new DCP_StockOutProcessReq();
                outReq.setServiceId("DCP_StockOutProcess");
                outReq.setToken(req.getToken());

                DCP_StockOutProcessReq.levelElm soRequest = outReq.new levelElm();
                soRequest.setStockOutNo(order.getBillNo());
                soRequest.setDocType(order.getDocType());
                soRequest.setStatus("2");//确认
                soRequest.setOrgNo(order.getOrgNo()); //组织别切换
                outReq.setRequest(soRequest);

                String jsontemp = pj.beanToJson(outReq);

                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_StockOutProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_StockOutProcessRes>() {
                });
                if (resserver.isSuccess() == false) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "出库单审核失败！");
                }
            }

        }


//        if (!Check.Null(ckNo)) {
////自动审核
//            DCP_StockOutProcessReq outReq = new DCP_StockOutProcessReq();
//            outReq.setServiceId("DCP_StockOutProcess");
//            outReq.setToken(req.getToken());
//            DCP_StockOutProcessReq.levelElm soRequest = outReq.new levelElm();
//            soRequest.setStockOutNo(ckNo);
//            soRequest.setDocType("5");
//            soRequest.setStatus("2");//确认
//            outReq.setRequest(soRequest);
//
//            String jsontemp = pj.beanToJson(outReq);
//
//            DispatchService ds = DispatchService.getInstance();
//            String resXml = ds.callService(jsontemp, StaticInfo.dao);
//            DCP_StockOutProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_StockOutProcessRes>() {
//            });
//            if (resserver.isSuccess() == false) {
//                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "出库单审核失败！");
//            }
//        }
//        if (!Check.Null(rkNo)) {
//            //自动审核
//            DCP_StockInProcessReq inReq = new DCP_StockInProcessReq();
//            inReq.setServiceId("DCP_StockInProcess");
//            inReq.setToken(req.getToken());
//            DCP_StockInProcessReq.levelElm soRequest = inReq.new levelElm();
//            soRequest.setStockInNo(ckNo);
//            soRequest.setDocType("5");
//            soRequest.setStatus("2");//确认
//            inReq.setRequest(soRequest);
//
//            String jsontemp = pj.beanToJson(inReq);
//
//            DispatchService ds = DispatchService.getInstance();
//            String resXml = ds.callService(jsontemp, StaticInfo.dao);
//            DCP_StockInProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_StockInProcessRes>() {
//            });
//            if (resserver.isSuccess() == false) {
//                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单审核失败！");
//            }
//        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        //}
        //catch (Exception e) {
        //	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DifferenceProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DifferenceProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DifferenceProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DifferenceProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelElm request = req.getRequest();

        String DifferenceNO = request.getDifferenceNo();
        String Status = request.getStatus();
        if (Check.Null(DifferenceNO)) {
            isFail = true;
            errMsg.append("单据编号不可为空值, ");
        }

        if (Check.Null(Status)) {
            isFail = true;
            errMsg.append("状态不可为空值, ");
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_DifferenceProcessReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DifferenceProcessReq>() {
        };
    }

    @Override
    protected DCP_DifferenceProcessRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DifferenceProcessRes();
    }

    private List<Order> differenceAdjust(DCP_DifferenceProcessReq req) throws Exception {

        List<Order> rList = new ArrayList<>();

        String differenceNo = req.getRequest().getDifferenceNo();
        String eid = req.geteId();

        String querySql = " SELECT c.*,b.INVWAREHOUSE,b.TRANSFER_SHOP,b.TRANSFER_WAREHOUSE " +
                "    ,NVL(a.PQTY,0) INQTY,NVL(d.PQTY,0) OUTQTY,a.PLU_BARCODE " +
                "    ,f.DOC_TYPE INDOC_TYPE,e.DOC_TYPE OUTDOC_TYPE,a.ITEM INITEM,d.ITEM OUTITEM,a.BATCH_NO " +
                "    ,d.STOCKOUTNO,a.stockinno,a.ORGANIZATIONNO INORGANIZATIONNO,d.ORGANIZATIONNO OUTORGANIZATIONNO " +
                "    ,a.PROD_DATE,a.EXPDATE,a.MES_LOCATION,a.DISTRIPRICE INDISTRIPRICE,e.RECEIPT_ORG OUTRORG" +
                "    ,e.TRANSFER_SHOP OUTTRANSFER_SHOP,f.PTEMPLATENO INPTEMPLATENO,f.TRANSFER_WAREHOUSE INTRANSFER_WAREHOUSE" +
                "    ,f.TRANSFER_SHOP INTRANSFER_SHOP,f.PACKINGNO INPACKINGNO,f.LOAD_DOCNO INLOAD_DOCNO,e.TRANSFER_WAREHOUSE OUTTRANSFER_WAREHOUSE " +
                "    ,a.OOFNO INOOFNO,a.OOITEM INOOITEM,f.OOTYPE INOOTYPE,a.WAREHOUSE INWAREHOUSE,d.WAREHOUSE OUTWAREHOUSE " +
                "    ,d.BATCHNO OUTBATCHNO,d.LOCATION OUTLOCATION,d.PRODDATE OUTPRODDATE,d.EXPDATE OUTEXPDATE,e.PTEMPLATENO OUTPTEMPLATENO" +
                "    ,e.LOAD_DOCSHOP OUTLOAD_DOCSHOP,e.LOAD_DOCTYPE OUTLOAD_DOCTYPE,e.LOAD_DOCNO OUTLOAD_DOCNO " +
                "    FROM dcp_difference_detail c " +
                "    inner join dcp_difference b on c.eid=b.eid and c.ORGANIZATIONNO=b.ORGANIZATIONNO and c.DIFFERENCENO=b.DIFFERENCENO " +
                "    inner join dcp_stockin_detail a on a.eid=b.eid and a.stockinno = b.ofno and a.ITEM=c.OITEM " +
                "    inner join dcp_stockout_batch d on a.EID=d.eid and a.OOFNO=d.STOCKOUTNO and a.OOITEM=d.ITEM " +
                "    inner join dcp_stockout e on d.eid=e.eid and d.STOCKOUTNO=e.STOCKOUTNO and d.SHOPID=e.SHOPID and d.ORGANIZATIONNO=e.ORGANIZATIONNO " +
                "    inner join DCP_STOCKIN f on f.eid=a.eid and f.STOCKINNO=a.STOCKINNO and f.SHOPID=a.SHOPID and f.ORGANIZATIONNO=a.ORGANIZATIONNO " +
                "  where c.eid = '" + eid + "'" +
                "  and c.differenceno = '" + differenceNo + "' ";
        List<Map<String, Object>> stockList = this.executeQuerySQL_BindSQL(querySql, null);


        Map<String, List<Map<String, Object>>> ckList = new HashMap<>();
        Map<String, List<Map<String, Object>>> rkList = new HashMap<>();

        for (Map<String, Object> oneData : stockList) {

            BigDecimal inQty = new BigDecimal(oneData.get("INQTY").toString());  //收货量
            BigDecimal outQty = new BigDecimal(oneData.get("OUTQTY").toString()); //出货量
//            BigDecimal diffQty = new BigDecimal(oneData.get("REQ_QTY").toString()); //差异量 = 申请量
            BigDecimal diffQty = inQty.subtract(outQty); //差异量
            BigDecimal pQty = new BigDecimal(oneData.get("PQTY").toString()); //核准量
            BigDecimal inPQty = pQty.subtract(diffQty);    //入库调整量

            String orderType;

            if (pQty.compareTo(BigDecimal.ZERO) < 0) {
                orderType = "1";     //核准量负数出库红冲
            } else if (pQty.compareTo(BigDecimal.ZERO) > 0) {
                orderType = "2";     //核准量正数出库
            } else {
                orderType = "";
            }

            if (StringUtils.isNotEmpty(orderType)) {
                if (null != ckList.get(orderType)) {
                    ckList.get(orderType).add(oneData);
                } else {
                    ckList.put(orderType, new ArrayList<>());
                    ckList.get(orderType).add(oneData);
                }
            }

            if (inPQty.compareTo(BigDecimal.ZERO) < 0) {
                orderType = "3";     //核准量 - 差异量  负数入库红冲
            } else if (inPQty.compareTo(BigDecimal.ZERO) > 0) {
                orderType = "4";     //核准量 - 差异量  正数入库
            } else {
                orderType = "";
            }

            if (StringUtils.isNotEmpty(orderType)) {
                if (null != rkList.get(orderType)) {
                    rkList.get(orderType).add(oneData);
                } else {
                    rkList.put(orderType, new ArrayList<>());
                    rkList.get(orderType).add(oneData);
                }
            }

        }

        for (Map.Entry<String, List<Map<String, Object>>> entry : ckList.entrySet()) {
            Order order = insertStockOut(req, entry.getValue());
            if (null != order) {
                rList.add(order);  //添加出库
            }
        }

        for (Map.Entry<String, List<Map<String, Object>>> entry : rkList.entrySet()) {
            Order order = insertStockIn(req, entry.getValue());
            if (null != order) {
                rList.add(order); //添加入库
            }
        }

        insertAdjust(req, stockList);

        return rList;

        /*
         if (CollectionUtils.isNotEmpty(stockList)) {
         Map<String, Object> oneData = stockList.get(0);

         String differenceOrg = oneData.get("ORGANIZATIONNO").toString();
         String transferShop = oneData.get("TRANSFER_SHOP").toString();
         String transferWarehouse = oneData.get("TRANSFER_WAREHOUSE").toString();
         String differWarehouse = oneData.get("WAREHOUSE").toString();
         String stockOutDocType = oneData.get("OUTDOC_TYPE").toString();
         String stockInDocType = oneData.get("INDOC_TYPE").toString();
         String invWarehouse = oneData.get("INVWAREHOUSE").toString();
         String stockOutNo = oneData.get("STOCKOUTNO").toString();
         String stockInNo = oneData.get("STOCKINNO").toString();
         String inOrg = oneData.get("INORGANIZATIONNO").toString();
         String outOrg = oneData.get("OUTORGANIZATIONNO").toString();
         String outROrg = oneData.get("OUTRORG").toString();

         String ckNo = "";
         String rkNo = "";

         BigDecimal totPqtyc = new BigDecimal(0);
         BigDecimal totAmtc = new BigDecimal(0);
         BigDecimal totDistriAmtc = new BigDecimal(0);

         List<String> pfcList = new ArrayList<>();

         String orderType = "";

         //            String outBatchQuerySql = " SELECT * FROM DCP_STOCKOUT_BATCH WHERE EID='" + eid + "' AND STOCKOUTNO='" + stockOutNo + "'";
         //            List<Map<String, Object>> batchInfo = doQueryData(outBatchQuerySql, null);

         int item = 0;
         for (Map<String, Object> map : stockList) {
         ++item;

         BigDecimal pQty = new BigDecimal(map.get("PQTY").toString()); //核准量
         BigDecimal outQty = new BigDecimal(map.get("OUTQTY").toString());
         BigDecimal inQty = new BigDecimal(map.get("INQTY").toString());
         BigDecimal diffQty = new BigDecimal(map.get("REQ_QTY").toString()); //差异量

         String warehouse = map.get("WAREHOUSE").toString();

         String pluno = map.get("PLUNO").toString();
         String featureno = map.get("FEATURENO").toString();
         String location = StringUtils.toString(map.get("MES_LOCATION"), " ");
         String punit = map.get("PUNIT").toString();
         String oItem = map.get("ITEM").toString();
         String outItem = map.get("OUTITEM").toString();
         String inItem = map.get("INITEM").toString();

         String baseunit = map.get("BASEUNIT").toString();
         BigDecimal price = new BigDecimal(map.get("PRICE").toString());
         BigDecimal distriprice = new BigDecimal(map.get("DISTRIPRICE").toString());
         if (StringUtils.isEmpty(map.get("DISTRIPRICE").toString())) {
         distriprice = new BigDecimal(map.get("INDISTRIPRICE").toString());
         }

         BigDecimal amt = diffQty.multiply(price);
         BigDecimal distriAmt = diffQty.multiply(distriprice);
         BigDecimal unit_ratio = new BigDecimal(map.get("UNIT_RATIO").toString());
         BigDecimal baseQty = diffQty.multiply(unit_ratio);
         String pluBarCode = map.get("PLU_BARCODE").toString();
         String batchNo = StringUtils.toString(oneData.get("BATCH_NO"), " ");
         String prodDate = oneData.get("PROD_DATE").toString();
         String expDate = oneData.get("EXPDATE").toString();

         if (!pfcList.contains(pluno + featureno)) {
         pfcList.add(pluno + featureno);
         }

         if (outQty.compareTo(inQty) == 0) { //出库和入库一致
         if (StringUtils.isEmpty(ckNo)) {
         orderType = "3";

         ckNo = getStockOutNo(req, stockOutDocType, transferShop);
         Order ckOrder = new Order(orderType, transferShop, ckNo);
         rList.add(ckOrder);
         }

         if (StringUtils.isEmpty(rkNo)) {
         orderType = "4";

         rkNo = getStockInNo(req, stockInDocType, inOrg);
         Order ckOrder = new Order(orderType, inOrg, rkNo);
         rList.add(ckOrder);
         }

         if (diffQty.compareTo(BigDecimal.ZERO) < 0) {
         BigDecimal dir = new BigDecimal(-1);
         diffQty = diffQty.multiply(dir);
         amt = amt.multiply(dir);
         distriAmt = distriAmt.multiply(dir);
         baseQty = baseQty.multiply(dir);
         }


         ColumnDataValue dcp_stockout_detail = new ColumnDataValue();
         dcp_stockout_detail.add("EID", DataValues.newString(eid));
         dcp_stockout_detail.add("ORGANIZATIONNO", DataValues.newString(outOrg));
         dcp_stockout_detail.add("SHOPID", DataValues.newString(transferShop));
         dcp_stockout_detail.add("STOCKOUTNO", DataValues.newString(rList.get(0).getBillNo()));
         dcp_stockout_detail.add("ITEM", DataValues.newString(item));
         //                    dcp_stockout_detail.add("DOC_TYPE", DataValues.newString(stockOutDocType));

         dcp_stockout_detail.add("PLU_BARCODE", DataValues.newString(pluBarCode));
         dcp_stockout_detail.add("PLUNO", DataValues.newString(pluno));
         dcp_stockout_detail.add("FEATURENO", DataValues.newString(featureno));
         dcp_stockout_detail.add("WAREHOUSE", DataValues.newString(warehouse));
         dcp_stockout_detail.add("PUNIT", DataValues.newString(punit));
         dcp_stockout_detail.add("PQTY", DataValues.newString(diffQty.doubleValue()));
         dcp_stockout_detail.add("PRICE", DataValues.newString(price));
         dcp_stockout_detail.add("AMT", DataValues.newString(amt));
         dcp_stockout_detail.add("DISTRIPRICE", DataValues.newString(distriprice));
         dcp_stockout_detail.add("DISTRIAMT", DataValues.newString(distriAmt));
         dcp_stockout_detail.add("BASEUNIT", DataValues.newString(baseunit));
         dcp_stockout_detail.add("BASEQTY", DataValues.newString(baseQty));
         dcp_stockout_detail.add("UNIT_RATIO", DataValues.newString(unit_ratio));
         dcp_stockout_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         dcp_stockout_detail.add("PARTITION_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));

         //                    dcp_stockout_detail.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         //                    dcp_stockout_detail.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
         //                    dcp_stockout_detail.add("MEMO", DataValues.newString("差异申诉调整"));

         dcp_stockout_detail.add("OFNO", DataValues.newString(differenceNo));
         dcp_stockout_detail.add("OTYPE", DataValues.newString("7"));
         dcp_stockout_detail.add("OITEM", DataValues.newString(oItem));

         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_DETAIL", dcp_stockout_detail)));

         ColumnDataValue dcp_stockout_batch = new ColumnDataValue();
         dcp_stockout_batch.add("EID", DataValues.newString(eid));
         dcp_stockout_batch.add("ORGANIZATIONNO", DataValues.newString(outOrg));
         dcp_stockout_batch.add("SHOPID", DataValues.newString(transferShop));
         dcp_stockout_batch.add("STOCKOUTNO", DataValues.newString(ckNo));
         dcp_stockout_batch.add("ITEM", DataValues.newString(item));
         dcp_stockout_batch.add("ITEM2", DataValues.newString(item));
         dcp_stockout_batch.add("PLUNO", DataValues.newString(pluno));
         dcp_stockout_batch.add("FEATURENO", DataValues.newString(featureno));
         dcp_stockout_batch.add("WAREHOUSE", DataValues.newString(warehouse));
         dcp_stockout_batch.add("LOCATION", DataValues.newString(location));
         dcp_stockout_batch.add("BATCHNO", DataValues.newString(batchNo));
         dcp_stockout_batch.add("PRODDATE", DataValues.newString(prodDate));
         dcp_stockout_batch.add("EXPDATE", DataValues.newString(expDate));
         dcp_stockout_batch.add("PUNIT", DataValues.newString(punit));
         dcp_stockout_batch.add("PQTY", DataValues.newString(diffQty.doubleValue()));
         dcp_stockout_batch.add("BASEUNIT", DataValues.newString(baseunit));
         dcp_stockout_batch.add("BASEQTY", DataValues.newString(baseQty));
         dcp_stockout_batch.add("UNITRATIO", DataValues.newString(unit_ratio));
         dcp_stockout_batch.add("BATCHNO", DataValues.newString(batchNo));
         dcp_stockout_batch.add("PRODDATE", DataValues.newString(prodDate));
         dcp_stockout_batch.add("EXPDATE", DataValues.newString(expDate));

         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_stockout_batch", dcp_stockout_batch)));

         ColumnDataValue dcp_stockin_detail = new ColumnDataValue();
         dcp_stockin_detail.add("EID", DataValues.newString(eid));
         dcp_stockin_detail.add("ORGANIZATIONNO", DataValues.newString(inOrg));
         dcp_stockin_detail.add("SHOPID", DataValues.newString(transferShop));
         dcp_stockin_detail.add("STOCKINNO", DataValues.newString(rList.get(0).getBillNo()));
         dcp_stockin_detail.add("ITEM", DataValues.newString(item));
         //                    dcp_stockin_detail.add("DOC_TYPE", DataValues.newString(stockOutDocType));

         dcp_stockin_detail.add("PLU_BARCODE", DataValues.newString(pluBarCode));
         dcp_stockin_detail.add("PLUNO", DataValues.newString(pluno));
         dcp_stockin_detail.add("FEATURENO", DataValues.newString(featureno));
         dcp_stockin_detail.add("WAREHOUSE", DataValues.newString(warehouse));
         dcp_stockin_detail.add("PUNIT", DataValues.newString(punit));
         dcp_stockin_detail.add("PQTY", DataValues.newString(diffQty.doubleValue()));
         dcp_stockin_detail.add("PRICE", DataValues.newString(price));
         dcp_stockin_detail.add("AMT", DataValues.newString(amt));
         dcp_stockin_detail.add("DISTRIPRICE", DataValues.newString(distriprice));
         dcp_stockin_detail.add("DISTRIAMT", DataValues.newString(distriAmt));
         dcp_stockin_detail.add("BASEUNIT", DataValues.newString(baseunit));
         dcp_stockin_detail.add("BASEQTY", DataValues.newString(baseQty));
         dcp_stockin_detail.add("UNIT_RATIO", DataValues.newString(unit_ratio));
         dcp_stockin_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         dcp_stockin_detail.add("PARTITION_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         dcp_stockin_detail.add("TRANSFER_BATCHNO", DataValues.newString(map.get("TRANSFER_BATCHNO").toString()));

         //                    dcp_stockin_detail.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         //                    dcp_stockin_detail.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
         //                    dcp_stockin_detail.add("MEMO", DataValues.newString("差异申诉调整"));

         dcp_stockin_detail.add("OFNO", DataValues.newString(differenceNo));
         dcp_stockin_detail.add("OTYPE", DataValues.newString("7"));
         dcp_stockin_detail.add("OITEM", DataValues.newString(oItem));

         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN_DETAIL", dcp_stockin_detail)));

         } else {  //出库和入库不一致

         //生成出库配货出库红冲
         if (rList.isEmpty()) {
         if (pQty.compareTo(BigDecimal.ZERO) < 0) {    //配货少收
         orderType = "1";
         } else if (pQty.compareTo(BigDecimal.ZERO) > 0) {
         orderType = "2";    //配货多收
         } else {
         orderType = "5";
         if (diffQty.compareTo(BigDecimal.ZERO) < 0) {
         orderType = "4";
         }
         }

         if ("5".equals(orderType) || "4".equals(orderType)) {
         rkNo = getStockInNo(req, stockInDocType, transferShop);
         Order newOrder = new Order(orderType, transferShop, rkNo);
         rList.add(newOrder);
         } else {
         ckNo = getStockOutNo(req, stockOutDocType, transferShop);
         Order newOrder = new Order(orderType, transferShop, ckNo);
         rList.add(newOrder);
         }
         }

         if ("1".equals(stockOutDocType) || "3".equals(stockOutDocType) || "4".equals(stockOutDocType)) {
         if (diffQty.compareTo(BigDecimal.ZERO) > 0) {  //配货多收
         //判断是否有批号

         }

         }

         if (pQty.compareTo(BigDecimal.ZERO) == 0) { //差异核准数量为0


         if (pQty.subtract(diffQty).compareTo(BigDecimal.ZERO) != 0 && diffQty.compareTo(BigDecimal.ZERO) < 0) {
         if (StringUtils.isNotEmpty(ckNo)) {
         ckNo = getStockOutNo(req, stockOutDocType, transferShop);
         Order ckOrder = new Order(orderType, transferShop, ckNo);
         rList.add(ckOrder);
         }


         }

         diffQty = diffQty.multiply(BigDecimal.valueOf(-1));
         amt = diffQty.multiply(price);
         distriAmt = diffQty.multiply(distriprice);
         baseQty = diffQty.multiply(unit_ratio);

         ColumnDataValue dcp_stockin_detail = new ColumnDataValue();
         dcp_stockin_detail.add("EID", DataValues.newString(eid));
         dcp_stockin_detail.add("ORGANIZATIONNO", DataValues.newString(transferShop));
         dcp_stockin_detail.add("SHOPID", DataValues.newString(transferShop));
         dcp_stockin_detail.add("STOCKINNO", DataValues.newString(rList.get(0).getBillNo()));
         dcp_stockin_detail.add("ITEM", DataValues.newString(item));
         //                    dcp_stockin_detail.add("DOC_TYPE", DataValues.newString(stockOutDocType));

         dcp_stockin_detail.add("PLU_BARCODE", DataValues.newString(pluBarCode));
         dcp_stockin_detail.add("PLUNO", DataValues.newString(pluno));
         dcp_stockin_detail.add("FEATURENO", DataValues.newString(featureno));
         dcp_stockin_detail.add("WAREHOUSE", DataValues.newString(warehouse));
         dcp_stockin_detail.add("PUNIT", DataValues.newString(punit));
         dcp_stockin_detail.add("PQTY", DataValues.newString(diffQty.doubleValue()));
         dcp_stockin_detail.add("PRICE", DataValues.newString(price));
         dcp_stockin_detail.add("AMT", DataValues.newString(amt));
         dcp_stockin_detail.add("DISTRIPRICE", DataValues.newString(distriprice));
         dcp_stockin_detail.add("DISTRIAMT", DataValues.newString(distriAmt));
         dcp_stockin_detail.add("BASEUNIT", DataValues.newString(baseunit));
         dcp_stockin_detail.add("BASEQTY", DataValues.newString(baseQty));
         dcp_stockin_detail.add("UNIT_RATIO", DataValues.newString(unit_ratio));
         dcp_stockin_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         dcp_stockin_detail.add("PARTITION_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         dcp_stockin_detail.add("TRANSFER_BATCHNO", DataValues.newString(map.get("TRANSFER_BATCHNO").toString()));

         dcp_stockin_detail.add("OFNO", DataValues.newString(differenceNo));
         dcp_stockin_detail.add("OTYPE", DataValues.newString("7"));
         dcp_stockin_detail.add("OITEM", DataValues.newString(oItem));

         if ("5".equals(orderType)) {
         dcp_stockin_detail.add("PQTY_ORIGIN", DataValues.newString(inQty));
         dcp_stockin_detail.add("ITEM_ORIGIN", DataValues.newString(inItem));
         }


         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN_DETAIL", dcp_stockin_detail)));

         if ("5".equals(orderType)) {
         //回写原单
         ColumnDataValue oCondition = new ColumnDataValue();
         ColumnDataValue oValues = new ColumnDataValue();

         oCondition.add("EID", DataValues.newString(eid));
         oCondition.add("STOCKINNO", DataValues.newString(stockInNo));
         oCondition.add("ITEM", DataValues.newString(inItem));

         oValues.add("PQTY_REFUND", DataValues.newString(diffQty.doubleValue()));

         this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKIN_DETAIL", oCondition, oValues)));

         }


         } else {
         ColumnDataValue dcp_stockout_detail = new ColumnDataValue();

         dcp_stockout_detail.add("EID", DataValues.newString(eid));
         dcp_stockout_detail.add("ORGANIZATIONNO", DataValues.newString(transferShop));
         dcp_stockout_detail.add("SHOPID", DataValues.newString(transferShop));
         dcp_stockout_detail.add("STOCKOUTNO", DataValues.newString(ckNo));
         dcp_stockout_detail.add("ITEM", DataValues.newString(item));
         //                    dcp_stockout_detail.add("DOC_TYPE", DataValues.newString(stockOutDocType));

         dcp_stockout_detail.add("PLU_BARCODE", DataValues.newString(pluBarCode));
         dcp_stockout_detail.add("PLUNO", DataValues.newString(pluno));
         dcp_stockout_detail.add("FEATURENO", DataValues.newString(featureno));
         dcp_stockout_detail.add("WAREHOUSE", DataValues.newString(warehouse));
         dcp_stockout_detail.add("PUNIT", DataValues.newString(punit));
         dcp_stockout_detail.add("PQTY", DataValues.newString(diffQty.doubleValue()));
         dcp_stockout_detail.add("PRICE", DataValues.newString(price));
         dcp_stockout_detail.add("AMT", DataValues.newString(amt));
         dcp_stockout_detail.add("DISTRIPRICE", DataValues.newString(distriprice));
         dcp_stockout_detail.add("DISTRIAMT", DataValues.newString(distriAmt));
         dcp_stockout_detail.add("BASEUNIT", DataValues.newString(baseunit));
         dcp_stockout_detail.add("BASEQTY", DataValues.newString(baseQty));
         dcp_stockout_detail.add("UNIT_RATIO", DataValues.newString(unit_ratio));
         dcp_stockout_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         dcp_stockout_detail.add("PARTITION_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         dcp_stockout_detail.add("TRANSFER_BATCHNO", DataValues.newString(map.get("TRANSFER_BATCHNO").toString()));
         dcp_stockout_detail.add("BATCH_NO", DataValues.newString(batchNo));
         dcp_stockout_detail.add("PROD_DATE", DataValues.newString(prodDate));
         dcp_stockout_detail.add("EXPDATE", DataValues.newString(expDate));

         //                    dcp_stockout_detail.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         //                    dcp_stockout_detail.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
         //                    dcp_stockout_detail.add("MEMO", DataValues.newString("差异申诉调整"));

         dcp_stockout_detail.add("OFNO", DataValues.newString(differenceNo));
         dcp_stockout_detail.add("OTYPE", DataValues.newString("7"));
         dcp_stockout_detail.add("OITEM", DataValues.newString(oItem));
         if ("1".equals(orderType)) {
         dcp_stockout_detail.add("PQTY_ORIGIN", DataValues.newString(outQty));
         dcp_stockout_detail.add("ITEM_ORIGIN", DataValues.newString(outItem));

         //回写原单
         ColumnDataValue oCondition = new ColumnDataValue();
         ColumnDataValue oValues = new ColumnDataValue();

         oCondition.add("EID", DataValues.newString(eid));
         oCondition.add("STOCKOUTNO", DataValues.newString(stockOutNo));
         oCondition.add("ITEM", DataValues.newString(outItem));

         oValues.add("PQTY_REFUND", DataValues.newString(diffQty.doubleValue()));

         this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT_DETAIL", oCondition, oValues)));

         }
         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_DETAIL", dcp_stockout_detail)));

         ColumnDataValue dcp_stockout_batch = new ColumnDataValue();

         dcp_stockout_batch.add("EID", DataValues.newString(eid));
         dcp_stockout_batch.add("ORGANIZATIONNO", DataValues.newString(transferShop));
         dcp_stockout_batch.add("SHOPID", DataValues.newString(transferShop));
         dcp_stockout_batch.add("STOCKOUTNO", DataValues.newString(ckNo));
         dcp_stockout_batch.add("ITEM", DataValues.newString(item));
         dcp_stockout_batch.add("ITEM2", DataValues.newString(item));
         dcp_stockout_batch.add("PLUNO", DataValues.newString(pluno));
         dcp_stockout_batch.add("FEATURENO", DataValues.newString(featureno));
         dcp_stockout_batch.add("WAREHOUSE", DataValues.newString(warehouse));
         dcp_stockout_batch.add("LOCATION", DataValues.newString(location));
         dcp_stockout_batch.add("BATCHNO", DataValues.newString(batchNo));
         dcp_stockout_batch.add("PRODDATE", DataValues.newString(prodDate));
         dcp_stockout_batch.add("EXPDATE", DataValues.newString(expDate));
         dcp_stockout_batch.add("PUNIT", DataValues.newString(punit));
         dcp_stockout_batch.add("PQTY", DataValues.newString(diffQty.doubleValue()));
         dcp_stockout_batch.add("BASEUNIT", DataValues.newString(baseunit));
         dcp_stockout_batch.add("BASEQTY", DataValues.newString(baseQty));
         dcp_stockout_batch.add("UNITRATIO", DataValues.newString(unit_ratio));

         if ("1".equals(orderType)) {
         dcp_stockout_batch.add("PQTY_ORIGIN", DataValues.newString(outQty));
         dcp_stockout_batch.add("ITEM_ORIGIN", DataValues.newString(outItem));

         //回写原单
         ColumnDataValue oCondition = new ColumnDataValue();
         ColumnDataValue oValues = new ColumnDataValue();

         oCondition.add("EID", DataValues.newString(eid));
         oCondition.add("STOCKOUTNO", DataValues.newString(stockOutNo));
         oCondition.add("ITEM2", DataValues.newString(outItem));

         oValues.add("PQTY_REFUND", DataValues.newString(diffQty.doubleValue()));

         this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT_BATCH", oCondition, oValues)));

         }
         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_BATCH", dcp_stockout_batch)));

         }

         }
         totPqtyc = totPqtyc.add(diffQty);
         totAmtc = totAmtc.add(amt);
         totDistriAmtc = totDistriAmtc.add(distriAmt);
         }

         double totCqtyc = pfcList.size();

         if (CollectionUtils.isNotEmpty(rList)) {
         for (Order order : rList) {
         ColumnDataValue mainColumns = new ColumnDataValue();
         if (!"4".equals(order.getType()) && !"5".equals(order.getType())) {  //出库

         mainColumns.add("EID", DataValues.newString(eid));
         mainColumns.add("SHOPID", DataValues.newString(order.getOrgNo()));
         mainColumns.add("ORGANIZATIONNO", DataValues.newString(order.getOrgNo()));
         mainColumns.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         mainColumns.add("STOCKOUTNO", DataValues.newString(order.getBillNo()));

         mainColumns.add("DOC_TYPE", DataValues.newString(stockOutDocType));

         mainColumns.add("RECEIPT_ORG", DataValues.newString(outROrg));

         mainColumns.add("WAREHOUSE", DataValues.newString(differWarehouse));
         mainColumns.add("TRANSFER_SHOP", DataValues.newString(transferShop));
         mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(transferWarehouse));

         mainColumns.add("TOT_CQTY", DataValues.newString(totCqtyc));
         mainColumns.add("TOT_PQTY", DataValues.newString(totPqtyc));
         mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmtc));
         mainColumns.add("TOT_AMT", DataValues.newString(totAmtc));

         mainColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
         mainColumns.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         mainColumns.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
         mainColumns.add("STATUS", DataValues.newString("0"));

         mainColumns.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
         mainColumns.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
         mainColumns.add("INVWAREHOUSE", DataValues.newString(invWarehouse));

         mainColumns.add("OFNO", DataValues.newString(differenceNo));
         mainColumns.add("OTYPE", DataValues.newString("7"));

         mainColumns.add("MEMO", DataValues.newString("差异申诉调整"));

         } else { //入库
         mainColumns.add("EID", DataValues.newString(eid));
         mainColumns.add("SHOPID", DataValues.newString(order.getOrgNo()));
         mainColumns.add("ORGANIZATIONNO", DataValues.newString(order.getOrgNo()));
         mainColumns.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         mainColumns.add("STOCKINNO", DataValues.newString(order.getBillNo()));

         mainColumns.add("DOC_TYPE", DataValues.newString(stockInDocType));


         mainColumns.add("TRANSFER_SHOP", DataValues.newString(transferShop));
         mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(transferWarehouse));

         mainColumns.add("TOT_CQTY", DataValues.newString(totCqtyc));
         mainColumns.add("TOT_PQTY", DataValues.newString(totPqtyc));
         mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmtc));
         mainColumns.add("TOT_AMT", DataValues.newString(totAmtc));

         mainColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
         mainColumns.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
         mainColumns.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
         mainColumns.add("STATUS", DataValues.newString("0"));

         mainColumns.add("PACKINGNO", DataValues.newString(oneData.get("INPACKINGNO").toString()));
         mainColumns.add("LOAD_DOCNO", DataValues.newString(oneData.get("INLOAD_DOCNO").toString()));
         mainColumns.add("PTEMPLATENO", DataValues.newString(oneData.get("INPTEMPLATENO").toString()));
         mainColumns.add("WAREHOUSE", DataValues.newString(differWarehouse));

         mainColumns.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
         mainColumns.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
         mainColumns.add("INVWAREHOUSE", DataValues.newString(invWarehouse));

         mainColumns.add("OFNO", DataValues.newString(differenceNo));
         mainColumns.add("OTYPE", DataValues.newString("7"));

         mainColumns.add("MEMO", DataValues.newString("差异申诉调整"));

         }


         if ("1".equals(order.getType())) {  //配货出库红冲
         mainColumns.add("STOCKOUTNO_ORIGIN", DataValues.newString(stockOutNo));
         }

         if ("5".equals(order.getType())) {  //配货出库红冲
         mainColumns.add("STOCKINNO_ORIGIN", DataValues.newString(stockInNo));
         }

         if (!"4".equals(order.getType()) && !"5".equals(order.getType())) {
         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT", mainColumns)));
         } else {
         this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN", mainColumns)));
         }

         if ("1".equals(order.getType())) {  //配货出库红冲

         //回写原单
         ColumnDataValue condition = new ColumnDataValue();
         ColumnDataValue values = new ColumnDataValue();

         condition.add("EID", DataValues.newString(eid));
         condition.add("STOCKOUTNO", DataValues.newString(stockOutNo));

         values.add("STOCKOUTNO_REFUND", DataValues.newString(order.getBillNo()));

         this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT", condition, values)));

         }

         if ("5".equals(order.getType())) {  //配货出库红冲
         //回写原单
         ColumnDataValue condition = new ColumnDataValue();
         ColumnDataValue values = new ColumnDataValue();

         condition.add("EID", DataValues.newString(eid));
         condition.add("STOCKINNO", DataValues.newString(stockInNo));

         values.add("STOCKINNO_REFUND", DataValues.newString(order.getBillNo()));

         this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKIN", condition, values)));

         }
         }
         }
         }

         return rList;
         **/
    }


    private Order insertStockOut(DCP_DifferenceProcessReq req, List<Map<String, Object>> stockList) throws Exception {

        if (CollectionUtils.isEmpty(stockList)) {
            return null;
        }

        Order outOrder = new Order();

        String differenceNo = req.getRequest().getDifferenceNo();
        String eid = req.geteId();

        Map<String, Object> oneData = stockList.get(0);
        BigDecimal pQty = new BigDecimal(oneData.get("PQTY").toString()); //核准量
        BigDecimal outQty = new BigDecimal(oneData.get("OUTQTY").toString());
        BigDecimal inQty = new BigDecimal(oneData.get("INQTY").toString());
        BigDecimal diffQty = inQty.subtract(outQty); //差异量

        String differenceOrg = oneData.get("ORGANIZATIONNO").toString();
        String transferShop = oneData.get("TRANSFER_SHOP").toString();
        String transferWarehouse = oneData.get("TRANSFER_WAREHOUSE").toString();
        String outTransferWarehouse = oneData.get("OUTTRANSFER_WAREHOUSE").toString();

        String differWarehouse = oneData.get("WAREHOUSE").toString();
        String stockOutDocType = oneData.get("OUTDOC_TYPE").toString();
        String stockInDocType = oneData.get("INDOC_TYPE").toString();
        String invWarehouse = oneData.get("INVWAREHOUSE").toString();
        String stockOutNo = oneData.get("STOCKOUTNO").toString();
        String stockInNo = oneData.get("STOCKINNO").toString();
        String inOrg = oneData.get("INORGANIZATIONNO").toString();
        String outOrg = oneData.get("OUTORGANIZATIONNO").toString();
        String outROrg = oneData.get("OUTRORG").toString();

        String orderType = "";
        String warehouse = differWarehouse;

        String orgNo = outOrg;
        if (pQty.compareTo(BigDecimal.ZERO) < 0) {
            orderType = "1";  //红冲
        } else if (pQty.compareTo(BigDecimal.ZERO) > 0) {
            orderType = "2";  //出库
        } else {
            orderType = "";
        }

        if (StringUtils.isEmpty(orderType)) {
            return null;
        }

        outOrder.setDocType(stockOutDocType);
        outOrder.setType(orderType);
        outOrder.setOrgNo(orgNo);
        outOrder.setBillNo(getStockOutNo(req, stockOutDocType, orgNo));

        BigDecimal totPqtyc = new BigDecimal(0);
        BigDecimal totAmtc = new BigDecimal(0);
        BigDecimal totDistriAmtc = new BigDecimal(0);

        List<String> pfcList = new ArrayList<>();

        int item = 0;
        for (Map<String, Object> map : stockList) {
            ++item;

            pQty = new BigDecimal(map.get("PQTY").toString()); //核准量
            outQty = new BigDecimal(map.get("OUTQTY").toString());
            inQty = new BigDecimal(map.get("INQTY").toString());
            diffQty = inQty.subtract(outQty); //差异量

            warehouse = map.get("OUTWAREHOUSE").toString();
            if (inOrg.equals(outOrder.getOrgNo())) {
                warehouse = map.get("INWAREHOUSE").toString();
            }

            String pluno = map.get("PLUNO").toString();
            String featureno = map.get("FEATURENO").toString();
            String location = StringUtils.toString(map.get("OUTLOCATION"), " ");
            String punit = map.get("PUNIT").toString();
            String oItem = map.get("ITEM").toString();
            String outItem = map.get("OUTITEM").toString();

            String baseunit = map.get("BASEUNIT").toString();
            BigDecimal price = new BigDecimal(map.get("PRICE").toString());
            BigDecimal distriprice = new BigDecimal(map.get("DISTRIPRICE").toString());
            if (StringUtils.isEmpty(map.get("DISTRIPRICE").toString())) {
                distriprice = new BigDecimal(map.get("INDISTRIPRICE").toString());
            }

            BigDecimal unit_ratio = new BigDecimal(map.get("UNIT_RATIO").toString());
            BigDecimal baseQty;
            BigDecimal amt;
            BigDecimal distriAmt;

            String pluBarCode = map.get("PLU_BARCODE").toString();
            String batchNo = StringUtils.toString(oneData.get("BATCH_NO"), " ");
            String outBatchNo = StringUtils.toString(oneData.get("OUTBATCHNO"), " ");
            String prodDate = oneData.get("OUTPRODDATE").toString();
            String expDate = oneData.get("OUTEXPDATE").toString();

            if (!pfcList.contains(pluno + featureno)) {
                pfcList.add(pluno + featureno);
            }
            BigDecimal nowQty = pQty;

            baseQty = nowQty.multiply(unit_ratio);
            amt = nowQty.multiply(price);
            distriAmt = nowQty.multiply(distriprice);

            ColumnDataValue dcp_stockout_detail = new ColumnDataValue();
            dcp_stockout_detail.add("EID", DataValues.newString(eid));
            dcp_stockout_detail.add("ORGANIZATIONNO", DataValues.newString(orgNo));
            dcp_stockout_detail.add("SHOPID", DataValues.newString(orgNo));
            dcp_stockout_detail.add("STOCKOUTNO", DataValues.newString(outOrder.getBillNo()));
            dcp_stockout_detail.add("ITEM", DataValues.newString(item));

            dcp_stockout_detail.add("PLU_BARCODE", DataValues.newString(pluBarCode));
            dcp_stockout_detail.add("PLUNO", DataValues.newString(pluno));
            dcp_stockout_detail.add("FEATURENO", DataValues.newString(featureno));
            dcp_stockout_detail.add("WAREHOUSE", DataValues.newString(warehouse));
            dcp_stockout_detail.add("PUNIT", DataValues.newString(punit));
            dcp_stockout_detail.add("PQTY", DataValues.newString(nowQty.doubleValue()));
            dcp_stockout_detail.add("PRICE", DataValues.newString(price));
            dcp_stockout_detail.add("AMT", DataValues.newString(amt));
            dcp_stockout_detail.add("DISTRIPRICE", DataValues.newString(distriprice));
            dcp_stockout_detail.add("DISTRIAMT", DataValues.newString(distriAmt));
            dcp_stockout_detail.add("BASEUNIT", DataValues.newString(baseunit));
            dcp_stockout_detail.add("BASEQTY", DataValues.newString(baseQty));
            dcp_stockout_detail.add("UNIT_RATIO", DataValues.newString(unit_ratio));
//            dcp_stockout_detail.add("BATCH_NO", DataValues.newString(batchNo));
            dcp_stockout_detail.add("BATCH_NO", DataValues.newString(outBatchNo));

            dcp_stockout_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_stockout_detail.add("PARTITION_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));

            dcp_stockout_detail.add("OFNO", DataValues.newString(differenceNo));
            dcp_stockout_detail.add("OTYPE", DataValues.newString("7"));
            dcp_stockout_detail.add("OITEM", DataValues.newString(oItem));

            if ("1".equals(orderType)) {
                dcp_stockout_detail.add("PQTY_ORIGIN", DataValues.newString(outQty));
                dcp_stockout_detail.add("ITEM_ORIGIN", DataValues.newString(outItem));

                //回写原单
                ColumnDataValue oCondition = new ColumnDataValue();
                ColumnDataValue oValues = new ColumnDataValue();

                oCondition.add("EID", DataValues.newString(eid));
                oCondition.add("STOCKOUTNO", DataValues.newString(stockOutNo));
                oCondition.add("ITEM", DataValues.newString(outItem));

                oValues.add("PQTY_REFUND", DataValues.newString(nowQty.doubleValue()));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT_DETAIL", oCondition, oValues)));

            }
            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_DETAIL", dcp_stockout_detail)));

            ColumnDataValue dcp_stockout_batch = new ColumnDataValue();

            dcp_stockout_batch.add("EID", DataValues.newString(eid));
            dcp_stockout_batch.add("ORGANIZATIONNO", DataValues.newString(orgNo));
            dcp_stockout_batch.add("SHOPID", DataValues.newString(orgNo));
            dcp_stockout_batch.add("STOCKOUTNO", DataValues.newString(outOrder.getBillNo()));
            dcp_stockout_batch.add("ITEM", DataValues.newString(item));
            dcp_stockout_batch.add("ITEM2", DataValues.newString(item));
            dcp_stockout_batch.add("PLUNO", DataValues.newString(pluno));
            dcp_stockout_batch.add("FEATURENO", DataValues.newString(featureno));
            dcp_stockout_batch.add("WAREHOUSE", DataValues.newString(warehouse));
            dcp_stockout_batch.add("LOCATION", DataValues.newString(location));
            dcp_stockout_batch.add("BATCHNO", DataValues.newString(outBatchNo));
            dcp_stockout_batch.add("PRODDATE", DataValues.newString(prodDate));
            dcp_stockout_batch.add("EXPDATE", DataValues.newString(expDate));
            dcp_stockout_batch.add("PUNIT", DataValues.newString(punit));
            dcp_stockout_batch.add("PQTY", DataValues.newString(nowQty.doubleValue()));
            dcp_stockout_batch.add("BASEUNIT", DataValues.newString(baseunit));
            dcp_stockout_batch.add("BASEQTY", DataValues.newString(baseQty));
            dcp_stockout_batch.add("UNITRATIO", DataValues.newString(unit_ratio));

            if ("1".equals(orderType)) {
                dcp_stockout_batch.add("PQTY_ORIGIN", DataValues.newString(outQty));
                dcp_stockout_batch.add("ITEM_ORIGIN", DataValues.newString(outItem));

                //回写原单
                ColumnDataValue oCondition = new ColumnDataValue();
                ColumnDataValue oValues = new ColumnDataValue();

                oCondition.add("EID", DataValues.newString(eid));
                oCondition.add("STOCKOUTNO", DataValues.newString(stockOutNo));
                oCondition.add("ITEM2", DataValues.newString(outItem));

                oValues.add("PQTY_REFUND", DataValues.newString(nowQty.doubleValue()));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT_BATCH", oCondition, oValues)));

            }
            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT_BATCH", dcp_stockout_batch)));


            totPqtyc = totPqtyc.add(nowQty);
            totAmtc = totAmtc.add(amt);
            totDistriAmtc = totDistriAmtc.add(distriAmt);
        }

        ColumnDataValue mainColumns = new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eid));
        mainColumns.add("SHOPID", DataValues.newString(outOrder.getOrgNo()));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(outOrder.getOrgNo()));
        mainColumns.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        mainColumns.add("STOCKOUTNO", DataValues.newString(outOrder.getBillNo()));

        mainColumns.add("DOC_TYPE", DataValues.newString(stockOutDocType));

        mainColumns.add("RECEIPT_ORG", DataValues.newString(outROrg));
        mainColumns.add("PTEMPLATENO", DataValues.newString(oneData.get("OUTPTEMPLATENO").toString()));
        mainColumns.add("LOAD_DOCSHOP", DataValues.newString(oneData.get("OUTLOAD_DOCSHOP").toString()));
        mainColumns.add("LOAD_DOCNO", DataValues.newString(oneData.get("OUTLOAD_DOCNO").toString()));
        mainColumns.add("LOAD_DOCTYPE", DataValues.newString(oneData.get("OUTLOAD_DOCTYPE").toString()));

        mainColumns.add("WAREHOUSE", DataValues.newString(warehouse));
        mainColumns.add("TRANSFER_SHOP", DataValues.newString(transferShop));
        mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(outTransferWarehouse));

        mainColumns.add("TOT_CQTY", DataValues.newString(pfcList.size()));
        mainColumns.add("TOT_PQTY", DataValues.newString(totPqtyc));
        mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmtc));
        mainColumns.add("TOT_AMT", DataValues.newString(totAmtc));

        mainColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
        mainColumns.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        mainColumns.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
        mainColumns.add("STATUS", DataValues.newString("0"));

        mainColumns.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
        mainColumns.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
        mainColumns.add("INVWAREHOUSE", DataValues.newString(invWarehouse));

        mainColumns.add("OFNO", DataValues.newString(differenceNo));
        mainColumns.add("OTYPE", DataValues.newString("7"));

        mainColumns.add("MEMO", DataValues.newString("差异申诉调整"));

        if ("1".equals(outOrder.getType())) {  //配货出库红冲
            mainColumns.add("STOCKOUTNO_ORIGIN", DataValues.newString(stockOutNo));

            //回写原单
            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue values = new ColumnDataValue();

            condition.add("EID", DataValues.newString(eid));
            condition.add("STOCKOUTNO", DataValues.newString(stockOutNo));

            values.add("STOCKOUTNO_REFUND", DataValues.newString(outOrder.getBillNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKOUT", condition, values)));

        }

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKOUT", mainColumns)));

        return outOrder;
    }


    private Order insertStockIn(DCP_DifferenceProcessReq req, List<Map<String, Object>> stockList) throws Exception {
        if (CollectionUtils.isEmpty(stockList)) {
            return null;
        }

        Order inOrder = new Order();
        String differenceNo = req.getRequest().getDifferenceNo();
        String eid = req.geteId();

        Map<String, Object> oneData = stockList.get(0);

        BigDecimal outQty = new BigDecimal(oneData.get("OUTQTY").toString());
        BigDecimal inQty = new BigDecimal(oneData.get("INQTY").toString());
        BigDecimal diffQty = inQty.subtract(outQty); //差异量

//        BigDecimal diffQty = new BigDecimal(oneData.get("REQ_QTY").toString()); //差异量 = 申请量
        BigDecimal pQty = new BigDecimal(oneData.get("PQTY").toString()); //核准量
        BigDecimal inPQty = pQty.subtract(diffQty);    //入库调整量


        String transferShop = oneData.get("TRANSFER_SHOP").toString();
        String transferWarehouse = oneData.get("TRANSFER_WAREHOUSE").toString();
        String inTransferWarehouse = oneData.get("INTRANSFER_WAREHOUSE").toString();
        String differWarehouse = oneData.get("WAREHOUSE").toString();
        String stockOutDocType = oneData.get("OUTDOC_TYPE").toString();
        String stockInDocType = oneData.get("INDOC_TYPE").toString();
        String invWarehouse = oneData.get("INVWAREHOUSE").toString();
        String stockOutNo = oneData.get("STOCKOUTNO").toString();
        String stockInNo = oneData.get("STOCKINNO").toString();
        String inOrg = oneData.get("INORGANIZATIONNO").toString();
        String outOrg = oneData.get("OUTORGANIZATIONNO").toString();
        String outROrg = oneData.get("OUTRORG").toString();

        String orgNo = transferShop;
        String orderType = "";

        if (inPQty.compareTo(BigDecimal.ZERO) < 0) {
            orderType = "3";      //红冲单据
        } else if (inPQty.compareTo(BigDecimal.ZERO) > 0) {
            orderType = "4";
        } else {
            orderType = "";
        }
        orgNo = inOrg;


        if (StringUtils.isEmpty(orderType)) {
            return null;
        }

        inOrder.setDocType(stockInDocType);
        inOrder.setType(orderType);
        inOrder.setOrgNo(orgNo);
        inOrder.setBillNo(getStockInNo(req, stockInDocType, orgNo));

        BigDecimal totPqtyc = new BigDecimal(0);
        BigDecimal totAmtc = new BigDecimal(0);
        BigDecimal totDistriAmtc = new BigDecimal(0);

        List<String> pfcList = new ArrayList<>();
        String warehouse = differWarehouse;
        int item = 0;
        for (Map<String, Object> map : stockList) {
            ++item;

            pQty = new BigDecimal(map.get("PQTY").toString()); //核准量
            outQty = new BigDecimal(map.get("OUTQTY").toString());
            inQty = new BigDecimal(map.get("INQTY").toString());
            diffQty = inQty.subtract(outQty); //差异量

            inPQty = pQty.subtract(diffQty);    //入库调整量

            warehouse = map.get("OUTWAREHOUSE").toString();
            if (inOrg.equals(inOrder.getOrgNo())) {
                warehouse = map.get("INWAREHOUSE").toString();
            }

            String pluno = map.get("PLUNO").toString();
            String featureno = map.get("FEATURENO").toString();
            String location = StringUtils.toString(map.get("MES_LOCATION"), " ");
            String punit = map.get("PUNIT").toString();
            String oItem = map.get("ITEM").toString();

            String inItem = map.get("INITEM").toString();
            inQty = new BigDecimal(map.get("INQTY").toString());
            String inWarehouse = map.get("INWAREHOUSE").toString();

            String baseunit = map.get("BASEUNIT").toString();
            BigDecimal price = new BigDecimal(map.get("PRICE").toString());
            BigDecimal distriprice = new BigDecimal(StringUtils.toString(map.get("DISTRIPRICE"), "0"));
            if (StringUtils.isEmpty(map.get("DISTRIPRICE").toString())) {
                distriprice = new BigDecimal(map.get("INDISTRIPRICE").toString());
            }

            BigDecimal unit_ratio = new BigDecimal(map.get("UNIT_RATIO").toString());
            BigDecimal baseQty;
            BigDecimal amt;
            BigDecimal distriAmt;

            String pluBarCode = map.get("PLU_BARCODE").toString();
            String batchNo = StringUtils.toString(oneData.get("BATCH_NO"), " ");
            String prodDate = map.get("PROD_DATE").toString();
            String expDate = map.get("EXPDATE").toString();

            if (!pfcList.contains(pluno + featureno)) {
                pfcList.add(pluno + featureno);
            }
            BigDecimal nowQty;
            nowQty = inPQty;

            baseQty = nowQty.multiply(unit_ratio);
            amt = nowQty.multiply(price);
            distriAmt = nowQty.multiply(distriprice);

            ColumnDataValue dcp_stockin_detail = new ColumnDataValue();
            dcp_stockin_detail.add("EID", DataValues.newString(eid));
            dcp_stockin_detail.add("ORGANIZATIONNO", DataValues.newString(inOrder.getOrgNo()));
            dcp_stockin_detail.add("SHOPID", DataValues.newString(inOrder.getOrgNo()));
            dcp_stockin_detail.add("STOCKINNO", DataValues.newString(inOrder.getBillNo()));
            dcp_stockin_detail.add("ITEM", DataValues.newString(item));

            dcp_stockin_detail.add("PLU_BARCODE", DataValues.newString(pluBarCode));
            dcp_stockin_detail.add("PLUNO", DataValues.newString(pluno));
            dcp_stockin_detail.add("FEATURENO", DataValues.newString(featureno));
            dcp_stockin_detail.add("WAREHOUSE", DataValues.newString(warehouse));
            dcp_stockin_detail.add("PUNIT", DataValues.newString(punit));
            dcp_stockin_detail.add("PQTY", DataValues.newString(nowQty.doubleValue()));
            dcp_stockin_detail.add("PRICE", DataValues.newString(price));
            dcp_stockin_detail.add("AMT", DataValues.newString(amt));
            dcp_stockin_detail.add("DISTRIPRICE", DataValues.newString(distriprice));
            dcp_stockin_detail.add("DISTRIAMT", DataValues.newString(distriAmt));
            dcp_stockin_detail.add("BASEUNIT", DataValues.newString(baseunit));
            dcp_stockin_detail.add("BASEQTY", DataValues.newString(baseQty));
            dcp_stockin_detail.add("UNIT_RATIO", DataValues.newString(unit_ratio));
            dcp_stockin_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_stockin_detail.add("PARTITION_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_stockin_detail.add("TRANSFER_BATCHNO", DataValues.newString(map.get("TRANSFER_BATCHNO").toString()));
            dcp_stockin_detail.add("MES_LOCATION", DataValues.newString(location));
            dcp_stockin_detail.add("BATCH_NO", DataValues.newString(batchNo));
            dcp_stockin_detail.add("PROD_DATE", DataValues.newString(prodDate));
            dcp_stockin_detail.add("EXPDATE", DataValues.newString(expDate));

            dcp_stockin_detail.add("OFNO", DataValues.newString(differenceNo));
            dcp_stockin_detail.add("OTYPE", DataValues.newString("7"));
            dcp_stockin_detail.add("OITEM", DataValues.newString(oItem));

            dcp_stockin_detail.add("OOFNO", DataValues.newString(map.get("INOOFNO").toString()));
            dcp_stockin_detail.add("OOITEM", DataValues.newString(map.get("INOOITEM").toString()));
            dcp_stockin_detail.add("OOTYPE", DataValues.newString(map.get("INOOTYPE").toString()));

            if ("3".equals(orderType)) {
                dcp_stockin_detail.add("PQTY_ORIGIN", DataValues.newString(inQty));
                dcp_stockin_detail.add("ITEM_ORIGIN", DataValues.newString(inItem));
            }

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN_DETAIL", dcp_stockin_detail)));

            if ("3".equals(orderType)) {
                //回写原单
                ColumnDataValue oCondition = new ColumnDataValue();
                ColumnDataValue oValues = new ColumnDataValue();

                oCondition.add("EID", DataValues.newString(eid));
                oCondition.add("STOCKINNO", DataValues.newString(stockInNo));
                oCondition.add("ITEM", DataValues.newString(inItem));

                oValues.add("PQTY_REFUND", DataValues.newString(nowQty.doubleValue()));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKIN_DETAIL", oCondition, oValues)));

            }

            totPqtyc = totPqtyc.add(nowQty);
            totAmtc = totAmtc.add(amt);
            totDistriAmtc = totDistriAmtc.add(distriAmt);
        }

        ColumnDataValue mainColumns = new ColumnDataValue();

        mainColumns.add("EID", DataValues.newString(eid));
        mainColumns.add("SHOPID", DataValues.newString(inOrder.getOrgNo()));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(inOrder.getOrgNo()));
        mainColumns.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        mainColumns.add("STOCKINNO", DataValues.newString(inOrder.getBillNo()));

        mainColumns.add("DOC_TYPE", DataValues.newString(stockInDocType));

        mainColumns.add("TRANSFER_SHOP", DataValues.newString(transferShop));
        mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(inTransferWarehouse));

        mainColumns.add("TOT_CQTY", DataValues.newString(pfcList.size()));
        mainColumns.add("TOT_PQTY", DataValues.newString(totPqtyc));
        mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmtc));
        mainColumns.add("TOT_AMT", DataValues.newString(totAmtc));

        mainColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
        mainColumns.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        mainColumns.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
        mainColumns.add("STATUS", DataValues.newString("0"));

        mainColumns.add("PACKINGNO", DataValues.newString(oneData.get("INPACKINGNO").toString()));
        mainColumns.add("LOAD_DOCNO", DataValues.newString(oneData.get("INLOAD_DOCNO").toString()));
        mainColumns.add("PTEMPLATENO", DataValues.newString(oneData.get("INPTEMPLATENO").toString()));
        mainColumns.add("WAREHOUSE", DataValues.newString(warehouse));

        mainColumns.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
        mainColumns.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
        mainColumns.add("INVWAREHOUSE", DataValues.newString(invWarehouse));

        mainColumns.add("OFNO", DataValues.newString(differenceNo));
        mainColumns.add("OTYPE", DataValues.newString("7"));

        mainColumns.add("OOTYPE", DataValues.newString(oneData.get("INOOTYPE").toString()));
        mainColumns.add("OOFNO", DataValues.newString(oneData.get("INOOFNO").toString()));

        mainColumns.add("MEMO", DataValues.newString("差异申诉调整"));

        if ("3".equals(inOrder.getType())) {  //配货出库红冲
            mainColumns.add("STOCKINNO_ORIGIN", DataValues.newString(stockInNo));
        }

        if ("3".equals(inOrder.getType())) {  //配货出库红冲
            //回写原单
            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue values = new ColumnDataValue();

            condition.add("EID", DataValues.newString(eid));
            condition.add("STOCKINNO", DataValues.newString(stockInNo));

            values.add("STOCKINNO_REFUND", DataValues.newString(inOrder.getBillNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_STOCKIN", condition, values)));

        }
        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN", mainColumns)));

        return inOrder;
    }

    private String insertAdjust(DCP_DifferenceProcessReq req, List<Map<String, Object>> stockList) throws Exception {

        Map<String, Object> oneData = stockList.get(0);
        String differenceNo = req.getRequest().getDifferenceNo();
        String eid = req.geteId();
        String invWarehouse = oneData.get("INVWAREHOUSE").toString();
        boolean isAdjust = false;

        for (Map<String, Object> stock : stockList) {
            invWarehouse = stock.get("INVWAREHOUSE").toString();
            BigDecimal pQty = new BigDecimal(stock.get("PQTY").toString()); //核准量
            BigDecimal outQty = new BigDecimal(stock.get("OUTQTY").toString());
            BigDecimal inQty = new BigDecimal(stock.get("INQTY").toString());
            BigDecimal diffQty = inQty.subtract(outQty); //差异量

            BigDecimal inPQty = pQty.subtract(diffQty);    //入库调整量

            String outBatchNo = StringUtils.toString(stock.get("OUTBATCHNO"), " ");
            String prodDate = stock.get("OUTPRODDATE").toString();
            String expDate = stock.get("OUTEXPDATE").toString();

            String batchNo = outBatchNo;

            if (StringUtils.isEmpty(batchNo) || StringUtils.isBlank(batchNo) || StringUtils.isEmpty(invWarehouse)) {  //无批号，无在途仓不需要处理
                continue;
            }

            //多收自动冲减多收入库时生成的在途异动
            if (diffQty.compareTo(BigDecimal.ZERO) > 0) {  //只有多收的时候才会产生库存调整
                isAdjust = true;
                break;
            }


        }
        String inOrg = oneData.get("INORGANIZATIONNO").toString();
        String outOrg = oneData.get("OUTORGANIZATIONNO").toString();
        String orgNo = outOrg;
        String adjustNo = "";
        double totCQty =0;
        if (isAdjust) {
            int item = 0;
            adjustNo = getOrderNO(req, orgNo, "KCTZ");

            for (Map<String, Object> stock : stockList) {
                invWarehouse = stock.get("INVWAREHOUSE").toString();
                BigDecimal pQty = new BigDecimal(stock.get("PQTY").toString()); //核准量
                BigDecimal outQty = new BigDecimal(stock.get("OUTQTY").toString());
                BigDecimal inQty = new BigDecimal(stock.get("INQTY").toString());
                BigDecimal diffQty = inQty.subtract(outQty); //差异量

                BigDecimal inPQty = pQty.subtract(diffQty);    //入库调整量

                String pluno = stock.get("PLUNO").toString();
                String featureno = stock.get("FEATURENO").toString();
                String location = StringUtils.toString(stock.get("OUTLOCATION"), " ");
                String punit = stock.get("PUNIT").toString();
                String oItem = stock.get("ITEM").toString();

                String outBatchNo = StringUtils.toString(stock.get("OUTBATCHNO"), " ");
                String prodDate = stock.get("OUTPRODDATE").toString();
                String expDate = stock.get("OUTEXPDATE").toString();

                String batchNo = outBatchNo;
                String pluBarCode = stock.get("PLU_BARCODE").toString();

//                String prodDate = stock.get("PROD_DATE").toString();
//                String expDate = stock.get("EXPDATE").toString();

                String baseunit = stock.get("BASEUNIT").toString();
                BigDecimal price = new BigDecimal(stock.get("PRICE").toString());
                BigDecimal distriprice = new BigDecimal(StringUtils.toString(stock.get("DISTRIPRICE"), "0"));
                if (StringUtils.isEmpty(stock.get("DISTRIPRICE").toString())) {
                    distriprice = new BigDecimal(stock.get("INDISTRIPRICE").toString());
                }

                BigDecimal unit_ratio = new BigDecimal(stock.get("UNIT_RATIO").toString());

                if (StringUtils.isEmpty(batchNo) || StringUtils.isBlank(batchNo) || StringUtils.isEmpty(invWarehouse)) {  //无批号，无在途仓不需要处理
                    continue;
                }

                totCQty++;
                //多收自动冲减多收入库时生成的在途异动
                if (diffQty.compareTo(BigDecimal.ZERO) > 0) {  //只有多收的时候才会产生库存调整

                    ColumnDataValue dcp_adjust_detail = new ColumnDataValue();

                    dcp_adjust_detail.add("EID", DataValues.newString(eid));
                    dcp_adjust_detail.add("SHOPID", DataValues.newString(orgNo));
                    dcp_adjust_detail.add("ORGANIZATIONNO", DataValues.newString(orgNo));
                    dcp_adjust_detail.add("ADJUSTNO", DataValues.newString(adjustNo));
                    dcp_adjust_detail.add("ITEM", DataValues.newString(++item));
                    BigDecimal nowQty = diffQty;

                    BigDecimal baseQty = nowQty.multiply(unit_ratio);
                    BigDecimal amt = nowQty.multiply(price);
                    BigDecimal distriAmt = nowQty.multiply(distriprice);

                    dcp_adjust_detail.add("BASEQTY", DataValues.newString(baseQty.doubleValue()));
                    dcp_adjust_detail.add("WAREHOUSE", DataValues.newString(invWarehouse));
                    dcp_adjust_detail.add("PLU_BARCODE", DataValues.newString(pluBarCode));
                    dcp_adjust_detail.add("UNIT_RATIO", DataValues.newString(unit_ratio));
                    dcp_adjust_detail.add("BASEUNIT", DataValues.newString(baseunit));
                    dcp_adjust_detail.add("PRICE", DataValues.newString(price));
                    dcp_adjust_detail.add("OITEM", DataValues.newString(stock.get("ITEM").toString()));
                    dcp_adjust_detail.add("PQTY", DataValues.newString(nowQty));
                    dcp_adjust_detail.add("PLUNO", DataValues.newString(pluno));
                    dcp_adjust_detail.add("PUNIT", DataValues.newString(punit));
                    dcp_adjust_detail.add("AMT", DataValues.newString(amt.doubleValue()));
                    dcp_adjust_detail.add("DISTRIPRICE", DataValues.newString(distriprice));
                    dcp_adjust_detail.add("BATCH_NO", DataValues.newString("DEFAULTBATCH"));
                    dcp_adjust_detail.add("PROD_DATE", DataValues.newString(""));
                    dcp_adjust_detail.add("DISTRIAMT", DataValues.newString(distriAmt.doubleValue()));
                    dcp_adjust_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                    dcp_adjust_detail.add("FEATURENO", DataValues.newString(featureno));
                    dcp_adjust_detail.add("LOCATION", DataValues.newString(location));
                    dcp_adjust_detail.add("EXPDATE", DataValues.newString(""));
//                    dcp_adjust_detail.add("CATEGORY", DataValues.newString(category));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_adjust_detail", dcp_adjust_detail)));

                    List<Object> inputParameter = Lists.newArrayList();
                    inputParameter.add(eid);
                    inputParameter.add(orgNo);
                    inputParameter.add("09");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                    inputParameter.add(adjustNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                    inputParameter.add(item);                                                   //P_Item		IN	INTEGER,	--单据行号
                    inputParameter.add(1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                    inputParameter.add(DateFormatUtils.getNowDate());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                    inputParameter.add(pluno);                                          //P_PluNo		IN	VARCHAR2,	--品号
                    inputParameter.add(featureno);          //P_FeatureNo	IN	VARCHAR2,	--特征码
                    inputParameter.add(invWarehouse);                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                    inputParameter.add("DEFAULTBATCH");        //P_BATCHNO	IN	VARCHAR2,	--批号
                    inputParameter.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                    inputParameter.add(punit);                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                    inputParameter.add(nowQty);                                            //P_Qty		IN	NUMBER,		--交易数量
                    inputParameter.add(baseunit);                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                    inputParameter.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                    inputParameter.add(unit_ratio);                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                    inputParameter.add(price);                                                   //P_Price		IN	NUMBER,		--零售价
                    inputParameter.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                    inputParameter.add(distriprice);                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                    inputParameter.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                    inputParameter.add(DateFormatUtils.getNowDate());               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                    inputParameter.add(DateFormatUtils.getDate(prodDate));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                    inputParameter.add(DateFormatUtils.getNowDate());               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                    inputParameter.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                    inputParameter.add("在途库存调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                    inputParameter.add(req.getEmployeeNo());                                                    //P_UserID	IN	VARCHAR2	--操作员

                    this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V3", inputParameter)));

                    ColumnDataValue dcp_adjust_detail2 = new ColumnDataValue();

                    dcp_adjust_detail2.add("EID", DataValues.newString(eid));
                    dcp_adjust_detail2.add("SHOPID", DataValues.newString(orgNo));
                    dcp_adjust_detail2.add("ORGANIZATIONNO", DataValues.newString(orgNo));
                    dcp_adjust_detail2.add("ADJUSTNO", DataValues.newString(adjustNo));
                    dcp_adjust_detail2.add("ITEM", DataValues.newString(++item));

                    nowQty = diffQty.multiply(BigDecimal.valueOf(-1));
                    baseQty = nowQty.multiply(unit_ratio);
                    amt = nowQty.multiply(price);
                    distriAmt = nowQty.multiply(distriprice);

                    dcp_adjust_detail2.add("BASEQTY", DataValues.newString(baseQty.doubleValue()));
                    dcp_adjust_detail2.add("WAREHOUSE", DataValues.newString(invWarehouse));
                    dcp_adjust_detail2.add("PLU_BARCODE", DataValues.newString(pluBarCode));
                    dcp_adjust_detail2.add("UNIT_RATIO", DataValues.newString(unit_ratio));
                    dcp_adjust_detail2.add("BASEUNIT", DataValues.newString(baseunit));
                    dcp_adjust_detail2.add("PRICE", DataValues.newString(price));
                    dcp_adjust_detail2.add("OITEM", DataValues.newString(stock.get("ITEM").toString()));
                    dcp_adjust_detail2.add("PQTY", DataValues.newString(nowQty));
                    dcp_adjust_detail2.add("PLUNO", DataValues.newString(pluno));
                    dcp_adjust_detail2.add("PUNIT", DataValues.newString(punit));
                    dcp_adjust_detail2.add("AMT", DataValues.newString(amt.doubleValue()));
                    dcp_adjust_detail2.add("DISTRIPRICE", DataValues.newString(distriprice));
                    dcp_adjust_detail2.add("BATCH_NO", DataValues.newString(batchNo));
                    dcp_adjust_detail2.add("PROD_DATE", DataValues.newString(prodDate));
                    dcp_adjust_detail2.add("DISTRIAMT", DataValues.newString(distriAmt.doubleValue()));
                    dcp_adjust_detail.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                    dcp_adjust_detail2.add("FEATURENO", DataValues.newString(featureno));
                    dcp_adjust_detail2.add("LOCATION", DataValues.newString(location));
                    dcp_adjust_detail2.add("EXPDATE", DataValues.newString(expDate));
//                    dcp_adjust_detail.add("CATEGORY", DataValues.newString(category));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_adjust_detail", dcp_adjust_detail2)));

                    List<Object> inputParameter1 = Lists.newArrayList();
                    inputParameter1.add(eid);
                    inputParameter1.add(orgNo);
                    inputParameter1.add("09");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                    inputParameter1.add(adjustNo);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                    inputParameter1.add(item);                                                   //P_Item		IN	INTEGER,	--单据行号
                    inputParameter1.add(1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                    inputParameter1.add(DateFormatUtils.getNowDate());                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                    inputParameter1.add(pluno);                                          //P_PluNo		IN	VARCHAR2,	--品号
                    inputParameter1.add(featureno);          //P_FeatureNo	IN	VARCHAR2,	--特征码
                    inputParameter1.add(invWarehouse);                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                    inputParameter1.add(batchNo);        //P_BATCHNO	IN	VARCHAR2,	--批号
                    inputParameter1.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                    inputParameter1.add(punit);                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                    inputParameter1.add(nowQty);                                            //P_Qty		IN	NUMBER,		--交易数量
                    inputParameter1.add(baseunit);                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                    inputParameter1.add(baseQty);                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                    inputParameter1.add(unit_ratio);                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                    inputParameter1.add(price);                                                   //P_Price		IN	NUMBER,		--零售价
                    inputParameter1.add(amt);                                                     //P_Amt		IN	NUMBER,		--零售金额
                    inputParameter1.add(distriprice);                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                    inputParameter1.add(distriAmt);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额
                    inputParameter1.add(DateFormatUtils.getNowDate());               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                    inputParameter1.add(DateFormatUtils.getDate(prodDate));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                    inputParameter1.add(DateFormatUtils.getNowDate());               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                    inputParameter1.add("");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                    inputParameter1.add("在途库存调整");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                    inputParameter1.add(req.getEmployeeNo());                                                    //P_UserID	IN	VARCHAR2	--操作员

                    this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V3", inputParameter1)));

                }

            }
            ColumnDataValue dcp_adjust = new ColumnDataValue();

            dcp_adjust.add("EID", DataValues.newString(eid));
            dcp_adjust.add("SHOPID", DataValues.newString(orgNo));
            dcp_adjust.add("ORGANIZATIONNO", DataValues.newString(orgNo));
            dcp_adjust.add("ADJUSTNO", DataValues.newString(adjustNo));
            dcp_adjust.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_adjust.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_adjust.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
            dcp_adjust.add("ACCOUNT_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_adjust.add("ACCOUNT_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
            dcp_adjust.add("MODIFY_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_adjust.add("MODIFY_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

            dcp_adjust.add("WAREHOUSE", DataValues.newString(invWarehouse));
            dcp_adjust.add("MEMO", DataValues.newString("差异申诉调整"));
            dcp_adjust.add("TOT_CQTY", DataValues.newString(totCQty));
            dcp_adjust.add("TOT_PQTY", DataValues.newString("0"));
            dcp_adjust.add("TOT_AMT", DataValues.newString("0"));

            dcp_adjust.add("DOC_TYPE", DataValues.newString("3"));

            dcp_adjust.add("OFNO", DataValues.newString(differenceNo));
            dcp_adjust.add("OTYPE", DataValues.newString("7"));
            dcp_adjust.add("STATUS", DataValues.newString("2"));

            dcp_adjust.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
            dcp_adjust.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_adjust.add("ACCOUNTBY", DataValues.newString(req.getEmployeeNo()));
            dcp_adjust.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            dcp_adjust.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_adjust", dcp_adjust)));

        }


        return adjustNo;
    }


    private String getStockOutNo(DCP_DifferenceProcessReq req, String docType, String orgNo) throws Exception {
        String prefix = "PSCK";
        if (docType.equals("1")) {
            prefix = "DBCK";
        } else if (docType.equals("0") || docType.equals("2")) {
            prefix = "THCK";
        } else if (docType.equals("3")) {
            prefix = "QTCK";
        } else if (docType.equals("4")) {
            prefix = "YCCK";
        }

        return getOrderNO(req, orgNo, prefix);
    }


    private String getStockInNo(DCP_DifferenceProcessReq req, String docType, String orgNo) throws Exception {
        String prefix = "PSRK";
        if ("1".equals(docType)) {
            prefix = "DBSH";
        } else if ("0".equals(docType) || "2".equals(docType)) {
            prefix = "PSSH";
        } else if ("3".equals(docType)) {
            prefix = "QTRK";
        } else if ("4".equals(docType)) {
            prefix = "YCSH";
        } else if ("5".equals(docType)) {
            prefix = "TPRK";
        } else if ("7".equals(docType)) {
            prefix = "YCRK";
        }
        return getOrderNO(req, orgNo, prefix);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Order {
        private String type;
        private String orgNo;
        private String billNo;
        private String docType;

    }


}
