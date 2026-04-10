package com.report.service.impl.system;

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
 * 查询所有企业编号服务实现
 */
@Service("allEidQueryService")
public class AllEidQueryServiceImpl extends BaseService implements ReportService {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public ServiceResponse<?> execute(Map<String, Object> params, Integer pageNumber, Integer pageSize) {
        if (jdbcTemplate == null) {
            return ServiceResponse.error("500", "数据库未连接");
        }

        try {
            String sql = "select distinct EID from PLATFORM_STAFFS order by EID";
            
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            // 提取 EID 列表
            java.util.List<String> eidList = new java.util.ArrayList<>();
            for (Map<String, Object> row : resultList) {
                Object eidObj = row.get("EID");
                if (eidObj != null) {
                    eidList.add(eidObj.toString());
                }
            }

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", eidList);
            resultData.put("total", eidList.size());

            ServiceResponse<Map<String, Object>> response = new ServiceResponse<>();
            response.setDatas(resultData);
            response.setServiceDescription("查询成功，共 " + eidList.size() + " 个企业");
            response.setTotalRecords(eidList.size());
            response.setTotalPages(1);
            response.setPageNumber(1);
            response.setPageSize(0);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.error("500", "查询失败：" + e.getMessage());
        }
    }
}
