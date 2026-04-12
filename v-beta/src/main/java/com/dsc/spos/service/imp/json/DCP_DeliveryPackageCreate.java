package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DeliveryPackageCreateReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryPackageCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 货运包裹新增
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_DeliveryPackageCreate extends SPosAdvanceService<DCP_DeliveryPackageCreateReq, DCP_DeliveryPackageCreateRes> {

	@Override
	protected void processDUID(DCP_DeliveryPackageCreateReq req, DCP_DeliveryPackageCreateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		String sql = null;
		try 
		{
			String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String eId = req.geteId();
			String packageNo = req.getRequest().getPackageNo();
			String packageName = req.getRequest().getPackageName();
			String measureNo = req.getRequest().getMeasureNo();
			String measureName = req.getRequest().getMeasureName();
			String temperateNo = req.getRequest().getTemperateNo();
			String temperateName = req.getRequest().getTemperateName();
			String packageFee = req.getRequest().getPackageFee();
			String status = req.getRequest().getStatus();

			sql = this.isRepeat(eId, packageNo);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData.isEmpty()) {
				String[] columns1 = {
						"EID",
						"PACKAGENO",
						"PACKAGENAME",
						"MEASURENO",
						"MEASURENAME",
						"TEMPERATENO",
						"TEMPERATENAME",
						"PACKAGEFEE",
						"STATUS",
						"CREATEOPID",
						"CREATEOPNAME",
						"CREATETIME",
						"LASTMODIOPID",
						"LASTMODIOPNAME",
						"LASTMODITIME"
				};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(packageNo, Types.VARCHAR),
								new DataValue(packageName, Types.VARCHAR),
								new DataValue(measureNo, Types.VARCHAR),
								new DataValue(measureName, Types.VARCHAR),
								new DataValue(temperateNo, Types.VARCHAR),
								new DataValue(temperateName, Types.VARCHAR),
								new DataValue(packageFee, Types.VARCHAR),						
								new DataValue(status, Types.VARCHAR),
								new DataValue(req.getOpNO(), Types.VARCHAR),
								new DataValue(req.getOpName(), Types.VARCHAR),
								new DataValue(lastmoditime, Types.DATE),
								new DataValue(req.getOpNO(), Types.VARCHAR),
								new DataValue(req.getOpName(), Types.VARCHAR),
								new DataValue(lastmoditime, Types.DATE)

						};
				InsBean ib1 = new InsBean("DCP_SHIPPACKAGESET", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增单头
				this.doExecuteDataToDB();
				if (res.isSuccess()) 
				{
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");						
				} 

			}else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("编码为" +packageNo+" 包裹信息 已存在，请勿重复添加");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DeliveryPackageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DeliveryPackageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DeliveryPackageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DeliveryPackageCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String packageNO = req.getRequest().getPackageNo();
		String packageName = req.getRequest().getPackageName();
		if(Check.Null(packageNO))
		{
			errCt++;
			errMsg.append("包裹编号不可为空值, ");
			isFail = true;
		}
		if(Check.Null(packageName))
		{
			errCt++;
			errMsg.append("包裹编号不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliveryPackageCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliveryPackageCreateReq>(){};
	}

	@Override
	protected DCP_DeliveryPackageCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliveryPackageCreateRes();
	}

	/**
	 * 验证是否已存在
	 * @param packageNo
	 * @param eId
	 * @return
	 */
	private String isRepeat( String eId , String packageNo ){
		String sql = null;
		sql = "select packageNo from dcp_shippackageset "
				+ " where  packageNo = '"+packageNo +"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}





}
