package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderMealOut_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderMealOut_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.*;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
public class DCP_OrderMealOut_Open extends SPosAdvanceService<DCP_OrderMealOut_OpenReq, DCP_OrderMealOut_OpenRes> {

    @Override
    protected void processDUID(DCP_OrderMealOut_OpenReq req, DCP_OrderMealOut_OpenRes res)
            throws Exception {
        // TODO Auto-generated method stub
        String eId = req.getRequest().geteId();
        if (eId == null || eId.isEmpty()) {
            eId = req.geteId();
        }
        if (eId == null || eId.isEmpty()) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败，");
        }
        req.seteId(eId);
        req.getRequest().seteId(eId);

        String orderNo = req.getRequest().getOrderNo();
        //String loadDocType = req.getRequest().getLoadDocType();
        String shopId = req.getRequest().getShopId();
        if (shopId == null) {
            shopId = "";
        }
        String opNo = req.getRequest().getOpNo();
        if (opNo == null) {
            opNo = "";
        }
        String opName = req.getRequest().getOpName();
        if (opName == null) {
            opName = "";
        }


        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from dcp_order where eid='" + eId + "' and orderno='" + orderNo + "' ");
        String sql = sqlbuf.toString();
        String orderStatus = "";//订单状态
        String orderShop = "";//数据库里面下单门店
        boolean nResult = false;
        StringBuilder meassgeInfo = new StringBuilder();
        HelpTools.writelog_waimai("【第三方调用DCP_OrderMealOut_Open接口，确认出餐接口】查询开始：orderNo=" + orderNo + ",传入参数操作门店shopId=" + shopId + ",查询语句：" + sql);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
        if (getQDataDetail == null || getQDataDetail.isEmpty()) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("该订单不存在");
            HelpTools.writelog_waimai("【第三方调用DCP_OrderMealOut_Open接口，确认出餐】查询完成：该订单不存在！ 单号orderNo=" + orderNo);
            return;
        }
        String mealStatus = getQDataDetail.get(0).getOrDefault("MEALSTATUS","").toString();
        if ("1".equals(mealStatus)) {
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("该订单已出餐，无需再出餐");
            return;
        }

        orderStatus = getQDataDetail.get(0).get("STATUS").toString();

        orderShop = getQDataDetail.get(0).get("SHOP").toString();
        String orderShopName = getQDataDetail.get(0).getOrDefault("SHOPNAME", "").toString();
        String loadDocType = getQDataDetail.get(0).getOrDefault("LOADDOCTYPE", "").toString();
        String channelId = getQDataDetail.get(0).getOrDefault("CHANNELID", "").toString();
        String app_poi_code = getQDataDetail.get(0).getOrDefault("ORDERSHOP", "").toString();
        HelpTools.writelog_waimai("【第三方调用DCP_OrderMealOut_Open接口，确认出餐】查询完成：单号orderNo=" + orderNo + " 数据库中下单门店shop=" + orderShop + " 订单状态status=" + orderStatus);

        //防止没有传更新的节点，那么就不用执行语句
        boolean isNeedUpdate = false;
        String logmemo = "确认已出餐";

        UptBean ub1 = null;
        ub1 = new UptBean("DCP_ORDER");

        //condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));


        String updateDBStatus = "";
        StringBuilder errorMeassge = new StringBuilder();


        if (orderLoadDocType.ELEME.equals(loadDocType))//饿了么
        {
            //errorMeassge.append("饿了么接口还没实现！");
            Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, orderShop, loadDocType,app_poi_code);
            if (map != null) {
                String elmAPPKey = map.get("APPKEY").toString();
                String elmAPPSecret = map.get("APPSECRET").toString();
                String elmAPPName = map.get("APPNAME").toString();
                String elmIsTest = map.get("ISTEST").toString();
                boolean elmIsSandbox = false;
                if (elmIsTest != null && elmIsTest.equals("Y")) {
                    elmIsSandbox = true;
                }
                String isJBP = map.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
                String userId = map.getOrDefault("USERID","").toString();
                if ("Y".equals(isJBP))
                {
                    nResult = WMELMOrderProcess.orderPrepared(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo, errorMeassge);
                }
                else
                {
                    nResult = WMELMOrderProcess.orderPrepared(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo, errorMeassge);
                }

            } else {
                nResult = WMELMOrderProcess.orderPrepared(orderNo, errorMeassge);
            }


        } else if (orderLoadDocType.MEITUAN.equals(loadDocType))//美团
        {
            //nResult = WMMTOrderProcess.orderPreparationMealComplete(orderNo, errorMeassge);

			Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, orderShop, loadDocType,app_poi_code);
			if (map == null)
			{
				HelpTools.writelog_waimai("【获取美团外卖映射门店对应的渠道参数】为空！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
			}
			String isJbp = map.get("ISJBP").toString();

			if (isJbp != null && isJbp.equals("Y"))//聚宝盆
			{
				nResult =	WMJBPOrderProcess.orderPreparationMealComplete(eId, orderShop, orderNo, errorMeassge);
			}
			else
			{
                nResult = WMMTOrderProcess.orderPreparationMealComplete(orderNo, errorMeassge);
			}

        }
        else if (orderLoadDocType.MTSG.equals(loadDocType))
        {
            //nResult = WMMTOrderProcess.orderPreparationMealComplete(orderNo, errorMeassge);
            logmemo = "拣货完成";
            Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, orderShop, loadDocType,app_poi_code);
            if (map == null)
            {
                HelpTools.writelog_waimai("【获取美团闪购映射门店对应的渠道参数】为空！");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【获取美团外卖映射门店对应的渠道参数】为空！");
            }

            nResult = WMSGOrderProcess.orderPreparationMealComplete(orderNo, errorMeassge);

        }
        else {
            nResult = true;
        }

        if (!nResult) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("订单出餐失败:" + errorMeassge.toString());
            if (orderLoadDocType.MTSG.equals(loadDocType))
            {
                res.setServiceDescription("拣货失败:" + errorMeassge.toString());
            }

			HelpTools.writelog_waimai("【第三方调用DCP_OrderMealOut_Open接口，确认出餐】查出餐失败："+errorMeassge.toString()+",单号orderNo=" + orderNo);
            //region 写下日志
            try {
                //region 写订单日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType(loadDocType);
                onelv1.setLoadDocOrderNo(orderNo);
                onelv1.seteId(eId);
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(orderShop);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");
                String statusType = "99";//其他状态
                String updateStaus = "99";//订单修改
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                String statusName = "其他";
                String statusTypeName = "其他状态";
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                onelv1.setMemo("出餐失败<br>" + errorMeassge.toString());
                if (orderLoadDocType.MTSG.equals(loadDocType))
                {
                    onelv1.setMemo("拣货失败<br>" + errorMeassge.toString());
                }
                onelv1.setDisplay("0");
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);

                orderStatusLogList.add(onelv1);

                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
                //endregion
            } catch (Exception e) {
                HelpTools.writelog_waimai("【写表tv_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNo=" + orderNo);
            }
            //endregion
            return;
        }


        ub1.addUpdateValue("MEALSTATUS", new DataValue("1", Types.VARCHAR));
        ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
        ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));
        this.doExecuteDataToDB();
        HelpTools.writelog_waimai("【第三方调用DCP_OrderMealOut_Open接口，确认出餐】修改成功，单号OrderNo=" + orderNo);

        //region 写下日志
        try {
            //region 写订单日志
            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
            orderStatusLog onelv1 = new orderStatusLog();
            onelv1.setLoadDocType(loadDocType);
            onelv1.setChannelId(channelId);
            onelv1.setLoadDocBillType(loadDocType);
            onelv1.setLoadDocOrderNo(orderNo);
            onelv1.seteId(eId);
            onelv1.setOpName(opName);
            onelv1.setOpNo(opNo);
            onelv1.setShopNo(orderShop);
            onelv1.setOrderNo(orderNo);
            onelv1.setMachShopNo("");
            onelv1.setShippingShopNo("");
            String statusType = "99";//其他状态
            String updateStaus = "99";//订单修改
            onelv1.setStatusType(statusType);
            onelv1.setStatus(updateStaus);
            String statusName = "其他";
            String statusTypeName = "其他状态";
            onelv1.setStatusTypeName(statusTypeName);
            onelv1.setStatusName(statusName);
            onelv1.setMemo(logmemo);

            onelv1.setDisplay("1");

            String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            onelv1.setUpdate_time(updateDatetime);

            orderStatusLogList.add(onelv1);
            StringBuilder errorStatusLogMessage = new StringBuilder();
            HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
            //endregion
        } catch (Exception e) {
            HelpTools.writelog_waimai("【写表tv_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNo=" + orderNo);
        }
        //endregion


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderMealOut_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderMealOut_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderMealOut_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderMealOut_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        if (req.getRequest() == null) {
            errCt++;
            errMsg.append("请求节点request不存在, ");
            isFail = true;
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
    /*if (Check.Null(req.getRequest().geteId()))
	{
		errCt++;
		errMsg.append("请求节点eId不能为空, ");
		isFail = true;
	}*/
        if (Check.Null(req.getRequest().getOrderNo())) {
            errCt++;
            errMsg.append("请求节点orderNo不能为空, ");
            isFail = true;
        }
	/*if (Check.Null(req.getRequest().getLoadDocType()))
	{
		errCt++;
		errMsg.append("请求节点loadDocType不能为空, ");
		isFail = true;
	}*/

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_OrderMealOut_OpenReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrderMealOut_OpenReq>() {
        };
    }

    @Override
    protected DCP_OrderMealOut_OpenRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_OrderMealOut_OpenRes();
    }


    private void SaveRedis(String redis_key, String hash_key, String hash_value) throws Exception {
        try {
            RedisPosPub redis = new RedisPosPub();
            String Response_json = hash_value;
            //Response_json = Response_json.replace("\"[{", "[{").replace("}]\"", "}]").replace("\"{", "{").replace("}\"", "}");
            boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
            if (isexistHashkey) {
                redis.DeleteHkey(redis_key, hash_key);//
                HelpTools.writelog_waimai("【DCP_OrderMealOut_Open删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
            }
            HelpTools.writelog_waimai("【DCP_OrderMealOut_Open开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
            boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
            if (nret) {
                HelpTools.writelog_waimai("【DCP_OrderMealOut_Open写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            } else {
                HelpTools.writelog_waimai("【DCP_OrderMealOut_Open写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
            }
            //redis.Close();

        } catch (Exception e) {
            HelpTools.writelog_waimai("【DCP_OrderMealOut_Open写缓存】异常 " + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
        }
    }

}
