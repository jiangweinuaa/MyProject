package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EInvoiceQrcodeGet_OpenReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceQrcodeGet_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 获取开票二维码
 * @author: wangzyc
 * @create: 2022-02-23
 */
public class DCP_EInvoiceQrcodeGet_Open extends SPosAdvanceService<DCP_EInvoiceQrcodeGet_OpenReq, DCP_EInvoiceQrcodeGet_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_EInvoiceQrcodeGet_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_EInvoiceQrcodeGet_OpenReq.level1Elm request = req.getRequest();
        
        if (Check.Null(request.getOrderNo())) {
            errMsg.append("业务单号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getBillType())) {
            errMsg.append("业务单据类型不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("开票门店id不可为空值, ");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_EInvoiceQrcodeGet_OpenReq> getRequestType() {
        return new TypeToken<DCP_EInvoiceQrcodeGet_OpenReq>() {
        };
    }
    
    @Override
    protected DCP_EInvoiceQrcodeGet_OpenRes getResponseType() {
        return new DCP_EInvoiceQrcodeGet_OpenRes();
    }
    
    
    @Override
    protected void processDUID(DCP_EInvoiceQrcodeGet_OpenReq req, DCP_EInvoiceQrcodeGet_OpenRes res) throws Exception {
        DCP_EInvoiceQrcodeGet_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String shopId = request.getShopId();
        // 0-发票平台二维码 1-鼎捷发票二维码，默认0
        String invoiceQrCodeType = "0";
        if (!Check.Null(request.getInvoiceQrCodeType())) {
            invoiceQrCodeType = request.getInvoiceQrCodeType();
        }
        
        String templateId = request.getTemplateId();
        String orderNo = request.getOrderNo();
        String billType = request.getBillType();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        SimpleDateFormat sdfh = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String invoiceBillNo = sdfh.format(new Date());
        String dateTime = sdf.format(new Date());
        
        MyCommon myCommon = new MyCommon();
        try {
            /**
             * 1.优先根据发票参数模板ID查找发票模板，如发票参数模板id为空则根据门店查找
             */
            String sql = getQuerySql(req);
            List<Map<String, Object>> getPARAMSs = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getPARAMSs)) {
                // 优先取门店 取第一个
                Map<String, Object> getParams = getPARAMSs.get(0);
                String platformtype = getParams.get("PLATFORMTYPE").toString();
                String params = getParams.get("PARAMS").toString();
                Map<String, String> invoiceParmsMap = myCommon.invoiceParmsStringToMap(params);
                
                String serviceUrl = "";
                String appKey = invoiceParmsMap.getOrDefault("appKey", ""); // 企业6位代码
                String appSecret = invoiceParmsMap.getOrDefault("appSecret", "");
                String drawer = invoiceParmsMap.getOrDefault("drawer", ""); // 开票人
                String receiver = invoiceParmsMap.getOrDefault("receiver", ""); // 收款人
                String reviewer = invoiceParmsMap.getOrDefault("reviewer", ""); // 复核人
                String sellerNumber = invoiceParmsMap.getOrDefault("sellerNumber", ""); // 销方税号
                String sellerPhone = invoiceParmsMap.getOrDefault("sellerPhone", ""); // 销方电话
                String sellerAddress = invoiceParmsMap.getOrDefault("sellerAddress", ""); // 销方地址
                String sellerAccount = invoiceParmsMap.getOrDefault("sellerAccount", ""); // 销方银行账号和开户行地址
                String taxRate = invoiceParmsMap.getOrDefault("taxRate", ""); // 税率
                String expireTime = invoiceParmsMap.getOrDefault("expireTime", ""); // 过期时间
                String serviceUrl_QRCODE = invoiceParmsMap.getOrDefault("serviceUrl_QRCODE", ""); // 开票二维码地址
                String serviceUrl_QRCODE2 = invoiceParmsMap.getOrDefault("serviceUrl_QRCODE2", ""); // 鼎捷二维码地址
                
                
                if (!CollectionUtils.isEmpty(getPARAMSs)) {
                    
                    if ("0".equals(invoiceQrCodeType)) {
                        
                        if (Check.Null(appSecret)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "发票参数appSecret未配置!");
                        }
                        
                        if ("#NUONUO_V1".equals(platformtype)) {
                            // 根据订单找开票记录DCP_EINVOICE_BUSINESS；
                            sql = "SELECT DISTINCT a.EID, a.INVOICEBILLNO, b.SOURCESHOPID, b.SOURCEBILLNO, a.SOURCEBILLTYPE , a.STATUS, a.ISAPPLY,a.QRCODE " +
                                    " FROM DCP_EINVOICE a LEFT JOIN DCP_EINVOICE_BUSINESS b ON a.EID = b.EID AND a.INVOICEBILLNO = b.INVOICEBILLNO " +
                                    " WHERE b.SOURCEBILLNO = '" + orderNo + "' and a.eid = '" + eId + "'";
                            List<Map<String, Object>> business = this.doQueryData(sql, null);
                            // 对orderNo 后四位进行加密

//                        String key = appSecret.substring(0, 8);
//                        String offset = appSecret.substring(appSecret.length() - 8, appSecret.length());
                            String orderAfter = invoiceBillNo.substring(invoiceBillNo.length() - 4, invoiceBillNo.length());
//                        DESDZFP.key =  new String(Base64.encode(appSecret.getBytes()));
//                        String desOrder = myCommon.encrypt(orderAfter, key, offset);
//                       String desOrder =  DesUtil.encrypt_CBC(orderAfter,appSecret.substring(0, 8),appSecret.substring(8, 16));
//                        String desOrder = DESDZFP.encrypt(orderAfter4);
                            if (CollectionUtils.isEmpty(business)) {
                                // 生成新的二维码     成功后插入开票记录DCP_EINVOICE、DCP_EINVOICE_BUSINESS；
                                
                                String qrCodeUrl = getQrCodeUrl(appKey, "", serviceUrl_QRCODE, invoiceBillNo, expireTime);
                                
                                DCP_EInvoiceQrcodeGet_OpenRes.level1Elm lv1 = res.new level1Elm();
                                lv1.setQrCode(qrCodeUrl);
                                
                                String[] columns_EINVOICE = {"EID", "INVOICEBILLNO", "SOURCEBILLTYPE", "PLATFORMTYPE", "INVOICETYPE", "TAXRATE", "DRAWER", "RECEIVER", "REVIEWER"
                                        , "SALETAXNUM", "SALETEL", "SALEADDRESS", "SALEACCOUNT", "QRCODETIME", "QRCODE", "CREATETIME", "UPDATETIME"};
                                String[] columns_EINVOICE_BUSINESS = {"EID", "INVOICEBILLNO", "SOURCEBILLNO", "SOURCESHOPID"};
                                DataValue[] insValue = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(invoiceBillNo, Types.VARCHAR),
                                        new DataValue(request.getBillType(), Types.VARCHAR),
                                        new DataValue(platformtype, Types.VARCHAR),
                                        new DataValue("1", Types.VARCHAR),
                                        new DataValue(taxRate, Types.VARCHAR),
                                        new DataValue(drawer, Types.VARCHAR),
                                        new DataValue(receiver, Types.VARCHAR),
                                        new DataValue(reviewer, Types.VARCHAR),
                                        new DataValue(sellerNumber, Types.VARCHAR),
                                        new DataValue(sellerPhone, Types.VARCHAR),
                                        new DataValue(sellerAddress, Types.VARCHAR),
                                        new DataValue(sellerAccount, Types.VARCHAR),
                                        new DataValue(dateTime, Types.DATE),
                                        new DataValue(qrCodeUrl, Types.VARCHAR), // 二维码链接地址
                                        new DataValue(dateTime, Types.DATE),
                                        new DataValue(dateTime, Types.DATE),
                                };
                                
                                InsBean ib1 = new InsBean("DCP_EINVOICE", columns_EINVOICE);
                                ib1.addValues(insValue);
                                this.addProcessData(new DataProcessBean(ib1));
                                
                                DataValue[] insValue2 = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(invoiceBillNo, Types.VARCHAR),
                                        new DataValue(orderNo, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR)
                                };
                                
                                InsBean ib2 = new InsBean("DCP_EINVOICE_BUSINESS", columns_EINVOICE_BUSINESS);
                                ib2.addValues(insValue2);
                                this.addProcessData(new DataProcessBean(ib2));
                                
                                this.doExecuteDataToDB();
                                
                                //【ID1028227】【霸王】诺诺发票线上开票--服务 by jinzma 20220825 增加出参status和isApply 安驰
                                lv1.setStatus("");
                                lv1.setIsApply("");
                                
                                res.setDatas(lv1);
                            } else {
                                //    根据诺诺的二维码接口重新生成新二维码；[未申请开票、未开票成功]  成功后更新开票记录DCP_EINVOICE、DCP_EINVOICE_BUSINESS；
                                String invoicebillno = business.get(0).get("INVOICEBILLNO").toString();
                                String status = business.get(0).get("STATUS").toString();
                                String isapply = business.get(0).get("ISAPPLY").toString();
                                String qrCodeUrl = "";
                                if ("N".equals(isapply) || !"Y".equals(status)) {
                                    qrCodeUrl = getQrCodeUrl(appKey, "", serviceUrl_QRCODE, invoicebillno, expireTime);
                                    
                                    UptBean ub = new UptBean("DCP_EINVOICE");
                                    ub.addUpdateValue("QRCODETIME", new DataValue(dateTime, Types.DATE));
                                    ub.addUpdateValue("QRCODE", new DataValue(qrCodeUrl, Types.VARCHAR));
                                    ub.addUpdateValue("TAXRATE", new DataValue(taxRate, Types.VARCHAR));
                                    ub.addUpdateValue("DRAWER", new DataValue(drawer, Types.VARCHAR));
                                    ub.addUpdateValue("RECEIVER", new DataValue(receiver, Types.VARCHAR));
                                    ub.addUpdateValue("REVIEWER", new DataValue(reviewer, Types.VARCHAR));
                                    ub.addUpdateValue("SALETAXNUM", new DataValue(sellerNumber, Types.VARCHAR));
                                    ub.addUpdateValue("SALETEL", new DataValue(sellerPhone, Types.VARCHAR));
                                    ub.addUpdateValue("SALEADDRESS", new DataValue(sellerAddress, Types.VARCHAR));
                                    ub.addUpdateValue("SALEACCOUNT", new DataValue(sellerAccount, Types.VARCHAR));
                                    ub.addUpdateValue("UPDATETIME", new DataValue(sdf.format(new Date()), Types.DATE));
                                    //condition
                                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    ub.addCondition("INVOICEBILLNO", new DataValue(invoicebillno, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(ub));
                                    this.doExecuteDataToDB();
                                }
                                DCP_EInvoiceQrcodeGet_OpenRes.level1Elm lv1 = res.new level1Elm();
                                lv1.setQrCode(qrCodeUrl);
                                
                                //【ID1028227】【霸王】诺诺发票线上开票--服务 by jinzma 20220825 增加出参status和isApply 安驰
                                lv1.setStatus(status);
                                lv1.setIsApply(isapply);
                                
                                res.setDatas(lv1);
                            }
                        }
                        
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                    } else if ("1".equals(invoiceQrCodeType)) {
                        // 鼎捷开票二维码
                        String appid = req.getApiUserCode();
                        // 拼接开票二维码链接
                        String qrCode =  serviceUrl_QRCODE2+"&orderNo="+orderNo+"&billType="+billType;
                        DCP_EInvoiceQrcodeGet_OpenRes.level1Elm lv1 = res.new level1Elm();
                        lv1.setQrCode(qrCode);
                        
                        //【ID1028227】【霸王】诺诺发票线上开票--服务 by jinzma 20220825 增加出参status和isApply 安驰
                        lv1.setStatus("");
                        lv1.setIsApply("");
                        res.setDatas(lv1);
                    }
                    
                    
                } else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "发票参数未配置!");
                }
                
            }
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_EInvoiceQrcodeGet_OpenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_EInvoiceQrcodeGet_OpenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_EInvoiceQrcodeGet_OpenReq req) throws Exception {
        return null;
    }
    
    
    /**
     * 拼接二维码地址
     *
     * @param desOrder
     * @param serviceUrl_QRCODE
     * @param orderNo
     * @param expireTime
     * @return
     */
    private String getQrCodeUrl(String appkey, String desOrder, String serviceUrl_QRCODE, String orderNo, String expireTime) {
        String qrCodeUrl = "";
//        if (Check.Null(desOrder)) {
//            // 使用不加密方式
//            StringBuffer qrbuf = new StringBuffer();
//            qrbuf.append(serviceUrl_QRCODE).append("?jskpCode="+appkey+"").append("&orderno="+orderNo);
//            if(!Check.Null(expireTime)){
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(expireTime));
//                long timeInMillis = calendar.getTimeInMillis();
//                qrbuf.append("&expiretime="+timeInMillis);
//            }
//            qrCodeUrl = qrbuf.toString();
//        }else {
//            // 如果加密DES不为空 则用加密方式
//            StringBuffer qrbuf = new StringBuffer();
//            String orderno2 = orderNo.substring(0,orderNo.length()-4);
//            String j = appkey+":"+orderno2+""+desOrder;
//            qrbuf.append(serviceUrl_QRCODE).append("?j="+j);
//            if(!Check.Null(expireTime)){
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(expireTime));
//                long timeInMillis = calendar.getTimeInMillis();
//                qrbuf.append("&expiretime="+timeInMillis);
//            }
//            qrCodeUrl = qrbuf.toString();
//        }
        
        // 使用不加密方式
        StringBuffer qrbuf = new StringBuffer();
        qrbuf.append(serviceUrl_QRCODE).append("?jskpCode=" + appkey + "").append("&orderno=" + orderNo);
        if (!Check.Null(expireTime)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(expireTime));
            long timeInMillis = calendar.getTimeInMillis();
            qrbuf.append("&expiretime=" + timeInMillis);
        }
        qrCodeUrl = qrbuf.toString();
        return qrCodeUrl;
    }
    
    @Override
    protected String getQuerySql(DCP_EInvoiceQrcodeGet_OpenReq req) throws Exception {
        DCP_EInvoiceQrcodeGet_OpenReq.level1Elm request = req.getRequest();
        String templateId = request.getTemplateId();
        String shopId = request.getShopId();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("SELECT DISTINCT a.PLATFORMTYPE,a.TEMPLATETYPE,a.PARAMS FROM DCP_FAPIAO_TEMPLATE a  " +
                "LEFT JOIN DCP_FAPIAO_TEMPLATE_SHOP b ON a.TEMPLATEID = b.TEMPLATEID  AND a.EID  = b.EID  " +
                " WHERE a.EID  = '" + req.geteId() + "'  AND a.STATUS ='100'");
        if (!Check.Null(templateId)) {
            sqlbuf.append(" and a.TEMPLATEID = '" + templateId + "'");
        } else {
            sqlbuf.append(" and (a.TEMPLATETYPE = '1' or (a.TEMPLATETYPE = '2' and b.SHOPID = '" + shopId + "'))");
        }
        sqlbuf.append(" order by a.TEMPLATETYPE desc");
        sql = sqlbuf.toString();
        return sql;
    }
    
}
