package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RegisterQueryReq;
import com.dsc.spos.json.cust.res.DCP_RegisterQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_RegisterQuery extends SPosAdvanceService<DCP_RegisterQueryReq, DCP_RegisterQueryRes>
{

	@Override
	protected void processDUID(DCP_RegisterQueryReq req, DCP_RegisterQueryRes res) throws Exception {
	// TODO Auto-generated method stub
		String snumber=req.getSNumber();
		String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		
		String sql="select * from ( select AA.*,row_number() OVER (PARTITION BY AA.SNUMBER ORDER BY AA.Tran_Time  DESC  )  as ST  from  "
				+ " (select A.Sname,B.* from platform_sregister A left join platform_cregister B on A.Snumber=B.Snumber "
				+ " where A.Snumber='"+snumber+"' ) AA ) order by Snumber,tran_time  ";
	  List<Map<String, Object>> lissnumber=this.doQueryData(sql, null);
	  if(lissnumber!=null&&!lissnumber.isEmpty())
	  {
	  //单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("SNUMBER", true);
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(lissnumber, condition);
			res.setDatas(new ArrayList<DCP_RegisterQueryRes.level1Elm>());
			for (Map<String, Object> headdetail : getQHeader)
			{
				int tot_count=0;
				int canusecount=0;
				DCP_RegisterQueryRes.level1Elm head=res.new level1Elm();
				for (Map<String, Object> itemdetail : lissnumber)
				{
					head.setDatas(new ArrayList<DCP_RegisterQueryRes.level2Elm>());
					if(headdetail.get("SNUMBER").toString().equals(itemdetail.get("SNUMBER").toString()))
					{
						DCP_RegisterQueryRes.level2Elm detail=res.new level2Elm();
						String bdate=itemdetail.get("BDATE").toString();
						String edate=itemdetail.get("EDATE").toString();
						String curcount=itemdetail.get("CURCOUNT").toString();
						Date formatdate=new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(itemdetail.get("TRAN_TIME").toString());
						
						int icount=Integer.parseInt(curcount);
						tot_count+=icount;
						if(Integer.parseInt(bdate)<=Integer.parseInt(sdate) && Integer.parseInt(edate)>=Integer.parseInt(sdate) )
						{
							canusecount+=icount;
						}
						detail.setRegisterType(itemdetail.get("REGISTERTYPE").toString());
						detail.setSCount(curcount);
						detail.setBDate(bdate);
						detail.setEDate(edate);
						detail.setSDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(formatdate));
						
						detail.setTerminalLicence(itemdetail.get("TERMINALLICENCE").toString());
						detail.setMachineName(itemdetail.get("MACHINENAME").toString());
						detail.setMotherboardSN(itemdetail.get("MOTHERBOARDSN").toString());
						detail.setHardDiskSN(itemdetail.get("HARDDISKSN").toString());
						detail.setCPUSerial(itemdetail.get("CPUSERIAL").toString());
						detail.setSMac(itemdetail.get("SMAC").toString());
						detail.setIsRegister(itemdetail.get("ISREGISTER").toString());
						detail.setIsFirst(itemdetail.get("ISFIRST").toString());
						head.getDatas().add(detail);
						detail=null;
						
					}
					else
					{
						continue;
					}
					
				}
				head.setSName(headdetail.get("SNAME").toString());
				head.setSNumber(headdetail.get("SNUMBER").toString());
				head.setCanUseCount(Integer.toString(canusecount));
				head.setTOT_Count(Integer.toString(tot_count));
				res.getDatas().add(head);
				head=null;
		  }
			
			
	  }
	  else
	  {
	  	res.setSuccess(false);
	  	res.setServiceDescription("查询内容为空！");
	  }
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_RegisterQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RegisterQueryReq>(){};
	}

	@Override
	protected DCP_RegisterQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_RegisterQueryRes();
	}

}
