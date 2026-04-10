package com.report.service.impl.sales;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.report.service.impl.BaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品销售明细查询服务实现
 */
@Service("dcpSaleQueryService")
public class DcpSaleQueryServiceImpl extends BaseService implements ReportService {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String shopId = "";
            String startDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDate = startDate;

            if (params != null) {
                if (params.get("shopId") != null) {
                    shopId = params.get("shopId").toString().trim();
                }
                if (params.get("startDate") != null) {
                    startDate = params.get("startDate").toString();
                }
                if (params.get("endDate") != null) {
                    endDate = params.get("endDate").toString();
                }
            }

            String eid = resolveEid(jdbcTemplate, params);
            
            // 构建动态 SQL - 门店号为空时不限制门店，考虑退货的正负号
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.SHOPID,a.SALENO, ");
            sqlBuilder.append("(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_OLDAMT) else (a.TOT_OLDAMT) end) as TOT_OLDAMT, ");
            sqlBuilder.append("(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_DISC) else (a.TOT_DISC) end) as TOT_DISC, ");
            sqlBuilder.append("(case when a.type='1' or a.type='2' or a.type='4' then -(a.TOT_AMT) else (a.TOT_AMT) end) as TOT_AMT, ");
            sqlBuilder.append("a.BDATE,a.WORKNO,a.OPNO,a.MACHINE,a.SDATE,a.STIME ");
            sqlBuilder.append("FROM DCP_SALE a ");
            sqlBuilder.append("where a.EID = ? ");
            
            java.util.List<Object> sqlParams = new java.util.ArrayList<>();
            sqlParams.add(eid);
            
            if (shopId != null && !shopId.trim().isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                sqlParams.add(shopId);
            }
            
            sqlBuilder.append("and a.BDATE>=? and a.BDATE <= ? ");
            sqlParams.add(startDate);
            sqlParams.add(endDate);
            
            sqlBuilder.append("order by a.SDATE,a.STIME,a.SHOPID,a.SALENO");
            
            String sql = sqlBuilder.toString();
            
            // 构建 COUNT SQL
            StringBuilder countSqlBuilder = new StringBuilder();
            countSqlBuilder.append("SELECT COUNT(*) FROM DCP_SALE a ");
            countSqlBuilder.append("where a.EID = ? ");
            
            java.util.List<Object> countSqlParams = new java.util.ArrayList<>();
            countSqlParams.add(eid);
            
            if (shopId != null && !shopId.trim().isEmpty()) {
                countSqlBuilder.append("AND a.SHOPID = ? ");
                countSqlParams.add(shopId);
            }
            
            countSqlBuilder.append("and a.BDATE>=? and a.BDATE <= ?");
            countSqlParams.add(startDate);
            countSqlParams.add(endDate);
            
            String countSql = countSqlBuilder.toString();

            // 执行分页查询（使用基类的统一分页方法）
            Map<String, Object> pageData = buildPaginatedResult(
                jdbcTemplate, sql, countSql, pageNumber, pageSize, countSqlParams.toArray()
            );
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
