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
import com.dsc.spos.json.cust.req.DCP_GoodsImageCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsImageCreateReq.levelDetail;
import com.dsc.spos.json.cust.req.DCP_GoodsImageCreateReq.levelProduct;
import com.dsc.spos.json.cust.req.DCP_GoodsImageCreateReq.levelSpec;
import com.dsc.spos.json.cust.req.DCP_GoodsImageCreateReq.levelSymbol;
import com.dsc.spos.json.cust.res.DCP_GoodsImageCreateRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsImageCreate extends SPosAdvanceService<DCP_GoodsImageCreateReq,DCP_GoodsImageCreateRes>
{

	@Override
	protected void processDUID(DCP_GoodsImageCreateReq req, DCP_GoodsImageCreateRes res) throws Exception 
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();
		String appType = req.getRequest().getAppType();
		String listImage=req.getRequest().getListImage();		
		String pluno=req.getRequest().getPluNo();
		String pluType=req.getRequest().getPluType();
		String symboDisplay=req.getRequest().getSymbolDisplay();

		List<levelDetail> detailImgList=req.getRequest().getDetailImageList();
		List<levelProduct> prodImgList=req.getRequest().getProdImageList();
		List<levelSpec> specImgList=req.getRequest().getSpecImageList();
		List<levelSymbol> symbolImgList=req.getRequest().getSymbolList();		

		String sql = "select PLUNO from DCP_GOODSIMAGE where eid='"+eId+"' and PLUNO='"+pluno+"' and APPTYPE='"+appType+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData!=null && getData.isEmpty()==false) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此商品图片记录已经存在！");
			return;
		}	


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

		//
		if (prodImgList!=null && prodImgList.size()>0) 
		{
			String[] columns_DCP_GOODSIMAGE_PRODIMAGE = 
				{ 
						"EID","APPTYPE","PLUNO","ITEM","PRODIMAGE","LASTMODITIME"
				};

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

		//
		if (specImgList!=null && specImgList.size()>0) 
		{
			String[] columns_DCP_GOODSIMAGE_SPECIMAGE = 
				{ 
						"EID","APPTYPE","PLUNO","ATTRVALUEID1","SPECIMAGE","LASTMODITIME"
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
		String[] columns_DCP_GOODSIMAGE = 
			{ 
					"EID","APPTYPE","PLUNO","PLUTYPE",
					"LISTIMAGE","SYMBOLDISPLAY","LASTMODITIME"
			};
		DataValue[] insValue1 = null;

		insValue1 = new DataValue[]{
				new DataValue(eId, Types.VARCHAR),
				new DataValue(appType, Types.VARCHAR),
				new DataValue(pluno, Types.VARCHAR),
				new DataValue(pluType, Types.VARCHAR),
				new DataValue(listImage, Types.VARCHAR),
				new DataValue(symboDisplay, Types.INTEGER),				
				new DataValue(lastmoditime, Types.DATE)				
		};

		InsBean ib1 = new InsBean("DCP_GOODSIMAGE", columns_DCP_GOODSIMAGE);
		ib1.addValues(insValue1);
		this.addProcessData(new DataProcessBean(ib1)); // 新增

		//
		this.doExecuteDataToDB();


        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
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
        //
        //同步缓存
        List<Plu_POS_GoodsPriceRedisUpdate> pluList=new ArrayList<>();
        //同步缓存
        Plu_POS_GoodsPriceRedisUpdate plu_pos_goodsPriceRedisUpdate=new Plu_POS_GoodsPriceRedisUpdate();
        plu_pos_goodsPriceRedisUpdate.setPluNo(req.getRequest().getPluNo());
        pluList.add(plu_pos_goodsPriceRedisUpdate);

        PosPub.POS_GoodsPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,pluList);

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsImageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsImageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsImageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsImageCreateReq req) throws Exception 
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
	protected TypeToken<DCP_GoodsImageCreateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsImageCreateReq>() {};
	}

	@Override
	protected DCP_GoodsImageCreateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsImageCreateRes();
	}



}
