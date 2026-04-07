package com.report.service.impl;

import com.report.dto.*;
import com.report.service.LoginService;
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
    /**
     * 默认 EID (生产环境从 token 解析)
     */
    private static final String DEFAULT_EID = "99";

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @Autowired(required = false)
    private LoginService loginService;

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
        Integer pageNumber = request.getPageNumber();
        Integer pageSize = request.getPageSize();

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
                return queryMember(params, pageNumber, pageSize);
            case "QueryReport":
                return queryReport(params, pageNumber, pageSize);
            case "DaySaleQuery":
                return daySaleQuery(params, pageNumber, pageSize);
            case "DayShopGoodsQuery":
                return dayShopGoodsQuery(params, pageNumber, pageSize);
            case "DayChannelQuery":
                return dayChannelQuery(params, pageNumber, pageSize);
            case "DayShopChannelQuery":
                return dayShopChannelQuery(params, pageNumber, pageSize);
            case "StockQuery":
                return stockQuery(params, pageNumber, pageSize);
            case "StockSumQuery":
                return stockSumQuery(params, pageNumber, pageSize);
            case "GetStockColumns":
                return getStockColumns(params, pageNumber, pageSize);
            case "AllEidQuery":
                return allEidQuery(params, pageNumber, pageSize);
            case "UserLogin":
                return userLogin(params);
            case "DcpSaleQty":
                return dcpSaleQty(params, pageNumber, pageSize);
            default:
                return ServiceResponse.error("999", "未知服务 ID: " + serviceId);
        }
    }

    @Override
    public ServiceResponse<?> executeByServiceId(String serviceId, Object params, String key) {
        System.out.println("[DEBUG executeByServiceId] serviceId=" + serviceId);
        System.out.println("[DEBUG executeByServiceId] params 类型=" + (params != null ? params.getClass().getName() : "null"));
        
        ServiceRequest request = new ServiceRequest();
        request.setServiceId(serviceId);
        
        // 如果 params 已经是完整的请求结构（包含 request、sign、pageNumber、pageSize）
        if (params instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) params;
            System.out.println("[DEBUG executeByServiceId] paramMap keys=" + paramMap.keySet());
            
            // 提取 request 对象
            Object requestObj = paramMap.get("request");
            System.out.println("[DEBUG executeByServiceId] requestObj=" + (requestObj != null ? requestObj.getClass().getName() : "null"));
            
            if (requestObj != null) {
                request.setRequest(requestObj);
                if (requestObj instanceof Map) {
                    Map<?, ?> requestMap = (Map<?, ?>) requestObj;
                    System.out.println("[DEBUG executeByServiceId] 日期参数 startDate=" + requestMap.get("startDate") + ", endDate=" + requestMap.get("endDate"));
                }
            } else {
                request.setRequest(params);
            }
            
            // 提取 sign 对象
            Object signObj = paramMap.get("sign");
            if (signObj instanceof Map) {
                Map<?, ?> signMap = (Map<?, ?>) signObj;
                String signKey = signMap.get("key") != null ? signMap.get("key").toString() : key;
                String signValue = signMap.get("sign") != null ? signMap.get("sign").toString() : "";
                String token = signMap.get("token") != null ? signMap.get("token").toString() : "";
                request.setSign(new SignInfo(signKey, signValue, token));
            } else if (key != null) {
                String sign = SignUtil.generateSign(params.toString(), key);
                request.setSign(new SignInfo(key, sign));
            }
            
            // 提取分页参数
            Object pageNumObj = paramMap.get("pageNumber");
            Object pageSizeObj = paramMap.get("pageSize");
            System.out.println("[DEBUG executeByServiceId] pageNumber=" + pageNumObj + ", pageSize=" + pageSizeObj);
            
            if (pageNumObj != null) {
                request.setPageNumber(Integer.valueOf(pageNumObj.toString()));
            }
            if (pageSizeObj != null) {
                request.setPageSize(Integer.valueOf(pageSizeObj.toString()));
            }
        } else {
            request.setRequest(params);
            if (key != null) {
                String sign = SignUtil.generateSign(params.toString(), key);
                request.setSign(new SignInfo(key, sign));
            }
        }

        System.out.println("[DEBUG executeByServiceId] 最终 request 对象：" + request.getRequest());
        return execute(request);
    }

    /**
     * 查询会员示例服务
     */
    private ServiceResponse<?> queryMember(Object params, Integer pageNumber, Integer pageSize) {
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
        String respSign = SignUtil.generateSign(memberData.toString(), null);
        response.setSign(new SignInfo(null, respSign));

        return response;
    }

    /**
     * 查询报表示例服务
     */
    private ServiceResponse<?> queryReport(Object params, Integer pageNumber, Integer pageSize) {
        Map<String, Object> reportData = new HashMap<>();
        
        // 模拟报表数据
        reportData.put("reportId", "RPT20260403001");
        reportData.put("reportName", "销售日报");
        reportData.put("reportDate", "2026-04-03");
        reportData.put("totalAmount", 12580.50);
        reportData.put("orderCount", 156);

        ServiceResponse<Map<String, Object>> response = ServiceResponse.success(reportData);
        
        String respSign = SignUtil.generateSign(reportData.toString(), null);
        response.setSign(new SignInfo(null, respSign));

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
    private ServiceResponse<?> daySaleQuery(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 直接从 params 读取日期参数（params 本身就是 request 对象）
                if (paramMap.get("startDate") != null) {
                    startDate = paramMap.get("startDate").toString();
                }
                if (paramMap.get("endDate") != null) {
                    endDate = paramMap.get("endDate").toString();
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
                    "left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.SHOPID and gl.LANG_TYPE = 'zh_CN' " +
                    "where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? " +
                    "group by a.BDATE, a.SHOPID, gl.ORG_NAME " +
                    "order by a.BDATE, a.SHOPID, gl.ORG_NAME";
            
            String countSql = "SELECT COUNT(*) FROM (SELECT a.SHOPID FROM DCP_SALE a " +
                    "where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? " +
                    "group by a.BDATE, a.SHOPID)";

            Map<String, Object> pageData = buildPaginatedResult(sql, pageNumber, pageSize, countSql, eid, startDate, endDate);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

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

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");
            
            // 设置分页信息
            response.setTotalRecords((Integer) pageData.get("totalRecords"));
            response.setTotalPages((Integer) pageData.get("totalPages"));
            response.setPageNumber((Integer) pageData.get("pageNumber"));
            response.setPageSize((Integer) pageData.get("pageSize"));

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
    private ServiceResponse<?> dayShopGoodsQuery(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 直接从 params 读取参数（params 本身就是 request 对象）
                if (paramMap.get("startDate") != null) {
                    startDate = paramMap.get("startDate").toString();
                }
                if (paramMap.get("endDate") != null) {
                    endDate = paramMap.get("endDate").toString();
                }
                if (paramMap.get("shopId") != null) {
                    shopId = paramMap.get("shopId").toString();
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
            
            List<Object> sqlParamsList = new java.util.ArrayList<>();
            sqlParamsList.add(eid);
            sqlParamsList.add(startDate);
            sqlParamsList.add(endDate);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                sqlParamsList.add(shopId);
            }
            
            sqlBuilder.append("group by d.PLUNO, gl.PLU_NAME ");
            sqlBuilder.append("order by d.PLUNO");
            
            String sql = sqlBuilder.toString();
            String countSql = "SELECT COUNT(*) FROM (" + sqlBuilder.toString().replace("order by d.PLUNO", "") + ")";
            
            Object[] sqlParams = sqlParamsList.toArray();
            Map<String, Object> pageData = buildPaginatedResult(sql, pageNumber, pageSize, countSql, sqlParams);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

            // 计算汇总数据
            double totalAmount = 0;
            double totalQty = 0;
            
            for (Map<String, Object> row : resultList) {
                Object amountObj = row.get("AMOUNT");
                if (amountObj != null) {
                    totalAmount += Double.parseDouble(amountObj.toString());
                }
                Object qtyObj = row.get("QTY");
                if (qtyObj != null) {
                    totalQty += Double.parseDouble(qtyObj.toString());
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

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 种商品");
            
            // 设置分页信息
            response.setTotalRecords((Integer) pageData.get("totalRecords"));
            response.setTotalPages((Integer) pageData.get("totalPages"));
            response.setPageNumber((Integer) pageData.get("pageNumber"));
            response.setPageSize((Integer) pageData.get("pageSize"));

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
    private ServiceResponse<?> dayChannelQuery(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 直接从 params 读取参数（params 本身就是 request 对象）
                if (paramMap.get("startDate") != null) {
                    startDate = paramMap.get("startDate").toString();
                }
                if (paramMap.get("endDate") != null) {
                    endDate = paramMap.get("endDate").toString();
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
            
            String countSql = "SELECT COUNT(*) FROM (SELECT a.CHANNELID FROM DCP_SALE a " +
                    "where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? " +
                    "group by a.BDATE, a.CHANNELID)";

            Map<String, Object> pageData = buildPaginatedResult(sql, pageNumber, pageSize, countSql, eid, startDate, endDate);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

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

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");
            
            // 设置分页信息
            response.setTotalRecords((Integer) pageData.get("totalRecords"));
            response.setTotalPages((Integer) pageData.get("totalPages"));
            response.setPageNumber((Integer) pageData.get("pageNumber"));
            response.setPageSize((Integer) pageData.get("pageSize"));

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
    private ServiceResponse<?> dayShopChannelQuery(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 直接从 params 读取参数（params 本身就是 request 对象）
                if (paramMap.get("startDate") != null) {
                    startDate = paramMap.get("startDate").toString();
                }
                if (paramMap.get("endDate") != null) {
                    endDate = paramMap.get("endDate").toString();
                }
                if (paramMap.get("shopId") != null) {
                    shopId = paramMap.get("shopId").toString();
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
            List<Object> sqlParamsList = new java.util.ArrayList<>();
            sqlParamsList.add(eid);
            sqlParamsList.add(startDate);
            sqlParamsList.add(endDate);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                sqlParamsList.add(shopId);
            }
            
            sqlBuilder.append("group by a.BDATE, a.CHANNELID ");
            sqlBuilder.append("order by a.BDATE, a.CHANNELID");

            String sql = sqlBuilder.toString();
            String countSql = "SELECT COUNT(*) FROM (" + sqlBuilder.toString().replace("order by a.BDATE, a.CHANNELID", "") + ")";
            
            Object[] sqlParams = sqlParamsList.toArray();
            Map<String, Object> pageData = buildPaginatedResult(sql, pageNumber, pageSize, countSql, sqlParams);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

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

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");
            
            // 设置分页信息
            response.setTotalRecords((Integer) pageData.get("totalRecords"));
            response.setTotalPages((Integer) pageData.get("totalPages"));
            response.setPageNumber((Integer) pageData.get("pageNumber"));
            response.setPageSize((Integer) pageData.get("pageSize"));

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
    private ServiceResponse<?> stockSumQuery(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 直接从 params 读取参数（params 本身就是 request 对象）
                if (paramMap.get("shopId") != null) {
                    shopId = paramMap.get("shopId").toString();
                }
            }

            // 构建动态 SQL - 直接按门店分组
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT a.ORGANIZATIONNO AS SHOPID, gl.ORG_NAME, ");
            sqlBuilder.append("SUM(a.QTY) AS STOCK_QTY, SUM(a.QTY * goods.PRICE) AS STOCK_AMT ");
            sqlBuilder.append("FROM DCP_STOCK a ");
            sqlBuilder.append("LEFT JOIN DCP_ORG_LANG gl ON gl.EID = a.EID AND gl.ORGANIZATIONNO = a.ORGANIZATIONNO AND gl.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("LEFT JOIN DCP_GOODS goods ON goods.EID = a.EID AND goods.PLUNO = a.PLUNO ");
            sqlBuilder.append("WHERE a.EID = ? ");
            
            String eid = getEidFromParams(params);
            List<Object> sqlParamsList = new java.util.ArrayList<>();
            sqlParamsList.add(eid);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.ORGANIZATIONNO = ? ");
                sqlParamsList.add(shopId);
            }
            
            sqlBuilder.append("GROUP BY a.ORGANIZATIONNO, gl.ORG_NAME ");
            sqlBuilder.append("ORDER BY a.ORGANIZATIONNO");

            String sql = sqlBuilder.toString();
            String countSql = "SELECT COUNT(*) FROM (" + sqlBuilder.toString().replace("ORDER BY a.ORGANIZATIONNO", "") + ")";
            
            Object[] sqlParams = sqlParamsList.toArray();
            Map<String, Object> pageData = buildPaginatedResult(sql, pageNumber, pageSize, countSql, sqlParams);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

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

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 家门店");
            
            // 设置分页信息
            response.setTotalRecords((Integer) pageData.get("totalRecords"));
            response.setTotalPages((Integer) pageData.get("totalPages"));
            response.setPageNumber((Integer) pageData.get("pageNumber"));
            response.setPageSize((Integer) pageData.get("pageSize"));

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取 DCP_STOCK 表结构
     */
    private ServiceResponse<?> getStockColumns(Object params, Integer pageNumber, Integer pageSize) {
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
    private ServiceResponse<?> stockQuery(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String shopId = "";

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 直接从 params 读取参数（params 本身就是 request 对象）
                if (paramMap.get("shopId") != null) {
                    shopId = paramMap.get("shopId").toString();
                }
            }

            // 构建动态 SQL
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.ORGANIZATIONNO as SHOPID, gl.ORG_NAME, a.PLUNO, g.PLU_NAME, ");
            sqlBuilder.append("SUM(a.QTY) as STOCK_QTY, SUM(a.QTY * goods.PRICE) as STOCK_AMT ");
            sqlBuilder.append("FROM DCP_STOCK a ");
            sqlBuilder.append("left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.ORGANIZATIONNO and gl.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("left join DCP_GOODS_LANG g on g.EID = a.EID and g.PLUNO = a.PLUNO and g.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("left join DCP_GOODS goods on goods.EID = a.EID and goods.PLUNO = a.PLUNO ");
            sqlBuilder.append("where a.EID = ? ");
            
            String eid = getEidFromParams(params);
            List<Object> sqlParamsList = new java.util.ArrayList<>();
            sqlParamsList.add(eid);
            
            // 如果指定了门店 ID，添加条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.ORGANIZATIONNO = ? ");
                sqlParamsList.add(shopId);
            }
            
            sqlBuilder.append("group by a.ORGANIZATIONNO, gl.ORG_NAME, a.PLUNO, g.PLU_NAME ");
            sqlBuilder.append("order by a.ORGANIZATIONNO, a.PLUNO");

            String sql = sqlBuilder.toString();
            String countSql = "SELECT COUNT(*) FROM (" + sqlBuilder.toString().replace("order by a.ORGANIZATIONNO, a.PLUNO", "") + ")";
            
            Object[] sqlParams = sqlParamsList.toArray();
            Map<String, Object> pageData = buildPaginatedResult(sql, pageNumber, pageSize, countSql, sqlParams);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

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

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");
            
            // 设置分页信息
            response.setTotalRecords((Integer) pageData.get("totalRecords"));
            response.setTotalPages((Integer) pageData.get("totalPages"));
            response.setPageNumber((Integer) pageData.get("pageNumber"));
            response.setPageSize((Integer) pageData.get("pageSize"));

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有企业编号（特殊服务：不需要 token 校验）
     * SQL: select distinct EID from PLATFORM_STAFFS order by EID
     */
    private ServiceResponse<?> allEidQuery(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String sql = "select distinct EID from PLATFORM_STAFFS order by EID";
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            // 提取 EID 列表
            List<String> eidList = new java.util.ArrayList<>();
            for (Map<String, Object> row : resultList) {
                Object eidObj = row.get("EID");
                if (eidObj != null) {
                    eidList.add(eidObj.toString());
                }
            }

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", eidList);
            resultData.put("total", eidList.size());

            ServiceResponse<Map<String, Object>> response = ServiceResponse.success(resultData);
            response.setServiceDescription("查询成功，共 " + eidList.size() + " 个企业");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录服务（通过 /api/service/UserLogin 调用）
     */
    private ServiceResponse<?> userLogin(Object params) {
        // 调用 LoginService 实现登录
        String opno = "";
        String password = "";
        String clientIp = "unknown";
        
        if (params instanceof Map) {
            Map<?, ?> requestMap = (Map<?, ?>) params;
            opno = requestMap.get("username") != null ? requestMap.get("username").toString() : "";
            password = requestMap.get("password") != null ? requestMap.get("password").toString() : "";
            // 获取 Controller 传递的客户端 IP
            Object ipObj = requestMap.get("clientIp");
            if (ipObj != null && !"unknown".equals(ipObj.toString())) {
                clientIp = ipObj.toString();
            }
        }
        
        return loginService.login(opno, password, clientIp);
    }

    /**
     * 商品销售明细查询服务
     * SQL: select a.SHOPID,a.SALENO,a.TOT_OLDAMT,a.TOT_DISC,a.TOT_AMT,a.BDATE,a.WORKNO,a.OPNO,a.MACHINE,a.SDATE,a.STIME 
     *      FROM DCP_SALE a 
     *      where a.EID = ? AND a.SHOPID = ? and a.BDATE>=? and a.BDATE <= ?
     */
    private ServiceResponse<?> dcpSaleQty(Object params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String shopId = "";
            String startDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDate = startDate;

            if (params instanceof Map) {
                Map<?, ?> paramMap = (Map<?, ?>) params;
                
                // 直接从 params 读取参数
                if (paramMap.get("shopId") != null) {
                    shopId = paramMap.get("shopId").toString().trim();
                }
                if (paramMap.get("startDate") != null) {
                    startDate = paramMap.get("startDate").toString();
                }
                if (paramMap.get("endDate") != null) {
                    endDate = paramMap.get("endDate").toString();
                }
            }

            String eid = getEidFromParams(params);
            
            // 构建动态 SQL - 门店号为空时不限制门店，考虑退货的正负号
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.SHOPID,a.SALENO, ");
            sqlBuilder.append("(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_OLDAMT) else (a.TOT_OLDAMT) end) as TOT_OLDAMT, ");
            sqlBuilder.append("(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_DISC) else (a.TOT_DISC) end) as TOT_DISC, ");
            sqlBuilder.append("(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as TOT_AMT, ");
            sqlBuilder.append("a.BDATE,a.WORKNO,a.OPNO,a.MACHINE,a.SDATE,a.STIME ");
            sqlBuilder.append("FROM DCP_SALE a ");
            sqlBuilder.append("where a.EID = ? ");
            
            List<Object> sqlParams = new java.util.ArrayList<>();
            sqlParams.add(eid);
            
            // 如果指定了门店号，添加门店条件
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                sqlParams.add(shopId);
            }
            
            sqlBuilder.append("and a.BDATE>=? and a.BDATE <= ? ");
            sqlParams.add(startDate);
            sqlParams.add(endDate);
            
            // 排序：按系统日期、系统时间、门店、销售单号排序
            sqlBuilder.append("order by a.SDATE,a.STIME,a.SHOPID,a.SALENO");
            
            String sql = sqlBuilder.toString();
            
            // 构建 COUNT SQL（不需要 case when，只统计记录数）
            StringBuilder countSqlBuilder = new StringBuilder();
            countSqlBuilder.append("SELECT COUNT(*) FROM DCP_SALE a ");
            countSqlBuilder.append("where a.EID = ? ");
            
            List<Object> countSqlParams = new java.util.ArrayList<>();
            countSqlParams.add(eid);
            
            if (shopId != null && !shopId.trim().isEmpty()) {
                countSqlBuilder.append("AND a.SHOPID = ? ");
                countSqlParams.add(shopId);
            }
            
            countSqlBuilder.append("and a.BDATE>=? and a.BDATE <= ?");
            countSqlParams.add(startDate);
            countSqlParams.add(endDate);
            
            String countSql = countSqlBuilder.toString();

            Map<String, Object> pageData = buildPaginatedResult(sql, pageNumber, pageSize, countSql, countSqlParams.toArray());
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("total", resultList.size());
            resultData.put("shopId", shopId);
            resultData.put("startDate", startDate);
            resultData.put("endDate", endDate);

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");
            
            // 设置分页信息
            response.setTotalRecords((Integer) pageData.get("totalRecords"));
            response.setTotalPages((Integer) pageData.get("totalPages"));
            response.setPageNumber((Integer) pageData.get("pageNumber"));
            response.setPageSize((Integer) pageData.get("pageSize"));

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 构建分页 SQL 和处理分页结果
     * @param sql 原始 SQL（必须包含 order by）
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @param countSql 用于查询总数的 SQL
     * @param sqlParams SQL 参数
     * @return 包含分页数据的 Map
     */
    private Map<String, Object> buildPaginatedResult(String sql, Integer pageNumber, Integer pageSize, 
                                                      String countSql, Object... sqlParams) {
        Map<String, Object> result = new HashMap<>();
        
        // 判断是否需要分页
        boolean needPagination = pageNumber != null && pageSize != null && pageNumber > 0 && pageSize > 0;
        
        System.out.println("[DEBUG 分页] needPagination=" + needPagination + 
                          ", pageNumber=" + pageNumber + 
                          ", pageSize=" + pageSize);
        
        if (needPagination) {
            // 查询总数
            int totalRecords = jdbcTemplate.queryForObject(countSql, Integer.class, sqlParams);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            System.out.println("[DEBUG 分页] totalRecords=" + totalRecords + 
                              ", totalPages=" + totalPages);
            
            // 确保页码不越界
            if (pageNumber > totalPages) {
                pageNumber = totalPages > 0 ? totalPages : 1;
            }
            
            // 计算行号范围（Oracle 行号从 1 开始）
            int startRow = (pageNumber - 1) * pageSize + 1;
            int endRow = pageNumber * pageSize;
            
            System.out.println("[DEBUG 分页] startRow=" + startRow + ", endRow=" + endRow);
            
            // 构建分页 SQL（Oracle 语法 - 三层嵌套）
            String paginatedSql = "select * from ( SELECT rownum as NUM, ALLTABLE.* FROM ( " + sql + " ) ALLTABLE ) where NUM >= " + startRow + " AND NUM <= " + endRow;
            
            System.out.println("[DEBUG 分页 SQL] " + paginatedSql);
            
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(paginatedSql, sqlParams);
            
            System.out.println("[DEBUG 分页结果] 返回 " + resultList.size() + " 条记录");
            
            result.put("list", resultList);
            result.put("totalRecords", totalRecords);
            result.put("totalPages", totalPages);
            result.put("pageNumber", pageNumber);
            result.put("pageSize", pageSize);
        } else {
            // 不分页，查询全部
            System.out.println("[DEBUG 分页] 不分页模式，查询全部");
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, sqlParams);
            
            result.put("list", resultList);
            result.put("totalRecords", resultList.size());
            result.put("totalPages", resultList.size() > 0 ? 1 : 0);
            result.put("pageNumber", 1);
            result.put("pageSize", 0);
        }
        
        return result;
    }
}
