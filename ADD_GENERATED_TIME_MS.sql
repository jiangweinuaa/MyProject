-- ============================================
-- 添加 SQL 生成时间字段
-- ============================================

-- 添加 GENERATED_TIME_MS 字段
ALTER TABLE AI_NL_QUERY_LOG ADD (
    GENERATED_TIME_MS NUMBER
);

-- 添加注释
COMMENT ON COLUMN AI_NL_QUERY_LOG.GENERATED_TIME_MS IS 'SQL 生成时间（毫秒）';

-- 提交
COMMIT;

-- 验证
DESC AI_NL_QUERY_LOG;
