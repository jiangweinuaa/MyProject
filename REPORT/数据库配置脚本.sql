-- =====================================================
-- 阿里云商品识别集成 - 数据库配置脚本
-- =====================================================

-- 1. 修改 PRODUCT_TRAINING_SAMPLES 表，添加 OSS 图片 URL 字段
-- =====================================================

-- 添加 OSS_IMAGE_URL 字段
ALTER TABLE PRODUCT_TRAINING_SAMPLES ADD (
    OSS_IMAGE_URL VARCHAR2(1000)
);

-- 添加字段注释
COMMENT ON COLUMN PRODUCT_TRAINING_SAMPLES.OSS_IMAGE_URL IS '阿里云 OSS 图片 URL';

-- =====================================================
-- 2. 配置阿里云 AccessKey（商品识别 API 用）
-- =====================================================

-- 查询现有配置
-- SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI';

-- 插入或更新商品识别 API 配置
-- ACCESSKEYID: 阿里云 AccessKey ID
-- ACCESSKEYSECRET: 阿里云 AccessKey Secret
MERGE INTO PRODUCT_APPKEY T
USING DUAL
ON (T.PLATFORM = 'ALI')
WHEN MATCHED THEN
  UPDATE SET T.ACCESSKEYID = '你的 AccessKey ID',
             T.ACCESSKEYSECRET = '你的 AccessKey Secret'
WHEN NOT MATCHED THEN
  INSERT (ACCESSKEYID, ACCESSKEYSECRET, PLATFORM)
  VALUES ('你的 AccessKey ID', '你的 AccessKey Secret', 'ALI');

-- =====================================================
-- 3. 配置阿里云 OSS（图片存储用）
-- =====================================================

-- 查询现有配置
-- SELECT * FROM PRODUCT_APPKEY WHERE PLATFORM = 'ALI_OSS';

-- 插入或更新 OSS 配置
-- ACCESSKEYID: OSS Bucket 名称
-- ACCESSKEYSECRET: OSS Endpoint
MERGE INTO PRODUCT_APPKEY T
USING DUAL
ON (T.PLATFORM = 'ALI_OSS')
WHEN MATCHED THEN
  UPDATE SET T.ACCESSKEYID = 'product-training-images',
             T.ACCESSKEYSECRET = 'oss-cn-shanghai.aliyuncs.com'
WHEN NOT MATCHED THEN
  INSERT (ACCESSKEYID, ACCESSKEYSECRET, PLATFORM)
  VALUES ('product-training-images', 'oss-cn-shanghai.aliyuncs.com', 'ALI_OSS');

-- =====================================================
-- 4. 验证配置
-- =====================================================

-- 查看所有阿里云相关配置
SELECT PLATFORM, ACCESSKEYID, ACCESSKEYSECRET 
FROM PRODUCT_APPKEY 
WHERE PLATFORM IN ('ALI', 'ALI_OSS');

-- =====================================================
-- 注意事项：
-- 1. 请将上述 SQL 中的占位符替换为实际的配置值
-- 2. 如果 PRODUCT_APPKEY 表不存在，需要先创建表
-- 3. 确保执行用户有表结构修改权限
-- =====================================================
