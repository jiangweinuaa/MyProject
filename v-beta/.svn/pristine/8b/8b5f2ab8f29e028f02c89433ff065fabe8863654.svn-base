package com.dsc.spos.service.utils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.ExecuteService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.EncryptUtils;
import com.google.gson.reflect.TypeToken;

public class TokenManagerRetail
{

	Logger logger = LogManager.getLogger(TokenManagerRetail.class);

	public static Map<String, TokeBean> tokenData;

	private final long EFFECTIVE_MINS = 480; //目前 token 八小時內有效

	public TokenManagerRetail()
	{
		if (tokenData==null)
		{
			tokenData= new LinkedHashMap<String, TokeBean>();
		}
	}


	/**
	 * 新增資料
	 * @param key
	 */
	private void addTokenData(String key, DCP_LoginRetailRes res)
	{
		tokenData.put(key, new TokeBean(res, new Date()));
	}

	/**
	 * 取得 token 資料.
	 * @param key
	 * @return
	 */
	private TokeBean getTokenData(String key)
	{
		TokeBean tb = tokenData.get(key);
        tb=null;
		//************本地找不到，到数据库取**************
		if(tb==null)
		{
			try
			{
				List<Map<String, Object>> getQData2=StaticInfo.dao.executeQuerySQL("select * from platform_token where key='"+key+"'", null);
				if (getQData2 != null && getQData2.isEmpty() == false )
				{
					DCP_LoginRetailRes res=new DCP_LoginRetailRes();
					res.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());

					DCP_LoginRetailRes.level1Elm oneLv1=new DCP_LoginRetailRes().new level1Elm();
					//json格式解析
					JSONObject jsonres = new JSONObject(getQData2.get(0).get("JSON").toString());
					oneLv1.setShopId(jsonres.getString("SHOPID").toString());
					oneLv1.setOrg_Form(jsonres.getString("ORG_FORM").toString());
					oneLv1.setShopName(jsonres.getString("SHOPNAME").toString());
					oneLv1.setOrganizationNo(jsonres.getString("SHOPID").toString());
					oneLv1.seteId(jsonres.getString("EID").toString());
					oneLv1.setLangType(jsonres.getString("LANGTYPE").toString());
					oneLv1.setOpNo(jsonres.getString("OPNO").toString());
					oneLv1.setOpName(jsonres.getString("OPNAME").toString());

					oneLv1.setIn_cost_warehouse(jsonres.getString("IN_COST_WAREHOUSE").toString());
					oneLv1.setIn_non_cost_warehouse(jsonres.getString("IN_NON_COST_WAREHOUSE").toString());
					oneLv1.setOut_cost_warehouse(jsonres.getString("OUT_COST_WAREHOUSE").toString());
					oneLv1.setOut_non_cost_warehouse(jsonres.getString("OUT_NON_COST_WAREHOUSE").toString());
					oneLv1.setInv_cost_warehouse(jsonres.getString("INV_COST_WAREHOUSE").toString());
					oneLv1.setInv_non_cost_warehouse(jsonres.getString("INV_NON_COST_WAREHOUSE").toString());
                    oneLv1.setReturn_cost_warehouse(jsonres.getString("RETURN_COST_WAREHOUSE").toString());
                    oneLv1.setReturn_cost_warehouse_name(jsonres.getString("RETURN_COST_WAREHOUSENAME").toString());
					oneLv1.setIn_cost_warehouse_name(jsonres.getString("IN_COST_WAREHOUSENAME").toString());
					oneLv1.setIn_non_cost_warehouse_name(jsonres.getString("IN_NON_COST_WAREHOUSENAME").toString());
					oneLv1.setOut_cost_warehouse_name(jsonres.getString("OUT_COST_WAREHOUSENAME").toString());
					oneLv1.setOut_non_cost_warehouse_name(jsonres.getString("OUT_NON_COST_WAREHOUSENAME").toString());
					oneLv1.setInv_cost_warehouse_name(jsonres.getString("INV_COST_WAREHOUSENAME").toString());
					oneLv1.setInv_non_cost_warehouse_name(jsonres.getString("INV_NON_COST_WAREHOUSENAME").toString());
					oneLv1.setReturn_cost_warehouse(jsonres.getString("RETURN_COST_WAREHOUSE").toString());
					oneLv1.setReturn_cost_warehouse_name(jsonres.getString("RETURN_COST_WAREHOUSENAME").toString());

					oneLv1.setDefDepartNo(jsonres.getString("DEFDEPARTNO").toString());
					oneLv1.setDefDepartName(jsonres.getString("DEFDEPARTNAME").toString());
					oneLv1.setChatUserId(jsonres.getString("CHATUSERID").toString());

					oneLv1.setCITY(jsonres.getString("CITY").toString());
					oneLv1.setDISTRICT(jsonres.getString("DISTRICT").toString());
					oneLv1.setENABLECREDIT(jsonres.getString("ENABLECREDIT").toString());
					oneLv1.setBELFIRM(jsonres.getString("BELFIRM").toString());
					oneLv1.setBELFIRM_NAME(jsonres.getString("BELFIRM_NAME").toString());
					oneLv1.setMultiWarehouse(jsonres.getString("MULTIWAREHOUSE").toString());
					oneLv1.setEnableMultiLang(jsonres.getString("ENABLEMULTILANG").toString());
					oneLv1.setPageSizeDetail(jsonres.getString("PAGESIZEDETAIL").toString());

                    oneLv1.setEmployeeNo(jsonres.getString("EMPLOYEENO").toString());
                    oneLv1.setDepartmentNo(jsonres.getString("DEPARTMENTNO").toString());
                    oneLv1.setEmployeeName(jsonres.getString("EMPLOYEENAME").toString());
					oneLv1.setDepartmentName(jsonres.getString("DEPARTMENTNAME").toString());

					oneLv1.setCorp(jsonres.getString("CORP").toString());
					oneLv1.setCorpName(jsonres.getString("CORPNAME").toString());

					oneLv1.setOrgRange(jsonres.getString("ORGRANGE").toString());
					oneLv1.setDefaultOrg(jsonres.getString("DEFAULTORG").toString());
					oneLv1.setBelOrgNo(jsonres.getString("BELORGNO").toString());
					oneLv1.setBelOrgName(jsonres.getString("BELORGNAME").toString());
					oneLv1.setUpDepartNo(jsonres.getString("UPDEPARTNO").toString());

                    oneLv1.setTaxPayerType(jsonres.getString("TAXPAYERTYPE").toString());
                    oneLv1.setOutputTaxCode(jsonres.getString("OUTPUTTAXCODE").toString());
                    oneLv1.setOutputTaxName(jsonres.getString("OUTPUTTAXNAME").toString());
                    oneLv1.setOutputTaxRate(jsonres.getString("OUTPUTTAXRATE").toString());
                    oneLv1.setInputTaxCode(jsonres.getString("INPUTTAXCODE").toString());
                    oneLv1.setInputTaxName(jsonres.getString("INPUTTAXNAME").toString());
                    oneLv1.setInputTaxRate(jsonres.getString("INPUTTAXRATE").toString());
                    oneLv1.setInputTaxCalType(jsonres.getString("INPUTTAXCALTYPE").toString());
                    oneLv1.setInputTaxInclTax(jsonres.getString("INPUTTAXINCLTAX").toString());
                    oneLv1.setOutputTaxCalType(jsonres.getString("OUTPUTTAXCALTYPE").toString());
                    oneLv1.setOutputTaxInclTax(jsonres.getString("OUTPUTTAXINCLTAX").toString());

					res.getDatas().add(oneLv1);

					oneLv1=null;

					getQData2=null;

					addTokenData(key,res);
					tb = tokenData.get(key);

				}
				//dao.closeDAO();
				//dao=null;

			}
			catch (Exception ex)
			{
				logger.error("\r\n*****getTokenData获取token失败：" + ex.getMessage()+"******\r\n");
			}
		}
		//**************************

