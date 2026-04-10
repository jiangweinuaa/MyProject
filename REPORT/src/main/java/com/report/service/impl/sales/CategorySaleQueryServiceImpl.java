package com.report.service.impl.sales;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 品类销售分析查询服务实现
 * 从 DCP_SALE_DETAIL 表取数据
 */
@Service("categorySaleQueryService")
public class CategorySaleQueryServiceImpl extends BaseService implements ReportService {

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
            String shopId = getStringParam(params, "shopId");

            // 从 token 解析 EID（公共方法）
            String eid = resolveEid(jdbcTemplate, params);
            
            // 构建 SQL - 按品类汇总（通过 DCP_GOODS 表关联品类）
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ");
            sqlBuilder.append("  g.CATEGORY, ");
            sqlBuilder.append("  cl.CATEGORY_NAME, ");
            sqlBuilder.append("  SUM(CASE WHEN a.type='1' OR a.type='2' OR a.type='4' THEN -(d.AMT) ELSE d.AMT END) AS SALE_AMT, ");
            sqlBuilder.append("  SUM(CASE WHEN a.type='1' OR a.type='2' OR a.type='4' THEN -(d.BASEQTY) ELSE d.BASEQTY END) AS SALE_QTY, ");
            sqlBuilder.append("  COUNT(DISTINCT a.SALENO) AS ORDER_COUNT ");
            sqlBuilder.append("FROM DCP_SALE a ");
            sqlBuilder.append("JOIN DCP_SALE_DETAIL d ON d.EID = a.EID AND d.SHOPID = a.SHOPID AND d.SALENO = a.SALENO ");
            sqlBuilder.append("JOIN DCP_GOODS g ON g.EID = d.EID AND g.PLUNO = d.PLUNO ");
            sqlBuilder.append("LEFT JOIN DCP_CATEGORY_LANG cl ON cl.EID = g.EID AND cl.CATEGORY = g.CATEGORY AND cl.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("WHERE a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? ");
            
            List<Object> sqlParams = new ArrayList<>();
            sqlParams.add(eid);
            sqlParams.add(startDate);
            sqlParams.add(endDate);
            
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                sqlParams.add(shopId);
            }
            
            sqlBuilder.append("GROUP BY g.CATEGORY, cl.CATEGORY_NAME ");
            sqlBuilder.append("ORDER BY SALE_AMT DESC");
            
            String sql = sqlBuilder.toString();
            
            // COUNT SQL
            StringBuilder countSqlBuilder = new StringBuilder();
            countSqlBuilder.append("SELECT COUNT(DISTINCT g.CATEGORY) ");
            countSqlBuilder.append("FROM DCP_SALE a ");
            countSqlBuilder.append("JOIN DCP_SALE_DETAIL d ON d.EID = a.EID AND d.SHOPID = a.SHOPID AND d.SALENO = a.SALENO ");
            countSqlBuilder.append("JOIN DCP_GOODS g ON g.EID = d.EID AND g.PLUNO = d.PLUNO ");
            countSqlBuilder.append("WHERE a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? ");
            
            List<Object> countSqlParams = new ArrayList<>();
            countSqlParams.add(eid);
            countSqlParams.add(startDate);
            countSqlParams.add(endDate);
            
            if (shopId != null && !shopId.trim().isEmpty()) {
                countSqlBuilder.append("AND a.SHOPID = ? ");
                countSqlParams.add(shopId);
            }
            
            String countSql = countSqlBuilder.toString();
            
            // 执行分页查询（使用基类的统一分页方法）
            Map<String, Object> pageData = buildPaginatedResult(
                jdbcTemplate, sql, countSql, pageNumber, pageSize, sqlParams.toArray()
            );
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageData.get("list");

            // 计算汇总数据
            double totalAmount = 0;
            double totalQty = 0;
            int totalOrderCount = 0;
            
            for (Map<String, Object> row : resultList) {
                Object amountObj = row.get("SALE_AMT");
                if (amountObj != null) {
                    totalAmount += Double.parseDouble(amountObj.toString());
                }
                Object qtyObj = row.get("SALE_QTY");
                if (qtyObj != null) {
                    totalQty += Double.parseDouble(qtyObj.toString());
                }
                Object orderCountObj = row.get("ORDER_COUNT");
                if (orderCountObj != null) {
                    totalOrderCount += Integer.parseInt(orderCountObj.toString());
                }
            }

            // 构建结果
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
            summary.put("totalOrderCount", totalOrderCount);
            summary.put("categoryCount", resultList.size());
            resultData.put("summary", summary);

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + resultList.size() + " 个品类");
            
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
}
