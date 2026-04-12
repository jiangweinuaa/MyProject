package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PageUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PageUpdateReq.level2ElmPage;
import com.dsc.spos.json.cust.req.DCP_PageUpdateReq.level2ElmShop;
import com.dsc.spos.json.cust.res.DCP_PageUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PageUpdate extends SPosAdvanceService<DCP_PageUpdateReq,DCP_PageUpdateRes>
{
	@Override
	protected void processDUID(DCP_PageUpdateReq req, DCP_PageUpdateRes res) throws Exception 
	{	
    List<level2ElmPage> datas=req.getDatas();
		
		List<level2ElmShop> shops=req.getShops();
		
		if (req.getType().equals("3")) //复制
		{
		
			for (level2ElmShop myshop : shops) 
			{				
			  //先删除原来的
				DelBean db1 = new DelBean("DCP_PAGE");
				db1.addCondition("EID", new DataValue(req.geteId().toUpperCase(), Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(myshop.getShopId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				for (level2ElmPage Page : datas) 
				{
					int insColCt = 0;
					String[] columnsNOTICE_FILE ={"EID","SHOPID","PAGE_ID","PAGEINDEX","PAGENAME","PAGETYPE","STATUS"};
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
							keyVal=PosPub.getGUID(false).toUpperCase();
							break;
						case 3:
							keyVal=Page.getPageIndex();
							break;
						case 4:
							keyVal=Page.getPageName();
							break;
						case 5:
							keyVal=Page.getPageType();
							break;
						case 6:
							keyVal=Page.getStatus();
							break;
							
						default:

							break;

						}
						
						if (keyVal != null) 
						{
							insColCt++;
							if (i == 3 || i == 5) 
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

					InsBean ib2 = new InsBean("DCP_PAGE", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));		
										
				}
				
			}
			
			this.doExecuteDataToDB();
			if (res.isSuccess()) 			
			{			
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}	
		
		}
		else if (req.getType().equals("2")) //更新,门店只可能一条 否则PAGE_ID会重复
		{		
						
			for (level2ElmShop myshop : shops) 
			{				
			  //先删除原来的
				DelBean db1 = new DelBean("DCP_PAGE");
				db1.addCondition("EID", new DataValue(req.geteId().toUpperCase(), Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(myshop.getShopId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				for (level2ElmPage Page : datas) 
				{
					int insColCt = 0;
					String[] columnsNOTICE_FILE ={"EID","SHOPID","PAGE_ID","PAGEINDEX","PAGENAME","PAGETYPE","STATUS"};
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
							keyVal=Page.getPageID();
							break;
						case 3:
							keyVal=Page.getPageIndex();
							break;
						case 4:
							keyVal=Page.getPageName();
							break;
						case 5:
							keyVal=Page.getPageType();
							break;
						case 6:
							keyVal=Page.getStatus();
							break;
							
						default:

							break;

						}
						
						if (keyVal != null) 
						{
							insColCt++;
							if (i == 3 || i == 5) 
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

					InsBean ib2 = new InsBean("DCP_PAGE", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));		
										
				}
				break;				
			}
			
			this.doExecuteDataToDB();
			if (res.isSuccess()) 			
			{			
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}	
			
		}
		else //=1:插入,门店只可能一条 否则PAGE_ID会重复
		{			
			for (level2ElmShop myshop : shops) 
			{				
				for (level2ElmPage Page : datas) 
				{
					String sql="select  PAGE_ID from TA_PAGE "
							+ "where EID='"+req.geteId()+"' "
							+ "and SHOPID='"+myshop.getShopId()+"' "
							+ "and PAGE_ID='"+Page.getPageID()+"'";

					String[] conditionValues_Count = {};//查詢條件
					List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
					if (getQData_Count!=null && getQData_Count.isEmpty()==false) 
					{
						//报异常
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "PAGE_ID为" +Page.getPageID() + "的已经存在！");						
					}
					else
					{
						int insColCt = 0;
						String[] columnsNOTICE_FILE ={"EID","SHOPID","PAGE_ID","PAGEINDEX","PAGENAME","PAGETYPE","STATUS"};
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
								keyVal=Page.getPageID();
								break;
							case 3:
								keyVal=Page.getPageIndex();
								break;
							case 4:
								keyVal=Page.getPageName();
								break;
							case 5:
								keyVal=Page.getPageType();
								break;
							case 6:
								keyVal=Page.getStatus();
								break;
								
							default:

								break;

							}
							
							if (keyVal != null) 
							{
								insColCt++;
								if (i == 3 || i == 5) 
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

						InsBean ib2 = new InsBean("DCP_PAGE", columns2);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));						
					}
				}			
				break;
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
	protected List<InsBean> prepareInsertData(DCP_PageUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PageUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PageUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PageUpdateReq req) throws Exception 
	{
	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		
		List<level2ElmPage> datas=req.getDatas();
		
		List<level2ElmShop> shops=req.getShops();
		
		if (Check.Null(req.getType())) 
		{
			errCt++;
			errMsg.append("分页设置类型不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		for (level2ElmPage Page : datas) 
		{
			if (Check.Null(Page.getPageID())) 
			{
				errCt++;
				errMsg.append("GUID不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(Page.getPageType())) 
			{
				errCt++;
				errMsg.append("分页类型不可为空值, ");
				isFail = true;
			} 
			
			if (PosPub.isNumeric(Page.getPageType())==false) 
			{
				errCt++;
				errMsg.append("分页类型必须为数字类型, ");
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
			
			if (Check.Null(Page.getPageName())) 
			{
				errCt++;
				errMsg.append("分页名称不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(Page.getStatus())) 
			{
				errCt++;
				errMsg.append("状态不可为空值, ");
				isFail = true;
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
	protected TypeToken<DCP_PageUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PageUpdateReq>(){};
	}

	@Override
	protected DCP_PageUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PageUpdateRes();
	}
	
	
	protected String getQueryPAGE_ID_Exist(DCP_PageUpdateReq req) throws Exception
	{
		String sql="select  PAGE_ID from TA_PAGE "
			+ "where EID='"+req.geteId()+"' "
			+ "and SHOPID='"+req.getShopId()+"' "
			+ "and PAGE_ID=''";
				
		return sql;
	}
	

}
