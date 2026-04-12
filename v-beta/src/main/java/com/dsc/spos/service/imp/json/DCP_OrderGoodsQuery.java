package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：OrderGoodsGet
 * 服务说明：外卖商品查询
 * @author jinzma	 
 * @since  2019-03-11
 */
public class DCP_OrderGoodsQuery extends SPosBasicService<DCP_OrderGoodsQueryReq,DCP_OrderGoodsQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub	
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderGoodsQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderGoodsQueryReq>(){};
	}

	@Override
	protected DCP_OrderGoodsQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderGoodsQueryRes();
	}

	@Override
	protected DCP_OrderGoodsQueryRes processJson(DCP_OrderGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub	
		String sql=null;		
		DCP_OrderGoodsQueryRes res  = this.getResponse();
		try 
		{
			//单头查询
			sql = getQuerySql(req);
			String[] conditionValues = null;
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				//算總頁數
				String num = getQDataDetail.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("PLUNO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);				
				res.setDatas(new ArrayList<level1Elm>());
				for (Map<String, Object> oneData : getQHeader) 
				{
					level1Elm oneLv1 = res.new level1Elm();
					String pluNO = oneData.get("PLUNO").toString();
					String pluName = oneData.get("PLUNAME").toString();
					String categoryNO = oneData.get("CATEGORYNO").toString();
					String categoryName = oneData.get("CATEGORYNAME").toString();
					String description = oneData.get("DESCRIPTION").toString();
					String fileName = oneData.get("FILENAME").toString();
					String unit = oneData.get("UNIT").toString();
					String priority = oneData.get("PRIORITY").toString();	
					String materialID1 = oneData.get("MATERIALID1").toString();
					String materialID2 = oneData.get("MATERIALID2").toString();
					String materialID3 = oneData.get("MATERIALID3").toString();
					String materialID4 = oneData.get("MATERIALID4").toString();
					String materialID5 = oneData.get("MATERIALID5").toString();
					String materialID6 = oneData.get("MATERIALID6").toString();
					String materialID7 = oneData.get("MATERIALID7").toString();
					String materialID8 = oneData.get("MATERIALID8").toString();
					String materialID9 = oneData.get("MATERIALID9").toString();
					String materialID10 = oneData.get("MATERIALID10").toString();
					String material1 = oneData.get("MATERIAL1").toString();
					String material2 = oneData.get("MATERIAL2").toString();
					String material3 = oneData.get("MATERIAL3").toString();
					String material4 = oneData.get("MATERIAL4").toString();
					String material5 = oneData.get("MATERIAL5").toString();
					String material6 = oneData.get("MATERIAL6").toString();
					String material7 = oneData.get("MATERIAL7").toString();
					String material8 = oneData.get("MATERIAL8").toString();
					String material9 = oneData.get("MATERIAL9").toString();
					String material10 = oneData.get("MATERIAL10").toString();					
					String isAllTimeSell = oneData.get("ISALLTIMESELL").toString();
					String beginDate = oneData.get("BEGINDATE").toString();
					String endDate = oneData.get("ENDDATE").toString();
					String sellWeek = oneData.get("SELLWEEK").toString();
					String sellTime = oneData.get("SELLTIME").toString();
					String memo = oneData.get("MEMO").toString();
					String status = oneData.get("STATUS").toString();

					oneLv1.setPluNO(pluNO);
					oneLv1.setPluName(pluName);
					oneLv1.setCategoryNO(categoryNO);
					oneLv1.setCategoryName(categoryName);
					oneLv1.setDescription(description);
					oneLv1.setFileName(fileName);
					oneLv1.setUnit(unit);
					oneLv1.setPriority(priority);
					oneLv1.setMaterialID1(materialID1);
					oneLv1.setMaterialID2(materialID2);
					oneLv1.setMaterialID3(materialID3);
					oneLv1.setMaterialID4(materialID4);
					oneLv1.setMaterialID5(materialID5);
					oneLv1.setMaterialID6(materialID6);
					oneLv1.setMaterialID7(materialID7);
					oneLv1.setMaterialID8(materialID8);
					oneLv1.setMaterialID9(materialID9);
					oneLv1.setMaterialID10(materialID10);
					oneLv1.setMaterial1(material1);
					oneLv1.setMaterial2(material2);
					oneLv1.setMaterial3(material3);
					oneLv1.setMaterial4(material4);
					oneLv1.setMaterial5(material5);
					oneLv1.setMaterial6(material6);
					oneLv1.setMaterial7(material7);
					oneLv1.setMaterial8(material8);
					oneLv1.setMaterial9(material9);
					oneLv1.setMaterial10(material10);		
					oneLv1.setIsAllTimeSell(isAllTimeSell);
					oneLv1.setBeginDate(beginDate);
					oneLv1.setEndDate(endDate);
					oneLv1.setSellWeek(sellWeek);
					oneLv1.setSellTime(sellTime);		
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status);
					oneLv1.setSpecDatas(new ArrayList<DCP_OrderGoodsQueryRes.level2Spec>());	
					oneLv1.setAttrDatas(new ArrayList<DCP_OrderGoodsQueryRes.level2Attr>());	

					//region SPEC
					Map<String, Boolean> condition_spec = new HashMap<String, Boolean>(); //查詢條件
					condition_spec.put("PLUNO", true);
					condition_spec.put("SPECNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader_SPEC = MapDistinct.getMap(getQDataDetail, condition_spec);							
					for (Map<String, Object> oneData_SPEC : getQHeader_SPEC) 
					{
						DCP_OrderGoodsQueryRes.level2Spec onelv2_SPEC = res.new level2Spec();
						String pluNo_SPEC = oneData_SPEC.get("PLUNO").toString();
						if(pluNo_SPEC.equals(pluNO))
						{						
							String specNO = oneData_SPEC.get("SPECNO").toString();
							String specName = oneData_SPEC.get("SPECNAME").toString();
							String price = oneData_SPEC.get("PRICE").toString();
							String stockQty = oneData_SPEC.get("STOCKQTY").toString();
							String packageFee = oneData_SPEC.get("PACKAGEFEE").toString();
							String isOnshelf = oneData_SPEC.get("ISONSHELF").toString();
							String netWeight = oneData_SPEC.get("NETWEIGHT").toString();

							onelv2_SPEC.setSpecNO(specNO);
							onelv2_SPEC.setSpecName(specName);
							onelv2_SPEC.setPrice(price);
							onelv2_SPEC.setStockQty(stockQty);
							onelv2_SPEC.setPackageFee(packageFee);
							onelv2_SPEC.setIsOnshelf(isOnshelf);
							onelv2_SPEC.setNetWeight(netWeight);
							onelv2_SPEC.setStatus("100");
							oneLv1.getSpecDatas().add(onelv2_SPEC);			
							
							onelv2_SPEC = null;
						}
					}

					//region ATTRIB
					Map<String, Boolean> condition_attr = new HashMap<String, Boolean>(); //查詢條件
					condition_attr.put("PLUNO", true);
					condition_attr.put("ATTRNO", true);
					condition_attr.put("ATTRVALUE", true);

					//调用过滤函数
					List<Map<String, Object>> getQHeader_ATTR = MapDistinct.getMap(getQDataDetail, condition_attr);							
					for (Map<String, Object> oneData_ATTR : getQHeader_ATTR) 
					{
						DCP_OrderGoodsQueryRes.level2Attr onelv2_ATTR = res.new level2Attr();
						String pluNo_ATTR = oneData_ATTR.get("PLUNO").toString();
						if(pluNo_ATTR.equals(pluNO))
						{						
							String attrName = oneData_ATTR.get("ATTRNAME").toString();
							String attrValue = oneData_ATTR.get("ATTRVALUE").toString();						
							if (Check.Null(attrName)||Check.Null(attrValue)) continue;							
							onelv2_ATTR.setAttrName(attrName);
							onelv2_ATTR.setAttrValue(attrValue);
							onelv2_ATTR.setStatus("100");
							oneLv1.getAttrDatas().add(onelv2_ATTR);			
							onelv2_ATTR = null;
						}
					}

					res.getDatas().add(oneLv1);
					
					oneLv1 = null;
				}					
			}
			else
			{
				res.setDatas(new ArrayList<level1Elm>());
				totalRecords = 0;
				totalPages = 0;	
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);			
			return res;
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		String categoryNO = req.getCategoryNO();
		String belFirm = req.getOrganizationNO();

		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append( "select * from ( "
				+ " select count(*) over() num,row_number() over (order by a.pluno) rn , "
				+ " a.pluno,a.pluname,a.categoryno,d.categoryname,a.description,a.filename,a.unit,a.priority, "				
				+ " a.MATERIALID1,a.MATERIALID2,a.MATERIALID3,a.MATERIALID4,a.MATERIALID5,a.MATERIALID6,"
				+ " a.MATERIALID7,a.MATERIALID8,a.MATERIALID9,a.MATERIALID10, "
				+ " a.MATERIAL1,a.MATERIAL2,a.MATERIAL3,a.MATERIAL4,a.MATERIAL5,a.MATERIAL6,a.MATERIAL7,a.MATERIAL8, "
				+ " a.MATERIAL9,a.MATERIAL10,a.ISALLTIMESELL,a.BEGINDATE,a.ENDDATE,a.SELLWEEK,a.SELLTIME,a.MEMO,a.status, "
				+ "	b.specno,b.specname,b.price,b.stockqty,b.packagefee,b.isOnshelf,b.NETWEIGHT,c.attrname,c.attrvalue "
				+ " from OC_goods a "
				+ " inner join OC_goods_spec b on a.EID=b.EID and a.pluno=b.pluno and a.BELFIRM=b.BELFIRM"
				+ "	 left join OC_goods_attr c on a.EID=c.EID and a.pluno=c.pluno and a.BELFIRM=c.BELFIRM "
				+ " inner join OC_category d on a.EID=d.EID and a.categoryno=d.categoryno "
				+ " where a.EID='"+ eId + "' " );
		
		if (belFirm != null && belFirm.length()>0)
		{
			sqlbuf.append(" and (a.BELFIRM = '"+belFirm+"'  ) ");
		}

		if (keyTxt != null && keyTxt.length()>0)
		{	  	
			sqlbuf.append(" and (a.PLUNO like '%"+keyTxt+"%' or a.PLUNAME like '%"+keyTxt+"%'  or a.tbmemo like '%"+keyTxt+"%' )  ");
		}
		
		
		if (categoryNO != null && categoryNO.length()>0)
		{	  	
			sqlbuf.append(" and (d.CATEGORYNO = '"+categoryNO+"'  ) ");
		}

		sqlbuf.append( " order by a.pluno	)  "
				+ " where  rn>"+startRow+" and rn<="+(startRow+pageSize)+"  ");

		sql = sqlbuf.toString();
		return sql;
	}

}
