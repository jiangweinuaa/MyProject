package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsBatchCreateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsBatchCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/*
 * 服务函数:GoodsBatchCreateDCP
 * 服务说明:商品批号新建
 * @author JZMA
 * @since  2019-07-18
 */
public class DCP_GoodsBatchCreate extends SPosAdvanceService<DCP_GoodsBatchCreateReq,DCP_GoodsBatchCreateRes>{

	static boolean bRun=false;//标记此服务是否正在执行中
	
	@Override
	protected void processDUID(DCP_GoodsBatchCreateReq req, DCP_GoodsBatchCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		if (bRun )
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务执行中，请稍后再试");	
		}
		
		bRun=true;//		
		
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String sDate = df.format(cal.getTime());	

		try
		{
			String batchNO=getBatchNO(sDate);
			String serialNO = "";
			
			if (Check.Null(batchNO))
			{
				//新增
				batchNO="1";
				String[] columns = {"SDATE", "BATCHNO" };
				DataValue[] insValue = new DataValue[]{
						new DataValue(sDate, Types.VARCHAR), 
						new DataValue(batchNO, Types.INTEGER)
				};
				InsBean ib = new InsBean("DCP_BATCHNO", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 	
			}
			else 
			{
				//修改
		    Integer batchNO_I = new Integer(batchNO); 
		    batchNO_I=batchNO_I+1;
		    batchNO=String.valueOf(batchNO_I);
		    
		    UptBean ub = null;	
				ub = new UptBean("DCP_BATCHNO");
				ub.addUpdateValue("BATCHNO", new DataValue(batchNO_I, Types.INTEGER));
				ub.addCondition("SDATE", new DataValue(sDate, Types.VARCHAR));	
				this.addProcessData(new DataProcessBean(ub));
		  }
			
			DecimalFormat df_D=new DecimalFormat("00000");
			batchNO = df_D.format(Integer.valueOf(batchNO));
			serialNO = batchNO;
			batchNO=sDate+batchNO ;	
			
			this.doExecuteDataToDB();
			
			res.setSerialNo(serialNO);
			res.setBatchNo(batchNO);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		
		}
		catch(Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		finally 
		{
			bRun=false;//
		}	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsBatchCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsBatchCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsBatchCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsBatchCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsBatchCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_GoodsBatchCreateReq>(){};
	}

	@Override
	protected DCP_GoodsBatchCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_GoodsBatchCreateRes();
	}

	private String getBatchNO(String sDate)  throws Exception {
		String sql = " select * from DCP_BATCHNO where sdate='"+sDate+"'  " ;
		String batchNO="";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			batchNO = getQData.get(0).get("BATCHNO").toString();
		}
		return batchNO;
	}
	
	
	
}
