package com.report.service.impl;

import com.report.dto.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 门店销售预估查询服务实现
 */
@Service("shopSaleForecastQueryService")
public class ShopSaleForecastQueryServiceImpl extends BaseService {
    
    /**
     * 查询准确性分析完整数据（一个接口返回所有数据）
     */
    public ServiceResponse queryAccuracyAnalysisFull(Map<String, Object> params) {
        ServiceResponse response = new ServiceResponse();
        
        try {
            // 业务数据从商家库读取（降级到平台库）
            JdbcTemplate businessJdbc = resolveBusinessJdbcTemplate();
            
            // 从 token 解析 EID（使用平台库）
            String eid = resolveEid(params);
            if (eid == null || eid.isEmpty()) {
                response.setSuccess(false);
                response.setServiceDescription("未找到用户信息，请重新登录");
                return response;
            }
            
            String shopId = getStringParam(params, "shopId", "");
            String startDate = getStringParam(params, "startDate", "");
            String endDate = getStringParam(params, "endDate", "");
            
            // 1. 查询原始数据（使用商家库）
            List<Map<String, Object>> rawData = queryRawData(businessJdbc, eid, shopId, startDate, endDate);
            
            // 2. 构建返回结果
            Map<String, Object> result = new HashMap<>();
            
            // 3. 计算准确性指标
            Map<String, Object> metrics = calculateAccuracyMetrics(rawData);
            result.put("metrics", metrics);
            
            // 4. 计算偏差分析
            Map<String, Object> bias = calculateBiasAnalysis(rawData, metrics);
            result.put("bias", bias);
            
            // 5. 按星期维度分析
            List<Map<String, Object>> weekdayAnalysis = analyzeByWeekday(rawData);
            result.put("weekdayData", weekdayAnalysis);
            
            // 6. 误差分布
            List<Map<String, Object>> errorDist = calculateErrorDistribution(rawData);
            result.put("errorDist", errorDist);
            
            // 7. 按日期维度分析（新增）
            List<Map<String, Object>> dateAnalysis = analyzeByDate(rawData);
            result.put("dateAnalysis", dateAnalysis);
            
            // 8. 逐日数据（用于图表）
            List<Map<String, Object>> dailyData = processDailyData(rawData);
            result.put("dailyData", dailyData);
            
            // 9. 异常天数
            List<Map<String, Object>> abnormalDays = extractAbnormalDays(rawData);
            result.put("abnormalDays", abnormalDays);
            
            // 9. 综合评级
            Map<String, Object> rating = calculateRating(metrics);
            result.put("rating", rating);
            
            response.setSuccess(true);
            response.setDatas(result);
            response.setServiceDescription("分析成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setServiceDescription("分析失败：" + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 查询原始数据（只查询销售额，不查询数量）
     */
    private List<Map<String, Object>> queryRawData(JdbcTemplate jdbc, String eid, String shopId, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.SALEDATE, ");
        sql.append("NVL(SUM(b.AMT), 0) as AMT, ");
        sql.append("SUM(a.AMT) as AMT_FORCAST ");
        sql.append("from BI_SALE_DAY_P a ");
        sql.append("left join BI_SALE_DAY b on b.EID = a.EID and b.SHOPID = a.SHOPID and b.SALEDATE = a.SALEDATE ");
        sql.append("where a.EID = ? ");
        
        // SHOPID 可选
        if (shopId != null && !shopId.trim().isEmpty()) {
            sql.append("and a.SHOPID = ? ");
        }
        
        // 日期范围可选
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            sql.append("and a.SALEDATE >= TO_DATE(?, 'YYYY-MM-DD') ");
            sql.append("and a.SALEDATE <= TO_DATE(?, 'YYYY-MM-DD') ");
        }
        
        sql.append("group by a.SALEDATE ");
        sql.append("order by a.SALEDATE");
        
        // 构建参数列表
        List<Object> params = new ArrayList<>();
        params.add(eid);
        
        if (shopId != null && !shopId.trim().isEmpty()) {
            params.add(shopId);
        }
        
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            params.add(startDate);
            params.add(endDate);
        }
        
        return jdbc.queryForList(sql.toString(), params.toArray());
    }
    
    /**
     * 计算准确性指标
     */
    private Map<String, Object> calculateAccuracyMetrics(List<Map<String, Object>> rawData) {
        Map<String, Object> result = new HashMap<>();
        
        int totalDays = rawData.size();
        int abnormalDays = 0;
        double totalSalesMAE = 0;
        double totalSalesMAPE = 0;
        double totalSquaredError = 0;
        int validDays = 0;
        
        for (Map<String, Object> row : rawData) {
            Number actualAmt = (Number) row.get("AMT");
            Number forecastAmt = (Number) row.get("AMT_FORCAST");
            
            if (actualAmt == null || forecastAmt == null) {
                continue;
            }
            
            double actual = actualAmt.doubleValue();
            double forecast = forecastAmt.doubleValue();
            
            // 检查异常（负数预估）
            if (forecast < 0) {
                abnormalDays++;
                continue;
            }
            
            // 实际销售额为 0 的日期不纳入计算
            if (actual == 0) {
                continue;
            }
            
            validDays++;
            
            // 计算误差
            double salesError = Math.abs(forecast - actual);
            totalSalesMAE += salesError;
            
            // 计算平方误差（用于 RMSE）
            totalSquaredError += salesError * salesError;
            
            // 计算 MAPE
            if (actual > 0) {
                totalSalesMAPE += (salesError / actual) * 100;
            }
        }
        
        // 计算平均值
        double avgSalesMAE = validDays > 0 ? totalSalesMAE / validDays : 0;
        double avgSalesMAPE = validDays > 0 ? totalSalesMAPE / validDays : 0;
        double salesRMSE = validDays > 0 ? Math.sqrt(totalSquaredError / validDays) : 0;
        
        // 计算准确率
        double accuracyRate = 100 - avgSalesMAPE;
        
        result.put("validDays", validDays);
        result.put("abnormalDays", abnormalDays);
        result.put("totalDays", totalDays);
        result.put("accuracyRate", Math.round(accuracyRate * 10.0) / 10.0);
        result.put("salesMAE", Math.round(avgSalesMAE * 10.0) / 10.0);
        result.put("salesMAPE", Math.round(avgSalesMAPE * 10.0) / 10.0);
        result.put("salesRMSE", Math.round(salesRMSE * 10.0) / 10.0);
        
        return result;
    }
    
    /**
     * 计算偏差分析
     */
    private Map<String, Object> calculateBiasAnalysis(List<Map<String, Object>> rawData, Map<String, Object> metrics) {
        Map<String, Object> result = new HashMap<>();
        
        double totalForecastSales = 0;
        double totalActualSales = 0;
        double totalSalesMAE = 0;
        int validDays = (Integer) metrics.get("validDays");
        
        for (Map<String, Object> row : rawData) {
            Number actualAmt = (Number) row.get("AMT");
            Number forecastAmt = (Number) row.get("AMT_FORCAST");
            
            // 实际销售额为 0 的日期不纳入计算
            if (actualAmt != null && forecastAmt != null && forecastAmt.doubleValue() >= 0 && actualAmt.doubleValue() > 0) {
                double actual = actualAmt.doubleValue();
                double forecast = forecastAmt.doubleValue();
                totalForecastSales += forecast;
                totalActualSales += actual;
                totalSalesMAE += Math.abs(forecast - actual);
            }
        }
        
        double salesBias = validDays > 0 ? (totalForecastSales - totalActualSales) / validDays : 0;
        double totalBiasRate = totalActualSales > 0 ? ((totalForecastSales - totalActualSales) / totalActualSales) * 100 : 0;
        
        result.put("salesBias", Math.round(salesBias * 10.0) / 10.0);
        result.put("biasDirection", salesBias > 0 ? "高估" : (salesBias < 0 ? "低估" : "准确"));
        result.put("totalBiasRate", Math.round(totalBiasRate * 10.0) / 10.0);
        result.put("totalForecastSales", Math.round(totalForecastSales * 100.0) / 100.0);
        result.put("totalActualSales", Math.round(totalActualSales * 100.0) / 100.0);
        result.put("difference", Math.round((totalForecastSales - totalActualSales) * 100.0) / 100.0);
        result.put("salesMAE", Math.round(totalSalesMAE / validDays * 10.0) / 10.0);
        
        return result;
    }
    
    /**
     * 按星期维度分析
     */
    private List<Map<String, Object>> analyzeByWeekday(List<Map<String, Object>> rawData) {
        Map<Integer, List<Map<String, Object>>> weekdayData = new HashMap<>();
        
        // 按星期分组
        for (Map<String, Object> row : rawData) {
            Date saleDate = (Date) row.get("SALEDATE");
            if (saleDate == null) continue;
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(saleDate);
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            
            weekdayData.computeIfAbsent(weekday, k -> new ArrayList<>()).add(row);
        }
        
        // 计算每个星期的指标
        List<Map<String, Object>> result = new ArrayList<>();
        String[] weekdayNames = {"", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Integer[] weekdayOrder = {2, 3, 4, 5, 6, 7, 1}; // 星期一到星期日
        
        for (int weekday : weekdayOrder) {
            List<Map<String, Object>> dayData = weekdayData.getOrDefault(weekday, new ArrayList<>());
            if (dayData.isEmpty()) continue;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("weekday", weekdayNames[weekday]);
            stats.put("weekdayIndex", weekday == 1 ? 7 : weekday - 1); // 用于排序：星期一=1, 星期日=7
            stats.put("days", dayData.size());
            
            double totalForecast = 0, totalActual = 0, totalMAE = 0;
            
            for (Map<String, Object> r : dayData) {
                Number actualAmt = (Number) r.get("AMT");
                Number forecastAmt = (Number) r.get("AMT_FORCAST");
                
                if (actualAmt != null && forecastAmt != null && forecastAmt.doubleValue() >= 0 && actualAmt.doubleValue() > 0) {
                    double actual = actualAmt.doubleValue();
                    double forecast = forecastAmt.doubleValue();
                    totalForecast += forecast;
                    totalActual += actual;
                    totalMAE += Math.abs(forecast - actual);
                }
            }
            
            stats.put("forecastAvg", Math.round(totalForecast / dayData.size() * 10.0) / 10.0);
            stats.put("actualAvg", Math.round(totalActual / dayData.size() * 10.0) / 10.0);
            stats.put("mae", Math.round(totalMAE / dayData.size() * 10.0) / 10.0);
            stats.put("bias", Math.round((totalForecast - totalActual) / dayData.size() * 10.0) / 10.0);
            
            double accuracy = totalActual > 0 ? (1 - Math.abs(totalForecast - totalActual) / totalActual) * 100 : 0;
            stats.put("accuracy", Math.round(Math.max(0, accuracy) * 10.0) / 10.0);
            
            result.add(stats);
        }
        
        return result;
    }
    
    /**
     * 计算误差分布
     */
    private List<Map<String, Object>> calculateErrorDistribution(List<Map<String, Object>> rawData) {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("0-5%", 0);
        dist.put("5-10%", 0);
        dist.put("10-20%", 0);
        dist.put("20-30%", 0);
        dist.put("30-50%", 0);
        dist.put("50% 以上", 0);
        
        int validCount = 0;
        
        for (Map<String, Object> row : rawData) {
            Number actualAmt = (Number) row.get("AMT");
            Number forecastAmt = (Number) row.get("AMT_FORCAST");
            
            if (actualAmt == null || forecastAmt == null || actualAmt.doubleValue() <= 0 || forecastAmt.doubleValue() < 0) {
                continue;
            }
            
            validCount++;
            double actual = actualAmt.doubleValue();
            double forecast = forecastAmt.doubleValue();
            double mape = Math.abs(forecast - actual) / actual * 100;
            
            if (mape < 5) {
                dist.put("0-5%", dist.get("0-5%") + 1);
            } else if (mape < 10) {
                dist.put("5-10%", dist.get("5-10%") + 1);
            } else if (mape < 20) {
                dist.put("10-20%", dist.get("10-20%") + 1);
            } else if (mape < 30) {
                dist.put("20-30%", dist.get("20-30%") + 1);
            } else if (mape < 50) {
                dist.put("30-50%", dist.get("30-50%") + 1);
            } else {
                dist.put("50% 以上", dist.get("50% 以上") + 1);
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("range", entry.getKey());
            item.put("count", entry.getValue());
            item.put("percentage", validCount > 0 ? Math.round(entry.getValue() * 1000.0 / validCount) / 10.0 : 0);
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 按日期维度分析（新增）
     */
    private List<Map<String, Object>> analyzeByDate(List<Map<String, Object>> rawData) {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] weekdayNames = {"", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        
        for (Map<String, Object> row : rawData) {
            Number actualAmt = (Number) row.get("AMT");
            Number forecastAmt = (Number) row.get("AMT_FORCAST");
            Date saleDate = (Date) row.get("SALEDATE");
            
            if (actualAmt != null && forecastAmt != null && saleDate != null) {
                double actual = actualAmt.doubleValue();
                double forecast = forecastAmt.doubleValue();
                double error = forecast - actual;
                double errorRate = actual > 0 ? (error / actual) * 100 : 0;
                double mape = actual > 0 ? Math.abs(error) / actual * 100 : 0;
                double accuracy = actual > 0 ? Math.max(0, (1 - Math.abs(error) / actual) * 100) : 0;
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(saleDate);
                int weekday = cal.get(Calendar.DAY_OF_WEEK);
                
                Map<String, Object> item = new HashMap<>();
                item.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(saleDate));
                item.put("weekday", weekdayNames[weekday]);
                item.put("forecastSales", Math.round(forecast * 100.0) / 100.0);
                item.put("actualSales", Math.round(actual * 100.0) / 100.0);
                item.put("errorSales", Math.round(error * 100.0) / 100.0);
                item.put("errorRate", Math.round(errorRate * 10.0) / 10.0);
                item.put("mape", Math.round(mape * 10.0) / 10.0);
                item.put("accuracy", Math.round(accuracy * 10.0) / 10.0);
                item.put("abnormal", forecast < 0);
                
                result.add(item);
            }
        }
        
        return result;
    }
    
    /**
     * 处理逐日数据（只处理销售额，用于图表）
     */
    private List<Map<String, Object>> processDailyData(List<Map<String, Object>> rawData) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map<String, Object> row : rawData) {
            Number actualAmt = (Number) row.get("AMT");
            Number forecastAmt = (Number) row.get("AMT_FORCAST");
            Date saleDate = (Date) row.get("SALEDATE");
            
            if (actualAmt != null && forecastAmt != null && saleDate != null) {
                double actual = actualAmt.doubleValue();
                double forecast = forecastAmt.doubleValue();
                double accuracy = actual > 0 ? Math.max(0, (1 - Math.abs(forecast - actual) / actual) * 100) : 0;
                
                Map<String, Object> item = new HashMap<>();
                item.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(saleDate));
                item.put("forecast", forecast);
                item.put("actual", actual);
                item.put("accuracy", Math.round(accuracy * 10.0) / 10.0);
                item.put("abnormal", forecast < 0);
                
                result.add(item);
            }
        }
        
        return result;
    }
    
    /**
     * 提取异常天数
     */
    private List<Map<String, Object>> extractAbnormalDays(List<Map<String, Object>> rawData) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map<String, Object> row : rawData) {
            Number forecastAmt = (Number) row.get("AMT_FORCAST");
            Date saleDate = (Date) row.get("SALEDATE");
            
            if (forecastAmt != null && forecastAmt.doubleValue() < 0 && saleDate != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(saleDate));
                item.put("value", forecastAmt + "件");
                result.add(item);
            }
        }
        
        return result;
    }
    
    /**
     * 计算综合评级
     */
    private Map<String, Object> calculateRating(Map<String, Object> metrics) {
        Map<String, Object> result = new HashMap<>();
        
        double accuracyRate = (Double) metrics.get("accuracyRate");
        int abnormalDays = (Integer) metrics.get("abnormalDays");
        double salesMAPE = (Double) metrics.get("salesMAPE");
        
        int stars;
        String text;
        
        if (accuracyRate >= 90) {
            stars = 5;
            text = "优秀 ★★★★★";
        } else if (accuracyRate >= 80) {
            stars = 4;
            text = "良好 ★★★★☆";
        } else if (accuracyRate >= 70) {
            stars = 3;
            text = "一般 ★★★☆☆";
        } else if (accuracyRate >= 60) {
            stars = 2;
            text = "较差 ★★☆☆☆";
        } else {
            stars = 1;
            text = "差 ★☆☆☆☆";
        }
        
        result.put("stars", stars);
        result.put("text", text);
        result.put("description", String.format("销量 MAPE = %.1f%%（剔除%d天异常预估）", salesMAPE, abnormalDays));
        
        return result;
    }
}
