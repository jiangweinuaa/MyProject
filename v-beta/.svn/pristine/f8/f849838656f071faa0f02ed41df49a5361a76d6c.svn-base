package com.dsc.spos.service.imp.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_RegisterReq;
import com.dsc.spos.json.cust.res.DCP_RegisterRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.Register;
import com.google.gson.reflect.TypeToken;

public class DCP_Register extends SPosAdvanceService<DCP_RegisterReq, DCP_RegisterRes>
{
	@Override
	protected void processDUID(DCP_RegisterReq req, DCP_RegisterRes res) throws Exception {
	// TODO Auto-generated method stub
	  //判断类型，是客户段服务还是服务器的服务  0接收前台网页的注册服务  1接受来自公司注册服务器的注册 2 作为公司的注册服务器接受客户服务器的注册
	//这里默认写一条注册信息
		String sdFormat = new SimpleDateFormat("yyyyMMdd") .format(new Date());//当天日期
	  Calendar caltemp=Calendar.getInstance();
	  caltemp.add(Calendar.MONTH, 3);
	  String sedFormat = new SimpleDateFormat("yyyyMMdd") .format(caltemp.getTime());//当天日期
	  String stFormat = new SimpleDateFormat("HHmmss") .format(new Date());//当天时间
	  
	  ParseJson pj = new ParseJson();
	  
		if(req.getFRType().equals("0"))
		{
			//这里接收前台网页来的信息去注册
			//开始造req数据
			String scpu=Register.getCPUSerial();
		  String sHardDisk=Register.getHardDiskSN("c");
		  String scouputername=Register.getcomputername();
		  String sMotherboard=Register.getMotherboardSN();
		  String smac=Register.getMac();
			req.setCPUSerial(scpu);
			req.setHardDiskSN(sHardDisk);
			req.setMachineName(scouputername);
			req.setMotherboardSN(sMotherboard);
			req.setSMac(smac);
			req.setFRType("2");
		  //转换成JSON
			String jsontemp= pj.beanToJson(req);
			String sres= HttpSend.Sendcom(jsontemp, "http://180.167.0.42:28080/sposWeb/services/jaxrs/sposService/invoke");
			DCP_RegisterRes resserver=pj.jsonToBean(sres, new TypeToken<DCP_RegisterRes>(){});
			if(resserver.isSuccess()==false)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "注册失败："+resserver.getServiceDescription());
			}
			//本地的文件更新一下
	  	//作为门店注册服务方,读取本地的文件
			String path= System.getProperty("user.dir")+"\\Register\\"+"Register.txt";
			File file =new File(path);
			String dirpath= System.getProperty("user.dir")+"\\Register";
			
