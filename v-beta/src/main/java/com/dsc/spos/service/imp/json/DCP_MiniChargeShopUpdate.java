package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MiniChargeShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_MiniChargeShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MiniChargeShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务费修改
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeShopUpdate extends SPosAdvanceService<DCP_MiniChargeShopUpdateReq, DCP_MiniChargeShopUpdateRes> {

	@Override
	protected void processDUID(DCP_MiniChargeShopUpdateReq req, DCP_MiniChargeShopUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String eId = req.geteId(); 
			String miniChargeNO = req.getRequest().getMiniChargeNo();
			List<DCP_MiniChargeShopUpdateReq.level1Elm> jsonData=req.getRequest().getDatas();
			//先删除原来单身
			DelBean db1 = new DelBean("DCP_MINICHARGE_SHOP");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("MINICHARGENO", new DataValue(miniChargeNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));			
			this.doExecuteDataToDB();
			for (DCP_MiniChargeShopUpdateReq.level1Elm level1Elm : jsonData) 
			{
				//新增SQL
				int insColCt = 0;
				String[] columnsModularDetail ={"EID","MINICHARGENO","SHOPID"};
				DataValue[] columnsVal = new DataValue[columnsModularDetail.length];
				for (int i = 0; i < columnsVal.length; i++)
				{
					String keyVal = null;
					switch (i) 
					{
					case 0:
						keyVal=eId;
						break;
					case 1:
						keyVal=miniChargeNO;
						break;
					case 2:
						keyVal=level1Elm.getShopId();
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

				String[] columns2 = new String[insColCt];
				DataValue[] insValue2 = new DataValue[insColCt];

				insColCt = 0;

				for (int i = 0; i < columnsVal.length; i++) 
				{
					if (columnsVal[i] != null) 
					{
						columns2[insColCt] = columnsModularDetail[i];
						insValue2[insColCt] = columnsVal[i];
						insColCt++;
						if (insColCt >= insValue2.length)
							break;
					}
				}

				InsBean ib2 = new InsBean("DCP_MINICHARGE_SHOP", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2));	
			}

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch(Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MiniChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MiniChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MiniChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MiniChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    
	    List<level1Elm> datas = req.getRequest().getDatas();
	    String miniChargeNO = req.getRequest().getMiniChargeNo();
	    
		if (Check.Null(miniChargeNO)) 
		{
			errMsg.append("编码不能为空值 ");
			isFail = true;
		}
		
	    for(level1Elm par : datas){
			
			if (Check.Null(par.getShopId())) 
			{
				errMsg.append("门店编号不能为空值 ");
				isFail = true;
			}
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_MiniChargeShopUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MiniChargeShopUpdateReq>(){};
	}

	@Override
	protected DCP_MiniChargeShopUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MiniChargeShopUpdateRes();
	}


}
