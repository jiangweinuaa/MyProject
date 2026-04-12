package com.dsc.spos.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)//表示属性字段
@Retention(RetentionPolicy.RUNTIME)//表示程序运行时使用
public @interface ORMField 
{	
	/**
	 * 字段名称
	 */
    String ColumnName() default "";  
      
    /**
	 * 字段类型
	 */
    String ColumnType();  
    
    /**
	 * 字段长度
	 */
    int ColumnLength() default 0;
    
    /**
     * 小数位数
     */
    int ColumnScale() default 0;
    
    /**
	 * 字段默认值
	 */
    String ColumnDefaultValue() default "";
    
    /**
	 * 字段是否可为空
	 */
    String ColumnNullable() default "Y"; 
    
    /**
     * 是否主键
     */
    String  ColumnPrimarykey() default "N"; 
    
    
    /**
     * 非数据库字段
     */
    String  NotMapped() default "N"; 
    
    
    /**
     * 对应的非聚集索引名称:表名称_IDX_01(索引名称1,索引名称2,索引名称3)
     * @return
     */
    String  MultiColumnIndexName() default ""; 
    
    
	/**
	 * 存在于暂存库Middle还是在门店总部库Shop中(两边都有不用填)
	 */
    String InDatabase() default "";  
    
	/**
	 * 字段备注说明
	 */
    String Comment() default "";  
    
    /**
            * 初始化数据时，此字段值不修改
     */
    String NotInitUpdateValue() default "N";  
    
}
