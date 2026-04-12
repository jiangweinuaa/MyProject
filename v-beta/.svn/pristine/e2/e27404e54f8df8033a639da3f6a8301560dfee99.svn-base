package com.dsc.spos.waimai.meituanJBP;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.waimai.HelpTools;

import java.util.HashMap;
import java.util.Map;

public class jbpService {

    private static final String logFileName = "jbpRequestLog";

    private static final String HOST = "https://api-open-cater.meituan.com";

    //版本
    private static final String version = "2";
    //
    private static final String charset = "utf-8";
    //如团购:1 外卖:2
    private static final String businessId= "2";


    /**
     * 美团聚宝盆（服务商模式）出餐接口
     * @param baseReq 公共参数
     * @param req 请求内容
     * @param error
     * @return
     * @throws Exception
     */
    public String preparationMealComplete(baseEntityParaReq baseReq, preparationMealCompleteReq req, StringBuffer error) throws  Exception
    {
        String method = "preparationMealComplete";
        String orderNo = "";
        try
        {
            orderNo = req.getOrderId();
            String url = HOST+"/waimai/order/"+method;
            String developerId = baseReq.getDeveloperId();
            String appAuthToken = baseReq.getAppAuthToken();
            String signKey = baseReq.getSignKey();
            String biz = JSONObject.toJSONString(req);
            String timestamp = System.currentTimeMillis()/1000+"";
            Map<String,String> param = new HashMap();
            //下方的参数为每次调用api需要的参数
            param.put("timestamp",timestamp);
            param.put("developerId",developerId);
            param.put("version",version);
            param.put("charset",charset);
            param.put("businessId",businessId);
            param.put("biz",biz);
            param.put("appAuthToken",appAuthToken);
            String sign=SignUtil.getSign(signKey,param);
            param.put("sign",sign);

            String res = jbpHttpClientUtil.postFormRequest(url,param);
            return  res;
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("【调用美团JBP接口】【出餐】"+method+",异常:"+e.getMessage()+",订单号orderNo="+orderNo,logFileName);
            error.append(e.getMessage());
            return "";
        }


    }

    /**
     * 批量更新菜品售卖状态（上下架）
     * @param baseReq
     * @param req
     * @param error
     * @return
     * @throws Exception
     */
    public String sellStatusUpdate(baseEntityParaReq baseReq, sellStatusReq req, StringBuffer error) throws Exception
    {
        String method = "sellStatus";
        String url = HOST+"/waimai/ng/dish/sku/"+method;
        try
        {
            String developerId = baseReq.getDeveloperId();
            String appAuthToken = baseReq.getAppAuthToken();
            String signKey = baseReq.getSignKey();
            String biz = JSONObject.toJSONString(req);
            String timestamp = System.currentTimeMillis()/1000+"";
            Map<String,String> param = new HashMap();
            //下方的参数为每次调用api需要的参数
            param.put("timestamp",timestamp);
            param.put("developerId",developerId);
            param.put("version",version);
            param.put("charset",charset);
            param.put("businessId",businessId);
            param.put("biz",biz);
            param.put("appAuthToken",appAuthToken);
            String sign=SignUtil.getSign(signKey,param);
            param.put("sign",sign);
            HelpTools.writelog_fileName("【聚宝盆】上下架请求："+biz,method);
            String res = jbpHttpClientUtil.postFormRequest(url,param);
            HelpTools.writelog_fileName("【聚宝盆】上下架返回："+res,method);
            return  res;

        }
        catch (Exception e)
        {
            return "";
        }
    }

    public String batchUpload(baseEntityParaReq baseReq, sellStatusReq req, StringBuffer error) throws Exception
    {


        return "";
    }

    /**
     * 上传菜品图片到美团云，支持jpg，jpeg格式，上传成功会返回图片id，可以使用此id值批量上传菜品
     * @param baseReq 公共请求参数
     * @param ePoiId ERP方门店id 最大长度100
     * @param imageName 图片名称 文件名只能是字母或数字,且必须以.jpg结尾
     * @param fileBase64 图片base64内容。支持jpg、jpeg格式，图片需要小于1600*1200
     * @param error
     * @return
     * @throws Exception
     */
    public String imageUpload(baseEntityParaReq baseReq, String ePoiId, String imageName, String fileBase64, StringBuffer error) throws Exception
    {
        String method = "imageUpload";
        String url = HOST+"/waimai/image/upload";
        try
        {
            JSONObject req = new JSONObject();
            req.put("ePoiId",ePoiId);
            req.put("imageName",imageName);
            req.put("file",fileBase64);
            String developerId = baseReq.getDeveloperId();
            String appAuthToken = baseReq.getAppAuthToken();
            String signKey = baseReq.getSignKey();
            String biz = req.toJSONString();
            String timestamp = System.currentTimeMillis()/1000+"";
            Map<String,String> param = new HashMap();
            //下方的参数为每次调用api需要的参数
            param.put("timestamp",timestamp);
            param.put("developerId",developerId);
            param.put("version",version);
            param.put("charset",charset);
            param.put("businessId",businessId);
            param.put("biz",biz);
            param.put("appAuthToken",appAuthToken);
            String sign=SignUtil.getSign(signKey,param);
            param.put("sign",sign);
            HelpTools.writelog_fileName("【聚宝盆】上传图片请求："+biz,method);
            String res = jbpHttpClientUtil.postFormRequest(url,param);
            HelpTools.writelog_fileName("【聚宝盆】上传图片返回："+res,method);
            return  res;

        }
        catch (Exception e)
        {
            return "";
        }

    }



}
