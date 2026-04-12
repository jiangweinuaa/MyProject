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
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsCategoryCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsCategoryCreate extends SPosAdvanceService<DCP_GoodsCategoryCreateReq, DCP_GoodsCategoryCreateRes> {

	@Override
	protected void processDUID(DCP_GoodsCategoryCreateReq req, DCP_GoodsCategoryCreateRes res) throws Exception {
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

			List<level1Elm> getlangDatas = req.getRequest().getCategoryName_lang();

			sql = this.isRepeat(category, eId);
			List<Map<String, Object>> catDatas = this.doQueryData(sql, null);
			if(catDatas.isEmpty() ){
				String[] columns1 = { 
						"CATEGORY","UP_CATEGORY","CATEGORYLEVEL","DOWN_CATEGORYQTY","TOP_CATEGORY",
						"STATUS","EID","CREATETIME","CREATEOPID","PREFIXCODE" };
				DataValue[] insValue1 = null;

				if(getlangDatas != null && !getlangDatas.isEmpty()){
					for(level1Elm oneLv1: getlangDatas)
					{
						int insColCt = 0;
						String[] columnsName = {
								"CATEGORY","LANG_TYPE","CATEGORY_NAME","EID"
						};
						

						String langType = oneLv1.getLangType();
						String categoryName = oneLv1.getName();
						

						sql = this.isRepeatLang(category, eId,langType);
						List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);

						if(detailDatas.isEmpty()){
							
							DataValue[] insValueDetail = new DataValue[] 
									{
										new DataValue(category, Types.VARCHAR),
										new DataValue(langType, Types.VARCHAR),
										new DataValue(categoryName, Types.VARCHAR),									
										new DataValue(eId, Types.VARCHAR)										
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

				String downCategoryQty = "0";
				sql = this.getDowmCategoryQty(category, eId);
				List<Map<String, Object>> downQty = this.doQueryData(sql, null);
				if (downQty != null && downQty.isEmpty() == false)
				{
					downCategoryQty = downQty.get(0).get("DOWNCATEGORYQTY").toString();
				}

				insValue1 = new DataValue[]{
						new DataValue(category, Types.VARCHAR),
						new DataValue(upCategory, Types.VARCHAR),
						new DataValue(categoryLevel, Types.VARCHAR),
						new DataValue(downCategoryQty, Types.VARCHAR),
						new DataValue(topCategory, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR),					
						new DataValue(lastmoditime, Types.DATE),
                        new DataValue(req.getOpNO(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPreFixCode(), Types.VARCHAR),
				};

				InsBean ib1 = new InsBean("DCP_CATEGORY", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

				
				sql = this.isCateGoryImage(eId,category,categoryImage);
				List<Map<String, Object>> doQueryData = this.doQueryData(sql, null);
				
				if(doQueryData.isEmpty()){
					// 添加商品分类图片 
					String[] columnsDcpCategoryImage = {
							"EID","CATEGORY","CATEGORYIMAGE","LASTMODITIME"
					};
					insValue1 = null;
					insValue1 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(category, Types.VARCHAR),
							new DataValue(categoryImage, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE),
					};
					InsBean ib2 = new InsBean("DCP_CATEGORY_IMAGE", columnsDcpCategoryImage);
					ib2.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib2)); // 新增分类图片
				}
				
				
				this.doExecuteDataToDB();	
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else
			{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败: 分类编码为  "+category+" 的信息已存在");		
			}
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
	protected List<InsBean> prepareInsertData(DCP_GoodsCategoryCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsCategoryCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsCategoryCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsCategoryCreateReq req) throws Exception {
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
			errMsg.append("分类编码不能为空值, ");
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
	protected TypeToken<DCP_GoodsCategoryCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsCategoryCreateReq>(){};
	}

	@Override
	protected DCP_GoodsCategoryCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsCategoryCreateRes();
	}

	/**
	 * 验证当前分类是否已存在
	 * @param category
	 * @param eId
	 * @return
	 */
	private String isRepeat(String category, String eId){
		String sql = null;
		sql = "SELECT * FROM DCP_CATEGORY WHERE "
				+ " CATEGORY = '"+category+"' "
				+ " and EID = '"+eId+"'" ;
				//+ " and CATEGORYTYPE='"+categoryType+"' ";
		return sql;
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
	private String isCateGoryImage(String eId,String category,String categoryImage){
		String sql = null;
		sql = "SELECT * FROM DCP_CATEGORY_IMAGE WHERE "
				+ "EID = '"+eId+"' "
				+ "AND CATEGORY = '"+category+"' "
				+ "AND CATEGORYIMAGE = '"+categoryImage+"'";
		return sql;
	}


}
