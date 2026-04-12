package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsImageUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsImageUpdateReq.levelDetail;
import com.dsc.spos.json.cust.req.DCP_GoodsImageUpdateReq.levelProduct;
import com.dsc.spos.json.cust.req.DCP_GoodsImageUpdateReq.levelSpec;
import com.dsc.spos.json.cust.req.DCP_GoodsImageUpdateReq.levelSymbol;
import com.dsc.spos.json.cust.res.DCP_GoodsImageUpdateRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsImageUpdate extends SPosAdvanceService<DCP_GoodsImageUpdateReq,DCP_GoodsImageUpdateRes>
{

	@Override
	protected void processDUID(DCP_GoodsImageUpdateReq req, DCP_GoodsImageUpdateRes res) throws Exception 
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();
		String appType = req.getRequest().getAppType();
		String listImage=req.getRequest().getListImage();		
		String pluno=req.getRequest().getPluNo();
		String pluType=req.getRequest().getPluType();
		String symboDisplay=req.getRequest().getSymbolDisplay();
		String dPic = req.getRequest().getdPic();

		List<levelDetail> detailImgList=req.getRequest().getDetailImageList();
		List<levelProduct> prodImgList=req.getRequest().getProdImageList();
		List<levelSpec> specImgList=req.getRequest().getSpecImageList();
		List<levelSymbol> symbolImgList=req.getRequest().getSymbolList();		

		String sql = "select PLUNO from DCP_GOODSIMAGE where eid='"+eId+"' and PLUNO='"+pluno+"' and APPTYPE='"+appType+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData!=null &&getData.isEmpty()==false) 
		{
			/*res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此商品图片记录不存在！");
			return;*/

			UptBean ub1 = new UptBean("DCP_GOODSIMAGE");
			ub1.addUpdateValue("PLUTYPE", new DataValue(pluType,Types.VARCHAR));
			ub1.addUpdateValue("LISTIMAGE", new DataValue(listImage,Types.VARCHAR));
			ub1.addUpdateValue("SYMBOLDISPLAY", new DataValue(symboDisplay,Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
			ub1.addUpdateValue("DPIC", new DataValue(dPic, Types.VARCHAR));

			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("APPTYPE", new DataValue(appType, Types.VARCHAR));
			ub1.addCondition("PLUNO", new DataValue(pluno, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
		}
		else
		{
			//
			String[] columns_DCP_GOODSIMAGE = 
				{ 
						"EID","APPTYPE","PLUNO","PLUTYPE",
						"LISTIMAGE","DPIC","SYMBOLDISPLAY","LASTMODITIME"
				};
			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(appType, Types.VARCHAR),
					new DataValue(pluno, Types.VARCHAR),
					new DataValue(pluType, Types.VARCHAR),
					new DataValue(listImage, Types.VARCHAR),
					new DataValue(dPic, Types.VARCHAR),
					new DataValue(symboDisplay, Types.INTEGER),				
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_GOODSIMAGE", columns_DCP_GOODSIMAGE);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增

		}

		//删除之前的,再插入
		DelBean db1 = new DelBean("DCP_GOODSIMAGE_DETAILIMAGE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("APPTYPE", new DataValue(appType,Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(pluno,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	

		db1 = new DelBean("DCP_GOODSIMAGE_PRODIMAGE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("APPTYPE", new DataValue(appType,Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(pluno,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	

		db1 = new DelBean("DCP_GOODSIMAGE_SPECIMAGE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("APPTYPE", new DataValue(appType,Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(pluno,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	

		db1 = new DelBean("DCP_GOODSIMAGE_SYMBOL");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("APPTYPE", new DataValue(appType,Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(pluno,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	

		//
		if (detailImgList!=null && detailImgList.size()>0) 
		{
			String[] columns_DCP_GOODSIMAGE_DETAILIMAGE = 
				{ 
						"EID","APPTYPE","PLUNO","ITEM","TYPE","DETAILIMAGE","CONTENT","LASTMODITIME"
				};

			int detail_item=0;
			for (levelDetail detail : detailImgList) 
			{
				//
				detail_item=detail_item+1;

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(appType, Types.VARCHAR),
						new DataValue(pluno, Types.VARCHAR),
						new DataValue(detail_item, Types.INTEGER),
						new DataValue(detail.getType(), Types.VARCHAR),
						new DataValue(detail.getDetailImage(), Types.VARCHAR),
						new DataValue(detail.getContent(), Types.CLOB),
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_GOODSIMAGE_DETAILIMAGE", columns_DCP_GOODSIMAGE_DETAILIMAGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增
			}
		}	

		//产品图为空时,默认存列表图:prodimage默认存列表图
		String[] columns_DCP_GOODSIMAGE_PRODIMAGE = 
			{ 
					"EID","APPTYPE","PLUNO","ITEM","PRODIMAGE","LASTMODITIME"
			};
		if (prodImgList!=null && prodImgList.size()>0) 
		{
			int detail_item=0;

			for (levelProduct product : prodImgList) 
			{
				//
				detail_item=detail_item+1;

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(appType, Types.VARCHAR),
						new DataValue(pluno, Types.VARCHAR),
						new DataValue(detail_item, Types.VARCHAR),
						new DataValue(product.getProdImage(), Types.VARCHAR),

						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_GOODSIMAGE_PRODIMAGE", columns_DCP_GOODSIMAGE_PRODIMAGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增			
			}			
		}
		else{
			 
			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(appType, Types.VARCHAR),
					new DataValue(pluno, Types.VARCHAR),
					new DataValue(1, Types.VARCHAR),
					new DataValue(listImage, Types.VARCHAR),

					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_GOODSIMAGE_PRODIMAGE", columns_DCP_GOODSIMAGE_PRODIMAGE);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增	
		}

		//
		if (specImgList!=null && specImgList.size()>0) 
		{
			String[] columns_DCP_GOODSIMAGE_SPECIMAGE = 
				{ 
						"EID","APPTYPE","PLUNO","ATTRVALUEID1","SPECIMAGE","ATTRID1","LASTMODITIME"
				};


			for (levelSpec spec : specImgList) 
			{

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(appType, Types.VARCHAR),
						new DataValue(pluno, Types.VARCHAR),
						new DataValue(spec.getAttrValueId1(), Types.VARCHAR),						
						new DataValue(spec.getSpecImage(), Types.VARCHAR),
						new DataValue(spec.getAttrId1(), Types.VARCHAR),

						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_GOODSIMAGE_SPECIMAGE", columns_DCP_GOODSIMAGE_SPECIMAGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增			
			}			
		}


		//
		if (symbolImgList!=null && symbolImgList.size()>0) 
		{
			String[] columns_DCP_GOODSIMAGE_SYMBOL = 
				{ 
						"EID","APPTYPE","PLUNO","ITEM","SYMBOLTYPE","SYMBOLTAG",
						"SYMBOLIMAGE","BEGINDATE","ENDDATE","LASTMODITIME"
				};

			int detail_item=0;

			for (levelSymbol symbol : symbolImgList) 
			{
				//
				detail_item=detail_item+1;

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(appType, Types.VARCHAR),
						new DataValue(pluno, Types.VARCHAR),
						new DataValue(detail_item, Types.INTEGER),
						new DataValue(symbol.getSymbolType(), Types.VARCHAR),
						new DataValue(symbol.getSymbolTag(), Types.VARCHAR),
						new DataValue(symbol.getSymbolImage(), Types.VARCHAR),

						new DataValue(symbol.getBeginDate(), Types.DATE),
						new DataValue(symbol.getEndDate(), Types.DATE),
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_GOODSIMAGE_SYMBOL", columns_DCP_GOODSIMAGE_SYMBOL);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增			
			}			
		}





		//
		this.doExecuteDataToDB();


		//清缓存
		String posUrl = PosPub.getPOS_INNER_URL(eId);
		String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
				" AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
		List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
		String apiUserCode = "";
		String apiUserKey = "";
		if (result != null && result.size() == 2) {
			for (Map<String, Object> map : result) {
				if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
					apiUserCode = map.get("ITEMVALUE").toString();
				} else {
					apiUserKey = map.get("ITEMVALUE").toString();
				}
			}
		}
		PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,eId);


        //同步缓存
        List<Plu_POS_GoodsPriceRedisUpdate> pluList=new ArrayList<>();
        //同步缓存
        Plu_POS_GoodsPriceRedisUpdate plu_pos_goodsPriceRedisUpdate=new Plu_POS_GoodsPriceRedisUpdate();
        plu_pos_goodsPriceRedisUpdate.setPluNo(req.getRequest().getPluNo());
        pluList.add(plu_pos_goodsPriceRedisUpdate);
        //
        PosPub.POS_GoodsPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,pluList);

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsImageUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsImageUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsImageUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsImageUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String appType = req.getRequest().getAppType();
		String listImage=req.getRequest().getListImage();		
		String pluno=req.getRequest().getPluNo();
		String pluType=req.getRequest().getPluType();
		String symboDisplay=req.getRequest().getSymbolDisplay();

		List<levelDetail> detailImgList=req.getRequest().getDetailImageList();
		List<levelProduct> prodImgList=req.getRequest().getProdImageList();
		List<levelSpec> specImgList=req.getRequest().getSpecImageList();
		List<levelSymbol> symbolImgList=req.getRequest().getSymbolList();		

		if(Check.Null(appType))
		{
			errMsg.append("应用类型不能为空值 ");
			isFail = true;
		}
		if(Check.Null(listImage))
		{
			errMsg.append("列表图片不能为空值 ");
			isFail = true;
		}

		if(Check.Null(pluno))
		{
			errMsg.append("商品编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(pluType))
		{
			errMsg.append("商品类型不能为空值 ");
			isFail = true;
		}
		if(Check.Null(symboDisplay))
		{
			errMsg.append("是否显示角标不能为空值 ");
			isFail = true;
		}

		if (symbolImgList!=null && symbolImgList.size()>0) 
		{
			for (levelSymbol symbol : symbolImgList) 
			{				
				String beginDate=symbol.getBeginDate();//生效日期YYYY-MM-DD
				String endDate=symbol.getEndDate();//失效日期YYYY-MM-DD

				if (beginDate==null || beginDate.length()!=10) 
				{
					errMsg.append("图片角标生效日期格式必须为 YYYY-MM-DD");
					isFail = true;
				}

				if (endDate==null || endDate.length()!=10) 
				{
					errMsg.append("图片角标失效日期格式必须为 YYYY-MM-DD");
					isFail = true;
				}
			}			
		}

		if (detailImgList!=null && detailImgList.size()>0)
		{
			for (levelDetail detail : detailImgList)
			{
				if(Check.Null(detail.getType()))
				{
					errMsg.append("图文详情列表中文件类型不能为空值 ");
					isFail = true;
				}
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsImageUpdateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsImageUpdateReq>() {};
	}

	@Override
	protected DCP_GoodsImageUpdateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsImageUpdateRes();
	}





}
