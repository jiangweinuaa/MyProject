package com.dsc.spos.waimai;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderShippingReq;
import com.dsc.spos.json.cust.req.DCP_StockLock_OpenReq;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.cust.res.DCP_OrderCreateRes.Card;
import com.dsc.spos.json.cust.res.DCP_WMSPGoodsDetailRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.InvoiceCaculateRequest;
import com.dsc.spos.model.InvoiceCreateRequest;
import com.dsc.spos.model.JindieGoodsDetail;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderAbnormal;
import com.dsc.spos.waimai.entity.orderAbnormalDetail;
import com.dsc.spos.waimai.entity.orderAbnormalType;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderGoodsItemAgio;
import com.dsc.spos.waimai.entity.orderGoodsItemMessage;
import com.dsc.spos.waimai.entity.orderInvoice;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderPay;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.convert.Convert;

public class HelpTools {
    
    private static Logger logger = LogManager.getLogger(HelpTools.class);
    
    //美团饿了么渠道外卖订单商家实收算法，0-嘉华算法，1-店铺收入。其他渠道取默认值0
    private static String WaiMaiMerReceiveMode = "";
    //美团饿了么渠道外卖商品规格和属性是否拆分，Y-拆分，N-不拆分
    private static String WaiMaiGoodsSplit = "";
    public static Map<String,Map<String,String>> elmMappingShopList = null;
    public static Map<String,Map<String,String>> mtMappingShopList = null;
    public static Map<String,Map<String,String>> jbpMappingShopList = null;
    public static Map<String,Map<String,String>> sgmtMappingShopList = null;
    public static Map<String,Map<String,String>> dyMappingShopList = null;
    /**
     * 饿了么门店对应的appkey、appsecret的配置参数
     */
    public static Map<String,Map<String,String>> elmShopIdConfigList = null;
    // 写日志
    public static void writelog_waimai(String log) throws IOException {
        // 生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
        String path = System.getProperty("user.dir") + "\\log\\waimailog" + sdFormat + ".txt";
        File file = new File(path);
        
        String dirpath = System.getProperty("user.dir") + "\\log";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        
        FileOutputStream writerStream = new FileOutputStream(file,true);
        OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);
        
        // 前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
        String slog = stFormat + " " + log + "\r\n";
        
        stFormat=null;
        sdFormat=null;
        
        writer.append(slog);
        writer.close();
        writer=null;
        
        osw.close();
        osw=null;
        
        writerStream.close();
        writerStream=null;
        
        file=null;
        
        sdFormat=null;
    }
    
    // 写日志
    public static void writelog_waimaiException(String log) throws IOException {
        // 生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
        String path = System.getProperty("user.dir") + "\\log\\waimaiExceptionlog" + sdFormat + ".txt";
        File file = new File(path);
        
        String dirpath = System.getProperty("user.dir") + "\\log";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        
        FileOutputStream writerStream = new FileOutputStream(file,true);
        OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);
        
        // 前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
        String slog = stFormat + " " + log + "\r\n";
        
        stFormat=null;
        sdFormat=null;
        
        writer.append(slog);
        writer.close();
        writer=null;
        
        osw.close();
        osw=null;
        
        writerStream.close();
        writerStream=null;
        
        file=null;
        
        sdFormat=null;
        
    }
    
    // 写日志
    public static void writelog_fileName(String log, String fileName) throws IOException {
        // 生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
        String path = System.getProperty("user.dir") + "\\log\\" + fileName + sdFormat + ".txt";
        File file = new File(path);
        
        String dirpath = System.getProperty("user.dir") + "\\log";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        
        FileOutputStream writerStream = new FileOutputStream(file,true);
        OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);
        
        // 前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
        String slog = stFormat + " " + log + "\r\n";
        stFormat=null;
        
        stFormat=null;
        sdFormat=null;
        
        writer.append(slog);
        writer.close();
        writer=null;
        
        osw.close();
        osw=null;
        
        writerStream.close();
        writerStream=null;
        
        file=null;
        
        sdFormat=null;
    }
    
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        
        }
        return result;
    }
    
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        
        }
        return result;
    }
    
    public static order GetMicroMarketResponse(String responseStr,String loadDocType, StringBuffer errorMessage) throws Exception {
        
        
        // String responseStr_1 = getURLDecoderString(responseStr);//一次转码
        // （获取的是：%E6%B5%8B%E8%AF%95）
        // writelog_waimai("【美团URL转码后1】"+responseStr_1);
        // String responseStr_2 = getURLDecoderString(responseStr_1);//二次转码（获取为
        // 中文）
        // writelog_waimai("【美团URL转码后2】"+responseStr_2);
        // 解析收到的微商城请求
        try {
            order orderInfo = new order();
            JSONObject jsonobjresponse = new JSONObject();
            JSONObject jsonobj = new JSONObject(responseStr);
            JSONObject datasobj = jsonobj.getJSONObject("datas");
            
            String eId = "99";
            String shopno = " ";// 主键不能为空，所以默认空格
            String shopname = "";
            
            eId = datasobj.get("eId").toString();// 企业编号
            orderInfo.seteId(eId);
            orderInfo.setLoadDocType(loadDocType);
            orderInfo.setIsBook("Y");// 预定单
            
            String channelId = "";//下单渠道（公众号appid）
            if(!datasobj.isNull("channelId"))
            {
                channelId = datasobj.optString("channelId");//下单渠道（公众号appid）
            }
            orderInfo.setChannelId(channelId);
            
            String billType = "";		//手机商城单别
            if(!datasobj.isNull("billType"))
            {
                billType = datasobj.get("billType").toString();
            }
            orderInfo.setLoadDocBillType(billType);
            
            String orderNo = datasobj.get("orderNo").toString();
            orderInfo.setOrderNo(orderNo);
            orderInfo.setLoadDocOrderNo(orderNo);
            orderInfo.setHeadOrderNo(datasobj.optString("headOrderNo"));
            
            int sn = 0;
            try
            {
                sn = Integer.parseInt(orderNo.substring(orderNo.length()-6));//流水号后6位
            } catch (Exception e) {
            }
            orderInfo.setSn(sn+"");
            
            String belfirm = "";
            if (!datasobj.isNull("companyId")) {
                belfirm = datasobj.get("companyId").toString();
            }
            
            orderInfo.setBelfirm(belfirm);
            
            String shopno_ship = "";//配送门店
            if (!datasobj.isNull("shopId"))// 商城配送门店
            {
                shopno_ship = datasobj.get("shopId").toString();
            }
            String shopname_ship = "";//配送门店
            
            if (!datasobj.isNull("shopName"))//
            {
                shopname_ship = datasobj.get("shopName").toString();
            }
            orderInfo.setShippingShopNo(shopno_ship);
            orderInfo.setShippingShopName(shopname_ship);
            
            String shopno_create = "";// 下单门店
            String shopname_create = "";// 下单门店名称
            if (!datasobj.isNull("shopId_create"))// 如果有下单门店
            {
                shopno_create = datasobj.get("shopId_create").toString();
            }
            if (!datasobj.isNull("shopName_create"))// 如果有下单门店
            {
                shopname_create = datasobj.get("shopName_create").toString();
            }
            orderInfo.setShopNo(shopno_create);
            orderInfo.setShopName(shopname_create);
            
            //收款门店，货郎商城需求
            String payShopId = datasobj.optString("payShopId","");
            orderInfo.setPayShopId(payShopId);
            
            orderInvoice invoiceDetail = new orderInvoice();
            //处理一下发票
            JSONObject datainvinfo = null;//发票申请信息
            JSONArray datainvno = null;//发票信息
            try
            {
                datainvinfo = datasobj.optJSONObject("invoiceInfo");//发票申请信息
                datainvno =	datasobj.optJSONArray("invoiceInfo2");//发票信息
                
            } catch (Exception e) {
            }
            
            // 客户自提不管，台湾的自提也需要调度
            //通过配置文件读取
            String langtype="zh_CN";
            List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
            if(lstProd!=null&&!lstProd.isEmpty())
            {
                langtype=lstProd.get(0).getHostLang().getValue();
            }
            
            String peopleType="";//主体类型：1公司，2个人
            String invoiceType = "";
            String isInvoice ="N";
            
            //大陆对应的是税别号
            String taxRegnumber = "";
            String buyerGuiNo ="";//买家统编
            String carrierCode ="";//载具类别编码
            String carriershowId="";//载具显码
            String carrierhiddenId ="";//载具隐码
            String loveCode="";//爱心码
            if (datainvinfo!=null)
            {
                
                if(!datainvinfo.isNull("peopleType"))
                {
                    peopleType = datainvinfo.get("peopleType").toString();
                }
                
                if (peopleType!=null&&peopleType.trim().length()>0)
                {
                    isInvoice = "Y";
                }
                
                //发票类型
                //台湾发票类型：2 二联-个人; 3 三联-公司;
                //大陆：9-普通发票 8-增值税专用发票
                if(!datainvinfo.isNull("invoiceType"))
                {
                    invoiceType = datainvinfo.get("invoiceType").toString();//台湾才取这个节点
                }
                
                
                
                if(!datainvinfo.isNull("invoiceNumber"))
                {
                    if(langtype.equals("zh_CN"))
                    {
                        taxRegnumber = datainvinfo.get("invoiceNumber").toString();// 节点值可能返回为null不能用opString
                        jsonobjresponse.put("taxRegnumber", taxRegnumber);
                    }
                }
                
                if (invoiceType.equals("3"))//三联-公司
                {
                    if(!datainvinfo.isNull("invoiceNumber"))
                    {
                        buyerGuiNo = datainvinfo.get("invoiceNumber").toString();// 节点值可能返回为null不能用opString
                    }
                }
                
                //载具类别 1.手机条码载具 3.会员载具 4.捐赠  9-无/大陆
                String carrierType="";
                if(!datainvinfo.isNull("carrierType"))
                {
                    carrierType = datainvinfo.get("carrierType").toString();// 节点值可能返回为null不能用opString
                }
                
                if (carrierType.equals("1"))//手机条码载具 固定
                {
                    carrierCode="3J0002";
                    
                    if(!datainvinfo.isNull("invoiceNumber"))
                    {
                        carrierhiddenId = datainvinfo.get("invoiceNumber").toString();// 节点值可能返回为null不能用opString
                        carriershowId=carrierhiddenId;
                    }
                }
                
                if (carrierType.equals("3"))//会员载具
                {
                    if(!datainvinfo.isNull("carrierHiddenId"))
                    {
                        carrierhiddenId = datainvinfo.get("carrierHiddenId").toString();// 节点值可能返回为null不能用opString
                        carriershowId=carrierhiddenId;
                    }
                }
                
                if (carrierType.equals("4"))//捐赠发票
                {
                    if(!datainvinfo.isNull("invoiceNumber"))
                    {
                        loveCode= datainvinfo.get("invoiceNumber").toString();// 节点值可能返回为null不能用opString
                    }
                }
                
                
            }
            
            invoiceDetail.setIsInvoice(isInvoice);
            invoiceDetail.setPeopleType(peopleType);
            invoiceDetail.setInvoiceType(invoiceType);
            invoiceDetail.setPassPort("");
            invoiceDetail.setFreeCode("");
            invoiceDetail.setBuyerGuiNo(buyerGuiNo);
            invoiceDetail.setCarrierCode(carrierCode);
            invoiceDetail.setCarrierShowId(carriershowId);
            invoiceDetail.setCarrierHiddenId(carrierhiddenId);
            invoiceDetail.setLoveCode(loveCode);
            invoiceDetail.setInvMemo("");
            
            //订单原价
            double totdisc = 0;
            try {
                
                totdisc = Double.parseDouble(datasobj.get("minusAmount").toString());
            } catch (Exception e) {
            }
            
            orderInfo.setTotDisc(totdisc);// 订单优惠总额
            orderInfo.setSellerDisc(0);// 商户优惠总额
            orderInfo.setPlatformDisc(0);// 平台优惠总额
            orderInfo.setPackageFee(0);// 餐盒费
            orderInfo.setServiceCharge(0);// 服务费
            
            
            
            //jsonobjresponse.put("address", address);
            String contactName = "";
            if(!datasobj.isNull("contactName"))
            {
                contactName = datasobj.get("contactName").toString();// 联系人
            }
            orderInfo.setContMan(contactName);
            orderInfo.setGetMan(contactName);//没有放回收货人节点，默认联系人
            String contactTelephone ="";
            if(!datasobj.isNull("contactTelephone"))
            {
                contactTelephone = datasobj.get("contactTelephone").toString();
            }
            orderInfo.setContTel(contactTelephone);
            orderInfo.setGetManTel(contactTelephone);//没有放回收货人电话，默认联系人电话
            
            //收货人名称
            String getMan = "";
            if(!datasobj.isNull("contactNameSelf"))
            {
                getMan = datasobj.get("contactNameSelf").toString();
            }
            if (!getMan.isEmpty())
            {
                orderInfo.setGetMan(getMan);
            }
            //收货人电话
            String getMantel = "";
            if(!datasobj.isNull("contactTelephoneSelf"))
            {
                getMantel = datasobj.get("contactTelephoneSelf").toString();
            }
            if (!getMantel.isEmpty())
            {
                orderInfo.setGetManTel(getMantel);
            }
            
            
            String memberId = "";
            if(!datasobj.isNull("memberId"))
            {
                memberId = datasobj.get("memberId").toString();
            }
            orderInfo.setMemberId(memberId);
            
            String memberName = "";
            if(!datasobj.isNull("memberName"))
            {
                memberName = datasobj.get("memberName").toString();
            }
            orderInfo.setMemberName(memberName);
            
            String cardNo = "";
            if(!datasobj.isNull("cardNo"))
            {
                cardNo = datasobj.get("cardNo").toString();
            }
            orderInfo.setCardNo(cardNo);
            
            String isShipcompany = "N";
            if(!datasobj.isNull("isShipCompany"))
            {
                isShipcompany = datasobj.get("isShipCompany").toString();
            }
            //jsonobjresponse.put("isShipcompany", isShipcompany);// 总部配送 Y/N  3.0未对接
            
            String address = datasobj.get("address").toString();//后面拼接详细地址
            //新增省市区  08546  栏位名称与HelpTools.GetInsertOrder中保持一致
            String province=datasobj.optString("province","");
            orderInfo.setProvince(province);
            String city=datasobj.optString("city","");
            orderInfo.setCity(city);
            String county=datasobj.optString("county","");
            orderInfo.setCounty(county);
            
            /*String zipCode ="";//邮递区号
            if(!datasobj.isNull("zipCode"))
            {
                zipCode = datasobj.get("zipCode").toString();
            }*/
            
            String address2 ="";//更详细地址 20191213
            if(!datasobj.isNull("address2"))
            {
                address2 = datasobj.get("address2").toString();
            }
            String streetNo ="";//门牌号 20191213
            if(!datasobj.isNull("streetNo"))
            {
                streetNo = datasobj.get("streetNo").toString();
            }
            
            address = province+city+county+address+address2+streetNo;
            jsonobjresponse.put("address", address);//详细地址
            orderInfo.setAddress(address);
            
            
            String cashOnDelivery ="0";//货到付款标记default 0
            if(!datasobj.isNull("cashOnDelivery"))
            {
                cashOnDelivery = datasobj.get("cashOnDelivery").toString();
            }
            //jsonobjresponse.put("cashOnDelivery", cashOnDelivery);//货到付款标记default 0， 3.0未对接
            
            
            
            
            String longitude = "0";
            if (!datasobj.isNull("longitude"))
            {
                longitude = datasobj.get("longitude").toString();// 经度
            }
            orderInfo.setLongitude(longitude);// 经度
            
            String latitude = "0";
            if (!datasobj.isNull("latitude"))
            {
                latitude = datasobj.get("latitude").toString();// 纬度
            }
            orderInfo.setLatitude(latitude);// 纬度
            
            String virtualAccountCode="";//虚拟账户编码
            if(!datasobj.isNull("virtualAccountCode"))
            {
                virtualAccountCode = datasobj.get("virtualAccountCode").toString();
            }
            //jsonobjresponse.put("virtualAccountCode", virtualAccountCode); //3.0未对接
            
            // 微商城过来的参数，没有shipType，只有deliverType
            String shipType = "";//1.订单来源渠道 2.全国配送 3.顾客自提 5总部配送 6同城配送
            String deliverType = datasobj.optString("deliverType", "");// 取货方式 1=自提2=同城 3=快递  4=超取
            
            if (deliverType.equals("1") )
            {
                shipType = "3";
            }
            else if ( deliverType.equals("2"))
            {
                shipType = "6";
            }
            else if ( deliverType.equals("3"))
            {
                shipType = "2";
            }
            else
            {
                shipType = "1";//默认1 订单来源渠道配送
            }
            orderInfo.setShipType(shipType);
            String isMerPay = "Y";//是否商家结算配送费 商城默认Y
            orderInfo.setIsMerPay(isMerPay);
            
            String status = "1";// 订单状态 默认成 订单开立
            orderInfo.setStatus(status);
            
            //jsonobjresponse.put("ecCustomerNo", ecCustomerNo); //3.0未对接
            
            String orderDateTime = datasobj.get("orderDateTime").toString();// 下单时间
            // 2018-08-29
            // 16:57:13
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String orderDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Date date = null;
            try {
                date = format.parse(orderDateTime);
                orderDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
                orderDate =  new SimpleDateFormat("yyyyMMdd").format(date);
            } catch (Exception e) {
            
            }
            //jsonobjresponse.put("createDatetime", orderDateTime);// 下单时间，格式yyyyMMddHHmmssSSS
            orderInfo.setCreateDatetime(orderDateTime);
            orderInfo.setbDate(orderDate);//营业日期，线上订单没有，默认下单创建日期
            
            Calendar cal = Calendar.getInstance();// 获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            
            String sTime = df.format(cal.getTime());
            orderInfo.setsTime(sTime);
            
            
            String needDate = datasobj.get("needDate").toString();// 微商城格式 2018-08-08
            
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(needDate);
                needDate = new SimpleDateFormat("yyyyMMdd").format(date);
            } catch (Exception e) {
            
            }
            //jsonobjresponse.put("shipDate", needDate);
            orderInfo.setShipDate(needDate);// 配送日期格式yyyyMMdd 20180808
            
            String needTime = datasobj.get("needTime").toString();// 微商城格式 17:00
            try {
                date = new SimpleDateFormat("HH:mm").parse(needTime);
                needTime = new SimpleDateFormat("HHmmss").format(date);
            } catch (Exception e) {
            
            }
            //jsonobjresponse.put("shipTime", needTime);// 配送时间 HHmmss 170000
            orderInfo.setShipStartTime(needTime);// 配送开始时间 HHmmss 170000
            orderInfo.setShipEndTime(needTime);// 配送结束时间 HHmmss 170000
            
            //新加配送开始时间
            String beginNeedTime = "";
            if (datasobj.has("beginNeedTime"))
            {
                beginNeedTime = datasobj.get("beginNeedTime").toString();// 微商城格式 17:00
            }
            try {
                date = new SimpleDateFormat("HH:mm").parse(beginNeedTime);
                beginNeedTime = new SimpleDateFormat("HHmmss").format(date);
            } catch (Exception e) {
                beginNeedTime = "";
                
            }
            if (!beginNeedTime.isEmpty())
            {
                orderInfo.setShipStartTime(beginNeedTime);// 配送开始时间 HHmmss 170000
            }
            
            String message = "";// 买家留言
            try {
                message = datasobj.get("message").toString();// 可能没这个节点
            } catch (Exception e) {
                message = "";
            }
            //jsonobjresponse.put("memo", message);// 单头备注
            orderInfo.setMemo(message);// 单头备注
            /*
             * String totalAmount = datasobj.get("totalAmount").toString();//微商城
             * 订单总应付金额 jsonobjresponse.put("tot_oldAmt", totalAmount);//订单原价
             * jsonobjresponse.put("tot_Amt", totalAmount);//订单金额
             * jsonobjresponse.put("incomeAmt", totalAmount);//商家实收金额 String
             * payedAmount = datasobj.get("payedAmount").toString();//微商城 已付金额
             * jsonobjresponse.put("payAmt", payedAmount);//用户已支付金额
             */
            String deliverAmount = datasobj.get("deliverAmount").toString();// 微商城
            // 运费
            jsonobjresponse.put("shipFee", deliverAmount);// 配送费
            double deliverAmount_d = 0;
            try {
                
                deliverAmount_d = Double.parseDouble(deliverAmount);
            } catch (Exception e) {
            
            }
            orderInfo.setShipFee(deliverAmount_d);
            
            String goodsAmount = datasobj.get("goodsAmount").toString();// 微商城
            // 商品金额
            double goodsAmount_d = 0;
            try {
                
                goodsAmount_d = Double.parseDouble(goodsAmount);
            } catch (Exception e) {
            
            }
            //订单原价
            double totalAmounttemp = 0;
            try {
                
                totalAmounttemp = Double.parseDouble(datasobj.get("totalAmount").toString());
            } catch (Exception e) {
                totalAmounttemp=goodsAmount_d;
            }
            
            double totalAmount_d = goodsAmount_d + deliverAmount_d;// 订单总金额=商品总金额+配送费
            double totalAmount_old = totalAmounttemp + deliverAmount_d;// 订单总金额=商品总金额+配送费
            
            String totalAmount = String.valueOf(totalAmount_d);
            //jsonobjresponse.put("tot_oldAmt", totalAmount_old);// 订单总金额
            //jsonobjresponse.put("tot_Amt", totalAmount);// 订单总金额
            //jsonobjresponse.put("incomeAmt", totalAmount);// 订单总金额
            //jsonobjresponse.put("payAmt", totalAmount);// 订单总金额
            orderInfo.setTot_oldAmt(totalAmount_old);
            orderInfo.setTot_Amt(totalAmount_d);// 订单总金额
            orderInfo.setIncomeAmt(totalAmount_d);// 订单总金额
            orderInfo.setPayAmt(totalAmount_d);
            
            String payStatus = datasobj.get("payStatus").toString();// 微商城支付状态  0=未支付;1=订金已付;	2=已付清
            if (payStatus.equals("1"))
            {
                payStatus = "2";//部分付款
            }
            else if (payStatus.equals("2"))
            {
                payStatus = "3";//付清
            }
            else
            {
                payStatus = "1";//未支付
            }
            orderInfo.setPayStatus(payStatus);// 支付状态 1.未支付 2.部分支付 3.付清
            
            orderInfo.setRefundStatus("1");//1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功,7.用户申请部分退款 8.拒绝部分退款 9.部分退款失败 10.部分退款成功
            orderInfo.setShopShareShipfee(0);// 商家替用户承担的配送费
            
            String payBank = "";//付款银行
            if(!datasobj.isNull("payBank"))
            {
                payBank = datasobj.get("payBank").toString();
            }
            //jsonobjresponse.put("payBank", payBank);//3.0未对接
            
            String payAccount = "";//付款帐号
            if(!datasobj.isNull("payAccount"))
            {
                payAccount = datasobj.get("payAccount").toString();
            }
            //jsonobjresponse.put("payAccount", payAccount);//3.0未对接
            
            // 解析goods
            JSONArray goodsarray = datasobj.getJSONArray("goods");
            JSONArray array = new JSONArray();
            String detail_message = "";
            
            orderInfo.setInvoiceDetail(invoiceDetail);
            orderInfo.setGoodsList(new ArrayList<orderGoodsItem>());

            double TOT_QTY = 0;
            for (int i = 0; i < goodsarray.length(); i++) {
                try {
                    orderGoodsItem goodsItem = new orderGoodsItem();
                    goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                    JSONObject goodsobj = new JSONObject();
                    
                    JSONObject job = goodsarray.getJSONObject(i);
                    String isPackageMaster = job.optString("isPackageMaster", "");
                    if("2".equals(isPackageMaster) || "3".equals(isPackageMaster)){
                        orderInfo.setIsApportion("Y");
                    }

                    double price_old = 0;
                    double price = 0;
                    double quantity = 0;
                    String price_str = "0";
                    String quantity_str = "0";
						/*try {
							price_str = job.get("price").toString();
							price = Double.parseDouble(price_str);

						} catch (Exception e) {

						}*/
                    
                    try {
                        price_old = Double.parseDouble(job.get("price1").toString());
                    } catch (Exception e) {
                    
                    }
                    String disc_str = "0";
                    double disc_d = 0;
                    try {
                        disc_str = job.get("disc").toString();
                        disc_d = Double.parseDouble(disc_str);
                        
                    } catch (Exception e) {
                    
                    }
                    
                    try {
                        quantity_str = job.get("quantity").toString();
                        quantity = Double.parseDouble(quantity_str);
                        TOT_QTY+=quantity;
                        
                    } catch (Exception e) {
                    
                    }
                    
                    double amt_old = price_old * quantity;
                    double amt = amt_old-disc_d;
                    //计算下原件
                    BigDecimal amt_b = new BigDecimal(amt);
                    try {
                        BigDecimal qty_b = new BigDecimal(quantity_str);
                        
                        price = amt_b.divide(qty_b,2,BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
                    catch (Exception e)
                    {
                    
                    }
                    
                    
                    
                    String plubarcode=job.get("goodsId").toString();

						/*
						goodsobj.put("item", job.get("serialNo").toString());
						goodsobj.put("pluNO", job.get("extGoodsId").toString());
						goodsobj.put("pluBarcode", job.get("goodsId").toString());

						goodsobj.put("pluName", job.get("mallGoodsName").toString());
						goodsobj.put("specName", job.get("spec").toString());
						goodsobj.put("attrName", "");
						goodsobj.put("unit", "");
						goodsobj.put("price", price_str);
						goodsobj.put("qty", quantity_str);
						goodsobj.put("goodsGroup", "");
						goodsobj.put("disc", disc_str );
						goodsobj.put("boxNum", "0");
						goodsobj.put("boxPrice", "0");
						goodsobj.put("amt", Double.toString(amt));*/
                    goodsItem.setItem(job.get("serialNo").toString());
                    goodsItem.setPluNo(job.optString("goodsId", ""));
                    goodsItem.setPluBarcode("");
                    goodsItem.setPluName(job.optString("mallGoodsName", ""));
                    goodsItem.setSpecName(job.optString("spec", ""));
                    goodsItem.setAttrName("");
                    goodsItem.setFeatureNo(" ");
                    if(!job.isNull("subGoodsId"))
                    {
                        goodsItem.setFeatureNo(job.optString("subGoodsId", " "));
                    }
                    goodsItem.setFeatureName("");
                    goodsItem.setsUnit(job.optString("unit", ""));
                    goodsItem.setPrice(price);
                    goodsItem.setOldPrice(price_old);
                    goodsItem.setQty(quantity);
                    goodsItem.setAmt(amt);
                    goodsItem.setOldAmt(amt_old);
                    goodsItem.setDisc(disc_d);
                    goodsItem.setBoxNum(0);
                    goodsItem.setBoxPrice(0);
                    goodsItem.setsUnitName(job.optString("unitName", ""));
                    goodsItem.setPackageType(job.optString("isPackageMaster", ""));
                    goodsItem.setPackageMitem(job.optString("pSerialNo", ""));

						/*String sourceCode="";//来源编号
						if(!job.isNull("sourceCode"))
						{
							sourceCode = job.get("sourceCode").toString();
						}
						goodsobj.put("sourceCode", sourceCode);

						String qrCodeValid="";//是否有效   0无效；1有效
						if(!job.isNull("qrCodeValid"))
						{
							qrCodeValid = job.get("qrCodeValid").toString();
						}
						goodsobj.put("qrCodeValid", qrCodeValid);*/
                    
                    String isMemo = "N";
                    JSONArray messagesarray = job.getJSONArray("messages");
                    
                    if (messagesarray != null && messagesarray.length() > 0) {
                        isMemo = "Y";
                        goodsobj.put("messages", messagesarray);
                        detail_message += "(";
                        for (int j = 0; j < messagesarray.length(); j++) {
                            try {
                                orderGoodsItemMessage goodsItemMessage = new orderGoodsItemMessage();
                                JSONObject message_obj = messagesarray.getJSONObject(j);
                                String msgName = message_obj.get("msgName").toString();
                                String messageInfo = message_obj.get("message").toString();
                                goodsItemMessage.setMsgType(message_obj.optString("msgType", ""));
                                goodsItemMessage.setMsgName(msgName);
                                goodsItemMessage.setMessage(messageInfo);
                                
                                goodsItem.getMessages().add(goodsItemMessage);
                                
                                detail_message += msgName + ":" + messageInfo;
                                
                            } catch (Exception e) {
                            
                            }
                            
                        }
                        detail_message += ")";
                        
                    }
                    //goodsobj.put("isMemo", isMemo);
                    goodsItem.setIsMemo(isMemo);
                    //新增折扣对接
                    goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                    JSONArray discListArray = job.getJSONArray("discList");
                    if (discListArray!=null&&discListArray.length()>0)
                    {
                        for (int j = 0; j < discListArray.length(); j++)
                        {
                            try
                            {
                                orderGoodsItemAgio agio = new orderGoodsItemAgio();
                                JSONObject disc_obj = discListArray.getJSONObject(j);
                                agio.setItem(disc_obj.optString("item"));
                                agio.setAmt(Double.parseDouble(disc_obj.optString("amt","0")));
                                agio.setQty(Double.parseDouble(disc_obj.optString("qty","0")));
                                agio.setDisc(Double.parseDouble(disc_obj.optString("disc","0")));
                                //agio.setRealDisc(Double.parseDouble(disc_obj.optString("realDisc","0")));
                                agio.setDcType(disc_obj.optString("dcType"));
                                agio.setDcTypeName(disc_obj.optString("dcTypeName"));
                                agio.setGiftCtf(disc_obj.optString("giftCtf"));
                                agio.setGiftCtfNo(disc_obj.optString("giftCtfNo"));
                                agio.setPmtNo(disc_obj.optString("pmtNo"));
                                
                                goodsItem.getAgioInfo().add(agio);
                                
                            }
                            catch (Exception e)
                            {
                                writelog_waimai("解析微商城discList节点失败：" + e.getMessage()+",单号orderNo="+orderInfo.getOrderNo());
                            }
                        }
                    }
                    
                    
                    //【ID1036116】【乐沙儿3.0】小程序扫码购需求-小程序服务  by jinzma 20230919
                    goodsItem.setSellerNo(job.optString("sellerNo", ""));
                    goodsItem.setSellerName(job.optString("sellerName", ""));
                    
                    orderInfo.getGoodsList().add(goodsItem);
                    
                    //array.put(goodsobj);
                    
                } catch (Exception e) {
                    writelog_waimaiException("解析微商城goods节点失败：" + e.getMessage());
                    continue;
                }
                
            }
            //message += detail_message;
            //jsonobjresponse.put("memo", message);// 单头备注
            //orderInfo.setMemo(message);// 单头备注
            
            //jsonobjresponse.put("goods", array);
            //jsonobjresponse.put("TOT_QTY", TOT_QTY+"");// 合计数量
            orderInfo.setTot_qty(TOT_QTY);// 合计数量
            orderInfo.setTotQty(orderInfo.getTot_qty());
            //货到付款的金额
            BigDecimal bdm_cashonAmt=new BigDecimal("0");
            
            // 解析payDetail
            orderInfo.setPay(new ArrayList<orderPay>());
            
            try
            {
                JSONArray payDetailArray = datasobj.getJSONArray("payDetail");
                JSONArray payarray = new JSONArray();
                int payItem = 0;
                for (int i = 0; i < payDetailArray.length(); i++)
                {
                    try
                    {
                        orderPay payModel = new orderPay();
                        payItem++;
                        JSONObject payobj = new JSONObject();
                        
                        JSONObject job = payDetailArray.getJSONObject(i);
                        String payCode = job.get("payChannel").toString();
                        String noCode ="";
                        
                        //有就取，没有就算了
                        String mobile =job.has("mobile")?job.get("mobile").toString():"";
                        
                        try
                        {
                            noCode = job.get("noCode").toString();
                            
                        } catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                        
                        String pay=job.get("amount").toString();
                        if (payCode.equals("codPay"))//货到付款
                        {
                            bdm_cashonAmt=bdm_cashonAmt.add(new BigDecimal(pay));
                        }
                        
                        String payName = job.optString("payChannelName");
                        if(payName==null||payName.trim().isEmpty()){
                            payName = GetMicroMarketPayName(payCode);
                        }
                        String purpose = job.get("purpose").toString();// 1=定金 2=尾款 3=退款
                        String isOrderpay = "N";
                        if (purpose != null && purpose.equals("1"))
                        {
                            isOrderpay = "Y";
                        }
                        String orderpay = payCode;
                        String payCodeerp = payCode;//默认
                        String paytemp = "";

                        /*payobj.put("item", String.valueOf(payItem));
                        payobj.put("payCode", payCode);
                        payobj.put("payCodeerp", payCodeerp);
                        payobj.put("payName", payName);
                        payobj.put("mobile", mobile);
                        payobj.put("cardNO", noCode);
                        payobj.put("ctType", "");
                        payobj.put("paySernum", job.get("payTradeNo").toString());// 支付后的支付品台交易号
                        payobj.put("serialNO", job.get("payOrderNo").toString());// 发起支付平台的商户唯一订单号
                        payobj.put("refNO", "");
                        payobj.put("teriminalNO", "");
                        payobj.put("descore", "0");
                        payobj.put("pay", pay);
                        payobj.put("extra", "0");
                        payobj.put("changed", "0");
                        payobj.put("bdate", orderDate);
                        payobj.put("isOrderpay", isOrderpay);
                        payobj.put("isOnlinePay", "Y");
                        payobj.put("order_PayCode", orderpay);*/
                        
                        payModel.setItem(payItem+"");
                        payModel.setPay(pay);
                        payModel.setPayCode(payCode);
                        payModel.setPayCodeErp(payCodeerp);
                        payModel.setPayName(payName);
                        payModel.setMobile(mobile);
                        payModel.setCardNo(noCode);
                        payModel.setCtType(job.optString("ctType"));//卡券类型
                        payModel.setPaySerNum(job.optString("payTradeNo"));// 支付后的支付品台交易号
                        payModel.setSerialNo(job.optString("payOrderNo"));// 发起支付平台的商户唯一订单号
                        payModel.setRefNo("");
                        payModel.setDescore("0");
                        if ("#05".equals(orderpay))
                        {
                            //积分抵现。时给下抵扣的积分，积分字段在单头
                            String usePoint = "0";
                            if (!datasobj.isNull("usePoint"))
                            {
                                usePoint = datasobj.get("usePoint").toString();// 使用积分（积分抵现）
                            }
                            try
                            {
                                Double.parseDouble(usePoint);
                            }
                            catch (Exception e)
                            {
                                usePoint = "0";
                            }
                            payModel.setDescore(usePoint);
                            
                        }
                        payModel.setChanged("0");
                        payModel.setExtra("0");
                        payModel.setIsOrderPay(isOrderpay);
                        payModel.setIsOnlinePay("Y");
                        payModel.setOrder_payCode(orderpay);
                        payModel.setbDate(orderDate);
                        String cardSendPay = job.optString("cardSendPay","0");
                        try
                        {
                            payModel.setCardSendPay(Double.parseDouble(cardSendPay)+"");
                        }
                        catch (Exception e)
                        {
                            payModel.setCardSendPay("0");
                        }

							/*String canInvoice = "";
							if (!job.isNull("canInvoice"))
							{
								canInvoice = job.get("canInvoice").toString();
							}
							payobj.put("invoicetype", canInvoice);//

							if(datainvno!=null&&datainvno.length()>0)
							{
								try {
									payobj.put("invoiceno", datainvno.getJSONObject(0).optString("invoiceNo"));// 支付后的支付品台交易号
								} catch (Exception e) {
								}
								payobj.put("isinvoice", "Y");// 支付后的支付品台交易号
							}*/
                        
                        orderInfo.getPay().add(payModel);
                        
                        payarray.put(payobj);
                    } catch (Exception e) {
                        writelog_waimaiException("解析微商城payDetail节点失败：" + e.getMessage());
                        continue;
                    }
                    
                }
                jsonobjresponse.put("pay", payarray);
                
            }
            catch (Exception e)
            {
            
            }
            
            //jsonobjresponse.put("cashOnAMT", bdm_cashonAmt);//货到付款的金额	 3.0未对接
            
            String orderShopNO = jsonobjresponse.optString("shopNO");
            //根据转换后的下单门店 ，取对应的
            
            ParseJson pj = new ParseJson();
            String Response_json =  pj.beanToJson(orderInfo);//jsonobjresponse.toString();
            
            return orderInfo;
        } catch (Exception e) {
            writelog_waimaiException("微商城发送的请求格式有误！" + e.getMessage());
            return null;
        }
    }
    
    public static String GetWuXiangResponse(String responseStr) throws Exception {
        
        if (responseStr == null || responseStr.length() == 0) {
            writelog_waimaiException("舞像发送的请求为空！");
            return null;
        }
        writelog_waimai("【舞像请求内容】" + responseStr);
        
        ParseJson pj = new ParseJson();
        WuXiangOder curreginfo = pj.jsonToBean(responseStr, new TypeToken<WuXiangOder>() {
        });
        
        // 解析收到的舞像请求
        try
        {
            JSONObject jsonobjresponse = new JSONObject();
            
            // JSONObject jsonobj = new JSONObject(responseStr);
            
            String companyno = "99";
            String shopno = " ";// 主键不能为空，所以默认空格
            String shopname = "";
            
            companyno = "99";// 企业编号
            jsonobjresponse.put("companyNO", companyno);
            jsonobjresponse.put("customerNO", curreginfo.getCard_no());
            
            jsonobjresponse.put("loadDocType", "7");// 1.饿了么 2.美团外卖 3.微商城
            jsonobjresponse.put("isShipcompany", "N");// 总部配送 Y/N
            jsonobjresponse.put("sn", "0");
            jsonobjresponse.put("isInvoice", "N");// 是否开发票
            jsonobjresponse.put("invoiceType", "");
            jsonobjresponse.put("invoiceTitle", "");
            jsonobjresponse.put("taxRegnumber", "");
            jsonobjresponse.put("packageFee", "0");// 餐盒费
            jsonobjresponse.put("serviceCharge", "0");// 服务费
            
            jsonobjresponse.put("totDisc", "0");// 订单优惠总额
            jsonobjresponse.put("sellerDisc", "0");// 商户优惠总额
            jsonobjresponse.put("platformDisc", "0");// 平台优惠总额
            jsonobjresponse.put("isBook", "Y");// 外卖预定单
            
            // JSONObject datasobj = jsonobj.getJSONObject("datas");
            String orderNo = curreginfo.getOrderNo();
            jsonobjresponse.put("orderNO", curreginfo.getOrderNo());
            // String address = datasobj.get("address").toString();//
            jsonobjresponse.put("address", curreginfo.getReceiverAddress());
            // String contactName = datasobj.get("contactName").toString();//联系人
            jsonobjresponse.put("contMan", curreginfo.getReceiverName());
            // String contactTelephone =
            // datasobj.get("contactTelephone").toString();//
            jsonobjresponse.put("contTel", curreginfo.getReceiverMobile());
            String deliverType = curreginfo.getPostType() + "";// 微商城取货方式 1=自提
            // 2=配送
            if (deliverType != null && deliverType.equals("1")) {
                deliverType = "2";
            } else {
                deliverType = "3";
            }
            jsonobjresponse.put("shipType", deliverType);// 配送方式 1.外卖平台配送 2.配送
            // 3.顾客自提
            Date orderDatetemp = curreginfo.getCreateTime();// 下单时间 2018-08-29
            // 16:57:13
            String orderDateTime = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = orderDatetemp;
                orderDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
            } catch (Exception e) {
            
            }
            jsonobjresponse.put("createDatetime", orderDateTime);// 下单时间，格式yyyyMMddHHmmssSSS
            
            Calendar cal = Calendar.getInstance();// 获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String createDate = df.format(cal.getTime());
            
            df = new SimpleDateFormat("HHmmss");
            String createTime = df.format(cal.getTime());
            jsonobjresponse.put("sDate", createDate);
            jsonobjresponse.put("sTime", createTime);
            
            try {
                shopno = curreginfo.getSub_store_code();// 这个节点可能不返回
            } catch (Exception e) {
                shopno = " ";
            }
            try {
                shopname = "";// 这个节点可能不返回
            } catch (Exception e) {
                shopname = "";
            }
            // 主键不能为空
            if (shopno == null || shopno.isEmpty() || shopno.length() == 0) {
                shopno = " ";
            }
            
            jsonobjresponse.put("shopNO", shopno);// 下单门店
            jsonobjresponse.put("organizationNO", shopno);
            jsonobjresponse.put("shopName", shopname);
            jsonobjresponse.put("shippingShopNO", shopno);// 配送门店=下单门店
            jsonobjresponse.put("shippingShopName", shopname);
            jsonobjresponse.put("machShopNO", "");// 生产门店，先默认 后面去指定
            jsonobjresponse.put("machShopName", "");
            
            String needDate = curreginfo.getSelfDelivertTime();// 微商城格式
            // 2018-08-08
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(needDate);
                needDate = new SimpleDateFormat("yyyyMMdd").format(date);
            } catch (Exception e) {
            
            }
            jsonobjresponse.put("shipDate", needDate);// 配送日期格式yyyyMMdd 20180808
            
            // 时间不知道怎么处理
            String needTime = curreginfo.getSelfDelivertTime();// 微商城格式 17:00
            try {
                if (needTime.length() > 18) {
                    String[] needTimetemp = needTime.split(" ");
                    needTime = needTimetemp[1];
                }
            } catch (Exception e) {
            
            }
            jsonobjresponse.put("shipTime", needTime);// 配送时间 HHmmss 170000
            String message = "";// 买家留言
            try {
                message = "";// 可能没这个节点
            } catch (Exception e) {
                message = "";
            }
            jsonobjresponse.put("memo", message);// 单头备注
            // String totalAmount = datasobj.get("totalAmount").toString();//微商城
            // 订单总应付金额
            
            jsonobjresponse.put("tot_oldAmt", curreginfo.getTotamtString());// 订单原价
            jsonobjresponse.put("tot_Amt", curreginfo.getTotamtString());// 订单金额
            jsonobjresponse.put("incomeAmt", curreginfo.getPayamtString());// 商家实收金额
            
            // String payedAmount = datasobj.get("payedAmount").toString();//微商城
            // 已付金额
            jsonobjresponse.put("payAmt", curreginfo.getPayamtString());// 用户已支付金额
            
            // String deliverAmount =
            // datasobj.get("deliverAmount").toString();//微商城 运费
            jsonobjresponse.put("shipFee", curreginfo.getFreightPaid() / 100);// 配送费
            // String goodsAmount = datasobj.get("goodsAmount").toString();//微商城
            // 商品金额
            
            jsonobjresponse.put("status", "0");// 订单状态 默认成已接单
            String payStatus = "2";// 微商城 0=未支付;1=订金已付; 2=已付清
            if (payStatus.equals("1")) {
                payStatus = "2";
            } else if (payStatus.equals("2")) {
                payStatus = "3";
            } else {
                payStatus = "1";
            }
            jsonobjresponse.put("payStatus", payStatus);// 支付状态 1.未支付 2.部分支付
            // 3.付清
            jsonobjresponse.put("refundStatus", "1");//// 退单状态 1.未申请 2.用户申请退单
            //// 3.拒绝退单 4.客服仲裁中
            //// 5.退单失败 6.退单成功
            jsonobjresponse.put("shopShareDeliveryFee", "0");// 商家替用户承担的配送费
            jsonobjresponse.put("partRefundAmt", "0");// 部分退单 的退款金额
            // 解析goods
            // JSONArray goodsarray = datasobj.getJSONArray("goods");
            
            JSONArray array = new JSONArray();
            
            for (int i = 0; i < curreginfo.getOrderSub().size(); i++) {
                try {
                    JSONObject goodsobj = new JSONObject();
                    
                    // JSONObject job = goodsarray.getJSONObject(i);
                    
                    double price = 0;
                    double quantity = 0;
                    String price_str = "0";
                    String quantity_str = "0";
                    try {
                        price_str = curreginfo.getOrderSub().get(i).getSalesPriceString() + "";
                        price = Double.parseDouble(price_str);
                        
                    } catch (Exception e) {
                    
                    }
                    try {
                        quantity_str = curreginfo.getOrderSub().get(i).getQuantity() + "";
                        quantity = Double.parseDouble(quantity_str);
                        
                    } catch (Exception e) {
                    
                    }
                    double amt = price * quantity;
                    
                    goodsobj.put("item", i + 1);
                    goodsobj.put("pluNO", curreginfo.getOrderSub().get(i).getGoodsCode());
                    goodsobj.put("pluBarcode", curreginfo.getOrderSub().get(i).getSku_code());
                    goodsobj.put("pluName", curreginfo.getOrderSub().get(i).getGoods_name());
                    goodsobj.put("specName", "");
                    goodsobj.put("attrName", "");
                    goodsobj.put("unit", "");
                    goodsobj.put("price", price_str);
                    goodsobj.put("qty", quantity_str);
                    goodsobj.put("goodsGroup", "");
                    goodsobj.put("disc", "0");
                    goodsobj.put("boxNum", "0");
                    goodsobj.put("boxPrice", "0");
                    goodsobj.put("amt", Double.toString(amt));
                    
                    String isMemo = "N";
                    // JSONArray messagesarray = job.getJSONArray("messages");
                    // if(messagesarray!=null &&messagesarray.length()>0)
                    // {
                    // isMemo = "Y";
                    // goodsobj.put("messages", messagesarray);
                    // }
                    // goodsobj.put("isMemo", isMemo);
                    
                    array.put(goodsobj);
                    
                } catch (Exception e) {
                    writelog_waimaiException("解析舞像goods节点失败：" + e.getMessage());
                    continue;
                }
                
            }
            
            jsonobjresponse.put("goods", array);
            
            String Response_json = jsonobjresponse.toString();
            
            
            String redis_key = "WMORDER" + "_" + companyno + "_" + shopno;
            // String hash_key = orderid + "&" + orderStatus;
            String hash_key = orderNo;
            writelog_waimai(
                    "【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
            try {
                
                RedisPosPub redis = new RedisPosPub();
                boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                if (isexistHashkey) {
                    
                    redis.DeleteHkey(redis_key, hash_key);//
                    writelog_waimai("【删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                }
                boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                if (nret) {
                    writelog_waimai("【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                } else {
                    writelog_waimai("【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                }
                //redis.Close();
                
            } catch (Exception e) {
                writelog_waimai("【写缓存】Exception:" + e.getMessage());
            }
            
            pj=null;
            return Response_json;
        } catch (Exception e)
        {
            pj=null;
            writelog_waimaiException("舞像发送的请求格式有误！" + e.getMessage());
            return null;
        }
        
    }
    
    
    public static String GetMTResponse(String responseStr) throws Exception {
        
        if (responseStr == null || responseStr.length() == 0) {
            //writelog_waimaiException("美团外卖发送的请求为空！");
            return null;
        }
        // writelog_waimai("【美团URL转码前】"+responseStr);
        writelog_fileName("【美团URL转码前】"+responseStr,"MTRequsetLog");
        //String responseStr_1 = getURLDecoderString(responseStr);// 一次转码
        //String responseStr_2 = getURLDecoderString(responseStr_1);// 二次转码（获取为 中文）
        //writelog_waimai("【美团URL转码后2】" + responseStr_2);
        /*
         * total=10&delivery_time=201808111508&wm_poi_name=商家名称&utime=
         * 201808111508&detail={"A":"商品A"}&caution=辣&original_price=10&order_id=
         * 1234567890&recipient_name=陶日平&timestamp=1533885008&city_id=123&
         * wm_poi_phone=18888888888&pay_type=1&longitude=24&status=0&
         * invoice_title=发票抬头&app_poi_code=1001&shipper_phone=18777777777&
         * is_third_shipping=1&ctime=201808111508&shipping_fee=1&has_invoiced=1&
         * extras={"spec":"大份"}&wm_poi_address=商家地址&recipient_phone=18717912278&
         * wm_order_id_view=1234567890&app_id=2605&latitude=134&
         * recipient_address=鼎捷软件&sig=db7d636bd6136467bb045474edda4c8b
         */
        // 解析收到的美团外卖请求
        
        String[] MTResquest = responseStr.split("&");
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_waimaiException("解析美团外卖发送的请求格式有误！");
            return null;
        }
        
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        
        String urlDecodeString ="";
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());//这里收到的是URL数据，需要解码
                //美团需要URL解码2次
                s2 = getURLDecoderString(s2);// 美团需要转码2次 转码
                s2 = getURLDecoderString(s2);// 二次转码（获取为 中文）
                map_MTResquest.put(s1, s2);
                
                urlDecodeString +=s1+"="+s2+"&";//记日志
                
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        writelog_waimai("【美团URL转码后2】"+urlDecodeString);
        
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate = df.format(cal.getTime());
        
        df = new SimpleDateFormat("HHmmss");
        String sTime = df.format(cal.getTime());
        df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String sDateTime = df.format(cal.getTime());
        
        //Map<String, String> map = new HashMap<String, String>();
        JSONObject jsonobjresponse = new JSONObject();
        order dcpOrder = new order();
        dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
        dcpOrder.setPay(new ArrayList<orderPay>());
        
        String loadDocType = orderLoadDocType.MEITUAN;// 渠道类型
        String mtOrderStatus = "";//MT订单状态
        
        try {
            String orderid = map_MTResquest.get("order_id").toString();// 订单ID
            // 美团（1：用户已提交订单；2：可推送到APP方平台也可推送到商家；3：商家已收到；4：商家已确认；8：已完成；9：已取消
            String orderStatus = map_MTResquest.get("status").toString();// 订单状态
            mtOrderStatus = orderStatus;
            String app_poi_code = map_MTResquest.get("app_poi_code").toString();// APP方商家ID (企业编号_门店编号 99_10001)
            String shopname = map_MTResquest.get("wm_poi_name").toString();// 美团商家名称
            long dt1 = System.currentTimeMillis();
            Map<String, String>	mappingShopMap = GetMTMappingShop(app_poi_code);//查询下门店对应缓存MT_MappingShop
            String eId = mappingShopMap.get("eId");
            String erpshopNo = mappingShopMap.get("erpShopNo");
            String channelId = mappingShopMap.get("channelId");
            String erpShopName = mappingShopMap.getOrDefault("erpShopName", "");
            if (erpShopName==null||erpShopName.isEmpty())
            {
                erpShopName = shopname;
            }
            long dt2 = System.currentTimeMillis();
            long dt_spwn = dt2-dt1;
            if (dt_spwn>=100)
            {
                HelpTools.writelog_waimai("【查询映射门店】耗时:[" + dt_spwn+"]MS，订单orderNo="+orderid);
            }
            writelog_waimai("【MT获取对应ERP门店】app_poi_code="+app_poi_code+"-->对应ERP的企业ID="+eId+",门店="+erpshopNo+",门店名称="+erpShopName+",订单号="+orderid);
            
            dcpOrder.seteId(eId);
            dcpOrder.setLoadDocType(loadDocType);
            dcpOrder.setChannelId(channelId);
            dcpOrder.setOrderNo(orderid);//dcp单号=来源单号
            dcpOrder.setLoadDocOrderNo(orderid);//来源单号
            dcpOrder.setLoadDocBillType("");//来源单据类型
            dcpOrder.setOrderShop(app_poi_code);//第三方门店ID
            dcpOrder.setOrderShopName(shopname);;//第三方门店名称
            dcpOrder.setShopNo(erpshopNo);
            dcpOrder.setShopName(erpShopName);
            dcpOrder.setShippingShopNo(erpshopNo);
            dcpOrder.setShippingShopName(erpShopName);
            dcpOrder.setMachShopNo(erpshopNo);
            dcpOrder.setMachShopName(erpShopName);
            
            // 订单中心status
            /*
             * 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货
             * 11.已完成 12.已退单
             */
            
            dcpOrder.setStatus("");
            dcpOrder.setRefundStatus("1");
            if (orderStatus.equals("2") || orderStatus.equals("1"))// 推送已经支付的订单
            {
                dcpOrder.setStatus("1");
            }
            else if (orderStatus.equals("4"))// 门店已接单
            {
                dcpOrder.setStatus("2");
            }
            else if (orderStatus.equals("8"))// 订单已完成
            {
                dcpOrder.setStatus("11");
            }
            else if (orderStatus.equals("9"))// 已取消
            {
                dcpOrder.setStatus("3");
            }
            
            
            dcpOrder.setMemo(map_MTResquest.get("caution"));// string 忌口或备注
            String day_seq = "0";
            if(map_MTResquest.containsKey("day_seq"))
            {
                day_seq = map_MTResquest.get("day_seq").toString();//门店当天的推单流水号，该信息默认不推送，如有需求可在开发者中心订阅
            }
            dcpOrder.setSn(day_seq);// 门店当天的推单流水号
            
            /***********************发票相关处理*******************************/
            orderInvoice dcpOrderInvoiceDetail = new orderInvoice();
            String has_invoiced = map_MTResquest.get("has_invoiced").toString();
            String isInvoice = "N";// 是否开发票
            if (has_invoiced != null && has_invoiced.equals("1")) {
                isInvoice = "Y";
            }
            dcpOrderInvoiceDetail.setIsInvoice(isInvoice);;// 是否开发票
            dcpOrderInvoiceDetail.setInvoiceTitle(map_MTResquest.get("invoice_title"));// 发票抬头
            
            String taxpayer_id = "";
            if(map_MTResquest.containsKey("taxpayer_id"))
            {
                taxpayer_id = map_MTResquest.get("taxpayer_id").toString();//，该信息默认不推送，如有需求可在开发者中心订阅
            }
            dcpOrderInvoiceDetail.setTaxRegNumber(taxpayer_id);// 纳税人识别号
            String peopleType ="2";//发票类型 1.公司 2.个人
            if(taxpayer_id!=null&&taxpayer_id.trim().isEmpty()==false)
            {
                peopleType = "1";
            }
            dcpOrderInvoiceDetail.setPeopleType(peopleType);// 1.公司 2.个人
            dcpOrderInvoiceDetail.setInvoiceType("");// 台湾 二联  三联
            
            dcpOrder.setInvoiceDetail(dcpOrderInvoiceDetail);
            
            jsonobjresponse.put("orderIdView", map_MTResquest.get("wm_order_id_view"));// 订单展示ID
            String orderCodeView = map_MTResquest.get("wm_order_id_view");
            orderCodeView = getMTOrderIdView(orderCodeView);
            //jsonobjresponse.put("orderCodeView", orderCodeView);//订单展示ID（美团外卖需要按规则生成一维码字符串）
            dcpOrder.setOrderCodeView(orderCodeView);
            String ctime = map_MTResquest.get("ctime");// 时间戳秒
            String createDatetime = sDateTime;
            String createDate_order = sDate;
            String createTime_order = sTime;
            try {
                long lt = new Long(ctime);
                Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                createDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
                createDate_order = new SimpleDateFormat("yyyyMMdd").format(date);
                createTime_order = new SimpleDateFormat("HHmmss").format(date);
            } catch (Exception e) {
            
            }
            dcpOrder.setCreateDatetime(createDatetime);// String 创建时间
            //jsonobjresponse.put("orderStatus", orderStatus);// int 第三方订单状态
            
            String longitude ="0";
            String latitude ="0";
            if (map_MTResquest.containsKey("longitude")) {
                longitude = map_MTResquest.get("longitude").toString();// 经度
                
            }
            if (map_MTResquest.containsKey("latitude")) {
                latitude = map_MTResquest.get("latitude").toString();// 纬度
            }
            
            dcpOrder.setLongitude(longitude);
            dcpOrder.setLatitude(latitude);
            
            String deliveryTime = map_MTResquest.get("delivery_time").toString();// 用户预计送达时间，“立即送达”时为0
            String shipDate = createDate_order;//配送日期默认下单日期
            String shipTime = createTime_order;//配送时间默认下单时间
            String isBook = "N";
            if (deliveryTime != null && deliveryTime.equals("0") == false) {
                isBook = "Y";
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                    long lt = new Long(deliveryTime);
                    Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                    shipDate = dateFormat.format(date);
                    shipTime = timeFormat.format(date);
                    
                } catch (Exception e) {
                
                }
                
            }
            
            dcpOrder.setShipDate(shipDate);
            dcpOrder.setShipStartTime(shipTime);//配送开始时间
            dcpOrder.setShipEndTime(shipTime);//配送结束时间
            dcpOrder.setIsBook(isBook);// 是否预订单
            
            String pickType = "";
            try {
                pickType = map_MTResquest.get("pick_type").toString();// 0：普通取餐；1：到店取餐
                // 取餐类型（0：普通取餐；1：到店取餐），该信息默认不推送，如有需求可在开发者中心订阅
                
            } catch (Exception e) {
            
            }
            String shipType = "1"; // 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
            String isMerPay = "N";//配送费是否商家结算
            
            String logistics_code = "";//配送方式  0000	商家自配送
            try
            {
                logistics_code = map_MTResquest.get("logistics_code").toString();
            } catch (Exception e) {
                // TODO: handle exception
                
            }
            if(logistics_code!= null&&logistics_code.equals("0000"))
            {
                shipType = "6";//商家自配送
                isMerPay = "Y";
            }
            
            // 因为pickType节点需要联系美团人员，比较麻烦，所以用送餐地址去判断下，到店自取的送餐地址=
            String recipientAddress = map_MTResquest.get("recipient_address").toString();
            if (recipientAddress != null && recipientAddress.startsWith("到店自取")) {
                shipType = "3";
            }
            
            if (pickType != null && pickType.equals("1")) {
                shipType = "3";
            }
            //新增隐私地址的字段：recipientAddressDesensitization。此字段不会包含recipientAddress字段中@#后面的值
            //logistics_code推送非 0000、5001、00009003，必须使用隐私地址。
            String recipient_address_desensitization = map_MTResquest.getOrDefault("recipient_address_desensitization", "").toString();
            if(!recipient_address_desensitization.isEmpty())
            {
                if(logistics_code.equals("0000")||logistics_code.equals("5001")||logistics_code.equals("00009003"))
                {
                
                }
                else
                {
                    recipientAddress = recipient_address_desensitization;
                }
            }
            dcpOrder.setShipType(shipType);// 配送类型1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
            dcpOrder.setIsMerPay(isMerPay);
            dcpOrder.setAddress(recipientAddress);// 收件人地址
            dcpOrder.setContMan(map_MTResquest.get("recipient_name"));// 收件人姓名
            dcpOrder.setGetMan(map_MTResquest.get("recipient_name"));// 收件人姓名
            dcpOrder.setContTel(map_MTResquest.get("recipient_phone"));// String
            dcpOrder.setGetManTel(map_MTResquest.get("recipient_phone"));// String
            dcpOrder.setPayStatus("3");// 1.未支付 2.部分支付 3.付清
            dcpOrder.setsTime(sDateTime);//系统时间 yyyyMMddhhmmssSSS
            
            dcpOrder.setShopShareShipfee(0);// 商家替用户承担的配送费
            dcpOrder.setRefundAmt(0);// 部分退单 的退款金额
            
            
            double tot_oldAmt = 0;
            try
            {
                tot_oldAmt = Double.parseDouble(map_MTResquest.get("original_price").toString());
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            dcpOrder.setTot_oldAmt(tot_oldAmt);
            double tot_Amt = 0;
            try
            {
                tot_Amt = Double.parseDouble(map_MTResquest.get("total").toString());
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            dcpOrder.setTot_Amt(tot_Amt);
            double shipFee = 0;
            try
            {
                shipFee = Double.parseDouble(map_MTResquest.get("shipping_fee").toString());
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            dcpOrder.setShipFee(shipFee);
            
            JSONObject detailObj =  new JSONObject("{\"detail\":" + map_MTResquest.get("detail") + "}");
            
            // 解析goods
            
            JSONArray goodsarray = detailObj.getJSONArray("detail");
            JSONArray array = new JSONArray();
            int item = 0;// 项次
            double packageFee = 0;// 包装费
            double tot_qty = 0;
            for (int i = 0; i < goodsarray.length(); i++) {
                try {
                    item++;
                    orderGoodsItem goodsItem = new orderGoodsItem();
                    goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                    goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                    
                    JSONObject job = goodsarray.getJSONObject(i);
                    String app_food_code = job.get("app_food_code").toString();// APP方菜品id
                    String food_name = job.get("food_name").toString();// 菜品名称
                    String sku_id = job.get("sku_id").toString();// sku编码
                    String quantity_str = job.get("quantity").toString();// 商品数量
                    String price_str = job.get("price").toString();// 商品单价，此字段默认为活动折扣后价格，可在开发者中心订阅是否替换为原价
                    String unit = job.get("unit").toString();// 单位
                    String attr = "";// 菜品属性
                    try
                    {
                        attr = job.get("food_property").toString();//菜品属性，多个属性用半角逗号隔开，该信息默认不推送，如有需求可在开发者中心订阅
                    }
                    catch (Exception e)
                    {
                        attr = "";
                    }
                    
                    String spec = "";// 菜品规格名称，
                    try {
                        spec = job.get("spec").toString();//菜品规格名称，该信息默认不推送，如有需求可在开发者中心订阅
                    } catch (Exception e) {
                        spec = "";
                    }
                    String cart_id = "";
                    int cart_no = 0;
                    try {
                        cart_id = job.get("cart_id").toString();// 商品所在的口袋，0为1号口袋，1为2号口袋
                        cart_no = Integer.parseInt(cart_id) + 1;
                        cart_id = cart_no + "号口袋";
                    } catch (Exception e) {
                        cart_id = "";
                    }
                    
                    double price = 0;
                    double quantity = 0;
                    try {
                        price = Double.parseDouble(price_str);
                    } catch (Exception e) {
                        price = 0;
                    }
                    try {
                        quantity = Double.parseDouble(quantity_str);
                    } catch (Exception e) {
                        quantity = 0;
                    }
                    double amt = price*quantity;
                    tot_qty +=quantity;
                    // 计算餐盒 包装费
                    String box_price_str = job.get("box_price").toString();// 餐盒价格
                    String box_num_str = job.get("box_num").toString();// 餐盒数量
                    double box_price = 0;
                    double box_num = 0;
                    //餐盒数量,在计算餐盒数量和餐盒费用时，请先按照商品规格维度将餐盒数量向上取整后，再乘以相应的餐盒费单价，计算得出餐盒费用。
                    try {
                        box_price = Double.parseDouble(box_price_str);
                    } catch (Exception e) {
                        box_price = 0;
                    }
                    try {
                        box_num = Math.ceil(Double.parseDouble(box_num_str)) ;
                    } catch (Exception e) {
                        box_num = 0;
                    }
                    
                    packageFee += box_price * box_num;
                    
                    goodsItem.setItem(item+"");
                    goodsItem.setPluNo(sku_id);
                    goodsItem.setPluBarcode(sku_id);
                    goodsItem.setSkuId(sku_id);
                    goodsItem.setPluName(food_name);
                    goodsItem.setSpecName(spec);
                    goodsItem.setAttrName(attr);
                    goodsItem.setFeatureNo("");
                    goodsItem.setFeatureName("");
                    goodsItem.setsUnit(unit);
                    goodsItem.setPrice(price);
                    goodsItem.setOldPrice(price);
                    goodsItem.setQty(quantity);
                    goodsItem.setAmt(amt);
                    goodsItem.setOldAmt(amt);
                    goodsItem.setDisc(0);
                    goodsItem.setBoxNum(box_num);
                    goodsItem.setBoxPrice(box_price);
                    goodsItem.setsUnitName(unit);
                    goodsItem.setGoodsGroup(cart_id);
                    goodsItem.setIsMemo("N");
                    
                    dcpOrder.getGoodsList().add(goodsItem);
                    
                }
                catch (Exception e)
                {
                    writelog_waimai("解析MT的detail节点失败：" + e.getMessage());
                    continue;
                }
                
            }
            
            dcpOrder.setPackageFee(packageFee);// 包装费（MT没有直接返回，需要计算）
            dcpOrder.setTot_qty(tot_qty);
            dcpOrder.setTotQty(dcpOrder.getTot_qty());
            dcpOrder.setLoadDocTypeName("美团");
            dcpOrder.setChannelIdName("美团");
            // 解析extras
            double totDisc = 0;
            double platformDisc = 0;
            double sellerDisc = 0;
            String memo_zengsong= "【买赠】";//赠送得商品，在折扣类型里面，只要一个说明
            boolean isExistZengsong = false;//是否存在买赠
            try {
                JSONObject extrasObj = new JSONObject("{\"extras\":" + map_MTResquest.get("extras") + "}");
                JSONArray extrasarray = extrasObj.getJSONArray("extras");
                jsonobjresponse.put("extras", extrasarray);
                for (int i = 0; i < extrasarray.length(); i++) {
                    try {
                        JSONObject job = extrasarray.getJSONObject(i);
                        String reduce_fee_str = job.get("reduce_fee").toString();// 活动优惠金额，也即美团承担活动费用和商户承担活动费用的总和
                        String remark = "";
                        String type_mt = "";
                        BigDecimal reduce_fee_B = new BigDecimal("0");
                        try
                        {
                            reduce_fee_B = new BigDecimal(reduce_fee_str);
                        } catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                        
                        //美团瞎搞，目前发现 买赠类型折扣，只写商家承担折扣，也不写总得折扣，而且也没有商品明细，只是加了一个折扣类型type=23
                        if(reduce_fee_B.compareTo(BigDecimal.ZERO)==0)
                        {
                            
                            try
                            {
                                remark = job.get("remark").toString();
                                if (remark!=null&&remark.isEmpty()==false)
                                {
                                    memo_zengsong +=remark+",";
                                    isExistZengsong = true;
                                }
                                
                                type_mt = job.get("type").toString();
                            }
                            catch (Exception e)
                            {
                                // TODO: handle exception
                            }
                            
                            writelog_waimai("存在【买赠】折扣，美团折扣类型type="+type_mt+",折扣描述remark="+remark+",单号orderNo="+orderid);
                            
                            continue;
                        }
                        
                        
                        
                        try {
                            totDisc += Double.parseDouble(reduce_fee_str);
                        } catch (Exception e) {
                            totDisc += 0;
                        }
                        
                        String mt_charge_str = job.get("mt_charge").toString();// 优惠金额中美团承担的部分，该信息默认不推送，如有需求可在开发者中心订阅
                        try {
                            platformDisc += Double.parseDouble(mt_charge_str);
                        } catch (Exception e) {
                            platformDisc += 0;
                        }
                        
                        String poi_charge_str = job.get("poi_charge").toString();// 优惠金额中商家承担的部分，该信息默认不推送，如有需求可在开发者中心订阅
                        try {
                            sellerDisc += Double.parseDouble(poi_charge_str);
                        } catch (Exception e) {
                            sellerDisc += 0;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        writelog_waimai("解析聚宝盆extras异常：" + e.getMessage());
                        continue;
                    }
                }
                
            } catch (Exception e) {
                writelog_waimai("获取聚宝盆extras节点失败：" + e.getMessage());
            }
            
            if(isExistZengsong)
            {
                dcpOrder.setMemo(map_MTResquest.get("caution")+memo_zengsong);
            }
            
            
            dcpOrder.setTotDisc(totDisc);// 优惠总金额
            dcpOrder.setPlatformDisc(platformDisc);// 平台优惠总金额
            //dcpOrder.setSellerDisc(sellerDisc);// 商户优惠总金额
            dcpOrder.setSellerDisc(totDisc-platformDisc);// 商户优惠总金额
            // 解析poiReceiveDetail
            double incomeAmt = 0;
            double serviceCharge = 0;
            double shopIncome_onlinePayment = 0;
            try {
                JSONObject poiReceiveDetail_Res = new JSONObject("{\"poi_receive_detail\":" + map_MTResquest.get("poi_receive_detail") + "}");
                jsonobjresponse.put("poiReceiveDetail", poiReceiveDetail_Res.get("poi_receive_detail"));
                
                JSONObject poiReceiveDetail = new  JSONObject(map_MTResquest.get("poi_receive_detail").toString());
                String wmPoiReceiveCent_str = poiReceiveDetail.get("wmPoiReceiveCent").toString();// 商家应收款，单位为分
                String logisticsFee_str = poiReceiveDetail.get("logisticsFee").toString();// 用户实际支付配送费  (分)
                try {
                    incomeAmt = Double.parseDouble(wmPoiReceiveCent_str) / 100;
                } catch (Exception e) {
                    incomeAmt = 0;
                }
                
                String foodShareFeeChargeByPoi_str = poiReceiveDetail.get("foodShareFeeChargeByPoi").toString();// 商品分成，即平台服务费，单位为分
                try {
                    serviceCharge = Double.parseDouble(foodShareFeeChargeByPoi_str) / 100;
                } catch (Exception e) {
                    serviceCharge = 0;
                }
                
                String onlinePayment_str = poiReceiveDetail.get("onlinePayment").toString();// 在线支付款，单位为分
                try {
                    shopIncome_onlinePayment = Double.parseDouble(onlinePayment_str) / 100;
                } catch (Exception e) {
                    shopIncome_onlinePayment = 0;
                }
                
            } catch (Exception e) {
                writelog_waimai("获取MT的poiReceiveDetail节点失败：" + e.getMessage());
            }
            
            dcpOrder.setIncomeAmt(incomeAmt);// 店铺实际收入
            dcpOrder.setServiceCharge(serviceCharge);// 平台服务费
            dcpOrder.setPayAmt(shopIncome_onlinePayment);// 在线支付款
            
            
            //调用支付方式
            StringBuffer errorPayMessage = new StringBuffer();
            HelpTools.updateOrderPayByMapping(dcpOrder, errorPayMessage);
            
            errorPayMessage = new StringBuffer();
            HelpTools.updateOrderDetailInfo(dcpOrder, errorPayMessage);
            
            HelpTools.updateOrderWithPackage(dcpOrder, "", errorPayMessage);
            
            String status_json = dcpOrder.getStatus();//获取下订单状态
            
            ParseJson pj = new ParseJson();
            String Response_json = pj.beanToJson(dcpOrder);
            
            
            String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + erpshopNo;
            // String hash_key = orderid + "&" + orderStatus;
            String hash_key = orderid;
            try {
                boolean IsUpdateRedis = true;
                RedisPosPub redis = new RedisPosPub();
                if ("4".equals(mtOrderStatus)||"8".equals(mtOrderStatus))
                {
                    //已接单,已完成状态不在写缓存。
                    IsUpdateRedis = false;
                }
                else
                {
                    writelog_waimai("【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                    boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                    if (isexistHashkey)
                    {
                        if(status_json!=null&&status_json.equals("1"))//新订单的时候，已经存在了，说明缓存已经是最新的状态了，不需要更新缓存
                        {
                            IsUpdateRedis = false;
                            writelog_waimai("【MT订单开立状态】【MT已经存在hash_key的缓存】【说明缓存已经最新状态不用更新缓存】！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                       /* else
                        {
                            redis.DeleteHkey(redis_key, hash_key);//
                            writelog_waimai("【删除存在hash_key的缓存】成功！");
                        }*/
                    }
                    
                }
                if(IsUpdateRedis)
                {
                    boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                    if (nret) {
                        writelog_waimai("【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    } else {
                        writelog_waimai("【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                    
                }
                //redis.Close();
                
            } catch (Exception e) {
                writelog_waimai(
                        "【写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
            }
            
            return Response_json;
            
        } catch (Exception e) {
            writelog_waimaiException("解析美团外卖发送的请求格式有误！" + e.getMessage());
            return null;
        }
        
    }
    
    
    public static String GetMTCancelResponse(String responseStr) throws Exception
    {
        if (responseStr == null || responseStr.length() == 0) {
            writelog_waimaiException("美团外卖发送的订单取消消息为空！");
            return null;
        }
        try
        {
            writelog_waimai("【解析MT发送消息类型=取消消息】" + responseStr);
            String loadDocType = orderLoadDocType.MEITUAN;
            JSONObject jsonobj = new JSONObject(responseStr);
            JSONObject jsonobjresponse = new JSONObject();
            String orderid = jsonobj.get("order_id").toString();// 订单ID
            String reasonCode = jsonobj.get("reason_code").toString();// 原因码
            // =1103，已退单
            String reason = "";
            if (!jsonobj.isNull("reason")) {
                reason = jsonobj.get("reason").toString();
            }
            String deal_op_type = "";//当前订单取消操作人类型，1-用户、 2-商家端 、3-客服、4-BD	、5-系统 、6-开放平台
            
            
            //查询下数据库
            order orderDB = GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, orderid);
            if(orderDB==null)
            {
                orderDB = getMTOrderOnline(orderid,"","");
                if (orderDB==null)
                {
                    writelog_waimai("【MT订单取消查询本地订单】异常！在线查询订单失败，单号="+orderid);
                    return null;
                }
            }
            try
            {
                
                String eId = orderDB.geteId();
                String shopNo = orderDB.getShopNo();
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                String hash_key = orderid;
                
                String status = "3";
                String refundStatus = "1";
                boolean IsUpdateRedis = true;//是否更新缓存。
                // 原因码 =1103，表示已退单
                if (reasonCode != null && reasonCode.equals("1103")) {
                    status = "12";
                    refundStatus = "6";
                }
                else
                {
                    
                    try
                    {
                        //查询下缓存 如果缓存中是已退单状态，就无需更新缓存了，
                        RedisPosPub redis = new RedisPosPub();
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey)
                        {
                            //如果存在看下缓存里面状态是不是 已经是退单成功状态
                            String redis_order = redis.getHashMap(redis_key, hash_key);
                            
                            JSONObject redis_order_obj = new JSONObject(redis_order);
                            String	status_redis =  redis_order_obj.optString("status");
                            if(status_redis.equals("12"))//缓存里面已经是退成功状态
                            {
                                IsUpdateRedis = false;
                                status = "12";
                                refundStatus = "6";
                            }
                        }
                        //redis.Close();
                    }
                    catch (Exception e)
                    {
                    }
                    
                    //如果缓存没有，判断下数据库里面，是不是已经是退单状态
                    if(IsUpdateRedis)
                    {
                        try
                        {
                            String status_db =  orderDB.getStatus();//数据库里面订单状态
                            if(status_db.equals("12"))
                            {
                                status = "12";
                                refundStatus = "6";
                            }
                            
                            
                        }
                        catch (Exception e)
                        {
                        
                        }
                        
                    }
                    
                    
                }
                // 更新订单状态
                
                orderDB.setStatus(status);
                orderDB.setRefundStatus(refundStatus);
                orderDB.setRefundReason(reason);
                
                ParseJson pj = new ParseJson();
                String Response_json = pj.beanToJson(orderDB) ;
                if(!IsUpdateRedis)//无需更新缓存
                {
                    writelog_waimai("【MT订单取消开始写缓存】【缓存中是已退单状态,无需更新缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                            + " hash_value:" + Response_json);
                    return Response_json;
                }
                
                
                try
                {
                    RedisPosPub redis = new RedisPosPub();
                   /* boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                    if (isexistHashkey) {
                        redis.DeleteHkey(redis_key, hash_key);//
                        writelog_waimai(
                                "【MT删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                    }*/
                    writelog_waimai("【MT订单取消开始写缓存】" + "redis_key:" + redis_key + ",hash_key:" + hash_key
                            + " hash_value:" + Response_json);
                    boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                    if (nret) {
                        HelpTools.writelog_waimai(
                                "【MT订单取消写缓存】OK" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                    } else {
                        HelpTools.writelog_waimai(
                                "【MT订单取消写缓存】Error" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                    }
                    //redis.Close();
                    
                    
                }
                catch (Exception e)
                {
                    HelpTools.writelog_waimai("更新缓存中MT取消消息异常！" + e.getMessage());
                    
                }
                
                return Response_json;
            }
            catch (Exception e)
            {
                writelog_waimai("【MT订单取消】异常："+e.getMessage());
                return null;
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimaiException("解析MT发送的订单取消消息格式有误！");
            return null;
        }
        
        
        
        
    }
    
    
    public static String GetMTRefundResponse(String responseStr) throws Exception
    {
        if (responseStr == null || responseStr.length() == 0) {
            writelog_waimaiException("美团外卖发送的订单退款消息为空！");
            return null;
        }
        try
        {
            writelog_waimai("【解析MT发送消息类型=订单退款消息】" + responseStr);
            String loadDocType = orderLoadDocType.MEITUAN;
            String messageType = "1";//1整单退类型、2部分退
            JSONObject jsonobj = new JSONObject(responseStr);
            JSONObject jsonobjresponse = new JSONObject();
            String orderid = jsonobj.get("order_id").toString();// 订单ID
            String notify_type = jsonobj.get("notify_type").toString();// 原因码
            String food = "";//部分退单才会有的节点
            double refundMoney = 0;//部分退单才会有的节点
            if(!jsonobj.isNull("food"))
            {
                messageType = "2";
                food = jsonobj.get("food").toString();
                try
                {
                    refundMoney = Double.parseDouble(jsonobj.get("money").toString());
                    
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
            }
            
            // =1103，已退单
            String reason = "";
            if (!jsonobj.isNull("reason")) {
                reason = jsonobj.get("reason").toString();
            }
            if(messageType.equals("2"))
            {
                writelog_waimai("【解析MT发送消息类型=订单退款消息】【部分退款】" );
            }
            else
            {
                writelog_waimai("【解析MT发送消息类型=订单退款消息】【整单退款】" );
            }
            
            //查询下数据库
            String orderDBJson = "";//GetOrderInfoByOrderNO(StaticInfo.dao,"",  "", "2", orderid);
            order orderDB = GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, orderid);
            if(orderDB==null)
            {
                writelog_waimai("【MT订单退款查询本地订单】异常！单号="+orderid);
                return null;
            }
            try
            {
                //JSONObject jsonObj = new JSONObject(orderDBJson);
                
                
                String eId = orderDB.geteId();
                String shopNo = orderDB.getShopNo();
                String status_db =  orderDB.getStatus();//数据库里面订单状态
                String refundStatus_db =  orderDB.getRefundStatus();
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                String hash_key = orderid;
                
                String status = "11";// 订单状态1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单	 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
                String refundStatus = "1";// 订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
                if (messageType.equals("1")) //整单退款
                {
                    if (notify_type.equals("apply"))// 发起退款
                    {
                        refundStatus = "2";
                    }
                    else if (notify_type.equals("agree"))// 确认退款
                    {
                        status = "12";
                        refundStatus = "6";
                    }
                    else if (notify_type.equals("reject"))// 驳回退款
                    {
                        refundStatus = "3";
                    }
                    else if (notify_type.equals("cancelRefund"))// 用户取消退款申请
                    {
                        refundStatus = "5";
                    }
                    else if (notify_type.equals("cancelRefundComplaint"))// 取消退款申诉
                    {
                        refundStatus = "5";
                    }
                    else
                    {
                        writelog_waimai("解析MT发送类型=整单退款消息！通知类型异常！notifyType= " + notify_type);
                        return null;
                    }
                    
                    
                }
                else
                {
                    if (notify_type.equals("part"))// 发起部分退款
                    {
                        refundStatus = "7";
                    }
                    else if (notify_type.equals("agree"))// 确认部分退款
                    {
                        status ="11";
                        refundStatus = "10";
                    }
                    else if (notify_type.equals("reject"))// 驳回部分退款
                    {
                        refundStatus = "8";
                        status = "11";
                    }
                    else if (notify_type.equals("cancelRefund"))// 取消申请部分退款
                    {
                        refundStatus = "9";
                        status = "11";
                    }
                    else if (notify_type.equals("cancelRefundComplaint"))// 取消退款申诉
                    {
                        refundStatus = "9";
                        status = "11";
                    }
                    else
                    {
                        writelog_waimai("解析MT发送类型=部分退款消息！通知类型异常！notifyType= " + notify_type);
                        return null;
                    }
                    
                    
                }
                
                orderDB.setStatus(status);
                orderDB.setRefundStatus(refundStatus);
                orderDB.setRefundReason(reason);
                orderDB.setRefundAmt(refundMoney);// 部分退单 的退款金额
                
                ParseJson pj = new ParseJson();
                
                if(refundStatus.equals("10"))
                {
                    try
                    {
                        //部分退单的商品
                        JSONObject foodObj =  new JSONObject("{\"food\":" + food + "}");
                        JSONArray partRefundGoodsArray = foodObj.getJSONArray("food");
                        
                        List<orderGoodsItem> goodsArray = orderDB.getGoodsList();
                        List<orderGoodsItem> goodsArray_PartRefund = new ArrayList<orderGoodsItem>();
                        boolean IsExistPartRefundGoods = false; // 检查是不是已经添加过部分退单商品了
                        int partRefundGoodsItem = 999;
                        if (goodsArray != null&&goodsArray.size()>0)
                        {
                            partRefundGoodsItem = goodsArray.size() + 1;
                            for (int j = goodsArray.size() - 1; j >= 0; j--)
                            {
                                //JSONObject oldObj = goodsArray.getJSONObject(j);
                                orderGoodsItem  oldObj =goodsArray.get(j);
                                //String qty_str = oldObj.getString("qty").toString();
                                double qty = oldObj.getQty();
                                
                                
                                if (qty < 0)
                                {
                                    IsExistPartRefundGoods = true;
                                }
                                else
                                {
                                    goodsArray_PartRefund.add(oldObj);
                                    
                                }
                                
                            }
                            
                        }
                        // 循环部分退款的商品，添加到之前的商品（数量为负，金额为负）
                        if (IsExistPartRefundGoods == false)
                        {
                            for (int i = 0; i < partRefundGoodsArray.length(); i++)
                            {
                                try {
                                    
                                    orderGoodsItem goodsItem = new orderGoodsItem();
                                    goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                                    goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                                    
                                    JSONObject job = partRefundGoodsArray.getJSONObject(i);
                                    
                                    String app_food_code = job.get("app_food_code").toString();// APP方菜品id
                                    String food_name = job.get("food_name").toString();// 菜品名称
                                    String sku_id = job.get("sku_id").toString();// sku编码
                                    String quantity_str = job.get("count").toString();// 部分退单商品数量
                                    // 转成负数
                                    String price_str = job.get("refund_price").toString();// 退款的商品单价，此字段默认为活动折扣后价格
                                    String unit = "";// 单位
                                    if (!job.isNull("unit")) {
                                        unit = job.get("unit").toString();
                                    }
                                    if (unit == null || unit.isEmpty()) {
                                        unit = "份";// 默认个
                                    }
                                    // String food_discount =
                                    // job.get("food_discount").toString();//商品折扣，默认为1，仅美团商家可设置
                                    String attr = "";// 菜品属性 "中辣,微甜"
                                    if (!job.isNull("food_property")) {
                                        attr = job.get("food_property").toString();
                                    }
                                    String spec = "";// 菜品规格名称，
										/*try {
											spec = job.get("spec").toString();// 菜品规格名称
										} catch (Exception e) {
											spec = "";
										}*/
                                    String cart_id = "1号口袋";//默认，可以循环比较，没必要。
                                    
                                    double price = 0;
                                    double quantity = 0; // 部分退单的商品数量为负
                                    try {
                                        price = Double.parseDouble(price_str);
                                    } catch (Exception e) {
                                        price = 0;
                                    }
                                    try {
                                        quantity = 0 - Double.parseDouble(quantity_str);
                                    } catch (Exception e) {
                                        quantity = 0;
                                    }
                                    
                                    double amt = price * quantity;
                                    
                                    // 计算餐盒 包装费
                                    String box_price_str = job.get("box_price").toString();// 餐盒价格
                                    String box_num_str = job.get("box_num").toString();// 餐盒数量
                                    // 部分退单的商品数量为负
                                    double box_price = 0;
                                    double box_num = 0;// 部分退单的商品数量为负
                                    
                                    try {
                                        box_price = Double.parseDouble(box_price_str);
                                    } catch (Exception e) {
                                        box_price = 0;
                                    }
                                    try {
                                        box_num = 0 - Math.ceil(Double.parseDouble(box_num_str));
                                    } catch (Exception e) {
                                        box_num = 0;
                                    }
                                    
                                    goodsItem.setItem(partRefundGoodsItem+"");
                                    goodsItem.setPluNo(sku_id);
                                    goodsItem.setPluBarcode(sku_id);
                                    goodsItem.setSkuId(sku_id);
                                    goodsItem.setPluName(food_name);
                                    goodsItem.setSpecName(spec);
                                    goodsItem.setAttrName(attr);
                                    goodsItem.setFeatureNo("");
                                    goodsItem.setFeatureName("");
                                    goodsItem.setsUnit(unit);
                                    goodsItem.setPrice(price);
                                    goodsItem.setOldPrice(price);
                                    goodsItem.setQty(quantity);
                                    goodsItem.setAmt(amt);
                                    goodsItem.setOldAmt(amt);
                                    goodsItem.setDisc(0);
                                    goodsItem.setBoxNum(box_num);
                                    goodsItem.setBoxPrice(box_price);
                                    goodsItem.setsUnitName(unit);
                                    goodsItem.setGoodsGroup(cart_id);
                                    goodsItem.setIsMemo("N");
                                    partRefundGoodsItem++;
                                    goodsArray_PartRefund.add(goodsItem);
                                    
                                }
                                catch (Exception e)
                                {
                                    writelog_waimai("解析MT部分退款food节点点失败：" + e.getMessage());
                                    continue;
                                }
                                
                            }
                            
                            orderDB.setGoodsList(goodsArray_PartRefund);
                        }
                        
                        
                    }
                    catch (Exception e)
                    {
                        
                        writelog_waimai("添加MT部分退款food节点点失败：" + e.getMessage());
                    }
                    
                }
                
                String Response_json = pj.beanToJson(orderDB);
                
                try
                {
                    boolean IsUpdateRedis = true;//是否更新缓存。
                    RedisPosPub redis = new RedisPosPub();
                    boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                    if (isexistHashkey)
                    {
                        //如果存在看下缓存里面状态是不是 已经是退单成功状态
                        String redis_order = redis.getHashMap(redis_key, hash_key);
                        try
                        {
                            JSONObject redis_order_obj = new JSONObject(redis_order);
                            String	status_redis =  redis_order_obj.optString("status");
                            String	refundStatus_redis =  redis_order_obj.optString("refundStatus");
                            if(refundStatus.equals("2"))
                            {
                                if(status_redis.equals("12"))//缓存里面已经是退成功状态
                                {
                                    IsUpdateRedis = false;
                                }
                            }
                            else	if(refundStatus.equals("7"))
                            {
                                if(refundStatus_redis.equals("10"))////缓存里面已经是退成功状态
                                {
                                    IsUpdateRedis = false;
                                }
                            }
                            
                        }
                        catch (Exception e)
                        {
                        }
                        
                        
                        if(IsUpdateRedis)
                        {
                            redis.DeleteHkey(redis_key, hash_key);
                            writelog_waimai(
                                    "【MT删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        
                    }
                    
                    
                    //这里对比下数据库状态
                    //MT可能先推送退单成功状态，再推送申请退单状态
                    if(refundStatus.equals("2"))
                    {
                        if(status_db.equals("12"))
                        {
                            IsUpdateRedis = false;
                        }
                    }
                    else	if(refundStatus.equals("7"))
                    {
                        if(refundStatus_db.equals("10"))
                        {
                            IsUpdateRedis = false;
                        }
                    }
                    
                    
                    if(IsUpdateRedis)
                    {
                        
                        writelog_waimai("【MT订单退款开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                + " hash_value:" + Response_json);
                        boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                        if (nret) {
                            HelpTools.writelog_waimai(
                                    "【MT订单退款写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        } else {
                            HelpTools.writelog_waimai(
                                    "【MT订单退款写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        
                    }
                    else
                    {
                        writelog_waimai("【MT订单退款开始写缓存】【无需写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                + " hash_value:" + Response_json+" 数据库中订单status="+status_db+" refundStatus="+refundStatus_db);
                        
                    }
                    
                    
                    
                    //redis.Close();
                    
                    
                }
                catch (Exception e)
                {
                    HelpTools.writelog_waimai("更新缓存中订单退款消息异常！" + e.getMessage());
                }
                
                return Response_json;
            }
            catch (Exception e)
            {
                writelog_waimai("【MT订单取消】异常："+e.getMessage());
                return null;
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimaiException("解析美团发送的订单取消消息格式有误！");
            return null;
        }
        
        
        
        
    }
    
    
    public static String GetJBPResponse(String responseStr) throws Exception {
        
        if (responseStr == null || responseStr.length() == 0) {
            // writelog_waimaiException("美团外卖发送的请求为空！");
            return null;
        }
        //writelog_fileName("【聚宝盆URL转码前】"+responseStr,"MTRequsetLog");
        // 解析收到的美团外卖请求
        
        String[] MTResquest = responseStr.split("&");//
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_waimaiException("解析聚宝盆发送的请求格式有误！");
            return null;
        }
        
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        String urlDecodeString = "";
        for (String string_mt : MTResquest) {
            
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                /*
                 * String[] ss = string_mt.split("="); //包含多个=会有问题
                 * map_MTResquest.put(ss[0], ss[1]);
                 */
					/*String responseStrEncode = getURLEncoderString(s2);
					String s2_decode1 = getURLDecoderString(responseStrEncode);
					String s2_decode2 = getURLDecoderString(s2_decode1);
					writelog_waimai("【聚宝盆URL转码后1】" + s2_decode2);*/
                
                String s2_decode = getURLDecoderString(s2);
                //writelog_waimai("【聚宝盆URL转码后2】" +s1+"="+ s2_decode);
                map_MTResquest.put(s1, s2_decode);
                urlDecodeString +=s1+"="+s2_decode+"&";
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        
        writelog_waimai("【聚宝盆URL转码后2】" +urlDecodeString);
        
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String sTime = df.format(cal.getTime());
        df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String sDateTime = df.format(cal.getTime());
        
        // 会有4种消息类型
        String orderCancelString = map_MTResquest.get("orderCancel");
        String orderRefundString = map_MTResquest.get("orderRefund");
        String orderString = map_MTResquest.get("order");
        String orderPartRefundString = map_MTResquest.get("partOrderRefund");// 部分退款
        String loadDocType = orderLoadDocType.MEITUAN;// 渠道类型
        String mtOrderStatus = "";//
        
        if (orderString != null) {
            // region 推送新订单、已确认、已完成的消息
            try // 推送新订单、已确认、已完成的消息
            {
                writelog_waimai("解析聚宝盆发送类型=新订单、已确认、已完成消息！");
                
                JSONObject jsonobj = new JSONObject(orderString);
                order dcpOrder = new order();
                dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
                dcpOrder.setPay(new ArrayList<orderPay>());
                
                //detail
                try
                {
                    String detailString = jsonobj.optString("detail");
                    JSONArray detail_array = new JSONArray(detailString);
                    jsonobj.put("detail", detail_array);
                    
                }
                catch (Exception e)
                {
                
                }
                
                //extras
                try
                {
                    //extras
                    String extrasString = jsonobj.optString("extras");
                    JSONArray extras_array = new JSONArray(extrasString);
                    jsonobj.put("extras", extras_array);
                    
                }
                catch (Exception e)
                {
                
                }
                
                //poiReceiveDetail
                try
                {
                    //poiReceiveDetail
                    String poiReceiveDetailString = jsonobj.optString("poiReceiveDetail");
                    JSONObject poiReceiveDetail_obj = new JSONObject(poiReceiveDetailString);
                    jsonobj.put("poiReceiveDetail", poiReceiveDetail_obj);
                    
                }
                catch (Exception e)
                {
                
                }
                String app_poi_code = map_MTResquest.get("ePoiId").toString();// APP方商家ID
                
                //JSONObject jsonobjresponse = new JSONObject();
                
                String orderid = jsonobj.optString("orderId");// 订单ID
                String orderStatus = jsonobj.optString("status");// 订单状态
                // 美团（1：用户已提交订单；2：可推送到APP方平台也可推送到商家；3：商家已收到；4：商家已确认；8：已完成；9：已取消
                mtOrderStatus = orderStatus;
                //writelog_waimai("解析聚宝盆发送类型status=" + orderStatus);
                String has_invoiced = jsonobj.optString("hasInvoiced");
                String shopname = jsonobj.optString("poiName");// 美团商家名称
                Map<String, String>	mappingShopMap = GetJBPMappingShop(app_poi_code);//查询下门店对应缓存MT_MappingShop
                String eId = mappingShopMap.get("eId");
                String erpshopNo = mappingShopMap.get("erpShopNo");
                String channelId = mappingShopMap.get("channelId");
                String orderShopNo = mappingShopMap.get("orderShopNo");
                String erpShopName = mappingShopMap.getOrDefault("erpShopName", "");
                if (erpShopName==null||erpShopName.isEmpty())
                {
                    erpShopName = shopname;
                }
                if (channelId==null||channelId.isEmpty())
                {
                    channelId = loadDocType+"001";//默认
                }
                
                writelog_waimai("【MT获取对应ERP门店】app_poi_code="+app_poi_code+"-->对应三方美团门店ID="+orderShopNo+",ERP的企业ID="+eId+",门店="+erpshopNo+",门店名称="+erpShopName+",订单号="+orderid);
                
                dcpOrder.seteId(eId);
                dcpOrder.setLoadDocType(loadDocType);
                dcpOrder.setChannelId(channelId);
                dcpOrder.setOrderNo(orderid);//dcp单号=来源单号
                dcpOrder.setLoadDocOrderNo(orderid);//来源单号
                dcpOrder.setLoadDocBillType("");//来源单据类型
                dcpOrder.setOrderShop(orderShopNo);//第三方门店ID
                dcpOrder.setOrderShopName(shopname);;//第三方门店名称
                dcpOrder.setShopNo(erpshopNo);
                dcpOrder.setShopName(erpShopName);
                dcpOrder.setShippingShopNo(erpshopNo);
                dcpOrder.setShippingShopName(erpShopName);
                dcpOrder.setMachShopNo(erpshopNo);
                dcpOrder.setMachShopName(erpShopName);
                
                String companyno = "99";
                String erpshopno = " ";
                
                // 订单中心status
                /*
                 * 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送
                 * 10.已发货 11.已完成 12.已退单
                 */
                // 订单中心status
                /*
                 * 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货
                 * 11.已完成 12.已退单
                 */
                
                dcpOrder.setStatus("");
                dcpOrder.setRefundStatus("1");
                if (orderStatus.equals("2") || orderStatus.equals("1"))// 推送已经支付的订单
                {
                    dcpOrder.setStatus("1");
                }
                else if (orderStatus.equals("4"))// 门店已接单
                {
                    dcpOrder.setStatus("2");
                }
                else if (orderStatus.equals("8"))// 订单已完成
                {
                    dcpOrder.setStatus("11");
                }
                else if (orderStatus.equals("9"))// 已取消
                {
                    dcpOrder.setStatus("3");
                }
                
                dcpOrder.setMemo(jsonobj.optString("caution",""));// // 忌口或备注
                dcpOrder.setSn(jsonobj.optString("daySeq","0"));// 门店当天的订单流水号
                /***********************发票相关处理【开始】*******************************/
                orderInvoice dcpOrderInvoiceDetail = new orderInvoice();
                String isInvoice = "N";// 是否开发票
                if (has_invoiced != null && has_invoiced.equals("1")) {
                    isInvoice = "Y";
                }
                
                dcpOrderInvoiceDetail.setIsInvoice(isInvoice);;// 是否开发票
                dcpOrderInvoiceDetail.setInvoiceTitle(jsonobj.optString("invoiceTitle",""));// String  // 发票抬头
                dcpOrderInvoiceDetail.setTaxRegNumber(jsonobj.optString("taxpayerId",""));//String 纳税人识别号，该信息默认不推送，如有需求可在开发者中心订阅
                String peopleType ="2";//发票类型 1.公司 2.个人
                if (dcpOrderInvoiceDetail.getTaxRegNumber()!=null&&!dcpOrderInvoiceDetail.getTaxRegNumber().trim().isEmpty())
                {
                    peopleType ="1";
                }
                dcpOrderInvoiceDetail.setPeopleType(peopleType);// 1.公司 2.个人
                dcpOrderInvoiceDetail.setInvoiceType("");// 台湾 二联  三联
                
                dcpOrder.setInvoiceDetail(dcpOrderInvoiceDetail);
                /***********************发票相关处理【结束】*******************************/
                
                String orderCodeView = jsonobj.optString("orderIdView","");
                orderCodeView = getMTOrderIdView(orderCodeView);
                dcpOrder.setOrderCodeView(orderCodeView);//订单展示ID（美团外卖需要按规则生成一维码字符串）
                
                String ctime = jsonobj.optString("ctime","");// 时间戳秒
                String createDatetime = sDateTime;
                String createDate_order = sDate;
                String createTime_order = sTime;
                try {
                    long lt = new Long(ctime);
                    Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                    createDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
                    createDate_order = new SimpleDateFormat("yyyyMMdd").format(date);
                    createTime_order = new SimpleDateFormat("HHmmss").format(date);
                } catch (Exception e) {
                
                }
                dcpOrder.setCreateDatetime(createDatetime);// String 创建时间
                
                String longitude ="0";
                String latitude ="0";
                if (!jsonobj.isNull("longitude")) {
                    longitude = jsonobj.get("longitude").toString();// 经度
                }
                if (!jsonobj.isNull("latitude")) {
                    latitude = jsonobj.get("latitude").toString();// 纬度
                }
                dcpOrder.setLongitude(longitude);
                dcpOrder.setLatitude(latitude);
                
                String deliveryTime = jsonobj.optString("deliveryTime");// 用户预计送达时间，“立即送达”时为0
                String shipDate = createDate_order;//配送日期默认下单日期
                String shipTime = createTime_order;//配送时间默认下单时间
                String isBook = "N";
                if (deliveryTime != null && deliveryTime.equals("0") == false) {
                    isBook = "Y";
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                        long lt = new Long(deliveryTime);
                        Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                        shipDate = dateFormat.format(date);
                        shipTime = timeFormat.format(date);
                        
                    } catch (Exception e) {
                    
                    }
                }
                
                dcpOrder.setShipDate(shipDate);
                dcpOrder.setShipStartTime(shipTime);//配送开始时间
                dcpOrder.setShipEndTime(shipTime);//配送结束时间
                dcpOrder.setIsBook(isBook);// 是否预订单
                
                
                String shipType = "1"; // 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
                String isMerPay = "N";//配送费是否商家结算
                
                String logisticsCode = jsonobj.optString("logisticsCode");;//配送方式  0000	商家自配送
                if(logisticsCode!= null&&logisticsCode.equals("0000"))
                {
                    shipType = "6";//商家自配送
                    isMerPay = "Y";
                }
                
                // 因为pickType节点需要联系美团人员，比较麻烦，所以用送餐地址去判断下，到店自取的送餐地址=
                String recipientAddress = jsonobj.optString("recipientAddress","");
                if (recipientAddress != null && recipientAddress.startsWith("到店自取")) {
                    shipType = "3";
                }
                
                String pickType = jsonobj.optString("pickType");// 0：普通取餐；1：到店取餐; // 该信息默认不推送，如有需求可联系开放平台工作人员开通
                if (pickType != null && pickType.equals("1")) {
                    shipType = "3";
                }
                
                //新增隐私地址的字段：recipientAddressDesensitization。此字段不会包含recipientAddress字段中@#后面的值
                //logistics_code推送非 0000、5001、00009003，必须使用隐私地址。
                String recipientAddressDesensitization = "";
                try
                {
                    recipientAddressDesensitization  = jsonobj.optString("recipientAddressDesensitization","");
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                if(!recipientAddressDesensitization.isEmpty())
                {
                    if(logisticsCode.equals("0000")||logisticsCode.equals("5001")||logisticsCode.equals("00009003"))
                    {
                    
                    }
                    else
                    {
                        recipientAddress = recipientAddressDesensitization;
                    }
                }
                dcpOrder.setShipType(shipType);// 配送类型1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
                dcpOrder.setIsMerPay(isMerPay);
                dcpOrder.setAddress(recipientAddress);// 收件人地址
                String recipientName = jsonobj.optString("recipientName","");
                String recipientPhone = jsonobj.optString("recipientPhone","");
                dcpOrder.setContMan(recipientName);// 收件人姓名
                dcpOrder.setGetMan(recipientName);// 收件人姓名
                dcpOrder.setContTel(recipientPhone);// String
                dcpOrder.setGetManTel(recipientPhone);// String
                dcpOrder.setPayStatus("3");// 1.未支付 2.部分支付 3.付清
                dcpOrder.setsTime(sDateTime);//系统时间 yyyyMMddhhmmssSSS
                
                dcpOrder.setShopShareShipfee(0);// 商家替用户承担的配送费
                dcpOrder.setRefundAmt(0);// 部分退单 的退款金额
                
                double tot_oldAmt = 0;
                try
                {
                    tot_oldAmt = Double.parseDouble(jsonobj.optString("originalPrice"));
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                dcpOrder.setTot_oldAmt(tot_oldAmt);
                
                double tot_Amt = 0;
                try
                {
                    tot_Amt = Double.parseDouble(jsonobj.optString("total")); // 订单总价
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                dcpOrder.setTot_Amt(tot_Amt);
                
                double shipFee = 0;
                try
                {
                    shipFee = Double.parseDouble(jsonobj.optString("shippingFee"));// 门店配送费
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                dcpOrder.setShipFee(shipFee);
                
                // 解析goods
                JSONArray goodsarray = jsonobj.getJSONArray("detail");
                JSONArray array = new JSONArray();
                int item = 0;// 项次
                double packageFee = 0;// 包装费
                double tot_qty = 0;
                for (int i = 0; i < goodsarray.length(); i++) {
                    try {
                        item++;
                        orderGoodsItem goodsItem = new orderGoodsItem();
                        goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                        goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                        
                        JSONObject job = goodsarray.getJSONObject(i);
                        
                        String app_food_code = job.optString("app_food_code");// APP方菜品id
                        String food_name = job.optString("food_name");// 菜品名称
                        String sku_id = job.optString("sku_id");// sku编码
                        String quantity_str = job.optString("quantity");// 商品数量
                        String price_str = job.optString("price");// 商品单价，此字段默认为活动折扣后价格
                        String unit = job.optString("unit");// 单位
                        String attr = job.optString("food_property","");// 菜品属性  // "中辣,微甜"
                        String spec = "";// 菜品规格名称，
                        try {
                            spec = job.optString("spec");// 菜品规格名称
                        } catch (Exception e) {
                            spec = "";
                        }
                        String cart_id = "";
                        int cart_no = 0;
                        try {
                            cart_id = job.optString("cart_id");// 商品所在的口袋，0为1号口袋，1为2号口袋
                            cart_no = Integer.parseInt(cart_id) + 1;
                            cart_id = cart_no + "号口袋";
                        } catch (Exception e) {
                            cart_id = "";
                        }
                        
                        double price = 0;
                        double quantity = 0;
                        try {
                            price = Double.parseDouble(price_str);
                        } catch (Exception e) {
                            price = 0;
                        }
                        try {
                            quantity = Double.parseDouble(quantity_str);
                        } catch (Exception e) {
                            quantity = 0;
                        }
                        double amt = price*quantity;
                        tot_qty +=quantity;
                        
                        // 计算餐盒 包装费
                        String box_price_str = job.optString("box_price");// 餐盒价格
                        String box_num_str = job.optString("box_num");// 餐盒数量
                        double box_price = 0;
                        double box_num = 0;
                        //餐盒数量,在计算餐盒数量和餐盒费用时，请先按照商品规格维度将餐盒数量向上取整后，再乘以相应的餐盒费单价，计算得出餐盒费用。
                        try {
                            box_price = Double.parseDouble(box_price_str);
                        } catch (Exception e) {
                            box_price = 0;
                        }
                        try {
                            box_num = Math.ceil(Double.parseDouble(box_num_str));
                        } catch (Exception e) {
                            box_num = 0;
                        }
                        packageFee += box_price * box_num;
                        
                        goodsItem.setItem(item+"");
                        goodsItem.setPluNo(sku_id);
                        goodsItem.setPluBarcode(sku_id);
                        goodsItem.setSkuId(sku_id);
                        goodsItem.setPluName(food_name);
                        goodsItem.setSpecName(spec);
                        goodsItem.setAttrName(attr);
                        goodsItem.setFeatureNo("");
                        goodsItem.setFeatureName("");
                        goodsItem.setsUnit(unit);
                        goodsItem.setPrice(price);
                        goodsItem.setOldPrice(price);
                        goodsItem.setQty(quantity);
                        goodsItem.setAmt(amt);
                        goodsItem.setOldAmt(amt);
                        goodsItem.setDisc(0);
                        goodsItem.setBoxNum(box_num);
                        goodsItem.setBoxPrice(box_price);
                        goodsItem.setsUnitName(unit);
                        goodsItem.setGoodsGroup(cart_id);
                        goodsItem.setIsMemo("N");
                        
                        dcpOrder.getGoodsList().add(goodsItem);
                        
                    } catch (Exception e) {
                        writelog_waimai("解析聚宝盆detail节点失败：" + e.getMessage());
                        continue;
                    }
                    
                }
                
                dcpOrder.setPackageFee(packageFee);// 包装费（MT没有直接返回，需要计算）
                dcpOrder.setTot_qty(tot_qty);
                dcpOrder.setTotQty(dcpOrder.getTot_qty());
                dcpOrder.setLoadDocTypeName("美团");
                dcpOrder.setChannelIdName("美团");
                
                
                // 解析extras
                double totDisc = 0;
                double platformDisc = 0;
                double sellerDisc = 0;
                String memo_zengsong= "【买赠】";//赠送得商品，在折扣类型里面，只要一个说明
                boolean isExistZengsong = false;//是否存在买赠
                try {
                    JSONArray extrasarray = jsonobj.getJSONArray("extras");
                    
                    for (int i = 0; i < extrasarray.length(); i++) {
                        try {
                            JSONObject job = extrasarray.getJSONObject(i);
                            String reduce_fee_str = job.optString("reduce_fee");// 活动优惠金额，也即美团承担活动费用和商户承担活动费用的总和
                            String remark = "";
                            String type_mt = "";
                            BigDecimal reduce_fee_B = new BigDecimal("0");
                            try
                            {
                                reduce_fee_B = new BigDecimal(reduce_fee_str);
                            } catch (Exception e)
                            {
                                // TODO: handle exception
                            }
                            
                            //美团瞎搞，目前发现 买赠类型折扣，只写商家承担折扣，也不写总得折扣，而且也没有商品明细，只是加了一个折扣类型type=23
                            if(reduce_fee_B.compareTo(BigDecimal.ZERO)==0)
                            {
                                
                                try
                                {
                                    remark = job.optString("remark");
                                    if (remark!=null&&remark.isEmpty()==false)
                                    {
                                        memo_zengsong +=remark+",";
                                        isExistZengsong = true;
                                    }
                                    
                                    type_mt = job.optString("type");
                                }
                                catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                
                                writelog_waimai("存在【买赠】折扣，美团折扣类型type="+type_mt+",折扣描述remark="+remark+",单号orderNo="+orderid);
                                
                                continue;
                            }
                            
                            
                            try {
                                totDisc += Double.parseDouble(reduce_fee_str);
                            } catch (Exception e) {
                                totDisc += 0;
                            }
                            
                            String mt_charge_str = job.optString("mt_charge");// 优惠金额中美团承担的部分，该信息默认不推送，如有需求可在开发者中心订阅
                            try {
                                platformDisc += Double.parseDouble(mt_charge_str);
                            } catch (Exception e) {
                                platformDisc += 0;
                            }
                            
                            String poi_charge_str = job.optString("poi_charge");// 优惠金额中商家承担的部分，该信息默认不推送，如有需求可在开发者中心订阅
                            try {
                                sellerDisc += Double.parseDouble(poi_charge_str);
                            } catch (Exception e) {
                                sellerDisc += 0;
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            writelog_waimai("解析聚宝盆extras异常：" + e.getMessage());
                            continue;
                        }
                    }
                    
                } catch (Exception e) {
                    writelog_waimai("获取聚宝盆extras节点失败：" + e.getMessage());
                }
                if(isExistZengsong)
                {
                    dcpOrder.setMemo(dcpOrder.getMemo()+memo_zengsong);
                }
                
                dcpOrder.setTotDisc(totDisc);// 优惠总金额
                dcpOrder.setPlatformDisc(platformDisc);// 平台优惠总金额
                //dcpOrder.setSellerDisc(sellerDisc);// 商户优惠总金额
                dcpOrder.setSellerDisc(totDisc-platformDisc);// 商户优惠总金额
                
                // 解析poiReceiveDetail
                double incomeAmt = 0;
                double serviceCharge = 0;
                double shopIncome_onlinePayment = 0;
                try {
                    JSONObject poiReceiveDetail = jsonobj.getJSONObject("poiReceiveDetail");
                    
                    String wmPoiReceiveCent_str = poiReceiveDetail.optString("wmPoiReceiveCent");// 商家应收款，单位为分
                    String logisticsFee_str = poiReceiveDetail.optString("logisticsFee");// 用户实际支付配送费
                    // (分)
                    try {
                        incomeAmt = Double.parseDouble(wmPoiReceiveCent_str) / 100;
                    } catch (Exception e) {
                        incomeAmt = 0;
                    }
                    
                    String foodShareFeeChargeByPoi_str = poiReceiveDetail.optString("foodShareFeeChargeByPoi");// 商品分成，即平台服务费，单位为分
                    try {
                        serviceCharge = Double.parseDouble(foodShareFeeChargeByPoi_str) / 100;
                    } catch (Exception e) {
                        serviceCharge = 0;
                    }
                    
                    String onlinePayment_str = poiReceiveDetail.optString("onlinePayment");// 在线支付款，单位为分
                    try {
                        shopIncome_onlinePayment = Double.parseDouble(onlinePayment_str) / 100;
                    } catch (Exception e) {
                        shopIncome_onlinePayment = 0;
                    }
                    
                } catch (Exception e) {
                    writelog_waimai("获取聚宝盆poiReceiveDetail节点失败：" + e.getMessage());
                }
                
                dcpOrder.setIncomeAmt(incomeAmt);// 店铺实际收入
                dcpOrder.setServiceCharge(serviceCharge);// 平台服务费
                dcpOrder.setPayAmt(shopIncome_onlinePayment);// 在线支付款
                
                //调用支付方式
                StringBuffer errorPayMessage = new StringBuffer();
                HelpTools.updateOrderPayByMapping(dcpOrder, errorPayMessage);
                
                errorPayMessage = new StringBuffer();
                HelpTools.updateOrderDetailInfo(dcpOrder, errorPayMessage);
                
                HelpTools.updateOrderWithPackage(dcpOrder, "", errorPayMessage);
                
                String status_json = dcpOrder.getStatus();//获取下订单状态
                
                ParseJson pj = new ParseJson();
                String Response_json = pj.beanToJson(dcpOrder);
                
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + erpshopNo;
                // String hash_key = orderid + "&" + orderStatus;
                String hash_key = orderid;
                
                try {
                    boolean IsUpdateRedis = true;
                    RedisPosPub redis = new RedisPosPub();
                    if ("4".equals(mtOrderStatus)||"8".equals(mtOrderStatus))
                    {
                        //已接单,已完成状态不在写缓存。
                        IsUpdateRedis = false;
                    }
                    else
                    {
                        writelog_waimai("【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey) {
                            if(status_json!=null&&status_json.equals("1"))//新订单的时候，已经存在了，说明缓存已经是最新的状态了，不需要更新缓存
                            {
                                IsUpdateRedis = false;
                                writelog_waimai("【MT订单开立状态】【MT已经存在hash_key的缓存】【说明缓存已经最新状态不用更新缓存】！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
                            /*else
                            {
                                redis.DeleteHkey(redis_key, hash_key);//
                                writelog_waimai("【删除存在hash_key的缓存】成功！");
                            }*/
                        }
                    }
                    
                    if(IsUpdateRedis)
                    {
                        boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                        if (nret) {
                            writelog_waimai("【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        } else {
                            writelog_waimai("【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        
                    }
                    
                    //redis.Close();
                    
                } catch (Exception e) {
                    writelog_waimai(
                            "【写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
                }
                
                return Response_json;
                
            } catch (Exception e) {
                writelog_waimai("解析聚宝盆发送的请求格式有误！" + e.getMessage());
                return null;
            }
            // endregion
            
        } else if (orderCancelString != null) {
            // region 推送的取消消息
            try // 推送的取消消息
            {
                writelog_waimai("解析聚宝盆发送类型=取消消息！");
                
                JSONObject jsonobj = new JSONObject(orderCancelString);
                String orderid = jsonobj.optString("orderId");// 订单ID
                String reasonCode = jsonobj.optString("reasonCode");// 原因码
                // =1103，已退单
                String reason = "";
                if (!jsonobj.isNull("reason")) {
                    reason = jsonobj.optString("reason");
                }
                String deal_op_type = "";//当前订单取消操作人类型，1-用户、 2-商家端 、3-客服、4-BD	、5-系统 、6-开放平台
                
                //查询下数据库
                order orderDB = GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, orderid);
                if(orderDB==null)
                {
                    writelog_waimai("【MT订单取消查询本地订单】异常！单号="+orderid);
                    return null;
                }
                try
                {
                    
                    String eId = orderDB.geteId();
                    String shopNo = orderDB.getShopNo();
                    String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                    String hash_key = orderid;
                    
                    String status = "3";
                    String refundStatus = "1";
                    boolean IsUpdateRedis = true;//是否更新缓存。
                    // 原因码 =1103，表示已退单
                    if (reasonCode != null && reasonCode.equals("1103")) {
                        status = "12";
                        refundStatus = "6";
                    }
                    else
                    {
                        
                        try
                        {
                            //查询下缓存 如果缓存中是已退单状态，就无需更新缓存了，
                            RedisPosPub redis = new RedisPosPub();
                            boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                            if (isexistHashkey)
                            {
                                //如果存在看下缓存里面状态是不是 已经是退单成功状态
                                String redis_order = redis.getHashMap(redis_key, hash_key);
                                
                                JSONObject redis_order_obj = new JSONObject(redis_order);
                                String	status_redis =  redis_order_obj.optString("status");
                                if(status_redis.equals("12"))//缓存里面已经是退成功状态
                                {
                                    IsUpdateRedis = false;
                                    status = "12";
                                    refundStatus = "6";
                                }
                            }
                            //redis.Close();
                        }
                        catch (Exception e)
                        {
                        }
                        
                        //如果缓存没有，判断下数据库里面，是不是已经是退单状态
                        if(IsUpdateRedis)
                        {
                            try
                            {
                                String status_db =  orderDB.getStatus();//数据库里面订单状态
                                if(status_db.equals("12"))
                                {
                                    status = "12";
                                    refundStatus = "6";
                                }
                                
                                
                            }
                            catch (Exception e)
                            {
                            
                            }
                            
                        }
                        
                        
                    }
                    // 更新订单状态
                    
                    orderDB.setStatus(status);
                    orderDB.setRefundStatus(refundStatus);
                    orderDB.setRefundReason(reason);
                    
                    ParseJson pj = new ParseJson();
                    String Response_json = pj.beanToJson(orderDB) ;
                    if(!IsUpdateRedis)//无需更新缓存
                    {
                        writelog_waimai("【MT订单取消开始写缓存】【缓存中是已退单状态,无需更新缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                + " hash_value:" + Response_json);
                        return Response_json;
                    }
                    
                    
                    try
                    {
                        RedisPosPub redis = new RedisPosPub();
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey) {
                            redis.DeleteHkey(redis_key, hash_key);//
                            writelog_waimai(
                                    "【MT删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        writelog_waimai("【MT订单取消开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                + " hash_value:" + Response_json);
                        boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                        if (nret) {
                            HelpTools.writelog_waimai(
                                    "【MT订单取消写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        } else {
                            HelpTools.writelog_waimai(
                                    "【MT订单取消写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        //redis.Close();
                        
                        
                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai("更新缓存中MT取消消息异常！" + e.getMessage());
                        
                    }
                    
                    return Response_json;
                }
                catch (Exception e)
                {
                    writelog_waimai("【MT订单取消】异常："+e.getMessage());
                    return null;
                }
                
                
            } catch (Exception e) // 不要处理 继续往下走
            {
                writelog_waimai("解析聚宝盆发送的请求格式有误！" + e.getMessage());
                return null;
            }
            // endregion
            
        } else if (orderRefundString != null) {
            // region 推送的订单退款类消息
            try // 推送的订单退款类消息
            {
                
                writelog_waimai("解析聚宝盆发送类型=退款消息！");
                JSONObject jsonobj = new JSONObject(orderRefundString);
                String orderid = jsonobj.optString("orderId");// 订单ID
                String notify_type = jsonobj.optString("notifyType");//
                String reason = "";
                if (!jsonobj.isNull("reason")) {
                    reason = jsonobj.optString("reason");
                }
                order orderDB = GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, orderid);
                if(orderDB==null)
                {
                    writelog_waimai("【MT订单退款查询本地订单】异常！单号="+orderid);
                    return null;
                }
                
                
                try {
                    String eId = orderDB.geteId();
                    String shopNo = orderDB.getShopNo();
                    String status_db =  orderDB.getStatus();//数据库里面订单状态
                    String refundStatus_db =  orderDB.getRefundStatus();
                    String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                    String hash_key = orderid;
                    
                    String status = "11";// 订单状态1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单	 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
                    String refundStatus = "1";// 订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
                    if (notify_type.equals("apply"))// 发起退款
                    {
                        refundStatus = "2";
                    }
                    else if (notify_type.equals("agree"))// 确认退款
                    {
                        status = "12";
                        refundStatus = "6";
                    }
                    else if (notify_type.equals("reject"))// 驳回退款
                    {
                        refundStatus = "3";
                    }
                    else if (notify_type.equals("cancelRefund"))// 用户取消退款申请
                    {
                        refundStatus = "5";
                    }
                    else if (notify_type.equals("cancelRefundComplaint"))// 取消退款申诉
                    {
                        refundStatus = "5";
                    }
                    else
                    {
                        writelog_waimai("解析MT发送类型=整单退款消息！通知类型异常！notifyType= " + notify_type);
                        return null;
                    }
                    orderDB.setStatus(status);
                    orderDB.setRefundStatus(refundStatus);
                    orderDB.setRefundReason(reason);
                    ParseJson pj = new ParseJson();
                    String Response_json = pj.beanToJson(orderDB);
                    
                    try
                    {
                        boolean IsUpdateRedis = true;//是否更新缓存。
                        RedisPosPub redis = new RedisPosPub();
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey)
                        {
                            //如果存在看下缓存里面状态是不是 已经是退单成功状态
                            String redis_order = redis.getHashMap(redis_key, hash_key);
                            try
                            {
                                JSONObject redis_order_obj = new JSONObject(redis_order);
                                String	status_redis =  redis_order_obj.optString("status");
                                String	refundStatus_redis =  redis_order_obj.optString("refundStatus");
                                if(refundStatus.equals("2"))
                                {
                                    if(status_redis.equals("12"))//缓存里面已经是退成功状态
                                    {
                                        IsUpdateRedis = false;
                                    }
                                }
                                else if(refundStatus.equals("7"))
                                {
                                    if(refundStatus_redis.equals("10"))////缓存里面已经是退成功状态
                                    {
                                        IsUpdateRedis = false;
                                    }
                                }
                                
                            }
                            catch (Exception e)
                            {
                            }
                            
                            
                            if(IsUpdateRedis)
                            {
                                redis.DeleteHkey(redis_key, hash_key);
                                writelog_waimai(
                                        "【MT删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
                            
                        }
                        
                        
                        //这里对比下数据库状态
                        //MT可能先推送退单成功状态，再推送申请退单状态
                        if(refundStatus.equals("2"))
                        {
                            if(status_db.equals("12"))
                            {
                                IsUpdateRedis = false;
                            }
                        }
                        else if(refundStatus.equals("7"))
                        {
                            if(refundStatus_db.equals("10"))
                            {
                                IsUpdateRedis = false;
                            }
                        }
                        
                        
                        if(IsUpdateRedis)
                        {
                            
                            writelog_waimai("【MT订单退款开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                    + " hash_value:" + Response_json);
                            boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                            if (nret) {
                                HelpTools.writelog_waimai(
                                        "【MT订单退款写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            } else {
                                HelpTools.writelog_waimai(
                                        "【MT订单退款写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
                            
                        }
                        else
                        {
                            writelog_waimai("【MT订单退款开始写缓存】【无需写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                    + " hash_value:" + Response_json+" 数据库中订单status="+status_db+" refundStatus="+refundStatus_db);
                            
                        }
                        
                        
                        
                        //redis.Close();
                        
                        
                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai("更新缓存中订单退款消息异常！" + e.getMessage());
                    }
                    
                    return Response_json;
                    
                } catch (Exception e) {
                    writelog_waimai("解析聚宝盆发送退款消息格式有误！" + e.getMessage());
                    return null;
                }
                
            } catch (Exception e) {
                writelog_waimai("解析聚宝盆发送的请求格式有误！" + e.getMessage());
                return null;
            }
            // endregion
        } else if (orderPartRefundString != null) {
            // region 推送的订单部分退款类消息
            try // 推送的订单部分退款类消息
            {
                
                writelog_waimai("解析聚宝盆发送类型=部分退款消息！");
                JSONObject jsonobj = new JSONObject(orderPartRefundString);
                String orderid = jsonobj.optString("orderId");// 订单ID
                String notify_type = jsonobj.optString("notifyType");//
                double refundMoney = 0;//部分退单才会有的节点
                String reason = "";
                if (!jsonobj.isNull("reason")) {
                    reason = jsonobj.optString("reason");
                }
                if(!jsonobj.isNull("food"))
                {
                    
                    String food_string = jsonobj.optString("food");
                    JSONArray food_array = new JSONArray(food_string);
                    jsonobj.put("food", food_array);
                    try
                    {
                        refundMoney = Double.parseDouble(jsonobj.optString("money"));
                        
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                }
                
                order orderDB = GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, orderid);
                if(orderDB==null)
                {
                    writelog_waimai("【MT订单退款查询本地订单】异常！单号="+orderid);
                    return null;
                }
                
                try {
                    
                    String eId = orderDB.geteId();
                    String shopNo = orderDB.getShopNo();
                    String status_db =  orderDB.getStatus();//数据库里面订单状态
                    String refundStatus_db =  orderDB.getRefundStatus();
                    String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                    String hash_key = orderid;
                    
                    String status = "11";// 订单状态1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单	 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
                    String refundStatus = "1";// 订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
                    
                    // List<WMJBPPartRefundGoodModel> partRefundGoods = new
                    // ArrayList<WMJBPPartRefundGoodModel>();
                    
                    if (notify_type.equals("part"))// 发起部分退款
                    {
                        refundStatus = "7";
                    }
                    else if (notify_type.equals("agree"))// 确认部分退款
                    {
                        status ="11";
                        refundStatus = "10";
                    }
                    else if (notify_type.equals("reject"))// 驳回部分退款
                    {
                        refundStatus = "8";
                        status = "11";
                    }
                    else if (notify_type.equals("cancelRefund"))// 取消申请部分退款
                    {
                        refundStatus = "9";
                        status = "11";
                    }
                    else if (notify_type.equals("cancelRefundComplaint"))// 取消退款申诉
                    {
                        refundStatus = "9";
                        status = "11";
                    }
                    else
                    {
                        writelog_waimai("解析MT发送类型=部分退款消息！通知类型异常！notifyType= " + notify_type);
                        return null;
                    }
                    orderDB.setStatus(status);
                    orderDB.setRefundStatus(refundStatus);
                    orderDB.setRefundReason(reason);
                    orderDB.setRefundAmt(refundMoney);// 部分退单 的退款金额
                    
                    
                    if(refundStatus.equals("10"))
                    {
                        try
                        {
                            JSONArray partRefundGoodsArray = jsonobj.getJSONArray("food");
                            List<orderGoodsItem> goodsArray = orderDB.getGoodsList();
                            List<orderGoodsItem> goodsArray_PartRefund = new ArrayList<orderGoodsItem>();
                            boolean IsExistPartRefundGoods = false; // 检查是不是已经添加过部分退单商品了
                            int partRefundGoodsItem = 999;
                            if (goodsArray != null&&goodsArray.size()>0)
                            {
                                partRefundGoodsItem = goodsArray.size() + 1;
                                for (int j = goodsArray.size() - 1; j >= 0; j--)
                                {
                                    //JSONObject oldObj = goodsArray.getJSONObject(j);
                                    orderGoodsItem  oldObj =goodsArray.get(j);
                                    //String qty_str = oldObj.getString("qty").toString();
                                    double qty = oldObj.getQty();
                                    
                                    
                                    if (qty < 0)
                                    {
                                        IsExistPartRefundGoods = true;
                                    }
                                    else
                                    {
                                        goodsArray_PartRefund.add(oldObj);
                                        
                                    }
                                    
                                }
                                
                            }
                            // 循环部分退款的商品，添加到之前的商品（数量为负，金额为负）
                            if (IsExistPartRefundGoods == false)
                            {
                                for (int i = 0; i < partRefundGoodsArray.length(); i++)
                                {
                                    try {
                                        
                                        orderGoodsItem goodsItem = new orderGoodsItem();
                                        goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                                        goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                                        
                                        JSONObject job = partRefundGoodsArray.getJSONObject(i);
                                        
                                        String app_food_code = job.optString("app_food_code");// APP方菜品id
                                        String food_name = job.optString("food_name");// 菜品名称
                                        String sku_id = job.optString("sku_id","");// sku编码
                                        String quantity_str = job.optString("count");// 部分退单商品数量
                                        // 转成负数
                                        String price_str = job.optString("refund_price");// 退款的商品单价，此字段默认为活动折扣后价格
                                        String unit = "";// 单位
                                        if (!job.isNull("unit")) {
                                            unit = job.optString("unit");
                                        }
                                        if (unit == null || unit.isEmpty()) {
                                            unit = "份";// 默认个
                                        }
                                        // String food_discount =
                                        // job.get("food_discount").toString();//商品折扣，默认为1，仅美团商家可设置
                                        String attr = "";// 菜品属性 "中辣,微甜"
                                        if (!job.isNull("food_property")) {
                                            attr = job.optString("food_property");
                                        }
                                        String spec = "";// 菜品规格名称，
										/*try {
											spec = job.get("spec").toString();// 菜品规格名称
										} catch (Exception e) {
											spec = "";
										}*/
                                        String cart_id = "1号口袋";//默认，可以循环比较，没必要。
                                        
                                        double price = 0;
                                        double quantity = 0; // 部分退单的商品数量为负
                                        try {
                                            price = Double.parseDouble(price_str);
                                        } catch (Exception e) {
                                            price = 0;
                                        }
                                        try {
                                            quantity = 0 - Double.parseDouble(quantity_str);
                                        } catch (Exception e) {
                                            quantity = 0;
                                        }
                                        
                                        double amt = price * quantity;
                                        
                                        // 计算餐盒 包装费
                                        String box_price_str = job.optString("box_price");// 餐盒价格
                                        String box_num_str = job.optString("box_num");// 餐盒数量
                                        // 部分退单的商品数量为负
                                        double box_price = 0;
                                        double box_num = 0;// 部分退单的商品数量为负
                                        
                                        try {
                                            box_price = Double.parseDouble(box_price_str);
                                        } catch (Exception e) {
                                            box_price = 0;
                                        }
                                        try {
                                            box_num = 0 - Math.ceil(Double.parseDouble(box_num_str));
                                        } catch (Exception e) {
                                            box_num = 0;
                                        }
                                        
                                        goodsItem.setItem(partRefundGoodsItem+"");
                                        goodsItem.setPluNo(sku_id);
                                        goodsItem.setPluBarcode(sku_id);
                                        goodsItem.setSkuId(sku_id);
                                        goodsItem.setPluName(food_name);
                                        goodsItem.setSpecName(spec);
                                        goodsItem.setAttrName(attr);
                                        goodsItem.setFeatureNo("");
                                        goodsItem.setFeatureName("");
                                        goodsItem.setsUnit(unit);
                                        goodsItem.setPrice(price);
                                        goodsItem.setOldPrice(price);
                                        goodsItem.setQty(quantity);
                                        goodsItem.setAmt(amt);
                                        goodsItem.setOldAmt(amt);
                                        goodsItem.setDisc(0);
                                        goodsItem.setBoxNum(box_num);
                                        goodsItem.setBoxPrice(box_price);
                                        goodsItem.setsUnitName(unit);
                                        goodsItem.setGoodsGroup(cart_id);
                                        goodsItem.setIsMemo("N");
                                        partRefundGoodsItem++;
                                        goodsArray_PartRefund.add(goodsItem);
                                        
                                    }
                                    catch (Exception e)
                                    {
                                        writelog_waimai("解析MT部分退款food节点点失败：" + e.getMessage());
                                        continue;
                                    }
                                    
                                }
                                
                                orderDB.setGoodsList(goodsArray_PartRefund);
                            }
                            
                            
                        }
                        catch (Exception e)
                        {
                            
                            writelog_waimai("添加MT部分退款food节点点失败：" + e.getMessage());
                        }
                        
                    }
                    
                    ParseJson pj = new ParseJson();
                    String Response_json = pj.beanToJson(orderDB);;
                    try
                    {
                        boolean IsUpdateRedis = true;//是否更新缓存。
                        RedisPosPub redis = new RedisPosPub();
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey)
                        {
                            //如果存在看下缓存里面状态是不是 已经是退单成功状态
                            String redis_order = redis.getHashMap(redis_key, hash_key);
                            try
                            {
                                JSONObject redis_order_obj = new JSONObject(redis_order);
                                String	status_redis =  redis_order_obj.optString("status");
                                String	refundStatus_redis =  redis_order_obj.optString("refundStatus");
                                if(refundStatus.equals("2"))
                                {
                                    if(status_redis.equals("12"))//缓存里面已经是退成功状态
                                    {
                                        IsUpdateRedis = false;
                                    }
                                }
                                else	if(refundStatus.equals("7"))
                                {
                                    if(refundStatus_redis.equals("10"))////缓存里面已经是退成功状态
                                    {
                                        IsUpdateRedis = false;
                                    }
                                }
                                
                            }
                            catch (Exception e)
                            {
                            }
                            
                            
                            if(IsUpdateRedis)
                            {
                                redis.DeleteHkey(redis_key, hash_key);
                                writelog_waimai(
                                        "【MT删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
                            
                        }
                        
                        //这里对比下数据库状态
                        //MT可能先推送退单成功状态，再推送申请退单状态
                        if(refundStatus.equals("2"))
                        {
                            if(status_db.equals("12"))
                            {
                                IsUpdateRedis = false;
                            }
                        }
                        else if(refundStatus.equals("7"))
                        {
                            if(refundStatus_db.equals("10"))
                            {
                                IsUpdateRedis = false;
                            }
                        }
                        
                        
                        if(IsUpdateRedis)
                        {
                            
                            writelog_waimai("【MT订单退款开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                    + " hash_value:" + Response_json);
                            boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                            if (nret) {
                                HelpTools.writelog_waimai(
                                        "【MT订单退款写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            } else {
                                HelpTools.writelog_waimai(
                                        "【MT订单退款写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
                            
                        }
                        else
                        {
                            writelog_waimai("【MT订单退款开始写缓存】【无需写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                    + " hash_value:" + Response_json+" 数据库中订单status="+status_db+" refundStatus="+refundStatus_db);
                            
                        }
                        
                        
                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai("更新缓存中订单退款消息异常！" + e.getMessage());
                    }
                    
                    return Response_json;
                    
                    
                } catch (Exception e) {
                    writelog_waimai("解析聚宝盆发送部分退款消息格式有误！" + e.getMessage());
                    return null;
                }
                
            } catch (Exception e) {
                writelog_waimai("解析聚宝盆发送部分退款消息请求格式有误！" + e.getMessage());
                return null;
            }
            // endregion
            
        } else {
            writelog_waimai("解析聚宝盆发送部分退款消息类型有误！");
            return null;
        }
    }
    
    public static String GetJBPTokenResponse(String responseStr) throws Exception {
        if (responseStr == null || responseStr.length() == 0) {
            // writelog_waimaiException("美团外卖发送的请求为空！");
            return null;
        }
        String logFileName = "ShopsSaveLocal";
        writelog_fileName("【聚宝盆门店绑定后Token回传消息转码前】" + responseStr,logFileName);
        
        String[] MTResquest = responseStr.split("&");
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_fileName("聚宝盆门店绑定后Token回传消息发送的请求格式有误！",logFileName);
            return null;
        }
        
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        String urlDecodeString = "";
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                String s2_decode = getURLDecoderString(s2);// 二次转码（获取为中文）
                //writelog_waimai("【聚宝盆门店绑定后Token回传消息转码前转码后】"+s1+"=" + s2_decode);
                map_MTResquest.put(s1, s2_decode);
                urlDecodeString +=s1+"="+s2_decode+"&";
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        writelog_fileName("【聚宝盆门店绑定后Token回传消息转码后】" + urlDecodeString,logFileName);
        try {
            boolean isDigiwinISV = false;
            String developerId = map_MTResquest.getOrDefault("developerId","");//开发者id
            String SignKey = "";
            if ("100146".equals(developerId))
            {
                isDigiwinISV = true;
                SignKey = "jevw2dkj37mb8pun";
            }
            String appAuthToken = map_MTResquest.getOrDefault("appAuthToken","");// 门店绑定的授权token，将来的门店业务操作必须要传
            String ePoiId = map_MTResquest.getOrDefault("ePoiId","");// 门店绑定时，传入的ERP厂商分配给门店的唯一标识 // 99_10001
            String poiId = map_MTResquest.getOrDefault("poiId","");// 美团门店id
            String poiName = map_MTResquest.getOrDefault("poiName","");// 美团门店名称
            String shopno_poi = ePoiId;//后面解析用
            String businessId = map_MTResquest.getOrDefault("businessId","");// 1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
            String eId = "99";
            String erpShopNo = ePoiId;
            String channelId = orderLoadDocType.MEITUAN+"001";//默认渠道ID
            String customerNo = " ";//主键不能为空
            if (!"2".equals(businessId))
            {
                writelog_fileName("【聚宝盆门店绑定后Token回传消息】businessId="+businessId+"，非外卖门店绑定无需保存到本地",logFileName);
                return null;
            }
            
            
            
            try {
                
                if (isDigiwinISV)
                {
                    int indexofSpec = shopno_poi.indexOf("_");//客户编码_企业编码_门店编码
                    String s1 = shopno_poi.substring(0, indexofSpec);
                    String s2 = shopno_poi.substring(indexofSpec + 1, shopno_poi.length());//企业编码_门店编码
                    if(indexofSpec>0)
                    {
                        customerNo = s1;
                        int indexofSpec_new = s2.indexOf("_");//99_LS_1001这种兼容下
                        String s1_new = s2.substring(0, indexofSpec_new);
                        String s2_new = s2.substring(indexofSpec_new + 1, s2.length());
                        if(indexofSpec_new>0)
                        {
                            eId = s1_new;
                            erpShopNo = s2_new;
                        }
                        else
                        {
                            erpShopNo = shopno_poi;
                        }
                    }
                    else
                    {
                        erpShopNo = shopno_poi;
                    }
                }
                else
                {
                    //非鼎捷的服务商开发这id,兼容下，比如味多美万一升级3.0呢
                    int indexofSpec = shopno_poi.indexOf("_");//企业编码_门店编码
                    String s1 = shopno_poi.substring(0, indexofSpec);
                    String s2 = shopno_poi.substring(indexofSpec + 1, shopno_poi.length());
                    if(indexofSpec>0)
                    {
                        eId = s1;
                        erpShopNo = s2;
                    }
                    else
                    {
                        erpShopNo = shopno_poi;
                    }
                }
                
                
                
            } catch (Exception e) {
            
            }
            String Response_json = "";
            JSONObject obj = new JSONObject();
            obj.put("customerNo", customerNo);
            obj.put("channelId", channelId);
            obj.put("orderShopNo", poiId);
            obj.put("orderShopName", poiName);
            obj.put("erpShopNo", erpShopNo);
            obj.put("erpShopName", "");
            obj.put("appAuthToken", appAuthToken);
            obj.put("eId", eId);
            obj.put("businessId", businessId);// 美团聚宝盆才有 默认2代表外卖
            obj.put("appKey", developerId);
            obj.put("appName", "");
            if ("1".equals(businessId))
            {
                obj.put("appName", "美团团购(服务商)");
            }
            else if ("2".equals(businessId))
            {
                obj.put("appName", "美团外卖(服务商)");
            }
            else if ("3".equals(businessId))
            {
                obj.put("appName", "美团闪惠(服务商)");
            }
            else
            {
            
            }
            obj.put("appSecret", SignKey);
            obj.put("isTest", "N");
            obj.put("isJbp", "Y");
            obj.put("mappingShopNo", ePoiId);//真正的唯一标识
            Response_json = obj.toString();
            
            // Token存缓存
            String redis_key = orderRedisKeyInfo.redisKey_jbpMappingshop;
            String hash_key = ePoiId;
            writelog_fileName("【开始写缓存JBP_MappingShop门店映射】" + "redis_key:" + redis_key + ",hash_key:" + hash_key
                    + ",hash_value:" + Response_json,logFileName);
            try {
                
                RedisPosPub redis = new RedisPosPub();
                boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                if (nret) {
                    writelog_fileName("【写缓存】OK" + ",redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
                } else {
                    writelog_fileName("【写缓存】Error" + ",redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
                }
                //redis.Close();
                
            } catch (Exception e) {
                writelog_fileName(
                        "【写缓存】Exception:" + e.getMessage() + ",redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
            }
            
            return Response_json;
            
        } catch (Exception e) {
            writelog_fileName("聚宝盆门店绑定后Token回传消息发送的请求格式有误！",logFileName);
            return null;
        }
    }
    
    public static String GetJBPTokenReleaseBindingResponse(String responseStr) throws Exception {
        if (responseStr == null || responseStr.length() == 0) {
            // writelog_waimaiException("美团外卖发送的请求为空！");
            return null;
        }
        String logFileName = "ShopsSaveLocal";
        writelog_fileName("【聚宝盆门店解绑后回传消息转码前】" + responseStr,logFileName);
        
        String[] MTResquest = responseStr.split("&");
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_fileName("聚宝盆门店解绑后回传消息发送的请求格式有误！",logFileName);
            return null;
        }
        
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        String urlDecodeString = "";
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                String s2_decode = getURLDecoderString(s2);// 二次转码（获取为中文）
                //writelog_waimai("【聚宝盆门店解绑后回传消息转码前转码后2】" +s1+"="+ s2_decode);
                
                map_MTResquest.put(s1, s2_decode);
                urlDecodeString +=s1+"="+s2_decode+"&";
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        writelog_fileName("【聚宝盆门店解绑后回传消息转码后】" + urlDecodeString,logFileName);
        try {
            boolean isDigiwinISV = false;
            String developerId = map_MTResquest.getOrDefault("developerId","");//开发者id
            String SignKey = "";
            if ("100146".equals(developerId))
            {
                isDigiwinISV = true;
                SignKey = "jevw2dkj37mb8pun";
            }
            String appAuthToken = "";
            String ePoiId = map_MTResquest.get("ePoiId").toString();// 门店绑定时，传入的ERP厂商分配给门店的唯一标识
            String poiId = "";
            String poiName = "";// map_MTResquest.get("poiName");//美团门店名称
            String shopno_poi = ePoiId;//后面解析用
            String businessId = map_MTResquest.getOrDefault("businessId","");// 1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
            String eId = "99";
            String erpShopNo = ePoiId;
            String channelId = orderLoadDocType.MEITUAN+"001";//默认渠道ID
            String customerNo = " ";//主键不能为空
            if (!"2".equals(businessId))
            {
                writelog_fileName("【聚宝盆门店解绑后回传消息】businessId="+businessId+"，非外卖门店绑定无需保存到本地",logFileName);
                return null;
            }
            
            try {
                
                if (isDigiwinISV)
                {
                    int indexofSpec = shopno_poi.indexOf("_");//客户编码_企业编码_门店编码
                    String s1 = shopno_poi.substring(0, indexofSpec);
                    String s2 = shopno_poi.substring(indexofSpec + 1, shopno_poi.length());//企业编码_门店编码
                    if(indexofSpec>0)
                    {
                        customerNo = s1;
                        int indexofSpec_new = s2.indexOf("_");//99_LS_1001这种兼容下
                        String s1_new = s2.substring(0, indexofSpec_new);
                        String s2_new = s2.substring(indexofSpec_new + 1, s2.length());
                        if(indexofSpec_new>0)
                        {
                            eId = s1_new;
                            erpShopNo = s2_new;
                        }
                        else
                        {
                            erpShopNo = shopno_poi;
                        }
                    }
                    else
                    {
                        erpShopNo = shopno_poi;
                    }
                }
                else
                {
                    //非鼎捷的服务商开发这id,兼容下，比如味多美万一升级3.0呢
                    int indexofSpec = shopno_poi.indexOf("_");//企业编码_门店编码
                    String s1 = shopno_poi.substring(0, indexofSpec);
                    String s2 = shopno_poi.substring(indexofSpec + 1, shopno_poi.length());
                    if(indexofSpec>0)
                    {
                        eId = s1;
                        erpShopNo = s2;
                    }
                    else
                    {
                        erpShopNo = shopno_poi;
                    }
                }
                
                
                
            } catch (Exception e) {
            
            }
            String Response_json = "";
            JSONObject obj = new JSONObject();
            obj.put("customerNo", customerNo);
            obj.put("channelId", channelId);
            obj.put("orderShopNo", poiId);
            obj.put("orderShopName", poiName);
            obj.put("erpShopNo", erpShopNo);
            obj.put("erpShopName", "");
            obj.put("appAuthToken", appAuthToken);
            obj.put("eId", eId);
            obj.put("businessId", businessId);// 美团聚宝盆才有 默认2代表外卖
            obj.put("appKey", developerId);
            obj.put("appName", "");
            if ("1".equals(businessId))
            {
                obj.put("appName", "美团团购(服务商)");
            }
            else if ("2".equals(businessId))
            {
                obj.put("appName", "美团外卖(服务商)");
            }
            else if ("3".equals(businessId))
            {
                obj.put("appName", "美团闪惠(服务商)");
            }
            else
            {
            
            }
            obj.put("appSecret", SignKey);
            obj.put("isTest", "N");
            obj.put("isJbp", "Y");
            obj.put("mappingShopNo", ePoiId);//真正的唯一标识
            Response_json = obj.toString();
            
            // Token存缓存
            String redis_key = orderRedisKeyInfo.redisKey_jbpMappingshop;
            String hash_key = ePoiId;
            writelog_fileName("【解绑后开始删除缓存JBP_MappingShop门店映射】" + "redis_key:" + redis_key + ",hash_key:" + hash_key
                    + ",hash_value:" + Response_json,logFileName);
            try {
                RedisPosPub redis = new RedisPosPub();
                boolean nret = redis.DeleteHkey(redis_key, hash_key);
                if (nret) {
                    writelog_fileName("【删除缓存】OK" + ",redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
                } else {
                    writelog_fileName("【删除缓存】Error" + ",redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
                }
                //redis.Close();
                if (jbpMappingShopList!=null&&!jbpMappingShopList.isEmpty())
                {
                    jbpMappingShopList.remove(ePoiId);
                }
                
            } catch (Exception e) {
                writelog_fileName(
                        "【删除缓存】Exception:" + e.getMessage() + ",redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
            }
            
            return Response_json;
            
        } catch (Exception e) {
            writelog_fileName("聚宝盆门店解绑后回传消息发送的请求格式有误！",logFileName);
            return null;
        }
    }
    
    // 产生UUID
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }
    
    
    public static String GetOrderByBuffer(String reqjson) throws Exception {
        String resjson = "";
        // writelog_waimai("【获取订单】请求："+reqjson);
        try {/*
				JSONObject obj = new JSONObject(reqjson);
				String companyno = obj.get("o_companyNO").toString();
				String shopno = obj.get("o_shopNO").toString();

				RedisPosPub redis = new RedisPosPub();
				String redis_key = "WMORDER" + "_" + companyno + "_" + shopno;

				// String redis_key = "WMORDER_99_10001";

				Map<String, String> ordermap = redis.getALLHashMap(redis_key);
				//redis.Close();
				OrderGetRes res = new OrderGetRes();
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				res.setSuccess(true);
				res.setDatas(new ArrayList<OrderGetRes.level1Elm>());
				ParseJson pj = new ParseJson();
				for (Map.Entry<String, String> entry : ordermap.entrySet())
				{
					if (entry.getValue() != null) {
						try {
							String orderJson = entry.getValue();
							OrderGetRes.level1Elm orderModel = pj.jsonToBean(orderJson,
									new TypeToken<OrderGetRes.level1Elm>() {
									});
							// orderList.add(orderModel);
							if(orderModel!=null&&orderModel.getLoadDocType()!=null)
							{
								if(orderModel.getLoadDocType().equals("1")||orderModel.getLoadDocType().equals("2")||orderModel.getLoadDocType().equals("8"))
								{
									if(orderModel.getShopNO().equals(shopno)==false)
									{
										writelog_fileName("【获取缓存订单】【外卖类型(1.2.8)订单】【请求门店和实际返回门店不一致】单号orderNO="+orderModel.getOrderNO()+" Redis主键："+redis_key+" 请求req："+reqjson+" 返回res："+orderJson,"waimailogEx");
										continue;
									}
								}
								else
								{

									boolean flag = false;
									if(orderModel.getShopNO()!=null&&orderModel.getShopNO().equals(shopno))
									{
										flag = true;
									}
									if(orderModel.getMachShopNO()!=null&&orderModel.getMachShopNO().equals(shopno))
									{
										flag = true;
									}
									if(orderModel.getShippingShopNO()!=null&&orderModel.getShippingShopNO().equals(shopno))
									{
										flag = true;
									}

									if(flag)
									{

									}
									else
									{
										writelog_fileName("【获取缓存订单】【其他类型订单】【请求门店和实际返回门店(下单、生产、配送)不一致】单号orderNO="+orderModel.getOrderNO()+" Redis主键："+redis_key+" 请求req："+reqjson+" 返回res："+orderJson,"waimailogEx");
										continue;

									}

								}

							}
							res.getDatas().add(orderModel);

						} catch (Exception e) {
							writelog_fileName("【获取缓存订单】【异常】"+e.getMessage()+" 异常订单json:"+entry.getValue()+" Redis主键："+redis_key+" 请求req："+reqjson,"waimailogEx");
							continue;

						}

					}
				}

				String Response_json = pj.beanToJson(res);

				pj=null;
				// writelog_waimai("【获取订单】Redis主键："+redis_key); //数据太多了。
				// writelog_fileName("【获取订单】Redis主键："+redis_key+"
				// 返回:"+Response_json, "wangyangqtest");
				return Response_json;

			 */}
        
        catch (Exception e) {
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("success", false);
            jsonobj.put("serviceStatus", "200");
            jsonobj.put("serviceDescription", "服务执行异常：" + e.getMessage());
            resjson = jsonobj.toString();
            writelog_waimaiException("【获取订单异常】请求：" + reqjson);
            writelog_waimaiException("【获取订单异常】返回：" + resjson);
            return resjson;
        }
        return resjson;
    }
    
    
    public static InsBean  InsertOrderStatusLog(orderStatusLog item)
    {
        InsBean ib1=null;
        try
        {
            String orderNo = item.getOrderNo();
            if (orderNo == null || orderNo.length() == 0)
            {
                return null;
            }
            String eId = item.geteId();
            if (eId==null||eId.isEmpty())
            {
                return null;
            }
            String shopNo = item.getShopNo();
            
            String loadDocType = item.getLoadDocType();
            
            String chanleId = item.getChannelId();
            
            String loadDocBillType = item.getLoadDocBillType();
            String loadDocOrderNo = item.getLoadDocOrderNo();
            
            if(loadDocOrderNo==null||loadDocOrderNo.isEmpty())
            {
                loadDocOrderNo = orderNo;
            }
            
            String statusType = item.getStatusType();
            if (statusType == null || statusType.length() == 0)
            {
                return null;
            }
            String statusTypeName = item.getStatusTypeName();
            
            String status = item.getStatus();
            if (status == null || status.length() == 0)
            {
                return null;
            }
            
            String statusName = item.getStatusName();
            
            String update_time = item.getUpdate_time();
            if (update_time == null || update_time.length() == 0)
            {
                update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            }
            
            String need_notify = item.getNeed_notify();
            if (need_notify==null||need_notify.isEmpty())
            {
                need_notify = "N";
            }
            
            String notify_status = item.getNotify_status();
            if (notify_status==null||notify_status.isEmpty())
            {
                notify_status = "";
            }
            
            if (need_notify.equals("Y")) {
                notify_status = "0";
            }
            
            String need_callback = item.getNeed_callback();
            if (need_callback==null||need_callback.isEmpty())
            {
                need_callback = "N";
            }
            
            String callback_status = item.getCallback_status();
            if (callback_status==null||callback_status.isEmpty())
            {
                callback_status = "";
            }
            if (need_callback.equals("Y")) {
                callback_status = "0";
            }
            
            String memo = item.getMemo();
            String o_opNO = item.getOpNo();
            String o_opName = item.getOpName();
            String machShopNO = item.getMachShopNo();
            String shippingShopNO = item.getShippingShopNo();
            
            //1:对外给买家看的 否则写0
            String display = item.getDisplay();
            if (display==null||display.isEmpty())
            {
                display = "0";
            }
            //订单 调度、开立 状态不显示给买家
            if (status.equals("0") ||status.equals("1"))
            {
                display = "0";
            }
            
            String[] columns1 = { "EID",   "ORDERNO", "LOADDOCTYPE","CHANNELID","LOADDOCBILLTYPE","LOADDOCORDERNO",
                    "STATUSTYPE", "STATUSTYPENAME", "STATUS", "STATUSNAME",
                    "OPNO", "OPNAME", "UPDATE_TIME", "MEMO","DISPLAY" };
            
            DataValue[] insValue1 = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(loadDocType, Types.VARCHAR),
                            new DataValue(chanleId, Types.VARCHAR),
                            new DataValue(loadDocBillType, Types.VARCHAR),
                            new DataValue(loadDocOrderNo, Types.VARCHAR),
                            new DataValue(statusType, Types.VARCHAR), // 状态类型
                            new DataValue(statusTypeName, Types.VARCHAR), // 状态类型名称
                            new DataValue(status, Types.VARCHAR), // 状态
                            new DataValue(statusName, Types.VARCHAR), // 状态名称
                            new DataValue(o_opNO, Types.VARCHAR), //
                            new DataValue(o_opName, Types.VARCHAR), //
                            new DataValue(update_time, Types.VARCHAR), //
                            new DataValue(memo, Types.VARCHAR), //
                            new DataValue(display, Types.VARCHAR), //
                    };
            
            ib1 = new InsBean("DCP_ORDER_STATUSLOG", columns1);
            ib1.addValues(insValue1);
        }
        catch (Exception e)
        {
            return null;
        }
        
        return ib1;
    }
    
    /**
     * 根据状态类型&状态获取状态名称
     *
     * @param statusType
     * @param status
     * @return
     * @throws Exception
     */
    public static String GetOrderStatusName(String statusType, String status, StringBuilder statusTypeName)
            throws Exception {
        String statusName = "";
        String typeName = "";
        if (statusTypeName == null) {
            statusTypeName = new StringBuilder();
        }
        
        try
        {
            switch (statusType)
            {
                // region 订单状态类型
                case "1":
                    typeName = "订单状态";
                    statusTypeName.append(typeName);
                    switch (status)
                    {
                        case "0":
                            statusName = "待调度";
                            break;
                        case "1":
                            statusName = "开立";
                            break;
                        case "2":
                            statusName = "已接单";
                            break;
                        case "3":
                            statusName = "已拒单/已取消";
                            break;
                        case "4":
                            statusName = "生产接单";
                            break;
                        case "5":
                            statusName = "生产拒单";
                            break;
                        case "6":
                            statusName = "生产完成";
                            break;
                        case "7":
                            statusName = "门店调拨";
                            break;
                        case "8":
                            statusName = "待提货";
                        case "9":
                            statusName = "待配送";
                            break;
                        case "10":
                            statusName = "已发货";
                            break;
                        case "11":
                            statusName = "订单完成";
                            break;
                        case "12":
                            statusName = "已退单";
                            break;
                        case "13":
                            statusName = "已点货";
                            break;
                        case "14":
                            statusName = "开始制作";
                        case "101"://这个状态是用在写日志的,不是订单状态
                            statusName = "部分提货";
                            break;
                        default:
                            statusName = "(status=" + status + ")";
                            break;
                    }
                    
                    break;
                // endregion
                
                // region 配送状态类型
                case "2":
                    typeName = "配送状态";
                    statusTypeName.append(typeName);
                    switch (status) {
                        case "-3":
                            statusName = "退货入库";
                            break;
                        case "-2":
                            statusName = "取消配送";
                            break;
                        case "-1":
                            statusName = "物流预下单";
                            break;
                        case "0":
                            statusName = "物流已下单";
                            break;
                        case "1":
                            statusName = "物流已接单";
                            break;
                        case "2":
                            statusName = "物流已取件";
                            break;
                        case "3":
                            statusName = "用户签收";
                            break;
                        case "4":
                            statusName = "物流异常或取消";
                            break;
                        case "5":
                            statusName = "手动撤销";
                            break;
                        case "6":
                            statusName = "配送员到店";
                            break;
                        case "7":
                            statusName = "物流重下单";
                            break;
                        case "8":
                            statusName = "货到物流中心";
                            break;
                        case "9":
                            statusName = "消费者七天未取件";
                            break;
                        default:
                            statusName = "(" + status + ")";
                            break;
                    }
                    
                    break;
                // endregion
                
                // region 退单状态类型
                case "3":
                    typeName = "退单状态";
                    statusTypeName.append(typeName);
                    switch (status) {
                        case "1":
                            statusName = "未申请";
                            break;
                        case "2":
                            statusName = "用户申请退单";
                            break;
                        case "3":
                            statusName = "已拒绝";
                            break;
                        case "4":
                            statusName = "客服仲裁中";
                            break;
                        case "5":
                            statusName = "退单失败";
                            break;
                        case "6":
                            statusName = "已退单成功";
                            break;
                        case "7":
                            statusName = "用户申请部分退款";
                            break;
                        case "8":
                            statusName = "已拒绝部分退款";
                            break;
                        case "9":
                            statusName = "部分退款失败";
                            break;
                        case "10":
                            statusName = "部分退款成功";
                            break;
                        default:
                            statusName = "(" + status + ")";
                            break;
                    }
                    
                    break;
                // endregion
                
                // region 其他状态类型
                case "4":
                    typeName = "生产状态";
                    statusTypeName.append(typeName);
                    switch (status)
                    {
                        case "4":
                            statusName = "生产接单";
                            break;
                        case "5":
                            statusName = "生产拒单";
                            break;
                        case "6":
                            statusName = "完工入库";
                            break;
                        case "7":
                            statusName = "内部调拨";
                            break;
                        default:
                            statusName = "(" + status + ")";
                            break;
                    }
                    
                    break;
                // endregion
                default:
                    typeName = "状态类型(statusType=" + statusType + ")";
                    statusName = "状态(status=" + status + ")";
                    statusTypeName.append(typeName);
                    break;
            }
            
        } catch (Exception e) {
        
        }
        
        return statusName;
    }
    
    public static String GetMicroMarketPayName(String payCode) throws Exception {
        String payName = "";
        switch (payCode)
        {
            case "#P1":
                payName = "微信";
                break;
            case "#P2":
                payName = "支付宝";
                break;
            case "#03":
                payName = "会员卡";
                break;
            case "#04":
                payName = "券";
                break;
            case "#05":
                payName = "积分";
                break;
            case "codPay":
                payName = "货到付款";
                break;
            
            default:
                payName = "未知支付方式";
                break;
        }
        return payName;
        
    }
    
    public static String GetJBPShippingResponse(String responseStr) throws Exception {
        if (responseStr == null || responseStr.length() == 0) {
            return null;
        }
        //writelog_fileName("【聚宝盆配送消息URL转码前】"+responseStr,"MTShippingRequsetLog");
        String[] MTResquest = responseStr.split("&");//
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_waimaiException("解析聚宝盆发送的请求格式有误！");
            return null;
        }
        String urlDecodeString = "";
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                /*
                 * String[] ss = string_mt.split("="); //包含多个=会有问题
                 * map_MTResquest.put(ss[0], ss[1]);
                 */
                String responseStr_2 = getURLDecoderString(s2);//
                responseStr_2 = getURLDecoderString(responseStr_2);//二次转码，煞笔一样有的需要转码2次，这里需要2次转码 才能得到中文
                
                //writelog_waimai("【聚宝盆配送消息URL转码后2】" +s1+"="+ responseStr_2);
                map_MTResquest.put(s1, responseStr_2);
                urlDecodeString +=s1+"="+responseStr_2+"&";
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        writelog_waimai("【聚宝盆配送消息URL转码后2】" +urlDecodeString);
        
        String ePoiId = map_MTResquest.get("ePoiId");
        
        String shippingStatus = map_MTResquest.get("shippingStatus");
        
        JSONObject obj = new JSONObject(shippingStatus);
        String order_id = obj.get("orderId").toString();
        String logistics_status = obj.get("shippingStatus").toString();
        String time = obj.optString("time");
        String dispatcher_name = obj.optString("dispatcherName","");
        String dispatcher_mobile = obj.optString("dispatcherMobile","");
        
        JSONObject obj_MT = new JSONObject();
        
        obj_MT.put("order_id", order_id);
        obj_MT.put("logistics_status", logistics_status);
        obj_MT.put("dispatcher_name", dispatcher_name);
        obj_MT.put("dispatcher_mobile", dispatcher_mobile);
        obj_MT.put("time", time);
        String Response_json = obj_MT.toString();
        return Response_json;
        
    }
    
    public static String GetMTShippingResponse(String responseStr) throws Exception {
        if (responseStr == null || responseStr.length() == 0) {
            return null;
        }
        writelog_fileName("【美团URL转码前】【配送消息】"+responseStr,"MTRequsetLog");
        String[] MTResquest = responseStr.split("&");//
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_waimaiException("解析MT配送状态消息格式有误！");
            return null;
        }
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        String urlDecodeString ="";
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                /*
                 * String[] ss = string_mt.split("="); //包含多个=会有问题
                 * map_MTResquest.put(ss[0], ss[1]);
                 */
                s2 = getURLDecoderString(s2);// 二次转码（获取为
                s2 = getURLDecoderString(s2);
                
                urlDecodeString +=s1+"="+s2+"&";//记日志
                map_MTResquest.put(s1, s2);
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        
        writelog_waimai("【美团URL转码后2】【配送消息】"+urlDecodeString);
        
        String companyno = "99";
        String erpshopno = " ";
        
        String order_id = map_MTResquest.get("order_id").toString();
        String logistics_status = map_MTResquest.get("logistics_status").toString();
        String time = map_MTResquest.get("time").toString();
        String dispatcher_name = map_MTResquest.get("dispatcher_name").toString();
        String dispatcher_mobile = map_MTResquest.get("dispatcher_mobile").toString();
        
        JSONObject obj = new JSONObject();
        
        obj.put("order_id", order_id);
        obj.put("logistics_status", logistics_status);
        obj.put("dispatcher_name", dispatcher_name);
        obj.put("dispatcher_mobile", dispatcher_mobile);
        String Response_json = obj.toString();
        return Response_json;
        
    }
    
    
    
    public static String GetHHSResponse(String responseStr,List<Map<String, Object>> mapPaylist) throws Exception
    {
        if (responseStr == null || responseStr.length() == 0)
        {
            return null;
        }
        
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String createDate = df.format(cal.getTime());
        
        df = new SimpleDateFormat("HHmmss");
        String createTime = df.format(cal.getTime());
        
        //转义
        responseStr = responseStr.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
        responseStr = responseStr.replaceAll("\\+", "%2B");  //+
        
        responseStr = URLDecoder.decode(responseStr, "UTF-8");
        
        //================================后续需处理
        String sEId="99";
        String sShopno="01";
        
        //订单类型标记 1:订单 2:重新下单 3:取消订单
        int orderDeliverooType=0;
        
        JSONObject res=new JSONObject(responseStr);
        
        //订单
        if(res.has("order"))
        {
            // (企业编号_门店编号
            // 99_10001)
            JSONObject jsonobjresponse = new JSONObject();
            
            //
            JSONObject resOrder=res.getJSONObject("order");
            
            //重新下单的原单金额
            int sOrder_cost=0;
            //定义责任方  restaurant 订单金额total_price为0 否则就是原价
            String sFault="";
            
            if(resOrder.has("remake_details"))//重新下单(仅仅针对外卖平台的骑手)
            {
                //订单类型标记 1:订单 2:重新下单 3:取消订单
                orderDeliverooType=2;
                //重新下单
                JSONObject resRemake_details=resOrder.getJSONObject("remake_details");
                String sParent_order_id=resRemake_details.getString("parent_order_id");//原单号
                sFault=resRemake_details.getString("fault");//定义责任方  restaurant 订单金额total_price为0 否则就是原价
                sOrder_cost=resRemake_details.getInt("order_cost");//原价
                
                jsonobjresponse.put("headOrderno", sParent_order_id);
                jsonobjresponse.put("proMemo", sFault+"@@@"+sOrder_cost);//借用生产备注字段
                //重新下单后面有处理金额，如果餐馆原因订单金额是0,都算到折扣去
                
            }
            
            String sEvent=res.getString("event");
            if (sEvent.equals("cancel_order")) //取消订单
            {
                //订单类型标记 1:订单 2:重新下单 3:取消订单
                orderDeliverooType=3;
                
                String sCancelled_at=res.getString("cancelled_at");//取消时间 2019-06-15T16:30:00Z
                
                SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date dLasttime = sdfA.parse(sCancelled_at);
                //取消日期
                sdfA=new SimpleDateFormat("yyyyMMdd");
                String StartDateA=sdfA.format(dLasttime);
                //System.out.println(StartDateA);
                //取消时间
                sdfA=new SimpleDateFormat("HHmmss");
                String StartTimeA=sdfA.format(dLasttime);
                //System.out.println(StartTimeA);
                
                String sLocation_id=res.getString("location_id");//POS location id
                
                //订单信息
                String sId=resOrder.getString("id");//订单号
                String sDisplay_id=resOrder.getString("display_id");//流水号
                String sPickup_at=resOrder.getString("pickup_at");//骑手接单时间
                
                sdfA = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                dLasttime = sdfA.parse(sPickup_at);
                //骑手接单日期
                sdfA=new SimpleDateFormat("yyyyMMdd");
                String StartDateB=sdfA.format(dLasttime);
                //System.out.println(StartDateB);
                //骑手接单时间
                sdfA=new SimpleDateFormat("HHmmss");
                String StartTimeB=sdfA.format(dLasttime);
                //System.out.println(StartTimeB);
                
                String sReason=resOrder.getString("reason");//取消原因
                
                //*******************deliveroo平台取消订单处理**********************
                
                try
                {
                    // 从缓存中取原订单
                    RedisPosPub redis = new RedisPosPub();
                    String redis_key = "WMORDER" + "_" + sEId + "_" + sShopno;//
                    String hash_key = sId;
                    String ordermap = redis.getHashMap(redis_key, hash_key);
                    // 缓存中如果已经删除了
                    if (ordermap == null || ordermap.isEmpty() || ordermap.length() == 0)
                    {
                        order model = new order();
                        model.seteId(sEId);
                        model.setShopNo(sShopno);
                        model.setOrderNo(sId);
                        model.setLoadDocOrderNo(sId);
                        model.setLoadDocType("37");//deliveroo户户送
                        model.setGoodsList(new ArrayList<orderGoodsItem>());
                        ParseJson pj = new ParseJson();
                        ordermap = pj.beanToJson(model);
                        pj=null;
                    }
                    JSONObject obj = new JSONObject(ordermap);
                    String status = "3";//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
                    String refundStatus = "1";//1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功 7.用户申请部分退款 8.拒绝部分退款 9 部分退款失败 10.部分退款成功
                    
                    // 更新订单状态
                    obj.put("status", status);
                    obj.put("refundStatus", refundStatus);
                    obj.put("refundReason", sReason);//
                    
                    String Response_json = obj.toString();
                    boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                    if (isexistHashkey)
                    {
                        redis.DeleteHkey(redis_key, hash_key);//
                        HelpTools.writelog_waimai("【deliveroo户户送删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                    HelpTools.writelog_waimai("【deliveroo户户送开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key	+ " hash_value:" + Response_json);
                    boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                    if (nret)
                    {
                        HelpTools.writelog_waimai("【deliveroo户户送写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                    else
                    {
                        HelpTools.writelog_waimai("【deliveroo户户送写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                    //redis.Close();
                    return Response_json;
                    
                }
                catch (Exception e)
                {
                    HelpTools.writelog_waimai("更新缓存中deliveroo户户送取消消息异常！" + e.getMessage());
                    return null;
                }
                
            }
            else //******************************订单******************************
            {
                //订单类型标记 1:订单 2:重新下单 3:取消订单
                if (orderDeliverooType==0)
                {
                    orderDeliverooType=1;
                }
                
                String sLocation_id=res.getString("location_id");//POS location id
                String sRestaurant_acknowledged_at=res.getString("restaurant_acknowledged_at");//商家接单时间2018-12-04T11:39:41Z
                
                SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date dLasttime = sdfA.parse(sRestaurant_acknowledged_at);
                //商家接单日期
                sdfA=new SimpleDateFormat("yyyyMMdd");
                String sRestaurantStartDate=sdfA.format(dLasttime);
                //System.out.println(sRestaurantStartDate);
                //商家接单时间
                sdfA=new SimpleDateFormat("HHmmss");
                String sRestaurantStartTime=sdfA.format(dLasttime);
                //System.out.println(sRestaurantStartTime);
                
                String sFulfillment_type=resOrder.getString("fulfillment_type");
                
                //订单信息
                String sId=resOrder.getString("id");//订单号
                String sDisplay_id=resOrder.getString("display_id");//流水号
                
                //卖家付款信息
                JSONObject resTotal_price=resOrder.getJSONObject("total_price");
                //台币没有小数点，这里不用处理
                int sFractional=resTotal_price.getInt("fractional");//付款方式的最新单位
                String sCurrency_code=resTotal_price.getString("currency_code");//货币类型代码
                
                boolean bAsap=resOrder.getBoolean("asap");//true是当天订单，false是预定单(也可能明天配送)
                String sNotes=resOrder.getString("notes");//买家备注
                String sPickupStartDate="";//骑手接单日期
                String sPickupStartTime="";//骑手接单时间
                
                //骑手接单时间======平台配送才有此节点
                if (resOrder.has("pickup_at"))
                {
                    String sPickup_at=resOrder.getString("pickup_at");
                    sdfA = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    dLasttime = sdfA.parse(sPickup_at);
                    //骑手接单日期
                    sdfA=new SimpleDateFormat("yyyyMMdd");
                    sPickupStartDate=sdfA.format(dLasttime);
                    //System.out.println(sPickupStartDate);
                    //骑手接单时间
                    sdfA=new SimpleDateFormat("HHmmss");
                    sPickupStartTime=sdfA.format(dLasttime);
                    //System.out.println(sPickupStartTime);
                }
                
                //订单总折扣======重新下单没有此节点信息
                int sDiscFractional=0;
                if (resOrder.has("offer_discount"))
                {
                    JSONObject resOffer_discount=resOrder.getJSONObject("offer_discount");
                    //
                    JSONObject resAmount=resOffer_discount.getJSONObject("amount");
                    //台币没有小数点，这里不用处理
                    sDiscFractional=resAmount.getInt("fractional");//单价的最新单位
                    String sDiscCurrency_code=resAmount.getString("currency_code");//货币类型代码
                }
                
                //订单类型标记 1:订单 2:重新下单 3:取消订单
                //重新下单后面有处理金额，如果餐馆原因订单金额是0,都算到折扣去
                if(orderDeliverooType==2 && sFault.equals("restaurant"))
                {
                    sDiscFractional=sOrder_cost;
                }
                
                //配达时想买家收取的金额
                int sCashFractional=0;
                if (resOrder.has("cash_due"))
                {
                    JSONObject sCash_due=resOrder.getJSONObject("cash_due");
                    //台币没有小数点，这里不用处理
                    sCashFractional=sCash_due.getInt("fractional");//金额的最新单位
                    String sCashCurrency_code=sCash_due.getString("currency_code");//货币类型代码
                }
                
                
                //平台补贴的金额
                int sSurchargeFractional=0;
                if (resOrder.has("surcharge"))
                {
                    JSONObject sSurcharge=resOrder.getJSONObject("surcharge");
                    //台币没有小数点，这里不用处理
                    sSurchargeFractional=sSurcharge.getInt("fractional");//金额的最新单位
                    String sSurchargeCurrency_code=sSurcharge.getString("currency_code");//货币类型代码
                }
                
                //配送地址
                String sLine1="";//街道、路
                String sLine2="";//楼幢号
                String sCity="";//城市、镇
                String sPostcode="";//邮编
                String sContact_number="";//用于联系客户的第三方号码
                String sContact_access_code="";//访问码==用于联系客户的第三方号码
                String sDeliveryStartDate="";//配达日期
                String sDeliveryStartTime="";//配达时间
                String sCustomer_name="";//买家姓名
                String sDeliveryNote="";//配送备注
                int sDelivery_feeFractional=0;//配送费
                double sLatitude=0;//纬度
                double sLongitude=0;//经度
                
                if (resOrder.has("delivery"))
                {
                    JSONObject sDelivery=resOrder.getJSONObject("delivery");
                    sLine1=sDelivery.getString("line1");
                    if(sDelivery.isNull("line2"))
                    {
                        sLine2="";
                    }
                    else
                    {
                        sLine2=sDelivery.getString("line2");//
                    }
                    
                    sCity=sDelivery.getString("city");//城市、镇
                    sPostcode=sDelivery.getString("postcode");//邮编
                    sContact_number=sDelivery.getString("contact_number");//用于联系客户的第三方号码
                    sContact_access_code=sDelivery.getString("contact_access_code");//访问码==用于联系客户的第三方号码
                    String sDeliver_by=sDelivery.getString("deliver_by");//配达时间   2019-01-09T17:47:50Z
                    sdfA = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    dLasttime = sdfA.parse(sDeliver_by);
                    //配达日期
                    sdfA=new SimpleDateFormat("yyyyMMdd");
                    sDeliveryStartDate=sdfA.format(dLasttime);
                    //System.out.println(sDeliveryStartDate);
                    //配达时间
                    sdfA=new SimpleDateFormat("HHmmss");
                    sDeliveryStartTime=sdfA.format(dLasttime);
                    //System.out.println(sDeliveryStartTime);
                    
                    sCustomer_name=sDelivery.getString("customer_name");//买家姓名
                    sDeliveryNote=sDelivery.getString("note");//配送备注
                    
                    //配送费
                    JSONObject sDelivery_fee=sDelivery.getJSONObject("delivery_fee");
                    //台币没有小数点，这里不用处理
                    sDelivery_feeFractional=sDelivery_fee.getInt("fractional");//金额的最新单位
                    String sDelivery_feeCurrency_code=sDelivery_fee.getString("currency_code");//货币类型代码
                    
                    //经纬度位置信息
                    JSONObject sLocation=sDelivery.getJSONObject("location");
                    sLatitude=sLocation.getDouble("latitude");//纬度
                    sLongitude=sLocation.getDouble("longitude");//经度
                    
                    //System.out.println(sLatitude);
                    //System.out.println(sLongitude);
                }
                
                
                
                String isInvoice = "N";// 是否开发票
                
                /* 订单中心status
                 * 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送
                 * 10.已发货 11.已完成 12.已退单
                 */
                jsonobjresponse.put("status", "2");
                jsonobjresponse.put("refundStatus", "1");//1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 	5.退单失败 6.退单成功 7.用户申请部分退款 8.拒绝部分退款 9 部分退款失败 10.部分退款成功
                
                jsonobjresponse.put("memo",sNotes);// string
                // 忌口或备注
                jsonobjresponse.put("sn", sDisplay_id);// 门店当天的订单流水号
                jsonobjresponse.put("isInvoice", isInvoice);// 是否开发票
                jsonobjresponse.put("invoiceTitle", "");
                jsonobjresponse.put("taxRegnumber", "");
                
                jsonobjresponse.put("orderNO", sId);// String 订单ID
                jsonobjresponse.put("createDatetime", createDate+createTime);//
                jsonobjresponse.put("loadDocType", "37");// deliveroo户户送
                jsonobjresponse.put("address",sCity+sLine1+sLine2 );// String
                jsonobjresponse.put("contMan", sCustomer_name);// String
                jsonobjresponse.put("contTel", sContact_number+"_"+sContact_access_code);// String
                jsonobjresponse.put("shipFee", sDelivery_feeFractional);// double
                String isBook = "N";
                if (bAsap==false)
                {
                    isBook = "Y";
                }
                jsonobjresponse.put("shipDate", sDeliveryStartDate);// 配送日期
                jsonobjresponse.put("shipTime", sDeliveryStartTime);// 配送时间
                jsonobjresponse.put("isBook", isBook);// 是否预订单
                int shipType=1;
                if (sFulfillment_type.equals("deliveroo"))
                {
                    shipType=1;
                }
                else
                {
                    shipType=2;
                }
                jsonobjresponse.put("shipType", shipType);// 配送类型 1.外卖平台配送 2.自配送 3.顾客自提
                jsonobjresponse.put("isShipcompany", "N");// 是否总部配送
                
                jsonobjresponse.put("sDate", createDate);// 系统日期
                jsonobjresponse.put("sTime", createTime);// 系统时间
                jsonobjresponse.put("companyNO", sEId);//
                jsonobjresponse.put("organizationNO", sShopno);//
                jsonobjresponse.put("shopNO", sShopno);//
                jsonobjresponse.put("shopName", "");//
                jsonobjresponse.put("machShopNO", "");//生产门店
                jsonobjresponse.put("machShopName", "");
                jsonobjresponse.put("shippingShopNO", "");//配送门店
                jsonobjresponse.put("shippingShopName", "");
                
                
                JSONArray array = new JSONArray();
                int item = 0;// 项次
                
                
                double tot_oldAMT=0;
                double tot_QTY=0;
                
                //商品明细
                JSONArray resItems=resOrder.getJSONArray("items");
                for(int ii=0;ii<resItems.length();ii++)
                {
                    int sQuantity=resItems.getJSONObject(ii).getInt("quantity");//商品数量
                    tot_QTY+=sQuantity;
                }
                
                BigDecimal tempDisc=new BigDecimal(0);
                for(int ii=0;ii<resItems.length();ii++)
                {
                    String sPos_item_id=resItems.getJSONObject(ii).getString("pos_item_id");//商品ID
                    int sQuantity=resItems.getJSONObject(ii).getInt("quantity");//商品数量
                    //单价信息
                    JSONObject resUnit_price=resItems.getJSONObject(ii).getJSONObject("unit_price");
                    //台币没有小数点，这里不用处理
                    int sUFractional=resUnit_price.getInt("fractional");//单价的最新单位
                    String sUCurrency_code=resUnit_price.getString("currency_code");//货币类型代码
                    
                    tot_oldAMT+=(sUFractional*sQuantity);
                    
                    //折扣分摊
                    BigDecimal  disc=new BigDecimal(0);
                    if (ii==resItems.length()-1)
                    {
                        disc= new BigDecimal(sQuantity*sDiscFractional/tot_QTY);
                        
                        BigDecimal discTot=new BigDecimal(sDiscFractional);
                        
                        disc=discTot.subtract(tempDisc);
                    }
                    else
                    {
                        disc= new BigDecimal(sQuantity*sDiscFractional/tot_QTY);
                        disc.setScale(2, 4);
                        tempDisc.add(disc);
                    }
                    
                    //成交金额
                    BigDecimal amt=new BigDecimal(sUFractional*sQuantity);
                    amt=amt.subtract(disc);
                    
                    if (sFulfillment_type.equals("deliveroo"))//由deliveroo平台配送的订单
                    {
                        //修改人的ID
                        JSONArray sModifiers = resItems.getJSONObject(ii).getJSONArray("modifiers");
                        for(int a=0;a<sModifiers.length();a++)
                        {
                            String sModifiersID=sModifiers.getString(a);
                            //System.out.println(sModifiersID);
                        }
                    }
                    else
                    {
                        //厨房通知备品
                        JSONArray sModiItem = resItems.getJSONObject(ii).getJSONArray("modifiers");
                        for(int aa=0;aa<sModiItem.length();aa++)
                        {
                            String sModiPos_item_id=sModiItem.getJSONObject(aa).getString("pos_item_id");//商品ID
                            int sModiQuantity=sModiItem.getJSONObject(aa).getInt("quantity");//商品数量
                            
                            //单价信息
                            JSONObject resModiUnit_price=sModiItem.getJSONObject(aa).getJSONObject("unit_price");
                            //台币没有小数点，这里不用处理
                            int sModiUFractional=resModiUnit_price.getInt("fractional");//单价的最新单位
                            String sModiUCurrency_code=resModiUnit_price.getString("currency_code");//货币类型代码
                            
                            //修改人的ID
                            JSONArray sModifiers = sModiItem.getJSONObject(aa).getJSONArray("modifiers");
                            for(int a=0;a<sModifiers.length();a++)
                            {
                                String sModifiersID=sModifiers.getString(a);
                                //System.out.println(sModifiersID);
                            }
                            
                        }
                    }
                    
                    //****************************订单商品处理********************************
                    item++;
                    JSONObject goodsobj = new JSONObject();
                    
                    goodsobj.put("item", item+"");
                    goodsobj.put("pluBarcode", sPos_item_id);
                    goodsobj.put("pluNO", sPos_item_id);
                    goodsobj.put("skuID", sPos_item_id);
                    goodsobj.put("pluName", sPos_item_id);
                    goodsobj.put("goodsGroup", "");// 菜品分组
                    goodsobj.put("unit", "");
                    goodsobj.put("price", sUFractional+"");
                    goodsobj.put("qty", sQuantity+"");
                    goodsobj.put("attrName", "");
                    goodsobj.put("specName", "");
                    goodsobj.put("disc", disc+"");
                    goodsobj.put("amt", amt+"");
                    goodsobj.put("isMemo", "N");
                    goodsobj.put("boxNum", "");
                    goodsobj.put("boxPrice", "");
                    
                    array.put(goodsobj);
                    
                }
                
                double tot_AMT=tot_oldAMT-sDiscFractional;
                
                jsonobjresponse.put("tot_oldAmt", tot_oldAMT+sDelivery_feeFractional);// double
                jsonobjresponse.put("tot_Amt", tot_AMT+sDelivery_feeFractional);// double
                jsonobjresponse.put("incomeAmt", sFractional);//
                jsonobjresponse.put("serviceCharge", "0");//
                jsonobjresponse.put("totDisc", sDiscFractional);//
                jsonobjresponse.put("sellerDisc", sDiscFractional);//
                jsonobjresponse.put("platformDisc", "0");//
                
                String payStatus="1";
                String payAmt="0";
                if (sCashFractional>0)
                {
                    payStatus="1";
                    payAmt="0";
                }
                else
                {
                    payStatus="3";
                    payAmt=tot_AMT+sDelivery_feeFractional+"";
                }
                jsonobjresponse.put("payStatus", payStatus);// 1.未支付 2.部分支付 3.付清
                jsonobjresponse.put("payAmt", payAmt);// double
                
                //
                jsonobjresponse.put("goods", array);
                
                //对应的ERP支付方式
                String erp_paycode="";
                String erp_payName="";
                String erp_paycodeERP="";
                if (mapPaylist.size()>0)
                {
                    erp_paycode=mapPaylist.get(0).get("PAYCODE").toString();
                    erp_payName=mapPaylist.get(0).get("PAYNAME").toString();
                    erp_paycodeERP=mapPaylist.get(0).get("PAYCODEERP").toString();
                    
                    JSONArray payarray = new JSONArray();
                    JSONObject payobj = new JSONObject();
                    payobj.put("item", "1");
                    payobj.put("payCode", erp_paycode);
                    payobj.put("payCodeerp", erp_paycodeERP);
                    payobj.put("payName", erp_payName);
                    payobj.put("cardNO", "");
                    payobj.put("ctType", "");
                    payobj.put("paySernum", "");
                    payobj.put("serialNO", "");
                    payobj.put("refNO", "");
                    payobj.put("teriminalNO", "");
                    payobj.put("descore", "0");
                    payobj.put("pay", tot_AMT+sDelivery_feeFractional);
                    payobj.put("extra", "0");
                    payobj.put("changed", "0");
                    payobj.put("bdate", createDate);
                    payobj.put("isOrderpay", "N");
                    payobj.put("isOnlinePay", "Y");
                    payobj.put("order_PayCode", "ALL");
                    payarray.put(payobj);
                }
                
                //
                String Response_json = jsonobjresponse.toString();
                
                try
                {
                    String redis_key = "WMORDER" + "_" + sEId + "_" + sShopno;//
                    String hash_key = sId;
                    writelog_waimai("【deliveroo户户送开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                    try
                    {
                        RedisPosPub redis = new RedisPosPub();
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey)
                        {
                            //redis.DeleteHkey(redis_key, hash_key);//
                            //writelog_waimai("【deliveroo户户送删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                            writelog_waimai("【deliveroo户户送hash_key的缓存】已存在！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        else
                        {
                            boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                            if (nret)
                            {
                                writelog_waimai("【deliveroo户户送写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
                            else
                            {
                                writelog_waimai("【deliveroo户户送写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                            }
                        }
                        
                        //redis.Close();
                        
                    }
                    catch (Exception e)
                    {
                        writelog_waimai("【deliveroo户户送写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                    
                    return Response_json;
                    
                }
                catch (Exception e)
                {
                    writelog_waimai("解析deliveroo户户送发送的请求格式有误！" + e.getMessage());
                    return null;
                }
                
            }
            
        }
        else
        {
            writelog_waimai("deliveroo户户送发送的请求非订单消息！");
            return null;
        }
    }
    
    
    /**
     * 根据单号查询订单详细信息
     * @param dao
     * @param eid 企业ID 值可为空
     * @param loaddoctype 渠道类型 值可为空
     * @param orderNo 单号 必传值
     * @return
     * @throws Exception
     */
    public static order GetOrderInfoByOrderNO(DsmDAO dao, String eid,String loaddoctype, String orderNo) throws Exception
    {
        
        order dcpOrder = new order();
        dcpOrder.setInvoiceDetail(new orderInvoice());
        dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
        dcpOrder.setPay(new ArrayList<orderPay>());
        
        String sql_head = "select * from dcp_order where orderno='"+orderNo+"' ";
        if(eid!=null&&eid.isEmpty()==false)
        {
            sql_head +=" and eid='"+eid+"' ";
        }
        if(loaddoctype!=null&&loaddoctype.isEmpty()==false)
        {
            sql_head +=" and loaddoctype='"+loaddoctype+"' ";
        }
        sql_head +=" and rownum = 1";
        HelpTools.writelog_waimai("【根据单号查询单头语句】" + sql_head);
        List<Map<String, Object>> getOrderHead = dao.executeQuerySQL(sql_head, null);
        
        if(getOrderHead==null||getOrderHead.isEmpty())
        {
            return null;
        }
        Map<String, Object> map = getOrderHead.get(0);
        String eId = map.get("EID").toString();
        String loadDocType =map.get("LOADDOCTYPE").toString();
        String channelId = map.get("CHANNELID").toString();
        String loadDocOrderNo = map.get("LOADDOCORDERNO").toString();
        String loadDocBillType = map.get("LOADDOCBILLTYPE").toString();
        String status =  map.get("STATUS").toString();
        String refundStatus =  map.get("REFUNDSTATUS").toString();
        String shopNo = map.get("SHOP").toString();
        String machShopNo = map.get("MACHSHOP").toString();
        String shipppingShopNo = map.get("SHIPPINGSHOP").toString();
        
        String loadDocTypeName = "";
        String channelIdName = "";
        if(orderLoadDocType.ELEME.equals(loadDocType))
        {
            loadDocTypeName = "饿了么";
            channelIdName = "饿了么";
        }
        else if(orderLoadDocType.MEITUAN.equals(loadDocType))
        {
            loadDocTypeName = "美团";
            channelIdName = "美团";
        }
        else if(orderLoadDocType.MTSG.equals(loadDocType))
        {
            loadDocTypeName = "美团闪购";
            channelIdName = "美团闪购";
        }
        else if(orderLoadDocType.MINI.equals(loadDocType))
        {
            loadDocTypeName = "小程序商城";
            channelIdName = "小程序商城";
        }
        else if(orderLoadDocType.WECHAT.equals(loadDocType))
        {
            loadDocTypeName = "微信手机商城";
            channelIdName = "微信手机商城";
        }
        else
        {
        
        }
        
        dcpOrder.setLoadDocTypeName(loadDocTypeName);
        dcpOrder.setChannelIdName(channelIdName);
        
        dcpOrder.seteId(eId);
        dcpOrder.setOrderNo(orderNo);
        dcpOrder.setLoadDocType(loadDocType);
        dcpOrder.setChannelId(channelId);
        dcpOrder.setLoadDocOrderNo(loadDocOrderNo);
        dcpOrder.setLoadDocBillType(loadDocBillType);
        dcpOrder.setStatus(status);
        dcpOrder.setRefundStatus(refundStatus);
        dcpOrder.setShopNo(shopNo);
        dcpOrder.setMachShopNo(machShopNo);
        dcpOrder.setShippingShopNo(shipppingShopNo);
        
        
        dcpOrder.setAddress(map.get("ADDRESS").toString());
        dcpOrder.setbDate(map.get("BDATE").toString());
        dcpOrder.setBelfirm(map.get("BELFIRM").toString());
        dcpOrder.setCardNo(map.get("CARDNO").toString());
        dcpOrder.setChannelId(map.get("CHANNELID").toString());
        dcpOrder.setCity(map.get("CITY").toString());
        dcpOrder.setContMan(map.get("CONTMAN").toString());
        dcpOrder.setContTel(map.get("CONTTEL").toString());
        dcpOrder.setCounty(map.get("COUNTY").toString());
        dcpOrder.setCreateDatetime(map.get("CREATE_DATETIME").toString());
        dcpOrder.setCustomer(map.get("CUSTOMER").toString());
        dcpOrder.setCustomerName(map.get("CUSTOMERNAME").toString());
        dcpOrder.setDeliveryNo(map.get("DELIVERYNO").toString());
        dcpOrder.setDeliveryStatus(map.get("DELIVERYSTATUS").toString());
        dcpOrder.setDeliveryType(map.get("DELIVERYTYPE").toString());
        dcpOrder.setDelMemo(map.get("DELMEMO").toString());
        dcpOrder.setDetailType(map.get("DETAILTYPE").toString());
        dcpOrder.setDeliveryBusinessType(map.get("DELIVERYBUSINESSTYPE").toString());
        try
        {
            dcpOrder.setEraseAmt(Double.parseDouble(map.get("ERASE_AMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setEraseAmt(0);
        }
        dcpOrder.setExceptionStatus(map.get("EXCEPTIONSTATUS").toString());
        
        dcpOrder.setGetMan(map.get("GETMAN").toString());
        dcpOrder.setGetManTel(map.get("GETMANTEL").toString());
        dcpOrder.setHeadOrderNo(map.get("HEADORDERNO").toString());
        try
        {
            dcpOrder.setIncomeAmt(Double.parseDouble(map.get("INCOMEAMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setIncomeAmt(0);
        }
        dcpOrder.setIsBook(map.get("ISBOOK").toString());
        dcpOrder.setIsChargeOrder(map.get("ISCHARGEORDER").toString());
        
        dcpOrder.setIsUrgentOrder(map.get("ISURGENTORDER").toString());
        dcpOrder.setLatitude(map.get("LATITUDE").toString());
        dcpOrder.setLoadDocBillType(map.get("LOADDOCBILLTYPE").toString());
        dcpOrder.setLoadDocOrderNo(map.get("LOADDOCORDERNO").toString());
        dcpOrder.setLoadDocType(map.get("LOADDOCTYPE").toString());
        dcpOrder.setLongitude(map.get("LONGITUDE").toString());
        dcpOrder.setMachineNo(map.get("MACHINE").toString());
        dcpOrder.setMachShopName(map.get("MACHSHOPNAME").toString());
        dcpOrder.setManualNo(map.get("MANUALNO").toString());
        dcpOrder.setMachShopNo(map.get("MACHSHOP").toString());
        
        try
        {
            dcpOrder.setMealNumber(Double.parseDouble(map.get("MEALNUMBER").toString()));
        } catch (Exception e)
        {
            dcpOrder.setMealNumber(0);
        }
        
        dcpOrder.setMemberId(map.get("MEMBERID").toString());
        dcpOrder.setMemberName(map.get("MEMBERNAME").toString());
        dcpOrder.setMemberPayNo(map.get("MEMBERPAYNO").toString());
        dcpOrder.setMemo(map.get("MEMO").toString());
        dcpOrder.setOpNo(map.get("OPNO").toString());
        dcpOrder.setOrderNo(map.get("ORDERNO").toString());
        dcpOrder.setOrderShop(map.get("ORDERSHOP").toString());
        dcpOrder.setOrderShopName(map.get("ORDERSHOPNAME").toString());
        dcpOrder.setOutDocType(map.get("OUTDOCTYPE").toString());
        dcpOrder.setOutDocTypeName(map.get("OUTDOCTYPENAME").toString());
        try
        {
            dcpOrder.setPackageFee(Double.parseDouble(map.get("PACKAGEFEE").toString()));
        } catch (Exception e)
        {
            dcpOrder.setPackageFee(0);
        }
        
        
        try
        {
            dcpOrder.setPayAmt(Double.parseDouble(map.get("PAYAMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setPayAmt(0);
        }
        dcpOrder.setPayStatus(map.get("PAYSTATUS").toString());
        try
        {
            dcpOrder.setPlatformDisc(Double.parseDouble(map.get("PLATFORM_DISC").toString()));
        } catch (Exception e)
        {
            dcpOrder.setPlatformDisc(0);
        }
        try
        {
            dcpOrder.setPointQty(Double.parseDouble(map.get("POINTQTY").toString()));
        } catch (Exception e)
        {
            dcpOrder.setPointQty(0);
        }
        dcpOrder.setProMemo(map.get("PROMEMO").toString());
        dcpOrder.setProvince(map.get("PROVINCE").toString());
        try
        {
            dcpOrder.setRefundAmt(Double.parseDouble(map.get("REFUNDAMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setRefundAmt(0);
        }
        dcpOrder.setRefundReason(map.get("REFUNDREASON").toString());
        dcpOrder.setRefundReasonName(map.get("REFUNDREASONNAME").toString());
        dcpOrder.setRefundReasonNo(map.get("REFUNDREASONNO").toString());
        dcpOrder.setRefundSourceBillNo(map.get("REFUNDSOURCEBILLNO").toString());
        dcpOrder.setRefundStatus(map.get("REFUNDSTATUS").toString());
        dcpOrder.setRequestId(map.get("REQUESTID").toString());
        dcpOrder.setRshipFee(map.get("RSHIPFEE").toString());
        dcpOrder.setSellCredit(map.get("SELLCREDIT").toString());
        
        try
        {
            dcpOrder.setSellerDisc(Double.parseDouble(map.get("SELLER_DISC").toString()));
        } catch (Exception e)
        {
            dcpOrder.setSellerDisc(0);
        }
        dcpOrder.setSellNo(map.get("SELLNO").toString());
        
        try
        {
            dcpOrder.setServiceCharge(Double.parseDouble(map.get("SERVICECHARGE").toString()));
        } catch (Exception e)
        {
            dcpOrder.setServiceCharge(0);
        }
        dcpOrder.setShipDate(map.get("SHIPDATE").toString());
        dcpOrder.setShipEndTime(map.get("SHIPENDTIME").toString());
        
        try
        {
            dcpOrder.setShipFee(Double.parseDouble(map.get("SHIPFEE").toString()));
        } catch (Exception e)
        {
            dcpOrder.setShipFee(0);
        }
        dcpOrder.setShippingShopName(map.get("SHIPPINGSHOPNAME").toString());
        dcpOrder.setShippingShopNo(map.get("SHIPPINGSHOP").toString());
        dcpOrder.setShipStartTime(map.get("SHIPSTARTTIME").toString());
        dcpOrder.setShipType(map.get("SHIPTYPE").toString());
        dcpOrder.setShopName(map.get("SHOPNAME").toString());
        dcpOrder.setShopNo(map.get("SHOP").toString());
        dcpOrder.setSn(map.get("ORDER_SN").toString());
        dcpOrder.setSquadNo(map.get("SQUADNO").toString());
        dcpOrder.setStatus(map.get("STATUS").toString());
        dcpOrder.setsTime(map.get("STIME").toString());
        dcpOrder.setStreet(map.get("STREET").toString());
        
        try
        {
            dcpOrder.setTot_Amt(Double.parseDouble(map.get("TOT_AMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setTot_Amt(0);
        }
        
        try
        {
            dcpOrder.setTot_oldAmt(Double.parseDouble(map.get("TOT_OLDAMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setTot_oldAmt(0);
        }
        dcpOrder.setTot_shipFee(map.get("TOTSHIPFEE").toString());
        
        try
        {
            dcpOrder.setTot_uAmt(Double.parseDouble(map.get("TOT_UAMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setTot_uAmt(0);
        }
        
        try
        {
            dcpOrder.setTotDisc(Double.parseDouble(map.get("TOT_DISC").toString()));
        } catch (Exception e)
        {
            dcpOrder.setTotDisc(0);
        }
        dcpOrder.setVerNum(map.get("VER_NUM").toString());
        dcpOrder.setWorkNo(map.get("WORKNO").toString());
        
        try
        {
            dcpOrder.setWriteOffAmt(Double.parseDouble(map.get("WRITEOFFAMT").toString()));
        } catch (Exception e)
        {
            dcpOrder.setWriteOffAmt(0);
        }
        dcpOrder.setIsMerPay(map.get("ISMERPAY").toString());
        //写缓存时，已经展开套餐了，
        dcpOrder.setIsApportion(map.getOrDefault("ISAPPORTION", "N").toString());

        dcpOrder.setpOrderNo(map.getOrDefault("PORDERNO", "").toString());
        String isIntention = map.getOrDefault("ISINTENTION","").toString();
        if (!"Y".equals(isIntention))
        {
            isIntention = "N";
        }
        dcpOrder.setIsIntention(isIntention);

        String isShipCompany = map.getOrDefault("ISSHIPCOMPANY","").toString();
        if (!"Y".equals(isShipCompany))
        {
            isShipCompany = "N";
        }
        dcpOrder.setIsShipCompany(isShipCompany);

        
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select b.*,"
                + "c.MITEM C_MITEM,c.ITEM C_ITEM,c.QTY C_QTY,c.AMT C_AMT,c.INPUTDISC C_INPUTDISC, "
                + "c.REALDISC C_REALDISC,c.DISC C_DISC,c.DCTYPE C_DCTYPE,c.DCTYPENAME C_DCTYPENAME, "
                + "c.PMTNO C_PMTNO,c.GIFTCTF C_GIFTCTF,c.GIFTCTFNO C_GIFTCTFNO,c.BSNO C_BSNO, "
                + "d.BILLNO D_BILLNO,d.ITEM D_ITEM,d.BILLDATE D_BILLDATE,d.BDATE D_BDATE, "
                + "d.SOURCEBILLTYPE D_SOURCEBILLTYPE,d.SOURCEBILLNO D_SOURCEBILLNO, "
                + "d.LOADDOCTYPE D_LOADDOCTYPE,d.CHANNELID D_CHANNELID,d.PAYCODE D_PAYCODE, "
                + "d.PAYCODEERP D_PAYCODEERP,d.PAYNAME D_PAYNAME,d.ORDER_PAYCODE D_ORDER_PAYCODE, "
                + "d.ISONLINEPAY D_ISONLINEPAY,d.PAY D_PAY,d.PAYDISCAMT D_PAYDISCAMT, "
                + "d.PAYAMT1 D_PAYAMT1,d.PAYAMT2 D_PAYAMT2,d.DESCORE D_DESCORE,d.CTTYPE D_CTTYPE, "
                + "d.CARDNO D_CARDNO,d.CARDBEFOREAMT D_CARDBEFOREAMT,d.CARDREMAINAMT D_CARDREMAINAMT, "
                + "d.COUPONQTY D_COUPONQTY,d.ISVERIFICATION D_ISVERIFICATION, "
                + "d.EXTRA D_EXTRA,d.CHANGED D_CHANGED,d.PAYSERNUM D_PAYSERNUM,d.SERIALNO D_SERIALNO, "
                + "d.REFNO D_REFNO,d.TERIMINALNO D_TERIMINALNO,d.CANINVOICE D_CANINVOICE, "
                + "d.WRITEOFFAMT D_WRITEOFFAMT,d.AUTHCODE D_AUTHCODE, "
                + "d.LASTMODIOPID D_LASTMODIOPID,d.LASTMODIOPNAME D_LASTMODIOPNAME, "
                + "d.LASTMODITIME D_LASTMODITIME, d.FUNCNO  D_FUNCNO,d.PAYDOCTYPE D_PAYDOCTYPE,d.SENDPAY D_SENDPAY, "
                + "d.PAYTYPE D_PAYTYPE, "
                + "d.MERDISCOUNT D_MERDISCOUNT,d.MERRECEIVE D_MERRECEIVE,d.THIRDDISCOUNT D_THIRDDISCOUNT,d.CUSTPAYREAL D_CUSTPAYREAL, "
                + "e.oitem E_OITEM,e.item E_ITEM,e.memotype E_MEMOTYPE,e.memoname E_MEMONAME,e.memo E_MEMO "
                + "from dcp_order_detail b "
                + "left join dcp_order_detail_agio c on b.eid=c.eid and b.orderno=c.orderno and b.item=c.mitem "
                + "left join dcp_order_pay_detail d on b.eid=d.eid and b.orderno=d.sourcebillno and d.sourcebilltype='Order' "
                + "left join dcp_order_detail_memo e on b.eid=e.eid and b.orderno=e.orderno and b.item=e.oitem "
                + " where b.eid='"+eId+"' and b.orderno='"+orderNo+"' "
                + "");
        
        
        
        String sql = sqlbuf.toString();
        HelpTools.writelog_waimai("【根据单号查询明细语句】" + sql);
        try
        {
            List<Map<String, Object>> getQDataDetail = dao.executeQuerySQL(sql, null);
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
            {
                //ITEM过滤商品
                Map<String, Boolean> condDetail_Item=new HashMap<>();
                condDetail_Item.put("ITEM", true);
                List<Map<String, Object>> getDetail=MapDistinct.getMap(getQDataDetail, condDetail_Item);
                
                //ITEM过滤商品的折扣
                Map<String, Boolean> condDetail_Item_Agio=new HashMap<>();
                condDetail_Item_Agio.put("C_MITEM", true);
                condDetail_Item_Agio.put("C_ITEM", true);
                List<Map<String, Object>> getDetailAgio=MapDistinct.getMap(getQDataDetail, condDetail_Item_Agio);
                
                //ITEM过滤商品的备注
                Map<String, Boolean> condDetail_Item_Memo=new HashMap<>();
                condDetail_Item_Memo.put("E_OITEM", true);
                condDetail_Item_Memo.put("E_ITEM", true);
                List<Map<String, Object>> getDetailMemo=MapDistinct.getMap(getQDataDetail, condDetail_Item_Memo);
                
                for (Map<String, Object> detail : getDetail)
                {
                    
                    orderGoodsItem goods=new orderGoodsItem();
                    
                    String item = detail.get("ITEM").toString();
                    
                    goods.setAccNo(detail.get("ACCNO").toString());
                    try
                    {
                        goods.setAmt(Double.parseDouble(detail.get("AMT").toString()));
                    } catch (Exception e)
                    {
                        goods.setAmt(0);
                    }
                    
                    goods.setAttrName(detail.get("ATTRNAME").toString());
                    goods.setAttrName_origin(detail.getOrDefault("ATTRNAME_ORIGIN","").toString());
                    try
                    {
                        goods.setBoxNum(Double.parseDouble(detail.get("BOXNUM").toString()));
                    } catch (Exception e)
                    {
                        goods.setBoxNum(0);
                    }
                    try
                    {
                        goods.setBoxPrice(Double.parseDouble(detail.get("BOXPRICE").toString()));
                    } catch (Exception e)
                    {
                        goods.setBoxPrice(0);
                    }
                    
                    goods.setCounterNo(detail.get("COUNTERNO").toString());
                    goods.setCouponCode(detail.get("COUPONCODE").toString());
                    goods.setCouponType(detail.get("COUPONTYPE").toString());
                    
                    try
                    {
                        goods.setDisc(Double.parseDouble(detail.get("DISC").toString()));
                    } catch (Exception e)
                    {
                        goods.setDisc(0);
                    }
                    goods.setFeatureName(detail.get("FEATURENAME").toString());
                    goods.setFeatureNo(detail.get("FEATURENO").toString());
                    goods.setGift(detail.get("GIFT").toString());
                    goods.setGiftReason(detail.get("GIFTREASON").toString());
                    goods.setGiftSourceSerialNo(detail.get("GIFTSOURCESERIALNO").toString());
                    goods.setGoodsGroup(detail.get("GOODSGROUP").toString());
                    goods.setGoodsUrl(detail.get("GOODSURL").toString());
                    goods.setInclTax(detail.get("INCLTAX").toString());
                    //goods.setInvNo(detail.get("INVNO").toString());
                    goods.setInvSplitType(detail.get("INVSPLITTYPE").toString());
                    goods.setIsMemo(detail.get("ISMEMO").toString());
                    goods.setItem(item);
                    goods.setoItem(detail.get("OITEM").toString());
                    //goods.setoReItem(detail.get("OREITEM").toString());
                    try
                    {
                        goods.setOldAmt(Double.parseDouble(detail.get("OLDAMT").toString()));
                    } catch (Exception e)
                    {
                        goods.setOldAmt(0);
                    }
                    
                    try
                    {
                        goods.setOldPrice(Double.parseDouble(detail.get("OLDPRICE").toString()));
                    } catch (Exception e)
                    {
                        goods.setOldPrice(0);
                    }
                    goods.setPackageMitem(detail.get("PACKAGEMITEM").toString());
                    goods.setPackageType(detail.get("PACKAGETYPE").toString());
                    
                    try
                    {
                        goods.setPickQty(Double.parseDouble(detail.get("PICKQTY").toString()));
                    } catch (Exception e)
                    {
                        goods.setPickQty(0);
                    }
                    goods.setPluBarcode(detail.get("PLUBARCODE").toString());
                    goods.setPluName(detail.get("PLUNAME").toString());
                    goods.setPluNo(detail.get("PLUNO").toString());
                    
                    try
                    {
                        goods.setPrice(Double.parseDouble(detail.get("PRICE").toString()));
                    } catch (Exception e)
                    {
                        goods.setPrice(0);
                    }
                    
                    try
                    {
                        goods.setQty(Double.parseDouble(detail.get("QTY").toString()));
                    } catch (Exception e)
                    {
                        goods.setQty(0);
                    }
                    
                    try
                    {
                        goods.setrQty(Double.parseDouble(detail.get("RQTY").toString()));
                    } catch (Exception e)
                    {
                        goods.setrQty(0);
                    }
                    goods.setSellerName(detail.get("SELLERNAME").toString());
                    goods.setSellerNo(detail.get("SELLERNO").toString());
                    
                    try
                    {
                        goods.setShopQty(Double.parseDouble(detail.get("SHOPQTY").toString()));
                    } catch (Exception e)
                    {
                        goods.setShopQty(0);
                    }
                    goods.setSpecName(detail.get("SPECNAME").toString());
                    goods.setSpecName_origin(detail.getOrDefault("SPECNAME_ORIGIN","").toString());
                    goods.setsTime(detail.get("STIME").toString());
                    goods.setsUnit(detail.get("SUNIT").toString());
                    goods.setsUnitName(detail.get("SUNITNAME").toString());
                    goods.setTaxCode(detail.get("TAXCODE").toString());
                    goods.setTaxType(detail.get("TAXTYPE").toString());
                    goods.setToppingMitem(detail.get("TOPPINGMITEM").toString());
                    goods.setToppingType(detail.get("TOPPINGTYPE").toString());
                    goods.setWarehouse(detail.get("WAREHOUSE").toString());
                    goods.setWarehouseName(detail.get("WAREHOUSENAME").toString());
                    goods.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                    goods.setMessages(new ArrayList<orderGoodsItemMessage>());
                    
                    //商品折扣
                    for (Map<String, Object> detailAgio : getDetailAgio)
                    {
                        String C_MITEM = detailAgio.get("C_MITEM").toString();
                        String C_ITEM = detailAgio.get("C_ITEM").toString();
                        if(C_MITEM==null||C_MITEM.isEmpty()||C_ITEM==null||C_ITEM.isEmpty())
                        {
                            continue;
                        }
                        if(C_MITEM.equals(item))
                        {
                            orderGoodsItemAgio agio=new orderGoodsItemAgio();
                            
                            try
                            {
                                agio.setAmt(Double.parseDouble(detailAgio.get("C_AMT").toString()));
                            } catch (Exception e)
                            {
                                agio.setAmt(0);
                            }
                            agio.setBsNo(detailAgio.get("C_BSNO").toString());
                            agio.setDcType(detailAgio.get("C_DCTYPE").toString());
                            agio.setDcTypeName(detailAgio.get("C_DCTYPENAME").toString());
                            
                            try
                            {
                                agio.setDisc(Double.parseDouble(detailAgio.get("C_DISC").toString()));
                            } catch (Exception e)
                            {
                                agio.setDisc(0);
                            }
                            agio.setGiftCtf(detailAgio.get("C_GIFTCTF").toString());
                            agio.setGiftCtfNo(detailAgio.get("C_GIFTCTFNO").toString());
                            
                            try
                            {
                                agio.setInputDisc(Double.parseDouble(detailAgio.get("C_INPUTDISC").toString()));
                            } catch (Exception e)
                            {
                                agio.setInputDisc(0);
                            }
                            agio.setItem(detailAgio.get("C_ITEM").toString());
                            agio.setPmtNo(detailAgio.get("C_PMTNO").toString());
                            
                            try
                            {
                                agio.setQty(Double.parseDouble(detailAgio.get("C_QTY").toString()));
                            } catch (Exception e)
                            {
                                agio.setQty(0);
                            }
                            
                            try
                            {
                                agio.setRealDisc(Double.parseDouble(detailAgio.get("C_REALDISC").toString()));
                            } catch (Exception e)
                            {
                                agio.setRealDisc(0);
                            }
                            
                            goods.getAgioInfo().add(agio);
                            agio=null;
                        }
                        
                        
                    }
                    
                    //商品备注
                    for (Map<String, Object> detail_Memo : getDetailMemo)
                    {
                        String E_OITEM = detail_Memo.get("E_OITEM").toString();
                        String E_ITEM = detail_Memo.get("E_ITEM").toString();
                        if(E_OITEM==null||E_OITEM.isEmpty()||E_ITEM==null||E_ITEM.isEmpty())
                        {
                            continue;
                        }
                        
                        if(E_OITEM.equals(item))
                        {
                            orderGoodsItemMessage memos=new orderGoodsItemMessage();
                            memos.setMessage(detail_Memo.get("E_MEMO").toString());
                            memos.setMsgName(detail_Memo.get("E_MEMONAME").toString());
                            memos.setMsgType(detail_Memo.get("E_MEMOTYPE").toString());
                            
                            goods.getMessages().add(memos);
                            memos=null;
                        }
                        
                        
                    }
                    
                    dcpOrder.getGoodsList().add(goods);
                    goods=null;
                    
                }
                
                
                //D_ITEM过滤付款明细
                Map<String, Boolean> condPayDetail_DItem=new HashMap<>();
                condPayDetail_DItem.put("D_ITEM", true);
                List<Map<String, Object>> getPayDetail=MapDistinct.getMap(getQDataDetail, condPayDetail_DItem);
                
                for (Map<String, Object> payDetail : getPayDetail)
                {
                    String D_ITEM = payDetail.get("D_ITEM").toString();
                    if (D_ITEM==null||D_ITEM.isEmpty())
                    {
                        continue;
                    }
                    
                    orderPay paylist= new orderPay();
                    paylist.setAuthCode(payDetail.get("D_AUTHCODE").toString());
                    paylist.setbDate(payDetail.get("D_BDATE").toString());
                    paylist.setCanInvoice(payDetail.get("D_CANINVOICE").toString());
                    paylist.setCardBeforeAmt(payDetail.get("D_CARDBEFOREAMT").toString());
                    paylist.setCardNo(payDetail.get("D_CARDNO").toString());
                    paylist.setCardRemainAmt(payDetail.get("D_CARDREMAINAMT").toString());
                    paylist.setChanged(payDetail.get("D_CHANGED").toString());
                    //paylist.setCouponQty(payDetail.get("D_COUPONQTY").toString());
                    paylist.setCtType(payDetail.get("D_CTTYPE").toString());
                    paylist.setDescore(payDetail.get("D_DESCORE").toString());
                    paylist.setExtra(payDetail.get("D_EXTRA").toString());
                    paylist.setIsOnlinePay(payDetail.get("D_ISONLINEPAY").toString());
                    //paylist.setIsOrderpay(payDetail.get("D_ISORDERPAY").toString());
                    paylist.setIsVerification(payDetail.get("D_ISVERIFICATION").toString());
                    paylist.setItem(D_ITEM);
                    //paylist.setLoadDocType(payDetail.get("D_LOADDOCTYPE").toString());
                    paylist.setOrder_payCode(payDetail.get("D_ORDER_PAYCODE").toString());
                    paylist.setPay(payDetail.get("D_PAY").toString());
                    paylist.setPayAmt1(payDetail.get("D_PAYAMT1").toString());
                    paylist.setPayAmt2(payDetail.get("D_PAYAMT2").toString());
                    paylist.setPayCode(payDetail.get("D_PAYCODE").toString());
                    paylist.setPayCodeErp(payDetail.get("D_PAYCODEERP").toString());
                    paylist.setPayDiscAmt(payDetail.get("D_PAYDISCAMT").toString());
                    paylist.setPayName(payDetail.get("D_PAYNAME").toString());
                    paylist.setPaySerNum(payDetail.get("D_PAYSERNUM").toString());
                    paylist.setRefNo(payDetail.get("D_REFNO").toString());
                    paylist.setSerialNo(payDetail.get("D_SERIALNO").toString());
                    paylist.setTeriminalNo(payDetail.get("D_TERIMINALNO").toString());
                    paylist.setPaydoctype(payDetail.get("D_PAYDOCTYPE").toString());
                    paylist.setFuncNo(payDetail.get("D_FUNCNO").toString());
                    paylist.setCardSendPay(payDetail.get("D_SENDPAY").toString());
                    paylist.setPayType(payDetail.get("D_PAYTYPE").toString());
                    
                    String MERDISCOUNT =  payDetail.getOrDefault("D_MERDISCOUNT", "0").toString();
                    try
                    {
                        paylist.setMerDiscount(Double.parseDouble(MERDISCOUNT));
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                    
                    String MERRECEIVE =  payDetail.getOrDefault("D_MERRECEIVE", "0").toString();
                    try
                    {
                        paylist.setMerReceive(Double.parseDouble(MERRECEIVE));
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                    
                    String THIRDDISCOUNT =  payDetail.getOrDefault("D_THIRDDISCOUNT", "0").toString();
                    try
                    {
                        paylist.setThirdDiscount(Double.parseDouble(THIRDDISCOUNT));
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                    
                    String CUSTPAYREAL =  payDetail.getOrDefault("D_CUSTPAYREAL", "0").toString();
                    try
                    {
                        paylist.setCustPayReal(Double.parseDouble(CUSTPAYREAL));
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                    
                    dcpOrder.getPay().add(paylist);
                    paylist=null;
                }
                
                
                
                
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        
        
        
        return dcpOrder;
    }
    
    /**
     * @param orderNo
     *            订单编号
     * @param DeliveryType
     *            0、无 1 自配送 2 顺丰 3百度 4达达 5 人人 6 闪送 等等
     * @param statusType
     *            状态类型 1-订单状态 2-配送状态 3-退单状态
     * @param status
     *            最新状态
     * @param disMobile
     *            配送员手机号
     * @param opName
     *            用户名称 可以给值如"骑士：XXX"
     * @param otherDes
     *            其他描述，如"超出配送范围，退单"
     * @throws Exception
     */
    public static void save(DsmDAO dao, String orderNo, String DeliveryType, String statusType, String status,
                            Object disMobile, String opName, String otherDes) throws Exception {
        
        try {
            String sqlTvOrder = "select * from DCP_ORDER where ORDERNO=? and DELIVERYTYPE=? ";
            List<Map<String, Object>> listTvOrder = dao.executeQuerySQL(sqlTvOrder,
                    new String[] { orderNo, DeliveryType });
            if (listTvOrder == null || listTvOrder.size() == 0) {
                listTvOrder = dao.executeQuerySQL("select * from DCP_ORDER where ORDERNO=? ",
                        new String[] { orderNo });
            }
            if (listTvOrder != null && listTvOrder.size() > 0) {/*
					String opNO = "";
					// region订单状态
					// 门店
					String shop = listTvOrder.get(0).get("SHOP").toString();
					OrderStatusLogCreateReq.level1Elm onelv1 = new OrderStatusLogCreateReq(). new level1Elm();
					onelv1.setO_companyNO(listTvOrder.get(0).get("COMPANYNO").toString());
					onelv1.setO_organizationNO(shop);
					onelv1.setO_customerNO(listTvOrder.get(0).get("CUSTOMERNO") == null ? ""
							: listTvOrder.get(0).get("CUSTOMERNO").toString());
					onelv1.setO_shopNO(shop);
					onelv1.setOrderNO(orderNo);
					onelv1.setO_opNO(opNO);
					onelv1.setO_opName(opName);
					onelv1.setLoadDocType(listTvOrder.get(0).get("LOAD_DOCTYPE") == null ? ""
							: listTvOrder.get(0).get("LOAD_DOCTYPE").toString());
					// 状态类型 1-订单状态 2-配送状态 3-退单状态
					onelv1.setStatusType(statusType);

					StringBuilder statusTypeNameObj = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName(statusType, status, statusTypeNameObj);
					String statusTypeName = statusTypeNameObj.toString();
					onelv1.setStatusTypeName(statusTypeName);
					onelv1.setStatusName(statusName);
					onelv1.setStatus(status);

					String memo = "";
					if (otherDes == null ||otherDes.trim().isEmpty()) {
						memo += statusTypeName + "-->" + statusName;
					}

					if (disMobile != null && !disMobile.toString().trim().isEmpty()) {
						memo += " 配送电话-->" + disMobile;
					}
					if (otherDes != null && !otherDes.trim().isEmpty()) {
						memo += " " + otherDes;
					}
					onelv1.setMemo(memo);
					onelv1.setDisplay("1");

					String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv1.setUpdate_time(updateDatetime);

					// 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
					onelv1.setCallback_status("0");
					// 是否调用第三方接口，N-不需要调用，Y-需要
					onelv1.setNeed_callback("N");
					// 是否通知云pos,N-不需要调用，Y-需要
					onelv1.setNeed_notify("N");

					OrderStatusLogCreateReq req_log = new OrderStatusLogCreateReq();
					req_log.setDatas(new ArrayList<OrderStatusLogCreateReq.level1Elm>());
					req_log.getDatas().add(onelv1);

					String req_log_json = "";
					try
					{
						ParseJson pj = new ParseJson();
						req_log_json = pj.beanToJson(req_log);
						pj=null;
					} catch (Exception e) {

					}
					StringBuilder errorMessage = new StringBuilder();
					boolean nRet = HelpTools.InsertOrderStatusLog(dao, req_log_json, errorMessage);
					if (nRet) {
						HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
					} else {
						HelpTools.writelog_waimai(
								"【写表tv_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
					}
					dao.closeDAO();
				 */} else {
                HelpTools.writelog_fileName("【物流回调WULIUCallBack】 单号:" + orderNo + "查无资料", "WULIUCallBack");
            }
        } catch (Exception e) {
        
        }
    }
    
    /**
     * 返回饿了么对应的ERP门店映射（99_10001,companyno_shopno）
     * @param app_poi_code
     * @return
     */
    public static Map<String, String> GetELMMappingShop(String app_poi_code)
    {
        Map<String, String> mappingshopMap = new HashMap<String, String>();
        //String mappingshop_redis_key = orderRedisKeyInfo.redisKey_elemeMappingshop;
        //String mappingshop_hash_key = app_poi_code;
        String eid = "99";
        String shopno = "";
        String channelId = "";
        String shopName = "";
        boolean needQueryDB = false;//是否需要从数据库查询
        boolean needUpdateMapList = false;//是否需要更新内存中数据
        try
        {
            long dt1 = System.currentTimeMillis();
            //RedisPosPub redis = new RedisPosPub();
            //String mappingshop = redis.getHashMap(mappingshop_redis_key, mappingshop_hash_key);
            Map<String,String> mappingshop = null;
            if (elmMappingShopList!=null&&!elmMappingShopList.isEmpty())
            {
                mappingshop = elmMappingShopList.get(app_poi_code);
            }
            //redis.Close();
            
            if (mappingshop != null && mappingshop.isEmpty() == false)
            {
               /* JSONObject obj_mappingshop = new JSONObject(mappingshop);
                eid = obj_mappingshop.get("eId").toString();
                shopno = obj_mappingshop.get("erpShopNo").toString();
                channelId = obj_mappingshop.optString("channelId","");
                shopName = obj_mappingshop.optString("erpShopName","");*/
                long dt2 = System.currentTimeMillis();
                long dt_spwn = dt2-dt1;
                if (dt_spwn>=100)
                {
                    writelog_waimai("【内存中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+app_poi_code);
                }
                return mappingshop;
            }
            else
            {
                needQueryDB = true;
            }
            
        }
        catch (Exception e)
        {
            needQueryDB = true;
        }
        
        
        if(needQueryDB)
        {
            try
            {
                Map<String, Object> getData = GetWaimaiMappingShopFromDB(orderLoadDocType.ELEME, app_poi_code);
                if(getData!=null)
                {
                    eid = getData.get("EID").toString();
                    shopno = getData.get("SHOPID").toString();
                    channelId = getData.get("CHANNELID").toString();
                    shopName = getData.get("SHOPNAME").toString();
                    needUpdateMapList = true;
                }
                
            }
            catch (Exception e)
            {
            
            
            }
        }
        mappingshopMap.put("eId", eid);
        mappingshopMap.put("erpShopNo", shopno);
        mappingshopMap.put("channelId", channelId);
        mappingshopMap.put("erpShopName", shopName);
        if (needUpdateMapList)
        {
            if (elmMappingShopList==null)
            {
                elmMappingShopList = new HashMap<>();
            }
            elmMappingShopList.put(app_poi_code,mappingshopMap);
        }
        
        return mappingshopMap;
    }
    
    /**
     * 返回美团对应的ERP门店映射（99_10001,companyno_shopno）
     * @param app_poi_code
     * @return
     */
    public static Map<String, String> GetMTMappingShop(String app_poi_code)
    {
        Map<String, String> mappingshopMap = new HashMap<String, String>();
        String mappingshop_redis_key = orderRedisKeyInfo.redisKey_mtMappingshop;
        String mappingshop_hash_key = app_poi_code;
        String eid = "99";
        String shopno = "";
        String channelId = "";
        String shopName = "";
        boolean needQueryDB = false;//是否需要从数据库查询
        boolean needUpdateMapList = false;//是否需要更新内存中数据
        try
        {
            long dt1 = System.currentTimeMillis();
            //RedisPosPub redis = new RedisPosPub();
            //String mappingshop = redis.getHashMap(mappingshop_redis_key, mappingshop_hash_key);
            //redis.Close();
            Map<String,String> mappingshop = null;
            if (mtMappingShopList!=null&&!mtMappingShopList.isEmpty())
            {
                mappingshop = mtMappingShopList.get(app_poi_code);
            }
            
            if (mappingshop != null && mappingshop.isEmpty() == false)
            {
                /*JSONObject obj_mappingshop = new JSONObject(mappingshop);
                eid = obj_mappingshop.get("eId").toString();
                shopno = obj_mappingshop.get("erpShopNo").toString();
                channelId = obj_mappingshop.optString("channelId","");
                shopName = obj_mappingshop.optString("erpShopName","");*/
                long dt2 = System.currentTimeMillis();
                long dt_spwn = dt2-dt1;
                if (dt_spwn>=100)
                {
                    writelog_waimai("【内存中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+app_poi_code);
                }
                return mappingshop;
            }
            else
            {
                needQueryDB = true;
            }
            
        }
        catch (Exception e)
        {
            needQueryDB = true;
        }
        
        
        if(needQueryDB)
        {
            try
            {
                Map<String, Object> getData = GetWaimaiMappingShopFromDB(orderLoadDocType.MEITUAN, app_poi_code);
                if(getData!=null)
                {
                    eid = getData.get("EID").toString();
                    shopno = getData.get("SHOPID").toString();
                    channelId = getData.get("CHANNELID").toString();
                    shopName = getData.get("SHOPNAME").toString();
                    needUpdateMapList = true;
                }
                
            }
            catch (Exception e)
            {
            
            
            }
        }
        mappingshopMap.put("eId", eid);
        mappingshopMap.put("erpShopNo", shopno);
        mappingshopMap.put("channelId", channelId);
        mappingshopMap.put("erpShopName", shopName);
        if (needUpdateMapList)
        {
            if (mtMappingShopList==null)
            {
                mtMappingShopList = new HashMap<>();
            }
            mtMappingShopList.put(app_poi_code,mappingshopMap);
        }
        
        return mappingshopMap;
    }
    
    public static Map<String, String> GetJBPMappingShop(String ePoiId)
    {
        Map<String, String> mappingshopMap = new HashMap<String, String>();
        String mappingshop_redis_key = orderRedisKeyInfo.redisKey_jbpMappingshop;
        //String mappingshop_hash_key = ePoiId;
        String eid = "99";
        String shopno = "";
        String channelId = "";
        String shopName = "";
        String orderShopNo = "";
        boolean needQueryDB = false;//是否需要从数据库查询
        boolean needUpdateMapList = false;//是否需要更新内存中数据
        try
        {
            long dt1 = System.currentTimeMillis();
            //RedisPosPub redis = new RedisPosPub();
            //String mappingshop = redis.getHashMap(mappingshop_redis_key, mappingshop_hash_key);
            //redis.Close();
            Map<String,String> mappingshop = null;
            if (jbpMappingShopList!=null&&!jbpMappingShopList.isEmpty())
            {
                mappingshop = jbpMappingShopList.get(ePoiId);
            }
            if (mappingshop != null && mappingshop.isEmpty() == false)
            {
                /*JSONObject obj_mappingshop = new JSONObject(mappingshop);
                eid = obj_mappingshop.get("eId").toString();
                shopno = obj_mappingshop.get("erpShopNo").toString();
                orderShopNo = obj_mappingshop.optString("orderShopNo","");
                channelId = obj_mappingshop.optString("channelId","");
                shopName = obj_mappingshop.optString("erpShopName","");*/
                long dt2 = System.currentTimeMillis();
                long dt_spwn = dt2-dt1;
                if (dt_spwn>=100)
                {
                    writelog_waimai("【内存中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+ePoiId);
                }
                return mappingshop;
            }
            else
            {
                needQueryDB = true;
            }
            
        }
        catch (Exception e)
        {
            needQueryDB = true;
        }
        
        
        if(needQueryDB)
        {
            try
            {
                Map<String, Object> getData = GetJBPWaimaiMappingShopFromDB(orderLoadDocType.MEITUAN, ePoiId);
                if(getData!=null)
                {
                    eid = getData.get("EID").toString();
                    shopno = getData.get("SHOPID").toString();
                    orderShopNo = getData.get("ORDERSHOPNO").toString();
                    channelId = getData.get("CHANNELID").toString();
                    shopName = getData.get("SHOPNAME").toString();
                    needUpdateMapList = true;
                }
                
            }
            catch (Exception e)
            {
            
            
            }
        }
        mappingshopMap.put("eId", eid);
        mappingshopMap.put("erpShopNo", shopno);
        mappingshopMap.put("orderShopNo", orderShopNo);
        mappingshopMap.put("channelId", channelId);
        mappingshopMap.put("erpShopName", shopName);
        if (needUpdateMapList)
        {
            if (jbpMappingShopList==null)
            {
                jbpMappingShopList = new HashMap<>();
            }
            jbpMappingShopList.put(ePoiId,mappingshopMap);
        }
        
        return mappingshopMap;
    }
    
    /**
     * 返回美团对应的ERP门店映射（99_10001,companyno_shopno）
     * @param app_poi_code
     * @return
     */
    public static Map<String, String> GetSGMTMappingShop(String app_poi_code)
    {
        Map<String, String> mappingshopMap = new HashMap<String, String>();
        String mappingshop_redis_key = orderRedisKeyInfo.redisKey_sgmtMappingshop;
        String mappingshop_hash_key = app_poi_code;
        String eid = "99";
        String shopno = "";
        String channelId = "";
        String shopName = "";
        boolean needQueryDB = false;//是否需要从数据库查询
        boolean needUpdateMapList = false;//是否需要更新内存中数据
        try
        {
            long dt1 = System.currentTimeMillis();
            //RedisPosPub redis = new RedisPosPub();
            //String mappingshop = redis.getHashMap(mappingshop_redis_key, mappingshop_hash_key);
            //redis.Close();
            Map<String,String> mappingshop = null;
            if (sgmtMappingShopList!=null&&!sgmtMappingShopList.isEmpty())
            {
                mappingshop = sgmtMappingShopList.get(app_poi_code);
            }
            
            if (mappingshop != null && mappingshop.isEmpty() == false)
            {
                /*JSONObject obj_mappingshop = new JSONObject(mappingshop);
                eid = obj_mappingshop.get("eId").toString();
                shopno = obj_mappingshop.get("erpShopNo").toString();
                channelId = obj_mappingshop.optString("channelId","");
                shopName = obj_mappingshop.optString("erpShopName","");*/
                long dt2 = System.currentTimeMillis();
                long dt_spwn = dt2-dt1;
                if (dt_spwn>=100)
                {
                    writelog_waimai("【内存中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+app_poi_code);
                }
                return mappingshop;
            }
            else
            {
                needQueryDB = true;
            }
            
        }
        catch (Exception e)
        {
            needQueryDB = true;
        }
        
        
        if(needQueryDB)
        {
            try
            {
                Map<String, Object> getData = GetWaimaiMappingShopFromDB(orderLoadDocType.MTSG, app_poi_code);
                if(getData!=null)
                {
                    eid = getData.get("EID").toString();
                    shopno = getData.get("SHOPID").toString();
                    channelId = getData.get("CHANNELID").toString();
                    shopName = getData.get("SHOPNAME").toString();
                    needUpdateMapList = true;
                }
                
            }
            catch (Exception e)
            {
            
            
            }
        }
        mappingshopMap.put("eId", eid);
        mappingshopMap.put("erpShopNo", shopno);
        mappingshopMap.put("channelId", channelId);
        mappingshopMap.put("erpShopName", shopName);
        if (needUpdateMapList)
        {
            if (sgmtMappingShopList==null)
            {
                sgmtMappingShopList = new HashMap<>();
            }
            sgmtMappingShopList.put(app_poi_code,mappingshopMap);
        }
        
        return mappingshopMap;
    }
    
    /**
     * 返回抖音对应的ERP门店映射
     * @param poi
     * @return
     */
    public static Map<String, String> GetDYMappingShop(String poi)
    {
        Map<String, String> mappingshopMap = new HashMap<String, String>();
        //String mappingshop_redis_key = orderRedisKeyInfo.redisKey_dywmMappingshop;
        //String mappingshop_hash_key = poi;
        String eid = "99";
        String shopno = "";
        String channelId = "";
        String shopName = "";
        boolean needQueryDB = false;//是否需要从数据库查询
        boolean needUpdateMapList = false;//是否需要更新内存中数据
        try
        {
            long dt1 = System.currentTimeMillis();
            //RedisPosPub redis = new RedisPosPub();
            //String mappingshop = redis.getHashMap(mappingshop_redis_key, mappingshop_hash_key);
            //redis.Close();
            Map<String,String> mappingshop = null;
            if (dyMappingShopList!=null&&!dyMappingShopList.isEmpty())
            {
                mappingshop = dyMappingShopList.get(poi);
            }
            
            if (mappingshop != null && mappingshop.isEmpty() == false)
            {
                /*JSONObject obj_mappingshop = new JSONObject(mappingshop);
                eid = obj_mappingshop.get("eId").toString();
                shopno = obj_mappingshop.get("erpShopNo").toString();
                channelId = obj_mappingshop.optString("channelId","");
                shopName = obj_mappingshop.optString("erpShopName","");*/
                long dt2 = System.currentTimeMillis();
                long dt_spwn = dt2-dt1;
                if (dt_spwn>=100)
                {
                    writelog_waimai("【内存中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+poi);
                }
                return mappingshop;
            }
            else
            {
                needQueryDB = true;
            }
            
        }
        catch (Exception e)
        {
            needQueryDB = true;
        }
        
        
        if(needQueryDB)
        {
            try
            {
                Map<String, Object> getData = GetWaimaiMappingShopFromDB(orderLoadDocType.DYWM, poi);
                if(getData!=null)
                {
                    eid = getData.get("EID").toString();
                    shopno = getData.get("SHOPID").toString();
                    channelId = getData.get("CHANNELID").toString();
                    shopName = getData.get("SHOPNAME").toString();
                    needUpdateMapList = true;
                }
                
            }
            catch (Exception e)
            {
            
            
            }
        }
        mappingshopMap.put("eId", eid);
        mappingshopMap.put("erpShopNo", shopno);
        mappingshopMap.put("channelId", channelId);
        mappingshopMap.put("erpShopName", shopName);
        if (needUpdateMapList)
        {
            if (dyMappingShopList==null)
            {
                dyMappingShopList = new HashMap<>();
            }
            dyMappingShopList.put(poi,mappingshopMap);
        }
        
        return mappingshopMap;
    }
    
    /**
     * 从数据库查询外卖映射的门店
     * @param loadDocType 外卖平台类型
     * @param orderShopNo 平台门店ID
     * @return
     */
    public static Map<String, Object> GetWaimaiMappingShopFromDB(String loadDocType,String orderShopNo)
    {
        try
        {
            long dt1 = System.currentTimeMillis();
            String sql = "select * from dcp_mappingshop where businessid='2' and load_doctype='"+ loadDocType+"'  and ordershopno='"+orderShopNo+"' ";
            writelog_waimai("【从数据库查询外卖映射门店】开始，平台类型="+loadDocType+" 平台门店="+orderShopNo+" 查询sql="+sql);
            List<Map<String, Object>> getData = StaticInfo.dao.executeQuerySQL(sql, null);
            writelog_waimai("【从数据库查询外卖映射门店】完成，平台类型="+loadDocType+" 平台门店="+orderShopNo);
            long dt2 = System.currentTimeMillis();
            long dt_spwn = dt2-dt1;
            if (dt_spwn>=100)
            {
                writelog_waimai("【数据库中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+orderShopNo);
            }
            if(getData!=null&&getData.isEmpty()==false)
            {
                return getData.get(0);
            }
            else
            {
                return null;
            }
            
            
        }
        catch (Exception e)
        {
            return null;
        }
        
    }
    
    /**
     * 从数据库查询美团(服务商模式对接)外卖映射的门店
     * @param loadDocType
     * @param ePoiId
     * @return
     */
    public static Map<String, Object> GetJBPWaimaiMappingShopFromDB(String loadDocType,String ePoiId)
    {
        try
        {
            long dt1 = System.currentTimeMillis();
            String sql = "select * from dcp_mappingshop where businessid='2' and load_doctype='"+ loadDocType+"'  and MAPPINGSHOPNO='"+ePoiId+"' order by tran_time desc";
            writelog_waimai("【从数据库查询外卖映射门店】开始，平台类型="+loadDocType+" 平台门店="+ePoiId+" 查询sql="+sql);
            List<Map<String, Object>> getData = StaticInfo.dao.executeQuerySQL(sql, null);
            writelog_waimai("【从数据库查询外卖映射门店】完成，平台类型="+loadDocType+" 平台门店="+ePoiId);
            long dt2 = System.currentTimeMillis();
            long dt_spwn = dt2-dt1;
            if (dt_spwn>=100)
            {
                writelog_waimai("【数据库中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+ePoiId);
            }
            if(getData!=null&&getData.isEmpty()==false)
            {
                return getData.get(0);
            }
            else
            {
                return null;
            }
            
            
        }
        catch (Exception e)
        {
            return null;
        }
        
    }
    
    /**
     * 订单退单上传ERP(不用管ERP返回结果)
     * @param dao
     * @param companyNo
     * @param loadDocType
     * @param orderNo
     * @param opNo
     * @param opName
     * @param memo
     * @throws Exception
     */
    public static void orderReturnNotifyERP(DsmDAO dao,String companyNo,String loadDocType,String orderNo,String opNo,String opName,String refundBdate,String refundDatetime,String memo) throws Exception
    {
        try
        {
            if(opNo==null)
            {
                opNo = "";
            }
            if(opName==null)
            {
                opName = "";
            }
            if(memo==null)
            {
                memo = "";
            }
            String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sdateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            
            if(refundBdate==null||refundBdate.trim().isEmpty())
            {
                refundBdate = sdate;
            }
            if(refundDatetime==null||refundDatetime.trim().isEmpty())
            {
                refundDatetime = sdateTime;
            }
            
            String isRequestErp = PosPub.getPARA_SMS(dao, companyNo, "", "OrgOrderOnline");//订单修改退订是否调用ERP接口
            HelpTools.writelog_waimai("门店订单退款是否调用ERP接口参数值：ERP是否检核参数OrgOrderOnline="+isRequestErp);
            StringBuffer sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from tv_order");
            sqlbuf.append(" where companyno='"+companyNo+"'  and LOAD_DOCTYPE='"+ loadDocType+"' and orderno='"+orderNo+"' and rownum=1 ");
            String process_status ="";
            String shop ="";
            String shipType ="";
            String isShipcompany ="";
            
            List<Map<String, Object>> getQDataDetail=dao.executeQuerySQL(sqlbuf.toString(),null);
            if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
            {
                process_status = getQDataDetail.get(0).get("PROCESS_STATUS").toString();
                shop = getQDataDetail.get(0).get("SHOP").toString();
                shipType = getQDataDetail.get(0).get("SHIPTYPE").toString();
                isShipcompany = getQDataDetail.get(0).get("ISSHIPCOMPANY").toString();
            }
            else
            {
                HelpTools.writelog_waimai("门店订单退款是否调用ERP接口， 该符合条件的订单不存在,【无需调用ERP】查询sql:"+sqlbuf.toString()+" 单号orderNO="+orderNo);
                return;
            }
            
            if(process_status.equals("Y")==false)
            {
                HelpTools.writelog_waimai("门店订单退款是否调用ERP接口， 该订单还【未上传】无须调用ERP接口,单号orderNO="+orderNo);
                return;
            }
            
            if(isRequestErp!=null&&isRequestErp.toUpperCase().equals("Y")&&process_status.equals("Y"))
            {
                HelpTools.writelog_waimai("门店订单退款否调用ERP接口， 该订单【已上传】需要调用ERP接口orderstatuslog.create 单号orderNO="+orderNo);
                
                //t100req中的payload对象
                JSONObject payload = new JSONObject();
                // 自定义payload中的json结构
                JSONObject std_data = new JSONObject();
                JSONObject parameter = new JSONObject();
                
                JSONArray request = new JSONArray();
                JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
                
                
                String statusType = "1";//订单状态
                String updateStaus = "12";//已退单
                
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                // 给单头赋值
                header.put("o_companyNO", companyNo);
                header.put("customerNO", " ");
                header.put("o_organizationNO", shop);
                header.put("o_shopNO", shop);
                header.put("orderNO", orderNo);
                header.put("loadDocType", loadDocType);
                header.put("o_opNO", opNo);
                header.put("o_opName", opName);
                header.put("statusType", statusType);
                header.put("statusTypeName", statusTypeName);
                header.put("status", updateStaus);
                header.put("statusName", statusName);
                header.put("memo", memo);
                header.put("update_time",  refundDatetime);
                header.put("refundBdate", refundBdate);
                header.put("refundDatetime", refundDatetime);
                request.put(header);
                
                parameter.put("request", request);
                std_data.put("parameter", parameter);
                payload.put("std_data", std_data);
                String str = payload.toString();// 将json对象转换为字符串
                HelpTools.writelog_waimai("门店订单退款调用ERP接口 orderstatuslog.create请求："+str);
                String resbody="";
                try
                {
                    resbody=HttpSend.Send(str, "orderstatuslog.create", companyNo, shop,shop,orderNo);
                    HelpTools.writelog_waimai("门店订单退款调用ERP接口 orderstatuslog.create返回："+resbody);
                    if(resbody==null || resbody.isEmpty() )
                    {
                        return;
                    }
                    JSONObject jsonres = new JSONObject(resbody);
                    JSONObject std_data_res = jsonres.getJSONObject("std_data");
                    JSONObject execution_res = std_data_res.getJSONObject("execution");
                    
                    String code = execution_res.getString("code");
                    //String sqlcode = execution_res.getString("sqlcode");
                    
                    //String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");
                    String description ="";
                    if  (!execution_res.isNull("description") )
                    {
                        description = execution_res.getString("description");
                    }
                    if (code.equals("0"))
                    {
                        
                        try
                        {
                            // values
                            Map<String, DataValue> values = new HashMap<String, DataValue>();
                            DataValue v = new DataValue("Y", Types.VARCHAR);
                            values.put("refund_process_status", v);
                            DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR);
                            values.put("UPDATE_TIME", v1);
                            
                            // condition
                            Map<String, DataValue> conditions = new HashMap<String, DataValue>();
                            DataValue c1 = new DataValue(loadDocType, Types.VARCHAR);
                            conditions.put("LOAD_DOCTYPE", c1);
                            DataValue c2 = new DataValue(companyNo, Types.VARCHAR);
                            conditions.put("CompanyNo", c2);
                            DataValue c4 = new DataValue(orderNo, Types.VARCHAR);
                            conditions.put("ORDERNO", c4);
                            
                            dao.update("TV_ORDER", values, conditions);
                            
                            //传送成功后写一条日志
                            try
                            {/*
									 OrderStatusLogCreateReq req_log = new OrderStatusLogCreateReq();
									 req_log.setDatas(new ArrayList<OrderStatusLogCreateReq.level1Elm>());

									 //region订单状态
									 OrderStatusLogCreateReq.level1Elm onelv1 = req_log.new level1Elm();
									 onelv1.setCallback_status("0");
									 onelv1.setLoadDocType(loadDocType);

									 onelv1.setNeed_callback("Y");
									 onelv1.setNeed_notify("Y");
									 //String o_companyNO = o_companyNO;

									 onelv1.setO_companyNO(companyNo);

									 String opNO = opNo;

									 String o_opName = opName;

									 onelv1.setO_opName(o_opName);
									 onelv1.setO_opNO(opNO);

									 onelv1.setO_organizationNO(shop);
									 onelv1.setO_shopNO(shop);

									 onelv1.setOrderNO(orderNo);

									 statusType = "4";//

									 updateStaus = "9";//

									 onelv1.setStatusType(statusType);
									 onelv1.setStatus(updateStaus);
									 statusTypeNameObj = new StringBuilder();
									 statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);
									 statusTypeName = statusTypeNameObj.toString();
									 onelv1.setStatusTypeName(statusTypeName);
									 onelv1.setStatusName(statusName);

									 memo = "";
									 memo += statusName+"(订单退订上传)";

									 onelv1.setMemo(memo);
									 if (statusType.equals("2")) //物流配送信息对外
									 {
										 onelv1.setDisplay("1");
									 }
									 else
									 {
										 onelv1.setDisplay("0");
									 }

									 String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
									 onelv1.setUpdate_time(updateDatetime);


									 req_log.getDatas().add(onelv1);

								   String req_log_json ="";
								   try
								   {
								  	 ParseJson pj = new ParseJson();
								  	 req_log_json = pj.beanToJson(req_log);
								  	 pj=null;
								   }
								   catch(Exception e)
								   {

								   }
							  	 StringBuilder errorMessage2 = new StringBuilder();
							  	 boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage2);
	//						  	 if(nRet)
	//						  	 {
	//						  		 HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】"+" 订单号orderNO:"+req.getOrderNO());
	//						  	 }
	//						  	 else
	//						  	 {
	//						  		 HelpTools.writelog_waimai("【写表tv_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+req.getOrderNO());
	//						  	 }
							  	 //endregion

								 */}
                            catch (Exception  e)
                            {
                            
                            }
                            
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                            
                        }
                        
                        
                    }
                    else
                    {
                        HelpTools.writelog_waimai("门店订单退款调用ERP接口 orderstatuslog.create失败，ERP返回："+description);
                    }
                }
                catch (Exception e)
                {
                    HelpTools.writelog_waimai("门店订单退款调用ERP接口 orderstatuslog.create返回异常："+e.getMessage());
                }
            }
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            HelpTools.writelog_waimai("门店订单退款调用ERP接口 orderstatuslog.create返回异常："+e.getMessage()+" 单号orderNo="+orderNo);
        }
    }
    
    /**
     * 美团订单展示ID生成字符串
     * @param orderIdView
     * @return
     * @throws Exception
     */
    public static String getMTOrderIdView(String orderIdView) throws Exception
    {
        /*
         * 订单一维码转换方式如下： 格式：平台标志(1位字符) + 订单编码（N位字符）
         * 说明：将【订单展示id】转换成62进制后倒叙排列，并在转换字符串前追加一个M 示例： 订单展示id：66645381811439206
         * 条码对应的字符串：M8xKhRjFeV4 条形码编码方式：CODE 128 B字库
         */
        try
        {
            String resultStr = "";
            if (orderIdView == null || orderIdView.trim().isEmpty())
            {
                return "";
            }
            String MT_Flag = "M";
            // String change62 = new BigInteger(orderIdView, 10).toString(62);
            String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            int scale = 62;
            long num = new Long(orderIdView);
            StringBuilder sb = new StringBuilder();
            int remainder;
            while (num > scale - 1)
            {
                // 对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转字符串
                remainder = Long.valueOf(num % scale).intValue();
                sb.append(chars.charAt(remainder));
                // 除以进制数，获取下一个末尾数
                num = num / scale;
            }
            sb.append(chars.charAt(Long.valueOf(num).intValue()));
            //String change62 = sb.reverse().toString();
            resultStr = sb.toString();
            
            if (resultStr != null && resultStr.length() > 0)
            {
                resultStr = MT_Flag + resultStr;
            }
            
            return resultStr;
            
        } catch (Exception e)
        {
            return "";
        }
    }
    
    /**
     * 饿了么订单ID生成出差条码
     * @param orderIdView 订单id
     * @return
     * @throws Exception
     */
    public static String getELMOrderIdView(String orderIdView) throws Exception
    {
        /*
         * 订单一维码转换方式如下： 格式：平台标志(1位字符) + 订单编码（N位字符）
         * 说明：将19位订单号转化为58进制字符，并在转换字符串前追加一个E 示例： 订单展示id：5060312396537546206
         * 条码对应的字符串：EcKhdzSYiBb7 条形码编码方式：CODE 128 B字库
         */
        try
        {
            String resultStr = "";
            if (orderIdView == null || orderIdView.trim().isEmpty())
            {
                return "";
            }
            String ELM_Flag = "E";
            // String change62 = new BigInteger(orderIdView, 10).toString(62);
            //62进制排除4个，58进制
            //排除数字0，小写字母l,大写字母I、O
            String chars = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
            int scale = 58;
            long num = new Long(orderIdView);
            StringBuilder sb = new StringBuilder();
            int remainder;
            while (num > scale - 1)
            {
                // 对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转字符串
                remainder = Long.valueOf(num % scale).intValue();
                sb.append(chars.charAt(remainder));
                // 除以进制数，获取下一个末尾数
                num = num / scale;
            }
            sb.append(chars.charAt(Long.valueOf(num).intValue()));
            
            resultStr = sb.reverse().toString();
            
            if (resultStr != null && resultStr.length() > 0)
            {
                resultStr = ELM_Flag + resultStr;
            }
            
            return resultStr;
            
        } catch (Exception e)
        {
            return "";
        }
    }
    
    
    /**
     * 订单生成insert语句
     * @param orderList
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static ArrayList<DataProcessBean> GetInsertOrderCreat(List<order> orderList, StringBuffer errorMessage,List<Card> CardsInfo) throws Exception
    {
        errorMessage = new StringBuffer();
        ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
        writelog_waimai("【开始生成insert语句】");
        String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String curTime = new SimpleDateFormat("HHmmss").format(new Date());
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//数据库DATE类型
        for (order par : orderList)
        {
            String eId = par.geteId();
            String loadDocType = par.getLoadDocType();//渠道类型
            String channelId = par.getChannelId();//渠道编码
            String billType = par.getBillType();//单据类型（1：订单；-1：退订单）
            if(billType==null||billType.isEmpty())
            {
                billType = "1";
            }
            String orderNo = par.getOrderNo();//订单中心生成的订单号=外部传入的单号
            String loadDocOrderNo = par.getLoadDocOrderNo();//渠道单号,订单来源单号，外部传入得单号，保证唯一
            String companyId = par.getBelfirm();//公司别
            String machineNo = par.getMachineNo();//机台
            String sellNo = par.getSellNo();//大客户编号
            String workNo = par.getWorkNo();
            String squadNo = par.getSquadNo();
            String bDate = par.getbDate();
            if(bDate==null||bDate.isEmpty())
            {
                bDate = curDate;
                par.setbDate(bDate);
            }
            if(orderNo==null||orderNo.isEmpty())
            {
                orderNo = loadDocOrderNo;
                par.setOrderNo(loadDocOrderNo);
            }
            String shop = par.getShopNo();
            if(orderLoadDocType.WAIMAI.equals(loadDocType)||orderLoadDocType.OWNCHANNEL.equals(loadDocType))
            {
                par.setCreateDatetime(curDateTime);
            }
            String orderCreateDateTime =  par.getCreateDatetime();
            
            if(orderCreateDateTime==null||orderCreateDateTime.isEmpty())
            {
                orderCreateDateTime = curDateTime;
                par.setCreateDatetime(orderCreateDateTime);
            }
            
            String isMerPay = par.getIsMerPay();//配送费是否商家结算
            
            if(isMerPay==null||isMerPay.isEmpty()||isMerPay.equals("Y")==false)
            {
                isMerPay = "N";
            }
            
            
            try
            {
                String belfirm = par.getBelfirm();
                if(belfirm==null||belfirm.isEmpty())
                {
                    //公司别为空查询下单门店的公司别
                    if(shop!=null&&shop.trim().isEmpty()==false)
                    {
                        String sql_shop_belfirm = " select ORG_FORM,BELFIRM from dcp_org where EID='"+eId+"' and ORGANIZATIONNO='"+shop+"'";
                        HelpTools.writelog_waimai("根据下单门店查询所属公司别SQL="+sql_shop_belfirm);
                        try
                        {
                            List<Map<String, Object>> org_FormList = StaticInfo.dao.executeQuerySQL(sql_shop_belfirm, null);
                            
                            if(org_FormList!=null&&org_FormList.isEmpty()==false)
                            {
                                String org_Form = org_FormList.get(0).get("ORG_FORM").toString();
                                String belfirm_shop = org_FormList.get(0).get("BELFIRM").toString();
                                
                                if(org_Form!=null&&org_Form.equals("0"))	//如果下单门店 就是公司
                                {
                                    belfirm  = shop;
                                    
                                    par.setBelfirm(belfirm);
                                    
                                    HelpTools.writelog_waimai("根据下单门店查询门店组织类型ORG_FORM="+org_Form+",【本身就是公司】belfirm="+belfirm);
                                }
                                else
                                {
                                    belfirm  = belfirm_shop;
                                    par.setBelfirm(belfirm);
                                    
                                    HelpTools.writelog_waimai("根据下单门店查询门店组织类型ORG_FORM="+org_Form+",【对应的所属公司别】belfirm="+belfirm);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                    }
                    
                    
                }
                
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
            
            //region 判断下生产门店，是否总部
            if(par.getMachShopNo()!=null&&par.getMachShopNo().isEmpty()==false)
            {
                if(loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
                {
                    //外卖的不用管，不需要查询，浪费效能
                }
                else
                {
                    try
                    {
                        String sql_shop_machShop = "select * from dcp_org where eid='"+eId+"' and ORGANIZATIONNO='"+par.getMachShopNo()+"' ";
                        HelpTools.writelog_waimai("根据生产门店查询所属是否总部生产SQL="+sql_shop_machShop);
                        List<Map<String, Object>> getShopData = StaticInfo.dao.executeQuerySQL(sql_shop_machShop, null);
                        if(getShopData!=null&&getShopData.isEmpty()==false)
                        {
                            String org_form = getShopData.get(0).getOrDefault("ORG_FORM", "").toString();
                            if(org_form.equals("0"))
                            {
                                par.setIsShipCompany("Y");
                                HelpTools.writelog_waimai("根据生产门店查询门店组织类型ORG_FORM="+org_form+",【对应的是否总部生产】isShipCompany="+par.getIsShipCompany());
                            }
                            
                            
                        }
                        
                        
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                }
                
            }
            
            //endregion
            
            try
            {
                writelog_waimai("【开始生成insert语句】单号="+orderNo);
                
                //查询配送门店仓库
                updateOrderShippingNoWarehouseInfo(par,errorMessage);
                
                //套餐商品查询子商品
                updateOrderWithPackage(par, "", errorMessage);
                
                //由于增加了字段，给下默认值 顺便统计下tot_Qty
                try
                {
                    
                    //单头
                    if(par.getTot_Amt_merReceive()<0.01)
                    {
                        par.setTot_Amt_merReceive(par.getTot_Amt());
                    }
                    if(par.getTot_Amt_custPayReal()<0.01)
                    {
                        par.setTot_Amt_custPayReal(par.getTot_Amt());
                    }
                    
                    //付款
                    if(par.getPay()!=null&&par.getPay().isEmpty()==false)
                    {
                        for (orderPay payInfo : par.getPay())
                        {
                            if(Math.abs(payInfo.getMerDiscount())<0.01&&Math.abs(payInfo.getThirdDiscount())<0.01)
                            {
                                //商家折扣 和 平台折扣都是0 ，给下默认值，
                                BigDecimal pay = new BigDecimal("0");
                                try
                                {
                                    pay = new BigDecimal(payInfo.getPay());
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                BigDecimal changed = new BigDecimal("0");
                                try
                                {
                                    changed = new BigDecimal(payInfo.getChanged());
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                BigDecimal extra = new BigDecimal("0");
                                try
                                {
                                    extra = new BigDecimal(payInfo.getExtra());
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                
                                //if(payInfo.getMerReceive()<0.01)
                                {
                                    payInfo.setMerReceive(pay.subtract(changed).subtract(extra).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                                }
                                //if(payInfo.getCustPayReal()<0.01)//顾客实付可能真是0
                                {
                                    
                                    payInfo.setCustPayReal(pay.subtract(changed).subtract(extra).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                                }
                            }
                            
                            
                            
                            
                        }
                    }
                    
                    
                    BigDecimal tot_qty = new BigDecimal("0");
                    BigDecimal saleAmt = new BigDecimal("0");
                    BigDecimal saleDisc = new BigDecimal("0");
                    for (orderGoodsItem map : par.getGoodsList())
                    {
                        if(map.getAmt_merReceive()<0.01)
                        {
                            map.setAmt_merReceive(map.getAmt());
                        }
                        
                        if(map.getAmt_custPayReal()<0.01)
                        {
                            map.setAmt_custPayReal(map.getAmt());
                        }
                        
                        if(map.getPackageType()!=null&&map.getPackageType().equals("3"))
                        {
                            continue;
                        }
                        tot_qty = tot_qty.add(new BigDecimal(map.getQty())).setScale(2, BigDecimal.ROUND_HALF_UP);
                        saleAmt = saleAmt.add(new BigDecimal(map.getAmt()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        saleDisc = saleDisc.add(new BigDecimal(map.getDisc()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    
                    if(par.getTot_qty()<0.01)//如果前端传入了值，不用计算
                    {
                        par.setTot_qty(tot_qty.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    }
                    par.setSaleAmt(saleAmt.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    par.setSaleDisc(saleDisc.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
                
                par.setTotQty(par.getTot_qty());
                
                
                
                if(billType.equals("1"))
                {
                    posOrderPayDiscShareProcess(par, errorMessage);
                }
                //外卖折扣分摊
                waimaiOrderDiscShareProcess(par, errorMessage);
                
                //pos抹零分摊
                posOrderEraseAmtShareProcess(par, errorMessage);
                
                //pos存在订单没有完全付款的情况，重新计算单头的商户实收和顾客实付
                posOrderTotAmtMerReceiveProcess(par, errorMessage);
                
                
                orderFieldLengthProcess(par, errorMessage);
                
                orderInvoice invoiceDetail = par.getInvoiceDetail();
                if(invoiceDetail==null)
                {
                    invoiceDetail = new orderInvoice();
                }
                
                double longitude = 0;
                try
                {
                    longitude = Double.parseDouble(par.getLongitude());
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                double latitude = 0;
                try
                {
                    latitude = Double.parseDouble(par.getLatitude());
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
                String isApportion = par.getIsApportion();//是否已分摊过套餐
                
                if (isApportion==null||isApportion.isEmpty()||isApportion.equals("Y")==false)
                {
                    isApportion = "N";
                }
                
                String exceptionStatus = par.getExceptionStatus();//是否异常标识
                
                if (exceptionStatus==null||exceptionStatus.isEmpty()||exceptionStatus.equals("Y")==false)
                {
                    exceptionStatus = "N";
                }
                
                //配送结束时间为空，就默认配送开始时间
                if(par.getShipEndTime()==null||par.getShipEndTime().isEmpty())
                {
                    if(par.getShipStartTime()!=null)
                    {
                        par.setShipEndTime(par.getShipStartTime());
                    }
                }
                
                //获取下当天门店对应的最大流水号
                if(loadDocType.equals(orderLoadDocType.WAIMAI))
                {
                    int maxOrderSn = getMaxOrderSn(par);
                    par.setSn(maxOrderSn+"");
                    
                }
                //美团饿了么外卖订单商户实收类型，0-嘉华算法，1-店铺收入，其他渠道默认0
                if(par.getWaiMaiMerReceiveMode()==null||"1".equals(par.getWaiMaiMerReceiveMode())==false)
                {
                    par.setWaiMaiMerReceiveMode("0");
                }
                //饿了么降级订单标识，Y-是，N-否，默认N
                if(par.getDowngraded()==null||"Y".equals(par.getDowngraded())==false)
                {
                    par.setDowngraded("N");
                }
                //詹记意向单
                if(par.getIsIntention()==null||"Y".equals(par.getIsIntention())==false)
                {
                    par.setIsIntention("N");
                }
                
                /**********************************订单单头*********************************/
                String[] columns_order =
                        { "eid", "billtype", "orderno", "manualno", "loaddoctype", "channelid", "loaddocbilltype",
                                "loaddocorderno", "outdoctype", "outdoctypename", "ordershop", "ordershopname", "order_sn",
                                "machine", "ver_num", "squadno", "workno", "opno", "isorgorder",
                                "isshipcompany", "sellcredit", "customer", "customername", "isbook", "shop", "shopname", "machshop",
                                "machshopname", "shippingshop", "shippingshopname", "latitude", "longitude", "belfirm", "contman",
                                "conttel", "getman", "getmantel", "getmanemail", "province", "city", "county", "street", "address",
                                "zipcode", "shiptype", "shipdate", "shipstarttime", "shipendtime", "deliverytype", "deliveryno",
                                "deliverystatus", "subdeliverycompanyno", "subdeliverycompanyname", "delname", "deltelephone",
                                "tot_qty", "tot_oldamt", "erase_amt", "tot_disc", "tot_amt", "tot_uamt", "payamt",
                                "writeoffamt", "refundamt", "packagefee",
                                "totshipfee", "rshipfee", "shipfee", "shopshareshipfee", "servicecharge", "incomeamt",
                                "seller_disc", "platform_disc", "passport", "freecode", "buyerguino", "carriercode",
                                "carriershowid", "carrierhiddenid", "lovecode", "isinvoice", "invoicetype", "invoicetitle",
                                "taxregnumber", "invmemo", "invoicedate", "invoperatetype", "rebateno", "invsplittype",
                                "mealnumber", "cardno", "memberid", "membername", "pointqty", "memberpayno", "sellno",
                                "eccustomerno", "currencyno", "memo", "promemo", "delmemo",
                                "status","refundstatus", "paystatus","PRODUCTSTATUS", "stime", "create_datetime", "complete_datetime", "bdate" ,
                                "AUTODELIVERY","DELIVERYBUSINESSTYPE","ISCHARGEORDER","REQUESTID","ISAPPORTION","EXCEPTIONSTATUS",
                                "TOT_AMT_MERRECEIVE","TOT_AMT_CUSTPAYREAL","TOT_DISC_MERRECEIVE","TOT_DISC_CUSTPAYREAL",
                                "ISMERPAY","TABLEWAREQTY","PREPARATIONSTATUS","ORDERCODEVIEW","DOWNGRADED","WAIMAIMERRECEIVEMODE",
                                "SALEDISC","SALEAMT","PARTITION_DATE","PROCESS_STATUS","CANMODIFY",
                                "UPDATE_TIME","TRAN_TIME","GROUPBUYING","PARTNERMEMBER","DELIVERYMONEY","SUPERZONEMONEY",
                                "URGENTMONEY","HEADORDERNO","ISHAVECARD","ISCARDPRINT","LINENO","LINENAME","ISINTENTION","ADDORDERORIGINNO"};


                
                DataValue[] insValue_order = new DataValue[] {
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(billType, Types.VARCHAR),
                        new DataValue(orderNo, Types.VARCHAR),
                        new DataValue(par.getManualNo(), Types.VARCHAR),
                        new DataValue(loadDocType, Types.VARCHAR),
                        new DataValue(channelId, Types.VARCHAR),
                        new DataValue(par.getLoadDocBillType(), Types.VARCHAR),
                        new DataValue(loadDocOrderNo, Types.VARCHAR),
                        new DataValue(par.getOutDocType(), Types.VARCHAR),
                        new DataValue(par.getOutDocTypeName(), Types.VARCHAR),
                        new DataValue(par.getOrderShop(), Types.VARCHAR),//ordershop
                        new DataValue(par.getOrderShopName(), Types.VARCHAR),//ordershopname
                        new DataValue(par.getSn(), Types.VARCHAR),
                        new DataValue(par.getMachineNo(), Types.VARCHAR),
                        new DataValue(par.getVerNum(), Types.VARCHAR),
                        new DataValue(par.getSquadNo(), Types.VARCHAR),
                        new DataValue(par.getWorkNo(), Types.VARCHAR),
                        new DataValue(par.getOpNo(), Types.VARCHAR),
                        new DataValue(par.getIsOrgOrder(), Types.VARCHAR),//isorgorder 是否节日订单没有对应节点，默认N
                        new DataValue(par.getIsShipCompany(), Types.VARCHAR),//isshipcompany 是否总部（生产）
                        new DataValue(par.getSellCredit(), Types.VARCHAR),
                        new DataValue(par.getCustomer(), Types.VARCHAR),
                        new DataValue(par.getCustomerName(), Types.VARCHAR),
                        new DataValue(par.getIsBook(), Types.VARCHAR),
                        new DataValue(par.getShopNo(), Types.VARCHAR),
                        new DataValue(par.getShopName(), Types.VARCHAR),
                        new DataValue(par.getMachShopNo(), Types.VARCHAR),
                        new DataValue(par.getMachShopName(), Types.VARCHAR),
                        new DataValue(par.getShippingShopNo(), Types.VARCHAR),
                        new DataValue(par.getShippingShopName(), Types.VARCHAR),
                        new DataValue(latitude, Types.VARCHAR),
                        new DataValue(longitude, Types.VARCHAR),
                        new DataValue(par.getBelfirm(), Types.VARCHAR),
                        new DataValue(par.getContMan(), Types.VARCHAR),
                        new DataValue(par.getContTel(), Types.VARCHAR),
                        new DataValue(par.getGetMan(), Types.VARCHAR),
                        new DataValue(par.getGetManTel(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(par.getProvince(), Types.VARCHAR),
                        new DataValue(par.getCity(), Types.VARCHAR),
                        new DataValue(par.getCounty(), Types.VARCHAR),
                        new DataValue(par.getStreet(), Types.VARCHAR),
                        new DataValue(par.getAddress(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),//zipcode
                        new DataValue(par.getShipType(), Types.VARCHAR),
                        new DataValue(par.getShipDate(), Types.VARCHAR),
                        new DataValue(par.getShipStartTime(), Types.VARCHAR),
                        new DataValue(par.getShipEndTime(), Types.VARCHAR),
                        new DataValue(par.getDeliveryType(), Types.VARCHAR),//deliverytype
                        new DataValue(par.getDeliveryNo(), Types.VARCHAR),//deliveryno
                        new DataValue(par.getDeliveryStatus(), Types.VARCHAR),//deliverystutas
                        new DataValue("", Types.VARCHAR),//subdeliverycompanyno
                        new DataValue("", Types.VARCHAR),//subdeliverycompanyname
                        new DataValue("", Types.VARCHAR),//delname
                        new DataValue("", Types.VARCHAR),//deltelephone
                        new DataValue(par.getTot_qty(), Types.VARCHAR),
                        new DataValue(par.getTot_oldAmt(), Types.VARCHAR),
                        new DataValue(par.getEraseAmt(), Types.VARCHAR),
                        new DataValue(par.getTotDisc(), Types.VARCHAR),
                        new DataValue(par.getTot_Amt(), Types.VARCHAR),
                        new DataValue(par.getTot_uAmt(), Types.VARCHAR),
                        new DataValue(par.getPayAmt(), Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),//writeoffamt
                        new DataValue("0", Types.VARCHAR),//refundamt
                        new DataValue(par.getPackageFee(), Types.VARCHAR),
                        new DataValue(par.getTot_shipFee(), Types.VARCHAR),
                        new DataValue(par.getRshipFee(), Types.VARCHAR),
                        new DataValue(par.getShipFee(), Types.VARCHAR),
                        new DataValue(par.getShopShareShipfee(), Types.VARCHAR),
                        new DataValue(par.getServiceCharge(), Types.VARCHAR),
                        new DataValue(par.getIncomeAmt(), Types.VARCHAR),
                        new DataValue(par.getSellerDisc(), Types.VARCHAR),
                        new DataValue(par.getPlatformDisc(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getPassPort(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getFreeCode(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getBuyerGuiNo(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getCarrierCode(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getCarrierShowId(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getCarrierHiddenId(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getLoveCode(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getIsInvoice(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getInvoiceType(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getInvoiceTitle(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getTaxRegNumber(), Types.VARCHAR),
                        new DataValue(invoiceDetail.getInvMemo(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),//invoicedate
                        new DataValue(invoiceDetail.getInvOperationType(), Types.VARCHAR),//invoperatetype
                        new DataValue("", Types.VARCHAR),//rebateno
                        new DataValue(invoiceDetail.getInvSplitType(), Types.VARCHAR),
                        new DataValue(par.getMealNumber(), Types.VARCHAR),
                        new DataValue(par.getCardNo(), Types.VARCHAR),
                        new DataValue(par.getMemberId(), Types.VARCHAR),
                        new DataValue(par.getMemberName(), Types.VARCHAR),
                        new DataValue(par.getPointQty(), Types.VARCHAR),
                        new DataValue(par.getMemberPayNo(), Types.VARCHAR),
                        new DataValue(par.getSellNo(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),//eccustomerno
                        new DataValue("", Types.VARCHAR),//currencyno
                        new DataValue(par.getMemo(), Types.VARCHAR),
                        new DataValue(par.getProMemo(), Types.VARCHAR),
                        new DataValue(par.getDelMemo(), Types.VARCHAR),
                        new DataValue(par.getStatus(), Types.VARCHAR),
                        new DataValue("1", Types.VARCHAR),//refundstatus 新建时，默认1
                        new DataValue(par.getPayStatus(), Types.VARCHAR),
                        new DataValue(par.getProductStatus(), Types.VARCHAR),
                        new DataValue(curDateTime, Types.VARCHAR),//stime
                        new DataValue(par.getCreateDatetime(), Types.VARCHAR),//create_datetime
                        new DataValue("", Types.VARCHAR),//complete_datetime
                        new DataValue(par.getbDate(), Types.VARCHAR),
                        new DataValue(par.getAutoDelivery()==null?"N":par.getAutoDelivery(), Types.VARCHAR),//AUTODELIVERY
                        new DataValue(par.getDeliveryBusinessType(), Types.VARCHAR),//配送业务类型（1随车 2代发）
                        new DataValue(par.getIsChargeOrder()==null?"N":par.getIsChargeOrder(), Types.VARCHAR),
                        new DataValue(par.getRequestId(), Types.VARCHAR),
                        new DataValue(isApportion, Types.VARCHAR),
                        new DataValue(exceptionStatus, Types.VARCHAR),
                        new DataValue(par.getTot_Amt_merReceive(), Types.VARCHAR),
                        new DataValue(par.getTot_Amt_custPayReal(), Types.VARCHAR),
                        new DataValue(par.getTotDisc_merReceive(), Types.VARCHAR),
                        new DataValue(par.getTotDisc_custPayReal(), Types.VARCHAR),
                        new DataValue(isMerPay, Types.VARCHAR),
                        new DataValue(par.getTablewareQty(), Types.VARCHAR),
                        new DataValue(par.getPreparationStatus(), Types.VARCHAR),
                        new DataValue(par.getOrderCodeView(), Types.VARCHAR),
                        new DataValue(par.getDowngraded(), Types.VARCHAR),
                        new DataValue(par.getWaiMaiMerReceiveMode(), Types.VARCHAR),
                        new DataValue(par.getSaleDisc(), Types.VARCHAR),
                        new DataValue(par.getSaleAmt(), Types.VARCHAR),
                        new DataValue(par.getbDate(), Types.NUMERIC),//分区字段
                        new DataValue(par.getProcess_status(), Types.VARCHAR),
                        new DataValue(par.getCanModify(), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        new DataValue(par.getGroupBuying(), Types.VARCHAR),
                        new DataValue(par.getPartnerMember()==null?"":par.getPartnerMember(),Types.VARCHAR),
                        
                        //【ID1037561】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单下订增加配送费，超区费，加急费同时支持费用补录---服务  by jinzma 20231206
                        new DataValue(par.getDeliveryMoney(), Types.VARCHAR),
                        new DataValue(par.getSuperZoneMoney(), Types.VARCHAR),
                        new DataValue(par.getUrgentMoney(), Types.VARCHAR),
//                        new DataValue("", Types.VARCHAR),
                        new DataValue(par.getHeadOrderNo(), Types.VARCHAR),

                        //【ID1037551】【热点商品3.0】热点食品需求（品牌名称：布朗先生）---订单打印贺卡及配送路线选择---服务 by jinzma 20231229
                        new DataValue(par.getIsHaveCard(), Types.VARCHAR),
                        new DataValue(par.getIsCardPrint(), Types.VARCHAR),
                        new DataValue(par.getLineNo(), Types.VARCHAR),
                        new DataValue(par.getLineName(), Types.VARCHAR),
                        new DataValue(par.getIsIntention(), Types.VARCHAR),
                        new DataValue(par.getAddOrderOriginNo(), Types.VARCHAR),//追加子单对应的来源订单

                };
                
                InsBean ib_order = new InsBean("DCP_ORDER", columns_order);//分区字段已处理
                ib_order.addValues(insValue_order);
                DataPB.add(new DataProcessBean(ib_order));
                
                /**********************************记录促销次数*********************************/
                List<String> promNoList = new ArrayList<String>();
                
                /**********************************订单商品明细*********************************/
                List<orderGoodsItem> goodsItemList = par.getGoodsList();
                if(goodsItemList!=null)
                {
                    
                    String[] columns_goods =
                            { "eid", "orderno", "item", "loaddoctype", "channelid", "pluno", "pluname", "plubarcode", "featureno",
                                    "featurename", "goodsurl", "specname", "attrname", "sunit", "sunitname", "warehouse",
                                    "warehousename", "skuid", "gift", "giftsourceserialno", "giftreason", "goodsgroup", "packagetype",
                                    "packagemitem", "toppingtype", "toppingmitem", "oitem", "pickqty", "rqty", "rcqty", "shopqty",
                                    "boxnum", "boxprice", "qty", "oldprice", "oldamt", "price", "disc", "amt", "incltax", "taxcode",
                                    "taxtype", "invitem","invsplittype", "sellerno", "sellername", "accno", "counterno", "coupontype", "couponcode",
                                    "sourcecode", "ismemo", "stime","VIRTUAL","DISC_MERRECEIVE","AMT_MERRECEIVE",
                                    "DISC_CUSTPAYREAL","AMT_CUSTPAYREAL","PREPARATIONSTATUS","PARTITION_DATE","SPECNAME_ORIGIN","ATTRNAME_ORIGIN","FLAVORSTUFFDETAIL" };
                    
                    for (orderGoodsItem goodsItem : goodsItemList)
                    {
                        //数据库长度截取
                        try
                        {
                            if(goodsItem.getPluNo()!=null&&goodsItem.getPluNo().length()>40)
                            {
                                goodsItem.setPluNo(goodsItem.getPluNo().substring(0,40));
                            }
                            if(goodsItem.getPluBarcode()!=null&&goodsItem.getPluBarcode().length()>40)
                            {
                                goodsItem.setPluBarcode(goodsItem.getPluBarcode().substring(0,40));
                            }
                            if(goodsItem.getPluName()!=null&&goodsItem.getPluName().length()>120)
                            {
                                goodsItem.setPluName(goodsItem.getPluName().substring(0,120));
                            }
                            if(goodsItem.getSkuId()!=null&&goodsItem.getSkuId().length()>120)
                            {
                                goodsItem.setSkuId(goodsItem.getSkuId().substring(0,120));
                            }
                            if(goodsItem.getsUnit()!=null&&goodsItem.getsUnit().length()>32)
                            {
                                goodsItem.setsUnit(goodsItem.getsUnit().substring(0,32));
                            }
                            if(goodsItem.getsUnitName()!=null&&goodsItem.getsUnitName().length()>100)
                            {
                                goodsItem.setsUnitName(goodsItem.getsUnitName().substring(0,100));
                            }
                            if(goodsItem.getFeatureNo()!=null&&goodsItem.getFeatureNo().length()>64)
                            {
                                goodsItem.setFeatureNo(goodsItem.getFeatureNo().substring(0,64));
                            }
                            if(goodsItem.getFeatureName()!=null&&goodsItem.getFeatureName().length()>64)
                            {
                                goodsItem.setFeatureName(goodsItem.getFeatureName().substring(0,64));
                            }
                            if(goodsItem.getSpecName()!=null&&goodsItem.getSpecName().length()>120)
                            {
                                goodsItem.setSpecName(goodsItem.getSpecName().substring(0,120));
                            }
                            if(goodsItem.getAttrName()!=null&&goodsItem.getAttrName().length()>120)
                            {
                                goodsItem.setAttrName(goodsItem.getAttrName().substring(0,120));
                            }
                            if(goodsItem.getSpecName_origin()!=null&&goodsItem.getSpecName_origin().length()>120)
                            {
                                goodsItem.setSpecName_origin(goodsItem.getSpecName_origin().substring(0,120));
                            }
                            if(goodsItem.getAttrName_origin()!=null&&goodsItem.getAttrName_origin().length()>120)
                            {
                                goodsItem.setAttrName_origin(goodsItem.getAttrName_origin().substring(0,120));
                            }
                            if(goodsItem.getFlavorStuffDetail()!=null&&goodsItem.getFlavorStuffDetail().length()>128)
                            {
                                goodsItem.setFlavorStuffDetail(goodsItem.getFlavorStuffDetail().substring(0,128));
                            }
                            //除了饿了么，其他的都是等于自己
                            if (!orderLoadDocType.ELEME.equals(loadDocType))
                            {
                                goodsItem.setSpecName_origin(goodsItem.getSpecName());
                                goodsItem.setAttrName_origin(goodsItem.getAttrName());
                            }
                            if(goodsItem.getWarehouse()!=null&&goodsItem.getWarehouse().length()>32)
                            {
                                goodsItem.setWarehouse(goodsItem.getWarehouse().substring(0,32));
                            }
                            if(goodsItem.getWarehouseName()!=null&&goodsItem.getWarehouseName().length()>64)
                            {
                                goodsItem.setWarehouseName(goodsItem.getWarehouseName().substring(0,64));
                            }
                            if(goodsItem.getSellerNo()!=null&&goodsItem.getSellerNo().length()>32)
                            {
                                goodsItem.setSellerNo(goodsItem.getSellerNo().substring(0,32));
                            }
                            if(goodsItem.getSellerName()!=null&&goodsItem.getSellerName().length()>64)
                            {
                                goodsItem.setSellerName(goodsItem.getSellerName().substring(0,64));
                            }
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                        
                        
                        
                        goodsItem.setIsMemo("N");
                        List<orderGoodsItemMessage> goodsMemoList = goodsItem.getMessages();
                        if(goodsMemoList!=null&&goodsMemoList.size()>0)
                        {
                            goodsItem.setIsMemo("Y");
                        }
                        
                        String featureNo = goodsItem.getFeatureNo();
                        if(featureNo==null||featureNo.isEmpty())
                        {
                            featureNo = " ";//默认空格
                        }
                        String virtual = goodsItem.getVirtual();
                        if(virtual==null||virtual.equals("Y")==false)
                        {
                            virtual = "N";
                        }
                        
                        DataValue[] insValue_good = new DataValue[] {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(goodsItem.getItem(), Types.VARCHAR),
                                new DataValue(loadDocType, Types.VARCHAR),
                                new DataValue(channelId, Types.VARCHAR),
                                new DataValue(goodsItem.getPluNo(), Types.VARCHAR),
                                new DataValue(goodsItem.getPluName(), Types.VARCHAR),
                                new DataValue(goodsItem.getPluBarcode(), Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(goodsItem.getFeatureName(), Types.VARCHAR),//ordershop
                                new DataValue(goodsItem.getGoodsUrl(), Types.VARCHAR),//ordershopname
                                new DataValue(goodsItem.getSpecName(), Types.VARCHAR),
                                new DataValue(goodsItem.getAttrName(), Types.VARCHAR),
                                new DataValue(goodsItem.getsUnit(), Types.VARCHAR),
                                new DataValue(goodsItem.getsUnitName(), Types.VARCHAR),
                                new DataValue(goodsItem.getWarehouse(), Types.VARCHAR),//warehouse
                                new DataValue(goodsItem.getWarehouseName(), Types.VARCHAR),//warehousename
                                new DataValue(goodsItem.getSkuId(), Types.VARCHAR),
                                new DataValue(goodsItem.getGift(), Types.VARCHAR),
                                new DataValue(goodsItem.getGiftSourceSerialNo(), Types.VARCHAR),
                                new DataValue(goodsItem.getGiftReason(), Types.VARCHAR),
                                new DataValue(goodsItem.getGoodsGroup(), Types.VARCHAR),
                                new DataValue(goodsItem.getPackageType(), Types.VARCHAR),
                                new DataValue(goodsItem.getPackageMitem(), Types.VARCHAR),
                                new DataValue(goodsItem.getToppingType(), Types.VARCHAR),
                                new DataValue(goodsItem.getToppingMitem(), Types.VARCHAR),
                                new DataValue(goodsItem.getoItem(), Types.VARCHAR),
                                new DataValue(goodsItem.getPickQty(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),//rqty
                                new DataValue("0", Types.VARCHAR),//rcqty
                                new DataValue(goodsItem.getShopQty(), Types.VARCHAR),
                                new DataValue(goodsItem.getBoxNum(), Types.VARCHAR),
                                new DataValue(goodsItem.getBoxPrice(), Types.VARCHAR),
                                new DataValue(goodsItem.getQty(), Types.VARCHAR),
                                new DataValue(goodsItem.getOldPrice(), Types.VARCHAR),
                                new DataValue(goodsItem.getOldAmt(), Types.VARCHAR),
                                new DataValue(goodsItem.getPrice(), Types.VARCHAR),//price
                                new DataValue(goodsItem.getDisc(), Types.VARCHAR),
                                new DataValue(goodsItem.getAmt(), Types.VARCHAR),
                                new DataValue(goodsItem.getInclTax(), Types.VARCHAR),
                                new DataValue(goodsItem.getTaxCode(), Types.VARCHAR),
                                new DataValue(goodsItem.getTaxType(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),//invitem
                                new DataValue(goodsItem.getInvSplitType(), Types.VARCHAR),//invsplittype
                                new DataValue(goodsItem.getSellerNo(), Types.VARCHAR),
                                new DataValue(goodsItem.getSellerName(), Types.VARCHAR),
                                new DataValue(goodsItem.getAccNo(), Types.VARCHAR),
                                new DataValue(goodsItem.getCounterNo(), Types.VARCHAR),
                                new DataValue(goodsItem.getCouponType(), Types.VARCHAR),
                                new DataValue(goodsItem.getCouponCode(), Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),//sourcecode
                                new DataValue(goodsItem.getIsMemo(), Types.VARCHAR),
                                new DataValue(curDateTime, Types.VARCHAR),
                                new DataValue(virtual, Types.VARCHAR),
                                new DataValue(goodsItem.getDisc_merReceive(), Types.VARCHAR),
                                new DataValue(goodsItem.getAmt_merReceive(), Types.VARCHAR),
                                new DataValue(goodsItem.getDisc_custPayReal(), Types.VARCHAR),
                                new DataValue(goodsItem.getAmt_custPayReal(), Types.VARCHAR),
                                new DataValue(goodsItem.getPreparationStatus(), Types.VARCHAR),
                                new DataValue(par.getbDate(), Types.NUMERIC),//分区字段
                                new DataValue(goodsItem.getSpecName_origin(), Types.VARCHAR),
                                new DataValue(goodsItem.getAttrName_origin(), Types.VARCHAR),
                                new DataValue(goodsItem.getFlavorStuffDetail(), Types.VARCHAR),
                        };
                        
                        InsBean ib_goods = new InsBean("DCP_ORDER_DETAIL", columns_goods);//分区字段已处理
                        ib_goods.addValues(insValue_good);
                        DataPB.add(new DataProcessBean(ib_goods));
                        
                        
                        /*******************商品备注************************/
                        if(goodsMemoList!=null&&goodsMemoList.size()>0)
                        {
                            
                            String[] columns_goodsMemo =
                                    { "eid", "orderno", "SHOPID", "OITEM", "ITEM", "MEMONAME","MEMOTYPE","MEMO"};
                            
                            int goodsMemoItem = 0;
                            for (orderGoodsItemMessage  goodsMessage : goodsMemoList)
                            {
                                try
                                {
                                    if(goodsMessage.getMsgType()!=null&&goodsMessage.getMsgType().length()>10)
                                    {
                                        goodsMessage.setMsgType(goodsMessage.getMsgType().substring(0,10));
                                    }
                                    if(goodsMessage.getMsgName()!=null&&goodsMessage.getMsgName().length()>255)
                                    {
                                        goodsMessage.setMsgName(goodsMessage.getMsgName().substring(0,255));
                                    }
                                    if(goodsMessage.getMessage()!=null&&goodsMessage.getMessage().length()>255)
                                    {
                                        goodsMessage.setMessage(goodsMessage.getMessage().substring(0,255));
                                    }
                                }
                                catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                
                                goodsMemoItem++;
                                DataValue[] insValue_goodsMemo = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(orderNo, Types.VARCHAR),
                                        new DataValue(shop, Types.VARCHAR),
                                        new DataValue(goodsItem.getItem(), Types.VARCHAR),
                                        new DataValue(goodsMemoItem, Types.VARCHAR),
                                        new DataValue(goodsMessage.getMsgName(), Types.VARCHAR),
                                        new DataValue(goodsMessage.getMsgType(), Types.VARCHAR),
                                        new DataValue(goodsMessage.getMessage(), Types.VARCHAR),
                                };
                                
                                InsBean ib_goodsMemo = new InsBean("DCP_ORDER_DETAIL_MEMO", columns_goodsMemo);
                                ib_goodsMemo.addValues(insValue_goodsMemo);
                                DataPB.add(new DataProcessBean(ib_goodsMemo));
                            }
                        }
                        
                        /*********************商品折扣**************************/
                        List<orderGoodsItemAgio> goodsAgioList = goodsItem.getAgioInfo();
                        
                        if(goodsAgioList!=null&&goodsAgioList.size()>0)
                        {
                            int goodsAgioItem = 0;
                            String[] columns_goodsAgio =
                                    { "eid", "orderno", "MITEM", "ITEM", "QTY", "AMT","INPUTDISC","REALDISC","DISC","DCTYPE","DCTYPENAME","PMTNO","GIFTCTF","GIFTCTFNO","BSNO",
                                            "DISC_MERRECEIVE","DISC_CUSTPAYREAL","PARTITION_DATE"};
                            for (orderGoodsItemAgio agio : goodsAgioList)
                            {
                                try
                                {
                                    /**************促销单号记录***************/
                                    if (loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID))
                                    {
                                        if (agio.getPmtNo()!=null&&agio.getPmtNo().isEmpty()==false)
                                        {
                                            if (promNoList.contains(agio.getPmtNo())==false)
                                            {
                                                promNoList.add(agio.getPmtNo());
                                            }
                                        }
                                    }
                                    if(agio.getDcType()!=null&&agio.getDcType().length()>32)
                                    {
                                        agio.setDcType(agio.getDcType().substring(0,32));
                                    }
                                    if(agio.getDcTypeName()!=null&&agio.getDcTypeName().length()>64)
                                    {
                                        agio.setDcTypeName(agio.getDcTypeName().substring(0,64));
                                    }
                                    if(agio.getPmtNo()!=null&&agio.getPmtNo().length()>32)
                                    {
                                        agio.setPmtNo(agio.getPmtNo().substring(0,32));
                                    }
                                    if(agio.getGiftCtf()!=null&&agio.getGiftCtf().length()>32)
                                    {
                                        agio.setGiftCtf(agio.getGiftCtf().substring(0,32));
                                    }
                                    if(agio.getGiftCtfNo()!=null&&agio.getGiftCtfNo().length()>32)
                                    {
                                        agio.setGiftCtfNo(agio.getGiftCtfNo().substring(0,32));
                                    }
                                    if(agio.getBsNo()!=null&&agio.getBsNo().length()>32)
                                    {
                                        agio.setBsNo(agio.getBsNo().substring(0,32));
                                    }
                                    
                                }
                                catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                goodsAgioItem++;
                                DataValue[] insValue_goodsAgio = new DataValue[] {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(orderNo, Types.VARCHAR),
                                        new DataValue(goodsItem.getItem(), Types.VARCHAR),
                                        new DataValue(goodsAgioItem, Types.VARCHAR),
                                        new DataValue(agio.getQty(), Types.VARCHAR),
                                        new DataValue(agio.getAmt(), Types.VARCHAR),
                                        new DataValue(agio.getInputDisc(), Types.VARCHAR),
                                        new DataValue(agio.getRealDisc(), Types.VARCHAR),
                                        new DataValue(agio.getDisc(), Types.VARCHAR),
                                        new DataValue(agio.getDcType(), Types.VARCHAR),
                                        new DataValue(agio.getDcTypeName(), Types.VARCHAR),
                                        new DataValue(agio.getPmtNo(), Types.VARCHAR),
                                        new DataValue(agio.getGiftCtf(), Types.VARCHAR),
                                        new DataValue(agio.getGiftCtfNo(), Types.VARCHAR),
                                        new DataValue(agio.getBsNo(), Types.VARCHAR),
                                        new DataValue(agio.getDisc_merReceive(), Types.VARCHAR),
                                        new DataValue(agio.getDisc_custPayReal(), Types.VARCHAR),
                                        new DataValue(par.getbDate(), Types.NUMERIC),//分区字段
                                };
                                
                                InsBean ib_goodsAgio = new InsBean("DCP_ORDER_DETAIL_AGIO", columns_goodsAgio);//分区字段已处理
                                ib_goodsAgio.addValues(insValue_goodsAgio);
                                DataPB.add(new DataProcessBean(ib_goodsAgio));
                                
                            }
                            
                        }
                    }
                    
                    
                    
                }
                
                
                /**********************************订单付款明细*********************************/
                
                String sourcebilltype = "Order";//来源单据类型：Order-订单TableRsv-桌台预订
                
                List<orderPay> payList = par.getPay();
                if(payList!=null&&payList.size()>0)
                {
                    boolean isNeedInsert_DCP_STATISTIC_INFO = false;
                    boolean isNeedInsert_DCP_CUSTOMER_CREDIT_DETAIL = false;//是否写赊销表
                    if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
                    {
                        isNeedInsert_DCP_CUSTOMER_CREDIT_DETAIL = true;//是否写赊销表
                        //2.0同步过来的订单不需要写了 标记赋值在job里面
                        if ("FROM-2.0".equals(par.getVerNum()))
                        {
                            isNeedInsert_DCP_CUSTOMER_CREDIT_DETAIL = false;
                            writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，版本号VerNum="+par.getVerNum()+",不需要写【交班统计信息表DCP_STATISTIC_INFO】，单号="+orderNo);
                        }
                        else if (orderLoadDocType.OWNCHANNEL.equals(loadDocType))
                        {

                        }
                        else
                        {
                            isNeedInsert_DCP_STATISTIC_INFO = true;
                            writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【交班统计信息表DCP_STATISTIC_INFO】，单号="+orderNo);
                        }
                        
                    }
                    
                    String payBillNo = UUID.randomUUID().toString().replace("-", "");//收款单号
                    
                    BigDecimal pay_tot = new BigDecimal("0");
                    BigDecimal extra_tot = new BigDecimal("0");
                    BigDecimal changed_tot = new BigDecimal("0");
                    BigDecimal writeoffamt_tot = new BigDecimal("0");//冲销金额
                    BigDecimal lackamt_tot = new BigDecimal("0");//未冲销金额
                    
                    String[] columns_pay_detail =
                            { "eid", "billno", "item", "billdate", "bdate", "sourcebilltype", "sourcebillno", "loaddoctype",
                                    "channelid", "paycode", "paycodeerp", "payname", "order_paycode", "isonlinepay", "pay",
                                    "paydiscamt", "payamt1", "payamt2", "descore", "cttype", "cardno", "cardbeforeamt", "cardremainamt",
                                    "couponqty", "isverification", "extra", "changed", "paysernum", "serialno", "refno", "teriminalno",
                                    "caninvoice", "writeoffamt", "authcode","FUNCNO","PAYDOCTYPE","SENDPAY","paytype",
                                    "MERDISCOUNT","MERRECEIVE","THIRDDISCOUNT","CUSTPAYREAL","COUPONMARKETPRICE","COUPONPRICE",
                                    "mobile","PAYCHANNELCODE","CHARGEAMOUNT","PARTITION_DATE","GAINCHANNEL","GAINCHANNELNAME"};
                    for (orderPay payItem : payList)
                    {
                        try
                        {
                            if(payItem.getPayName()!=null&&payItem.getPayName().length()>120)
                            {
                                payItem.setPayName(payItem.getPayName().substring(0, 120));
                            }
                            if(payItem.getOrder_payCode()!=null&&payItem.getOrder_payCode().length()>100)
                            {
                                payItem.setOrder_payCode(payItem.getOrder_payCode().substring(0, 100));
                            }
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                        BigDecimal pay = new BigDecimal("0");
                        try
                        {
                            pay = new BigDecimal(payItem.getPay());
                        } catch (Exception e)
                        {
                        }
                        BigDecimal extra = new BigDecimal("0");
                        try
                        {
                            extra = new BigDecimal(payItem.getExtra());
                        } catch (Exception e)
                        {
                        }
                        BigDecimal changed= new BigDecimal("0");
                        try
                        {
                            changed = new BigDecimal(payItem.getChanged());
                        } catch (Exception e)
                        {
                        }
                        BigDecimal writeoffamt= new BigDecimal("0");
                        try
                        {
                            //writeoffamt = new BigDecimal(payItem.());
                        } catch (Exception e)
                        {
                        }
                        BigDecimal lackamt= new BigDecimal("0");
                        try
                        {
                            //lackamt = new BigDecimal(payItem.getl());
                        } catch (Exception e)
                        {
                        }
                        
                        //收款金额 写交班流水表
                        BigDecimal p_amt=pay.subtract(changed);
                        
                        pay_tot = pay_tot.add(pay);
                        extra_tot = extra_tot.add(extra);
                        changed_tot = changed_tot.add(changed);
                        writeoffamt_tot = writeoffamt_tot.add(writeoffamt);
                        lackamt_tot = lackamt_tot.add(lackamt);
                        
                        //POS卡付款处理
                        if (CardsInfo!=null)
                        {
                            for (Card card : CardsInfo)
                            {
                                if (card.getCardNo().equals(payItem.getCardNo()) || ("3011".equals(payItem.getFuncNo()) && payItem.getCardNo().contains(card.getCardNo())) ||"3012".equals(payItem.getFuncNo()) ||"3013".equals(payItem.getFuncNo())||"3014".equals(payItem.getFuncNo()))
                                {
                                    //卡付款后
                                    payItem.setCardBeforeAmt(card.getAmount_before());
                                    payItem.setCardRemainAmt(card.getAmount_after());
                                    payItem.setCardSendPay(card.getAmount2());
                                    payItem.setLpcardNo(card.getCardNo());
                                    break;
                                }
                            }
                        }
                        
                        //禄品卡处理
                        String tempCardno=payItem.getCardNo();
                        if( "3011".equals(payItem.getFuncNo()))
                        {
                            tempCardno=payItem.getLpcardNo();
                        }
                        if(null==payItem.getGainChannel())
                        {
                        	payItem.setGainChannel("");
                        }
                        if(null==payItem.getGainChannelName())
                        {
                        	payItem.setGainChannelName("");
                        }
                        DataValue[] insValue_pay_detail = new DataValue[] {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(payBillNo, Types.VARCHAR),
                                new DataValue(payItem.getItem(), Types.INTEGER),
                                new DataValue(curDate, Types.VARCHAR),
                                new DataValue(par.getbDate(), Types.VARCHAR),
                                new DataValue(sourcebilltype, Types.VARCHAR),//sourcebilltype 来源单据类型：Order-订单TableRsv-桌台预订
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(loadDocType, Types.VARCHAR),
                                new DataValue(channelId, Types.VARCHAR),
                                new DataValue(payItem.getPayCode(), Types.VARCHAR),
                                new DataValue(payItem.getPayCodeErp(), Types.VARCHAR),
                                new DataValue(payItem.getPayName(), Types.VARCHAR),//ordershop
                                new DataValue(payItem.getOrder_payCode(), Types.VARCHAR),//ordershopname
                                new DataValue(payItem.getIsOnlinePay(), Types.VARCHAR),
                                new DataValue(payItem.getPay(), Types.VARCHAR),
                                new DataValue(payItem.getPayDiscAmt(), Types.VARCHAR),
                                new DataValue(payItem.getPayAmt1(), Types.VARCHAR),
                                new DataValue(payItem.getPayAmt2(), Types.VARCHAR),
                                new DataValue(payItem.getDescore(), Types.VARCHAR),
                                new DataValue(payItem.getCtType(), Types.VARCHAR),
                                new DataValue(tempCardno, Types.VARCHAR),
                                new DataValue(payItem.getCardBeforeAmt(), Types.VARCHAR),
                                new DataValue(payItem.getCardRemainAmt(), Types.VARCHAR),
                                new DataValue(payItem.getCouponQty(), Types.VARCHAR),
                                new DataValue(payItem.getIsVerification(), Types.VARCHAR),
                                new DataValue(payItem.getExtra(), Types.VARCHAR),
                                new DataValue(payItem.getChanged(), Types.VARCHAR),
                                new DataValue(payItem.getPaySerNum(), Types.VARCHAR),
                                new DataValue(payItem.getSerialNo(), Types.VARCHAR),
                                new DataValue(payItem.getRefNo(), Types.VARCHAR),
                                new DataValue(payItem.getTeriminalNo(), Types.VARCHAR),
                                new DataValue(payItem.getCanInvoice(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),//writeoffamt
                                new DataValue(payItem.getAuthCode(), Types.VARCHAR),
                                new DataValue(payItem.getFuncNo(), Types.VARCHAR),
                                new DataValue(payItem.getPaydoctype(), Types.VARCHAR),
                                new DataValue(payItem.getCardSendPay(), Types.VARCHAR),
                                new DataValue(payItem.getPayType(), Types.VARCHAR),
                                new DataValue(payItem.getMerDiscount(), Types.VARCHAR),//商户优惠金额，移动支付用，例如支付宝，微信等
                                new DataValue(payItem.getMerReceive(), Types.VARCHAR),//商家实收金额，移动支付用，例如支付宝，微信等
                                new DataValue(payItem.getThirdDiscount(), Types.VARCHAR),//第三方优惠金额：移动支付用，例如支付宝，微信等
                                new DataValue(payItem.getCustPayReal(), Types.VARCHAR),//客户实付金额：移动支付用，例如支付宝，微信等
                                new DataValue(payItem.getCouponMarketPrice(), Types.VARCHAR),//券面值
                                new DataValue(payItem.getCouponPrice(), Types.VARCHAR),//券售价
                                new DataValue(payItem.getMobile(), Types.VARCHAR),//会员卡付款对应的手机号
                                new DataValue(payItem.getPayChannelCode(), Types.VARCHAR),
                                new DataValue(payItem.getChargeAmount(), Types.VARCHAR),
                                new DataValue(par.getbDate(), Types.NUMERIC),//分区字段
                                new DataValue(payItem.getGainChannel(), Types.VARCHAR),
                                new DataValue(payItem.getGainChannelName(), Types.VARCHAR),
                        };
                        
                        InsBean ib_pay_detail = new InsBean("DCP_ORDER_PAY_DETAIL", columns_pay_detail);//分区字段已处理
                        ib_pay_detail.addValues(insValue_pay_detail);
                        DataPB.add(new DataProcessBean(ib_pay_detail));
                        
                        
                        if (isNeedInsert_DCP_STATISTIC_INFO)
                        {
                            //交班统计信息表DCP_STATISTIC_INFO
                            String[] Columns_DCP_STATISTIC_INFO = {
                                    "EID","SHOPID","MACHINE","OPNO","SQUADNO","ORDERNO","ITEM","PAYCODE",
                                    "PAYNAME","AMT","SDATE","STIME","ISORDERPAY","WORKNO","TYPE","BDATE","CARDNO",
                                    "CUSTOMERNO","CHANGED","EXTRA","ISTURNOVER","STATUS","APPTYPE","CHANNELID","PAYTYPE","MERDISCOUNT","THIRDDISCOUNT","DIRECTION","PAYCHANNELCODE","CHARGEAMOUNT"
                            };
                            DataValue[] insValue_DCP_STATISTIC_INFO = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shop, Types.VARCHAR),
                                    new DataValue(par.getMachineNo(), Types.VARCHAR),
                                    new DataValue(par.getOpNo(), Types.VARCHAR),
                                    new DataValue(par.getSquadNo(), Types.VARCHAR),
                                    new DataValue(orderNo, Types.VARCHAR),
                                    new DataValue(payItem.getItem(), Types.VARCHAR),
                                    new DataValue(payItem.getPayCode(), Types.VARCHAR),
                                    new DataValue(payItem.getPayName(), Types.VARCHAR),
                                    new DataValue(p_amt, Types.DECIMAL),
                                    new DataValue(curDate, Types.VARCHAR),
                                    new DataValue(curTime, Types.VARCHAR),
                                    new DataValue("N", Types.VARCHAR),//固定写N，这样才交班单能统计
                                    new DataValue(par.getWorkNo(), Types.VARCHAR),
                                    new DataValue("3", Types.VARCHAR),//TYPE 注意给值
                                    new DataValue(bDate, Types.VARCHAR),
                                    new DataValue(par.getCardNo(), Types.VARCHAR),
                                    new DataValue(par.getCustomer(), Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),
                                    new DataValue(payItem.getExtra(), Types.VARCHAR),
                                    new DataValue("Y", Types.VARCHAR),//ISTURNOVER
                                    new DataValue("100", Types.VARCHAR),
                                    new DataValue(loadDocType, Types.VARCHAR),//
                                    new DataValue(channelId, Types.VARCHAR),//
                                    new DataValue(payItem.getPayType(), Types.VARCHAR),//
                                    new DataValue(payItem.getMerDiscount(), Types.VARCHAR),//
                                    new DataValue(payItem.getThirdDiscount(), Types.VARCHAR),//
                                    new DataValue("1", Types.VARCHAR),
                                    new DataValue(payItem.getPayChannelCode(), Types.VARCHAR),
                                    new DataValue(payItem.getChargeAmount(), Types.VARCHAR),
                            };
                            InsBean ib_DCP_STATISTIC_INFO = new InsBean("DCP_STATISTIC_INFO", Columns_DCP_STATISTIC_INFO);
                            ib_DCP_STATISTIC_INFO.addValues(insValue_DCP_STATISTIC_INFO);
                            DataPB.add(new DataProcessBean(ib_DCP_STATISTIC_INFO));

                        }

                        if(isNeedInsert_DCP_CUSTOMER_CREDIT_DETAIL&&payItem.getFuncNo()!=null&&payItem.getFuncNo().equals("601"))
                        {
                            writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【写赊销明细表 DCP_CUSTOMER_CREDIT_DETAIL】，单号="+orderNo);
                            //交班统计信息表DCP_CUSTOMER_CREDIT_DETAIL
                            String[] Columns_DCP_CUSTOMER_CREDIT_DETAIL = {
                                    "EID","SHOPID","MACHNO","OPNO","BDATE","CUSTOMERNO","CREDITNAME","SOURCENO",
                                    "SOURCETYPE","CREDITAMT","RETURNAMT","LACKAMT","UPDATE_TIME"
                            };
                            DataValue[] insValue_DCP_CUSTOMER_CREDIT_DETAIL  = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shop, Types.VARCHAR),
                                    new DataValue(par.getMachineNo(), Types.VARCHAR),
                                    new DataValue(par.getOpNo(), Types.VARCHAR),
                                    new DataValue(par.getbDate(), Types.VARCHAR),
                                    new DataValue(par.getCustomer(), Types.VARCHAR),
                                    new DataValue(par.getCustomerName(), Types.VARCHAR),// 赊销人creditName == 传入参数 customerName
                                    new DataValue(orderNo, Types.VARCHAR),//来源单号 sourceNo == 订单号 orderNo ，
                                    new DataValue("3", Types.VARCHAR),//来源类型 sourceType == 3 订单
                                    new DataValue(payItem.getPay(), Types.VARCHAR),//赊销金额 creditAmt == 付款方式601的 pay 付款金额
                                    new DataValue("0", Types.VARCHAR),//已核销金额 returnAmt == 0，
                                    new DataValue(payItem.getPay(), Types.VARCHAR),//未核销金额 lackAmt == 赊销金额 creditAmt 。
                                    new DataValue(curDateTime, Types.VARCHAR),
                            };
                            InsBean ib_DCP_CUSTOMER_CREDIT_DETAIL = new InsBean("DCP_CUSTOMER_CREDIT_DETAIL", Columns_DCP_CUSTOMER_CREDIT_DETAIL);
                            ib_DCP_CUSTOMER_CREDIT_DETAIL.addValues(insValue_DCP_CUSTOMER_CREDIT_DETAIL);
                            DataPB.add(new DataProcessBean(ib_DCP_CUSTOMER_CREDIT_DETAIL));
                        }
                        

                    }
                    
                    /*****************************收款汇总表************************************/
                    String sourceheadbillno = orderNo;
                    String direction = billType;//金额方向:1、-1
                    String usetype = "front";//款项用途：front-预付款 refund-退款 final-尾款
                    if(direction.equals("-1"))
                    {
                        usetype ="refund";
                        sourceheadbillno ="";
                    }
                    
                    
                    String dcp_order_pay_status = "100";//收款状态：-1不成功 100成功
                    BigDecimal payrealamt_tot = pay_tot.subtract(changed_tot).subtract(extra_tot);//实付金额=付款金额-找零-溢收
                    String[] columns_pay =
                            { "eid", "billno", "billdate", "bdate", "sourcebilltype", "sourcebillno", "companyid", "shopid",
                                    "CHANNELID", "LOADDOCTYPE", "machineid", "customerno", "squadno", "workno", "direction", "payrealamt",
                                    "writeoffamt", "usetype", "status", "memo", "createopid", "createopname", "createtime",
                                    "SOURCEHEADBILLNO","PARTITION_DATE","UPDATE_TIME","TRAN_TIME"
                            };
                    String payShopId = shop;//收款门店，
                    if(loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.LINE))
                    {
                        if (par.getPayShopId()!=null&&!par.getPayShopId().trim().isEmpty())
                        {
                            payShopId = par.getPayShopId();
                        }
                        
                    }
                    DataValue[] insValue_pay = new DataValue[] {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(payBillNo, Types.VARCHAR),
                            new DataValue(curDate, Types.VARCHAR),
                            new DataValue(par.getbDate(), Types.VARCHAR),
                            new DataValue(sourcebilltype, Types.VARCHAR),//sourcebilltype 来源单据类型：Order-订单TableRsv-桌台预订
                            new DataValue(orderNo, Types.VARCHAR),
                            new DataValue(companyId, Types.VARCHAR),
                            new DataValue(payShopId, Types.VARCHAR),
                            new DataValue(channelId, Types.VARCHAR),
                            new DataValue(loadDocType, Types.VARCHAR),
                            new DataValue(par.getMachineNo(), Types.VARCHAR),
                            new DataValue(par.getCustomer(), Types.VARCHAR),
                            new DataValue(squadNo, Types.VARCHAR),
                            new DataValue(workNo, Types.VARCHAR),
                            new DataValue(direction, Types.VARCHAR),//direction  金额方向:1、-1
                            new DataValue(payrealamt_tot, Types.VARCHAR),
                            new DataValue(writeoffamt_tot, Types.VARCHAR),
                            new DataValue(usetype, Types.VARCHAR),//usetype 款项用途：front-预付款 refund-退款
                            new DataValue(dcp_order_pay_status, Types.VARCHAR),//status 收款状态：-1不成功 100成功
                            new DataValue("", Types.VARCHAR),//memo
                            new DataValue(par.getOpNo(), Types.VARCHAR),//createopid
                            new DataValue("", Types.VARCHAR),//createopname
                            new DataValue(lastmoditime, Types.DATE),//createtime
                            new DataValue(sourceheadbillno, Types.VARCHAR),//sourceheadbillno
                            new DataValue(par.getbDate(), Types.NUMERIC),//分区字段
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        
                    };
                    
                    InsBean ib_pay = new InsBean("DCP_ORDER_PAY", columns_pay);//分区字段已处理
                    ib_pay.addValues(insValue_pay);
                    DataPB.add(new DataProcessBean(ib_pay));
                    
                }
                
                /**********************************促销参与明细*********************************/
                if (loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID))
                {
                    if (promNoList!=null&&promNoList.isEmpty()==false)
                    {
                        writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【促销参与明细 PROM_MEMBER】，单号="+orderNo);
                        String SOURCEBILLTYPE = "3";//来源业务类型：0-销售 1-退单 2-无单退货 3-订单 4-退订
                        String DIRECTION = billType;//金额方向:1、-1
                        if(DIRECTION.equals("-1"))
                        {
                            SOURCEBILLTYPE ="4";
                        }
                        String[] columns_prom =
                                { "EID", "ID", "PROMNO", "SDATE", "SOURCEBILLTYPE", "SOURBILLNO","MEMBERID","CUSTID","CREATETIME","DIRECTION"};
                        for (String promNo : promNoList)
                        {
                            String promId = UUID.randomUUID().toString().replace("-", "");//参与记录ID
                            DataValue[] insValue_prom = new DataValue[] {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(promId, Types.VARCHAR),
                                    new DataValue(promNo, Types.VARCHAR),
                                    new DataValue(curDate, Types.VARCHAR),
                                    new DataValue(SOURCEBILLTYPE, Types.VARCHAR),
                                    new DataValue(orderNo, Types.VARCHAR),
                                    new DataValue(par.getMemberId(), Types.VARCHAR),
                                    new DataValue(par.getSellNo(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE),
                                    new DataValue(DIRECTION, Types.VARCHAR),
                            };
                            
                            InsBean ib_prom = new InsBean("PROM_MEMBER", columns_prom);
                            ib_prom.addValues(insValue_prom);
                            DataPB.add(new DataProcessBean(ib_prom));
                            
                        }
                        
                        
                    }
                }
                
                //【ID1035408】【阿哆诺斯升级3.0】券找零移植3.0---POS服务  by jinzma 20230828
                /**********************************找零券保存*********************************/
                List<order.CouponChange> couponChangeList = par.getCouponChangeList();
                if (couponChangeList!=null && !couponChangeList.isEmpty()) {
                    writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【找零券 DCP_ORDER_COUPON】，单号="+orderNo);
                    
                    String[] columns = {"EID","SHOPID","ORDERNO","COUPONCODE","COUPONNO","QTY","AMT","BDATE","SDATE","STIME","OPNO","TRAN_TIME"};
                    for (order.CouponChange couponChange : couponChangeList) {
                        DataValue[] insValue = new DataValue[] {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shop, Types.VARCHAR),
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(couponChange.getCouponCode(), Types.VARCHAR),
                                new DataValue(couponChange.getCouponNo(), Types.VARCHAR),
                                new DataValue(couponChange.getQuantity(), Types.VARCHAR),
                                new DataValue(couponChange.getFaceAmount(), Types.VARCHAR),
                                new DataValue(bDate, Types.VARCHAR),
                                new DataValue(curDate, Types.VARCHAR),
                                new DataValue(curTime, Types.VARCHAR),
                                new DataValue(par.getOpNo(), Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), Types.VARCHAR),   //2023-08-24 16:54:39
                        };
                        
                        InsBean ib = new InsBean("DCP_ORDER_COUPON", columns);
                        ib.addValues(insValue);
                        DataPB.add(new DataProcessBean(ib));
                    }
                }

                //建行实物券，有价券
                //【ID1039103】[3.0]金贝儿--建行开发接口评估---POS服务
                List<order.otherCoupnPay> otherCoupnPayList=par.getOtherCoupnPayList();
                if (otherCoupnPayList != null && otherCoupnPayList.size()>0)
                {
                    writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【建行实物券，有价券 DCP_OTHERCOUPON_PAY】，单号="+orderNo);

                    String[] columns = {"EID","SHOP","SALENO","ITEM","CARDNO"};

                    for (order.otherCoupnPay other : otherCoupnPayList)
                    {
                        DataValue[] insValue = new DataValue[] {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shop, Types.VARCHAR),
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(other.getItem(), Types.VARCHAR),
                                new DataValue(other.getCouponCode(), Types.VARCHAR),
                        };

                        InsBean ib = new InsBean("DCP_OTHERCOUPON_PAY", columns);
                        ib.addValues(insValue);
                        DataPB.add(new DataProcessBean(ib));
                    }
                }



                
            }
            catch (Exception e)
            {
                // TODO: handle exception
                writelog_waimai("【开始生成insert语句】异常；"+e.getMessage()+" 单号="+orderNo);
                errorMessage.append("【生成insert语句】异常；"+e.getMessage()+" 单号="+orderNo);
                return null;
            }
            
        }
        
        return DataPB;
    }
    
    
    public static boolean InsertOrderStatusLog(DsmDAO dao, List<orderStatusLog> statusLogList, StringBuilder errorMessage) throws Exception
    {
        
        errorMessage = new StringBuilder();
        //HelpTools.writelog_waimai("【写表tv_orderStatuslog收到的数据】" + req);
        boolean isError = false;
        try
        {
            
            if (statusLogList == null || statusLogList.size() == 0)
            {
                errorMessage.append("数据为空，");
                return false;
            }
            
            ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
            String[] columns1 = { "EID",   "ORDERNO", "LOADDOCTYPE","CHANNELID","LOADDOCBILLTYPE","LOADDOCORDERNO",
                    "STATUSTYPE", "STATUSTYPENAME", "STATUS", "STATUSNAME",
                    "OPNO", "OPNAME", "UPDATE_TIME", "MEMO","DISPLAY" };
            
            for (orderStatusLog item : statusLogList)
            {
                DataValue[] insValue1 = null;
                
                String eId = item.geteId();
                if (eId==null||eId.isEmpty())
                {
                    eId = " ";
                }
                String shopNo = item.getShopNo();
                String orderNo = item.getOrderNo();
                
                
                if (orderNo == null || orderNo.length() == 0) {
                    isError = true;
                    errorMessage.append("orderNo值不能为空，");
                }
                
                String loadDocType = item.getLoadDocType();
                
                String chanleId = item.getChannelId();
                
                if(chanleId==null||chanleId.isEmpty())
                {
                    chanleId = loadDocType;
                }
                
                String loadDocBillType = item.getLoadDocBillType();
                String loadDocOrderNo = item.getLoadDocOrderNo();
                
                if(loadDocOrderNo==null||loadDocOrderNo.isEmpty())
                {
                    loadDocOrderNo = orderNo;
                }
                
                
                
                
                String statusType = item.getStatusType();
                if (statusType == null || statusType.length() == 0) {
                    isError = true;
                    errorMessage.append("状态类型statusType值不能为空，");
                }
                String statusTypeName = item.getStatusTypeName();
                
                String status = item.getStatus();
                if (status == null || status.length() == 0)
                {
                    isError = true;
                    errorMessage.append("状态status值不能为空，");
                }
                
                String statusName = item.getStatusName();
                
                String update_time = item.getUpdate_time();
                if (update_time == null || update_time.length() == 0)
                {
						/*isError = true;
						errorMessage.append("操作时间update_time值不能为空，");*/
                    update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                }
                
                String need_notify = item.getNeed_notify();
                if (need_notify==null||need_notify.isEmpty())
                {
                    need_notify = "N";
                }
                
                String notify_status = item.getNotify_status();
                if (notify_status==null||notify_status.isEmpty())
                {
                    notify_status = "";
                }
                
                if (need_notify.equals("Y")) {
                    notify_status = "0";
                }
                
                String need_callback = item.getNeed_callback();
                if (need_callback==null||need_callback.isEmpty())
                {
                    need_callback = "N";
                }
                
                String callback_status = item.getCallback_status();
                if (callback_status==null||callback_status.isEmpty())
                {
                    callback_status = "";
                }
                if (need_callback.equals("Y")) {
                    callback_status = "0";
                }
                
                String memo = item.getMemo();
                
                
                String o_opNO = item.getOpNo();
                String o_opName = item.getOpName();
                
                String machShopNO = item.getMachShopNo();
                String shippingShopNO = item.getShippingShopNo();
                
                //1:对外给买家看的 否则写0
                String display = item.getDisplay();
                if (display==null||display.isEmpty())
                {
                    display = "0";
                }
                
                //订单 调度、开立 状态不显示给买家
                if (status.equals("0") ||status.equals("1"))
                {
                    display = "0";
                }
                
                if (isError)
                {
                    return false;
                }
                
                // 控制下长度截取
                
                if (memo != null && memo.length() > 2000) {
                    memo = memo.substring(0, 2000);// 数据库最长120
                }
                
                if (machShopNO != null && machShopNO.length() > 20) {
                    machShopNO = machShopNO.substring(0, 20);// 数据库最长20
                }
                
                if (shippingShopNO != null && shippingShopNO.length() > 20) {
                    shippingShopNO = shippingShopNO.substring(0, 20);// 数据库最长20
                }
                
                //statusType==999 && status==999 删除再插入
                if("999".equals(statusType)&&"999".equals(status))
                {
                    DelBean del = new DelBean("DCP_ORDER_STATUSLOG");
                    del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    del.addCondition("STATUSTYPE", new DataValue(statusType, Types.VARCHAR));
                    del.addCondition("STATUS", new DataValue(status, Types.VARCHAR));
                    
                    DPB.add(new DataProcessBean(del));
                }
                
                
                if("998".equals(statusType)&&"998".equals(status))
                {
                    DelBean del = new DelBean("DCP_ORDER_STATUSLOG");
                    del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    del.addCondition("STATUSTYPE", new DataValue(statusType, Types.VARCHAR));
                    del.addCondition("STATUS", new DataValue(status, Types.VARCHAR));
                    
                    DPB.add(new DataProcessBean(del));
                }
                //通知晓柚商城写日志
                if(("997".equals(statusType)&&"997".equals(status))||("996".equals(statusType)&&"996".equals(status)))
                {
                    DelBean del = new DelBean("DCP_ORDER_STATUSLOG");
                    del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    del.addCondition("STATUSTYPE", new DataValue(statusType, Types.VARCHAR));
                    del.addCondition("STATUS", new DataValue(status, Types.VARCHAR));
                    
                    DPB.add(new DataProcessBean(del));
                }
                
                //statusType==995 && status==995 订转销失败
                if("995".equals(statusType)&&"995".equals(status))
                {
                    DelBean del = new DelBean("DCP_ORDER_STATUSLOG");
                    del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                    del.addCondition("STATUSTYPE", new DataValue(statusType, Types.VARCHAR));
                    del.addCondition("STATUS", new DataValue(status, Types.VARCHAR));
                    
                    DPB.add(new DataProcessBean(del));
                }
                
                insValue1 = new DataValue[] {
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(orderNo, Types.VARCHAR),
                        new DataValue(loadDocType, Types.VARCHAR),
                        new DataValue(chanleId, Types.VARCHAR),
                        new DataValue(loadDocBillType, Types.VARCHAR),
                        new DataValue(loadDocOrderNo, Types.VARCHAR),
                        new DataValue(statusType, Types.VARCHAR), // 状态类型
                        new DataValue(statusTypeName, Types.VARCHAR), // 状态类型名称
                        new DataValue(status, Types.VARCHAR), // 状态
                        new DataValue(statusName, Types.VARCHAR), // 状态名称
                        new DataValue(o_opNO, Types.VARCHAR), //
                        new DataValue(o_opName, Types.VARCHAR), //
                        new DataValue(update_time, Types.VARCHAR), //
                        new DataValue(memo, Types.VARCHAR), //
                        new DataValue(display, Types.VARCHAR),
                };
                
                InsBean ib1 = new InsBean("DCP_ORDER_STATUSLOG", columns1);
                ib1.addValues(insValue1);
                DPB.add(new DataProcessBean(ib1));
                
                
                
            }
            
            
            if (isError)
            {
                return false;
            }
            
            if (DPB == null || DPB.size() == 0)
            {
                errorMessage.append("添加的执行语句列表为空！");
                return false;
            }
            
            // 开始保存数据库
            dao.useTransactionProcessData(DPB);
            
            return true;
            
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            errorMessage.append(e.getMessage());
            return false;
        }
        
        
    }
    
    
    /**
     * 更新订单相应的信息函数汇总(渠道参数设置，支付方式映射，是否自动发快递)
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void updateOrderFunction(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
        {
            errorMessage.append("渠道类型="+loadDocType+"暂不处理！");
            return;
            
        }
        //处理渠道参数
        updateOrderCreateByParaSet(dcpOrder,errorMessage);
        //处理支付方式映射
        updateOrderPayByMapping(dcpOrder,errorMessage);
        //是否自动发快递
        updateOrderAutoDelivery(dcpOrder,errorMessage);
        
    }
    
    
    /**
     * 支付方式映射
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void updateOrderPayByMapping(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        
        if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.WAIMAI)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
        {
            errorMessage.append("渠道类型="+loadDocType+"无需处理支付映射！");
            return;
            
        }
        
        //外卖的自动插入一笔
        if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
        {
            //errorMessage.append("渠道类型="+loadDocType+"没有付款记录，自动插入入一笔， 单号orderNo="+orderNo);
            
            dcpOrder.setPay(new ArrayList<orderPay>());
            
            String payType ="";//#501-美团外卖支付, #502-饿了么外卖支付, #503-京东到家支付,#504-抖音外卖支付
            String payCodeerp = "";//默认
            String payCode = "";
            String payName ="";
            if(loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.MTSG))
            {
                payType ="#501";
            }
            else if(loadDocType.equals(orderLoadDocType.ELEME))
            {
                payType ="#502";
            }
            else if(loadDocType.equals(orderLoadDocType.JDDJ))
            {
                payType ="#503";
            }
            else if(loadDocType.equals(orderLoadDocType.DYWM))
            {
                payType ="#504";
            }
            else
            {
                payType = loadDocType;
            }
            String sql = " SELECT A.PAYCODE,B.PAYCODEERP,C.PAYNAME FROM DCP_PAYTYPE A left join DCP_PAYMENT  B on A.Eid=B.eid AND A.PAYCODE=B.PAYCODE "
                    + " left join DCP_PAYTYPE_LANG C on  A.Eid=c.eid AND A.PAYTYPE=C.PAYTYPE AND C.LANG_TYPE='zh_CN' "
                    + " WHERE A.EID='"+eId+"' and A.PAYTYPE='"+payType+"'";
            List<Map<String, Object>> getPayCodeERP = StaticInfo.dao.executeQuerySQL(sql, null);
            
            if(getPayCodeERP!=null&&getPayCodeERP.isEmpty()==false)
            {
                payCode = getPayCodeERP.get(0).get("PAYCODE").toString();
                payCodeerp = getPayCodeERP.get(0).get("PAYCODEERP").toString();
                payName = getPayCodeERP.get(0).get("PAYNAME").toString();
            }
            
            
            writelog_waimai("【获取支付方式映射】付款列表为空，渠道类型="+loadDocType+"自动插入一笔，支付类型payType="+payType+",对应payCode="+payCode+",对应payCodeerp="+payCodeerp+", 单号orderNo="+orderNo);
            
            orderPay payModel = new orderPay();
            payModel.setItem("1");
            payModel.setPay(dcpOrder.getPayAmt()+"");
            payModel.setPayType(payType);
            payModel.setPayCode(payCode);
            payModel.setPayCodeErp(payCodeerp);
            payModel.setPayName(payName);
            payModel.setCardNo("");
            payModel.setCtType("");
            payModel.setPaySerNum("");// 支付后的支付品台交易号
            payModel.setSerialNo("");// 发起支付平台的商户唯一订单号
            payModel.setRefNo("");
            payModel.setDescore("0");
            payModel.setChanged("0");
            payModel.setExtra("0");
            payModel.setIsOrderPay(dcpOrder.getIsBook());
            payModel.setIsOnlinePay("Y");
            payModel.setOrder_payCode("");
            payModel.setbDate(dcpOrder.getbDate());
            
            dcpOrder.getPay().add(payModel);
            
            
            return;
            
        }
        
        
        if(dcpOrder.getPay()==null||dcpOrder.getPay().isEmpty())
        {
            writelog_waimai("【获取支付方式映射】渠道类型="+loadDocType+"付款列表为空，无须获取支付方式映射， 单号orderNo="+orderNo);
            return;
            
        }
        
        //手机商城
        if(loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))
        {
            for (orderPay pay : dcpOrder.getPay())
            {
                String order_payCode = pay.getOrder_payCode();//渠道过来的支付编码 自己商城=payType
                String payType = order_payCode;
                pay.setPayType(payType);
                String payCodeerp = "";//默认
                String payCode = "";
                String payName = "";
                String funcNo ="";
                String sql = " SELECT A.PAYCODE,A.FUNCNO,B.PAYCODEERP,B.PAYNAME FROM DCP_PAYTYPE A left join DCP_PAYMENT  B on A.Eid=B.eid AND A.PAYCODE=B.PAYCODE "
                        + " WHERE A.EID='"+eId+"' and A.PAYTYPE='"+payType+"'";
                List<Map<String, Object>> getPayCodeERP = StaticInfo.dao.executeQuerySQL(sql, null);
                
                if(getPayCodeERP!=null&&getPayCodeERP.isEmpty()==false)
                {
                    payCode = getPayCodeERP.get(0).get("PAYCODE").toString();
                    payCodeerp = getPayCodeERP.get(0).get("PAYCODEERP").toString();
                    payName = getPayCodeERP.get(0).get("PAYNAME").toString();
                    funcNo = getPayCodeERP.get(0).getOrDefault("FUNCNO","").toString();
                }
                pay.setPayCode(payCode);
                pay.setPayCodeErp(payCodeerp);
                pay.setPayName(payName);
                pay.setFuncNo(funcNo);
                writelog_waimai("【获取支付方式映射】，渠道类型="+loadDocType+"，支付类型payType="+payType+",对应payCode="+payCode+",对应payCodeerp="+payCodeerp+", 单号orderNo="+orderNo);
                
                
            }
            
            return;
            
        }
        
        //其他渠道，比如有赞，通过支付映射表
        
        String sql = " select A.*,B.PAYCODE,C.PAYCODEERP,C.PAYNAME PAYMENTNAME from DCP_PAYMENTMAPPING A " +
                " left join DCP_PAYTYPE B on  B.EID=A.EID AND B.PAYTYPE=A.PAYTYPE" +
                " left join DCP_PAYMENT C on C.EID=B.EID AND C.PAYCODE=B.PAYCODE" +
                " where A.EID='"+eId+"' and A.CHANNELTYPE='"+loadDocType+"' ";
        writelog_waimai("【获取支付方式映射】查询sql=" + sql+",单号orderNo="+orderNo+",渠道类型="+loadDocType+",渠道ID="+channelId);
        List<Map<String, Object>> getMappingPayMentList = StaticInfo.dao.executeQuerySQL(sql, null);
        if(getMappingPayMentList==null||getMappingPayMentList.isEmpty())
        {
            writelog_waimai("【获取支付方式映射】查询结果为空， 单号orderNo="+orderNo);
            return;
        }
        for (orderPay pay : dcpOrder.getPay())
        {
            String order_payCode = pay.getOrder_payCode();//渠道过来的支付编码
            if (order_payCode==null||order_payCode.isEmpty())
            {
                writelog_waimai("【循环订单传入的支付列表匹配支付方式映射】项次item="+pay.getItem()+",第三方传入的支付编码order_payCode为空无法映射,单号orderNo="+orderNo);
                continue;
            }
            String payType_mapping = "";//新零售payType
            String payCodeerp_mapping = "";//对应的erp
            String payCode_mapping = "";//对应的erp
            String payName_mapping = "";//设置映射的时候名称
            String payName_payMent = "";//payment表的名称
            
            for (Map<String, Object> map : getMappingPayMentList)
            {
                if (map.get("ORDER_PAYCODE").equals("ALL") && payType_mapping.isEmpty())
                {
                    payType_mapping = map.get("PAYTYPE").toString();
                    payCode_mapping = map.get("PAYCODE").toString();
                    payCodeerp_mapping = map.get("PAYCODEERP").toString();
                    payName_mapping = map.get("PAYNAME").toString();
                    payName_payMent = map.get("PAYMENTNAME").toString();
                }
                if (map.get("ORDER_PAYCODE").equals(order_payCode))
                {
                    payType_mapping = map.get("PAYTYPE").toString();
                    payCode_mapping = map.get("PAYCODE").toString();
                    payCodeerp_mapping = map.get("PAYCODEERP").toString();
                    payName_mapping = map.get("PAYNAME").toString();
                    payName_payMent = map.get("PAYMENTNAME").toString();
                    break;
                }
                
            }
            
            if(!payType_mapping.isEmpty())
            {
                pay.setPayType(payType_mapping);
                //pay.setPaydoctype(payType_mapping);
                pay.setPayCode(payCode_mapping);
                pay.setPayCodeErp(payCodeerp_mapping);
                if (pay.getPayName()==null||pay.getPayName().isEmpty())
                {
                    if (!payName_payMent.isEmpty())
                    {
                        pay.setPayName(payName_payMent);
                    }
                    
                }
                writelog_waimai("【循环订单传入的支付列表匹配支付方式映射】【映射成功】项次item="+pay.getItem()+",第三方支付编码order_payCode="+order_payCode+",对应payType="+pay.getPayType()+",payCode="+pay.getPayCode()+",payCodeerp="+pay.getPayCodeErp()+",单号orderNo="+orderNo);
            }
            else
            {
                writelog_waimai("【循环订单传入的支付列表匹配支付方式映射】【未找到映射】项次item="+pay.getItem()+",第三方支付编码order_payCode="+order_payCode+",对应payType="+pay.getPayType()+",payCode="+pay.getPayCode()+",payCodeerp="+pay.getPayCodeErp()+",单号orderNo="+orderNo);
            }
            
        }
        
    }
    
    /**
     * 订单接入到订单中心时根据渠道设置进行重新赋值
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void updateOrderCreateByParaSet(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        
        if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
        {
            errorMessage.append("渠道类型="+loadDocType+"暂不处理！");
            return;
            
        }
        
        String sql = " select * from DCP_ECOMMERCE where EID='"+eId+"' and LOADDOCTYPE='"+loadDocType+"' and CHANNELID='"+channelId+"' ";
        writelog_waimai("【获取渠道参数设置】查询sql=" + sql+" 单号orderNo="+orderNo);
        List<Map<String, Object>> getLoadDocTypeSet = StaticInfo.dao.executeQuerySQL(sql, null);
        if(getLoadDocTypeSet==null||getLoadDocTypeSet.isEmpty())
        {
            writelog_waimai("【获取渠道参数设置】查询结果为空， 单号orderNo="+orderNo);
            return;
        }
        
        String shippingShopNo_set =  getLoadDocTypeSet.get(0).get("SHIPPINGSHOPNO").toString();//默认配送机构
        String warehouse_set = getLoadDocTypeSet.get(0).get("WAREHOUSE").toString();//仓库
        
        if(dcpOrder.getShippingShopNo()==null||dcpOrder.getShippingShopNo().trim().isEmpty())
        {
            dcpOrder.setShippingShopNo(shippingShopNo_set);
            writelog_waimai("【获取渠道参数设置】【订单上没有指定配送门店】，订单的配送门店=渠道上设置的配送机构shippingShopNo="+shippingShopNo_set+"，单号orderNo="+orderNo);
        }

        
        String isProdisPatch = getLoadDocTypeSet.get(0).get("ISPRODISPATCH").toString();//是否开启生产调度
        writelog_waimai("【获取渠道参数设置】【参数】【是否开启生产调度】="+isProdisPatch+"，单号orderNo="+orderNo);
        if(isProdisPatch!=null&&isProdisPatch.equals("Y"))
        {
            getMachShopByShippingShop(dcpOrder,errorMessage);
            //鼎捷自己的商城，根据商品属性判断是否需要生产，没有需要生产的商品，则清空之前自动匹配的生产机构(如果是生产机构是总部，不清空)
            if (loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE)||loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
            {
                updateMachShopByGoods(dcpOrder,errorMessage);
            }
        }

        //先得到生产/配送门店之后，来判断门店订单审核
        String status=dcpOrder.getStatus();
        String isReview = getLoadDocTypeSet.get(0).get("ISREVIEW").toString();//是否开启订单审核 Y开启、N不开启
        if(isReview!=null&&isReview.equals("Y"))
        {
            boolean isUpdateStatus = true;
            //订单审核类型(1或者空-支持审核所有订单；2-仅支持审核配送订单)
            String reviewType = getLoadDocTypeSet.get(0).getOrDefault("REVIEWTYPE","").toString();
            if (reviewType==null||reviewType.trim().isEmpty())
            {
                reviewType = "1";
            }

            if ("2".equals(reviewType))
            {
                String shipType = dcpOrder.getShipType();//1.订单来源渠道 2.全国配送 3.顾客自提 5总部配送 6同城配送
                if ("3".equals(shipType))
                {
                    isUpdateStatus = false;
                }

            }

            if (isUpdateStatus)
            {
                ////是否门店订单审核 Y开启、N不开启
                String isReviewToShop = getLoadDocTypeSet.get(0).getOrDefault("ISREVIEWTOSHOP","").toString();
                if ("Y".equals(isReviewToShop))
                {
                    String sql_isReviewToShop = " select * from DCP_CHANNELREVIEW_SHOP where EID='"+eId+"' and LOADDOCTYPE='"+loadDocType+"' and CHANNELID='"+channelId+"' and SHOPNO in ('"+dcpOrder.getMachShopNo()+"','"+dcpOrder.getShippingShopNo()+"') ";
                    writelog_waimai("【获取渠道参数设置】【参数】【是否开启订单审核】="+isReview+"，【订单审核类型】reviewType="+reviewType+",【订单审核是否到门店】isReviewToShop="+isReviewToShop+",开始判断生产/配送门店是否满足sql语句:"+sql_isReviewToShop+"，单号orderNo="+orderNo);
                    List<Map<String, Object>> getReviewShopList = StaticInfo.dao.executeQuerySQL(sql_isReviewToShop, null);
                    if (getReviewShopList!=null&&!getReviewShopList.isEmpty())
                    {
                        dcpOrder.setStatus("0");
                        writelog_waimai("【获取渠道参数设置】【参数】【是否开启订单审核】="+isReview+"，【订单审核类型】reviewType="+reviewType+",【订单审核是否到门店】isReviewToShop="+isReviewToShop+",订单状态转成待审核status=0，单号orderNo="+orderNo);
                    }
                    else
                    {
                        dcpOrder.setStatus("1");
                        writelog_waimai("【获取渠道参数设置】【参数】【是否开启订单审核】="+isReview+"，【订单审核类型】reviewType="+reviewType+",【订单审核是否到门店】isReviewToShop="+isReviewToShop+",生产/配送门店不在设置的里面，不用转成待审核,订单状态开立status=1，单号orderNo="+orderNo);
                    }


                }
                else
                {
                    dcpOrder.setStatus("0");
                    writelog_waimai("【获取渠道参数设置】【参数】【是否开启订单审核】="+isReview+"，【订单审核类型】reviewType="+reviewType+",【订单审核是否到门店】isReviewToShop="+isReviewToShop+",订单状态转成待审核status=0，单号orderNo="+orderNo);
                }


            }
            else
            {
                dcpOrder.setStatus("1");
                writelog_waimai("【获取渠道参数设置】【参数】【是否开启订单审核】="+isReview+"，【订单审核类型】reviewType="+reviewType+",订单状态status="+dcpOrder.getStatus()+",不用转成待审核，订单状态开立status=1,单号orderNo="+orderNo);
            }
        }
        else
        {
            dcpOrder.setStatus("1");
            writelog_waimai("【获取渠道参数设置】【参数】【是否开启订单审核】="+isReview+"，不用转成待审核，订单状态开立status=1，单号orderNo="+orderNo);
        }
        String loadDoctype=dcpOrder.getLoadDocType();
        //企迈渠道  status根据入参给值
        if(orderLoadDocType.QIMAI.equals(loadDoctype)){
            if(status!=null&&status.length()>0){
                dcpOrder.setStatus(status);
                writelog_waimai("【启迈渠道】订单状态等于启迈传入的status="+status+"，单号orderNo="+orderNo);
            }
        }
        
        String isOrderLockStock = getLoadDocTypeSet.get(0).get("ISORDERLOCKSTOCK").toString();//是否订单中心锁库存
        writelog_waimai("【获取渠道参数设置】【参数】【是否订单中心锁库存】="+isOrderLockStock+"，单号orderNo="+orderNo);
        if(isOrderLockStock!=null&&isOrderLockStock.equals("Y"))
        {
            if(orderLoadDocType.MINI.equals(loadDoctype)||orderLoadDocType.WECHAT.equals(loadDoctype)||orderLoadDocType.LINE.equals(loadDoctype))
            {
                writelog_waimai("该渠道类型："+loadDoctype+"【已经在CRM服务中锁库了，不用再次锁库】，单号orderNo="+orderNo);
            }
            else
            {
                orderCreateLockStock(dcpOrder,errorMessage);
            }
            
        }
        
        
        
    }
    
    /**
     * 订单接入是锁定库存
     * @param dcpOrder
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static boolean orderCreateLockStock(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return false;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String billType = "order";
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        
        DCP_StockLock_OpenReq  reqLockStock = new DCP_StockLock_OpenReq();
        reqLockStock.seteId(eId);
        reqLockStock.setToken("");
        reqLockStock.setServiceId("DCP_StockLock");
        DCP_StockLock_OpenReq.levelReq reqLockStock_request = reqLockStock.new levelReq();
        reqLockStock_request.setPluList(new ArrayList<DCP_StockLock_OpenReq.PluList>());
        reqLockStock_request.setBillNo(orderNo);
        reqLockStock_request.setBillType(billType);
        String bDate = dcpOrder.getbDate();
        if(bDate==null||bDate.isEmpty())
        {
            bDate = sDate;
            dcpOrder.setbDate(bDate);
        }
        reqLockStock_request.setbDate(bDate);
        reqLockStock_request.setAddress(dcpOrder.getAddress());
        reqLockStock_request.setChannelId(channelId);
        reqLockStock_request.setCity(dcpOrder.getCity());
        reqLockStock_request.setProvince(dcpOrder.getProvince());
        reqLockStock_request.setOrganizationNo(dcpOrder.getShippingShopNo());
        for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
        {
            DCP_StockLock_OpenReq.PluList oneLv1 = reqLockStock.new PluList();
            
            oneLv1.setPluNo(goodsItem.getPluNo());
            oneLv1.setFeatureNo(goodsItem.getFeatureNo());
            oneLv1.setsUnit(goodsItem.getsUnit());
            oneLv1.setQty(goodsItem.getQty()+"");
            reqLockStock_request.getPluList().add(oneLv1);
        }
        
        reqLockStock.setRequest(reqLockStock_request);
        try
        {
            ParseJson pj = new ParseJson();
            
            String json = pj.beanToJson(reqLockStock);
            HelpTools.writelog_waimai("【订单接入】【调用库存锁定接口】开始，请求json="+json+" 订单单号orderNo="+orderNo);
            
            DispatchService ds = DispatchService.getInstance();
            String resXML = ds.callService(json, StaticInfo.dao);
            JSONObject json_res = new JSONObject(resXML);
            HelpTools.writelog_waimai("【订单接入】【调用库存锁定接口】结束，返回json="+json_res+" 订单单号orderNo="+orderNo);
            
            boolean success =  json_res.getBoolean("success");
            String serviceDescription = json_res.get("serviceDescription").toString();
            if(success)
            {
                
                HelpTools.writelog_waimai("【订单接入】【调用库存锁定接口】成功， 订单单号orderNo="+orderNo);
                return true;
            }
            else
            {
                HelpTools.writelog_waimai("【订单接入】【调用库存锁定接口】失败："+serviceDescription+"， 订单单号orderNo="+orderNo);
                errorMessage.append("【订单接入】【调用库存锁定接口】失败："+serviceDescription);
                return false;
            }
        }
        catch(Exception e)
        {
            errorMessage.append("【订单接入】【调用库存锁定接口】异常："+e.getMessage());
            HelpTools.writelog_waimai("【订单接入】【调用库存锁定接口】异常："+e.getMessage()+" 订单单号orderNo="+orderNo);
        }
        
        
        return false;
    }
    
    /**
     * 更新订单快递类型以及是否自动发快递
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void updateOrderAutoDelivery(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String shippingShopNo = dcpOrder.getShippingShopNo();
        String shipType = dcpOrder.getShipType();
        if(shipType==null)
        {
            return;
        }
        if(shipType.equals("2")||shipType.equals("6"))//2全国快递 6 同城配送
        {
        
        }
        else
        {
            writelog_waimai("【获取订单上配送门店设置的物流类型】配送方式shipType="+shipType+"无需判断是否自动发快递， 单号orderNo="+orderNo);
            return;
        }
        
        String sql = " select * from DCP_ORG_ORDERTAKESET where EID='"+eId+"' and LOADDOCTYPE='"+loadDocType+"' and ORGANIZATIONNO='"+shippingShopNo+"' ";
        writelog_waimai("【获取订单上配送门店设置的物流类型】查询sql=" + sql+" 单号orderNo="+orderNo);
        List<Map<String, Object>> getLoadDocTypeSet = StaticInfo.dao.executeQuerySQL(sql, null);
        if(getLoadDocTypeSet==null||getLoadDocTypeSet.isEmpty())
        {
            writelog_waimai("【获取订单上配送门店设置的物流类型】查询结果为空， 单号orderNo="+orderNo);
            return;
        }
        //dcpOrder.getDeliveryBusinessType()
        
        String autoDelivery = "N";
        String cityDelieryType = getLoadDocTypeSet.get(0).get("CITYDELIVERYTYPE").toString();//同城默认物流类型
        String nationalDelieryType = getLoadDocTypeSet.get(0).get("NATIONALDELIVERYTYPE").toString();//全国默认物流类型
        String autoDelivery_db = getLoadDocTypeSet.get(0).get("ISAUTODELIVERY").toString();//是否自动发快递 Y N
        if(autoDelivery_db!=null&&autoDelivery_db.equals("Y"))
        {
            if(shipType.equals("2")&&nationalDelieryType!=null&&nationalDelieryType.isEmpty()==false)
            {
                autoDelivery = "Y";
                dcpOrder.setAutoDelivery(autoDelivery);
                dcpOrder.setDeliveryType(nationalDelieryType);
                writelog_waimai("【获取订单上配送门店设置的物流类型】自动发快递autoDelivery="+autoDelivery+"，配送方式shipType="+shipType+"，全国默认物流类型deliverType="+nationalDelieryType+"， 单号orderNo="+orderNo);
                return;
            }
            if(shipType.equals("6")&&cityDelieryType!=null&&cityDelieryType.isEmpty()==false)
            {
                autoDelivery = "Y";
                dcpOrder.setAutoDelivery(autoDelivery);
                dcpOrder.setDeliveryType(cityDelieryType);
                writelog_waimai("【获取订单上配送门店设置的物流类型】自动发快递autoDelivery="+autoDelivery+"，配送方式shipType="+shipType+"，同城默认物流类型deliverType="+cityDelieryType+"， 单号orderNo="+orderNo);
                return;
            }
        }
        
        writelog_waimai("【获取订单上配送门店设置的物流类型】自动发快递autoDelivery="+autoDelivery+"，配送方式shipType="+shipType+"， 单号orderNo="+orderNo);
        
        
    }
    
    /**
     * 根据条码查询pluno,featureno
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void updateOrderDetailInfo(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String langType = "zh_CN";
        
        
        
        if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
        {
            errorMessage.append("渠道类型="+loadDocType+"无需处理商品资料映射！ 单号orderNo="+orderNo);
            return;
            
        }
        
        if(dcpOrder.getGoodsList()==null||dcpOrder.getGoodsList().isEmpty())
        {
            writelog_waimai("【获取商品映射资料】渠道类型="+loadDocType+"商品列表为空，无须获取获取商品映射， 单号orderNo="+orderNo);
            return;
        }
        
        
        
        
        
        writelog_waimai("【获取商品映射资料】循环开始， 单号orderNo="+orderNo);//先不管效能问题，后续再优化
        try
        {
            boolean isErrorGoods = false;
            String memo_errorGoods = "";
            orderAbnormal abnormalHead = new orderAbnormal();
            abnormalHead.setAbnormalType(orderAbnormalType.goodsNotFound);
            abnormalHead.setAbnormalTypeName("商品错误/未找到");
            abnormalHead.setStatus("0");
            abnormalHead.setDetail(new ArrayList<orderAbnormalDetail>());
            
            for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
            {
                try
                {
                    String pluBarcode = goodsItem.getPluBarcode();//渠道过来的支付编码
                    boolean isQueryAbormalGoodsName = false;//只需要查询本一次地异常商品表资料就可以 dcp_abnormalgood_mapping
                    //优化下效能,没有做映射的门店，不需要查询本一次地异常商品表资料就
                    if (dcpOrder.getShopNo()==null||dcpOrder.getShopNo().trim().isEmpty())
                    {
                        isQueryAbormalGoodsName = true;
                    }
                    if(pluBarcode==null||pluBarcode.isEmpty())
                    {
                        if (!isQueryAbormalGoodsName)
                        {
                            pluBarcode = getPluBarcodeByAbnormalGoodsName(goodsItem.getPluName(),eId,loadDocType,channelId);
                            writelog_waimai("【平台商品未映射或映射错误】【根据平台商品名称查询本地异常商品资料表dcp_abnormalgood_mapping】，平台商品名称pluName="+goodsItem.getPluName()+",查询返回plubarcode="+ pluBarcode);
                            isQueryAbormalGoodsName = true;
                            if (pluBarcode!=null&&!pluBarcode.isEmpty())
                            {
                                goodsItem.setPluBarcode(pluBarcode);
                            }
                        }
                        
                    }
                    
                    String sql = "SELECT * FROM ("
                            + " SELECT A.PLUBARCODE,A.PLUNO,A.UNIT,A.FEATURENO,FL.FEATURENAME,UL.UNAME,A.STATUS AS STATUS_BARCODE,G.STATUS AS STATUS_PLUNO, "
                            + "NVL(H1.ISLIQUOR, H2.ISLIQUOR) ISLIQUOR, "
                            + "NVL(H1.isKdsShow, H2.isKdsShow) isKdsShow, "
                            + "NVL(H1.ISKDS_CATERING_SHOW, H2.ISKDS_CATERING_SHOW) ISKDSCATERINGSHOW, "
                            + "NVL(H1.KDS_MAX_MAKE_QTY, H2.KDS_MAX_MAKE_QTY) KDSMAXMAKEQTY, "
                            + "NVL(H1.ISQTYPRINT, H2.ISQTYPRINT) ISQTYPRINT, "
                            + "NVL(H1.isPrintReturn, H2.isPrintReturn) isPrintReturn, "
                            + "NVL(H1.isPrintCrossMenu, H2.isPrintCrossMenu) isPrintCrossMenu, "
                            + "NVL(H1.CROSSPRINTERNAME, H2.CROSSPRINTERNAME) CROSSPRINTER, "
                            + "NVL(H1.PRINTERNAME, H2.PRINTERNAME) kitchenPrinter "
                            + " FROM DCP_GOODS_BARCODE A "
                            + " left join  DCP_GOODS_FEATURE_LANG FL on A.EID =FL.EID AND A.PLUNO=FL.PLUNO AND A.FEATURENO=FL.FEATURENO and FL.Lang_Type='"+langType+"' "
                            + " left join dcp_unit_lang UL on A.EID =UL.EID AND A.UNIT=UL.UNIT and UL.Lang_Type='"+langType+"' "
                            + " left join dcp_goods G  on A.EID =G.EID  and A.PLUNO=G.PLUNO "
                            + " LEFT JOIN DCP_KITCHENPRINTSET H1 ON G.EID=H1.EID AND G.PLUNO=H1.ID AND H1.TYPE='GOODS' AND H1.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                            + " LEFT JOIN DCP_KITCHENPRINTSET H2 ON G.EID=H2.EID AND G.Category=H2.ID AND H2.TYPE='CATEGORY' AND H2.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                            + " where  A.EID='"+eId+"' and A.plubarcode ='"+pluBarcode.replaceAll("'","''")+"' "
                            + ")";
                    //String[] conditionValues = {pluBarcode};
                    writelog_waimai("【获取商品映射资料】循环开始，查询资料sql="+ sql+",传参plubarcode="+pluBarcode+",单号orderNo="+orderNo);
                    if(pluBarcode==null||pluBarcode.isEmpty())
                    {
                        orderAbnormalDetail abnormalDetail = new orderAbnormalDetail();
                        String memoGoods = "未在平台上映射对应条码，";
                        abnormalDetail.setItem(goodsItem.getItem());
                        abnormalDetail.setMemo(memoGoods);
                        abnormalDetail.setPluName(goodsItem.getPluName());
                        abnormalDetail.setStatus("0");
                        memo_errorGoods = memo_errorGoods+memoGoods+"<br>";
                        abnormalHead.getDetail().add(abnormalDetail);
                        
                        isErrorGoods = true;
                        continue;
                    }
                    List<Map<String, Object>> getPluInfo = StaticInfo.dao.executeQuerySQL(sql, null);
                    
                    if(getPluInfo==null||getPluInfo.isEmpty())
                    {
                        if (isQueryAbormalGoodsName)
                        {
                            writelog_waimai("【获取商品映射资料】循环，条码pluBarcode="+ pluBarcode+"查无资料,单号orderNo="+orderNo);
                            
                            orderAbnormalDetail abnormalDetail = new orderAbnormalDetail();
                            String memoGoods = "平台上映射的条码错误，查无资料，";
                            abnormalDetail.setItem(goodsItem.getItem());
                            abnormalDetail.setMemo(memoGoods);
                            abnormalDetail.setPluName(goodsItem.getPluName());
                            abnormalDetail.setStatus("0");
                            memo_errorGoods = memo_errorGoods+memoGoods+"<br>";
                            abnormalHead.getDetail().add(abnormalDetail);
                            
                            isErrorGoods = true;
                            continue;
                        }
                        else
                        {
                            String pluBarcode_old = goodsItem.getPluBarcode();
                            //根据商品名称重新获取下plubarcode
                            pluBarcode = getPluBarcodeByAbnormalGoodsName(goodsItem.getPluName(),eId,loadDocType,channelId);
                            writelog_waimai("【平台商品未映射或映射错误】【根据平台商品名称查询本地异常商品资料表dcp_abnormalgood_mapping】，平台商品名称pluName="+goodsItem.getPluName()+",查询返回plubarcode="+ pluBarcode);
                            isQueryAbormalGoodsName = true;
                            if (pluBarcode!=null&&!pluBarcode.isEmpty())
                            {
                                //根据新获取的plubarcode，再查询一次基础资料
                                sql = "SELECT * FROM ("
                                        + " SELECT A.PLUBARCODE,A.PLUNO,A.UNIT,A.FEATURENO,FL.FEATURENAME,UL.UNAME,A.STATUS AS STATUS_BARCODE,G.STATUS AS STATUS_PLUNO, "
                                        + "NVL(H1.ISLIQUOR, H2.ISLIQUOR) ISLIQUOR, "
                                        + "NVL(H1.isKdsShow, H2.isKdsShow) isKdsShow, "
                                        + "NVL(H1.ISKDS_CATERING_SHOW, H2.ISKDS_CATERING_SHOW) ISKDSCATERINGSHOW, "
                                        + "NVL(H1.KDS_MAX_MAKE_QTY, H2.KDS_MAX_MAKE_QTY) KDSMAXMAKEQTY, "
                                        + "NVL(H1.ISQTYPRINT, H2.ISQTYPRINT) ISQTYPRINT, "
                                        + "NVL(H1.isPrintReturn, H2.isPrintReturn) isPrintReturn, "
                                        + "NVL(H1.isPrintCrossMenu, H2.isPrintCrossMenu) isPrintCrossMenu, "
                                        + "NVL(H1.CROSSPRINTERNAME, H2.CROSSPRINTERNAME) CROSSPRINTER, "
                                        + "NVL(H1.PRINTERNAME, H2.PRINTERNAME) kitchenPrinter "
                                        + " FROM DCP_GOODS_BARCODE A "
                                        + " left join  DCP_GOODS_FEATURE_LANG FL on A.EID =FL.EID AND A.PLUNO=FL.PLUNO AND A.FEATURENO=FL.FEATURENO and FL.Lang_Type='"+langType+"' "
                                        + " left join dcp_unit_lang UL on A.EID =UL.EID AND A.UNIT=UL.UNIT and UL.Lang_Type='"+langType+"' "
                                        + " left join dcp_goods G  on A.EID =G.EID  and A.PLUNO=G.PLUNO "
                                        + " LEFT JOIN DCP_KITCHENPRINTSET H1 ON G.EID=H1.EID AND G.PLUNO=H1.ID AND H1.TYPE='GOODS' AND H1.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                                        + " LEFT JOIN DCP_KITCHENPRINTSET H2 ON G.EID=H2.EID AND G.Category=H2.ID AND H2.TYPE='CATEGORY' AND H2.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                                        + " where  A.EID='"+eId+"' and A.plubarcode ='"+pluBarcode.replaceAll("'","''")+"' "
                                        + ")";
                                writelog_waimai("【根据平台商品名称查询本地异常商品资料表dcp_abnormalgood_mapping】【获取商品映射资料】开始，再次查询资料sql="+ sql+",单号orderNo="+orderNo);
                                getPluInfo = StaticInfo.dao.executeQuerySQL(sql, null);
                                if (getPluInfo==null||getPluInfo.isEmpty())
                                {
                                    writelog_waimai("【获取商品映射资料】循环，条码pluBarcode="+ pluBarcode+"查无资料,单号orderNo="+orderNo);
                                    
                                    orderAbnormalDetail abnormalDetail = new orderAbnormalDetail();
                                    String memoGoods = "平台上映射的条码错误，查无资料，";
                                    abnormalDetail.setItem(goodsItem.getItem());
                                    abnormalDetail.setMemo(memoGoods);
                                    abnormalDetail.setPluName(goodsItem.getPluName());
                                    abnormalDetail.setStatus("0");
                                    memo_errorGoods = memo_errorGoods+memoGoods+"<br>";
                                    abnormalHead.getDetail().add(abnormalDetail);
                                    
                                    isErrorGoods = true;
                                    continue;
                                    
                                }
                                else
                                {
                                    goodsItem.setPluBarcode(pluBarcode);
                                }
                            }
                            else
                            {
                                writelog_waimai("【获取商品映射资料】循环，条码pluBarcode="+ goodsItem.getPluBarcode()+"查无资料,单号orderNo="+orderNo);
                                
                                orderAbnormalDetail abnormalDetail = new orderAbnormalDetail();
                                String memoGoods = "平台上映射的条码错误，查无资料，";
                                abnormalDetail.setItem(goodsItem.getItem());
                                abnormalDetail.setMemo(memoGoods);
                                abnormalDetail.setPluName(goodsItem.getPluName());
                                abnormalDetail.setStatus("0");
                                memo_errorGoods = memo_errorGoods+memoGoods+"<br>";
                                abnormalHead.getDetail().add(abnormalDetail);
                                
                                isErrorGoods = true;
                                continue;
                                
                            }
                        }
                        
                        
                        
                    }
                    if(getPluInfo!=null&&getPluInfo.isEmpty()==false)
                    {
                        String pluNo = getPluInfo.get(0).get("PLUNO").toString();
                        String featureNo = getPluInfo.get(0).get("FEATURENO").toString();
                        String featureName = getPluInfo.get(0).get("FEATURENAME").toString();
                        String unit = getPluInfo.get(0).get("UNIT").toString();
                        String unitName = getPluInfo.get(0).get("UNAME").toString();
                        String status_barcode = getPluInfo.get(0).get("STATUS_BARCODE").toString();
                        String status_pluno = getPluInfo.get(0).get("STATUS_PLUNO").toString();
                        
                        goodsItem.setPluNo(pluNo);
                        goodsItem.setFeatureNo(featureNo);
                        goodsItem.setFeatureName(featureName);
                        goodsItem.setsUnit(unit);
                        if(unitName!=null&&unitName.isEmpty()==false)
                        {
                            goodsItem.setsUnitName(unitName);
                        }
                        
                        //这部分是后厨打印，你可以加参数控制不走SQL这些字段
                        goodsItem.setIsLiquor(getPluInfo.get(0).getOrDefault("ISLIQUOR","N").toString());
                        goodsItem.setKdsMaxMakeQty(Convert.toBigDecimal(getPluInfo.get(0).getOrDefault("KDSMAXMAKEQTY", 0), BigDecimal.ZERO));
                        goodsItem.setIsQtyPrint(getPluInfo.get(0).getOrDefault("ISQTYPRINT","N").toString());
                        goodsItem.setIsPrintReturn(getPluInfo.get(0).getOrDefault("ISPRINTRETURN","N").toString());
                        goodsItem.setIsPrintCrossMenu(getPluInfo.get(0).getOrDefault("ISPRINTCROSSMENU","N").toString());
                        goodsItem.setCrossPrinter(getPluInfo.get(0).getOrDefault("CROSSPRINTER","").toString());
                        goodsItem.setKitchenPrinter(getPluInfo.get(0).getOrDefault("KITCHENPRINTER","").toString());
                        
                        
                        writelog_waimai("【获取商品映射资料】循环，条码pluBarcode="+ pluBarcode+",对应pluNo="+pluNo+"，对应featureNo="+featureNo+",对应featureName="+featureName+",对应unit="+unit+",对应unitName="+unitName+",单号orderNo="+orderNo);
                        
                        //条码档基础资料和编码档基础资料是否异常
                        if("100".equals(status_barcode)&&"100".equals(status_pluno))
                        {
                        
                        }
                        else
                        {
                            orderAbnormalDetail abnormalDetail = new orderAbnormalDetail();
                            String memoGoods = "";
                            if("100".equals(status_barcode)==false)
                            {
                                memoGoods += "该条码pluBarcode="+ pluBarcode+"对应基础资料表DCP_GOODS_BARCODE未启用/已禁用，";
                            }
                            if("100".equals(status_pluno)==false)
                            {
                                memoGoods += "该编码pluNo="+ pluNo+"对应基础资料表DCP_GOODS未启用/已禁用，";
                            }
                            abnormalDetail.setItem(goodsItem.getItem());
                            abnormalDetail.setMemo(memoGoods);
                            abnormalDetail.setPluName(goodsItem.getPluName());
                            abnormalDetail.setStatus("0");
                            memo_errorGoods = memo_errorGoods+memoGoods+"<br>";
                            abnormalHead.getDetail().add(abnormalDetail);
                            
                            isErrorGoods = true;
                        }
                        
                        continue;
                    }
                    
                }
                catch (Exception e)
                {
                    writelog_waimai("【获取商品映射资料】异常："+e.getMessage()+"， 单号orderNo="+orderNo);
                    continue;
                }
            }
            
            if(isErrorGoods)
            {
                abnormalHead.setMemo(memo_errorGoods);
                dcpOrder.setAbnormalList(new ArrayList<orderAbnormal>());
                dcpOrder.getAbnormalList().add(abnormalHead);
                dcpOrder.setExceptionStatus("Y");//异常标识 Y
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            errorMessage.append(e.getMessage());
            writelog_waimai("【获取商品映射资料】异常："+e.getMessage()+"， 单号orderNo="+orderNo);
        }
        
        
    }
    
    /**
     * 根据订单配送门店获取对应的仓库给商品单身赋值
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void updateOrderShippingNoWarehouseInfo(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String langType = "zh_CN";
        
        //查询下配送门店的仓库
        String shippingShopNo =  dcpOrder.getShippingShopNo();
        String warehouseNo = "";
        String warehouseName = "";
        try
        {
            writelog_waimai("【获取配送门店的仓库】配送门店shippingShopNo="+shippingShopNo+"， 单号orderNo="+orderNo);
            String sql_warehouse = "select A.OUT_COST_WAREHOUSE,AL.WAREHOUSE_NAME from dcp_org  A "
                    + " left join dcp_warehouse_lang AL on A.EID=AL.EID  AND   A.ORGANIZATIONNO=AL.ORGANIZATIONNO AND A.OUT_COST_WAREHOUSE = AL.WAREHOUSE AND AL.LANG_TYPE='"+langType+"' "
                    + " where  A.EID='"+eId+"' and A.ORGANIZATIONNO ='"+shippingShopNo+"' ";
            
            writelog_waimai("【获取配送门店的仓库】查询配送门店仓库sql="+sql_warehouse+",配送门店shippingShopNo="+shippingShopNo+"， 单号orderNo="+orderNo);
            
            if(shippingShopNo==null||shippingShopNo.isEmpty())
            {
                return;
            }
            
            List<Map<String, Object>> getShippingshopWarehouseInfo = StaticInfo.dao.executeQuerySQL(sql_warehouse, null);
            
            if(getShippingshopWarehouseInfo==null||getShippingshopWarehouseInfo.isEmpty())
            {
                writelog_waimai("【获取配送门店的仓库】，配送门店shippingShopNo="+ shippingShopNo+"查无资料,单号orderNo="+orderNo);
                return;
            }
            warehouseNo = getShippingshopWarehouseInfo.get(0).get("OUT_COST_WAREHOUSE").toString();
            warehouseName = getShippingshopWarehouseInfo.get(0).get("WAREHOUSE_NAME").toString();
            
            for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
            {
                goodsItem.setWarehouse(warehouseNo);
                goodsItem.setWarehouseName(warehouseName);
            }
            
            
        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            writelog_waimai("【获取配送门店的仓库】异常："+e.getMessage()+"， 单号orderNo="+orderNo);
        }
        
    }
    
    /**
     * 处理订单中套餐商品对应子商品展开
     * @param dcpOrder
     * @param langType
     * @param errorMessage
     * @throws Exception
     */
    public static void updateOrderWithPackage(order dcpOrder, String langType, StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        if(langType==null||langType.isEmpty())
        {
            langType = "zh_CN";
        }
        int scaleCount = 2;//默认小数位
        if(langType.equals("zh_TW"))
        {
            scaleCount = 0;
        }
        
        int scaleCount_qty = 3;//默认数量小数位
        
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String isApportion = dcpOrder.getIsApportion();//是否已分摊过套餐
        
        
        
        if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.WAIMAI)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
        {
            errorMessage.append("渠道类型="+loadDocType+"无需处理套餐商品！ 单号orderNo="+orderNo);
            return;
            
        }
        
        if (isApportion!=null&&isApportion.equals("Y"))
        {
            errorMessage.append("渠道类型="+loadDocType+"已处理套餐商品，标记isApportion=Y， 单号orderNo="+orderNo);
            return;
        }
        
        if(dcpOrder.getGoodsList()==null||dcpOrder.getGoodsList().isEmpty())
        {
            writelog_waimai("【获取套餐商品资料】【是否虚拟商品】渠道类型="+loadDocType+"商品列表为空，无须获取套餐商品， 单号orderNo="+orderNo);
            return;
        }
        
        writelog_waimai("【获取套餐商品资料】【是否虚拟商品】循环开始， 单号orderNo="+orderNo);//先不管效能问题，后续再优化
        
        try
        {
            String pluNoSqlCondition ="";
            for (orderGoodsItem par : dcpOrder.getGoodsList())
            {
                if (par.getPluNo()==null)
                {
                    continue;
                }
                pluNoSqlCondition = "'"+par.getPluNo().replaceAll("'","''")+"'"+","+pluNoSqlCondition;
            }
            pluNoSqlCondition = pluNoSqlCondition.substring(0,pluNoSqlCondition.length()-1);
            
            //查询条码对应编码以及是否套餐标记
            String sql = " select A.PLUNO,A.PLUTYPE,A.VIRTUAL  FROM  dcp_goods A "
                    + " WHERE A.EID='"+eId+"' and A.PLUNO IN ("+pluNoSqlCondition+") ";
            writelog_waimai("【检测是否套餐商品】【是否虚拟商品】根据pluno查询sql="+sql+"，单号orderNO="+orderNo);
            List<Map<String, Object>> getPluNoData = StaticInfo.dao.executeQuerySQL(sql, null);
            if (getPluNoData == null || getPluNoData.isEmpty())
            {
                writelog_waimai("【检测是否套餐商品】【是否虚拟商品】根据pluno查询没有资料，单号orderNO="+orderNo);
                return;
            }
            String pluNoPackageSqlCondition ="";
            writelog_waimai("【检测是否套餐商品】【是否虚拟商品】更新pluNo,packageType,virtual节点信息，单号orderNO="+orderNo);
            //原订单商品资料
            List<orderGoodsItem> goodList_origin = dcpOrder.getGoodsList();
            for (orderGoodsItem par : goodList_origin)
            {
                try
                {
                    String pluNo = par.getPluNo();
                    if(pluNo!=null&&pluNo.isEmpty()==false)
                    {
                        for (Map<String, Object> map : getPluNoData)
                        {
                            if(pluNo.equals(map.get("PLUNO").toString()))
                            {
                                par.setPackageType("1");
                                
                                //是否是虚拟商品
                                try
                                {
                                    par.setVirtual(map.get("VIRTUAL").toString());
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                
                                if(map.get("PLUTYPE")!=null&&map.get("PLUTYPE").toString().equals("PACKAGE"))
                                {
                                    par.setPackageType("2");;//1、正常商品 2、套餐主商品  3、套餐子商品
                                    pluNoPackageSqlCondition = "'"+map.get("PLUNO").toString()+"'"+","+pluNoPackageSqlCondition;
                                }
                                break;
                            }
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
            }
            
            isApportion = "N";
            //***********循环之前已经标记过套餐商品的goods开始添加套餐商品 ***********
            String SDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            writelog_waimai("【检测是否套餐商品】不在重新改写非套餐商品或主商品item项次，单号orderNO="+orderNo);
            //int goodsItem = 0;//项次，有套餐会重新改写
            int goodsPackageItem = goodList_origin.size();//项次，套餐子商品项次，不在重写之前得主商品项次。
            for (orderGoodsItem par : goodList_origin)
            {
                int item_origin = 0;
                try
                {
                    item_origin = Integer.parseInt(par.getItem());
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
                if(item_origin>goodsPackageItem)
                {
                    goodsPackageItem = item_origin;
                }
            }
            writelog_waimai("【查询套餐商品】获取当前商品明细的最大项次item="+goodsPackageItem+"，单号orderNO="+orderNo);
            List<orderGoodsItem> goodList_package = new ArrayList<orderGoodsItem>();
            List<orderAbnormal> abnormalList = dcpOrder.getAbnormalList();
            
            for (orderGoodsItem par : goodList_origin)
            {
                try
                {
                    
                    //goodsItem++;
                    
                    orderGoodsItem goodObj = par;
                    /****************由于item改变，需要更新********************/ //暂时不能注释，防止更新前得异常需要处理
                    if(abnormalList!=null&&abnormalList.isEmpty()==false)
                    {
                        for (orderAbnormal order_Abnormal : abnormalList)
                        {
                            List<orderAbnormalDetail> abnormalDetailList = order_Abnormal.getDetail();
                            if(abnormalDetailList!=null&&abnormalDetailList.isEmpty()==false)
                            {
                                for (orderAbnormalDetail order_AbnormalDetail : abnormalDetailList)
                                {
                                    if(par.getItem().equals(order_AbnormalDetail.getItem()))
                                    {
                                        //order_AbnormalDetail.setItem(goodsItem+"");
                                        order_AbnormalDetail.setItem(goodObj.getItem());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    
                    //goodObj.setItem(goodsItem+"");
                    String pluNO = goodObj.getPluNo();
                    String packageType = goodObj.getPackageType();
                    if(pluNO==null||pluNO.isEmpty()||packageType==null||"2".equals(packageType)==false)//不是套餐，直接赋值给新的jsonArray
                    {
                        goodList_package.add(goodObj);
                        continue;
                    }
                    
                    //如果是套餐商品就要查询数据库进行处理，先不考虑效能了（一般一单套餐也不过有多个）
                    sql = "";
                    sql = " select * from ( "
                            + " SELECT  PLUNO,CONDCOUNT,PCLASSNO,DPLUNO,DUNIT,QTY,EXTRAAMT,SPLIT,NVL(PRICE,0) PRICE,VIRTUAL,NVL(PLUBARCODE,DPLUNO) PLUBARCODE,PLU_NAME, DUNITNAME, "
                            + " ISLIQUOR,isKdsShow,ISKDSCATERINGSHOW,KDSMAXMAKEQTY,ISQTYPRINT,isPrintReturn,isPrintCrossMenu,CROSSPRINTER,kitchenPrinter, "
                            + " row_number() over(partition by pluno,DPLUNO,DUNIT  order by DPLUNO) rn   "
                            + " FROM ( SELECT  A.PLUNO,A.CONDCOUNT,A.PCLASSNO,B.DPLUNO,B.DUNIT,B.QTY,B.EXTRAAMT,B.SPLIT,P.PRICE,P.VIRTUAL,Q.PLUBARCODE,L.PLU_NAME,U.UNAME DUNITNAME ,  "
                            +"  NVL(H1.ISLIQUOR, H2.ISLIQUOR) ISLIQUOR, "
                            + " NVL(H1.isKdsShow, H2.isKdsShow) isKdsShow, "
                            + " NVL(H1.ISKDS_CATERING_SHOW, H2.ISKDS_CATERING_SHOW) ISKDSCATERINGSHOW, "
                            + " NVL(H1.KDS_MAX_MAKE_QTY, H2.KDS_MAX_MAKE_QTY) KDSMAXMAKEQTY, "
                            + " NVL(H1.ISQTYPRINT, H2.ISQTYPRINT) ISQTYPRINT, "
                            + " NVL(H1.isPrintReturn, H2.isPrintReturn) isPrintReturn, "
                            + " NVL(H1.isPrintCrossMenu, H2.isPrintCrossMenu) isPrintCrossMenu, "
                            + " NVL(H1.CROSSPRINTERNAME, H2.CROSSPRINTERNAME) CROSSPRINTER, "
                            + " NVL(H1.PRINTERNAME, H2.PRINTERNAME) kitchenPrinter from DCP_pgoodsclass A "
                            + " inner join DCP_pgoodsclass_detail B on A.EID=B.EID and A.PLUNO=B.PLUNO AND A.PCLASSNO=B.PCLASSNO and A.STATUS=B.STATUS AND A.INVOWAY=B.INVOWAY  "
                            + " left join dcp_goods P on B.EID=P.EID AND B.DPLUNO=P.PLUNO  "
                            + " LEFT JOIN DCP_KITCHENPRINTSET H1 ON P.EID=H1.EID AND P.PLUNO=H1.ID AND H1.TYPE='GOODS' AND H1.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                            + " LEFT JOIN DCP_KITCHENPRINTSET H2 ON P.EID=H2.EID AND P.Category=H2.ID AND H2.TYPE='CATEGORY' AND H2.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                            + " left join dcp_goods_barcode Q on B.EID=Q.EID AND B.DPLUNO=Q.PLUNO AND B.DUNIT=Q.UNIT AND Q.status=100 "
                            + "  left join DCP_goods_lang L on B.EID=L.EID AND B.DPLUNO=L.PLUNO AND L.lang_type='"+langType+"' "
                            + " left join dcp_unit_lang U on B.EID = U.EID AND B.DUNIT = U.UNIT  AND  U.lang_type='"+langType+"' "
                            + " where A.EID='"+eId+"' and A.INVOWAY=1 and A.PLUNO='"+pluNO.replaceAll("'","''")+"'"
                            + " )) where rn=1 order by SPLIT ";//查询下子商品的plubarcode条码档，没有的话因为是主键默认成子商品的pluNo
                    writelog_waimai("【查询套餐商品】【是否虚拟商品】查询子商品sql="+sql+"，单号orderNO="+orderNo);
                    
                    List<Map<String, Object>> getData_package = StaticInfo.dao.executeQuerySQL(sql, null);
                    if (getData_package==null||getData_package.isEmpty())
                    {
                        goodObj.setPackageType("1");
                        goodList_package.add(goodObj);
                        isApportion = "Y";
                        writelog_waimai("【查询套餐商品】查询子商品资料为空(更新该套餐主商品为普通商品)，PLUNO="+pluNO+"，单号orderNO="+orderNo);
                        continue;
                    }
                    
                    goodList_package.add(goodObj);//先添加套餐主商品
                    isApportion = "Y";
                    
                    //int packageMitem = goodsItem;
                    String packageMitem = goodObj.getItem();//goodsItem;
                    BigDecimal price_Mitem = new BigDecimal(goodObj.getPrice());//套餐主商品售价
                    BigDecimal disc_Mitem = new BigDecimal(goodObj.getDisc());//套餐主商品总折扣金额
                    BigDecimal qty_Mitem = new BigDecimal(goodObj.getQty());//套餐主商品数量
                    BigDecimal amt_Mitem = new BigDecimal(goodObj.getAmt());//套餐主商品金额
                    BigDecimal tot_amtOrigin_item = new BigDecimal("0");//套餐子商品原金额合计

                    /***********为了兼容如果发现所有子商品，都不参与折扣，那么都默认参与折扣***********/
                    //套餐主商品总额50，商品A=30;商品B=20;商品C=10
                    //套餐需要分摊总折扣=套餐子商品合计=60-套餐主商品金额50=10
                    //如果 A 不参与折扣分摊，BC参与,那么就是 折扣10 分摊到 BC 商品，按金额比例分摊，
                    //实际套餐子商品参与折扣分摊总金额=商品B+商品C=30,总折扣10，
                    //商品B分摊折扣=10*(20/30)，商品C 最后一个用减
                    boolean isExistSplit = false;//是否存在，参与分摊折扣的套餐子商品
                    BigDecimal tot_amtOrigin_item_split = new BigDecimal("0");//套餐子商品参与折扣分摊的原金额合计
                    int count_split = 0;//套餐子商品参与折扣分摊的种类数


                    List<orderGoodsItem> goodsArray_packageMitem = new ArrayList<orderGoodsItem>();
                    
                    for (Map<String, Object> map : getData_package)
                    {
                        //goodsItem ++;//子商品先累加项次，因为是主键不能重复
                        goodsPackageItem++;//子商品 项次 根据所有主商品 项次之后累加
                        if (map.get("PRICE")==null||map.get("PRICE").toString().trim().isEmpty())
                        {
                            map.put("PRICE","0");
                        }
                        if (map.get("QTY")==null||map.get("QTY").toString().trim().isEmpty())
                        {
                            map.put("QTY","1");//数量默认1吧 ，不能为空
                        }
                        
                        BigDecimal price_origin = new BigDecimal(map.get("PRICE").toString());//子商品原售价
                        BigDecimal qty_origin = new BigDecimal(map.get("QTY").toString());//套餐子商品设置的数量
                        BigDecimal qty = qty_origin.multiply(qty_Mitem);//子商品实际数量=主商品数量*套餐子商品设置的数量
                        BigDecimal amt_origin = qty.multiply(price_origin);//子商品原金额 = 子商品原售价*子商品实际数量
                        tot_amtOrigin_item = tot_amtOrigin_item.add(amt_origin);//子商品原金额合计
                        
                        orderGoodsItem goodObj_item = new orderGoodsItem();
                        goodObj_item.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                        goodObj_item.setMessages(new ArrayList<orderGoodsItemMessage>());
                        
                        //goodObj_item.setItem(goodsItem+"");
                        goodObj_item.setItem(goodsPackageItem+"");
                        goodObj_item.setPackageMitem(packageMitem+"");//主商品项次
                        goodObj_item.setPackageType("3");//1、正常商品 2、套餐主商品  3、套餐子商品
                        goodObj_item.setPluNo(map.get("DPLUNO").toString());
                        goodObj_item.setPluBarcode(map.get("PLUBARCODE").toString());
                        goodObj_item.setPluName(map.get("PLU_NAME").toString());
                        goodObj_item.setSpecName("");
                        goodObj_item.setAttrName("");
                        goodObj_item.setFeatureNo(" ");
                        goodObj_item.setFeatureName("");
                        goodObj_item.setsUnit(map.get("DUNIT").toString());
                        goodObj_item.setPrice(Double.parseDouble(map.get("PRICE").toString()));
                        goodObj_item.setOldPrice(Double.parseDouble(map.get("PRICE").toString()));
                        goodObj_item.setQty(qty.setScale(scaleCount_qty, BigDecimal.ROUND_HALF_UP).doubleValue());
                        goodObj_item.setAmt(amt_origin.setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                        goodObj_item.setOldAmt(amt_origin.setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                        goodObj_item.setDisc(0);////后面更新
                        goodObj_item.setBoxNum(0);
                        goodObj_item.setBoxPrice(0);
                        goodObj_item.setsUnitName(map.get("DUNITNAME").toString());
                        goodObj_item.setIsMemo("N");
                        goodObj_item.setWarehouse(goodObj.getWarehouse());//取套餐主商品
                        goodObj_item.setWarehouseName(goodObj.getWarehouseName());//取套餐主商品
                        goodObj_item.setGoodsGroup(goodObj.getGoodsGroup());//取套餐主商品
                        
                        goodObj_item.setIsLiquor(map.getOrDefault("ISLIQUOR","N").toString());
                        goodObj_item.setKdsMaxMakeQty(Convert.toBigDecimal(map.getOrDefault("KDSMAXMAKEQTY",0),BigDecimal.ZERO));
                        goodObj_item.setIsQtyPrint(map.getOrDefault("ISQTYPRINT","N").toString());
                        goodObj_item.setIsPrintReturn(map.getOrDefault("ISPRINTRETURN","N").toString());
                        goodObj_item.setIsPrintCrossMenu(map.getOrDefault("ISPRINTCROSSMENU","N").toString());
                        goodObj_item.setCrossPrinter(map.getOrDefault("CROSSPRINTER","").toString());
                        goodObj_item.setKitchenPrinter(map.getOrDefault("KITCHENPRINTER","").toString());
                        
                        try
                        {
                            goodObj_item.setVirtual(map.get("VIRTUAL").toString());
                        } catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                        String spilt = map.getOrDefault("SPLIT","").toString();
                        if ("1".equals(spilt))
                        {
                            spilt = "1";
                            isExistSplit = true;
                            tot_amtOrigin_item_split = tot_amtOrigin_item_split.add(amt_origin);//子商品原金额合计
                            count_split++;
                        }
                        else
                        {
                            spilt = "0";
                        }
                        goodObj_item.setSplit(spilt);
                        
                        goodsArray_packageMitem.add(goodObj_item);
                        
                    }
                    
                    //分摊折扣，根据原价金额占比分摊
                    if(amt_Mitem.compareTo(tot_amtOrigin_item)==0||tot_amtOrigin_item.compareTo(BigDecimal.ZERO)==0)//主商品金额=子商品原金额合计 不用分摊 ,子商品合计金额=0，也无法分摊
                    {
						/*if (amt_Mitem.compareTo(tot_amtOrigin_item)==1)
						{
							writelog_waimai("【存在套餐商品】主商品金额大于套餐子商品金额合计,不处理分摊。单号orderNO="+orderNo);
						}*/
                        if(amt_Mitem.compareTo(tot_amtOrigin_item)==0)
                        {
                            writelog_waimai("【存在套餐商品】主商品金额=套餐子商品金额合计,不处理分摊。单号orderNO="+orderNo);
                            for(int j=0;j<goodsArray_packageMitem.size();j++)
                            {
                                goodList_package.add(goodsArray_packageMitem.get(j));
                            }
                        }
                        else
                        {
                            writelog_waimai("【存在套餐商品】套餐子商品金额合计=0,单号orderNO="+orderNo);
                            //项次平摊
                            
                            //BigDecimal tot_amt_package_item = amt_Mitem.setScale(scaleCount, BigDecimal.ROUND_HALF_UP);//子商品总金额
                            BigDecimal amt_package_item_add = new BigDecimal("0");//子商品 折扣合计，
                            int size = goodsArray_packageMitem.size();//商品种类数
                            if (isExistSplit)
                            {
                                size = count_split;
                            }
                            for(int j=0;j<goodsArray_packageMitem.size();j++)
                            {
                                orderGoodsItem itemObj = goodsArray_packageMitem.get(j);
                                if (isExistSplit && "0".equals(itemObj.getSplit()))
                                {
                                    //存在参与折扣分摊的话且这个商品不参与折扣分摊
                                    goodList_package.add(itemObj);
                                    continue;
                                }
                                //由于前面排序了，所有参与折扣分摊的，商品肯定是最后一个
                                if(j == goodsArray_packageMitem.size()-1)//最后一笔 用减
                                {
                                    BigDecimal amt_origin = new BigDecimal(itemObj.getAmt());
                                    BigDecimal qty = new BigDecimal(itemObj.getQty());
                                    BigDecimal amt = amt_Mitem.subtract(amt_package_item_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal price_deal = new BigDecimal("0");
                                    try
                                    {
                                        price_deal = amt.divide(qty, scaleCount, BigDecimal.ROUND_UP);// 始终大于
                                        // AMT
                                        
                                    } catch (Exception e)
                                    {
                                        // TODO: handle exception
                                    }
                                    
                                    itemObj.setPrice(price_deal.doubleValue());
                                    itemObj.setDisc(0);// 后面更新
                                    itemObj.setAmt(amt.doubleValue());
                                    
                                    BigDecimal disc_real = new BigDecimal("0");
                                    disc_real = amt_origin.subtract(amt).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                    
                                    itemObj.setDisc(disc_real.doubleValue());
                                    
                                }
                                else
                                {
                                    BigDecimal amt_origin = new BigDecimal(itemObj.getAmt());
                                    
                                    BigDecimal rate = new BigDecimal(1).divide(new BigDecimal(size),4,RoundingMode.HALF_UP);//根据项次 平摊
                                    BigDecimal amt = amt_Mitem.multiply(rate).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);//主商品金额*分摊比例=当前实际金额
                                    
                                    amt_package_item_add = amt_package_item_add.add(amt);
                                    BigDecimal qty =  new BigDecimal(itemObj.getQty());
                                    BigDecimal price_deal = new BigDecimal("0");
                                    try
                                    {
                                        price_deal = amt.divide(qty,scaleCount, BigDecimal.ROUND_UP);//始终大于 AMT
                                        
                                    }
                                    catch (Exception e)
                                    {
                                        // TODO: handle exception
                                    }
                                    
                                    itemObj.setPrice(price_deal.doubleValue());
                                    itemObj.setDisc(0);// 后面更新
                                    itemObj.setAmt(amt.doubleValue());
                                    
                                    BigDecimal disc_real = new BigDecimal("0");
                                    disc_real = amt_origin.subtract(amt).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                    itemObj.setDisc(disc_real.doubleValue());
                                }
                                
                                goodList_package.add(itemObj);
                                
                            }
                            
                            
                            
                        }
                        
                    }
                    else
                    {
                        BigDecimal tot_disc_item = tot_amtOrigin_item.subtract(amt_Mitem).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);//总折扣=子商品原价金额-主商品金额
                        BigDecimal disc_add = new BigDecimal("0");//子商品 折扣合计，
                        BigDecimal tot_amtOrigin_item_real = tot_amtOrigin_item;//套餐子商品参与分摊的原价金额合计
                        if (isExistSplit)
                        {
                            tot_amtOrigin_item_real = tot_amtOrigin_item_split;
                        }


                        for(int j=0;j<goodsArray_packageMitem.size();j++)
                        {
                            orderGoodsItem itemObj = goodsArray_packageMitem.get(j);
                            if (isExistSplit && "0".equals(itemObj.getSplit()))
                            {
                                //存在参与折扣分摊的话且这个商品不参与折扣分摊
                                goodList_package.add(itemObj);
                                continue;
                            }
                            if(j == goodsArray_packageMitem.size()-1)//最后一笔 用减
                            {
                                BigDecimal amt_origin = new BigDecimal(itemObj.getAmt());
                                BigDecimal qty = new BigDecimal(itemObj.getQty());
                                BigDecimal disc = tot_disc_item.subtract(disc_add).setScale(scaleCount,
                                        BigDecimal.ROUND_HALF_UP);
                                BigDecimal amt = amt_origin.subtract(disc).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                BigDecimal price_deal = new BigDecimal("0");
                                try
                                {
                                    price_deal = amt.divide(qty, scaleCount, BigDecimal.ROUND_UP);// 始终大于
                                    // AMT
                                    
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                
                                itemObj.setPrice(price_deal.doubleValue());
                                itemObj.setDisc(0);// 后面更新
                                itemObj.setAmt(amt.doubleValue());
                                
                                BigDecimal disc_real = new BigDecimal("0");
                                disc_real = amt_origin.subtract(amt).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);;
                                
                                itemObj.setDisc(disc_real.doubleValue());
                                
                            }
                            else
                            {
                                BigDecimal amt_origin = new BigDecimal(itemObj.getAmt());
                                BigDecimal rate = amt_origin.divide(tot_amtOrigin_item_real, 4, BigDecimal.ROUND_HALF_UP);//根据子商品金额比例分摊，当前子商品原金额/子商品原金额合计
                                BigDecimal amt = amt_Mitem.multiply(rate).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);//主商品金额*分摊比例=当前实际金额
                                
                                BigDecimal disc = amt_origin.subtract(amt).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                disc_add = disc_add.add(disc);
                                BigDecimal qty =  new BigDecimal(itemObj.getQty());
                                BigDecimal price_deal = new BigDecimal("0");
                                try
                                {
                                    price_deal = amt.divide(qty,scaleCount, BigDecimal.ROUND_UP);//始终大于 AMT
                                    
                                }
                                catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                
                                itemObj.setPrice(price_deal.doubleValue());
                                itemObj.setDisc(0);// 后面更新
                                itemObj.setAmt(amt.doubleValue());
                                
                                BigDecimal disc_real = new BigDecimal("0");
                                disc_real = amt_origin.subtract(amt).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);;
                                itemObj.setDisc(disc_real.doubleValue());
                            }
                            
                            goodList_package.add(itemObj);
                            
                        }
                        
                    }
                    
                    
                    
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    writelog_waimai("【检测是否套餐商品】添加套餐子商品,异常:"+e.getMessage()+",单号orderNO="+orderNo);
                    continue;
                }
            }
            
            dcpOrder.setGoodsList(goodList_package);
            dcpOrder.setIsApportion(isApportion);
            if(isApportion.equals("Y"))
            {
                writelog_waimai("【存在套餐商品】更新goods节点信息完成,单号orderNO="+orderNo);
            }
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimai("【检测是否套餐商品】更新goods节点信息,异常:"+e.getMessage()+",单号orderNO="+orderNo);
        }
        //霸王需求规格和属性拆分
        try
        {
            waimaiOrderSplitGoods(dcpOrder,langType,errorMessage);
        }
        catch (Exception e)
        {
        
        }
        
    }
    
    /**
     * 第三方外卖平台推送订单退单或者取消消息时，如果有生成销售单-->需要生成销退单，如果没有生成销售单-->需要生成退订单
     *
     * @param dcpOrder
     * @param refundBdate
     * @param errorMessage
     * @throws Exception
     */
    public static void OrderRefundOrCancelProcess(order dcpOrder, String refundBdate, StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【第三方外卖平台推送订单退单或者取消处理】单号orderNo="+orderNo+",";
        try
        {
            
            boolean checkPara = false;
            if(eId==null||eId.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单eId=为空,");
                checkPara = true;
                
            }
            if(orderNo==null||orderNo.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单orderNo=为空,");
                checkPara = true;
                
            }
            if(loadDocType==null||loadDocType.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单loadDocType=为空,");
                checkPara = true;
                
            }
            if(channelId==null||channelId.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单channelId=为空,");
                checkPara = true;
                
            }
            if(status==null||status.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单状态status=为空,");
                checkPara = true;
                
            }
            
            if(checkPara)
            {
                return;
            }
            
            //只处理 取消 和 退单的
            if(status.equals("3")||status.equals("12"))
            {
            
            }
            else
            {
                errorMessage.append(logBeginStr+"订单状态status="+status+"，无须处理");
                return;
            }
            
            //判断有没有订转销得锁
            boolean isExistSaleLock = HelpTools.IsExistWaiMaiOrderToSaleOrRefundRedisLock("0",eId,orderNo);
            if (isExistSaleLock)
            {
                //由于这段操作必须要走，所有不用等订转销解锁了。平台要求3秒内必须返回，等订转销服务1500毫秒
                Thread.sleep(1500);
            }
            
            String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String stime = new SimpleDateFormat("HHmmss").format(new Date());
            String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            String tran_time = update_time;
            String bdate = refundBdate;
            if(bdate==null||bdate.isEmpty())
            {
                bdate = sdate;
            }
            
            int otype = 0;//退单来源类型
            
            ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
            //查询来源类型是订单，来源单号是订单单号的销售单号
            String sql = "select  SALENO,SHOPID from dcp_sale where ofno='"+orderNo+"' and eid='"+eId+"' ";
            HelpTools.writelog_waimai(logBeginStr+"有没有生成销售单，查询sql:"+sql);
            List<Map<String, Object>> sourceSaleNoList = StaticInfo.dao.executeQuerySQL(sql, null);
            
            //如果存在的话，需要生成销退单
            if(sourceSaleNoList!=null&&sourceSaleNoList.size()>0)
            {
                String sourceSaleNo = sourceSaleNoList.get(0).get("SALENO").toString();
                String shopid = sourceSaleNoList.get(0).get("SHOPID").toString();
                String saleno = "RE"+sourceSaleNo;//退单的单号saleno
                String saleno_partRefund = "RE"+sourceSaleNo+"_01";//之前部分退单的单号
                String saleno_partRefund_tv = sourceSaleNo+"_01";//红冲之前部分退单的单号
                int type = 1;//退单的类型type
                String typename = "原单退";
                String ofno = sourceSaleNo;//退单ofno的来源单号
                
                sql = "";
                sql = "select  SALENO,SHOPID from dcp_sale where type=1 and saleno='"+saleno+"' and eid='"+eId+"' and shopid='"+shopid+"' ";
                HelpTools.writelog_waimai(logBeginStr+"对应销售单单号saleNo="+sourceSaleNo+",有没有生成销退单单，查询sql:"+sql);
                
                List<Map<String, Object>> sourceRefundSaleNoList = StaticInfo.dao.executeQuerySQL(sql, null);
                if(sourceRefundSaleNoList!=null&&sourceRefundSaleNoList.size()>0)
                {
                    HelpTools.writelog_waimai(logBeginStr+"对应销售单单号saleNo="+sourceSaleNo+",已生成销退单单，无须再生成");
                    return;
                }
                
                
                //不能直接删除部分退单得单据(为了账单平衡，这样才完全平)
                String partRefundSql = "";
                partRefundSql = " select saleno from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+saleno_partRefund+"'";
                List<Map<String, Object>> partRefundSaleNo = StaticInfo.dao.executeQuerySQL(partRefundSql, null);
                if(partRefundSaleNo!=null&&partRefundSaleNo.size()>0)
                {
                    HelpTools.writelog_waimai(logBeginStr+"存在部分销售退单，需要红冲，对应部分退销退单号saleNo="+saleno_partRefund);
                    
                    StringBuffer strBuff_hc = new StringBuffer("");
                    int type_hc = 0;//销售单
                    String typename_hc = "红冲部分销退单";
                    String ofno_hc = saleno_partRefund;
                    //生成单头语句
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into DCP_SALE (");//分区字段
                    strBuff_hc.append(" eid,shopid,saleno,trno,ver_num,legalper,machine,type,typename,bdate,squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,otype,ofno,sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                            + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                            + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                            + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                            + ",sdate,stime,evaluate,isuploaded,update_time,tran_time,rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,WAIMAIMERRECEIVEMODE,PARTITION_DATE)");//分区字段已处理
                    
                    strBuff_hc.append("select eid,shopid,'"+saleno_partRefund_tv+"',trno,ver_num,legalper,machine,"+type_hc+",'"+typename_hc+"','"+bdate+"',squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,'"+otype+"','"+ofno_hc+"',sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                            + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                            + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                            + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                            + ",'"+sdate+"','"+stime+"',evaluate,'N','"+update_time+"','"+tran_time+"',rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,WAIMAIMERRECEIVEMODE,'"+bdate+"'");//分区字段已处理
                    
                    strBuff_hc.append(" from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+saleno_partRefund+"' ");
                    String execsql = strBuff_hc.toString();
                    ExecBean exSale = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale));
                    
                    //生成商品单身
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into DCP_SALE_DETAIL (");//分区字段
                    strBuff_hc.append(" eid, shopid, saleno, warehouse, item, oitem, clerkno, sellername, accno, tableno, dealtype, coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                            + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                            + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, bdate, sdate, stime, tran_time, prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理
                    
                    strBuff_hc.append(" select eid, shopid, '"+saleno_partRefund_tv+"', warehouse, item, oitem, clerkno, sellername, accno, tableno, '1', coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                            + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                            + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,'"+bdate+"'");//分区字段已处理
                    
                    strBuff_hc.append(" from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+saleno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    ExecBean exSale_detail = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_detail));
                    
                    //生成商品单身折扣
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into DCP_SALE_DETAIL_agio (");//分区字段
                    strBuff_hc.append("  eid, shopid, saleno, mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
                    
                    strBuff_hc.append(" select  eid, shopid, '"+saleno_partRefund_tv+"', mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");
                    
                    strBuff_hc.append(" from DCP_SALE_DETAIL_agio where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+saleno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    ExecBean exSale_detail_agio = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_detail_agio));
                    
                    //生成付款单
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into DCP_SALE_PAY (");//分区字段
                    strBuff_hc.append("eid, shopid, saleno, item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, isorderpay, prepaybillno, authcode, isturnover, status, bdate, sdate, stime, tran_time, paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PARTITION_DATE )");	//分区字段已处理
                    strBuff_hc.append(" select eid, shopid, '"+saleno_partRefund_tv+"', item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, 'N', prepaybillno, authcode, isturnover, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,'"+bdate+"'");//分区字段已处理
                    
                    strBuff_hc.append(" from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+saleno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    ExecBean exSale_pay = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_pay));
                    
                    //库存流水账生成
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" select * from dcp_stock_detail where billtype=21");
                    strBuff_hc.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+saleno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    
                    HelpTools.writelog_waimai(logBeginStr+"【红冲部分销退单】查询该订单对应的销售单号SALENO="+saleno_partRefund+" 生成的库存流水账，查询sql:"+execsql);
                    //营业日期 -存储过程
                    String stockChange_BDATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    if(bdate!=null&&bdate.isEmpty()==false)
                    {
                        stockChange_BDATE = bdate;
                        if(bdate.length()==8)
                        {
                            stockChange_BDATE = bdate.substring(0,4)+"-"+bdate.substring(4,6)+"-"+bdate.substring(6,8);
                        }
                    }
                    List<Map<String, Object>> getQData_stockDetail = StaticInfo.dao.executeQuerySQL(execsql, null);
                    
                    
                    //流水表没有到历史流水表里查
                    if (getQData_stockDetail==null || getQData_stockDetail.size()==0)
                    {
                        strBuff_hc = new StringBuffer("");
                        strBuff_hc.append(" select * from dcp_stock_detail_static where billtype=21");
                        strBuff_hc.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+saleno_partRefund+"' ");
                        execsql = "";
                        execsql = strBuff_hc.toString();
                        HelpTools.writelog_waimai(logBeginStr+"【红冲部分销退单】查询该订单对应的销售单号SALENO="+saleno_partRefund+" 生成的库存流水账，查询sql:"+execsql);
                        getQData_stockDetail = StaticInfo.dao.executeQuerySQL(execsql, null);
                    }
                    
                    
                    
                    if(getQData_stockDetail!=null&&getQData_stockDetail.isEmpty()==false)
                    {
                        String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopid);
                        String procedure="SP_DCP_StockChange";
                        for (Map<String, Object> map : getQData_stockDetail)
                        {
                            
                            Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1,map.get("EID").toString());                                       //--企业ID
                            inputParameter.put(2,map.get("ORGANIZATIONNO").toString());                                    //--组织
                            inputParameter.put(3,"20");                                      //--单据类型
                            inputParameter.put(4,saleno_partRefund_tv);	                                 //--单据号
                            inputParameter.put(5,map.get("ITEM").toString());            //--单据行号
                            inputParameter.put(6,"-1");                                      //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(7,stockChange_BDATE);           //--营业日期 yyyy-MM-dd
                            inputParameter.put(8,map.get("PLUNO").toString());           //--品号
                            inputParameter.put(9,map.get("FEATURENO").toString());       //--特征码
                            inputParameter.put(10,map.get("WAREHOUSE").toString());                                //--仓库
                            inputParameter.put(11,map.get("BATCHNO").toString());       //--批号
                            inputParameter.put(12,map.get("UNIT").toString());          //--交易单位
                            inputParameter.put(13,map.get("QTY").toString());           //--交易数量
                            inputParameter.put(14,map.get("BASEUNIT").toString());       //--基准单位
                            inputParameter.put(15,map.get("BASEQTY").toString());        //--基准数量
                            inputParameter.put(16,map.get("UNITRATIO").toString());     //--换算比例
                            inputParameter.put(17,map.get("PRICE").toString());          //--零售价
                            inputParameter.put(18,map.get("AMT").toString());            //--零售金额
                            inputParameter.put(19,map.get("DISTRIPRICE").toString());    //--进货价
                            inputParameter.put(20,map.get("DISTRIAMT").toString());      //--进货金额
                            inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                            inputParameter.put(22,map.get("PRODDATE").toString());      //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(23,sdate);                                  //--单据日期
                            inputParameter.put(24,"");                                       //--异动原因
                            inputParameter.put(25,"红冲部分销退单");                                //--异动描述
                            inputParameter.put(26,"");                                //--操作员
                            
                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            DataPB.add(new DataProcessBean(pdb));
                            HelpTools.writelog_waimai("********** 调用存储过程SP_DCP_StockChange参数："+inputParameter.toString());
                            
                            
                        }
                    }
                    
                    try {
                        StaticInfo.dao.useTransactionProcessData(DataPB);
                        DataPB.clear();
                        HelpTools.writelog_fileName(logBeginStr+"【红冲部分销退单】处理成功！红冲单号SaleNo:"+saleno_partRefund_tv,"partRefund");
                        HelpTools.writelog_waimai(logBeginStr+"【红冲部分销退单】处理成功！红冲单号SaleNo:"+saleno_partRefund_tv);
                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_fileName(logBeginStr+"【红冲部分销退单】处理异常！红冲单号SaleNo:"+saleno_partRefund_tv+",异常："+e.getMessage(),"partRefund");
                        HelpTools.writelog_waimai(logBeginStr+"【红冲部分销退单】处理异常！红冲单号SaleNo:"+saleno_partRefund_tv+",异常："+e.getMessage());
                        DataPB.clear();
                    }
                    
                    
                }
                
                DataPB.clear();//前面的先执行，不需要一个事务
                
                
                StringBuffer strBuff = new StringBuffer("");
                //生成单头语句
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE (");//分区字段
                strBuff.append(" eid,shopid,saleno,trno,ver_num,legalper,machine,type,typename,bdate,squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,otype,ofno,sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                        + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                        + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                        + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                        + ",sdate,stime,evaluate,isuploaded,update_time,tran_time,rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,WAIMAIMERRECEIVEMODE,PARTITION_DATE)");//分区字段已处理
                
                strBuff.append("select eid,shopid,'"+saleno+"',trno,ver_num,legalper,machine,"+type+",'"+typename+"','"+bdate+"',squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,'"+otype+"','"+ofno+"',sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                        + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                        + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                        + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                        + ",'"+sdate+"','"+stime+"',evaluate,'N','"+update_time+"','"+tran_time+"',rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,WAIMAIMERRECEIVEMODE,'"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                String execsql = strBuff.toString();
                ExecBean exSale = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale));
                
                //生成商品单身
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_DETAIL (");//分区字段
                strBuff.append(" eid, shopid, saleno, warehouse, item, oitem, clerkno, sellername, accno, tableno, dealtype, coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                        + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                        + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, bdate, sdate, stime, tran_time, prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理
                
                strBuff.append(" select eid, shopid, '"+saleno+"', warehouse, item, oitem, clerkno, sellername, accno, tableno, '2', coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                        + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                        + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,'"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from DCP_SALE_DETAIL where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail));
                
                //生成商品单身折扣
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_DETAIL_agio (");//分区字段
                strBuff.append("  eid, shopid, saleno, mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
                
                strBuff.append(" select  eid, shopid, '"+saleno+"', mitem, item, qty, amt, disc, realdisc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, dcopno, dctime, memo, status, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");
                
                strBuff.append(" from DCP_SALE_DETAIL_agio where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail_agio = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail_agio));
                
                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_PAY (");//分区字段
                strBuff.append("eid, shopid, saleno, item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, isorderpay, prepaybillno, authcode, isturnover, status, bdate, sdate, stime, tran_time, paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PARTITION_DATE )");	//分区字段已处理
                strBuff.append(" select eid, shopid, '"+saleno+"', item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, 'N', prepaybillno, authcode, isturnover, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,'"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from DCP_SALE_PAY where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay));
                
                //库存流水账生成
                strBuff = new StringBuffer("");
                strBuff.append(" select * from dcp_stock_detail where billtype=20");
                strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                
                HelpTools.writelog_waimai("【调用订单退单DCP_OrderRefundAgreeOrReject】查询该订单对应的销售单号SALENO="+sourceSaleNo+" 生成的库存流水账，查询sql:"+execsql);
                //营业日期 -存储过程
                String stockChange_BDATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                if(bdate!=null&&bdate.isEmpty()==false)
                {
                    stockChange_BDATE = bdate;
                    if(bdate.length()==8)
                    {
                        stockChange_BDATE = bdate.substring(0,4)+"-"+bdate.substring(4,6)+"-"+bdate.substring(6,8);
                    }
                }
                List<Map<String, Object>> getQData_stockDetail = StaticInfo.dao.executeQuerySQL(execsql, null);
                
                
                //流水表没有到历史流水表里查
                if (getQData_stockDetail==null || getQData_stockDetail.size()==0)
                {
                    strBuff = new StringBuffer("");
                    strBuff.append(" select * from dcp_stock_detail_static where billtype=20");
                    strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                    execsql = "";
                    execsql = strBuff.toString();
                    HelpTools.writelog_waimai(logBeginStr+"查询该订单对应的销售单号SALENO="+sourceSaleNo+" 生成的库存流水账，查询sql:"+execsql);
                    getQData_stockDetail = StaticInfo.dao.executeQuerySQL(execsql, null);
                }
                
                
                
                if(getQData_stockDetail!=null&&getQData_stockDetail.isEmpty()==false)
                {
                    String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopid);
                    String procedure="SP_DCP_StockChange";
                    for (Map<String, Object> map : getQData_stockDetail)
                    {
                        
                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,map.get("EID").toString());                                       //--企业ID
                        inputParameter.put(2,map.get("ORGANIZATIONNO").toString());                                    //--组织
                        inputParameter.put(3,"21");                                      //--单据类型
                        inputParameter.put(4,saleno);	                                 //--单据号
                        inputParameter.put(5,map.get("ITEM").toString());            //--单据行号
                        inputParameter.put(6,"1");                                      //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(7,stockChange_BDATE);           //--营业日期 yyyy-MM-dd
                        inputParameter.put(8,map.get("PLUNO").toString());           //--品号
                        inputParameter.put(9,map.get("FEATURENO").toString());       //--特征码
                        inputParameter.put(10,map.get("WAREHOUSE").toString());                                //--仓库
                        inputParameter.put(11,map.get("BATCHNO").toString());       //--批号
                        inputParameter.put(12,map.get("UNIT").toString());          //--交易单位
                        inputParameter.put(13,map.get("QTY").toString());           //--交易数量
                        inputParameter.put(14,map.get("BASEUNIT").toString());       //--基准单位
                        inputParameter.put(15,map.get("BASEQTY").toString());        //--基准数量
                        inputParameter.put(16,map.get("UNITRATIO").toString());     //--换算比例
                        inputParameter.put(17,map.get("PRICE").toString());          //--零售价
                        inputParameter.put(18,map.get("AMT").toString());            //--零售金额
                        inputParameter.put(19,map.get("DISTRIPRICE").toString());    //--进货价
                        inputParameter.put(20,map.get("DISTRIAMT").toString());      //--进货金额
                        inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                        inputParameter.put(22,map.get("PRODDATE").toString());      //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(23,sdate);                                  //--单据日期
                        inputParameter.put(24,"");                                       //--异动原因
                        inputParameter.put(25,"销售单退单");                                //--异动描述
                        inputParameter.put(26,"");                                //--操作员
                        
                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        DataPB.add(new DataProcessBean(pdb));
                        HelpTools.writelog_waimai("********** 调用存储过程SP_DCP_StockChange参数："+inputParameter.toString());
                        
                        
                    }
                }
                
                
                //添加订单状态
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(dcpOrder.getChannelId());
                onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                onelv1.seteId(eId);
                onelv1.setOpName("");
                onelv1.setOpNo("");
                onelv1.setShopNo(dcpOrder.getShopNo());
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo(dcpOrder.getMachShopNo());
                onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
                String statusType = "";
                String updateStaus = "99";//其他
                statusType = "99";// 订单状态
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = "其他";
                String statusTypeName = "其他状态";
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                String memo = "生成销退单成功";
                
                onelv1.setMemo(memo);
                onelv1.setDisplay("0");
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
            }
            else//没有生成销售单，需要生成退订单
            {
                String sourceOrderNo = orderNo;
                String orderno = "RE"+sourceOrderNo;//退订单单号
                String orderno_partRefund = "RE"+sourceOrderNo+"_01";//部分退订单单号
                String orderno_partRefund_hc = sourceOrderNo+"_01";//红冲部分退订单单号
                
                String billno = UUID.randomUUID().toString().replace("-", "");//收款单号
                String billtype = "-1";//单据类型
                String direction = "-1";//金额方向:1、-1
                String usetype = "refund";//款项用途：front-预付款 refund-退款 final-尾款
                
                //判断下有没有生成过 退订单了
                sql = "";
                sql = "select  ORDERNO from DCP_ORDER where billtype='-1' and orderno='"+orderno+"' and eid='"+eId+"' ";
                HelpTools.writelog_waimai(logBeginStr+"对应退订单号orderNo="+orderno+",有没有生成退订单，查询sql:"+sql);
                
                List<Map<String, Object>> sourceRefundOrderNoList = StaticInfo.dao.executeQuerySQL(sql, null);
                if(sourceRefundOrderNoList!=null&&sourceRefundOrderNoList.size()>0)
                {
                    HelpTools.writelog_waimai(logBeginStr+"对应退订单号orderNo="+orderno+",已生成退订单，无须再生成");
                    return;
                }
                
                //不能直接删除部分退单得单据(为了账单平衡，这样才完全平)
                String partRefundSql = "";
                partRefundSql = " select * from DCP_ORDER where eid='"+eId+"' and orderno='"+orderno_partRefund+"' ";
                List<Map<String, Object>> partRefundOrderNo = StaticInfo.dao.executeQuerySQL(partRefundSql, null);
                if(partRefundOrderNo!=null&&partRefundOrderNo.size()>0)
                {
                    HelpTools.writelog_waimai(logBeginStr+"存在部分退订单，需要红冲，对应部分退订单号orderNo="+orderno_partRefund);
                    StringBuffer strBuff_hc = new StringBuffer("");
                    String billType_hc = "1";//订单
                    String typename_hc = "红冲部分退订单";
                    String ofno_hc = orderno_partRefund;
                    String usetype_hc = "front";//款项用途：front-预付款 refund-退款 final-尾款
                    //生成单头语句
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into DCP_ORDER (");//分区字段
                    strBuff_hc.append(" eid, billtype, orderno, requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                            + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, refundamt,REFUNDAMT_MERRECEIVE,REFUNDAMT_CUSTPAYREAL"
                            + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                            + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, refundsourcebillno, refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                            + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                            + ", stime, create_datetime, complete_datetime, bdate, tran_time, update_time, process_status, peopletype, printcount, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DOWNGRADED,WAIMAIMERRECEIVEMODE,PARTITION_DATE )");//分区字段已处理
                    
                    strBuff_hc.append("select eid, '"+billType_hc+"', '"+orderno_partRefund_hc+"', requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                            + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, payamt,tot_amt_merreceive,tot_amt_custpayreal"
                            + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                            + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, '"+ofno_hc+"', refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                            + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                            + ", '"+tran_time+"', '"+tran_time+"', '', '"+bdate+"', '"+tran_time+"', '"+update_time+"', 'N', peopletype, 0, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DOWNGRADED,WAIMAIMERRECEIVEMODE,'"+bdate+"'");//分区字段已处理
                    
                    strBuff_hc.append(" from DCP_ORDER where eid='"+eId+"'  and orderno='"+orderno_partRefund+"' ");
                    String execsql = strBuff_hc.toString();
                    ExecBean exSale = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale));
                    
                    //生成商品单身
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into DCP_ORDER_DETAIL (");//分区字段
                    strBuff_hc.append(" eid, orderno, item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, oitem, oreitem, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                            + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, stime, tran_time, RUNPICKQTY, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理
                    
                    strBuff_hc.append("select eid, '"+orderno_partRefund_hc+"', item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, item, item, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                            + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, '"+tran_time+"', '"+tran_time+"', qty, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,'"+bdate+"'");//分区字段已处理
                    
                    strBuff_hc.append(" from DCP_ORDER_DETAIL where qty>=0 and eid='"+eId+"' and orderno='"+orderno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    ExecBean exSale_detail = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_detail));
                    
                    //生成商品单身折扣
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into DCP_ORDER_DETAIL_agio (");//分区字段
                    strBuff_hc.append(" eid, orderno, mitem, item, qty, amt, inputdisc, realdisc, disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
                    
                    strBuff_hc.append(" select eid, '"+orderno_partRefund_hc+"', mitem, item, qty, amt, inputdisc, realdisc, disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");
                    
                    strBuff_hc.append(" from DCP_ORDER_DETAIL_agio where eid='"+eId+"' and orderno='"+orderno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    ExecBean exSale_detail_agio = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_detail_agio));
                    
                    //生成付款单
                    String billno_hc = UUID.randomUUID().toString().replace("-", "");//收款单号
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into Dcp_Order_Pay_Detail (");//分区字段
                    strBuff_hc.append(" eid, billno, item, billdate, bdate, sourcebilltype, sourcebillno, loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime, tran_time,SOURCEHEADBILLNO "
                            + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYTYPE,PARTITION_DATE )");//分区字段已处理
                    strBuff_hc.append(" select eid, '"+billno_hc+"', item, '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno_partRefund_hc+"', loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"','"+orderno_partRefund+"' "
                            + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYTYPE,'"+bdate+"'");//分区字段已处理
                    
                    strBuff_hc.append(" from Dcp_Order_Pay_Detail where eid='"+eId+"' and SOURCEBILLNO='"+orderno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    ExecBean exSale_pay_detail = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_pay_detail));
                    
                    //生成付款单
                    strBuff_hc = new StringBuffer("");
                    strBuff_hc.append(" insert into Dcp_Order_pay (");//分区字段
                    strBuff_hc.append("eid, billno, billdate, bdate, sourcebilltype, sourcebillno, companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, direction, payrealamt, writeoffamt, usetype, status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, tran_time, process_status,SOURCEHEADBILLNO,PARTITION_DATE )");//分区字段已处理
                    strBuff_hc.append(" select eid, '"+billno_hc+"', '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno_partRefund_hc+"', companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, 1, payrealamt, writeoffamt, '"+usetype_hc+"', status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"', 'N', '"+orderno_partRefund+"','"+bdate+"'");//分区字段已处理
                    
                    strBuff_hc.append(" from Dcp_Order_pay where eid='"+eId+"'  and SOURCEBILLNO='"+orderno_partRefund+"' ");
                    execsql = "";
                    execsql = strBuff_hc.toString();
                    ExecBean exSale_pay = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_pay));
                    try {
                        StaticInfo.dao.useTransactionProcessData(DataPB);
                        DataPB.clear();
                        HelpTools.writelog_waimai(logBeginStr+"【红冲部分退订单】处理成功！红冲单号orderNo:"+orderno_partRefund_hc);
                        HelpTools.writelog_fileName(logBeginStr+"【红冲部分退订单】处理成功！红冲单号orderNo:"+orderno_partRefund_hc,"partRefund");
                        
                        //写下历程
                        orderStatusLog onelv1 = new orderStatusLog();
                        onelv1.setLoadDocType(loadDocType);
                        onelv1.setChannelId(dcpOrder.getChannelId());
                        onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                        onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                        onelv1.seteId(eId);
                        onelv1.setOpName("");
                        onelv1.setOpNo("");
                        onelv1.setShopNo(dcpOrder.getShopNo());
                        onelv1.setOrderNo(orderno_partRefund_hc);
                        onelv1.setMachShopNo(dcpOrder.getMachShopNo());
                        onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
                        String statusType = "";
                        String updateStaus = "99";//其他
                        statusType = "99";// 订单状态
                        onelv1.setStatusType(statusType);
                        onelv1.setStatus(updateStaus);
                        StringBuilder statusTypeNameObj = new StringBuilder();
                        String statusName = "其他";
                        String statusTypeName = "其他状态";
                        onelv1.setStatusTypeName(statusTypeName);
                        onelv1.setStatusName(statusName);
                        
                        String memo = "红冲部分退订单(先部分退，又整单退)<br>原部分退订单号:"+orderno_partRefund;
                        
                        onelv1.setMemo(memo);
                        onelv1.setDisplay("0");
                        
                        String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        onelv1.setUpdate_time(updateDatetime);
                        
                        orderStatusLogList.add(onelv1);
                    }
                    catch (Exception e)
                    {
                        HelpTools.writelog_waimai(logBeginStr+"【红冲部分退订单】处理异常！红冲单号orderNo:"+orderno_partRefund_hc+",异常："+e.getMessage());
                        HelpTools.writelog_fileName(logBeginStr+"【红冲部分退订单】处理异常！红冲单号orderNo:"+orderno_partRefund_hc+",异常："+e.getMessage(),"partRefund");
                        DataPB.clear();
                    }
                    
                }
                DataPB.clear();//前面的先执行，不需要一个事务
                
                //生成单头语句
                StringBuffer strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_ORDER (");//分区字段
                strBuff.append(" eid, billtype, orderno, requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                        + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, refundamt,REFUNDAMT_MERRECEIVE,REFUNDAMT_CUSTPAYREAL"
                        + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                        + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, refundsourcebillno, refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                        + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                        + ", stime, create_datetime, complete_datetime, bdate, tran_time, update_time, process_status, peopletype, printcount, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DOWNGRADED,WAIMAIMERRECEIVEMODE,PARTITION_DATE )");//分区字段已处理
                
                strBuff.append("select eid, '"+billtype+"', '"+orderno+"', requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                        + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, payamt,tot_amt_merreceive,tot_amt_custpayreal"
                        + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                        + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, '"+sourceOrderNo+"', refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                        + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, '"+status+"', '"+refundStatus+"', paystatus, productstatus"
                        + ", '"+tran_time+"', '"+tran_time+"', '', '"+bdate+"', '"+tran_time+"', '"+update_time+"', 'N', peopletype, 0, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,ISMERPAY,DOWNGRADED,WAIMAIMERRECEIVEMODE,'"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from DCP_ORDER where eid='"+eId+"'  and orderno='"+sourceOrderNo+"' ");
                String execsql = strBuff.toString();
                ExecBean exSale = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale));
                
                //生成商品单身
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_ORDER_DETAIL (");//分区字段
                strBuff.append(" eid, orderno, item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, oitem, oreitem, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                        + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, stime, tran_time, RUNPICKQTY, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理
                
                strBuff.append("select eid, '"+orderno+"', item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, item, item, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                        + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, '"+tran_time+"', '"+tran_time+"', qty, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,'"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from DCP_ORDER_DETAIL where qty>=0 and eid='"+eId+"' and orderno='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail));
                
                //生成商品单身折扣
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_ORDER_DETAIL_agio (");//分区字段
                strBuff.append(" eid, orderno, mitem, item, qty, amt, inputdisc, realdisc, disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, tran_time, DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE )");
                
                strBuff.append(" select eid, '"+orderno+"', mitem, item, qty, amt, inputdisc, realdisc, disc, dctype, dctypename, pmtno, giftctf, giftctfno, bsno, '"+tran_time+"', DISC_MERRECEIVE, DISC_CUSTPAYREAL,'"+bdate+"'");
                
                strBuff.append(" from DCP_ORDER_DETAIL_agio where eid='"+eId+"' and orderno='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_detail_agio = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_detail_agio));
                
                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into Dcp_Order_Pay_Detail (");//分区字段
                strBuff.append(" eid, billno, item, billdate, bdate, sourcebilltype, sourcebillno, loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime, tran_time,SOURCEHEADBILLNO "
                        + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYTYPE,PARTITION_DATE )");//分区字段已处理
                strBuff.append(" select eid, '"+billno+"', item, '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"','"+sourceOrderNo+"' "
                        + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYTYPE,'"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from Dcp_Order_Pay_Detail where eid='"+eId+"' and SOURCEBILLNO='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay_detail = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay_detail));
                
                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into Dcp_Order_pay (");//分区字段
                strBuff.append("eid, billno, billdate, bdate, sourcebilltype, sourcebillno, companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, direction, payrealamt, writeoffamt, usetype, status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, tran_time, process_status,SOURCEHEADBILLNO,PARTITION_DATE )");//分区字段已处理
                strBuff.append(" select eid, '"+billno+"', '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, -direction, payrealamt, writeoffamt, '"+usetype+"', status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"', 'N', '"+sourceOrderNo+"','"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from Dcp_Order_pay where eid='"+eId+"'  and SOURCEBILLNO='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay));
                
                //写下历程
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(dcpOrder.getChannelId());
                onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                onelv1.seteId(eId);
                onelv1.setOpName("");
                onelv1.setOpNo("");
                onelv1.setShopNo(dcpOrder.getShopNo());
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo(dcpOrder.getMachShopNo());
                onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
                String statusType = "";
                String updateStaus = "99";//其他
                statusType = "99";// 订单状态
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = "其他";
                String statusTypeName = "其他状态";
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                String memo = "生成退订单成功";
                
                onelv1.setMemo(memo);
                onelv1.setDisplay("0");
                
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
            }
            
            //更新单身已退数量
            String execsql = "update dcp_order_detail set rqty=qty,runpickqty=qty-pickqty where eid='"+eId+"' and orderno='"+orderNo+"' ";
            ExecBean exSale = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale));
            
            execsql = "";
            execsql = "update dcp_order set refundamt=tot_amt,REFUNDAMT_MERRECEIVE=tot_amt_merreceive,REFUNDAMT_CUSTPAYREAL=tot_amt_custpayreal where eid='"+eId+"' and orderno='"+orderNo+"' ";
            ExecBean exSale_refundAmt = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale_refundAmt));
            
            //防止之前订转销了更新原单状态，再次更新下
            execsql = "";
            execsql = "update dcp_order set status='"+status+"',refundstatus='"+refundStatus+"' where eid='"+eId+"' and orderno='"+orderNo+"' ";
            ExecBean exSale_originOrderStatus = new ExecBean(execsql);
            DataPB.add(new DataProcessBean(exSale_originOrderStatus));
            
            StaticInfo.dao.useTransactionProcessData(DataPB);
            
            HelpTools.writelog_waimai(logBeginStr+"处理成功！");
            
            
            
            // 写订单日志
            if(orderStatusLogList.size()>0)
            {
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai("【写表dcp_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                            "【写表dcp_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNo:" + orderNo);
                }
            }
            //endregion
            
            //region kds处理
            StringBuffer error_task = new StringBuffer("");
            updateProcessTask(eId,orderNo,"","",error_task);
            //endregion
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            HelpTools.writelog_waimai(logBeginStr+"处理异常:"+e.getMessage());
        }
        
    }
    
    /**
     * 订单接入时税额试算
     * @param dcpOrder
     * @param langType
     * @param errorMessage
     * @throws Exception
     */
    public static boolean OrderInvoiceCaculate(order dcpOrder, String langType, StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return false;
        }
        
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String billType = dcpOrder.getBillType();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【税额计算】单号orderNo="+orderNo+",";
        
        
        
        if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
        {
            //外卖的不处理，不需要判断是不是台湾环境了
            writelog_waimai(logBeginStr+"渠道类型loadDocType="+loadDocType+",无需调用");
            return true;
        }
        
        //查询下参数AreaType
        String AreaType = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "AreaType");
        if(AreaType==null||AreaType.equals("TW")==false)
        {
            writelog_waimai(logBeginStr+"参数AreaType="+AreaType+",无需调用");
            return true;
        }
        
        if(billType!=null&&billType.equals("-1"))
        {
            writelog_waimai(logBeginStr+"单据类型billType="+billType+"(退单新建),无需调用");
            return true;
        }
        
        //查询下DCP自己的接口账号
        String sql_apiUser = "select * from crm_apiuser where apptype='OWNCHANNEL'  and eid='"+eId+"' ";
        writelog_waimai(logBeginStr+"调用POS服务，接口账号查询sql="+sql_apiUser);
        List<Map<String, Object>> apiUserList = StaticInfo.dao.executeQuerySQL(sql_apiUser, null);
        if(apiUserList==null||apiUserList.isEmpty())
        {
            writelog_waimai(logBeginStr+"接口账号未设置,无法调用POS服务");
            errorMessage.append(logBeginStr+"接口账号未设置,无法调用POS服务");
            return false;
        }
        String apiUserCode = apiUserList.get(0).get("USERCODE").toString();
        String apiUserKey = apiUserList.get(0).get("USERKEY").toString();//签名密钥
        
        String posUrl = PosPub.getPOS_INNER_URL(eId);//参数PosUrl地址
        if(posUrl==null||posUrl.trim().isEmpty())
        {
            writelog_waimai(logBeginStr+"参数PosUrl(POS服务的接口地址)未设置,无法调用POS服务");
            errorMessage.append(logBeginStr+"参数PosUrl(POS服务的接口地址)未设置,无法调用POS服务");
            return false;
        }
        
        boolean checkPara = false;
        String errorStr = "";
        if(eId==null||eId.isEmpty())
        {
            errorStr = "订单eId=为空,"+errorStr;
            checkPara = true;
            
        }
        if(orderNo==null||orderNo.isEmpty())
        {
            errorStr = "订单orderNo=为空,"+errorStr;
            checkPara = true;
            
        }
        if(loadDocType==null||loadDocType.isEmpty())
        {
            errorStr = "订单loadDocType=为空,"+errorStr;
            checkPara = true;
            
        }
        if(channelId==null||channelId.isEmpty())
        {
            errorStr = "订单channelId=为空,"+errorStr;
            checkPara = true;
            
        }
        if(status==null||status.isEmpty())
        {
            errorStr = "订单status=为空,"+errorStr;
            checkPara = true;
        }
        
        if(checkPara)
        {
            writelog_waimai(logBeginStr+errorStr);
            errorMessage.append(logBeginStr+errorStr);
            return false;
        }
        
        //组装税额计算的请求requset
        InvoiceCaculateRequest caculateReq = new InvoiceCaculateRequest();
        
        caculateReq.setInvoiceList(new ArrayList<InvoiceCaculateRequest.InvoiceList>());
        caculateReq.setGoodsList(new ArrayList<InvoiceCaculateRequest.GoodsList>());
        caculateReq.setPayList(new ArrayList<InvoiceCaculateRequest.PayList>());
        String saleType = "Order";//Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值；
        String saleNo =orderNo;
        String shopId = "";//pos的给下单门店，其他暂时不用给值
        if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
        {
            shopId = dcpOrder.getShopNo();
        }
        String oprType = "1";//0-仅进行发票试算 1-保存发票试算数据【订单创建接口使用】，默认0
        String invSplitType = "1";//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分，不传时默认1不拆分
        
        caculateReq.setSaleType(saleType);
        caculateReq.setSaleNo(saleNo);
        caculateReq.setShopId(shopId);
        caculateReq.setOprType(oprType);
        caculateReq.setInvSplitType(invSplitType);
        caculateReq.setFreeCode("");
        caculateReq.setPassport("");
        
        
        /*********************组装商品GoodsList***************************************/
        //零售单/订单：不包含套餐子商品;按金额拆分的时候可以不传
        int invItem = 1;//默认1 发票项次，从1开始
        String pluNoSqlCondition = "";	//用查询商品的税别编码和税率
        for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
        {
            
            if(goodsItem.getPackageType()!=null&&goodsItem.getPackageType().equals("3"))//不包含套餐子商品;
            {
                //1、正常商品 2、套餐主商品  3、套餐子商品
                continue;
            }
            InvoiceCaculateRequest.GoodsList goodsInvoice = new InvoiceCaculateRequest().new GoodsList();
            goodsInvoice.setItem(goodsItem.getItem());
            goodsInvoice.setInvItem(invItem+"");
            goodsInvoice.setMItem(goodsItem.getToppingMitem());
            goodsInvoice.setPluNo(goodsItem.getPluNo());
            
            goodsInvoice.setPluName(goodsItem.getPluName());
            goodsInvoice.setBarcode(goodsItem.getPluBarcode());
            goodsInvoice.setTaxCode("");//后面赋值
            goodsInvoice.setTaxRate(0);//后面赋值
            goodsInvoice.setQty(goodsItem.getQty());
            goodsInvoice.setAmt(goodsItem.getAmt());
            
            
            String sql_tax_item = " select * from ("
                    + " select PLUNO,a.TAXCODE,NVL(b.TAXRATE,0) TAXRATE,b.TAXTYPE from dcp_goods a "
                    + " left join dcp_taxcategory b on a.eid=b.eid and a.taxcode = b.taxcode "
                    + " where a.eid='"+eId+"' and a.pluno='"+goodsItem.getPluNo()+"' "
                    + ")";
            //查询下pluNo对应的税别编码和税率
            writelog_waimai(logBeginStr+"循环查询商品对应taxCode、taxType、taxRate的sql="+sql_tax_item);
            List<Map<String, Object>> getPluNoTaxList_item = StaticInfo.dao.executeQuerySQL(sql_tax_item, null);
            //给税别编码和税率赋值
            if(getPluNoTaxList_item!=null&&getPluNoTaxList_item.isEmpty()==false)
            {
                Map<String, Object> map = getPluNoTaxList_item.get(0);
                String pluNoTax = map.get("PLUNO").toString();
                String taxCode = map.get("TAXCODE").toString();
                String taxType = map.get("TAXTYPE").toString();
                String taxRate_str = map.get("TAXRATE").toString();
                
                goodsInvoice.setTaxCode(taxCode);
                //goodsInvoice.setTaxType(taxType);
                try
                {
                    goodsInvoice.setTaxRate(Double.parseDouble(taxRate_str));
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
                //订单上也赋值下
                goodsItem.setTaxCode(taxCode);
                goodsItem.setTaxType(taxType);
                
            }
            
            pluNoSqlCondition = "'"+goodsItem.getPluNo()+"'"+","+pluNoSqlCondition;
            caculateReq.getGoodsList().add(goodsInvoice);
            
        }

		/*
		pluNoSqlCondition = pluNoSqlCondition.substring(0,pluNoSqlCondition.length()-1);
		String sql_tax = " select * from ("
				+ " select PLUNO,a.TAXCODE,NVL(b.TAXRATE,0) TAXRATE from dcp_goods a "
				+ " left join dcp_taxcategory b on a.eid=b.eid and a.taxcode = b.taxcode "
				+ " where a.eid='"+eId+"' and a.pluno in ("+pluNoSqlCondition+") "
				+ ")";

		//查询下pluNo对应的税别编码和税率
		writelog_waimai(logBeginStr+"查询商品对应taxCode和taxRate的sql="+sql_tax);
		List<Map<String, Object>> getPluNoTaxList = StaticInfo.dao.executeQuerySQL(sql_tax, null);
		//给税别编码和税率赋值
		if(getPluNoTaxList!=null&&getPluNoTaxList.isEmpty()==false)
		{
			for (InvoiceCaculateRequest.GoodsList par : caculateReq.getGoodsList())
			{
				String pluNo = par.getPluNo();
				for (Map<String, Object> map : getPluNoTaxList)
				{
					String pluNoTax = map.get("PLUNO").toString();
					String taxCode = map.get("TAXCODE").toString();
					String taxRate_str = map.get("TAXRATE").toString();
					if(pluNo.equals(pluNoTax))
					{
						par.setTaxCode(taxCode);
						try
						{
							par.setTaxRate(Double.parseDouble(taxRate_str));
						} catch (Exception e)
						{
							// TODO: handle exception
						}
						break;
					}
				}
			}
		}
		*/
        
        
        /*********************组装付款PayList***************************************/
        String payCanInvoiceSqlCondition = "";	//用查询付款方式对应的开票方式
        for (orderPay payMent : dcpOrder.getPay())
        {
            InvoiceCaculateRequest.PayList payInvoice = new InvoiceCaculateRequest().new PayList();
            payInvoice.setCanInvoice("0");//后面赋值
            try
            {
                payInvoice.setChange(Double.parseDouble(payMent.getChanged()));
            }
            catch (Exception e)
            {
                
                payInvoice.setChange(0);
            }
            try
            {
                payInvoice.setExtra(Double.parseDouble(payMent.getExtra()));
            }
            catch (Exception e)
            {
                
                payInvoice.setExtra(0);
            }
            payInvoice.setIsOrderPay(payMent.getIsOrderPay());
            try
            {
                payInvoice.setPayAmt(Double.parseDouble(payMent.getPay()));
            }
            catch (Exception e)
            {
                
                payInvoice.setPayAmt(0);
            }
            payInvoice.setPayCode(payMent.getPayCode());
            payInvoice.setPayName(payMent.getPayName());
            payInvoice.setPayType(payMent.getPayType());
            
            String sql_canInvoice_item = " select PAYTYPE, CANOPENINVOICE from  DCP_PAYTYPE where eid='"+eId+"' and paytype='"+payMent.getPayType()+"' ";
            //查询付款方式对应开票方式
            writelog_waimai(logBeginStr+"查询付款方式对应开票方式canInvoice的sql="+sql_canInvoice_item);
            List<Map<String, Object>> getPayCanInvoiceList = StaticInfo.dao.executeQuerySQL(sql_canInvoice_item, null);
            
            if(getPayCanInvoiceList!=null&&getPayCanInvoiceList.isEmpty()==false)
            {
                Map<String, Object> map = getPayCanInvoiceList.get(0);
                payInvoice.setCanInvoice(map.get("CANOPENINVOICE").toString());
                payMent.setCanInvoice(map.get("CANOPENINVOICE").toString());
            }
            
            
            caculateReq.getPayList().add(payInvoice);
            
            payCanInvoiceSqlCondition = "'"+payMent.getPayType()+"'"+","+payCanInvoiceSqlCondition;
        }

		/*
		payCanInvoiceSqlCondition = payCanInvoiceSqlCondition.substring(0,payCanInvoiceSqlCondition.length()-1);
		String sql_canInvoice = " select PAYTYPE, CANOPENINVOICE from  DCP_PAYTYPE where eid='"+eId+"' and paytype in ("+payCanInvoiceSqlCondition+")";
		//查询付款方式对应开票方式
		writelog_waimai(logBeginStr+"查询付款方式对应开票方式canInvoice的sql="+sql_canInvoice);
		List<Map<String, Object>> getPayCanInvoiceList = StaticInfo.dao.executeQuerySQL(sql_canInvoice, null);

		if(getPayCanInvoiceList!=null&&getPayCanInvoiceList.isEmpty()==false)
		{
			for (InvoiceCaculateRequest.PayList par : caculateReq.getPayList())
			{
				String payType = par.getPayType();
				for (Map<String, Object> map : getPayCanInvoiceList)
				{
					String payType_DB = map.get("PAYTYPE").toString();

					if(payType.equals(payType_DB))
					{
						par.setCanInvoice(map.get("CANOPENINVOICE").toString());
						break;
					}
				}
			}
		}
		*/
        
        
        //组装pos服务header
        if(langType==null||langType.trim().isEmpty())
        {
            langType = "zh_TW";//默认繁体吧，如果没传，因为台湾环境才有发票试算
        }
        
        
        Map<String, Object> mapHeader = new HashMap<>();
        String serviceId = "POS_InvoiceCaculate_Open";
        String requestId = UUID.randomUUID().toString();
        
        mapHeader.put("serviceId", serviceId);
        mapHeader.put("requestId", requestId);
        mapHeader.put("langType", langType);
        mapHeader.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        mapHeader.put("version", "v3.0");
        mapHeader.put("apiUserCode", apiUserCode);
        ParseJson pj = new ParseJson();
        
        String request = pj.beanToJson(caculateReq);
        
        mapHeader.put("sign", PosPub.encodeMD5(request + apiUserKey));
        writelog_waimai(logBeginStr+"调用接口url:"+posUrl+",调用接口serviceId:"+serviceId+"\r\n请求header:"+mapHeader.toString()+"\r\n请求Request:"+request);
        String res = HttpSend.doPost(posUrl, request, mapHeader,requestId);
        writelog_waimai(logBeginStr+"返回res:"+res);
        boolean nRest = false;
        try
        {
            if (res != null)
            {
                JSONObject resJson = new JSONObject(res);
                boolean success = resJson.getBoolean("success");
                
                if(success)
                {
                    nRest = true;
                    writelog_waimai(logBeginStr+"税额计算成功");
                }
                else
                {
                    errorMessage.append(resJson.get("serviceDescription").toString());
                    
                }
            }
            else
            {
                errorMessage.append("调用接口"+serviceId+"返回为空!");
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            errorMessage.append(e.getMessage());
        }
        
        if(nRest==false)
        {
            dcpOrder.setExceptionStatus("Y");
            String ABNORMALTYPE = "invoiceTrial";//异常类型：invoiceTrial（税额计算）
            String ABNORMALTYPENAME = "发票试算";//异常类型：invoiceTrial（税额计算）
            //写下异常
            Map<String, Object> map_DCP_ORDER_ABNORMALINFO = new HashMap<String, Object>();
            mapHeader.put("EID", eId);
            mapHeader.put("ORDERNO", orderNo);
            mapHeader.put("ABNORMALTYPE", ABNORMALTYPE);
            mapHeader.put("ABNORMALTYPENAME", ABNORMALTYPENAME);
            
            mapHeader.put("MEMO", errorMessage.toString());
            mapHeader.put("STATUS", "100");
            mapHeader.put("LASTMODIOPID", "");
            mapHeader.put("LASTMODIOPNAME", "");
            
            StringBuffer error = new StringBuffer();
            
            insert_DCP_ORDER_ABNORMALINFO(map_DCP_ORDER_ABNORMALINFO, error);
            
            
        }
        return nRest;
        
    }
    
    /**
     * 台湾订单创建时发票开立
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static boolean OrderInvoiceCreate(order dcpOrder, String langType, JSONObject invoiceJson, StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return false;
        }
        
        orderInvoice dcpOrderInvoice = dcpOrder.getInvoiceDetail();
        if(dcpOrderInvoice==null||dcpOrderInvoice.getIsInvoice()==null||dcpOrderInvoice.getIsInvoice().equals("Y")==false)
        {
            errorMessage.append("无需开票！");
            return true;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String billType = dcpOrder.getBillType();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【发票开立】单号orderNo="+orderNo+",";
        
        
        
        if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.PADGUIDE)||loadDocType.equals(orderLoadDocType.WAIMAI))
        {
            //调用开发票：POS_InvoiceCreate_Open（仅针对渠道类型为：POS/POSANDROID/PADGUIDE/WAIMAI）
        }
        else
        {
            writelog_waimai(logBeginStr+"渠道类型loadDocType="+loadDocType+",无需调用");
            return true;
        }
        
        //查询下参数AreaType
        String AreaType = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "AreaType");
        if(AreaType==null||AreaType.equals("TW")==false)
        {
            writelog_waimai(logBeginStr+"参数AreaType="+AreaType+",无需调用");
            return true;
        }
        
        if(billType!=null&&billType.equals("-1"))
        {
            writelog_waimai(logBeginStr+"单据类型billType="+billType+"(退单新建),无需调用");
            return true;
        }
        
        //查询下DCP自己的接口账号
        String sql_apiUser = "select * from crm_apiuser where apptype='OWNCHANNEL'  and eid='"+eId+"' ";
        writelog_waimai(logBeginStr+"调用POS服务，接口账号查询sql="+sql_apiUser);
        List<Map<String, Object>> apiUserList = StaticInfo.dao.executeQuerySQL(sql_apiUser, null);
        if(apiUserList==null||apiUserList.isEmpty())
        {
            writelog_waimai(logBeginStr+"接口账号未设置,无法调用POS服务");
            return false;
        }
        String apiUserCode = apiUserList.get(0).get("USERCODE").toString();
        String apiUserKey = apiUserList.get(0).get("USERKEY").toString();//签名密钥
        
        String posUrl = PosPub.getPOS_INNER_URL(eId);//参数PosUrl地址
        if(posUrl==null||posUrl.trim().isEmpty())
        {
            writelog_waimai(logBeginStr+"参数PosUrl(POS服务的接口地址)未设置,无法调用POS服务");
            return false;
        }
        
        boolean checkPara = false;
        String errorStr = "";
        if(eId==null||eId.isEmpty())
        {
            errorStr = "订单eId=为空,"+errorStr;
            checkPara = true;
            
        }
        if(orderNo==null||orderNo.isEmpty())
        {
            errorStr = "订单orderNo=为空,"+errorStr;
            checkPara = true;
            
        }
        if(loadDocType==null||loadDocType.isEmpty())
        {
            errorStr = "订单loadDocType=为空,"+errorStr;
            checkPara = true;
            
        }
        if(channelId==null||channelId.isEmpty())
        {
            errorStr = "订单channelId=为空,"+errorStr;
            checkPara = true;
            
        }
        if(status==null||status.isEmpty())
        {
            errorStr = "订单status=为空,"+errorStr;
            checkPara = true;
        }
        
        if(checkPara)
        {
            writelog_waimai(logBeginStr+errorStr);
            errorMessage.append(logBeginStr+errorStr);
            return false;
        }
        
        //组装税额计算的请求requset
        InvoiceCreateRequest createReq = new InvoiceCreateRequest();
        
        createReq.setInvoiceList(new ArrayList<InvoiceCreateRequest.InvoiceList>());
        createReq.setGoodsList(new ArrayList<InvoiceCreateRequest.GoodsList>());
        createReq.setPayList(new ArrayList<InvoiceCreateRequest.PayList>());
        String saleType = "Order";//Sale-销售单 Order-订单 Card-售卡 Coupon-售券 Recharge-充值；
        String saleNo =orderNo;
        String orgId = dcpOrder.getShippingShopNo();//
        String machineId = dcpOrder.getMachineNo()==null?"":dcpOrder.getMachineNo();
        if(loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))
        {
            //机台号(微商城传"OnLine")
            machineId = "OnLine";
        }
        
        String invCount = "1";//发票张数，不传时默认1；
        String invSplitType = "1";//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分，不传时默认1不拆分
        String recipient = "3";//1.云POS 2.全渠道会员 3.云中台 4.外卖点餐
        
        createReq.setSaleType(saleType);
        createReq.setSaleNo(saleNo);
        createReq.setOrgId(orgId);
        createReq.setInvCount(invCount);
        createReq.setInvSplitType(invSplitType);
        createReq.setFreeCode("");
        createReq.setPassport("");
        
        int invItem = 1;//默认1 发票项次，从1开始
        
        /*********************组装开票信息invoiceList***************************************/
        InvoiceCreateRequest.InvoiceList openInvoiceInfo = new InvoiceCreateRequest().new InvoiceList();
        openInvoiceInfo.setInvItem(invItem+"");
        openInvoiceInfo.setBDate(dcpOrder.getbDate());
        openInvoiceInfo.setInvType(dcpOrderInvoice.getInvoiceType());//发票类型：0园区收据，2二联，3三联，4收据，5二联式收银机发票，6三联式收银机发票，X不申报，7电子发票
        openInvoiceInfo.setInvMemo(dcpOrderInvoice.getInvMemo());
        openInvoiceInfo.setSellerGuiNo("");//卖家统编 订单新建上没有这个
        openInvoiceInfo.setBuyerGuiNo(dcpOrderInvoice.getBuyerGuiNo());//买家统一编号
        openInvoiceInfo.setCarrierCode(dcpOrderInvoice.getCarrierCode());//载具类别编码
        openInvoiceInfo.setCarrierShowId(dcpOrderInvoice.getCarrierShowId());//载具显码
        openInvoiceInfo.setCarrierHiddenId(dcpOrderInvoice.getCarrierHiddenId());//载具隐码
        openInvoiceInfo.setLoveCode(dcpOrderInvoice.getLoveCode());//爱心码
        
        createReq.getInvoiceList().add(openInvoiceInfo);
        
        
        /*********************组装商品GoodsList***************************************/
        //零售单/订单：不包含套餐子商品;按金额拆分的时候可以不传
        
        String pluNoSqlCondition = "";	//用查询商品的税别编码和税率
        for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
        {
            
            if(goodsItem.getPackageType()!=null&&goodsItem.getPackageType().equals("3"))//不包含套餐子商品;
            {
                //1、正常商品 2、套餐主商品  3、套餐子商品
                continue;
            }
            InvoiceCreateRequest.GoodsList goodsInvoice = new InvoiceCreateRequest().new GoodsList();
            goodsInvoice.setInvItem(invItem+"");
            goodsInvoice.setOItem(goodsItem.getItem());
            
            goodsInvoice.setPluNo(goodsItem.getPluNo());
            goodsInvoice.setPluName(goodsItem.getPluName());
            goodsInvoice.setInclTax(goodsItem.getInclTax());//不知道咋给值
            goodsInvoice.setTaxCode("");//后面赋值
            goodsInvoice.setTaxRate("0");//后面赋值
            goodsInvoice.setTaxType("");//后面赋值
            goodsInvoice.setQty(goodsItem.getQty());
            goodsInvoice.setAmt(goodsItem.getAmt());
            
            String sql_tax_item = " select * from ("
                    + " select PLUNO,a.TAXCODE,NVL(b.TAXRATE,0) TAXRATE,b.TAXTYPE from dcp_goods a "
                    + " left join dcp_taxcategory b on a.eid=b.eid and a.taxcode = b.taxcode "
                    + " where a.eid='"+eId+"' and a.pluno='"+goodsItem.getPluNo()+"' "
                    + ")";
            //查询下pluNo对应的税别编码和税率
            writelog_waimai(logBeginStr+"循环查询商品对应taxCode、taxType、taxRate的sql="+sql_tax_item);
            List<Map<String, Object>> getPluNoTaxList_item = StaticInfo.dao.executeQuerySQL(sql_tax_item, null);
            //给税别编码和税率赋值
            if(getPluNoTaxList_item!=null&&getPluNoTaxList_item.isEmpty()==false)
            {
                Map<String, Object> map = getPluNoTaxList_item.get(0);
                String pluNoTax = map.get("PLUNO").toString();
                String taxCode = map.get("TAXCODE").toString();
                String taxType = map.get("TAXTYPE").toString();
                String taxRate_str = map.get("TAXRATE").toString();
                
                goodsInvoice.setTaxCode(taxCode);
                goodsInvoice.setTaxType(taxType);
                try
                {
                    goodsInvoice.setTaxRate(Double.parseDouble(taxRate_str)+"");
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
                //订单上也赋值下
                goodsItem.setTaxCode(taxCode);
                goodsItem.setTaxType(taxType);
                
            }
            pluNoSqlCondition = "'"+goodsItem.getPluNo()+"'"+","+pluNoSqlCondition;
            createReq.getGoodsList().add(goodsInvoice);
            
        }

		/*
		pluNoSqlCondition = pluNoSqlCondition.substring(0,pluNoSqlCondition.length()-1);
		String sql_tax = " select * from ("
				+ " select PLUNO,a.TAXCODE,NVL(b.TAXRATE,0) TAXRATE,b.TAXTYPE from dcp_goods a "
				+ " left join dcp_taxcategory b on a.eid=b.eid and a.taxcode = b.taxcode "
				+ " where a.eid='"+eId+"' and a.pluno in ("+pluNoSqlCondition+") "
				+ ")";

		//查询下pluNo对应的税别编码和税率
		writelog_waimai(logBeginStr+"查询商品对应taxCode、taxType、taxRate的sql="+sql_tax);
		List<Map<String, Object>> getPluNoTaxList = StaticInfo.dao.executeQuerySQL(sql_tax, null);
		//给税别编码和税率赋值
		if(getPluNoTaxList!=null&&getPluNoTaxList.isEmpty()==false)
		{
			for (InvoiceCreateRequest.GoodsList par : createReq.getGoodsList())
			{
				String pluNo = par.getPluNo();
				for (Map<String, Object> map : getPluNoTaxList)
				{
					String pluNoTax = map.get("PLUNO").toString();
					String taxCode = map.get("TAXCODE").toString();
					String taxType = map.get("TAXTYPE").toString();
					String taxRate_str = map.get("TAXRATE").toString();
					if(pluNo.equals(pluNoTax))
					{
						par.setTaxCode(taxCode);
						par.setTaxType(taxType);
						try
						{
							par.setTaxRate(Double.parseDouble(taxRate_str)+"");
						} catch (Exception e)
						{
							// TODO: handle exception
						}
						break;
					}
				}
			}
		}
		*/
        
        
        /*********************组装付款PayList***************************************/
        String payCanInvoiceSqlCondition = "";	//用查询付款方式对应的开票方式
        for (orderPay payMent : dcpOrder.getPay())
        {
            InvoiceCreateRequest.PayList payInvoice = new InvoiceCreateRequest().new PayList();
            payInvoice.setPayType(payMent.getPayType());//表的主键是这个，需要加上关联，不能通过payCode去搞
            payInvoice.setPayCode(payMent.getPayCode());
            payInvoice.setPayName(payMent.getPayName());
            payInvoice.setPayCodeErp(payMent.getPayCodeErp());
            try
            {
                payInvoice.setSendPayAmt(Double.parseDouble(payMent.getCardSendPay()));
                
            } catch (Exception e)
            {
                payInvoice.setSendPayAmt(0);
            }
            
            try
            {
                payInvoice.setPayAmt(Double.parseDouble(payMent.getPay()));
            }
            catch (Exception e)
            {
                
                payInvoice.setPayAmt(0);
            }
            
            payInvoice.setIsOrderPay(payMent.getIsOrderPay());
            payInvoice.setIsTurnover("Y");//是否纳入营业额Y/N ,红艳也不知道，继续去追问吧
            payInvoice.setCanOpenInvoice("0");//后面赋值  开票方式：0不可开票 1可开票 2已开票 3第三方已开票
            payInvoice.setCtType(payMent.getCtType());//卡券类型：1卡2券
            payInvoice.setCtId("");//卡券类型id  ,红艳也不知道，继续去追问吧
            payInvoice.setTaxCode("");//税别编码--取卡类型和券类型上的税别编码
            payInvoice.setTaxType("");//税别类型：1应税内含，2零税率，3免税
            payInvoice.setTaxRate("");//税率
            
            try
            {
                payInvoice.setChange(Double.parseDouble(payMent.getChanged()));
            }
            catch (Exception e)
            {
                
                payInvoice.setChange(0);
            }
            try
            {
                payInvoice.setExtra(Double.parseDouble(payMent.getExtra()));
            }
            catch (Exception e)
            {
                
                payInvoice.setExtra(0);
            }
            
            String sql_canInvoice_item = " select PAYTYPE, CANOPENINVOICE from  DCP_PAYTYPE where eid='"+eId+"' and paytype='"+payMent.getPayType()+"' ";
            //查询付款方式对应开票方式
            writelog_waimai(logBeginStr+"循环查询付款方式对应开票方式canInvoice的sql="+sql_canInvoice_item);
            List<Map<String, Object>> getPayCanInvoiceList = StaticInfo.dao.executeQuerySQL(sql_canInvoice_item, null);
            
            if(getPayCanInvoiceList!=null&&getPayCanInvoiceList.isEmpty()==false)
            {
                Map<String, Object> map = getPayCanInvoiceList.get(0);
                payInvoice.setCanOpenInvoice(map.get("CANOPENINVOICE").toString());
                payMent.setCanInvoice(map.get("CANOPENINVOICE").toString());
            }
            
            createReq.getPayList().add(payInvoice);
            
            payCanInvoiceSqlCondition = "'"+payMent.getPayType()+"'"+","+payCanInvoiceSqlCondition;
        }

		/*
		payCanInvoiceSqlCondition = payCanInvoiceSqlCondition.substring(0,payCanInvoiceSqlCondition.length()-1);
		String sql_canInvoice = " select PAYTYPE, CANOPENINVOICE from  DCP_PAYTYPE where eid='"+eId+"' and paytype in ("+payCanInvoiceSqlCondition+")";
		//查询付款方式对应开票方式
		writelog_waimai(logBeginStr+"查询付款方式对应开票方式canInvoice的sql="+sql_canInvoice);
		List<Map<String, Object>> getPayCanInvoiceList = StaticInfo.dao.executeQuerySQL(sql_canInvoice, null);

		if(getPayCanInvoiceList!=null&&getPayCanInvoiceList.isEmpty()==false)
		{
			for (InvoiceCreateRequest.PayList par : createReq.getPayList())
			{
				String payType = par.getPayType();
				for (Map<String, Object> map : getPayCanInvoiceList)
				{
					String payType_DB = map.get("PAYTYPE").toString();

					if(payType.equals(payType_DB))
					{
						par.setCanOpenInvoice(map.get("CANOPENINVOICE").toString());
						break;
					}
				}
			}
		}
		*/
        
        
        //组装pos服务header
        if(langType==null||langType.trim().isEmpty())
        {
            langType = "zh_TW";//默认繁体吧，如果没传，因为台湾环境才有发票试算
        }
        boolean nRest = false;
        
        Map<String, Object> mapHeader = new HashMap<>();
        String serviceId = "POS_InvoiceCreate_Open";
        String requestId = UUID.randomUUID().toString();
        
        mapHeader.put("serviceId", serviceId);
        mapHeader.put("requestId", requestId);
        mapHeader.put("langType", langType);
        mapHeader.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        mapHeader.put("version", "v3.0");
        mapHeader.put("apiUserCode", apiUserCode);
        ParseJson pj = new ParseJson();
        
        String request = pj.beanToJson(createReq);
        try
        {
            
            mapHeader.put("sign", PosPub.encodeMD5(request + apiUserKey));
            writelog_waimai(logBeginStr + "调用接口url:" + posUrl + ",调用接口serviceId:" + serviceId + "\r\n请求header:"
                    + mapHeader.toString() + "\r\n请求Request:" + request);
            String res = HttpSend.doPost(posUrl, request, mapHeader,requestId);
            writelog_waimai(logBeginStr + "返回res:" + res);
            
            if (res != null)
            {
                JSONObject resJson = new JSONObject(res);
                boolean success = resJson.getBoolean("success");
                
                
                if (success)
                {
                    JSONObject datasObj = resJson.getJSONObject("datas");
                    JSONArray invoiceListJsonArray = datasObj.getJSONArray("invoiceList");
                    invoiceJson = invoiceListJsonArray.getJSONObject(0);
                    
                    nRest = true;
                    writelog_waimai(logBeginStr + "发票开立成功");
                }
                else
                {
                    errorMessage.append(resJson.get("serviceDescription").toString());
                }
            }
            else
            {
                errorMessage.append("调用接口" + serviceId + "返回为空!");
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            errorMessage.append(e.getMessage());
        }
        
        if(nRest==false)
        {
            dcpOrder.setExceptionStatus("Y");
            String ABNORMALTYPE = "invoiceOpen";//异常类型：invoiceOpen（发票开立）
            String ABNORMALTYPENAME = "发票开立";//异常类型：invoiceOpen（发票开立）
            //写下异常
            Map<String, Object> map_DCP_ORDER_ABNORMALINFO = new HashMap<String, Object>();
            mapHeader.put("EID", eId);
            mapHeader.put("ORDERNO", orderNo);
            mapHeader.put("ABNORMALTYPE", ABNORMALTYPE);
            mapHeader.put("ABNORMALTYPENAME", ABNORMALTYPENAME);
            
            mapHeader.put("MEMO", errorMessage.toString());
            mapHeader.put("STATUS", "100");
            mapHeader.put("LASTMODIOPID", "");
            mapHeader.put("LASTMODIOPNAME", "");
            
            StringBuffer error = new StringBuffer();
            
            insert_DCP_ORDER_ABNORMALINFO(map_DCP_ORDER_ABNORMALINFO, error);
            
            
        }
        return nRest;
        
        
    }
    
    /**
     * 发票作废或折让
     * @param eId 企业ID
     * @param originOrderNo 原订单号
     * @param refundOrderNo 退订单号
     * @param reasonCode 理由码
     * @param reason 理由码名称
     * @param InvOperateType 发票操作: 1作废，2折让单
     * @param opNo 操作人
     * @param outMap 输出的折让单单号节点rebateNo
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static boolean OrderInvoiceRefund(String eId,String originOrderNo,String refundOrderNo, String reasonCode,String reason,String InvOperateType,String opNo,Map<String, Object> outMap, StringBuffer errorMessage) throws Exception
    {
        boolean nRest = false;
        try
        {
            if (errorMessage == null)
            {
                errorMessage = new StringBuffer();
            }
            
            String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String logBeginStr = "【发票作废和折让】原订单号orderNo="+originOrderNo+",";
            
            
            
            //查询下参数AreaType
            String AreaType = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "AreaType");
            if(AreaType==null||AreaType.equals("TW")==false)
            {
                writelog_waimai(logBeginStr+"参数AreaType="+AreaType+",无需调用");
                return true;
            }
            
            
            
            //查询下DCP自己的接口账号
            String sql_apiUser = "select * from crm_apiuser where apptype='OWNCHANNEL'  and eid='"+eId+"' ";
            writelog_waimai(logBeginStr+"调用POS服务，接口账号查询sql="+sql_apiUser);
            List<Map<String, Object>> apiUserList = StaticInfo.dao.executeQuerySQL(sql_apiUser, null);
            if(apiUserList==null||apiUserList.isEmpty())
            {
                writelog_waimai(logBeginStr+"接口账号未设置,无法调用POS服务");
                
                String ABNORMALTYPE = "invoiceRefund";// 异常类型：invoiceRefund（发票折让或作废）
                String ABNORMALTYPENAME = "发票折让或作废";// 异常类型：invoiceRefund（发票折让或作废）
                // 写下异常
                Map<String, Object> map_DCP_ORDER_ABNORMALINFO = new HashMap<String, Object>();
                map_DCP_ORDER_ABNORMALINFO.put("EID", eId);
                map_DCP_ORDER_ABNORMALINFO.put("ORDERNO", originOrderNo);
                map_DCP_ORDER_ABNORMALINFO.put("ABNORMALTYPE", ABNORMALTYPE);
                map_DCP_ORDER_ABNORMALINFO.put("ABNORMALTYPENAME", ABNORMALTYPENAME);
                
                map_DCP_ORDER_ABNORMALINFO.put("MEMO", "接口账号未设置,无法调用POS服务");
                map_DCP_ORDER_ABNORMALINFO.put("STATUS", "100");
                map_DCP_ORDER_ABNORMALINFO.put("LASTMODIOPID", "");
                map_DCP_ORDER_ABNORMALINFO.put("LASTMODIOPNAME", "");
                
                StringBuffer error = new StringBuffer();
                
                insert_DCP_ORDER_ABNORMALINFO(map_DCP_ORDER_ABNORMALINFO, error);
                
                
                return false;
            }
            String apiUserCode = apiUserList.get(0).get("USERCODE").toString();
            String apiUserKey = apiUserList.get(0).get("USERKEY").toString();//签名密钥
            
            String posUrl = PosPub.getPOS_INNER_URL(eId);//参数PosUrl地址
            if(posUrl==null||posUrl.trim().isEmpty())
            {
                writelog_waimai(logBeginStr+"参数PosUrl(POS服务的接口地址)未设置,无法调用POS服务");
                String ABNORMALTYPE = "invoiceRefund";// 异常类型：invoiceRefund（发票折让或作废）
                String ABNORMALTYPENAME = "发票折让或作废";// 异常类型：invoiceRefund（发票折让或作废）
                // 写下异常
                Map<String, Object> map_DCP_ORDER_ABNORMALINFO = new HashMap<String, Object>();
                map_DCP_ORDER_ABNORMALINFO.put("EID", eId);
                map_DCP_ORDER_ABNORMALINFO.put("ORDERNO", originOrderNo);
                map_DCP_ORDER_ABNORMALINFO.put("ABNORMALTYPE", ABNORMALTYPE);
                map_DCP_ORDER_ABNORMALINFO.put("ABNORMALTYPENAME", ABNORMALTYPENAME);
                
                map_DCP_ORDER_ABNORMALINFO.put("MEMO", "参数PosUrl(POS服务的接口地址)未设置,无法调用POS服务");
                map_DCP_ORDER_ABNORMALINFO.put("STATUS", "100");
                map_DCP_ORDER_ABNORMALINFO.put("LASTMODIOPID", "");
                map_DCP_ORDER_ABNORMALINFO.put("LASTMODIOPNAME", "");
                
                StringBuffer error = new StringBuffer();
                
                insert_DCP_ORDER_ABNORMALINFO(map_DCP_ORDER_ABNORMALINFO, error);
                
                
                return false;
            }
            
            
            // 组装pos服务header
            
            String langType = "zh_TW";// 默认繁体吧，如果没传，因为台湾环境才有发票试算
            
            Map<String, Object> mapHeader = new HashMap<>();
            String serviceId = "POS_InvoiceRefund_Open";
            String requestId = UUID.randomUUID().toString();
            
            mapHeader.put("serviceId", serviceId);
            mapHeader.put("requestId", requestId);
            mapHeader.put("langType", langType);
            mapHeader.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            mapHeader.put("version", "v3.0");
            mapHeader.put("apiUserCode", apiUserCode);
            
            
            org.json.JSONObject requestObj = new org.json.JSONObject();
            String saleType = "Order";//单据类型：Sale销售单，Order订单，Card售卡 Coupon售券，Recharge储值
            requestObj.put("saleType", saleType);
            requestObj.put("oriSaleNo", originOrderNo);
            requestObj.put("refundSaleNo", refundOrderNo);
            requestObj.put("reasonCode", reasonCode);
            requestObj.put("reason", reason);
            requestObj.put("InvOperateType", InvOperateType);
            requestObj.put("opNo", opNo);
            String request = requestObj.toString();
            try
            {
                
                mapHeader.put("sign", PosPub.encodeMD5(request + apiUserKey));
                writelog_waimai(logBeginStr + "调用接口url:" + posUrl + ",调用接口serviceId:" + serviceId + "\r\n请求header:"
                        + mapHeader.toString() + "\r\n请求Request:" + request);
                String res = HttpSend.doPost(posUrl, request, mapHeader,requestId);
                writelog_waimai(logBeginStr + "返回res:" + res);
                
                if (res != null)
                {
                    JSONObject resJson = new JSONObject(res);
                    boolean success = resJson.getBoolean("success");
                    
                    if (success)
                    {
                        JSONObject datasObj = resJson.getJSONObject("datas");
                        String rebateNo = datasObj.optString("rebateNo","");
                        if (outMap==null)
                        {
                            outMap = new HashMap<String,Object>();
                        }
                        outMap.put("rebateNo", rebateNo);
                        nRest = true;
                        writelog_waimai(logBeginStr + "发票作废或折让成功");
                    }
                    else
                    {
                        errorMessage.append(resJson.get("serviceDescription").toString());
                    }
                }
                else
                {
                    errorMessage.append("调用接口" + serviceId + "返回为空!");
                }
                
            }
            catch (Exception e)
            {
                // TODO: handle exception
                errorMessage.append(e.getMessage());
            }
            
            if (nRest == false)
            {
                String ABNORMALTYPE = "invoiceRefund";// 异常类型：invoiceRefund（发票折让或作废）
                String ABNORMALTYPENAME = "发票折让或作废";// 异常类型：invoiceRefund（发票折让或作废）
                // 写下异常
                Map<String, Object> map_DCP_ORDER_ABNORMALINFO = new HashMap<String, Object>();
                map_DCP_ORDER_ABNORMALINFO.put("EID", eId);
                map_DCP_ORDER_ABNORMALINFO.put("ORDERNO", originOrderNo);
                map_DCP_ORDER_ABNORMALINFO.put("ABNORMALTYPE", ABNORMALTYPE);
                map_DCP_ORDER_ABNORMALINFO.put("ABNORMALTYPENAME", ABNORMALTYPENAME);
                
                map_DCP_ORDER_ABNORMALINFO.put("MEMO", errorMessage.toString());
                map_DCP_ORDER_ABNORMALINFO.put("STATUS", "100");
                map_DCP_ORDER_ABNORMALINFO.put("LASTMODIOPID", "");
                map_DCP_ORDER_ABNORMALINFO.put("LASTMODIOPNAME", "");
                
                StringBuffer error = new StringBuffer();
                
                insert_DCP_ORDER_ABNORMALINFO(map_DCP_ORDER_ABNORMALINFO, error);
                
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            errorMessage.append(e.getMessage());
        }
        
        
        return nRest;
    }
    
    
    /**
     * 发票试算或者开立调用接口异常记录
     * @param map
     * @param error
     */
    public  static void insert_DCP_ORDER_ABNORMALINFO(Map<String, Object> map,StringBuffer error) throws Exception
    {
        String sdateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        String EID = map.getOrDefault("EID", "").toString();
        String ORDERNO = map.getOrDefault("ORDERNO", "").toString();
        String ABNORMALTYPE = map.getOrDefault("ABNORMALTYPE", "").toString();//异常类型
        String ABNORMALTYPENAME = map.getOrDefault("ABNORMALTYPENAME", "").toString();//异常类型名称
        String ABNORMALTIME = sdateTime;
        String MEMO = map.getOrDefault("MEMO", "").toString();
        String STATUS = map.getOrDefault("STATUS", "100").toString();
        String LASTMODIOPID = map.getOrDefault("LASTMODIOPID", "").toString();
        String LASTMODIOPNAME = map.getOrDefault("LASTMODIOPNAME", "").toString();
        String LASTMODITIME = sdateTime;
        try
        {
            writelog_waimai("单号orderNo="+ORDERNO+",发票试算或开立调用接口返回有异常，开始插入或更新表DCP_ORDER_ABNORMALINFO");
            
            
            if(ABNORMALTYPE.length()>32)
            {
                ABNORMALTYPE = ABNORMALTYPE.substring(0, 32);
            }
            
            if(ABNORMALTYPENAME.length()>64)
            {
                ABNORMALTYPENAME = ABNORMALTYPENAME.substring(0, 64);
            }
            
            if(MEMO.length()>1024)
            {
                MEMO = MEMO.substring(0, 1024);
            }
            List<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
            String sql = "select * from DCP_ORDER_ABNORMALINFO where EID='"+EID+"' AND ORDERNO='"+ORDERNO+"' AND ABNORMALTYPE='"+ABNORMALTYPE+"' ";
            List<Map<String, Object>> isExist = StaticInfo.dao.executeQuerySQL(sql, null);
            if(isExist!=null&&isExist.isEmpty()==false)
            {
                UptBean ub = null;
                ub = new UptBean("DCP_ORDER_ABNORMALINFO");
                
                ub.addUpdateValue("ABNORMALTYPENAME", new DataValue(ABNORMALTYPENAME, Types.VARCHAR));
                ub.addUpdateValue("ABNORMALTIME", new DataValue(ABNORMALTIME, Types.DATE));
                ub.addUpdateValue("MEMO", new DataValue(MEMO, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPID", new DataValue(LASTMODIOPID, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME", new DataValue(LASTMODIOPNAME, Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(LASTMODITIME, Types.DATE));
                
                
                // condition
                ub.addCondition("EID", new DataValue(EID, Types.VARCHAR));
                ub.addCondition("ORDERNO", new DataValue(ORDERNO, Types.VARCHAR));
                ub.addCondition("ABNORMALTYPE", new DataValue(ABNORMALTYPE, Types.VARCHAR));
                
                DPB.add(new DataProcessBean(ub));
            }
            else
            {
                String[] columns1 = {
                        "EID","ORDERNO","ABNORMALTYPE","ABNORMALTYPENAME","ABNORMALTIME","MEMO","STATUS","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
                };
                
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(EID, Types.VARCHAR),
                        new DataValue(ORDERNO, Types.VARCHAR),
                        new DataValue(ABNORMALTYPE, Types.VARCHAR),
                        new DataValue(ABNORMALTYPENAME, Types.VARCHAR),
                        new DataValue(ABNORMALTIME, Types.DATE),
                        new DataValue(MEMO, Types.VARCHAR),
                        new DataValue(STATUS, Types.VARCHAR),
                        new DataValue(LASTMODIOPID, Types.VARCHAR),
                        new DataValue(LASTMODIOPNAME, Types.VARCHAR),
                        new DataValue(LASTMODITIME, Types.DATE)
                };
                
                InsBean ib1 = new InsBean("DCP_ORDER_ABNORMALINFO", columns1);
                ib1.addValues(insValue1);
                
                DPB.add(new DataProcessBean(ib1));
                
                
            }
            
            StaticInfo.dao.useTransactionProcessData(DPB);
            writelog_waimai("单号orderNo="+ORDERNO+",发票试算或开立调用接口返回有异常，开始插入或更新表DCP_ORDER_ABNORMALINFO成功!");
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimai("单号orderNo="+ORDERNO+",发票试算或开立调用接口返回有异常，开始插入或更新表DCP_ORDER_ABNORMALINFO异常:"+e.getMessage());
        }
    }
    
    
    public  static List<orderGoodsItem> waimaiPartRefundGoodsProcess(order dcpOrder,StringBuffer error) throws Exception
    {
        List<orderGoodsItem> goodsList_partRefund = new ArrayList<orderGoodsItem>();//真正的转化出来的部分退单单身(包含套餐商品处理)
        
        try
        {
            String orderNo = dcpOrder.getOrderNo();
            String eId = dcpOrder.geteId();
            
            String logStartStr = "【外卖部分退单成功】更新处理部分退订商品,订单号orderNo="+orderNo;
            writelog_waimai(logStartStr+",【开始】");
            int scaleCount = 2;//默认小数位
            
            List<orderGoodsItem> goodsList_origin = new ArrayList<orderGoodsItem>();//原单的商品单身
            List<orderGoodsItem> goodsList_origin_partRefund= new ArrayList<orderGoodsItem>();//部分退单商品单身(推送的消息返回添加的负数的单身)
            
            for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
            {
                double qty = goodsItem.getQty();
                if (qty >=  0) //之前给的退单商品时负数
                {
                    goodsList_origin.add(goodsItem);
                }
                else
                {
                    goodsList_origin_partRefund.add(goodsItem);
                }
            }
            
            
            int partRefundIndex = 0;
            for (orderGoodsItem goodsItem : goodsList_origin_partRefund)
            {
                partRefundIndex ++;//后面可能有套餐子商品，item 也是累加
                String pluName = goodsItem.getPluName();
                String specName = goodsItem.getSpecName();
                double qty = 0- goodsItem.getQty(); //由于之前添加的是负数
                double boxNum = 0-goodsItem.getBoxNum();
                double amt = 0- goodsItem.getAmt();
                
                for (orderGoodsItem goodsItem_origin : goodsList_origin)
                {
                    String pluName_origin = goodsItem_origin.getPluName();
                    String specName_origin = goodsItem_origin.getSpecName();
                    String packageType = goodsItem_origin.getPackageType();
                    double qty_origin = goodsItem_origin.getQty();
                    String item_orgin = goodsItem_origin.getItem();
                    double amt_origin = goodsItem_origin.getAmt();
                    
                    
                    //1、正常商品 2、套餐主商品  3、套餐子商品
                    if(packageType!=null&&packageType.equals("3"))
                    {
                        //过滤套餐子商品
                        continue;
                    }
                    
                    //商品名称 相等
                    if(pluName_origin.equals(pluName)==false)
                    {
                        continue;
                    }
                    if(qty>qty_origin)
                    {
                        continue;
                    }
                    //如果退单的商品有规格名称
					/*if(specName!=null&&specName.trim().isEmpty()==false)
					{
						if(specName.equals(specName_origin)==false)
						{
							continue;
						}
					}*/
                    
                    //找到了
                    writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin);
                    orderGoodsItem  goodsItem_refund = null;
                    try
                    {
                        goodsItem_refund = PosPub.deepCopy(goodsItem_origin);
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+",深拷贝对象返回异常："+e.getMessage());
                    }
                    
                    
                    if(goodsItem_refund==null)
                    {
                        writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+",深拷贝对象返回null");
                        goodsItem_refund = new orderGoodsItem();
                    }
                    
                    
                    goodsItem_refund.setAttrName(goodsItem_origin.getAttrName());
                    goodsItem_refund.setBoxNum(goodsItem_origin.getBoxNum());
                    goodsItem_refund.setBoxPrice(goodsItem_origin.getBoxPrice());
                    goodsItem_refund.setDisc(goodsItem_origin.getDisc());
                    goodsItem_refund.setFeatureName(goodsItem_origin.getFeatureName());
                    goodsItem_refund.setFeatureNo(goodsItem_origin.getFeatureNo());
                    goodsItem_refund.setGoodsGroup(goodsItem_origin.getGoodsGroup());
                    goodsItem_refund.setIsMemo(goodsItem_origin.getIsMemo());
                    goodsItem_refund.setMessages(new ArrayList<orderGoodsItemMessage>());
                    
                    goodsItem_refund.setPackageType(goodsItem_origin.getPackageType());
                    goodsItem_refund.setPluBarcode(goodsItem_origin.getPluBarcode());
                    goodsItem_refund.setPluNo(goodsItem_origin.getPluNo());
                    goodsItem_refund.setPluName(goodsItem_origin.getPluName());
                    goodsItem_refund.setSkuId(goodsItem_origin.getSkuId());
                    goodsItem_refund.setSpecName(goodsItem_origin.getSpecName());
                    goodsItem_refund.setsUnit(goodsItem_origin.getsUnit());
                    goodsItem_refund.setsUnitName(goodsItem_origin.getsUnitName());
                    goodsItem_refund.setWarehouse(goodsItem_origin.getWarehouse());
                    goodsItem_refund.setWarehouseName(goodsItem_origin.getWarehouseName());
                    goodsItem_refund.setVirtual(goodsItem_origin.getVirtual());
                    
                    
                    int curItem = partRefundIndex;
                    
                    goodsItem_refund.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                    goodsItem_refund.setoItem(item_orgin);
                    goodsItem_refund.setItem(curItem+"");
                    
                    goodsItem_refund.setQty(qty);
                    goodsItem_refund.setPrice(goodsItem.getPrice());
                    goodsItem_refund.setAmt(amt);
                    goodsItem_refund.setAmt_custPayReal(amt);
                    goodsItem_refund.setAmt_merReceive(amt);
                    goodsItem_refund.setOldAmt(amt);
                    goodsItem_refund.setOldPrice(goodsItem.getPrice());
                    goodsItem_refund.setDisc(0);
                    goodsItem_refund.setDisc_custPayReal(0);
                    goodsItem_refund.setDisc_custPayReal(0);
                    
                    goodsItem_refund.setBoxNum(boxNum);
                    goodsItem_refund.setBoxPrice(goodsItem.getBoxPrice());
                    
                    goodsList_partRefund.add(goodsItem_refund);
                    
                    //如果这个退货得商品是套餐商品，还要找出来
                    if(packageType!=null&&packageType.equals("2"))
                    {
                        writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+"->【套餐商品】处理开始");
                        // 找出套餐子商品 ，筛选 子商品 MItem = 主商品Item
                        List<orderGoodsItem> goodsList_origin_package = goodsList_origin.stream()
                                .filter(g -> g.getPackageType().equals("3")
                                        && g.getPackageMitem().equals(item_orgin))
                                .collect(Collectors.toList());
                        
                        if(goodsList_origin_package!=null&&goodsList_origin_package.isEmpty()==false)
                        {
                            
                            //算个比例出来
                            BigDecimal packageCountRate = new BigDecimal("1");//默认1，防止被除数=0
                            try
                            {
                                packageCountRate = new BigDecimal(qty/qty_origin);
                            } catch (Exception e)
                            {
                                // TODO: handle exception
                                
                            }
                            
                            
                            BigDecimal packageGoodsItem_TotAmt = new BigDecimal("0");//分摊退单的套餐主商品金额
                            BigDecimal goodsMItem_Amt = new BigDecimal(amt);//套餐主商品实际金额
                            
                            for (int i =0;i<goodsList_origin_package.size();i++)
                            {
                                writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+"->【套餐商品】处理开始->【添加套餐子商品】开始");
                                partRefundIndex ++;
                                int curPackageItem = partRefundIndex;
                                orderGoodsItem  goodsItem_package_refund = null;
                                try
                                {
                                    goodsItem_package_refund = PosPub.deepCopy(goodsList_origin_package.get(i));
                                    
                                } catch (Exception e)
                                {
                                    writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+"->【套餐商品】处理开始->【添加套餐子商品】开始,深拷贝对象返回异常："+e.getMessage());
                                }
                                
                                if(goodsItem_package_refund==null)
                                {
                                    writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+"->【套餐商品】处理开始->【添加套餐子商品】开始,深拷贝对象返回null");
                                    goodsItem_package_refund = new orderGoodsItem();
                                }
                                
                                
                                goodsItem_package_refund.setAttrName(goodsList_origin_package.get(i).getAttrName());
                                goodsItem_package_refund.setBoxNum(goodsList_origin_package.get(i).getBoxNum());
                                goodsItem_package_refund.setBoxPrice(goodsList_origin_package.get(i).getBoxPrice());
                                goodsItem_package_refund.setDisc(goodsList_origin_package.get(i).getDisc());
                                goodsItem_package_refund.setFeatureName(goodsList_origin_package.get(i).getFeatureName());
                                goodsItem_package_refund.setFeatureNo(goodsList_origin_package.get(i).getFeatureNo());
                                goodsItem_package_refund.setGoodsGroup(goodsList_origin_package.get(i).getGoodsGroup());
                                goodsItem_package_refund.setIsMemo(goodsList_origin_package.get(i).getIsMemo());
                                goodsItem_package_refund.setMessages(new ArrayList<orderGoodsItemMessage>());
                                
                                goodsItem_package_refund.setPackageType(goodsList_origin_package.get(i).getPackageType());
                                goodsItem_package_refund.setPluBarcode(goodsList_origin_package.get(i).getPluBarcode());
                                goodsItem_package_refund.setPluNo(goodsList_origin_package.get(i).getPluNo());
                                goodsItem_package_refund.setPluName(goodsList_origin_package.get(i).getPluName());
                                goodsItem_package_refund.setSkuId(goodsList_origin_package.get(i).getSkuId());
                                goodsItem_package_refund.setSpecName(goodsList_origin_package.get(i).getSpecName());
                                goodsItem_package_refund.setsUnit(goodsList_origin_package.get(i).getsUnit());
                                goodsItem_package_refund.setsUnitName(goodsList_origin_package.get(i).getsUnitName());
                                goodsItem_package_refund.setWarehouse(goodsList_origin_package.get(i).getWarehouse());
                                goodsItem_package_refund.setWarehouseName(goodsList_origin_package.get(i).getWarehouseName());
                                goodsItem_package_refund.setVirtual(goodsList_origin_package.get(i).getVirtual());
                                
                                
                                double oldPackageAmt = goodsItem_package_refund.getAmt();
                                double oldPackageQty = goodsItem_package_refund.getQty();
                                double oldPackagePrice = goodsItem_package_refund.getPrice();
                                String oldPackageItem = goodsItem_package_refund.getItem();
                                
                                
                                goodsItem_package_refund.setItem(curPackageItem+"");
                                goodsItem_package_refund.setoItem(oldPackageItem);
                                
                                //金额分摊下，按之前的比例
                                BigDecimal amtRate  = new BigDecimal("1");
                                try
                                {
                                    amtRate = new BigDecimal(oldPackageAmt/amt_origin);//原单的套餐金额分摊比例
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                BigDecimal curPackageAmt = new BigDecimal("0");
                                BigDecimal curPackageQty = new BigDecimal("0");
                                BigDecimal curPackagePrice = new BigDecimal("0");
                                if(i==goodsList_origin_package.size()-1)//最后一个减
                                {
                                    curPackageAmt = goodsMItem_Amt.subtract(packageGoodsItem_TotAmt).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                }
                                else
                                {
                                    curPackageAmt = goodsMItem_Amt.multiply(amtRate).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                    packageGoodsItem_TotAmt = packageGoodsItem_TotAmt.add(curPackageAmt).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                }
                                
                                curPackageQty = packageCountRate.multiply(new BigDecimal(oldPackageQty)).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                //反算价格
                                try
                                {
                                    curPackagePrice = curPackageAmt.divide(curPackageQty,scaleCount, BigDecimal.ROUND_HALF_UP);
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                
                                goodsItem_package_refund.setItem(curPackageItem+"");
                                goodsItem_package_refund.setoItem(oldPackageItem);
                                goodsItem_package_refund.setPackageMitem(curItem+"");//关联套餐主商品
                                goodsItem_package_refund.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                                goodsItem_package_refund.setPrice(curPackagePrice.doubleValue());
                                goodsItem_package_refund.setAmt(curPackageAmt.doubleValue());
                                goodsItem_package_refund.setAmt_custPayReal(curPackageAmt.doubleValue());
                                goodsItem_package_refund.setAmt_merReceive(curPackageAmt.doubleValue());
                                goodsItem_package_refund.setQty(curPackageQty.doubleValue());
                                goodsItem_package_refund.setBoxNum(0);
                                goodsItem_package_refund.setOldAmt(curPackageAmt.doubleValue());
                                goodsItem_package_refund.setOldPrice(curPackagePrice.doubleValue());
                                goodsItem_package_refund.setDisc(0);
                                goodsItem_package_refund.setDisc_custPayReal(0);
                                goodsItem_package_refund.setDisc_merReceive(0);
                                
                                goodsList_partRefund.add(goodsItem_package_refund);
                                writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+"->【套餐商品】处理开始->【添加套餐子商品】成功");
                                
                            }
                        }
                        
                    }
                    
                    writelog_waimai(logStartStr+",匹配到原单商品项次item="+item_orgin+"【添加到部分退单商品列表成功】");
                    break;
                }
                
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        
        return goodsList_partRefund;
    }
    
    /**
     * 处理外卖部分退订
     * @param dcpOrder 原单对象
     * @param goodsList_partRefund 已经处理好的关联原单的部分退订商品列表
     * @param refundBdate
     * @param errorMessage
     * @throws Exception
     */
    public static void waimaiOrderPartRefundProcess(order dcpOrder, List<orderGoodsItem> goodsList_partRefund, String refundBdate, StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【第三方外卖平台推送部分退单成功消息处理】单号orderNo="+orderNo+",";
        try
        {
            
            boolean checkPara = false;
            if(eId==null||eId.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单eId=为空,");
                checkPara = true;
                
            }
            if(orderNo==null||orderNo.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单orderNo=为空,");
                checkPara = true;
                
            }
            if(loadDocType==null||loadDocType.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单loadDocType=为空,");
                checkPara = true;
                
            }
            if(channelId==null||channelId.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单channelId=为空,");
                checkPara = true;
                
            }
            if(status==null||status.isEmpty())
            {
                errorMessage.append(logBeginStr+"订单状态status=为空,");
                checkPara = true;
                
            }
            
            if(checkPara)
            {
                return;
            }
            
            
            if(status.equals("3")||status.equals("12"))
            {
                errorMessage.append(logBeginStr+"订单状态status="+status+"，无须处理");
                return;
            }
            
            
            
            String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String stime = new SimpleDateFormat("HHmmss").format(new Date());
            String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            String tran_time = update_time;
            String bdate = refundBdate;
            if(bdate==null||bdate.isEmpty())
            {
                bdate = sdate;
            }
            
            int otype = 0;//退单来源类型
            
            ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
            List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
            //查询来源类型是订单，来源单号是订单单号的销售单号
            String sql = "select  SALENO,SHOPID from dcp_sale where ofno='"+orderNo+"' and eid='"+eId+"' ";
            HelpTools.writelog_waimai(logBeginStr+"有没有生成销售单，查询sql:"+sql);
            List<Map<String, Object>> sourceSaleNoList = StaticInfo.dao.executeQuerySQL(sql, null);
            double tot_amt = dcpOrder.getRefundAmt();//部分退订金额
            double pay = tot_amt;
            //单身的金额合计，与单头存在差异， 数量*单价=金额，分摊到最后一个商品上去
            double tot_oldamt = tot_amt;
            double tot_disc = 0;
            double refundAmt_goods = 0;
            List<orderGoodsItem> goodsList_partRefund_noPackageDetail = new ArrayList<orderGoodsItem>();//不包含套餐子商品
            List<orderGoodsItem> goodsList_partRefund_PackageDetail = new ArrayList<orderGoodsItem>();//套餐子商品
            for (orderGoodsItem map : goodsList_partRefund)
            {
                String pluno = map.getPluNo();
                String item =  map.getItem();
                double amt = map.getAmt();
                String packageType = map.getPackageType();
                if(amt<0.01)
                {
                    continue;
                }
                //套餐子商品过滤
                if(packageType!=null&&packageType.equals("3"))
                {
                    goodsList_partRefund_PackageDetail.add(map);
                    continue;
                }
                refundAmt_goods +=amt;
                goodsList_partRefund_noPackageDetail.add(map);
            }
            
            double deff_money = new BigDecimal(refundAmt_goods-tot_amt).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            if(Math.abs(deff_money)>=0.01)
            {
                tot_disc = deff_money;
                tot_oldamt = new BigDecimal(tot_amt+tot_disc).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                HelpTools.writelog_waimai(logBeginStr+"部分退款金额与商品明细合计存在差异金额:"+deff_money+",单头tot_oldamt="+tot_oldamt+",总折扣tot_disc="+tot_disc+",金额tot_amt="+tot_amt);
                //分摊到最后一个商品上去
                String dcType = "35";
                String dcTypeName = "";
                for (int i = 0;i<goodsList_partRefund_noPackageDetail.size();i++)
                {
                    if (i==goodsList_partRefund_noPackageDetail.size()-1)
                    {
                        orderGoodsItem map = goodsList_partRefund_noPackageDetail.get(i);
                        String item =  map.getItem();
                        double amt = map.getAmt();
                        double oldamt = map.getOldAmt();
                        String packageType = map.getPackageType();
                        double disc = tot_disc;
                        BigDecimal new_amt = new BigDecimal("0");
                        BigDecimal oldamt_b = new BigDecimal(oldamt);
                        BigDecimal disc_b = new BigDecimal(tot_disc);
                        new_amt = oldamt_b.subtract(disc_b);
                        map.setDisc(disc);
                        map.setAmt(new_amt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                        //判断是不是是套餐主商品
                        if(packageType!=null&&packageType.equals("2"))
                        {
                            HelpTools.writelog_waimai(logBeginStr+"是套餐【主】商品，准备添加到子商品折扣开始，套餐主商品单身项次item="+map.getItem()+",pluno="+map.getPluNo()+",折扣disc="+disc+",oldamt="+map.getOldAmt()+",amt="+map.getAmt());
                            // 找出套餐子商品 ，筛选 子商品 MItem = 主商品Item
                            List<orderGoodsItem> goodsList_curItem_package = goodsList_partRefund_PackageDetail.stream()
                                    .filter(g -> g.getPackageType().equals("3")
                                            && g.getPackageMitem().equals(item))
                                    .collect(Collectors.toList());
                            if(goodsList_curItem_package!=null&&goodsList_curItem_package.isEmpty()==false)
                            {
                                
                                //同样分摊到最后一个套餐子商品
                                for (int j = 0;j<goodsList_curItem_package.size();j++)
                                {
                                    if (j==goodsList_curItem_package.size()-1)
                                    {
                                        orderGoodsItem mapPackageDetail = goodsList_curItem_package.get(j);
                                        String item_packageDetail =  mapPackageDetail.getItem();
                                        double amt_packageDetail = mapPackageDetail.getAmt();
                                        double oldamt_packageDetail = mapPackageDetail.getOldAmt();
                                        double disc_packageDetail = tot_disc;
                                        BigDecimal new_amt_packageDetail = new BigDecimal("0");
                                        BigDecimal oldamt_b_packageDetail = new BigDecimal(oldamt_packageDetail);
                                        BigDecimal disc_b_packageDetail = new BigDecimal(tot_disc);
                                        new_amt_packageDetail = oldamt_b_packageDetail.subtract(disc_b_packageDetail);
                                        mapPackageDetail.setDisc(disc_packageDetail);
                                        mapPackageDetail.setAmt(new_amt_packageDetail.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                                        
                                        orderGoodsItemAgio agio = new orderGoodsItemAgio();
                                        agio.setDcType(dcType);
                                        agio.setAmt(mapPackageDetail.getOldAmt());
                                        agio.setDisc(disc_packageDetail);
                                        agio.setRealDisc(0-disc_packageDetail);
                                        agio.setQty(mapPackageDetail.getQty());
                                        agio.setItem("1");
                                        List<orderGoodsItemAgio> agioList = new ArrayList<orderGoodsItemAgio>();
                                        agioList.add(agio);
                                        mapPackageDetail.setAgioInfo(agioList);
                                        
                                        HelpTools.writelog_waimai(logBeginStr+"套餐【子商品】商品添加折扣成功，商品单身项次item="+mapPackageDetail.getItem()+",pluno="+mapPackageDetail.getPluNo()+",折扣disc="+disc_packageDetail+",oldamt="+mapPackageDetail.getOldAmt()+",amt="+mapPackageDetail.getAmt());
                                        break;
                                    }
                                    
                                    
                                }
                                
                            }
                            
                            
                        }
                        else
                        {
                            orderGoodsItemAgio agio = new orderGoodsItemAgio();
                            agio.setDcType(dcType);
                            agio.setAmt(map.getOldAmt());
                            agio.setDisc(disc);
                            agio.setRealDisc(0-disc);
                            agio.setQty(map.getQty());
                            agio.setItem("1");
                            List<orderGoodsItemAgio> agioList = new ArrayList<orderGoodsItemAgio>();
                            agioList.add(agio);
                            map.setAgioInfo(agioList);
                            HelpTools.writelog_waimai(logBeginStr+"非套餐主商品添加折扣成功，商品单身项次item="+map.getItem()+",pluno="+map.getPluNo()+",折扣disc="+disc+",oldamt="+map.getOldAmt()+",amt="+map.getAmt());
                        }
                        
                        break;
                        
                    }
                }
            }
            
            
            //如果存在的话，需要生成销退单
            if(sourceSaleNoList!=null&&sourceSaleNoList.size()>0)
            {
                String sourceSaleNo = sourceSaleNoList.get(0).get("SALENO").toString();
                String shopid = sourceSaleNoList.get(0).get("SHOPID").toString();
                String saleno = "RE"+sourceSaleNo+"_01";//部分退单号
                int type = 1;//退单的类型type
                String typename = "原单退";
                String ofno = sourceSaleNo;//退单ofno的来源单号
                
                sql = "";
                sql = "select  SALENO,SHOPID from dcp_sale where type=1 and ofno='"+sourceSaleNo+"' and eid='"+eId+"' and shopid='"+shopid+"' ";
                HelpTools.writelog_waimai(logBeginStr+"对应销售单单号saleNo="+sourceSaleNo+",有没有生成销退单单，查询sql:"+sql);
                
                List<Map<String, Object>> sourceRefundSaleNoList = StaticInfo.dao.executeQuerySQL(sql, null);
                if(sourceRefundSaleNoList!=null&&sourceRefundSaleNoList.size()>0)
                {
                    HelpTools.writelog_waimai(logBeginStr+"对应销售单单号saleNo="+sourceSaleNo+",已生成销退单单，无须再生成");
                    return;
                }
                
                
                
                StringBuffer strBuff = new StringBuffer("");
                String execsql ="";
                BigDecimal tot_qty = new BigDecimal("0");
                
                
                for (orderGoodsItem map : goodsList_partRefund)
                {
                    //生成商品单身
                    String pluno = map.getPluNo();
                    String item =  map.getItem();
                    double qty = map.getQty();
                    tot_qty = tot_qty.add(new BigDecimal(qty));
                    
                    double price = map.getPrice();
                    double oldprice = price;
                    double price2 = price;
                    double price3 = price;
                    double amt = map.getAmt();
                    double disc = map.getDisc();
                    
                    double oldamt = map.getOldAmt();
                    String ispackage="N";//是否套餐
                    String packagemaster="N";//套餐主商品
                    String upitem = "0";//套餐主商品项次
                    String packageType = map.getPackageType();
                    double packageamt = 0;
                    double packageqty = 0;
                    //1、正常商品 2、套餐主商品  3、套餐子商品
                    if(packageType!=null&&packageType.equals("2"))
                    {
                        ispackage="Y";
                        packagemaster ="Y";
                        packageamt = amt;
                        packageqty = qty;
                        
                    }
                    if(packageType!=null&&packageType.equals("3"))
                    {
                        ispackage="Y";
                        upitem = map.getPackageMitem();
                        packageamt = amt;
                        packageqty = qty;
                    }
                    
                    
                    strBuff = new StringBuffer("");
                    strBuff.append(" insert into DCP_SALE_DETAIL (");//分区字段
                    strBuff.append(" eid, shopid, saleno, warehouse, item, oitem, clerkno, sellername, accno, tableno, dealtype, coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                            + ", qty, oldprice, price2, price3, canback, bsno, rqty, returnuserid, returntableno, refundopno, refundtime, oldamt, disc, saledisc, paydisc, price, additionalprice, amt, point_qty, counteramt, servcharge, packagemaster, ispackage, packageamt, packageqty, upitem, shareamt, isstuff, detailitem, flavorstuffdetail"
                            + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, bdate, sdate, stime, tran_time, prom_couponno, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理
                    
                    strBuff.append(" select eid, shopid, '"+saleno+"', warehouse, "+item+", item, clerkno, sellername, accno, tableno, '2', coupontype, couponcode, pluno, pname, isgift, giftreason, giftopno, gifttime, isexchange, ispickgood, plubarcode, scanno, mno, counterno, srackno, batchno, tgcategoryno, featureno, attr01, attr02, unit, baseunit"
                            + ", "+qty+", "+oldprice+", "+price2+", "+price3+", canback, bsno, 0, returnuserid, returntableno, refundopno, refundtime, "+oldamt+", "+disc+", "+disc+", 0, "+price+", additionalprice, "+amt+", point_qty, counteramt, servcharge, '"+packagemaster+"', '"+ispackage+"', "+packageamt+", "+packageqty+", "+upitem+", shareamt, isstuff, detailitem, flavorstuffdetail"
                            + ", cakeblessing, materials, dishesstatus, socalled, repast_type, packageamount, packageprice, packagefee, incltax, taxcode, taxtype, taxrate, orderrateamount, memo, confirmopno, confirmtime, ordertime, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', prom_couponno, 0, "+amt+", 0, "+amt+",'"+bdate+"'");//分区字段已处理
                    
                    strBuff.append(" from DCP_SALE_DETAIL where rownum=1 and eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"'  and pluno='"+pluno+"' ");
                    execsql = strBuff.toString();
                    
                    ExecBean exSale_detail = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_detail));
                    if (map.getAgioInfo()!=null&&map.getAgioInfo().isEmpty()==false)
                    {
                        for (orderGoodsItemAgio agio : map.getAgioInfo())
                        {
                            strBuff = new StringBuffer("");
                            strBuff.append(" insert into DCP_SALE_DETAIL_AGIO (");//分区字段
                            strBuff.append(" EID, SHOPID, SALENO, MITEM, ITEM, QTY, AMT, DISC, REALDISC, DCTYPE, DCTYPENAME, PMTNO,PARTITION_DATE,STATUS)");
                            strBuff.append(" values ('"+dcpOrder.geteId()+"', '"+shopid+"', '"+saleno+"', "+item+", "+agio.getItem()+", "+agio.getQty()+", "+agio.getAmt()+", "+agio.getDisc()+","+agio.getRealDisc()+", '"+agio.getDcType()+"', '"+agio.getDcTypeName()+"', '','"+bdate+"',100 )");
                            
                            String execsql_agio = strBuff.toString();
                            
                            ExecBean exSale_detail_agio = new ExecBean(execsql_agio);
                            DataPB.add(new DataProcessBean(exSale_detail_agio));
                        }
                    }
                    
                }
                tot_qty = tot_qty.setScale(2,BigDecimal.ROUND_HALF_UP);
                
                
                
                //生成单头语句
                ofno = "";//否则整单退有问题。
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE (");//分区字段
                strBuff.append(" eid,shopid,saleno,trno,ver_num,legalper,machine,type,typename,bdate,squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,otype,ofno,sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                        + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                        + ",tot_qty,tot_oldamt,tot_disc,saledisc,paydisc,erase_amt,tot_amt,servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,pay_amt,tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                        + ",order_id,order_sn,platform_disc,seller_disc,packagefee,shippingfee,delivery_fee_shop,delivery_fee_user,wm_user_paid,platform_fee,wm_extra_fee,shopincome,productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                        + ",sdate,stime,evaluate,isuploaded,update_time,tran_time,rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,WAIMAIMERRECEIVEMODE,PARTITION_DATE )");//分区字段已处理
                
                strBuff.append("select eid,shopid,'"+saleno+"',trno,ver_num,legalper,machine,"+type+",'"+typename+"','"+bdate+"',squadno,workno,opno,authorizeropno,teriminal_id,oshop,omachine,'"+otype+"','"+sourceSaleNo+"',sourcesuborderno,otrno,obdate,approval,cardno,memberid,membername,cardtypeid,cardamount,point_qty,remainpoint,memberorderno,ordershop"
                        + ",contman,conttel,getmode,getshop,getman,getmantel,shipadd,gdate,gtime,manualno,mealnumber,childnumber,memo,ecsflg,ecsdate,distribution,sendmemo,tableno,openid,tablekind,guestnum,repast_type,dinnerdate,dinnertime,dinnersign,dinnertype,tour_countrycode,tour_travelno,tour_groupno,tour_guideno,tour_peoplenum"
                        + ","+tot_qty+","+tot_oldamt+","+tot_disc+","+tot_disc+",0,0,"+tot_amt+",servcharge,orderamount,freecode,passport,isinvoice,invoicetitle,invoicebank,invoiceaccount,invoicetel,invoiceaddr,taxregnumber,sellcredit,customerno,customername,"+pay+",tot_changed,oinvstartno,isinvoicemakeout,invsplittype,invcount,istakeout,takeaway"
                        + ",order_id,order_sn,0,0,0,0,0,0,wm_user_paid,0,0,"+tot_amt+",productionmode,productionshop,isbuffer,buffer_timeout,eccustomerno,status,isreturn,returnuserid,bsno"
                        + ",'"+sdate+"','"+stime+"',evaluate,'N','"+update_time+"','"+tran_time+"',rsv_id,orderreturn,companyid,channelid,apptype,ocompanyid,ochannelid,oapptype,wxopenid, "+tot_amt+", "+tot_amt+", 0, 0,WAIMAIMERRECEIVEMODE,'"+bdate+"'");//分区字段已处理
                
                strBuff.append(" from DCP_SALE where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale));
                
                
                
                //生成商品单身折扣
                
                
                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_SALE_PAY (");//分区字段
                strBuff.append("eid, shopid, saleno, item, paydoctype, paycode, paycodeerp, payname, pay, pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, isorderpay, prepaybillno, authcode, isturnover, status, bdate, sdate, stime, tran_time, paytype, payshop, isdeposit, funcno, MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PARTITION_DATE )");
                strBuff.append(" select eid, shopid, '"+saleno+"', 1, paydoctype, paycode, paycodeerp, payname, "+pay+", pos_pay, changed, extra, returnrate, paysernum, serialno, refno, teriminalno, cttype, cardno, cardamtbefore, remainamt, sendpay, isverification, couponqty, descore, 'N', prepaybillno, authcode, isturnover, status, '"+bdate+"', '"+sdate+"', '"+stime+"', '"+tran_time+"', paytype, payshop, isdeposit, funcno, 0, "+pay+", 0, "+pay+", 0, 0,'"+bdate+"'");
                
                strBuff.append(" from DCP_SALE_PAY where rownum=1 and eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay));
                
                //库存流水账生成
                strBuff = new StringBuffer("");
                strBuff.append(" select * from dcp_stock_detail where billtype=20");
                strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                
                HelpTools.writelog_waimai(logBeginStr+"查询该订单对应的销售单号SALENO="+sourceSaleNo+" 生成的库存流水账，查询sql:"+execsql);
                //营业日期 -存储过程
                String stockChange_BDATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                if(bdate!=null&&bdate.isEmpty()==false)
                {
                    stockChange_BDATE = bdate;
                    if(bdate.length()==8)
                    {
                        stockChange_BDATE = bdate.substring(0,4)+"-"+bdate.substring(4,6)+"-"+bdate.substring(6,8);
                    }
                }
                List<Map<String, Object>> getQData_stockDetail = StaticInfo.dao.executeQuerySQL(execsql, null);
                
                
                //流水表没有到历史流水表里查
                if (getQData_stockDetail==null || getQData_stockDetail.size()==0)
                {
                    strBuff = new StringBuffer("");
                    strBuff.append(" select * from dcp_stock_detail_static where billtype=20");
                    strBuff.append(" and eid='"+eId+"' and ORGANIZATIONNO='"+shopid+"' and billno='"+sourceSaleNo+"' ");
                    execsql = "";
                    execsql = strBuff.toString();
                    HelpTools.writelog_waimai(logBeginStr+"查询该订单对应的销售单号SALENO="+sourceSaleNo+" 生成的库存流水账，查询sql:"+execsql);
                    getQData_stockDetail = StaticInfo.dao.executeQuerySQL(execsql, null);
                }
                
                
                
                if(getQData_stockDetail!=null&&getQData_stockDetail.isEmpty()==false)
                {
                    String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopid);
                    String procedure="SP_DCP_StockChange";
                    
                    for (orderGoodsItem par : goodsList_partRefund)
                    {
                        String pluno = par.getPluNo();
                        String item =  par.getItem();
                        double qty = par.getQty();
                        double price = par.getPrice();
                        double amt = par.getAmt();
                        
                        for (Map<String, Object> map : getQData_stockDetail)
                        {
                            
                            if(pluno.equals(map.get("PLUNO").toString())==false)
                            {
                                continue;
                            }
                            
                            Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1,map.get("EID").toString());                                       //--企业ID
                            inputParameter.put(2,map.get("ORGANIZATIONNO").toString());                                    //--组织
                            inputParameter.put(3,"21");                                      //--单据类型
                            inputParameter.put(4,saleno);	                                 //--单据号
                            inputParameter.put(5,item);            //--单据行号
                            inputParameter.put(6,"1");                                      //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(7,stockChange_BDATE);           //--营业日期 yyyy-MM-dd
                            inputParameter.put(8,map.get("PLUNO").toString());           //--品号
                            inputParameter.put(9,map.get("FEATURENO").toString());       //--特征码
                            inputParameter.put(10,map.get("WAREHOUSE").toString());                                //--仓库
                            inputParameter.put(11,map.get("BATCHNO").toString());       //--批号
                            inputParameter.put(12,map.get("UNIT").toString());          //--交易单位
                            inputParameter.put(13,qty);           //--交易数量
                            inputParameter.put(14,map.get("BASEUNIT").toString());       //--基准单位
                            inputParameter.put(15,qty);        //--基准数量
                            inputParameter.put(16,map.get("UNITRATIO").toString());     //--换算比例
                            inputParameter.put(17,price);          //--零售价
                            inputParameter.put(18,amt);            //--零售金额
                            inputParameter.put(19,map.get("DISTRIPRICE").toString());    //--进货价
                            inputParameter.put(20,map.get("DISTRIAMT").toString());      //--进货金额
                            inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                            inputParameter.put(22,map.get("PRODDATE").toString());      //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(23,sdate);                                  //--单据日期
                            inputParameter.put(24,"");                                       //--异动原因
                            inputParameter.put(25,"销售单退单");                                //--异动描述
                            inputParameter.put(26,"");                                //--操作员
                            
                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            DataPB.add(new DataProcessBean(pdb));
                            HelpTools.writelog_waimai("********** 调用存储过程SP_DCP_StockChange参数："+inputParameter.toString());
                            break;
                            
                        }
                        
                    }
                    
                    
                    
                }
                
                
                //添加订单状态
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(dcpOrder.getChannelId());
                onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                onelv1.seteId(eId);
                onelv1.setOpName("");
                onelv1.setOpNo("");
                onelv1.setShopNo(dcpOrder.getShopNo());
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo(dcpOrder.getMachShopNo());
                onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
                String statusType = "";
                String updateStaus = "99";//其他
                statusType = "99";// 订单状态
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = "其他";
                String statusTypeName = "其他状态";
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                String memo = "生成部分销退单成功";
                
                onelv1.setMemo(memo);
                onelv1.setDisplay("0");
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
            }
            else//没有生成销售单，需要生成退订单
            {
                String sourceOrderNo = orderNo;
                String orderno = "RE"+sourceOrderNo+"_01";//退订单单号
                
                String billno = UUID.randomUUID().toString().replace("-", "");//收款单号
                String billtype = "-1";//单据类型
                String direction = "-1";//金额方向:1、-1
                String usetype = "refund";//款项用途：front-预付款 refund-退款 final-尾款
                
                //判断下有没有生成过 退订单了(整单或部分的)
                sql = "";
                sql = "select  ORDERNO from DCP_ORDER where billtype='-1' and (orderno='"+orderno+"' or orderno='RE"+sourceOrderNo+"') and eid='"+eId+"' ";
                HelpTools.writelog_waimai(logBeginStr+"对应退订单号orderNo="+orderno+",有没有生成退订单，查询sql:"+sql);
                
                List<Map<String, Object>> sourceRefundOrderNoList = StaticInfo.dao.executeQuerySQL(sql, null);
                if(sourceRefundOrderNoList!=null&&sourceRefundOrderNoList.size()>0)
                {
                    HelpTools.writelog_waimai(logBeginStr+"对应退订单号orderNo="+orderno+",已生成退订单，无须再生成");
                    return;
                }
                
                StringBuffer strBuff = new StringBuffer("");
                String execsql = "";
                //生成商品单身
                BigDecimal tot_qty = new BigDecimal("0");
                for (orderGoodsItem map : goodsList_partRefund)
                {
                    //生成商品单身
                    String pluno = map.getPluNo();
                    String item =  map.getItem();
                    String oItem = map.getoItem();
                    String packagemitem = map.getPackageMitem();
                    double qty = map.getQty();
                    tot_qty = tot_qty.add(new BigDecimal(qty));
                    
                    double price = map.getPrice();
                    double amt = map.getAmt();
                    double oldamt = map.getOldAmt();
                    double disc = map.getDisc();
                    double boxnum =  map.getBoxNum();
                    double boxprice =  map.getBoxPrice();
                    
                    
                    
                    
                    strBuff = new StringBuffer("");
                    strBuff.append(" insert into DCP_ORDER_DETAIL (");//分区字段
                    strBuff.append(" eid, orderno, item, loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, oitem, oreitem, pickqty, rqty, rcqty, shopqty, boxnum, boxprice, qty, oldprice, oldamt, price, disc, amt"
                            + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, stime, tran_time, RUNPICKQTY, VIRTUAL, DISC_MERRECEIVE, AMT_MERRECEIVE, DISC_CUSTPAYREAL, AMT_CUSTPAYREAL,PARTITION_DATE )");//分区字段已处理
                    
                    strBuff.append("select eid, '"+orderno+"', "+item+", loaddoctype, channelid, pluno, pluname, plubarcode, featureno, featurename, goodsurl, specname, attrname, sunit, sunitname, warehouse, warehousename, skuid, gift, giftsourceserialno, giftreason, goodsgroup, packagetype, packagemitem, toppingtype, toppingmitem, item, item, 0, 0, 0, shopqty, "+boxnum+", "+boxprice+", "+qty+", "+price+", "+oldamt+", "+price+", "+disc+", "+amt
                            + ", incltax, taxcode, taxtype, taxrate, invitem, invsplittype, invno, sellerno, sellername, accno, counterno, coupontype, couponcode, sourcecode, ismemo, '"+tran_time+"', '"+tran_time+"', "+qty+", VIRTUAL, 0, "+amt+", 0, "+amt+",'"+bdate+"'");//分区字段已处理
                    
                    strBuff.append(" from DCP_ORDER_DETAIL where qty>=0 and eid='"+eId+"' and orderno='"+sourceOrderNo+"'  and item="+oItem);
                    execsql = "";
                    execsql = strBuff.toString();
                    ExecBean exSale_detail = new ExecBean(execsql);
                    DataPB.add(new DataProcessBean(exSale_detail));
                    
                    if (map.getAgioInfo()!=null&&map.getAgioInfo().isEmpty()==false)
                    {
                        for (orderGoodsItemAgio agio : map.getAgioInfo())
                        {
                            strBuff = new StringBuffer("");
                            strBuff.append(" insert into dcp_order_detail_agio (");//分区字段
                            strBuff.append(" EID, ORDERNO, MITEM, ITEM, QTY, AMT, INPUTDISC, REALDISC, DISC, DCTYPE, DCTYPENAME, PMTNO,DISC_MERRECEIVE, DISC_CUSTPAYREAL,PARTITION_DATE)");//分区字段已处理
                            strBuff.append(" values ('"+dcpOrder.geteId()+"', '"+orderno+"', '"+item+"', "+agio.getItem()+", "+agio.getQty()+", "+agio.getAmt()+",0,0, "+agio.getDisc()+",'"+agio.getDcType()+"', '"+agio.getDcTypeName()+"', '',0,0,'"+bdate+"')");//分区字段已处理
                            
                            String execsql_agio = strBuff.toString();
                            
                            ExecBean exSale_detail_agio = new ExecBean(execsql_agio);
                            DataPB.add(new DataProcessBean(exSale_detail_agio));
                        }
                    }
                    
                    
                }
                
                
                //生成单头语句
                strBuff = new StringBuffer("");
                strBuff.append(" insert into DCP_ORDER (");//分区字段
                strBuff.append(" eid, billtype, orderno, requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                        + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, tot_qty, tot_oldamt, erase_amt, tot_disc, tot_amt, tot_uamt, payamt, writeoffamt, refundamt"
                        + ", packagefee, totshipfee, rshipfee, shipfee, shopshareshipfee, servicecharge, incomeamt, seller_disc, platform_disc, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                        + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, refundsourcebillno, refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                        + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, status, refundstatus, paystatus, productstatus"
                        + ", stime, create_datetime, complete_datetime, bdate, tran_time, update_time, process_status, peopletype, printcount, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, ORDERTOSALE_DATETIME, TOT_AMT_MERRECEIVE, TOT_AMT_CUSTPAYREAL, TOT_DISC_MERRECEIVE, TOT_DISC_CUSTPAYREAL,DOWNGRADED,WAIMAIMERRECEIVEMODE,PARTITION_DATE)");
                
                strBuff.append("select eid, '"+billtype+"', '"+orderno+"', requestid, manualno, loaddoctype, channelid, loaddocbilltype, loaddocorderno, outdoctype, outdoctypename, ordershop, ordershopname, order_sn, booknotify_status, machine, ver_num, squadno, workno, opno, isorgorder, isshipcompany, ischargeorder, sellcredit, customer, customername, isbook, shop, shopname, machshop, machshopname, shippingshop, shippingshopname"
                        + ", latitude, longitude, belfirm, contman, conttel, getman, getmantel, getmanemail, province, city, county, street, address, zipcode, shiptype, shipdate, shipstarttime, shipendtime, deliverytype, deliveryno, deliverystatus, subdeliverycompanyno, subdeliverycompanyname, delname, deltelephone, "+tot_qty+", "+tot_oldamt+", 0, "+tot_disc+", "+tot_amt+", tot_uamt, "+tot_amt+", writeoffamt, 0"
                        + ", 0, 0, 0, 0, 0, 0, "+tot_amt+", 0, 0, passport, freecode, buyerguino, carriercode, carriershowid, carrierhiddenid, lovecode, isinvoice, invoicetype, invoicetitle, taxregnumber, invmemo, invoicedate, invoperatetype, rebateno, invsplittype, invno, mealnumber, cardno, memberid, membername, pointqty, memberpayno"
                        + ", sellno, eccustomerno, currencyno, memo, promemo, delmemo, detailtype, headorderno, '"+sourceOrderNo+"', refundreasonno, refundreasonname, refundreason, returnsn, returnusername, returnemail, returnimageurl, exceptionstatus, exceptionmemo, yaohuono, virtualaccountcode, outselid, pickupdocprint, shopee_mode, shopee_address_id, shopee_pickup_time_id, shopee_branch_id, shopee_sender_real_name"
                        + ", greenworld_logisticsid, greenworld_merchanttradeno, greenworld_validno, greenworld_rtnlogisticsid, greenworld_rtnmerchanttradeno, greenworld_rtnvalidno, greenworld_rtnorderno, distanceno, distancename, receiver_fivecode, receiver_sevencode, packageno, packagename, measureno, measurename, temperatelayerno, temperatelayername, weight, '12', '6', paystatus, productstatus"
                        + ", '"+tran_time+"', '"+tran_time+"', '', '"+bdate+"', '"+tran_time+"', '"+update_time+"', 'N', peopletype, 0, autodelivery, deliverybusinesstype, isurgentorder, ISAPPORTION, '', "+tot_amt+", "+tot_amt+", 0, 0,DOWNGRADED,WAIMAIMERRECEIVEMODE,'"+bdate+"'");
                
                strBuff.append(" from DCP_ORDER where eid='"+eId+"'  and orderno='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale));
                
                
                
                //生成商品单身折扣
                
                
                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into Dcp_Order_Pay_Detail (");//分区字段
                strBuff.append(" eid, billno, item, billdate, bdate, sourcebilltype, sourcebillno, loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, pay, paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime, tran_time,SOURCEHEADBILLNO"
                        + ",MERDISCOUNT, MERRECEIVE, THIRDDISCOUNT, CUSTPAYREAL, COUPONMARKETPRICE, COUPONPRICE,PAYTYPE,PARTITION_DATE)");
                strBuff.append(" select eid, '"+billno+"', 1, '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', loaddoctype, channelid, paycode, paycodeerp, payname, order_paycode, isonlinepay, "+pay+", paydiscamt, payamt1, payamt2, descore, cttype, cardno, cardbeforeamt, cardremainamt, couponqty, isverification, extra, changed, paysernum, serialno, refno, teriminalno, caninvoice, writeoffamt, authcode, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"','"+sourceOrderNo+"'"
                        + ",0, "+pay+", 0, "+pay+", 0, 0,PAYTYPE,'"+bdate+"'");
                
                strBuff.append(" from Dcp_Order_Pay_Detail where rownum=1 and eid='"+eId+"' and SOURCEBILLNO='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay_detail = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay_detail));
                
                //生成付款单
                strBuff = new StringBuffer("");
                strBuff.append(" insert into Dcp_Order_pay (");//分区字段
                strBuff.append("eid, billno, billdate, bdate, sourcebilltype, sourcebillno, companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, direction, payrealamt, writeoffamt, usetype, status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, tran_time, process_status,SOURCEHEADBILLNO,PARTITION_DATE)");
                strBuff.append(" select eid, '"+billno+"', '"+sdate+"', '"+bdate+"', sourcebilltype, '"+orderno+"', companyid, loaddoctype, shopid, channelid, machineid, customerno, squadno, workno, -direction, "+pay+", writeoffamt, '"+usetype+"', status, memo, createopid, createopname, createtime, lastmodiopid, lastmodiopname, lastmoditime, '"+tran_time+"', 'N', '"+sourceOrderNo+"','"+bdate+"'");
                
                strBuff.append(" from Dcp_Order_pay where rownum=1 and eid='"+eId+"'  and SOURCEBILLNO='"+sourceOrderNo+"' ");
                execsql = "";
                execsql = strBuff.toString();
                ExecBean exSale_pay = new ExecBean(execsql);
                DataPB.add(new DataProcessBean(exSale_pay));
                
                //写下历程
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(dcpOrder.getChannelId());
                onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
                onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
                onelv1.seteId(eId);
                onelv1.setOpName("");
                onelv1.setOpNo("");
                onelv1.setShopNo(dcpOrder.getShopNo());
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo(dcpOrder.getMachShopNo());
                onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
                String statusType = "";
                String updateStaus = "99";//其他
                statusType = "99";// 订单状态
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = "其他";
                String statusTypeName = "其他状态";
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                String memo = "生成部分退订单成功";
                
                onelv1.setMemo(memo);
                onelv1.setDisplay("0");
                
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
            }
            
            
            StaticInfo.dao.useTransactionProcessData(DataPB);
            
            HelpTools.writelog_waimai(logBeginStr+"处理成功！");
            
            
            
            // 写订单日志
            if(orderStatusLogList.size()>0)
            {
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai("【写表dcp_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                            "【写表dcp_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNo:" + orderNo);
                }
            }
            //endregion
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            HelpTools.writelog_waimai(logBeginStr+"处理异常:"+e.getMessage());
        }
        
    }
    
    
    /**
     * 外卖折扣分摊处理
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void waimaiOrderDiscShareProcess(order dcpOrder,  StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【第三方外卖折扣分摊处理】单号orderNo="+orderNo+",";
        
        if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
        {
        
        
        }
        else
        {
            errorMessage.append("渠道类型="+loadDocType+"无须处理折扣分摊！ 单号orderNo="+orderNo);
            return;
            
        }
        
        
        int scaleCount = 2;//默认小数位
        String curWaiMaiMerReceiveMode = "1";//0-商品金额+打包盒-商户优惠，1-外卖平台店铺收入，
        try
        {
            writelog_waimai(logBeginStr+"分摊开始");
            if (WaiMaiMerReceiveMode==null||WaiMaiMerReceiveMode.isEmpty())
            {
                WaiMaiMerReceiveMode = PosPub.getPARA_SMS(StaticInfo.dao,eId,"","WaiMaiMerReceiveMode");
            }
            //嘉华的数据库，提前把参数加到数据库，参数值设置为0，其他客户均默认参数值=1的逻辑
            if (WaiMaiMerReceiveMode!=null&&"0".equals(WaiMaiMerReceiveMode))
            {
                curWaiMaiMerReceiveMode = "0";
            }
            dcpOrder.setWaiMaiMerReceiveMode(curWaiMaiMerReceiveMode);
            
            writelog_waimai(logBeginStr+"商户实收参数WaiMaiMerReceiveMode="+curWaiMaiMerReceiveMode);
            ParseJson pj =new ParseJson();
            writelog_waimai(logBeginStr + "分摊前json:" + pj.beanToJson(dcpOrder));
            BigDecimal tot_oldAmt = new BigDecimal(dcpOrder.getTot_oldAmt()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//订单原价
            BigDecimal tot_disc = new BigDecimal(dcpOrder.getTotDisc()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//订单总折扣
            //tot_amt = tot_oldAmt - tot_disc //订单金额=顾客实付
            BigDecimal tot_Amt = new BigDecimal(dcpOrder.getTot_Amt()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//订单金额=顾客实付金额
            writelog_waimai(logBeginStr+"订单原价tot_oldAmt="+tot_oldAmt+",订单金额（顾客实付）tot_Amt="+tot_Amt+",订单总优惠tot_disc="+tot_disc+",公式： 订单金额 tot_Amt = tot_oldAmt - tot_disc");
            BigDecimal packageFee =  new BigDecimal(dcpOrder.getPackageFee()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//餐盒费
            BigDecimal shipFee =  new BigDecimal(dcpOrder.getShipFee()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//实际配送费
            //商家替用户承担得配送费，饿了么由于不算这个金额到订单总价，所以单独记录，需要扣除
            BigDecimal shopShareShipfee = new BigDecimal(dcpOrder.getShopShareShipfee()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//商家替用户承担得配送费，饿了么才有
            
            BigDecimal tot_oldAmt_goods = tot_oldAmt.subtract(packageFee).subtract(shipFee).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);// 原商品总价
            
            writelog_waimai(logBeginStr+"订单原价商品总金额tot_oldAmt_goods="+tot_oldAmt_goods+",餐盒费packageFee="+packageFee+",配送费shipFee="+shipFee+",公式 ：订单原价tot_oldAmt = tot_oldAmt_goods + packageFee + shipFee");
            
            BigDecimal sellerDisc = new BigDecimal(dcpOrder.getSellerDisc()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//商家承担折扣
            BigDecimal platformDisc = new BigDecimal(dcpOrder.getPlatformDisc()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//平台承担折扣
            //tot_disc = sellerDisc+ platformDisc //订单总优惠=商家优惠+平台优惠
            writelog_waimai(logBeginStr+"订单总优惠tot_disc="+tot_disc+",商家优惠sellerDisc="+sellerDisc+",平台优惠platformDisc="+platformDisc+",公式 ：订单总优惠tot_disc = sellerDisc + platformDisc");
            BigDecimal tot_Amt_merReceive = new BigDecimal("0");//嘉华需要的商家实收
            tot_Amt_merReceive = tot_oldAmt_goods.subtract(sellerDisc).subtract(shopShareShipfee).add(packageFee).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);
            if(dcpOrder.getIsMerPay()!=null&&dcpOrder.getIsMerPay().equals("Y"))
            {
                writelog_waimai(logBeginStr+"配送费商家结算isMerPay="+dcpOrder.getIsMerPay()+",商家实收需要加上配送费");
                tot_Amt_merReceive = tot_Amt_merReceive.add(shipFee).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);
            }
            BigDecimal incomeAmt = new BigDecimal(dcpOrder.getIncomeAmt()).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//店铺收入
            BigDecimal totDisc_merReceive = sellerDisc.add(shopShareShipfee).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//嘉华实收对应的总折扣
            if ("1".equals(curWaiMaiMerReceiveMode))
            {
                tot_Amt_merReceive = incomeAmt;
                totDisc_merReceive = tot_oldAmt_goods.subtract(incomeAmt).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);//店铺收入对应的总折扣
                writelog_waimai(logBeginStr+"订单店铺收入incomeAmt="+incomeAmt+",店铺收入对应的总折扣totDisc_merReceive="+totDisc_merReceive+"");
            }
            
            dcpOrder.setTot_Amt_merReceive(tot_Amt_merReceive.doubleValue());
            dcpOrder.setTotDisc_merReceive(totDisc_merReceive.doubleValue());
            
            BigDecimal tot_Amt_custPayReal = tot_Amt;//顾客实付
            BigDecimal totDisc_custPayReal = tot_disc;//顾客实付对应的总折扣
            dcpOrder.setTot_Amt_custPayReal(tot_Amt_custPayReal.doubleValue());
            dcpOrder.setTotDisc_custPayReal(totDisc_custPayReal.doubleValue());
            
            if ("1".equals(curWaiMaiMerReceiveMode))
            {
                /*totDisc_custPayReal = tot_oldAmt_goods.subtract(tot_Amt).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);;//顾客实付对应的总折扣
                dcpOrder.setTotDisc_custPayReal(totDisc_custPayReal.doubleValue());*/
                writelog_waimai(logBeginStr+"商户实收(外卖平台店店铺收入)tot_Amt_merReceive="+tot_Amt_merReceive+",其他对账顾客实付tot_Amt_custPayReal="+tot_Amt_custPayReal);
            }
            else
            {
                writelog_waimai(logBeginStr+"嘉华对账需要得商户实收(原商品总价-商家优惠-商家替用户承担配送费[饿了么特有])tot_Amt_merReceive="+tot_Amt_merReceive+",其他对账顾客实付tot_Amt_custPayReal="+tot_Amt_custPayReal);
            }
            
            
            
            if(dcpOrder.getPay()!=null)
            {
                for (orderPay payMent : dcpOrder.getPay())
                {
                    /*if ("1".equals(curWaiMaiMerReceiveMode))
                    {
                        payMent.setMerReceive(tot_Amt_merReceive.doubleValue());//店铺实收
                        payMent.setCustPayReal(tot_Amt_custPayReal.doubleValue());//顾客实付
                        payMent.setMerDiscount(dcpOrder.getTotDisc_merReceive());//商家折扣
                        payMent.setThirdDiscount(dcpOrder.getPlatformDisc());//
                        //订转销的时候 分摊商户实收 是合计 pay的MerDiscount+ThirdDiscount
                        payMent.setThirdDiscount(dcpOrder.getTotDisc_custPayReal()-dcpOrder.getTotDisc_merReceive());
                    }
                    else
                    {
                        payMent.setMerReceive(tot_Amt_merReceive.doubleValue());//嘉华商家实收
                        payMent.setCustPayReal(tot_Amt_custPayReal.doubleValue());//顾客实付
                        payMent.setMerDiscount(dcpOrder.getSellerDisc());//商家折扣
                        payMent.setThirdDiscount(dcpOrder.getPlatformDisc());//第三方折扣
                    }*/
                    payMent.setMerReceive(tot_Amt_merReceive.doubleValue());//嘉华商家实收
                    payMent.setCustPayReal(tot_Amt_custPayReal.doubleValue());//顾客实付
                    payMent.setMerDiscount(totDisc_merReceive.doubleValue());//商家折扣
                    payMent.setThirdDiscount(dcpOrder.getPlatformDisc());//第三方折扣
                    
                    break;
                }
            }
            if("0".equals(curWaiMaiMerReceiveMode)&&Math.abs(dcpOrder.getSellerDisc())<0.01&&Math.abs(dcpOrder.getTotDisc())<0.01)
            {
                for (orderGoodsItem orderGoodsItem : dcpOrder.getGoodsList())
                {
                    orderGoodsItem.setAmt_merReceive(orderGoodsItem.getAmt());
                    orderGoodsItem.setAmt_custPayReal(orderGoodsItem.getAmt());
                }
                writelog_waimai(logBeginStr+"没有折扣，无须分摊");
                return;//没有折扣，不用分摊
            }
            
            List<orderGoodsItem> goodsItem_NoPackage = new ArrayList<orderGoodsItem>();//不包含套餐子商品
            List<orderGoodsItem> goodsItem_Package = new ArrayList<orderGoodsItem>();//只有套餐子商品
            for (orderGoodsItem orderGoodsItem : dcpOrder.getGoodsList())
            {
                if(orderGoodsItem.getAmt()<0.01)
                {
                    continue;//金额为0过滤
                }
                //1、正常商品 2、套餐主商品  3、套餐子商品
                if(orderGoodsItem.getPackageType()!=null&&orderGoodsItem.getPackageType().equals("3"))
                {
                    goodsItem_Package.add(orderGoodsItem);
                }
                else
                {
                    goodsItem_NoPackage.add(orderGoodsItem);
                }
                
            }
            String dcType = "60";
            String dcTypeName = "支付折扣";
            
            writelog_waimai(logBeginStr+"分摊开始【先分摊主商品】");
            //分摊主商品折扣
            BigDecimal disc_merReceive_add = new BigDecimal("0");
            BigDecimal disc_custPayReal_add = new BigDecimal("0");
            
            for(int i=0;i<goodsItem_NoPackage.size();i++)
            {
                BigDecimal curDisc_merReceive = new BigDecimal("0");
                BigDecimal curDisc_custPayReal = new BigDecimal("0");
                //最后一个
                if(i==goodsItem_NoPackage.size()-1)
                {
                    curDisc_merReceive = totDisc_merReceive.subtract(disc_merReceive_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                    curDisc_custPayReal = totDisc_custPayReal.subtract(disc_custPayReal_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                    
                }
                else
                {
                    //折扣分摊下 安金额比例
                    BigDecimal amtRate  = new BigDecimal("0");
                    BigDecimal curAmt = new BigDecimal(goodsItem_NoPackage.get(i).getAmt());
                    try
                    {
                        amtRate = curAmt.divide(tot_oldAmt_goods,4, BigDecimal.ROUND_HALF_UP);
                    } catch (Exception e)
                    {
                    
                    }
                    curDisc_merReceive = amtRate.multiply(totDisc_merReceive).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);
                    curDisc_custPayReal = amtRate.multiply(totDisc_custPayReal).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);
                    disc_merReceive_add = disc_merReceive_add.add(curDisc_merReceive);
                    disc_custPayReal_add = disc_custPayReal_add.add(curDisc_custPayReal);
                }
                
                //添加商品折扣
                goodsItem_NoPackage.get(i).setDisc_merReceive(curDisc_merReceive.doubleValue());
                goodsItem_NoPackage.get(i).setDisc_custPayReal(curDisc_custPayReal.doubleValue());
                
                goodsItem_NoPackage.get(i).setAmt_merReceive(goodsItem_NoPackage.get(i).getAmt()-goodsItem_NoPackage.get(i).getDisc_merReceive());
                goodsItem_NoPackage.get(i).setAmt_custPayReal(goodsItem_NoPackage.get(i).getAmt()-goodsItem_NoPackage.get(i).getDisc_custPayReal());
                
                
                //判断下是不是套餐主商品，套餐主商品不挂折扣
                if(goodsItem_NoPackage.get(i).getPackageType()!=null&&goodsItem_NoPackage.get(i).getPackageType().equals("2"))
                {
                    //如果是套餐如果主商品，子商品也要分摊
                    List<orderGoodsItem> goodsItem_Package_MItem = new ArrayList<orderGoodsItem>();//当前主商品对应得套餐子商品
                    String curItem = goodsItem_NoPackage.get(i).getItem();
                    for (orderGoodsItem packageItem : goodsItem_Package)
                    {
                        if(packageItem.getPackageMitem()!=null&&packageItem.getPackageMitem().equals(curItem))
                        {
                            goodsItem_Package_MItem.add(packageItem);
                        }
                    }
                    writelog_waimai(logBeginStr+"分摊开始【存在套餐商品】【分摊套餐主商品下对应得子商品】");
                    //分摊主商品折扣
                    BigDecimal package_disc_merReceive_add = new BigDecimal("0");
                    BigDecimal package_disc_custPayReal_add = new BigDecimal("0");
                    for(int j =0 ;j<goodsItem_Package_MItem.size();j++)
                    {
                        BigDecimal package_curDisc_merReceive = new BigDecimal("0");
                        BigDecimal package_curDisc_custPayReal = new BigDecimal("0");
                        //最后一个
                        if(j==goodsItem_Package_MItem.size()-1)
                        {
                            package_curDisc_merReceive = curDisc_merReceive.subtract(package_disc_merReceive_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            package_curDisc_custPayReal = curDisc_custPayReal.subtract(package_disc_custPayReal_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            
                        }
                        else
                        {
                            //折扣分摊下 安金额比例
                            BigDecimal amtRate  = new BigDecimal("0");
                            
                            try
                            {
                                amtRate = new BigDecimal(goodsItem_Package_MItem.get(j).getAmt()/goodsItem_NoPackage.get(i).getAmt());
                            } catch (Exception e)
                            {
                                // TODO: handle exception
                            }
                            package_curDisc_merReceive = amtRate.multiply(curDisc_merReceive).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);
                            package_curDisc_custPayReal = amtRate.multiply(curDisc_custPayReal).setScale(scaleCount,BigDecimal.ROUND_HALF_UP);
                            package_disc_merReceive_add = package_disc_merReceive_add.add(package_curDisc_merReceive);
                            package_disc_custPayReal_add = package_disc_custPayReal_add.add(package_curDisc_custPayReal);
                        }
                        
                        orderGoodsItemAgio agio_package = new orderGoodsItemAgio();
                        agio_package.setDcType(dcType);
                        agio_package.setDcTypeName(dcTypeName);
                        agio_package.setAmt(goodsItem_Package_MItem.get(j).getAmt());
                        agio_package.setDisc(0);//原始账
                        agio_package.setDisc_merReceive(package_curDisc_merReceive.doubleValue());//嘉华实收
                        agio_package.setDisc_custPayReal(package_curDisc_custPayReal.doubleValue());//顾客实付
                        agio_package.setQty(goodsItem_Package_MItem.get(j).getQty());
                        agio_package.setItem("1");
                        
                        List<orderGoodsItemAgio> agioList_package = new ArrayList<orderGoodsItemAgio>();
                        agioList_package.add(agio_package);
                        
                        goodsItem_Package_MItem.get(j).setAgioInfo(agioList_package);
                        goodsItem_Package_MItem.get(j).setDisc_merReceive(package_curDisc_merReceive.doubleValue());
                        goodsItem_Package_MItem.get(j).setDisc_custPayReal(package_curDisc_custPayReal.doubleValue());
                        
                        goodsItem_Package_MItem.get(j).setAmt_merReceive(goodsItem_Package_MItem.get(j).getAmt()-goodsItem_Package_MItem.get(j).getDisc_merReceive());
                        goodsItem_Package_MItem.get(j).setAmt_custPayReal(goodsItem_Package_MItem.get(j).getAmt()-goodsItem_Package_MItem.get(j).getDisc_custPayReal());
                        
                        
                    }
                    
                }
                else
                {
                    //非套餐主商品挂折扣
                    orderGoodsItemAgio agio = new orderGoodsItemAgio();
                    agio.setDcType(dcType);
                    agio.setDcTypeName(dcTypeName);
                    agio.setAmt(goodsItem_NoPackage.get(i).getAmt());
                    agio.setDisc(0);//原始账
                    agio.setDisc_merReceive(curDisc_merReceive.doubleValue());//嘉华实收
                    agio.setDisc_custPayReal(curDisc_custPayReal.doubleValue());//顾客实付
                    agio.setQty(goodsItem_NoPackage.get(i).getQty());
                    agio.setItem("1");
                    
                    List<orderGoodsItemAgio> agioList = new ArrayList<orderGoodsItemAgio>();
                    agioList.add(agio);
                    
                    goodsItem_NoPackage.get(i).setAgioInfo(agioList);
					/*goodsItem_NoPackage.get(i).setDisc_merReceive(curDisc_merReceive.doubleValue());
					goodsItem_NoPackage.get(i).setDisc_custPayReal(curDisc_custPayReal.doubleValue());

					goodsItem_NoPackage.get(i).setAmt_merReceive(goodsItem_NoPackage.get(i).getAmt()-goodsItem_NoPackage.get(i).getDisc_merReceive());
					goodsItem_NoPackage.get(i).setAmt_custPayReal(goodsItem_NoPackage.get(i).getAmt()-goodsItem_NoPackage.get(i).getDisc_custPayReal());*/
                
                
                }
                
            }
            writelog_waimai(logBeginStr+"分摊完成！");
            
            writelog_waimai(logBeginStr+"分摊后json:"+pj.beanToJson(dcpOrder));
            
            
            
        }
        catch (Exception e)
        {
            writelog_waimai(logBeginStr+"处理异常:"+e.getMessage());
        }
        
        
        
        
    }
    
    
    
    
    /**
     * pos订单付款折扣分摊处理
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void posOrderPayDiscShareProcess(order dcpOrder,  StringBuffer errorMessage) throws Exception
    {
        if (errorMessage == null)
        {
            errorMessage = new StringBuffer();
        }
        if (dcpOrder == null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【订单支付折扣分摊处理】单号orderNo=" + orderNo + ",";
        
        if (dcpOrder.getPay() == null || dcpOrder.getPay().isEmpty())
        {
            return;
        }
        
        if (loadDocType.equals(orderLoadDocType.POS) || loadDocType.equals(orderLoadDocType.POSANDROID))
        {
        
        } else
        {
            errorMessage.append("渠道类型=" + loadDocType + "无须处理付款折扣分摊！ 单号orderNo=" + orderNo);
            return;
        }
        
        /*****************【ID1026268】【詹记3.0】大众点评券支付时商户实收金额错误-DCP服务****************/
        //region 渠道POS、POSANDROID商户实收金额重新修改 ----2022-06-14、版本V3.0.1.4
        try
        {
            String payTypeSqlCondition ="";
            for (orderPay payMent : dcpOrder.getPay())
            {
                String payType = payMent.getPayType();
                if (payType!=null&&!payType.isEmpty())
                {
                    payTypeSqlCondition ="'"+payType+"',"+payTypeSqlCondition;
                }
            }
            payTypeSqlCondition = payTypeSqlCondition.substring(0,payTypeSqlCondition.length()-1);
            String sql_payType = " select * from DCP_PAYTYPE where EID='"+eId+"' and PAYTYPE in ("+payTypeSqlCondition+")";
            writelog_waimai("【检测pos订单收款方式】【是否纳入营业额】根据payType查询sql="+sql_payType+"，单号orderNO="+orderNo);
            List<Map<String, Object>> getPayTypeData = StaticInfo.dao.executeQuerySQL(sql_payType, null);
            List<String> payType_no_isturnover = new ArrayList<String>();//不纳入营业统计的payType列表
            if (getPayTypeData!=null&&!getPayTypeData.isEmpty())
            {
                for (Map<String, Object> map_payType : getPayTypeData)
                {
                    //ISTURNOVER 是否纳入营业额统计   0：不可 1：可
                    String isturnover = map_payType.getOrDefault("ISTURNOVER","").toString();
                    
                    if ("0".equals(isturnover))
                    {
                        payType_no_isturnover.add(map_payType.getOrDefault("PAYTYPE","").toString());
                    }
                }
                
            }
            for (orderPay payInfo : dcpOrder.getPay())
            {
                String payType = payInfo.getPayType();
                String funcNo = payInfo.getFuncNo();
                
                BigDecimal pay = new BigDecimal("0");
                try
                {
                    pay = new BigDecimal(payInfo.getPay());
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                BigDecimal changed = new BigDecimal("0");
                try
                {
                    changed = new BigDecimal(payInfo.getChanged());
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                BigDecimal extra = new BigDecimal("0");
                try
                {
                    extra = new BigDecimal(payInfo.getExtra());
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
                
                //region 如果收款方式不纳入营业额，则商家优惠金额=pay-changed,平台优惠=0,商家实收=0，顾客实付=0；
                if (payType!=null&&!payType.isEmpty())
                {
                    if (payType_no_isturnover.contains(payType))
                    {
                        payInfo.setMerDiscount(pay.subtract(changed).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        payInfo.setThirdDiscount(0);
                        payInfo.setMerReceive(0);
                        payInfo.setCustPayReal(0);
                        writelog_waimai("【pos订单收款方式】【不纳入营业额】payType="+payType+",项次item='"+payInfo.getItem()+",单号orderNO="+orderNo);
                        continue;
                    }
                }
                //endregion
                
                //region funcno =商家自己发行的电子券或纸质券 304 307 308
                //商户优惠=溢收*-1、平台优惠=0、商户实收=pay-changed、顾客实付=pay-changed
                if ("304".equals(funcNo)||"307".equals(funcNo)||"308".equals(funcNo))
                {
                    payInfo.setMerDiscount(new BigDecimal(-1).multiply(extra).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    payInfo.setThirdDiscount(0);
                    payInfo.setMerReceive(pay.subtract(changed).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    payInfo.setCustPayReal(pay.subtract(changed).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    continue;
                }
                //endregion

                //region 其他的逻辑，商户优惠=入参【默认0】、平台优惠=入参【默认0】、商户实收=pay-changed-商户优惠、顾客实付=pay-changed-商户优惠-平台优惠
                if(Math.abs(payInfo.getMerDiscount())<0.01)
                {
                    //商户优惠=溢收*-1
                    payInfo.setMerDiscount(new BigDecimal(-1).multiply(extra).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    payInfo.setMerReceive(pay.subtract(changed).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                else
                {
                    //【ID1039717】【大万-3.0】dcp_order_detail表商家实收金额问题
                    //美团券、抖音券这种，如果存在溢收以及商户实收折扣，POS传入的商户实收肯定是正确的，反算商户实收折扣
                    BigDecimal merReceive_b = new BigDecimal(payInfo.getMerReceive());
                    if (merReceive_b.compareTo(BigDecimal.ZERO)==0||extra.compareTo(BigDecimal.ZERO)==0)
                    {
                        //防呆设置，因为本来逻辑是根据商户折扣反算商户实收的
                        //现在根据商户实收反算商户实收折扣，所以防止异常
                    }
                    else
                    {
                        double old_MerDiscount = payInfo.getMerDiscount();
                        //根据公式：商户实收=PAY-溢收-商户实收折扣
                        //反算：商户实收折扣merDiscount=pay-extra-merReceive
                        payInfo.setMerDiscount(pay.subtract(extra).subtract(merReceive_b).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                        writelog_waimai("【pos订单收款方式】【美团券、抖音券传入了商户实收和溢收，改写商户实收折扣】payType="+payType+",项次item='"+payInfo.getItem()+",原商户实收折扣merDiscount="+old_MerDiscount+",修改后="+payInfo.getMerDiscount()+",单号orderNO="+orderNo);
                    }

                }
                if(Math.abs(payInfo.getThirdDiscount())<0.01)
                {
                    payInfo.setThirdDiscount(0);
                    payInfo.setCustPayReal(pay.subtract(changed).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                //endregion
                
            }
            
        }
        catch (Exception e)
        {
        
        }
        //endregion
        
        int scaleCount = 2;// 默认小数位
        try
        {
            writelog_waimai(logBeginStr + "分摊开始");
            ParseJson pj = new ParseJson();
            writelog_waimai(logBeginStr + "分摊前json:" + pj.beanToJson(dcpOrder));
            String dcType = "60";
            String dcTypeName = "支付折扣";
            
            BigDecimal tot_oldAmt = new BigDecimal(dcpOrder.getTot_oldAmt()).setScale(scaleCount,
                    BigDecimal.ROUND_HALF_UP);// 订单原价
            BigDecimal tot_disc = new BigDecimal(dcpOrder.getTotDisc()).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);// 订单总折扣
            BigDecimal tot_Amt = new BigDecimal(dcpOrder.getTot_Amt()).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);// 商品单身金额合计
            
            
            BigDecimal totDisc_merReceive_add = new BigDecimal("0");
            BigDecimal totDisc_custPayReal_add = new BigDecimal("0");
            for (orderPay payMent : dcpOrder.getPay())
            {
                
                if (Math.abs(payMent.getMerDiscount()) < 0.01 && Math.abs(payMent.getThirdDiscount()) < 0.01)
                {
                    writelog_waimai(logBeginStr + "pos支付方式payType=" + payMent.getPayType() + ",没有折扣，无须分摊");
                    continue;// 没有折扣，不用分摊
                }
                
                BigDecimal totDisc_merReceive = new BigDecimal(payMent.getMerDiscount()).setScale(scaleCount,
                        BigDecimal.ROUND_HALF_UP);// 商家折扣
                BigDecimal totDisc_thirdDiscount = new BigDecimal(payMent.getThirdDiscount()).setScale(scaleCount,
                        BigDecimal.ROUND_HALF_UP);// 第三方折扣
                BigDecimal totDisc_custPayReal = totDisc_merReceive.add(totDisc_thirdDiscount).setScale(scaleCount,
                        BigDecimal.ROUND_HALF_UP);// 总折扣
                
                totDisc_merReceive_add = totDisc_merReceive_add.add(totDisc_merReceive).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                totDisc_custPayReal_add = totDisc_custPayReal_add.add(totDisc_custPayReal).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                
                writelog_waimai(logBeginStr + "pos支付方式payType=" + payMent.getPayType() + ",商家折扣totDisc_merReceive="
                        + totDisc_merReceive + ",第三方折扣totDisc_thirdDiscount=" + totDisc_thirdDiscount
                        + ",总折扣（顾客实付折扣）totDisc_custPayReal=" + totDisc_custPayReal+",分摊开始");
                
                List<orderGoodsItem> goodsItem_NoPackage = new ArrayList<orderGoodsItem>();// 不包含套餐子商品
                List<orderGoodsItem> goodsItem_Package = new ArrayList<orderGoodsItem>();// 只有套餐子商品
                
                for (orderGoodsItem orderGoodsItem : dcpOrder.getGoodsList())
                {
                    if (orderGoodsItem.getAmt() < 0.01)
                    {
                        continue;// 金额为0过滤
                    }
                    // 1、正常商品 2、套餐主商品 3、套餐子商品
                    if (orderGoodsItem.getPackageType() != null && orderGoodsItem.getPackageType().equals("3"))
                    {
                        goodsItem_Package.add(orderGoodsItem);
                    } else
                    {
                        goodsItem_NoPackage.add(orderGoodsItem);
                    }
                    
                }
                
                // 分摊主商品折扣
                BigDecimal disc_merReceive_add = new BigDecimal("0");
                BigDecimal disc_custPayReal_add = new BigDecimal("0");
                for (int i = 0; i < goodsItem_NoPackage.size(); i++)
                {
                    BigDecimal curDisc_merReceive = new BigDecimal("0");
                    BigDecimal curDisc_custPayReal = new BigDecimal("0");
                    // 最后一个
                    if (i == goodsItem_NoPackage.size() - 1)
                    {
                        curDisc_merReceive = totDisc_merReceive.subtract(disc_merReceive_add).setScale(scaleCount,
                                BigDecimal.ROUND_HALF_UP);
                        curDisc_custPayReal = totDisc_custPayReal.subtract(disc_custPayReal_add).setScale(scaleCount,
                                BigDecimal.ROUND_HALF_UP);
                    } else
                    {
                        // 折扣分摊下 安金额比例
                        BigDecimal amtRate = new BigDecimal("0");
                        BigDecimal curAmt = new BigDecimal(goodsItem_NoPackage.get(i).getAmt());
                        try
                        {
                            amtRate = curAmt.divide(tot_Amt, 4, BigDecimal.ROUND_HALF_UP);
                        } catch (Exception e)
                        {
                        
                        }
                        curDisc_merReceive = amtRate.multiply(totDisc_merReceive).setScale(scaleCount,
                                BigDecimal.ROUND_HALF_UP);
                        curDisc_custPayReal = amtRate.multiply(totDisc_custPayReal).setScale(scaleCount,
                                BigDecimal.ROUND_HALF_UP);
                        disc_merReceive_add = disc_merReceive_add.add(curDisc_merReceive);
                        disc_custPayReal_add = disc_custPayReal_add.add(curDisc_custPayReal);
                        
                    }
                    
                    // 商品折扣合计 。循环分摊的要加上前一次的折扣
                    BigDecimal oldDisc_merReceive = new BigDecimal(goodsItem_NoPackage.get(i).getDisc_merReceive());
                    BigDecimal oldDisc_custPayReal = new BigDecimal(goodsItem_NoPackage.get(i).getDisc_custPayReal());
                    
                    goodsItem_NoPackage.get(i).setDisc_merReceive(curDisc_merReceive.add(oldDisc_merReceive)
                            .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                    goodsItem_NoPackage.get(i).setDisc_custPayReal(curDisc_custPayReal.add(oldDisc_custPayReal)
                            .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                    
                    goodsItem_NoPackage.get(i).setAmt_merReceive(
                            goodsItem_NoPackage.get(i).getAmt() - goodsItem_NoPackage.get(i).getDisc_merReceive());
                    goodsItem_NoPackage.get(i).setAmt_custPayReal(
                            goodsItem_NoPackage.get(i).getAmt() - goodsItem_NoPackage.get(i).getDisc_custPayReal());
                    
                    // 套餐主商品不挂折扣档，挂到子商品下面
                    if (goodsItem_NoPackage.get(i).getPackageType() != null
                            && goodsItem_NoPackage.get(i).getPackageType().equals("2"))
                    {
                        // 套餐主商品不挂折扣档，挂到子商品下面
                        List<orderGoodsItem> goodsItem_Package_MItem = new ArrayList<orderGoodsItem>();// 当前主商品对应得套餐子商品
                        String curItem = goodsItem_NoPackage.get(i).getItem();
                        for (orderGoodsItem packageItem : goodsItem_Package)
                        {
                            if (packageItem.getPackageMitem() != null && packageItem.getPackageMitem().equals(curItem))
                            {
                                goodsItem_Package_MItem.add(packageItem);
                            }
                        }
                        writelog_waimai(logBeginStr + "分摊开始【存在套餐商品】【分摊套餐主商品下对应得子商品】");
                        // 分摊主商品折扣
                        BigDecimal package_disc_merReceive_add = new BigDecimal("0");
                        BigDecimal package_disc_custPayReal_add = new BigDecimal("0");
                        for (int j = 0; j < goodsItem_Package_MItem.size(); j++)
                        {
                            
                            BigDecimal package_curDisc_merReceive = new BigDecimal("0");
                            BigDecimal package_curDisc_custPayReal = new BigDecimal("0");
                            // 最后一个
                            if (j == goodsItem_Package_MItem.size() - 1)
                            {
                                package_curDisc_merReceive = curDisc_merReceive.subtract(package_disc_merReceive_add)
                                        .setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                                package_curDisc_custPayReal = curDisc_custPayReal.subtract(package_disc_custPayReal_add)
                                        .setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            } else
                            {
                                // 折扣分摊下 安金额比例
                                BigDecimal amtRate = new BigDecimal("0");
                                
                                try
                                {
                                    amtRate = new BigDecimal(goodsItem_Package_MItem.get(j).getAmt()
                                            / goodsItem_NoPackage.get(i).getAmt());
                                } catch (Exception e)
                                {
                                    // TODO: handle exception
                                }
                                package_curDisc_merReceive = amtRate.multiply(curDisc_merReceive).setScale(scaleCount,
                                        BigDecimal.ROUND_HALF_UP);
                                package_curDisc_custPayReal = amtRate.multiply(curDisc_custPayReal).setScale(scaleCount,
                                        BigDecimal.ROUND_HALF_UP);
                                package_disc_merReceive_add = package_disc_merReceive_add
                                        .add(package_curDisc_merReceive);
                                package_disc_custPayReal_add = package_disc_custPayReal_add
                                        .add(package_curDisc_custPayReal);
                                
                            }
                            
                            // 添加折扣档
                            if (goodsItem_Package_MItem.get(j).getAgioInfo() == null)
                            {
                                goodsItem_Package_MItem.get(j).setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                            }
                            
                            int agioItem = goodsItem_Package_MItem.get(j).getAgioInfo().size() + 1;
                            
                            orderGoodsItemAgio agio = new orderGoodsItemAgio();
                            agio.setDcType(dcType);
                            agio.setDcTypeName(dcTypeName);
                            agio.setAmt(goodsItem_Package_MItem.get(j).getAmt());
                            agio.setDisc(0);// 原始账
                            agio.setDisc_merReceive(package_curDisc_merReceive.doubleValue());// 实收
                            agio.setDisc_custPayReal(package_curDisc_custPayReal.doubleValue());// 顾客实付
                            agio.setQty(goodsItem_Package_MItem.get(j).getQty());
                            agio.setItem(agioItem + "");
                            
                            goodsItem_Package_MItem.get(j).getAgioInfo().add(agio);
                            
                            // 商品折扣合计 。循环分摊的所有要加上前一次的折扣
                            BigDecimal package_oldDisc_merReceive = new BigDecimal(
                                    goodsItem_Package_MItem.get(j).getDisc_merReceive());
                            BigDecimal package_oldDisc_custPayReal = new BigDecimal(
                                    goodsItem_Package_MItem.get(j).getDisc_custPayReal());
                            goodsItem_Package_MItem.get(j)
                                    .setDisc_merReceive(package_curDisc_merReceive.add(package_oldDisc_merReceive)
                                            .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                            goodsItem_Package_MItem.get(j)
                                    .setDisc_custPayReal(package_curDisc_custPayReal.add(package_oldDisc_custPayReal)
                                            .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                            
                            goodsItem_Package_MItem.get(j).setAmt_merReceive(goodsItem_Package_MItem.get(j).getAmt()
                                    - goodsItem_Package_MItem.get(j).getDisc_merReceive());
                            goodsItem_Package_MItem.get(j).setAmt_custPayReal(goodsItem_Package_MItem.get(j).getAmt()
                                    - goodsItem_Package_MItem.get(j).getDisc_custPayReal());
                            // goodsItem_Share.add(goodsItem_Package_MItem.get(j));
                            
                        }
                        
                    } else
                    {
                        
                        if (goodsItem_NoPackage.get(i).getAgioInfo() == null)
                        {
                            goodsItem_NoPackage.get(i).setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                        }
                        
                        int agioItem = goodsItem_NoPackage.get(i).getAgioInfo().size() + 1;
                        
                        orderGoodsItemAgio agio = new orderGoodsItemAgio();
                        agio.setDcType(dcType);
                        agio.setDcTypeName(dcTypeName);
                        agio.setAmt(goodsItem_NoPackage.get(i).getAmt());
                        agio.setDisc(0);// 原始账
                        agio.setDisc_merReceive(curDisc_merReceive.doubleValue());// 嘉华实收
                        agio.setDisc_custPayReal(curDisc_custPayReal.doubleValue());// 顾客实付
                        agio.setQty(goodsItem_NoPackage.get(i).getQty());
                        agio.setItem(agioItem + "");
                        
                        goodsItem_NoPackage.get(i).getAgioInfo().add(agio);
                        
                    }
                    
                    // goodsItem_Share.add(goodsItem_NoPackage.get(i));
                    
                }
                
            }
            
            dcpOrder.setTotDisc_merReceive(totDisc_merReceive_add.doubleValue());
            dcpOrder.setTotDisc_custPayReal(totDisc_custPayReal_add.doubleValue());
            dcpOrder.setTot_Amt_merReceive(tot_Amt.subtract(totDisc_merReceive_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
            dcpOrder.setTot_Amt_custPayReal(tot_Amt.subtract(totDisc_custPayReal_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
            
            writelog_waimai(logBeginStr + "分摊完成！");
            writelog_waimai(logBeginStr + "分摊后json:" + pj.beanToJson(dcpOrder));
            
        }
        
        catch (Exception e)
        {
            writelog_waimai(logBeginStr + "处理异常:" + e.getMessage());
        }
    }
    
    
    /**
     * 抹零金额分摊
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void posOrderEraseAmtShareProcess(order dcpOrder,  StringBuffer errorMessage) throws Exception
    {
        
        if (errorMessage == null)
        {
            errorMessage = new StringBuffer();
        }
        if (dcpOrder == null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【订单抹零金额分摊处理】单号orderNo=" + orderNo + ",";
        
        if (dcpOrder.getPay() == null || dcpOrder.getPay().isEmpty())
        {
            return;
        }
        
        if (loadDocType.equals(orderLoadDocType.POS) || loadDocType.equals(orderLoadDocType.POSANDROID))
        {
        
        } else
        {
            errorMessage.append("渠道类型=" + loadDocType + "无须抹零金额分摊！ 单号orderNo=" + orderNo);
            return;
        }
        
        
        if (Math.abs(dcpOrder.getEraseAmt()) < 0.01)
        {
            writelog_waimai(logBeginStr + "没有抹零金额，无须分摊");
            return;
        }
        
        
        
        
        
        int scaleCount = 2;// 默认小数位
        try
        {
            writelog_waimai(logBeginStr + "分摊开始,抹零金额eraseAmt="+dcpOrder.getEraseAmt());
            ParseJson pj = new ParseJson();
            writelog_waimai(logBeginStr + "分摊前json:" + pj.beanToJson(dcpOrder));
            String dcType = "60";
            String dcTypeName = "支付折扣";
            
            BigDecimal tot_oldAmt = new BigDecimal(dcpOrder.getTot_oldAmt()).setScale(scaleCount,
                    BigDecimal.ROUND_HALF_UP);// 订单原价
            BigDecimal tot_disc = new BigDecimal(dcpOrder.getTotDisc()).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);// 订单总折扣
            BigDecimal tot_Amt = new BigDecimal(dcpOrder.getTot_Amt()).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);// 商品单身金额合计
            
            
            
            
            List<orderGoodsItem> goodsItem_NoPackage = new ArrayList<orderGoodsItem>();// 不包含套餐子商品
            List<orderGoodsItem> goodsItem_Package = new ArrayList<orderGoodsItem>();// 只有套餐子商品
            
            for (orderGoodsItem orderGoodsItem : dcpOrder.getGoodsList())
            {
                if (orderGoodsItem.getAmt() < 0.01)
                {
                    continue;// 金额为0过滤
                }
                // 1、正常商品 2、套餐主商品 3、套餐子商品
                if (orderGoodsItem.getPackageType() != null && orderGoodsItem.getPackageType().equals("3"))
                {
                    goodsItem_Package.add(orderGoodsItem);
                } else
                {
                    goodsItem_NoPackage.add(orderGoodsItem);
                }
                
            }
            
            //本次分摊的总折扣 --》实收 和实付 总折扣 都是 抹零金额
            BigDecimal totDisc_merReceive = new BigDecimal(dcpOrder.getEraseAmt()).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);;
            BigDecimal totDisc_custPayReal = new BigDecimal(dcpOrder.getEraseAmt()).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);;
            
            // 分摊主商品折扣
            BigDecimal disc_merReceive_add = new BigDecimal("0");
            BigDecimal disc_custPayReal_add = new BigDecimal("0");
            
            
            
            for (int i = 0; i < goodsItem_NoPackage.size(); i++)
            {
                BigDecimal curDisc_merReceive = new BigDecimal("0");
                BigDecimal curDisc_custPayReal = new BigDecimal("0");
                // 最后一个
                if (i == goodsItem_NoPackage.size() - 1)
                {
                    curDisc_merReceive = totDisc_merReceive.subtract(disc_merReceive_add).setScale(scaleCount,
                            BigDecimal.ROUND_HALF_UP);
                    curDisc_custPayReal = totDisc_custPayReal.subtract(disc_custPayReal_add).setScale(scaleCount,
                            BigDecimal.ROUND_HALF_UP);
                } else
                {
                    // 折扣分摊下 安金额比例
                    BigDecimal amtRate = new BigDecimal("0");
                    BigDecimal curAmt = new BigDecimal(goodsItem_NoPackage.get(i).getAmt());
                    try
                    {
                        amtRate = curAmt.divide(tot_Amt, 4, BigDecimal.ROUND_HALF_UP);
                    } catch (Exception e)
                    {
                    
                    }
                    curDisc_merReceive = amtRate.multiply(totDisc_merReceive).setScale(scaleCount,
                            BigDecimal.ROUND_HALF_UP);
                    curDisc_custPayReal = amtRate.multiply(totDisc_custPayReal).setScale(scaleCount,
                            BigDecimal.ROUND_HALF_UP);
                    disc_merReceive_add = disc_merReceive_add.add(curDisc_merReceive);
                    disc_custPayReal_add = disc_custPayReal_add.add(curDisc_custPayReal);
                    
                }
                
                if(curDisc_merReceive.compareTo(BigDecimal.ZERO)==0&&curDisc_custPayReal.compareTo(BigDecimal.ZERO)==0)
                {
                    writelog_waimai(logBeginStr + "金额太小了，保留2位小数后折扣=0，循环下个商品");
                    continue;
                }
                
                // 商品折扣合计 。循环分摊的要加上前一次的折扣
                BigDecimal oldDisc_merReceive = new BigDecimal(goodsItem_NoPackage.get(i).getDisc_merReceive());
                BigDecimal oldDisc_custPayReal = new BigDecimal(goodsItem_NoPackage.get(i).getDisc_custPayReal());
                
                goodsItem_NoPackage.get(i).setDisc_merReceive(curDisc_merReceive.add(oldDisc_merReceive)
                        .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsItem_NoPackage.get(i).setDisc_custPayReal(curDisc_custPayReal.add(oldDisc_custPayReal)
                        .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                
                goodsItem_NoPackage.get(i).setAmt_merReceive(
                        goodsItem_NoPackage.get(i).getAmt() - goodsItem_NoPackage.get(i).getDisc_merReceive());
                goodsItem_NoPackage.get(i).setAmt_custPayReal(
                        goodsItem_NoPackage.get(i).getAmt() - goodsItem_NoPackage.get(i).getDisc_custPayReal());
                
                // 套餐主商品不挂折扣档，挂到子商品下面
                if (goodsItem_NoPackage.get(i).getPackageType() != null
                        && goodsItem_NoPackage.get(i).getPackageType().equals("2"))
                {
                    // 套餐主商品不挂折扣档，挂到子商品下面
                    List<orderGoodsItem> goodsItem_Package_MItem = new ArrayList<orderGoodsItem>();// 当前主商品对应得套餐子商品
                    String curItem = goodsItem_NoPackage.get(i).getItem();
                    for (orderGoodsItem packageItem : goodsItem_Package)
                    {
                        if (packageItem.getPackageMitem() != null && packageItem.getPackageMitem().equals(curItem))
                        {
                            goodsItem_Package_MItem.add(packageItem);
                        }
                    }
                    writelog_waimai(logBeginStr + "分摊开始【存在套餐商品】【分摊套餐主商品下对应得子商品】");
                    // 分摊主商品折扣
                    BigDecimal package_disc_merReceive_add = new BigDecimal("0");
                    BigDecimal package_disc_custPayReal_add = new BigDecimal("0");
                    for (int j = 0; j < goodsItem_Package_MItem.size(); j++)
                    {
                        
                        BigDecimal package_curDisc_merReceive = new BigDecimal("0");
                        BigDecimal package_curDisc_custPayReal = new BigDecimal("0");
                        // 最后一个
                        if (j == goodsItem_Package_MItem.size() - 1)
                        {
                            package_curDisc_merReceive = curDisc_merReceive.subtract(package_disc_merReceive_add)
                                    .setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            package_curDisc_custPayReal = curDisc_custPayReal.subtract(package_disc_custPayReal_add)
                                    .setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                        } else
                        {
                            // 折扣分摊下 安金额比例
                            BigDecimal amtRate = new BigDecimal("0");
                            
                            try
                            {
                                amtRate = new BigDecimal(goodsItem_Package_MItem.get(j).getAmt()
                                        / goodsItem_NoPackage.get(i).getAmt());
                            } catch (Exception e)
                            {
                                // TODO: handle exception
                            }
                            package_curDisc_merReceive = amtRate.multiply(curDisc_merReceive).setScale(scaleCount,
                                    BigDecimal.ROUND_HALF_UP);
                            package_curDisc_custPayReal = amtRate.multiply(curDisc_custPayReal).setScale(scaleCount,
                                    BigDecimal.ROUND_HALF_UP);
                            package_disc_merReceive_add = package_disc_merReceive_add
                                    .add(package_curDisc_merReceive);
                            package_disc_custPayReal_add = package_disc_custPayReal_add
                                    .add(package_curDisc_custPayReal);
                            
                        }
                        
                        // 添加折扣档
                        if (goodsItem_Package_MItem.get(j).getAgioInfo() == null)
                        {
                            goodsItem_Package_MItem.get(j).setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                        }
                        
                        int agioItem = goodsItem_Package_MItem.get(j).getAgioInfo().size() + 1;
                        
                        orderGoodsItemAgio agio = new orderGoodsItemAgio();
                        agio.setDcType(dcType);
                        agio.setDcTypeName(dcTypeName);
                        agio.setAmt(goodsItem_Package_MItem.get(j).getAmt());
                        agio.setDisc(0);// 原始账
                        agio.setDisc_merReceive(package_curDisc_merReceive.doubleValue());// 实收
                        agio.setDisc_custPayReal(package_curDisc_custPayReal.doubleValue());// 顾客实付
                        agio.setQty(goodsItem_Package_MItem.get(j).getQty());
                        agio.setItem(agioItem + "");
                        
                        goodsItem_Package_MItem.get(j).getAgioInfo().add(agio);
                        
                        // 商品折扣合计 。循环分摊的所有要加上前一次的折扣
                        BigDecimal package_oldDisc_merReceive = new BigDecimal(
                                goodsItem_Package_MItem.get(j).getDisc_merReceive());
                        BigDecimal package_oldDisc_custPayReal = new BigDecimal(
                                goodsItem_Package_MItem.get(j).getDisc_custPayReal());
                        goodsItem_Package_MItem.get(j)
                                .setDisc_merReceive(package_curDisc_merReceive.add(package_oldDisc_merReceive)
                                        .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                        goodsItem_Package_MItem.get(j)
                                .setDisc_custPayReal(package_curDisc_custPayReal.add(package_oldDisc_custPayReal)
                                        .setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
                        
                        goodsItem_Package_MItem.get(j).setAmt_merReceive(goodsItem_Package_MItem.get(j).getAmt()
                                - goodsItem_Package_MItem.get(j).getDisc_merReceive());
                        goodsItem_Package_MItem.get(j).setAmt_custPayReal(goodsItem_Package_MItem.get(j).getAmt()
                                - goodsItem_Package_MItem.get(j).getDisc_custPayReal());
                        // goodsItem_Share.add(goodsItem_Package_MItem.get(j));
                        
                    }
                    
                } else
                {
                    
                    if (goodsItem_NoPackage.get(i).getAgioInfo() == null)
                    {
                        goodsItem_NoPackage.get(i).setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                    }
                    
                    int agioItem = goodsItem_NoPackage.get(i).getAgioInfo().size() + 1;
                    
                    orderGoodsItemAgio agio = new orderGoodsItemAgio();
                    agio.setDcType(dcType);
                    agio.setDcTypeName(dcTypeName);
                    agio.setAmt(goodsItem_NoPackage.get(i).getAmt());
                    agio.setDisc(0);// 原始账
                    agio.setDisc_merReceive(curDisc_merReceive.doubleValue());// 嘉华实收
                    agio.setDisc_custPayReal(curDisc_custPayReal.doubleValue());// 顾客实付
                    agio.setQty(goodsItem_NoPackage.get(i).getQty());
                    agio.setItem(agioItem + "");
                    
                    goodsItem_NoPackage.get(i).getAgioInfo().add(agio);
                    
                }
                
                // goodsItem_Share.add(goodsItem_NoPackage.get(i));
                
            }
            
            //旧的折扣
            BigDecimal totDisc_merReceive_old = new BigDecimal(dcpOrder.getTotDisc_merReceive());
            BigDecimal totDisc_custPayReal_old = new BigDecimal(dcpOrder.getTotDisc_custPayReal());
            
            //
            BigDecimal totDisc_merReceive_add = totDisc_merReceive.add(totDisc_merReceive_old).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
            BigDecimal totDisc_custPayReal_add = totDisc_custPayReal.add(totDisc_custPayReal_old).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
            
            dcpOrder.setTotDisc_merReceive(totDisc_merReceive_add.doubleValue());
            dcpOrder.setTotDisc_custPayReal(totDisc_custPayReal_add.doubleValue());
            dcpOrder.setTot_Amt_merReceive(tot_Amt.subtract(totDisc_merReceive_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
            dcpOrder.setTot_Amt_custPayReal(tot_Amt.subtract(totDisc_custPayReal_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP).doubleValue());
            
            writelog_waimai(logBeginStr + "分摊完成！");
            writelog_waimai(logBeginStr + "分摊后json:" + pj.beanToJson(dcpOrder));
            
        }
        
        catch (Exception e)
        {
            writelog_waimai(logBeginStr + "处理异常:" + e.getMessage());
        }
        
        
    }
    
    /**
     * 单头商户实收和顾客实付重新取值，取付款对应的合计
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void posOrderTotAmtMerReceiveProcess(order dcpOrder,  StringBuffer errorMessage) throws Exception
    {
        
        if (errorMessage == null)
        {
            errorMessage = new StringBuffer();
        }
        if (dcpOrder == null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String status = dcpOrder.getStatus();
        String refundStatus = dcpOrder.getRefundStatus();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String logBeginStr = "【订单单头商户实收和顾客实付重新赋值】单号orderNo=" + orderNo + ",";
        
        
        if (loadDocType.equals(orderLoadDocType.POS) || loadDocType.equals(orderLoadDocType.POSANDROID))
        {
        
        } else
        {
            errorMessage.append("渠道类型=" + loadDocType + "单头商户实收和顾客实付无须重新赋值！ 单号orderNo=" + orderNo);
            return;
        }
        
        writelog_waimai(logBeginStr + "开始");
        writelog_waimai(logBeginStr + "赋值前，商户实收tot_amt_merreceive="+dcpOrder.getTot_Amt_merReceive() + ",顾客实付tot_amt_custpayreal="+dcpOrder.getTot_Amt_custPayReal());
        int scaleCount = 2;
        BigDecimal tot_amt_merReceive = new BigDecimal("0");
        BigDecimal tot_amt_custPayReal = new BigDecimal("0");
        
        
        if (dcpOrder.getPay() != null)
        {
            for (orderPay payMent : dcpOrder.getPay())
            {
                
                BigDecimal merReceive =new BigDecimal(payMent.getMerReceive());
                tot_amt_merReceive = tot_amt_merReceive.add(merReceive).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                
                BigDecimal custPayReal =new BigDecimal(payMent.getCustPayReal());
                tot_amt_custPayReal = tot_amt_custPayReal.add(custPayReal).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                
            }
            
        }
        dcpOrder.setTot_Amt_merReceive(tot_amt_merReceive.doubleValue());
        dcpOrder.setTot_Amt_custPayReal(tot_amt_custPayReal.doubleValue());
        
        writelog_waimai(logBeginStr + "赋值后，商户实收tot_amt_merreceive="+dcpOrder.getTot_Amt_merReceive() + ",顾客实付tot_amt_custpayreal="+dcpOrder.getTot_Amt_custPayReal());
        
        
    }
    
    
    /**
     * 数据库字段长度截取处理
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void orderFieldLengthProcess(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if (errorMessage == null)
        {
            errorMessage = new StringBuffer();
        }
        if (dcpOrder == null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        try
        {
            if(dcpOrder.getOutDocType()!=null&&dcpOrder.getOutDocType().length()>6)//第三方平台类型
            {
                dcpOrder.setOutDocType(dcpOrder.getOutDocType().substring(0,6));
            }
            if(dcpOrder.getOutDocTypeName()!=null&&dcpOrder.getOutDocTypeName().length()>100)//第三方平台名称
            {
                dcpOrder.setOutDocTypeName(dcpOrder.getOutDocTypeName().substring(0,100));
            }
            if(dcpOrder.getOrderShop()!=null&&dcpOrder.getOrderShop().length()>100)//第三方门店ID
            {
                dcpOrder.setOrderShop(dcpOrder.getOrderShop().substring(0,100));
            }
            if(dcpOrder.getOrderShopName()!=null&&dcpOrder.getOrderShopName().length()>100)//第三方门店名称
            {
                dcpOrder.setOrderShopName(dcpOrder.getOrderShopName().substring(0,100));
            }
            if(dcpOrder.getMachineNo()!=null&&dcpOrder.getMachineNo().length()>6)//机台
            {
                dcpOrder.setMachineNo(dcpOrder.getMachineNo().substring(0,6));
            }
            if(dcpOrder.getVerNum()!=null&&dcpOrder.getVerNum().length()>32)
            {
                dcpOrder.setVerNum(dcpOrder.getVerNum().substring(0,32));
            }
            if(dcpOrder.getWorkNo()!=null&&dcpOrder.getWorkNo().length()>32)//班号
            {
                dcpOrder.setWorkNo(dcpOrder.getWorkNo().substring(0,32));
            }
            if(dcpOrder.getOpNo()!=null&&dcpOrder.getOpNo().length()>32)//用户编号
            {
                dcpOrder.setOpNo(dcpOrder.getOpNo().substring(0,32));
            }
            
            if(dcpOrder.getShopName()!=null&&dcpOrder.getShopName().length()>80)
            {
                dcpOrder.setShopName(dcpOrder.getShopName().substring(0,80));
            }
            
            if(dcpOrder.getMachShopName()!=null&&dcpOrder.getMachShopName().length()>80)
            {
                dcpOrder.setMachShopName(dcpOrder.getMachShopName().substring(0,80));
            }
            if(dcpOrder.getShippingShopName()!=null&&dcpOrder.getShippingShopName().length()>80)
            {
                dcpOrder.setShippingShopName(dcpOrder.getShippingShopName().substring(0,80));
            }
            if(dcpOrder.getAddress()!=null&&dcpOrder.getAddress().length()>100)
            {
                dcpOrder.setAddress(dcpOrder.getAddress().substring(0,100));
            }
            if(dcpOrder.getContMan()!=null&&dcpOrder.getContMan().length()>150)
            {
                dcpOrder.setContMan(dcpOrder.getContMan().substring(0,150));
            }
            if(dcpOrder.getContTel()!=null&&dcpOrder.getContTel().length()>150)
            {
                dcpOrder.setContTel(dcpOrder.getContTel().substring(0,150));
            }
            if(dcpOrder.getGetMan()!=null&&dcpOrder.getGetMan().length()>150)
            {
                dcpOrder.setGetMan(dcpOrder.getGetMan().substring(0,150));
            }
            if(dcpOrder.getGetManTel()!=null&&dcpOrder.getGetManTel().length()>150)
            {
                dcpOrder.setGetManTel(dcpOrder.getGetManTel().substring(0,150));
            }
            if(dcpOrder.getMemo()!=null&&dcpOrder.getMemo().length()>255)//订单备注
            {
                dcpOrder.setMemo(dcpOrder.getMemo().substring(0,255));
            }
            if(dcpOrder.getProMemo()!=null&&dcpOrder.getProMemo().length()>150)//生产备注
            {
                dcpOrder.setProMemo(dcpOrder.getProMemo().substring(0,150));
            }
            if(dcpOrder.getDelMemo()!=null&&dcpOrder.getDelMemo().length()>150)//配送备注
            {
                dcpOrder.setDelMemo(dcpOrder.getDelMemo().substring(0,150));
            }
            if(dcpOrder.getRefundReason()!=null&&dcpOrder.getRefundReason().length()>255)//配送备注
            {
                dcpOrder.setRefundReason(dcpOrder.getRefundReason().substring(0,255));
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        
        
        
    }
    
    /**
     * 外面订单接入时商品资料异常
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void waimaiOrderAbnormalSave(order dcpOrder, StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if (dcpOrder == null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        List<orderAbnormal> abnormalList = dcpOrder.getAbnormalList();
        if (abnormalList == null || abnormalList.isEmpty())
        {
            errorMessage.append("没有异常类型");
            return;
        }
        
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//数据库DATE类型
        ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
        try
        {
            for (orderAbnormal orderAbnormal : abnormalList)
            {
                String abnormalType = orderAbnormal.getAbnormalType();
                String memo = orderAbnormal.getMemo();
                if(memo!=null&&memo.length()>1024)
                {
                    memo = memo.substring(0, 1024);
                }
                DelBean del_abnormal = new DelBean("DCP_ORDER_ABNORMALINFO");
                del_abnormal.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                del_abnormal.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                del_abnormal.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
                DataPB.add(new DataProcessBean(del_abnormal));
                String[] columns1= {
                        "EID","ORDERNO","ABNORMALTYPE","ABNORMALTYPENAME","ABNORMALTIME","MEMO","STATUS","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
                };
                
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(orderNo, Types.VARCHAR),
                        new DataValue(abnormalType, Types.VARCHAR),
                        new DataValue(orderAbnormal.getAbnormalTypeName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(memo, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };
                
                InsBean ib1 = new InsBean("DCP_ORDER_ABNORMALINFO", columns1);
                ib1.addValues(insValue1);
                
                DataPB.add(new DataProcessBean(ib1));
                
                
                
                if(orderAbnormal.getDetail()!=null&&orderAbnormal.getDetail().isEmpty()==false)
                {
                    String[] columns_abnormalDetail = {
                            "EID","ORDERNO","ABNORMALTYPE","OITEM","MEMO","STATUS"};
                    for (orderAbnormalDetail detail : orderAbnormal.getDetail())
                    {
                        //先删 在插入
                        DelBean del_detail = new DelBean("DCP_ORDER_ABNORMALINFO_DETAIL");
                        del_detail.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        del_detail.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                        del_detail.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
                        del_detail.addCondition("OITEM", new DataValue(detail.getItem(), Types.VARCHAR));
                        DataPB.add(new DataProcessBean(del_detail));
                        
                        //再插入
                        DataValue[] insValue_abnormalDetail = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(abnormalType, Types.VARCHAR),
                                new DataValue(detail.getItem(), Types.VARCHAR),
                                new DataValue(detail.getMemo(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR)
                            
                        };
                        
                        InsBean ib_abnormalDetail = new InsBean("DCP_ORDER_ABNORMALINFO_DETAIL", columns_abnormalDetail);
                        ib_abnormalDetail.addValues(insValue_abnormalDetail);
                        
                        DataPB.add(new DataProcessBean(ib_abnormalDetail));
                    }
                    
                }
                
                
            }
            
            if(DataPB!=null&&DataPB.isEmpty()==false)
            {
                writelog_waimai("订单接入存在异常，保存数据库开始，订单号orderNo="+orderNo);
                StaticInfo.dao.useTransactionProcessData(DataPB);
                DataPB.clear();
                writelog_waimai("订单接入存在异常，保存数据库成功，订单号orderNo="+orderNo);
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            DataPB.clear();
            writelog_waimai("订单接入存在异常，保存这些异常失败："+e.getMessage()+",订单号orderNo="+orderNo);
        }
        
        
        
    }
    
    
    /**
     * 手机商城订单，订转销后退单需要退积分
     * @param eId
     * @param orderNo
     * @throws Exception
     */
    public static void wechatOrderRefundPoint(String eId,String orderNo,String headOrderNo,String opNo,String opName) throws Exception
    {
        try
        {
            
            if(opNo==null)
            {
                opNo = "";
            }
            if(opName==null)
            {
                opName = "";
            }
            
            if(eId==null||eId.isEmpty())
            {
                return;
            }
            
            if(orderNo==null||orderNo.isEmpty())
            {
                return;
            }
            String logStart = "【手机商城订单，订转销后退单需要退积分】订单号orderNo="+orderNo+",";
            
            
            String sql_header = "select * from dcp_order where eid='"+eId+"'  and orderno='"+orderNo+"' ";
            writelog_waimai(logStart+"查询sql:"+sql_header);
            
            List<Map<String, Object>> dataHeader = StaticInfo.dao.executeQuerySQL(sql_header, null);
            if(dataHeader==null||dataHeader.isEmpty())
            {
                writelog_waimai(logStart+"订单不存在！");
                return;
            }
            String loadDocType =  dataHeader.get(0).getOrDefault("LOADDOCTYPE", " ").toString();
            if(loadDocType.equals(orderLoadDocType.WECHAT)||loadDocType.equals(orderLoadDocType.MINI)||loadDocType.equals(orderLoadDocType.LINE))
            {
            
            }
            else
            {
                writelog_waimai(logStart+"渠道类型loadDoctype="+loadDocType+"暂不支持！");
                return;
            }
            
            boolean isCreateSale = false;
            String memberOrderNo = "";
            String refundOrderNo = "";//销售单退单单号。目前是固定得 RE+原销售单号
            try
            {
                
//                String sql_sale = "select saleno,MEMBERORDERNO from dcp_sale where eid='"+eId+"'  and  ofno='"+orderNo+"' ";
                String sql_sale = "";
                if(headOrderNo!=null&&headOrderNo.trim().length()>0){
                	//有主单号
                	//SOURCESUBORDERNO	NVARCHAR2(64 CHAR)	Yes	''	21	原子单号
                	sql_sale = "select * from dcp_sale where ofno='"+headOrderNo+"' and eid='"+eId+"' and sourcesuborderno='"+orderNo+"'";
                }else{
                	sql_sale = "select * from dcp_sale where ofno='"+orderNo+"' and eid='"+eId+"' ";
                }
                writelog_waimai(logStart+"查询该订单有没有生成销售单sql:"+sql_sale);
                List<Map<String, Object>> jobList=StaticInfo.dao.executeQuerySQL(sql_sale, null);
                if(jobList!=null&&jobList.isEmpty()==false)
                {
                    isCreateSale = true;
                    memberOrderNo = jobList.get(0).getOrDefault("MEMBERORDERNO", "").toString();
                    refundOrderNo = "RE"+jobList.get(0).getOrDefault("SALENO", "").toString();
                }
            }
            catch (Exception e)
            {
            
            }
            if(!isCreateSale)
            {
                writelog_waimai(logStart+"订单没有生成销售单，无须退积分！");
                return;
            }
            if(memberOrderNo==null||memberOrderNo.isEmpty())
            {
                writelog_waimai(logStart+"订单没有会员消费，无须退积分！");
                return;
            }
            writelog_waimai(logStart+"订转销时调用会员消费接口交易单号:"+memberOrderNo);
            
            
            String shop = dataHeader.get(0).getOrDefault("SHOP", " ").toString();
            String orderAmount = dataHeader.get(0).getOrDefault("TOT_AMT", "0").toString();
            String memberId = dataHeader.get(0).getOrDefault("MEMBERID", "").toString();
            String channelId = dataHeader.get(0).getOrDefault("CHANNELID", "").toString();
            String orgType = "3";//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
            
            

            boolean nResult = false;
            String errorStr = "";
            org.json.JSONObject json1 = null;
            //先调用MemberPayPreRefund
            {
            	JSONObject reqJsonobj = new JSONObject();
            	reqJsonobj.put("orderNo", memberOrderNo);//注意是交易单号--
                reqJsonobj.put("orgType", orgType);//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
                reqJsonobj.put("oprId", opNo);
            	String requestStr = reqJsonobj.toString();
                String microMarkServiceName = "MemberPayPreRefund";
                String resbody = HttpSend.MicroMarkSend(requestStr, eId, microMarkServiceName,channelId);
                writelog_waimai(logStart+"调用会员MemberPayPreRefund接口返回:"+resbody);
                if(resbody==null||resbody.isEmpty())
                {
                    errorStr = "MemberPayPreRefund接口不通或返回为空！";
                    return;
                }
                
                json1 = new org.json.JSONObject(resbody);
                try
                {
                    String success = json1.get("success").toString();
                    String serviceDescription = json1.get("serviceDescription").toString();
                    if(success.equals("true"))
                    {
                        nResult = true;
                    }
                    else
                    {
                        
                        errorStr = serviceDescription;
                        
                    }
                    
                }
                catch (Exception e)
                {
                    errorStr = e.getMessage();
                }
            }
            
            //调用MemberPayPreRefund成功再调用MemberPayRefund
            if(nResult){
            	JSONObject reqJsonobj = new JSONObject();
                JSONArray goodsListArray = new JSONArray();
                JSONArray payListArray = new JSONArray();
                
                
                String sql_detail = "select * from dcp_order_Detail where eid='"+eId+"' and orderno='"+orderNo+"' ";
                writelog_waimai(logStart+"查询商品明细sql:"+sql_detail);
                
                List<Map<String, Object>> dataDetail = StaticInfo.dao.executeQuerySQL(sql_detail, null);
                if(dataDetail!=null&&dataDetail.isEmpty()==false)
                {
                    for (Map<String, Object> map : dataDetail)
                    {
                        String packageType = map.getOrDefault("PACKAGETYPE", "").toString();
                        
                        
                        String goods_id = map.getOrDefault("PLUBARCODE", "").toString();
                        String goods_name = map.getOrDefault("PLUNAME", "").toString();
                        String price = map.getOrDefault("PRICE", "0").toString();
                        String quantity = map.getOrDefault("QTY", "0").toString();
                        String amount = map.getOrDefault("AMT", "0").toString();
                        String allowPoint = "1";//是否计算积分 1=是、 0=否
                        
                        JSONObject goodsJsonobj = new JSONObject();
                        goodsJsonobj.put("goods_id", goods_id);
                        goodsJsonobj.put("goods_name", goods_name);
                        goodsJsonobj.put("price", price);
                        goodsJsonobj.put("quantity", quantity);
                        goodsJsonobj.put("amount", amount);
                        goodsJsonobj.put("allowPoint", allowPoint);
                        
                        goodsListArray.put(goodsJsonobj);
                    }
                }
                
                reqJsonobj.put("orderNo", memberOrderNo);//注意是交易单号--
                reqJsonobj.put("refundOrderNo", refundOrderNo);//新的退款单号（商户唯一） 对应退销售单号
                reqJsonobj.put("orderAmount", orderAmount);
                reqJsonobj.put("memberId", memberId);
                reqJsonobj.put("channelId", channelId);
                reqJsonobj.put("orgType", orgType);//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
                reqJsonobj.put("oprId", opNo);
                
                reqJsonobj.put("goodsdetail", goodsListArray);
                if(json1!=null){
                	if(json1.has("datas")){
                		org.json.JSONObject datajson1 = json1.getJSONObject("datas");
                		if(datajson1.has("cards")){
                    		reqJsonobj.put("cards", datajson1.opt("cards"));
                    	}
                    	if(datajson1.has("coupons")){
                    		reqJsonobj.put("coupons", datajson1.opt("coupons"));
                    	}
                	}
                	
                }
                
                String requestStr = reqJsonobj.toString();
                
                String microMarkServiceName = "MemberPayRefund";
                nResult = false;
                errorStr = "";
                String resbody = HttpSend.MicroMarkSend(requestStr, eId, microMarkServiceName,channelId);
                writelog_waimai(logStart+"调用会员MemberPayRefund接口返回:"+resbody);
                if(resbody==null||resbody.isEmpty())
                {
                    errorStr = "接口不通或返回为空！";
                    return;
                }
                
                org.json.JSONObject json = new org.json.JSONObject(resbody);
                try
                {
                    String success = json.get("success").toString();
                    String serviceDescription = json.get("serviceDescription").toString();
                    if(success.equals("true"))
                    {
                        nResult = true;
                    }
                    else
                    {
                        
                        errorStr = serviceDescription;
                        
                    }
                    
                }
                catch (Exception e)
                {
                    errorStr = e.getMessage();
                }
            }
            
            
            
            
            //写日志
            try
            {
                // 写日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(channelId);
                onelv1.setLoadDocBillType("");
                onelv1.setLoadDocOrderNo("");
                onelv1.seteId(eId);
                
                onelv1.setOpName(opName);
                onelv1.setOpNo(opNo);
                onelv1.setShopNo(shop);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo("");
                onelv1.setShippingShopNo("");
                String statusType = "1";
                String updateStaus = "99";
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                
                String statusName = "其他";
                String statusTypeName = "其他状态";
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                String memo = "";
                if(nResult)
                {
                    memo = "退消费积分成功<br>";
                }
                else
                {
                    if(errorStr!=null&&errorStr.length()>30)
                    {
                        errorStr = errorStr.substring(0,30)+"...";
                    }
                    memo = "退消费积分失败("+errorStr+")<br>";
                }
                
                onelv1.setMemo(memo);
                onelv1.setDisplay("0");
                
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet)
                {
                    HelpTools.writelog_waimai("【写表dcp_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
                } else
                {
                    HelpTools.writelog_waimai(
                            "【写表dcp_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
                }
                
            }
            catch (Exception  e)
            {
            
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }
    
    
    public static boolean CustomerCreditUpdate(String eId,String apiUserCode,String userKey,String langType,
                                               String orderNo, String companyId, String shopId,String opType, String opName, String memo, String loadBillNo,
                                               List<JindieGoodsDetail>  details, String customerId,double dealCreditAmount,StringBuffer error,String bdate) throws Exception
    {
        boolean nRet = false;
        String Yc_Url="";
        String Yc_Key=apiUserCode;
        String Yc_Sign_Key=userKey;
        if(langType==null||langType.isEmpty())
        {
            langType = "zh_CN";
        }
        Yc_Url=PosPub.getCRM_INNER_URL(eId);
        writelog_waimai("【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate,请求地址CrmUrl="+Yc_Url +",请求apiUserCode:"+Yc_Key+",请求userKey:"+userKey+",单号orderNo="+orderNo);
        if(Yc_Url==null||Yc_Url.trim().equals("") || Yc_Key==null|| Yc_Key.trim().equals("")||Yc_Sign_Key==null ||Yc_Sign_Key.trim().equals(""))
        {
            writelog_waimai("【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate,参数CrmUrl、apiUserCode、userKey有空值，单号orderNo="+orderNo);
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate,CrmUrl、apiUserCode、userKey参数都不可为空!");
        }
        
        
        try
        {
            String serviceId = "CustomerCreditUpdate";
            JSONObject req_body=new JSONObject(new TreeMap<String, Object>());
            
            req_body.put("companyId", companyId);
            req_body.put("customerId", customerId);
            req_body.put("shopId", shopId);
            req_body.put("dealCreditAmount", dealCreditAmount);
            req_body.put("opType", opType);//操作类型 1:订单 2:订单作废 3:订转销 4:销售单 5:销售退单
            req_body.put("loadBillNo", loadBillNo);//来源单据编号
            req_body.put("billNo", orderNo);//单据编号
            req_body.put("opName", opName);//单据经办人
            req_body.put("memo", memo);//单据备注
            req_body.put("bdate", bdate);//营业日期yyyy-MM-dd
            
            
            //添加商品明细
            JSONArray goodsDetail=new JSONArray();
            if (details != null)
            {
                for (JindieGoodsDetail lv2Detail : details)
                {
                    JSONObject req_goods=new JSONObject();
                    req_goods.put("item",lv2Detail.getItem());
                    req_goods.put("oItem",lv2Detail.getoItem());
                    req_goods.put("oQty",lv2Detail.getoQty());
                    req_goods.put("pluNo",lv2Detail.getPluNo());
                    req_goods.put("unitId",lv2Detail.getUnitId());
                    req_goods.put("qty",lv2Detail.getQty());
                    req_goods.put("oldPrice",lv2Detail.getOldPrice());
                    req_goods.put("price",lv2Detail.getPrice());
                    req_goods.put("disc",lv2Detail.getDisc());
                    req_goods.put("amt",lv2Detail.getAmt());
                    req_goods.put("oldAmt",lv2Detail.getOldAmt());
                    req_goods.put("memo",lv2Detail.getMemo());
                    goodsDetail.put(req_goods);
                }
            }
            req_body.put("goodsDetail",goodsDetail);
            
            String req_body_str = req_body.toString();
            String sign = PosPub.encodeMD5(req_body_str+Yc_Sign_Key);//sign=md5(body+key)加密

            String requestId=PosPub.getGUID(false);
            Map<String, Object> map = new HashMap<>();
            
            map.put("serviceId", serviceId);
            map.put("apiUserCode", Yc_Key);
            map.put("sign", sign);
            map.put("langType", langType);
            map.put("requestId", requestId);
            map.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            map.put("version", "3.0");
            
            writelog_waimai("【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate,请求Body:"+req_body_str +",请求header："+map.toString()+",单号orderNo="+orderNo);
            String res = HttpSend.doPost(Yc_Url, req_body_str, map,requestId);
            writelog_waimai("【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate,返回(不用关心返回结果):"+res +",单号orderNo="+orderNo);
            
            if (Check.Null(res))
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate返回空，请检查网络!");
            }
            
            JSONObject res_json=new JSONObject(res);
            
            nRet=res_json.getBoolean("success");
            
            if (!nRet)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate返回" + res_json.get("serviceDescription").toString());
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimai("【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate,异常:"+e.getMessage()+",单号orderNo="+orderNo);
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "【订单创建】【赊销订单】调用CRM接口CustomerCreditUpdate返回" + e.getMessage());
        }
        
        return nRet;
    }
    
    
    private static int getMaxOrderSn(order dcpOrder) throws Exception
    {
        int orderSn = 1;
        try
        {
            StringBuffer sqlBuff = new StringBuffer("");
            sqlBuff.append(" select * from (");
            sqlBuff.append(" select max(TO_NUMBER(order_sn)) sn from dcp_order where order_sn is not null");
            sqlBuff.append(" and loaddoctype='"+dcpOrder.getLoadDocType()+"' ");
            sqlBuff.append(" and shop ='"+dcpOrder.getShopNo()+"' ");
            sqlBuff.append(" and bdate='"+dcpOrder.getbDate()+"' ");
            sqlBuff.append(")");
            
            String sql = sqlBuff.toString();
            writelog_waimai("渠道类型loaddoctype="+dcpOrder.getLoadDocType()+"，查询当天下单门店最大流水号sql="+sql+",单号orderNo="+dcpOrder.getOrderNo());
            List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
            if(getQData!=null&&getQData.isEmpty()==false)
            {
                String sn = getQData.get(0).get("SN").toString();
                try
                {
                    
                    orderSn = Integer.parseInt(sn)+1;
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
            }
            
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        return orderSn;
    }
    
    public static void orderToSale(DsmDAO dao,String eId,String shop,String orderNo,String logFileName) throws Exception
    {
        if(logFileName==null||logFileName.isEmpty())
        {
            logFileName = "orderToSaleAuto";
        }
        try
        {
            TokenManagerRetail tmr=new TokenManagerRetail();
            DCP_LoginRetailRes res_token = new DCP_LoginRetailRes();
            DCP_LoginRetailRes.level1Elm oneLv1 =res_token.new level1Elm();
            oneLv1.seteId(eId);
            oneLv1.setShopId(shop);
            oneLv1.setLangType("zh_CN");
            res_token.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());
            res_token.getDatas().add(oneLv1);
            String token = tmr.produce(res_token);
            
            DCP_OrderShippingReq req_sale = new  DCP_OrderShippingReq();
            DCP_OrderShippingReq.levelRequest req_sale_requeset = req_sale.new levelRequest();
            req_sale_requeset.setOpType("1");
            
            req_sale_requeset.setOrderList(new String[] {orderNo});
            Map<String,Object> jsonMap=new HashMap<String,Object>();
            jsonMap.put("serviceId", "DCP_OrderShipping");
            //这个token是无意义的
            jsonMap.put("token", token);
            jsonMap.put("plantType", "nrc");
            jsonMap.put("request", req_sale_requeset);
            ParseJson pj=new ParseJson();
            String json_ship = pj.beanToJson(jsonMap);
            writelog_fileName("订单orderNo="+orderNo+",出货订转销请求： "+ json_ship,logFileName);
            
            DispatchService ds = DispatchService.getInstance();
            String resbody_ship = ds.callService(json_ship, dao);
            tmr.deleteTokenAndDB(token);//删掉用过的
            tmr = null;
            writelog_fileName("订单orderNo="+orderNo+",出货订转销返回： "+ resbody_ship,logFileName);
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_fileName("订单orderNo="+orderNo+",出货订转销异常:"+e.getMessage(),logFileName);
        }
    }
    
    /**
     * 物流类型名称枚举值
     * @param deliverType
     * @return
     * @throws Exception
     */
    public static String getDeliveryTypeName(String deliverType) throws Exception
    {
        String res = "";
        try
        {
            switch (deliverType)
            {
                case "2":
                    res = "顺丰同城";
                    break;
                case "4":
                    res = "达达";
                    break;
                case "5":
                    res = "人人快递";
                    break;
                case "6":
                    res = "闪送";
                    break;
                case "20":
                    res = "点我达";
                    break;
                case "21":
                    res = "管易云物流";
                    break;
                case "23":
                    res = "美团跑腿";
                    break;
                case "24":
                    res = "圆通";
                    break;
                case "KDN":
                    res = "快递鸟物流";
                    break;
                
                default:
                    res = "未知";
                    break;
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        
        
        return res;
        
    }
    
    /**
     * 订单接入时自动分配生产机构(开启了生产调度,调用该方法前判断)(根据配送机构查询距离最近的生产机构)
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void getMachShopByShippingShop(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        try
        {
            //先判断有没有生产门店 ，已经有生产门店了，无须自动匹配
            if(dcpOrder.getMachShopNo()!=null&&dcpOrder.getMachShopNo().isEmpty()==false)
            {
                if (orderLoadDocType.YOUZAN.equals(dcpOrder.getLoadDocType()))
                {
                    writelog_waimai("【生产调度】,订单上已有生产机构machShopNo="+dcpOrder.getMachShopNo()+"，但渠道类型="+dcpOrder.getLoadDocType()+"还得继续查询自动分配生产机构，单号orderNo="+orderNo);
                }
                else
                {
                    writelog_waimai("【生产调度】,订单上已有生产机构machShopNo="+dcpOrder.getMachShopNo()+"，无须自动分配生产机构，单号orderNo="+orderNo);
                    return;
                }
                
            }
            
            String sql = "";
            //根据配送机构，找对应的生产机构。
            //1.先判断配送机构，是否支持生产，支持生产，生产机构=配送机构
            //2.不支持的话，根据配送机构辐射的生产机构，寻找最近的生产机构
            String shippingShopNo = dcpOrder.getShippingShopNo();
            if(shippingShopNo==null||shippingShopNo.isEmpty())
            {
                writelog_waimai("【生产调度】,订单上配送机构为空！无法根据配送机构自动分配生产机构，单号orderNo="+orderNo);
                return;
            }
            
            sql = "";
            sql = " select A.*,B.LATITUDE,B.LONGITUDE from DCP_ORG_ORDERSET A "
                    + " left join DCP_ORG B on A.EID=B.EID and A.ORGANIZATIONNO=B.ORGANIZATIONNO "
                    + " where A.EID='"+eId+"' and A.ORGANIZATIONNO='"+shippingShopNo+"' ";
            writelog_waimai("【生产调度】【获取配送机构参数设置】查询sql=" + sql+" 单号orderNo="+orderNo);
            List<Map<String, Object>> getShippingShopOrderSet = StaticInfo.dao.executeQuerySQL(sql, null);
            if(getShippingShopOrderSet==null||getShippingShopOrderSet.isEmpty())
            {
                writelog_waimai("【生产调度】【获取配送机构参数设置】查询结果为空， 单号orderNo="+orderNo);
                return;
            }
            
            String isProduction = getShippingShopOrderSet.get(0).getOrDefault("ISPRODUCTION", "").toString();
            
            writelog_waimai("【生产调度】【获取配送机构参数值】是否支持生产isProduction="+isProduction+"， 单号orderNo="+orderNo);
            if("Y".equals(isProduction))
            {
                dcpOrder.setMachShopNo(shippingShopNo);
                dcpOrder.setMachShopName(dcpOrder.getShippingShopName());
                writelog_waimai("【生产调度】【配送机构支持生产】生产机构=配送机构="+shippingShopNo+"， 单号orderNo="+orderNo);
                return;
            }
            
            String latitude = getShippingShopOrderSet.get(0).getOrDefault("LATITUDE", "").toString();
            String longitude = getShippingShopOrderSet.get(0).getOrDefault("LONGITUDE", "").toString();
            writelog_waimai("【生产调度】【配送机构不支持生产】该配送机构的经纬度，维度latitude="+latitude+"，经度longitude="+longitude+"， 单号orderNo="+orderNo);
            if(latitude.isEmpty()||longitude.isEmpty())
            {
                writelog_waimai("【生产调度】【配送机构不支持生产】该配送机构的经纬度为空，维度latitude="+latitude+"，经度longitude="+longitude+"， 单号orderNo="+orderNo);
                return;
            }
            
            StringBuffer sqlBuffer = new StringBuffer("");
            
            sqlBuffer.append(" select * from (");
            //先查询 辐射该配送机构的生产机构
            sqlBuffer.append(" with p as (");
            sqlBuffer.append(" select distinct  EID,ORGANIZATIONNO FROM (");
            sqlBuffer.append(" select EID,ORGANIZATIONNO FROM DCP_ORG_ORDERSET where ISPRODUCTION='Y' AND RADIATETYPE='0' AND EID='"+eId+"' ");//全部辐射的
            sqlBuffer.append(" UNION ALL ");
            sqlBuffer.append(" select A.EID,A.ORGANIZATIONNO  FROM DCP_ORG_ORDERSET A inner join DCP_ORG_ORDERSET_RADIATEORG B on A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO ");
            sqlBuffer.append("  where A.ISPRODUCTION='Y' AND A.RADIATETYPE='1' AND A.EID='"+eId+"' AND B.RADIATESHIPPINGSHOP='"+shippingShopNo+"' ");//辐射指定机构
            sqlBuffer.append(" )");
            sqlBuffer.append(" )");
            //再经纬度距离计算
            sqlBuffer.append(" select a.*,c.ORG_NAME,F_CRM_GetDistance("+latitude+","+longitude+",a.LATITUDE,a.LONGITUDE) DISTANCE from DCP_ORG a ");
            sqlBuffer.append(" inner join p on a.eid=p.eid and a.ORGANIZATIONNO=p.ORGANIZATIONNO ");
            sqlBuffer.append(" left join Dcp_Org_Lang  C on a.eid=C.eid and a.ORGANIZATIONNO=C.ORGANIZATIONNO AND C.LANG_TYPE='zh_CN' ");
            sqlBuffer.append(" where a.eid='"+eId+"'");
            
            sqlBuffer.append(" ) order by DISTANCE");
            
            sql="";
            sql = sqlBuffer.toString();
            writelog_waimai("【生产调度】【配送机构不支持生产】查询辐射该配送机构的生产机构，sql="+sql+"， 单号orderNo="+orderNo);
            
            List<Map<String, Object>> getMachShop = StaticInfo.dao.executeQuerySQL(sql, null);
            if(getMachShop==null||getMachShop.isEmpty())
            {
                writelog_waimai("【生产调度】【配送机构不支持生产】查询辐射该配送机构的生产机构,查询结果为空， 单号orderNo="+orderNo);
                return;
            }
            
            String machShopNo = getMachShop.get(0).getOrDefault("ORGANIZATIONNO", "").toString();
            String machShopName = getMachShop.get(0).getOrDefault("ORG_NAME", "").toString();
            dcpOrder.setMachShopNo(machShopNo);
            dcpOrder.setMachShopName(machShopName);
            writelog_waimai("【生产调度】【配送机构不支持生产】查询辐射该配送机构的生产机构,找到距离最近的生产机构machShopNo="+machShopNo+"，machShopName="+machShopName+"， 单号orderNo="+orderNo);
            
        }
        catch (Exception e)
        {
            writelog_waimai("【生产调度】异常:"+e.getMessage()+"， 单号orderNo="+orderNo);
            errorMessage.append(e.getMessage());
        }
    }
    
    /**
     * 订单的生产门店，或者生产数量根据商品属性来判断
     * @param dcpOrder
     * @param errorMessage
     * @throws Exception
     */
    public static void updateMachShopByGoods(order dcpOrder,StringBuffer errorMessage) throws Exception
    {
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        boolean isNeedProduce = false;//是否需要生产
        try
        {
            if (dcpOrder.getGoodsList()==null||dcpOrder.getGoodsList().isEmpty())
            {
                return;
            }
            String sql = "";
            for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
            {
                sql = "";
                try
                {
                    String pluNo = goodsItem.getPluNo();
                    if (pluNo==null||pluNo.trim().isEmpty())
                    {
                        isNeedProduce = true;//默认都生产
                        continue;
                    }
                    sql = " select * from dcp_goods where eId='"+eId+"' and pluno='"+pluNo.replaceAll("'","''")+"' ";
                    HelpTools.writelog_waimai("【获取商品是否需要生产属性】循环开始，查询资料sql="+ sql+",商品编码pluNo="+pluNo+",单号orderNo="+orderNo);
                    List<Map<String, Object>> getPluInfo = StaticInfo.dao.executeQuerySQL(sql, null);
                    if (getPluInfo!=null&&!getPluInfo.isEmpty())
                    {
                        String goodsType = getPluInfo.get(0).getOrDefault("GOODSTYPE","").toString();//商品类型 1普通商品，2需要生产的商品
                        if ("1".equals(goodsType))
                        {
                            HelpTools.writelog_waimai("【获取商品是否需要生产属性】循环开始,商品编码pluNo="+pluNo+"对应的商品类型goodsType="+goodsType+",无需生产,则把shopQty=qty,单号orderNo="+orderNo);
                            goodsItem.setShopQty(goodsItem.getQty());
                        }
                        else
                        {
                            HelpTools.writelog_waimai("【获取商品是否需要生产属性】循环开始,商品编码pluNo="+pluNo+"对应的商品类型goodsType="+goodsType+",需要生产,单号orderNo="+orderNo);
                            isNeedProduce = true;
                        }
                        
                    }
                }
                catch (Exception e)
                {
                    continue;
                }
                
                
            }
            
            //如果没有需要生产的商品，则清空生产门店
            if (!isNeedProduce)
            {
                String isShipCompany = "N";//是否总部生产
                //如果生产门店是总部,则不清空
                String machShopNo = dcpOrder.getMachShopNo();
                if (machShopNo!=null&&!machShopNo.isEmpty())
                {
                    String sql_shop_machShop = "select * from dcp_org where eid='"+eId+"' and ORGANIZATIONNO='"+machShopNo+"' ";
                    List<Map<String, Object>> getShopData = StaticInfo.dao.executeQuerySQL(sql_shop_machShop, null);
                    if(getShopData!=null&&getShopData.isEmpty()==false)
                    {
                        String org_form = getShopData.get(0).getOrDefault("ORG_FORM", "").toString();
                        if(org_form.equals("0"))
                        {
                            isShipCompany = "Y";
                            HelpTools.writelog_waimai("根据生产门店查询门店组织类型ORG_FORM="+org_form+"【是总部生产】,【不清空生产门店】,单号orderNo="+orderNo);
                        }
                        
                    }
                }
                
                if ("N".equals(isShipCompany))
                {
                    if (machShopNo.equals(dcpOrder.getShippingShopNo()))
                    {
                        HelpTools.writelog_waimai("该订单的商品明细没有需要生产的商品,配送机构shippingShopNo="+dcpOrder.getShippingShopNo()+",生产机构machShopNo="+machShopNo+",【配送和生产机构一致，不清空生产门店】,单号orderNo="+orderNo);
                    }
                    else
                    {
                        dcpOrder.setMachShopNo("");
                        dcpOrder.setMachShopName("");
                        HelpTools.writelog_waimai("该订单的商品明细没有需要生产的商品,配送机构shippingShopNo="+dcpOrder.getShippingShopNo()+",生产机构machShopNo="+machShopNo+",【配送和生产机构不一致，清空生产门店】,单号orderNo="+orderNo);
                    }
                }
                
            }
            
            
            
        }
        catch (Exception e)
        {
            
        }
    }
    
    /**
     * 外卖单取消或退单、订转销时加锁，ETL设置默认15秒
     * @param opType 0表示订转销；1表示订单取消或退单
     * @param eId 企业ID
     * @param orderNo 单号
     * @throws Exception
     */
    public static void  setWaiMaiOrderToSaleOrRefundRedisLock(String opType,String eId,String orderNo) throws Exception
    {
        int exprieSeconds = 15;
        String redisKey = orderRedisKeyInfo.redis_OrderRefund+":"+eId+":"+orderNo;
        String redisValue = orderNo;
        String typeString = "";
        if ("0".equals(opType))
        {
            redisKey = orderRedisKeyInfo.redis_OrderToSale+":"+eId+":"+orderNo;
            typeString = "【订转销】";
        }
        else if ("1".equals(opType))
        {
            redisKey = orderRedisKeyInfo.redis_OrderRefund+":"+eId+":"+orderNo;
            typeString = "【取消或退单】";
        }
        else
        {
            return;
        }
        
        try
        {
            RedisPosPub redis = new RedisPosPub();
            redis.setString(redisKey,redisValue);
            redis.setExpire(redisKey,15);
            writelog_waimai(typeString+"【外卖单加锁】写Redis成功,主键redisKey="+redisKey);
        }
        catch (Exception e)
        {
            writelog_waimai(typeString+"【外卖单加锁】写Redis异常:"+e.getMessage()+",主键redisKey="+redisKey);
        }
    }
    
    /**
     * 判断下是否存在相应的锁（订转销需要判断有没有取消或退单的锁;订单取消或退单需要判断有没有订转销的锁）
     * @param opType 0表示订转销；1表示订单取消或退单
     * @param eId 企业ID
     * @param orderNo 单号
     * @return
     * @throws Exception
     */
    public static boolean  IsExistWaiMaiOrderToSaleOrRefundRedisLock(String opType,String eId,String orderNo) throws Exception
    {
        String redisKey = orderRedisKeyInfo.redis_OrderRefund+":"+eId+":"+orderNo;
        String redisValue = orderNo;
        String typeString = "";
        if ("0".equals(opType))
        {
            redisKey = orderRedisKeyInfo.redis_OrderToSale+":"+eId+":"+orderNo;
            typeString = "【查询是否存在订转销的锁】,主键redisKey="+redisKey;
        }
        else if ("1".equals(opType))
        {
            redisKey = orderRedisKeyInfo.redis_OrderRefund+":"+eId+":"+orderNo;
            typeString = "【查询是否存在取消或退单的锁】,主键redisKey="+redisKey;
        }
        else
        {
            return false;
        }
        
        try
        {
            RedisPosPub redis = new RedisPosPub();
            boolean isexistHashkey = redis.IsExistStringKey(redisKey);
            if (isexistHashkey)
            {
                writelog_waimai(typeString+"，查询结果:【存在】");
            }
            else {
                writelog_waimai(typeString+"，查询结果:【不存在】");
            }
            return isexistHashkey;
            
        }
        catch (Exception e)
        {
            writelog_waimai(typeString+"异常:"+e.getMessage());
        }
        return  false;
    }
    
    /**
     * 解锁
     * @param opType 0表示订转销；1表示订单取消或退单
     * @param eId 企业ID
     * @param orderNo 单号
     * @throws Exception
     */
    public static void  clearWaiMaiOrderToSaleOrRefundRedisLock(String opType,String eId,String orderNo) throws Exception
    {
        String redisKey = orderRedisKeyInfo.redis_OrderRefund+":"+eId+":"+orderNo;
        String redisValue = orderNo;
        String typeString = "";
        if ("0".equals(opType))
        {
            redisKey = orderRedisKeyInfo.redis_OrderToSale+":"+eId+":"+orderNo;
            typeString = "【订转销】";
        }
        else if ("1".equals(opType))
        {
            redisKey = orderRedisKeyInfo.redis_OrderRefund+":"+eId+":"+orderNo;
            typeString = "【取消或退单】";
        }
        else
        {
            return;
        }
        
        try
        {
            RedisPosPub redis = new RedisPosPub();
            redis.DeleteKey(redisKey);
            writelog_waimai(typeString+"【外卖单解锁】成功,主键redisKey="+redisKey);
        }
        catch (Exception e)
        {
            writelog_waimai(typeString+"【外卖单解锁】异常:"+e.getMessage()+",主键redisKey="+redisKey);
        }
    }
    
    /**
     * 如果没有在外卖平台上维护条码，根据平台商品名称查询本地异常商品表对应的条码
     * @param goodsName 平台商品名称
     * @param eId 企业ID
     * @param loadDocType 渠道类型
     * @param channelId 渠道ID
     * @return
     * @throws Exception
     */
    public static String getPluBarcodeByAbnormalGoodsName(String goodsName,String eId,String loadDocType,String channelId) throws  Exception
    {
        String pluBarcode = "";
        try
        {
            String sql = " select GOODBARCODE from dcp_abnormalgood_mapping where EID='"+eId+"' and GOODNAME='"+goodsName+"' "
                    + " and LOADDOCTYPE='"+loadDocType+"' and channelId='"+channelId+"' ";
            writelog_waimai("【平台商品未映射或映射错误】【根据平台商品名称查询本地异常商品资料】，查询资料sql="+ sql);
            List<Map<String,Object>> getPluBarcodes = StaticInfo.dao.executeQuerySQL(sql, null);
            if (getPluBarcodes!=null&&!getPluBarcodes.isEmpty())
            {
                pluBarcode = getPluBarcodes.get(0).getOrDefault("GOODBARCODE","").toString();
            }
            
        }
        catch (Exception e)
        {
        
        }
        
        return pluBarcode;
    }
    
    /**
     * 外卖整单退单同步加工任务单
     * @param eId 企业编码
     * @param orderNo 订单号
     * @param opNo 用户编码
     * @param chatUserId 企业微信id
     * @param errorMessage
     * @throws Exception
     */
    public static void updateProcessTask (String eId,String orderNo,String opNo,String chatUserId,StringBuffer errorMessage) throws Exception
    {
        writelog_waimai("同步加工任务单【开始】，单号orderNo="+orderNo);
        try
        {
            String Crm_channel_KDS_SQL="select * from crm_channel where eid='"+eId+"' and appno='KDS' and status=100";
            List<Map<String, Object>> getData_KDS = StaticInfo.dao.executeQuerySQL(Crm_channel_KDS_SQL,null);
            if (getData_KDS == null && getData_KDS.isEmpty())
            {
                writelog_waimai("同步加工任务单【结束】，未启用，单号orderNo="+orderNo);
                return;
            }
            //查订单单头
            String order_SQL="select * from dcp_order where eid='" + eId + "' and orderno='" + orderNo + "'  ";
            List<Map<String, Object>> getData_Order = StaticInfo.dao.executeQuerySQL(order_SQL,null);
            if (getData_Order==null||getData_Order.isEmpty())
            {
                writelog_waimai("同步加工任务单【结束】，查询订单为空，单号orderNo="+orderNo);
                return;
            }
            String shopId = getData_Order.get(0).getOrDefault("SHOP","").toString();
            String loadDocType = getData_Order.get(0).getOrDefault("LOADDOCTYPE","").toString();
            String channelId = getData_Order.get(0).getOrDefault("CHANNELID","").toString();
            if (shopId==null||shopId.trim().isEmpty())
            {
                writelog_waimai("同步加工任务单【结束】，该订单下单门店为空，单号orderNo="+orderNo);
                return;
            }
            String refundReasonName = getData_Order.get(0).getOrDefault("REFUNDREASONNAME","").toString();
            String modifyBy = opNo;
            //查订单单身
            String ordeDetail_SQL="select a.*,b.category,b.ISDOUBLEGOODS from dcp_order_detail a " +
                    "left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                    "where a.eid='" + eId + "' and a.orderno='" + orderNo + "'  ";
            List<Map<String, Object>> getData_Order_detail= StaticInfo.dao.executeQuerySQL(ordeDetail_SQL,null);
            if (getData_Order_detail==null||getData_Order_detail.isEmpty())
            {
                writelog_waimai("同步加工任务单【结束】，查询订单商品明细为空，单号orderNo="+orderNo);
                return;
            }
            //查订单单身备注
            List<Map<String, Object>> getData_Order_detail_memo = new ArrayList<>();
            if (orderLoadDocType.ELEME.equals(loadDocType)||orderLoadDocType.MEITUAN.equals(loadDocType)||orderLoadDocType.JDDJ.equals(loadDocType)||orderLoadDocType.MTSG.equals(loadDocType)||orderLoadDocType.DYWM.equals(loadDocType))
            {
                //外卖没有商品明细备注
            }
            else
            {
                String ordeDetail_Memo_SQL="select * from dcp_order_detail_memo a " +
                        " where a.eid='" + eId + "' and a.orderno='" + orderNo + "'  ";
                getData_Order_detail_memo = StaticInfo.dao.executeQuerySQL(ordeDetail_Memo_SQL,null);
                
            }
            
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());
            String modifyDateTime = modifyDate+modifyTime;
            List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
            String refundType = "0";
            
            // 配送时间
            String shipDate = getData_Order.get(0).get("SHIPDATE").toString();
            String shipStartTime = getData_Order.get(0).get("SHIPSTARTTIME").toString();
            shipStartTime = shipStartTime.replace("-", "");
            if (shipStartTime.isEmpty()) {
                shipStartTime = new SimpleDateFormat("HHmmss").format(new Date());
            }
            String shipStartDateTime = shipDate + shipStartTime;// 日期格式如"20181223110438"使用yyyyMMddHHmmss
            
            // 如果查询有值 则同步单据状态 修改 ISREFUND = N , refundReasonName 退单原因名称
            //更新原单
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_PROCESSTASK");
            ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(chatUserId, Types.VARCHAR));
            ub1.addUpdateValue("OSTATUS", new DataValue("12", Types.VARCHAR));
            ub1.addUpdateValue("ISREFUND", new DataValue("N", Types.VARCHAR));
            ub1.addUpdateValue("REFUNDREASONNAME", new DataValue(refundReasonName, Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            ub1.addUpdateValue("ISREFUNDORDER", new DataValue(refundType, Types.VARCHAR));
            
            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
            pData.add(new DataProcessBean(ub1));
            
            //更新原单
            ub1 = new UptBean("dcp_product_sale");
            ub1.addUpdateValue("ISREFUNDORDER", new DataValue("0", Types.VARCHAR));
            ub1.addUpdateValue("ISREFUND", new DataValue("N", Types.VARCHAR));
            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
            pData.add(new DataProcessBean(ub1));
            
            //写零售生产单头
            String[] columns_productSale = { "EID",   "SHOPID", "BILLTYPE","BILLNO","OFNO","TRNO",
                    "TABLENO", "REPASTTYPE", "DINNERSIGN", "GUESTNUM","PRODUCTSTATUS", "MEMO",
                    "ISTAKEOUT", "CHANNELID","APPTYPE" ,"LOADDOCTYPE","WXOPENID"
                    ,"ORDERTIME","ADULTCOUNT","ISREFUNDORDER", "SHIPENDTIME","ISBOOK"};
            //
            DataValue[] insValue_productSale = new DataValue[] {
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue("REFUNDORDER", Types.VARCHAR),
                    new DataValue("RE"+orderNo, Types.VARCHAR),
                    new DataValue(orderNo, Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue("2", Types.VARCHAR),
                    new DataValue("", Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEMO").toString(), Types.VARCHAR),
                    new DataValue("Y", Types.VARCHAR),
                    new DataValue(channelId, Types.VARCHAR),
                    new DataValue(loadDocType, Types.VARCHAR),
                    new DataValue(loadDocType, Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("OUTSELID").toString(), Types.VARCHAR),
                    new DataValue(modifyDateTime, Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("MEALNUMBER").toString(), Types.FLOAT),
                    new DataValue("0", Types.VARCHAR),
                    new DataValue(shipStartDateTime, Types.VARCHAR),
                    new DataValue(getData_Order.get(0).get("ISBOOK").toString(), Types.VARCHAR),
            };
            InsBean ib_productSale = new InsBean("dcp_product_sale", columns_productSale);
            ib_productSale.addValues(insValue_productSale);
            pData.add(new DataProcessBean(ib_productSale));
            
            int v_beforeItem=0;
            
            BigDecimal v_TotRemainQty=new BigDecimal("0");
            
            //产生单号
            String v_BeforeProcessTaskNO=getProcessTaskNO(eId,shopId);
            //退单用,处理加回商品数量到新的预制单
            
            String sql_beforDish_UseQty="select a.oitem,nvl(a.useqty,0) USEQTY " +
                    "from DCP_BEFOREDISHTASK a " +
                    "inner join dcp_processtask b on a.eid=b.eid and a.shopid=b.shopid and a.billno=b.processtaskno " +
                    "where a.eid='"+eId+"' " +
                    "and a.shopid='"+shopId+"' " +
                    "and a.ofno='"+orderNo+"' " +
                    "and b.otype='BEFORE' ";
            String sql_processDetail_cookQty=" select a.oitem,nvl(a.pqty,0) PQTY from dcp_processtask_detail a " +
                    "where a.eid='"+eId+"' " +
                    "and a.shopid='"+shopId+"' " +
                    "and a.ofno='"+orderNo+"' " +
                    "and a.goodsstatus in ('2','3') ";
            
            List<Map<String,Object>> getData_beforDish_UseQty=StaticInfo.dao.executeQuerySQL(sql_beforDish_UseQty, null);
            List<Map<String,Object>> getData_processDetail_cookQty=StaticInfo.dao.executeQuerySQL(sql_processDetail_cookQty, null);
            
            
            String[] columns_Processtask_Detail = {
                    "EID", "SHOPID", "ORGANIZATIONNO", "PROCESSTASKNO", "ITEM", "MUL_QTY", "PQTY",  "PUNIT",
                    "BASEQTY", "PLUNO", "PLUNAME", "PRICE", "BASEUNIT", "UNIT_RATIO", "AMT", "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO",
                    "GOODSSTATUS", "FINALCATEGORY", "PLUBARCODE", "AVAILQTY"
            };
            
            
            // 写单头
            String[] columns_Processtask = {
                    "SHOPID", "PROCESSTASKNO", "EID", "ORGANIZATIONNO", "CREATE_TIME", "CREATE_DATE", "CREATEBY",
                    "STATUS", "TOT_CQTY", "PROCESS_STATUS", "BDATE", "TOT_PQTY", "MEMO", "UPDATE_TIME", "WAREHOUSE",
                    "MATERIALWAREHOUSE", "OTYPE", "CREATEDATETIME", "TOT_AMT", "TOT_DISTRIAMT"
            };
            
            
            //退的明细
            for (Map<String, Object> map_order_detail : getData_Order_detail)
            {
                String  item_detail = map_order_detail.get("ITEM").toString();
                //更新原单
                ub1 = new UptBean("DCP_PRODUCT_DETAIL");
                ub1.addUpdateValue("ISREFUNDORDER", new DataValue(refundType, Types.VARCHAR));
                ub1.addUpdateValue("REFUNDQTY", new DataValue(map_order_detail.get("QTY").toString(), Types.VARCHAR));
                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("BILLNO", new DataValue(orderNo, Types.VARCHAR));
                ub1.addCondition("OITEM", new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR));
                pData.add(new DataProcessBean(ub1));
                
                //是否有备注
                StringBuffer dMemoBuffer=new StringBuffer("");
                if (getData_Order_detail_memo != null && getData_Order_detail_memo.size()>0)
                {
                    for (Map<String, Object> map_detail_memo : getData_Order_detail_memo)
                    {
                        String oItem_detail_memo = map_detail_memo.getOrDefault("OITEM","").toString();
                        if (oItem_detail_memo.isEmpty())
                        {
                            continue;
                        }
                        if (oItem_detail_memo.equals(item_detail))
                        {
                            dMemoBuffer.append(map_detail_memo.get("MEMO").toString()+",");
                        }
                        
                    }
                    if (dMemoBuffer.length()>0)
                    {
                        dMemoBuffer.deleteCharAt(dMemoBuffer.length()-1);
                    }
                }
                String dMemo = dMemoBuffer.toString();
                if (dMemo.length()>255)
                {
                    dMemo = dMemo.substring(0,255);//数据库长度255
                }
                
                String packageMitem=map_order_detail.get("PACKAGEMITEM").toString();
                if (Check.Null(packageMitem))
                {
                    packageMitem="0";
                }
                //写零售生产单身
                String[] columns_productSale_detail = { "EID",   "SHOPID", "BILLTYPE","BILLNO","OFNO","OITEM",
                        "PLUNO", "PLUNAME", "PLUBARCODE", "QTY","SPECNAME", "UNITID",
                        "UNITNAME", "FLAVORSTUFFDETAIL","ISPACKAGE" ,"PGOODSDETAIL","GOODSSTATUS"
                        ,"REPASTTYPE","MEMO","ISURGE","FINALCATEGORY","REFUNDQTY","ATTRNAME","PACKAGEMITEM"};
                //
                DataValue[] insValue_productSale_detail = new DataValue[] {
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue("REFUNDORDER", Types.VARCHAR),
                        new DataValue("RE"+orderNo, Types.VARCHAR),
                        new DataValue(orderNo, Types.VARCHAR),
                        new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUNO").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUNAME").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("PLUBARCODE").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("QTY").toString(), Types.FLOAT),
                        new DataValue(map_order_detail.get("SPECNAME").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                        new DataValue(map_order_detail.get("SUNITNAME").toString(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(map_order_detail.get("PACKAGETYPE").toString().equals("2")?"Y":"N", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue(dMemo, Types.VARCHAR),
                        new DataValue("N", Types.VARCHAR),
                        new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR),
                        new DataValue(0, Types.VARCHAR),
                        new DataValue(map_order_detail.get("ATTRNAME").toString(), Types.VARCHAR),
                        new DataValue(packageMitem, Types.VARCHAR),
                };
                InsBean ib_productSale_detail = new InsBean("dcp_product_detail", columns_productSale_detail);
                ib_productSale_detail.addValues(insValue_productSale_detail);
                pData.add(new DataProcessBean(ib_productSale_detail));
                
                
                //加工任务明细，只更新，不新增
                //更新退的字段值
                //这里注意1个问题，A商品2条，第一条4个，第二条2，买6个现在要退5个，要处理成：第一条更新退4个，第二条更新退1个
                String processTask_detail_SQL="select  * from dcp_processtask_detail where eid='"+eId+"' and OFNO='"+orderNo+"' and OITEM="+map_order_detail.get("ITEM").toString()+" order by OITEM ";
                List<Map<String, Object>> getData_processTask_detail =StaticInfo.dao.executeQuerySQL(processTask_detail_SQL,null);
                BigDecimal m_qty=new BigDecimal(map_order_detail.get("RQTY").toString());
                if (getData_processTask_detail!=null&&!getData_processTask_detail.isEmpty())
                {
                    for (Map<String, Object> map_of_detail : getData_processTask_detail)
                    {
                        BigDecimal refundQty=new BigDecimal(0);
                        if (m_qty.compareTo(new BigDecimal(map_of_detail.get("PQTY").toString()))>0)
                        {
                            refundQty=new BigDecimal(map_of_detail.get("PQTY").toString());//此行全退
                            m_qty=m_qty.subtract(refundQty);
                        }
                        else
                        {
                            refundQty=m_qty;//退前面剩余的部分
                            m_qty=new BigDecimal(0);
                        }
                        
                        ub1 = new UptBean("dcp_processtask_detail");
                        ub1.addUpdateValue("ISREFUNDORDER", new DataValue(refundType, Types.VARCHAR));
                        ub1.addUpdateValue("REFUNDQTY", new DataValue(refundQty.doubleValue(), Types.VARCHAR));
                        //condition
                        ub1.addCondition("EID", new DataValue(map_of_detail.get("EID").toString(), Types.VARCHAR));
                        ub1.addCondition("OFNO", new DataValue(map_of_detail.get("OFNO").toString(), Types.VARCHAR));
                        ub1.addCondition("OITEM", new DataValue(map_order_detail.get("ITEM").toString(), Types.VARCHAR));
                        ub1.addCondition("ITEM", new DataValue(map_of_detail.get("ITEM").toString(), Types.VARCHAR));
                        pData.add(new DataProcessBean(ub1));
                    }
                }
                
                
                
                //退单直接产生预制单，把预制菜数量加回去。
                //等于是说普通单子做出多余的也自动算成预制菜，没做的菜就不用做了
                //举例：来单共7份，预制菜2份，任务明细4份做了，1份没做，现在要退5份，怎么办?
                //预制菜份数(占用了，要加回来)+ 做好份数-实际购买的数量
                //公式：预制菜(2)+任务明细做好的(4)-( (来单数量(7)-退单数量(5) )=6-2=4个
                //预制菜占用表还要同BILLNO加工任务单号去关联加工任务单，取预制单的，因为普通的也写这个占用表了
                
                //退菜加回预制菜库存：
                //上面是对原单更新，现在要将预制菜占用及加工好的商品自动产生一张预制菜库存
                
                //双拼菜标记
                String isDoubleGoods = map_order_detail.get("ISDOUBLEGOODS").toString();
                
                //预制菜份数占用
                BigDecimal bdm_o_beforDish_UseQty=new BigDecimal("0");
                if (getData_beforDish_UseQty != null && getData_beforDish_UseQty.size()>0)
                {
                    List<Map<String,Object>> temp_beforDish_UseQty= getData_beforDish_UseQty.stream().filter(p->p.get("OITEM").toString().equals(map_order_detail.get("ITEM").toString())).collect(Collectors.toList());
                    if (temp_beforDish_UseQty != null && temp_beforDish_UseQty.size()>0)
                    {
                        //累加当前oitem对应的数量
                        bdm_o_beforDish_UseQty=temp_beforDish_UseQty.stream().map(p -> new BigDecimal(p.get("USEQTY").toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                }
                //任务明细做好的
                BigDecimal bdm_o_processDetail_cookQty=new BigDecimal("0");
                if (getData_processDetail_cookQty != null && getData_processDetail_cookQty.size()>0)
                {
                    List<Map<String,Object>> temp_processDetail_cookQty= getData_processDetail_cookQty.stream().filter(p->p.get("OITEM").toString().equals(map_order_detail.get("ITEM").toString())).collect(Collectors.toList());
                    if (temp_processDetail_cookQty != null && temp_processDetail_cookQty.size()>0)
                    {
                        //累加当前oitem对应的数量
                        bdm_o_processDetail_cookQty=temp_processDetail_cookQty.stream().map(p -> new BigDecimal(p.get("PQTY").toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                }
                
                //公式计算结果:这里全退
                BigDecimal v_canUseQty=bdm_o_beforDish_UseQty.add(bdm_o_processDetail_cookQty);
                
                //大于0就新产生预制菜明细
                if (v_canUseQty.compareTo(BigDecimal.ZERO)>0)
                {
                    //*************************************双拼菜，使用BOM商品同步************************************
                    if (isDoubleGoods != null && isDoubleGoods.equals("Y"))
                    {
                        //双拼菜数量除除2，因为加工明细里面对应的OITEM有双拼菜主菜+子菜 ，数量翻倍了
                        v_canUseQty=v_canUseQty.divide(new BigDecimal("2"),2,RoundingMode.HALF_UP);
                        
                        StringBuffer sb_bom=new StringBuffer("        select a.bomno,a.bomtype,a.pluno,a.unit,a.mulqty," +
                                "        c.material_pluno,c.qty,c.material_unit,c.material_qty,c.loss_rate,c.isbuckle,c.isreplace,c.sortid," +
                                "        mgl.plu_name as materialPluName,mul.uname as materialUnitName,mu.udlength as materialUnitLength," +
                                "        mg.baseunit as materialBaseUnit,mbul.uname as materialBaseUnitName,mgu.unitratio as materialUnitRatio," +
                                "        mg.isbatch as materialIsBatch,mg.price,mg.category,kc.unside,kc.uncook,kc.uncall, " +
                                "        hqkc.unside hq_unside,hqkc.uncook hq_uncook,hqkc.uncall hq_uncall " +
                                "        from (" +
                                "        select row_number() over (partition by a.pluno,a.unit order by effdate desc) as rn,a.* from dcp_bom a" +
                                "        left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"'" +
                                "        where a.eId='"+eId+"' and a.effdate <=trunc(sysdate) and a.status='100' and a.bomtype = '0'" +
                                "        and a.pluno='"+map_order_detail.get("PLUNO").toString() +"' and a.unit='"+map_order_detail.get("SUNIT").toString()+"'" +
                                "        and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))" +
                                "        )a" +
                                "        inner join dcp_bom_material c on a.bomno=c.bomno and c.eid='"+eId+"' and c.material_bdate <=trunc(sysdate) and material_edate >=trunc(sysdate)" +
                                "        left  join dcp_goods_lang mgl on mgl.eid=a.eid and mgl.pluno=c.material_pluno and mgl.lang_type='zh_CN'" +
                                "        left  join dcp_unit_lang mul on mul.eid=a.eid and mul.unit=c.material_unit and mul.lang_type='zh_CN'" +
                                "        left  join dcp_unit mu on mu.eid=a.eid and mu.unit=c.material_unit and mu.status='100'" +
                                "        inner join dcp_goods mg on mg.eid=a.eid and mg.pluno=c.material_pluno and mg.status='100'" +
                                "        left  join dcp_unit_lang mbul on mbul.eid=a.eid and mbul.unit=mg.baseunit and mbul.lang_type='zh_CN'" +
                                "        inner join dcp_goods_unit mgu on mgu.eid =a.eid and mgu.pluno=c.material_pluno and mgu.ounit=c.material_unit and mgu.unit=mg.baseunit" +
                                "        left join DCP_KDSDISHES_CONTROL kc on a.eid=kc.eid and kc.shopid='"+shopId+"' and c.material_pluno=kc.pluno " +
                                "        left join dcp_hqkdsdishes_control hqkc on a.eid = hqkc.eid and ((hqkc.goodstype=2 and  c.material_pluno=hqkc.id) or (hqkc.goodstype=1 and mg.category=hqkc.id)) " +
                                "        where a.rn=1" +
                                "        order by a.pluno,c.sortid");
                        
                        List<Map<String, Object>> temp_Bom = StaticInfo.dao.executeQuerySQL(sb_bom.toString(), null);
                        
                        if (temp_Bom != null && temp_Bom.size()>0)
                        {
                            for (Map<String, Object> map_Bom : temp_Bom)
                            {
                                
                                v_beforeItem+=1;
                                //累加总的
                                v_TotRemainQty=v_TotRemainQty.add(v_canUseQty);
                                
                                BigDecimal bdm_remainQty=v_canUseQty.multiply(new BigDecimal(Convert.toDouble(map_Bom.get("MATERIAL_QTY").toString(), 1d))).divide(new BigDecimal(Convert.toDouble(map_Bom.get("QTY").toString(), 1d)), 2, RoundingMode.HALF_UP);
                                
                                DataValue[] insValueDetail = new DataValue[]
                                        {
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(shopId, Types.VARCHAR),
                                                new DataValue(shopId, Types.VARCHAR),
                                                new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                                new DataValue(v_beforeItem, Types.VARCHAR),
                                                new DataValue(0, Types.VARCHAR), // 倍量 默认0
                                                new DataValue(bdm_remainQty, Types.VARCHAR), // 数量 以单份维度存储
                                                new DataValue(map_Bom.get("MATERIAL_UNIT").toString(), Types.VARCHAR),
                                                new DataValue(bdm_remainQty.multiply(new BigDecimal(Convert.toStr(map_Bom.get("MATERIALUNITRATIO")))).doubleValue(), Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIAL_PLUNO").toString(), Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIALPLUNAME").toString(), Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(map_Bom.get("MATERIALBASEUNIT").toString(), Types.VARCHAR),
                                                new DataValue("1", Types.VARCHAR), // 单位转换率
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(modifyDate, Types.VARCHAR),//当天
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("3", Types.VARCHAR), // goodsStatus 菜品状态
                                                new DataValue(map_Bom.get("CATEGORY").toString(), Types.VARCHAR), // 末级分类
                                                new DataValue("", Types.VARCHAR), // 条码
                                                new DataValue(bdm_remainQty, Types.VARCHAR) // 剩余可用数量
                                        };
                                InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                                ib1.addValues(insValueDetail);
                                pData.add(new DataProcessBean(ib1));
                            }
                        }
                    }
                    else
                    {
                        v_beforeItem+=1;
                        //累加总的
                        v_TotRemainQty=v_TotRemainQty.add(v_canUseQty);
                        
                        DataValue[] insValueDetail = new DataValue[]
                                {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                        new DataValue(v_beforeItem, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR), // 倍量 默认0
                                        new DataValue(v_canUseQty, Types.VARCHAR), // 数量 以单份维度存储
                                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("QTY").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map_order_detail.get("PLUNAME").toString(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(map_order_detail.get("SUNIT").toString(), Types.VARCHAR),
                                        new DataValue("1", Types.VARCHAR), // 单位转换率
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(modifyDate, Types.VARCHAR),//当天
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("3", Types.VARCHAR), // goodsStatus 菜品状态
                                        new DataValue(map_order_detail.get("CATEGORY").toString(), Types.VARCHAR), // 末级分类
                                        new DataValue(map_order_detail.get("PLUBARCODE").toString(), Types.VARCHAR), // 条码
                                        new DataValue(v_canUseQty, Types.VARCHAR) // 剩余可用数量
                                };
                        InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                        ib1.addValues(insValueDetail);
                        pData.add(new DataProcessBean(ib1));
                    }
                    
                    
                    
                }
                
                
                
                
                
            }
            
            //说明有明细，再添加单头
            if (v_beforeItem >0)
            {
                String sql_out_cost_warehouse="select OUT_COST_WAREHOUSE from DCP_ORG where eid = '"+eId+"' and ORGANIZATIONNO = '"+shopId+"' ";
                List<Map<String, Object>> getOut_cost_warehouse = StaticInfo.dao.executeQuerySQL(sql_out_cost_warehouse, null);
                String out_cost_warehouse = "";
                if(!CollectionUtils.isEmpty(getOut_cost_warehouse)){
                    out_cost_warehouse =  getOut_cost_warehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
                }
                
                DataValue[] insValue = new DataValue[]
                        {
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(modifyTime, Types.VARCHAR),
                                new DataValue(modifyDate, Types.VARCHAR),
                                new DataValue(modifyBy, Types.VARCHAR),
                                new DataValue("6", Types.VARCHAR), // status 默认6
                                new DataValue(v_beforeItem, Types.VARCHAR),
                                new DataValue("N", Types.VARCHAR),
                                new DataValue(modifyDate, Types.VARCHAR),
                                new DataValue(v_TotRemainQty, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR),
                                new DataValue(out_cost_warehouse, Types.VARCHAR),
                                new DataValue(out_cost_warehouse, Types.VARCHAR), // MATERIALWAREHOUSE 原料仓库 取默认出货仓库
                                new DataValue("BEFORE", Types.VARCHAR), // 单据类型 此处为预制单 Before
                                new DataValue(modifyDate+modifyTime, Types.VARCHAR), // 生产日期
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                        };
                
                InsBean ib2 = new InsBean("DCP_PROCESSTASK", columns_Processtask);
                ib2.addValues(insValue);
                pData.add(new DataProcessBean(ib2));
            }
            
            
            
            StaticInfo.dao.useTransactionProcessData(pData);
            writelog_waimai("同步加工任务单【成功】，单号orderNo="+orderNo);
            
        }
        catch (Exception e)
        {
            writelog_waimai("同步加工任务单【异常】:"+e.getMessage()+"，单号orderNo="+orderNo);
        }
        writelog_waimai("同步加工任务单【结束】，单号orderNo="+orderNo);
    }
    
    
    /**
     * 外卖自由双拼商品规格和属性名称拆分(这个方法一定要在套餐展开updateOrderWithPackage方法之后调用)
     * @param dcpOrder
     * @param langType
     * @param errorMessage
     * @throws Exception
     */
    public static void waimaiOrderSplitGoods(order dcpOrder, String langType, StringBuffer errorMessage) throws Exception
    {
        if(errorMessage==null)
        {
            errorMessage = new StringBuffer();
        }
        if(dcpOrder==null)
        {
            errorMessage.append("order对象为null");
            return;
        }
        
        if(langType==null||langType.isEmpty())
        {
            langType = "zh_CN";
        }
        int scaleCount = 2;//默认小数位
        if(langType.equals("zh_TW"))
        {
            scaleCount = 0;
        }
        int scaleCount_qty = 3;//默认数量小数位
        String eId = dcpOrder.geteId();
        String orderNo = dcpOrder.getOrderNo();
        String loadDocType = dcpOrder.getLoadDocType();
        String channelId = dcpOrder.getChannelId();
        String sDate  = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String isApportion = dcpOrder.getIsApportion();//是否已分摊过套餐
        //目前只有美团/饿了么拆分
        if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN))
        {
        }
        else
        {
            errorMessage.append("渠道类型="+loadDocType+"无需处理商品的规格属性拆分！ 单号orderNo="+orderNo);
            return;
        }
        if(dcpOrder.getGoodsList()==null||dcpOrder.getGoodsList().isEmpty())
        {
            writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】渠道类型="+loadDocType+"商品列表为空，无须获取套餐商品， 单号orderNo="+orderNo);
            return;
        }
        if (WaiMaiGoodsSplit==null||WaiMaiGoodsSplit.isEmpty())
        {
            WaiMaiGoodsSplit = PosPub.getPARA_SMS(StaticInfo.dao,eId,"","WaiMaiGoodsSplit");
        }
        
        if (!"Y".equals(WaiMaiGoodsSplit))
        {
            writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】参数值WaiMaiGoodsSplit="+WaiMaiGoodsSplit+"(非Y),无需拆分 单号orderNo="+orderNo);
            return;
        }
        
        writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】开始， 单号orderNo="+orderNo);//先不管效能问题，后续再优化
        
        try
        {
            //原订单商品资料
            List<orderGoodsItem> goodList_origin = dcpOrder.getGoodsList();
            String specIdSqlCondition ="";
            for (orderGoodsItem par : goodList_origin)
            {
                try
                {
                    String pluNo = par.getPluNo();
                    String pluBarcode = par.getPluBarcode();
                    String packageType = par.getPackageType();
                    if (pluNo==null||pluNo.isEmpty()||pluBarcode==null||pluBarcode.isEmpty())
                    {
                        continue;
                    }
                    if ("2".equals(packageType)||"3".equals(packageType))
                    {
                        continue;
                    }
                    //查找规格和属性对应的列表
                    String attrName = par.getAttrName();
                    String specName = par.getSpecName();
                    //饿了么特殊逻辑
                    if (orderLoadDocType.ELEME.equals(loadDocType))
                    {
                        //饿了么取这个节点。
                        attrName = par.getAttrName_origin();
                        specName = par.getSpecName_origin();
                        //饿了么规格和属性 一定都要有值。过滤这些没必要的数据
                        if (specName==null||specName.isEmpty())
                        {
                            continue;
                        }
                    }
                    
                    if(attrName==null||attrName.isEmpty())
                    {
                        continue;
                    }
                    //美团特殊逻辑
                    if (orderLoadDocType.MEITUAN.equals(loadDocType))
                    {
                        //美团一定是要启用的新的商品新增方式，规格和属性都放在属性里面,分割
                        //酸辣鸡杂,拼西红柿炒鸡蛋
                        //酸辣鸡杂,拼酸菜青豆炒肉末
                        if (!attrName.contains(","))
                        {
                            continue;
                        }
                        
                    }
                    specIdSqlCondition = "'"+pluBarcode.replaceAll("'","''")+"'"+","+specIdSqlCondition;//防止sql注入
                    
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
            }
            
            if (specIdSqlCondition.isEmpty())
            {
                writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】没有需要拆分的商品， 单号orderNo="+orderNo);
                return;
            }
            specIdSqlCondition = specIdSqlCondition.substring(0,specIdSqlCondition.length()-1);
            String sql = " select A.EID,A.ID,A.NAME,B.SPECID,B.SPECNAME,C.ATTRVALUE,C.ATTRVALUE_ELM,C.ATTRVALUE_MT,C.PLUBARCODE from Dcp_Wmspgoods A"
                    +" inner join Dcp_Wmspgoods_Spec B ON B.EID=A.EID AND B.ID=A.ID "
                    +" inner join Dcp_Wmspgoods_Attr C ON C.EID=A.EID AND C.ID=A.ID AND C.ATTRNAME='拼' "
                    +" where A.STATUS='100' AND A.EID='"+eId+"' AND B.SPECID IN ("+specIdSqlCondition+") "
                    +" AND C.PLUBARCODE IS NOT NULL AND C.ATTRVALUE IS NOT NULL";
            writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】根据pluBarcode查询sql="+sql+"，单号orderNO="+orderNo);
            List<Map<String, Object>> getSplitDatas = StaticInfo.dao.executeQuerySQL(sql, null);
            if (getSplitDatas == null || getSplitDatas.isEmpty())
            {
                writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】根据pluBarcode查询没有资料，单号orderNO="+orderNo);
                return;
            }
            List<DCP_WMSPGoodsDetailRes.level1Elm> splitDataListById = new ArrayList<>();
            try
            {
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("ID", true);
                List<Map<String, Object>> getSplitDatas_header = MapDistinct.getMap(getSplitDatas, condition);
                condition.put("SPECID",true);
                List<Map<String, Object>> getSplitDatas_spec = MapDistinct.getMap(getSplitDatas, condition);
                condition.clear();
                condition.put("ID", true);
                condition.put("ATTRVALUE", true);
                List<Map<String, Object>> getSplitDatas_attr = MapDistinct.getMap(getSplitDatas, condition);
                DCP_WMSPGoodsDetailRes res = new DCP_WMSPGoodsDetailRes();
                for (Map<String, Object> par : getSplitDatas_header)
                {
                    DCP_WMSPGoodsDetailRes.level1Elm oneLv1 = res.new level1Elm();
                    oneLv1.setSpecDatas(new ArrayList<>());
                    oneLv1.setAttrDatas(new ArrayList<>());
                    String id = par.get("ID").toString();
                    oneLv1.setId(id);
                    oneLv1.setName(par.get("NAME").toString());
                    for (Map<String, Object> par_spec : getSplitDatas_spec)
                    {
                        String id_spec = par_spec.get("ID").toString();
                        if (!id.equals(id_spec))
                        {
                            continue;
                        }
                        DCP_WMSPGoodsDetailRes.levelSpec oneLv1_spec = res.new levelSpec();
                        oneLv1_spec.setSpecId(par_spec.get("SPECID").toString());
                        oneLv1_spec.setSpecName(par_spec.get("SPECNAME").toString());
                        oneLv1.getSpecDatas().add(oneLv1_spec);
                    }
                    
                    for (Map<String, Object> par_attr : getSplitDatas_attr)
                    {
                        String id_attr = par_attr.get("ID").toString();
                        if (!id.equals(id_attr))
                        {
                            continue;
                        }
                        DCP_WMSPGoodsDetailRes.levelAttr oneLv1_attr = res.new levelAttr();
                        oneLv1_attr.setAttrValue(par_attr.get("ATTRVALUE").toString());
                        oneLv1_attr.setAttrValue_elm(par_attr.get("ATTRVALUE_ELM").toString());
                        oneLv1_attr.setAttrValue_mt(par_attr.get("ATTRVALUE_MT").toString());
                        oneLv1_attr.setPluBarcode(par_attr.get("PLUBARCODE").toString());
                        oneLv1.getAttrDatas().add(oneLv1_attr);
                    }
                    
                    splitDataListById.add(oneLv1);
                    
                }
                
            }
            catch (Exception e)
            {
                writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】根据pluBarcode组装数据时异常:"+e.getMessage()+"，单号orderNO="+orderNo);
                return;
            }
            if (splitDataListById==null||splitDataListById.isEmpty())
            {
                writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】根据pluBarcode组装数据时为空，单号orderNO="+orderNo);
                return;
            }
            
            //***********循环之前已经标记过套餐商品的goods开始添加套餐商品 ***********
            String SDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            writelog_waimai("【规格和属性拆分商品】不在重新改写非套餐商品或主商品item项次，单号orderNO="+orderNo);
            //int goodsItem = 0;//项次，有套餐会重新改写
            int goodsPackageItem = goodList_origin.size();//项次，套餐子商品项次，不在重写之前得主商品项次。
            for (orderGoodsItem par : goodList_origin)
            {
                int item_origin = 0;
                try
                {
                    item_origin = Integer.parseInt(par.getItem());
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
                if(item_origin>goodsPackageItem)
                {
                    goodsPackageItem = item_origin;
                }
            }
            writelog_waimai("【查询规格和属性拆分商品】获取当前商品明细的最大项次item="+goodsPackageItem+"，单号orderNO="+orderNo);
            List<orderGoodsItem> goodList_package = new ArrayList<orderGoodsItem>();
            List<orderAbnormal> abnormalList = dcpOrder.getAbnormalList();
            
            for (orderGoodsItem par : goodList_origin)
            {
                try
                {
                    orderGoodsItem goodObj = par;
                    String pluNo = par.getPluNo();
                    String pluBarcode = par.getPluBarcode();
                    String packageType = par.getPackageType();
                    if (pluNo==null||pluNo.isEmpty()||pluBarcode==null||pluBarcode.isEmpty())
                    {
                        goodList_package.add(goodObj);
                        continue;
                    }
                    if ("2".equals(packageType)||"3".equals(packageType))
                    {
                        goodList_package.add(goodObj);
                        continue;
                    }
                    //查找规格和属性对应的列表
                    String attrName = par.getAttrName();
                    String specName = par.getSpecName();
                    if (orderLoadDocType.ELEME.equals(loadDocType))
                    {
                        //饿了么取这个节点。
                        attrName = par.getAttrName_origin();
                        specName = par.getSpecName_origin();
                        //饿了么规格和属性 一定都要有值。过滤这些没必要的数据
                        if (specName==null||specName.isEmpty())
                        {
                            goodList_package.add(goodObj);
                            continue;
                        }
                        
                    }
                    
                    if(attrName==null||attrName.isEmpty())
                    {
                        goodList_package.add(goodObj);
                        continue;
                    }
                    
                    if (orderLoadDocType.MEITUAN.equals(loadDocType))
                    {
                        //美团一定是要启用的新的商品新增方式，规格和属性都放在属性里面,分割
                        //酸辣鸡杂,拼西红柿炒鸡蛋
                        //酸辣鸡杂,拼酸菜青豆炒肉末
                        int indexofSpec = attrName.indexOf(",");
                        if (indexofSpec<0)
                        {
                            goodList_package.add(goodObj);
                            continue;
                        }
                        specName = attrName.substring(0, indexofSpec);
                        attrName = attrName.substring(indexofSpec + 1, attrName.length());
                        if(attrName==null||attrName.isEmpty())
                        {
                            goodList_package.add(goodObj);
                            continue;
                        }
                    }
                    
                    String attrName_pluBarcode = "";
                    //开始根据规格编码plubarcode和属性名称，查找属性对应的条码
                    for (DCP_WMSPGoodsDetailRes.level1Elm oneData : splitDataListById)
                    {
                        List<DCP_WMSPGoodsDetailRes.levelSpec> specDatas = oneData.getSpecDatas();
                        List<DCP_WMSPGoodsDetailRes.levelAttr> attrDatas = oneData.getAttrDatas();
                        if (specDatas==null||specDatas.isEmpty()||attrDatas==null||attrDatas.isEmpty())
                        {
                            continue;
                        }
                        //先匹配规格id,
                        boolean isFindSpecId = false;
                        for (DCP_WMSPGoodsDetailRes.levelSpec oneData_spec : specDatas)
                        {
                            if (pluBarcode.equals(oneData_spec.getSpecId()))
                            {
                                isFindSpecId = true;
                                break;
                            }
                        }
                        //再匹配属性名称，找到属性名称对应的条码
                        boolean isFindAttrValue = false;
                        if (isFindSpecId)
                        {
                            for (DCP_WMSPGoodsDetailRes.levelAttr oneData_attr : attrDatas)
                            {
                                if (attrName.equals(oneData_attr.getAttrValue())||attrName.equals(oneData_attr.getAttrValue_elm())||attrName.equals(oneData_attr.getAttrValue_mt()))
                                {
                                    attrName_pluBarcode = oneData_attr.getPluBarcode();
                                    isFindAttrValue = true;
                                    writelog_waimai("【查询规格和属性拆分商品】已找到属性值对应的条码，当前商品项次item="+goodObj.getItem()+",商品名称pluName="+goodObj.getPluName()+"，商品属性值名称attrName="+attrName+"对应的条码为:"+attrName_pluBarcode+",单号orderNO="+orderNo);
                                    break;
                                }
                            }
                            
                        }
                        
                        //找到了，就不要循环了。
                        if (isFindSpecId&&isFindAttrValue)
                        {
                            break;
                        }
                        
                    }
                    
                    //没找到的话，就返回
                    if (attrName_pluBarcode==null||attrName_pluBarcode.isEmpty())
                    {
                        goodList_package.add(goodObj);
                        continue;
                    }
                    
                    //找到了，就查询下商品资料
                    sql = "SELECT * FROM ("
                            + " SELECT A.PLUBARCODE,A.PLUNO,A.UNIT,A.FEATURENO,FL.FEATURENAME,UL.UNAME,A.STATUS AS STATUS_BARCODE,G.STATUS AS STATUS_PLUNO, "
                            + "NVL(H1.ISLIQUOR, H2.ISLIQUOR) ISLIQUOR, "
                            + "NVL(H1.isKdsShow, H2.isKdsShow) isKdsShow, "
                            + "NVL(H1.ISKDS_CATERING_SHOW, H2.ISKDS_CATERING_SHOW) ISKDSCATERINGSHOW, "
                            + "NVL(H1.KDS_MAX_MAKE_QTY, H2.KDS_MAX_MAKE_QTY) KDSMAXMAKEQTY, "
                            + "NVL(H1.ISQTYPRINT, H2.ISQTYPRINT) ISQTYPRINT, "
                            + "NVL(H1.isPrintReturn, H2.isPrintReturn) isPrintReturn, "
                            + "NVL(H1.isPrintCrossMenu, H2.isPrintCrossMenu) isPrintCrossMenu, "
                            + "NVL(H1.CROSSPRINTERNAME, H2.CROSSPRINTERNAME) CROSSPRINTER, "
                            + "NVL(H1.PRINTERNAME, H2.PRINTERNAME) kitchenPrinter "
                            + " FROM DCP_GOODS_BARCODE A "
                            + " left join  DCP_GOODS_FEATURE_LANG FL on A.EID =FL.EID AND A.PLUNO=FL.PLUNO AND A.FEATURENO=FL.FEATURENO and FL.Lang_Type='"+langType+"' "
                            + " left join dcp_unit_lang UL on A.EID =UL.EID AND A.UNIT=UL.UNIT and UL.Lang_Type='"+langType+"' "
                            + " left join dcp_goods G  on A.EID =G.EID  and A.PLUNO=G.PLUNO "
                            + " LEFT JOIN DCP_KITCHENPRINTSET H1 ON G.EID=H1.EID AND G.PLUNO=H1.ID AND H1.TYPE='GOODS' AND H1.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                            + " LEFT JOIN DCP_KITCHENPRINTSET H2 ON G.EID=H2.EID AND G.Category=H2.ID AND H2.TYPE='CATEGORY' AND H2.SHOPID='"+dcpOrder.getMachShopNo()+"' "
                            + " where  A.EID='"+eId+"' and A.plubarcode ='"+attrName_pluBarcode+"' "
                            + ")";
                    writelog_waimai("【获取拆分后属性名称对应的商品资料】，查询属性名称对应的商品资料sql="+ sql+",单号orderNo="+orderNo);
                    List<Map<String, Object>> getPluInfo = StaticInfo.dao.executeQuerySQL(sql, null);
                    if(getPluInfo==null||getPluInfo.isEmpty())
                    {
                        writelog_waimai("【获取拆分后属性名称对应的商品资料】，查询属性名称对应的商品条码pluBarcode="+ attrName_pluBarcode+"查无资料,单号orderNo="+orderNo);
                        goodList_package.add(goodObj);
                        continue;
                        
                    }
                    
                    //查询到资料
                    String pluNo_attr = getPluInfo.get(0).get("PLUNO").toString();
                    String featureNo_attr = getPluInfo.get(0).get("FEATURENO").toString();
                    String featureName_attr = getPluInfo.get(0).get("FEATURENAME").toString();
                    String unit_attr = getPluInfo.get(0).get("UNIT").toString();
                    String unitName_attr = getPluInfo.get(0).get("UNAME").toString();
                    String status_barcode_attr = getPluInfo.get(0).get("STATUS_BARCODE").toString();
                    String status_pluno_attr = getPluInfo.get(0).get("STATUS_PLUNO").toString();
                    
                    //存储个对象
                    orderGoodsItem  goodsItem_attr = new orderGoodsItem();
                    goodsItem_attr.setDisc(0);
                    goodsItem_attr.setBoxNum(0);
                    goodsItem_attr.setBoxPrice(0);
                    goodsItem_attr.setIsMemo("N");
                    goodsItem_attr.setSpecName("");
                    goodsItem_attr.setAttrName("");
                    
                    goodsItem_attr.setPluName(attrName);//子商品名称=属性名称
                    goodsItem_attr.setPluNo(pluNo_attr);
                    goodsItem_attr.setPluBarcode(attrName_pluBarcode);
                    goodsItem_attr.setFeatureNo(featureNo_attr);
                    goodsItem_attr.setFeatureName(featureName_attr);
                    goodsItem_attr.setsUnit(unit_attr);
                    goodsItem_attr.setsUnitName(unitName_attr);
                    goodsItem_attr.setVirtual("N");
                    goodsItem_attr.setPackageType("3");
                    
                    //这部分是后厨打印，你可以加参数控制不走SQL这些字段
                    goodsItem_attr.setIsLiquor(getPluInfo.get(0).getOrDefault("ISLIQUOR","N").toString());
                    goodsItem_attr.setKdsMaxMakeQty(Convert.toBigDecimal(getPluInfo.get(0).getOrDefault("KDSMAXMAKEQTY", 0), BigDecimal.ZERO));
                    goodsItem_attr.setIsQtyPrint(getPluInfo.get(0).getOrDefault("ISQTYPRINT","N").toString());
                    goodsItem_attr.setIsPrintReturn(getPluInfo.get(0).getOrDefault("ISPRINTRETURN","N").toString());
                    goodsItem_attr.setIsPrintCrossMenu(getPluInfo.get(0).getOrDefault("ISPRINTCROSSMENU","N").toString());
                    goodsItem_attr.setCrossPrinter(getPluInfo.get(0).getOrDefault("CROSSPRINTER","").toString());
                    goodsItem_attr.setKitchenPrinter(getPluInfo.get(0).getOrDefault("KITCHENPRINTER","").toString());
                    
                    
                    writelog_waimai("【获取拆分后属性名称对应的商品资料】，查询属性名称对应的条码pluBarcode="+ attrName_pluBarcode+",对应pluNo="+pluNo_attr+"，对应featureNo="+featureNo_attr+",对应featureName="+featureName_attr+",对应unit="+unit_attr+",对应unitName="+unitName_attr+",单号orderNo="+orderNo);
                    
                    //开始添加2个子商品，
                    //首先本身自己的那个商品就是一个子商品，数量是主商品的1/2,需要把原主商品，变成虚拟商品以及套餐主商品
                    //复制深拷贝一个
                    orderGoodsItem  goodsItem_spec = null;
                    try
                    {
                        goodsItem_spec = PosPub.deepCopy(goodObj);
                        goodsItem_spec.setVirtual("N");
                        goodsItem_spec.setPackageType("3");
                        goodsItem_spec.setSpecName("");
                        goodsItem_spec.setAttrName("");
                        goodsItem_spec.setSpecName_origin("");
                        goodsItem_spec.setAttrName_origin("");
                        goodsItem_spec.setPluName(specName);
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        writelog_waimai("【查询规格和属性拆分商品】,深拷贝对象返回异常："+e.getMessage()+",单号orderNo="+orderNo);
                    }
                    if (goodsItem_spec==null)
                    {
                        goodList_package.add(goodObj);
                        continue;
                    }
                    //region 开始处理逻辑，把主商品拆分成2个子商品，本身自己作为一个子商品，以及属性名称对应一个子商品
                    //主商品 需要要修改成套餐主商品以及虚拟商品
                    goodObj.setPackageType("2");//1、正常商品 2、套餐主商品  3、套餐子商品
                    goodObj.setVirtual("Y");//
                    goodList_package.add(goodObj);
                    isApportion = "Y";
                    
                    String packageMitem = goodObj.getItem();//goodsItem;
                    BigDecimal price_Mitem = new BigDecimal(goodObj.getPrice());//套餐主商品售价
                    BigDecimal disc_Mitem = new BigDecimal(goodObj.getDisc());//套餐主商品总折扣金额
                    BigDecimal qty_Mitem = new BigDecimal(goodObj.getQty());//套餐主商品数量
                    BigDecimal amt_Mitem = new BigDecimal(goodObj.getAmt());//套餐主商品金额
                    
                    //添加规格子商品，属性子商品
                    List<orderGoodsItem> goodsArray_packageMitem = new ArrayList<orderGoodsItem>();
                    goodsArray_packageMitem.add(goodsItem_spec);
                    goodsArray_packageMitem.add(goodsItem_attr);
                    
                    BigDecimal amt_package_item_add = new BigDecimal("0");//子商品 qty合计
                    BigDecimal qty_package_item_add = new BigDecimal("0");////子商品qty合计
                    BigDecimal rate = new BigDecimal("0.5");
                    for (int i=0; i<goodsArray_packageMitem.size();i++)
                    {
                        goodsPackageItem++;//子商品 项次 根据所有主商品 项次之后累加
                        orderGoodsItem itemObj = goodsArray_packageMitem.get(i);
                        itemObj.setVirtual("N");
                        itemObj.setPackageType("3");//1、正常商品 2、套餐主商品  3、套餐子商品
                        itemObj.setItem(goodsPackageItem+"");
                        itemObj.setPackageMitem(packageMitem);
                        itemObj.setSkuId("");
                        itemObj.setAgioInfo(new ArrayList<>());
                        itemObj.setWarehouse(goodObj.getWarehouse());//取套餐主商品
                        itemObj.setWarehouseName(goodObj.getWarehouseName());//取套餐主商品
                        itemObj.setGoodsGroup(goodObj.getGoodsGroup());//取套餐主商品
                        itemObj.setDisc(0);////后面更新
                        itemObj.setBoxNum(0);
                        itemObj.setBoxPrice(0);
                        if (i==goodsArray_packageMitem.size()-1)//最后一笔 用减
                        {
                            BigDecimal qty = qty_Mitem.subtract(qty_package_item_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            BigDecimal amt = amt_Mitem.subtract(amt_package_item_add).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            BigDecimal price_deal = new BigDecimal("0");
                            try
                            {
                                price_deal = amt.divide(qty,scaleCount, BigDecimal.ROUND_HALF_UP);//始终大于 AMT
                                
                            }
                            catch (Exception e)
                            {
                                // TODO: handle exception
                            }
                            itemObj.setAmt(amt.doubleValue());
                            itemObj.setQty(qty.doubleValue());
                            itemObj.setPrice(price_deal.doubleValue());
                            itemObj.setDisc(0);
                            itemObj.setOldAmt(amt.doubleValue());
                            itemObj.setOldPrice(price_deal.doubleValue());
                            
                        }
                        else
                        {
                            BigDecimal qty = qty_Mitem.multiply(rate).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            BigDecimal amt = amt_Mitem.multiply(rate).setScale(scaleCount, BigDecimal.ROUND_HALF_UP);
                            amt_package_item_add = amt_package_item_add.add(amt);
                            qty_package_item_add = qty_package_item_add.add(qty);
                            BigDecimal price_deal = new BigDecimal("0");
                            try
                            {
                                price_deal = amt.divide(qty,scaleCount, BigDecimal.ROUND_HALF_UP);//始终大于 AMT
                            }
                            catch (Exception e)
                            {
                                // TODO: handle exception
                            }
                            itemObj.setAmt(amt.doubleValue());
                            itemObj.setQty(qty.doubleValue());
                            itemObj.setPrice(price_deal.doubleValue());
                            itemObj.setDisc(0);
                            itemObj.setOldAmt(amt.doubleValue());
                            itemObj.setOldPrice(price_deal.doubleValue());
                            
                        }
                        
                        goodList_package.add(itemObj);
                        
                    }
                    //endregion
                    
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】,异常:"+e.getMessage()+",单号orderNO="+orderNo);
                    continue;
                }
            }
            
            dcpOrder.setGoodsList(goodList_package);
            dcpOrder.setIsApportion(isApportion);
            if(isApportion.equals("Y"))
            {
                writelog_waimai("【存在规格和属性拆分商品】更新goods节点信息完成,单号orderNO="+orderNo);
            }
            writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】结束， 单号orderNo="+orderNo);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimai("【获取规格和属性拆分商品资料】【是否需要拆分商品】,异常:"+e.getMessage()+",单号orderNO="+orderNo);
        }
        
    }
    
    /**
     * 订单写缓存（所有门店都写缓存(下单门店、配送门店、生产门店不一致)，不写排除门店缓存)
     * @param dcpOrder
     * @param removeShopNo （排除写缓存的门店编码)
     * @param error
     * @throws Exception
     */
    public static void writeOrderRedisByAllShop(order dcpOrder,String removeShopNo,StringBuffer error) throws Exception
    {
        try
        {
            String shop_create = dcpOrder.getShopNo();
            String shop_shipping = dcpOrder.getShippingShopNo();
            String shop_mach = dcpOrder.getMachShopNo();
            String eId = dcpOrder.geteId();
            String loadDocType = dcpOrder.getLoadDocType();
            String status = dcpOrder.getStatus();
            String redis_key = "";
            String hash_key = dcpOrder.getOrderNo();
            if (removeShopNo!=null&&!removeShopNo.trim().isEmpty())
            {
                if (removeShopNo.equals(shop_create))
                {
                    HelpTools.writelog_waimai("订单orderNo="+hash_key+",渠道类型loadDocType="+loadDocType+",【下单门店】=【排除门店】="+removeShopNo+"，不写缓存");
                    shop_create = "";
                }
                if (removeShopNo.equals(shop_shipping))
                {
                    HelpTools.writelog_waimai("订单orderNo="+hash_key+",渠道类型loadDocType="+loadDocType+",【配送门店】=【排除门店】="+removeShopNo+"，不写缓存");
                    shop_shipping = "";
                }
                if (removeShopNo.equals(shop_mach))
                {
                    HelpTools.writelog_waimai("订单orderNo="+hash_key+",渠道类型loadDocType="+loadDocType+",【生产门店】=【排除门店】="+removeShopNo+"，不写缓存");
                    shop_mach = "";
                }
                if ("12".equals(status))
                {
                    try {
                        RedisPosPub redis = new RedisPosPub();
                        redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + removeShopNo;
                        redis.DeleteHkey(redis_key,hash_key);
                        HelpTools.writelog_waimai("订单orderNo="+hash_key+",渠道类型loadDocType="+loadDocType+",订单已退单成功，【排除门店】删除缓存,"+"redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                    catch (Exception e)
                    {
                    
                    }
                }
                
            }
            
            
            ParseJson pj = new ParseJson();
            String Response_json = pj.beanToJson(dcpOrder);
            
            if(shop_create!=null&&!shop_create.trim().isEmpty())
            {
                redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shop_create;
                HelpTools.writelog_waimai(
                        "渠道类型loadDocType="+loadDocType+",【下单门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                RedisPosPub redis = new RedisPosPub();
                boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                if (nret) {
                    HelpTools.writelog_waimai("【下单门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                } else {
                    HelpTools.writelog_waimai("【下单门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                }
            }
            
            if(shop_shipping!=null&&!shop_shipping.trim().isEmpty()&&!shop_shipping.equals(shop_create))
            {
                redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shop_shipping;
                HelpTools.writelog_waimai(
                        "渠道类型loadDocType="+loadDocType+",【配送门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                RedisPosPub redis = new RedisPosPub();
                boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                if (nret) {
                    HelpTools.writelog_waimai("【配送门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                } else {
                    HelpTools.writelog_waimai("【配送门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                }
            }
            
            //生产门店与其他2个门店不一致
            if(shop_mach!=null&&!shop_mach.trim().isEmpty()&&!shop_mach.equals(shop_shipping)&&!shop_mach.equals(shop_create))
            {
                redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shop_mach;
                HelpTools.writelog_waimai(
                        "渠道类型loadDocType="+loadDocType+",【生产门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                RedisPosPub redis = new RedisPosPub();
                boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                if (nret) {
                    HelpTools.writelog_waimai("【生产门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                } else {
                    HelpTools.writelog_waimai("【生产门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                }
            }
            
            
        }
        catch (Exception e)
        {
            error.append(e.getMessage());
        }
    }
    
    
    /**
     * 在线查询美团订单
     * @param orderNo
     * @param status
     * @param refundStatus
     * @return
     * @throws Exception
     */
    public static order getMTOrderOnline(String orderNo,String status,String refundStatus) throws Exception
    {
        order orderDB = null;
        try
        {
            writelog_waimai("【MT订单取消查询本地订单】不存在！在线查询订单开始，单号="+orderNo);
            StringBuilder errorGetOnlineOrder = new StringBuilder();
            String onlineMTReq = WMMTOrderProcess.getOrderDetail(orderNo,errorGetOnlineOrder);
            if (onlineMTReq==null||onlineMTReq.isEmpty())
            {
                writelog_waimai("【MT订单取消在线查询订单】异常："+errorGetOnlineOrder.toString()+",单号="+orderNo);
                return null;
            }
            String res_json = GetMTResponse(onlineMTReq);
            ParseJson pj = new ParseJson();
            orderDB = pj.jsonToBean(res_json, new TypeToken<order>(){});
            if (status!=null&&!status.isEmpty())
            {
                orderDB.setStatus(status);
            }
            if (refundStatus!=null&&!refundStatus.isEmpty())
            {
                orderDB.setRefundStatus(refundStatus);
            }
            String orderstatus = orderDB.getStatus();// 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
            String eId = orderDB.geteId();
            String shopNo = orderDB.getShopNo();
            String loadDocType = orderDB.getLoadDocType();
            String channelId = orderDB.getChannelId();
            HelpTools.writelog_waimai("【MT更新的单据不存在】开始插入到数据库"+" 订单号orderNo:"+orderNo+" 订单状态status="+orderstatus);
            
            StringBuffer errorMessage = new StringBuffer();
            List<order> orderList = new ArrayList<order>();
            orderList.add(orderDB);
            ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrderCreat(orderList, errorMessage,null);
            
            if (DPB != null && DPB.size() > 0)
            {
                StaticInfo.dao.useTransactionProcessData(DPB);
                HelpTools.writelog_waimai("【MT更新的单据不存在】插入数据库成功"+" 订单号orderNo:"+orderNo+" 订单状态status="+orderstatus);
                //商品资料异常
                HelpTools.waimaiOrderAbnormalSave(orderDB, errorMessage);
                //region 写订单日志
                // 写订单日志
                List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
                orderStatusLog onelv1 = new orderStatusLog();
                onelv1.setLoadDocType(loadDocType);
                onelv1.setChannelId(orderDB.getChannelId());
                onelv1.setLoadDocBillType(orderDB.getLoadDocBillType());
                onelv1.setLoadDocOrderNo(orderDB.getLoadDocOrderNo());
                onelv1.seteId(eId);
                String opNO = "";
                String o_opName = "美团用户";
                
                onelv1.setOpName(o_opName);
                onelv1.setOpNo(opNO);
                onelv1.setShopNo(shopNo);
                onelv1.setOrderNo(orderNo);
                onelv1.setMachShopNo(orderDB.getMachShopNo());
                onelv1.setShippingShopNo(orderDB.getShippingShopNo());
                String statusType = "";
                String updateStaus = orderstatus;
                statusType = "1";// 订单状态
                onelv1.setStatusType(statusType);
                onelv1.setStatus(updateStaus);
                StringBuilder statusTypeNameObj = new StringBuilder();
                String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                String statusTypeName = statusTypeNameObj.toString();
                onelv1.setStatusTypeName(statusTypeName);
                onelv1.setStatusName(statusName);
                
                String memo = "在线查询订单";
                //memo += statusName;
                onelv1.setMemo(memo);
                onelv1.setDisplay("1");
                
                String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                onelv1.setUpdate_time(updateDatetime);
                
                orderStatusLogList.add(onelv1);
                
                StringBuilder errorStatusLogMessage = new StringBuilder();
                boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorStatusLogMessage);
                if (nRet) {
                    HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
                } else {
                    HelpTools.writelog_waimai(
                            "【写表tv_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNo:" + orderNo);
                }
                //endregion
            }
            
        }
        catch (Exception e)
        {
        
        }
        return orderDB;
    }
    
    
    /**
     *解析美团闪购 订单信息
     * @param responseStr
     * @return
     * @throws Exception
     */
    public static String GetSGMTResponse(String responseStr) throws Exception {
        
        if (responseStr == null || responseStr.length() == 0) {
            //writelog_waimaiException("美团外卖发送的请求为空！");
            return null;
        }
        // writelog_waimai("【美团URL转码前】"+responseStr);
        writelog_fileName("【美团闪购URL转码前】"+responseStr,"MTSGRequsetLog");
        // 解析收到的美团外卖请求
        String[] MTResquest = responseStr.split("&");
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_waimaiException("解析美团闪购发送的请求格式有误！");
            return null;
        }
        
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        
        String urlDecodeString ="";
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());//这里收到的是URL数据，需要解码
                //美团需要URL解码2次
                s2 = getURLDecoderString(s2);// 美团需要转码2次 转码
                s2 = getURLDecoderString(s2);// 二次转码（获取为 中文）
                map_MTResquest.put(s1, s2);
                
                urlDecodeString +=s1+"="+s2+"&";//记日志
                
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        writelog_waimai("【美团闪购URL转码后2】"+urlDecodeString);
        
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate = df.format(cal.getTime());
        
        df = new SimpleDateFormat("HHmmss");
        String sTime = df.format(cal.getTime());
        df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String sDateTime = df.format(cal.getTime());
        
        //Map<String, String> map = new HashMap<String, String>();
        order dcpOrder = new order();
        dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
        dcpOrder.setPay(new ArrayList<orderPay>());
        
        String loadDocType = orderLoadDocType.MTSG;// 渠道类型
        String mtOrderStatus = "";//MT订单状态
        
        try {
            String orderid = map_MTResquest.get("order_id").toString();// 订单ID
            // 美团（1：用户已提交订单；2：可推送到APP方平台也可推送到商家；3：商家已收到；4：商家已确认；8：已完成；9：已取消
            String orderStatus = map_MTResquest.get("status").toString();// 订单状态
            mtOrderStatus = orderStatus;
            String app_poi_code = map_MTResquest.get("app_poi_code").toString();// APP方商家ID (企业编号_门店编号 99_10001)
            String shopname = map_MTResquest.get("wm_poi_name").toString();// 美团商家名称
            long dt1 = System.currentTimeMillis();
            Map<String, String>	mappingShopMap = GetSGMTMappingShop(app_poi_code);//查询下门店对应缓存MT_MappingShop
            String eId = mappingShopMap.get("eId");
            String erpshopNo = mappingShopMap.get("erpShopNo");
            String channelId = mappingShopMap.get("channelId");
            String erpShopName = mappingShopMap.getOrDefault("erpShopName", "");
            if (erpShopName==null||erpShopName.isEmpty())
            {
                erpShopName = shopname;
            }
            long dt2 = System.currentTimeMillis();
            long dt_spwn = dt2-dt1;
            if (dt_spwn>=100)
            {
                HelpTools.writelog_waimai("【查询映射门店】耗时:[" + dt_spwn+"]MS，订单orderNo="+orderid);
            }
            writelog_waimai("【MTSG获取对应ERP门店】app_poi_code="+app_poi_code+"-->对应ERP的企业ID="+eId+",门店="+erpshopNo+",门店名称="+erpShopName+",订单号="+orderid);
            
            dcpOrder.seteId(eId);
            dcpOrder.setLoadDocType(loadDocType);
            dcpOrder.setChannelId(channelId);
            dcpOrder.setOrderNo(orderid);//dcp单号=来源单号
            dcpOrder.setLoadDocOrderNo(orderid);//来源单号
            dcpOrder.setLoadDocBillType("");//来源单据类型
            dcpOrder.setOrderShop(app_poi_code);//第三方门店ID
            dcpOrder.setOrderShopName(shopname);;//第三方门店名称
            dcpOrder.setShopNo(erpshopNo);
            dcpOrder.setShopName(erpShopName);
            dcpOrder.setShippingShopNo(erpshopNo);
            dcpOrder.setShippingShopName(erpShopName);
            dcpOrder.setMachShopNo(erpshopNo);
            dcpOrder.setMachShopName(erpShopName);
            
            // 订单中心status
            /*
             * 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货
             * 11.已完成 12.已退单
             */
            
            dcpOrder.setStatus("");
            dcpOrder.setRefundStatus("1");
            if (orderStatus.equals("2"))// 推送已经支付的订单
            {
                dcpOrder.setStatus("1");
            }
            else if (orderStatus.equals("4"))// 门店已接单
            {
                dcpOrder.setStatus("2");
            }
            else if (orderStatus.equals("8"))// 订单已完成
            {
                dcpOrder.setStatus("11");
            }
            else if (orderStatus.equals("9"))// 已取消
            {
                dcpOrder.setStatus("3");
            }
            
            
            dcpOrder.setMemo(map_MTResquest.get("caution"));// string 忌口或备注
            String day_seq = "0";
            if(map_MTResquest.containsKey("day_seq"))
            {
                day_seq = map_MTResquest.get("day_seq").toString();//门店当天的推单流水号，该信息默认不推送，如有需求可在开发者中心订阅
            }
            dcpOrder.setSn(day_seq);// 门店当天的推单流水号
            
            /***********************发票相关处理*******************************/
            orderInvoice dcpOrderInvoiceDetail = new orderInvoice();
            String has_invoiced = map_MTResquest.get("has_invoiced").toString();
            String isInvoice = "N";// 是否开发票
            if (has_invoiced != null && has_invoiced.equals("1")) {
                isInvoice = "Y";
            }
            dcpOrderInvoiceDetail.setIsInvoice(isInvoice);;// 是否开发票
            dcpOrderInvoiceDetail.setInvoiceTitle(map_MTResquest.get("invoice_title"));// 发票抬头
            
            String taxpayer_id = "";
            if(map_MTResquest.containsKey("taxpayer_id"))
            {
                taxpayer_id = map_MTResquest.get("taxpayer_id").toString();//，该信息默认不推送，如有需求可在开发者中心订阅
            }
            dcpOrderInvoiceDetail.setTaxRegNumber(taxpayer_id);// 纳税人识别号
            String peopleType ="2";//发票类型 1.公司 2.个人
            if(taxpayer_id!=null&&taxpayer_id.trim().isEmpty()==false)
            {
                peopleType = "1";
            }
            dcpOrderInvoiceDetail.setPeopleType(peopleType);// 1.公司 2.个人
            dcpOrderInvoiceDetail.setInvoiceType("");// 台湾 二联  三联
            
            dcpOrder.setInvoiceDetail(dcpOrderInvoiceDetail);
            
            String orderCodeView = map_MTResquest.get("wm_order_id_view");
            orderCodeView = getMTOrderIdView(orderCodeView);
            dcpOrder.setOrderCodeView(orderCodeView);
            String ctime = map_MTResquest.get("ctime");// 时间戳秒
            String createDatetime = sDateTime;
            String createDate_order = sDate;
            String createTime_order = sTime;
            try {
                long lt = new Long(ctime);
                Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                createDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
                createDate_order = new SimpleDateFormat("yyyyMMdd").format(date);
                createTime_order = new SimpleDateFormat("HHmmss").format(date);
            } catch (Exception e) {
            
            }
            dcpOrder.setCreateDatetime(createDatetime);// String 创建时间
            
            String longitude ="0";
            String latitude ="0";
            if (map_MTResquest.containsKey("longitude")) {
                longitude = map_MTResquest.get("longitude").toString();// 经度
                
            }
            if (map_MTResquest.containsKey("latitude")) {
                latitude = map_MTResquest.get("latitude").toString();// 纬度
            }
            
            dcpOrder.setLongitude(longitude);
            dcpOrder.setLatitude(latitude);
            
            String deliveryTime = map_MTResquest.getOrDefault("delivery_time","").toString();// 用户预计送达时间，“立即送达”时为0
            String estimate_arrival_time = map_MTResquest.getOrDefault("estimate_arrival_time","").toString();// 订单预计送达时间，为10位秒级的时间戳。
            String shipDate = createDate_order;//配送日期默认下单日期
            String shipTime = createTime_order;//配送时间默认下单时间
            String isBook = "N";
            if (deliveryTime != null && deliveryTime.equals("0") == false) {
                //isBook = "Y";
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                    long lt = new Long(deliveryTime);
                    Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                    shipDate = dateFormat.format(date);
                    shipTime = timeFormat.format(date);
                    
                } catch (Exception e) {
                
                }
                
            }
            
            if(estimate_arrival_time!=null&&!estimate_arrival_time.isEmpty())
            {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                    long lt = new Long(estimate_arrival_time);
                    Date date = new Date(lt * 1000);// 秒转成毫秒 这个函数默认是毫秒时间戳
                    shipDate = dateFormat.format(date);
                    shipTime = timeFormat.format(date);
                    
                } catch (Exception e) {
                
                }
            }
            
            
            String is_pre_order = map_MTResquest.getOrDefault("is_pre_order","").toString();//是否为预订单，true-是，false-否
            if ("true".equalsIgnoreCase(is_pre_order))
            {
                isBook = "Y";
            }
            String is_pre_sale_order = map_MTResquest.getOrDefault("is_pre_sale_order","").toString();//是否为预售单，true-是，false-否。如果为预售单，可以订阅estimate_arrival_time字段获取预计发货时间
            
            dcpOrder.setShipDate(shipDate);
            dcpOrder.setShipStartTime(shipTime);//配送开始时间
            dcpOrder.setShipEndTime(shipTime);//配送结束时间
            
            dcpOrder.setIsBook(isBook);// 是否预订单
            
            String shipType = "1"; // 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
            String isMerPay = "N";//配送费是否商家结算
            
            String logistics_code = map_MTResquest.getOrDefault("logistics_code","").toString();//配送方式  0000	商家自配送
            if(logistics_code!= null&&logistics_code.equals("0000"))
            {
                shipType = "6";//商家自配送
                isMerPay = "Y";
            }
            
            String pickType = map_MTResquest.getOrDefault("pick_type","").toString();// 0：普通取餐；1：到店取餐;
            // 因为pickType节点需要联系美团人员，比较麻烦，所以用送餐地址去判断下，到店自取的送餐地址=
            String recipientAddress = map_MTResquest.get("recipient_address").toString();
            if (recipientAddress != null && recipientAddress.startsWith("到店自取")) {
                shipType = "3";
            }
            
            if (pickType != null && pickType.equals("1")) {
                shipType = "3";
            }
            String order_shipping_address = map_MTResquest.getOrDefault("order_shipping_address", "");
            if (!order_shipping_address.isEmpty())
            {
                recipientAddress = recipientAddress+"("+order_shipping_address+")";
            }
            dcpOrder.setShipType(shipType);// 配送类型1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
            dcpOrder.setIsMerPay(isMerPay);
            dcpOrder.setAddress(recipientAddress);// 收件人地址
            dcpOrder.setContMan(map_MTResquest.get("recipient_name"));// 收件人姓名
            dcpOrder.setGetMan(map_MTResquest.get("recipient_name"));// 收件人姓名
            dcpOrder.setContTel(map_MTResquest.get("recipient_phone"));// String
            dcpOrder.setGetManTel(map_MTResquest.get("recipient_phone"));// String
            dcpOrder.setPayStatus("3");// 1.未支付 2.部分支付 3.付清
            dcpOrder.setsTime(sDateTime);//系统时间 yyyyMMddhhmmssSSS
            
            dcpOrder.setShopShareShipfee(0);// 商家替用户承担的配送费
            dcpOrder.setRefundAmt(0);// 部分退单 的退款金额
            
            
            double tot_oldAmt = 0;
            try
            {
                tot_oldAmt = Double.parseDouble(map_MTResquest.get("original_price"));
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            dcpOrder.setTot_oldAmt(tot_oldAmt);
            double tot_Amt = 0;
            try
            {
                tot_Amt = Double.parseDouble(map_MTResquest.get("total"));
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            dcpOrder.setTot_Amt(tot_Amt);
            double shipFee = 0;
            try
            {
                shipFee = Double.parseDouble(map_MTResquest.get("shipping_fee"));//门店配送费，单位是元。当前订单产生时该门店的配送费（商家自配送运费或美团配送运费），此字段数据为运费优惠前的原价
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            dcpOrder.setShipFee(shipFee);
            
            JSONObject detailObj =  new JSONObject("{\"detail\":" + map_MTResquest.get("detail") + "}");
            
            // 解析goods
            
            JSONArray goodsarray = detailObj.getJSONArray("detail");
            JSONArray array = new JSONArray();
            int item = 0;// 项次
            double packageFee = 0;// 包装费
            try
            {
                packageFee = Double.parseDouble(map_MTResquest.get("package_bag_money"))/100;//订单维度的打包袋金额，单位是分
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            double tot_qty = 0;
            for (int i = 0; i < goodsarray.length(); i++) {
                try {
                    item++;
                    orderGoodsItem goodsItem = new orderGoodsItem();
                    goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                    goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                    
                    JSONObject job = goodsarray.getJSONObject(i);
                    String app_food_code = job.get("app_spu_code").toString();// （原app_food_code字段）APP方商品id，即商家中台系统里商品的编码(spu_code值)：
                    String food_name = job.get("food_name").toString();// 菜品名称
                    String sku_id = job.get("sku_id").toString();// sku编码
                    String quantity_str = job.get("quantity").toString();// 商品数量
                    String price_str = job.get("price").toString();// 商品单价，此字段默认为活动折扣后价格，可在开发者中心订阅是否替换为原价
                    String unit = job.get("unit").toString();// 单位
                    String attr = "";// 菜品属性
                    try
                    {
                        attr = job.get("food_property").toString();//菜品属性，多个属性用半角逗号隔开，该信息默认不推送，如有需求可在开发者中心订阅
                    }
                    catch (Exception e)
                    {
                        attr = "";
                    }
                    
                    String spec = "";// 菜品规格名称，
                    try {
                        spec = job.get("spec").toString();//菜品规格名称，该信息默认不推送，如有需求可在开发者中心订阅
                    } catch (Exception e) {
                        spec = "";
                    }
                    String cart_id = "";
                    int cart_no = 0;
                    try {
                        cart_id = job.get("cart_id").toString();// 商品所在的口袋，0为1号口袋，1为2号口袋
                        cart_no = Integer.parseInt(cart_id) + 1;
                        cart_id = cart_no + "号口袋";
                    } catch (Exception e) {
                        cart_id = "";
                    }
                    
                    double price = 0;
                    double quantity = 0;
                    try {
                        price = Double.parseDouble(price_str);
                    } catch (Exception e) {
                        price = 0;
                    }
                    try {
                        quantity = Double.parseDouble(quantity_str);
                    } catch (Exception e) {
                        quantity = 0;
                    }
                    double amt = price*quantity;
                    tot_qty +=quantity;
                    // 计算餐盒 包装费
                    String box_price_str = job.get("box_price").toString();// 餐盒价格
                    String box_num_str = job.get("box_num").toString();// 餐盒数量
                    double box_price = 0;
                    double box_num = 0;
                    //餐盒数量,在计算餐盒数量和餐盒费用时，请先按照商品规格维度将餐盒数量向上取整后，再乘以相应的餐盒费单价，计算得出餐盒费用。
                    try {
                        box_price = Double.parseDouble(box_price_str);
                    } catch (Exception e) {
                        box_price = 0;
                    }
                    try {
                        box_num = Math.ceil(Double.parseDouble(box_num_str)) ;
                    } catch (Exception e) {
                        box_num = 0;
                    }
                    
                    packageFee += box_price * box_num;
                    
                    goodsItem.setItem(item+"");
                    goodsItem.setPluNo(sku_id);
                    goodsItem.setPluBarcode(sku_id);
                    goodsItem.setSkuId(sku_id);
                    goodsItem.setPluName(food_name);
                    goodsItem.setSpecName(spec);
                    goodsItem.setAttrName(attr);
                    goodsItem.setFeatureNo("");
                    goodsItem.setFeatureName("");
                    goodsItem.setsUnit(unit);
                    goodsItem.setPrice(price);
                    goodsItem.setOldPrice(price);
                    goodsItem.setQty(quantity);
                    goodsItem.setAmt(amt);
                    goodsItem.setOldAmt(amt);
                    goodsItem.setDisc(0);
                    goodsItem.setBoxNum(box_num);
                    goodsItem.setBoxPrice(box_price);
                    goodsItem.setsUnitName(unit);
                    goodsItem.setGoodsGroup(cart_id);
                    goodsItem.setIsMemo("N");
                    
                    dcpOrder.getGoodsList().add(goodsItem);
                    
                }
                catch (Exception e)
                {
                    writelog_waimai("解析MT的detail节点失败：" + e.getMessage());
                    continue;
                }
                
            }
            dcpOrder.setPackageFee(packageFee);// 包装费（MT没有直接返回，需要计算）
            dcpOrder.setTot_qty(tot_qty);
            dcpOrder.setTotQty(dcpOrder.getTot_qty());
            dcpOrder.setLoadDocTypeName("美团闪购");
            dcpOrder.setChannelIdName("美团闪购");
            // 解析extras
            double totDisc = 0;
            double platformDisc = 0;
            double sellerDisc = 0;
            String memo_zengsong= "【买赠】";//赠送得商品，在折扣类型里面，只要一个说明
            boolean isExistZengsong = false;//是否存在买赠
            try {
                JSONObject extrasObj = new JSONObject("{\"extras\":" + map_MTResquest.get("extras") + "}");
                JSONArray extrasarray = extrasObj.getJSONArray("extras");
                //jsonobjresponse.put("extras", extrasarray);
                for (int i = 0; i < extrasarray.length(); i++) {
                    try {
                        JSONObject job = extrasarray.getJSONObject(i);
                        String reduce_fee_str = job.get("reduce_fee").toString();// 活动优惠金额，也即美团承担活动费用和商户承担活动费用的总和
                        String remark = "";
                        String type_mt = "";
                        BigDecimal reduce_fee_B = new BigDecimal("0");
                        try
                        {
                            reduce_fee_B = new BigDecimal(reduce_fee_str);
                        } catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                        
                        //美团瞎搞，目前发现 买赠类型折扣，只写商家承担折扣，也不写总得折扣，而且也没有商品明细，只是加了一个折扣类型type=23
                        if(reduce_fee_B.compareTo(BigDecimal.ZERO)==0)
                        {
                            
                            try
                            {
                                remark = job.get("remark").toString();
                                if (remark!=null&&remark.isEmpty()==false)
                                {
                                    memo_zengsong +=remark+",";
                                    isExistZengsong = true;
                                }
                                
                                type_mt = job.get("type").toString();
                            }
                            catch (Exception e)
                            {
                                // TODO: handle exception
                            }
                            
                            writelog_waimai("存在【买赠】折扣，美团折扣类型type="+type_mt+",折扣描述remark="+remark+",单号orderNo="+orderid);
                            
                            continue;
                        }
                        
                        
                        
                        try {
                            totDisc += Double.parseDouble(reduce_fee_str);
                        } catch (Exception e) {
                            totDisc += 0;
                        }
                        
                        String mt_charge_str = job.get("mt_charge").toString();// 优惠金额中美团承担的部分，该信息默认不推送，如有需求可在开发者中心订阅
                        try {
                            platformDisc += Double.parseDouble(mt_charge_str);
                        } catch (Exception e) {
                            platformDisc += 0;
                        }
                        
                        String poi_charge_str = job.get("poi_charge").toString();// 优惠金额中商家承担的部分，该信息默认不推送，如有需求可在开发者中心订阅
                        try {
                            sellerDisc += Double.parseDouble(poi_charge_str);
                        } catch (Exception e) {
                            sellerDisc += 0;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        writelog_waimai("解析聚宝盆extras异常：" + e.getMessage());
                        continue;
                    }
                }
                
            } catch (Exception e) {
                writelog_waimai("获取聚宝盆extras节点失败：" + e.getMessage());
            }
            
            if(isExistZengsong)
            {
                dcpOrder.setMemo(map_MTResquest.get("caution")+memo_zengsong);
            }
            
            
            dcpOrder.setTotDisc(totDisc);// 优惠总金额
            dcpOrder.setPlatformDisc(platformDisc);// 平台优惠总金额
            //dcpOrder.setSellerDisc(sellerDisc);// 商户优惠总金额
            dcpOrder.setSellerDisc(totDisc-platformDisc);// 商户优惠总金额
            // 解析poiReceiveDetail
            double incomeAmt = 0;
            double serviceCharge = 0;
            double shopIncome_onlinePayment = 0;
            try {
                JSONObject poiReceiveDetail_Res = new JSONObject("{\"poi_receive_detail\":" + map_MTResquest.get("poi_receive_detail") + "}");
                //jsonobjresponse.put("poiReceiveDetail", poiReceiveDetail_Res.get("poi_receive_detail"));
                
                JSONObject poiReceiveDetail = new  JSONObject(map_MTResquest.get("poi_receive_detail").toString());
                String wmPoiReceiveCent_str = poiReceiveDetail.get("wmPoiReceiveCent").toString();// 商家应收款，单位为分
                String logisticsFee_str = poiReceiveDetail.get("logisticsFee").toString();// 用户实际支付配送费  (分)
                try {
                    incomeAmt = Double.parseDouble(wmPoiReceiveCent_str) / 100;
                } catch (Exception e) {
                    incomeAmt = 0;
                }
                
                String foodShareFeeChargeByPoi_str = poiReceiveDetail.get("foodShareFeeChargeByPoi").toString();// 商品分成，即平台服务费，单位为分
                try {
                    serviceCharge = Double.parseDouble(foodShareFeeChargeByPoi_str) / 100;
                } catch (Exception e) {
                    serviceCharge = 0;
                }
                
                String onlinePayment_str = poiReceiveDetail.get("onlinePayment").toString();// 在线支付款，单位为分
                try {
                    shopIncome_onlinePayment = Double.parseDouble(onlinePayment_str) / 100;
                } catch (Exception e) {
                    shopIncome_onlinePayment = 0;
                }
                
                String reconciliationExtras_str = poiReceiveDetail.optString("reconciliationExtras","");
                if (reconciliationExtras_str!=null&&!reconciliationExtras_str.isEmpty())
                {
                    try
                    {
                        JSONObject econciliationExtras = new  JSONObject(reconciliationExtras_str);
                        String chargeMode = econciliationExtras.optString("chargeMode","");//订单服务费的费率模式，枚举：1-美配旧收费模式；2-美配新收费模式；6-闪购企客模式
                        String performanceServiceFee_str = econciliationExtras.optString("performanceServiceFee","0");//配新收费模式&企客配送模式下的订单履约服务费金额，单位元。如订单无履约服务费，则返回0
                        if ("2".equals(chargeMode))
                        {
                            double performanceServiceFee = 0;
                            try {
                                performanceServiceFee = Double.parseDouble(performanceServiceFee_str);
                            } catch (Exception e) {
                                performanceServiceFee = 0;
                            }
                            String serviceCharge_str = serviceCharge+"";//写日志使用
                            serviceCharge = serviceCharge + performanceServiceFee;
                            serviceCharge = new BigDecimal(serviceCharge).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                            writelog_waimai("服务费:"+serviceCharge+"=平台服务费("+serviceCharge_str+")+履约服务费("+performanceServiceFee+"),单号orderNo="+orderid);
                        }
                        
                    }
                    catch (Exception e)
                    {
                    
                    }
                    
                    
                }
                
            } catch (Exception e) {
                writelog_waimai("获取MT的poiReceiveDetail节点失败：" + e.getMessage());
            }
            
            dcpOrder.setIncomeAmt(incomeAmt);// 店铺实际收入
            dcpOrder.setServiceCharge(serviceCharge);// 平台服务费
            dcpOrder.setPayAmt(shopIncome_onlinePayment);// 在线支付款
            
            
            //调用支付方式
            StringBuffer errorPayMessage = new StringBuffer();
            HelpTools.updateOrderPayByMapping(dcpOrder, errorPayMessage);
            
            errorPayMessage = new StringBuffer();
            HelpTools.updateOrderDetailInfo(dcpOrder, errorPayMessage);
            
            HelpTools.updateOrderWithPackage(dcpOrder, "", errorPayMessage);
            
            String status_json = dcpOrder.getStatus();//获取下订单状态
            
            ParseJson pj = new ParseJson();
            String Response_json = pj.beanToJson(dcpOrder);
            
            
            String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + erpshopNo;
            // String hash_key = orderid + "&" + orderStatus;
            String hash_key = orderid;
            try {
                boolean IsUpdateRedis = true;
                RedisPosPub redis = new RedisPosPub();
                if ("4".equals(mtOrderStatus)||"8".equals(mtOrderStatus))
                {
                    //已接单,已完成状态不在写缓存。
                    IsUpdateRedis = false;
                }
                else
                {
                    writelog_waimai("【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
                    boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                    if (isexistHashkey)
                    {
                        if(status_json!=null&&status_json.equals("1"))//新订单的时候，已经存在了，说明缓存已经是最新的状态了，不需要更新缓存
                        {
                            IsUpdateRedis = false;
                            writelog_waimai("【MT订单开立状态】【MT已经存在hash_key的缓存】【说明缓存已经最新状态不用更新缓存】！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                       /* else
                        {
                            redis.DeleteHkey(redis_key, hash_key);//
                            writelog_waimai("【删除存在hash_key的缓存】成功！");
                        }*/
                    }
                    
                }
                if(IsUpdateRedis)
                {
                    boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                    if (nret) {
                        writelog_waimai("【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    } else {
                        writelog_waimai("【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                    }
                    
                }
                //redis.Close();
                
            } catch (Exception e) {
                writelog_waimai(
                        "【写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
            }
            
            return Response_json;
            
        } catch (Exception e) {
            writelog_waimaiException("解析美团闪购发送的请求格式有误！" + e.getMessage());
            return null;
        }
        
    }
    
    
    /**
     * 解析美团闪购 取消信息
     * @param responseStr
     * @return
     * @throws Exception
     */
    public static String GetSGMTCancelResponse(String responseStr) throws Exception
    {
        if (responseStr == null || responseStr.length() == 0) {
            writelog_waimaiException("美团闪购发送的订单取消消息为空！");
            return null;
        }
        try
        {
            writelog_fileName("【美团URL转码前】【取消消息】"+responseStr,"MTSGRequsetLog");
            String[] MTResquest = responseStr.split("&");//
            if (MTResquest == null || MTResquest.length == 0) {
                writelog_waimaiException("解析MTSG取消消息格式有误！");
                return null;
            }
            Map<String, String> map_MTResquest = new HashMap<String, String>();
            String urlDecodeString ="";
            for (String string_mt : MTResquest) {
                try {
                    int indexofSpec = string_mt.indexOf("=");
                    String s1 = string_mt.substring(0, indexofSpec);
                    String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                    /*
                     * String[] ss = string_mt.split("="); //包含多个=会有问题
                     * map_MTResquest.put(ss[0], ss[1]);
                     */
                    s2 = getURLDecoderString(s2);// 二次转码（获取为
                    s2 = getURLDecoderString(s2);
                    
                    urlDecodeString +=s1+"="+s2+"&";//记日志
                    map_MTResquest.put(s1, s2);
                } catch (Exception e) {
                    // TODO: handle exception
                    continue;
                }
            }
            writelog_waimai("【美团URL转码后2】【取消消息】"+urlDecodeString);
            String loadDocType = orderLoadDocType.MTSG;
            String orderid = map_MTResquest.get("order_id");// 订单ID
            String app_poi_code = map_MTResquest.getOrDefault("app_poi_code","");// APP方门店id
            String reasonCode = map_MTResquest.getOrDefault("reason_code","");// 原因码
            String reason = map_MTResquest.getOrDefault("reason","");
            // =1103，已退单
            String deal_op_type = "";//当前订单取消操作人类型，1-用户、 2-商家端 、3-客服、4-BD	、5-系统 、6-开放平台
            
            //查询下数据库
            order orderDB = GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, orderid);
            if(orderDB==null)
            {
                //orderDB = getMTOrderOnline(orderid,"","");
                if (orderDB==null)
                {
                    writelog_waimai("【MTSG订单取消查询本地订单】异常！在线查询订单失败，单号="+orderid);
                    return null;
                }
            }
            try
            {
                
                String eId = orderDB.geteId();
                String shopNo = orderDB.getShopNo();
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                String hash_key = orderid;
                
                String status = "3";
                String refundStatus = "1";
                boolean IsUpdateRedis = true;//是否更新缓存。
                // 原因码 =1103，表示已退单
                if (reasonCode != null && reasonCode.equals("1103")) {
                    status = "12";
                    refundStatus = "6";
                }
                else
                {
                    
                    try
                    {
                        //查询下缓存 如果缓存中是已退单状态，就无需更新缓存了，
                        RedisPosPub redis = new RedisPosPub();
                        boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                        if (isexistHashkey)
                        {
                            //如果存在看下缓存里面状态是不是 已经是退单成功状态
                            String redis_order = redis.getHashMap(redis_key, hash_key);
                            
                            JSONObject redis_order_obj = new JSONObject(redis_order);
                            String	status_redis =  redis_order_obj.optString("status");
                            if(status_redis.equals("12"))//缓存里面已经是退成功状态
                            {
                                IsUpdateRedis = false;
                                status = "12";
                                refundStatus = "6";
                            }
                        }
                        //redis.Close();
                    }
                    catch (Exception e)
                    {
                    }
                    
                    //如果缓存没有，判断下数据库里面，是不是已经是退单状态
                    if(IsUpdateRedis)
                    {
                        try
                        {
                            String status_db =  orderDB.getStatus();//数据库里面订单状态
                            if(status_db.equals("12"))
                            {
                                status = "12";
                                refundStatus = "6";
                            }
                            
                            
                        }
                        catch (Exception e)
                        {
                        
                        }
                        
                    }
                    
                    
                }
                // 更新订单状态
                
                orderDB.setStatus(status);
                orderDB.setRefundStatus(refundStatus);
                orderDB.setRefundReason(reason);
                
                ParseJson pj = new ParseJson();
                String Response_json = pj.beanToJson(orderDB) ;
                if(!IsUpdateRedis)//无需更新缓存
                {
                    writelog_waimai("【MTSG订单取消开始写缓存】【缓存中是已退单状态,无需更新缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                            + " hash_value:" + Response_json);
                    return Response_json;
                }
                
                
                try
                {
                    RedisPosPub redis = new RedisPosPub();
                   /* boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                    if (isexistHashkey) {
                        redis.DeleteHkey(redis_key, hash_key);//
                        writelog_waimai(
                                "【MT删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                    }*/
                    writelog_waimai("【MTSG订单取消开始写缓存】" + "redis_key:" + redis_key + ",hash_key:" + hash_key
                            + " hash_value:" + Response_json);
                    boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                    if (nret) {
                        HelpTools.writelog_waimai(
                                "【MTSG订单取消写缓存】OK" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                    } else {
                        HelpTools.writelog_waimai(
                                "【MTSG订单取消写缓存】Error" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
                    }
                    //redis.Close();
                    
                    
                }
                catch (Exception e)
                {
                    HelpTools.writelog_waimai("更新缓存中MTSG取消消息异常！" + e.getMessage());
                    
                }
                
                return Response_json;
            }
            catch (Exception e)
            {
                writelog_waimai("【MTSG订单取消】异常："+e.getMessage());
                return null;
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimaiException("解析MTSG发送的订单取消消息格式有误！");
            return null;
        }
        
        
        
        
    }
    
    
    /**
     * 解析美团闪购 退款信息
     * @param responseStr
     * @return
     * @throws Exception
     */
    public static String GetSGMTRefundResponse(String responseStr) throws Exception
    {
        if (responseStr == null || responseStr.length() == 0) {
            writelog_waimaiException("美团闪购发送的订单退款消息为空！");
            return null;
        }
        try
        {
            writelog_fileName("【美团URL转码前】【退款消息】"+responseStr,"MTSGRequsetLog");
            String[] MTResquest = responseStr.split("&");//
            if (MTResquest == null || MTResquest.length == 0) {
                writelog_waimaiException("解析MTSG退款消息格式有误！");
                return null;
            }
            Map<String, String> map_MTResquest = new HashMap<String, String>();
            String urlDecodeString ="";
            for (String string_mt : MTResquest) {
                try {
                    int indexofSpec = string_mt.indexOf("=");
                    String s1 = string_mt.substring(0, indexofSpec);
                    String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                    /*
                     * String[] ss = string_mt.split("="); //包含多个=会有问题
                     * map_MTResquest.put(ss[0], ss[1]);
                     */
                    s2 = getURLDecoderString(s2);// 二次转码（获取为
                    s2 = getURLDecoderString(s2);
                    
                    urlDecodeString +=s1+"="+s2+"&";//记日志
                    map_MTResquest.put(s1, s2);
                } catch (Exception e) {
                    // TODO: handle exception
                    continue;
                }
            }
            
            writelog_waimai("【美团URL转码后2】【退款消息】"+urlDecodeString);
            String loadDocType = orderLoadDocType.MTSG;
            String messageType = "1";//1整单退类型、2部分退
            String orderid = map_MTResquest.get("order_id");// 订单ID
            String notify_type = map_MTResquest.getOrDefault("notify_type","");//退款通知类型
            String res_type = map_MTResquest.getOrDefault("res_type","");//退款状态类型
            String status_type = map_MTResquest.getOrDefault("status","");//notify_type不一定推送，需要参考status
            //notify_type参考值：apply-发起退款；agree-确认退款；reject-驳回退款；cancelRefund-用户取消退款申请；cancelRefundComplaint-用户取消退款申诉
            // status参考值：1-已申请 10-初审已同意 11-初审已驳回 16-初审已申诉 17-初审申诉已同意 18-初审申诉已驳回 20-终审已发起（用户已发货） 21-终审已同意 22-终审已驳回 26-终审已申诉 27-终审申诉已同意 28-终审申诉已驳回 30-已取消
            if (notify_type==null||notify_type.isEmpty())
            {
                if ("1".equals(status_type))
                {
                    notify_type = "apply";//部分退可能是part
                }
                else if ("21".equals(status_type)||"27".equals(status_type))
                {
                    //21-终审已同意;27-终审申诉已同意
                    notify_type = "agree";
                }
                else if ("22".equals(status_type)||"28".equals(status_type))
                {
                    //22-终审已驳回;28-终审申诉已驳回
                    notify_type = "reject";
                }
                else if ("30".equals(status_type))
                {
                    //30-已取消
                    notify_type = "cancelRefund";
                }
                else
                {
                
                }
                
            }
            String refund_id = map_MTResquest.getOrDefault("refund_id","");// 本次退款申请的退款id，可用于商家区分多次部分退款。
            String food = "";//部分退单才会有的节点
            double refundMoney = 0;//部分退单才会有的节点
            if(map_MTResquest.containsKey("food"))
            {
                messageType = "2";
                food = map_MTResquest.get("food").toString();
                try
                {
                    refundMoney = Double.parseDouble(map_MTResquest.get("money").toString());
                    
                } catch (Exception e)
                {
                    // TODO: handle exception
                }
                
            }
            
            // =1103，已退单
            String reason = map_MTResquest.getOrDefault("reason","").toString();;
            
            if(messageType.equals("2"))
            {
                writelog_waimai("【解析MT发送消息类型=订单退款消息】【部分退款】" );
            }
            else
            {
                writelog_waimai("【解析MT发送消息类型=订单退款消息】【整单退款】" );
            }
            
            //查询下数据库
            String orderDBJson = "";//GetOrderInfoByOrderNO(StaticInfo.dao,"",  "", "2", orderid);
            order orderDB = GetOrderInfoByOrderNO(StaticInfo.dao,"",  loadDocType, orderid);
            if(orderDB==null)
            {
                writelog_waimai("【MT订单退款查询本地订单】异常！单号="+orderid);
                return null;
            }
            try
            {
                //JSONObject jsonObj = new JSONObject(orderDBJson);
                
                
                String eId = orderDB.geteId();
                String shopNo = orderDB.getShopNo();
                String status_db =  orderDB.getStatus();//数据库里面订单状态
                String refundStatus_db =  orderDB.getRefundStatus();
                String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
                String hash_key = orderid;
                
                String status = "11";// 订单状态1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单	 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
                String refundStatus = "1";// 订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
                if (messageType.equals("1")) //整单退款
                {
                    if (notify_type.equals("apply"))// 发起退款
                    {
                        refundStatus = "2";
                    }
                    else if (notify_type.equals("agree"))// 确认退款
                    {
                        status = "12";
                        refundStatus = "6";
                    }
                    else if (notify_type.equals("reject"))// 驳回退款
                    {
                        refundStatus = "3";
                    }
                    else if (notify_type.equals("cancelRefund"))// 用户取消退款申请
                    {
                        refundStatus = "5";
                    }
                    else if (notify_type.equals("cancelRefundComplaint"))// 取消退款申诉
                    {
                        refundStatus = "5";
                    }
                    else
                    {
                        writelog_waimai("解析MT发送类型=整单退款消息！通知类型异常notifyType= " + notify_type+",退款状态类型res_type="+res_type+",售后单的状态类型status="+status_type);
                        return null;
                    }
                    
                    
                }
                else
                {
                    if (notify_type.equals("part")||notify_type.equals("apply"))// 发起部分退款
                    {
                        refundStatus = "7";
                    }
                    else if (notify_type.equals("agree"))// 确认部分退款
                    {
                        status ="11";
                        refundStatus = "10";
                    }
                    else if (notify_type.equals("reject"))// 驳回部分退款
                    {
                        refundStatus = "8";
                        status = "11";
                    }
                    else if (notify_type.equals("cancelRefund"))// 取消申请部分退款
                    {
                        refundStatus = "9";
                        status = "11";
                    }
                    else if (notify_type.equals("cancelRefundComplaint"))// 取消退款申诉
                    {
                        refundStatus = "9";
                        status = "11";
                    }
                    else
                    {
                        writelog_waimai("解析MT发送类型=部分退款消息！通知类型异常notifyType= " + notify_type+",退款状态类型res_type="+res_type+",售后单的状态类型status="+status_type);
                        return null;
                    }
                    
                    
                }
                
                orderDB.setStatus(status);
                orderDB.setRefundStatus(refundStatus);
                orderDB.setRefundReason(reason);
                orderDB.setRefundAmt(refundMoney);// 部分退单 的退款金额
                
                ParseJson pj = new ParseJson();
                
                if(refundStatus.equals("10"))
                {
                    try
                    {
                        //部分退单的商品
                        JSONObject foodObj =  new JSONObject("{\"food\":" + food + "}");
                        JSONArray partRefundGoodsArray = foodObj.getJSONArray("food");
                        
                        List<orderGoodsItem> goodsArray = orderDB.getGoodsList();
                        List<orderGoodsItem> goodsArray_PartRefund = new ArrayList<orderGoodsItem>();
                        boolean IsExistPartRefundGoods = false; // 检查是不是已经添加过部分退单商品了
                        int partRefundGoodsItem = 999;
                        if (goodsArray != null&&goodsArray.size()>0)
                        {
                            partRefundGoodsItem = goodsArray.size() + 1;
                            for (int j = goodsArray.size() - 1; j >= 0; j--)
                            {
                                //JSONObject oldObj = goodsArray.getJSONObject(j);
                                orderGoodsItem  oldObj =goodsArray.get(j);
                                //String qty_str = oldObj.getString("qty").toString();
                                double qty = oldObj.getQty();
                                
                                
                                if (qty < 0)
                                {
                                    IsExistPartRefundGoods = true;
                                }
                                else
                                {
                                    goodsArray_PartRefund.add(oldObj);
                                    
                                }
                                
                            }
                            
                        }
                        // 循环部分退款的商品，添加到之前的商品（数量为负，金额为负）
                        if (IsExistPartRefundGoods == false)
                        {
                            for (int i = 0; i < partRefundGoodsArray.length(); i++)
                            {
                                try {
                                    
                                    orderGoodsItem goodsItem = new orderGoodsItem();
                                    goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
                                    goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                                    
                                    JSONObject job = partRefundGoodsArray.getJSONObject(i);
                                    
                                    String app_food_code = job.optString("app_spu_code");//（原app_food_code字段 APP方菜品id
                                    String food_name = job.optString("food_name");// 菜品名称
                                    String sku_id = job.optString("sku_id");// sku编码
                                    String item_id = job.optString("item_id");// sku编码
                                    String quantity_str = job.optString("count");// 部分退单商品数量
                                    // 转成负数
                                    String price_str = job.optString("refund_price");// 退款的商品单价，此字段默认为活动折扣后价格
                                    String unit = "";// 单位
                                    if (!job.isNull("unit")) {
                                        unit = job.get("unit").toString();
                                    }
                                    if (unit == null || unit.isEmpty()) {
                                        unit = "份";// 默认个
                                    }
                                    // String food_discount =
                                    // job.get("food_discount").toString();//商品折扣，默认为1，仅美团商家可设置
                                    String attr = "";// 菜品属性 "中辣,微甜"
                                    if (!job.isNull("food_property")) {
                                        attr = job.get("food_property").toString();
                                    }
                                    String spec = job.optString("spec");// 菜品规格名称，
                                    String cart_id = "1号口袋";//默认，可以循环比较，没必要。
                                    
                                    double price = 0;
                                    double quantity = 0; // 部分退单的商品数量为负
                                    try {
                                        price = Double.parseDouble(price_str);
                                    } catch (Exception e) {
                                        price = 0;
                                    }
                                    try {
                                        quantity = 0 - Double.parseDouble(quantity_str);
                                    } catch (Exception e) {
                                        quantity = 0;
                                    }
                                    
                                    double amt = price * quantity;
                                    
                                    // 计算餐盒 包装费
                                    String box_price_str = job.get("box_price").toString();// 餐盒价格
                                    String box_num_str = job.get("box_num").toString();// 餐盒数量
                                    // 部分退单的商品数量为负
                                    double box_price = 0;
                                    double box_num = 0;// 部分退单的商品数量为负
                                    
                                    try {
                                        box_price = Double.parseDouble(box_price_str);
                                    } catch (Exception e) {
                                        box_price = 0;
                                    }
                                    try {
                                        box_num = 0 - Math.ceil(Double.parseDouble(box_num_str));
                                    } catch (Exception e) {
                                        box_num = 0;
                                    }
                                    
                                    goodsItem.setItem(partRefundGoodsItem+"");
                                    goodsItem.setPluNo(sku_id);
                                    goodsItem.setPluBarcode(sku_id);
                                    goodsItem.setSkuId(sku_id);
                                    goodsItem.setPluName(food_name);
                                    goodsItem.setSpecName(spec);
                                    goodsItem.setAttrName(attr);
                                    goodsItem.setFeatureNo("");
                                    goodsItem.setFeatureName("");
                                    goodsItem.setsUnit(unit);
                                    goodsItem.setPrice(price);
                                    goodsItem.setOldPrice(price);
                                    goodsItem.setQty(quantity);
                                    goodsItem.setAmt(amt);
                                    goodsItem.setOldAmt(amt);
                                    goodsItem.setDisc(0);
                                    goodsItem.setBoxNum(box_num);
                                    goodsItem.setBoxPrice(box_price);
                                    goodsItem.setsUnitName(unit);
                                    goodsItem.setGoodsGroup(cart_id);
                                    goodsItem.setIsMemo("N");
                                    partRefundGoodsItem++;
                                    goodsArray_PartRefund.add(goodsItem);
                                    
                                }
                                catch (Exception e)
                                {
                                    writelog_waimai("解析MTSG部分退款food节点点失败：" + e.getMessage());
                                    continue;
                                }
                                
                            }
                            
                            orderDB.setGoodsList(goodsArray_PartRefund);
                        }
                        
                        
                    }
                    catch (Exception e)
                    {
                        
                        writelog_waimai("添加MT部分退款food节点点失败：" + e.getMessage());
                    }
                    
                }
                
                String Response_json = pj.beanToJson(orderDB);
                
                try
                {
                    boolean IsUpdateRedis = true;//是否更新缓存。
                    RedisPosPub redis = new RedisPosPub();
                    boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
                    if (isexistHashkey)
                    {
                        //如果存在看下缓存里面状态是不是 已经是退单成功状态
                        String redis_order = redis.getHashMap(redis_key, hash_key);
                        try
                        {
                            JSONObject redis_order_obj = new JSONObject(redis_order);
                            String	status_redis =  redis_order_obj.optString("status");
                            String	refundStatus_redis =  redis_order_obj.optString("refundStatus");
                            if(refundStatus.equals("2"))
                            {
                                if(status_redis.equals("12"))//缓存里面已经是退成功状态
                                {
                                    IsUpdateRedis = false;
                                }
                            }
                            else	if(refundStatus.equals("7"))
                            {
                                if(refundStatus_redis.equals("10"))////缓存里面已经是退成功状态
                                {
                                    IsUpdateRedis = false;
                                }
                            }
                            
                        }
                        catch (Exception e)
                        {
                        }
                        
                        
                        if(IsUpdateRedis)
                        {
                            redis.DeleteHkey(redis_key, hash_key);
                            writelog_waimai(
                                    "【MT删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        
                    }
                    
                    
                    //这里对比下数据库状态
                    //MT可能先推送退单成功状态，再推送申请退单状态
                    if(refundStatus.equals("2"))
                    {
                        if(status_db.equals("12"))
                        {
                            IsUpdateRedis = false;
                        }
                    }
                    else	if(refundStatus.equals("7"))
                    {
                        if(refundStatus_db.equals("10"))
                        {
                            IsUpdateRedis = false;
                        }
                    }
                    
                    
                    if(IsUpdateRedis)
                    {
                        
                        writelog_waimai("【MT订单退款开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                + " hash_value:" + Response_json);
                        boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
                        if (nret) {
                            HelpTools.writelog_waimai(
                                    "【MT订单退款写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        } else {
                            HelpTools.writelog_waimai(
                                    "【MT订单退款写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
                        }
                        
                    }
                    else
                    {
                        writelog_waimai("【MT订单退款开始写缓存】【无需写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key
                                + " hash_value:" + Response_json+" 数据库中订单status="+status_db+" refundStatus="+refundStatus_db);
                        
                    }
                    
                    
                    
                    //redis.Close();
                    
                    
                }
                catch (Exception e)
                {
                    HelpTools.writelog_waimai("更新缓存中订单退款消息异常！" + e.getMessage());
                }
                
                return Response_json;
            }
            catch (Exception e)
            {
                writelog_waimai("【MT订单取消】异常："+e.getMessage());
                return null;
            }
            
        }
        catch (Exception e)
        {
            // TODO: handle exception
            writelog_waimaiException("解析美团发送的订单取消消息格式有误！");
            return null;
        }
        
        
        
        
    }
    
    /**
     * 解析美团闪购，配送状态信息
     * @param responseStr
     * @return
     * @throws Exception
     */
    public static String GetSGMTShippingResponse(String responseStr) throws Exception {
        if (responseStr == null || responseStr.length() == 0) {
            return null;
        }
        writelog_fileName("【美团URL转码前】【配送消息】"+responseStr,"MTSGRequsetLog");
        String[] MTResquest = responseStr.split("&");//
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_waimaiException("解析MTSG配送状态消息格式有误！");
            return null;
        }
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        String urlDecodeString ="";
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                /*
                 * String[] ss = string_mt.split("="); //包含多个=会有问题
                 * map_MTResquest.put(ss[0], ss[1]);
                 */
                s2 = getURLDecoderString(s2);// 二次转码（获取为
                s2 = getURLDecoderString(s2);
                
                urlDecodeString +=s1+"="+s2+"&";//记日志
                map_MTResquest.put(s1, s2);
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        
        writelog_waimai("【美团URL转码后2】【配送消息】"+urlDecodeString);
        
        String companyno = "99";
        String erpshopno = " ";
        
        String order_id = map_MTResquest.get("order_id");
        String logistics_status = map_MTResquest.get("logistics_status");
        String app_poi_code = map_MTResquest.getOrDefault("app_poi_code","");
        String time = map_MTResquest.getOrDefault("time","");
        String dispatcher_name = map_MTResquest.getOrDefault("dispatcher_name","");
        String dispatcher_mobile = map_MTResquest.getOrDefault("dispatcher_mobile","");
        
        JSONObject obj = new JSONObject();
        
        obj.put("order_id", order_id);
        obj.put("app_poi_code", app_poi_code);
        obj.put("logistics_status", logistics_status);
        obj.put("dispatcher_name", dispatcher_name);
        obj.put("dispatcher_mobile", dispatcher_mobile);
        String Response_json = obj.toString();
        return Response_json;
        
    }
    
    /**
     * 解析美团闪购，配送异常信息
     * @param responseStr
     * @return
     * @throws Exception
     */
    public static String GetSGMTShippingExceptionResponse(String responseStr) throws Exception {
        if (responseStr == null || responseStr.length() == 0) {
            return null;
        }
        writelog_fileName("【美团URL转码前】【配送异常消息】"+responseStr,"MTRequsetLog");
        String[] MTResquest = responseStr.split("&");//
        if (MTResquest == null || MTResquest.length == 0) {
            writelog_waimaiException("解析MTSG配送异常消息格式有误！");
            return null;
        }
        Map<String, String> map_MTResquest = new HashMap<String, String>();
        String urlDecodeString ="";
        for (String string_mt : MTResquest) {
            try {
                int indexofSpec = string_mt.indexOf("=");
                String s1 = string_mt.substring(0, indexofSpec);
                String s2 = string_mt.substring(indexofSpec + 1, string_mt.length());
                /*
                 * String[] ss = string_mt.split("="); //包含多个=会有问题
                 * map_MTResquest.put(ss[0], ss[1]);
                 */
                s2 = getURLDecoderString(s2);// 二次转码（获取为
                s2 = getURLDecoderString(s2);
                
                urlDecodeString +=s1+"="+s2+"&";//记日志
                map_MTResquest.put(s1, s2);
            } catch (Exception e) {
                // TODO: handle exception
                continue;
            }
        }
        
        writelog_waimai("【美团URL转码后2】【配送异常消息】"+urlDecodeString);
        
        String companyno = "99";
        String erpshopno = " ";
        
        String order_id = map_MTResquest.get("order_view_id");
        String app_poi_code = map_MTResquest.getOrDefault("app_poi_code","");
        String time = map_MTResquest.getOrDefault("time","");
        String exception_reason = map_MTResquest.get("exception_reason");
        
        JSONObject obj = new JSONObject();
        
        obj.put("order_id", order_id);
        obj.put("app_poi_code", app_poi_code);
        obj.put("exception_reason", exception_reason);
        String Response_json = obj.toString();
        return Response_json;
        
    }
    
    /**
     * 返回饿了么门店id对应的appkey、appsecret的配置
     * @param app_poi_code
     * @return
     */
    public static Map<String, String> GetELMShopIdConfig(String app_poi_code)
    {
        Map<String, String> mappingshopMap = new HashMap<String, String>();
        //String mappingshop_redis_key = orderRedisKeyInfo.redisKey_elemeMappingshop;
        //String mappingshop_hash_key = app_poi_code;
        String eid = "99";
        String shopno = "";
        String channelId = "";
        String shopName = "";
        String appKey = "";
        String appSecret = "";
        String appName = "";
        String isTest = "";
        String isJbp = "";
        String userId = "";
        boolean needQueryDB = false;//是否需要从数据库查询
        boolean needUpdateMapList = false;//是否需要更新内存中数据
        try
        {
            long dt1 = System.currentTimeMillis();
            //RedisPosPub redis = new RedisPosPub();
            //String mappingshop = redis.getHashMap(mappingshop_redis_key, mappingshop_hash_key);
            Map<String,String> mappingshop = null;
            if (elmShopIdConfigList!=null&&!elmShopIdConfigList.isEmpty())
            {
                mappingshop = elmShopIdConfigList.get(app_poi_code);
            }
            //redis.Close();
            
            if (mappingshop != null && mappingshop.isEmpty() == false)
            {
               /* JSONObject obj_mappingshop = new JSONObject(mappingshop);
                eid = obj_mappingshop.get("eId").toString();
                shopno = obj_mappingshop.get("erpShopNo").toString();
                channelId = obj_mappingshop.optString("channelId","");
                shopName = obj_mappingshop.optString("erpShopName","");*/
                long dt2 = System.currentTimeMillis();
                long dt_spwn = dt2-dt1;
                if (dt_spwn>=100)
                {
                    writelog_waimai("【内存中查询映射门店】耗时:[" + dt_spwn+"]MS，平台门店="+app_poi_code);
                }
                return mappingshop;
            }
            else
            {
                needQueryDB = true;
            }
            
        }
        catch (Exception e)
        {
            needQueryDB = true;
        }
        
        
        if(needQueryDB)
        {
            try
            {
                Map<String, Object> getData = GetWaimaiMappingShopFromDB(orderLoadDocType.ELEME, app_poi_code);
                if(getData!=null)
                {
                    eid = getData.get("EID").toString();
                    shopno = getData.get("SHOPID").toString();
                    channelId = getData.get("CHANNELID").toString();
                    shopName = getData.get("SHOPNAME").toString();
                    appKey = getData.get("APPKEY").toString();
                    appSecret = getData.get("APPSECRET").toString();
                    appName = getData.get("APPNAME").toString();
                    isTest = getData.get("ISTEST").toString();
                    isJbp = getData.get("ISJBP").toString();
                    userId = getData.getOrDefault("USERID","").toString();
                    needUpdateMapList = true;
                }
                
            }
            catch (Exception e)
            {
            
            
            }
        }
        mappingshopMap.put("eId", eid);
        mappingshopMap.put("erpShopNo", shopno);
        mappingshopMap.put("channelId", channelId);
        mappingshopMap.put("erpShopName", shopName);
        mappingshopMap.put("appKey", appKey);
        mappingshopMap.put("appSecret", appSecret);
        mappingshopMap.put("appName", appName);
        mappingshopMap.put("isTest", isTest);
        mappingshopMap.put("isJbp", isJbp);
        mappingshopMap.put("userId", userId);
        if (needUpdateMapList)
        {
            if (elmShopIdConfigList==null)
            {
                elmShopIdConfigList = new HashMap<>();
            }
            elmShopIdConfigList.put(app_poi_code,mappingshopMap);
        }
        
        return mappingshopMap;
    }
    
    public static String getProcessTaskNO(String eId,String shopId) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String processTaskNO = null;
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','JGRW') PROCESSTASKNO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
        
        if (getQData != null && getQData.isEmpty() == false)
        {
            processTaskNO = (String) getQData.get(0).get("PROCESSTASKNO");
        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return processTaskNO;
    }
    
    public static String getDeliverShopId(String eId,String appid,String shopId) throws Exception {
    	String deliverShopId="";
//    	String sql=" SELECT A.*,B.SHOPID AS BSHOPID,B.DELIVERYSHOPID AS BDELIVERYSHOPID FROM CRM_EXPRESSSET A "
//        		+ " LEFT JOIN CRM_EXPRESSSET_DETAIL B ON A.EID=B.EID AND A.APPID=B.APPID"
//                + " WHERE A.EID='"+eId+"' and A.APPID='"+appid+"' and EXPRESS='1' ";
//        List<Map<String, Object>> getDeliverShop = StaticInfo.dao.executeQuerySQL(sql, null,false);
//        if (!CollectionUtils.isEmpty(getDeliverShop)){
//        	String deliverytype=getDeliverShop.get(0).get("DELIVERYTYPE").toString();
//        	if("3".equals(deliverytype)){
//        		if(shopId!=null&&shopId.length()>0){
//        			List<Map<String, Object>> maps1 = getDeliverShop.stream().filter(g->(g.get("BSHOPID")==null?"":g.get("BSHOPID").toString()).equals(shopId)).collect(Collectors.toList());
//        			if(maps1!=null&&maps1.size()==1){
//        				deliverShopId = maps1.get(0).get("BDELIVERYSHOPID").toString();
//        			}
//        			if(maps1==null||maps1.size()<1){
//        				deliverShopId = getDeliverShop.get(0).get("DELIVERYSHOPID")==null?"":getDeliverShop.get(0).get("DELIVERYSHOPID").toString();
//        			}
//        		}
//        	}else if("2".equals(deliverytype)){
//        		deliverShopId = getDeliverShop.get(0).get("DELIVERYSHOPID")==null?"":getDeliverShop.get(0).get("DELIVERYSHOPID").toString();
//        	}
//        }
    	
    	if(!Check.Null(shopId)){
    		StringBuffer sqlbuf = new StringBuffer();
    		sqlbuf.append(" SELECT A.* ");
    		sqlbuf.append(" FROM DCP_ORG_ORDERSET A ");
    		sqlbuf.append(" WHERE A.EID='"+eId+"' and A.ORGANIZATIONNO='"+shopId+"' ");
            List<Map<String, Object>> getDeliverShop = StaticInfo.dao.executeQuerySQL(sqlbuf.toString(), null,false);
            if (getDeliverShop!=null&&getDeliverShop.size()>0){
            	deliverShopId=getDeliverShop.get(0).get("DELIVERSHOP")==null?"":getDeliverShop.get(0).get("DELIVERSHOP").toString();
            }
    	}
    	
    	if(Check.Null(deliverShopId)&&!Check.Null(appid)){
    		String sql=" SELECT A.* FROM CRM_EXPRESSSET A ";
    		sql+= " WHERE A.EID='"+eId+"' and A.APPID='"+appid+"' and A.EXPRESS='1' ";
            List<Map<String, Object>> getDeliverShop = StaticInfo.dao.executeQuerySQL(sql, null,false);
            if (getDeliverShop!=null&&getDeliverShop.size()>0){
            	//DELIVERYTYPE	NUMBER(38,0)	Yes		13	发货方式1-门店自发货2-统一指定门店发货 3-门店指定发货
            	String deliverytype=getDeliverShop.get(0).get("DELIVERYTYPE").toString();
            	if("2".equals(deliverytype)){
            		deliverShopId = getDeliverShop.get(0).get("DELIVERYSHOPID")==null?"":getDeliverShop.get(0).get("DELIVERYSHOPID").toString();
            	}else if("1".equals(deliverytype)){
            		deliverShopId=shopId;
            	}
            }
    	}
    	return deliverShopId;
    }
    
    
}
