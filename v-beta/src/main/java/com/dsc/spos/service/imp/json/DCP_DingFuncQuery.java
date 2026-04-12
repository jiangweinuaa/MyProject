package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DingFuncQueryReq;
import com.dsc.spos.json.cust.res.DCP_DingFuncQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DingFuncGetDCP
 * 服务说明：钉钉功能审批查询
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingFuncQuery extends SPosBasicService<DCP_DingFuncQueryReq,DCP_DingFuncQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_DingFuncQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_DingFuncQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingFuncQueryReq>(){};
	}

	@Override
	protected DCP_DingFuncQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingFuncQueryRes();
	}

	@Override
	protected DCP_DingFuncQueryRes processJson(DCP_DingFuncQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_DingFuncQueryRes res = this.getResponse();
		String langType=req.getLangType();
		try
		{		
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_DingFuncQueryRes.level1Elm>());
			//预置钉钉审批功能    6.退货    7.退单    18.赠送    37.行折   38.全折   89.退订
//			boolean funcNO6 = true ;
//			boolean funcNO7 = true;
//			boolean funcNO18 = true ;
//			boolean funcNO37 = true ;
//			boolean funcNO38 = true ;
//			boolean funcNO89 = true ;

			if (getQData != null && getQData.isEmpty() == false) 
			{
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("FUNCNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_DingFuncQueryRes.level1Elm oneLv1 = new DCP_DingFuncQueryRes().new level1Elm();
					oneLv1.setDatas(new ArrayList<DCP_DingFuncQueryRes.level2Elm>());		
					//取出第一层
					String funcNO = oneData.get("FUNCNO").toString();
					String funcName = oneData.get("CHSMSG").toString();
					if (langType.equals("zh_TW")) funcName = oneData.get("CHTMSG").toString();
					String templateNO = oneData.get("TEMPLATENO").toString();
					String templateName = oneData.get("TEMPLATENAME").toString();
					String status = oneData.get("STATUS").toString();

					condition.clear();
					condition.put("FUNCNO", true);
					condition.put("SHOPID", true);
					//调用过滤函数
					List<Map<String, Object>> getQShop=MapDistinct.getMap(getQData, condition);
					for (Map<String, Object> oneShop : getQShop) 
					{
						//过滤属于此单头的明细
						if(funcNO.equals(oneShop.get("FUNCNO")))
						{
							DCP_DingFuncQueryRes.level2Elm oneLv2 = new DCP_DingFuncQueryRes().new level2Elm();
							oneLv2.setDatas(new ArrayList<DCP_DingFuncQueryRes.level3Elm>());		
							String shopId = oneShop.get("SHOPID").toString();
							String shopName = oneShop.get("ORG_NAME").toString();
							String defUserID = oneShop.get("DEF_USERID").toString();
							String defUserName = oneShop.get("DEFUSERNAME").toString();
							String defDeptID = oneShop.get("DEF_DEPTID").toString();

							for (Map<String, Object> oneDataAppr : getQData) 
							{
								if(funcNO.equals(oneDataAppr.get("FUNCNO")) && shopId.equals(oneDataAppr.get("SHOPID")))
								{
									DCP_DingFuncQueryRes.level3Elm oneLv3 = new DCP_DingFuncQueryRes().new level3Elm();
									String approvedByid = oneDataAppr.get("APPROVEDBYID").toString();
									String approvedByName = oneDataAppr.get("APPROVEDBYNAME").toString();
									String approvedByDeptID = oneDataAppr.get("APPROVEDBYDEPTID").toString();

									oneLv3.setApprovedByDeptID(approvedByDeptID);
									oneLv3.setApprovedByid(approvedByid);
									oneLv3.setApprovedByName(approvedByName);

									oneLv2.getDatas().add(oneLv3);
									oneLv3 = null;
								}
							}

							oneLv2.setDefDeptID(defDeptID);
							oneLv2.setDefUserID(defUserID);
							oneLv2.setDefUserName(defUserName);
							oneLv2.setShopName(shopName);
							oneLv2.setShopId(shopId);
							oneLv1.getDatas().add(oneLv2);
							
							oneLv2 = null;
						}
					}
					oneLv1.setStatus(status);
					oneLv1.setFuncName(funcName);
					oneLv1.setFuncNo(funcNO);
					oneLv1.setTemplateName(templateName);
					oneLv1.setTemplateNo(templateNO);
					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}	
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(0);
			res.setTotalPages(0);
			return res;		

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DingFuncQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType = req.getLangType();
		String keyTxt = req.getRequest().getKeyTxt();
		sqlbuf.append(" select a.EID,a.funcno,nvl(i.chsmsg,d.chsmsg) as chsmsg,nvl(i.chtmsg,d.chtmsg) as chtmsg,a.templateno,e.templatename,a.status,"
				+ " b.SHOPID,f.org_name,b.def_userid,g.username as defUserName,b.def_deptid,"
				+ " c.approvedbyid,h.username as approvedByName,c.approvedbydeptid from DCP_DING_FUNC a "
				+ " left join DCP_DING_FUNC_SHOP b on a.EID=b.EID and a.funcno=b.funcno "
				+ " left join DCP_DING_FUNC_SHOP_APPROVEDBY c on a.EID=c.EID and a.funcno=c.funcno and b.SHOPID=c.SHOPID "
				+ " left join DCP_MODULAR_function d on a.EID=d.EID and a.funcno=d.funcno and d.status='100' and d.modularno='2701' "
				+ " left join DCP_DING_TEMPLATE e on a.EID=e.EID and a.templateno=e.templateno and e.status='100' "
				+ " left join DCP_ORG_lang f on a.EID=f.EID and b.SHOPID=f.organizationno and f.status='100' and f.lang_type='"+langType+"' "
				+ " left join DCP_DING_USERSET g on a.EID=g.EID and b.def_userid=g.userid and g.status='100' "
				+ " left join DCP_DING_USERSET h on a.EID=h.EID and c.approvedbyid=h.userid and h.status='100' "
				+ " left join DCP_MODULAR i on a.EID=i.EID and a.funcno=i.modularno and i.status='100' "
				+ " where a.EID='"+eId+"' " );

		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.funcno like '%%"+keyTxt+"%%' or d.chsmsg like '%%"+keyTxt+"%%' or d.chtmsg like '%%"+keyTxt+"%%' )  ");
		}
		sqlbuf.append(" order by a.funcno ");

		sql = sqlbuf.toString();
		return sql;

	}

}