		return tb;
	}

	/**
	 * 取得之前登入的資料
	 * @param token
	 * @return
	 */
	public DCP_LoginRetailRes getLoginData(String token)
	{
		//System.out.println("登录成功后获取"+this.getTokenData(token).getRes().getServiceStatus());
		return this.getTokenData(token).getRes();
	}

	/**
	 * 移除失效的 token
	 * @param token 令牌
	 */
	private void revoked(String token)
	{
		tokenData.remove(token);
	}

	/**
	 * 產生 token
	 * @param res
	 * @return 產生token
	 * @throws JSONException
	 */

	public String produce(DCP_LoginRetailRes res) throws Exception
	{
		DCP_LoginRetailRes.level1Elm oneLv1 = res.getDatas().get(0);
		//生成一个GUID供TOKEN使用
		String tguid=UUID.randomUUID().toString();
		EncryptUtils eu=new EncryptUtils();
		String token = eu.encodeMD5(oneLv1.getOpNo()+ tguid + System.currentTimeMillis());
		eu=null;

		//内存优化
		String xJson=com.alibaba.fastjson.JSONObject.toJSONString(res);
		ParseJson pj = new ParseJson();
		DCP_LoginRetailRes tempRes=pj.jsonToBean(xJson, new TypeToken<DCP_LoginRetailRes>(){});
		pj=null;
		tempRes.getDatas().get(0).setDataParas(new ArrayList<DCP_LoginRetailRes.paras>());
		tempRes.getDatas().get(0).setMyPower(new ArrayList<DCP_LoginRetailRes.level2Powers>());
		tempRes.getDatas().get(0).setMyShops(new ArrayList<DCP_LoginRetailRes.level2Shops>());
		//tempRes是清理List后的数据,占内存小很多
		this.addTokenData(token, tempRes);
		xJson=null;
		tempRes=null;

		//*****************存储token**************************
		JSONObject headerJson = new JSONObject();

		headerJson.put("SHOPID",oneLv1.getShopId());
		headerJson.put("SHOPNAME",oneLv1.getShopName());
		headerJson.put("ORGANIZATIONNO",oneLv1.getOrganizationNo());
		headerJson.put("ORG_FORM",oneLv1.getOrg_Form());

		headerJson.put("EID",oneLv1.geteId());
		headerJson.put("LANGTYPE",oneLv1.getLangType());
		headerJson.put("OPNO",oneLv1.getOpNo());
		headerJson.put("OPNAME",oneLv1.getOpName());
		headerJson.put("VIEWABLEDAY",oneLv1.getViewAbleDay());

		headerJson.put("IN_COST_WAREHOUSE",oneLv1.getIn_cost_warehouse());
		headerJson.put("IN_NON_COST_WAREHOUSE",oneLv1.getIn_non_cost_warehouse());
		headerJson.put("OUT_COST_WAREHOUSE",oneLv1.getOut_cost_warehouse());
		headerJson.put("OUT_NON_COST_WAREHOUSE",oneLv1.getOut_non_cost_warehouse());
		headerJson.put("INV_COST_WAREHOUSE",oneLv1.getInv_cost_warehouse());
		headerJson.put("INV_NON_COST_WAREHOUSE",oneLv1.getInv_non_cost_warehouse());

		headerJson.put("IN_COST_WAREHOUSENAME",oneLv1.getIn_cost_warehouse_name());
		headerJson.put("IN_NON_COST_WAREHOUSENAME",oneLv1.getIn_non_cost_warehouse_name());
		headerJson.put("OUT_COST_WAREHOUSENAME",oneLv1.getOut_cost_warehouse_name());
		headerJson.put("OUT_NON_COST_WAREHOUSENAME",oneLv1.getOut_non_cost_warehouse_name());
		headerJson.put("INV_COST_WAREHOUSENAME",oneLv1.getInv_cost_warehouse_name());
		headerJson.put("INV_NON_COST_WAREHOUSENAME",oneLv1.getInv_non_cost_warehouse_name());
		headerJson.put("RETURN_COST_WAREHOUSE",oneLv1.getReturn_cost_warehouse());
        headerJson.put("RETURN_COST_WAREHOUSENAME",oneLv1.getReturn_cost_warehouse_name());
        headerJson.put("DEFDEPARTNO",oneLv1.getDefDepartNo());
		headerJson.put("DEFDEPARTNAME",oneLv1.getDefDepartName());
		headerJson.put("CHATUSERID",oneLv1.getChatUserId());

		headerJson.put("CITY",oneLv1.getCITY());
		headerJson.put("DISTRICT",oneLv1.getDISTRICT());
		headerJson.put("ENABLECREDIT",oneLv1.getENABLECREDIT());
		headerJson.put("BELFIRM",oneLv1.getBELFIRM());
		headerJson.put("BELFIRM_NAME",oneLv1.getBELFIRM_NAME());
		headerJson.put("MULTIWAREHOUSE",oneLv1.getMultiWarehouse());
		headerJson.put("ENABLEMULTILANG",oneLv1.getEnableMultiLang());
		headerJson.put("PAGESIZEDETAIL", oneLv1.getPageSizeDetail());
        headerJson.put("EMPLOYEENO",oneLv1.getEmployeeNo());
        headerJson.put("EMPLOYEENAME",oneLv1.getEmployeeName());
        headerJson.put("DEPARTMENTNAME",oneLv1.getDepartmentName());
        headerJson.put("DEPARTMENTNO",oneLv1.getDepartmentNo());
        headerJson.put("CORP",oneLv1.getCorp());
        headerJson.put("CORPNAME",oneLv1.getCorpName());
        headerJson.put("ORGRANGE",oneLv1.getOrgRange());
        headerJson.put("DEFAULTORG",oneLv1.getDefaultOrg());
        headerJson.put("BELORGNO",oneLv1.getBelOrgNo());
        headerJson.put("BELORGNAME",oneLv1.getBelOrgName());
        headerJson.put("UPDEPARTNO",oneLv1.getUpDepartNo());

        headerJson.put("TAXPAYERTYPE",oneLv1.getTaxPayerType());
        headerJson.put("INPUTTAXCODE",oneLv1.getInputTaxCode());
        headerJson.put("INPUTTAXNAME",oneLv1.getInputTaxName());
        headerJson.put("INPUTTAXRATE",oneLv1.getInputTaxRate());
        headerJson.put("OUTPUTTAXRATE",oneLv1.getOutputTaxRate());
        headerJson.put("OUTPUTTAXCODE",oneLv1.getOutputTaxCode());
        headerJson.put("OUTPUTTAXNAME",oneLv1.getOutputTaxName());
        headerJson.put("INPUTTAXCALTYPE",oneLv1.getInputTaxCalType());
        headerJson.put("OUTPUTTAXCALTYPE",oneLv1.getOutputTaxCalType());
        headerJson.put("INPUTTAXINCLTAX",oneLv1.getInputTaxInclTax());
        headerJson.put("OUTPUTTAXINCLTAX",oneLv1.getOutputTaxInclTax());

		String[] columns1 = {"KEY", "JSON"};
		DataValue[] insValue1 = new DataValue[]
				{
						new DataValue(token, Types.VARCHAR),
						new DataValue(headerJson.toString(), Types.VARCHAR),
				};
		StaticInfo.dao.insert("PLATFORM_TOKEN", columns1, insValue1);

		//
		insValue1=null;
		columns1=null;
		headerJson=null;


		//dao.closeDAO();
		//dao=null;
		//***************************************************

		return token;
	}


	/**
	 * 檢查是否為合法使用 Service.
	 * @param es
	 * @param token
	 * @throws Exception
	 */
	public void checkCanUseService(ExecuteService es, String token) throws Exception {
		if (es.needTokenVerify())
		{
			if (token == null || token.length() == 0)
			{
				//				throw new Exception(MesageUtils.getInstance().getMessage(MessageCommanType.LOSE_TOKEN));
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E440); // modi. 07558 at 2016-06-24
			}
			else
			{
				boolean v = verify(token);
				if (!v)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E440); // modi. 07558 at 2016-06-24
					//					throw new Exception(MesageUtils.getInstance().getMessage(MessageCommanType.LOSE_TOKEN));
				}
			}
		}
	}


	/**
	 * 驗証 token.
	 * @param token
	 * @return
	 */
	public boolean verify(String token)
	{
		TokeBean tb = this.getTokenData(token);
		if (tb == null)
		{
			return Boolean.FALSE; //查無 token 資料
		}

		boolean b = verify(tb.getLoginTime());
		if (b)
		{
			return b;
		}

		//資訊不對, 一律移除
		this.revoked(token);


		//*********移除*************
		try
		{
			// condition
			Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
			DataValue c1 = new DataValue(token, Types.VARCHAR);
			conditions.put("KEY", c1);

			StaticInfo.dao.doDelete("PLATFORM_TOKEN", conditions);

			//dao.closeDAO();
			//dao=null;
		}
		catch (Exception ex)
		{
			logger.error("\r\n*****verify移除token失败：" + ex.getMessage()+"******\r\n");
		}

		//*************************

		return Boolean.FALSE;
	}

	/**
	 * 驗証 token.
	 * @param loginDate
	 * @return
	 */
	private boolean verify(Date loginDate)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long loginTime = loginDate.getTime();
		//		long minus = (((now - loginTime) / 1000L) / 60L) / 60L;
		//		if (minus < 8) { //八小時內的 token 都是有效的
		//			return Boolean.TRUE;
		//		}
		long minus = ((now - loginTime) / 1000L) / 60L;

		if (minus < this.EFFECTIVE_MINS)
		{
			return Boolean.TRUE;
		}

		logger.error("\r\n******token用户已登录时间: " + minus  + "分钟******\r\n");

		return Boolean.FALSE;
	}

	/**
	 * 清除己失效的 token. 目前己八小時為一個單位.
	 * @return 清除的數量
	 */
	public int doClearLoseEffectiveness()
	{
		int count = 0;

		try
		{
			//列表SQL
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();

			String[] strZero=new String[0];
			String[] tokenKeys = tokenData.keySet().toArray(strZero);

			for (String tk : tokenKeys)
			{
				TokeBean tb = this.getTokenData(tk);
				if (!this.verify(tb.getLoginTime()))
				{
					this.revoked(tk);
					count++;

					DelBean db1 = new DelBean("PLATFORM_TOKEN");
					db1.addCondition("KEY", new DataValue(tk, Types.VARCHAR));
					lstData.add(new DataProcessBean(db1));
					db1=null;
				}
			}

			tokenKeys=null;
			strZero=null;
			//
			StaticInfo.dao.useTransactionProcessData(lstData);
			lstData=null;
		}
		catch (Exception e)
		{
			logger.error("\r\n******token过期清理报错: " + e.getMessage()  + "******\r\n");
		}

		return count;
	}


	public void deleteTokenAndDB(String token)
	{
		try
		{
			//列表SQL
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();

			tokenData.remove(token);

			DelBean db1 = new DelBean("PLATFORM_TOKEN");
			db1.addCondition("KEY", new DataValue(token, Types.VARCHAR));
			lstData.add(new DataProcessBean(db1));
			db1=null;

			//
			StaticInfo.dao.useTransactionProcessData(lstData);
			lstData=null;
		}
		catch (Exception e)
		{
			logger.error("\r\n******token删除报错: " + e.getMessage()  + "******\r\n");
		}
	}

	/**
	 * 返回token总数
	 * @return
	 */
	public int CountToken()
	{
		return tokenData.size();
	}

	/**
	 * 取得 Token 的列表
	 * @return
	 */
	public Map<String, TokeBean> getTokenData()
	{
		return tokenData;
	}



	public class TokeBean
	{
		private Date loginTime; //登入時間

		public DCP_LoginRetailRes getRes()
		{
			return res;
		}

		public Date getLoginTime()
		{
			return loginTime;
		}

		private DCP_LoginRetailRes res;

		public TokeBean(DCP_LoginRetailRes res, Date loginTime)
		{
			this.res = res;
			this.loginTime = loginTime;
		}



	}

}
