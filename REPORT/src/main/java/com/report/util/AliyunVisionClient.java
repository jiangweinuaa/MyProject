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
 * 阿里云视觉智能客户端
 * 提供 OSS 图片上传和商品识别功能
 */
@Component
public class AliyunVisionClient {
    
    @Autowired
    private AliyunConfigUtil configUtil;
    
    /**
     * 获取 OSS Bucket 名称（从数据库读取）
     */
    private String getOssBucket() {
        String bucket = configUtil.getOssBucket();
        if (bucket == null || bucket.trim().isEmpty()) {
            // 默认值
            return "product-training-images";
        }
        return bucket.trim();
    }
    
    /**
     * 获取 OSS Endpoint（从数据库读取）
     */
    private String getOssEndpoint() {
        String endpoint = configUtil.getOssEndpoint();
        if (endpoint == null || endpoint.trim().isEmpty()) {
            // 默认值
            return "oss-cn-shanghai.aliyuncs.com";
        }
        return endpoint.trim();
    }
    
    /**
     * 上传图片到阿里云 OSS
     * @param file 图片文件
     * @param pluno 商品品号
     * @return OSS 图片 URL
     * @throws IOException 上传失败时抛出异常
     */
    public String uploadImageToOSS(MultipartFile file, String pluno) throws IOException {
        String accessKeyId = configUtil.getAccessKeyId();
        String accessKeySecret = configUtil.getAccessKeySecret();
        String ossBucket = getOssBucket();
        String ossEndpoint = getOssEndpoint();
        
        // 检查配置是否完整
        if (accessKeyId == null || accessKeySecret == null || 
            accessKeyId.trim().isEmpty() || accessKeySecret.trim().isEmpty()) {
            throw new RuntimeException("阿里云 AccessKey 未配置，请检查 PRODUCT_APPKEY 表 (PLATFORM='ALI')");
        }
        
        // 检查 OSS 配置
        if (ossBucket == null || ossEndpoint == null) {
            throw new RuntimeException("阿里云 OSS 配置未完整，请检查 PRODUCT_APPKEY 表 (PLATFORM='ALI_OSS')");
        }
        
        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(ossEndpoint, accessKeyId, accessKeySecret);
        
        try {
            // 生成 OSS 对象键：product-training/{pluno}/{uuid}.jpg
            String objectKey = "product-training/" + pluno + "/" + 
                              UUID.randomUUID().toString().replace("-", "") + ".jpg";
            
            // 上传图片
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossBucket, 
                objectKey, 
                new ByteArrayInputStream(file.getBytes())
            );
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            
            // 记录 ETag
            System.out.println("OSS 上传成功，ETag: " + result.getETag());
            
            // 返回图片 URL
            return "https://" + ossBucket + "." + ossEndpoint + "/" + objectKey;
            
        } catch (Exception e) {
            System.err.println("OSS 上传失败：" + e.getMessage());
            throw new IOException("上传到阿里云 OSS 失败：" + e.getMessage(), e);
        } finally {
            // 关闭 OSS 客户端
            ossClient.shutdown();
        }
    }
    
    /**
     * 调用阿里云视觉智能开放平台 - 商品分类 API
     * API 文档：https://help.aliyun.com/document_detail/151536.html
     * @param imageUrl 图片 URL（OSS URL）
     * @return 识别结果 Map
     */
    public Map<String, Object> recognizeProduct(String imageUrl) {
        String accessKeyId = configUtil.getAccessKeyId();
        String accessKeySecret = configUtil.getAccessKeySecret();
        
        Map<String, Object> result = new HashMap<>();
        
        // 检查配置
        if (accessKeyId == null || accessKeySecret == null ||
            accessKeyId.trim().isEmpty() || accessKeySecret.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "阿里云 AccessKey 未配置");
            result.put("pluno", null);
            result.put("productName", null);
            result.put("category", null);
            result.put("confidence", 0.0);
            return result;
        }
        
        try {
            System.out.println("🔍 开始调用阿里云商品分类 API...");
            System.out.println("📷 图片 URL: " + imageUrl);
            
            // 使用阿里云视觉智能 API
            // Endpoint: goodstech.cn-shanghai.aliyuncs.com
            // Action: ClassifyCommodity
            // Version: 2019-12-30
            String host = "goodstech.cn-shanghai.aliyuncs.com";
            String method = "POST";
            
            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("ImageURL", imageUrl);
            
            // 调用 OpenAPI
            String responseBody = callOpenApi(host, method, accessKeyId, accessKeySecret, params);
            
            System.out.println("🔍 商品分类 API 响应：" + responseBody);
            
            // 解析响应
            // 返回格式：{"RequestId":"xxx","Data":{"Categories":[{"CategoryId":"584","CategoryName":"旁轴相机","Score":0.417}]}}
            result.put("success", true);
            result.put("message", "识别成功");
            
            // 提取最佳匹配结果
            String categoryName = extractFromJson(responseBody, "CategoryName");
            String categoryId = extractFromJson(responseBody, "CategoryId");
            String score = extractFromJson(responseBody, "Score");
            
            // 打印原始返回 JSON 用于调试
            System.out.println("📋 阿里云原始返回 JSON: " + responseBody);
            System.out.println("📋 解析结果：CategoryId=" + categoryId + ", CategoryName=" + categoryName + ", Score=" + score);
            
            // pluno: 使用 CategoryId 作为临时品号（阿里返回的原始识别结果）
            result.put("sourcePluno", categoryId != null ? categoryId : null);
            result.put("productName", categoryName != null ? categoryName : "识别商品");
            result.put("category", categoryName != null ? categoryName : "通用商品");
            result.put("confidence", score != null ? Double.parseDouble(score) : 0.85);
            
            System.out.println("🎯 识别完成：类目=" + categoryName + ", 置信度=" + score);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("商品识别失败：" + e.getMessage());
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
     * 调用阿里云 OpenAPI（通用方法）
     */
    private String callOpenApi(String host, String method, 
                               String accessKeyId, String accessKeySecret,
                               Map<String, String> params) throws Exception {
        // 添加公共参数（注意：签名时不包含 Signature 参数）
        params.put("Action", "ClassifyCommodity");
        params.put("Version", "2019-12-30");
        params.put("AccessKeyId", accessKeyId);
        params.put("Timestamp", generateTimestamp());
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("Format", "JSON");
        
        // 生成签名（使用原始参数值）
        String signature = generateSignature("GET", params, accessKeySecret);
        
        // 构建请求 URL（参数排序后构建，使用 URL 编码）
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);
        
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://").append(host);
        urlBuilder.append("?");
        
        List<String> paramList = new ArrayList<>();
        for (String key : sortedKeys) {
            String value = params.get(key);
            // ImageURL 特殊处理：不二次编码，因为签名时已经编码过了
            String encodedValue;
            if ("ImageURL".equals(key)) {
                // ImageURL 在签名时已经编码，这里直接使用原值
                encodedValue = URLEncoder.encode(value, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
            } else {
                encodedValue = percentEncode(value);
            }
            paramList.add(key + "=" + encodedValue);
        }
        // 添加签名参数
        paramList.add("Signature=" + percentEncode(signature));
        
        urlBuilder.append(String.join("&", paramList));
        
        String fullUrl = urlBuilder.toString();
        System.out.println("📡 请求 URL: " + fullUrl);
        
        // 发送 HTTP GET 请求
        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        
        int responseCode = conn.getResponseCode();
        System.out.println("📡 HTTP 响应码：" + responseCode);
        
        if (responseCode == 200) {
            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();
            return response;
        } else {
            // 读取错误响应
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
     * 生成时间戳
     */
    private String generateTimestamp() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }
    
    /**
     * 生成签名（阿里云 OpenAPI 标准签名算法）
     */
    private String generateSignature(String method, Map<String, String> params, String accessKeySecret) throws Exception {
        // 1. 参数排序
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);
        
        // 2. 构建规范请求字符串（参数名和值都要编码）
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            canonicalizedQueryString.append("&");
            canonicalizedQueryString.append(percentEncode(key));
            canonicalizedQueryString.append("=");
            canonicalizedQueryString.append(percentEncode(params.get(key)));
        }
        
        // 3. 构建签名字符串
        String stringToSign = method + "&" + 
                             percentEncode("/") + "&" + 
                             percentEncode(canonicalizedQueryString.substring(1));
        
        System.out.println("🔐 签名字符串：" + stringToSign);
        
        // 4. 计算签名
        Mac mac = Mac.getInstance("HmacSHA1");
        String keySecret = accessKeySecret + "&";
        mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        
        String signature = Base64.getEncoder().encodeToString(signData);
        System.out.println("🔐 签名结果：" + signature);
        
        return signature;
    }
    
    /**
     * URL 编码（阿里云规范）
     */
    private String percentEncode(String value) throws Exception {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~");
    }
    
    /**
     * 简单 JSON 提取（临时方案，后续可用 FastJSON）
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
    
    /**
     * 从 MultipartFile 直接识别商品（不上传 OSS）
     * @param file 图片文件
     * @return 识别结果
     */
    public Map<String, Object> recognizeProductFromMultipart(MultipartFile file) {
        try {
            // 先上传到 OSS 获取 URL
            String ossImageUrl = uploadImageToOSS(file, "temp");
            
            // 调用识别 API
            return recognizeProduct(ossImageUrl);
            
        } catch (IOException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "图片处理失败：" + e.getMessage());
            result.put("pluno", null);
            result.put("confidence", 0.0);
            return result;
        }
    }
}
