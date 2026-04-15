package com.report.service.impl.stock;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 库存查询服务实现
 */
@Service("stockQueryService")
public class StockQueryServiceImpl extends BaseService implements ReportService {

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        try {
            String pluno = getStringParam(params, "pluno", "");
            String eid = resolveEid(params);
            
            // 业务数据从商家库读取（降级到平台库）
            JdbcTemplate businessJdbc = resolveBusinessJdbcTemplate();
            
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT s.STOCK_ID, s.PLUNO, p.PRODUCT_NAME, s.STOCK_QTY, s.STOCK_DATE, s.SHOP_ID ");
            sqlBuilder.append("FROM STOCK s ");
            sqlBuilder.append("left join PRODUCT p on p.EID = s.EID and p.PLUNO = s.PLUNO ");
            sqlBuilder.append("where s.EID = ? ");
            
            List<Object> sqlParams = new ArrayList<>();
            sqlParams.add(eid);
            
            if (!pluno.isEmpty()) {
                sqlBuilder.append("AND s.PLUNO = ? ");
                sqlParams.add(pluno);
            }
            
            sqlBuilder.append("order by s.STOCK_DATE DESC");
            
            String sql = sqlBuilder.toString();
            String countSql = "SELECT COUNT(*) FROM STOCK s where s.EID = ? " +
                    (pluno.isEmpty() ? "" : "AND s.PLUNO = ?");

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
