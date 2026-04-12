package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ReasonMsgQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReasonMsgQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_ReasonMsgQuery extends SPosBasicService<DCP_ReasonMsgQueryReq, DCP_ReasonMsgQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ReasonMsgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ReasonMsgQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ReasonMsgQueryReq>(){};
	}

	@Override
	protected DCP_ReasonMsgQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ReasonMsgQueryRes();
	}

	@Override
	protected DCP_ReasonMsgQueryRes processJson(DCP_ReasonMsgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		 
		String sql = null;
		DCP_ReasonMsgQueryRes res = this.getResponse();
		String langType= req.getLangType();
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		
		//查询原因码信息
		sql = this.getQuerySql(req);
		
		String[] conditionValues1 = { }; //查詢條件
		
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			String num = getQDataDetail.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("BSNO", true);
			condition.put("BSTYPE", true);
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			
			res.setDatas(new ArrayList<DCP_ReasonMsgQueryRes.level1Elm>());
			                   
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_ReasonMsgQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setDatas(new ArrayList<DCP_ReasonMsgQueryRes.level2Elm>());
				// 取出第一层
				String BSNO = oneData.get("BSNO").toString();
				String BSType = oneData.get("BSTYPE").toString();
				String BSName = oneData.get("BSNAME").toString();
				String status = oneData.get("STATUS").toString();
				oneLv1.setBsNo(BSNO);
				oneLv1.setBsType(BSType);
				oneLv1.setBsName(BSName);
				oneLv1.setStatus(status);
				
				for (Map<String, Object> oneData2 : getQDataDetail) 
				{
					//过滤属于此单头的明细
					if( (BSNO.equals(oneData2.get("BSNO")) && BSType.equals(oneData2.get("BSTYPE")) ) == false )
						continue;
						
					DCP_ReasonMsgQueryRes.level2Elm oneLv2 = res.new level2Elm();

					String LBSNO = oneData2.get("LBSNO").toString();
					
					// 如果 多语言信息为空 ( 从ERP传下来的值可能为空  ) ，默认 加一条当前登陆用户所选择的多语言类型 
					if(LBSNO==null || LBSNO.length() == 0){
						oneLv2.setBsNo(BSNO);
						oneLv2.setBsType(BSType);
						oneLv2.setBsName(BSName);
						oneLv2.setStatus(status);
						oneLv2.setLangType(langType);
					}
					else{
						String LBSType = oneData2.get("LBSTYPE").toString();
						String LBSName = oneData2.get("LBSNAME").toString();
						String Lstatus = oneData2.get("LSTATUS").toString();
						String LlangType = oneData2.get("LANGTYPE").toString();
						
						oneLv2.setBsNo(LBSNO);
						oneLv2.setBsType(LBSType);
						oneLv2.setBsName(LBSName);
						oneLv2.setStatus(Lstatus);
						oneLv2.setLangType(LlangType);
					}
					
					oneLv1.getDatas().add(oneLv2);
					oneLv2=null;
				}
				res.getDatas().add(oneLv1);
				oneLv1=null;
			}
		}
		else
		{
			res.setDatas(new ArrayList<DCP_ReasonMsgQueryRes.level1Elm>());
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ReasonMsgQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String sql=null;
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
    
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String status = req.getRequest().getStatus();
		String BSNO = req.getRequest().getBsNo();
		String BSType = req.getRequest().getBsType();
		String BSName = req.getRequest().getBsName();
		if(status == null) status="";
		if(BSNO == null) BSNO="";
		if(BSType == null) BSType="";
		if(BSName == null) BSName="";
		
		String eId = req.geteId();
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( "SELECT * FROM ( "
				+ "SELECT COUNT(*) OVER() NUM, ROW_NUMber() OVER(ORDER BY BSNO)  rn , BSNO , BSName ,BSType , status , "
				+ " LBSNO, LBSName , langType ,  LBSType , Lstatus , EID  FROM (  "
				+ " SELECT a.EID ,"
				+ " a.bsno AS BSNO , a.bsname AS BSName , a.bstype AS BSType ,a.status AS status , "
				+ " b.bsno AS LBSNO, b.reason_Name AS LBSName , b.lang_Type AS langType , b.BSType AS LBSType ,"
				+ " b.status AS Lstatus  "
				+ " FROM DCP_REASON a  LEFT JOIN DCP_REASON_lang b ON a.BSNO = b.BSNO AND a.EID = b.EID and a.BSType = b.BSType  "
				+ "   )"
				+ " WHERE EID = '"+eId+"'"); 
		if(status!=null && status.length()>0)
  		{
  			sqlbuf.append("and status='" + status + "' ");
  		}
    	if (BSNO != null && BSNO.length()>0)
 	    {
    		sqlbuf.append("AND BSNO like '%%"+BSNO+"%%' ");
 	    }
    	if (BSName != null && BSName.length()>0)
 	    {
    		sqlbuf.append("AND BSName like '%%"+BSName+"%%' ");
 	    }
    	if(BSType!=null && BSType.length()>0)
  		{
  			sqlbuf.append("AND BSType='" + BSType + "' ");
  		}   
				
    	sqlbuf.append( " ) where  rn > "+startRow+" AND rn <="+(startRow+pageSize)+" "
				+ " order by BSNO ");
		
		sql=sqlbuf.toString();
		return sql;
	}
	

	
	
}
