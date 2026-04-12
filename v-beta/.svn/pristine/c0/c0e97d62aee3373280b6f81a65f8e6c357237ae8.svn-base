package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StockOutTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_StockOutTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_StockOutTemplateDetailGet
 *    說明：调拨模板查询
 * 服务说明：调拨模板查询
 * @author 袁云洋 
 * @since  2019-12-18
 */
public class DCP_StockOutTemplateDetail extends SPosBasicService<DCP_StockOutTemplateDetailReq, DCP_StockOutTemplateDetailRes>{

	@Override
	protected boolean isVerifyFail(DCP_StockOutTemplateDetailReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockOutTemplateDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockOutTemplateDetailReq>(){};
	}

	@Override
	protected DCP_StockOutTemplateDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockOutTemplateDetailRes();
	}

	@Override
	protected DCP_StockOutTemplateDetailRes processJson(DCP_StockOutTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_StockOutTemplateDetailRes res = null;
		res = this.getResponse();
		int totalRecords = 0;								//总笔数
		int totalPages = 0;		
		String sql = "";
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_StockOutTemplateDetailRes.level1Elm>());
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
//				Map<String, Object> oneData_Count = getQDataDetail.get(0);
//				
//				String num = oneData_Count.get("COUNT").toString();
//				totalRecords=Integer.parseInt(num);
//				//算總頁數
//				totalPages = totalRecords / req.getPageSize();
//				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				for (Map<String, Object> oneData : getQDataDetail) 
				{
			
					DCP_StockOutTemplateDetailRes.level1Elm lv1 = res.new level1Elm();
					lv1.setItem(oneData.get("ITEM").toString());
					lv1.setPluNo(oneData.get("PLUNO").toString());
					lv1.setPluName(oneData.get("PLUNAME").toString());
					lv1.setPunit(oneData.get("PUNIT").toString());
					lv1.setPunitName(oneData.get("PUNITNAME").toString());
					lv1.setStatus(oneData.get("STATUS").toString());
					
					res.getDatas().add(lv1);
					lv1 = null; 
				}
			}
				
//			res.setPageNumber(req.getPageNumber());
//			res.setPageSize(req.getPageSize());
//			res.setTotalRecords(totalRecords);
//			res.setTotalPages(totalPages);
//			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！！");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StockOutTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub

		String sql=null;			
		String eId = req.geteId();
		String langType= req.getLangType();
		String templateno = req.getRequest().getTemplateNo();
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( 
				"select a.item,a.pluno,b.plu_name as pluName ,a.punit,c.uname as punitname, a.status from DCP_ptemplate_detail a " 
						+ " left join DCP_GOODS_lang  b on a.EID=b.EID and a.pluno=b.pluno and b.lang_type='"+ langType +"' "
						+ " left join DCP_UNIT_lang   c on a.EID=c.EID and a.punit =c.unit and c.lang_type='"+ langType +"' "
						+ " where a.EID='"+ eId +"' and a.ptemplateno='"+ templateno +"' and a.doc_type= '5' "
						+ " order by a.item , a.pluNo "	) ;

		sql = sqlbuf.toString();
		return sql;
		
	}
	
}
