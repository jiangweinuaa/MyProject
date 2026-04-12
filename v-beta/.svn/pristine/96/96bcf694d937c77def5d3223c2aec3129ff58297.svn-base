package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_POrderRevokeReq;
import com.dsc.spos.json.cust.req.DCP_POrderRevokeReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_POrderRevokeRes;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.google.gson.reflect.TypeToken;
//要货单撤销功能
public class DCP_POrderRevoke extends SPosAdvanceService<DCP_POrderRevokeReq,DCP_POrderRevokeRes>
{

	Logger logger = LogManager.getLogger(DCP_POrderRevoke.class.getName());

	@Override
	protected void processDUID(DCP_POrderRevokeReq req, DCP_POrderRevokeRes res) throws Exception 
	{
		level1Elm request = req.getRequest();
		String pOrderNo = request.getPorderNo();		
		String eId = req.geteId();
		String shopId=req.getShopId();
		try 
		{
			String sql = " "
					+ " with b as (select b.EID,b.SHOPID,c.ofno,b.status,b.receivingno,b.LOAD_DOCNO from DCP_receiving b "
					+ " inner join DCP_receiving_detail c on b.EID=c.EID and b.SHOPID=c.SHOPID and b.receivingno=c.receivingno "
					+ " where b.EID='"+eId+"' and b.SHOPID='"+shopId+"' and c.ofno='"+pOrderNo+"'"
					+ " )"
					+ " select a.porderno,a.process_status,b.status,b.receivingno,b.LOAD_DOCNO,c.org_form from DCP_porder a "
					+ " left join b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.porderno=b.ofno "
					+ " left join dcp_org c on a.eid=c.eid and a.receipt_org=c.organizationno and c.org_form='2' and c.status='100' AND c.isdistbr='Y' "
					+ " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' and a.porderno='"+pOrderNo+"'  ";
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && getQData.isEmpty() == false) {
				String orgForm = getQData.get(0).get("ORG_FORM").toString();
				if (!Check.Null(orgForm)){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "要货申请单："+pOrderNo+" 发货组织为门店,无法撤销");
				}

				String processStatus = getQData.get(0).get("PROCESS_STATUS").toString();
				if (Check.Null(processStatus) || !processStatus.equals("Y"))  //如果直接撤销，可能JOB同时在跑，又上传到ERP了
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "要货申请单："+pOrderNo+" 上传未完成,无法撤销 ");
				}
				for (Map<String, Object> oneData : getQData)
				{
					String status= oneData.get("STATUS").toString();
					if (!Check.Null(status) && status.equals("7"))
					{
						String receivingNo =  oneData.get("RECEIVINGNO").toString();
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "收货通知单："+receivingNo+" 已完成收货，无法撤销 ");		
					}
				}

				//调用ERP 撤销要货申请单
				boolean isSucess = requisitionCancel(pOrderNo,req) ; 
				if (isSucess)
				{
					//更新DCP_PORDER.STATUS更新成新建状态0,DCP_PORDER.PROCESS_STATUS='N'				
					UptBean ub1 = new UptBean("DCP_PORDER");
					ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
					ub1.addUpdateValue("PROCESS_STATUS", new DataValue("N", Types.VARCHAR));
	                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
	                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					//condition
					ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));		
					ub1.addCondition("PORDERNO", new DataValue(pOrderNo, Types.VARCHAR));		
					this.addProcessData(new DataProcessBean(ub1));

					for (Map<String, Object> oneData : getQData)
					{
						String status= oneData.get("STATUS").toString();
						if (!Check.Null(status) && status.equals("6"))
						{
							String receivingNo =  oneData.get("RECEIVINGNO").toString();
							String loadDocNO = oneData.get("LOAD_DOCNO").toString();

							//删除收货通知单 DCP_RECEIVING
							DelBean db1 = new DelBean("DCP_RECEIVING");
							db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							db1.addCondition("RECEIVINGNO", new DataValue(receivingNo, Types.VARCHAR));
							this.addProcessData(new DataProcessBean(db1));
							//删除收货通知单 DCP_RECEIVING_DETAIL
							DelBean db2 = new DelBean("DCP_RECEIVING_DETAIL");
							db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							db2.addCondition("RECEIVINGNO", new DataValue(receivingNo, Types.VARCHAR));
							this.addProcessData(new DataProcessBean(db2));

							//删除 dcp_platform_billcheck
							DelBean db = new DelBean("DCP_PLATFORM_BILLCHECK");
							db.addCondition("EID",  new DataValue(eId, Types.VARCHAR));
							db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR) );
							db.addCondition("BILLTYPE",  new DataValue("receivingCreate", Types.VARCHAR)); 
							db.addCondition("BILLNO", new DataValue(loadDocNO , Types.VARCHAR));      
							this.addProcessData(new DataProcessBean(db));
						}
					}
				}
				else 
				{
					res.setSuccess(false);
					res.setServiceDescription("ERP接口调用失败");
				}

				this.doExecuteDataToDB();
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "要货申请单："+pOrderNo+" 不存在，请重新查询 ");		
			}

		} 
		catch (Exception e) 
		{			
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_POrderRevokeReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_POrderRevokeReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_POrderRevokeReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected boolean isVerifyFail(DCP_POrderRevokeReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		if (request!=null )
		{
			if (Check.Null(request.getPorderNo())) 
			{
				errMsg.append("要货单号不可为空值, ");
				isFail = true;
			}
		}		
		else
		{
			errMsg.append("request不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		errMsg.setLength(0);
		errMsg=null;

		return isFail;
	}
	@Override
	protected TypeToken<DCP_POrderRevokeReq> getRequestType() {
		return new TypeToken<DCP_POrderRevokeReq>(){};
	}
	@Override
	protected DCP_POrderRevokeRes getResponseType() {
		return new DCP_POrderRevokeRes();
	}

	private boolean requisitionCancel(String pOrderNo,DCP_POrderRevokeReq req)throws Exception {
		boolean isSucess = true;
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyyMMddHHmmss");
		String sysTime=dfDatetime.format(cal.getTime());

		//调用ERP接口requisition.cancel			
		JSONObject payload = new JSONObject();
		JSONObject std_data = new JSONObject();
		JSONObject parameter = new JSONObject();
		JSONObject header = new JSONObject();

		// 给单头赋值
		header.put("version", "3.0");//	
		header.put("site_no", req.getShopId());//	
		header.put("modify_no", req.getOpNO());//	
		header.put("modify_datetime", sysTime);//
		header.put("remark", "");//暂时为空
		header.put("front_no", pOrderNo);//	

		parameter.put("requisitioncancel", header);
		std_data.put("parameter", parameter);
		payload.put("std_data", std_data);							

		String str = payload.toString();// 将json对象转换为字符串		
		
		logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******要货撤销服务requisition.cancel请求T100传入参数：  " + str + "\r\n");
		String resbody=HttpSend.Send(str, "requisition.cancel", req.geteId(), req.getShopId(),req.getOrganizationNO(),pOrderNo);
		logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******要货撤销服务requisition.cancel请求T100返回参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n" + resbody + "******\r\n");
		//
		if (resbody.equals("")==false) 
		{
			JSONObject jsonres = new JSONObject(resbody);
			JSONObject std_data_res = jsonres.getJSONObject("std_data");
			JSONObject execution_res = std_data_res.getJSONObject("execution");
			String code = execution_res.getString("code");
			String description ="";
			if  (!execution_res.isNull("description") )
			{
				description = execution_res.getString("description");
			}

			//成功
			if (code.equals("0"))
			{
				isSucess = true;
				//删除WS日志 By jzma 20190524
				InsertWSLOG.delete_WSLOG(req.geteId(), req.getShopId(),"1",pOrderNo);
			}
			else
			{ 
				isSucess=false;
				logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********"+"要货撤销服务requisition.cancel>>>ERP返回错误信息:" + code + "," + description+"************\r\n");
				//写数据库
				InsertWSLOG.insert_WSLOG("requisition.cancel",pOrderNo,req.geteId(),req.getShopId(),"1",str,resbody,code,description) ;
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "要货撤销失败!ERP返回错误信息:" + code + "," + description);
			}

			execution_res=null;
			std_data=null;
			jsonres=null;		
		}
		else 
		{
			isSucess=false;
		}			
		return isSucess;
	}
}
