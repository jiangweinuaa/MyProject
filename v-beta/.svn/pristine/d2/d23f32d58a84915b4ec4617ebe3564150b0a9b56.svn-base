package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HorseLampCreateReq;
import com.dsc.spos.json.cust.req.DCP_HorseLampCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_HorseLampCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 跑马灯新增
 * @author yuanyy
 *
 */
public class DCP_HorseLampCreate extends SPosAdvanceService<DCP_HorseLampCreateReq, DCP_HorseLampCreateRes> {

	@Override
	protected void processDUID(DCP_HorseLampCreateReq req, DCP_HorseLampCreateRes res) throws Exception {
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
			
			if(Check.Null(createTime)){
				Date dt = new Date();
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				createTime =  matter.format(dt);
			}
			
			if(Check.Null(billDate)){
				Date dt = new Date();
				SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
				billDate =  matter.format(dt);
			}
			
			String billNo = this.getMaxBillNo(req);
			
			List<level2Elm> shopList = req.getRequest().getShopList();
			res.setDatas(new ArrayList<DCP_HorseLampCreateRes.level1Elm>());
			DCP_HorseLampCreateRes.level1Elm lv1 = res.new level1Elm();
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
			
			String[] columns_hm ={"EID","BILLNO","BILLNAME","BILLTYPE","BILLDATE","COMPANYID","SHOPID","CHANNELID",
					"EMPLOYEEID","DEPARTID","BEGINDATE","ENDDATE","NOTICECONTENT","RESTRICTSHOP","STATUS","CREATEOPID",
					"CREATETIME"
			};
			DataValue[] insValue_hm = new DataValue[] 
					{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(billNo, Types.VARCHAR), 
							new DataValue(billName==null?"":billName, Types.VARCHAR),
							new DataValue("1", Types.VARCHAR),
							new DataValue(billDate ,Types.DATE),
							new DataValue(companyId, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR),
							new DataValue(channelId , Types.VARCHAR),
							new DataValue(employeeId, Types.VARCHAR),
							new DataValue(departId, Types.VARCHAR),
							new DataValue(beginDate, Types.DATE),
							new DataValue(endDate, Types.DATE),
							new DataValue(noticeContent, Types.VARCHAR),
							new DataValue(restrictShop, Types.VARCHAR),
							new DataValue(status, Types.VARCHAR),
							new DataValue(createopid, Types.VARCHAR),
							new DataValue(createTime, Types.DATE)
					};
			
			InsBean ib_hm = new InsBean("DCP_HORSELAMP", columns_hm);
			ib_hm.addValues(insValue_hm);
			this.addProcessData(new DataProcessBean(ib_hm)); 
			
			this.doExecuteDataToDB();
			lv1.setBillNo(billNo);
			res.getDatas().add(lv1);
			
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
	protected List<InsBean> prepareInsertData(DCP_HorseLampCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_HorseLampCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_HorseLampCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_HorseLampCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_HorseLampCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_HorseLampCreateReq>(){};
	}

	@Override
	protected DCP_HorseLampCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_HorseLampCreateRes();
	}
	
	private String getMaxBillNo(DCP_HorseLampCreateReq req) throws Exception{
		String billNo = "";
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
		billNo = "PMDS" + matter.format(dt);
		sqlbuf.append("" + "select billNO  from ( " + "select max(BillNo) as  billNO "
				+ "  from DCP_HorseLamp " + " where eId='"+req.geteId()+"'  " 
				+ " and BillNo like '%%" + billNo + "%%' "); 
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			
			billNo = (String) getQData.get(0).get("BILLNO");
			if (billNo != null && billNo.length() > 0) {
				long i;
				billNo = billNo.substring(4, billNo.length());
				i = Long.parseLong(billNo) + 1;
				billNo = i + "";
				billNo = "PMDS" + billNo;
			} else {
				billNo = "PMDS" + matter.format(dt) + "00001";
			}
		} else {
			billNo = "PMDS" + matter.format(dt) + "00001";
		}
		return billNo;
	}
	
}
