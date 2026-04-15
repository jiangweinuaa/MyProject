package com.report.service.impl;

import com.report.dto.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 门店商品销售预估查询服务实现（单品维度准确性分析）
 */
@Service("shopGoodsSaleForcastQueryService")
public class ShopGoodsSaleForcastQueryServiceImpl extends BaseService {
    
    /**
     * 查询单品准确性分析完整数据
     */
    public ServiceResponse queryGoodsAccuracyAnalysisFull(Map<String, Object> params) {
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
            
            // 3. 计算核心指标
            Map<String, Object> metrics = calculateMetrics(rawData);
            result.put("metrics", metrics);
            
            // 4. 计算偏差分析
            Map<String, Object> bias = calculateBias(rawData, metrics);
            result.put("bias", bias);
            
            // 5. SKU 明细数据
            List<Map<String, Object>> skuDetail = processSkuDetail(rawData);
            result.put("skuDetail", skuDetail);
            
            // 6. 误差区间分析
            List<Map<String, Object>> errorDist = calculateErrorDist(rawData);
            result.put("errorDist", errorDist);
            
            // 7. 按销量级别分析
            List<Map<String, Object>> salesLevelAnalysis = analyzeBySalesLevel(rawData);
            result.put("salesLevelAnalysis", salesLevelAnalysis);
            
            // 8. 重点关注 SKU（Top20）
            List<Map<String, Object>> top20Sku = getTop20Sku(rawData);
            result.put("top20Sku", top20Sku);
            
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
     * 查询原始数据（单品维度）
     */
    private List<Map<String, Object>> queryRawData(JdbcTemplate jdbc, String eid, String shopId, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.PLUNO, NVL(gl.PLU_NAME, a.PLUNO) || '(' || a.UNIT || ')' as PLU_NAME, ");
        sql.append("NVL(SUM(b.QTY), 0) as QTY, ");
        sql.append("SUM(a.QTY) as QTY_FORCAST, ");
        sql.append("NVL(SUM(b.AMT), 0) as AMT, ");
        sql.append("SUM(a.AMT) as AMT_FORCAST ");
        sql.append("from BI_SALE_DAY_GOODS_P a ");
        sql.append("left join BI_SALE_GOODS_DAY b on b.EID = a.EID and b.SHOPID = a.SHOPID ");
        sql.append("and b.SALEDATE = a.SALEDATE and b.PLUNO = a.PLUNO and b.FEATURENO = a.FEATURENO ");
        sql.append("left join DCP_GOODS_LANG gl on gl.EID = a.EID and gl.PLUNO = a.PLUNO AND gl.LANG_TYPE = 'zh_CN' ");
        sql.append("where a.EID = ? ");
        
        List<Object> params = new ArrayList<>();
        params.add(eid);
        
        if (shopId != null && !shopId.trim().isEmpty()) {
            sql.append("and a.SHOPID = ? ");
            params.add(shopId);
        }
        
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            sql.append("and a.SALEDATE >= TO_DATE(?, 'YYYY-MM-DD') ");
            sql.append("and a.SALEDATE <= TO_DATE(?, 'YYYY-MM-DD') ");
            params.add(startDate);
            params.add(endDate);
        }
        
        sql.append("group by a.PLUNO, NVL(gl.PLU_NAME, a.PLUNO) || '(' || a.UNIT || ')' ");
        sql.append("order by a.PLUNO");
        
        return jdbc.queryForList(sql.toString(), params.toArray());
    }
    
    /**
     * 计算核心指标
     */
    private Map<String, Object> calculateMetrics(List<Map<String, Object>> rawData) {
        Map<String, Object> result = new HashMap<>();
        
        int totalSku = rawData.size();
        int validSku = 0;
        double totalForecastQty = 0;
        double totalActualQty = 0;
        double totalMAE = 0;
        double totalSquaredError = 0;
        double totalMAPE = 0;
        
        for (Map<String, Object> row : rawData) {
            Number actualQty = (Number) row.get("QTY");
            Number forecastQty = (Number) row.get("QTY_FORCAST");
            
            if (actualQty == null || forecastQty == null) {
                continue;
            }
            
            double actual = actualQty.doubleValue();
            double forecast = forecastQty.doubleValue();
            
            // 实际为 0 的不纳入计算
            if (actual == 0) {
                continue;
            }
            
            validSku++;
            totalForecastQty += forecast;
            totalActualQty += actual;
            
            double error = Math.abs(forecast - actual);
            totalMAE += error;
            totalSquaredError += error * error;
            totalMAPE += (error / actual) * 100;
        }
        
        double avgMAE = validSku > 0 ? totalMAE / validSku : 0;
        double avgRMSE = validSku > 0 ? Math.sqrt(totalSquaredError / validSku) : 0;
        double avgMAPE = validSku > 0 ? totalMAPE / validSku : 0;
        double accuracyRate = 100 - avgMAPE;
        
        result.put("totalSku", totalSku);
        result.put("validSku", validSku);
        result.put("totalForecastQty", Math.round(totalForecastQty * 100.0) / 100.0);
        result.put("totalActualQty", Math.round(totalActualQty * 100.0) / 100.0);
        result.put("mae", Math.round(avgMAE * 100.0) / 100.0);
        result.put("rmse", Math.round(avgRMSE * 100.0) / 100.0);
        result.put("mape", Math.round(avgMAPE * 10.0) / 10.0);
        result.put("accuracyRate", Math.round(accuracyRate * 10.0) / 10.0);
        
        return result;
    }
    
