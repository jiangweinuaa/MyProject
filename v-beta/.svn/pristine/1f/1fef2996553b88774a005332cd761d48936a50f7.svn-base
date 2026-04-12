package com.dsc.spos.waimai;

import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class WMDYOrderProcess {

    private static String logFileName = "douyinWMLog";
    private static String HOST="https://open.douyin.com";

    /**
     * 同意接单
     * @param isTest
     * @param clientKey
     * @param clientSecret
     * @param orderNo
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static boolean orderConfirm(boolean isTest,String clientKey,String clientSecret,String orderNo,StringBuilder errorMessage) throws Exception
    {
        boolean nRet = false;
        String api = "/goodlife/v1/trade/buy/merchant_confirm_order/";
        String req = "";
        String res = "";
        String reqIdLog = "请求ID["+UUID.randomUUID().toString().replace("-","")+"]";
        try {
            String token = WMDYUtilTools.GetTokenInRedis(isTest,clientKey,clientSecret);
            String url = HOST+api;
            Map<String,Object> headerParams = new HashMap<>();
            if (isTest)
            {
                headerParams.put("X-SandBox-Token","1");
            }
            headerParams.put("access-token",token);

            Map<String,Object> reqMap = new HashMap<>();
            reqMap.put("order_id",orderNo);
            req = com.alibaba.fastjson.JSON.toJSONString(reqMap);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM确认接单接口URL】:"+url+",请求header内容："+headerParams.toString()+",请求req:"+req, logFileName);
            res = WMDYUtilTools.postJson(url,headerParams,req);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM确认接单接口】返回res:"+res, logFileName);
            //{"data":{"error_code":0,"description":"success"},"extra":{"description":"success","error_code":0,"logid":"20220825160718010225082199060C966B"}}
            org.json.JSONObject resJson = new org.json.JSONObject(res);
            org.json.JSONObject extraObj = resJson.getJSONObject("extra");
            String error_code = extraObj.get("error_code").toString();//网关层必须为0，
            //齐坤  14:31
            //回复 陶日平: 
            //能确认下吗。是不是 extra 和 data节点 下面的 error_code 都是0 才表示 ，接口返回成功
            //@陶日平 这两个是一样的，判断一个就可以了
            if (!"0".equals(error_code))
            {
                String msg = extraObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }
           /* org.json.JSONObject dataObj = resJson.getJSONObject("data");
            error_code = dataObj.get("error_code").toString();
            if (!"0".equals(error_code))
            {
                String msg = dataObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }*/
            return true;

        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            HelpTools.writelog_fileName(reqIdLog+"【DYWM确认接单接口】异常:"+e.getMessage(), logFileName);
            return false;
        }

    }

    /**
     * 拒绝接单
     * @param isTest
     * @param clientKey
     * @param clientSecret
     * @param orderNo
     * @param reason
     * @param reasonCode
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static boolean orderCancel(boolean isTest,String clientKey,String clientSecret,String orderNo,String reason, String reasonCode,StringBuilder errorMessage) throws Exception
    {
        boolean nRet = false;
        String api = "/goodlife/v1/after_sale/order/merchant_reject/";
        String req = "";
        String res = "";
        String reqIdLog = "请求ID["+UUID.randomUUID().toString().replace("-","")+"]";
        try {
            String token = WMDYUtilTools.GetTokenInRedis(isTest,clientKey,clientSecret);
            String url = HOST+api;
            Map<String,Object> headerParams = new HashMap<>();
            if (isTest)
            {
                headerParams.put("X-SandBox-Token","1");
            }
            headerParams.put("access-token",token);

            Map<String,Object> reqMap = new HashMap<>();
            reqMap.put("order_id",orderNo);
            Map<String,Object> reject_reasonMap = new HashMap<>();
            List<Integer> codeList = new ArrayList<>();
            codeList.add(502);//"501": "用户信息错误","502": "商品已经售完", "503": "商家已经打烊", "504": "商家现在太忙","505": "超出配送范围",
            reject_reasonMap.put("reason_code",codeList);
            reject_reasonMap.put("desc","商品已经售完");
            reqMap.put("reject_reason",reject_reasonMap);
            req = com.alibaba.fastjson.JSON.toJSONString(reqMap);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM拒绝接单接口URL】:"+url+",请求header内容："+headerParams.toString()+",请求req:"+req, logFileName);
            res = WMDYUtilTools.postJson(url,headerParams,req);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM拒绝接单接口】返回res:"+res, logFileName);
            //{"data":{"error_code":0,"description":"success"},"extra":{"description":"success","error_code":0,"logid":"20220825160718010225082199060C966B"}}
            org.json.JSONObject resJson = new org.json.JSONObject(res);
            org.json.JSONObject extraObj = resJson.getJSONObject("extra");
            String error_code = extraObj.get("error_code").toString();//网关层必须为0，
            //齐坤  14:31
            //回复 陶日平: 
            //能确认下吗。是不是 extra 和 data节点 下面的 error_code 都是0 才表示 ，接口返回成功
            //@陶日平 这两个是一样的，判断一个就可以了
            if (!"0".equals(error_code))
            {
                String msg = extraObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }
           /* org.json.JSONObject dataObj = resJson.getJSONObject("data");
            error_code = dataObj.get("error_code").toString();
            if (!"0".equals(error_code))
            {
                String msg = dataObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }*/
            return true;

        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            HelpTools.writelog_fileName(reqIdLog+"【DYWM拒绝接单接口】异常:"+e.getMessage(), logFileName);
            return false;
        }

    }

    /**
     * 同意退款
     * @param isTest
     * @param clientKey
     * @param clientSecret
     * @param orderNo 原订单号
     * @param after_sale_id 同意申请退单SPI消息中售后单ID(退单单号)
     * @param reason
     * @param reasonCode
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static boolean orderRefundAgree(boolean isTest,String clientKey,String clientSecret,String orderNo,String after_sale_id,String reason, String reasonCode,StringBuilder errorMessage) throws Exception
    {
        boolean nRet = false;
        String api = "/goodlife/v1/after_sale/audit/notify/";
        String req = "";
        String res = "";
        String reqIdLog = "请求ID["+UUID.randomUUID().toString().replace("-","")+"]";
        boolean is_approved = true;//同意退单
        try {
            String token = WMDYUtilTools.GetTokenInRedis(isTest,clientKey,clientSecret);
            String url = HOST+api;
            Map<String,Object> headerParams = new HashMap<>();
            if (isTest)
            {
                headerParams.put("X-SandBox-Token","1");
            }
            headerParams.put("access-token",token);

            Map<String,Object> reqMap = new HashMap<>();
            reqMap.put("after_sale_id",after_sale_id);
            reqMap.put("is_approved",is_approved);
            req = com.alibaba.fastjson.JSON.toJSONString(reqMap);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM同意退单接口URL】:"+url+",请求header内容："+headerParams.toString()+",请求req:"+req+",原订单号orderNo:"+orderNo, logFileName);
            res = WMDYUtilTools.postJson(url,headerParams,req);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM同意退单接口】返回res:"+res+",原订单号orderNo:"+orderNo, logFileName);
            //{"data":{"error_code":0,"description":"success"},"extra":{"description":"success","error_code":0,"logid":"20220825160718010225082199060C966B"}}
            org.json.JSONObject resJson = new org.json.JSONObject(res);
            org.json.JSONObject extraObj = resJson.getJSONObject("extra");
            String error_code = extraObj.get("error_code").toString();//网关层必须为0，
            //齐坤  14:31
            //回复 陶日平: 
            //能确认下吗。是不是 extra 和 data节点 下面的 error_code 都是0 才表示 ，接口返回成功
            //@陶日平 这两个是一样的，判断一个就可以了
            if (!"0".equals(error_code))
            {
                String msg = extraObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }
           /* org.json.JSONObject dataObj = resJson.getJSONObject("data");
            error_code = dataObj.get("error_code").toString();
            if (!"0".equals(error_code))
            {
                String msg = dataObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }*/
            return true;

        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            HelpTools.writelog_fileName(reqIdLog+"【DYWM同意退单接口】异常:"+e.getMessage()+",原订单号orderNo:"+orderNo, logFileName);
            return false;
        }

    }

    /**
     * 同意/拒绝退款
     * @param isTest
     * @param clientKey
     * @param clientSecret
     * @param orderNo 原订单号
     * @param after_sale_id 同意申请退单SPI消息中售后单ID(退单单号)
     * @param reason
     * @param reasonCode
     * @param errorMessage
     * @return
     * @throws Exception
     */
    public static boolean orderRefundReject(boolean isTest,String clientKey,String clientSecret,String orderNo,String after_sale_id,String reason, String reasonCode,StringBuilder errorMessage) throws Exception
    {
        boolean nRet = false;
        String api = "/goodlife/v1/after_sale/audit/notify/";
        String req = "";
        String res = "";
        String reqIdLog = "请求ID["+UUID.randomUUID().toString().replace("-","")+"]";
        boolean is_approved = false;//拒绝退单
        try {
            String token = WMDYUtilTools.GetTokenInRedis(isTest,clientKey,clientSecret);
            String url = HOST+api;
            Map<String,Object> headerParams = new HashMap<>();
            if (isTest)
            {
                headerParams.put("X-SandBox-Token","1");
            }
            headerParams.put("access-token",token);

            Map<String,Object> reqMap = new HashMap<>();
            reqMap.put("after_sale_id",after_sale_id);
            reqMap.put("is_approved",is_approved);
            Map<String,Object> reject_reasonMap = new HashMap<>();
            List<Integer> codeList = new ArrayList<>();
            codeList.add(101);//"101": "商家已出餐", "102": "骑手已在取货途中","103": "其他原因", "104": "商品已送出", "105": "已与用户达成一致","106": "用户已收到商品","107": "餐品已在制作中"
            codeList.add(103);
            reject_reasonMap.put("reason_code",codeList);
            reject_reasonMap.put("desc","其他原因");
            reqMap.put("reject_reason",reject_reasonMap);
            req = com.alibaba.fastjson.JSON.toJSONString(reqMap);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM拒绝退单接口URL】:"+url+",请求header内容："+headerParams.toString()+",请求req:"+req+",原订单号orderNo:"+orderNo, logFileName);
            res = WMDYUtilTools.postJson(url,headerParams,req);
            HelpTools.writelog_fileName(reqIdLog+"【DYWM拒绝退单接口】返回res:"+res+",原订单号orderNo:"+orderNo, logFileName);
            //{"data":{"error_code":0,"description":"success"},"extra":{"description":"success","error_code":0,"logid":"20220825160718010225082199060C966B"}}
            org.json.JSONObject resJson = new org.json.JSONObject(res);
            org.json.JSONObject extraObj = resJson.getJSONObject("extra");
            String error_code = extraObj.get("error_code").toString();//网关层必须为0，
            //齐坤  14:31
            //回复 陶日平: 
            //能确认下吗。是不是 extra 和 data节点 下面的 error_code 都是0 才表示 ，接口返回成功
            //@陶日平 这两个是一样的，判断一个就可以了
            if (!"0".equals(error_code))
            {
                String msg = extraObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }
           /* org.json.JSONObject dataObj = resJson.getJSONObject("data");
            error_code = dataObj.get("error_code").toString();
            if (!"0".equals(error_code))
            {
                String msg = dataObj.get("description").toString();
                errorMessage.append(msg);
                return false;
            }*/
            return true;

        }
        catch (Exception e)
        {
            errorMessage.append(e.getMessage());
            HelpTools.writelog_fileName(reqIdLog+"【DYWM拒绝退单接口】异常:"+e.getMessage()+",原订单号orderNo:"+orderNo, logFileName);
            return false;
        }

    }
}
