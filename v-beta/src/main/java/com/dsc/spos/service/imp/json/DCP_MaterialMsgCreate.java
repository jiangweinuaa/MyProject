package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MaterialMsgCreateReq;
import com.dsc.spos.json.cust.req.DCP_MaterialMsgCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MaterialMsgCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 物料报单新增： 页面上选择商品添加时， 调用该服务
 * @author yuanyy 2019-10-28
 *
 */
public class DCP_MaterialMsgCreate extends SPosAdvanceService<DCP_MaterialMsgCreateReq, DCP_MaterialMsgCreateRes> {

	@Override
	protected void processDUID(DCP_MaterialMsgCreateReq req, DCP_MaterialMsgCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
//			String bDate = req.getbDate();
			String eId = req.geteId();
			String shopId = req.getShopId();
			String pfNo = req.getRequest().getPfNo();
			int maxItem = Integer.parseInt(req.getRequest().getMaxItem().toString());
			
			String[] columns2 = {
					"EID", "SHOPID", "ORGANIZATIONNO", "PFNO", "ITEM", "PLUNO", "PUNIT", "PQTY", "WUNIT", 
					"WQTY", "PRICE", "AMT", "REQWQTY","KQTY", //13
					"KADJQTY","REF_AMT","ADJ_AMT","MEMO", "ISCLEAR", "LASTSALETIME" , "PRESALEQTY", "TRUEQTY"
			};
			
			// 需要查库存量  和  昨日销量
			List<DCP_MaterialMsgCreateReq.level1Elm> detailDatas = req.getRequest().getDatas();
			if(detailDatas.size() > 0){
				for (level1Elm map : detailDatas) {
					
					maxItem = maxItem + 1; 
					
					String materialPluNo = map.getMaterialPluNo();
					String pUnit = map.getpUnit();
					String wUnit = map.getwUnit();
					String price = map.getPrice();
					
					DataValue[] insValue2 = null;
					
					// 插入主表数据
					insValue2 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR),
							new DataValue(pfNo, Types.VARCHAR), 
							new DataValue(maxItem, Types.VARCHAR), 
							new DataValue(materialPluNo, Types.VARCHAR),
							new DataValue(pUnit, Types.VARCHAR),
							new DataValue("0",  Types.VARCHAR),  
							new DataValue(wUnit, Types.VARCHAR), 
							new DataValue("0", Types.VARCHAR), 
							new DataValue(price, Types.VARCHAR),  
							new DataValue("0", Types.VARCHAR), //amt 
							new DataValue("0", Types.VARCHAR), //reqWQty 
							new DataValue("0", Types.VARCHAR), //kqty 
							new DataValue("0", Types.VARCHAR),  
							new DataValue("0", Types.VARCHAR), 
							new DataValue("0", Types.VARCHAR),  
							new DataValue("", Types.VARCHAR),//memo
							new DataValue("N", Types.VARCHAR),//isClear
							new DataValue("", Types.VARCHAR),//lastSaleTime
							new DataValue("0", Types.VARCHAR),//昨日销量
							new DataValue("0", Types.VARCHAR)
							
					};
					
					InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
				}
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MaterialMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MaterialMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MaterialMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MaterialMsgCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MaterialMsgCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MaterialMsgCreateReq>(){};
	}

	@Override
	protected DCP_MaterialMsgCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MaterialMsgCreateRes();
	}
	
}