			File dirfile =new File(dirpath);
			if(!dirfile.exists())
			{
				dirfile.mkdir();
			}
			if(!file.exists())
			{
				file.createNewFile();
				//转换成JSON
				//写文件，需要加密一下
				EncryptUtils eu = new EncryptUtils();
				String AES_Key_Register = "DigiwinPosmpcfx5";
				sres=eu.encodeAES256(AES_Key_Register,sres);
				eu=null;
				
				BufferedWriter output=new BufferedWriter(new FileWriter(file,false));
				output.write(sres);
				output.close();
			}
			else 
			{
				Long filelength=file.length();
				byte[] filecontent=new byte[filelength.intValue()];
				FileInputStream in =new FileInputStream(file);
				in.read(filecontent);
				in.close();
				String outfile=new String(filecontent, "UTF-8");
				//JSON转OBJ,需要解密一下
				EncryptUtils eu = new EncryptUtils();
				String AES_Key_Register = "DigiwinPosmpcfx5";
				outfile=eu.decodeAES256(AES_Key_Register,outfile);
				
				DCP_RegisterRes reqtemp = pj.jsonToBean(outfile, new TypeToken<DCP_RegisterRes>(){});
				//读取到本地的信息文件后，如果是正式区注册，需要对原来的信息做改变，如果是测试区则直接加入进去
				reqtemp.setSNumber(resserver.getSNumber());
				
				if(resserver.getDatas().get(0).getRegisterType().equals("0"))
				{
					//reqtemp.setTOT_Count(resserver.getDatas().get(0).getSCount());
					reqtemp.setTOT_Count(resserver.getTOT_Count() );
					//还需要替换明细的正式区注册数量
					DCP_RegisterRes.level1 uptemp=null;
					for (DCP_RegisterRes.level1 detailtemp : reqtemp.getDatas()) 
					{
						if(detailtemp.getRegisterType().equals("0"))
						{
							uptemp=detailtemp;
						}
			    }
					if(uptemp==null)
					{
						uptemp=res.new level1();
						uptemp.setBDate(resserver.getDatas().get(0).getBDate());
						uptemp.setEDate(resserver.getDatas().get(0).getEDate());
						uptemp.setRegisterType("0");
						uptemp.setSCount(resserver.getDatas().get(0).getSCount());
						uptemp.setSDateTime(sdFormat+stFormat);
						uptemp.setTerminalLicence(resserver.getDatas().get(0).getTerminalLicence());
						reqtemp.getDatas().add(uptemp);
					}
					else
					{
						uptemp.setBDate(resserver.getDatas().get(0).getBDate());
						uptemp.setEDate(resserver.getDatas().get(0).getEDate());
						uptemp.setRegisterType("0");
						uptemp.setSCount(resserver.getDatas().get(0).getSCount());
						uptemp.setSDateTime(sdFormat+stFormat);
						uptemp.setTerminalLicence(resserver.getDatas().get(0).getTerminalLicence());
					}
				}
				else 
				{
					DCP_RegisterRes.level1 uptemp=null;
					for (DCP_RegisterRes.level1 detailtemp : reqtemp.getDatas()) 
					{
						if(detailtemp.getTerminalLicence().equals(resserver.getDatas().get(0).getTerminalLicence()))
						{
							uptemp=detailtemp;
						}
			    }
					if(uptemp==null)
					{
						reqtemp.getDatas().add(resserver.getDatas().get(0));
					}
		    }
			  //转换成JSON
				String jtemp= pj.beanToJson(reqtemp);
				//写文件
			  //写文件，需要加密一下
				jtemp=eu.encodeAES256(AES_Key_Register,jtemp);
				
				BufferedWriter output=new BufferedWriter(new FileWriter(file,false));
				output.write(jtemp);
				output.close();
				
				//通过reqtemp给res赋值
				res.setCPUSerial(reqtemp.getCPUSerial());
				//res.setDatas(reqtemp.set);
				res.setHardDiskSN(reqtemp.getHardDiskSN());
				res.setMachineName(reqtemp.getMachineName());
				res.setMotherboardSN(reqtemp.getMotherboardSN());
				res.setSMac(reqtemp.getSMac());
				res.setSNumber(reqtemp.getSNumber());
				res.setTOT_Count(reqtemp.getTOT_Count());
				//res.setDatas(new ArrayList<RegisterRes.level1>());
				res.setDatas(reqtemp.getDatas());
				
				eu=null;
		  }
			
		}
		else if(req.getFRType().equals("1"))
		{
			String scpu=Register.getCPUSerial();
		  String sHardDisk=Register.getHardDiskSN("c");
		  String scouputername=Register.getcomputername();
		  String sMotherboard=Register.getMotherboardSN();
		  String smac=Register.getMac();
		  
			//作为门店注册服务方,读取本地的文件
			String path= System.getProperty("user.dir")+"\\Register\\"+"Register.txt";
			File file =new File(path);
			String dirpath= System.getProperty("user.dir")+"\\Register";
			
			File dirfile =new File(dirpath);
			if(!dirfile.exists())
			{
				dirfile.mkdir();
			}
			if(!file.exists())
			{
				file.createNewFile();
				DCP_RegisterRes regs=new DCP_RegisterRes();
				regs.setSNumber(req.getSNumber());
				regs.setCPUSerial(scpu);
				regs.setHardDiskSN(sHardDisk);
				regs.setMachineName(scouputername);
				regs.setMotherboardSN(sMotherboard);
				regs.setSMac(smac);
				regs.setDatas(new ArrayList<DCP_RegisterRes.level1>() );
				DCP_RegisterRes.level1 levtemp=res.new level1();
				levtemp.setBDate(req.getBDate());
				levtemp.setEDate(req.getEDate());
				levtemp.setRegisterType(req.getRegisterType());
				levtemp.setSCount(req.getSCount());
				levtemp.setSDateTime(sdFormat+stFormat);
				//生成GUID
				levtemp.setTerminalLicence(req.getTerminalLicence());
				regs.getDatas().add(levtemp) ;
				//转换成JSON
				String jsontemp= pj.beanToJson(regs);
			  //写文件，需要加密一下
				EncryptUtils eu = new EncryptUtils();
				String AES_Key_Register = "DigiwinPosmpcfx5";
				jsontemp=eu.encodeAES256(AES_Key_Register,jsontemp);
				eu=null;
				
				BufferedWriter output=new BufferedWriter(new FileWriter(file,false));
				output.write(jsontemp);
				output.close();
				//回复信息
				res=regs;
			}
			else
			{
				if(req.getISFirst().equals("N"))
				{
					//如果不是第一次注册，需要判断一下硬件信息
					int i=0;
					if(scpu.equals(req.getCPUSerial()))
					{
						i++;
					}
					if(sHardDisk.equals(req.getHardDiskSN()))
					{
						i++;
					}
					if(scouputername.equals(req.getMachineName()))
					{
						i++;
					}
					if(sMotherboard.equals(req.getMotherboardSN()))
					{
						i++;
					}
					if(smac.equals(req.getSMac()))
					{
						i++;
					}
					if(i<=2)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "硬件信息不匹配！");
					}
				}
				Long filelength=file.length();
				byte[] filecontent=new byte[filelength.intValue()];
				FileInputStream in =new FileInputStream(file);
				in.read(filecontent);
				in.close();
				String outfile=new String(filecontent, "UTF-8");
			  //JSON转OBJ,先解密一下
				EncryptUtils eu = new EncryptUtils();
				String AES_Key_Register = "DigiwinPosmpcfx5";
				outfile=eu.decodeAES256(AES_Key_Register,outfile);

				DCP_RegisterRes reqtemp = pj.jsonToBean(outfile, new TypeToken<DCP_RegisterRes>(){});
				
				if(req.getISFirst().equals("N"))
				{
					if(!reqtemp.getSNumber().toString().equals(req.getSNumber()))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "序列号不匹配！");
					}
				}
				
				//读取到本地的信息文件后，如果是正式区注册，需要对原来的信息做改变，如果是测试区则直接加入进去
				reqtemp.setSNumber(req.getSNumber());
				if(req.getRegisterType().equals("0"))
				{
					reqtemp.setTOT_Count(req.getSCount());
					//还需要替换明细的正式区注册数量
					DCP_RegisterRes.level1 uptemp=null;
					for (DCP_RegisterRes.level1 detailtemp : reqtemp.getDatas()) 
					{
						if(detailtemp.getRegisterType().equals("0"))
						{
							uptemp=detailtemp;
						}
			    }
					if(uptemp==null)
					{
						uptemp=res.new level1();
						uptemp.setBDate(req.getBDate());
						uptemp.setEDate(req.getEDate());
						uptemp.setRegisterType("0");
						uptemp.setSCount(req.getSCount());
						uptemp.setSDateTime(sdFormat+stFormat);
						uptemp.setTerminalLicence(req.getTerminalLicence());
						reqtemp.getDatas().add(uptemp);
					}
					else
					{
						uptemp.setBDate(req.getBDate());
						uptemp.setEDate(req.getEDate());
						uptemp.setRegisterType("0");
						uptemp.setSCount(req.getSCount());
						uptemp.setSDateTime(sdFormat+stFormat);
						uptemp.setTerminalLicence(req.getTerminalLicence());
					}
				}
				else 
				{
					DCP_RegisterRes.level1 uptemp=null;
					for (DCP_RegisterRes.level1 detailtemp : reqtemp.getDatas()) 
					{
						if(detailtemp.getTerminalLicence().equals(req.getTerminalLicence()))
						{
							uptemp=detailtemp;
						}
			    }
					if(uptemp==null)
					{
						uptemp=res.new level1();
						uptemp.setBDate(req.getBDate());
						uptemp.setEDate(req.getEDate());
						uptemp.setRegisterType("1");
						uptemp.setSCount(req.getSCount());
						uptemp.setSDateTime(sdFormat+stFormat);
						uptemp.setTerminalLicence(req.getTerminalLicence());
						reqtemp.getDatas().add(uptemp);
					}
					else 
					{
						uptemp.setBDate(req.getBDate());
						uptemp.setEDate(req.getEDate());
						uptemp.setRegisterType("0");
						uptemp.setSCount(req.getSCount());
						uptemp.setSDateTime(sdFormat+stFormat);
						uptemp.setTerminalLicence(req.getTerminalLicence());
			    }
		    }
			  //转换成JSON
				String jsontemp= pj.beanToJson(reqtemp);
			  //写文件，需要加密一下
				String resjosn=jsontemp;
				jsontemp=eu.encodeAES256(AES_Key_Register,jsontemp);
				
				BufferedWriter output=new BufferedWriter(new FileWriter(file,false));
				output.write(jsontemp);
				output.close();
			 
			  //通过reqtemp给res赋值
				//res=new RegisterRes();
				res.setCPUSerial(reqtemp.getCPUSerial());
				//res.setDatas(reqtemp.set);
				res.setHardDiskSN(reqtemp.getHardDiskSN());
				res.setMachineName(reqtemp.getMachineName());
				res.setMotherboardSN(reqtemp.getMotherboardSN());
				res.setSMac(reqtemp.getSMac());
				res.setSNumber(reqtemp.getSNumber());
				res.setTOT_Count(reqtemp.getTOT_Count());
				//res.setDatas(new ArrayList<RegisterRes.level1>());
				res.setDatas(reqtemp.getDatas());
				
				eu=null;
			}
		}
		else if(req.getFRType().equals("2")) 
		{
			//作为公司的注册服务器，需要提供注册服务，需要接受前端的填写的序列号，完成注册
			//1、判断是否有该序列号存在
			String sql="select A.TOT_COUNT,B.* from PLATFORM_SREGISTER A left join PLATFORM_CREGISTER B on A.SNUMBER=B.SNUMBER"
				+ " where B.TERMINALLICENCE='"+req.getTerminalLicence()+"'  ";
			List<Map<String, Object>> getlicece = this.doQueryData(sql, null);
			if (getlicece != null && getlicece.isEmpty() == false) 
			{
				//能查到相关的信息
				if(getlicece.get(0).get("ISREGISTER").toString().equals("Y"))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该注册号已被注册！");
				}
				
				if(getlicece.get(0).get("ISFIRST").toString().equals("Y"))
				{	
				}
				else 
				{
//					if(!getlicece.get(0).get("SNUMBER").toString().equals(req.getSNumber()))
//					{
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "序列号不匹配！");
//					}
					
				  //如果不是第一次注册，需要判断一下硬件信息
					int i=0;
					if(getlicece.get(0).get("CPUSERIAL").toString().equals(req.getCPUSerial()))
					{
						i++;
					}
					if(getlicece.get(0).get("HARDDISKSN").toString().equals(req.getHardDiskSN()))
					{
						i++;
					}
					if(getlicece.get(0).get("MACHINENAME").toString().equals(req.getMachineName()))
					{
						i++;
					}
					if(getlicece.get(0).get("MOTHERBOARDSN").toString().equals(req.getMotherboardSN()))
					{
						i++;
					}
					if(getlicece.get(0).get("SMAC").toString().equals(req.getSMac()))
					{
						i++;
					}
					if(i<=2)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "硬件信息不匹配！");
					}
		    }
				//更新数据库查找的那条信息为已注册
				UptBean up1=new UptBean("PLATFORM_CREGISTER");
				up1.addUpdateValue("CPUSERIAL", new DataValue(req.getCPUSerial(), Types.VARCHAR));
				up1.addUpdateValue("HARDDISKSN", new DataValue(req.getHardDiskSN(), Types.VARCHAR));
				up1.addUpdateValue("MACHINENAME", new DataValue(req.getMachineName(), Types.VARCHAR));
				up1.addUpdateValue("MOTHERBOARDSN", new DataValue(req.getMotherboardSN(), Types.VARCHAR));
				up1.addUpdateValue("SMAC", new DataValue(req.getSMac(), Types.VARCHAR));
				up1.addUpdateValue("ISREGISTER", new DataValue("Y", Types.VARCHAR));
				up1.addUpdateValue("ISFIRST", new DataValue("N", Types.VARCHAR));
				up1.addCondition("TERMINALLICENCE", new DataValue(req.getTerminalLicence(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(up1));
				//设置返回值
				res.setSNumber(getlicece.get(0).get("MOTHERBOARDSN").toString());
				res.setCPUSerial(req.getCPUSerial());
				res.setHardDiskSN(req.getHardDiskSN());
				res.setMachineName(req.getMachineName());
				res.setMotherboardSN(req.getMotherboardSN());
				res.setSMac(req.getSMac());
				res.setTOT_Count(getlicece.get(0).get("TOT_COUNT").toString());
				res.setDatas(new ArrayList<DCP_RegisterRes.level1>() );
				DCP_RegisterRes.level1 levtemp=res.new level1();
				levtemp.setBDate(getlicece.get(0).get("BDATE").toString());
				levtemp.setEDate(getlicece.get(0).get("EDATE").toString());
				levtemp.setRegisterType(getlicece.get(0).get("REGISTERTYPE").toString());
				levtemp.setSCount(getlicece.get(0).get("SCOUNT").toString());
				levtemp.setSDateTime(sdFormat+stFormat);
				//生成GUID
				levtemp.setTerminalLicence(req.getTerminalLicence().toString());
				res.getDatas().add(levtemp) ;
				levtemp=null;
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "注册号码不存在，请确认填写是否正确！");
			}
			
	  }
	  else if(req.getFRType().equals("3"))
		{
			//服务器段的注册，向客户端注册
		//1、判断是否有该序列号存在
			String sql="select * from PLATFORM_SREGISTER A left join PLATFORM_CREGISTER B on A.SNUMBER=B.SNUMBER"
				+ " where B.TERMINALLICENCE='"+req.getTerminalLicence()+"'  ";
			List<Map<String, Object>> getlicece = this.doQueryData(sql, null);
			if (getlicece != null && getlicece.isEmpty() == false) 
			{
				//查找相关信息构建REQ向门店的客户端进行注册
				//能查到相关的信息
				if(getlicece.get(0).get("ISREGISTER").toString().equals("Y"))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该注册号已被注册！");
				}
			//开始造req数据
				req.setCPUSerial(getlicece.get(0).get("CPUSERIAL").toString());
				req.setHardDiskSN(getlicece.get(0).get("HARDDISKSN").toString());
				req.setMachineName(getlicece.get(0).get("MACHINENAME").toString());
				req.setMotherboardSN(getlicece.get(0).get("MOTHERBOARDSN").toString());
				req.setSMac(getlicece.get(0).get("SMAC").toString());
				req.setSNumber(getlicece.get(0).get("SNUMBER").toString());
				req.setBDate(getlicece.get(0).get("BDATE").toString());
				req.setEDate(getlicece.get(0).get("EDATE").toString());
				req.setISFirst(getlicece.get(0).get("ISFIRST").toString());
				req.setRegisterType(getlicece.get(0).get("REGISTERTYPE").toString());
				req.setSCount(getlicece.get(0).get("SCOUNT").toString());
				req.setFRType("1");
			  //转换成JSON
				String jsontemp= pj.beanToJson(req);
				String sres= HttpSend.Sendcom(jsontemp, getlicece.get(0).get("REGISTERURL").toString());
				res=pj.jsonToBean(sres, new TypeToken<DCP_RegisterRes>(){});
				if(res.isSuccess()==false)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "注册失败："+res.getServiceDescription());
				}
			  //更新数据库查找的那条信息为已注册
				UptBean up1=new UptBean("PLATFORM_CREGISTER");
				up1.addUpdateValue("CPUSERIAL", new DataValue(res.getCPUSerial(), Types.VARCHAR));
				up1.addUpdateValue("HARDDISKSN", new DataValue(res.getHardDiskSN(), Types.VARCHAR));
				up1.addUpdateValue("MACHINENAME", new DataValue(res.getMachineName(), Types.VARCHAR));
				up1.addUpdateValue("MOTHERBOARDSN", new DataValue(res.getMotherboardSN(), Types.VARCHAR));
				up1.addUpdateValue("SMAC", new DataValue(res.getSMac(), Types.VARCHAR));
				up1.addUpdateValue("ISREGISTER", new DataValue("Y", Types.VARCHAR));
				up1.addCondition("TERMINALLICENCE", new DataValue(req.getTerminalLicence(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(up1));
				
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "注册号码不存在，请确认填写是否正确！");
			}
			
	  }
		else 
		{
			String path= System.getProperty("user.dir")+"\\Register\\"+"Register.txt";
			File file =new File(path);
			
			String dirpath= System.getProperty("user.dir")+"\\Register";
			File dirfile =new File(dirpath);
			if(!dirfile.exists())
			{
				dirfile.mkdir();
			}
			if(file.exists())
			{
				Long filelength=file.length();
				byte[] filecontent=new byte[filelength.intValue()];
				FileInputStream in =new FileInputStream(file);
				in.read(filecontent);
				in.close();
				String outfile=new String(filecontent, "UTF-8");
				//JSON转OBJ,先解密一下
				EncryptUtils eu = new EncryptUtils();
				String AES_Key_Register = "DigiwinPosmpcfx5";
				outfile=eu.decodeAES256(AES_Key_Register,outfile);
				eu=null;
				
			  DCP_RegisterRes reqtemp = pj.jsonToBean(outfile, new TypeToken<DCP_RegisterRes>(){});
			  
			  //通过reqtemp给res赋值
				res.setCPUSerial(reqtemp.getCPUSerial());
				//res.setDatas(reqtemp.set);
				res.setHardDiskSN(reqtemp.getHardDiskSN());
				res.setMachineName(reqtemp.getMachineName());
				res.setMotherboardSN(reqtemp.getMotherboardSN());
				res.setSMac(reqtemp.getSMac());
				res.setSNumber(reqtemp.getSNumber());
				res.setTOT_Count(reqtemp.getTOT_Count());
				//res.setDatas(new ArrayList<RegisterRes.level1>());
				res.setDatas(reqtemp.getDatas());
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "注册文件不存在！");
			}
			
	  }
		pj=null;
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_RegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_RegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_RegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_RegisterReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_RegisterReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_RegisterReq>(){};
	}

	@Override
	protected DCP_RegisterRes getResponseType() 
	{
	// TODO Auto-generated method stub
	return new DCP_RegisterRes();
	}
	
	//服务器注册相关逻辑
	
}
