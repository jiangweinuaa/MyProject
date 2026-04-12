package com.dsc.spos.service.imp.json;

import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_NoticeQueryReq;
import com.dsc.spos.json.cust.res.DCP_NoticeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_NoticeQuery extends SPosBasicService<DCP_NoticeQueryReq,DCP_NoticeQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_NoticeQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		
		if (req.getRequest().getTimeFrame()==null) 
		{
			errCt++;
			errMsg.append("时间范围不能为Null, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	
		return false;
	}

	@Override
	protected TypeToken<DCP_NoticeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_NoticeQueryReq>(){};
	}

	@Override
	protected DCP_NoticeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_NoticeQueryRes();
	}

	@Override
	protected DCP_NoticeQueryRes processJson(DCP_NoticeQueryReq req) throws Exception 
	{	
		String sql=null;
		//查詢資料
		DCP_NoticeQueryRes res = null;
		res = this.getResponse();
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
		String totQty="0";
		String unReadQty="0";

		//公告总数
		sql = this.getNoticeCountSql(req);				

		String[] condCountValuesX1 = { }; //查詢條件
		List<Map<String, Object>> getQData_CountX1 = this.doQueryData(sql, condCountValuesX1);
		if (getQData_CountX1 != null && getQData_CountX1.isEmpty() == false) 
		{
			Map<String, Object> oneData_CountX1 = getQData_CountX1.get(0);
			totQty = oneData_CountX1.get("NUM").toString();
		}

		//未读总数
		sql = this.getNoticeNoReadCountSql(req);

		String[] condCountValuesX2 = { }; //查詢條件
		List<Map<String, Object>> getQData_CountX2 = this.doQueryData(sql, condCountValuesX2);
		if (getQData_CountX2 != null && getQData_CountX2.isEmpty() == false) 
		{
			Map<String, Object> oneData_CountX2 = getQData_CountX2.get(0);
			unReadQty = oneData_CountX2.get("NUM").toString();
		}

		sql=this.getQuerySql(req);

		String[] conditionValues1 = {}; //查詢條件
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("NOTICEID", true);		
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			res.setDatas(new ArrayList<DCP_NoticeQueryRes.level1Elm>());
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_NoticeQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setDatas(new ArrayList<DCP_NoticeQueryRes.level2Elm>());
				// 取出第一层
				String noticeID = oneData.get("NOTICEID").toString();

				String type = oneData.get("TYPE").toString();
				String title = oneData.get("TITLE").toString();
				
				//Blob类型读取
				Blob blob =(Blob) oneData.get("CONTENT");
				
				InputStream input= blob.getBinaryStream();
				StringBuffer out = new StringBuffer();
				byte[] b = new byte[4096];
				for (int n; (n = input.read(b)) != -1;) 
				{
					out.append(new String(b, 0, n));
				}
				String content =out.toString();				
	    	
				String status = oneData.get("STATUS").toString();
				String issueDate = oneData.get("ISSUEDATE").toString();
				String issueByName = oneData.get("ISSUEBYNAME").toString();

				String isRead = oneData.get("ISREAD").toString();
				String isFile = oneData.get("ISFILE").toString();
				//设置响应
				oneLv1.setContent(content);
				oneLv1.setIsFile(isFile);
				oneLv1.setIsRead(isRead);
				oneLv1.setIssueByName(issueByName);
				oneLv1.setIssueDate(issueDate);
				oneLv1.setNoticeID(noticeID);
				oneLv1.setStatus(status);
				oneLv1.setTitle(title);				
				oneLv1.setType(type);
				//oneLv1.setTotQty(totQty);
				//oneLv1.setUnReadQty(unReadQty);

				for (Map<String, Object> oneData2 : getQDataDetail) 
				{
					//过滤属于此单头的明细
					if(noticeID.equals(oneData2.get("NOTICEID"))==false) continue;

					String fileName = oneData2.get("FILENAME").toString();
					String filePath = oneData2.get("FILEPATH").toString();

					if (fileName.equals("")) continue;

					DCP_NoticeQueryRes.level2Elm oneLv2 = res.new level2Elm();

					oneLv2.setFileName(fileName);
					oneLv2.setFilePath(filePath);

					//添加
					oneLv1.getDatas().add(oneLv2);
					oneLv2 = null;
				}

				//添加
				res.getDatas().add(oneLv1);		
				
				oneLv1 = null;
			}
		}
		else
		{
			res.setDatas(new ArrayList<DCP_NoticeQueryRes.level1Elm>());			
		}
		
		res.setTotQty(totQty);
		res.setUnReadQty(unReadQty);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_NoticeQueryReq req) throws Exception 
	{	
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		String KeyTxt=req.getRequest().getKeyTxt();
		String TimeFrame=req.getRequest().getTimeFrame();
		String Status= req.getRequest().getStatus();
		String ReadStatus= req.getRequest().getReadStatus();

		if(KeyTxt==null) KeyTxt="";
		if(TimeFrame==null) TimeFrame="";
		if(Status==null) Status="";
		if(ReadStatus==null) ReadStatus="";

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select noticeID,type,title,content,status,issueDate,issueByName,fileName,filePath,isRead,isFile from "
				+ "("
				+ "SELECT a.notice_id noticeID,a.type,a.title,a.content,a.status,a.issue_date as issueDate,(case WHEN (a.issueby IS NULL) then cast('' as nvarchar2(120)) ELSE b.op_name end) as issueByName,c.file_name as fileName,c.file_path as filePath,(case WHEN d.opno IS NULL  then 'N' ELSE 'Y' end) AS isRead,(case WHEN c.file_name IS NULL  then 'N' ELSE 'Y' end) AS isFile "
				+ "FROM TA_NOTICE a "
				+ "inner join platform_staffs_lang b on a.EID=b.EID and (a.issueby=b.opno or (a.issueby is null and b.opno='"+req.getOpNO()+"'))  and b.lang_type='"+req.getLangType()+"' "
				+ "LEFT JOIN TA_NOTICE_FILE c  on  a.EID=c.EID and a.notice_id=c.notice_id "
				+ "left join ta_NOTICE_READBY d on a.EID=d.EID and a.notice_id=d.notice_id "
				+ "WHERE a.EID='"+req.geteId()+"'  ");

		if (TimeFrame != null && TimeFrame.length()>0)
		{
			sqlbuf.append("and  a.create_date>='"+TimeFrame+"' ");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append("and (a.type like '%"+KeyTxt+"%' or a.title like '%"+KeyTxt+"%' ) ");
		}

		if (Status != null && Status.length()>0)
		{
			sqlbuf.append( "and a.status='"+Status+"' ");
		}

		if (ReadStatus != null && ReadStatus.length()>0)
		{
			if(ReadStatus.equals("1"))
			{
				sqlbuf.append( "and a.notice_id in "
						+ "(select notice_id from ta_notice_readby "
						+ "where EID='"+req.geteId()+"' "
						+ "and opno='"+req.getOpNO()+"'"
						+ ") ");
			}
			else
			{
				sqlbuf.append( "and a.notice_id not in "
						+ "(select notice_id from ta_notice_readby "
						+ "where EID='"+req.geteId()+"' "
						+ "and opno='"+req.getOpNO()+"'"
						+ ") ");

			}
		}


		sqlbuf.append( "and a.notice_id in"
				+ "("
				+ "select cc.NOTICE_ID from "
				+ "(select rn,NOTICE_ID from "
				+ "(select NOTICE_ID,rownum rn from TA_NOTICE a "
				+ "inner join platform_staffs_lang b on a.EID=b.EID and (a.issueby=b.opno or (a.issueby is null and b.opno='"+req.getOpNO()+"'))  and b.lang_type='"+req.getLangType()+"' "
				+ "WHERE a.EID='"+req.geteId()+"'  ");

		if (TimeFrame != null && TimeFrame.length()>0)
		{
			sqlbuf.append( "and  a.create_date>='"+TimeFrame+"' ");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append( "and (a.type like '%"+KeyTxt+"%' or a.title like '%"+KeyTxt+"%' ) ");
		}

		if (Status != null && Status.length()>0)
		{
			sqlbuf.append( "and a.status='"+Status+"' ");
		}

		if (ReadStatus != null && ReadStatus.length()>0)
		{
			if(ReadStatus.equals("1"))
			{
				sqlbuf.append( "and a.notice_id in "
						+ "("
						+ "select notice_id from ta_notice_readby "
						+ "where EID='"+req.geteId()+"' "
						+ "and opno='"+req.getOpNO()+"'"
						+ ")");
			}
			else
			{				
				sqlbuf.append( "and a.notice_id not in "
						+ "("
						+ "select notice_id from ta_notice_readby "
						+ "where EID='"+req.geteId()+"' "
						+ "and opno='"+req.getOpNO()+"'"
						+ ")");
			}
		}

		sqlbuf.append( ") "
				+ ") cc where rn>"+startRow+" and rn<="+(startRow+pageSize)+""
				+ ")");

		sqlbuf.append( ")");	


		sql=sqlbuf.toString();

		return sql;
	}
	
	/*
	 * 查询总数
	 */
	protected String getCountSql(DCP_NoticeQueryReq req) throws Exception
	{
		String sql=null;
		String KeyTxt=req.getRequest().getKeyTxt();
		String TimeFrame=req.getRequest().getTimeFrame();
		String Status= req.getRequest().getStatus();
		String ReadStatus= req.getRequest().getReadStatus();

		if(KeyTxt==null) KeyTxt="";
		if(TimeFrame==null) TimeFrame="";
		if(Status==null) Status="";
		if(ReadStatus==null) ReadStatus="";

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select num from( "
				+ "SELECT count(*) as num FROM TA_NOTICE a "
				+ "inner join platform_staffs_lang b on a.EID=b.EID and (a.issueby=b.opno or (a.issueby is null and b.opno='"+req.getOpNO()+"')) "
				+ "and b.lang_type='"+req.getLangType()+"' "
				+ "WHERE a.EID='"+req.geteId()+"'  ");

		if (TimeFrame != null && TimeFrame.length()>0)
		{
			sqlbuf.append("and  a.create_date>='"+TimeFrame+"' ");
		}

		if (KeyTxt != null && KeyTxt.length()>0)
		{
			sqlbuf.append("and (a.type like '%"+KeyTxt+"%' or a.title like '%"+KeyTxt+"%' ) ");
		}

		if (Status != null && Status.length()>0)
		{
			sqlbuf.append("and a.status='"+Status+"' ");
		}

		if (ReadStatus != null && ReadStatus.length()>0)
		{
			if(ReadStatus.equals("1"))
			{
				sqlbuf.append("and a.notice_id in "
						+ "("
						+ "select notice_id from ta_notice_readby "
						+ "where EID='"+req.geteId()+"' "
						+ "and opno='"+req.getOpNO()+"'"
						+ ")");
			}
			else
			{
				sqlbuf.append("and a.notice_id not in "
						+ "("
						+ "select notice_id from ta_notice_readby "
						+ "where EID='"+req.geteId()+"' "
						+ "and opno='"+req.getOpNO()+"'"
						+ ")");

			}
		}

		sqlbuf.append(")");


		sql=sqlbuf.toString();

		return sql;
		
	}

	
	/*
	 * 公告总数
	 */
	protected String getNoticeCountSql(DCP_NoticeQueryReq req) throws Exception
	{
		String sql=null;
		StringBuffer sqlbuf=new StringBuffer("");
		
		sqlbuf.append("select num from "
			+ "(select count(*) as num from ta_notice "
			+ "where EID='"+req.geteId()+"' "
			+ "and status='1'"
			+ ")");
		
		sql=sqlbuf.toString();

		return sql;
	}
	
	
	/*
	 * 未阅读的
	 */
	protected String getNoticeNoReadCountSql(DCP_NoticeQueryReq req) throws Exception
	{
		String sql=null;
		StringBuffer sqlbuf=new StringBuffer("");
		
		sqlbuf.append("select num from "
			+ "("
			+ "select count(*) num from ta_notice "
			+ "where EID='"+req.geteId()+"' "
			+ "and status='1' "
			+ "and notice_id not in "
			+ "("
			+ "select notice_id from ta_notice_readby "
			+ "where EID='"+req.geteId()+"' "
			+ "and opno='"+req.getOpNO()+"'"
			+ ")"
			+ ")");
		
		sql=sqlbuf.toString();

		return sql;
	}
	
	
	
	
}
