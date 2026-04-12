package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderCategorySyncReq;
import com.dsc.spos.json.cust.req.DCP_OrderCategorySyncReq.level1categoryElm;
import com.dsc.spos.json.cust.req.DCP_OrderCategorySyncReq.level1shopsElm;
import com.dsc.spos.json.cust.res.DCP_OrderCategorySyncRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：OrderCategorySync
 * 服务说明：外卖分类同步
 * @author jinzma	 
 * @since  2019-03-12
 */
public class DCP_OrderCategorySync  extends SPosAdvanceService<DCP_OrderCategorySyncReq,DCP_OrderCategorySyncRes> {

	@Override
	protected void processDUID(DCP_OrderCategorySyncReq req, DCP_OrderCategorySyncRes res) throws Exception {
		// TODO 自动生成的方法存根
		String[] loadDocTypes = req.getLoadDocType();
		String loadDocType=loadDocTypes[0].toString();
		String operType= req.getOperType();   //1.新增 2.修改 3.删除
		try
		{
			if (Check.Null(loadDocType))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "外卖平台未选择，");		
			}

			//分类新增
			//			if (operType.equals("1")) categoryInsert(req,loadDocType,operType);
			//分类修改
			//			if (operType.equals("2")) categoryUpdate(req,loadDocType);
			//分类删除
			//			if (operType.equals("3")) categoryDelete(req,loadDocType);

			categorySync(req,loadDocType,operType);
			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCategorySyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCategorySyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCategorySyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderCategorySyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String[] loadDocType = req.getLoadDocType();
		String operType = req.getOperType();
		List<level1categoryElm> categorydatas = req.getCategorydatas();
		List<level1shopsElm> shopsdatas = req.getShopsdatas();

		if (loadDocType == null || loadDocType.length == 0) 
		{
			errMsg.append("外卖平台不可为空值, ");
			isFail = true;
		}
		if (Check.Null(operType) ) 
		{
			errMsg.append("操作类型不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level1categoryElm par : categorydatas) 
		{	
			if (Check.Null(par.getCategoryNO())) 
			{
				errMsg.append("分类编号不可为空值, ");
				isFail = true;
			}	
			if (Check.Null(par.getCategoryName())) 
			{
				errMsg.append("分类名称不可为空值, ");
				isFail = true;
			}	


			if ( operType.equals("2") && Check.Null(par.getNewCategoryName())) 
			{
				errMsg.append("新的分类名称不可为空值, ");
				isFail = true;
			}	

			if (Check.Null(par.getPriority())) 
			{
				errMsg.append("分类优先级不可为空值, ");
				isFail = true;
			}	
			else 
			{
				if (!PosPub.isNumeric(par.getPriority()))
				{
					errMsg.append("分类优先级必须为数值, ");
					isFail = true;
				}
			}

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		for (level1shopsElm par : shopsdatas) 
		{	
			if (Check.Null(par.getErpShopNO())) 
			{
				errMsg.append("门店编号不可为空值, ");
				isFail = true;
			}	
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderCategorySyncReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderCategorySyncReq>(){} ;
	}

	@Override
	protected DCP_OrderCategorySyncRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderCategorySyncRes();
	}

	private void categorySync(DCP_OrderCategorySyncReq req,String load_DocType,String trans_Type )  throws Exception {
		String eId = req.geteId();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String sDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String sTime = df.format(cal.getTime());
		String sql ="";	

		DataValue[] insValue = null;
		List<level1categoryElm> categorydatas = req.getCategorydatas();
		List<level1shopsElm> shopsdatas = req.getShopsdatas();		
		
		if(trans_Type.equals("2"))//分类修改，每次只能修改一个
		{
			if(categorydatas.size()>1)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "修改分类时只能一次修改一个分类！");
			}
		}
		String[] conditionValues = {eId,load_DocType}; 		
		sql = " select SHOPID,ORDER_CATEGORYNO,ORDER_CATEGORYNAME  from OC_MAPPINGCATEGORY "
				+ " where SHOPID<>' ' AND ORDER_CATEGORYNO<>' ' AND  EID=? and LOAD_DOCTYPE = ?  " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		//循环门店
		for (level1shopsElm shopspar : shopsdatas) 
		{
			String shopId=shopspar.getErpShopNO();
			String orderShopNO=shopspar.getOrderShopNO();	
			String orderShopName = shopspar.getOrderShopName();

			for (level1categoryElm categorypar : categorydatas) 
			{
				String trans_ID=UUID.randomUUID().toString();
				String categoryNO=categorypar.getCategoryNO();
				String categoryName=categorypar.getCategoryName();
				String newCategoryName=categorypar.getNewCategoryName();
				String priority=categorypar.getPriority();
				String order_CategoryNO="";
				String order_CategoryName="";  				

				//新增OC_TRANSTASK
				String[] transTask_columns = {
						"EID","SHOPID","TRANS_ID","LOAD_DOCTYPE","TRANS_TYPE","ORDER_SHOP","ORDER_SHOPNAME",
						"CREATEBYNO","CREATEBYNAME","TRANS_DATE","TRANS_TIME","TRANS_FLG","STATUS" 
				};		
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 					
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(trans_ID, Types.VARCHAR),
						new DataValue(load_DocType, Types.VARCHAR),					
						new DataValue(trans_Type, Types.VARCHAR),
						new DataValue(orderShopNO,Types.VARCHAR),
						new DataValue(orderShopName,Types.VARCHAR),
						new DataValue(req.getOpNO(), Types.VARCHAR),
						new DataValue(req.getOpName(), Types.VARCHAR),					
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("100", Types.VARCHAR)
				};
				InsBean transTask_ib = new InsBean("OC_TRANSTASK", transTask_columns);
				transTask_ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(transTask_ib)); 	



				for ( Map<String, Object> oneData : getQData) 
				{
					String oneData_order_CategoryNO=oneData.get("ORDER_CATEGORYNO").toString();
					String oneData_order_Categoryname=oneData.get("ORDER_CATEGORYNAME").toString();
					String oneData_shopNO=oneData.get("SHOPID").toString();
					if (shopId.equals(oneData_shopNO) && categoryName.equals(oneData_order_Categoryname))//分类用名称做对应关系
					{
						order_CategoryNO=oneData_order_CategoryNO;
						order_CategoryName=oneData_order_Categoryname;
						break;
					}
				}
				//分类修改处理
				if (trans_Type.equals("2"))
				{
					if (!Check.Null(order_CategoryNO) && !Check.Null(order_CategoryName))
					{
						categoryName=newCategoryName;
					}
				}

				//新增OC_TRANSTASK_CATEGORY
				String[] transTaskCategory_columns = {
						"EID","SHOPID","TRANS_ID","CATEGORYNO","CATEGORYNAME",
						"PRIORITY","ORDER_CATEGORYNO","ORDER_CATEGORYNAME","STATUS" };						
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(trans_ID, Types.VARCHAR),
						new DataValue(categoryNO, Types.VARCHAR),								
						new DataValue(categoryName, Types.VARCHAR),  
						new DataValue(Integer.valueOf(priority), Types.INTEGER),
						new DataValue(order_CategoryNO, Types.VARCHAR),
						new DataValue(order_CategoryName, Types.VARCHAR),						
						new DataValue("100", Types.VARCHAR)
				};		
				InsBean transTaskCategory_ib = new InsBean("OC_TRANSTASK_CATEGORY", transTaskCategory_columns);
				transTaskCategory_ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(transTaskCategory_ib)); 				

			}
			//this.doExecuteDataToDB();	
		}
	}


}


