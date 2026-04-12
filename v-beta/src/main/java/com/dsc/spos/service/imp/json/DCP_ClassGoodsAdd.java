package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ClassGoodsAddReq;
import com.dsc.spos.json.cust.req.DCP_ClassGoodsAddReq.goods;
import com.dsc.spos.json.cust.res.DCP_ClassGoodsAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;


public class DCP_ClassGoodsAdd extends SPosAdvanceService<DCP_ClassGoodsAddReq, DCP_ClassGoodsAddRes> {

	@Override
	protected void processDUID(DCP_ClassGoodsAddReq req, DCP_ClassGoodsAddRes res) throws Exception {
	// TODO Auto-generated method stub

		String eId = req.geteId();
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

	
		String classType = req.getRequest().getClassType();
		String classNo = req.getRequest().getClassNo();
		List<DCP_ClassGoodsAddReq.goods> goodsList = req.getRequest().getGoodsList();
		
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		String[] columns_class ={"EID","CLASSTYPE","CLASSNO","PLUNO","PLUTYPE","SORTID","LASTMODITIME"
		};
		String sql = "";
		int sortId = 0;
		for (DCP_ClassGoodsAddReq.goods par : goodsList) 
		{
			sortId++;
			String pluNo = par.getPluNo();
			String pluType = par.getPluType();
			if(isRepeat(eId, classType, classNo, pluNo))
			{
				continue;
				//添加商品重复不要报错,直接新增非重复商品
//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品："+pluNo+" 已存在当前菜单分类下！");
			}
			
			
			DataValue[] insValue_class = new DataValue[] 
					{
						new DataValue(eId, Types.VARCHAR),						
						new DataValue(classType, Types.VARCHAR),
						new DataValue(classNo, Types.VARCHAR),
						new DataValue(pluNo,Types.VARCHAR),
						new DataValue(pluType, Types.VARCHAR),
						new DataValue(sortId, Types.VARCHAR),						
						new DataValue(lastmoditime , Types.DATE) 						
					};
			
			InsBean ib_class = new InsBean("DCP_CLASS_GOODS", columns_class);
			ib_class.addValues(insValue_class);
			this.addProcessData(new DataProcessBean(ib_class)); 
			
			
			sql = "";
			sql += " insert into  dcp_class_goods_lang (EID,CLASSTYPE,CLASSNO,PLUNO,LANG_TYPE,DISPLAYNAME)";
			sql =" (select EID,"+"'"+classType+"','"+classNo+"',PLUNO,LANG_TYPE,PLU_NAME "
				+ "from dcp_goods_lang where EID='"+eId+"' AND PLUNO='"+pluNo+"') ";
			
			ExecBean eb =new ExecBean(sql);
			this.addProcessData(new DataProcessBean(eb)); 
			
		}
			
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
		try 
		{
			this.pData.clear();
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
		catch (Exception e) 
		{
	
		}
	  
		
		
	 
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ClassGoodsAddReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ClassGoodsAddReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ClassGoodsAddReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ClassGoodsAddReq req) throws Exception {
	// TODO Auto-generated method stub
	
		boolean isFail = false;
	  StringBuffer errMsg = new StringBuffer("");
	
	  if(req.getRequest()==null)
	  {
	  	errMsg.append("request不能为空值 ");
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	  
	      
	  if(Check.Null(req.getRequest().getClassType())){
	   	errMsg.append("菜单类型不能为空值， ");
	   	isFail = true;
	
	  }
	  if(Check.Null(req.getRequest().getClassNo())){
	   	errMsg.append("分类编码不能为空值 ，");
	   	isFail = true;
	
	  }
	  
	  List<DCP_ClassGoodsAddReq.goods> goodsList = req.getRequest().getGoodsList();
	  if(goodsList==null||goodsList.isEmpty())
	  {
	  	errMsg.append("商品列表不能为空值 ，");
	  	isFail = true;
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	  for (goods par : goodsList) 
	  {
	  	if (Check.Null(par.getPluType()))
	  	{
	  		errMsg.append("商品类型不能为空值， ");
		  	isFail = true;			
	  	}
	  	if (Check.Null(par.getPluNo()))
	  	{
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
	protected TypeToken<DCP_ClassGoodsAddReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ClassGoodsAddReq>(){} ;
	}

	@Override
	protected DCP_ClassGoodsAddRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ClassGoodsAddRes();
	}

	private boolean isRepeat(String eId,String classType,String classNo,String pluNo) throws Exception
	{
		boolean nRet = false;
		
		String sql = "select * from DCP_CLASS_GOODS where EID='"+eId+"' and CLASSTYPE='"+classType+"' "
			+ " and CLASSNO='"+classNo+"' and PLUNO='"+pluNo+"' ";
		List<Map<String, Object>> getData = this.doQueryData(sql, null);
		if(getData!=null&&getData.isEmpty()==false)
		{
			nRet = true;
		}
					
		return nRet;
	}
	
}
