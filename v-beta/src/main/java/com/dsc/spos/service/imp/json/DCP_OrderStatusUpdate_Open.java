package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderShippingReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderShippingRes;
import com.dsc.spos.json.cust.res.DCP_OrderStatusUpdate_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_OrderStatusUpdate_Open
 * 說明：订单状态变更
 * 服务说明：订单状态变更
 *
 * @author wangzyc
 * @since 2021-5-11
 */
public class DCP_OrderStatusUpdate_Open extends SPosAdvanceService<DCP_OrderStatusUpdate_OpenReq, DCP_OrderStatusUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_OrderStatusUpdate_OpenReq req, DCP_OrderStatusUpdate_OpenRes res) throws Exception {
        DCP_OrderStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        String eId = request.getEId();
        String deliveryStatus = request.getDeliveryStatus(); // 0: 取消配送 / 1： 订单状态=2 整单定转销
        String orderNo = request.getOrderNo();
        String loadDocType = request.getLoadDocType();



        // 配送员
        String delId = request.getDelId();
        String delName = request.getDelName();
        String delTelephone = request.getDelTelephone();

        // 物流
        String deliveryType = request.getDeliveryType();
        String deliveryNo = request.getDeliveryNo();

        String status = request.getStatus();
//        Boolean nRet = true;
//        StringBuffer error = new StringBuffer("");

        try {
            if (deliveryStatus.equals("0")) {
                // 取消配送
                ////修改订单单头
                UptBean ub = new UptBean("DCP_ORDER");
                ub.addUpdateValue("DELID", new DataValue("", Types.VARCHAR));
                ub.addUpdateValue("DELNAME", new DataValue("", Types.VARCHAR));
                ub.addUpdateValue("DELTELEPHONE", new DataValue("", Types.VARCHAR));
                ub.addUpdateValue("DELIVERYTYPE", new DataValue("", Types.VARCHAR));
                ub.addUpdateValue("DELIVERYNO", new DataValue("", Types.VARCHAR));
                if (!Check.Null(status)) {
                    ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                }
                if (!Check.Null(deliveryNo)) {
                    ub.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
                }
                ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
    			ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));

            } else {
                ////修改订单单头
                UptBean ub = new UptBean("DCP_ORDER");
                ub.addUpdateValue("DELID", new DataValue(delId, Types.VARCHAR));
                ub.addUpdateValue("DELNAME", new DataValue(delName, Types.VARCHAR));
                ub.addUpdateValue("DELTELEPHONE", new DataValue(delTelephone, Types.VARCHAR));
                ub.addUpdateValue("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
                ub.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
                ub.addUpdateValue("DELIVERYSTATUS", new DataValue(deliveryStatus, Types.VARCHAR));
                if (!Check.Null(status)) {
                    ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                }
                if (!Check.Null(deliveryNo)) {
                    ub.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo, Types.VARCHAR));
                }


                // 查询订单 status  状态
                //查询下订单状态
                String sql_head = "select * from dcp_order where orderno='" + orderNo + "' and eid='" + eId + "' ";
                List<Map<String, Object>> getOrderHead = this.doQueryData(sql_head, null);
                if (!CollectionUtils.isEmpty(getOrderHead)) {
                    Map<String, Object> map = getOrderHead.get(0);
                    String status2 = map.get("STATUS").toString();// 查出来的数据只有一条

                    if (deliveryStatus.equals("1")) {
                        // 若所传物流状态deliveryStatus == 1 ,  查询出订单状态为 status == 2，则该单需整单订转销；   若订单状态 status != 2, 则不做订转销处理。
                        if (status2.equals("2")) {
                            // Status ==2 需整单订转销
                            String logStart = "KDS订单 物流状态为1，订单状态为2,自动订转销，单号orderNo=" + orderNo + ",";

//                        //1.深拷贝一份源服务请求
                            ParseJson pj = new ParseJson();
                            String jsonReq = pj.beanToJson(req);
                            DCP_OrderShippingReq dcp_OrderShippingReq = pj.jsonToBean(jsonReq, new TypeToken<DCP_OrderShippingReq>() {
                            });
                            //2.目标服务部分字段需重新给值
                            dcp_OrderShippingReq.setServiceId("DCP_OrderShipping");
                            dcp_OrderShippingReq.getRequest().setOrderList(new String[]{orderNo});
                            dcp_OrderShippingReq.getRequest().setOpType("1");
                            //3.调用目标服务
                            DCP_OrderShipping dcp_OrderShipping = new DCP_OrderShipping();
                            DCP_OrderShippingRes dcp_OrderShipping_Res = new DCP_OrderShippingRes();
                            dcp_OrderShipping.setDao(this.dao);
                            dcp_OrderShipping.processDUID(dcp_OrderShippingReq, dcp_OrderShipping_Res);

                            //4.处理服务返回
                            //*注意
                            //*res不能重新初始化，会导致赋值返回无效，就像下面这句不能用
                            //res=pj.jsonToBean(jsonRes,new TypeToken<DCP_OrderShippingRes>(){});
                            res.setSuccess(dcp_OrderShipping_Res.isSuccess());
                            res.setServiceStatus(dcp_OrderShipping_Res.getServiceStatus());
                            res.setServiceDescription(dcp_OrderShipping_Res.getServiceDescription());
                            dcp_OrderShipping = null;
                            pj = null;
                            dcp_OrderShipping_Res = null;

                        }
                    }

                    if (deliveryStatus.equals("3")) {
                        // 若所传物流状态deliveryStatus==3，查询出订单状态为status==10，则记录送达时间DCP_ORDER.DELIVERYTIME；
                        if(status2.equals("10")){
                            // Status ==10 需记录送达时间
                            ub.addUpdateValue("DELIVERYTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.DATE));
                        }
                    }
                }

                ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
    			ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));

            }
            this.doExecuteDataToDB();//
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功!");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderStatusUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_OrderStatusUpdate_OpenReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getEId())) {
            errMsg.append("企业编号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getOpNo())) {
            errMsg.append("操作人编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getOpName())) {
            errMsg.append("操作人名称不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getOrderNo())) {
            errMsg.append("订单号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getLoadDocType())) {
            errMsg.append("来源类型不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderStatusUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderStatusUpdate_OpenReq>() {
        };
    }

    @Override
    protected DCP_OrderStatusUpdate_OpenRes getResponseType() {
        return new DCP_OrderStatusUpdate_OpenRes();
    }
}
