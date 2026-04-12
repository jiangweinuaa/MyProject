package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_CreditERPCreateReq;
import com.dsc.spos.json.cust.res.DCP_CreditERPCreateRes;
import com.dsc.spos.scheduler.job.InsertWSLOG;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.xml.utils.ParseXml;
import com.google.gson.reflect.TypeToken;

public class DCP_CreditERPCreate extends SPosBasicService<DCP_CreditERPCreateReq, DCP_CreditERPCreateRes>
{
	@Override
	protected boolean isVerifyFail(DCP_CreditERPCreateReq req) throws Exception 
	{
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if (Check.Null(req.getRequest().getPay_type()))
		{
			errMsg.append("支付方式不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getOrder_id()))
		{
			errMsg.append("商户单号不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getPay_amt()))
		{
			errMsg.append("付款金额不可为空值, ");
			isFail = true;
		} 
		
		if (PosPub.isNumericType(req.getRequest().getPay_amt())==false)
		{
			errMsg.append("付款金额必须为数字类型, ");
			isFail = true;
		} 
		
		BigDecimal data1 = new BigDecimal(req.getRequest().getPay_amt());
		BigDecimal data2 = new BigDecimal("0"); 

		if (data1.compareTo(data2)<= 0)
		{			
			errMsg.append("付款金额必须大于0, ");
			isFail = true;
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_CreditERPCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_CreditERPCreateReq>(){};
	}

	@Override
	protected DCP_CreditERPCreateRes getResponseType() 
	{
		return new DCP_CreditERPCreateRes();
	}

	@Override
	protected DCP_CreditERPCreateRes processJson(DCP_CreditERPCreateReq req) throws Exception 
	{
		Logger logger = LogManager.getLogger(DCP_CreditERPCreate.class.getName());

		//查询资料
		DCP_CreditERPCreateRes res = null;
		res = this.getResponse();
		res.setSuccess(false);
		res.setServiceDescription("信用额度ERP增加额度失败!");			

		try 
		{			
			if(req.geteId()==null)
			{
				req.seteId(req.getRequest().geteId());
				req.setShopId(req.getRequest().getShopId());
			}
			String sqlDef = this.getQuerySqlBankno(req);
			List<Map<String, Object>> getQDatacondition = this.doQueryData(sqlDef, null);
			String bankNO="";//客户回款银行账号
			if (getQDatacondition != null && getQDatacondition.isEmpty() == false) 
			{
				bankNO=getQDatacondition.get(0).get("ACCOUNT").toString();					
			}

            sqlDef = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sqlDef, null);
            String thirdShop="";
            if (getQData != null && getQData.isEmpty() == false)
            {
                thirdShop=getQData.get(0).get("THIRD_SHOP").toString();
            }
            getQData=null;
            if( Check.Null(thirdShop) || thirdShop.isEmpty() || thirdShop.trim().equals("") || thirdShop.trim().equals(" ") )
            {
                res.setServiceDescription( "组织信息-本门店加盟支付店号未配置!");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "组织信息-本门店加盟支付店号未配置!");
            }

            String sqlMachid = this.getQuerySqlMACHID(req,thirdShop);
            getQData = this.doQueryData(sqlMachid, null);
            String machId="";//商户ID
            if (getQData != null && getQData.isEmpty() == false)
            {
                machId=getQData.get(0).get("MCHID").toString();
            }

            String sqlPaycode = this.getQuerySqlPaycode(req);
            List<Map<String, Object>> getQDataPaycode = this.doQueryData(sqlPaycode, null);
            String paycode="";//ERP支付方式
            if (getQDataPaycode != null && getQDataPaycode.isEmpty() == false)
            {
                paycode=getQDataPaycode.get(0).get("PAYCODE").toString();
            }




            JSONObject payload = new JSONObject();
			JSONObject std_data = new JSONObject();
			JSONObject parameter = new JSONObject();

			JSONArray request = new JSONArray();
			JSONObject header = new JSONObject();
			
			JSONArray request_detail = new JSONArray(); // 存所有单身			
			
			JSONObject body = new JSONObject(); // 存一笔单身
			body.put("seq", "1");
			body.put("receive_method", paycode);//微信
			body.put("customer_bank_account", bankNO);
			body.put("amount", req.getRequest().getPay_amt());
			request_detail.put(body);

			String pay_date=Check.Null(req.getRequest().getPayDate())?new SimpleDateFormat("yyyyMMdd").format(new Date()):req.getRequest().getPayDate();
			// 给单头赋值
			header.put("doc_no", req.getRequest().getOrder_id());//回款单号
			header.put("shop_no", thirdShop);//门店编号
			header.put("customer", req.getShopId());//客户编号
            header.put("pay_date",pay_date);//支付日期yyyyMMdd
            header.put("id",machId);//商户号

			header.put("transfer_detail", request_detail);
			request.put(header);			

			parameter.put("transfer", request);
			std_data.put("parameter", parameter);
			payload.put("std_data", std_data);							

			String str = payload.toString();// 将json对象转换为字符串		

			String	resbody = "";			
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******信用额度ERP增加额度服务collection.create 单号:"+req.getRequest().getOrder_id()+"请求T100参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n" + str + "******\r\n");
			
			resbody=HttpSend.Send(str, "collection.create", req.geteId(), req.getShopId(),req.getOrganizationNO(),"门店=" +req.getShopId());

			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******信用额度ERP增加额度服务collection.create 单号:"+req.getRequest().getOrder_id()+"请求T100返回参数：  "+ "\r\n" + "门店=" +req.getShopId() + "\r\n" + resbody + "******\r\n");

			JSONObject jsonres = new JSONObject(resbody);
			JSONObject std_data_res = jsonres.getJSONObject("std_data");
			JSONObject execution_res = std_data_res.getJSONObject("execution");

			String code = execution_res.getString("code");
			//String sqlcode = execution_res.getString("sqlcode");

			String description ="";
			if  (!execution_res.isNull("description") )
			{
				description = execution_res.getString("description");
			}
			if (code.equals("0"))
			{						
				res.setSuccess(true);
				res.setServiceDescription("服务执行成功!");			
				String docNo = "";
				String orgNo = "";
				if(std_data_res.has("parameter")){
					JSONObject parameter_res = std_data_res.getJSONObject("parameter");
					if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
						if(!parameter_res.isNull("doc_no"))
						{
							docNo = parameter_res.get("doc_no").toString();
						}
						if(!parameter_res.isNull("org_no"))
						{
							orgNo = parameter_res.get("org_no").toString();
						}
					}
				}
				
				// values
				Map<String, DataValue> values = new HashMap<String, DataValue>();
				DataValue v = new DataValue("Y", Types.VARCHAR);
				values.put("process_status", v);
				DataValue v1 = new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) , Types.DATE);
				values.put("UPLOADTIME", v1);

				//记录ERP 返回的单号和组织
				DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
				DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
				values.put("PROCESS_ERP_NO", docNoVal);
				values.put("PROCESS_ERP_ORG", orgNoVal);
//				DataValue v2 = new DataValue(2, Types.INTEGER);
//				values.put("STATUS", v2);
				// condition
				Map<String, DataValue> conditions = new HashMap<String, DataValue>();
				DataValue c1 = new DataValue(req.getRequest().getOrder_id(), Types.VARCHAR);
				conditions.put("ORDERID", c1);
				DataValue c2 = new DataValue(req.geteId(), Types.VARCHAR);
				conditions.put("EID", c2);
				this.dao.update("DCP_CREDIT_PAY", values, conditions);
				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********"+"信用额度ERP增加额度服务collection.create>>>单号:"+req.getRequest().getOrder_id()+"ERP返回成功信息:" + code + "," + description+"************\r\n");
				//完成***********************************************************************				
			}
			else
			{ 
				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********"+"信用额度ERP增加额度服务collection.create>>>单号:"+req.getRequest().getOrder_id()+"ERP返回错误信息:" + code + "," + description+"************\r\n");
				//写数据库
				InsertWSLOG.insert_WSLOG("collection.create","门店=" +req.getShopId(),req.geteId(),req.getShopId(),"1",str,resbody,code,description) ;
				res.setServiceDescription("信用额度ERP增加额度失败，ERP返回错误信息:" + code + "," + description);

			}
		} 
		catch (Exception e) 
		{
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******信用额度ERP增加额度服务collection.create：门店=" +req.getShopId()+",组织编码=" + req.getOrganizationNO() + ",公司编码=" +req.geteId()  + "\r\n报错信息："+e.getMessage()+"******\r\n");
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_CreditERPCreateReq req) throws Exception 
	{
        String sql="select NVL(THIRD_SHOP,' ') AS THIRD_SHOP FROM DCP_ORG where EID='"+req.geteId()+"' AND ORGANIZATIONNO='"+req.getShopId()+"' ";

        return sql;
	}

	
	protected String getQuerySqlBankno(DCP_CreditERPCreateReq req) throws Exception 
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT * FROM DCP_SHOP_ACCOUNT WHERE EID='"+req.geteId()+"' AND SHOPID='"+req.getShopId()+"' AND status='100' ");
		sql = sqlbuf.toString();
		return sql;
	}

    protected String getQuerySqlMACHID(DCP_CreditERPCreateReq req,String thirdShop) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select MCHID from pay_list where eid='"+req.geteId()+"' and shopid='"+thirdShop+"' and paytype='#P1' and posid='888888' and orderid='"+req.getRequest().getOrder_id()+"' ");
        sql = sqlbuf.toString();
        return sql;
    }

    protected String getQuerySqlPaycode(DCP_CreditERPCreateReq req) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from DCP_PAYTYPE where eid='"+req.geteId()+"' and paytype='#P1' AND STATUS=100 ");
        sql = sqlbuf.toString();
        return sql;
    }	
}
