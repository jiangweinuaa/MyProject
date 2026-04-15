-- ============================================================
-- 智问会话历史表结构（存储完整结果集）
-- 创建时间：2026-04-15
-- 用途：存储自然语言查询的会话和对话历史（包含完整结果）
-- ============================================================

-- 1. 会话表
CREATE TABLE AI_NL_CONVERSATION (
    SESSION_ID VARCHAR2(100) PRIMARY KEY,      -- 会话 ID（主键）
    USER_ID VARCHAR2(100) DEFAULT 'default_user',  -- 用户 ID
    TITLE VARCHAR2(500),                       -- 会话标题（自动生成）
    CREATED_TIME DATE DEFAULT SYSDATE,         -- 创建时间
    UPDATED_TIME DATE DEFAULT SYSDATE,         -- 最后更新时间
    STATUS NUMBER DEFAULT 100                  -- 状态：100=活跃，0=已结束
);

-- 添加注释
COMMENT ON TABLE AI_NL_CONVERSATION IS '智问会话表';
COMMENT ON COLUMN AI_NL_CONVERSATION.SESSION_ID IS '会话 ID（主键）';
COMMENT ON COLUMN AI_NL_CONVERSATION.USER_ID IS '用户 ID';
COMMENT ON COLUMN AI_NL_CONVERSATION.TITLE IS '会话标题（自动生成）';
COMMENT ON COLUMN AI_NL_CONVERSATION.CREATED_TIME IS '创建时间';
COMMENT ON COLUMN AI_NL_CONVERSATION.UPDATED_TIME IS '最后更新时间';
COMMENT ON COLUMN AI_NL_CONVERSATION.STATUS IS '状态：100=活跃，0=已结束';

-- 2. 对话记录表（存储完整结果集）
CREATE TABLE AI_NL_DIALOGUE (
    ID VARCHAR2(100) PRIMARY KEY,              -- 对话记录 ID（主键）
    SESSION_ID VARCHAR2(100),                  -- 会话 ID（外键）
    QUESTION CLOB,                             -- 用户问题
    SQL_GENERATED CLOB,                        -- 生成的 SQL
    RESULT_DATA CLOB,                          -- 完整结果集（JSON 格式）
    ROW_COUNT NUMBER,                          -- 返回行数
    EXECUTION_TIME_MS NUMBER,                  -- 执行耗时（毫秒）
    CREATED_TIME DATE DEFAULT SYSDATE,         -- 创建时间
    CONSTRAINT FK_DIALOGUE_SESSION FOREIGN KEY (SESSION_ID) 
        REFERENCES AI_NL_CONVERSATION(SESSION_ID) ON DELETE CASCADE
);

-- 添加注释
COMMENT ON TABLE AI_NL_DIALOGUE IS '智问对话记录表（存储完整结果）';
COMMENT ON COLUMN AI_NL_DIALOGUE.ID IS '对话记录 ID（主键）';
COMMENT ON COLUMN AI_NL_DIALOGUE.SESSION_ID IS '会话 ID（外键）';
COMMENT ON COLUMN AI_NL_DIALOGUE.QUESTION IS '用户问题';
COMMENT ON COLUMN AI_NL_DIALOGUE.SQL_GENERATED IS '生成的 SQL';
COMMENT ON COLUMN AI_NL_DIALOGUE.RESULT_DATA IS '完整结果集（JSON 格式）';
COMMENT ON COLUMN AI_NL_DIALOGUE.ROW_COUNT IS '返回行数';
COMMENT ON COLUMN AI_NL_DIALOGUE.EXECUTION_TIME_MS IS '执行耗时（毫秒）';
COMMENT ON COLUMN AI_NL_DIALOGUE.CREATED_TIME IS '创建时间';

-- 3. 创建索引（优化查询性能）
-- 对话记录按会话和时间排序
CREATE INDEX IDX_AI_DIALOGUE_SESSION ON AI_NL_DIALOGUE(SESSION_ID, CREATED_TIME DESC);

-- 会话按用户和更新时间排序
CREATE INDEX IDX_AI_CONVERSATION_USER ON AI_NL_CONVERSATION(USER_ID, UPDATED_TIME DESC);

-- 4. 创建序列（用于生成 ID）
CREATE SEQUENCE SEQ_AI_DIALOGUE_ID 
    START WITH 1 
    INCREMENT BY 1 
    NOCACHE;

-- ============================================================
-- 使用示例
-- ============================================================

-- 查询用户的会话列表（分页）
-- SELECT * FROM (
--   SELECT c.*, ROWNUM rn FROM (
--     SELECT SESSION_ID, TITLE, CREATED_TIME, UPDATED_TIME,
--            (SELECT COUNT(*) FROM AI_NL_DIALOGUE d WHERE d.SESSION_ID = c.SESSION_ID) as DIALOGUE_COUNT
--     FROM AI_NL_CONVERSATION
--     WHERE USER_ID = 'default_user' AND STATUS = 100
--     ORDER BY UPDATED_TIME DESC
--   ) c WHERE ROWNUM <= 20
-- ) WHERE rn > 0;

-- 查询会话的对话历史（分页）
-- SELECT ID, QUESTION, SQL_GENERATED, RESULT_DATA, ROW_COUNT, CREATED_TIME
-- FROM (
--   SELECT d.*, ROWNUM rn FROM (
--     SELECT ID, QUESTION, SQL_GENERATED, RESULT_DATA, ROW_COUNT, CREATED_TIME
--     FROM AI_NL_DIALOGUE
--     WHERE SESSION_ID = 'test_session_001'
--     ORDER BY CREATED_TIME DESC
--   ) d WHERE ROWNUM <= 20
-- ) WHERE rn > 0;

-- ============================================================
-- 清理脚本（慎用）
-- ============================================================

-- DROP TABLE AI_NL_DIALOGUE CASCADE CONSTRAINTS;
-- DROP TABLE AI_NL_CONVERSATION CASCADE CONSTRAINTS;
-- DROP SEQUENCE SEQ_AI_DIALOGUE_ID;
-- DROP INDEX IDX_AI_DIALOGUE_SESSION;
-- DROP INDEX IDX_AI_CONVERSATION_USER;
