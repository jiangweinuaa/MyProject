package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_POrderTemplateDetailRes;
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
public class DCP_POrderTemplateDetail extends SPosBasicService<DCP_POrderTemplateDetailReq,DCP_POrderTemplateDetailRes>  {
	
	@Override
	protected boolean isVerifyFail(DCP_POrderTemplateDetailReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String templateNO =req.getRequest().getTemplateNo();
		if (Check.Null(templateNO)) {
			errMsg.append("单号不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_POrderTemplateDetailReq> getRequestType() {
		return new TypeToken<DCP_POrderTemplateDetailReq>(){};
	}
	
	@Override
	protected DCP_POrderTemplateDetailRes getResponseType() {
		return new DCP_POrderTemplateDetailRes();
	}
	
	@Override
	protected DCP_POrderTemplateDetailRes processJson(DCP_POrderTemplateDetailReq req) throws Exception {
		String sql=null;
		//查詢資料
		DCP_POrderTemplateDetailRes res = this.getResponse();
		
		//单头查询
		try {
			sql=this.getQuerySql(req);
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_POrderTemplateDetailRes.level1Elm>());//
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
				for (Map<String, Object> oneData : getQDataDetail) {
					DCP_POrderTemplateDetailRes.level1Elm oneLv1 = res.new level1Elm();
					String item = oneData.get("ITEM").toString();
					String pluNO = oneData.get("PLUNO").toString();
					String pluName = oneData.get("PLU_NAME").toString();
					String punit = oneData.get("PUNIT").toString();
					String punitName = oneData.get("UNAME").toString();
					String minQty = oneData.get("MIN_QTY").toString();
					String maxQty = oneData.get("MAX_QTY").toString();
					String mulQty = oneData.get("MUL_QTY").toString();
					String defQty = oneData.get("DEFAULT_QTY").toString();
					
					String status = oneData.get("STATUS").toString();
					String groupNO = oneData.get("PORDER_GROUP").toString();
					String groupName = oneData.get("GROUPNAME").toString();
					
					//设置响应
					oneLv1.setItem(item);
					oneLv1.setPluNo(pluNO);
					oneLv1.setPluName(pluName);
					oneLv1.setPunit(punit);
					oneLv1.setPunitName(punitName);
					oneLv1.setMinQty(minQty);
					oneLv1.setMaxQty(maxQty);
					oneLv1.setMulQty(mulQty);
					oneLv1.setDefQty(defQty);
					oneLv1.setStatus(status);
					oneLv1.setGroupNo(groupNO);
					oneLv1.setGroupName(groupName);
					
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_POrderTemplateDetailReq req) throws Exception {
		
		String sql=null;
		String eId = req.geteId();
		String langType= req.getLangType();
		String templateno = req.getRequest().getTemplateNo();
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(" "
				+ " select a.item,a.pluno,b.plu_name,a.punit,c.uname,min_Qty,max_Qty,mul_Qty,a.status, "
				+ " a.default_Qty,a.porder_group,e.groupname from DCP_ptemplate_detail a "
				+ " left join DCP_GOODS_lang  b on a.EID=b.EID and a.pluno=b.pluno and b.lang_type='"+ langType +"' "
				+ " left join DCP_UNIT_lang   c on a.EID=c.EID and a.punit =c.unit and c.lang_type='"+ langType +"' "
				+ " inner join DCP_GOODS      d on d.status='100' and  a.EID=d.EID and a.pluno=d.pluno "
				+ " left join  DCP_ptemplate_detail_group e on a.EID=e.EID and a.porder_group=e.groupno "
				+ " where a.EID='"+ eId +"' and a.ptemplateno='"+ templateno +"' and a.doc_type= '0' "
				+ " order by a.item "	) ;
		
		sql = sqlbuf.toString();
		return sql;
		
	}
	
	
}


