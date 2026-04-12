package com.dsc.spos.orm;

import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy; 

@Retention(RetentionPolicy.RUNTIME)
public @interface MaxLength 
{
	String value() default "0";

}



