package com.report.service.impl.stock;

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
 * 商品库存查询服务实现
 */
@Service("stockQueryService")
public class StockQueryServiceImpl implements ReportService {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String shopId = "";

            if (params != null) {
                if (params.get("shopId") != null) {
                    shopId = params.get("shopId").toString();
                }
            }

            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select a.ORGANIZATIONNO as SHOPID, gl.ORG_NAME, a.PLUNO, g.PLU_NAME, ");
            sqlBuilder.append("SUM(a.QTY) as STOCK_QTY, SUM(a.QTY * goods.PRICE) as STOCK_AMT ");
            sqlBuilder.append("FROM DCP_STOCK a ");
            sqlBuilder.append("left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.ORGANIZATIONNO and gl.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("left join DCP_GOODS_LANG g on g.EID = a.EID and g.PLUNO = a.PLUNO and g.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("left join DCP_GOODS goods on goods.EID = a.EID and goods.PLUNO = a.PLUNO ");
            sqlBuilder.append("where a.EID = ? ");
            
            String eid = "66";
            java.util.List<Object> sqlParamsList = new java.util.ArrayList<>();
            sqlParamsList.add(eid);
            
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

            double totalQty = 0;
            double totalAmt = 0;
            Set<String> shopSet = new HashSet<>();
            Set<String> goodsSet = new HashSet<>();
            
            for (Map<String, Object> row : resultList) {
                Object qtyObj = row.get("STOCK_QTY");
                if (qtyObj != null) {
                    totalQty += Double.parseDouble(qtyObj.toString());
                }
                Object amtObj = row.get("STOCK_AMT");
                if (amtObj != null) {
                    totalAmt += Double.parseDouble(amtObj.toString());
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
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalQty", totalQty);
            summary.put("totalAmt", totalAmt);
            summary.put("shopCount", shopSet.size());
            summary.put("goodsCount", goodsSet.size());
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
