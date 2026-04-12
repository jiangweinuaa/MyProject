package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopManagePrintCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutDetailQueryReq;
import com.dsc.spos.json.cust.req.DCP_StockTakeDetailReq;
import com.dsc.spos.json.cust.res.DCP_ShopManagePrintCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DCP_ShopManagePrintCreate extends SPosBasicService<DCP_ShopManagePrintCreateReq, DCP_ShopManagePrintCreateRes> {
    @Override
    protected boolean isVerifyFail(DCP_ShopManagePrintCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if(Check.Null(req.getRequest().geteId())){
            errMsg.append("企业eId不能为空值,");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getShopId())){
            errMsg.append("门店shopId不能为空值,");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getPrintBillType())){
            errMsg.append("单据类型printBillType不能为空值,");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getBillNo())){
            errMsg.append("单据编号billNo不能为空值,");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getDatas()))
        {
            errMsg.append("打印的内容datas不能为空值,");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ShopManagePrintCreateReq> getRequestType() {
        return new TypeToken<DCP_ShopManagePrintCreateReq>(){};
    }

    @Override
    protected DCP_ShopManagePrintCreateRes getResponseType() {
        return new DCP_ShopManagePrintCreateRes();
    }

    @Override
    protected DCP_ShopManagePrintCreateRes processJson(DCP_ShopManagePrintCreateReq req) throws Exception {

        DCP_ShopManagePrintCreateRes res = this.getResponse();
        res.setDatas(res.new level1Elm());
        DCP_ShopManagePrintCreateRes.level1Elm res_datas = res.new level1Elm();

        String token = req.getToken();
        String eId_token = req.geteId();
        String shopId_token = req.getShopId();
        String eId_req = req.getRequest().geteId();
        String shopId_req = req.getRequest().getShopId();
        //检核下，后面会根据token去查询单身明细
        if (!eId_token.equals(eId_req))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "token里面的eId和requset参数传入eId不一致！");
        }
        if (!shopId_token.equals(shopId_req))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "token里面的shopId和requset参数传入shopId不一致！");
        }

        //库存盘点（10）、报损出库（07）、调拨出库（04）、调拨收货（02）、加工任务（080）、完工入库（08）；
        //退货出库 （03）、采购入库（05）、采购退货（06）、其他出库（15）、试吃出库（15）、赠送出库（15）、其他入库（14）
        //盘点模板商品明细(PDMB)
        String printBillType = req.getRequest().getPrintBillType();
        String billNo = req.getRequest().getBillNo();
        String printBillTypeName = getBillTypeName(printBillType);
        StringBuffer errorMessage = new StringBuffer("");

        //KEY = 时间戳_单据类型_单号 【时间戳 YYYYMMDDHHMMSSSSS ,方便排序】
        String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String batchKey = curDateTime+"_"+printBillType+"_"+billNo;
        String redis_key = "shopManageOrderPrint"+":"+eId_req+":"+shopId_req;
        String hash_key = batchKey;

        res_datas.setBatchKey(batchKey);
        String datasReqStr = req.getRequest().getDatas();

        JSONObject datasReq = new JSONObject(datasReqStr);
        datasReq.put("printBillType",printBillType);
        ParseJson pj = new ParseJson();
        if ("10".equals(printBillType))
        {
            printBillTypeName = "库存盘点";
            //库存盘点，需要查询明细 DCP_StockTakeDetail
            DCP_StockTakeDetailReq req_other = new DCP_StockTakeDetailReq();
            DCP_StockTakeDetailReq.levelElm request_other = req_other.new levelElm();
            request_other.setStockTakeNo(billNo);
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put("serviceId", "DCP_StockTakeDetail");
            //这个token是无意义的
            jsonMap.put("token", token);
            jsonMap.put("request", request_other);
            //json
            String json_other = pj.beanToJson(jsonMap);
            DispatchService ds = DispatchService.getInstance();
            String resXML = ds.callService(json_other, StaticInfo.dao);
            JSONObject json_res = new JSONObject(resXML);
            JSONArray datas_res = json_res.optJSONArray("datas");
            if (datas_res==null||datas_res.length()==0)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询库存盘点单("+billNo+")商品明细为空！");
            }
            datasReq.put("datas",datas_res);
            //开始写缓存
            String hash_value = datasReq.toString();
            boolean nRet = WriteRedis(redis_key,hash_key,hash_value,printBillTypeName,errorMessage);
            if (nRet)
            {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功!");
                res.setDatas(res_datas);
            }
            else
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("服务执行失败:"+errorMessage.toString());
            }
            return res;
        }
        else if ("03".equals(printBillType)||"04".equals(printBillType)||"15".equals(printBillType))
        {
            //退货出库（03）、调拨出库（04）、其他出库（15）
           // printBillTypeName = "调拨出库/其他出库";
            //库存盘点，需要查询明细 DCP_StockTakeDetail
            DCP_StockOutDetailQueryReq req_other = new DCP_StockOutDetailQueryReq();
            DCP_StockOutDetailQueryReq.levelElm request_other = req_other.new levelElm();
            request_other.setStockOutNo(billNo);
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put("serviceId", "DCP_StockOutDetailQuery");
            //这个token是无意义的
            jsonMap.put("token", token);
            jsonMap.put("request", request_other);
            //json
            String json_other = pj.beanToJson(jsonMap);
            DispatchService ds = DispatchService.getInstance();
            String resXML = ds.callService(json_other, StaticInfo.dao);
            JSONObject json_res = new JSONObject(resXML);
            JSONArray datas_res = json_res.optJSONArray("datas");
            if (datas_res==null||datas_res.length()==0)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询"+printBillTypeName+"("+billNo+")商品明细为空！");
            }
            datasReq.put("datas",datas_res);
            //开始写缓存
            String hash_value = datasReq.toString();
            boolean nRet = WriteRedis(redis_key,hash_key,hash_value,printBillTypeName,errorMessage);
            if (nRet)
            {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功!");
                res.setDatas(res_datas);
            }
            else
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("服务执行失败:"+errorMessage.toString());
            }
            return res;
        }
        else
        {
            //开始写缓存
            String hash_value = datasReq.toString();
            boolean nRet = WriteRedis(redis_key,hash_key,hash_value,printBillTypeName,errorMessage);
            if (nRet)
            {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功!");
                res.setDatas(res_datas);
            }
            else
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("服务执行失败:"+errorMessage.toString());
            }
            return res;

        }

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ShopManagePrintCreateReq req) throws Exception {
        return null;
    }

    private boolean WriteRedis(String redis_key, String hash_key, String hash_value,String printBillTypeName,StringBuffer errorMessage) throws Exception
    {
        boolean result = false;
        String logFileName = "DCP_ShopManagePrintCreate";
        try
        {
            RedisPosPub redis = new RedisPosPub();
            HelpTools.writelog_fileName(
                    "【"+printBillTypeName+"】【开始写缓存】" + " redis_key:" + redis_key + ",hash_key:" + hash_key + ",hash_value:" + hash_value,logFileName);
            redis.DeleteHkey(redis_key, hash_key);//
            boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
            if (nret)
            {
                result = true;
                HelpTools.writelog_fileName("【"+printBillTypeName+"】【开始写缓存】OK" + " redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
            } else
            {
                errorMessage.append("写缓存失败！");
                HelpTools.writelog_fileName("【"+printBillTypeName+"】【写缓存】Error" + " redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
            }
            // redis.Close();


        } catch (Exception e)
        {
            errorMessage.append("写缓存异常:"+e.getMessage());
            HelpTools.writelog_fileName(
                    "【"+printBillTypeName+"】【开始写缓存】异常:" + e.getMessage() + ",redis_key:" + redis_key + ",hash_key:" + hash_key,logFileName);
        }
        return result;
    }
    private String getBillTypeName(String billType)
    {
        String billTypeName = "未知单据";
        switch (billType)
        {
            case "10":
                billTypeName = "库存盘点";
                break;
            case "07":
                billTypeName = "报损出库";
                break;
            case "04":
                billTypeName = "调拨出库";
                break;
            case "02":
                billTypeName = "调拨收货";
                break;
            case "080":
                billTypeName = "加工任务";
                break;
            case "08":
                billTypeName = "完工入库";
                break;
            case "03":
                billTypeName = "退货出库";
                break;
            case "05":
                billTypeName = "采购入库";
                break;
            case "06":
                billTypeName = "采购退货";
                break;
            case "14":
                billTypeName = "其他入库";
                break;
            case "15":
                billTypeName = "其他出库";
                break;
            case "100":
                billTypeName = "库存调整";
                break;
            case "PDMB":
                billTypeName = "盘点模板商品明细";
                break;
            default:
                billTypeName = "未知单据";
                break;

        }
        return billTypeName;
    }
}
