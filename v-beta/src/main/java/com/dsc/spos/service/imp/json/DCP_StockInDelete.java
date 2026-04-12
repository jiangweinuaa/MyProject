package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockInDeleteReq;
import com.dsc.spos.json.cust.req.DCP_StockInDeleteReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockInDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

public class DCP_StockInDelete extends SPosAdvanceService<DCP_StockInDeleteReq, DCP_StockInDeleteRes> {

	@Override
	protected void processDUID(DCP_StockInDeleteReq req, DCP_StockInDeleteRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		levelElm	request = req.getRequest();
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String stockInNO = request.getStockInNo();

		//try
		//{
			//查詢單據狀態
			String sql = "select status,ofno,doc_type "
					+ " from DCP_Stockin "
					+ " where SHOPID = '"+shopId+"' AND OrganizationNO = '"+organizationNO+"' "
					+ " AND EID = '"+eId+"' and stockinNO = '"+stockInNO+"' ";

			List<Map<String, Object>> getQData = this.doQueryData(sql,null);

			if (getQData != null && getQData.isEmpty() == false) 
			{ 
				String status = getQData.get(0).get("STATUS").toString();
				String docType = getQData.get(0).get("DOC_TYPE").toString();
				String ofNo = getQData.get(0).get("OFNO").toString();

				if (status.equals("0"))
				{					
					//DCP_STOCKIN
					DelBean db1 = new DelBean("DCP_STOCKIN");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
					db1.addCondition("StockInNO", new DataValue(stockInNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					//DCP_STOCKIN_DETAIL
					DelBean db2 = new DelBean("DCP_STOCKIN_DETAIL");
					db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
					db2.addCondition("StockInNO", new DataValue(stockInNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db2));

                    // 更新关联通知单单身 by jinzma 20210422  【ID1017037】【3.0货郎】按商品行收货(DCP_StockInDelete（收货入库单删除）)
                    if ((docType.equals("0")||docType.equals("1")||docType.equals("5")) && !Check.Null(ofNo)) {
                        UptBean ub = new UptBean("DCP_RECEIVING_DETAIL");
                        // add value
                        ub.addUpdateValue("STOCKIN_QTY", new DataValue("0", Types.VARCHAR));
                        ub.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
                        // condition
                        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("RECEIVINGNO", new DataValue(ofNo, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub));

                        UptBean ub2 = new UptBean("DCP_RECEIVING");
                        // add value
                        ub2.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));
                        // condition
                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("RECEIVINGNO", new DataValue(ofNo, Types.VARCHAR));
						ub2.addCondition("RECEIPTORGNO", new DataValue(organizationNO, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub2));

                    }

                    //移仓通知  (发货与收货为同一个组织)
                    if("4".equals(docType)&& !Check.Null(ofNo)){
                        UptBean ub2 = new UptBean("DCP_RECEIVING");
                        ub2.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));

                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("RECEIVINGNO", new DataValue(ofNo, Types.VARCHAR));
                        ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub2));
                    }

                    //删除内部结算
                    ColumnDataValue condition = new ColumnDataValue();
                    condition.add("EID", req.geteId());
                    condition.add("BILLNO", req.getRequest().getStockInNo());

                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));


                    this.doExecuteDataToDB();

					res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");
				}
				else
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已确认，不能删除！");
				}
			}
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，请重新确认！");
			}

		//}
		//catch(Exception e)
		//{
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockInDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockInDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockInDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockInDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		levelElm request = req.getRequest();

		String stockInNO = request.getStockInNo();
		if (Check.Null(stockInNO)) {
			isFail = true;
			errMsg.append("单据编号不可为空值, ");
		}  

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockInDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockInDeleteReq>(){};
	}

	@Override
	protected DCP_StockInDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockInDeleteRes();
	}

}
