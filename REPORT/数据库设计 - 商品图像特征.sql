-- =====================================================
-- 商品识别优化 - 数据库表结构设计
-- 创建时间：2026-04-12
-- =====================================================

-- =====================================================
-- 1. 商品图像特征表
-- =====================================================

-- 创建表
CREATE TABLE PRODUCT_IMAGE_FEATURES (
    FEATURE_ID VARCHAR2(36) PRIMARY KEY,     -- 特征 ID（UUID）
    PLUNO VARCHAR2(50) NOT NULL,             -- 商品品号
    IMAGE_URL VARCHAR2(500),                 -- 图片 URL
    OSS_IMAGE_URL VARCHAR2(1000),            -- 阿里云 OSS 图片 URL
    FEATURE_VECTOR BLOB,                     -- 特征向量（二进制存储）
    FEATURE_DIMENSION NUMBER DEFAULT 512,    -- 向量维度（512/1024）
    MODEL_VERSION VARCHAR2(50),              -- 模型版本
    CREATED_TIME DATE DEFAULT SYSDATE,       -- 创建时间
    CREATED_BY VARCHAR2(50),                 -- 创建人
    REMARK VARCHAR2(500)                     -- 备注
);

-- 添加表注释
COMMENT ON TABLE PRODUCT_IMAGE_FEATURES IS '商品图像特征表';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.FEATURE_ID IS '特征 ID（UUID）';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.PLUNO IS '商品品号';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.IMAGE_URL IS '图片 URL';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.OSS_IMAGE_URL IS '阿里云 OSS 图片 URL';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.FEATURE_VECTOR IS '特征向量（二进制存储）';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.FEATURE_DIMENSION IS '向量维度（512/1024）';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.MODEL_VERSION IS '模型版本';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.CREATED_TIME IS '创建时间';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.CREATED_BY IS '创建人';
COMMENT ON COLUMN PRODUCT_IMAGE_FEATURES.REMARK IS '备注';

-- 创建索引
CREATE INDEX IDX_PLUNO ON PRODUCT_IMAGE_FEATURES(PLUNO);
CREATE INDEX IDX_CREATED_TIME ON PRODUCT_IMAGE_FEATURES(CREATED_TIME);
CREATE INDEX IDX_MODEL_VERSION ON PRODUCT_IMAGE_FEATURES(MODEL_VERSION);

-- =====================================================
-- 2. 修改现有表 - PRODUCT_TRAINING_SAMPLES
-- =====================================================

-- 添加特征提取相关字段
ALTER TABLE PRODUCT_TRAINING_SAMPLES ADD (
    FEATURE_EXTRACTED VARCHAR2(1) DEFAULT 'N',  -- 是否已提取特征 Y/N
    FEATURE_ID VARCHAR2(36),                    -- 关联的特征 ID
    FEATURE_EXTRACT_TIME DATE                   -- 特征提取时间
);

-- 添加字段注释
COMMENT ON COLUMN PRODUCT_TRAINING_SAMPLES.FEATURE_EXTRACTED IS '是否已提取特征 Y/N';
COMMENT ON COLUMN PRODUCT_TRAINING_SAMPLES.FEATURE_ID IS '关联的特征 ID';
COMMENT ON COLUMN PRODUCT_TRAINING_SAMPLES.FEATURE_EXTRACT_TIME IS '特征提取时间';

-- =====================================================
-- 3. 修改现有表 - PRODUCT_FEATURES
-- =====================================================

-- 添加图像特征相关字段
ALTER TABLE PRODUCT_FEATURES ADD (
    HAS_IMAGE_FEATURES VARCHAR2(1) DEFAULT 'N',  -- 是否有图像特征 Y/N
    IMAGE_FEATURE_COUNT NUMBER DEFAULT 0,        -- 图像特征数量
    LAST_FEATURE_UPDATE DATE                     -- 最后特征更新时间
);

-- 添加字段注释
COMMENT ON COLUMN PRODUCT_FEATURES.HAS_IMAGE_FEATURES IS '是否有图像特征 Y/N';
COMMENT ON COLUMN PRODUCT_FEATURES.IMAGE_FEATURE_COUNT IS '图像特征数量';
COMMENT ON COLUMN PRODUCT_FEATURES.LAST_FEATURE_UPDATE IS '最后特征更新时间';

-- =====================================================
-- 4. 创建视图 - 商品特征统计
-- =====================================================

CREATE OR REPLACE VIEW V_PRODUCT_FEATURE_STATS AS
SELECT 
    pf.PLUNO,
    pf.PRODUCT_NAME,
    pf.CATEGORY,
    COUNT(pif.FEATURE_ID) AS FEATURE_COUNT,
    MAX(pif.CREATED_TIME) AS LAST_UPDATE_TIME,
    pf.HAS_IMAGE_FEATURES,
    pf.IMAGE_FEATURE_COUNT
FROM PRODUCT_FEATURES pf
LEFT JOIN PRODUCT_IMAGE_FEATURES pif ON pf.PLUNO = pif.PLUNO
GROUP BY 
    pf.PLUNO, 
    pf.PRODUCT_NAME, 
    pf.CATEGORY,
    pf.HAS_IMAGE_FEATURES,
    pf.IMAGE_FEATURE_COUNT;

-- 添加视图注释
COMMENT ON TABLE V_PRODUCT_FEATURE_STATS IS '商品特征统计视图';

-- =====================================================
-- 5. 初始化数据
-- =====================================================

-- 初始化 PRODUCT_FEATURES 表的图像特征字段
UPDATE PRODUCT_FEATURES 
SET 
    HAS_IMAGE_FEATURES = 'N',
    IMAGE_FEATURE_COUNT = 0,
    LAST_FEATURE_UPDATE = NULL
WHERE 
    HAS_IMAGE_FEATURES IS NULL;

COMMIT;

-- =====================================================
-- 6. 查询示例
-- =====================================================

-- 查询某个商品的所有特征
-- SELECT * FROM PRODUCT_IMAGE_FEATURES WHERE PLUNO = '001';

-- 查询所有已提取特征的商品
-- SELECT * FROM V_PRODUCT_FEATURE_STATS WHERE HAS_IMAGE_FEATURES = 'Y';

-- 查询特征数量统计
-- SELECT 
--     COUNT(*) AS TOTAL_PRODUCTS,
--     SUM(CASE WHEN HAS_IMAGE_FEATURES = 'Y' THEN 1 ELSE 0 END) AS WITH_FEATURES,
--     SUM(CASE WHEN HAS_IMAGE_FEATURES = 'N' THEN 1 ELSE 0 END) AS WITHOUT_FEATURES
-- FROM PRODUCT_FEATURES;

-- =====================================================
-- 7. 性能优化建议
-- =====================================================

-- 1. FEATURE_VECTOR 是 BLOB 类型，查询时避免 SELECT *
-- 2. 定期清理无用特征数据
-- 3. 当特征数据量大时，考虑分区表
-- 4. 考虑使用专门的向量数据库（如 Milvus）存储特征向量

-- =====================================================
-- 8. 后续扩展
-- =====================================================

-- 1. 添加特征向量索引表（用于快速检索）
-- 2. 添加特征质量评估字段
-- 3. 添加特征版本管理
-- 4. 添加特征使用统计
