package com.dsc.spos.utils.invoice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.model.EInvoiceRequest;
import com.dsc.spos.model.EInvoiceResponse;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.invoice.order.OrderFacade;
import com.dsc.spos.utils.invoice.order.OrderFacadeAgent;
import com.dsc.spos.utils.invoice.order.dto.Order;
import com.dsc.spos.utils.invoice.order.dto.OrderItem;
import com.dsc.spos.utils.invoice.order.dto.Result;
import com.dsc.spos.waimai.HelpTools;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 发票接口
 * @author: wangzyc
 * @create: 2022-02-25
 */
public class InvoiceService {
    Logger logger = LogManager.getLogger(InvoiceService.class.getName());
    
    // 瑞宏专用
    private static OrderFacade orderFacade = null;
    // 瑞宏发起创建订单后的处理结果
    private static String code= "";
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdff = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat sdfh = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    
    /**
     * 通过订单查询开票信息
     *
     * @param dao
     * @param json
     * @param platformType 发票平台类型
     * @return
     */
    public String invoiceQueryInfoByOrder(DsmDAO dao, String json, String platformType) {
        
        String res = "";
        try {
            HelpTools.writelog_fileName(
                    "*********** 调用通过订单查询开票信息 invoiceQueryInfoByOrder,请求JSON:" + json + " *************",
                    "invoiceService");
            EInvoiceRequest request = new EInvoiceRequest();
            if (!Check.Null(json)) {
                com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(json);
                request = JSON.toJavaObject(jsonObj, EInvoiceRequest.class);
            }
            // 业务单据类型：Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值 ;鼎捷开票页面入口时必传；
            String billtype = request.getBilltype();
            String templateid = request.getTemplateid();
            String querytype = request.getQuerytype();
            String operater = request.getOperater();
            String apply = request.getApply();
            String langType = request.getLangType();
            if (Check.Null(querytype)) {
                querytype = "1";
            }
            String shopid = request.getShopid();
            String eId = "";
            MyCommon myCommon = new MyCommon();
            List<String> orderNos = new ArrayList<>();
            String ischecklimitation = request.getIschecklimitation();
            
            /**
             * 根据单个发票单号查询
             */
            if ("queryInfoByOrderId".equals(operater)) {
                
                /**
                 * 诺诺发票
                 */
                if ("NUONUO_V1".equals(platformType)) {
                    
                    if ("1".equals(querytype) || "0".equals(querytype)) {
                        // 查可开票信息
                        EInvoiceResponse eInvoiceResponse = new EInvoiceResponse();
                        
                        String orderno = request.getOrderno();
                        orderNos.add(orderno);
                        
                        String einvoiceInfoSql = getEinvoiceInfoSql(orderNos);
                        List<Map<String, Object>> getEinvoiceInfoDatas = dao.executeQuerySQL(einvoiceInfoSql, null);
                        List<String> sourceBillNos = new ArrayList<>();
                        String invoicedate = "";
                        String isApply = "";
                        if (!CollectionUtils.isEmpty(getEinvoiceInfoDatas)) {
                            Map<String, Object> getEinvoiceInfo = getEinvoiceInfoDatas.get(0);
                            eId = getEinvoiceInfo.get("EID").toString();
                            shopid = getEinvoiceInfo.get("SOURCESHOPID").toString();
                            billtype = getEinvoiceInfo.get("SOURCEBILLTYPE").toString();
                            invoicedate = getEinvoiceInfo.get("CREATETIME").toString();
                            isApply = getEinvoiceInfo.get("ISAPPLY").toString();
                            
                            for (Map<String, Object> getEinvoiceInfoData : getEinvoiceInfoDatas) {
                                String sourcebillno = getEinvoiceInfoData.get("SOURCEBILLNO").toString();
                                sourceBillNos.add(sourcebillno);
                            }
                        }
                        String sql = getInvoiceParmsSql(eId, templateid, shopid);
                        List<Map<String, Object>> getPARAMSs = dao.executeQuerySQL(sql.toString(), null);
                        List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();
                        if (!CollectionUtils.isEmpty(getPARAMSs)) {
                            // 优先取门店 取第一个
                            Map<String, Object> getParams = getPARAMSs.get(0);
                            String platformtype = getParams.get("PLATFORMTYPE").toString();
                            String params = getParams.get("PARAMS").toString();
                            Map<String, String> invoiceParmsMap = myCommon.invoiceParmsStringToMap(params);
                            String appKey = invoiceParmsMap.getOrDefault("appKey", ""); // 企业6位代码
                            String projectName = invoiceParmsMap.getOrDefault("projectName", ""); // 开票项目名称
                            String appSecret = invoiceParmsMap.getOrDefault("appSecret", "");
                            String taxCode = invoiceParmsMap.getOrDefault("taxCode", ""); // 税收编码
                            String drawer = invoiceParmsMap.getOrDefault("drawer", ""); // 开票人
                            String receiver = invoiceParmsMap.getOrDefault("receiver", ""); // 收款人
                            String reviewer = invoiceParmsMap.getOrDefault("reviewer", ""); // 复核人
                            String sellerNumber = invoiceParmsMap.getOrDefault("sellerNumber", ""); // 销方税号
                            String sellerPhone = invoiceParmsMap.getOrDefault("sellerPhone", ""); // 销方电话
                            String sellerAddress = invoiceParmsMap.getOrDefault("sellerAddress", ""); // 销方地址
                            String sellerBank = invoiceParmsMap.getOrDefault("sellerBank", "");
                            String sellerAccount = invoiceParmsMap.getOrDefault("sellerAccount", ""); // 销方银行账号和开户行地址
                            String taxRatestr = invoiceParmsMap.getOrDefault("taxRate", ""); // 税率
                            
                            String taxCode2 = invoiceParmsMap.getOrDefault("taxCode2", ""); // 卡券税别
                            String projectName2 = invoiceParmsMap.getOrDefault("projectName2", ""); // 卡券项目名称
                            String taxRate2 = invoiceParmsMap.getOrDefault("taxRate2", ""); // 卡券税率
                            
                            String sellerBankAccount = sellerBank+" "+sellerAccount; //需 拼接 银行+空格+帐号
                            
                            
                            // 如果卡券卡充值则用 卡券的税率
                            if("Card".equals(billtype)||"Coupon".equals(billtype)||"Recharge".equals(billtype)){
                                taxCode = taxCode2;
                                projectName = projectName2;
                                taxRatestr = taxRate2;
                            }
                            
                            BigDecimal bigtaxRate = new BigDecimal(taxRatestr).multiply(new BigDecimal("0.01"));
                            double taxRate = bigtaxRate.doubleValue();
                            
                            
                            // 根据单个订单号查询发票信息
                            if ("0".equals(querytype)) {
                                // 0-开票数据试算【不验证 退单、已开票等信息】
                                if("Sale".equals(billtype)||"Card".equals(billtype)||"Coupon".equals(billtype)||"Recharge".equals(billtype)){
                                    sql  = getOrderPayInfoByOrderNos(eId,sourceBillNos,billtype,taxRate);
                                    List<Map<String, Object>> orderPayInfoList = dao.executeQuerySQL(sql, null);
                                    
                                    EInvoiceResponse invoiceResponse = new EInvoiceResponse();
                                    invoiceResponse.setDetail(new ArrayList<>());
                                    BigDecimal ordertotal = new BigDecimal(0);
                                    BigDecimal taxtotal = new BigDecimal(0);
                                    BigDecimal bhtaxtotal = new BigDecimal(0);
                                    if (!CollectionUtils.isEmpty(orderPayInfoList)) {
                                        for (Map<String, Object> orderPayInfo : orderPayInfoList) {
                                            String taxamt = orderPayInfo.get("TAXAMT").toString();
                                            String tax = orderPayInfo.get("TAX").toString();
                                            String taxfreeamt = orderPayInfo.get("TAXFREEAMT").toString();
                                            
                                            ordertotal = ordertotal.add(new BigDecimal(taxamt));
                                            taxtotal = taxtotal.add(new BigDecimal(tax));
                                            bhtaxtotal = bhtaxtotal.add(new BigDecimal(taxfreeamt));
                                            EInvoiceResponse.level1Elm lv1 = eInvoiceResponse.new level1Elm();
                                            lv1.setGoodsname(projectName);
                                            lv1.setNum("1");
                                            lv1.setHsbz("1");
                                            lv1.setPrice(taxamt);
                                            lv1.setTaxrate(taxRate + "");
                                            lv1.setSpbm(taxCode);
                                            lv1.setFphxz("0");
                                            //【ID1027767】 霸王3.0 诺诺开票时，免税（0税率）商品不能开票 by jinzma 20220805
                                            // yhzcbs  当税率=0时为1，否则为0          小凤
                                            // zzstsgl 当税率=0时为“免税”，否则为空     小凤
                                            // lslbs   当税率=0时为1，否则为空         小凤
                                            if (bigtaxRate.compareTo(BigDecimal.ZERO)==0){
                                                lv1.setYhzcbs("1");
                                                lv1.setZzstsgl("免税");
                                                lv1.setLslbs("1");
                                            }else{
                                                lv1.setYhzcbs("0");
                                                lv1.setZzstsgl("");
                                                lv1.setLslbs("");
                                            }
                                            
                                            lv1.setTaxamt(taxamt);
                                            lv1.setTax(tax);
                                            lv1.setTaxfreeamt(taxfreeamt);
                                            invoiceResponse.getDetail().add(lv1);
                                        }
                                    } else {
                                        EInvoiceResponse.level1Elm lv1 = eInvoiceResponse.new level1Elm();
                                        lv1.setGoodsname(projectName);
                                        lv1.setNum("1");
                                        lv1.setHsbz("1");
                                        lv1.setPrice("0");
                                        lv1.setTaxrate(taxRate + "");
                                        lv1.setSpbm(taxCode);
                                        lv1.setFphxz("0");
                                        //【ID1027767】 霸王3.0 诺诺开票时，免税（0税率）商品不能开票 by jinzma 20220805
                                        // yhzcbs  当税率=0时为1，否则为0          小凤
                                        // zzstsgl 当税率=0时为“免税”，否则为空     小凤
                                        // lslbs   当税率=0时为1，否则为空         小凤
                                        if (bigtaxRate.compareTo(BigDecimal.ZERO)==0){
                                            lv1.setYhzcbs("1");
                                            lv1.setZzstsgl("免税");
                                            lv1.setLslbs("1");
                                        }else{
                                            lv1.setYhzcbs("0");
                                            lv1.setZzstsgl("");
                                            lv1.setLslbs("");
                                        }
                                        lv1.setTaxamt("0");
                                        lv1.setTax("0");
                                        lv1.setTaxfreeamt("0");
                                        invoiceResponse.getDetail().add(lv1);
                                    }
                                    invoiceResponse.setOrderno(orderno);
                                    invoiceResponse.setPlatformtype(platformtype);
                                    invoiceResponse.setInvoicebillno(orderno);
                                    invoiceResponse.setOrdertotal(ordertotal.toString());
                                    invoiceResponse.setTaxtotal(taxtotal.toString());
                                    invoiceResponse.setBhtaxtotal(bhtaxtotal.toString());
                                    invoiceResponse.setKptype("1");
                                    invoiceResponse.setQdbz("0");
                                    invoiceResponse.setQdxmmc(projectName);
                                    invoiceResponse.setInvoicedate(invoicedate);
                                    invoiceResponse.setMessage("");
                                    invoiceResponse.setClerk(drawer);
                                    invoiceResponse.setPayee(receiver);
                                    invoiceResponse.setChecker(reviewer);
                                    invoiceResponse.setCpybz("0");
                                    invoiceResponse.setSelf_flag("1");
                                    invoiceResponse.setSaleaddress(sellerAddress);
                                    invoiceResponse.setSalephone(sellerPhone);
                                    invoiceResponse.setSaletaxnum(sellerNumber);
                                    invoiceResponse.setSaleaccount(sellerBankAccount);
                                    res = JSONArray.toJSONString(invoiceResponse);
                                }
                            } else if ("1".equals(querytype)) {
                                String[] columns_EINVOICE_DETAIL = {"EID", "INVOICEBILLNO", "ITEM", "GOODSNAME", "NUM", "HSBZ", "PRICE", "TAXRATE", "SPBM", "FPHXZ", "YHZCBS", "ZZSTSGL", "LSLBS", "AMT", "TAXAMT", "EXTAXAMT"};
                                // 1-查可开票信息
                                if("Sale".equals(billtype)||"Card".equals(billtype)||"Coupon".equals(billtype)||"Recharge".equals(billtype)){
                                    boolean isQuery = true;
                                    // 1. 查询订单是否已申请开票【申请开票状态=Y】
                                    if ("Y".equals(isApply)) {
                                        isQuery = false;
                                    }
                                    
                                    // 查询订单是否已退
                                    sql = CheckOrderIsReturn(eId,sourceBillNos,billtype);
                                    List<Map<String, Object>> getOrderIsReturn = dao.executeQuerySQL(sql, null);
                                    String isReturn = "N";
                                    if (!CollectionUtils.isEmpty(getOrderIsReturn)) {
                                        isReturn = getOrderIsReturn.get(0).get("ISRETURN").toString();
                                    }
                                    if ("Y".equals(isReturn)) {
                                        isQuery = false;
                                    }
                                    
                                    if (isQuery) {
                                        sql  = getOrderPayInfoByOrderNos(eId,sourceBillNos,billtype,taxRate);
                                        List<Map<String, Object>> orderPayInfoList = dao.executeQuerySQL(sql, null);
                                        EInvoiceResponse invoiceResponse = new EInvoiceResponse();
                                        
                                        invoiceResponse.setDetail(new ArrayList<>());
                                        BigDecimal ordertotal = new BigDecimal(0);
                                        BigDecimal taxtotal = new BigDecimal(0);
                                        BigDecimal bhtaxtotal = new BigDecimal(0);
                                        if (!CollectionUtils.isEmpty(orderPayInfoList)) {
                                            for (Map<String, Object> orderPayInfo : orderPayInfoList) {
                                                String taxamt = orderPayInfo.get("TAXAMT").toString();
                                                String tax = orderPayInfo.get("TAX").toString();
                                                String taxfreeamt = orderPayInfo.get("TAXFREEAMT").toString();
                                                
                                                ordertotal = ordertotal.add(new BigDecimal(taxamt));
                                                taxtotal = taxtotal.add(new BigDecimal(tax));
                                                bhtaxtotal = bhtaxtotal.add(new BigDecimal(taxfreeamt));
                                                EInvoiceResponse.level1Elm lv1 = eInvoiceResponse.new level1Elm();
                                                lv1.setGoodsname(projectName);
                                                lv1.setNum("1");
                                                lv1.setHsbz("1");
                                                lv1.setPrice(taxamt);
                                                lv1.setTaxrate(taxRate + "");
                                                lv1.setSpbm(taxCode);
                                                lv1.setFphxz("0");
                                                //【ID1027767】 霸王3.0 诺诺开票时，免税（0税率）商品不能开票 by jinzma 20220805
                                                // yhzcbs  当税率=0时为1，否则为0          小凤
                                                // zzstsgl 当税率=0时为“免税”，否则为空     小凤
                                                // lslbs   当税率=0时为1，否则为空         小凤
                                                if (bigtaxRate.compareTo(BigDecimal.ZERO)==0){
                                                    lv1.setYhzcbs("1");
                                                    lv1.setZzstsgl("免税");
                                                    lv1.setLslbs("1");
                                                }else{
                                                    lv1.setYhzcbs("0");
                                                    lv1.setZzstsgl("");
                                                    lv1.setLslbs("");
                                                }
                                                lv1.setTaxamt(taxamt);
                                                lv1.setTax(tax);
                                                lv1.setTaxfreeamt(taxfreeamt);
                                                invoiceResponse.getDetail().add(lv1);
                                            }
                                        } else {
                                            EInvoiceResponse.level1Elm lv1 = eInvoiceResponse.new level1Elm();
                                            lv1.setGoodsname(projectName);
                                            lv1.setNum("1");
                                            lv1.setHsbz("1");
                                            lv1.setPrice("0");
                                            lv1.setTaxrate(taxRate + "");
                                            lv1.setSpbm(taxCode);
                                            lv1.setFphxz("0");
                                            //【ID1027767】 霸王3.0 诺诺开票时，免税（0税率）商品不能开票 by jinzma 20220805
                                            // yhzcbs  当税率=0时为1，否则为0          小凤
                                            // zzstsgl 当税率=0时为“免税”，否则为空     小凤
                                            // lslbs   当税率=0时为1，否则为空         小凤
                                            if (bigtaxRate.compareTo(BigDecimal.ZERO)==0){
                                                lv1.setYhzcbs("1");
                                                lv1.setZzstsgl("免税");
                                                lv1.setLslbs("1");
                                            }else{
                                                lv1.setYhzcbs("0");
                                                lv1.setZzstsgl("");
                                                lv1.setLslbs("");
                                            }
                                            lv1.setTaxamt("0");
                                            lv1.setTax("0");
                                            lv1.setTaxfreeamt("0");
                                            invoiceResponse.getDetail().add(lv1);
                                        }
                                        invoiceResponse.setOrderno(orderno);
                                        invoiceResponse.setPlatformtype(platformtype);
                                        invoiceResponse.setInvoicebillno(orderno);
                                        invoiceResponse.setOrdertotal(ordertotal.toString());
                                        invoiceResponse.setTaxtotal(taxtotal.toString());
                                        invoiceResponse.setBhtaxtotal(bhtaxtotal.toString());
                                        invoiceResponse.setKptype("1");
                                        invoiceResponse.setQdbz("0");
                                        invoiceResponse.setQdxmmc(projectName);
                                        invoiceResponse.setInvoicedate(invoicedate);
                                        invoiceResponse.setMessage("");
                                        invoiceResponse.setClerk(drawer);
                                        invoiceResponse.setPayee(receiver);
                                        invoiceResponse.setChecker(reviewer);
                                        invoiceResponse.setCpybz("0");
                                        invoiceResponse.setSelf_flag("1");
                                        invoiceResponse.setSaleaddress(sellerAddress);
                                        invoiceResponse.setSalephone(sellerPhone);
                                        invoiceResponse.setSaletaxnum(sellerNumber);
                                        invoiceResponse.setSaleaccount(sellerBankAccount);
//                                    String data = JSONArray.toJSONString(invoiceResponse);
//                                    res = MyCommon.encrypt(data, appSecret.substring(0, 8), appSecret.substring(appSecret.length() - 8, appSecret.length()));
                                        
                                        DelBean del = new DelBean("DCP_EINVOICE_DETAIL");
                                        //condition
                                        del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        del.addCondition("INVOICEBILLNO", new DataValue(orderno, Types.VARCHAR));
                                        lstData.add(new DataProcessBean(del));
                                        
                                        int item = 0;
                                        for (EInvoiceResponse.level1Elm lv1 : invoiceResponse.getDetail()) {
                                            item++;
                                            DataValue[] insValue = new DataValue[]{
                                                    new DataValue(eId, Types.VARCHAR),
                                                    new DataValue(orderno, Types.VARCHAR),
                                                    new DataValue(item, Types.VARCHAR),
                                                    new DataValue(lv1.getGoodsname(), Types.VARCHAR),
                                                    new DataValue(lv1.getNum(), Types.VARCHAR),
                                                    new DataValue(lv1.getHsbz(), Types.VARCHAR),
                                                    new DataValue(lv1.getPrice(), Types.VARCHAR),
                                                    new DataValue(taxRatestr, Types.VARCHAR),
                                                    new DataValue(lv1.getSpbm(), Types.VARCHAR),
                                                    new DataValue(lv1.getFphxz(), Types.VARCHAR),
                                                    new DataValue(lv1.getYhzcbs(), Types.VARCHAR),
                                                    new DataValue(lv1.getZzstsgl(), Types.VARCHAR),
                                                    new DataValue(lv1.getLslbs(), Types.VARCHAR),
                                                    new DataValue(lv1.getTaxamt(), Types.VARCHAR),
                                                    new DataValue(lv1.getTax(), Types.VARCHAR),
                                                    new DataValue(lv1.getTaxfreeamt(), Types.VARCHAR)
                                            };
                                            InsBean ib1 = new InsBean("DCP_EINVOICE_DETAIL", columns_EINVOICE_DETAIL);
                                            ib1.addValues(insValue);
                                            lstData.add(new DataProcessBean(ib1));
                                        }
                                        
                                        res = JSONArray.toJSONString(invoiceResponse);
//                                    DESDZFP.key = new String(Base64.encode(appSecret.getBytes()));
                                        res = DESDZFP.encrypt(res);
                                    }
                                }
                                
                                // 执行
                                StaticInfo.dao.useTransactionProcessData(lstData);
                            }
                        }
                    }
                }
            } else if ("queryInfoByOrderList".equals(operater)) {
                Map<String,String> resMap = new HashMap<>();
                List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();
                if("2".equals(querytype)){
                    /**
                     * 瑞宏
                     */
                    if("RUIHONG".equals(platformType)){
                        String projectId = request.getProjectId();
                        String invoiceKind = request.getInvoiceKind();
                        if(Check.Null(invoiceKind)){
                            invoiceKind = "p";
                        }
                        eId = request.getEid();
                        // 查询发票参数
                        String sql = getInvoiceParmsSql(eId, templateid, shopid);
                        List<Map<String, Object>> getPARAMSs = dao.executeQuerySQL(sql, null);
                        if (!CollectionUtils.isEmpty(getPARAMSs)) {
                            
                            // 优先取门店 取第一个
                            Map<String, Object> getParams = getPARAMSs.get(0);
                            String platformtype = getParams.get("PLATFORMTYPE").toString();
                            String params = getParams.get("PARAMS").toString();
                            Map<String, String> invoiceParmsMap = myCommon.invoiceParmsStringToMap(params);
                            
                            String serviceUrl = invoiceParmsMap.getOrDefault("serviceUrl", "");// 接口URL
                            String appKey = invoiceParmsMap.getOrDefault("appKey", ""); // 由电子发票平台分配的appCode
                            String certificateName = invoiceParmsMap.getOrDefault("certificateName", ""); // 密钥库别名
                            String certificatePassword = invoiceParmsMap.getOrDefault("certificatePassword", ""); // 密钥库密码
                            String projectName = invoiceParmsMap.getOrDefault("projectName", ""); // 开票项目名称
                            String appSecret = invoiceParmsMap.getOrDefault("appSecret", "");
                            String taxCode = invoiceParmsMap.getOrDefault("taxCode", ""); // 税收编码
                            String drawer = invoiceParmsMap.getOrDefault("drawer", ""); // 开票人
                            String receiver = invoiceParmsMap.getOrDefault("receiver", ""); // 收款人
                            String reviewer = invoiceParmsMap.getOrDefault("reviewer", ""); // 复核人
                            String sellerNumber = invoiceParmsMap.getOrDefault("sellerNumber", ""); // 销方税号
                            String sellerPhone = invoiceParmsMap.getOrDefault("sellerPhone", ""); // 销方电话
                            String sellerName = invoiceParmsMap.getOrDefault("sellerName", ""); // 销方名称
                            String sellerAddress = invoiceParmsMap.getOrDefault("sellerAddress", ""); // 销方地址
                            String sellerBank = invoiceParmsMap.getOrDefault("sellerBank", ""); // 销方开户银行
                            String sellerAccount = invoiceParmsMap.getOrDefault("sellerAccount", ""); // 销方银行账号和开户行地址
                            String taxRatestr = invoiceParmsMap.getOrDefault("taxRate", ""); // 税率
                            String limitation = invoiceParmsMap.getOrDefault("limitation", "10000"); // 税率
                            String serviceUrl_callBack = invoiceParmsMap.getOrDefault("serviceUrl_callBack", ""); // 回调地址
                            String taxCode2 = invoiceParmsMap.getOrDefault("taxCode2", ""); // 卡券税别
                            String projectName2 = invoiceParmsMap.getOrDefault("projectName2", ""); // 卡券项目名称
                            String taxRate2 = invoiceParmsMap.getOrDefault("taxRate2", ""); // 卡券税率

                            //【ID1039046】【3.0】詹记对接全电发票 by jinzma 20240223
                            String invoiceType = invoiceParmsMap.getOrDefault("invoiceType", ""); // 发票类型
                            if (!Check.Null(invoiceType)){
                                invoiceKind = invoiceType;   //q 全电普票（数电普票）
                            }
                            
                            // 获取密钥文件地址,所有客户都使用这一个文件
                            URL url = InvoiceService.class.getClassLoader().getResource("PT000201.keystore");
                            String keyStorePath =  url.getPath();
                            // 如果卡券卡充值则用 卡券的税率
                            if("Card".equals(billtype)||"Coupon".equals(billtype)||"Recharge".equals(billtype)){
                                taxCode = taxCode2;
                                projectName = projectName2;
                                taxRatestr = taxRate2;
                            }
                            
                            // 如开票项目id不为空 则优先取开票项目的税别税率
                            
                            if(!Check.Null(projectId)){
                                sql = getFaPiaoPojo(eId, projectId,langType);
                                List<Map<String, Object>> getFaPiaoPo = dao.executeQuerySQL(sql, null);
                                if(!CollectionUtils.isEmpty(getFaPiaoPo)){
                                    taxCode =   getFaPiaoPo.get(0).get("TAXCODE").toString();
                                    taxRatestr =   getFaPiaoPo.get(0).get("TAXRATE").toString();
                                    projectName =   getFaPiaoPo.get(0).get("PROJECTNAME").toString();
                                }
                            }
                            // 瑞宏税率限制 税率。只能为0、0.03、0.04、0.06、0.09、0.10、0.13或0.16。
                            boolean isTaxRate = false;
                            if(!Check.Null(taxRatestr)){
                                String[] taxRates = new String[]{"0","3","4","6","9","10","13","16"};
                                for (String taxRate : taxRates) {
                                    if(taxRate.equals(taxRatestr)){
                                        isTaxRate = true;
                                        break;
                                    }
                                }
                            }
                            if(!isTaxRate){
                                resMap.put("errorMessage","税率,只能为0、0.03、0.04、0.06、0.09、0.10、0.13或0.16");
                                res = JSONArray.toJSONString(resMap);
                                return res;
                            }
                            
                            BigDecimal bigtaxRate = new BigDecimal(taxRatestr).multiply(new BigDecimal("0.01"));
                            double taxRate = bigtaxRate.doubleValue();
                            
                            // 调用瑞宏发票 申请开票
                            orderFacade = new OrderFacadeAgent(appKey, keyStorePath, certificateName, certificatePassword, serviceUrl);
                            
                            
                            List<EInvoiceRequest.level1Elm> ordernolist = request.getOrdernolist();
                            orderNos  = ordernolist.stream().map(EInvoiceRequest.level1Elm::getOrderno).collect(Collectors.toList());
                            
                            
                            BigDecimal ordertotal = new BigDecimal(0);
                            BigDecimal taxtotal = new BigDecimal(0);
                            BigDecimal bhtaxtotal = new BigDecimal(0);
                            EInvoiceResponse invoiceResponse = new EInvoiceResponse();
                            
                            StringBuffer message = new StringBuffer("");
                            String einvoiceInfoSql = getEinvoiceInfoForbillNoSql(orderNos);
                            boolean isQuery = true;
                            List<Map<String, Object>> getEinvoiceInfoDatas = dao.executeQuerySQL(einvoiceInfoSql, null);
                            // 过滤
                            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                            condition.put("SOURCEBILLNO", true);
                            // 调用过滤函数
                            getEinvoiceInfoDatas = MapDistinct.getMap(getEinvoiceInfoDatas, condition);
                            if (!CollectionUtils.isEmpty(getEinvoiceInfoDatas)) {
                                for (Map<String, Object> getEinvoiceInfoData : getEinvoiceInfoDatas) {
                                    String orderno2 = getEinvoiceInfoData.get("SOURCEBILLNO").toString();
                                    String  isApply = getEinvoiceInfoData.get("ISAPPLY").toString();
                                    String  status = getEinvoiceInfoData.get("STATUS").toString();
                                    if("Y".equals(isApply)||"Y".equals(status)){
                                        isQuery = false;
                                        message.append(orderno2+",");
                                    }
                                }
                            }
                            
                            if(!isQuery){
                                message.deleteCharAt(message.length()-1);
                                resMap.put("errorMessage",message.toString()+"订单已开票，请勿重新开票！");
                                res = JSONArray.toJSONString(resMap);
                                return res;
                            }
                            
                            // 发票试算 业务单据类型：Sale-销售单  Card-售卡 Coupon-售券 Recharge-充值
                            if("Sale".equals(billtype)||"Card".equals(billtype)||"Coupon".equals(billtype)||"Recharge".equals(billtype)){
                                // 查询订单是否已退
                                sql = this.CheckOrderIsReturn(eId, orderNos, billtype);
                                List<Map<String, Object>> getOrderIsReturn = dao.executeQuerySQL(sql, null);
                                
                                if (!CollectionUtils.isEmpty(getOrderIsReturn)) {
                                    for (Map<String, Object> map : getOrderIsReturn) {
                                        String isreturn = map.get("ISRETURN").toString();
                                        String orderno = map.get("ORDERNO").toString();
                                        String status = map.getOrDefault("STATUS","100").toString();
                                        String quotesign = map.getOrDefault("QUOTESIGN","0").toString();
                                        if("Y".equals(isreturn)||"0".equals(status)||"-1".equals(status)||"90".equals(status)||"1".equals(quotesign)){
                                            isQuery = false;
                                            message.append(orderno+",");
                                        }
                                    }
                                }
                                if(!isQuery){
                                    resMap.put("errorMessage",message.toString()+"订单已退订，请重新选择开票单据。");
                                    res = JSONArray.toJSONString(resMap);
                                    return res;
                                }
                                
                                // 计算价格
                                sql  = getOrderPayInfoByOrderNos(eId,orderNos,billtype,taxRate);
                                List<Map<String, Object>> orderPayInfoList = dao.executeQuerySQL(sql, null);
                                
                                invoiceResponse.setDetail(new ArrayList<>());
                                if (!CollectionUtils.isEmpty(orderPayInfoList)) {
                                    for (String orderNo : orderNos) {
                                        boolean isFlag = false;
                                        for (Map<String, Object> orderPayInfo : orderPayInfoList) {
                                            String orderno = orderPayInfo.get("ORDERNO").toString();
                                            if(!orderNo.equals(orderno)){
                                                continue;
                                            }
                                            String taxamt = orderPayInfo.get("TAXAMT").toString();
                                            String tax = orderPayInfo.get("TAX").toString();
                                            String taxfreeamt = orderPayInfo.get("TAXFREEAMT").toString();
                                            
                                            ordertotal = ordertotal.add(new BigDecimal(taxamt));
                                            taxtotal = taxtotal.add(new BigDecimal(tax));
                                            bhtaxtotal = bhtaxtotal.add(new BigDecimal(taxfreeamt));
                                            
                                            EInvoiceResponse.level1Elm lv1 = invoiceResponse.new level1Elm();
                                            lv1.setPrice(taxamt);
                                            lv1.setTaxrate(taxRatestr + "");
                                            lv1.setSpbm(taxCode);
                                            lv1.setTaxamt(taxamt);
                                            lv1.setTax(tax);
                                            lv1.setTaxfreeamt(taxfreeamt);
                                            invoiceResponse.getDetail().add(lv1);
                                        }
//                                        if(!isFlag){
//                                            EInvoiceResponse.level1Elm lv1 = invoiceResponse.new level1Elm();
//                                            lv1.setTaxamt("0");
//                                            lv1.setTax("0");
//                                            lv1.setTaxfreeamt("0");
//                                            invoiceResponse.getDetail().add(lv1);
//                                        }
                                    }
                                    if("Y".equals(ischecklimitation)){
                                        if(bhtaxtotal.compareTo(new BigDecimal(limitation)) > 0){
                                            if(orderNos.size()>1){
                                                resMap.put("errorMessage","开票金额超出上限"+limitation+"元,请联系企业处理.");
                                                res = JSONArray.toJSONString(resMap);
                                                return res;
                                            }else{
                                                resMap.put("errorMessage","开票金额超出上限"+limitation+"元,请重新选择开票单据.");
                                                res = JSONArray.toJSONString(resMap);
                                                return res;
                                            }
                                            
                                        }
                                    }
                                    
                                    
                                    if("Y".equals(apply)){
                                        // 生成单号
                                        String invoiceBillNo = sdfh.format(new Date());
                                        String dateTime = sdf.format(new Date());
                                        // ***************** 组装订单信息 Begin********************
                                        Order order = new Order();
                                        // 订单编号
                                        order.setOrderNo(invoiceBillNo);
                                        // 销货方纳税人识别号
                                        order.setTaxpayerCode(sellerNumber);
                                        // 用户扫码key
                                        order.setScanCodeKey(UUID.randomUUID().toString());
                                        // 订单时间（yyyy-MM-dd HH:mm:ss）
                                        order.setOrderTime(LocalDateTime.now().format(df));
                                        // 开票类型。p:电子增值税普通发票（默认）
                                        order.setInvoiceType(invoiceKind);
                                        // 联系电话
                                        order.setContactTel(request.getBuyerPhone());
                                        // 联系邮箱
                                        if(!Check.Null(request.getBuyerEmaill())){
                                            order.setContactMail(request.getBuyerEmaill());
                                        }
                                        // 配送地址
                                        order.setShippingAddress(request.getBuyerAddress());
                                        // 销方名称
                                        order.setTaxpayerName(sellerName);
                                        // 销方地址
                                        order.setTaxpayerAddress(sellerAddress);
                                        // 销方电话
                                        order.setTaxpayerTel(sellerPhone);
                                        // 销方开户银行
                                        order.setTaxpayerBankName(sellerBank);
                                        // 销方银行账户
                                        order.setTaxpayerBankAccount(sellerAccount);
//                                        // 发票抬头
                                        order.setCustomerName(request.getBuyerName());
                                        // 购方税号
                                        order.setCustomerCode(request.getBuyerTaxNum());
                                        // 购方地址
                                        order.setCustomerAddress(request.getBuyerAddress());
                                        // 购方电话
                                        order.setCustomerTel(request.getBuyerTel());
                                        // 购方开户行
                                        order.setCustomerBankName(request.getBuyerBank());
                                        // 购方购方银行账户
                                        order.setCustomerBankAccount(request.getBuyerAccount());
                                        // 是否直接开票
                                        order.setAutoBilling(true);
                                        // 开票人
                                        order.setDrawer(drawer);
                                        // 收款人
                                        order.setPayee(receiver);
                                        // 复核人
                                        order.setReviewer(reviewer);
                                        // 价税合计
                                        order.setTotalAmount(ordertotal);
                                        order.setOrderItems(new ArrayList<>());
                                        for (EInvoiceResponse.level1Elm level1Elm : invoiceResponse.getDetail()) {
                                            // 发票明细
                                            OrderItem orderItem = new OrderItem();
                                            //商品编码
//                                        orderItem1.setCode("001");
                                            //商品名称
                                            orderItem.setName(projectName);
                                            //规格型号
//                                        orderItem1.setSpec("无");
                                            //税率
                                            orderItem.setTaxRate(bigtaxRate);
                                            //税价合计金额
                                            orderItem.setAmount(new BigDecimal(level1Elm.getTaxamt()));
                                            //商品分类编码
                                            if(taxCode.length()==19){
                                                orderItem.setCatalogCode(taxCode);
                                            }else{
                                                int i = 19 - taxCode.length();
                                                StringBuffer taxcodeBuffer = new StringBuffer(taxCode);
                                                for(int j=1;j<=i;j++){
                                                    taxcodeBuffer.append("0");
                                                }
                                                orderItem.setCatalogCode(taxcodeBuffer.toString());
                                            }
                                            
                                            order.getOrderItems().add(orderItem);
                                        }
                                        // 自定义参数
                                        order.setDynamicParams(Collections.singletonMap("callbackUrl", serviceUrl_callBack));
                                        // 调用接口方法
                                        HelpTools.writelog_fileName(
                                                "*********** 调用通过订单查询开票信息 【订单申请开票】请求:" + order.toString() + " *************",
                                                "invoiceService");
                                        Result result1 = orderFacade.kpAsync(order);
                                        HelpTools.writelog_fileName(
                                                "*********** 调用通过订单查询开票信息 【订单申请开票】返回:" + result1.toString() + " *************",
                                                "invoiceService");
                                        BigDecimal finalOrdertotal = ordertotal;
                                        BigDecimal finalTaxtotal = taxtotal;
                                        BigDecimal finalBhtaxtotal = bhtaxtotal;
                                        String finalEId = eId;
                                        EInvoiceRequest finalRequest = request;
                                        String finalTaxRatestr = taxRatestr;
                                        String finalProjectName = projectName;
                                        // 防止请求超时 调用发起订单后调用线程去查询是否申请开票成功
                                        Thread thread = null;
                                        thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int i = 1;
                                                boolean isflag = false;
                                                Result result = result1;
                                                while (i <= 3) {
                                                    try {
                                                        if (null != result) {
                                                            isflag = true;
                                                        } else {
                                                            // 对开局失败对订单重新申请开票
                                                            Order order = new Order();
                                                            // 订单编号
                                                            order.setOrderNo(invoiceBillNo);
                                                            // 销货方纳税人识别号
                                                            order.setTaxpayerCode(sellerNumber);
                                                            // 调用接口方法
                                                            HelpTools.writelog_fileName(
                                                                    "*********** 调用通过订单查询开票信息 【订单重新申请开票】次数"+i+"请求:" + order.toString() + " *************",
                                                                    "invoiceService");
                                                            result = orderFacade.retryKpAsync(order);
                                                            HelpTools.writelog_fileName(
                                                                    "*********** 调用通过订单查询开票信息 【订单重新申请开票】次数"+i+" 返回:" + result.toString() + " *************",
                                                                    "invoiceService");
                                                            if (null!=result) {
                                                                isflag = true;
                                                            }
                                                        }
                                                        
                                                        if(isflag){
                                                            code = result.getCode();
                                                            if("0".equals(code)||"501".equals(code)){
                                                                resMap.put("code",code);
                                                                resMap.put("message",result.getMessage());
                                                                resMap.put("invoiceBillNo",invoiceBillNo);
                                                                resMap.put("platformType","#RUIHONG");
                                                                resMap.put("amt", finalOrdertotal.toString());
                                                                resMap.put("taxAmt", finalTaxtotal.toString());
                                                                resMap.put("exTaxAmt", finalBhtaxtotal.toString());
                                                                
                                                                
                                                                String[] columns_EINVOICE = {"EID","INVOICEBILLNO","SOURCEBILLTYPE","PLATFORMTYPE","INVOICETYPE","TAXRATE","AMT","TAXAMT","EXTAXAMT","DRAWER","RECEIVER","REVIEWER"
                                                                        ,"SALETAXNUM","SALETEL","SALEADDRESS","SALEACCOUNT","QRCODETIME","QRCODE","CREATETIME","UPDATETIME","SALEBANK","BUYERBANK","TEMPLATEID","ISAPPLY","APPLYDATE",
                                                                        "BUYERNAME","EMAIL","BUYERPHONE","BUYERTAXNUM","BUYERTEL","BUYERADDRESS","BUYERACCOUNT","MEMBERID","MEMBERNAME","OPENID"};
                                                                String[] columns_EINVOICE_BUSINESS = {"EID","INVOICEBILLNO","SOURCEBILLNO","SOURCESHOPID"};
                                                                String[] columns_EINVOICE_DETAIL = {"EID", "INVOICEBILLNO", "ITEM", "GOODSNAME", "NUM", "HSBZ", "PRICE", "TAXRATE", "SPBM", "FPHXZ", "YHZCBS", "ZZSTSGL", "LSLBS", "AMT", "TAXAMT", "EXTAXAMT"};
                                                                
                                                                DataValue[] insValue = new DataValue[] {
                                                                        new DataValue(finalEId, Types.VARCHAR),
                                                                        new DataValue(invoiceBillNo, Types.VARCHAR),
                                                                        new DataValue(finalRequest.getBilltype(), Types.VARCHAR),
                                                                        new DataValue(platformtype, Types.VARCHAR),
                                                                        new DataValue("1", Types.VARCHAR),
                                                                        new DataValue(finalTaxRatestr, Types.VARCHAR), // 税率
                                                                        new DataValue(finalOrdertotal.toString(), Types.VARCHAR), // 价税合计
                                                                        new DataValue(finalTaxtotal.toString(), Types.VARCHAR), // 税额
                                                                        new DataValue(finalBhtaxtotal.toString(), Types.VARCHAR), // 未税金额
                                                                        new DataValue(drawer, Types.VARCHAR),
                                                                        new DataValue(receiver, Types.VARCHAR),
                                                                        new DataValue(reviewer, Types.VARCHAR),
                                                                        new DataValue(sellerNumber, Types.VARCHAR),
                                                                        new DataValue(sellerPhone, Types.VARCHAR),
                                                                        new DataValue(sellerAddress, Types.VARCHAR),
                                                                        new DataValue(sellerAccount, Types.VARCHAR),
                                                                        new DataValue(dateTime, Types.DATE),
                                                                        new DataValue("", Types.VARCHAR), // 二维码链接地址
                                                                        new DataValue(dateTime, Types.DATE),
                                                                        new DataValue(dateTime, Types.DATE),
                                                                        new DataValue(sellerBank, Types.VARCHAR), // 销方银行开户行地址
                                                                        new DataValue(finalRequest.getBuyerBank(), Types.VARCHAR), // 购方银行开户行地址
                                                                        new DataValue(templateid, Types.VARCHAR), // 模版ID
                                                                        new DataValue("Y", Types.VARCHAR), // 是否申请开票
                                                                        new DataValue(dateTime, Types.DATE), // 申请开票时间
                                                                        new DataValue(finalRequest.getBuyerName(), Types.VARCHAR), // 购方名称
                                                                        new DataValue(finalRequest.getBuyerEmaill(), Types.VARCHAR), // 购方邮箱
                                                                        new DataValue(finalRequest.getBuyerPhone(), Types.VARCHAR), // 购方电话
                                                                        new DataValue(finalRequest.getBuyerTaxNum(), Types.VARCHAR), // 购方税号
                                                                        new DataValue(finalRequest.getBuyerTel(), Types.VARCHAR), // 电话
                                                                        new DataValue(finalRequest.getBuyerAddress(), Types.VARCHAR), // 地址
                                                                        new DataValue(finalRequest.getBuyerAccount(), Types.VARCHAR), // 银行账户
                                                                        new DataValue(finalRequest.getMemberId(), Types.VARCHAR), // 会员ID
                                                                        new DataValue(finalRequest.getMemberName(), Types.VARCHAR), // 会员名称
                                                                        new DataValue(finalRequest.getOpenId(), Types.VARCHAR), // openId
                                                                };
                                                                InsBean ib1 = new InsBean("DCP_EINVOICE", columns_EINVOICE);
                                                                ib1.addValues(insValue);
                                                                lstData.add(new DataProcessBean(ib1));
                                                                
                                                                
                                                                for (Map<String, Object> map : getOrderIsReturn) {
                                                                    DataValue[] insValue2 = new DataValue[] {
                                                                            new DataValue(finalEId, Types.VARCHAR),
                                                                            new DataValue(invoiceBillNo, Types.VARCHAR),
                                                                            new DataValue(map.get("ORDERNO").toString(), Types.VARCHAR),
                                                                            new DataValue(map.get("SHOPID").toString(), Types.VARCHAR)
                                                                    };
                                                                    
                                                                    InsBean ib2 = new InsBean("DCP_EINVOICE_BUSINESS", columns_EINVOICE_BUSINESS);
                                                                    ib2.addValues(insValue2);
                                                                    lstData.add(new DataProcessBean(ib2));
                                                                }
                                                                
                                                                DelBean del = new DelBean("DCP_EINVOICE_DETAIL");
                                                                //condition
                                                                del.addCondition("EID", new DataValue(finalEId, Types.VARCHAR));
                                                                del.addCondition("INVOICEBILLNO", new DataValue(invoiceBillNo, Types.VARCHAR));
                                                                lstData.add(new DataProcessBean(del));
                                                                
                                                                int item = 0;
                                                                for (EInvoiceResponse.level1Elm lv1 : invoiceResponse.getDetail()) {
                                                                    item++;
                                                                    DataValue[] insValue2 = new DataValue[]{
                                                                            new DataValue(finalEId, Types.VARCHAR),
                                                                            new DataValue(invoiceBillNo, Types.VARCHAR),
                                                                            new DataValue(item, Types.VARCHAR),
                                                                            new DataValue(finalProjectName, Types.VARCHAR),
                                                                            new DataValue(1, Types.VARCHAR),
                                                                            new DataValue(1, Types.VARCHAR),
                                                                            new DataValue(lv1.getPrice(), Types.VARCHAR),
                                                                            new DataValue(lv1.getTaxrate(), Types.VARCHAR),
                                                                            new DataValue(lv1.getSpbm(), Types.VARCHAR),
                                                                            new DataValue(0, Types.VARCHAR),
                                                                            new DataValue(0, Types.VARCHAR),
                                                                            new DataValue("", Types.VARCHAR),
                                                                            new DataValue("", Types.VARCHAR),
                                                                            new DataValue(lv1.getTaxamt(), Types.VARCHAR),
                                                                            new DataValue(lv1.getTax(), Types.VARCHAR),
                                                                            new DataValue(lv1.getTaxfreeamt(), Types.VARCHAR)
                                                                    };
                                                                    InsBean ib2 = new InsBean("DCP_EINVOICE_DETAIL", columns_EINVOICE_DETAIL);
                                                                    ib2.addValues(insValue2);
                                                                    lstData.add(new DataProcessBean(ib2));
                                                                }
                                                                
                                                                // 执行
                                                                StaticInfo.dao.useTransactionProcessData(lstData);
                                                                lstData.clear();
                                                                break;
                                                            }else {
                                                                resMap.put("code",code);
                                                                resMap.put("message",result.getMessage());
                                                                break;
                                                            }
                                                        }
                                                        Thread.sleep(10 * 1000);
                                                        i++;
                                                    } catch (Exception e) {
                                                        resMap.put("code",code);
                                                        resMap.put("message",result.getMessage());
                                                        break;
                                                    }
                                                }
                                                
                                                if(!isflag&&i==3){
                                                    try {
                                                        // 进行取消订单
                                                        Order order = new Order();
                                                        // 订单编号
                                                        order.setOrderNo(invoiceBillNo);
                                                        // 销货方纳税人识别号
                                                        order.setTaxpayerCode(sellerNumber);
                                                        // 调用接口方法
                                                        HelpTools.writelog_fileName(
                                                                "*********** 调用通过订单查询开票信息 【取消开票】次数"+i+"请求:" + order.toString() + " *************",
                                                                "invoiceService");
                                                        Result cancel = orderFacade.cancel(order);
                                                        HelpTools.writelog_fileName(
                                                                "*********** 调用通过订单查询开票信息 【取消开票】次数"+i+"返回:" + cancel.toString() + " *************",
                                                                "invoiceService");
                                                        code = cancel.getCode();
                                                        if("0".equals(code)||"501".equals(code)){
                                                            resMap.put("code",code);
                                                            resMap.put("message","发票开具异常，已为您取消订单，请重新发起开票");
                                                        }else {
                                                            resMap.put("code",code);
                                                            resMap.put("message",result.getMessage());
                                                        }
                                                    } catch (Exception e) {
                                                        resMap.put("code",code);
                                                        resMap.put("message",result.getMessage());
                                                    }
                                                }
                                            }
                                        });
                                        thread.start();
                                        
                                        try {
                                            thread.join();
                                        } catch (InterruptedException e) {
                                            resMap.put("code",code);
                                            resMap.put("message",e.getMessage());
                                        }
                                        
                                        // ***************** 组装订单信息 End********************
                                        
                                    }else {
                                        resMap.put("amt",ordertotal.toString());
                                        resMap.put("taxAmt",taxtotal.toString());
                                        resMap.put("exTaxAmt",bhtaxtotal.toString());
                                        resMap.put("limitation",limitation);
                                        resMap.put("projectId",projectId);
                                        resMap.put("projectName",projectName);
                                        resMap.put("taxRate",taxRatestr);
                                        resMap.put("tacCode",taxCode);
                                    }
                                    res = JSONArray.toJSONString(resMap);
                                }
                                
                            }
                        }
                        
                    }
                }
                
            }
            HelpTools.writelog_fileName(
                    "***********调用通过订单查询开票信息 invoiceQueryInfoByOrder  返回:" + res + " *************",
                    "invoiceService");
        } catch (Exception e) {
            res = "{\"status\":\"9999\",\"message\":\"" + e.getMessage() + "\"}";
        }
        
        return res;
    }
    
    
    /**
     * 开票申请结果数据回传
     *
     * @param dao
     * @param json
     * @return
     */
    public String invoiceApply(DsmDAO dao, String json, String orderNo, String taxNo, String isSuccess, String invoiceId) {
        String res = "";
        List<DataProcessBean> lstData = null;
        try {
            String str = "orderNo:" + orderNo + " taxNo:" + taxNo + " isSuccess:" + isSuccess + " invoiceId:" + invoiceId;
            HelpTools.writelog_fileName(
                    "***********调用开票申请结果回调 invoiceApply 成功！请求为:" + str + " *************",
                    "invoiceService");
            
            String dateTime = sdf.format(new Date());
            
            // 列表SQL
            lstData = new ArrayList<DataProcessBean>();
            
            if ("true".equalsIgnoreCase(isSuccess)) {
                // 开票申请成功
                // 更新电子发票
                UptBean ubec = new UptBean("DCP_EINVOICE");
                ubec.addCondition("INVOICEBILLNO", new DataValue(orderNo, Types.VARCHAR));
                
                ubec.addUpdateValue("ISAPPLY", new DataValue("Y", Types.VARCHAR));
                ubec.addUpdateValue("APPLYDATE", new DataValue(dateTime, Types.DATE));
                if (!Check.Null(invoiceId)) {
                    ubec.addUpdateValue("INVOICESERIALNUM", new DataValue(invoiceId, Types.VARCHAR));
                }
                ubec.addUpdateValue("UPDATETIME", new DataValue(dateTime, Types.DATE));
                lstData.add(new DataProcessBean(ubec));
            } else {
                // 开票申请失败
                // 更新电子发票
                UptBean ubec = new UptBean("DCP_EINVOICE");
                ubec.addCondition("INVOICEBILLNO", new DataValue(orderNo, Types.VARCHAR));
                
                ubec.addUpdateValue("ISAPPLY", new DataValue("E", Types.VARCHAR));
                ubec.addUpdateValue("UPDATETIME", new DataValue(dateTime, Types.DATE));
                lstData.add(new DataProcessBean(ubec));
            }
            StaticInfo.dao.useTransactionProcessData(lstData);
            res = "{\"status\":\"0000\",\"message\":\"同步成功\"}";
            HelpTools.writelog_fileName(
                    "***********调用开票申请结果回调 invoiceApply 返回结果为:" + res + " *************",
                    "invoiceService");
        } catch (Exception e) {
            res = "{\"status\":\"9999\",\"message\":\"" + e.getMessage() + "\"}";
        }
        
        return res;
    }
    
    
    /**
     * 开票成功票面数据回调
     *
     * @param json
     * @return
     */
    public String invoiceCallbake(DsmDAO dao, String json, String PLATFORMTYPE) {
        
        String res = "";
        com.alibaba.fastjson.JSONObject jsonObj = new JSONObject();
        String dateTime = sdf.format(new Date());
        // 列表SQL
        List<DataProcessBean> lstData = new ArrayList<DataProcessBean>();
        try {
            if (!Check.Null(json)) {
                jsonObj = JSON.parseObject(json);
            }
            HelpTools.writelog_fileName(
                    "***********调用 "+PLATFORMTYPE+" 开票结果回调 成功！请求结果为:"+json+"  *************",
                    "invoiceService");
            
            if ("NUONUO_V1".equals(PLATFORMTYPE)) {
                // 诺诺发票
                String orderno = "";
                String contentJson = "";
                
                if (jsonObj.containsKey("orderno")) {
                    orderno = jsonObj.getString("orderno");
                    contentJson = jsonObj.getString("content");
                }
                
                EInvoiceRequest.level2Elm content = null;
                if (!Check.Null(contentJson)) {
                    content = JSON.toJavaObject(JSON.parseObject(contentJson), EInvoiceRequest.level2Elm.class);
                    String c_orderno = content.getC_orderno();
                    if (Check.Null(c_orderno) || "2".equals(content.getC_status())) {
                        // 更新电子发票
                        UptBean ubec = new UptBean("DCP_EINVOICE");
                        ubec.addCondition("INVOICEBILLNO", new DataValue(orderno, Types.VARCHAR));
                        
                        ubec.addUpdateValue("STATUS", new DataValue("E", Types.VARCHAR));
                        ubec.addUpdateValue("UPDATETIME", new DataValue(dateTime, Types.DATE));
                        lstData.add(new DataProcessBean(ubec));
                    } else if ("1".equals(content.getC_status())) {
                        HelpTools.writelog_fileName(
                                "***********调用 开票结果回调 成功！开始存档  *************",
                                "invoiceService");
                        String c_bhsje = content.getC_bhsje(); // 不含税税额
                        String c_hsje = content.getC_hjse(); // 价税税额
                        BigDecimal taxamt = new BigDecimal(c_hsje).add(new BigDecimal(c_bhsje));
                        String INVOICEDATE = sdf.format(sdff.parse(sdff.format(Long.parseLong(content.getC_kprq()))));
                        // 更新电子发票
                        UptBean ubec = new UptBean("DCP_EINVOICE");
                        ubec.addCondition("INVOICEBILLNO", new DataValue(orderno, Types.VARCHAR));
                        
                        ubec.addUpdateValue("STATUS", new DataValue("Y", Types.VARCHAR));
                        ubec.addUpdateValue("UPDATETIME", new DataValue(dateTime, Types.DATE));
                        ubec.addUpdateValue("AMT", new DataValue(taxamt.toString(), Types.VARCHAR));
                        ubec.addUpdateValue("TAXAMT", new DataValue(c_hsje, Types.VARCHAR));
                        ubec.addUpdateValue("EXTAXAMT", new DataValue(c_bhsje, Types.VARCHAR));
                        ubec.addUpdateValue("BUYERTEL", new DataValue(content.getPhone(), Types.VARCHAR));
                        ubec.addUpdateValue("EMAIL", new DataValue(content.getEmail(), Types.VARCHAR));
                        ubec.addUpdateValue("INVOICETIME", new DataValue(content.getC_kprq(), Types.VARCHAR));
                        ubec.addUpdateValue("INVOICEDATE", new DataValue(INVOICEDATE, Types.DATE));
                        ubec.addUpdateValue("INVOICECODE", new DataValue(content.getC_fpdm(), Types.VARCHAR));
                        ubec.addUpdateValue("INVOICENO", new DataValue(content.getC_fphm(), Types.VARCHAR));
                        ubec.addUpdateValue("PDFURL", new DataValue(content.getC_url(), Types.VARCHAR));
                        ubec.addUpdateValue("INVOICESERIALNUM", new DataValue(content.getC_fpqqlsh(), Types.VARCHAR));
                        ubec.addUpdateValue("PICTUREURL", new DataValue(content.getC_jpg_url(), Types.VARCHAR));
                        ubec.addUpdateValue("CHECKCODE", new DataValue(content.getCheckCode(), Types.VARCHAR));
                        ubec.addUpdateValue("CIPHERTEXT", new DataValue(content.getCipherText(), Types.VARCHAR));
                        ubec.addUpdateValue("MACHINECODE", new DataValue(content.getMachineCode(), Types.VARCHAR));
                        ubec.addUpdateValue("BUYERPHONE", new DataValue(content.getPhone(), Types.VARCHAR));
                        lstData.add(new DataProcessBean(ubec));
                        HelpTools.writelog_fileName(
                                "***********调用 开票结果回调 成功！存档结束  *************",
                                "invoiceService");
                    }
                } else {
                    // 更新电子发票
                    UptBean ubec = new UptBean("DCP_EINVOICE");
                    ubec.addCondition("INVOICEBILLNO", new DataValue(orderno, Types.VARCHAR));
                    
                    ubec.addUpdateValue("STATUS", new DataValue("E", Types.VARCHAR));
                    ubec.addUpdateValue("UPDATETIME", new DataValue(dateTime, Types.DATE));
                    lstData.add(new DataProcessBean(ubec));
                }
                res = "{\"status\":\"0000\",\"message\":\"同步成功\"}";
            }else if("RUIHONG".equals(PLATFORMTYPE)){
                // 瑞宏发票
                
                String code = "";
                if (jsonObj.containsKey("code")) {
                    code = jsonObj.getString("code");
                }
                // 回调成功
                if ("0".equals(code)) {
                    
                    if (jsonObj.containsKey("invoices")) {
                        JSONArray invoices = jsonObj.getJSONArray("invoices");
                        JSONObject jsonObject = invoices.getJSONObject(0);
                        
                        String status = "";
                        if (jsonObject.containsKey("status")) {
                            status = jsonObject.getString("status");
                            
                            if("1".equals(status)||"3".equals(status)||"4".equals(status)){
                                String viewUrl = jsonObject.getString("viewUrl");
                                String pdfUnsignedUrl = jsonObject.getString("pdfUnsignedUrl");
                                String orderNo = jsonObject.getString("orderNo");
                                String checkCode = jsonObject.getString("checkCode");
                                String generateTime = jsonObject.getString("generateTime");
                                
                                // 回调存库
                                HelpTools.writelog_fileName(
                                        "***********调用 开票结果回调 成功！开始存档  *************",
                                        "invoiceService");
                                
                                // 更新电子发票
                                UptBean ubec = new UptBean("DCP_EINVOICE");
                                ubec.addCondition("INVOICEBILLNO", new DataValue(orderNo, Types.VARCHAR));
                                
                                ubec.addUpdateValue("STATUS", new DataValue("Y", Types.VARCHAR));
                                ubec.addUpdateValue("UPDATETIME", new DataValue(dateTime, Types.DATE));
                                ubec.addUpdateValue("INVOICEDATE", new DataValue(generateTime, Types.DATE));
                                ubec.addUpdateValue("PDFURL", new DataValue(pdfUnsignedUrl, Types.VARCHAR));
                                ubec.addUpdateValue("PICTUREURL", new DataValue(viewUrl, Types.VARCHAR));
                                ubec.addUpdateValue("CHECKCODE", new DataValue(checkCode, Types.VARCHAR));
                                lstData.add(new DataProcessBean(ubec));
                            }
                            
                        }
                    }
                }else {
                    // 回调失败
                    // 更新电子发票
//                    UptBean ubec = new UptBean("DCP_EINVOICE");
//                    ubec.addCondition("INVOICEBILLNO", new DataValue(orderno, Types.VARCHAR));
//
//                    ubec.addUpdateValue("STATUS", new DataValue("E", Types.VARCHAR));
//                    ubec.addUpdateValue("UPDATETIME", new DataValue(dateTime, Types.DATE));
//                    lstData.add(new DataProcessBean(ubec));
                }
            }
            
            StaticInfo.dao.useTransactionProcessData(lstData);
            HelpTools.writelog_fileName(
                    "***********调用 开票结果回调 返回结果为:" + res + " *************",
                    "invoiceService");
        } catch (Exception e) {
            res = "{\"status\":\"9999\",\"message\":\"" + e.getMessage() + "\"}";
        }
        return res;
    }
    
    
    /**
     * 根据订单号查询订单信息
     *
     * @param ordrNos
     * @return
     */
    public String getEinvoiceInfoSql(List<String> ordrNos) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        if (!CollectionUtils.isEmpty(ordrNos)) {
            String ordrNos_Str = PosPub.getArrayStrSQLIn(ordrNos.toArray(new String[ordrNos.size()]));
            sqlbuf.append(""
                    + " select distinct a.eid,a.invoicebillno,b.sourceshopid ,b.sourcebillno,a.sourcebilltype,a.platformtype,"
                    + " a.createtime,a.isapply,a.status "
                    + " from dcp_einvoice a "
                    + " left join dcp_einvoice_business b on a.eid = b.eid and a.invoicebillno = b.invoicebillno "
                    + " where a.invoicebillno in (" + ordrNos_Str + ")"
            );
        }
        sql = sqlbuf.toString();
        return sql;
    }
    
    /**
     * 根据业务单号查询订单信息
     *
     * @param ordrNos
     * @return
     */
    public String getEinvoiceInfoForbillNoSql(List<String> ordrNos) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        if (!CollectionUtils.isEmpty(ordrNos)) {
            String ordrNos_Str = PosPub.getArrayStrSQLIn(ordrNos.toArray(new String[ordrNos.size()]));
            sqlbuf.append("SELECT DISTINCT a.EID,a.INVOICEBILLNO,b.SOURCESHOPID ,b.SOURCEBILLNO,a.SOURCEBILLTYPE,a.PLATFORMTYPE,a.CREATETIME,a.ISAPPLY,a.status " +
                    "  FROM DCP_EINVOICE a LEFT JOIN DCP_EINVOICE_BUSINESS b ON a.EID = b.EID AND a.INVOICEBILLNO = b.INVOICEBILLNO " +
                    " WHERE b.SOURCEBILLNO in(" + ordrNos_Str + ")");
        }
        sql = sqlbuf.toString();
        return sql;
    }
    
    /**
     * 查询发票参数
     *
     * @param eid
     * @param templateId
     * @param shopId
     * @return
     */
    public String getInvoiceParmsSql(String eid, String templateId, String shopId) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("SELECT DISTINCT a.PLATFORMTYPE,a.TEMPLATETYPE,a.PARAMS FROM DCP_FAPIAO_TEMPLATE a  " +
                "LEFT JOIN DCP_FAPIAO_TEMPLATE_SHOP b ON a.TEMPLATEID = b.TEMPLATEID  AND a.EID  = b.EID  " +
                " WHERE a.EID  = '" + eid + "'  AND a.STATUS ='100'");
        if (!Check.Null(templateId)) {
            sqlbuf.append(" and a.TEMPLATEID = '" + templateId + "'");
        } else {
            sqlbuf.append(" and (a.TEMPLATETYPE = '1' or (a.TEMPLATETYPE = '2' and b.SHOPID = '" + shopId + "'))");
        }
        sqlbuf.append(" order by a.TEMPLATETYPE desc");
        sql = sqlbuf.toString();
        return sql;
    }
    
    /**
     * 查询开票项目税别、税率
     * @param eid
     * @param projectId
     * @return
     */
    public String getFaPiaoPojo(String eid,String projectId,String langType){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select a.*,B.PROJECTNAME from DCP_FAPIAO_PROJ  a  " +
                " left join DCP_FAPIAO_PROJ_LANG B on A.EID=B.EID AND A.PROJECTID=B.PROJECTID " +
                "where a.eid = '"+eid+"' and a.PROJECTID = '"+projectId+"' and a.STATUS = '100' and b.LANG_TYPE = '"+langType+"' ");
        sql = sqlbuf.toString();
        return sql;
    }
    
    /**
     * 查询各种类型单据是否已退
     * @param eId
     * @param orderNos
     * @param billtype Sale-销售单  Card-售卡 Coupon-售券 Recharge-充值
     * @return
     */
    public String CheckOrderIsReturn(String eId,List<String> orderNos,String billtype){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        String ordrNos_Str = PosPub.getArrayStrSQLIn(orderNos.toArray(new String[orderNos.size()]));
        if (!Check.Null(ordrNos_Str)) {
            if ("Sale".equals(billtype)) {
                sqlbuf.append("SELECT SALENO orderNo,ISRETURN,SHOPID，'100' STATUS ,'0' QUOTESIGN FROM dcp_sale WHERE eid = '" + eId + "' AND  SALENO in( " + ordrNos_Str + ")");
            } else if ("Card".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo,DECODE(REFUND,'1','Y','N') ISRETURN,SHOPID,STATUS,QUOTESIGN FROM CRM_CARDSALE WHERE eid = '" + eId + "' AND  (BILLNO in( " + ordrNos_Str + ") or THIRDTRANSNO IN ("+ordrNos_Str+"))");
            } else if ("Coupon".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo ,DECODE(REFUND,'1','Y','N') ISRETURN, SHOPID,STATUS,QUOTESIGN FROM CRM_COUPONSALE WHERE eid = '" + eId + "' AND  (BILLNO in( " + ordrNos_Str + ") or THIRDTRANSNO IN ("+ordrNos_Str+"))");
            } else if ("Recharge".equals(billtype)) {
                sqlbuf.append("SELECT BILLNO orderNo ,DECODE(REFUND,'1','Y','N') ISRETURN,SHOPID ,STATUS,QUOTESIGN FROM CRM_CARDRECHARGE WHERE eid = '" + eId + "' AND  (BILLNO in( " + ordrNos_Str + ") or THIRDTRANSNO IN ("+ordrNos_Str+"))");
            }
        }
        sql = sqlbuf.toString();
        return sql;
    }
    
    /**
     * 根据不同的单据类型进行查询金额
     * @param eId
     * @param orderNos
     * @param billtype  Sale-销售单  Card-售卡 Coupon-售券 Recharge-充值
     * @param taxrate
     * @return
     */
    public String getOrderPayInfoByOrderNos(String eId,List<String> orderNos,String billtype,Double taxrate){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        String ordrNos_Str = PosPub.getArrayStrSQLIn(orderNos.toArray(new String[orderNos.size()]));
        if (!Check.Null(ordrNos_Str)) {
            if ("Sale".equals(billtype)) {
                //【ID1027960】 霸王3.0   美团、饿了么外卖小票的的金额未同步至诺诺发票金额 by jinzma 20220811 关联来源单号查询
                /*sqlbuf.append("  SELECT SALENO orderNo, ROUND(sum(PAY-CHANGED-EXTRA), 2) AS taxamt , ROUND(SUM(PAY-CHANGED-EXTRA) * " + taxrate + " / (1 + " + taxrate + "), 2) AS tax , ROUND(sum(PAY-CHANGED-EXTRA) - ROUND((SUM(PAY-CHANGED-EXTRA) * " + taxrate + ") / (1 + " + taxrate + "), 2), 2) AS taxfreeamt " +
                        " FROM DCP_SALE_PAY a LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID  AND a.PAYCODE = b.PAYCODE  AND a.PAYTYPE  = b.PAYTYPE " +
                        " WHERE a.EID = '" + eId + "'  AND a.SALENO in(" + ordrNos_Str + ") ");
                sqlbuf.append(" AND b.CANOPENINVOICE  = '1' ");
                sqlbuf.append(" GROUP BY a.SALENO ,b.CANOPENINVOICE");*/
                
                sqlbuf.append(" "
                        //【ID1028648】霸王3.0 美团核销的单据，发票取值不正确-DCP发票服务 by jinzma
                        // 返回参数中detail.taxamt算法调整，修改前：支付方式中可开票金额合计【不含找零，不含溢收】  修改后：支付方式中可开票 顾客实付CUSTPAYREAL合计
                        + " select a.SALENO orderNo, ROUND(sum(b.custpayreal),2) AS taxamt," // ROUND(sum(b.PAY-b.CHANGED-b.EXTRA),2) AS taxamt
                        //+ " ROUND(SUM(b.PAY-b.CHANGED-b.EXTRA) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax,"
                        //+ " ROUND(sum(b.PAY-b.CHANGED-b.EXTRA) - ROUND((SUM(b.PAY-b.CHANGED-b.EXTRA) * "+taxrate+") / (1 + "+taxrate+"), 2), 2) AS taxfreeamt"
                        + " ROUND(SUM(b.custpayreal) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax,"
                        + " ROUND(sum(b.custpayreal) - ROUND((SUM(b.custpayreal) * "+taxrate+") / (1 + "+taxrate+"), 2), 2) AS taxfreeamt"
                        + " from dcp_sale a"
                        + " inner join dcp_sale_pay b on a.eid=b.eid and a.shopid=b.shopid and a.saleno=b.saleno"
                        + " inner join dcp_paytype c ON a.eid=c.eid AND b.PAYCODE = c.PAYCODE  AND b.PAYTYPE  = c.PAYTYPE"
                        + " WHERE a.EID='"+eId+"' AND c.CANOPENINVOICE = '1'"
                        + " AND (a.SALENO in("+ordrNos_Str+") or (a.type='0' and a.otype='3' and a.ofno in("+ordrNos_Str+")))"
                        + " GROUP BY a.saleno"
                );
            } else if ("Card".equals(billtype)) {
                //【ID1028648】霸王3.0 美团核销的单据，发票取值不正确-DCP发票服务 by jinzma
                // 返回参数中detail.taxamt算法调整，修改前：支付方式中可开票金额合计【不含找零，不含溢收】  修改后：支付方式中可开票 顾客实付CUSTPAYREAL合计
                sqlbuf.append("SELECT a.BILLNO orderNo, ROUND(sum(a.custpayreal), 2) AS taxamt , "     //ROUND(sum(WORTH), 2) AS taxamt
                        //+ " ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , "
                        //+ " ROUND(sum(WORTH) - ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt "
                        + " ROUND(SUM(a.custpayreal) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , "
                        + " ROUND(sum(a.custpayreal) - ROUND(SUM(a.custpayreal) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt "
                        + " FROM CRM_CARDSALEPAY a  "
                        + " LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID AND a.PAYTYPEID = b.PAYTYPE  "
                        + " left join CRM_CARDSALE c on a.eid = c.eid and a.BILLNO  = c.BILLNO "
                        + " WHERE a.EID = '"+eId+"' AND (a.BILLNO IN ("+ordrNos_Str+") or c.THIRDTRANSNO IN ("+ordrNos_Str+")) "
                        + " AND b.CANOPENINVOICE = '1' "
                        + " GROUP BY a.BILLNO, b.CANOPENINVOICE ");
            } else if ("Coupon".equals(billtype)) {
                //【ID1028648】霸王3.0 美团核销的单据，发票取值不正确-DCP发票服务 by jinzma
                // 返回参数中detail.taxamt算法调整，修改前：支付方式中可开票金额合计【不含找零，不含溢收】  修改后：支付方式中可开票 顾客实付CUSTPAYREAL合计
                sqlbuf.append("SELECT a.BILLNO orderNo, ROUND(sum(a.custpayreal), 2) AS taxamt , "    //ROUND(sum(WORTH), 2) AS taxamt
                        //+ " ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , "
                        //+ " ROUND(sum(WORTH) - ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt "
                        + " ROUND(SUM(a.custpayreal) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , "
                        + " ROUND(sum(a.custpayreal) - ROUND(SUM(a.custpayreal) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt "
                        + " FROM CRM_COUPONSALEPAY a  "
                        + " LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID AND a.PAYTYPEID = b.PAYTYPE  "
                        + " left join CRM_COUPONSALE c on a.eid = c.eid and a.BILLNO  = c.BILLNO "
                        + " WHERE a.EID = '"+eId+"' AND (a.BILLNO IN ("+ordrNos_Str+") or c.THIRDTRANSNO IN( "+ordrNos_Str+")) "
                        + " AND b.CANOPENINVOICE = '1' "
                        + " GROUP BY a.BILLNO, b.CANOPENINVOICE ");
            } else if ("Recharge".equals(billtype)) {
                //【ID1028648】霸王3.0 美团核销的单据，发票取值不正确-DCP发票服务 by jinzma
                // 返回参数中detail.taxamt算法调整，修改前：支付方式中可开票金额合计【不含找零，不含溢收】  修改后：支付方式中可开票 顾客实付CUSTPAYREAL合计
                sqlbuf.append("SELECT a.BILLNO orderNo, ROUND(sum(a.custpayreal), 2) AS taxamt , "           //ROUND(sum(WORTH), 2) AS taxamt
                        // + " ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , "
                        // + " ROUND(sum(WORTH) - ROUND(SUM(WORTH) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt "
                        + " ROUND(SUM(a.custpayreal) * "+taxrate+" / (1 + "+taxrate+"), 2) AS tax , "
                        + " ROUND(sum(a.custpayreal) - ROUND(SUM(a.custpayreal) * "+taxrate+" / (1 + "+taxrate+"), 2), 2) AS taxfreeamt "
                        + " FROM CRM_CARDRECHARGEPAY a  "
                        + " LEFT JOIN DCP_PAYTYPE b ON a.EID = b.EID AND a.PAYTYPEID = b.PAYTYPE  "
                        + " left join CRM_CARDRECHARGE c on a.eid = c.eid and a.BILLNO  = c.BILLNO "
                        + " WHERE a.EID = '"+eId+"' AND ( a.BILLNO IN ("+ordrNos_Str+") or c.THIRDTRANSNO IN ("+ordrNos_Str+")) "
                        + " AND b.CANOPENINVOICE = '1' "
                        + " GROUP BY a.BILLNO, b.CANOPENINVOICE ");
            }
        }
        sql = sqlbuf.toString();
        return sql;
    }
    
}
