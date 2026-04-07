package com.report.service.impl.sales;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 每日门店渠道查询服务实现
 */
@Service("dayShopChannelQueryService")
public class DayShopChannelQueryServiceImpl implements ReportService {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = "20250101";
            String endDate = "20261231";
            String shopId = "";

            if (params != null) {
                if (params.get("startDate") != null) {
                    startDate = params.get("startDate").toString();
                }
                if (params.get("endDate") != null) {
                    endDate = params.get("endDate").toString();
                }
                if (params.get("shopId") != null) {
                    shopId = params.get("shopId").toString();
                }
            }

            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.CHANNELID, a.BDATE as SALEDATE, ");
            sqlBuilder.append("SUM(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as AMOUNT, ");
            sqlBuilder.append("COUNT(*) as ORDERCOUNT ");
            sqlBuilder.append("FROM DCP_SALE a ");
            sqlBuilder.append("where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? ");
            
            String eid = "66";
            java.util.List<Object> sqlParamsList = new java.util.ArrayList<>();
            sqlParamsList.add(eid);
            sqlParamsList.add(startDate);
            sqlParamsList.add(endDate);
            
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

            double totalAmount = 0;
            int totalOrderCount = 0;
            Set<String> channelSet = new HashSet<>();
            
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
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("totalOrderCount", totalOrderCount);
            summary.put("channelCount", channelSet.size());
            summary.put("avgOrderValue", avgOrderValue);
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 条记录");
            
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

    private Map<String, Object> buildPaginatedResult(String sql, Integer pageNumber, Integer pageSize, 
                                                      String countSql, Object... sqlParams) {
        Map<String, Object> result = new HashMap<>();
        
        boolean needPagination = pageNumber != null && pageSize != null && pageNumber > 0 && pageSize > 0;
        
        if (needPagination) {
            int totalRecords = jdbcTemplate.queryForObject(countSql, Integer.class, sqlParams);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            if (pageNumber > totalPages) {
                pageNumber = totalPages > 0 ? totalPages : 1;
            }
            
            int startRow = (pageNumber - 1) * pageSize + 1;
            int endRow = pageNumber * pageSize;
            
            String paginatedSql = "select * from ( SELECT rownum as NUM, ALLTABLE.* FROM ( " + sql + " ) ALLTABLE ) where NUM >= " + startRow + " AND NUM <= " + endRow;
            
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(paginatedSql, sqlParams);
            
            result.put("list", resultList);
            result.put("totalRecords", totalRecords);
            result.put("totalPages", totalPages);
            result.put("pageNumber", pageNumber);
            result.put("pageSize", pageSize);
        } else {
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
