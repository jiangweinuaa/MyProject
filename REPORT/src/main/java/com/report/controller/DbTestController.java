package com.report.controller;

import com.report.dto.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 数据库测试接口
 */
@RestController
@RequestMapping("/api")
public class DbTestController {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    /**
     * 查看 DCP_SALE_DETAIL 表结构
     */
    @GetMapping("/db-test/sale-detail-columns")
    public ServiceResponse<?> getSaleDetailColumns() {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String testSql = 
                "SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE " +
                "FROM USER_TAB_COLUMNS " +
                "WHERE TABLE_NAME = 'DCP_SALE_DETAIL' " +
                "ORDER BY COLUMN_ID";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(testSql);

            return ServiceResponse.success(resultList, "查询成功");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查看 DCP_GOODS 表结构
     */
    @GetMapping("/db-test/goods-columns")
    public ServiceResponse<?> getGoodsColumns() {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String testSql = 
                "SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE " +
                "FROM USER_TAB_COLUMNS " +
                "WHERE TABLE_NAME = 'DCP_GOODS' " +
                "ORDER BY COLUMN_ID";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(testSql);

            return ServiceResponse.success(resultList, "查询成功");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查看 DCP_CATEGORY_LANG 表结构
     */
    @GetMapping("/db-test/category-lang-columns")
    public ServiceResponse<?> getCategoryLangColumns() {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String testSql = 
                "SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE " +
                "FROM USER_TAB_COLUMNS " +
                "WHERE TABLE_NAME = 'DCP_CATEGORY_LANG' " +
                "ORDER BY COLUMN_ID";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(testSql);

            return ServiceResponse.success(resultList, "查询成功");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 测试品类销售 SQL
     */
    @GetMapping("/db-test/category-sale")
    public ServiceResponse<?> testCategorySaleSql() {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String testSql = 
                "SELECT d.EID, d.SHOPID, d.SALENO, d.PLUNO, d.AMT, d.BASEQTY, " +
                "cl.CATEGORY_NAME, a.BDATE, a.TYPE " +
                "FROM DCP_SALE a " +
                "JOIN DCP_SALE_DETAIL d ON d.EID = a.EID AND d.SHOPID = a.SHOPID AND d.SALENO = a.SALENO " +
                "JOIN DCP_GOODS g ON g.EID = d.EID AND g.PLUNO = d.PLUNO " +
                "LEFT JOIN DCP_CATEGORY_LANG cl ON cl.EID = g.EID AND cl.CATEGORY = g.CATEGORY AND cl.LANG_TYPE = 'zh_CN' " +
                "WHERE a.EID = '99' AND a.BDATE >= '20250101' AND a.BDATE <= '20261231' " +
                "AND ROWNUM <= 10";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(testSql);

            Map<String, Object> result = new HashMap<>();
            result.put("list", resultList);
            result.put("count", resultList.size());
            result.put("sql", testSql);

            return ServiceResponse.success(result, "查询成功");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }
}
