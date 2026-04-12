package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderInvoiceReq;
import com.dsc.spos.json.cust.res.DCP_OrderInvoiceRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *               说明：
 * 目前规格只支持开发票、折让发票，不支持作废发票
 */
public class DCP_OrderInvoice extends SPosAdvanceService<DCP_OrderInvoiceReq, DCP_OrderInvoiceRes>
{
    @Override
    protected void processDUID(DCP_OrderInvoiceReq req, DCP_OrderInvoiceRes res) throws Exception
    {
        res.setDatas(res.new Data());

        String eId=req.geteId();
        String orderNo=req.getRequest().getOrderNo();
        String invOperateType=req.getRequest().getInvOperateType();//操作类型  0：发票打印  1：发票作废，2：折让单打印

        if (invOperateType.equals("1"))
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("目前不支持发票作废功能！");
            return;
        }

        String Yc_Key="";
        String Yc_Sign_Key="";
        String posUrl = PosPub.getPOS_INNER_URL(eId);

        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + eId + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        if (result != null && result.size() > 0)
        {
            for (Map<String, Object> map : result)
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
        }


        String refundOrderno="";//退发票的单号
        //发票折让要根据退订单号查询
        if (invOperateType.equals("2"))
        {
            //查退单SQL
            String sqlOrder="select * from dcp_order a where a.eid='"+eId+"' and a.REFUNDSOURCEBILLNO='"+orderNo+"' ";
            List<Map<String, Object>> getData_Order=this.doQueryData(sqlOrder, null);
            if (getData_Order!=null && getData_Order.isEmpty()==false)
            {
                refundOrderno=getData_Order.get(0).get("ORDERNO").toString();
            }
            else
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("找不到eId:"+eId+",订单号："+orderNo +",对应的退单信息！");
                return;
            }
        }

        //调用发票查询接口
        JSONObject request_InvoiceQuery = new JSONObject();
        request_InvoiceQuery.put("saleType","Order");//单据类型：Sale销售单，Order订单，Card售卡 Coupon售券，Recharge储值

        //发票折让要根据退订单号查询
        if (invOperateType.equals("2"))
        {
            request_InvoiceQuery.put("saleNo",refundOrderno);//单据号
        }
        else
        {
            request_InvoiceQuery.put("saleNo",orderNo);//单据号
        }
        request_InvoiceQuery.put("invOperateType",invOperateType);//非必须,记录类型：0-开立 1-作废 2-折让
        String str_InvoiceQuery=request_InvoiceQuery.toString();

        String requestId=PosPub.getGUID(false);
        //
        Map<String, Object> map = new HashMap<>();
        map.put("serviceId", "POS_InvoiceQuery_Open");
        map.put("apiUserCode",Yc_Key);
        map.put("sign", PosPub.encodeMD5(str_InvoiceQuery + Yc_Sign_Key));
        posUrl = PosPub.getPOS_INNER_URL(eId);

        map.put("langType",req.getLangType());
        map.put("requestId",requestId);
        map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
        map.put("version","3.0");

        PosPub.WriteETLJOBLog("调用发票查询接口POS_InvoiceQuery_Open req： "+ str_InvoiceQuery);

        String resbody_InvoiceQuery= HttpSend.doPost(posUrl, str_InvoiceQuery, map,requestId);
        map.clear();
        map=null;

        PosPub.WriteETLJOBLog("调用发票查询接口POS_InvoiceQuery_Open res： "+ resbody_InvoiceQuery);

        if (resbody_InvoiceQuery.equals("")==false)
        {
            JSONObject jsonres_InvoiceQuery = new JSONObject(resbody_InvoiceQuery);
            boolean success = jsonres_InvoiceQuery.getBoolean("success");
            if (success)
            {
                JSONObject  datas_InvoiceQuery=jsonres_InvoiceQuery.getJSONObject("datas");
                //invoiceList发票节点是否有记录判断
                if (datas_InvoiceQuery != null && datas_InvoiceQuery.getJSONArray("invoiceList").length()>0)
                {
                    //已开发票或已折让发票都直接返回查询发票结果
                    ParseJson pj = new ParseJson();
                    DCP_OrderInvoiceRes.Data myData=pj.jsonToBean(datas_InvoiceQuery.toString(),new TypeToken<DCP_OrderInvoiceRes.Data>(){});
                    pj=null;
                    res.setDatas(myData);
                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功！");
                }
                else
                {
                    if (invOperateType.equals("0"))//0：开发票
                    {
                        List<DataProcessBean> data = new ArrayList<DataProcessBean>();

                        String sqlOrder="select * from dcp_order a where a.eid='"+eId+"' and a.ORDERNO='"+orderNo+"' ";
                        List<Map<String, Object>> getData_Order=this.doQueryData(sqlOrder, null);
                        if (getData_Order==null || getData_Order.isEmpty())
                        {
                            res.setSuccess(false);
                            res.setServiceStatus("100");
                            res.setServiceDescription("找不到eId:"+eId+",订单号："+orderNo +"的资料！");
                            return;
                        }
                        //
                        String invoiceShopId=getData_Order.get(0).get("SHIPPINGSHOP").toString();//开发票门店
                        String accountDate = PosPub.getAccountDate_SMS(dao, eId, invoiceShopId);//取入账日期
                        String freeCode=getData_Order.get(0).get("FREECODE").toString();
                        String passport=getData_Order.get(0).get("PASSPORT").toString();
                        String invMemo=getData_Order.get(0).get("INVMEMO").toString();
                        String carrierCode=getData_Order.get(0).get("CARRIERCODE").toString();
                        String loveCode=getData_Order.get(0).get("LOVECODE").toString();
                        String buyerGuiNo=getData_Order.get(0).get("BUYERGUINO").toString();
                        String invoiceType=getData_Order.get(0).get("INVOICETYPE").toString();
                        String carrierShowId=getData_Order.get(0).get("CARRIERSHOWID").toString();
                        String carrierHiddenId=getData_Order.get(0).get("CARRIERHIDDENID").toString();

                        //调用开发票接口POS_InvoiceCreate_Open
                        JSONObject request_InvoiceCreate = new JSONObject();
                        JSONArray datas_invoiceCreateList = new JSONArray();

                        JSONArray datas_goodsList = new JSONArray();
                        JSONArray datas_payList = new JSONArray();

                        request_InvoiceCreate.put("orgId", invoiceShopId);
                        request_InvoiceCreate.put("recipient", "3");//1.云POS 2.全渠道会员 3.云中台 4.外卖点餐
                        request_InvoiceCreate.put("saleType", "Order");//单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值
                        request_InvoiceCreate.put("saleNo", orderNo);
                        request_InvoiceCreate.put("invCount", "1");//发票张数，不传时默认1
                        request_InvoiceCreate.put("freeCode", freeCode);//零税证号
                        request_InvoiceCreate.put("passport", passport);//护照号
                        request_InvoiceCreate.put("invSplitType", "1");//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分，不传时默认1不拆分

                        //发票信息
                        JSONObject req_InvoiceCreate = new JSONObject();
                        req_InvoiceCreate.put("invItem", "1");//发票项次
                        req_InvoiceCreate.put("invMemo", invMemo);
                        req_InvoiceCreate.put("carrierCode", carrierCode);
                        req_InvoiceCreate.put("loveCode", loveCode);
                        req_InvoiceCreate.put("buyerGuiNo", buyerGuiNo);
                        req_InvoiceCreate.put("bDate", accountDate);
                        req_InvoiceCreate.put("invType", invoiceType);//发票类型：0园区收据，2二联，3三联，4收据，5二联式收银机发票，6三联式收银机发票，X不申报，7电子发票
                        req_InvoiceCreate.put("carrierShowId", carrierShowId);
                        req_InvoiceCreate.put("carrierHiddenId", carrierHiddenId);
                        datas_invoiceCreateList.put(req_InvoiceCreate);

                        request_InvoiceCreate.put("invoiceList", datas_invoiceCreateList);
                        request_InvoiceCreate.put("goodsList", datas_goodsList);
                        request_InvoiceCreate.put("payList", datas_payList);

                        //订单商品
                        String sqlOrder_detail="select * from dcp_order_detail a where a.eid='"+eId+"' and a.ORDERNO='"+orderNo+"' ";
                        List<Map<String, Object>> getData_Order_detail=this.doQueryData(sqlOrder_detail, null);
                        if (getData_Order_detail==null || getData_Order_detail.isEmpty())
                        {
                            res.setSuccess(false);
                            res.setServiceStatus("100");
                            res.setServiceDescription("找不到eId:"+eId+",订单号："+orderNo +"的商品信息！");
                            return;
                        }

                        //订单付款
                        String sqlOrder_paydetail="select * from dcp_order_pay_detail a where a.eid='"+eId+"' and a.sourcebillno='"+orderNo+"' ";
                        List<Map<String, Object>> getData_Order_paydetail=this.doQueryData(sqlOrder_paydetail, null);
                        if (getData_Order_paydetail==null || getData_Order_paydetail.isEmpty())
                        {
                            res.setSuccess(false);
                            res.setServiceStatus("100");
                            res.setServiceDescription("找不到eId:"+eId+",订单号："+orderNo +"的付款信息！");
                            return;
                        }

                        for (Map<String, Object> mapGoods : getData_Order_detail)
                        {
                            //零售单/订单：不包含套餐子商品
                            //套餐类型（1、正常商品 2、套餐主商品  3、套餐子商品）
                            if (mapGoods.get("PACKAGETYPE").toString().equals("1") ||mapGoods.get("PACKAGETYPE").toString().equals("2"))
                            {
                                JSONObject req_goods = new JSONObject();
                                req_goods.put("invItem", "1");//发票项次
                                req_goods.put("invNo", mapGoods.get("INVNO").toString());
                                req_goods.put("oItem", mapGoods.get("ITEM").toString());
                                req_goods.put("pluNo", mapGoods.get("PLUNO").toString());
                                req_goods.put("pluName", mapGoods.get("PLUNAME").toString());
                                req_goods.put("inclTax", mapGoods.get("INCLTAX").toString());
                                req_goods.put("taxRate", mapGoods.get("TAXRATE").toString());
                                req_goods.put("qty", mapGoods.get("QTY").toString());
                                req_goods.put("amt", mapGoods.get("AMT").toString());
                                datas_goodsList.put(req_goods);
                            }
                        }

                        for (Map<String, Object> mapPaydetail : getData_Order_paydetail)
                        {
                            JSONObject req_pay = new JSONObject();
                            req_pay.put("payCode", mapPaydetail.get("PAYCODE").toString());
                            req_pay.put("payName", mapPaydetail.get("PAYNAME").toString());
                            req_pay.put("payCodeErp", mapPaydetail.get("PAYCODEERP").toString());
                            req_pay.put("payAmt", mapPaydetail.get("PAY").toString());
                            req_pay.put("sendPayAmt", mapPaydetail.get("SENDPAY").toString());//支付金额-赠送部分：常见卡赠送金额，券售价折扣部分
                            req_pay.put("isOrderPay", "N");//是否订金冲销Y/N
                            req_pay.put("isTurnover", "Y");//是否纳入营业额Y/N
                            req_pay.put("canOpenInvoice", mapPaydetail.get("CANINVOICE").toString());//开票方式：0不可开票 1可开票 2已开票 3第三方已开票
                            req_pay.put("extra", mapPaydetail.get("EXTRA").toString());
                            req_pay.put("change", mapPaydetail.get("CHANGED").toString());
                            datas_payList.put(req_pay);
                        }

                        String str_invoiceCreate=request_InvoiceCreate.toString();
                        //
                        map = new HashMap<>();
                        map.put("serviceId", "POS_InvoiceCreate_Open");
                        map.put("apiUserCode",Yc_Key);
                        map.put("sign",PosPub.encodeMD5(str_invoiceCreate + Yc_Sign_Key));
                        map.put("langType",req.getLangType());
                        map.put("requestId",PosPub.getGUID(false));
                        map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                        map.put("version","3.0");

                        PosPub.WriteETLJOBLog("开发票请求POS_InvoiceCreate_Open： "+ str_invoiceCreate);

                        String resbody_invoiceCreate=HttpSend.doPost(posUrl,str_invoiceCreate,map,"");
                        map.clear();
                        map=null;

                        PosPub.WriteETLJOBLog("开发票返回POS_InvoiceCreate_Open： "+ resbody_invoiceCreate);

                        if (resbody_invoiceCreate.equals("")==false)
                        {
                            JSONObject jsonres_InvoiceCreate = new JSONObject(resbody_invoiceCreate);
                            success = jsonres_InvoiceCreate.getBoolean("success");
                            if (success)
                            {
                                JSONObject jsonres_datas=jsonres_InvoiceCreate.getJSONObject("datas");

                                JSONArray invoiceList=jsonres_datas.getJSONArray("invoiceList");

                                if (jsonres_datas != null && invoiceList.length()>0)
                                {
                                    //发票信息
                                    String invoiceDate=invoiceList.getJSONObject(0).getString("invoiceDate");//开票日期YYYYMMDD
                                    int OperateType=invoiceList.getJSONObject(0).getInt("invOperateType");//0-开立 1-作废 2-折让
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
                                    ub_DCP_ORDER_Invoice.addUpdateValue("INVOPERATETYPE", new DataValue(OperateType, Types.VARCHAR));//
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
                                    //已开发票或已折让发票都直接返回查询发票结果
                                    ParseJson pj = new ParseJson();
                                    DCP_OrderInvoiceRes.Data myData=pj.jsonToBean(jsonres_datas.toString(),new TypeToken<DCP_OrderInvoiceRes.Data>(){});
                                    pj=null;
                                    res.setDatas(myData);

                                    res.setSuccess(true);
                                    res.setServiceStatus("000");
                                    res.setServiceDescription("服务执行成功！");
                                }
                            }
                            else
                            {
                                //开发票失败
                                res.setSuccess(false);
                                res.setServiceStatus("100");
                                String tempDesc=jsonres_InvoiceCreate.get("serviceDescription")==null||jsonres_InvoiceCreate.get("serviceDescription").toString().equals("null")?"null":jsonres_InvoiceCreate.get("serviceDescription").toString();
                                res.setServiceDescription("订单号："+orderNo+"调用开发票失败，接口返回为：" +tempDesc);

                                //记录发票异常档
                                DataProcessBean dpbBean=InvoiceCreate(eId, orderNo, req.getOpNO(), req.getOpName(),"调用开发票失败，接口返回为：" +tempDesc);
                                if (dpbBean!=null)
                                {
                                    data.add(dpbBean);
                                }
                            }
                        }
                        else
                        {
                            //开发票失败
                            res.setSuccess(false);
                            res.setServiceStatus("100");
                            res.setServiceDescription("订单号："+orderNo+"调用开发票失败，接口返回为空：");
                            //记录发票异常档
                            DataProcessBean dpbBean=InvoiceCreate(eId, orderNo, req.getOpNO(), req.getOpName(),"调用开发票失败，接口返回为空：");
                            if (dpbBean!=null)
                            {
                                data.add(dpbBean);
                            }
                        }

                        //调用开发票后的执行
                        dao.useTransactionProcessData(data);
                        return;
                    }
                    else//折让发票查询
                    {
                        /*
                        //退发票这里不需要，仅仅用于测试
                        JSONObject request_InvoiceRefund = new JSONObject();
                        request_InvoiceRefund.put("saleType","Order");//单据类型：Sale销售单，Order订单，Card售卡 Coupon售券，Recharge储值
                        request_InvoiceRefund.put("oriSaleNo",orderNo);
                        request_InvoiceRefund.put("refundSaleNo","RE" + orderNo);
                        request_InvoiceRefund.put("reasonCode","TEST");
                        request_InvoiceRefund.put("reason","TEST");
                        request_InvoiceRefund.put("InvOperateType","2");
                        String str_InvoiceRefund=request_InvoiceRefund.toString();
                        //
                        map = new HashMap<>();
                        map.put("serviceId", "POS_InvoiceRefund_Open");
                        map.put("apiUserCode",Yc_Key);
                        map.put("sign", PosPub.encodeMD5(str_InvoiceRefund + Yc_Sign_Key));
                        posUrl = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "PosUrl");

                        map.put("langType",req.getLangType());
                        map.put("requestId",PosPub.getGUID(false));
                        map.put("timestamp",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                        map.put("version","3.0");

                        PosPub.WriteETLJOBLog("调用退发票接口POS_InvoiceRefund_Open req： "+ str_InvoiceRefund);

                        String resbody_InvoiceRefund= HttpSend.doPost(posUrl, str_InvoiceRefund, map);
                        map.clear();
                        map=null;

                        PosPub.WriteETLJOBLog("调用退发票接口POS_InvoiceRefund_Open res： "+ resbody_InvoiceRefund);

                        */

                        //
                        res.setSuccess(false);
                        res.setServiceStatus("100");
                        res.setServiceDescription("订单号："+orderNo+"，找不到对应的折让发票记录！");
                        return;
                    }
                }
            }
            else
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("调用发票查询接口POS_InvoiceQuery_Open失败！");
                return;
            }
        }
        else
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("调用发票查询接口POS_InvoiceQuery_Open失败！\r\n 接口地址URL=" + posUrl);
            return;
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderInvoiceReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderInvoiceReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderInvoiceReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderInvoiceReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getOrderNo()))
        {
            errCt++;
            errMsg.append("订单号orderNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getInvOperateType()))
        {
            errCt++;
            errMsg.append("操作类型invOperateType不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderInvoiceReq> getRequestType()
    {
        return new TypeToken<DCP_OrderInvoiceReq>(){};
    }

    @Override
    protected DCP_OrderInvoiceRes getResponseType()
    {
        return new DCP_OrderInvoiceRes();
    }


    /**
     * 订转销开发票异常档
     * @param eId
     * @param orderNo
     * @param opNo
     * @param opName
     * @return
     */
    private DataProcessBean InvoiceCreate(String eId,String orderNo,String opNo,String opName,String memo)
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
                        new DataValue("订单开发票", Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(memo, Types.VARCHAR),//备注
                        new DataValue("0", Types.VARCHAR),
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
