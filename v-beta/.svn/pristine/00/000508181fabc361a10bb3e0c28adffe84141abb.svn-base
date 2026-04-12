package com.dsc.spos.service.imp.json;

import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockUnlockReq;
import com.dsc.spos.json.cust.req.DCP_StockUnlockReq.OrgList;
import com.dsc.spos.json.cust.req.DCP_StockUnlockReq.PluList;
import com.dsc.spos.json.cust.res.DCP_StockUnlockRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

/**
 * 库存解锁
 * @author 2020-06-18
 *
 */
public class DCP_StockUnlock extends SPosAdvanceService<DCP_StockUnlockReq, DCP_StockUnlockRes> {
	
	@Override
	protected void processDUID(DCP_StockUnlockReq req, DCP_StockUnlockRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			
			String billNo = req.getRequest().getBillNo();
			String billType = req.getRequest().getBillType();
			// 根据 SA-孙红艳设计， 此处搞成 固定值 Order 
			billType = "Order";
			String channelId = req.getRequest().getChannelId();
			String unLockType = req.getRequest().getUnLockType(); // 解锁类型出： 1：库解锁   （订转销） -1：订单取消解锁（退订）
			String bDate = req.getRequest().getbDate();
			
			List<PluList> pluList = req.getRequest().getPluList();
			//【ID1030112】【乐沙儿3.0】库存锁定问题（频繁） by jinzma 20221205
			pluList = pluList.stream().collect(Collectors.collectingAndThen(
					Collectors.toCollection(()-> new TreeSet<>(
							Comparator.comparing(p->p.getPluNo()+";"+p.getFeatureNo()+";"+p.getsUnit()))
					),ArrayList::new)
			);
			
			
			/**
			 * 库存解锁
			 * 根据单号（billNo） 查询库存锁定信息，然后再看有没有传 商品信息进来，匹配商品信息后部分解锁或 全部解锁。
			 *
			 */
			String billSql = " select * from DCP_Stock_lock a where a.eId = '"+eId+"' and billNo = '"+billNo+"' and channelId = '"+channelId+"' ";
			
			HelpTools.writelog_fileName("******** DCP_StockUnlock_Open 服务查询锁定信息：" + billSql,"LockLog");
			
			List<Map<String ,Object>> billDatas = this.doQueryData(billSql, null);
			if(billDatas == null || billDatas.size() < 1){

//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "无有效单据锁定信息！");
			}else{
				
				for (Map<String, Object> map : billDatas) {
					
					String pluNo = map.get("PLUNO").toString();
					String featureNo = map.get("FEATURENO").toString();
					String sUnit = map.get("SUNIT").toString();
					String pluLockQty = map.get("SQTY").toString();
					
					String item = map.get("ITEM").toString();
					String baseUnit = map.get("BASEUNIT").toString();
					
					/**
					 * 商品信息节点， 疑问： 部分商品解锁？ 整单所有商品解锁？
					 */
					for (PluList pluMap : pluList) {
						
						HelpTools.writelog_fileName("********** DCP_StockUnLock 传入商品参数：商品编码："+ pluMap.getPluNo()  + "特征码:"+pluMap.getFeatureNo()+"单位："+pluMap.getsUnit(),"LockLog");
						//商品部分解锁
						if(pluMap.getOrganizationList() != null && pluMap.getOrganizationList().size() > 0 ){
							
							if (pluMap.getPluNo().equals(pluNo) && pluMap.getFeatureNo().equals(featureNo) && pluMap.getsUnit().equals(sUnit)){
								
								Map<Integer, Object>inputParameter = new HashMap<>();
								Map<Integer, Integer>outParameter = new HashMap<>();
								inputParameter.put(1, eId);
								
								inputParameter.put(3, billType);
								inputParameter.put(4, billNo);
								inputParameter.put(5, item);
								inputParameter.put(6, bDate);
								inputParameter.put(7, pluNo);
								inputParameter.put(8, featureNo);
								
								inputParameter.put(10, channelId);
								inputParameter.put(11, sUnit);
								
								inputParameter.put(13, baseUnit);
								
								//商品指定 解锁机构
								for (OrgList orgMap : pluMap.getOrganizationList()) {
									
									String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, orgMap.getQty().toString());
									
									inputParameter.put(2, orgMap.getOrganizationNo());
									inputParameter.put(9, orgMap.getWarehouse());
									inputParameter.put(12, orgMap.getQty());
									inputParameter.put(14, baseQty);
									
									inputParameter.put(15, unLockType);
									inputParameter.put(16, "R01");
									inputParameter.put(17, "解锁原因描述");
									inputParameter.put(18, "admin");
									
									ProcedureBean pc = new ProcedureBean("SP_DCP_UnLockStock", inputParameter);
									this.addProcessData(new DataProcessBean(pc));
									HelpTools.writelog_fileName("********** DCP_StockUnLock 服务调用存储过程参数："+inputParameter.toString(),"LockLog");
									
								}
								
							}
							
						}
						//商品没有指定解锁机构
						else{
							if (pluMap.getPluNo().equals(pluNo) && pluMap.getFeatureNo().equals(featureNo) && pluMap.getsUnit().equals(sUnit)){
								Map<Integer, Object>inputParameter = new HashMap<>();
								Map<Integer, Integer>outParameter = new HashMap<>();
								inputParameter.put(1, eId);
								inputParameter.put(2, map.get("ORGANIZATIONNO"));
								inputParameter.put(3, billType);
								inputParameter.put(4, billNo);
								inputParameter.put(5, item);
								inputParameter.put(6, bDate);
								inputParameter.put(7, pluNo);
								inputParameter.put(8, featureNo);
								inputParameter.put(9, map.get("WAREHOUSE"));
								inputParameter.put(10, channelId);
								inputParameter.put(11, sUnit);
								inputParameter.put(12, map.get("SQTY")); //没有指定解锁结构的时候，该单据商品数量全部解锁
								inputParameter.put(13, baseUnit);
								inputParameter.put(14, map.get("BQTY"));
								inputParameter.put(15, unLockType);
								inputParameter.put(16, "R01");
								inputParameter.put(17, "解锁原因描述");
								inputParameter.put(18, "admin");
								
								ProcedureBean pc = new ProcedureBean("SP_DCP_UnLockStock", inputParameter);
								this.addProcessData(new DataProcessBean(pc));
								HelpTools.writelog_fileName("********** DCP_StockUnLock 服务调用存储过程参数："+inputParameter.toString(),"LockLog");
								
							}
						}
					}
				}
				
				this.doExecuteDataToDB();
			}
			
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
	protected List<InsBean> prepareInsertData(DCP_StockUnlockReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockUnlockReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockUnlockReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_StockUnlockReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected TypeToken<DCP_StockUnlockReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockUnlockReq> (){};
	}
	
	@Override
	protected DCP_StockUnlockRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockUnlockRes();
	}
	
}
