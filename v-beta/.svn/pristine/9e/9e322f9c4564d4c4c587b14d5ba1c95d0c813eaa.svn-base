package com.dsc.spos.service.imp.json;

import java.sql.SQLSyntaxErrorException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformCategoryQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformCategoryQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WMELMProductService;
import com.dsc.spos.waimai.WMJBPProductService;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.dsc.spos.waimai.jddj.OShopCategory;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.product.OCategory;

public class DCP_OrderPlatformCategoryQuery extends SPosAdvanceService<DCP_OrderPlatformCategoryQueryReq,DCP_OrderPlatformCategoryQueryRes> {

	static String goodsLogFileName = "CategorysSaveLocal";
	@Override
	protected void processDUID(DCP_OrderPlatformCategoryQueryReq req, DCP_OrderPlatformCategoryQueryRes res) throws Exception {
		// TODO Auto-generated method stub

		String isOnline = req.getIsOnline();
		if(isOnline.equals("N"))//取本地
		{	
			this.GetCategoryFromDB(req,res);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
		}
		else
		{
			this.GetOnlineCategorys(req);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");

		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformCategoryQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(Check.Null(req.getLoadDocType()))
		{
			errCt++;
			errMsg.append("平台类型LoadDocType不可为空值, ");
			isFail = true;
		}

		if(Check.Null(req.getIsOnline()))
		{
			errCt++;
			errMsg.append("请求类型IsOnline不可为空值, ");
			isFail = true;
		}

		if(req.getErpShopNO()==null||req.getErpShopNO().length ==0)
		{
			errCt++;
			errMsg.append("门店编码不能为空, ");
			isFail = true;
		}




		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderPlatformCategoryQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderPlatformCategoryQueryReq>(){};
	}

	@Override
	protected DCP_OrderPlatformCategoryQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderPlatformCategoryQueryRes();
	}

