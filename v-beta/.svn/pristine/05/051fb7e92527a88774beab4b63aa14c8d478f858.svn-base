package com.dsc.spos.scheduler.eventlistener;

import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.json.cust.res.DCP_RegisterRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisUtil;
import com.dsc.spos.scheduler.job.DCP_GoodsShelfStatusJob;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.scheduler.util.QuartzUtil;
import com.dsc.spos.utils.*;
import com.dsc.spos.xml.utils.ParseXml;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.Preferences;


public class QuartzServiceListener implements ServletContextListener 
{
	Logger log = LogManager.getLogger(QuartzServiceListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try 
		{
			//scheduler.shutdown();
			QuartzUtil.shutdownScheduler();
			log.info("停止调度任务");
		} 
		catch (SchedulerException e) 
		{
			log.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void contextInitialized(ServletContextEvent event) 
	{
		try 
		{
			URL xmlPath = this.getClass().getResource("/config/PlugInService.xml"); //xml 取得設定檔
			URL propPath = this.getClass().getResource("/config/jdbc.properties"); //取得 jdbc 設定檔
			ParseXml pXml=new ParseXml();
			StaticInfo.psc = pXml.xmlToBean(xmlPath,propPath, SPosConfig.class); //取得混合過後的設定
			pXml=null;
			propPath=null;
			xmlPath=null;
			
			//数据库判断依据
			StaticInfo.dbType=0;
			if (StaticInfo.psc.getDataBaseConfig().getDriverClassName().getValue().toLowerCase().contains("postgresql")) 
			{
				StaticInfo.dbType=1;
			}

			StaticInfo.Using_GAODEMAP_key = StaticInfo.psc.getUsing_GAODEMAP_key().getValue();//天气预报key

            if(StaticInfo.psc.getKeepLogsDay()!=null){
                StaticInfo.KeepLogsDay = StaticInfo.psc.getKeepLogsDay().getValue();
            }
            else
            {
                StaticInfo.KeepLogsDay = "45";
            }

            //分销系统OMS接口地址
            if(StaticInfo.psc.getOMS_Url()!=null)
            {
                StaticInfo.OMS_Url = StaticInfo.psc.getOMS_Url().getValue();
            }
            else
            {
                StaticInfo.OMS_Url = "";
            }
            //分销系统OMS企业编号
            if(StaticInfo.psc.getOMS_Eid()!=null)
            {
                StaticInfo.OMS_Eid = StaticInfo.psc.getOMS_Eid().getValue();
            }
            else
            {
                StaticInfo.OMS_Eid = "";
            }
            //ERP企业编号
            if(StaticInfo.psc.getOMS_ERP_Eid()!=null)
            {
                StaticInfo.OMS_ERP_Eid = StaticInfo.psc.getOMS_ERP_Eid().getValue();
            }
            else
            {
                StaticInfo.OMS_ERP_Eid = "";
            }

            //锐翔系统接口地址
            if(StaticInfo.psc.getRuiXiang_Url()!=null)
            {
                StaticInfo.RuiXiang_Url = StaticInfo.psc.getRuiXiang_Url().getValue();
            }
            else
            {
                StaticInfo.RuiXiang_Url = "";
            }
            //锐翔系统签名秘钥
            if(StaticInfo.psc.getRuiXiang_Secret()!=null)
            {
                StaticInfo.RuiXiang_Secret = StaticInfo.psc.getRuiXiang_Secret().getValue();
            }
            else
            {
                StaticInfo.RuiXiang_Secret = "";
            }

            //锐翔系统Appid
            if(StaticInfo.psc.getRuiXiang_Appid()!=null)
            {
                StaticInfo.RuiXiang_Appid = StaticInfo.psc.getRuiXiang_Appid().getValue();
            }
            else
            {
                StaticInfo.RuiXiang_Appid = "";
            }
            //锐翔系统企业编号
            if(StaticInfo.psc.getRuiXiang_Eid()!=null)
            {
                StaticInfo.RuiXiang_Eid = StaticInfo.psc.getRuiXiang_Eid().getValue();
            }
            else
            {
                StaticInfo.RuiXiang_Eid = "";
            }

			try 
			{
				StaticInfo.waimaiMTAPPID = StaticInfo.psc.getNewretailTransferm().getWaimaiMTAPPID().getValue();	    				
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiMTAPPID ="";
			}
			try 
			{		    
				StaticInfo.waimaiMTSignKey = StaticInfo.psc.getNewretailTransferm().getWaimaiMTSignKey().getValue();		    			
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiMTSignKey ="";
			}
			try 
			{		    	
				StaticInfo.waimaiMTIsJBP = StaticInfo.psc.getNewretailTransferm().getWaimaiMTIsJBP().getValue();		    		
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiMTIsJBP ="";
			}
			try 
			{		    	
				StaticInfo.waimaiELMAPPKey = StaticInfo.psc.getNewretailTransferm().getWaimaiELMAPPKey().getValue();		    		
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiELMAPPKey ="";
			}		   
			try 
			{		    	
				StaticInfo.waimaiELMSecret = StaticInfo.psc.getNewretailTransferm().getWaimaiELMSecret().getValue();			
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiELMSecret ="";
			}

			try 
			{		    	
				StaticInfo.waimaiELMSandbox = StaticInfo.psc.getNewretailTransferm().getWaimaiELMSandbox().getValue();			
				if(StaticInfo.waimaiELMSandbox.toUpperCase().equals("Y"))
				{
					StaticInfo.waimaiELMIsSandbox = true;
				}
				else
				{
					StaticInfo.waimaiELMIsSandbox = false;
				}
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiELMSandbox ="";
				StaticInfo.waimaiELMIsSandbox = true;
			}

			//2018-08-29新增微商城信息
			try 
			{
				StaticInfo.microMarkSign = StaticInfo.psc.getMicroMarkTransferm().getMicroMarkSign().getValue();    				
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.microMarkSign ="";
			}

			try 
			{
				StaticInfo.microMarkKey = StaticInfo.psc.getMicroMarkTransferm().getMicroMarkKey().getValue();    				
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.microMarkKey ="";
			}

			try 
			{
				StaticInfo.microMarkHttpPost = StaticInfo.psc.getMicroMarkTransferm().getMicroMarkHttpPost().getValue();    				
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.microMarkHttpPost ="";
			}

			//2019-02-01新增京东到家配置信息
			try 
			{		    	
				StaticInfo.waimaiJDDJAPPKey = StaticInfo.psc.getNewretailTransferm().getWaimaiJDDJAPPKey().getValue();			
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiJDDJAPPKey ="";
			}

			try 
			{		    	
				StaticInfo.waimaiJDDJSecret = StaticInfo.psc.getNewretailTransferm().getWaimaiJDDJSecret().getValue();			
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiJDDJSecret ="";
			}

			try 
			{		    	
				StaticInfo.waimaiJDDJToken = StaticInfo.psc.getNewretailTransferm().getWaimaiJDDJToken().getValue();		
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiJDDJToken ="";
			}

			try 
			{		    	
				StaticInfo.waimaiJDDJSandbox = StaticInfo.psc.getNewretailTransferm().getWaimaiJDDJSandbox().getValue();		
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				StaticInfo.waimaiJDDJSandbox ="N";
			}

			try
			{
				List<Map<String, Object>> data;
				
				//不同数据库SQL处理
				if (StaticInfo.dbType==0) 
				{
					data = this.doQueryData("select a.eid, a.organizationno,b.org_name from DCP_ORG a "
							+ "inner join DCP_ORG_lang b on a.eid=b.eid and a.organizationno=b.organizationno  "
							+ "where a.org_form=0 and a.is_corp='Y' and ROWNUM<=5 ",null);
				}
				else 
				{
					data = this.doQueryData("select a.eid, a.organizationno,b.org_name from DCP_ORG a "
							+ "inner join DCP_ORG_lang b on a.eid=b.eid and a.organizationno=b.organizationno  "
							+ "where a.org_form='0' and a.is_corp='Y' limit 5 ",null);
		
				}

				StringBuffer tempOrg=new StringBuffer("");
				for (int i = 0; i < data.size(); i++) 
				{					
					String eId = data.get(i).get("EID").toString();
					String ORGANIZATIONNO = data.get(i).get("ORGANIZATIONNO").toString();
					String ORG_NAME = data.get(i).get("ORG_NAME").toString();
					//
					tempOrg.append("eId(企业编码)="+eId+",organizationno(组织编码)="+ORGANIZATIONNO+",org_name(组织名称)="+ORG_NAME+"<br>");
				}				
				StaticInfo.sOrgTopName=tempOrg.toString();


				//表分区标记处理
                //不同数据库SQL处理,暂时只处理oracle，其他不分区
                if (StaticInfo.dbType==0)
                {
                    List<Map<String, Object>> dataPartition=this.doQueryData("select distinct table_name from user_tab_partitions " +
                                             "where table_name='DCP_ORDER' " +//订单表
                                             "UNION ALL " +
                                             "select distinct table_name from user_tab_partitions " +
                                             "where table_name='DCP_SALE' ",null);//销售单表
                    if (dataPartition != null && dataPartition.size()>0)
                    {
                        for (Map<String, Object> map_partition : dataPartition)
                        {
                            if (map_partition.get("TABLE_NAME").toString().equals("DCP_ORDER"))
                            {
                                PosPub.tablePartition_dcp_order="1";
                            }
                            else if (map_partition.get("TABLE_NAME").toString().equals("DCP_SALE"))
                            {
                                PosPub.tablePartition_dcp_sale="1";
                            }
                        }
                    }
                    else
                    {
                        PosPub.tablePartition_dcp_order="0";
                        PosPub.tablePartition_dcp_sale="0";
                    }
                }

            }
			catch(Exception e)
			{
				log.error(e.getMessage(), e);
			}

			//启动饿了么监听
			if(StaticInfo.waimaiELMAPPKey.equals("")==false && StaticInfo.waimaiELMSecret.equals("")==false)
			{
				
			}
			//中台地址
            if(StaticInfo.psc.getDCP_URL()!=null)
            {
            	StaticInfo.DCP_URL = StaticInfo.psc.getDCP_URL().getValue();
            }else
            {
            	StaticInfo.DCP_URL="";
            }
			//会员地址
            if(StaticInfo.psc.getCRM_URL()!=null)
            {
            	StaticInfo.CRM_URL = StaticInfo.psc.getCRM_URL().getValue();
            }else
            {
            	StaticInfo.CRM_URL="";
            }
			//PAY地址
            if(StaticInfo.psc.getPAY_URL()!=null)
            {
            	StaticInfo.PAY_URL = StaticInfo.psc.getPAY_URL().getValue();
            }else
            {
            	StaticInfo.PAY_URL="";
            }
            //MES地址
            if(StaticInfo.psc.getMES_URL()!=null)
            {
                StaticInfo.MES_URL = StaticInfo.psc.getMES_URL().getValue();
            }else
            {
                StaticInfo.MES_URL="";
            }
            //POS地址
            if(StaticInfo.psc.getPOS_URL()!=null)
            {
            	StaticInfo.POS_URL = StaticInfo.psc.getPOS_URL().getValue();
            }else
            {
            	StaticInfo.POS_URL="";
            }
            if(StaticInfo.psc.getPROM_URL()!=null)
            {
            	StaticInfo.PROM_URL = StaticInfo.psc.getPROM_URL().getValue();
            }else
            {
            	StaticInfo.PROM_URL="";
            }
            if(StaticInfo.psc.getPICTURE_URL()!=null)
            {
            	StaticInfo.PICTURE_URL = StaticInfo.psc.getPICTURE_URL().getValue();
            }else
            {
            	StaticInfo.PICTURE_URL="";
            }
			//中台地址
            if(StaticInfo.psc.getDCP_INNER_URL()!=null)
            {
            	StaticInfo.DCP_INNER_URL = StaticInfo.psc.getDCP_INNER_URL().getValue();
            }else
            {
            	StaticInfo.DCP_INNER_URL="";
            }
			//会员地址
            if(StaticInfo.psc.getCRM_INNER_URL()!=null)
            {
            	StaticInfo.CRM_INNER_URL = StaticInfo.psc.getCRM_INNER_URL().getValue();
            }else
            {
            	StaticInfo.CRM_INNER_URL="";
            }
			//PAY地址
            if(StaticInfo.psc.getPAY_INNER_URL()!=null)
            {
            	StaticInfo.PAY_INNER_URL = StaticInfo.psc.getPAY_INNER_URL().getValue();
            }else
            {
            	StaticInfo.PAY_INNER_URL="";
            }
            //MES地址
            if(StaticInfo.psc.getMES_INNER_URL()!=null)
            {
                StaticInfo.MES_INNER_URL = StaticInfo.psc.getMES_INNER_URL().getValue();
            }else
            {
                StaticInfo.MES_INNER_URL="";
            }
            //POS地址
            if(StaticInfo.psc.getPOS_INNER_URL()!=null)
            {
            	StaticInfo.POS_INNER_URL = StaticInfo.psc.getPOS_INNER_URL().getValue();
            }else
            {
            	StaticInfo.POS_INNER_URL="";
            }
            if(StaticInfo.psc.getPROM_INNER_URL()!=null)
            {
            	StaticInfo.PROM_INNER_URL = StaticInfo.psc.getPROM_INNER_URL().getValue();
            }else
            {
            	StaticInfo.PROM_INNER_URL="";
            }
            if(StaticInfo.psc.getPICTURE_INNER_URL()!=null)
            {
            	StaticInfo.PICTURE_INNER_URL = StaticInfo.psc.getPICTURE_INNER_URL().getValue();
            }else
            {
            	StaticInfo.PICTURE_INNER_URL="";
            }
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}


		//*****************************************************
		//*******************Redis初始化***********************
		//*****************************************************
		try 
		{		
			RedisUtil.initialPool();		

			boolean bTestOk=RedisUtil.connectRedisTest();	
			if (bTestOk) 
			{
				StaticInfo.Using_Redis="1";
			}
			else 
			{
				StaticInfo.Using_Redis="0";
			}			
		} 
		catch (Exception e) 
		{
			log.error("\r\n********Redis无法连接！********\r\n");
		}			
		
		


		try 
		{
			//加入注册信息的判断
			//生成文件路径
			try
			{
				String sdFormat = new SimpleDateFormat("yyyyMMdd") .format(new Date());//当天日期
				Calendar caltemp=Calendar.getInstance();
				caltemp.add(Calendar.MONTH, 3);
				String sedFormat = new SimpleDateFormat("yyyyMMdd") .format(caltemp.getTime());//当天日期
				String stFormat = new SimpleDateFormat("HHmmss") .format(new Date());//当天时间
				String path= System.getProperty("user.dir")+"\\Register\\"+"Register.txt";
				File file =new File(path);

				String dirpath= System.getProperty("user.dir")+"\\Register";
				File dirfile =new File(dirpath);
				if(!dirfile.exists())
				{
					dirfile.mkdir();
				}
				ParseJson pj = new ParseJson();
				if(!file.exists())
				{
					file.createNewFile();
					//这里默认写一条注册信息
					DCP_RegisterRes regs=new DCP_RegisterRes();
					regs.setSNumber("000");
					regs.setCPUSerial(Register.getCPUSerial());
					regs.setHardDiskSN(Register.getHardDiskSN("c"));
					regs.setMachineName(Register.getcomputername());
					regs.setMotherboardSN(Register.getMotherboardSN());
					regs.setSMac(Register.getMac());
					regs.setTOT_Count("0");
					regs.setDatas(new ArrayList<DCP_RegisterRes.level1>() );
					DCP_RegisterRes.level1 levtemp=new DCP_RegisterRes(). new level1();
					levtemp.setBDate(sdFormat);
					levtemp.setEDate(sedFormat);
					levtemp.setRegisterType("1");
					levtemp.setSCount("10");
					levtemp.setSDateTime(sdFormat+stFormat);
					//生成GUID
					levtemp.setTerminalLicence(UUID.randomUUID().toString());
					regs.getDatas().add(levtemp) ;
					levtemp=null;

					//转换成JSON
					String jsontemp= pj.beanToJson(regs);
					//写文件
					//这里先加密一下再存
					EncryptUtils eu = new EncryptUtils();
					String AES_Key_Register = "DigiwinPosmpcfx5";
					jsontemp=eu.encodeAES256(AES_Key_Register,jsontemp);
					eu=null;
					
					BufferedWriter output=new BufferedWriter(new FileWriter(file,true));
					output.write(jsontemp);
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
					//JSON转OBJ,先解密一下
					EncryptUtils eu = new EncryptUtils();
					String AES_Key_Register = "DigiwinPosmpcfx5";
					outfile=eu.decodeAES256(AES_Key_Register,outfile);
					eu=null;
					
					DCP_RegisterRes req = pj.jsonToBean(outfile, new TypeToken<DCP_RegisterRes>(){});
					String aa="0";
				}

				pj=null;

				//新注册机制	
				//取得机器码，以及硬件信息去注册
				String sguid=UUID.randomUUID().toString();
				Preferences pre = Preferences.systemRoot().node("digiwin");
				Preferences  test = pre.node("digiwinsoft");
				String machinecode= test.get("digiwincode", "");
				if(machinecode==null||machinecode.isEmpty())
				{
					test.put("digiwincode", sguid);
					test.flush();
				}
			}
			catch(Exception ex)
			{
				log.error("\r\n********启动时注册文件报错！\r\n"+ex.toString());
			}

			QuartzUtil.initSchedule();    		    
			// 取得 SQL
			String sql = null;
			sql = this.getQuerySql();
			String[] conditionValues = null;
			try
			{
				URL xmlPath = this.getClass().getResource("/config/PlugInService.xml"); //xml 取得設定檔
				URL propPath = this.getClass().getResource("/config/jdbc.properties"); //取得 jdbc 設定檔
				ParseXml pXml=new ParseXml();
				StaticInfo.psc = pXml.xmlToBean(xmlPath,propPath, SPosConfig.class); //取得混合過後的設定
				pXml=null;
				propPath=null;
				xmlPath=null;
				try{
					StaticInfo.DCP_Slave  =StaticInfo.psc.getDCP_Slave().getValue();	
				}catch(Exception e)
				{
				}
				if(StaticInfo.DCP_Slave.equals("0"))
				{
					List<Map<String, Object>> data = this.doQueryData(sql, conditionValues);
					for (int i = 0; i < data.size(); i++) {
						// 单个任务挂掉不能影响其他的
						try {
							// String triggerName,String groupName,
							// String jobClass,String jobName, String cronExp
							String jobName = (String) data.get(i).get("JOB_NAME");
							String jobClass = (String) data.get(i).get("JOB_CLASS");
							String groupName = (String) data.get(i).get("JOB_GROUPNAME");
							String triggerName = (String) data.get(i).get("JOB_TRIGGERNAME");
							BigDecimal time = (BigDecimal) data.get(i).get("JOB_TIME");
							String ETL_KETTLE = (String) data.get(i).get("ETL_KETTLE");
							String JOB_FOLDER = (String) data.get(i).get("JOB_FOLDER");

							// 0:在适当时机立即执行(基本等价于1) 1:立即执行一次 2:不会先执行一次
							BigDecimal jobType = (BigDecimal) data.get(i).get("JOB_TYPE");

							String cronExp = "";

							if (time.intValue() / 1000 >= 60) {
								// 分鐘
								// "0 0/2 * * * ?"
								cronExp = "0 0/" + time.intValue() / 1000 / 60 + " * * * ?";
							} else {
								// 秒
								// "0/3 * * * * ?"
								cronExp = "0/" + time.intValue() / 1000 + " * * * * ?";
							}
							if(jobName.equals("EveryDayCallSP"))
							{
								cronExp = "0 0 4 * * ?";
							}
							if(jobName.equals("DCP_ClearHisData"))
							{
								cronExp = "0 0 2 * * ?";
							}
							if (ETL_KETTLE.equals("Y")) {
								JobDataMap jdm = new JobDataMap();
								jdm.put("JOBNAME", jobName);
								jdm.put("JOB_FOLDER", JOB_FOLDER);

								QuartzUtil.addSchedule(jobName, jobName, jobClass, "ETLJOB", cronExp, jobType.intValue(),
									jdm);
							} else {
								QuartzUtil.addSchedule(triggerName, groupName, jobClass, jobName, cronExp,
									jobType.intValue(), null);
							}

						} catch (Exception e) 
						{
							log.error(e.getMessage(), e);
						}
					}
				}

                // 如果是从 那么只执行清理日志的 RegularClearlogs 此job
                if(StaticInfo.DCP_Slave.equals("1")){
                    List<Map<String, Object>> data = this.doQueryData(sql, conditionValues);
                    for (int i = 0; i < data.size(); i++) {
                        // 单个任务挂掉不能影响其他的
                        try {
                            // String triggerName,String groupName,
                            // String jobClass,String jobName, String cronExp
                            String jobName = (String) data.get(i).get("JOB_NAME");
                            if(jobName.equals("RegularClearlogs") ||
                               jobName.equals("ZipLogFile")
                            		){ // 如果是清理日志的 job
                                String jobClass = (String) data.get(i).get("JOB_CLASS");
                                String groupName = (String) data.get(i).get("JOB_GROUPNAME");
                                String triggerName = (String) data.get(i).get("JOB_TRIGGERNAME");
                                BigDecimal time = (BigDecimal) data.get(i).get("JOB_TIME");
                                String ETL_KETTLE = (String) data.get(i).get("ETL_KETTLE");
                                String JOB_FOLDER = (String) data.get(i).get("JOB_FOLDER");

                                // 0:在适当时机立即执行(基本等价于1) 1:立即执行一次 2:不会先执行一次
                                BigDecimal jobType = (BigDecimal) data.get(i).get("JOB_TYPE");

                                String cronExp = "";

                                if (time.intValue() / 1000 >= 60) {
                                    // 分鐘
                                    // "0 0/2 * * * ?"
                                    cronExp = "0 0/" + time.intValue() / 1000 / 60 + " * * * ?";
                                } else {
                                    // 秒
                                    // "0/3 * * * * ?"
                                    cronExp = "0/" + time.intValue() / 1000 + " * * * * ?";
                                }

                                if (ETL_KETTLE.equals("Y")) {
                                    JobDataMap jdm = new JobDataMap();
                                    jdm.put("JOBNAME", jobName);
                                    jdm.put("JOB_FOLDER", JOB_FOLDER);

                                    QuartzUtil.addSchedule(jobName, jobName, jobClass, "ETLJOB", cronExp, jobType.intValue(),
                                            jdm);
                                } else {
                                    QuartzUtil.addSchedule(triggerName, groupName, jobClass, jobName, cronExp,
                                            jobType.intValue(), null);
                                }
                            }

                        } catch (Exception e)
                        {
                            log.error(e.getMessage(), e);
                        }
                    }

                }
			}catch(Exception e) 
			{
				log.error(e.getMessage(), e);
				if(StaticInfo.DCP_Slave.equals("0"))
				{
					QuartzUtil.addSchedule("JOBTIMER_triggername", "JOBTIMER_groupname", "com.dsc.spos.scheduler.job.JOBTIMER", "JOBTIMER","0 0/1 * * * ?",0, null);
				}
			}
		

		} 
		catch (Exception e) 
		{
			log.error(e.getMessage(), e);

		}

		//商品渠道/门店自动上下架job初始化
		try
		{   // 上架
            List<Map<String, Object>> dataOn = new ArrayList<>();
			//渠道上架
			String sqlOn = "select EID, PLUNO, CHANNELID, ONSHELFDATE || ' ' || ONSHELFTIME ONDATE from DCP_GOODS_SHELF_DATE where ONSHELFAUTO='1' and (ONSHELFDATE || ' ' || ONSHELFTIME) >= to_char(SYSDATE ,'YYYY-MM-DD HH24:MI:SS')";
            List<Map<String, Object>> dataChannelOn = this.doQueryData(sqlOn, null);
            if(!CollectionUtils.isEmpty(dataChannelOn)){
                dataOn.addAll(dataChannelOn);
            }
            // 门店上架
            String sqlOn2 = "select EID, PLUNO,SHOPID, CHANNELID, ONSHELFDATE || ' ' || ONSHELFTIME ONDATE from DCP_GOODS_SHELF_SHOPDATE where ONSHELFAUTO='1' and (ONSHELFDATE || ' ' || ONSHELFTIME) >= to_char(SYSDATE ,'YYYY-MM-DD HH24:MI:SS')";
            List<Map<String, Object>> dataShopOn = this.doQueryData(sqlOn2, null);
            if(!CollectionUtils.isEmpty(dataShopOn)){
                dataOn.addAll(dataShopOn);
            }
            for (Map<String, Object> datum : dataOn) {
				String jobName = datum.get("EID").toString()+datum.get("PLUNO").toString()+datum.get("CHANNELID").toString()+"ONShedule";
				String triggerName = datum.get("EID").toString()+datum.get("PLUNO").toString()+datum.get("CHANNELID").toString()+"ONTrigger";
				JobDataMap map = new JobDataMap();
				map.put("EID", datum.get("EID").toString());
				map.put("PLUNO", datum.get("PLUNO").toString());
				map.put("CHANNELID", datum.get("CHANNELID").toString());
				map.put("TYPE", "ON");
                map.put("SHOPID", datum.getOrDefault("SHOPID",""));
				QuartzUtil.addSchedule(triggerName, DCP_GoodsShelfStatusJob.JOB_GROUP_NAME, DCP_GoodsShelfStatusJob.class.getName(), jobName, QuartzUtil.onlyOnce(datum.get("ONDATE").toString()), 0, map);
			}

			//下架

            // 渠道下架
            List<Map<String, Object>> dataOff = new ArrayList<>();
			String sqlOff = "select EID, PLUNO, CHANNELID, OFFSHELFDATE || ' ' || OFFSHELFTIME OFFDATE from DCP_GOODS_SHELF_DATE where OFFSHELFAUTO='1' and (OFFSHELFDATE || ' ' || OFFSHELFTIME) >= to_char(SYSDATE ,'YYYY-MM-DD HH24:MI:SS')";
            List<Map<String, Object>> dataChannelOff = this.doQueryData(sqlOff, null);
            if(!CollectionUtils.isEmpty(dataChannelOff)){
                dataOff.addAll(dataChannelOff);
            }
            // 门店下架
            String sqlOff2 = "select EID, PLUNO,SHOPID, CHANNELID, OFFSHELFDATE || ' ' || OFFSHELFTIME OFFDATE from DCP_GOODS_SHELF_SHOPDATE where OFFSHELFAUTO='1' and (OFFSHELFDATE || ' ' || OFFSHELFTIME) >= to_char(SYSDATE ,'YYYY-MM-DD HH24:MI:SS')";
            List<Map<String, Object>> dataShopOff = this.doQueryData(sqlOff2, null);
            if(!CollectionUtils.isEmpty(dataShopOff)){
                dataOff.addAll(dataShopOff);
            }
			for (Map<String, Object> datum : dataOff) {
				String jobName = datum.get("EID").toString()+datum.get("PLUNO").toString()+datum.get("CHANNELID").toString()+"OFFShedule";
				String triggerName = datum.get("EID").toString()+datum.get("PLUNO").toString()+datum.get("CHANNELID").toString()+"OFFTrigger";
				JobDataMap map = new JobDataMap();
				map.put("EID", datum.get("EID").toString());
				map.put("PLUNO", datum.get("PLUNO").toString());
				map.put("CHANNELID", datum.get("CHANNELID").toString());
				map.put("TYPE", "OFF");
                map.put("SHOPID", datum.getOrDefault("SHOPID",""));
				QuartzUtil.addSchedule(triggerName, DCP_GoodsShelfStatusJob.JOB_GROUP_NAME, DCP_GoodsShelfStatusJob.class.getName(), jobName, QuartzUtil.onlyOnce(datum.get("OFFDATE").toString()), 0, map);
			}
		}
		catch(Exception ex)
		{
			log.error("\r\n********商品渠道/门店自动上下架job初始化报错！\r\n"+ex.toString());
		}

		if (QuartzUtil.doIsStarted()) 
		{
			// 已經啟動了
		} 
		else 
		{
			try 
			{
				QuartzUtil.doStartSchedule();
			} 
			catch (SchedulerException e) 
			{

			}
		}
		
		System.gc();

	}

	public String getQuerySql() 
	{
		String sql1 = "select JOB_NAME,JOB_CLASS,JOB_GROUPNAME,JOB_TRIGGERNAME,JOB_TIME,JOB_TYPE,STATUS,JOB_DISCRETION,ETL_KETTLE,JOB_FOLDER from job_quartz "
				+ " where status='100' OR JOB_NAME IN ('RegularClearlogs','ZipLogFile','JOBTIMER')       ORDER BY JOB_TIME,JOB_NAME ";
		return sql1;
	}


	private MySpringContext springContext;
	public void setSpringContext(MySpringContext springContext) 
	{
		this.springContext = springContext;
	}

	/**
	 * 查詢資料
	 * @param sql
	 * @param conditionValues
	 * @return
	 * @throws Exception
	 */
	protected List<Map<String, Object>> doQueryData(String sql, String[] conditionValues) throws Exception 
	{		
		return StaticInfo.dao.executeQuerySQL(sql, conditionValues);		
	}



}