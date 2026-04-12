package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsRecommend_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsRecommend_OpenReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_GetMallGoodsList_OpenRes;
import com.dsc.spos.json.cust.res.DCP_GetMallGoodsRecommend_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 服务函数：DCP_GetMallGoodsRecommend_Open
 * 服务说明：获取线上商品推荐
 * @author jinzma
 * @since  2020-09-27
 */
public class DCP_GetMallGoodsRecommend_Open extends SPosBasicService<DCP_GetMallGoodsRecommend_OpenReq,DCP_GetMallGoodsRecommend_OpenRes>{
	Logger logger = LogManager.getLogger(DCP_GetMallGoodsRecommend_Open.class);
	@Override
	protected boolean isVerifyFail(DCP_GetMallGoodsRecommend_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		levelElm request = req.getRequest();
		if (request == null){
			errMsg.append("request不能为空, ");
			isFail = true;
		}
		else{
			if (request.getQueryCondition() == null){
				errMsg.append("queryCondition不能为空, ");
				isFail = true;
			}
			else{
				if (request.getQueryCondition().getMallGoodsId()==null){
					errMsg.append("mallGoodsId不能为空值, ");
					isFail = true;
				}
			}
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_GetMallGoodsRecommend_OpenReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_GetMallGoodsRecommend_OpenReq>(){};
	}
	
	@Override
	protected DCP_GetMallGoodsRecommend_OpenRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_GetMallGoodsRecommend_OpenRes();
	}
	
