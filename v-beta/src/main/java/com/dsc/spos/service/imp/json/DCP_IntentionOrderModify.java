package com.dsc.spos.service.imp.json;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderCreateReq;
import com.dsc.spos.json.cust.res.DCP_IntentionOrderModifyRes;
import com.dsc.spos.json.cust.res.DCP_IntentionOrderModifyRes.responseDatas;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_IntentionOrderModify extends SPosAdvanceService<DCP_OrderCreateReq, DCP_IntentionOrderModifyRes>
{

	boolean bMemberPay=false;//调用会员支付标记
	@Override
	protected void processDUID(DCP_OrderCreateReq req, DCP_IntentionOrderModifyRes res) throws Exception
	{

		ParseJson pj = new ParseJson();
		String reqStr = pj.beanToJson(req);
		HelpTools.writelog_waimai("【调用订单(意向单)修改接口DCP_IntentionOrderModify】请求req内容:"+reqStr);
        res.setDatas(res.new responseDatas());
		boolean isOpen = false;//外部接口
		String token = req.getToken();
		if(token==null||token.trim().isEmpty())
		{
			isOpen = true;
		}

		if(isOpen)
		{
			String apiUseCode = req.getApiUserCode();
			String loadDocType = req.getApiUser().getAppType();
			String channelId = req.getApiUser().getChannelId();
			String orderNo = req.getRequest().getOrderNo();
			HelpTools.writelog_waimai("【外部调用订单(意向单)修改接口DCP_IntentionOrderModify】接口账号apiUseCode="+apiUseCode+"，渠道类型loadDocType="+loadDocType+"，渠道编码channelId="+channelId+"，传入的渠道类型节点渠道类型loadDocType="+req.getRequest().getLoadDocType()+",原订单号orderNo="+orderNo);
			if ("KDS".equalsIgnoreCase(loadDocType))
			{
				if (req.getRequest().getLoadDocType() != null && !req.getRequest().getLoadDocType().trim().isEmpty())
				{
					if (!req.getRequest().getLoadDocType().equals(loadDocType))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "接口账号对应的渠道类型["+loadDocType+"]与传入的渠道类型节点loadDocType["+req.getRequest().getLoadDocType()+"]不一致，请检查接口账号是否使用正确！");
					}
				}
			}

			req.getRequest().seteId(req.geteId());
		}

		if (req.getRequest().geteId()==null||req.getRequest().geteId().isEmpty())
		{
			req.getRequest().seteId(req.geteId());
		}
		boolean isFail = false ;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getRequest().getOrderNo()))
		{
			errMsg.append("原订单单号orderNo不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getShopNo()))
		{
			errMsg.append("下订门店编号shopNo不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getShopName()))
		{
			errMsg.append("下订门店名称shopName不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		try
		{
			String langType = req.getLangType();
			if(langType==null||langType.isEmpty())
			{
				langType = "zh_CN";
			}
			String eId = req.getRequest().geteId();
			String orderNo = req.getRequest().getOrderNo();
			String sql = "Select * from DCP_ORDER  WHERE EID='" + eId + "'  and ORDERNO='" + orderNo + "' ";
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData == null || getQData.isEmpty())
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该订单(意向单)不存在，无法修改！");
				return;
			}
			Map<String, Object> mapOrder = getQData.get(0);
			String loadDocType = mapOrder.get("LOADDOCTYPE").toString();
			String channelId = mapOrder.get("CHANNELID").toString();
			String orderStatus = mapOrder.get("STATUS").toString();
			String isIntention = mapOrder.get("ISINTENTION").toString();
			String shopNo_db = mapOrder.get("SHOP").toString();
			if (!"Y".equals(isIntention))
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该订单非意向单暂不支持修改！");
				return;
			}
			if ("3".equals(orderStatus)||"11".equals(orderStatus)||"12".equals(orderStatus))
			{
				StringBuilder statusTypeName = new StringBuilder("");
				String statusType = "1";
				String statusName = HelpTools.GetOrderStatusName(statusType, orderStatus, statusTypeName);
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该订单状态是" + statusName + "，暂不支持修改！");
				return;
			}
			if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
			{

			}
			else
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该订单对应的渠道类型("+loadDocType+")暂不支持修改！");
				return;
			}
			if (!req.getRequest().getShopNo().equals(shopNo_db))
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("传入的下单门店("+req.getRequest().getShopNo()+")和原单下单门店("+shopNo_db+")不一致，不支持修改订单的下单门店！");
				return;
			}


			String orderShipType = mapOrder.get("SHIPTYPE").toString();
			String orderContMan = mapOrder.get("CONTMAN").toString();
			String orderContTel = mapOrder.get("CONTTEL").toString();
			String orderAdress = mapOrder.get("ADDRESS").toString();
			String orderGetMan = mapOrder.get("GETMAN").toString();
			String orderGetManTel = mapOrder.get("GETMANTEL").toString();
			String orderShipDate = mapOrder.get("SHIPDATE").toString();
			String orderShipStartTime = mapOrder.get("SHIPSTARTTIME").toString();
			String orderShipEndTime = mapOrder.get("SHIPENDTIME").toString();
			String orderShipShopNo = mapOrder.get("SHIPPINGSHOP").toString();
			String orderShipShopName = mapOrder.get("SHIPPINGSHOPNAME").toString();
			String orderMachShopNo = mapOrder.get("MACHSHOP").toString();
			String orderMachShopName = mapOrder.get("MACHSHOPNAME").toString();
			String orderMemo = mapOrder.get("MEMO").toString();
			String orderProMemo = mapOrder.get("PROMEMO").toString();
			String orderDelMemo = mapOrder.get("DELMEMO").toString();
			String deliverStatus = mapOrder.getOrDefault("DELIVERYSTATUS", "").toString();//配送状态
			String productStatus = mapOrder.getOrDefault("PRODUCTSTATUS", "").toString();//生产状态
			String isShipcompany_db = mapOrder.getOrDefault("ISSHIPCOMPANY", "").toString();//是否总部生产
			String isDelete_db = mapOrder.getOrDefault("ISDELETE", "").toString();

			String order_province = mapOrder.getOrDefault("PROVINCE", "").toString();
			String order_city = mapOrder.getOrDefault("CITY", "").toString();
			String order_county = mapOrder.getOrDefault("COUNTY", "").toString();
			String order_street = mapOrder.getOrDefault("STREET", "").toString();

			String order_deliveryBusinessType = mapOrder.getOrDefault("DELIVERYBUSINESSTYPE", "").toString();
			String order_isUrgentOrder = mapOrder.getOrDefault("ISURGENTORDER", "").toString();
			String order_deliveryType = mapOrder.getOrDefault("DELIVERYTYPE", "").toString();
			String order_longitude = mapOrder.getOrDefault("LONGITUDE", "").toString();
			String order_latitude = mapOrder.getOrDefault("LATITUDE", "").toString();

			String delId = mapOrder.getOrDefault("DELID", "").toString();
			String delName = mapOrder.getOrDefault("DELNAME", "").toString();
			String delTelephone = mapOrder.getOrDefault("DELTELEPHONE", "").toString();
			String packerId = mapOrder.getOrDefault("PACKERID", "").toString();
			String packerName = mapOrder.getOrDefault("PACKERNAME", "").toString();
			String packerTelephone = mapOrder.getOrDefault("PACKERTELEPHONE", "").toString();
			String canModify = mapOrder.getOrDefault("CANMODIFY", "").toString();
			String operationtype = mapOrder.getOrDefault("OPERATIONTYPE", "").toString();


			//增加菲尔雪逻辑
			//是否需要生产控制” ControlProduction    Y/N 默认值N；
			String ControlProduction = PosPub.getPARA_SMS(dao,eId,"","ControlProduction");
			//操作门店不为空，并且参数是Y
			if ("Y".equals(ControlProduction)&&!Check.Null(req.getRequest().getShopNo()))
			{
				//// operationType枚举值：0或者空 表示不可修改订单不可退订 1表示可修改订单，2表示可退单
				if ("1".equals(operationtype)==false)
				{
					//生产状态(4生产接单；5生产拒单；6完工入库；7内部调拨）
					if ("4".equals(productStatus)||"6".equals(productStatus)||"7".equals(productStatus))
					{
						//1下单机构，2生产机构，3配送机构  默认值2；
						String typeOfOrg = PosPub.getPARA_SMS(dao,eId,"","TypeOfOrganization");
						if (StrUtil.isEmpty(typeOfOrg))
						{
							typeOfOrg = "2";
						}
						String str_error = "需要生产机构同意，请联系该门店";
						boolean isCheckOrg = true;
						String shop_create = mapOrder.get("SHOP").toString();
						String shopName_create = mapOrder.get("SHOPNAME").toString();
						String shop_mach = mapOrder.get("MACHSHOP").toString();
						String shopName_mach = mapOrder.get("MACHSHOPNAME").toString();
						String shop_shipping = mapOrder.get("SHIPPINGSHOP").toString();
						String shopName_shipping = mapOrder.get("SHIPPINGSHOPNAME").toString();
						if ("1".equals(typeOfOrg))
						{
							if (shop_create!=null&&shop_create.trim().isEmpty()==false)
							{
								str_error = "需要下单机构("+shop_create+shopName_create+")同意，请联系该门店!";
								isCheckOrg = false;
							}
						}
						else if ("3".equals(typeOfOrg))
						{
							if (shop_shipping!=null&&shop_shipping.trim().isEmpty()==false)
							{
								str_error = "需要配送机构("+shop_shipping+shopName_shipping+")同意，请联系该门店!";
								isCheckOrg = false;
							}
						}
						else
						{
							if (shop_mach!=null&&shop_mach.trim().isEmpty()==false)
							{
								str_error = "需要生产机构("+shop_mach+shopName_mach+")同意，请联系该门店!";
								isCheckOrg = false;
							}
						}

						if (!isCheckOrg)
						{
							res.setSuccess(false);
							res.setServiceStatus("200");
							res.setServiceDescription(str_error);
							HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】"+str_error+"，不能修改信息！单号OrderNO=" + orderNo
									+ " 数据库中下单门店=" + shopNo_db + " 订单状态status=" + orderStatus);
							return;
						}
					}

				}

			}

			boolean isCanModify_deliverStatus = false;//是否可以修改配送相关
			if(deliverStatus==null||deliverStatus.isEmpty()||deliverStatus.equals("4")||deliverStatus.equals("5"))
			{
				//配送状态为4或5或空时才可修改配送机构
				isCanModify_deliverStatus = true;
			}
			StringBuilder statusTypeName = new StringBuilder("");
			String deliverStatusName = HelpTools.GetOrderStatusName("2", deliverStatus, statusTypeName);


			boolean isCanModify_productStatus = false;//是否可以修改生产相关
			if(productStatus==null||productStatus.isEmpty()||productStatus.equals("5"))
			{
				//生产状态为5.生产拒单或空时才可修改生产机构
				isCanModify_productStatus = true;
			}
			statusTypeName = new StringBuilder("");
			String productStatusName = HelpTools.GetOrderStatusName("4", productStatus, statusTypeName);

			boolean isUpdateGoodsDetail = false;//是不是修改了商品明细
			//查询商品明细，判断下
			sql = "";
			sql = "Select * from DCP_ORDER_DETAIL  WHERE EID='" + eId + "'  and ORDERNO='" + orderNo + "' ";
			List<Map<String, Object>> getQDataGoods = this.doQueryData(sql, null);
			if (getQDataGoods == null || getQDataGoods.isEmpty())
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("该订单(意向单)没有商品明细，无法修改！");
				return;
			}
			BigDecimal tot_amt_detail = new BigDecimal("0");//单身商品AMT合计
			BigDecimal tot_qty_detail = new BigDecimal("0");//单身商品qty合计
			StringBuffer logMemo_detail = new StringBuffer("");

			//前端没有传入商品，就不会修改商品明细，只是修改，单头基本资料
			if (req.getRequest().getGoodsList()!=null&&!req.getRequest().getGoodsList().isEmpty())
			{
				for (Map<String, Object> parGoods : getQDataGoods)
				{
					int item_db = Integer.parseInt(parGoods.get("ITEM").toString());
					String pluNo = parGoods.get("PLUNO").toString();
					String pluName = parGoods.get("PLUNAME").toString();
					String qty = parGoods.get("QTY").toString();
					String amt = parGoods.get("AMT").toString();
					String packageType = parGoods.get("PACKAGETYPE").toString();//1普通商品；2-套餐主商品；3-套餐子商品
					BigDecimal qty_b = new BigDecimal("0");
					try {
						qty_b = new BigDecimal(qty);
					} catch (Exception e) {
					}
					String pickQty = parGoods.get("PICKQTY").toString();
					BigDecimal pickQty_B = new BigDecimal("0");
					try {
						pickQty_B = new BigDecimal(pickQty);
					} catch (Exception e) {
					}

					if (pickQty_B.compareTo(BigDecimal.ZERO) != 0) {
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("该订单(意向单)商品明细对应的商品(项次=" + item_db + ",商品编码=" + pluNo + "，商品名称=" + pluName + ")存在部分提货,不能在进行修改！");
						return;
					}

					boolean isFindItem = false;
					for (orderGoodsItem goodsItem : req.getRequest().getGoodsList())
					{
						int item_req = Integer.parseInt(goodsItem.getItem());
						if (item_db != item_req) {
							continue;
						}
						if (!pluNo.equals(goodsItem.getPluNo())) {
							continue;
						}
						isFindItem = true;
						BigDecimal amt_req = new BigDecimal(goodsItem.getAmt());
						BigDecimal qty_req = new BigDecimal(goodsItem.getQty());
						if (!"3".equals(packageType))//合计非套餐子商品
						{
							tot_amt_detail = tot_amt_detail.add(amt_req);//合计单身商品AMT
							tot_qty_detail = tot_qty_detail.add(qty_req);//合计单身商品数量
						}

						if (qty_b.compareTo(qty_req) == 0)
						{
							//没有修改商品单身数量
						}
						else
						{
							isUpdateGoodsDetail = true;
							logMemo_detail.append("<br>项次:" + item_db + " 商品:" + pluNo + ",更新数量:" + qty + "-->" + goodsItem.getQty() + ",更新金额:" + amt + "-->" + goodsItem.getAmt());
						}
						break;

					}
					//如果没找到，那么也要报错
					if (!isFindItem) {
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("该订单(意向单)原商品明细对应的商品(项次=" + item_db + ",商品编码=" + pluNo + "，商品名称=" + pluName + ")与传入的商品无法匹配,不能在进行修改！");
						return;
					}

				}
			}

			//如果修改了商品单身，检查下前端传入的资料
			if (isUpdateGoodsDetail)
			{
				tot_amt_detail = tot_amt_detail.setScale(2,BigDecimal.ROUND_HALF_UP);
				String orderPayAmt = mapOrder.getOrDefault("PAYAMT", "").toString();
				BigDecimal orderPayAmt_b = new BigDecimal("0");
				try {
					orderPayAmt_b = new BigDecimal(orderPayAmt).setScale(2,BigDecimal.ROUND_HALF_UP);
				}
				catch (Exception e)
				{

				}
				if (orderPayAmt_b.compareTo(tot_amt_detail)>0)
				{

					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("该订单(意向单)修改商品后的成交金额("+tot_amt_detail+")小于原订单付款金额("+orderPayAmt+"),无法修改！");
					return;
				}

				String orderTotAmt = req.getRequest().getTot_Amt()+"";
				BigDecimal orderTotAmt_b = new BigDecimal("0");
				try {
					orderTotAmt_b = new BigDecimal(orderTotAmt);
				}
				catch (Exception e)
				{

				}
				if (tot_amt_detail.compareTo(orderTotAmt_b)!=0)
				{

					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("该订单(意向单)修改商品后的成交金额合计("+tot_amt_detail+")不等于传入订单金额("+orderTotAmt+"),无法修改！");
					return;
				}

			}



			// 防止没有传更新的节点，那么就不用执行语句
			boolean isNeedUpdate = false;
			StringBuffer logmemo = new StringBuffer("");
			UptBean ub1 = new UptBean("DCP_ORDER");
			// condition
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));

			//region 开始更新单头
			if (req.getRequest().getShipType() != null && req.getRequest().getShipType().trim().length() > 0)
			{
				if (req.getRequest().getShipType().length() > 1)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipType节点值太大，长度不能超过1，");
				}
				if(req.getRequest().getShipType().equals(orderShipType)==false)
				{
					if(!isCanModify_deliverStatus)
					{
						String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送方式！";
						/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
								+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
					}

					ub1.addUpdateValue("shiptype", new DataValue(req.getRequest().getShipType(), Types.VARCHAR));

					// 修改配送方式的时候，同时清空配送状态
					if (!orderStatus.equals("12"))
					{
						// 为12时，不接受配送状态变更的信息，否则会影响到之前的配送状态
						ub1.addUpdateValue("DeliveryStatus", new DataValue("", Types.VARCHAR));
					}

					isNeedUpdate = true;

					String bname = "";

					if (orderShipType.equals("1"))
					{
						bname = "订单来源渠道";
					} else if (orderShipType.equals("2"))
					{
						bname = "全国配送";
					} else if (orderShipType.equals("3"))
					{
						bname = "顾客自提";
					} else if (orderShipType.equals("5"))
					{
						bname = "总部配送";
					} else if (orderShipType.equals("6"))
					{
						bname = "同城配送";
					} else
					{
						bname = "其他未知";
					}

					String ename = "";
					if (req.getRequest().getShipType().equals("1"))
					{
						ename = "订单来源渠道";
					} else if (req.getRequest().getShipType().equals("2"))
					{
						ename = "全国配送";
					} else if (req.getRequest().getShipType().equals("3"))
					{
						ename = "顾客自提";
					} else if (req.getRequest().getShipType().equals("5"))
					{
						ename = "总部配送";
					} else if (req.getRequest().getShipType().equals("6"))
					{
						ename = "同城配送";
					} else
					{
						ename = "其他未知";
					}

					logmemo .append( "<br>配送方式：" +orderShipType+"("+ bname +")"+ "-->" +req.getRequest().getShipType()+"("+ ename +")");
				}

			}

			if (req.getRequest().getShippingShopNo() != null)
			{
				if (req.getRequest().getShippingShopNo().length() > 20)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shippingShopNo节点值太大，长度不能超过20，");
				}
				if(req.getRequest().getShippingShopNo().equals(orderShipShopNo)==false)
				{

					if(!isCanModify_deliverStatus)
					{
						String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送门店！";
						/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
								+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
					}


					ub1.addUpdateValue("SHIPPINGSHOP", new DataValue(req.getRequest().getShippingShopNo(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送门店：" + orderShipShopNo + "-->" + req.getRequest().getShippingShopNo());

					//更新商品单身对应的仓库
					try
					{
						String shippingShopNo_update = req.getRequest().getShippingShopNo();
						HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】【修改了配送门店】【获取配送门店的仓库】配送门店shippingShopNo="+shippingShopNo_update+"， 单号orderNo="+orderNo);
						String sql_warehouse = "select A.OUT_COST_WAREHOUSE,AL.WAREHOUSE_NAME from dcp_org  A "
								+ " left join dcp_warehouse_lang AL on A.EID=AL.EID  AND   A.ORGANIZATIONNO=AL.ORGANIZATIONNO AND A.OUT_COST_WAREHOUSE = AL.WAREHOUSE AND AL.LANG_TYPE='"+langType+"' "
								+ " where  A.EID='"+eId+"' and A.ORGANIZATIONNO ='"+shippingShopNo_update+"' ";

						HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】【修改了配送门店】【获取配送门店的仓库】查询配送门店仓库sql="+sql_warehouse+",配送门店shippingShopNo="+shippingShopNo_update+"， 单号orderNo="+orderNo);

						List<Map<String, Object>> getShippingshopWarehouseInfo = StaticInfo.dao.executeQuerySQL(sql_warehouse, null);

						if(getShippingshopWarehouseInfo==null||getShippingshopWarehouseInfo.isEmpty())
						{
							HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】【修改了配送门店】【获取配送门店的仓库】，配送门店shippingShopNo="+ shippingShopNo_update+"查无资料,单号orderNo="+orderNo);
						}
						else
						{

							String warehouseNo = getShippingshopWarehouseInfo.get(0).getOrDefault("OUT_COST_WAREHOUSE","").toString();
							String warehouseName = getShippingshopWarehouseInfo.get(0).getOrDefault("WAREHOUSE_NAME","").toString();
							HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】【修改了配送门店】【更新DCP_ORDER_DETAIL】更新WAREHOUSE="+warehouseNo+",更新WAREHOUSENAME="+warehouseName+"， 单号orderNo="+orderNo);
							if (isUpdateGoodsDetail)
							{
								//后面插入的时候，更下实体类
								for (orderGoodsItem goodsItem : req.getRequest().getGoodsList())
								{
									goodsItem.setWarehouse(warehouseNo);
									goodsItem.setWarehouseName(warehouseName);
								}
							}
							else
							{
								//需要更新下商品单身的仓库
								UptBean up2 = new UptBean("DCP_ORDER_DETAIL");
								up2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								up2.addCondition("orderno", new DataValue(orderNo, Types.VARCHAR));


								up2.addUpdateValue("WAREHOUSE", new DataValue(warehouseNo, Types.VARCHAR));
								up2.addUpdateValue("WAREHOUSENAME", new DataValue(warehouseName, Types.VARCHAR));
								this.addProcessData(new DataProcessBean(up2));
							}


						}

					}
					catch (Exception e)
					{
						// TODO: handle exception
					}



				}

			}

			if (req.getRequest().getShippingShopName() != null)
			{
				String shippingShopName = req.getRequest().getShippingShopName() == null ? ""
						: req.getRequest().getShippingShopName();
				if(shippingShopName.equals(orderShipShopName)==false)
				{
					if (shippingShopName.length() > 80)
					{
						shippingShopName = shippingShopName.substring(0, 80);
					}
					ub1.addUpdateValue("SHIPPINGSHOPNAME", new DataValue(shippingShopName, Types.VARCHAR));
					isNeedUpdate = true;
					logmemo .append( "<br>配送门店名称：" +  orderShipShopName + "-->"+ req.getRequest().getShippingShopName());
				}
			}

			if (req.getRequest().getMachShopNo() != null)
			{
				if (req.getRequest().getMachShopNo().length() > 20)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的machShopNo节点值太大，长度不能超过20，");
				}
				if(req.getRequest().getMachShopNo().equals(orderMachShopNo)==false)
				{

					if(!isCanModify_productStatus)
					{
						String errorLog = "该订单生产是"+productStatusName+"，不能修改生产门店！";
						/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
								+ " 数据库中 生产状态productStatus=" + productStatus);*/
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
					}

					ub1.addUpdateValue("MACHSHOP", new DataValue(req.getRequest().getMachShopNo(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>生产门店：" + orderMachShopNo +  "-->" + req.getRequest().getMachShopNo() );


					//这里查询下修改得门店是不是，总部
					String sql_shop = "select * from dcp_org where eid='"+eId+"' and ORGANIZATIONNO='"+req.getRequest().getMachShopNo()+"' ";
					List<Map<String, Object>> getShopData = this.doQueryData(sql_shop, null);
					if(getShopData!=null&&getShopData.isEmpty()==false)
					{
						String isShipcompany_req = "N";
						String org_form = getShopData.get(0).getOrDefault("ORG_FORM", "").toString();
						if(org_form.equals("0"))
						{
							isShipcompany_req = "Y";
						}

						if(isShipcompany_db.equals(isShipcompany_req)==false)
						{
							ub1.addUpdateValue("ISSHIPCOMPANY", new DataValue(isShipcompany_req, Types.VARCHAR));
							logmemo .append( "<br>是否总部生产：" + isShipcompany_db + "-->" + isShipcompany_req);
						}
					}
				}

			}

			if (req.getRequest().getMachShopName() != null)
			{
				String machShopName = req.getRequest().getMachShopName() == null ? ""
						: req.getRequest().getMachShopName();
				if(machShopName.equals(orderMachShopName)==false)
				{
					if (machShopName.length() > 80)
					{
						machShopName = machShopName.substring(0, 80);
					}
					ub1.addUpdateValue("MACHSHOPNAME", new DataValue(machShopName, Types.VARCHAR));
					isNeedUpdate = true;
					logmemo .append( "<br>生产门店名称：" +  orderMachShopName + "-->"+ req.getRequest().getMachShopName() );
				}

			}


			if (req.getRequest().getShipDate() != null)
			{
				if (req.getRequest().getShipDate().length() > 8)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过8，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送日期！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getShipDate().equals(orderShipDate)==false)
				{
					ub1.addUpdateValue("SHIPDATE", new DataValue(req.getRequest().getShipDate(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送日期：" + orderShipDate + "-->" + req.getRequest().getShipDate());
				}

			}

			if (req.getRequest().getShipStartTime() != null)
			{
				if (req.getRequest().getShipStartTime().length() > 50)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipStartTTime节点值太大，长度不能超过50，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送时间！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getShipStartTime().equals(orderShipStartTime)==false)
				{
					ub1.addUpdateValue("SHIPSTARTTIME", new DataValue(req.getRequest().getShipStartTime(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送开始时间：" + orderShipStartTime + "-->" + req.getRequest().getShipStartTime() );
				}

			}

			if (req.getRequest().getShipEndTime() != null)
			{
				if (req.getRequest().getShipEndTime().length() > 50)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipEndTime节点值太大，长度不能超过50，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送时间！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getShipEndTime().equals(orderShipEndTime)==false)
				{
					ub1.addUpdateValue("SHIPENDTIME", new DataValue(req.getRequest().getShipEndTime(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送结束时间：" + orderShipEndTime + "-->" + req.getRequest().getShipEndTime() );
				}

			}

			if (req.getRequest().getDeliveryType() != null)
			{
				if (req.getRequest().getDeliveryType().length() > 6)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的deliveryType节点值太大，长度不能超过6，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改物流类型！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getDeliveryType().equals(order_deliveryType)==false)
				{
					ub1.addUpdateValue("DELIVERYTYPE", new DataValue(req.getRequest().getDeliveryType(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>物流类型：" + order_deliveryType + "-->" + req.getRequest().getDeliveryType() );
				}

			}

			if (req.getRequest().getAddress() != null)
			{
				if (req.getRequest().getAddress().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的address节点值太大，长度不能超过100，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送地址！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getAddress().equals(orderAdress)==false)
				{
					ub1.addUpdateValue("address", new DataValue(req.getRequest().getAddress(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo.append( "<br>收货地址：" + orderAdress + "-->" + req.getRequest().getAddress() );
				}

			}

			if (req.getRequest().getProvince() != null)
			{
				if (req.getRequest().getProvince().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送省份！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getProvince().equals(order_province)==false)
				{
					ub1.addUpdateValue("PROVINCE", new DataValue(req.getRequest().getProvince(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo.append( "<br>配送省份：" + order_province + "-->" + req.getRequest().getProvince() );
				}

			}

			if (req.getRequest().getCity() != null)
			{
				if (req.getRequest().getCity().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送城市！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getCity().equals(order_city)==false)
				{
					ub1.addUpdateValue("CITY", new DataValue(req.getRequest().getCity(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo.append( "<br>配送城市：" + order_city + "-->" + req.getRequest().getCity() );
				}

			}

			if (req.getRequest().getCounty() != null)
			{
				if (req.getRequest().getCounty().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送区县！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getCounty().equals(order_county)==false)
				{
					ub1.addUpdateValue("COUNTY", new DataValue(req.getRequest().getCounty(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送区县：" + order_county + "-->" + req.getRequest().getCounty() );
				}

			}

			if (req.getRequest().getStreet() != null)
			{
				if (req.getRequest().getStreet().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的shipDate节点值太大，长度不能超过100，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送街道！";
				/*	HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getStreet().equals(order_street)==false)
				{
					ub1.addUpdateValue("STREET", new DataValue(req.getRequest().getStreet(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送街道：" + order_street + "-->" + req.getRequest().getStreet() );
				}

			}

			if (req.getRequest().getLongitude() != null&&!req.getRequest().getLongitude().isEmpty())
			{
				try
				{
					Double.parseDouble(req.getRequest().getLongitude());
				} catch (Exception e)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的longitude节点值必须是数字类型，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送地址的经度！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getLongitude().equals(order_longitude)==false)
				{
					ub1.addUpdateValue("LONGITUDE", new DataValue(req.getRequest().getLongitude(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送地址经度：" + order_longitude + "-->" + req.getRequest().getLongitude() );
				}

			}

			if (req.getRequest().getLatitude() != null&&!req.getRequest().getLatitude().isEmpty())
			{
				try
				{
					Double.parseDouble(req.getRequest().getLatitude());
				} catch (Exception e)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的longitude节点值必须是数字类型，");
				}
				if(!isCanModify_deliverStatus)
				{
					String errorLog = "该订单配送状态是"+deliverStatusName+"，不能修改配送地址的维度！";
					/*HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】" + errorLog + "，单号OrderNO=" + orderNo
							+ " 数据库中 配送状态deliverStatus=" + deliverStatus);*/
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorLog);
				}
				if(req.getRequest().getLatitude().equals(order_latitude)==false)
				{
					ub1.addUpdateValue("LATITUDE", new DataValue(req.getRequest().getLatitude(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送地址维度：" + order_latitude + "-->" + req.getRequest().getLatitude() );
				}

			}

			if (req.getRequest().getContMan() != null)
			{
				if (req.getRequest().getContMan().length() > 40)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的contMan节点值太大，长度不能超过40，");
				}
				if(req.getRequest().getContMan().equals(orderContMan)==false)
				{
					ub1.addUpdateValue("CONTMAN", new DataValue(req.getRequest().getContMan(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>订货人：" + orderContMan + "-->" + req.getRequest().getContMan() );
				}

			}

			if (req.getRequest().getContTel() != null)
			{
				if (req.getRequest().getContTel().length() > 40)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的getManTel节点值太大，长度不能超过40，");
				}
				if(req.getRequest().getContTel().equals(orderContTel)==false)
				{
					ub1.addUpdateValue("CONTTEL", new DataValue(req.getRequest().getContTel(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>订货人电话：" + orderContTel + "-->" + req.getRequest().getContTel() );

				}

			}


			if (req.getRequest().getGetMan() != null)
			{
				if (req.getRequest().getGetMan().length() > 40)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的getMan节点值太大，长度不能超过40，");
				}
				if(req.getRequest().getGetMan().equals(orderGetMan)==false)
				{
					ub1.addUpdateValue("getman", new DataValue(req.getRequest().getGetMan(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>收货人：" + orderGetMan + "-->" + req.getRequest().getGetMan() );
				}

			}

			if (req.getRequest().getGetManTel() != null)
			{
				if (req.getRequest().getGetManTel().length() > 40)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的getManTel节点值太大，长度不能超过40，");
				}
				if(req.getRequest().getGetManTel().equals(orderGetManTel)==false)
				{
					ub1.addUpdateValue("GETMANTEL", new DataValue(req.getRequest().getGetManTel(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>收货人电话：" + orderGetManTel + "-->" + req.getRequest().getGetManTel() );

				}

			}

			if (req.getRequest().getDeliveryBusinessType() != null)
			{
				if (req.getRequest().getDeliveryBusinessType().length() > 32)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的deliveryBusinessType节点值太大，长度不能超过32，");
				}
				if(req.getRequest().getDeliveryBusinessType().equals(order_deliveryBusinessType)==false)
				{
					ub1.addUpdateValue("DELIVERYBUSINESSTYPE",
							new DataValue(req.getRequest().getDeliveryBusinessType(), Types.VARCHAR));

					isNeedUpdate = true;

					String bname = "";

					if (order_deliveryBusinessType.equals("1"))
					{
						bname = "随车";
					} else if (order_deliveryBusinessType.equals("2"))
					{
						bname = "代发";
					} else if (order_deliveryBusinessType.equals("3"))
					{
						bname = "总部派车";
					} else
					{
						bname = "其他未知";
					}
					String ename = "";
					if (req.getRequest().getDeliveryBusinessType().equals("1"))
					{
						ename = "随车";
					} else if (req.getRequest().getDeliveryBusinessType().equals("2"))
					{
						ename = "代发";
					} else if (req.getRequest().getDeliveryBusinessType().equals("3"))
					{
						ename = "总部派车";
					} else
					{
						ename = "其他未知";
					}

					logmemo.append("<br>配送业务类型：" + order_deliveryBusinessType + "(" + bname + ")" + "-->"
							+ req.getRequest().getDeliveryBusinessType() + "(" + ename + ")" );
				}

			}

			if (req.getRequest().getIsUrgentOrder() != null)
			{
				if (req.getRequest().getIsUrgentOrder().length() > 1)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的isUrgentOrder节点值太大，长度不能超过1，");
				}
				if(req.getRequest().getIsUrgentOrder().equals(order_isUrgentOrder)==false)
				{
					ub1.addUpdateValue("ISURGENTORDER", new DataValue(req.getRequest().getIsUrgentOrder(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo.append( "<br>是否紧急订单：" + order_isUrgentOrder + "-->" + req.getRequest().getIsUrgentOrder() );

				}

			}

			if (req.getRequest().getMemo() != null)
			{
				if (req.getRequest().getMemo().length() > 255)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入memo节点值太大，长度不能超过255，");
				}
				if(req.getRequest().getMemo().equals(orderMemo)==false)
				{
					ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo.append( "<br>订单备注：" + orderMemo + "-->" + req.getRequest().getMemo() );
				}

			}

			if (req.getRequest().getProMemo() != null)
			{
				if (req.getRequest().getProMemo().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入proMemo节点值太大，长度不能超过100，");
				}
				if(req.getRequest().getProMemo().equals(orderProMemo)==false)
				{
					ub1.addUpdateValue("PROMEMO", new DataValue(req.getRequest().getProMemo(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>生产备注：" + orderProMemo + "-->" + req.getRequest().getProMemo() );
				}

			}

			if (req.getRequest().getDelMemo() != null)
			{
				if (req.getRequest().getDelMemo().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入delMemo节点值太大，长度不能超过100，");
				}
				if(req.getRequest().getDelMemo().equals(orderDelMemo)==false)
				{
					ub1.addUpdateValue("DELMEMO", new DataValue(req.getRequest().getDelMemo(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送备注：" + orderDelMemo + "-->" + req.getRequest().getDelMemo() );
				}

			}

			if(req.getRequest().getIsDelete()!=null)
			{
				if (req.getRequest().getIsDelete().length() > 1)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入isDelete节点值太大，长度不能超过1，");
				}
				if(req.getRequest().getIsDelete().equals(isDelete_db)==false)
				{
					ub1.addUpdateValue("ISDELETE", new DataValue(req.getRequest().getIsDelete(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "ISDELETE：" + isDelete_db + "-->" + req.getRequest().getIsDelete() );
				}
			}

			if (req.getRequest().getPackerId() != null)
			{
				if (req.getRequest().getPackerId().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的packerId节点值太大，长度不能超过100，");
				}
				if(req.getRequest().getPackerId().equals(packerId)==false)
				{
					ub1.addUpdateValue("PACKERID", new DataValue(req.getRequest().getPackerId(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>打包人ID：" + packerId + "-->" + req.getRequest().getPackerId() );
				}

			}
			if (req.getRequest().getPackerName() != null)
			{
				if (req.getRequest().getPackerName().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的packerName节点值太大，长度不能超过100，");
				}
				if(req.getRequest().getPackerName().equals(packerName)==false)
				{
					ub1.addUpdateValue("PACKERNAME", new DataValue(req.getRequest().getPackerName(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>打包人：" + packerName + "-->" + req.getRequest().getPackerName() );
				}

			}
			if (req.getRequest().getPackerTelephone() != null)
			{
				if (req.getRequest().getPackerTelephone().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的packerTelephone节点值太大，长度不能超过100，");
				}
				if(req.getRequest().getPackerTelephone().equals(packerTelephone)==false)
				{
					ub1.addUpdateValue("PACKERTELEPHONE", new DataValue(req.getRequest().getPackerTelephone(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>打包人电话：" + packerTelephone + "-->" + req.getRequest().getPackerTelephone() );
				}

			}

			if (req.getRequest().getDelId() != null)
			{
				if (req.getRequest().getDelId().length() > 10)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的delId节点值太大，长度不能超过10，");
				}
				if(req.getRequest().getDelId().equals(delId)==false)
				{
					ub1.addUpdateValue("DELID", new DataValue(req.getRequest().getDelId(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送人ID：" + delId + "-->" + req.getRequest().getDelId() );
				}

			}
			if (req.getRequest().getDelName() != null)
			{
				if (req.getRequest().getDelName().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的delName节点值太大，长度不能超过100，");
				}
				if(req.getRequest().getDelName().equals(delName)==false)
				{
					ub1.addUpdateValue("DELNAME", new DataValue(req.getRequest().getDelName(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送人：" + delName + "-->" + req.getRequest().getDelName() );
				}

			}
			if (req.getRequest().getDelTelephone() != null)
			{
				if (req.getRequest().getDelTelephone().length() > 100)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的delTelephone节点值太大，长度不能超过100，");
				}
				if(req.getRequest().getDelTelephone().equals(delTelephone)==false)
				{
					ub1.addUpdateValue("DELTELEPHONE", new DataValue(req.getRequest().getDelTelephone(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo .append( "<br>配送人电话：" + delTelephone + "-->" + req.getRequest().getDelTelephone() );
				}

			}

			if (req.getRequest().getCanModify() != null)
			{
				if (req.getRequest().getCanModify().length() > 1)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "传入的canModify节点值太大，长度不能超过1，");
				}
				if(req.getRequest().getCanModify().equals(canModify)==false)
				{
					ub1.addUpdateValue("CANMODIFY", new DataValue(req.getRequest().getCanModify(), Types.VARCHAR));

					isNeedUpdate = true;
					logmemo.append( "<br>是否允许补录：" + canModify + "-->" + req.getRequest().getCanModify());
				}

			}
			//endregion

			//是否需要更新商品明细
			if (isUpdateGoodsDetail)
			{
				isNeedUpdate = true;
				ub1.addUpdateValue("TOT_OLDAMT", new DataValue(req.getRequest().getTot_oldAmt(), Types.VARCHAR));
				ub1.addUpdateValue("TOT_AMT", new DataValue(req.getRequest().getTot_Amt(), Types.VARCHAR));
				ub1.addUpdateValue("TOT_DISC", new DataValue(req.getRequest().getTotDisc(), Types.VARCHAR));
				ub1.addUpdateValue("TOT_QTY", new DataValue(tot_qty_detail, Types.VARCHAR));
                ub1.addUpdateValue("PROCESS_STATUS", new DataValue("N", Types.VARCHAR));//重新上传
				logmemo.append( "<br>订单原金额：" + mapOrder.get("TOT_OLDAMT").toString() + "-->" + req.getRequest().getTot_oldAmt());
				logmemo.append( "<br>订单总折扣：" + mapOrder.get("TOT_DISC").toString() + "-->" + req.getRequest().getTotDisc());
				logmemo.append( "<br>订单总金额：" + mapOrder.get("TOT_AMT").toString() + "-->" + req.getRequest().getTot_Amt());
				logmemo.append( "<br>订单总数量：" + mapOrder.get("TOT_QTY").toString() + "-->" + tot_qty_detail);

				DelBean del1 = new DelBean("DCP_ORDER_DETAIL");
				del1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				del1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(del1));

				DelBean del2 = new DelBean("DCP_ORDER_DETAIL_AGIO");
				del2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				del2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(del2));

				//开始计算商户实收等折扣，

				for (orderGoodsItem map : req.getRequest().getGoodsList())
				{
					if(map.getAmt_merReceive()<0.01)
					{
						map.setAmt_merReceive(map.getAmt());
					}

					if(map.getAmt_custPayReal()<0.01)
					{
						map.setAmt_custPayReal(map.getAmt());
					}

					if(map.getPackageType()!=null&&map.getPackageType().equals("3"))
					{
						continue;
					}

				}

                 //下面走 订单创建逻辑里面的 支付折扣
				StringBuffer error = new StringBuffer();

				HelpTools.posOrderPayDiscShareProcess(req.getRequest(),error);

				//pos抹零分摊
				HelpTools.posOrderEraseAmtShareProcess(req.getRequest(), error);

				//下面开始插入商品单身表和商品折扣表
				List<DataProcessBean> DPB = this.getGoodsDetailSql(req.getRequest());
				if (DPB==null||DPB.isEmpty())
				{
					this.pData.clear();
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("该订单(意向单)修改商品数量时异常：重新生成商品明细insert语句为空！");
					return;
				}
				for (DataProcessBean dataBean : DPB)
				{
					this.addProcessData(dataBean);
				}


			}
			
			if (isNeedUpdate == false)
			{
				this.pData.clear();
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("没有进行任何修改!");
				return;
				//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "没有进行任何修改！");
			}
			ub1.addUpdateValue("OPERATIONTYPE", new DataValue("0", Types.VARCHAR));
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
			this.doExecuteDataToDB();
			HelpTools.writelog_waimai("【调用订单(意向单)DCP_IntentionOrderModify修改接口】修改成功，单号OrderNO=" + orderNo);
			// region 写下日志
			try
			{
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();

				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(channelId);

				onelv1.setNeed_callback("N");
				onelv1.setNeed_notify("N");

				onelv1.seteId(eId);

				String opNO = req.getOpNO();
				if (opNO==null||opNO.isEmpty())
				{
					opNO = "pos";
				}
				String opName = req.getOpName();
				onelv1.setOpNo(opNO);
				onelv1.setOpName(opName);
				onelv1.setOrderNo(orderNo);
				onelv1.setLoadDocBillType(req.getRequest().getLoadDocBillType());
				onelv1.setLoadDocOrderNo(req.getRequest().getLoadDocOrderNo());

				String statusType_log = "99";// 其他状态
				String updateStaus_log = "99";// 订单修改
				if (isUpdateGoodsDetail)
				{
					statusType_log = "9999";
					updateStaus_log = "9999";
				}
				onelv1.setStatusType(statusType_log);
				onelv1.setStatus(updateStaus_log);
				String statusName_log = "(意向单)订单修改";
				String statusTypeName_log = "其他状态";
				onelv1.setStatusTypeName(statusTypeName_log);
				onelv1.setStatusName(statusName_log);

				StringBuffer memo = new StringBuffer("");
				if (req.getPlantType()!=null&&!req.getPlantType().isEmpty())
				{
					memo.append("操作平台："+req.getPlantType()+"<br>");
				}
				memo.append(statusTypeName_log + "-->" + statusName_log);
				memo.append("<br>订单单头修改如下:");
				memo.append(logmemo.toString());
				if (logMemo_detail.length()>0)
				{
					memo.append("<br>订单商品资料修改如下:");
					memo.append(logMemo_detail.toString());
				}

				onelv1.setMemo(memo.toString());
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);

				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
				if (nRet)
				{
					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else
				{
					HelpTools.writelog_waimai(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();

			} catch (Exception e)
			{
				HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNO:" + orderNo);
			}
			// endregion

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		}
		catch (SPosCodeException e)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("修改意向单异常："+e.getMessage());
		}
		catch (Exception e)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("修改意向单异常："+e.getMessage());
		}
		
		
	}

	private List<DataProcessBean> getGoodsDetailSql(order dcpOrder) throws Exception
	{
		List<DataProcessBean> DPB = new ArrayList<>();
		String eId = dcpOrder.geteId();
		String orderNo = dcpOrder.getOrderNo();
		String loadDocType = dcpOrder.getLoadDocType();//渠道类型
		String channelId = dcpOrder.getChannelId();
		List<orderGoodsItem> goodsItemList = dcpOrder.getGoodsList();
		String[] columns_goods =
				{ "eid", "orderno", "item", "loaddoctype", "channelid", "pluno", "pluname", "plubarcode", "featureno",
						"featurename", "goodsurl", "specname", "attrname", "sunit", "sunitname", "warehouse",
						"warehousename", "skuid", "gift", "giftsourceserialno", "giftreason", "goodsgroup", "packagetype",
						"packagemitem", "toppingtype", "toppingmitem", "oitem", "pickqty", "rqty", "rcqty", "shopqty",
						"boxnum", "boxprice", "qty", "oldprice", "oldamt", "price", "disc", "amt", "incltax", "taxcode",
						"taxtype", "invitem","invsplittype", "sellerno", "sellername", "accno", "counterno", "coupontype", "couponcode",
						"sourcecode", "ismemo", "stime","VIRTUAL","DISC_MERRECEIVE","AMT_MERRECEIVE",
						"DISC_CUSTPAYREAL","AMT_CUSTPAYREAL","PREPARATIONSTATUS","PARTITION_DATE","SPECNAME_ORIGIN","ATTRNAME_ORIGIN","FLAVORSTUFFDETAIL" };
		for (orderGoodsItem goodsItem : goodsItemList)
		{
			//数据库长度截取
			try
			{
				if(goodsItem.getPluNo()!=null&&goodsItem.getPluNo().length()>40)
				{
					goodsItem.setPluNo(goodsItem.getPluNo().substring(0,40));
				}
				if(goodsItem.getPluBarcode()!=null&&goodsItem.getPluBarcode().length()>40)
				{
					goodsItem.setPluBarcode(goodsItem.getPluBarcode().substring(0,40));
				}
				if(goodsItem.getPluName()!=null&&goodsItem.getPluName().length()>120)
				{
					goodsItem.setPluName(goodsItem.getPluName().substring(0,120));
				}
				if(goodsItem.getSkuId()!=null&&goodsItem.getSkuId().length()>120)
				{
					goodsItem.setSkuId(goodsItem.getSkuId().substring(0,120));
				}
				if(goodsItem.getsUnit()!=null&&goodsItem.getsUnit().length()>32)
				{
					goodsItem.setsUnit(goodsItem.getsUnit().substring(0,32));
				}
				if(goodsItem.getsUnitName()!=null&&goodsItem.getsUnitName().length()>100)
				{
					goodsItem.setsUnitName(goodsItem.getsUnitName().substring(0,100));
				}
				if(goodsItem.getFeatureNo()!=null&&goodsItem.getFeatureNo().length()>64)
				{
					goodsItem.setFeatureNo(goodsItem.getFeatureNo().substring(0,64));
				}
				if(goodsItem.getFeatureName()!=null&&goodsItem.getFeatureName().length()>64)
				{
					goodsItem.setFeatureName(goodsItem.getFeatureName().substring(0,64));
				}
				if(goodsItem.getSpecName()!=null&&goodsItem.getSpecName().length()>120)
				{
					goodsItem.setSpecName(goodsItem.getSpecName().substring(0,120));
				}
				if(goodsItem.getAttrName()!=null&&goodsItem.getAttrName().length()>120)
				{
					goodsItem.setAttrName(goodsItem.getAttrName().substring(0,120));
				}
				if(goodsItem.getSpecName_origin()!=null&&goodsItem.getSpecName_origin().length()>120)
				{
					goodsItem.setSpecName_origin(goodsItem.getSpecName_origin().substring(0,120));
				}
				if(goodsItem.getAttrName_origin()!=null&&goodsItem.getAttrName_origin().length()>120)
				{
					goodsItem.setAttrName_origin(goodsItem.getAttrName_origin().substring(0,120));
				}
				if(goodsItem.getFlavorStuffDetail()!=null&&goodsItem.getFlavorStuffDetail().length()>128)
				{
					goodsItem.setFlavorStuffDetail(goodsItem.getFlavorStuffDetail().substring(0,128));
				}
				//除了饿了么，其他的都是等于自己
				if (!orderLoadDocType.ELEME.equals(dcpOrder.getLoadDocType()))
				{
					goodsItem.setSpecName_origin(goodsItem.getSpecName());
					goodsItem.setAttrName_origin(goodsItem.getAttrName());
				}
				if(goodsItem.getWarehouse()!=null&&goodsItem.getWarehouse().length()>32)
				{
					goodsItem.setWarehouse(goodsItem.getWarehouse().substring(0,32));
				}
				if(goodsItem.getWarehouseName()!=null&&goodsItem.getWarehouseName().length()>64)
				{
					goodsItem.setWarehouseName(goodsItem.getWarehouseName().substring(0,64));
				}
				if(goodsItem.getSellerNo()!=null&&goodsItem.getSellerNo().length()>32)
				{
					goodsItem.setSellerNo(goodsItem.getSellerNo().substring(0,32));
				}
				if(goodsItem.getSellerName()!=null&&goodsItem.getSellerName().length()>64)
				{
					goodsItem.setSellerName(goodsItem.getSellerName().substring(0,64));
				}
			}
			catch (Exception e)
			{
				// TODO: handle exception
			}
			String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			String curTime = new SimpleDateFormat("HHmmss").format(new Date());
			String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//数据库DATE类型
			String featureNo = goodsItem.getFeatureNo();
			if(featureNo==null||featureNo.isEmpty())
			{
				featureNo = " ";//默认空格
			}
			String virtual = goodsItem.getVirtual();
			if(virtual==null||virtual.equals("Y")==false)
			{
				virtual = "N";
			}

			DataValue[] insValue_good = new DataValue[] {
					new DataValue(eId, Types.VARCHAR),
					new DataValue(orderNo, Types.VARCHAR),
					new DataValue(goodsItem.getItem(), Types.VARCHAR),
					new DataValue(loadDocType, Types.VARCHAR),
					new DataValue(channelId, Types.VARCHAR),
					new DataValue(goodsItem.getPluNo(), Types.VARCHAR),
					new DataValue(goodsItem.getPluName(), Types.VARCHAR),
					new DataValue(goodsItem.getPluBarcode(), Types.VARCHAR),
					new DataValue(featureNo, Types.VARCHAR),
					new DataValue(goodsItem.getFeatureName(), Types.VARCHAR),//ordershop
					new DataValue(goodsItem.getGoodsUrl(), Types.VARCHAR),//ordershopname
					new DataValue(goodsItem.getSpecName(), Types.VARCHAR),
					new DataValue(goodsItem.getAttrName(), Types.VARCHAR),
					new DataValue(goodsItem.getsUnit(), Types.VARCHAR),
					new DataValue(goodsItem.getsUnitName(), Types.VARCHAR),
					new DataValue(goodsItem.getWarehouse(), Types.VARCHAR),//warehouse
					new DataValue(goodsItem.getWarehouseName(), Types.VARCHAR),//warehousename
					new DataValue(goodsItem.getSkuId(), Types.VARCHAR),
					new DataValue(goodsItem.getGift(), Types.VARCHAR),
					new DataValue(goodsItem.getGiftSourceSerialNo(), Types.VARCHAR),
					new DataValue(goodsItem.getGiftReason(), Types.VARCHAR),
					new DataValue(goodsItem.getGoodsGroup(), Types.VARCHAR),
					new DataValue(goodsItem.getPackageType(), Types.VARCHAR),
					new DataValue(goodsItem.getPackageMitem(), Types.VARCHAR),
					new DataValue(goodsItem.getToppingType(), Types.VARCHAR),
					new DataValue(goodsItem.getToppingMitem(), Types.VARCHAR),
					new DataValue(goodsItem.getoItem(), Types.VARCHAR),
					new DataValue(goodsItem.getPickQty(), Types.VARCHAR),
					new DataValue("0", Types.VARCHAR),//rqty
					new DataValue("0", Types.VARCHAR),//rcqty
					new DataValue(goodsItem.getShopQty(), Types.VARCHAR),
					new DataValue(goodsItem.getBoxNum(), Types.VARCHAR),
					new DataValue(goodsItem.getBoxPrice(), Types.VARCHAR),
					new DataValue(goodsItem.getQty(), Types.VARCHAR),
					new DataValue(goodsItem.getOldPrice(), Types.VARCHAR),
					new DataValue(goodsItem.getOldAmt(), Types.VARCHAR),
					new DataValue(goodsItem.getPrice(), Types.VARCHAR),//price
					new DataValue(goodsItem.getDisc(), Types.VARCHAR),
					new DataValue(goodsItem.getAmt(), Types.VARCHAR),
					new DataValue(goodsItem.getInclTax(), Types.VARCHAR),
					new DataValue(goodsItem.getTaxCode(), Types.VARCHAR),
					new DataValue(goodsItem.getTaxType(), Types.VARCHAR),
					new DataValue("0", Types.VARCHAR),//invitem
					new DataValue(goodsItem.getInvSplitType(), Types.VARCHAR),//invsplittype
					new DataValue(goodsItem.getSellerNo(), Types.VARCHAR),
					new DataValue(goodsItem.getSellerName(), Types.VARCHAR),
					new DataValue(goodsItem.getAccNo(), Types.VARCHAR),
					new DataValue(goodsItem.getCounterNo(), Types.VARCHAR),
					new DataValue(goodsItem.getCouponType(), Types.VARCHAR),
					new DataValue(goodsItem.getCouponCode(), Types.VARCHAR),
					new DataValue("", Types.VARCHAR),//sourcecode
					new DataValue(goodsItem.getIsMemo(), Types.VARCHAR),
					new DataValue(curDateTime, Types.VARCHAR),
					new DataValue(virtual, Types.VARCHAR),
					new DataValue(goodsItem.getDisc_merReceive(), Types.VARCHAR),
					new DataValue(goodsItem.getAmt_merReceive(), Types.VARCHAR),
					new DataValue(goodsItem.getDisc_custPayReal(), Types.VARCHAR),
					new DataValue(goodsItem.getAmt_custPayReal(), Types.VARCHAR),
					new DataValue(goodsItem.getPreparationStatus(), Types.VARCHAR),
					new DataValue(dcpOrder.getbDate(), Types.NUMERIC),//分区字段
					new DataValue(goodsItem.getSpecName_origin(), Types.VARCHAR),
					new DataValue(goodsItem.getAttrName_origin(), Types.VARCHAR),
					new DataValue(goodsItem.getFlavorStuffDetail(), Types.VARCHAR),
			};

			InsBean ib_goods = new InsBean("DCP_ORDER_DETAIL", columns_goods);//分区字段已处理
			ib_goods.addValues(insValue_good);
			DPB.add(new DataProcessBean(ib_goods));

			/*********************商品折扣**************************/
			List<orderGoodsItemAgio> goodsAgioList = goodsItem.getAgioInfo();

			if(goodsAgioList!=null&&goodsAgioList.size()>0)
			{
				int goodsAgioItem = 0;
				String[] columns_goodsAgio =
						{ "eid", "orderno", "MITEM", "ITEM", "QTY", "AMT","INPUTDISC","REALDISC","DISC","DCTYPE","DCTYPENAME","PMTNO","GIFTCTF","GIFTCTFNO","BSNO",
								"DISC_MERRECEIVE","DISC_CUSTPAYREAL","PARTITION_DATE"};
				for (orderGoodsItemAgio agio : goodsAgioList)
				{
					try
					{
						/**************促销单号记录***************/
						if (loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID))
						{
						}
						if(agio.getDcType()!=null&&agio.getDcType().length()>32)
						{
							agio.setDcType(agio.getDcType().substring(0,32));
						}
						if(agio.getDcTypeName()!=null&&agio.getDcTypeName().length()>64)
						{
							agio.setDcTypeName(agio.getDcTypeName().substring(0,64));
						}
						if(agio.getPmtNo()!=null&&agio.getPmtNo().length()>32)
						{
							agio.setPmtNo(agio.getPmtNo().substring(0,32));
						}
						if(agio.getGiftCtf()!=null&&agio.getGiftCtf().length()>32)
						{
							agio.setGiftCtf(agio.getGiftCtf().substring(0,32));
						}
						if(agio.getGiftCtfNo()!=null&&agio.getGiftCtfNo().length()>32)
						{
							agio.setGiftCtfNo(agio.getGiftCtfNo().substring(0,32));
						}
						if(agio.getBsNo()!=null&&agio.getBsNo().length()>32)
						{
							agio.setBsNo(agio.getBsNo().substring(0,32));
						}

					}
					catch (Exception e)
					{
						// TODO: handle exception
					}
					goodsAgioItem++;
					DataValue[] insValue_goodsAgio = new DataValue[] {
							new DataValue(eId, Types.VARCHAR),
							new DataValue(orderNo, Types.VARCHAR),
							new DataValue(goodsItem.getItem(), Types.VARCHAR),
							new DataValue(goodsAgioItem, Types.VARCHAR),
							new DataValue(agio.getQty(), Types.VARCHAR),
							new DataValue(agio.getAmt(), Types.VARCHAR),
							new DataValue(agio.getInputDisc(), Types.VARCHAR),
							new DataValue(agio.getRealDisc(), Types.VARCHAR),
							new DataValue(agio.getDisc(), Types.VARCHAR),
							new DataValue(agio.getDcType(), Types.VARCHAR),
							new DataValue(agio.getDcTypeName(), Types.VARCHAR),
							new DataValue(agio.getPmtNo(), Types.VARCHAR),
							new DataValue(agio.getGiftCtf(), Types.VARCHAR),
							new DataValue(agio.getGiftCtfNo(), Types.VARCHAR),
							new DataValue(agio.getBsNo(), Types.VARCHAR),
							new DataValue(agio.getDisc_merReceive(), Types.VARCHAR),
							new DataValue(agio.getDisc_custPayReal(), Types.VARCHAR),
							new DataValue(dcpOrder.getbDate(), Types.NUMERIC),//分区字段
					};

					InsBean ib_goodsAgio = new InsBean("DCP_ORDER_DETAIL_AGIO", columns_goodsAgio);//分区字段已处理
					ib_goodsAgio.addValues(insValue_goodsAgio);
					DPB.add(new DataProcessBean(ib_goodsAgio));

				}

			}

		}
		return DPB;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCreateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCreateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCreateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_OrderCreateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		
		if (req.getRequest() == null)
		{
			errMsg.append("request节点不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		order order = req.getRequest();
		
		if (Check.Null(order.getStatus()))
		{
			errCt++;
			errMsg.append("订单状态status不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
		
	}
	
	@Override
	protected TypeToken<DCP_OrderCreateReq> getRequestType()
	{
		return new TypeToken<DCP_OrderCreateReq>() {};
	}
	
	@Override
	protected DCP_IntentionOrderModifyRes getResponseType()
	{
		return new DCP_IntentionOrderModifyRes();
	}

}
