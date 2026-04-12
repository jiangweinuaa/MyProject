package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;

import com.dsc.spos.json.cust.req.DCP_RequisitionUpdateReq;

import com.dsc.spos.json.cust.res.DCP_RequisitionUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//要货单接收ERP审核/反审核
public class DCP_RequisitionUpdate extends SPosAdvanceService<DCP_RequisitionUpdateReq,DCP_RequisitionUpdateRes>
{
	private Logger loger = LogManager.getLogger(DCP_RequisitionUpdate.class.getName());
	@Override
	protected void processDUID(DCP_RequisitionUpdateReq req, DCP_RequisitionUpdateRes res) throws Exception {
		try 
		{
			String shopId = req.getShopId();
			String loadtype = req.getLoad_type();//0:反审核   1：审核
			String porderno = req.getpOrderno();
			String sql = this.getQuerySql_Check(req);
			List<Map<String, Object>> getQData_Check = this.doQueryData(sql, null);
			if(getQData_Check == null || getQData_Check.isEmpty() == true)
			{
				res.setDoc_no(porderno);
				res.setOrg_no(shopId);
				
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("site_no=" +shopId+ ",front_no=" +porderno + "的单据不存在！");	
			}
			else 
			{
				//要货单状态
				String status=getQData_Check.get(0).get("STATUS").toString();
				if (loadtype.equals("1")) //审核
				{
					if (status.equals("2")==false) 
					{
						//云中台要货单状态必须是已提交才能执行审核，直接抛异常
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "云中台要货单状态必须是已提交才能执行审核！");		
					}
				}
				else //反审核
				{
					if (status.equals("6")==false) 
					{
						//云中台要货单状态必须是已审核才能执行反审核，直接抛异常
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "云中台要货单状态必须是已审核才能执行反审核！");		
					}
				}	
				
				List<Map<String, String>> jsonDatas = req.getDatas();
				for (Map<String, String> par : jsonDatas) 
				{
					String item=par.get("oitem");
					String pluNO=par.get("pluNO");
					//String punit=par.get("punit");//暂时先不处理单位换算的问题
					String pqty=par.get("pqty");//审核量
					
					Map<String, Object> condiV=new HashMap<String, Object>();					
					condiV.put("ITEM", item);
					condiV.put("PLUNO", pluNO);
					List<Map<String, Object>> detailList= MapDistinct.getWhereMap(getQData_Check, condiV, false);
					if (detailList!=null && detailList.size()>0) 
					{
						UptBean ub1 = new UptBean("DCP_PORDER_DETAIL");
						if (loadtype.equals("1")) //审核
						{
							ub1.addUpdateValue("REVIEW_QTY", new DataValue(pqty, Types.FLOAT));
						}
						else //反审核
						{
							ub1.addUpdateValue("REVIEW_QTY", new DataValue("0", Types.FLOAT));
						}						
						
						//condition
						ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
						ub1.addCondition("PORDERNO", new DataValue(porderno, Types.VARCHAR));
						ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						ub1.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));	
						ub1.addCondition("ITEM", new DataValue(item, Types.INTEGER));	
						
						this.addProcessData(new DataProcessBean(ub1));							
					}
					else 
					{					  
						//ERP审核商品与中台订单商品不一致直接抛异常
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "ERP审核商品与中台要货商品不一致！");		
					}					
				}	
				
				if (this.pData.size()>0) 
				{
					UptBean ub1 = new UptBean("DCP_PORDER");
					
					if (loadtype.equals("1")) //审核
					{
						ub1.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));//已审核
					}
					else //反审核
					{
						ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));//反审核
					}					
	                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
	                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					//condition
					ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("PORDERNO", new DataValue(porderno, Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));		
					this.addProcessData(new DataProcessBean(ub1));				
					
					this.doExecuteDataToDB();
					
					res.setDoc_no(porderno);
					res.setOrg_no(shopId);
					res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");	
				}				
			}			
		}
		catch (Exception e) 
		{
			StringWriter errors = new StringWriter();
			PrintWriter pw=new PrintWriter(errors);
			e.printStackTrace(pw);		
			
			pw.flush();
			pw.close();			
			
			errors.flush();
			errors.close();
			
			loger.error("\r\n*********要货单审核/反审核失败,"+errors.toString()+"*************\r\n");
			
			pw=null;
			errors=null;
			
			this.pData.clear();

			//插入失败
			res.setDoc_no("");
			res.setOrg_no("");
			res.setSuccess(false);
			res.setServiceStatus("000");
			res.setServiceDescription(e.getMessage());			
		}		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RequisitionUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RequisitionUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RequisitionUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RequisitionUpdateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<Map<String, String>> jsonDatas = req.getDatas();

		//必传值不为空
		String shopId = req.getShopId();
		String loadtype = req.getLoad_type();//0:反审核   1：审核
		String porderno = req.getpOrderno();
		
		if (Check.Null(shopId)) 
		{
			errMsg.append("门店(site_no)不可为空值, ");
			isFail = true;
		} 
		if(Check.Null(loadtype))
		{
			errMsg.append("来源类型(load_type)不可为空值, ");
			isFail = true;
		}		
		if(Check.Null(porderno))
		{
			errMsg.append("前端单号(front_no)不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		
		for (Map<String, String> par : jsonDatas) 
		{			
			if (Check.Null(par.get("item"))) 
			{
				errMsg.append("项次(seq)不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(par.get("oitem"))) 
			{
				errMsg.append("来源项次(load_seq)不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(par.get("pluNO"))) 
			{
				errMsg.append("商品编号(item_no)不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(par.get("punit"))) 
			{
				errMsg.append("包装单位(packing_unit)不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(par.get("pqty"))) 
			{
				errMsg.append("包装数量(packing_qty)不可为空值, ");
				isFail = true;
			} 
			else 
			{
				if (PosPub.isNumericType(par.get("pqty"))==false) 
				{
					errMsg.append("包装数量(packing_qty)必须是数字类型, ");
					isFail = true;
				}
			}
			
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
			
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_RequisitionUpdateReq> getRequestType() {
		
		return new TypeToken<DCP_RequisitionUpdateReq>(){};
	}

	@Override
	protected DCP_RequisitionUpdateRes getResponseType() {
		
		return new DCP_RequisitionUpdateRes();
	}
	
	protected String getQuerySql_Check(DCP_RequisitionUpdateReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select a.status,b.porderno,b.item,b.pluno,b.punit,b.pqty,b.baseunit,b.baseqty,b.unit_ratio,b.featureno from dcp_porder a "
				+ " inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno and a.bdate=b.bdate "
				+ " where a.eid='"+req.geteId()+"' and a.shopid='"+req.getShopId()+"' and a.porderno='"+req.getpOrderno()+"' and a.process_status='Y' ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	

}
