package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PageFunQueryReq;
import com.dsc.spos.json.cust.res.DCP_PageFunQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PageFunQuery extends SPosBasicService<DCP_PageFunQueryReq,DCP_PageFunQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_PageFunQueryReq req) throws Exception 
	{
	
		return false;
	}

	@Override
	protected TypeToken<DCP_PageFunQueryReq> getRequestType() 
	{	
		return new TypeToken<DCP_PageFunQueryReq>(){};
	}

	@Override
	protected DCP_PageFunQueryRes getResponseType() 
	{	
		return new DCP_PageFunQueryRes();
	}

	@Override
	protected DCP_PageFunQueryRes processJson(DCP_PageFunQueryReq req) throws Exception 
	{

		String sql=null;
		DCP_PageFunQueryRes res=this.getResponse();
		//单头总数
		sql = this.getCountSql(req);			

		String[] condCountValues = { }; //查詢條件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, condCountValues);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false) 
		{ 			
			Map<String, Object> oneData_Count = getQData_Count.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);

		//
		sql=this.getQuery_FuncSql(req);
		String[] conditionValues = {}; //查詢條件
		List<Map<String, Object>> getQDataFunc=this.doQueryData(sql, conditionValues);

		res.setFunc(new ArrayList<DCP_PageFunQueryRes.level1ElmFunc>());

		if (getQDataFunc != null && getQDataFunc.isEmpty() == false)
		{	
			for (Map<String, Object> oneData : getQDataFunc) 
			{
				DCP_PageFunQueryRes.level1ElmFunc oneLvFunc= res.new level1ElmFunc();

				String funcNO= oneData.get("FUNCNO").toString();
				String chsmsg= oneData.get("CHSMSG").toString();

				oneLvFunc.setFuncNO(funcNO);
				oneLvFunc.setFuncName(chsmsg);

				res.getFunc().add(oneLvFunc);
			}
		}

		//
		sql=this.getQuerySql(req);
		String[] conditionValues1 = {}; //查詢條件
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("pageMenuID", true);		
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);

			res.setDatas(new ArrayList<DCP_PageFunQueryRes.level1ElmPage>());

			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_PageFunQueryRes.level1ElmPage oneLv1 = res.new level1ElmPage();
				oneLv1.setDatas(new ArrayList<DCP_PageFunQueryRes.level2Elm>());

				String bgColor= oneData.get("BGCOLOR").toString();
				String buttonName= oneData.get("BUTTONNAME").toString();
				String fntColor= oneData.get("FNTCOLOR").toString();
				String fntName= oneData.get("FNTNAME").toString();
				String fntSize= oneData.get("FNTSIZE").toString();
				String fntStyle= oneData.get("FNTSTYLE").toString();
				String high= oneData.get("HIGH").toString();
				String pageID= oneData.get("PAGEID").toString();
				String pageIndex= oneData.get("PAGEINDEX").toString();
				String pageMenuID= oneData.get("PAGEMENUID").toString();
				String shopId= oneData.get("SHOPID").toString();
				String shopName= oneData.get("SHOPNAME").toString();
				String width= oneData.get("WIDTH").toString();
				String x= oneData.get("X").toString();
				String y= oneData.get("Y").toString();

				oneLv1.setBgColor(bgColor);
				oneLv1.setButtonName(buttonName);
				oneLv1.setFntColor(fntColor);
				oneLv1.setFntName(fntName);
				oneLv1.setFntSize(fntSize);
				oneLv1.setFntStyle(fntStyle);
				oneLv1.setHigh(high);
				oneLv1.setPageID(pageID);
				oneLv1.setPageIndex(pageIndex);
				oneLv1.setPageMenuID(pageMenuID);
				oneLv1.setShopId(shopId);
				oneLv1.setShopName(shopName);
				oneLv1.setWidth(width);
				oneLv1.setX(x);
				oneLv1.setY(y);

				for (Map<String, Object> oneData2 : getQDataDetail) 
				{
					//过滤属于此单头的明细
					if(pageMenuID.equals(oneData2.get("PAGEMENUID"))==false) continue;

					DCP_PageFunQueryRes.level2Elm oneLv2 = res.new level2Elm();

					String funcno = oneData2.get("FUNCNO").toString();

					if(funcno.trim().equals("")) continue;//过滤掉空值

					oneLv2.setFuncNO(funcno);

					//添加
					oneLv1.getDatas().add(oneLv2);
				}

				//添加
				res.getDatas().add(oneLv1);
			}			

		}
		else
		{
			res.setDatas(new ArrayList<DCP_PageFunQueryRes.level1ElmPage>());			
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * 查询数据
	 */
	@Override
	protected String getQuerySql(DCP_PageFunQueryReq req) throws Exception 
	{
		String sql=null;
		String KeyTxt=req.getKeyTxt();
		String PageType=req.getPageType();
		String oShopId= req.getoShopId();

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		if(KeyTxt==null) KeyTxt="";
		if(PageType==null) PageType="";
		if(oShopId==null) oShopId="";

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select pageMenuID ,pageID,SHOPID,pageIndex,x,y,high,width, "
				+"bgColor,fntName,fntSize,fntColor, "
				+"fntStyle,buttonName,pagetype,pageName,shopName ,funcno "
				+"from "
				+"( " 
				+ "select a.page_menu_id as pageMenuID ,a.PAGE_ID as pageID,a.SHOPID,a.pageIndex,a.x,a.y,a.high,a.width, "
				+"a.bg_color as bgColor,a.fnt_name as fntName,a.fnt_size as fntSize,a.fnt_color as fntColor, "
				+"a.fnt_style as fntStyle,a.buttonName,b.pagetype,b.pageName,d.SHOPNAME as shopName ,c.funcno "
				+"from ta_page_menu a  "
				+"inner join ta_page b on a.EID=b.EID and a.page_id=b.page_id " 
				+"inner join ta_page_menu_func c on a.EID=c.EID and a.page_menu_id=c.page_menu_id "
				+"inner join "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") d on a.EID=d.EID and a.SHOPID=d.SHOPID  "
				+"and d.lang_type='"+req.getLangType()+"' "
				+"where  a.EID='"+req.geteId()+"' ");

		if (PageType != null && PageType.length()>0)
		{
			sqlbuf.append(" and b.pagetype='"+PageType+"' ");
		}

		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append(" and a.SHOPID='"+oShopId+"' ");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append(" and (a.SHOPID like '"+KeyTxt+"%' or a.buttonname  like '"+KeyTxt+"%') ");
		}
		else
		{
			sqlbuf.append(" and  a.SHOPID in (select SHOPID  from platform_staffs_shop where EID='"+req.geteId()+"' and opno='"+req.getOpNO()+"'   )");			
		}

		sqlbuf.append("and a.page_menu_id in "
				+"( "
				+"select pageMenuID from  "
				+"( "
				+"select rownum rn,pageMenuID from  "
				+"( "
				+"select distinct a.page_menu_id as pageMenuID from ta_page_menu a " 
				+"inner join ta_page b on a.EID=b.EID and a.page_id=b.page_id  "
				+"inner join ta_page_menu_func c on a.EID=c.EID and a.page_menu_id=c.page_menu_id "
				+"inner join "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") d on a.EID=d.EID and a.SHOPID=d.SHOPID  "
				+"and d.lang_type='"+req.getLangType()+"' "
				+"where  a.EID='"+req.geteId()+"' ");

		if (PageType != null && PageType.length()>0)
		{
			sqlbuf.append(" and b.pagetype='"+PageType+"' ");
		}

		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append(" and a.SHOPID='"+oShopId+"' ");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append(" and (a.SHOPID like '"+KeyTxt+"%' or a.buttonname  like '"+KeyTxt+"%') ");
		}
		else
		{
			sqlbuf.append(" and  a.SHOPID in (select SHOPID  from platform_staffs_shop where EID='"+req.geteId()+"' and opno='"+req.getOpNO()+"'   )");			
		}
		sqlbuf.append(") ");
		sqlbuf.append(")");
		sqlbuf.append("where rn>"+startRow+" and rn<="+(startRow+pageSize)+"");
		sqlbuf.append(")");
		sqlbuf.append(")");

		sql=sqlbuf.toString();

		return sql;		
	}
	
	/**
	 * 查询记录数
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getCountSql(DCP_PageFunQueryReq req) throws Exception
	{
		String sql=null;
		String KeyTxt=req.getKeyTxt();
		String PageType=req.getPageType();
		String oShopId= req.getoShopId();

		if(KeyTxt==null) KeyTxt="";
		if(PageType==null) PageType="";
		if(oShopId==null) oShopId="";

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select num from "
				+"( "
				+"select count(*) num from  "
				+"( "
				+"select distinct a.page_menu_id as pageMenuID from ta_page_menu a " 
				+"inner join ta_page b on a.EID=b.EID and a.page_id=b.page_id " 
				+"inner join ta_page_menu_func c on a.EID=c.EID and a.page_menu_id=c.page_menu_id "
				+"inner join "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") d on a.EID=d.EID and a.SHOPID=d.SHOPID " 
				+"and d.lang_type='"+req.getLangType()+"' "
				+"where  a.EID='"+req.geteId()+"' ");

		if (PageType != null && PageType.length()>0)
		{
			sqlbuf.append(" and b.pagetype='"+PageType+"' ");
		}

		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append(" and a.SHOPID='"+oShopId+"' ");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append(" and (a.SHOPID like '"+KeyTxt+"%' or a.buttonname  like '"+KeyTxt+"%') ");
		}
		else
		{
			sqlbuf.append(" and  a.SHOPID in (select SHOPID  from platform_staffs_shop where EID='"+req.geteId()+"' and opno='"+req.getOpNO()+"'   )");			
		}

		sqlbuf.append(") ");
		sqlbuf.append(") ");


		sql=sqlbuf.toString();

		return sql;		
				
	}
	
	/**
	 * 查询功能列表
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getQuery_FuncSql(DCP_PageFunQueryReq req) throws Exception 
	{
		String sql=null;
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select funcNO,chsmsg from "
				+"( "
				+"select a.funcNO,a.chsmsg from DCP_MODULAR_function a  "
				+"inner join DCP_MODULAR b on a.EID=b.EID and a.modularno=b.modularno " 
				+"where a.EID='"+req.geteId()+"' "
				+"and b.stype='0' "
				+")");

		sql=sqlbuf.toString();

		return sql;		
	}

}
