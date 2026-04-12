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
import com.dsc.spos.json.cust.req.DCP_DinnerTimeCreateReq;
import com.dsc.spos.json.cust.res.DCP_DinnerTimeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 餐段新增
 * @author yuanyy 2019-09-18
 *
 */
public class DCP_DinnerTimeCreate extends SPosAdvanceService<DCP_DinnerTimeCreateReq, DCP_DinnerTimeCreateRes> {

	@Override
	protected void processDUID(DCP_DinnerTimeCreateReq req, DCP_DinnerTimeCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		try {
			String dtNo = req.getRequest().getDtNo();
			String dtName = req.getRequest().getDtName();
			String beginTime = req.getRequest().getBeginTime();
			String endTime = req.getRequest().getEndTime();
			String status = req.getRequest().getStatus();
			String createBy = req.getOpNO();
			String workNo = req.getRequest().getWorkNo();
			String priority = req.getRequest().getPriority()== null?"0":req.getRequest().getPriority().toString();
			
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createDateTime = dfDate.format(cal.getTime());
			
			String sql = this.isRepeat(dtNo, eId, shopId);
			List<Map<String, Object>> repeatDatas = this.doQueryData(sql, null);
			
			if(repeatDatas.isEmpty()){
				
				String[] columns1 = { "EID","SHOPID","DTNO","DTNAME","BEGIN_TIME","END_TIME",
						"CREATE_DATETIME","CREATEBY","STATUS","WORKNO","PRIORITY" };
				DataValue[] insValue1 = null;
				
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(dtNo, Types.VARCHAR),
						new DataValue(dtName, Types.VARCHAR),
						new DataValue(beginTime, Types.VARCHAR),
						new DataValue(endTime, Types.VARCHAR),
						new DataValue(createDateTime, Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(workNo, Types.VARCHAR),
						new DataValue(priority, Types.VARCHAR)
					};
				
				InsBean ib1 = new InsBean("DCP_DINNERTIME", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				this.doExecuteDataToDB();	
				
			}
			else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败:编码为" +dtNo+" 的餐段信息已存在");	
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DinnerTimeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerTimeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerTimeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerTimeCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String dtNo = req.getRequest().getDtNo();
		String dtName = req.getRequest().getDtName();
		String beginTime = req.getRequest().getBeginTime();
		String endTime = req.getRequest().getEndTime();

		if (Check.Null(dtNo)) 
		{
			errCt++;
			errMsg.append("餐段编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(dtName)) 
		{
			errCt++;
			errMsg.append("餐段名称不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(beginTime)) 
		{
			errCt++;
			errMsg.append("开始时间不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(endTime)) 
		{
			errCt++;
			errMsg.append("结束时间不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}


		return isFail;
	}

	@Override
	protected TypeToken<DCP_DinnerTimeCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DinnerTimeCreateReq>(){};
	}

	@Override
	protected DCP_DinnerTimeCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DinnerTimeCreateRes();
	}
	
	/**
	 * 验证是否重复
	 * @param dtNo
	 * @param eId
	 * @return
	 */
	private String isRepeat(String dtNo , String eId , String shopId){
		String sql = null;
	
		sql = "select * from DCP_DINNERTIME "
			+ " where DTNO = '"+dtNo +"' "
			+ " and EID = '"+eId+"'"
					+ " and SHOPID = '"+shopId+"' ";
		return sql;
	}
	
	
	

}
