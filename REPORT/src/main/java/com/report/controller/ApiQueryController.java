package com.report.controller;

import com.report.dto.ServiceResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * API 接口查询服务
 * 提供接口文档查看功能
 */
@RestController
@RequestMapping("/api")
public class ApiQueryController {

    /**
     * 获取所有接口清单
     */
    @GetMapping("/api-query/list")
    public ServiceResponse<?> getApiList() {
        List<Map<String, Object>> apiList = new ArrayList<>();
        
        // 销售相关接口
        apiList.add(createApiInfo("DaySaleQuery", "每日销售查询", 
            "查询指定日期范围内的销售数据，按门店和日期汇总", 
            "POST", "/api/service"));
        
        apiList.add(createApiInfo("DcpSaleQuery", "商品销售明细查询", 
            "查询商品销售明细，支持按门店和日期筛选，考虑退货正负号", 
            "POST", "/api/service"));
        
        apiList.add(createApiInfo("CategorySaleQuery", "品类销售分析查询", 
            "查询按品类汇总的销售数据，支持按门店和日期筛选", 
            "POST", "/api/service"));
        
        apiList.add(createApiInfo("DayShopGoodsQuery", "每日门店商品查询", 
            "查询指定门店的商品销售数据", 
            "POST", "/api/service"));
        
        apiList.add(createApiInfo("DayChannelQuery", "每日渠道查询", 
            "查询按渠道汇总的销售数据", 
            "POST", "/api/service"));
        
        apiList.add(createApiInfo("DayShopChannelQuery", "每日门店渠道查询", 
            "查询指定门店的渠道销售数据", 
            "POST", "/api/service"));
        
        // 库存相关接口
        apiList.add(createApiInfo("StockSumQuery", "门店库存汇总查询", 
            "查询各门店的库存汇总数据", 
            "POST", "/api/service"));
        
        apiList.add(createApiInfo("StockQuery", "商品库存查询", 
            "查询商品库存明细，支持按门店筛选", 
            "POST", "/api/service"));
        
        // 系统接口
        apiList.add(createApiInfo("AllEidQuery", "查询所有企业编号", 
            "查询系统中所有可用的企业编号", 
            "POST", "/api/service"));
        
        apiList.add(createApiInfo("UserLogin", "用户登录", 
            "用户登录接口，返回 token", 
            "POST", "/api/service"));
        
        // 销售预估相关接口
        apiList.add(createApiInfo("ShopSaleForecastQuery", "销售预估准确性分析", 
            "查询销售预估准确性分析完整数据，包含准确率、偏差分析、误差分布、按日期/星期维度分析等", 
            "POST", "/api/service"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", apiList);
        result.put("total", apiList.size());
        
        return ServiceResponse.success(result, "查询成功");
    }
    
    /**
     * 获取接口详细信息
     */
    @GetMapping("/api-query/detail")
    public ServiceResponse<?> getApiDetail(@RequestParam String serviceId) {
        Map<String, Object> detail = getApiDetailMap(serviceId);
        
        if (detail == null) {
            return ServiceResponse.error("404", "接口不存在：" + serviceId);
        }
        
        return ServiceResponse.success(detail, "查询成功");
    }
    
    /**
     * 创建接口信息
     */
    private Map<String, Object> createApiInfo(String serviceId, String name, 
                                               String description, String method, String url) {
        Map<String, Object> api = new HashMap<>();
        api.put("serviceId", serviceId);
        api.put("name", name);
        api.put("description", description);
        api.put("method", method);
        api.put("url", url);
        return api;
    }
    
    /**
     * 获取接口详细信息
     */
    private Map<String, Object> getApiDetailMap(String serviceId) {
        Map<String, Object> detail = new HashMap<>();
        detail.put("serviceId", serviceId);
        
        switch (serviceId) {
            case "DaySaleQuery":
                detail.put("name", "每日销售查询");
                detail.put("description", "查询指定日期范围内的销售数据，按门店和日期汇总");
                detail.put("requestFields", getRequestFields("date,shop"));
                detail.put("responseFields", getResponseFields("sales"));
                detail.put("requestExample", getRequestExample("date"));
                detail.put("responseExample", getResponseExample("sales"));
                break;
                
            case "DcpSaleQuery":
                detail.put("name", "商品销售明细查询");
                detail.put("description", "查询商品销售明细，支持按门店和日期筛选，考虑退货正负号");
                detail.put("requestFields", getRequestFields("date,shop"));
                detail.put("responseFields", getResponseFields("detail"));
                detail.put("requestExample", getRequestExample("date"));
                detail.put("responseExample", getResponseExample("detail"));
                break;
                
            case "CategorySaleQuery":
                detail.put("name", "品类销售分析查询");
                detail.put("description", "查询按品类汇总的销售数据，支持按门店和日期筛选");
                detail.put("requestFields", getRequestFields("date,shop"));
                detail.put("responseFields", getResponseFields("category"));
                detail.put("requestExample", getRequestExample("date"));
                detail.put("responseExample", getResponseExample("category"));
                break;
                
            case "DayShopGoodsQuery":
                detail.put("name", "每日门店商品查询");
                detail.put("description", "查询指定门店的商品销售数据");
                detail.put("requestFields", getRequestFields("date,shop"));
                detail.put("responseFields", getResponseFields("goods"));
                detail.put("requestExample", getRequestExample("date,shop"));
                detail.put("responseExample", getResponseExample("goods"));
                break;
                
            case "DayChannelQuery":
                detail.put("name", "每日渠道查询");
                detail.put("description", "查询按渠道汇总的销售数据");
                detail.put("requestFields", getRequestFields("date"));
                detail.put("responseFields", getResponseFields("channel"));
                detail.put("requestExample", getRequestExample("date"));
                detail.put("responseExample", getResponseExample("channel"));
                break;
                
            case "DayShopChannelQuery":
                detail.put("name", "每日门店渠道查询");
                detail.put("description", "查询指定门店的渠道销售数据");
                detail.put("requestFields", getRequestFields("date,shop"));
                detail.put("responseFields", getResponseFields("channel"));
                detail.put("requestExample", getRequestExample("date,shop"));
                detail.put("responseExample", getResponseExample("channel"));
                break;
                
            case "StockSumQuery":
                detail.put("name", "门店库存汇总查询");
                detail.put("description", "查询各门店的库存汇总数据");
                detail.put("requestFields", getRequestFields("shop"));
                detail.put("responseFields", getResponseFields("stock"));
                detail.put("requestExample", getRequestExample(""));
                detail.put("responseExample", getResponseExample("stock"));
                break;
                
            case "StockQuery":
                detail.put("name", "商品库存查询");
                detail.put("description", "查询商品库存明细，支持按门店筛选");
                detail.put("requestFields", getRequestFields("shop"));
                detail.put("responseFields", getResponseFields("stock"));
                detail.put("requestExample", getRequestExample(""));
                detail.put("responseExample", getResponseExample("stock"));
                break;
                
            case "AllEidQuery":
                detail.put("name", "查询所有企业编号");
                detail.put("description", "查询系统中所有可用的企业编号");
                detail.put("requestFields", new ArrayList<>());
                detail.put("responseFields", getResponseFields("eid"));
                detail.put("requestExample", "{}");
                detail.put("responseExample", getResponseExample("eid"));
                break;
                
            case "UserLogin":
                detail.put("name", "用户登录");
                detail.put("description", "用户登录接口，返回 token");
                detail.put("requestFields", getLoginRequestFields());
                detail.put("responseFields", getLoginResponseFields());
                detail.put("requestExample", getLoginRequestExample());
                detail.put("responseExample", getLoginResponseExample());
                break;
                
            case "ShopSaleForecastQuery":
                detail.put("name", "销售预估准确性分析");
                detail.put("description", "查询销售预估准确性分析完整数据，包含准确率、偏差分析、误差分布、按日期/星期维度分析等");
                detail.put("requestFields", getForecastRequestFields());
                detail.put("responseFields", getForecastResponseFields());
                detail.put("requestExample", getForecastRequestExample());
                detail.put("responseExample", getForecastResponseExample());
                break;
                
            default:
                return null;
        }
        
        detail.put("method", "POST");
        detail.put("url", "/api/service");
        
        return detail;
    }
    
    // ============ 字段定义 ============
    
    private List<Map<String, String>> getRequestFields(String type) {
        List<Map<String, String>> fields = new ArrayList<>();
        
        if (type.contains("date")) {
            fields.add(createField("startDate", "String", "是", "开始日期", "yyyyMMdd"));
            fields.add(createField("endDate", "String", "是", "截止日期", "yyyyMMdd"));
        }
        
        if (type.contains("shop")) {
            fields.add(createField("shopId", "String", "否", "门店 ID", "留空查询所有门店"));
        }
        
        fields.add(createField("token", "String", "是", "登录 Token", "从 sign.token 传递"));
        
        return fields;
    }
    
    private List<Map<String, String>> getResponseFields(String type) {
        List<Map<String, String>> fields = new ArrayList<>();
        
        // 公共字段
        fields.add(createField("success", "Boolean", "", "是否成功", ""));
        fields.add(createField("serviceStatus", "String", "服务状态码", "000 表示成功"));
        fields.add(createField("totalRecords", "Integer", "总记录数", ""));
        fields.add(createField("totalPages", "Integer", "总页数", ""));
        fields.add(createField("pageNumber", "Integer", "当前页码", ""));
        fields.add(createField("pageSize", "Integer", "每页大小", ""));
        
        if (type.equals("sales") || type.equals("detail") || type.equals("goods") || type.equals("channel")) {
            fields.add(createField("list[].SHOPID", "String", "", "门店 ID", ""));
            fields.add(createField("list[].ORG_NAME", "String", "", "门店名称", ""));
            fields.add(createField("list[].SALEDATE", "String", "销售日期", "yyyyMMdd"));
            fields.add(createField("list[].AMOUNT", "Number", "", "销售金额", ""));
            fields.add(createField("list[].ORDERCOUNT", "Integer", "", "订单数", ""));
        }
        
        if (type.equals("detail")) {
            fields.add(createField("list[].SALENO", "String", "", "销售单号", ""));
            fields.add(createField("list[].TOT_OLDAMT", "Number", "原金额", "退货为负"));
            fields.add(createField("list[].TOT_DISC", "Number", "折扣", "退货为负"));
            fields.add(createField("list[].TOT_AMT", "Number", "销售金额", "退货为负"));
            fields.add(createField("list[].SDATE", "String", "", "系统日期", ""));
            fields.add(createField("list[].STIME", "String", "", "系统时间", ""));
        }
        
        if (type.equals("goods")) {
            fields.add(createField("list[].PLUNO", "String", "", "商品编码", ""));
            fields.add(createField("list[].PLU_NAME", "String", "", "商品名称", ""));
            fields.add(createField("list[].QTY", "Number", "", "销售数量", ""));
        }
        
        if (type.equals("channel")) {
            fields.add(createField("list[].CHANNELID", "String", "", "渠道 ID", ""));
            fields.add(createField("list[].CHANNEL_NAME", "String", "", "渠道名称", ""));
        }
        
        if (type.equals("stock")) {
            fields.add(createField("list[].SHOPID", "String", "", "门店 ID", ""));
            fields.add(createField("list[].ORG_NAME", "String", "", "门店名称", ""));
            fields.add(createField("list[].PLUNO", "String", "", "商品编码", ""));
            fields.add(createField("list[].PLU_NAME", "String", "", "商品名称", ""));
            fields.add(createField("list[].STOCK_QTY", "Number", "", "库存数量", ""));
            fields.add(createField("list[].STOCK_AMT", "Number", "", "库存金额", ""));
        }
        
        if (type.equals("eid")) {
            fields.add(createField("list[]", "Array", "", "企业编号列表", ""));
        }
        
        return fields;
    }
    
    private List<Map<String, String>> getLoginRequestFields() {
        List<Map<String, String>> fields = new ArrayList<>();
        fields.add(createField("username", "String", "是", "用户名", ""));
        fields.add(createField("password", "String", "是", "密码", ""));
        return fields;
    }
    
    private List<Map<String, String>> getForecastRequestFields() {
        List<Map<String, String>> fields = new ArrayList<>();
        fields.add(createField("shopId", "String", "否", "门店 ID", "可选，不传查询当前用户默认门店"));
        fields.add(createField("startDate", "String", "是", "开始日期", "YYYY-MM-DD 格式"));
        fields.add(createField("endDate", "String", "是", "截止日期", "YYYY-MM-DD 格式"));
        fields.add(createField("token", "String", "是", "登录 Token", "从 sign.token 传递"));
        return fields;
    }
    
    private List<Map<String, String>> getLoginResponseFields() {
        List<Map<String, String>> fields = new ArrayList<>();
        fields.add(createField("token", "String", "", "登录 Token", ""));
        fields.add(createField("opno", "String", "", "操作员编号", ""));
        fields.add(createField("eid", "String", "", "企业编号", ""));
        fields.add(createField("expireTime", "String", "", "过期时间", ""));
        return fields;
    }
    
    private List<Map<String, String>> getForecastResponseFields() {
        List<Map<String, String>> fields = new ArrayList<>();
        
        // 公共字段
        fields.add(createField("success", "Boolean", "", "是否成功", ""));
        fields.add(createField("serviceDescription", "String", "", "服务描述", ""));
        
        // 核心指标
        fields.add(createField("metrics.accuracyRate", "Number", "", "综合准确率", "100 - MAPE"));
        fields.add(createField("metrics.salesMAPE", "Number", "", "销售额 MAPE", "平均绝对百分比误差"));
        fields.add(createField("metrics.salesMAE", "Number", "", "销售额 MAE", "平均绝对误差"));
        fields.add(createField("metrics.salesRMSE", "Number", "", "销售额 RMSE", "均方根误差"));
        fields.add(createField("metrics.validDays", "Integer", "", "有效分析天数", ""));
        fields.add(createField("metrics.abnormalDays", "Integer", "", "异常天数", "预估值为负数的天数"));
        
        // 偏差分析
        fields.add(createField("bias.salesBias", "Number", "", "销售额偏差", "平均预估 - 平均实际"));
        fields.add(createField("bias.biasDirection", "String", "", "偏差方向", "高估/低估/准确"));
        fields.add(createField("bias.totalBiasRate", "Number", "", "总偏差率", "%"));
        fields.add(createField("bias.totalForecastSales", "Number", "", "预估总销售额", ""));
        fields.add(createField("bias.totalActualSales", "Number", "", "实际总销售额", ""));
        fields.add(createField("bias.difference", "Number", "", "差异", ""));
        
        // 按日期维度分析
        fields.add(createField("dateAnalysis[].date", "String", "", "日期", "YYYY-MM-DD"));
        fields.add(createField("dateAnalysis[].weekday", "String", "", "星期", ""));
        fields.add(createField("dateAnalysis[].forecastSales", "Number", "", "预估销售额", ""));
        fields.add(createField("dateAnalysis[].actualSales", "Number", "", "实际销售额", ""));
        fields.add(createField("dateAnalysis[].errorSales", "Number", "", "误差销售额", "预估 - 实际"));
        fields.add(createField("dateAnalysis[].errorRate", "Number", "", "误差率", "%，带正负号"));
        fields.add(createField("dateAnalysis[].mape", "Number", "", "MAPE", "绝对误差率%"));
        fields.add(createField("dateAnalysis[].accuracy", "Number", "", "准确率", "%"));
        
        // 按星期维度分析
        fields.add(createField("weekdayData[].weekday", "String", "", "星期", ""));
        fields.add(createField("weekdayData[].days", "Integer", "", "天数", ""));
        fields.add(createField("weekdayData[].forecastAvg", "Number", "", "预估均值", ""));
        fields.add(createField("weekdayData[].actualAvg", "Number", "", "实际均值", ""));
        fields.add(createField("weekdayData[].mae", "Number", "", "MAE", ""));
        fields.add(createField("weekdayData[].bias", "Number", "", "Bias", ""));
        fields.add(createField("weekdayData[].accuracy", "Number", "", "准确率", "%"));
        
        // 误差分布
        fields.add(createField("errorDist[].range", "String", "", "误差范围", "如 0-5%"));
        fields.add(createField("errorDist[].count", "Integer", "", "天数", ""));
        fields.add(createField("errorDist[].percentage", "Number", "", "占比", "%"));
        
        // 逐日数据（图表用）
        fields.add(createField("dailyData[].date", "String", "", "日期", ""));
        fields.add(createField("dailyData[].forecast", "Number", "", "预估销售额", ""));
        fields.add(createField("dailyData[].actual", "Number", "", "实际销售额", ""));
        fields.add(createField("dailyData[].accuracy", "Number", "", "准确率", "%"));
        
        // 异常天数
        fields.add(createField("abnormalDays[].date", "String", "", "异常日期", ""));
        fields.add(createField("abnormalDays[].value", "String", "", "异常值", ""));
        
        // 综合评级
        fields.add(createField("rating.stars", "Integer", "", "星级", "1-5"));
        fields.add(createField("rating.text", "String", "", "评级文字", "优秀/良好/一般/较差/差"));
        fields.add(createField("rating.description", "String", "", "评级描述", ""));
        
        return fields;
    }
    
    private Map<String, String> createField(String name, String type, String description) {
        Map<String, String> field = new HashMap<>();
        field.put("name", name);
        field.put("type", type);
        field.put("description", description);
        return field;
    }
    
    private Map<String, String> createField(String name, String type, String required, String description) {
        Map<String, String> field = new HashMap<>();
        field.put("name", name);
        field.put("type", type);
        field.put("required", required);
        field.put("description", description);
        field.put("remark", "");
        return field;
    }
    
    private Map<String, String> createField(String name, String type, String required, 
                                             String description, String remark) {
        Map<String, String> field = new HashMap<>();
        field.put("name", name);
        field.put("type", type);
        field.put("required", required);
        field.put("description", description);
        field.put("remark", remark);
        return field;
    }
    
    // ============ 示例 ============
    
    private String getRequestExample(String type) {
        if (type.equals("date")) {
            return "{\n" +
                   "  \"serviceId\": \"DaySaleQuery\",\n" +
                   "  \"request\": {\n" +
                   "    \"startDate\": \"20260401\",\n" +
                   "    \"endDate\": \"20260407\"\n" +
                   "  },\n" +
                   "  \"sign\": {\n" +
                   "    \"key\": \"\",\n" +
                   "    \"sign\": \"\",\n" +
                   "    \"token\": \"5E99F26A391F4E1293FCC01BFD3ACD9F\"\n" +
                   "  },\n" +
                   "  \"pageNumber\": 1,\n" +
                   "  \"pageSize\": 20\n" +
                   "}";
        }
        
        if (type.equals("date,shop")) {
            return "{\n" +
                   "  \"serviceId\": \"DayShopGoodsQuery\",\n" +
                   "  \"request\": {\n" +
                   "    \"startDate\": \"20260401\",\n" +
                   "    \"endDate\": \"20260407\",\n" +
                   "    \"shopId\": \"01\"\n" +
                   "  },\n" +
                   "  \"sign\": {\n" +
                   "    \"key\": \"\",\n" +
                   "    \"sign\": \"\",\n" +
                   "    \"token\": \"5E99F26A391F4E1293FCC01BFD3ACD9F\"\n" +
                   "  }\n" +
                   "}";
        }
        
        if (type.equals("")) {
            return "{\n" +
                   "  \"serviceId\": \"StockSumQuery\",\n" +
                   "  \"request\": {},\n" +
                   "  \"sign\": {\n" +
                   "    \"key\": \"\",\n" +
                   "    \"sign\": \"\",\n" +
                   "    \"token\": \"5E99F26A391F4E1293FCC01BFD3ACD9F\"\n" +
                   "  }\n" +
                   "}";
        }
        
        return "{}";
    }
    
    private String getResponseExample(String type) {
        if (type.equals("sales")) {
            return "{\n" +
                   "  \"datas\": {\n" +
                   "    \"list\": [\n" +
                   "      {\n" +
                   "        \"SHOPID\": \"01\",\n" +
                   "        \"ORG_NAME\": \"V3 新街口店\",\n" +
                   "        \"SALEDATE\": \"20260401\",\n" +
                   "        \"AMOUNT\": 325.2,\n" +
                   "        \"ORDERCOUNT\": 11\n" +
                   "      }\n" +
                   "    ],\n" +
                   "    \"summary\": {\n" +
                   "      \"totalAmount\": 912.64,\n" +
                   "      \"totalOrderCount\": 41,\n" +
                   "      \"shopCount\": 2,\n" +
                   "      \"avgOrderValue\": 22.26\n" +
                   "    }\n" +
                   "  },\n" +
                   "  \"success\": true,\n" +
                   "  \"serviceStatus\": \"000\",\n" +
                   "  \"totalRecords\": 6,\n" +
                   "  \"totalPages\": 1,\n" +
                   "  \"pageNumber\": 1,\n" +
                   "  \"pageSize\": 0\n" +
                   "}";
        }
        
        if (type.equals("detail")) {
            return "{\n" +
                   "  \"datas\": {\n" +
                   "    \"list\": [\n" +
                   "      {\n" +
                   "        \"SHOPID\": \"01\",\n" +
                   "        \"SALENO\": \"012375520260407130905990\",\n" +
                   "        \"TOT_OLDAMT\": 100.0,\n" +
                   "        \"TOT_DISC\": 10.0,\n" +
                   "        \"TOT_AMT\": 90.0,\n" +
                   "        \"BDATE\": \"20260407\",\n" +
                   "        \"SDATE\": \"20260407\",\n" +
                   "        \"STIME\": \"130913\"\n" +
                   "      }\n" +
                   "    ]\n" +
                   "  },\n" +
                   "  \"success\": true,\n" +
                   "  \"serviceStatus\": \"000\"\n" +
                   "}";
        }
        
        if (type.equals("goods")) {
            return "{\n" +
                   "  \"datas\": {\n" +
                   "    \"list\": [\n" +
                   "      {\n" +
                   "        \"PLUNO\": \"P001\",\n" +
                   "        \"PLU_NAME\": \"商品 A\",\n" +
                   "        \"AMOUNT\": 100.0,\n" +
                   "        \"QTY\": 10\n" +
                   "      }\n" +
                   "    ],\n" +
                   "    \"summary\": {\n" +
                   "      \"totalAmount\": 1000.0,\n" +
                   "      \"totalQty\": 100,\n" +
                   "      \"goodsCount\": 10\n" +
                   "    }\n" +
                   "  },\n" +
                   "  \"success\": true\n" +
                   "}";
        }
        
        if (type.equals("channel")) {
            return "{\n" +
                   "  \"datas\": {\n" +
                   "    \"list\": [\n" +
                   "      {\n" +
                   "        \"CHANNELID\": \"C01\",\n" +
                   "        \"CHANNEL_NAME\": \"线上渠道\",\n" +
                   "        \"AMOUNT\": 500.0,\n" +
                   "        \"ORDERCOUNT\": 20\n" +
                   "      }\n" +
                   "    ]\n" +
                   "  },\n" +
                   "  \"success\": true\n" +
                   "}";
        }
        
        if (type.equals("stock")) {
            return "{\n" +
                   "  \"datas\": {\n" +
                   "    \"list\": [\n" +
                   "      {\n" +
                   "        \"SHOPID\": \"01\",\n" +
                   "        \"ORG_NAME\": \"V3 新街口店\",\n" +
                   "        \"PLUNO\": \"P001\",\n" +
                   "        \"PLU_NAME\": \"商品 A\",\n" +
                   "        \"STOCK_QTY\": 100,\n" +
                   "        \"STOCK_AMT\": 1000.0\n" +
                   "      }\n" +
                   "    ],\n" +
                   "    \"summary\": {\n" +
                   "      \"totalQty\": 1000,\n" +
                   "      \"totalAmt\": 10000.0,\n" +
                   "      \"shopCount\": 5,\n" +
                   "      \"goodsCount\": 50\n" +
                   "    }\n" +
                   "  },\n" +
                   "  \"success\": true\n" +
                   "}";
        }
        
        if (type.equals("eid")) {
            return "{\n" +
                   "  \"datas\": {\n" +
                   "    \"list\": [\"00\", \"06\", \"11\", \"66\", \"99\"]\n" +
                   "  },\n" +
                   "  \"success\": true,\n" +
                   "  \"serviceDescription\": \"查询成功，共 5 个企业\"\n" +
                   "}";
        }
        
        return "{}";
    }
    
    private String getLoginRequestExample() {
        return "{\n" +
               "  \"username\": \"admin\",\n" +
               "  \"password\": \"123456\"\n" +
               "}";
    }
    
    private String getLoginResponseExample() {
        return "{\n" +
               "  \"datas\": {\n" +
               "    \"token\": \"5E99F26A391F4E1293FCC01BFD3ACD9F\",\n" +
               "    \"opno\": \"admin\",\n" +
               "    \"eid\": \"66\",\n" +
               "    \"expireTime\": \"24h\"\n" +
               "  },\n" +
               "  \"success\": true,\n" +
               "  \"serviceStatus\": \"000\"\n" +
               "}";
    }
    
    private String getForecastRequestExample() {
        return "{\n" +
               "  \"serviceId\": \"ShopSaleForecastQuery\",\n" +
               "  \"request\": {\n" +
               "    \"shopId\": \"120021\",\n" +
               "    \"startDate\": \"2026-03-01\",\n" +
               "    \"endDate\": \"2026-03-31\"\n" +
               "  },\n" +
               "  \"sign\": {\n" +
               "    \"key\": \"\",\n" +
               "    \"sign\": \"\",\n" +
               "    \"token\": \"5E99F26A391F4E1293FCC01BFD3ACD9F\"\n" +
               "  }\n" +
               "}";
    }
    
    private String getForecastResponseExample() {
        return "{\n" +
               "  \"datas\": {\n" +
               "    \"metrics\": {\n" +
               "      \"accuracyRate\": 85.5,\n" +
               "      \"salesMAPE\": 14.5,\n" +
               "      \"salesMAE\": 125.5,\n" +
               "      \"salesRMSE\": 180.2,\n" +
               "      \"validDays\": 28,\n" +
               "      \"abnormalDays\": 2\n" +
               "    },\n" +
               "    \"bias\": {\n" +
               "      \"salesBias\": 95.5,\n" +
               "      \"biasDirection\": \"高估\",\n" +
               "      \"totalBiasRate\": 8.2,\n" +
               "      \"totalForecastSales\": 35280.5,\n" +
               "      \"totalActualSales\": 32450.0,\n" +
               "      \"difference\": 2830.5\n" +
               "    },\n" +
               "    \"dateAnalysis\": [\n" +
               "      {\n" +
               "        \"date\": \"2026-03-01\",\n" +
               "        \"weekday\": \"星期日\",\n" +
               "        \"forecastSales\": 1200.0,\n" +
               "        \"actualSales\": 1100.0,\n" +
               "        \"errorSales\": 100.0,\n" +
               "        \"errorRate\": 9.1,\n" +
               "        \"mape\": 9.1,\n" +
               "        \"accuracy\": 90.9\n" +
               "      }\n" +
               "    ],\n" +
               "    \"weekdayData\": [\n" +
               "      {\n" +
               "        \"weekday\": \"星期一\",\n" +
               "        \"days\": 4,\n" +
               "        \"forecastAvg\": 1150.0,\n" +
               "        \"actualAvg\": 1080.0,\n" +
               "        \"mae\": 85.5,\n" +
               "        \"bias\": 70.0,\n" +
               "        \"accuracy\": 93.5\n" +
               "      }\n" +
               "    ],\n" +
               "    \"errorDist\": [\n" +
               "      {\"range\": \"0-5%\", \"count\": 10, \"percentage\": 35.7},\n" +
               "      {\"range\": \"5-10%\", \"count\": 8, \"percentage\": 28.6},\n" +
               "      {\"range\": \"10-20%\", \"count\": 6, \"percentage\": 21.4},\n" +
               "      {\"range\": \"20-30%\", \"count\": 3, \"percentage\": 10.7},\n" +
               "      {\"range\": \"30-50%\", \"count\": 1, \"percentage\": 3.6}\n" +
               "    ],\n" +
               "    \"dailyData\": [\n" +
               "      {\n" +
               "        \"date\": \"2026-03-01\",\n" +
               "        \"forecast\": 1200.0,\n" +
               "        \"actual\": 1100.0,\n" +
               "        \"accuracy\": 90.9\n" +
               "      }\n" +
               "    ],\n" +
               "    \"abnormalDays\": [],\n" +
               "    \"rating\": {\n" +
               "      \"stars\": 4,\n" +
               "      \"text\": \"良好 ★★★★☆\",\n" +
               "      \"description\": \"销量 MAPE = 14.5%（剔除 2 天异常预估）\"\n" +
               "    }\n" +
               "  },\n" +
               "  \"success\": true,\n" +
               "  \"serviceDescription\": \"分析成功\"\n" +
               "}";
    }
}
