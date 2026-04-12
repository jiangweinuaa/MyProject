package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PayTypeQueryReq;
import com.dsc.spos.json.cust.res.DCP_PayTypeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PayTypeQuery extends SPosBasicService<DCP_PayTypeQueryReq, DCP_PayTypeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PayTypeQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_PayTypeQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PayTypeQueryReq>(){} ;
	}

	@Override
	protected DCP_PayTypeQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PayTypeQueryRes();
	}

	@Override
	protected DCP_PayTypeQueryRes processJson(DCP_PayTypeQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_PayTypeQueryRes res = this.getResponse();

        int totalRecords = 0;                                //总笔数
        int totalPages = 0;                                    //总页数


		String sql = null;
		String curLangtype = req.getLangType();
		if(curLangtype==null||curLangtype.isEmpty())
		{
			curLangtype = "zh_CN";
		}

		sql = this.getQuerySql(req);

		res.setDatas(new ArrayList<DCP_PayTypeQueryRes.level1Elm>());

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            // 计算页数
			int pageSize = 1000;
			if(req.getPageSize()!=0){
				pageSize = req.getPageSize();
			}
				totalPages = totalRecords / pageSize;
				totalPages = (totalRecords % pageSize > 0) ? totalPages + 1 : totalPages;
			Map<String, Boolean> condition_payType = new HashMap<String, Boolean>(); //查詢條件
			condition_payType.put("PAYTYPE", true);
		  //调用过滤函数
			List<Map<String, Object>> payTypeDatas=MapDistinct.getMap(getQData, condition_payType);

			condition_payType.put("LANGTYPE", true);
		  //调用过滤函数
			List<Map<String, Object>> payTypeLangDatas=MapDistinct.getMap(getQData, condition_payType);

			Map<String, Boolean> condition_range = new HashMap<String, Boolean>(); //查詢條件
			condition_range.put("PAYTYPE", true);
			condition_range.put("RANGETYPE", true);
			condition_range.put("ID", true);
		  //调用过滤函数
			List<Map<String, Object>> rangeDatas=MapDistinct.getMap(getQData, condition_range);

			for (Map<String, Object> map : payTypeDatas)
			{
				DCP_PayTypeQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String classNo = map.get("CLASSNO").toString();
				String className = map.get("CLASSNAME").toString();
				String payType = map.get("PAYTYPE").toString();
				String payName = map.get("PAYNAME").toString();
				String payCode = map.get("PAYCODE").toString();
				String payNameErp = map.get("PAYCODEERPNAME").toString();
                String payCodeErp = map.get("PAYCODEERP").toString();
				String funcNo = map.get("FUNCNO").toString();
				String funcName = map.get("FUNCNAME").toString();
				String disPlayType = map.get("DISPLAYTYPE").toString();
				String shortcut =  map.get("SHORTCUT").toString();
				String status = map.get("STATUS").toString();
				String canRecharge = map.get("CANRECHARGE").toString();//适用充值0-否1-是
				String canSaleCard = map.get("CANSALECARD").toString();//适用售卡0-否1-是
				String canSaleTicket = map.get("CANSALETICKET").toString();//适用售券0-否1-是
				String isVoucherInput = map.get("ISVOUCHERINPUT").toString();//是否需录入凭证0-否1-是
				String canOpenCasher = map.get("CANOPENCASHER").toString();//是否可开钱箱0-否1-是
				String canCharge = map.get("CANCHARGE").toString();		//可找零0-否1-是
				String maxCh = map.get("MAXCH").toString();		//最大找零金额
				String canSpill= map.get("CANSPILL").toString();//是否可溢收0-否1-是
				String maxSpill = map.get("MAXSPILL").toString();//最大溢收金额
				String canReverse = map.get("CANREVERSE").toString();//是否可退款0-否1-是
				String canScore = map.get("CANSCORE").toString();//是否可积分0-否1-是
				String canDiscount = map.get("CANDISCOUNT").toString();	//是否可打折0-否1-是
				String canOpenInvoice = map.get("CANOPENINVOICE").toString();//是否可开票0-否1-是
				String isTurnOver = map.get("ISTURNOVER").toString();//是否纳入营业额0-否1-是
				String restrictCompany = map.get("RESTRICTCOMPANY").toString();//适用公司：0-所有公司1-指定公司
				String restrictShop = map.get("RESTRICTSHOP").toString();//适用门店：0-所有门店1-指定门店2-排除门店
				String restrictChannel = map.get("RESTRICTCHANNEL").toString();//适用渠道：0-所有渠道1-指定渠道2-排除渠道
				String restrictAppType = map.get("RESTRICTAPPTYPE").toString();//适用应用：0-所有应用1-指定应用

				String defineType = map.get("DEFINETYPE").toString();//定义方式：SYSTEM-系统固化 CUSTOMIZE-客户自定义
				String canShiftDisplay = map.get("CANSHIFTDISPLAY").toString();//交班是否显示0：不可 1：可
				String canShiftInput = map.get("CANSHIFTINPUT").toString();	//交班录入实缴0：不可 1：可
                String eraseType = map.get("ERASETYPE").toString();

				oneLv1.setPayType(payType);
				oneLv1.setPayName(payName);
				oneLv1.setPayCode(payCode);
                oneLv1.setPayCodeErp(payCodeErp);
				oneLv1.setPayNameErp(payNameErp);
				oneLv1.setClassNo(classNo);
				oneLv1.setClassName(className);
				oneLv1.setSortId(map.get("SORTID").toString());
				oneLv1.setFuncNo(funcNo);
				oneLv1.setFuncName(funcName);
				oneLv1.setDisPlayType(disPlayType);
				oneLv1.setShortcut(shortcut);
				oneLv1.setCanRecharge(canRecharge);
				oneLv1.setCanSaleCard(canSaleCard);
				oneLv1.setCanSaleTicket(canSaleTicket);
				oneLv1.setIsVoucherInput(isVoucherInput);
				oneLv1.setCanOpenCasher(canOpenCasher);
				oneLv1.setCanCharge(canCharge);
				oneLv1.setMaxCh(maxCh);
				oneLv1.setCanSpill(canSpill);
				oneLv1.setMaxSpill(maxSpill);
				oneLv1.setCanReverse(canReverse);
				oneLv1.setCanScore(canScore);
				oneLv1.setCanDiscount(canDiscount);
				oneLv1.setCanOpenInvoice(canOpenInvoice);
				oneLv1.setIsTurnOver(isTurnOver);
				oneLv1.setRestrictCompany(restrictCompany);
				oneLv1.setRestrictShop(restrictShop);
				oneLv1.setRestrictChannel(restrictChannel);
				oneLv1.setRestrictAppType(restrictAppType);
				oneLv1.setDefineType(defineType);
				oneLv1.setCanShiftDisplay(canShiftDisplay);
				oneLv1.setCanShiftInput(canShiftInput);
				oneLv1.setCanCustReturn(map.get("CANCUSTRETURN").toString());
				oneLv1.setCanTableRsv(map.get("CANTABLERSV").toString());
				oneLv1.setMemo(map.get("CREATEOPID").toString());
				oneLv1.setStatus(status);
				oneLv1.setCreateopid(map.get("CREATEOPID").toString());
				oneLv1.setCreateopname(map.get("CREATEOPNAME").toString());
				oneLv1.setCreatetime(map.get("CREATETIME").toString());
				oneLv1.setLastmodiopid(map.get("LASTMODIOPID").toString());
				oneLv1.setLastmodiname(map.get("LASTMODIOPNAME").toString());
				oneLv1.setLastmoditime(map.get("LASTMODITIME").toString());
				oneLv1.setEraseType(eraseType);

				oneLv1.setPayName_lang(new ArrayList<DCP_PayTypeQueryRes.payNameLang>());
				for (Map<String, Object> mapLang : payTypeLangDatas)
				{
					String payType_lang = mapLang.get("PAYTYPE").toString();
					String langType = mapLang.get("LANGTYPE").toString();
					String name = mapLang.get("PAYNAME").toString();
					if(langType==null||langType.isEmpty())
					{
						continue;
					}

					if(payType_lang.equals(payType))
					{
						DCP_PayTypeQueryRes.payNameLang oneLv2 = res.new payNameLang();
						oneLv2.setLangType(langType);
						oneLv2.setName(name);
						if(curLangtype.equals(langType))
						{
							oneLv1.setPayName(name);
						}
						oneLv1.getPayName_lang().add(oneLv2);


					}


				}


				oneLv1.setRangeList(new ArrayList<DCP_PayTypeQueryRes.range>());

				for (Map<String, Object> map_range : rangeDatas)
				{
					String payType_range = map_range.get("PAYTYPE").toString();
					String rangeType = map_range.get("RANGETYPE").toString();
					String id = map_range.get("ID").toString();
					String name = map_range.get("NAME").toString();
					if(rangeType==null||rangeType.isEmpty()||id==null||id.isEmpty())
					{
						continue;
					}

					if(payType.equals(payType_range))
					{
						DCP_PayTypeQueryRes.range oneLv2 = res.new range();
						oneLv2.setRangeType(rangeType);
						oneLv2.setId(id);
						oneLv2.setName(name);
						oneLv1.getRangeList().add(oneLv2);


					}



				}

				res.getDatas().add(oneLv1);

			}




		}
        res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_PayTypeQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	String eId = req.geteId();
	String langtype = req.getLangType();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;

	if(langtype==null||langtype.isEmpty())
	{
		langtype = "zh_CN";
	}

	String ketTxt = null;
	String appType = null;
	String channelId = null;
	String shopId = null;
	String status = null;
	String canRecharge = null;
	String canSaleCard = null;
	String canSaleTicket = null;
	String classNo = null;
	String canCustReturn = null;
	String canTableRsv = null;

	if(req.getRequest()!=null)
	{
		ketTxt = req.getRequest().getKeyTxt();
		appType = req.getRequest().getAppType();
		channelId = req.getRequest().getChannelId();
		shopId = req.getRequest().getShopId();
		status = req.getRequest().getStatus();
		canRecharge = req.getRequest().getCanRecharge();
		canSaleCard = req.getRequest().getCanSaleCard();
		canSaleTicket = req.getRequest().getCanSaleTicket();
		classNo = req.getRequest().getClassNo();
		canCustReturn=req.getRequest().getCanCustReturn();
		canTableRsv=req.getRequest().getCanTableRsv();
	}

	String sql = null;
	StringBuffer sqlbuf=new StringBuffer("");

	sqlbuf.append(" select * from (");
	sqlbuf.append(" select COUNT(DISTINCT a.PAYTYPE) OVER() num, DENSE_RANK () OVER(ORDER BY a.PAYTYPE) rn," +
            " A.*,AL.LANG_TYPE AS LANGTYPE,AL.PAYNAME,BL.CLASSNAME,CL.FUNCNAME, "
		+ " M.PAYCODEERP,M.PAYNAME AS PAYCODEERPNAME,R.RANGETYPE,R.ID,R.NAME from DCP_PAYTYPE A");
	sqlbuf.append(" LEFT JOIN DCP_PAYTYPE_LANG AL on A.EID=AL.EID and A.PAYTYPE=AL.PAYTYPE ");
	//sqlbuf.append(" LEFT JOIN DCP_PAYCLASS B on A.EID=B.EID and A.CLASSNO=B.CLASSNO ");
	sqlbuf.append(" LEFT JOIN DCP_PAYCLASS_LANG BL on A.EID=BL.EID and A.CLASSNO=BL.CLASSNO AND BL.LANG_TYPE='"+langtype+"' ");
	sqlbuf.append(" LEFT JOIN DCP_PAYFUNC_LANG CL on A.EID=CL.EID and A.FUNCNO=CL.FUNCNO AND CL.LANG_TYPE='"+langtype+"' ");
	sqlbuf.append(" LEFT JOIN DCP_PAYMENT M on A.EID=M.EID and A.PAYCODE=M.PAYCODE ");
	sqlbuf.append(" LEFT JOIN DCP_PAYTYPE_RANGE R on A.EID=R.EID and A.PAYTYPE=R.PAYTYPE ");
	sqlbuf.append(" where a.eid='"+eId+"' ");
	if(classNo!=null&&classNo.length()>0)
	{
		sqlbuf.append(" and a.CLASSNO='"+classNo +"' ");
	}
	if(status != null && status.length() >0)
	{
		sqlbuf.append(" and a.status="+status +" ");
	}
	if(canRecharge != null && canRecharge.length()>0)
	{
		sqlbuf.append(" and a.canRecharge="+canRecharge +" ");
	}
	if(canSaleCard != null && canSaleCard.length()>0)
	{
		sqlbuf.append(" and a.canSaleCard="+canSaleCard +" ");
	}
	if(canSaleTicket != null && canSaleTicket.length()>0)
	{
		sqlbuf.append(" and a.canSaleTicket="+canSaleTicket +" ");
	}

	if(canCustReturn != null && canCustReturn.length()>0)
	{
		sqlbuf.append(" and a.CANCUSTRETURN="+canCustReturn +" ");
	}

	if(canTableRsv != null && canTableRsv.length()>0)
	{
		sqlbuf.append(" and a.CANTABLERSV="+canTableRsv +" ");
	}


	if(ketTxt != null && ketTxt.length() >0)
	{
		sqlbuf.append(" and (a.PAYTYPE like '%%"+ketTxt+"%%' or AL.PAYNAME like '%%"+ketTxt+"%%' "
				+ " OR a.PAYCODE = '"+ketTxt+"' OR M.PAYNAME LIKE '%%"+ketTxt+"%%') ");
	}

	if(shopId != null && shopId.length() >0) //rangetype=2 是适用门店
	{
		sqlbuf.append(" and "
				+ "(a.PAYTYPE in "
				+ "(select PAYTYPE from DCP_PAYTYPE_RANGE d  where d.eid='"+eId+"' and (d.rangetype=2 and d.id='"+shopId+"') "
				+ ") or a.restrictshop=0 "
				+ ") ");
	}

	if(channelId != null && channelId.length() >0) //rangetype=3 是适用渠道
	{
		sqlbuf.append(" and "
				+ "(a.PAYTYPE in "
				+ "(select PAYTYPE from DCP_PAYTYPE_RANGE d  where d.eid='"+eId+"' and (d.rangetype=3 and d.id='"+channelId+"') "
				+ ") or a.restrictchannel=0 "
				+ ") ");
	}

	if(appType != null && appType.length() >0) //rangetype=4 是适用应用
	{
		sqlbuf.append(" and "
				+ "(a.PAYTYPE in "
				+ "(select PAYTYPE from DCP_PAYTYPE_RANGE d  where d.eid='"+eId+"' and (d.rangetype=4 and d.id='"+appType+"') "
				+ ") or a.restrictapptype=0 "
				+ ") ");
	}


	sqlbuf.append(") ");
	if(pageNumber != 0 && pageSize!= 0){
	    sqlbuf.append(" WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + "");
    }
	sqlbuf.append(" order by SORTID");
	sql = sqlbuf.toString();
	return sql;

	}

}
