package com.dsc.spos.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapDistinct 
{
	/**
	 * 
	 * @param oldMap 传入的结果集
	 * @param conditionValues 主键字段过滤
	 * @return
	 */
	public static List<Map<String, Object>> getMap(List<Map<String, Object>> oldMap,Map<String, Boolean> conditionValues) 
	{ 	 	
		//去除重复后的单头信息
		List<Map<String,Object>> tmpList=new ArrayList<Map<String,Object>>();
		Set<String> keysSet = new HashSet<String>();
		try 
		{
			for (Map<String, Object> map : oldMap)
			{	
				//新主键(组合字段)
				String keys = "";
				//循环	
				for (Map.Entry<String, Object> entry1 : map.entrySet()) 
				{	 
					//传入的条件字段
					if(conditionValues.containsKey(entry1.getKey()))
					{
						//根据传入的主键字段，创建新主键
						if(conditionValues.get(entry1.getKey()).booleanValue())
						{
							keys+=entry1.getValue();
						}
					}	  			  		
				}

				int beforeSize = keysSet.size();
				keysSet.add(keys);

				int afterSize = keysSet.size();
				if(afterSize == beforeSize + 1)
				{
					tmpList.add(map);
				}	  	
			}
						
			return tmpList;
		} 
		catch (Exception e) 
		{		
			tmpList.clear();
			
			return tmpList;
		}
		finally 
		{
			keysSet.clear();
			keysSet=null;
			
			tmpList=null;
		}		
	}

	/**
	 * 
	 * @param oldMap 传入的结果集
	 * @param conditionValues 主键字段过滤
	 * @return
	 */
	public static List<Map<String, String>> getMap_String(List<Map<String, String>> oldMap,Map<String, Boolean> conditionValues) 
	{ 	 	
		//去除重复后的单头信息
		List<Map<String,String>> tmpList=new ArrayList<Map<String,String>>();
		Set<String> keysSet = new HashSet<String>();
		
		try 
		{
			for (Map<String, String> map : oldMap)
			{
				//新主键(组合字段)
				String keys = "";
				//循环	
				for (Map.Entry<String, String> entry1 : map.entrySet()) 
				{	 
					//传入的条件字段
					if(conditionValues.containsKey(entry1.getKey()))
					{
						//根据传入的主键字段，创建新主键
						if(conditionValues.get(entry1.getKey()).booleanValue())
						{
							keys+=entry1.getValue();
						}
					}		  			  		
				}

				int beforeSize = keysSet.size();
				keysSet.add(keys);

				int afterSize = keysSet.size();
				if(afterSize == beforeSize + 1)
				{
					tmpList.add(map);
				}	  	
			}	
			
			return tmpList;
		} 
		catch (Exception e) 
		{
			tmpList.clear();
			return tmpList;
		}
		finally 
		{
			keysSet.clear();
			keysSet=null;
			
			tmpList=null;			
		}

	}  


	/**
	 * 实体类根据字段过来重复
	 * @param list
	 * @param conditionValues
	 * @return
	 */
	public static <T> List<T> getListObject(List<T> list,Map<String, Boolean> conditionValues)
	{
		List<T> tmpList= new ArrayList<T>();
		Set<String> keysSet = new HashSet<String>();

		try 
		{
			if(list!= null && list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					//新主键(组合字段)
					String keys = "";

					//得到类对象  
					@SuppressWarnings("rawtypes")
					Class obj = (Class) list.get(i).getClass();
					Field[] fs = obj.getDeclaredFields();
					for(int f = 0 ; f < fs.length; f++)
					{ 					
						try 
						{
							fs[f].setAccessible(true); //设置些属性是可以访问的  
							String fieldName=fs[f].getName();
							Object fieldValue=fs[f].get(list.get(i));

							//传入的条件字段
							if(conditionValues.containsKey(fieldName))
							{
								//根据传入的主键字段，创建新主键
								if(conditionValues.get(fieldName).booleanValue())
								{
									keys+=fieldValue;
								}							 
							}						 
						} 
						catch (IllegalArgumentException e) 
						{
							
						} 
						catch (IllegalAccessException e) 
						{
							
						}
					}

					if(keys.equals(""))
					{
						continue;
					}

					int beforeSize = keysSet.size();
					keysSet.add(keys);

					int afterSize = keysSet.size();
					if(afterSize == beforeSize + 1)
					{
						tmpList.add(list.get(i));
					}	  	

				}
			}

			return tmpList;
		} 
		catch (Exception e) 
		{
			tmpList.clear();
			return tmpList;
		}
		finally 
		{
			keysSet.clear();
			keysSet=null;
			
			tmpList=null;
		}	

	}


	/**
	 * 实体类根据字段过滤结果集
	 * @param list            原始结果集
	 * @param conditionValues 字段信息
	 * @param bMultiRecord    false查询第一笔，true查询多笔
	 * @return
	 */

	public static <T> List<T> getList_WhereObject(List<T> list,Map<String, Object> conditionValues,boolean bMultiRecord)
	{
		List<T> tmpList= new ArrayList<T>();
		try 
		{
			if(list!= null && list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					//
					int countCond=conditionValues.size();
					int count=0;

					//得到类对象  
					@SuppressWarnings("rawtypes")
					Class obj = (Class) list.get(i).getClass();
					Field[] fs = obj.getDeclaredFields();
					for(int f = 0 ; f < fs.length; f++)
					{ 					
						try 
						{
							fs[f].setAccessible(true); //设置些属性是可以访问的  
							String fieldName=fs[f].getName();
							Object fieldValue=fs[f].get(list.get(i));

							//传入的条件字段
							if(conditionValues.containsKey(fieldName))
							{						 
								for (Map.Entry<String, Object> entry1 : conditionValues.entrySet()) 
								{
									if(fieldValue.equals(entry1.getValue()))
									{
										count+=1;										
									}
								}
							}						 
						} 
						catch (IllegalArgumentException e) 
						{
							
						} 
						catch (IllegalAccessException e) 
						{
							
						}
					}

					if (count==countCond) 
					{
						tmpList.add(list.get(i));
						//只取一条
						if(bMultiRecord==false)
						{
							break;
						}
					}		  

				}
			}

			return tmpList;
		} 
		catch (Exception e) 
		{
			tmpList.clear();
			return tmpList;
		}
		finally 
		{
			tmpList=null;
		}
		

	}


	/**
	 * 根据字段过滤结果集
	 * @param oldMap           原始结果集
	 * @param conditionValues  字段信息
	 * @param bMultiRecord     false查询第一笔，true查询多笔
	 * @param iContains     1包含关系 记录包含传值的条件 ，2：传值的条件包含记录，不传是等于
	 * @return
	 */
	public static List<Map<String, Object>> getWhereMap(List<Map<String, Object>> oldMap,Map<String, Object> conditionValues,boolean bMultiRecord,int... iContains) 
	{ 	 	
		//去除重复后的单头信息
		List<Map<String,Object>> tmpList=new ArrayList<Map<String,Object>>();

		try 
		{
			for (Map<String, Object> map : oldMap)
			{	
				//
				int countCond=conditionValues.size();
				int count=0;
				//循环	
				for (Map.Entry<String, Object> entry1 : conditionValues.entrySet()) 
				{	 			 
					//传入的条件字段
					if(map.containsKey(entry1.getKey()))
					{
						int iucontain=0;
						
						if (iContains.length>0) 
						{
							iucontain=iContains[0];
						}
						
						if (iucontain==1) //1:记录包含传值的条件
						{
							//根据传入的主键字段，创建新主键  只要包含即可
							if(map.get(entry1.getKey()).toString().contains(entry1.getValue().toString()))
							{
								count+=1;
							}			
						}
						else if (iucontain==2) //2：传值的条件包含记录
						{
							if(entry1.getValue().toString().contains(map.get(entry1.getKey()).toString()))
							{
								count+=1;
							}			
						}
						else 
						{
							//根据传入的主键字段，创建新主键
                            Object value1 = map.get(entry1.getKey());
                            Object value2 = entry1.getValue();

                            if (value1 instanceof Number || value2 instanceof Number){
                                BigDecimal bd1 = new BigDecimal(value1.toString());
                                BigDecimal bd2 = new BigDecimal(value2.toString());
                                if (bd1.compareTo(bd2)==0 ){ //数值型比较
                                    count+=1;
                                }
                            }else if (value1.equals(value2)){
                                count+=1;
                            }else if (value1.toString().equals(value2.toString())){
                                count+=1;
                            }
                            //20250515 modi by 11217 for 修复equals 无法判断数值型的精度问题
//							if(map.get(entry1.getKey()).toString().equals(entry1.getValue()))
//							{
//								count+=1;
//							}
						}
							  
					}			 
				}

				if (count==countCond) 
				{
					tmpList.add(map);
					//只取一条
					if(bMultiRecord==false)
					{
						break;
					}
				}		  
			}
			return tmpList;
		} 
		catch (Exception e) 
		{
			tmpList.clear();
			return tmpList;
		}
		finally 
		{
			tmpList=null;
		}
	}
	
}
