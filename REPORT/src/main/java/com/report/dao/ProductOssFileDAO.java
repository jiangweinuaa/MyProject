package com.report.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * OSS 文件去重 DAO
 */
@Repository
public class ProductOssFileDAO {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 根据文件 Hash 查询已存在的 OSS URL
     * @param fileHash 文件 MD5 Hash 值
     * @return OSS URL，不存在返回 null
     */
    public String findExistingUrl(String fileHash) {
        if (jdbcTemplate == null || fileHash == null) {
            return null;
        }
        
        try {
            String sql = "SELECT OSS_URL FROM PRODUCT_OSS_FILES WHERE FILE_HASH = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, fileHash);
            
            if (list != null && !list.isEmpty()) {
                return (String) list.get(0).get("OSS_URL");
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ 查询 OSS 文件失败：" + e.getMessage());
            return null;
        }
    }
    
    /**
     * 保存文件记录到去重表
     * @param fileHash 文件 Hash 值
     * @param ossUrl OSS 图片 URL
     * @param fileName 原始文件名
     * @param fileSize 文件大小
     * @param pluno 商品品号
     * @return 文件 ID
     */
    public String saveFileRecord(String fileHash, String ossUrl, String fileName, 
                                  long fileSize, String pluno) {
        if (jdbcTemplate == null) {
            return null;
        }
        
        try {
            String fileId = java.util.UUID.randomUUID().toString().replace("-", "");
            
            String sql = "INSERT INTO PRODUCT_OSS_FILES (" +
                        "FILE_ID, FILE_HASH, OSS_URL, FILE_NAME, FILE_SIZE, " +
                        "UPLOAD_TIME, UPLOAD_BY, PLUNO) " +
                        "VALUES (?, ?, ?, ?, ?, SYSDATE, 'system', ?)";
            
            jdbcTemplate.update(sql, 
                fileId,
                fileHash,
                ossUrl,
                fileName,
                fileSize,
                pluno
            );
            
            System.out.println("✅ OSS 文件记录已保存：" + fileId);
            return fileId;
            
        } catch (Exception e) {
            System.err.println("❌ 保存 OSS 文件记录失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 查询文件记录
     * @param fileHash 文件 Hash 值
     * @return 文件记录
     */
    public Map<String, Object> findFileRecord(String fileHash) {
        if (jdbcTemplate == null || fileHash == null) {
            return null;
        }
        
        try {
            String sql = "SELECT * FROM PRODUCT_OSS_FILES WHERE FILE_HASH = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, fileHash);
            
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ 查询文件记录失败：" + e.getMessage());
            return null;
        }
    }
}
