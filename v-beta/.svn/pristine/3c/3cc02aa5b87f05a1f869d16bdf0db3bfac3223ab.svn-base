package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsSyncReq;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsSyncReq.level1goodsElm;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsSyncReq.level1shopsElm;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsSyncRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：OrderCategorySync
 * 服务说明：外卖商品同步
 * @author jinzma	 
 * @since  2019-03-12
 */

public class DCP_OrderGoodsSync extends SPosAdvanceService<DCP_OrderGoodsSyncReq,DCP_OrderGoodsSyncRes >{

	@Override
	protected void processDUID(DCP_OrderGoodsSyncReq req, DCP_OrderGoodsSyncRes res) throws Exception {
		// TODO 自动生成的方法存根
		String[] loadDocTypes = req.getLoadDocType();
		String operType= req.getOperType();   //1.新增 2.修改 3.删除
		String loadDocType=loadDocTypes[0].toString();
		if (Check.Null(loadDocType))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "外卖平台必须设置，");		
		}
		if (operType.equals("1")) operType="4";
		if (operType.equals("2")) operType="5";
		if (operType.equals("3")) operType="6";

		try
		{
			goodsSync(req,loadDocType,operType);
			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderGoodsSyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderGoodsSyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderGoodsSyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderGoodsSyncReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String[] loadDocType = req.getLoadDocType();
		String operType = req.getOperType();
		List<level1goodsElm> goodsdatas = req.getGoodsdatas();
		List<level1shopsElm> shopsdatas = req.getShopsdatas();

		if (loadDocType == null || loadDocType.length == 0) 
		{
			errMsg.append("外卖平台不可为空值, ");
			isFail = true;
		}
		if (Check.Null(operType) ) 
		{
			errMsg.append("操作类型不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level1goodsElm par : goodsdatas) 
		{	
			if (Check.Null(par.getPluNO())) 
			{
				errMsg.append("商品编号不可为空值, ");
				isFail = true;
			}	

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		for (level1shopsElm par : shopsdatas) 
		{	
			if (Check.Null(par.getErpShopNO())) 
			{
				errMsg.append("门店编号不可为空值, ");
				isFail = true;
			}	
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderGoodsSyncReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderGoodsSyncReq>(){} ;
	}

	@Override
	protected DCP_OrderGoodsSyncRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderGoodsSyncRes();
	}

	private void goodsSync(DCP_OrderGoodsSyncReq req,String load_DocType ,String trans_Type)  throws Exception {

		String eId = req.geteId();
		String belFirm = req.getOrganizationNO();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String sDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String sTime = df.format(cal.getTime());
		String shops = toFormatShops(req) ;
		String goods = toFormatGoods(req) ;
		String sql ="";	
		DataValue[] insValue = null;
		List<level1shopsElm> shopsDatas = req.getShopsdatas();
		List<level1goodsElm> goodsDatas = req.getGoodsdatas();

		String[] OC_ConditionValues = {eId}; 		
		sql = " select a.*,b.SPECNO,b.SPECNAME,b.PRICE,b.STOCKQTY,b.PACKAGEFEE,b.ISONSHELF,b.NETWEIGHT,c.attrvalue,c.attrname from OC_goods a "
				+ " inner join OC_goods_spec b on a.EID=b.EID and a.pluno=b.pluno and a.belfirm=b.belfirm "
				+ " left  join OC_goods_attr c on a.EID=c.EID and a.pluno=c.pluno and a.belfirm=c.belfirm "
				+ " where a.EID=?  and a.pluno in (" +goods + ")  " ;
		if(belFirm!=null&&belFirm.length()>0)
		{
			sql+=" and a.belfirm='"+belFirm+"'";
		}

		List<Map<String, Object>> getTvQData = this.doQueryData(sql, OC_ConditionValues);	

		String[] mapping_ConditionValues = {eId,load_DocType}; 
		sql = " select a.EID,a.SHOPID,a.order_shop,a.order_shopname,a.order_pluno,a.order_pluname,a.pluno,a.pluname,"
				+ " a.order_categoryno,a.order_categoryname, "				
				+ " b.order_specno,b.order_specname,b.specno,b.specname, "
				+ " c.order_attrname,c.order_attrvalue,c.attrname,c.attrvalue from OC_mappinggoods a "
				+ " inner join OC_mappinggoods_spec b "
				+ " on a.EID=b.EID and a.SHOPID=b.SHOPID and a.load_doctype=b.load_doctype and a.pluno=b.pluno "
				+ " left  join OC_mappinggoods_attr c "
				+ " on a.EID=c.EID and a.SHOPID=c.SHOPID and a.load_doctype=c.load_doctype and a.pluno=c.pluno "
				+ " where a.EID=? and a.LOAD_DOCTYPE = ? and a.pluno in (" +goods + ") and a.SHOPID in (" +shops + ") " ;

		List<Map<String, Object>> getMappingQData = this.doQueryData(sql, mapping_ConditionValues);	
		
		
		String[] mappingCategory_ConditionValues = {eId,load_DocType}; 
		sql = " select SHOPID,categoryno,categoryname,order_categoryno,order_categoryname from OC_mappingcategory a  "
			+ " where a.EID=? and a.LOAD_DOCTYPE = ?  and a.SHOPID in (" +shops + ") " ;
		List<Map<String, Object>> getMappingCategoryQData = this.doQueryData(sql, mappingCategory_ConditionValues);	
    

		for (level1shopsElm shoppar :shopsDatas )
		{
			String shopId=shoppar.getErpShopNO() ;
			String orderShopNO=shoppar.getOrderShopNO();
			String orderShopName=shoppar.getOrderShopName();			
      if(shopId.trim().length()==0)//可能有没做映射的门店，门店编号=' '
      {
      	continue;
      }
			
			for (level1goodsElm goodspar :goodsDatas	)
			{
				if(trans_Type.equals("4"))
				{
					if(goodspar.getStatus().equals("0"))
					{
						continue;
					}
				}
				
				String trans_ID=UUID.randomUUID().toString();
				//新增OC_TRANSTASK
				String[] transTask_columns = {
						"EID","SHOPID","TRANS_ID","LOAD_DOCTYPE","TRANS_TYPE","ORDER_SHOP","ORDER_SHOPNAME",
						"CREATEBYNO","CREATEBYNAME","TRANS_DATE","TRANS_TIME","TRANS_FLG","STATUS" 
				};		
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 					
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(trans_ID, Types.VARCHAR),
						new DataValue(load_DocType, Types.VARCHAR),					
						new DataValue(trans_Type, Types.VARCHAR),
						new DataValue(orderShopNO,Types.VARCHAR),
						new DataValue(orderShopName,Types.VARCHAR),
						new DataValue(req.getOpNO(), Types.VARCHAR),
						new DataValue(req.getOpName(), Types.VARCHAR),					
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("100", Types.VARCHAR)
				};
				InsBean transTask_ib = new InsBean("OC_TRANSTASK", transTask_columns);
				transTask_ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(transTask_ib)); 				

				String pluNO= goodspar.getPluNO();				

				//插入OC_TRANSTASK_GOODS
				Map<String, Boolean> condition_pluno = new HashMap<String, Boolean>(); //查询条件
				condition_pluno.put("PLUNO", true);
				//调用过滤函数
				List<Map<String, Object>> getTVGoodsQData  =MapDistinct.getMap(getTvQData, condition_pluno);
				for (Map<String, Object> oneTVGoodsQData : getTVGoodsQData) 
				{ 
					String QOC_pluNO = oneTVGoodsQData.get("PLUNO").toString();
					if (pluNO.equals(QOC_pluNO))
					{
						String QOC_categotyNO = oneTVGoodsQData.get("CATEGORYNO").toString();
						String QOC_pluName = oneTVGoodsQData.get("PLUNAME").toString();
						String QOC_description =oneTVGoodsQData.get("DESCRIPTION").toString(); 
						String QOC_fileName =oneTVGoodsQData.get("FILENAME").toString(); 
						String QOC_elmHash = oneTVGoodsQData.get("ELMHASH").toString(); 
						String QOC_jbpHash = ""; 
						try 
						{
							QOC_jbpHash = oneTVGoodsQData.get("JBPHASH").toString(); 
				
			      } 
						catch (Exception e) 
						{
							QOC_jbpHash = ""; 
				
			      }
						String QOC_unit = oneTVGoodsQData.get("UNIT").toString(); 
						String QOC_priority = oneTVGoodsQData.get("PRIORITY").toString(); 
						String QOC_material1 = oneTVGoodsQData.get("MATERIAL1").toString(); 
						String QOC_material2 = oneTVGoodsQData.get("MATERIAL2").toString(); 
						String QOC_material3 = oneTVGoodsQData.get("MATERIAL3").toString(); 
						String QOC_material4 = oneTVGoodsQData.get("MATERIAL4").toString(); 
						String QOC_material5 = oneTVGoodsQData.get("MATERIAL5").toString(); 
						String QOC_material6 = oneTVGoodsQData.get("MATERIAL6").toString(); 
						String QOC_material7 = oneTVGoodsQData.get("MATERIAL7").toString(); 
						String QOC_material8 = oneTVGoodsQData.get("MATERIAL8").toString(); 
						String QOC_material9 = oneTVGoodsQData.get("MATERIAL9").toString(); 
						String QOC_material10 = oneTVGoodsQData.get("MATERIAL10").toString(); 
						String QOC_materialID1 = oneTVGoodsQData.get("MATERIALID1").toString(); 
						String QOC_materialID2 = oneTVGoodsQData.get("MATERIALID2").toString(); 
						String QOC_materialID3 = oneTVGoodsQData.get("MATERIALID3").toString(); 
						String QOC_materialID4 = oneTVGoodsQData.get("MATERIALID4").toString(); 
						String QOC_materialID5 = oneTVGoodsQData.get("MATERIALID5").toString(); 
						String QOC_materialID6 = oneTVGoodsQData.get("MATERIALID6").toString(); 
						String QOC_materialID7 = oneTVGoodsQData.get("MATERIALID7").toString(); 
						String QOC_materialID8 = oneTVGoodsQData.get("MATERIALID8").toString(); 
						String QOC_materialID9 = oneTVGoodsQData.get("MATERIALID9").toString(); 
						String QOC_materialID10 = oneTVGoodsQData.get("MATERIALID10").toString(); 					
						String QOC_isAlltimeSell = oneTVGoodsQData.get("ISALLTIMESELL").toString(); 
						String QOC_beginDate = oneTVGoodsQData.get("BEGINDATE").toString(); 
						String QOC_endDate = oneTVGoodsQData.get("ENDDATE").toString(); 
						String QOC_sellWeek = oneTVGoodsQData.get("SELLWEEK").toString(); 
						String QOC_sellTime = oneTVGoodsQData.get("SELLTIME").toString(); 

						String QMapping_orderPluNO="";
						String QMapping_orderPluName="";
						String QMapping_orderCategoryNO="";
						String QMapping_orderCategoryName="";
						//查MAPPINGGOODS 
						condition_pluno.clear();
						condition_pluno.put("SHOPID", true);
						condition_pluno.put("PLUNO", true);
						//调用过滤函数
						List<Map<String, Object>> getMappingGoodsQData  =MapDistinct.getMap(getMappingQData, condition_pluno);
						for (Map<String, Object> oneMappingGoodsQData : getMappingGoodsQData) 
						{ 
							String QMapping_shopNO=oneMappingGoodsQData.get("SHOPID").toString(); 		
							String QMapping_pluNO=oneMappingGoodsQData.get("PLUNO").toString(); 		
							if (shopId.equals(QMapping_shopNO) && pluNO.equals(QMapping_pluNO))
							{
								QMapping_orderPluNO=oneMappingGoodsQData.get("ORDER_PLUNO").toString(); 		
								QMapping_orderPluName=oneMappingGoodsQData.get("ORDER_PLUNAME").toString(); 	
								QMapping_orderCategoryNO=oneMappingGoodsQData.get("ORDER_CATEGORYNO").toString(); 
								QMapping_orderCategoryName=oneMappingGoodsQData.get("ORDER_CATEGORYNAME").toString(); 	
								break;
							}
						}
						
						//查找OC_mappingcategory
						if (trans_Type.equals("4")||trans_Type.equals("5")) //新增和修改都要查，修改会变更分类
						{
							for (Map<String, Object> oneMappingCategoryQData : getMappingCategoryQData) 
							{ 
								String QMappingCategory_shopNO=oneMappingCategoryQData.get("SHOPID").toString(); 		
								String QMappingCategory_categoryNO=oneMappingCategoryQData.get("CATEGORYNO").toString(); 					
								if (shopId.equals(QMappingCategory_shopNO) && QOC_categotyNO.equals(QMappingCategory_categoryNO))
								{
									String QMappingCategory_orderCategoryNO=oneMappingCategoryQData.get("ORDER_CATEGORYNO").toString(); 
								  String QMappingCategory_orderCategoryName=oneMappingCategoryQData.get("ORDER_CATEGORYNAME").toString(); 
									QMapping_orderCategoryNO=QMappingCategory_orderCategoryNO;
									QMapping_orderCategoryName=QMappingCategory_orderCategoryName;	
									break;
								}
							}
						}
						
						
						//新增OC_TRANSTASK_GOODS
						String[] transTaskGoods_columns = {						
								"EID","SHOPID","TRANS_ID","PLUNO","PLUNAME","ORDER_PLUNO","ORDER_PLUNAME","CATEGORYNO",	
								"ORDER_CATEGORYNO","ORDER_CATEGORYNAME","DESCRIPTION","FILENAME","ELMHASH","JBPHASH","UNIT",	
								"PRIORITY","MATERIAL1","MATERIAL2","MATERIAL3","MATERIAL4","MATERIAL5","MATERIAL6","MATERIAL7",
								"MATERIAL8","MATERIAL9","MATERIAL10","MATERIALID1","MATERIALID2","MATERIALID3","MATERIALID4",
								"MATERIALID5","MATERIALID6","MATERIALID7","MATERIALID8","MATERIALID9","MATERIALID10",
								"ISALLTIMESELL","BEGINDATE","ENDDATE","SELLWEEK","SELLTIME","STATUS"			};				
						insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR), 
								new DataValue(trans_ID, Types.VARCHAR),
								new DataValue(QOC_pluNO, Types.VARCHAR),								
								new DataValue(QOC_pluName, Types.VARCHAR),  
								new DataValue(QMapping_orderPluNO, Types.VARCHAR),  
								new DataValue(QMapping_orderPluName, Types.VARCHAR),
								new DataValue(QOC_categotyNO, Types.VARCHAR),  
								new DataValue(QMapping_orderCategoryNO, Types.VARCHAR),  
								new DataValue(QMapping_orderCategoryName, Types.VARCHAR),
								new DataValue(QOC_description, Types.VARCHAR),  
								new DataValue(QOC_fileName, Types.VARCHAR),  
								new DataValue(QOC_elmHash, Types.VARCHAR),
								new DataValue(QOC_jbpHash, Types.VARCHAR),
								new DataValue(QOC_unit, Types.VARCHAR),  
								new DataValue(Integer.valueOf(QOC_priority), Types.INTEGER),  
								new DataValue(QOC_material1, Types.VARCHAR),
								new DataValue(QOC_material2, Types.VARCHAR),
								new DataValue(QOC_material3, Types.VARCHAR),
								new DataValue(QOC_material4, Types.VARCHAR),
								new DataValue(QOC_material5, Types.VARCHAR),
								new DataValue(QOC_material6, Types.VARCHAR),
								new DataValue(QOC_material7, Types.VARCHAR),
								new DataValue(QOC_material8, Types.VARCHAR),
								new DataValue(QOC_material9, Types.VARCHAR),
								new DataValue(QOC_material10, Types.VARCHAR),
								new DataValue(QOC_materialID1, Types.VARCHAR),
								new DataValue(QOC_materialID2, Types.VARCHAR),
								new DataValue(QOC_materialID3, Types.VARCHAR),
								new DataValue(QOC_materialID4, Types.VARCHAR),
								new DataValue(QOC_materialID5, Types.VARCHAR),
								new DataValue(QOC_materialID6, Types.VARCHAR),
								new DataValue(QOC_materialID7, Types.VARCHAR),
								new DataValue(QOC_materialID8, Types.VARCHAR),
								new DataValue(QOC_materialID9, Types.VARCHAR),
								new DataValue(QOC_materialID10, Types.VARCHAR),								
								new DataValue(QOC_isAlltimeSell, Types.VARCHAR),
								new DataValue(QOC_beginDate, Types.VARCHAR),
								new DataValue(QOC_endDate, Types.VARCHAR),
								new DataValue(QOC_sellWeek, Types.VARCHAR),
								new DataValue(QOC_sellTime, Types.VARCHAR),
								new DataValue("100", Types.VARCHAR)
						};		
						InsBean transTaskGoods_ib = new InsBean("OC_TRANSTASK_GOODS", transTaskGoods_columns);
						transTaskGoods_ib.addValues(insValue);
						this.addProcessData(new DataProcessBean(transTaskGoods_ib)); 		
						break;
					}
				}

				//插入OC_TRANSTASK_GOODS_SPEC
				Map<String, Boolean> condition_spec = new HashMap<String, Boolean>(); //查询条件
				condition_spec.put("PLUNO", true);
				condition_spec.put("SPECNO", true);
				//调用过滤函数
				List<Map<String, Object>> getTVGoodsSpecQData  =MapDistinct.getMap(getTvQData, condition_spec);
				for (Map<String, Object> oneTVGoodsSpecQData : getTVGoodsSpecQData) 
				{ 
					String QTVspec_pluNO = oneTVGoodsSpecQData.get("PLUNO").toString();
					if (pluNO.equals(QTVspec_pluNO))
					{
						String QTVspec_specNO = oneTVGoodsSpecQData.get("SPECNO").toString();
						String QTVspec_specName = oneTVGoodsSpecQData.get("SPECNAME").toString();	
						//价格四舍五入
						BigDecimal  QTVspec_price_B = new BigDecimal(oneTVGoodsSpecQData.get("PRICE").toString());
						QTVspec_price_B=QTVspec_price_B.setScale(2, BigDecimal.ROUND_HALF_UP);
						String QTVspec_price =QTVspec_price_B.toString();
						
				  	//库存
						BigDecimal  QTVspec_stockQty_B = new BigDecimal(oneTVGoodsSpecQData.get("STOCKQTY").toString());
						QTVspec_stockQty_B=QTVspec_stockQty_B.setScale(0, BigDecimal.ROUND_HALF_UP);
						String QTVspec_stockQty =QTVspec_stockQty_B.toString();
						
						
				  	//包装费四舍五入
						String packageFee_Str = oneTVGoodsSpecQData.get("PACKAGEFEE").toString();
						if(packageFee_Str==null||packageFee_Str.isEmpty())
						{
							packageFee_Str = "0";
						}
						BigDecimal  QTVspec_packageFee_B = new BigDecimal(packageFee_Str);
						QTVspec_packageFee_B=QTVspec_packageFee_B.setScale(2, BigDecimal.ROUND_HALF_UP);
						String QTVspec_packageFee =QTVspec_packageFee_B.toString();
						
						String QTVspec_isOnshelf = oneTVGoodsSpecQData.get("ISONSHELF").toString();
					  //净重
						String QTVspec_netWeight = oneTVGoodsSpecQData.get("NETWEIGHT").toString();

						String QMappingSpec_orderSpecNO="";
						String QMappingSpec_orderSpecName="";
						//查MAPPINGGOODS 
						condition_spec.clear();
						condition_spec.put("SHOPID", true);
						condition_spec.put("PLUNO", true);
						condition_spec.put("SPECNO", true);
						//调用过滤函数
						List<Map<String, Object>> getMappingGoodsSpecQData  =MapDistinct.getMap(getMappingQData, condition_spec);
						for (Map<String, Object> oneMappingGoodsSpecQData : getMappingGoodsSpecQData) 
						{ 
							String QMappingSpec_shopNO=oneMappingGoodsSpecQData.get("SHOPID").toString(); 		
							String QMappingSpec_pluNO=oneMappingGoodsSpecQData.get("PLUNO").toString(); 
							String QMappingSpec_specNO=oneMappingGoodsSpecQData.get("SPECNO").toString(); 
							if (shopId.equals(QMappingSpec_shopNO) && pluNO.equals(QMappingSpec_pluNO) && QTVspec_specNO.equals(QMappingSpec_specNO) )
							{
								QMappingSpec_orderSpecNO=oneMappingGoodsSpecQData.get("ORDER_SPECNO").toString(); 		
								QMappingSpec_orderSpecName=oneMappingGoodsSpecQData.get("ORDER_SPECNAME").toString(); 		
								break;
							}
						}

						//新增OC_TRANSTASK_GOODS_SPEC
						String[] transTaskGoodsSpec_columns = {						
								"EID","SHOPID","TRANS_ID","PLUNO","SPECNO","SPECNAME","ORDER_SPECNO","ORDER_SPECNAME","PRICE",
								"STOCKQTY","PACKAGEFEE","ISONSHELF","NETWEIGHT","STATUS"
						};							
						insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR), 
								new DataValue(trans_ID, Types.VARCHAR),
								new DataValue(pluNO, Types.VARCHAR),								
								new DataValue(QTVspec_specNO, Types.VARCHAR),  
								new DataValue(QTVspec_specName, Types.VARCHAR),  
								new DataValue(QMappingSpec_orderSpecNO, Types.VARCHAR),
								new DataValue(QMappingSpec_orderSpecName, Types.VARCHAR),  
								new DataValue(QTVspec_price, Types.VARCHAR),  
								new DataValue(QTVspec_stockQty, Types.VARCHAR),
								new DataValue(QTVspec_packageFee, Types.VARCHAR),  
								new DataValue(QTVspec_isOnshelf, Types.VARCHAR),  
								new DataValue(QTVspec_netWeight, Types.VARCHAR),
								new DataValue("100", Types.VARCHAR)
						};		
						InsBean transTaskGoodsSpec_ib = new InsBean("OC_TRANSTASK_GOODS_SPEC", transTaskGoodsSpec_columns);
						transTaskGoodsSpec_ib.addValues(insValue);
						this.addProcessData(new DataProcessBean(transTaskGoodsSpec_ib)); 	
					}
				}

				//插入OC_TRANSTASK_GOODS_ATTR
				Map<String, Boolean> condition_attr = new HashMap<String, Boolean>(); //查询条件
				condition_attr.put("PLUNO", true);
				condition_attr.put("ATTRNAME", true);
				condition_attr.put("ATTRVALUE", true);
				//调用过滤函数
				List<Map<String, Object>> getTVGoodsAttrQData  =MapDistinct.getMap(getTvQData, condition_attr);
				for (Map<String, Object> oneTVGoodsAttrQData : getTVGoodsAttrQData) 
				{ 
					String QTVattr_pluNO = oneTVGoodsAttrQData.get("PLUNO").toString();
					String QTVattr_attrName = oneTVGoodsAttrQData.get("ATTRNAME").toString();
					String QTVattr_attrValue = oneTVGoodsAttrQData.get("ATTRVALUE").toString();
					if (pluNO.equals(QTVattr_pluNO)&&!Check.Null(QTVattr_attrName)&&!Check.Null(QTVattr_attrValue)  )
					{
						String QMappingAttr_orderAttrName="";
						String QMappingAttr_orderAttrValue="";						
						//查MAPPINGGOODS 
						condition_attr.clear();
						condition_attr.put("SHOPID", true);
						condition_attr.put("PLUNO", true);
						condition_attr.put("ATTRNAME", true);
						condition_attr.put("ATTRVALUE", true);
						//调用过滤函数
						List<Map<String, Object>> getMappingGoodsAttrQData  =MapDistinct.getMap(getMappingQData, condition_attr);
						for (Map<String, Object> oneMappingGoodsAttrQData : getMappingGoodsAttrQData) 
						{ 
							String QMappingAttr_shopNO=oneMappingGoodsAttrQData.get("SHOPID").toString(); 		
							String QMappingAttr_pluNO=oneMappingGoodsAttrQData.get("PLUNO").toString(); 
							String QMappingAttr_attrName=oneMappingGoodsAttrQData.get("ATTRNAME").toString();
							String QMappingAttr_attrValue=oneMappingGoodsAttrQData.get("ATTRVALUE").toString(); 
							if (shopId.equals(QMappingAttr_shopNO) && pluNO.equals(QMappingAttr_pluNO) && QTVattr_attrName.equals(QMappingAttr_attrName)&&QTVattr_attrValue.equals(QMappingAttr_attrValue) )
							{
								QMappingAttr_orderAttrName=oneMappingGoodsAttrQData.get("ORDER_ATTRNAME").toString(); 		
								QMappingAttr_orderAttrValue=oneMappingGoodsAttrQData.get("ORDER_ATTRVALUE").toString(); 		
								break;
							}
						}

						//新增OC_TRANSTASK_GOODS_ATTR
						String[] transTaskGoodsAttr_columns = {						
								"EID","SHOPID","TRANS_ID","PLUNO","ATTRNAME","ATTRVALUE","ORDER_ATTRNAME","ORDER_ATTRVALUE","STATUS"
						};	
						insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR), 
								new DataValue(trans_ID, Types.VARCHAR),
								new DataValue(pluNO, Types.VARCHAR),								
								new DataValue(QTVattr_attrName, Types.VARCHAR),  
								new DataValue(QTVattr_attrValue, Types.VARCHAR),  
								new DataValue(QMappingAttr_orderAttrName, Types.VARCHAR),
								new DataValue(QMappingAttr_orderAttrValue, Types.VARCHAR),  
								new DataValue("100", Types.VARCHAR)
						};		
						InsBean transTaskGoodsAttr_ib = new InsBean("OC_TRANSTASK_GOODS_ATTR", transTaskGoodsAttr_columns);
						transTaskGoodsAttr_ib.addValues(insValue);
						this.addProcessData(new DataProcessBean(transTaskGoodsAttr_ib)); 	
					}
				}
			}
		}
	}

	private String toFormatShops(DCP_OrderGoodsSyncReq req)throws Exception {
		String shops="";
		List<level1shopsElm> shopsData = req.getShopsdatas();		
		for (level1shopsElm par :shopsData )
		{
			if(par.getErpShopNO().trim().length()>0)
			{
				shops="'" + par.getErpShopNO() + "'," + shops;	
			}
					
		}
		shops=shops.substring(0, shops.length()-1);
		return shops;
	}
	private String toFormatGoods(DCP_OrderGoodsSyncReq req)throws Exception {
		String goods="";
		List<level1goodsElm> goodsData = req.getGoodsdatas();
		for (level1goodsElm par :goodsData )
		{
			goods="'" + par.getPluNO() + "'," + goods;			
		}
		goods=goods.substring(0, goods.length()-1);
		return goods;
	}


}
