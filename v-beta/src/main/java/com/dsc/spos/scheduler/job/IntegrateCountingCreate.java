package com.dsc.spos.scheduler.job;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;

//********************20171221新加接口**************************
//********************盘点单据新增上传*****************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class IntegrateCountingCreate extends InitJob
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";
	String pOrganizationNO="";
	String pBillNo="";

	public IntegrateCountingCreate()
	{

	}

	public IntegrateCountingCreate(String eId,String shopId,String organizationNO, String billNo)
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	Logger logger = LogManager.getLogger(IntegrateCountingCreate.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public String doExe()
	{
		//返回信息
		String sReturnInfo="";

		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{
			logger.info("\r\n*********盘点单据integrate.counting.create正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-盘点单据integrate.counting.create正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//
		logger.info("\r\n*********盘点单据integrate.counting.create定时调用Start:************\r\n");

		try
		{
			String sql = this.getQuerySql100_10();
			logger.debug("\r\n******盘点单据integrate.counting.create 执行SQL语句："+sql+"******\r\n");
			String[] conditionValues100_10 = {}; // 查詢條件
			List<Map<String, Object>> getQData100_10 = this.doQueryData(sql, conditionValues100_10);
			if (getQData100_10 != null && getQData100_10.isEmpty() == false)
			{
				for (Map<String, Object> oneData100 : getQData100_10)
				{
					String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
					String transfer_date = "";
					if (bDate != null && bDate.length() > 0)
					{
						transfer_date = DCP_ConversionTimeFormat.converToDate(bDate);
					}

					String loadDocType = oneData100.get("LOADDOCTYPE") == null ? "" : oneData100.get("LOADDOCTYPE").toString();

					if (loadDocType == null || loadDocType.length() <= 0)
					{
						loadDocType = "1";
					}
					else
					{
						loadDocType = "2";
					}

					String shopId = oneData100.get("SHOPID") == null ? "" : oneData100.get("SHOPID").toString();
					String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
					String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
					String stocktakeNO = oneData100.get("STOCKTAKENO") == null ? "" : oneData100.get("STOCKTAKENO").toString();
					String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
					String docType = oneData100.get("DOCTYPE") == null ? "" : oneData100.get("DOCTYPE").toString();
					String notGoodsMode = oneData100.get("NOTGOODSMODE") == null ? "" : oneData100.get("NOTGOODSMODE").toString();
					String loadDocNO = oneData100.get("LOADDOCNO") == null ? "" : oneData100.get("LOADDOCNO").toString();
					String ptemplateNO=oneData100.get("PTEMPLATENO").toString();
					String createBy = oneData100.get("CREATEBY") == null ? "" : oneData100.get("CREATEBY").toString();
					String createDate = oneData100.get("CREATEDATE") == null ? "" : oneData100.get("CREATEDATE").toString();
					String createTime = oneData100.get("CREATETIME") == null ? "" : oneData100.get("CREATETIME").toString();
					String create_datetime = "";
					if (createDate != null && createDate.length() > 0 && createTime != null&& createTime.length() > 0)
					{
						create_datetime = DCP_ConversionTimeFormat.converToDatetime(createDate + createTime);
					}
					String modifyBy = oneData100.get("MODIFYBY") == null ? "" : oneData100.get("MODIFYBY").toString();
					String modifyDate = oneData100.get("MODIFYDATE") == null ? "" : oneData100.get("MODIFYDATE").toString();
					String modifyTime = oneData100.get("MODIFYTIME") == null ? "" : oneData100.get("MODIFYTIME").toString();
					String modify_datetime = "";
					if (modifyDate != null && modifyDate.length() > 0 && modifyTime != null && modifyTime.length() > 0)
					{
						modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate + modifyTime);
					}
					String confirmBy = oneData100.get("CONFIRMBY") == null ? "" : oneData100.get("CONFIRMBY").toString();
					String confirmDate = oneData100.get("CONFIRMDATE") == null ? "" : oneData100.get("CONFIRMDATE").toString();
					String confirmTime = oneData100.get("CONFIRMTIME") == null ? "" : oneData100.get("CONFIRMTIME").toString();
					String approve_datetime = "";
					if (confirmDate != null && confirmDate.length() > 0 && confirmTime != null && confirmTime.length() > 0)
					{
						approve_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate + confirmTime);
					}
					String account_date = oneData100.get("ACCOUNT_DATE") == null ? "" : oneData100.get("ACCOUNT_DATE").toString();
					String isAdjustStock = oneData100.getOrDefault("IS_ADJUST_STOCK", "Y").toString();

					// 获取单身数据
					sql = this.getQuerySql101_10(eId, organizationNO, stocktakeNO,docType,notGoodsMode);
					String[] conditionValues101_10 = {  }; // 查詢條件
					List<Map<String, Object>> getQData101_10 = this.doQueryData(sql, conditionValues101_10);
					if (getQData101_10 != null && getQData101_10.isEmpty() == false)
					{
						//payload对象
						JSONObject payload = new JSONObject();
						JSONObject std_data = new JSONObject();
						JSONObject parameter = new JSONObject();
						JSONArray request = new JSONArray();
						JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
						JSONArray request_detail = new JSONArray(); // 存所有单身

						int i =1;
						for (Map<String, Object> oneData101 : getQData101_10)
						{
							// 获取单身数据并赋值
							JSONObject body = new JSONObject(); // 存一笔单身
							//全盘关联了库存调整单导致项次重复  BY JZMA 20200117
							//String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();							
							String item=String.valueOf(i);
							String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
							String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
							String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
							String punit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
							String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
							String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
							String ref_baseqty = oneData101.get("REFBASEQTY") == null ? "0" : oneData101.get("REFBASEQTY").toString();
							String diff_qty = oneData101.get("DIFFQTY") == null ? "0" : oneData101.get("DIFFQTY").toString();
							String unitRatio = oneData101.get("UNITRATIO") == null ? "0" : oneData101.get("UNITRATIO").toString();
							String price = oneData101.get("PRICE") == null ? "0" : oneData101.get("PRICE").toString();
							String amt = oneData101.get("AMT") == null ? "0" : oneData101.get("AMT").toString();
							String WAREHOUSE = oneData101.get("WAREHOUSE") == null ? "" : oneData101.get("WAREHOUSE").toString();
							String MEMO = oneData101.get("MEMO") == null ? "" : oneData101.get("MEMO").toString();
							String batchNO = oneData101.get("BATCH_NO") == null ? "" : oneData101.get("BATCH_NO").toString();
							String prodDate = oneData101.get("PROD_DATE") == null ? "" : oneData101.get("PROD_DATE").toString();
							String distriprice = oneData101.get("DISTRIPRICE") == null ? "" : oneData101.get("BATCH_NO").toString();
							String distriamt = oneData101.get("DISTRIAMT") == null ? "" : oneData101.get("PROD_DATE").toString();

							body.put("seq", item);
							body.put("item_no", pluNO);
							body.put("feature_no", featureNo);
							body.put("warehouse_no", WAREHOUSE);
							body.put("base_unit", baseUnit);
							body.put("diff_qty", diff_qty);
							body.put("reason_no", "");
							body.put("remark", MEMO);
							body.put("base_qty", ref_baseqty);
							body.put("phy_counting_qty", baseQty);
							body.put("item_batch_no", batchNO);
							body.put("prod_date", prodDate);
							body.put("price",price);
							body.put("amt", amt);
							body.put("distriprice", distriprice);
							body.put("distriamt", distriamt);
							request_detail.put(body);
							i++;
						}

						// 给单头赋值
						header.put("front_no", stocktakeNO);
						header.put("site_no", shopId);
						header.put("version", "3.0");
						header.put("create_date", bDate);
						header.put("creator", createBy);
						header.put("account_date", account_date);
						header.put("is_adjust_stock", isAdjustStock);

						header.put("request_detail", request_detail);
						//老母鸡使用的TT盘点计划不会下传，下传的是盘点模板，盘点模板编码就是对应ERP的盘点计划
						if(loadDocNO.equals(""))
						{
							loadDocNO=ptemplateNO;
						}
						header.put("counting_plans", loadDocNO);
						request.put(header);

						parameter.put("request", request);
						std_data.put("parameter", parameter);
						payload.put("std_data", std_data);
						String str = payload.toString();// 将json对象转换为字符串

						logger.info("\r\n******盘点单据integrate.counting.create请求T100传入参数：  " + str + "\r\n");
						String resbody="";
						//执行请求操作，并拿到结果（同步阻塞）
						try
						{
							resbody=HttpSend.Send(str, "integrate.counting.create", eId, shopId,organizationNO,stocktakeNO);

							logger.info("\r\n******盘点单据integrate.counting.create请求T100返回参数：  "+ "\r\n单号="+ stocktakeNO + "\r\n" + resbody + "******\r\n");
							if(Check.Null(resbody) || resbody.isEmpty() )
							{
								continue;
							}
							///2018/1/1 jzma  增加为空的判断  

							if  (resbody.equals(""))
							{
								//
								String code ="331" ;
								String description ="服务执行逾时";
								sReturnInfo="中台返回错误信息:" + code + "," + description;
								InsertWSLOG.insert_WSLOG("integrate.counting.create",stocktakeNO,eId,organizationNO,"1",str,resbody,code,description);
							}
							else
							{
								JSONObject jsonres = new JSONObject(resbody);
								JSONObject std_data_res = jsonres.getJSONObject("std_data");
								JSONObject execution_res = std_data_res.getJSONObject("execution");

								// parameter 节点，记录ERP对应的单号和 ERP 对应的组织
								String docNo = "";
								String orgNo = "";
								if(std_data_res.has("parameter")){
									JSONObject parameter_res = std_data_res.getJSONObject("parameter");
									if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
										docNo = parameter_res.get("doc_no").toString();
										orgNo = parameter_res.get("org_no").toString();
									}
								}

								String code = execution_res.getString("code");
								//String sqlcode = execution_res.getString("sqlcode");
								String description ="";
								//String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");
								if  (!execution_res.isNull("description") )
								{
									description = execution_res.getString("description");
								}

								if (code.equals("0"))
								{
									// values
									Map<String, DataValue> values = new HashMap<String, DataValue>();
									DataValue v = new DataValue("Y", Types.VARCHAR);
									values.put("process_status", v);
									DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
									values.put("UPDATE_TIME", v1);

									//记录ERP 返回的单号和组织
									DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
									DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
									values.put("PROCESS_ERP_NO", docNoVal);
									values.put("PROCESS_ERP_ORG", orgNoVal);

									// condition
									Map<String, DataValue> conditions = new HashMap<String, DataValue>();
									DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
									conditions.put("OrganizationNO", c1);
									DataValue c2 = new DataValue(eId, Types.VARCHAR);
									conditions.put("EID", c2);
									DataValue c3 = new DataValue(shopId, Types.VARCHAR);
									conditions.put("SHOPID", c3);
									DataValue c4 = new DataValue(stocktakeNO, Types.VARCHAR);
									conditions.put("STOCKTAKENO", c4);

									this.doUpdate("DCP_STOCKTAKE", values, conditions);

									//删除WS日志 By jzma 20190524
									InsertWSLOG.delete_WSLOG(eId,shopId,"1",stocktakeNO);
									sReturnInfo="0";
								}
								else
								{
									//
									sReturnInfo="ERP返回错误信息:" + code + "," + description;

									InsertWSLOG.insert_WSLOG("integrate.counting.create",stocktakeNO,eId,organizationNO,"1",str,resbody,code,description);
								}
							}

						}
						catch (Exception e)
						{
							//记录WS日志 By jzma 20190524
							InsertWSLOG.insert_WSLOG("integrate.counting.create",stocktakeNO,eId,shopId,"1",str,resbody,"-1",e.getMessage());

							sReturnInfo="错误信息:" + e.getMessage();

							//System.out.println(e.toString());

							logger.error("\r\n******盘点单据integrate.counting.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stocktakeNO + "\r\n报错信息："+e.getMessage()+"******\r\n");

						}
					}
					else
					{
						//
						sReturnInfo="错误信息:无单身数据！";

						logger.info("\r\n******盘点单据integrate.counting.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stocktakeNO + "无单身数据！******\r\n");
					}


				}
			}
			else
			{
				//
				sReturnInfo="错误信息:无单头数据！";

				logger.info("\r\n******盘点单据integrate.counting.create没有要上传的单头数据******\r\n");
			}


		}
		catch (Exception e)
		{
			try
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);

				pw.flush();
				pw.close();

				errors.flush();
				errors.close();

				logger.error("\r\n******盘点单据integrate.counting.create报错信息" + e.getMessage() +"\r\n" + errors.toString()+ "******\r\n");

				pw=null;
				errors=null;
			}
			catch (IOException e1)
			{
				logger.error("\r\n******盘点单据integrate.counting.create报错信息" + e.getMessage() + "******\r\n");
			}

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally
		{
			bRun=false;//
			logger.info("\r\n*********盘点单据integrate.counting.create定时调用End:************\r\n");
		}

		//
		return sReturnInfo;



	}


	//DCP_STOCKTAKE
	protected String getQuerySql100_10() throws Exception
	{
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ "select EID,ORGANIZATIONNO,STOCKTAKENO,SHOPID,BDATE,MEMO,STATUS,DOCTYPE,LOADDOCTYPE,LOADDOCNO,CREATEBY, "
				+ " CREATEDATE,CREATETIME,CONFIRMBY,CONFIRMDATE,CONFIRMTIME, "
				+ " TOTPQTY,TOTAMT,TOTCQTY,MODIFYBY,MODIFYDATE,MODIFYTIME,PTEMPLATENO,ACCOUNT_DATE,IS_ADJUST_STOCK,NOTGOODSMODE "
				+ " from ("
				+ " SELECT EID,ORGANIZATIONNO,STOCKTAKENO,SHOPID,BDATE,MEMO,STATUS,DOC_TYPE as DOCTYPE, "
				+ " LOAD_DOCTYPE as LOADDOCTYPE, LOAD_DOCNO as LOADDOCNO,CREATEBY, "
				+ " CREATE_DATE as CREATEDATE,CREATE_TIME as CREATETIME,CONFIRMBY,CONFIRM_DATE as CONFIRMDATE,CONFIRM_TIME as CONFIRMTIME, "
				+ " TOT_PQTY as TOTPQTY,TOT_AMT as TOTAMT,TOT_CQTY as TOTCQTY,"
				+ "	MODIFYBY,MODIFY_DATE as MODIFYDATE,MODIFY_TIME as MODIFYTIME,PTEMPLATENO,ACCOUNT_DATE,"
				+ " IS_ADJUST_STOCK,NOTGOODSMODE "
				+ " FROM DCP_STOCKTAKE "
				+ " WHERE status = '2' AND process_status = 'N' "
		);
		//status 0-新建  1-完成  2-提交

		//******兼容即时服务的,只查询指定的那张单据******
		if (pEId.equals("")==false)
		{
			sqlbuf.append(" and EID='"+pEId+"' "
					+ " and SHOPID='"+pShop+"' "
					+ " and ORGANIZATIONNO='"+pOrganizationNO+"' "
					+ " and STOCKTAKENO='"+pBillNo+"' ");

		}

		sqlbuf.append(" ) TBL ");
		return sqlbuf.toString();
	}

	//DCP_STOCKTAKE_DETAIL
	protected String getQuerySql101_10(String eId, String organizationNO, String stocktakeNO,String docType,String notGoodsMode   ) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		if (Check.Null(docType))  docType="";
		if (Check.Null(notGoodsMode))  notGoodsMode="";

		sqlbuf.append(""
				+ "select ITEM,PLUNO,baseunit,PUNIT,PQTY,baseqty,REFbaseqty,DIFFQTY, "
				+ " UNITRATIO,PRICE,AMT,distriprice,distriamt,WAREHOUSE,MEMO,batch_no,prod_date,featureno from ("
				+ " SELECT ITEM,PLUNO,baseunit,PUNIT,"
				+ " PQTY,"
				+ " baseqty, "
				+ " REF_baseqty AS REFbaseqty,"
				+ " (baseqty-nvl(REF_baseqty,0)) as DIFFQTY , "
				+ " UNIT_RATIO AS UNITRATIO,PRICE,AMT,DISTRIPRICE,DISTRIAMT,WAREHOUSE,MEMO,batch_no,prod_date,featureno"
				+ " FROM DCP_STOCKTAKE_DETAIL "
				+ " WHERE EID = '"+eId+"'  AND ORGANIZATIONNO = '"+organizationNO+"'  AND STOCKTAKENO = '"+stocktakeNO+"' "
		);

		//全盘且漏盘商品为零的关联库存调整单返回给ERP  BY JZMA 20191210
		if (docType.equals("0") && notGoodsMode.equals("1"))
		{
			sqlbuf.append(" "
					+ " union all"
					+ " select a.item,a.pluno,a.baseunit,a.punit,0 as PQTY,0 as baseqty,0-a.baseqty as REFbaseqty,a.baseqty as DIFFQTY,"
					+ " a.UNIT_RATIO as UNITRATIO,"
					+ " a.PRICE,0 as amt,a.distriprice,0 as distriamt,b.WAREHOUSE,N''as memo,a.BATCH_NO,a.PROD_DATE,a.featureno"
					+ " from DCP_adjust_detail a"
					+ " inner join DCP_adjust b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.adjustno=b.adjustno and b.ofno='"+stocktakeNO+"' "
					+ " left join DCP_stocktake_detail c on c.EID=a.EID and c.SHOPID=a.SHOPID "
					+ " and c.stocktakeno=b.ofno and c.pluno||c.featureno||c.batch_no = a.pluno||a.featureno||a.batch_no "
					+ " where a.EID='"+eId+"'  and a.organizationno='"+organizationNO+"'  and c.pluno is null " );
		}
		sqlbuf.append(" ) TBL  order by ITEM  ");

		sql = sqlbuf.toString();

		return sql;
	}


}
