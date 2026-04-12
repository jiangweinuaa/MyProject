package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BasicSetCreateReq;
import com.dsc.spos.json.cust.req.DCP_BasicSetCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_BasicSetCreateRes;
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
public class DCP_BasicSetCreate extends SPosAdvanceService<DCP_BasicSetCreateReq, DCP_BasicSetCreateRes> {

	@Override
	protected void processDUID(DCP_BasicSetCreateReq req, DCP_BasicSetCreateRes res) throws Exception {
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
			
			String createopid = req.getRequest().getCreateopid();
			String createOpName = req.getRequest().getCreateOpName();
			String createTime = req.getRequest().getCreateTime();
			String pictureGetAddress = req.getRequest().getPictureGetAddress();
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
			
			boolean isRepeat = false;
			isRepeat = this.checkRepeat(eId, templateNo);
			
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
			
			shopSqlBuffer.append(" select a.templateNO , a.restrictshop , b.shopId "
					+ " FROM  DCP_PADGUIDE_BASESET a "
					+ " LEFT JOIN DCP_PADGUIDE_BASESET_PICKSHOP b  ON a.eId = b.eid AND a.templateNo = b.templateNo"
					+ " WHERE a.eId = '"+eId+"' " );
			
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
			
			//DCP_PADGUIDE_BASESET_PICKSHOP 
			if(isRepeat){
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("模板编码"+templateNo+"已存在！");
			}else{
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
							new DataValue(createopid, Types.VARCHAR),
							new DataValue(createOpName, Types.VARCHAR),
							new DataValue(createTime, Types.DATE)
						};
						
						InsBean ib_hms = new InsBean("DCP_PADGUIDE_BASESET_PICKSHOP", columns_hms);
						ib_hms.addValues(insValue_hms);
						this.addProcessData(new DataProcessBean(ib_hms));
						
						item = item + 1;
					}
				}
	
				String[] columns_hm ={"EID","TEMPLATENO","UPDATESERVICEADDRESS","POSSERVICEADDRESS","MEMBERSERVICEADDRESS" ,
						"USERCODE","USERKEY","SPREADAPPNO", "SPREADAPPNAME","RESTRICTSHOP","CREATEOPID","CREATEOPNAME","CREATETIME",
						"LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","PICTUREGETADDRESS","PAD_USE" ,"MOBILECASH_USE","PAYSERVICEADDRESS"
				};
				DataValue[] insValue_hm = new DataValue[] 
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(templateNo, Types.VARCHAR), 
							new DataValue(updateServiceAddress, Types.VARCHAR),
							new DataValue(posServiceAddress, Types.VARCHAR),
							new DataValue(memberServiceAddress ,Types.VARCHAR),
							new DataValue(userCode, Types.VARCHAR),
							new DataValue(userKey, Types.VARCHAR),
							new DataValue(spreadAppNo ,Types.VARCHAR),
							new DataValue(spreadAppName ,Types.VARCHAR),
							new DataValue(restrictShop, Types.VARCHAR),
							new DataValue(createopid, Types.VARCHAR),
							new DataValue(createOpName ,Types.VARCHAR),
							new DataValue(createTime , Types.DATE),
							new DataValue(createopid, Types.VARCHAR), //修改人、时间等信息
							new DataValue(createOpName ,Types.VARCHAR),
							new DataValue(createTime , Types.DATE) ,
							new DataValue(pictureGetAddress ,Types.VARCHAR),
							new DataValue(padUse ,Types.VARCHAR),
							new DataValue(mobileCashUse ,Types.VARCHAR),
							new DataValue(payServiceAddress ,Types.VARCHAR)
						};
				
				InsBean ib_hm = new InsBean("DCP_PADGUIDE_BASESET", columns_hm);
				ib_hm.addValues(insValue_hm);
				this.addProcessData(new DataProcessBean(ib_hm)); 
				
				this.doExecuteDataToDB();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！"+e.getMessage());
			res.setSuccess(false);
			res.setServiceStatus("200");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BasicSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BasicSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BasicSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BasicSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_BasicSetCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BasicSetCreateReq>(){};
	}

	@Override
	protected DCP_BasicSetCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BasicSetCreateRes();
	}
	
	/**
	 * 验证模板编码是否重复
	 * @param eId
	 * @param templateNo
	 * @return
	 * @throws Exception
	 */
	private boolean checkRepeat(String eId, String templateNo) throws Exception{
		String sql = "";
	    boolean temp = false;
		sql = "select * from DCP_PADGUIDE_BASESET  "
			+ " where  EID = '"+eId+"' and templateNo = '"+templateNo+"' ";
		
		List<Map<String, Object>> reDatas = this.doQueryData(sql, null);
		if(reDatas != null && reDatas.size() > 0){
			temp = true;
		}
		
		return temp;
	}
	
	private boolean checkRepeat2(String eId, String templateNo) throws Exception{
		String sql = "";
	    boolean temp = false;
		sql = "select a.eId, a. , a.templateNo , b.shopId from DCP_PADGUIDE_BASESET_PICKSHOP a "
				+ " where  EID = '"+eId+"' and templateNo = '"+templateNo+"' ";
		
		List<Map<String, Object>> reDatas = this.doQueryData(sql, null);
		if(reDatas != null && reDatas.size() > 0){
			temp = true;
		}
		
		return temp;
	}

}
