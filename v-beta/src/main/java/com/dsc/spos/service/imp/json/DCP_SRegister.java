package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SRegisterReq;
import com.dsc.spos.json.cust.res.DCP_SRegisterRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.PLATFORM_SREGISTERDETAIL;
import com.dsc.spos.model.PLATFORM_SREGISTERHEAD;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.RSAUtil;
import com.google.gson.reflect.TypeToken;

public class DCP_SRegister extends SPosAdvanceService<DCP_SRegisterReq, DCP_SRegisterRes>
{

	@Override
	protected void processDUID(DCP_SRegisterReq req, DCP_SRegisterRes res) throws Exception {
	// TODO Auto-generated method stub
		//查询注册号,转换下注册，后面可以去掉
		ParseJson pjtemp = new ParseJson();
		String jsontemp= pjtemp.beanToJson(req);
		String sres= HttpSend.Sendcom(jsontemp, "http://101.37.33.19:38090/sposWeb/services/jaxrs/sposService/invoke");
		DCP_SRegisterRes restemp=pjtemp.jsonToBean(sres, new TypeToken<DCP_SRegisterRes>(){});
		if(true)
		{
			res.setSuccess(restemp.isSuccess());
			res.setServiceDescription(restemp.getServiceDescription());
			res.setCustomerNo(restemp.getCustomerNo());
			res.setCustomerName(restemp.getCustomerName());
			res.setDatas(restemp.getDatas());
			
		    return;
		}
		
		
		String sql="select A.CustomerNo,A.CustomerName,A.MEMO,B.TerminalLicence,B.IsRegister,B.SDateTime BSDATETIME,B.MEMO BMEMO,C.RegisterType,C.Producttype,C.Scount,C.Bdate,C.Edate,C.SDateTime CSDATETIME,C.MEMO CMEMO, "
				+ " D.SDetailType DSDETAILTYPE,D.SDetailmodular DSDETAILMODULAR,D.Scount DSCOUNT " 
				+ "  from Platform_RCustomer A left join Platform_SregisterHead B on A.CustomerNo=B.CustomerNo "
				+ " left join Platform_SregisterDetail C on B.CustomerNo=C.CustomerNo and B.TerminalLicence=C.TerminalLicence "
				+ " left join Platform_SDetailinfo D on C.CustomerNo=D.CustomerNo and C.TerminalLicence=D.TerminalLicence and C.PRODUCTTYPE=D.PRODUCTTYPE   "
				
				+ " where B.TerminalLicence='"+req.getTerminalLicence()+"' ";
			List<Map<String, Object>> listcustomer=this.doQueryData(sql, null);
			
			
			if(listcustomer!=null&&!listcustomer.isEmpty())
			{
				if(listcustomer.get(0).get("ISREGISTER").equals("Y"))
				{
					res.setSuccess(false);
					res.setServiceDescription("注册失败，该注册号已被注册使用！");
				}
				else
				{
					//处理数据库中的一些信息
					UptBean up=new UptBean("Platform_SregisterHead");
					up.addUpdateValue("IsRegister", new DataValue("Y", Types.VARCHAR));
					up.addUpdateValue("MachineName", new DataValue(req.getMachineName(), Types.VARCHAR));
					up.addUpdateValue("MotherBoardSn", new DataValue(req.getMotherBoardSn(), Types.VARCHAR));
					up.addUpdateValue("HardDiskSn", new DataValue(req.getHardDiskSn(), Types.VARCHAR));
					up.addUpdateValue("CpuSerial", new DataValue(req.getCpuSerial(), Types.VARCHAR));
					up.addUpdateValue("SMAC", new DataValue(req.getSmac(), Types.VARCHAR));
					up.addUpdateValue("Installpath", new DataValue(req.getInstallpath(), Types.VARCHAR));
					up.addUpdateValue("MachineCode", new DataValue(req.getMachincode(), Types.VARCHAR));
					
					up.addCondition("TerminalLicence", new DataValue(req.getTerminalLicence(), Types.VARCHAR));
					this.addProcessData(new DataProcessBean(up));
					this.doExecuteDataToDB();//插入到数据库中
					//再查询一次当前客户的所有注册号
					String sqlall="select A.CustomerNo,A.CustomerName,A.MEMO,B.TerminalLicence,B.IsRegister,B.SDateTime BSDATETIME,B.MEMO BMEMO,C.RegisterType,C.Producttype,C.Scount,C.Bdate,C.Edate,C.SDateTime CSDATETIME,C.MEMO CMEMO "
						+ ",B.MACHINECODE,B.ISREGISTER,B.REGISTERURL,B.ISFIRST,B.MACHINENAME,B.MOTHERBOARDSN,B.HARDDISKSN,B.CPUSERIAL,B.SMAC,B.INSTALLPATH,B.SDATETIME,  "
						+ " D.SDetailType DSDETAILTYPE,D.SDetailmodular DSDETAILMODULAR,D.Scount DSCOUNT "  
						
						+ "  from Platform_RCustomer A left join Platform_SregisterHead B on A.CustomerNo=B.CustomerNo "
							+ " left join Platform_SregisterDetail C on B.CustomerNo=C.CustomerNo and B.TerminalLicence=C.TerminalLicence "
							+ " left join Platform_SDetailinfo D on C.CustomerNo=D.CustomerNo and C.TerminalLicence=D.TerminalLicence and C.PRODUCTTYPE=D.PRODUCTTYPE   "
							
							+ " where A.CUSTOMERNO='"+listcustomer.get(0).get("CUSTOMERNO").toString()+"' and  B.TerminalLicence='"+req.getTerminalLicence()+"' ";
						List<Map<String, Object>> listcustomerall=this.doQueryData(sqlall, null);
					
					
					res.setCustomerNo(listcustomer.get(0).get("CUSTOMERNO").toString());
					res.setCustomerName(listcustomer.get(0).get("CUSTOMERNAME").toString());
					res.setDatas(new ArrayList<DCP_SRegisterRes.level1Elm>());
					//过滤注册号
				  //单头主键字段
					Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
					condition1.put("CUSTOMERNO", true);
					condition1.put("TERMINALLICENCE", true);
					//调用过滤函数
					List<Map<String, Object>> getcurstomhead=MapDistinct.getMap(listcustomerall, condition1);

					ParseJson pj = new ParseJson();
					
					EncryptUtils eu = new EncryptUtils();
					String AES_Key_Register = "DigiwinPosmpcfx5";
					
					for (Map<String, Object> maphead : getcurstomhead) 
					{
						DCP_SRegisterRes.level1Elm lv1= res.new level1Elm();
						lv1.setTerminalLicence(maphead.get("TERMINALLICENCE").toString());
						lv1.setMachinecode(maphead.get("MACHINECODE").toString());
						
						PLATFORM_SREGISTERHEAD headtemp=new PLATFORM_SREGISTERHEAD();
						headtemp.setCUSTOMERNO(listcustomer.get(0).get("CUSTOMERNO").toString());
						headtemp.setMACHINECODE(maphead.get("MACHINECODE").toString());
						headtemp.setTERMINALLICENCE(maphead.get("TERMINALLICENCE").toString());
						headtemp.setISREGISTER(maphead.get("ISREGISTER").toString());
						headtemp.setREGISTERURL(maphead.get("REGISTERURL").toString());
						headtemp.setISFIRST(maphead.get("ISFIRST").toString());
						headtemp.setMACHINENAME(maphead.get("MACHINENAME").toString());
						headtemp.setMOTHERBOARDSN(maphead.get("MOTHERBOARDSN").toString());
						headtemp.setHARDDISKSN(maphead.get("HARDDISKSN").toString());
						headtemp.setCPUSERIAL(maphead.get("CPUSERIAL").toString());
						headtemp.setSMAC(maphead.get("SMAC").toString());
						headtemp.setINSTALLPATH(maphead.get("INSTALLPATH").toString());
						headtemp.setSDATETIME(maphead.get("BSDATETIME").toString());
						headtemp.setMEMO(maphead.get("BMEMO").toString());
						String tlinfo=pj.beanToJson(headtemp);
						tlinfo=eu.encodeAES256(AES_Key_Register,tlinfo);
						lv1.setTlinfohead(tlinfo);
						
						lv1.setRegisterHead(new ArrayList<DCP_SRegisterRes.level2Elm>());
						
						for (Map<String, Object> mapdetail : listcustomerall) 
						{
							if(maphead.get("TERMINALLICENCE").toString().equals(mapdetail.get("TERMINALLICENCE").toString()))
							{
								DCP_SRegisterRes.level2Elm lv2 = res.new level2Elm();
								
								PLATFORM_SREGISTERDETAIL detailtemp=new PLATFORM_SREGISTERDETAIL();
								detailtemp.setCUSTOMERNO(listcustomer.get(0).get("CUSTOMERNO").toString());
								detailtemp.setTERMINALLICENCE(maphead.get("TERMINALLICENCE").toString());
								detailtemp.setREGISTERTYPE(mapdetail.get("REGISTERTYPE").toString());
								detailtemp.setPRODUCTTYPE(mapdetail.get("PRODUCTTYPE").toString());
								detailtemp.setSCOUNT(Integer.parseInt(mapdetail.get("SCOUNT").toString()) );
								detailtemp.setBDATE(mapdetail.get("BDATE").toString());
								detailtemp.setEDATE(mapdetail.get("EDATE").toString());
								detailtemp.setSDATETIME(mapdetail.get("CSDATETIME").toString());
								detailtemp.setMEMO(mapdetail.get("CMEMO").toString());
							    tlinfo=pj.beanToJson(detailtemp);
								tlinfo=eu.encodeAES256(AES_Key_Register,tlinfo);
								
								lv2.setProducttype(mapdetail.get("PRODUCTTYPE").toString());
								lv2.setTlinfo(tlinfo);
								lv1.getRegisterHead().add(lv2);
								
							}
			      }
						
						res.getDatas().add(lv1);
						
			    }
					
					pj=null;
					eu=null;
					
				}
			}
			else
			{
				res.setSuccess(false);
				res.setServiceDescription("注册号查询失败，请确认注册号是否正确！");
			}
				
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SRegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SRegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SRegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SRegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_SRegisterReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_SRegisterReq>(){};
	}

	@Override
	protected DCP_SRegisterRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_SRegisterRes();
	}

}
