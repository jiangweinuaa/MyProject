package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SaleRecordDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_SaleRecordDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 面销记录
 * @author yuanyy 
 *
 */
public class DCP_SaleRecordDetailQuery extends SPosBasicService<DCP_SaleRecordDetailQueryReq, DCP_SaleRecordDetailQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SaleRecordDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SaleRecordDetailQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleRecordDetailQueryReq>(){};
	}

	@Override
	protected DCP_SaleRecordDetailQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleRecordDetailQueryRes();
	}

	@Override
	protected DCP_SaleRecordDetailQueryRes processJson(DCP_SaleRecordDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_SaleRecordDetailQueryRes res = null;
		res = this.getResponse();
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			res.setIntentionalProduct(new ArrayList<DCP_SaleRecordDetailQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				
				for (Map<String, Object> map : queryDatas) {
					
					DCP_SaleRecordDetailQueryRes.level1Elm lv1= res.new level1Elm() ;
					String item = map.get("ITEM").toString();
					String pluNo = map.get("PLUNO").toString();
					String pluName = map.get("PLUNAME").toString();
					String categoryName = map.get("CATEGORYNAME").toString();
					String wunit = map.get("WUNIT").toString();
					String wunitName = map.getOrDefault("WUNITNAME", "").toString();
					String price = map.get("PRICE").toString();
					String imageFileName = map.getOrDefault("IMAGEFILENAME", "").toString();
					String originalPrice = map.getOrDefault("ORIGINALPRICE", "0").toString();
					
					lv1.setItem(item);
					lv1.setPluName(pluName);
					lv1.setPluNo(pluNo);
					lv1.setCategoryName(categoryName);
					lv1.setWunit(wunit);
					lv1.setWunitName(wunitName);
					lv1.setPrice(price);
					lv1.setImageFileName(imageFileName);
					lv1.setOriginalPrice(originalPrice);
					
					res.getIntentionalProduct().add(lv1);
					
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！");
			res.setServiceStatus("200");
			res.setSuccess(false);
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getQuerySql(DCP_SaleRecordDetailQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
//		int pageNumber=req.getPageNumber();
//		int pageSize=req.getPageSize();
//		int startRow=(pageNumber-1) * pageSize;
		
		String langType = req.getLangType();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String opNo = req.getOpNO();
		
		String salesRecordNo = req.getRequest().getSalesRecordNo();
		
		//查询门店当前登陆人员的面销记录单号
		sqlbuf.append(" SELECT a.SALESRECORDNO, a.item ,a.pluNo ,b.plu_name as  pluName , "
				+ " a.categoryName , a.wunit , a.price , c.unit_Name  as wunitName, a.fileName as IMAGEFILENAME,a.originalPrice "
				+ " FROM  DCP_SALESRECORD_detail a  "
				+ " left join DCP_GOODS_lang b on a.eid = b.EID and a.pluNo = b.pluNo and b.lang_type = '"+langType+"' "
				+ " left join DCP_UNIT_lang c on a.eid = c.EID and a.wunit = c.unit and c.lang_type = '"+langType+"'"
				+ " where a.eid = '"+eId+"' and a.shopId = '"+shopId+"'  "
				+ " and a.salesrecordNo = '"+salesRecordNo+"' and b.status='100' "
				+ " order by a.item " );
				
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
