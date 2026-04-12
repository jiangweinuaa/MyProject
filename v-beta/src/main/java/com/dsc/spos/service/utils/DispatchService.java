package com.dsc.spos.service.utils;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.ExecuteService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 調用對應的 service.
 *
 * @author Xavier
 */
public class DispatchService {
    //类静态锁，可以设置超时时间
    static ReentrantLock reentrantLock = new ReentrantLock();

    Logger logger = LogManager.getLogger(DispatchService.class);
    private static DispatchService ds;
    private static final String FIND_SERVICE_PATH = "/com/dsc/spos/service/imp/json";

    //可查找 service 的路徑, 支持多路徑
    private List<String> regServicePath = new ArrayList<String>();

    private DispatchService() {
        this.regServicePath.add("pc.com.dsc.spos.service.imp.json");
        this.regServicePath.add("svip.com.dsc.spos.service.imp.json");
        this.regServicePath.add("com.dsc.spos.service.imp.json");
    }

    public static DispatchService getInstance() {
        if (ds == null) {
            ds = new DispatchService();
        }
        return ds;
    }

    /**
     * call 相關服務
     *
     * @param json
     * @return
     */
    public String callService(String json, DsmDAO dao) {
        ExecuteService es = null;
        ParseJson pj = new ParseJson();
        Map<String, Object> mapJson = new HashMap<String, Object>();

        EncryptUtils eu = new EncryptUtils();
        String md5Key = eu.encodeMD5(json);
        eu = null;

        mapJson.put(md5Key, "1");//

        //
        String serviceName = "";
        long start = System.currentTimeMillis();
        try {
            //转义
            //json = json.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
            //json = json.replaceAll("\\+", "%2B");  //+

            //兼容以前json=的方式
            if (json.startsWith("json=")) //URL方式需要解码,Body方式不需要解码
            {
                json = json.substring(5);
                json = URLDecoder.decode(json, "UTF-8");
            }

            //分析 json
            JsonBasicReq req = pj.jsonToBean(json, new TypeToken<JsonBasicReq>() {
            });
            serviceName = req.getServiceId(); //取得將要執行的服務.
            //logger.info("\r\n" + "服务" + serviceName + "传入参数：  " + json + "\n");
            if (!serviceName.equals("DCP_OrderQueryBuffer_Open")) {
                logger.info("服务" + serviceName + "传入参数：  " + json);
            }
//            else
//            {
//            	logger.debug("服务" + serviceName + "传入参数：  " + json);
//            }
            //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"*****服务"+serviceName+"传入参数：  "+json+"*****\r\n");

            if (serviceName.equals("OrderGet")) {
                JSONObject myJsonObject = new JSONObject(json);
                try {
                    String status = myJsonObject.optString("status").toString(); //myJsonObject.getString("status");
                    String shopId = myJsonObject.optString("oShopId").toString();// myJsonObject.get("oShopId").toString();
                    boolean IsPageSize = myJsonObject.isNull("pageNumber");//如果传了分页，就不走缓存
                    if (status.equals("1") && shopId.equals("ALL") == false && IsPageSize) {
                        try {


                        } catch (Exception e) {

                        }


                        logger.info("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "" + "*****服务" + serviceName + "传入参数：  " + json + "*****\r\n");
                        String waimaiRes = HelpTools.GetOrderByBuffer(json);
                        logger.info("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "服务" + serviceName + "响应结果：  成功！（数据太多不写具体日志了）！*****\r\n");
                        return waimaiRes;
                    }

                } catch (Exception e) {
                    logger.info("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "" + "*****服务" + serviceName + "异常：  " + e.getMessage() + "*****\r\n");
                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,e.getMessage());
                }

            }

            if (StaticInfo.Using_Redis.equals("1")) {
                //啥也不干
            }

            //執行 服務
            es = this.getService(serviceName);
            String serviceName_Open = es.getClass().getSimpleName();

            /*
             * OrderGet 订单中心查询
             * OrderProcess  OrderReturnProcess OrderUpdate  微商城订单处理
             * OrderCreate OrderModify 云pos门店请求创建订单
             * OrderAgreeProcess OrderRejectProcess 外卖订单处理
             */
            boolean noExistToken = false;
            if (serviceName.startsWith("ISV_") || serviceName.startsWith("DCP_OrderModify_") || serviceName.equals("DCP_OrderCreateCancle_Open") || serviceName.equals("DCP_OrderSaleCancle_Open") || serviceName.equals("DCP_OrderDeliveryProcess") || serviceName.equals("DCP_OrderRedisProcess") || serviceName.equals("DCP_OrgOrderUpdate") || serviceName.equals("DCP_CRegisterDelete") || serviceName.equals("DCP_OrderQuery") || serviceName.equals("DCP_OrderShopStatusUpdate") || serviceName.equals("DCP_OrderShopStatusQuery") || serviceName.equals("DCP_ShopInfoExQuery") || serviceName.equals("DCP_GoodsSetExQuery") || serviceName.equals("DCP_OrderStatusLogQuery") || serviceName.equals("DCP_OrderStatusLogCreate") || serviceName.equals("DCP_StockB2BQuery") || serviceName.equals("DCP_OrderProcess") || serviceName.equals("DCP_OrderReturnProcess") || serviceName.equals("DCP_OrderUpdate") || serviceName.equals("DCP_OrderCreate") || serviceName.equals("DCP_OrderAgreeProcess") || serviceName.equals("DCP_OrderRejectProcess") || serviceName.equals("DCP_OrderStatusUpdate") || serviceName.equals("DCP_OrderStatusUpdateERP") || serviceName.equals("DCP_RequisitionUpdate") || serviceName.equals("DCP_OrderStatusUpdateJh") || serviceName.equals("DCP_OrderQueryBuffer") || serviceName.equals("DCP_OrderDelteBuffer") || serviceName.equals("DCP_PayInCheck") || serviceName.equals("DCP_OrderDeliveryQuery") || serviceName.equals("DCP_OrderPrintErrorSaveBuffer") || serviceName.equals("DCP_OrderPrintErrorQueryBuffer")) {
                noExistToken = true;
            }

            //外部接口不用token
            if (serviceName.endsWith("_Open")) {
                noExistToken = true;
            }

            if (noExistToken || serviceName.equals("DCP_HolidayGoodsSync") || serviceName.equals("DCP_SaleCreate")
//                    ||serviceName.equals("DCP_ReceivingCreate")
                    || serviceName.equals("DCP_StockTaskCreate")
                    || serviceName.equals("DCP_StockOutErpUpdate") || serviceName.equals("DCP_DayEndCheck")
                    || serviceName.equals("DCP_AdjustCreate") || serviceName.equals("DCP_CRegisterCheck")
                    || serviceName.equals("DCP_SRegister") || serviceName.equals("DCP_TransferCreate")
                    || serviceName.equals("DCP_UpErrorLogQuery") || serviceName.equals("DCP_Register")
                    || serviceName.equals("DCP_RegisterCreate") || serviceName.equals("DCP_RegisterQuery")
                    || serviceName.equals("DCP_VersionQuery") || serviceName.equals("DCP_LStockOutErpUpdate")
                    || serviceName.equals("DCP_NRCRestfulStatusQuery") || serviceName.equals("DCP_RejectCreate")
                    || serviceName.equals("DCP_UndoCreate") || serviceName.equals("DCP_BFeeErpUpdate")
                    || (serviceName.equals("DCP_ShopEDate") && ((json.indexOf("\"eType\":\"0\"") > 0))
                    || (json.indexOf("\"eType\":\"2\"") > 0))
                    || (serviceName.equals("DCP_ShopEDateProcess") && ((json.indexOf("\"eType\":\"0\"") > 0)))
                    || (serviceName.equals("DCP_ProcessPlanCreateTask") && ((json.indexOf("\"eType\":\"0\"") > 0)))
                    || (serviceName.equals("DCP_OrderECShippingCreate") && (json.indexOf("\"Jobway\":\"1\"") > 0))
                    || (serviceName.equals("DCP_StockOutProcess") && (json.indexOf("\"oEId\"") > 0))
                    || (serviceName.equals("DCP_CreditPayResultQuery") && (json.indexOf("\"eid\"") > 0))
                    || (serviceName.equals("DCP_CreditERPCreate"))
                    || (serviceName.equals("DCP_EtlRetransProcess"))
                    || serviceName.equals("DCP_POrderERPCreate") || serviceName.equals("DCP_POrderERPEcsflg")
                    // 以下為外部接口服務名，無需token
                    || serviceName.equals("DCP_TestPort") || serviceName.equals("DCP_IFGoodsQuery") || serviceName.equals("DCP_IFGoodsCategoryQuery")
                    || serviceName.equals("DCP_IFBomShopQuery") || serviceName.equals("DCP_IFGoodsPriceShopQuery") || serviceName.equals("DCP_IFGoodsUnitConvertQuery")
                    || serviceName.equals("DCP_IFUnitConvertQuery") || serviceName.equals("DCP_IFShopQuery") || serviceName.equals("DCP_IFSaleOrderQuery")
                    || serviceName.equals("DCP_IFPOrderQuery") || serviceName.equals("DCP_IFPTemplateQuery") || serviceName.equals("DCP_IFPTemplateShopQuery")
                    || serviceName.equals("DCP_IFStockDayStaticQuery") || serviceName.equals("DCP_IFStockDetailStaticQuery") || serviceName.equals("DCP_IFOrderQuery")
                    || serviceName.equals("DCP_IFSaleForecastCreate") || serviceName.equals("DCP_IFPOrderCreate") || serviceName.equals("DCP_IFShipmentPlanCreate")
                    || serviceName.equals("DCP_IFGoodsPriceQuery") || serviceName.equals("DCP_IFPriceShopQuery") || serviceName.equals("DCP_IFVOrderQuery")
                    || serviceName.equals("DCP_IFStockInQuery") || serviceName.equals("DCP_IFStockTakeQuery") || serviceName.equals("DCP_IFStockTakeDetailQuery")
                    || serviceName.equals("DCP_IFReceivingQuery")

                    // pos 销售订单转要货
                    || serviceName.equals("DCP_SaleOrderToP") || serviceName.equals("DCP_PosProcessTaskQuery") || serviceName.equals("DCP_PosPlanQuery")
                    || serviceName.equals("DCP_WeChatLoginRetail")
                    || serviceName.equals("DCP_PluGuQingQuery")
                    || serviceName.equals("DCP_PluGuQingConform")
                    || serviceName.equals("DCP_PlanQtyUpdate")

                    // PAD 导购用
                    || serviceName.equals("DCP_BasicSetQuery") || serviceName.equals("DCP_MachineQuery_Open")
                    || serviceName.equals("DCP_ShopListQuery_Open") || serviceName.equals("DCP_promListQuery_Open")
                    || serviceName.equals("DCP_OrderStatusQuery_Open")
                    || serviceName.equals("DCP_TakeOutOrderBaseSetQuery_Open")
                    || serviceName.equals("DCP_DinnerTimeQuery_Open")
                    || serviceName.equals("DCP_OrderPay_Open")


                    // 云pos 批号创建和批号查询
                    || serviceName.equals("DCP_GoodsBatchQuery") || serviceName.equals("DCP_GoodsBatchCreate")
                    //JOB调订转销用到，没token
                    || (serviceName.equals("DCP_StockOrderLockDetail") && (req.getToken() == null))
                    //JOB调订转销用到，没token
                    || (serviceName.equals("DCP_StockUnlock") && (req.getToken() == null))
                    || (serviceName.equals("DCP_StockLock") && (Check.Null(req.getToken())))
                    || (serviceName.equals("DCP_BefUserInfoQuery"))
                    || (serviceName.equals("MES_ErpStockTakeAdd"))
                    || (serviceName.equals("MES_MoCreate"))
                    || (serviceName.equals("MES_ReceivingAdd"))
                    || (serviceName.equals("MES_SortDataAdd"))
                    || (serviceName.equals("MES_SalesOrderCreate"))
                    || (serviceName.equals("MES_StockOutApplicationCreate"))
                    || (serviceName.equals("MES_SalesReturnCreate"))
                    || (serviceName.equals("MES_ComposeDisCreate"))
                    || (serviceName.equals("MES_PurchaseReturnCreate"))
                    || (serviceName.equals("MES_StockDetailCreate"))


            ) {
                //门店收货通知新增服务 不需要token
            } else {
                //檢查是否為合法的使用 markbycs 20160726
                TokenManagerRetail tmr = new TokenManagerRetail();
                tmr.checkCanUseService(es, req.getToken());
                tmr = null;
            }
            es.setDao(dao); //設定要使用的 dataSource.


            //防止重复提交
            if (serviceName.endsWith("Create") || serviceName.endsWith("Update")
                    || serviceName.endsWith("Process") || serviceName.endsWith("Delete") || serviceName.endsWith("Query")
                    || serviceName.endsWith("Detail")
                    || serviceName.endsWith("Create_Open")
                    || serviceName.endsWith("Update_Open")
                    || serviceName.endsWith("Process_Open")
            ) {
                //锁住这块代码
                boolean blocked = false;
                try {
                    //加锁，最多等2秒，等不到就加锁失败，也不影响
                    blocked = reentrantLock.tryLock(2, TimeUnit.SECONDS);

                    //请不要重复提交
                    if (PosPub.listmapJson.contains(mapJson)) {
                        //移除
                        PosPub.listmapJson.remove(mapJson);

                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请不要重复提交"); //请不要重复提交
                    } else {
                        PosPub.listmapJson.add(mapJson);
                    }
                } finally {
                    //没锁住不用解锁
                    if (blocked == true) {
                        reentrantLock.unlock();
                    }
                }

            }
			
			/*if(serviceName.equals(serviceName_Open)==false)
			{
				JSONObject json_new = new JSONObject(json);
				json_new.put("serviceId", serviceName_Open);
				json = json_new.toString();
			}*/

            String res = es.execute(json); //執行服務
            //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"\n" + "服务" + serviceName + "响应结果：  " + res  + ", 耗时:[" + (System.currentTimeMillis()-start) + "] MS\n");
            // logger.info("\r\n" + "服务" + serviceName + "响应结果：  " + res  + ", 耗时:[" + (System.currentTimeMillis()-start) + "] MS\n");
            if (!serviceName.equals("DCP_OrderQueryBuffer_Open")) {
                logger.info("服务" + serviceName + "响应结果：  " + res + ", 耗时:[" + (System.currentTimeMillis() - start) + "] MS");
            }
//            else
//            {
//            	logger.debug("服务" + serviceName + "响应结果：  " + res  + ", 耗时:[" + (System.currentTimeMillis()-start) + "] MS");
//            }
            if (!serviceName.equals("LoginNew")) {

                //JSONObject jsonRes = new JSONObject(res);
                //String oriJson="";
                //if (jsonRes.has("oriJson"))
                //{
                //	oriJson=jsonRes.getString("oriJson");
                //}

                //				try
                //				{
                //					if(!serviceName.toUpperCase().contains("GET"))
                //					{
                //						logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"***********************KEY="+oriJson+",\r\n服务"+serviceName+"响应结果：  "+res+"*****\r\n");
                //					}
                //				}
                //				catch(Exception ex)
                //				{}

            }

            return res;

        } catch (Exception e) {
            logger.error("服务" + serviceName + "出错：  " + e.getMessage() + ", 耗时:[" + (System.currentTimeMillis() - start) + "] MS", e);
            e.printStackTrace();
            return this.handlingError(e);
        } finally {
            //防止重复提交
            if (serviceName.endsWith("Create") || serviceName.endsWith("Update")
                    || serviceName.endsWith("Process") || serviceName.endsWith("Delete")
                    || serviceName.endsWith("Query") || serviceName.endsWith("Detail")) {
                //*****移除此请求记录*****
                if (PosPub.listmapJson.contains(mapJson)) {
                    PosPub.listmapJson.remove(mapJson);
                }

                //*****移除此请求记录*****
            }

            mapJson = null;
            pj = null;
        }


    }

    /**
     * 取得所有的服務
     *
     * @return
     */
    public String[] getServiceList() throws Exception {
        String uri = DispatchService.class.getResource(FIND_SERVICE_PATH).getFile();
        uri = URLDecoder.decode(uri, "UTF-8");

        File root = new File(uri);
        if (root.isDirectory()) {
            List<String> data = new ArrayList<String>();
            File[] services = root.listFiles();
            for (File f : services) {
                if (f.isDirectory()) {
                    continue;
                }
                String sName = f.getName();
                sName = sName.substring(0, sName.indexOf("ServiceImp")); //FIXME 要再找更好的做法
                data.add(sName);
            }
            return data.toArray(new String[0]);
        }
        return new String[0];
    }

    /**
     * 錯誤處理.
     *
     * @param e
     * @return
     */
    private String handlingError(Exception e) {
        try {
            ParseJson pj = new ParseJson();
            JsonRes res = new JsonRes();
            res.setSuccess(Boolean.FALSE);
            if (e instanceof SPosCodeException) {
                SPosCodeException ce = (SPosCodeException) e;
                String localMsg = ce.getLocalizedMessage() != null ? ce.getLocalizedMessage() : "";
                String description = ce.getErrorCodeType().toString();
                res.setServiceStatus(ce.getErrorCodeType().getCodeType());

                //通过配置文件读取  BY JZMA 20200420
                String langtype = "zh_CN";
                List<ProdInterface> lstProd = StaticInfo.psc.getT100Interface().getProdInterface();
                if (lstProd != null && !lstProd.isEmpty()) {
                    langtype = lstProd.get(0).getHostLang().getValue();
                }
                if (langtype.equals("zh_TW") && localMsg != null && !localMsg.isEmpty()) {
                    localMsg = ZHConverter.convert(localMsg, 0);
                }


                if (description.equals(localMsg)) {
                    res.setServiceDescription(description);
                } else if (!"".equals(localMsg)) {
                    res.setServiceDescription(localMsg);
                } else {
                    res.setServiceDescription(ce.getErrorCodeType().toString() + ", " + e.toString());
                }
            } else {
                res.setServiceStatus(CODE_EXCEPTION_TYPE.E500.getCodeType());
                //e.fillInStackTrace().getCause().getMessage()比	e.toString()更详细
                if (e.fillInStackTrace().getCause() == null) {
                    res.setServiceDescription(CODE_EXCEPTION_TYPE.E500.toString() + ", " + e.fillInStackTrace().getMessage());
                } else {
                    res.setServiceDescription(CODE_EXCEPTION_TYPE.E500.toString() + ", " + e.fillInStackTrace().getCause().getMessage());
                }
            }

            String sRes = pj.beanToJson(res);
            pj = null;

            return sRes;
        } catch (Exception e1) {
            return e1.toString();
        }
    }

    @SuppressWarnings("rawtypes")
    private ExecuteService getService(String serviceName) throws Exception {
        ClassLoader loader = this.getClass().getClassLoader();
        for (String serivcePath : this.regServicePath) {
            String servicePatch = serivcePath + "." + serviceName + "ServiceImp";
            try {
                //判断服务是否存在
                String strpath = serivcePath.replace(".", "/");
                URL url = DispatchService.class.getResource("/" + strpath);
                if (url != null) {
                    strpath = url.getPath() + serviceName + "ServiceImp.class";
                    File myFile = new File(strpath);

                    //如果不存在兼容新版本去掉ServiceImp
                    if (!myFile.exists()) {
                        myFile = new File(url.getPath() + serviceName + ".class");
                        servicePatch = serivcePath + "." + serviceName;
                    }


                    //如果还是找不到的话 兼容_Open
                    if (!myFile.exists()) {
                        String flagStr = "_Open";
                        if (serviceName.endsWith(flagStr)) {
                            myFile = new File(url.getPath() + serviceName.substring(0, serviceName.length() - flagStr.length()) + ".class");
                            servicePatch = serivcePath + "." + serviceName.substring(0, serviceName.length() - flagStr.length());
                        } else {
                            myFile = new File(url.getPath() + serviceName + flagStr + ".class");
                            servicePatch = serivcePath + "." + serviceName + flagStr;
                        }
                    }

                    if (myFile.exists()) {
                        Class c;
                        try {
                            c = loader.loadClass(servicePatch);
                        } catch (ClassNotFoundException e) { //catch 一次防止出现已经有用小写的接口出现
                            //v3x兼容下划线后首字母小写接口
                            servicePatch = StringUtils.toUnderScoreFirstUpperCase(servicePatch);
                            c = loader.loadClass(servicePatch);
                        }

                        if (c != null) {
                            myFile = null;
                            loader = null;
                            return (ExecuteService) c.newInstance();
                        }
                        loader = null;
                    }
                    myFile = null;
                }
                url = null;
            } catch (ClassNotFoundException e) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E404, e.toString());
            }
        }
        throw new Exception("Service not Found!!!");
    }

