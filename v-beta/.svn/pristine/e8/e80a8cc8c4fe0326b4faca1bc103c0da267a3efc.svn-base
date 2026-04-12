package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.model.SALE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 2.0升级到3.0 销售数据导入
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class Movedb_Sale extends InitJob
{
    Logger logger = LogManager.getLogger(Movedb_Sale.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中


    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    public Movedb_Sale()
    {

    }

    public Movedb_Sale(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }


    public String doExe()
    {
        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********2.0升级到3.0 销售数据导入Movedb_Sale正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-2.0升级到3.0 销售数据导入Movedb_Sale正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********2.0升级到3.0 销售数据导入Movedb_Sale定时调用Start:************\r\n");

        try
        {
            //取配置表资料
            List<Map<String, Object>> map_movedbsetting=StaticInfo.dao.executeQuerySQL("select * from DCP_MOVEDBSETTING where STATUS=100 ",null);
            if (map_movedbsetting != null && map_movedbsetting.size()>0)
            {
                for (Map<String, Object> map1 : map_movedbsetting)
                {
                    String neweid=map1.get("NEWEID").toString();
                    String oldeid=map1.get("OLDEID").toString();
                    String startdate=map1.get("STARTDATE").toString();

                    //取配置表门店范围资料,门店没配置就是全部门店
                    List<Map<String, Object>> map_movedbsetting_shop=StaticInfo.dao.executeQuerySQL("select * from DCP_MOVEDBSETTING_SHOP ",null);
                    if (map_movedbsetting_shop != null && map_movedbsetting_shop.size()>0)
                    {
                        for (Map<String, Object> map2 : map_movedbsetting_shop)
                        {
                            String newshopid=map2.get("NEWSHOPID").toString();

                            StringBuffer sb=new StringBuffer("");
                            sb.append("select rownum rn , t1.* from " +
                                              "(select * from td_sale where companyno='"+oldeid+"' and shop='"+newshopid+"' and nvl(movedbstatus,'@#$')='@#$' " );
                            if (Check.Null(startdate)==false && startdate.length()==8)
                            {
                                sb.append(" and bdate='"+startdate+"' ");
                            }
                            sb.append("union all " +
                                              "select * from td_sale where companyno='"+oldeid+"' and shop='"+newshopid+"' and movedbstatus='N' " );
                            if (Check.Null(startdate)==false && startdate.length()==8)
                            {
                                sb.append(" and bdate='"+startdate+"' ");
                            }
                            sb.append( ") t1  where rownum<= 200 ");


                            //只查is null 和 N的，E的是导入错误的不传了
                            List<Map<String, Object>> map_sale=StaticInfo.dao_pos2.executeQuerySQL(sb.toString(),null);


                            //数据处理
                            dealData(map_sale,neweid,oldeid);

                        }
                    }
                    else
                    {
                        StringBuffer sb=new StringBuffer("");
                        sb.append("select rownum rn , t1.* from " +
                                          "(select * from td_sale where companyno='"+oldeid+"' and nvl(movedbstatus,'@#$')='@#$' " );
                        if (Check.Null(startdate)==false && startdate.length()==8)
                        {
                            sb.append(" and bdate='"+startdate+"' ");
                        }
                        sb.append("union all " +
                                          "select * from td_sale where companyno='"+oldeid+"' and movedbstatus='N' " );
                        if (Check.Null(startdate)==false && startdate.length()==8)
                        {
                            sb.append(" and bdate='"+startdate+"' ");
                        }
                        sb.append( ") t1  where rownum<= 200 ");

                        //只查is null 和 N的，E的是导入错误的不传了
                        List<Map<String, Object>> map_sale=StaticInfo.dao_pos2.executeQuerySQL(sb.toString(),null);

                        //数据处理
                        dealData(map_sale,neweid,oldeid);
                    }

                }
            }
            else
            {
                logger.info("\r\n*********2.0升级到3.0 销售数据导入Movedb_Sale配置表资料DCP_MOVEDBSETTING没有数据:************\r\n");
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

                logger.error("\r\n******2.0升级到3.0 销售数据导入Movedb_Sale报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******2.0升级到3.0 销售数据导入Movedb_Sale报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();

        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********2.0升级到3.0 销售数据导入Movedb_Sale定时调用End:************\r\n");
        }
        return sReturnInfo;
    }



    //门店所属公司
    Map<String, Object> map_belfirm=new HashMap<>();
    //渠道id,根据渠道取默认第一条
    Map<String, Object> map_channel=new HashMap<>();
    //功能编码,根据paycode取默认第一条
    Map<String, Object> map_funcno=new HashMap<>();

    private void dealData(List<Map<String, Object>> map_sale,String neweid,String oldeid)
    {
        if (map_sale == null || map_sale.size()==0)
        {
            logger.info("\r\n*********2.0升级到3.0 销售数据导入Movedb_Sale没有需要导入的销售数据:************\r\n");
            return;
        }
        for (Map<String, Object> map : map_sale)
        {
            List<DataProcessBean> data = new ArrayList<DataProcessBean>();
            //标记单据导入处理成功
            boolean billToSale_ok=false;
            String saleno="";
            String a_shop="";
            String lastmoditime =new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            try
            {
                SALE sale =new SALE();
                saleno=map.get("SALENO").toString();
                a_shop=map.get("SHOP").toString();

                //查找销售单,存在就不管了
                String sql_sale="select * from dcp_sale where eid='"+neweid+"' and SHOPID='"+a_shop+"' and saleno='"+saleno+"'";
                List<Map<String, Object>> sqlsale=StaticInfo.dao.executeQuerySQL(sql_sale, null);
                if (sqlsale != null && sqlsale.size()>0)
                {
                    billToSale_ok=true;
                    continue;
                }
                sale.setTrno(map.get("TRNO").toString());
                sale.setIsMerPay("N");
                sale.setEraseAmt(map.get("ERASE_AMT").toString());
                sale.setApproval(map.get("APPROVAL").toString());//钉钉审批id
                sale.setAuthorizerOpno(map.get("AUTHORIZEROPNO").toString());//授权人编码
                sale.setGetMode(map.get("GETMODE").toString());//配送方式
                sale.setGetShop(map.get("GETSHOP").toString());//
                sale.setEvaluate(map.get("EVALUATE").toString());//是否评价0-否1-是
                sale.setEcsDate(map.get("ECSDATE").toString());//订单结案日期
                sale.setEcsFlg(map.get("ECSFLG").toString());//订单结案否Y/N
                sale.setCardAmount(map.get("CARDAMOUNT").toString());//初始余额
                sale.setCardTypeId("");//卡类型编码
                //桌台相关
                sale.setTableKind(map.get("TABLEKIND").toString());//桌台类型
                sale.setTableNO(map.get("TABLENO").toString());//桌台号
                sale.setOpenerId("");//开台人
                sale.setDinnerDate(map.get("DINNERDATE").toString());//就餐日期
                sale.setDinnerSign(map.get("SIGN").toString());//就餐牌号码
                sale.setDinnerTime(map.get("DINNERTIME").toString());//就餐时间
                sale.setDinnerType(map.get("DINNERTYPE").toString());//餐饮单据状态
                sale.setChildNumber(map.get("CHILDNUMBER").toString());//儿童数量
                sale.setServCharge(map.get("SERVCHARGE").toString());//餐厅服务费
                sale.setGuestNum(map.get("GUESTNUM").toString());//桌台人数
                //发票相关
                sale.setTaxRegNumber(map.get("TAXREGNUMBER").toString());//纳税人识别号
                sale.setFreeCode(map.get("FREECODE").toString());//零税证号-只有外交官才有的凭证--零税的其中一种凭证
                sale.setPassport(map.get("PASSPORT").toString());//护照编号-零税的其中一种凭证
                sale.setIsInvoice(map.get("ISINVOICE").toString());//是否需要发票Y/N
                sale.setIsInvoiceMakeOut(map.get("ISINVOICEMAKEOUT").toString());//是否已开票
                sale.setInvCount(map.get("INVCOUNT").toString());//发票数量
                sale.setInvoiceAccount(map.get("INVOICEACCOUNT").toString());//发票开户账号
                sale.setInvoiceAddr(map.get("INVOICEADDR").toString());//发票公司地址
                sale.setInvoiceBank(map.get("INVOICEBANK").toString());//发票开户行
                sale.setInvoiceTel(map.get("INVOICETEL").toString());//发票联系方式
                sale.setInvoiceTitle(map.get("INVOICETITLE").toString());//发票抬头
                sale.setInvSplitType(map.get("INVSPLITTYPE").toString());//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分
                sale.setoInvstartno(map.get("INVSTARTNO").toString());//原发票号码
                //旅游团相关
                sale.setTourCountryCode(map.get("TOUR_COUNTRYCODE").toString());//国家编码
                sale.setTourGroupNO(map.get("TOUR_GROUPNO").toString());//团号
                sale.setTourGuideNO(map.get("TOUR_GUIDENO").toString());//导游编号
                sale.setTourPeopleNum(map.get("TOUR_PEOPLENUM").toString());//进店人数
                sale.setTourTravelNO(map.get("TOUR_TRAVELNO").toString());//旅行社编码
                //外卖
                sale.setTakeAway(map.get("TAKEAWAY").toString());//外卖平台
                sale.setIsTakeout(map.get("ISTAKEOUT").toString());//是否外卖订单
                sale.setWxOpenId(map.get("OPENID").toString());//第三方用户id
                sale.setWmExtraFee(map.get("WM_EXTRA_FEE").toString());//外卖：平台其他费用(每单捐赠1分钱支持环保之类的活动)
                sale.setWmUserPaid(map.get("WM_USER_PAID").toString());//外卖：用户实际支付
                sale.setDeliveryFeeShop(map.get("DELIVERY_FEE_SHOP").toString());//外卖：门店承担配送费
                sale.setOrderSn(map.get("ORDER_SN").toString());//外卖订单流水号
                //
                sale.setIsUploaded(map.get("ISUPLOADED").toString());//是否已传第三方（例如商场）Y已上传，N未上传
                sale.setLegalper(map.get("LEGALPER").toString());//法人
                sale.setIsReturn(map.get("ISRETURN").toString());//是否已退Y/N
                sale.setOrderAmount(map.get("ORDERAMOUNT").toString());//订金金额

                //这个需要转换
                String s_orderChannel=map.get("ORDERCHANNEL").toString();

                switch (s_orderChannel)
                {
                    case "32"://饿了么
                        s_orderChannel="ELEME" ;
                        break;
                    case "31"://美团
                        s_orderChannel="MEITUAN" ;
                        break;
                    case "33"://微商城
                        s_orderChannel="WECHAT" ;
                        break;
                    case "0"://云POS
                        s_orderChannel="POS" ;
                        break;
                    case "30"://云中台
                        s_orderChannel="OWNCHANNEL" ;
                        break;
                    case "34"://官网
                        s_orderChannel="OFFICIAL" ;
                        break;
                    case "35"://舞像
                        s_orderChannel="WUXIANG" ;
                        break;
                    case "36"://京东到家
                        s_orderChannel="JDDJ" ;
                        break;
                    case "2"://APP小程序
                        s_orderChannel="MINI" ;
                        break;
                    case "37"://京东商城
                        s_orderChannel="JDMALL" ;
                        break;
                    case "38"://PAD导购
                        s_orderChannel="PADGUIDE" ;
                        break;
                    case "112"://鼎捷外卖
                        s_orderChannel="WAIMAI" ;
                        break;
                    case "111"://管易云
                        s_orderChannel="GUANYIYUN" ;
                        break;
                    case "107"://提货券
                        s_orderChannel="GIFTCOUPON" ;
                        break;
                    case "100":
                        s_orderChannel="YAHOO" ;
                        break;
                    case "101":
                        s_orderChannel="91APP" ;
                        break;
                    case "102":
                        s_orderChannel="XIAPI" ;
                        break;
                    case "105":
                        s_orderChannel="LETIAN" ;
                        break;
                    case "103":
                        s_orderChannel="PCHOME" ;
                        break;
                    case "104":
                        s_orderChannel="MOMO" ;
                        break;
                    case "108":
                        s_orderChannel="PCHOME" ;//pchome24销售购物中心
                        break;
                    case "110":
                        s_orderChannel="YAHOO" ;//雅虎购物中心
                        break;

                    default:
                        s_orderChannel="0" ;
                        break;
                }

                sale.setOrder_appType(s_orderChannel);//下单应用类型

                sale.setoBdate(map.get("OBDATE").toString());//原单营业日期
                sale.setoMachine(map.get("OMACHINE").toString());//原单机台号
                sale.setoOrder_appType(lastmoditime + "-2.0");//来源应用类型
                sale.setoOrder_channelId("");//来源渠道编码
                sale.setoOrder_companyId("");//来源公司编码
                sale.setTeriminalID(map.get("TERIMINAL_ID").toString());//终端ID
                sale.setTotChanged(map.get("TOT_CHANGED").toString());//找零金额
                sale.setOrderReturn("");////订金退款类型：0:沒有訂金1:部份訂金2:全部訂金【CM专用】
                sale.setOrderShop(map.get("ORDERSHOP").toString());//下订门店
                sale.setoShop(map.get("OSHOP").toString());//原单门店
                sale.setOtrno(map.get("OTRNO").toString());//原单流水号
                sale.setoType(map.get("OTYPE").toString());//原单类型
                sale.setPayDisc(map.get("PAYDISC").toString());//付款折扣金额
                sale.setProductionMode(map.get("PRODUCTIONMODE").toString());//生产方式：0本店制作1异店制作2总部制作
                sale.setProductionShop(map.get("PRODUCTIONSHOP").toString());//生产门店
                sale.setRemainPoint(map.get("REMAINPOINT").toString());//剩余积分
                sale.setRepastType(map.get("REPAST_TYPE").toString());//就餐类型：0堂食1打包2外卖
                sale.setRsvId(map.get("RSV_ID").toString());//预订ID
                //
                sale.setIsBuff(map.get("ISBUFFER").toString());//是否暂存
                sale.setIsBuff_timeout(map.get("BUFFER_TIMEOUT").toString());//暂存超时时间
                sale.setbDate(map.get("BDATE").toString());
                sale.setCardNO(map.get("CARDNO").toString());
                sale.setContMan(map.get("CONTMAN").toString());
                sale.setContTel(map.get("CONTTEL").toString());
                sale.setCustomerName(map.get("CUSTOMERNAME").toString());
                sale.setCustomerNO(map.get("CUSTOMERNO").toString());
                sale.setDeliveryFeeUser(map.get("DELIVERY_FEE_USER").toString());
                sale.setDistribution(map.get("DISTRIBUTION").toString());
                sale.setMealNumber(map.get("MEALNUMBER").toString());
                sale.setBsno(map.get("BSNO").toString());
                sale.seteId(neweid);
                sale.setEcCustomerNO(map.get("ECCUSTOMERNO").toString());
                sale.setgDate(map.get("GDATE").toString());
                sale.setgTime(map.get("GTIME").toString());
                sale.setGetMan(map.get("GETMAN").toString());
                sale.setGetManTel(map.get("GETMANTEL").toString());
                sale.setMachine(map.get("MACHINE").toString());
                sale.setManualNO(map.get("MANUALNO").toString());
                sale.setMemberId(map.get("MEMBERID").toString());
                sale.setMemberName(map.get("MEMBERNAME").toString());
                sale.setMemo(map.get("MEMO").toString());
                sale.setOfNO(map.get("OFNO").toString());
                sale.setOpNO(map.get("OPNO").toString());

                //渠道id
                String channelId="";
                String channelId_key=neweid+"_"+s_orderChannel;
                if (map_channel.containsKey(channelId_key))
                {
                    channelId=map_channel.get(channelId_key).toString();
                }
                else
                {
                    String sql_channel="select CHANNELID from crm_channel where eid='"+neweid+"' and APPNO='"+s_orderChannel+"' AND STATUS=100 ";
                    List<Map<String, Object>> sqllist_channel=StaticInfo.dao.executeQuerySQL(sql_channel, null);
                    if (sqllist_channel != null && sqllist_channel.size()>0)
                    {
                        channelId=sqllist_channel.get(0).get("CHANNELID").toString();
                    }
                    map_channel.put(channelId_key,channelId);
                }
                sale.setOrder_channelId(channelId);


                String belfirm="";
                String belfirm_key=neweid+"_"+a_shop;
                if (map_belfirm.containsKey(belfirm_key))
                {
                    belfirm=map_belfirm.get(belfirm_key).toString();
                }
                else
                {
                    String sql_org="select BELFIRM from dcp_org where eid='"+neweid+"' and organizationno='"+a_shop+"' ";
                    List<Map<String, Object>> sqllist_org=StaticInfo.dao.executeQuerySQL(sql_org, null);
                    if (sqllist_org != null && sqllist_org.size()>0)
                    {
                        belfirm=sqllist_org.get(0).get("BELFIRM").toString();
                    }
                    map_belfirm.put(belfirm_key,belfirm);
                }

                sale.setOrder_companyId(belfirm);
                sale.setOrderID(map.get("ORDER_ID").toString());
                sale.setPackageFee(map.get("PACKAGEFEE").toString());
                sale.setPlatformDisc(map.get("PLATFORM_DISC").toString());
                sale.setPlatformFee(map.get("PLATFORM_FEE").toString());
                sale.setsDate(map.get("SDATE").toString());
                sale.setSellCredit(map.get("SELLCREDIT").toString());
                sale.setReturnUserId(map.get("RETURNUSERID").toString());
                sale.setSendMemo(map.get("SENDMEMO").toString());
                sale.setShipAdd(map.get("SHIPADD").toString());
                sale.setShippingFee(map.get("SHIPPINGFEE").toString());
                sale.setDeliveryFeeShop(map.get("DELIVERY_FEE_SHOP").toString());
                sale.setShopId(a_shop);
                sale.setSourceSubOrderno(map.get("SOURCESUBORDERNO").toString());
                sale.setSquadNO(map.get("SQUADNO").toString());
                sale.setStatus("100");
                sale.setsTime(map.get("STIME").toString());
                sale.setType(map.get("TYPE").toString());
                sale.setTypeName(map.get("TYPENAME").toString());
                sale.setVerNum(map.get("VER_NUM").toString());
                sale.setWorkNO(map.get("WORKNO").toString());
                sale.setTran_time(map.get("TRAN_TIME").toString());
                sale.setWaimaiMerreceiveMode("0");
                //源于付款+抹零
                sale.setTotDisc_merReceive(map.get("TOT_DISC").toString());
                sale.setTotDisc_custPayReal(map.get("TOT_DISC").toString());

                //源于付款
                sale.setTot_Amt_merReceive(map.get("TOT_AMT").toString());
                sale.setTot_Amt_custPayReal(map.get("TOT_AMT").toString());
                //刷新销售单头字段
                sale.setTotOldAmt(new BigDecimal(map.get("TOT_AMT").toString()).add(new BigDecimal(map.get("TOT_DISC").toString())).toPlainString());
                sale.setTotAmt(map.get("TOT_AMT").toString());
                sale.setTotDisc(map.get("TOT_DISC").toString());
                sale.setPayAmt(map.get("PAY_AMT").toString());
                sale.setSaleDisc(map.get("SALEDISC").toString());//销售折扣金额
                sale.setSellerDisc(map.get("SELLER_DISC").toString());
                sale.setShopIncome(map.get("SHOPINCOME").toString());
                sale.setPointQty(map.get("POINT_QTY").toString());
                sale.setMemberOrderno(map.get("MEMBERORDERNO").toString());//调用积分memberpay的orderno
                sale.setPays(new ArrayList<>());
                sale.setDetails(new ArrayList<>());

                BigDecimal bdm_qty=new BigDecimal("0");
                //查明细
                String sql_detail="select * from td_sale_detail where companyno='"+oldeid+"' and saleno='"+saleno+"' ";
                List<Map<String, Object>> sqllist_detail=StaticInfo.dao_pos2.executeQuerySQL(sql_detail, null);

                //查折扣
                String sql_agio="select * from td_sale_detail_agio where companyno='"+oldeid+"' and saleno='"+saleno+"' ";
                List<Map<String, Object>> sqllist_agio=StaticInfo.dao_pos2.executeQuerySQL(sql_agio, null);

                if (sqllist_detail != null && sqllist_detail.size()>0)
                {
                    for (Map<String, Object> mapDetail : sqllist_detail)
                    {
                        SALE.Detail detail=sale.new Detail();
                        detail.setAccno(mapDetail.get("ACCNO").toString());
                        detail.setAmt(mapDetail.get("AMT").toString());
                        detail.setbDate(sale.getbDate());
                        detail.setBsno(mapDetail.get("BSNO").toString());
                        detail.setClerkName(mapDetail.get("SELLERNAME").toString());
                        detail.setClerkNo(mapDetail.get("CLERKNO").toString());
                        detail.setCounterNo(mapDetail.get("COUNTERNO").toString());
                        detail.setCouponCode(mapDetail.get("COUPONCODE").toString());
                        detail.setCouponType(mapDetail.get("COUPONTYPE").toString());
                        detail.setDetailItem(mapDetail.get("DETAILITEM").toString());
                        detail.setDisc(mapDetail.get("DISC").toString());
                        detail.seteId(neweid);
                        detail.setFeatureNo(mapDetail.get("FEATURENO").toString());
                        if (Check.Null(detail.getFeatureNo()))
                        {
                            detail.setFeatureNo(" ");
                        }
                        detail.setGiftReason(mapDetail.get("GIFTREASON").toString());
                        detail.setIsGift(mapDetail.get("ISGIFT").toString());
                        detail.setPackageMaster(mapDetail.get("PACKAGEMASTER").toString());
                        detail.setIsPackage(mapDetail.get("ISPACKAGE").toString());

                        //加料主商品项次
                        String detailitem=mapDetail.get("DETAILITEM").toString();
                        String isStuff="N";
                        if (Check.Null(detailitem) || detailitem.equals("0"))
                        {
                            isStuff="N";
                        }
                        else
                        {
                            isStuff="Y";
                        }
                        detail.setIsStuff(isStuff);//是否加料商品Y/N
                        detail.setItem(mapDetail.get("ITEM").toString());
                        detail.setInclTax(mapDetail.get("INCLTAX").toString());
                        String oldAmt=mapDetail.get("OLDAMT").toString();
                        if (Check.Null(oldAmt))
                        {
                            oldAmt="0";
                        }
                        detail.setOldAMT(oldAmt);
                        detail.setOldPrice(mapDetail.get("OLDPRICE").toString());
                        detail.setPackageAmount(mapDetail.get("PACKAGEAMOUNT").toString());//餐盒数量
                        detail.setPackagePrice(mapDetail.get("PACKAGEPRICE").toString());
                        detail.setPackageFee(mapDetail.get("PACKAGEFEE").toString());//餐盒总价
                        detail.setShopId(a_shop);
                        detail.setsDate(sale.getsDate());
                        detail.setsTime(sale.getsTime());
                        detail.setPluBarcode(mapDetail.get("PLUBARCODE").toString());
                        detail.setPluNo(mapDetail.get("PLUNO").toString());
                        detail.setpName(mapDetail.get("PNAME").toString());
                        detail.setPrice(mapDetail.get("PRICE").toString());
                        detail.setQty(mapDetail.get("QTY").toString());
                        detail.setTaxCode(mapDetail.get("TAXCODE").toString());
                        detail.setTaxRate(mapDetail.get("TAXRATE").toString());
                        detail.setTaxType(mapDetail.get("TAXTYPE").toString());
                        detail.setUnit(mapDetail.get("UNIT").toString());
                        detail.setUpItem(mapDetail.get("UPITEM").toString());
                        detail.setWarehouse(mapDetail.get("WAREHOUSE").toString());
                        detail.setStatus("100");//有效否100-有效0-无效
                        detail.setVirtual("N");
                        detail.setAmt_merReceive(mapDetail.get("AMT").toString());
                        detail.setAmt_custPayReal(mapDetail.get("AMT").toString());
                        detail.setDisc_merReceive(mapDetail.get("DISC").toString());
                        detail.setDisc_custPayReal(mapDetail.get("DISC").toString());
                        detail.setBaseUnit(mapDetail.get("WUNIT").toString());//基准单位
                        detail.setAdditionalPrice(mapDetail.get("ADDITIONALPRICE").toString());//加价
                        detail.setAttr01(mapDetail.get("ATTR01").toString());//商品特性1
                        detail.setAttr02(mapDetail.get("ATTR02").toString());//商品特性2
                        detail.setBatchNo(mapDetail.get("BATCHNO").toString());//批次号
                        detail.setCakeBlessing(mapDetail.get("CAKEBLESSING").toString());//蛋糕祝福
                        detail.setCanBack(mapDetail.get("CANBACK").toString());//是否可退Y/N
                        detail.setConfirmOpno(mapDetail.get("CONFIRMOPNO").toString());//确认人
                        detail.setConfirmTime(mapDetail.get("CONFIRMTIME").toString());//确认时间
                        detail.setCounterAmt(mapDetail.get("COUNTERAMT").toString());//专柜成交金额
                        detail.setDealType(mapDetail.get("DEALTYPE").toString());//商品交易类型：1销售2退货3赠送11免单
                        detail.setDishesStatus(mapDetail.get("DISHESSTATUS").toString());//菜品状态
                        detail.setFlavorStuffDetail(mapDetail.get("FLAVORSTUFFDETAIL").toString());//口味加料细节
                        detail.setGiftOpno(mapDetail.get("GIFTOPNO").toString());//赠送人编码
                        detail.setGiftTime(mapDetail.get("GIFTTIME").toString());//赠送时间
                        detail.setIsExchange("N");//是否换购品Y/N
                        detail.setIsPickGoods(mapDetail.get("ISPICKGOOD").toString());////是否现提商品Y/N
                        detail.setMaterials(mapDetail.get("MATERIALS").toString());//蛋糕坯子
                        detail.setMemo(mapDetail.get("MEMO").toString());//备注
                        detail.setMno(mapDetail.get("MNO").toString());//商场码
                        detail.setoItem(mapDetail.get("OITEM").toString());//来源项次
                        detail.setOrderRateAmount(mapDetail.get("ORDERRATEAMOUNT").toString());//订金分摊金额
                        detail.setOrderTime(mapDetail.get("ORDERTIME").toString());//预定时间
                        detail.setPackageAmt(mapDetail.get("PACKAGEAMT").toString());//套餐商品金额
                        detail.setPackageQty(mapDetail.get("PACKAGEQTY").toString());//套餐商品数量
                        detail.setPayDisc(mapDetail.get("PAYDISC").toString());//付款折扣
                        detail.setPointQty(mapDetail.get("POINT_QTY").toString());//积分
                        detail.setPrice2(mapDetail.get("PRICE2").toString());//底价
                        detail.setPrice3(mapDetail.get("PRICE3").toString());//最高退价
                        detail.setPromCouponNo(mapDetail.get("PROM_COUPONNO").toString());//促销赠券券号
                        detail.setRepastType(mapDetail.get("REPAST_TYPE").toString());//就餐类型：0堂食1打包2外卖
                        detail.setRefundOpno(mapDetail.get("REFUNDOPNO").toString());//退菜人编号
                        detail.setReturnTableNo(mapDetail.get("RETURNTABLENO").toString());//退货桌号
                        detail.setRefundTime(mapDetail.get("REFUNDTIME").toString());//退菜时间
                        detail.setReturnUserId(mapDetail.get("RETURNUSERID").toString());//退货人id
                        detail.setrQty(mapDetail.get("RQTY").toString());//退货数量
                        detail.setSaleDisc(mapDetail.get("SALEDISC").toString());//销售折扣金额
                        detail.setScanNo(mapDetail.get("SCANNO").toString());//扫描码
                        detail.setServCharge(mapDetail.get("SERVCHARGE").toString());//餐厅服务费
                        detail.setShareAmt(mapDetail.get("SHAREAMT").toString());//套餐分摊金额
                        detail.setSocalled(mapDetail.get("SOCALLED").toString());//等叫类型
                        detail.setSrackNo(mapDetail.get("SRACKNO").toString());//货架编号
                        detail.setTableNo(mapDetail.get("TABLENO").toString());//桌台号
                        detail.setTgCategoryNo(mapDetail.get("TGCATEGORYNO").toString());//团务分类编号
                        detail.setUnitRatio(1d);
                        detail.setBaseQty(new BigDecimal(detail.getQty()).doubleValue());

                        detail.setDetailAgios(new ArrayList<>());
                        sale.getDetails().add(detail);


                        bdm_qty=bdm_qty.add(new BigDecimal(mapDetail.get("QTY").toString()));//累加数量

                        //DCP_SALE_DETAIL
                        String[] Columns_DCP_SALE_DETAIL = {
                                "EID","SHOPID","SALENO","WAREHOUSE","ITEM","OITEM","CLERKNO","SELLERNAME",
                                "ACCNO","TABLENO","DEALTYPE","COUPONTYPE","COUPONCODE","PLUNO","PNAME",
                                "ISGIFT","GIFTREASON","GIFTOPNO","GIFTTIME","ISEXCHANGE","ISPICKGOOD",
                                "PLUBARCODE","SCANNO","MNO","COUNTERNO","SRACKNO","BATCHNO","TGCATEGORYNO",
                                "FEATURENO","ATTR01","ATTR02","UNIT","BASEUNIT","QTY","OLDPRICE","PRICE2",
                                "PRICE3","CANBACK","BSNO","RQTY","RETURNUSERID","RETURNTABLENO","REFUNDOPNO",
                                "REFUNDTIME","OLDAMT","DISC","SALEDISC","PAYDISC","PRICE","ADDITIONALPRICE",
                                "AMT","POINT_QTY","COUNTERAMT","SERVCHARGE","PACKAGEMASTER","ISPACKAGE",
                                "PACKAGEAMT","PACKAGEQTY","UPITEM","SHAREAMT","ISSTUFF","DETAILITEM",
                                "FLAVORSTUFFDETAIL","CAKEBLESSING","MATERIALS","DISHESSTATUS","SOCALLED",
                                "REPAST_TYPE","PACKAGEAMOUNT","PACKAGEPRICE","PACKAGEFEE","INCLTAX","TAXCODE",
                                "TAXTYPE","TAXRATE","ORDERRATEAMOUNT","MEMO","CONFIRMOPNO","CONFIRMTIME",
                                "ORDERTIME","STATUS","BDATE","SDATE","STIME","PROM_COUPONNO","DISC_MERRECEIVE",
                                "AMT_MERRECEIVE","DISC_CUSTPAYREAL","AMT_CUSTPAYREAL","PARTITION_DATE",
                                "UNITRATIO","BASEQTY"
                        };
                        DataValue[] insValue_DCP_SALE_DETAIL = new DataValue[]{
                                new DataValue(neweid, Types.VARCHAR),
                                new DataValue(a_shop, Types.VARCHAR),
                                new DataValue(saleno, Types.VARCHAR),
                                new DataValue(detail.getWarehouse(), Types.VARCHAR),
                                new DataValue(detail.getItem(), Types.VARCHAR),
                                new DataValue(detail.getoItem(), Types.VARCHAR),
                                new DataValue(detail.getClerkNo(), Types.VARCHAR),
                                new DataValue(detail.getClerkName(), Types.VARCHAR),
                                new DataValue(detail.getAccno(), Types.VARCHAR),
                                new DataValue(detail.getTableNo(), Types.VARCHAR),
                                new DataValue(detail.getDealType(), Types.VARCHAR),
                                new DataValue(detail.getCouponType(), Types.VARCHAR),
                                new DataValue(detail.getCouponCode(), Types.VARCHAR),
                                new DataValue(detail.getPluNo(), Types.VARCHAR),
                                new DataValue(detail.getpName(), Types.VARCHAR),
                                new DataValue(detail.getIsGift(), Types.VARCHAR),
                                new DataValue(detail.getGiftReason(), Types.VARCHAR),
                                new DataValue(detail.getGiftOpno(), Types.VARCHAR),
                                new DataValue(detail.getGiftTime(), Types.VARCHAR),
                                new DataValue(detail.getIsExchange(), Types.VARCHAR),
                                new DataValue(detail.getIsPickGoods(), Types.VARCHAR),
                                new DataValue(detail.getPluBarcode(), Types.VARCHAR),
                                new DataValue(detail.getScanNo(), Types.VARCHAR),
                                new DataValue(detail.getMno(), Types.VARCHAR),
                                new DataValue(detail.getCounterNo(), Types.VARCHAR),
                                new DataValue(detail.getSrackNo(), Types.VARCHAR),
                                new DataValue(detail.getBatchNo(), Types.VARCHAR),
                                new DataValue(detail.getTgCategoryNo(), Types.VARCHAR),
                                new DataValue(detail.getFeatureNo(), Types.VARCHAR),
                                new DataValue(detail.getAttr01(), Types.VARCHAR),
                                new DataValue(detail.getAttr02(), Types.VARCHAR),
                                new DataValue(detail.getUnit(), Types.VARCHAR),
                                new DataValue(detail.getBaseUnit(), Types.VARCHAR),
                                new DataValue(detail.getQty(), Types.VARCHAR),
                                new DataValue(detail.getOldPrice(), Types.VARCHAR),
                                new DataValue(detail.getPrice2(), Types.VARCHAR),
                                new DataValue(detail.getPrice3(), Types.VARCHAR),
                                new DataValue(detail.getCanBack(), Types.VARCHAR),
                                new DataValue(detail.getBsno(), Types.VARCHAR),
                                new DataValue(detail.getrQty(), Types.VARCHAR),
                                new DataValue(detail.getReturnUserId(), Types.VARCHAR),
                                new DataValue(detail.getReturnTableNo(), Types.VARCHAR),
                                new DataValue(detail.getRefundOpno(), Types.VARCHAR),
                                new DataValue(detail.getRefundTime(), Types.VARCHAR),
                                new DataValue(detail.getOldAMT(), Types.VARCHAR),
                                new DataValue(detail.getDisc(), Types.VARCHAR),
                                new DataValue(detail.getSaleDisc(), Types.VARCHAR),
                                new DataValue(detail.getPayDisc(), Types.VARCHAR),
                                new DataValue(detail.getPrice(), Types.VARCHAR),
                                new DataValue(detail.getAdditionalPrice(), Types.VARCHAR),
                                new DataValue(detail.getAmt(), Types.VARCHAR),
                                new DataValue(detail.getPointQty(), Types.VARCHAR),
                                new DataValue(detail.getCounterAmt(), Types.VARCHAR),
                                new DataValue(detail.getServCharge(), Types.VARCHAR),
                                new DataValue(detail.getPackageMaster(), Types.VARCHAR),
                                new DataValue(detail.getIsPackage(), Types.VARCHAR),
                                new DataValue(detail.getPackageAmt(), Types.VARCHAR),
                                new DataValue(detail.getPackageQty(), Types.VARCHAR),
                                new DataValue(detail.getUpItem(), Types.VARCHAR),
                                new DataValue(detail.getShareAmt(), Types.VARCHAR),
                                new DataValue(detail.getIsStuff(), Types.VARCHAR),
                                new DataValue(detail.getDetailItem(), Types.VARCHAR),
                                new DataValue(detail.getFlavorStuffDetail(), Types.VARCHAR),
                                new DataValue(detail.getCakeBlessing(), Types.VARCHAR),
                                new DataValue(detail.getMaterials(), Types.VARCHAR),
                                new DataValue(detail.getDishesStatus(), Types.VARCHAR),
                                new DataValue(detail.getSocalled(), Types.VARCHAR),
                                new DataValue(detail.getRepastType(), Types.VARCHAR),
                                new DataValue(detail.getPackageAmount(), Types.VARCHAR),
                                new DataValue(detail.getPackagePrice(), Types.VARCHAR),
                                new DataValue(detail.getPackageFee(), Types.VARCHAR),
                                new DataValue(detail.getInclTax(), Types.VARCHAR),
                                new DataValue(detail.getTaxCode(), Types.VARCHAR),
                                new DataValue(detail.getTaxType(), Types.VARCHAR),
                                new DataValue(detail.getTaxRate(), Types.VARCHAR),
                                new DataValue(detail.getOrderRateAmount(), Types.VARCHAR),
                                new DataValue(detail.getMemo(), Types.VARCHAR),
                                new DataValue(detail.getConfirmOpno(), Types.VARCHAR),
                                new DataValue(detail.getConfirmTime(), Types.VARCHAR),
                                new DataValue(detail.getOrderTime(), Types.VARCHAR),
                                new DataValue("100", Types.VARCHAR),
                                new DataValue(detail.getbDate(), Types.VARCHAR),
                                new DataValue(detail.getsDate(), Types.VARCHAR),
                                new DataValue(detail.getsTime(), Types.VARCHAR),
                                new DataValue(detail.getPromCouponNo(), Types.VARCHAR),
                                new DataValue(detail.getDisc_merReceive(), Types.VARCHAR),
                                new DataValue(detail.getAmt_merReceive(), Types.VARCHAR),
                                new DataValue(detail.getDisc_custPayReal(), Types.VARCHAR),
                                new DataValue(detail.getAmt_custPayReal(), Types.VARCHAR),
                                new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                                new DataValue(detail.getUnitRatio(), Types.FLOAT),
                                new DataValue(detail.getBaseQty(), Types.FLOAT),
                        };
                        InsBean ib_DCP_SALE_DETAIL = new InsBean("DCP_SALE_DETAIL", Columns_DCP_SALE_DETAIL);//分区字段已处理
                        ib_DCP_SALE_DETAIL.addValues(insValue_DCP_SALE_DETAIL);
                        data.add(new DataProcessBean(ib_DCP_SALE_DETAIL));


                        //过滤此单折扣
                        if (sqllist_agio != null && sqllist_agio.size()>0)
                        {
                            //过滤此商品折扣
                            List<Map<String, Object>> list_agio=sqllist_agio.stream().filter(p->p.get("MITEM").toString().equals(detail.getItem())).collect(Collectors.toList());
                            if(list_agio!=null && list_agio.size()>0)
                            {
                                for (Map<String, Object> rAgio : list_agio)
                                {
                                    SALE.DetailAgio agio=sale.new DetailAgio();

                                    agio.seteId(neweid);
                                    agio.setShopId(a_shop);
                                    agio.setStatus("100");
                                    agio.setAmt(rAgio.get("AMT").toString());
                                    agio.setBsno(rAgio.get("BSNO").toString());
                                    agio.setDcType(rAgio.get("DCTYPE").toString());
                                    agio.setDcTypeName(rAgio.get("DCTYPENAME").toString());
                                    agio.setDisc(rAgio.get("DISC").toString());
                                    agio.setGiftctf(rAgio.get("GIFTCTF").toString());
                                    agio.setGiftctfNo(rAgio.get("GIFTCTFNO").toString());
                                    agio.setItem(rAgio.get("ITEM").toString());
                                    agio.setmItem(detail.getItem());
                                    agio.setPmtNo(rAgio.get("PMTNO").toString());
                                    agio.setQty(rAgio.get("QTY").toString());
                                    agio.setRealDisc(rAgio.get("REALDISC").toString());
                                    agio.setOrderToSalePayAgioFlag("N");
                                    agio.setDisc_merReceive(rAgio.get("DISC").toString());
                                    agio.setDisc_custPayReal(rAgio.get("AMT").toString());

                                    agio.setDcOpno("");//打折人/授权人
                                    agio.setDcTime("");//打折时间
                                    agio.setMemo("");//折扣备注

                                    detail.getDetailAgios().add(agio);


                                    //DCP_SALE_DETAIL_AGIO
                                    String[] Columns_DCP_SALE_DETAIL_AGIO = {
                                            "EID","SHOPID","SALENO","MITEM","ITEM","QTY","AMT","DISC","REALDISC","DCTYPE",
                                            "DCTYPENAME","PMTNO","GIFTCTF","GIFTCTFNO","BSNO","DCOPNO","DCTIME","MEMO",
                                            "STATUS","DISC_MERRECEIVE","DISC_CUSTPAYREAL","PARTITION_DATE"
                                    };
                                    DataValue[] insValue_DCP_SALE_DETAIL_AGIO = new DataValue[]{
                                            new DataValue(neweid, Types.VARCHAR),
                                            new DataValue(a_shop, Types.VARCHAR),
                                            new DataValue(saleno, Types.VARCHAR),
                                            new DataValue(agio.getmItem(), Types.VARCHAR),
                                            new DataValue(agio.getItem(), Types.VARCHAR),
                                            new DataValue(agio.getQty(), Types.VARCHAR),
                                            new DataValue(agio.getAmt(), Types.DECIMAL),
                                            new DataValue(agio.getDisc(), Types.DECIMAL),
                                            new DataValue(agio.getRealDisc(), Types.DECIMAL),
                                            new DataValue(agio.getDcType(), Types.VARCHAR),
                                            new DataValue(agio.getDcTypeName(), Types.VARCHAR),
                                            new DataValue(agio.getPmtNo(), Types.VARCHAR),
                                            new DataValue(agio.getGiftctf(), Types.VARCHAR),
                                            new DataValue(agio.getGiftctfNo(), Types.VARCHAR),
                                            new DataValue(agio.getBsno(), Types.VARCHAR),
                                            new DataValue(agio.getDcOpno(), Types.VARCHAR),
                                            new DataValue(agio.getDcTime(), Types.VARCHAR),
                                            new DataValue(agio.getMemo(), Types.VARCHAR),
                                            new DataValue(agio.getStatus(), Types.VARCHAR),
                                            new DataValue(agio.getDisc_merReceive(), Types.VARCHAR),
                                            new DataValue(agio.getDisc_custPayReal(), Types.VARCHAR),
                                            new DataValue(sale.getbDate(), Types.NUMERIC)//分区字段

                                    };
                                    InsBean ib_DCP_SALE_DETAIL_AGIO = new InsBean("DCP_SALE_DETAIL_AGIO", Columns_DCP_SALE_DETAIL_AGIO);//分区字段已处理
                                    ib_DCP_SALE_DETAIL_AGIO.addValues(insValue_DCP_SALE_DETAIL_AGIO);
                                    data.add(new DataProcessBean(ib_DCP_SALE_DETAIL_AGIO));

                                }
                            }
                        }

                    }
                }

                sale.setTotQty(bdm_qty.toPlainString());


                //查付款
                String sql_pay="select * from td_SALE_PAY where companyno='"+oldeid+"' and saleno='"+saleno+"' ";
                List<Map<String, Object>> sqllist_pay=StaticInfo.dao_pos2.executeQuerySQL(sql_pay, null);
                if (sqllist_pay != null && sqllist_pay.size()>0)
                {
                    for (Map<String, Object> mapPay : sqllist_pay)
                    {
                        SALE.Payment pay=sale.new Payment();
                        pay.setPaycode(mapPay.get("PAYCODE").toString());
                        pay.setIsTurnOver(mapPay.get("ISTURNOVER").toString());//是否纳入营业额Y/N
                        pay.setPayShop(mapPay.get("PAYSHOP").toString());//支付门店编号
                        pay.setPayType(mapPay.get("PAYCODE").toString());//第三方支付方式
                        pay.setPosPay(mapPay.get("POS_PAY").toString());//POS支付金额
                        pay.setPrePayBillNo(mapPay.get("PREPAYBILLNO").toString());//订金收款单号
                        pay.setReturnRate(mapPay.get("RETURNRATE").toString());//退货吸收比率


                        String funcno="";
                        String funcno_key=oldeid+"_"+ pay.getPaycode();
                        if (map_funcno.containsKey(funcno_key))
                        {
                            funcno=map_funcno.get(funcno_key).toString();
                        }
                        else
                        {
                            String sql_org="select FUNCNO from TA_PAYFUNCNOINFO where paycode='"+pay.getPaycode()+"' and companyno='"+oldeid+"' ";
                            List<Map<String, Object>> sqllist_org=StaticInfo.dao_pos2.executeQuerySQL(sql_org, null);
                            if (sqllist_org != null && sqllist_org.size()>0)
                            {
                                funcno=sqllist_org.get(0).get("FUNCNO").toString();
                            }
                            map_funcno.put(funcno_key,funcno);
                        }

                        pay.setFuncNo(funcno);//功能编码
                        pay.setsDate(sale.getsDate());
                        pay.seteId(neweid);
                        pay.setShopId(a_shop);
                        pay.setStatus("100");
                        pay.setsTime(sale.getsTime());
                        pay.setAuthCode(mapPay.get("AUTHCODE").toString());
                        pay.setbDate(sale.getbDate());
                        pay.setCardNo(mapPay.get("CARDNO").toString());
                        //卡付款后
                        pay.setCardAmtBefore(mapPay.get("CARDAMTBEFORE").toString());
                        pay.setCardRemainAmt(mapPay.get("REMAINAMT").toString());
                        pay.setCardSendPay(mapPay.get("SENDPAY").toString());
                        pay.setChanged(mapPay.get("CHANGED").toString());
                        pay.setCouponQty(mapPay.get("COUPONQTY").toString());
                        pay.setCtType(mapPay.get("CTTYPE").toString());
                        pay.setDescore(mapPay.get("DESCORE").toString());
                        pay.setExtra(mapPay.get("EXTRA").toString());
                        pay.setIsDeposit(mapPay.get("ISDEPOSIT").toString());//是否订金Y/N【CM专用】
                        pay.setIsOrderPay(mapPay.get("ISORDERPAY").toString());//是否订金冲销Y/N
                        pay.setIsVerifycation(mapPay.get("ISVERIFICATION").toString());
                        pay.setItem(mapPay.get("ITEM").toString());
                        pay.setPay(mapPay.get("PAY").toString());
                        pay.setPaycodeErp(mapPay.get("PAYCODEERP").toString());
                        pay.setPayDocType(mapPay.get("PAYDOCTYPE").toString());//支付平台类型：POS-4
                        pay.setPayName(mapPay.get("PAYNAME").toString());
                        pay.setPayserNum(mapPay.get("PAYSERNUM").toString());
                        pay.setRefNo(mapPay.get("REFNO").toString());
                        pay.setSerialNo(mapPay.get("SERIALNO").toString());
                        pay.setTeriminalNo(mapPay.get("TERIMINALNO").toString());

                        BigDecimal bdm_amt=new BigDecimal(mapPay.get("PAY").toString()).subtract(new BigDecimal(mapPay.get("EXTRA").toString())).subtract(new BigDecimal(mapPay.get("CHANGED").toString())).setScale(2,BigDecimal.ROUND_HALF_UP);
                        pay.setMerDiscount("0");
                        pay.setMerReceive(bdm_amt.toPlainString());
                        pay.setThirdDiscout("0");
                        pay.setCustPayReal(bdm_amt.toPlainString());

                        sale.getPays().add(pay);

                        //DCP_SALE_PAY
                        String[] Columns_DCP_SALE_PAY = {"EID","SHOPID","SALENO","ITEM","PAYDOCTYPE","PAYCODE"
                                ,"PAYCODEERP","PAYNAME","PAY","POS_PAY","CHANGED","EXTRA","RETURNRATE","PAYSERNUM"
                                ,"SERIALNO","REFNO","TERIMINALNO","CTTYPE","CARDNO","CARDAMTBEFORE","REMAINAMT"
                                ,"SENDPAY","ISVERIFICATION","COUPONQTY","DESCORE","ISORDERPAY","PREPAYBILLNO"
                                ,"AUTHCODE","ISTURNOVER","STATUS","BDATE","SDATE","STIME","PAYTYPE","PAYSHOP"
                                ,"ISDEPOSIT","FUNCNO","MERDISCOUNT","MERRECEIVE","THIRDDISCOUNT","CUSTPAYREAL"
                                ,"COUPONMARKETPRICE","COUPONPRICE","MOBILE","PARTITION_DATE"
                        };
                        DataValue[] insValue_DCP_SALE_PAY = new DataValue[]{
                                new DataValue(neweid, Types.VARCHAR),
                                new DataValue(a_shop, Types.VARCHAR),
                                new DataValue(saleno, Types.VARCHAR),
                                new DataValue(pay.getItem(), Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(pay.getPaycode(), Types.VARCHAR),
                                new DataValue(pay.getPaycodeErp(), Types.VARCHAR),
                                new DataValue(pay.getPayName(), Types.VARCHAR),
                                new DataValue(pay.getPay(), Types.DECIMAL),
                                new DataValue(pay.getPosPay(), Types.DECIMAL),
                                new DataValue(pay.getChanged(), Types.DECIMAL),
                                new DataValue(pay.getExtra(), Types.DECIMAL),
                                new DataValue(pay.getReturnRate(), Types.DECIMAL),
                                new DataValue(pay.getPayserNum(), Types.VARCHAR),
                                new DataValue(pay.getSerialNo(), Types.VARCHAR),
                                new DataValue(pay.getRefNo(), Types.VARCHAR),
                                new DataValue(pay.getTeriminalNo(), Types.VARCHAR),
                                new DataValue(pay.getCtType(), Types.VARCHAR),
                                new DataValue(pay.getCardNo(), Types.VARCHAR),
                                new DataValue(pay.getCardAmtBefore(), Types.DECIMAL),
                                new DataValue(pay.getCardRemainAmt(), Types.DECIMAL),
                                new DataValue(pay.getCardSendPay(), Types.DECIMAL),
                                new DataValue(pay.getIsVerifycation(), Types.VARCHAR),
                                new DataValue(pay.getCouponQty(), Types.DECIMAL),
                                new DataValue(pay.getDescore(), Types.DECIMAL),
                                new DataValue(pay.getIsOrderPay(), Types.VARCHAR),
                                new DataValue(pay.getPrePayBillNo(), Types.VARCHAR),
                                new DataValue(pay.getAuthCode(), Types.VARCHAR),
                                new DataValue(pay.getIsTurnOver(), Types.VARCHAR),
                                new DataValue(pay.getStatus(), Types.VARCHAR),
                                new DataValue(pay.getbDate(), Types.VARCHAR),
                                new DataValue(pay.getsDate(), Types.VARCHAR),
                                new DataValue(pay.getsTime(), Types.VARCHAR),
                                new DataValue(pay.getPayType(), Types.VARCHAR),
                                new DataValue(pay.getPayShop(), Types.VARCHAR),
                                new DataValue(pay.getIsDeposit(), Types.VARCHAR),
                                new DataValue(pay.getFuncNo(), Types.VARCHAR),
                                new DataValue(pay.getMerDiscount(), Types.VARCHAR),
                                new DataValue(pay.getMerReceive(), Types.VARCHAR),
                                new DataValue(pay.getThirdDiscout(), Types.VARCHAR),
                                new DataValue(pay.getCustPayReal(), Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                        };
                        InsBean ib_DCP_SALE_PAY = new InsBean("DCP_SALE_PAY", Columns_DCP_SALE_PAY);//分区字段已处理
                        ib_DCP_SALE_PAY.addValues(insValue_DCP_SALE_PAY);
                        data.add(new DataProcessBean(ib_DCP_SALE_PAY));
                    }

                }


                //DCP_SALE
                String[] Columns_DCP_SALE = {
                        "EID","SHOPID","SALENO","TRNO","FNO","VER_NUM","LEGALPER","MACHINE","TYPE","TYPENAME","BDATE"
                        ,"SQUADNO","WORKNO","OPNO","AUTHORIZEROPNO","TERIMINAL_ID","OSHOP","OMACHINE","OTYPE","OFNO",
                        "SOURCESUBORDERNO","OTRNO","OBDATE","APPROVAL","CARDNO","MEMBERID","MEMBERNAME","CARDTYPEID",
                        "CARDAMOUNT","POINT_QTY","REMAINPOINT","MEMBERORDERNO","ORDERSHOP","CONTMAN","CONTTEL","GETMODE",
                        "GETSHOP","GETMAN","GETMANTEL","SHIPADD","GDATE","GTIME","MANUALNO","MEALNUMBER","CHILDNUMBER",
                        "MEMO","ECSFLG","ECSDATE","DISTRIBUTION","SENDMEMO","TABLENO","OPENID","TABLEKIND","GUESTNUM",
                        "REPAST_TYPE","DINNERDATE","DINNERTIME","DINNERSIGN","DINNERTYPE","TOUR_COUNTRYCODE","TOUR_TRAVELNO",
                        "TOUR_GROUPNO","TOUR_GUIDENO","TOUR_PEOPLENUM","TOT_QTY","TOT_OLDAMT","TOT_DISC","SALEDISC","PAYDISC",
                        "ERASE_AMT","TOT_AMT","SERVCHARGE","ORDERAMOUNT","FREECODE","PASSPORT","ISINVOICE","INVOICETITLE",
                        "INVOICEBANK","INVOICEACCOUNT","INVOICETEL","INVOICEADDR","TAXREGNUMBER","SELLCREDIT","CUSTOMERNO",
                        "CUSTOMERNAME","PAY_AMT","TOT_CHANGED","OINVSTARTNO","ISINVOICEMAKEOUT","INVSPLITTYPE","INVCOUNT",
                        "ISTAKEOUT","TAKEAWAY","ORDER_ID","ORDER_SN","PLATFORM_DISC","SELLER_DISC","PACKAGEFEE","SHIPPINGFEE",
                        "DELIVERY_FEE_SHOP","DELIVERY_FEE_USER","WM_USER_PAID","PLATFORM_FEE","WM_EXTRA_FEE","SHOPINCOME",
                        "PRODUCTIONMODE","PRODUCTIONSHOP","ISBUFFER","BUFFER_TIMEOUT","ECCUSTOMERNO","STATUS","ISRETURN",
                        "RETURNUSERID","BSNO","SDATE","STIME","EVALUATE","ISUPLOADED","RSV_ID","ORDERRETURN","COMPANYID",
                        "CHANNELID","APPTYPE","OCOMPANYID","OCHANNELID","OAPPTYPE","WXOPENID","TRAN_TIME","TOT_AMT_MERRECEIVE",
                        "TOT_AMT_CUSTPAYREAL","TOT_DISC_MERRECEIVE","TOT_DISC_CUSTPAYREAL","ISMERPAY","DEPARTNO","WAIMAIMERRECEIVEMODE",
                        "PARTITION_DATE"
                };
                DataValue[] insValue_DCP_SALE = new DataValue[]{
                        new DataValue(sale.geteId(), Types.VARCHAR),
                        new DataValue(sale.getShopId(), Types.VARCHAR),
                        new DataValue(saleno, Types.VARCHAR),
                        new DataValue(sale.getTrno(), Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(sale.getVerNum(), Types.VARCHAR),
                        new DataValue(sale.getLegalper(), Types.VARCHAR),
                        new DataValue(sale.getMachine(), Types.VARCHAR),
                        new DataValue(sale.getType(), Types.VARCHAR),
                        new DataValue(sale.getTypeName(), Types.VARCHAR),
                        new DataValue(sale.getbDate(), Types.VARCHAR),
                        new DataValue(sale.getSquadNO(), Types.VARCHAR),
                        new DataValue(sale.getWorkNO(), Types.VARCHAR),
                        new DataValue(sale.getOpNO(), Types.VARCHAR),
                        new DataValue(sale.getAuthorizerOpno(), Types.VARCHAR),
                        new DataValue(sale.getTeriminalID(), Types.VARCHAR),
                        new DataValue(sale.getoShop(), Types.VARCHAR),
                        new DataValue(sale.getoMachine(), Types.VARCHAR),
                        new DataValue(sale.getoType(), Types.VARCHAR),
                        new DataValue(sale.getOfNO(), Types.VARCHAR),
                        new DataValue(sale.getSourceSubOrderno(), Types.VARCHAR),
                        new DataValue(sale.getOtrno(), Types.VARCHAR),
                        new DataValue(sale.getoBdate(), Types.VARCHAR),
                        new DataValue(sale.getApproval(), Types.VARCHAR),
                        new DataValue(sale.getCardNO(), Types.VARCHAR),
                        new DataValue(sale.getMemberId(), Types.VARCHAR),
                        new DataValue(sale.getMemberName(), Types.VARCHAR),
                        new DataValue(sale.getCardTypeId(), Types.VARCHAR),
                        new DataValue(sale.getCardAmount(), Types.DECIMAL),
                        new DataValue(sale.getPointQty(), Types.DECIMAL),
                        new DataValue(sale.getRemainPoint(), Types.DECIMAL),
                        new DataValue(sale.getMemberOrderno(), Types.VARCHAR),
                        new DataValue(sale.getOrderShop(), Types.VARCHAR),
                        new DataValue(sale.getContMan(), Types.VARCHAR),
                        new DataValue(sale.getContTel(), Types.VARCHAR),
                        new DataValue(sale.getGetMode(), Types.VARCHAR),
                        new DataValue(sale.getGetShop(), Types.VARCHAR),
                        new DataValue(sale.getGetMan(), Types.VARCHAR),
                        new DataValue(sale.getGetManTel(), Types.VARCHAR),
                        new DataValue(sale.getShipAdd(), Types.VARCHAR),
                        new DataValue(sale.getgDate(), Types.VARCHAR),
                        new DataValue(sale.getgTime(), Types.VARCHAR),
                        new DataValue(sale.getManualNO(), Types.VARCHAR),
                        new DataValue(sale.getMealNumber(), Types.VARCHAR),
                        new DataValue(sale.getChildNumber(), Types.VARCHAR),
                        new DataValue(sale.getMemo(), Types.VARCHAR),
                        new DataValue(sale.getEcsFlg(), Types.VARCHAR),
                        new DataValue(sale.getEcsDate(), Types.VARCHAR),
                        new DataValue(sale.getDistribution(), Types.VARCHAR),
                        new DataValue(sale.getSendMemo(), Types.VARCHAR),
                        new DataValue(sale.getTableNO(), Types.VARCHAR),
                        new DataValue(sale.getOpenerId(), Types.VARCHAR),
                        new DataValue(sale.getTableKind(), Types.VARCHAR),
                        new DataValue(sale.getGuestNum(), Types.VARCHAR),
                        new DataValue(sale.getRepastType(), Types.VARCHAR),
                        new DataValue(sale.getDinnerDate(), Types.VARCHAR),
                        new DataValue(sale.getDinnerTime(), Types.VARCHAR),
                        new DataValue(sale.getDinnerSign(), Types.VARCHAR),
                        new DataValue(sale.getDinnerType(), Types.VARCHAR),
                        new DataValue(sale.getTourCountryCode(), Types.VARCHAR),
                        new DataValue(sale.getTourTravelNO(), Types.VARCHAR),
                        new DataValue(sale.getTourGroupNO(), Types.VARCHAR),
                        new DataValue(sale.getTourGuideNO(), Types.VARCHAR),
                        new DataValue(sale.getTourPeopleNum(), Types.VARCHAR),
                        new DataValue(sale.getTotQty(), Types.DECIMAL),
                        new DataValue(sale.getTotOldAmt(), Types.DECIMAL),
                        new DataValue(sale.getTotDisc(), Types.DECIMAL),
                        new DataValue(sale.getSaleDisc(), Types.DECIMAL),
                        new DataValue(sale.getPayDisc(), Types.DECIMAL),
                        new DataValue(sale.getEraseAmt(), Types.DECIMAL),
                        new DataValue(sale.getTotAmt(), Types.DECIMAL),
                        new DataValue(sale.getServCharge(), Types.DECIMAL),
                        new DataValue(sale.getOrderAmount(), Types.DECIMAL),
                        new DataValue(sale.getFreeCode(), Types.VARCHAR),
                        new DataValue(sale.getPassport(), Types.VARCHAR),
                        new DataValue(sale.getIsInvoice(), Types.VARCHAR),
                        new DataValue(sale.getInvoiceTitle(), Types.VARCHAR),
                        new DataValue(sale.getInvoiceBank(), Types.VARCHAR),
                        new DataValue(sale.getInvoiceAccount(), Types.VARCHAR),
                        new DataValue(sale.getInvoiceTel(), Types.VARCHAR),
                        new DataValue(sale.getInvoiceAddr(), Types.VARCHAR),
                        new DataValue(sale.getTaxRegNumber(), Types.VARCHAR),
                        new DataValue(sale.getSellCredit(), Types.VARCHAR),
                        new DataValue(sale.getCustomerNO(), Types.VARCHAR),
                        new DataValue(sale.getCustomerName(), Types.VARCHAR),
                        new DataValue(sale.getPayAmt(), Types.DECIMAL),
                        new DataValue(sale.getTotChanged(), Types.DECIMAL),
                        new DataValue(sale.getoInvstartno(), Types.VARCHAR),
                        new DataValue(sale.getIsInvoiceMakeOut(), Types.VARCHAR),
                        new DataValue(sale.getInvSplitType(), Types.VARCHAR),
                        new DataValue(sale.getInvCount(), Types.VARCHAR),
                        new DataValue(sale.getIsTakeout(), Types.VARCHAR),
                        new DataValue(sale.getTakeAway(), Types.VARCHAR),
                        new DataValue(sale.getOrderID(), Types.VARCHAR),
                        new DataValue(sale.getOrderSn(), Types.VARCHAR),
                        new DataValue(sale.getPlatformDisc(), Types.DECIMAL),
                        new DataValue(sale.getSellerDisc(), Types.DECIMAL),
                        new DataValue(sale.getPackageFee(), Types.DECIMAL),
                        new DataValue(sale.getShippingFee(), Types.DECIMAL),
                        new DataValue(sale.getDeliveryFeeShop(), Types.DECIMAL),
                        new DataValue(sale.getDeliveryFeeUser(), Types.DECIMAL),
                        new DataValue(sale.getWmUserPaid(), Types.DECIMAL),
                        new DataValue(sale.getPlatformFee(), Types.DECIMAL),
                        new DataValue(sale.getWmExtraFee(), Types.DECIMAL),
                        new DataValue(sale.getShopIncome(), Types.DECIMAL),
                        new DataValue(sale.getProductionMode(), Types.VARCHAR),
                        new DataValue(sale.getProductionShop(), Types.VARCHAR),
                        new DataValue(sale.getIsBuff(), Types.VARCHAR),
                        new DataValue(sale.getIsBuff_timeout(), Types.VARCHAR),
                        new DataValue(sale.getEcCustomerNO(), Types.VARCHAR),
                        new DataValue(sale.getStatus(), Types.VARCHAR),
                        new DataValue(sale.getIsReturn(), Types.VARCHAR),
                        new DataValue(sale.getReturnUserId(), Types.VARCHAR),
                        new DataValue(sale.getBsno(), Types.VARCHAR),
                        new DataValue(sale.getsDate(), Types.VARCHAR),
                        new DataValue(sale.getsTime(), Types.VARCHAR),
                        new DataValue(sale.getEvaluate(), Types.VARCHAR),
                        new DataValue(sale.getIsUploaded(), Types.VARCHAR),
                        new DataValue(sale.getRsvId(), Types.VARCHAR),
                        new DataValue(sale.getOrderReturn(), Types.VARCHAR),
                        new DataValue(sale.getOrder_companyId(), Types.VARCHAR),
                        new DataValue(sale.getOrder_channelId(), Types.VARCHAR),
                        new DataValue(sale.getOrder_appType(), Types.VARCHAR),
                        new DataValue(sale.getoOrder_companyId(), Types.VARCHAR),
                        new DataValue(sale.getoOrder_channelId(), Types.VARCHAR),
                        new DataValue(sale.getoOrder_appType(), Types.VARCHAR),
                        new DataValue(sale.getWxOpenId(), Types.VARCHAR),
                        new DataValue(sale.getTran_time(), Types.VARCHAR),
                        new DataValue(sale.getTot_Amt_merReceive(), Types.VARCHAR),
                        new DataValue(sale.getTot_Amt_custPayReal(), Types.VARCHAR),
                        new DataValue(sale.getTotDisc_merReceive(), Types.VARCHAR),
                        new DataValue(sale.getTotDisc_custPayReal(), Types.VARCHAR),
                        new DataValue(sale.getIsMerPay(), Types.VARCHAR),
                        new DataValue(sale.getDepartNo(), Types.VARCHAR),
                        new DataValue(sale.getWaimaiMerreceiveMode(), Types.VARCHAR),
                        new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段

                };
                InsBean ib_DCP_SALE = new InsBean("DCP_SALE", Columns_DCP_SALE);//分区字段已处理
                ib_DCP_SALE.addValues(insValue_DCP_SALE);
                data.add(new DataProcessBean(ib_DCP_SALE));

                //最后执行SQL，一单一处理
                StaticInfo.dao.useTransactionProcessData(data);
                data.clear();

                billToSale_ok=true;

            }
            catch (Exception e)
            {
                //处理失败
                billToSale_ok=false;

                try
                {
                    StringWriter errors = new StringWriter();
                    PrintWriter pw=new PrintWriter(errors);
                    e.printStackTrace(pw);

                    pw.flush();
                    pw.close();

                    errors.flush();
                    errors.close();

                    logger.error("\r\n******2.0升级到3.0 销售数据导入Movedb_Sale报错信息1" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                    pw=null;
                    errors=null;
                }
                catch (IOException e1)
                {
                    logger.error("\r\n******2.0升级到3.0 销售数据导入Movedb_Sale报错信息1" + e1.getMessage() + "******\r\n");
                }
            }
            finally
            {
                List<DataProcessBean> data2 = new ArrayList<DataProcessBean>();

                UptBean ub = new UptBean("td_sale");
                //处理失败
                if (!billToSale_ok)
                {
                    ub.addUpdateValue("MOVEDBSTATUS", new DataValue("E", Types.VARCHAR));
                }
                else
                {
                    ub.addUpdateValue("MOVEDBSTATUS", new DataValue("Y", Types.VARCHAR));
                }
                //Condition
                ub.addCondition("COMPANYNO", new DataValue(oldeid, Types.VARCHAR));
                ub.addCondition("SALENO", new DataValue(saleno, Types.VARCHAR));

                data2.add(new DataProcessBean(ub));
                //最后执行SQL，一单一处理
                try
                {
                    StaticInfo.dao_pos2.useTransactionProcessData(data2);
                }
                catch (Exception e)
                {
                    logger.error("\r\n******2.0升级到3.0 销售数据导入Movedb_Sale报错信息3" + e.getMessage() + "******\r\n");
                }
                data2.clear();
            }
        }

    }



}
