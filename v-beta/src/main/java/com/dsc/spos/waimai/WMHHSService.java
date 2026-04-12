package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.DeliverooAsynExecutor;

//台湾外卖deliveroo==户户送
public class WMHHSService extends SWaimaiBasicService
{

	@Override
	public String execute(String json) throws Exception 
	{
		//订单付款方式映射,目前deliveroo没有推送付款方式，取当前第一笔
		String companyNO="99";//================后续需处理
		String sqlTvMappingPay="select * from tv_mappingpayment t where t.load_doctype='37' and t.cnfflg='Y' and t.companyno='"+companyNO+"' ";
		//查询付款方式映射
		List<Map<String, Object>> sqllistPaymapping=this.doQueryData(sqlTvMappingPay, null);		

		String res_json = HelpTools.GetHHSResponse(json,sqllistPaymapping);
		if(res_json ==null ||res_json.length()==0)
		{
			return null;
		}
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(res_json, res);		

		return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception 
	{
		try 
		{
			JSONObject obj = new JSONObject(req);
			String orderstatus = obj.get("status").toString();//订单状态	转成我们自己的状态 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单	
			String orderNO = obj.get("orderNO").toString();
			String refundStatus = obj.get("refundStatus").toString();
			String companyNO = obj.get("companyNO").toString();		
			String shopNO = obj.get("shopNO").toString();
			String organizationNO = shopNO;
			String loadDocType = obj.get("loadDocType").toString();//
			if(orderstatus !=null)
			{
				if(orderstatus.equals("2"))//2.已接单 
				{
					//重复推送处理
					Map<String, Object> orderHead = new HashMap<String, Object>();
					boolean IsExistOrder = IsExistOrder(companyNO, organizationNO, shopNO, orderNO,loadDocType,orderHead);
					if(IsExistOrder==false)
					{
						ArrayList<DataProcessBean> DPB = null;// HelpTools.GetInsertOrder(obj);
						if (DPB != null && DPB.size() > 0)
						{
							for (DataProcessBean dataProcessBean : DPB) 
							{
								this.addProcessData(dataProcessBean);			
							}					

							try 
							{
								this.doExecuteDataToDB();
								HelpTools.writelog_waimai("【deliveroo户户送保存数据库成功】"+" 订单号orderNO:"+orderNO);

							} 
							catch (Exception e) 
							{								
								HelpTools.writelog_waimai("【deliveroo户户送保存数据库成功】"+" 订单号orderNO:"+orderNO);
							}

							//订单追踪  1、订单创建时间  2、订单接单时间  3、订单拒单时间  4、生产接单时间  5、完工入库时间  6、订单调拨时间  7、订单配送时间  8、订单完成时间 9、订单退订时间
							//InsertOrderTrack(obj.get("companyNO").toString(),obj.get("shopNO").toString(),orderNO,"2","1");

							//写订单日志
							/*OrderStatusLogCreateReq req_log = new OrderStatusLogCreateReq();
							req_log.setDatas(new ArrayList<OrderStatusLogCreateReq.level1Elm>());
							OrderStatusLogCreateReq.level1Elm onelv1 = new OrderStatusLogCreateReq().new level1Elm();
							onelv1.setCallback_status("0");
							onelv1.setLoadDocType("37");//37:deliveroo户户送

							onelv1.setNeed_callback("N");
							onelv1.setNeed_notify("Y");
							String o_companyNO = obj.get("companyNO").toString();

							onelv1.setO_companyNO(o_companyNO);
							onelv1.setO_opName("deliveroo户户送");
							onelv1.setO_opNO("");
							String o_shopNO = obj.get("shopNO").toString();
							onelv1.setO_organizationNO(o_shopNO);
							onelv1.setO_shopNO(o_shopNO);
							onelv1.setOrderNO(orderNO);

							String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
							onelv1.setUpdate_time(updateDatetime);

							String statusType = "1";
							String updateStaus = orderstatus;

							onelv1.setStatusType(statusType);				 					
							onelv1.setStatus(updateStaus);

							StringBuilder statusTypeNameObj = new StringBuilder();
							String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
							String statusTypeName = statusTypeNameObj.toString();
							onelv1.setStatusTypeName(statusTypeName);
							onelv1.setStatusName(statusName);

							String memo = "";
							memo += statusName;
							onelv1.setMemo(memo);
							onelv1.setDisplay("1");


							req_log.getDatas().add(onelv1);

							ParseJson pj = new ParseJson();

							String req_log_json = pj.beanToJson(req_log);
							pj=null;

							StringBuilder errorMessage = new StringBuilder();
							boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, req_log_json, errorMessage);
							if(nRet)
							{		  		 
								HelpTools.writelog_waimai("【deliveroo户户送，写表tv_orderStatuslog保存成功】"+" 订单号orderNO:"+orderNO);
							}
							else
							{			  		 
								HelpTools.writelog_waimai("【deliveroo户户送，写表tv_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
							}
*/

							//同步订单成功状态给平台
							DeliverooAsynExecutor deliveroo=new DeliverooAsynExecutor();
							String apiUrl="";
							String secret="";
							String sequence_guid="";

							deliveroo.asynTask(apiUrl, secret, sequence_guid,orderNO);							

						}
					}
				}			
				else//更新 数据库状态
				{
					HelpTools.writelog_waimai("【deliveroo户户送开始更新数据库】"+" 订单号orderNO:"+orderNO + " 订单状态status=" + orderstatus);
					UpdateOrderStatus(req);
				}

			}


		} 
		catch (SQLException e) 
		{
			HelpTools.writelog_waimai("【deliveroo户户送执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req);		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【deliveroo户户送执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req);		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}	


	private void UpdateOrderStatus(String req) throws Exception
	{
		try 
		{
			JSONObject obj = new JSONObject(req);
			String status = obj.get("status").toString();
			String refundStatus = obj.get("refundStatus").toString();
			String orderNO = obj.get("orderNO").toString();
			String companyNO = obj.get("companyNO").toString();	
			String shopNO = obj.get("shopNO").toString();
			String organizationNO = shopNO;
			String loadDocType = obj.get("loadDocType").toString();
			float partRefundAmt = 0;
			if(!obj.isNull("partRefundAmt"))
			{

				try 
				{
					partRefundAmt =Float.parseFloat(obj.get("partRefundAmt").toString());			
				}
				catch (Exception e) 
				{

				}
			}

			String reason ="";
			if(!obj.isNull("refundReason"))
			{

				reason = obj.get("refundReason").toString();
			}

			if (reason != null && reason.length() > 255) 
			{				
				reason = reason.substring(0, 255);
			}

			Map<String, Object> orderHead = new HashMap<String, Object>();
			boolean IsExistOrder = IsExistOrder(companyNO, organizationNO, shopNO, orderNO,loadDocType,orderHead);

			if(IsExistOrder)//存在就Update
			{
				if(refundStatus.equals("2")||refundStatus.equals("7"))//申请了退款，或者部分退款
				{
					try 
					{
						String status_DB =orderHead.get("STATUS").toString();
						String refundStatus_DB =orderHead.get("REFUNDSTATUS").toString();
						if(refundStatus.equals("2"))
						{
							if(refundStatus_DB.equals("6")||refundStatus_DB.equals("3")||refundStatus_DB.equals("5"))
							{
								//已经处理了的退款，删除缓存
								HelpTools.writelog_waimai("【deliveroo户户送退单重复推送退单申请】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
								String redis_key = "WMORDER" + "_" + companyNO + "_" + shopNO;							 
								String hash_key = orderNO;
								DeleteRedis(redis_key,hash_key);
								return;
							}

						}
						else
						{
							if(refundStatus_DB.equals("10")||refundStatus_DB.equals("8")||refundStatus_DB.equals("9"))
							{
								//已经处理了的部分退款，删除缓存
								HelpTools.writelog_waimai("【deliveroo户户送部分退单重复推送退单申请】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
								String redis_key = "WMORDER" + "_" + companyNO + "_" + shopNO;
								String hash_key = orderNO;
								DeleteRedis(redis_key,hash_key);
								return;
							}

						}

					} 
					catch (Exception e) 
					{

					}

				}

				UptBean ub1 = null;	
				ub1 = new UptBean("TV_ORDER");
				ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));				
				//部分退单，最后还是算已完成订单
				ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus,Types.VARCHAR));
				if(refundStatus.equals("2")||refundStatus.equals("7"))
				{
					HelpTools.writelog_fileName("【deliveroo户户送】申请退款更新数据，单号orderNO="+orderNO+" 退货原因refundReason="+reason, "refunReasonLog");
					ub1.addUpdateValue("REFUNDREASON", new DataValue(reason,Types.VARCHAR));
				}

				if(refundStatus.equals("10"))
				{
					ub1.addUpdateValue("PARTREFUNDAMT", new DataValue(partRefundAmt,Types.FLOAT));
				}			
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

				ub1.addCondition("COMPANYNO", new DataValue(companyNO, Types.VARCHAR));
				ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
				ub1.addCondition("SHOP", new DataValue(shopNO, Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
				ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));							
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();	
				HelpTools.writelog_waimai("【deliveroo户户送更新STATUS成功】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
				//同意部分退款后，再插入退款的明细商品 不用放在同一个事务里面
				if(refundStatus.equals("10"))
				{
					HelpTools.writelog_waimai("【deliveroo户户送部分退单新增单身开始】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status);
					boolean isExist = false;
					//获取下最大的单身item+1 由于之前已经查询了数据库了，
					//int maxPartRefundGoodsItem = GetMaxOrderDetailItem(companyNO, organizationNO, shopNO, orderNO,loadDocType);
					try 
					{
						//单身
						JSONArray array=	obj.getJSONArray("goods");
						for (int i = array.length()-1; i >=0; i--) 
						{
							try 
							{
								JSONObject job = array.getJSONObject(i);				
								String pluBarcode = job.get("pluBarcode").toString();
								String qty = job.get("qty").toString();
								if(Double.parseDouble(qty)>=0)//只插入负数
								{
									continue;
								}

								String pluNO = pluBarcode;//后面可能不需要
								if(!job.isNull("pluNO"))
								{
									pluNO = job.get("pluNO").toString();
								}

								String skuID = "";
								if(!job.isNull("skuID"))
								{
									skuID = job.get("skuID").toString();
								}

								String pluName = job.get("pluName").toString();
								String specName = job.get("specName").toString();					
								String attrName = job.get("attrName").toString();
								String unit = job.get("unit").toString();
								String price = job.get("price").toString();
								//String qty = job.get("qty").toString();
								String goodsGroup = job.get("goodsGroup").toString();									
								String disc = job.get("disc").toString();
								String boxNum = job.get("boxNum").toString();
								String boxPrice = job.get("boxPrice").toString();
								String amt = job.get("amt").toString();
								String isMemo = job.get("isMemo").toString();
								String item =job.get("item").toString();//重新数据库取下
								/*
								 * 数据保存时控制长度 截取
								 */
								if (goodsGroup != null && goodsGroup.length() > 10)
								{
									goodsGroup = goodsGroup.substring(0, 9);
								}
								if (pluNO != null && pluNO.length() > 40)
								{
									pluNO = pluNO.substring(0, 39);
								}
								if (pluBarcode != null && pluBarcode.length() > 40)
								{
									pluBarcode = pluBarcode.substring(0, 39);
								}
								if (pluName != null && pluName.length() > 120)
								{
									pluName = pluName.substring(0, 119);
								}
								if (specName != null && specName.length() > 120)
								{
									specName = specName.substring(0, 119);//数据库最长120
								}
								if (attrName != null && attrName.length() > 120)
								{
									attrName = attrName.substring(0, 119);//数据库最长120
								}



								String[] columns2 = {"COMPANYNO","ORGANIZATIONNO","SHOP","ORDERNO","ITEM","LOAD_DOCTYPE",
										"PLUNO","PLUBARCODE","PLUNAME","SPECNAME","ATTRNAME","UNIT",
										"PRICE","QTY","GOODSGROUP","DISC","BOXNUM","BOXPRICE","AMT","ISMEMO","SKUID","CNFFLG"};
								DataValue[] insValue2 = null;

								insValue2 = new DataValue[]{
										new DataValue(companyNO, Types.VARCHAR),
										new DataValue(shopNO, Types.VARCHAR),//组织编号=门店编号
										new DataValue(shopNO, Types.VARCHAR),//映射后的门店		
										//new DataValue(orderid, Types.VARCHAR),//
										new DataValue(orderNO, Types.VARCHAR),//美团 饿了么 订单ID 唯一				
										new DataValue(item, Types.INTEGER),//项次
										new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
										new DataValue(pluNO, Types.VARCHAR),//联系人
										new DataValue(pluBarcode, Types.VARCHAR),//联系电话
										new DataValue(pluName, Types.VARCHAR),//配送地址
										new DataValue(specName, Types.VARCHAR),//配送日期
										new DataValue(attrName, Types.VARCHAR),//配送时间
										new DataValue(unit, Types.VARCHAR),//配送类型 1.外卖平台配送 2.自配送 3.顾客自提
										new DataValue(price, Types.VARCHAR),//是否总部配送 Y/N
										new DataValue(qty, Types.VARCHAR),//备注
										new DataValue(goodsGroup, Types.VARCHAR),//下单时间
										new DataValue(disc, Types.VARCHAR),
										new DataValue(boxNum, Types.VARCHAR),//是否开发票 Y/N
										new DataValue(boxPrice, Types.VARCHAR),//发票类型 1.个人 2.企业
										new DataValue(amt, Types.VARCHAR),//发票抬头
										new DataValue(isMemo, Types.VARCHAR),//发票抬头
										new DataValue(skuID, Types.VARCHAR),
										new DataValue("Y", Types.VARCHAR)
								};

								InsBean ib2 = new InsBean("TV_ORDER_DETAIL", columns2);
								ib2.addValues(insValue2);
								this.addProcessData(new DataProcessBean(ib2));
								isExist = true;
								//maxPartRefundGoodsItem ++;
							} 
							catch (Exception e) 
							{
								HelpTools.writelog_waimai("【deliveroo户户送，开始保存数据库】添加部分退单单身异常："+e.getMessage()+"(companyno="+companyNO+" shopno="+shopNO+" orderNO="+orderNO+")");
								continue;
							}			
						}	

						if(isExist)
						{
							this.doExecuteDataToDB();
							HelpTools.writelog_waimai("【deliveroo户户送，部分退单单身添加执行语句成功】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
						}
						else
						{
							HelpTools.writelog_waimai("【deliveroo户户送，部分退单单身为空！】"+" 订单号orderNO:"+orderNO+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);
						}

					}
					catch (SQLException e) 
					{
						HelpTools.writelog_waimai("【deliveroo户户送，部分退单新增单身执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
					}
					catch (Exception e) 
					{
						// TODO: handle exception
						HelpTools.writelog_waimai("【deliveroo户户送，部分退单新增单身执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
					}
				}

			}
			else
			{
				HelpTools.writelog_waimai("【deliveroo户户送，更新的单据不存在】开始插入到数据库"+" 订单号orderNO:"+orderNO+" 订单状态status="+status);
				ArrayList<DataProcessBean> DPB = null;//HelpTools.GetInsertOrder(obj);
				if (DPB != null && DPB.size() > 0)
				{
					for (DataProcessBean dataProcessBean : DPB) 
					{
						this.addProcessData(dataProcessBean);			
					}					
					this.doExecuteDataToDB();
					HelpTools.writelog_waimai("【deliveroo户户送，更新的单据不存在】插入数据库成功"+" 订单号orderNO:"+orderNO+" 订单状态status="+status);
				}

			}


			//region写订单日志
			try
			{/*
				OrderStatusLogCreateReq req_log = new OrderStatusLogCreateReq();
				req_log.setDatas(new ArrayList<OrderStatusLogCreateReq.level1Elm>());
				OrderStatusLogCreateReq.level1Elm onelv1 = new OrderStatusLogCreateReq().new level1Elm();
				onelv1.setCallback_status("0");
				onelv1.setLoadDocType("37");

				onelv1.setNeed_callback("N");
				String need_notify ="N";

				onelv1.setNeed_notify(need_notify);
				String o_companyNO = companyNO;

				onelv1.setO_companyNO(o_companyNO);
				onelv1.setO_opName("deliveroo户户送");
				onelv1.setO_opNO("");
				String o_shopNO = shopNO;
				onelv1.setO_organizationNO(o_shopNO);
				onelv1.setO_shopNO(o_shopNO);
				onelv1.setOrderNO(orderNO);

				String statusType = "1";

				String updateStaus = status;

				onelv1.setStatusType(statusType);				 					
				onelv1.setStatus(updateStaus);

				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				String memo = "";
				memo += statusName;
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");
				
				//req_log.getDatas().add(onelv1);//这里需要注意（先不添加 ，没有申请退单在添加，因为申请退单之前的订单状态已经存了)，
				//如果有申请退单
				if(refundStatus.equals("2")||refundStatus.equals("7"))
				{
					need_notify ="Y";//通知POS				 
					statusType = "3";//退单状态
					OrderStatusLogCreateReq.level1Elm onelv2 = new OrderStatusLogCreateReq().new level1Elm();
					onelv2.setCallback_status("0");
					onelv2.setLoadDocType("2");

					onelv2.setNeed_callback("N");

					onelv2.setNeed_notify(need_notify);
					//String o_companyNO = companyNO;

					onelv2.setO_companyNO(o_companyNO);
					onelv2.setO_opName("deliveroo户户送");
					onelv2.setO_opNO("");
					//String o_shopNO = shopNO;
					onelv2.setO_organizationNO(o_shopNO);
					onelv2.setO_shopNO(o_shopNO);
					onelv2.setOrderNO(orderNO);

					updateStaus = refundStatus;

					onelv2.setStatusType(statusType);				 					
					onelv2.setStatus(updateStaus);

					statusTypeNameObj = new StringBuilder();
					statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
					statusTypeName = statusTypeNameObj.toString();
					onelv2.setStatusTypeName(statusTypeName);
					onelv2.setStatusName(statusName);
					onelv2.setDisplay("1");

					updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv2.setUpdate_time(updateDatetime);
					req_log.getDatas().add(onelv2);					 
				}
				else
				{
					req_log.getDatas().add(onelv1);
				}

				onelv1 = null;

				ParseJson pj = new ParseJson();

				String req_log_json = pj.beanToJson(req_log);

				pj=null;

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, req_log_json, errorMessage);
				if(nRet)
				{		  		 
					HelpTools.writelog_waimai("【deliveroo户户送，写表tv_orderStatuslog保存成功】"+" 订单号orderNO:"+orderNO);
				}
				else
				{			  		 
					HelpTools.writelog_waimai("【deliveroo户户送，写表tv_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
				}


			*/}
			catch (Exception e)
			{

			}
			//endregion

		}
		catch (SQLException e) 
		{
			HelpTools.writelog_waimai("【deliveroo户户送执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【deliveroo户户送执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req);		
		}		

	}


	private boolean IsExistOrder(String companyno,String organizationno,String shopNO,String orderNO,String loadDocType,Map<String, Object> orderHead) throws Exception
	{
		boolean nRet = false;

		String sql = "select * from tv_order where COMPANYNO='" + companyno + "' and ORGANIZATIONNO='" + organizationno + "'";
		sql += " and SHOP='" + shopNO + "' and ORDERNO='" + orderNO + "' and LOAD_DOCTYPE='" + loadDocType + "'";

		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql,null);

		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			nRet = true;
			String status =getQDataDetail.get(0).get("STATUS").toString();
			String refundStatus =getQDataDetail.get(0).get("REFUNDSTATUS").toString();
			orderHead.put("STATUS", status);
			orderHead.put("REFUNDSTATUS", refundStatus);
		}
		return nRet;

	}

	private int GetMaxOrderDetailItem(String companyno,String organizationno,String shopNO,String orderNO,String loadDocType) throws Exception
	{
		int nRet = 100;
		String sql = "select ITEM FROM ( ";
		sql += "select max(item) as ITEM  from tv_order_detail where COMPANYNO='" + companyno + "' and ORGANIZATIONNO='" + organizationno + "'";
		sql += " and SHOP='" + shopNO + "' and ORDERNO='" + orderNO + "'";
		sql += ")";
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql,null);

		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			try 
			{
				nRet = Integer.parseInt(getQDataDetail.get(0).get("ITEM").toString());

			} 
			catch (Exception e) 
			{
				nRet = 100;		
			}
		}

		nRet = nRet+1;
		return nRet;

	}

	private void DeleteRedis(String redis_key,String hash_key) throws Exception
	{
		try 
		{			
			RedisPosPub redis = new RedisPosPub();
			HelpTools.writelog_waimai("【开始删除缓存】"+" redis_key:"+redis_key+" hash_key:"+hash_key);
			redis.DeleteHkey(redis_key, hash_key);//
			HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！"+" redis_key:"+redis_key+" hash_key:"+hash_key);
			//redis.Close();

		} 
		catch (Exception e) 
		{
			HelpTools.writelog_waimai("【删除存在hash_key的缓存】异常"+e.getMessage()+" redis_key:"+redis_key+" hash_key:"+hash_key);
		}
	}



}
