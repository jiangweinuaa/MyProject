package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineDetailReq;
import com.dsc.spos.json.cust.res.DCP_GoodsOnlineDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

public class DCP_GoodsOnlineDetail extends SPosBasicService<DCP_GoodsOnlineDetailReq, DCP_GoodsOnlineDetailRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_GoodsOnlineDetailReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String pluNo = req.getRequest().getPluNo();
		if(Check.Null(pluNo)){
			errMsg.append("编码不能为空值 ，");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
		
	}
	
	@Override
	protected TypeToken<DCP_GoodsOnlineDetailReq> getRequestType() {
		return new TypeToken<DCP_GoodsOnlineDetailReq>(){};
	}
	
	@Override
	protected DCP_GoodsOnlineDetailRes getResponseType() {
		return new DCP_GoodsOnlineDetailRes();
	}
	
	@Override
	protected DCP_GoodsOnlineDetailRes processJson(DCP_GoodsOnlineDetailReq req) throws Exception {
		
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		DCP_GoodsOnlineDetailRes res = this.getResponse();
		String sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		DCP_GoodsOnlineDetailRes.levelElm data = res.new levelElm();
		if(getQData!=null && !getQData.isEmpty())
		{
			String pluNo = getQData.get(0).get("PLUNO").toString();
			String pluType = getQData.get(0).get("PLUTYPE").toString();//商品类型：GOODS-商品；MSPECGOODS-多规格商品，不可修改
			String stockDisplay = getQData.get(0).get("STOCKDISPLAY").toString();//页面是否显示库存0-否1-是
			String preSale = getQData.get(0).get("PRESALE").toString();//是否预订，需提前预订0-否1-是
			String attrGroupId = getQData.get(0).get("ATTRGROUPID").toString();
			String attrGroupName = getQData.get(0).get("ATTRGROUPNAME").toString();
			String deliveryDateType = getQData.get(0).get("DELIVERYDATETYPE").toString();//发货时机类型1：付款成功后发货2：指定日期发货
			String deliveryDateType2 = getQData.get(0).get("DELIVERYDATETYPE2").toString();//发货时间类型1：小时 2：天
			String deliveryDateValue = getQData.get(0).get("DELIVERYDATEVALUE").toString();//付款后%S天/小时后发货，发货时机类型为1时必须传入
			String deliveryDate = getQData.get(0).get("DELIVERYDATE").toString();//预计发货日期，发货时机类型为2时必须传入，YYYY-MM-DD
			String shopPickUp = getQData.get(0).get("SHOPPICKUP").toString();//是否支持自提0-否1-是
			String cityDeliver = getQData.get(0).get("CITYDELIVER").toString();//是否支持同城配送0-否1-是
			String expressDeliver = getQData.get(0).get("EXPRESSDELIVER").toString();//是否支持全国快递0-否1-是
			String freightFree = getQData.get(0).get("FREIGHTFREE").toString();//是否包邮0-否1-是
//			String onShelfAuto = getQData.get(0).get("ONSHELFAUTO").toString();//是否自动上架0-否1-是
//			String onShelfDate = getQData.get(0).get("ONSHELFDATE").toString();//上架日期，自动时不可空，YYYY-MM-DD
//			String onShelfTime = getQData.get(0).get("ONSHELFTIME").toString();//上架时间，自动时不可空,HH:MI:SS
//			String offShelfAuto = getQData.get(0).get("OFFSHELFAUTO").toString();//是否自动下架0-否1-是	
//			String offShelfDate = getQData.get(0).get("OFFSHELFDATE").toString();//下架日期，自动时不可空，YYYY-MM-DD
//			String offShelfTime = getQData.get(0).get("OFFSHELFTIME").toString();//下架时间，自动时不可空,HH:MI:SS
			
			String freightTemplateId = getQData.get(0).get("FREIGHTTEMPLEID").toString();//运费模板编码
			String memo = getQData.get(0).get("MEMO").toString();//
			String status = getQData.get(0).get("STATUS").toString();
			String sortId = getQData.get(0).get("SORTID").toString();
			
			data.setPluNo(pluNo);
			data.setPluName(getQData.get(0).get("DISPLAYNAME").toString());
			data.setPluType(pluType);
			data.setStockDisplay(stockDisplay);
			data.setAttrGroupId(attrGroupId);
			data.setAttrGroupName(attrGroupName);
			data.setPreSale(preSale);
			data.setDeliveryDateType(deliveryDateType);
			data.setDeliveryDateType2(deliveryDateType2);
			data.setDeliveryDateValue(deliveryDateValue);
			data.setDeliveryDate(deliveryDate);
			data.setShopPickUp(shopPickUp);
			data.setCityDeliver(cityDeliver);
			data.setExpressDeliver(expressDeliver);
			data.setFreightFree(freightFree);
			data.setFreightTemplateId(freightTemplateId);
//			data.setOnShelfAuto(onShelfAuto);
//			data.setOnShelfDate(onShelfDate);
//			data.setOnShelfTime(onShelfTime);
//			data.setOffShelfAuto(offShelfAuto);
//			data.setOffShelfDate(offShelfDate);
//			data.setOffShelfTime(offShelfTime);
			data.setMemo(memo);
			data.setStatus(status);
			
			data.setDisplayName(getQData.get(0).get("DISPLAYNAME").toString());
			data.setShareDescription(getQData.get(0).get("SHAREDESCRIPTION").toString());
			data.setSimpleDescription(getQData.get(0).get("SIMPLEDESCRIPTION").toString());
			
			data.setDisplayName_lang(new ArrayList<DCP_GoodsOnlineDetailRes.displayNameLang>());
			data.setShareDescription_lang(new ArrayList<DCP_GoodsOnlineDetailRes.shareDescription>());
			data.setSimpleDescription_lang(new ArrayList<DCP_GoodsOnlineDetailRes.simpleDescription>());
			//商品多语言
			Map<String, Boolean> condition_pluName_Lang = new HashMap<String, Boolean>(); //查詢條件		
			condition_pluName_Lang.put("LANG_TYPE", true);
			//调用过滤函数
			List<Map<String, Object>> pluNameLangDatas=MapDistinct.getMap(getQData, condition_pluName_Lang);
			for (Map<String, Object> map : pluNameLangDatas)
			{
				String langType = map.get("LANG_TYPE").toString();
				if(langType==null||langType.isEmpty())
				{
					continue;
				}
				DCP_GoodsOnlineDetailRes.shareDescription shareDes = res.new shareDescription();
				shareDes.setLangType(langType);
				shareDes.setName(map.get("SHAREDESCRIPTION").toString());
				data.getShareDescription_lang().add(shareDes);
				
				DCP_GoodsOnlineDetailRes.simpleDescription simpleDes = res.new simpleDescription();
				simpleDes.setLangType(langType);
				simpleDes.setName(map.get("SIMPLEDESCRIPTION").toString());
				data.getSimpleDescription_lang().add(simpleDes);
				
				DCP_GoodsOnlineDetailRes.displayNameLang lang = res.new displayNameLang();
				lang.setLangType(langType);
				lang.setName(map.get("DISPLAYNAME").toString());
				if (langType.equals(curLangType))
				{
					data.setPluName(map.get("DISPLAYNAME").toString());
					data.setDisplayName(map.get("DISPLAYNAME").toString());
					data.setShareDescription(map.get("SHAREDESCRIPTION").toString());
					data.setSimpleDescription(map.get("SIMPLEDESCRIPTION").toString());
				}
				
				data.getDisplayName_lang().add(lang);
				
			}
			
			
			data.setClassList(new ArrayList<DCP_GoodsOnlineDetailRes.classMemu>());
			//商品多语言
			Map<String, Boolean> condition_class = new HashMap<String, Boolean>(); //查詢條件		
			condition_class.put("CLASSNO", true);
			//调用过滤函数
			List<Map<String, Object>> classMemuDatas=MapDistinct.getMap(getQData, condition_class);
			for (Map<String, Object> map : classMemuDatas)
			{
				String classNo = map.get("CLASSNO").toString();
				String className =  map.get("CLASSNAME").toString();
				if(classNo==null||classNo.isEmpty())
				{
					continue;
				}
				DCP_GoodsOnlineDetailRes.classMemu memu = res.new classMemu();
				memu.setClassNo(classNo);
				memu.setClassName(className);
				data.getClassList().add(memu);
			}
			
			data.setRefClassList(new ArrayList<DCP_GoodsOnlineDetailRes.refClassMemu>());
			
			//商品多语言
			Map<String, Boolean> condition_class_ref = new HashMap<String, Boolean>(); //查詢條件		
			condition_class_ref.put("CLASSNO_REF", true);
			//调用过滤函数
			List<Map<String, Object>> classMemuDatas_ref=MapDistinct.getMap(getQData, condition_class_ref);
			for (Map<String, Object> map : classMemuDatas_ref)
			{
				String classNo = map.get("CLASSNO_REF").toString();
				String className =  map.get("CLASSNAME_REF").toString();
				if(classNo==null||classNo.isEmpty())
				{
					continue;
				}
				DCP_GoodsOnlineDetailRes.refClassMemu memu = res.new refClassMemu();
				memu.setClassNo(classNo);
				memu.setClassName(className);
				data.getRefClassList().add(memu);
			}
			
			data.setRangeList(new ArrayList<DCP_GoodsOnlineDetailRes.range>());
			
			
			data.setIntroList(new ArrayList<DCP_GoodsOnlineDetailRes.intro>());
			//商品多语言
			Map<String, Boolean> condition_intro = new HashMap<String, Boolean>(); //查詢條件		
			condition_intro.put("ATTRID", true);
			//调用过滤函数
			List<Map<String, Object>> cintroDatas = MapDistinct.getMap(getQData, condition_intro);
			for (Map<String, Object> map : cintroDatas)
			{
				String attrId = map.get("ATTRID").toString();
				String attrName =  map.get("ATTRNAME").toString();
				String intro =  map.get("INTRO").toString();
				if(attrId==null||attrId.isEmpty())
				{
					continue;
				}
				DCP_GoodsOnlineDetailRes.intro introModel = res.new intro();
				introModel.setAttrId(attrId);
				introModel.setAttrName(attrName);
				introModel.setIntro(intro);
				
				data.getIntroList().add(introModel);
				
				
			}
			
			data.setMsgKindList(new ArrayList<DCP_GoodsOnlineDetailRes.msgKind>());
			//商品多语言
			Map<String, Boolean> condition_msgKind = new HashMap<String, Boolean>(); //查詢條件		
			condition_msgKind.put("MSGKINDID", true);
			//调用过滤函数
			List<Map<String, Object>> msgKindDatas = MapDistinct.getMap(getQData, condition_msgKind);
			for (Map<String, Object> map : msgKindDatas)
			{
				String msgKindId = map.get("MSGKINDID").toString();
				String need =  map.get("NEED").toString();
				if(msgKindId==null||msgKindId.isEmpty())
				{
					continue;
				}
				DCP_GoodsOnlineDetailRes.msgKind magKindModel = res.new msgKind();
				magKindModel.setMsgKindId(msgKindId);
				magKindModel.setNeed(need);
				data.getMsgKindList().add(magKindModel);
			}
			
			//【ID1033123】【标准产品3.0】商品标签及商城副标题展示---中台服务 by jinzma 20230531
			data.setTags(new ArrayList<>());
			sql = " select a.tagno,a.onlilasorting,b.tagname from dcp_tagtype_detail a"
					+ " left join dcp_tagtype_lang b on a.eid=b.eid and a.taggrouptype=b.taggrouptype and a.tagno=b.tagno and b.lang_type='"+req.getLangType()+"' "
					+ " where a.eid='"+req.geteId()+"' and a.taggrouptype='GOODS' and a.id='"+pluNo+"' "
                    + " order by a.onlilasorting";
			List<Map<String, Object>> getQTag = this.doQueryData(sql, null);
			if (!CollectionUtils.isEmpty(getQTag)){
				for (Map<String, Object>oneTag:getQTag){
					DCP_GoodsOnlineDetailRes.Tags tags = res.new Tags();
					tags.setTagNo(oneTag.get("TAGNO").toString());
					tags.setTagName(oneTag.get("TAGNAME").toString());
					tags.setOnliLaSorting(oneTag.get("ONLILASORTING").toString());
					
					data.getTags().add(tags);
				}
			}
		}
		
		res.setDatas(data);
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		
	}
	
	@Override
	protected String getQuerySql(DCP_GoodsOnlineDetailReq req) throws Exception {
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		String classType = "ONLINE";
		String pluNo = req.getRequest().getPluNo();
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" select * from ( ");
		sqlbuf.append(" select A.*,AL.LANG_TYPE,AL.DISPLAYNAME,AL.SIMPLEDESCRIPTION,AL.SHAREDESCRIPTION,C.CLASSNO,CL.CLASSNAME CLASSNAME,"
				+ "RC.CLASSNO  CLASSNO_REF,RCL.CLASSNAME  CLASSNAME_REF,D.ATTRID,DL.INTRO,E.ATTRNAME,F.MSGKINDID,F.NEED,N.ATTRGROUPNAME ");
		sqlbuf.append(" from DCP_GOODS_ONLINE A ");
		sqlbuf.append(" left join DCP_GOODS_ONLINE_LANG AL on A.EID=AL.EID AND A.PLUNO=AL.PLUNO ");
		sqlbuf.append(" left join DCP_CLASS_GOODS C ON  A.EID=C.EID AND A.PLUNO=C.PLUNO AND C.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join DCP_CLASS_LANG CL ON C.EID=CL.EID AND C.CLASSNO=CL.CLASSNO AND C.CLASSTYPE=CL.CLASSTYPE AND CL.CLASSTYPE='"+classType+"' and CL.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" left join DCP_GOODS_ONLINE_REFCLASS RC ON A.EID=RC.EID AND A.PLUNO=RC.PLUNO AND RC.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join DCP_CLASS_LANG RCL ON  RC.EID=RCL.EID AND RCL.CLASSTYPE=RC.CLASSTYPE  AND  RCL.CLASSNO=RC.CLASSNO AND RCL.CLASSTYPE='"+classType+"' and RCL.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" left join DCP_GOODS_ONLINE_INTRO D ON A.EID=D.EID AND A.PLUNO=D.PLUNO ");
		sqlbuf.append(" LEFT JOIN DCP_GOODS_ONLINE_INTRO_LANG DL ON A.EID=DL.EID AND D.PLUNO=DL.PLUNO AND D.ATTRID=DL.ATTRID AND DL.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" LEFT JOIN Dcp_Attribution_Lang E ON  A.EID=E.EID AND D.ATTRID=E.ATTRID AND E.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" left join DCP_GOODS_ONLINE_MSGKIND F ON A.EID=F.EID and A.PLUNO=F.PLUNO");
		sqlbuf.append(" LEFT JOIN DCP_ATTRGROUP_LANG N on A.EID=N.EID  and A.ATTRGROUPID=N.ATTRGROUPID  AND N.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" where A.EID='"+eId+"' AND A.PLUNO='"+pluNo+"' "
				+ " order by a.sortId desc ");
		sqlbuf.append(" )");
		
		return sqlbuf.toString();
	}
	
}
