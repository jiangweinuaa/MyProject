-- ============================================================
-- 智问日志表添加 CREATED_BY 字段
-- 创建时间：2026-04-15
-- 用途：记录用户编号（OPNO）
-- ============================================================

-- 1. 添加 CREATED_BY 字段
ALTER TABLE AI_NL_QUERY_LOG ADD (
    CREATED_BY VARCHAR2(100)
);

-- 2. 添加注释
COMMENT ON COLUMN AI_NL_QUERY_LOG.CREATED_BY IS '创建人（用户编号 OPNO）';

-- 3. 验证字段是否添加成功
SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, COMMENTS 
FROM USER_TAB_COLUMNS c
LEFT JOIN USER_COL_COMMENTS cm ON c.TABLE_NAME = cm.TABLE_NAME AND c.COLUMN_NAME = cm.COLUMN_NAME
WHERE c.TABLE_NAME = 'AI_NL_QUERY_LOG'
ORDER BY c.COLUMN_ID;
