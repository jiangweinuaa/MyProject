package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HorseLampUpdateReq;
import com.dsc.spos.json.cust.req.DCP_HorseLampUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_HorseLampUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 跑马灯xiugai 
 * @author yuanyy
 *
 */
public class DCP_HorseLampUpdate extends SPosAdvanceService<DCP_HorseLampUpdateReq, DCP_HorseLampUpdateRes> {

	@Override
	protected void processDUID(DCP_HorseLampUpdateReq req, DCP_HorseLampUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String billDate = req.getRequest().getBillDate();
			String companyId = req.getRequest().getCompanyId();
			String shopId = req.getRequest().getShopId();
			String channelId = req.getRequest().getChannelId();
			String employeeId = req.getRequest().getEmployeeId();
			String departId = req.getRequest().getDepartId();
			String billName = req.getRequest().getBillName();
			String beginDate = req.getRequest().getBeginDate();
			String endDate = req.getRequest().getEndDate();
			String noticeContent = req.getRequest().getNoticeContent();
			String restrictShop = req.getRequest().getRestrictShop()== null? "noLimit":req.getRequest().getRestrictShop();
			String status = req.getRequest().getStatus() == null? "-1":req.getRequest().getStatus();
			String createopid = req.getRequest().getCreateopid();
			String createTime = req.getRequest().getCreateTime();
			
			String lastmodiopid = req.getRequest().getLastmodiopid();
			String lastmoditime = req.getRequest().getLastmodiTime();
			
			if(Check.Null(lastmoditime)){
				Date dt = new Date();
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				lastmoditime =  matter.format(dt);
			}
			
			if(Check.Null(lastmodiopid)){
				lastmodiopid =  req.getOpNO();
			}
			
			String billNo = req.getRequest().getBillNo();
			
			List<level2Elm> shopList = req.getRequest().getShopList();
			res.setDatas(new ArrayList<DCP_HorseLampUpdateRes.level1Elm>());
			DCP_HorseLampUpdateRes.level1Elm lv1 = res.new level1Elm();
			
			DelBean db1 = new DelBean("DCP_HORSELAMP_PICKSHOP");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			this.doExecuteDataToDB();
			
			if(shopList != null && shopList.size() > 0){
				
				int item = 1 ;
				
				for (level2Elm lv2 : shopList) {
					String syShopId = lv2.getShopId();
					String syCompanyId = lv2.getCompanyId();
					
					if(syShopId.equals("")){
						continue;
					}
					
					String[] columns_hms ={"EID","BILLNO","SERIALNO","SHOPID","COMPANYID"};
					DataValue[] insValue_hms = new DataValue[] 
					{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(billNo, Types.VARCHAR), 
						new DataValue(item, Types.VARCHAR),
						new DataValue(syShopId==null?"":syShopId, Types.VARCHAR),
						new DataValue(syCompanyId==null?"":syCompanyId, Types.VARCHAR)
					};
					
					InsBean ib_hms = new InsBean("DCP_HORSELAMP_PICKSHOP", columns_hms);
					ib_hms.addValues(insValue_hms);
					this.addProcessData(new DataProcessBean(ib_hms));
					item++;
					
				}
			}
			
			UptBean ub1 = null;
			ub1 = new UptBean("DCP_HORSELAMP");
			ub1.addUpdateValue("BILLDATE", new DataValue(billDate, Types.DATE));
			ub1.addUpdateValue("COMPANYID", new DataValue(companyId, Types.VARCHAR));
			ub1.addUpdateValue("SHOPID", new DataValue(shopId, Types.VARCHAR));
			ub1.addUpdateValue("CHANNELID", new DataValue(channelId, Types.VARCHAR));
			ub1.addUpdateValue("EMPLOYEEID", new DataValue(employeeId, Types.VARCHAR));
			ub1.addUpdateValue("DEPARTID", new DataValue(departId, Types.VARCHAR));
			ub1.addUpdateValue("BILLNAME", new DataValue(billName, Types.VARCHAR));
			ub1.addUpdateValue("BEGINDATE", new DataValue(beginDate, Types.DATE));
			ub1.addUpdateValue("ENDDATE", new DataValue(endDate, Types.DATE));
			ub1.addUpdateValue("NOTICECONTENT", new DataValue(noticeContent, Types.VARCHAR));
			ub1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
			
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(lastmodiopid, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			
			// condition
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_HorseLampUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_HorseLampUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_HorseLampUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_HorseLampUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_HorseLampUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_HorseLampUpdateReq>(){};
	}

	@Override
	protected DCP_HorseLampUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_HorseLampUpdateRes();
	}
	
}
