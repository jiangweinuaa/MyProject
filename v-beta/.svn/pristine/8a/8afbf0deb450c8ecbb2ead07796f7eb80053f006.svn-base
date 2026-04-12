package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ChannelQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ChannelQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ChannelQuery_Open extends SPosBasicService<DCP_ChannelQuery_OpenReq,DCP_ChannelQuery_OpenRes>
{

	@Override
	protected boolean isVerifyFail(DCP_ChannelQuery_OpenReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ChannelQuery_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ChannelQuery_OpenReq>() {};
	}

	@Override
	protected DCP_ChannelQuery_OpenRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_ChannelQuery_OpenRes();
	}

	@Override
	protected DCP_ChannelQuery_OpenRes processJson(DCP_ChannelQuery_OpenReq req) throws Exception 
	{	
		DCP_ChannelQuery_OpenRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		
		String sql = this.getQuerySql(req);				
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_ChannelQuery_OpenRes.level1Elm>());

		//CRM_APIUSER一个渠道有多个帐号，武小凤说3.0不支持，过滤掉
        Map<String, Boolean> condv=new HashMap<>();
        condv.put("EID",true);
        condv.put("CHANNELID",true);
        //过滤
        List<Map<String, Object>> getHeaderChanel= MapDistinct.getMap(getQData,condv);
		if(getHeaderChanel!=null && getHeaderChanel.isEmpty()==false)
		{
			totalRecords=getHeaderChanel.size();

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			
			for (Map<String, Object> map : getHeaderChanel)
			{				
				DCP_ChannelQuery_OpenRes.level1Elm lv1=res.new level1Elm();
				lv1.setChannelId(map.get("CHANNELID").toString());
				lv1.setChannelName(map.get("CHANNELNAME").toString());				
				lv1.setStatus(map.get("STATUS").toString());
				lv1.setAppName(map.get("APPNAME").toString());
				lv1.setAppNo(map.get("APPNO").toString());
				lv1.setCardTypeId(map.get("CARDTYPEID").toString());
				lv1.setCardTypeName(map.get("CARDTYPENAME").toString());
				lv1.setCreateOpId(map.get("CREATEOPID").toString());
				lv1.setCreateOpName(map.get("CREATENAME").toString());
				lv1.setCreateTime(map.get("CREATETIME").toString());
				lv1.setLastModiOpId(map.get("LASTMODIOPID").toString());
				lv1.setLastModiOpName(map.get("LASTMODINAME").toString());
				lv1.setLastModiTime(map.get("LASTMODITIME").toString());
				lv1.setIsRegByMachine(map.get("ISREGBYMACHINE").toString());

				res.getDatas().add(lv1);
				lv1=null;
			}
		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");


		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_ChannelQuery_OpenReq req) throws Exception 
	{
		String eId = req.geteId();

		String keyTxt = req.getRequest().getSearchString();//
		String status = req.getRequest().getStatus();//
		String channelId = req.getRequest().getChannelId();//
		String appno = req.getRequest().getAppNo();//
		String isThird = req.getRequest().getIsThird();//是否第三方   default 0,查非第3方渠道,不传仅查非第3方渠道 =1 查第3方渠道	=2,查全部渠道
		String onlyOnline = req.getRequest().getOnlyOnline();//2=仅查询线下 1=仅查线上0=查询全部
		String isRegByMachine = req.getRequest().getIsRegByMachine();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		

		String sql = "";
		StringBuffer sqlBuff = new StringBuffer("select * from ("
				+ "select count(*) over() num, rownum rn,a.*,b.appname,b.isregbymachine,c.cardtypename,d.op_name CREATENAME,e.op_name LASTMODINAME,f.usercode "
				+ "from CRM_CHANNEL a "
				+ "inner join platform_app b on a.appno=b.appno "
				+ "left join crm_cardtype c on a.eid=c.eid and a.cardtypeid=c.cardtypeid "
				+ "left join platform_staffs_lang d on a.eid=d.eid and a.createopid=d.opno and d.lang_type='"+req.getLangType()+"' "
				+ "left join platform_staffs_lang e on a.eid=e.eid and a.lastmodiopid=e.opno and e.lang_type='"+req.getLangType()+"' "
				+ "left join CRM_APIUSER f on a.eid=f.eid and a.channelid=f.channelid and a.appno=f.apptype "
				+ "where a.eid='"+eId+"' ");

		if (keyTxt != null && keyTxt.length()>0) 
		{ 	
			sqlBuff.append( " AND (a.CHANNELID like '%%"+keyTxt+"%%' or a.CHANNELNAME like '%%"+keyTxt+"%%' ) ");
		}

		if (channelId != null && channelId.length()>0) 
		{ 	
			sqlBuff.append( " AND a.CHANNELID = '"+channelId+"' ");
		}

		if (status != null && status.length()>0) 
		{ 	
			sqlBuff.append( " AND a.status = "+status+" ");
		}
		
		if (isThird != null && isThird.length()>0) 
		{ 	
			if (isThird.equals("0"))
			{
				sqlBuff.append( " AND b.isthird ='N' ");
			}
		
			if (isThird.equals("1"))
			{
				sqlBuff.append( " AND b.isthird ='Y' ");
			}		
		}
		
		if (isRegByMachine != null && isRegByMachine.length()>0) 
		{
			sqlBuff.append( " AND b.ISREGBYMACHINE ='"+isRegByMachine+"' ");
		}
		
		if (appno != null && appno.length()>0) 
		{ 	
			sqlBuff.append( " AND a.appno = '"+appno+"' ");
		}
		
		if (onlyOnline != null && onlyOnline.length()>0) 
		{ 	
			if (onlyOnline.equals("1"))
			{
				sqlBuff.append( " AND b.isonline ='Y' ");
			}		
			if (onlyOnline.equals("2"))
			{
				sqlBuff.append( " AND b.isonline ='N' ");
			}	
		}

		sqlBuff.append( " order by a.CHANNELID ");

		sqlBuff.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlBuff.toString();
		return sql;
	}






}
