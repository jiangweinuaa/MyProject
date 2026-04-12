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
import com.dsc.spos.json.cust.req.DCP_PGoodsClassCreateReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsClassCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsClassCreate extends SPosAdvanceService<DCP_PGoodsClassCreateReq, DCP_PGoodsClassCreateRes>
{

	@Override
	protected void processDUID(DCP_PGoodsClassCreateReq req, DCP_PGoodsClassCreateRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		String sql = null;
		sql = this.getPGoodsClassNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{

			//DCP_PACKAGECLASS
			String[] columnsModular = 
				{
						"EID",
						"PCLASSNO",
						"PCLASSNAME",
						"UPDATE_TIME",
						"STATUS"
				};


			DataValue[] insValue1 = null;

			insValue1 = new DataValue[] 
					{ 
							new DataValue(req.geteId(), Types.VARCHAR),
							new DataValue(req.getRequest().getPclassNo(), Types.VARCHAR),
							new DataValue(req.getRequest().getPclassName(), Types.VARCHAR), 							
							new DataValue(mySysTime, Types.VARCHAR),
							new DataValue(100, Types.INTEGER),
					};

			InsBean ib1 = new InsBean("DCP_PACKAGECLASS", columnsModular);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		else
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("套餐类别编码已经存在！");			
		}		

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PGoodsClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsClassCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsClassCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PGoodsClassCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String PclassNO =req.getRequest().getPclassNo();
		String PclassName =req.getRequest().getPclassName();


		if (Check.Null(PclassNO)) 
		{
			errMsg.append("套餐类别编码不可为空值, ");
			isFail = true;
		} 


		if (Check.Null(PclassName)) 
		{
			errMsg.append("套餐类别名称不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsClassCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsClassCreateReq>(){};
	}

	@Override
	protected DCP_PGoodsClassCreateRes getResponseType() 
	{
		return new DCP_PGoodsClassCreateRes();
	}

	protected String getPGoodsClassNO_SQL(DCP_PGoodsClassCreateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_PACKAGECLASS WHERE EID='"+req.geteId()+"' AND PCLASSNO='"+req.getRequest().getPclassNo()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}


}
