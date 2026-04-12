package com.dsc.spos.service.imp.json;

import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_TouchMenuDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_TouchMenuDetailQueryRes;
public class DCP_TouchMenuDetailQuery extends SPosBasicService<DCP_TouchMenuDetailQueryReq, DCP_TouchMenuDetailQueryRes>  {
	/**
	 * 服務函數：TouchMenuDetailGetDCP
	 * 服务说明：触屏菜单明细查询DCP
	 * @author Jinzma 
	 * @since  2018-10-26
	 */
	@Override
	protected boolean isVerifyFail(DCP_TouchMenuDetailQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String menuNO = req.getMenuNO();
		if (Check.Null(menuNO)) {
			errMsg.append("菜单编号不可为空值, ");
			isFail = true;
		} 
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TouchMenuDetailQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return  new TypeToken<DCP_TouchMenuDetailQueryReq>(){};
	}

	@Override
	protected DCP_TouchMenuDetailQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TouchMenuDetailQueryRes();
	}

	@Override
	protected DCP_TouchMenuDetailQueryRes processJson(DCP_TouchMenuDetailQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;
		DCP_TouchMenuDetailQueryRes res = null;
		res = this.getResponse();	
		try 
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("A_CLASSNO", true);
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			res.setDatas(new ArrayList<DCP_TouchMenuDetailQueryRes.level1Elm>()) ;
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_TouchMenuDetailQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setGoods(new ArrayList<DCP_TouchMenuDetailQueryRes.level2Elm>());		
				//取出第一层
				String classNO = oneData.get("A_CLASSNO").toString();
				String className = oneData.get("A_CLASSNAME").toString();
				String priority = oneData.get("A_PRIORITY").toString();
				String lbTime = oneData.get("A_LBTIME").toString();
				String leTime = oneData.get("A_LETIME").toString();
				String status =  oneData.get("A_STATUS").toString();
				String picRatio =  oneData.get("A_PICRATIO").toString() ;
				//设置响应
				if (Check.Null(classNO)) continue ;
				oneLv1.setClassNO(classNO);
				oneLv1.setClassName(className);
				oneLv1.setPriority(priority);
				oneLv1.setLbTime(lbTime);
				oneLv1.setLeTime(leTime);
				oneLv1.setStatus(status);
				oneLv1.setPicRatio(picRatio);
				for (Map<String, Object> oneData2 : getQDataDetail) 
				{
					//过滤属于此单头的明细
					if(classNO.equals(oneData2.get("A_CLASSNO")))
					{
						DCP_TouchMenuDetailQueryRes.level2Elm oneLv2 = res.new level2Elm();
						String type=oneData2.get("B_TYPE").toString();	
						String pluNO=oneData2.get("B_PLUNO").toString();											
						String dispName=oneData2.get("B_DISPNAME").toString();
						String unitNO=oneData2.get("B_UNITNO").toString();
						String unitName=oneData2.get("B_UNITNAME").toString();
						String price=oneData2.get("B_PRICE").toString();
						String b_priority=oneData2.get("B_PRIORITY").toString();
						//单身赋值
						if (Check.Null(pluNO)) continue ;
						oneLv2.setType(type);
						oneLv2.setPluNO(pluNO);
						oneLv2.setDispName(dispName);
						oneLv2.setUnitNO(unitNO);
						oneLv2.setUnitName(unitName);
						oneLv2.setPrice(price);
						oneLv2.setPriority(b_priority);
						oneLv2.setStatus("100");

						oneLv1.getGoods().add(oneLv2);
					}
				}
				res.getDatas().add(oneLv1);
			}

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");		
		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_TouchMenuDetailQueryReq req) throws Exception {

		String sql=null;
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String menuNO=req.getMenuNO();
		String langType = req.getLangType();
		sqlbuf.append(" "
				+ " select a_classno,a_classname,a_priority,a_lbtime,a_letime,a_status,a_picratio,b_pluno,b_type,b_dispname,b_unitno, "
				+ " b_unitname,b_priority,B_PRICE from ( select a.classno as a_classno ,a.classname as a_classname, "
				+ " a.priority as a_priority ,a.lbtime as a_lbtime ,a.letime as a_letime,a.status as a_status,"
				+ " a.PICRATIO as a_picratio ,"
				+ " b.pluno as b_pluno,b.type as b_type, b.dispname as b_dispname ,b.unit as b_unitno , "
				+ " c.unit_name as b_unitname,b.priority as b_priority,d.price1 as B_PRICE from DCP_TOUCHMENU_class  a "
				+ "	left join DCP_TOUCHMENU_class_goods b "
				+ " on a.EID=b.EID and a.menuno=b.menuno  and a.classno=b.classno  and b.status='100'  "
				+ " left join DCP_UNIT_lang c on c.EID=b.EID and c.unit=b.unit and  "
				+ " c.status='100' and c.lang_type='"+langType +"'  "
				+ " left join ( select pluno,unit,EID,max(price1) as price1 from  DCP_PRICE "
				+ " where status='100' group by  pluno,unit,EID) d "
				+ " on d.EID=b.EID and d.pluno=b.pluno and d.unit = b.unit "
				
				+ "  and a.EID = d.EID and b.type = '1' "
				+ "	where a.EID='"+eId +"' and a.menuno='"+menuNO +"'   ) "
				+ " order by a_priority,b_priority " );

		sql=sqlbuf.toString();
		return sql;

	}





}
