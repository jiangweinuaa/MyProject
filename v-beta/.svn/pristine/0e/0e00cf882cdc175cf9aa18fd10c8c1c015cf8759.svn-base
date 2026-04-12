package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_FuncQueryReq;
import com.dsc.spos.json.cust.res.DCP_FuncQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_FuncGet
 * 服务说明：基本功能查询
 * @author jinzma 
 * @since  2019-12-17
 */
public class DCP_FuncQuery extends SPosBasicService<DCP_FuncQueryReq,DCP_FuncQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_FuncQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_FuncQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_FuncQueryReq>(){};
	}

	@Override
	protected DCP_FuncQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_FuncQueryRes() ;
	}

	@Override
	protected DCP_FuncQueryRes processJson(DCP_FuncQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
    String langType=req.getLangType();
		DCP_FuncQueryRes res =  this.getResponse();
		try
		{
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			res.setDatas(new ArrayList<DCP_FuncQueryRes.level1Elm>());

			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				for (Map<String, Object> oneData : getQData) 
				{
					DCP_FuncQueryRes.level1Elm oneLv1 = res.new level1Elm(); 
					String funcNo = oneData.get("FUNCNO").toString();
					String funcName = oneData.get("CHSMSG").toString();
					if (langType.equals("zh_TW")) funcName = oneData.get("CHTMSG").toString();
					String funcType = oneData.get("FUNCTYPE").toString();
					String approve = oneData.get("APPROVE").toString();

					oneLv1.setApprove(approve);
					oneLv1.setFuncName(funcName);
					oneLv1.setFuncNo(funcNo);
					oneLv1.setFuncType(funcType);
					
					res.getDatas().add(oneLv1);
				}
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
	protected String getQuerySql(DCP_FuncQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		DCP_FuncQueryReq.level1Elm level1Elm = req.getRequest();
		String keyTxt = level1Elm.getKeyTxt();
		String funcType = level1Elm.getFuncType();
		String approve = level1Elm.getApprove();
		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append(" select num,rn,nvl(a.funcno,a.MODULARNO) as funcno,nvl(a.chsmsg,modular_chsmsg)as chsmsg, "
				+ " nvl(a.chtmsg,modular_chtmsg)as chtmsg,nvl(approve,'1') as approve,funcType from ( "
				+ " select count(*) over() num,row_number() over (order by b.funcno,a.MODULARNO) rn,b.funcno,"
				+ " b.chsmsg,b.chtmsg,a.chsmsg as modular_chsmsg,a.chtmsg as modular_chtmsg,a.MODULARNO, "
				+ " nvl(c.approve,d.approve) as approve,case when b.funcno is null  then 'dcp' else 'pos' end as funcType from DCP_MODULAR a "
				+ " left join DCP_MODULAR_function b "
				+ " on a.EID=b.EID and a.modularno=b.modularno  and a.status='100' and b.status='100' and b.modularno='2701' "
				+ " left join dcp_dingfunc c on c.eid='"+eId+"' and c.funcno=b.funcno and c.functype='pos' "
				+ " left join dcp_dingfunc d on d.eid='"+eId+"' and d.funcno=a.MODULARNO and d.functype='dcp' "
				+ " where a.EID='"+eId+"' " );

		if (Check.Null(funcType))
		{
//			sqlbuf.append(" and (b.modularno='2701' or a.uppermodular in ('1201','1202')) ");
			sqlbuf.append(" and (b.modularno='2701' or a.modularno='120109' ) ");
		}
		else if (funcType.equals("pos"))
		{
			sqlbuf.append(" and b.modularno='2701' ");
		}
		else if (funcType.equals("dcp"))
		{
			//sqlbuf.append(" and a.uppermodular in ('1201','1202') ");
			sqlbuf.append(" and a.modularno='120109' ");
		}

		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.MODULARNO like '%%"+keyTxt+"%%' or b.funcno like '%%"+keyTxt+"%%' or "
				+ " b.chsmsg like '%%"+keyTxt+"%%' or b.chtmsg like '%%"+keyTxt+"%%' or a.chsmsg like '%%"+keyTxt+"%%' or a.chtmsg like '%%"+keyTxt+"%%') ");  
		}

		if (!Check.Null(approve)&&approve.equals("0"))  //支持钉钉审批
		{
			sqlbuf.append(" and (c.approve='0' or d.approve='0') ");
		}
		if (!Check.Null(approve)&&approve.equals("1"))  //不支持钉钉审批
		{
			sqlbuf.append(" and (c.approve='1' or c.approve is null) and (d.approve='1' or d.approve is null) ");
		}
		sqlbuf.append(" )a 	 where a.rn>" + startRow + " and a.rn<=" + (startRow+pageSize) );		
		sql = sqlbuf.toString();
		return sql;
	}

}
