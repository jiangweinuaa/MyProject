package com.report.service.impl.stock;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 库存汇总查询服务实现
 */
@Service("stockSumQueryService")
public class StockSumQueryServiceImpl extends BaseService implements ReportService {

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        try {
            String shopId = getStringParam(params, "shopId", "");
            String eid = resolveEid(params);
            
            // 业务数据从商家库读取（降级到平台库）
            JdbcTemplate businessJdbc = resolveBusinessJdbcTemplate();
            
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT s.STOCK_ID, s.PLUNO, p.PRODUCT_NAME, s.STOCK_QTY, s.STOCK_DATE ");
            sqlBuilder.append("FROM STOCK s ");
            sqlBuilder.append("left join PRODUCT p on p.EID = s.EID and p.PLUNO = s.PLUNO ");
            sqlBuilder.append("where s.EID = ? ");
            
            List<Object> sqlParams = new ArrayList<>();
            sqlParams.add(eid);
            
            if (!shopId.isEmpty()) {
                sqlBuilder.append("AND s.SHOP_ID = ? ");
                sqlParams.add(shopId);
            }
            
            sqlBuilder.append("order by s.STOCK_DATE DESC");
            
            String sql = sqlBuilder.toString();
            String countSql = "SELECT COUNT(*) FROM STOCK s where s.EID = ? " +
                    (shopId.isEmpty() ? "" : "AND s.SHOP_ID = ?");

            Map<String, Object> pageData = buildPaginatedResult(
                businessJdbc, sql, countSql, pageNumber, pageSize, sqlParams.toArray()
            );
            
            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(pageData);
            response.setServiceDescription("查询成功，共 " + pageData.get("totalRecords") + " 条库存记录");
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
