package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PFOrderProcessReq;
import com.dsc.spos.json.cust.res.DCP_PFOrderProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 营业预估删除
 * @author yuanyy
 *
 */
public class DCP_PFOrderProcess extends SPosAdvanceService<DCP_PFOrderProcessReq, DCP_PFOrderProcessRes> {
	
	@Override
	protected void processDUID(DCP_PFOrderProcessReq req, DCP_PFOrderProcessRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String shopId = req.getShopId();
			String PFNO = req.getRequest().getPfNo();
			String trueRDate = req.getRequest().getrDate();

			if(!Check.Null(trueRDate)){
				trueRDate = trueRDate.replace("-", "");
			}
			
			String rDateSql = "select * from ("
					+ " select 1 as docType , pfNO AS docNO ,rDate from DCP_PORDER_FORECAST where EID = '"+eId+"' and SHOPID = '"+shopId+"' "
					+ " and rDate = '"+trueRDate+"' and status = '1' "
					+ " UNION "
					+ " SELECT 2 as docType , guqingNo AS docNO , rDate  FROM DCP_Guqingorder "
					+ " where EID = '"+eId+"' and SHOPID = '"+shopId+"' and rDate = '"+trueRDate+"'"
					+ " ) order by docType , docNO ";
			List<Map<String, Object>> rDateDatas = this.doQueryData(rDateSql, null);
			String guQingNo = "";
			if(rDateDatas != null && !rDateDatas.isEmpty() ){
				
				String rePfNo = "";
				for (Map<String, Object> map : rDateDatas) {
//					if(map.get("DOCTYPE").toString().equals("2")){
//						guQingNo = map.get("DOCNO").toString();
//					}
					rePfNo = rePfNo + map.get("DOCNO").toString() + " ";
				}
				
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("已存在需求日期为"+trueRDate+"的单据("+rePfNo+")");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在需求日期为"+trueRDate+"的单据("+rePfNo+")");
			}
			
			UptBean ub1 = new UptBean("DCP_PORDER_FORECAST");		
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
			ub1.addCondition("PFNO",new DataValue(PFNO, Types.VARCHAR));
			
			ub1.addUpdateValue("STATUS",new DataValue("1", Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(ub1));	
			
			if(Check.Null(guQingNo)){ // 说明该需求日期没有 POS 端建立沽清单，  也没有计划报单建立的沽清单
				guQingNo = this.getGuQingNO(req);
			}
			
			String createBy = req.getOpNO();
			String createByName = req.getOpName();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			String createDate = df.format(cal.getTime());
			df=new SimpleDateFormat("HHmmss");
			String createTime = df.format(cal.getTime());
			
			String sql = " SELECT a.pfNO , a.pforderType , a.bDate , a.rDate, b.pluNo , b.punit, b.price , b.guqingtype , "
					+ " c.dtNo, c.dtName , c.begin_Time , c.end_Time , c.kQty ,c.qty "
					+ " FROM DCP_porder_forecast a "
					+ " LEFT JOIN   DCP_porder_forecast_detail b ON a.EID = b.EID AND a.SHOPID  = b.SHOPID AND a.pfNo = b.pfNo "
					+ " LEFT JOIN DCP_porder_forecast_dinnertime c ON b.EID = c.EID AND b.SHOPID  = c.SHOPID AND b.pfNo = c.pfNo AND b.pluNo = c.pluNo  "
					+ " WHERE a.EID = '"+eId+"' AND a.SHOPID = '"+shopId+"' "
					+ " AND a.pfNo = '"+PFNO+"' ";
			
			List<Map<String, Object>> allDatas = this.doQueryData(sql, null);
			
			if(allDatas != null){
				//单头主键字段
				Map<String, Object> conditiondt = new HashMap<String, Object>(); //查询条件
				conditiondt.put("DTNO", "");
				conditiondt.put("DTNO", null);
				//调用过滤函数
				List<Map<String, Object>> dtDatas = MapDistinct.getWhereMap(allDatas, conditiondt,true);
				
				if(dtDatas != null && dtDatas.size() > 0){
					//单头主键字段
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
					condition.put("PFNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader=MapDistinct.getMap(allDatas, condition);
					
					//单头主键字段
					Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
					condition2.put("PFNO", true);
					condition2.put("PLUNO", true);
					//调用过滤函数
					List<Map<String, Object>> pluDatas = MapDistinct.getMap(allDatas, condition2);
					
					//******************* DCP_GUQINGORDER *******************
					String[] columns = {"EID", "SHOPID","GUQINGNO","ORGANIZATIONNO","BDATE","RDATE",
								"CREATEBY","CREATE_DATE","CREATE_TIME"};
					String pfOrderType = "";
					
					for (Map<String, Object> headMap : getQHeader) {
						
						String bDate = headMap.get("BDATE").toString();
						String rDate = headMap.get("RDATE").toString();
						DataValue[] insValue = null;
						insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(guQingNo, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(bDate, Types.VARCHAR),
							new DataValue(rDate, Types.VARCHAR), 
							new DataValue(createBy, Types.VARCHAR), 
							new DataValue(createDate, Types.VARCHAR), 
							new DataValue(createTime, Types.VARCHAR) 
						};
						
						InsBean ib1 = new InsBean("DCP_GUQINGORDER", columns);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
					}
					
					
					
					//******************* DCP_GUQINGORDER_DETAIL *******************
					String[] columnsDetail = {"EID", "SHOPID","GUQINGNO","ORGANIZATIONNO","PLUNO","PUNIT",
							"PRICE","GUQINGTYPE","PFNO","PFORDERTYPE",
							"MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME"};
					
					for (Map<String, Object> pluMap : pluDatas) {
						
						String pluNo = pluMap.get("PLUNO").toString();
						String pUnit = pluMap.get("PUNIT").toString();
						String price = pluMap.get("PRICE").toString();
						String guQingType = pluMap.get("GUQINGTYPE").toString();
						pfOrderType = pluMap.get("PFORDERTYPE").toString();
						DataValue[] insValue = null;
						insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(guQingNo, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(pluNo, Types.VARCHAR),
							new DataValue(pUnit, Types.VARCHAR), 
							new DataValue(price, Types.VARCHAR), 
							new DataValue(guQingType, Types.VARCHAR),
							new DataValue(PFNO, Types.VARCHAR),
							new DataValue(pfOrderType, Types.VARCHAR),
							new DataValue(createBy, Types.VARCHAR), 
							new DataValue(createByName, Types.VARCHAR), 
							new DataValue(createDate, Types.VARCHAR), 
							new DataValue(createTime, Types.VARCHAR) 
						};
						
						InsBean ib1 = new InsBean("DCP_GUQINGORDER_DETAIL", columnsDetail);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
						
					}
					
					
					//******************* DCP_GUQINGORDER_DINNERTIME *******************
					int item = 1;
					for (Map<String, Object> pluMap : allDatas) {
						String[] columnsDinner = {"EID", "SHOPID","GUQINGNO","PLUNO","DTNO","DTNAME",
								"BEGIN_TIME","END_TIME","KQTY","QTY","SALEQTY","RESTQTY","ISCLEAR",
								"MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME","PUNIT"};
						String pluNo = pluMap.get("PLUNO").toString();
						String pUnit = pluMap.get("PUNIT").toString();
						String dtNo = pluMap.get("DTNO").toString();
						String dtName = pluMap.get("DTNAME").toString();
						String beginTime = pluMap.get("BEGIN_TIME").toString();
						String endTime = pluMap.get("END_TIME").toString();
						
						if(Check.Null(pluNo) || Check.Null(dtNo)){
							continue;
						}
						
						String kQty = pluMap.get("KQTY").toString();
						String qty = pluMap.get("QTY").toString();
						
						DataValue[] insValue = null;
						insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(guQingNo, Types.VARCHAR), 
							new DataValue(pluNo, Types.VARCHAR),
							new DataValue(dtNo, Types.VARCHAR), 
							new DataValue(dtName, Types.VARCHAR), 
							new DataValue(beginTime, Types.VARCHAR),
							new DataValue(endTime, Types.VARCHAR),
							
							new DataValue(kQty, Types.VARCHAR),
							new DataValue(qty, Types.VARCHAR),
							new DataValue("0", Types.VARCHAR), // saleQty 已售数量
							new DataValue(qty, Types.VARCHAR), // 剩余数量， 新增单子时默认给 qty 的值
							new DataValue("N", Types.VARCHAR), // 是否沽清 ，默认 N 
							
							new DataValue(createBy, Types.VARCHAR), 
							new DataValue(createByName, Types.VARCHAR), 
							new DataValue(createDate, Types.VARCHAR), 
							new DataValue(createTime, Types.VARCHAR),
							new DataValue(pUnit, Types.VARCHAR) 
						};
						
						InsBean ib1 = new InsBean("DCP_GUQINGORDER_DINNERTIME", columnsDinner);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
						
						
						//************* 沽清调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********
						String[] columns_hm ={"EID","SHOPID","GUQINGNO","PLUNO","ITEM","UPDATETYPE","DTNO","DTNAME",
								"BEGIN_TIME","END_TIME","PFNO","PFORDERTYPE","QTY","MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME",
								"RESTQTY","SALEQTY","PUNIT"
						};
						
						DataValue[] insValue_hm = new DataValue[] 
								{
										new DataValue(eId, Types.VARCHAR),
										new DataValue(shopId, Types.VARCHAR), 
										new DataValue(guQingNo, Types.VARCHAR),
										new DataValue(pluNo, Types.VARCHAR),
										new DataValue(item+"" ,Types.VARCHAR),
										new DataValue("initial", Types.VARCHAR),//枚举: initial：初始化,guQing：沽清,backEnd：后台,pos：POS端
										new DataValue(dtNo, Types.VARCHAR),
										new DataValue(dtName , Types.VARCHAR),
										new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
										new DataValue(endTime.replace(":", ""), Types.VARCHAR),
										new DataValue(PFNO, Types.VARCHAR),
										new DataValue(pfOrderType, Types.VARCHAR),
										new DataValue(qty, Types.VARCHAR),
										new DataValue(createBy, Types.VARCHAR), 
										new DataValue(createByName, Types.VARCHAR), 
										new DataValue(createDate, Types.VARCHAR), 
										new DataValue(createTime, Types.VARCHAR),
										new DataValue(qty, Types.VARCHAR),
										new DataValue("0", Types.VARCHAR),
										new DataValue(pUnit, Types.VARCHAR)
								};
						
						InsBean ib_hm = new InsBean("DCP_GUQINGORDER_PLQTYUPRECORD", columns_hm);
						ib_hm.addValues(insValue_hm);
						this.addProcessData(new DataProcessBean(ib_hm)); 
						item = item + 1;
						
					}
				
				}
			}
			
			this.doExecuteDataToDB();		
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription(e.getMessage());
		}	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PFOrderProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PFOrderProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PFOrderProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PFOrderProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//String pfId =req.getPfId();
		String pfNo = req.getRequest().getPfNo();
		
		if (Check.Null(pfNo)) 
		{
			errMsg.append("单据编号不可为空 ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PFOrderProcessReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderProcessReq>(){};
	}

	@Override
	protected DCP_PFOrderProcessRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderProcessRes();
	}
	
	/**
	 * 生成单号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	private String getGuQingNO(DCP_PFOrderProcessReq req) throws Exception{
		
		String guQingNo = "";
		String sql = "";
		String eId = req.geteId();
		String shopId = req.getShopId();

		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		
		sql = "select MAX(guQingNo) AS guQingNo from DCP_GUQINGORDER where EID = '"+eId+"' and SHOPID = '"+shopId+"' and guQingNo like '%%"+bDate+"%%'";
		List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
		if(getDatas.size() > 0 ){
			guQingNo = (String) getDatas.get(0).get("GUQINGNO");

			if (guQingNo != null && guQingNo.length() > 0) {
				long i;
				guQingNo = guQingNo.substring(2, guQingNo.length());
				i = Long.parseLong(guQingNo) + 1;
				guQingNo = i + "";
				guQingNo = "GQ" + guQingNo;
				
			} else {
				guQingNo = "GQ" + bDate + "00001";
			}
		
		}
		
		return guQingNo;
		
	}
	
	
}
