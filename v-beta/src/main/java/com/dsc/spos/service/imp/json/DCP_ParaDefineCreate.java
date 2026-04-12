package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level3Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineCreateReq.level4Elm;
import com.dsc.spos.json.cust.res.DCP_ParaDefineCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 服務函數：ParaDefineCreate
 *   說明：参数定义新增
 * 服务说明：参数定义新增
 * @author Jinzma 
 * @since  2017-03-03
 */
public class DCP_ParaDefineCreate extends SPosAdvanceService<DCP_ParaDefineCreateReq,DCP_ParaDefineCreateRes>
{
	@Override
	protected void processDUID(DCP_ParaDefineCreateReq req, DCP_ParaDefineCreateRes res) throws Exception {
		// TODO 自动生成的方法存根			
		String sql = null;
		try{
//			sql = this.getBaseSetTempItem_SQL(req);
//			String[] conditionValues  = null; 
//			List<Map<String, Object>> getQData_check = this.doQueryData(sql,conditionValues);
//			//参数存在否判断
//			if(getQData_check==null || getQData_check.isEmpty())
//			{		
				//新增单头资料	
				String item    = req.getRequest().getItem();
				String def     = req.getRequest().getDefValue();
				String initdef = req.getRequest().getInitdefValue();
				String type =req.getRequest().getParaType();  ///1.中台参数 2.门店参数 3.POS参数 4.机台参数
				String conType =req.getRequest().getConType();   ///1.文本格式 2.数字格式 3.日期格式 4.时间格式 5.下拉框 6.状态格式
				String eId = req.geteId();
				String classNO = req.getRequest().getClassNO();
				String onSale = req.getRequest().getOnSale();
				String priority = req.getRequest().getPriority();
				String remark = req.getRequest().getRemark()==null?"":req.getRequest().getRemark();
				
				DelBean dbmain = new DelBean("PLATFORM_BASESETTEMP");
				dbmain.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				dbmain.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(dbmain)); // 删除
				
				DataValue[] insValue1 = null;
				String[] columns1 = {"EID","ITEM","CLASSNO","ITEMVALUE","INITDEF","TYPE","CONTYPE","STATUS","PRIORITY","ONSALE","REMARK"};
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(item, Types.VARCHAR),
						new DataValue(classNO, Types.VARCHAR),
						new DataValue(def, Types.VARCHAR),
						new DataValue(initdef, Types.VARCHAR),
						new DataValue(type, Types.INTEGER),
						new DataValue(conType, Types.VARCHAR),
						new DataValue("100", Types.VARCHAR),
						new DataValue(priority, Types.VARCHAR), 
						new DataValue(onSale, Types.VARCHAR), 
						new DataValue(remark, Types.VARCHAR) 
				};
				InsBean ib1 = new InsBean("PLATFORM_BASESETTEMP", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));

				for (level1Elm par : req.getRequest().getItemlang())
				{
					String langType = par.getLangType();
					String itemName = par.getItemName();
					
					DelBean db1 = new DelBean("PLATFORM_BASESETTEMP_LANG");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					db1.addCondition("LANG_TYPE", new DataValue(langType, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1)); // 删除
					
					String[] columns2 = {"EID","ITEM","LANG_TYPE","ITEM_NAME","STATUS"};
					DataValue[] insValue2 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(item, Types.VARCHAR),
							new DataValue(langType, Types.VARCHAR),
							new DataValue(itemName, Types.VARCHAR),
							new DataValue("100", Types.VARCHAR),
					};
					InsBean ib2 = new InsBean("PLATFORM_BASESETTEMP_LANG", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
				}

				// platform_basesettemp_modular 表
				if (req.getRequest().getModulars()!=null)
				{
					for (level2Elm par : req.getRequest().getModulars())
					{
						String modularId = par.getModularId();
						String modularName = par.getModularName();

						DelBean db1 = new DelBean("PLATFORM_BASESETTEMP_MODULAR");
						db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
						db1.addCondition("MODULARID", new DataValue(modularId, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db1)); // 删除	
						
						String[] columns2 = {"EID","ITEM","MODULARID","MODULARNAME"};
						DataValue[] insValue2 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(item, Types.VARCHAR),
								new DataValue(modularId, Types.VARCHAR),
								new DataValue(modularName, Types.VARCHAR)
						};
						InsBean ib2 = new InsBean("PLATFORM_BASESETTEMP_MODULAR", columns2);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));
					}

				}

				// 参数值表 platform_basesettemp_para 和 platform_basesettemp_para_lang 表

				if (req.getRequest().getPara()!=null)
				{
					//新增新的单身（PLATFORM_BASESETTEMP_PARA）
					List<DCP_ParaDefineCreateReq.level3Elm> jsonDatas = req.getRequest().getPara();
					for (DCP_ParaDefineCreateReq.level3Elm par : jsonDatas) {
						
						String itemValue=par.getItemValue() ;
						String itemOnSale = par.getOnSale();
						
						DelBean db1 = new DelBean("PLATFORM_BASESETTEMP_PARA");
						db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
						db1.addCondition("ITEMVALUE", new DataValue(itemValue, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(db1)); // 删除
						
						String[] columnsName = {"EID","ITEM","ITEMVALUE","ONSALE","STATUS"};
						DataValue[] insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(item, Types.VARCHAR),
								new DataValue(itemValue, Types.VARCHAR),
								new DataValue(itemOnSale, Types.VARCHAR),
								new DataValue("100", Types.VARCHAR),
						};
						InsBean ib = new InsBean("PLATFORM_BASESETTEMP_PARA", columnsName);
						ib.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib));

						List<level4Elm> valuelang = par.getValuelang();
						for (level4Elm level2par: valuelang )
						{
							String langType = level2par.getLangType();
							String valueName = level2par.getValueName();
							
							DelBean db2 = new DelBean("PLATFORM_BASESETTEMP_PARA_LANG");
							db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							db2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
							db2.addCondition("ITEMVALUE", new DataValue(itemValue, Types.VARCHAR));
							db2.addCondition("LANG_TYPE", new DataValue(langType, Types.VARCHAR));
							this.addProcessData(new DataProcessBean(db2)); // 删除
							
							
							//新增新的单身（PLATFORM_BASESETTEMP_PARA_LANG）
							String[] columnsNameLang = {"EID","ITEM","ITEMVALUE","LANG_TYPE","VALUE_NAME","STATUS"};
							insValue = new DataValue[]{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(item, Types.VARCHAR),
									new DataValue(itemValue, Types.VARCHAR),
									new DataValue(langType, Types.VARCHAR),
									new DataValue(valueName, Types.VARCHAR),
									new DataValue("100", Types.VARCHAR),
							};							
							InsBean ibLang = new InsBean("PLATFORM_BASESETTEMP_PARA_LANG", columnsNameLang);
							ibLang.addValues(insValue);
							this.addProcessData(new DataProcessBean(ibLang));
						}
					}
				}

				this.doExecuteDataToDB();
