package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PStockInRefundDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PStockInRefundDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PStockInRefundDelete extends SPosAdvanceService<DCP_PStockInRefundDeleteReq, DCP_PStockInRefundDeleteRes>
{

	@Override
	protected void processDUID(DCP_PStockInRefundDeleteReq req, DCP_PStockInRefundDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();;
		String pStockInNO = req.getRequest().getpStockInNo();

		try
		{
			String sql = this.getStatusQuerySql(req);
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)	
			{
				String status = getQDataDetail.get(0).get("STATUS").toString();
				if(!status.equals("0")) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此单已确认，不能删除！");				
				}
				else 
				{
					String pStockInNo_origin = req.getRequest().getpStockInNo_origin();

					//DCP_PSTOCKIN
					DelBean db1 = new DelBean("DCP_PSTOCKIN");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					db1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
					db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));			
					this.addProcessData(new DataProcessBean(db1));

					//DCP_PSTOCKIN_DETAIL
					DelBean db2 = new DelBean("DCP_PSTOCKIN_DETAIL");
					db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					db2.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
					db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));			
					this.addProcessData(new DataProcessBean(db2));

					//2018-09-07新增
					//DCP_PSTOCKIN_MATERIAL
					DelBean db3 = new DelBean("DCP_PSTOCKIN_MATERIAL");
					db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					db3.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
					db3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db3));

                    DelBean db4 = new DelBean("DCP_PSTOCKOUT_BATCH");
                    db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db4.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    db4.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db4));

					if(!Check.Null(pStockInNo_origin))
					{
						UptBean ub2 = new UptBean("DCP_PSTOCKIN");	
						//add Value PSTOCKINNO_REFUND
						ub2.addUpdateValue("PSTOCKINNO_REFUND", new DataValue("", Types.VARCHAR));// 更新原单红冲单单号				
						ub2.addUpdateValue("refundstatus", new DataValue("", Types.VARCHAR));// 更新原单红冲单状态
						ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
						ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
						//condition
						ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub2.addCondition("PSTOCKINNO", new DataValue(pStockInNo_origin, Types.VARCHAR));
						ub2.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));		
						this.addProcessData(new DataProcessBean(ub2));
					}

					this.doExecuteDataToDB();
					res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");
				}
			}
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	protected String getStatusQuerySql(DCP_PStockInRefundDeleteReq req){
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select status from DCP_pstockin "
				+ " where status='0' and EID='"+req.geteId()+"' and organizationno='"+req.getShopId()+"' "
				+ " and pstockinno='"+req.getRequest().getpStockInNo()+"'");
		sql = sqlbuf.toString();
		return sql;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PStockInRefundDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PStockInRefundDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PStockInRefundDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PStockInRefundDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String pStockInNO = req.getRequest().getpStockInNo();
		if (Check.Null(pStockInNO)) {
			isFail = true;
			errMsg.append("红冲单单号不可为空值, ");
		}
		String pStockInNO_origin = req.getRequest().getpStockInNo_origin();
		if(Check.Null(pStockInNO_origin)){
			errMsg.append("原完工入库单号（pStockInNO_origin）不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PStockInRefundDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PStockInRefundDeleteReq>(){};
	}

	@Override
	protected DCP_PStockInRefundDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PStockInRefundDeleteRes();
	}

}
