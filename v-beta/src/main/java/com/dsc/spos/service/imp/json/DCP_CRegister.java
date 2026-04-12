package com.dsc.spos.service.imp.json;

import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.prefs.Preferences;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CRegisterReq;
import com.dsc.spos.json.cust.req.DCP_SRegisterReq;
import com.dsc.spos.json.cust.res.DCP_CRegisterRes;
import com.dsc.spos.json.cust.res.DCP_SRegisterRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.PLATFORM_SREGISTERDETAIL;
import com.dsc.spos.model.PLATFORM_SREGISTERHEAD;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.RSAUtil;
import com.dsc.spos.utils.Register;
import com.google.gson.reflect.TypeToken;

public class DCP_CRegister  extends SPosAdvanceService<DCP_CRegisterReq, DCP_CRegisterRes>
{
	@Override
	protected void processDUID(DCP_CRegisterReq req, DCP_CRegisterRes res) throws Exception {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		//取得机器码，以及硬件信息去注册 
		//			Preferences pre = Preferences.systemRoot().node("digiwin");
		//			Preferences  test = pre.node("digiwinsoft");
		//			test.put("digiwincode", "12345");
		//			test.flush();
		//			test.get("digiwincode", "");
		//加一个type type为1的时候为注册 为2的时候为查询
		String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		ParseJson pj = new ParseJson();

		if(req.getRequest().getStype().equals("1"))
		{
			Preferences pre = Preferences.systemRoot().node("digiwin");
			Preferences  test = pre.node("digiwinsoft");
			String machinecode= test.get("digiwincode", "");
			//取得硬件信息
			String scpu=Register.getCPUSerial();
			String sHardDisk=Register.getHardDiskSN("c");
			String scouputername=Register.getcomputername();
			String sMotherboard=Register.getMotherboardSN();
			String smac=Register.getMac();
			//取得安装目录
			File f = new File(this.getClass().getResource("/").getPath());
			String spath=f.toString();

			//这里再组一个注册的服务
			DCP_SRegisterReq sreq=new DCP_SRegisterReq();
			sreq.setServiceId("SRegisterDCP");
			sreq.setTerminalLicence(req.getRequest().getTerminalLicence());
			sreq.setMachineName(scouputername);
			sreq.setMotherBoardSn(sMotherboard);
			sreq.setHardDiskSn(sHardDisk);
			sreq.setCpuSerial(scpu);
			sreq.setSmac(smac);
			sreq.setInstallpath(spath);
			sreq.setMachincode(machinecode);
			//转换成JSON
			String jsontemp= pj.beanToJson(sreq);
			//String sres= HttpSend.Sendcom(jsontemp, "http://180.167.0.42:28080/sposWeb/services/jaxrs/sposService/invoke");
			String sres= HttpSend.Sendcom(jsontemp, "http://47.96.22.157:38090/sposWeb/services/jaxrs/sposService/invoke");
			if(sres.isEmpty())
			{                                                                
			  sres= HttpSend.Sendcom(jsontemp, "http://retaildev.digiwin.com.cn/regService/sposWeb/services/jaxrs/sposService/invoke");
			}
			if(sres.isEmpty() || sres.contains("502 Bad Gateway"))
			{
			  sres= HttpSend.Sendcom(jsontemp, "http://101.37.33.19:38090/sposWeb/services/jaxrs/sposService/invoke");
			}
			if(sres.isEmpty())
			{
			  sres= HttpSend.Sendcom(jsontemp, "http://eliutong2.digiwin.com.cn/regService/sposWeb/services/jaxrs/sposService/invoke");
			}
			if(sres.isEmpty())
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "未收到鼎捷注册服务器返回信息！！");
			}
			if(sres.contains("502 Bad Gateway"))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "未收到鼎捷注册服务器返回信息(502 Bad Gateway)！！");
			}
			DCP_SRegisterRes resserver=pj.jsonToBean(sres, new TypeToken<DCP_SRegisterRes>(){});
			if(resserver.isSuccess()==false)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "注册失败："+resserver.getServiceDescription());
			}
			//解析服务器返回的信息,存Platform_CregisterHead，相当于一个同步的功能
			//String sql="select * from Platform_CregisterHead where machinecode='"+machinecode+"' ";
			String sql="select * from Platform_CregisterHead order by SDATETIME desc ";
			List<Map<String, Object>> listtl=this.doQueryData(sql, null);

			for (DCP_SRegisterRes.level1Elm resserverdetail : resserver.getDatas()) 
			{
				boolean isexist=false;
				for (Map<String, Object> map : listtl) 
				{
					if(map.get("TERMINALLICENCE").toString().equals(resserverdetail.getTerminalLicence()))
					{
						isexist=true;
						break;
					}
				}
				if(isexist==false)
				{
					//这里插入数据库中所需要的注册信息，自动生成
					//String outfile=new String(resserverdetail.getTlinfo(), "UTF-8");
					EncryptUtils eu = new EncryptUtils();
					String AES_Key_Register = "DigiwinPosmpcfx5";
					String sbase64=resserverdetail.getTlinfohead();

					String restlinfo=eu.decodeAES256(AES_Key_Register,sbase64);
					
					PLATFORM_SREGISTERHEAD curreginfo=pj.jsonToBean(restlinfo, new TypeToken<PLATFORM_SREGISTERHEAD>(){});

					List<PLATFORM_SREGISTERDETAIL> currlist=new ArrayList<PLATFORM_SREGISTERDETAIL>();
					PLATFORM_SREGISTERDETAIL detailtemp=new PLATFORM_SREGISTERDETAIL();
					TypeToken<PLATFORM_SREGISTERDETAIL> ttokentemp=new TypeToken<PLATFORM_SREGISTERDETAIL>(){};

					//需要插入
					for (DCP_SRegisterRes.level2Elm map : resserverdetail.getRegisterHead()) {
						String[] colname={"CustomerNo","TerminalLicence","MachineCode","TLINFOHEAD","PRODUCTTYPE","TLINFO"};
						DataValue[] insValue={new DataValue(resserver.getCustomerNo(), Types.VARCHAR)
								,new DataValue(resserverdetail.getTerminalLicence(), Types.VARCHAR)
								,new DataValue(resserverdetail.getMachinecode(), Types.VARCHAR)
								,new DataValue(resserverdetail.getTlinfohead(), Types.VARCHAR)
								,new DataValue(map.getProducttype(), Types.VARCHAR)
								,new DataValue(map.getTlinfo(), Types.VARCHAR)
						};
						InsBean ins=new InsBean("Platform_CregisterHead", colname);
						ins.addValues(insValue);
						this.addProcessData(new DataProcessBean(ins));

						sbase64=map.getTlinfo();
						restlinfo=eu.decodeAES256(AES_Key_Register,sbase64);
						detailtemp=pj.jsonToBean(restlinfo, ttokentemp);
						currlist.add(detailtemp);

					}


					for (PLATFORM_SREGISTERDETAIL listdetail : currlist) 
					{
						String sqlmachin="select * from Platform_CregisterDetail where Producttype='"+listdetail.getPRODUCTTYPE()+"' and TerminalLicence='"+listdetail.getTERMINALLICENCE()+"'  ";
						List<Map<String, Object>> listmachine=this.doQueryData(sqlmachin, null);
						int countmachine=listmachine.size();
						int scount=listdetail.getSCOUNT();
						int diffcount=scount-countmachine;
						if(diffcount>0)
						{
							for (int i = 0; i < diffcount; i++) 
							{
								//添加注册信息进去
								String sgudi=UUID.randomUUID().toString();


								String TLINFO=sgudi+listdetail.getBDATE()+listdetail.getEDATE()+resserver.getCustomerNo();
								//用公钥加密
								TLINFO=RSAUtil.RSAEncrypt(TLINFO);

								String[] colname1={"EID","CustomerNo","TerminalLicence","MachineCode","RegisterType","BDATE","EDATE","Producttype","SDateTime","TLINFO","IsRegister"};
								DataValue[] insValue1={
										new DataValue(req.geteId(), Types.VARCHAR)
										,new DataValue(resserver.getCustomerNo(), Types.VARCHAR)
										,new DataValue(listdetail.getTERMINALLICENCE(), Types.VARCHAR)
										,new DataValue(sgudi, Types.VARCHAR)
										,new DataValue(listdetail.getREGISTERTYPE(), Types.VARCHAR)
										,new DataValue(listdetail.getBDATE(), Types.VARCHAR)
										,new DataValue(listdetail.getEDATE(), Types.VARCHAR)
										,new DataValue(listdetail.getPRODUCTTYPE(), Types.VARCHAR)
										,new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR)
										//这里需要用RSA加密，把日期验证也加入进去，改了日期相当于验证不通过，把客户编号也加入验证，防止拷贝别的客户编号过来
										,new DataValue(TLINFO, Types.VARCHAR)
										,new DataValue("N", Types.VARCHAR)};

								InsBean ins1=new InsBean("Platform_CregisterDetail", colname1);
								ins1.addValues(insValue1);
								this.pData.add(new DataProcessBean(ins1));
							}

						}

					}


					eu=null;
				}
				//返回信息
			}
			this.doExecuteDataToDB();
		}

		String sqlhead="select * from Platform_CregisterHead";
		List<Map<String, Object>> listtl=this.doQueryData(sqlhead, null);

		int dcpCount=0;//云中台数量
		int crmCount=0;//会员数量
		int posCount=0;//POS数量
		int smCount=0;//门店管理数量
		res.setRegisterHead(new ArrayList<DCP_CRegisterRes.level1Elm>());

		res.setCountdatas(new ArrayList<DCP_CRegisterRes.level4Elm>());
		if(listtl!=null&&!listtl.isEmpty())
		{
			String sqldetail="select A.*,B.Org_Name,C.Machinename from Platform_CregisterDetail A "
					+ " left join DCP_ORG_lang B on A.EID=B.EID and A.SHOPID=B.Organizationno and B.Lang_Type='"+req.getLangType()+"'  "
					+ " left join platform_machine C on A.EID=C.EID and A.SHOPID=C.SHOPID and A.machine=C.machine  "
					+ " where A.SHOPID is not null  ";
			List<Map<String, Object>> listdetail=this.doQueryData(sqldetail, null);


			//这里过滤一下单头通过 terminalLicence 过滤
			//单头主键字段，这里其实应该查自己的machinecode  listtlhead
			Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
			condition1.put("TERMINALLICENCE", true);
			//调用过滤函数
			List<Map<String, Object>> listtlhead=MapDistinct.getMap(listtl, condition1);

			for (Map<String, Object> maphade : listtlhead) 
			{
				DCP_CRegisterRes.level1Elm lv1=res.new level1Elm();
				//这里TLINFO要解密下
				//String TLINFO=RSAUtil.RSADecrypt2(maphade.get("TLINFO").toString());
				EncryptUtils eu = new EncryptUtils();
				String AES_Key_Register = "DigiwinPosmpcfx5";
				String sbase64=maphade.get("TLINFOHEAD").toString();
				//sbase64=eu.decodeBASE64String(sbase64);
				String TLINFO=eu.decodeAES256(AES_Key_Register,sbase64);

				PLATFORM_SREGISTERHEAD headtemp=pj.jsonToBean(TLINFO, new TypeToken<PLATFORM_SREGISTERHEAD>(){});

				lv1.setTerminalLicence(headtemp.getTERMINALLICENCE());
				lv1.setIsRegister(headtemp.getISREGISTER());
				lv1.setMemo(headtemp.getMEMO());

				//这里时间要格式化一下
				String sdatatimetemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(headtemp.getSDATETIME()));

				lv1.setsDateTime(sdatatimetemp);
				lv1.setRegisterDetail(new ArrayList<DCP_CRegisterRes.level2Elm>());

				String restlinfo="";
				PLATFORM_SREGISTERDETAIL detailtemp=new PLATFORM_SREGISTERDETAIL();

				TypeToken<PLATFORM_SREGISTERDETAIL> ttokentemp=new TypeToken<PLATFORM_SREGISTERDETAIL>(){};
				List<PLATFORM_SREGISTERDETAIL> currlist=new ArrayList<PLATFORM_SREGISTERDETAIL>();
				//这里添加一下currlist
				for (Map<String, Object> maphadeall : listtl)
				{
					if(maphadeall.get("TERMINALLICENCE").toString().equals(maphade.get("TERMINALLICENCE").toString()))
					{
						sbase64=maphadeall.get("TLINFO").toString();
						restlinfo=eu.decodeAES256(AES_Key_Register,sbase64);
						detailtemp=pj.jsonToBean(restlinfo, ttokentemp);
						currlist.add(detailtemp);
					}
				}


				for (PLATFORM_SREGISTERDETAIL mapdetail : currlist) 
				{
					DCP_CRegisterRes.level2Elm lv2=res.new level2Elm();
					lv2.setRegisterType(mapdetail.getREGISTERTYPE());
					lv2.setProducttype(mapdetail.getPRODUCTTYPE());
					lv2.setScount(mapdetail.getSCOUNT()+"");
					lv2.setEdate(mapdetail.getEDATE());
					lv2.setBdate(mapdetail.getBDATE());
					lv2.setMemo(mapdetail.getMEMO());
					lv1.getRegisterDetail().add(lv2);

					String bdate=mapdetail.getBDATE();
					String edate=mapdetail.getEDATE();
					if(Integer.parseInt(sdate)>=Integer.parseInt(bdate)&&Integer.parseInt(sdate)<=Integer.parseInt(edate))
					{

						//这里要循环一下，如果没有就加入进去，如果有就把数量加上去
						boolean isexist=false;
						for (DCP_CRegisterRes.level4Elm map : res.getCountdatas()) 
						{
							if(map.getProducttype().equals(mapdetail.getPRODUCTTYPE()))
							{
								isexist=true;
								String scount1=map.getScount();
								String scount2=mapdetail.getSCOUNT()+"";
								map.setScount(Integer.parseInt(scount1)+Integer.parseInt(scount2)+"");
							}
						}

						if(isexist==false)
						{
							DCP_CRegisterRes.level4Elm lv4=res.new level4Elm();
							lv4.setProducttype(mapdetail.getPRODUCTTYPE());
							lv4.setScount(mapdetail.getSCOUNT()+"");
							res.getCountdatas().add(lv4);
						}

					}

					lv2.setRegisterMachine(new ArrayList<DCP_CRegisterRes.level3Elm>());
					//这里开始组注册的机台信息
					int isreg=0;
					for (Map<String, Object> mapmachine : listdetail) 
					{
						if(mapmachine.get("TERMINALLICENCE").toString().equals(headtemp.getTERMINALLICENCE())&&mapmachine.get("PRODUCTTYPE").toString().equals(mapdetail.getPRODUCTTYPE()))
						{
							DCP_CRegisterRes.level3Elm lv3=res.new level3Elm();
							lv3.setProducttype(mapmachine.get("PRODUCTTYPE").toString());
							lv3.setMachineCode(mapmachine.get("MACHINECODE").toString());
							lv3.setrEId(mapmachine.get("EID").toString());
							lv3.setrShopId(mapmachine.get("SHOPID").toString());
							lv3.setRmachine(mapmachine.get("MACHINE").toString());
							lv3.setRshopName(mapmachine.get("ORG_NAME").toString());
							lv3.setRmachineName(mapmachine.get("MACHINENAME").toString());

							lv2.getRegisterMachine().add(lv3);

							if(Integer.parseInt(sdate)>=Integer.parseInt(bdate)&&Integer.parseInt(sdate)<=Integer.parseInt(edate))
							{	
								isreg++;
							}
						}
					}
					lv2.setIscount(isreg+"");
					//这里把已注册数量加上去
					//这里要循环一下，如果没有就加入进去，如果有就把数量加上去
					boolean isexist=false;
					for (DCP_CRegisterRes.level4Elm map : res.getCountdatas()) 
					{
						if(map.getProducttype().equals(mapdetail.getPRODUCTTYPE()))
						{
							isexist=true;
							String scount1=map.getIscount();
							if(scount1==null||scount1.isEmpty())
							{
								scount1="0";
							}
							String scount2=isreg+"";
							map.setIscount(Integer.parseInt(scount1)+Integer.parseInt(scount2)+"");
						}
					}



				}
				res.getRegisterHead().add(lv1);
				
				eu=null;
			}
		}

		pj=null;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CRegisterReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CRegisterReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CRegisterReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CRegisterReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_CRegisterReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CRegisterReq>(){};
	}

	@Override
	protected DCP_CRegisterRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CRegisterRes();
	}}
