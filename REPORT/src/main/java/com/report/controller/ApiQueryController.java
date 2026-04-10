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
    
    private List<Map<String, String>> getLoginResponseFields() {
        List<Map<String, String>> fields = new ArrayList<>();
        fields.add(createField("token", "String", "", "登录 Token", ""));
        fields.add(createField("opno", "String", "", "操作员编号", ""));
        fields.add(createField("eid", "String", "", "企业编号", ""));
        fields.add(createField("expireTime", "String", "", "过期时间", ""));
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
}
