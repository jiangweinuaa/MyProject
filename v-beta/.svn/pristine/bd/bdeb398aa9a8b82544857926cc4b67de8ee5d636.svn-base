package com.dsc.spos.service;
import com.dsc.spos.dao.*;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.ResultDatas;
import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.utils.JSONUtils;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.ApiUser;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.dsc.spos.utils.Log;

import java.net.URLDecoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.dsc.spos.utils.*;

/**
 * 基礎查詢功能
 * @author Xavier
 *
 * @param <REQ>
 * @param <RES>
 */
public abstract class SPosBasicService<REQ extends JsonBasicReq, RES extends JsonBasicRes > implements ExecuteService
{
    protected DsmDAO dao;
    
    private DCP_LoginRetailRes loginRes;
    
    private String key="";
    
    Logger logger = LogManager.getLogger(SPosBasicService.class);
    
    @Override
    public String execute(String json) throws Exception {
        ParseJson pj = new ParseJson();
        //System.out.println(json);
        REQ req = (REQ) pj.jsonToBean(json, this.getRequestType());

        StringBuilder errMsg = new StringBuilder();
        JSONUtils.checkRequiredFields(req, errMsg);
        if (StringUtils.isNotEmpty(errMsg.toString())) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (this.isVerifyFail(req)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400); //格式錯誤
        }
        key=req.getToken();
        JSONObject jsonObj = new JSONObject(json);
        String serviceName = jsonObj.getString("serviceId");
        String reqguid=UUID.randomUUID().toString();
        
        /*
         * OrderGet 订单中心查询
         * OrderProcess  OrderReturnProcess OrderUpdate  微商城订单处理
         * OrderCreate OrderModify 云pos门店请求创建订单
         * OrderAgreeProcess OrderRejectProcess 外卖订单处理
         */
        boolean noExistToken = false;
        if(serviceName.startsWith("ISV_")||serviceName.startsWith("DCP_OrderModify_")||serviceName.equals("DCP_OrderCreateCancle_Open")
                ||serviceName.equals("DCP_OrderSaleCancle_Open")||serviceName.equals("DCP_OrderDeliveryProcess")
                ||serviceName.equals("DCP_OrderRedisProcess")||serviceName.equals("DCP_OrgOrderUpdate")
                ||serviceName.equals("DCP_CRegisterDelete")||serviceName.equals("DCP_OrderQuery")
                ||serviceName.equals("DCP_OrderShopStatusUpdate")||serviceName.equals("DCP_OrderShopStatusQuery")
                ||serviceName.equals("DCP_ShopInfoExQuery")||serviceName.equals("DCP_GoodsSetExQuery")
                ||serviceName.equals("DCP_OrderStatusLogQuery")||serviceName.equals("DCP_OrderStatusLogCreate")
                ||serviceName.equals("DCP_StockB2BQuery")||serviceName.equals("DCP_OrderProcess")
                ||serviceName.equals("DCP_OrderReturnProcess")||serviceName.equals("DCP_OrderUpdate")
                ||serviceName.equals("DCP_OrderCreate")
                ||serviceName.equals("DCP_OrderAgreeProcess")||serviceName.equals("DCP_OrderRejectProcess")
                ||serviceName.equals("DCP_OrderStatusUpdate")||serviceName.equals("DCP_OrderStatusUpdateERP")
                ||serviceName.equals("DCP_RequisitionUpdate")||serviceName.equals("DCP_OrderStatusUpdateJh")
                ||serviceName.equals("DCP_OrderQueryBuffer")||serviceName.equals("DCP_OrderDelteBuffer")
                ||serviceName.equals("DCP_PayInCheck")||serviceName.equals("DCP_OrderDeliveryQuery")||serviceName.equals("DCP_OrderPrintErrorSaveBuffer")||serviceName.equals("DCP_OrderPrintErrorQueryBuffer")) {
            noExistToken = true;
        }
        
        //记录下订转销日志
        /*if (serviceName.equals("DCP_OrderToSaleProcess_Open") || serviceName.equals("DCP_OrderToSaleProcess")
                || serviceName.equals("DCP_OrderToSaleProcess2_Open") || serviceName.equals("DCP_OrderToSaleProcess2")){
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"订转销服务" + serviceName+":\r\n" +json);
        }*/
        
