package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ScutKeyQueryReq;
import com.dsc.spos.json.cust.res.DCP_ScutKeyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：ScutKeyGetDCP
 *   說明：快捷键查询DCP
 * 服务说明：快捷键查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_ScutKeyQuery extends SPosBasicService<DCP_ScutKeyQueryReq,DCP_ScutKeyQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_ScutKeyQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_ScutKeyQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ScutKeyQueryReq>(){};
	}

	@Override
	protected DCP_ScutKeyQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ScutKeyQueryRes();
	}

	@Override
	protected DCP_ScutKeyQueryRes processJson(DCP_ScutKeyQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_ScutKeyQueryRes res = this.getResponse();	
		try
		{
			int totalRecords;								//总笔数
			int totalPages;									//总页数
      String langType = req.getLangType();

			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_ScutKeyQueryRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				//算總頁數
				String num = getQDataDetail.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_ScutKeyQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String funcNO = oneData.get("FUNCNO").toString();
					String funcName = oneData.get("CHSMSG").toString();
					if (langType.equals("zh_TW"))
					{
						funcName = oneData.get("CHTMSG").toString();
					}
					String keyScan = oneData.get("KEYSCAN").toString();					
				
					
					//设置响应
					oneLv1.setFuncNO(funcNO);
					oneLv1.setFuncName(funcName);
					oneLv1.setKeyScan(keyScan);
					oneLv1.setStatus("100");
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
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
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
	protected String getQuerySql(DCP_ScutKeyQueryReq req) throws Exception {

		String sql=null;			
		String eId = req.geteId();
		String docType = req.getDocType();   //0.POS功能键   1.门店管理
		String keyTxt = req.getKeyTxt();
		StringBuffer sqlbuf=new StringBuffer("");

		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append(""
				+ " select num,rn,nvl(a.funcno,a.MODULARNO) as funcno,nvl(a.chsmsg,modular_chsmsg)as chsmsg,nvl(a.chtmsg,modular_chtmsg)as chtmsg,c.keyscan from ("
				+ " select count(*) over() num,row_number() over (order by b.funcno,a.MODULARNO) rn,b.funcno,b.chsmsg,b.chtmsg,a.chsmsg as modular_chsmsg,a.chtmsg as modular_chtmsg,a.MODULARNO from DCP_MODULAR a "
				+ " left join  DCP_MODULAR_function b "
				+ " on a.EID=b.EID and a.modularno=b.modularno  and a.status='100' and b.status='100' and b.modularno='2701' " );

		if (Check.Null(docType))  //0.POS功能键+门店管理单据  
		{
			sqlbuf.append(" where a.EID='"+eId +"' and  ( b.modularno='2701' or a.uppermodular in ('1201','1202')) ");
		}
		else if (docType.equals("0"))  //只查询POS功能键
		{
			sqlbuf.append(" where a.EID='"+eId +"' and b.modularno='2701' ");
		}
		else if (docType.equals("1"))  //只查询门店管理
		{
			sqlbuf.append(" where a.EID='"+eId +"' and a.uppermodular in ('1201','1202') ");
		}

		if (!Check.Null(keyTxt))
		{
			sqlbuf.append( " and ( b.chsmsg like '%%"+keyTxt+"%%' or b.chtmsg like '%%"+keyTxt+"%%' or a.chsmsg  like '%%"+keyTxt+"%%' or  a.chtmsg like '%%"+keyTxt+"%%') ");
		}		
		sqlbuf.append(" ) a "
				+ " left join DCP_SHORTCUTKEY  c "
				+ " on c.funcno=a.funcno and c.status='100' and c.EID='"+eId +"'  "
				+ " where a.rn>" + startRow + " and a.rn<=" + (startRow+pageSize) );		
		
		sql = sqlbuf.toString();
		return sql;
	}

}