	private void GetOnlineCategorys(DCP_OrderPlatformCategoryQueryReq req) throws Exception
	{
		String loadDocType = req.getLoadDocType();
		String eId = req.geteId();
		List<Map<String, Object>> mappingShops = this.GetMappingShops(req);
		if (mappingShops == null || mappingShops.isEmpty())
		{
			return;
		}
		
		DCP_OrderPlatformCategoryQueryRes baseRes = new DCP_OrderPlatformCategoryQueryRes();
		if(loadDocType.equals("3"))//京东到家分类 是全局的
		{
			baseRes.setDatas(new ArrayList<DCP_OrderPlatformCategoryQueryRes.level1Elm>());
			StringBuilder errorMessage = new StringBuilder();			
			HelpTools.writelog_fileName("【同步分类资料到本地】开始(JDDJ分类所有门店一样)! 平台类型LoadDocType:"+loadDocType, goodsLogFileName);
		  List<OShopCategory> jddjCategoryList = HelpJDDJHttpUtil.queryCategoriesByOrgCode(errorMessage);
		  if(jddjCategoryList==null||jddjCategoryList.size()==0)
			{
				HelpTools.writelog_fileName("【同步分类资料到本地】获取【京东到家】分类资料结束 没有分类资料！ 平台类型LoadDocType:"+loadDocType, goodsLogFileName);
				return;
			}
		  for (OShopCategory oShopCategory : jddjCategoryList) 
		  {
		  	//目前只有一级分类
		  	if(oShopCategory.getShopCategoryLevel()==1)
		  	{		
		  		DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 = baseRes.new level1Elm();
					String categoryNO = " ";//主键 为空格
					String categoryName = "";
					String orderCategoryNO = String.valueOf(oShopCategory.getId());
					String orderCategoryName = oShopCategory.getShopCategoryName();
					String orderDescription = "";
					String orderPriority = String.valueOf(oShopCategory.getSort());
					oneLv1.setOrderShopNO(" ");
					oneLv1.setCategoryNO(categoryNO);
					oneLv1.setCategoryName(categoryName);
					oneLv1.setOrderCategoryNO(orderCategoryNO);
					oneLv1.setOrderCategoryName(orderCategoryName);
					oneLv1.setOrderDescription(orderDescription);
					oneLv1.setOrderPriority(orderPriority);

					baseRes.getDatas().add(oneLv1);		
		  		
		  	}
		
		  }
		  
		  
		}
		
		for (Map<String, Object> mapShop : mappingShops) 
		{
			DCP_OrderPlatformCategoryQueryRes res = new DCP_OrderPlatformCategoryQueryRes();
			res.setDatas(new ArrayList<DCP_OrderPlatformCategoryQueryRes.level1Elm>());
			String erpShopNO = mapShop.get("SHOPID").toString();
			String orderShopNO = mapShop.get("ORDERSHOPNO").toString();
			HelpTools.writelog_fileName("【同步分类资料到本地】获取当前门店分类资料开始！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
			try 
			{
				if(loadDocType.equals("1"))
				{
					Map<String, Object> map = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, erpShopNO, loadDocType,"");
					Boolean isGoNewFunction = false;//是否走新的接口
					String elmAPPKey = "";
					String elmAPPSecret = "";
					String elmAPPName = "";			
					boolean elmIsSandbox = false;
					if (map != null)
					{
						elmAPPKey = map.get("APPKEY").toString();
						elmAPPSecret = map.get("APPSECRET").toString();
						elmAPPName = map.get("APPNAME").toString();
						String	elmIsTest = map.get("ISTEST").toString();					
						if (elmIsTest != null && elmIsTest.equals("Y"))
						{
							elmIsSandbox = true;
						}
						isGoNewFunction = true;
					}
					long shopId =	Long.parseLong(orderShopNO);
					StringBuilder errorMessage = new StringBuilder("");
					List<OCategory> categoryList = null;
					if(isGoNewFunction)
					{
						categoryList = WMELMProductService.getShopCategories(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,shopId, errorMessage);
					}
					else
					{
						categoryList = WMELMProductService.getShopCategories(shopId, errorMessage);
					}

					if (categoryList == null || categoryList.size() == 0)
					{
						HelpTools.writelog_fileName("【同步分类资料到本地】获取该门店分类资料完成！该门店没有商品分类资料！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
						continue;
					}					
					HelpTools.writelog_fileName("【同步分类资料到本地】获取该门店分类资料完成！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的分类总个数："+categoryList.size(), goodsLogFileName);	

					for (OCategory oCategory : categoryList) 
					{
						DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();
						String categoryNO = " ";//主键 为空格
						String categoryName = "";
						String orderCategoryNO = String.valueOf(oCategory.getId());
						String orderCategoryName = oCategory.getName();
						String orderDescription = oCategory.getDescription();
						String orderPriority = "0";
						int orderIsValid = oCategory.getIsValid();

						if (orderIsValid==1)
						{
							oneLv1.setOrderShopNO(orderShopNO);
							oneLv1.setCategoryNO(categoryNO);
							oneLv1.setCategoryName(categoryName);
							oneLv1.setOrderCategoryNO(orderCategoryNO);
							oneLv1.setOrderCategoryName(orderCategoryName);
							oneLv1.setOrderDescription(orderDescription);
							oneLv1.setOrderPriority(orderPriority);

							res.getDatas().add(oneLv1);		
						}

					}



				}
				else if(loadDocType.equals("2"))
				{
					StringBuilder errorMessage = new StringBuilder("");
					String	resultJson = WMJBPProductService.queryCatList(eId, erpShopNO, errorMessage);
					if(resultJson == null ||resultJson.isEmpty()||resultJson.length()==0)
					{					
						HelpTools.writelog_fileName("【同步分类资料到本地】获取该门店商品资料完成！该门店没有商品资料！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
						continue;					
					}
					HelpTools.writelog_fileName("【同步分类资料到本地】获取该门店商品资料完成！该门店没有商品资料！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 返回的Respone:\n"+resultJson, goodsLogFileName);
					JSONObject jsonObject = new JSONObject(resultJson);			   
					try 
					{
						JSONArray array = jsonObject.getJSONArray("data");
						if(array !=null && array.length()>0)
						{	  	    	
							for (int i = 0; i < array.length(); i++)
							{
								DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();

								try 
								{						
									JSONObject item = array.getJSONObject(i);

									String categoryNO = " ";//主键 为空格
									String categoryName = "";
									String orderCategoryNO = item.get("name").toString();//美团只有分类名称，没有ID
									String orderCategoryName = orderCategoryNO;
									String orderDescription = "";
									String orderPriority = item.get("sequence").toString();

									oneLv1.setOrderShopNO(orderShopNO);
									oneLv1.setCategoryNO(categoryNO);
									oneLv1.setCategoryName(categoryName);
									oneLv1.setOrderCategoryNO(orderCategoryNO);
									oneLv1.setOrderCategoryName(orderCategoryName);
									oneLv1.setOrderDescription(orderDescription);
									oneLv1.setOrderPriority(orderPriority);

									res.getDatas().add(oneLv1);

								} 
								catch (Exception e) 
								{														
									continue;						
								}								

							}

						}
						else
						{ 	    	
							HelpTools.writelog_fileName("【同步分类资料到本地】获取该门店分类完成！【美团聚宝盆】没有data节点解析不了！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
							continue;
						}

					} 
					catch (Exception e) 
					{
						HelpTools.writelog_fileName("【同步分类资料到本地】获取该门店分类完成！【美团聚宝盆】解析返回的内容异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
						continue;
					}		
				}
					
				else //京东到家
				{
          //深拷贝	只需要改写下门店编码，不用调用接口
					for (DCP_OrderPlatformCategoryQueryRes.level1Elm oneData : baseRes.getDatas()) 
					{
						DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();
						String categoryNO = " ";//主键 为空格
						String categoryName = "";
						String orderCategoryNO = oneData.getOrderCategoryNO();
						String orderCategoryName = oneData.getOrderCategoryName();
						String orderDescription = oneData.getOrderDescription();
						String orderPriority = oneData.getOrderPriority();
						
						oneLv1.setOrderShopNO(orderShopNO);
						oneLv1.setCategoryNO(categoryNO);
						oneLv1.setCategoryName(categoryName);
						oneLv1.setOrderCategoryNO(orderCategoryNO);
						oneLv1.setOrderCategoryName(orderCategoryName);
						oneLv1.setOrderDescription(orderDescription);
						oneLv1.setOrderPriority(orderPriority);

						res.getDatas().add(oneLv1);		
						

			    }


				}

			}
			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【同步分类资料到本地】循环门店开始！异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);
				continue;
			}


			//region 开始保存数据库
			this.SaveOnlineCategory(req, res, erpShopNO, orderShopNO);
			//endregion
		}



	}

	private List<Map<String, Object>> GetMappingShops(DCP_OrderPlatformCategoryQueryReq req) throws Exception
	{
		String loadDocType = req.getLoadDocType();
		String[] erpShopNO = req.getErpShopNO();
		String erpShopNOStr = this.getString(erpShopNO);

		String sql = " select * from OC_MAPPINGSHOP where LOAD_DOCTYPE='"+loadDocType+"'";		
		sql += " and SHOPID in ("+erpShopNOStr+")";

		List<Map<String, Object>> mappingShops = this.doQueryData(sql, null);
		if (mappingShops != null && mappingShops.isEmpty() == false)
		{
			return mappingShops;
		}

		return null;

	}

	private void SaveOnlineCategory(DCP_OrderPlatformCategoryQueryReq req, DCP_OrderPlatformCategoryQueryRes res,String erpShopNO,String orderShopNO) throws Exception
	{
		String eId = req.geteId();
		String loadDocType = req.getLoadDocType();
		String belFirm = null;
		if(req.getOrg_Form()!=null&&req.getOrg_Form().equals("0"))
		{
			belFirm = req.getOrganizationNO();
		}
		else
		{
			belFirm = req.getBELFIRM();
		}
		String sql="";
		List<DCP_OrderPlatformCategoryQueryRes.level1Elm> categoryList = res.getDatas();
		HelpTools.writelog_fileName("【同步分类资料到本地】开始组装SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 获取的分类总个数："+categoryList.size(), goodsLogFileName);

		//先删 在插入
		String execsql1 = "delete from OC_MAPPINGCATEGORY where EID='"+eId+"' and LOAD_DOCTYPE='"+loadDocType+"' and ORDER_SHOP='"+orderShopNO+"'   ";		  	
		ExecBean exc1 = new ExecBean(execsql1);	  	
		this.addProcessData(new DataProcessBean(exc1));	  	

		//查询分类编号和分类名称
		
		sql =" SELECT categoryno,categoryname FROM OC_category where EID='"+eId+"' ";
		if (belFirm != null && belFirm.trim().length() > 0)
		{
			sql +="  AND Belfirm='"+belFirm+"'";
		}
		List<Map<String, Object>> getQDataCategory = this.doQueryData(sql, null);
		
		for (DCP_OrderPlatformCategoryQueryRes.level1Elm oneData1 : categoryList) 
		{
			String orderCategoryNO = oneData1.getOrderCategoryNO();
			String orderCategoryName = oneData1.getOrderCategoryName() == null ? "" : oneData1.getOrderCategoryName();
			try 
			{

				String orderDescription = oneData1.getOrderDescription()== null ? "" : oneData1.getOrderDescription();
				String categoryNO = oneData1.getCategoryNO()== null ? " " : oneData1.getCategoryNO();
				String categoryName = oneData1.getCategoryName()== null ? "" : oneData1.getCategoryName();	
				
				for (Map<String, Object> oneQDataCategory: getQDataCategory) 
				{					
					String q_CategoryName = oneQDataCategory.get("CATEGORYNAME").toString();
	        if (orderCategoryName.equals(q_CategoryName))
	        {
	        	categoryNO =  oneQDataCategory.get("CATEGORYNO").toString();
	        	categoryName = q_CategoryName;
	        	break;
	        }
				}
				
				
				if(categoryNO.length()==0)
				{
					categoryNO = " ";//主键
				}
				String priority = oneData1.getOrderPriority()== null ? "" : oneData1.getOrderPriority();	

				if(priority.length()==0)
				{
					priority = "0";//主键
				}

				String[] columns1 = { "EID", "LOAD_DOCTYPE", "SHOPID", "ORGANIZATIONNO", "CATEGORYNO", "CATEGORYNAME","ORDER_SHOP","ORDER_CATEGORYNO",
						"ORDER_CATEGORYNAME", "PRIORITY", "STATUS" };
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.
						new DataValue(erpShopNO, Types.VARCHAR),//组织编号=门店编号
						new DataValue(erpShopNO, Types.VARCHAR),//ERP门店				
						new DataValue(categoryNO, Types.VARCHAR),//ERP商品编码
						new DataValue(categoryName, Types.VARCHAR),//ERP商品名称
						new DataValue(orderShopNO, Types.VARCHAR),//ERP商品分类编码
						new DataValue(orderCategoryNO, Types.VARCHAR),//ERP商品分类编码
						new DataValue(orderCategoryName, Types.VARCHAR),//外卖平台门店ID				
						new DataValue(priority, Types.VARCHAR),//	
						new DataValue("100", Types.VARCHAR)	
				};

				InsBean ib1 = new InsBean("OC_MAPPINGCATEGORY", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));	

			} 

			catch (Exception e) 
			{
				HelpTools.writelog_fileName("【同步分类资料到本地】开始组装SQL语句！有异常:"+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO+" 异常的平台分类ID/名称："+orderCategoryNO+"/"+orderCategoryName, goodsLogFileName);
				continue;		
			}


		}

		//可以一个门店 一个事务
		try 
		{
			HelpTools.writelog_fileName("【同步分类资料到本地】开始执行SQL语句！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);  
			this.doExecuteDataToDB();
			HelpTools.writelog_fileName("【同步分类资料到本地】开始执行SQL语句！成功！ 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName);  

		} 
		catch (SQLSyntaxErrorException e)
		{
			HelpTools.writelog_fileName("【同步分类资料到本地】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName); 		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【同步分类资料到本地】开始执行SQL语句！异常："+e.getMessage()+" 平台类型LoadDocType:"+loadDocType+" 平台门店ID："+orderShopNO+" 门店shopId:"+erpShopNO, goodsLogFileName); 		
		}
		this.pData.clear();


	}


	private void GetCategoryFromDB(DCP_OrderPlatformCategoryQueryReq req, DCP_OrderPlatformCategoryQueryRes res) throws Exception
	{
		String loadDocType = req.getLoadDocType();
		String erpShopNO = req.getErpShopNO()[0];
		String eId = req.geteId();
		res.setDatas(new ArrayList<DCP_OrderPlatformCategoryQueryRes.level1Elm>());
		String	sql = " select * from OC_MAPPINGCATEGORY ";
		sql += " where A.EID='"+eId+"' and A.LOAD_DOCTYPE='"+loadDocType+"' and A.SHOPID='"+erpShopNO+"'";
		
		
		
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			for (Map<String, Object> map : getQDataDetail) 
			{
				DCP_OrderPlatformCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String categoryNO = map.get("CATEGORYNO").toString();//主键 为空格
				String categoryName = map.get("CATEGORYNAME").toString();;
				String orderCategoryNO = map.get("ORDER_CATEGORYNO").toString();
				String orderCategoryName = map.get("ORDER_CATEGORYNAME").toString();
				String orderDescription = "";
				String orderPriority = map.get("PRIORITY").toString();;

				oneLv1.setCategoryNO(categoryNO);
				oneLv1.setCategoryName(categoryName);
				oneLv1.setOrderCategoryNO(orderCategoryNO);
				oneLv1.setOrderCategoryName(orderCategoryName);
				oneLv1.setOrderDescription(orderDescription);
				oneLv1.setOrderPriority(orderPriority);

				res.getDatas().add(oneLv1);		

			}

		}
	}

	protected String getString(String[] str)
	{
		String str2 = "";

		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		//System.out.println(str2);

		return str2;
	}

}
