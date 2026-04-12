package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.foreign.erp.request.HolidayOrderCreateRequest;
import com.dsc.spos.utils.*;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;

import javafx.geometry.Pos;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 上传订单ERP (不允许并发执行)
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HolidayShoporderCreate_V3 extends InitJob
{

	static boolean bRun = false;// 标记此服务是否正在执行中

	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		log("【同步任务HolidayShoporderCreate_V3】同步START！");
		int loopCount = 0;
		try
		{
			if (bRun)
			{
				log("\r\n*********同步任务HolidayShoporderCreate_V3正在执行中,本次调用取消:************\r\n");
				sReturnInfo="同步任务HolidayShoporderCreate_V3正在执行中,本次调用取消";
				return sReturnInfo;
			}

			bRun = true;

			// 1、订单到ERP
			List<Wrapper<HolidayOrderCreateRequest.RequestBean>> orders = fetchTop20HolidayOrders("1");
			if (orders == null || orders.isEmpty())
			{
				log("订单上传HolidayShoporderCreate_V3请求T100无数据");
			}
			/*if (orders!=null&&!orders.isEmpty())
            {
                for (Wrapper<HolidayOrderCreateRequest.RequestBean> order : orders)
                {
                    sReturnInfo = uploadHolidayOrder(order);
                }
            }*/
            while (!orders.isEmpty() && ++loopCount <= 5)
            {
                for (Wrapper<HolidayOrderCreateRequest.RequestBean> order : orders)
                {
                    sReturnInfo = uploadHolidayOrder(order);
                }
                orders = fetchTop20HolidayOrders("1");
                Thread.sleep(500);
            }

			//上传退单
            if (orders != null)
            {
                orders.clear();
            }
            orders = fetchTop20HolidayOrders("-1");
            if (orders == null || orders.isEmpty())
            {
                log("退单上传HolidayShoporderCreate_V3请求T100无数据");
            }
            if (orders!=null&&!orders.isEmpty())
            {
                for (Wrapper<HolidayOrderCreateRequest.RequestBean> order : orders)
                {
                    sReturnInfo = uploadHolidayOrder(order);
                }
            }
            //上传退单
            if (orders != null)
            {
                orders.clear();
                orders=null;
            }
		}
		catch (Exception e)
		{
			try
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);

				pw.flush();
				pw.close();

				errors.flush();
				errors.close();

				log("订单/退单上传HolidayShoporderCreate_V3异常" + e.getMessage()+"\r\n" + errors.toString());

				pw=null;
				errors=null;
			}
			catch (IOException e1)
			{

			}
		}
		finally
		{
			bRun=false;
			log("【同步任务HolidayShoporderCreate_V3】同步End！");
		}

		return sReturnInfo;
	}

	private String uploadHolidayOrder(Wrapper<HolidayOrderCreateRequest.RequestBean> wrapper)
	{
		String sReturnInfo = "";
		HolidayOrderCreateRequest.RequestBean order = wrapper.getObj();
		String eId = wrapper.geteId();
		String rShopId = order.getSite_no();
		String organizationNO = order.getSite_no();
		String porderNO100 = order.getFront_no();

		//退单判断原单有没有上传,不需要了，查询的时候已经分开了，退订单查询条件，是原单必须已上传
		/*if (order.getOrder_type()!=null && order.getOrder_type().equals("-1"))
		{
			String sqlSourceno="select PROCESS_STATUS from dcp_order where orderno='"+order.getRefundSourceBillNo()+"' AND EID='"+eId+"' ";
			try
			{
				List<Map<String, Object>> data_Source = this.doQueryData(sqlSourceno, null);

				if (data_Source!=null && data_Source.size()>0)
				{
					if (data_Source.get(0).get("PROCESS_STATUS")==null || data_Source.get(0).get("PROCESS_STATUS").toString().equals("Y")==false)
					{
						return "退单的原订单未上传！";
					}
				}

				//
				if (data_Source != null)
				{
					data_Source.clear();
					data_Source=null;
				}
			}
			catch (Exception e)
			{

			}
		}*/


		//   String strtemp;
		//  strtemp="\"std_data\":{\"parameter\": {";
		String str = "{\"std_data\":{\"parameter\":{\"request\":["+JSON.toJSONString(order)+"]}}}";

		log("订单上传HolidayShoporderCreate_V3请求T100传入参数：" + str);


		// region 写下日志

		List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();

		orderStatusLog onelv1 = new orderStatusLog();

		onelv1.setLoadDocType(order.getLoadDocType());
		onelv1.setChannelId(order.getChannelId());

		onelv1.setNeed_callback("N");
		onelv1.setNeed_notify("N");

		onelv1.seteId(eId);

		String opNO = "";

		String o_opName = "系统自动";

		onelv1.setOpNo(opNO);
		onelv1.setOpName(o_opName);
		onelv1.setOrderNo(order.getFront_no());
		onelv1.setLoadDocBillType(order.getLoadDocBillType());
		onelv1.setLoadDocOrderNo(order.getLoadDocOrderNo());

		String statusType = "999";// 其他状态
		String updateStaus = "999";// 订单修改

		onelv1.setStatusType(statusType);
		onelv1.setStatus(updateStaus);

		String statusName = "订单上传";
		String statusTypeName = "其他状态";
		onelv1.setStatusTypeName(statusTypeName);
		onelv1.setStatusName(statusName);

		StringBuffer memo_s = new StringBuffer(statusTypeName + "-->" + statusName + "<br>");

	   // endregion





		String resbody = "";
		try
		{
			//resbody = HttpSend.Send(str, "HolidayShoporderCreate_V3", eId, rShopId, organizationNO, porderNO100);
			resbody = HttpSend.Send(str, "holidayorder.create", eId, rShopId, organizationNO, porderNO100);
			log("订单新建/修改HolidayShoporderCreate_V3请求T100返回数据：" + resbody);
			if (Check.Null(resbody) || resbody.isEmpty())
			{
				InsertWSLOG.insert_WSLOG("holidayorder.create", porderNO100, eId, organizationNO, "1", str, resbody, "-1", "");
				memo_s .append("订单上传失败(返回为空)");
				onelv1.setMemo(memo_s.toString());
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);


				if (nRet_s)
				{
					HelpTools.writelog_fileName("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + porderNO100,jddjLogFileName);
				} else
				{
					HelpTools.writelog_fileName(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + porderNO100,jddjLogFileName);
				}

				return null;
			}

			JSONObject jsonres = new JSONObject(resbody);
			JSONObject std_data_res = jsonres.getJSONObject("std_data");
			JSONObject execution_res = std_data_res.getJSONObject("execution");
			String code = execution_res.getString("code");


			String description = "";
			if (!execution_res.isNull("description"))
			{
				description = execution_res.getString("description");
			}
			if (code.equals("0"))
			{
				maintainProcessStatus(eId,organizationNO,porderNO100);
				sReturnInfo = "0";

				memo_s .append("订单上传成功");
				onelv1.setMemo(memo_s.toString());
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
				if (nRet_s)
				{
					HelpTools.writelog_fileName("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + porderNO100,jddjLogFileName);
				} else
				{
					HelpTools.writelog_fileName(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + porderNO100,jddjLogFileName);
				}

			}
			else
			{
				sReturnInfo = "ERP返回错误信息:" + code + "," + description;
				//写数据库
				InsertWSLOG.insert_WSLOG("holidayorder.create", porderNO100, eId, organizationNO, "1", str, resbody, code, description);

				memo_s .append("订单上传失败("+sReturnInfo+")");
				onelv1.setMemo(memo_s.toString());
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
				if (nRet_s)
				{
					HelpTools.writelog_fileName("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + porderNO100,jddjLogFileName);
				}
				else
				{
					HelpTools.writelog_fileName(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + porderNO100,jddjLogFileName);
				}


			}

			//
			execution_res=null;
			std_data_res=null;
			jsonres=null;

		}
		catch (Exception e)
		{
			sReturnInfo = "错误信息:" + e.getMessage();
			log("订单新建/修改HolidayShoporderCreate_V3上传异常：" + sReturnInfo);
			try
			{
				InsertWSLOG.insert_WSLOG("holidayorder.create", porderNO100, eId, organizationNO, "1", str, resbody, "-1", e.getMessage());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			try
			{
				memo_s .append("订单上传异常("+sReturnInfo+")");
				onelv1.setMemo(memo_s.toString());
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet_s = HelpTools.InsertOrderStatusLog(StaticInfo.dao, orderStatusLogList, errorMessage);
				if (nRet_s)
				{
					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + porderNO100);
				}
				else
				{
					HelpTools.writelog_waimai(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + porderNO100);
				}

			}
			catch (Exception e2)
			{

			}
		}

		//
		onelv1=null;
		orderStatusLogList.clear();
		orderStatusLogList=null;
		order=null;

		return sReturnInfo;
	}

	private void maintainProcessStatus( String eId, String organizationNO, String orderNo)
	{
		try
		{
			// values
			Map<String, DataValue> values = new HashMap<String, DataValue>();
			values.put("process_status", new DataValue("Y", Types.VARCHAR));
			DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
			values.put("UPDATE_TIME", v1);
			values.put("TRAN_TIME", v1);
			// condition
			Map<String, DataValue> conditions = new HashMap<String, DataValue>();
			conditions.put("EID", new DataValue(eId, Types.VARCHAR));
			conditions.put("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			this.doUpdate("DCP_ORDER", values, conditions);

			//删除WS日志  By jzma 20201120
			String deleteShop =organizationNO;
			if (Check.Null(deleteShop))
				deleteShop=" ";
			InsertWSLOG.delete_WSLOG(eId, deleteShop,"1",orderNo);
		}
		catch (Exception e)
		{
			log("维护处理状态异常：" + e.getMessage());
		}
	}

	private List<Wrapper<HolidayOrderCreateRequest.RequestBean>> fetchTop20HolidayOrders(String billType ) throws Exception
	{
	    if (billType==null||billType.isEmpty())
        {
            billType = "1";
        }
		List<Wrapper<HolidayOrderCreateRequest.RequestBean>> orderList = new ArrayList<>();
		List<Wrapper<HolidayOrderCreateRequest.RequestDetailBean>> detailList = new ArrayList<>();
		List<Wrapper<HolidayOrderCreateRequest.MessagesBean>> messageList = new ArrayList<>();
		List<Wrapper<HolidayOrderCreateRequest.AgioInfo>> agioList = new ArrayList<>();
		String paraItem = "IsUploadWaimai";//是否上传外卖
        String isUploadWaimai = "Y";//默认都是Y
		String sql_para = " select ITEMVALUE from platform_basesettemp where status='100' and  item='IsUploadWaimai' ";
        List<Map<String, Object>> getUploadPara = this.doQueryData(sql_para.toString(), null);
        log("查询外卖渠道类型订单是否上传参数sql:" + sql_para);
        if (getUploadPara!=null&&!getUploadPara.isEmpty())
        {
            if ("N".equals(getUploadPara.get(0).getOrDefault("ITEMVALUE","").toString()))
            {
                isUploadWaimai = "N";
            }
        }
        log("查询外卖渠道类型订单是否上传参数值IsUploadWaimai=" + isUploadWaimai);

		// 单头
		//String sql = " select * from (select A.*,row_number()over(order by create_Datetime desc) rn from DCP_ORDER A where  A.process_status='N' )where rn<=20 ";
        String logStartStr = "查询【订单】上传sql:";
		String waimaiDocTypeCon = "'"+orderLoadDocType.ELEME+"','"+orderLoadDocType.MEITUAN+"','"+orderLoadDocType.JDDJ+"','"+orderLoadDocType.MTSG+"','"+orderLoadDocType.DYWM+"'";
		//增加订单上传ERP的白名单管控  BY JZMA 20201203
		StringBuffer sql =new StringBuffer("");
        sql.append(" select * from (");
        sql.append(" select A.* ,row_number()over(order by A.SHIPTYPE_P asc, A.LOADDOCTYPE_P asc, A.create_Datetime desc) rn FROM (");
        //sql.append(" select A.*,row_number()over(order by A.LOADDOCTYPE desc, A.create_Datetime desc) rn ");
        sql.append(" select A.*,case when A.LOADDOCTYPE in ('POS','POSANDROID') then 0 when A.LOADDOCTYPE in ('MINI','WECHAT') then 1 when A.LOADDOCTYPE in ('MEITUAN','ELEME','JDDJ') then 99 else 2 end as LOADDOCTYPE_P,  ");
        sql.append(" case when A.SHIPTYPE=5 then 0  else 2 end as SHIPTYPE_P ");
        sql.append(" from DCP_ORDER A ");
        sql.append(" left join dcp_orderbasicsetting b on a.eid=b.eid and b.settingno='orderTransferErp' and b.status='100' ");
        sql.append(" left join dcp_ordertransfererpset c on a.eid=c.eid and a.shop=c.shop ");
        sql.append(" where A.process_status='N' ");
        sql.append(" and A.BILLTYPE ='"+billType+"' ");
        if ("-1".equals(billType))
        {
            logStartStr = "查询【退单】上传sql:";
            //目前退订单，都有原单，原单已上传了，才需要上传退订单
            sql.append(" and A.refundsourcebillno in (select orderno from DCP_ORDER where billType='1' and process_status='Y') ");
        }
        sql.append(" and A.DOWNGRADED<>'Y' ");
        sql.append(" and A.EXCEPTIONSTATUS='N' ");
        if ("N".equals(isUploadWaimai))
        {
            //外卖的渠道类型不上传
            sql.append(" and (A.Loaddoctype not in ("+waimaiDocTypeCon+") )");
        }
        else
        {
            sql.append(" and (A.Loaddoctype not in ("+waimaiDocTypeCon+") or (A.Loaddoctype in ("+waimaiDocTypeCon+") and A.shop<>' '  and A.Shop is not null))");
        }

        sql.append(" and (b.settingvalue='N' or b.settingvalue is null or (b.settingvalue='Y' and c.shop is not null)) ");
        sql.append(" ) A");
        sql.append(" )");
        sql.append(" where rn<=200 ");

		log(logStartStr + sql.toString());
		List<Map<String, Object>> data = this.doQueryData(sql.toString(), null);
		for (Map<String, Object> map : data)
		{
			HolidayOrderCreateRequest.RequestBean b = ConvertUtils.mapToBean(map, HolidayOrderCreateRequest.RequestBean.class);
			String isIntention = map.getOrDefault("ISINTENTION","N").toString();
			if (!"Y".equals(isIntention))
            {
                b.setIsIntention("N");
            }
			b.setVersion("3.0");
			b.setOperation_type("0");
			b.setOrder_type(map.get("BILLTYPE").toString());
			b.setFront_no(map.get("ORDERNO").toString());
			b.setCustomer_no(map.get("SELLNO").toString());
			b.setAtmvirtualaccount(map.get("VIRTUALACCOUNTCODE").toString());
			b.setShippingshop(map.get("SHIPPINGSHOP").toString());
			b.setShippingshopname(map.get("SHIPPINGSHOPNAME").toString());
			b.setProductionshop(map.get("MACHSHOP").toString());//生产门店
			b.setPickupway("");
			b.setBuyerguino(map.get("BUYERGUINO").toString());
			b.setShiptype(map.get("SHIPTYPE").toString());
			b.setGetman(map.get("GETMAN").toString());
			b.setGetmantel(map.get("GETMANTEL").toString());
			b.setShiptime(ConvertUtils.toStr(map.get("SHIPTIME")));
			String SHIPTYPE = map.get("SHIPTYPE").toString();
			String ISSHIPCOMPANY = map.get("ISSHIPCOMPANY").toString();
			//POS总部生产填的ISSHIPCOMPANY 为Y
			b.setType(ISSHIPCOMPANY.equals("Y") ? "2" :"1");
			b.setSite_no(map.get("SHOP").toString());
			//是否是需求日期
			String SHIPDATE = map.get("SHIPDATE").toString();
			//这里判断一下需求日期是否为空
			if (SHIPDATE != null && !SHIPDATE.isEmpty()) {
				SHIPDATE = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(SHIPDATE));
			}
			b.setAgreee_date(SHIPDATE);
			b.setSid(map.get("MANUALNO").toString());
			b.setInvuser(map.get("CONTMAN").toString());
			b.setPhone(map.get("CONTTEL").toString());
			b.setAddress(map.get("ADDRESS").toString());
			b.setCreator("");

			String CREATE_DATETIME = map.get("CREATE_DATETIME").toString();
			CREATE_DATETIME = CREATE_DATETIME.substring(0, 14);
			CREATE_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(CREATE_DATETIME));
			b.setCreate_datetime(CREATE_DATETIME);

			// todo 需要确认 CREATE_DATETIME => yyyy-MM-dd
			/* String SDATE = map.get("SDATE").toString();
		            SDATE = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(SDATE));
		            b.setOrder_date(SDATE);*/
			// 日期格式yyyy-MM-dd   截取订单创建时间create_datetime字段里的日期  BY JZMA 20201123
			Date day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(CREATE_DATETIME);
			String orderDate = new SimpleDateFormat("yyyy-MM-dd").format(day);
			b.setOrder_date(orderDate);

			b.setModify_no("");
			b.setModify_datetime("");
			b.setApprove_no("");
			b.setApprove_datetime("");
			b.setMemo(map.get("MEMO").toString());
			String ISORGORDER = map.get("ISORGORDER").toString();
			if (ISORGORDER == null || ISORGORDER.isEmpty()) {
				ISORGORDER = "N";
			}
			b.setIsholidayorder(ISORGORDER);
			b.setRequest_detail(new ArrayList<>());
			//超商取货
			String subtype_name = "";
			String DeliveryType = map.get("DELIVERYTYPE").toString();
			if (SHIPTYPE.equals("6")) {
				if (DeliveryType.equals("16")) {
					subtype_name = "綠界7-11";
				}
				if (DeliveryType.equals("17")) {
					subtype_name = "綠界全家";
				}
				if (DeliveryType.equals("18")) {
					subtype_name = "綠界萊爾富";
				}
			}
			b.setSubtype_name(subtype_name);
			b.setInvoiceType(map.get("INVOPERATETYPE").toString());
			b.setInvNo(map.get("INVNO").toString());
			//【ID1014119】【订单3.0】对接ERP接口字段增加（商家实收用户实付涉及改动服务）
			b.setTot_amt_merReceive(map.get("TOT_AMT_MERRECEIVE").toString());
			b.setTot_amt_custPayReal(map.get("TOT_AMT_CUSTPAYREAL").toString());
			b.setTot_disc_merReceive(map.get("TOT_DISC_MERRECEIVE").toString());
			b.setTot_disc_custPayReal(map.get("TOT_DISC_CUSTPAYREAL").toString());
			b.setDepartNo(map.getOrDefault("DEPARTNO", "").toString());

			//发票试算单头数据
			StringBuffer sql_INVOICEPRE=new StringBuffer("select * from DCP_INVOICEPRE where SALETYPE='Order' and SALENO='"+map.get("ORDERNO").toString()+"' ");

			List<Map<String, Object>> data_INVOICEPRE = this.doQueryData(sql_INVOICEPRE.toString(), null);
			if (data_INVOICEPRE!=null && data_INVOICEPRE.size()>0)
			{
				b.setTaxAbleUamt(data_INVOICEPRE.get(0).get("TAXABLEUAMT").toString());
				b.setZeroTaxAmt(data_INVOICEPRE.get(0).get("ZEROTAXAMT").toString());
				b.setFreeTaxAmt(data_INVOICEPRE.get(0).get("FREETAXAMT").toString());
				b.setTaxAbleAmt(data_INVOICEPRE.get(0).get("TAXABLEAMT").toString());
				b.setTaxAbleTax(data_INVOICEPRE.get(0).get("TAXABLETAX").toString());
				b.setUntaxPayAmt(data_INVOICEPRE.get(0).get("UNTAXPAYAMT").toString());
				b.setUntaxPayTax(data_INVOICEPRE.get(0).get("UNTAXPAYTAX").toString());
				b.setTaxRate(data_INVOICEPRE.get(0).get("TAXRATE").toString());
				b.setInvTotAmt(data_INVOICEPRE.get(0).get("INVTOTAMT").toString());
				b.setInvAmt(data_INVOICEPRE.get(0).get("INVAMT").toString());
				b.setInvUamt(data_INVOICEPRE.get(0).get("INVUAMT").toString());
				b.setInvTax(data_INVOICEPRE.get(0).get("INVTAX").toString());
				b.setGftInvAmt(data_INVOICEPRE.get(0).get("GFTINVAMT").toString());
				b.setGftInvTax(data_INVOICEPRE.get(0).get("GFTINVTAX").toString());
				b.setAccAmt(data_INVOICEPRE.get(0).get("ACCUMAMT").toString());
				b.setAccTax(data_INVOICEPRE.get(0).get("ACCUMTAX").toString());
				b.setExtraCpAmt(data_INVOICEPRE.get(0).get("EXTRACPAMT").toString());
				b.setExtraCpTax(data_INVOICEPRE.get(0).get("EXTRACPTAX").toString());
			}

			//【ID1013581】订单配送地址增加省市区，优先处理，今天能把webservice改好，这样ERP才能改，否正周一达不到切换条件 BY JZMA 20201207
			b.setProvince(map.get("PROVINCE").toString());
			b.setCity(map.get("CITY").toString());
			b.setCounty(map.get("COUNTY").toString());
			b.setStreet(map.get("STREET").toString());
			b.setStatus(map.get("STATUS").toString());
			b.setPayStatus(map.get("PAYSTATUS").toString());


			Wrapper<HolidayOrderCreateRequest.RequestBean> w = new Wrapper<HolidayOrderCreateRequest.RequestBean>(
					map.get("EID").toString(), map.get("ORDERNO").toString(),  b);
			orderList.add(w);

			//
            w=null;
            b=null;
		}

		if (!orderList.isEmpty())
		{
			String inSql = "(" + orderList.stream().map(Wrapper::getInKey).collect(Collectors.joining(",")) + ")";
			// 单身
			sql.setLength(0);
			sql.append("select a.* from DCP_ORDER_DETAIL a where a.QTY>=0 and (a.EID, a.ORDERNO) in " + inSql);
			data = this.doQueryData(sql.toString(), new String[]{});
			data.forEach(mapdetail->
			{
				HolidayOrderCreateRequest.RequestDetailBean d = ConvertUtils.mapToBean(mapdetail, HolidayOrderCreateRequest.RequestDetailBean.class);
				d.setSeq(mapdetail.get("ITEM").toString());
				d.setItem_no(mapdetail.get("PLUNO").toString());
				d.setsUnit(mapdetail.get("SUNIT").toString());
				d.setQty(mapdetail.get("QTY").toString());
				d.setOriprice(mapdetail.get("OLDPRICE").toString());
				d.setQrcode_key(ConvertUtils.toStr(mapdetail.get("SOURCECODE_DETAIL")));

				String PICKQTY=mapdetail.get("PICKQTY").toString();
				String SHOPQTY=mapdetail.get("SHOPQTY").toString();
				if (PICKQTY == null || PICKQTY.isEmpty()) {
					PICKQTY = "0";
				}
				if (SHOPQTY == null || SHOPQTY.isEmpty()) {
					SHOPQTY = "0";
				}
				//double shopqtytemp=Double.parseDouble(PICKQTY)+Double.parseDouble(SHOPQTY);
                double shopqtytemp=Double.parseDouble(SHOPQTY);
				d.setShopqty(PosPub.GetdoubleScale(shopqtytemp,2)+"" );
				//这里可以计算一下实际价格
				double AMT=Double.parseDouble(mapdetail.get("AMT").toString());
				double QTY=Double.parseDouble(mapdetail.get("QTY").toString());
				try
                {
                    d.setPrice(PosPub.GetdoubleScale(AMT/QTY, 2)+"");
                }
				catch (Exception e)
                {
                    d.setPrice("0");
                }

				d.setAmount(mapdetail.get("AMT").toString());
				d.setInvItem(mapdetail.get("INVITEM").toString());
				d.setInvSplitType(mapdetail.get("INVSPLITTYPE").toString());
				//【ID1014119】【订单3.0】对接ERP接口字段增加（商家实收用户实付涉及改动服务）
				d.setDisc_merReceive(mapdetail.get("DISC_MERRECEIVE").toString());
				d.setAmt_merReceive(mapdetail.get("AMT_MERRECEIVE").toString());
				d.setDisc_custPayReal(mapdetail.get("DISC_CUSTPAYREAL").toString());
				d.setAmt_custPayReal(mapdetail.get("AMT_CUSTPAYREAL").toString());


                //处理==绑定变量SQL的写法
                List<DataValue> lstDV=new ArrayList<>();
                DataValue dv=null;

				//发票试算商品
				String sql_INVOICEPRE_GOODS="select * from DCP_INVOICEPRE_GOODS where SALETYPE='Order' and SALENO=? and ITEM=? ";

                //?问号参数赋值处理
                dv=new DataValue(mapdetail.get("ORDERNO").toString(),Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(mapdetail.get("ITEM").toString(),Types.INTEGER);
                lstDV.add(dv);

				List<Map<String, Object>> data_INVOICEPRE_GOODS=null;
				try
				{
					data_INVOICEPRE_GOODS = this.executeQuerySQL_BindSQL(sql_INVOICEPRE_GOODS, lstDV);
				}
				catch (Exception e)
				{
					log("订单新建/修改HolidayShoporderCreate_V3发票试算商品查询异常:" +e.getMessage());
				}
				if (data_INVOICEPRE_GOODS!=null && data_INVOICEPRE_GOODS.size()>0)
				{
					d.setGftInvAmt(data_INVOICEPRE_GOODS.get(0).get("GFTINVAMT").toString());
					d.setGftInvTax(data_INVOICEPRE_GOODS.get(0).get("GFTINVTAX").toString());
					d.setTaxAbleUamt(data_INVOICEPRE_GOODS.get(0).get("TAXABLEUAMT").toString());
					d.setZeroTaxAmt(data_INVOICEPRE_GOODS.get(0).get("ZEROTAXAMT").toString());
					d.setFreeTaxAmt(data_INVOICEPRE_GOODS.get(0).get("FREETAXAMT").toString());
					d.setTaxAbleAmt(data_INVOICEPRE_GOODS.get(0).get("TAXABLEAMT").toString());
					d.setOldAmt(data_INVOICEPRE_GOODS.get(0).get("AMT").toString());
					d.setUntaxPayAmt(data_INVOICEPRE_GOODS.get(0).get("UNTAXPAYAMT").toString());
					d.setUntaxPayTax(data_INVOICEPRE_GOODS.get(0).get("UNTAXPAYTAX").toString());
					d.setInvTotAmt(data_INVOICEPRE_GOODS.get(0).get("INVTOTAMT").toString());
					d.setInvAmt(data_INVOICEPRE_GOODS.get(0).get("INVAMT").toString());
					d.setInvUamt(data_INVOICEPRE_GOODS.get(0).get("INVUAMT").toString());
					d.setInvTax(data_INVOICEPRE_GOODS.get(0).get("INVTAX").toString());
					d.setAccAmt(data_INVOICEPRE_GOODS.get(0).get("ACCUMAMT").toString());
					d.setAccTax(data_INVOICEPRE_GOODS.get(0).get("ACCUMTAX").toString());
					d.setExtraCpAmt(data_INVOICEPRE_GOODS.get(0).get("EXTRACPAMT").toString());
					d.setExtraCpTax(data_INVOICEPRE_GOODS.get(0).get("EXTRACPTAX").toString());
				}


				Wrapper<HolidayOrderCreateRequest.RequestDetailBean> wrapper = new Wrapper<HolidayOrderCreateRequest.RequestDetailBean>(mapdetail.get("EID").toString(),
						mapdetail.get("ORDERNO").toString(), mapdetail.get("ITEM").toString(), d);
				detailList.add(wrapper);

				//
				wrapper=null;
				d=null;

			});

			// 消息
			sql.setLength(0);
			sql.append("select a.* from DCP_ORDER_DETAIL_MEMO a where (a.EID, a.ORDERNO) in " + inSql);
			data = this.doQueryData(sql.toString(), new String[]{});
			data.forEach(map->{
				HolidayOrderCreateRequest.MessagesBean b = ConvertUtils.mapToBean(map, HolidayOrderCreateRequest.MessagesBean.class);
				b.setMsgtype(map.get("MEMOTYPE").toString());
				b.setMsgname(map.get("MEMONAME").toString());
				b.setMessage(map.get("MEMO").toString());
				Wrapper<HolidayOrderCreateRequest.MessagesBean> wrapper = new Wrapper<HolidayOrderCreateRequest.MessagesBean>(map.get("EID").toString(),
						map.get("ORDERNO").toString(),  map.get("OITEM").toString(), b);
				messageList.add(wrapper);

				//
				wrapper=null;
				b=null;
			});

			// 优惠
			sql.setLength(0);
			sql.append("select a.* from DCP_ORDER_DETAIL_AGIO a where (a.EID, a.ORDERNO) in " + inSql);
			data = this.doQueryData(sql.toString(), new String[]{});
			data.forEach(m->{
				HolidayOrderCreateRequest.AgioInfo b = ConvertUtils.mapToBean(m, HolidayOrderCreateRequest.AgioInfo.class);
				b.setItem(m.get("ITEM").toString());
				b.setQty(m.get("QTY").toString());
				b.setAmt(m.get("AMT").toString());
				b.setInputDisc(m.get("INPUTDISC").toString());
				b.setRealDisc(m.get("REALDISC").toString());
				b.setDisc(m.get("DISC").toString());
				b.setDcType(m.get("DCTYPE").toString());
				b.setDcTypeName(m.get("DCTYPENAME").toString());
				b.setPmtNo(m.get("PMTNO").toString());
				b.setBsNo(m.get("BSNO").toString());
				//【ID1014119】【订单3.0】对接ERP接口字段增加（商家实收用户实付涉及改动服务）
				b.setDisc_merReceive(m.get("DISC_MERRECEIVE").toString());
                b.setDisc_custPayReal(m.get("DISC_CUSTPAYREAL").toString());

				Wrapper<HolidayOrderCreateRequest.AgioInfo> wrapper = new Wrapper<HolidayOrderCreateRequest.AgioInfo>(m.get("EID").toString(),
						m.get("ORDERNO").toString(),  m.get("MITEM").toString(), b);
				agioList.add(wrapper);

				//
				wrapper=null;
				b=null;

			});

			// 组装数据
			Map<String, Wrapper<HolidayOrderCreateRequest.RequestDetailBean>> deailMap = detailList.stream().collect(Collectors.toMap(Wrapper::getItemKey, v -> v));
			Map<String, List<Wrapper<HolidayOrderCreateRequest.RequestDetailBean>>> deailMap2 = detailList.stream().collect(Collectors.groupingBy(Wrapper::getOrderKey));

			//循环订单
			for (Wrapper<HolidayOrderCreateRequest.RequestBean> it : orderList)
			{
				List<Wrapper<HolidayOrderCreateRequest.RequestDetailBean>> w = deailMap2.get(it.getOrderKey());
				if(w != null)
				{
					it.getObj().setRequest_detail(w.stream().map(Wrapper::getObj).collect(Collectors.toList()));
				}
				w=null;
			}

			// 循环message
			for (Wrapper<HolidayOrderCreateRequest.MessagesBean> it : messageList)
			{
				Wrapper<HolidayOrderCreateRequest.RequestDetailBean> w = deailMap.get(it.getItemKey());
				if(w != null)
				{
					w.getObj().getMessages().add(it.getObj());
				}
				w=null;
			}

			//循环折扣
			for (Wrapper<HolidayOrderCreateRequest.AgioInfo> it : agioList)
			{
				Wrapper<HolidayOrderCreateRequest.RequestDetailBean> w = deailMap.get(it.getItemKey());
				if(w != null)
				{
					w.getObj().getAgio_info().add(it.getObj());
				}
				w=null;
			}

			deailMap2=null;
			deailMap=null;

		}

		return orderList;
	}

	public static final String jddjLogFileName = "HolidayShoporderCreate_V3";
	public void log(String message)
	{
		try
		{
			HelpTools.writelog_fileName(message, jddjLogFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class Wrapper<T>{
		private String eId;
		private String orderNo;
		private String upItem;
		private T obj;

		public Wrapper() {
		}

		public Wrapper(String eId, String orderNo) {
			this.eId = eId;
			this.orderNo = orderNo;
		}

		public Wrapper(String eId, String orderNo, String upItem) {
			this.eId = eId;
			this.orderNo = orderNo;
			this.upItem = upItem;
		}

		public Wrapper(String eId, String orderNo, T obj) {
			this.eId = eId;
			this.orderNo = orderNo;
			this.obj = obj;
		}

		public Wrapper(String eId, String orderNo, String upItem, T obj) {
			this.eId = eId;
			this.orderNo = orderNo;
			this.upItem = upItem;
			this.obj = obj;
		}

		public String getItemKey() {
			String key = eId + "_" + orderNo;
			if (upItem != null && !upItem.isEmpty()) {
				key += "_" + upItem;
			}

			return key;
		}

		public String getInKey(){
			return  "('" + eId + "','" + orderNo +"')";
		}

		public String getOrderKey() {
			return eId + "_" + orderNo;
		}

		public String geteId() {
			return eId;
		}

		public void seteId(String eId) {
			this.eId = eId;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		public T getObj() {
			return obj;
		}

		public void setObj(T obj) {
			this.obj = obj;
		}

		public String getItem() {
			return upItem;
		}

		public void setItem(String item) {
			this.upItem = item;
		}
	}
}
