package com.dsc.spos.waimai;

import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sankuai.meituan.shangou.open.sdk.domain.SystemParam;
import com.sankuai.meituan.shangou.open.sdk.exception.SgOpenException;
import com.sankuai.meituan.shangou.open.sdk.request.OrderRefundRejectRequest;
import com.sankuai.meituan.shangou.open.sdk.request.PoiMGetRequest;
import com.sankuai.meituan.shangou.open.sdk.request.RetailListRequest;
import com.sankuai.meituan.shangou.open.sdk.request.RetailSkuStockRequest;
import com.sankuai.meituan.shangou.open.sdk.response.SgOpenResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WMSGProductService {

    static String mtLogFileName = "MTSGProductlog";

    /**
     *查询门店商品列表
     * @param app_poi_code APP方门店id
     * @param offset 起始条目数(偏移量)
     * @param limit 每页大小(最多200)
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static JSONArray queryListByEPoiId(String app_poi_code,int offset,int limit, StringBuilder errorMessage ) throws Exception
    {
        try
        {

            HelpTools.writelog_fileName("【MTSG】查询门店商品信息请求：app_poi_code=" +app_poi_code+",起始条目offset="+offset+",每页大小limit="+limit,mtLogFileName);
            RetailListRequest request = new RetailListRequest(getSystemParam());
            request.setApp_poi_code(app_poi_code);
            request.setOffset(offset);
            request.setLimit(limit);
            SgOpenResponse sgOpenResponse;
            try {
                sgOpenResponse = request.doRequest();
            } catch (SgOpenException e) {
                //e.printStackTrace();
                errorMessage.append(e.getMessage());
                return null;
            } catch (Exception e) {
                //e.printStackTrace();
                errorMessage.append(e.getMessage());
                return null;
            }
            //发起请求时的sig，用来联系美团员工排查问题时使用
            //String requestSig = sgOpenResponse.getRequestSig();
            //请求返回的结果，按照官网的接口文档自行解析即可
            String requestResult = sgOpenResponse.getRequestResult();
            //HelpTools.writelog_fileName("【MTSG】查询门店商品信息返回res:" +requestResult,mtLogFileName);
            HelpTools.writelog_fileName("【MTSG】查询门店商品信息返回res:太多了不记录了！" ,mtLogFileName);
            //System.out.println(requestResult);
            org.json.JSONObject resObj = new JSONObject(requestResult);
            if (resObj.isNull("data"))
            {
                return null;
            }
            if ("ng".equals(resObj.optString("data")))
            {
                String error = resObj.optString("error","美团闪购返回查询失败");
                errorMessage.append(error);
                return null;
            }
            JSONArray jsonArray = resObj.getJSONArray("data");
            return jsonArray;

        }
        catch ( Exception e)
        {
            errorMessage.append(e.getMessage());
            return null;
        }

    }

    /**
     * 更新库存(传参对应文档https://opendj.meituan.com/home/docDetail/78)
     * @param app_poi_code
     * @param foodSkuStockParams 参考开发文档
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static boolean updateStock (String app_poi_code,List<Map<String,Object>> foodSkuStockParams,StringBuilder errorMessage) throws Exception
    {
        boolean nRet = false;
        try
        {

            String reqestId = UUID.randomUUID().toString().replace("-", "");
            String food_data = com.alibaba.fastjson.JSON.toJSONString(foodSkuStockParams);
            RetailSkuStockRequest request = new RetailSkuStockRequest (getSystemParam());
            request.setApp_poi_code(app_poi_code);
            request.setFood_data(food_data);
            //request.setReason(reason);
            HelpTools.writelog_fileName("【MTSG】更新门店商品库存，请求Id["+reqestId+"],门店app_poi_code=" +app_poi_code+",请求req="+food_data,mtLogFileName);
            SgOpenResponse sgOpenResponse;
            try {
                sgOpenResponse = request.doRequest();
            } catch (SgOpenException e) {
                //e.printStackTrace();
                errorMessage.append(e.getMessage());
                return false;
            } catch (Exception e) {
                //e.printStackTrace();
                errorMessage.append(e.getMessage());
                return false;
            }
            //发起请求时的sig，用来联系美团员工排查问题时使用
            //String requestSig = sgOpenResponse.getRequestSig();
            //请求返回的结果，按照官网的接口文档自行解析即可
            String requestResult = sgOpenResponse.getRequestResult();
            //System.out.println(requestResult);
            HelpTools.writelog_fileName("【MTSG】更新门店商品库存，请求Id["+reqestId+"],门店app_poi_code=" +app_poi_code+",返回res="+requestResult,mtLogFileName);
            org.json.JSONObject resObj = new JSONObject(requestResult);
            String data = resObj.optString("data", "");
            //{"data":"ok"}
            if ("OK".equalsIgnoreCase(data)) {
                return true;
            }
            //{"data":"ng","error":{"code":808,"msg":"操作失败,订单已经确认过了"}}
            org.json.JSONObject errorObj = resObj.getJSONObject("error");
            errorMessage.append(errorObj.optString("msg", ""));
            return false;

        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            return false;
        }
    }

    private static SystemParam getSystemParam() throws Exception
    {
        String appId = "";
        String appSecret = "";
        String eId = "";
        List<Map<String, Object>> elmAppKeyList = PosPub.getWaimaiAppConfig(StaticInfo.dao, eId,
                orderLoadDocType.MTSG);
        if (elmAppKeyList != null && elmAppKeyList.size() > 0)
        {
            appId = elmAppKeyList.get(0).get("APIKEY").toString();
            appSecret = elmAppKeyList.get(0).get("APISECRET").toString();
        }

        com.sankuai.meituan.shangou.open.sdk.domain.SystemParam sysPram = new SystemParam(appId, appSecret);

        return sysPram;
    }
}
