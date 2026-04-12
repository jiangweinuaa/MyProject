package com.dsc.spos.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * 类注解：对应数据库的表
 * @author Administrator
 *
 */
@Target(ElementType.TYPE)//表示类  
@Retention(RetentionPolicy.RUNTIME)//表示程序运行时使用
public @interface ORMTable 
{
	/**
	 * 数据库表名称
	 */
	String TableName() default "";	
	
	/**
	 * 是数据库实体表
	 */
	String IsDatabaseTable() default "Y";    	
	
    
	/**
	 * 存在于暂存库Middle还是在门店总部库Shop中(两边都有不用填)
	 */
    String InDatabase() default "";  
    
    
    /**
	 * 数据库表名称简写：(用于表明过长无法创建主键,因为主键名为PK_表名称)
	 */
	String TableShortName() default "";	
	
	/**
	 * 唯一索引
	 * @return
	 */
	String UniqueIndex() default "";	
	
	
    
}

