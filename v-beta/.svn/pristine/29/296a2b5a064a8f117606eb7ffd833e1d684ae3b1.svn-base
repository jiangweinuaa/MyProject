package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_KeyUpdateReq;
import com.dsc.spos.json.cust.res.DCP_KeyUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_KeyUpdate extends SPosAdvanceService<DCP_KeyUpdateReq, DCP_KeyUpdateRes>{

	@Override
	protected void processDUID(DCP_KeyUpdateReq req, DCP_KeyUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer errMsg = new StringBuffer("");
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();;
		
		String type = req.getType();
		List<Map<String, String>> shops = req.getShops();
		List<Map<String, String>> datas = req.getDatas();
		String pShop = "";
		if(shops != null && shops.isEmpty() == false) {
			for (Map<String, String> mshop : shops) {
				pShop = mshop.get("PSHOP").toString();
			}
		}
		
		//当Type=1,插入ta_Key表
		if(type.equals("1")){
			String sql = null;
			if(datas != null && datas.isEmpty() == false) {
				for (Map<String, String> data : datas) {
					boolean existGuid;
					String keyID = data.get("KEYID").toString();
					String kbType = data.get("KBTYPE").toString();
					String kbName = data.get("KBNAME").toString();
					String status = data.get("STATUS").toString();
					String[] conditionValues = { eId, shopId, keyID }; // 查询用户表
					sql = this.getQuerySql_getGuid();
					List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

					if (getQData != null && getQData.isEmpty() == false) {
						existGuid = true;
					} else {			
						existGuid =  false;
					}
					
					if(existGuid == false){
						if(shops != null && shops.isEmpty() == false) {
							String[] columns1 = {
									"KEY_ID","SHOPID","KBTYPE","KBNAME","STATUS","EID"
								};
							DataValue[] insValue1 = null;
							insValue1 = new DataValue[]{
									new DataValue(keyID, Types.VARCHAR),
									new DataValue(pShop, Types.VARCHAR),
									new DataValue(kbType, Types.VARCHAR),		
									new DataValue(kbName, Types.INTEGER),
									new DataValue(status, Types.VARCHAR),
									new DataValue(eId, Types.VARCHAR),												
								};

							InsBean ib1 = new InsBean("DCP_KEY", columns1);
							ib1.addValues(insValue1);
							this.addProcessData(new DataProcessBean(ib1));										
						}
					}
				}
			}
 		}
		//当Type=2,更新ta_Key表
		//当Type=3,复制数据
		else if(type.equals("2") || type.equals("3")){
			//先删除数据
			List<DelBean> dataDel = new ArrayList<DelBean>();
			DelBean db1 = new DelBean("DCP_KEY");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(pShop, Types.VARCHAR));
			dataDel.add(db1);
			
			//再插入数据
			if(datas != null && datas.isEmpty() == false) {
				for (Map<String, String> data : datas) {
					String keyID = "";
					if(type.equals("2")){
						keyID = data.get("KEYID").toString();
					}
					else if(type.equals("3")){
						keyID = UUID.randomUUID().toString().replace("-", "");
					}
					
					String kbType = data.get("KBTYPE").toString();
					String kbName = data.get("KBNAME").toString();
					String status = data.get("STATUS").toString();
					
					if(shops != null && shops.isEmpty() == false) {
						for (Map<String, String> mshop : shops) {
							pShop = mshop.get("PSHOP").toString();
						
							if(shops != null && shops.isEmpty() == false) {
								String[] columns1 = {
										"KEY_ID","SHOPID","KBTYPE","KBNAME","STATUS","EID"
									};
								DataValue[] insValue1 = null;
								insValue1 = new DataValue[]{
										new DataValue(keyID, Types.VARCHAR),
										new DataValue(pShop, Types.VARCHAR),
										new DataValue(kbType, Types.VARCHAR),		
										new DataValue(kbName, Types.INTEGER),
										new DataValue(status, Types.VARCHAR),
										new DataValue(eId, Types.VARCHAR),												
									};
		
								InsBean ib1 = new InsBean("DCP_KEY", columns1);
								ib1.addValues(insValue1);
								this.addProcessData(new DataProcessBean(ib1));
							}
						}
					}
				}
			}
		}
		
		this.doExecuteDataToDB();
		if (res.isSuccess()) 
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_KeyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_KeyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_KeyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_KeyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    
	    //必传值不为空
	    String type = req.getType();
	    List<Map<String, String>> shops = req.getShops();
	    List<Map<String, String>> datas = req.getDatas();
	    
	    if(Check.Null(type)){
			errCt++;
		  	errMsg.append("键盘设置类型不可为空值, ");
		 	isFail = true;
		}
	    
	    for(Map<String, String> par : shops){
	    	if (Check.Null(par.get("PSHOP"))) 
	    	{
		        errCt++;
		        errMsg.append("门店编码不可为空值, ");
		        isFail = true;
	    	}
	    	
	    	if (isFail)
	    	{
		        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		  	}
	    }
	    
	    for(Map<String, String> par : datas){
	    	if (Check.Null(par.get("KEYID"))) 
	    	{
		        errCt++;
		        errMsg.append("键盘ID不可为空值, ");
		        isFail = true;
	    	}
	    	
	    	if (Check.Null(par.get("KBTYPE"))) 
	    	{
		        errCt++;
		        errMsg.append("键盘编号不可为空值, ");
		        isFail = true;
	    	}
	    	
	    	if (Check.Null(par.get("KBNAME"))) 
	    	{
		        errCt++;
		        errMsg.append("键盘名称不可为空值, ");
		        isFail = true;
	    	}
	    	
	    	if (Check.Null(par.get("STATUS"))) 
	    	{
		        errCt++;
		        errMsg.append("状态不可为空值, ");
		        isFail = true;
	    	}
	    	
	    	if (isFail)
	    	{
		        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		  	}
	    }
	    
	    if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_KeyUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_KeyUpdateReq>(){};
	}

	@Override
	protected DCP_KeyUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_KeyUpdateRes();
	}

	protected String getQuerySql_getGuid() throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" select * from ta_key where EID = ? and SHOPID = ? and key_id = ?"); 

		if (sqlbuf.length() > 0)
			sql = sqlbuf.toString();

		return sql;
	}
}
