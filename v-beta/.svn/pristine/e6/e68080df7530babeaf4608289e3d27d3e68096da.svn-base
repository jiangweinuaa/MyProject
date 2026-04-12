package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BizPartnerCreateReq;
import com.dsc.spos.json.cust.req.DCP_LocationUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PayDateAlterReq;
import com.dsc.spos.json.cust.req.DCP_LocationUpdateReq.LocationList;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

import com.dsc.spos.json.cust.JsonRes;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交易对象
 * 
 * @date 2024-10-23
 * @author 01029
 */
public class DCP_LocationUpdate extends SPosAdvanceService<DCP_LocationUpdateReq, JsonRes> {

	@Override
	protected void processDUID(DCP_LocationUpdateReq req, JsonRes res) throws Exception {

		try {
			// String oprType = req.getRequest().getOprType();//I insert U
			// update

			processOnCreate(req, res);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:" + e.getMessage());

		}
	}

	private void processOnCreate(DCP_LocationUpdateReq req, JsonRes res) throws Exception {

		String eId = req.geteId();
		String orgNo = req.getRequest().getOrgNo();
		String ware = req.getRequest().getWareHouse();
		String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String sql = null;
		//sql = this.isRepeat(eId, orgNo, ware);

		String locationSql="select * from DCP_LOCATION a where a.eid='"+eId+"' and a.organizationno='"+orgNo+"' and a.warehouse='"+req.getRequest().getWareHouse()+"'";
		List<Map<String, Object>> yetList = this.doQueryData(locationSql, null);


		List<Map<String, Object>> mDatas = null;
		if (SUtil.EmptyList(mDatas)) {

//			UptBean ub1 = null;
//			ub1 = new UptBean("DCP_LOCATION");
//			ub1.addCondition("ORGANIZATIONNO", new DataValue(orgNo, Types.VARCHAR));
//			ub1.addCondition("WAREHOUSE", new DataValue(ware, Types.VARCHAR));
			
			//DelBean db2 = new DelBean("DCP_LOCATION");
            //db2.addCondition("ORGANIZATIONNO", new DataValue(orgNo, Types.VARCHAR));
            //db2.addCondition("WAREHOUSE", new DataValue(ware, Types.VARCHAR));
            //this.addProcessData(new DataProcessBean(db2));
            ColumnDataValue columns = new ColumnDataValue();
        	String[] columns1 =null;
        	DataValue[] insValue1 =null;
			List<LocationList> listLoc = req.getRequest().getLocationList();
			if (!SUtil.EmptyList(listLoc)) {
				for (DCP_LocationUpdateReq.LocationList par : listLoc) {
//					ub1.addUpdateValue("LOCATIONNAME", new DataValue(par.getLocationName(), Types.VARCHAR));
//					ub1.addUpdateValue("CONTENT", new DataValue(par.getContent(), Types.VARCHAR));
//					ub1.addUpdateValue("STATUS", new DataValue(par.getStatus(), Types.VARCHAR));
//
//					ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
//					ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

					List<Map<String, Object>> filterRows = yetList.stream().filter(x -> x.get("LOCATION").toString().equals(par.getLocation())).collect(Collectors.toList());
					if(filterRows.size()<=0) {
						columns.Columns.clear();
						columns.DataValues.clear();
						columns.Add("EID", eId, Types.VARCHAR);
						columns.Add("ORGANIZATIONNO", orgNo, Types.VARCHAR);
						columns.Add("WAREHOUSE ", ware, Types.VARCHAR);
						columns.Add("LOCATIONNAME", par.getLocationName(), Types.VARCHAR);
						columns.Add("CONTENT", par.getContent(), Types.VARCHAR);
						columns.Add("LOCATION", par.getLocation(), Types.VARCHAR);
						columns.Add("STATUS", Integer.valueOf(par.getStatus()), Types.INTEGER);
						columns.Add("LASTMODIOPID ", req.getEmployeeNo(), Types.VARCHAR);
						columns.Add("LASTMODITIME ", lastmoditime, Types.DATE);
						columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR);
						columns.Add("CREATEDEPTID", req.getDepartmentNo(), Types.VARCHAR);
						columns.Add("CREATETIME", lastmoditime, Types.DATE);
						columns.Add("SORTID", par.getSortId(), Types.VARCHAR);
						columns.Add("WAREREGIONNO", par.getWareRegionNo(), Types.VARCHAR);
						columns.Add("LOCATIONTYPE", par.getLocationType(), Types.VARCHAR);


						columns1 = columns.Columns.toArray(new String[0]);
						insValue1 = columns.DataValues.toArray(new DataValue[0]);

						InsBean ib2 = new InsBean("DCP_LOCATION", columns1);
						ib2.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib2));
					}else{
						UptBean ub1 = new UptBean("DCP_LOCATION");
						ub1.addUpdateValue("LOCATIONNAME", new DataValue(par.getLocationName(), Types.VARCHAR));
				    	ub1.addUpdateValue("CONTENT", new DataValue(par.getContent(), Types.VARCHAR));
			    		ub1.addUpdateValue("STATUS", new DataValue(par.getStatus(), Types.VARCHAR));
						ub1.addUpdateValue("SORTID", new DataValue(par.getSortId(), Types.VARCHAR));
						ub1.addUpdateValue("WAREREGIONNO", new DataValue(par.getWareRegionNo(), Types.VARCHAR));
						ub1.addUpdateValue("LOCATIONTYPE", new DataValue(par.getLocationType(), Types.VARCHAR));


				    	ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
				    	ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub1.addCondition("ORGANIZATIONNO", new DataValue(orgNo, Types.VARCHAR));
						ub1.addCondition("WAREHOUSE", new DataValue(req.getRequest().getWareHouse(), Types.VARCHAR));
						ub1.addCondition("LOCATION", new DataValue(par.getLocation(), Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub1));
					}
				}
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} else {
//			res.setSuccess(false);
//			res.setServiceStatus("200");
//			res.setServiceDescription("服务执行失败: 对应仓库信息：" + ware + "不存在 ");
			return;
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_LocationUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_LocationUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_LocationUpdateReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_LocationUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		// 必传值不为空
		String orgNo = req.getRequest().getOrgNo();
		String ware = req.getRequest().getWareHouse();

		if (Check.Null(orgNo)) {
			errMsg.append("组织编号不能为空值 ");
			isFail = true;
		}
		if (Check.Null(ware)) {
			errMsg.append("仓库编号不能为空值 ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_LocationUpdateReq> getRequestType() {
		return new TypeToken<DCP_LocationUpdateReq>() {
		};
	}

	@Override
	protected JsonRes getResponseType() {
		return new JsonRes();
	}

	/**
	 * 判断 信息时候已存在或重复
	 */
	private String isRepeat(String... key) {
		String sql = null;
		sql = " SELECT * FROM DCP_LOCATION WHERE EID='%s'   AND ORGANIZATIONNO='%s'  AND WAREHOUSE='%s'   ";
		sql = String.format(sql, key);
		return sql;
	}

}
