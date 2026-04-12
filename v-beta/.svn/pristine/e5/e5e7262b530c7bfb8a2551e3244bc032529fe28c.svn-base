package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayTypeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PayTypeUpdateReq.payName;
import com.dsc.spos.json.cust.req.DCP_PayTypeUpdateReq.range;
import com.dsc.spos.json.cust.res.DCP_PayTypeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayTypeUpdate extends SPosAdvanceService<DCP_PayTypeUpdateReq, DCP_PayTypeUpdateRes> {

	@Override
	protected void processDUID(DCP_PayTypeUpdateReq req, DCP_PayTypeUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId= req.geteId();
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}

		//必传字段
		String payType = req.getRequest().getPayType();
		//String payName = req.getRequest().getPayName();
		String payCode = req.getRequest().getPayCode();
		String funcNo = req.getRequest().getFuncNo();
		String disPlayType = req.getRequest().getDisPlayType();
		String status = req.getRequest().getStatus();
		String canRecharge = req.getRequest().getCanRecharge();//适用充值0-否1-是
		String canSaleCard = req.getRequest().getCanSaleCard();//适用售卡0-否1-是
		String canSaleTicket = req.getRequest().getCanSaleTicket();//适用售券0-否1-是
		String isVoucherInput = req.getRequest().getIsVoucherInput();//是否需录入凭证0-否1-是
		String canOpenCasher = req.getRequest().getCanOpenCasher();//是否可开钱箱0-否1-是
		String canCharge = req.getRequest().getCanCharge();		//可找零0-否1-是
		String maxCh = req.getRequest().getMaxCh();		//最大找零金额
		String canCustReturn=req.getRequest().getCanCustReturn();
		String canTableRsv=req.getRequest().getCanTableRsv();

		if(canCharge.equals("1"))
		{
			if(Check.Null(maxCh))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"最大找零金额不能为空" );
			}

		}
		String canSpill= req.getRequest().getCanSpill();//是否可溢收0-否1-是
		String maxSpill = req.getRequest().getMaxSpill();//最大溢收金额
		if(canSpill.equals("1"))
		{
			if(Check.Null(maxSpill))
			{

				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"最大溢收金额不能为空" );
			}

		}
		String canReverse = req.getRequest().getCanReverse();//是否可退款0-否1-是
		String canScore = req.getRequest().getCanScore();//是否可积分0-否1-是
		String canDiscount = req.getRequest().getCanDiscount();	//是否可打折0-否1-是
		String canOpenInvoice = req.getRequest().getCanOpenInvoice();//是否可开票0-否1-是
		String isTurnOver = req.getRequest().getIsTurnOver();//是否纳入营业额0-否1-是
		String restrictCompany = req.getRequest().getRestrictCompany();//适用公司：0-所有公司1-指定公司
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店2-排除门店
		String restrictChannel = req.getRequest().getRestrictChannel() == null ? "0" : req.getRequest().getRestrictChannel();//适用渠道：0-所有渠道1-指定渠道2-排除渠道
		String restrictAppType = req.getRequest().getRestrictAppType() == null ? "0" : req.getRequest().getRestrictAppType();//适用应用：0-所有应用1-指定应用

		String defineType = req.getRequest().getDefineType();//定义方式：SYSTEM-系统固化 CUSTOMIZE-客户自定义
		String canShiftDisplay = req.getRequest().getCanShiftDisplay();//交班是否显示0：不可 1：可
		String canShiftInput = req.getRequest().getCanShiftInput();	//交班录入实缴0：不可 1：可


		//非必须
		String classNo = req.getRequest().getClassNo();
		String sortId = req.getRequest().getSortId();
		String shortcut = req.getRequest().getShortcut();
		String eraseType = req.getRequest().getEraseType();
		String memo = req.getRequest().getMemo();
		List<DCP_PayTypeUpdateReq.range> rangeList = req.getRequest().getRangeList();
		List<DCP_PayTypeUpdateReq.payName> payName_lang = req.getRequest().getPayName_lang();

		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}

		UptBean up1 = new UptBean("DCP_PAYTYPE");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("PAYTYPE", new DataValue(payType, Types.VARCHAR));

		up1.addUpdateValue("PAYCODE", new DataValue(payCode, Types.VARCHAR));
		up1.addUpdateValue("CLASSNO", new DataValue(classNo, Types.VARCHAR));
		up1.addUpdateValue("SORTID", new DataValue(sortId, Types.VARCHAR));
		up1.addUpdateValue("FUNCNO", new DataValue(funcNo, Types.VARCHAR));
		up1.addUpdateValue("DISPLAYTYPE", new DataValue(disPlayType, Types.VARCHAR));
		up1.addUpdateValue("SHORTCUT", new DataValue(shortcut, Types.VARCHAR));

		up1.addUpdateValue("CANRECHARGE", new DataValue(canRecharge, Types.VARCHAR));
		up1.addUpdateValue("CANSALECARD", new DataValue(canSaleCard, Types.VARCHAR));
		up1.addUpdateValue("CANSALETICKET", new DataValue(canSaleTicket, Types.VARCHAR));
		up1.addUpdateValue("ISVOUCHERINPUT", new DataValue(isVoucherInput, Types.VARCHAR));
		up1.addUpdateValue("CANOPENCASHER", new DataValue(canOpenCasher, Types.VARCHAR));
		up1.addUpdateValue("CANCHARGE", new DataValue(canCharge, Types.VARCHAR));
		if (maxCh!=null&&maxCh.isEmpty()==false)
		{
			up1.addUpdateValue("MAXCH", new DataValue(maxCh, Types.VARCHAR));
		}

		up1.addUpdateValue("CANSPILL", new DataValue(canSpill, Types.VARCHAR));
		if (maxSpill!=null&&maxSpill.isEmpty()==false)
		{
			up1.addUpdateValue("MAXSPILL", new DataValue(maxSpill, Types.VARCHAR));
		}

		up1.addUpdateValue("CANREVERSE", new DataValue(canReverse, Types.VARCHAR));
		up1.addUpdateValue("CANSCORE", new DataValue(canScore, Types.VARCHAR));
		up1.addUpdateValue("CANDISCOUNT", new DataValue(canDiscount, Types.VARCHAR));
		up1.addUpdateValue("CANOPENINVOICE", new DataValue(canOpenInvoice, Types.VARCHAR));
		up1.addUpdateValue("ISTURNOVER", new DataValue(isTurnOver, Types.VARCHAR));
		up1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

		up1.addUpdateValue("DEFINETYPE", new DataValue(defineType, Types.VARCHAR));
		up1.addUpdateValue("CANSHIFTDISPLAY", new DataValue(canShiftDisplay, Types.VARCHAR));
		up1.addUpdateValue("CANSHIFTINPUT", new DataValue(canShiftInput, Types.VARCHAR));
		up1.addUpdateValue("ERASETYPE", new DataValue(eraseType, Types.INTEGER));

		up1.addUpdateValue("RESTRICTCOMPANY", new DataValue(restrictCompany, Types.VARCHAR));
		up1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
		up1.addUpdateValue("RESTRICTCHANNEL", new DataValue(restrictChannel, Types.VARCHAR));
		up1.addUpdateValue("RESTRICTAPPTYPE", new DataValue(restrictAppType, Types.VARCHAR));
		up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		up1.addUpdateValue("CANCUSTRETURN", new DataValue(canCustReturn, Types.VARCHAR));
		up1.addUpdateValue("CANTABLERSV", new DataValue(canTableRsv, Types.VARCHAR));

		this.addProcessData(new DataProcessBean(up1));

		DelBean	db1 = new DelBean("DCP_PAYTYPE_RANGE");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("PAYTYPE", new DataValue(payType, Types.VARCHAR));

		this.addProcessData(new DataProcessBean(db1));

		db1 = new DelBean("DCP_PAYTYPE_LANG");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("PAYTYPE", new DataValue(payType, Types.VARCHAR));

		this.addProcessData(new DataProcessBean(db1));

		String[] columns_class_lang =
			{
					"EID",
					"PAYTYPE",
					"LANG_TYPE" ,
					"PAYNAME",
					"LASTMODITIME"

			};
		for (payName lang : payName_lang)
		{
			DataValue[] insValue1_lang = null;
			insValue1_lang = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(payType, Types.VARCHAR),
					new DataValue(lang.getLangType(), Types.VARCHAR),
					new DataValue(lang.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
			};

			InsBean ib1_lang = new InsBean("DCP_PAYTYPE_LANG", columns_class_lang);
			ib1_lang.addValues(insValue1_lang);
			this.addProcessData(new DataProcessBean(ib1_lang));


		}

		if(rangeList!=null)
		{
			String[] columns_class_range =
				{
						"EID",
						"PAYTYPE",
						"RANGETYPE" ,
						"ID",
						"NAME",
						"LASTMODITIME"

				};

			for (range par : rangeList)
			{

				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(payType, Types.VARCHAR),
						new DataValue(par.getRangeType(), Types.VARCHAR),
						new DataValue(par.getId(), Types.VARCHAR),
						new DataValue(par.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)
				};

				InsBean ib1 = new InsBean("DCP_PAYTYPE_RANGE", columns_class_range);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));

			}

		}


		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayTypeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayTypeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayTypeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayTypeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if(req.getRequest().getPayName_lang()==null||req.getRequest().getPayName_lang().isEmpty())
		{
			errMsg.append("多语言不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		//必传字段
		String payType = req.getRequest().getPayType();
		//String payName = req.getRequest().getPayName();
		String payCode = req.getRequest().getPayCode();
		String funcNo = req.getRequest().getFuncNo();
		String disPlayType = req.getRequest().getDisPlayType();
		String status = req.getRequest().getStatus();

		String canRecharge = req.getRequest().getCanRecharge();//适用充值0-否1-是
		String canSaleCard = req.getRequest().getCanSaleCard();//适用售卡0-否1-是
		String canSaleTicket = req.getRequest().getCanSaleTicket();//适用售券0-否1-是
		String isVoucherInput = req.getRequest().getIsVoucherInput();//是否需录入凭证0-否1-是
		String canOpenCasher = req.getRequest().getCanOpenCasher();//是否可开钱箱0-否1-是
		String canCharge = req.getRequest().getCanCharge();		//可找零0-否1-是
		String maxCh = req.getRequest().getMaxCh();		//最大找零金额
		String canSpill= req.getRequest().getCanSpill();//是否可溢收0-否1-是
		String maxSpill = req.getRequest().getMaxSpill();//最大溢收金额
		String canReverse = req.getRequest().getCanReverse();//是否可退款0-否1-是
		String canScore = req.getRequest().getCanScore();//是否可积分0-否1-是
		String canDiscount = req.getRequest().getCanDiscount();	//是否可打折0-否1-是
		String canOpenInvoice = req.getRequest().getCanOpenInvoice();//是否可开票0-否1-是
		String isTurnOver = req.getRequest().getIsTurnOver();//是否纳入营业额0-否1-是
		String restrictCompany = req.getRequest().getRestrictCompany();//适用公司：0-所有公司1-指定公司
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店2-排除门店
		String restrictChannel = req.getRequest().getRestrictChannel() == null ? "0" : req.getRequest().getRestrictChannel();//适用渠道：0-所有渠道1-指定渠道2-排除渠道
		String restrictAppType = req.getRequest().getRestrictAppType() == null ? "0" : req.getRequest().getRestrictAppType();//适用应用：0-所有应用1-指定应用

		String defineType = req.getRequest().getDefineType();//定义方式：SYSTEM-系统固化 CUSTOMIZE-客户自定义
		String canShiftDisplay = req.getRequest().getCanShiftDisplay();//交班是否显示0：不可 1：可
		String canShiftInput = req.getRequest().getCanShiftInput();	//交班录入实缴0：不可 1：可

		List<DCP_PayTypeUpdateReq.range> rangeList = req.getRequest().getRangeList();
		 //卡支付:#C14 乐享卡  #C15 聚优 #C11 四威
		if(funcNo.equals("3011"))
		{
			if(!Check.Null(payType) && !payType.equals("#C13"))
			{
				errMsg.append("禄品卡款别编码payType值错误,必须为#C13, ");
				isFail = true;
			}
		}
		if(funcNo.equals("3012"))
		{
			if(!Check.Null(payType) && !payType.equals("#C11"))
			{
				errMsg.append("苏福通卡款别编码payType值错误,必须为#C11, ");
				isFail = true;
			}
		}
		if(funcNo.equals("3013"))
		{
			if(!Check.Null(payType) && !payType.equals("#C14"))
			{
				errMsg.append("乐享卡款别编码payType值错误,必须为#C14, ");
				isFail = true;
			}
		}
		if(funcNo.equals("3014"))
		{
			if(!Check.Null(payType) && !payType.equals("#C15"))
			{
				errMsg.append("聚优福利卡款别编码payType值错误,必须为#C15, ");
				isFail = true;
			}
		}

		if(Check.Null(payType)){
			errMsg.append("款别编码payType不能为空值， ");
			isFail = true;

		}
		/*if(Check.Null(payName)){
	   	errMsg.append("款别名称payName不能为空值， ");
	   	isFail = true;

	  }*/
		if(Check.Null(payCode)){
			errMsg.append("erp款别编码payCode不能为空值， ");
			isFail = true;

		}
		if(Check.Null(funcNo)){
			errMsg.append("支付接口编码funcNo不能为空值， ");
			isFail = true;

		}
		if(Check.Null(disPlayType)){
			errMsg.append("显示名称方式disPlayType不能为空值， ");
			isFail = true;

		}
		if(Check.Null(status)){
			errMsg.append("状态不能为空值， ");
			isFail = true;

		}

		if(Check.Null(canRecharge)){
			errMsg.append("canRecharge不能为空值， ");
			isFail = true;

		}
		if(Check.Null(canSaleCard)){
			errMsg.append("canSaleCard不能为空值， ");
			isFail = true;

		}
		if(Check.Null(canSaleTicket)){
			errMsg.append("canSaleTicket不能为空值， ");
			isFail = true;

		}
		if(Check.Null(isVoucherInput)){
			errMsg.append("isVoucherInput不能为空值， ");
			isFail = true;

		}
		if(Check.Null(canOpenCasher)){
			errMsg.append("canOpenCasher不能为空值， ");
			isFail = true;

		}
		if(Check.Null(canCharge)){
			errMsg.append("canCharge不能为空值， ");
			isFail = true;

		}
		/*if(Check.Null(maxCh)){
	   	errMsg.append("maxCh不能为空值， ");
	   	isFail = true;

	  }*/
		if(Check.Null(canSpill)){
			errMsg.append("canSpill不能为空值， ");
			isFail = true;

		}

		/* if(Check.Null(maxSpill)){
	   	errMsg.append("maxSpill不能为空值， ");
	   	isFail = true;

	  }*/
		if(Check.Null(canReverse)){
			errMsg.append("canReverse不能为空值， ");
			isFail = true;

		}

		if(Check.Null(canScore)){
			errMsg.append("canScore不能为空值， ");
			isFail = true;

		}
		if(Check.Null(canDiscount)){
			errMsg.append("canDiscount不能为空值， ");
			isFail = true;

		}

		if(Check.Null(canOpenInvoice)){
			errMsg.append("canOpenInvoice不能为空值， ");
			isFail = true;

		}
		if(Check.Null(isTurnOver)){
			errMsg.append("isTurnOver不能为空值， ");
			isFail = true;

		}
		if(Check.Null(defineType)){
			errMsg.append("defineType不能为空值， ");
			isFail = true;

		}
		if(Check.Null(canShiftDisplay)){
			errMsg.append("canShiftDisplay不能为空值， ");
			isFail = true;

		}
		if(Check.Null(canShiftInput)){
			errMsg.append("canShiftInput不能为空值， ");
			isFail = true;

		}
		if(Check.Null(restrictCompany)){
			errMsg.append("restrictCompany不能为空值， ");
			isFail = true;

		}

		if(Check.Null(restrictShop)){
			errMsg.append("restrictShop不能为空值， ");
			isFail = true;

		}
		/*if(Check.Null(restrictChannel)){
			errMsg.append("restrictChannel不能为空值， ");
			isFail = true;

		}
		if(Check.Null(restrictAppType)){
			errMsg.append("restrictAppType不能为空值， ");
			isFail = true;

		}*/


		boolean bCompany=false;
		boolean bshop=false;
		boolean bchannel=false;

		if (rangeList!=null && rangeList.size()>0)
		{
			for (range range : rangeList)
			{
				if(Check.Null(range.getRangeType()))
				{
					errMsg.append("适用范围列表类型rangeType不能为空值， ");
					isFail = true;
				}

				//1-公司2-门店3-渠道4-应用
				if(restrictCompany!=null)
				{
					if (restrictCompany.equals("0")|| ( restrictCompany.equals("0")==false &&range.getRangeType().equals("1")))
					{
						bCompany=true;
					}
				}
				if(restrictShop!=null)
				{
					if (restrictShop.equals("0") || (restrictShop.equals("0")==false &&range.getRangeType().equals("2")))
					{
						bshop=true;
					}
				}
				if(restrictChannel!=null)
				{
					if (restrictChannel.equals("0") || (restrictChannel.equals("0")==false &&range.getRangeType().equals("3")))
					{
						bchannel=true;
					}
				}

			}
		}
		else
		{
			if (restrictCompany!=null&&restrictCompany.equals("0"))
			{
				bCompany=true;
			}
			if (restrictShop!=null&&restrictShop.equals("0"))
			{
				bshop=true;
			}
			if (restrictChannel!=null&&restrictChannel.equals("0"))
			{
				bchannel=true;
			}
		}


		if (bCompany==false)
		{
			errMsg.append("适用范围列表-适用公司不能为空值， ");
			isFail = true;
		}

		if (bshop==false)
		{
			errMsg.append("适用范围列表-适用门店不能为空值， ");
			isFail = true;
		}

		if (bchannel==false)
		{
			errMsg.append("适用范围列表-适用渠道不能为空值， ");
			isFail = true;
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PayTypeUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayTypeUpdateReq>(){};
	}

	@Override
	protected DCP_PayTypeUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayTypeUpdateRes();
	}

}
