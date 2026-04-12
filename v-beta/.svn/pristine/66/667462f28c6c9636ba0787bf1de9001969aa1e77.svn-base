package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_STakeTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_STakeTemplateDetailRes;
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
public class DCP_STakeTemplateDetail extends SPosBasicService<DCP_STakeTemplateDetailReq,DCP_STakeTemplateDetailRes>  
{

	@Override
	protected boolean isVerifyFail(DCP_STakeTemplateDetailReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		if(req.getRequest()==null) {
            errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String templateNO =req.getRequest().getTemplateNo();

        if (Check.Null(templateNO)) {
            errMsg.append("单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_STakeTemplateDetailReq> getRequestType() {
		return new TypeToken<DCP_STakeTemplateDetailReq>(){};
	}

	@Override
	protected DCP_STakeTemplateDetailRes getResponseType() {
		return new DCP_STakeTemplateDetailRes();
	}

	@Override
	protected DCP_STakeTemplateDetailRes processJson(DCP_STakeTemplateDetailReq req) throws Exception {

		DCP_STakeTemplateDetailRes res = this.getResponse();

		//单头查询
		try 
		{
			String sql=this.getQuerySql(req);
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_STakeTemplateDetailRes.level1Elm>());//
			if (getQDataDetail != null && !getQDataDetail.isEmpty())
			{
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_STakeTemplateDetailRes.level1Elm oneLv1 = res.new level1Elm();		
					String item = oneData.get("ITEM").toString();
					String pluNO = oneData.get("PLUNO").toString();
					String pluName = oneData.get("PLU_NAME").toString();
					String punit = oneData.get("PUNIT").toString();
					String punitName = oneData.get("UNAME").toString();
					String status = oneData.get("STATUS").toString();
					String categoryNo =oneData.get("CATEGORYNO").toString();
					String categoryName = oneData.get("CATEGORY_NAME").toString();

					//设置响应
					oneLv1.setItem(item);
					oneLv1.setPluNo(pluNO);
					oneLv1.setPluName(pluName);
					oneLv1.setPunit(punit);
					oneLv1.setPunitName(punitName);
					oneLv1.setStatus(status);
					oneLv1.setCategoryNo(categoryNo);
					oneLv1.setCategoryName(categoryName);

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

	}

	@Override
	protected String getQuerySql(DCP_STakeTemplateDetailReq req) throws Exception {

		String eId = req.geteId();
		String langType= req.getLangType();
		String templateno = req.getRequest().getTemplateNo();
		StringBuffer sqlbuf=new StringBuffer();
		String rangeWay = req.getRequest().getRangeWay();	 //0.按商品盘点 1.按分类盘点	
		
		if (Check.Null(rangeWay)||rangeWay.equals("0"))   
		{
			sqlbuf.append( 
					"select a.item,a.pluno,b.plu_name,a.punit,c.uname,a.status,N'' as CATEGORYNO,N'' as CATEGORY_NAME "
							+ " from DCP_ptemplate_detail a " 
							+ " left join DCP_GOODS_lang  b on a.EID=b.EID and a.pluno=b.pluno and b.lang_type='"+ langType +"' "
							+ " left join DCP_UNIT_lang   c on c.EID=a.EID and a.punit =c.unit and c.lang_type='"+ langType +"' "
							+ " inner join DCP_GOODS      d on d.status='100' and  a.EID=d.EID and a.pluno=d.pluno "
							+ " where a.EID='"+ eId +"' and a.ptemplateno='"+ templateno +"' and a.doc_type= '1' "
							+ " order by a.item "	) ;
		}
		else
		{
			sqlbuf.append( 
					" select a.item,a.pluno,N'' as plu_name,a.punit,N'' as uname,a.status,a.CATEGORYNO,b.CATEGORY_NAME"
							+ " from DCP_ptemplate_detail a " 
							+ " left join DCP_CATEGORY_lang b on a.EID=b.EID and a.CATEGORYNO=b.CATEGORY and b.lang_type='"+ langType +"' "
							+ " where a.EID='"+ eId +"' and a.ptemplateno='"+ templateno +"' and a.doc_type= '1' "
							+ " order by a.item "	) ;
		}

		return sqlbuf.toString();

	}


}


