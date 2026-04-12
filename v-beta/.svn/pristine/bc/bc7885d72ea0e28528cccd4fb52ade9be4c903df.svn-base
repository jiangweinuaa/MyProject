package com.dsc.spos.dao.dialect;

import java.sql.Types;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StandardBasicTypes;

/**
 * 1.修正 hibernate 不支援  sql server 的欄位型態
 * 2.將所有的欄位型態視為 String 來處理
 * @author Xavier
 *
 */
public class DsmOracleDialect extends Oracle10gDialect {

	public DsmOracleDialect() {
		super();
		registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());   
    registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
    registerHibernateType(Types.VARCHAR, StandardBasicTypes.STRING.getName()); 
    registerHibernateType(Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName());
    registerHibernateType(Types.DECIMAL, StandardBasicTypes.DOUBLE.getName());
	} 
}
