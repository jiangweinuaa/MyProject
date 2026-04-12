package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECFreeGoodsCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderECFreeGoodsCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderECFreeGoodsCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 赠品添加
 * @author yunayy 2019-06-20
 *
 */
public class DCP_OrderECFreeGoodsCreate extends SPosAdvanceService<DCP_OrderECFreeGoodsCreateReq, DCP_OrderECFreeGoodsCreateRes> 
{

	//處理訂單以配送門店為歸屬門店
	String myshop ="";
			
	@Override
	protected void processDUID(DCP_OrderECFreeGoodsCreateReq req, DCP_OrderECFreeGoodsCreateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		String eId = req.geteId();
		myshop = req.getShopId();
		String ecOrderNo = req.getEcOrderNo();
		String ecPlatformNo = req.getEcPlatformNo();
		
		//新增单身（多笔）
		List<level1Elm> datas = req.getDatas();

		//處理訂單以配送門店為歸屬門店
		String itemStr = this.queryMaxItem(req);
		int item = Integer.parseInt(itemStr);
		
		for (level1Elm par : datas) {
			int insColCt = 0;  
			String pluNo = par.getPluNo();
			String pluName = par.getPluName();
			String qty = par.getQty();
			
			String[] columnsName = {
					"EID", "CUSTOMERNO",  "SHOPID" , "ORDERNO",
					"ITEM","PLUNO","PLUBARCODE","PLUNAME","PRICE","QTY","DISC",
					"BOXNUM", "BOXPRICE", "AMT","RCQTY" , "LOAD_DOCTYPE" ,"ORGANIZATIONNO"
			};
			DataValue[] columnsVal = new DataValue[columnsName.length];

			for (int i = 0; i < columnsVal.length; i++) { 
				String keyVal = null;
				switch (i) { 
				case 0:
					keyVal = eId;
					break;
				case 1:
					keyVal = " ";
					break;
				case 2:
					keyVal = myshop;
					break;
				case 3:  
					keyVal = ecOrderNo; 
					break;
				case 4:  
					keyVal = item + "";
					break;
				case 5:
					keyVal = pluNo;
					break;
				case 6:
					keyVal = pluNo;
					break;
				case 7:
					keyVal = pluName;
					break;
				case 8:
					keyVal = "0"; // price
					break;
				case 9:
					keyVal = qty; // qty
					break;
				case 10:
					keyVal = "0"; // disc
					break;
				case 11:
					keyVal = "0"; // boxNum
					break;
				case 12:
					keyVal = "0"; // boxPrice
					break;
				case 13:
					keyVal = "0"; // amt
					break;
				case 14:
					keyVal = "0"; // rcqty
					break;
				case 15:
					keyVal = ecPlatformNo; // load_docType 来源类型 
					break;
				case 16:
					keyVal = myshop;
					break;
				default:
					break;
				}

				if (keyVal != null) {
					insColCt++;
					if (i == 8 || i == 9 || i == 12 ){
						columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
					}else{
						columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
					}
				}
				else {
					columnsVal[i] = null;
				}
			}
			

			String[] columns2  = new String[insColCt];
			DataValue[] insValue2 = new DataValue[insColCt];
			// 依照傳入參數組譯要insert的欄位與數值；
			insColCt = 0;

			for (int i = 0;i<columnsVal.length;i++){
				if (columnsVal[i] != null){
					columns2[insColCt] = columnsName[i];
					insValue2[insColCt] = columnsVal[i];
					insColCt ++;
					if (insColCt >= insValue2.length) 
						break;
				}
			}
			InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
			ib2.addValues(insValue2);
			this.addProcessData(new DataProcessBean(ib2));
			item++;
			
		}
		
		this.doExecuteDataToDB();
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECFreeGoodsCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECFreeGoodsCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECFreeGoodsCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECFreeGoodsCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECFreeGoodsCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECFreeGoodsCreateReq>(){};
	}

	@Override
	protected DCP_OrderECFreeGoodsCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECFreeGoodsCreateRes();
	}
	
	protected String queryMaxItem(DCP_OrderECFreeGoodsCreateReq req ) throws Exception{
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String ecOrderNo = req.getEcOrderNo();
		String ecPlatformNo = req.getEcPlatformNo();
		
		sqlbuf.append("select  nvl(MAX(item)+1,'1') AS item,b.SHOPID  from OC_order_detail a inner join OC_order b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.orderno=b.orderno "
				+ "where a.EID = '"+eId+"' and (b.SHOPID = '"+shopId+"' or b.shippingshop='"+shopId+"') "
			    + " and a.orderNo = '"+ecOrderNo+"' and a.load_docType = '"+ecPlatformNo+"' "
			    + " group by b.SHOPID ");
		
		sql = sqlbuf.toString();
		
		String item = "1";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		if (getQData != null && getQData.isEmpty() == false) 
		{
			item = getQData.get(0).get("ITEM").toString();
			myshop=getQData.get(0).get("SHOPID").toString();
		}
		
		return item;
	}
	
	
	
	
	
}
