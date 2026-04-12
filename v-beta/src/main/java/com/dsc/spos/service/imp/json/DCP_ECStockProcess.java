package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ECStockProcessReq;
import com.dsc.spos.json.cust.req.DCP_ECStockProcessReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ECStockProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ECStockProcess
 * 服务说明：电商平台库存上下架提交
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockProcess extends SPosAdvanceService<DCP_ECStockProcessReq,DCP_ECStockProcessRes>{

	@Override
	protected void processDUID(DCP_ECStockProcessReq req, DCP_ECStockProcessRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId=req.geteId();
		String shopId=req.getShopId();
		String ecStockNo = req.getRequest().getEcStockNo();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		String accountDate=sdf.format(cal.getTime());
		String opNo=req.getOpNO();
		String opName=req.getOpName();

		try
		{
			String sql=" select A.*,B.ECPLATFORMNO,B.ITEM,B.PLUNO,B.PLUBARCODE,B.UNIT,B.ALLQTY,B.QTY,C.SHOPID AS ECPLATFORMSHOPNO from DCP_ECSTOCK a "
					+ " inner join DCP_ECSTOCK_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.ECSTOCKNO=B.ECSTOCKNO "
					+ " inner join OC_ECOMMERCE C ON C.EID=A.EID AND C.ECPLATFORMNO=B.ECPLATFORMNO "
					+ " WHERE A.EID='"+eId+"' AND A.SHOPID='"+shopId+"' AND A.ECSTOCKNO='"+ecStockNo+"' and A.STATUS='0' ";

			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && getQData.isEmpty() == false) 
			{
				// 更新电商平台实时库存
				JSONObject json = new JSONObject();
				JSONObject jsonDatas = new JSONObject();
				JSONArray datas = new JSONArray();
				JSONArray detail = new JSONArray();

				for (Map<String, Object> oneDate : getQData)
				{
					String ecPlatformNO = oneDate.get("ECPLATFORMNO").toString();
					String ecPlatformShopNo = oneDate.get("ECPLATFORMSHOPNO").toString();    
					String pluNo=oneDate.get("PLUNO").toString();
					String pluBarcode=oneDate.get("PLUBARCODE").toString();
					String unit = oneDate.get("UNIT").toString();
					String qty = oneDate.get("QTY").toString();	
					String item = oneDate.get("ITEM").toString();	

					JSONObject jsonDetail = new JSONObject();
					jsonDetail.put("loadDocItem",item );
					jsonDetail.put("ecPlatformNO",ecPlatformNO );
					jsonDetail.put("shopId",ecPlatformShopNo );
					jsonDetail.put("pluNO",pluNo );
					jsonDetail.put("pluBarcode",pluBarcode );
					jsonDetail.put("unit",unit );
					jsonDetail.put("qty",qty );
					detail.add(jsonDetail);
				}

				String docType = getQData.get(0).get("DOCTYPE").toString();	

				jsonDatas.put("eId",eId );
				jsonDatas.put("ecStockNO",ecStockNo );
				jsonDatas.put("docType", docType);
				jsonDatas.put("opNo",opNo );
				jsonDatas.put("loadType","0" );   // 0手动上下架 1调拨收货 2订单 3调拨出库 
				jsonDatas.put("loadDocno",ecStockNo );
				jsonDatas.put("loadDocShop",shopId );
				jsonDatas.put("detail", detail);
				datas.add(jsonDatas);
				json.put("datas", datas);

				String jsonStr=json.toString();
				List<DataProcessBean> dataProcessBeans= PosPub.UpdateEC_Stock(dao, jsonStr);
				if (dataProcessBeans!=null && dataProcessBeans.isEmpty()==false)
				{
					for (DataProcessBean dataProcessBean:dataProcessBeans)
					{
						this.addProcessData(dataProcessBean);
					}
				}
				else
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "更新电商平台实时库存失败");		
				}


				//单据状态变更   
				UptBean ub = new UptBean("DCP_ECSTOCK");
				ub.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.DATE));
				ub.addUpdateValue("ACCOUNTBY", new DataValue(opNo, Types.VARCHAR));
				ub.addUpdateValue("ACCOUNTBYNAME", new DataValue(opName, Types.VARCHAR));
				ub.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
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
	protected List<InsBean> prepareInsertData(DCP_ECStockProcessReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ECStockProcessReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ECStockProcessReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ECStockProcessReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		String ecStockNo = request.getEcStockNo();

		if (Check.Null(ecStockNo)) 
		{
			errMsg.append("上下架编号不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_ECStockProcessReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ECStockProcessReq>(){};
	}

	@Override
	protected DCP_ECStockProcessRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ECStockProcessRes();
	}

}
