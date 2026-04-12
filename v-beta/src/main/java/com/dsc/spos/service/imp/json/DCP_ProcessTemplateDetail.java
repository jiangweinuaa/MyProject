package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ProcessTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：PTemplateGet
 *   說明：要货模板查询
 * 服务说明：要货模板查询
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_ProcessTemplateDetail extends SPosBasicService<DCP_ProcessTemplateDetailReq,DCP_ProcessTemplateDetailRes>  {

	@Override
	protected boolean isVerifyFail(DCP_ProcessTemplateDetailReq req) throws Exception {
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
		if (Check.Null(templateNO)) {
			errCt++;
			errMsg.append("模板单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_ProcessTemplateDetailReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ProcessTemplateDetailReq>(){};
	}

	@Override
	protected DCP_ProcessTemplateDetailRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ProcessTemplateDetailRes();
	}

	@Override
	protected DCP_ProcessTemplateDetailRes processJson(DCP_ProcessTemplateDetailReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_ProcessTemplateDetailRes res = this.getResponse();

		//单头查询
		try 
		{
			sql=this.getQuerySql(req);			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_ProcessTemplateDetailRes.level1Elm>());//
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_ProcessTemplateDetailRes.level1Elm oneLv1 = res.new level1Elm();		
					String item = oneData.get("ITEM").toString();
					String pluNO = oneData.get("PLUNO").toString();
					String pluName = oneData.get("PLU_NAME").toString();
					String punit = oneData.get("PUNIT").toString();
					String punitName = oneData.get("UNIT_NAME").toString();
					String status = oneData.get("STATUS").toString();
                    String categoryno = oneData.get("CATEGORYNO").toString();
                    String category_name = oneData.get("CATEGORY_NAME").toString();
                    String sortid = oneData.get("SORTID").toString();


                    //设置响应
					oneLv1.setItem(item);
					oneLv1.setPluNo(pluNO);
					oneLv1.setPluName(pluName);
					oneLv1.setPunit(punit);
					oneLv1.setPunitName(punitName);
					oneLv1.setStatus(status);
                    oneLv1.setCategory(categoryno);
                    oneLv1.setCategoryName(category_name);
                    oneLv1.setSortId(sortid);

					res.getDatas().add(oneLv1);
					oneLv1=null;
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
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_ProcessTemplateDetailReq req) throws Exception {

		String sql=null;			
		String eId = req.geteId();
		String langType= req.getLangType();
		String templateno = req.getRequest().getTemplateNo();
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( 
				"select a.item,a.pluno,b.plu_name,nvl(d.prod_unit,a.punit) as  punit,"
				+ "case when d.prod_unit is not null then c.uname else e.uname end as unit_name ,a.status,a.categoryno,f.category_name,a.sortid " +
                        "from DCP_ptemplate_detail a "
						+ " left join DCP_GOODS_lang  b on a.EID=b.EID and a.pluno=b.pluno and b.lang_type='"+ langType +"' "
						+ " inner join DCP_GOODS      d on a.EID=d.EID and a.pluno=d.pluno "
						+ " left join DCP_UNIT_lang   c on c.EID=d.EID and d.prod_unit =c.unit and c.lang_type='"+ langType +"' "
						+ " left join DCP_UNIT_lang   e on e.EID=a.EID and a.punit =e.unit and e.lang_type='"+ langType +"' " +
                        "   left join dcp_category_lang f on a.eid=f.eid and a.categoryno=f.category and f.lang_type='"+langType+"' "
						+ " where a.EID='"+ eId +"' and a.ptemplateno='"+ templateno +"' and a.doc_type= '2' " +
                        "   order by a.sortid,a.item "	) ;

		sql = sqlbuf.toString();
		return sql;

	}


}


