package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ECStockUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ECStockUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_ECStockUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_ECStockUpdateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_ECStockUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ECStockUpdate
 * 服务说明：电商平台库存上下架修改
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockUpdate extends SPosAdvanceService<DCP_ECStockUpdateReq,DCP_ECStockUpdateRes>{

	@Override
	protected void processDUID(DCP_ECStockUpdateReq req, DCP_ECStockUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String shopId = req.getShopId();
		level1Elm request = req.getRequest();
		List<level2Elm> goodsDatas = request.getGoodsDatas();
		String ecStockNo = request.getEcStockNo();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		String modifyDate=sdf.format(cal.getTime());
		String opNo=req.getOpNO();
		String opName=req.getOpName();
		try 
		{

			if (checkExist(req))
			{
				//删除单身资料
				DelBean db = new DelBean("DCP_ECSTOCK_DETAIL");
				db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db.addCondition("ECSTOCKNO", new DataValue(ecStockNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db));

				//保存单身资料
				for (level2Elm lev2 : goodsDatas)
				{
					String item = lev2.getItem();
					String pluNo = lev2.getPluNo();
					String pluBarcode = lev2.getPluBarcode();
					String unit = lev2.getUnit();
					String allQty = lev2.getAllQty();
					List<level3Elm> stockDatas = lev2.getStockDatas();
					for (level3Elm lev3 : stockDatas)
					{
						//String ecPlatformName = lev3.getEcPlatformName();
						String ecPlatformNo = lev3.getEcPlatformNo();
						String qty = lev3.getQty();
						String[] columns_detail = {
								"EID","SHOPID","ECSTOCKNO","ECPLATFORMNO",
								"ITEM","PLUNO","PLUBARCODE","UNIT","ALLQTY","QTY" 
						};

						DataValue[] insValue_detail = new DataValue[] 
								{
										new DataValue(eId, Types.VARCHAR), 
										new DataValue(shopId, Types.VARCHAR), 
										new DataValue(ecStockNo, Types.VARCHAR),
										new DataValue(ecPlatformNo, Types.VARCHAR),								
										new DataValue(item, Types.VARCHAR),
										new DataValue(pluNo, Types.VARCHAR),
										new DataValue(pluBarcode, Types.VARCHAR),
										new DataValue(unit, Types.VARCHAR),
										new DataValue(allQty, Types.VARCHAR),
										new DataValue(qty, Types.VARCHAR)
								};
						InsBean ib_detail = new InsBean("DCP_ECSTOCK_DETAIL", columns_detail);
						ib_detail.addValues(insValue_detail);
						this.addProcessData(new DataProcessBean(ib_detail)); 	
					}
				}

				//更新单头资料
				String docType = request.getDocType();
				String opType = request.getOpType();
				String totQty=request.getTotQty();
				String loadDocType=request.getLoadDocType();
				String loadDocNo=request.getLoadDocNo();
				String loadDocShop = request.getLoadDocShop();


				UptBean ub = new UptBean("DCP_ECSTOCK");
				ub.addUpdateValue("OPTYPE", new DataValue(opType, Types.VARCHAR));
				ub.addUpdateValue("DOCTYPE", new DataValue(docType, Types.VARCHAR));
				ub.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.DATE));
				ub.addUpdateValue("MODIFYBY", new DataValue(opNo, Types.VARCHAR));
				ub.addUpdateValue("MODIFYBYNAME", new DataValue(opName, Types.VARCHAR));
				ub.addUpdateValue("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
				ub.addUpdateValue("LOAD_DOCNO", new DataValue(loadDocNo, Types.VARCHAR));
				ub.addUpdateValue("LOAD_DOCSHOP", new DataValue(loadDocShop, Types.VARCHAR));
				ub.addUpdateValue("TOT_QTY", new DataValue(totQty, Types.VARCHAR));
				
				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				ub.addCondition("ECSTOCKNO", new DataValue(ecStockNo, Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub));

			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "上下架单号不存在，请重新查询 ");		
			}


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
	protected List<InsBean> prepareInsertData(DCP_ECStockUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ECStockUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ECStockUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ECStockUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		String ecStockNo = request.getEcStockNo();
		String docType = request.getDocType();
		String opType = request.getOpType();
		List<level2Elm> goodsDatas = request.getGoodsDatas();

		if (Check.Null(ecStockNo)) 
		{
			errMsg.append("上下架编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(docType)) 
		{
			errMsg.append("异动方向不可为空值, ");
			isFail = true;
		}
		if (Check.Null(opType)) 
		{
			errMsg.append("操作类型不可为空值, ");
			isFail = true;
		}

		for (level2Elm lev2 : goodsDatas)
		{
			String item = lev2.getItem();
			String pluNo = lev2.getPluNo();
			String unit = lev2.getUnit();
			String allQty = lev2.getAllQty();
			List<level3Elm> stockDatas = lev2.getStockDatas();

			if (Check.Null(item)) 
			{
				errMsg.append("商品主项次不可为空值, ");
				isFail = true;
			}
			if (Check.Null(pluNo)) 
			{
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			if (Check.Null(unit)) 
			{
				errMsg.append("商品单位不可为空值, ");
				isFail = true;
			}
			if (Check.Null(allQty)) 
			{
				errMsg.append("合计平台数量不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumeric(allQty))
				{
					errMsg.append("合计平台数量必须为整数, ");
					isFail = true;
				}
			}

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}

			for (level3Elm lev3 : stockDatas)
			{
				String ecPlatformNo = lev3.getEcPlatformNo();
				String ecPlatformName = lev3.getEcPlatformName();
				String qty = lev3.getQty();

				if (Check.Null(ecPlatformNo)) 
				{
					errMsg.append("电商平台代码不可为空值, ");
					isFail = true;
				}
				if (Check.Null(ecPlatformName)) 
				{
					errMsg.append("电商平台名称不可为空值, ");
					isFail = true;
				}
				if (Check.Null(qty)) 
				{
					errMsg.append("异动数量不可为空值, ");
					isFail = true;
				}
				else
				{
					if (!PosPub.isNumeric(qty))
					{
						errMsg.append("异动数量必须为整数, ");
						isFail = true;
					}
				}
				if (isFail)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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
	protected TypeToken<DCP_ECStockUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ECStockUpdateReq>(){};
	}

	@Override
	protected DCP_ECStockUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ECStockUpdateRes();
	}

	private boolean checkExist(DCP_ECStockUpdateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String shopId=req.getShopId();
		level1Elm request = req.getRequest();
		String ecStockNo = request.getEcStockNo();	
		sql = " select * from DCP_ECSTOCK where status='0' and EID='"+eId+"' and SHOPID='"+shopId+"' and ECSTOCKNO='"+ecStockNo+"'  ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}


}