    /**
     * 计算偏差分析
     */
    private Map<String, Object> calculateBias(List<Map<String, Object>> rawData, Map<String, Object> metrics) {
        Map<String, Object> result = new HashMap<>();
        
        int validSku = (Integer) metrics.get("validSku");
        double totalForecastQty = (Double) metrics.get("totalForecastQty");
        double totalActualQty = (Double) metrics.get("totalActualQty");
        
        double bias = validSku > 0 ? (totalForecastQty - totalActualQty) / validSku : 0;
        double totalBiasRate = totalActualQty > 0 ? ((totalForecastQty - totalActualQty) / totalActualQty) * 100 : 0;
        
        // 统计高估/低估 SKU 数
        int overestimateCount = 0;
        int underestimateCount = 0;
        
        for (Map<String, Object> row : rawData) {
            Number actualQty = (Number) row.get("QTY");
            Number forecastQty = (Number) row.get("QTY_FORCAST");
            
            if (actualQty != null && forecastQty != null && actualQty.doubleValue() > 0) {
                if (forecastQty.doubleValue() > actualQty.doubleValue()) {
                    overestimateCount++;
                } else {
                    underestimateCount++;
                }
            }
        }
        
        result.put("bias", Math.round(bias * 100.0) / 100.0);
        result.put("biasDirection", bias > 0 ? "高估" : (bias < 0 ? "低估" : "准确"));
        result.put("totalBiasRate", Math.round(totalBiasRate * 10.0) / 10.0);
        result.put("overestimateCount", overestimateCount);
        result.put("underestimateCount", underestimateCount);
        
        return result;
    }
    
