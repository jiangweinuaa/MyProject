package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderMappingShopQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderMappingShopQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderMappingShopQuery_Open extends SPosBasicService<DCP_OrderMappingShopQuery_OpenReq,DCP_OrderMappingShopQuery_OpenRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderMappingShopQuery_OpenReq req) throws Exception
	{
		boolean isFail = false;
		if(req.getRequest()==null)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空！");
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderMappingShopQuery_OpenReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderMappingShopQuery_OpenReq>(){} ;
	}

	@Override
	protected DCP_OrderMappingShopQuery_OpenRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderMappingShopQuery_OpenRes();
	}

	@Override
	protected DCP_OrderMappingShopQuery_OpenRes processJson(DCP_OrderMappingShopQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		
		boolean isFail = false;
		if(req.getApiUser()==null)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "接口账号查询的渠道信息为空！");
		}
		
		
		StringBuffer errMsg = new StringBuffer("");
		String appType = req.getApiUser().getAppType();
		String channelId = req.getApiUser().getChannelId();
		if(Check.Null(appType)){
			errMsg.append("接口账号对应的渠道类型appType不可为空值, ");
			isFail = true;
		}
		if(Check.Null(channelId)){
			errMsg.append("接口账号对应的渠道编码channelId不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if (req.getPageNumber()==0)
		{
			req.setPageNumber(1);
		}
		
		if (req.getPageSize()==0)
		{
			req.setPageSize(100);						
		}
		
		DCP_OrderMappingShopQuery_OpenRes res = this.getResponse();
		DCP_OrderMappingShopQuery_OpenRes.levelDatas datas = res.new levelDatas();
		datas.setOrgList(new ArrayList<DCP_OrderMappingShopQuery_OpenRes.level1Elm>());
		String sql = getQuerySql(req);
		HelpTools.writelog_fileName("有赞门店映射查询sql:"+sql, "DCP_OrderMappingShopQuery_Open");
		
		int totalRecords;											//总笔数
        int totalPages;	
        
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false)
        {
        	 //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            
            for (Map<String, Object> map : getQData)
			{
            	DCP_OrderMappingShopQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();
            	
            	String erpShopNo = map.getOrDefault("SHOPID", "").toString();
            	String erpShopName = map.getOrDefault("ORG_NAME", "").toString();
            	String orderShopNo = map.getOrDefault("ORDERSHOPNO", "").toString();
            	String orderShopName = map.getOrDefault("ORDERSHOPNAME", "").toString();
            	String kdtId = map.getOrDefault("APPKEY", "").toString();
            	
            	oneLv1.setErpShopNo(erpShopNo);
            	oneLv1.setErpShopName(erpShopName);
            	oneLv1.setOrderShopNo(orderShopNo);
            	oneLv1.setOrderShopName(orderShopName);
            	oneLv1.setKdtId(kdtId);
            	
            	datas.getOrgList().add(oneLv1);
            	
			}
        }
        else 
        {
        	 totalRecords = 0;
             totalPages = 0;
		}
        res.setDatas(datas);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderMappingShopQuery_OpenReq req) throws Exception
	{
		String appType = req.getApiUser().getAppType();
		String channelId = req.getApiUser().getChannelId();
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		boolean isPage = true;
		String[] erpShopNo  = req.getRequest().getErpShopNo();
		String[] orderShopNo  = req.getRequest().getOrderShopNo();
				
		 String erpShopNos = getString(erpShopNo);
		 String orderShopNos = getString(orderShopNo);
		 
		 String keyTxt = req.getRequest().getKeyTxt();
		 
		int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; 
        
        int endRow = startRow + pageSize;
		
		StringBuffer sqlBuffer = new StringBuffer("");
		
		sqlBuffer.append("");
		sqlBuffer.append("");
		
		sqlBuffer.append(" select * from (");
		
		sqlBuffer.append(" select count(*) over() num,rownum rn,APPKEY,SHOPID,ORG_NAME,ORDERSHOPNO,ORDERSHOPNAME from (");
		sqlBuffer.append(" select a.*,b.org_name from  DCP_MAPPINGSHOP a left join dcp_org_lang b on a.eid = b.eid and a.shopid=b.organizationno and b.lang_type='"+langType+"'");
		sqlBuffer.append(" where a.eid='"+eId+"' and a.LOAD_DOCTYPE='"+appType+"' ");
		if(!Check.Null(erpShopNos))
		{
			isPage = false;
			sqlBuffer.append(" and a.SHOPID in ("+erpShopNos+")");
		}
		if(!Check.Null(orderShopNos))
		{
			isPage = false;
			sqlBuffer.append(" and a.ORDERSHOPNO in ("+orderShopNos+")");
		}
		
		if(!Check.Null(keyTxt))
		{
			isPage = false;
			sqlBuffer.append(" and (a.SHOPID like '%%"+keyTxt+"%%' or a.ORDERSHOPNO like '%%"+keyTxt+"%%')");
		}
		
		sqlBuffer.append(" order by a.shopid");
		sqlBuffer.append(" )");
		
		sqlBuffer.append(" ) ");
		if(isPage)
		{
			sqlBuffer.append(" where rn>"+startRow+" and rn<="+endRow);
		}
		
		String sql = sqlBuffer.toString();
		
		return sql;
	}
	
	private String getString(String[] str) {
        StringBuffer str2 = new StringBuffer();
        if (str!=null && str.length>0)
        {
            for (String s:str) {
                str2.append("'").append(s).append("'").append(",");
            }
            if (str2.length()>0) {
                str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
            }
        }
        return str2.toString();
    }

}
