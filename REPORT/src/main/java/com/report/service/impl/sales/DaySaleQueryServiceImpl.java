package com.report.service.impl.sales;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 每日销售查询服务实现
 * 
 * 数据源使用：
 * - 业务数据（DCP_SALE）：商家库（优先）或平台库（降级）
 * - EID 解析：平台库
 */
@Service("daySaleQueryService")
public class DaySaleQueryServiceImpl extends BaseService implements ReportService {

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        try {
            String startDate = getStringParam(params, "startDate", "20250101");
            String endDate = getStringParam(params, "endDate", "20261231");
            String eid = resolveEid(params);
            
            // 关键改动：业务数据从商家库读取（降级到平台库）
            JdbcTemplate businessJdbc = resolveBusinessJdbcTemplate();
            
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

            Map<String, Object> pageData = buildPaginatedResult(
                businessJdbc, sql, countSql, pageNumber, pageSize, eid, startDate, endDate
            );
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
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("totalOrderCount", totalOrderCount);
            summary.put("shopCount", shopSet.size());
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
}