    /**
     * 处理 SKU 明细数据
     */
    private List<Map<String, Object>> processSkuDetail(List<Map<String, Object>> rawData) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map<String, Object> row : rawData) {
            Number actualQty = (Number) row.get("QTY");
            Number forecastQty = (Number) row.get("QTY_FORCAST");
            String pluno = (String) row.get("PLUNO");
            String pluName = (String) row.get("PLU_NAME");
            String unit = (String) row.get("UNIT");
            
            if (actualQty != null && forecastQty != null) {
                double actual = actualQty.doubleValue();
                double forecast = forecastQty.doubleValue();
                double error = forecast - actual;
                double mape = actual > 0 ? Math.abs(error) / actual * 100 : 0;
                double accuracy = actual > 0 ? Math.max(0, (1 - Math.abs(error) / actual) * 100) : 0;
                
                Map<String, Object> item = new HashMap<>();
                item.put("pluno", pluno != null ? pluno : "");
                item.put("pluName", pluName != null ? pluName : "");
                item.put("unit", unit != null ? unit : "");
                item.put("forecastQty", Math.round(forecast * 100.0) / 100.0);
                item.put("actualQty", Math.round(actual * 100.0) / 100.0);
                item.put("error", Math.round(error * 100.0) / 100.0);
                item.put("mape", Math.round(mape * 10.0) / 10.0);
                item.put("accuracy", Math.round(accuracy * 10.0) / 10.0);
                item.put("salesLevel", getSalesLevel(actual));
                item.put("biasDirection", error > 0 ? "高估" : (error < 0 ? "低估" : "准确"));
                
                result.add(item);
            }
        }
        
        return result;
    }
    
    /**
     * 计算误差区间分析
     */
    private List<Map<String, Object>> calculateErrorDist(List<Map<String, Object>> rawData) {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("0-5%", 0);
        dist.put("5-10%", 0);
        dist.put("10-20%", 0);
        dist.put("20-30%", 0);
        dist.put("30-50%", 0);
        dist.put("50% 以上", 0);
        
        int validCount = 0;
        
        for (Map<String, Object> row : rawData) {
            Number actualQty = (Number) row.get("QTY");
            Number forecastQty = (Number) row.get("QTY_FORCAST");
            
            if (actualQty == null || forecastQty == null || actualQty.doubleValue() <= 0) {
                continue;
            }
            
            validCount++;
            double actual = actualQty.doubleValue();
            double forecast = forecastQty.doubleValue();
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
        
        String[] qualityLabels = {"极准确", "准确", "尚可", "偏差较大", "偏差大", "严重偏差"};
        int idx = 0;
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("range", entry.getKey());
            item.put("count", entry.getValue());
            item.put("percentage", validCount > 0 ? Math.round(entry.getValue() * 1000.0 / validCount) / 10.0 : 0);
            double cumulative = 0;
            for (int i = 0; i <= idx; i++) {
                cumulative += (double) new ArrayList<>(dist.values()).get(i) / validCount * 100;
            }
            item.put("cumulativePercentage", Math.round(cumulative * 10.0) / 10.0);
            item.put("quality", qualityLabels[idx++]);
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 按销量级别分析
     */
    private List<Map<String, Object>> analyzeBySalesLevel(List<Map<String, Object>> rawData) {
        Map<String, List<Map<String, Object>>> levelData = new LinkedHashMap<>();
        levelData.put("微量 (1-3)", new ArrayList<>());
        levelData.put("低量 (4-10)", new ArrayList<>());
        levelData.put("中量 (11-20)", new ArrayList<>());
        levelData.put("高量 (21-50)", new ArrayList<>());
        levelData.put("超高量 (50+)", new ArrayList<>());
        
        for (Map<String, Object> row : rawData) {
            Number actualQty = (Number) row.get("QTY");
            Number forecastQty = (Number) row.get("QTY_FORCAST");
            
            if (actualQty != null && forecastQty != null && actualQty.doubleValue() > 0) {
                String level = getSalesLevel(actualQty.doubleValue());
                levelData.get(level).add(row);
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : levelData.entrySet()) {
            List<Map<String, Object>> items = entry.getValue();
            if (items.isEmpty()) continue;
            
            double totalForecast = 0, totalActual = 0, totalMAPE = 0;
            double totalBias = 0;
            int overCount = 0, underCount = 0;
            
            for (Map<String, Object> r : items) {
                double actual = ((Number) r.get("QTY")).doubleValue();
                double forecast = ((Number) r.get("QTY_FORCAST")).doubleValue();
                totalForecast += forecast;
                totalActual += actual;
                totalMAPE += Math.abs(forecast - actual) / actual * 100;
                totalBias += (forecast - actual);
                if (forecast > actual) overCount++;
                else underCount++;
            }
            
            int count = items.size();
            Map<String, Object> stats = new HashMap<>();
            stats.put("level", entry.getKey());
            stats.put("skuCount", count);
            stats.put("forecastAvg", Math.round(totalForecast / count * 100.0) / 100.0);
            stats.put("actualAvg", Math.round(totalActual / count * 100.0) / 100.0);
            stats.put("mape", Math.round(totalMAPE / count * 10.0) / 10.0);
            stats.put("accuracy", Math.round((100 - totalMAPE / count) * 10.0) / 10.0);
            stats.put("bias", Math.round(totalBias / count * 100.0) / 100.0);
            stats.put("biasRate", totalActual > 0 ? Math.round((totalForecast - totalActual) / totalActual * 1000.0) / 10.0 : 0);
            stats.put("overestimateCount", overCount);
            stats.put("underestimateCount", underCount);
            
            result.add(stats);
        }
        
        return result;
    }
    
    /**
     * 获取重点关注 SKU（误差最大的 Top20）
     */
    private List<Map<String, Object>> getTop20Sku(List<Map<String, Object>> rawData) {
        List<Map<String, Object>> skuList = new ArrayList<>();
        
        for (Map<String, Object> row : rawData) {
            Number actualQty = (Number) row.get("QTY");
            Number forecastQty = (Number) row.get("QTY_FORCAST");
            
            if (actualQty != null && forecastQty != null && actualQty.doubleValue() > 0) {
                double actual = actualQty.doubleValue();
                double forecast = forecastQty.doubleValue();
                double error = Math.abs(forecast - actual);
                double mape = error / actual * 100;
                
                Map<String, Object> item = new HashMap<>();
                item.put("pluno", (String) row.get("PLUNO"));
                item.put("pluName", (String) row.get("PLU_NAME"));
                item.put("forecastQty", Math.round(forecast * 100.0) / 100.0);
                item.put("actualQty", Math.round(actual * 100.0) / 100.0);
                item.put("error", Math.round((forecast - actual) * 100.0) / 100.0);
                item.put("mape", Math.round(mape * 10.0) / 10.0);
                item.put("salesLevel", getSalesLevel(actual));
                
                skuList.add(item);
            }
        }
        
        // 按 MAPE 降序排序
        skuList.sort((a, b) -> Double.compare((Double) b.get("mape"), (Double) a.get("mape")));
        
        // 返回 Top20
        return skuList.subList(0, Math.min(20, skuList.size()));
    }
    
    /**
     * 计算综合评级
     */
    private Map<String, Object> calculateRating(Map<String, Object> metrics) {
        Map<String, Object> result = new HashMap<>();
        
        double accuracyRate = (Double) metrics.get("accuracyRate");
        double mape = (Double) metrics.get("mape");
        
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
        result.put("description", String.format("MAPE = %.1f%%", mape));
        
        return result;
    }
    
    /**
     * 获取销量级别
     */
    private String getSalesLevel(double actualQty) {
        if (actualQty <= 3) return "微量 (1-3)";
        if (actualQty <= 10) return "低量 (4-10)";
        if (actualQty <= 20) return "中量 (11-20)";
        if (actualQty <= 50) return "高量 (21-50)";
        return "超高量 (50+)";
    }
}
