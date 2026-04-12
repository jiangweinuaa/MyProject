package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MiniChargeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_MiniChargeUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MiniChargeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 低消信息新增
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeUpdate extends SPosAdvanceService<DCP_MiniChargeUpdateReq, DCP_MiniChargeUpdateRes> {

	@Override
	protected void processDUID(DCP_MiniChargeUpdateReq req, DCP_MiniChargeUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String eId = req.geteId(); 
			String miniChargeNO = req.getRequest().getMiniChargeNo();
			String mcType = req.getRequest().getMcType();
			String adultQty = req.getRequest().getAdultQty();
			String childQty = req.getRequest().getChildQty();
			String priceClean = req.getRequest().getPriceClean();
			String amtMini = req.getRequest().getAmtMini();

			String opNO = req.getOpNO();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
			String modifyDate = dfDate.format(cal.getTime());
			String modifyTime = dfTime.format(cal.getTime());
			String status = req.getRequest().getStatus();

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_MINICHARGE");
			ub1.addUpdateValue("MCTYPE", new DataValue(mcType, Types.INTEGER));
			ub1.addUpdateValue("ADULT_QTY", new DataValue(adultQty, Types.VARCHAR));
			ub1.addUpdateValue("CHILD_QTY", new DataValue(childQty,Types.VARCHAR));
			ub1.addUpdateValue("PRICE_CLEAN", new DataValue(priceClean, Types.VARCHAR));
			ub1.addUpdateValue("AMT_MINI", new DataValue(amtMini,Types.VARCHAR));
			ub1.addUpdateValue("MODIFYBY", new DataValue(opNO,Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate,Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime,Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));

			ub1.addCondition("MINICHARGENO", new DataValue(miniChargeNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			DelBean db1 = new DelBean("DCP_MINICHARGE_DETAIL");
			db1.addCondition("MINICHARGENO", new DataValue(miniChargeNO, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));		

			List<level1Elm> datas = req.getRequest().getDatas();
			//			if(datas.isEmpty())
			//			{
			//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务费详细信息为空!!");
			//			}
			for (level1Elm par : datas) {
				int insColCt = 0;  
				String[] columnsName = {
						"EID","MINICHARGENO","PLUNO"
				};

				String pluNO = par.getPluNo();
				DataValue[] columnsVal = new DataValue[columnsName.length];
				for (int i = 0; i < columnsVal.length; i++) { 
					String keyVal = null;
					switch (i) { 
					case 0:
						keyVal = eId;
						break;
					case 1:
						keyVal = miniChargeNO;
						break;
					case 2:
						keyVal = pluNO;
						break;
					default:
						break;
					}
					if (keyVal != null) 
					{
						insColCt++;
						columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
					} 
					else 
					{
						columnsVal[i] = null;
					}

				}
				String[] columns2  = new String[insColCt];
				DataValue[] insValue2 = new DataValue[insColCt];
				// 依照傳入參數組譯要insert的欄位與數值；
				insColCt = 0;

				for (int i=0;i<columnsVal.length;i++){
					if (columnsVal[i] != null){
						columns2[insColCt] = columnsName[i];
						insValue2[insColCt] = columnsVal[i];
						insColCt ++;
						if (insColCt >= insValue2.length) 
							break;
					}
				}
				InsBean ib2 = new InsBean("DCP_MINICHARGE_DETAIL", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2));

			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MiniChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MiniChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MiniChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MiniChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String miniChargeNO = req.getRequest().getMiniChargeNo();

		if (Check.Null(miniChargeNO)  ) 
		{
			errMsg.append("编号不能为空！ ");
			isFail = true;
		}	

		List<level1Elm> jsonDatas = req.getRequest().getDatas();
		for (level1Elm par : jsonDatas){ 
			//keyName必须为数值型

			String pluNO = par.getPluNo();

			if (Check.Null(pluNO)  ) 
			{
				errMsg.append("商品编号不能为空！ ");
				isFail = true;
			}	

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_MiniChargeUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MiniChargeUpdateReq>(){};
	}

	@Override
	protected DCP_MiniChargeUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MiniChargeUpdateRes();
	}

}
