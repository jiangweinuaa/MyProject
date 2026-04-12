package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockOutDeleteReq;
import com.dsc.spos.json.cust.req.DCP_StockOutDeleteReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockOutDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：StockOutDelete
 *    說明：出货单删除
 * 服务说明：出货单删除
 * @author panjing 
 * @since  2016-09-28
 */
public class DCP_StockOutDelete extends SPosAdvanceService<DCP_StockOutDeleteReq, DCP_StockOutDeleteRes> 
{

	@Override
	protected boolean isVerifyFail(DCP_StockOutDeleteReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		levelElm request = req.getRequest();

		String stockOutNO = request.getStockOutNo();		

		if (Check.Null(stockOutNO)) {
			isFail = true;
			errMsg.append("出货单据编号不可为空值, ");
		}  

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockOutDeleteReq> getRequestType() {
		return new TypeToken<DCP_StockOutDeleteReq>(){};
	}

	@Override
	protected DCP_StockOutDeleteRes getResponseType() {
		return new DCP_StockOutDeleteRes();
	}

	@Override
	protected void processDUID(DCP_StockOutDeleteReq req,DCP_StockOutDeleteRes res) throws Exception {
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		levelElm request = req.getRequest();	
		String stockOutNO = request.getStockOutNo();

		try 
		{
			//查詢單據狀態
			String sql = "select status,otype,ofno "
					+ " from DCP_Stockout "
					+ " where SHOPID = '"+shopId+"' AND OrganizationNO = '"+organizationNO+"' "
					+ " AND EID = '"+eId+"' and stockOutNO = '"+stockOutNO+"' ";

			List<Map<String, Object>> getQData = this.doQueryData(sql,null);

			if (getQData != null && getQData.isEmpty() == false) 
			{ 
				String status=getQData.get(0).get("STATUS").toString();
                String oType = getQData.get(0).get("OTYPE").toString();
                //String ofNo = getQData.get(0).get("OFNO").toString();
                if (status.equals("-1"))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批中，不能删除！");
				}

				if (status.equals("0"))
				{
					//樣板 刪除行為
					DelBean db1 = new DelBean("DCP_StockOut");
					db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					db1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					DelBean db2 = new DelBean("DCP_StockOut_Detail");
					db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
					db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
					db2.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db2));

                    db2 = new DelBean("DCP_STOCKOUT_DETAIL_IMAGE");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db2.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                    DelBean db3 = new DelBean("DCP_STOCKOUT_BATCH");
                    db3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db3.addCondition("STOCKOUTNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db3));

                    DelBean db4 = new DelBean("DCP_STOCKOUT_DETAIL_LOCATION");
                    db4.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db4.addCondition("STOCKOUTNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db4));

                    if(oType.equals("3")||oType.equals("4")||oType.equals("5")||oType.equals("6")) {

						//先把单身存在的ofno弄出来
						String detailSql="select distinct a.ofno " +
                                " from dcp_stockout_detail a where a.eid='"+eId+"'" +
								" and a.organizationno='"+organizationNO+"' " +
								" and a.stockoutno='"+stockOutNO+"'";
						List<Map<String, Object>> ofList = this.doQueryData(detailSql, null);

						for (Map<String, Object> ofData : ofList){
							String ofNo = ofData.get("OFNO").toString();
							String upSql="select nvl(b.stockoutno,'') as stockoutno, a.billno,a.item,nvl(a.STOCKOUTQTY,0) as STOCKOUTQTY,nvl(c.pqty,0) as pqty,nvl(a.pqty,0) as npqty,a.status,b.status as STOCKOUTSTATUS " +
									" from DCP_STOCKOUTNOTICE_DETAIL a " +
									" left join dcp_stockout_detail c on a.eid=c.eid  and a.item=c.oitem and a.billno=c.ofno " +
									" left join dcp_stockout b on a.eid=b.eid and c.organizationno=b.organizationno and c.stockoutno=b.stockoutno " +
									" where a.eid='"+eId+"'  and a.billno='"+ofNo+"' ";
							List<Map<String, Object>> getUpDetail = this.doQueryData(upSql, null);

							List detailStatusList=new ArrayList();
							for (Map<String, Object> oneData : getUpDetail) {
								String billNo = oneData.get("BILLNO").toString();
								String nStatus = oneData.get("STATUS").toString();
								String item = oneData.get("ITEM").toString();
								String stockOutStatus = oneData.get("STOCKOUTSTATUS").toString();
								String nowStockoutNo = oneData.get("STOCKOUTNO").toString();
								BigDecimal stockoutQty = new BigDecimal(oneData.get("STOCKOUTQTY").toString());
								//BigDecimal pQty = new BigDecimal(oneData.get("PQTY").toString());
								BigDecimal npQty = new BigDecimal(oneData.get("NPQTY").toString());
								String detailStatus="";
								if(Check.Null(billNo)||Check.Null(item)){
									continue;
								}
								if("2".equals(stockOutStatus)){
									continue;
								}
								if(!nowStockoutNo.equals(stockOutNO)){
									//该条不是这次出库单的
									if(!detailStatusList.contains(nStatus)){
										detailStatusList.add(nStatus);
									}
									continue;
								}
								//删除和这张出库单的pqty没关系
								//stockoutQty = stockoutQty.add(pQty);//
								if(stockoutQty.compareTo(npQty)<0){
									detailStatus="1";
								}else{
									detailStatus="2";
								}
								if(!detailStatusList.contains(detailStatus)){
									detailStatusList.add(detailStatus);
								}

								UptBean upNotice=new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
								upNotice.addUpdateValue("STATUS", new DataValue(Float.parseFloat(detailStatus), Types.VARCHAR));
								upNotice.addUpdateValue("STOCKOUTQTY", new DataValue(stockoutQty, Types.VARCHAR));
								// condition
								//upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
								upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								upNotice.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
								upNotice.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
								this.addProcessData(new DataProcessBean(upNotice));


							}

							if(detailStatusList.size()>1){
                                // 存在行状态=【4-出货中】/【3-已派工】，则单据状态=【4-出货中】
                                if(detailStatusList.contains("3")||detailStatusList.contains("4")) {

                                    UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                    upNotice.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));
                                    // condition
                                    //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                    upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    upNotice.addCondition("BILLNO", new DataValue(ofNo, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(upNotice));
                                }else{

                                    UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                    upNotice.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
                                    // condition
                                    //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                    upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    upNotice.addCondition("BILLNO", new DataValue(ofNo, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(upNotice));
                                }


                            }else{
								if (detailStatusList.size()==1){
									if(detailStatusList.get(0).equals("1")){
										UptBean upNotice=new UptBean("DCP_STOCKOUTNOTICE");
										upNotice.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
										// condition
										//upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
										upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
										upNotice.addCondition("BILLNO", new DataValue(ofNo, Types.VARCHAR));
										this.addProcessData(new DataProcessBean(upNotice));
									}else if(detailStatusList.get(0).equals("2")){
										UptBean upNotice=new UptBean("DCP_STOCKOUTNOTICE");
										upNotice.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
										// condition
										//upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
										upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
										upNotice.addCondition("BILLNO", new DataValue(ofNo, Types.VARCHAR));
										this.addProcessData(new DataProcessBean(upNotice));
									}
								}
							}
						}

                    }

                    //删除内部结算
                    ColumnDataValue condition = new ColumnDataValue();
                    condition.add("EID", req.geteId());
                    condition.add("BILLNO", req.getRequest().getStockOutNo());

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
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockOutDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockOutDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockOutDeleteReq req) throws Exception {
		return null;
	}

}