	@Override
	protected DCP_GetMallGoodsRecommend_OpenRes processJson(DCP_GetMallGoodsRecommend_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_GetMallGoodsRecommend_OpenRes res = this.getResponse();
		try
		{
			String apiUserCode = req.getApiUserCode();
			String eId = "";              //从apiUserCode 查询得到企业编号
			String appType = "";          //从apiUserCode 查询得到应用类型
			String channelId = "";        //从apiUserCode 查询得到渠道编码
			levelElm request = req.getRequest();
			String shopId = request.getQueryCondition().getShopId();              //门店编号	
			String memberId = request.getQueryCondition().getMemberId();          //会员号
			String cardNo = request.getQueryCondition().getCardNo();              //会员卡号
			String promUrl = "";           //参数PromUrl
			String requestId = req.getRequestId();
			if (Check.Null(requestId))
				requestId = UUID.randomUUID().toString();
			
			//以下是云洋在基类里面进行赋值  20200915
			eId=req.geteId();
			appType = req.getApiUser().getAppType();
			channelId = req.getApiUser().getChannelId();
			
			if (Check.Null(eId))
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的eId");
			if (Check.Null(appType))
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的appType");
			if (Check.Null(channelId))
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的channelId");
			
			///默认仓库获取
			String out_cost_warehouse="";
			//			if (!Check.Null(shopId))
			//			{
			//				String sql=" select out_cost_warehouse from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
			//				List<Map<String, Object>> getWarehouse = this.doQueryData(sql, null);
			//				if (getWarehouse!=null && getWarehouse.isEmpty()==false)
			//				{
			//					out_cost_warehouse = getWarehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
			//				}
			//				else
			//				{
			//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店:"+shopId+ " 默认出货成本仓未设置");
			//				}
			//			}
			
			promUrl = PosPub.getPROM_INNER_URL(eId);  ///会员服务地址
			if (Check.Null(promUrl))
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数PromUrl未设置 ");
			
			////图片地址参数获取
			String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
			String httpStr = isHttps.equals("1") ? "https://" : "http://";
			String domainName = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
			String imagePath = "";
			if (domainName.endsWith("/")) {
				imagePath = httpStr + domainName + "resource/image/";
			} else {
				imagePath = httpStr + domainName + "/resource/image/";
			}
			
			
			
			//取当前商品相同销售分组的商品，需排除当前商品本身
			String referSameSql =getReferSameQuerySql(req,eId,appType,channelId,shopId);
			List<Map<String, Object>> getQReferSame = this.doQueryData(referSameSql, null);
			
			//取当前商品关联推荐分组商品，需排除当前商品本身
			String referAppendSql =getReferAppendQuerySql(req,eId,appType,channelId,shopId);
			List<Map<String, Object>> getQReferAppend = this.doQueryData(referAppendSql, null);
			
			DCP_GetMallGoodsRecommend_OpenRes.levelElm lv = res.new levelElm();
			lv.setReferAppend(new ArrayList<DCP_GetMallGoodsList_OpenRes.level1Elm>());
			lv.setReferSame(new ArrayList<DCP_GetMallGoodsList_OpenRes.level1Elm>());
			
			//两个分组商品资料处理
			if ((getQReferSame!=null && getQReferSame.isEmpty()==false) || (getQReferAppend!=null && getQReferAppend.isEmpty()==false))
			{
				List<Map<String, Object>> getQPlu = new ArrayList<Map<String,Object>>();
				
				if (getQReferSame!=null && getQReferSame.isEmpty()==false)
				{
					getQPlu.addAll(getQReferSame);
					if (getQReferAppend!=null && getQReferAppend.isEmpty()==false)
					{
						getQPlu.addAll(getQReferAppend);
					}
				}
				else
				{
					getQPlu.addAll(getQReferAppend);
				}
				
				//基础促销价计算  
				MyCommon comm = new MyCommon();
				Map<String, Boolean> condition = new HashMap<>(); //查詢條件
				condition.put("PLUNO", true);
				condition.put("PLUTYPE", true);
				List<Map<String, Object>> getQPlus=MapDistinct.getMap(getQPlu, condition);
				//调用公用促销价计算方法
				List<Map<String, Object>> getBasicProm = comm.getBasicProm(apiUserCode, req.getApiUser().getUserKey(), req.getLangType(), requestId, req.getApiUser().getCompanyId(), shopId, memberId, cardNo, promUrl, getQPlus);
				if (getBasicProm==null || getBasicProm.isEmpty()){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员基础促销价(PROM_BasicPromotionCalc_Open)调用失败 ");
				}
				
				////处理相同销售分组
				if (getQReferSame!=null && getQReferSame.isEmpty()==false){
					int i =1;  // 取当前商品相同销售分组的商品，只返回8个，需排除当前商品本身
					DCP_GetMallGoodsList_OpenRes getQReferSameRes = new DCP_GetMallGoodsList_OpenRes();
					for (Map<String, Object> oneData:getQReferSame)
					{
						if (i > 8)
							break;
						DCP_GetMallGoodsList_OpenRes.level1Elm lv1 = getQReferSameRes.new level1Elm();
						String pluNo = oneData.get("PLUNO").toString();
						String pluType = oneData.get("PLUTYPE").toString();
						String sUnit = "";
						String originalPrice = "";
						String price = "";
						String promLable = "";
						//String basicPrice = "";
						// 20200929和玲霞沟通确认   促销接口未返回价格时，后端返回空给前端，库存数为空时返回0给前端
						for (Map<String, Object>oneBasicProm : getBasicProm)
						{
							if (pluNo.equals(oneBasicProm.get("PLUNO").toString())&& pluType.equals(oneBasicProm.get("PLUTYPE").toString()))
							{
								sUnit = oneBasicProm.get("UNITID").toString();
								originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
								price = oneBasicProm.get("PRICE").toString();
								//basicPrice = oneBasicProm.get("BASICPRICE").toString();
								promLable = oneBasicProm.get("PROMLABLE").toString();
								break;
							}
						}
						if (!PosPub.isNumericType(originalPrice))
							originalPrice="-1"; ///促销只要取不到的全部维护成-1  BY 玲霞 20201026
						if (!PosPub.isNumericType(price))
							price="-1";
						//						if (!PosPub.isNumericType(basicPrice))
						//							basicPrice="";
						
						//可售量查询	
						//门店前端未给值，造成库存查询不准
						if (Check.Null(shopId))
							shopId="";
						
						//【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
						String totalStock="";
						try{
							totalStock = PosPub.queryStockQty(dao, eId, pluNo, " ", shopId, channelId, out_cost_warehouse, sUnit);
						}catch (SPosCodeException e){
							String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+pluNo+"',' ','"+shopId+"','"+channelId+"','"+out_cost_warehouse+"','"+sUnit+"') as qty FROM dual";
							logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsRecommend_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
							totalStock="";
						}
						
						if (Check.Null(totalStock) || !PosPub.isNumericType(totalStock)) {
							totalStock="0";
						} else {
							////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
							BigDecimal totalStock_b = new BigDecimal(totalStock);
							totalStock_b = totalStock_b.setScale(0, RoundingMode.DOWN);
							totalStock = totalStock_b.toPlainString();
						}
						
						String description = oneData.get("DESCRIPTION").toString();
						String groupId = oneData.get("CLASSNO").toString();
						String groupName = oneData.get("CLASSNAME").toString();
						String mallGoodsName = oneData.get("DISPLAYNAME").toString();
						String picUrl = oneData.get("PICURL").toString();
						if (!Check.Null(picUrl))
							picUrl = imagePath+picUrl;
						String symbolDisplay = oneData.get("SYMBOLDISPLAY").toString();
						String symbolIcon = oneData.get("SYMBOLICON").toString();
						String symbolText = oneData.get("SYMBOLTEXT").toString();
						String symbolType = oneData.get("SYMBOLTYPE").toString();
						
						//GetMallGoodsRecommend中symbolDisplay 取不到设定,给0  BY 玲霞 20201026
						if (Check.Null(symbolDisplay))
							symbolDisplay="0";
						
						lv1.setPromLable(promLable);
						lv1.setDescription(description);
						lv1.setGroupId(groupId);
						lv1.setGroupName(groupName);
						lv1.setMallGoodsId(pluNo);
						lv1.setMallGoodsName(mallGoodsName);
						lv1.setOriPrice(originalPrice);
						lv1.setPicUrl(picUrl);
						lv1.setPluType(pluType);
						lv1.setPrice(price);
						lv1.setSymbolDisplay(symbolDisplay);
						lv1.setSymbolIcon(symbolIcon);
						lv1.setSymbolText(symbolText);
						lv1.setSymbolType(symbolType);
						lv1.setTotalStock(totalStock);
						
						lv.getReferSame().add(lv1);
						i++;
					}
				}
				
				////处理关联推荐分组
				if (getQReferAppend!=null && getQReferAppend.isEmpty()==false){
					int i =1;  // 取当前商品相同销售分组的商品，只返回8个，需排除当前商品本身
					DCP_GetMallGoodsList_OpenRes getQReferAppendRes = new DCP_GetMallGoodsList_OpenRes();
					for (Map<String, Object> oneData:getQReferAppend)
					{
						if (i > 8)
							break;
						DCP_GetMallGoodsList_OpenRes.level1Elm lv1 = getQReferAppendRes.new level1Elm();
						String pluNo = oneData.get("PLUNO").toString();
						String pluType = oneData.get("PLUTYPE").toString();
						String sUnit = "";
						String originalPrice = "";
						String price = "";
						//String basicPrice = "";
						String promLable = "";
						
						// 20200929和玲霞沟通确认   促销接口未返回价格时，后端返回空给前端，库存数为空时返回0给前端
						for (Map<String, Object>oneBasicProm : getBasicProm)
						{
							if (pluNo.equals(oneBasicProm.get("PLUNO").toString())&& pluType.equals(oneBasicProm.get("PLUTYPE").toString()))
							{
								sUnit = oneBasicProm.get("UNITID").toString();
								originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
								price = oneBasicProm.get("PRICE").toString();
								//basicPrice = oneBasicProm.get("BASICPRICE").toString();
								promLable = oneBasicProm.get("PROMLABLE").toString();
								break;
							}
						}
						
						if (!PosPub.isNumericType(originalPrice))
							originalPrice="-1"; ///促销只要取不到的全部维护成-1  BY 玲霞 20201026
						if (!PosPub.isNumericType(price))
							price="-1";
						//						if (!PosPub.isNumericType(basicPrice))
						//							basicPrice="";
						
						//可售量查询	
						//门店前端未给值，造成库存查询不准
						if (Check.Null(shopId))
							shopId="";
						
						//【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
						String totalStock="";
						try{
							totalStock = PosPub.queryStockQty(dao, eId, pluNo, " ", shopId, channelId, out_cost_warehouse, sUnit);
						}catch (SPosCodeException e){
							String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+pluNo+"',' ','"+shopId+"','"+channelId+"','"+out_cost_warehouse+"','"+sUnit+"') as qty FROM dual";
							logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsRecommend_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
							totalStock="";
						}
						
						if (Check.Null(totalStock) || !PosPub.isNumericType(totalStock)) {
							totalStock="0";
						} else {
							////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
							BigDecimal totalStock_b = new BigDecimal(totalStock);
							totalStock_b = totalStock_b.setScale(0, RoundingMode.DOWN);
							totalStock = totalStock_b.toPlainString();
						}
						
						String description = oneData.get("DESCRIPTION").toString();
						String groupId = oneData.get("CLASSNO").toString();
						String groupName = oneData.get("CLASSNAME").toString();
						String mallGoodsName = oneData.get("DISPLAYNAME").toString();
						String picUrl = oneData.get("PICURL").toString();
						if (!Check.Null(picUrl))
							picUrl = imagePath+picUrl;
						String symbolDisplay = oneData.get("SYMBOLDISPLAY").toString();
						String symbolIcon = oneData.get("SYMBOLICON").toString();
						String symbolText = oneData.get("SYMBOLTEXT").toString();
						String symbolType = oneData.get("SYMBOLTYPE").toString();
						
						//GetMallGoodsRecommend中symbolDisplay 取不到设定,给0  BY 玲霞 20201026
						if (Check.Null(symbolDisplay))
							symbolDisplay="0";
						
						lv1.setPromLable(promLable);
						lv1.setDescription(description);
						lv1.setGroupId(groupId);
						lv1.setGroupName(groupName);
						lv1.setMallGoodsId(pluNo);
						lv1.setMallGoodsName(mallGoodsName);
						lv1.setOriPrice(originalPrice);
						lv1.setPicUrl(picUrl);
						lv1.setPluType(pluType);
						lv1.setPrice(price);
						lv1.setSymbolDisplay(symbolDisplay);
						lv1.setSymbolIcon(symbolIcon);
						lv1.setSymbolText(symbolText);
						lv1.setSymbolType(symbolType);
						lv1.setTotalStock(totalStock);
						
						lv.getReferAppend().add(lv1);
						i++;
					}
				}
				
			}
			
			res.setDatas(lv);
			return res;
			
		}
		catch (Exception e)
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根
		
	}
	
