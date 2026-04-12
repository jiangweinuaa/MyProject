package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ProcessTaskMaterialQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskMaterialQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 加工任务原料查询	
 * @author yuanyy	
 * 2019-12-13
 */
public class DCP_ProcessTaskMaterialQuery extends SPosBasicService<DCP_ProcessTaskMaterialQueryReq, DCP_ProcessTaskMaterialQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_ProcessTaskMaterialQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ProcessTaskMaterialQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ProcessTaskMaterialQueryReq>(){};
	}

	@Override
	protected DCP_ProcessTaskMaterialQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ProcessTaskMaterialQueryRes();
	}

	@Override
	protected DCP_ProcessTaskMaterialQueryRes processJson(DCP_ProcessTaskMaterialQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_ProcessTaskMaterialQueryRes res = null;
		res = this.getResponse();
		
		try {
			
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> materialDatas = this.doQueryData(sql, null);
			
			res.setDatas(new ArrayList<DCP_ProcessTaskMaterialQueryRes.level1Elm>());
			if(materialDatas != null && materialDatas.size() > 0){
				
				for (Map<String, Object> map : materialDatas) {
					DCP_ProcessTaskMaterialQueryRes.level1Elm lv1 = res.new level1Elm();
					String materialPluNo = map.getOrDefault("MATERIALPLUNO", "").toString();
					String materialPluName = map.getOrDefault("MATERIALPLUNAME", "").toString();
					String materialBomUnit = map.getOrDefault("BOMUNIT", "").toString(); //原料BOM单位
					String totBomQty = map.getOrDefault("TOTBOMQTY", "0").toString(); //原料BOM单位数量
					
					String materialPUnit = map.getOrDefault("MATERIALPUNIT", "").toString(); //原料要货单位
					String materialWUnit = map.getOrDefault("MATERIALWUNIT", "").toString(); //原料库存单位
					
					String wUnitUdLength = map.getOrDefault("WUNITUDLENGTH", "2").toString();
					String pUnitUdLengthStr = map.getOrDefault("PUNITUDLENGTH", "2").toString();
					
					if(pUnitUdLengthStr == null || pUnitUdLengthStr.trim().equals("")){
						pUnitUdLengthStr = "2";
					}
					
					int pUnitUdLength = Integer.parseInt(pUnitUdLengthStr);
					
					String minQty = map.getOrDefault("MINQTY","0").toString(); //最小要货量
					String maxQty = map.getOrDefault("MAXQTY","99999").toString(); //最大要货量
					String mulQty = map.getOrDefault("MULQTY","0").toString(); //要货倍量
					
					double bomUnitRatio = 1;
					double pUnitRatio = 1;
					//需要计算不同单位对应数量
					//原料 BOM单位==》库存单位 ==》 要货单位, 
					if(!materialWUnit.equals(materialBomUnit)){ 
						
						List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
								materialPluNo, materialBomUnit); 
					
						if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + materialPluNo + " 找不到对应的 "+materialBomUnit+" 到"+materialWUnit+" 的换算关系");
						}

						bomUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
						
					}
					
					if(!materialWUnit.equals(materialPUnit)){
						
						List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
								materialPluNo, materialPUnit);
						
						if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + materialPluNo + " 找不到对应的 "+materialPUnit+" 到"+materialWUnit+" 的换算关系");
						}

						pUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
						
					}
					
					double pQty = Double.parseDouble(totBomQty) * bomUnitRatio / pUnitRatio ; //要货单位数量
					
