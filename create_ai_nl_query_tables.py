#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
创建智问日志表结构
"""

import cx_Oracle

# 数据库连接配置
DB_CONFIG = {
    'user': 'POS',
    'password': 'pos_2020',
    'dsn': '47.99.153.144:1521/posdb'
}

# SQL 语句
CREATE_TABLES_SQL = """
-- ============================================
-- 智问对话日志表
-- ============================================
CREATE TABLE AI_NL_QUERY_LOG (
    LOG_ID              VARCHAR2(64) CONSTRAINT PK_AI_NL_QUERY_LOG PRIMARY KEY,
    SESSION_ID          VARCHAR2(64),
    USER_QUESTION       VARCHAR2(2000) NOT NULL,
    GENERATED_SQL       CLOB NOT NULL,
    IS_RETRY            CHAR(1) DEFAULT 'N' CONSTRAINT CK_AI_NL_RETRY CHECK (IS_RETRY IN ('Y', 'N')),
    ORIGINAL_SQL        CLOB,
    FINAL_SQL           CLOB NOT NULL,
    EXEC_STATUS         VARCHAR2(20) NOT NULL,
    ERROR_MESSAGE       VARCHAR2(2000),
    EXEC_TIME_MS        NUMBER,
    RESPONSE_TIME_MS    NUMBER,
    MODEL_NAME          VARCHAR2(100) DEFAULT 'qwen-plus',
    CREATED_TIME        DATE DEFAULT SYSDATE NOT NULL,
    CREATED_BY          VARCHAR2(100)
)
"""

CREATE_INDEXES_SQL = """
-- 创建索引
CREATE INDEX IDX_AI_NL_QUERY_TIME ON AI_NL_QUERY_LOG(CREATED_TIME)
"""

CREATE_INDEXES_SQL2 = """
CREATE INDEX IDX_AI_NL_QUERY_STATUS ON AI_NL_QUERY_LOG(EXEC_STATUS)
"""

CREATE_INDEXES_SQL3 = """
CREATE INDEX IDX_AI_NL_QUERY_SESSION ON AI_NL_QUERY_LOG(SESSION_ID)
"""

CREATE_SESSION_TABLE_SQL = """
-- ============================================
-- 智问会话表
-- ============================================
CREATE TABLE AI_NL_QUERY_SESSION (
    SESSION_ID          VARCHAR2(64) CONSTRAINT PK_AI_NL_QUERY_SESSION PRIMARY KEY,
    USER_ID             VARCHAR2(100),
    START_TIME          DATE DEFAULT SYSDATE NOT NULL,
    END_TIME            DATE,
    QUESTION_COUNT      NUMBER DEFAULT 0,
    SUCCESS_COUNT       NUMBER DEFAULT 0,
    FAILED_COUNT        NUMBER DEFAULT 0,
    LAST_QUESTION       VARCHAR2(2000),
    STATUS              VARCHAR2(20) DEFAULT 'ACTIVE',
    CREATED_TIME        DATE DEFAULT SYSDATE NOT NULL
)
"""

CREATE_VIEW_DAILY_SQL = """
-- ============================================
-- 智问日志统计视图（按天）
-- ============================================
CREATE OR REPLACE VIEW V_AI_NL_QUERY_DAILY_STATS AS
SELECT 
    TRUNC(CREATED_TIME) AS STAT_DATE,
    COUNT(*) AS TOTAL_QUERIES,
    SUM(CASE WHEN EXEC_STATUS = 'SUCCESS' THEN 1 ELSE 0 END) AS SUCCESS_COUNT,
    SUM(CASE WHEN EXEC_STATUS = 'FAILED' THEN 1 ELSE 0 END) AS FAILED_COUNT,
    ROUND(SUM(CASE WHEN EXEC_STATUS = 'SUCCESS' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS SUCCESS_RATE,
    ROUND(AVG(EXEC_TIME_MS), 2) AS AVG_EXEC_TIME_MS,
    ROUND(AVG(RESPONSE_TIME_MS), 2) AS AVG_RESPONSE_TIME_MS,
    SUM(CASE WHEN IS_RETRY = 'Y' THEN 1 ELSE 0 END) AS RETRY_COUNT
FROM AI_NL_QUERY_LOG
GROUP BY TRUNC(CREATED_TIME)
ORDER BY STAT_DATE DESC
"""

CREATE_VIEW_QUESTION_SQL = """
-- ============================================
-- 智问日志统计视图（按问题类型）
-- ============================================
CREATE OR REPLACE VIEW V_AI_NL_QUERY_QUESTION_STATS AS
SELECT 
    CASE 
        WHEN UPPER(USER_QUESTION) LIKE '%销售额%' THEN '销售查询'
        WHEN UPPER(USER_QUESTION) LIKE '%销量%' THEN '销量查询'
        WHEN UPPER(USER_QUESTION) LIKE '%库存%' THEN '库存查询'
        WHEN UPPER(USER_QUESTION) LIKE '%品类%' THEN '品类分析'
        WHEN UPPER(USER_QUESTION) LIKE '%门店%' THEN '门店分析'
        WHEN UPPER(USER_QUESTION) LIKE '%占比%' THEN '占比分析'
        WHEN UPPER(USER_QUESTION) LIKE '%趋势%' THEN '趋势分析'
        ELSE '其他'
    END AS QUESTION_TYPE,
    COUNT(*) AS TOTAL_QUERIES,
    SUM(CASE WHEN EXEC_STATUS = 'SUCCESS' THEN 1 ELSE 0 END) AS SUCCESS_COUNT,
    ROUND(SUM(CASE WHEN EXEC_STATUS = 'SUCCESS' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS SUCCESS_RATE,
    ROUND(AVG(EXEC_TIME_MS), 2) AS AVG_EXEC_TIME_MS
FROM AI_NL_QUERY_LOG
GROUP BY 
    CASE 
        WHEN UPPER(USER_QUESTION) LIKE '%销售额%' THEN '销售查询'
        WHEN UPPER(USER_QUESTION) LIKE '%销量%' THEN '销量查询'
        WHEN UPPER(USER_QUESTION) LIKE '%库存%' THEN '库存查询'
        WHEN UPPER(USER_QUESTION) LIKE '%品类%' THEN '品类分析'
        WHEN UPPER(USER_QUESTION) LIKE '%门店%' THEN '门店分析'
        WHEN UPPER(USER_QUESTION) LIKE '%占比%' THEN '占比分析'
        WHEN UPPER(USER_QUESTION) LIKE '%趋势%' THEN '趋势分析'
        ELSE '其他'
    END
ORDER BY TOTAL_QUERIES DESC
"""

ADD_COMMENTS_SQL = """
-- 添加注释
COMMENT ON TABLE AI_NL_QUERY_LOG IS '智问对话日志表'
"""

ADD_COMMENTS_SQL2 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.LOG_ID IS '日志 ID'
"""

ADD_COMMENTS_SQL3 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.SESSION_ID IS '会话 ID（多轮对话追踪）'
"""

ADD_COMMENTS_SQL4 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.USER_QUESTION IS '用户问题'
"""

ADD_COMMENTS_SQL5 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.GENERATED_SQL IS '生成的 SQL'
"""

ADD_COMMENTS_SQL6 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.IS_RETRY IS '是否重试：Y/N'
"""

ADD_COMMENTS_SQL7 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.ORIGINAL_SQL IS '原始 SQL（重试时有值）'
"""

ADD_COMMENTS_SQL8 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.FINAL_SQL IS '最终执行的 SQL'
"""

ADD_COMMENTS_SQL9 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.EXEC_STATUS IS '执行状态：SUCCESS/FAILED'
"""

ADD_COMMENTS_SQL10 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.ERROR_MESSAGE IS '错误信息'
"""

ADD_COMMENTS_SQL11 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.EXEC_TIME_MS IS 'SQL 执行时间（毫秒）'
"""

ADD_COMMENTS_SQL12 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.RESPONSE_TIME_MS IS '总响应时间（毫秒）'
"""

ADD_COMMENTS_SQL13 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.MODEL_NAME IS '使用的模型'
"""

ADD_COMMENTS_SQL14 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.CREATED_TIME IS '创建时间'
"""

ADD_COMMENTS_SQL15 = """
COMMENT ON COLUMN AI_NL_QUERY_LOG.CREATED_BY IS '创建人（用户 ID）'
"""

ADD_COMMENTS_SQL16 = """
COMMENT ON TABLE AI_NL_QUERY_SESSION IS '智问会话表'
"""

ADD_COMMENTS_SQL17 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.SESSION_ID IS '会话 ID'
"""

ADD_COMMENTS_SQL18 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.USER_ID IS '用户 ID'
"""

ADD_COMMENTS_SQL19 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.START_TIME IS '会话开始时间'
"""

ADD_COMMENTS_SQL20 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.END_TIME IS '会话结束时间'
"""

ADD_COMMENTS_SQL21 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.QUESTION_COUNT IS '问题数量'
"""

ADD_COMMENTS_SQL22 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.SUCCESS_COUNT IS '成功数量'
"""

ADD_COMMENTS_SQL23 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.FAILED_COUNT IS '失败数量'
"""

ADD_COMMENTS_SQL24 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.LAST_QUESTION IS '最后一个问题'
"""

ADD_COMMENTS_SQL25 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.STATUS IS '状态：ACTIVE/CLOSED'
"""

ADD_COMMENTS_SQL26 = """
COMMENT ON COLUMN AI_NL_QUERY_SESSION.CREATED_TIME IS '创建时间'
"""

ADD_COMMENTS_SQL27 = """
COMMENT ON TABLE V_AI_NL_QUERY_DAILY_STATS IS '智问日志统计视图（按天）'
"""

ADD_COMMENTS_SQL28 = """
COMMENT ON TABLE V_AI_NL_QUERY_QUESTION_STATS IS '智问日志统计视图（按问题类型）'
"""

def execute_sql(cursor, sql, description):
    """执行 SQL 语句"""
    try:
        cursor.execute(sql)
        print(f"✅ {description}")
        return True
    except Exception as e:
        print(f"❌ {description}: {str(e)}")
        return False

def main():
    """主函数"""
    print("🚀 开始创建智问日志表结构...")
    print("=" * 50)
    
    try:
        # 连接数据库
        print("📡 连接数据库...")
        connection = cx_Oracle.connect(**DB_CONFIG)
        cursor = connection.cursor()
        print("✅ 数据库连接成功")
        print("=" * 50)
        
        # 执行 SQL
        sql_statements = [
            (CREATE_TABLES_SQL, "创建 AI_NL_QUERY_LOG 表"),
            (CREATE_INDEXES_SQL, "创建索引 IDX_AI_NL_QUERY_TIME"),
            (CREATE_INDEXES_SQL2, "创建索引 IDX_AI_NL_QUERY_STATUS"),
            (CREATE_INDEXES_SQL3, "创建索引 IDX_AI_NL_QUERY_SESSION"),
            (CREATE_SESSION_TABLE_SQL, "创建 AI_NL_QUERY_SESSION 表"),
            (CREATE_VIEW_DAILY_SQL, "创建视图 V_AI_NL_QUERY_DAILY_STATS"),
            (CREATE_VIEW_QUESTION_SQL, "创建视图 V_AI_NL_QUERY_QUESTION_STATS"),
            (ADD_COMMENTS_SQL, "添加表注释 AI_NL_QUERY_LOG"),
            (ADD_COMMENTS_SQL2, "添加列注释 LOG_ID"),
            (ADD_COMMENTS_SQL3, "添加列注释 SESSION_ID"),
            (ADD_COMMENTS_SQL4, "添加列注释 USER_QUESTION"),
            (ADD_COMMENTS_SQL5, "添加列注释 GENERATED_SQL"),
            (ADD_COMMENTS_SQL6, "添加列注释 IS_RETRY"),
            (ADD_COMMENTS_SQL7, "添加列注释 ORIGINAL_SQL"),
            (ADD_COMMENTS_SQL8, "添加列注释 FINAL_SQL"),
            (ADD_COMMENTS_SQL9, "添加列注释 EXEC_STATUS"),
            (ADD_COMMENTS_SQL10, "添加列注释 ERROR_MESSAGE"),
            (ADD_COMMENTS_SQL11, "添加列注释 EXEC_TIME_MS"),
            (ADD_COMMENTS_SQL12, "添加列注释 RESPONSE_TIME_MS"),
            (ADD_COMMENTS_SQL13, "添加列注释 MODEL_NAME"),
            (ADD_COMMENTS_SQL14, "添加列注释 CREATED_TIME"),
            (ADD_COMMENTS_SQL15, "添加列注释 CREATED_BY"),
            (ADD_COMMENTS_SQL16, "添加表注释 AI_NL_QUERY_SESSION"),
            (ADD_COMMENTS_SQL17, "添加列注释 SESSION_ID"),
            (ADD_COMMENTS_SQL18, "添加列注释 USER_ID"),
            (ADD_COMMENTS_SQL19, "添加列注释 START_TIME"),
            (ADD_COMMENTS_SQL20, "添加列注释 END_TIME"),
            (ADD_COMMENTS_SQL21, "添加列注释 QUESTION_COUNT"),
            (ADD_COMMENTS_SQL22, "添加列注释 SUCCESS_COUNT"),
            (ADD_COMMENTS_SQL23, "添加列注释 FAILED_COUNT"),
            (ADD_COMMENTS_SQL24, "添加列注释 LAST_QUESTION"),
            (ADD_COMMENTS_SQL25, "添加列注释 STATUS"),
            (ADD_COMMENTS_SQL26, "添加列注释 CREATED_TIME"),
            (ADD_COMMENTS_SQL27, "添加视图注释 V_AI_NL_QUERY_DAILY_STATS"),
            (ADD_COMMENTS_SQL28, "添加视图注释 V_AI_NL_QUERY_QUESTION_STATS"),
        ]
        
        success_count = 0
        for sql, desc in sql_statements:
            if execute_sql(cursor, sql, desc):
                success_count += 1
        
        # 提交事务
        print("=" * 50)
        print("💾 提交事务...")
        connection.commit()
        print("✅ 事务提交成功")
        
        # 验证
        print("=" * 50)
        print("🔍 验证表结构...")
        cursor.execute("""
            SELECT 'AI_NL_QUERY_LOG' AS TABLE_NAME, COUNT(*) AS ROW_COUNT FROM AI_NL_QUERY_LOG
            UNION ALL
            SELECT 'AI_NL_QUERY_SESSION', COUNT(*) FROM AI_NL_QUERY_SESSION
        """)
        rows = cursor.fetchall()
        for row in rows:
            print(f"   {row[0]}: {row[1]} 行")
        
        print("=" * 50)
        print(f"🎉 完成！成功执行 {success_count}/{len(sql_statements)} 个语句")
        
        # 关闭连接
        cursor.close()
        connection.close()
        
    except Exception as e:
        print(f"❌ 错误：{str(e)}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())
