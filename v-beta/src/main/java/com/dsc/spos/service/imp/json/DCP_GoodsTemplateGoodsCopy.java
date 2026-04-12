package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsCopyReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsCopyReq.levelPlu;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateGoodsCopyRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateGoodsCopy extends SPosAdvanceService<DCP_GoodsTemplateGoodsCopyReq,DCP_GoodsTemplateGoodsCopyRes>
{

	@Override
	protected void processDUID(DCP_GoodsTemplateGoodsCopyReq req, DCP_GoodsTemplateGoodsCopyRes res) throws Exception 
	{

		String eId=req.geteId();

		
		String sourceTemplateId = req.getRequest().getSourceTemplateId();//来源模板id
		String targetTemplateId = req.getRequest().getTargetTemplateId();//目标模板id
		String isAllGoods = req.getRequest().getIsAllGoods();//是否全部商品Y/N
		String isCover = req.getRequest().getIsCover();//存在时是否覆盖Y/N
		if(isAllGoods.equals("Y"))
		{
			if(isCover.equals("Y"))
			{
				//之前存在的，但是不在来源模板的商品要不要删除？
				/*String execsql = "";
				execsql = " delete from DCP_GOODSTEMPLATE_GOODS where TEMPLATEID='"+targetTemplateId+"' "
						+ "and pluno in (select pluno from DCP_GOODSTEMPLATE_GOODS where TEMPLATEID='"+sourceTemplateId+"' )";
				ExecBean ex = new ExecBean(execsql);*/
				DelBean db1 = new DelBean("DCP_GOODSTEMPLATE_GOODS");
				db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(targetTemplateId,Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				StringBuffer strBuff = new StringBuffer("");
				
				strBuff.append(" insert into DCP_GOODSTEMPLATE_GOODS (");
				strBuff.append(" EID,TEMPLATEID,PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE )");
				strBuff.append(" select EID,'"+targetTemplateId+"',PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE ");
				strBuff.append(" from DCP_GOODSTEMPLATE_GOODS where eid='"+eId+"' and TEMPLATEID='"+sourceTemplateId+"' ");
				String execsql = strBuff.toString();
				ExecBean ex = new ExecBean(execsql);
				
				this.addProcessData(new DataProcessBean(ex));
			}
			else
			{
				StringBuffer strBuff = new StringBuffer();
				strBuff.append(" insert into DCP_GOODSTEMPLATE_GOODS (");
				strBuff.append(" EID,TEMPLATEID,PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE )");
				strBuff.append(" select EID,'"+targetTemplateId+"',PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE ");
				strBuff.append(" from DCP_GOODSTEMPLATE_GOODS where eid='"+eId+"' and TEMPLATEID='"+sourceTemplateId+"' "
						+ " and pluno not in (select pluno from DCP_GOODSTEMPLATE_GOODS where eid='"+eId+"' and TEMPLATEID='"+targetTemplateId+"')");
				String execsql = strBuff.toString();
				ExecBean ex = new ExecBean(execsql);
				
				this.addProcessData(new DataProcessBean(ex));
				
			}
			
		}
		else 
		{
			List<levelPlu> pluNoList = req.getRequest().getPluList();
			if(pluNoList!=null)
			{
				String pluNoListStr = "";
				if(isCover.equals("Y"))
				{
					for (levelPlu par : pluNoList) 
					{
						String pluNo=par.getPluNo();
						pluNoListStr="'"+pluNo+"'"+","+pluNoListStr;

						DelBean db1 = new DelBean("DCP_GOODSTEMPLATE_GOODS");
						db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
						db1.addCondition("TEMPLATEID", new DataValue(targetTemplateId,Types.VARCHAR));
						db1.addCondition("PLUNO", new DataValue(pluNo,Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db1));											
						
					}	
					
					pluNoListStr = pluNoListStr.substring(0,pluNoListStr.length()-1);

					StringBuffer strBuff = new StringBuffer();
					strBuff.append(" insert into DCP_GOODSTEMPLATE_GOODS (");
					strBuff.append(" EID,TEMPLATEID,PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE )");
					strBuff.append(" select EID,'"+targetTemplateId+"',PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE ");
					strBuff.append(" from DCP_GOODSTEMPLATE_GOODS where eid='"+eId+"' and TEMPLATEID='"+sourceTemplateId+"' "
							+ " and pluno in("+pluNoListStr+")");
					String execsql = strBuff.toString();
					ExecBean ex = new ExecBean(execsql);
					
					this.addProcessData(new DataProcessBean(ex));
					
				}
				else
				{
					List<String> targetPluNoList = new ArrayList<String>();
					String sql = "select PLUNO from DCP_GOODSTEMPLATE_GOODS where eid='"+eId+"' and TEMPLATEID='"+targetTemplateId+"' ";
					List<Map<String, Object>> getQData = this.doQueryData(sql, null);
					if(getQData!=null&&getQData.isEmpty()==false)
					{
						for (Map<String, Object> map : getQData)
						{
							targetPluNoList.add(map.get("PLUNO").toString());
						}
						
					}
					
					for (levelPlu par : pluNoList) 
					{
						String pluNo=par.getPluNo();
						if(targetPluNoList.contains(pluNo))//目标模板存在的 ，就不复制
						{
							continue;
						}
							
						pluNoListStr="'"+pluNo+"'"+","+pluNoListStr;
															
					}	
					pluNoListStr = pluNoListStr.substring(0,pluNoListStr.length()-1);

					
					StringBuffer strBuff = new StringBuffer();
					strBuff.append(" insert into DCP_GOODSTEMPLATE_GOODS (");
					strBuff.append(" EID,TEMPLATEID,PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE )");
					strBuff.append(" select EID,'"+targetTemplateId+"',PLUNO,WARNINGQTY,SAFEQTY,CANSALE,CANFREE,CANRETURN,CANORDER,CANPURCHASE,CANREQUIRE,MINQTY,MAXQTY,MULQTY,CANREQUIREBACK,IS_AUTO_SUBTRACT,CANESTIMATE,CLEARTYPE,STATUS,SUPPLIERID,SUPPLIERTYPE ");
					strBuff.append(" from DCP_GOODSTEMPLATE_GOODS where eid='"+eId+"' and TEMPLATEID='"+sourceTemplateId+"' "
							+ " and pluno in("+pluNoListStr+")");
					String execsql = strBuff.toString();
					ExecBean ex = new ExecBean(execsql);
					
					this.addProcessData(new DataProcessBean(ex));
					
					
				}
				
			}
			
		}

		
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateGoodsCopyReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateGoodsCopyReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateGoodsCopyReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateGoodsCopyReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if(Check.Null(req.getRequest().getIsAllGoods()))
		{
			isFail = true;
			errMsg.append("是否删除全部商品isAllGoods不能为空 ");
		}
		if(Check.Null(req.getRequest().getIsCover()))
		{
			isFail = true;
			errMsg.append("存在时是否覆盖isCover不能为空 ");
		}
		    

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsTemplateGoodsCopyReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateGoodsCopyReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateGoodsCopyRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateGoodsCopyRes();
	}




}
