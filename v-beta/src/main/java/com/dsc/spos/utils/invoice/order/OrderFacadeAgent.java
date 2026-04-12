package com.dsc.spos.utils.invoice.order;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.utils.invoice.order.dto.InvoiceResult;
import com.dsc.spos.utils.invoice.order.dto.Order;
import com.dsc.spos.utils.invoice.order.dto.OrderBase;
import com.dsc.spos.utils.invoice.order.dto.Result;
import com.dsc.spos.utils.invoice.utils.CertificateUtils;
import com.dsc.spos.utils.invoice.utils.HttpUtil;
import com.dsc.spos.waimai.HelpTools;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单接口代理实现
 */
public class OrderFacadeAgent implements OrderFacade {
    private String appCode, keyStorePath, alias, password, apiUrl;

    /**
     * 构造方法
     * @param appCode 由电子发票平台分配的appCode
     * @param keyStorePath 密钥库文件路径
     * @param alias 密钥库别名
     * @param password 密钥库密码
     * @param apiUrl 接口URL
     */
    public OrderFacadeAgent(String appCode, String keyStorePath, String alias, String password, String apiUrl) {
        this.appCode = appCode;
        this.keyStorePath = keyStorePath;
        this.alias = alias;
        this.password = password;
        this.apiUrl = apiUrl;
    }

    /**
     * (异步)开具蓝字发票
     */
    public Result kpAsync(Order order) throws Exception {
        return exec(order, "chinaeinv.api.order.v11.kp_async", Result.class);
    }

    /**
     * (异步)开具红字发票
     */
    public Result chAsync(Order order) throws Exception {
        return exec(order, "chinaeinv.api.order.v11.ch_async", Result.class);
    }

    /**
     * (异步)对开票失败的订单重新开票
     */
    public Result retryKpAsync(OrderBase orderBase) throws Exception {
        return exec(orderBase, "chinaeinv.api.order.v11.retryKp_async", Result.class);
    }

    /**
     * (同步)取消订单
     */
    public Result cancel(OrderBase orderBase) throws Exception {
        return exec(orderBase, "chinaeinv.api.order.v11.cancel", Result.class);
    }

    /**
     * (同步)根据订单编号查询发票
     */
    public InvoiceResult cxByOrderNo(OrderBase orderBase) throws Exception {
        return exec(orderBase, "chinaeinv.api.order.v11.cx.orderNo", InvoiceResult.class);
    }

    /**
     * 调用接口
     * @param order 请求数据
     * @return 响应数据
     * @throws Exception
     */
    private <T> T exec(OrderBase order, String cmdName, Class<T> resultType) throws Exception {
        // 将Order对象转为json
        String requestJson = JSON.toJSONString(order);
        System.out.println("方法名称: " + cmdName + ", 请求报文: " + requestJson);
        HelpTools.writelog_fileName(
                "方法名称: " + cmdName + ", 请求报文: " + requestJson,
                "invoiceService");
        // 生成签名
        String sign = CertificateUtils.signToBase64(requestJson.getBytes("UTF-8"), keyStorePath, alias, password);
        System.out.println("方法名称: " + cmdName + ", 签名: " + sign);
        HelpTools.writelog_fileName(
                "方法名称: " + cmdName + ", 签名: " + sign,
                "invoiceService");
        // 调用接口
        Map<String, String> params = new HashMap<>();
        params.put("appCode", appCode);
        params.put("cmdName", cmdName);
        params.put("sign", URLEncoder.encode(sign,"UTF-8"));
        String responseJson = HttpUtil.doPost(apiUrl, params, requestJson);
        System.out.println("方法名称: " + cmdName + ", 响应报文: " + responseJson);
        HelpTools.writelog_fileName(
                "方法名称: " + cmdName + ", 响应报文: " + responseJson,
                "invoiceService");

        return JSON.parseObject(responseJson, resultType);
    }
}
