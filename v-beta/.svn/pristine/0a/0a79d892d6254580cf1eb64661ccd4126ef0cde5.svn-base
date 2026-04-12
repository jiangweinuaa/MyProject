package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.*;
import com.dsc.spos.waimai.entity.order;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class Movedb_Order extends InitJob {

    static boolean bRun = false;// 标记此服务是否正在执行中
    static final String jddjLogFileName = "Movedb_Order";
    public String doExe() throws Exception
    {
        String sReturnInfo = "";
        log("【同步任务Movedb_Order】同步START！");
        try
        {
            if (bRun)
            {
                log("\r\n*********同步任务Movedb_Order正在执行中,本次调用取消:************\r\n");
                sReturnInfo="同步任务Movedb_Order正在执行中,本次调用取消";
                return sReturnInfo;
            }

            bRun = true;
            //
            String sql_set = " select * from DCP_MOVEDBSETTING where status=100 ";
            log("【同步任务Movedb_Order】查询设置sql:"+sql_set);
            List<Map<String,Object>> getMoveDBSetList = this.doQueryData(sql_set,null);
            if (getMoveDBSetList==null||getMoveDBSetList.isEmpty())
            {
                log("【同步任务Movedb_Order】查询配置资料为空或者未启用");
                return sReturnInfo;
            }
            //循环
            for (Map<String,Object> map : getMoveDBSetList)
            {
                String eId_3 = map.get("NEWEID").toString();//3.0企业id
                String eId_2 = map.get("OLDEID").toString();//2.0 企业id
                //查询下门店
                sql_set = " select * from DCP_MOVEDBSETTING_shop where NEWEID='"+eId_3+"'";
                log("【同步任务Movedb_Order】查询设置的门店sql:"+sql_set);
                List<Map<String,Object>> getMoveDBSet_shopList = this.doQueryData(sql_set,null);
                if (getMoveDBSet_shopList==null||getMoveDBSet_shopList.isEmpty())
                {
                    log("【同步任务Movedb_Order】查询设置的门店:没有设置门店，同步所有门店订单,开始");
                    this.Movedb_Order2to3(eId_3,eId_2,"");
                    log("【同步任务Movedb_Order】查询设置的门店:没有设置门店，同步所有门店订单,结束");
                }
                else
                {
                    for (Map<String,Object> mapShop : getMoveDBSet_shopList)
                    {
                        String shop = mapShop.get("NEWSHOPID").toString();
                        log("【同步任务Movedb_Order】【循环】【开始】下单门店:"+shop);
                        this.Movedb_Order2to3(eId_3,eId_2,shop);
                        log("【同步任务Movedb_Order】【循环】【结束】下单门店:"+shop);
                    }

                }

            }


        }
        catch (Exception e)
        {
            log("【同步任务Movedb_Order】异常:"+e.getMessage());
        }
        finally {
            bRun=false;
            log("【同步任务Movedb_Order】同步End！");
        }
        return sReturnInfo;
    }


    /**
     * 同步2.0订单到3.0
     * @param newEID 3.0企业id
     * @param oldEID 2.0企业id
     * @param shop 下单门店id
     * @throws Exception
     */
    private void Movedb_Order2to3 (String newEID,String oldEID,String shop) throws Exception
    {
        try
        {
            //检查下3.0 的渠道类型
            //String sql_appType = " select * from  crm_channel where status=100 and  appno='POS' and eid='"+newEID+"'";
            //获取POS渠道类型，适用于全部门店的 渠道编码 shoptype:0-全部门店；1-指定门店，优先取全部门店
            String sql_appType =" select a.CHANNELID,a.CHANNELNAME from crm_channel a "
                       + " left join crm_apiuser b on a.eid=b.eid and a.channelid=b.channelid and a.status=b.status and a.appno=b.apptype "
                       + " where a.appno='POS' and a.status=100  and a.eid='"+newEID+"' order by b.shoptype ";
            log("【同步任务Movedb_Order】查询3.0渠道crm_channel表POS渠道,获取渠道编码channelId的sql:"+sql_appType);
            List<Map<String,Object>> getAppType_POS = this.doQueryData(sql_appType,null);
            if (getAppType_POS==null||getAppType_POS.isEmpty())
            {
                log("【同步任务Movedb_Order】查询3.0渠道crm_channel表POS渠道为空,无法获取渠道编码channelId，无法同步");
                return;
            }
            String channelId_pos = getAppType_POS.get(0).get("CHANNELID").toString();
            String channelId_pos_name = getAppType_POS.get(0).get("CHANNELNAME").toString();

            //2.0商城渠道 有记录channelid 同3.0一致都是appid，只是可能渠道类型不一样。2.0只有商城只有一个渠道类型，3.0拆分了，默认mini把

            //查询2.0 订单数据
            StringBuffer sb = new StringBuffer(" select * from (");
            sb.append(" select LOAD_DOCTYPE,ORDERNO ,ROWNUM RN from tv_order "
                    + " where isbook='Y' and (MOVEDBSTATUS='N' or MOVEDBSTATUS is null) "
                    //+ " and status<>'3' and status<>'12' and status <>'11' "
                    + " and status<>'3' and status<>'12'"
                    + " and shipdate>='20240101'"
                    + " and (LOAD_DOCTYPE='3' or LOAD_DOCTYPE='4') "
                    + " and  companyno='"+oldEID+"'") ;
            if (shop!=null&&!shop.isEmpty())
            {
                sb.append(" and SHOP='"+shop+"'");
            }
            sb.append(" ) where RN<=200");
            String sql_oldOrder = sb.toString();
            log("【同步任务Movedb_Order】查询需要同步的单头sql:"+sql_oldOrder);
            List<Map<String,Object>> getOldOrderHeadList = StaticInfo.dao_pos2.executeQuerySQL(sql_oldOrder,null);
            if (getOldOrderHeadList==null||getOldOrderHeadList.isEmpty())
            {
                log("【同步任务Movedb_Order】查询需要同步的单头资料，无数据");
                return;
            }
            String sql = "";
            for (Map<String,Object> par : getOldOrderHeadList)
            {
                sql = "";
                String orderNo = par.get("ORDERNO").toString();
                String load_docType = par.get("LOAD_DOCTYPE").toString();
                log("【循环同步】开始，单号orderNo=" + orderNo+",渠道load_docType="+load_docType);
                if (this.isExistOrder(newEID,oldEID,orderNo,load_docType))
                {
                    continue;
                }
                try
                {
                    sql = " select A.* "
                            + ",B.ITEM,B.PLUNO,B.PLUBARCODE,B.PLUNAME,B.SPECNAME,B.ATTRNAME,B.UNIT,B.PRICE,B.QTY,B.Goodsgroup,B.Disc,B.Boxnum,B.BOXPRICE,B.AMT AS DETAILAMT,B.ISMEMO,B.SKUID,B.PICKQTY,B.RQTY,B.RCQTY,decode(B.QTY,0,0,null,0,B.AMT / B.QTY) DEALPRICE,B.PACKAGETYPE,B.PACKAGEMITEM,B.TOPPINGTYPE,B.TOPPINGMITEM,B.COUPONTYPE,B.COUPONCODE,B.SHOPQTY,B.GOODSURL,B.INVITEM,B.CLERKNO,B.UNITNAME,B.GIFT,B.GIFTSOURCESERIALNO"
                            + ",C.ITEM AS PAYITEM,C.PAYCODE,C.PAYCODEERP,C.PAYNAME,C.CARDNO AS PAYCARDNO,C.CTTYPE,C.PAYSERNUM,C.SERIALNO,C.REFNO,C.TERIMINALNO,C.DESCORE,C.PAY,C.EXTRA,C.CHANGED,C.BDATE,C.ISORDERPAY,C.ISONLINEPAY,C.ORDER_PAYCODE,C.RCPAY,C.SHOP_PAY,C.LOAD_DOCTYPE_PAY,C.SENDPAY"
                            + ",D.MEMOTYPE DMEMOTYPE,D.MEMONAME DMEMONAME,D.MEMO DMEMO,D.ITEM DITEM,D.OITEM  DOITEM"
                            + " from TV_ORDER A"
                            + " inner join tv_order_detail B on A.COMPANYNO=B.COMPANYNO AND A.SHOP=B.SHOP AND A.Orderno=B.Orderno  AND A.LOAD_DOCTYPE=B.LOAD_DOCTYPE "
                            + " left join tv_order_pay C on A.COMPANYNO=C.COMPANYNO  AND A.SHOP=C.SHOP AND A.Orderno=C.Orderno AND A.LOAD_DOCTYPE=C.LOAD_DOCTYPE "
                            + " left join TV_ORDER_DETAIL_MEMO D on B.COMPANYNO=D.COMPANYNO  AND B.SHOP=D.SHOP AND B.Orderno=D.Orderno AND B.LOAD_DOCTYPE=D.LOAD_DOCTYPE and B.item=D.oitem "
                            + " where A.COMPANYNO='"+oldEID+"' AND A.LOAD_DOCTYPE='"+load_docType+"' and A.ORDERNO='"+orderNo+"' ";

                    List<Map<String,Object>> getQDataDetail = StaticInfo.dao_pos2.executeQuerySQL(sql,null);
                    if (getQDataDetail==null||getQDataDetail.isEmpty())
                    {
                        log("【循环同步】开始，查询没有明细数据，无需同步，单号orderNo=" + orderNo+",渠道load_docType="+load_docType);
                        continue;
                    }

                    Map<String, Object> map = getQDataDetail.get(0);
                    String status =  map.get("STATUS").toString();
                   /* if ("3".equals(status)||"12".equals(status)||"11".equals(status))
                    {
                        log("【循环同步】开始，查询没有明细数据，状态status已改变，无需同步，单号orderNo=" + orderNo+",渠道load_docType="+load_docType);
                        continue;
                    }*/
                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                    condition.put("ITEM", true);
                    List<Map<String, Object>> getOrderGoodsDetail = MapDistinct.getMap(getQDataDetail, condition);

                    condition.clear();
                    condition.put("PAYITEM", true);
                    List<Map<String, Object>> getOrderPay = MapDistinct.getMap(getQDataDetail, condition);

                    condition.clear();
                    condition.put("DITEM", true);
                    condition.put("DOITEM", true);
                    List<Map<String, Object>> getOrderGoodsDetailMemo = MapDistinct.getMap(getQDataDetail, condition);

                    //组装3.0的 订单数据
                    com.dsc.spos.waimai.entity.order dcpOrder = new order();
                    dcpOrder.setInvoiceDetail(new orderInvoice());
                    dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
                    dcpOrder.setPay(new ArrayList<orderPay>());

                    String eId = newEID;
                    String channeType =getLoadDocType(load_docType);
                    String channelId = channelId_pos;//默认取pos渠道
                    String loadDocBillType = channeType;
                    if ("3".equals(load_docType))
                    {
                        channelId = map.get("CHANNELID").toString();//只有2.0微商城有值
                        loadDocBillType = map.get("BILLTYPE").toString();//只有2.0微商城有值
                    }
                    String loadDocOrderNo = orderNo;

                    String refundStatus =  map.get("REFUNDSTATUS").toString();
                    String shopNo = map.get("SHOP").toString();
                    String machShopNo = map.get("MACHSHOP").toString();
                    String shipppingShopNo = map.get("SHIPPINGSHOP").toString();
                    dcpOrder.setBillType("1");
                    dcpOrder.seteId(eId);
                    dcpOrder.setOrderNo(orderNo);
                    dcpOrder.setStatus(status);
                    dcpOrder.setRefundStatus(refundStatus);
                    if ("4".equals(status)||"5".equals(status)||"6".equals(status)||"7".equals(status))
                    {
                        //2.0 status=4、5、6、7表示生产状态，(4生产接单；5生产拒单；6完工入库；7内部调拨）
                        //3.0 专门加了个字段表示生产状态，productStatus枚举值同上
                        dcpOrder.setProductStatus(status);
                        dcpOrder.setStatus("2");//转成默认2，否则POS查询不到
                    }
                    dcpOrder.setLoadDocType(channeType);
                    if ("POS".equals(channeType))////pos渠道类型，获取下渠道ID
                    {
                        dcpOrder.setChannelIdName(channelId_pos_name);
                        //获取门店对应的接口账号，对应的渠道编码
                        Map<String,String> getPosChannelIdByShop = this.getPosChannelId(newEID,shopNo);
                        if (getPosChannelIdByShop!=null)
                        {
                           String channelId_pos_shop = getPosChannelIdByShop.getOrDefault("CHANNELID","").toString();
                           String channelId_pos_shop_name = getPosChannelIdByShop.getOrDefault("CHANNELNAME","").toString();
                           if (!channelId_pos_shop.isEmpty())
                           {
                               channelId = channelId_pos_shop;
                               dcpOrder.setChannelIdName(channelId_pos_shop_name);
                           }
                        }
                    }
                    dcpOrder.setChannelId(channelId);
                    dcpOrder.setLoadDocBillType(loadDocBillType);
                    dcpOrder.setLoadDocOrderNo(orderNo);
                    dcpOrder.setShopNo(shopNo);
                    dcpOrder.setShopName(map.getOrDefault("SHOPNAME","").toString());
                    dcpOrder.setMachShopNo(machShopNo);
                    dcpOrder.setMachShopName(map.getOrDefault("MACHSHOPNAME","").toString());
                    dcpOrder.setShippingShopNo(shipppingShopNo);
                    dcpOrder.setShippingShopName(map.getOrDefault("SHIPPINGSHOPNAME","").toString());
                    dcpOrder.setOutDocType(map.getOrDefault("OUTDOCTYPE","").toString());
                    dcpOrder.setOutDocTypeName(map.getOrDefault("OUTDOCTYPENAME","").toString());
                    String shipType = map.getOrDefault("SHIPTYPE","").toString();
                    //2.0的配送方式 1.外卖平台配送 2.配送 3.顾客自提 5总部配送
                    //3.0的配送方式 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
                    if ("2".equals(shipType))
                    {
                        shipType = "1";
                    }
                    dcpOrder.setShipType(shipType);
                    dcpOrder.setShipDate(map.getOrDefault("SHIPDATE","").toString());
                    String shipTime = map.getOrDefault("SHIPTIME","").toString();
                    shipTime = shipTime.replace(":","");
                    String shipStartTime = shipTime;
                    String shipEndTime = shipTime;
                    try
                    {
                        String[] ss = shipTime.split("-");
                        if (ss.length>=2)
                        {
                            shipStartTime = ss[0];
                            shipEndTime = ss[1];
                        }

                    }
                    catch (Exception e)
                    {

                    }
                    if (shipStartTime!=null&&shipStartTime.length()>8)
                    {
                        shipStartTime = shipStartTime.substring(0,8);
                    }
                    if (shipEndTime!=null&&shipEndTime.length()>8)
                    {
                        shipEndTime = shipEndTime.substring(0,8);
                    }
                    dcpOrder.setShipStartTime(shipStartTime);
                    dcpOrder.setShipEndTime(shipEndTime);
                    dcpOrder.setCreateDatetime(map.getOrDefault("CREATE_DATETIME","").toString());
                    dcpOrder.setMachineNo(map.getOrDefault("MACHINE","").toString());
                    dcpOrder.setGetMan(map.getOrDefault("GETMAN","").toString());
                    dcpOrder.setGetManTel(map.getOrDefault("GETMANTEL","").toString());
                    dcpOrder.setContMan(map.getOrDefault("CONTMAN","").toString());
                    dcpOrder.setContTel(map.getOrDefault("CONTTEL","").toString());
                    dcpOrder.setCity(map.getOrDefault("CITY","").toString());
                    dcpOrder.setProvince(map.getOrDefault("PROVINCE","").toString());
                    dcpOrder.setCounty(map.getOrDefault("COUNTY","").toString());
                    dcpOrder.setStreet(map.getOrDefault("STREET","").toString());
                    dcpOrder.setAddress(map.getOrDefault("ADDRESS","").toString());
                    dcpOrder.setLongitude(map.getOrDefault("LONGITUDE","0").toString());
                    dcpOrder.setLatitude(map.getOrDefault("LATITUDE","0").toString());
                    dcpOrder.setManualNo(map.getOrDefault("MANUALNO","").toString());
                    dcpOrder.setMemo(map.getOrDefault("MEMO","").toString());
                    dcpOrder.setSn(map.getOrDefault("ORDER_SN","").toString());
                    dcpOrder.setbDate(map.getOrDefault("ORDERBDATE","").toString());//营业日期
                    dcpOrder.setBelfirm(map.getOrDefault("BELFIRM","").toString());
                    dcpOrder.setCardNo(map.getOrDefault("CARDNO","").toString());
                    dcpOrder.setMemberId(map.getOrDefault("MEMBERID","").toString());
                    dcpOrder.setMemberName(map.getOrDefault("MEMBERNAME","").toString());
                    dcpOrder.setIsBook(map.getOrDefault("ISBOOK","").toString());
                    dcpOrder.setPayStatus(map.getOrDefault("PAYSTATUS","").toString());
                    dcpOrder.setSellCredit(map.getOrDefault("SELLCREDIT","N").toString());//是否赊销
                    dcpOrder.setSellNo(map.getOrDefault("SELLNO","").toString());//大客户编码
                    dcpOrder.setCustomer(map.getOrDefault("SELLNO","").toString());//赊销人id 2.0 没有
                    dcpOrder.setCustomerName(map.getOrDefault("SELLCREDITNAME","").toString());//赊销人名称
                    dcpOrder.setIsShipCompany(map.getOrDefault("ISSHIPCOMPANY","N").toString());
                    dcpOrder.setOpNo("admin");//2.0 没有这个字段
                    dcpOrder.setMemberPayNo(map.getOrDefault("MEMBERPAYNO","").toString());
                    dcpOrder.setProcess_status("E");//默认不上传

                    try
                    {
                        dcpOrder.setPointQty(Double.parseDouble(map.getOrDefault("POINTQTY","0").toString()));
                    } catch (Exception e)
                    {
                        dcpOrder.setPointQty(0);
                    }
                    try
                    {
                        dcpOrder.setTot_qty(Double.parseDouble(map.getOrDefault("TOT_QTY","0").toString()));
                    } catch (Exception e)
                    {
                        dcpOrder.setPayAmt(0);
                    }
                    try
                    {
                        dcpOrder.setEraseAmt(Double.parseDouble(map.getOrDefault("ERASE_AMT","0").toString()));//抹零
                    } catch (Exception e)
                    {
                        dcpOrder.setEraseAmt(0);
                    }
                    try
                    {
                        dcpOrder.setIncomeAmt(Double.parseDouble(map.getOrDefault("INCOMEAMT","0").toString()));
                    } catch (Exception e)
                    {
                        dcpOrder.setIncomeAmt(0);
                    }
                    try
                    {
                        dcpOrder.setPackageFee(Double.parseDouble(map.getOrDefault("PACKAGEFEE","0").toString()));
                    } catch (Exception e)
                    {
                        dcpOrder.setPackageFee(0);
                    }
                    try
                    {
                        dcpOrder.setPayAmt(Double.parseDouble(map.getOrDefault("PAYAMT","0").toString()));
                    } catch (Exception e)
                    {
                        dcpOrder.setPayAmt(0);
                    }
                    try
                    {
                        dcpOrder.setTot_Amt(Double.parseDouble(map.getOrDefault("TOT_AMT","0").toString()));
                    } catch (Exception e)
                    {
                        dcpOrder.setPayAmt(0);
                    }
                    dcpOrder.setTot_oldAmt(dcpOrder.getTot_Amt());
                    dcpOrder.setTotDisc(0);//转3.0 不要折扣了
                    dcpOrder.setVerNum("FROM-2.0");//特殊标记
                    dcpOrder.setIsApportion("Y");//不需要展开套餐

                    //region 商品明细
                    for (Map<String,Object> detail : getOrderGoodsDetail)
                    {
                        orderGoodsItem goods=new orderGoodsItem();
                        goods.setAgioInfo(new ArrayList<orderGoodsItemAgio>());
                        goods.setMessages(new ArrayList<orderGoodsItemMessage>());
                        String item = detail.get("ITEM").toString();
                        goods.setItem(item);
                        goods.setPluBarcode(detail.get("PLUBARCODE").toString());
                        goods.setPluName(detail.get("PLUNAME").toString());
                        goods.setPluNo(detail.get("PLUNO").toString());
                        goods.setAccNo("");
                        try
                        {
                            goods.setAmt(Double.parseDouble(detail.get("DETAILAMT").toString()));
                        } catch (Exception e)
                        {
                            goods.setAmt(0);
                        }

                        goods.setAttrName(detail.get("ATTRNAME").toString());
                        goods.setSpecName(detail.get("SPECNAME").toString());
                        try
                        {
                            goods.setBoxNum(Double.parseDouble(detail.getOrDefault("BOXNUM","0").toString()));
                        } catch (Exception e)
                        {
                            goods.setBoxNum(0);
                        }
                        try
                        {
                            goods.setBoxPrice(Double.parseDouble(detail.getOrDefault("BOXPRICE","0").toString()));
                        } catch (Exception e)
                        {
                            goods.setBoxPrice(0);
                        }

                        goods.setCounterNo("");
                        goods.setCouponCode(detail.get("COUPONCODE").toString());
                        goods.setCouponType(detail.get("COUPONTYPE").toString());

                        goods.setDisc(0);//全部转成没有折扣的
                        goods.setFeatureName("");
                        goods.setFeatureNo(" ");//2.0没有，默认空格
                        goods.setGift(detail.getOrDefault("GIFT","").toString());
                        goods.setGiftReason("");
                        goods.setGiftSourceSerialNo(detail.getOrDefault("GIFTSOURCESERIALNO","").toString());
                        goods.setGoodsGroup(detail.getOrDefault("GOODSGROUP","").toString());
                        goods.setGoodsUrl(detail.getOrDefault("GOODSURL","").toString());
                        //goods.setInclTax("");
                        //goods.setInvNo(detail.get("INVNO").toString());
                        //goods.setInvSplitType("");
                        goods.setIsMemo(detail.get("ISMEMO").toString());

                        //goods.setoItem(detail.get("OITEM").toString());
                        //goods.setoReItem(detail.get("OREITEM").toString());
                        try
                        {
                            BigDecimal delPrice_b = new BigDecimal(detail.get("DEALPRICE").toString());
                            goods.setPrice(delPrice_b.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue());
                        } catch (Exception e)
                        {
                            goods.setPrice(0);
                        }
                        goods.setOldAmt(goods.getAmt());
                        goods.setOldPrice(goods.getPrice());
                        goods.setPackageMitem(detail.get("PACKAGEMITEM").toString());
                        goods.setPackageType(detail.get("PACKAGETYPE").toString());
                        try
                        {
                            goods.setQty(Double.parseDouble(detail.get("QTY").toString()));
                        } catch (Exception e)
                        {
                            goods.setQty(0);
                        }

                        try
                        {
                            goods.setrQty(Double.parseDouble(detail.get("RQTY").toString()));
                        } catch (Exception e)
                        {
                            goods.setrQty(0);
                        }
                        try
                        {
                            goods.setPickQty(Double.parseDouble(detail.get("PICKQTY").toString()));
                        } catch (Exception e)
                        {
                            goods.setPickQty(0);
                        }
                        try
                        {
                            goods.setShopQty(Double.parseDouble(detail.get("SHOPQTY").toString()));
                        } catch (Exception e)
                        {
                            goods.setShopQty(0);
                        }


                        goods.setSellerName("");
                        goods.setSellerNo(detail.getOrDefault("CLERKNO","").toString());//营业员编号

                        //goods.setsTime("");
                        goods.setsUnit(detail.get("UNIT").toString());
                        goods.setsUnitName(detail.get("UNITNAME").toString());
                        //goods.setTaxCode("");
                        //goods.setTaxType("");
                        goods.setToppingMitem(detail.get("TOPPINGMITEM").toString());
                        goods.setToppingType(detail.get("TOPPINGTYPE").toString());
                        goods.setSkuId(detail.getOrDefault("SKUID","").toString());
                        for (Map<String, Object> detail_Memo : getOrderGoodsDetailMemo)
                        {
                            String E_OITEM = detail_Memo.get("DOITEM").toString();
                            String E_ITEM = detail_Memo.get("DITEM").toString();
                            if(E_OITEM==null||E_OITEM.isEmpty()||E_ITEM==null||E_ITEM.isEmpty())
                            {
                                continue;
                            }

                            if(E_OITEM.equals(item))
                            {
                                orderGoodsItemMessage memos=new orderGoodsItemMessage();
                                memos.setMessage(detail_Memo.get("DMEMO").toString());
                                memos.setMsgName(detail_Memo.get("DMEMONAME").toString());
                                memos.setMsgType(detail_Memo.get("DMEMOTYPE").toString());

                                goods.getMessages().add(memos);
                                memos=null;
                            }

                        }
                        dcpOrder.getGoodsList().add(goods);
                        goods=null;

                    }
                    //endregion

                    //region 付款明细
                    for (Map<String, Object> payDetail : getOrderPay)
                    {
                        String payItem = payDetail.get("PAYITEM").toString();
                        if (payItem==null||payItem.isEmpty())
                        {
                            continue;
                        }
                        orderPay paylist= new orderPay();
                        paylist.setAuthCode("");
                        paylist.setbDate(payDetail.get("BDATE").toString());
                        //paylist.setCanInvoice(payDetail.get("D_CANINVOICE").toString());
                        //paylist.setCardBeforeAmt(payDetail.get("D_CARDBEFOREAMT").toString());
                        paylist.setCardNo(payDetail.get("PAYCARDNO").toString());
                        //paylist.setCardRemainAmt(payDetail.get("D_CARDREMAINAMT").toString());
                        paylist.setChanged(payDetail.get("CHANGED").toString());
                        //paylist.setCouponQty(payDetail.get("D_COUPONQTY").toString());
                        paylist.setCtType(payDetail.get("CTTYPE").toString());
                        paylist.setDescore(payDetail.get("DESCORE").toString());
                        paylist.setExtra(payDetail.get("EXTRA").toString());
                        paylist.setIsOnlinePay(payDetail.get("ISONLINEPAY").toString());
                        //paylist.setIsOrderpay(payDetail.get("D_ISORDERPAY").toString());
                        //paylist.setIsVerification(payDetail.get("D_ISVERIFICATION").toString());
                        paylist.setItem(payItem);
                        //paylist.setLoadDocType(payDetail.get("D_LOADDOCTYPE").toString());
                        paylist.setOrder_payCode(payDetail.get("ORDER_PAYCODE").toString());
                        paylist.setPay(payDetail.get("PAY").toString());
                        //paylist.setPayAmt1(payDetail.get("D_PAYAMT1").toString());
                        //paylist.setPayAmt2(payDetail.get("D_PAYAMT2").toString());
                        paylist.setPayCode(payDetail.get("PAYCODE").toString());
                        paylist.setPayCodeErp(payDetail.get("PAYCODEERP").toString());
                        //paylist.setPayDiscAmt(payDetail.get("D_PAYDISCAMT").toString());
                        paylist.setPayName(payDetail.get("PAYNAME").toString());
                        paylist.setPaySerNum(payDetail.get("PAYSERNUM").toString());
                        paylist.setRefNo(payDetail.get("REFNO").toString());
                        paylist.setSerialNo(payDetail.get("SERIALNO").toString());
                        paylist.setTeriminalNo(payDetail.get("TERIMINALNO").toString());
                        paylist.setPaydoctype(payDetail.get("LOAD_DOCTYPE_PAY").toString());
                        paylist.setFuncNo("");//2.0没有
                        paylist.setCardSendPay(payDetail.get("SENDPAY").toString());
                        paylist.setPayType(paylist.getPayCode());//2.0 没有 默认给paycode

                        dcpOrder.getPay().add(paylist);
                        paylist=null;

                    }
                    //endregion

                    List<order> orderList = new ArrayList<order>();
                    orderList.add(dcpOrder);
                    StringBuffer errorMessage=new StringBuffer();
                    ArrayList<DataProcessBean> DPB = com.dsc.spos.waimai.HelpTools.GetInsertOrderCreat(orderList,errorMessage,null);
                    if (DPB != null && DPB.size() > 0) {
                        try {
                            StaticInfo.dao.useTransactionProcessData(DPB);
                            log("【同步成功】【保存数据库成功】，单号orderNo=" + orderNo);
                            this.updateOldOrderMoveStatus(newEID,oldEID,orderNo,load_docType,"Y");
                        } catch (Exception e) {
                            log("【同步失败】异常:"+e.getMessage()+",单号orderNo=" + orderNo);
                            this.updateOldOrderMoveStatus(newEID,oldEID,orderNo,load_docType,"E");
                        }
                    }
                    else
                    {
                        log("【同步失败】组装sql失败,单号orderNo=" + orderNo);
                        this.updateOldOrderMoveStatus(newEID,oldEID,orderNo,load_docType,"E");
                    }

                }
                catch (Exception e)
                {
                    StringWriter errors = new StringWriter();
                    PrintWriter pw = new PrintWriter(errors);
                    e.printStackTrace(pw);

                    pw.flush();
                    pw.close();

                    errors.flush();
                    errors.close();
                    log("【循环同步】异常:"+e.getMessage()+ "\r\n" + errors+"，单号orderNo=" + orderNo+",渠道load_docType="+load_docType);
                    continue;
                }

            }


        }
        catch (Exception e)
        {
            log("【同步任务Movedb_Order】异常:"+e.getMessage());
        }
    }

    private static Map<String,Map<String,String>> posChannelIdByShop;
    private Map<String,String> getPosChannelId(String newEID,String shop) throws Exception
    {
        if (posChannelIdByShop==null)
        {
            posChannelIdByShop = new HashMap<>();
        }
        if (shop==null||shop.trim().isEmpty())
        {
            return null;
        }
        if (posChannelIdByShop.containsKey(shop))
        {
            Map<String,String> nRet = posChannelIdByShop.get(shop);
            return nRet;
        }
        //根据门店获取POS渠道类型对应的接口账号，对应的渠道编码
        String sql_appType =" select a.CHANNELID,a.CHANNELNAME,c.SHOPID from crm_channel a "
                + " left join crm_apiuser b on a.eid=b.eid and a.channelid=b.channelid and a.status=b.status and a.appno=b.apptype "
                + " left join crm_apiusershop c on c.eid=b.eid and c.usercode=b.usercode"
                + " where a.appno='POS' and a.status=100 and b.shoptype='1' and a.eid='"+newEID+"' and c.shopid='"+shop+"'";
        log("【同步任务Movedb_Order】查询3.0渠道crm_channel表POS渠道,订单下单门店对应渠道编码channelId的sql:"+sql_appType);
        List<Map<String,Object>> getAppType_POS = this.doQueryData(sql_appType,null);
        if (getAppType_POS==null||getAppType_POS.isEmpty())
        {
            log("【同步任务Movedb_Order】查询3.0渠道crm_channel表POS渠道为空,订单下单门店对应渠道编码channelId为空,取默认的POS渠道类型对应的渠道编码");
            return null;
        }
        else
        {
            String CHANNELID = getAppType_POS.get(0).get("CHANNELID").toString();
            String CHANNELNAME = getAppType_POS.get(0).get("CHANNELNAME").toString();
            Map<String,String> map_value = new HashMap<>();
            map_value.put("CHANNELID",CHANNELID);
            map_value.put("CHANNELNAME",CHANNELNAME);
            posChannelIdByShop.put(shop,map_value);
            return map_value;
        }


    }

    private String getLoadDocType(String loadDocType)
    {
        String newLoadDocType = "";
        switch (loadDocType)
        {
            case "3":
                newLoadDocType = "MINI";
                break;
            case "4":
                newLoadDocType = "POS";
                break;
            default:
                newLoadDocType = "";
                break;
        }
        return newLoadDocType;
    }

    private void log(String message)
    {
        try
        {
            HelpTools.writelog_fileName(message, jddjLogFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 同步是否成功都要回写2.0同步状态
     * @param newEID 3.0企业id
     * @param oldEID 2.0企业id
     * @param orderNo 单号
     * @param load_doctype 2.0渠道类型
     * @param moveDBStatus
     * @throws Exception
     */
    private void updateOldOrderMoveStatus(String newEID,String oldEID,String orderNo,String load_doctype,String moveDBStatus) throws Exception
    {
        String descStr = "同步成功";
        if ("Y".equals(moveDBStatus))
        {
            descStr = "【同步成功】";
        }
        else
        {
            descStr = "【同步失败】";
        }
        try
        {
            // values
            Map<String, DataValue> values = new HashMap<String, DataValue>();
            values.put("MOVEDBSTATUS", new DataValue(moveDBStatus, Types.VARCHAR));
            // condition
            Map<String, DataValue> conditions = new HashMap<String, DataValue>();
            conditions.put("COMPANYNO", new DataValue(oldEID, Types.VARCHAR));
            conditions.put("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
            conditions.put("LOAD_DOCTYPE", new DataValue(load_doctype, Types.VARCHAR));
            StaticInfo.dao_pos2.update("TV_ORDER",values,conditions);
            log(descStr+"【回写2.0订单表】成功，更新MOVEDBSTATUS="+moveDBStatus+",单号orderNo="+orderNo+",渠道load_doctype="+load_doctype);
        }
        catch (Exception e)
        {
            log(descStr+"【回写2.0订单表】异常:"+e.getMessage()+"，更新MOVEDBSTATUS="+moveDBStatus+",单号orderNo="+orderNo+",渠道load_doctype="+load_doctype);
        }
    }

    /**
     * 判断下是不是已存在，如果存在也要回写2.0订单
     * @param newEID 3.0的eid
     * @param oldEID 2.0的eid
     * @param orderNo 单号
     * @param load_doctype 2.0渠道类型
     * @return
     * @throws Exception
     */
    private boolean isExistOrder (String newEID,String oldEID,String orderNo,String load_doctype) throws Exception
    {
        boolean nRet = false;
        try
        {
            String sql = " select * from dcp_order where eid='"+newEID+"' and orderno='"+orderNo+"'";
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            if (getQData==null||getQData.isEmpty())
            {

            }
            else
            {
                nRet = true;
                log("3.0已存在，无需同步，单号orderNo=" + orderNo);
                String moveDBStatus = "Y";
                this.updateOldOrderMoveStatus(newEID,oldEID,orderNo,load_doctype,moveDBStatus);
            }

        }
        catch (Exception e)
        {
            log("【判断订单是否已同步】异常:"+e.getMessage()+"，单号orderNo=" + orderNo);
        }
        return nRet;

    }
}
