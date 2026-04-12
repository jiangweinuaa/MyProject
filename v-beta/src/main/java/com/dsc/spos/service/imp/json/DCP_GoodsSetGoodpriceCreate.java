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
import com.dsc.spos.json.cust.req.DCP_GoodsSetGoodpriceCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsSetGoodpriceCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetGoodpriceCreate extends SPosAdvanceService<DCP_GoodsSetGoodpriceCreateReq, DCP_GoodsSetGoodpriceCreateRes>
{
	@Override
	protected void processDUID(DCP_GoodsSetGoodpriceCreateReq req, DCP_GoodsSetGoodpriceCreateRes res) throws Exception
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		String sql = null;
		sql = this.getDCP_GOODS_PRICE_SQL(req);
		//检查
		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		
		if(getQData_check!=null && getQData_check.isEmpty()==false)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("商品供货价信息已经存在！");		    	
		}
		else	    
		{
			//DCP_GOODS_PRICE
			String[] columnsModular = 
				{
						"EID",
						"PLUNO",
						"UNIT",
						"TRADE_TYPE",
						"TRADE_OBJECT",
						"PRICE",
						"STATUS",
						"UPDATE_TIME"
				};

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[] 
					{ 
							new DataValue(req.geteId(), Types.VARCHAR),
							new DataValue(req.getPluNO(), Types.VARCHAR), 
							new DataValue(req.getUnitNO(), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue(req.getSupplierNO(), Types.VARCHAR),
							new DataValue(req.getGoodsPrice(), Types.FLOAT),
							new DataValue(req.getStatus(), Types.VARCHAR),
							new DataValue(mySysTime, Types.VARCHAR)				
					};

			InsBean ib1 = new InsBean("DCP_GOODS_PRICE", columnsModular);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


			List<level1Elm> jsonDatas=req.getDatas();

			//DCP_GOODS_PRICE_ORG
			String[] columnsModular2 = 
				{
						"EID",
						"ORGANIZATIONNO",
						"PLUNO",
						"UNIT",
						"TRADE_TYPE",
						"TRADE_OBJECT",
						"PRICE",
						"STATUS",
						"UPDATE_TIME"
				};

			StringBuffer sPlunoMulti=new StringBuffer("");
			if(jsonDatas!=null)
			{
				for (level1Elm par : jsonDatas) 
				{			

					sPlunoMulti.append(par.getShopId()+",");

					DataValue[] insValue2 = null;

					insValue2 = new DataValue[] 
							{ 
									new DataValue(req.geteId(), Types.VARCHAR),
									new DataValue(par.getShopId(), Types.VARCHAR),
									new DataValue(req.getPluNO(), Types.VARCHAR), 
									new DataValue(req.getUnitNO(), Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue(req.getSupplierNO(), Types.VARCHAR),
									new DataValue(par.getShopGoodPrice(), Types.FLOAT),
									new DataValue("100", Types.VARCHAR),
									new DataValue(mySysTime, Types.VARCHAR)				
							};

					InsBean ib2 = new InsBean("DCP_GOODS_PRICE_ORG", columnsModular2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭

				}
			}

			if(sPlunoMulti.length()>0)
			{
                sPlunoMulti.deleteCharAt(sPlunoMulti.length()-1);
				//
				String[] arrPluno=new String[] {sPlunoMulti.toString()};
				sql=this.getDCP_GOODS_PRICE_ORG_SQL(req,arrPluno);
				getQData_check = this.doQueryData(sql,null);		
				if(getQData_check!=null && getQData_check.isEmpty()==false)
				{
					StringBuffer sMultiShop=new StringBuffer("");
					for(Map<String, Object> par: getQData_check)
					{
						sMultiShop.append(par.get("ORGANIZATIONNO").toString() + ",");
					}
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("特殊供货价信息已经存在！门店：" + sMultiShop.toString());
					this.pData.clear();
					return;//跳出
				}

			}

			this.doExecuteDataToDB();		

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
			
		}



	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetGoodpriceCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetGoodpriceCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetGoodpriceCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetGoodpriceCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");


		String pluno =req.getPluNO();
		String GoodsPrice= req.getGoodsPrice();
		String SupplierNO= req.getSupplierNO();


		List<DCP_GoodsSetGoodpriceCreateReq.level1Elm> shopdatas =req.getDatas();

		if (Check.Null(pluno)) 
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		} 

		if (PosPub.isNumericType(GoodsPrice)==false) 
		{
			errMsg.append("供货价必须是数字类型, ");
			isFail = true;
		} 

		if (Check.Null(SupplierNO)) 
		{
			errMsg.append("供货组织编码不可为空值, ");
			isFail = true;
		} 

		if (shopdatas==null) 
		{
			errMsg.append("门店编码不可为空值, ");
			isFail = true;
		} 
		else
		{			
			for(DCP_GoodsSetGoodpriceCreateReq.level1Elm par: shopdatas)
			{
				if (Check.Null(par.getShopId())) 
				{
					errMsg.append("适用门店编码不可为空值, ");
					isFail = true;
				} 

				if (PosPub.isNumericType(par.getShopGoodPrice())==false) 
				{
					errMsg.append("特殊供货价必须是数字类型, ");
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
	protected TypeToken<DCP_GoodsSetGoodpriceCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetGoodpriceCreateReq>(){};
	}

	@Override
	protected DCP_GoodsSetGoodpriceCreateRes getResponseType() 
	{
		return new DCP_GoodsSetGoodpriceCreateRes();
	}


	protected String getDCP_GOODS_PRICE_SQL(DCP_GoodsSetGoodpriceCreateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT PLUNO FROM DCP_GOODS_PRICE WHERE EID='"+req.geteId()+"' AND PLUNO='"+req.getPluNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}


	protected String getDCP_GOODS_PRICE_ORG_SQL(DCP_GoodsSetGoodpriceCreateReq req,String[] sourcePluno) throws Exception
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT A.ORGANIZATIONNO FROM DCP_GOODS_PRICE_ORG A INNER JOIN ("
				+ PosPub.getFormatSourcePluno(sourcePluno)
				+ ") B ON A.ORGANIZATIONNO=B.PLUNO WHERE A.EID='"+req.geteId()+"' AND A.PLUNO='"+req.getPluNO()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}





}