//			}
//			else 
//			{
//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数："+req.getRequest().getItem()+"已经存在");
//			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}


	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaDefineCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaDefineCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaDefineCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaDefineCreateReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；	
		if (Check.Null(req.getRequest().getItem())) 
		{
			errCt++;
			errMsg.append("参数编码不可为空值, ");
			isFail = true;
		} 

		//		if (Check.Null(req.getDefValue())) 
		//		{
		//			errCt++;
		//			errMsg.append("默认值不可为空值, ");
		//			isFail = true;
		//		}

		if (Check.Null(req.getRequest().getParaType())) 
		{
			errCt++;
			errMsg.append("参数类型不可为空值, ");
			isFail = true;
		} 
		else
		{
			if (!isNumeric(req.getRequest().getParaType().toString())){
				errCt++;
				errMsg.append("参数类型必须为数值, ");
				isFail = true;
			}
		} 

		if (Check.Null(req.getRequest().getConType()))
		{
			errCt++;
			errMsg.append("控件类型不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ParaDefineCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ParaDefineCreateReq>(){};
	}

	@Override
	protected DCP_ParaDefineCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ParaDefineCreateRes();

	}

	protected String getBaseSetTempItem_SQL(DCP_ParaDefineCreateReq req)
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId=req.geteId();
		String ITEM=req.getRequest().getItem();
		sqlbuf.append("select ITEM from Platform_BaseSetTemp "
				+ "WHERE EID='"+eId +"'  "
				+ "AND UPPER(ITEM)='"+ITEM.toUpperCase() +"' ");			
		sql = sqlbuf.toString(); 	
		return sql;	
	}

	public boolean isNumeric(String str){   
		//将正则表达式修改为“^-?[0-9]+”即可，修改为“-?[0-9]+.?[0-9]+”即可匹配所有数字。
		Pattern pattern = Pattern.compile("[0-9]*"); 
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false; 
		} 
		return true; 
	}


	/**
	 * 
	 * @param eId
	 * @return
	 */
	protected String getMaxPriority(String eId)
	{ 
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select NVL (MAX(priority)+1, 1  ) AS priority  from Platform_BaseSetTemp "
				+ "WHERE EID= '"+eId +"'");

		sql = sqlbuf.toString();

		return sql;	
	}




}
