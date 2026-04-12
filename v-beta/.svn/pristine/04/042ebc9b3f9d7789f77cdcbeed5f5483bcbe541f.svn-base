package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess2_OpenReq;
import com.dsc.spos.json.cust.req.DCP_StockOrderLockDetail_OpenReq;
import com.dsc.spos.json.cust.req.DCP_StockUnlock_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderToSaleProcess_OpenRes;
import com.dsc.spos.json.cust.res.DCP_StockOrderLockDetail_OpenRes;
import com.dsc.spos.json.cust.res.DCP_StockUnlock_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.JindieGoodsDetail;
import com.dsc.spos.model.SALE;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.thirdpart.youzan.YouZanCallBackServiceV3;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
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

/****************支持***************
 * 1.整单订转销
 * 2.部分退订后，整单订转销
 */
public class DCP_OrderToSaleProcess2_Open extends SPosAdvanceService<DCP_OrderToSaleProcess2_OpenReq, DCP_OrderToSaleProcess_OpenRes>
{
    Logger logger = LogManager.getLogger(DCP_OrderToSaleProcess2_Open.class.getName());
    
    @Override
    protected void processDUID(DCP_OrderToSaleProcess2_OpenReq req, DCP_OrderToSaleProcess_OpenRes res) throws Exception
    {
        StringBuilder errorMessage = new StringBuilder();
        boolean nRet;
		RedisPosPub rpp = new RedisPosPub();
		String checkKey="";
		checkKey="DCP_OrderToSaleProcess2_Open:"+req.getRequest().getOrderNo();
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
        }
		finally
		{
            rpp.DeleteKey(checkKey);
		}
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderToSaleProcess2_OpenReq req) throws Exception
    {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderToSaleProcess2_OpenReq req) throws Exception
    {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderToSaleProcess2_OpenReq req) throws Exception
    {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_OrderToSaleProcess2_OpenReq req) throws Exception
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
        
        if (Check.Null(req.getRequest().getOrderNo()))
        {
            errMsg.append("订单单号orderNo不可为空值, ");
            isFail = true;
        }
        
        List<DCP_OrderToSaleProcess2_OpenReq.goods> goodsList=req.getRequest().getGoodsList();
        
        if (goodsList!=null )
        {
            for (DCP_OrderToSaleProcess2_OpenReq.goods goods : goodsList)
            {
                if (Check.Null(goods.getItem()) )
                {
                    errMsg.append("商品项次item不可为空值, ");
                    isFail = true;
                }
            }
        }
        
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_OrderToSaleProcess2_OpenReq> getRequestType()
    {
        return new TypeToken<DCP_OrderToSaleProcess2_OpenReq>(){};
    }
    
    @Override
    protected DCP_OrderToSaleProcess_OpenRes getResponseType()
    {
        return new DCP_OrderToSaleProcess_OpenRes();
    }
    
    private boolean ecOrderToSale(DCP_OrderToSaleProcess2_OpenReq req,StringBuilder errorMessage,DCP_OrderToSaleProcess_OpenRes res)
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate=df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String sTime=df.format(cal.getTime());
        
        //
        df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String sDatetimeMs= df.format(cal.getTime());
        
        
        DCP_OrderToSaleProcess2_OpenReq.levelRequest otpReq=req.getRequest();
        //初始化，避免null
        if (otpReq.getGoodsList() == null) otpReq.setGoodsList(new ArrayList<>());
        if (otpReq.getPay() == null) otpReq.setPay(new ArrayList<>());
        if (otpReq.getEraseAmt() == null) otpReq.setEraseAmt("0");
        if (PosPub.isNumericTypeMinus(otpReq.getEraseAmt())==false) otpReq.setEraseAmt("0");
        
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
        boolean isNeedMemberpay=true;//是否需要会员积分调用memberpay
        
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
        try
        {
            sale = new SALE();
            String eId=otpReq.geteId();
            String orderNo=otpReq.getOrderNo();
            String LogStatus="";
            AreaType = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "AreaType");
            
            String sql_Order = " select *  from DCP_ORDER  where EID='"+eId+"' and orderno='"+orderNo+"' ";
            List<Map<String, Object>> getQData_Order = this.doQueryData(sql_Order, null);
            if (getQData_Order == null || getQData_Order.isEmpty())
            {
                errorMessage.append("订单表找不到此订单信息 !"  );
                return false;
            }
            String shopId = getQData_Order.get(0).get("SHIPPINGSHOP").toString();
            String shopName = getQData_Order.get(0).get("SHIPPINGSHOPNAME").toString();
            String partnerMember=getQData_Order.get(0).getOrDefault("PARTNERMEMBER", "digiwin").toString();
            String detailType=getQData_Order.get(0).get("DETAILTYPE").toString();//1无拆单 2 主单   3子单
            String headOrderNo=getQData_Order.get(0).get("HEADORDERNO").toString();
            String shipType=getQData_Order.get(0).get("SHIPTYPE").toString();
            String getShop=getQData_Order.get(0).get("ORDERSHOP").toString();
            String freeCode=getQData_Order.get(0).get("FREECODE").toString();
            String passport=getQData_Order.get(0).get("PASSPORT").toString();
            String loadDocType=getQData_Order.get(0).get("LOADDOCTYPE").toString();
            //String bDate=getQData_Order.get(0).get("BDATE").toString();//下订单的营业日期
            String channelId=getQData_Order.get(0).get("CHANNELID").toString();
            String belfirm=getQData_Order.get(0).get("BELFIRM").toString();
            String orderShop=getQData_Order.get(0).get("SHOP").toString();
            String totDisc=getQData_Order.get(0).get("TOT_DISC").toString();
            if (PosPub.isNumericTypeMinus(totDisc)==false)  totDisc="0";
            
            String cardNo=getQData_Order.get(0).get("CARDNO").toString();
            String contMan=getQData_Order.get(0).get("CONTMAN").toString();
            String contTel=getQData_Order.get(0).get("CONTTEL").toString();
            String customerName=getQData_Order.get(0).get("CUSTOMERNAME").toString();
            String customer=getQData_Order.get(0).get("CUSTOMER").toString();
            String shipFee=getQData_Order.get(0).get("SHIPFEE").toString();
            if (PosPub.isNumericTypeMinus(shipFee)==false)  shipFee="0";
            
            String shopshareshipfee=getQData_Order.get(0).get("SHOPSHARESHIPFEE").toString();
            if (PosPub.isNumericTypeMinus(shopshareshipfee)==false)  shopshareshipfee="0";
            
            String delName=getQData_Order.get(0).get("DELNAME").toString();
            String mealNumber=getQData_Order.get(0).get("MEALNUMBER").toString();
            String refundReasonNo=getQData_Order.get(0).get("REFUNDREASONNO").toString();
            String refundReason=getQData_Order.get(0).get("REFUNDREASON").toString();
            String refundReasonName=getQData_Order.get(0).get("REFUNDREASONNAME").toString();
            String refundAMT=getQData_Order.get(0).get("REFUNDAMT").toString();//已退金额
            if (PosPub.isNumericTypeMinus(refundAMT)==false)  refundAMT="0";
            String writeoffAMT=getQData_Order.get(0).get("WRITEOFFAMT").toString();//已提货金额
            if (PosPub.isNumericTypeMinus(writeoffAMT)==false)  writeoffAMT="0";
            String sellNo=getQData_Order.get(0).get("SELLNO").toString();//大客户编号
            String shipDate=getQData_Order.get(0).get("SHIPDATE").toString();
            String shipStartTime=getQData_Order.get(0).get("SHIPSTARTTIME").toString();
            String getMan=getQData_Order.get(0).get("GETMAN").toString();
            String getManTel=getQData_Order.get(0).get("GETMANTEL").toString();
            String manualNo=getQData_Order.get(0).get("MANUALNO").toString();
            String machine=getQData_Order.get(0).get("MACHINE").toString();
            String memberId=getQData_Order.get(0).get("MEMBERID").toString();
            String memberName=getQData_Order.get(0).get("MEMBERNAME").toString();
            String memo=getQData_Order.get(0).get("MEMO").toString();
            String tot_shipFee=getQData_Order.get(0).get("TOTSHIPFEE").toString();
            if (PosPub.isNumericTypeMinus(tot_shipFee)==false)  tot_shipFee=shipFee;
            
            String packageFee=getQData_Order.get(0).get("PACKAGEFEE").toString();
            String payAmt=getQData_Order.get(0).get("PAYAMT").toString();
            String platformDisc=getQData_Order.get(0).get("PLATFORM_DISC").toString();
            String sellerDisc=getQData_Order.get(0).get("SELLER_DISC").toString();
            if (PosPub.isNumericTypeMinus(sellerDisc)==false)  sellerDisc="0";
            
            String serviceCharge=getQData_Order.get(0).get("SERVICECHARGE").toString();
            String pointQty=getQData_Order.get(0).get("POINTQTY").toString();
            String sellCredit=getQData_Order.get(0).get("SELLCREDIT").toString();
            String delMemo=getQData_Order.get(0).get("DELMEMO").toString();
            String address=getQData_Order.get(0).get("ADDRESS").toString();
            String incomeAmt=getQData_Order.get(0).get("INCOMEAMT").toString();
            if (PosPub.isNumericTypeMinus(incomeAmt)==false)  incomeAmt="0";
            
            String totQty=getQData_Order.get(0).get("TOT_QTY").toString();
            String tot_Amt=getQData_Order.get(0).get("TOT_AMT").toString();
            if (PosPub.isNumericTypeMinus(tot_Amt)==false)  tot_Amt="0";
            String tot_oldAmt=getQData_Order.get(0).get("TOT_OLDAMT").toString();
            if (PosPub.isNumericTypeMinus(tot_oldAmt)==false)  tot_oldAmt="0";
            String verNum=getQData_Order.get(0).get("VER_NUM").toString();
            //0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
            String status=getQData_Order.get(0).get("STATUS").toString();
            String refundStatus=getQData_Order.get(0).get("REFUNDSTATUS").toString();
            String deliveryHandinput=getQData_Order.get(0).get("DELIVERYHANDINPUT").toString();
            String deliveryType=getQData_Order.get(0).get("DELIVERYTYPE").toString();
            String deliveryNo=getQData_Order.get(0).get("DELIVERYNO").toString();
            String subDeliveryCompanyNo=getQData_Order.get(0).get("SUBDELIVERYCOMPANYNO").toString();
            String subDeliveryCompanyName=getQData_Order.get(0).get("SUBDELIVERYCOMPANYNAME").toString();
            String paystatus=getQData_Order.get(0).get("PAYSTATUS").toString();// 1.未支付 2.部分支付 3.付清
            String waimaiMerreceiveMode=getQData_Order.get(0).get("WAIMAIMERRECEIVEMODE").toString();
            
            //isMerPay配送费是否商家结算
            String isMerPay=getQData_Order.get(0).get("ISMERPAY").toString();
            if (Check.Null(isMerPay)) isMerPay="N";
            
            //即使传入了会员卡号爷不需要调用会员加分接口
            if(orderLoadDocType.QIMAI.equals(loadDocType)||orderLoadDocType.XIAOYOU.equals(loadDocType)||"WEIPINKE".equals(loadDocType)){
                isNeedMemberpay=false;
            }
            
            //取下数据库里面的，
            String departNo_DB = getQData_Order.get(0).getOrDefault("DEPARTNO", "").toString();
            String departNo = otpReq.getDepartNo();
            if(departNo==null||departNo.isEmpty())
            {
                departNo = departNo_DB;
            }
            String order_sn=getQData_Order.get(0).get("ORDER_SN").toString();//外卖流水
            
            
            //内部订转销必须全款
            if (req.getApiUser() == null)
            {
                if (paystatus.equals("3")==false)
                {
                    errorMessage.append("此订单款未付清，不能操作!"  );
                    return false;
                }
            }
            
            //前端必须给值
            if (Check.Null(otpReq.getOpShopId()))
            {
                errorMessage.append("操作门店opShopId不能为空 !"  );
                return false;
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
            //【ID1034999】【嘉华3.0】团购打折功能-dcp服务  by jinzma 20230804
            String groupBuying=getQData_Order.get(0).getOrDefault("GROUPBUYING","N").toString();
            
            //获取默认出货仓
            String outCostWareHouse = getOutCostWareHouse(eId,shopId) ;
            if (Check.Null(outCostWareHouse))
            {
                errorMessage.append("企业编号：" + eId + " 门店编号："+shopId + "，组织未生效或组织默认出货仓DCP_ORG.OUT_COST_WAREHOUSE未设置 !"  );
                return false;
            }
            String sd_warehouse = outCostWareHouse;
            
            saleno="TV"+orderNo;
            
            //
            if (getExistSaleno(eId, shopId, saleno))
            {
                errorMessage.append("企业编号：" + eId + " 门店编号："+shopId + "的销售单已经存在 !"  );
                return false;
            }
            //取入账日期-销售单
            String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
            //
            if (Check.Null(otpReq.getOpBDate())) otpReq.setOpBDate(accountDate);
            
            //不能小于入账日期，这个是订转销给销售单的营业日期
            if (otpReq.getOpBDate().compareTo(accountDate)<0)
            {
                errorMessage.append("营业日期bdate("+otpReq.getOpBDate()+")不能小于库存入账日期("+accountDate+")！");
                return false;
            }
            if (Check.Null(otpReq.getOpMachineNo()))otpReq.setOpMachineNo("999");
            
            //取trno
            String trno=getTRNO(eId, shopId, otpReq.getOpBDate(), saleno,req);
            if (Check.Null(trno))
            {
                errorMessage.append("计算销售单最大流水号TRNO报错 !"  );
                return false;
            }
            
            //拆单的主单不能直接订转销，1无拆单 2 主单   3子单
            if (detailType.equals("2"))
            {
                errorMessage.append("订单拆单后,只能通过子单订转销 !"  );
                return false;
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
            
            //发票试算的定义
            JSONArray datas_goodsList = new JSONArray();
            JSONArray datas_payList = new JSONArray();
            
            //当前单据是子单
            boolean b_subOrderFlag=detailType.equals("3") && headOrderNo.equals("")==false ?true:false;
            
            //商品
            String sql_Order_detail = " select *  from DCP_ORDER_DETAIL  where EID='"+eId+"' and orderno='"+orderNo+"' ";
            List<Map<String, Object>> getQData_Order_detail = this.doQueryData(sql_Order_detail, null);
            if (getQData_Order_detail == null || getQData_Order_detail.isEmpty())
            {
                errorMessage.append("订单明细商品表找不到此订单信息 !"  );
                return false;
            }
            //商品折扣
            String sql_Order_detail_agio = " select *  from DCP_ORDER_DETAIL_AGIO  where EID='"+eId+"' and orderno='"+orderNo+"' ";
            List<Map<String, Object>> getQData_Order_detail_agio = this.doQueryData(sql_Order_detail_agio, null);
            
            //退订单号
            String tempRefundOrderNo="";
            //1.查退单，目的是要根据退单商品金额为依据，只能退1次
            String sql_Order_refund = " select *  from DCP_ORDER  where EID='"+eId+"' and REFUNDSOURCEBILLNO='"+orderNo+"' ";
            List<Map<String, Object>> getQData_Order_refund = this.doQueryData(sql_Order_refund, null);
            //初始化
            List<Map<String, Object>> getQData_refundOrder_detail=new ArrayList<>();
            if (getQData_Order_refund != null && getQData_Order_refund.isEmpty()==false)
            {
                tempRefundOrderNo=getQData_Order_refund.get(0).get("ORDERNO").toString();
                //查退单商品
                String sql_refundOrder_detail = " select *  from DCP_ORDER_DETAIL  where EID='"+eId+"' and orderno='"+tempRefundOrderNo+"' ";
                getQData_refundOrder_detail = this.doQueryData(sql_refundOrder_detail, null);
                if (getQData_refundOrder_detail == null || getQData_refundOrder_detail.isEmpty())
                {
                    errorMessage.append("订单已退，找不到此订单的退单商品信息 !");
                    return false;
                }
            }
            
            //记录一下部分退商品的差额折扣,比如原价2元，退客户1.25，差额就是0.75要在原来的基础减掉
            BigDecimal bdm_tot_disc_part=new BigDecimal("0");
            //销售的原价总额=原订单oldamt-bdm_tot_oldamt_part
            BigDecimal bdm_tot_oldamt_part=new BigDecimal("0");
            //销售的成交总额=原订单amt-bdm_tot_amt_part
            BigDecimal bdm_tot_amt_part=new BigDecimal("0");
            
            //前端传商品，以前端传值item取数据库剩余字段,项次和数量以前端为准，不用赋值
            if (otpReq.getGoodsList().size()>0)
            {
                for (DCP_OrderToSaleProcess2_OpenReq.goods tempGoods : otpReq.getGoodsList())
                {
                    Map<String, Object> condM=new HashMap<>();
                    condM.put("ITEM",tempGoods.getItem());
                    List<Map<String, Object>> tempMapDetail=MapDistinct.getWhereMap(getQData_Order_detail,condM,false);
                    if (tempMapDetail==null || tempMapDetail.size() == 0)
                    {
                        errorMessage.append("找不到项次item="+tempGoods.getItem()+"的订单商品记录!"  );
                        return false;
                    }
                    Map<String, Object> mapDetail=tempMapDetail.get(0);
                    
                    //1.处理退的商品数量
                    String sRQty="0";//退货数量(取退单的)
                    String sRAMT="0";//退货金额(取退单的)
                    Map<String, Object> condRefund=new HashMap<>();
                    //用原OITEM关联
                    condRefund.put("OITEM",mapDetail.get("ITEM").toString());
                    List<Map<String, Object>> tempMapRefund=MapDistinct.getWhereMap(getQData_refundOrder_detail,condRefund,false);
                    if (tempMapRefund != null && tempMapRefund.size()>0)
                    {
                        sRQty=tempMapRefund.get(0).get("QTY").toString();
                        sRAMT=tempMapRefund.get(0).get("AMT").toString();
                    }
                    if (PosPub.isNumericTypeMinus(sRQty)==false) sRQty="0";
                    if (PosPub.isNumericTypeMinus(sRAMT)==false) sRAMT="0";
                    
                    //订单信息
                    String sQty=mapDetail.get("QTY").toString();//购买数量
                    if (PosPub.isNumericTypeMinus(sQty)==false) sQty="1";
                    
                    String sAMT=mapDetail.get("AMT").toString();//购买金额
                    if (PosPub.isNumericTypeMinus(sAMT)==false) sAMT="0";
                    
                    String sPickQty=mapDetail.get("PICKQTY").toString();//已提货数量
                    if (PosPub.isNumericTypeMinus(sPickQty)==false) sPickQty="0";
                    BigDecimal bdmPickQty=new BigDecimal(sPickQty);
                    
                    //已提货金额占比计算：已提货数量*amt/购买数量
                    BigDecimal bdmPickAMT=bdmPickQty.multiply(new BigDecimal(sAMT)).divide(new BigDecimal(sQty),2,BigDecimal.ROUND_HALF_UP);
                    
                    
                    //商品剩余量=购买数量-提货数量-退货数量
                    BigDecimal bdmRemainQty=new BigDecimal(sQty).subtract(bdmPickQty).subtract(new BigDecimal(sRQty));
                    
                    //剩余金额=购买金额-提货金额-已退金额
                    BigDecimal bdmRemainAMT=new BigDecimal(sAMT).subtract(bdmPickAMT).subtract(new BigDecimal(sRAMT));
                    
                    if (bdmRemainQty.compareTo(tempGoods.getQty())<0)
                    {
                        errorMessage.append("订转销数量不能大于商品剩余数量，项次item="+tempGoods.getItem()+"的商品剩余量为："+bdmRemainQty+",请求传入值为：" +tempGoods.getQty() +""  );
                        return false;
                    }
                    
                    tempGoods.setPluNo(mapDetail.get("PLUNO").toString());
                    tempGoods.setPluName(mapDetail.get("PLUNAME").toString());
                    tempGoods.setPluBarcode(mapDetail.get("PLUBARCODE").toString());
                    tempGoods.setFeatureNo(mapDetail.get("FEATURENO").toString());
                    tempGoods.setFeatureName(mapDetail.get("FEATURENAME").toString());
                    tempGoods.setSpecName(mapDetail.get("SPECNAME").toString());
                    tempGoods.setsUnitName(mapDetail.get("SUNITNAME").toString());
                    tempGoods.setsUnit(mapDetail.get("SUNIT").toString());
                    tempGoods.setWarehouseName(mapDetail.get("WAREHOUSENAME").toString());
                    tempGoods.setWarehouse(mapDetail.get("WAREHOUSE").toString());
                    tempGoods.setGiftReason(mapDetail.get("GIFTREASON").toString());
                    tempGoods.setGift(mapDetail.get("GIFT").toString());
                    tempGoods.setPackageType(mapDetail.get("PACKAGETYPE").toString());
                    tempGoods.setPackageMitem(mapDetail.get("PACKAGEMITEM").toString());
                    tempGoods.setToppingMitem(mapDetail.get("TOPPINGMITEM").toString());
                    tempGoods.setToppingType(mapDetail.get("TOPPINGTYPE").toString());
                    tempGoods.setPickQty(mapDetail.get("PICKQTY").toString());//已提货数量
                    tempGoods.setrQty(mapDetail.get("RQTY").toString());//退货数量
                    
                    tempGoods.setRcQty(mapDetail.get("RCQTY").toString());
                    tempGoods.setUnPickQty(mapDetail.get("RUNPICKQTY").toString());
                    tempGoods.setDisc_custPayReal(mapDetail.get("DISC_CUSTPAYREAL").toString());
                    tempGoods.setDisc_merReceive(mapDetail.get("DISC_MERRECEIVE").toString());
                    tempGoods.setAmt_custPayReal(mapDetail.get("AMT_CUSTPAYREAL").toString());
                    tempGoods.setAmt_merReceive(mapDetail.get("AMT_MERRECEIVE").toString());
                    tempGoods.setBoxNum(mapDetail.get("BOXNUM").toString());
                    tempGoods.setBoxPrice(mapDetail.get("BOXPRICE").toString());
                    tempGoods.setPrice(mapDetail.get("PRICE").toString());
                    tempGoods.setOldPrice(mapDetail.get("OLDPRICE").toString());
                    
                    //重新计算
                    //原价金额
                    tempGoods.setOldAmt(tempGoods.getQty().multiply(new BigDecimal(tempGoods.getOldPrice())).toPlainString());
                    //用剩余金额占比计算
                    tempGoods.setAmt(tempGoods.getQty().multiply(bdmRemainAMT).divide(bdmRemainQty,2,BigDecimal.ROUND_HALF_UP).toPlainString());
                    tempGoods.setDisc(new BigDecimal(tempGoods.getOldAmt()).subtract(new BigDecimal(tempGoods.getAmt())).toPlainString());
                    
                    tempGoods.setInclTax(mapDetail.get("INCLTAX").toString());
                    tempGoods.setTaxCode(mapDetail.get("TAXCODE").toString());
                    String sTaxrate=mapDetail.get("TAXRATE").toString();
                    if (PosPub.isNumericTypeMinus(sTaxrate)==false)
                    {
                        sTaxrate="0";
                    }
                    tempGoods.setTaxRate(new BigDecimal(sTaxrate));
                    tempGoods.setTaxType(mapDetail.get("TAXTYPE").toString());
                    tempGoods.setInvItem(mapDetail.get("INVITEM").toString());
                    tempGoods.setInvNo(mapDetail.get("INVNO").toString());
                    tempGoods.setInvSplitType(mapDetail.get("INVSPLITTYPE").toString());
                    tempGoods.setSellerNo(mapDetail.get("SELLERNO").toString());
                    tempGoods.setSellerName(mapDetail.get("SELLERNAME").toString());
                    tempGoods.setVirtual(mapDetail.get("VIRTUAL").toString());
                    tempGoods.setAccNo(mapDetail.get("ACCNO").toString());
                    tempGoods.setCounterNo(mapDetail.get("COUNTERNO").toString());
                    tempGoods.setCouponCode(mapDetail.get("COUPONCODE").toString());
                    tempGoods.setCouponType(mapDetail.get("COUPONTYPE").toString());
                    tempGoods.setAttrName(mapDetail.get("ATTRNAME").toString());
                    tempGoods.setFlavorStuffDetail(mapDetail.getOrDefault("FLAVORSTUFFDETAIL","").toString());
                    //折扣
                    tempGoods.setAgioInfo(new ArrayList<>());
                    
                    //取当前商品折扣
                    Map<String, Object> condV=new HashMap<>();
                    condV.put("MITEM",tempGoods.getItem());
                    List<Map<String, Object>> tempMapAgio=MapDistinct.getWhereMap(getQData_Order_detail_agio,condV,true);
                    for (Map<String, Object> mapDetailAgio : tempMapAgio)
                    {
                        DCP_OrderToSaleProcess2_OpenReq.Agio tempAgio=new DCP_OrderToSaleProcess2_OpenReq().new Agio();
                        tempAgio.setItem(mapDetailAgio.get("ITEM").toString());
                        tempAgio.setDcType(mapDetailAgio.get("DCTYPE").toString());
                        //重新计算:按订转销数量比重
                        String o_QTY=mapDetailAgio.get("QTY").toString();
                        String o_DISC=mapDetailAgio.get("DISC").toString();
                        String o_AMT=mapDetailAgio.get("AMT").toString();
                        
                        if (PosPub.isNumericTypeMinus(o_QTY) == false) o_QTY="0";
                        if (PosPub.isNumericTypeMinus(o_DISC) == false) o_DISC="0";
                        if (PosPub.isNumericTypeMinus(o_AMT) == false) o_AMT="0";
                        
                        //
                        tempAgio.setQty(tempGoods.getQty().toPlainString());
                        tempAgio.setDisc(tempGoods.getQty().multiply(new BigDecimal(o_DISC)).divide(new BigDecimal(o_QTY),2,BigDecimal.ROUND_HALF_UP).toPlainString());
                        tempAgio.setAmt(tempGoods.getQty().multiply(new BigDecimal(o_AMT)).divide(new BigDecimal(o_QTY),2,BigDecimal.ROUND_HALF_UP).toPlainString());
                        
                        tempAgio.setInputDisc(mapDetailAgio.get("INPUTDISC").toString());
                        tempAgio.setRealDisc(mapDetailAgio.get("REALDISC").toString());
                        tempAgio.setDisc_custPayReal(mapDetailAgio.get("DISC_CUSTPAYREAL").toString());
                        tempAgio.setDisc_merReceive(mapDetailAgio.get("DISC_MERRECEIVE").toString());
                        tempAgio.setDcTypeName(mapDetailAgio.get("DCTYPENAME").toString());
                        tempAgio.setBsNo(mapDetailAgio.get("BSNO").toString());
                        tempAgio.setGiftCtf(mapDetailAgio.get("GIFTCTF").toString());
                        tempAgio.setGiftCtfNo(mapDetailAgio.get("GIFTCTFNO").toString());
                        tempAgio.setPmtNo(mapDetailAgio.get("PMTNO").toString());
                        tempAgio.setOrderToSalePayAgioFlag("N");
                        tempGoods.getAgioInfo().add(tempAgio);
                    }
                    
                }
            }
            else
            {
                //2.处理剩余
                for (Map<String, Object> mapDetail : getQData_Order_detail)
                {
                    //1.处理退的商品数量
                    String sRQty="0";//退货数量(取退单的)
                    String sRAMT="0";//退货金额(取退单的)
                    Map<String, Object> condRefund=new HashMap<>();
                    condRefund.put("OITEM",mapDetail.get("ITEM").toString());
                    List<Map<String, Object>> tempMapRefund=MapDistinct.getWhereMap(getQData_refundOrder_detail,condRefund,false);
                    if (tempMapRefund != null && tempMapRefund.size()>0)
                    {
                        sRQty=tempMapRefund.get(0).get("QTY").toString();
                        sRAMT=tempMapRefund.get(0).get("AMT").toString();
                    }
                    if (PosPub.isNumericTypeMinus(sRQty)==false) sRQty="0";
                    if (PosPub.isNumericTypeMinus(sRAMT)==false) sRAMT="0";
                    
                    //订单信息
                    String sQty=mapDetail.get("QTY").toString();//购买数量
                    if (PosPub.isNumericTypeMinus(sQty)==false) sQty="1";
                    
                    String sOldAMT=mapDetail.get("OLDAMT").toString();//购买原价金额
                    if (PosPub.isNumericTypeMinus(sOldAMT)==false) sOldAMT="0";
                    
                    String sAMT=mapDetail.get("AMT").toString();//购买金额
                    if (PosPub.isNumericTypeMinus(sAMT)==false) sAMT="0";
                    
                    String sPickQty=mapDetail.get("PICKQTY").toString();//已提货数量
                    if (PosPub.isNumericTypeMinus(sPickQty)==false) sPickQty="0";
                    BigDecimal bdmPickQty=new BigDecimal(sPickQty);
                    
                    //已提货金额占比计算：已提货数量*amt/购买数量
                    BigDecimal bdmPickAMT=bdmPickQty.multiply(new BigDecimal(sAMT)).divide(new BigDecimal(sQty),2,BigDecimal.ROUND_HALF_UP);
                    
                    
                    //商品剩余量=购买数量-提货数量-退货数量
                    BigDecimal bdmRemainQty=new BigDecimal(sQty).subtract(bdmPickQty).subtract(new BigDecimal(sRQty));
                    
                    //剩余金额=购买金额-提货金额-已退金额
                    //40块1个，买3个，退2个
                    BigDecimal bdmRemainAMT=new BigDecimal(sAMT).multiply(new BigDecimal(sRQty)).divide(new BigDecimal(sQty),2,BigDecimal.ROUND_HALF_UP).subtract(bdmPickAMT).subtract(new BigDecimal(sRAMT)).setScale(2,BigDecimal.ROUND_HALF_UP);
                    
                    //这笔是部分退商品
                    if (tempMapRefund != null && tempMapRefund.size()>0)
                    {
                        //此商品购买数量部分退，
                        //比如40*2=80  退1个给客户24.79元，15.21元作为折扣，
                        //比如40*3=120  退2个给客户50元，30元作为折扣，
                        //比如40*4=160  退2个给客户70元，10元作为折扣，
                        //剩余那个商品的AMT还正常记录40
                        bdm_tot_disc_part=bdm_tot_disc_part.add(bdmRemainAMT);
                        bdm_tot_oldamt_part=bdm_tot_oldamt_part.add(new BigDecimal(sOldAMT).multiply(new BigDecimal(sRQty)).divide(new BigDecimal(sQty),2,BigDecimal.ROUND_HALF_UP));
                        bdm_tot_amt_part=bdm_tot_amt_part.add(new BigDecimal(sRAMT));
                    }
                    
                    if (bdmRemainQty.compareTo(BigDecimal.ZERO)>0)
                    {
                        DCP_OrderToSaleProcess2_OpenReq.goods tempGoods=new DCP_OrderToSaleProcess2_OpenReq().new goods();
                        tempGoods.setItem(mapDetail.get("ITEM").toString());
                        tempGoods.setPluNo(mapDetail.get("PLUNO").toString());
                        tempGoods.setPluName(mapDetail.get("PLUNAME").toString());
                        tempGoods.setPluBarcode(mapDetail.get("PLUBARCODE").toString());
                        tempGoods.setFeatureNo(mapDetail.get("FEATURENO").toString());
                        tempGoods.setFeatureName(mapDetail.get("FEATURENAME").toString());
                        tempGoods.setSpecName(mapDetail.get("SPECNAME").toString());
                        tempGoods.setsUnitName(mapDetail.get("SUNITNAME").toString());
                        tempGoods.setsUnit(mapDetail.get("SUNIT").toString());
                        tempGoods.setWarehouseName(mapDetail.get("WAREHOUSENAME").toString());
                        tempGoods.setWarehouse(mapDetail.get("WAREHOUSE").toString());
                        tempGoods.setGiftReason(mapDetail.get("GIFTREASON").toString());
                        tempGoods.setGift(mapDetail.get("GIFT").toString());
                        tempGoods.setPackageType(mapDetail.get("PACKAGETYPE").toString());
                        tempGoods.setPackageMitem(mapDetail.get("PACKAGEMITEM").toString());
                        tempGoods.setToppingMitem(mapDetail.get("TOPPINGMITEM").toString());
                        tempGoods.setToppingType(mapDetail.get("TOPPINGTYPE").toString());
                        tempGoods.setPickQty(bdmPickQty.toPlainString());//提货数量
                        tempGoods.setrQty(sRQty);//退货数量
                        tempGoods.setRcQty(mapDetail.get("RCQTY").toString());
                        tempGoods.setUnPickQty(mapDetail.get("RUNPICKQTY").toString());
                        tempGoods.setDisc_custPayReal(mapDetail.get("DISC_CUSTPAYREAL").toString());
                        tempGoods.setDisc_merReceive(mapDetail.get("DISC_MERRECEIVE").toString());
                        tempGoods.setAmt_custPayReal(mapDetail.get("AMT_CUSTPAYREAL").toString());
                        tempGoods.setAmt_merReceive(mapDetail.get("AMT_MERRECEIVE").toString());
                        tempGoods.setBoxNum(mapDetail.get("BOXNUM").toString());
                        tempGoods.setBoxPrice(mapDetail.get("BOXPRICE").toString());
                        tempGoods.setQty(bdmRemainQty);//剩余数量
                        tempGoods.setPrice(mapDetail.get("PRICE").toString());
                        tempGoods.setOldPrice(mapDetail.get("OLDPRICE").toString());
                        
                        //原价金额
                        tempGoods.setOldAmt(bdmRemainQty.multiply(new BigDecimal(tempGoods.getOldPrice())).toPlainString());
                        
                        //剩余按照单个成商品成交额算，等于不要那个退掉数量啦
                        BigDecimal bdmTempAMT=new BigDecimal(sAMT).multiply(bdmRemainQty).divide(new BigDecimal(sQty),3,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP);
                        tempGoods.setAmt(bdmTempAMT.toPlainString());
                        tempGoods.setDisc(new BigDecimal(tempGoods.getOldAmt()).subtract(new BigDecimal(tempGoods.getAmt())).toPlainString());
                        
                        tempGoods.setInclTax(mapDetail.get("INCLTAX").toString());
                        tempGoods.setTaxCode(mapDetail.get("TAXCODE").toString());
                        String sTaxrate=mapDetail.get("TAXRATE").toString();
                        if (PosPub.isNumericTypeMinus(sTaxrate)==false)
                        {
                            sTaxrate="0";
                        }
                        tempGoods.setTaxRate(new BigDecimal(sTaxrate));
                        tempGoods.setTaxType(mapDetail.get("TAXTYPE").toString());
                        tempGoods.setInvItem(mapDetail.get("INVITEM").toString());
                        tempGoods.setInvNo(mapDetail.get("INVNO").toString());
                        tempGoods.setInvSplitType(mapDetail.get("INVSPLITTYPE").toString());
                        tempGoods.setSellerNo(mapDetail.get("SELLERNO").toString());
                        tempGoods.setSellerName(mapDetail.get("SELLERNAME").toString());
                        tempGoods.setVirtual(mapDetail.get("VIRTUAL").toString());
                        tempGoods.setAccNo(mapDetail.get("ACCNO").toString());
                        tempGoods.setCounterNo(mapDetail.get("COUNTERNO").toString());
                        tempGoods.setCouponCode(mapDetail.get("COUPONCODE").toString());
                        tempGoods.setCouponType(mapDetail.get("COUPONTYPE").toString());
                        tempGoods.setAttrName(mapDetail.get("ATTRNAME").toString());
                        tempGoods.setFlavorStuffDetail(mapDetail.getOrDefault("FLAVORSTUFFDETAIL","").toString());
                        //折扣
                        tempGoods.setAgioInfo(new ArrayList<>());
                        
                        //取当前商品折扣
                        Map<String, Object> condV=new HashMap<>();
                        condV.put("MITEM",tempGoods.getItem());
                        List<Map<String, Object>> tempMapAgio=MapDistinct.getWhereMap(getQData_Order_detail_agio,condV,true);
                        for (Map<String, Object> mapDetailAgio : tempMapAgio)
                        {
                            DCP_OrderToSaleProcess2_OpenReq.Agio tempAgio=new DCP_OrderToSaleProcess2_OpenReq().new Agio();
                            tempAgio.setItem(mapDetailAgio.get("ITEM").toString());
                            tempAgio.setDcType(mapDetailAgio.get("DCTYPE").toString());
                            
                            //重新计算:按订转销数量比重
                            String o_QTY=mapDetailAgio.get("QTY").toString();
                            String o_DISC=mapDetailAgio.get("DISC").toString();
                            String o_AMT=mapDetailAgio.get("AMT").toString();
                            
                            if (PosPub.isNumericTypeMinus(o_QTY) == false) o_QTY="0";
                            if (PosPub.isNumericTypeMinus(o_DISC) == false) o_DISC="0";
                            if (PosPub.isNumericTypeMinus(o_AMT) == false) o_AMT="0";
                            
                            //
                            tempAgio.setQty(tempGoods.getQty().toPlainString());
                            tempAgio.setDisc(tempGoods.getQty().multiply(new BigDecimal(o_DISC)).divide(new BigDecimal(o_QTY),2,BigDecimal.ROUND_HALF_UP).toPlainString());
                            tempAgio.setAmt(tempGoods.getQty().multiply(new BigDecimal(o_AMT)).divide(new BigDecimal(o_QTY),2,BigDecimal.ROUND_HALF_UP).toPlainString());
                            
                            
                            tempAgio.setInputDisc(mapDetailAgio.get("INPUTDISC").toString());
                            tempAgio.setRealDisc(mapDetailAgio.get("REALDISC").toString());
                            tempAgio.setDisc_custPayReal(mapDetailAgio.get("DISC_CUSTPAYREAL").toString());
                            tempAgio.setDisc_merReceive(mapDetailAgio.get("DISC_MERRECEIVE").toString());
                            tempAgio.setDcTypeName(mapDetailAgio.get("DCTYPENAME").toString());
                            tempAgio.setBsNo(mapDetailAgio.get("BSNO").toString());
                            tempAgio.setGiftCtf(mapDetailAgio.get("GIFTCTF").toString());
                            tempAgio.setGiftCtfNo(mapDetailAgio.get("GIFTCTFNO").toString());
                            tempAgio.setPmtNo(mapDetailAgio.get("PMTNO").toString());
                            tempAgio.setOrderToSalePayAgioFlag("N");
                            tempGoods.getAgioInfo().add(tempAgio);
                        }
                        otpReq.getGoodsList().add(tempGoods);
                    }
                }
            }
            
            sale.setIsMerPay(isMerPay);
            sale.setEraseAmt(otpReq.getEraseAmt());
            sale.setApproval("");//钉钉审批id
            sale.setAuthorizerOpno("");//授权人编码
            sale.setGetMode(shipType);//配送方式
            sale.setGetShop(shopId);//
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
            sale.setFreeCode(freeCode);//零税证号-只有外交官才有的凭证--零税的其中一种凭证
            sale.setPassport(passport);//护照编号-零税的其中一种凭证
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
            boolean istakeout=loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM);
            sale.setIsTakeout(istakeout?"Y":"N");//是否外卖订单
            sale.setWxOpenId("");//第三方用户id
            sale.setWmExtraFee("0");//外卖：平台其他费用(每单捐赠1分钱支持环保之类的活动)
            sale.setWmUserPaid("0");//外卖：用户实际支付
            sale.setDeliveryFeeShop("0");//外卖：门店承担配送费
            sale.setOrderSn(order_sn);//外卖订单流水号
            //
            sale.setIsUploaded("N");//是否已传第三方（例如商场）Y已上传，N未上传
            sale.setLegalper("");//法人
            sale.setIsReturn("N");//是否已退Y/N
            sale.setOrderAmount("0");//订金金额
            sale.setOrder_appType("");//下单应用类型
            sale.setoBdate(otpReq.getOpBDate());//原单营业日期
            sale.setoMachine(otpReq.getOpMachineNo());//原单机台号
            sale.setoOrder_appType(loadDocType);//来源应用类型
            sale.setoOrder_channelId(channelId);//来源渠道编码
            sale.setoOrder_companyId(belfirm);//来源公司编码
            sale.setTeriminalID("");//终端ID
            sale.setTotChanged("0");//找零金额
            sale.setOrderReturn("");////订金退款类型：0:沒有訂金1:部份訂金2:全部訂金【CM专用】
            sale.setOrderShop(orderShop);//下订门店
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
            sale.setbDate(otpReq.getOpBDate());
            sale.setCardNO(cardNo);
            sale.setContMan(contMan);
            sale.setContTel(contTel);
            sale.setCustomerName(customerName);
            sale.setCustomerNO(customer);
            sale.setDeliveryFeeUser(shipFee);
            sale.setDistribution(delName);
            sale.setMealNumber(mealNumber);
            sale.setBsno(refundReasonNo);
            sale.seteId(eId);
            sale.setEcCustomerNO(sellNo);
            sale.setgDate(shipDate);
            sale.setgTime(shipStartTime);
            sale.setGetMan(getMan);
            sale.setGetManTel(getManTel);
            sale.setMachine(otpReq.getOpMachineNo());
            sale.setManualNO(manualNo);
            sale.setMemberId(memberId);
            sale.setMemberName(memberName);
            sale.setMemberOrderno("");//调用积分memberpay的orderno,下面会给值
            sale.setMemo(memo);
            sale.setOfNO(Check.Null(headOrderNo)?orderNo:headOrderNo);
            sale.setOpNO(otpReq.getOpOpNo());
            sale.setOrder_channelId(channelId);
            sale.setOrder_companyId(belfirm);
            sale.setOrderID(Check.Null(headOrderNo)?orderNo:headOrderNo);
            sale.setPackageFee(packageFee);
            sale.setPayAmt(payAmt);
            sale.setPlatformDisc(platformDisc);
            sale.setPlatformFee(serviceCharge);
            sale.setPointQty(pointQty);
            sale.setsDate(sDate);
            sale.setSellCredit(sellCredit);
            sale.setReturnUserId(otpReq.getOpOpNo());
            sale.setSendMemo(delMemo);
            sale.setShipAdd(address);
            sale.setShippingFee(shipFee);
            sale.setDeliveryFeeShop(shopshareshipfee);
            sale.setShopId(shopId);
            sale.setSourceSubOrderno(Check.Null(headOrderNo)?"":orderNo);
            sale.setSquadNO(otpReq.getOpSquadNo());
            sale.setStatus("100");
            sale.setsTime(sTime);
            sale.setTotQty(totQty);//总数量
            sale.setTotAmt(tot_Amt);
            sale.setTotDisc(totDisc);
            sale.setTotOldAmt(tot_oldAmt);
            sale.setType("0");
            sale.setTypeName("销售单");
            sale.setVerNum(verNum);
            sale.setWorkNO(otpReq.getOpWorkNo());
            sale.setTran_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            sale.setWaimaiMerreceiveMode(waimaiMerreceiveMode);
            sale.setGroupBuying(groupBuying);
            sale.setPartnerMember(partnerMember);
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
                        payslistArray.add(tempPay);
                        
                        //
                        com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                        tempCard.put("cardNo",cardno);
                        tempCard.put("amount","0");//0只处理积分
                        tempCard.put("getPoint","0");
                        cardlistArray.add(tempCard);
                        
                        //
                        listCardno.add(cardno);
                    }
                    
                    //601是赊销付款方式
                    if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
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
            
            //**********************处理memberpay数据
            if (otpReq.getPay()!=null)
            {
                for (DCP_OrderToSaleProcess2_OpenReq.Payment rpay : otpReq.getPay())
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
                    if (loadDocType.equals("POS"))
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
                    if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
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
                        
                        cardlistArray.add(tempCard);
                    }
                    
                }
            }
            
            //累加已付金额
            bdm_tot_payamt=bdm_tot_payamt.add(payTot);
            
            //保留2位小数吧
            bdm_tot_merdiscount=bdm_tot_merdiscount.setScale(2,BigDecimal.ROUND_HALF_UP);
            bdm_tot_thirddiscount=bdm_tot_thirddiscount.setScale(2,BigDecimal.ROUND_HALF_UP);
            bdm_tot_merreceive=bdm_tot_merreceive.setScale(2,BigDecimal.ROUND_HALF_UP);
            bdm_tot_custpayreal=bdm_tot_custpayreal.setScale(2,BigDecimal.ROUND_HALF_UP);
            
            //如果有退订单的处理
            if (tempRefundOrderNo.equals("") == false)
            {
                bdm_tot_payamt=bdm_tot_payamt.subtract(new BigDecimal(refundAMT));
                
                //减去退单的折扣额，2元，退1.25元，默认产生的0.75当折扣
                bdm_tot_merdiscount=bdm_tot_merdiscount.subtract(bdm_tot_disc_part);
                
                bdm_tot_merreceive=bdm_tot_merreceive.subtract(new BigDecimal(refundAMT));
                bdm_tot_custpayreal=bdm_tot_custpayreal.subtract(new BigDecimal(refundAMT));
            }
            
            //*****根据单号调用库存锁定查询DCP_StockOrderLockDetail_Open
            boolean bDCP_StockUnlock=false;//是否需要调用库存解锁服务DCP_StockUnlock
            try
            {
                //内部服务形式调用
                if (req.getApiUser() == null)
                {
                    JSONObject req_SOLD = new JSONObject();
                    req_SOLD.put("serviceId","DCP_StockOrderLockDetail");
                    req_SOLD.put("token", req.getToken());//内部服务是有token的
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
            BigDecimal bdm_CurrentTotOldAmt=new BigDecimal("0");
            BigDecimal bdm_CurrentTotAmt=new BigDecimal("0");
            BigDecimal bdm_CurrentTotDisc=new BigDecimal("0");
            
            sale.setDetails(new ArrayList<>());
            
            
            //累加用于计算分摊比率
            BigDecimal bdm_tot_temp_amt=new BigDecimal("0");
            //商品数据验证处理
            for (DCP_OrderToSaleProcess2_OpenReq.goods goods : otpReq.getGoodsList())
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
            List<DCP_OrderToSaleProcess2_OpenReq.goods> tempShareGoods=otpReq.getGoodsList().stream().filter(p-> "2".equals(p.getPackageType())==false && new BigDecimal(p.getAmt()).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
            for (DCP_OrderToSaleProcess2_OpenReq.goods goods : tempShareGoods)
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
                        disc_merReceive=new BigDecimal(goods.getAmt()).multiply(bdm_tot_merdiscount).divide(bdm_tot_temp_amt, 2, RoundingMode.HALF_UP);
                        disc_Third=new BigDecimal(goods.getAmt()).multiply(bdm_tot_thirddiscount).divide(bdm_tot_temp_amt,2,RoundingMode.HALF_UP);
                        
                        //商家实收-配送费-餐盒费
                        //BigDecimal bdm_Realmerreceive=bdm_tot_merreceive.subtract(new BigDecimal(packageFee)).subtract(new BigDecimal(tot_shipFee));
                        
                        //商家实收（改成不减了）
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

//                //只要有付款折扣,自动加1笔折扣记录
                if (disc_custPayReal.compareTo(BigDecimal.ZERO)!=0)
                {
                    DCP_OrderToSaleProcess2_OpenReq.Agio tempAgio=req.new Agio();
                    
                    //处理付款顺序
                    int disc_item=1;
                    if (goods.getAgioInfo().size()>0)
                    {
                        //排序
                        goods.getAgioInfo().sort(Comparator.comparingInt(a->Integer.parseInt(a.getItem())));
                        //取最大的+1
                        disc_item=Integer.parseInt(goods.getAgioInfo().get(goods.getAgioInfo().size()-1).getItem())+1;
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
                    tempAgio.setQty(goods.getQty().toPlainString());
                    tempAgio.setRealDisc("0");
                    goods.getAgioInfo().add(tempAgio);
                }
            }
            
            //抹零金额尾款分摊处理(只处理商品折扣字段，不处理金额字段)
            if (new BigDecimal(sale.getEraseAmt()).compareTo(BigDecimal.ZERO)>0)
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
                for (DCP_OrderToSaleProcess2_OpenReq.goods goods : tempShareGoods)
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

//                    //只要有付款折扣,自动加1笔折扣记录
//                    if (disc_custPayReal.compareTo(BigDecimal.ZERO)>0)
//                    {
//
//                    }
                    
                    DCP_OrderToSaleProcess2_OpenReq.Agio tempAgio=req.new Agio();
                    
                    //处理付款顺序
                    int disc_item=1;
                    if (goods.getAgioInfo().size()>0)
                    {
                        //排序
                        goods.getAgioInfo().sort(Comparator.comparingInt(a->Integer.parseInt(a.getItem())));
                        //取最大的+1
                        disc_item=Integer.parseInt(goods.getAgioInfo().get(goods.getAgioInfo().size()-1).getItem())+1;
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
                    tempAgio.setQty(goods.getQty().toPlainString());
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
            
            //写销售明细表
            for (DCP_OrderToSaleProcess2_OpenReq.goods goods : otpReq.getGoodsList())
            {
                SALE.Detail detail=sale.new Detail();
                
                //字段验证处理
                if (Check.Null(goods.getAmt()) ) goods.setAmt("0");
                if (PosPub.isNumericTypeMinus(goods.getAmt())==false) goods.setAmt("0");
                
                if (Check.Null(goods.getBoxNum()) ) goods.setBoxNum("0");
                if (PosPub.isNumericTypeMinus(goods.getBoxNum())==false) goods.setBoxNum("0");
                
                if (Check.Null(goods.getBoxPrice()) ) goods.setBoxPrice("0");
                if (PosPub.isNumericTypeMinus(goods.getBoxPrice())==false) goods.setBoxPrice("0");
                
                if (Check.Null(goods.getPackageMitem()) ) goods.setPackageMitem("0");
                if (PosPub.isNumericTypeMinus(goods.getPackageMitem())==false) goods.setPackageMitem("0");
                
                if (Check.Null(goods.getFeatureNo()) ) goods.setFeatureNo(" ");
                if (Check.Null(goods.getWarehouse()) ) goods.setWarehouse(sd_warehouse);
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
                goods.setPluName(goods.getPluName());
                
                if(goods.getFeatureName()==null) goods.setFeatureName("");
                goods.setFeatureName(goods.getFeatureName());
                
                if(goods.getWarehouseName()==null) goods.setWarehouseName("");
                goods.setWarehouseName(goods.getWarehouseName());
                
                if(goods.getsUnitName()==null) goods.setsUnitName("");
                goods.setsUnitName(goods.getsUnitName());
                
                if(goods.getSpecName()==null) goods.setSpecName("");
                goods.setSpecName(goods.getSpecName());
                
                if(goods.getAttrName()==null) goods.setAttrName("");
                goods.setAttrName(goods.getAttrName());
                
                if(goods.getSellerName()==null) goods.setSellerName("");
                goods.setSellerName(goods.getSellerName());
                if(goods.getGiftReason()==null) goods.setGiftReason("");
                goods.setGiftReason(goods.getGiftReason());
                
                if(goods.getPluNo()==null) goods.setPluNo("");
                goods.setPluNo(goods.getPluNo());
                
                if(goods.getPluBarcode()==null) goods.setPluBarcode("");
                goods.setPluBarcode(goods.getPluBarcode());
                
                detail.setAccno(goods.getAccNo());
                detail.setAmt(goods.getAmt());
                detail.setbDate(sale.getbDate());
                detail.setBsno(refundReasonNo);
                //修复营业员编号和营业员名称赋值错误   by jinzma 20230915
                detail.setClerkName(goods.getSellerName());  //detail.setClerkName(goods.getSellerNo());
                detail.setClerkNo(goods.getSellerNo());      //detail.setClerkNo(goods.getSellerName());
                
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
                detail.setQty(goods.getQty().toPlainString());
                detail.setTaxCode(goods.getTaxCode());
                detail.setTaxRate(goods.getTaxRate().toPlainString());
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
                detail.setFlavorStuffDetail(goods.getFlavorStuffDetail());//口味加料细节
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
                bdm_CurrentTotOldAmt=bdm_CurrentTotOldAmt.add(new BigDecimal(detail.getOldAMT()));
                bdm_CurrentTotAmt=bdm_CurrentTotAmt.add(new BigDecimal(detail.getAmt()));
                bdm_CurrentTotDisc=bdm_CurrentTotDisc.add(new BigDecimal(detail.getDisc()));
                
                //套餐主商品或者普通商品
                if ("Y".equals(detail.getPackageMaster()) || "N".equals(detail.getIsPackage()))
                {
                    saleamt=saleamt.add(new BigDecimal(detail.getAmt()));
                    saledisc=saledisc.add(new BigDecimal(detail.getDisc()));
                }
                
                detail.setDetailAgios(new ArrayList<>());
                if (goods.getAgioInfo()==null)goods.setAgioInfo(new ArrayList<>());
                
                for (DCP_OrderToSaleProcess2_OpenReq.Agio rAgio : goods.getAgioInfo())
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
                ub_DCP_ORDER_DETAIL.addUpdateValue("PICKQTY", new DataValue(detail.getQty(), Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//已提货数量
                
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
                    ub_DCP_ORDER_DETAIL.addUpdateValue("PICKQTY", new DataValue(detail.getQty(), Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//已提货数量
                    
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
                    ub_DCP_ORDER_DETAIL.addCondition("ORDERNO", new DataValue(headOrderNo, Types.VARCHAR));
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
                            detail_org.put("organizationNo", shopId);
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
                            tempORG.setOrganizationNo(shopId);
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
                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"不写库存流水原因:订单号=" + orderNo +",pluno=" + detail.getPluNo()+",virtual=" +detail.getVirtual() + ",packageMaster=" +detail.getPackageMaster());
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
                    
                    success_credit=HelpTools.CustomerCreditUpdate(eId, apiUserCode, apiUserKey, req.getLangType(), saleno, belfirm,
                            orderShop, "3",req.getRequest().getOpOpName(),memo,orderNo,details,
                            customer, dealCreditAmount, error_sellCredit,bdate);
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
            
            
            //源于付款+抹零
            sale.setTotDisc_merReceive(bdm_tot_merdiscount.toPlainString());
            sale.setTotDisc_custPayReal(bdm_tot_merdiscount.add(bdm_tot_thirddiscount).toPlainString());
            
            //源于付款
            sale.setTot_Amt_merReceive(bdm_tot_merreceive.toPlainString());
            sale.setTot_Amt_custPayReal(bdm_tot_custpayreal.toPlainString());
            
            
            //是否需要调用库存解锁服务DCP_StockUnlock
            if (bDCP_StockUnlock)
            {
                try
                {
                    String bdate_DSUL=otpReq.getOpBDate().substring(0,4)+"-" + otpReq.getOpBDate().substring(4,6) +"-" +otpReq.getOpBDate().substring(6,8);
                    
                    if (req.getApiUser() == null)
                    {
                        JSONObject req_DSUL = new JSONObject();
                        req_DSUL.put("serviceId","DCP_StockUnlock");
                        req_DSUL.put("token",req.getToken());//内部服务是有token的
                        //
                        JSONObject request_DSUL = new JSONObject();
                        request_DSUL.put("billNo", orderNo);
                        request_DSUL.put("billType", "Order");
                        request_DSUL.put("channelId", channelId);
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
                        unlockrequest.setChannelId(channelId);
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
                        request_DSUL.put("channelId", channelId);
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
            
            //精度格式化
            bdm_CurrentTotQty=bdm_CurrentTotQty.setScale(3,BigDecimal.ROUND_HALF_UP);
            
            //如果有退订单的处理
            if (tempRefundOrderNo.equals("") == false)
            {
                //原单直接减退的，因为这个订单的存值不是明细汇总值
                bdm_CurrentTotOldAmt=new BigDecimal(tot_oldAmt).subtract(bdm_tot_oldamt_part).setScale(2,BigDecimal.ROUND_HALF_UP);
                bdm_CurrentTotAmt=new BigDecimal(tot_Amt).subtract(bdm_tot_amt_part).setScale(2,BigDecimal.ROUND_HALF_UP);
                bdm_CurrentTotDisc=bdm_CurrentTotOldAmt.subtract(bdm_CurrentTotAmt).setScale(2,BigDecimal.ROUND_HALF_UP);
                
                totDisc=new BigDecimal(totDisc).subtract(bdm_tot_disc_part).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString();
                sellerDisc=new BigDecimal(sellerDisc).subtract(bdm_tot_disc_part).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString();
                incomeAmt=new BigDecimal(incomeAmt).subtract(bdm_tot_amt_part).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString();
            }
            else
            {
                bdm_CurrentTotOldAmt=bdm_CurrentTotOldAmt.setScale(2,BigDecimal.ROUND_HALF_UP);
                bdm_CurrentTotAmt=bdm_CurrentTotAmt.setScale(2,BigDecimal.ROUND_HALF_UP);
                bdm_CurrentTotDisc=bdm_CurrentTotDisc.setScale(2,BigDecimal.ROUND_HALF_UP);
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
                errorMessage.append("orderno="+orderNo+",status="+status+",refundstatus="+refundStatus+",本次商品数量("+bdm_CurrentTotQty+")>订单剩余可提货量("+bdm_tot_RemainQTY+")，请检查数量栏位 !"  );
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
            
            //刷新销售单头字段
            sale.setTotOldAmt(bdm_CurrentTotOldAmt.toPlainString());
            sale.setTotAmt(bdm_CurrentTotAmt.toPlainString());
            sale.setTotDisc(bdm_CurrentTotDisc.toPlainString());
            sale.setTotQty(bdm_CurrentTotQty.toPlainString());
            sale.setPayAmt(bdm_tot_payamt.toPlainString());
            sale.setSaleDisc(totDisc);//销售折扣金额
            sale.setSellerDisc(sellerDisc);
            sale.setShopIncome(incomeAmt);
            
            
            //已付金额>=(getTotAmt-getEraseAmt)
            if (bdm_tot_payamt.compareTo(new BigDecimal(sale.getTotAmt()).subtract(new BigDecimal(sale.getEraseAmt())))>=0)
            {
                bPayComplete=true;
            }
            
            
            //积分累计或301消费扣款处理
            if (isNeedMemberpay&&(Check.Null(memberId)==false ||  Check.Null(cardNo)==false || payslistArray.size()>0 || cardlistArray.size()>0 || couponlistArray.size()>0))
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
                if (cardNo.equals("")==false && listCardno.contains(cardNo)==false)
                {
                    //
                    com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                    tempCard.put("cardNo",cardNo);
                    tempCard.put("amount","0");//只处理积分
                    tempCard.put("getPoint","0");
                    cardlistArray.add(tempCard);
                }
                
                
                reqheader.put("orderNo", memberOrderno);//需唯一
                reqheader.put("businessType", "2");//业务类型0.其他1.订单下订2.订单提货3.零售支付
                reqheader.put("srcBillType", "订转销");//实际业务单别
                reqheader.put("srcBillNo", orderNo);//实际业务单号
                reqheader.put("orderAmount", tot_Amt);//
                reqheader.put("pointAmount", tot_Amt);//
                reqheader.put("memberId",memberId);//
                reqheader.put("orgType", "2");//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
                reqheader.put("orgId", otpReq.getOpShopId());//订转销取操作门店
                //【ID1039870】小程序支付成功，订单中心门店视角发货报错。提示卡不能在本店使用。
                if(orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType))
                {
                    reqheader.put("orgType", "3");
                    reqheader.put("orgId", channelId);
                }
                reqheader.put("oprId", otpReq.getOpOpNo());//
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
                    
                    String serviceDescription=jsonres.get("serviceDescription").toString();
                    String serviceStatus=jsonres.get("serviceStatus").toString();
                    
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
                                
                                res.getDatas().getVipDatas().setCardsInfo(new ArrayList<DCP_OrderToSaleProcess_OpenRes.Card>());
                                
                                com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
                                for (int pi = 0; pi < cardsList.size(); pi++)
                                {
                                    //多张卡累加积分
                                    getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
                                    
                                    DCP_OrderToSaleProcess_OpenRes.Card card=res.new Card();
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
                                    res.getDatas().getVipDatas().setMemberId(memberId);
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
                            
                            res.getDatas().getVipDatas().setCardsInfo(new ArrayList<DCP_OrderToSaleProcess_OpenRes.Card>());
                            
                            com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
                            for (int pi = 0; pi < cardsList.size(); pi++)
                            {
                                //多张卡累加积分
                                getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
                                
                                DCP_OrderToSaleProcess_OpenRes.Card card=res.new Card();
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
                                res.getDatas().getVipDatas().setMemberId(memberId);
                                res.getDatas().getVipDatas().setPoints(card.getPoint_after());
                            }
                        }
                        else
                        {
                            errorMessage.append("调用会员积分查询接口MemberPay失败:" + serviceDescription);
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
                for (DCP_OrderToSaleProcess2_OpenReq.Payment rpay : otpReq.getPay())
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
                        for (DCP_OrderToSaleProcess_OpenRes.Card card : res.getDatas().getVipDatas().getCardsInfo())
                        {
                            if (card.getCardNo().equals(rpay.getCardNo()) || ("3011".equals(rpay.getFuncNo()) && rpay.getCardNo().contains(card.getCardNo())) || "3012".equals(rpay.getFuncNo()) || "3013".equals(rpay.getFuncNo())|| "3014".equals(rpay.getFuncNo()))
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
                    pay.setPayserNum(rpay.getPaySerNum());
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
                            new DataValue(loadDocType, Types.VARCHAR),
                            new DataValue(channelId, Types.VARCHAR),
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
                    if (loadDocType.equals(orderLoadDocType.POS)|| loadDocType.equals(orderLoadDocType.POSANDROID))
                    {
                        //交班统计信息表DCP_STATISTIC_INFO
                        String[] Columns_DCP_STATISTIC_INFO = {
                                "EID","SHOPID","MACHINE","OPNO","SQUADNO","ORDERNO","ITEM","PAYCODE",
                                "PAYNAME","AMT","SDATE","STIME","ISORDERPAY","WORKNO","TYPE","BDATE","CARDNO",
                                "CUSTOMERNO","CHANGED","EXTRA","ISTURNOVER","STATUS","APPTYPE","CHANNELID","PAYTYPE",
                                "MERDISCOUNT","THIRDDISCOUNT","DIRECTION","CHARGEAMOUNT","PAYCHANNELCODE"
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
                                new DataValue(customer, Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue(rpay.getExtra(), Types.VARCHAR),
                                new DataValue("Y", Types.VARCHAR),//ISTURNOVER
                                new DataValue("100", Types.VARCHAR),
                                new DataValue(loadDocType, Types.VARCHAR),//
                                new DataValue(channelId, Types.VARCHAR),//
                                new DataValue(pay.getPayType(), Types.VARCHAR),//
                                new DataValue(rpay.getMerDiscount(), Types.VARCHAR),
                                new DataValue(rpay.getThirdDiscount(), Types.VARCHAR),
                                new DataValue(1, Types.INTEGER),
                                new DataValue(rpay.getChargeAmount(), Types.DECIMAL),
                                new DataValue(rpay.getPayChannelCode(), Types.VARCHAR),
                        };
                        InsBean ib_DCP_STATISTIC_INFO = new InsBean("DCP_STATISTIC_INFO", Columns_DCP_STATISTIC_INFO);
                        ib_DCP_STATISTIC_INFO.addValues(insValue_DCP_STATISTIC_INFO);
                        data.add(new DataProcessBean(ib_DCP_STATISTIC_INFO));
                    }
                    
                    
                    
                    //更新订单付款明细表DCP_ORDER_PAY_DETAIL(尾款只有1次，全部冲销)
                    UptBean ub_DCP_ORDER_PAY_DETAIL = new UptBean("DCP_ORDER_PAY_DETAIL");
                    //更新值
                    ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("WRITEOFFAMT", new DataValue("PAY", Types.DECIMAL, DataValue.DataExpression.OtherFieldname));//已提货金额
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
                            new DataValue(belfirm, Types.VARCHAR),
                            new DataValue(loadDocType, Types.VARCHAR),
                            new DataValue(otpReq.getOpShopId(), Types.VARCHAR),
                            new DataValue(channelId, Types.VARCHAR),//渠道不管了
                            new DataValue(otpReq.getOpMachineNo(), Types.VARCHAR),
                            new DataValue(customer, Types.VARCHAR),
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
                    ub_DCP_ORDER_PAY.addUpdateValue("WRITEOFFAMT", new DataValue("PAYREALAMT", Types.DECIMAL, DataValue.DataExpression.OtherFieldname));//冲销金额
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
            BigDecimal bdm_writeAMT=new BigDecimal(tot_Amt).subtract(payTot);
            
            String order_pay_billno="";
            
            
            if(getData_order_pay!=null && getData_order_pay.isEmpty()==false)
            {
                for (Map<String, Object> map : getData_order_pay)
                {
                    order_pay_billno=map.get("BILLNO").toString();
                    
                    String funcno=map.get("FUNCNO").toString();
                    String cardno=map.get("CARDNO").toString();
                    
                    //PAY-CHANGED-EXTRA-WRITEOFFAMT
                    BigDecimal bdm_pay=new BigDecimal(map.get("PAY").toString()).subtract(new BigDecimal(map.get("CHANGED").toString())).subtract(new BigDecimal(map.get("EXTRA").toString())).subtract(new BigDecimal(map.get("WRITEOFFAMT").toString()));
                    
                    //PAY-CHANGED
                    BigDecimal p_amt=new BigDecimal(map.get("PAY").toString()).subtract(new BigDecimal(map.get("CHANGED").toString()));
                    
                    //
                    BigDecimal bdm_O_pay=new BigDecimal(map.get("PAY").toString());
                    BigDecimal bdm_O_change=new BigDecimal(map.get("CHANGED").toString());
                    BigDecimal bdm_O_merreceive=new BigDecimal(map.get("MERRECEIVE").toString());
                    BigDecimal bdm_O_custpayreal=new BigDecimal(map.get("CUSTPAYREAL").toString());
                    BigDecimal bdm_O_merdiscount=new BigDecimal(map.get("MERDISCOUNT").toString());
                    
                    
                    //如果有退订单的处理
                    if (tempRefundOrderNo.equals("")==false)
                    {
                        //减掉已退金额
                        p_amt=p_amt.subtract(new BigDecimal(refundAMT));
                        
                        bdm_O_pay=bdm_O_pay.subtract(new BigDecimal(refundAMT));
                        bdm_O_merreceive=bdm_O_merreceive.subtract(new BigDecimal(refundAMT));
                        bdm_O_custpayreal=bdm_O_custpayreal.subtract(new BigDecimal(refundAMT));
                        bdm_O_merdiscount=bdm_O_merdiscount.subtract(bdm_tot_disc_part);
                    }
                    
                    
                    
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
                            new DataValue(bdm_O_pay, Types.DECIMAL),//tempPay
                            new DataValue("0", Types.DECIMAL),
                            new DataValue(map.get("CHANGED").toString(), Types.DECIMAL),
                            new DataValue(map.get("EXTRA").toString(), Types.DECIMAL),
                            new DataValue("0", Types.DECIMAL),
                            new DataValue(map.get("PAYSERNUM").toString(), Types.VARCHAR),
                            new DataValue(map.get("SERIALNO").toString(), Types.VARCHAR),
                            new DataValue(map.get("REFNO").toString(), Types.VARCHAR),
                            new DataValue(map.get("TERIMINALNO").toString(), Types.VARCHAR),
                            new DataValue(map.get("CTTYPE").toString(), Types.VARCHAR),
                            new DataValue(cardno, Types.VARCHAR),
                            new DataValue(map.get("CARDBEFOREAMT").toString(), Types.DECIMAL),
                            new DataValue(map.get("CARDREMAINAMT").toString(), Types.DECIMAL),
                            new DataValue(map.get("SENDPAY").toString(), Types.DECIMAL),
                            new DataValue(map.get("ISVERIFICATION").toString(), Types.VARCHAR),
                            new DataValue(map.get("COUPONQTY").toString(), Types.DECIMAL),
                            new DataValue(map.get("DESCORE").toString(), Types.DECIMAL),
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
                            new DataValue(bdm_O_merdiscount, Types.VARCHAR),
                            new DataValue(bdm_O_merreceive.toPlainString(), Types.VARCHAR),
                            new DataValue(map.get("THIRDDISCOUNT").toString(), Types.VARCHAR),
                            new DataValue(bdm_O_custpayreal.toPlainString(), Types.VARCHAR),
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
                    
                    //只有POS和Android POS
                    if (loadDocType.equals(orderLoadDocType.POS)|| loadDocType.equals(orderLoadDocType.POSANDROID))
                    {
                        //交班统计信息表DCP_STATISTIC_INFO
                        String[] Columns_DCP_STATISTIC_INFO = {
                                "EID","SHOPID","MACHINE","OPNO","SQUADNO","ORDERNO","ITEM","PAYCODE",
                                "PAYNAME","AMT","SDATE","STIME","ISORDERPAY","WORKNO","TYPE","BDATE","CARDNO",
                                "CUSTOMERNO","CHANGED","EXTRA","ISTURNOVER","STATUS","APPTYPE","CHANNELID","PAYTYPE",
                                "MERDISCOUNT","THIRDDISCOUNT","DIRECTION","CHARGEAMOUNT","PAYCHANNELCODE"
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
                                new DataValue(map.get("MERDISCOUNT").toString(), Types.VARCHAR),
                                new DataValue(map.get("THIRDDISCOUNT").toString(), Types.VARCHAR),
                                new DataValue(1, Types.INTEGER),
                                new DataValue(map.get("CHARGEAMOUNT").toString(), Types.DECIMAL),
                                new DataValue(map.get("PAYCHANNELCODE").toString(), Types.VARCHAR),
                        };
                        InsBean ib_DCP_STATISTIC_INFO = new InsBean("DCP_STATISTIC_INFO", Columns_DCP_STATISTIC_INFO);
                        ib_DCP_STATISTIC_INFO.addValues(insValue_DCP_STATISTIC_INFO);
                        data.add(new DataProcessBean(ib_DCP_STATISTIC_INFO));
                    }
                    
                    
                    //更新订单付款明细表DCP_ORDER_PAY_DETAIL(尾款只有1次，全部冲销)
                    UptBean ub_DCP_ORDER_PAY_DETAIL = new UptBean("DCP_ORDER_PAY_DETAIL");
                    //更新值
                    ub_DCP_ORDER_PAY_DETAIL.addUpdateValue("WRITEOFFAMT", new DataValue(tempPay, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//冲销金额
                    //更新条件
                    ub_DCP_ORDER_PAY_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_ORDER_PAY_DETAIL.addCondition("BILLNO", new DataValue(order_pay_billno, Types.VARCHAR));
                    data.add(new DataProcessBean(ub_DCP_ORDER_PAY_DETAIL));
                    
                }
            }
            
            
            //订单付款单头冲销金额
            if (order_pay_billno.equals("")==false)
            {
                //更新订单付款明细表DCP_ORDER_PAY
                UptBean ub_DCP_DCP_ORDER_PAY = new UptBean("DCP_ORDER_PAY");
                //更新值
                ub_DCP_DCP_ORDER_PAY.addUpdateValue("WRITEOFFAMT", new DataValue(bdm_writeAMT.toPlainString(), Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//冲销金额
                ub_DCP_DCP_ORDER_PAY.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub_DCP_DCP_ORDER_PAY.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                
                //更新条件
                ub_DCP_DCP_ORDER_PAY.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub_DCP_DCP_ORDER_PAY.addCondition("BILLNO", new DataValue(order_pay_billno, Types.VARCHAR));
                data.add(new DataProcessBean(ub_DCP_DCP_ORDER_PAY));
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
                    "PARTITION_DATE","GROUPBUYING","PARTNERMEMBER"
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
                    new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                    new DataValue(sale.getGroupBuying(), Types.VARCHAR),
                    new DataValue(sale.getPartnerMember(),Types.VARCHAR),
                
            };
            InsBean ib_DCP_SALE = new InsBean("DCP_SALE", Columns_DCP_SALE);//分区字段已处理
            ib_DCP_SALE.addValues(insValue_DCP_SALE);
            data.add(new DataProcessBean(ib_DCP_SALE));
            
            
            
            //更新订单主表DCP_ORDER
            UptBean ub_DCP_ORDER = new UptBean("DCP_ORDER");
            //更新值
            ub_DCP_ORDER.addUpdateValue("WRITEOFFAMT", new DataValue(tot_Amt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//已提货金额
            ub_DCP_ORDER.addUpdateValue("POINTQTY", new DataValue(pointQty, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//会员积分
            ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue(payTot.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(), Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//已付金额
            ub_DCP_ORDER.addUpdateValue("ORDERTOSALE_DATETIME", new DataValue(sDatetimeMs, Types.VARCHAR));
            
            //本次订转销之后单据就提货完成标记
            if (bPickqtyOk)
            {
                // 1.订单来源渠道 2.全国配送 3.顾客自提 5总部配送 6同城配送
                if (shipType.equals("1") || shipType.equals("3") || deliveryHandinput.equals("Y"))
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
                if (loadDocType.equals("LINE") || loadDocType.equals("MINI") || loadDocType.equals("WECHAT"))
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
                        if (deliveryType!=null)
                        {
                            //物流信息
                            body = new JSONObject();
                            //body.put("expressType", deliveryType);
                            body.put("expressTypeName", subDeliveryCompanyName);
                            body.put("expressBillNo", deliveryNo);
                            //body.put("expressTypeCode", deliveryType);
                            body.put("expressTypeCode", subDeliveryCompanyNo);
                            deliverInfo.add(body);
                            //js.put("deliverInfo", deliverInfo);
                            if (!"KDN".equals(deliveryType))
                            {
                                js.put("deliverInfo", deliverInfo);
                            }
                            
                            
                            //写订单日志:已发货的物流日志
                            orderStatusLog oslog=new orderStatusLog();
                            oslog.setCallback_status("N");
                            oslog.setChannelId(channelId);
                            oslog.setDisplay("1");
                            oslog.seteId(eId);
                            oslog.setLoadDocBillType(loadDocType);
                            oslog.setLoadDocOrderNo(orderNo);
                            oslog.setLoadDocType(loadDocType);
                            oslog.setMachShopName(otpReq.getOpMachineNo());
                            oslog.setMachShopNo(otpReq.getOpMachineNo());
                            oslog.setMemo("物流配送:" + deliveryType+"-"+deliveryNo);
                            if (deliveryType!=null&&!deliveryType.isEmpty())
                            {
                                String deliveryTypeName = subDeliveryCompanyName;
                                if (deliveryTypeName==null||deliveryTypeName.isEmpty())
                                {
                                    deliveryTypeName = HelpTools.getDeliveryTypeName(deliveryType);
                                }
                                oslog.setMemo("物流配送:" + deliveryTypeName+"-"+deliveryNo);
                            }

                            oslog.setNeed_callback("N");
                            oslog.setNeed_notify("N");
                            oslog.setNotify_status("N");
                            oslog.setOpName(otpReq.getOpOpName());
                            oslog.setOpNo(otpReq.getOpOpNo());
                            oslog.setOrderNo(orderNo);
                            oslog.setShippingShopName(shopName);
                            oslog.setShippingShopNo(shopId);
                            oslog.setShopName(otpReq.getOpShopId());
                            oslog.setShopNo(otpReq.getOpShopId());
                            oslog.setStatus(LogStatus);
                            //
                            String statusType="1";
                            StringBuilder statusTypeName=new StringBuilder();
                            String statusName= HelpTools.GetOrderStatusName(statusType, "10", statusTypeName);
                            oslog.setStatusName(statusName);
                            oslog.setStatusType(statusType);
                            oslog.setStatusTypeName(statusTypeName.toString());
                            oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                            InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
                            data.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));
                            
                        }
                        String request = js.toString();
                        HttpSend.MicroMarkSend(request, eId, "OrderStatusUpdate",channelId);
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
                        if (deliveryType!=null)
                        {
                            //物流信息
                            body = new JSONObject();
                            //body.put("expressType", deliveryType);//这个int，必须先不传了。
                            body.put("expressTypeName", subDeliveryCompanyName);
                            body.put("expressBillNo", deliveryNo);
                            //body.put("expressTypeCode", deliveryType);
                            body.put("expressTypeCode", subDeliveryCompanyNo);
                            deliverInfo.add(body);
                            //js.put("deliverInfo", deliverInfo);
                            if (!"KDN".equals(deliveryType))
                            {
                                js.put("deliverInfo", deliverInfo);
                            }
                            
                            
                            
                        }
                        String request = js.toString();
                        HttpSend.MicroMarkSend(request, eId, "OrderStatusUpdate",channelId);
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
                    ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue("TOT_AMT", Types.DECIMAL, DataValue.DataExpression.OtherFieldname));//已付金额
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
            oslog.setChannelId(channelId);
            oslog.setDisplay("0");
            oslog.seteId(eId);
            oslog.setLoadDocBillType(loadDocType);
            oslog.setLoadDocOrderNo(orderNo);
            oslog.setLoadDocType(loadDocType);
            oslog.setMachShopName(otpReq.getOpMachineNo());
            oslog.setMachShopNo(otpReq.getOpMachineNo());
            oslog.setMemo(memo);
            oslog.setNeed_callback("N");
            oslog.setNeed_notify("N");
            oslog.setNotify_status("N");
            oslog.setOpName(otpReq.getOpOpName());
            oslog.setOpNo(otpReq.getOpOpNo());
            oslog.setOrderNo(orderNo);
            oslog.setShippingShopName(shopName);
            oslog.setShippingShopNo(shopId);
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
                ub_DCP_ORDER.addUpdateValue("WRITEOFFAMT", new DataValue("", Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//已提货金额
                ub_DCP_ORDER.addUpdateValue("POINTQTY", new DataValue(pointQty, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//会员积分
                
                //本次订转销之后单据就提货完成标记
                if (bPickqtyOk)
                {
                    //(尾款一次性付清)
                    ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue("TOT_AMT", Types.DECIMAL, DataValue.DataExpression.OtherFieldname));//已付金额
                    ub_DCP_ORDER.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR));// 1.未支付 2.部分支付 3.付清
                    ub_DCP_ORDER.addUpdateValue("ORDERTOSALE_DATETIME", new DataValue(sDatetimeMs, Types.VARCHAR));
                    
                    // 1.订单来源渠道 2.全国配送 3.顾客自提 5总部配送 6同城配送
                    if ((shipType.equals("1") && deliveryType!=null && !deliveryType.equals("21")) || shipType.equals("3") || deliveryHandinput.equals("Y"))
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
                    ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue(payTot.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(), Types.DECIMAL, DataValue.DataExpression.UpdateSelf));//已付金额
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
                        ub_DCP_ORDER.addUpdateValue("PAYAMT", new DataValue("TOT_AMT", Types.DECIMAL, DataValue.DataExpression.OtherFieldname));//已付金额
                    }
                }
                
                ub_DCP_ORDER.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                ub_DCP_ORDER.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                
                //更新条件
                ub_DCP_ORDER.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub_DCP_ORDER.addCondition("ORDERNO", new DataValue(headOrderNo, Types.VARCHAR));
                data.add(new DataProcessBean(ub_DCP_ORDER));
                
                
                //写订单日志
                oslog=new orderStatusLog();
                oslog.setCallback_status("N");
                oslog.setChannelId(channelId);
                oslog.setDisplay("0");
                oslog.seteId(eId);
                oslog.setLoadDocBillType(loadDocType);
                oslog.setLoadDocOrderNo(headOrderNo);
                oslog.setLoadDocType(loadDocType);
                oslog.setMachShopName(otpReq.getOpMachineNo());
                oslog.setMachShopNo(otpReq.getOpMachineNo());
                oslog.setMemo(memo);
                oslog.setNeed_callback("N");
                oslog.setNeed_notify("N");
                oslog.setNotify_status("N");
                oslog.setOpName(otpReq.getOpOpName());
                oslog.setOpNo(otpReq.getOpOpNo());
                oslog.setOrderNo(headOrderNo);
                oslog.setShippingShopName(shopName);
                oslog.setShippingShopNo(shopId);
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
            ub_DCP_ORDER_PAY.addUpdateValue("WRITEOFFAMT", new DataValue("PAYREALAMT", Types.DECIMAL, DataValue.DataExpression.OtherFieldname));//已提货金额
            ub_DCP_ORDER_PAY.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub_DCP_ORDER_PAY.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            
            
            //更新条件
            ub_DCP_ORDER_PAY.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub_DCP_ORDER_PAY.addCondition("SOURCEBILLNO", new DataValue(b_subOrderFlag?headOrderNo:orderNo, Types.VARCHAR));
            data.add(new DataProcessBean(ub_DCP_ORDER_PAY));
            
            //
            
            String loadDocType1=getQData_Order.get(0).get("LOADDOCTYPE")==null?"":getQData_Order.get(0).get("LOADDOCTYPE").toString();
            
            if(orderLoadDocType.YOUZAN.equals(loadDocType1)){
                YouZanCallBackServiceV3 ycb=new YouZanCallBackServiceV3();
                try{
                    Map<String, Object> otherMap = new HashMap<String, Object>();
                    otherMap.put("extra_info", otpReq.getOpOpNo()==null?otpReq.getOpShopId():otpReq.getOpOpNo());//操作人
                    JsonBasicRes thisRes=new JsonBasicRes();
                    thisRes=ycb.OrderToSale(eId, orderNo, shopId,otherMap,null);
                    if(!thisRes.isSuccess()){
                        errorMessage.append("操作失败："+thisRes.getServiceDescription());
                        return false;
                    }
                }catch (Exception e) {
                    errorMessage.append("操作失败："+e.getMessage());
                    return false;
                }
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
                        if (loadDocType.equals(orderLoadDocType.POS)
                                ||loadDocType.equals(orderLoadDocType.POSANDROID)
                                ||loadDocType.equals(orderLoadDocType.PADGUIDE)
                                ||loadDocType.equals(orderLoadDocType.WAIMAI))
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
                            req_InvoiceCreate.put("bDate", otpReq.getOpBDate());
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
                            
                            String resbody_invoiceCreate=HttpSend.doPost(posUrl,str_invoiceCreate,map,"requestId");
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
                                            //int oItem=detailList.getJSONObject(di).getInt("oItem");
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
                    
                    String sqlerr= ex.getCause()==null || ex.getCause().getMessage()==null?"":ex.getCause().getMessage();
                    
                    PosPub.WriteETLJOBLog("出貨訂轉銷DCP_OrderToSaleProcess_Open，调用开发票POS_InvoiceCreate_Open异常原因："+ sqlerr +"\r\n"+ errors.toString());
                    
                    //开发票失败
                    res.getDatas().getInvoiceInfo().setSuccess(false);
                    res.getDatas().getInvoiceInfo().setServiceDescription("订单号："+orderNo+"调用开发票异常原因："+ sqlerr +"\r\n"+ex.getMessage() +"<br/>" +errors.toString());
                    
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
                    
                    String resbody_InvoicePreDelete= HttpSend.doPost(posUrl, str_InvoicePreDelete, map,requestId);
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
    private String getTRNO(String eId,String shopId,String bDate,String saleno,DCP_OrderToSaleProcess2_OpenReq req)
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
     * @param
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
            
            req_sign= DigestUtils.md5Hex(req_sign);
            
            //
            signheader.put("key", Yc_Key);//
            signheader.put("sign", req_sign);//md5
            
            payReq.put("serviceId", "MemberPayReverse");
            
            payReq.put("request", reqheader);
            payReq.put("sign", signheader);
            
            String str = payReq.toString();
            
            PosPub.WriteETLJOBLog("会员撤销付款接口MemberPayReverse请求内容："+str +"\r\n");
            
            //编码处理
            str= URLEncoder.encode(str, "UTF-8");
            
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
    
    
    
}
