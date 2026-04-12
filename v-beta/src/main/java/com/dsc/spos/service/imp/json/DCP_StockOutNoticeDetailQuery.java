package com.dsc.spos.service.imp.json;
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticeDetailQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;

 
public class DCP_StockOutNoticeDetailQuery extends SPosBasicService<DCP_StockOutNoticeDetailQueryReq, DCP_StockOutNoticeDetailQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_StockOutNoticeDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockOutNoticeDetailQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockOutNoticeDetailQueryReq>(){};
	}

	@Override
	protected DCP_StockOutNoticeDetailQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockOutNoticeDetailQueryRes();
	}

	@Override
	protected DCP_StockOutNoticeDetailQueryRes processJson(DCP_StockOutNoticeDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_StockOutNoticeDetailQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		List<Map<String, Object>> getQLangData = null;
		//try {
			 	
			sql = this.getQuerySql(req);			
			String[] conditionValues1 = { }; //查詢條件			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);

			sql = this.getDetailQuerySql(req);
			List<Map<String, Object>> getQLangDataDetail=this.doQueryData(sql, conditionValues1);
			
 
			String eID="";
			String billNo="";
			String payType="";
			res.setDatas(new ArrayList<>());

			String sStockoutDetailSql = getSStockoutDetailSql(req);
			List<Map<String, Object>> sStockList = this.doQueryData(sStockoutDetailSql, null);

			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{				 			
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_StockOutNoticeDetailQueryRes.DataDetail oneLv1 = res.new DataDetail();
					eID = oneData.get("EID").toString(); 
					billNo = oneData.get("BILLNO").toString();
					payType = oneData.get("BILLTYPE").toString();
					oneLv1.setStatus(oneData.get("STATUS").toString());
					oneLv1.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
					oneLv1.setOrgName(oneData.get("ORGNAME").toString());
					oneLv1.setBillType(oneData.get("BILLTYPE").toString());
					oneLv1.setBillNo(oneData.get("BILLNO").toString());
					oneLv1.setSourceType(oneData.get("SOURCETYPE").toString());
					oneLv1.setSourceBillNo(oneData.get("SOURCEBILLNO").toString());
					oneLv1.setDeliverOrgNo(oneData.get("DELIVERORGNO").toString());
					oneLv1.setDeliverOrgName(oneData.get("DELIVERORGNAME").toString());
					oneLv1.setBDate(oneData.get("BDATE").toString());
					oneLv1.setRDate(oneData.get("RDATE").toString());
					oneLv1.setObjectType(oneData.get("OBJECTTYPE").toString());
					oneLv1.setObjectId(oneData.get("OBJECTID").toString());
					oneLv1.setObjectName(oneData.get("OBJECTNAME").toString());
					oneLv1.setPayType(oneData.get("PAYTYPE").toString());
					oneLv1.setPayOrgNo(oneData.get("PAYORGNO").toString());
					oneLv1.setPayOrgName(oneData.get("PAYORGNAME").toString());
					oneLv1.setBillDateNo(oneData.get("BILLDATENO").toString());
					oneLv1.setBillDateDesc(oneData.get("BILLDATEDESC").toString());
					oneLv1.setPayDateNo(oneData.get("PAYDATENO").toString());
					oneLv1.setPayDateDesc(oneData.get("PAYDATEDESC").toString());
					oneLv1.setInvoiceCode(oneData.get("INVOICECODE").toString());
					oneLv1.setInvoiceName(oneData.get("INVOICENAME").toString());
					oneLv1.setCurrency(oneData.get("CURRENCY").toString());
					oneLv1.setCurrencyName(oneData.get("CURRNAME").toString());
					oneLv1.setReturnType(oneData.get("RETURNTYPE").toString());
					oneLv1.setWareHouse(oneData.get("WAREHOUSE").toString());
					oneLv1.setWareHouseName(oneData.get("WAREHOUSENAME").toString());
					oneLv1.setMemo(oneData.get("MEMO").toString());
					oneLv1.setTotCqty(oneData.get("TOTCQTY").toString());
					oneLv1.setTotPqty(oneData.get("TOTPQTY").toString());
					oneLv1.setTotStockOutQty(oneData.get("TOTSTOCKOUTQTY").toString());
					oneLv1.setTotAmt(oneData.get("TOTAMT").toString());
					oneLv1.setTotPreTaxAmt(oneData.get("TOTPRETAXAMT").toString());
					oneLv1.setTotTaxAmt(oneData.get("TOTTAXAMT").toString());
					oneLv1.setEmployeeID(oneData.get("EMPLOYEEID").toString());
					oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
					oneLv1.setDepartID(oneData.get("DEPARTID").toString());
					oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
					oneLv1.setCreateOpID(oneData.get("CREATEOPID").toString());
					oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
					oneLv1.setCreateDateTime(oneData.get("CREATETIME").toString());
					oneLv1.setLastModiOpID(oneData.get("LASTMODIOPID").toString());
					oneLv1.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
					oneLv1.setModifyDateTime(oneData.get("LASTMODITIME").toString());
					oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
					oneLv1.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
					oneLv1.setConfirmDateTime(oneData.get("CONFIRMTIME").toString());
					oneLv1.setCancelBy(oneData.get("CANCELBY").toString());
					oneLv1.setCancelByName(oneData.get("CANCELBYNAME").toString());
					oneLv1.setCancelDateTime(oneData.get("CANCELTIME").toString());
					oneLv1.setOwnOpID(oneData.get("OWNOPID").toString());
					oneLv1.setOwnOpName(oneData.get("OWNOPNAME").toString());
					oneLv1.setOwnDeptID(oneData.get("OWNDEPTID").toString());
					oneLv1.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
					//oneLv1.setIsLocation("F"); //mark by 01029 20250307
					oneLv1.setIsLocation(oneData.get("ISLOCATION").toString());
					//是否启用批号管理
			        String paraIsBatch= PosPub.getPARA_SMS(dao, eID, req.getShopId(), "Is_BatchNO");
			        if (Check.Null(paraIsBatch) || !paraIsBatch.equals("Y"))
			            paraIsBatch="N";
					oneLv1.setIsBatchManage(paraIsBatch);
					oneLv1.setTotRetailAmt(oneData.get("TOTRETAILAMT").toString());
					oneLv1.setDeliveryAddress(oneData.get("DELIVERYADDRESS").toString());
					oneLv1.setDeliveryDate(oneData.get("DELIVERYDATE").toString());
					oneLv1.setReceiptWarehouse(oneData.get("RECEIPTWAREHOUSE").toString());
					oneLv1.setReceiptWarehouseName(oneData.get("WNAME").toString());
					oneLv1.setInvWarehouse(oneData.get("INVWAREHOUSE").toString());
					oneLv1.setInvWarehouseName(oneData.get("W1NAME").toString());
					oneLv1.setIsTranInConfirm(oneData.get("ISTRANINCONFIRM").toString());
					oneLv1.setReceiptWHIsLocation(oneData.get("RECEIPTWHISLOCATION").toString());
					oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
					oneLv1.setCloseBy(oneData.get("CLOSEBY").toString());
					oneLv1.setCloseByName(oneData.get("CLOSEBYNAME").toString());
					oneLv1.setCloseTime(oneData.get("CLOSETIME").toString());
					oneLv1.setTemplateNo(oneData.get("TEMPLATENO").toString());
					oneLv1.setTemplateName(oneData.get("TEMPLATENAME").toString());
					oneLv1.setPayee(oneData.get("PAYEE").toString());
					oneLv1.setPayeeName(oneData.get("PAYEENAME").toString());
					oneLv1.setPayer(oneData.get("PAYER").toString());
					oneLv1.setPayerName(oneData.get("PAYERNAME").toString());
					oneLv1.setCorp(oneData.get("CORP").toString());
					oneLv1.setCorpName(oneData.get("CORPNAME").toString());
					oneLv1.setReceiptCorp(oneData.get("RECEIPTCORP").toString());
					oneLv1.setReceiptCorpName(oneData.get("RECEIPTCORPNAME").toString());
					oneLv1.setDeliveryCorp(oneData.get("DELIVERYCORP").toString());
					oneLv1.setDeliveryCorpName(oneData.get("DELIVERYCORPNAME").toString());
				 
					String id = eID;
					String bill = billNo;
					String pType = payType;
					//组织先筛选出符合当前条件的数据
					getQLangData = getQLangDataDetail.stream()
							.filter(LangData -> id.equals(LangData.get("EID").toString())
									&& bill.equals(LangData.get("BILLNO").toString()))
							.collect(Collectors.toList());
					List<Map<String, Object>> poList=new ArrayList<>();
					if("3".equals(oneLv1.getSourceType())){
						String pOSql=this.getPOrderSql(req,oneLv1.getSourceBillNo());
						poList = this.doQueryData(pOSql, null);
					}

					oneLv1.setDataList(new ArrayList<DCP_StockOutNoticeDetailQueryRes.DetailList>());
					for (Map<String, Object> oneData2 : getQLangData) {
						DCP_StockOutNoticeDetailQueryRes.DetailList oneLv2 = res.new DetailList();
						oneLv2.setItem(oneData2.get("ITEM").toString());
						oneLv2.setSourceType(oneData2.get("SOURCETYPE").toString());
						oneLv2.setSourceBillNo(oneData2.get("SOURCEBILLNO").toString());
						oneLv2.setOItem(oneData2.get("OITEM").toString());
						oneLv2.setPluNo(oneData2.get("PLUNO").toString());
						oneLv2.setPluName(oneData2.get("PLU_NAME").toString());
						oneLv2.setSpec(oneData2.get("SPEC").toString());
						oneLv2.setPluBarcode(oneData2.get("PLUBARCODE").toString());
						oneLv2.setFeatureNo(oneData2.get("FEATURENO").toString());
						oneLv2.setFeatureName(oneData2.get("FEATURENAME").toString());
						oneLv2.setPUnit(oneData2.get("PUNIT").toString());
						oneLv2.setPUnitName(oneData2.get("PUNITNAME").toString());
						oneLv2.setPQty(oneData2.get("PQTY").toString());
						
						oneLv2.setPrice(oneData2.get("PRICE").toString());
						oneLv2.setAmount(oneData2.get("AMOUNT").toString());
						oneLv2.setPreTaxAmt(oneData2.get("PRETAXAMT").toString());
						oneLv2.setTaxAmt(oneData2.get("TAXAMT").toString());
						oneLv2.setTaxCode(oneData2.get("TAXCODE").toString());
						oneLv2.setTaxName(oneData2.get("TAXNAME").toString());
						oneLv2.setTaxRate(oneData2.get("TAXRATE").toString());
						oneLv2.setPoQty(oneData2.get("POQTY").toString());
						oneLv2.setNoQty(oneData2.get("NOQTY").toString());
						
						oneLv2.setBaseUnit(oneData2.get("BASEUNIT").toString());
						oneLv2.setBaseUnitName(oneData2.get("BASEUNITNAME").toString());
						oneLv2.setBaseQty(oneData2.get("BASEQTY").toString());
						oneLv2.setWUnit(oneData2.get("WUNIT").toString());
						oneLv2.setWUnitName(oneData2.get("WUNITNAME").toString());
						oneLv2.setWQty(oneData2.get("WQTY").toString());
						oneLv2.setStatus(oneData2.get("STATUS").toString());
						oneLv2.setBsNo(oneData2.get("BSNO").toString());
						oneLv2.setBsName(oneData2.get("BSNAME").toString());
						oneLv2.setUnitRatio(oneData2.get("UNITRATIO").toString());
						oneLv2.setTaxCalType(oneData2.get("TAXCALTYPE").toString());
						oneLv2.setBaseUnitUdLength(oneData2.get("UDLENGTH").toString());
						oneLv2.setTemplateNo(oneData2.get("TEMPLATENO").toString());
						oneLv2.setMemo(oneData2.get("MEMO").toString());
						oneLv2.setCanReturnQty(oneData2.get("CANRETURNQTY").toString());
						oneLv2.setCanDeliverQty("0");
						oneLv2.setStockOutQty(oneData2.get("STOCKOUTQTY").toString());
						oneLv2.setAvailableStockQty(oneData2.get("AVAILABLESTOCKQTY").toString());
						oneLv2.setCanStockOutQty(oneData2.get("CANSTOCKOUTQTY").toString());
						oneLv2.setListImage(oneData2.get("LISTIMAGE").toString());
						oneLv2.setIsBatch(oneData2.get("ISBATCH").toString());//TODO
						oneLv2.setIsGift(oneData2.get("ISGIFT").toString());
						oneLv2.setInclTax(oneData2.get("INCLTAX").toString());
						oneLv2.setPUnitUdLength(oneData2.get("PUDLENGTH").toString());
						List<Map<String, Object>> ssList = sStockList.stream().filter(x -> x.get("OITEM").toString().equals(oneData2.get("ITEM").toString())).collect(Collectors.toList());
						BigDecimal spqty = new BigDecimal(ssList.stream().mapToDouble(x -> Double.parseDouble(x.get("PQTY").toString())).sum());
						BigDecimal subtract = new BigDecimal(oneLv2.getPQty()).subtract(new BigDecimal(oneLv2.getStockOutQty())).subtract(spqty);
						oneLv2.setCanStockOutQty(subtract.toString());
						oneLv2.setRetailPrice(oneData2.get("RETAILPRICE").toString());
						oneLv2.setRetailAmt(oneData2.get("RETAILAMT").toString());
						oneLv2.setObjectType(oneData2.get("OBJECTTYPE").toString());
						oneLv2.setObjectName(oneData2.get("OBJECTNAME").toString());
						oneLv2.setObjectId(oneData2.get("OBJECTID").toString());

						oneLv2.setReceivePrice(oneData2.get("RECEIVEPRICE").toString());

						oneLv2.setReceivePrice(oneData2.get("RECEIVEPRICE").toString());
						oneLv2.setReceiveAmt(oneData2.get("RECEIVEAMT").toString());
        				oneLv2.setSupPrice(oneData2.get("SUPPRICE").toString());
   				    	oneLv2.setSupAmt(oneData2.get("SUPAMT").toString());
   				    	oneLv2.setRefPurPrice(oneData2.get("REFPURPRICE").toString());
   				    	oneLv2.setCategory(oneData2.get("CATEGORY").toString());

						if("3".equals(oneLv1.getSourceType())&&Check.NotNull(oneLv1.getSourceBillNo())){
							List<Map<String, Object>> filterRows = poList.stream().filter(x -> x.get("OITEM").toString().equals(oneData2.get("OITEM").toString())
							&&!x.get("BILLNO").equals(oneData.get("BILLNO").toString())).collect(Collectors.toList());
							if(CollUtil.isNotEmpty(filterRows)){
								BigDecimal reviewQty = new BigDecimal(filterRows.get(0).get("REVIEW_QTY").toString());
								BigDecimal yetPqty=new BigDecimal(0);
								BigDecimal ynPqty=new BigDecimal(0);
								for (Map<String, Object> map : filterRows) {
									String status = map.get("STATUS").toString();
									BigDecimal pqty = new BigDecimal(map.get("PQTY").toString());
									if(status.equals("1")||status.equals("2")){
										yetPqty=yetPqty.add(pqty);
									}else{
										ynPqty=ynPqty.add(pqty);
									}
								}
								reviewQty=reviewQty.subtract(yetPqty).subtract(ynPqty);
								oneLv2.setCanDeliverQty(reviewQty.toString());
							}
						}

				        oneLv1.getDataList().add(oneLv2);
				        oneLv2 = null;
					}
					
				 
					 
					
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			
			
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	res.setDatas(new ArrayList<DCP_StockOutNoticeDetailQueryRes.DataDetail>());
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,req.getServiceId()+ e.getMessage());//add by 01029 20240703
		//}
		
		return res;
	}

	private String getPOrderSql(DCP_StockOutNoticeDetailQueryReq req,String pOrderNo) {
		StringBuffer sb=new StringBuffer();
		sb.append("select c.sourcebillno,c.oitem,b.REVIEW_QTY,c.pqty,c.billno,c.item,d.status  " +
				" from dcp_porder a " +
				" left join dcp_porder_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.porderno=b.porderno " +
				" left join dcp_stockoutnotice_detail c on c.eid=a.eid and c.organizationno=a.organizationno and c.SOURCEBILLNO=a.porderno and c.oitem=b.item " +
				" left join dcp_stockoutnotice d on c.eid=a.eid and c.organizationno=a.organizationno and c.billno=d.billno  " +
				" where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.porderno='"+pOrderNo+"'");

		return sb.toString();
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StockOutNoticeDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		//int pageNumber = req.getPageNumber(); 明细只有一条 不用这个
		//int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		//int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String type = null;
		String status = null; 
		String detailFilter = null;
		String eId = req.geteId();
		
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
			type = req.getRequest().getBillType();
			detailFilter = req.getRequest().getDetailFilter();
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
 
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT COUNT( 1 ) OVER() NUM ,row_number()  over (order by bdate desc,BILLNO desc ) rn  "
					+ ",  a.*  ,J.ORG_NAME AS ORGNAME ,J1.ORG_NAME AS DELIVERORGNAME "
					+ " ,CASE WHEN a.OBJECTTYPE='3' THEN b2.ORG_NAME ELSE b1.SNAME END AS OBJECTNAME "
					+ "  , p.op_NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME1,c.op_NAME as lastModiOpName "
					+ "  ,BD.NAME AS BILLDATEDESC ,PD.NAME AS PAYDATEDESC ,IE.NAME AS INVOICENAME,W2.WAREHOUSE_NAME AS WAREHOUSENAME "
					+ "  , p1.NAME as EMPLOYEENAME,d6.DEPARTNAME as DEPARTNAME, p2.op_NAME as   CONFIRMBYNAME, p3.op_NAME as   CANCELBYNAME"
					+ "  , p4.op_NAME as OWNOPNAME,d4.DEPARTNAME as OWNDEPTNAME "
					+ "  ,cc.name as CURRNAME ,W.WAREHOUSE_NAME AS WNAME,W1.WAREHOUSE_NAME AS W1NAME,wa.ISLOCATION,d5.DEPARTNAME as CREATEDEPTNAME , p5.op_NAME as CLOSEBYNAME,w2.islocation as receiptWHIsLocation,j2.org_name as payOrgName,pt.PTEMPLATE_NAME as templatename," +
				" m.sname as payeename,n.sname as payername,j3.org_name as corpname,j4.org_name as receiptCorpName,j5.org_name as deliveryCorpName "
					+ " FROM DCP_STOCKOUTNOTICE a   "
					//+ " left join DCP_STOCKOUTNOTICE_DETAIL  detail on a.eid=detail.eid and detail.BILLNO=a.BILLNO "
					+ " left join DCP_bizpartner b1 on a.eid=b1.eid and a.OBJECTID=b1.BIZPARTNERNO  and a.OBJECTTYPE<>'3' "
					+ " left join DCP_org_lang b2 on a.eid=b2.eid and a.OBJECTID=b2.ORGANIZATIONNO  and a.OBJECTTYPE='3' and b2.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p on a.eid=p.eid and p.opno=a.CREATEOPID and p.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  c on a.eid=c.eid and c.opno=a.LASTMODIOPID and c.lang_type='"+langType+"' "
					+"  left join DCP_DEPARTMENT_LANG d  on a.eid=d.eid and a.CREATEDEPTID=d.DEPARTNO and d.lang_type='"+langType+"' "
					+"   LEFT JOIN DCP_ORG_LANG j on j.EID=a.EID AND j.ORGANIZATIONNO=a.ORGANIZATIONNO AND j.LANG_TYPE='" + langType +"' " 
					+"   LEFT JOIN DCP_ORG_LANG j1 on j1.EID=a.EID AND j1.ORGANIZATIONNO=a.DELIVERORGNO AND j1.LANG_TYPE='" + langType +"' "
					+"   LEFT JOIN DCP_ORG_LANG j2 on j2.EID=a.EID AND j2.ORGANIZATIONNO=a.payorgno AND j2.LANG_TYPE='" + langType +"' "

				+"   LEFT JOIN DCP_ORG_LANG j3 on j3.EID=a.EID AND j3.ORGANIZATIONNO=a.corp AND j3.LANG_TYPE='" + langType +"' "
				+"   LEFT JOIN DCP_ORG_LANG j4 on j4.EID=a.EID AND j4.ORGANIZATIONNO=a.receiptcorp AND j4.LANG_TYPE='" + langType +"' "
				+"   LEFT JOIN DCP_ORG_LANG j5 on j5.EID=a.EID AND j5.ORGANIZATIONNO=a.deliverycorp AND j5.LANG_TYPE='" + langType +"' "


				+ " left join DCP_BILLDATE  BD on a.eid=BD.eid and BD.BILLDATENO=a.BILLDATENO "
					+ " left join DCP_PAYDATE  PD on a.eid=PD.eid and PD.PAYDATENO=a.PAYDATENO "
					+ " left join DCP_INVOICETYPE  IE on a.eid=IE.eid and IE.INVOICECODE=a.INVOICECODE "
					+"   LEFT JOIN DCP_WAREHOUSE_LANG W2 on W2.EID=a.EID AND W2.ORGANIZATIONNO=a.DELIVERORGNO AND W2.WAREHOUSE=a.WAREHOUSE AND w2.LANG_TYPE='" + langType +"' "
					+ " left join DCP_EMPLOYEE  p1 on a.eid=p1.eid and p1.EMPLOYEENO=a.EMPLOYEEID "
					+ "  left join DCP_DEPARTMENT_LANG d1  on p1.eid=d1.eid and p1.DEPARTMENTNO=d1.DEPARTNO and d1.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p2 on a.eid=p2.eid and p2.opno=a.CONFIRMBY and p2.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p3 on a.eid=p3.eid and p3.opno=a.CANCELBY and p3.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p4 on a.eid=p4.eid and p4.opno=a.OWNOPID and p4.lang_type='"+langType+"' "
					+ " left join PLATFORM_STAFFS_LANG  p5 on a.eid=p5.eid and p5.opno=a.CLOSEBY and p5.lang_type='"+langType+"' "
					+"   LEFT JOIN DCP_WAREHOUSE Wa on Wa.EID=A.EID AND Wa.ORGANIZATIONNO=a.ORGANIZATIONNO AND Wa.WAREHOUSE=a.WAREHOUSE     "
					+"   LEFT JOIN DCP_WAREHOUSE_LANG W on W.EID=A.EID AND W.ORGANIZATIONNO=a.ORGANIZATIONNO AND W.WAREHOUSE=a.RECEIPTWAREHOUSE AND w.LANG_TYPE='" + langType +"' "
						+"   LEFT JOIN DCP_WAREHOUSE W2 on W2.EID=A.EID AND W2.ORGANIZATIONNO=a.ORGANIZATIONNO AND W2.WAREHOUSE=a.RECEIPTWAREHOUSE  "
						+"   LEFT JOIN DCP_WAREHOUSE_LANG W1 on W1.EID=A.EID AND W1.ORGANIZATIONNO=a.ORGANIZATIONNO AND W1.WAREHOUSE=a.INVWAREHOUSE AND w1.LANG_TYPE='" + langType +"' "
					+"  left join DCP_DEPARTMENT_LANG d4  on a.eid=d4.eid and a.OWNDEPTID=d4.DEPARTNO and d4.lang_type='"+langType+"' "
					+"  left join DCP_DEPARTMENT_LANG d5  on a.eid=d5.eid and a.CREATEDEPTID=d5.DEPARTNO and d5.lang_type='"+langType+"' "
						+"  left join DCP_DEPARTMENT_LANG d6  on a.eid=d6.eid and a.departid=d6.DEPARTNO and d6.lang_type='"+langType+"' "
				+" left join DCP_PTEMPLATE  pt on pt.eid=a.eid and pt.ptemplateno=a.templateno and pt.doc_type='0' " +
				" left join dcp_bizpartner m on m.eid=a.eid and m.bizpartnerno=a.payee " +
				" left join dcp_bizpartner n on n.eid=a.eid and n.bizpartnerno=a.payer "

		);
		sqlbuf.append("  left join DCP_CURRENCY_LANG cc on cc.eid=a.eid and cc.CURRENCY=a.CURRENCY  and cc.lang_type='"+langType+"' ");
	    String searchScope ="2" ; //查询所有 ，本来考虑的单头单身 查询用一个接口
		if("1".equals(searchScope)){
			sqlbuf.append(" inner join (select * from DCP_STOCKOUTNOTICE_DETAIL a where a.eid='"+eId+"' " +
					" and a.organizationno='"+req.getOrganizationNO()+"' " +
					"and nvl(a.PQTY,0)>nvl(a.STOCKOUTQTY,0) ) p on p.eid=a.eid and p.organizationno=p.organizationno and p.billno=a.billno");
		}
		sqlbuf.append(" WHERE a.EID = '"+eId+"' ");

		if(Check.Null(req.getRequest().getGetType())||"0".equals(req.getRequest().getGetType())){
			sqlbuf.append(" and a.ORGANIZATIONNO = '" + req.getOrganizationNO() + "' ");
		}else if ("1".equals(req.getRequest().getGetType())){
			sqlbuf.append(" and a.DELIVERORGNO = '" + req.getOrganizationNO() + "' ");
		}
	 
		if(type != null && type.length() > 0)
			sqlbuf.append(" and a.billType = '"+type+"' ");
		if(key != null && key.length() > 0)
			sqlbuf.append(" and a.BILLNO = '"+key+"' ");
		 
		sqlbuf.append(" ) DBL   ");
 
		sqlbuf.append(" order by BILLNO " );
		
		
		sql = sqlbuf.toString();
		//logger.info(sql);  
		return sql;
	}
	
	
	
	/**
	 * 新增要處理的資料(先加入的, 先處理)
	 * 
	 * @param row
	 */
	protected final void addProcessData(DataProcessBean row) {
		this.pData.add(row);
	}
	
	/**
	 * 计算总数
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getCountSql(DCP_StockOutNoticeDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type =null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
		}
 	
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select num from( select count(*) AS num   "	
				+ " FROM DCP_SUPLICENSECHANGE a"
				+ "where a.EID='"+eId+"'  " );
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
 
		}
		sqlbuf.append(" )");
		
		sql= sqlbuf.toString();
		return sql;
	}
	
	
	 
	protected String getDetailQuerySql(DCP_StockOutNoticeDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String detailFilter = null; 
		String eId = req.geteId();
		String type = null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
			type = req.getRequest().getBillType();
			detailFilter = req.getRequest().getDetailFilter();
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		
		//Z.ITEM, Z.SOURCETYPE, Z.SOURCEBILLNO, Z.OITEM, Z.PLUNO
		//, Z1.PLUNAME, Z.SPEC, Z.PLUBARCODE, Z.FEATURENO, Z.FEATURENAME
		//, Z.PUNIT, Z1.PUNITNAME, Z.PQTY, Z1.CANRETURNQTY, Z1.CANDELIVERQTY, Z.PRICE, Z.AMOUNT, Z.PRETAXAMT, Z.TAXAMT, Z.TAXCODE, Z1.TAXNAME, Z.TAXRATE, Z.STOCKOUTQTY, Z.BASEUNIT, Z1.BASEUNITNAME, Z.BASEQTY, Z.WUNIT, Z1.WUNITNAME, Z.WQTY, Z.STATUS, Z.BSNO, Z1.BSNAME, Z.MEMO, Z1.AVAILABLESTOCKQTY, Z1.CANSTOCKOUTQTY, Z1.LISTIMAGE, Z.RETAILPRICE, Z.RETAILAMT, Z1.ISBATCH, Z.ISGIFT
		sqlbuf.append("SELECT * FROM ("
				+ " SELECT COUNT( 1 ) OVER() NUM ,row_number()  over ( order by z.BILLNO ,z.ITEM ) rn  "
				+ ",  z.* ,G.isbatch,TX1.TAXNAME , d1.UNAME as baseUnitName, d2.UNAME as WUNITName  "
				+ "  , F.PLU_NAME,G.SPEC,H.FEATURENAME,I.UNAME as PUNITNAME,E.LISTIMAGE "
				+ "  ,nvl((c2.STOCKINQTY-c2.RETURNQTY) ,0) as CANRETURNQTY,nvl(st.QTY-st.LOCKQTY,0) as AVAILABLESTOCKQTY "
				+ " ,t1.UDLENGTH ,t3.UDLENGTH as PUDLENGTH,bs.bsname,z.PQTY-z.STOCKOUTQTY as canStockOutQty,case when z.objecttype='3' then o1.org_name else p1.sname end as objectname   "//t.TAXCALTYPE
				+ " FROM DCP_STOCKOUTNOTICE a   "
				+ " left join DCP_STOCKOUTNOTICE_DETAIL  z   on a.eid=z.eid and z.BILLNO=a.BILLNO "
				+ " LEFT JOIN DCP_PURORDER_DETAIL C1 ON z.SOURCEBILLNO=c1.PURORDERNO and z.OITEM=c1.ITEM "
				+ " left JOIN DCP_PURORDER_DELIVERY c2 ON c1.EID=c2.EID AND c1.PURORDERNO=c2.PURORDERNO AND c1.ITEM =c2.ITEM2 "
				+ " left join DCP_bizpartner  b on a.eid=b.eid and a.OBJECTID=b.BIZPARTNERNO  and b.BIZTYPE=a.OBJECTTYPE "
				+ " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
				+ " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
				+"  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='"+langType+"' "
				+"   LEFT JOIN DCP_ORG_LANG j on j.EID=a.EID AND j.ORGANIZATIONNO=a.ORGANIZATIONNO AND j.LANG_TYPE='" + langType +"' " 
				+"   LEFT JOIN DCP_ORG_LANG j1 on j1.EID=a.EID AND j1.ORGANIZATIONNO=a.DELIVERORGNO AND j1.LANG_TYPE='" + langType +"' " 
				+ " LEFT JOIN DCP_GOODSIMAGE E ON E.PLUNO=z.PLUNO "
				+ " LEFT JOIN DCP_GOODS_LANG F ON F.PLUNO=z.PLUNO AND F.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_GOODS G ON G.PLUNO=z.PLUNO  "
				+ " LEFT JOIN DCP_GOODS_FEATURE_LANG H ON H.PLUNO=z.PLUNO AND H.FEATURENO=z.FEATURENO AND H.LANG_TYPE='"+langType+"' " 
				+ " LEFT JOIN DCP_STOCK st ON z.eid=st.eid and st.PLUNO=z.PLUNO and st.FEATURENO=z.FEATURENO and st.WAREHOUSE=z.WAREHOUSE and z.organizationno=st.organizationno "
				+"  left join DCP_TAXCATEGORY_LANG TX1  on TX1.eid=z.eid and z.TAXCODE=TX1.TAXCODE and TX1.TAXAREA='CN'  and TX1.lang_type='"+langType+"' "
				+ " LEFT JOIN DCP_REASON bs ON z.eid=bs.eid and z.BSNO=bs.BSNO and bs.bsType='17'  "
				+ " LEFT JOIN DCP_TAXCATEGORY T ON t.EID=z.EID AND t.TAXCODE=z.TAXCODE "
				+ " LEFT JOIN DCP_UNIT T1 ON t1.EID=z.EID AND t1.UNIT=z.BASEUNIT   "
				+ " LEFT JOIN DCP_UNIT T3 ON t3.EID=z.EID AND t3.UNIT=z.PUNIT   "
				//+ " LEFT JOIN DCP_UNIT_LANG I1  ON I1.UNIT=B.BASEUNIT AND I1.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_GOODS_UNIT T2 ON t2.EID=z.EID AND t2.PLUNO=z.PLUNO  AND t2.OUNIT=z.PUNIT   "
				+ "	LEFT JOIN DCP_UNIT_LANG d1 ON d1.UNIT=z.BASEUNIT AND z.EID=d1.EID and d1.LANG_TYPE='"+langType+"' "
				+ "	LEFT JOIN DCP_UNIT_LANG d2 ON d2.UNIT=z.WUNIT AND z.EID=d2.EID and d2.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_UNIT_LANG I  ON I.UNIT=z.PUNIT AND z.EID=I.EID and  I. LANG_TYPE='"+langType+"' " +
						" left join dcp_org_lang o1 on o1.eid=a.eid and o1.organizationno=z.objectid and o1.lang_type='"+langType+"' " +
						" left join dcp_bizpartner p1 on p1.eid=a.eid and p1.BIZPARTNERNO=z.objectid "
				 );
		sqlbuf.append(" WHERE a.EID = '"+eId+"' ");
	if(type != null && type.length() > 0)
		sqlbuf.append(" and a.billType = '"+type+"' ");
	if(key != null && key.length() > 0)
		sqlbuf.append(" and a.BILLNO = '"+key+"' ");
	 
	sqlbuf.append(" ) DBL   ");

	sqlbuf.append(" order by BILLNO ,ITEM" );
		sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}
	
	protected String getMultiQuerySql(DCP_StockOutNoticeDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type = null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getBillNo();
 
		}
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT DBL.*,b.*   FROM ("
					+ " SELECT ROWNUM AS rn   "
					+ "  , a.eid   "
					+ " FROM DCP_SUPLICENSECHANGE a"
					
					+ " WHERE a.EID = '"+eId+"' " );  
		
		if(key != null && key.length() > 0){
			sqlbuf.append(" and  a.BILLNO  like  "+SUtil.RetLikeStr(key)); 
 
		}
 
		sqlbuf.append("    and  ROWNUM > "+startRow +" AND ROWNUM  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" ) DBL   ");  //这里先查出和主数据一样的数据 然后关联语言档
		sqlbuf.append(" left join DCP_SUPLICENSECHANGE_DETAIL  b on a.eid=b.eid and b.BILLNO=a.BILLNO   ");
		sql = sqlbuf.toString();
		//logger.info(sql);   
		return sql;
	}

	//查询自采出库单明细
	private String getSStockoutDetailSql(DCP_StockOutNoticeDetailQueryReq req) throws Exception{
		StringBuffer sb=new StringBuffer();
		sb.append("select a.ofno,a.oitem,sum(A.PQTY) AS PQTY from DCP_SSTOCKOUT_DETAIL a " +
				" inner join DCP_SSTOCKOUT b on a.eid=b.eid and a.organizationno=b.organizationno and a.SSTOCKOUTNO=b.SSTOCKOUTNO " +
				" where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
				" and a.OFNO='"+req.getRequest().getBillNo()+"' and b.status='0' " +
				" group by a.ofno,a.oitem ");

		return sb.toString();
	}
	
	
}