        //外部接口不用token
        if (serviceName.endsWith("_Open") && Check.Null(key) ) {
            noExistToken = true;
            String eId = "";
            req.getServiceId();
            //******************** 校验外部接口开始 **********************
            com.alibaba.fastjson.JSONObject fastReqJson = com.alibaba.fastjson.JSONObject.parseObject(json, Feature.OrderedField);
            /*
            String reqJsonStr = fastReqJson.get("request").toString();
            String signJsonStr = fastReqJson.get("signJson").toString();
             */
            LinkedHashMap<String,Object> hashJson=com.alibaba.fastjson.JSON.parseObject(json, LinkedHashMap.class, Feature.OrderedField);
            com.alibaba.fastjson.JSONObject  jsonObject=new com.alibaba.fastjson.JSONObject (true);
            jsonObject.putAll(hashJson);
            String reqJsonStr =jsonObject.getJSONObject("request").toJSONString();
            String signJsonStr =jsonObject.getJSONObject("signJson").toJSONString();
            //主动释放
            if (hashJson!= null) {
                hashJson.clear();
                hashJson=null;
            }
            jsonObject=null;

//			String apiUserCode = fastReqJson.get("apiUserCode").toString();
//			com.alibaba.fastjson.JSONObject signJson = new com.alibaba.fastjson.JSONObject();
//
//			signJson.put("sign", sign);
//			signJson.put("key", apiUserCode);
//			String signJsonStr = signJson.toString();
            
            MyCommon mc = new MyCommon();
            ResultDatas rd = mc.checkSign(dao, reqJsonStr, signJsonStr);
            eId = rd.geteId();
            String description = rd.getDesctiption();
            ApiUser apiUser = new ApiUser();
            if( rd.getStatus().equals("SUCCESS")){
                apiUser = rd.getApiUser();
                req.setApiUser(apiUser);
                //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "*********************** 服务" + serviceName + "签名校验成功， 传入参数：  " + json + "*****\r\n");
            } else {
                if (serviceName.equals("DCP_OrderToSaleProcess_Open") || serviceName.equals("DCP_OrderToSaleProcess")
                        || serviceName.equals("DCP_OrderToSaleProcess2_Open") || serviceName.equals("DCP_OrderToSaleProcess2")) {
                    //订转销接口临时跳过
                    apiUser = rd.getApiUser();
                    req.setApiUser(apiUser);
                } else {
                    logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "*********************** 服务" + serviceName + "签名校验失败,传入参数:" + json + "*****\r\n");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"签名校验失败!");
                }
            }
            
            //******************** 校验外部接口结束 **********************
            req.seteId(eId);
        }
        
        /** ||serviceName.equals("DCP_ReceivingCreate") || serviceName.equals("DCP_TransferCreate")
         *  || serviceName.equals("DCP_CRegisterCheck") || serviceName.equals("DCP_SRegister")
         *
         */
        
        if(noExistToken||serviceName.equals("DCP_HolidayGoodsSync") ||serviceName.equals("DCP_SaleCreate")
//                ||serviceName.equals("DCP_ReceivingCreate")
                || serviceName.equals("DCP_StockTaskCreate")
                || serviceName.equals("DCP_StockOutErpUpdate") || serviceName.equals("DCP_DayEndCheck")
                || serviceName.equals("DCP_TransferCreate")	|| serviceName.equals("DCP_CRegisterCheck")
                || serviceName.equals("DCP_SRegister")  	|| serviceName.equals("DCP_AdjustCreate")
                || serviceName.equals("DCP_Register") || serviceName.equals("DCP_RegisterCreate")
                || serviceName.equals("DCP_RegisterQuery") || serviceName.equals("DCP_UpErrorLogQuery")
                || serviceName.equals("DCP_VersionQuery")|| serviceName.equals("DCP_LStockOutErpUpdate")
                || serviceName.equals("DCP_NRCRestfulStatusQuery")|| serviceName.equals("DCP_RejectCreate")
                || serviceName.equals("DCP_UndoCreate")|| serviceName.equals("DCP_BFeeErpUpdate")
                ||(serviceName.equals("DCP_ShopEDate")&& ((json.indexOf("\"eType\":\"0\"")>0))||(json.indexOf("\"eType\":\"2\"")>0))
                ||(serviceName.equals("DCP_ShopEDateProcess") && ((json.indexOf("\"eType\":\"0\"")>0)))
                ||(serviceName.equals("DCP_ProcessPlanCreateTask") && ((json.indexOf("\"eType\":\"0\"")>0)))
                ||(serviceName.equals("DCP_OrderECShippingCreate") &&(json.indexOf("\"Jobway\":\"1\"")>0))
                ||(serviceName.equals("DCP_StockOutProcess") &&(json.indexOf("\"oEId\"")>0))
                ||(serviceName.equals("DCP_CreditPayResultQuery") &&(json.indexOf("\"eid\"")>0))
                ||(serviceName.equals("DCP_CreditERPCreate"))
                || serviceName.equals("DCP_POrderERPCreate") || serviceName.equals("DCP_POrderERPEcsflg")
                // 以下為外部接口服務名，無需token
                || serviceName.equals("DCP_TestPort") || serviceName.equals("DCP_IFGoodsQuery")
                || serviceName.equals("DCP_IFGoodsCategoryQuery") || serviceName.equals("DCP_IFBomShopQuery")
                || serviceName.equals("DCP_IFGoodsPriceShopQuery") || serviceName.equals("DCP_IFGoodsUnitConvertQuery")
                || serviceName.equals("DCP_IFUnitConvertQuery") || serviceName.equals("DCP_IFShopQuery")
                || serviceName.equals("DCP_IFSaleOrderQuery") || serviceName.equals("DCP_IFPOrderQuery")
                || serviceName.equals("DCP_IFPTemplateQuery") || serviceName.equals("DCP_IFPTemplateShopQuery")
                || serviceName.equals("DCP_IFStockDayStaticQuery") || serviceName.equals("DCP_IFStockDetailStaticQuery")
                || serviceName.equals("DCP_IFOrderQuery") || serviceName.equals("DCP_IFSaleForecastCreate")
                || serviceName.equals("DCP_IFPOrderCreate") || serviceName.equals("DCP_IFShipmentPlanCreate")
                || serviceName.equals("DCP_IFGoodsPriceQuery") || serviceName.equals("DCP_IFPriceShopQuery")
                || serviceName.equals("DCP_IFVOrderQuery") || serviceName.equals("DCP_IFStockInQuery")
                || serviceName.equals("DCP_IFStockTakeQuery") || serviceName.equals("DCP_IFStockTakeDetailQuery")
                || serviceName.equals("DCP_IFReceivingQuery")
                ||(serviceName.equals("DCP_EtlRetransProcess"))
                // pos 销售订单转要货
                || serviceName.equals("DCP_SaleOrderToP") || serviceName.equals("DCP_PosProcessTaskQuery")
                || serviceName.equals("DCP_PosPlanQuery") || serviceName.equals("DCP_WeChatLoginRetail")
                || serviceName.equals("DCP_PluGuQingQuery") || serviceName.equals("DCP_PluGuQingConform")
                || serviceName.equals("DCP_PlanQtyUpdate")
                // PAD 导购用
                || serviceName.equals("DCP_BasicSetQuery") || serviceName.equals("DCP_MachineQuery_Open")
                || serviceName.equals("DCP_ShopListQuery_Open") || serviceName.equals("DCP_promListQuery_Open")
                || serviceName.equals("DCP_OrderStatusQuery_Open") || serviceName.equals("DCP_TakeOutOrderBaseSetQuery_Open")
                || serviceName.equals("DCP_OrderPay_Open")
                // 云pos 批号创建和批号查询
                || serviceName.equals("DCP_GoodsBatchQuery") || serviceName.equals("DCP_GoodsBatchCreate")
                || serviceName.equals("DCP_DinnerTimeQuery_Open")
                //JOB调订转销用到，没token
                ||(serviceName.equals("DCP_StockOrderLockDetail") &&(req.getToken()==null))
                //JOB调订转销用到，没token
                ||(serviceName.equals("DCP_StockUnlock") &&(req.getToken()==null))
                ||(serviceName.equals("DCP_StockLock") &&(Check.Null(req.getToken())))
                ||(serviceName.equals("DCP_BefUserInfoQuery"))
                ||(serviceName.equals("MES_ErpStockTakeAdd"))
                ||(serviceName.equals("MES_MoCreate"))
                ||(serviceName.equals("MES_ReceivingAdd"))
                ||(serviceName.equals("MES_SortDataAdd"))
                ||(serviceName.equals("MES_SalesOrderCreate"))
                ||(serviceName.equals("MES_StockOutApplicationCreate"))
                ||(serviceName.equals("MES_SalesReturnCreate"))
                ||(serviceName.equals("MES_ComposeDisCreate"))
                ||(serviceName.equals("MES_PurchaseReturnCreate"))
                ||(serviceName.equals("MES_StockDetailCreate"))



        )
        {
            //门店收货通知新增服务 不需要token
            //******标记日志******
            String pEId="";
            String pShop="";
            if (jsonObj.has("eId")) {
                pEId=jsonObj.getString("eId");
            }else if (jsonObj.has("eEId")) {
                pEId=jsonObj.getString("eEId");
            }else {
                pEId= "" ;
            }
            
            if (jsonObj.has("shopId")) {
                pShop=jsonObj.getString("shopId");
            } else if (jsonObj.has("eShop")) {
                pShop=jsonObj.getString("eShop");
            } else {
                pShop="";
            }
            jsonObj=null;
        } else {
            //取得當前操作人員
            TokenManagerRetail tmr=new TokenManagerRetail();
            this.loginRes = tmr.getLoginData(req.getToken());
            tmr=null;
            DCP_LoginRetailRes.level1Elm oneLv1 = loginRes.getDatas().get(0);
            
            req.setShopId(oneLv1.getShopId());
            req.setShopName(oneLv1.getShopName());
            req.setOrganizationNO(oneLv1.getOrganizationNo());
            req.setOrg_Form(oneLv1.getOrg_Form());
            
            req.seteId(oneLv1.geteId());
            req.setLangType(oneLv1.getLangType());
            req.setOpNO(oneLv1.getOpNo());
            req.setOpName(oneLv1.getOpName());
            req.setViewAbleDay(oneLv1.getViewAbleDay());
            
            req.setIn_cost_warehouse(oneLv1.getIn_cost_warehouse());
            req.setIn_non_cost_warehouse(oneLv1.getIn_non_cost_warehouse());
            req.setOut_cost_warehouse(oneLv1.getOut_cost_warehouse());
            req.setOut_non_cost_warehouse(oneLv1.getOut_non_cost_warehouse());
            req.setInv_cost_warehouse(oneLv1.getInv_cost_warehouse());
            req.setInv_non_cost_warehouse(oneLv1.getInv_non_cost_warehouse());
            req.setReturn_cost_warehouse(oneLv1.getReturn_cost_warehouse());
            req.setReturn_cost_warehouseName(oneLv1.getReturn_cost_warehouse_name());
            
            req.setIn_cost_warehouseName(oneLv1.getIn_cost_warehouse_name());
            req.setIn_non_cost_warehouseName(oneLv1.getIn_non_cost_warehouse_name());
            req.setOut_cost_warehouseName(oneLv1.getOut_cost_warehouse_name());
            req.setOut_non_cost_warehouseName(oneLv1.getOut_non_cost_warehouse_name());
            req.setInv_cost_warehouseName(oneLv1.getInv_cost_warehouse_name());
            req.setInv_non_cost_warehouseName(oneLv1.getInv_non_cost_warehouse_name());
            
            req.setDefDepartNo(oneLv1.getDefDepartNo());
            req.setDefDepartName(oneLv1.getDefDepartName());
            req.setCITY(oneLv1.getCITY());
            req.setDISTRICT(oneLv1.getDISTRICT());
            req.setENABLECREDIT(oneLv1.getENABLECREDIT());
            req.setBELFIRM(oneLv1.getBELFIRM());
            req.setBELFIRM_NAME(oneLv1.getBELFIRM_NAME());
            req.setMultiWarehouse(oneLv1.getMultiWarehouse());
            req.setEnableMultiLang(oneLv1.getEnableMultiLang());
            req.setPageSizeDetail(oneLv1.getPageSizeDetail());
            req.setChatUserId(oneLv1.getChatUserId());
            req.setEmployeeNo(oneLv1.getEmployeeNo());
            req.setOrgRange(oneLv1.getOrgRange());
            req.setDefaultOrg(oneLv1.getDefaultOrg());
            req.setEmployeeName(oneLv1.getEmployeeName());
            req.setDepartmentName(oneLv1.getDepartmentName());
            req.setDepartmentNo(oneLv1.getDepartmentNo());
            req.setBelOrgNo(oneLv1.getBelOrgNo());
            req.setBelOrgName(oneLv1.getBelOrgName());
            req.setUpDepartNo(oneLv1.getUpDepartNo());
            req.setCorp(oneLv1.getCorp());
            req.setCorpName(oneLv1.getCorpName());
            req.setTaxPayerType(oneLv1.getTaxPayerType());
            req.setOutputTaxCode(oneLv1.getOutputTaxCode());
            req.setOutputTaxName(oneLv1.getOutputTaxName());
            req.setOutputTaxRate(oneLv1.getOutputTaxRate());
            req.setInputTaxCode(oneLv1.getInputTaxCode());
            req.setInputTaxName(oneLv1.getInputTaxName());
            req.setInputTaxRate(oneLv1.getInputTaxRate());
            req.setInputTaxCalType(oneLv1.getInputTaxCalType());
            req.setInputTaxInclTax(oneLv1.getInputTaxInclTax());
            req.setOutputTaxCalType(oneLv1.getOutputTaxCalType());
            req.setOutputTaxInclTax(oneLv1.getOutputTaxInclTax());
        }
        
        
        try
        {
            //!serviceName.toUpperCase().contains("GET") && !serviceName.toUpperCase().contains("QUERY") && 查询还是要记录日志  by jinzma 20211020
            if(!serviceName.equals("DCP_OrderQueryBuffer_Open")) {
                //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "***********************reqguid=" + reqguid + "  KEY=" + key + ",服务" + serviceName + ",eId=" + req.geteId() + ",shopId=" + req.getShopId() + ",opno=" + req.getOpNO() + " 传入参数：  " + json + "*****\r\n");
            }
        } catch(Exception ex) {}
        if (!this.AuthCheck(req)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E600);
        }
        RES res = this.processJson(req);
        
        //返回的语言类型设置成请求的，保持一致(多语言的问题)
        res.setLangType(req.getLangType());
        
        //通过配置文件读取
        String langtype="zh_CN";
        List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
        if(lstProd!=null&&!lstProd.isEmpty()) {
            langtype=lstProd.get(0).getHostLang().getValue();
        }
        if(req.getLangType()!=null&&langtype.equals("zh_TW")&&res.getServiceDescription()!=null&&!res.getServiceDescription().isEmpty()) {
            res.setServiceDescription(ZHConverter.convert(res.getServiceDescription(),0));
        }
        
        String rres=pj.beanToJson(res);
        
        pj=null;
        
        if(serviceName.equals("DCP_OrderQRCodeQuery_Open")||serviceName.equals("DCP_OrderQRCodeQuery")) {
            try {
                rres = URLDecoder.decode(rres, "utf-8");
				/*Gson gsUrl = new GsonBuilder().disableHtmlEscaping().create();				
				rres = gsUrl.toJson(res);
				gsUrl = null;*/
            
            } catch (Exception e) {
            }
            
        }
        if(serviceName.startsWith("DCP_ISVMTShop")||serviceName.startsWith("DCP_ISVELMShop"))
        {
            try {
                rres = rres.replace("\\u003d","=").replace("\\u0026","&");

            }
            catch (Exception e)
            {

            }

        }
        
        if(req.getLangType()!=null&&langtype.equals("zh_TW"))
        {
            if(serviceName.equals("OrderStatusLogGet"))
            {
                rres=ZHConverter.convert(rres,0);
            }
        }
        
        try {
            //!serviceName.toUpperCase().contains("GET") && !serviceName.toUpperCase().contains("QUERY") &&  查询还是要记录日志  by jinzma 20211020
            if(!serviceName.equals("DCP_OrderQueryBuffer_Open") ) {
               // logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "***********************reqguid=" + reqguid + "  KEY=" + key + ",服务" + serviceName + ",eId=" + req.geteId() + ",shopId=" + req.getShopId() + ",opno=" + req.getOpNO() + " 响应结果：  " + rres + "*****\r\n");
            }
        } catch(Exception ex)
        {}
        
        return rres;
    }
    
    /**
     * 取得 login 資訊
     * @return
     */
    protected DCP_LoginRetailRes getLoginData() {
        return this.loginRes;
    }
    
    /**
     * 檢查傳入的JSON 格式, 是否為該作業想要的.
     * @param req
     * @throws Exception
     */
    protected abstract boolean isVerifyFail(REQ req) throws Exception;
    
    /**
     * 取得 request 的型態
     * @return
     */
    protected abstract TypeToken<REQ> getRequestType();
    
    /**
     * 取得Response的型態
     * @return
     */
    protected abstract RES getResponseType();
    
    /**
     * 處理資料
     * @return
     */
    protected abstract RES processJson(REQ req) throws Exception;
    
    /**
     * 取得查詢資料
     * @return
     */
    protected abstract void processRow(Map<String, Object> row) throws Exception;
    
    /**
     * 取得查詢 SQL
     * @return
     */
    protected abstract String getQuerySql(REQ req) throws Exception;
    
    /**
     * 取得 response.
     * @return
     * @throws Exception
     */
    protected RES getResponse() throws Exception
    {
        RES res = this.getResponseType();
        res.setOriJson(key);
        res.setSuccess(Boolean.TRUE);
        res.setServiceStatus(CODE_EXCEPTION_TYPE.E200.getCodeType());
        res.setServiceDescription(CODE_EXCEPTION_TYPE.E200.toString());
        if (res instanceof JsonRes)
        {
            //	((JsonRes)res).setDatas(new ArrayList<Map<String, Object>>());
        }
        return res;
    }
    
    /**
     * 查詢資料
     * @param sql
     * @param conditionValues
     * @return
     * @throws Exception
     */
    protected RES doQueryData(REQ req, String sql, String[] conditionValues) throws Exception {
        //查資料
        List<Map<String, Object>> data = this.doQueryData(sql, conditionValues); //FIXME 可能會有效能問題
        
        //設定基礎資料
        JsonRes res = (JsonRes)this.getResponse();
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(data.size());
        
        //算總頁數
        int totalPages = res.getTotalRecords() / req.getPageSize();
        totalPages = (res.getTotalRecords() % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
        res.setTotalPages(totalPages);
        
        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        
        int endRow = req.getPageNumber() * req.getPageSize();
        if ((res.getTotalRecords() < endRow)) { //筆數沒這麼多.
            endRow = res.getTotalRecords();
        }
        for (int i = startRow; i < endRow; i++) {
            Map<String, Object> row = data.get(i);
            this.processRow(row); //處理內容
            //res.getDatas().add(row);
        }
		/*
		for (Map<String, Object> row : data) {
			this.processRow(row); //處理內容
			res.getDatas().add(row);
		}*/
        return (RES)res;
    }
    
    protected RES doQueryData2(REQ req, String sql, String sql2,String shopId,String organizationNO, String eId,String status,String beginDate,String endDate, String[] conditionValues) throws Exception {
        //查資料
        List<Map<String, Object>> data = this.doQueryData(sql, conditionValues); //查询单头资料
        
        for(int i = 0; i < data.size(); i++)
        {
            //System.out.println("单头	"+data.get(i));
            String OrderNO = (String) data.get(i).get("pOrderNO");
            //System.out.println("OrderNO"+OrderNO);
            String[] conditionValues2 = {shopId,organizationNO, eId,status,beginDate,endDate,OrderNO}; //查詢條件
            List<Map<String, Object>> data2 = this.doQueryData(sql2, conditionValues2);  //查询单身資料
            
            JSONArray jsonArray = new JSONArray();
            
            for (int j = 0; j < data2.size(); j++){
                ////System.out.println("单身	"+data2.get(j));
                jsonArray.put(data2.get(j));
            }
            data.get(i).put("datas", jsonArray);
        }
        
        //System.out.println("组合完成后的-------------"+data);
        
        //設定基礎資料
        JsonRes res = (JsonRes)this.getResponse();
        
        for (Map<String, Object> row : data) {
            this.processRow(row); //處理內容
            //res.getDatas().add(row);
        }
        return (RES)res;
    }
    
    /**
     * 查詢資料
     * @param sql
     * @param conditionValues
     * @return
     * @throws Exception
     */
    protected List<Map<String, Object>> doQueryData(String sql, String[] conditionValues) throws Exception {
        return this.dao.executeQuerySQL(sql, conditionValues);
    }


    @Override
    public void setDao(DsmDAO dao) {
        this.dao = dao;
    }
    
    @Override
    public boolean needTokenVerify() {
        return Boolean.TRUE;
    }
    
    /**
     * 绑定变量SQL的写法(注意仅适用于简单SQL语句，较复杂的SQL不要用)
     * @param sql
     * @param values
     * @return
     * @throws Exception
     */
    protected List<Map<String, Object>> executeQuerySQL_BindSQL(String sql, final List<DataValue> values) throws Exception
    {
        return  this.dao.executeQuerySQL_BindSQL(sql,values);
    }
    /**
     * 授权模块并发用户数管控
     * @param req
     * @throws Exception
     */
    protected boolean AuthCheck_v1(REQ req) throws Exception
    {
    	if(Check.isEmpty(req.getModularNo()) || Check.isEmpty(req.geteId()) || Check.isEmpty(req.getToken()))
    	{
    		return true;
    	}
        String sql = "select * from DCP_MODULAR A WHERE A.EID='"+req.geteId()+"' and a.MODULARNO='"+req.getModularNo()+"' ";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&getQData.isEmpty()==false)
        {
        	String rfuncNo=getQData.get(0).get("RFUNCNO").toString();
        	if(Check.isNotEmpty(rfuncNo))
        	{
        		sql="SELECT * FROM DCP_REGEDISTMODULAR B where B.RFUNCNO='"+rfuncNo+"' ";
        		getQData = this.doQueryData(sql,null);
        		if (getQData!=null&&getQData.isEmpty()==false)
        		{
        			String rmodularNo=getQData.get(0).get("RMODULARNO").toString();
        			//String modularAuth=getQData.get(0).get("MODULAR_AUTH").toString();
        			String modularRtypeInfo=getQData.get(0).get("RTYPEINFO").toString();
        			if("4".equals(modularRtypeInfo))//模块授权 and 并发数
        			{
        				sql="SELECT * FROM PLATFORM_CREGISTERDETAIL WHERE PRODUCTTYPE='"+rfuncNo+"'  AND BDATE<=TO_CHAR(SYSDATE,'YYYYMMDD')  AND EDATE>=TO_CHAR(SYSDATE,'YYYYMMDD')"
        				  + " ORDER BY TOKEN ASC ";
        				getQData = this.doQueryData(sql,null);
        				if (getQData!=null&&getQData.isEmpty()==false)
        				{
        					for(Map<String,Object> map:getQData)
        					{
        						String queryToken=map.get("TOKEN").toString();
        						if(req.getToken().equals(queryToken))
        						{
        					        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        							List<DataProcessBean> data_d =  new ArrayList<DataProcessBean>();
        							UptBean ub = new UptBean("PLATFORM_CREGISTERDETAIL");
        							ub.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
        							ub.addCondition("MACHINECODE", new DataValue(map.get("MACHINECODE").toString(), Types.VARCHAR));
        							ub.addCondition("TOKEN", new DataValue(req.getToken(), Types.VARCHAR));
        							data_d.add(new DataProcessBean(ub));
        							dao.useTransactionProcessData(data_d);
        					        sql="SELECT * FROM DCP_MODULAR_WORKING_AUTH WHERE MODULARNO='"+req.getModularNo()+"' AND TOKEN='"+req.getToken()+"' " ;		
        					        List<Map<String,Object>> modularWorkingList = this.doQueryData(sql,null);
        					        if (modularWorkingList!=null&&modularWorkingList.isEmpty()==false)
        					        {
        					        	List<DataProcessBean> data_d1 =  new ArrayList<DataProcessBean>();
            							UptBean ub1 = new UptBean("DCP_MODULAR_WORKING_AUTH");
            							ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
            							ub1.addCondition("MODULARNO", new DataValue(req.getModularNo(), Types.VARCHAR));
            							ub1.addCondition("TOKEN", new DataValue(req.getToken(), Types.VARCHAR));
            							data_d1.add(new DataProcessBean(ub1));
            							dao.useTransactionProcessData(data_d1);
        					        }else
        					        {
        					        	List<DataProcessBean> data_d1 =  new ArrayList<DataProcessBean>();
        					        	ColumnDataValue columnDataValueAuth = new ColumnDataValue();
        					        	columnDataValueAuth.Add("MODULARNO",req.getModularNo(), Types.VARCHAR);
        					        	columnDataValueAuth.Add("TOKEN",req.getToken(), Types.VARCHAR);
        					        	columnDataValueAuth.Add("RFUNCNO",rfuncNo, Types.VARCHAR);
        					        	columnDataValueAuth.Add("RMODULARNO",rmodularNo, Types.VARCHAR);
        					        	columnDataValueAuth.Add("CREATETIME",lastmoditime, Types.DATE);
        					        	columnDataValueAuth.Add("LASTMODITIME",lastmoditime, Types.DATE);
        		                    	String[] columnsAuth= columnDataValueAuth.Columns.toArray(new String[0]);
        		                    	DataValue[] insDataValueSale = columnDataValueAuth.DataValues.toArray(new DataValue[0]);
        		                    	InsBean ibAuth = new InsBean("DCP_MODULAR_WORKING_AUTH", columnsAuth);
        		                    	ibAuth.addValues(insDataValueSale);
        		                    	data_d1.add(new DataProcessBean(ibAuth));
            							dao.useTransactionProcessData(data_d1);
        					        }
        							return true;
        						}
        						if(Check.isEmpty(queryToken))
        						{
        					        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        							List<DataProcessBean> data_d =  new ArrayList<DataProcessBean>();
        							UptBean ub = new UptBean("PLATFORM_CREGISTERDETAIL");
        							ub.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
        							ub.addUpdateValue("TOKEN", new DataValue(req.getToken(), Types.VARCHAR));
        							ub.addCondition("MACHINECODE", new DataValue(map.get("MACHINECODE").toString(), Types.VARCHAR));
        							data_d.add(new DataProcessBean(ub));
        							dao.useTransactionProcessData(data_d);
          					        sql="SELECT * FROM DCP_MODULAR_WORKING_AUTH WHERE MODULARNO='"+req.getModularNo()+"' AND TOKEN='"+req.getToken()+"' " ;		
        					        List<Map<String,Object>> modularWorkingList = this.doQueryData(sql,null);
        					        if (modularWorkingList!=null&&modularWorkingList.isEmpty()==false)
        					        {
        					        	List<DataProcessBean> data_d1 =  new ArrayList<DataProcessBean>();
            							UptBean ub1 = new UptBean("DCP_MODULAR_WORKING_AUTH");
            							ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
            							ub1.addCondition("MODULARNO", new DataValue(req.getModularNo(), Types.VARCHAR));
            							ub1.addCondition("TOKEN", new DataValue(req.getToken(), Types.VARCHAR));
            							data_d1.add(new DataProcessBean(ub1));
            							dao.useTransactionProcessData(data_d1);
        					        }else
        					        {
        					        	List<DataProcessBean> data_d1 =  new ArrayList<DataProcessBean>();
        					        	ColumnDataValue columnDataValueAuth = new ColumnDataValue();
        					        	columnDataValueAuth.Add("MODULARNO",req.getModularNo(), Types.VARCHAR);
        					        	columnDataValueAuth.Add("TOKEN",req.getToken(), Types.VARCHAR);
        					        	columnDataValueAuth.Add("RFUNCNO",rfuncNo, Types.VARCHAR);
        					        	columnDataValueAuth.Add("RMODULARNO",rmodularNo, Types.VARCHAR);
        					        	columnDataValueAuth.Add("CREATETIME",lastmoditime, Types.DATE);
        					        	columnDataValueAuth.Add("LASTMODITIME",lastmoditime, Types.DATE);
        		                    	String[] columnsAuth= columnDataValueAuth.Columns.toArray(new String[0]);
        		                    	DataValue[] insDataValueSale = columnDataValueAuth.DataValues.toArray(new DataValue[0]);
        		                    	InsBean ibAuth = new InsBean("DCP_MODULAR_WORKING_AUTH", columnsAuth);
        		                    	ibAuth.addValues(insDataValueSale);
        		                    	data_d1.add(new DataProcessBean(ibAuth));
            							dao.useTransactionProcessData(data_d1);
        					        }
        							return true;
        						}
        					}
        					return false;
        				}else
        				{
        					return false;
        				}
        			}else
        			{
        			  return true;	
        			}
        		}else
        		{
        			return true;
        		}
        	}else
        	{
        		return true;
        	}
        }else
        {
        	return false;
        }
    }
    /**
     * 授权模块并发用户数管控
     * @param req
     * @throws Exception
     */
    protected boolean AuthCheck(REQ req) throws Exception
    {
        if(Check.isEmpty(req.getModularNo()) || Check.isEmpty(req.geteId()) || Check.isEmpty(req.getToken()))
        {
            return true;
        }
        String sql = "select * from DCP_MODULAR A WHERE A.EID='"+req.geteId()+"' and a.MODULARNO='"+req.getModularNo()+"' ";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&getQData.isEmpty()==false)
        {
            String rfuncNo=getQData.get(0).get("RFUNCNO").toString();
            if(Check.isNotEmpty(rfuncNo))
            {
                sql="SELECT * FROM DCP_REGEDISTMODULAR B where B.RFUNCNO='"+rfuncNo+"' ";
                getQData = this.doQueryData(sql,null);
                if (getQData!=null&&getQData.isEmpty()==false)
                {
                    String rmodularNo=getQData.get(0).get("RMODULARNO").toString();
                    String modularRtypeInfo=getQData.get(0).get("RTYPEINFO").toString();
                    if("4".equals(modularRtypeInfo))//模块授权 and 并发数
                    {
                        sql="SELECT * FROM PLATFORM_CREGISTERDETAIL WHERE PRODUCTTYPE='"+rfuncNo+"'  AND BDATE<=TO_CHAR(SYSDATE,'YYYYMMDD')  AND EDATE>=TO_CHAR(SYSDATE,'YYYYMMDD')"
                                + " ORDER BY TOKEN ASC ";
                        getQData = this.doQueryData(sql,null);
                        if (getQData!=null&&getQData.isEmpty()==false)
                        {
                            for(Map<String,Object> map:getQData)
                            {
                                String queryToken=map.get("TOKEN").toString();
                                if(req.getToken().equals(queryToken))
                                {
                                    String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                    List<DataProcessBean> data =  new ArrayList<DataProcessBean>();
                                    UptBean ub = new UptBean("PLATFORM_CREGISTERDETAIL");
                                    ub.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
                                    ub.addCondition("MACHINECODE", new DataValue(map.get("MACHINECODE").toString(), Types.VARCHAR));
                                    ub.addCondition("TOKEN", new DataValue(req.getToken(), Types.VARCHAR));
                                    data.add(new DataProcessBean(ub));
                                    sql ="MERGE INTO DCP_MODULAR_WORKING_AUTH A " +
                                            "USING (SELECT '"+req.getModularNo()+"' AS MODULARNO, '"+req.getToken()+"' AS TOKEN,'"+rfuncNo+"' AS RFUNCNO,'"+rmodularNo+"' AS RMODULARNO,to_date('"+lastmoditime+"','YYYY-MM-DD HH24:MI:SS') AS CREATETIME,to_date('"+lastmoditime+"','YYYY-MM-DD HH24:MI:SS') AS LASTMODITIME   FROM dual) b " +
                                            "ON (A.MODULARNO=B.MODULARNO AND A.TOKEN=B.TOKEN) " +
                                            "WHEN MATCHED THEN " +
                                            "UPDATE SET A.LASTMODITIME=B.LASTMODITIME " +
                                            "WHEN NOT MATCHED THEN " +
                                            "INSERT (A.MODULARNO,A.TOKEN,A.RFUNCNO,A.RMODULARNO,A.CREATETIME,A.LASTMODITIME) " +
                                            "VALUES (B.MODULARNO,B.TOKEN,B.RFUNCNO,B.RMODULARNO,B.CREATETIME,B.LASTMODITIME) ";
                                    ExecBean exec = new ExecBean(sql);
                                    data.add(new DataProcessBean(exec));
                                    dao.useTransactionProcessData(data);
                                    return true;
                                }
                                if(Check.isEmpty(queryToken))
                                {
                                    String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                    List<DataProcessBean> data =  new ArrayList<DataProcessBean>();
                                    UptBean ub = new UptBean("PLATFORM_CREGISTERDETAIL");
                                    ub.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
                                    ub.addUpdateValue("TOKEN", new DataValue(req.getToken(), Types.VARCHAR));
                                    ub.addCondition("MACHINECODE", new DataValue(map.get("MACHINECODE").toString(), Types.VARCHAR));
                                    data.add(new DataProcessBean(ub));
//                                    dao.useTransactionProcessData(data);
                                    sql ="MERGE INTO DCP_MODULAR_WORKING_AUTH A " +
                                            "USING (SELECT '"+req.getModularNo()+"' AS MODULARNO, '"+req.getToken()+"' AS TOKEN,'"+rfuncNo+"' AS RFUNCNO,'"+rmodularNo+"' AS RMODULARNO,to_date('"+lastmoditime+"','YYYY-MM-DD HH24:MI:SS') AS CREATETIME,to_date('"+lastmoditime+"','YYYY-MM-DD HH24:MI:SS') AS LASTMODITIME   FROM dual) b " +
                                            "ON (A.MODULARNO=B.MODULARNO AND A.TOKEN=B.TOKEN) " +
                                            "WHEN MATCHED THEN " +
                                            "UPDATE SET A.LASTMODITIME=B.LASTMODITIME " +
                                            "WHEN NOT MATCHED THEN " +
                                            "INSERT (A.MODULARNO,A.TOKEN,A.RFUNCNO,A.RMODULARNO,A.CREATETIME,A.LASTMODITIME) " +
                                            "VALUES (B.MODULARNO,B.TOKEN,B.RFUNCNO,B.RMODULARNO,B.CREATETIME,B.LASTMODITIME) ";
                                    ExecBean exec = new ExecBean(sql);
                                    data.add(new DataProcessBean(exec));
                                    dao.useTransactionProcessData(data);
                                    return true;
                                }
                            }
                            return false;
                        }else
                        {
                            return false;
                        }
                    }else
                    {
                        return true;
                    }
                }else
                {
                    return true;
                }
            }else
            {
                return true;
            }
        }else
        {
            return false;
        }
    }

}

