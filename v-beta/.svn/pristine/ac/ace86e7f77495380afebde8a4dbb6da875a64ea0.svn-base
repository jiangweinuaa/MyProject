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
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsCategoryUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsCategoryUpdate extends SPosAdvanceService<DCP_GoodsCategoryUpdateReq, DCP_GoodsCategoryUpdateRes> {

	@Override
	protected void processDUID(DCP_GoodsCategoryUpdateReq req, DCP_GoodsCategoryUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;

		try 
		{
			String eId = req.geteId();
			String category = req.getRequest().getCategory();
			String upCategory = req.getRequest().getUpCategory();
			String topCategory = req.getRequest().getTopCategory();
			String categoryLevel = req.getRequest().getCategoryLevel();
			String status = req.getRequest().getStatus();
            if(Check.Null(req.getRequest().getPreFixCode())){
                req.getRequest().setPreFixCode(category);
            }
			//String categoryType = req.getRequest().getCategoryType();
			String lastmoditime = null;//req.getRequest().getLastmoditime();
			String categoryImage = req.getRequest().getCategoryImage();
			if(lastmoditime==null||lastmoditime.isEmpty())
			{
				lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}

			String downCategoryQty = "0";
			sql = this.getDowmCategoryQty(category, eId);
			List<Map<String, Object>> downQty = this.doQueryData(sql, null);
			if (downQty != null && downQty.isEmpty() == false)
			{
				downCategoryQty = downQty.get(0).get("DOWNCATEGORYQTY").toString();
			}

			List<level1Elm> getlangDatas = req.getRequest().getCategoryName_lang();
			//先删除原有多语言信息 
			DelBean db1 = new DelBean("DCP_CATEGORY_LANG");
			db1.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			//db1.addCondition("CATEGORYTYPE", new DataValue(categoryType, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			if(getlangDatas != null && !getlangDatas.isEmpty()){
				for(level1Elm oneLv1: getlangDatas)
				{
					String[] columnsName = {
							"CATEGORY","LANG_TYPE","CATEGORY_NAME","EID","LASTMODITIME"
					};			
					String langType = oneLv1.getLangType();
					String categoryName = oneLv1.getName();
				
					sql = this.isRepeatLang(category, langType, eId);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);

					if(detailDatas.isEmpty()){
						
						DataValue[] insValueDetail = new DataValue[] 
								{
									new DataValue(category, Types.VARCHAR),
									new DataValue(langType, Types.VARCHAR),
									new DataValue(categoryName, Types.VARCHAR),									
									new DataValue(eId, Types.VARCHAR),								
									new DataValue(lastmoditime, Types.DATE)
								};

						InsBean ib2 = new InsBean("DCP_CATEGORY_LANG", columnsName);
						ib2.addValues(insValueDetail);
						this.addProcessData(new DataProcessBean(ib2));		
						
					
						
					}
					else
					{
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription("服务执行失败: 分类编码为  "+category+" ,多语言类型为 "+langType+" 的信息已存在");	
						return;
					}
				}
			}

			UptBean ub1 = new UptBean("DCP_CATEGORY");			
			ub1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));
            ub1.addUpdateValue("PREFIXCODE", new DataValue(req.getRequest().getPreFixCode(), Types.VARCHAR));
            ub1.addUpdateValue("UP_CATEGORY",new DataValue(upCategory, Types.VARCHAR));
			ub1.addUpdateValue("TOP_CATEGORY",new DataValue(topCategory, Types.VARCHAR)); 
			ub1.addUpdateValue("CATEGORYLEVEL",new DataValue(categoryLevel, Types.VARCHAR)); 
			ub1.addUpdateValue("DOWN_CATEGORYQTY",new DataValue(downCategoryQty, Types.VARCHAR)); 
			ub1.addUpdateValue("STATUS",new DataValue(status, Types.VARCHAR)); 
			ub1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE));
            ub1.addUpdateValue("CREATEOPID",new DataValue(req.getOpNO(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
			
			sql = this.isCateGoryImage(eId,category);
			List<Map<String, Object>> doQueryData = this.doQueryData(sql, null);
			
			
			// 如果商品图片存在 进行修改  如果不存在则插入数据
			if(doQueryData.isEmpty()){
				String[] columnsDcpCategoryImage = {
						"EID","CATEGORY","CATEGORYIMAGE","LASTMODITIME"
				};
				DataValue[] insValueDetail = new DataValue[] 
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(category, Types.VARCHAR),
							new DataValue(categoryImage, Types.VARCHAR),									
							new DataValue(lastmoditime, Types.DATE)
						};
				InsBean ib3 = new InsBean("DCP_CATEGORY_IMAGE", columnsDcpCategoryImage);
				ib3.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ib3));	
			}else {
				UptBean ub2 = new UptBean("DCP_CATEGORY_IMAGE");			
				ub2.addCondition("EID",new DataValue(eId, Types.VARCHAR));
				ub2.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));		

				ub2.addUpdateValue("CATEGORYIMAGE",new DataValue(categoryImage, Types.VARCHAR)); 
				ub2.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE));
				this.addProcessData(new DataProcessBean(ub2));	
			}
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:"+e.getMessage());		

		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsCategoryUpdateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		String category = req.getRequest().getCategory();
		
		if(Check.Null(category))
		{
			errMsg.append("分类编码不能为空值 ");
			isFail = true;
		}
		
		
		List<level1Elm> datas = req.getRequest().getCategoryName_lang();

		if(datas==null||datas.isEmpty())
		{
			errMsg.append("多语言资料不能为空");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		
		for (level1Elm oneData : datas) 
		{
			String langType = oneData.getLangType();

			if(Check.Null(langType)){
				errMsg.append("多语言类型不能为空值 ");
				isFail = true;
			}
		}
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsCategoryUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsCategoryUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsCategoryUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsCategoryUpdateRes();
	}

	/**
	 * 验证多语言信息是否已经存在
	 * @param category
	 * @param eId
	 * @param langType
	 * @return
	 */
	private String isRepeatLang(String category, String eId, String langType){
		String sql = null;
		sql = "SELECT * FROM DCP_CATEGORY_LANG WHERE "
				+ " CATEGORY = '"+category+"' "
				+ " AND EID = '"+eId+"' "
				+ " AND LANG_TYPE = '"+langType+"'" ;
				
		return sql;
	}

	/**
	 * 获取下级分类总数
	 * @param category
	 * @param eId
	 * @return
	 */
	private String getDowmCategoryQty(String category, String eId){
		String sql = null;
		sql = "SELECT COUNT(*) AS downCategoryQty  FROM  dcp_category "
				+ " where  UP_CATEGORY = '"+category+"' "
				+ " AND EID = '"+eId+"' ";
				
		return sql;
	}
	
	/**
	 * 验证分类图片是否存在
	 * @param eId
	 * @param category
	 * @param
	 * @return
	 */
	private String isCateGoryImage(String eId,String category){
		String sql = null;
		sql = "SELECT * FROM DCP_CATEGORY_IMAGE WHERE "
				+ "EID = '"+eId+"' "
				+ "AND CATEGORY = '"+category+"' ";
		return sql;
	}


}
