package com.dsc.spos.thirdpart.xiaoyou;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.waimai.HelpTools;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class xiaoyouService {

    private static final String logFileName = "xiaoyouLog";
    //超时时间，默认10秒
    private int timeout = 10000;
    private  String HOST = "http://api.topuoo.com/index";//http://apimy.miyapay.com/index

    /**
     * 微商城订单发货(sendWeshopOrder)
     * @param setMap 设置参数map
     * @param orderNo 单号
     * @param detailList 商品明细
     * @return
     * @throws Exception
     */
    public String sendWeshopOrder(Map<String, Object> setMap, String orderNo,Map<String, Object> orderMap, List<Map<String, Object>> detailList) throws Exception
    {
        String url = HOST;
        String method = "sendWeshopOrder";
        String api_key = setMap.getOrDefault("APIKEY","").toString();//190633ce38293d2863e89f7ce274ba25
        String secret_key = setMap.getOrDefault("APISECRET","").toString();//57777e0ddbaf324edc1c64b40091c68a
        req_sendWeshopOrder  req = new req_sendWeshopOrder();
        req.setApi_key(api_key);
        req.setMethod(method);
        req.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        req.setOrder_no(orderNo);
        req.setOrder_dtl(new ArrayList<String>());
        String exp_no = orderMap.getOrDefault("DELIVERYNO","").toString();
        String exp_code = orderMap.getOrDefault("DELIVERYTYPE","").toString();
        if (exp_code.isEmpty())
        {
            exp_code ="YUNDA";
        }

        for (Map<String, Object> par : detailList)
        {
            req_sendWeshopOrder.orderDtl orderDtl = new req_sendWeshopOrder.orderDtl();
            orderDtl.setSku_code(par.getOrDefault("PLUBARCODE","").toString());
            orderDtl.setExp_no(exp_no);
            orderDtl.setExp_code(exp_code);
            if ("YUNDA".equals(exp_code))
            {
                orderDtl.setExp_name("韵达快递");
            }
            req.getOrder_dtl().add(JSONObject.toJSONString(orderDtl));
        }

        String reqStr = getBody(req,secret_key);
        String res = post(url,reqStr,timeout);


        return res;

    }


    public String addRefundAction(Map<String, Object> setMap, String orderNo) throws Exception
    {
        String url = HOST;
        String method = "addRefundAction";
        String api_key = setMap.getOrDefault("APIKEY","").toString();//190633ce38293d2863e89f7ce274ba25
        String secret_key = setMap.getOrDefault("APISECRET","").toString();//57777e0ddbaf324edc1c64b40091c68a
        req_addRefundAction  req = new req_addRefundAction();
        req.setApi_key(api_key);
        req.setMethod(method);
        req.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        req.setRefund_no(orderNo);
        req.setAction_desc("确认收货");
        String reqStr = getBody(req,secret_key);
        String res = post(url,reqStr,timeout);

        return res;

    }

    private String getBody(Object bean,String secret_key)
    {
        //body
        Map<String, Object> map = BeanUtil.beanToMap(bean, false, true);
        //签名
        String signList = MapUtil.sortJoin(map, "&", "=", true);
        String signature = SecureUtil.md5(signList+secret_key).toUpperCase();

        String params = escapeUtil(map)+"&sign="+signature;

        return params;

    }

    private String post(String url,String body,int timeout) throws Exception
    {
        HelpTools.writelog_fileName("晓柚PostUrl:"+url + ",请求Req:"+body,logFileName);
        try {
            String res = HttpUtil.post(url, body, timeout);
            try
            {
                res = Convert.unicodeToStr(res);
            }
            catch (Exception e)
            {
            }

            HelpTools.writelog_fileName("晓柚PostUrl:"+url + ",返回Res:"+res,logFileName);
            return res;
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName("晓柚PostUrl:"+url + ",返回异常:"+e.getMessage(),logFileName);
            return "";
        }

    }

    private String escapeUtil(Map<String, Object> map) {
        for (Map.Entry<String, Object> m : map.entrySet()) {
            if(m.getValue() instanceof List) {
                List<String> list = Convert.toList(String.class, m.getValue());
                List<String> newList = new ArrayList<>();
                for (String value : list) {
                    newList.add(value.replace("&", "%26").replace("+", "%2B"));
                }
                m.setValue(newList);
            }
            if(m.getValue() instanceof String) {
                String str = Convert.toStr(m.getValue());
                m.setValue(str.replace("&", "%26").replace("+", "%2B"));
            }
            map.put(m.getKey(), m.getValue());
        }
        return MapUtil.sortJoin(map, "&", "=", true);
    }

}
