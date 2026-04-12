package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SaleCreateReq;
import com.dsc.spos.json.cust.req.DCP_SaleCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_SaleCreateReq.level2Goods;
import com.dsc.spos.json.cust.req.DCP_SaleCreateReq.level2Pay;
import com.dsc.spos.json.cust.res.DCP_SaleCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;

public class DCP_SaleCreate extends SPosAdvanceService<DCP_SaleCreateReq,DCP_SaleCreateRes> {

	@Override
	protected void processDUID(DCP_SaleCreateReq req, DCP_SaleCreateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		String eId=req.geteId();
		//String createBy = req.getOpNO();
		String OrganizationNO=req.getOrganizationNO();
    String Key_ID = req.getKeyid();
    int i_count=0;
    int j_count=0;
    int k_count=0;
		if(checkGuid(req))
		{
			PosPub.writelog_SaleCreate("请求服务Key："+Key_ID+" 已经执行过，不能重复执行！");
			PosPub.writelog_SaleCreateException("请求服务Key："+Key_ID+" 已经执行过，不能重复执行！");
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		
			res.setSuccess(true);
		}
		else
		{
			List<level1Elm> jsonDatas=req.getSale();
			//region 单头循环
			for (level1Elm itemlevel1 : jsonDatas)
			{			
				i_count++;
				String[] columnsSale = {					
						"KEY_ID",
						"SALE_ID",
						"SHOPID",
						"MACHINE",
						"SALENO",
						"BDATE",
						"SQUADNO",
						"OPNO",
						"TOT_QTY",
						"TOT_AMT",
						"TOT_UAMT",
						"TYPE",
						"OTYPE",
						"OFNO",
						"SDATE",
						"STIME",
						"ISPRACTICE",
						"STATUS",
						"ISBUFFER",
						"ORDER_ID",
						"EID",
						"ORGANIZATIONNO",
						"PC_CDATE",
						"PC_CTIME",
						"PC_SPECRECE",
				"PC_SALETYPE",
				"PARTITION_DATE"};

				String Sale_ID=itemlevel1.getSale_id();
				String Sale_NO=itemlevel1.getSale_no();
				String shopId=itemlevel1.getShopId();
				String Sdate=itemlevel1.getSdate();
				String Stime=itemlevel1.getStime();
				

				//region 子表sale_detail 和 sale_pay
				List<level2Goods> jsonGoods=itemlevel1.getSale_goods_detail();
				if(jsonGoods==null||jsonGoods.size()==0)
				{
					String errmessage="门店："+shopId+"单号："+Sale_NO+"没有销售单身！"+"请求服务Key:"+Key_ID;
					PosPub.writelog_SaleCreateException(errmessage);
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errmessage);
				}
				List<level2Pay> jsonPay=itemlevel1.getSale_pay_detail();
				if(jsonPay==null||jsonPay.size()==0)
				{
					String errmessage="门店："+shopId+"单号："+Sale_NO+"没有付款档！"+"请求服务Key:"+Key_ID;
					PosPub.writelog_SaleCreateException(errmessage);
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errmessage);
				}
				for(level2Goods itemgood : jsonGoods)
				{
					j_count++;
					String[] columnsSaleDetail ={
							"SALE_ID",
							"OLD_ID",
							"SALE_DETAIL_ID",
							"SHOPID",
							"SALENO",
							"ITEM",
							"PLUNO",
							"PLUBARCODE",
							"SCANNO",
							"UNIT",
							"OLDPRICE",
							"PRICE",
							"QTY",
							"DISC",
							"AMT",
							"UAMT",
							"OITEM",
							"SDATE",
							"STIME",
							"STATUS",
							"MQTY",
							"UNITRATIO",
							"SUNIT",
							"DATASOURCE",
							"PRICEFACTOR",
							"EID",
							"ORGANIZATIONNO",
							"PC_ISSCRAP",
					"PC_ISADD",
                    "PARTITION_DATE",
                            "UNITRATIO","BASEQTY"};
					DataValue[] insValue2 = null;
					insValue2 = new DataValue[] 
							{
									new DataValue(Sale_ID, Types.VARCHAR),
									new DataValue(itemgood.getOld_sale_detail_id(), Types.VARCHAR),
									new DataValue(itemgood.getSale_detail_id(), Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(Sale_NO, Types.VARCHAR),
									new DataValue(Integer.parseInt(itemgood.getSeq()), Types.INTEGER),
									new DataValue(itemgood.getPlu_no(), Types.VARCHAR),
									new DataValue(itemgood.getPlu_barcode(), Types.VARCHAR),
									new DataValue(itemgood.getScan_barcode(), Types.VARCHAR),
									new DataValue("PCS", Types.VARCHAR),								
									new DataValue(Float.parseFloat(itemgood.getOld_price()), Types.FLOAT),
									new DataValue(Float.parseFloat(itemgood.getPrice()), Types.FLOAT),
									new DataValue(Float.parseFloat(itemgood.getQty()), Types.FLOAT),
									new DataValue(Float.parseFloat(itemgood.getDisc()), Types.FLOAT),
									new DataValue(Float.parseFloat(itemgood.getAmt()), Types.FLOAT),
									new DataValue(Float.parseFloat(itemgood.getUamt()), Types.FLOAT),
									new DataValue(Integer.parseInt(itemgood.getSource_seq()), Types.INTEGER),
									new DataValue(Sdate, Types.VARCHAR),
									new DataValue(Stime, Types.VARCHAR),
									new DataValue("100", Types.VARCHAR),
									new DataValue(Float.parseFloat(itemgood.getQty()), Types.FLOAT),
									new DataValue(1, Types.FLOAT),
									new DataValue("PCS", Types.VARCHAR),
									new DataValue("1", Types.VARCHAR),
									new DataValue(1, Types.INTEGER),
									new DataValue(eId, Types.VARCHAR),
									new DataValue(OrganizationNO, Types.VARCHAR),
									new DataValue(itemgood.getIs_scrap(), Types.VARCHAR),
									new DataValue(itemgood.getIs_add(), Types.VARCHAR),
                                    new DataValue(itemlevel1.getBusiness_date(), Types.NUMERIC),//分区字段
                                    new DataValue(1d, Types.FLOAT),
                                    new DataValue(itemgood.getQty(), Types.FLOAT)
							};
					InsBean ib2 = new InsBean("DCP_SALE_DETAIL", columnsSaleDetail);//分区字段已处理
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增单身
					//region 库存流水账
					String stock_type="1";
					String doc_type="20";
					if(itemlevel1.getDoc_type().equals("1"))
					{
						stock_type="0";
						doc_type="21";
					}
					String[] columnsDCP_STOCK_DETAIL =
						{
								"EID","ORGANIZATIONNO","WAREHOUSE",
								"STOCK_TYPE","DOC_TYPE","DOCNO",
								"DOC_OBJECT","BDATE",
								"BUSINESS_TYPE","OTYPE","OFNO","DELIVERY_NO",
								"MEMO","ITEM",
								"OITEM",
								"PLUNO","FEATURENO","PUNIT",
								"PQTY","WUNIT","UNIT_RATIO",
								"WQTY","PRICE","AMT",
								"CREATEBY","CREATE_DATE","CREATE_TIME",
								"ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
								"IS_SCRAP","LOAD_DOCTYPE","LOAD_DOCNO"
						};
					DataValue[] insValue3 = null;
				
					//判断仓库不能为空或空格  BY JZMA 20191118  这个服务应该是客制的，仓库栏位竟然给的是门店编号
					if (Check.Null(shopId)||Check.Null(shopId.trim())||shopId.trim().equals("")||shopId.trim().isEmpty())
					{
						this.pData.clear();
						//仓库编号为空格的返回
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");			
					}
					
					insValue3 = new DataValue[] 
							{							
									new DataValue(eId, Types.VARCHAR),
									new DataValue(OrganizationNO, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(stock_type, Types.VARCHAR),
									new DataValue(doc_type, Types.VARCHAR),																
									new DataValue(Sale_NO, Types.VARCHAR),
									new DataValue("", Types.VARCHAR),
									new DataValue(itemlevel1.getBusiness_date(), Types.VARCHAR),
									new DataValue(itemlevel1.getDoc_type(), Types.VARCHAR),
									new DataValue(itemlevel1.getSource_type(), Types.VARCHAR),
									new DataValue(itemlevel1.getSource_no(), Types.VARCHAR),
									new DataValue("", Types.VARCHAR),
									new DataValue("", Types.VARCHAR),
									new DataValue(Integer.parseInt(itemgood.getSeq()), Types.INTEGER),
									new DataValue(Integer.parseInt(itemgood.getSource_seq()), Types.INTEGER),
									new DataValue(itemgood.getPlu_no(), Types.VARCHAR),
									new DataValue(" ", Types.VARCHAR),							
									new DataValue("PCS", Types.VARCHAR),	
									new DataValue(Float.parseFloat(itemgood.getQty()), Types.FLOAT),
									new DataValue("PCS", Types.VARCHAR),
									new DataValue(1, Types.FLOAT),
									new DataValue(Float.parseFloat(itemgood.getQty()), Types.FLOAT),								
									new DataValue(Float.parseFloat(itemgood.getPrice()), Types.FLOAT),								
									new DataValue(Float.parseFloat(itemgood.getAmt()), Types.FLOAT),	
									new DataValue(itemlevel1.getCreator(), Types.VARCHAR),
									new DataValue(Sdate, Types.VARCHAR),
									new DataValue(Stime, Types.VARCHAR),
									new DataValue(itemlevel1.getCreator(), Types.VARCHAR),
									new DataValue(itemlevel1.getBusiness_date(), Types.VARCHAR),
									new DataValue(Stime, Types.VARCHAR),																
									new DataValue(itemgood.getIs_scrap(), Types.VARCHAR),
									new DataValue("", Types.VARCHAR),
									new DataValue("", Types.VARCHAR)
							};
					InsBean ib3 = new InsBean("DCP_STOCK_DETAIL", columnsDCP_STOCK_DETAIL);
					ib3.addValues(insValue3);
					this.addProcessData(new DataProcessBean(ib3)); // 新增单身
					//endregion
				}
				for(level2Pay itemPay : jsonPay)
				{
					k_count++;
					String[] columnsSalePay ={
							"SALE_PAY_ID",
							"SALE_ID",
							"SHOPID",
							"SALENO",
							"ITEM",
							"PAYCODE",
							"PAYCODEERP",
							"ERPPAYNO",
							"PAYSERNUM",
							"SERIALNO",
							"PAY",
							"EXTRA",
							"CHANGED",
							"SDATE",						
							"STIME",
							"ISVERIFICATION",
							"EXRATE",
							"INPUTPAY",	
							"PC_BANKNAME",
					"STATUS",
					"PARTITION_DATE"};

					DataValue[] insValue2 = null;
					insValue2 = new DataValue[] 
							{
									new DataValue(itemPay.getSale_pay_id(), Types.VARCHAR),
									new DataValue(Sale_ID, Types.VARCHAR),					
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(Sale_NO, Types.VARCHAR),
									new DataValue(Integer.parseInt(itemPay.getSeq()), Types.INTEGER),
									new DataValue(itemPay.getPay_code(), Types.VARCHAR),
									new DataValue(itemPay.getPay_type(), Types.VARCHAR),
									new DataValue(itemPay.getErp_pay_code(), Types.VARCHAR),
									new DataValue(itemPay.getPay_sernum(), Types.VARCHAR),
									new DataValue(itemPay.getSerial_no(), Types.VARCHAR),										
									new DataValue(Float.parseFloat(itemPay.getPay()), Types.FLOAT),
									new DataValue(Float.parseFloat(itemPay.getExtra_amt()), Types.FLOAT),
									new DataValue(Float.parseFloat(itemPay.getChanged_amt()), Types.FLOAT),
									new DataValue(Sdate, Types.VARCHAR),
									new DataValue(Stime, Types.VARCHAR),
									new DataValue("Y", Types.VARCHAR),								
									new DataValue(1, Types.FLOAT),
									new DataValue(Float.parseFloat(itemPay.getInput_amt()), Types.FLOAT),	
									new DataValue(itemPay.getPc_bankname(), Types.VARCHAR),
									new DataValue("100", Types.VARCHAR),
									new DataValue(itemlevel1.getBusiness_date(), Types.NUMERIC)//分区字段
							};
					InsBean ib2 = new InsBean("DCP_SALE_PAY", columnsSalePay);//分区字段已处理
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增单身
				}
				//endregion

				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]
						{
								new DataValue(Key_ID, Types.VARCHAR),
								new DataValue(Sale_ID, Types.VARCHAR),					
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(itemlevel1.getMachine_no(), Types.VARCHAR),
								new DataValue(Sale_NO, Types.VARCHAR),					
								new DataValue(itemlevel1.getBusiness_date(), Types.VARCHAR),
								new DataValue(Integer.parseInt(itemlevel1.getClass_no()), Types.INTEGER),
								new DataValue(itemlevel1.getCreator(), Types.VARCHAR),												
								new DataValue(Float.parseFloat(itemlevel1.getTot_qty()), Types.FLOAT),
								new DataValue(Float.parseFloat(itemlevel1.getTot_amt()), Types.FLOAT),
								new DataValue(Float.parseFloat(itemlevel1.getTot_uamt()), Types.FLOAT),						
								new DataValue(Integer.parseInt(itemlevel1.getDoc_type()), Types.INTEGER),
								new DataValue(Integer.parseInt(itemlevel1.getSource_type()), Types.INTEGER),
								new DataValue(itemlevel1.getSource_no(), Types.VARCHAR),
								new DataValue(Sdate, Types.VARCHAR),
								new DataValue(Stime, Types.VARCHAR),
								new DataValue("N", Types.VARCHAR),
								new DataValue("100", Types.VARCHAR),
								new DataValue("N", Types.VARCHAR),
								new DataValue(itemlevel1.getOrder_id() , Types.VARCHAR),
								new DataValue(eId, Types.VARCHAR),
								new DataValue(OrganizationNO, Types.VARCHAR),
								new DataValue(itemlevel1.getCreate_date(), Types.VARCHAR),
								new DataValue(itemlevel1.getCreate_time(), Types.VARCHAR),
								new DataValue(itemlevel1.getPc_specrece(), Types.VARCHAR),
								new DataValue(itemlevel1.getPc_saletype(), Types.VARCHAR),
								new DataValue(itemlevel1.getBusiness_date(), Types.NUMERIC)//分区字段
						};
				InsBean ib1 = new InsBean("DCP_SALE", columnsSale);//分区字段已处理
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增单头			
			}
			//endregion		
			PosPub.writelog_SaleCreate("销售单头总数："+i_count+" 销售单身总数："+j_count+" 付款明细总数："+k_count);
			PosPub.writelog_SaleCreate("开始插入数据");
			Date cal_start = Calendar.getInstance().getTime();// 获得当前时间    
			this.doExecuteDataToDB();
			Date cal_end = Calendar.getInstance().getTime();
			PosPub.writelog_SaleCreate("结束插入数据");
			long diff_ss=cal_end.getTime()-cal_start.getTime();		
			PosPub.writelog_SaleCreate("数据插入耗时："+diff_ss);
			if (res.isSuccess()) 
			{
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");						
			} 
		}
	}


	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String Key_ID = req.getKeyid();
		List<level1Elm> jsonDatas = req.getSale();
		for(level1Elm item1 : jsonDatas)
		{
			//region 单头不能为空判断
			if (Check.Null(item1.getSale_id())) 
			{
				errCt++;
				errMsg.append("销售单GUID不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(item1.getSale_no())) 
			{
				errCt++;
				errMsg.append("销售单号不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(item1.getBusiness_date())) 
			{
				errCt++;
				errMsg.append("营业日期不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(item1.getClass_no())) 
			{
				errCt++;
				errMsg.append("班次不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(item1.getTot_qty())) 
			{
				errCt++;
				errMsg.append("总数量不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(item1.getTot_amt())) 
			{
				errCt++;
				errMsg.append("总金额不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(item1.getTot_uamt())) 
			{
				errCt++;
				errMsg.append("未税总金额不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(item1.getDoc_type())) 
			{
				errCt++;
				errMsg.append("单据类型不可为空值, ");
				isFail = true;
			}
			if (Check.Null(item1.getSource_type().trim())) 
			{
				item1.setSource_type("0");
			} 

			if(Check.Null(item1.getClass_no()) == false)
			{
				try 
				{
					Integer.parseInt(item1.getClass_no());
				} 
				catch (Exception e)
				{
					errCt++;
					errMsg.append("班次必须为整数, ");
					isFail = true;
				}
			}
			if (Check.Null(item1.getPc_saletype())) 
			{
				errCt++;
				errMsg.append("业务类型不可为空值, ");
				isFail = true;
			}

			//endregion
			//region 单身不能为空判断
			List<level2Goods> json2Datas=item1.getSale_goods_detail();
			List<level2Pay> json2DatasPay=item1.getSale_pay_detail();
			//region goods
			for(level2Goods itemgood : json2Datas)
			{
				if (Check.Null(itemgood.getSale_detail_id())) 
				{
					errCt++;
					errMsg.append("交易明细Sale_Detail_ID不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itemgood.getSeq())) 
				{
					errCt++;
					errMsg.append("项次不可为空值, ");
					isFail = true;
				} 

				if (Check.Null(itemgood.getPlu_no())) 
				{
					errCt++;
					errMsg.append("商品编号不可为空值, ");
					isFail = true;
				} 

				if (Check.Null(itemgood.getSource_seq())) 
				{
					itemgood.setSource_seq("0");
				} 
				if (Check.Null(itemgood.getOld_price())) 
				{
					errCt++;
					errMsg.append("原价不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itemgood.getPrice())) 
				{
					errCt++;
					errMsg.append("售价不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itemgood.getQty())) 
				{
					errCt++;
					errMsg.append("数量不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itemgood.getDisc())) 
				{
					itemgood.setDisc("0");
				} 

				if (Check.Null(itemgood.getAmt())) 
				{
					errCt++;
					errMsg.append("含税金额小计不可为空值, ");
					isFail = true;
				} 

				if (Check.Null(itemgood.getUamt())) 
				{
					errCt++;
					errMsg.append("未税金额小计不可为空值, ");
					isFail = true;
				} 
				if(Check.Null(itemgood.getSeq()) == false)
				{
					try 
					{
						Integer.parseInt(itemgood.getSeq());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("项次必须为整数, ");
						isFail = true;
					}
				}
				if(Check.Null(itemgood.getOld_price()) == false)
				{
					try 
					{
						Float.parseFloat(itemgood.getOld_price());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("原价必须为数字, ");
						isFail = true;
					}
				}	
				if(Check.Null(itemgood.getPrice()) == false)
				{
					try 
					{
						Float.parseFloat(itemgood.getPrice());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("售价必须为数字, ");
						isFail = true;
					}
				}			
				if(Check.Null(itemgood.getQty()) == false)
				{
					try 
					{
						Float.parseFloat(itemgood.getQty());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("数量必须为数字, ");
						isFail = true;
					}
				}	
				if(Check.Null(itemgood.getAmt()) == false)
				{
					try 
					{
						Float.parseFloat(itemgood.getAmt());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("含税金额小计必须为数字, ");
						isFail = true;
					}
				}	
				if(Check.Null(itemgood.getUamt()) == false)
				{
					try 
					{
						Float.parseFloat(itemgood.getUamt());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("未税金额小计必须为数字, ");
						isFail = true;
					}
				}	
			}
			//endregion
			//region pay
			for(level2Pay itempay : json2DatasPay)
			{
				if (Check.Null(itempay.getSale_pay_id())) 
				{
					errCt++;
					errMsg.append("收款Sale_Pay_ID不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itempay.getSeq())) 
				{
					errCt++;
					errMsg.append("项次不可为空值, ");
					isFail = true;
				} 

				if (Check.Null(itempay.getPay_code())) 
				{
					errCt++;
					errMsg.append("支付编码不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itempay.getPay_type())) 
				{
					errCt++;
					errMsg.append("支付类型不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itempay.getErp_pay_code())) 
				{
					errCt++;
					errMsg.append("ERP支付编码不可为空值, ");
					isFail = true;
				} 
				if (Check.Null(itempay.getPay())) 
				{
					errCt++;
					errMsg.append("付款金额不可为空值, ");
					isFail = true;
				} 							
				if (Check.Null(itempay.getExtra_amt())) 
				{
					itempay.setExtra_amt("0");
				} 
				if (Check.Null(itempay.getChanged_amt())) 
				{
					itempay.setChanged_amt("0");
				} 
				if (Check.Null(itempay.getInput_amt())) 
				{
					itempay.setInput_amt("0");
				} 

				if(Check.Null(itempay.getSeq()) == false)
				{
					try 
					{
						Integer.parseInt(itempay.getSeq());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("项次必须为整数, ");
						isFail = true;
					}
				}
				if(Check.Null(itempay.getPay()) == false)
				{
					try 
					{
						Float.parseFloat(itempay.getPay());
					} 
					catch (Exception e)
					{
						errCt++;
						errMsg.append("付款金额必须为数字, ");
						isFail = true;
					}
				}					
			}
			//endregion
			//endregion
		}
		if (isFail)
		{
			PosPub.writelog_SaleCreateException("服务Key："+Key_ID+" "+errMsg.toString());
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return	isFail;
	}

	@Override
	protected TypeToken<DCP_SaleCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleCreateReq>(){};
	}

	@Override
	protected DCP_SaleCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleCreateRes();
	}

	private boolean checkGuid(DCP_SaleCreateReq req) throws Exception {
		String sql = null;
		boolean existGuid; 
		String Key_ID = " " ;
		Key_ID = req.getKeyid().toUpperCase();
		sql = "select * from DCP_SALE where key_id = ? ";
		String[] conditionValues={Key_ID};
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}
}
