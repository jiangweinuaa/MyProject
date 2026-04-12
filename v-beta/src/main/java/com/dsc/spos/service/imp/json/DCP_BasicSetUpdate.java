package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BasicSetUpdateReq;
import com.dsc.spos.json.cust.req.DCP_BasicSetUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_BasicSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 基础设置新增
 * @author yuanyy 2020-03-03
 *
 */
public class DCP_BasicSetUpdate extends SPosAdvanceService<DCP_BasicSetUpdateReq, DCP_BasicSetUpdateRes> {

	@Override
	protected void processDUID(DCP_BasicSetUpdateReq req, DCP_BasicSetUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		try {
			String templateNo = req.getRequest().getTemplateNo();
			String updateServiceAddress = req.getRequest().getUpdateServiceAddress();
			String posServiceAddress = req.getRequest().getPosServiceAddress();
			String memberServiceAddress = req.getRequest().getMemberServiceAddress();
			
			String userCode = req.getRequest().getUserCode();
//			String userName = req.getRequest().getUserName();
			String userKey = req.getRequest().getUserKey();
			String spreadAppNo = req.getRequest().getSpreadAppNo();
			String spreadAppName = req.getRequest().getSpreadAppName();
			String restrictShop = req.getRequest().getRestrictShop();
			String pictureGetAddress = req.getRequest().getPictureGetAddress();
			
			String lastmodiopid = req.getRequest().getLastmodiopid();
			String lastmodiOpName = req.getRequest().getLastmodiOpName();
			String lastmoditime = req.getRequest().getLastmoditime();
			String terminalType = req.getRequest().getTerminalType(); // pad：PAD导购,mobileCash：移动收银
			String payServiceAddress = req.getRequest().getPayServiceAddress() == null?"":req.getRequest().getPayServiceAddress();
			
			
			String padUse = "0";
			String mobileCashUse = "0";
			if(!Check.Null(terminalType) && terminalType.equals("pad")){
				padUse = "1";
			}
			
			if(!Check.Null(terminalType) && terminalType.equals("mobileCash")){
				mobileCashUse = "1";
			}
			
			List<level2Elm> shopDatas = req.getRequest().getShopList();
			String shopStr = "''";
			if(shopDatas != null && restrictShop.equals("limit")){ //类型为适用门店时 需要插入数据。
				for (level2Elm lv2 : shopDatas) {
					String shopId = lv2.getShopId(); 
					shopStr = shopStr + ",'"+shopId+"'";
				}
			}
			
			StringBuffer shopSqlBuffer = new StringBuffer();
			String shopSql = "";
			
			//update 服务里必须得加 templateNo 不等于当前 模板。 修改的时候当前模板编码是不让改的
			shopSqlBuffer.append(" select a.templateNO , a.restrictshop , b.shopId "
					+ " FROM  DCP_PADGUIDE_BASESET a "
					+ " LEFT JOIN DCP_PADGUIDE_BASESET_PICKSHOP b  ON a.eId = b.eid AND a.templateNo = b.templateNo"
					+ " WHERE a.eId = '"+eId+"' and a.templateNo != '"+templateNo+"'" );
			
			if(!Check.Null(terminalType) && terminalType.equals("pad")){
				shopSqlBuffer.append( " and pad_use = '1' " );
			}
			
			if(!Check.Null(terminalType) && terminalType.equals("mobileCash")){
				shopSqlBuffer.append( " and mobileCash_use = '1' " );
			}
			
			shopSqlBuffer.append(" AND  ( a.restrictshop = 'noLimit' ");
			
			if(!shopStr.trim().equals("''")){
				shopSqlBuffer.append(" or b.shopId in( "+shopStr+")   " );
			}
			
			shopSqlBuffer.append(" ) ");
			shopSql = shopSqlBuffer.toString();
			
			List<Map<String, Object>> repeatDatas = this.doQueryData(shopSql, null);
			
			String repeatShopStr = "";
			String allTemplateNo = "";
			if(repeatDatas != null){
				
				for (Map<String, Object> map : repeatDatas) {
					String restrictshop = map.get("RESTRICTSHOP").toString();
					if(restrictshop.equals("limit")){ //存在适用门店
						String evShopId = map.get("SHOPID").toString();
						
						for (level2Elm lv2 : shopDatas) { // 多次一举
							String shopId = lv2.getShopId(); 
							if(shopId.equals(evShopId)){
								repeatShopStr = repeatShopStr + map.get("SHOPID").toString() + " ";
							}
						}
						
						res.setServiceDescription("已存在适用于以下门店的模板"+repeatShopStr);
						res.setSuccess(false);
						res.setServiceStatus("200");
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在适用于以下门店的模板"+repeatShopStr);
						
					}
					
					if(restrictshop.equals("noLimit")){
						allTemplateNo = allTemplateNo + map.get("TEMPLATENO").toString() +" ";
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在模板编号 "+allTemplateNo+ "适用于全部门店");
					}
					
				}
				
			}
			
			
			UptBean ub2 = new UptBean("DCP_PADGUIDE_BASESET");
			ub2.addUpdateValue("UPDATESERVICEADDRESS", new DataValue(updateServiceAddress, Types.VARCHAR));
			ub2.addUpdateValue("POSSERVICEADDRESS", new DataValue(posServiceAddress, Types.VARCHAR));
			ub2.addUpdateValue("MEMBERSERVICEADDRESS", new DataValue(memberServiceAddress, Types.VARCHAR));
			ub2.addUpdateValue("PAYSERVICEADDRESS", new DataValue(payServiceAddress, Types.VARCHAR));
			
			ub2.addUpdateValue("USERCODE", new DataValue(userCode, Types.VARCHAR));
			ub2.addUpdateValue("USERKEY", new DataValue(userKey, Types.VARCHAR));
			ub2.addUpdateValue("SPREADAPPNO", new DataValue(spreadAppNo, Types.VARCHAR));
			ub2.addUpdateValue("SPREADAPPNAME", new DataValue(spreadAppName, Types.VARCHAR));
			ub2.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
			ub2.addUpdateValue("PICTUREGETADDRESS", new DataValue(pictureGetAddress, Types.VARCHAR));
			ub2.addUpdateValue("PAD_USE", new DataValue(padUse, Types.VARCHAR));
			ub2.addUpdateValue("MOBILECASH_USE", new DataValue(mobileCashUse, Types.VARCHAR));
			
			ub2.addUpdateValue("LASTMODIOPID", new DataValue(lastmodiopid, Types.VARCHAR));
			ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(lastmodiOpName, Types.VARCHAR));
			ub2.addUpdateValue("LASTmoditime", new DataValue(lastmoditime, Types.DATE));
			// condition
			ub2.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub2));

			DelBean db1 = new DelBean("DCP_PADGUIDE_BASESET_PICKSHOP");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
//			List<level2Elm> shopDatas = req.getRequest().getShopList();
			if(shopDatas != null && restrictShop.equals("limit")){ //类型为适用门店时 需要插入数据。
				
				int item = 1;
				for (level2Elm lv2 : shopDatas) {
					
					String shopId = lv2.getShopId();
					
					String[] columns_hms ={"EID","TEMPLATENO","SERIALNO","SHOPID","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
					DataValue[] insValue_hms = new DataValue[] 
					{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateNo, Types.VARCHAR), 
						new DataValue(item, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(lastmodiopid, Types.VARCHAR),
						new DataValue(lastmodiOpName, Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE),
					};
					
					InsBean ib_hms = new InsBean("DCP_PADGUIDE_BASESET_PICKSHOP", columns_hms);
					ib_hms.addValues(insValue_hms);
					this.addProcessData(new DataProcessBean(ib_hms));
					item = item + 1;
				}
			}

			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！");
			res.setSuccess(false);
			res.setServiceStatus("200");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BasicSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BasicSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BasicSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BasicSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_BasicSetUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BasicSetUpdateReq>(){};
	}

	@Override
	protected DCP_BasicSetUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BasicSetUpdateRes();
	}

	
}
