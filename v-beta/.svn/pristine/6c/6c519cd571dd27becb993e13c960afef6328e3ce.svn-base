package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsSetGoodpriceCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetShopCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetShopCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsSetShopCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetShopCreate extends SPosAdvanceService<DCP_GoodsSetShopCreateReq, DCP_GoodsSetShopCreateRes> 
{

	@Override
	protected void processDUID(DCP_GoodsSetShopCreateReq req, DCP_GoodsSetShopCreateRes res) throws Exception 
	{
		
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());
			
		//DCP_GOODS_SHOP
		String[] columnsModular = 
			{
					"EID",
					"ORGANIZATIONNO",
					"PLUNO",
					"FSOD",
					"FPSO",
					"FPSP",
					"FSBA",
					"FSAL",
					"COUNTERNO",
					"WARNING_QTY",
					"MIN_QTY",
					"MUL_QTY",
					"MAX_QTY",
					"TAXCODE",
					"SAFE_QTY",
					"STYPE",
					"SUPPLIER",
					"IS_AUTO_SUBTRACT",
					"STATUS",
					"UPDATE_TIME"
			};


		List<level1Elm> jsonDatas=req.getSHOPDATAS();

		String sPlunoMulti="";
		
		if(jsonDatas!=null)
		{
			for (level1Elm par : jsonDatas) 
			{			

				sPlunoMulti+=par.getShopId()+",";
				
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[] 
						{ 
								new DataValue(req.geteId(), Types.VARCHAR),
								new DataValue(par.getShopId(), Types.VARCHAR), 
								new DataValue(req.getPLUNO(), Types.VARCHAR),
								new DataValue(req.getFSOD(), Types.VARCHAR),
								new DataValue(req.getFPSO(), Types.VARCHAR),
								new DataValue(req.getFPSP(), Types.VARCHAR),
								new DataValue(req.getFSBA(), Types.VARCHAR),
								new DataValue(req.getFSAL(), Types.VARCHAR),
								new DataValue(req.getCOUNTERNO(), Types.VARCHAR),
								new DataValue(req.getWARNING_QTY(), Types.FLOAT),
								new DataValue(req.getMIN_QTY(), Types.FLOAT),
								new DataValue(req.getMUL_QTY(), Types.FLOAT),
								new DataValue(req.getMAX_QTY(), Types.FLOAT),
								new DataValue(req.getTAXCODE(), Types.VARCHAR),
								new DataValue(req.getSAFE_QTY(), Types.FLOAT),
								new DataValue(req.getSTYPE(), Types.VARCHAR),
								new DataValue(req.getSUPPLIER(), Types.VARCHAR),
								new DataValue(req.getIS_AUTO_SUBTRACT(), Types.VARCHAR),
								new DataValue(req.getStatus(), Types.VARCHAR),
								new DataValue(mySysTime, Types.VARCHAR)				
						};

				InsBean ib1 = new InsBean("DCP_GOODS_SHOP", columnsModular);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			}
		}

		if(sPlunoMulti.length()>0)
		{
			sPlunoMulti=sPlunoMulti.substring(0, sPlunoMulti.length()-1);			
			//
			String[] arrPluno=new String[] {sPlunoMulti};		
			String sql = null;
			sql=this.getDCP_GOODS_SHOP_SQL(req,arrPluno);
			List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		
			if(getQData_check!=null && getQData_check.isEmpty()==false)
			{
				String sMultiShop="";
				for(Map<String, Object> par: getQData_check)
				{
					sMultiShop+=par.get("ORGANIZATIONNO").toString() + ",";
				}
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("商品适用门店信息已经存在！门店：" + sMultiShop);		    	
				this.pData.clear();
				return;//跳出
			}
		}
		
		
		this.doExecuteDataToDB();		

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetShopCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetShopCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetShopCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetShopCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		String pluno =req.getPLUNO();
		String STYPE =req.getSTYPE();	
		String SUPPLIER =req.getSUPPLIER();	
		String MAX_QTY =req.getMAX_QTY();		
		String MIN_QTY =req.getMIN_QTY();		
		String MUL_QTY =req.getMUL_QTY();		
		String SAFE_QTY =req.getSAFE_QTY();	
		String WARNING_QTY =req.getWARNING_QTY();			
		String status =req.getStatus();		

		List<DCP_GoodsSetShopCreateReq.level1Elm> shopdatas =req.getSHOPDATAS();
		
		if (Check.Null(pluno)) 
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(STYPE)) 
		{
			errMsg.append("供货商类型不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(SUPPLIER)) 
		{
			errMsg.append("供货商不可为空值, ");
			isFail = true;
		} 
		
		
		if (PosPub.isNumericType(MAX_QTY)==false) 
		{
			errMsg.append("要货最大量必须为数字类型, ");
			isFail = true;
		} 
		
		
		if (PosPub.isNumericType(MIN_QTY)==false) 
		{
			errMsg.append("要货最小量必须为数字类型, ");
			isFail = true;
		} 
		
		
		if (PosPub.isNumericType(MUL_QTY)==false) 
		{
			errMsg.append("要货倍量必须为数字类型, ");
			isFail = true;
		} 
		
		
		if (PosPub.isNumericType(SAFE_QTY)==false) 
		{
			errMsg.append("安全库存量必须为数字类型, ");
			isFail = true;
		} 
		
		
		if (PosPub.isNumericType(WARNING_QTY)==false) 
		{
			errMsg.append("警戒库存量必须为数字类型, ");
			isFail = true;
		} 
		
		if (Check.Null(status)) 
		{
			errMsg.append("商品状态不可为空值, ");
			isFail = true;
		} 
		
		
		
		if (shopdatas==null) 
		{
			errMsg.append("适用门店信息不可为空值, ");
			isFail = true;
		} 
		else
		{			
			for(DCP_GoodsSetShopCreateReq.level1Elm par: shopdatas)
			{
				if (Check.Null(par.getShopId())) 
				{
					errMsg.append("适用门店编码不可为空值, ");
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
	protected TypeToken<DCP_GoodsSetShopCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetShopCreateReq>(){};
	}

	@Override
	protected DCP_GoodsSetShopCreateRes getResponseType() 
	{
		return new DCP_GoodsSetShopCreateRes();
	}

	
	protected String getDCP_GOODS_SHOP_SQL(DCP_GoodsSetShopCreateReq req,String[] sourcePluno) throws Exception
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT A.ORGANIZATIONNO FROM DCP_GOODS_SHOP A INNER JOIN ("
				+ PosPub.getFormatSourcePluno(sourcePluno)
				+ ") B ON A.ORGANIZATIONNO=B.PLUNO WHERE A.EID='"+req.geteId()+"' AND A.PLUNO='"+req.getPLUNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	
	
}
