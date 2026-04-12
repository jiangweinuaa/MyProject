package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECDCCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDCCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 大物流信息新增
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCCreate extends SPosAdvanceService<DCP_OrderECDCCreateReq , DCP_OrderECDCCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECDCCreateReq req, DCP_OrderECDCCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null; 
		try {
			String eId = req.geteId();
			String dcNo = req.getDcNo();
			String dcName = req.getDcName();
			String lgplatformNo = req.getLgPlatformNo();
			String lgplatformName = req.getLgPlatformName();
			String dcContactman = req.getDcContactman();
			String dcPhone = req.getDcPhone();
			String dcAddress = req.getDcAddress();
			String status = req.getStatus();

			sql = this.isRepeat(req);
			List<Map<String, Object>> repeatDatas = this.doQueryData(sql, null);
			if(repeatDatas.isEmpty()){
				String[] columns1 = { "EID", "DCNO", "DCNAME", "DCADDRESS", 
						"DCPHONE","DCCONTACTMAN","STATUS","LGPLATFORMNO","LGPLATFORMNAME"};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[] { 
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(dcNo, Types.VARCHAR),
						new DataValue(dcName, Types.VARCHAR), 
						new DataValue(dcAddress, Types.VARCHAR),
						new DataValue(dcPhone, Types.VARCHAR) ,
						new DataValue(dcContactman, Types.VARCHAR) ,
						new DataValue(status, Types.VARCHAR),
						new DataValue(lgplatformNo, Types.VARCHAR) ,
						new DataValue(lgplatformName, Types.VARCHAR) 
				};

				InsBean ib1 = new InsBean("OC_LOGISTICS_DC", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
			}
			else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("编码 为 '"+dcNo+" 的大物流信息已存在");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");

		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECDCCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECDCCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECDCCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECDCCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String dcNo = req.getDcNo();

		if (Check.Null(dcNo)) 
		{
			errMsg.append("大物流中心编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECDCCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDCCreateReq>(){};
	}

	@Override
	protected DCP_OrderECDCCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDCCreateRes();
	}


	/**
	 * 验证是否重复
	 * @param dcNo
	 * @return
	 */
	private String isRepeat(DCP_OrderECDCCreateReq req ){
		String eId = req.geteId();
		String dcNo = req.getDcNo();
		String sql = null;
		sql = "select * from OC_LOGISTICS_DC where EID = '"+eId+"' and DCNO = '"+dcNo+"'" ;
		return sql;
	}

}
