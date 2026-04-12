package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaSetUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ParaSetUpdateRes;
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
public class DCP_ParaSetUpdate extends SPosAdvanceService<DCP_ParaSetUpdateReq, DCP_ParaSetUpdateRes> 
{

	@Override
	protected void processDUID(DCP_ParaSetUpdateReq req, DCP_ParaSetUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String sqlBaseSetTempItem=getBaseSetTempItem_SQL(req);	
		String[] conditionValues_checkNO = null ; //查詢條件
		try{
			List<Map<String, Object>> getQData_checkNO = this.doQueryData(sqlBaseSetTempItem,conditionValues_checkNO);
			if(getQData_checkNO!=null && !getQData_checkNO.isEmpty())
			{
				String item = req.getRequest().getItem();
				if ("WaiMai_MT_ISV".equalsIgnoreCase(item)||"WaiMai_ISV_ClientNo".equalsIgnoreCase(item))
                {
                    //要去特定得页面进行注册
                    res.setSuccess(false);
                    res.setServiceStatus("000");
                    res.setServiceDescription("此参数不允许修改！");
                    return;
                }
				String paraType = req.getRequest().getParaType();
				String itemValue = req.getRequest().getItemValue();
				String paraShop = req.getRequest().getParaShop();		
				String paraMachine = req.getRequest().getParaMachine();			
				String eId = req.geteId();
				String lang_type = req.getLangType();
				String sql ;
				
				//更新参数模板
				UptBean ub1 = null;	
				ub1 = new UptBean("PLATFORM_BASESETTEMP");
				//add Value
				ub1.addUpdateValue("ITEMVALUE", new DataValue(itemValue, Types.VARCHAR));
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				//condition
				ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));				
				
//				if (Check.Null(paraShop))
//				{
//					///原来更新temp 在这里。。。
//				}
//				else 
//				{
//					//查询门店基本参数
//					if (Check.Null(paraMachine))
//					{
//						sql="select ITEM from Platform_BaseSet WHERE status='100' and EID='"+eId +"' and SHOPID='"+paraShop +"' AND ITEM = '"+ item +"' ";
//						paraMachine=" ";
//					}
//					else
//					{
//						sql="select ITEM from Platform_BaseSet WHERE status='100' and EID='"+eId +"' and SHOPID='"+paraShop +"'  and  MACHINE='"+paraMachine +"'  AND ITEM = '"+ item +"'  ";
//					}
//
//					String[] conditionValues_Count = {};			//查詢條件
//					List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
//					if (getQData_Count == null || getQData_Count.isEmpty() == true)
//					{
//						//新增门店基本参数
//						DataValue[] insValue1 = null;
//						String[] columns1 = {"EID","ORGANIZATIONNO","SHOPID","MACHINE","ITEM","ITEMVALUE","STATUS"};
//						insValue1 = new DataValue[]{
//								new DataValue(eId, Types.VARCHAR),
//								new DataValue(paraShop, Types.VARCHAR),
//								new DataValue(paraShop, Types.VARCHAR),
//								new DataValue(paraMachine, Types.VARCHAR),
//								new DataValue(item, Types.VARCHAR),
//								new DataValue(itemValue, Types.VARCHAR),
//								new DataValue("100", Types.VARCHAR),
//						};
//						InsBean ib1 = new InsBean("PLATFORM_BASESET", columns1);
//						ib1.addValues(insValue1);
//						this.addProcessData(new DataProcessBean(ib1));
//
//					}
//					else
//					{
//						//更新门店基本参数
//						UptBean ub1 = null;	
//						ub1 = new UptBean("PLATFORM_BASESET");
//						//add Value
//						ub1.addUpdateValue("ITEMVALUE", new DataValue(itemValue, Types.VARCHAR));
//						//condition
//						ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
//						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//						ub1.addCondition("SHOPID", new DataValue(paraShop, Types.VARCHAR));
//						if (!Check.Null(paraMachine))
//						{
//							ub1.addCondition("MACHINE", new DataValue(paraMachine, Types.VARCHAR));
//						}
//						this.addProcessData(new DataProcessBean(ub1));
//					}
//				}
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
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaSetUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaSetUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaSetUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaSetUpdateReq req) throws Exception {
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
//		if (Check.Null(req.getParaType())) 
//		{
//			errCt++;
//			errMsg.append("参数类型不可为空值, ");
//			isFail = true;
//		}	
//		if (Check.Null(req.getItemValue())) 
//		{
//			errCt++;
//			errMsg.append("参数值不可为空值, ");
//			isFail = true;
//		}	

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_ParaSetUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ParaSetUpdateReq>(){};
	}

	@Override
	protected DCP_ParaSetUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ParaSetUpdateRes();
	}

	protected String getBaseSetTempItem_SQL(DCP_ParaSetUpdateReq req)
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId=req.geteId();
		String ITEM=req.getRequest().getItem();
		sqlbuf.append("select ITEM from Platform_BaseSetTemp "
				+ "WHERE status='100' AND  EID='"+eId +"'  "
				+ "AND ITEM='"+ITEM +"' ");			
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
