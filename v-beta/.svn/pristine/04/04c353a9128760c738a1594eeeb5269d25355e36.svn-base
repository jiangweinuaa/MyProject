package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.*;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_OrderModifyReq;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess_OpenReq.Agio;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess_OpenReq.goods;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess_OpenReq.levelRequest;
import com.dsc.spos.json.cust.req.DCP_StockOrderLockDetail_OpenReq;
import com.dsc.spos.json.cust.req.DCP_StockUnlock_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderModifyRes;
import com.dsc.spos.json.cust.res.DCP_OrderToSaleProcess_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderToSaleProcess_OpenRes.Card;
import com.dsc.spos.json.cust.res.DCP_StockOrderLockDetail_OpenRes;
import com.dsc.spos.json.cust.res.DCP_StockUnlock_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.ApiUser;
import com.dsc.spos.model.JindieGoodsDetail;
import com.dsc.spos.model.SALE;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.thirdpart.qimai.QiMaiService;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackServiceV3;
import com.dsc.spos.utils.*;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_OrderToSaleProcess_Open extends SPosAdvanceService<DCP_OrderToSaleProcess_OpenReq,DCP_OrderToSaleProcess_OpenRes>
{
    Logger logger = LogManager.getLogger(DCP_OrderToSaleProcess_Open.class.getName());

    //【ID1030282】【大万3.0】车销业务场景下系统改造评估----绩效算到下单门店  by jinzma 20221226
    boolean isStockTransfer = false;          //是否创建调拨单
    String stockTransferShopId = "";          //调拨单调入门店
    String stockTransferShippingShopNo = "";  //调拨单调出门店
    String stockOutNo="";
    String stockInNo="";

    @Override
    protected void processDUID(DCP_OrderToSaleProcess_OpenReq req, DCP_OrderToSaleProcess_OpenRes res) throws Exception
    {
        StringBuilder errorMessage = new StringBuilder();
        boolean nRet;
        RedisPosPub rpp = new RedisPosPub();
        String checkKey="";
        checkKey="DCP_OrderToSaleProcess_Open:"+req.getRequest().getOrderNo();
        try
        {
            if(!rpp.IsExistStringKey(checkKey))
            {
                Calendar cal = Calendar.getInstance();// 获得当前时间
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createDate = df1.format(cal.getTime());
                rpp.setEx(checkKey,300,createDate);
            }else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "订单:"+req.getRequest().getOrderNo()+"正在处理中...请等候");
            }
            res.setDatas(res.new level1());
            res.getDatas().setVipDatas(res.new level2());
            res.getDatas().setInvoiceInfo(res.new invoice());

            //【ID1030282】【大万3.0】车销业务场景下系统改造评估----绩效算到下单门店  by jinzma 20221226 是否控制绩效算下单门店
            String eId = req.getRequest().geteId();
            String shippingShopNo = req.getRequest().getShippingShopNo();
            String shippingShopName = req.getRequest().getShippingShopName();
            String shopId = req.getRequest().getShopNo();
            String shopName = req.getRequest().getShopName();
            String payStatus = req.getRequest().getPayStatus();
            String orderNo = req.getRequest().getOrderNo();

            if ("3".equals(payStatus) && !Check.Null(shippingShopNo) && !Check.Null(shopId)){
                if (!shippingShopNo.equals(shopId)){
                    String PerformInOrderStore = PosPub.getPARA_SMS(dao,eId ,shippingShopNo,"PerformInOrderStore");
                    if (!Check.Null(PerformInOrderStore) && PerformInOrderStore.equals("Y")){
                        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店)开始,订单单号:"+orderNo+" \r\n");
                        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
                        //判断是否闭店
                        {
                            String sql = " select bdate from dcp_date a where a.eid='" + eId + "' and a.shopid='" + shopId + "' and bdate='" + accountDate + "' ";
                            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                            if (CollectionUtil.isEmpty(getQData)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店:" + shopId + " 闭店检查失败,dcp_date无当天数据,请重新确认! ");
                            }
                        }

                        //调用订单修改
                        {
                            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调用订单修改开始 \r\n");
                            DCP_OrderModifyReq orderModifyReq = new DCP_OrderModifyReq();
                            DCP_OrderModifyReq.levelRequest orderModifyReqRequest = orderModifyReq.new levelRequest();
                            orderModifyReqRequest.seteId(req.getRequest().geteId());
                            orderModifyReqRequest.setOrderNo(req.getRequest().getOrderNo());
                            orderModifyReqRequest.setLoadDocType(req.getRequest().getLoadDocType());
                            orderModifyReqRequest.setShippingShopNo(shopId);
                            orderModifyReqRequest.setShippingShopName(shopName);

                            orderModifyReq.setServiceId("DCP_OrderModify");
                            orderModifyReq.setRequest(orderModifyReqRequest);

                            DCP_OrderModify dcp_OrderModify = new DCP_OrderModify();
                            DCP_OrderModifyRes dcp_OrderModify_Res = new DCP_OrderModifyRes();
                            dcp_OrderModify.setDao(this.dao);
                            dcp_OrderModify.processDUID(orderModifyReq, dcp_OrderModify_Res);

                            if (!dcp_OrderModify_Res.isSuccess()) {
                                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调用订单修改失败,原因:"+dcp_OrderModify_Res.getServiceDescription()+" \r\n");
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单修改返回失败"+dcp_OrderModify_Res.getServiceDescription());
                            }
                            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调用订单修改结束 \r\n");
                        }


                        //调用订单转销售
                        {
                            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调用订单转销售开始 \r\n");
                            req.getRequest().setShippingShopNo(shopId);
                            req.getRequest().setShippingShopName(shopName);
                            req.getRequest().setbDate(accountDate);
                            
                            /*String sql = " select warehouse,warehousename from dcp_order_detail where eid='" + eId + "' and orderno='" + orderNo + "' ";
                            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                            if (CollectionUtil.isEmpty(getQData)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单:" + orderNo + " 缺订单商品明细资料,请重新确认! ");
                            }
                            for (goods par : req.getRequest().getGoodsList()) {
                                par.setWarehouse(getQData.get(0).get("WAREHOUSE").toString());
                                par.setWarehouseName(getQData.get(0).get("WAREHOUSENAME").toString());
                            }*/
                            isStockTransfer=true;                          //销售单产生调拨单
                            stockTransferShopId = shopId;                  //调拨单调入门店
                            stockTransferShippingShopNo = shippingShopNo;  //调拨单调出门店
                            if (!ecOrderToSale(req, errorMessage, res)){
                                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调用订单转销售失败,原因:"+errorMessage+" \r\n");
                                //撤回订单修改
                                {
                                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",订转销失败,撤回订单修改开始 \r\n");
                                    DCP_OrderModifyReq orderModifyReq = new DCP_OrderModifyReq();
                                    DCP_OrderModifyReq.levelRequest orderModifyReqRequest = orderModifyReq.new levelRequest();
                                    orderModifyReqRequest.seteId(req.getRequest().geteId());
                                    orderModifyReqRequest.setOrderNo(req.getRequest().getOrderNo());
                                    orderModifyReqRequest.setLoadDocType(req.getRequest().getLoadDocType());
                                    orderModifyReqRequest.setShippingShopNo(shippingShopNo);
                                    orderModifyReqRequest.setShippingShopName(shippingShopName);

                                    orderModifyReq.setServiceId("DCP_OrderModify");
                                    orderModifyReq.setRequest(orderModifyReqRequest);

                                    DCP_OrderModify dcp_OrderModify = new DCP_OrderModify();
                                    DCP_OrderModifyRes dcp_OrderModify_Res = new DCP_OrderModifyRes();
                                    dcp_OrderModify.setDao(this.dao);
                                    dcp_OrderModify.processDUID(orderModifyReq, dcp_OrderModify_Res);

                                    if (!dcp_OrderModify_Res.isSuccess()) {
                                        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",订转销失败,撤回订单修改失败,原因:"+dcp_OrderModify_Res.getServiceDescription()+" \r\n");
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "撤回订单修改返回失败"+dcp_OrderModify_Res.getServiceDescription());
                                    }
                                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",订转销失败,撤回订单修改结束 \r\n");
                                }


                                res.setSuccess(false);
                                res.setServiceDescription(errorMessage.toString());
                                return;
                            }
                            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调用订单转销售结束 \r\n");
                        }


                        //产生调拨单
                        /*{
                            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调拨单创建开始 \r\n");
                            
                            String saleNo = res.getDatas().getBillNo();
                            if (!stockTransfer(req,eId,shopId,shippingShopNo,saleNo,errorMessage)) {
                                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调拨单创建失败,原因:"+errorMessage+" \r\n");
                                
                                res.setSuccess(false);
                                res.setServiceDescription(errorMessage.toString());
                                return;
                            }
                            
                            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调拨单创建结束 \r\n");
                        }*/



                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功！");
                        return;
                    }
                }
            }



            nRet = ecOrderToSale(req,errorMessage,res);

            if (nRet)
            {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功！");
                return;
            }
            else
            {
                res.setSuccess(false);
                res.setServiceDescription(errorMessage.toString());
                return;
            }
        }
        catch ( Exception e)
        {

            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                PosPub.WriteETLJOBLog("X1： "+ errors.toString());



                pw=null;
                errors=null;
            }
            catch (Exception eX)
            {

            }


            res.setSuccess(false);
            res.setServiceDescription(e.getMessage());
            return;
        }finally
        {
            rpp.DeleteKey(checkKey);
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderToSaleProcess_OpenReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderToSaleProcess_OpenReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderToSaleProcess_OpenReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderToSaleProcess_OpenReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().geteId()) )
        {
            errMsg.append("企业编码eId不可为空值, ");
            isFail = true;
        }

        List<DCP_OrderToSaleProcess_OpenReq.goods> goodsList=req.getRequest().getGoodsList();

        if (goodsList==null || goodsList.size()==0)
        {
            errMsg.append("商品列表goodsList不可为空值, ");
            isFail = true;
        }

        for (DCP_OrderToSaleProcess_OpenReq.goods goods : goodsList)
        {
            if (Check.Null(goods.getItem()) )
            {
                errMsg.append("商品项次item不可为空值, ");
                isFail = true;
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderToSaleProcess_OpenReq> getRequestType()
    {

        return new TypeToken<DCP_OrderToSaleProcess_OpenReq>(){};
    }

    @Override
    protected DCP_OrderToSaleProcess_OpenRes getResponseType()
    {

        return new DCP_OrderToSaleProcess_OpenRes();
    }


    private boolean ecOrderToSale(DCP_OrderToSaleProcess_OpenReq req , StringBuilder errorMessage,DCP_OrderToSaleProcess_OpenRes res )
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate=df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String sTime=df.format(cal.getTime());

        //
        df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String sDatetimeMs=df.format(cal.getTime());


        levelRequest otpReq=req.getRequest();

        SALE sale =null;

        List<DataProcessBean> data = new ArrayList<DataProcessBean>();

        //发票试算成功标记(如果发票试算成功,销售单保存失败,需要调用删除试算接口)
        boolean bInvoiceCal=false;
        //POS服务地址
        String posUrl="";
        String saleno="";
        String memberOrderno=PosPub.getGUID(false);//调用积分memberpay的orderno
        //会员支付
        boolean bMemberPay=false;//调用会员支付标记

        String Yc_Url="";
        String Yc_Key="";
        String Yc_Sign_Key="";
        //外部服务直接取，内部服务下面要查数据库
        if (req.getApiUser() != null)
        {
            Yc_Key=req.getApiUserCode();
            Yc_Sign_Key=req.getApiUser().getUserKey();
        }

        String AreaType="";//台湾环境判断

        //有尾款折扣标记，就要更新订单资料
        boolean bFinalPayDisc=false;

        //尾款付清标记，有尾款且尾款付清才更新单头的付款状态
        boolean bPayComplete=false;
        boolean isNeedMemberpay=true;//是否需要调用memberpay

        try
        {
            sale = new SALE();
            String eId=otpReq.geteId();
            String shopId = otpReq.getShippingShopNo();
            String orderNo=otpReq.getOrderNo();
            String LogStatus="";


            AreaType = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "AreaType");

            String sql_Order = " select *  from DCP_ORDER  where EID='"+eId+"' and orderno='"+orderNo+"' ";
            List<Map<String, Object>> getQData_Order = this.doQueryData(sql_Order,null);
            if (getQData_Order == null || getQData_Order.isEmpty())
            {
                errorMessage.append("订单表找不到此订单信息 !"  );
                return false;
            }
            if (req.getRequest().getOpShopId()!=null&&!req.getRequest().getOpShopId().isEmpty())
            {
                String shippingShop = getQData_Order.get(0).getOrDefault("SHIPPINGSHOP", "").toString();
                if (!req.getRequest().getOpShopId().equals(shippingShop))
                {
                    errorMessage.append("订单号："+orderNo+"，当前操作订转销的门店("+req.getRequest().getOpShopId()+")非取货门店，请到取货门店("+shippingShop+")操作订转销");
                    return false;
                }
            }

            //部分订转销检核判断 begin
            String isOrderToSaleAll=otpReq.getIsOrderToSaleAll();//1:部分订转销售
            if(Check.Null(isOrderToSaleAll))
            {
                isOrderToSaleAll="0";
            }
            if("1".equals(isOrderToSaleAll) )
            {
                if(null==otpReq.getGoodsList() || otpReq.getGoodsList().isEmpty())
                {

                    errorMessage.append("订单已经没有需要订转销的商品了,请核实订转销出货信息!"  );
                    return false;
                }
            }
            if(!Check.Null(getQData_Order.get(0).getOrDefault("ORDERTOSALE_DATETIME","").toString())
                    &&("11".equals(getQData_Order.get(0).getOrDefault("STATUS","").toString())
                    ||"10".equals(getQData_Order.get(0).getOrDefault("STATUS","").toString())))
            //已经做过订转销,但无法确定所有商品都已经做了订转销
            {
                String sql="select *  from DCP_ORDER_DETAIL  where EID='"+eId+"' and orderno='"+orderNo+"'"
                        + " AND QTY-PICKQTY-RQTY<>0 ";
                List<Map<String, Object>> getQData_OrderDetail = this.doQueryData(sql,null);
                if (getQData_OrderDetail == null || getQData_OrderDetail.isEmpty())
                {
                    errorMessage.append("订单已经没有需要订转销的商品了,请核实订转销出货信息!"  );
                    return false;
                }
            }
            String loadDocType=getQData_Order.get(0).getOrDefault("LOADDOCTYPE","").toString();
            String channelId =getQData_Order.get(0).getOrDefault("CHANNELID","").toString();
            if(orderLoadDocType.QIMAI.equals(loadDocType)||orderLoadDocType.XIAOYOU.equals(loadDocType)||"WEIPINKE".equals(loadDocType)){
                isNeedMemberpay=false;
            }
            String canModify=getQData_Order.get(0).getOrDefault("CANMODIFY","").toString();
            if ("Y".equals(canModify))
            {
                if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
                {
                    errorMessage.append("订单信息未完整，请先到pos完成信息补录！");
                    return false;
                }
            }
            if(isOrderToSaleAll.equals("1"))
            {
                if(otpReq.getPay().size()>0)
                {
                    errorMessage.append("分批订转销,不可收尾款,请检查资料!!"  );
                    return false;
                }else
                {
                    if(!getQData_Order.get(0).getOrDefault("PAYSTATUS","1").toString().equals("3"))
                    {
                        errorMessage.append("未付全款不能分批订转销！！");
                        return false;
                    }
                }
                if(loadDocType.equals("POS")
                        || loadDocType.equals("POSANDROID")
                        || loadDocType.equals("WECHAT")
                        || loadDocType.equals("MINI"))
                {

                }else
                {
                    errorMessage.append("渠道类型:"+loadDocType+"不支持分批订转销");
                    return false;
                }
//                if(loadDocType.equals("MEITUAN")||loadDocType.equals("ELEME") ||loadDocType.equals("JDDJ")             )
//                {
//                    errorMessage.append("渠道类型:"+loadDocType+"不支持分批订转销");
//                    return false;
//                }
            }
            //部分订转销检核判断 end
            if(isOrderToSaleAll.equals("1"))//根据请求内容的商品项次及数量补充重算请求内容
            {
                if(!afreshAssign(otpReq, errorMessage))
                {
                    return false;
                }

            }
            //判断 订转销，是否需要订单调拨
            if(loadDocType.equals("POS") || loadDocType.equals("POSANDROID") || loadDocType.equals("WECHAT") || loadDocType.equals("MINI"))
            {
                String productStatus = getQData_Order.get(0).getOrDefault("PRODUCTSTATUS", "").toString();//生产状态
                String machshop = getQData_Order.get(0).getOrDefault("MACHSHOP", "").toString();//生产门店
                String shippingShop = getQData_Order.get(0).getOrDefault("SHIPPINGSHOP", "").toString();
                if(!productStatus.trim().isEmpty())
                {
                    //是否需要生产控制” ControlProduction    Y/N 默认值N；
                    String ControlProduction = PosPub.getPARA_SMS(this.dao,eId,"","ControlProduction");
                    if ("Y".equals(ControlProduction))
                    {
                        //订单上商品属性不需要生产的，不用控制
                        boolean needControl = true;//默认为true，需要控制，必须要这样防止，sql查询异常，一定要默认控制
                        try
                        {

                            String sql_goodsType = " select B.PLUNO,C.GOODSTYPE from dcp_order_Detail b left join dcp_goods c on b.eid=c.eid and b.pluno=c.pluno " +
                                    "  where b.eid='"+eId+"' and b.orderno='"+orderNo+"' ";
                            HelpTools.writelog_waimai("订单需要生产控制时，订转销查询订单商品属性是否需要生产sql="+sql_goodsType);
                            List<Map<String, Object>> getQData_goodsTypeList = this.doQueryData(sql_goodsType, null);
                            if (getQData_goodsTypeList!=null&&!getQData_goodsTypeList.isEmpty())
                            {
                                boolean allGoodsNoProduct = true;//默认所有商品，都不用生产
                                for (Map<String, Object> mapGoodsType : getQData_goodsTypeList)
                                {
                                    String goodsType = mapGoodsType.getOrDefault("GOODSTYPE","").toString();//1-不需要生产；2或者空需要生产
                                    if (!"1".equals(goodsType))
                                    {
                                        //只要存在需要生产商品
                                        allGoodsNoProduct = false;
                                        break;
                                    }

                                }

                                if (allGoodsNoProduct)
                                {
                                    needControl = false;
                                }
                            }



                        }
                        catch (Exception e)
                        {

                        }
                        if (needControl)
                        {
                            //生产和配送门店一直，必须操作生产完成
                            if (machshop.equals(shippingShop))
                            {
                                if (!"6".equals(productStatus))
                                {

                                    errorMessage.append("该订单还未生产完成不能提货，请联系生产门店，生产门店:"+machshop  );
                                    return false;
                                }
                            }
                            else
                            {
                                if (!"7".equals(productStatus))
                                {

                                    errorMessage.append("该订单还未调拨不能提货，请联系生产门店，生产门店:"+machshop  );
                                    return false;
                                }
                            }
                        }
                        else
                        {
                            HelpTools.writelog_waimai("订单需要生产控制时，订转销查询订单商品明细都不需要生产，不用操作订单生产完成或者内部调拨!订单号orderNo="+orderNo);
                        }

                    }

                }
            }

            //云中台渠道类型，需要调用CRM会员接口，如果是内部调用获取下接口账号
            if (orderLoadDocType.OWNCHANNEL.equals(req.getRequest().getLoadDocType()))
            {
                if (req.getApiUser()==null)
                {
                    ApiUser apiUser = PosPub.getApiUserByChannelId(this.dao,req.getRequest().geteId(),req.getRequest().getChannelId());
                    if (apiUser!=null)
                    {
                        req.setApiUser(apiUser);
                        req.setApiUserCode(apiUser.getUserCode());
                        Yc_Key=req.getApiUserCode();
                        Yc_Sign_Key=req.getApiUser().getUserKey();
                    }

                }

            }

            //isMerPay配送费是否商家结算
            String isMerPay=getQData_Order.get(0).get("ISMERPAY").toString();
            if (Check.Null(isMerPay)) isMerPay="N";

            //取下数据库里面的，
            String departNo_DB = getQData_Order.get(0).getOrDefault("DEPARTNO", "").toString();
            String departNo = otpReq.getDepartNo();
            if(departNo==null||departNo.isEmpty())
            {
                departNo = departNo_DB;
            }

            //
            otpReq.setStatus(getQData_Order.get(0).get("STATUS").toString());
            otpReq.setRefundStatus(getQData_Order.get(0).get("REFUNDSTATUS").toString());
            otpReq.setPayStatus(getQData_Order.get(0).get("PAYSTATUS").toString());
            if(!isOrderToSaleAll.equals("1"))
            {
                otpReq.setPayAmt(getQData_Order.get(0).get("PAYAMT").toString());
                otpReq.setTot_Amt(getQData_Order.get(0).get("TOT_AMT").toString());
            }

            if (PosPub.isNumericTypeMinus(otpReq.getPackageFee())==false) otpReq.setPackageFee("0");
            if (PosPub.isNumericTypeMinus(otpReq.getTot_shipFee())==false) otpReq.setTot_shipFee("0");

            if (Check.Null(orderNo))
            {
                errorMessage.append("企业编号：" + eId + " 门店编号："+shopId + "订单单号orderNo未设置 !"  );
                return false;
            }

            //前端必须给值
            if (Check.Null(otpReq.getOpShopId()))
            {
                errorMessage.append("操作门店opShopId不能为空 !"  );
                return false;
            }

            //获取默认出货仓
            String outCostWareHouse = getOutCostWareHouse(eId,shopId) ;
            if (Check.Null(outCostWareHouse))
            {
                errorMessage.append("企业编号：" + eId + " 门店编号："+shopId + "，组织未生效或组织默认出货仓DCP_ORG.OUT_COST_WAREHOUSE未设置 !"  );
                return false;
            }
            String sd_warehouse = outCostWareHouse;

            saleno="TV"+orderNo;
            if(isOrderToSaleAll.equals("1"))
            {
                saleno=getOrderToSaleNO(eId,orderNo);
            }
            //
            if (getExistSaleno(eId, shopId, saleno))
            {
                errorMessage.append("企业编号：" + eId + " 门店编号："+shopId + "的销售单已经存在 !"  );
                return false;
            }

            //取入账日期-销售单
            String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

            if (Check.Null(otpReq.getbDate()))otpReq.setbDate(accountDate);

            //不能小于入账日期
            if (otpReq.getbDate().compareTo(accountDate)<0)
            {
                errorMessage.append("营业日期bdate("+otpReq.getbDate()+")不能小于库存入账日期("+accountDate+")！");
                return false;
            }


            if (Check.Null(otpReq.getOpMachineNo()))otpReq.setOpMachineNo("999");
            if (Check.Null(otpReq.getVerNum()))otpReq.setVerNum("v3.0");
            if (Check.Null(otpReq.getDeliveryHandinput()))otpReq.setDeliveryHandinput("Y");

            if (Check.Null(otpReq.getDeliveryType()))otpReq.setDeliveryType(getQData_Order.get(0).get("DELIVERYTYPE").toString());
            if (Check.Null(otpReq.getDeliveryNo()))otpReq.setDeliveryNo(getQData_Order.get(0).get("DELIVERYNO").toString());
            if (Check.Null(otpReq.getSubDeliveryCompanyName()))otpReq.setSubDeliveryCompanyName(getQData_Order.get(0).get("SUBDELIVERYCOMPANYNAME").toString());
            if (Check.Null(otpReq.getSubDeliveryCompanyNo()))otpReq.setSubDeliveryCompanyNo(getQData_Order.get(0).get("SUBDELIVERYCOMPANYNO").toString());

            //取trno
            String trno=getTRNO(eId, shopId, otpReq.getbDate(),saleno,req);
            if (Check.Null(trno))
            {
                errorMessage.append("计算销售单最大流水号TRNO报错 !"  );
                return false;
            }

            //*******字段值处理**********
            if (otpReq.getHeadOrderNo()==null) otpReq.setHeadOrderNo("");
            if (otpReq.getDetailType()==null) otpReq.setDetailType("1");
            if (otpReq.getTot_Amt()==null) otpReq.setTot_Amt("");
            if (PosPub.isNumericTypeMinus(otpReq.getTot_Amt())==false)
            {
                errorMessage.append("请求的栏位tot_Amt不正确 !"  );
                return false;
            }
            if (otpReq.getPointQty()==null) otpReq.setPointQty("0");
            String pointQty=otpReq.getPointQty();

            if (otpReq.getTotQty()==null) otpReq.setTotQty("");
            if (PosPub.isNumericTypeMinus(otpReq.getTotQty())==false)
            {
                errorMessage.append("请求的栏位totQty不正确 !"  );
                return false;
            }

            //异常处理
            if (otpReq.getShipFee()==null) otpReq.setShipFee("0");
            if (PosPub.isNumericTypeMinus(otpReq.getShipFee())==false) otpReq.setShipFee("0");

            if (otpReq.getEraseAmt()==null) otpReq.setEraseAmt("0");
            if (PosPub.isNumericTypeMinus(otpReq.getEraseAmt())==false) otpReq.setEraseAmt("0");


            //发票试算的定义
            JSONArray datas_goodsList = new JSONArray();
            JSONArray datas_payList = new JSONArray();


            //当前单据是子单
            boolean b_subOrderFlag=false;
            b_subOrderFlag=otpReq.getDetailType().equals("3") && otpReq.getHeadOrderNo().equals("")==false ?true:false;
            //会员信息校验
            String orderMemberId=getQData_Order.get(0).get("MEMBERID").toString();
            String orderMemberCardNo=getQData_Order.get(0).get("CARDNO").toString();
            String orderPartnerMember=Check.Null(getQData_Order.get(0).get("PARTNERMEMBER").toString())?"digiwin":getQData_Order.get(0).get("PARTNERMEMBER").toString();
            if (Check.Null(otpReq.getPartnerMember()))
            {
                otpReq.setPartnerMember("digiwin");
            }
            if(!Check.Null(orderMemberId))
            {
                if(Check.Null(otpReq.getMemberId()))
                {
                    errorMessage.append("订单会员Id:" +orderMemberId+"订转销请求内容会员Id不可为空" );
                    return false;
                }
                if(orderMemberId.equals(otpReq.getMemberId()))
                {
                    if(!Check.Null(otpReq.getPartnerMember()) && !orderPartnerMember.equals(otpReq.getPartnerMember()))
                    {
                        errorMessage.append("订单接入会员:" +orderPartnerMember+"与订转销请求接入会员"+otpReq.getPartnerMember()+"不符" );
                        return false;
                    }
                }
            }
            sale.setIsMerPay(isMerPay);
            sale.setEraseAmt(otpReq.getEraseAmt());
            sale.setApproval("");//钉钉审批id
            sale.setAuthorizerOpno("");//授权人编码
            sale.setGetMode(otpReq.getShipType());//配送方式
            sale.setGetShop(otpReq.getShippingShopNo());//
            sale.setEvaluate("0");//是否评价0-否1-是
            sale.setEcsDate("");//订单结案日期
            sale.setEcsFlg("");//订单结案否Y/N
            sale.setCardAmount("0");//初始余额
            sale.setCardTypeId("");//卡类型编码
            //桌台相关
            sale.setTableKind("");//桌台类型
            sale.setTableNO("");//桌台号
            sale.setOpenerId("");//开台人
            sale.setDinnerDate("");//就餐日期
            sale.setDinnerSign("");//就餐牌号码
            sale.setDinnerTime("");//就餐时间
            sale.setDinnerType("");//餐饮单据状态
            sale.setChildNumber("0");//儿童数量
            sale.setServCharge("0");//餐厅服务费
            sale.setGuestNum("0");//桌台人数
            //发票相关
            sale.setTaxRegNumber("");//纳税人识别号
            sale.setFreeCode(otpReq.getFreeCode());//零税证号-只有外交官才有的凭证--零税的其中一种凭证
            sale.setPassport(otpReq.getPassport());//护照编号-零税的其中一种凭证
            sale.setIsInvoice("");//是否需要发票Y/N
            sale.setIsInvoiceMakeOut("");//是否已开票
            sale.setInvCount("0");//发票数量
            sale.setInvoiceAccount("");//发票开户账号
            sale.setInvoiceAddr("");//发票公司地址
            sale.setInvoiceBank("");//发票开户行
            sale.setInvoiceTel("");//发票联系方式
            sale.setInvoiceTitle("");//发票抬头
            sale.setInvSplitType("");//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分
            sale.setoInvstartno("");//原发票号码
            //旅游团相关
            sale.setTourCountryCode("");//国家编码
            sale.setTourGroupNO("");//团号
            sale.setTourGuideNO("");//导游编号
            sale.setTourPeopleNum("0");//进店人数
            sale.setTourTravelNO("");//旅行社编码
            //外卖
            sale.setTakeAway("");//外卖平台
            //
            boolean istakeout=otpReq.getLoadDocType().equals(orderLoadDocType.ELEME)||otpReq.getLoadDocType().equals(orderLoadDocType.MEITUAN)||otpReq.getLoadDocType().equals(orderLoadDocType.JDDJ)||otpReq.getLoadDocType().equals(orderLoadDocType.MTSG)||otpReq.getLoadDocType().equals(orderLoadDocType.DYWM);
            sale.setIsTakeout(istakeout?"Y":"N");//是否外卖订单
            sale.setWxOpenId("");//第三方用户id
            sale.setWmExtraFee("0");//外卖：平台其他费用(每单捐赠1分钱支持环保之类的活动)
            sale.setWmUserPaid("0");//外卖：用户实际支付
            sale.setDeliveryFeeShop("0");//外卖：门店承担配送费
            sale.setOrderSn(otpReq.getSn());//外卖订单流水号
            //
            sale.setIsUploaded("N");//是否已传第三方（例如商场）Y已上传，N未上传
            sale.setLegalper("");//法人
            sale.setIsReturn("N");//是否已退Y/N
            sale.setOrderAmount("0");//订金金额
            sale.setOrder_appType("");//下单应用类型
            sale.setoBdate(otpReq.getbDate());//原单营业日期
            sale.setoMachine(otpReq.getOpMachineNo());//原单机台号
            sale.setoOrder_appType(otpReq.getLoadDocType());//来源应用类型
            sale.setoOrder_channelId(otpReq.getChannelId());//来源渠道编码
            sale.setoOrder_companyId(otpReq.getBelfirm());//来源公司编码
            sale.setTeriminalID("");//终端ID
            sale.setTotChanged("0");//找零金额
            sale.setOrderReturn("");////订金退款类型：0:沒有訂金1:部份訂金2:全部訂金【CM专用】
            sale.setOrderShop(Check.Null(otpReq.getShopNo())?otpReq.getOpShopId():otpReq.getShopNo());//下订门店
            sale.setoShop("");//原单门店
            sale.setOtrno("");//原单流水号
            sale.setoType("3");//原单类型
            sale.setPayDisc("0");//付款折扣金额
            sale.setProductionMode("");//生产方式：0本店制作1异店制作2总部制作
            sale.setProductionShop("");//生产门店
            sale.setRemainPoint("0");//剩余积分
            sale.setRepastType("0");//就餐类型：0堂食1打包2外卖
            sale.setRsvId("");//预订ID
            sale.setSaleAmt("0");
            sale.setSaleDisc("0");//销售折扣金额

            //
            sale.setIsBuff("N");//是否暂存
            sale.setIsBuff_timeout("");//暂存超时时间
            sale.setbDate(otpReq.getbDate());
            sale.setCardNO(otpReq.getCardNo());
            if(!Check.Null(otpReq.getPartnerMember()))
            {
                sale.setPartnerMember(otpReq.getPartnerMember());
            }else
            {
                sale.setPartnerMember("digiwin");
            }
            if(otpReq.getContMan()==null) otpReq.setContMan("");
            //解码
            sale.setContMan(otpReq.getContMan());

            if(otpReq.getContTel()==null) otpReq.setContTel("");
            //解码
            sale.setContTel(otpReq.getContTel());
            sale.setCustomerName(otpReq.getCustomerName());
            sale.setCustomerNO(otpReq.getCustomer());
            sale.setDeliveryFeeUser(otpReq.getShipFee());
            sale.setDeliveryFeeShop(otpReq.getDeliveryFeeShop());
            sale.setDistribution(otpReq.getDelName());
            sale.setMealNumber(otpReq.getMealNumber());
            sale.setBsno(otpReq.getRefundReasonNo());
            sale.seteId(eId);
            sale.setEcCustomerNO(otpReq.getSellNo());
            sale.setgDate(otpReq.getShipDate());
            sale.setgTime(otpReq.getShipStartTime());
            sale.setGetMan(otpReq.getGetMan());
            sale.setGetManTel(otpReq.getGetManTel());
            sale.setMachine(otpReq.getOpMachineNo());
            sale.setManualNO(otpReq.getManualNo());
            sale.setMemberId(otpReq.getMemberId());
            sale.setMemberName(otpReq.getMemberName());
            sale.setMemberOrderno("");//调用积分memberpay的orderno,下面会给值

            if(otpReq.getMemo()==null) otpReq.setMemo("");
            //解码
            otpReq.setMemo(otpReq.getMemo());

            sale.setMemo(otpReq.getMemo());
            sale.setOfNO(Check.Null(otpReq.getHeadOrderNo())?orderNo:otpReq.getHeadOrderNo());
            sale.setOpNO(otpReq.getOpOpNo());
            sale.setOrder_channelId(otpReq.getChannelId());
            sale.setOrder_companyId(otpReq.getBelfirm());
            sale.setOrderID(Check.Null(otpReq.getHeadOrderNo())?orderNo:otpReq.getHeadOrderNo());
            sale.setPackageFee(otpReq.getPackageFee());
            sale.setPayAmt(otpReq.getPayAmt());
            sale.setPlatformDisc(otpReq.getPlatformDisc());
            sale.setSellerDisc(otpReq.getSellerDisc());
            sale.setPlatformFee(otpReq.getServiceCharge());
            sale.setPointQty(otpReq.getPointQty());
            sale.setsDate(sDate);
            sale.setSellCredit(otpReq.getSellCredit());
            sale.setReturnUserId(otpReq.getOpNo());

            if(otpReq.getDelMemo()==null) otpReq.setDelMemo("");
            //解码
            sale.setSendMemo(otpReq.getDelMemo());

            //解码
            if(otpReq.getAddress()==null) otpReq.setAddress("");
            otpReq.setAddress(otpReq.getAddress());
            sale.setShipAdd(otpReq.getAddress());
            sale.setShippingFee(otpReq.getShipFee());
            sale.setShopId(shopId);
            sale.setShopIncome(otpReq.getIncomeAmt());
            sale.setSourceSubOrderno(Check.Null(otpReq.getHeadOrderNo())?"":orderNo);
            sale.setSquadNO(otpReq.getOpSquadNo());
            sale.setStatus("100");
            sale.setsTime(sTime);
            sale.setTotQty(otpReq.getTotQty());//总数量
            sale.setTotAmt(otpReq.getTot_Amt());
            sale.setTotDisc(otpReq.getTotDisc());
            sale.setTotOldAmt(otpReq.getTot_oldAmt());
            sale.setType("0");
            sale.setTypeName("销售单");
            sale.setVerNum(otpReq.getVerNum());
            sale.setWorkNO(otpReq.getOpWorkNo());
            sale.setTran_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            sale.setDepartNo(departNo);

            //【ID1034999】【嘉华3.0】团购打折功能-dcp服务 by jinzma 20230804
            sale.setGroupBuying(getQData_Order.get(0).getOrDefault("GROUPBUYING","N").toString());


            //android POS 外卖直接调这个接口，参数没传，这里处理一下吧，啊啊啊啊啊啊
            if (Check.Null(otpReq.getWaimaiMerreceiveMode()))
            {
                otpReq.setWaimaiMerreceiveMode(getQData_Order.get(0).getOrDefault("WAIMAIMERRECEIVEMODE", "").toString());
            }
            sale.setWaimaiMerreceiveMode(otpReq.getWaimaiMerreceiveMode());


            //尾款处理,这个只是记录付款
            com.alibaba.fastjson.JSONArray payslistArray=new com.alibaba.fastjson.JSONArray();
            //这里才会扣款
            com.alibaba.fastjson.JSONArray cardlistArray=new com.alibaba.fastjson.JSONArray();
            //券列表
            com.alibaba.fastjson.JSONArray couponlistArray=new com.alibaba.fastjson.JSONArray();


            String billno=PosPub.getGUID(false);//产生收款单号
            BigDecimal payTot=new BigDecimal("0");//本次付款总额
            sale.setPays(new ArrayList<>());

            //这里判断下 是不是大客户赊销订单
            boolean isSellCredit = false;
            double dealCreditAmount = 0;//金蝶不是以这个金额，是明细里的

            //积分卡
            List<String> listCardno=new ArrayList<>();


            //根据付款记录分摊处理
            BigDecimal bdm_tot_payamt=new BigDecimal("0");//已付总金额
            BigDecimal bdm_tot_merdiscount=new BigDecimal("0");//商家总折扣，下面会累加上抹零的折扣
            BigDecimal bdm_tot_thirddiscount=new BigDecimal("0");//第三方平台总折扣，下面会累加上抹零的折扣
            BigDecimal bdm_tot_merreceive=new BigDecimal("0");//商家实收金额(等于订单单头的字段)
            BigDecimal bdm_tot_custpayreal=new BigDecimal("0");//客户实付金额(等于订单单头的字段)

            String sql = " select * from DCP_ORDER_PAY_DETAIL where EID='"+eId+"' and SOURCEBILLNO='"+orderNo+"' and SOURCEBILLTYPE='Order'  ";
            List<Map<String, Object>> getData_order_pay=dao.executeQuerySQL(sql, null);
            if(getData_order_pay!=null && getData_order_pay.isEmpty()==false)
            {
                for (Map<String, Object> map : getData_order_pay)
                {
                    String funcno=map.get("FUNCNO").toString();
                    String cardno=map.get("CARDNO").toString();
                    String payChannelCode=map.get("PAYCHANNELCODE").toString();
                    BigDecimal p_pay=new BigDecimal(map.get("PAY").toString());
                    BigDecimal p_changed=new BigDecimal(map.get("CHANGED").toString());
                    BigDecimal p_extra=new BigDecimal(map.get("EXTRA").toString());

                    String merdiscount=map.get("MERDISCOUNT").toString();
                    if (PosPub.isNumericTypeMinus(merdiscount)==false) merdiscount="0";

                    String thirddiscount=map.get("THIRDDISCOUNT").toString();
                    if (PosPub.isNumericTypeMinus(thirddiscount)==false) thirddiscount="0";

                    String merreceive=map.get("MERRECEIVE").toString();
                    if (PosPub.isNumericTypeMinus(merreceive)==false) merreceive="0";

                    String custpayreal=map.get("CUSTPAYREAL").toString();
                    if (PosPub.isNumericTypeMinus(custpayreal)==false) custpayreal="0";

                    //订转销积分
                    if (funcno.equals("301"))
                    {
                        //pay-changed-extra累加起来
                        BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);

                        com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempPay.put("payType",map.get("PAYTYPE").toString());//收款方式代号
                        tempPay.put("payName",map.get("PAYNAME").toString());//收款方式名称
                        tempPay.put("payAmount",p_realpay);//付款金额
                        tempPay.put("noCode",cardno);//卡号
                        tempPay.put("isCardPay",1);//
                        tempPay.put("partnerMember", payChannelCode);
                        payslistArray.add(tempPay);

                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",cardno);
                        tempCard.put("amount","0");//0只处理积分
                        tempCard.put("getPoint","0");
                        tempCard.put("partnerMember",payChannelCode );
                        cardlistArray.add(tempCard);

                        //
                        listCardno.add(cardno);
                    }

                    //601是赊销付款方式
                    if(otpReq.getLoadDocType().equals(orderLoadDocType.POS)||otpReq.getLoadDocType().equals(orderLoadDocType.POSANDROID)||otpReq.getLoadDocType().equals(orderLoadDocType.OWNCHANNEL))
                    {
                        if (funcno.equals("601"))
                        {
                            isSellCredit = true;
                        }
                    }

                    //累加已付金额
                    bdm_tot_payamt=bdm_tot_payamt.add(p_pay.subtract(p_changed).subtract(p_extra));
                    bdm_tot_merdiscount=bdm_tot_merdiscount.add(new BigDecimal(merdiscount));
                    bdm_tot_thirddiscount=bdm_tot_thirddiscount.add(new BigDecimal(thirddiscount));
                    bdm_tot_merreceive=bdm_tot_merreceive.add(new BigDecimal(merreceive));
                    bdm_tot_custpayreal=bdm_tot_custpayreal.add(new BigDecimal(custpayreal));
                }
            }
            if(isOrderToSaleAll.equals("1"))
            {
                bdm_tot_merdiscount=new BigDecimal(otpReq.getTotDisc_merReceive());
                bdm_tot_merreceive=new BigDecimal(otpReq.getTot_Amt_merReceive());
                bdm_tot_custpayreal=new BigDecimal(otpReq.getTot_Amt_custPayReal());
                bdm_tot_thirddiscount=new BigDecimal(otpReq.getTotThirdDiscount());
            }
            //**********************处理memberpay数据
            if (otpReq.getPay()!=null)
            {
                for (DCP_OrderToSaleProcess_OpenReq.Payment rpay : otpReq.getPay())
                {
                    //有尾款就要更新订单
                    bFinalPayDisc=true;

                    SALE.Payment pay=sale.new Payment();

                    //字段验证
                    if (rpay.getDescore()==null) rpay.setDescore("0");
                    if (PosPub.isNumericTypeMinus(rpay.getDescore())==false) rpay.setDescore("0");

                    if (rpay.getCouponQty()==null) rpay.setCouponQty("0");
                    if (PosPub.isNumericTypeMinus(rpay.getCouponQty())==false) rpay.setCouponQty("0");

                    if (rpay.getPay()==null) rpay.setPay("0");
                    if (PosPub.isNumericTypeMinus(rpay.getPay())==false) rpay.setPay("0");

                    if (rpay.getExtra()==null) rpay.setExtra("0");
                    if (PosPub.isNumericTypeMinus(rpay.getExtra())==false) rpay.setExtra("0");

                    if (rpay.getChanged()==null) rpay.setChanged("0");
                    if (PosPub.isNumericTypeMinus(rpay.getChanged())==false) rpay.setChanged("0");

                    if (rpay.getSendPay()==null) rpay.setSendPay("0");
                    if (PosPub.isNumericTypeMinus(rpay.getSendPay())==false) rpay.setSendPay("0");

                    if (rpay.getFuncNo()==null) rpay.setFuncNo("");

                    BigDecimal p_pay=new BigDecimal(rpay.getPay());
                    BigDecimal p_changed=new BigDecimal(rpay.getChanged());
                    BigDecimal p_extra=new BigDecimal(rpay.getExtra());

                    //pay-changed-extra累加起来
                    BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);

                    payTot=payTot.add(p_realpay);

                    //券面额
                    BigDecimal faceAmt=p_pay;//.add(p_extra);


                    if (rpay.getMerDiscount()==null) rpay.setMerDiscount("0");
                    if (PosPub.isNumericTypeMinus(rpay.getMerDiscount())==false) rpay.setMerDiscount("0");

                    if (rpay.getMerReceive()==null) rpay.setMerReceive(p_realpay.toPlainString());
                    if (PosPub.isNumericTypeMinus(rpay.getMerReceive())==false) rpay.setMerReceive(p_realpay.toPlainString());

                    if (rpay.getThirdDiscount()==null) rpay.setThirdDiscount("0");
                    if (PosPub.isNumericTypeMinus(rpay.getThirdDiscount())==false) rpay.setThirdDiscount("0");

                    if (rpay.getCustPayReal()==null) rpay.setCustPayReal(p_realpay.toPlainString());
                    if (PosPub.isNumericTypeMinus(rpay.getCustPayReal())==false) rpay.setCustPayReal(p_realpay.toPlainString());

                    if (rpay.getCouponMarketPrice()==null) rpay.setCouponMarketPrice("0");
                    if (PosPub.isNumericTypeMinus(rpay.getCouponMarketPrice())==false) rpay.setCouponMarketPrice("0");

                    if (rpay.getCouponPrice()==null) rpay.setCouponPrice("0");
                    if (PosPub.isNumericTypeMinus(rpay.getCouponPrice())==false) rpay.setCouponPrice("0");

                    //累加
                    bdm_tot_merdiscount=bdm_tot_merdiscount.add(new BigDecimal(rpay.getMerDiscount()));
                    bdm_tot_thirddiscount=bdm_tot_thirddiscount.add(new BigDecimal(rpay.getThirdDiscount()));
                    bdm_tot_merreceive=bdm_tot_merreceive.add(new BigDecimal(rpay.getMerReceive()));
                    bdm_tot_custpayreal=bdm_tot_custpayreal.add(new BigDecimal(rpay.getCustPayReal()));

                    pay.setFuncNo(rpay.getFuncNo());//功能编码
                    pay.setCardNo(rpay.getCardNo());
                    pay.setDescore(rpay.getDescore());
                    pay.setPayserNum(rpay.getPaySerNum());
                    pay.setPaycode(rpay.getPayCode());
                    pay.setPayName(rpay.getPayName());
                    pay.setPassword(rpay.getPassword());
                    pay.setPayType(rpay.getPayType());

                    //【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务 by jinzma 20231205
                    pay.setPayChannelCode(rpay.getPayChannelCode());
                    pay.setEcardSign(rpay.getEcardSign());   //年初做的有BUG，没有赋值，后面其实都取不到

                    //POS专用
                    if (otpReq.getLoadDocType().equals("POS"))
                    {
                        if (Check.Null(rpay.getPaydoctype())) rpay.setPaydoctype("4");
                    }

                    sale.getPays().add(pay);

                    //****会员卡扣款****
                    if (pay.getFuncNo().equals("301"))
                    {
                        pay.setPayserNum(memberOrderno);

                        com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempPay.put("payType",pay.getPayType());//收款方式代号
                        tempPay.put("payName",pay.getPayName());//收款方式名称
                        tempPay.put("payAmount",p_realpay);//付款金额
                        tempPay.put("noCode",pay.getCardNo());//卡号
                        tempPay.put("isCardPay",1);//
                        tempPay.put("partnerMember", pay.getPayChannelCode());
                        payslistArray.add(tempPay);

                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",pay.getCardNo());
                        tempCard.put("amount",p_realpay);//0只处理积分
                        tempCard.put("getPoint","0");//0只处理积分
                        //【ID1030898】//【菲尔雪3.0】会员及卡券接企迈-POS服务 by jinzma 2023
                        tempCard.put("ecardSign",pay.getEcardSign());  //企迈 ecardSign字段，0实体卡，1电子卡
                        //【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务 by jinzma 20231205
                        tempCard.put("partnerMember",pay.getPayChannelCode());

                        cardlistArray.add(tempCard);
                    }
                    if (pay.getFuncNo().equals("302"))//积分扣减
                    {
                        pay.setPayserNum(memberOrderno);

                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",pay.getCardNo());
                        tempCard.put("usePoint",pay.getDescore());//积分扣减
                        tempCard.put("amount","0");//0只处理积分
                        tempCard.put("getPoint","0");//0只处理积分
                        //【ID1030898】//【菲尔雪3.0】会员及卡券接企迈-POS服务 by jinzma 2023
                        tempCard.put("ecardSign",pay.getEcardSign());  //企迈 ecardSign字段，0实体卡，1电子卡
                        //【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务 by jinzma 20231205
                        tempCard.put("partnerMember",pay.getPayChannelCode());

                        cardlistArray.add(tempCard);
                    }
                    if (pay.getFuncNo().equals("304") || pay.getFuncNo().equals("305")|| pay.getFuncNo().equals("307"))//现金券/折扣券
                    {
                        pay.setPayserNum(memberOrderno);

                        //
                        com.alibaba.fastjson.JSONObject tempCoupon=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCoupon.put("couponCode",pay.getCardNo());//券号
                        tempCoupon.put("couponType","");//券类型
                        tempCoupon.put("quantity",rpay.getCouponQty());//使用张数
                        tempCoupon.put("faceAmount",faceAmt);//总面额
                        tempCoupon.put("buyAmount",p_realpay);//抵账金额
                        //【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务 by jinzma 20231205
                        tempCoupon.put("partnerMember",pay.getPayChannelCode());
                        couponlistArray.add(tempCoupon);
                    }

                    //601是赊销付款方式
                    if(req.getRequest().getLoadDocType().equals(orderLoadDocType.POS)||req.getRequest().getLoadDocType().equals(orderLoadDocType.POSANDROID)||req.getRequest().getLoadDocType().equals(orderLoadDocType.OWNCHANNEL))
                    {
                        if (pay.getFuncNo().equals("601"))
                        {
                            isSellCredit = true;

                            //累加赊销金额
                            dealCreditAmount=p_realpay.add(new BigDecimal(dealCreditAmount)).doubleValue();
                        }
                    }

                    if (pay.getFuncNo().equals("3011"))//禄品电影卡
                    {
                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",pay.getCardNo());
                        tempCard.put("amount",p_realpay);//0只处理积分
                        tempCard.put("getPoint","0");//0只处理积分
                        tempCard.put("cardType","LPDY");//
                        tempCard.put("cardPwd",pay.getPassword());//
                        tempCard.put("partnerMember", pay.getPayChannelCode());
                        cardlistArray.add(tempCard);
                    }
                    if (pay.getFuncNo().equals("3012"))//四威
                    {
                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",pay.getCardNo());
                        tempCard.put("amount",p_realpay);//0只处理积分
                        tempCard.put("getPoint","0");//0只处理积分
                        tempCard.put("cardType","SIWEI_CARD");//
                        tempCard.put("cardPwd",pay.getPassword());//
                        tempCard.put("partnerMember", pay.getPayChannelCode());
                        cardlistArray.add(tempCard);
                    }
                    if (pay.getFuncNo().equals("3013"))//乐享支付
                    {
                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",pay.getCardNo());
                        tempCard.put("amount",p_realpay);//0只处理积分
                        tempCard.put("getPoint","0");//0只处理积分
                        tempCard.put("cardType","LXYS");//
                        tempCard.put("cardPwd",pay.getPassword());//
                        tempCard.put("rnd1",pay.getRnd1());//
                        tempCard.put("rnd2",pay.getRnd2());//
                        tempCard.put("partnerMember", pay.getPayChannelCode());
                        cardlistArray.add(tempCard);
                    }
                    if (pay.getFuncNo().equals("3014"))//聚优福利卡
                    {
                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",pay.getCardNo());
                        tempCard.put("amount",p_realpay);//0只处理积分
                        tempCard.put("getPoint","0");//0只处理积分
                        tempCard.put("cardType","JYFL");//
                        tempCard.put("partnerMember", pay.getPayChannelCode());

                        cardlistArray.add(tempCard);
                    }
                }

            }

            //累加已付金额
            bdm_tot_payamt=bdm_tot_payamt.add(payTot);

            //*****根据单号调用库存锁定查询DCP_StockOrderLockDetail_Open
            boolean bDCP_StockUnlock=false;//是否需要调用库存解锁服务DCP_StockUnlock
            try
            {
                //内部服务形式调用
                if (req.getApiUser() == null)
                {
                    JSONObject req_SOLD = new JSONObject();
                    req_SOLD.put("serviceId","DCP_StockOrderLockDetail");
                    req_SOLD.put("token", req.getToken());
                    // //JOB调订转销用到，没token
                    req_SOLD.put("eId",eId);
                    req_SOLD.put("eShop",shopId);

                    req_SOLD.put("pageNumber","1");
                    req_SOLD.put("pageSize","100");
                    //
                    JSONObject request_SOLD = new JSONObject();
                    request_SOLD.put("billNo", orderNo);
                    req_SOLD.put("request", request_SOLD);

                    String str_SOLD = req_SOLD.toString();// 将json对象转换为字符串

                    PosPub.WriteETLJOBLog("訂轉銷調用庫存鎖定查詢服務DCP_StockOrderLockDetail請求： "+ str_SOLD);

                    //内部调内部
                    DispatchService ds = DispatchService.getInstance();
                    String resbody_SOLD = ds.callService(str_SOLD, this.dao);

                    PosPub.WriteETLJOBLog("訂轉銷調用庫存鎖定查詢服務DCP_StockOrderLockDetail返回： "+ resbody_SOLD);

                    if (resbody_SOLD.equals(""))
                    {
                        errorMessage.append("调用库存锁定查询服务DCP_StockOrderLockDetail失败,返回为空");
                        return false;
                    }
                    else
                    {
                        JSONObject jsonres_SOLD = new JSONObject(resbody_SOLD);
                        boolean success = jsonres_SOLD.getBoolean("success");
                        if (success)
                        {
                            JSONObject datas_SOLD=jsonres_SOLD.getJSONObject("datas");
                            if (datas_SOLD.getJSONArray("billList").length()>0)
                            {
                                bDCP_StockUnlock=true;
                            }
                        }
                        else
                        {
                            String err_SOLD=jsonres_SOLD.get("serviceDescription").toString();

                            errorMessage.append("调用库存锁定查询服务DCP_StockOrderLockDetail失败,原因：" +err_SOLD);
                            return false;
                        }
                    }
                }
                else
                {
                    //DCP调用外部DCP服务
                    DCP_StockOrderLockDetail_OpenReq stockOrderLockDetail_openReq=new DCP_StockOrderLockDetail_OpenReq();
                    DCP_StockOrderLockDetail_OpenReq.levelReq orderLockdetailRequest=stockOrderLockDetail_openReq.new levelReq();
                    orderLockdetailRequest.setBillNo(orderNo);
                    stockOrderLockDetail_openReq.setRequest(orderLockdetailRequest);
                    stockOrderLockDetail_openReq.setPageNumber(1);
                    stockOrderLockDetail_openReq.setPageSize(100);
                    stockOrderLockDetail_openReq.seteId(req.geteId());
                    stockOrderLockDetail_openReq.setLangType(req.getLangType());
                    stockOrderLockDetail_openReq.setApiUserCode(req.getApiUserCode());
                    stockOrderLockDetail_openReq.setApiUser(req.getApiUser());
                    //
                    DCP_StockOrderLockDetail_Open stockOrderLockDetail_open=new DCP_StockOrderLockDetail_Open();
                    stockOrderLockDetail_open.setDao(this.dao);
                    DCP_StockOrderLockDetail_OpenRes stockOrderLockDetail_openRes=stockOrderLockDetail_open.processJson(stockOrderLockDetail_openReq);
                    if (stockOrderLockDetail_openRes.isSuccess())
                    {
                        if (stockOrderLockDetail_openRes.datas.getBillList().size()>0)
                        {
                            bDCP_StockUnlock=true;
                        }
                    }
                    else
                    {
                        String err_SOLD=stockOrderLockDetail_openRes.getServiceDescription();

                        errorMessage.append("调用库存锁定查询服务DCP_StockOrderLockDetail_Open失败,原因：" +err_SOLD);
                        return false;
                    }

                    /*
					JSONObject req_SOLD = new JSONObject();
					req_SOLD.put("pageNumber","1");
					req_SOLD.put("pageSize","100");
					//
					JSONObject request_SOLD = new JSONObject();
					request_SOLD.put("billNo", orderNo);
					req_SOLD.put("request", request_SOLD);

					String str_SOLD = req_SOLD.toString();// 将json对象转换为字符串

					Map<String, Object> map = new HashMap<>();
					map.put("serviceId", "DCP_StockOrderLockDetail_Open");
					map.put("apiUserCode",req.getApiUserCode());
					map.put("sign",PosPub.encodeMD5(req_SOLD.toString() + req.getApiUser().getUserKey()));
					map.put("langType",req.getLangType());
					map.put("requestId",PosPub.getGUID(false));
					map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
					map.put("version","3.0");

					String platformUrl = PosPub.getPARA_SMS(this.dao, req.geteId(), "", "PlatformCentreURL");

					PosPub.WriteETLJOBLog("訂轉銷調用庫存鎖定查詢服務DCP_StockOrderLockDetail_Open請求： "+ str_SOLD);

					//外部调外部
					String resbody_SOLD=HttpSend.doPost(platformUrl,str_SOLD,map);
					map.clear();
					map=null;

					PosPub.WriteETLJOBLog("訂轉銷調用庫存鎖定查詢服務DCP_StockOrderLockDetail_Open返回： "+ resbody_SOLD);

					if (resbody_SOLD.equals(""))
					{
						errorMessage.append("调用库存锁定查询服务DCP_StockOrderLockDetail_Open失败,返回为空");
						return false;
					}
					else
					{
						JSONObject jsonres_SOLD = new JSONObject(resbody_SOLD);
						boolean success = jsonres_SOLD.getBoolean("success");
						if (success)
						{
							JSONObject datas_SOLD=jsonres_SOLD.getJSONObject("datas");
							if (datas_SOLD.getJSONArray("billList").length()>0)
							{
								bDCP_StockUnlock=true;
							}
						}
						else
						{
							String err_SOLD=jsonres_SOLD.get("serviceDescription").toString();

							errorMessage.append("调用库存锁定查询服务DCP_StockOrderLockDetail_Open失败,原因：" +err_SOLD);
							return false;
						}
					}
					*/
                }
            }
            catch (Exception e)
            {
                errorMessage.append("发现异常-调用库存锁定查询服务DCP_StockOrderLockDetail_Open失败!");
                return false;
            }

            //DCP_StockUnlock用到的明细商品
            JSONArray data_plu = new JSONArray();
            DCP_StockUnlock_OpenReq stockUnlock_openReq=new DCP_StockUnlock_OpenReq();
            List<DCP_StockUnlock_OpenReq.PluList> unplulist=new ArrayList<>();

            //商品明细
            //累加本次订转销
            BigDecimal bdm_CurrentTotQty=new BigDecimal("0");
            sale.setDetails(new ArrayList<>());


            //累加用于计算分摊比率
            BigDecimal bdm_tot_temp_amt=new BigDecimal("0");
            //商品数据验证处理
            for (goods goods : otpReq.getGoodsList())
            {
                if (Check.Null(goods.getAmt()) ) goods.setAmt("0");
                if (PosPub.isNumericTypeMinus(goods.getAmt())==false) goods.setAmt("0");

                if (Check.Null(goods.getDisc_merReceive()) ) goods.setDisc_merReceive(goods.getDisc());
                if (PosPub.isNumericTypeMinus(goods.getDisc_merReceive())==false) goods.setDisc_merReceive(goods.getDisc());

                if (Check.Null(goods.getDisc_custPayReal()) ) goods.setDisc_custPayReal(goods.getDisc());
                if (PosPub.isNumericTypeMinus(goods.getDisc_custPayReal())==false) goods.setDisc_custPayReal(goods.getDisc());

                if (Check.Null(goods.getAmt_merReceive()) ) goods.setAmt_merReceive(goods.getAmt());
                if (PosPub.isNumericTypeMinus(goods.getAmt_merReceive())==false) goods.setAmt_merReceive(goods.getAmt());

                if (Check.Null(goods.getAmt_custPayReal()) ) goods.setAmt_custPayReal(goods.getAmt());
                if (PosPub.isNumericTypeMinus(goods.getAmt_custPayReal())==false) goods.setAmt_custPayReal(goods.getAmt());

                //1、正常商品 2、套餐主商品 3、套餐子商品
                //套餐主商品不分摊
                if(goods.getPackageType()!=null && goods.getPackageType().equals("2"))
                {
                    continue;
                }

                if (goods.getAgioInfo()==null)goods.setAgioInfo(new ArrayList<>());

                //订单分摊折扣过滤掉，不然订转销分摊会重复
                for (int ai = goods.getAgioInfo().size()-1; ai >=0; ai--)
                {
                    if (goods.getAgioInfo().get(ai).getDcType() != null && goods.getAgioInfo().get(ai).getDcType().equals("60"))
                    {
                        goods.getAgioInfo().remove(goods.getAgioInfo().get(ai));
                    }
                }

                //累加用于计算
                bdm_tot_temp_amt=bdm_tot_temp_amt.add(new BigDecimal(goods.getAmt()));
            }



            //处理已付金额的商品分摊
            int sdi=0;
            BigDecimal sum_disc_merReceive=new BigDecimal("0");
            BigDecimal sum_disc_Third=new BigDecimal("0");
            BigDecimal sum_amt_merReceive=new BigDecimal("0");
            BigDecimal sum_amt_custpayReal=new BigDecimal("0");

            //1、正常商品 2、套餐主商品 3、套餐子商品
            //处理分摊，0元商品不分摊，套餐主商品不分摊,过滤掉
            List<goods> tempShareGoods=otpReq.getGoodsList().stream().filter(p-> "2".equals(p.getPackageType())==false && new BigDecimal(p.getAmt()).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
            for (goods goods : tempShareGoods)
            {
                sdi=sdi+1;

                //
                BigDecimal disc_merReceive=new BigDecimal("0");
                BigDecimal disc_Third=new BigDecimal("0");
                BigDecimal disc_custPayReal=new BigDecimal("0");

                BigDecimal amt_Merreceive=new BigDecimal("0");
                BigDecimal amt_CustpayReal=new BigDecimal("0");

                //保留2位小数
                if (sdi==tempShareGoods.size())
                {
                    //最后1笔采用减法
                    disc_merReceive=bdm_tot_merdiscount.subtract(sum_disc_merReceive);
                    disc_Third=bdm_tot_thirddiscount.subtract(sum_disc_Third);
                    amt_Merreceive=bdm_tot_merreceive.subtract(sum_amt_merReceive);
                    amt_CustpayReal=bdm_tot_custpayreal.subtract(sum_amt_custpayReal);
                }
                else
                {
                    if (bdm_tot_temp_amt.compareTo(BigDecimal.ZERO) !=0)
                    {
                        disc_merReceive=new BigDecimal(goods.getAmt()).multiply(bdm_tot_merdiscount).divide(bdm_tot_temp_amt,2,RoundingMode.HALF_UP);
                        disc_Third=new BigDecimal(goods.getAmt()).multiply(bdm_tot_thirddiscount).divide(bdm_tot_temp_amt,2,RoundingMode.HALF_UP);

                        //商家实收-配送费-餐盒费
                        //BigDecimal bdm_Realmerreceive=bdm_tot_merreceive.subtract(new BigDecimal(otpReq.getPackageFee())).subtract(new BigDecimal(otpReq.getTot_shipFee()));

                        //商家实收(改成不减了)
                        BigDecimal bdm_Realmerreceive=bdm_tot_merreceive;
                        amt_Merreceive=new BigDecimal(goods.getAmt()).multiply(bdm_Realmerreceive).divide(bdm_tot_temp_amt,2,RoundingMode.HALF_UP);
                        amt_CustpayReal=new BigDecimal(goods.getAmt()).multiply(bdm_tot_custpayreal).divide(bdm_tot_temp_amt,2,RoundingMode.HALF_UP);

                    }
                    else
                    {
                        disc_merReceive=new BigDecimal("0");
                        disc_Third=new BigDecimal("0");

                        amt_Merreceive=new BigDecimal("0");
                        amt_CustpayReal=new BigDecimal("0");
                    }
                }
                //累加
                sum_disc_merReceive=sum_disc_merReceive.add(disc_merReceive);
                sum_disc_Third=sum_disc_Third.add(disc_Third);
                sum_amt_merReceive=sum_amt_merReceive.add(amt_Merreceive);
                sum_amt_custpayReal=sum_amt_custpayReal.add(amt_CustpayReal);

                //商家优惠，单头分摊计算来的
                goods.setDisc_merReceive(disc_merReceive.toPlainString());
                //计算来的，累加不等于单头的，商家实收，不减了  ****************
                goods.setAmt_merReceive(amt_Merreceive.toPlainString());
                //客户实际折扣=商家折扣+平台折扣，单头分摊计算来的
                disc_custPayReal=disc_merReceive.add(disc_Third);
                goods.setDisc_custPayReal(disc_custPayReal.toPlainString());
                //客户实付金额，单头分摊计算来的
                goods.setAmt_custPayReal(amt_CustpayReal.toPlainString());

                if (goods.getAgioInfo()==null)goods.setAgioInfo(new ArrayList<>());
//
//				//只要有付款折扣,自动加1笔折扣记录
                if (disc_custPayReal.compareTo(BigDecimal.ZERO)!=0)
                {

                    Agio tempAgio=req.new Agio();

                    //处理付款顺序
                    int disc_item=1;
                    if (goods.getAgioInfo().size()>0)
                    {
                        //排序
                        goods.getAgioInfo().sort(Comparator.comparingInt(a->Integer.parseInt(a.getItem())));
                        //取最大的+1
                        disc_item=Integer.parseInt(goods.getAgioInfo().get(goods.getAgioInfo().size()-1).getItem())+1;
                    }
                    if(isOrderToSaleAll.equals("1"))
                    {
                        tempAgio.setMitem(goods.getItem());
                    }
                    tempAgio.setItem(disc_item+"");
                    tempAgio.setAmt(goods.getAmt());
                    tempAgio.setBsNo("");
                    tempAgio.setDcType("60");
                    tempAgio.setDcTypeName("支付折扣");
                    tempAgio.setDisc("0");
                    tempAgio.setOrderToSalePayAgioFlag("Y");
                    tempAgio.setDisc_merReceive(disc_merReceive.toPlainString());
                    tempAgio.setDisc_custPayReal(disc_custPayReal.toPlainString());
                    tempAgio.setGiftCtf("");
                    tempAgio.setGiftCtfNo("");
                    tempAgio.setInputDisc("100");
                    tempAgio.setPmtNo("");
                    tempAgio.setQty(goods.getQty());
                    tempAgio.setRealDisc("0");
                    goods.getAgioInfo().add(tempAgio);
                }
            }

            //抹零金额尾款分摊处理(只处理商品折扣字段，不处理金额字段)
            if (new BigDecimal(sale.getEraseAmt()).compareTo(BigDecimal.ZERO)>0  &&  !isOrderToSaleAll.equals("1"))
            {
                //这也算尾款折扣
                bFinalPayDisc=true;

                BigDecimal disc_merReceive=new BigDecimal("0");
                BigDecimal disc_Third=new BigDecimal("0");
                BigDecimal disc_custPayReal=new BigDecimal("0");

                sdi=0;
                sum_disc_merReceive=new BigDecimal("0");
                sum_disc_Third=new BigDecimal("0");

                //1、正常商品 2、套餐主商品 3、套餐子商品
                //处理分摊，0元商品不分摊，套餐主商品不分摊,过滤掉
                for (goods goods : tempShareGoods)
                {
                    sdi=sdi+1;

                    //保留2位小数
                    if (sdi==tempShareGoods.size())
                    {
                        //最后1笔采用减法
                        disc_merReceive=new BigDecimal(sale.getEraseAmt()).subtract(sum_disc_merReceive);
                    }
                    else
                    {
                        if (bdm_tot_temp_amt.compareTo(BigDecimal.ZERO) !=0)
                        {
                            disc_merReceive=new BigDecimal(goods.getAmt()).multiply(new BigDecimal(sale.getEraseAmt())).divide(bdm_tot_temp_amt,2,RoundingMode.HALF_UP);
                        }
                        else
                        {
                            disc_merReceive=new BigDecimal("0");
                        }

                        //避免四舍五入后多计算金额
                        if (new BigDecimal(sale.getEraseAmt()).subtract(sum_disc_merReceive).compareTo(disc_merReceive)<0)
                        {
                            disc_merReceive=new BigDecimal("0");
                        }
                    }
                    //累加
                    sum_disc_merReceive=sum_disc_merReceive.add(disc_merReceive);
                    sum_disc_Third=sum_disc_Third.add(disc_Third);

                    //商家优惠
                    goods.setDisc_merReceive(new BigDecimal(goods.getDisc_merReceive()).add(disc_merReceive).toPlainString());
                    //客户实际折扣=商家折扣+平台折扣
                    disc_custPayReal=disc_merReceive.add(disc_Third);
                    goods.setDisc_custPayReal(new BigDecimal(goods.getDisc_custPayReal()).add(disc_custPayReal).toPlainString());

                    if (goods.getAgioInfo()==null)goods.setAgioInfo(new ArrayList<>());

//					//只要有付款折扣,自动加1笔折扣记录
//					if (disc_custPayReal.compareTo(BigDecimal.ZERO)>0)
//					{
//
//					}
                    Agio tempAgio=req.new Agio();

                    //处理付款顺序
                    int disc_item=1;
                    if (goods.getAgioInfo().size()>0)
                    {
                        //排序
                        goods.getAgioInfo().sort(Comparator.comparingInt(a->Integer.parseInt(a.getItem())));
                        //取最大的+1
                        disc_item=Integer.parseInt(goods.getAgioInfo().get(goods.getAgioInfo().size()-1).getItem())+1;
                    }
                    if(isOrderToSaleAll.equals("1"))
                    {
                        tempAgio.setMitem(goods.getItem());
                    }
                    tempAgio.setItem(disc_item+"");
                    tempAgio.setAmt(goods.getAmt());
                    tempAgio.setBsNo("");
                    tempAgio.setDcType("60");
                    tempAgio.setDcTypeName("支付折扣");
                    tempAgio.setDisc("0");
                    tempAgio.setOrderToSalePayAgioFlag("Y");
                    tempAgio.setDisc_merReceive(disc_merReceive.toPlainString());
                    tempAgio.setDisc_custPayReal(disc_custPayReal.toPlainString());
                    tempAgio.setGiftCtf("");
                    tempAgio.setGiftCtfNo("");
                    tempAgio.setInputDisc("100");
                    tempAgio.setPmtNo("");
                    tempAgio.setQty(goods.getQty());
                    tempAgio.setRealDisc("0");
                    goods.getAgioInfo().add(tempAgio);
                }
                //累加上抹零的折扣
                bdm_tot_merdiscount=bdm_tot_merdiscount.add(sum_disc_merReceive);
                bdm_tot_thirddiscount=bdm_tot_thirddiscount.add(sum_disc_Third);
            }


            //这个是商品明细赊销用
            List<JindieGoodsDetail>  details=new ArrayList<>();

            BigDecimal saleamt=new BigDecimal("0");
            BigDecimal saledisc=new BigDecimal("0");


            //【ID1039790】 【大万-3.0】库存表中门店和仓库信息不匹配。  by jinzma 20240326
            //此处重新赋值仓库，因为订转销业绩算下单门店的时候，只改了单头的门店，单身的仓库还是原来的门店所属仓库
            String getWareouseSql = " select WAREHOUSE from DCP_ORDER_DETAIL where EID='"+eId+"' and orderno='"+orderNo+"'" ;
            String warehouse = "";
            List<Map<String, Object>> getWareouseSqlList=dao.executeQuerySQL(getWareouseSql, null);
            if (CollectionUtil.isNotEmpty(getWareouseSqlList)){
                warehouse = getWareouseSqlList.get(0).get("WAREHOUSE").toString();
            }

            //写销售明细表
            for (goods goods : otpReq.getGoodsList())
            {
                SALE.Detail detail=sale.new Detail();

                //字段验证处理
                if (Check.Null(goods.getQty()) ) goods.setQty("0");
                if (PosPub.isNumericTypeMinus(goods.getQty())==false) goods.setQty("0");

                if (Check.Null(goods.getAmt()) ) goods.setAmt("0");
                if (PosPub.isNumericTypeMinus(goods.getAmt())==false) goods.setAmt("0");

                if (Check.Null(goods.getBoxNum()) ) goods.setBoxNum("0");
                if (PosPub.isNumericTypeMinus(goods.getBoxNum())==false) goods.setBoxNum("0");

                if (Check.Null(goods.getBoxPrice()) ) goods.setBoxPrice("0");
                if (PosPub.isNumericTypeMinus(goods.getBoxPrice())==false) goods.setBoxPrice("0");

                if (Check.Null(goods.getTaxRate()) ) goods.setTaxRate("0");
                if (PosPub.isNumericTypeMinus(goods.getTaxRate())==false) goods.setTaxRate("0");

                if (Check.Null(goods.getPackageMitem()) ) goods.setPackageMitem("0");
                if (PosPub.isNumericTypeMinus(goods.getPackageMitem())==false) goods.setPackageMitem("0");

                if (Check.Null(goods.getFeatureNo()) ) goods.setFeatureNo(" ");

                //【ID1039790】 【大万-3.0】库存表中门店和仓库信息不匹配。  by jinzma 20240326
                //此处重新赋值仓库，因为订转销业绩算下单门店的时候，只改了单头的门店，单身的仓库还是原来的门店所属仓库
                if (Check.Null(goods.getWarehouse()) || Check.Null(warehouse)){
                    goods.setWarehouse(sd_warehouse);
                }else {
                    goods.setWarehouse(warehouse);
                }


                if (Check.Null(goods.getPackageType()) ) goods.setPackageType("1");
                if (Check.Null(goods.getVirtual()) ) goods.setVirtual("N");

                if (Check.Null(goods.getDisc_merReceive()) ) goods.setDisc_merReceive(goods.getDisc());
                if (PosPub.isNumericTypeMinus(goods.getDisc_merReceive())==false) goods.setDisc_merReceive(goods.getDisc());

                if (Check.Null(goods.getDisc_custPayReal()) ) goods.setDisc_custPayReal(goods.getDisc());
                if (PosPub.isNumericTypeMinus(goods.getDisc_custPayReal())==false) goods.setDisc_custPayReal(goods.getDisc());

                if (Check.Null(goods.getAmt_merReceive()) ) goods.setAmt_merReceive(goods.getAmt());
                if (PosPub.isNumericTypeMinus(goods.getAmt_merReceive())==false) goods.setAmt_merReceive(goods.getAmt());

                if (Check.Null(goods.getAmt_custPayReal()) ) goods.setAmt_custPayReal(goods.getAmt());
                if (PosPub.isNumericTypeMinus(goods.getAmt_custPayReal())==false) goods.setAmt_custPayReal(goods.getAmt());

                if(goods.getPluName()==null) goods.setPluName("");
                //解码
                goods.setPluName(goods.getPluName());

                if(goods.getFeatureName()==null) goods.setFeatureName("");
                //解码
                goods.setFeatureName(goods.getFeatureName());

                if(goods.getWarehouseName()==null) goods.setWarehouseName("");
                //解码
                goods.setWarehouseName(goods.getWarehouseName());

                if(goods.getsUnitName()==null) goods.setsUnitName("");
                //解码
                goods.setsUnitName(goods.getsUnitName());

                if(goods.getSpecName()==null) goods.setSpecName("");
                //解码
                goods.setSpecName(goods.getSpecName());

                if(goods.getAttrName()==null) goods.setAttrName("");
                //解码
                goods.setAttrName(goods.getAttrName());

                if(goods.getSellerName()==null) goods.setSellerName("");
                //解码
                goods.setSellerName(goods.getSellerName());
                if(goods.getGiftReason()==null) goods.setGiftReason("");
                //解码
                goods.setGiftReason(goods.getGiftReason());

                if(goods.getPluNo()==null) goods.setPluNo("");
                //解码
                goods.setPluNo(goods.getPluNo());

                if(goods.getPluBarcode()==null) goods.setPluBarcode("");
                //解码
                goods.setPluBarcode(goods.getPluBarcode());

                detail.setAccno(goods.getAccNo());
                detail.setAmt(goods.getAmt());
                detail.setbDate(sale.getbDate());
                detail.setBsno(otpReq.getRefundReasonNo());
                //修复营业员编号和营业员名称赋值错误   by jinzma 20230915
                detail.setClerkName(goods.getSellerName());    //detail.setClerkName(goods.getSellerNo());
                detail.setClerkNo(goods.getSellerNo());        //detail.setClerkNo(goods.getSellerName());

                detail.setCounterNo(goods.getCounterNo());
                detail.setCouponCode(goods.getCouponType());
                detail.setCouponType(goods.getCouponCode());
                detail.setDetailItem(goods.getToppingMitem()!=null && PosPub.isNumeric(goods.getToppingMitem())?goods.getToppingMitem():"0");
                detail.setDisc(goods.getDisc());
                detail.seteId(eId);
                detail.setFeatureNo(goods.getFeatureNo());
                detail.setGiftReason(goods.getGiftReason());
                detail.setIsGift(goods.getGift());
                detail.setPackageMaster(goods.getPackageType().equals("2")?"Y":"N");
                detail.setIsPackage(goods.getPackageType().equals("2")||goods.getPackageType().equals("3")?"Y":"N");
                detail.setIsStuff(goods.getToppingType()!=null&&goods.getToppingType().equals("1")?"Y":"N");//是否加料商品Y/N
                detail.setItem(goods.getItem());
                detail.setInclTax(goods.getInclTax());
                detail.setOldAMT(goods.getOldAmt());
                detail.setOldPrice(goods.getOldPrice());
                detail.setPackageAmount(goods.getBoxNum());//餐盒数量
                detail.setPackagePrice(goods.getBoxPrice());
                BigDecimal bdm_boxnum=new BigDecimal(goods.getBoxNum());
                BigDecimal bdm_boxprice=new BigDecimal(goods.getBoxPrice());
                detail.setPackageFee(bdm_boxnum.multiply(bdm_boxprice).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString());//餐盒总价
                detail.setShopId(shopId);
                detail.setsDate(sDate);
                detail.setsTime(sTime);
                detail.setPluBarcode(goods.getPluBarcode());
                detail.setPluNo(goods.getPluNo());
                detail.setpName(goods.getPluName());
                detail.setPrice(goods.getPrice());
                detail.setQty(goods.getQty());
                detail.setTaxCode(goods.getTaxCode());
                detail.setTaxRate(goods.getTaxRate());
                detail.setTaxType(goods.getTaxType());
                detail.setUnit(goods.getsUnit());
                detail.setUpItem(goods.getPackageMitem());
                detail.setWarehouse(goods.getWarehouse());
                detail.setStatus("100");//有效否100-有效0-无效
                detail.setVirtual(goods.getVirtual());
                detail.setAmt_merReceive(goods.getAmt_merReceive());
                detail.setAmt_custPayReal(goods.getAmt_custPayReal());
                detail.setDisc_merReceive(goods.getDisc_merReceive());
                detail.setDisc_custPayReal(goods.getDisc_custPayReal());

                String sql_baseUnit="select BASEUNIT from dcp_goods  where eid='"+eId+"' and pluno='"+goods.getPluNo()+"' and status='100' ";
                List<Map<String, Object>> getQbaseUnit=this.doQueryData(sql_baseUnit, null);
                if (getQbaseUnit != null && getQbaseUnit.isEmpty() == false)
                {
                    detail.setBaseUnit(getQbaseUnit.get(0).get("BASEUNIT").toString());//基准单位
                }
                else
                {
                    errorMessage.append("订单号orderno="+orderNo+",找不到dcp_goods表的eid="+eId+",pluno="+goods.getPluNo()+",status=100的基准单位(BASEUNIT)!");
                    return false;
                }

                detail.setAdditionalPrice("0");//加价
                detail.setAttr01("");//商品特性1
                detail.setAttr02("");//商品特性2
                detail.setBatchNo("");//批次号
                detail.setCakeBlessing("");//蛋糕祝福
                detail.setCanBack("");//是否可退Y/N
                detail.setConfirmOpno("");//确认人
                detail.setConfirmTime("");//确认时间
                detail.setCounterAmt("0");//专柜成交金额
                detail.setDealType("1");//商品交易类型：1销售2退货3赠送11免单
                detail.setDishesStatus("");//菜品状态
                detail.setFlavorStuffDetail("");//口味加料细节
                detail.setGiftOpno("");//赠送人编码
                detail.setGiftTime("");//赠送时间
                detail.setIsExchange("");//是否换购品Y/N
                detail.setIsPickGoods("");////是否现提商品Y/N
                detail.setMaterials("");//蛋糕坯子
                detail.setMemo("");//备注
                detail.setMno("");//商场码
                detail.setoItem(goods.getItem());//来源项次
                detail.setOrderRateAmount("0");//订金分摊金额
                detail.setOrderTime("");//预定时间
                detail.setPackageAmt("0");//套餐商品金额
                detail.setPackageQty("0");//套餐商品数量
                detail.setPayDisc("0");//付款折扣
                detail.setPointQty("0");//积分
                detail.setPrice2("0");//底价
                detail.setPrice3("0");//最高退价
                detail.setPromCouponNo("");//促销赠券券号
                detail.setRepastType("");//就餐类型：0堂食1打包2外卖
                detail.setRefundOpno("");//退菜人编号
                detail.setReturnTableNo("");//退货桌号
                detail.setRefundTime("");//退菜时间
                detail.setReturnUserId("");//退货人id
                detail.setrQty("0");//退货数量
                detail.setSaleDisc(goods.getDisc());//销售折扣金额
                detail.setScanNo("");//扫描码
                detail.setServCharge("0");//餐厅服务费
                detail.setShareAmt("0");//套餐分摊金额
                detail.setSocalled("");//等叫类型
                detail.setSrackNo("");//货架编号
                detail.setTableNo("");//桌台号
                detail.setTgCategoryNo("");//团务分类编号

                //调用库存流水
                String unitRatio="";
                String sql_ratio="select UNITRATIO from dcp_goods_unit  where eid='"+eId+"' and pluno='"+detail.getPluNo()+"' and unit='"+detail.getBaseUnit()+"' and ounit='"+detail.getUnit()+"' ";
                List<Map<String, Object>> getQratio=this.doQueryData(sql_ratio, null);
                if (getQratio != null && getQratio.isEmpty() == false)
                {
                    unitRatio=getQratio.get(0).get("UNITRATIO").toString();//换算比率
                }
                else
                {
                    errorMessage.append("找不到dcp_goods_unit表的eid="+eId+",pluno="+detail.getPluNo()+",unit="+detail.getBaseUnit()+",ounit="+detail.getUnit()+"的换算比率(UNITRATIO)!");
                    return false;
                }

                detail.setUnitRatio(new BigDecimal(unitRatio).doubleValue());
                detail.setBaseQty(new BigDecimal(detail.getQty()).multiply(new BigDecimal(unitRatio)).doubleValue());

                //累加数量处理
                bdm_CurrentTotQty=bdm_CurrentTotQty.add(new BigDecimal(detail.getQty()));

                //套餐主商品或者普通商品
                if ("Y".equals(detail.getPackageMaster()) || "N".equals(detail.getIsPackage()))
                {
                    saleamt=saleamt.add(new BigDecimal(detail.getAmt()));
                    saledisc=saledisc.add(new BigDecimal(detail.getDisc()));
                }

                detail.setFlavorStuffDetail(goods.getFlavorStuffDetail());
                detail.setDetailAgios(new ArrayList<>());
                if (goods.getAgioInfo()==null)goods.setAgioInfo(new ArrayList<>());

                for (Agio rAgio : goods.getAgioInfo())
                {
                    SALE.DetailAgio agio=sale.new DetailAgio();

                    if (Check.Null(rAgio.getDisc()) ) rAgio.setDisc("0");
                    if (PosPub.isNumericTypeMinus(rAgio.getDisc())==false) rAgio.setDisc("0");

                    if (Check.Null(rAgio.getDisc_merReceive()) ) rAgio.setDisc_merReceive(rAgio.getDisc());
                    if (PosPub.isNumericTypeMinus(rAgio.getDisc_merReceive())==false) rAgio.setDisc_merReceive(rAgio.getDisc());

                    if (Check.Null(rAgio.getDisc_custPayReal()) ) rAgio.setDisc_custPayReal(rAgio.getDisc());
                    if (PosPub.isNumericTypeMinus(rAgio.getDisc_custPayReal())==false) rAgio.setDisc_custPayReal(rAgio.getDisc());

                    agio.seteId(eId);
                    agio.setShopId(shopId);
                    agio.setStatus("100");
                    agio.setAmt(rAgio.getAmt());
                    agio.setBsno(rAgio.getBsNo());
                    agio.setDcType(rAgio.getDcType());
                    agio.setDcTypeName(rAgio.getDcTypeName());
                    agio.setDisc(rAgio.getDisc());
                    agio.setGiftctf(rAgio.getGiftCtf());
                    agio.setGiftctfNo(rAgio.getGiftCtfNo());
                    agio.setItem(rAgio.getItem());
                    agio.setmItem(detail.getItem());
                    agio.setPmtNo(rAgio.getPmtNo());
                    agio.setQty(rAgio.getQty());
                    agio.setRealDisc(rAgio.getRealDisc());
                    agio.setOrderToSalePayAgioFlag(rAgio.getOrderToSalePayAgioFlag());
                    agio.setDisc_merReceive(rAgio.getDisc_merReceive());
                    agio.setDisc_custPayReal(rAgio.getDisc_custPayReal());

                    agio.setDcOpno("");//打折人/授权人
                    agio.setDcTime("");//打折时间
                    agio.setMemo("");//折扣备注

                    detail.getDetailAgios().add(agio);

					/*订单折扣没什么用，订转销不回写
					//付款方式折扣处理，这里要注意订单创建时的付款折扣不能要，不然重复了
					if (agio.getOrderToSalePayAgioFlag()!=null && agio.getOrderToSalePayAgioFlag().equals("Y") && agio.getDcType()!=null && agio.getDcType().equals("60"))
					{
						//有尾款才更新折扣表
					    if (bFinalPayDisc)
					    {
							//DCP_ORDER_DETAIL_AGIO
							String[] Columns_DCP_ORDER_DETAIL_AGIO = {
									"EID","ORDERNO","MITEM","ITEM","QTY","AMT","INPUTDISC","REALDISC","DISC","DCTYPE",
									"DCTYPENAME","PMTNO","GIFTCTF","GIFTCTFNO","BSNO","DISC_MERRECEIVE","DISC_CUSTPAYREAL"
							};
							DataValue[] insValue_DCP_ORDER_DETAIL_AGIO = new DataValue[]{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(orderNo, Types.VARCHAR),
									new DataValue(agio.getmItem(), Types.VARCHAR),
									new DataValue(agio.getItem(), Types.VARCHAR),
									new DataValue(agio.getQty(), Types.VARCHAR),
									new DataValue(agio.getAmt(), Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue(agio.getDisc(), Types.VARCHAR),
									new DataValue(agio.getDcType(), Types.VARCHAR),
									new DataValue(agio.getDcTypeName(), Types.VARCHAR),
									new DataValue(agio.getPmtNo(), Types.VARCHAR),
									new DataValue(agio.getGiftctf(), Types.VARCHAR),
									new DataValue(agio.getGiftctfNo(), Types.VARCHAR),
									new DataValue(agio.getBsno(), Types.VARCHAR),
									new DataValue(agio.getDisc_merReceive(), Types.VARCHAR),
									new DataValue(agio.getDisc_custPayReal(), Types.VARCHAR),

							};
							InsBean ib_DCP_ORDER_DETAIL_AGIO = new InsBean("DCP_ORDER_DETAIL_AGIO", Columns_DCP_ORDER_DETAIL_AGIO);
							ib_DCP_ORDER_DETAIL_AGIO.addValues(insValue_DCP_ORDER_DETAIL_AGIO);
							data.add(new DataProcessBean(ib_DCP_ORDER_DETAIL_AGIO));
					    }
					}
					*/

                    //DCP_SALE_DETAIL_AGIO
                    String[] Columns_DCP_SALE_DETAIL_AGIO = {
                            "EID","SHOPID","SALENO","MITEM","ITEM","QTY","AMT","DISC","REALDISC","DCTYPE",
                            "DCTYPENAME","PMTNO","GIFTCTF","GIFTCTFNO","BSNO","DCOPNO","DCTIME","MEMO",
                            "STATUS","DISC_MERRECEIVE","DISC_CUSTPAYREAL","PARTITION_DATE"
                    };
                    DataValue[] insValue_DCP_SALE_DETAIL_AGIO = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(saleno, Types.VARCHAR),
                            new DataValue(agio.getmItem(), Types.VARCHAR),
                            new DataValue(agio.getItem(), Types.VARCHAR),
                            new DataValue(agio.getQty(), Types.VARCHAR),
                            new DataValue(agio.getAmt(), Types.DECIMAL),
                            new DataValue(agio.getDisc(), Types.DECIMAL),
                            new DataValue(agio.getRealDisc(), Types.DECIMAL),
                            new DataValue(agio.getDcType(), Types.VARCHAR),
                            new DataValue(agio.getDcTypeName(), Types.VARCHAR),
                            new DataValue(agio.getPmtNo(), Types.VARCHAR),
                            new DataValue(agio.getGiftctf(), Types.VARCHAR),
                            new DataValue(agio.getGiftctfNo(), Types.VARCHAR),
                            new DataValue(agio.getBsno(), Types.VARCHAR),
                            new DataValue(agio.getDcOpno(), Types.VARCHAR),
                            new DataValue(agio.getDcTime(), Types.VARCHAR),
                            new DataValue(agio.getMemo(), Types.VARCHAR),
                            new DataValue(agio.getStatus(), Types.VARCHAR),
                            new DataValue(agio.getDisc_merReceive(), Types.VARCHAR),
                            new DataValue(agio.getDisc_custPayReal(), Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.NUMERIC)//分区字段

                    };
                    InsBean ib_DCP_SALE_DETAIL_AGIO = new InsBean("DCP_SALE_DETAIL_AGIO", Columns_DCP_SALE_DETAIL_AGIO);//分区字段已处理
                    ib_DCP_SALE_DETAIL_AGIO.addValues(insValue_DCP_SALE_DETAIL_AGIO);
                    data.add(new DataProcessBean(ib_DCP_SALE_DETAIL_AGIO));

                    //更新值
                    if(isOrderToSaleAll.equals("1") && !agio.getDcType().equals("60") )
                    {
                        //更新(主)订单明细表DCP_ORDER_DETAIL
                        UptBean ub_DCP_ORDER_DETAIL_AGIO = new UptBean("DCP_ORDER_DETAIL_AGIO");
                        ub_DCP_ORDER_DETAIL_AGIO.addUpdateValue("QTY_SALE", new DataValue(agio.getQty(), Types.DECIMAL,DataExpression.UpdateSelf));//已提货数量
                        //已提货金额
                        ub_DCP_ORDER_DETAIL_AGIO.addUpdateValue("AMT_SALE", new DataValue(agio.getAmt(), Types.DECIMAL,DataExpression.UpdateSelf));
                        //已提货折扣
                        ub_DCP_ORDER_DETAIL_AGIO.addUpdateValue("DISC_SALE", new DataValue(agio.getDisc(), Types.DECIMAL,DataExpression.UpdateSelf));
                        //已提货客户实付折扣
//						ub_DCP_ORDER_DETAIL_AGIO.addUpdateValue("DISC_CUSTPAYREAL_SALE", new DataValue(agio.getDisc_custPayReal(), Types.DECIMAL,DataExpression.UpdateSelf));
                        //已提货商家实收折扣
//						ub_DCP_ORDER_DETAIL_AGIO.addUpdateValue("DISC_MERRECEIVE_SALE", new DataValue(agio.getDisc_merReceive(), Types.DECIMAL,DataExpression.UpdateSelf));
                        //更新条件
                        ub_DCP_ORDER_DETAIL_AGIO.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub_DCP_ORDER_DETAIL_AGIO.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                        ub_DCP_ORDER_DETAIL_AGIO.addCondition("ITEM", new DataValue(agio.getItem(), Types.VARCHAR));
                        ub_DCP_ORDER_DETAIL_AGIO.addCondition("MITEM", new DataValue(agio.getmItem(), Types.VARCHAR));
                        data.add(new DataProcessBean(ub_DCP_ORDER_DETAIL_AGIO));

                    }
                }

                sale.getDetails().add(detail);

                //DCP_SALE_DETAIL
                String[] Columns_DCP_SALE_DETAIL = {
                        "EID","SHOPID","SALENO","WAREHOUSE","ITEM","OITEM","CLERKNO","SELLERNAME",
                        "ACCNO","TABLENO","DEALTYPE","COUPONTYPE","COUPONCODE","PLUNO","PNAME",
                        "ISGIFT","GIFTREASON","GIFTOPNO","GIFTTIME","ISEXCHANGE","ISPICKGOOD",
                        "PLUBARCODE","SCANNO","MNO","COUNTERNO","SRACKNO","BATCHNO","TGCATEGORYNO",
                        "FEATURENO","ATTR01","ATTR02","UNIT","BASEUNIT","QTY","OLDPRICE","PRICE2",
                        "PRICE3","CANBACK","BSNO","RQTY","RETURNUSERID","RETURNTABLENO","REFUNDOPNO",
                        "REFUNDTIME","OLDAMT","DISC","SALEDISC","PAYDISC","PRICE","ADDITIONALPRICE",
                        "AMT","POINT_QTY","COUNTERAMT","SERVCHARGE","PACKAGEMASTER","ISPACKAGE",
                        "PACKAGEAMT","PACKAGEQTY","UPITEM","SHAREAMT","ISSTUFF","DETAILITEM",
                        "FLAVORSTUFFDETAIL","CAKEBLESSING","MATERIALS","DISHESSTATUS","SOCALLED",
                        "REPAST_TYPE","PACKAGEAMOUNT","PACKAGEPRICE","PACKAGEFEE","INCLTAX","TAXCODE",
                        "TAXTYPE","TAXRATE","ORDERRATEAMOUNT","MEMO","CONFIRMOPNO","CONFIRMTIME",
                        "ORDERTIME","STATUS","BDATE","SDATE","STIME","PROM_COUPONNO","DISC_MERRECEIVE",
                        "AMT_MERRECEIVE","DISC_CUSTPAYREAL","AMT_CUSTPAYREAL","PARTITION_DATE",
                        "UNITRATIO","BASEQTY"
                };
                DataValue[] insValue_DCP_SALE_DETAIL = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(saleno, Types.VARCHAR),
                        new DataValue(detail.getWarehouse(), Types.VARCHAR),
                        new DataValue(detail.getItem(), Types.VARCHAR),
                        new DataValue(detail.getoItem(), Types.VARCHAR),
                        new DataValue(detail.getClerkNo(), Types.VARCHAR),
                        new DataValue(detail.getClerkName(), Types.VARCHAR),
                        new DataValue(detail.getAccno(), Types.VARCHAR),
                        new DataValue(detail.getTableNo(), Types.VARCHAR),
                        new DataValue(detail.getDealType(), Types.VARCHAR),
                        new DataValue(detail.getCouponType(), Types.VARCHAR),
                        new DataValue(detail.getCouponCode(), Types.VARCHAR),
                        new DataValue(detail.getPluNo(), Types.VARCHAR),
                        new DataValue(detail.getpName(), Types.VARCHAR),
                        new DataValue(detail.getIsGift(), Types.VARCHAR),
                        new DataValue(detail.getGiftReason(), Types.VARCHAR),
                        new DataValue(detail.getGiftOpno(), Types.VARCHAR),
                        new DataValue(detail.getGiftTime(), Types.VARCHAR),
                        new DataValue(detail.getIsExchange(), Types.VARCHAR),
                        new DataValue(detail.getIsPickGoods(), Types.VARCHAR),
                        new DataValue(detail.getPluBarcode(), Types.VARCHAR),
                        new DataValue(detail.getScanNo(), Types.VARCHAR),
                        new DataValue(detail.getMno(), Types.VARCHAR),
                        new DataValue(detail.getCounterNo(), Types.VARCHAR),
                        new DataValue(detail.getSrackNo(), Types.VARCHAR),
                        new DataValue(detail.getBatchNo(), Types.VARCHAR),
                        new DataValue(detail.getTgCategoryNo(), Types.VARCHAR),
                        new DataValue(detail.getFeatureNo(), Types.VARCHAR),
                        new DataValue(detail.getAttr01(), Types.VARCHAR),
                        new DataValue(detail.getAttr02(), Types.VARCHAR),
                        new DataValue(detail.getUnit(), Types.VARCHAR),
                        new DataValue(detail.getBaseUnit(), Types.VARCHAR),
                        new DataValue(detail.getQty(), Types.VARCHAR),
                        new DataValue(detail.getOldPrice(), Types.VARCHAR),
                        new DataValue(detail.getPrice2(), Types.VARCHAR),
                        new DataValue(detail.getPrice3(), Types.VARCHAR),
                        new DataValue(detail.getCanBack(), Types.VARCHAR),
                        new DataValue(detail.getBsno(), Types.VARCHAR),
                        new DataValue(detail.getrQty(), Types.VARCHAR),
                        new DataValue(detail.getReturnUserId(), Types.VARCHAR),
                        new DataValue(detail.getReturnTableNo(), Types.VARCHAR),
                        new DataValue(detail.getRefundOpno(), Types.VARCHAR),
                        new DataValue(detail.getRefundTime(), Types.VARCHAR),
                        new DataValue(detail.getOldAMT(), Types.VARCHAR),
                        new DataValue(detail.getDisc(), Types.VARCHAR),
                        new DataValue(detail.getSaleDisc(), Types.VARCHAR),
                        new DataValue(detail.getPayDisc(), Types.VARCHAR),
                        new DataValue(detail.getPrice(), Types.VARCHAR),
                        new DataValue(detail.getAdditionalPrice(), Types.VARCHAR),
                        new DataValue(detail.getAmt(), Types.VARCHAR),
                        new DataValue(detail.getPointQty(), Types.VARCHAR),
                        new DataValue(detail.getCounterAmt(), Types.VARCHAR),
                        new DataValue(detail.getServCharge(), Types.VARCHAR),
                        new DataValue(detail.getPackageMaster(), Types.VARCHAR),
                        new DataValue(detail.getIsPackage(), Types.VARCHAR),
                        new DataValue(detail.getPackageAmt(), Types.VARCHAR),
                        new DataValue(detail.getPackageQty(), Types.VARCHAR),
                        new DataValue(detail.getUpItem(), Types.VARCHAR),
                        new DataValue(detail.getShareAmt(), Types.VARCHAR),
                        new DataValue(detail.getIsStuff(), Types.VARCHAR),
                        new DataValue(detail.getDetailItem(), Types.VARCHAR),
                        new DataValue(detail.getFlavorStuffDetail(), Types.VARCHAR),
                        new DataValue(detail.getCakeBlessing(), Types.VARCHAR),
                        new DataValue(detail.getMaterials(), Types.VARCHAR),
                        new DataValue(detail.getDishesStatus(), Types.VARCHAR),
                        new DataValue(detail.getSocalled(), Types.VARCHAR),
                        new DataValue(detail.getRepastType(), Types.VARCHAR),
                        new DataValue(detail.getPackageAmount(), Types.VARCHAR),
                        new DataValue(detail.getPackagePrice(), Types.VARCHAR),
                        new DataValue(detail.getPackageFee(), Types.VARCHAR),
                        new DataValue(detail.getInclTax(), Types.VARCHAR),
                        new DataValue(detail.getTaxCode(), Types.VARCHAR),
                        new DataValue(detail.getTaxType(), Types.VARCHAR),
                        new DataValue(detail.getTaxRate(), Types.VARCHAR),
                        new DataValue(detail.getOrderRateAmount(), Types.VARCHAR),
                        new DataValue(detail.getMemo(), Types.VARCHAR),
                        new DataValue(detail.getConfirmOpno(), Types.VARCHAR),
                        new DataValue(detail.getConfirmTime(), Types.VARCHAR),
                        new DataValue(detail.getOrderTime(), Types.VARCHAR),
                        new DataValue("100", Types.VARCHAR),
                        new DataValue(detail.getbDate(), Types.VARCHAR),
                        new DataValue(detail.getsDate(), Types.VARCHAR),
                        new DataValue(detail.getsTime(), Types.VARCHAR),
                        new DataValue(detail.getPromCouponNo(), Types.VARCHAR),
                        new DataValue(detail.getDisc_merReceive(), Types.VARCHAR),
                        new DataValue(detail.getAmt_merReceive(), Types.VARCHAR),
                        new DataValue(detail.getDisc_custPayReal(), Types.VARCHAR),
                        new DataValue(detail.getAmt_custPayReal(), Types.VARCHAR),
                        new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                        new DataValue(detail.getUnitRatio(), Types.FLOAT),
                        new DataValue(detail.getBaseQty(), Types.FLOAT),
                };
                InsBean ib_DCP_SALE_DETAIL = new InsBean("DCP_SALE_DETAIL", Columns_DCP_SALE_DETAIL);//分区字段已处理
                ib_DCP_SALE_DETAIL.addValues(insValue_DCP_SALE_DETAIL);
                data.add(new DataProcessBean(ib_DCP_SALE_DETAIL));

                //更新(主)订单明细表DCP_ORDER_DETAIL
                UptBean ub_DCP_ORDER_DETAIL = new UptBean("DCP_ORDER_DETAIL");
                //更新值
                if(isOrderToSaleAll.equals("1"))
                {
                    ub_DCP_ORDER_DETAIL.addUpdateValue("PICKQTY", new DataValue(goods.getQty(), Types.DECIMAL,DataExpression.UpdateSelf));//已提货数量
                    //已提货金额
                    ub_DCP_ORDER_DETAIL.addUpdateValue("WRITEOFFAMT", new DataValue(goods.getAmt(), Types.DECIMAL,DataExpression.UpdateSelf));
                    //已提货折扣
                    ub_DCP_ORDER_DETAIL.addUpdateValue("DISC_SALE", new DataValue(goods.getDisc(), Types.DECIMAL,DataExpression.UpdateSelf));
                    //已提货原价金额
                    ub_DCP_ORDER_DETAIL.addUpdateValue("OLDAMT_SALE", new DataValue(goods.getOldAmt(), Types.DECIMAL,DataExpression.UpdateSelf));
                }else
                {
                    ub_DCP_ORDER_DETAIL.addUpdateValue("PICKQTY", new DataValue(detail.getQty(), Types.DECIMAL,DataExpression.UpdateSelf));//已提货数量
                }
                //没有尾款折扣，不刷新订单数据
                if (bFinalPayDisc)
                {
                    //付款折扣栏位处理
                    ub_DCP_ORDER_DETAIL.addUpdateValue("DISC_MERRECEIVE", new DataValue(detail.getDisc_merReceive(), Types.DECIMAL));
                    ub_DCP_ORDER_DETAIL.addUpdateValue("AMT_MERRECEIVE", new DataValue(detail.getAmt_merReceive(), Types.DECIMAL));
                    ub_DCP_ORDER_DETAIL.addUpdateValue("DISC_CUSTPAYREAL", new DataValue(detail.getDisc_custPayReal(), Types.DECIMAL));
                    ub_DCP_ORDER_DETAIL.addUpdateValue("AMT_CUSTPAYREAL", new DataValue(detail.getAmt_custPayReal(), Types.DECIMAL));
                }

                //更新条件
                ub_DCP_ORDER_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub_DCP_ORDER_DETAIL.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                ub_DCP_ORDER_DETAIL.addCondition("ITEM", new DataValue(detail.getItem(), Types.VARCHAR));
                data.add(new DataProcessBean(ub_DCP_ORDER_DETAIL));

                //更新(子)订单明细表DCP_ORDER_DETAIL
                if (b_subOrderFlag)
                {
                    //更新(子)订单明细表DCP_ORDER_DETAIL
                    ub_DCP_ORDER_DETAIL = new UptBean("DCP_ORDER_DETAIL");
                    //更新值
                    ub_DCP_ORDER_DETAIL.addUpdateValue("PICKQTY", new DataValue(detail.getQty(), Types.DECIMAL,DataExpression.UpdateSelf));//已提货数量

                    //没有尾款折扣，不刷新订单数据
                    if (bFinalPayDisc)
                    {
                        //付款折扣栏位处理
                        ub_DCP_ORDER_DETAIL.addUpdateValue("DISC_MERRECEIVE", new DataValue(detail.getDisc_merReceive(), Types.DECIMAL));
                        ub_DCP_ORDER_DETAIL.addUpdateValue("AMT_MERRECEIVE", new DataValue(detail.getAmt_merReceive(), Types.DECIMAL));
                        ub_DCP_ORDER_DETAIL.addUpdateValue("DISC_CUSTPAYREAL", new DataValue(detail.getDisc_custPayReal(), Types.DECIMAL));
                        ub_DCP_ORDER_DETAIL.addUpdateValue("AMT_CUSTPAYREAL", new DataValue(detail.getAmt_custPayReal(), Types.DECIMAL));
                    }

                    //更新条件
                    ub_DCP_ORDER_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_ORDER_DETAIL.addCondition("ORDERNO", new DataValue(otpReq.getHeadOrderNo(), Types.VARCHAR));
                    ub_DCP_ORDER_DETAIL.addCondition("ITEM", new DataValue(detail.getItem(), Types.VARCHAR));
                    data.add(new DataProcessBean(ub_DCP_ORDER_DETAIL));
                }

                //虚拟商品及套餐主商品不处理库存逻辑
                if (detail.getVirtual().equals("N") && detail.getPackageMaster().equals("N"))
                {
                    //是否需要调用库存解锁服务DCP_StockUnlock
                    if (bDCP_StockUnlock)
                    {
                        if (req.getApiUser() == null)
                        {
                            //
                            JSONArray data_org = new JSONArray();
                            JSONObject detail_org = new JSONObject();
                            detail_org.put("organizationNo", otpReq.getShippingShopNo());
                            detail_org.put("warehouse", sd_warehouse);
                            detail_org.put("qty", detail.getQty());
                            data_org.put(detail_org);
                            //
                            JSONObject detail_plu = new JSONObject();
                            detail_plu.put("pluNo", detail.getPluNo());
                            detail_plu.put("featureNo", detail.getFeatureNo());
                            detail_plu.put("sUnit", detail.getUnit());
                            detail_plu.put("organizationList", data_org);
                            data_plu.put(detail_plu);
                        }
                        else
                        {
                            List<DCP_StockUnlock_OpenReq.OrgList> unOrglist=new ArrayList<>();
                            DCP_StockUnlock_OpenReq.OrgList tempORG=stockUnlock_openReq.new OrgList();
                            tempORG.setOrganizationNo(otpReq.getShippingShopNo());
                            tempORG.setWarehouse(sd_warehouse);
                            tempORG.setQty(detail.getQty());
                            unOrglist.add(tempORG);

                            DCP_StockUnlock_OpenReq.PluList tempPLU=stockUnlock_openReq.new PluList();
                            tempPLU.setPluNo(detail.getPluNo());
                            tempPLU.setFeatureNo(detail.getFeatureNo());
                            tempPLU.setsUnit(detail.getUnit());
                            tempPLU.setOrganizationList(unOrglist);

                            unplulist.add(tempPLU);
                        }
                    }




                    String procedure="SP_DCP_StockChange";
                    Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                    inputParameter.put(1,eId);                                       //--企业ID
                    inputParameter.put(2,shopId);                                    //--组织
                    inputParameter.put(3,"20");                                      //--单据类型
                    inputParameter.put(4,saleno);	                                 //--单据号
                    inputParameter.put(5,detail.getItem());            //--单据行号
                    inputParameter.put(6,"-1");                                      //--异动方向 1=加库存 -1=减库存
                    inputParameter.put(7,sale.getbDate());           //--营业日期 yyyy-MM-dd
                    inputParameter.put(8,detail.getPluNo());           //--品号
                    inputParameter.put(9,detail.getFeatureNo());       //--特征码
                    inputParameter.put(10,Check.Null(detail.getWarehouse())?sd_warehouse:detail.getWarehouse());                                //--仓库
                    inputParameter.put(11,detail.getBatchNo());       //--批号
                    inputParameter.put(12,detail.getUnit());          //--交易单位
                    inputParameter.put(13,detail.getQty());           //--交易数量
                    inputParameter.put(14,detail.getBaseUnit());       //--基准单位
                    inputParameter.put(15,new BigDecimal(detail.getQty()).multiply(new BigDecimal(unitRatio)).toPlainString());        //--基准数量
                    inputParameter.put(16,unitRatio);     //--换算比例
                    inputParameter.put(17,detail.getPrice());          //--零售价
                    inputParameter.put(18,detail.getAmt());            //--零售金额
                    inputParameter.put(19,"0");    //--进货价
                    inputParameter.put(20,"0");      //--进货金额
                    inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                    inputParameter.put(22,"");      //--批号的生产日期 yyyy-MM-dd
                    inputParameter.put(23,sDate);                                  //--单据日期
                    inputParameter.put(24,"");                                       //--异动原因
                    inputParameter.put(25,"订转销扣库存");                                //--异动描述
                    inputParameter.put(26,"");                                //--操作员

                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                    data.add(new DataProcessBean(pdb));
                }
                else
                {
                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_OrderToSaleProcess_Open:no writing不写库存流水原因:订单号=" + orderNo +",pluno=" + detail.getPluNo()+",virtual=" +detail.getVirtual() + ",packageMaster=" +detail.getPackageMaster());
                }

                //调用发票试算接口
                //零售单/订单：不包含套餐子商品
                if (goods.getPackageType()!=null && (goods.getPackageType().equals("1") ||goods.getPackageType().equals("2")))
                {
                    JSONObject req_goods = new JSONObject();
                    req_goods.put("invItem", Check.Null(goods.getInvItem()) || goods.getInvItem().equals("0")?"1":goods.getInvItem());//发票项次
                    req_goods.put("invNo", goods.getInvNo());
                    req_goods.put("item", goods.getItem());
                    req_goods.put("oItem", goods.getItem());//开发票用
                    req_goods.put("inclTax", goods.getInclTax());////开发票用
                    req_goods.put("mItem", goods.getToppingMitem());
                    req_goods.put("pluNo", goods.getPluNo());
                    req_goods.put("pluName", goods.getPluName());
                    req_goods.put("barcode", goods.getPluBarcode());
                    req_goods.put("taxRate", goods.getTaxRate());
                    req_goods.put("taxCode", goods.getTaxCode());
                    req_goods.put("qty", goods.getQty());
                    req_goods.put("amt", goods.getAmt());
                    datas_goodsList.put(req_goods);
                }

                if(isSellCredit)
                {
                    //普通商品或套餐子商品金额累加
                    if ((detail.getIsPackage() !=null && !"Y".equals(detail.getIsPackage())) || (detail.getPackageMaster()!=null && !"Y".equals(detail.getPackageMaster())))
                    {
                        //顺便加入赊销列表
                        JindieGoodsDetail lv2Detail = new JindieGoodsDetail();
                        lv2Detail.setItem(Integer.parseInt(detail.getItem()));
                        lv2Detail.setoItem(Integer.parseInt(detail.getItem()));
                        lv2Detail.setoQty(new BigDecimal(detail.getQty()).setScale(2, RoundingMode.HALF_UP));
                        lv2Detail.setQty(new BigDecimal(detail.getQty()).setScale(2, RoundingMode.HALF_UP));
                        lv2Detail.setPluNo(detail.getPluNo());
                        lv2Detail.setUnitId(detail.getUnit());
                        lv2Detail.setOldPrice(new BigDecimal(detail.getOldPrice()).setScale(2, RoundingMode.HALF_UP));
                        lv2Detail.setPrice(new BigDecimal(detail.getPrice()).setScale(2, RoundingMode.HALF_UP));
                        //抹零会算在支付折扣，取DISC_MERRECEIVE+DISC
                        lv2Detail.setDisc(new BigDecimal(detail.getDisc()).add(new BigDecimal(detail.getDisc_merReceive())).setScale(2, RoundingMode.HALF_UP));
                        //抹零会算在支付折扣，取AMT_MERRECEIVE
                        lv2Detail.setAmt(new BigDecimal(detail.getAmt_merReceive()).setScale(2, RoundingMode.HALF_UP));
                        lv2Detail.setOldAmt(lv2Detail.getAmt().add(lv2Detail.getDisc()).setScale(2, RoundingMode.HALF_UP));
                        lv2Detail.setMemo("");
                        details.add(lv2Detail);
                    }

                }

            }


            //赊销订单调用CRM接口
            String errorMsg="";
            if(isSellCredit)
            {
                boolean success_credit=false;
                try
                {
                    StringBuffer error_sellCredit = new StringBuffer("");

                    String apiUserCode="";
                    String apiUserKey="";
                    //内部服务形式调用
                    if (req.getApiUser() == null)
                    {
                        String sqlDef="select t.item,t.ITEMVALUE from platform_basesettemp t where (t.item='ApiUserKey' or t.item='ApiUserCode') and t.EID='"+eId+"'  and t.status='100' ";
                        List<Map<String, Object>> getQDataDef = this.doQueryData(sqlDef, null);
                        if (getQDataDef != null && getQDataDef.isEmpty() == false)
                        {
                            for (Map<String, Object> par : getQDataDef)
                            {
                                if(par.get("ITEM").toString().equals("ApiUserCode"))
                                {
                                    apiUserCode=par.get("ITEMVALUE").toString();
                                }

                                if(par.get("ITEM").toString().equals("ApiUserKey"))
                                {
                                    apiUserKey=par.get("ITEMVALUE").toString();
                                }
                            }
                        }
                        getQDataDef=null;
                    }
                    else
                    {
                        apiUserCode=req.getApiUserCode();
                        apiUserKey=req.getApiUser().getUserKey();
                    }

                    //金蝶赊销增加营业日期
                    String bdate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    if (otpReq.getOpBDate()!=null && otpReq.getOpBDate().length()==8)
                    {
                        bdate=otpReq.getOpBDate().substring(0,4)+"-" + otpReq.getOpBDate().substring(4,6) +"-" +otpReq.getOpBDate().substring(6,8);
                    }

                    success_credit=HelpTools.CustomerCreditUpdate(eId, apiUserCode, apiUserKey, req.getLangType(), saleno, req.getRequest().getBelfirm(),
                            req.getRequest().getShopNo(), "3",req.getRequest().getOpOpName(),req.getRequest().getMemo(),orderNo,details,
                            req.getRequest().getCustomer(), dealCreditAmount, error_sellCredit,bdate);
                }
                catch (Exception e)
                {
                    errorMsg=e.getMessage();
                }
                //赊销调用失败，不能进行下去
                if (!success_credit)
                {
                    errorMessage.append(errorMsg);
                    return false;
                }
            }


            //保留2位小数吧
            bdm_tot_merdiscount=bdm_tot_merdiscount.setScale(2,BigDecimal.ROUND_HALF_UP);
            bdm_tot_thirddiscount=bdm_tot_thirddiscount.setScale(2,BigDecimal.ROUND_HALF_UP);
            bdm_tot_merreceive=bdm_tot_merreceive.setScale(2,BigDecimal.ROUND_HALF_UP);
            bdm_tot_custpayreal=bdm_tot_custpayreal.setScale(2,BigDecimal.ROUND_HALF_UP);

            //源于付款+抹零
            sale.setTotDisc_merReceive(bdm_tot_merdiscount.toPlainString());
            sale.setTotDisc_custPayReal(bdm_tot_merdiscount.add(bdm_tot_thirddiscount).toPlainString());

            //源于付款
            sale.setTot_Amt_merReceive(bdm_tot_merreceive.toPlainString());
            sale.setTot_Amt_custPayReal(bdm_tot_custpayreal.toPlainString());

            //已付金额>=(getTotAmt-getEraseAmt)
            if (bdm_tot_payamt.compareTo(new BigDecimal(sale.getTotAmt()).subtract(new BigDecimal(sale.getEraseAmt())))>=0)
            {
                bPayComplete=true;
            }

            //是否需要调用库存解锁服务DCP_StockUnlock
            if (bDCP_StockUnlock)
            {
                try
                {
                    String bdate_DSUL=otpReq.getbDate().substring(0,4)+"-" + otpReq.getbDate().substring(4,6) +"-" +otpReq.getbDate().substring(6,8);

                    if (req.getApiUser() == null)
                    {
                        JSONObject req_DSUL = new JSONObject();
                        req_DSUL.put("serviceId","DCP_StockUnlock");
                        req_DSUL.put("token",req.getToken());
                        // //JOB调订转销用到，没token
                        req_DSUL.put("eId",eId);
                        req_DSUL.put("eShop",shopId);
                        //
                        JSONObject request_DSUL = new JSONObject();
                        request_DSUL.put("billNo", orderNo);
                        request_DSUL.put("billType", "Order");
                        request_DSUL.put("channelId", otpReq.getChannelId());
                        request_DSUL.put("unLockType", "1");//解锁类型出： 1：库解锁   （订转销） -1：订单取消解锁（退订）
                        request_DSUL.put("bDate", bdate_DSUL);
                        request_DSUL.put("pluList", data_plu);

                        req_DSUL.put("request", request_DSUL);
                        String str_DSUL = req_DSUL.toString();// 将json对象转换为字符串

                        PosPub.WriteETLJOBLog("訂轉銷調用庫存解鎖服務DCP_StockUnlock請求： "+ str_DSUL);
                        DispatchService ds = DispatchService.getInstance();
                        String resbody_DSUL = ds.callService(str_DSUL, this.dao);
                        PosPub.WriteETLJOBLog("訂轉銷調用庫存解鎖服務DCP_StockUnlock返回： "+ resbody_DSUL);

                        JSONObject jsonres_DSUL = new JSONObject(resbody_DSUL);
                        boolean success = jsonres_DSUL.getBoolean("success");
                        String serviceDescription_DSUL=jsonres_DSUL.get("serviceDescription")==null? "serviceDescription为空":jsonres_DSUL.get("serviceDescription").toString();
                        if (success==false)
                        {
                            errorMessage.append("调用库存解锁服务DCP_StockUnlock失败! "+serviceDescription_DSUL);
                            return false;
                        }
                    }
                    else
                    {

                        DCP_StockUnlock_OpenReq.levelReq unlockrequest=stockUnlock_openReq.new levelReq();
                        unlockrequest.setBillNo(orderNo);
                        unlockrequest.setBillType("Order");
                        unlockrequest.setChannelId(otpReq.getChannelId());
                        unlockrequest.setUnLockType("1");//解锁类型出： 1：库解锁   （订转销） -1：订单取消解锁（退订）
                        unlockrequest.setbDate(bdate_DSUL);

                        unlockrequest.setPluList(unplulist);
                        stockUnlock_openReq.setRequest(unlockrequest);
                        stockUnlock_openReq.seteId(req.geteId());
                        stockUnlock_openReq.setLangType(req.getLangType());
                        stockUnlock_openReq.setApiUserCode(req.getApiUserCode());
                        stockUnlock_openReq.setApiUser(req.getApiUser());

                        DCP_StockUnlock_Open stockUnlock_open=new DCP_StockUnlock_Open();
                        DCP_StockUnlock_OpenRes stockUnlock_openRes=new DCP_StockUnlock_OpenRes();
                        stockUnlock_open.setDao(this.dao);
                        stockUnlock_open.processDUID(stockUnlock_openReq,stockUnlock_openRes);

                        if (stockUnlock_openRes.isSuccess() == false)
                        {
                            String serviceDescription_DSUL=Check.Null(stockUnlock_openRes.getServiceDescription())?"返回空":stockUnlock_openRes.getServiceDescription();
                            errorMessage.append("调用库存解锁服务DCP_StockUnlock_Open失败! " +serviceDescription_DSUL);
                            return false;
                        }

						/*
						//
						JSONObject request_DSUL = new JSONObject();
						request_DSUL.put("billNo", orderNo);
						request_DSUL.put("billType", "Order");
						request_DSUL.put("channelId", otpReq.getChannelId());
						request_DSUL.put("unLockType", "1");//解锁类型出： 1：库解锁   （订转销） -1：订单取消解锁（退订）
						request_DSUL.put("bDate", bdate_DSUL);
						request_DSUL.put("pluList", data_plu);

						//
						Map<String, Object> map = new HashMap<>();
						map.put("serviceId", "DCP_StockUnlock_Open");
						map.put("apiUserCode",req.getApiUserCode());
						map.put("sign",PosPub.encodeMD5(request_DSUL.toString() + req.getApiUser().getUserKey()));
						map.put("langType",req.getLangType());
						map.put("requestId",PosPub.getGUID(false));
						map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
						map.put("version","3.0");

						String platformUrl = PosPub.getPARA_SMS(this.dao, eId, "", "PlatformCentreURL");

						PosPub.WriteETLJOBLog("訂轉銷調用庫存解鎖服務DCP_StockUnlock_Open請求： "+ request_DSUL.toString());

						String resbody_DSUL=HttpSend.doPost(platformUrl,request_DSUL.toString(),map);
						map.clear();
						map=null;

						PosPub.WriteETLJOBLog("訂轉銷調用庫存解鎖服務DCP_StockUnlock_Open返回： "+ resbody_DSUL);

						JSONObject jsonres_DSUL = new JSONObject(resbody_DSUL);
						boolean success = jsonres_DSUL.getBoolean("success");

						String serviceDescription_DSUL=jsonres_DSUL.get("serviceDescription")==null? "serviceDescription为空":jsonres_DSUL.get("serviceDescription").toString();

						if (success==false)
						{
							errorMessage.append("调用库存解锁服务DCP_StockUnlock_Open失败! " +serviceDescription_DSUL);
							return false;
						}
						*/
                    }
                }
                catch (Exception e)
                {
                    errorMessage.append("发现异常-调用库存解锁服务DCP_StockUnlock_Open失败!");
                    return false;
                }
            }

            //本次订转销之后单据就提货完成标记
            boolean bPickqtyOk=true;
            String tot_RemainQTY=getTot_RemainQTY(eId, orderNo);
            if (Check.Null(tot_RemainQTY))
            {
                errorMessage.append("计算剩余商品数量报错 !"  );
                return false;
            }
            BigDecimal bdm_tot_RemainQTY=new BigDecimal(tot_RemainQTY);

            if (bdm_CurrentTotQty.compareTo(bdm_tot_RemainQTY)>0)
            {
                errorMessage.append("orderno="+orderNo+",status="+otpReq.getStatus()+",refundstatus="+otpReq.getRefundStatus()+",本次商品数量("+bdm_CurrentTotQty+")>订单剩余可提货量("+bdm_tot_RemainQTY+")，请检查数量栏位 !"  );
                return false;
            }

            if (bdm_CurrentTotQty.compareTo(bdm_tot_RemainQTY)==0)
            {
                bPickqtyOk=true;
            }
            else
            {
                bPickqtyOk=false;
            }

            //积分累计或301消费扣款处理
            if (isNeedMemberpay&&
                    (Check.Null(otpReq.getMemberId())==false ||  Check.Null(otpReq.getCardNo())==false || payslistArray.size()>0 || cardlistArray.size()>0 || couponlistArray.size()>0))
            {
                String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + eId + "'" +
                        " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' or ITEM = 'CrmUrl')";
                List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
                if (result != null && result.size() > 0)
                {
                    for (Map<String, Object> map : result)
                    {
                        //内部服务调用形式的才取数据库，外部的用传进来的接口帐号
                        if (req.getApiUser() == null)
                        {
                            if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode"))
                            {
                                Yc_Key = map.get("ITEMVALUE").toString();
                            }
                            if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserKey"))
                            {
                                Yc_Sign_Key = map.get("ITEMVALUE").toString();
                            }
                        }else
                        {
                            Yc_Key=req.getApiUserCode();
                            Yc_Sign_Key=req.getApiUser().getUserKey();
                        }

                        if (map.get("ITEM") != null && map.get("ITEM").toString().equals("CrmUrl"))
                        {
                            Yc_Url = map.get("ITEMVALUE").toString();
                        }
                    }
                }
                Yc_Url=PosPub.getCRM_INNER_URL(eId);
                if(Yc_Url.trim().equals(""))
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "CrmUrl会员服务地址参数未设置!");
                }
                if(Yc_Key.trim().equals(""))
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode接入账号参数未设置!");
                }
                if(Yc_Sign_Key.trim().equals("") &&  null == req.getApiUser() )
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserKey接入密钥参数未设置!");
                }
                com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
                for (SALE.Detail detail : sale.getDetails())
                {
                    com.alibaba.fastjson.JSONObject goods=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                    goods.put("goods_id",detail.getPluBarcode());
                    goods.put("goods_name",detail.getpName());
                    goods.put("price",detail.getPrice());
                    goods.put("quantity",detail.getQty());
                    goods.put("amount",detail.getAmt());
                    goods.put("allowPoint","1");
                    goodslistArray.add(goods);
                }

                //卡号为空不用传了
                if (otpReq.getCardNo().equals("")==false && listCardno.contains(otpReq.getCardNo())==false)
                {
                    //
                    com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                    tempCard.put("cardNo",otpReq.getCardNo());
                    tempCard.put("amount","0");//只处理积分
                    tempCard.put("getPoint","0");
                    if(!Check.Null(otpReq.getPartnerMember()))
                    {
                        tempCard.put("partnerMember", otpReq.getPartnerMember());
                    }else
                    {
                        tempCard.put("partnerMember","digiwin");
                    }

                    cardlistArray.add(tempCard);
                }


                reqheader.put("orderNo", memberOrderno);//需唯一
                reqheader.put("businessType", "2");//业务类型0.其他1.订单下订2.订单提货3.零售支付
                reqheader.put("srcBillType", "订转销");//实际业务单别
                reqheader.put("srcBillNo", orderNo);//实际业务单号
                reqheader.put("orderAmount", otpReq.getTot_Amt());//
                reqheader.put("pointAmount", otpReq.getTot_Amt());//
                reqheader.put("memberId",otpReq.getMemberId() );//
                reqheader.put("orgType", "2");//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
                reqheader.put("orgId", otpReq.getOpShopId());//订转销取操作门店
                //【ID1039870】小程序支付成功，订单中心门店视角发货报错。提示卡不能在本店使用。
                if(orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType))
                {
                    reqheader.put("orgType", "3");
                    reqheader.put("orgId", channelId);
                }
                reqheader.put("oprId", otpReq.getOpNo());//
                reqheader.put("goodsdetail", goodslistArray);
                reqheader.put("cards", cardlistArray);
                reqheader.put("coupons", couponlistArray);
                reqheader.put("payDetail", payslistArray);

                //
                String req_sign=reqheader.toString() + Yc_Sign_Key;

                req_sign=DigestUtils.md5Hex(req_sign);

                //
                signheader.put("key", Yc_Key);//
                signheader.put("sign", req_sign);//md5

                payReq.put("serviceId", "MemberPay");

                payReq.put("request", reqheader);
                payReq.put("sign", signheader);


                String str = payReq.toString();

                PosPub.WriteETLJOBLog("会员积分接口MemberPay请求内容："+str +"\r\n");

                String	resbody = "";

                //编码处理
                str=URLEncoder.encode(str,"UTF-8");

                resbody=HttpSend.Sendcom(str, Yc_Url);

                PosPub.WriteETLJOBLog("会员积分接口MemberPay返回："+resbody +"\r\n");

                //获得积分
                BigDecimal getPoint=new BigDecimal(0);

                if (resbody.equals("")==false)
                {
                    com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
                    String serviceDescription="";
                    String serviceStatus="000";
                    if(null!=jsonres.get("serviceDescription"))
                    {
                        serviceDescription=jsonres.get("serviceDescription").toString();
                    }
                    if(null!=jsonres.get("serviceStatus"))
                    {
                        serviceStatus=jsonres.get("serviceStatus").toString();
                    }

                    //单号重复,直接查询积分
                    if (serviceStatus.equals("900"))
                    {
                        reqheader.clear();
                        signheader.clear();
                        payReq.clear();

                        reqheader.put("orderNo", memberOrderno);
                        req_sign=reqheader.toString() + Yc_Sign_Key;
                        req_sign=DigestUtils.md5Hex(req_sign);

                        //
                        signheader.put("key", Yc_Key);//
                        signheader.put("sign", req_sign);//md5

                        payReq.put("serviceId", "MemberPayQuery");

                        payReq.put("request", reqheader);
                        payReq.put("sign", signheader);

                        str = payReq.toString();

                        PosPub.WriteETLJOBLog("会员积分接口MemberPayQuery请求内容："+str +"\r\n");

                        //编码处理
                        str=URLEncoder.encode(str,"UTF-8");

                        resbody=HttpSend.Sendcom(str, Yc_Url);

                        PosPub.WriteETLJOBLog("会员积分接口MemberPayQuery返回："+resbody +"\r\n");

                        if (resbody.equals("")==false)
                        {
                            jsonres = JSON.parseObject(resbody);

                            serviceDescription=jsonres.get("serviceDescription").toString();
                            serviceStatus=jsonres.get("serviceStatus").toString();
                            if (jsonres.get("success").toString().equals("true"))
                            {
                                //会员支付
                                bMemberPay=true;

                                res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());

                                com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
                                for (int pi = 0; pi < cardsList.size(); pi++)
                                {
                                    //多张卡累加积分
                                    getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));

                                    Card card=res.new Card();
                                    card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
                                    card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
                                    card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
                                    card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
                                    card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
                                    card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
                                    card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
                                    card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
                                    card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
                                    card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
                                    card.setGetPoint(getPoint.toPlainString());
                                    card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
                                    card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));

                                    res.getDatas().getVipDatas().getCardsInfo().add(card);

                                    //
                                    res.getDatas().getVipDatas().setAmount(card.getAmount_after());
                                    res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
                                    res.getDatas().getVipDatas().setMemberId(otpReq.getMemberId());
                                    res.getDatas().getVipDatas().setPoints(card.getPoint_after());
                                }
                            }
                            else
                            {
                                errorMessage.append("调用会员积分查询接口MemberPayQuery失败:" +serviceDescription );
                                return false;
                            }
                        }
                        else
                        {
                            errorMessage.append("调用会员积分查询接口MemberPayQuery失败:");
                            return false;
                        }
                    }
                    else
                    {
                        if (jsonres.get("success").toString().equals("true"))
                        {
                            //会员支付
                            bMemberPay=true;
                            if(jsonres.containsKey("datas"))
                            {
                                if(null!=jsonres.getJSONObject("datas"))
                                {
                                    if(jsonres.getJSONObject("datas").containsKey("cards"))
                                    {
                                        res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());
                                        com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
                                        for (int pi = 0; pi < cardsList.size(); pi++)
                                        {
                                            //多张卡累加积分
                                            getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));

                                            Card card=res.new Card();
                                            card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
                                            card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
                                            card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
                                            card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
                                            card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
                                            card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
                                            card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
                                            card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
                                            card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
                                            card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
                                            card.setGetPoint(getPoint.toPlainString());
                                            card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
                                            card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));

                                            res.getDatas().getVipDatas().getCardsInfo().add(card);

                                            //
                                            res.getDatas().getVipDatas().setAmount(card.getAmount_after());
                                            res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
                                            res.getDatas().getVipDatas().setMemberId(otpReq.getMemberId());
                                            res.getDatas().getVipDatas().setPoints(card.getPoint_after());
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            errorMessage.append("调用会员积分接口MemberPay失败:" + serviceDescription);
                            return false;
                        }
                    }
                }
                else
                {
                    errorMessage.append("调用会员积分接口MemberPay失败:");
                    return false;
                }

                if (PosPub.isNumericTypeMinus(pointQty)==false) pointQty="0";
                //累加本次积分
                pointQty=new BigDecimal(pointQty).add(getPoint).toString();
                //
                sale.setPointQty(pointQty);
                sale.setMemberOrderno(memberOrderno);//调用积分memberpay的orderno
            }
            //************************************memberpay结束***************************************


            //写表
            if (otpReq.getPay()!=null)
            {
                for (DCP_OrderToSaleProcess_OpenReq.Payment rpay : otpReq.getPay())
                {
                    SALE.Payment pay=sale.new Payment();

                    if (Check.Null(rpay.getCanInvoice()) ) rpay.setCanInvoice("0");
                    if (Check.Null(rpay.getIsOrderPay()))rpay.setIsOrderPay("N");
                    if (Check.Null(rpay.getIsTurnover()))rpay.setIsTurnover("Y");

                    pay.setIsTurnOver(rpay.getIsTurnover());//是否纳入营业额Y/N
                    pay.setPayShop("");//支付门店编号
                    pay.setPayType(rpay.getPayType());//第三方支付方式
                    pay.setPosPay("0");//POS支付金额
                    pay.setPrePayBillNo("");//订金收款单号
                    pay.setReturnRate("0");//退货吸收比率

                    pay.setFuncNo(rpay.getFuncNo());//功能编码
                    pay.setsDate(sDate);
                    pay.seteId(eId);
                    pay.setShopId(shopId);
                    pay.setStatus("100");
                    pay.setsTime(sTime);
                    pay.setAuthCode(rpay.getAuthCode());
                    pay.setbDate(sale.getbDate());
                    pay.setCardNo(rpay.getCardNo());

                    //卡付款后
                    pay.setCardAmtBefore(rpay.getCardBeforeAmt());
                    pay.setCardRemainAmt(rpay.getCardRemainAmt());
                    pay.setCardSendPay(rpay.getSendPay());

                    //
                    if (res.getDatas().getVipDatas()!=null &&res.getDatas().getVipDatas().getCardsInfo()!=null)
                    {
                        for (Card card : res.getDatas().getVipDatas().getCardsInfo())
                        {
                            if (card.getCardNo().equals(rpay.getCardNo()) || ("3011".equals(rpay.getFuncNo()) && rpay.getCardNo().contains(card.getCardNo())) || "3012".equals(rpay.getFuncNo()) ||"3013".equals(rpay.getFuncNo())||"3014".equals(rpay.getFuncNo()))
                            {
                                //卡付款后
                                pay.setCardAmtBefore(card.getAmount_before());
                                pay.setCardRemainAmt(card.getAmount_after());
                                pay.setCardSendPay(card.getAmount2());
                                pay.setLpcardNo(card.getCardNo());//禄品真实卡号

                                break;
                            }
                        }
                    }

                    pay.setChanged(rpay.getChanged());
                    pay.setCouponQty(rpay.getCouponQty());
                    pay.setCtType(rpay.getCtType());
                    pay.setDescore(rpay.getDescore());
                    pay.setExtra(rpay.getExtra());
                    pay.setIsDeposit(rpay.getIsOrderPay());//是否订金Y/N【CM专用】
                    pay.setIsOrderPay(rpay.getIsOrderPay());//是否订金冲销Y/N
                    pay.setIsVerifycation(rpay.getIsVerification());
                    pay.setItem(rpay.getItem());
                    pay.setPay(rpay.getPay());
                    pay.setPaycode(rpay.getPayCode());
                    pay.setPaycodeErp(rpay.getPayCodeErp());
                    pay.setPayDocType(rpay.getPaydoctype());//支付平台类型：POS-4
                    pay.setPayName(rpay.getPayName());
                    if("N".equals(rpay.getIsOrderPay()) &&
                            (   "#03".equals(rpay.getPayType())//鼎捷卡支付
                                    ||	"#031".equals(rpay.getPayType())//企迈卡支付
                                    || "#04".equals(rpay.getPayType()) //鼎捷券支付
                                    || "#041".equals(rpay.getPayType()) //企迈券支付
                            )
                    )
                    {
                        if(Check.Null(rpay.getPaySerNum()))
                        {
                            pay.setPayserNum(memberOrderno);  //调用memberpay的orderNo
                        }
                    }
                    pay.setRefNo(rpay.getRefNo());
                    pay.setSerialNo(rpay.getSerialNo());
                    pay.setTeriminalNo(rpay.getTeriminalNo());
                    pay.setChargeAmount(rpay.getChargeAmount());
                    pay.setPayChannelCode(rpay.getPayChannelCode());

                    BigDecimal p_pay=new BigDecimal(rpay.getPay());
                    BigDecimal p_changed=new BigDecimal(rpay.getChanged());
                    BigDecimal p_extra=new BigDecimal(rpay.getExtra());

                    //pay-changed-extra累加起来
                    BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra);

                    //pay-changed
                    BigDecimal p_amt=p_pay.subtract(p_changed);

                    String tempCardno=pay.getCardNo();
                    //禄品卡
                    if ("3011".equals(pay.getFuncNo()))
                    {
                        tempCardno=pay.getLpcardNo();
                    }

                    //DCP_ORDER_PAY_DETAIL
                    String[] Columns_DCP_ORDER_PAY_DETAIL = {
                            "EID","BILLNO","ITEM","BILLDATE","BDATE","SOURCEBILLTYPE","SOURCEBILLNO","LOADDOCTYPE",
                            "CHANNELID","PAYCODE","PAYCODEERP","PAYNAME","ORDER_PAYCODE","ISONLINEPAY","PAY",
                            "PAYDISCAMT","PAYAMT1","PAYAMT2","DESCORE","CTTYPE","CARDNO","CARDBEFOREAMT",
                            "CARDREMAINAMT","COUPONQTY","ISVERIFICATION","EXTRA","CHANGED","PAYSERNUM","SERIALNO",
                            "REFNO","TERIMINALNO","CANINVOICE","WRITEOFFAMT","AUTHCODE","LASTMODIOPID",
                            "LASTMODIOPNAME","LASTMODITIME","FUNCNO","PAYDOCTYPE","PAYTYPE","SENDPAY","MERDISCOUNT","MERRECEIVE",
                            "THIRDDISCOUNT","CUSTPAYREAL","COUPONMARKETPRICE","COUPONPRICE","CHARGEAMOUNT",
                            "PAYCHANNELCODE","PARTITION_DATE"
                    };
                    DataValue[] insValue_DCP_ORDER_PAY_DETAIL = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(billno, Types.VARCHAR),
                            new DataValue(pay.getItem(), Types.VARCHAR),
                            new DataValue(sDate, Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.VARCHAR),
                            new DataValue("Order", Types.VARCHAR),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(otpReq.getLoadDocType(), Types.VARCHAR),
                            new DataValue(otpReq.getChannelId(), Types.VARCHAR),
                            new DataValue(pay.getPaycode(), Types.VARCHAR),
                            new DataValue(pay.getPaycodeErp(), Types.VARCHAR),
                            new DataValue(pay.getPayName(), Types.VARCHAR),
                            new DataValue(rpay.getOrder_payCode(), Types.VARCHAR),
                            new DataValue(rpay.getIsOnlinePay(), Types.VARCHAR),
                            new DataValue(pay.getPay(), Types.VARCHAR),
                            new DataValue(rpay.getPayDiscAmt(), Types.VARCHAR),
                            new DataValue(rpay.getPayAmt1(), Types.VARCHAR),
                            new DataValue(rpay.getPayAmt2(), Types.VARCHAR),
                            new DataValue(rpay.getDescore(), Types.VARCHAR),
                            new DataValue(rpay.getCtType(), Types.VARCHAR),
                            new DataValue(tempCardno, Types.VARCHAR),
                            new DataValue(rpay.getCardBeforeAmt(), Types.VARCHAR),
                            new DataValue(rpay.getCardRemainAmt(), Types.VARCHAR),
                            new DataValue(rpay.getCouponQty(), Types.VARCHAR),
                            new DataValue(rpay.getIsVerification(), Types.VARCHAR),
                            new DataValue(rpay.getExtra(), Types.VARCHAR),
                            new DataValue(rpay.getChanged(), Types.VARCHAR),
                            new DataValue(pay.getPayserNum(), Types.VARCHAR),//这个是调用memberpay接口的orderno字段
                            new DataValue(rpay.getSerialNo(), Types.VARCHAR),
                            new DataValue(rpay.getRefNo(), Types.VARCHAR),
                            new DataValue(rpay.getTeriminalNo(), Types.VARCHAR),
                            new DataValue(rpay.getCanInvoice(), Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),//冲销金额0
                            new DataValue(rpay.getAuthCode(), Types.VARCHAR),
                            new DataValue(otpReq.getOpOpNo(), Types.VARCHAR),
                            new DataValue(otpReq.getOpOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue(rpay.getFuncNo(), Types.VARCHAR),
                            new DataValue(rpay.getPaydoctype(), Types.VARCHAR),
                            new DataValue(rpay.getPayType(), Types.VARCHAR),
                            new DataValue(pay.getCardSendPay(), Types.VARCHAR),
                            new DataValue(rpay.getMerDiscount(), Types.VARCHAR),
                            new DataValue(rpay.getMerReceive(), Types.VARCHAR),
                            new DataValue(rpay.getThirdDiscount(), Types.VARCHAR),
                            new DataValue(rpay.getCustPayReal(), Types.VARCHAR),
                            new DataValue(rpay.getCouponMarketPrice(), Types.VARCHAR),
                            new DataValue(rpay.getCouponPrice(), Types.VARCHAR),
                            new DataValue(rpay.getChargeAmount(), Types.DECIMAL),
                            new DataValue(rpay.getPayChannelCode(), Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.NUMERIC)//分区字段
                    };
                    InsBean ib_DCP_ORDER_PAY_DETAIL = new InsBean("DCP_ORDER_PAY_DETAIL", Columns_DCP_ORDER_PAY_DETAIL);//分区字段已处理
                    ib_DCP_ORDER_PAY_DETAIL.addValues(insValue_DCP_ORDER_PAY_DETAIL);
                    data.add(new DataProcessBean(ib_DCP_ORDER_PAY_DETAIL));


                    //只有POS和Android POS
                    if (otpReq.getLoadDocType().equals(orderLoadDocType.POS)|| otpReq.getLoadDocType().equals(orderLoadDocType.POSANDROID))
                    {
                        //交班统计信息表DCP_STATISTIC_INFO
                        String[] Columns_DCP_STATISTIC_INFO = {
                                "EID","SHOPID","MACHINE","OPNO","SQUADNO","ORDERNO","ITEM","PAYCODE",
                                "PAYNAME","AMT","SDATE","STIME","ISORDERPAY","WORKNO","TYPE","BDATE","CARDNO",
                                "CUSTOMERNO","CHANGED","EXTRA","ISTURNOVER","STATUS","APPTYPE","CHANNELID","PAYTYPE",
                                "MERDISCOUNT","THIRDDISCOUNT","DIRECTION","CHARGEAMOUNT","PAYCHANNELCODE","SENDPAY"
                        };
                        DataValue[] insValue_DCP_STATISTIC_INFO = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpShopId())?shopId:otpReq.getOpShopId(), Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpMachineNo())?"999":otpReq.getOpMachineNo(), Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpOpNo())?"admin":otpReq.getOpOpNo(), Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpSquadNo())?"999":otpReq.getOpSquadNo(), Types.VARCHAR),
                                new DataValue(saleno, Types.VARCHAR),
                                new DataValue(pay.getItem(), Types.VARCHAR),
                                new DataValue(pay.getPaycode(), Types.VARCHAR),
                                new DataValue(pay.getPayName(), Types.VARCHAR),
                                new DataValue(p_amt, Types.DECIMAL),
                                new DataValue(sDate, Types.VARCHAR),
                                new DataValue(sTime, Types.VARCHAR),
                                new DataValue("N", Types.VARCHAR),//尾款固定N 原订单的固定Y
                                new DataValue(Check.Null(otpReq.getOpWorkNo())?"999":otpReq.getOpWorkNo(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),//TYPE 注意给值
                                new DataValue(sale.getbDate(), Types.VARCHAR),
                                new DataValue(tempCardno, Types.VARCHAR),
                                new DataValue(otpReq.getCustomer(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue(rpay.getExtra(), Types.VARCHAR),
                                new DataValue("Y", Types.VARCHAR),//ISTURNOVER
                                new DataValue("100", Types.VARCHAR),
                                new DataValue(otpReq.getLoadDocType(), Types.VARCHAR),//
                                new DataValue(otpReq.getChannelId(), Types.VARCHAR),//
                                new DataValue(pay.getPayType(), Types.VARCHAR),//
                                new DataValue(rpay.getMerDiscount(), Types.VARCHAR),
                                new DataValue(rpay.getThirdDiscount(), Types.VARCHAR),
                                new DataValue(1, Types.INTEGER),
                                new DataValue(rpay.getChargeAmount(), Types.DECIMAL),
                                new DataValue(rpay.getPayChannelCode(), Types.VARCHAR),
                                new DataValue(rpay.getSendPay(), Types.DECIMAL),
                        };
                        InsBean ib_DCP_STATISTIC_INFO = new InsBean("DCP_STATISTIC_INFO", Columns_DCP_STATISTIC_INFO);
                        ib_DCP_STATISTIC_INFO.addValues(insValue_DCP_STATISTIC_INFO);
                        data.add(new DataProcessBean(ib_DCP_STATISTIC_INFO));
                    }



                    //更新订单付款明细表DCP_ORDER_PAY_DETAIL(尾款只有1次，全部冲销)
                    UptBean ub_DCP_ORDER_PAY_DETAIL = new UptBean("DCP_ORDER_PAY_DETAIL");
                    //更新值
                    ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("WRITEOFFAMT", new DataValue("PAY", Types.DECIMAL,DataExpression.OtherFieldname));//已提货金额
                    //更新条件
                    ub_DCP_ORDER_PAY_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_ORDER_PAY_DETAIL.addCondition("BILLNO", new DataValue(billno, Types.VARCHAR));
                    data.add(new DataProcessBean(ub_DCP_ORDER_PAY_DETAIL));

                    //DCP_SALE_PAY
                    String[] Columns_DCP_SALE_PAY = {"EID","SHOPID","SALENO","ITEM","PAYDOCTYPE","PAYCODE"
                            ,"PAYCODEERP","PAYNAME","PAY","POS_PAY","CHANGED","EXTRA","RETURNRATE","PAYSERNUM"
                            ,"SERIALNO","REFNO","TERIMINALNO","CTTYPE","CARDNO","CARDAMTBEFORE","REMAINAMT"
                            ,"SENDPAY","ISVERIFICATION","COUPONQTY","DESCORE","ISORDERPAY","PREPAYBILLNO"
                            ,"AUTHCODE","ISTURNOVER","STATUS","BDATE","SDATE","STIME","PAYTYPE","PAYSHOP"
                            ,"ISDEPOSIT","FUNCNO","MERDISCOUNT","MERRECEIVE","THIRDDISCOUNT","CUSTPAYREAL"
                            ,"COUPONMARKETPRICE","COUPONPRICE","MOBILE","CHARGEAMOUNT","PAYCHANNELCODE"
                            ,"PARTITION_DATE","GAINCHANNEL","GAINCHANNELNAME"
                    };
                    DataValue[] insValue_DCP_SALE_PAY = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(saleno, Types.VARCHAR),
                            new DataValue(pay.getItem(), Types.VARCHAR),
                            new DataValue(rpay.getPaydoctype(), Types.VARCHAR),
                            new DataValue(pay.getPaycode(), Types.VARCHAR),
                            new DataValue(pay.getPaycodeErp(), Types.VARCHAR),
                            new DataValue(pay.getPayName(), Types.VARCHAR),
                            new DataValue(pay.getPay(), Types.DECIMAL),
                            new DataValue(pay.getPosPay(), Types.DECIMAL),
                            new DataValue(pay.getChanged(), Types.DECIMAL),
                            new DataValue(pay.getExtra(), Types.DECIMAL),
                            new DataValue(pay.getReturnRate(), Types.DECIMAL),
                            new DataValue(pay.getPayserNum(), Types.VARCHAR),
                            new DataValue(pay.getSerialNo(), Types.VARCHAR),
                            new DataValue(pay.getRefNo(), Types.VARCHAR),
                            new DataValue(pay.getTeriminalNo(), Types.VARCHAR),
                            new DataValue(pay.getCtType(), Types.VARCHAR),
                            new DataValue(tempCardno, Types.VARCHAR),
                            new DataValue(pay.getCardAmtBefore(), Types.DECIMAL),
                            new DataValue(pay.getCardRemainAmt(), Types.DECIMAL),
                            new DataValue(pay.getCardSendPay(), Types.DECIMAL),
                            new DataValue(pay.getIsVerifycation(), Types.VARCHAR),
                            new DataValue(pay.getCouponQty(), Types.DECIMAL),
                            new DataValue(pay.getDescore(), Types.DECIMAL),
                            new DataValue(pay.getIsOrderPay(), Types.VARCHAR),
                            new DataValue(pay.getPrePayBillNo(), Types.VARCHAR),
                            new DataValue(pay.getAuthCode(), Types.VARCHAR),
                            new DataValue(pay.getIsTurnOver(), Types.VARCHAR),
                            new DataValue(pay.getStatus(), Types.VARCHAR),
                            new DataValue(pay.getbDate(), Types.VARCHAR),
                            new DataValue(pay.getsDate(), Types.VARCHAR),
                            new DataValue(pay.getsTime(), Types.VARCHAR),
                            new DataValue(pay.getPayType(), Types.VARCHAR),
                            new DataValue(pay.getPayShop(), Types.VARCHAR),
                            new DataValue(pay.getIsDeposit(), Types.VARCHAR),
                            new DataValue(pay.getFuncNo(), Types.VARCHAR),
                            new DataValue(rpay.getMerDiscount(), Types.VARCHAR),
                            new DataValue(rpay.getMerReceive(), Types.VARCHAR),
                            new DataValue(rpay.getThirdDiscount(), Types.VARCHAR),
                            new DataValue(rpay.getCustPayReal(), Types.VARCHAR),
                            new DataValue(rpay.getCouponMarketPrice(), Types.VARCHAR),
                            new DataValue(rpay.getCouponPrice(), Types.VARCHAR),
                            new DataValue(rpay.getMobile(), Types.VARCHAR),
                            new DataValue(rpay.getChargeAmount(), Types.DECIMAL),
                            new DataValue(rpay.getPayChannelCode(), Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                            new DataValue(rpay.getGainChannel(), Types.VARCHAR),
                            new DataValue(rpay.getGainChannelName(), Types.VARCHAR),
                    };
                    InsBean ib_DCP_SALE_PAY = new InsBean("DCP_SALE_PAY", Columns_DCP_SALE_PAY);//分区字段已处理
                    ib_DCP_SALE_PAY.addValues(insValue_DCP_SALE_PAY);
                    data.add(new DataProcessBean(ib_DCP_SALE_PAY));


                    //发票试算接口调用*****
                    //尾款时传入尾款的支付列表
                    JSONObject req_pay = new JSONObject();
                    req_pay.put("payCode", pay.getPaycode());
                    req_pay.put("payName", pay.getPayName());
                    req_pay.put("payAmt", pay.getPay());
                    req_pay.put("isOrderPay", pay.getIsOrderPay());
                    req_pay.put("canInvoice", rpay.getCanInvoice());
                    req_pay.put("extra", pay.getExtra());
                    req_pay.put("change", pay.getChanged());

                    req_pay.put("payCodeErp", pay.getPaycodeErp());//开发票用
                    req_pay.put("sendPayAmt", pay.getCardSendPay());//开发票用
                    req_pay.put("isTurnover", pay.getIsTurnOver());//开发票用
                    req_pay.put("canOpenInvoice", rpay.getCanInvoice());//开发票用
                    datas_payList.put(req_pay);

                }
                if (otpReq.getPay().size()>0)
                {
                    //DCP_ORDER_PAY
                    String[] Columns_DCP_ORDER_PAY = {
                            "EID","BILLNO","BILLDATE","BDATE","SOURCEBILLTYPE","SOURCEBILLNO","COMPANYID",
                            "LOADDOCTYPE","SHOPID","CHANNELID","MACHINEID","CUSTOMERNO","SQUADNO","WORKNO",
                            "DIRECTION","PAYREALAMT","WRITEOFFAMT","USETYPE","STATUS","MEMO","CREATEOPID",
                            "CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME",
                            "SOURCEHEADBILLNO","PARTITION_DATE","UPDATE_TIME","TRAN_TIME"
                    };
                    DataValue[] insValue_DCP_ORDER_PAY = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(billno, Types.VARCHAR),
                            new DataValue(sDate, Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.VARCHAR),
                            new DataValue("Order", Types.VARCHAR),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(otpReq.getBelfirm(), Types.VARCHAR),
                            new DataValue(otpReq.getLoadDocType(), Types.VARCHAR),
                            new DataValue(otpReq.getOpShopId(), Types.VARCHAR),
                            new DataValue(otpReq.getChannelId(), Types.VARCHAR),//渠道不管了
                            new DataValue(otpReq.getOpMachineNo(), Types.VARCHAR),
                            new DataValue(otpReq.getCustomer(), Types.VARCHAR),
                            new DataValue(otpReq.getOpSquadNo(), Types.VARCHAR),
                            new DataValue(otpReq.getOpWorkNo(), Types.VARCHAR),
                            new DataValue("1", Types.VARCHAR),
                            new DataValue(payTot.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(), Types.DECIMAL),
                            new DataValue("0", Types.VARCHAR),//冲销金额
                            new DataValue("final", Types.VARCHAR),//款项用途：front-预付款 refund-退款 final-尾款
                            new DataValue("100", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),//备注
                            new DataValue(otpReq.getOpOpNo(), Types.VARCHAR),//创建人ID
                            new DataValue(otpReq.getOpOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue(otpReq.getOpOpNo(), Types.VARCHAR),//最后修改人ID
                            new DataValue(otpReq.getOpOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    };
                    InsBean ib_DCP_ORDER_PAY = new InsBean("DCP_ORDER_PAY", Columns_DCP_ORDER_PAY);//分区字段已处理
                    ib_DCP_ORDER_PAY.addValues(insValue_DCP_ORDER_PAY);
                    data.add(new DataProcessBean(ib_DCP_ORDER_PAY));

                    //更新订单付款表DCP_ORDER_PAY(尾款只有1次，全部冲销)
                    UptBean ub_DCP_ORDER_PAY = new UptBean("DCP_ORDER_PAY");
                    //更新值
                    ub_DCP_ORDER_PAY.addUpdateValue("WRITEOFFAMT", new DataValue("PAYREALAMT", Types.DECIMAL,DataExpression.OtherFieldname));//冲销金额
                    ub_DCP_ORDER_PAY.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub_DCP_ORDER_PAY.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                    //更新条件
                    ub_DCP_ORDER_PAY.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_ORDER_PAY.addCondition("BILLNO", new DataValue(billno, Types.VARCHAR));
                    data.add(new DataProcessBean(ub_DCP_ORDER_PAY));

                    //******台湾环境订转销尾款处理发票START**********
                    if(AreaType!=null&&AreaType.equals("TW"))
                    {
                        JSONObject request_InvoiceCal = new JSONObject();
                        JSONArray datas_invoiceList = new JSONArray();

                        request_InvoiceCal.put("saleType", "Sale");//单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值
                        request_InvoiceCal.put("saleNo", saleno);
                        request_InvoiceCal.put("shopId", shopId);
                        request_InvoiceCal.put("oprType", "1");//0-仅进行发票试算 1-保存发票试算数据【订单创建接口使用】，默认0
                        request_InvoiceCal.put("freeCode", sale.getFreeCode());//零税证号
                        request_InvoiceCal.put("passport", sale.getPassport());//护照号
                        request_InvoiceCal.put("invSplitType", "1");//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分，不传时默认1不拆分

                        //发票信息
                        JSONObject req_invoice = new JSONObject();
                        req_invoice.put("item", "1");//发票项次
                        //req_invoice.put("invNo", "");
                        req_invoice.put("invMemo", otpReq.getInvMemo());
                        req_invoice.put("carrierCode", otpReq.getCarrierCode());
                        req_invoice.put("loveCode", otpReq.getLoveCode());
                        req_invoice.put("buyerGuiNo", otpReq.getBuyerGuiNo());
                        datas_invoiceList.put(req_invoice);

                        request_InvoiceCal.put("invoiceList", datas_invoiceList);
                        request_InvoiceCal.put("goodsList", datas_goodsList);
                        request_InvoiceCal.put("payList", datas_payList);

                        String str_InvoiceCal=request_InvoiceCal.toString();
                        String requestId=PosPub.getGUID(false);
                        //
                        Map<String, Object> map = new HashMap<>();
                        map.put("serviceId", "POS_InvoiceCaculate_Open");
                        if (req.getApiUser() == null)
                        {
                            map.put("apiUserCode",Yc_Key);
                            map.put("sign",PosPub.encodeMD5(str_InvoiceCal + Yc_Sign_Key));
                        }
                        else
                        {
                            map.put("apiUserCode",req.getApiUserCode());
                            map.put("sign",PosPub.encodeMD5(str_InvoiceCal + req.getApiUser().getUserKey()));
                        }

                        posUrl = PosPub.getPOS_INNER_URL(eId);

                        map.put("langType",req.getLangType());
                        map.put("requestId",requestId);
                        map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                        map.put("version","3.0");

                        PosPub.WriteETLJOBLog("出貨訂轉銷請求POS_InvoiceCaculate_Open： "+ str_InvoiceCal);

                        String resbody_InvoiceCal=HttpSend.doPost(posUrl,str_InvoiceCal,map,requestId);
                        map.clear();
                        map=null;

                        PosPub.WriteETLJOBLog("出貨訂轉銷返回POS_InvoiceCaculate_Open： "+ resbody_InvoiceCal);

                        if (resbody_InvoiceCal.equals("")==false)
                        {
                            JSONObject jsonres_InvoiceCal = new JSONObject(resbody_InvoiceCal);
                            boolean success = jsonres_InvoiceCal.getBoolean("success");
                            if (success)
                            {
                                bInvoiceCal=true;
                            }
                            else
                            {
                                //发票试算失败后,撤销会员卡支付
                                if (bMemberPay)
                                {
                                    boolean bRetmemberPayRefund=MemberPayReverse(memberOrderno, Yc_Sign_Key, Yc_Key, Yc_Url, errorMessage);
                                    if (bRetmemberPayRefund==false)
                                    {
                                        return false;
                                    }

                                    //这个目的撤销过的,单据保存异常就不调用,否则要调用
                                    bMemberPay=false;
                                }

                                String tempDesc=jsonres_InvoiceCal.get("serviceDescription")==null||jsonres_InvoiceCal.get("serviceDescription").toString().equals("null")?"null":jsonres_InvoiceCal.get("serviceDescription").toString();
                                errorMessage.append("订单号："+orderNo+"发票试算失败返回：" +tempDesc);
                                return false;
                            }
                        }
                        else
                        {
                            //发票试算失败后,撤销会员卡支付
                            if (bMemberPay)
                            {
                                boolean bRetmemberPayRefund=MemberPayReverse(memberOrderno, Yc_Sign_Key, Yc_Key, Yc_Url, errorMessage);
                                if (bRetmemberPayRefund==false)
                                {
                                    return false;
                                }

                                //这个目的撤销过的,单据保存异常就不调用,否则要调用
                                bMemberPay=false;
                            }

                            errorMessage.append("订单号："+orderNo+"发票试算失败返回为空："  );
                            return false;
                        }
                    }
                    //******台湾环境订转销尾款处理发票END**********
                }
            }
            //计算冲销扣减订单付款档(本次提货金额-本次尾款金额)
            BigDecimal bdm_writeAMT=new BigDecimal(otpReq.getTot_Amt()).subtract(payTot);

            String order_pay_billno="";


            if(getData_order_pay!=null && getData_order_pay.isEmpty()==false)
            {
                //double totAmt=0;
                double totPay=0;
                double totExtra=0;
                double totMerReceive=0;
                double totCustPayReal=0;
                double payAmt=0;
                //totAmt=Double.valueOf(sale.getTotAmt());
                payAmt=Double.valueOf(sale.getPayAmt());
                for (Map<String, Object> map : getData_order_pay)
                {
                    order_pay_billno=map.get("BILLNO").toString();

                    String funcno=map.get("FUNCNO").toString();
                    String cardno=map.get("CARDNO").toString();

                    //PAY-CHANGED-EXTRA-WRITEOFFAMT
                    BigDecimal bdm_pay=new BigDecimal(map.get("PAY").toString()).subtract(new BigDecimal(map.get("CHANGED").toString())).subtract(new BigDecimal(map.get("EXTRA").toString())).subtract(new BigDecimal(map.get("WRITEOFFAMT").toString()));

                    //PAY-CHANGED
                    BigDecimal p_amt=new BigDecimal(map.get("PAY").toString()).subtract(new BigDecimal(map.get("CHANGED").toString()));

                    //冲销完就跳出吧
                    if (bdm_pay.compareTo(BigDecimal.ZERO)<=0)
                    {
                        continue;
                    }

                    String tempPay="0";
                    if (bdm_pay.compareTo(bdm_writeAMT)<=0)
                    {
                        tempPay=bdm_pay.toPlainString();
                        bdm_writeAMT=bdm_writeAMT.subtract(bdm_pay);
                    }
                    else
                    {
                        tempPay=bdm_writeAMT.toPlainString();
                        bdm_writeAMT=new BigDecimal("0");
                    }
                    //分批订转销
                    double pay=0;
                    double orderPay=0;
                    double orderWriteOffAmt=0;
                    double changed=0;
                    double orderChanged=0;
                    double extra=0;
                    double orderExtra=0;
                    double orderExtraSale=0;
                    double merReceive=0;
                    double orderMerReceive=0;
                    double orderMerReceiveSale=0;
                    double custPayReal=0;
                    double orderCustPayReal=0;
                    double orderCustPayRealSale=0;
                    double sendPay=0;
                    double orderSendPay=0;
                    double orderSendPaySale=0;
                    double descore=0;
                    double orderDescore=0;
                    double orderDescoreSale=0;
                    orderPay=Double.valueOf(Check.Null(map.get("PAY").toString()) ? "0" : map.get("PAY").toString());
                    orderChanged=Double.valueOf(Check.Null(map.get("CHANGED").toString()) ? "0" : map.get("CHANGED").toString());
                    orderWriteOffAmt=Double.valueOf(Check.Null(map.get("WRITEOFFAMT").toString()) ? "0" : map.get("WRITEOFFAMT").toString());
                    orderMerReceive=Double.valueOf(Check.Null(map.get("MERRECEIVE").toString()) ? "0" : map.get("MERRECEIVE").toString());
                    orderMerReceiveSale=Double.valueOf(Check.Null(map.get("MERRECEIVE_SALE").toString()) ? "0" : map.get("MERRECEIVE_SALE").toString());
                    orderCustPayReal=Double.valueOf(Check.Null(map.get("CUSTPAYREAL").toString()) ? "0" : map.get("CUSTPAYREAL").toString());
                    orderCustPayRealSale=Double.valueOf(Check.Null(map.get("CUSTPAYREAL_SALE").toString()) ? "0" : map.get("CUSTPAYREAL_SALE").toString());
                    orderExtra=Double.valueOf(Check.Null(map.get("EXTRA").toString()) ? "0" : map.get("EXTRA").toString());
                    orderExtraSale=Double.valueOf(Check.Null(map.get("EXTRA_SALE").toString()) ? "0" : map.get("EXTRA_SALE").toString());
                    orderSendPay=Double.valueOf(Check.Null(map.get("SENDPAY").toString()) ? "0" : map.get("SENDPAY").toString());
                    orderSendPaySale=Double.valueOf(Check.Null(map.get("SENDPAY_SALE").toString()) ? "0" : map.get("SENDPAY_SALE").toString());
                    orderDescore=Double.valueOf(Check.Null(map.get("DESCORE").toString()) ? "0" : map.get("DESCORE").toString());
                    orderDescoreSale=Double.valueOf(Check.Null(map.get("DESCORE_SALE").toString()) ? "0" : map.get("DESCORE_SALE").toString());
                    if(isOrderToSaleAll.equals("1"))
                    {
                        if(BigDecimalUtils.equal(payAmt, totPay-totExtra))
                        {
                            continue;
                        }
                        if(BigDecimalUtils.bigger(orderPay-orderChanged-orderWriteOffAmt,payAmt-totPay+totExtra)) //可转销售付款金额>=本单尚需支付金额
                        {
                            pay=payAmt-totPay+orderExtra-orderExtraSale;
                            extra=orderExtra-orderExtraSale;
                            merReceive=BigDecimalUtils.round(pay/(orderPay-orderChanged)*orderMerReceive,2);
                            custPayReal=BigDecimalUtils.round(pay/(orderPay-orderChanged)*orderCustPayReal,2);
                            sendPay=BigDecimalUtils.round(pay/(orderPay-orderChanged)*orderSendPay,2);
                            descore=BigDecimalUtils.round(pay/(orderPay-orderChanged)*orderDescore,2);
                        }else
                        {
                            pay=orderPay-orderChanged-orderWriteOffAmt+orderExtra-orderExtraSale;
                            extra=orderExtra-orderExtraSale;
                            merReceive=orderMerReceive-orderMerReceiveSale;
                            custPayReal=orderCustPayReal-orderCustPayRealSale;
                            sendPay=orderSendPay-orderSendPaySale;
                            descore=orderDescore-orderDescoreSale;
                        }
                        p_amt= BigDecimal.valueOf(pay-extra);//交班使用
                        totExtra=totExtra+extra;
                        totPay=totPay+pay;
                        totMerReceive=totMerReceive+merReceive;
                        totCustPayReal=totCustPayReal+custPayReal;
                    }else
                    {
                        pay=orderPay;
                        changed=orderChanged;
                        extra  =orderExtra;
                        merReceive=orderMerReceive;
                        custPayReal=orderCustPayReal;
                        sendPay=orderSendPay;
                        descore=orderDescore;
                    }
                    //DCP_SALE_PAY
                    String[] Columns_DCP_SALE_PAY = {"EID","SHOPID","SALENO","ITEM","PAYDOCTYPE","PAYCODE"
                            ,"PAYCODEERP","PAYNAME","PAY","POS_PAY","CHANGED","EXTRA","RETURNRATE","PAYSERNUM"
                            ,"SERIALNO","REFNO","TERIMINALNO","CTTYPE","CARDNO","CARDAMTBEFORE","REMAINAMT"
                            ,"SENDPAY","ISVERIFICATION","COUPONQTY","DESCORE","ISORDERPAY","PREPAYBILLNO"
                            ,"AUTHCODE","ISTURNOVER","STATUS","BDATE","SDATE","STIME","PAYTYPE","PAYSHOP"
                            ,"ISDEPOSIT","FUNCNO","MERDISCOUNT","MERRECEIVE","THIRDDISCOUNT","CUSTPAYREAL"
                            ,"COUPONMARKETPRICE","COUPONPRICE","MOBILE","CHARGEAMOUNT","PAYCHANNELCODE"
                            ,"PARTITION_DATE","GAINCHANNEL","GAINCHANNELNAME"
                    };
                    DataValue[] insValue_DCP_SALE_PAY = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(saleno, Types.VARCHAR),
                            new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                            new DataValue(map.get("PAYDOCTYPE").toString(), Types.VARCHAR),
                            new DataValue(map.get("PAYCODE").toString(), Types.VARCHAR),
                            new DataValue(map.get("PAYCODEERP").toString(), Types.VARCHAR),
                            new DataValue(map.get("PAYNAME").toString(), Types.VARCHAR),
                            //new DataValue(map.get("PAY").toString(), Types.DECIMAL),//tempPay
                            new DataValue(pay, Types.DECIMAL),//tempPay
                            new DataValue("0", Types.DECIMAL),
                            //new DataValue(map.get("CHANGED").toString(), Types.DECIMAL),
                            new DataValue(changed, Types.DECIMAL),
                            //new DataValue(map.get("EXTRA").toString(), Types.DECIMAL),
                            new DataValue(extra, Types.DECIMAL),
                            new DataValue("0", Types.DECIMAL),
                            new DataValue(map.get("PAYSERNUM").toString(), Types.VARCHAR),
                            new DataValue(map.get("SERIALNO").toString(), Types.VARCHAR),
                            new DataValue(map.get("REFNO").toString(), Types.VARCHAR),
                            new DataValue(map.get("TERIMINALNO").toString(), Types.VARCHAR),
                            new DataValue(map.get("CTTYPE").toString(), Types.VARCHAR),
                            new DataValue(cardno, Types.VARCHAR),
                            new DataValue(map.get("CARDBEFOREAMT").toString(), Types.DECIMAL),
                            new DataValue(map.get("CARDREMAINAMT").toString(), Types.DECIMAL),
//							new DataValue(map.get("SENDPAY").toString(), Types.DECIMAL),
                            new DataValue(sendPay, Types.DECIMAL),
                            new DataValue(map.get("ISVERIFICATION").toString(), Types.VARCHAR),
                            new DataValue(map.get("COUPONQTY").toString(), Types.DECIMAL),
                            //new DataValue(map.get("DESCORE").toString(), Types.DECIMAL),
                            new DataValue(descore, Types.DECIMAL),
                            new DataValue("Y", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(map.get("AUTHCODE").toString(), Types.VARCHAR),
                            new DataValue("Y", Types.VARCHAR),
                            new DataValue("100", Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.VARCHAR),
                            new DataValue(sDate, Types.VARCHAR),
                            new DataValue(sTime, Types.VARCHAR),
                            new DataValue(map.get("PAYTYPE").toString(), Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("Y", Types.VARCHAR),
                            new DataValue(funcno, Types.VARCHAR),
//							new DataValue(map.get("MERDISCOUNT").toString(), Types.VARCHAR),
//							new DataValue(map.get("MERRECEIVE").toString(), Types.VARCHAR),
                            new DataValue(pay-merReceive-extra, Types.VARCHAR),
                            new DataValue(merReceive, Types.VARCHAR),
//							new DataValue(map.get("THIRDDISCOUNT").toString(), Types.VARCHAR),
//							new DataValue(map.get("CUSTPAYREAL").toString(), Types.VARCHAR),
                            new DataValue(merReceive-custPayReal, Types.VARCHAR),
                            new DataValue(custPayReal, Types.VARCHAR),
                            new DataValue(map.get("COUPONMARKETPRICE").toString(), Types.VARCHAR),
                            new DataValue(map.get("COUPONPRICE").toString(), Types.VARCHAR),
                            new DataValue(map.get("MOBILE").toString(), Types.VARCHAR),
                            new DataValue(map.get("CHARGEAMOUNT").toString(), Types.DECIMAL),
                            new DataValue(map.get("PAYCHANNELCODE").toString(), Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                            new DataValue(map.get("GAINCHANNEL").toString(), Types.VARCHAR),
                            new DataValue(map.get("GAINCHANNELNAME").toString(), Types.VARCHAR),
                    };
                    InsBean ib_DCP_SALE_PAY = new InsBean("DCP_SALE_PAY", Columns_DCP_SALE_PAY);//分区字段已处理
                    ib_DCP_SALE_PAY.addValues(insValue_DCP_SALE_PAY);
                    data.add(new DataProcessBean(ib_DCP_SALE_PAY));


                    //更新值
                    if(isOrderToSaleAll.equals("1"))
                    {
                        //更新(主)订单明细表DCP_ORDER_DETAIL
                        UptBean ub_DCP_ORDER_PAY_DETAIL = new UptBean("DCP_ORDER_PAY_DETAIL");
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("WRITEOFFAMT", new DataValue(pay, Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("EXTRA_SALE", new DataValue(extra, Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("MERRECEIVE_SALE", new DataValue(merReceive, Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("MERDISCOUNT_SALE", new DataValue(BigDecimalUtils.round(pay-merReceive-extra,2), Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("CUSTPAYREAL_SALE", new DataValue(custPayReal, Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("THIRDDISCOUNT_SALE", new DataValue(BigDecimalUtils.round(merReceive-custPayReal,2), Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("SENDPAY_SALE", new DataValue(sendPay, Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("DESCORE_SALE", new DataValue(descore, Types.DECIMAL,DataExpression.UpdateSelf));
                        ub_DCP_ORDER_PAY_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub_DCP_ORDER_PAY_DETAIL.addCondition("SOURCEBILLTYPE", new DataValue("Order", Types.VARCHAR));
                        ub_DCP_ORDER_PAY_DETAIL.addCondition("SOURCEBILLNO", new DataValue(orderNo, Types.VARCHAR));
                        ub_DCP_ORDER_PAY_DETAIL.addCondition("ITEM", new DataValue(map.get("ITEM"), Types.VARCHAR));
                        data.add(new DataProcessBean(ub_DCP_ORDER_PAY_DETAIL));
                    }
                    //只有POS和Android POS
                    if (otpReq.getLoadDocType().equals(orderLoadDocType.POS)|| otpReq.getLoadDocType().equals(orderLoadDocType.POSANDROID))
                    {
                        //交班统计信息表DCP_STATISTIC_INFO
                        String[] Columns_DCP_STATISTIC_INFO = {
                                "EID","SHOPID","MACHINE","OPNO","SQUADNO","ORDERNO","ITEM","PAYCODE",
                                "PAYNAME","AMT","SDATE","STIME","ISORDERPAY","WORKNO","TYPE","BDATE","CARDNO",
                                "CUSTOMERNO","CHANGED","EXTRA","ISTURNOVER","STATUS","APPTYPE","CHANNELID","PAYTYPE",
                                "MERDISCOUNT","THIRDDISCOUNT","DIRECTION","CHARGEAMOUNT","PAYCHANNELCODE","SENDPAY"
                        };
                        DataValue[] insValue_DCP_STATISTIC_INFO = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpShopId())?shopId:otpReq.getOpShopId(), Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpMachineNo())?"999":otpReq.getOpMachineNo(), Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpOpNo())?"admin":otpReq.getOpOpNo(), Types.VARCHAR),
                                new DataValue(Check.Null(otpReq.getOpSquadNo())?"999":otpReq.getOpSquadNo(), Types.VARCHAR),
                                new DataValue(saleno, Types.VARCHAR),
                                new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                new DataValue(map.get("PAYCODE").toString(), Types.VARCHAR),
                                new DataValue(map.get("PAYNAME").toString(), Types.VARCHAR),
                                new DataValue(p_amt, Types.DECIMAL),
                                new DataValue(sDate, Types.VARCHAR),
                                new DataValue(sTime, Types.VARCHAR),
                                new DataValue("Y", Types.VARCHAR),//尾款固定N 原订单的固定Y
                                new DataValue(Check.Null(otpReq.getOpWorkNo())?"999":otpReq.getOpWorkNo(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),//TYPE 注意给值
                                new DataValue(sale.getbDate(), Types.VARCHAR),
                                new DataValue(map.get("CARDNO").toString(), Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue(map.get("EXTRA").toString(), Types.VARCHAR),
                                new DataValue("Y", Types.VARCHAR),//ISTURNOVER
                                new DataValue("100", Types.VARCHAR),
                                new DataValue(map.get("LOADDOCTYPE").toString(), Types.VARCHAR),//
                                new DataValue(map.get("CHANNELID").toString(), Types.VARCHAR),//
                                new DataValue(map.get("PAYTYPE").toString(), Types.VARCHAR),//
//								new DataValue(map.get("MERDISCOUNT").toString(), Types.VARCHAR),
//								new DataValue(map.get("THIRDDISCOUNT").toString(), Types.VARCHAR),
                                new DataValue(BigDecimalUtils.round(pay-merReceive,2), Types.VARCHAR),
                                new DataValue(BigDecimalUtils.round(pay-custPayReal,2), Types.VARCHAR),
                                new DataValue(1, Types.INTEGER),
                                new DataValue(map.get("CHARGEAMOUNT").toString(), Types.DECIMAL),
                                new DataValue(map.get("PAYCHANNELCODE").toString(), Types.VARCHAR),
                                new DataValue(sendPay, Types.DECIMAL),
                        };
                        InsBean ib_DCP_STATISTIC_INFO = new InsBean("DCP_STATISTIC_INFO", Columns_DCP_STATISTIC_INFO);
                        ib_DCP_STATISTIC_INFO.addValues(insValue_DCP_STATISTIC_INFO);
                        data.add(new DataProcessBean(ib_DCP_STATISTIC_INFO));

                    }

                    if(isOrderToSaleAll.equals("1"))
                    {

                    }else
                    {
                        //更新订单付款明细表DCP_ORDER_PAY_DETAIL(尾款只有1次，全部冲销)
                        UptBean ub_DCP_ORDER_PAY_DETAIL = new UptBean("DCP_ORDER_PAY_DETAIL");
                        //更新值
                        ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("WRITEOFFAMT", new DataValue(tempPay, Types.DECIMAL,DataExpression.UpdateSelf));//冲销金额
                        //更新条件
                        ub_DCP_ORDER_PAY_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub_DCP_ORDER_PAY_DETAIL.addCondition("BILLNO", new DataValue(order_pay_billno, Types.VARCHAR));
                        data.add(new DataProcessBean(ub_DCP_ORDER_PAY_DETAIL));

                    }
                }
            }

            if(!isOrderToSaleAll.equals("1"))
            {
                //订单付款单头冲销金额
                if (order_pay_billno.equals("")==false)
                {
                    //更新订单付款明细表DCP_ORDER_PAY
                    UptBean ub_DCP_DCP_ORDER_PAY = new UptBean("DCP_ORDER_PAY");
                    //更新值
                    ub_DCP_DCP_ORDER_PAY.addUpdateValue("WRITEOFFAMT", new DataValue(bdm_writeAMT.toPlainString(), Types.DECIMAL,DataExpression.UpdateSelf));//冲销金额
                    ub_DCP_DCP_ORDER_PAY.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub_DCP_DCP_ORDER_PAY.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));


                    //更新条件
                    ub_DCP_DCP_ORDER_PAY.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_DCP_ORDER_PAY.addCondition("BILLNO", new DataValue(order_pay_billno, Types.VARCHAR));
                    data.add(new DataProcessBean(ub_DCP_DCP_ORDER_PAY));

                }
            }
            //DCP_SALE
            String[] Columns_DCP_SALE = {
                    "EID","SHOPID","SALENO","TRNO","FNO","VER_NUM","LEGALPER","MACHINE","TYPE","TYPENAME","BDATE"
                    ,"SQUADNO","WORKNO","OPNO","AUTHORIZEROPNO","TERIMINAL_ID","OSHOP","OMACHINE","OTYPE","OFNO",
                    "SOURCESUBORDERNO","OTRNO","OBDATE","APPROVAL","CARDNO","MEMBERID","MEMBERNAME","CARDTYPEID",
                    "CARDAMOUNT","POINT_QTY","REMAINPOINT","MEMBERORDERNO","ORDERSHOP","CONTMAN","CONTTEL","GETMODE",
                    "GETSHOP","GETMAN","GETMANTEL","SHIPADD","GDATE","GTIME","MANUALNO","MEALNUMBER","CHILDNUMBER",
                    "MEMO","ECSFLG","ECSDATE","DISTRIBUTION","SENDMEMO","TABLENO","OPENID","TABLEKIND","GUESTNUM",
                    "REPAST_TYPE","DINNERDATE","DINNERTIME","DINNERSIGN","DINNERTYPE","TOUR_COUNTRYCODE","TOUR_TRAVELNO",
                    "TOUR_GROUPNO","TOUR_GUIDENO","TOUR_PEOPLENUM","TOT_QTY","TOT_OLDAMT","TOT_DISC","SALEAMT","SALEDISC","PAYDISC",
                    "ERASE_AMT","TOT_AMT","SERVCHARGE","ORDERAMOUNT","FREECODE","PASSPORT","ISINVOICE","INVOICETITLE",
                    "INVOICEBANK","INVOICEACCOUNT","INVOICETEL","INVOICEADDR","TAXREGNUMBER","SELLCREDIT","CUSTOMERNO",
                    "CUSTOMERNAME","PAY_AMT","TOT_CHANGED","OINVSTARTNO","ISINVOICEMAKEOUT","INVSPLITTYPE","INVCOUNT",
                    "ISTAKEOUT","TAKEAWAY","ORDER_ID","ORDER_SN","PLATFORM_DISC","SELLER_DISC","PACKAGEFEE","SHIPPINGFEE",
                    "DELIVERY_FEE_SHOP","DELIVERY_FEE_USER","WM_USER_PAID","PLATFORM_FEE","WM_EXTRA_FEE","SHOPINCOME",
                    "PRODUCTIONMODE","PRODUCTIONSHOP","ISBUFFER","BUFFER_TIMEOUT","ECCUSTOMERNO","STATUS","ISRETURN",
                    "RETURNUSERID","BSNO","SDATE","STIME","EVALUATE","ISUPLOADED","RSV_ID","ORDERRETURN","COMPANYID",
                    "CHANNELID","APPTYPE","OCOMPANYID","OCHANNELID","OAPPTYPE","WXOPENID","TRAN_TIME","TOT_AMT_MERRECEIVE",
                    "TOT_AMT_CUSTPAYREAL","TOT_DISC_MERRECEIVE","TOT_DISC_CUSTPAYREAL","ISMERPAY","DEPARTNO","WAIMAIMERRECEIVEMODE",
                    "ISORDERTOSALEALL","PARTITION_DATE","GROUPBUYING","PARTNERMEMBER"
            };
            DataValue[] insValue_DCP_SALE = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(saleno, Types.VARCHAR),
                    new DataValue(trno, Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue(sale.getVerNum(), Types.VARCHAR),
                    new DataValue(sale.getLegalper(), Types.VARCHAR),
                    new DataValue(otpReq.getOpMachineNo(), Types.VARCHAR),
                    new DataValue(sale.getType(), Types.VARCHAR),
                    new DataValue(sale.getTypeName(), Types.VARCHAR),
                    new DataValue(sale.getbDate(), Types.VARCHAR),
                    new DataValue(sale.getSquadNO(), Types.VARCHAR),
                    new DataValue(sale.getWorkNO(), Types.VARCHAR),
                    new DataValue(sale.getOpNO(), Types.VARCHAR),
                    new DataValue(sale.getAuthorizerOpno(), Types.VARCHAR),
                    new DataValue(sale.getTeriminalID(), Types.VARCHAR),
                    new DataValue(sale.getoShop(), Types.VARCHAR),
                    new DataValue(sale.getoMachine(), Types.VARCHAR),
                    new DataValue(sale.getoType(), Types.VARCHAR),
                    new DataValue(sale.getOfNO(), Types.VARCHAR),
                    new DataValue(sale.getSourceSubOrderno(), Types.VARCHAR),
                    new DataValue(sale.getOtrno(), Types.VARCHAR),
                    new DataValue(sale.getoBdate(), Types.VARCHAR),
                    new DataValue(sale.getApproval(), Types.VARCHAR),
                    new DataValue(sale.getCardNO(), Types.VARCHAR),
                    new DataValue(sale.getMemberId(), Types.VARCHAR),
                    new DataValue(sale.getMemberName(), Types.VARCHAR),
                    new DataValue(sale.getCardTypeId(), Types.VARCHAR),
                    new DataValue(sale.getCardAmount(), Types.DECIMAL),
                    new DataValue(pointQty, Types.DECIMAL),
                    new DataValue(sale.getRemainPoint(), Types.DECIMAL),
                    new DataValue(sale.getMemberOrderno(), Types.VARCHAR),
                    new DataValue(sale.getOrderShop(), Types.VARCHAR),
                    new DataValue(sale.getContMan(), Types.VARCHAR),
                    new DataValue(sale.getContTel(), Types.VARCHAR),
                    new DataValue(sale.getGetMode(), Types.VARCHAR),
                    new DataValue(sale.getGetShop(), Types.VARCHAR),
                    new DataValue(sale.getGetMan(), Types.VARCHAR),
                    new DataValue(sale.getGetManTel(), Types.VARCHAR),
                    new DataValue(sale.getShipAdd(), Types.VARCHAR),
                    new DataValue(sale.getgDate(), Types.VARCHAR),
                    new DataValue(sale.getgTime(), Types.VARCHAR),
                    new DataValue(sale.getManualNO(), Types.VARCHAR),
                    new DataValue(sale.getMealNumber(), Types.VARCHAR),
                    new DataValue(sale.getChildNumber(), Types.VARCHAR),
                    new DataValue(sale.getMemo(), Types.VARCHAR),
                    new DataValue(sale.getEcsFlg(), Types.VARCHAR),
                    new DataValue(sale.getEcsDate(), Types.VARCHAR),
                    new DataValue(sale.getDistribution(), Types.VARCHAR),
                    new DataValue(sale.getSendMemo(), Types.VARCHAR),
                    new DataValue(sale.getTableNO(), Types.VARCHAR),
                    new DataValue(sale.getOpenerId(), Types.VARCHAR),
                    new DataValue(sale.getTableKind(), Types.VARCHAR),
                    new DataValue(sale.getGuestNum(), Types.VARCHAR),
                    new DataValue(sale.getRepastType(), Types.VARCHAR),
                    new DataValue(sale.getDinnerDate(), Types.VARCHAR),
                    new DataValue(sale.getDinnerTime(), Types.VARCHAR),
                    new DataValue(sale.getDinnerSign(), Types.VARCHAR),
                    new DataValue(sale.getDinnerType(), Types.VARCHAR),
                    new DataValue(sale.getTourCountryCode(), Types.VARCHAR),
                    new DataValue(sale.getTourTravelNO(), Types.VARCHAR),
                    new DataValue(sale.getTourGroupNO(), Types.VARCHAR),
                    new DataValue(sale.getTourGuideNO(), Types.VARCHAR),
                    new DataValue(sale.getTourPeopleNum(), Types.VARCHAR),
                    new DataValue(sale.getTotQty(), Types.DECIMAL),
                    new DataValue(sale.getTotOldAmt(), Types.DECIMAL),
                    new DataValue(sale.getTotDisc(), Types.DECIMAL),
                    new DataValue(saleamt, Types.DECIMAL),
                    new DataValue(saledisc, Types.DECIMAL),
                    new DataValue(sale.getPayDisc(), Types.DECIMAL),
                    new DataValue(sale.getEraseAmt(), Types.DECIMAL),
                    new DataValue(sale.getTotAmt(), Types.DECIMAL),
                    new DataValue(sale.getServCharge(), Types.DECIMAL),
                    new DataValue(sale.getOrderAmount(), Types.DECIMAL),
                    new DataValue(sale.getFreeCode(), Types.VARCHAR),
                    new DataValue(sale.getPassport(), Types.VARCHAR),
                    new DataValue(sale.getIsInvoice(), Types.VARCHAR),
                    new DataValue(sale.getInvoiceTitle(), Types.VARCHAR),
                    new DataValue(sale.getInvoiceBank(), Types.VARCHAR),
                    new DataValue(sale.getInvoiceAccount(), Types.VARCHAR),
                    new DataValue(sale.getInvoiceTel(), Types.VARCHAR),
                    new DataValue(sale.getInvoiceAddr(), Types.VARCHAR),
                    new DataValue(sale.getTaxRegNumber(), Types.VARCHAR),
                    new DataValue(sale.getSellCredit(), Types.VARCHAR),
                    new DataValue(sale.getCustomerNO(), Types.VARCHAR),
                    new DataValue(sale.getCustomerName(), Types.VARCHAR),
                    new DataValue(sale.getPayAmt(), Types.DECIMAL),
                    new DataValue(sale.getTotChanged(), Types.DECIMAL),
                    new DataValue(sale.getoInvstartno(), Types.VARCHAR),
                    new DataValue(sale.getIsInvoiceMakeOut(), Types.VARCHAR),
                    new DataValue(sale.getInvSplitType(), Types.VARCHAR),
                    new DataValue(sale.getInvCount(), Types.VARCHAR),
                    new DataValue(sale.getIsTakeout(), Types.VARCHAR),
                    new DataValue(sale.getTakeAway(), Types.VARCHAR),
                    new DataValue(sale.getOrderID(), Types.VARCHAR),
                    new DataValue(sale.getOrderSn(), Types.VARCHAR),
                    new DataValue(sale.getPlatformDisc(), Types.DECIMAL),
                    new DataValue(sale.getSellerDisc(), Types.DECIMAL),
                    new DataValue(sale.getPackageFee(), Types.DECIMAL),
                    new DataValue(sale.getShippingFee(), Types.DECIMAL),
                    new DataValue(sale.getDeliveryFeeShop(), Types.DECIMAL),
                    new DataValue(sale.getDeliveryFeeUser(), Types.DECIMAL),
                    new DataValue(sale.getWmUserPaid(), Types.DECIMAL),
                    new DataValue(sale.getPlatformFee(), Types.DECIMAL),
                    new DataValue(sale.getWmExtraFee(), Types.DECIMAL),
                    new DataValue(sale.getShopIncome(), Types.DECIMAL),
                    new DataValue(sale.getProductionMode(), Types.VARCHAR),
                    new DataValue(sale.getProductionShop(), Types.VARCHAR),
                    new DataValue(sale.getIsBuff(), Types.VARCHAR),
                    new DataValue(sale.getIsBuff_timeout(), Types.VARCHAR),
                    new DataValue(sale.getEcCustomerNO(), Types.VARCHAR),
                    new DataValue(sale.getStatus(), Types.VARCHAR),
                    new DataValue(sale.getIsReturn(), Types.VARCHAR),
                    new DataValue(sale.getReturnUserId(), Types.VARCHAR),
                    new DataValue(sale.getBsno(), Types.VARCHAR),
                    new DataValue(sale.getsDate(), Types.VARCHAR),
                    new DataValue(sale.getsTime(), Types.VARCHAR),
                    new DataValue(sale.getEvaluate(), Types.VARCHAR),
                    new DataValue(sale.getIsUploaded(), Types.VARCHAR),
                    new DataValue(sale.getRsvId(), Types.VARCHAR),
                    new DataValue(sale.getOrderReturn(), Types.VARCHAR),
                    new DataValue(sale.getOrder_companyId(), Types.VARCHAR),
                    new DataValue(sale.getOrder_channelId(), Types.VARCHAR),
                    new DataValue(sale.getOrder_appType(), Types.VARCHAR),
                    new DataValue(sale.getoOrder_companyId(), Types.VARCHAR),
                    new DataValue(sale.getoOrder_channelId(), Types.VARCHAR),
                    new DataValue(sale.getoOrder_appType(), Types.VARCHAR),
                    new DataValue(sale.getWxOpenId(), Types.VARCHAR),
                    new DataValue(sale.getTran_time(), Types.VARCHAR),
                    new DataValue(sale.getTot_Amt_merReceive(), Types.VARCHAR),
                    new DataValue(sale.getTot_Amt_custPayReal(), Types.VARCHAR),
                    new DataValue(sale.getTotDisc_merReceive(), Types.VARCHAR),
                    new DataValue(sale.getTotDisc_custPayReal(), Types.VARCHAR),
                    new DataValue(sale.getIsMerPay(), Types.VARCHAR),
                    new DataValue(sale.getDepartNo(), Types.VARCHAR),
                    new DataValue(sale.getWaimaiMerreceiveMode(), Types.VARCHAR),
                    new DataValue(isOrderToSaleAll,Types.VARCHAR),
                    new DataValue(sale.getbDate(),Types.NUMERIC),//分区字段
                    new DataValue(sale.getGroupBuying(),Types.VARCHAR),
                    new DataValue(sale.getPartnerMember(),Types.VARCHAR),
            };
            InsBean ib_DCP_SALE = new InsBean("DCP_SALE", Columns_DCP_SALE);//分区字段已处理
            ib_DCP_SALE.addValues(insValue_DCP_SALE);
            data.add(new DataProcessBean(ib_DCP_SALE));


            //更新订单主表DCP_ORDER
            UptBean ub_DCP_ORDER = new UptBean("DCP_ORDER");
            //更新值
            ub_DCP_ORDER.addUpdateValue("WRITEOFFAMT", new DataValue(otpReq.getTot_Amt(), Types.DECIMAL,DataExpression.UpdateSelf));//已提货金额
            ub_DCP_ORDER.addUpdateValue("POINTQTY", new DataValue(pointQty, Types.DECIMAL,DataExpression.UpdateSelf));//会员积分
            ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue(payTot.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(), Types.DECIMAL,DataExpression.UpdateSelf));//已付金额
            ub_DCP_ORDER.addUpdateValue("ORDERTOSALE_DATETIME", new DataValue(sDatetimeMs, Types.VARCHAR));
            if(isOrderToSaleAll.equals("1"))
            {
                ub_DCP_ORDER.addUpdateValue("TOT_OLDAMT_SALE", new DataValue(otpReq.getTot_oldAmt(), Types.DECIMAL,DataExpression.UpdateSelf));
                ub_DCP_ORDER.addUpdateValue("TOT_DISC_SALE", new DataValue(otpReq.getTotDisc(), Types.DECIMAL,DataExpression.UpdateSelf));//已冲销折扣
                ub_DCP_ORDER.addUpdateValue("SHIPFEE_SALE", new DataValue(otpReq.getShipFee(), Types.DECIMAL,DataExpression.UpdateSelf));//已冲销
                ub_DCP_ORDER.addUpdateValue("PACKAGEFEE_SALE", new DataValue(otpReq.getPackageFee(), Types.DECIMAL,DataExpression.UpdateSelf));//已冲销
                ub_DCP_ORDER.addUpdateValue("TOT_DISC_MERRECEIVE_SALE", new DataValue(otpReq.getTotDisc_merReceive(), Types.DECIMAL,DataExpression.UpdateSelf));
                ub_DCP_ORDER.addUpdateValue("TOT_DISC_CUSTPAYREAL_SALE", new DataValue(BigDecimalUtils.round(Double.valueOf(otpReq.getTotThirdDiscount().toString())+Double.valueOf(otpReq.getTotDisc_merReceive().toString()),2), Types.DECIMAL,DataExpression.UpdateSelf));
                ub_DCP_ORDER.addUpdateValue("TOT_AMT_MERRECEIVE_SALE", new DataValue(otpReq.getTot_Amt_merReceive(), Types.DECIMAL,DataExpression.UpdateSelf));
                ub_DCP_ORDER.addUpdateValue("TOT_AMT_CUSTPAYREAL_SALE", new DataValue(otpReq.getTot_Amt_custPayReal(), Types.DECIMAL,DataExpression.UpdateSelf));
                ub_DCP_ORDER.addUpdateValue("ERASE_AMT_SALE", new DataValue(otpReq.getEraseAmt(), Types.DECIMAL,DataExpression.UpdateSelf));

            }
            //本次订转销之后单据就提货完成标记
            if (bPickqtyOk)
            {
                // 1.订单来源渠道 2.全国配送 3.顾客自提 5总部配送 6同城配送
                if (otpReq.getShipType().equals("1") || otpReq.getShipType().equals("3") || otpReq.getDeliveryHandinput().equals("Y"))
                {
                    ub_DCP_ORDER.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));//已完成
                    ub_DCP_ORDER.addUpdateValue("COMPLETE_DATETIME", new DataValue(sDatetimeMs, Types.VARCHAR));
                    LogStatus="11";
                }
                else
                {
                    ub_DCP_ORDER.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));//已发货
                    LogStatus="10";
                }

                //更新商城订单信息
                if (otpReq.getLoadDocType().equals("LINE") || otpReq.getLoadDocType().equals("MINI") || otpReq.getLoadDocType().equals("WECHAT"))
                {
                    //手工录入的物流单号也需要直接完成
                    if (LogStatus.equals("11"))//已完成
                    {
                        JSONObject js=new JSONObject();
                        js.put("serviceId", "OrderStatusUpdate");
                        js.put("orderNo", orderNo);
                        js.put("statusType", "1");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
                        js.put("status", "6");//交易状态 3=已接单 4=已发货 5=已收货未回款 6=交易成功（确认收货，货款两清） 7=交易关闭（取消订单、全额退款、超时未支付、拒绝接单）
                        js.put("description", "已完成");
                        js.put("oprId", "admin");
                        js.put("orgType", "2");
                        js.put("orgId", shopId);
                        js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));

                        JSONObject body =null;
                        com.alibaba.fastjson.JSONArray deliverInfo = new com.alibaba.fastjson.JSONArray();
                        if (otpReq.getDeliveryType()!=null)
                        {
                            //物流信息
                            body = new JSONObject();
                            //body.put("expressType", otpReq.getDeliveryType());
                            body.put("expressTypeName", otpReq.getSubDeliveryCompanyName());
                            body.put("expressBillNo", otpReq.getDeliveryNo());
                            //body.put("expressTypeCode", otpReq.getDeliveryType());
                            body.put("expressTypeCode", otpReq.getSubDeliveryCompanyNo());
                            deliverInfo.add(body);
                            if (!"KDN".equals(otpReq.getDeliveryType()))
                            {
                                js.put("deliverInfo", deliverInfo);//不要添加 物流信息了，因为会回调
                            }



                            //写订单日志:已发货的物流日志
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(otpReq.getChannelId());
                            oslog.setDisplay("1");
                            oslog.seteId(eId);
                            oslog.setLoadDocBillType(otpReq.getLoadDocType());
                            oslog.setLoadDocOrderNo(orderNo);
                            oslog.setLoadDocType(otpReq.getLoadDocType());
                            oslog.setMachShopName(otpReq.getOpMachineNo());
                            oslog.setMachShopNo(otpReq.getOpMachineNo());
                            oslog.setMemo("物流配送:" + otpReq.getDeliveryType()+"-"+otpReq.getDeliveryNo());
                            if(otpReq.getDeliveryType()!=null)
                            {
                                String deliveryTypeName = otpReq.getSubDeliveryCompanyName();
                                if (deliveryTypeName==null||deliveryTypeName.isEmpty())
                                {
                                    deliveryTypeName = HelpTools.getDeliveryTypeName(otpReq.getDeliveryType());
                                }
                                oslog.setMemo("物流配送:" + deliveryTypeName+"-"+otpReq.getDeliveryNo());
                            }
                            if (req.getPlantType()!=null&&!req.getPlantType().isEmpty())
                            {
                                oslog.setMemo(oslog.getMemo()+"<br>操作平台:"+req.getPlantType());
                            }

                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName(otpReq.getOpOpName());
                            oslog.setOpNo(otpReq.getOpOpNo());
                            oslog.setOrderNo(orderNo);
                            oslog.setShippingShopName(otpReq.getShippingShopName());
                            oslog.setShippingShopNo(otpReq.getShippingShopNo());
                            oslog.setShopName(otpReq.getOpShopId());
                            oslog.setShopNo(otpReq.getOpShopId());
                            oslog.setStatus(LogStatus);
                            //
                            String statusType="1";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName=HelpTools.GetOrderStatusName(statusType, "10", statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            data.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

                        }
                        String request = js.toString();
                        HttpSend.MicroMarkSend(request, eId, "OrderStatusUpdate",otpReq.getChannelId());
                        body=null;
                        deliverInfo=null;
                        js=null;
                    }
                    else //已发货
                    {
                        JSONObject js=new JSONObject();
                        js.put("serviceId", "OrderStatusUpdate");
                        js.put("orderNo", orderNo);
                        js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
                        js.put("status", "1");//物流状态：0=未配送 1=配送中 2=已配送 3=确认收货
                        js.put("description", "物流已发货");
                        js.put("oprId", "admin");
                        js.put("orgType", "2");
                        js.put("orgId", shopId);
                        js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));

                        JSONObject body =null;
                        com.alibaba.fastjson.JSONArray deliverInfo = new com.alibaba.fastjson.JSONArray();
                        if (otpReq.getDeliveryType()!=null)
                        {
                            //物流信息
                            body = new JSONObject();
                            //body.put("expressType", otpReq.getDeliveryType());
                            body.put("expressTypeName", otpReq.getSubDeliveryCompanyName());
                            body.put("expressBillNo", otpReq.getDeliveryNo());
                            //body.put("expressTypeCode", otpReq.getDeliveryType());
                            body.put("expressTypeCode", otpReq.getSubDeliveryCompanyNo());
                            deliverInfo.add(body);
                            //js.put("deliverInfo", deliverInfo);
                            if (!"KDN".equals(otpReq.getDeliveryType()))
                            {
                                js.put("deliverInfo", deliverInfo);//不要添加 物流信息了，因为会回调
                            }


                        }
                        String request = js.toString();
                        HttpSend.MicroMarkSend(request, eId, "OrderStatusUpdate",otpReq.getChannelId());
                        body=null;
                        deliverInfo=null;
                        js=null;
                    }

                }

            }
            else
            {
                LogStatus="101";
            }

            //没有尾款折扣，不刷新订单数据
            if (bFinalPayDisc)
            {
                //付款活动折扣处理
                ub_DCP_ORDER.addUpdateValue("TOT_AMT_MERRECEIVE", new DataValue(bdm_tot_merreceive.toPlainString(), Types.DECIMAL));
                ub_DCP_ORDER.addUpdateValue("TOT_AMT_CUSTPAYREAL", new DataValue(bdm_tot_custpayreal.toPlainString(), Types.DECIMAL));
                ub_DCP_ORDER.addUpdateValue("TOT_DISC_MERRECEIVE", new DataValue(bdm_tot_merdiscount.toPlainString(), Types.DECIMAL));
                ub_DCP_ORDER.addUpdateValue("TOT_DISC_CUSTPAYREAL", new DataValue(bdm_tot_merdiscount.add(bdm_tot_thirddiscount).toPlainString(), Types.DECIMAL));

                //尾款付清
                if (bPayComplete)
                {
                    ub_DCP_ORDER.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR));// 1.未支付 2.部分支付 3.付清
                    ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue("TOT_AMT", Types.DECIMAL,DataExpression.OtherFieldname));//已付金额
                }
            }
            ub_DCP_ORDER.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            ub_DCP_ORDER.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

            //更新条件
            ub_DCP_ORDER.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub_DCP_ORDER.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
            data.add(new DataProcessBean(ub_DCP_ORDER));

            //写订单日志
            orderStatusLog oslog=new orderStatusLog();
            oslog.setCallback_status("N");
            oslog.setChannelId(otpReq.getChannelId());
            oslog.setDisplay("0");
            oslog.seteId(eId);
            oslog.setLoadDocBillType(otpReq.getLoadDocType());
            oslog.setLoadDocOrderNo(orderNo);
            oslog.setLoadDocType(otpReq.getLoadDocType());
            oslog.setMachShopName(otpReq.getOpMachineNo());
            oslog.setMachShopNo(otpReq.getOpMachineNo());
            oslog.setMemo(otpReq.getMemo());
            if (req.getPlantType()!=null&&!req.getPlantType().isEmpty())
            {
                if (oslog.getMemo()==null||oslog.getMemo().isEmpty())
                {
                    oslog.setMemo("操作平台:"+req.getPlantType());
                }
                else
                {
                    oslog.setMemo(oslog.getMemo()+"<br>操作平台:"+req.getPlantType());
                }

            }
            oslog.setNeed_callback("N");
            oslog.setNeed_notify("N");
            oslog.setNotify_status("N");
            oslog.setOpName(otpReq.getOpOpName());
            oslog.setOpNo(otpReq.getOpOpNo());
            oslog.setOrderNo(orderNo);
            oslog.setShippingShopName(otpReq.getShippingShopName());
            oslog.setShippingShopNo(otpReq.getShippingShopNo());
            oslog.setShopName(otpReq.getOpShopId());
            oslog.setShopNo(otpReq.getOpShopId());
            oslog.setStatus(LogStatus);
            //
            String statusType="1";
            StringBuilder statusTypeName=new StringBuilder();
            String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
            oslog.setStatusName(statusName);
            oslog.setStatusType(statusType);
            oslog.setStatusTypeName(statusTypeName.toString());
            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
            data.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

            if (b_subOrderFlag)
            {
                //更新(主)订单主表DCP_ORDER
                ub_DCP_ORDER = new UptBean("DCP_ORDER");
                //更新值
                ub_DCP_ORDER.addUpdateValue("WRITEOFFAMT", new DataValue("", Types.DECIMAL,DataExpression.UpdateSelf));//已提货金额
                ub_DCP_ORDER.addUpdateValue("POINTQTY", new DataValue(pointQty, Types.DECIMAL,DataExpression.UpdateSelf));//会员积分
                ub_DCP_ORDER.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                ub_DCP_ORDER.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                //本次订转销之后单据就提货完成标记
                if (bPickqtyOk)
                {
                    //(尾款一次性付清)
                    ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue("TOT_AMT", Types.DECIMAL,DataExpression.OtherFieldname));//已付金额
                    ub_DCP_ORDER.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR));// 1.未支付 2.部分支付 3.付清
                    ub_DCP_ORDER.addUpdateValue("ORDERTOSALE_DATETIME", new DataValue(sDatetimeMs, Types.VARCHAR));

                    // 1.订单来源渠道 2.全国配送 3.顾客自提 5总部配送 6同城配送
                    if ((otpReq.getShipType().equals("1") && otpReq.getDeliveryType()!=null && !otpReq.getDeliveryType().equals("21")) || otpReq.getShipType().equals("3") || otpReq.getDeliveryHandinput().equals("Y"))
                    {
                        ub_DCP_ORDER.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));//已完成
                        ub_DCP_ORDER.addUpdateValue("COMPLETE_DATETIME", new DataValue(sDatetimeMs, Types.VARCHAR));
                    }
                    else
                    {
                        ub_DCP_ORDER.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));//已发货
                    }
                }
                else
                {
                    ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue(payTot.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(), Types.DECIMAL,DataExpression.UpdateSelf));//已付金额
                }

                //没有尾款折扣，不刷新订单数据
                if (bFinalPayDisc)
                {
                    //付款活动折扣处理
                    ub_DCP_ORDER.addUpdateValue("TOT_AMT_MERRECEIVE", new DataValue(bdm_tot_merreceive.toPlainString(), Types.DECIMAL));
                    ub_DCP_ORDER.addUpdateValue("TOT_AMT_CUSTPAYREAL", new DataValue(bdm_tot_custpayreal.toPlainString(), Types.DECIMAL));
                    ub_DCP_ORDER.addUpdateValue("TOT_DISC_MERRECEIVE", new DataValue(bdm_tot_merdiscount.toPlainString(), Types.DECIMAL));
                    ub_DCP_ORDER.addUpdateValue("TOT_DISC_CUSTPAYREAL", new DataValue(bdm_tot_merdiscount.add(bdm_tot_thirddiscount).toPlainString(), Types.DECIMAL));

                    //尾款付清
                    if (bPayComplete)
                    {
                        ub_DCP_ORDER.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR));// 1.未支付 2.部分支付 3.付清
                        ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue("TOT_AMT", Types.DECIMAL,DataExpression.OtherFieldname));//已付金额
                    }
                }

                //更新条件
                ub_DCP_ORDER.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub_DCP_ORDER.addCondition("ORDERNO", new DataValue(otpReq.getHeadOrderNo(), Types.VARCHAR));
                data.add(new DataProcessBean(ub_DCP_ORDER));


                //写订单日志
                oslog=new orderStatusLog();
                oslog.setCallback_status("N");
                oslog.setChannelId(otpReq.getChannelId());
                oslog.setDisplay("0");
                oslog.seteId(eId);
                oslog.setLoadDocBillType(otpReq.getLoadDocType());
                oslog.setLoadDocOrderNo(otpReq.getHeadOrderNo());
                oslog.setLoadDocType(otpReq.getLoadDocType());
                oslog.setMachShopName(otpReq.getOpMachineNo());
                oslog.setMachShopNo(otpReq.getOpMachineNo());
                oslog.setMemo(otpReq.getMemo());
                if (req.getPlantType()!=null&&!req.getPlantType().isEmpty())
                {
                    if (oslog.getMemo()==null||oslog.getMemo().isEmpty())
                    {
                        oslog.setMemo("操作平台:"+req.getPlantType());
                    }
                    else
                    {
                        oslog.setMemo(oslog.getMemo()+"<br>操作平台:"+req.getPlantType());
                    }

                }
                oslog.setNeed_callback("N");
                oslog.setNeed_notify("N");
                oslog.setNotify_status("N");
                oslog.setOpName(otpReq.getOpOpName());
                oslog.setOpNo(otpReq.getOpOpNo());
                oslog.setOrderNo(otpReq.getHeadOrderNo());
                oslog.setShippingShopName(otpReq.getShippingShopName());
                oslog.setShippingShopNo(otpReq.getShippingShopNo());
                oslog.setShopName(otpReq.getOpShopId());
                oslog.setShopNo(otpReq.getOpShopId());
                oslog.setStatus(LogStatus);
                //
                statusType="1";
                statusTypeName=new StringBuilder();
                statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
                oslog.setStatusName(statusName);
                oslog.setStatusType(statusType);
                oslog.setStatusTypeName(statusTypeName.toString());
                oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                data.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));

            }


            //更新订单付款主表DCP_ORDER_PAY
            UptBean ub_DCP_ORDER_PAY = new UptBean("DCP_ORDER_PAY");
            //更新值
            if(isOrderToSaleAll.equals("1"))
            {
                ub_DCP_ORDER_PAY.addUpdateValue("WRITEOFFAMT", new DataValue(otpReq.getPayAmt(), Types.DECIMAL,DataExpression.UpdateSelf));//已提货金额
            }else
            {
                ub_DCP_ORDER_PAY.addUpdateValue("WRITEOFFAMT", new DataValue("PAYREALAMT", Types.DECIMAL,DataExpression.OtherFieldname));//已提货金额
            }
            ub_DCP_ORDER_PAY.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub_DCP_ORDER_PAY.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            //更新条件
            ub_DCP_ORDER_PAY.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub_DCP_ORDER_PAY.addCondition("SOURCEBILLNO", new DataValue(b_subOrderFlag?otpReq.getHeadOrderNo():orderNo, Types.VARCHAR));
            data.add(new DataProcessBean(ub_DCP_ORDER_PAY));

            //

            String loadDocType1=getQData_Order.get(0).get("LOADDOCTYPE")==null?"":getQData_Order.get(0).get("LOADDOCTYPE").toString();

            //有赞订单处理
            if(orderLoadDocType.YOUZAN.equals(loadDocType1)){
                String appType1=req.getApiUser()==null?"":req.getApiUser().getAppType();
                //有赞发起的，不需要再回调有赞
                if(!"YOUZAN".equals(appType1)){
                    YouZanCallBackServiceV3 ycb=new YouZanCallBackServiceV3();
                    try{
                        Map<String, Object> otherMap = new HashMap<String, Object>();
                        otherMap.put("extra_info", otpReq.getOpOpNo()==null?otpReq.getOpShopId():otpReq.getOpOpNo());//操作人
                        JsonBasicRes thisRes=new JsonBasicRes();
                        thisRes=ycb.OrderToSale(eId, orderNo, shopId,otherMap,null);
//    					if(!thisRes.isSuccess()){
//    						errorMessage.append("操作失败："+thisRes.getServiceDescription());
//    						return false;
//    					}
                    }catch (Exception e) {
//    					errorMessage.append("操作失败："+e.getMessage());
//    					return false;
                    }
                }
            }
            //企迈订单处理
            if(orderLoadDocType.QIMAI.equals(loadDocType1)){
                String appType1=req.getApiUser()==null?"":req.getApiUser().getAppType();
                if(!"QIMAI".equals(appType1)){

                    try{
                        Map<String, Object> otherMap = new HashMap<String, Object>();
                        JsonBasicRes thisRes=new JsonBasicRes();
                        thisRes=QiMaiService.getInstance().OrderToSale(getQData_Order.get(0), otherMap);
                        if(!thisRes.isSuccess()){
                            errorMessage.append("操作失败："+thisRes.getServiceDescription());
                            return false;
                        }
                    }catch (Exception e) {
                        errorMessage.append("操作失败："+e.getMessage());
                        return false;
                    }
                }
            }


            //产生调拨单
            if (isStockTransfer) {

                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调拨单创建开始 \r\n");
                if (!stockTransfer(req,eId,stockTransferShopId,stockTransferShippingShopNo,sale,saleno,errorMessage,data)) {
                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调拨单创建失败,原因:"+errorMessage+" \r\n");
                    data.clear();
                    return false;
                }

                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销(绩效算下单门店),订单单号:"+orderNo+ ",调拨单创建结束 \r\n");
            }



            //【ID1035408】【阿哆诺斯升级3.0】券找零移植3.0---POS服务  by jinzma 20230830
            if (!CollectionUtils.isEmpty(req.getRequest().getCouponChangeList())) {
                String[] columns = {"EID", "SHOPID", "SALENO", "COUPONCODE", "COUPONNO", "QTY", "AMT", "BDATE", "SDATE",
                        "STIME", "OPNO", "TRAN_TIME"};
                List<order.CouponChange> couponChangeList = req.getRequest().getCouponChangeList();
                for (order.CouponChange couponChange : couponChangeList) {
                    //找零券校验
                    if (Check.Null(couponChange.getCouponCode())) {
                        errorMessage.append("找零券的券流水号不可为空值, ");
                        data.clear();
                        return false;
                    }
                    if (Check.Null(couponChange.getCouponType())) {
                        errorMessage.append("找零券的券类型不可为空值, ");
                        data.clear();
                        return false;
                    }
                    if (!PosPub.isNumeric(couponChange.getQuantity())) {
                        errorMessage.append("找零券的券数量不可为空值或非数值, ");
                        data.clear();
                        return false;
                    }
                    if (!PosPub.isNumericType(couponChange.getFaceAmount())) {
                        errorMessage.append("找零券的券金额不可为空值或非数值, ");
                        data.clear();
                        return false;
                    }

                    sql = " SELECT EID,COUPONCODE,COUPONTYPEID,FACEAMOUNT,STATUS FROM CRM_COUPON"
                            + " WHERE EID='" + eId + "' AND COUPONCODE='" + couponChange.getCouponCode() + "' ";
                    List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
                    if (CollectionUtils.isEmpty(getQData)) {
                        errorMessage.append("找零券:" + couponChange.getCouponCode() + " 此券号不存在 ");
                        data.clear();
                        return false;
                    } else {
                        if (!"2".equals(getQData.get(0).get("STATUS").toString())) {
                            errorMessage.append("存在不可找零的券,券号:" + couponChange.getCouponCode() + ",请重试 ");
                            data.clear();
                            return false;
                        }
                    }

                    //写入DCP_SALE_COUPON
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(saleno, Types.VARCHAR),
                            new DataValue(couponChange.getCouponCode(), Types.VARCHAR),
                            new DataValue(couponChange.getCouponNo(), Types.VARCHAR),
                            new DataValue(couponChange.getQuantity(), Types.VARCHAR),
                            new DataValue(couponChange.getFaceAmount(), Types.VARCHAR),
                            new DataValue(sale.getbDate(), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            new DataValue(sale.getOpNO(), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), Types.VARCHAR),   //2023-08-24 16:54:39
                    };

                    InsBean ib = new InsBean("DCP_SALE_COUPON", columns);
                    ib.addValues(insValue);
                    data.add(new DataProcessBean(ib));

                }
            }



            //建行实物券，有价券
            //【ID1039103】[3.0]金贝儿--建行开发接口评估---POS服务
            List<order.otherCoupnPay> otherCoupnPayList=req.getRequest().getOtherCoupnPayList();
            if (otherCoupnPayList != null && otherCoupnPayList.size()>0)
            {
                String[] columns = {"EID","SHOP","SALENO","ITEM","CARDNO"};

                for (order.otherCoupnPay other : otherCoupnPayList)
                {
                    DataValue[] insValue = new DataValue[] {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(saleno, Types.VARCHAR),
                            new DataValue(other.getItem(), Types.VARCHAR),
                            new DataValue(other.getCouponCode(), Types.VARCHAR),
                    };

                    InsBean ib = new InsBean("DCP_OTHERCOUPON_PAY", columns);
                    ib.addValues(insValue);
                    data.add(new DataProcessBean(ib));
                }
            }




            //最后执行SQL
            dao.useTransactionProcessData(data);

            //销售单号
            res.getDatas().setBillNo(saleno);

            //
            errorMessage.setLength(0);
            data.clear();


            //***********调用存储过程分摊收款方式到商品***************
            try
            {
                String procedure="SP_DCP_SALE_DETAIL_PAY";
                Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                inputParameter.put(1,eId);                                       //--企业ID
                inputParameter.put(2,shopId);                                    //--组织
                inputParameter.put(3,saleno);
                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                data.add(new DataProcessBean(pdb));
                //最后执行SQL
                dao.useTransactionProcessData(data);
                data.clear();
            }
            catch (Exception e)
            {
                PosPub.WriteETLJOBLog("订单号："+otpReq.getOrderNo()+", 出貨訂轉銷DCP_OrderToSaleProcess_Open： 分摊支付方式到商品SP_DCP_SALE_DETAIL_PAY 存储过程失败："+ e.getMessage());
            }

            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
            try
            {
                WebHookService.stockSync(eId,shopId,saleno);

                if (isStockTransfer) {
                    WebHookService.stockSync(eId, stockTransferShippingShopNo, stockOutNo);
                    WebHookService.stockSync(eId, stockTransferShopId, stockInNo);
                }

            }
            catch (Exception e)
            {

            }

            //***********调用开发票接口***************
            try
            {
                if(AreaType!=null&&AreaType.equals("TW"))
                {
                    if (otpReq.getIsInvoice()!=null && otpReq.getIsInvoice().equals("Y"))
                    {
                        //仅针对渠道类型
                        if (otpReq.getLoadDocType().equals(orderLoadDocType.POS)
                                ||otpReq.getLoadDocType().equals(orderLoadDocType.POSANDROID)
                                ||otpReq.getLoadDocType().equals(orderLoadDocType.PADGUIDE)
                                ||otpReq.getLoadDocType().equals(orderLoadDocType.WAIMAI))
                        {

                            JSONObject request_InvoiceCreate = new JSONObject();
                            JSONArray datas_invoiceCreateList = new JSONArray();

                            request_InvoiceCreate.put("orgId", shopId);
                            request_InvoiceCreate.put("recipient", "3");//1.云POS 2.全渠道会员 3.云中台 4.外卖点餐
                            request_InvoiceCreate.put("saleType", "Sale");//单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值
                            request_InvoiceCreate.put("saleNo", saleno);
                            request_InvoiceCreate.put("invCount", "1");//发票张数，不传时默认1
                            request_InvoiceCreate.put("freeCode", sale.getFreeCode());//零税证号
                            request_InvoiceCreate.put("passport", sale.getPassport());//护照号
                            request_InvoiceCreate.put("invSplitType", "1");//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分，不传时默认1不拆分

                            //发票信息
                            JSONObject req_InvoiceCreate = new JSONObject();
                            req_InvoiceCreate.put("invItem", "1");//发票项次
                            //req_InvoiceCreate.put("invNo", "");
                            req_InvoiceCreate.put("invMemo", otpReq.getInvMemo());
                            req_InvoiceCreate.put("carrierCode", otpReq.getCarrierCode());
                            req_InvoiceCreate.put("loveCode", otpReq.getLoveCode());
                            req_InvoiceCreate.put("buyerGuiNo", otpReq.getBuyerGuiNo());
                            req_InvoiceCreate.put("bDate", otpReq.getbDate());
                            req_InvoiceCreate.put("invType", otpReq.getInvoiceType());//发票类型：0园区收据，2二联，3三联，4收据，5二联式收银机发票，6三联式收银机发票，X不申报，7电子发票
                            req_InvoiceCreate.put("carrierShowId", otpReq.getCarrierShowId());
                            req_InvoiceCreate.put("carrierHiddenId", otpReq.getCarrierHiddenId());
                            datas_invoiceCreateList.put(req_InvoiceCreate);

                            request_InvoiceCreate.put("invoiceList", datas_invoiceCreateList);
                            request_InvoiceCreate.put("goodsList", datas_goodsList);
                            request_InvoiceCreate.put("payList", datas_payList);

                            String str_invoiceCreate=request_InvoiceCreate.toString();
                            String requestId=PosPub.getGUID(false);
                            //
                            Map<String, Object> map = new HashMap<>();
                            map.put("serviceId", "POS_InvoiceCreate_Open");

                            if (req.getApiUser() == null)
                            {
                                map.put("apiUserCode",Yc_Key);
                                map.put("sign",PosPub.encodeMD5(str_invoiceCreate + Yc_Sign_Key));
                            }
                            else
                            {
                                map.put("apiUserCode",req.getApiUserCode());
                                map.put("sign",PosPub.encodeMD5(str_invoiceCreate + req.getApiUser().getUserKey()));
                            }
                            map.put("langType",req.getLangType());
                            map.put("requestId",requestId);
                            map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                            map.put("version","3.0");

                            PosPub.WriteETLJOBLog("出貨訂轉銷請求POS_InvoiceCreate_Open： "+ str_invoiceCreate);

                            String resbody_invoiceCreate=HttpSend.doPost(posUrl,str_invoiceCreate,map,requestId);
                            map.clear();
                            map=null;

                            PosPub.WriteETLJOBLog("出貨訂轉銷返回POS_InvoiceCreate_Open： "+ resbody_invoiceCreate);

                            if (resbody_invoiceCreate.equals("")==false)
                            {
                                JSONObject jsonres_InvoiceCreate = new JSONObject(resbody_invoiceCreate);
                                boolean success = jsonres_InvoiceCreate.getBoolean("success");
                                if (success)
                                {
                                    JSONObject jsonres_datas=jsonres_InvoiceCreate.getJSONObject("datas");

                                    JSONArray invoiceList=jsonres_datas.getJSONArray("invoiceList");

                                    if (invoiceList.length()>0)
                                    {
                                        //发票信息
                                        String invoiceDate=invoiceList.getJSONObject(0).getString("invoiceDate");//开票日期YYYY-MM-DD
                                        int invOperateType=invoiceList.getJSONObject(0).getInt("invOperateType");//0-开立 1-作废 2-折让
                                        String invNo=invoiceList.getJSONObject(0).getString("invNo");//

                                        //发票日期
                                        if (invoiceDate.length()==10)
                                        {
                                            invoiceDate=invoiceDate.substring(0,4) +invoiceDate.substring(5,2) + invoiceDate.substring(8,2);
                                        }

                                        //更新订单主表DCP_ORDER
                                        UptBean ub_DCP_ORDER_Invoice = new UptBean("DCP_ORDER");
                                        //更新值
                                        ub_DCP_ORDER_Invoice.addUpdateValue("INVOICEDATE", new DataValue(invoiceDate, Types.VARCHAR));//
                                        ub_DCP_ORDER_Invoice.addUpdateValue("INVOPERATETYPE", new DataValue(invOperateType, Types.VARCHAR));//
                                        ub_DCP_ORDER_Invoice.addUpdateValue("INVNO", new DataValue(invNo, Types.VARCHAR));//
                                        ub_DCP_ORDER_Invoice.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                        ub_DCP_ORDER_Invoice.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                                        //更新条件
                                        ub_DCP_ORDER_Invoice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        ub_DCP_ORDER_Invoice.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                        data.add(new DataProcessBean(ub_DCP_ORDER_Invoice));

                                        JSONArray detailList=invoiceList.getJSONObject(0).getJSONArray("detailList");
                                        for (int di = 0; di < detailList.length(); di++)
                                        {
                                            //发票商品
                                            int item=detailList.getJSONObject(di).getInt("item");
                                            int invItem=detailList.getJSONObject(di).getInt("invItem");
                                            String detail_invNo=detailList.getJSONObject(di).getString("invNo");

                                            //更新(主)订单明细表DCP_ORDER_DETAIL
                                            UptBean ub_DCP_ORDER_Detail_Invoice = new UptBean("DCP_ORDER_DETAIL");
                                            //更新值
                                            ub_DCP_ORDER_Detail_Invoice.addUpdateValue("INVITEM", new DataValue(invItem, Types.VARCHAR));//
                                            ub_DCP_ORDER_Detail_Invoice.addUpdateValue("INVNO", new DataValue(detail_invNo, Types.VARCHAR));//
                                            //更新条件
                                            ub_DCP_ORDER_Detail_Invoice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                            ub_DCP_ORDER_Detail_Invoice.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                            ub_DCP_ORDER_Detail_Invoice.addCondition("ITEM", new DataValue(item, Types.VARCHAR));

                                            data.add(new DataProcessBean(ub_DCP_ORDER_Detail_Invoice));
                                        }
                                        //开发票成功
                                        ParseJson pj = new ParseJson();
                                        DCP_OrderToSaleProcess_OpenRes.invoice myData=pj.jsonToBean(jsonres_datas.toString(),new TypeToken<DCP_OrderToSaleProcess_OpenRes.invoice>(){});
                                        pj=null;
                                        res.getDatas().setInvoiceInfo(myData);
                                        res.getDatas().getInvoiceInfo().setSuccess(true);
                                        res.getDatas().getInvoiceInfo().setServiceDescription("开发票成功！");

                                    }
                                }
                                else
                                {
                                    //开发票失败
                                    res.getDatas().getInvoiceInfo().setSuccess(false);
                                    String tempDesc=jsonres_InvoiceCreate.get("serviceDescription")==null||jsonres_InvoiceCreate.get("serviceDescription").toString().equals("null")?"null":jsonres_InvoiceCreate.get("serviceDescription").toString();
                                    res.getDatas().getInvoiceInfo().setServiceDescription("订单号："+orderNo+"调用开发票失败，接口返回为：" +tempDesc);

                                    //记录发票异常档
                                    DataProcessBean dpbBean=InvoiceCreate(eId, orderNo, otpReq.getOpOpNo(), otpReq.getOpOpName());
                                    if (dpbBean!=null)
                                    {
                                        data.add(dpbBean);
                                    }
                                }
                            }
                            else
                            {
                                //开发票失败
                                res.getDatas().getInvoiceInfo().setSuccess(false);
                                res.getDatas().getInvoiceInfo().setServiceDescription("订单号："+orderNo+"调用开发票失败，接口返回为空：");

                                //记录发票异常档
                                DataProcessBean dpbBean=InvoiceCreate(eId, orderNo, otpReq.getOpOpNo(), otpReq.getOpOpName());
                                if (dpbBean!=null)
                                {
                                    data.add(dpbBean);
                                }
                            }
                        }
                    }

                    //调用开发票后的执行
                    dao.useTransactionProcessData(data);
                }
            }
            catch (Exception ex)
            {
                try
                {
                    StringWriter errors = new StringWriter();
                    PrintWriter pw=new PrintWriter(errors);
                    ex.printStackTrace(pw);

                    pw.flush();
                    pw.close();

                    errors.flush();
                    errors.close();

                    PosPub.WriteETLJOBLog("出貨訂轉銷DCP_OrderToSaleProcess_Open，调用开发票POS_InvoiceCreate_Open异常原因："+ errors.toString());

                    //开发票失败
                    res.getDatas().getInvoiceInfo().setSuccess(false);
                    res.getDatas().getInvoiceInfo().setServiceDescription("订单号："+orderNo+"调用开发票异常原因："+ex.getMessage() +"<br/>" +ex.toString());

                    pw=null;
                    errors=null;
                }
                catch (Exception e)
                {

                }
            }


            //【ID1035408】【阿哆诺斯升级3.0】券找零移植3.0---POS服务 by jinzma 20230830
            if (!CollectionUtils.isEmpty(req.getRequest().getCouponChangeList())) {
                try {
                    DCP_OrderCreate orderCreate = new DCP_OrderCreate();
                    orderCreate.callMemberPayCouponChange(Yc_Key,Yc_Sign_Key,eId,memberOrderno,req.getRequest().getCouponChangeList());

                } catch (Exception e) {

                }
            }




        }
        catch (Exception ex)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                ex.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                String sqlerr= ex.getCause()==null || ex.getCause().getMessage()==null?"":ex.getCause().getMessage();

                PosPub.WriteETLJOBLog("出貨訂轉銷DCP_OrderToSaleProcess_Open： " + sqlerr +"\r\n"+ errors.toString());

                errorMessage.append("发现异常-调用订转销服务DCP_OrderToSaleProcess_Open失败:" + sqlerr +"\r\n"+ex.getMessage() +"<br/>" +errors.toString() );

                pw=null;
                errors=null;

                ////发票试算成功标记(如果发票试算成功,销售单保存失败,需要调用删除试算接口)
                if (bInvoiceCal)
                {
                    JSONObject request_InvoicePreDelete = new JSONObject();

                    request_InvoicePreDelete.put("saleType", "Sale");//单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值
                    request_InvoicePreDelete.put("saleNo", saleno);

                    String str_InvoicePreDelete=request_InvoicePreDelete.toString();

                    String requestId=PosPub.getGUID(false);
                    //
                    Map<String, Object> map = new HashMap<>();
                    map.put("serviceId", "POS_InvoicePreDelete");
                    if (req.getApiUser() == null)
                    {
                        map.put("apiUserCode",Yc_Key);
                        map.put("sign",PosPub.encodeMD5(str_InvoicePreDelete + Yc_Sign_Key));
                    }
                    else
                    {
                        map.put("apiUserCode",req.getApiUserCode());
                        map.put("sign",PosPub.encodeMD5(str_InvoicePreDelete + req.getApiUser().getUserKey()));
                    }
                    map.put("langType",req.getLangType());
                    map.put("requestId",requestId);
                    map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                    map.put("version","3.0");

                    PosPub.WriteETLJOBLog("出貨訂轉銷請求POS_InvoicePreDelete： "+ str_InvoicePreDelete);

                    String resbody_InvoicePreDelete=HttpSend.doPost(posUrl,str_InvoicePreDelete,map,requestId);
                    map.clear();
                    map=null;

                    PosPub.WriteETLJOBLog("出貨訂轉銷返回POS_InvoicePreDelete： "+ resbody_InvoicePreDelete);
                }

                //单据保存失败,如果上面没撤销,这里要撤销
                if (bMemberPay)
                {
                    MemberPayReverse(memberOrderno, Yc_Sign_Key, Yc_Key, Yc_Url, errorMessage);
                }

            }
            catch (Exception e)
            {

            }

            return false;
        }

        return true;
    }

    private String getOutCostWareHouse(String eId,String shopId)  throws Exception
    {
        String outCostWareHouse ="";
        String sql = null;

        sql = " select OUT_COST_WAREHOUSE  from DCP_ORG  where EID='"+eId+"' and organizationno='"+shopId+"' AND status='100' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && getQData.isEmpty() == false)
        {
            outCostWareHouse = getQData.get(0).get("OUT_COST_WAREHOUSE").toString();
        }
        return outCostWareHouse ;
    }


    /**
     * 销售单是否已存在
     * @param eId
     * @param shopId
     * @param saleno
     * @return
     * @throws Exception
     */
    private boolean getExistSaleno(String eId,String shopId,String saleno)  throws Exception
    {
        String sql = " select saleno from dcp_sale where eid='"+eId+"' and shopid='"+shopId+"' and saleno='"+saleno+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && getQData.isEmpty() == false)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    private String getQuerySql_TbGoods(String eId,String pluBarcode ) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select a.pluno,a.plubarcode,a.unit,b.ISPACKAGE,b.VIRTUAL   from DCP_BARCODE a   "
                + " inner join DCP_GOODS b on a.EID=b.EID and a.pluno=b.pluno  and a.status='100' and b.status='100'  "
                + " 	where a.EID = '"+eId+"' and a.plubarcode = '"+pluBarcode+"'  ");
        sql = sqlbuf.toString();
        return sql;
    }

    private String getQuerySql_ShippingFeeGoods(String eId , String pluBarcode ) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select a.pluno,a.plubarcode,b.wunit   from DCP_BARCODE a "
                + " inner join DCP_GOODS b on a.EID=b.EID and a.pluno=b.pluno and a.status='100' and b.status='100' and b.virtual='Y' "
                + " where a.EID='"+eId+"' and a.plubarcode='"+pluBarcode+"'  " );
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 取当天最大流水号,F_POS_TRNO函数 1个单号只能调一次，不然序号不连续
     * @param eId
     * @param shopId
     * @param bDate
     * @return
     */
    private String getTRNO(String eId,String shopId,String bDate,String saleno,DCP_OrderToSaleProcess_OpenReq req)
    {
        RedisPosPub redis=new RedisPosPub();

        String trno="";
        try
        {
            //F_POS_TRNO 调一次加一次，只能控制1单只能调1次，不然序号不连续

            //先取缓存
            String key="SALE:"+eId+":"+shopId+":"+bDate;
            trno= redis.getHashMap(key,saleno);
            if (Check.Null(trno))
            {
                //取库
                String sql_trno = " select F_POS_TRNO('"+eId+"','"+shopId+"','"+bDate+"') TRNO FROM dual ";

                List<Map<String, Object>> getData_trno=dao.executeQuerySQL(sql_trno, null);
                if(getData_trno==null || getData_trno.isEmpty())
                {
                    trno="1";
                }
                else
                {
                    if (Check.Null(getData_trno.get(0).get("TRNO").toString()))
                    {
                        trno="1";
                    }
                    else
                    {
                        trno=getData_trno.get(0).get("TRNO").toString();
                    }
                }
                redis.setHashMap(key,saleno,trno);
            }
        }
        catch (Exception e)
        {
            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"计算销售单最大流水号TRNO报错1："+e.getMessage() + errors.toString() +"\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"计算销售单最大流水号TRNO报错2："+e1.getMessage() +"\r\n");
            }

            trno="";
        }
        finally
        {
            redis.Close();
        }
        return trno;
    }

    /**
     * 计算总剩余商品数量，用于判断本次订转销后是全部完成，还是有剩余处理
     * @param eId
     * @param orderNo 主单号
     * @return
     */
    private String getTot_RemainQTY(String eId,String orderNo)
    {
        String remainQty="";
        try
        {
            String sql_tot = " select NVL(SUM(QTY),0) QTY,NVL(SUM(RQTY),0) RQTY,NVL(SUM(PICKQTY),0) PICKQTY from DCP_ORDER_DETAIL WHERE EID='"+eId+"' AND ORDERNO='"+orderNo+"'  ";
            List<Map<String, Object>> getData_tot=dao.executeQuerySQL(sql_tot, null);
            if(getData_tot==null || getData_tot.isEmpty())
            {
                remainQty="";
            }
            else
            {
                BigDecimal qty= new BigDecimal(getData_tot.get(0).get("QTY").toString());
                BigDecimal rqty= new BigDecimal(getData_tot.get(0).get("RQTY").toString());
                BigDecimal pickqty= new BigDecimal(getData_tot.get(0).get("PICKQTY").toString());
                remainQty=qty.subtract(rqty).subtract(pickqty).toPlainString();
            }
        }
        catch (Exception e)
        {
            remainQty="";
        }
        return remainQty;
    }

    /**
     * 会员撤销接口
     * @param memberOrderno 订单流水号
     * @param Yc_Sign_Key
     * @param Yc_Key
     * @param Yc_Url
     * @param errorMessage
     * @return
     */
    private boolean MemberPayReverse(String memberOrderno,String Yc_Sign_Key,String Yc_Key,String Yc_Url, StringBuilder errorMessage)
    {
        try
        {
            com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
            com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
            com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());

            reqheader.put("orderNo", memberOrderno);//需唯一
            //
            String req_sign=reqheader.toString() + Yc_Sign_Key;

            req_sign=DigestUtils.md5Hex(req_sign);

            //
            signheader.put("key", Yc_Key);//
            signheader.put("sign", req_sign);//md5

            payReq.put("serviceId", "MemberPayReverse");

            payReq.put("request", reqheader);
            payReq.put("sign", signheader);

            String str = payReq.toString();

            PosPub.WriteETLJOBLog("会员撤销付款接口MemberPayReverse请求内容："+str +"\r\n");

            //编码处理
            str=URLEncoder.encode(str,"UTF-8");

            String resbody=HttpSend.Sendcom(str, Yc_Url);

            PosPub.WriteETLJOBLog("会员撤销付款接口MemberPayReverse返回："+resbody +"\r\n");

            if (resbody.equals("")==false)
            {
                com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
                String serviceDescription=jsonres.get("serviceDescription")==null||jsonres.get("serviceDescription").toString().equals("null")?"null":jsonres.get("serviceDescription").toString();

                if (jsonres.get("success").toString().equals("true")==false)
                {
                    errorMessage.append("调用会员撤销付款接口MemberPayReverse失败返回：" +serviceDescription);
                    return false;
                }
            }
            else
            {
                errorMessage.append("调用会员撤销付款接口MemberPayReverse失败返回为空：");
                return false;
            }

            return true;
        }
        catch (Exception e)
        {
            errorMessage.append("调用会员撤销付款接口MemberPayReverse失败返回为空：");
            return false;
        }
    }

    /**
     * 订转销开发票异常档
     * @param eId
     * @param orderNo
     * @param opNo
     * @param opName
     * @return
     */
    private DataProcessBean InvoiceCreate(String eId,String orderNo,String opNo,String opName)
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql_ABNORMALINFO="select * from DCP_ORDER_ABNORMALINFO where EID='"+eId+"' and ORDERNO='"+orderNo+"' and ABNORMALTYPE='invoiceFinalOpen' ";
        List<Map<String, Object>> getData_getData_order_pay;
        try
        {
            getData_getData_order_pay = dao.executeQuerySQL(sql_ABNORMALINFO, null);
            if (getData_getData_order_pay==null || getData_getData_order_pay.isEmpty())
            {
                //DCP_ORDER_ABNORMALINFO
                String[] Columns_DCP_ORDER_ABNORMALINFO = {"EID","ORDERNO","ABNORMALTYPE",
                        "ABNORMALTYPENAME","ABNORMALTIME","MEMO","STATUS","LASTMODIOPID",
                        "LASTMODIOPNAME","LASTMODITIME"
                };

                DataValue[] insValue_DCP_ORDER_ABNORMALINFO = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(orderNo, Types.VARCHAR),
                        new DataValue("invoiceFinalOpen", Types.VARCHAR),
                        new DataValue("订转销开发票", Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue("", Types.VARCHAR),//备注
                        new DataValue("100", Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(opName, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                };

                InsBean ib_DCP_ORDER_ABNORMALINFO = new InsBean("DCP_ORDER_ABNORMALINFO", Columns_DCP_ORDER_ABNORMALINFO);
                ib_DCP_ORDER_ABNORMALINFO.addValues(insValue_DCP_ORDER_ABNORMALINFO);
                return new DataProcessBean(ib_DCP_ORDER_ABNORMALINFO);
            }
        }
        catch (Exception e)
        {
            return null;
        }

        return null;
    }
    private String getOrderToSaleNO(String eId,String orderNo) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String saleNo = null;
        String saleNoTemp = null;
        String tempNo = null;
        StringBuffer sqlbuf = new StringBuffer();
        saleNoTemp = "TV" + orderNo+'_';//matter.format(dt);  // 1.自采 2.统采 3.门店直供

        sqlbuf.append("select MAX(SALENO) AS SALENO  from DCP_SALE  "
                + " WHERE EID='"+eId+"' and SALENO LIKE '%%" + saleNoTemp + "%%' ");
        sql = sqlbuf.toString();

        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false) {

            tempNo = (String) getQData.get(0).get("SALENO");

            if (tempNo != null && tempNo.length() > 0) {
                long i;
                String temp = tempNo.substring(tempNo.length()-2, tempNo.length());
                i = Long.parseLong(temp) + 1;
                temp="00"+String.valueOf(i);
                temp=temp.substring(temp.length()-2, temp.length());
                saleNo = saleNoTemp+temp;

            }
            else {
                saleNo = saleNoTemp+"01";
            }
        }
        else {
            saleNo = saleNoTemp+"01";
        }

        return saleNo;
    }
    private boolean  afreshAssign(levelRequest otpReq,StringBuilder errorMessage) throws Exception  {

        String eId=otpReq.geteId().toString();
        String orderNo=otpReq.getOrderNo().toString();
        String sql = " select * from DCP_ORDER_DETAIL where EID='"+eId+"' and orderno='"+orderNo+"'" ;
        List<Map<String, Object>> getDataDetail=dao.executeQuerySQL(sql, null);
        sql = " select * from DCP_ORDER_DETAIL_AGIO where EID='"+eId+"' and orderno='"+orderNo+"'  and  NVL(DCTYPE,'AA')<>'60'" ;
        List<Map<String, Object>> getDataDetailAgio=dao.executeQuerySQL(sql, null);
        sql = " select * from DCP_ORDER_PAY_DETAIL where EID='"+eId+"' and SOURCEBILLNO='"+orderNo+"' and SOURCEBILLTYPE='Order' " ;
        List<Map<String, Object>> getDataPayDetail=dao.executeQuerySQL(sql, null);
        sql = " select * from DCP_ORDER where EID='"+eId+"' and orderno='"+orderNo+"'" ;
        List<Map<String, Object>> getData=dao.executeQuerySQL(sql, null);
        String isLastOrderToSale ="Y";
        if(getDataDetail!=null && getDataDetail.isEmpty()==false)
        {
            for (Map<String, Object> map : getDataDetail)
            {
                double orderQty=0;
                double orderPickQty=0;
                if(map.get("QTY")==null || Check.Null(map.get("QTY").toString()))
                {
                    orderQty=0;
                }else
                {
                    //orderQty=
                    if(PosPub.isNumericTypeMinus(map.get("QTY").toString())==false)
                    {
                        orderQty=0;
                    }else
                    {
                        orderQty=Double.valueOf(map.get("QTY").toString());
                    }
                }
                if(map.get("PICKQTY")==null || Check.Null(map.get("PICKQTY").toString()))
                {
                    orderPickQty=0;
                }else
                {
                    //orderQty=
                    if(PosPub.isNumericTypeMinus(map.get("PICKQTY").toString())==false)
                    {
                        orderPickQty=0;
                    }else
                    {
                        orderPickQty=Double.valueOf(map.get("PICKQTY").toString());
                    }
                }
                if(BigDecimalUtils.equal(orderQty,orderPickQty))
                {
                    continue;
                }
                String isFind="N";
                for (goods goods : otpReq.getGoodsList())
                {
                    if(map.get("ITEM").toString().equals(goods.getItem()))
                    {
                        isFind="Y";
                        double qty=Double.valueOf(goods.getQty().toString());
                        if(!BigDecimalUtils.equal(qty, orderQty-orderPickQty))
                        {
                            isLastOrderToSale="N";
                        }
                        break;
                    }
                }
                if(isFind.equals("N"))
                {
                    isLastOrderToSale="N";
                }
                if(isLastOrderToSale.equals("N"))
                {
                    break;
                }
            }
        }
        BigDecimal totAmt=new BigDecimal("0");//订转销总金额
        BigDecimal totQty=new BigDecimal("0");
        BigDecimal totOldAmt=new BigDecimal("0");
        BigDecimal totDisc=new BigDecimal("0");
        int    disc_item=0;
        for(int i=0;i<otpReq.getGoodsList().size();i++){
            Map<String, Object> condiV=new HashMap<String, Object>();
            condiV.put("ITEM", otpReq.getGoodsList().get(i).getItem());
            List<Map<String, Object>> detailList= MapDistinct.getWhereMap(getDataDetail, condiV, true);
            condiV.clear();
            if(detailList.size()==1 )
            {
                otpReq.getGoodsList().get(i).setAccNo(detailList.get(0).getOrDefault("ACCNO", "").toString());//授权人
                String packageType;
                if(Check.Null(detailList.get(0).getOrDefault("PACKAGETYPE", "1").toString()))
                {
                    packageType="1";
                }else
                {
                    packageType=detailList.get(0).getOrDefault("PACKAGETYPE", "1").toString();
                }
                BigDecimal amt=new BigDecimal("0");
//				double amtMerReceive=0;
//				double amtCustPayReal=0;
                BigDecimal disc=new BigDecimal("0");
                BigDecimal oldAmt=new BigDecimal("0");
                BigDecimal qty=new BigDecimal(otpReq.getGoodsList().get(i).getQty());
                BigDecimal orderQty=new BigDecimal(detailList.get(0).getOrDefault("QTY", 0).toString());
                double orderPrice = Double.valueOf(detailList.get(0).getOrDefault("PRICE", 0).toString());
                BigDecimal orderOldPrice = new BigDecimal(detailList.get(0).getOrDefault("OLDPRICE", 0).toString());
                double orderAmt=Double.valueOf(detailList.get(0).getOrDefault("AMT", 0).toString());
                BigDecimal orderOldAmt=new BigDecimal(detailList.get(0).getOrDefault("OLDAMT", 0).toString());
                //double orderAmtMerReceive=Double.valueOf(detailList.get(0).getOrDefault("AMT_MERRECEIVE", 0).toString());
                //double orderAmtCustPayReal=Double.valueOf(detailList.get(0).getOrDefault("AMT_CUSTPAYREAL", 0).toString());
                double orderDisc=Double.valueOf(detailList.get(0).getOrDefault("DISC", 0).toString());
                //double orderDiscCustPayReal=Double.valueOf(detailList.get(0).getOrDefault("AMT_CUSTPAYREAL_SALE", 0).toString());
                //double orderDiscMerReceive=Double.valueOf(detailList.get(0).getOrDefault("DISC_MERRECEIVE", 0).toString());

                //已冲销(提货)数量
                BigDecimal pickQty= new BigDecimal(detailList.get(0).getOrDefault("PICKQTY", 0).toString());
                //已冲销金额
                double writeOffAmt= Double.valueOf(detailList.get(0).getOrDefault("WRITEOFFAMT", 0).toString());
                //已冲原价销金额
                BigDecimal oldAmtSale=new BigDecimal("0");
                if (!Check.Null(detailList.get(0).getOrDefault("OLDAMT_SALE", 0).toString()))
                {
                    oldAmtSale= new BigDecimal(detailList.get(0).getOrDefault("OLDAMT_SALE", 0).toString());
                }

                //已冲销顾客支付金额
                double amtMerReceiveSale= Double.valueOf(detailList.get(0).getOrDefault("AMT_MERRECEIVE_SALE", 0).toString());
                //已冲销顾客支付金额
                double amtCustPayRealSale= Double.valueOf(detailList.get(0).getOrDefault("AMT_CUSTPAYREAL_SALE", 0).toString());
                //已冲销折扣金额
                double discSale= Double.valueOf(detailList.get(0).getOrDefault("DISC_SALE", 0).toString());
                //已冲销商家支付折扣金额
                double discMerReceiveSale= Double.valueOf(detailList.get(0).getOrDefault("DISC_MERRECEIVE_SALE", 0).toString());
                //已冲销顾客支付折扣金额
                double discCustPayRealSale= Double.valueOf(detailList.get(0).getOrDefault("DISC_CUSTPAYREAL_SALE", 0).toString());


                if(qty.compareTo(orderQty.subtract(pickQty))>0)
                {
                    errorMessage.append("订转销项次:"+otpReq.getGoodsList().get(i).getItem()+"此次转销售数量"+String.valueOf(qty)+"已经大于可转数量"+String.valueOf(orderQty.subtract(pickQty)));
                    return false;
                }else if(qty.compareTo(orderQty.subtract(pickQty))==0)
                {
                    oldAmt=orderOldAmt.subtract(oldAmtSale);
//					amt=orderAmt-writeOffAmt;
//					disc=orderDisc-discSale;
                }else
                {
                    oldAmt=orderOldPrice.multiply(qty).setScale(2,BigDecimal.ROUND_HALF_UP);
//					amt=BigDecimalUtils.round(orderPrice*qty,2);
//					disc=BigDecimalUtils.round(orderOldPrice*qty,2)-BigDecimalUtils.round(orderPrice*qty,2);
                }
                //goods.setAgioInfo(agioInfo);
                //otpReq.getGoodsList().get(i).setAmt_custPayReal(detailList.get(0).getOrDefault("AMT_CUSTPAYREAL", 0).toString());
                //otpReq.getGoodsList().get(i).setAmt_merReceive(detailList.get(0).getOrDefault("AMT_MERRECEIVE", 0).toString());
                otpReq.getGoodsList().get(i).setAttrName(detailList.get(0).getOrDefault("ATTRNAME", "").toString());
                //餐盒数量
                otpReq.getGoodsList().get(i).setBoxNum(detailList.get(0).getOrDefault("BOXNUM", 0).toString());
                //餐盒单价
                otpReq.getGoodsList().get(i).setBoxPrice(detailList.get(0).getOrDefault("BOXPRICE", 0).toString());
                //租柜编号
                otpReq.getGoodsList().get(i).setCounterNo(detailList.get(0).getOrDefault("COUNTERNO", "").toString());
                //提货劵劵号
                otpReq.getGoodsList().get(i).setCouponCode(detailList.get(0).getOrDefault("COUPONCODE", "").toString());
                //提货券类型
                otpReq.getGoodsList().get(i).setCouponType(detailList.get(0).getOrDefault("COUPONTYPE", "").toString());
                //					otpReq.getGoodsList().get(i).setDisc_custPayReal(disc_custPayReal);
                //					otpReq.getGoodsList().get(i).setDisc_merReceive(disc_merReceive);
                //特征名称
                otpReq.getGoodsList().get(i).setFeatureName(detailList.get(0).getOrDefault("FEATURENAME", "").toString());
                //特征编号
                otpReq.getGoodsList().get(i).setFeatureNo(detailList.get(0).getOrDefault("FEATURENO", "").toString());
                //是否赠品
                otpReq.getGoodsList().get(i).setGift(detailList.get(0).getOrDefault("GIFT", "0").toString());
                //赠送原因
                otpReq.getGoodsList().get(i).setGiftReason(detailList.get(0).getOrDefault("GIFTREASON", "").toString());
                //赠品上级商品
                otpReq.getGoodsList().get(i).setGiftSourceSerialNo(detailList.get(0).getOrDefault("GIFTSOURCESERIALNO", "").toString());
                //商品组
                otpReq.getGoodsList().get(i).setGoodsGroup(detailList.get(0).getOrDefault("GOODSGROUP", "").toString());
                //商品图片地址
                otpReq.getGoodsList().get(i).setGoodsUrl(detailList.get(0).getOrDefault("GOODSURL", "").toString());
                //单价含税否
                otpReq.getGoodsList().get(i).setInclTax(detailList.get(0).getOrDefault("INCLTAX", "").toString());
                //发票项次
                otpReq.getGoodsList().get(i).setInvItem(detailList.get(0).getOrDefault("INVITEM", "0").toString());
                //发票号码
                otpReq.getGoodsList().get(i).setInvNo(detailList.get(0).getOrDefault("INVNO", 0).toString());
                //发票开票拆分类型
                otpReq.getGoodsList().get(i).setInvSplitType(detailList.get(0).getOrDefault("INVSPLITTYPE", 0).toString());
                //otpReq.getGoodsList().get(i).setItem(item);
                //来源项次（拆单）
                otpReq.getGoodsList().get(i).setoItem(detailList.get(0).getOrDefault("OITEM", 0).toString());
                //来源项次（退单）
                otpReq.getGoodsList().get(i).setoReItem(detailList.get(0).getOrDefault("OREITEM", 0).toString());
                //原价总金额（含税）
                otpReq.getGoodsList().get(i).setOldAmt(String.valueOf(oldAmt));
                //原价
                otpReq.getGoodsList().get(i).setOldPrice(String.valueOf(orderOldPrice));
                //套餐来源项次
                otpReq.getGoodsList().get(i).setPackageMitem(detailList.get(0).getOrDefault("PACKAGEMITEM", 0).toString());
                //套餐类型(1、正常商品 2、套餐主商品  3、套餐子商品)
                otpReq.getGoodsList().get(i).setPackageType(packageType);
                //商品条码
                otpReq.getGoodsList().get(i).setPluBarcode(detailList.get(0).getOrDefault("PLUBARCODE", "").toString());
                //
                otpReq.getGoodsList().get(i).setPluName(detailList.get(0).getOrDefault("PLUNAME", "").toString());
                //
                otpReq.getGoodsList().get(i).setPluNo(detailList.get(0).getOrDefault("PLUNO", "").toString());
                //零售价
                otpReq.getGoodsList().get(i).setPrice(String.valueOf(orderPrice));
                //otpReq.getGoodsList().get(i).setQty(qty);
                //营业员
                otpReq.getGoodsList().get(i).setSellerName(detailList.get(0).getOrDefault("SELLERNAME", "").toString());
                //营业员编号
                otpReq.getGoodsList().get(i).setSellerNo(detailList.get(0).getOrDefault("SELLERNO", "").toString());
                //规格名称
                otpReq.getGoodsList().get(i).setSpecName(detailList.get(0).getOrDefault("SPECNAME", "").toString());
                //系统时间
                //otpReq.getGoodsList().get(i).setsTime(sTime);
                //销售单位
                otpReq.getGoodsList().get(i).setsUnit(detailList.get(0).getOrDefault("SUNIT", "").toString());
                //销售单位名称
                otpReq.getGoodsList().get(i).setsUnitName(detailList.get(0).getOrDefault("SUNITNAME", "").toString());
                //税别编码
                otpReq.getGoodsList().get(i).setTaxCode(detailList.get(0).getOrDefault("TAXCODE", "").toString());
                //税率
                otpReq.getGoodsList().get(i).setTaxRate(detailList.get(0).getOrDefault("TAXRATE", 0).toString());
                //税别类型
                otpReq.getGoodsList().get(i).setTaxType(detailList.get(0).getOrDefault("TAXTYPE", "").toString());
                //加料主商品项次
                otpReq.getGoodsList().get(i).setToppingMitem(detailList.get(0).getOrDefault("TOPPINGMITEM", 0).toString());
                //加料类型（1、正常商品 2、加料主商品  3、加料子商品）
                otpReq.getGoodsList().get(i).setToppingType(detailList.get(0).getOrDefault("TOPPINGTYPE", "1").toString());
                //是否虚拟商品：N否Y-是
                otpReq.getGoodsList().get(i).setVirtual(detailList.get(0).getOrDefault("VIRTUAL", "N").toString());
                //出货仓库
                otpReq.getGoodsList().get(i).setWarehouse(detailList.get(0).getOrDefault("WAREHOUSE","").toString());
                //出货仓库名称
                otpReq.getGoodsList().get(i).setWarehouseName(detailList.get(0).getOrDefault("WAREHOUSENAME", "N").toString());
                //								//已提数量
                //								otpReq.getGoodsList().get(i).setPickQty(String.valueOf(pickQty));
                //								//成交总额已冲销金额
                //								otpReq.getGoodsList().get(i).setWriteOffAmt(String.valueOf(writeOffAmt));
                //								//折扣已冲销金额
                //								otpReq.getGoodsList().get(i).setDiscSale(String.valueOf(discSale));
                //								//原单总金额（含税）已冲销金额
                //								otpReq.getGoodsList().get(i).setOldAmtSale(String.valueOf(oldAmtSale));
                //otpReq.getGoodsList().get(i).setDiscMerReceiveSale(discMerReceiveSale);
                //otpReq.getGoodsList().get(i).setDiscCustPayRealsale(discCustPayRealsale);
                //otpReq.getGoodsList().get(i).setAmtMerReceiveSale(amtMerReceiveSale);
                //goods.setAmtCustPayRealSale(amtCustPayRealSale);
                condiV.put("MITEM", otpReq.getGoodsList().get(i).getItem());
                List<Map<String, Object>> detailAgionList= MapDistinct.getWhereMap(getDataDetailAgio, condiV, true);
                condiV.clear();
                BigDecimal totAgioDisc=new BigDecimal("0");
                double totAgioDiscCustPayReal=0;
                double totAgioDiscMerReceive=0;
                if(detailAgionList.size()>0)
                {
                    if (otpReq.getGoodsList().get(i).getAgioInfo()==null)
                    {
                        otpReq.getGoodsList().get(i).setAgioInfo(new ArrayList<>());
                    }else
                    {
                        otpReq.getGoodsList().get(i).getAgioInfo().clear();
                    }
                    for (Map<String, Object> map : detailAgionList)
                    {
                        BigDecimal agioDisc=new BigDecimal("0");
                        BigDecimal orderAgioDisc=new BigDecimal("0");
                        BigDecimal orderAgioDiscSale=new BigDecimal("0");
                        //double agioDiscCustPayReal=0;
                        //double orderAgioDiscCustPayReal=0;
                        //double orderAgioDiscCustPayRealSale=0;
                        //double agioDiscMerReceive=0;
                        //double orderAgioDiscMerReceive=0;
                        //double orderAgioDiscMerReceiveSale=0;
                        BigDecimal orderAgioAmt=new BigDecimal("0");
                        BigDecimal agioAmt=new BigDecimal("0");
                        BigDecimal agioQty=new BigDecimal("0");
                        BigDecimal orderAgioAmtSale=new BigDecimal("0");
                        BigDecimal orderAgioQty=new BigDecimal("0");
                        BigDecimal orderAgioQtySale=new BigDecimal("0");
                        orderAgioDisc=new BigDecimal(map.getOrDefault("DISC", 0).toString());
                        orderAgioDiscSale=new BigDecimal(map.getOrDefault("DISC_SALE", 0).toString());
                        //orderAgioDiscCustPayReal=Double.valueOf(map.getOrDefault("DISC_CUSTPAYREAL", 0).toString());
                        //orderAgioDiscCustPayRealSale=Double.valueOf(map.getOrDefault("DISC_CUSTPAYREAL_SALE", 0).toString());
                        //orderAgioDiscMerReceive=Double.valueOf(map.getOrDefault("DISC_MERRECEIVE", 0).toString());
                        //orderAgioDiscMerReceiveSale=Double.valueOf(map.getOrDefault("DISC_MERRECEIVE_SALE", 0).toString());
                        orderAgioAmt=new BigDecimal(map.getOrDefault("AMT", 0).toString());
                        orderAgioAmtSale=new BigDecimal(Check.Null(map.getOrDefault("AMT_SALE", 0).toString()) ? "0":map.getOrDefault("AMT_SALE", 0).toString());
                        orderAgioQty=new BigDecimal(map.getOrDefault("QTY", 0).toString());
                        orderAgioQtySale=new BigDecimal(Check.Null(map.getOrDefault("QTY_SALE", 0).toString()) ? "0":map.getOrDefault("QTY_SALE", 0).toString());

                        if(qty.compareTo(orderQty.subtract(pickQty))==0)
                        {
                            agioDisc=orderAgioDisc.subtract(orderAgioDiscSale);
                            //agioDiscCustPayReal=orderAgioDiscCustPayReal-orderAgioDiscCustPayRealSale;
                            //agioDiscMerReceive=orderAgioDiscMerReceive-orderAgioDiscMerReceiveSale;
                            agioAmt =orderAgioAmt.subtract(orderAgioAmtSale);
                            agioQty =orderAgioQty.subtract(orderAgioQtySale);


                        }else
                        {
                            agioDisc=orderAgioDisc.multiply(qty).divide(orderQty,2,BigDecimal.ROUND_HALF_UP);
                            //agioDiscCustPayReal=BigDecimalUtils.round(orderAgioDiscCustPayReal*qty/orderQty,2);
                            //agioDiscMerReceive=BigDecimalUtils.round(orderAgioDiscMerReceive*qty/orderQty,2);
                            agioAmt =orderAgioAmt.multiply(qty).divide(orderQty,2,BigDecimal.ROUND_HALF_UP);
                            agioQty =orderAgioQty.multiply(qty).divide(orderQty,2,BigDecimal.ROUND_HALF_UP);
                        }
                        disc_item++;
                        Agio tempAgio=  new DCP_OrderToSaleProcess_OpenReq().new Agio() ;
                        tempAgio.setAmt(String.valueOf(agioAmt));
                        tempAgio.setBsNo(map.getOrDefault("BSNO", "").toString());
                        tempAgio.setDcType(map.getOrDefault("DCTYPE", "").toString());
                        tempAgio.setDcTypeName(map.getOrDefault("DCTYPENAME", "").toString());
                        tempAgio.setDisc(String.valueOf(agioDisc));
//						tempAgio.setDisc_custPayReal(String.valueOf(agioDiscCustPayReal));
//						tempAgio.setDisc_merReceive(String.valueOf(agioDiscMerReceive));
                        tempAgio.setDisc_custPayReal(String.valueOf(0));
                        tempAgio.setDisc_merReceive(String.valueOf(0));
                        tempAgio.setGiftCtf(map.getOrDefault("GIFTCTF", "").toString());
                        tempAgio.setGiftCtfNo(map.getOrDefault("GIFTCTFNO", "").toString());
                        tempAgio.setInputDisc(map.getOrDefault("INPUTDISC", "0").toString());
                        tempAgio.setItem(String.valueOf(map.getOrDefault("ITEM", "1").toString()));
                        tempAgio.setMitem((String.valueOf(map.getOrDefault("MITEM", String.valueOf(i)).toString())));
                        tempAgio.setPmtNo(map.getOrDefault("PMTNO", "").toString());
                        tempAgio.setQty(String.valueOf(agioQty));
                        tempAgio.setRealDisc(map.getOrDefault("REALDISC", "0").toString());
                        otpReq.getGoodsList().get(i).getAgioInfo().add(tempAgio);
                        totAgioDisc=totAgioDisc.add(agioDisc);
//						totAgioDiscCustPayReal=totAgioDiscCustPayReal+agioDiscCustPayReal;
//						totAgioDiscMerReceive=totAgioDiscMerReceive+agioDiscMerReceive;
                    }
                }
                disc = totAgioDisc;
                amt = oldAmt.subtract(disc);
                otpReq.getGoodsList().get(i).setAmt(String.valueOf(amt));
                //折扣金额
                otpReq.getGoodsList().get(i).setDisc(String.valueOf(disc));
                if(packageType.equals("1")||packageType.equals("2"))   //普通商品或套餐主商品
                {
                    totAmt=totAmt.add(amt);
                    totQty=totQty.add(qty);
                    totOldAmt=totOldAmt.add(oldAmt);
                    totDisc= totDisc.add(disc);
                }
            }
        }
        BigDecimal payAmt=new BigDecimal("0");
        BigDecimal shipFee=new BigDecimal("0");
        BigDecimal orderShipFee=new BigDecimal("0");
        BigDecimal orderShipFeeSale=new BigDecimal("0");
        BigDecimal packageFee=new BigDecimal("0");
        BigDecimal orderPackageFee=new BigDecimal("0");
        BigDecimal orderPackageFeeSale=new BigDecimal("0");
        BigDecimal orderTotOldAmt=new BigDecimal("0");
        BigDecimal eraseAmt=new BigDecimal("0");
        BigDecimal orderEraseAmt=new BigDecimal("0");
        BigDecimal orderEraseAmtSale=new BigDecimal("0");
        if(getData!=null && getData.isEmpty()==false)
        {
            orderShipFee=new BigDecimal(Check.Null(getData.get(0).get("SHIPFEE").toString()) ? "0" : getData.get(0).get("SHIPFEE").toString());
            orderShipFeeSale=new BigDecimal(Check.Null(getData.get(0).get("SHIPFEE_SALE").toString()) ? "0" : getData.get(0).get("SHIPFEE_SALE").toString());
            orderPackageFee=new BigDecimal(Check.Null(getData.get(0).get("PACKAGEFEE").toString()) ? "0" : getData.get(0).get("PACKAGEFEE").toString());
            orderPackageFeeSale=new BigDecimal(Check.Null(getData.get(0).get("PACKAGEFEE_SALE").toString()) ? "0" : getData.get(0).get("PACKAGEFEE_SALE").toString());
            orderTotOldAmt=new BigDecimal(Check.Null(getData.get(0).get("TOT_OLDAMT").toString()) ? "0" : getData.get(0).get("TOT_OLDAMT").toString());
            orderEraseAmt=new BigDecimal(Check.Null(getData.get(0).get("ERASE_AMT").toString()) ? "0" : getData.get(0).get("ERASE_AMT").toString());
            orderEraseAmtSale=new BigDecimal(Check.Null(getData.get(0).get("ERASE_AMT_SALE").toString()) ? "0" : getData.get(0).get("ERASE_AMT_SALE").toString());
        }

        if(orderEraseAmt.compareTo(orderEraseAmtSale)==0)
        {
            eraseAmt=new BigDecimal("0");
        }
        else
        {
            eraseAmt=orderEraseAmt.subtract(orderEraseAmtSale).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        if(isLastOrderToSale.equals("Y") || orderTotOldAmt.subtract(orderShipFee).subtract(orderPackageFee).compareTo(BigDecimal.ZERO)==0)
        {
            shipFee   =orderShipFee.subtract(orderShipFeeSale);
            packageFee=orderPackageFee.subtract(orderPackageFeeSale);
        }
        else
        {
            shipFee=totOldAmt.multiply(orderShipFee).divide(orderTotOldAmt.subtract(orderShipFee).subtract(orderPackageFee),2,BigDecimal.ROUND_HALF_UP);
            packageFee=totOldAmt.multiply(orderPackageFee).divide(orderTotOldAmt.subtract(orderShipFee).subtract(orderPackageFee),2,BigDecimal.ROUND_HALF_UP);
        }
        totOldAmt=totOldAmt.add(shipFee).add(packageFee);
        totAmt   =totAmt.add(shipFee).add(packageFee);
        payAmt   =totAmt.add(shipFee).add(packageFee).subtract(eraseAmt);
        otpReq.setTot_Amt(String.valueOf(totAmt));
        otpReq.setTot_oldAmt(String.valueOf(totOldAmt));
        otpReq.setTotQty(String.valueOf(totQty));
        otpReq.setTotDisc(String.valueOf(totDisc));
        otpReq.setIslastOrderToSale(isLastOrderToSale);
        otpReq.setShipFee(String.valueOf(shipFee));
        otpReq.setPackageFee(String.valueOf(packageFee));
        otpReq.setEraseAmt(String.valueOf(eraseAmt));
        BigDecimal totPay=new BigDecimal("0");
        BigDecimal totExtra=new BigDecimal("0");
        BigDecimal totMerReceive=new BigDecimal("0");
        BigDecimal totCustPayReal=new BigDecimal("0");
        if(getDataPayDetail!=null && getDataPayDetail.isEmpty()==false)
        {
            for (Map<String, Object> map : getDataPayDetail)
            {
                //分批订转销
                BigDecimal pay=new BigDecimal("0");
                BigDecimal orderPay=new BigDecimal("0");
                BigDecimal orderWriteOffAmt=new BigDecimal("0");
                BigDecimal changed=new BigDecimal("0");
                BigDecimal orderChanged=new BigDecimal("0");
                BigDecimal extra=new BigDecimal("0");
                BigDecimal orderExtra=new BigDecimal("0");
                BigDecimal orderExtraSale=new BigDecimal("0");
                BigDecimal merReceive=new BigDecimal("0");
                BigDecimal orderMerReceive=new BigDecimal("0");
                BigDecimal orderMerReceiveSale=new BigDecimal("0");
                BigDecimal custPayReal=new BigDecimal("0");
                BigDecimal orderCustPayReal=new BigDecimal("0");
                BigDecimal orderCustPayRealSale=new BigDecimal("0");
                BigDecimal sendPay=new BigDecimal("0");
                BigDecimal orderSendPay=new BigDecimal("0");
                BigDecimal orderSendPaySale=new BigDecimal("0");
                BigDecimal descore=new BigDecimal("0");
                BigDecimal orderDescore=new BigDecimal("0");
                BigDecimal orderDescoreSale=new BigDecimal("0");
                orderPay=new BigDecimal(Check.Null(map.get("PAY").toString()) ? "0" : map.get("PAY").toString());
                orderChanged=new BigDecimal(Check.Null(map.get("CHANGED").toString()) ? "0" : map.get("CHANGED").toString());
                orderWriteOffAmt=new BigDecimal(Check.Null(map.get("WRITEOFFAMT").toString()) ? "0" : map.get("WRITEOFFAMT").toString());
                orderMerReceive=new BigDecimal(Check.Null(map.get("MERRECEIVE").toString()) ? "0" : map.get("MERRECEIVE").toString());
                orderMerReceiveSale=new BigDecimal(Check.Null(map.get("MERRECEIVE_SALE").toString()) ? "0" : map.get("MERRECEIVE_SALE").toString());
                orderCustPayReal=new BigDecimal(Check.Null(map.get("CUSTPAYREAL").toString()) ? "0" : map.get("CUSTPAYREAL").toString());
                orderCustPayRealSale=new BigDecimal(Check.Null(map.get("CUSTPAYREAL_SALE").toString()) ? "0" : map.get("CUSTPAYREAL_SALE").toString());
                orderExtra=new BigDecimal(Check.Null(map.get("EXTRA").toString()) ? "0" : map.get("EXTRA").toString());
                orderExtraSale=new BigDecimal(Check.Null(map.get("EXTRA_SALE").toString()) ? "0" : map.get("EXTRA_SALE").toString());
                orderSendPay=new BigDecimal(Check.Null(map.get("SENDPAY").toString()) ? "0" : map.get("SENDPAY").toString());
                orderSendPaySale=new BigDecimal(Check.Null(map.get("SENDPAY_SALE").toString()) ? "0" : map.get("SENDPAY_SALE").toString());
                orderDescore=new BigDecimal(Check.Null(map.get("DESCORE").toString()) ? "0" : map.get("DESCORE").toString());
                orderDescoreSale=new BigDecimal(Check.Null(map.get("DESCORE_SALE").toString()) ? "0" : map.get("DESCORE_SALE").toString());

                if(payAmt.compareTo(totPay.subtract(totExtra))==0)
                {
                    continue;
                }

                if(orderPay.subtract(orderChanged).subtract(orderWriteOffAmt).compareTo(payAmt.subtract(totPay).add(totExtra))>0) //可转销售付款金额>=本单尚需支付金额
                {
                    pay=payAmt.subtract(totPay).add(orderExtra).subtract(orderExtraSale);
                    extra=orderExtra.subtract(orderExtraSale);
                    merReceive=pay.multiply(orderMerReceive).divide(orderPay.subtract(orderChanged),2,BigDecimal.ROUND_HALF_UP);
                    custPayReal=pay.multiply(orderCustPayReal).divide(orderPay.subtract(orderChanged),2,BigDecimal.ROUND_HALF_UP);
                    sendPay=pay.multiply(orderSendPay).divide(orderPay.subtract(orderChanged),2,BigDecimal.ROUND_HALF_UP);
                    descore=pay.multiply(orderDescore).divide(orderPay.subtract(orderChanged),2,BigDecimal.ROUND_HALF_UP);
                }else
                {
                    pay=orderPay.subtract(orderChanged).subtract(orderWriteOffAmt).add(orderExtra).subtract(orderExtraSale);
                    extra=orderExtra.subtract(orderExtraSale);
                    merReceive=orderMerReceive.subtract(orderMerReceiveSale);
                    custPayReal=orderCustPayReal.subtract(orderCustPayRealSale);
                    sendPay=orderSendPay.subtract(orderSendPaySale);
                    descore=orderDescore.subtract(orderDescoreSale);
                }
                totExtra=totExtra.add(extra);
                totPay=totPay.add(pay);
                totMerReceive=totMerReceive.add(merReceive);
                totCustPayReal=totCustPayReal.add(custPayReal);
            }
        }
        BigDecimal totDiscMerReceive=totPay.subtract(totExtra).subtract(totMerReceive).add(eraseAmt).setScale(2,BigDecimal.ROUND_HALF_UP); //支付总额-商户实收
        //double totDiscMerReceive=BigDecimalUtils.round(totAmt-totMerReceive,2); //商家优惠=应收-商家实收
        BigDecimal totThirdDiscount=totMerReceive.subtract(totCustPayReal).setScale(2,BigDecimal.ROUND_HALF_UP);//第三方优惠=商家实收-顾客实付
        otpReq.setTot_Amt_merReceive(String.valueOf(totMerReceive));
        otpReq.setTotDisc_merReceive(String.valueOf(totDiscMerReceive));
        otpReq.setTot_Amt_custPayReal(String.valueOf(totCustPayReal));
        otpReq.setTotThirdDiscount(String.valueOf(totThirdDiscount));  //从折扣中减掉商家折扣，因为后续还有加回来的逻辑
        //otpReq.setTotDisc_custPayReal(String.valueOf(BigDecimalUtils.round(totPay-totCustPayReal-totPay+totMerReceive,2)));  //从折扣中减掉商家折扣，因为后续还有加回来的逻辑
        //otpReq.setPayAmt(String.valueOf(BigDecimalUtils.round(totPay-totExtra,2)));
        otpReq.setPayAmt(String.valueOf(payAmt));
        return true;
    }


    private boolean stockTransfer(DCP_OrderToSaleProcess_OpenReq req,String eId,String shopId,String shippingShopNo,SALE sale,String saleNo,StringBuilder errorMessage,List<DataProcessBean> data) throws Exception {
        String[] columns_Stockout = {
                "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","BDATE","WAREHOUSE","DOC_TYPE",
                "OFNO","OTYPE","STATUS","MEMO","TRANSFER_SHOP","TRANSFER_WAREHOUSE",
                "TOT_CQTY","TOT_PQTY","TOT_AMT","TOT_DISTRIAMT",
                "CREATEBY","CREATE_DATE","CREATE_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                "SUBMITBY","SUBMIT_DATE","SUBMIT_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
                "PROCESS_STATUS","UPDATE_TIME","TRAN_TIME",
        };
        String[] columns_StockoutDetail = {
                "EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","WAREHOUSE","BDATE",
                "ITEM","OITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE",
                "PQTY","BASEQTY","UNIT_RATIO","PUNIT","BASEUNIT",
                "PRICE","DISTRIPRICE","AMT","DISTRIAMT",
        };
        String[] columns_Receiving = {
                "EID","ORGANIZATIONNO","SHOPID","RECEIVINGNO","BDATE","WAREHOUSE","MEMO","STATUS","COMPLETE_DATE",
                "DOC_TYPE","LOAD_DOCNO","TRANSFER_SHOP","TOT_CQTY","TOT_PQTY","TOT_AMT","TOT_DISTRIAMT",
                "CREATEBY","CREATE_DATE","CREATE_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                "CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
                "PROCESS_STATUS","UPDATE_TIME","TRAN_TIME",
        };
        String[] columns_ReceivingDetail = {
                "EID","ORGANIZATIONNO","SHOPID","RECEIVINGNO","BDATE","WAREHOUSE","OTYPE","OFNO","OITEM",
                "ITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE",
                "PQTY","BASEQTY","UNIT_RATIO","PUNIT","BASEUNIT",
                "PRICE","AMT","DISTRIPRICE","DISTRIAMT","STOCKIN_QTY","STATUS",
        };

        String[] columns_Stockin = {
                "EID","ORGANIZATIONNO","SHOPID","STOCKINNO","WAREHOUSE","BDATE","DOC_TYPE","OFNO","MEMO","STATUS",
                "LOAD_DOCNO","TRANSFER_SHOP","TRANSFER_WAREHOUSE","OTYPE","LOAD_DOCTYPE","RECEIPTDATE","PROCESS_STATUS",
                "TOT_CQTY","TOT_PQTY","TOT_AMT","TOT_DISTRIAMT",
                "CREATEBY","CREATE_DATE","CREATE_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                "ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                "UPDATE_TIME","TRAN_TIME",
        };

        String[] columns_StockinDetail = {
                "EID","ORGANIZATIONNO","SHOPID","STOCKINNO","WAREHOUSE","OFNO","OTYPE","RECEIVING_QTY","BDATE",
                "ITEM","OITEM","PLUNO","FEATURENO","BATCH_NO","PROD_DATE","PQTY","BASEQTY","PUNIT","BASEUNIT","UNIT_RATIO",
                "PRICE","AMT","DISTRIPRICE","DISTRIAMT",
        };

        try {
            
            /*String sql = " select a.warehouse,a.pluno,a.featureno,a.baseunit,a.baseqty,a.baseunit as punit,N'1' as unitratio from dcp_sale_detail a "
                    + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.saleno='" + saleNo + "' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQData)) {
                errorMessage.append("销售单号:" + saleNo + " 不存在,请重新确认! ");
                return false;
            }*/

            List<Map<String, Object>> getQData = new ArrayList<>();
            List<SALE.Detail> details = sale.getDetails();
            for (SALE.Detail detail:details){
                Map<String, Object> map = new HashMap<>();
                //【ID1038265】【大万3.0】订转销自动产生调拨个案-虚拟商品也产生了调拨数据  by jinzma 20240105 虚拟商品不调拨
                if (!"Y".equals(detail.getVirtual())){
                    map.put("PLUNO",detail.getPluNo());
                    map.put("FEATURENO",detail.getFeatureNo());
                    map.put("BASEUNIT",detail.getBaseUnit());
                    map.put("BASEQTY",detail.getBaseQty());
                    map.put("PUNIT",detail.getBaseUnit());
                    map.put("UNITRATIO","1");

                    getQData.add(map);
                }
            }

            MyCommon mc = new MyCommon();
            String opNo = req.getRequest().getOpNo();
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());

            String stockinWarehouse = details.get(0).getWarehouse();  // getQData.get(0).get("WAREHOUSE").toString();
            String stockOutWarehouse = "";

            //【ID1038265】【大万3.0】订转销自动产生调拨个案-虚拟商品也产生了调拨数据  by jinzma 20240105  虚拟商品不调拨，增加为空判断
            if (!getQData.isEmpty()) {

                //产生配送门店的调拨出库单
                {
                    ///处理金额小数位数
                    String amtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shippingShopNo, "amtLength");
                    if (Check.Null(amtLength) || !PosPub.isNumeric(amtLength)) {
                        amtLength = "2";
                    }
                    String distriAmtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shippingShopNo, "distriAmtLength");
                    if (Check.Null(distriAmtLength) || !PosPub.isNumeric(distriAmtLength)) {
                        distriAmtLength = "2";
                    }

                    //取零售价和进货价
                    String sql = " select belfirm,out_cost_warehouse from dcp_org where eid='" + eId + "' and organizationno='" + shippingShopNo + "' ";
                    List<Map<String, Object>> getCompanyId = this.doQueryData(sql, null);
                    String companyId = getCompanyId.get(0).get("BELFIRM").toString();
                    List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shippingShopNo, getQData, companyId);
                    //调拨出仓库
                    String warehouse = getCompanyId.get(0).get("OUT_COST_WAREHOUSE").toString();
                    stockOutWarehouse = warehouse;
                    //入账日期
                    String accountDate = PosPub.getAccountDate_SMS(dao, eId, shippingShopNo);
                    stockOutNo = getStockOutNo(eId, shippingShopNo, accountDate);
                    int item = 1;
                    BigDecimal totPqty = new BigDecimal("0");
                    BigDecimal totAmt = new BigDecimal("0");
                    BigDecimal totDistriamt = new BigDecimal("0");

                    for (Map<String, Object> oneData : getQData) {

                        String pluNo = oneData.get("PLUNO").toString();
                        String punit = oneData.get("BASEUNIT").toString();
                        String pqty = oneData.get("BASEQTY").toString();
                        totPqty = totPqty.add(new BigDecimal(pqty));
                        //商品取价
                        Map<String, Object> condiV = new HashMap<>();
                        condiV.put("PLUNO", pluNo);
                        condiV.put("PUNIT", punit);
                        List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                        condiV.clear();
                        String price = "0";
                        String distriPrice = "0";
                        if (priceList != null && priceList.size() > 0) {
                            price = priceList.get(0).get("PRICE").toString();
                            distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                        }
                        BigDecimal amt_b = new BigDecimal(price).multiply(new BigDecimal(pqty)).setScale(Integer.parseInt(amtLength), RoundingMode.HALF_UP);
                        BigDecimal distriAmt_b = new BigDecimal(distriPrice).multiply(new BigDecimal(pqty)).setScale(Integer.parseInt(distriAmtLength), RoundingMode.HALF_UP);

                        totAmt = totAmt.add(amt_b);
                        totDistriamt = totDistriamt.add(distriAmt_b);

                        //插入DCP_STOCKOUT_DETAIL
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shippingShopNo, Types.VARCHAR),
                                new DataValue(shippingShopNo, Types.VARCHAR),
                                new DataValue(stockOutNo, Types.VARCHAR),
                                new DataValue(warehouse, Types.VARCHAR),                            //WAREHOUSE
                                new DataValue(accountDate, Types.VARCHAR),                          //BDATE
                                new DataValue(item, Types.VARCHAR),                                 //ITEM
                                new DataValue("0", Types.VARCHAR),                            //OITEM
                                new DataValue(pluNo, Types.VARCHAR),                                //PLUNO
                                new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),  //FEATURENO
                                new DataValue("", Types.VARCHAR),                             //BATCH_NO
                                new DataValue("", Types.VARCHAR),                             //PROD_DATE
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),    //PQTY
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),    //BASEQTY
                                new DataValue("1", Types.VARCHAR),                            //UNIT_RATIO
                                new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),   //PUNIT
                                new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),   //BASEUNIT
                                new DataValue(price, Types.VARCHAR),                                //PRICE
                                new DataValue(distriPrice, Types.VARCHAR),                          //DISTRIPRICE
                                new DataValue(amt_b.toPlainString(), Types.VARCHAR),                 //AMT
                                new DataValue(distriAmt_b.toPlainString(), Types.VARCHAR),          //DISTRIAMT
                        };

                        InsBean ib = new InsBean("DCP_STOCKOUT_DETAIL", columns_StockoutDetail);
                        ib.addValues(insValue);
                        data.add(new DataProcessBean(ib));

                        //扣减库存
                        String procedure = "SP_DCP_StockChange";
                        Map<Integer, Object> inputParameterOut = new HashMap<Integer, Object>();
                        inputParameterOut.put(1, eId);                                      //--企业ID
                        inputParameterOut.put(2, shippingShopNo);                           //--组织
                        inputParameterOut.put(3, "04");                                     //--单据类型
                        inputParameterOut.put(4, stockOutNo);                               //--单据号
                        inputParameterOut.put(5, item);                                     //--单据行号
                        inputParameterOut.put(6, "-1");                                     //--异动方向 1=加库存 -1=减库存
                        inputParameterOut.put(7, accountDate);                              //--营业日期 yyyy-MM-dd
                        inputParameterOut.put(8, pluNo);                                    //--品号
                        inputParameterOut.put(9, oneData.get("FEATURENO").toString());      //--特征码
                        inputParameterOut.put(10, warehouse);                               //--仓库
                        inputParameterOut.put(11, "");                                      //--批号
                        inputParameterOut.put(12, oneData.get("BASEUNIT").toString());      //--交易单位
                        inputParameterOut.put(13, oneData.get("BASEQTY").toString());       //--交易数量
                        inputParameterOut.put(14, oneData.get("BASEUNIT").toString());      //--基准单位
                        inputParameterOut.put(15, oneData.get("BASEQTY").toString());       //--基准数量
                        inputParameterOut.put(16, "1");                                     //--换算比例
                        inputParameterOut.put(17, price);                                   //--零售价
                        inputParameterOut.put(18, amt_b.toPlainString());                   //--零售金额
                        inputParameterOut.put(19, distriPrice);                             //--进货价
                        inputParameterOut.put(20, distriAmt_b.toPlainString());             //--进货金额
                        inputParameterOut.put(21, accountDate);                             //--入账日期 yyyy-MM-dd
                        inputParameterOut.put(22, "");                                      //--批号的生产日期 yyyy-MM-dd
                        inputParameterOut.put(23, accountDate);                             //--单据日期
                        inputParameterOut.put(24, "");                                      //--异动原因
                        inputParameterOut.put(25, "");                                      //--异动描述
                        inputParameterOut.put(26, opNo);                                    //--操作员

                        ProcedureBean pdbOut = new ProcedureBean(procedure, inputParameterOut);
                        data.add(new DataProcessBean(pdbOut));

                        item++;
                    }

                    //插入DCP_STOCKOUT
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),   //EID
                            new DataValue(shippingShopNo, Types.VARCHAR),       //ORGANIZATIONNO
                            new DataValue(shippingShopNo, Types.VARCHAR),       //SHOPID
                            new DataValue(stockOutNo, Types.VARCHAR),           //STOCKOUTNO
                            new DataValue(accountDate, Types.VARCHAR),          //BDATE
                            new DataValue(warehouse, Types.VARCHAR),            //WAREHOUSE
                            new DataValue("1", Types.VARCHAR),           //DOC_TYPE
                            new DataValue(saleNo, Types.VARCHAR),               //OFNO
                            new DataValue("", Types.VARCHAR),            //OTYPE
                            new DataValue("3", Types.VARCHAR),           //STATUS
                            new DataValue("订转销(绩效算下单门店)自动产生调拨单,销售单号:" + saleNo, Types.VARCHAR),   //MEMO
                            new DataValue(shopId, Types.VARCHAR),                          //TRANSFER_SHOP
                            new DataValue(stockinWarehouse, Types.VARCHAR),               //TRANSFER_WAREHOUSE
                            new DataValue(String.valueOf(item), Types.VARCHAR),           //TOT_CQTY
                            new DataValue(totPqty.toPlainString(), Types.VARCHAR),        //TOT_PQTY
                            new DataValue(totAmt.toPlainString(), Types.VARCHAR),         //TOT_AMT
                            new DataValue(totDistriamt.toPlainString(), Types.VARCHAR),   //TOT_DISTRIAMT
                            new DataValue(opNo, Types.VARCHAR),      //CREATEBY
                            new DataValue(sDate, Types.VARCHAR),     //CREATE_DATE
                            new DataValue(sTime, Types.VARCHAR),     //CREATE_TIME
                            new DataValue(opNo, Types.VARCHAR),      //CONFIRMBY
                            new DataValue(sDate, Types.VARCHAR),     //CONFIRM_DATE
                            new DataValue(sTime, Types.VARCHAR),     //CONFIRM_TIME
                            new DataValue(opNo, Types.VARCHAR),      //SUBMITBY
                            new DataValue(sDate, Types.VARCHAR),      //SUBMIT_DATE
                            new DataValue(sTime, Types.VARCHAR),      //SUBMIT_TIME
                            new DataValue(opNo, Types.VARCHAR),        //ACCOUNTBY
                            new DataValue(accountDate, Types.VARCHAR), //ACCOUNT_DATE
                            new DataValue(sTime, Types.VARCHAR),       //ACCOUNT_TIME
                            new DataValue("N", Types.VARCHAR),   //PROCESS_STATUS
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    };

                    InsBean ib = new InsBean("DCP_STOCKOUT", columns_Stockout);
                    ib.addValues(insValue);
                    data.add(new DataProcessBean(ib));
                }

                //产生下单门店的调拨入库单和收货通知单
                {
                    ///处理金额小数位数
                    String amtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "amtLength");
                    if (Check.Null(amtLength) || !PosPub.isNumeric(amtLength)) {
                        amtLength = "2";
                    }
                    String distriAmtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "distriAmtLength");
                    if (Check.Null(distriAmtLength) || !PosPub.isNumeric(distriAmtLength)) {
                        distriAmtLength = "2";
                    }

                    //取零售价和进货价
                    String sql = " select belfirm,out_cost_warehouse from dcp_org where eid='" + eId + "' and organizationno='" + shopId + "' ";
                    List<Map<String, Object>> getCompanyId = this.doQueryData(sql, null);
                    String companyId = getCompanyId.get(0).get("BELFIRM").toString();
                    List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQData, companyId);

                    //入账日期
                    String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
                    String receivingNo = getReceivingNo(eId, shopId, accountDate);
                    stockInNo = getStockInNo(eId, shopId, accountDate);

                    int item = 1;
                    BigDecimal totPqty = new BigDecimal("0");
                    BigDecimal totAmt = new BigDecimal("0");
                    BigDecimal totDistriamt = new BigDecimal("0");

                    for (Map<String, Object> oneData : getQData) {
                        String pluNo = oneData.get("PLUNO").toString();
                        String punit = oneData.get("BASEUNIT").toString();
                        String pqty = oneData.get("BASEQTY").toString();
                        totPqty = totPqty.add(new BigDecimal(pqty));
                        //商品取价
                        Map<String, Object> condiV = new HashMap<>();
                        condiV.put("PLUNO", pluNo);
                        condiV.put("PUNIT", punit);
                        List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                        condiV.clear();
                        String price = "0";
                        String distriPrice = "0";
                        if (priceList != null && priceList.size() > 0) {
                            price = priceList.get(0).get("PRICE").toString();
                            distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                        }
                        BigDecimal amt_b = new BigDecimal(price).multiply(new BigDecimal(pqty)).setScale(Integer.parseInt(amtLength), RoundingMode.HALF_UP);
                        BigDecimal distriAmt_b = new BigDecimal(distriPrice).multiply(new BigDecimal(pqty)).setScale(Integer.parseInt(distriAmtLength), RoundingMode.HALF_UP);

                        totAmt = totAmt.add(amt_b);
                        totDistriamt = totDistriamt.add(distriAmt_b);

                        //插入DCP_RECEIVING_DETAIL
                        DataValue[] insValue1 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),  //EID
                                new DataValue(shopId, Types.VARCHAR),  //ORGANIZATIONNO
                                new DataValue(shopId, Types.VARCHAR),  //SHOPID
                                new DataValue(receivingNo, Types.VARCHAR),  //RECEIVINGNO
                                new DataValue(accountDate, Types.VARCHAR),  //BDATE
                                new DataValue(stockinWarehouse, Types.VARCHAR),  //WAREHOUSE
                                new DataValue("1", Types.VARCHAR),  //OTYPE
                                new DataValue(stockOutNo, Types.VARCHAR),  //OFNO
                                new DataValue(item, Types.VARCHAR),  //OITEM
                                new DataValue(item, Types.VARCHAR),  //ITEM
                                new DataValue(pluNo, Types.VARCHAR),  //PLUNO
                                new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),  //FEATURENO
                                new DataValue("", Types.VARCHAR),  //BATCH_NO
                                new DataValue("", Types.VARCHAR),  //PROD_DATE
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),  //PQTY
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),  //BASEQTY
                                new DataValue("1", Types.VARCHAR),  //UNIT_RATIO
                                new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),  //PUNIT
                                new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),  //BASEUNIT
                                new DataValue(price, Types.VARCHAR),  //PRICE
                                new DataValue(amt_b.toPlainString(), Types.VARCHAR),  //AMT
                                new DataValue(distriPrice, Types.VARCHAR),  //DISTRIPRICE
                                new DataValue(distriAmt_b.toPlainString(), Types.VARCHAR),  //DISTRIAMT
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),  //STOCKIN_QTY
                                new DataValue("100", Types.VARCHAR),  //STATUS
                        };
                        InsBean ib1 = new InsBean("DCP_RECEIVING_DETAIL", columns_ReceivingDetail);
                        ib1.addValues(insValue1);
                        data.add(new DataProcessBean(ib1));

                        //插入DCP_STOCKIN_DETAIL
                        DataValue[] insValue2 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),  //EID
                                new DataValue(shopId, Types.VARCHAR),  //ORGANIZATIONNO
                                new DataValue(shopId, Types.VARCHAR),  //SHOPID
                                new DataValue(stockInNo, Types.VARCHAR),  //STOCKINNO
                                new DataValue(stockinWarehouse, Types.VARCHAR),  //WAREHOUSE
                                new DataValue(receivingNo, Types.VARCHAR),  //OFNO
                                new DataValue("1", Types.VARCHAR),  //OTYPE
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),  //RECEIVING_QTY
                                new DataValue(accountDate, Types.VARCHAR),  //BDATE
                                new DataValue(item, Types.VARCHAR),  //ITEM
                                new DataValue(item, Types.VARCHAR),  //OITEM
                                new DataValue(pluNo, Types.VARCHAR),  //PLUNO
                                new DataValue(oneData.get("FEATURENO").toString(), Types.VARCHAR),  //FEATURENO
                                new DataValue("", Types.VARCHAR),  //BATCH_NO
                                new DataValue("", Types.VARCHAR),  //PROD_DATE
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),  //PQTY
                                new DataValue(oneData.get("BASEQTY").toString(), Types.VARCHAR),  //BASEQTY
                                new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),  //PUNIT
                                new DataValue(oneData.get("BASEUNIT").toString(), Types.VARCHAR),  //BASEUNIT
                                new DataValue("1", Types.VARCHAR),  //UNIT_RATIO
                                new DataValue(price, Types.VARCHAR),  //PRICE
                                new DataValue(amt_b.toPlainString(), Types.VARCHAR),  //AMT
                                new DataValue(distriPrice, Types.VARCHAR),  //DISTRIPRICE
                                new DataValue(distriAmt_b.toPlainString(), Types.VARCHAR),  //DISTRIAMT
                        };

                        InsBean ib2 = new InsBean("DCP_STOCKIN_DETAIL", columns_StockinDetail);
                        ib2.addValues(insValue2);
                        data.add(new DataProcessBean(ib2));

                        //增加库存
                        String procedure = "SP_DCP_StockChange";
                        Map<Integer, Object> inputParameterOut = new HashMap<Integer, Object>();
                        inputParameterOut.put(1, eId);                                      //--企业ID
                        inputParameterOut.put(2, shopId);                                   //--组织
                        inputParameterOut.put(3, "02");                                     //--单据类型
                        inputParameterOut.put(4, stockInNo);                                   //--单据号
                        inputParameterOut.put(5, item);                                     //--单据行号
                        inputParameterOut.put(6, "1");                                      //--异动方向 1=加库存 -1=减库存
                        inputParameterOut.put(7, accountDate);                              //--营业日期 yyyy-MM-dd
                        inputParameterOut.put(8, pluNo);                                    //--品号
                        inputParameterOut.put(9, oneData.get("FEATURENO").toString());      //--特征码
                        inputParameterOut.put(10, stockinWarehouse);                        //--仓库
                        inputParameterOut.put(11, "");                                      //--批号
                        inputParameterOut.put(12, oneData.get("BASEUNIT").toString());      //--交易单位
                        inputParameterOut.put(13, oneData.get("BASEQTY").toString());       //--交易数量
                        inputParameterOut.put(14, oneData.get("BASEUNIT").toString());      //--基准单位
                        inputParameterOut.put(15, oneData.get("BASEQTY").toString());       //--基准数量
                        inputParameterOut.put(16, "1");                                     //--换算比例
                        inputParameterOut.put(17, price);                                   //--零售价
                        inputParameterOut.put(18, amt_b.toPlainString());                   //--零售金额
                        inputParameterOut.put(19, distriPrice);                             //--进货价
                        inputParameterOut.put(20, distriAmt_b.toPlainString());             //--进货金额
                        inputParameterOut.put(21, accountDate);                             //--入账日期 yyyy-MM-dd
                        inputParameterOut.put(22, "");                                      //--批号的生产日期 yyyy-MM-dd
                        inputParameterOut.put(23, accountDate);                             //--单据日期
                        inputParameterOut.put(24, "");                                      //--异动原因
                        inputParameterOut.put(25, "");                                      //--异动描述
                        inputParameterOut.put(26, opNo);                                    //--操作员

                        ProcedureBean pdbOut = new ProcedureBean(procedure, inputParameterOut);
                        data.add(new DataProcessBean(pdbOut));

                        item++;
                    }

                    //插入DCP_RECEIVING
                    DataValue[] insValue1 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),  //EID
                            new DataValue(shopId, Types.VARCHAR),  //ORGANIZATIONNO
                            new DataValue(shopId, Types.VARCHAR),  //SHOPID
                            new DataValue(receivingNo, Types.VARCHAR),  //RECEIVINGNO
                            new DataValue(accountDate, Types.VARCHAR),  //BDATE
                            new DataValue(stockinWarehouse, Types.VARCHAR),  //WAREHOUSE
                            new DataValue("订转销(绩效算下单门店)自动产生调拨单,销售单号:" + saleNo, Types.VARCHAR),  //MEMO
                            new DataValue("7", Types.VARCHAR),  //STATUS
                            new DataValue(accountDate, Types.VARCHAR),  //COMPLETE_DATE
                            new DataValue("1", Types.VARCHAR),  //DOC_TYPE
                            new DataValue(stockOutNo, Types.VARCHAR),  //LOAD_DOCNO
                            new DataValue(shippingShopNo, Types.VARCHAR),  //TRANSFER_SHOP
                            new DataValue(String.valueOf(item), Types.VARCHAR),  //TOT_CQTY
                            new DataValue(totPqty.toPlainString(), Types.VARCHAR),  //TOT_PQTY
                            new DataValue(totAmt.toPlainString(), Types.VARCHAR),  //TOT_AMT
                            new DataValue(totDistriamt.toPlainString(), Types.VARCHAR),  //TOT_DISTRIAMT
                            new DataValue(opNo, Types.VARCHAR),  //CREATEBY
                            new DataValue(sDate, Types.VARCHAR),  //CREATE_DATE
                            new DataValue(sTime, Types.VARCHAR),  //CREATE_TIME
                            new DataValue(opNo, Types.VARCHAR),  //SUBMITBY
                            new DataValue(sDate, Types.VARCHAR),  //SUBMIT_DATE
                            new DataValue(sTime, Types.VARCHAR),  //SUBMIT_TIME
                            new DataValue(opNo, Types.VARCHAR),  //CONFIRMBY
                            new DataValue(sDate, Types.VARCHAR),  //CONFIRM_DATE
                            new DataValue(sTime, Types.VARCHAR),  //CONFIRM_TIME
                            new DataValue(opNo, Types.VARCHAR),  //ACCOUNTBY
                            new DataValue(accountDate, Types.VARCHAR),  //ACCOUNT_DATE
                            new DataValue(sTime, Types.VARCHAR),  //ACCOUNT_TIME
                            new DataValue("", Types.VARCHAR),  //PROCESS_STATUS
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    };

                    InsBean ib1 = new InsBean("DCP_RECEIVING", columns_Receiving);
                    ib1.addValues(insValue1);
                    data.add(new DataProcessBean(ib1));

                    //插入DCP_STOCKIN
                    DataValue[] insValue2 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),  //EID
                            new DataValue(shopId, Types.VARCHAR),  //ORGANIZATIONNO
                            new DataValue(shopId, Types.VARCHAR),  //SHOPID
                            new DataValue(stockInNo, Types.VARCHAR),  //STOCKINNO
                            new DataValue(stockinWarehouse, Types.VARCHAR),  //WAREHOUSE
                            new DataValue(accountDate, Types.VARCHAR),  //BDATE
                            new DataValue("1", Types.VARCHAR),  //DOC_TYPE
                            new DataValue(receivingNo, Types.VARCHAR),  //OFNO
                            new DataValue("订转销(绩效算下单门店)自动产生调拨单,销售单号:" + saleNo, Types.VARCHAR),  //MEMO
                            new DataValue("2", Types.VARCHAR),  //STATUS
                            new DataValue(stockOutNo, Types.VARCHAR),  //LOAD_DOCNO
                            new DataValue(shippingShopNo, Types.VARCHAR),  //TRANSFER_SHOP
                            new DataValue(stockOutWarehouse, Types.VARCHAR),  //TRANSFER_WAREHOUSE
                            new DataValue("1", Types.VARCHAR),  //OTYPE
                            new DataValue("", Types.VARCHAR),  //LOAD_DOCTYPE
                            new DataValue("", Types.VARCHAR),  //RECEIPTDATE
                            new DataValue("N", Types.VARCHAR),  //PROCESS_STATUS
                            new DataValue(String.valueOf(item), Types.VARCHAR),  //TOT_CQTY
                            new DataValue(totPqty.toPlainString(), Types.VARCHAR),  //TOT_PQTY
                            new DataValue(totAmt.toPlainString(), Types.VARCHAR),  //TOT_AMT
                            new DataValue(totDistriamt.toPlainString(), Types.VARCHAR),  //TOT_DISTRIAMT
                            new DataValue(opNo, Types.VARCHAR),  //CREATEBY
                            new DataValue(sDate, Types.VARCHAR),  //CREATE_DATE
                            new DataValue(sTime, Types.VARCHAR),  //CREATE_TIME
                            new DataValue(opNo, Types.VARCHAR),  //CONFIRMBY
                            new DataValue(sDate, Types.VARCHAR),  //CONFIRM_DATE
                            new DataValue(sTime, Types.VARCHAR),  //CONFIRM_TIME
                            new DataValue(opNo, Types.VARCHAR),  //ACCOUNTBY
                            new DataValue(accountDate, Types.VARCHAR),  //ACCOUNT_DATE
                            new DataValue(sTime, Types.VARCHAR),  //ACCOUNT_TIME
                            new DataValue(opNo, Types.VARCHAR),  //SUBMITBY
                            new DataValue(sDate, Types.VARCHAR),  //SUBMIT_DATE
                            new DataValue(sTime, Types.VARCHAR),  //SUBMIT_TIME
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),

                    };

                    InsBean ib2 = new InsBean("DCP_STOCKIN", columns_Stockin);
                    ib2.addValues(insValue2);
                    data.add(new DataProcessBean(ib2));
                }

            }
            //this.doExecuteDataToDB();

            return true;

        }catch (Exception e){
            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            errorMessage.append(errors);

            return false;
        }

    }

    private String getStockOutNo(String eId,String shopId,String accountDate) throws Exception {
        String stockOutNo = "DBCK" + accountDate;
        String sql = " select max(stockoutno) as stockoutno from dcp_stockout"
                + " where eid = '"+eId+"' and shopid = '"+shopId+"' and stockoutno like '%" + stockOutNo + "%' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        stockOutNo = getQData.get(0).get("STOCKOUTNO").toString();
        if (stockOutNo != null && stockOutNo.length() > 0) {
            stockOutNo = stockOutNo.substring(4);
            //【ID1038222】【大万-3.0】门店管理保存调拨单和pos订单订转销都报：For input string: \"20231228000011113311 by jinzma 20231229
            Long i = Long.parseLong(stockOutNo) + 1 ;
            stockOutNo = "DBCK" + i + "";
        } else {
            stockOutNo = "DBCK" + accountDate + "00001";
        }
        return stockOutNo;
    }
    private String getReceivingNo(String eId,String shopId,String accountDate) throws Exception {
        String receivingNo = "SHTZ"+accountDate;
        String sql = " select max(receivingno) as receivingno from dcp_receiving"
                + " where eid='"+eId +"' and shopid='"+shopId +"' and receivingno like '%"+receivingNo+"%' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        receivingNo = getQData.get(0).get("RECEIVINGNO").toString();

        if (receivingNo != null && receivingNo.length() > 0) {
            receivingNo = receivingNo.substring(4);
            //【ID1038222】【大万-3.0】门店管理保存调拨单和pos订单订转销都报：For input string: \"20231228000011113311 by jinzma 20231229
            Long i = Long.parseLong(receivingNo) + 1 ;
            receivingNo = "SHTZ" + i + "";
        } else {
            receivingNo = "SHTZ"+accountDate + "00001";
        }

        return receivingNo;
    }
    private String getStockInNo(String eId,String shopId,String accountDate) throws Exception {
        String stockInNo = "DBSH" + accountDate;
        String sql = " select max(stockinno) as stockinno from dcp_stockin "
                + " where eid = '"+eId+"' and shopid = '"+shopId+"' and stockinno like '%" + stockInNo + "%' " ;
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        stockInNo = getQData.get(0).get("STOCKINNO").toString();
        if (stockInNo != null && stockInNo.length() > 0) {
            stockInNo = stockInNo.substring(4);
            //【ID1038222】【大万-3.0】门店管理保存调拨单和pos订单订转销都报：For input string: \"20231228000011113311 by jinzma 20231229
            Long i = Long.parseLong(stockInNo) + 1 ;
            stockInNo = "DBSH" + i + "";
        } else {
            stockInNo = "DBSH" + accountDate + "00001";
        }
        return stockInNo;
    }
}
