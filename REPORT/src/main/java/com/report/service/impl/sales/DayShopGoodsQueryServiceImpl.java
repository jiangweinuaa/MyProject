package com.report.service.impl.sales;

import com.report.dto.ServiceResponse;
import com.report.service.ReportService;
import com.report.service.impl.BaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 每日商品销售查询服务实现
 */
@Service("dayShopGoodsQueryService")
public class DayShopGoodsQueryServiceImpl extends BaseService implements ReportService {

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        try {
            String shopId = getStringParam(params, "shopId", "");
            String pluno = getStringParam(params, "pluno", "");
            String startDate = getStringParam(params, "startDate", "20250101");
            String endDate = getStringParam(params, "endDate", "20261231");
            String eid = resolveEid(params);
            
            // 业务数据从商家库读取（降级到平台库）
            JdbcTemplate businessJdbc = resolveBusinessJdbcTemplate();
            
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT a.SHOPID, gl.ORG_NAME, a.PLUNO, p.PRODUCT_NAME, a.BDATE as SALEDATE, ");
            sqlBuilder.append("SUM(a.SALE_QTY) as TOTAL_QTY, ");
            sqlBuilder.append("SUM(a.SALE_AMT) as TOTAL_AMT ");
            sqlBuilder.append("FROM DCP_SHOP_GOODS_SALE a ");
            sqlBuilder.append("left join DCP_ORG_LANG gl on gl.EID = a.EID and gl.ORGANIZATIONNO = a.SHOPID and gl.LANG_TYPE = 'zh_CN' ");
            sqlBuilder.append("left join PRODUCT p on p.EID = a.EID and p.PLUNO = a.PLUNO ");
            sqlBuilder.append("where a.EID = ? ");
            
            List<Object> sqlParams = new ArrayList<>();
            sqlParams.add(eid);
            
            if (!shopId.isEmpty()) {
                sqlBuilder.append("AND a.SHOPID = ? ");
                sqlParams.add(shopId);
            }
            if (!pluno.isEmpty()) {
                sqlBuilder.append("AND a.PLUNO = ? ");
                sqlParams.add(pluno);
            }
            
            sqlBuilder.append("AND a.BDATE >= ? AND a.BDATE <= ? ");
            sqlParams.add(startDate);
            sqlParams.add(endDate);
            
            sqlBuilder.append("group by a.BDATE, a.SHOPID, gl.ORG_NAME, a.PLUNO, p.PRODUCT_NAME ");
            sqlBuilder.append("order by a.BDATE, a.SHOPID, a.PLUNO");
            
            String sql = sqlBuilder.toString();
            String countSql = "SELECT COUNT(*) FROM (SELECT 1 FROM DCP_SHOP_GOODS_SALE a " +
                    "where a.EID = ? " +
                    (shopId.isEmpty() ? "" : "AND a.SHOPID = ? ") +
                    (pluno.isEmpty() ? "" : "AND a.PLUNO = ? ") +
                    "AND a.BDATE >= ? AND a.BDATE <= ?)";

            List<Object> countParams = new ArrayList<>();
            countParams.add(eid);
            if (!shopId.isEmpty()) countParams.add(shopId);
            if (!pluno.isEmpty()) countParams.add(pluno);
            countParams.add(startDate);
            countParams.add(endDate);

            Map<String, Object> pageData = buildPaginatedResult(
                businessJdbc, sql, countSql, pageNumber, pageSize, countParams.toArray()
            );
            
            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(pageData);
            response.setServiceDescription("查询成功，共 " + pageData.get("totalRecords") + " 条记录");
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
