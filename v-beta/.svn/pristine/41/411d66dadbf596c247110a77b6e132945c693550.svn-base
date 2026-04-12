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
import com.dsc.spos.json.cust.req.DCP_PinPeiUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PinPeiUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PinPeiUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_PinPeiCreate
 * 服务说明：拼胚修改
 * @author jinzma
 * @since  2020-07-13
 */
public class DCP_PinPeiUpdate  extends SPosAdvanceService<DCP_PinPeiUpdateReq,DCP_PinPeiUpdateRes>{

	@Override
	protected void processDUID(DCP_PinPeiUpdateReq req, DCP_PinPeiUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId=req.geteId();
		String shopId=req.getShopId();
		String modifyBy = req.getOpNO();
		String modifyByName = req.getOpName();
		String modifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		try
		{
			String pinPeiNo=req.getRequest().getPinPeiNo();
			String sql = " select pinpeino from dcp_pinpei "
					+ " where eid='"+eId+"' and shopid='"+shopId+"' and pinpeino='"+pinPeiNo+"' and status='0' ";
			List<Map<String, Object>> getQData = this.doQueryData(sql,null);
			if (getQData != null && getQData.isEmpty()==false)
			{
				String bDate=req.getRequest().getbDate();
				String stockInWearehouse=req.getRequest().getStockInWearehouse();
				String stockOutWearehouse=req.getRequest().getStockOutWearehouse();
				String stockInBsNo=req.getRequest().getStockInBsNo();
				String stockOutBsNo=req.getRequest().getStockOutBsNo();
				String totPqty=req.getRequest().getTotPqty();
				String totAmt=req.getRequest().getTotAmt();
				String totDistriAmt=req.getRequest().getTotDistriAmt();
				String totCqty=req.getRequest().getTotCqty();
				String memo=req.getRequest().getMemo();

				//删除单据单身
				DelBean db = new DelBean("DCP_PINPEI_DETAIL");
				db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db.addCondition("PINPEINO", new DataValue(pinPeiNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db));

				//新增单据单身
				String[] columnsDetail = {
						"EID","SHOPID","PINPEINO","STOCKTYPE",
						"ITEM","PLUNO","WAREHOUSE","BATCH_NO","PROD_DATE",
						"PUNIT","BASEUNIT","PQTY","BASEQTY","UNIT_RATIO",
						"PRICE","DISTRIPRICE","AMT","DISTRIAMT",
						"PLU_MEMO","FEATURENO",
				};
				int item=1;
				List<level1Elm> stockInPluList=req.getRequest().getStockInPluList();
				for (level1Elm par : stockInPluList) {
					String pluNo=par.getPluNo();
					String punit=par.getPunit();
					String baseUnit=par.getBaseUnit();
					String unitRatio=par.getUnitRatio();
					String pqty=par.getPqty();
					String baseQty=par.getBaseQty();
					String price=par.getPrice();
					String distriPrice=par.getDistriPrice();
					String amt=par.getAmt();
					String distriAmt=par.getDistriAmt();
					String batchNo=par.getBatchNo();
					String prodDate=par.getProdDate();
					String pluMemo=par.getPluMemo();
					String featureNo = par.getFeatureNo();
					if (Check.Null(featureNo))
						featureNo=" ";

					DataValue[]	insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR),
							new DataValue(pinPeiNo, Types.VARCHAR),
							new DataValue("STOCKIN", Types.VARCHAR),    //STOCKTYPE字段：STOCKIN入库
							new DataValue(item, Types.VARCHAR),
							new DataValue(pluNo, Types.VARCHAR),
							new DataValue(stockInWearehouse, Types.VARCHAR),
							new DataValue(batchNo, Types.VARCHAR),
							new DataValue(prodDate, Types.VARCHAR),
							new DataValue(punit, Types.VARCHAR),
							new DataValue(baseUnit, Types.VARCHAR),
							new DataValue(pqty, Types.VARCHAR),
							new DataValue(baseQty, Types.VARCHAR),
							new DataValue(unitRatio, Types.VARCHAR),
							new DataValue(price, Types.VARCHAR),
							new DataValue(distriPrice, Types.VARCHAR),
							new DataValue(amt, Types.VARCHAR),
							new DataValue(distriAmt, Types.VARCHAR),
							new DataValue(pluMemo, Types.VARCHAR),
							new DataValue(featureNo, Types.VARCHAR),
					};
					InsBean ib = new InsBean("DCP_PINPEI_DETAIL", columnsDetail);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib));
					item++;
				}

				List<level1Elm> stockOutPluList=req.getRequest().getStockOutPluList();
				for (level1Elm par : stockOutPluList) {
					String pluNo=par.getPluNo();
					String punit=par.getPunit();
					String baseUnit=par.getBaseUnit();
					String unitRatio=par.getUnitRatio();
					String pqty=par.getPqty();
					String baseQty=par.getBaseQty();
					String price=par.getPrice();
					String distriPrice=par.getDistriPrice();
					String amt=par.getAmt();
					String distriAmt=par.getDistriAmt();
					String batchNo=par.getBatchNo();
					String prodDate=par.getProdDate();
					String pluMemo=par.getPluMemo();
					String featureNo = par.getFeatureNo();
					if (Check.Null(featureNo))
						featureNo=" ";

					DataValue[]	insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR),
							new DataValue(pinPeiNo, Types.VARCHAR),
							new DataValue("STOCKOUT", Types.VARCHAR),         //STOCKTYPE字段：STOCKOUT出库
							new DataValue(item, Types.VARCHAR),
							new DataValue(pluNo, Types.VARCHAR),
							new DataValue(stockOutWearehouse, Types.VARCHAR),
							new DataValue(batchNo, Types.VARCHAR),
							new DataValue(prodDate, Types.VARCHAR),
							new DataValue(punit, Types.VARCHAR),
							new DataValue(baseUnit, Types.VARCHAR),
							new DataValue(pqty, Types.VARCHAR),
							new DataValue(baseQty, Types.VARCHAR),
							new DataValue(unitRatio, Types.VARCHAR),
							new DataValue(price, Types.VARCHAR),
							new DataValue(distriPrice, Types.VARCHAR),
							new DataValue(amt, Types.VARCHAR),
							new DataValue(distriAmt, Types.VARCHAR),
							new DataValue(pluMemo, Types.VARCHAR),
							new DataValue(featureNo, Types.VARCHAR),
					};
					InsBean ib = new InsBean("DCP_PINPEI_DETAIL", columnsDetail);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib));
					item++;
				}

				UptBean ub = new UptBean("DCP_PINPEI");
				ub.addUpdateValue("BDATE", new DataValue(bDate, Types.VARCHAR));
				ub.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
				ub.addUpdateValue("STOCKINWEAREHOUSE", new DataValue(stockInWearehouse, Types.VARCHAR));
				ub.addUpdateValue("STOCKOUTWEAREHOUSE", new DataValue(stockOutWearehouse, Types.VARCHAR));
				ub.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
				ub.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
				ub.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
				ub.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
				ub.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
				ub.addUpdateValue("LASTMODIOPID", new DataValue(modifyBy, Types.VARCHAR));
				ub.addUpdateValue("LASTMODIOPNAME", new DataValue(modifyByName, Types.VARCHAR));
				ub.addUpdateValue("LASTMODITIME", new DataValue(modifyTime, Types.DATE));
				ub.addUpdateValue("STOCKINBSNO", new DataValue(stockInBsNo, Types.VARCHAR));
				ub.addUpdateValue("STOCKOUTBSNO", new DataValue(stockOutBsNo, Types.VARCHAR));


				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				ub.addCondition("PINPEINO", new DataValue(pinPeiNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新输入！");
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PinPeiUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PinPeiUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PinPeiUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PinPeiUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String pinPeiNo=req.getRequest().getPinPeiNo();
		String bDate=req.getRequest().getbDate();
		String stockInWearehouse=req.getRequest().getStockInWearehouse();
		String stockOutWearehouse=req.getRequest().getStockOutWearehouse();
		String stockInBsNo=req.getRequest().getStockInBsNo();
		String stockOutBsNo=req.getRequest().getStockOutBsNo();
		String totPqty=req.getRequest().getTotPqty();
		String totAmt=req.getRequest().getTotAmt();
		String totDistriAmt=req.getRequest().getTotDistriAmt();
		String totCqty=req.getRequest().getTotCqty();
		List<level1Elm> stockInPluList=req.getRequest().getStockInPluList();
		List<level1Elm> stockOutPluList=req.getRequest().getStockOutPluList();

		if (Check.Null(pinPeiNo))
		{
			errMsg.append("单据编号不能为空,");
			isFail = true;
		}
		if (Check.Null(bDate))
		{
			errMsg.append("单据日期不能为空,");
			isFail = true;
		}
		if (Check.Null(stockInWearehouse))
		{
			errMsg.append("入库仓库不能为空,");
			isFail = true;
		}
		if (Check.Null(stockOutWearehouse))
		{
			errMsg.append("出库仓库不能为空,");
			isFail = true;
		}
		if (Check.Null(stockInBsNo))
		{
			errMsg.append("入库理由码不能为空,");
			isFail = true;
		}
		if (Check.Null(stockOutBsNo))
		{
			errMsg.append("出库理由码不能为空,");
			isFail = true;
		}
		if(stockOutPluList==null || stockOutPluList.isEmpty())
		{
			errMsg.append("出库商品列表不能为空,");
			isFail = true;
		}
		if(stockInPluList==null || stockInPluList.isEmpty())
		{
			errMsg.append("入库商品列表不能为空,");
			isFail = true;
		}
		if (Check.Null(totPqty)) {
			errMsg.append("合计录入数量不可为空值, ");
			isFail = true;
		}
		if (Check.Null(totAmt)) {
			errMsg.append("合计录入数量不可为空值, ");
			isFail = true;
		}
		if (Check.Null(totDistriAmt)) {
			errMsg.append("合计进货金额可为空值, ");
			isFail = true;
		}
		if (Check.Null(totCqty)) {
			errMsg.append("合计品种数量不可为空值, ");
			isFail = true;
		}
		for (level1Elm par:stockInPluList)
		{
			String pluNo=par.getPluNo();
			String punit=par.getPunit();
			String baseUnit=par.getBaseUnit();
			String unitRatio=par.getUnitRatio();
			String pqty=par.getPqty();
			String baseQty=par.getBaseQty();
			String price=par.getPrice();
			String distriPrice=par.getDistriPrice();
			String amt=par.getAmt();
			String distriAmt=par.getDistriAmt();

			if (Check.Null(pluNo))
			{
				errMsg.append("商品编号不能为空,");
				isFail = true;
			}
			if (Check.Null(punit))
			{
				errMsg.append("出入库单位不能为空,");
				isFail = true;
			}
			if (Check.Null(baseUnit))
			{
				errMsg.append("基准单位不能为空,");
				isFail = true;
			}
			if (Check.Null(unitRatio))
			{
				errMsg.append("单位转换率不能为空,");
				isFail = true;
			}
			if (Check.Null(pqty))
			{
				errMsg.append("录入数量不能为空,");
				isFail = true;
			}
			if (Check.Null(baseQty))
			{
				errMsg.append("基准数量不能为空,");
				isFail = true;
			}
			if (Check.Null(price))
			{
				errMsg.append("单价不能为空,");
				isFail = true;
			}
			if (Check.Null(distriPrice))
			{
				errMsg.append("进货价不能为空,");
				isFail = true;
			}
			if (Check.Null(amt))
			{
				errMsg.append("金额不能为空,");
				isFail = true;
			}
			if (Check.Null(distriAmt))
			{
				errMsg.append("进货金额不能为空,");
				isFail = true;
			}

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		for (level1Elm par:stockOutPluList)
		{
			String pluNo=par.getPluNo();
			String punit=par.getPunit();
			String baseUnit=par.getBaseUnit();
			String unitRatio=par.getUnitRatio();
			String pqty=par.getPqty();
			String baseQty=par.getBaseQty();
			String price=par.getPrice();
			String distriPrice=par.getDistriPrice();
			String amt=par.getAmt();
			String distriAmt=par.getDistriAmt();

			if (Check.Null(pluNo))
			{
				errMsg.append("商品编号不能为空,");
				isFail = true;
			}
			if (Check.Null(punit))
			{
				errMsg.append("出入库单位不能为空,");
				isFail = true;
			}
			if (Check.Null(baseUnit))
			{
				errMsg.append("基准单位不能为空,");
				isFail = true;
			}
			if (Check.Null(unitRatio))
			{
				errMsg.append("单位转换率不能为空,");
				isFail = true;
			}
			if (Check.Null(pqty))
			{
				errMsg.append("录入数量不能为空,");
				isFail = true;
			}
			if (Check.Null(baseQty))
			{
				errMsg.append("基准数量不能为空,");
				isFail = true;
			}
			if (Check.Null(price))
			{
				errMsg.append("单价不能为空,");
				isFail = true;
			}
			if (Check.Null(distriPrice))
			{
				errMsg.append("进货价不能为空,");
				isFail = true;
			}
			if (Check.Null(amt))
			{
				errMsg.append("金额不能为空,");
				isFail = true;
			}
			if (Check.Null(distriAmt))
			{
				errMsg.append("进货金额不能为空,");
				isFail = true;
			}

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PinPeiUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_PinPeiUpdateReq>(){};
	}

	@Override
	protected DCP_PinPeiUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_PinPeiUpdateRes();
	}

}
