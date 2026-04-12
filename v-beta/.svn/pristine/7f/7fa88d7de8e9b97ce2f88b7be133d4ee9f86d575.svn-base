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
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsAddReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsAddReq.levelPlu;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateGoodsAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateGoodsAdd extends SPosAdvanceService<DCP_GoodsTemplateGoodsAddReq,DCP_GoodsTemplateGoodsAddRes>
{

	@Override
	protected void processDUID(DCP_GoodsTemplateGoodsAddReq req, DCP_GoodsTemplateGoodsAddRes res) throws Exception 
	{

		String eId=req.geteId();

		
		String templateId = req.getRequest().getTemplateId();
		String sql = "";
		//获取下最大的item项次
		int item = 0;
		sql =" select * from ( select max(item) as item from DCP_GOODSTEMPLATE_GOODS where EID='"+eId+"' AND TEMPLATEID='"+templateId+"' )";
		List<Map<String, Object>> getMaxItem = this.doQueryData(sql, null);
		if(getMaxItem!=null&&getMaxItem.isEmpty()==false)
		{
			try
			{
				item = Integer.parseInt(getMaxItem.get(0).get("ITEM").toString());
				
			} catch (Exception e)
			{
				// TODO: handle exception
			}
		}
		
		sql ="";
		//查询已存在pluno，防止新增的时候直接insert异常
		sql = " select PLUNO from DCP_GOODSTEMPLATE_GOODS where EID='"+eId+"' AND TEMPLATEID='"+templateId+"' ";
		List<Map<String, Object>> getExistPluNoList = this.doQueryData(sql, null);
		List<String> existPluNoList = new ArrayList<String>();
		if(getExistPluNoList!=null&&getExistPluNoList.isEmpty()==false)
		{
			for (Map<String, Object> map : getExistPluNoList)
			{
				existPluNoList.add(map.get("PLUNO").toString());
			}
		}
		
		List<levelPlu> pluList = req.getRequest().getPluList();
		String pluStr = "";
		for (levelPlu par : pluList) 
		{
			String pluNo=par.getPluNo();
			pluStr = pluStr+"'"+pluNo+"'"+",";					
		}	
	    pluStr = pluStr.substring(0,pluStr.length()-1);
	    sql ="";
	    //查询下商品对应的分类 控制表
	    sql = " select * from ("
	    		+ " select A.pluno,B.* from DCP_GOODS A left join DCP_CATEGORY_CONTROL B on A.EID=B.EID AND A.Category=B.Category "
	    		+ " WHERE A.EID='"+eId+"' AND A.pluno in ("+pluStr+") "
	    		+ ")";
	    List<Map<String, Object>> getPluNoCategoryControlList = this.doQueryData(sql, null);
	    
	    String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	    String status = "100";
	    StringBuffer message = new StringBuffer();
	    String[] columns_DCP_GOODSTEMPLATE_GOODS = 
			{ 
					"EID","TEMPLATEID","PLUNO","CANSALE","CANFREE","CANRETURN",
					"CANORDER","CANPURCHASE","CANREQUIRE","CANREQUIREBACK",
					"CANESTIMATE","CLEARTYPE","STATUS","ITEM","LASTMODITIME","SUPPLIERID","SUPPLIERTYPE","CREATEOPID","CREATETIME","LASTMODIOPID"
			};
	    for (levelPlu par : pluList) 
		{
	    	String pluNo=par.getPluNo();
	    	if(existPluNoList.contains(pluNo))
	    	{
	    		message.append(pluNo+",");
	    		continue;
	    	}
	    	else 
	    	{
	    		item++;
	    		String CANSALE = "Y";//可销售N-否Y-是
	    		String CANFREE = "N";//可免单N-否Y-是
	    		String CANRETURN = "Y";//可销退N-否Y-是
	    		String CANORDER = "Y";//可预订N-否Y-是
	    		String CANPURCHASE = "Y";//可采购N-否Y-是
	    		String CANREQUIRE = "Y";//可要货
	    		String CANREQUIREBACK = "Y";//可退仓
	    		//String IS_AUTO_SUBTRACT = "";
	    		String CANESTIMATE = "Y";//可预估N-否Y-是
	    		String CLEARTYPE = "DAY";//估清方式：N-不估清PERIOD-当餐DAY-当天
	    		if(getPluNoCategoryControlList!=null)
	    		{
	    			for (Map<String, Object> map : getPluNoCategoryControlList)
					{
						if(pluNo.equals(map.get("PLUNO").toString()))
						{							
							CANSALE = map.get("CANSALE").toString()==""?"Y":map.get("CANSALE").toString();
				    		CANFREE = map.get("CANFREE").toString()==""?"N":map.get("CANFREE").toString();
				    		CANRETURN = map.get("CANRETURN").toString()==""?"Y":map.get("CANRETURN").toString();
				    		CANORDER = map.get("CANORDER").toString()==""?"Y":map.get("CANORDER").toString();
				    	    CANPURCHASE = map.get("CANPURCHASE").toString()==""?"Y":map.get("CANPURCHASE").toString();
				    		CANREQUIRE = map.get("CANREQUIRE").toString()==""?"Y":map.get("CANREQUIRE").toString();
				    		CANREQUIREBACK = map.get("CANREQUIREBACK").toString()==""?"Y":map.get("CANREQUIREBACK").toString();				    	
				    		CANESTIMATE = map.get("CANESTIMATE").toString()==""?"Y":map.get("CANESTIMATE").toString();
				    		CLEARTYPE = map.get("CLEARTYPE").toString()==""?"DAY":map.get("CLEARTYPE").toString();
				    		break;
						}
					}
	    			
	    		}
	    		
	    		DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(CANSALE, Types.VARCHAR),
						new DataValue(CANFREE, Types.VARCHAR),						
						new DataValue(CANRETURN, Types.VARCHAR),
						new DataValue(CANORDER, Types.VARCHAR),
						new DataValue(CANPURCHASE, Types.VARCHAR),
						new DataValue(CANREQUIRE, Types.VARCHAR),
						new DataValue(CANREQUIREBACK, Types.VARCHAR),
						new DataValue(CANESTIMATE, Types.VARCHAR),
						new DataValue(CLEARTYPE, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),												
						new DataValue(item, Types.INTEGER),
						new DataValue(lastmoditime, Types.DATE)	,
                        new DataValue(par.getSupplierType(), Types.VARCHAR),
                        new DataValue(par.getSupplierId(), Types.VARCHAR),

                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
				};

				InsBean ib1 = new InsBean("DCP_GOODSTEMPLATE_GOODS", columns_DCP_GOODSTEMPLATE_GOODS);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增
	    		
			}
	    	
		}
	    
	    
	    
		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		String str_mes = message.toString();
		if(str_mes.isEmpty()==false)
		{
			res.setServiceDescription("服务执行成功！以下商品编号已存在无需新增("+str_mes+")");
			
		}
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateGoodsAddReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<levelPlu> pluList = req.getRequest().getPluList();

		if (pluList==null || pluList.size()==0) 
		{
			errMsg.append("商品列表不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (levelPlu par : pluList) 
		{
			if(Check.Null(par.getPluNo()))
			{
				errMsg.append("商品编码不能为空值 ");
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
	protected TypeToken<DCP_GoodsTemplateGoodsAddReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateGoodsAddReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateGoodsAddRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateGoodsAddRes();
	}




}
