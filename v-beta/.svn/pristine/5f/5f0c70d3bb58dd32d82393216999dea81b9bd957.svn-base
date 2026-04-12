package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ClassGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ClassGoodsDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ClassGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.BinaryClient.LIST_POSITION;

public class DCP_ClassGoodsDelete extends SPosAdvanceService<DCP_ClassGoodsDeleteReq, DCP_ClassGoodsDeleteRes> {

	@Override
	protected void processDUID(DCP_ClassGoodsDeleteReq req, DCP_ClassGoodsDeleteRes res) throws Exception 
	{
		
		String eId= req.geteId();		
		//清缓存
		String posUrl =  PosPub.getPOS_INNER_URL(eId);
		String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
				" AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
		List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
		String apiUserCode = "";
		String apiUserKey = "";
		if (result != null && result.size() == 2) {
			for (Map<String, Object> map : result) {
				if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
					apiUserCode = map.get("ITEMVALUE").toString();
				} else {
					apiUserKey = map.get("ITEMVALUE").toString();
				}
			}
		}
		PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,eId);

		try 
		{

			String sql = "";

			//用于更新 菜单分类下商品数量
			List<DCP_ClassGoodsDeleteReq.level1Elm> updateClassGoodsList = new ArrayList<DCP_ClassGoodsDeleteReq.level1Elm>();

			for (level1Elm par : req.getRequest().getGoodsList())
			{
				String classType = par.getClassType();
				String classNo = par.getClassNo();	
				String pluNo = par.getPluNo();	
				/*sql = "";
				sql = "select status from DCP_CLASS "
					+ "where status='-1' and eid='"+eId+"' and CLASSTYPE='"+classType+"' and CLASSNO='"+classNo+"' ";
				List<Map<String, Object>> getData = this.doQueryData(sql, null);
				if(getData==null||getData.isEmpty())
				{
					continue;
				}*/
				boolean isExist = false;

				for (level1Elm level1Elm : updateClassGoodsList) 
				{
					if(level1Elm.getClassType().equals(classType)&&level1Elm.getClassNo().equals(classNo))
					{
						isExist = true;
						break;
					}		

				}

				if(!isExist)
				{
					DCP_ClassGoodsDeleteReq.level1Elm oneLv1 = req.new level1Elm();
					oneLv1.setClassType(classType);					
					oneLv1.setClassNo(classNo);
					updateClassGoodsList.add(oneLv1);
				}		

				DelBean db1 = new DelBean("DCP_CLASS_goods");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
				db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
				db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				db1 = new DelBean("DCP_CLASS_goods_lang");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
				db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
				db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));				
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

			//用于更新 菜单分类下商品数量
			try 
			{
				for (level1Elm par : updateClassGoodsList)
				{
					this.pData.clear();
					sql = "";
					String classType = par.getClassType();
					String classNo = par.getClassNo();	
					//更新菜单表下属商品数量
					sql = "select * from "
							+ "select count(*) as CLASSGOODSCOUNT  from dcp_class_goods "
							+ "where EID='"+eId+"' and CLASSTYPE='"+classType+"' and CLASSNO='"+classNo+"' "
							+ ")";
					List<Map<String, Object>> getClassGoodsCount = this.doQueryData(sql, null);
					if(getClassGoodsCount!=null&&getClassGoodsCount.isEmpty()==false)
					{
						int count = Integer.parseInt(getClassGoodsCount.get(0).get("CLASSGOODSCOUNT").toString());

						UptBean up1 = new UptBean("DCP_CLASS");

						up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						up1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
						up1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));

						up1.addUpdateValue("CLASSGOODSCOUNT", new DataValue(count, Types.VARCHAR));

						this.doExecuteDataToDB();

					}

				}		
			} 
			catch (Exception e) 
			{

			}


		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());
		}





	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ClassGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ClassGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ClassGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ClassGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (req.getRequest().getGoodsList()==null) 
		{
			errMsg.append("编码不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}


		for (level1Elm par : req.getRequest().getGoodsList())
		{
			String classType = par.getClassType(); 
			String classNo = par.getClassNo();
			String pluNo = par.getPluNo();
			if(Check.Null(classType)){
				errMsg.append("类型不能为空值 ，");
				isFail = true;
			}
			if(Check.Null(classNo)){
				errMsg.append("分类编码不能为空值 ，");
				isFail = true;
			}
			if(Check.Null(pluNo)){
				errMsg.append("商品编码不能为空值 ，");
				isFail = true;
			}
		}



		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ClassGoodsDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ClassGoodsDeleteReq>(){};
	}

	@Override
	protected DCP_ClassGoodsDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ClassGoodsDeleteRes();
	}

}
