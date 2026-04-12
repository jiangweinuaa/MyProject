package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_PageFunUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PageFunUpdateReq.level2ElmData;
import com.dsc.spos.json.cust.req.DCP_PageFunUpdateReq.level2ElmShop;
import com.dsc.spos.json.cust.req.DCP_PageFunUpdateReq.level3Elm;
import com.dsc.spos.json.cust.req.DCP_PageUpdateReq.level2ElmPage;
import com.dsc.spos.json.cust.res.DCP_PageFunUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PageFunUpdate extends SPosAdvanceService<DCP_PageFunUpdateReq,DCP_PageFunUpdateRes>
{
	
	@Override
	protected void processDUID(DCP_PageFunUpdateReq req, DCP_PageFunUpdateRes res) throws Exception 
	{	
		List<level2ElmData> datas=req.getDatas();		
	  List<level2ElmShop> shops=req.getShops();
	  
	  if (req.getType().equals("3")) //复制
	  {
	  	for (level2ElmShop myshop : shops) 
	  	{
	  		for (level2ElmData Page : datas) 
	  		{
	  		  //先删除原来的	  			
	  			DelBean db2 = new DelBean("DCP_PAGE_MENU_FUNC");
					db2.addCondition("EID", new DataValue(req.geteId().toUpperCase(), Types.VARCHAR));
					db2.addCondition("SHOPID", new DataValue(myshop.getShopId(), Types.VARCHAR));
					db2.addCondition("PAGE_MENU_ID", new DataValue(" ( select PAGE_MENU_ID  from TA_PAGE_MENU where EID='"+req.geteId()+"' and SHOPID='"+myshop.getShopId()+"' and PAGE_ID in ( select page_id  from ta_page  where EID='"+req.geteId()+"' and SHOPID='"+myshop.getShopId()+"' and PAGEINDEX="+Page.getPageIndex()+" ) )", Types.VARCHAR,DataExpression.IN));
					this.addProcessData(new DataProcessBean(db2));

					DelBean db1 = new DelBean("DCP_PAGE_MENU");
					db1.addCondition("EID", new DataValue(req.geteId().toUpperCase(), Types.VARCHAR));
					db1.addCondition("SHOPID", new DataValue(myshop.getShopId(), Types.VARCHAR));
					db1.addCondition("PAGE_ID", new DataValue(" ( select page_id from ta_page  where EID='"+req.geteId()+"' and SHOPID='"+myshop.getShopId()+"' and PAGEINDEX="+Page.getPageIndex()+" )", Types.VARCHAR,DataExpression.IN));
					this.addProcessData(new DataProcessBean(db1));
					
										
	  			int insColCt = 0;
					String[] columnsNOTICE_FILE ={"EID","SHOPID","PAGE_MENU_ID","PAGEINDEX","X","Y","HIGH","WIDTH","BG_COLOR","FNT_NAME","FNT_SIZE","FNT_COLOR","FNT_STYLE","BUTTONNAME","STATUS","PAGE_ID"};
					DataValue[] columnsVal = new DataValue[columnsNOTICE_FILE.length];
					
					String pageMenuID=PosPub.getGUID(false).toUpperCase();
					
					for (int i = 0; i < columnsVal.length; i++)
					{
						String keyVal = null;
						switch (i) 
						{
						case 0:
							keyVal=req.geteId();
							break;
						case 1:
							keyVal=myshop.getShopId();
							break;
						case 2:
							keyVal=pageMenuID;
							break;
						case 3:
							keyVal=Page.getPageIndex();
							break;
						case 4:
							keyVal=Page.getX();
							break;
						case 5:
							keyVal=Page.getY();
							break;
						case 6:
							keyVal=Page.getHigh();
							break;
						case 7:
							keyVal=Page.getWidth();
							break;
						case 8:
							keyVal=Page.getBgColor();
							break;
						case 9:
							keyVal=Page.getFntName();
							break;
						case 10:
							if(PosPub.isNumeric(Page.getFntSize())==false)
							{									
								keyVal="0";
							}
							else 
							{
								keyVal=Page.getFntSize();
							}								
							break;
						case 11:
							if(PosPub.isNumeric(Page.getFntColor())==false)
							{
								keyVal="0";
							}
							else 
							{
								keyVal=Page.getFntColor();				
							}								
							break;
						case 12:
							keyVal=Page.getFntStyle();
							break;
						case 13:
							keyVal=Page.getButtonName();
							break;
						case 14:
							keyVal="100";
							break;
						case 15:
							keyVal=Page.getPageID();
							break;
							
						default:

							break;

						}
						
						if (keyVal != null) 
						{
							insColCt++;
							if (i == 3|| i == 4 || i == 5|| i == 6|| i == 7|| i == 8|| i == 10|| i == 11) 
							{
								columnsVal[i] = new DataValue(keyVal, Types.DECIMAL);
							} 
							else
							{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);									
							}								
						} 
						else 
						{
							columnsVal[i] = null;
						}
						
					}
					
					String[] columns2 = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];

					insColCt = 0;

					for (int i = 0; i < columnsVal.length; i++) 
					{
						if (columnsVal[i] != null) 
						{
							columns2[insColCt] = columnsNOTICE_FILE[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt++;
							if (insColCt >= insValue2.length)
								break;
						}
					}

					InsBean ib2 = new InsBean("DCP_PAGE_MENU", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));	
					
					List<level3Elm> datas3=Page.getDatas();
	  			for (level3Elm level3Elm : datas3)
	  			{
	  				int insColCtX = 0;
						String[] columnsNOTICE_FILEX ={"EID","SHOPID","PAGE_MENU_FUNC_ID","PAGEINDEX","FUNCNO","STATUS","PAGE_MENU_ID"};
						DataValue[] columnsValX = new DataValue[columnsNOTICE_FILEX.length];
						for (int i = 0; i < columnsValX.length; i++)
						{
							String keyVal = null;
							switch (i) 
							{
							case 0:
								keyVal=req.geteId();
								break;
							case 1:
								keyVal=myshop.getShopId();
								break;
							case 2:
								keyVal=PosPub.getGUID(false).toUpperCase();
								break;
							case 3:
								keyVal=Page.getPageIndex();
								break;
							case 4:
								keyVal=level3Elm.getFuncNO();
								break;
							case 5:
								keyVal="100";
								break;
							case 6:
								keyVal=pageMenuID;
								break;
								
							default:

								break;

							}
							if (keyVal != null) 
							{
								insColCtX++;
								if (i == 3 ) 
								{
									columnsValX[i] = new DataValue(keyVal, Types.DECIMAL);
								} 
								else
								{
									columnsValX[i] = new DataValue(keyVal, Types.VARCHAR);									
								}								
							} 
							else 
							{
								columnsValX[i] = null;
							}
							
						}
						String[] columns2X = new String[insColCtX];
						DataValue[] insValue2X = new DataValue[insColCtX];

						insColCtX = 0;

						for (int i = 0; i < columnsValX.length; i++) 
						{
							if (columnsValX[i] != null) 
							{
								columns2X[insColCtX] = columnsNOTICE_FILEX[i];
								insValue2X[insColCtX] = columnsValX[i];
								insColCtX++;
								if (insColCtX >= insValue2X.length)
									break;
							}
						}

						InsBean ib2X = new InsBean("DCP_PAGE_MENU_FUNC", columns2X);
						ib2X.addValues(insValue2X);
						this.addProcessData(new DataProcessBean(ib2X));		

	  			}
	  			
	  		}

	  	}
	  	
	  	this.doExecuteDataToDB();
			if (res.isSuccess()) 			
			{			
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}	
	  	
	  }
	  else if (req.getType().equals("2")) //更新
	  {
	  	List<DataProcessBean> lstIns=new ArrayList<DataProcessBean>();
	  	
	  	for (level2ElmShop myshop : shops) 
	  	{
	  		for (level2ElmData Page : datas) 
	  		{
	  		  
	  		  //先删除原来的
					DelBean db2 = new DelBean("DCP_PAGE_MENU_FUNC");
					db2.addCondition("EID", new DataValue(req.geteId().toUpperCase(), Types.VARCHAR));
					db2.addCondition("SHOPID", new DataValue(myshop.getShopId(), Types.VARCHAR));
					db2.addCondition("PAGE_MENU_ID", new DataValue(" ( select PAGE_MENU_ID  from TA_PAGE_MENU where EID='"+req.geteId()+"' and SHOPID='"+myshop.getShopId()+"' and PAGE_ID in ( select page_id  from ta_page  where EID='"+req.geteId()+"' and SHOPID='"+myshop.getShopId()+"' and PAGEINDEX="+Page.getPageIndex()+" ) )", Types.VARCHAR,DataExpression.IN));
					this.addProcessData(new DataProcessBean(db2));
					
				
					DelBean db1 = new DelBean("DCP_PAGE_MENU");
					db1.addCondition("EID", new DataValue(req.geteId().toUpperCase(), Types.VARCHAR));
					db1.addCondition("SHOPID", new DataValue(myshop.getShopId(), Types.VARCHAR));
					db1.addCondition("PAGE_ID", new DataValue(" ( select page_id from ta_page  where EID='"+req.geteId()+"' and SHOPID='"+myshop.getShopId()+"' and PAGEINDEX="+Page.getPageIndex()+" )", Types.VARCHAR,DataExpression.IN));
					this.addProcessData(new DataProcessBean(db1));
					
	  			int insColCt = 0;
					String[] columnsNOTICE_FILE ={"EID","SHOPID","PAGE_MENU_ID","PAGEINDEX","X","Y","HIGH","WIDTH","BG_COLOR","FNT_NAME","FNT_SIZE","FNT_COLOR","FNT_STYLE","BUTTONNAME","STATUS","PAGE_ID"};
					DataValue[] columnsVal = new DataValue[columnsNOTICE_FILE.length];
					for (int i = 0; i < columnsVal.length; i++)
					{
						String keyVal = null;
						switch (i) 
						{
						case 0:
							keyVal=req.geteId();
							break;
						case 1:
							keyVal=myshop.getShopId();
							break;
						case 2:
							keyVal=Page.getPageMenuID();
							break;
						case 3:
							keyVal=Page.getPageIndex();
							break;
						case 4:
							keyVal=Page.getX();
							break;
						case 5:
							keyVal=Page.getY();
							break;
						case 6:
							keyVal=Page.getHigh();
							break;
						case 7:
							keyVal=Page.getWidth();
							break;
						case 8:
							keyVal=Page.getBgColor();
							break;
						case 9:
							keyVal=Page.getFntName();
							break;
						case 10:
							if(PosPub.isNumeric(Page.getFntSize())==false)
							{									
								keyVal="0";
							}
							else 
							{
								keyVal=Page.getFntSize();
							}								
							break;
						case 11:
							if(PosPub.isNumeric(Page.getFntColor())==false)
							{
								keyVal="0";
							}
							else 
							{
								keyVal=Page.getFntColor();				
							}								
							break;
						case 12:
							keyVal=Page.getFntStyle();
							break;
						case 13:
							keyVal=Page.getButtonName();
							break;
						case 14:
							keyVal="100";
							break;
						case 15:
							keyVal=Page.getPageID();
							break;
							
						default:

							break;

						}
						
						if (keyVal != null) 
						{
							insColCt++;
							if (i == 3|| i == 4 || i == 5|| i == 6|| i == 7|| i == 8|| i == 10|| i == 11) 
							{
								columnsVal[i] = new DataValue(keyVal, Types.DECIMAL);
							} 
							else
							{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);									
							}								
						} 
						else 
						{
							columnsVal[i] = null;
						}
						
					}
					
					String[] columns2 = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];

					insColCt = 0;

					for (int i = 0; i < columnsVal.length; i++) 
					{
						if (columnsVal[i] != null) 
						{
							columns2[insColCt] = columnsNOTICE_FILE[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt++;
							if (insColCt >= insValue2.length)
								break;
						}
					}

					InsBean ib2 = new InsBean("DCP_PAGE_MENU", columns2);
					ib2.addValues(insValue2);
					lstIns.add(new DataProcessBean(ib2));
					
					List<level3Elm> datas3=Page.getDatas();
	  			for (level3Elm level3Elm : datas3)
	  			{
	  				int insColCtX = 0;
						String[] columnsNOTICE_FILEX ={"EID","SHOPID","PAGE_MENU_FUNC_ID","PAGEINDEX","FUNCNO","STATUS","PAGE_MENU_ID"};
						DataValue[] columnsValX = new DataValue[columnsNOTICE_FILEX.length];
						for (int i = 0; i < columnsValX.length; i++)
						{
							String keyVal = null;
							switch (i) 
							{
							case 0:
								keyVal=req.geteId();
								break;
							case 1:
								keyVal=myshop.getShopId();
								break;
							case 2:
								keyVal=PosPub.getGUID(false).toUpperCase();
								break;
							case 3:
								keyVal=Page.getPageIndex();
								break;
							case 4:
								keyVal=level3Elm.getFuncNO();
								break;
							case 5:
								keyVal="100";
								break;
							case 6:
								keyVal=Page.getPageMenuID();
								break;
								
							default:

								break;

							}
							if (keyVal != null) 
							{
								insColCtX++;
								if (i == 3 ) 
								{
									columnsValX[i] = new DataValue(keyVal, Types.DECIMAL);
								} 
								else
								{
									columnsValX[i] = new DataValue(keyVal, Types.VARCHAR);									
								}								
							} 
							else 
							{
								columnsValX[i] = null;
							}
							
						}
						String[] columns2X = new String[insColCtX];
						DataValue[] insValue2X = new DataValue[insColCtX];

						insColCtX = 0;

						for (int i = 0; i < columnsValX.length; i++) 
						{
							if (columnsValX[i] != null) 
							{
								columns2X[insColCtX] = columnsNOTICE_FILEX[i];
								insValue2X[insColCtX] = columnsValX[i];
								insColCtX++;
								if (insColCtX >= insValue2X.length)
									break;
							}
						}

						InsBean ib2X = new InsBean("DCP_PAGE_MENU_FUNC", columns2X);
						ib2X.addValues(insValue2X);	
						lstIns.add(new DataProcessBean(ib2X));
	  			}
	  			
	  		}
	  		break;//只有一个门店
	  	}
	  	
	  	//再加入新增SQL
			for (DataProcessBean ins : lstIns) 
			{				
				this.addProcessData(ins);
			}
			
	  	this.doExecuteDataToDB();
			if (res.isSuccess()) 			
			{			
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}	
	  	
	  }
	  else //=1:插入
	  {
	  	for (level2ElmShop myshop : shops) 
	  	{
	  		for (level2ElmData Page : datas) 
	  		{
	  			String sql="select page_menu_id from ta_page_menu "
	  					+"where EID='"+req.geteId()+"'  "
	  					+"and SHOPID='"+req.getShopId()+"'  "
	  					+"and page_menu_id='"+Page.getPageMenuID()+"'";

	  			String[] conditionValues_Count = {};//查詢條件
					List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
					if (getQData_Count!=null && getQData_Count.isEmpty()==false) 
					{
						//报异常
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "page_menu_id为" +Page.getPageMenuID() + "的已经存在！");						
					}
					else
					{
						int insColCt = 0;
						String[] columnsNOTICE_FILE ={"EID","SHOPID","PAGE_MENU_ID","PAGEINDEX","X","Y","HIGH","WIDTH","BG_COLOR","FNT_NAME","FNT_SIZE","FNT_COLOR","FNT_STYLE","BUTTONNAME","STATUS","PAGE_ID"};
						DataValue[] columnsVal = new DataValue[columnsNOTICE_FILE.length];
						for (int i = 0; i < columnsVal.length; i++)
						{
							String keyVal = null;
							switch (i) 
							{
							case 0:
								keyVal=req.geteId();
								break;
							case 1:
								keyVal=myshop.getShopId();
								break;
							case 2:
								keyVal=Page.getPageMenuID();
								break;
							case 3:
								keyVal=Page.getPageIndex();
								break;
							case 4:
								keyVal=Page.getX();
								break;
							case 5:
								keyVal=Page.getY();
								break;
							case 6:
								keyVal=Page.getHigh();
								break;
							case 7:
								keyVal=Page.getWidth();
								break;
							case 8:
								keyVal=Page.getBgColor();
								break;
							case 9:
								keyVal=Page.getFntName();
								break;
							case 10:
								if(PosPub.isNumeric(Page.getFntSize())==false)
								{									
									keyVal="0";
								}
								else 
								{
									keyVal=Page.getFntSize();
								}								
								break;
							case 11:
								if(PosPub.isNumeric(Page.getFntColor())==false)
								{
									keyVal="0";
								}
								else 
								{
									keyVal=Page.getFntColor();				
								}								
								break;
							case 12:
								keyVal=Page.getFntStyle();
								break;
							case 13:
								keyVal=Page.getButtonName();
								break;
							case 14:
								keyVal="100";
								break;
							case 15:
								keyVal=Page.getPageID();
								break;
								
							default:

								break;

							}
							
							if (keyVal != null) 
							{
								insColCt++;
								if (i == 3|| i == 4 || i == 5|| i == 6|| i == 7|| i == 8|| i == 10|| i == 11) 
								{
									columnsVal[i] = new DataValue(keyVal, Types.DECIMAL);
								} 
								else
								{
									columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);									
								}								
							} 
							else 
							{
								columnsVal[i] = null;
							}
							
						}
						
						String[] columns2 = new String[insColCt];
						DataValue[] insValue2 = new DataValue[insColCt];

						insColCt = 0;

						for (int i = 0; i < columnsVal.length; i++) 
						{
							if (columnsVal[i] != null) 
							{
								columns2[insColCt] = columnsNOTICE_FILE[i];
								insValue2[insColCt] = columnsVal[i];
								insColCt++;
								if (insColCt >= insValue2.length)
									break;
							}
						}

						InsBean ib2 = new InsBean("DCP_PAGE_MENU", columns2);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));	
						
						List<level3Elm> datas3=Page.getDatas();
		  			for (level3Elm level3Elm : datas3)
		  			{
		  				int insColCtX = 0;
							String[] columnsNOTICE_FILEX ={"EID","SHOPID","PAGE_MENU_FUNC_ID","PAGEINDEX","FUNCNO","STATUS","PAGE_MENU_ID"};
							DataValue[] columnsValX = new DataValue[columnsNOTICE_FILEX.length];
							for (int i = 0; i < columnsValX.length; i++)
							{
								String keyVal = null;
								switch (i) 
								{
								case 0:
									keyVal=req.geteId();
									break;
								case 1:
									keyVal=myshop.getShopId();
									break;
								case 2:
									keyVal=PosPub.getGUID(false).toUpperCase();
									break;
								case 3:
									keyVal=Page.getPageIndex();
									break;
								case 4:
									keyVal=level3Elm.getFuncNO();
									break;
								case 5:
									keyVal="100";
									break;
								case 6:
									keyVal=Page.getPageMenuID();
									break;
									
								default:

									break;

								}
								if (keyVal != null) 
								{
									insColCtX++;
									if (i == 3 ) 
									{
										columnsValX[i] = new DataValue(keyVal, Types.DECIMAL);
									} 
									else
									{
										columnsValX[i] = new DataValue(keyVal, Types.VARCHAR);									
									}								
								} 
								else 
								{
									columnsValX[i] = null;
								}
								
							}
							String[] columns2X = new String[insColCtX];
							DataValue[] insValue2X = new DataValue[insColCtX];

							insColCtX = 0;

							for (int i = 0; i < columnsValX.length; i++) 
							{
								if (columnsValX[i] != null) 
								{
									columns2X[insColCtX] = columnsNOTICE_FILEX[i];
									insValue2X[insColCtX] = columnsValX[i];
									insColCtX++;
									if (insColCtX >= insValue2X.length)
										break;
								}
							}

							InsBean ib2X = new InsBean("DCP_PAGE_MENU_FUNC", columns2X);
							ib2X.addValues(insValue2X);
							this.addProcessData(new DataProcessBean(ib2X));		

		  			}
						
					}	  			
	  			
	  		}
	  		break;//只有一个门店
	  	}
	  	
	  	this.doExecuteDataToDB();
			if (res.isSuccess()) 			
			{			
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}	
	  	
	  }
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PageFunUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PageFunUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PageFunUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PageFunUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	
	  List<level2ElmData> datas=req.getDatas();		
	  List<level2ElmShop> shops=req.getShops();
	  
	  if (Check.Null(req.getType())) 
		{
			errCt++;
			errMsg.append("分页功能类型不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		for (level2ElmData Page : datas) 
		{
			if (Check.Null(Page.getPageMenuID())) 
			{
				errCt++;
				errMsg.append("分页菜单GUID不可为空值, ");
				isFail = true;
			} 
			
			if (Check.Null(Page.getPageID())) 
			{
				errCt++;
				errMsg.append("分页GUID不可为空值, ");
				isFail = true;
			} 
			
			if (Check.Null(Page.getPageIndex())) 
			{
				errCt++;
				errMsg.append("分页编码不可为空值, ");
				isFail = true;
			} 
			
			if (PosPub.isNumeric(Page.getPageIndex())==false) 
			{
				errCt++;
				errMsg.append("分页编码必须为数字类型, ");
				isFail = true;
			} 
			
			
			if (Check.Null(Page.getX())) 
			{
				errCt++;
				errMsg.append("X坐标不可为空值, ");
				isFail = true;
			} 
			
			if (PosPub.isNumeric(Page.getX())==false) 
			{
				errCt++;
				errMsg.append("X坐标必须为数字类型, ");
				isFail = true;
			} 
			
			
			if (Check.Null(Page.getY())) 
			{
				errCt++;
				errMsg.append("Y坐标不可为空值, ");
				isFail = true;
			} 
			
			if (PosPub.isNumeric(Page.getY())==false) 
			{
				errCt++;
				errMsg.append("Y坐标必须为数字类型, ");
				isFail = true;
			} 
			
			if (Check.Null(Page.getHigh())) 
			{
				errCt++;
				errMsg.append("高度不可为空值, ");
				isFail = true;
			} 
			
			if (PosPub.isNumeric(Page.getHigh())==false) 
			{
				errCt++;
				errMsg.append("高度必须为数字类型, ");
				isFail = true;
			} 
			
			if (Check.Null(Page.getWidth())) 
			{
				errCt++;
				errMsg.append("宽度不可为空值, ");
				isFail = true;
			} 
			
			if (PosPub.isNumeric(Page.getWidth())==false) 
			{
				errCt++;
				errMsg.append("宽度必须为数字类型, ");
				isFail = true;
			} 
			
			List<level3Elm> datas3=Page.getDatas();
			for (level3Elm level3Elm : datas3)
			{
				if (Check.Null(level3Elm.getFuncNO())) 
				{
					errCt++;
					errMsg.append("功能编码不可为空值, ");
					isFail = true;
				} 
				
				if (isFail)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}
			}			
			
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
			
		}
		
		for (level2ElmShop myshop : shops) 
		{
			if (Check.Null(myshop.getShopId())) 
			{
				errCt++;
				errMsg.append("门店编码不可为空值, ");
				isFail = true;
			} 
			
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		
		
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PageFunUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PageFunUpdateReq>(){};
	}

	@Override
	protected DCP_PageFunUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PageFunUpdateRes();
	}

}
