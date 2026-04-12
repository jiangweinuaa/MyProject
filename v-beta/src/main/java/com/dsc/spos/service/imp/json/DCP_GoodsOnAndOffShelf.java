package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsOnAndOffShelfReq;
import com.dsc.spos.json.cust.req.DCP_GoodsOnAndOffShelfReq.level1Range;
import com.dsc.spos.json.cust.req.DCP_GoodsOnAndOffShelfReq.level1Plu;
import com.dsc.spos.json.cust.res.DCP_GoodsOnAndOffShelfRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsOnAndOffShelf extends SPosAdvanceService<DCP_GoodsOnAndOffShelfReq, DCP_GoodsOnAndOffShelfRes> {

	@Override
	protected void processDUID(DCP_GoodsOnAndOffShelfReq req, DCP_GoodsOnAndOffShelfRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			String eId= req.geteId();		
			String curLangType = req.getLangType();
			if(curLangType==null||curLangType.isEmpty())
			{
				curLangType = "zh_CN";
			}
			String classType = "ONLINE";
			String billType = req.getRequest().getBillType();//单据类型：1线上商品设置，2商品上下架
			String oprType = req.getRequest().getOprType();//操作类型1-渠道上架2-渠道下架3-门店上架4-门店下架
			String channelId = req.getRequest().getChannelId();//渠道编码，按渠道上下架时必传
			String orgType = req.getRequest().getOrgType();//操作机构类型1-公司2-门店
			String orgId = req.getRequest().getOrgId();//操作机构编码
			billType = "2";
			List<level1Range> rangeList = req.getRequest().getShelfRangeList();//按门店上下架时必传
			List<level1Plu> pluList = req.getRequest().getPluList();//编码
			
			String lastmoditime = null;//req.getRequset().getLastmoditime();
			if(lastmoditime==null||lastmoditime.isEmpty())
			{
				lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			
			if(billType.equals("1"))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据类型billType="+billType+"暂不支持！");
				/*String updatetStatus = "100";
				if(oprType.equals("1"))//1-全渠道上架
				{					
					updatetStatus = "100";									
				}
				else if(oprType.equals("2"))//2-全渠道下架
				{
					updatetStatus = "0";	
				}			
				else 
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "操作类型oprType="+oprType+"不支持线上商品设置！");
				
				}
				
				
				for (level1Plu par : pluList)
				{
					String pluNo = par.getPluNo();
					UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");			
					ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
					
					ub1.addUpdateValue("STATUS",new DataValue(updatetStatus, Types.VARCHAR)); 						
					ub1.addUpdateValue("LASTMODIOPID",new DataValue(req.getOpNO(), Types.VARCHAR)); 
					ub1.addUpdateValue("LASTMODIOPNAME",new DataValue(req.getOpName(), Types.VARCHAR));
					ub1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE)); 
					this.addProcessData(new DataProcessBean(ub1));
					
				}
				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				return;*/
						
			}
		  else if(billType.equals("2"))//商品上下架
		  {
				if (oprType.equals("1")) // 1-渠道上架
				{
					String shopId = "ALL";
					String status = "100";
					//oprType=1-渠道上架：DCP_GOODS_SHELF_RANGE中插入一笔传入渠道，SHOPID=ALL的记录；
					for (level1Plu par : pluList)
					{
						String pluNo = par.getPluNo();
						DelBean db1 = new DelBean("DCP_GOODS_SHELF_RANGE");
						db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
						db1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
						db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db1));
						
						String[] columns_Goods_Shelf_Range =
							{ "EID", "PLUNO", "PLUTYPE", "PLUNAME", "CHANNELID","SHOPID","STATUS","BILLTYPE", "ORGTYPE", "ORGID", "ORGNAME", 
									"CREATEOPID", "CREATEOPNAME", "CREATETIME" };

							DataValue[] insValue1 = null;

							insValue1 = new DataValue[]
							{ 
									new DataValue(eId, Types.VARCHAR), 
									new DataValue(pluNo, Types.VARCHAR),
									new DataValue(par.getPluType(), Types.VARCHAR), 
									new DataValue(par.getPluName(), Types.VARCHAR),									
									new DataValue(channelId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(status, Types.VARCHAR),
									new DataValue(billType, Types.VARCHAR),
									new DataValue(orgType, Types.VARCHAR),
									new DataValue(orgId, Types.VARCHAR), 
									new DataValue("", Types.VARCHAR),
									new DataValue(req.getOpNO(), Types.VARCHAR),
									new DataValue(req.getOpName(), Types.VARCHAR),
									new DataValue(lastmoditime, Types.DATE) };

							InsBean ib1 = new InsBean("DCP_GOODS_SHELF_RANGE", columns_Goods_Shelf_Range);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));			
					}

				} 
				else if (oprType.equals("2"))
				{
					//oprType=2-渠道下架：DCP_GOODS_SHELF_RANGE中删除传入渠道且SHOPID=ALL的记录；且删除DCP_GOODS_SHELF_RANGE 传入渠道的数据。
					String shopId = "ALL";
					String status = "0";			
					for (level1Plu par : pluList)
					{
						String pluNo = par.getPluNo();
						DelBean db1 = new DelBean("DCP_GOODS_SHELF_RANGE");
						db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
						db1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
						//db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db1));
						
						String[] columns_Goods_Shelf_Range =
							{ "EID", "PLUNO", "PLUTYPE", "PLUNAME", "CHANNELID","SHOPID","STATUS","BILLTYPE", "ORGTYPE", "ORGID", "ORGNAME", 
									"CREATEOPID", "CREATEOPNAME", "CREATETIME" };

							DataValue[] insValue1 = null;

							insValue1 = new DataValue[]
							{ 
									new DataValue(eId, Types.VARCHAR), 
									new DataValue(pluNo, Types.VARCHAR),
									new DataValue(par.getPluType(), Types.VARCHAR), 
									new DataValue(par.getPluName(), Types.VARCHAR),									
									new DataValue(channelId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(status, Types.VARCHAR),
									new DataValue(billType, Types.VARCHAR),
									new DataValue(orgType, Types.VARCHAR),
									new DataValue(orgId, Types.VARCHAR), 
									new DataValue("", Types.VARCHAR),
									new DataValue(req.getOpNO(), Types.VARCHAR),
									new DataValue(req.getOpName(), Types.VARCHAR),
									new DataValue(lastmoditime, Types.DATE) };

							InsBean ib1 = new InsBean("DCP_GOODS_SHELF_RANGE", columns_Goods_Shelf_Range);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));	
						
								
					}
					

				}
				else if (oprType.equals("3"))
				{
					//oprType=3-门店上架：DCP_GOODS_SHELF_RANGE中删除传入渠道传入门店且状态为下架的记录		
					String status = "100";
					for (level1Plu par : pluList)
					{
						String pluNo = par.getPluNo();
						for (level1Range par_range : rangeList)
						{							
							DelBean db1 = new DelBean("DCP_GOODS_SHELF_RANGE");
							db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
							db1.addCondition("CHANNELID", new DataValue(par_range.getChannelId(), Types.VARCHAR));
							db1.addCondition("SHOPID", new DataValue(par_range.getShopId(), Types.VARCHAR));
							this.addProcessData(new DataProcessBean(db1));
							
							String[] columns_Goods_Shelf_Range =
								{ "EID", "PLUNO", "PLUTYPE", "PLUNAME", "CHANNELID","SHOPID","STATUS","BILLTYPE", "ORGTYPE", "ORGID", "ORGNAME", 
										"CREATEOPID", "CREATEOPNAME", "CREATETIME" };

								DataValue[] insValue1 = null;

								insValue1 = new DataValue[]
								{ 
										new DataValue(eId, Types.VARCHAR), 
										new DataValue(pluNo, Types.VARCHAR),
										new DataValue(par.getPluType(), Types.VARCHAR), 
										new DataValue(par.getPluName(), Types.VARCHAR),									
										new DataValue(par_range.getChannelId(), Types.VARCHAR),
										new DataValue(par_range.getShopId(), Types.VARCHAR),
										new DataValue(status, Types.VARCHAR),
										new DataValue(billType, Types.VARCHAR),
										new DataValue(orgType, Types.VARCHAR),
										new DataValue(orgId, Types.VARCHAR), 
										new DataValue("", Types.VARCHAR),
										new DataValue(req.getOpNO(), Types.VARCHAR),
										new DataValue(req.getOpName(), Types.VARCHAR),
										new DataValue(lastmoditime, Types.DATE) };

								InsBean ib1 = new InsBean("DCP_GOODS_SHELF_RANGE", columns_Goods_Shelf_Range);
								ib1.addValues(insValue1);
								this.addProcessData(new DataProcessBean(ib1));	
							
						}
														
					}				

				}		  
				else if (oprType.equals("4"))
				{
					//oprType=4-门店下架：DCP_GOODS_SHELF_RANGE中插入一笔传入渠道传入门店且状态为下架的记录；
					String status = "0";	
					for (level1Plu par : pluList)
					{
						String pluNo = par.getPluNo();
						for (level1Range par_range : rangeList)
						{							
							DelBean db1 = new DelBean("DCP_GOODS_SHELF_RANGE");
							db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
							db1.addCondition("CHANNELID", new DataValue(par_range.getChannelId(), Types.VARCHAR));
							db1.addCondition("SHOPID", new DataValue(par_range.getShopId(), Types.VARCHAR));
							this.addProcessData(new DataProcessBean(db1));
							
							String[] columns_Goods_Shelf_Range =
								{ "EID", "PLUNO", "PLUTYPE", "PLUNAME", "CHANNELID","SHOPID","STATUS","BILLTYPE", "ORGTYPE", "ORGID", "ORGNAME", 
										"CREATEOPID", "CREATEOPNAME", "CREATETIME" };

								DataValue[] insValue1 = null;

								insValue1 = new DataValue[]
								{ 
										new DataValue(eId, Types.VARCHAR), 
										new DataValue(pluNo, Types.VARCHAR),
										new DataValue(par.getPluType(), Types.VARCHAR), 
										new DataValue(par.getPluName(), Types.VARCHAR),									
										new DataValue(par_range.getChannelId(), Types.VARCHAR),
										new DataValue(par_range.getShopId(), Types.VARCHAR),
										new DataValue(status, Types.VARCHAR),
										new DataValue(billType, Types.VARCHAR),
										new DataValue(orgType, Types.VARCHAR),
										new DataValue(orgId, Types.VARCHAR), 
										new DataValue("", Types.VARCHAR),
										new DataValue(req.getOpNO(), Types.VARCHAR),
										new DataValue(req.getOpName(), Types.VARCHAR),
										new DataValue(lastmoditime, Types.DATE) };

								InsBean ib1 = new InsBean("DCP_GOODS_SHELF_RANGE", columns_Goods_Shelf_Range);
								ib1.addValues(insValue1);
								this.addProcessData(new DataProcessBean(ib1));	
							
						}
														
					}				


				}
				else
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "操作类型oprType=" + oprType + "不支持商品上下架设置！");
				}
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
				return;

			}
		  else
		  {
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据类型billType="+billType+"未实现！");
		  }
			
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());
		}
		
		
		
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsOnAndOffShelfReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsOnAndOffShelfReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsOnAndOffShelfReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsOnAndOffShelfReq req) throws Exception
	{
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (req.getRequest().getPluList() == null)
		{
			errMsg.append("商品编码不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if(Check.Null(req.getRequest().getBillType()))
		{
			errMsg.append("单据类型不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
				
		if(Check.Null(req.getRequest().getOprType()))
		{
			errMsg.append("操作类型不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String oprType = req.getRequest().getOprType();//操作类型1-渠道上架2-渠道下架3-门店上架4-门店下架
		if(oprType.equals("1")||oprType.equals("2"))
		{
			if (Check.Null(req.getRequest().getChannelId()))
			{
				errMsg.append("按渠道上下架时渠道编码channelId不能为空，");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
			
		}
		
		if(oprType.equals("3")||oprType.equals("4"))
		{
			if (req.getRequest().getShelfRangeList()==null||req.getRequest().getShelfRangeList().isEmpty())
			{
				errMsg.append("按门店上下架时门店上下架列表shelfRangeList不能为空，");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
			
			for (level1Range par : req.getRequest().getShelfRangeList())
			{
				
				if (Check.Null(par.getChannelId()))
				{
					errMsg.append("按门店上下架时渠道编码不能为空值 ，");
					isFail = true;
				}
				if (Check.Null(par.getShopId()))
				{
					errMsg.append("按门店上下架时门店编码不能为空值 ，");
					isFail = true;
				}
			}
			
		}
		

		for (level1Plu par : req.getRequest().getPluList())
		{
			String pluNo = par.getPluNo();
			if (Check.Null(pluNo))
			{
				errMsg.append("商品编码不能为空值 ，");
				isFail = true;
			}
			if (Check.Null(par.getPluType()))
			{
				errMsg.append("商品类型不能为空值 ，");
				isFail = true;
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsOnAndOffShelfReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsOnAndOffShelfReq>(){};
	}

	@Override
	protected DCP_GoodsOnAndOffShelfRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsOnAndOffShelfRes();
	}

}
