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
import com.dsc.spos.json.cust.req.DCP_ECStockCreateReq;
import com.dsc.spos.json.cust.req.DCP_ECStockCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_ECStockCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_ECStockCreateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_ECStockCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ECStockCreate
 * 服务说明：电商平台库存上下架新增
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockCreate extends SPosAdvanceService<DCP_ECStockCreateReq,DCP_ECStockCreateRes>{

	@Override
	protected void processDUID(DCP_ECStockCreateReq req, DCP_ECStockCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String shopId = req.getShopId();
		level1Elm request = req.getRequest();
		List<level2Elm> goodsDatas = request.getGoodsDatas();

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		String createDate=sdf.format(cal.getTime());
		String opNo=req.getOpNO();
		String opName=req.getOpName();
		
		try 
		{
			if (checkExist(req) == false)
			{
				String ecStockNo = this.getEcStockNo(req);
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

				//保存单头资料
				String ecStockId= request.getEcStockId();
				String docType = request.getDocType();
				String opType = request.getOpType();
				String totQty=request.getTotQty();
				String loadDocType=request.getLoadDocType();
				String loadDocNo=request.getLoadDocNo();
				String loadDocShop = request.getLoadDocShop();

				
				String[] columns = {
						"EID","SHOPID","ECSTOCKNO","STATUS","ECSTOCKID","OPTYPE","DOCTYPE",
						"CREATER_DATE","CREATERBY","CREATERBYNAME","MODIFY_DATE","MODIFYBY","ACCOUNT_DATE","ACCOUNTBY",
						"LOAD_DOCTYPE","LOAD_DOCNO","LOAD_DOCSHOP","TOT_QTY" };
				DataValue[] insValue = new DataValue[] 
						{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR), 
								new DataValue(ecStockNo, Types.VARCHAR),
								new DataValue("0", Types.VARCHAR),								
								new DataValue(ecStockId, Types.VARCHAR),
								new DataValue(opType, Types.VARCHAR),
								new DataValue(docType, Types.VARCHAR),
								new DataValue(createDate, Types.DATE),
								new DataValue(opNo, Types.VARCHAR),
								new DataValue(opName, Types.VARCHAR),
								new DataValue("",Types.VARCHAR),
								new DataValue("",Types.VARCHAR),								
								new DataValue("",Types.VARCHAR),
								new DataValue("",Types.VARCHAR),
								new DataValue(loadDocType, Types.VARCHAR),
								new DataValue(loadDocNo, Types.VARCHAR),
								new DataValue(loadDocShop, Types.VARCHAR),
								new DataValue(totQty, Types.VARCHAR)
						};
				InsBean ib = new InsBean("DCP_ECSTOCK", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 	

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
	protected List<InsBean> prepareInsertData(DCP_ECStockCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}
	

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ECStockCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}
	

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ECStockCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}
	

	@Override
	protected boolean isVerifyFail(DCP_ECStockCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		String ecStockId = request.getEcStockId();
		String docType = request.getDocType();
		String opType = request.getOpType();
		List<level2Elm> goodsDatas = request.getGoodsDatas();

		if (Check.Null(ecStockId)) 
		{
			errMsg.append("上下架ID不可为空值, ");
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
	protected TypeToken<DCP_ECStockCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ECStockCreateReq>(){};
	}
	

	@Override
	protected DCP_ECStockCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ECStockCreateRes();
	}
	

	private boolean checkExist(DCP_ECStockCreateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String shopId=req.getShopId();
		level1Elm request = req.getRequest();
		String ecStockId = request.getEcStockId();	
		sql = " select * from DCP_ECSTOCK where EID='"+eId+"' and SHOPID='"+shopId+"' and ecstockid='"+ecStockId+"'  ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}
	


	private String getEcStockNo(DCP_ECStockCreateReq req) throws Exception{
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String sql = null;
		String ecStockNo = null;
		String eId = req.geteId();
		String shopId = req.getShopId();
		String bDate= PosPub.getAccountDate_SMS(dao, eId, shopId);
		ecStockNo = "DSKC" + bDate;

		sql=" select max(ECSTOCKNO) as ECSTOCKNO  from DCP_ECSTOCK "
				+ " where EID='"+eId+"' and SHOPID='"+shopId+"' and  ECSTOCKNO like '%%" + ecStockNo + "%%' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		if (getQData != null && getQData.isEmpty() == false) 
		{
			ecStockNo = getQData.get(0).get("ECSTOCKNO").toString();
			if (ecStockNo != null && ecStockNo.length() > 0) 
			{
				long i;
				ecStockNo = ecStockNo.substring(4, ecStockNo.length());
				i = Long.parseLong(ecStockNo) + 1;
				ecStockNo = i + "";
				ecStockNo = "DSKC" + ecStockNo;    
			} 
			else 
			{
				ecStockNo = "DSKC" + bDate + "00001";
			}
		} 
		else 
		{
			ecStockNo = "DSKC" + bDate + "00001";
		}

		return ecStockNo;
	}

}
