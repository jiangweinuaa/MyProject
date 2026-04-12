package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AbnormalOrderProcessReq;
import com.dsc.spos.json.cust.req.DCP_AbnormalOrderProcessReq.levelGoods;
import com.dsc.spos.json.cust.req.DCP_OrderShippingReq;
import com.dsc.spos.json.cust.res.DCP_AbnormalOrderProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderAbnormal;
import com.dsc.spos.waimai.entity.orderAbnormalDetail;
import com.dsc.spos.waimai.entity.orderAbnormalType;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderGoodsItemAgio;
import com.dsc.spos.waimai.entity.orderGoodsItemMessage;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

public class DCP_AbnormalOrderProcess extends SPosAdvanceService<DCP_AbnormalOrderProcessReq,DCP_AbnormalOrderProcessRes>
{

	@Override
	protected void processDUID(DCP_AbnormalOrderProcessReq req, DCP_AbnormalOrderProcessRes res) throws Exception
	{
		// TODO Auto-generated method stub
		String eId = req.getRequest().geteId();
		String orderNo = req.getRequest().getOrderNo();
		String abnormalType = req.getRequest().getAbnormalType();
		if(abnormalType.equals(orderAbnormalType.goodsNotFound))
		{
			List<DCP_AbnormalOrderProcessReq.levelGoods> goodsList = req.getRequest().getGoodsList();
			
			if(goodsList==null||goodsList.isEmpty())//什么都不传，顾问直接在数据改条码。是不是也可以支持下
			{
				
			}
			else
			{
				
				
				boolean isFail = false;
				StringBuffer errMsg = new StringBuffer("");
				for (levelGoods goodsItem : goodsList)
				{
					if(Check.Null(goodsItem.getItem()))
					{
						errMsg.append("商品来源项次item不能为空值 ");
					   	isFail = true;
					}
					if(Check.Null(goodsItem.getPluBarcode()))
					{
						errMsg.append("商品条码不能为空值 ");
					   	isFail = true;						
					}
				}
				if (isFail)
				 {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				 }
				
				/***********************检查下前端传入的item与后端异常item是否一致****************************/
				
				isFail = false;
				errMsg = new StringBuffer("");
				String sql_detail = " select * from DCP_ORDER_ABNORMALINFO_DETAIL where status='0' and EID='"+eId+"' and ORDERNO='"+orderNo+"' and ABNORMALTYPE='"+abnormalType+"' ";
				String logStart = "异常处理,更新商品plubarcode，单号orderNo="+orderNo+",";
				HelpTools.writelog_waimai(logStart+"查询异常明细sql="+sql_detail);
				
				List<Map<String, Object>> getAbnormalDetail = this.doQueryData(sql_detail, null);
				if(getAbnormalDetail==null||getAbnormalDetail.isEmpty())
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "没有需要处理的goodsNotFound异常类型");
				}
				
				for (levelGoods goodsItem : goodsList)
				{
					boolean isMatch = false;
					try
					{
						int item_req = Integer.parseInt(goodsItem.getItem());						
						for (Map<String, Object> map : getAbnormalDetail)
						{
							int item_db = Integer.parseInt(map.getOrDefault("OITEM", "0").toString());
							if(item_req==item_db)
							{
								isMatch = true;
								break;
							}
						}
					} 
					catch (Exception e)
					{
						// TODO: handle exception
					}
					
					if(!isMatch)
					{
						isFail = true;
						errMsg.append("传入的项次item="+goodsItem.getItem()+"没有对应的异常项次，");
					}
				}
				
				if (isFail)
				 {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				 }
				
			  boolean nRet =  this.goodsNotFound_Process(req, errMsg);
			  
			  res.setSuccess(nRet);
			  res.setServiceStatus("000");
			  res.setServiceDescription("服务执行成功！");
			  if(!nRet)
			  {
				  res.setServiceStatus("100");
				  res.setServiceDescription(errMsg.toString());
			  }

			  if (nRet)
			  {
				  updateRefundOrder(eId, orderNo);
				  StringBuffer error_orderToSale = new StringBuffer();
				  orderToSaleProcess(req, error_orderToSale);
			  }

			  return;
				
			}
			
			
			
		}
		else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "异常类型:"+abnormalType+"暂未实现！");
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_AbnormalOrderProcessReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AbnormalOrderProcessReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AbnormalOrderProcessReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AbnormalOrderProcessReq req) throws Exception
	{
		boolean isFail = false;
		  StringBuffer errMsg = new StringBuffer("");

		  if(req.getRequest()==null)
		  {
		  	errMsg.append("request不能为空值 ");
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		  }
		  
		  String eId = req.getRequest().geteId();
		  String orderNo = req.getRequest().getOrderNo();
		  String abnormalType = req.getRequest().getAbnormalType();
		      	  
		  if(Check.Null(eId)){
		   	errMsg.append("企业id不能为空值 ");
		   	isFail = true;

		  }
		  if(Check.Null(orderNo)){
			   	errMsg.append("订单号不能为空值 ");
			   	isFail = true;

			  }
		  if(Check.Null(abnormalType)){
			   	errMsg.append("异常类型不能为空值 ");
			   	isFail = true;

			  }
		   
		 if (isFail)
		 {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		 }
		  
		return isFail;
	}

	@Override
	protected TypeToken<DCP_AbnormalOrderProcessReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_AbnormalOrderProcessReq>(){};
	}

	@Override
	protected DCP_AbnormalOrderProcessRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_AbnormalOrderProcessRes();
	}
	
	private boolean goodsNotFound_Process(DCP_AbnormalOrderProcessReq req,StringBuffer error) throws Exception
	{
		if(error==null)
		{
			error = new StringBuffer("");
		}
		boolean nRet = false;
		
		String eId = req.getRequest().geteId();
		String orderNo = req.getRequest().getOrderNo();
		String abnormalType = req.getRequest().getAbnormalType();		
		String abnormalTypeName = "商品错误/未找到";
		List<DCP_AbnormalOrderProcessReq.levelGoods> goodsList = req.getRequest().getGoodsList();
		//查询下数据库 原单信息
		order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId, "", orderNo);
		if(dcpOrder==null)
		{
			error.append("订单不存在！");
			return false;
		}
		if(dcpOrder.getGoodsList()==null||dcpOrder.getGoodsList().isEmpty())
		{
			error.append("订单商品资料不存在！");
			return false;
		}
		//默认不能处理60天之前的异常订单
		Calendar calendar = Calendar.getInstance();
		String AbnormalOrderEffectDays = PosPub.getPARA_SMS(this.dao,eId,"","AbnormalOrderEffectDays");
		int spwnDays = 60;//默认处理60天之前的订单
		if (AbnormalOrderEffectDays!=null&&!AbnormalOrderEffectDays.trim().isEmpty())
		{
			try {
				 spwnDays = Integer.parseInt(AbnormalOrderEffectDays);
			}
			catch (Exception e)
			{
				spwnDays = 60;
			}
		}

		//大于0.才有意义
		if (spwnDays<=0)
		{
			spwnDays = 60;
		}

		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE,0-spwnDays);//2月前的订单
		String bdate_format = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		try
		{
			if(Integer.parseInt(dcpOrder.getbDate())<Integer.parseInt(bdate_format))
			{
				error.append("参数设置不能处理"+spwnDays+"天之前的异常订单!");
				return false;
			}


		}
		catch (Exception e)
		{

		}


				
		boolean isMatch = false;//有没有匹配到标识
		String logStart = "异常处理,更新商品plubarcode，单号orderNo="+orderNo+",";
		HelpTools.writelog_waimai(logStart+"循环匹配【开始】");
		
		String loadDocType = dcpOrder.getLoadDocType();
		String channelId = dcpOrder.getChannelId();
		
		boolean isWaimaiFlag = false;//是否饿了么、美团、京东到家外卖
		boolean isNeedPackage = false;//是否展开套餐

		if(loadDocType.equals(orderLoadDocType.YOUZAN)||loadDocType.equals(orderLoadDocType.XIAOYOU)||loadDocType.equals(orderLoadDocType.QIMAI))
		{
			isNeedPackage = true;//启迈渠道套餐自己传
		}
		if(loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM))
		{
			isWaimaiFlag = true;
			isNeedPackage = true;
		}
		
		if(isNeedPackage)
		{
			//先移除套餐子商品，后面会重新生成
			for(int i = dcpOrder.getGoodsList().size()-1;i>=0;i--)
			{
				if(dcpOrder.getGoodsList().get(i).getPackageType()!=null&&dcpOrder.getGoodsList().get(i).getPackageType().equals("3"))
				{
					dcpOrder.getGoodsList().remove(i);
				}
			}
		}
		
		List<levelGoods> matchItemList = new ArrayList<levelGoods>();
		
		StringBuffer log_Memo=new StringBuffer("");
		//给异常的plubarcode赋值
		for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
		{
			try
			{
				//把之前分摊的折扣去掉。后面还会走一次分摊
				if (isWaimaiFlag)
				{
					goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
				}

				String item = goodsItem.getItem();
				String oldPlubarcode = goodsItem.getPluBarcode();
				String pluName = goodsItem.getPluName();
				int item_i = Integer.parseInt(item);
				
				for (levelGoods updateGoods : goodsList)
				{
					try
					{
						String item_update = updateGoods.getItem();
						String newPlubarcode = updateGoods.getPluBarcode();
						int item_update_i = Integer.parseInt(item_update);
						
						if(item_i==item_update_i)
						{
							goodsItem.setPluBarcode(newPlubarcode);
							isMatch  = true;
							HelpTools.writelog_waimai(logStart+"匹配到项次item="+item+",商品名称pluName="+pluName+",更新条码，原plubarcode="+oldPlubarcode+"-->新plubarcode="+newPlubarcode);
							log_Memo.append("商品名称:"+pluName+",更新条码，原条码:"+oldPlubarcode+"-->新条码:"+newPlubarcode+"<br>");
							
							levelGoods matchItem_levelGoods = req.new levelGoods();
							matchItem_levelGoods.setItem(item);
							matchItem_levelGoods.setPluBarcode(newPlubarcode);
							matchItemList.add(matchItem_levelGoods);
							
							break;
						}
						
						
					} 
					catch (Exception e)
					{
						// TODO: handle exception
						continue;
					}
					
					
				}
			} 
			catch (Exception e)
			{
				// TODO: handle exception
				continue;
			}
			
			
		}
		
		//没有匹配到，就不用往下走了。
		if(!isMatch)
		{
			error.append("没有配到订单商品项次！");
			return false;
		}
		
		boolean isHashOtherException = false;
		try
		{				
			String sql_otherException = " select * from DCP_ORDER_ABNORMALINFO where status='0' and ABNORMALTYPE<>'"+abnormalType+"' and eid='"+eId+"' and orderno='"+orderNo+"' ";
			HelpTools.writelog_waimai(logStart+"查询下是否存在其他类型得异常sql:"+sql_otherException);
			List<Map<String, Object>> getOtherExceptionList = this.doQueryData(sql_otherException, null);
			if(getOtherExceptionList!=null&&getOtherExceptionList.isEmpty()==false)
			{
				isHashOtherException = true;
				HelpTools.writelog_waimai(logStart+"还存在其他类型得异常！");	
			}
			
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		
		
		if(isNeedPackage)
		{
			//重新走一遍，订单新建的逻辑
			
			//条码查询商品
			HelpTools.updateOrderDetailInfo(dcpOrder, error);
			//套餐商品查询子商品
			dcpOrder.setIsApportion("N");//是否取过套餐标记改成N，重新走一次
			if (loadDocType.equals(orderLoadDocType.DYWM))
			{
				dcpOrder.setIsApportion("Y");//抖音外卖，都是套餐商品，不需要自动展开
			}
			HelpTools.updateOrderWithPackage(dcpOrder, "", error);
			
			//外卖折扣分摊 下面方法里面有判断渠道类型了，无需再判断
			HelpTools.waimaiOrderDiscShareProcess(dcpOrder, error);
			
			
			 //重新生成订单商品单身、折扣档、单身备注sql语句列表
			ArrayList<DataProcessBean> DataPB_orderGoodsList = this.GetInsertOrderGoodsItemList(dcpOrder, error);
			if(DataPB_orderGoodsList==null||DataPB_orderGoodsList.isEmpty())
			{
				error.append("重新生成订单商品资料异常！");
				return false;
			}
			
	       
			//所有执行的sql
			ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
			
			DelBean del1 = new DelBean("DCP_ORDER_DETAIL");
			del1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			del1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));		
			DataPB.add(new DataProcessBean(del1));
			
			DelBean del2 = new DelBean("DCP_ORDER_DETAIL_AGIO");
			del2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			del2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			DataPB.add(new DataProcessBean(del2));
			
			DelBean del3 = new DelBean("DCP_ORDER_DETAIL_MEMO");
			del3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			del3.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			DataPB.add(new DataProcessBean(del3));
			
			for (DataProcessBean dataBean : DataPB_orderGoodsList)
			{
				DataPB.add(dataBean);
			}
			
			StringBuffer memo = new StringBuffer("");
			
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//数据库DATE类型
		    List<orderAbnormal> abnormalList = dcpOrder.getAbnormalList();
		    if (abnormalList==null||abnormalList.isEmpty())
			{
		    	nRet = true;
		    	if(!isHashOtherException)
		    	{
		    		UptBean up_orderHead = new UptBean("DCP_ORDER");
			    	up_orderHead.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			    	up_orderHead.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			    	
			    	up_orderHead.addUpdateValue("EXCEPTIONSTATUS", new DataValue("N", Types.VARCHAR));
			    	if ("Y".equals(dcpOrder.getIsApportion()))
					{
						up_orderHead.addUpdateValue("ISAPPORTION", new DataValue(dcpOrder.getIsApportion(), Types.VARCHAR));
					}
			    	else
					{
						up_orderHead.addUpdateValue("ISAPPORTION", new DataValue("N", Types.VARCHAR));
					}

			    	up_orderHead.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			    	up_orderHead.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					DataPB.add(new DataProcessBean(up_orderHead));
		    	}
		    	
		    	
		    	UptBean up1 = new UptBean("DCP_ORDER_ABNORMALINFO");
			    up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			    up1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			    up1.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
			    
			    up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));	
			    up1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
			    DataPB.add(new DataProcessBean(up1));
			    
			    UptBean up2 = new UptBean("DCP_ORDER_ABNORMALINFO_DETAIL");
			    up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			    up2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			    up2.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
			    
			    up2.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
			    DataPB.add(new DataProcessBean(up2));
			}
		    else
		    {		    
		    	for (orderAbnormal orderAbnormal : abnormalList)
				{
		    		if(orderAbnormal.getAbnormalType().equals(abnormalType))
		    		{
		    			if(orderAbnormal.getDetail()!=null&&orderAbnormal.getDetail().isEmpty()==false)
		    			{
		    				//先更新 子表都成功
		    		    	UptBean up2 = new UptBean("DCP_ORDER_ABNORMALINFO_DETAIL");
		    			    up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		    			    up2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		    			    up2.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
		    			    
		    			    up2.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
		    			    DataPB.add(new DataProcessBean(up2));
		    			    
		    			   
		    				
		    				String[] columns_abnormalDetail = {
	    					  		"EID","ORDERNO","ABNORMALTYPE","OITEM","MEMO","STATUS"};
		    				for (orderAbnormalDetail detail : orderAbnormal.getDetail())
							{
		    					//先删 在插入
		    					DelBean del_detail = new DelBean("DCP_ORDER_ABNORMALINFO_DETAIL");
		    					del_detail.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		    					del_detail.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		    					del_detail.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
		    					del_detail.addCondition("OITEM", new DataValue(detail.getItem(), Types.VARCHAR));	    					
		    					DataPB.add(new DataProcessBean(del_detail));	    					
		    					
		    					memo .append( detail.getMemo()+"<br>");
		    					//再插入
		    					DataValue[] insValue1 = new DataValue[]{
		    							new DataValue(eId, Types.VARCHAR),
		    							new DataValue(orderNo, Types.VARCHAR),
		    							new DataValue(abnormalType, Types.VARCHAR),
		    							new DataValue(detail.getItem(), Types.VARCHAR),	    							
		    							new DataValue(detail.getMemo(), Types.VARCHAR),
		    							new DataValue("0", Types.VARCHAR)
		    							
		    						};
		    					
		    					InsBean ib1 = new InsBean("DCP_ORDER_ABNORMALINFO_DETAIL", columns_abnormalDetail);
		    					ib1.addValues(insValue1);
		    					
		    					DataPB.add(new DataProcessBean(ib1));	    						    				    						    					
							}
		    				
		    				
		    				//region 单头处理
		    				if(memo.length()>1024)
		    				{
								memo.delete(1024,memo.length());
		    				}
		    				
		    				DelBean del_abnormal = new DelBean("DCP_ORDER_ABNORMALINFO");
		    				del_abnormal.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		    				del_abnormal.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		    				del_abnormal.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
		    				DataPB.add(new DataProcessBean(del_abnormal));
		    				
		    				
		    				String[] columns1 = {
		    				  		"EID","ORDERNO","ABNORMALTYPE","ABNORMALTYPENAME","ABNORMALTIME","MEMO","STATUS","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
		    					};
		    				
		    				DataValue[] insValue1 = new DataValue[]{
		    						new DataValue(eId, Types.VARCHAR),
		    						new DataValue(orderNo, Types.VARCHAR),
		    						new DataValue(abnormalType, Types.VARCHAR),
		    						new DataValue("商品错误/未找到", Types.VARCHAR),
		    						new DataValue(lastmoditime, Types.DATE),
		    						new DataValue(memo.toString(), Types.VARCHAR),
		    						new DataValue("0", Types.VARCHAR),
		    						new DataValue("", Types.VARCHAR),
		    						new DataValue("", Types.VARCHAR),
		    						new DataValue(lastmoditime, Types.DATE)
		    					};
		    				
		    				InsBean ib1 = new InsBean("DCP_ORDER_ABNORMALINFO", columns1);
		    				ib1.addValues(insValue1);
		    				
		    				DataPB.add(new DataProcessBean(ib1));
		    				//#endregion 
		    				
		    				UptBean up_orderHead = new UptBean("DCP_ORDER");
					    	up_orderHead.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					    	up_orderHead.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
					    	
					    	up_orderHead.addUpdateValue("EXCEPTIONSTATUS", new DataValue("Y", Types.VARCHAR));
					    	up_orderHead.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					    	up_orderHead.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					    	DataPB.add(new DataProcessBean(up_orderHead));
		    			  
		    				
		    			}
		    			
		    			break;//一种类型 只会有一个，找到就跳出
		    		}
		    		else
		    		{
		    			continue;
		    		}
				}
		    }
		    
		    
		    for (DataProcessBean dataProcessBean : DataPB) 
			{
				this.addProcessData(dataProcessBean);			
			}					
			this.doExecuteDataToDB();
			HelpTools.writelog_waimai(logStart+"异常处理结束！");
			if(!nRet)
			{			
				error.append(memo.toString());
			}
			
			// region 写下日志
			try
			{
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

				orderStatusLog onelv1 = new orderStatusLog();

				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(channelId);

				onelv1.setNeed_callback("N");
				onelv1.setNeed_notify("N");

				onelv1.seteId(eId);

				String opNO = req.getOpNO();

				String o_opName = req.getOpName();

				onelv1.setOpNo(opNO);
				onelv1.setOpName(o_opName);
				onelv1.setOrderNo(orderNo);
				onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
				onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());

				String statusType = "99";// 其他状态
				String updateStaus = "99";// 订单修改

				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				
				String statusName = "商品资料异常处理";
				String statusTypeName = "其他状态";
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				StringBuffer memo_s = new StringBuffer("");
				memo_s.append( statusTypeName + "-->" + statusName + "<br>");
				memo_s.append( log_Memo.toString());
				onelv1.setMemo(memo_s.toString());
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet_s = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
				if (nRet_s)
				{
					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else
				{
					HelpTools.writelog_waimai(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();

			} catch (Exception e)
			{
				HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNO:" + orderNo);
			}
			// endregion
			return nRet;
			
		}
		else
		{
			String langType = "zh_CN";
			nRet = true;
			//所有执行的sql
			ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//数据库DATE类型
			
			boolean allMatch = false;
			if(matchItemList.size()==goodsList.size())
			{
				allMatch = true;
			}
			
			for (levelGoods par : matchItemList)
			{				
				String pluBarcode = par.getPluBarcode();
				String sql = "SELECT A.PLUBARCODE,A.PLUNO,A.UNIT,A.FEATURENO,FL.FEATURENAME,UL.UNAME FROM DCP_GOODS_BARCODE A "
						+ " left join  DCP_GOODS_FEATURE_LANG FL on A.EID =FL.EID AND A.PLUNO=FL.PLUNO AND A.FEATURENO=FL.FEATURENO and FL.Lang_Type='"+langType+"' "
						+ " left join dcp_unit_lang UL on A.EID =UL.EID AND A.UNIT=UL.UNIT and UL.Lang_Type='"+langType+"' "
						+ " where  A.EID='"+eId+"' and A.plubarcode ='"+pluBarcode+"' ";
				HelpTools.writelog_waimai(logStart+"【获取商品映射资料】循环开始，查询资料sql="+ sql);
				
				List<Map<String, Object>> getPluInfo = this.doQueryData(sql, null);
				if(getPluInfo==null||getPluInfo.isEmpty())
				{
					allMatch = false;
					continue;
				}
				String pluNo = getPluInfo.get(0).get("PLUNO").toString();
				String featureNo = getPluInfo.get(0).get("FEATURENO").toString();
				String featureName = getPluInfo.get(0).get("FEATURENAME").toString();
				String unit = getPluInfo.get(0).get("UNIT").toString();
				String unitName = getPluInfo.get(0).get("UNAME").toString();
				
				if(featureNo==null||featureNo.isEmpty())
				{
					featureNo = " ";
				}
				
				HelpTools.writelog_waimai(logStart+"【获取商品映射资料】循环，条码pluBarcode="+ pluBarcode+",对应pluNo="+pluNo+"，对应featureNo="+featureNo+",对应featureName="+featureName+",对应unit="+unit+",对应unitName="+unitName);
				
				UptBean up_orderDetail = new UptBean("DCP_ORDER_DETAIL");
				up_orderDetail.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				up_orderDetail.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				up_orderDetail.addCondition("ITEM", new DataValue(par.getItem(), Types.VARCHAR));
		    	
				up_orderDetail.addUpdateValue("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				up_orderDetail.addUpdateValue("PLUBARCODE", new DataValue(pluBarcode, Types.VARCHAR));
				up_orderDetail.addUpdateValue("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
				up_orderDetail.addUpdateValue("FEATURENAME", new DataValue(featureName, Types.VARCHAR));
				up_orderDetail.addUpdateValue("SUNIT", new DataValue(unit, Types.VARCHAR));
				up_orderDetail.addUpdateValue("SUNITNAME", new DataValue(unitName, Types.VARCHAR));
				DataPB.add(new DataProcessBean(up_orderDetail));
				
				
				
				UptBean up2 = new UptBean("DCP_ORDER_ABNORMALINFO_DETAIL");
			    up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			    up2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			    up2.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
			    up2.addCondition("OITEM", new DataValue(par.getItem(), Types.VARCHAR));
			    
			    up2.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
			    DataPB.add(new DataProcessBean(up2));
			}
			
			if(allMatch)
			{
				if(!isHashOtherException)
		    	{
					UptBean up_orderHead = new UptBean("DCP_ORDER");
			    	up_orderHead.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			    	up_orderHead.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			    	
			    	up_orderHead.addUpdateValue("EXCEPTIONSTATUS", new DataValue("N", Types.VARCHAR));
			    	up_orderHead.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			    	up_orderHead.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			    	DataPB.add(new DataProcessBean(up_orderHead));
		    	}
				
		    	
		    	UptBean up1 = new UptBean("DCP_ORDER_ABNORMALINFO");
			    up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			    up1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			    up1.addCondition("ABNORMALTYPE", new DataValue(abnormalType, Types.VARCHAR));
			    
			    up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));	
			    up1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
			    DataPB.add(new DataProcessBean(up1));
			}
			
			for (DataProcessBean dataProcessBean : DataPB) 
			{
				this.addProcessData(dataProcessBean);			
			}	
			this.doExecuteDataToDB();
			HelpTools.writelog_waimai(logStart+"异常处理结束！");	
			
			// region 写下日志
			try
			{
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

				orderStatusLog onelv1 = new orderStatusLog();

				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(channelId);

				onelv1.setNeed_callback("N");
				onelv1.setNeed_notify("N");

				onelv1.seteId(eId);

				String opNO = req.getOpNO();

				String o_opName = req.getOpName();

				onelv1.setOpNo(opNO);
				onelv1.setOpName(o_opName);
				onelv1.setOrderNo(orderNo);
				onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
				onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());

				String statusType = "99";// 其他状态
				String updateStaus = "99";// 订单修改

				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);

				String statusName = "商品资料异常处理";
				String statusTypeName = "其他状态";
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				StringBuffer memo_s = new StringBuffer("");
				memo_s .append( statusTypeName + "-->" + statusName + "<br>");
				memo_s .append( log_Memo.toString());
				onelv1.setMemo(memo_s.toString());
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet_s = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
				if (nRet_s)
				{
					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else
				{
					HelpTools.writelog_waimai(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();

			} catch (Exception e)
			{
				HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNO:" + orderNo);
			}
			// endregion
			
			
			return nRet;
	    
		}
		
	}
	
	/**
	 * 重新生成订单商品单身、折扣档、单身备注sql语句
	 * @param par
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	private ArrayList<DataProcessBean> GetInsertOrderGoodsItemList(order par,StringBuffer errorMessage) throws Exception
	{
		if(errorMessage==null)
		{
			errorMessage = new StringBuffer();
		}
		
		ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
		
		String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String curTime = new SimpleDateFormat("HHmmss").format(new Date());
		String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//数据库DATE类型
		
		String eId = par.geteId();
		String orderNo = par.getOrderNo();//订单中心生成的订单号=外部传入的单号
		String loadDocType = par.getLoadDocType();//渠道类型
		String channelId = par.getChannelId();//渠道编码
		String shop = par.getShopNo();
				
		/**********************************订单商品明细*********************************/
		List<orderGoodsItem> goodsItemList = par.getGoodsList();
		if(goodsItemList!=null)
		{

			String[] columns_goods =
				{ "eid", "orderno", "item", "loaddoctype", "channelid", "pluno", "pluname", "plubarcode", "featureno",
						"featurename", "goodsurl", "specname", "attrname", "sunit", "sunitname", "warehouse",
						"warehousename", "skuid", "gift", "giftsourceserialno", "giftreason", "goodsgroup", "packagetype",
						"packagemitem", "toppingtype", "toppingmitem", "oitem", "pickqty", "rqty", "rcqty", "shopqty",
						"boxnum", "boxprice", "qty", "oldprice", "oldamt", "price", "disc", "amt", "incltax", "taxcode",
						"taxtype", "invitem","invsplittype", "sellerno", "sellername", "accno", "counterno", "coupontype", "couponcode",
						"sourcecode", "ismemo", "stime","VIRTUAL","DISC_MERRECEIVE","AMT_MERRECEIVE",
						"DISC_CUSTPAYREAL","AMT_CUSTPAYREAL","PARTITION_DATE","SPECNAME_ORIGIN","ATTRNAME_ORIGIN" };

			for (orderGoodsItem goodsItem : goodsItemList)
			{
				//数据库长度截取
				try
				{
					if(goodsItem.getPluNo()!=null&&goodsItem.getPluNo().length()>40)
					{
						goodsItem.setPluNo(goodsItem.getPluNo().substring(0,40));
					}
					if(goodsItem.getPluBarcode()!=null&&goodsItem.getPluBarcode().length()>40)
					{
						goodsItem.setPluBarcode(goodsItem.getPluBarcode().substring(0,40));
					}
					if(goodsItem.getPluName()!=null&&goodsItem.getPluName().length()>120)
					{
						goodsItem.setPluName(goodsItem.getPluName().substring(0,120));
					}
					if(goodsItem.getSkuId()!=null&&goodsItem.getSkuId().length()>120)
					{
						goodsItem.setSkuId(goodsItem.getSkuId().substring(0,120));
					}
					if(goodsItem.getsUnit()!=null&&goodsItem.getsUnit().length()>32)
					{
						goodsItem.setsUnit(goodsItem.getsUnit().substring(0,32));
					}
					if(goodsItem.getsUnitName()!=null&&goodsItem.getsUnitName().length()>100)
					{
						goodsItem.setsUnitName(goodsItem.getsUnitName().substring(0,100));
					}
					if(goodsItem.getFeatureNo()!=null&&goodsItem.getFeatureNo().length()>64)
					{
						goodsItem.setFeatureNo(goodsItem.getFeatureNo().substring(0,64));
					}
					if(goodsItem.getFeatureName()!=null&&goodsItem.getFeatureName().length()>64)
					{
						goodsItem.setFeatureName(goodsItem.getFeatureName().substring(0,64));
					}
					if(goodsItem.getSpecName()!=null&&goodsItem.getSpecName().length()>120)
					{
						goodsItem.setSpecName(goodsItem.getSpecName().substring(0,120));
					}
					if(goodsItem.getAttrName()!=null&&goodsItem.getAttrName().length()>120)
					{
						goodsItem.setAttrName(goodsItem.getAttrName().substring(0,120));
					}
					if(goodsItem.getSpecName_origin()!=null&&goodsItem.getSpecName_origin().length()>120)
					{
						goodsItem.setSpecName_origin(goodsItem.getSpecName_origin().substring(0,120));
					}
					if(goodsItem.getAttrName_origin()!=null&&goodsItem.getAttrName_origin().length()>120)
					{
						goodsItem.setAttrName_origin(goodsItem.getAttrName_origin().substring(0,120));
					}
					//除了饿了么，其他的都是等于自己
					if (!orderLoadDocType.ELEME.equals(loadDocType))
					{
						goodsItem.setSpecName_origin(goodsItem.getSpecName());
						goodsItem.setAttrName_origin(goodsItem.getAttrName());
					}
					if(goodsItem.getWarehouse()!=null&&goodsItem.getWarehouse().length()>32)
					{
						goodsItem.setWarehouse(goodsItem.getWarehouse().substring(0,32));
					}
					if(goodsItem.getWarehouseName()!=null&&goodsItem.getWarehouseName().length()>64)
					{
						goodsItem.setWarehouseName(goodsItem.getWarehouseName().substring(0,64));
					}
					if(goodsItem.getSellerNo()!=null&&goodsItem.getSellerNo().length()>32)
					{
						goodsItem.setSellerNo(goodsItem.getSellerNo().substring(0,32));
					}
					if(goodsItem.getSellerName()!=null&&goodsItem.getSellerName().length()>64)
					{
						goodsItem.setSellerName(goodsItem.getSellerName().substring(0,64));
					}
				} 
				catch (Exception e)
				{
					// TODO: handle exception
				}
				
				
				
				goodsItem.setIsMemo("N");
				List<orderGoodsItemMessage> goodsMemoList = goodsItem.getMessages();
				if(goodsMemoList!=null&&goodsMemoList.size()>0)
				{
					goodsItem.setIsMemo("Y");
				}

				String featureNo = goodsItem.getFeatureNo();
				if(featureNo==null||featureNo.isEmpty())
				{
					featureNo = " ";//默认空格
				}
				String virtual = goodsItem.getVirtual();
				if(virtual==null||virtual.equals("Y")==false)
				{
					virtual = "N";
				}

				DataValue[] insValue_good = new DataValue[] {
						new DataValue(eId, Types.VARCHAR),							
						new DataValue(orderNo, Types.VARCHAR),
						new DataValue(goodsItem.getItem(), Types.VARCHAR),
						new DataValue(loadDocType, Types.VARCHAR),
						new DataValue(channelId, Types.VARCHAR),
						new DataValue(goodsItem.getPluNo(), Types.VARCHAR),
						new DataValue(goodsItem.getPluName(), Types.VARCHAR),
						new DataValue(goodsItem.getPluBarcode(), Types.VARCHAR),
						new DataValue(featureNo, Types.VARCHAR),
						new DataValue(goodsItem.getFeatureName(), Types.VARCHAR),//ordershop
						new DataValue(goodsItem.getGoodsUrl(), Types.VARCHAR),//ordershopname
						new DataValue(goodsItem.getSpecName(), Types.VARCHAR),
						new DataValue(goodsItem.getAttrName(), Types.VARCHAR),
						new DataValue(goodsItem.getsUnit(), Types.VARCHAR),
						new DataValue(goodsItem.getsUnitName(), Types.VARCHAR),
						new DataValue(goodsItem.getWarehouse(), Types.VARCHAR),//warehouse
						new DataValue(goodsItem.getWarehouseName(), Types.VARCHAR),//warehousename
						new DataValue(goodsItem.getSkuId(), Types.VARCHAR),
						new DataValue(goodsItem.getGift(), Types.VARCHAR),
						new DataValue(goodsItem.getGiftSourceSerialNo(), Types.VARCHAR),
						new DataValue(goodsItem.getGiftReason(), Types.VARCHAR),
						new DataValue(goodsItem.getGoodsGroup(), Types.VARCHAR),
						new DataValue(goodsItem.getPackageType(), Types.VARCHAR),
						new DataValue(goodsItem.getPackageMitem(), Types.VARCHAR),
						new DataValue(goodsItem.getToppingType(), Types.VARCHAR),
						new DataValue(goodsItem.getToppingMitem(), Types.VARCHAR),
						new DataValue(goodsItem.getoItem(), Types.VARCHAR),
						new DataValue(goodsItem.getPickQty(), Types.VARCHAR),
						new DataValue(goodsItem.getrQty(), Types.VARCHAR),//rqty
						new DataValue("0", Types.VARCHAR),//rcqty
						new DataValue(goodsItem.getShopQty(), Types.VARCHAR),
						new DataValue(goodsItem.getBoxNum(), Types.VARCHAR),
						new DataValue(goodsItem.getBoxPrice(), Types.VARCHAR),
						new DataValue(goodsItem.getQty(), Types.VARCHAR),
						new DataValue(goodsItem.getOldPrice(), Types.VARCHAR),
						new DataValue(goodsItem.getOldAmt(), Types.VARCHAR),
						new DataValue(goodsItem.getPrice(), Types.VARCHAR),//price
						new DataValue(goodsItem.getDisc(), Types.VARCHAR),
						new DataValue(goodsItem.getAmt(), Types.VARCHAR),
						new DataValue(goodsItem.getInclTax(), Types.VARCHAR),
						new DataValue(goodsItem.getTaxCode(), Types.VARCHAR),
						new DataValue(goodsItem.getTaxType(), Types.VARCHAR),															
						new DataValue("0", Types.VARCHAR),//invitem
						new DataValue(goodsItem.getInvSplitType(), Types.VARCHAR),//invsplittype																													
						new DataValue(goodsItem.getSellerNo(), Types.VARCHAR),
						new DataValue(goodsItem.getSellerName(), Types.VARCHAR),
						new DataValue(goodsItem.getAccNo(), Types.VARCHAR),
						new DataValue(goodsItem.getCounterNo(), Types.VARCHAR),
						new DataValue(goodsItem.getCouponType(), Types.VARCHAR),
						new DataValue(goodsItem.getCouponCode(), Types.VARCHAR),
						new DataValue("", Types.VARCHAR),//sourcecode
						new DataValue(goodsItem.getIsMemo(), Types.VARCHAR),
						new DataValue(curDateTime, Types.VARCHAR),	
						new DataValue(virtual, Types.VARCHAR),
						new DataValue(goodsItem.getDisc_merReceive(), Types.VARCHAR),
						new DataValue(goodsItem.getAmt_merReceive(), Types.VARCHAR),
						new DataValue(goodsItem.getDisc_custPayReal(), Types.VARCHAR),
						new DataValue(goodsItem.getAmt_custPayReal(), Types.VARCHAR),
						new DataValue(par.getbDate(), Types.NUMERIC),//分区字段
						new DataValue(goodsItem.getSpecName_origin(), Types.VARCHAR),
						new DataValue(goodsItem.getAttrName_origin(), Types.VARCHAR),
				};

				InsBean ib_goods = new InsBean("DCP_ORDER_DETAIL", columns_goods);//分区字段已处理
				ib_goods.addValues(insValue_good);
				DataPB.add(new DataProcessBean(ib_goods));	


				/*******************商品备注************************/
				if(goodsMemoList!=null&&goodsMemoList.size()>0)
				{

					String[] columns_goodsMemo =
						{ "eid", "orderno", "SHOPID", "OITEM", "ITEM", "MEMONAME","MEMOTYPE","MEMO"};

					int goodsMemoItem = 0;
					for (orderGoodsItemMessage  goodsMessage : goodsMemoList)
					{
						try
						{
							if(goodsMessage.getMsgType()!=null&&goodsMessage.getMsgType().length()>10)
							{
								goodsMessage.setMsgType(goodsMessage.getMsgType().substring(0,10));
							}
							if(goodsMessage.getMsgName()!=null&&goodsMessage.getMsgName().length()>255)
							{
								goodsMessage.setMsgName(goodsMessage.getMsgName().substring(0,255));
							}
							if(goodsMessage.getMessage()!=null&&goodsMessage.getMessage().length()>255)
							{
								goodsMessage.setMessage(goodsMessage.getMessage().substring(0,255));
							}
						} 
						catch (Exception e)
						{
							// TODO: handle exception
						}
						
						goodsMemoItem++;
						DataValue[] insValue_goodsMemo = new DataValue[] {
								new DataValue(eId, Types.VARCHAR),							
								new DataValue(orderNo, Types.VARCHAR),
								new DataValue(shop, Types.VARCHAR),
								new DataValue(goodsItem.getItem(), Types.VARCHAR),
								new DataValue(goodsMemoItem, Types.VARCHAR),
								new DataValue(goodsMessage.getMsgName(), Types.VARCHAR),
								new DataValue(goodsMessage.getMsgType(), Types.VARCHAR),
								new DataValue(goodsMessage.getMessage(), Types.VARCHAR),
						};

						InsBean ib_goodsMemo = new InsBean("DCP_ORDER_DETAIL_MEMO", columns_goodsMemo);
						ib_goodsMemo.addValues(insValue_goodsMemo);
						DataPB.add(new DataProcessBean(ib_goodsMemo));
					}
				}

				/*********************商品折扣**************************/
				List<orderGoodsItemAgio> goodsAgioList = goodsItem.getAgioInfo();

				if(goodsAgioList!=null&&goodsAgioList.size()>0)
				{
					int goodsAgioItem = 0;
					String[] columns_goodsAgio =
						{ "eid", "orderno", "MITEM", "ITEM", "QTY", "AMT","INPUTDISC","REALDISC","DISC","DCTYPE","DCTYPENAME","PMTNO","GIFTCTF","GIFTCTFNO","BSNO",
								"DISC_MERRECEIVE","DISC_CUSTPAYREAL","PARTITION_DATE"};
					for (orderGoodsItemAgio agio : goodsAgioList)
					{
						try
						{
							if(agio.getDcType()!=null&&agio.getDcType().length()>32)
							{
								agio.setDcType(agio.getDcType().substring(0,32));
							}
							if(agio.getDcTypeName()!=null&&agio.getDcTypeName().length()>64)
							{
								agio.setDcTypeName(agio.getDcTypeName().substring(0,64));
							}
							if(agio.getPmtNo()!=null&&agio.getPmtNo().length()>32)
							{
								agio.setPmtNo(agio.getPmtNo().substring(0,32));
							}
							if(agio.getGiftCtf()!=null&&agio.getGiftCtf().length()>32)
							{
								agio.setGiftCtf(agio.getGiftCtf().substring(0,32));
							}
							if(agio.getGiftCtfNo()!=null&&agio.getGiftCtfNo().length()>32)
							{
								agio.setGiftCtfNo(agio.getGiftCtfNo().substring(0,32));
							}
							if(agio.getBsNo()!=null&&agio.getBsNo().length()>32)
							{
								agio.setBsNo(agio.getBsNo().substring(0,32));
							}
							
						} 
						catch (Exception e)
						{
							// TODO: handle exception
						}
						goodsAgioItem++;
						DataValue[] insValue_goodsAgio = new DataValue[] {
								new DataValue(eId, Types.VARCHAR),							
								new DataValue(orderNo, Types.VARCHAR),									
								new DataValue(goodsItem.getItem(), Types.VARCHAR),
								new DataValue(goodsAgioItem, Types.VARCHAR),
								new DataValue(agio.getQty(), Types.VARCHAR),
								new DataValue(agio.getAmt(), Types.VARCHAR),
								new DataValue(agio.getInputDisc(), Types.VARCHAR),
								new DataValue(agio.getRealDisc(), Types.VARCHAR),
								new DataValue(agio.getDisc(), Types.VARCHAR),
								new DataValue(agio.getDcType(), Types.VARCHAR),
								new DataValue(agio.getDcTypeName(), Types.VARCHAR),
								new DataValue(agio.getPmtNo(), Types.VARCHAR),
								new DataValue(agio.getGiftCtf(), Types.VARCHAR),
								new DataValue(agio.getGiftCtfNo(), Types.VARCHAR),
								new DataValue(agio.getBsNo(), Types.VARCHAR),
								new DataValue(agio.getDisc_merReceive(), Types.VARCHAR),
								new DataValue(agio.getDisc_custPayReal(), Types.VARCHAR),
								new DataValue(par.getbDate(), Types.NUMERIC),//分区字段
						};

						InsBean ib_goodsAgio = new InsBean("DCP_ORDER_DETAIL_AGIO", columns_goodsAgio);//分区字段已处理
						ib_goodsAgio.addValues(insValue_goodsAgio);
						DataPB.add(new DataProcessBean(ib_goodsAgio));

					}

				}
			}



		}
		
		return DataPB;
	}

	private boolean orderToSaleProcess(DCP_AbnormalOrderProcessReq req,StringBuffer error) throws Exception
	{
		if(error==null)
		{
			error = new StringBuffer("");
		}
		boolean nRet = false;
		String eId = req.getRequest().geteId();
		String orderNo = req.getRequest().getOrderNo();
		String abnormalType = req.getRequest().getAbnormalType();

		String antoOrderToSale = req.getRequest().getAutoOrderToSale();
		if (antoOrderToSale==null||antoOrderToSale.isEmpty())
		{
			antoOrderToSale = "N";
		}
		String logStart = "异常处理结束后,自动订转销，单号orderNo="+orderNo+",";
		HelpTools.writelog_waimai(logStart+"循环匹配【开始】");
		if (antoOrderToSale.equals("Y")==false)
		{
			HelpTools.writelog_waimai(logStart+"传参antoOrderToSale="+antoOrderToSale+"，无需处理!");
			return  true;
		}
		//查询下订单状态
		String sql_head = "select * from dcp_order where orderno='"+orderNo+"' and eid='"+eId+"' ";

		List<Map<String, Object>> getOrderHead = dao.executeQuerySQL(sql_head, null);
		if(getOrderHead==null||getOrderHead.isEmpty())
		{
			HelpTools.writelog_waimai(logStart+"订单不存在，无需处理!");
			return true;
		}
		Map<String, Object> map = getOrderHead.get(0);
		String loadDocType =map.get("LOADDOCTYPE").toString();
		String channelId = map.get("CHANNELID").toString();
		String loadDocOrderNo = map.get("LOADDOCORDERNO").toString();
		String loadDocBillType = map.get("LOADDOCBILLTYPE").toString();
		String status =  map.get("STATUS").toString();
		String refundStatus =  map.get("REFUNDSTATUS").toString();
		String shopNo = map.get("SHOP").toString();
		if (status.equals("3")||status.equals("12"))
		{
			HelpTools.writelog_waimai(logStart+"订单状态status="+status+"，无需处理!");
			return false;
		}
		

		//查询下有没有生成销售单
		String sql = "select  SALENO,SHOPID from dcp_sale where otype=3 and ofno='"+orderNo+"' and eid='"+eId+"' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		HelpTools.writelog_waimai(logStart+"有没有生成销售单，查询sql:");
		if(getQData==null||getQData.isEmpty())
		{
		}
		else
		{
			HelpTools.writelog_waimai(logStart+"订单已生成销售单，无需处理!");
			return true;
		}
		DCP_OrderShippingReq req_sale = new  DCP_OrderShippingReq();
		DCP_OrderShippingReq.levelRequest req_sale_requeset = req_sale.new levelRequest();
		req_sale_requeset.setOpType("1");
		req_sale_requeset.setOrderList(new String[] {orderNo});
		Map<String,Object> jsonMap=new HashMap<String,Object>();
		jsonMap.put("serviceId", "DCP_OrderShipping");
		//这个token是无意义的
		jsonMap.put("token", req.getToken());
		jsonMap.put("request", req_sale_requeset);
		ParseJson pj=new ParseJson();
		String json_ship = pj.beanToJson(jsonMap);
		HelpTools.writelog_waimai(logStart+"出貨訂轉銷請求： "+ json_ship);

		DispatchService ds = DispatchService.getInstance();
		String resbody_ship = ds.callService(json_ship, this.dao);

		HelpTools.writelog_waimai(logStart+"出貨訂轉銷返回： "+ resbody_ship);
		if (resbody_ship.equals("")==false)
		{
			JSONObject jsonres_ship = new JSONObject(resbody_ship);
			boolean success = jsonres_ship.getBoolean("success");
			if (success)
			{
				nRet = true;
			}
			else
			{
				nRet = false;
				error.append(jsonres_ship.optString("serviceDescription",""));
			}

		}
		else
		{
			nRet = false;
			error.append("调用DCP_OrderShipping接口返回为空！");
		}

		// region 写下日志
		try
		{
			List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

			orderStatusLog onelv1 = new orderStatusLog();

			onelv1.setLoadDocType(loadDocType);
			onelv1.setChannelId(channelId);

			onelv1.setNeed_callback("N");
			onelv1.setNeed_notify("N");

			onelv1.seteId(eId);

			String opNO = req.getOpNO();

			String o_opName = req.getOpName();

			onelv1.setOpNo(opNO);
			onelv1.setOpName(o_opName);
			onelv1.setOrderNo(orderNo);
			onelv1.setLoadDocBillType(loadDocBillType);
			onelv1.setLoadDocOrderNo(loadDocOrderNo);

			String statusType = "99";// 其他状态
			String updateStaus = "99";// 订单修改

			onelv1.setStatusType(statusType);
			onelv1.setStatus(updateStaus);

			String statusName = "商品资料异常处理后订转销";
			String statusTypeName = "其他状态";
			onelv1.setStatusTypeName(statusTypeName);
			onelv1.setStatusName(statusName);

			StringBuffer memo_s = new StringBuffer("");
			memo_s .append( statusTypeName + "-->订转销<br>");
			if (nRet)
			{
				memo_s .append( "订转销成功！");
			}
			else
			{
				memo_s .append( "订转销失败:");
				memo_s .append( error.toString());;
			}

			onelv1.setMemo(memo_s.toString());
			String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			onelv1.setUpdate_time(updateDatetime);
			orderStatusLogList.add(onelv1);

			StringBuilder errorMessage = new StringBuilder();
			boolean nRet_s = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
			if (nRet_s)
			{
				HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
			} else
			{
				HelpTools.writelog_waimai(
						"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
			}
			this.pData.clear();

		} catch (Exception e)
		{
			HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNO:" + orderNo);
		}
		// endregion



		return  nRet;
	}

	/**
	 * 查询下关联的退单商品异常的也修复下
	 * @param eId
	 * @param originOrderNo
	 * @throws Exception
	 */
	private void updateRefundOrder(String eId,String originOrderNo) throws Exception
	{
		String logFileName = "DCP_AbnormalOrderProcess";
		try
		{

			String sql = " select ORDERNO from dcp_order where billtype='-1' and EXCEPTIONSTATUS='Y' and eid='"+eId+"' and refundsourcebillno='"+originOrderNo+"'";
			HelpTools.writelog_fileName("【关联退单异常处理】单号orderNo="+originOrderNo+",异常处理成功后，查询下是否存在该订单关联的商品异常退单sql:"+sql,logFileName);
			List<Map<String, Object>> getRefundOrderList = this.doQueryData(sql, null);
			if (getRefundOrderList==null||getRefundOrderList.isEmpty())
			{
				HelpTools.writelog_fileName("【关联退单异常处理】单号orderNo="+originOrderNo+",异常处理成功后，查询是否存在该订单关联的商品异常退单数据为空，无需处理",logFileName);
				return;
			}
			for (Map<String, Object> mapOrder : getRefundOrderList)
			{
				String orderNo = "";
				try
				{
					ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
					orderNo = mapOrder.get("ORDERNO").toString();
					HelpTools.writelog_fileName("【关联退单异常处理】原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常处理开始",logFileName);
					String sql_abnormal = " select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderNo+"'"
							            +" and OITEM in "
							            +" (select OITEM from DCP_ORDER_ABNORMALINFO_DETAIL  where eid='"+eId+"' and orderno='"+originOrderNo+"')";
					List<Map<String, Object>> getRefund_AbnormalDetails = this.doQueryData(sql_abnormal, null);
					if (getRefund_AbnormalDetails==null||getRefund_AbnormalDetails.isEmpty())
					{
						HelpTools.writelog_fileName("【关联退单异常处理】原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常数据没有，直接更新订单异常标记EXCEPTIONSTATUS=N",logFileName);
						UptBean up_orderHead = new UptBean("DCP_ORDER");
						up_orderHead.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						up_orderHead.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
						up_orderHead.addUpdateValue("EXCEPTIONSTATUS", new DataValue("N", Types.VARCHAR));
						DataPB.add(new DataProcessBean(up_orderHead));
					}
					else
					{
						sql_abnormal = "";
						sql_abnormal = " select * from DCP_ORDER_ABNORMALINFO_DETAIL where eid='"+eId+"' and orderno='"+originOrderNo+"'";
						List<Map<String, Object>> getOrigin_AbnormalDetails = this.doQueryData(sql_abnormal, null);
						if (getOrigin_AbnormalDetails==null||getOrigin_AbnormalDetails.isEmpty())
						{
							continue;
							//HelpTools.writelog_fileName("原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常数据没有，直接更新订单异常标记EXCEPTIONSTATUS=N",logFileName);
						}
						sql_abnormal = "";
						sql_abnormal = " select * from DCP_ORDER_DETAIL where eid='"+eId+"' and orderno='"+originOrderNo+"'";
						List<Map<String, Object>> getOrderDetail = this.doQueryData(sql_abnormal, null);
						if (getOrderDetail==null||getOrderDetail.isEmpty())
						{
							continue;
							//HelpTools.writelog_fileName("原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常数据没有，直接更新订单异常标记EXCEPTIONSTATUS=N",logFileName);
						}
						boolean isAllProcess = true;//是否都处理完成
						List<String> oitemList = new ArrayList<>();
						for (Map<String, Object> par_refund : getRefund_AbnormalDetails)
						{
							String oitem_refund = par_refund.get("OITEM").toString();
							for (Map<String, Object> par_origin : getOrigin_AbnormalDetails)
							{
								String oitem_origin = par_origin.get("OITEM").toString();
								String status_origin = par_origin.get("STATUS").toString();
								if (oitem_refund.equals(oitem_origin))
								{
									if ("100".equals(status_origin))
									{
										oitemList.add(oitem_refund);
										String execSql = " update dcp_order_detail  set (PLUNO,PLUBARCODE,FEATURENO,FEATURENAME,SUNIT,SUNITNAME)="
												       +" (select PLUNO,PLUBARCODE,FEATURENO,FEATURENAME,SUNIT,SUNITNAME from dcp_order_detail where eid='"+eId+"' and orderno='"+originOrderNo+"' and item="+oitem_origin+")"
												       +" where eid='"+eId+"' and orderno='"+orderNo+"' and oitem="+oitem_origin;
										HelpTools.writelog_fileName("【循环添加更新商品sql语句】原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常数据更新sql语句:"+execSql,logFileName);
										ExecBean execBean1 = new ExecBean(execSql);
										DataPB.add(new DataProcessBean(execBean1));
									}
									else
									{
										isAllProcess = false;
									}
									break;
								}
							}

						}

						if (oitemList.size()==0)
						{
							continue;
						}
						if (isAllProcess)
						{
							UptBean up_orderHead = new UptBean("DCP_ORDER");
							up_orderHead.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							up_orderHead.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							up_orderHead.addUpdateValue("EXCEPTIONSTATUS", new DataValue("N", Types.VARCHAR));
							DataPB.add(new DataProcessBean(up_orderHead));
							HelpTools.writelog_fileName("【添加更新订单异常标记sql语句】原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常数据全部修复后，更新订单异常标记EXCEPTIONSTATUS=N",logFileName);
						}

					}

					if (DataPB.size()>0)
					{
						this.dao.useTransactionProcessData(DataPB);
						HelpTools.writelog_fileName("【关联退单异常处理】原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常处理成功",logFileName);
					}
				}
				catch (Exception e)
				{
					HelpTools.writelog_fileName("【关联退单异常处理】原订单orderNo="+originOrderNo+",异常处理成功后，关联退单单号orderNo="+orderNo+"，商品异常处理异常:"+e.getMessage(),logFileName);
				}
			}

		}
		catch (Exception e)
		{

		}
	}
}
