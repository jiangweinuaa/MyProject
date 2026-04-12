package com.dsc.spos.service.imp.json;

import cn.hutool.core.util.StrUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderOperateAgreeOrRejectReq;
import com.dsc.spos.json.cust.req.DCP_OrderProductionAgreeOrReject_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderOperateAgreeOrRejectRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_OrderOperateAgreeOrReject extends SPosAdvanceService<DCP_OrderOperateAgreeOrRejectReq, DCP_OrderOperateAgreeOrRejectRes> {
    @Override
    protected void processDUID(DCP_OrderOperateAgreeOrRejectReq req, DCP_OrderOperateAgreeOrRejectRes res) throws Exception {
        DCP_OrderOperateAgreeOrRejectRes.levRes resDatas = res.new levRes();
        resDatas.setErrorOrderList(new ArrayList<DCP_OrderOperateAgreeOrRejectRes.ErrorOrderList>());
        res.setDatas(resDatas);
        try
        {
            String eId = req.getRequest().geteId();
            String shopId = req.getRequest().getShopId();
            String opNo = req.getRequest().getOpNo();
            String opName = req.getRequest().getOpName();
            String opType = req.getRequest().getOperationType();
            List<DCP_OrderOperateAgreeOrRejectReq.OrderList> orderList = req.getRequest().getOrderList();
            List<String> sucessOrderList = new ArrayList<String>();//执行成功的 单号列表
            List<Map<String, Object>> failOrderList = new ArrayList<Map<String, Object>>();//失败的列表,以及错误描述
            String companyId = req.getBELFIRM();
            if(req.getApiUser()!=null){
                companyId = req.getApiUser().getCompanyId();
            }
            boolean isAllSucess = true;
            String logStrStart = "";
            //循环单号，后面不执行了
            for (DCP_OrderOperateAgreeOrRejectReq.OrderList orderNoList : orderList)
            {
                logStrStart = "";
                String orderNo = orderNoList.getOrderNo();
                logStrStart="循环操作【订单操作是否同意】单号orderNo="+orderNo+",";
                try
                {
                    String orderSql = " select a.*  from DCP_Order a "
                            + " where a.eid = '"+eId+"'  "
                            + " and a.OrderNo='"+orderNo+"' ";

                    List<Map<String, Object>> orderDatas = this.doQueryData(orderSql, null);
                    if(orderDatas==null||orderDatas.isEmpty())
                    {
                        HelpTools.writelog_waimai(logStrStart+"没有查询到该订单数据！");
                        isAllSucess = false;
                        Map<String, Object> errorMap = new HashMap<String, Object>();
                        errorMap.put("orderNo", orderNo);
                        errorMap.put("errorDesc", "没有查询到该订单数据！");
                        failOrderList.add(errorMap);
                        DCP_OrderOperateAgreeOrRejectRes.ErrorOrderList errorOrderList = res.new ErrorOrderList();
                        errorOrderList.setOrderNo(orderNo);
                        errorOrderList.setErrorDesc("没有查询到该订单数据！");
                        res.getDatas().getErrorOrderList().add(errorOrderList);
                        continue;
                    }
                    Map<String, Object> map = orderDatas.get(0);
                    //1下单机构，2生产机构，3配送机构  默认值2；
                    String typeOfOrg = PosPub.getPARA_SMS(dao,eId,"","TypeOfOrganization");
                    if (StrUtil.isEmpty(typeOfOrg))
                    {
                        typeOfOrg = "2";
                    }

                    String status = map.get("STATUS").toString();// 订单状态
                    String productStatus = map.get("PRODUCTSTATUS").toString();//生产状态
                    String loadDocType = map.get("LOADDOCTYPE").toString();
                    String channelId = map.get("CHANNELID").toString();
                    String shop_create = map.get("SHOP").toString();
                    String shopName_create = map.get("SHOPNAME").toString();
                    String shop_mach = map.get("MACHSHOP").toString();
                    String shopName_mach = map.get("MACHSHOPNAME").toString();
                    String shop_shipping = map.get("SHIPPINGSHOP").toString();
                    String shopName_shipping = map.get("SHIPPINGSHOPNAME").toString();
                    String str_error = "需要生产机构同意，请联系该门店";
                    boolean isCheckOrg = true;
                    if (shopId!=null&&shopId.trim().isEmpty()==false)
                    {
                        ////1下单机构，2生产机构，3配送机构  默认值2；
                        if ("1".equals(typeOfOrg))
                        {
                            if (shop_create!=null&&shop_create.trim().isEmpty()==false&&shopId.equals(shop_create)==false)
                            {
                                str_error = "需要下单机构("+shop_create+shopName_create+")同意，请联系该门店!";
                                isCheckOrg = false;
                            }
                        }
                        else if ("3".equals(typeOfOrg))
                        {
                            if (shop_shipping!=null&&shop_shipping.trim().isEmpty()==false&&shopId.equals(shop_shipping)==false)
                            {
                                str_error = "需要配送机构("+shop_shipping+shopName_shipping+")同意，请联系该门店!";
                                isCheckOrg = false;
                            }
                        }
                        else
                        {
                            if (shop_mach!=null&&shop_mach.trim().isEmpty()==false&&shopId.equals(shop_mach)==false)
                            {
                                str_error = "需要生产机构("+shop_mach+shopName_mach+")同意，请联系该门店!";
                                isCheckOrg = false;
                            }
                        }
                    }
                    if (!isCheckOrg)
                    {
                        HelpTools.writelog_waimai(logStrStart+str_error);
                        isAllSucess = false;
                        Map<String, Object> errorMap = new HashMap<String, Object>();
                        errorMap.put("orderNo", orderNo);
                        errorMap.put("errorDesc", str_error);
                        failOrderList.add(errorMap);
                        DCP_OrderOperateAgreeOrRejectRes.ErrorOrderList errorOrderList = res.new ErrorOrderList();
                        errorOrderList.setOrderNo(orderNo);
                        errorOrderList.setErrorDesc(str_error);
                        res.getDatas().getErrorOrderList().add(errorOrderList);
                        continue;
                    }


                    StringBuffer errMsg = new StringBuffer("");

                    UptBean up1 = new UptBean("DCP_ORDER");
                    up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));

                    //更新updatetime
                    up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                    // operationType  入参如果是1，则给dcp_order 里面字段operationType 设置为1；如果是2，则给dcp_order里面的字段operationType 设置为2
                    up1.addUpdateValue("OPERATIONTYPE", new DataValue(opType,Types.VARCHAR));

                    if ("1".equals(opType))
                    {
                        if ("4".equals(productStatus)||"5".equals(productStatus))
                        {
                            //生产状态(4生产接单；5生产拒单；6完工入库；7内部调拨）
                            up1.addUpdateValue("PRODUCTSTATUS", new DataValue("",Types.VARCHAR));
                        }
                    }

                    this.addProcessData(new DataProcessBean(up1));

                    this.doExecuteDataToDB();

                    //写日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                    orderStatusLog onelv1 = new orderStatusLog();
                    onelv1.setLoadDocType(loadDocType);
                    onelv1.setChannelId(channelId);
                    onelv1.setLoadDocBillType(map.get("LOADDOCBILLTYPE").toString());
                    onelv1.setLoadDocOrderNo(map.get("LOADDOCORDERNO").toString());
                    onelv1.seteId(eId);

                    onelv1.setOpName(opName);
                    onelv1.setOpNo(opNo);
                    onelv1.setShopNo(shopId);
                    onelv1.setOrderNo(orderNo);
                    onelv1.setMachShopNo(map.get("LOADDOCORDERNO").toString());
                    onelv1.setShippingShopNo(map.get("SHIPPINGSHOP").toString());
                    String statusType = "99";//订单状态
                    String updateStaus = "99"; //4-生产接单 ，5-生产拒单
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);

                    String statusName = "同意修改操作";
                    if ("2".equals(opType))
                    {
                        statusName = "同意退单操作";
                    }
                    String statusTypeName = "其他状态";
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);

                    String memo = "";
                    memo += statusName;
                    onelv1.setMemo(memo);
                    onelv1.setDisplay("0");

                    String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    onelv1.setUpdate_time(updateDatetime);

                    orderStatusLogList.add(onelv1);

                    StringBuilder errorStatusLogMessage = new StringBuilder();
                    boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                    if (nRet) {
                        HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                    } else {
                        HelpTools.writelog_waimai(
                                "【写表DCP_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                    }


                }
                catch (Exception e)
                {
                    isAllSucess = false;
                    Map<String, Object> errorMap = new HashMap<String, Object>();
                    errorMap.put("orderNo", orderNo);
                    errorMap.put("errorDesc", e.getMessage());
                    failOrderList.add(errorMap);
                    DCP_OrderOperateAgreeOrRejectRes.ErrorOrderList errorOrderList = res.new ErrorOrderList();
                    errorOrderList.setOrderNo(orderNo);
                    errorOrderList.setErrorDesc(e.getMessage());
                    res.getDatas().getErrorOrderList().add(errorOrderList);
                    continue;
                }
            }

            if(isAllSucess)
            {
                res.setSuccess(true);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行成功");
            }
            else
            {
                StringBuffer errorDescBuffer = new StringBuffer();
                //异常得单号先提示
                for (Map<String, Object> map : failOrderList)
                {
                    String orderNo = map.getOrDefault("orderNo", "").toString();
                    String errorDesc = map.getOrDefault("errorDesc", "").toString();
                    errorDescBuffer.append("单号:"+orderNo+",异常:"+errorDesc);
                }
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败！<br>"+errorDescBuffer.toString());

            }

        }
        catch (Exception e)
        {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:"+e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderOperateAgreeOrRejectReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderOperateAgreeOrRejectReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderOperateAgreeOrRejectReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderOperateAgreeOrRejectReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<DCP_OrderOperateAgreeOrRejectReq.OrderList> orderList = req.getRequest().getOrderList();
        if(orderList==null||orderList.isEmpty())
        {
            isFail = true;
            errMsg.append("orderList不能为空 ");
        }
        if(Check.Null(req.getRequest().getShopId()))
        {
            isFail = true;
            errMsg.append("shopId不能为空 ");
        }

        if(Check.Null(req.getRequest().getOperationType()))
        {
            isFail = true;
            errMsg.append("operationType不能为空 ");
        }
        else
        {
            if ("1".equals(req.getRequest().getOperationType())||"2".equals(req.getRequest().getOperationType()))
            {

            }
            else
            {
                isFail = true;
                errMsg.append("operationType参数值为[1,2] ");
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderOperateAgreeOrRejectReq> getRequestType() {
        return new TypeToken<DCP_OrderOperateAgreeOrRejectReq>(){};
    }

    @Override
    protected DCP_OrderOperateAgreeOrRejectRes getResponseType() {
        return new DCP_OrderOperateAgreeOrRejectRes();
    }
}