	@Override
	protected String getQuerySql(DCP_GetMallGoodsRecommend_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}
	
	private String getReferSameQuerySql(DCP_GetMallGoodsRecommend_OpenReq req,String eId,String appType,String channelId,String shopId)throws Exception {
		
		String sql="";
		String langType=req.getLangType();
		StringBuffer sb = new StringBuffer();
		String mallGoodsId = req.getRequest().getQueryCondition().getMallGoodsId();
		sb.append(" "
				+ " with plu as ("
				+ " select a.pluno,max(a.createtime) as createtime from dcp_goods_shelf_range a"
				+ " left join (select a.pluno from dcp_goods_shelf_range a"
				+ " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and shopid='"+shopId+"' and a.status='0') b on a.pluno = b.pluno"
				+ " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.shopid='ALL' and a.status='100' and b.pluno is null"
				+ " group by a.pluno)");
		
		sb.append(" "
				+ " ,goodsimage as ("
				+ " select pluno,picUrl,symbolDisplay from ("
				+ " select a.*,row_number() over (partition by pluno order by indx) as rn from ("
				+ " select a.pluno,a.listimage as picUrl,a.symbolDisplay,1 as indx from dcp_goodsimage a"
				+ " where a.eid='"+eId+"' and a.apptype='"+appType+"'"
				+ " union all"
				+ " select a.pluno,a.listimage as picUrl,a.symbolDisplay,2 as indx from dcp_goodsimage a"
				+ " where a.eid='"+eId+"' and a.apptype='ALL' )a"
				+ " ) where rn='1')");
		
		sb.append(" "
				+ " ,goodsimage_symbol as ("
				+ " select pluno,symbolType,symbolText,symbolIcon from ("
				+ " select a.*,row_number() over (partition by pluno order by indx) as rn from("
				+ " select pluno,symbolType,symbolText,symbolIcon,1 as indx from ("
				+ " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
				+ " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
				+ " where a.eid ='"+eId+"' and a.apptype='"+appType+"' "
				+ " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate)"
				+ " )a where rn='1'"
				+ " union all"
				+ " select pluno,symbolType,symbolText,symbolIcon,2 as indx from ("
				+ " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
				+ " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
				+ " where a.eid ='"+eId+"' and a.apptype='ALL' "
				+ " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate)"
				+ " )a where rn='1')a"
				+ " ) where rn='1')");
		
		sb.append(""
				+ " ,masterplu as ("
				+ " select a.masterpluno from dcp_mspecgoods_subgoods a"
				+ " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno and b.status='100'"
				+ " where a.eid='"+eId+"'"
				+ " group by a.masterpluno)"
				+ "");

		/*【ID1015340】 V3--DCP_GetGoodsGroup_Open,禁用商品会传空单位给促销接口,导致服务报错 玲霞 BY JZMA 20210207
		单规格,商品不能失效(DCP_GOODS.STATUS=100)
		多规格不能明细都失效,
		商城商品不能失效DCP_GOODS_ONLINE.STATUS<>90
		表说明：
		DCP_MSPECGOODS_SUBGOODS.PLUNO
		DCP_GOOD_ONLINE里都是商城商品
		plutype来区分单规格多规格*/
		
		sb.append(" "
				+ " select a.classno,cl1.classname,"
				+ " a.pluno,gol.displayname,gol.simpledescription as description,"
				+ " goodsonline.plutype,goods.sunit as unitId,"
				+ " goodsimage.picUrl,goodsimage.symbolDisplay,"
				+ " goodsimage_symbol.symbolType,goodsimage_symbol.symbolIcon,goodsimage_symbol.symbolText,"
				+ " to_char(nvl(plu.createtime,sysdate),'yyyymmddhh24miss') as createtime"
				+ " from dcp_class_goods a"
				+ " inner join "
				+ " (select max(classno) as classno from dcp_class_goods where eid='"+eId+"' and classtype='ONLINE' and pluno='"+mallGoodsId+"') "
				+ " class on class.classno=a.classno"
				+ " inner join dcp_goods_online goodsonline on a.eid=goodsonline.eid and a.pluno=goodsonline.pluno and goodsonline.status<>'90'"
				+ " inner join plu on a.pluno=plu.pluno"
				+ " left join dcp_class_lang cl1 on cl1.eid=a.eid and cl1.classtype='ONLINE' and cl1.classno=a.classno and cl1.lang_type='"+langType+"'"
				+ " left join goodsimage on goodsimage.pluno=a.pluno"
				+ " left join goodsimage_symbol on goodsimage_symbol.pluno=a.pluno"
				+ " left join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='"+langType+"'"
				+ " left join dcp_goods goods on goods.eid=a.eid and goods.pluno=a.pluno and goods.status='100'"
				+ " left join masterplu on masterplu.masterpluno = a.pluno"
				+ " where a.eid='"+eId+"' and a.classtype='ONLINE' and a.pluno<>'"+mallGoodsId+"'"
				+ " and ((goodsonline.plutype<>'MULTISPEC' and goods.pluno is not null) or (goodsonline.plutype='MULTISPEC' and masterplu.masterpluno is not null)) "
				+ " order by createtime desc"
				+ " ");
		
		sql=sb.toString();
		return sql;
	}
	