//					int num = 1 ;
					
					double mulQtyDou = Double.parseDouble(mulQty);
					double minQtyDou = Double.parseDouble(minQty);
					if(pQty <= minQtyDou){ //如果 预估数量 < 最小要货量， 预估数量==最小要货量。
						pQty = minQtyDou;
					}else{
						
						if(mulQtyDou > 0){ //倍量有可能为0 ，表示任意数都可以。

							double zh = pQty / mulQtyDou ;
							double yu = pQty % mulQtyDou ;
							
							if(yu > 0){
								pQty = ( new Double(zh).intValue() + 1 ) * mulQtyDou;
							}
							
//							BigDecimal zhb = new BigDecimal(zh);
//							double maxNum = zhb.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
//							String a = new DecimalFormat("0").format(maxNum);
//							num = Integer.parseInt(a);
//							
//							Double[] dArr = new Double[num];
//							if(num > 0 ){
//								for (int j = 0; j < num ; j++) {
//									double d2 = mulQtyDou * j ;
//									BigDecimal b = new BigDecimal(d2);
//							        /*setScale 第一个参数为保留位数 第二个参数为舍入机制
//							         BigDecimal.ROUND_DOWN 表示不进位 
//							         BigDecimal.ROUND_UP表示进位*/
//							        d2 = b.setScale(pUnitUdLength, BigDecimal.ROUND_HALF_UP).doubleValue();
//									dArr[j] = new Double(d2);
//								}
//								
//							}
//							
//							pQty = MyCommon.getNumberThree(dArr, pQty);
						}
						
						
						if(pQty > Double.parseDouble(maxQty)){ //如果预估量 > 最大要货量， 预估量==最大要货量即可
							pQty = Double.parseDouble(maxQty);
						}
						
					}
					
					double wQty = pQty / pUnitRatio; //库存单位数量
					
					lv1.setPluNo(materialPluNo);
					lv1.setPluName(materialPluName);
					lv1.setpUnit(materialPUnit);
					lv1.setpQty(String.format("%."+pUnitUdLength+"f", pQty)); // 数量保留小数位数，从单位信息中查询出
					lv1.setwUnit(materialWUnit);
					lv1.setwQty(String.format("%."+wUnitUdLength+"f", wQty)); 
					res.getDatas().add(lv1);
				}
				
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败："+e.getMessage());
			res.setServiceStatus("200");
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ProcessTaskMaterialQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		
		String processTaskNo = req.getRequest().getProcessTaskNo();
		String pTemplateNo = req.getRequest().getpTemplateNo();
		
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String _SysDATE = df.format(cal.getTime());
		
		sqlbuf.append(" "
+ " SELECT "
+ " ss.EID, ss.SHOPID, ss.processtaskno, ss.materialpluno, ss.materialpluname, ss.bomunit, ss.materialwunit, ss.materialpunit, ss.wunitudlength, ss.punitudlength  "
+ " , sum(ss.bomqty) as totbomqty ,"
+ " nvl(nvl(ts.minQty , gs.min_Qty ) , 0 )AS minQTy , nvl( nvl(ts.mulQty , gs.mul_Qty ) , 0)AS mulQty ,nvl( nvl(ts.maxQty , gs.max_Qty ) , 99999 ) AS maxQty "
+ " FROM ( "
+ " SELECT a.EID , a.SHOPID , a.processtaskno , b.pluNo , b.pqty , b.punit , "
+ " c.unit AS bomMainUnit , b.wunit , b.wqty , b.unit_ratio AS unitRatio ,"
+ " c.material_pluNo as  materialPluNo  , c.material_punit AS bomUnit , c.baseQty , c.material_baseqTY   AS materialBaseQty ,"
+ " ( c.material_baseQty / c.baseQty * b.pQty  ) AS bomQty , "
+ " d.wunit AS materialWUnit , d.punit AS materialPUnit  , e.plu_name as materialPluName "
+ "  , f1.udlength  AS wUnitUdLength  , f2.udlength  AS pUnitUdLength  "
+ " FROM  DCP_processTask a  "
+ " LEFT JOIN DCP_processtask_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.processtaskno = b.processTaskNo and a.BDATE=b.BDATE "

+ " LEFT JOIN ( "
+ " select a.EID,a.pluno,a.unit,a.material_pluno,a.material_qty as MATERIAL_BASEQTY,a.material_unit as MATERIAL_PUNIT,"
+ " a.qty as BASEQTY,a.BOM_type "
+ " from DCP_BOM a left join DCP_BOM_shop b "
+ " on a.EID=b.EID and a.pluno=b.pluno and b.EID='"+eId+"' and  b.MATERIAL_BDATE<='"+_SysDATE+"'  "
+ " and (b.MATERIAL_EDATE>='"+_SysDATE+"' or b.MATERIAL_EDATE is null) "
+ " and b.organizationno='"+shopId+"' and b.status='100' and a.bom_type=b.bom_type "
+ " where a.EID='"+eId+"' and a.effdate<='"+_SysDATE+"'  and  a.MATERIAL_BDATE<='"+_SysDATE+"'"
+ " and ( a.MATERIAL_EDATE>='"+_SysDATE+"'  or a.MATERIAL_EDATE is null ) "
+ " and a.status='100' and a.bom_type='0' and b.pluno is null  "
+ " union all "
+ " select EID,pluno,unit,material_pluno,material_qty as MATERIAL_BASEQTY,material_unit as MATERIAL_PUNIT,qty as BASEQTY,BOM_type from DCP_BOM_shop "
+ " where EID='"+eId+"' and MATERIAL_BDATE<='"+_SysDATE+"' and (MATERIAL_EDATE>='"+_SysDATE+"' or MATERIAL_EDATE is null) "
+ " and organizationno='"+shopId+"'  and  status='100'  and bom_type='0'  "

+ " ) c  ON b.EID = c.EID AND b.pluNO = c.pluNo  AND b.punit = c.unit " // 成品的加工任务单位、要货单位、Bom 成品单位，必须相同 
+ " LEFT JOIN DCP_GOODS d ON c.EID = d.EID and c.material_PluNo = d.pluNo AND d.status='100' "
+ " LEFT JOIN DCP_GOODS_LANG e on c.EID = e.EID and c.material_pluNo = e.pluNo and e.lang_type = '"+langType+"'"
+ " LEFT JOIN DCP_UNIT f1 ON d.EID = f1.EID AND  d.wunit = f1.unit  AND f1.status='100' "
+ " LEFT JOIN DCP_UNIT f2 ON d.EID = f2.EID AND  d.punit = f2.unit  AND f2.status='100' "
+ " WHERE a.EID = '"+eId+"' AND  a.SHOPID = '"+shopId+"' AND a.processtaskno = '"+processTaskNo+"' "

+ " ORDER BY materialPluNo   "
+ "  ) ss "
+ "  LEFT JOIN DCP_GOODS_shop gs ON ss.EID = gs.EID AND ss.SHOPID = gs.organizationno AND ss.materialpluno = gs.pluno AND gs.status='100' "
+ "  LEFT JOIN (  "       
+ "             SELECT a.EID , b.pluno , b.punit , b.min_qty AS minQty , b.mul_qty AS mulQty , b.max_qty AS maxQty "
+ "             FROM DCP_ptemplate a "
+ "             LEFT JOIN DCP_ptemplate_detail b ON a.EID = b.EID AND a.ptemplateno = b.ptemplateno AND a.doc_type = b.doc_type "
+ "             WHERE a.EID = '"+eId+"'  AND  a.ptemplateNo = '"+pTemplateNo+"'  AND a.status='100' AND b.status='100'"
+ "          )  ts ON ss.EID = ts.EID AND ts.pluno = ss.materialPluNo "
+ "  GROUP BY ss.EID, ss.SHOPID, ss.processtaskno, ss.materialpluno, ss.materialpluname, ss.bomunit, ss.materialwunit, ss.materialpunit, ss.wunitudlength, ss.punitudlength ,"
+ "  ts.minQty , gs.min_Qty , ts.mulQty , gs.mul_Qty ,ts.maxQty , gs.max_Qty   "
+ "  ORDER BY materialPluNo "

);
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	
}
