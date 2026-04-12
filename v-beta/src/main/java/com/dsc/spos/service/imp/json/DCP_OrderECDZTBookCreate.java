package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECDZTBookCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDZTBookCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 大智通配送流水新增
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECDZTBookCreate extends SPosAdvanceService<DCP_OrderECDZTBookCreateReq, DCP_OrderECDZTBookCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECDZTBookCreateReq req, DCP_OrderECDZTBookCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			String eId = req.geteId();
			String dztNo = req.getDztNo();
			String dztDescription = req.getDztDescription();
			String status = req.getStatus();
			String startNo = req.getStartNo();
			String endNo = req.getEndNo();
			String lastNo = req.getLastNo();
			String inputDate = req.getInputDate();
			
			sql = this.isRepeat(eId, dztNo);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData.isEmpty()) {
				String[] columns1 = {
						"EID",
						"DZTNO",
						"DZTDESCRIPTION",
						"INPUTDATE",
						"DZT_STARTNO",
						"DZT_ENDNO",
						"DZT_LASTNO",
						"STATUS"
							};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(dztNo, Types.VARCHAR),
							new DataValue(dztDescription, Types.VARCHAR),
							new DataValue(inputDate, Types.VARCHAR),
							new DataValue(startNo, Types.VARCHAR),
							new DataValue(endNo, Types.VARCHAR),
							new DataValue(lastNo, Types.VARCHAR),
							new DataValue(status, Types.VARCHAR)
						};
				InsBean ib1 = new InsBean("OC_SHIPBOOKDZT", columns1);
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
				res.setServiceDescription("流水段编码 ：" +dztNo+" 已存在，请勿重复添加");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECDZTBookCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECDZTBookCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECDZTBookCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECDZTBookCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String dztNO = req.getDztNo().toString();
		String dztDescription = req.getDztDescription().toString();
		
		if (Check.Null(dztNO)) 
		{
			errCt++;
			errMsg.append("流水段编码不可为空值  ");
			isFail = true;
		}
		if (Check.Null(dztDescription)) 
		{
			errCt++;
			errMsg.append("说明信息不可为空值  ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECDZTBookCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDZTBookCreateReq>(){};
	}

	@Override
	protected DCP_OrderECDZTBookCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDZTBookCreateRes();
	}
	/**
	 * 验证是否已存在
	 * @param eId
	 * @param dztNO
	 * @return
	 */
	private String isRepeat(String eId, String dztNo){
		String sql = "select dztNo from OC_SHIPBOOKDZT where EID = '"+eId+"' "
				+ " and dztNo = '"+dztNo+ "' ";
		return sql;
		
	}
}
