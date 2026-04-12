package com.report.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 腾讯云识别客户端
 * 提供商品识别功能
 */
@Component
public class TencentCloudClient {
    
    @Autowired
    private AliyunConfigUtil configUtil;
    
    /**
     * 调用腾讯云商品识别 API
     * API 文档：https://cloud.tencent.com/document/product/865/36457
     * @param imageUrl 图片 URL（OSS URL）
     * @return 识别结果 Map
     */
    public Map<String, Object> recognizeProduct(String imageUrl) {
        String secretId = configUtil.getTencentSecretId();
        String secretKey = configUtil.getTencentSecretKey();
        
        Map<String, Object> result = new HashMap<>();
        
        // 检查配置
        if (secretId == null || secretKey == null ||
            secretId.trim().isEmpty() || secretKey.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "腾讯云 SecretId/SecretKey 未配置");
            result.put("pluno", null);
            result.put("productName", null);
            result.put("category", null);
            result.put("confidence", 0.0);
            return result;
        }
        
        try {
            System.out.println("🔍 开始调用腾讯云商品识别 API...");
            System.out.println("📷 图片 URL: " + imageUrl);
            
            // 腾讯云 API 参数
            String host = "tiia.tencentcloudapi.com";
            String action = "DetectProduct";
            String version = "2019-05-29";
            String region = "ap-guangzhou";
            
            // 构建请求体
            String requestBody = String.format("{\"ImageUrl\":\"%s\"}", imageUrl);
            
            // 生成签名
            Map<String, String> headers = generateTencentSign(secretId, secretKey, host, action, version, region, requestBody);
            
            // 发送请求
            String responseBody = sendRequest(host, headers, requestBody);
            
            System.out.println("🔍 腾讯云 API 响应：" + responseBody);
            
            // 解析响应
            result.put("success", true);
            result.put("message", "识别成功");
            
            // 提取第一个商品结果
            String productName = extractFromJson(responseBody, "Name");
            String parents = extractFromJson(responseBody, "Parents");
            String confidence = extractFromJson(responseBody, "Confidence");
            
            // 腾讯云返回的置信度是整数（0-100），需要转换为小数
            Double confidenceValue = confidence != null ? Double.parseDouble(confidence) / 100.0 : 0.85;
            
            result.put("sourcePluno", null);  // 腾讯云不返回 CategoryId
            result.put("productName", productName != null ? productName : "识别商品");
            result.put("category", parents != null ? parents : (productName != null ? productName : "通用商品"));
            result.put("confidence", confidenceValue);
            result.put("platform", "TENCENT");  // 标记识别平台
            
            System.out.println("🎯 腾讯云识别完成：商品=" + productName + ", 置信度=" + confidence);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("腾讯云商品识别失败：" + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("message", "识别失败：" + e.getMessage());
            result.put("pluno", null);
            result.put("productName", null);
            result.put("category", null);
            result.put("confidence", 0.0);
            
            return result;
        }
    }
    
