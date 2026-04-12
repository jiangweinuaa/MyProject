package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.foreign.erp.request.OrderPayRefundCreateRequest;
import com.dsc.spos.scheduler.job.HolidayShoporderCreate_V3.Wrapper;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ConvertUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;

import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 上传订单支付信息至ERP (不允许并发执行)
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderPayRefundCreate_V3 extends InitJob
{

    private String AreaType="";

    public String doExe() throws Exception
    {
        String sReturnInfo = "";
        log("【同步任务OrderPayRefundCreate_V3】同步START！");
        int loopCount = 0;
        try
        {
            // 1、提取数据
            List<Wrapper<OrderPayRefundCreateRequest.RequestBean>> payList = fetchTop20PayList();
            if (payList == null || payList.isEmpty())
            {
                log("上传订单付款 orderpayrefund.create请求T100无数据");
            }

            // 2、上传订单到ERP
            while (!payList.isEmpty() && ++loopCount <= 10)
            {
                for (Wrapper<OrderPayRefundCreateRequest.RequestBean> order : payList)
                {
                    sReturnInfo = uploadOrderPayRefund(order);
                }
                payList = fetchTop20PayList();
            }

            //
            if (payList != null)
            {
                payList.clear();
                payList=null;
            }

        }
        catch (Exception e)
        {
            log("上传订单付款orderpayrefund.create异常" + e.toString());
        }
        finally
        {
            log("【同步任务OrderPayRefundCreate_V3】同步End！");
        }

        return sReturnInfo;
    }

    private List<Wrapper<OrderPayRefundCreateRequest.RequestBean>> fetchTop20PayList() throws Exception
    {
        List<Wrapper<OrderPayRefundCreateRequest.RequestBean>> payList = new ArrayList<>();
        List<Wrapper<OrderPayRefundCreateRequest.OrderPayDetailBean>> detailList = new ArrayList<>();

        //发票
        List<Wrapper<OrderPayRefundCreateRequest.RequestBeanInvoice>> invoiceList = new ArrayList<>();
        List<Wrapper<OrderPayRefundCreateRequest.RequestBeanInvoice_Detail>> invoiceDetailList = new ArrayList<>();
        // 单头
		/*String sql = "select a.*, b.ISINVOICE, b.SHOP as SHOPID"
                + " from DCP_ORDER_PAY a "
                + " join DCP_ORDER b on a.EID = b.EID and a.SOURCEBILLTYPE = 'Order' and a.SOURCEBILLNO = b.ORDERNO "
                + " where a.process_status <>'Y' and a.SOURCEBILLTYPE = 'Order' and a.USETYPE != 'final'"
                + " and ROWNUM <= 20 ";*/
        String waimaiDocTypeCon = "'"+orderLoadDocType.ELEME+"','"+orderLoadDocType.MEITUAN+"','"+orderLoadDocType.JDDJ+"','"+orderLoadDocType.MTSG+"','"+orderLoadDocType.DYWM+"'";
        //增加订单上传ERP的白名单管控  BY JZMA 20201203
        StringBuffer sql = new StringBuffer(" select * from ("
                                                    + "select a.*,row_number()over (order by a.loaddoctype desc, a.billdate desc) rn,b.SHOP SOURCESHOPID, b.ISINVOICE,b.DEPARTNO "
                                                    + " from DCP_ORDER_PAY a "
                                                    + " join DCP_ORDER b on a.EID = b.EID and a.SOURCEBILLTYPE = 'Order' and a.SOURCEBILLNO = b.ORDERNO "
                                                    + " left join dcp_orderbasicsetting c on b.eid=c.eid and c.settingno='orderTransferErp' and c.status='100' "
                                                    + " left join dcp_ordertransfererpset d on b.eid=d.eid and b.shop=d.shop "
                                                    + " where (a.process_status <> 'Y' or INVOICEPROCESS_STATUS<>'Y') and a.process_status <> 'E' "
                                                    + "  and a.SOURCEBILLTYPE = 'Order' and a.USETYPE != 'final'"
                                                    + " and (c.settingvalue='N' or c.settingvalue is null or (c.settingvalue='Y' and d.shop is not null)) "
                                                    + " and (b.Loaddoctype not in ("+waimaiDocTypeCon+") or (b.Loaddoctype in ("+waimaiDocTypeCon+") and b.shop<>' ' and b.Shop is not null))"
                                                    + " and b.DOWNGRADED<>'Y' "
                                                    + " and b.process_status='Y'"
                                                    + " ) where rn<=200 ");

        String eId="";
        log("查询订单付款上传sql：" + sql.toString());
        List<Map<String, Object>> data = this.doQueryData(sql.toString(), null);
        for (Map<String, Object> map : data)
        {
            eId=map.get("EID").toString();

            OrderPayRefundCreateRequest.RequestBean b = ConvertUtils.mapToBean(map, OrderPayRefundCreateRequest.RequestBean.class);
            b.setVersion("3.0");
            b.setDepartNo(map.getOrDefault("DEPARTNO", "").toString());
            Wrapper<OrderPayRefundCreateRequest.RequestBean> w = new Wrapper<OrderPayRefundCreateRequest.RequestBean>(
                    map.get("EID").toString(), map.get("BILLNO").toString(),map.get("SOURCEBILLNO").toString(), b);
            payList.add(w);

            //
            w=null;
            b=null;
        }

        if (!payList.isEmpty())
        {
            String inSql = "(" + payList.stream().map(Wrapper::getInKey).collect(Collectors.joining(",")) + ")";
            // 单身
            String sql_PAY_DETAIL = "select a.* from DCP_ORDER_PAY_DETAIL a where (a.EID, a.BILLNO) in " + inSql;
            data = this.doQueryData(sql_PAY_DETAIL, new String[]{});

            for (Map<String, Object> mapdetail : data)
            {
                OrderPayRefundCreateRequest.OrderPayDetailBean d = ConvertUtils.mapToBean(
                        mapdetail, OrderPayRefundCreateRequest.OrderPayDetailBean.class);
                d.setSeq(mapdetail.get("ITEM").toString());
                String sendPay = mapdetail.getOrDefault("SENDPAY","").toString();
                if (sendPay==null||sendPay.isEmpty())
                {
                    sendPay = "0";
                }
                d.setSendPay(sendPay);
                String couponPrice = mapdetail.getOrDefault("COUPONPRICE","").toString();
                if (couponPrice==null||couponPrice.isEmpty())
                {
                    couponPrice = "0";
                }
                d.setCouponPrice(couponPrice);

                String couponMarketPrice = mapdetail.getOrDefault("COUPONMARKETPRICE","").toString();
                if (couponMarketPrice==null||couponMarketPrice.isEmpty())
                {
                    couponMarketPrice = "0";
                }
                d.setCouponMarketPrice(couponMarketPrice);

                Wrapper<OrderPayRefundCreateRequest.OrderPayDetailBean> wrapper = new Wrapper<OrderPayRefundCreateRequest.OrderPayDetailBean>
                        (mapdetail.get("EID").toString(), mapdetail.get("BILLNO").toString(),
                         mapdetail.get("SOURCEBILLNO").toString(),
                         mapdetail.get("MERDISCOUNT").toString(),
                         mapdetail.get("MERRECEIVE").toString(),
                         mapdetail.get("THIRDDISCOUNT").toString(),
                         mapdetail.get("CUSTPAYREAL").toString() , d);

                detailList.add(wrapper);

                //
                wrapper=null;
                d=null;
            }

            //台湾环境判断
            AreaType=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "AreaType");
            if(AreaType!=null&&AreaType.equals("TW"))
            {
                String v_inSql = "(" + payList.stream().map(Wrapper::getInSalenoKey).collect(Collectors.joining(",")) + ")";

                String sql_INVOICE = "SELECT EID,BILLNO,COMPANYID，SHOPID，SALETYPE，SALENO，INVSPLITTYPE，BDATE，MACHINE machineId，SDATE,STIME," +
                        "RECORDTYPE,INVNO,INVFNO,INVMEMO,SELLERGUINO,INVYEAR,INVSTARTMON,INVENDMON,TAXATIONTYPE,INVTYPE,INVFORMAT," +
                        "BUYERGUINO,TOTAMT,FREETAXAMT,ZEROTAXAMT,TAXABLEAMT,TAXRATE,TAXABLEUAMT,TAXABLETAX,PAYAMT,UNTAXPAYAMT," +
                        "UNTAXPAYTAX,GFTINVAMT,GFTINVTAX,EXTRACPAMT,EXTRACPTAX,INVTOTAMT,INVFREEAMT,INVZEROAMT,INVAMT,INVUAMT," +
                        "INVTAX ,ACCUMAMT accAmt,ACCUMTAX accTax,ISEINVOICE,CARRIERCODE,CARRIERSHOWID,CARRIERHIDDENID,LOVECODE," +
                        "RANDOMCODE,PRINTCOUNT,OSDATE,REBATENO,REBATEAMT,INVALIDOP,INVALIDCODE,INVALIDNAME,ISNULLIFIED,STATUS," +
                        "TRAN_TIME,INVCHECKSTATUS,INVCHECKMEMO,UPDATE_TIME FROM DCP_INVOICE a WHERE a.SALETYPE='Order' and (a.EID, a.SALENO) in " + v_inSql;
                data = this.doQueryData(sql_INVOICE, null);

                for (Map<String, Object> mapdetail : data)
                {
                    OrderPayRefundCreateRequest.RequestBeanInvoice d=ConvertUtils.mapToBean(mapdetail, OrderPayRefundCreateRequest.RequestBeanInvoice.class);
                    Wrapper<OrderPayRefundCreateRequest.RequestBeanInvoice> wrapper = new Wrapper<OrderPayRefundCreateRequest.RequestBeanInvoice>
                            (mapdetail.get("EID").toString(), mapdetail.get("BILLNO").toString(),mapdetail.get("SALENO").toString(), d);

                    try
                    {
                        //发票明细
                        String sqlDetail = "SELECT  EID,BILLNO ,ITEM ,INVNO,BDATE,SDATE ,STIME ,OITEM,CONTENT ,QTY ,AMT ,FREETAXAMT,ZEROTAXAMT,TAXABLEAMT,TAXRATE ," +
                                "TAXABLEUAMT ,TAXABLETAX,PAYAMT,UNTAXPAYAMT ,UNTAXPAYTAX ,GFTINVAMT ,GFTINVTAX ,EXTRACPAMT,EXTRACPTAX," +
                                "INVTOTAMT ,INVAMT,INVUAMT ,INVTAX,ACCUMAMT accAmt,ACCUMTAX accTax FROM DCP_INVOICE_GOODS WHERE EID='"+mapdetail.get("EID").toString()+"' and BILLNO='"+mapdetail.get("BILLNO").toString()+"' ";
                        List<Map<String, Object>> dataDetail = this.doQueryData(sqlDetail, null);

                        for (Map<String, Object> mapinvoiceDeatil : dataDetail)
                        {
                            OrderPayRefundCreateRequest.RequestBeanInvoice_Detail dDetail=ConvertUtils.mapToBean(mapinvoiceDeatil, OrderPayRefundCreateRequest.RequestBeanInvoice_Detail.class);
                            Wrapper<OrderPayRefundCreateRequest.RequestBeanInvoice_Detail> wrapperDetail = new Wrapper<OrderPayRefundCreateRequest.RequestBeanInvoice_Detail>
                                    (mapinvoiceDeatil.get("EID").toString(), mapinvoiceDeatil.get("BILLNO").toString(),mapinvoiceDeatil.get("ITEM").toString(), dDetail);
                            //
                            wrapper.getObj().getInvoiceInfo_detail().add(wrapperDetail.getObj());

                            //
                            wrapperDetail=null;
                            dDetail=null;
                        }
                    }
                    catch (Exception ex)
                    {
                        log("上传订单付款orderpayrefund.create处理发票数据异常：" + ex.getMessage());
                    }
                    invoiceList.add(wrapper);
                }

            }

            // 组装数据
            Map<String, Wrapper<OrderPayRefundCreateRequest.RequestBean>> headerMap = payList.stream().collect(Collectors.toMap(k -> k.getKey(), v -> v));

            //
            for (Wrapper<OrderPayRefundCreateRequest.OrderPayDetailBean> it : detailList)
            {
                Wrapper<OrderPayRefundCreateRequest.RequestBean> w = headerMap.get(it.getKey());
                if (w != null)
                {
                    w.getObj().getOrderPay_detail().add(it.getObj());
                }
                w=null;
            }


            if(AreaType!=null&&AreaType.equals("TW"))
            {
                //组装数据	-发票
                Map<String, Wrapper<OrderPayRefundCreateRequest.RequestBean>> headerMapInVoice = payList.stream().collect(Collectors.toMap(k -> k.getSalenoKey(), v -> v));

                for (Wrapper<OrderPayRefundCreateRequest.RequestBeanInvoice> it : invoiceList)
                {
                    Wrapper<OrderPayRefundCreateRequest.RequestBean> w = headerMapInVoice.get(it.getSalenoKey());
                    if (w != null)
                    {
                        w.getObj().getInvoiceInfo().add(it.getObj());
                    }
                    w=null;
                }
            }


        }

        return payList;
    }

    private String uploadOrderPayRefund(Wrapper<OrderPayRefundCreateRequest.RequestBean> wrapper)
    {
        String sReturnInfo = "";
        OrderPayRefundCreateRequest.RequestBean header = wrapper.getObj();
        String eId = wrapper.geteId();
        String rShopId = header.getShopId();
        String organizationNO = header.getShopId();
        String porderNO100 = header.getBillNo();


        //是否有发票上传
        boolean bInvoiceStatus=header.getInvoiceInfo().size()>0?true:false;

        //台湾环境,单头传过了,但是发票没信息就别传了,等有发票了再传
        if(AreaType!=null&&AreaType.equals("TW"))
        {
            if (header.getProcess_status()!=null && header.getProcess_status().equals("Y") && bInvoiceStatus==false)
            {
                return null;
            }
        }
        else
        {
            //大陆环境历史数据可能发票上传标记为N,直接改成Y避免下次再捞到历史数据
            if (header.getProcess_status()!=null && header.getProcess_status().equals("Y"))
            {
                maintainProcessStatus(eId, organizationNO, porderNO100, "Y", "上传ERP成功",bInvoiceStatus);
                return "0";
            }
        }

        //这里先处理一下退单的原单号,之前有些单据是没有给值的
        if (header.getSourceBillNo()!=null && header.getSourceBillNo().startsWith("RE") &&
                (header.getSourceHeadBillNo()==null || header.getSourceHeadBillNo().equals("")))
        {
            String sqlSourceno="select REFUNDSOURCEBILLNO from dcp_order where orderno='"+header.getSourceBillNo()+"' AND EID='"+eId+"' ";
            try
            {
                List<Map<String, Object>> data_Source = this.doQueryData(sqlSourceno, null);

                if (data_Source!=null && data_Source.size()>0)
                {
                    //退单的原单号
                    header.setSourceHeadBillNo(data_Source.get(0).get("REFUNDSOURCEBILLNO").toString());

                    //退单的原订单未上传！
                    String sqlOrder="select PROCESS_STATUS from dcp_order where orderno='"+header.getSourceHeadBillNo()+"' AND EID='"+eId+"' ";
                    List<Map<String, Object>> data_Order = this.doQueryData(sqlOrder, null);
                    if (data_Order.get(0).get("PROCESS_STATUS")==null || data_Order.get(0).get("PROCESS_STATUS").toString().equals("Y")==false)
                    {
                        log("REFUNDSOURCEBILLNO=" + header.getSourceHeadBillNo()+",退单的原订单未上传！");
                        return "退单的原订单未上传！";
                    }

                    log("REFUNDSOURCEBILLNO=" + header.getSourceHeadBillNo());

                    //
                    if (data_Order != null)
                    {
                        data_Order.clear();
                        data_Order=null;
                    }
                }

                //
                if (data_Source != null)
                {
                    data_Source.clear();
                    data_Source=null;
                }

            }
            catch (Exception e)
            {

            }
        }

        String str = "{\"std_data\":{\"parameter\":{\"orderPay\":["+JSON.toJSONString(header)+"]}}}";


        log("上传订单付款orderpayrefund.create请求T100传入参数：" + str);


        // region 写下日志

        List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

        orderStatusLog onelv1 = new orderStatusLog();

        onelv1.setLoadDocType(header.getLoadDocType());
        onelv1.setChannelId(header.getChannelId());

        onelv1.setNeed_callback("N");
        onelv1.setNeed_notify("N");

        onelv1.seteId(eId);

        String opNO = "";

        String o_opName = "系统自动";

        onelv1.setOpNo(opNO);
        onelv1.setOpName(o_opName);
        onelv1.setOrderNo(header.getSourceBillNo());
        onelv1.setLoadDocBillType("");
        onelv1.setLoadDocOrderNo("");

        String statusType = "998";// 其他状态
        String updateStaus = "998";// 订单修改

        onelv1.setStatusType(statusType);
        onelv1.setStatus(updateStaus);

        String statusName = "付款上传";
        String statusTypeName = "其他状态";
        onelv1.setStatusTypeName(statusTypeName);
        onelv1.setStatusName(statusName);

        StringBuffer memo_s = new StringBuffer("");
        memo_s .append( statusTypeName + "-->" + statusName + "<br>");
        // endregion


        String resbody = "";
        try
        {
            resbody = HttpSend.Send(str, "orderpayrefund.create", eId, rShopId, organizationNO, porderNO100);
            log("上传订单付款orderpayrefund.create请求T100返回数据：" + resbody);
            if (Check.Null(resbody) || resbody.isEmpty())
            {
                memo_s .append(statusName+"失败(返回为空)");
                onelv1.setMemo(memo_s.toString());
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);

                StringBuilder errorMessage = new StringBuilder();
                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                if (nRet_s)
                {
                    HelpTools.writelog_fileName("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                } else
                {
                    HelpTools.writelog_fileName(
                            "【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                }


                return null;
            }

            JSONObject jsonres = new JSONObject(resbody);
            JSONObject std_data_res = jsonres.getJSONObject("std_data");
            JSONObject execution_res = std_data_res.getJSONObject("execution");
            String code = execution_res.getString("code");

            String description = "";
            if (!execution_res.isNull("description"))
            {
                description = execution_res.getString("description");
            }
            if (code.equals("0"))
            {
                maintainProcessStatus(eId, organizationNO, porderNO100, "Y", "上传ERP成功",bInvoiceStatus);
                sReturnInfo = "0";

                memo_s .append(statusName+"成功");
                onelv1.setMemo(memo_s.toString());
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);

                StringBuilder errorMessage = new StringBuilder();
                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                if (nRet_s)
                {
                    HelpTools.writelog_fileName("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                } else
                {
                    HelpTools.writelog_fileName(
                            "【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                }
            }
            else
            {
                sReturnInfo = "ERP返回错误信息:" + code + "," + description;
                //写数据库
                InsertWSLOG.insert_WSLOG("orderpayrefund.create", porderNO100, eId, organizationNO, "1", str, resbody, code, description);

                memo_s .append(statusName+"失败("+sReturnInfo+")");
                onelv1.setMemo(memo_s.toString());
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);

                StringBuilder errorMessage = new StringBuilder();
                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                if (nRet_s)
                {
                    HelpTools.writelog_fileName("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                } else
                {
                    HelpTools.writelog_fileName(
                            "【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                }

            }

            //
            execution_res=null;
            std_data_res=null;
            jsonres=null;
        }
        catch (Exception e)
        {
            sReturnInfo = "错误信息:" + e.getMessage();
            log("上传订单付款orderpayrefund.create上传异常：" + sReturnInfo);

            try
            {
                InsertWSLOG.insert_WSLOG("orderpayrefund.create", porderNO100, eId, organizationNO, "1", str, resbody, "-1", e.getMessage());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            try
            {
                memo_s .append(statusName+"失败("+sReturnInfo+")");
                onelv1.setMemo(memo_s.toString());
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                orderStatusLogList.add(onelv1);

                StringBuilder errorMessage = new StringBuilder();
                boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
                if (nRet_s)
                {
                    HelpTools.writelog_fileName("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                } else
                {
                    HelpTools.writelog_fileName(
                            "【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + header.getSourceBillNo(),jddjLogFileName);
                }

            }
            catch (Exception e2)
            {

            }
        }

        onelv1=null;
        orderStatusLogList.clear();
        orderStatusLogList=null;
        header=null;

        return sReturnInfo;
    }

    private void maintainProcessStatus( String eId, String organizationNO, String orderNo,  String process_status, String message,boolean bInvoiceStatus )
    {
        try
        {
            // values
            Map<String, DataValue> values = new HashMap<String, DataValue>();
            DataValue v = new DataValue(process_status, Types.VARCHAR);
            values.put("process_status", v);
            DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR);
            values.put("TRAN_TIME", v1);
            values.put("UPDATE_TIME", v1);
            if(AreaType!=null&&AreaType.equals("TW"))
            {
                //台湾有发票记录
                if (bInvoiceStatus)
                {
                    DataValue v2 = new DataValue("Y", Types.VARCHAR);
                    values.put("INVOICEPROCESS_STATUS", v2);
                }
            }
            else
            {
                //大陆无发票,直接更新成功
                DataValue v2 = new DataValue("Y", Types.VARCHAR);
                values.put("INVOICEPROCESS_STATUS", v2);
            }

            // condition
            Map<String, DataValue> conditions = new HashMap<String, DataValue>();
            DataValue c2 = new DataValue(eId, Types.VARCHAR);
            conditions.put("EID", c2);
            DataValue c3 = new DataValue(orderNo, Types.VARCHAR);
            conditions.put("BILLNO", c3);
            this.doUpdate("DCP_ORDER_PAY", values, conditions);
            
            
          //删除WS日志  By jzma 20201120
			String deleteShop =organizationNO;
			if (Check.Null(deleteShop))
				deleteShop=" ";
			InsertWSLOG.delete_WSLOG(eId, deleteShop,"1",orderNo);
        }
        catch (Exception e)
        {
            log("维护处理状态异常：" + e.getMessage());
        }
    }

    public static final String jddjLogFileName = "OrderPayRefundCreate_V3";
    public void log(String message) {
        try {
            HelpTools.writelog_fileName(message, jddjLogFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Wrapper<T>{
        private String eId;
        private String billNo;
        private String orderNo;
        private String shopId;
        private String sourceBillNo;
        private String saleNo;
        private String merDiscount;
        private String merReceive;
        private String thirdDiscount;
        private String custPayReal;

        private T obj;
        public Wrapper() {
        }



        public Wrapper(String eId, String billNo,String saleNo) {
            this.eId = eId;
            this.billNo = billNo;
            this.saleNo=saleNo;
        }

        public Wrapper(String eId, String billNo,String saleNo,String merDiscount,String merReceive,String thirdDiscount,String custPayReal,T obj) {
            this.eId = eId;
            this.billNo = billNo;
            this.saleNo=saleNo;
            this.merDiscount = merDiscount;
            this.merReceive=merReceive;
            this.thirdDiscount=thirdDiscount;
            this.custPayReal=custPayReal;
            this.obj = obj;
        }


        public Wrapper(String eId, String billNo,String saleNo, T obj) {
            this.eId = eId;
            this.billNo = billNo;
            this.saleNo=saleNo;
            this.obj = obj;
        }

        public String getKey() {
            String key = eId + "_" + billNo;
            return key;
        }
        public String getInKey(){
            return  "('" + eId + "','" + billNo +"')";
        }

        public String getSalenoKey() {
            String key = eId + "_" + saleNo;
            return key;
        }

        public String getInSalenoKey(){
            return  "('" + eId + "','" + saleNo +"')";
        }

        public String geteId() {
            return eId;
        }

        public void seteId(String eId) {
            this.eId = eId;
        }

        public T getObj() {
            return obj;
        }

        public void setObj(T obj) {
            this.obj = obj;
        }

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String getSaleNo()
        {
            return saleNo;
        }

        public void setSaleNo(String saleNo)
        {
            this.saleNo = saleNo;
        }

        public String getSourceBillNo() {
            return sourceBillNo;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public void setSourceBillNo(String sourceBillNo) {
            this.sourceBillNo = sourceBillNo;
        }

        public String getMerDiscount() {
            return merDiscount;
        }

        public void setMerDiscount(String merDiscount) {
            this.merDiscount = merDiscount;
        }

        public String getMerReceive() {
            return merReceive;
        }

        public void setMerReceive(String merReceive) {
            this.merReceive = merReceive;
        }

        public String getThirdDiscount() {
            return thirdDiscount;
        }

        public void setThirdDiscount(String thirdDiscount) {
            this.thirdDiscount = thirdDiscount;
        }

        public String getCustPayReal() {
            return custPayReal;
        }

        public void setCustPayReal(String custPayReal) {
            this.custPayReal = custPayReal;
        }
    }
}
