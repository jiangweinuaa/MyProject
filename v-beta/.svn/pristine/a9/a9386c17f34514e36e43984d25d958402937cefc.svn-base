package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReceivingStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReceivingStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ReceivingStatusUpdate extends SPosAdvanceService<DCP_ReceivingStatusUpdateReq, DCP_ReceivingStatusUpdateRes> {

    //枚举: confirm：审核 unconfirm：取消审核 cancel：作废 close：结束收货
    private static final String TYPE_CONFIRM = "confirm";
    private static final String TYPE_UNCONFIRM = "unconfirm";
    private static final String TYPE_CANCEL = "cancel";
    private static final String TYPE_CLOSE = "close";
    private static final String TYPE_UNCLOSE = "unclose";


    @Override
    protected void processDUID(DCP_ReceivingStatusUpdateReq req, DCP_ReceivingStatusUpdateRes res) throws Exception {

        //try {

            String nowTime =	new SimpleDateFormat("HHmmss").format(new Date());
            String nowDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

            String query = " SELECT STATUS FROM DCP_RECEIVING a " +
                    " WHERE a.EID='%s' AND a.RECEIVINGNO='%s' ";
            query = String.format(query, req.geteId(), req.getRequest().getReceivingNo());
            List<Map<String, Object>> data = this.doQueryData(query, null);

            if (CollectionUtils.isEmpty(data)){
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("未查询到对应信息");
                return;
            }
            Map<String, Object> bill = data.get(0);

            int status = Integer.parseInt(bill.get("STATUS").toString());

            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));

            ColumnDataValue master = new ColumnDataValue();
            if (StringUtils.equals(req.getRequest().getOpType(), TYPE_CONFIRM)) {
                if (status == 0) {
                    master.add("STATUS", DataValues.newInteger(6));
                    master.add("CONFIRMBY",DataValues.newString(req.getOpNO()));
                    master.add("CONFIRM_DATE",DataValues.newString(nowDate));
                    master.add("CONFIRM_TIME",DataValues.newString(nowTime));
                } else {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前单据状态不可审核");
                    return;
                }

            }
            else if (StringUtils.equals(req.getRequest().getOpType(), TYPE_UNCONFIRM)) {

                String rdSql="select * from dcp_receiving_detail a " +
                        " where a.eid='" + req.geteId() + "' and a.RECEIVINGNO='" + req.getRequest().getReceivingNo() + "' ";
                List<Map<String, Object>> rdData = this.doQueryData(rdSql, null);
                List<BigDecimal> filterRows = rdData.stream().map(x -> {
                    String stockin_qty = x.get("STOCKIN_QTY").toString();
                    BigDecimal qty = new BigDecimal(Check.Null(stockin_qty) ? "0" : stockin_qty);
                    return qty;
                }).filter(y -> y.compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());


                if (status == 6&&filterRows.size()<=0) {
                    master.add("STATUS", DataValues.newInteger(0));
                    master.add("CONFIRMBY",DataValues.newString(""));
                    master.add("CONFIRM_DATE",DataValues.newString(""));
                    master.add("CONFIRM_TIME",DataValues.newString(""));
                } else {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前单据状态不可反审核");
                    return;
                }
            }
            else if (StringUtils.equals(req.getRequest().getOpType(), TYPE_CANCEL)) {
                if (status == 0) {
                    master.add("STATUS", DataValues.newInteger(8));
                    master.add("CANCELBY",DataValues.newString(req.getOpNO()));
                    master.add("CANCEL_DATE",DataValues.newString(nowDate));
                    master.add("CANCEL_TIME",DataValues.newString(nowTime));
                } else {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前单据状态不可作废");
                    return;
                }
            }
            else if (StringUtils.equals(req.getRequest().getOpType(), TYPE_CLOSE)) {
                if (status == 6) {
                    master.add("STATUS", DataValues.newInteger(9));
                    String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    master.add("CLOSEOPID", DataValues.newString(req.getOpNO()));
                    master.add("CLOSETIME", DataValues.newDate(lastmoditime));

                    ColumnDataValue detail = new ColumnDataValue();
                    detail.add("STATUS", DataValues.newString(99));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING_DETAIL",condition,detail)));
                }else {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前单据状态不可结束收货");
                    return;
                }

            }
            else if(TYPE_UNCLOSE.equals(req.getRequest().getOpType())){
                if(status!=9){
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前单据状态不可取消结束");
                    return;
                }
                //获取detail中已收货量<数量的明细status=0待收货；
                // detail.status存在status=0的明细，则更新单据状status=6待收货；否则保持原状态不变，并提示：无可更新的数据！
                String sql="select * from dcp_receiving_detail a where a.eid='"+req.geteId()+"' " +
                        " and a.status='0' and nvl(a.STOCKIN_QTY,0)<a.pqty ";
                List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
                if(detailDatas.size()<=0){
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("无可更新的数据！");
                    return;
                }
                String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                master.add("STATUS", DataValues.newInteger(6));
                master.add("CLOSEOPID", DataValues.newString(""));
                master.add("CLOSETIME", DataValues.newDate(null));

                String sql1="select * from dcp_receiving_detail a where a.eid='"+req.geteId()+"' " +
                        " and a.status='99' ";
                List<Map<String, Object>> detailDatas1 = this.doQueryData(sql1, null);
                for (Map<String, Object> map : detailDatas1){
                    ColumnDataValue condition1 = new ColumnDataValue();
                    condition1.add("EID", DataValues.newString(req.geteId()));
                    condition1.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));
                    condition1.add("ITEM", DataValues.newString(map.get("ITEM").toString()));
                    ColumnDataValue detail = new ColumnDataValue();
                    detail.add("STATUS", DataValues.newString(0));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING_DETAIL",condition1,detail)));

                }
            }

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", condition, master)));


            //更新采购订单bookqty
            if(TYPE_CONFIRM.equals(req.getRequest().getOpType())||TYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {
                String receivingDSql = "select b.purorderno,b.item,b.item2,b.BOOKQTY,sum(a.PQTY) as pqty " +
                        " from dcp_receiving_detail a " +
                        " inner join dcp_purorder_delivery b on a.eid=b.eid and a.ofno=b.purorderno and a.oitem=b.item and a.oitem2=b.item2 " +
                        " where a.eid='" + req.geteId() + "' and a.RECEIVINGNO='" + req.getRequest().getReceivingNo() + "' " +
                        " group by b.purorderno,b.item,b.item2,b.BOOKQTY ";
                List<Map<String, Object>> receivingDDatas = this.doQueryData(receivingDSql, null);

                if (TYPE_CONFIRM.equals(req.getRequest().getOpType())) {

                    for (Map<String, Object> map : receivingDDatas) {
                        BigDecimal bookQty = new BigDecimal(map.get("BOOKQTY").toString());
                        BigDecimal pQty = new BigDecimal(map.get("PQTY").toString());
                        bookQty = bookQty.add(pQty);
                        UptBean ub1 = new UptBean("DCP_PURORDER_DELIVERY");
                        ub1.addUpdateValue("BOOKQTY", new DataValue(bookQty, Types.VARCHAR));
                        ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                        ub1.addCondition("PURORDERNO", new DataValue(map.get("PURORDERNO").toString(), Types.VARCHAR));
                        ub1.addCondition("ITEM", new DataValue(map.get("ITEM").toString(), Types.VARCHAR));
                        ub1.addCondition("ITEM2", new DataValue(map.get("ITEM2").toString(), Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub1));
                    }

                }
                else if (TYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {
                    for (Map<String, Object> map : receivingDDatas) {
                        BigDecimal bookQty = new BigDecimal(map.get("BOOKQTY").toString());
                        BigDecimal pQty = new BigDecimal(map.get("PQTY").toString());
                        bookQty = bookQty.subtract(pQty);
                        UptBean ub1 = new UptBean("DCP_PURORDER_DELIVERY");
                        ub1.addUpdateValue("BOOKQTY", new DataValue(bookQty, Types.VARCHAR));
                        ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                        ub1.addCondition("PURORDERNO", new DataValue(map.get("PURORDERNO").toString(), Types.VARCHAR));
                        ub1.addCondition("ITEM", new DataValue(map.get("ITEM").toString(), Types.VARCHAR));
                        ub1.addCondition("ITEM2", new DataValue(map.get("ITEM2").toString(), Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub1));
                    }
                }
            }


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        //} catch (Exception e) {
        //    res.setSuccess(false);
        //    res.setServiceStatus("200");
        //    res.setServiceDescription("服务执行异常:" + e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReceivingStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReceivingStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReceivingStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReceivingStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReceivingStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ReceivingStatusUpdateReq>() {

        };
    }

    @Override
    protected DCP_ReceivingStatusUpdateRes getResponseType() {
        return new DCP_ReceivingStatusUpdateRes();
    }
}
