package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticeRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 01029
 * @date 2024-09-19
 */
public class DCP_StockOutNoticeStatusUpdate
        extends SPosAdvanceService<DCP_StockOutNoticeStatusUpdateReq, DCP_StockOutNoticeRes> {

    @Override
    protected void processDUID(DCP_StockOutNoticeStatusUpdateReq req, DCP_StockOutNoticeRes res) throws Exception {

        //try {

        String eId = req.geteId();
        String status = "100";// 状态：-1未启用100已启用 0已禁用
        String billType = "";
        String keyNo = req.getRequest().getBillNo();
//        String sql = " SELECT * FROM DCP_STOCKOUTNOTICE " +
//                "WHERE EID='%s'  AND BILLNO='%s'  ";
        StringBuilder errMsg = new StringBuilder();
        String sql = getQuerySql(req);
        List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        if (CollectionUtils.isEmpty(mDatas)) {
            errMsg.append("单据不存在");
        } else {
            Map<String, Object> bill = mDatas.get(0);
            status = bill.get("MSTATUS").toString();
            billType = bill.get("BILLTYPE").toString();

        }
        String oprType = req.getRequest().getOpType();
        if (!"0".equals(status)) {
            if ("cancel".equals(oprType)) {
                errMsg.append("单据状态非【0-新建】不可作废！");
            }
            if ("confirm".equals(oprType)) {
                errMsg.append("单据状态非【0-新建】不可审核！");

                //3./DCP_StockOutNoticeStatusUpdate：审核增加检查，来源单号不为空时，
                // 累计【出通数】超出来源单据的订单需求量，
                // 则返回报错并提示：单号：xxxxx累计出货通知数已超出【可出货数】，请检查！
                //
                //【可出货数】取值根据【来源单据类型】关联对应表字段：
                //1.采购订单，取DCP_PURORDER_DELIVERY.STOCKINQTY(已入库量）
                //2.退货申请，取DCP_RETURNAPPLY.APPROVEQTY(退货核准数）
                //3.要货申请，取DCP_PORDER_DETAIL.REVIEW_QTY(要货核准数）
                //4.大客订单，取DCP_CUSTOMERPORDER_DETAIL.QTY(订单数）
                //5.调拨申请，取DCP_TRANSAPPLY.PQTY(调拨核准数）

                //SOURCETYPE
                //SOURCEBILLNO
                String detailSql = "select distinct a.SOURCETYPE,a.SOURCEBILLNO " +
                        " from dcp_stockoutnotice_detail a where a.eid='" + eId + "' " +
                        " and a.organizationno='" + req.getOrganizationNO() + "' and a.billno='" + req.getRequest().getBillNo() + "' ";
                List<Map<String, Object>> detailData = this.doQueryData(detailSql, null);

                for (Map<String, Object> detail : detailData) {
                    String sourceType = detail.get("SOURCETYPE").toString();
                    String sourceBillNo = detail.get("SOURCEBILLNO").toString();
                    if (Check.Null(sourceType) || Check.Null(sourceBillNo)) {
                        continue;
                    }
                    String valSql = "";
                    if ("1".equals(sourceType)) {
                        //1.采购订单，取DCP_PURORDER_DELIVERY.STOCKINQTY(已入库量）
                        valSql = "select a.purorderno as docno,a.item,a.STOCKINQTY as ynoqty,sum(nvl(b.noqty,0)) as noqty from DCP_PURORDER_DELIVERY a " +
                                " left join dcp_stockoutnotice_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.purorderno=b.sourcebillno and a.item=b.oitem " +
                                " where a.eid='" + eId + "' and a.organizationno='" + req.getOrganizationNO() + "' and a.purorderno='" + sourceBillNo + "'" +
                                " group by a.purorderno,a.item,a.STOCKINQTY ";

                    } else if ("2".equals(sourceType)) {
                        //2.退货申请，取DCP_RETURNAPPLY.APPROVEQTY(退货核准数）
                        valSql = "select a.billno as docno,a.item,a.APPROVEQTY as ynoqty,sum(nvl(b.noqty,0)) as noqty " +
                                " from DCP_RETURNAPPLY_DETAIL a " +
                                " left join dcp_stockoutnotice_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.billno=b.sourcebillno and a.item=b.oitem " +
                                " where a.eid='" + eId + "'  and a.billno='" + sourceBillNo + "'" +
                                " group by a.billno,a.item,a.APPROVEQTY ";
                    } else if ("3".equals(sourceType)) {
                        //3.要货申请，取DCP_PORDER_DETAIL.REVIEW_QTY(要货核准数）
                        valSql = "select a.porderno as docno,a.item,a.REVIEW_QTY as ynoqty,sum(nvl(b.noqty,0)) as noqty " +
                                " from DCP_PORDER_DETAIL a " +
                                " left join dcp_stockoutnotice_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.porderno=b.sourcebillno and a.item=b.oitem " +
                                " where a.eid='" + eId + "'  and a.porderno='" + sourceBillNo + "'" +
                                " group by a.porderno,a.item,a.REVIEW_QTY ";

                    } else if ("4".equals(sourceType)) {
                        //4.大客订单，取DCP_CUSTOMERPORDER_DETAIL.QTY(订单数）
                        valSql = "select a.PORDERNO as docno,a.item,a.QTY as ynoqty,sum(nvl(b.noqty,0)) as noqty " +
                                " from DCP_CUSTOMERPORDER_DETAIL a " +
                                " left join dcp_stockoutnotice_detail b on a.eid=b.eid and a.shopno=b.organizationno and a.PORDERNO=b.sourcebillno and a.item=b.oitem " +
                                " where a.eid='" + eId + "'  and a.PORDERNO='" + sourceBillNo + "'" +
                                " group by a.PORDERNO,a.item,a.QTY ";//and a.organizationno='"+req.getOrganizationNO()+"'
                    } else if ("5".equals(sourceType)) {
                        //5.调拨申请，取DCP_TRANSAPPLY.PQTY(调拨核准数）
                        valSql = "select a.billno as docno,a.item,a.PQTY as ynoqty,sum(nvl(b.noqty,0)) as noqty " +
                                " from DCP_TRANSAPPLY_DETAIL a " +
                                " left join dcp_stockoutnotice_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.billno=b.sourcebillno and a.item=b.oitem " +
                                " where a.eid='" + eId + "'  and a.billno='" + sourceBillNo + "'" +
                                " group by a.billno,a.item,a.PQTY ";//and a.organizationno='"+req.getOrganizationNO()+"'
                    }

                    if (Check.NotNull(valSql)) {
                        List<Map<String, Object>> valData = this.doQueryData(valSql, null);
                        for (Map<String, Object> val : valData) {
                            BigDecimal yNoQty = new BigDecimal(val.get("YNOQTY").toString());
                            BigDecimal noQty = new BigDecimal(val.get("NOQTY").toString());
                            String docNo = val.get("DOCNO").toString();
                            if (yNoQty.compareTo(noQty) < 0) {
                                errMsg.append("单号：").append(docNo).append("累计出货通知数已超出【可出货数】，请检查！");
                                break;
                            }
                        }
                    }
                }

            }
        }

        if (!"1".equals(status)) {
            if ("close".equals(oprType)) {
                errMsg.append("单据状态非待出货】不可结束发货！");
            }
            if ("unconfirm".equals(oprType)) {
                String sql1 = " SELECT * FROM DCP_PURSTOCKIN_DETAIL A INNER JOIN DCP_STOCKOUTNOTICE_DETAIL B "
                        + " ON A.NOTICENO=B.BILLNO AND A.NOTICEITEM=B.ITEM "
                        + "  WHERE EID='%s'   AND BILLNO='%s' ";
                sql1 = String.format(sql1, req.geteId(), keyNo);
                List<Map<String, Object>> mDatas1 = this.doQueryData(sql1, null);
                if (SUtil.EmptyList(mDatas1)) {

                } else {
                    errMsg.append("存在关联采退出库单不可取消审核！");

                }
                errMsg.append("单据状态非待出货不可取消审核！");
            }
        }

        if (StringUtils.isNotEmpty(errMsg.toString())) {
            res.setSuccess(false);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行失败" + errMsg.toString());
            return;
        }

        String oPId = req.getEmployeeNo();
        UptBean up1 = new UptBean("DCP_STOCKOUTNOTICE");
        up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        up1.addCondition("BILLNO", new DataValue(keyNo, Types.VARCHAR));

        String lastmoditime = DateFormatUtils.getNowDateTime();

        if ("cancel".equals(oprType)) {
            up1.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));
            up1.addUpdateValue("CANCELBY", new DataValue(oPId, Types.VARCHAR));
            up1.addUpdateValue("CANCELTIME", new DataValue(lastmoditime, Types.DATE));
        }
        if ("confirm".equals(oprType)) {
            up1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
            up1.addUpdateValue("CONFIRMBY", new DataValue(oPId, Types.VARCHAR));
            up1.addUpdateValue("CONFIRMTIME", new DataValue(lastmoditime, Types.DATE));
        }
        if ("unconfirm".equals(oprType)) {
            up1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            up1.addUpdateValue("CONFIRMBY", new DataValue("", Types.VARCHAR));
            up1.addUpdateValue("CONFIRMTIME", new DataValue("", Types.DATE));
            up1.addUpdateValue("LASTMODIOPID", new DataValue(oPId, Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
        }
        if ("close".equals(oprType)) {
//            String detailSql = "select a.* " +
//                    " from dcp_stockoutnotice_detail a where a.eid='" + eId + "' " +
//                    " and a.organizationno='" + req.getOrganizationNO() + "' and a.billno='" + req.getRequest().getBillNo() + "' ";
//            List<Map<String, Object>> detailData = this.doQueryData(detailSql, null);
            List<Map<String, Object>> filterRows1 = mDatas.stream().filter(x -> x.get("STATUS").toString().equals("4")).collect(Collectors.toList());
            if (!filterRows1.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "明细存在状态=“出货中”，不可结束出货！");
            }
            List<Map<String, Object>> filterRows2 = mDatas.stream().filter(x -> x.get("STATUS").toString().equals("1")).collect(Collectors.toList());

            up1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));//"2-出货结束"
            up1.addUpdateValue("LASTMODIOPID", new DataValue(oPId, Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

            for (Map<String, Object> detail : filterRows2) {
                UptBean up2 = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
                up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                up2.addCondition("BILLNO", new DataValue(keyNo, Types.VARCHAR));
                up2.addCondition("ITEM", new DataValue(detail.get("ITEM").toString(), Types.VARCHAR));
                up2.addUpdateValue("STATUS", new DataValue("9", Types.VARCHAR));//“2-出货结束”
                this.addProcessData(new DataProcessBean(up2));
            }

        }
        this.addProcessData(new DataProcessBean(up1));

        if ("confirm".equals(oprType)) {

            if ("2".equals(billType) || "3".equals(billType)) {

                List<DocNo> docNoList = getDocNo(req, mDatas);
                for (DocNo docNo : docNoList) {

                    Map<String, Object> condition = new HashMap<>();
                    condition.put("BILLNO", docNo.getBillNo());
                    condition.put("OBJECTID", docNo.getObjectId());
                    condition.put("TEMPLATENO", docNo.getTemplateNo());
                    List<Map<String, Object>> detailData = MapDistinct.getWhereMap(mDatas, condition, true);

                    Map<String, Object> master = detailData.get(0);


                    //生成底稿
                    ColumnDataValue mes_sortingdata = new ColumnDataValue();
                    mes_sortingdata.add("EID", DataValues.newString(master.get("EID")));
                    mes_sortingdata.add("ORGANIZATIONNO", DataValues.newString(master.get("ORGANIZATIONNO")));
                    mes_sortingdata.add("DOCNO", DataValues.newString(docNo.toString()));
                    mes_sortingdata.add("REQUIRENO", DataValues.newString(docNo.getObjectId()));
                    mes_sortingdata.add("BDATE", DataValues.newDate(DateFormatUtils.getDate(master.get("BDATE").toString())));
                    mes_sortingdata.add("RTAMPLATENO", DataValues.newString(docNo.getTemplateNo()));
                    mes_sortingdata.add("DEPARTMENT", DataValues.newString("2".equals(billType) ? "1" : "0"));
                    mes_sortingdata.add("TRANTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                    mes_sortingdata.add("NAME", DataValues.newString(master.get("CONTACT")));
                    mes_sortingdata.add("PHONE", DataValues.newString(master.get("PHONE")));
                    mes_sortingdata.add("ADDRESS", DataValues.newString(master.get("ADDRESS")));
                    mes_sortingdata.add("SDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                    mes_sortingdata.add("STIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
                    mes_sortingdata.add("STATUS", DataValues.newString("0"));
                    mes_sortingdata.add("OFNO", DataValues.newString(docNo.getBillNo()));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_SORTINGDATA", mes_sortingdata)));

                    int item = 0;
                    for (Map<String, Object> oneData : detailData) {
                        ColumnDataValue mes_sortdatadetail = new ColumnDataValue();

                        mes_sortdatadetail.add("EID", DataValues.newString(oneData.get("EID")));
                        mes_sortdatadetail.add("ORGANIZATIONNO", DataValues.newString(oneData.get("ORGANIZATIONNO")));
                        mes_sortdatadetail.add("DOCNO", DataValues.newString(docNo.toString()));
                        mes_sortdatadetail.add("ITEM", DataValues.newString(oneData.get("ITEM").toString()));
                        mes_sortdatadetail.add("PLUNO", DataValues.newString(oneData.get("PLUNO")));
                        mes_sortdatadetail.add("FEATURENO", DataValues.newString(oneData.get("FEATURENO")));
                        mes_sortdatadetail.add("UNIT", DataValues.newString(oneData.get("PUNIT")));
                        mes_sortdatadetail.add("QTY", DataValues.newString(oneData.get("PQTY")));
                        mes_sortdatadetail.add("RDATE", DataValues.newDate(DateFormatUtils.getDate(oneData.get("DELIVERYDATE").toString())));
                        mes_sortdatadetail.add("SHIPPEDQTY", DataValues.newString(0));
                        mes_sortdatadetail.add("RESIDUEQTY", DataValues.newString(0));
                        mes_sortdatadetail.add("TRANSFEROUT", DataValues.newString(oneData.get("WAREHOUSE")));
//                mes_sortdatadetail.add("TRANSFERIN",DataValues.newString(oneData.get("WAREHOUSE")));
                        mes_sortdatadetail.add("ACTUALALLOCATION", DataValues.newString(0));
                        mes_sortdatadetail.add("PRICE", DataValues.newString(oneData.get("PRICE")));
                        mes_sortdatadetail.add("AMT", DataValues.newString(oneData.get("AMOUNT").toString()));
                        mes_sortdatadetail.add("TAXRATE", DataValues.newString(oneData.get("TAXRATE").toString()));
                        mes_sortdatadetail.add("TAXAMOUNT", DataValues.newString(oneData.get("TAXAMT").toString()));
                        mes_sortdatadetail.add("SOURCETYPE", DataValues.newString("2".equals(billType) ? "32" : "B2"));
                        mes_sortdatadetail.add("SOURCENO", DataValues.newString(oneData.get("SOURCEBILLNO")));
                        mes_sortdatadetail.add("OITEM", DataValues.newString(oneData.get("OITEM")));
                        mes_sortdatadetail.add("ERPITEM", DataValues.newString(oneData.get("ITEM")));

                        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("MES_SORTDATADETAIL", mes_sortdatadetail)));

                    }


                }


            }


            if (billType.equals("2") || billType.equals("3")) {
                String demandSql = "select a.orderno,a.item,a.DISTRISTATUS,nvl(a.STOCKOUTNOQTY,0) as STOCKOUTNOQTY,nvl(b.pqty,0) as pqty,nvl(a.STOCKOUTQTY,0) as STOCKOUTQTY " +
                        " from dcp_demand a " +
                        " left join DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and b.sourcebillno=a.orderno and a.item=b.oitem " +
                        " where a.eid='" + eId + "' and b.billno='" + keyNo + "'" +
                        " ";

                List<Map<String, Object>> demandList = this.doQueryData(demandSql, null);

                List<Map<String, String>> gDemandList = demandList.stream().map(x -> {
                    Map<String, String> gMap = new HashMap<>();
                    gMap.put("ORDERNO", x.get("ORDERNO").toString());
                    gMap.put("ITEM", x.get("ITEM").toString());
                    gMap.put("DISTRISTATUS", x.get("DISTRISTATUS").toString());
                    return gMap;
                }).distinct().collect(Collectors.toList());

                for (Map<String, String> demand : gDemandList) {
                    String orderNo = demand.get("ORDERNO");
                    String item = demand.get("ITEM");
                    List<Map<String, Object>> gFilterList = demandList.stream().filter(x -> x.get("ORDERNO").toString().equals(orderNo) && x.get("ITEM").toString().equals(item)).collect(Collectors.toList());
                    BigDecimal stockOutNoticeQty = new BigDecimal(0);
                    BigDecimal stockOuQty = new BigDecimal(0);
                    BigDecimal pQty = new BigDecimal(0);
                    for (Map<String, Object> gFilter : gFilterList) {
                        stockOutNoticeQty = new BigDecimal(gFilter.get("STOCKOUTNOQTY").toString());
                        stockOuQty = new BigDecimal(gFilter.get("STOCKOUTQTY").toString());
                        pQty = pQty.add(new BigDecimal(gFilter.get("PQTY").toString()));
                    }

                    BigDecimal sum = stockOutNoticeQty.add(pQty);

                    String distriStatus = demand.get("DISTRISTATUS");
                    if (stockOutNoticeQty.compareTo(BigDecimal.ZERO) > 0) {
                        if (stockOuQty.compareTo(BigDecimal.ZERO) <= 0) {
                            distriStatus = "11";//11.已通知出货；
                        } else {
                            if (stockOutNoticeQty.compareTo(stockOuQty) > 0) {
                                distriStatus = "13";//13.部分出货；
                            }
                        }
                    }

                    UptBean up2 = new UptBean("DCP_DEMAND");
                    up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    up2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    up2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    up2.addUpdateValue("DISTRISTATUS", new DataValue(distriStatus, Types.VARCHAR));
                    up2.addUpdateValue("STOCKOUTNOQTY", new DataValue(sum.toString(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(up2));

                }
            }

        }

        if ("unconfirm".equals(oprType)) {

            if ("2".equals(billType) || "3".equals(billType)) {

                List<DocNo> docNoList = getDocNo(req, mDatas);

                StringBuilder querySortingData = new StringBuilder(" SELECT * FROM MES_SORTINGDATA a " +
                        " where a.eid='" + eId + "' AND a.STATUS<>'0' ");
                querySortingData.append(" AND (1=2");
                for (DocNo docNo : docNoList) {
                    querySortingData.append(" OR DOCNO='").append(docNo).append("'");
                }
                querySortingData.append(")");
                List<Map<String, Object>> sortingDatalist = this.doQueryData(querySortingData.toString(), null);
                if (CollectionUtils.isNotEmpty(sortingDatalist)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "底稿状态不为0:新建，不可取消审核!");
                }

                for (DocNo docNo : docNoList) {
                    //删除底稿
                    ColumnDataValue condition = new ColumnDataValue();
                    condition.add("EID", DataValues.newString(req.geteId()));
                    condition.add("DOCNO", DataValues.newString(docNo.toString()));

                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_SORTINGDATA", condition)));
                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("MES_SORTDATADETAIL", condition)));
                }

                String demandSql = "select a.orderno,a.item,a.DISTRISTATUS,nvl(a.STOCKOUTNOQTY,0) as STOCKOUTNOQTY,nvl(b.pqty,0) as pqty,nvl(a.STOCKOUTQTY,0) as STOCKOUTQTY " +
                        " from dcp_demand a " +
                        " left join DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and b.sourcebillno=a.orderno and a.item=b.oitem " +
                        " where a.eid='" + eId + "' and b.billno='" + keyNo + "'" +
                        " ";

                List<Map<String, Object>> demandList = this.doQueryData(demandSql, null);

                List<Map<String, String>> gDemandList = demandList.stream().map(x -> {
                    Map<String, String> gMap = new HashMap<>();
                    gMap.put("ORDERNO", x.get("ORDERNO").toString());
                    gMap.put("ITEM", x.get("ITEM").toString());
                    gMap.put("DISTRISTATUS", x.get("DISTRISTATUS").toString());
                    return gMap;
                }).distinct().collect(Collectors.toList());

                for (Map<String, String> demand : gDemandList) {
                    String orderNo = demand.get("ORDERNO");
                    String item = demand.get("ITEM");
                    List<Map<String, Object>> gFilterList = demandList.stream().filter(x -> x.get("ORDERNO").toString().equals(orderNo) && x.get("ITEM").toString().equals(item)).collect(Collectors.toList());
                    BigDecimal stockOutNoticeQty = new BigDecimal(0);
                    BigDecimal stockOuQty = new BigDecimal(0);
                    BigDecimal pQty = new BigDecimal(0);
                    for (Map<String, Object> gFilter : gFilterList) {
                        stockOutNoticeQty = new BigDecimal(gFilter.get("STOCKOUTNOQTY").toString());
                        stockOuQty = new BigDecimal(gFilter.get("STOCKOUTQTY").toString());
                        pQty = pQty.add(new BigDecimal(gFilter.get("PQTY").toString()));
                    }

                    BigDecimal sum = stockOutNoticeQty.subtract(pQty);

                    if (sum.compareTo(BigDecimal.ZERO) < 0) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "出通量小于0");
                    }

                    String disTriStatus = demand.get("DISTRISTATUS");
                    if (stockOutNoticeQty.compareTo(BigDecimal.ZERO) > 0) {
                        if (stockOuQty.compareTo(BigDecimal.ZERO) <= 0) {
                            disTriStatus = "11";//11.已通知出货；
                        } else {
                            if (stockOutNoticeQty.compareTo(stockOuQty) > 0) {
                                disTriStatus = "13";//13.部分出货；
                            }
                        }
                    } else {
                        disTriStatus = "00";
                    }
                    UptBean up2 = new UptBean("DCP_DEMAND");
                    up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    up2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    up2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    up2.addUpdateValue("DISTRISTATUS", new DataValue(disTriStatus, Types.VARCHAR));
                    up2.addUpdateValue("STOCKOUTNOQTY", new DataValue(sum.toString(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(up2));

                }
            }

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        //}
        //catch (Exception e) {

        //	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, req.getServiceId() + e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutNoticeStatusUpdateReq req) throws Exception {

        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutNoticeStatusUpdateReq req) throws Exception {

        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutNoticeStatusUpdateReq req) throws Exception {

        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockOutNoticeStatusUpdateReq req) throws Exception {

        boolean isFail = false;

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockOutNoticeStatusUpdateReq> getRequestType() {

        return new TypeToken<DCP_StockOutNoticeStatusUpdateReq>() {
        };
    }

    @Override
    protected String getQuerySql(DCP_StockOutNoticeStatusUpdateReq req) throws Exception {
        StringBuilder querySql = new StringBuilder(" SELECT " +
                "  a.BDATE,a.BILLTYPE,a.DELIVERORGNO,a.SOURCETYPE,a.SOURCEBILLNO " +
                "  ,a.RDATE,a.OBJECTTYPE MOBJECTTYPE,a.OBJECTID MOBJECTID,a.PAYTYPE,a.PAYORGNO,a.BILLDATENO " +
                "  ,a.PAYDATENO,a.INVOICECODE,a.CURRENCY,a.WAREHOUSE,a.BSNO,a.RETURNTYPE" +
                "  ,a.STATUS MSTATUS,o.CONTACT,o.PHONE,a.DELIVERYDATE " +
                "  ,CASE WHEN a.BILLTYPE='2' THEN bz.DELIVERY_ADDRESS ELSE o.ADDRESS END ADDRESS   " +
                "  ,b.* " +
                "  FROM  DCP_STOCKOUTNOTICE a" +
                "  INNER JOIN DCP_STOCKOUTNOTICE_DETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO " +
                "  LEFT JOIN DCP_ORG o on a.eid=o.eid and a.OBJECTID=o.ORGANIZATIONNO and a.BILLTYPE='3'" +
                "  LEFT JOIN DCP_BIZPARTNER bz on a.eid=bz.eid and a.OBJECTID=bz.BIZPARTNERNO and a.BILLTYPE='2' " +
                " ");
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" AND a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBillType())) {
            querySql.append(" AND a.BILLTYPE='").append(req.getRequest().getBillType()).append("'");
        }

        //指定结束时单身控制
        if (CollectionUtils.isNotEmpty(req.getRequest().getDetail()) && "close".equals(req.getRequest().getOpType())) {

            querySql.append(" AND ( 1=2 ");

            for (DCP_StockOutNoticeStatusUpdateReq.Detail item : req.getRequest().getDetail()) {
                querySql.append(" OR b.ITEM='").append(item.getItem()).append("'");
            }
            querySql.append(")");

        }

        return querySql.toString();
    }

    private List<DocNo> getDocNo(DCP_StockOutNoticeStatusUpdateReq req, List<Map<String, Object>> noticeData) {
        List<DocNo> docNoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(noticeData)) {
            for (Map<String, Object> notice : noticeData) {
                DocNo docNo = new DocNo();
                docNo.setBillNo(notice.get("BILLNO").toString());
                docNo.setObjectId(notice.get("OBJECTID").toString());
                docNo.setTemplateNo(notice.get("TEMPLATENO").toString());


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


    @Override
    protected DCP_StockOutNoticeRes getResponseType() {
        return new DCP_StockOutNoticeRes();
    }

}
