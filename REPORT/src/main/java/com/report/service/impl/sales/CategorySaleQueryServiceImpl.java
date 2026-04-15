package com.report.service.impl.sales;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 品类销售查询服务实现
 */
@Service("categorySaleQueryService")
public class CategorySaleQueryServiceImpl extends BaseService implements ReportService {

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        try {
            String startDate = getStringParam(params, "startDate", "20250101");
            String endDate = getStringParam(params, "endDate", "20261231");
            String eid = resolveEid(params);
            
            // 业务数据从商家库读取（降级到平台库）
            JdbcTemplate businessJdbc = resolveBusinessJdbcTemplate();
            
            String sql = "SELECT a.CATEGORY_ID, c.CATEGORY_NAME, " +
                    "SUM(a.SALE_QTY) as TOTAL_QTY, " +
                    "SUM(a.SALE_AMT) as TOTAL_AMT " +
                    "FROM DCP_CATEGORY_SALE a " +
                    "left join CATEGORY c on c.EID = a.EID and c.CATEGORY_ID = a.CATEGORY_ID " +
                    "where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? " +
                    "group by a.CATEGORY_ID, c.CATEGORY_NAME " +
                    "order by TOTAL_AMT DESC";
            
            String countSql = "SELECT COUNT(*) FROM (SELECT a.CATEGORY_ID FROM DCP_CATEGORY_SALE a " +
                    "where a.EID = ? AND a.BDATE >= ? AND a.BDATE <= ? " +
                    "group by a.CATEGORY_ID)";

            Map<String, Object> pageData = buildPaginatedResult(
                businessJdbc, sql, countSql, pageNumber, pageSize, eid, startDate, endDate
            );
            
            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(pageData);
            response.setServiceDescription("查询成功，共 " + pageData.get("totalRecords") + " 个品类");
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
