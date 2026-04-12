package com.dsc.spos.service.webhook;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.youzan.YouZanUtilsV3;
import com.dsc.spos.utils.ESBUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebHookGoodsService{

	static Logger logger = LogManager.getLogger(WebHookGoodsService.class);

	WebHookBaseMessage message;

	public void execute(String eid,String goodsid) throws Exception
	{/*
		List<Map<String,Object>> maps = WebHookService.GetWebHookSettings(eid,WebHookEventEnum.GOODS.name());

		for(int i=0;i<maps.size();i++)
		{
			String FORMAT = maps.get(i).get("FORMAT").toString();
			String URL = maps.get(i).get("URL").toString();

			if(URL.contains("[domain]")){
				URL = URL.replace("[domain]", ESBConfig.REALNAME);
			}

			if(FORMAT.equalsIgnoreCase("YOUZAN"))
			{
				executeYOUZAN(eid,goodsid,maps.get(i));
			}
		}*/

	}


	public void executeAll(String eid) throws Exception
	{
		List<Map<String,Object>> maps = WebHookService.GetWebHookSettings(eid,WebHookEventEnum.GOODS.name());

		for(int i=0;i<maps.size();i++)
		{
			String FORMAT = maps.get(i).get("FORMAT").toString();
			if(FORMAT.equalsIgnoreCase("YOUZAN"))//有赞
			{
				executeYOUZANAll(eid,maps.get(i));
			}
		}

	}

	public static String langType="zh_CN";
	public static String yz_SqlB1=""
			+ " SELECT A.EID,A.PLUBARCODE,A.PLUNO,A.FEATURENO,FL.FEATURENAME,"
			+ " UL.UNAME,A.STATUS AS STATUS_BARCODE,A.UNIT AS UNIT,"
			+ " A.STATUS AS ASTATUS,G.STATUS AS GSTATUS,I.STATUS AS ISTATUS,"
			+ " G.PRICE AS PRICE,"
			+ " H.PLU_NAME AS PLU_NAME, "
			+ " image.LISTIMAGE "
			+ " FROM DCP_GOODS_BARCODE A "
			+ " left join  DCP_GOODS_FEATURE_LANG FL on A.EID =FL.EID AND A.PLUNO=FL.PLUNO AND A.FEATURENO=FL.FEATURENO and FL.Lang_Type='"+langType+"' "
			+ " left join dcp_unit_lang UL on A.EID =UL.EID AND A.UNIT=UL.UNIT and UL.Lang_Type='"+langType+"' "
			+ " left join dcp_goods G  on A.EID =G.EID  and A.PLUNO=G.PLUNO "
			+ " left join DCP_GOODS_LANG H on G.EID =H.EID  and G.PLUNO=H.PLUNO and H.Lang_Type='"+langType+"' "
			+ " left join dcp_goodsimage image on image.eid=A.eid and image.pluno=A.pluno and image.apptype='ALL' "
			+ " INNER JOIN ("
			+ " SELECT B.* FROM DCP_GOODSTEMPLATE_GOODS B "
			+ " INNER JOIN DCP_GOODSTEMPLATE C ON B.EID=C.EID AND B.TEMPLATEID=C.TEMPLATEID AND C.STATUS='100' "
			+ " INNER JOIN DCP_GOODSTEMPLATE_RANGE D ON C.EID=D.EID AND C.TEMPLATEID=D.TEMPLATEID AND D.RANGETYPE='3' "
			+ " AND  exists(select 8 from CRM_CHANNEL WHERE EID=D.EID AND CHANNELID=D.ID AND APPTYPE='11' ) "
			+ " ) I ON A.EID=I.EID AND A.PLUNO=I.PLUNO "
			;

	public void executeYOUZANAll(String eid,Map<String,Object> map) throws Exception
	{
		String langType="zh_CN";

		String sqlA1=""
				+ " select A.*,B.LANG_TYPE LANGTYPE,B.MASTERPLUNAME, "
				+ " C.ATTRID,C.SORTID AS SORTID_ATTR,CL.ATTRNAME,D.ATTRVALUEID,DL.ATTRVALUENAME, "
				+ " G.FEATURENO,G.ATTRID1,G.ATTRVALUEID1,G.ATTRID2,G.ATTRVALUEID2,G.ATTRID3,G.ATTRVALUEID3,G.PLUNO,G.UNIT,G.PRICE,GL.FEATURENAME,GLL.PLU_NAME, "
				+ " U.UNAME,N.ATTRGROUPNAME "
				+ " from DCP_MSPECGOODS A "
				+ " LEFT JOIN DCP_MSPECGOODS_LANG B ON A.EID=B.EID AND A.MASTERPLUNO=B.MASTERPLUNO "
				+ " LEFT JOIN DCP_MSPECGOODS_ATTR C ON A.EID=C.EID AND A.MASTERPLUNO=C.MASTERPLUNO "
				+ " LEFT JOIN DCP_ATTRIBUTION_LANG CL ON A.EID=CL.EID AND C.ATTRID=CL.ATTRID AND CL.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_ATTRIBUTION_VALUE D ON A.EID=D.EID AND C.ATTRID=D.ATTRID and D.STATUS='100' "
				+ " LEFT JOIN DCP_ATTRIBUTION_VALUE_LANG DL ON A.EID=DL.EID AND D.ATTRID=DL.ATTRID AND D.ATTRVALUEID=DL.ATTRVALUEID AND DL.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_MSPECGOODS_SUBGOODS G ON A.EID=G.EID AND A.MASTERPLUNO=G.MASTERPLUNO "
				+ " LEFT JOIN DCP_MSPECGOODS_SUBGOODS_LANG GL ON A.EID=GL.EID AND A.MASTERPLUNO=GL.MASTERPLUNO AND G.FEATURENO=GL.FEATURENO AND GL.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_GOODS_LANG GLL ON A.EID=GLL.EID AND G.PLUNO=GLL.PLUNO  AND GLL.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_UNIT_LANG U on A.EID=U.EID  and G.UNIT=U.UNIT  AND U.LANG_TYPE='"+langType+"' "
				+ " LEFT JOIN DCP_ATTRGROUP_LANG N on A.EID=N.EID  and A.ATTRGROUPID=N.ATTRGROUPID  AND N.LANG_TYPE='"+langType+"' "


				+ " WHERE A.EID='"+eid+"' AND A.STATUS='100' AND G.PLUNO IN "
				+ " ("
				+ " SELECT DISTINCT A.PLUNO FROM DCP_GOODS_BARCODE A "
				+ " INNER JOIN DCP_GOODSTEMPLATE_GOODS B ON A.EID=B.EID AND A.PLUNO=B.PLUNO AND B.CANSALE='Y'  "
				+ " INNER JOIN DCP_GOODSTEMPLATE C ON B.EID=C.EID AND B.TEMPLATEID=C.TEMPLATEID AND C.STATUS='100'  "
				+ " INNER JOIN DCP_GOODSTEMPLATE_RANGE D ON C.EID=D.EID AND C.TEMPLATEID=D.TEMPLATEID AND D.RANGETYPE='3'   "
				+ " AND  exists(select 8 from CRM_CHANNEL WHERE EID=D.EID AND CHANNELID=D.ID AND APPTYPE='11' )  WHERE A.EID = '"+eid+"' ) "
				;




		YouZanUtilsV3 utils=new YouZanUtilsV3();
		Map<String, Object> paramMap =new HashMap<String, Object>();
		paramMap.put("EID", eid);
		List<Map<String,Object>> dcpECMaps = utils.getYouZanList(paramMap);
		if(dcpECMaps!=null&&dcpECMaps.size()>0){
			String mainUrl=getImageUrl(eid);
			map.put("IMAGEMAINURL", mainUrl);
			for(Map<String,Object> dcpECMap:dcpECMaps){
				try{

					//多特征商品
					{
						String sql1=""
								+ " SELECT A1.*,B1.* FROM ("
								+ sqlA1
								+ " ) A1 "
								+ " LEFT JOIN ( "
								+ yz_SqlB1
								+ " where A.EID='"+eid+"') B1 ON A1.EID=B1.EID AND A1.PLUNO=B1.PLUNO"
								;
						//utils.Log("【同步多规格商品】查询sql="+sql1);
                        WebHookUtils.saveLog("WebHookGoods","【同步多规格商品】查询sql="+sql1);
						List<Map<String,Object>> maps = StaticInfo.dao.executeQuerySQL(sql1, null);
						if(maps!=null&&maps.size()>0){


							Map<String, String> attrnameMap =new HashMap<String, String>();
							Map<String, Map<String, String>> attrvalueMap =new HashMap<String, Map<String, String>>();
							//属性
							Map<String, Boolean> condition_attr = new HashMap<String, Boolean>(); //查詢條件
							condition_attr.put("ATTRID", true);
							//调用过滤函数
							List<Map<String, Object>> attrDatas=MapDistinct.getMap(maps, condition_attr);

							if(attrDatas!=null&&attrDatas.size()>0){

								for (Map<String, Object> attrMap : attrDatas) {

									String attrId = attrMap.get("ATTRID")==null?"":attrMap.get("ATTRID").toString();
									if(attrId==null||attrId.length()<=0){
										continue;
									}
									String attrName = attrMap.get("ATTRNAME")==null?"":attrMap.get("ATTRNAME").toString();
									attrnameMap.put(attrId, attrName);

									List<Map<String,Object>> attrMaps=maps.stream().filter(g->g.get("ATTRID").toString().equals(attrId)).collect(Collectors.toList());
									//属性
									Map<String, Boolean> condition_attrValue = new HashMap<String, Boolean>(); //查詢條件
									condition_attrValue.put("ATTRID", true);
									condition_attrValue.put("ATTRVALUEID", true);
									//调用过滤函数
									List<Map<String, Object>> attrValueDatas=MapDistinct.getMap(attrMaps, condition_attrValue);

									Map<String, String> attrvalue =new HashMap<String, String>();
									if(attrValueDatas!=null&&attrValueDatas.size()>0){
										for (Map<String, Object> mapValue : attrValueDatas) {
											String attrvalueId=mapValue.get("ATTRVALUEID")==null?"":mapValue.get("ATTRVALUEID").toString();
											if(attrvalueId==null||attrvalueId.length()<=0){
												continue;
											}
											String attrvaluename=mapValue.get("ATTRVALUENAME")==null?"":mapValue.get("ATTRVALUENAME").toString();
											attrvalue.put(attrvalueId, attrvaluename);
										}
									}
									attrvalueMap.put(attrId, attrvalue);
								}
							}

							Map<String,List<Map<String,Object>>> groupTypeIdMaps = maps.stream().collect(Collectors.groupingBy(g->g.get("MASTERPLUNO").toString()));
							loop1:for (Map.Entry<String, List<Map<String,Object>>> typeIdEntry : groupTypeIdMaps.entrySet()) {

								Map<String,Object> json1=new HashMap<String,Object>();
								json1.put("itemType", "0");
								//商品编号
								String id=typeIdEntry.getKey();
								//相同券类型组出的List
								List<Map<String,Object>> list1=typeIdEntry.getValue();
								List<String> pluStrList=list1.stream().map(x->x.get("PLUNO").toString()).collect(Collectors.toList());
								pluStrList=pluStrList.stream().distinct().collect(Collectors.toList());
								List<Map<String,Object>> skuList=new ArrayList<Map<String,Object>>();

								Map<String,Object> masterMap=list1.get(0);
								String masterpluno=masterMap.get("MASTERPLUNO")==null?"":masterMap.get("MASTERPLUNO").toString();
								json1.put("outItemBarCode", masterpluno);
								json1.put("outItemId", masterpluno);
								json1.put("outItemNo", masterpluno);
								String minpriceStr=masterMap.get("MINPRICE")==null?"":masterMap.get("MINPRICE").toString();
								BigDecimal minpriceBig=new BigDecimal(minpriceStr);
								minpriceBig=minpriceBig.multiply(new BigDecimal(100));//乘以100，单位转换为分
								String minprice=String.valueOf(minpriceBig.intValue());

								json1.put("price", minprice);
								String masterpluname=masterMap.get("MASTERPLUNAME")==null?"":masterMap.get("MASTERPLUNAME").toString();
								json1.put("title", masterpluname);
								String memo=masterMap.get("MEMO")==null?"":masterMap.get("MEMO").toString();
								json1.put("desc", memo);

								//多规格中 每一个商品PLUNO与PLUBARCODE编号相同
								//PLUNO 对应图片列表
								List<String> imagesList=new ArrayList<String>();
								//PLUNO 对应模板，多个随便取一个
								List<Map<String,Object>> pluNoTemplateList = new ArrayList<>();

								for(String pluNo:pluStrList){


									List<Map<String,Object>> list2=list1.stream().filter(g->g.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
									List<String> barCodeStrList=list2.stream().map(x->x.get("PLUBARCODE").toString()).collect(Collectors.toList());
									barCodeStrList=barCodeStrList.stream().distinct().collect(Collectors.toList());
									if(barCodeStrList!=null&&barCodeStrList.size()>1){
										utils.writelogFileName("YouZanGoodsPostFailure","多规格商品商品["+id+"],PLUNO["+pluNo+"]包含多个条码["+barCodeStrList.toString()+"],资料异常");
										continue loop1;
									}
									Map<String,Object> pluMap=list2.get(0);
									String skuNo=pluMap.get("PLUBARCODE")==null?"":pluMap.get("PLUBARCODE").toString();
									String priceStr=pluMap.get("PRICE")==null?"":pluMap.get("PRICE").toString();
									BigDecimal priceBig=new BigDecimal(priceStr);
									priceBig=priceBig.multiply(new BigDecimal(100));//乘以100，单位转换为分
									String price=String.valueOf(priceBig.intValue());
									Map<String,Object> skuMap=new HashMap<String,Object>();
									skuMap.put("price", price);
									skuMap.put("skuNo", skuNo);

									String image1=pluMap.get("LISTIMAGE")==null?"":pluMap.get("LISTIMAGE").toString();
									if(mainUrl!=null&&mainUrl.trim().length()>1&&image1!=null&&image1.trim().length()>1){
										String iamgeUrl=mainUrl+image1;
										skuMap.put("skuImgUrl", iamgeUrl);
										imagesList.add(iamgeUrl);
									}
									//随便取一个子商品的对应的模板
									if (pluNoTemplateList==null||pluNoTemplateList.isEmpty())
									{
										pluNoTemplateList = this.getPluTemplateList(eid,pluMap.getOrDefault("PLUNO","").toString());
									}
									List<Map<String,Object>> skuPropsList=new ArrayList<Map<String,Object>>();
									String attrid1=pluMap.get("ATTRID1")==null?"":pluMap.get("ATTRID1").toString();
									String attrvalueid1=pluMap.get("ATTRVALUEID1")==null?"":pluMap.get("ATTRVALUEID1").toString();
									if(attrvalueid1!=null&&attrvalueid1.length()>0){
										Map<String,Object> skuProp=new HashMap<String,Object>();
										String attrName=attrnameMap.get(attrid1);
										skuProp.put("propName", attrName);
										Map<String,String> valueMap=attrvalueMap.get(attrid1);
										if(valueMap!=null&&!valueMap.isEmpty()){
											String attrvaluename=valueMap.get(attrvalueid1);
											skuProp.put("propValueName", attrvaluename);
										}
										skuPropsList.add(skuProp);
									}

									String attrid2=pluMap.get("ATTRID2")==null?"":pluMap.get("ATTRID2").toString();
									String attrvalueid2=pluMap.get("ATTRVALUEID2")==null?"":pluMap.get("ATTRVALUEID2").toString();
									if(attrvalueid2!=null&&attrvalueid2.length()>0){
										Map<String,Object> skuProp=new HashMap<String,Object>();
										String attrName=attrnameMap.get(attrid2);
										skuProp.put("propName", attrName);
										Map<String,String> valueMap=attrvalueMap.get(attrid2);
										if(valueMap!=null&&!valueMap.isEmpty()){
											String attrvaluename=valueMap.get(attrvalueid2);
											skuProp.put("propValueName", attrvaluename);
										}
										skuPropsList.add(skuProp);
									}

									String attrid3=pluMap.get("ATTRID3")==null?"":pluMap.get("ATTRID3").toString();
									String attrvalueid3=pluMap.get("ATTRVALUEID3")==null?"":pluMap.get("ATTRVALUEID3").toString();
									if(attrvalueid3!=null&&attrvalueid3.length()>0){
										Map<String,Object> skuProp=new HashMap<String,Object>();
										String attrName=attrnameMap.get(attrid3);
										skuProp.put("propName", attrName);
										Map<String,String> valueMap=attrvalueMap.get(attrid3);
										if(valueMap!=null&&!valueMap.isEmpty()){
											String attrvaluename=valueMap.get(attrvalueid3);
											skuProp.put("propValueName", attrvaluename);
										}
										skuPropsList.add(skuProp);
									}
									skuMap.put("skuProps", skuPropsList);
									skuList.add(skuMap);
								}
								json1.put("outItemNo", masterpluno);
								json1.put("imageUrls", imagesList);
								json1.put("skus", skuList);
								String status1=masterMap.get("ASTATUS")==null?"0":masterMap.get("ASTATUS").toString();
								String status2=masterMap.get("GSTATUS")==null?"0":masterMap.get("GSTATUS").toString();
								String status3=masterMap.get("ISTATUS")==null?"0":masterMap.get("ISTATUS").toString();
								if("100".equals(status1)&&"100".equals(status2)&&"100".equals(status3)){
									json1.put("status", "1");//商品状态，1:销售中， 2:已下架
								}else{
									json1.put("status", "2");//商品状态，1:销售中， 2:已下架
								}

								//添加模板
								List<Map<String,Object>> templateList = new ArrayList<>();
								if (pluNoTemplateList!=null&&!pluNoTemplateList.isEmpty())
								{
									for (Map<String,Object> par : pluNoTemplateList)
									{
										Map<String,Object> pluTemMap=new HashMap<String,Object>();
										pluTemMap.put("templateId", par.getOrDefault("TEMPLATEID","").toString());
										pluTemMap.put("templateName", par.getOrDefault("TEMPLATENAME","").toString());
										templateList.add(pluTemMap);
									}

								}

								json1.put("templateList", templateList);

								postYouZan(eid, masterpluno, map, dcpECMap, json1);
							}


						}

					}

					//单特征商品 可能会有多条码
					{
						String sql1=yz_SqlB1
								+ " WHERE A.EID='"+eid+"' "
								+ " AND A.PLUNO NOT IN("
								+ " SELECT DISTINCT A.PLUNO FROM DCP_MSPECGOODS_SUBGOODS A  "
								+ " LEFT JOIN DCP_MSPECGOODS B ON A.EID=B.EID AND A.MASTERPLUNO=B.MASTERPLUNO "
								+ " WHERE B.EID='"+eid+"' AND B.STATUS='100' "
								+ ") "
								;
						//utils.Log("【同步普通格商品】查询sql="+sql1);
                        WebHookUtils.saveLog("WebHookGoods","【同步普通格商品】查询sql="+sql1);
						List<Map<String,Object>> maps = StaticInfo.dao.executeQuerySQL(sql1, null);
						if(maps!=null&&maps.size()>0){
							for(Map<String,Object> couponTypeMap:maps){
								Map<String,Object> json1=PostCouponTypeFillMessageYZ(eid, null, couponTypeMap, map);
								String pluBarcode=couponTypeMap.get("PLUBARCODE")==null?"":couponTypeMap.get("PLUBARCODE").toString();
								postYouZan(eid, pluBarcode, map, dcpECMap, json1);
							}
						}
					}

				}catch(Exception e){

				}
			}
		}


	}

	public String getImageUrl(String eId) throws Exception{

		// 拼接返回图片路径
		String ISHTTPS = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "ISHTTPS");
		String httpStr="1".equals(ISHTTPS)?"https://":"http://";
		String DomainName = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "DomainName");
		if (DomainName!=null&&DomainName.endsWith("/")) {
			DomainName = httpStr+DomainName+"resource/image/";
		}else if(DomainName!=null&&DomainName.length()>0){
			DomainName = httpStr+DomainName+"/resource/image/";
		}
		return DomainName;
	}

	/*public void executeYOUZAN(String eid,String barCode,Map<String,Object> map ) throws Exception
	{*//*
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		Map<String, Object> paramMap =new HashMap<String, Object>();
		paramMap.put("EID", eid);
		List<Map<String,Object>> dcpECMaps = utils.getYouZanList(paramMap);
		if(dcpECMaps!=null&&dcpECMaps.size()>0){
			for(Map<String,Object> dcpECMap:dcpECMaps){
				try{
					postYouZan(eid, barCode, map, dcpECMap,null);
				}catch(Exception e){

				}
			}
		}*//*
	}*/

	public void postYouZan(String eid,String barCode,Map<String,Object> map,Map<String,Object> dcpECMap,Map<String, Object> params)throws Exception{
		int i=2;
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		while(true)
		{
			i++;
			if(i > 3)
			{
				//消息推送尝试3次
				return;
			}

			try
			{
				if(params==null||params.isEmpty()){
					String mainUrl=map.get("IMAGEMAINURL")==null?"":map.get("IMAGEMAINURL").toString();
					if(mainUrl==null||mainUrl.trim().length()<1){
						mainUrl=getImageUrl(eid);
					}
					map.put("IMAGEMAINURL", mainUrl);
					//创建三方券活动（创建鼎捷侧券类型）
					params = PostCouponTypeFillMessageYZ(eid, barCode,null,map);
					if(params==null||params.isEmpty()){
						utils.Log("\r\n PLUBARCODE["+barCode+"]资料不符合抛转需求");
						continue;
					}
				}

				String resStr2=utils.PostData("ITEM-ADD",dcpECMap, params);
				//判断response
				if(StrUtil.isNotEmpty(resStr2)){
					JSONObject dpsResJson = new JSONObject();
					dpsResJson = JSON.parseObject(resStr2,Feature.OrderedField);
					String resCode=dpsResJson.getString("errorCode");
					String errorMsg=dpsResJson.getString("errorMsg");
					if(resCode!=null&&resCode.equals("200")){
						utils.Log("\r\nPLUBARCODE["+barCode+"]本轮第"+i+"次抛转成功:"
								+ "\r\n参数:"+com.alibaba.fastjson.JSON.toJSONString(map)
								+ "\r\nrequest:"+com.alibaba.fastjson.JSON.toJSONString(params)+"\r\nresponse:"+resStr2 );
						utils.writelogFileName("YouZanGoodsPostSuccess","商品:"+barCode+"["+params.get("title")+"] 抛转成功\r\n");
						break;
					}else{
						utils.Log("\r\nPLUBARCODE["+barCode+"]本轮第"+i+"次抛转失败:"
								+ "\r\n参数:"+com.alibaba.fastjson.JSON.toJSONString(map)
								+ "\r\nrequest:"+com.alibaba.fastjson.JSON.toJSONString(params)+"\r\nresponse:"+resStr2 );
						if(i==3){
							utils.writelogFileName("YouZanGoodsPostFailure","商品:"+barCode+"["+params.get("title")+"] 抛转失败——"+errorMsg+"\r\n");
						}
						Thread.sleep(100);
						continue;
					}
				}else{
					utils.Log("\r\nPLUBARCODE["+barCode+"]本轮第"+i+"次抛转失败:"
							+ "\r\n参数:"+com.alibaba.fastjson.JSON.toJSONString(map)
							+ "\r\nrequest:"+com.alibaba.fastjson.JSON.toJSONString(params)+"\r\nresponse 返回空" );
					Thread.sleep(100);
					continue;
				}
			}
			catch(Exception ex)
			{
				utils.Log("\r\nPLUBARCODE["+barCode+"]本轮第"+i+"次抛转失败:"
						+ "\r\n发送异常\r\n参数:"+com.alibaba.fastjson.JSON.toJSONString(map));
				Thread.sleep(100);
				utils.ErrorLog(utils.getTrace(ex));
			}

		}
	}


	/**
	 * @param eid
	 * @param pluBarcode couponTypeMap无数据时，需要根据pluBarcode查商品信息
	 * @param couponTypeMap 已经由SQL查到的商品信息，当此处有数据时，不需要再根据pluBarcode查商品信息
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> PostCouponTypeFillMessageYZ(String eid,String pluBarcode, Map<String,Object> couponTypeMap,Map<String,Object> map) throws Exception
	{

		Map<String,Object> json1=new HashMap<String,Object>();
		YouZanUtilsV3 utils=new YouZanUtilsV3();
		try{
			if(couponTypeMap==null||couponTypeMap.isEmpty()){
				String sql1=yz_SqlB1
						+ " WHERE A.EID='"+eid+"' "
						+ " AND A.PLUNO NOT IN("
						+ " SELECT DISTINCT A.PLUNO FROM DCP_MSPECGOODS_SUBGOODS A  "
						+ " LEFT JOIN DCP_MSPECGOODS B ON A.EID=B.EID AND A.MASTERPLUNO=B.MASTERPLUNO "
						+ " WHERE B.EID='"+eid+"' AND B.STATUS='100' AND A.PLUBARCODE ='"+pluBarcode+"'"
						+ ") "
						;
				List<Map<String,Object>> maps = StaticInfo.dao.executeQuerySQL(sql1, null);
				if(maps!=null&&maps.size()>0){
					couponTypeMap=maps.get(0);
				}
			}
			pluBarcode=couponTypeMap.get("PLUBARCODE")==null?"":couponTypeMap.get("PLUBARCODE").toString();
			String pluNo=couponTypeMap.get("PLUNO")==null?"":couponTypeMap.get("PLUNO").toString();
			json1.put("outItemNo", pluNo);//三方商品编码 (编码与ID是区别的，是可以用户自定义规则并可见的)
			json1.put("outItemBarCode", pluBarcode);//三方商品条码 (零售)
			String title=couponTypeMap.get("PLU_NAME")==null?"":couponTypeMap.get("PLU_NAME").toString();
			json1.put("title", title);//商品名称

			String priceStr=(couponTypeMap.get("PRICE")==null||couponTypeMap.get("PRICE").toString().length()<=0)?"0":couponTypeMap.get("PRICE").toString();
			BigDecimal priceBig=new BigDecimal(priceStr);
			priceBig=priceBig.multiply(new BigDecimal(100));//乘以100，单位转换为分
			String price=String.valueOf(priceBig.intValue());
			json1.put("price", price);//商品价格
			String status1=couponTypeMap.get("ASTATUS")==null?"0":couponTypeMap.get("ASTATUS").toString();
			String status2=couponTypeMap.get("GSTATUS")==null?"0":couponTypeMap.get("GSTATUS").toString();
			String status3=couponTypeMap.get("ISTATUS")==null?"0":couponTypeMap.get("ISTATUS").toString();
			if("100".equals(status1)&&"100".equals(status2)&&"100".equals(status3)){
				json1.put("status", "1");//商品状态，1:销售中， 2:已下架
			}else{
				json1.put("status", "2");//商品状态，1:销售中， 2:已下架
			}
			json1.put("outItemId", pluNo);//outItemId三方商品 id
			json1.put("itemType", "0");//商品类型： 0—普通商品 3—UMP降价拍 5—外卖商品 10—分销商品 20—会员卡商品 21—礼品卡商品 22—团购券 25—批发商品 30—收银台商品 31—知识付费商品 35—酒店商品 40—美业商品 60—虚拟商品 61—电子卡券
			String desc=couponTypeMap.get("MEMO")==null?"":couponTypeMap.get("MEMO").toString();
			json1.put("desc", desc);



			List<Map<String,Object>> skuList=new ArrayList<Map<String,Object>>();
			Map<String,Object> skuMap=new HashMap<String,Object>();

			List<Map<String,Object>> skuPropsList=new ArrayList<Map<String,Object>>();
			String unitName=couponTypeMap.get("UNAME")==null?"":couponTypeMap.get("UNAME").toString();
			json1.put("unit", unitName);

//			String featureNo=couponTypeMap.get("FEATURENO")==null?"":couponTypeMap.get("FEATURENO").toString();
			String featureName=couponTypeMap.get("FEATURENAME")==null?"":couponTypeMap.get("FEATURENAME").toString();
			if(featureName!=null&&featureName.length()>0){
				Map<String,Object> skuPropsMap2=new HashMap<String,Object>();
				skuPropsMap2.put("propName", "特征");
				skuPropsMap2.put("propValueName", featureName);
				skuPropsList.add(skuPropsMap2);
			}
			skuMap.put("price", price);
			skuMap.put("skuNo", pluBarcode);
			skuMap.put("skuProps", skuPropsList);

			List<String> imagesList=new ArrayList<String>();
			String mainUrl=map.get("IMAGEMAINURL")==null?"":map.get("IMAGEMAINURL").toString();
			String image1=couponTypeMap.get("LISTIMAGE")==null?"":couponTypeMap.get("LISTIMAGE").toString();
			if(mainUrl!=null&&mainUrl.trim().length()>1&&image1!=null&&image1.trim().length()>1){
				String iamgeUrl=mainUrl+image1;
				skuMap.put("skuImgUrl", iamgeUrl);
				imagesList.add(iamgeUrl);
			}
			json1.put("imageUrls", imagesList);

			skuList.add(skuMap);
			json1.put("skus", skuList);

			//添加模板
			//PLUNO 对应模板，多个随便取一个
			List<Map<String,Object>> pluNoTemplateList = this.getPluTemplateList(eid,pluNo);
			List<Map<String,Object>> templateList = new ArrayList<>();
			if (pluNoTemplateList!=null&&!pluNoTemplateList.isEmpty())
			{
				for (Map<String,Object> par : pluNoTemplateList)
				{
					Map<String,Object> pluTemMap=new HashMap<String,Object>();
					pluTemMap.put("templateId", par.getOrDefault("TEMPLATEID","").toString());
					pluTemMap.put("templateName", par.getOrDefault("TEMPLATENAME","").toString());
					templateList.add(pluTemMap);
				}

			}
			json1.put("templateList", templateList);


		}catch(Exception e){
			utils.ErrorLog(utils.getTrace(e));
		}



		return json1;

	}

	/*public void LogYouZanGoodsSuccess(String log) throws Exception{

		YouZanUtilsV3 utils=new YouZanUtilsV3();
		utils.writelogFileName("YouZanGoodsPostSuccess", log);
	}
	public void LogYouZanGoodsFailure(String log) throws Exception{

		YouZanUtilsV3 utils=new YouZanUtilsV3();
		utils.writelogFileName("YouZanGoodsPostFailure", log);
	}*/

    /**
     * 获取pluno对应的商品模板id
     * @param eId
     * @param pluNo
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getPluTemplateList(String eId,String pluNo) throws Exception
    {
        try
        {
            if (eId.isEmpty()||pluNo.isEmpty())
            {
                return null;
            }
            String sql = "SELECT B.PLUNO,C.TEMPLATEID,CL.TEMPLATENAME FROM DCP_GOODSTEMPLATE_GOODS B "
                    + " INNER JOIN DCP_GOODSTEMPLATE C ON B.EID=C.EID AND B.TEMPLATEID=C.TEMPLATEID AND C.STATUS='100'"
                    + " INNER JOIN DCP_GOODSTEMPLATE_RANGE D ON C.EID=D.EID AND C.TEMPLATEID=D.TEMPLATEID AND D.RANGETYPE='3' "
                    + " AND  exists(select 8 from CRM_CHANNEL WHERE EID=D.EID AND CHANNELID=D.ID AND APPTYPE='11') "
                    + " LEFT JOIN DCP_GOODSTEMPLATE_LANG CL  ON  CL.EID=C.EID AND CL.TEMPLATEID=C.TEMPLATEID AND CL.LANG_TYPE='zh_CN' "
                    + " where B.EID='"+eId+"' and B.PLUNO='"+pluNo+"'";
            List<Map<String,Object>> mapList = StaticInfo.dao.executeQuerySQL(sql,null);
            return mapList;

        }
        catch (Exception e)
        {

        }

        return null;
    }


}
