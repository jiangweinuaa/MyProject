package com.dsc.spos.waimai;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.sankuai.meituan.shangou.open.sdk.domain.SystemParam;
import com.sankuai.meituan.shangou.open.sdk.exception.SgOpenException;
import com.sankuai.meituan.shangou.open.sdk.request.PoiGetIdsRequest;
import com.sankuai.meituan.shangou.open.sdk.request.PoiMGetRequest;
import com.sankuai.meituan.shangou.open.sdk.response.SgOpenResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WMSGShopService {
    public static String mtLogFileName = "MTSGShopServicelog";

    /**
     * 批量获取门店的app_poi_code，包括正式门店和审核中的门店
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static List<String> getShopIds(StringBuilder errorMessage) throws Exception
    {
        try
        {
            HelpTools.writelog_fileName("【MTSG】批量获取门店的app_poi_code开始",mtLogFileName);
            PoiGetIdsRequest request = new PoiGetIdsRequest(getSystemParam());
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
            //System.out.println(requestResult);
            HelpTools.writelog_fileName("【MTSG】批量获取门店的app_poi_code结束,返回res:" +requestResult,mtLogFileName);
            org.json.JSONObject resObj = new JSONObject(requestResult);
            if (resObj.isNull("data"))
            {
                errorMessage.append("美团返回为空");
                return null;
            }
            String data = resObj.optString("data", "");
            if ("ng".equalsIgnoreCase(data))
            {
                org.json.JSONObject errorObj = resObj.getJSONObject("error");
                errorMessage.append(errorObj.optString("msg", ""));
                return null;
            }
            JSONArray jsonArray = resObj.getJSONArray("data");
            List<String> shopIds =  new ArrayList<String>();
            for(int i = 0;i<jsonArray.length();i++)
            {
                shopIds.add(jsonArray.get(i).toString());
            }

            return shopIds;

        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            return null;
        }
    }

    /**
     * 批量获取门店的所有详细信息
     * @param shopList
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static JSONArray getShopIdsInfo(List<String> shopList,StringBuilder errorMessage ) throws Exception
    {
        try
        {
            String app_poi_codes = "";
            if(shopList==null||shopList.size()==0)
            {
                errorMessage.append("请求的门店列表为空！");
                return null;
            }
            for (String item_shop : shopList)
            {
                app_poi_codes +=item_shop+",";
            }
            HelpTools.writelog_fileName("【MT】查询门店信息请求：app_poi_codes=" +app_poi_codes,mtLogFileName);
            PoiMGetRequest request = new PoiMGetRequest(getSystemParam());
            request.setApp_poi_codes(app_poi_codes);
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
            HelpTools.writelog_fileName("【MT】查询门店信息返回res:" +requestResult,mtLogFileName);
            //System.out.println(requestResult);
            org.json.JSONObject resObj = new JSONObject(requestResult);
            if (resObj.isNull("data"))
            {
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
