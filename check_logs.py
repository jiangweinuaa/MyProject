#!/usr/bin/env python3
import cx_Oracle

try:
    conn = cx_Oracle.connect('POS', 'pos_2020', '47.99.153.144:1521/posdb')
    cursor = conn.cursor()
    
    # 检查日志表
    cursor.execute("SELECT COUNT(*) FROM AI_NL_QUERY_LOG")
    log_count = cursor.fetchone()[0]
    
    # 检查会话表
    cursor.execute("SELECT COUNT(*) FROM AI_NL_QUERY_SESSION")
    session_count = cursor.fetchone()[0]
    
    print(f"AI_NL_QUERY_LOG: {log_count} 行")
    print(f"AI_NL_QUERY_SESSION: {session_count} 行")
    
    # 显示最近的日志
    if log_count > 0:
        print("\n最近的日志：")
        cursor.execute("""
            SELECT USER_QUESTION, EXEC_STATUS, TO_CHAR(CREATED_TIME, 'YYYY-MM-DD HH24:MI:SS') AS CREATED_TIME
            FROM AI_NL_QUERY_LOG
            ORDER BY CREATED_TIME DESC
            WHERE ROWNUM <= 5
        """)
        for row in cursor.fetchall():
            print(f"  {row[2]} - {row[0][:50]}... - {row[1]}")
    
    cursor.close()
    conn.close()
    
except Exception as e:
    print(f"错误：{e}")
