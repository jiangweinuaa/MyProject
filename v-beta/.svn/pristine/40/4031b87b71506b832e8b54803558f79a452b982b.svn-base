package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCallBackGetCallBackFailedResultRequest;
import com.dingtalk.api.response.OapiCallBackGetCallBackFailedResultResponse;
import com.dingtalk.api.response.OapiCallBackGetCallBackFailedResultResponse.Failed;
import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ding.DCP_DingCallBack;
import com.dsc.spos.waimai.HelpTools;
import java.util.Calendar;
/**
 * *****************这个类是负责钉钉审批后的单据处理*****************
 * @author Administrator
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DCP_DingApproveProcess extends InitJob  {
	Logger logger = LogManager.getLogger(DCP_DingApproveProcess.class.getName());
	public DCP_DingApproveProcess()
	{

	}

	public String doExe() 
	{
		//返回信息
		String sReturnInfo="";	
		
		try 
		{
			String sql = " select * from DCP_DING_APPROVE where process_status='N' and status='0' and billtype='DCP' "; 
			List<Map<String, Object>> getQDate = StaticInfo.dao.executeQuerySQL(sql, null);
			if (getQDate != null && getQDate.isEmpty() == false) 
			{
			  //从钉钉平台获取未推送成功的资料
				ddCallBackGet(getQDate.get(0).get("EID").toString());
			}
			getQDate.clear();
			
			sql = " select * from DCP_DING_APPROVE where process_status='N' and status<>'0' and billtype='DCP' "; 
			getQDate = StaticInfo.dao.executeQuerySQL(sql, null);
			if (getQDate != null && getQDate.isEmpty() == false) 
			{
				
				for (Map<String, Object> oneDate : getQDate) 
				{
					List<DataProcessBean> data = new ArrayList<DataProcessBean>();
					String oEId=oneDate.get("EID").toString();
					String oShopId=oneDate.get("SHOPID").toString();
					String o_createBy= oneDate.get("CREATEOPID").toString(); 
					String docNo=oneDate.get("BILLNO").toString(); 
					String o_langType="";
					String status= oneDate.get("STATUS").toString();  //状态：0审批提交 1审核同意 2审批驳回 3审核撤回
					String processId = oneDate.get("PROCESSID").toString();

					try 
					{
						//获取多语言
						List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
						if(lstProd!=null && !lstProd.isEmpty())
						{
							o_langType=lstProd.get(0).getHostLang().getValue();
						}
						if (Check.Null(o_langType)) o_langType="zh_CN";

						//获取在途仓
						String o_inv_cost_warehouse="";
						sql = " select inv_cost_warehouse from DCP_ORG where EID='"+oEId+"' and organizationno='"+oShopId+"'  "; 
						getQDate.clear();
						getQDate = StaticInfo.dao.executeQuerySQL(sql, null);
						if (getQDate != null && getQDate.isEmpty() == false) 
						{
							o_inv_cost_warehouse=getQDate.get(0).get("INV_COST_WAREHOUSE").toString();					
						}

						//检查出库单状态==钉钉审批中(-1)
						sql="select status from DCP_STOCKOUT where EID='"+oEId+"' and SHOPID='"+oShopId+"' and STOCKOUTNO='"+docNo+"' and status='-1' ";
						List<Map<String, Object>> isExist=this.doQueryData(sql, null);
						if(isExist !=null && isExist.isEmpty()==false)
						{
							if (status.equals("1"))          //1审核同意
							{
								//调用StockOutProcessDCP服务
								//logger.info("\r\n***************DCP_DingApproveProcess钉钉审核处理开始****************\r\n");
								ParseJson pj=new ParseJson();
								String json="";
								try 
								{
									Map<String,Object> jsonMap=new HashMap<String,Object>();
									jsonMap.put("serviceId", "DCP_StockOutProcess");
									jsonMap.put("token", "abecbc7b42eb286a0d1f8587a9df97e5"); //这个token是无意义的
									jsonMap.put("stockOutNO",docNo);
									jsonMap.put("docType","3" );
									jsonMap.put("ofNO","");
									jsonMap.put("status","2");
									jsonMap.put("oEId",oEId );
									jsonMap.put("oShopId",oShopId);
									jsonMap.put("o_createBy",o_createBy);
									jsonMap.put("o_inv_cost_warehouse",o_inv_cost_warehouse);
									jsonMap.put("o_langType",o_langType);

									json=pj.beanToJson(jsonMap);			
									DispatchService ds = DispatchService.getInstance();
									String resXML = ds.callService(json, StaticInfo.dao);
									if (resXML==null)
									{
										//保存JOB异常日志  BY JZMA 20191016
										InsertWSLOG.insert_JOBLOG(oEId,oShopId, "DCP_DingApproveProcess", "钉钉审批处理", "StockOutProcessDCP 服务返回为空"); 
										continue;
									}
									else
									{
										JSONObject json_res = new JSONObject(resXML);
										String serviceStatus = json_res.optString("serviceStatus");
										String serviceDescription = json_res.optString("serviceDescription");
										if (!serviceStatus.equals("000"))
										{
											//保存JOB异常日志  BY JZMA 20191016
											InsertWSLOG.insert_JOBLOG(oEId,oShopId, "DCP_DingApproveProcess", "钉钉审批处理", serviceDescription); 
											continue;
										}
									}
								}
								catch(Exception e)
								{
									//保存JOB异常日志  BY JZMA 20191016
									InsertWSLOG.insert_JOBLOG(oEId,oShopId, "DCP_DingApproveProcess", "钉钉审批处理", e.getMessage());
									continue;
								}
							}
							else   //2审批驳回   3审核撤回
							{
								//修改出库单的STATUS==0
								UptBean ub =new UptBean("DCP_STOCKOUT");
								ub.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
					            ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					            ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
								ub.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
								ub.addCondition("SHOPID", new DataValue(oShopId, Types.VARCHAR));
								ub.addCondition("ORGANIZATIONNO", new DataValue(oShopId, Types.VARCHAR));
								ub.addCondition("STOCKOUTNO", new DataValue(docNo, Types.VARCHAR));
								data.add(new DataProcessBean(ub));
							}
						}

						//修改PROCESS_STATUS==Y
						UptBean ub =new UptBean("DCP_DING_APPROVE");
						ub.addUpdateValue("PROCESS_STATUS", new DataValue("Y", Types.VARCHAR));
						ub.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
						ub.addCondition("PROCESSID", new DataValue(processId, Types.VARCHAR));
						data.add(new DataProcessBean(ub));

						//删除JOB异常日志    BY JZMA 20191016
						InsertWSLOG.delete_JOBLOG(oEId,oShopId, "DCP_DingApproveProcess");
					}
					catch(Exception e)
					{
						//保存JOB异常日志  BY JZMA 20191016
						InsertWSLOG.insert_JOBLOG(oEId,oShopId, "DCP_DingApproveProcess", "钉钉审批处理", e.getMessage()); 
					}	

					StaticInfo.dao.useTransactionProcessData(data);
				}
			}
		}
		catch (Exception e) 
		{
			logger.error("\r\n***********钉钉审批处理发生异常:" + e.getMessage());
		}
		return sReturnInfo;
	}

	public void ddCallBackGet(String eId) throws Exception
	{
		//获取钉钉平台token
		DCP_DingCallBack dingCallBack = new DCP_DingCallBack();
		Map<String,String> accessTokenMap = dingCallBack.getDingAccessToken(StaticInfo.dao, eId, false);
		String accessToken = accessTokenMap.getOrDefault("token", "").toString();

		//从钉钉拉取未推送成功的审批单
		DingTalkClient  client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/get_call_back_failed_result");
		OapiCallBackGetCallBackFailedResultRequest request = new OapiCallBackGetCallBackFailedResultRequest();
		request.setHttpMethod("GET");
		OapiCallBackGetCallBackFailedResultResponse response = client.execute(request,accessToken);
		List<Failed> fails =  response.getFailedList();
		String sql="";

		if (fails!=null && fails.isEmpty()==false)
		{
			for (Failed par : fails) 
			{
				String bpmsInstanceChange = par.getBpmsInstanceChange();
				String bpmsTaskChange =par.getBpmsTaskChange();
				
				if (!Check.Null(bpmsInstanceChange)) //审批实例修改
				{		
					HelpTools.writelog_fileName("\r\n 钉钉未推送成功获取: " + bpmsInstanceChange + "\r\n" , "dingdingfailed");
					bpmsInstanceChange=bpmsInstanceChange.substring(1, bpmsInstanceChange.length()-1);
					int beginIndex = bpmsInstanceChange.indexOf("processInstanceId");
					int endIndex = bpmsInstanceChange.lastIndexOf("}");
					bpmsInstanceChange = bpmsInstanceChange.substring(beginIndex,endIndex);
					String[] str = bpmsInstanceChange.split(",");
					Map<String,Object> map = new HashMap<>();
					for (int i = 0; i < str.length; i++) 
					{
						String[] str1 = str[i].split("=");
						if (str1.length==2)
						{
							map.put(str1[0].trim(),str1[1].trim());
						}
						else
						{
							map.put(str1[0].trim(),"");
						}
					}

					String type  = map.get("type").toString();
					if (type.equals("terminate"))  //申请人发起撤销
					{
						String processId  = map.get("processInstanceId").toString();
						String finishTime = map.get("finishTime").toString();
						long finishTimeLong = Long.valueOf(finishTime);
						Date finishTimeDate = new Date(finishTimeLong);
						SimpleDateFormat timeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String checkTime =  timeSDF.format(finishTimeDate);
						//查询审批单 因为是撤销单所以审批人空白
						sql="select * from DCP_DING_APPROVE where PROCESSID='"+processId+"' " ;
						List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
						if (getQData != null && getQData.isEmpty() == false)
						{
							eId = getQData.get(0).get("EID").toString();
							String checkOpId = getQData.get(0).get("CREATEOPID").toString();
							String checkOpName = getQData.get(0).get("CREATEOPNAME").toString();

							List<DataProcessBean> data = new ArrayList<DataProcessBean>();
							UptBean up=new UptBean("DCP_DING_APPROVE");
							up.addUpdateValue("CHECKOPID", new DataValue(checkOpId, Types.VARCHAR));
							up.addUpdateValue("CHECKOPNAME", new DataValue(checkOpName, Types.VARCHAR));
							up.addUpdateValue("CHECKTIME", new DataValue(checkTime, Types.DATE));
							up.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));

							if (!Check.Null(eId))
							{
								up.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							}
							up.addCondition("PROCESSID", new DataValue(processId, Types.VARCHAR));
							up.addCondition("STATUS", new DataValue("0", Types.VARCHAR));

							data.add(new DataProcessBean(up));
							StaticInfo.dao.useTransactionProcessData(data);
							data.clear();
						}	
					}
				}

				if (!Check.Null(bpmsTaskChange)) //审批单据修改
				{
					HelpTools.writelog_fileName("\r\n 钉钉未推送成功获取: " + bpmsTaskChange + "\r\n" , "dingdingfailed");
					bpmsTaskChange=bpmsTaskChange.substring(1, bpmsTaskChange.length()-1);
					int beginIndex = bpmsTaskChange.indexOf("processInstanceId");
					int endIndex = bpmsTaskChange.lastIndexOf("}");
					bpmsTaskChange = bpmsTaskChange.substring(beginIndex,endIndex);
					String[] str = bpmsTaskChange.split(",");
					Map<String,Object> map = new HashMap<>();
					for (int i = 0; i < str.length; i++) 
					{
						String[] str1 = str[i].split("=");
						if (str1.length==2)
						{
							map.put(str1[0].trim(),str1[1].trim());
						}
						else
						{
							map.put(str1[0].trim(),"");
						}
					}

					String type  = map.get("type").toString();
					String processId  = map.get("processInstanceId").toString();
					if (type.equals("finish"))
					{
						String result  = map.get("result").toString();
						String staffId  = map.get("staffId").toString();
						String remark = map.getOrDefault("remark", "").toString(); 
						String finishTime = map.get("finishTime").toString();
						long finishTimeLong = Long.valueOf(finishTime);
						Date finishTimeDate = new Date(finishTimeLong);
						SimpleDateFormat timeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String checkTime =  timeSDF.format(finishTimeDate);
						String status="";

						if (result.equals("agree")) status="1";  //状态：1审核同意
						if (result.equals("refuse")) status="2"; //状态：2审批驳回
						sql="select OPNO,USERNAME,USERID,EID from DCP_DING_USERSET where userid ='"+staffId+"' " ;

						List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
						String checkOpId="";
						String checkOpName="";
						if (getQData != null && getQData.isEmpty() == false)
						{
							checkOpId=getQData.get(0).get("USERID").toString();
							checkOpName=getQData.get(0).get("USERNAME").toString();
							eId = getQData.get(0).get("EID").toString();
						}

						//回写审批单
						List<DataProcessBean> data = new ArrayList<DataProcessBean>();
						UptBean up=new UptBean("DCP_DING_APPROVE");
						up.addUpdateValue("CHECKOPID", new DataValue(checkOpId, Types.VARCHAR));
						up.addUpdateValue("CHECKOPNAME", new DataValue(checkOpName, Types.VARCHAR));
						up.addUpdateValue("CHECKTIME", new DataValue(checkTime, Types.DATE));
						up.addUpdateValue("REJECTREASON", new DataValue(remark, Types.VARCHAR));
						up.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

						if (!Check.Null(eId))
						{
							up.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						}
						up.addCondition("PROCESSID", new DataValue(processId, Types.VARCHAR));
						up.addCondition("STATUS", new DataValue("0", Types.VARCHAR));

						data.add(new DataProcessBean(up));
						StaticInfo.dao.useTransactionProcessData(data);
						data.clear();
					}	
				}
			}
		}
	}
}