    public static class SPosCodeException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * 服务状态码，当服务执行失败时，可以根据状态码判断失败原因
         *
         * @author Xavier
         */
        public enum CODE_EXCEPTION_TYPE {
            // modi. 07558 參考 http狀態碼方式定義回傳狀態；(保留舊有的定義)
            E200("000", "服务执行成功"), // 200, 服务执行成功
            E202("100", "服务呼叫成功"), // 202, Accepted 服务呼叫成功, 但可能尚未執行完成；
            E210("210", "服务执行成功，T100服务失败"), // 210, WEB服务执行成功，T100服务失败。
            E500("200", "服务执行异常"), // 500, Internal Server Error, 服务执行异常
            E404("201", "无对应的服务"), // 404, Not Found, 无对应的服务
            E400("202", "传入资料格式异常"), // 400, Bad Request, 传入资料格式异常
            E401("203", "帐号或密码错误"), // 401, 帐号或密码错误,
            E401_1("203", "帐号不存在"), // 401.1, account error
            E401_2("204", "密码错误"), // 401.2, password error
            //新增參考 http狀態碼
            E422("422", "传入资料逻辑异常"),  // Unprocessable Entity, 傳入資料格式正確，但資料邏輯錯誤；
            E440("440", "登录超时"),  // Login Timeout, 登录超时
            E501("501", "无待收货通知，不能收货"),  // 未找到对应的待收货通知，暂不能收货！
            E502("502", "已存在对应收货单，不能重复收货"),  // 已存在对应的收货单XXXXXXXX!，不能重复收货！
            E503("503", "已存在对应收货单，不能作废"),  // 已存在对应的收货单XXXXXXXX!，不能作废！
            E600("600", "模块或功能授权检查异常")  // 模块或功能授权检查异常
            ;
            String codeType;
            String description;

            private CODE_EXCEPTION_TYPE(String codeType, String description) {
                this.codeType = codeType;
                this.description = description;
            }

            public String getCodeType() {
                return this.codeType;
            }

            @Override
            public String toString() {
                return this.description;
            }
        }

        private CODE_EXCEPTION_TYPE eCodeType;

        public SPosCodeException(CODE_EXCEPTION_TYPE eCodeType, String msg) {
            super(msg);
            this.eCodeType = eCodeType;
        }

        public SPosCodeException(CODE_EXCEPTION_TYPE eCodeType) {
            super(eCodeType.toString());
            this.eCodeType = eCodeType;
        }

        public CODE_EXCEPTION_TYPE getErrorCodeType() {
            return eCodeType;
        }

        public void setErrorCodeType(CODE_EXCEPTION_TYPE eCodeType) {
            this.eCodeType = eCodeType;
        }

    }
}