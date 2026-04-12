package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_LStockOutTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_LStockOutTemplateDetail extends SPosBasicService<DCP_LStockOutTemplateDetailReq, DCP_LStockOutTemplateDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_LStockOutTemplateDetailReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String templateNO =req.getRequest().getTemplateNo();
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if (Check.Null(templateNO)) 
		{
			errCt++;
			errMsg.append("单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_LStockOutTemplateDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_LStockOutTemplateDetailReq>(){};
	}

	@Override
	protected DCP_LStockOutTemplateDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_LStockOutTemplateDetailRes();
	}

	@Override
	protected DCP_LStockOutTemplateDetailRes processJson(DCP_LStockOutTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;			
		//查詢資料
		DCP_LStockOutTemplateDetailRes res = this.getResponse();

		//单头查询
		try 
		{
			sql=this.getQuerySql(req);			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_LStockOutTemplateDetailRes.level1Elm>());//
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_LStockOutTemplateDetailRes.level1Elm oneLv1 = new DCP_LStockOutTemplateDetailRes().new level1Elm();		
					String item = oneData.get("ITEM").toString();
					String pluNO = oneData.get("PLUNO").toString();
					String pluName = oneData.get("PLU_NAME").toString();
					String punit = oneData.get("PUNIT").toString();
					String punitName = oneData.get("UNAME").toString();
					String status = oneData.get("STATUS").toString();

					//设置响应
					oneLv1.setItem(item);
					oneLv1.setPluNo(pluNO);
					oneLv1.setPluName(pluName);
					oneLv1.setPunit(punit);
					oneLv1.setPunitName(punitName);
					oneLv1.setStatus(status);

					res.getDatas().add(oneLv1);
				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_LStockOutTemplateDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;			
		String eId = req.geteId();
		String langType= req.getLangType();
		String templateno = req.getRequest().getTemplateNo();
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( 
				"select a.item,a.pluno,b.plu_name,a.punit,c.uname,a.status from DCP_ptemplate_detail a " 
						+ " left join DCP_GOODS_lang  b on a.EID=b.EID and a.pluno=b.pluno and b.lang_type='"+ langType +"' "
						+ " left join DCP_UNIT_lang   c on c.EID=a.EID and a.punit =c.unit and c.lang_type='"+ langType +"' "
						+ " inner join DCP_GOODS      d on d.status='100' and  a.EID=d.EID and a.pluno=d.pluno "
						+ " where a.EID='"+ eId +"' and a.ptemplateno='"+ templateno +"' and a.doc_type= '4' "	) ;
		sql = sqlbuf.toString();
		return sql;
	}
	
}
