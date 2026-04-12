package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.service.webhook.WebHookService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;

//********************配送收货自动收货**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoStockin extends InitJob {
	
	static boolean bRun=false;//标记此服务是否正在执行中
	public String doExe() {
		//此服务是否正在执行中
		//返回信息
		String sReturnInfo="";
		
		if (bRun ) {
			loger.info("\r\n*********配送收货AutoStockin正在执行中,本次调用取消:************\r\n");
			sReturnInfo="配送收货AutoStockin正在执行中！";
			return sReturnInfo;
		}
		
		bRun=true;
		
		loger.info("\r\n*********配送收货AutoStockin定时调用Start:************\r\n");
		try {
			String sdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
			String stime = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());
			//先查 job 执行时间，然后再执行后续操作
			String getTimeSql = "select * from job_quartz_detail where job_name = 'AutoStockin'  and STATUS = '100' ";
			List<Map<String, Object>> getTimeDatas=this.doQueryData(getTimeSql, null);
			if(getTimeDatas!= null && !getTimeDatas.isEmpty()) {
				boolean isTime = false;
				for (Map<String, Object> map : getTimeDatas) {
					String beginTime = map.get("BEGIN_TIME").toString();
					String endTime = map.get("END_TIME").toString();
					// 如果当前时间在 执行时间范围内，  就执行自动收货
					if(stime.compareTo(beginTime)>=0 && stime.compareTo(endTime)<0) {
						isTime = true;
						break;
					}
					/*// 如果不在执行时间范围内， 就不执行后续操作， return 即可
					else{
						//						logger.info("\r\n*********配送收货AutoStockin 时间未到不需调用Start:************\r\n");
						return sReturnInfo;
					}*/
				}
				if(!isTime) {
					return sReturnInfo;
				}
			}
			
			//以下注释，设计不合理， by jinzma 20220429
			/*// 如果没设置自动收货执行时间， 默认12点， 晚上八点执行收货，  这样不影响客户更新
			else {
				if(stime.compareTo("120000")>=0 && stime.compareTo("120500")<0) {
				
				}else{
					if(stime.compareTo("200000")>=0 && stime.compareTo("200500")<0) {
					
					}else{
						logger.info("\r\n*********配送收货AutoStockin 时间未到不需调用Start:************\r\n");
						return sReturnInfo;
					}
				}
			}*/
			
			//查询所有没有做配送收货的收货通知单
			String recevsql="select * from DCP_RECEIVING where DOC_TYPE='0' and status='6' and RECEIPTDATE<='"+sdate+"' "
					+ " order by EID,SHOPID,RECEIVINGNO ";
			List<Map<String, Object>> listrecev=this.doQueryData(recevsql, null);
			if(listrecev!=null&&!listrecev.isEmpty()) {
				boolean isFail = false;
				for (Map<String, Object> maphead : listrecev) {
					// 这里重新定义listdate用于一次事务执行
					List<DataProcessBean> data = new ArrayList<>();
					
					//先判断改门店是否启用了自动收货
					String shopId=maphead.get("SHOPID").toString();
					String eId=maphead.get("EID").toString();
					String receivno=maphead.get("RECEIVINGNO").toString();
					String loadDocNo = maphead.get("LOAD_DOCNO").toString();
					String warehouse = maphead.get("WAREHOUSE").toString();
					String receiptDate = maphead.get("RECEIPTDATE").toString();
					String warehouse_pstockin = warehouse;
					
					try {
						String IsAutoStockin = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "IsAutoStockin");
						if(Check.Null(IsAutoStockin) || !IsAutoStockin.endsWith("Y") ) {
							//logger.info("\r\n*********配送收货AutoStockin 门店:"+SHOPID+"没有启用自动收货************\r\n");
							continue;
						}
						
						//【ID1025546】 配送收货自动收货修复-增加前置天数的判断  by jinzma 20220429
						if (!Check.Null(receiptDate)) {
							String AutoStockInDay = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "AutoStockInDay");
							if (!PosPub.isNumeric(AutoStockInDay)) {
								AutoStockInDay = "0";
							}
							receiptDate = PosPub.GetStringDate(receiptDate, Integer.parseInt(AutoStockInDay));
							if (PosPub.compare_date(sdate,receiptDate) < 0){
								continue;
							}
						}
						
						///获取门店仓库
						if (Check.Null(warehouse)) {
							//取门店的配送收货的默认仓库
							String sqlshop = "select * from DCP_ORG where EID='"+eId+"' AND ORG_FORM='2' and ORGANIZATIONNO='"+shopId+"' and  status='100'  ";
							List<Map<String, Object>> slshop = this.doQueryData(sqlshop, null);
							// 这里还要get一下原料出货的成本仓
							if(slshop==null||slshop.isEmpty()) {
								//默认收货仓没有直接跳过
								loger.info("\r\n*********配送收货AutoStockin 门店:"+shopId+"组织资料为空************\r\n");
								continue;
							}
							warehouse = slshop.get(0).get("IN_COST_WAREHOUSE").toString();
							if (Check.Null(warehouse)) {
								loger.info("\r\n*********配送收货AutoStockin 门店:"+shopId+"仓库资料为空************\r\n");
								continue;
							}
						}
						warehouse_pstockin = warehouse;
						//通过来源单号再判断一次是否已经手工收货，防止重复收货  BY JZMA 20200106
						String checkDocSql = "select * from DCP_stockin "
								+ " where EID='"+eId+"' and SHOPID='"+shopId+"' and load_docno='"+loadDocNo+"' " ;
						List<Map<String, Object>> checkDoc = this.doQueryData(checkDocSql, null);
						if(checkDoc!=null && !checkDoc.isEmpty()) {
							//此单据已经手工收货
							//logger.info("\r\n*********配送收货AutoStockin 门店:"+SHOPID+" load_docno:" +loadDocNo+ "已经手工收货************\r\n");
							continue;
						}
						
						//查找DCP_RECEIVING_DETAIL的表数据
						String recevindetail=" select * from DCP_RECEIVING_DETAIL where EID='"+eId+"' and SHOPID='"+shopId+"' and RECEIVINGNO='"+receivno+"' ";
						List<Map<String, Object>> lisredetail=this.doQueryData(recevindetail, null);
						//插入DCP_STOCKIN以及DCP_STOCKIN_DETAIL
						//产生stockinno的写法
						String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopId);
						String stockInNO = getStockInNO(eId,shopId,accountDate);
						int i=1;
						for (Map<String, Object> recedetail : lisredetail){
							String[] columnsName = {
									"STOCKINNO", "SHOPID", "ITEM", "OITEM", "PLUNO",
									"RECEIVING_QTY", "PUNIT", "PQTY", "BASEUNIT", "BASEQTY", "UNIT_RATIO",
									"PLU_BARCODE", "PRICE", "AMT", "EID", "ORGANIZATIONNO",
									"OTYPE", "OFNO", "OOTYPE", "OOFNO", "OOITEM", "WAREHOUSE","POQTY",
									"BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO",
									"PLU_MEMO","PACKINGNO"
							};
							
							String featureNo=recedetail.get("FEATURENO").toString();
							if (Check.Null(featureNo)) {
								featureNo = " ";
							}
							DataValue[] insValue = new DataValue[]{
									new DataValue(stockInNO, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(i, Types.VARCHAR),
									new DataValue(recedetail.get("ITEM").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PLUNO").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PQTY").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PUNIT").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PQTY").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("BASEUNIT").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("BASEQTY").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("UNIT_RATIO").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PLU_BARCODE").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PRICE").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("AMT").toString(), Types.VARCHAR),
									new DataValue(eId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(maphead.get("DOC_TYPE").toString(), Types.VARCHAR),
									new DataValue(receivno, Types.VARCHAR),
									new DataValue(recedetail.get("OTYPE").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("OFNO").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("OITEM").toString(), Types.VARCHAR),
									new DataValue(warehouse, Types.VARCHAR),
									new DataValue(recedetail.get("POQTY").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("BATCH_NO").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PROD_DATE").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("DISTRIPRICE").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("DISTRIAMT").toString(), Types.VARCHAR),
									new DataValue(sdate, Types.VARCHAR),
									new DataValue(featureNo, Types.VARCHAR),
									new DataValue(recedetail.get("PLU_MEMO").toString(), Types.VARCHAR),
									new DataValue(recedetail.get("PACKINGNO").toString(), Types.VARCHAR),
							};
							
							// 插入DCP_STOCKIN_DETAIL
							InsBean ib = new InsBean("DCP_STOCKIN_DETAIL", columnsName);
							ib.addValues(insValue);
							data.add(new DataProcessBean(ib));
							
							//再插入库存流水表
							String procedure="SP_DCP_StockChange";
							Map<Integer,Object> inputParameter = new HashMap<>();
							inputParameter.put(1,eId);                                         //--企业ID
							inputParameter.put(2,shopId);                                      //--组织
							inputParameter.put(3,"01");                                        //--单据类型
							inputParameter.put(4,stockInNO);	                                 //--单据号
							inputParameter.put(5,i);                                           //--单据行号
							inputParameter.put(6,"1");                                         //--异动方向 1=加库存 -1=减库存
							inputParameter.put(7,sdate);                                       //--营业日期 yyyy-MM-dd
							inputParameter.put(8,recedetail.get("PLUNO").toString());          //--品号
							inputParameter.put(9,recedetail.get("FEATURENO").toString());      //--特征码
							inputParameter.put(10,warehouse);                                  //--仓库	
							inputParameter.put(11,recedetail.get("BATCH_NO").toString());      //--批号
							inputParameter.put(12,recedetail.get("PUNIT").toString());         //--交易单位
							inputParameter.put(13,recedetail.get("PQTY").toString());          //--交易数量
							inputParameter.put(14,recedetail.get("BASEUNIT").toString());      //--基准单位
							inputParameter.put(15,recedetail.get("BASEQTY").toString());       //--基准数量	
							inputParameter.put(16,recedetail.get("UNIT_RATIO").toString());    //--换算比例	
							inputParameter.put(17,recedetail.get("PRICE").toString());         //--零售价
							inputParameter.put(18,recedetail.get("AMT").toString());           //--零售金额
							inputParameter.put(19,recedetail.get("DISTRIPRICE").toString());   //--进货价
							inputParameter.put(20,recedetail.get("DISTRIAMT").toString());     //--进货金额
							inputParameter.put(21,accountDate);                                //--入账日期 yyyy-MM-dd
							inputParameter.put(22,recedetail.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
							inputParameter.put(23,sdate);                                      //--单据日期
							inputParameter.put(24,"");                                         //--异动原因
							inputParameter.put(25,"系统自动收货");                              //--异动描述
							inputParameter.put(26,"admin");                                    //--操作员
							
							ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
							data.add(new DataProcessBean(pdb));
							
							i++;
							
							// 更新关联通知单单身 by jinzma 20210422 【ID1017035】【3.0货郎】按商品行收货(DCP_StockInCreate（收货入库单新建))
							String oItem = recedetail.get("ITEM").toString(); //取收货通知单单身的item
							String pqty = recedetail.get("PQTY").toString();
							UptBean ub = new UptBean("DCP_RECEIVING_DETAIL");
							// add value
							ub.addUpdateValue("STOCKIN_QTY", new DataValue(pqty, Types.VARCHAR));
							ub.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
							// condition
							ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
							ub.addCondition("RECEIVINGNO", new DataValue(receivno, Types.VARCHAR));
							ub.addCondition("ITEM", new DataValue(oItem, Types.VARCHAR));
							data.add(new DataProcessBean(ub));
							
						}
						
						//还有个单头的要插入
						String[] columns1 = {
								"SHOPID", "ORGANIZATIONNO", "EID", "STOCKINNO", "BDATE", "MEMO", "STATUS",
								"DOC_TYPE", "OTYPE", "OFNO","PTEMPLATENO", "CREATEBY", "CREATE_DATE", "CREATE_TIME", "CONFIRMBY",
								"CONFIRM_DATE", "CONFIRM_TIME", "ACCOUNTBY", "ACCOUNT_DATE", "ACCOUNT_TIME", "TOT_PQTY",
								"TOT_AMT", "TOT_CQTY", "LOAD_DOCTYPE", "LOAD_DOCNO", "TRANSFER_SHOP", "STOCKIN_ID", "BSNO", "WAREHOUSE",
								"TOT_DISTRIAMT","RECEIPTDATE","UPDATE_TIME","TRAN_TIME"
						};
						DataValue[] insValue1 = new DataValue[]
								{
										new DataValue(shopId, Types.VARCHAR),
										new DataValue(shopId, Types.VARCHAR),
										new DataValue(eId, Types.VARCHAR),
										new DataValue(stockInNO, Types.VARCHAR),
										new DataValue(sdate, Types.VARCHAR),
										new DataValue("系统自动收货", Types.VARCHAR),
										new DataValue("2", Types.VARCHAR),
										new DataValue("0", Types.VARCHAR),
										new DataValue(maphead.get("DOC_TYPE").toString(), Types.VARCHAR),
										new DataValue(receivno, Types.VARCHAR),
										new DataValue(maphead.get("PTEMPLATENO").toString(), Types.VARCHAR),
										new DataValue("admin", Types.VARCHAR),
										new DataValue(sdate, Types.VARCHAR),
										new DataValue(stime, Types.VARCHAR),
										new DataValue("admin", Types.VARCHAR),
										new DataValue(sdate, Types.VARCHAR),
										new DataValue(stime, Types.VARCHAR),
										new DataValue("admin", Types.VARCHAR),
										new DataValue(accountDate, Types.VARCHAR),
										new DataValue(stime, Types.VARCHAR),
										new DataValue(maphead.get("TOT_PQTY").toString(), Types.VARCHAR),
										new DataValue(maphead.get("TOT_AMT").toString(), Types.VARCHAR),
										new DataValue(maphead.get("TOT_CQTY").toString(), Types.VARCHAR),
										new DataValue(maphead.get("LOAD_DOCTYPE").toString(), Types.VARCHAR),
										new DataValue(maphead.get("LOAD_DOCNO").toString(), Types.VARCHAR),
										new DataValue(maphead.get("TRANSFER_SHOP").toString(), Types.VARCHAR),
										new DataValue("", Types.VARCHAR),
										new DataValue("", Types.VARCHAR),
										new DataValue(warehouse, Types.VARCHAR),
										new DataValue(maphead.get("TOT_DISTRIAMT").toString(), Types.VARCHAR),
										new DataValue(maphead.get("RECEIPTDATE").toString(), Types.VARCHAR),
										new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
										new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
								};
						InsBean ib1 = new InsBean("DCP_STOCKIN", columns1);
						ib1.addValues(insValue1);
						data.add(new DataProcessBean(ib1));
						
						//更新一下收货通知单的status
						UptBean upreceving=new UptBean("DCP_RECEIVING");
						upreceving.addUpdateValue("STATUS", new DataValue("7", Types.VARCHAR));
						upreceving.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
						upreceving.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
						upreceving.addUpdateValue("COMPLETE_DATE", new DataValue(sdate, Types.VARCHAR));
      
						upreceving.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						upreceving.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						upreceving.addCondition("RECEIVINGNO", new DataValue(receivno, Types.VARCHAR));
						data.add(new DataProcessBean(upreceving));
						
						//插入单据检查表 DCP_PLATFORM_BILLCHECK，避免单号重复
						String[] columns = {"EID", "SHOPID", "BILLTYPE", "BILLNO"} ;
						DataValue[] insValue = new DataValue[]
								{
										new DataValue(eId, Types.VARCHAR),
										new DataValue(shopId, Types.VARCHAR),
										new DataValue("receivingStockIn", Types.VARCHAR),
										new DataValue(loadDocNo, Types.VARCHAR),
								};
						InsBean ib = new InsBean("DCP_PLATFORM_BILLCHECK", columns);
						ib.addValues(insValue);
						data.add(new DataProcessBean(ib));

                        // 0 不开启自动入库  1 开启后自动入库（但是产生库存流水）
                        String ReAutoCompletion = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "ReAutoCompletion");
                        PosPub.writelog("AutoStockin定时任务配送收货自动收货，詹记需求，轻加工商品收货自动完工入库参数ReAutoCompletion="+ReAutoCompletion+",企业eId="+eId+",门店organizationNO="+shopId+",配送收货单号stockInNO="+stockInNO+",收货通知单号ReceivingNo="+receivno);
                        if ("1".equals(ReAutoCompletion))
                        {
                            String sql_halfToFinish = getHalfToFinishPluSql(eId,shopId,receivno);
                            PosPub.writelog("AutoStockin定时任务配送收货自动收货，詹记需求，轻加工商品收货自动完工入库,【查询收货通知单】查询资料sql:"+sql_halfToFinish+",企业eId="+eId+",门店organizationNO="+shopId+",配送收货单号stockInNO="+stockInNO+",收货通知单号ReceivingNo="+receivno);
                            List<Map<String, Object>> getQData_halfToFinish = this.doQueryData(sql_halfToFinish, null);
                            if (getQData_halfToFinish==null||getQData_halfToFinish.isEmpty())
                            {
                                PosPub.writelog("AutoStockin定时任务配送收货自动收货，詹记需求，轻加工商品收货自动完工入库,【查询收货通知单】查询无数据，无需处理，企业eId="+eId+",门店organizationNO="+shopId+",配送收货单号stockInNO="+stockInNO+",收货通知单号ReceivingNo="+receivno);
                            }
                            else
                            {
                                PosPub.writelog("AutoStockin定时任务配送收货自动收货，詹记需求，轻加工商品收货自动完工入库,【查询收货通知单】查询轻加工商品完成，组装sql语句开始，企业eId="+eId+",门店organizationNO="+shopId+",配送收货单号stockInNO="+stockInNO+",收货通知单号ReceivingNo="+receivno);
                                String pStockInNO = this.getPStockInNO(eId,shopId,accountDate);
                                List<DataProcessBean> DPB = PosPub.getPStockInSql(eId,shopId,warehouse_pstockin,pStockInNO,accountDate,"admin",receivno,getQData_halfToFinish);
                                if (DPB.isEmpty()||DPB.size()<3)
                                {
                                    PosPub.writelog("AutoStockin定时任务配送收货自动收货，詹记需求，轻加工商品收货自动完工入库,查询轻加工商品完成，组装sql语句结束，【组装异常】，企业eId="+eId+",门店organizationNO="+shopId+",配送收货单号stockInNO="+stockInNO+",收货通知单号ReceivingNo="+receivno);
									continue;
                                    //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"轻加工商品收货时自动完工入库异常！");
                                }
                                PosPub.writelog("AutoStockin定时任务配送收货自动收货，詹记需求，轻加工商品收货自动完工入库,查询轻加工商品完成，组装sql语句结束，【组装成功】，企业eId="+eId+",门店organizationNO="+shopId+",配送收货单号stockInNO="+stockInNO+",收货通知单号ReceivingNo="+receivno+",对应完工入库单号pStockInNO="+pStockInNO);
                                for (DataProcessBean dpb : DPB)
                                {
									data.add(dpb);
                                }
                            }

                        }
						
						StaticInfo.dao.useTransactionProcessData(data);

                        //***********调用库存同步给三方，这是个异步，不会影响效能*****************
                        try
                        {
                            WebHookService.stockSync(eId,shopId,stockInNO);
                        }
                        catch (Exception e)
                        {

                        }
						
						
					} catch (Exception e) {
						//保存JOB异常日志  BY JZMA 20190527
						isFail=true;
						InsertWSLOG.insert_JOBLOG(" "," ","AutoStockin", "配送收货自动收货", e.getMessage());
					}
				}
				if (!isFail) {
					//删除JOB异常日志    BY JZMA 20190527
					InsertWSLOG.delete_JOBLOG(" "," ","AutoStockin");
				}
			}
			
		} catch (Exception e) {
			loger.error("\r\n******配送收货AutoStockin报错信息" + e.getMessage()+"\r\n******\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally {
			bRun=false;//
			loger.info("\r\n*********配送收货AutoStockin定时调用End:************\r\n");
		}
		
		
		return sReturnInfo;
	}
	
	private String getStockInNO(String eId,String shopId,String bDate) throws Exception {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果doctype=0 则固定编码PSSH  如果doctype=1 则固定码为DBSH
		 */
		String stockInNO = "PSSH" + bDate; //matter.format(dt);
		String sql =" select max(stockinno) as stockinno from dcp_stockin"
				+ " where OrganizationNO ='"+shopId+"' and EID ='"+eId+"' and SHOPID ='"+shopId+"' "
				+ " and stockInNO like '" + stockInNO + "%%' and doc_Type='0' " ; // 假資料
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && !getQData.isEmpty()) {
			stockInNO = getQData.get(0).get("STOCKINNO").toString();
			if (!Check.Null(stockInNO)) {
				long i;
				stockInNO = stockInNO.substring(4);
				i = Long.parseLong(stockInNO) + 1;
				stockInNO = i + "";
				stockInNO = "PSSH" + stockInNO;
			}else{
				stockInNO = "PSSH" + bDate + "00001";
			}
		}else{
			stockInNO = "PSSH" + bDate + "00001";
		}
		
		if (getQData != null) {
			getQData.clear();
		}
		
		return stockInNO;
	}

    private String getHalfToFinishPluSql (String eId,String shop,String receivingNo) throws Exception
    {
        String sql = " select A.PLUNO,A.FEATURENO,ITEM,OITEM,PUNIT,PQTY,BASEUNIT,UNIT_RATIO,BASEQTY,PRICE,AMT,WAREHOUSE,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT from DCP_RECEIVING_DETAIL A"
                + " inner join DCP_LIGHTPROGOODS B on A.EID=B.EID AND A.ORGANIZATIONNO=B.SHOPNO AND A.PLUNO=B.PLUNO  AND A.FEATURENO=B.FEATURENO "
                + " WHERE A.EID='"+eId+"' AND A.Organizationno='"+shop+"' AND A.receivingno='"+receivingNo+"'";

        return sql;
    }

    private String getPStockInNO(String eId,String shopId,String bDate) throws Exception
    {
        String pStockInNO = "";
        StringBuffer sqlbuf = new StringBuffer();
        pStockInNO = "WGRK" + bDate;
        sqlbuf.append(""
                + " select max(pstockinno) as pstockinno "
                + " from dcp_pstockin where eid = '"+eId+"' and shopid = '"+shopId+"' "
                + " and pstockinno like '%%" + pStockInNO + "%%' "); // 假資料

        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
        if (getQData != null && !getQData.isEmpty())
        {
            pStockInNO = getQData.get(0).get("PSTOCKINNO").toString();
            if (pStockInNO != null && pStockInNO.length() > 0)
            {
                long i;
                pStockInNO = pStockInNO.substring(4);
                i = Long.parseLong(pStockInNO) + 1;
                pStockInNO = i + "";
                pStockInNO = "WGRK" + pStockInNO;

            }
            else
            {
                pStockInNO = "WGRK" + bDate + "00001";
            }

        }
        else
        {
            pStockInNO = "WGRK" + bDate + "00001";
        }

        return pStockInNO;

    }
}
