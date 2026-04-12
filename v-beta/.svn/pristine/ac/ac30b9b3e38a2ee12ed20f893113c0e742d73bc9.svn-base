package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_ParaDefineUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ParaDefineUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineUpdateReq.level4Elm;
import com.dsc.spos.json.cust.req.DCP_ParaDefineUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_ParaDefineUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：ParaDefineUpdate
 *   說明：参数定义修改
 * 服务说明：参数定义修改
 * @author Jinzma 
 * @since  2017-03-03
 */
public class DCP_ParaDefineUpdate extends SPosAdvanceService<DCP_ParaDefineUpdateReq, DCP_ParaDefineUpdateRes> 
{

	@Override
	protected void processDUID(DCP_ParaDefineUpdateReq req, DCP_ParaDefineUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		try 
		{
			String sqlBaseSetTempItem=getBaseSetTempItem_SQL(req);	
			String[] conditionValues_checkNO = null ; //查詢條件
			List<Map<String, Object>> getQData_checkNO = this.doQueryData(sqlBaseSetTempItem,conditionValues_checkNO);
			if(getQData_checkNO!=null && !getQData_checkNO.isEmpty())
			{
				String item = req.getRequest().getItem();
				String paraType = req.getRequest().getParaType();
				String conType = req.getRequest().getConType();
				String defValue = req.getRequest().getDefValue();
				String initdefValue = req.getRequest().getInitdefValue();	
				String status = req.getRequest().getStatus();			
				String eId = req.geteId();
				String classNO= req.getRequest().getClassNO();
				String toPriority = req.getRequest().getToPriority();  //处理优先级
				String priority = req.getRequest().getPriority();
				String m_onSale = req.getRequest().getOnSale();
				String remark = req.getRequest().getRemark()==null?"":req.getRequest().getRemark();
				List<level1Elm> itemLang = req.getRequest().getItemlang();

				if(Check.Null(toPriority) || toPriority.equals(priority)){
					//删除原有单身
					DelBean db = new DelBean("PLATFORM_BASESETTEMP_LANG");
					db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db));

					DelBean db1 = new DelBean("PLATFORM_BASESETTEMP_PARA");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					DelBean db2 = new DelBean("PLATFORM_BASESETTEMP_PARA_LANG");
					db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db2));
					
					DelBean db3 = new DelBean("PLATFORM_BASESETTEMP_MODULAR");
					db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db3.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db3));
					
					if (req.getRequest().getItemlang()!=null)
					{
						for (level1Elm par : req.getRequest().getItemlang())
						{
							String langType = par.getLangType();
							String itemName = par.getItemName();
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
					}
					
					if (req.getRequest().getModulars()!=null)
					{
						// platform_basesettemp_modular 表
						for (level2Elm par : req.getRequest().getModulars())
						{
							String modularId = par.getModularId();
							String modularName = par.getModularName();
							
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
						List<DCP_ParaDefineUpdateReq.level3Elm> jsonDatas = req.getRequest().getPara();
						for (DCP_ParaDefineUpdateReq.level3Elm par : jsonDatas) {
							String itemValue=par.getItemValue() ;
							String itemOnSale = par.getOnSale();
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
					
					//更新单头PLATFORM_BASESETTEMP
					UptBean ub1 = null;	
					ub1 = new UptBean("PLATFORM_BASESETTEMP");
					//add Value
					ub1.addUpdateValue("ITEMVALUE", new DataValue(defValue, Types.VARCHAR));
					ub1.addUpdateValue("INITDEF", new DataValue(initdefValue, Types.VARCHAR));
					ub1.addUpdateValue("TYPE", new DataValue(paraType, Types.INTEGER));
					ub1.addUpdateValue("CONTYPE", new DataValue(conType, Types.VARCHAR));
					ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
					ub1.addUpdateValue("CLASSNO", new DataValue(classNO, Types.VARCHAR));
					ub1.addUpdateValue("PRIORITY", new DataValue(priority, Types.VARCHAR));
					ub1.addUpdateValue("ONSALE", new DataValue(m_onSale, Types.VARCHAR));
					ub1.addUpdateValue("REMARK", new DataValue(remark, Types.VARCHAR));
					//condition
					ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub1));
}
				else
				{
					int toPt = Integer.parseInt(toPriority);
					if(Check.Null(priority))
					{
						priority="0";
					}
					int pt = Integer.parseInt(priority);

					UptBean ub2 = null;	
					ub2 = new UptBean("PLATFORM_BASESETTEMP");
					///// 如果当前优先级序号 priority = toPriority ,前端需要给出提示信息：“ 已处于第N行 ”
					if(toPt > pt){  //相当 于   下移
						String[] priorityList = new String[toPt-pt];
						for(int i=0; i < toPt-pt ; i++) {
							priorityList[i] = String.valueOf(pt +i+ 1);
						}
						String str1 = StringUtils.join(priorityList,",");

						//add Value
						ub2.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.SubSelf));
						//condition
//						ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
						ub2.addCondition("PRIORITY", new DataValue(toPriority, Types.VARCHAR ));
					}
					else{  //  上移

						String[] priorityList = new String[pt-toPt];
						for(int i=0; i < pt- toPt ; i++) {
							priorityList[i] = String.valueOf(toPt + i);
						}
						String str1 = StringUtils.join(priorityList,",");

						//add Value
						ub2.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
						//condition
//						ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
						ub2.addCondition("PRIORITY", new DataValue(toPriority, Types.VARCHAR ));
					}

					ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));	

					UptBean ub3 = null;	
					ub3 = new UptBean("PLATFORM_BASESETTEMP");
					//add Value
					ub3.addUpdateValue("PRIORITY", new DataValue(toPriority, Types.VARCHAR));
					//condition
					ub3.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));		

					this.addProcessData(new DataProcessBean(ub2));
					this.addProcessData(new DataProcessBean(ub3));

				}
				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else
			{
				res.setSuccess(false);
				res.setServiceDescription("此参数编码不存在！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage()+ "   " +e.getCause());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaDefineUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaDefineUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaDefineUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaDefineUpdateReq req) throws Exception {
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
		if (Check.Null(req.getRequest().getStatus())) 
		{
			errCt++;
			errMsg.append("状态不可为空值, ");
			isFail = true;
		} 		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_ParaDefineUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ParaDefineUpdateReq>(){};
	}

	@Override
	protected DCP_ParaDefineUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ParaDefineUpdateRes();
	}

	protected String getBaseSetTempItem_SQL(DCP_ParaDefineUpdateReq req)
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


}
