package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CRegisterQueryReq;
import com.dsc.spos.json.cust.res.DCP_CRegisterQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_CRegisterQuery extends SPosAdvanceService<DCP_CRegisterQueryReq, DCP_CRegisterQueryRes>
{

	@Override
	protected void processDUID(DCP_CRegisterQueryReq req, DCP_CRegisterQueryRes res) throws Exception {
	// TODO Auto-generated method stub
		//查询Platform_CregisterDetail表
		String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		
		//查询一下DCP_REGEDISTMODULAR表的RTYPEINFO
		String regsql="select * from DCP_REGEDISTMODULAR where RFUNCNO='"+req.getRequest().getProducttype()+"' ";
		String RTYPEINFO="";
		List<Map<String, Object>> reglist=this.doQueryData(regsql, null);
		if(reglist!=null&&!reglist.isEmpty())
		{
			RTYPEINFO=reglist.get(0).get("RTYPEINFO").toString();
		}
		
		String sql="";
		if(req.getRequest().getProducttype().equals("3")||RTYPEINFO.equals("3") )
		{
	    sql="select A.*,count(*) over(partition by TerminalLicence) ST1 ,sum(A.iscount)  over(partition by TerminalLicence) ST2  from "
	    	+ " (select A.*,B.Org_Name,C.machinename ,case when A.SHOPID is null then 0 else 1 end iscount from Platform_CregisterDetail A "
	    	+ " left join DCP_ORG_Lang B on A.EID=B.EID and A.SHOPID=B.OrganizationNo and B.lang_type='"+req.getLangType()+"' "
	    		+ " left join platform_machine C  on A.EID=C.EID and A.SHOPID=C.SHOPID and A.machine=C.machine   where A.Producttype='"+req.getRequest().getProducttype()+"' "
	    		+ " ) A ORDER BY A.EID,A.SHOPID ,A.machine  ";
		}
		else
		{
			sql="select A.*,count(*) over(partition by TerminalLicence) ST1 ,sum(A.iscount)  over(partition by TerminalLicence) ST2  from "
		    	+ " (select A.*,B.Org_Name,'' machinename,case when A.SHOPID is null then 0 else 1 end iscount from Platform_CregisterDetail A "
		    	+ " left join DCP_ORG_Lang B on A.EID=B.EID and A.SHOPID=B.OrganizationNo and B.lang_type='"+req.getLangType()+"'  where A.Producttype='"+req.getRequest().getProducttype()+"' "
		    	+ " ) A ORDER BY A.EID,A.SHOPID ";
		}
		List<Map<String, Object>> listallmachine=this.doQueryData(sql, null);
		//开始组织registerDetail
		if(listallmachine!=null&&!listallmachine.isEmpty())
		{
			//先通过注册号过滤重复
		  //单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("TERMINALLICENCE", true);
			//调用过滤函数
			int totcount=0;
			int istotcount=0;
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(listallmachine, condition);
			res.setRegisterDetail(new ArrayList<DCP_CRegisterQueryRes.level1Elm>());
			for (Map<String, Object> maphead : getQHeader) 
			{
				String bdate=maphead.get("BDATE").toString();
			  String edate=maphead.get("EDATE").toString();
			  if(Integer.parseInt(sdate)>=Integer.parseInt(bdate)&&Integer.parseInt(sdate)<=Integer.parseInt(edate))
			  {
			  	totcount=Integer.parseInt(maphead.get("ST1").toString());
			  	istotcount=Integer.parseInt(maphead.get("ST2").toString());
			  	
			  	DCP_CRegisterQueryRes.level1Elm lv1=res.new level1Elm();
			  	lv1.setTerminalLicence(maphead.get("TERMINALLICENCE").toString());
			  	lv1.setRegisterType(maphead.get("REGISTERTYPE").toString());
			  	lv1.setProducttype(maphead.get("PRODUCTTYPE").toString());
			  	lv1.setBdate(bdate);
			  	lv1.setEdate(edate);
			  	//lv1.setMemo(maphead.get("MEMO").toString());
			  	lv1.setScount(maphead.get("ST1").toString());
			  	lv1.setIsCount(maphead.get("ST2").toString());
			  	res.getRegisterDetail().add(lv1);
			  	lv1 = null;
			  }
		  }
			res.setRegisterMachine(new ArrayList<DCP_CRegisterQueryRes.level2Elm>());
			for (Map<String, Object> mapdetail : listallmachine) 
			{
				//这里还需要判断一下门店的编号或者名称
				if(req.getRequest().getKeyTxt()!=null&&!req.getRequest().getKeyTxt().isEmpty())
				{
					if(mapdetail.get("ORG_NAME").toString().contains(req.getRequest().getKeyTxt())||mapdetail.get("SHOPID").toString().contains(req.getRequest().getKeyTxt())||mapdetail.get("TERMINALLICENCE").toString().contains(req.getRequest().getKeyTxt()))
					{}
					else
					{
						continue;
					}
				}
				//这里返回的是已选择的门店和机台，所以这里必须EID和shop,machine 都为空
				if((mapdetail.get("EID")==null||mapdetail.get("EID").toString().isEmpty()) &&(mapdetail.get("SHOPID")==null||mapdetail.get("SHOPID").toString().isEmpty()) &&(mapdetail.get("MACHINE")==null||mapdetail.get("MACHINE").toString().isEmpty())  )
				{
					//如果都有为说明没有注册过，不能添加到已注册的里面来
					continue;
				}
				
//				if(req.getProducttype().equals("3")&&mapdetail.get("ISREGISTER").toString().equals("Y"))
//				{
//					continue;
//				}
				if((req.getRequest().getProducttype().equals("1")||
						   req.getRequest().getProducttype().equals("6")||
						   req.getRequest().getProducttype().equals("7")||
						   req.getRequest().getProducttype().equals("8")||
						   req.getRequest().getProducttype().equals("22")||
						   req.getRequest().getProducttype().equals("41")||
						   req.getRequest().getProducttype().equals("46")||
						   req.getRequest().getProducttype().equals("47")||
						   req.getRequest().getProducttype().equals("05003")
					)&&mapdetail.get("SHOPID").toString().isEmpty())
				{
					continue;
				}
				DCP_CRegisterQueryRes.level2Elm lv2=res.new level2Elm();
				lv2.setTerminalLicence(mapdetail.get("TERMINALLICENCE").toString());
				lv2.setRegisterType(mapdetail.get("REGISTERTYPE").toString());
				lv2.setBdate(mapdetail.get("BDATE").toString());
				lv2.setEdate(mapdetail.get("EDATE").toString());
				
				lv2.setProducttype(mapdetail.get("PRODUCTTYPE").toString());
				lv2.setMachineCode(mapdetail.get("MACHINECODE").toString());
				lv2.setrEId(mapdetail.get("EID").toString());
				lv2.setrShopId(mapdetail.get("SHOPID").toString());
				lv2.setRmachine(mapdetail.get("MACHINE").toString());
				lv2.setRshopname(mapdetail.get("ORG_NAME").toString());
				lv2.setMachineName(mapdetail.get("MACHINENAME").toString());
				
				res.getRegisterMachine().add(lv2);
				lv2 = null;
			}
			res.setsCount(totcount+"");
			res.setIsCount(istotcount+"");
			if(req.getRequest().getProducttype().equals("3")||RTYPEINFO.equals("3"))
			{
				//查找机台,只出来还没有注册的机台
				
				String sqlmachine="select A.*,B.org_name from PLATFORM_MACHINE A "
					+ "  left join DCP_ORG_Lang B on A.EID=B.EID and A.SHOPID=B.OrganizationNo and B.lang_type='"+req.getLangType()+"' "
					+ "  left join platform_cregisterdetail C on A.SHOPID=C.SHOPID and A.MACHINE=C.MACHINE   " 
					+ " where A.status='100' and A.EID='"+req.geteId()+"' and C.SHOPID is null "
					+ " ORDER BY A.EID,A.SHOPID,A.MACHINE ";
				List<Map<String, Object>> lismachine=this.doQueryData(sqlmachine, null);
				if(lismachine!=null&&!lismachine.isEmpty())
				{
					res.setAllShop(new ArrayList<DCP_CRegisterQueryRes.level3Elm>());
					for (Map<String, Object> mapmachine : lismachine) 
					{
						DCP_CRegisterQueryRes.level3Elm lv3=res.new level3Elm();
						lv3.setrEId(mapmachine.get("EID").toString());
						lv3.setRshop(mapmachine.get("SHOPID").toString());
						lv3.setRmachine(mapmachine.get("MACHINE").toString());
						lv3.setRshopname(mapmachine.get("ORG_NAME").toString());
						lv3.setRmachinename(mapmachine.get("MACHINENAME").toString());
						
						res.getAllShop().add(lv3);
						lv3 = null;
			    }
					
				}
			}
			if(req.getRequest().getProducttype().equals("1")||
			   req.getRequest().getProducttype().equals("6")||
			   req.getRequest().getProducttype().equals("7")||
			   req.getRequest().getProducttype().equals("8")||
			   req.getRequest().getProducttype().equals("22")||
			   req.getRequest().getProducttype().equals("41")||
			   req.getRequest().getProducttype().equals("46")||
			   req.getRequest().getProducttype().equals("47")||
			   req.getRequest().getProducttype().equals("05003")
			   )
			{
			//查找门店
				String sqlmachine="select A.*,B.org_name from DCP_ORG A "
					+ "  left join DCP_ORG_Lang B on A.EID=B.EID and A.OrganizationNo=B.OrganizationNo and B.lang_type='"+req.getLangType()+"'  where A.status='100' and A.Org_Form='2' and A.EID='"+req.geteId()+"' "
					+ "  ORDER BY A.EID,A.ORGANIZATIONNO ";
				List<Map<String, Object>> lismachine=this.doQueryData(sqlmachine, null);
				if(lismachine!=null&&!lismachine.isEmpty())
				{
					res.setAllShop(new ArrayList<DCP_CRegisterQueryRes.level3Elm>());
					for (Map<String, Object> mapmachine : lismachine) 
					{
						DCP_CRegisterQueryRes.level3Elm lv3=res.new level3Elm();
						lv3.setrEId(mapmachine.get("EID").toString());
						lv3.setRshop(mapmachine.get("ORGANIZATIONNO").toString());
						lv3.setRshopname(mapmachine.get("ORG_NAME").toString());
						res.getAllShop().add(lv3);
						lv3 = null;
			    }
					
				}
			}
			
			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CRegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CRegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CRegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CRegisterQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_CRegisterQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CRegisterQueryReq>(){};
	}

	@Override
	protected DCP_CRegisterQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CRegisterQueryRes();
	}

}
