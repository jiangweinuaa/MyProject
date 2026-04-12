package com.dsc.spos.service.imp.json;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.json.cust.req.DCP_ShopCostQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopCostQueryRes;
import com.dsc.spos.scheduler.job.RequisitionCreate;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.xml.utils.ParseXml;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopCostQuery extends SPosBasicService<DCP_ShopCostQueryReq,DCP_ShopCostQueryRes>  {

	Logger logger = LogManager.getLogger(RequisitionCreate.class.getName());
	@Override
	protected boolean isVerifyFail(DCP_ShopCostQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(Check.Null(req.getoShopId()))
		{
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getBeginDate())) 
		{
			errMsg.append("起始日期不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(req.getEndDate())) 
		{
			errMsg.append("截止日期不可为空值, ");
			isFail = true;
		} 	


		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	
	}

	@Override
	protected TypeToken<DCP_ShopCostQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ShopCostQueryReq>(){};
	}

	@Override
	protected DCP_ShopCostQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ShopCostQueryRes();
	}

	@Override
	protected DCP_ShopCostQueryRes processJson(DCP_ShopCostQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String oShopId = req.getoShopId();//传入的门店
		String organizationNO = oShopId;
		String sReturnInfo = "";
		DCP_ShopCostQueryRes res = this.getResponse();
		
				
		//直接调用ERP服务，
		JSONObject payload = new JSONObject();

		// 自定义payload中的json结构
		JSONObject std_data = new JSONObject();
		JSONObject parameter = new JSONObject();

		JSONArray request = new JSONArray();
		JSONObject header = new JSONObject(); // 存一笔资料
		
		header.put("version", "3.0");
		header.put("site_no", req.getoShopId());
		header.put("begin_date", req.getBeginDate());
		header.put("end_date", req.getEndDate());
		request.put(header);
		parameter.put("request", request);
		std_data.put("parameter", parameter);
		payload.put("std_data", std_data);							

		String str = payload.toString();// 将json对象转换为字符串		

		logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******门店绩效成本shopcost.get请求ERP传入参数：  " + str + "\r\n");
		String	resbody = "";	
	  // 执行请求操作，并拿到结果（同步阻塞）
		try 
		{
			resbody=HttpSend.Send(str, "shopcost.get", eId, oShopId,organizationNO,"");

			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******门店绩效成本shopcost.get请求ERP返回参数：  "+ "\r\n" + resbody + "******\r\n");

			JSONObject jsonres = new JSONObject(resbody);
			JSONObject std_data_res = jsonres.getJSONObject("std_data");
			JSONObject execution_res = std_data_res.getJSONObject("execution");

			String code = execution_res.getString("code");
			//String sqlcode = execution_res.getString("sqlcode");
			
			//String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");
			String description ="";
			if  (!execution_res.isNull("description") )
			{
			     description = execution_res.getString("description");
			}
			if (code.equals("0"))
			{
				JSONObject parameter_res = std_data_res.getJSONObject("parameter");
				JSONArray cost_arry = parameter_res.getJSONArray("cost_category");
				res.setDatas(new ArrayList<DCP_ShopCostQueryRes.level1Elm>());
				
				for(int i = 0;i<cost_arry.length();i++)
				{
					
					JSONObject obj = cost_arry.getJSONObject(i);
					
					String item = obj.get("seq").toString();
					String category = obj.get("category_no").toString();
					String categoryName = obj.get("category_name").toString();
					String amount = obj.get("amount").toString();
					
					DCP_ShopCostQueryRes.level1Elm oneLv1 = new DCP_ShopCostQueryRes(). new level1Elm();
					oneLv1.setItem(item);
					oneLv1.setCategory(category);
					oneLv1.setCategoryName(categoryName);
					oneLv1.setAmount(amount);
					oneLv1.setDatas(new ArrayList<DCP_ShopCostQueryRes.level2Elm>());
					try 
					{
						JSONArray detail_arry = obj.getJSONArray("cost_category_detail");
						if(detail_arry!=null)
						{
							for(int j = 0;j < detail_arry.length();j++)
							{
								DCP_ShopCostQueryRes.level2Elm oneLv2 = new DCP_ShopCostQueryRes().new level2Elm();
								
								JSONObject obj_detail = detail_arry.getJSONObject(j);
								String item_detail = obj_detail.get("seq").toString();
								String pluNO = obj_detail.get("item_no").toString();
								String pluName = obj_detail.get("item_name").toString();
								String wunit = obj_detail.get("inventory_unit").toString();
								String wqty = obj_detail.get("qty").toString();
								String price = obj_detail.get("price").toString();
								String amt = obj_detail.get("amount").toString();
								
								oneLv2.setItem(item_detail);
								oneLv2.setPluNO(pluNO);
								oneLv2.setPluName(pluName);
								oneLv2.setWunit(wunit);
								oneLv2.setWqty(wqty);
								oneLv2.setPrice(price);
								oneLv2.setAmt(amt);
							  //添加单身
								oneLv1.getDatas().add(oneLv2);
							}
							
						}								
			    } 
					catch (Exception e) 
					{
			
			    }
				  //添加单头											
					res.getDatas().add(oneLv1);
					
				}
				
			
			}
			else
			{ 
				//
				sReturnInfo="ERP返回错误信息:" + code + "," + description;
				
				//写数据库
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, sReturnInfo);
			}
		} 
		catch (Exception e) 
		{
			//
			sReturnInfo="错误信息:" + e.getMessage();

			//System.out.println(e.toString());

			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******门店绩效成本shopcost.get：门店=" +oShopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号= \r\n报错信息："+e.getMessage()+"******\r\n");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, sReturnInfo);
		}
		
	  return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_ShopCostQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
