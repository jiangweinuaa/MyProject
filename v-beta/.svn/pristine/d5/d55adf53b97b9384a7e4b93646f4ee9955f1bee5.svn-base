package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_GoodsImageQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsImageQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsImageQuery extends SPosBasicService<DCP_GoodsImageQueryReq,DCP_GoodsImageQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_GoodsImageQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String appType=req.getRequest().getAppType();
		
		if(Check.Null(appType)) {
			isFail = true;
			errMsg.append("应用类型不能为空 ");
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_GoodsImageQueryReq> getRequestType() {
		return new TypeToken<DCP_GoodsImageQueryReq>() {};
	}
	
	@Override
	protected DCP_GoodsImageQueryRes getResponseType() {
		return new DCP_GoodsImageQueryRes();
	}
	
	@Override
	protected DCP_GoodsImageQueryRes processJson(DCP_GoodsImageQueryReq req) throws Exception {
		
		DCP_GoodsImageQueryRes res=this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;
		
		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		
		res.setDatas(new ArrayList<DCP_GoodsImageQueryRes.level1Elm>());
		
		if (getData!=null && getData.isEmpty()==false) {
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
			String httpStr=ISHTTPS.equals("1")?"https://":"http://";
			String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
			
			for (Map<String, Object> oneData : getData) {
				DCP_GoodsImageQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setListImage(oneData.get("LISTIMAGE").toString());
				if (DomainName.endsWith("/")) {
					lv1.setListImageUrl(httpStr+DomainName+"resource/image/" +oneData.get("LISTIMAGE").toString());
				} else {
					lv1.setListImageUrl(httpStr+DomainName+"/resource/image/"+oneData.get("LISTIMAGE").toString());
				}
				
				lv1.setPluName(oneData.get("PLUNAME").toString());
				lv1.setPluNo(oneData.get("PLUNO").toString());
				lv1.setPluType(oneData.get("PLUTYPE").toString());
				lv1.setSelfBuiltShopId(oneData.get("SELFBUILTSHOPID").toString());
				
				res.getDatas().add(lv1);
				lv1=null;
			}
		}
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_GoodsImageQueryReq req) throws Exception {
		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		String appType = req.getRequest().getAppType();
		StringBuffer sqlbuf=new StringBuffer();
		//searchScope：0、全部 1、总部和当前自建门店 2、仅总部 3、全部自建门店 4、仅当前自建门店  by jinzma 20220310
		String searchScope = req.getRequest().getSearchScope();
		String selfBuiltShopId = req.getRequest().getSelfBuiltShopId(); //自建门店
		if (Check.Null(searchScope)){
			searchScope="0";
		}
		
		//計算起啟位置
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append("select * from ( "
				+ " select count(*) over() num,row_number() over (order by selfbuiltshopid desc,pluno) rn,"
				+ " pluno,pluname,plutype,listimage,apptype,selfbuiltshopid from "
				+ " ("
				+ " select c.pluno,b.plu_name pluname,c.plutype,c.listimage,c.apptype,a.selfBuiltShopId "
				+ " from dcp_goodsimage c "
				+ " left join dcp_goods a on a.eid=c.eid and a.pluno=c.pluno "
				+ " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langtype+"' "
				+ " where c.eid='"+eId+"' and c.plutype in ('FEATURE','NORMAL','PACKAGE','GOODS') ");
		
		if(ketTxt != null && ketTxt.length() >0) {
			sqlbuf.append(" and (c.pluno like '%%"+ketTxt+"%%' or b.plu_name like '%%"+ketTxt+"%%') ");
		}
		
		if(appType != null && appType.length() >0) {
			sqlbuf.append(" and c.apptype = '"+appType+"' ");
		}
		
		//searchScope by jinzma 20220310
		switch (searchScope){
			case "0":    //0、全部
				break;
			case "1":    //1、总部和当前自建门店
				sqlbuf.append(" and (a.selfBuiltShopId is null or a.selfBuiltShopId= '"+selfBuiltShopId+"')");
				break;
			case "2":    //2、仅总部
				sqlbuf.append(" and a.selfBuiltShopId is null");
				break;
			case "3":    //3、全部自建门店
				sqlbuf.append(" and a.selfBuiltShopId is not null");
				break;
			case "4":    //4、仅当前自建门店
				sqlbuf.append(" and a.selfBuiltShopId= '"+selfBuiltShopId+"'");
				break;
		}
		
		sqlbuf.append(" union all "
				+ " select c.pluno PLUNO,b.masterpluname PLUNAME,c.plutype PLUTYPE,c.listimage,c.apptype,N'' as selfBuiltShopId"
				+ " from dcp_goodsimage c "
				+ " left join dcp_mspecgoods a on a.eid=c.eid and a.masterpluno=c.pluno "
				+ " left join dcp_mspecgoods_lang b on a.eid=b.eid and a.masterpluno=b.masterpluno and b.lang_type='"+langtype+"' "
				+ " where c.eid='"+eId+"' and c.plutype='MULTISPEC' ");
		
		if(ketTxt != null && ketTxt.length() >0) {
			sqlbuf.append(" and (c.pluno like '%%"+ketTxt+"%%' or b.masterpluname like '%%"+ketTxt+"%%') ");
		}
		
		if(appType != null && appType.length() >0) {
			sqlbuf.append(" and c.apptype= '"+appType+"' ");
		}
		
		//自建商品查询不返回多规格商品  by jinzma 20220302
		if (searchScope.equals("3")||searchScope.equals("4")){
			sqlbuf.append(" and 1<>1 ");
		}
		
		sqlbuf.append(" )"
				+ " ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		return sqlbuf.toString();
	}
	
}
