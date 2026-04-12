package com.dsc.spos.dao.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.type.StandardBasicTypes;

/**
 * 1.修正 hibernate 不支援  sql server 的欄位型態
 * 2.將所有的欄位型態視為 String 來處理
 * @author Xavier
 *
 */
public class DsmSQLServerDialect extends SQLServerDialect {

	public DsmSQLServerDialect() {
		super();
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName());
	} 
}
