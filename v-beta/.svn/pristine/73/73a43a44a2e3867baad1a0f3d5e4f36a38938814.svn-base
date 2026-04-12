package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.json.cust.req.DCP_GoodsImageDetailReq;
import com.dsc.spos.json.cust.res.DCP_GoodsImageDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_GoodsImageDetail extends SPosBasicService<DCP_GoodsImageDetailReq,DCP_GoodsImageDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsImageDetailReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();

		if(req.getRequest()==null) {
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String apptype = req.getRequest().getAppType();
		String pluno = req.getRequest().getPluNo();
		if (Check.Null(apptype) ) {
			errMsg.append("应用类型不可为空值, ");
			isFail = true;
		}
		if (Check.Null(pluno) ) {
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsImageDetailReq> getRequestType() {
		return new TypeToken<DCP_GoodsImageDetailReq>() {};
	}

	@Override
	protected DCP_GoodsImageDetailRes getResponseType() {
		return new DCP_GoodsImageDetailRes();
	}

	@Override
	protected DCP_GoodsImageDetailRes processJson(DCP_GoodsImageDetailReq req) throws Exception {
		String eId=req.geteId();
		String apptype = req.getRequest().getAppType();
		String pluno = req.getRequest().getPluNo();

		DCP_GoodsImageDetailRes res=this.getResponse();

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		if (getData!=null && !getData.isEmpty()) {
			//
			Map<String, Boolean> condV = new HashMap<>(); //查詢條件
			condV.put("PLUNO", true);
			//condV.put("PLUNAME", true);	
			List<Map<String, Object>> Header= MapDistinct.getMap(getData, condV);

			condV.clear();
			condV.put("D_ITEM", true);
			List<Map<String, Object>> detailImageList= MapDistinct.getMap(getData, condV);

			condV.clear();
			condV.put("E_ITEM", true);
			List<Map<String, Object>> prodImageList= MapDistinct.getMap(getData, condV);


			condV.clear();
			condV.put("F_ATTRVALUEID1", true);
			List<Map<String, Object>> specImageList= MapDistinct.getMap(getData, condV);

			condV.clear();
			condV.put("G_ITEM", true);
			List<Map<String, Object>> symbolList= MapDistinct.getMap(getData, condV);

			//
			String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
			String httpStr=ISHTTPS.equals("1")?"https://":"http://";

			String DomainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
			String imagePath;

			if (DomainName.endsWith("/")) {
				imagePath=httpStr+DomainName+"resource/image/";
			} else {
				imagePath=httpStr+DomainName+"/resource/image/";
			}
			for (Map<String, Object> oneData : Header) {
				DCP_GoodsImageDetailRes.level1Elm lv1=res.new level1Elm();
				lv1.setListImage(oneData.get("LISTIMAGE").toString());
				lv1.setListImageUrl(imagePath + oneData.get("LISTIMAGE").toString());
                lv1.setdPic(String.valueOf(oneData.get("DPIC")));
                lv1.setdPicUrl(imagePath + oneData.get("DPIC"));
				lv1.setPluName(oneData.get("PLUNAME").toString());
				lv1.setPluNo(oneData.get("PLUNO").toString());
				lv1.setPluType(oneData.get("PLUTYPE").toString());
				lv1.setAppType(oneData.get("APPTYPE").toString());
				lv1.setSymbolDisplay(oneData.get("SYMBOLDISPLAY").toString());
				lv1.setDetailImageList(new ArrayList<>());

				if(detailImageList != null && !detailImageList.isEmpty()){
					for (Map<String, Object> map : detailImageList) {
						DCP_GoodsImageDetailRes.levelDetail detail=res.new levelDetail();
						if(!(Check.Null(map.get("D_CONTENT").toString())
								&& Check.Null(map.get("D_DETAILIMAGE").toString())
								&& Check.Null(map.get("D_DETAILIMAGE").toString() )
								&& Check.Null(map.get("D_TYPE").toString()))){
							detail.setContent(map.get("D_CONTENT").toString());
							detail.setDetailImage(map.get("D_DETAILIMAGE").toString());
							detail.setDetailImageUrl(imagePath+map.get("D_DETAILIMAGE").toString());
							detail.setType(map.get("D_TYPE").toString());
							lv1.getDetailImageList().add(detail);
						}
					}
				}

				lv1.setProdImageList(new ArrayList<>());
				for (Map<String, Object> map : prodImageList) {
					DCP_GoodsImageDetailRes.levelProduct product=res.new levelProduct();
					product.setProdImage(map.get("E_PRODIMAGE").toString());
					product.setProdImageUrl(imagePath + map.get("E_PRODIMAGE").toString());
					lv1.getProdImageList().add(product);
				}

				lv1.setSpecImageList(new ArrayList<>());
				
				//【ID1035523】 多规格商品图，原有逻辑是给属性1配图，但把属性2也展示出来了。期望：把属性2的属性值去掉不展示。 by jinzma 20230824
				String attrid1="";
				sql = " select distinct attrid1 from dcp_mspecgoods_subgoods where eid='"+eId+"' and masterpluno='"+pluno+"'";
				List<Map<String, Object>> attrid = this.doQueryData(sql, null);
				if (!CollectionUtils.isEmpty(attrid)){
					attrid1 = attrid.get(0).get("ATTRID1").toString();
				}
				
				for (Map<String, Object> map : specImageList) {
					DCP_GoodsImageDetailRes.levelSpec spec=res.new levelSpec();
					
					if (!Check.Null(attrid1)){
						if (!attrid1.equals(map.get("F_ATTRID1").toString())){
							continue;
						}
					}
					
					spec.setAttrValueId1(map.get("F_ATTRVALUEID1").toString());
					spec.setAttrId1(map.get("F_ATTRID1").toString());
					spec.setAttrValueName(map.get("H_ATTRVALUENAME").toString());
					spec.setSpecImage(map.get("F_SPECIMAGE").toString());
					spec.setSpecImageUrl(imagePath +map.get("F_SPECIMAGE").toString());

					// 如果 specImageList 为空时返回空数组 
					if(spec.getAttrId1()!=null&& !spec.getAttrId1().isEmpty() &&
							spec.getAttrValueName()!=null&& !spec.getAttrValueName().isEmpty() &&
							spec.getAttrValueId1()!=null&& !spec.getAttrValueId1().isEmpty() &&
							spec.getSpecImage()!=null&& !spec.getSpecImage().isEmpty() &&
							spec.getSpecImageUrl()!=null&& !spec.getSpecImageUrl().isEmpty()){
						lv1.getSpecImageList().add(spec);
					}
				}

				//【ID1032902】//[货郎]多规格商品，商品图片新增/编辑，期望1.默认展示商品已启用规格2.点添加选规格，仅选已启用规格。 by jinzma 20230510
				StringBuffer sqlBuf = new StringBuffer();
				sqlBuf.append("select a.attrid,b.attrvalueid,d.attrvaluename from dcp_mspecgoods_attr a"
						+ " inner join dcp_mspecgoods_attr_value b on a.eid=b.eid and a.masterpluno=b.masterpluno and a.attrid=b.attrid"
						+ " left  join dcp_goodsimage_specimage c on a.eid=c.eid and a.masterpluno=c.pluno and a.attrid=c.attrid1 and b.attrvalueid=c.attrvalueid1 " );
				if (!Check.Null(apptype)){
					sqlBuf.append(" and c.apptype='"+apptype+"'");
				}
				sqlBuf.append(" left  join dcp_attribution_value_lang d on a.eid=d.eid and a.attrid=d.attrid and b.attrvalueid=d.attrvalueid and d.lang_type='"+req.getLangType()+"'"
						+ " where a.eid='"+eId+"' and a.masterpluno='"+oneData.get("PLUNO").toString()+"' and c.pluno is null ");
				List<Map<String,Object>> getAttrData=this.doQueryData(sqlBuf.toString(), null);
				if (!CollectionUtil.isEmpty(getAttrData)){
					for (Map<String,Object>oneAttrData:getAttrData){
						DCP_GoodsImageDetailRes.levelSpec spec=res.new levelSpec();
						spec.setAttrId1(oneAttrData.get("ATTRID").toString());
						spec.setAttrValueId1(oneAttrData.get("ATTRVALUEID").toString());
						spec.setAttrValueName(oneAttrData.get("ATTRVALUENAME").toString());
						spec.setSpecImage("");
						spec.setSpecImageUrl("");

						lv1.getSpecImageList().add(spec);
					}
				}


				lv1.setSymbolList(new ArrayList<>());
				for (Map<String, Object> map : symbolList) {
					DCP_GoodsImageDetailRes.levelSymbol symbol=res.new levelSymbol();
					symbol.setBeginDate(map.get("G_BEGINDATE").toString());
					symbol.setEndDate(map.get("G_ENDDATE").toString());
					symbol.setSymbolImage(map.get("G_SYMBOLIMAGE").toString());
					symbol.setSymbolImageUrl(imagePath + map.get("G_SYMBOLIMAGE").toString());
					symbol.setSymbolTag(map.get("G_SYMBOLTAG").toString());
					symbol.setSymbolType(map.get("G_SYMBOLTYPE").toString());

					// 如果 symbolList 为空时返回空数组 
					if(symbol.getSymbolType()!=null&& !symbol.getSymbolType().isEmpty()){
						lv1.getSymbolList().add(symbol);
					}
				}

				res.setDatas(lv1);
				break;
			}
		} else {
			DCP_GoodsImageDetailRes.level1Elm lv1=res.new level1Elm();
			res.setDatas(lv1);
		}


		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");


		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {

	}

	@Override
	protected String getQuerySql(DCP_GoodsImageDetailReq req) throws Exception {
		String eId = req.geteId();
		String langtype = req.getLangType();
		String pluno = req.getRequest().getPluNo();
		String appType = req.getRequest().getAppType();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;


		StringBuffer sqlbuf=new StringBuffer("select c.pluno PLUNO,b.plu_name PLUNAME,c.plutype PLUTYPE,c.listimage,c.dpic, c.apptype,c.symboldisplay "
				+ ",d.item D_ITEM,d.type D_TYPE,d.detailimage D_DETAILIMAGE,d.content D_CONTENT "
				+ ",e.item E_ITEM,e.prodimage E_PRODIMAGE "
				+ ",f.attrvalueid1 F_ATTRVALUEID1,f.specimage F_SPECIMAGE,f.attrid1 F_ATTRID1,h.attrvaluename H_ATTRVALUENAME "
				+ ",g.item G_ITEM,g.symboltype G_SYMBOLTYPE,g.symboltag G_SYMBOLTAG,g.symbolimage G_SYMBOLIMAGE,g.begindate G_BEGINDATE,g.enddate G_ENDDATE  "
				+ "from dcp_goodsimage c "
				+ "left join dcp_goods a on a.eid=c.eid and a.pluno=c.pluno "
				+ "left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langtype+"' "
				+ "left join dcp_goodsimage_detailimage d on c.eid=d.eid and c.apptype=d.apptype and c.pluno=d.pluno "
				+ "left join dcp_goodsimage_prodimage e on c.eid=e.eid and c.apptype=e.apptype and c.pluno=e.pluno "
				+ "left join dcp_goodsimage_specimage f on c.eid=f.eid and c.apptype=f.apptype and c.pluno=f.pluno "
				+ "left join DCP_ATTRIBUTION_VALUE_LANG h on f.attrvalueid1=h.attrvalueid and f.attrid1=h.attrid and h.lang_type='"+langtype+"' "
				+ "left join dcp_goodsimage_symbol g on c.eid=g.eid and c.apptype=g.apptype and c.pluno=g.pluno "
				+ "where c.eid='"+eId+"' and c.plutype in ( 'FEATURE','NORMAL','PACKAGE') ");

		if(pluno != null && !pluno.isEmpty()) {
			sqlbuf.append("and c.pluno = '"+pluno+"' ");
		}

		if(appType != null && !appType.isEmpty()) {
			sqlbuf.append("and c.apptype= '"+appType+"' ");
		}

		sqlbuf.append("UNION ALL "
				+ "select c.PLUNO PLUNO,b.masterpluname PLUNAME,c.plutype PLUTYPE,c.listimage,c.dpic, c.apptype,c.symboldisplay "
				+ ",d.item D_ITEM,d.type D_TYPE,d.detailimage D_DETAILIMAGE,d.content D_CONTENT "
				+ ",e.item E_ITEM,e.prodimage E_PRODIMAGE "
				+ ",f.attrvalueid1 F_ATTRVALUEID1,f.specimage F_SPECIMAGE,f.attrid1 F_ATTRID1,h.attrvaluename H_ATTRVALUENAME "
				+ ",g.item G_ITEM,g.symboltype G_SYMBOLTYPE,g.symboltag G_SYMBOLTAG,g.symbolimage G_SYMBOLIMAGE,g.begindate G_BEGINDATE,g.enddate G_ENDDATE "
				+ "from dcp_goodsimage c "
				+ "left join dcp_mspecgoods a on a.eid=c.eid and a.masterpluno=c.pluno "
				+ "left join dcp_mspecgoods_lang b on a.eid=b.eid and a.masterpluno=b.masterpluno and b.lang_type='"+langtype+"' "
				+ "left join dcp_goodsimage_detailimage d on c.eid=d.eid and c.apptype=d.apptype and c.pluno=d.pluno "
				+ "left join dcp_goodsimage_prodimage e on c.eid=e.eid and c.apptype=e.apptype and c.pluno=e.pluno "
				+ "left join dcp_goodsimage_specimage f on c.eid=f.eid and c.apptype=f.apptype and c.pluno=f.pluno "
				+ "left join DCP_ATTRIBUTION_VALUE_LANG h on f.attrvalueid1=h.attrvalueid and f.attrid1=h.attrid and h.lang_type='"+langtype+"' "
				+ "left join dcp_goodsimage_symbol g on c.eid=g.eid and c.apptype=g.apptype and c.pluno=g.pluno "
				//【ID1032300】 [货郎]多规格商品，商品图片编辑，默认没有展示商品规格，点添加又展示了所有该规格的值，而不是已选规格。  by jinzma 20230419
				+ "inner join dcp_mspecgoods_attr_value i on a.eid=i.eid and a.masterpluno=i.masterpluno and f.attrid1=i.attrid and f.attrvalueid1=i.attrvalueid "
				+ "where c.eid='"+eId+"' and c.plutype='MULTISPEC' ");

		if(pluno != null && !pluno.isEmpty()) {
			sqlbuf.append("and c.PLUNO = '"+pluno+"' ");
		}

		if(appType != null && !appType.isEmpty()) {
			sqlbuf.append("and c.apptype= '"+appType+"' ");
		}

		sqlbuf.append("order by  D_ITEM,E_ITEM");

		return sqlbuf.toString();
	}



}
