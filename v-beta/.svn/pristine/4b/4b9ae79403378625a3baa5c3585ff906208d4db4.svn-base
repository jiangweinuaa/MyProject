package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerPOrderStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPOrderStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * 影响因素新增
 *
 * @author yuanyy
 */
public class DCP_CustomerPOrderStatusUpdate extends SPosAdvanceService<DCP_CustomerPOrderStatusUpdateReq, DCP_CustomerPOrderStatusUpdateRes> {


    @Override
    protected void processDUID(DCP_CustomerPOrderStatusUpdateReq req, DCP_CustomerPOrderStatusUpdateRes res) throws Exception {
        // TODO Auto-generated method stub


//			confirm审核 unConfirm取消审核 cancel作废 close结束订单

        ColumnDataValue condition = new ColumnDataValue();

        List<Map<String, Object>> getQData = doQueryData(getQuerySql(req), null);
        if (null == getQData || getQData.isEmpty()) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, req.getRequest().getPOrderNo() + "该大客户订单不存在!");
        }

        Map<String, Object> oneData = getQData.get(0);

        String eid = oneData.get("EID").toString();
        String shopNO = oneData.get("SHOPNO").toString();
        String pOrderNo = oneData.get("PORDERNO").toString();
        String organizationNo = oneData.get("ORGANIZATIONNO").toString();

        String status = oneData.get("STATUS").toString();

        condition.add("EID", DataValues.newString(eid));
        condition.add("SHOPNO", DataValues.newString(shopNO));
        condition.add("PORDERNO", DataValues.newString(pOrderNo));
        condition.add("ORGANIZATIONNO", DataValues.newString(organizationNo));

        ColumnDataValue dcp_customerPOrder = new ColumnDataValue();
        String lastModifyTime = DateFormatUtils.getNowDateTime();

        dcp_customerPOrder.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_customerPOrder.add("LASTMODITIME", DataValues.newDate(lastModifyTime));

        if (Constant.OPR_TYPE_CONFIRM.equals(req.getRequest().getOpType())) {

            if (!"0".equals(status)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前单据状态不为0-新建,不可审核!");
            }

            dcp_customerPOrder.add("CONFIRMOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_customerPOrder.add("CONFIRMOPNAME", DataValues.newString(req.getEmployeeName()));
            dcp_customerPOrder.add("CONFIRMTIME", DataValues.newDate(lastModifyTime));

            dcp_customerPOrder.add("STATUS", DataValues.newInteger(1));

            List<Map<String, Object>> qDetail = doQueryData(getQueryDetail(req), null);

            for (Map<String, Object> detail : qDetail) {

                ColumnDataValue dcp_demand = new ColumnDataValue();
                dcp_demand.add("EID", detail.get("EID").toString());
                dcp_demand.add("ORGANIZATIONNO", detail.get("ORGANIZATIONNO").toString());
                dcp_demand.add("ORDERNO", detail.get("PORDERNO").toString());

                dcp_demand.add("ORDERTYPE", "2");
                dcp_demand.add("ITEM", detail.get("ITEM").toString());
                dcp_demand.add("PLUBARCODE", detail.get("PLUBARCODE").toString());
                dcp_demand.add("PLUNO", detail.get("PLUNO").toString());
                dcp_demand.add("FEATURENO", detail.get("FEATURENO").toString());
                dcp_demand.add("PUNIT", detail.get("UNIT").toString());
                dcp_demand.add("PQTY", detail.get("QTY").toString());
                dcp_demand.add("POQTY", detail.get("QTY").toString());
                dcp_demand.add("BASEUNIT", detail.get("BASEUNIT").toString());
                dcp_demand.add("BASEQTY", detail.get("BASEQTY").toString());
                dcp_demand.add("RDATE", detail.get("RDATE").toString());
                dcp_demand.add("OBJECTTYPE", "2");
                dcp_demand.add("OBJECTID", detail.get("CUSTOMERNO").toString());
                dcp_demand.add("EMPLOYEEID", detail.get("EMPLOYEEID").toString());
                dcp_demand.add("DEPARTID", detail.get("DEPARTID").toString());
                dcp_demand.add("BDATE", detail.get("BDATE").toString());

                dcp_demand.add("DELIVERYORGNO", detail.get("DELIVERYORGNO").toString());
                dcp_demand.add("DELIVERYWAREHOUSE", detail.get("DELIVERWAREHOUSE").toString());
                dcp_demand.add("STOCKOUTNOQTY", "0");
                dcp_demand.add("PRODUCEQTY", "0");

                dcp_demand.add("PURQTY", "0");
                dcp_demand.add("STOCKINQTY", "0");
                dcp_demand.add("STOCKOUTQTY", "0");

                dcp_demand.add("CLOSESTATUS", "0");
                dcp_demand.add("DISTRISTATUS", "00");
                dcp_demand.add("ISURGENT", "N");
                dcp_demand.add("ISADD", "N");

                dcp_demand.add("SUBMITTIME", detail.get("CONFIRMTIME").toString());
                dcp_demand.add("TEMPLATENO", detail.get("TEMPLATENO").toString());
                dcp_demand.add("ISMUSTALLOT", "N");
                dcp_demand.add("STATUS", "0");

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_DEMAND", dcp_demand)));


            }


        } else if (Constant.OPR_TYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {
            if (!"1".equals(status)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前单据状态不为1-已审核,不可取消审核!");
            }

            //● 订单已存在出货通知单且状态非“作废”不可取消！出货通知单：DCP_STOCKOUTNOTICE/DCP_STOCKOUTNOTICE_DETAIL
            String noticeStatus = oneData.get("NOTICESTATUS").toString();
            if (!"3".equals(noticeStatus)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单已存在出货通知单且状态非“作废”不可取消！");
            }
            String sStockOutStatus = oneData.get("SSTOCKOUTSTATUS").toString();
            //● 订单已存在销货单且状态非“作废”不可取消！销货单：DCP_SSTOCKOUT/DCP_SSTOCKOUT_DETAIL
            if (!"3".equals(sStockOutStatus)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单已存在销货单且状态非“作废”不可取消！!");
            }
            dcp_customerPOrder.add("CONFIRMOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_customerPOrder.add("CONFIRMOPNAME", DataValues.newString(req.getEmployeeName()));
            dcp_customerPOrder.add("CONFIRMTIME", DataValues.newDate(lastModifyTime));

            dcp_customerPOrder.add("STATUS", DataValues.newInteger(0));

            ColumnDataValue demandCondition = new ColumnDataValue();
            demandCondition.add("EID", req.geteId());
            demandCondition.add("ORDERNO", req.getRequest().getPOrderNo());
            demandCondition.add("ORDERTYPE", "2");

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_DEMAND", demandCondition)));

        } else if (Constant.OPR_TYPE_CANCEL.equals(req.getRequest().getOpType())) {
            if (!"0".equals(status)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前单据状态不为0-新建,不可作废!");
            }

            dcp_customerPOrder.add("CANCELBY", DataValues.newString(req.getEmployeeNo()));
            dcp_customerPOrder.add("CANCELTIME", DataValues.newDate(lastModifyTime));

            dcp_customerPOrder.add("STATUS", DataValues.newInteger(3));


        } else if (Constant.OPR_TYPE_CLOSE.equals(req.getRequest().getOpType())) {
            if (!"1".equals(status)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前单据状态不为1-已审核,不可结案!");
            }

            dcp_customerPOrder.add("CLOSEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_customerPOrder.add("CLOSETIME", DataValues.newDate(lastModifyTime));

            dcp_customerPOrder.add("STATUS", DataValues.newInteger(2));
        }

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_CUSTOMERPORDER", condition, dcp_customerPOrder)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    protected String getQueryDetail(DCP_CustomerPOrderStatusUpdateReq req) throws Exception {

        String querySql = " SELECT b.*," +
                " a.CUSTOMERNO,a.ORGANIZATIONNO,a.RDATE,a.BDATE,a.TEMPLATENO," +
                " a.PAYTYPE,a.PAYORGNO,a.CONFIRMTIME,a.EMPLOYEEID,a.DEPARTID " +
                " FROM DCP_CUSTOMERPORDER a " +
                " INNER JOIN DCP_CUSTOMERPORDER_DETAIL b on a.eid=b.eid and a.PORDERNO=b.PORDERNO and a.SHOPNO=b.SHOPNO ";
        if (StringUtils.isNotEmpty(req.getRequest().getEId())) {
            querySql += " WHERE a.EID = '" + req.getRequest().getEId() + "'";
        } else {
            querySql += " WHERE a.EID = '" + req.geteId() + "' ";
        }

        if (StringUtils.isNotEmpty(req.getRequest().getShopId())) {
            querySql += " AND a.SHOPNO = '" + req.getRequest().getShopId() + "'";
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPOrderNo())) {
            querySql += " AND a.PORDERNO = '" + req.getRequest().getPOrderNo() + "'";
        }

        return querySql;

    }


    @Override
    protected String getQuerySql(DCP_CustomerPOrderStatusUpdateReq req) throws Exception {
        String querySql = " SELECT a.EID,a.SHOPNO,a.PORDERNO,a.ORGANIZATIONNO,a.STATUS " +
                "  ,NVL(c.STATUS,3) as NOTICESTATUS " +
                "  ,NVL(e.STATUS,3) as SSTOCKOUTSTATUS " +
                " FROM DCP_CUSTOMERPORDER a " +
                " LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL b ON a.eid=b.EID and b.SOURCEBILLNO=a.PORDERNO and b.SOURCETYPE='4' " +
                " LEFT JOIN DCP_STOCKOUTNOTICE c on c.eid=b.eid and c.BILLNO=b.BILLNO " +
                " LEFT JOIN DCP_SSTOCKOUT_DETAIL d on d.eid = a.eid and d.OFNO=a.PORDERNO and d.OTYPE='5' " +
                " left join DCP_SSTOCKOUT e on d.eid = e.EID and e.SSTOCKOUTNO=d.SSTOCKOUTNO and e.SHOPID=d.SHOPID and e.ORGANIZATIONNO=d.ORGANIZATIONNO ";

        if (StringUtils.isNotEmpty(req.getRequest().getEId())) {
            querySql += " WHERE a.EID = '" + req.getRequest().getEId() + "'";
        } else {
            querySql += " WHERE a.EID = '" + req.geteId() + "' ";
        }

        if (StringUtils.isNotEmpty(req.getRequest().getShopId())) {
            querySql += " AND a.SHOPNO = '" + req.getRequest().getShopId() + "'";
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPOrderNo())) {
            querySql += " AND a.PORDERNO = '" + req.getRequest().getPOrderNo() + "'";
        }

        return querySql;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerPOrderStatusUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerPOrderStatusUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerPOrderStatusUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CustomerPOrderStatusUpdateReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPOrderStatusUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_CustomerPOrderStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_CustomerPOrderStatusUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_CustomerPOrderStatusUpdateRes();
    }


}
