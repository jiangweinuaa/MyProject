package com.report.service.impl.sales;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每日销售查询服务实现
 */
@Service("daySaleQueryService")
public class DaySaleQueryServiceImpl extends BaseService implements ReportService {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String startDate = getStringParam(params, "startDate", "20250101");
            String endDate = getStringParam(params, "endDate", "20261231");

            // 从 token 解析 EID（公共方法）
            String eid = resolveEid(jdbcTemplate, params);
            
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
     * 构建分页 SQL 和处理分页结果
     */
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
