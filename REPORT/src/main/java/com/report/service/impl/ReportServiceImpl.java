package com.report.service.impl;

import com.report.dto.*;
import com.report.service.ReportService;
import com.report.util.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {

    /**
     * 默认密钥 (生产环境请从配置读取)
     */
    private static final String DEFAULT_KEY = "digiwin";

    /**
     * 默认 EID (生产环境从 token 解析)
     */
    private static final String DEFAULT_EID = "99";

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据 token 解析获取 Token 信息
     * @param token token 字符串
     * @return TokenInfo 对象，包含 EID 等信息
     */
    private TokenInfo getTokenInfo(String token) {
        // TODO: 后续实现真实的 token 解析逻辑
        // 目前暂时返回默认 EID
        return new TokenInfo(DEFAULT_EID);
    }

    /**
     * 从请求参数中获取 EID
     * @param params 请求参数
     * @return EID 字符串
     */
    private String getEidFromParams(Object params) {
        String eid = DEFAULT_EID;
        
        if (params instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) params;
            Object signObj = paramMap.get("sign");
            if (signObj instanceof Map) {
                Map<?, ?> signMap = (Map<?, ?>) signObj;
                Object tokenObj = signMap.get("token");
                if (tokenObj != null && tokenObj.toString() != null && !tokenObj.toString().isEmpty()) {
                    TokenInfo tokenInfo = getTokenInfo(tokenObj.toString());
                    eid = tokenInfo.getEID();
                }
            }
        }
        
        return eid;
    }

    @Override
    public ServiceResponse<?> execute(ServiceRequest request) {
        String serviceId = request.getServiceId();
        Object params = request.getRequest();
        SignInfo signInfo = request.getSign();

        // 验证签名
        if (signInfo != null) {
            String key = signInfo.getKey();
            String sign = signInfo.getSign();
            // 这里可以根据业务需要验证签名
            // boolean valid = SignUtil.verifySign(params.toString(), key, sign);
        }

        // 根据 serviceId 路由到不同的服务
        switch (serviceId) {
            case "QueryMember":
                return queryMember(params);
            case "QueryReport":
                return queryReport(params);
            case "DaySaleQuery":
                return daySaleQuery(params);
            case "DayShopGoodsQuery":
                return dayShopGoodsQuery(params);
            case "DayChannelQuery":
                return dayChannelQuery(params);
            case "DayShopChannelQuery":
                return dayShopChannelQuery(params);
            case "StockQuery":
                return stockQuery(params);
            case "StockSumQuery":
                return stockSumQuery(params);
            case "GetStockColumns":
                return getStockColumns(params);
            default:
                return ServiceResponse.error("999", "未知服务 ID: " + serviceId);
        }
    }

    @Override
    public ServiceResponse<?> executeByServiceId(String serviceId, Object params, String key) {
        ServiceRequest request = new ServiceRequest();
        request.setServiceId(serviceId);
        request.setRequest(params);
        
        if (key != null && params != null) {
            String sign = SignUtil.generateSign(params.toString(), key);
            request.setSign(new SignInfo(key, sign));
        } else {
            request.setSign(new SignInfo(DEFAULT_KEY, ""));
        }

        return execute(request);
    }

    /**
     * 查询会员示例服务
     */
    private ServiceResponse<?> queryMember(Object params) {
        Map<String, Object> memberData = new HashMap<>();
        
        // 模拟会员数据
        memberData.put("memberId", "6220013808");
        memberData.put("createTime", "2023-04-18 09:44:49");
        memberData.put("mobile", "13952010514");
        memberData.put("name", "微信用户");
        memberData.put("cardNo", "00000000313");
        memberData.put("cardType", "0001");
        memberData.put("cardTypeName", "测金");
        memberData.put("levelId", "1");
        memberData.put("point", 0);
        memberData.put("shopId", "1002");
        memberData.put("shopName", "演示门店");

        ServiceResponse<Map<String, Object>> response = ServiceResponse.success(memberData);
        
        // 生成响应签名
        String respSign = SignUtil.generateSign(memberData.toString(), DEFAULT_KEY);
        response.setSign(new SignInfo(DEFAULT_KEY, respSign));

        return response;
    }

    /**
     * 查询报表示例服务
     */
    private ServiceResponse<?> queryReport(Object params) {
        Map<String, Object> reportData = new HashMap<>();
        
        // 模拟报表数据
        reportData.put("reportId", "RPT20260403001");
        reportData.put("reportName", "销售日报");
        reportData.put("reportDate", "2026-04-03");
        reportData.put("totalAmount", 12580.50);
        reportData.put("orderCount", 156);

        ServiceResponse<Map<String, Object>> response = ServiceResponse.success(reportData);
        
        String respSign = SignUtil.generateSign(reportData.toString(), DEFAULT_KEY);
        response.setSign(new SignInfo(DEFAULT_KEY, respSign));

        return response;
    }

    /**
     * 每日销售查询服务（按门店维度）
     * SQL: select a.SHOPID, gl.ORG_NAME, a.BDATE as SALEDATE, 
     *      SUM(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as AMOUNT, 
     *      COUNT(*) as ORDERCOUNT 
     *      FROM DCP_SALE a left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.SHOPID 
     *      where a.BDATE >= '开始日期' AND a.BDATE <= '截止日期' 
     *      group by a.BDATE,a.SHOPID,gl.ORG_NAME order by a.BDATE,a.SHOPID,gl.ORG_NAME
     */
    private ServiceResponse<?> daySaleQuery(Object params) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 参数在 request 嵌套对象中
                Object requestObj = paramMap.get("request");
                if (requestObj instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) requestObj;
                    if (requestMap.get("startDate") != null) {
                        startDate = requestMap.get("startDate").toString();
                    }
                    if (requestMap.get("endDate") != null) {
                        endDate = requestMap.get("endDate").toString();
                    }
                }
            } else if (params instanceof DaySaleQueryRequest) {
                DaySaleQueryRequest req = (DaySaleQueryRequest) params;
                if (req.getStartDate() != null) {
                    startDate = req.getStartDate();
                }
                if (req.getEndDate() != null) {
                    endDate = req.getEndDate();
                }
            }

            String eid = getEidFromParams(params);
            
            String sql = "select a.SHOPID, gl.ORG_NAME, a.BDATE as SALEDATE, " +
                    "SUM(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as AMOUNT, " +
                    "COUNT(*) as ORDERCOUNT " +
                    "FROM DCP_SALE a " +
                    "left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.SHOPID " +
                    "where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? AND gl.LANG_TYPE = 'zh_CN' " +
                    "group by a.BDATE, a.SHOPID, gl.ORG_NAME " +
                    "order by a.BDATE, a.SHOPID, gl.ORG_NAME";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, eid, startDate, endDate);

            // 计算汇总数据
            double totalAmount = 0;
            int totalOrderCount = 0;
            java.util.Set<String> shopSet = new java.util.HashSet<>();
            
            for (Map<String, Object> row : resultList) {
                Object amountObj = row.get("AMOUNT");
                if (amountObj != null) {
                    totalAmount += Double.parseDouble(amountObj.toString());
                }
                Object orderCountObj = row.get("ORDERCOUNT");
                if (orderCountObj != null) {
                    totalOrderCount += Integer.parseInt(orderCountObj.toString());
                }
                Object shopIdObj = row.get("SHOPID");
                if (shopIdObj != null) {
                    shopSet.add(shopIdObj.toString());
                }
            }
            
            double avgOrderValue = totalOrderCount > 0 ? totalAmount / totalOrderCount : 0;

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("total", resultList.size());
            resultData.put("startDate", startDate);
            resultData.put("endDate", endDate);
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("totalOrderCount", totalOrderCount);
            summary.put("shopCount", shopSet.size());
            summary.put("avgOrderValue", avgOrderValue);
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = ServiceResponse.success(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 每日门店商品查询服务
     * SQL: select d.PLUNO, gl.PLU_NAME, 
     *      SUM(case when a.type='1' or a.type='2' or a.type='4' then -(d.AMT) else (d.AMT) end) as AMOUNT, 
     *      SUM(case when a.type='1' or a.type='2' or a.type='4' then -(d.BASEQTY) else (d.BASEQTY) end) as QTY 
     *      FROM DCP_SALE a JOIN DCP_SALE_DETAIL d on d.EID = a.EID and d.SHOPID = a.SHOPID and d.SALENO = a.SALENO 
     *      left join DCP_GOODS_LANG gl on gl.EID = d.EID and gl.PLUNO = d.PLUNO and gl.LANG_TYPE = 'zh_CN' 
     *      where a.BDATE >= '开始日期' AND a.BDATE <= '截止日期' AND a.SHOPID = '门店' 
     *      group by d.PLUNO,gl.PLU_NAME order by d.PLUNO,gl.PLU_NAME
     */
    private ServiceResponse<?> dayShopGoodsQuery(Object params) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 参数在 request 嵌套对象中
                Object requestObj = paramMap.get("request");
                if (requestObj instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) requestObj;
                    if (requestMap.get("startDate") != null) {
                        startDate = requestMap.get("startDate").toString();
                    }
                    if (requestMap.get("endDate") != null) {
                        endDate = requestMap.get("endDate").toString();
                    }
                    if (requestMap.get("shopId") != null) {
                        shopId = requestMap.get("shopId").toString();
                    }
                }
            } else if (params instanceof DayShopGoodsQueryRequest) {
                DayShopGoodsQueryRequest req = (DayShopGoodsQueryRequest) params;
                if (req.getStartDate() != null) {
                    startDate = req.getStartDate();
                }
                if (req.getEndDate() != null) {
                    endDate = req.getEndDate();
                }
                if (req.getShopId() != null) {
                    shopId = req.getShopId();
                }
            }

            String eid = getEidFromParams(params);
            
            // 构建动态 SQL
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select d.PLUNO, gl.PLU_NAME, ");
            sqlBuilder.append("SUM(case when a.type='1' or a.type='2' or a.type='4' then -(d.AMT) else (d.AMT) end) as AMOUNT, ");
            sqlBuilder.append("SUM(case when a.type='1' or a.type='2' or a.type='4' then -(d.BASEQTY) else (d.BASEQTY) end) as QTY ");
            sqlBuilder.append("FROM DCP_SALE a ");
            sqlBuilder.append("JOIN DCP_SALE_DETAIL d on d.EID = a.EID and d.SHOPID = a.SHOPID and d.SALENO = a.SALENO ");
            sqlBuilder.append("left join DCP_GOODS_LANG gl on gl.EID = d.EID and gl.PLUNO = d.PLUNO and gl.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? ");
            
            List<Object> paramsList = new java.util.ArrayList<>();
            paramsList.add(eid);
            paramsList.add(startDate);
            paramsList.add(endDate);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                paramsList.add(shopId);
            }
            
            sqlBuilder.append("group by d.PLUNO, gl.PLU_NAME ");
            sqlBuilder.append("order by d.PLUNO");
            
            String sql = sqlBuilder.toString();
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, paramsList.toArray());

            // 计算汇总数据
            double totalAmount = 0;
            int totalQty = 0;
            
            for (Map<String, Object> row : resultList) {
                Object amountObj = row.get("AMOUNT");
                if (amountObj != null) {
                    totalAmount += Double.parseDouble(amountObj.toString());
                }
                Object qtyObj = row.get("QTY");
                if (qtyObj != null) {
                    totalQty += Integer.parseInt(qtyObj.toString());
                }
            }

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("total", resultList.size());
            resultData.put("startDate", startDate);
            resultData.put("endDate", endDate);
            resultData.put("shopId", shopId);
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("totalQty", totalQty);
            summary.put("goodsCount", resultList.size());
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = ServiceResponse.success(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 种商品");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 每日渠道查询服务（按渠道维度）
     * SQL: select a.CHANNELID, a.BDATE as SALEDATE,
     *      SUM(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as AMOUNT,
     *      COUNT(*) as ORDERCOUNT
     *      FROM DCP_SALE a
     *      where a.BDATE >= '开始日期' AND a.BDATE <= '截止日期'
     *      group by a.BDATE, a.CHANNELID order by a.BDATE, a.CHANNELID
     */
    private ServiceResponse<?> dayChannelQuery(Object params) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 参数在 request 嵌套对象中
                Object requestObj = paramMap.get("request");
                if (requestObj instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) requestObj;
                    if (requestMap.get("startDate") != null) {
                        startDate = requestMap.get("startDate").toString();
                    }
                    if (requestMap.get("endDate") != null) {
                        endDate = requestMap.get("endDate").toString();
                    }
                }
            }

            String eid = getEidFromParams(params);
            
            String sql = "select a.CHANNELID, a.BDATE as SALEDATE, " +
                    "SUM(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as AMOUNT, " +
                    "COUNT(*) as ORDERCOUNT " +
                    "FROM DCP_SALE a " +
                    "where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? " +
                    "group by a.BDATE, a.CHANNELID " +
                    "order by a.BDATE, a.CHANNELID";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, eid, startDate, endDate);

            // 计算汇总数据
            double totalAmount = 0;
            int totalOrderCount = 0;
            java.util.Set<String> channelSet = new java.util.HashSet<>();
            
            for (Map<String, Object> row : resultList) {
                Object amountObj = row.get("AMOUNT");
                if (amountObj != null) {
                    totalAmount += Double.parseDouble(amountObj.toString());
                }
                Object orderCountObj = row.get("ORDERCOUNT");
                if (orderCountObj != null) {
                    totalOrderCount += Integer.parseInt(orderCountObj.toString());
                }
                Object channelIdObj = row.get("CHANNELID");
                if (channelIdObj != null) {
                    channelSet.add(channelIdObj.toString());
                    // 添加 CHANNEL_NAME 字段（直接用 ID）
                    row.put("CHANNEL_NAME", channelIdObj.toString());
                }
            }
            
            double avgOrderValue = totalOrderCount > 0 ? totalAmount / totalOrderCount : 0;

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("total", resultList.size());
            resultData.put("startDate", startDate);
            resultData.put("endDate", endDate);
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("totalOrderCount", totalOrderCount);
            summary.put("channelCount", channelSet.size());
            summary.put("avgOrderValue", avgOrderValue);
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = ServiceResponse.success(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 每日门店渠道查询服务（按渠道维度，支持门店过滤）
     * SQL: select a.CHANNELID, a.BDATE as SALEDATE,
     *      SUM(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as AMOUNT,
     *      COUNT(*) as ORDERCOUNT
     *      FROM DCP_SALE a
     *      where a.BDATE >= '开始日期' AND a.BDATE <= '截止日期' AND a.SHOPID = '门店 ID'
     *      group by a.BDATE, a.CHANNELID order by a.BDATE, a.CHANNELID
     */
    private ServiceResponse<?> dayShopChannelQuery(Object params) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 参数在 request 嵌套对象中
                Object requestObj = paramMap.get("request");
                if (requestObj instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) requestObj;
                    if (requestMap.get("startDate") != null) {
                        startDate = requestMap.get("startDate").toString();
                    }
                    if (requestMap.get("endDate") != null) {
                        endDate = requestMap.get("endDate").toString();
                    }
                    if (requestMap.get("shopId") != null) {
                        shopId = requestMap.get("shopId").toString();
                    }
                }
            }

            // 构建动态 SQL
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.CHANNELID, a.BDATE as SALEDATE, ");
            sqlBuilder.append("SUM(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as AMOUNT, ");
            sqlBuilder.append("COUNT(*) as ORDERCOUNT ");
            sqlBuilder.append("FROM DCP_SALE a ");
            sqlBuilder.append("where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? ");
            
            String eid = getEidFromParams(params);
            List<Object> paramsList = new java.util.ArrayList<>();
            paramsList.add(eid);
            paramsList.add(startDate);
            paramsList.add(endDate);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                paramsList.add(shopId);
            }
            
            sqlBuilder.append("group by a.BDATE, a.CHANNELID ");
            sqlBuilder.append("order by a.BDATE, a.CHANNELID");

            String sql = sqlBuilder.toString();
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, paramsList.toArray());

            // 计算汇总数据
            double totalAmount = 0;
            int totalOrderCount = 0;
            java.util.Set<String> channelSet = new java.util.HashSet<>();
            
            for (Map<String, Object> row : resultList) {
                Object amountObj = row.get("AMOUNT");
                if (amountObj != null) {
                    totalAmount += Double.parseDouble(amountObj.toString());
                }
                Object orderCountObj = row.get("ORDERCOUNT");
                if (orderCountObj != null) {
                    totalOrderCount += Integer.parseInt(orderCountObj.toString());
                }
                Object channelIdObj = row.get("CHANNELID");
                if (channelIdObj != null) {
                    channelSet.add(channelIdObj.toString());
                    // 添加 CHANNEL_NAME 字段（直接用 ID）
                    row.put("CHANNEL_NAME", channelIdObj.toString());
                }
            }
            
            double avgOrderValue = totalOrderCount > 0 ? totalAmount / totalOrderCount : 0;

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("total", resultList.size());
            resultData.put("startDate", startDate);
            resultData.put("endDate", endDate);
            resultData.put("shopId", shopId);
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("totalOrderCount", totalOrderCount);
            summary.put("channelCount", channelSet.size());
            summary.put("avgOrderValue", avgOrderValue);
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = ServiceResponse.success(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 门店库存汇总查询服务（按门店分组，不分组到商品）
     * SQL: select a.ORGANIZATIONNO as SHOPID, gl.ORG_NAME,
     *      SUM(a.QTY) as STOCK_QTY, SUM(a.QTY * goods.PRICE) as STOCK_AMT
     *      FROM DCP_STOCK a
     *      left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.ORGANIZATIONNO
     *      left join DCP_GOODS goods on goods.EID = a.EID and goods.PLUNO = a.PLUNO
     *      where gl.LANG_TYPE = 'zh_CN'
     *      group by a.ORGANIZATIONNO, gl.ORG_NAME
     *      order by a.ORGANIZATIONNO
     */
    private ServiceResponse<?> stockSumQuery(Object params) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 参数在 request 嵌套对象中
                Object requestObj = paramMap.get("request");
                if (requestObj instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) requestObj;
                    if (requestMap.get("shopId") != null) {
                        shopId = requestMap.get("shopId").toString();
                    }
                }
            }

            // 构建动态 SQL
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.ORGANIZATIONNO as SHOPID, gl.ORG_NAME, ");
            sqlBuilder.append("SUM(a.QTY) as STOCK_QTY, SUM(a.QTY * goods.PRICE) as STOCK_AMT ");
            sqlBuilder.append("FROM DCP_STOCK a ");
            sqlBuilder.append("left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.ORGANIZATIONNO ");
            sqlBuilder.append("left join DCP_GOODS goods on goods.EID = a.EID and goods.PLUNO = a.PLUNO ");
            sqlBuilder.append("where a.EID = ? AND gl.LANG_TYPE = 'zh_CN' ");
            
            String eid = getEidFromParams(params);
            List<Object> paramsList = new java.util.ArrayList<>();
            paramsList.add(eid);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.ORGANIZATIONNO = ? ");
                paramsList.add(shopId);
            }
            
            sqlBuilder.append("group by a.ORGANIZATIONNO, gl.ORG_NAME ");
            sqlBuilder.append("order by a.ORGANIZATIONNO");

            String sql = sqlBuilder.toString();
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, paramsList.toArray());

            // 计算汇总数据
            double totalQty = 0;
            double totalAmt = 0;
            
            for (Map<String, Object> row : resultList) {
                Object qtyObj = row.get("STOCK_QTY");
                if (qtyObj != null) {
                    double qty = Double.parseDouble(qtyObj.toString());
                    totalQty += qty;
                }
                Object amtObj = row.get("STOCK_AMT");
                if (amtObj != null) {
                    double amt = Double.parseDouble(amtObj.toString());
                    totalAmt += amt;
                }
            }

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("total", resultList.size());
            resultData.put("shopId", shopId);
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalQty", totalQty);
            summary.put("totalAmt", totalAmt);
            summary.put("shopCount", resultList.size());
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = ServiceResponse.success(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 家门店");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取 DCP_STOCK 表结构
     */
    private ServiceResponse<?> getStockColumns(Object params) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String sql = "select COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_SCALE, NULLABLE " +
                    "from user_tab_columns " +
                    "where table_name = 'DCP_STOCK' " +
                    "order by COLUMN_ID";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            ServiceResponse<List<Map<String, Object>>> response = ServiceResponse.success(resultList);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 个字段");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 商品库存查询服务（按门店 + 品号分组）
     * SQL: select a.ORGANIZATIONNO as SHOPID, gl.ORG_NAME, a.PLUNO, g.PLU_NAME,
     *      SUM(a.QTY) as STOCK_QTY, SUM(a.QTY * goods.PRICE) as STOCK_AMT
     *      FROM DCP_STOCK a
     *      left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.ORGANIZATIONNO
     *      left join DCP_GOODS_LANG g on g.EID = a.EID and g.PLUNO = a.PLUNO
     *      left join DCP_GOODS goods on goods.EID = a.EID and goods.PLUNO = a.PLUNO
     *      where gl.LANG_TYPE = 'zh_CN' and g.LANG_TYPE = 'zh_CN'
     *      group by a.ORGANIZATIONNO, gl.ORG_NAME, a.PLUNO, g.PLU_NAME
     *      order by a.ORGANIZATIONNO, a.PLUNO
     */
    private ServiceResponse<?> stockQuery(Object params) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 参数在 request 嵌套对象中
                Object requestObj = paramMap.get("request");
                if (requestObj instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) requestObj;
                    if (requestMap.get("shopId") != null) {
                        shopId = requestMap.get("shopId").toString();
                    }
                }
            }

            // 构建动态 SQL
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.ORGANIZATIONNO as SHOPID, gl.ORG_NAME, a.PLUNO, g.PLU_NAME, ");
            sqlBuilder.append("SUM(a.QTY) as STOCK_QTY, SUM(a.QTY * goods.PRICE) as STOCK_AMT ");
            sqlBuilder.append("FROM DCP_STOCK a ");
            sqlBuilder.append("left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.ORGANIZATIONNO ");
            sqlBuilder.append("left join DCP_GOODS_LANG g on g.EID = a.EID and g.PLUNO = a.PLUNO ");
            sqlBuilder.append("left join DCP_GOODS goods on goods.EID = a.EID and goods.PLUNO = a.PLUNO ");
            sqlBuilder.append("where a.EID = ? AND gl.LANG_TYPE = 'zh_CN' and g.LANG_TYPE = 'zh_CN' ");
            
            String eid = getEidFromParams(params);
            List<Object> paramsList = new java.util.ArrayList<>();
            paramsList.add(eid);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.ORGANIZATIONNO = ? ");
                paramsList.add(shopId);
            }
            
            sqlBuilder.append("group by a.ORGANIZATIONNO, gl.ORG_NAME, a.PLUNO, g.PLU_NAME ");
            sqlBuilder.append("order by a.ORGANIZATIONNO, a.PLUNO");

            String sql = sqlBuilder.toString();
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, paramsList.toArray());

            // 计算汇总数据
            double totalQty = 0;
            double totalAmt = 0;
            java.util.Set<String> shopSet = new java.util.HashSet<>();
            java.util.Set<String> goodsSet = new java.util.HashSet<>();
            
            for (Map<String, Object> row : resultList) {
                Object qtyObj = row.get("STOCK_QTY");
                if (qtyObj != null) {
                    double qty = Double.parseDouble(qtyObj.toString());
                    totalQty += qty;
                }
                Object amtObj = row.get("STOCK_AMT");
                if (amtObj != null) {
                    double amt = Double.parseDouble(amtObj.toString());
                    totalAmt += amt;
                }
                Object shopIdObj = row.get("SHOPID");
                if (shopIdObj != null) {
                    shopSet.add(shopIdObj.toString());
                }
                Object plunoObj = row.get("PLUNO");
                if (plunoObj != null) {
                    goodsSet.add(plunoObj.toString());
                }
            }

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("total", resultList.size());
            resultData.put("shopId", shopId);
            
            // 汇总数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalQty", totalQty);
            summary.put("totalAmt", totalAmt);
            summary.put("shopCount", shopSet.size());
            summary.put("goodsCount", goodsSet.size());
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = ServiceResponse.success(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }
}
