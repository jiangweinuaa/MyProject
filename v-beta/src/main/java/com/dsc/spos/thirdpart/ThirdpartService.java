package com.dsc.spos.thirdpart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;

public class ThirdpartService {
	/**
	 * 写订单历程(物流回调)
	 * @param dao
	 * @param orderNo 订单号
	 * @param deliveryType 物流类型
	 * @param statusType 状态类型 (1-订单状态 2-配送状态 3-退单状态)
	 * @param status 状态类型对应的状态
	 * @param disMobile 配送员手机号
	 * @param opName 用户名称 可以给值如"骑士：XXX"
	 * @param otherDes 其他描述，如"超出配送范围，退单"
	 * @throws Exception
	 */
	public void save(DsmDAO dao, String orderNo, String deliveryType, String statusType, String status,
			Object disMobile, String opName, String otherDes) throws Exception {

		try {
			String sqlTvOrder = "select * from DCP_ORDER where ORDERNO='"+orderNo+"' and DELIVERYTYPE='"+deliveryType+"' ";
			List<Map<String, Object>> listTvOrder = dao.executeQuerySQL(sqlTvOrder,null);
			if (listTvOrder == null || listTvOrder.size() == 0) {
				listTvOrder = dao.executeQuerySQL("select * from DCP_ORDER where ORDERNO=? ",
						new String[] { orderNo });
			}
			if (listTvOrder != null && listTvOrder.size() > 0) {
				String opNO = "";
				boolean isNeedNotifyCrm =false;
				// region订单状态
				// 门店
				String shop = listTvOrder.get(0).get("SHOP").toString();
				String eId = listTvOrder.get(0).get("EID").toString();
				String loadDocType = listTvOrder.get(0).getOrDefault("LOADDOCTYPE","").toString();
				String shippingShop = listTvOrder.get(0).getOrDefault("SHIPPINGSHOP","").toString();
				String channelId = listTvOrder.get(0).getOrDefault("CHANNELID","").toString();

                if (!"KDN".equals(deliveryType))
				{
					//快递鸟，回调已经同步过了。
					if (orderLoadDocType.MINI.equals(loadDocType)||orderLoadDocType.WECHAT.equals(loadDocType)||orderLoadDocType.LINE.equals(loadDocType))
					{
						if ("1".equals(status)||"2".equals(status)||"3".equals(status)||"4".equals(status)||"6".equals(status))
						{
							////-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
							isNeedNotifyCrm = true;
						}
					}
				}




				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.seteId(listTvOrder.get(0).get("EID").toString());
				onelv1.setShopNo(shop);
				onelv1.setOrderNo(orderNo);
				onelv1.setOpNo(opNO);
				onelv1.setOpName(opName);
				onelv1.setLoadDocType(listTvOrder.get(0).get("LOADDOCTYPE") == null ? ""
						: listTvOrder.get(0).get("LOADDOCTYPE").toString());
				// 状态类型 1-订单状态 2-配送状态 3-退单状态
				onelv1.setStatusType(statusType);

				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, status, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);
				onelv1.setStatus(status);
				if (status.isEmpty())
				{
					onelv1.setStatusName("其他");
					onelv1.setStatus("99");
				}

				String memo = "";
				if (otherDes == null ||otherDes.trim().isEmpty()) {
					memo += statusTypeName + "-->" + statusName+"<br>";
				}

				if (disMobile != null && !disMobile.toString().trim().isEmpty()) {
					memo += "配送电话-->" + disMobile+"<br>";
				}
				if (otherDes != null && !otherDes.trim().isEmpty()) {
					memo += "" + otherDes;
				}
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				// 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
				onelv1.setCallback_status("0");
				// 是否调用第三方接口，N-不需要调用，Y-需要
				onelv1.setNeed_callback("N");
				// 是否通知云pos,N-不需要调用，Y-需要
				onelv1.setNeed_notify("N");

				List<orderStatusLog> req_log = new ArrayList<orderStatusLog>();
				req_log.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(dao, req_log, errorMessage);
				if (nRet) {
					Log("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else {
					Log(
							"【写表tv_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				dao.closeDAO();

				//物流状态变更通知CRM
				if (isNeedNotifyCrm)
				{
					try
					{
						org.json.JSONObject js=new org.json.JSONObject();
						js.put("serviceId", "OrderStatusUpdate");
						js.put("orderNo", orderNo);
						js.put("statusType", "2");//状态类型 1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更 6=开票状态变更
						js.put("status", "");//交易状态 0=未配送 1=配送中 2=已配送    3=确认收货 4=已取消 5=已下单6=已接单
						//deliverystatus中台物流状态 -1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单
						if ("1".equals(status))
						{
							js.put("status", "6");
						}
						if ("2".equals(status)||"6".equals(status))
						{
							js.put("status", "1");//1=配送中
						}
						else if ("3".equals(status))
						{
							js.put("status", "2");//2=已配送 已签收
						}
						else if ("4".equals(status))
						{
							js.put("status", "4");//物流取消
						}
						js.put("description", otherDes);
						js.put("oprId", "admin");
						js.put("orgType", "2");
						js.put("orgId", shippingShop);
						js.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

						String req_crm = js.toString();
						HelpTools.writelog_fileName("【物流回调消息】通知商城接口请求req:"+req_crm+",对应的订单单号orderNO="+orderNo,"WULIUCallBack");
						String result_crm = HttpSend.MicroMarkSend(req_crm, eId, "OrderStatusUpdate",channelId);
						HelpTools.writelog_fileName("【物流回调消息】通知商城接口返回res:"+result_crm+",对应的订单单号orderNO="+orderNo,"WULIUCallBack");

					}
					catch (Exception e)
					{

					}
				}




			 } else {
				 Log("【物流回调WULIUCallBack】 单号:" + orderNo + "查无资料");
			 }
		} catch (Exception e) {

		}
	}
	
	public void Log(String log) throws IOException {
		writelogFileName(log, "TvOrderStatuslog");
	}
	
	// 写日志
	public void writelogFileName(String log, String fileName) throws IOException {
		//HelpTools.writelog_fileName(log, fileName);
		// 生成文件路径
		String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
		String path = System.getProperty("user.dir") + "\\log\\" + fileName + sdFormat + ".txt";
		File file = new File(path);

		String dirpath = System.getProperty("user.dir") + "\\log";
		File dirfile = new File(dirpath);
		if (!dirfile.exists()) {
			dirfile.mkdir();
		}
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream writerStream = new FileOutputStream(file,true);
		OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
		BufferedWriter writer = new BufferedWriter(osw);

		// 前面加上时间
		String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
		String slog = stFormat + " " + log + "\r\n";
		stFormat=null;

		stFormat=null;
		sdFormat=null;

		writer.append(slog);
		writer.close();
		writer=null;

		osw.close();
		osw=null;

		writerStream.close();
		writerStream=null;

		file=null;

		sdFormat=null;
	}
	


 	public String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }
	
}