    /**
     * 生成腾讯云签名（TC3-HMAC-SHA256）
     */
    private Map<String, String> generateTencentSign(String secretId, String secretKey, 
                                                     String host, String action, 
                                                     String version, String region,
                                                     String requestBody) throws Exception {
        
        // 时间戳
        long timestamp = System.currentTimeMillis() / 1000;
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp * 1000));
        
        // 1. 拼接规范请求串
        String httpRequestMethod = "POST";
        String uri = "/";
        String queryString = "";
        
        // 2. 拼接规范请求串
        String canonicalRequest = httpRequestMethod + "\n" +
                                 uri + "\n" +
                                 queryString + "\n" +
                                 "content-type:application/json; charset=utf-8\n" +
                                 "host:" + host + "\n" +
                                 "\n" +
                                 "content-type;host\n" +
                                 sha256Hex(requestBody);
        
        // 3. 拼接待签名字符串
        String algorithm = "TC3-HMAC-SHA256";
        String credentialScope = date + "/tiia/tc3_request";
        String stringToSign = algorithm + "\n" +
                             timestamp + "\n" +
                             credentialScope + "\n" +
                             sha256Hex(canonicalRequest);
        
        // 4. 计算签名
        byte[] kDate = hmacSha256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date.getBytes(StandardCharsets.UTF_8));
        byte[] kService = hmacSha256(kDate, "tiia".getBytes(StandardCharsets.UTF_8));
        byte[] kSigning = hmacSha256(kService, "tc3_request".getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = hmacSha256(kSigning, stringToSign.getBytes(StandardCharsets.UTF_8));
        String signature = bytesToHex(signatureBytes);
        
        // 5. 组装 Authorization
        String authorization = algorithm + " Credential=" + secretId + "/" + credentialScope +
                              ", SignedHeaders=content-type;host, Signature=" + signature;
        
        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorization);
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Host", host);
        headers.put("X-TC-Action", action);
        headers.put("X-TC-Version", version);
        headers.put("X-TC-Region", region);
        headers.put("X-TC-Timestamp", String.valueOf(timestamp));
        
        return headers;
    }
    
    /**
     * 发送 HTTP 请求
     */
    private String sendRequest(String host, Map<String, String> headers, String requestBody) throws Exception {
        URL url = new URL("https://" + host);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        
        // 设置请求头
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        
        // 发送请求体
        conn.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));
        
        int responseCode = conn.getResponseCode();
        System.out.println("📡 HTTP 响应码：" + responseCode);
        
        if (responseCode == 200) {
            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();
            return response;
        } else {
            String errorBody = "";
            if (conn.getErrorStream() != null) {
                Scanner scanner = new Scanner(conn.getErrorStream(), "UTF-8");
                errorBody = scanner.useDelimiter("\\A").next();
                scanner.close();
            }
            System.err.println("❌ 错误响应：" + errorBody);
            throw new Exception("API 调用失败，HTTP 状态码：" + responseCode + ", 错误：" + errorBody);
        }
    }
    
    /**
     * 简单 JSON 提取
     */
    private String extractFromJson(String json, String key) {
        if (json == null || key == null) return null;
        String searchKey = "\"" + key + "\":\"";
        int index = json.indexOf(searchKey);
        if (index == -1) {
            // 尝试数字格式
            searchKey = "\"" + key + "\":";
            index = json.indexOf(searchKey);
            if (index == -1) return null;
            int start = index + searchKey.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).trim();
        }
        int start = index + searchKey.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
    
    // 工具方法
    private String sha256Hex(String str) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    private byte[] hmacSha256(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * 从 MultipartFile 直接识别商品（不上传 OSS）
     * @param file 图片文件
     * @return 识别结果
     */
    public Map<String, Object> recognizeProductFromMultipart(MultipartFile file) {
        try {
            // 将 MultipartFile 转换为 Base64
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // 调用腾讯云 API（使用 Base64）
            return recognizeProductFromBase64(base64Image);
            
        } catch (IOException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "图片处理失败：" + e.getMessage());
            result.put("pluno", null);
            result.put("confidence", 0.0);
            return result;
        }
    }
    
    /**
     * 从 Base64 图片识别商品
     * @param base64Image Base64 编码的图片
     * @return 识别结果
     */
    public Map<String, Object> recognizeProductFromBase64(String base64Image) {
        String secretId = configUtil.getTencentSecretId();
        String secretKey = configUtil.getTencentSecretKey();
        
        Map<String, Object> result = new HashMap<>();
        
        // 检查配置
        if (secretId == null || secretKey == null ||
            secretId.trim().isEmpty() || secretKey.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "腾讯云 SecretId/SecretKey 未配置");
            result.put("pluno", null);
            result.put("productName", null);
            result.put("category", null);
            result.put("confidence", 0.0);
            return result;
        }
        
        try {
            System.out.println("🔍 开始调用腾讯云商品识别 API（Base64）...");
            
            // 腾讯云 API 参数
            String host = "tiia.tencentcloudapi.com";
            String action = "DetectProduct";
            String version = "2019-05-29";
            String region = "ap-guangzhou";
            
            // 构建请求体（使用 ImageBase64）
            String requestBody = String.format("{\"ImageBase64\":\"%s\"}", base64Image);
            
            // 生成签名
            Map<String, String> headers = generateTencentSign(secretId, secretKey, host, action, version, region, requestBody);
            
            // 发送请求
            String responseBody = sendRequest(host, headers, requestBody);
            
            System.out.println("🔍 腾讯云 API 响应：" + responseBody);
            
            // 解析响应
            result.put("success", true);
            result.put("message", "识别成功");
            
            // 提取第一个商品结果
            String productName = extractFromJson(responseBody, "Name");
            String parents = extractFromJson(responseBody, "Parents");
            String confidence = extractFromJson(responseBody, "Confidence");
            
            // 腾讯云返回的置信度是整数（0-100），需要转换为小数
            Double confidenceValue = confidence != null ? Double.parseDouble(confidence) / 100.0 : 0.85;
            
            result.put("sourcePluno", null);  // 腾讯云不返回 CategoryId
            result.put("productName", productName != null ? productName : "识别商品");
            result.put("category", parents != null ? parents : (productName != null ? productName : "通用商品"));
            result.put("confidence", confidenceValue);
            result.put("platform", "TENCENT");  // 标记识别平台
            
            System.out.println("🎯 腾讯云识别完成：商品=" + productName + ", 置信度=" + confidence);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("腾讯云商品识别失败：" + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("message", "识别失败：" + e.getMessage());
            result.put("pluno", null);
            result.put("productName", null);
            result.put("category", null);
            result.put("confidence", 0.0);
            
            return result;
        }
    }
}