	private String getReferAppendQuerySql(DCP_GetMallGoodsRecommend_OpenReq req,String eId,String appType,String channelId,String shopId)throws Exception {
		
		String sql="";
		String langType=req.getLangType();
		StringBuffer sb = new StringBuffer();
		String mallGoodsId = req.getRequest().getQueryCondition().getMallGoodsId();
		sb.append(" "
				+ " with plu as ("
				+ " select a.pluno,max(a.createtime) as createtime from dcp_goods_shelf_range a"
				+ " left join (select a.pluno from dcp_goods_shelf_range a"
				+ " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and shopid='"+shopId+"' and a.status='0') b on a.pluno = b.pluno"
				+ " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.shopid='ALL' and a.status='100' and b.pluno is null"
				+ " group by a.pluno)");
		
		sb.append(" "
				+ " ,goodsimage as ("
				+ " select pluno,picUrl,symbolDisplay from ("
				+ " select a.*,row_number() over (partition by pluno order by indx) as rn from ("
				+ " select a.pluno,a.listimage as picUrl,a.symbolDisplay,1 as indx from dcp_goodsimage a"
				+ " where a.eid='"+eId+"' and a.apptype='"+appType+"'"
				+ " union all"
				+ " select a.pluno,a.listimage as picUrl,a.symbolDisplay,2 as indx from dcp_goodsimage a"
				+ " where a.eid='"+eId+"' and a.apptype='ALL' )a"
				+ " ) where rn='1')");
		
		sb.append(" "
				+ " ,goodsimage_symbol as ("
				+ " select pluno,symbolType,symbolText,symbolIcon from ("
				+ " select a.*,row_number() over (partition by pluno order by indx) as rn from("
				+ " select pluno,symbolType,symbolText,symbolIcon,1 as indx from ("
				+ " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
				+ " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
				+ " where a.eid ='"+eId+"' and a.apptype='ALL' "
				+ " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate)"
				+ " )a where rn='1'"
				+ " union all"
				+ " select pluno,symbolType,symbolText,symbolIcon,2 as indx from ("
				+ " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
				+ " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
				+ " where a.eid ='"+eId+"' and a.apptype='"+appType+"' "
				+ " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate)"
				+ " )a where rn='1')a"
				+ " ) where rn='1')");
		
		sb.append(""
				+ " ,masterplu as ("
				+ " select a.masterpluno from dcp_mspecgoods_subgoods a"
				+ " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno and b.status='100'"
				+ " where a.eid='"+eId+"'"
				+ " group by a.masterpluno)"
				+ "");

		/*【ID1015340】 V3--DCP_GetGoodsGroup_Open,禁用商品会传空单位给促销接口,导致服务报错 玲霞 BY JZMA 20210207
		单规格,商品不能失效(DCP_GOODS.STATUS=100)
		多规格不能明细都失效,
		商城商品不能失效DCP_GOODS_ONLINE.STATUS<>90
		表说明：
		DCP_MSPECGOODS_SUBGOODS.PLUNO
		DCP_GOOD_ONLINE里都是商城商品
		plutype来区分单规格多规格*/
		
		sb.append(" "
				+ " select a.classno,cl1.classname,"
				+ " a.pluno,gol.displayname,gol.simpledescription as description,"
				+ " goodsonline.plutype,goods.sunit as unitId,"
				+ " goodsimage.picUrl,goodsimage.symbolDisplay,"
				+ " goodsimage_symbol.symbolType,goodsimage_symbol.symbolIcon,goodsimage_symbol.symbolText,"
				+ " to_char(nvl(plu.createtime,sysdate),'yyyymmddhh24miss') as createtime"
				+ " from dcp_class_goods a"
				+ " inner join (select max(classno) as classno from dcp_goods_online_refclass where eid='"+eId+"' and classtype='ONLINE' and pluno='"+mallGoodsId+"') refclass"
				+ " on refclass.classno=a.classno"
				+ " inner join dcp_goods_online goodsonline on a.eid=goodsonline.eid and a.pluno=goodsonline.pluno and goodsonline.status<>'90'"
				+ " inner join plu on a.pluno=plu.pluno"
				+ " left join dcp_class_lang cl1 on cl1.eid=a.eid and cl1.classtype='ONLINE' and cl1.classno=a.classno and cl1.lang_type='"+langType+"'"
				+ " left join goodsimage on goodsimage.pluno=a.pluno"
				+ " left join goodsimage_symbol on goodsimage_symbol.pluno=a.pluno"
				+ " left join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='"+langType+"'"
				+ " left join dcp_goods goods on goods.eid=a.eid and goods.pluno=a.pluno and goods.status='100'"
				+ " left join masterplu on masterplu.masterpluno = a.pluno"
				+ " where a.eid='"+eId+"' and a.classtype='ONLINE' and a.pluno<>'"+mallGoodsId+"'"
				+ " and ((goodsonline.plutype<>'MULTISPEC' and goods.pluno is not null) or (goodsonline.plutype='MULTISPEC' and masterplu.masterpluno is not null)) "
				+ " order by createtime desc"
				+ " ");
		
		sql=sb.toString();
		return sql;
	}
	
}
