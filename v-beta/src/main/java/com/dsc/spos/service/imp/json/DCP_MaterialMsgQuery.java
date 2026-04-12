
package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MaterialMsgQueryReq;
import com.dsc.spos.json.cust.res.DCP_MaterialMsgQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
 
/**
 * 物料报单查询
 * @author yuanyy 
 *
 */
public class DCP_MaterialMsgQuery extends SPosBasicService<DCP_MaterialMsgQueryReq, DCP_MaterialMsgQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_MaterialMsgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    
	    //必传值不为空
	    String bDate = req.getRequest().getbDate();
	    String pfNo = req.getRequest().getPfNo();
	    if(Check.Null(bDate)){
	    	errCt++;
	    	errMsg.append("单据日期不可为空值, ");
	      	isFail = true;
	    }
	    
	    if(Check.Null(pfNo)){
	    	errCt++;
	    	errMsg.append("单据编码不可为空值, ");
	    	isFail = true;
	    }
	    
	    if (isFail){
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }    
	    return isFail;
	}

	@Override
	protected TypeToken<DCP_MaterialMsgQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MaterialMsgQueryReq>(){};
	}

	@Override
	protected DCP_MaterialMsgQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MaterialMsgQueryRes();
	}

	@Override
	protected DCP_MaterialMsgQueryRes processJson(DCP_MaterialMsgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_MaterialMsgQueryRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> matDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_MaterialMsgQueryRes.level1Elm>());
			
			String PFNO = req.getRequest().getPfNo();
			res.setPfNo(PFNO);
			
			if(matDatas.size() > 0){
				
				String bDate = matDatas.get(0).get("BDATE").toString();
				String rDate = matDatas.get(0).get("RDATE").toString();
				String pfOrderType = matDatas.get(0).getOrDefault("PFORDERTYPE","").toString();
				
				res.setrDate(rDate);
				res.setPfOrderType(pfOrderType);
				res.setbDate(bDate);
				
				int item = 1;
				
				for (Map<String, Object> map : matDatas) {
					
					// 不按照库里item 排序， 按照商品编码排序，给前端的 item 要重新排序
//					String item = map.get("ITEM").toString();
					String materialPluNo = map.get("MATERIALPLUNO").toString();
					String materialPluName = map.get("PLUNAME").toString();
					String punit = map.get("PUNIT").toString();
					String pqty = map.get("PQTY").toString(); // 报单参考数量， BOM计算出原料数量，再经过经济批量计算后的数量
					String wunit = map.get("BASEUNIT").toString();
					String wqty = map.get("BASEQTY").toString();
					String price = map.get("PRICE").toString();
					String amt = map.get("AMT").toString();
					String reqWQty = map.get("REQWQTY").toString();
					String kQty = map.get("KQTY").toString(); //理论数量， BOM信息计算出的原料数量，不经过经济批量计算
					String refAmt = map.get("REFAMT").toString();
					String mainPlu = map.get("MAINPLU").toString();
					
					String minQty = map.getOrDefault("MIN_QTY", "0").toString();
					String maxQty = map.getOrDefault("MAX_QTY", "99999").toString();
					String mulQty = map.getOrDefault("MUL_QTY", "0").toString();
					
					
					// 2019-10-28 增加以下字段，用于物料报单
					String dbyQty = map.getOrDefault("DBYQTY", "0").toString();
					String yWQty = map.getOrDefault("YBASEQTY", "0").toString();
					String rQty = map.getOrDefault("RQTY", "0").toString();//实收数量
					String uQty = map.getOrDefault("UQTY", "0").toString();//修改/调整量
					String dQty = map.getOrDefault("DQTY", "0").toString();//差异量
					String tQty = map.getOrDefault("TQTY", "0").toString();//今日底货量
					
					// 2020-03-09 增加以下字段，用于计划报单
					String fileName = map.getOrDefault("FILENAME", "").toString();
					String spec = map.getOrDefault("SPEC", "").toString();
					String materialUnitName = map.getOrDefault("MATERIALUNITNAME", "").toString();
					String porderNo = map.getOrDefault("PORDERNO", "").toString();
					String porderStatus = map.getOrDefault("PORDERSTATUS", "").toString();
					// distriPrice 进货价要查
					String distriPrice = map.getOrDefault("DISTRIPRICE", "0").toString();
					String punitUdlength = map.getOrDefault("PUNITUDLENGTH", "0").toString();
					
					DCP_MaterialMsgQueryRes.level1Elm lv1 = res.new level1Elm();
					lv1.setItem(item + "");
					lv1.setMaterialPluNo(materialPluNo);
					lv1.setMaterialPluName(materialPluName);
					lv1.setMaterialUnit(punit);
					lv1.setpQty(pqty);
					lv1.setPrice(price);
					lv1.setMaterialKQty(kQty);
					lv1.setRefWQty(reqWQty);
					lv1.setMinQty(minQty);
					lv1.setMaxQty(maxQty);
					lv1.setMulQty(mulQty);
					lv1.setMainPlu(mainPlu);
					lv1.setDbyQty(dbyQty);
					lv1.setyWQty(yWQty);
					lv1.setrQty(rQty);
					lv1.setuQty(uQty);
					lv1.setdQty(dQty);
					lv1.settQty(tQty);
					
					lv1.setSpec(spec);
					lv1.setFileName(fileName);
					lv1.setMaterialUnitName(materialUnitName);
					lv1.setPorderNO(porderNo);
					lv1.setStatus(porderStatus); 
					lv1.setPunitUdLength(punitUdlength);
					lv1.setDistriPrice(distriPrice);
					
					res.getDatas().add(lv1);
					
					item = item + 1;
				}
				
				res.setSuccess(true);
			}
					
			
			
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println(e.getMessage());
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_MaterialMsgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		String PFNO = req.getRequest().getPfNo();
		String langType = req.getLangType();
		String keyTxt = req.getRequest().getKeyTxt();
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String today = df.format(cal.getTime());
		
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" SELECT a.PFNO , a.bDate, a.item , a.MATERIAL_PLUNO as materialPluNo , b.plu_name AS pluName , "
				+ " gd.spec "
				//+ ", gd.fileName"
				+ " , u.uname AS materialUnitName , u2.udlength as punitudlength, pf.rDate , pf.pfordertype ,  "
				+ " a.pUnit , a.pqty , a.baseunit , a.baseqty , a.price , a.amt , nvl(ns.qty , '0') as reqWqty, "
				+ " a.kqty , a.kAdjQty , a.ref_amt  as refAmt , a.MainPlu ,"
				+ " a.Dbyqty , a.ybaseqty, NVL(f.pqty, 0) as rqty , a.min_Qty , a.max_qty , a.mul_qty , "
				//+ " a.rqty, "
				+ " a.uqty , a.dqty , a.tqty , a.porderNO , a.status as porderStatus "
				+ " FROM DCP_porder_forecast_material a "
				+ " LEFT JOIN DCP_GOODS_lang b ON a.EID = b.EID AND a.MATERIAL_PLUNO = b.pluNo AND b.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_GOODS gd ON a.EID = gd.EID AND a.material_pluno = gd.pluNo "
				+ " LEFT JOIN DCP_porder_forecast pf ON a.EID = pf.EID AND a.SHOPID = pf.SHOPID AND a.Pfno = pf.Pfno "
				+ " LEFT JOIN DCP_UNIT_lang u ON a.EID = u.EID AND a.punit = u.unit AND u.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_UNIT  u2 ON a.EID = u2.EID AND a.punit = u2.unit   "
				
				
				+ " LEFT JOIN DCP_stock_day_static c  ON a.EID = c.EID AND a.SHOPID = c.organizationno AND a.material_pluno = c.pluno AND c.eDate = to_char(to_date('"+bDate+"', 'yyyyMMdd') -1 , 'yyyyMMdd') "
				+ " LEFT JOIN DCP_stock_day_static d  ON a.EID = d.EID AND a.SHOPID = d.organizationno AND a.material_pluno = d.pluno AND d.eDate = to_char(to_date('"+bDate+"', 'yyyyMMdd') -2 , 'yyyyMMdd') "
				
				+ " LEFT JOIN ("
				+ "		select a.EID, a.SHOPID , a.status , a.doc_Type , b.pluNo , sum(b.pqty) as pqty , b.punit "
				+ " from DCP_stockin a "
				+ " left join DCP_stockin_detail b on a.EID = b.EID and a.SHOPID = b.SHOPID and a.stockinNO = b.stockinNo and a.BDATE=b.BDATE "
				+ " where a.EID = '"+eId+"' and a.SHOPID = '"+shopId+"' and a.bdate = '"+today+"' "
				+ " and a.status = '2' and a.doc_type = '0' " ); // 查已完成的配送收货单，获得今日收货量 
		
		sqlbuf.append(" group by  a.EID, a.SHOPID  , a.status , a.doc_Type , b.pluNo , b.punit "
				+ " order by pluNO ) f "
				+ " on a.EID = f.EID and a.SHOPID = f.SHOPID and a.MATERIAL_PLUNO = f.pluNo"
				+ " LEFT JOIN DCP_STOCK  ns on a.EID = ns.EID and a.SHOPID = ns.organizationno and a.MATERIAL_PLUNO = ns.pluNo "				
				+ " where a.EID = '"+eId+"' and  a.SHOPID = '"+shopId+"' and  a.PFNO = '"+PFNO+"' "
				+ " " );
								
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.MATERIAL_PLUNO like '%%"+ keyTxt +"%%' OR  b.plu_name LIKE '%%"+ keyTxt +"%%')  ");
		}
		
		sqlbuf.append(" order by b.pluNo, item ");
		sql =sqlbuf.toString();
		return sql;
	}
	
}
