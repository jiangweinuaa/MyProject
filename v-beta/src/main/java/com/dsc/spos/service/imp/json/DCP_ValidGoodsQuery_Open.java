package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ValidGoodsQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ValidGoodsQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_ValidGoodsQuery_Open extends SPosBasicService<DCP_ValidGoodsQuery_OpenReq, DCP_ValidGoodsQuery_OpenRes>
{
	
	@Override
	protected boolean isVerifyFail(DCP_ValidGoodsQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		if(req.getRequest()==null)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空！");
		}
		return isFail;
		
	}
	
	@Override
	protected TypeToken<DCP_ValidGoodsQuery_OpenReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ValidGoodsQuery_OpenReq>(){};
	}
	
	@Override
	protected DCP_ValidGoodsQuery_OpenRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_ValidGoodsQuery_OpenRes();
	}
	
	@Override
	protected DCP_ValidGoodsQuery_OpenRes processJson(DCP_ValidGoodsQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		if(req.getApiUser()==null)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "接口账号查询的渠道信息为空！");
		}
		
		
		StringBuffer errMsg = new StringBuffer("");
		String appType = req.getApiUser().getAppType();
		String channelId = req.getApiUser().getChannelId();
		if(Check.Null(appType)){
			errMsg.append("接口账号对应的渠道类型appType不可为空值, ");
			isFail = true;
		}
		if(Check.Null(channelId)){
			errMsg.append("接口账号对应的渠道编码channelId不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		
		
		
		DCP_ValidGoodsQuery_OpenRes res = this.getResponse();
		
		DCP_ValidGoodsQuery_OpenRes.levelDatas datas = res.new levelDatas();
		
		datas.setPluList(new ArrayList<DCP_ValidGoodsQuery_OpenRes.level1Elm>());
		
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		
		if (req.getPageNumber()==0)
		{
			req.setPageNumber(1);
		}
		
		if (req.getPageSize()==0)
		{
			req.setPageSize(10);
		}
		
		String sql = getQuerySql(req);
		HelpTools.writelog_fileName("有赞商品查询sql:"+sql+"\r\n", "DCP_ValidGoodsQuery_Open");
		
		// 拼接返回图片路径
		String ISHTTPS=PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
		String httpStr=ISHTTPS.equals("1")?"https://":"http://";
		String DomainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
		
		int totalRecords;											//总笔数
		int totalPages;
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false)
		{
			//算總頁數
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			//pluNo
			Map<String, Boolean> getCondition = new HashMap<String, Boolean>(); //查詢條件
			getCondition.put("PLUNO", true);
			//调用过滤函数
			List<Map<String, Object>> getPluNoQHeader=MapDistinct.getMap(getQData,getCondition);
			
			//Feature
			getCondition.clear(); //查詢條件
			getCondition.put("PLUNO", true);
			getCondition.put("FEATURENO", true);
			//调用过滤函数
			List<Map<String, Object>> getFeatureQHeader=MapDistinct.getMap(getQData,getCondition);
			
			//Barcode
			getCondition.clear(); //查詢條件
			getCondition.put("PLUNO", true);
			getCondition.put("PLUBARCODE", true);
			//调用过滤函数
			List<Map<String, Object>> getBarcodeQHeader=MapDistinct.getMap(getQData,getCondition);
			
			//goodsUnit
			getCondition.clear(); //查詢條件
			getCondition.put("PLUNO", true);
			getCondition.put("OUNIT", true);
			//调用过滤函数
			List<Map<String, Object>> getGoodsUnitQHeader=MapDistinct.getMap(getQData,getCondition);
			
			//OrgNo
			getCondition.clear(); //查詢條件
			getCondition.put("PLUNO", true);
			getCondition.put("UNIT_SALE", true);
			//getCondition.put("TEMPLATEID", true);
			getCondition.put("RANGETYPE", true);
			getCondition.put("ID", true);
			//调用过滤函数
			List<Map<String, Object>> getOrgNoQHeader=MapDistinct.getMap(getQData,getCondition);
			
			for (Map<String, Object> map : getPluNoQHeader)
			{
				DCP_ValidGoodsQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setFeatureList(new ArrayList<DCP_ValidGoodsQuery_OpenRes.level2Feature>());
				oneLv1.setPluBarcodeList(new ArrayList<DCP_ValidGoodsQuery_OpenRes.level2Barcode>());
				oneLv1.setUnitList(new ArrayList<DCP_ValidGoodsQuery_OpenRes.level2Unit>());
				
				String pluNo = map.getOrDefault("PLUNO", "").toString();
				oneLv1.setPluNo(pluNo);
				oneLv1.setPluName(map.getOrDefault("PLU_NAME", "").toString());
				oneLv1.setPluType(map.getOrDefault("PLUTYPE", "").toString());
				String listImage1 = map.getOrDefault("LISTIMAGE1", "").toString();//ALL
				String listImage2 = map.getOrDefault("LISTIMAGE2", "").toString();//指定渠道
				String listImage = listImage1;
				if(!listImage2.isEmpty())
				{
					listImage = listImage2;
				}
				
				if(!Check.Null(listImage))
				{
					if (DomainName.endsWith("/"))
					{
						oneLv1.setListImage(httpStr+DomainName+"resource/image/" +listImage);
					}
					else
					{
						oneLv1.setListImage(httpStr+DomainName+"/resource/image/" +listImage);
					}
				}
				else
				{
					oneLv1.setListImage("");
				}
				
				for (Map<String, Object> mapFeature : getFeatureQHeader)
				{
					String pluNo_Feature = mapFeature.getOrDefault("PLUNO", "").toString();
					String featureNo = mapFeature.getOrDefault("FEATURENO", "").toString();
					if(pluNo_Feature.isEmpty()||featureNo.isEmpty())
					{
						continue;
					}
					if(!pluNo_Feature.equals(pluNo))
					{
						continue;
					}
					
					String featureName= mapFeature.get("FEATURENAME").toString();
					String attrId1= mapFeature.get("ATTRID1").toString();
					String attrId1Name= mapFeature.get("ATTRVALUEID1").toString();
					String attrValueId1= mapFeature.get("ATTR1NAME").toString();
					String attrValueId1Name= mapFeature.get("AVALUE1NAME").toString();
					String attrId2= mapFeature.get("ATTRID2").toString();
					String attrId2Name= mapFeature.get("ATTRVALUEID2").toString();
					String attrValueId2= mapFeature.get("ATTR2NAME").toString();
					String attrValueId2Name= mapFeature.get("AVALUE2NAME").toString();
					String attrId3= mapFeature.get("ATTRID3").toString();
					String attrId3Name= mapFeature.get("ATTRVALUEID3").toString();
					String attrValueId3= mapFeature.get("ATTR3NAME").toString();
					String attrValueId3Name= mapFeature.get("AVALUE3NAME").toString();
					
					DCP_ValidGoodsQuery_OpenRes.level2Feature oneLv2 = res.new level2Feature();
					
					oneLv2.setFeatureNo(featureNo);
					oneLv2.setFeatureName(featureName);
					
					oneLv2.setAttrId1(attrId1);
					oneLv2.setAttrId1Name(attrId1Name);
					oneLv2.setAttrValueId1(attrValueId1);
					oneLv2.setAttrValueId1Name(attrValueId1Name);
					
					
					oneLv2.setAttrId2(attrId2);
					oneLv2.setAttrId2Name(attrId2Name);
					oneLv2.setAttrValueId2(attrValueId2);
					oneLv2.setAttrValueId2Name(attrValueId2Name);
					
					oneLv2.setAttrId3(attrId3);
					oneLv2.setAttrId3Name(attrId3Name);
					oneLv2.setAttrValueId3(attrValueId3);
					oneLv2.setAttrValueId3Name(attrValueId3Name);
					
					oneLv1.getFeatureList().add(oneLv2);
					oneLv2 = null;
					
				}
				
				
				for (Map<String, Object> mapBarcode : getBarcodeQHeader)
				{
					String pluNo_Barcode = mapBarcode.getOrDefault("PLUNO", "").toString();
					String pluBarcode = mapBarcode.getOrDefault("PLUBARCODE", "").toString();
					if(pluNo_Barcode.isEmpty()||pluBarcode.isEmpty())
					{
						continue;
					}
					if(!pluNo_Barcode.equals(pluNo))
					{
						continue;
					}
					
					DCP_ValidGoodsQuery_OpenRes.level2Barcode oneLv2 = res.new level2Barcode();
					
					oneLv2.setPluBarcode(pluBarcode);
					oneLv2.setUnit(mapBarcode.getOrDefault("UNIT_BAR", "").toString());
					oneLv2.setUnitName(mapBarcode.getOrDefault("UNAME_BAR", "").toString());
					oneLv2.setFeatureNo(mapBarcode.getOrDefault("FEATURENO_BAR", "").toString());
					oneLv2.setFeatureName(mapBarcode.getOrDefault("FEATURENAME_BAR", "").toString());
					
					oneLv1.getPluBarcodeList().add(oneLv2);
					oneLv2 = null;
					
				}
				
				for (Map<String, Object> mapUnit : getGoodsUnitQHeader)
				{
					String pluNo_Unit = mapUnit.getOrDefault("PLUNO", "").toString();
					String oUnit = mapUnit.getOrDefault("OUNIT", "").toString();//dcp_goods_unit 对应的ounitt
					if(pluNo_Unit.isEmpty()||oUnit.isEmpty())
					{
						continue;
					}
					if(!pluNo_Unit.equals(pluNo))
					{
						continue;
					}
					String sUnit = mapUnit.getOrDefault("SUNIT", "").toString();//dcp_goods对应的sunit
					String baseUnit = mapUnit.getOrDefault("BASEUNIT", "0").toString();
					String oUnit_UnitRatio = mapUnit.getOrDefault("UNITRATIO", "1").toString();//dcp_goods_unit 对应的基准单位换算
					String price_sunit = mapUnit.getOrDefault("PRICE_SUNIT", "0").toString();
					String baseUnitRatio_sunit = mapUnit.getOrDefault("BASEUNITRATIO_SUNIT", "1").toString();//dcp_goods里面price对应的基准单位换算
					String udLength = mapUnit.getOrDefault("UDLENGTH", "4").toString();
					String spec =  mapUnit.getOrDefault("SPEC", "").toString();
					double price = 0;
					if(oUnit.equals(sUnit))
					{
						price = Double.parseDouble(price_sunit);
					}
					else
					{
						
						try
						{
							BigDecimal price_sunit_b = new BigDecimal(price_sunit);
							BigDecimal  baseUnitRatio_sunit_b = new BigDecimal(baseUnitRatio_sunit);
							BigDecimal  oUnit_UnitRatio_b = new BigDecimal(oUnit_UnitRatio);
							
							BigDecimal price_b = price_sunit_b.multiply(oUnit_UnitRatio_b).divide(baseUnitRatio_sunit_b,6,RoundingMode.HALF_UP);
							price = price_b.setScale(Integer.parseInt(udLength), RoundingMode.HALF_UP).doubleValue();
							
						}
						catch (Exception e)
						{
							// TODO: handle exception
						}
						
					}
					
					
					DCP_ValidGoodsQuery_OpenRes.level2Unit oneLv2 = res.new level2Unit();
					oneLv2.setOrganizationList(new ArrayList<DCP_ValidGoodsQuery_OpenRes.level3Organization>());
					oneLv2.setBaseUnit(baseUnit);
					oneLv2.setBaseUnitName(mapUnit.getOrDefault("BASEUNITNAME", "").toString());
					oneLv2.setPrice(price+"");
					oneLv2.setSpec(mapUnit.getOrDefault("SPEC", "").toString());
					oneLv2.setUdLength(udLength);
					oneLv2.setUnit(oUnit);
					oneLv2.setUnitName(mapUnit.getOrDefault("OUNITNAME", "").toString());
					oneLv2.setUnitRatio(oUnit_UnitRatio);
					oneLv2.setBaseUnitUdLength(mapUnit.getOrDefault("BASEUNITUDLENGTH", "").toString());
					
					
					for (Map<String, Object> mapOrgNo : getOrgNoQHeader)
					{
						String pluNo_OrgNo = mapOrgNo.getOrDefault("PLUNO", "").toString();
						String unit_Sale = mapOrgNo.getOrDefault("UNIT_SALE", "").toString();//Dcp_Salepricetemplate_Price 对应的unitt
						String id =  mapOrgNo.getOrDefault("ID", "").toString();
						
						if(pluNo_OrgNo.isEmpty()||unit_Sale.isEmpty()||id.isEmpty())
						{
							continue;
						}
						if(!pluNo_OrgNo.equals(pluNo))
						{
							continue;
						}
						if(!unit_Sale.equals(oUnit))
						{
							continue;
						}
						
						String name =  mapOrgNo.getOrDefault("NAME", "").toString();
						String rangeType = mapOrgNo.getOrDefault("RANGETYPE", "").toString();
						DCP_ValidGoodsQuery_OpenRes.level3Organization oneLv3 = res.new level3Organization();
						
						oneLv3.setOrganizationNo(id);
						oneLv3.setOrganizationName(name);
						oneLv3.setRangeType(rangeType);
						oneLv3.setPrice(mapOrgNo.getOrDefault("PRICE_SALE", "0").toString());
						oneLv3.setMinPrice(mapOrgNo.getOrDefault("MINPRICE", "0").toString());
						
						oneLv2.getOrganizationList().add(oneLv3);
						oneLv3 = null;
						
					}
					
					oneLv1.getUnitList().add(oneLv2);
					oneLv2 = null;
					
				}
				
				datas.getPluList().add(oneLv1);
				oneLv1 = null;
			}
			
			
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;
		}
		getQData = null;
		res.setDatas(datas);
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected String getQuerySql(DCP_ValidGoodsQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String appType = req.getApiUser().getAppType();
		String channelId = req.getApiUser().getChannelId();
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		
		String[] pluNo  = req.getRequest().getPluNo();
		String[] pluBarcode  = req.getRequest().getPluBarcode();
		String keyTxt = req.getRequest().getKeyTxt();
		String pluNos = getString(pluNo);
		String pluBarcodes =getString(pluBarcode);
		
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow;
		
		int endRow = startRow + pageSize;
		
		StringBuffer sqlBuffer = new StringBuffer("");
		sqlBuffer.append("");
		sqlBuffer.append("");
		
		
		sqlBuffer.append(" with p as ( ");
		sqlBuffer.append(" select goods.*, feature.featureno,feature.featurename,"
				+ "feature.attrid1,feature.attrvalueid1,feature.attr1name,feature.avalue1name,"
				+ "feature.attrid2,feature.attrvalueid2,feature.attr2name,feature.avalue2name,"
				+ "feature.attrid3,feature.attrvalueid3,feature.attr3name,feature.avalue3name,"
				+ "image1.listimage listimage1,image2.listimage listimage2 from (");
		
		sqlBuffer.append(" select * from (");
		sqlBuffer.append(" select count(distinct A.PLUNO  ) OVER() AS NUM,dense_rank() over (order BY  a.PLUNO ) rn, "
				+ " A.EID,A.PLUNO,A.PLUTYPE,A.SUNIT,A.PRICE PRICE_SUNIT,AL.PLU_NAME,"
				+ " BAR.PLUBARCODE,BAR.UNIT UNIT_BAR,BAR.FEATURENO FEATURENO_BAR,BAR_UL.UNAME UNAME_BAR,BAR_FL.FEATURENAME FEATURENAME_BAR,"
				+ " SUNIT.UNITRATIO as BASEUNITRATIO_SUNIT,buludlength.udlength as baseunitudlength,"
				+ " GOODSUNIT.UNIT BASEUNIT, GOODSUNIT.OUNIT,GOODSUNIT.UNITRATIO,U.UDLENGTH,UL.UNAME OUNITNAME,BUL.UNAME BASEUNITNAME,GUL.SPEC ");
		sqlBuffer.append(" from dcp_goods a ");
		sqlBuffer.append(" left join dcp_goods_barcode  bar on bar.PLUNO=a.PLUNO AND bar.eId=a.eId  and bar.status=a.status ");
		sqlBuffer.append(" left join dcp_unit_lang BAR_UL on BAR_UL.eid=bar.eid and BAR_UL.unit=bar.unit  and BAR_UL.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_goods_feature_lang BAR_FL on BAR_FL.eid=bar.eid and BAR_FL.pluno=bar.pluno  and BAR_FL.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_goods_lang al ON al.eid=a.eid and al.pluno=a.pluno and al.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_goods_unit  SUNIT on SUNIT.eid=a.eid and SUNIT.pluno=a.pluno and SUNIT.OUNIT=a.sunit");
		sqlBuffer.append(" left join dcp_goods_unit goodsunit on  goodsunit.eid=a.eid and goodsunit.pluno=a.pluno and goodsunit.sunit_use='Y'");
		sqlBuffer.append(" left join dcp_unit u on u.eid=goodsunit.eid and u.unit=goodsunit.ounit and goodsunit.sunit_use='Y'");
		//【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
		sqlBuffer.append(" left join dcp_unit buludlength on buludlength.eid=goodsunit.eid and buludlength.unit=goodsunit.UNIT ");
		
		sqlBuffer.append(" left join dcp_goods_unit_lang gul on gul.eid=goodsunit.eid and gul.pluno=goodsunit.pluno and gul.ounit=goodsunit.ounit and gul.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_unit_lang UL on UL.eid=goodsunit.eid and UL.unit=goodsunit.ounit and   goodsunit.sunit_use='Y' and UL.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_unit_lang BUL on BUL.eid=goodsunit.eid and BUL.unit=goodsunit.unit and   goodsunit.sunit_use='Y' and BUL.lang_type='"+langType+"'");
		
		if("YOUZAN".equals(appType)||"QIMAI".equals(appType)){
			sqlBuffer.append(" inner join DCP_GOODSTEMPLATE_GOODS DGG ON a.EID=DGG.EID AND a.PLUNO=DGG.PLUNO AND DGG.CANSALE='Y' ");
			sqlBuffer.append(" inner join DCP_GOODSTEMPLATE DG ON DGG.EID=DG.EID AND  DGG.TEMPLATEID=DG.TEMPLATEID AND DG.STATUS='100' ");
			sqlBuffer.append(" inner join DCP_GOODSTEMPLATE_RANGE DGR ON DG.EID=DGR.EID AND DG.TEMPLATEID=DGR.TEMPLATEID AND DGR.RANGETYPE='3' ");
			sqlBuffer.append(" inner join CRM_CHANNEL DGC ON DGR.EID=DGC.EID AND DGR.ID=DGC.CHANNELID AND DGC.CHANNELID='"+channelId+"'");
		}
		
		sqlBuffer.append(" where a.eid='"+eId+"' and a.status='100' ");
		
		if(!Check.Null(pluNos))
		{
			sqlBuffer.append(" and a.pluno in ("+pluNos+")");
		}
		if(!Check.Null(pluBarcodes))
		{
			sqlBuffer.append(" and bar.PLUBARCODE in ("+pluBarcodes+")");
		}
		if (!Check.Null(keyTxt))
		{
			sqlBuffer.append(" and ("
					+ " (lower(bar.plubarcode)=lower('%%"+keyTxt+"%%')) or"
					+ " (lower(a.pluno) like lower('%%"+keyTxt+"%%')) or"
					+ " (lower(al.plu_name) like lower('%%"+keyTxt+"%%'))"
					+ " )");
		}
		sqlBuffer.append(" )   where rn>"+startRow+" and rn<="+endRow);
		
		sqlBuffer.append(" ) goods ");
		
		sqlBuffer.append(" left join (");
		sqlBuffer.append(" select a.eid,a.pluno,a.featureno,flang.featurename,"
				+ "a.attrid1,a.attrvalueid1,alang1.attrname as attr1name,avlang1.attrvaluename as avalue1name,"
				+ "a.attrid2,a.attrvalueid2,alang2.attrname as attr2name,avlang2.attrvaluename as avalue2name,"
				+ "a.attrid3,a.attrvalueid3,alang3.attrname as attr3name,avlang3.attrvaluename as avalue3name ");
		sqlBuffer.append(" from dcp_goods_feature a");
		sqlBuffer.append(" left join dcp_goods_feature_lang flang on flang.eid=a.eid and flang.pluno=a.pluno and flang.featureno=a.featureno and flang.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_attribution_lang alang1 on alang1.eid=a.eid and alang1.attrid=a.attrid1 and alang1.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_attribution_lang alang2 on alang2.eid=a.eid and alang2.attrid=a.attrid2 and alang2.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_attribution_lang alang3 on alang3.eid=a.eid and alang3.attrid=a.attrid3 and alang3.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_attribution_value_lang avlang1 on avlang1.eid=a.eid and avlang1.attrvalueid=a.attrvalueid1 and avlang1.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_attribution_value_lang avlang2 on avlang2.eid=a.eid and avlang2.attrvalueid=a.attrvalueid2 and avlang2.lang_type='"+langType+"'");
		sqlBuffer.append(" left join dcp_attribution_value_lang avlang3 on avlang3.eid=a.eid and avlang3.attrvalueid=a.attrvalueid3 and avlang3.lang_type='"+langType+"'");
		sqlBuffer.append(" where a.eid='"+eId+"' and a.status='100'");
		sqlBuffer.append(" ) feature on feature.eid=goods.eid and feature.pluno=goods.pluno ");
		
		sqlBuffer.append(" left join dcp_goodsimage image1 on image1.eid=goods.eid and image1.pluno=goods.pluno and image1.apptype='ALL'");
		sqlBuffer.append(" left join dcp_goodsimage image2 on image2.eid=goods.eid and image2.pluno=goods.pluno and image2.apptype='"+appType+"'");
		
		sqlBuffer.append(" )");
		
		//with结束
		sqlBuffer.append(" select p.*,SPP.UNIT UNIT_SALE,SPP.PRICE PRICE_SALE,SPP.MINPRICE,P.UNITRATIO UNITRATIO_SALE,SPR.RANGETYPE,SPR.ID,SPR.NAME ");
		sqlBuffer.append(" from p ");
		sqlBuffer.append(" LEFT join Dcp_Salepricetemplate_Price SPP on SPP.EID=P.EID AND SPP.PLUNO=P.PLUNO and SPP.UNIT=P.OUNIT "
				+ " and trunc(SPP.begindate)<=trunc(sysdate) and trunc(SPP.enddate)>=trunc(sysdate)");
		sqlBuffer.append(" LEFT JOIN Dcp_Salepricetemplate SP on SP.EID=SPP.EID AND SP.TEMPLATEID=SPP.TEMPLATEID AND (SP.RESTRICTCHANNEL=0 OR (SP.RESTRICTCHANNEL=1 AND SP.CHANNELID='"+channelId+"'))");
		sqlBuffer.append(" LEFT JOIN Dcp_Salepricetemplate_Range SPR ON SPR.EID=SP.EID AND SPR.TEMPLATEID=SP.TEMPLATEID ");
		
		String sql = sqlBuffer.toString();
		return sql;
	}
	
	private String getString(String[] str) {
		StringBuffer str2 = new StringBuffer();
		if (str!=null && str.length>0)
		{
			for (String s:str) {
				str2.append("'").append(s).append("'").append(",");
			}
			if (str2.length()>0) {
				str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
			}
		}
		return str2.toString();
	}
	
}
