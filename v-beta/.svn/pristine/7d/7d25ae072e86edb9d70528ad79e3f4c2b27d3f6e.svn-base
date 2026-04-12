package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsBrandCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsBrandCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsBrandCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 新增商品品牌 2018-10-18
 * @author yuanyy
 *
 */
public class DCP_GoodsBrandCreate extends SPosAdvanceService<DCP_GoodsBrandCreateReq, DCP_GoodsBrandCreateRes> {

	@Override
	protected void processDUID(DCP_GoodsBrandCreateReq req, DCP_GoodsBrandCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = null;
		String eId = req.geteId();
		String brandNo = req.getRequest().getBrandNo();
		String status = req.getRequest().getStatus();
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		List<level1Elm> getLangDatas = req.getRequest().getBrandName_lang();
		
		sql = this.isRepeat(brandNo, eId);
		List<Map<String , Object>> datas = this.doQueryData(sql, null);
		if(datas.isEmpty()){
			if(getLangDatas != null && !getLangDatas.isEmpty()){
				for(level1Elm oneLv1: getLangDatas){
									
					String[] columnsName = {
					  		"BRANDNO","LANG_TYPE","BRAND_NAME","EID"
						};
					
					String langType = oneLv1.getLangType();
					String brandName = oneLv1.getName();
								
					sql = this.isRepeatLang(brandNo, eId, langType);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
					
					if(detailDatas.isEmpty()){
						
						DataValue[] insValueDetail = new DataValue[] 
								{
									new DataValue(brandNo, Types.VARCHAR),
									new DataValue(langType, Types.VARCHAR),
									new DataValue(brandName, Types.VARCHAR),									
									new DataValue(eId, Types.VARCHAR)																						
								};
						InsBean ib2 = new InsBean("DCP_BRAND_LANG", columnsName);
						ib2.addValues(insValueDetail);
						this.addProcessData(new DataProcessBean(ib2));	
				
					}else{
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription("服务执行失败: 品牌编码为  "+brandNo+" , 多语言类型为 "+langType+" 的信息已存在");	
						return;
					}
				}
			}
			

			String[] columns1 = { "BRANDNO","STATUS","EID","CREATETIME" };
			DataValue[] insValue1 = null;
			
			insValue1 = new DataValue[]{
					new DataValue(brandNo, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
				};
			
			InsBean ib1 = new InsBean("DCP_BRAND", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增品牌
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			
		}else{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败: 品牌编码为  "+brandNo+"  的信息已存在");	
			return;
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsBrandCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsBrandCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsBrandCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsBrandCreateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");

	    if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
	    String brandNo = req.getRequest().getBrandNo();
	    
	    List<level1Elm> datas = req.getRequest().getBrandName_lang();
	    
	    if(Check.Null(brandNo)){
		   	errMsg.append("品牌编码不能为空值 ");
		   	isFail = true;
		}
	    if(datas != null && !datas.isEmpty()){
	    	for (level1Elm oneData : datas) 
			{
	    		String langType = oneData.getLangType();
	    		
	    		if(Check.Null(langType)){
	    		   	errMsg.append("多语言类型不能为空值 ");
	    		   	isFail = true;
	    		}
			}
	    	
	    }
	    
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsBrandCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsBrandCreateReq>(){};
	}

	@Override
	protected DCP_GoodsBrandCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsBrandCreateRes();
	}
	
	/**
	 * 验证品牌是否已存在
	 * @param brandNO
	 * @param eId
	 * @return
	 */
	private String isRepeat(String brandNO, String eId){
		String sql = null;
		sql = "SELECT * FROM DCP_BRAND WHERE "
				+ " BRANDNO = '"+brandNO+"' "
				+ " and EID = '"+eId+"'" ;
		return sql;
	}
	
	/**
	 * 验证多语言信息是否重复 
	 * @param brandNO
	 * @param eId
	 * @param langType
	 * @return
	 */
	private String isRepeatLang(String brandNO, String eId, String langType ){
		String sql = null;
		sql = "SELECT * FROM DCP_BRAND_LANG WHERE "
				+ " BRANDNO = '"+brandNO+"' "
				+ " and EID = '"+eId+"' "
				+ " and lang_Type = '"+langType+"'" ;
		return sql;
	}
	
}
