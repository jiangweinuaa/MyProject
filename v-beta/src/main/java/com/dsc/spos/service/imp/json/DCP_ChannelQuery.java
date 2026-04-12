package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ChannelQueryReq;
import com.dsc.spos.json.cust.res.DCP_ChannelQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ChannelQuery extends SPosBasicService<DCP_ChannelQueryReq,DCP_ChannelQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_ChannelQueryReq req) throws Exception 
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
	protected TypeToken<DCP_ChannelQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ChannelQueryReq>() {};
	}

	@Override
	protected DCP_ChannelQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_ChannelQueryRes();
	}

	@Override
	protected DCP_ChannelQueryRes processJson(DCP_ChannelQueryReq req) throws Exception 
	{	
		DCP_ChannelQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		
		if (req.getPageNumber()==0)
		{
			req.setPageNumber(1);
		}
		
		if (req.getPageSize()==0)
		{
			req.setPageSize(1000);
		}

		String sql = this.getQuerySql(req);				
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_ChannelQueryRes.level1Elm>());

		if(getQData!=null && getQData.isEmpty()==false)
		{
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	


			for (Map<String, Object> map : getQData) 
			{				
				DCP_ChannelQueryRes.level1Elm lv1=res.new level1Elm();
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
				lv1.setApiUserCode(map.get("USERCODE").toString());
				lv1.setIsRegByMachine(map.get("ISREGBYMACHINE").toString());
                lv1.setApiUserKey(map.get("USERKEY").toString());

				// 2021/4/8 增加返回应用类型
				lv1.setAppType(map.get("APPTYPE").toString());
				
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
	protected String getQuerySql(DCP_ChannelQueryReq req) throws Exception 
	{
		String eId = req.geteId();

		String keyTxt = req.getRequest().getSearchString();//
		String status = req.getRequest().getStatus();//
		String channelId = req.getRequest().getChannelId();//
		String appno = req.getRequest().getAppNo();//
		String isThird = req.getRequest().getIsThird();//是否第三方   default 0,查非第3方渠道,不传仅查非第3方渠道 =1 查第3方渠道	=2,查全部渠道
		String onlyOnline = req.getRequest().getOnlyOnline();//2=仅查询线下 1=仅查线上0=查询全部
		String isRegByMachine = req.getRequest().getIsRegByMachine();

		// ADD 2020/6/11 增加检索条件appId，关联表CRM_CHANNEL.APPID
        String appId = req.getRequest().getAppId();

        int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		

		String sql = "";
		StringBuffer sqlBuff = new StringBuffer("select * from ("
				+ "select count(*) over() num, rownum rn,a.*,b.appname,b.isregbymachine,c.cardtypename,d.op_name CREATENAME,e.op_name LASTMODINAME,f.usercode,f.USERKEY "
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

		if(!Check.Null(appId)){
            sqlBuff.append(" AND a.APPID = '"+appId+"'");
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
		
		//1=仅查询三方 0=查询自有和三方渠道
		String onlyThird=req.getRequest().getOnlyThird();
		if(onlyThird!=null&&"1".equals(onlyThird)){
			sqlBuff.append(" AND a.APPTYPE <>'0' ");
		}


		sqlBuff.append( " order by a.CHANNELID ");

		sqlBuff.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlBuff.toString();
		return sql;
	}







}
