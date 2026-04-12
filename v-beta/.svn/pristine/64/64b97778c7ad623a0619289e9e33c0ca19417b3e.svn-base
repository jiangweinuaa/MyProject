package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_FuncButtonQueryReq;
import com.dsc.spos.json.cust.res.DCP_FuncButtonQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：FuncButtonGetDCP
 * 服务说明：功能按键查询
 * @author jinzma 
 * @since  2019-02-19
 */
public class DCP_FuncButtonQuery extends SPosBasicService <DCP_FuncButtonQueryReq,DCP_FuncButtonQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_FuncButtonQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_FuncButtonQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_FuncButtonQueryReq>(){};
	}

	@Override
	protected DCP_FuncButtonQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_FuncButtonQueryRes();
	}

	@Override
	protected DCP_FuncButtonQueryRes processJson(DCP_FuncButtonQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		String eId=req.geteId();
		DCP_FuncButtonQueryRes res = this.getResponse();	
		try 
		{
			sql=" select num from ( "
					+ " select count(*) over() num from DCP_FUNCBUTTON "
					+ " where EID='"+ eId +"'  group by EID,ptemplateno ) " ;
			List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData_Count != null && getQData_Count.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData_Count.get(0).get("NUM").toString();
				totalRecords = Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				sql=this.getQuerySql(req);	
				List<Map<String, Object>> getQData=this.doQueryData(sql, null);
		
			//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("PTEMPLATENO", true);	
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData,condition);
				
				res.setDatas(new ArrayList<DCP_FuncButtonQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQHeader) {
					DCP_FuncButtonQueryRes.level1Elm oneLv1 = res.new level1Elm(); 
					oneLv1.setDatas(new ArrayList<DCP_FuncButtonQueryRes.level2Elm>());	
					oneLv1.setShops(new ArrayList<DCP_FuncButtonQueryRes.level2shopsElm>());	
					String ptemplateNO = oneData.get("PTEMPLATENO").toString();
					String ptemplateName = oneData.get("PTEMPLATENAME").toString();
					String businessType = oneData.get("BUSINESSTYPE").toString();  
					
					oneLv1.setPtemplateNo(ptemplateNO);
					oneLv1.setPtemplateName(ptemplateName);
					oneLv1.setBusinessType(businessType);
					condition.clear();
					condition.put("PTEMPLATENO", true);
					condition.put("FUNCNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQDetail=MapDistinct.getMap(getQData,condition);
					for (Map<String, Object> oneDataDetail : getQDetail) 
					{
						if(ptemplateNO.equals(oneDataDetail.get("PTEMPLATENO").toString())   )
						{							
							DCP_FuncButtonQueryRes.level2Elm oneLv2 = res.new level2Elm();
							String funcNO = oneDataDetail.get("FUNCNO").toString();
							String funcName = oneDataDetail.get("FUNCNAME").toString();
							String icon = oneDataDetail.get("ICON").toString();
							String funcGroup = oneDataDetail.get("FUNCGROUP").toString();
							String qss = oneDataDetail.get("BUTTONQSS").toString();
							String approveneed = oneDataDetail.get("APPROVENEED").toString();
							String priority = oneDataDetail.get("PRIORITY").toString();
							String status = oneDataDetail.get("STATUS").toString();

							oneLv2.setFuncNo(funcNO);
							oneLv2.setFuncName(funcName);
							oneLv2.setIcon(icon);
							oneLv2.setFuncGroup(funcGroup);
							oneLv2.setQss(qss);
							oneLv2.setApproveneed(approveneed);
							oneLv2.setPriority(priority);
							oneLv2.setStatus(status);		
							oneLv1.getDatas().add(oneLv2);
							oneLv2 = null;
									
						}						
					}

					condition.clear();
					condition.put("PTEMPLATENO", true);
					condition.put("SHOPID", true);
					//调用过滤函数
					List<Map<String, Object>> getQDetailShop=MapDistinct.getMap(getQData,condition);
					for (Map<String, Object> oneDataDetailShop : getQDetailShop) 
					{
						String shopId = oneDataDetailShop.get("SHOPID").toString();
						if(ptemplateNO.equals(oneDataDetailShop.get("PTEMPLATENO")) && !Check.Null(shopId))
						{							
							DCP_FuncButtonQueryRes.level2shopsElm oneLv2shop = res.new level2shopsElm();
							
							String shopName = oneDataDetailShop.get("ORG_NAME").toString();
							oneLv2shop.setShopId(shopId);		
							oneLv2shop.setShopName(shopName);
							oneLv1.getShops().add(oneLv2shop);
							oneLv2shop = null;
						}						
					}
					res.getDatas().add(oneLv1);	
					oneLv1 = null;
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_FuncButtonQueryRes.level1Elm>());				
				totalRecords = 0;
				totalPages = 0;	
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
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
	protected String getQuerySql(DCP_FuncButtonQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType=req.getLangType();

		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append( " select a1.EID,a1.ptemplateno,a1.ptemplatename,a1.funcno, "
				+ " a1.funcname,a1.icon,a1.funcgroup,a1.priority,a1.buttonqss,a1.approveneed,a1.status,a1.SHOPID,a1.BUSINESSTYPE,d.org_name from ( "
				+ " select a.EID,a.ptemplateno,a.ptemplatename,a.funcno,c.chsmsg as funcname,a.icon, "
				+ " a.funcgroup,a.priority,a.buttonqss,a.approveneed,a.status,b.SHOPID,a.BUSINESSTYPE from DCP_FUNCBUTTON a "
				+ " left join DCP_FUNCBUTTON_shop b on a.EID=b.EID and a.ptemplateno=b.ptemplateno"
				+ " left join DCP_MODULAR_function c on a.EID=c.EID and a.funcno=c.funcno and c.status='100' "
				+ " where a.EID='"+ eId +"' ) a1  "
				+ " inner join "
				+ " (select row_number() over ( order by a.ptemplateno ) rn,a.ptemplateno from DCP_FUNCBUTTON a  "
				+ " where a.EID='"+ eId +"'   group by a.ptemplateno) a2 "
				+ " on a1.ptemplateno=a2.ptemplateno  "
				+ " left join DCP_ORG_lang d on a1.EID=d.EID and a1.SHOPID=d.organizationno and d.lang_type='"+langType+"' "
				+ " where rn>"+startRow+" and rn<=" +(startRow+pageSize) 
				+ " order by a1.ptemplateno,a1.funcgroup,a1.priority  "  );

		sql = sqlbuf.toString();
		return sql;

	}

}
