package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.model.SALE;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.config.SPosConfig.ProdInterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 分销系统OMS用，获取OMS系统电商出货单，上次获取结束时间(到秒)保存在redis中
 *
 * 首次获取时间段：当前时间前1天0点0分0秒 到  当前时间
 *
 *
 * OMS_Ship
 * {"startTime":"","endTime":""}
 *
 * 增加：转销售单失败的OMS出货单号保存到Hash缓存中，定时处理失败单据
 *
 * OMS_Ship_Exception 小key  eid_omsdocno
 *
 * OMS单据状态变更都会更新修改时间字段，那个时间段参数就是根据修改时间查询的，所以我们只需要处理已审核状态的单据
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OMSShip extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(OMSShip.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public OMSShip()
    {

    }

    public OMSShip(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }

    public String doExe()  throws Exception
    {
        //返回信息
        String sReturnInfo="";

        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********获取分销OMS系统电商出货单OMSShip正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-获取分销OMS系统电商出货单OMSShip正在执行中！";
            return sReturnInfo;
        }

        //http://124.70.146.167/restful/service/I/IOpenApiRequestService/postRequest
        String oms_url=StaticInfo.OMS_Url;
        String oms_eid=StaticInfo.OMS_Eid;
        String erp_eid=StaticInfo.OMS_ERP_Eid;

        String machineno="800";
        if (Check.Null(oms_url) || Check.Null(oms_eid)|| Check.Null(erp_eid))
        {
            sReturnInfo="定时传输任务-获取分销OMS系统电商出货单OMSShip配置文件参数OMS_Url或OMS_Eid或OMS_ERP_Eid不能为空！";
            logger.error("\r\n*********"+sReturnInfo+"************\r\n");
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********获取分销OMS系统电商出货单OMSShip定时调用Start:************\r\n");

        RedisPosPub redis=new RedisPosPub();
        JSONObject jsonRedis=new JSONObject();

        try
        {
            String values=redis.getString("OMS_Ship");

            //每页10条
            int pageSize=10;
            //默认0下面会+1
            int pageNumber=0;
            //总页数
            int pageCount=1;

            String startTime="";
            String curTime= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String endTime="";
            if (Check.Null(values))
            {
                //减一天
                startTime= LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")) +"000000";
                endTime=curTime;

                //+1
                pageNumber=1;
            }
            else
            {
                JSONObject jsonObject=new JSONObject(values);
                startTime=jsonObject.get("startTime").toString();
                endTime=jsonObject.get("endTime").toString();
                if (jsonObject.has("pageSize"))
                {
                    pageSize=Integer.parseInt(jsonObject.get("pageSize").toString());
                }
                if (jsonObject.has("pageNumber"))
                {
                    pageNumber=Integer.parseInt(jsonObject.get("pageNumber").toString());
                }
                if (jsonObject.has("pageCount"))
                {
                    pageCount=Integer.parseInt(jsonObject.get("pageCount").toString());
                }

                //+1
                pageNumber=pageNumber+1;

                //开始时间=结束时间 说明那个时间段已经没记录了
                if (startTime.equals(endTime))
                {
                    endTime=curTime;
                }
            }

            //通过配置文件读取
            String langtype="zh_CN";
            List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
            if(lstProd!=null&&!lstProd.isEmpty())
            {
                langtype=lstProd.get(0).getHostLang().getValue();
            }

            //缓存参数格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //OMS接口参数格式
            SimpleDateFormat sdf_oms=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            //年月日格式
            SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyyMMdd");
            //时分秒格式
            SimpleDateFormat sdf_Time = new SimpleDateFormat("HHmmss");

            //年月日时分秒毫秒
            SimpleDateFormat sdf_ms = new SimpleDateFormat("yyyyMMddHHmmss");

            JSONObject head=new JSONObject();
            JSONObject param=new JSONObject();

            JSONObject requestparam=new JSONObject();
            requestparam.put("ebaent",oms_eid);
            requestparam.put("ebamdate_start",sdf_oms.format(sdf.parse(startTime)));//参数格式:2022/02/17 15:38:31
            requestparam.put("ebamdate_end",sdf_oms.format(sdf.parse(endTime)));
            requestparam.put("pageSize",pageSize+"");
            requestparam.put("pageNumber",pageNumber+"");

            param.put("requestinterface","IOpenApiRequestService");
            param.put("requestmethod","getDeliveryOrders");
            param.put("requestmodel","I");
            param.put("requestparam",requestparam);
            head.put("param",param);

            logger.info("\r\n*********获取分销OMS系统电商出货单OMSShip定时调用请求:"+head.toString()+"************\r\n");

            String resBody= HttpSend.Sendhttp("POST", head.toString(), oms_url);
            requestparam=null;
            param=null;
            head=null;

            if (Check.Null(resBody))
            {
                logger.error("\r\n*********获取分销OMS系统电商出货单OMSShip 请求分销系统报错，返回为空************\r\n");
            }
            else
            {
                JSONObject res=new JSONObject(resBody);
                String resStatus=res.get("status").toString();
                if (resStatus.equals("200"))
                {
                    JSONObject response=res.getJSONObject("response");
                    String Status="";
                    if (response.has("Status"))
                    {
                        Status=response.get("Status").toString();
                    }

                    //总页数
                    int rowCount=1;

                    //成功
                    if (Status.equals("0") || Status.equals(""))
                    {
                        //总页数
                        if (response.has("rowCount"))
                        {
                            rowCount=Integer.parseInt(response.get("rowCount").toString());
                        }

                        JSONArray datas=new JSONArray();
                        if (response.has("datas"))
                        {
                            datas=response.getJSONArray("datas");
                        }

                        //门店所属公司
                        Map<String, Object> map_belfirm=new HashMap<>();

                        for (int i = 0; i < datas.length(); i++)
                        {
                            List<DataProcessBean> data = new ArrayList<DataProcessBean>();
                            //标记单据转销售处理成功
                            boolean billToSale_ok=false;
                            String a_billno="";
                            String error="";
                            try
                            {
                                String a_eid=datas.getJSONObject(i).get("ebaent").toString();//企业编号
                                a_billno=datas.getJSONObject(i).get("ebadocno").toString();//出货单号

                                //查找销售单,存在就不管了
                                String sql_sale="select * from dcp_sale where eid='"+erp_eid+"' and saleno='"+a_billno+"'";
                                List<Map<String, Object>> sqlsale=this.doQueryData(sql_sale, null);
                                if (sqlsale != null && sqlsale.size()>0)
                                {
                                    billToSale_ok=true;
                                    continue;
                                }

                                String a_createDate=datas.getJSONObject(i).get("ebaddate").toString();//单据日期 2021/08/12 11:01:02
                                String a_platBillno=datas.getJSONObject(i).get("ebaord").toString();//平台单号
                                String a_totAmt=datas.getJSONObject(i).get("ebatamt").toString();//含税金额
                                //分销系统OMS的组织编码要与新零售编码一致，不做映射
                                String a_shop=datas.getJSONObject(i).get("ebalorg").toString();//归属组织
                                String a_orderShop=datas.getJSONObject(i).get("ebacorg").toString();//建立组织
                                String a_shipShop=datas.getJSONObject(i).get("ebaoorg").toString();//出库组织
                                String a_confirmNo=datas.getJSONObject(i).get("ebaaper").toString();//审核人
                                String a_modifyNo=datas.getJSONObject(i).get("ebamper").toString();//修改人
                                String a_modifyDate=datas.getJSONObject(i).get("ebamdate").toString();//修改日期 2021/08/12 11:01:02
                                String a_memo=datas.getJSONObject(i).get("ebanote").toString();//备注
                                String a_channel=datas.getJSONObject(i).get("ebachnl").toString();//渠道 00101
                                String a_createDpt=datas.getJSONObject(i).get("ebacdept").toString();//建立部门 10000101
                                String a_taxcode=datas.getJSONObject(i).get("ebatax").toString();//税种
                                String a_taxRate=datas.getJSONObject(i).get("ebatrate").toString();//税率 13
                                String a_getman=datas.getJSONObject(i).get("eba001").toString();//收货人
                                String a_gettel=datas.getJSONObject(i).get("ebamtel").toString();//收货人电话
                                String a_shipdate=datas.getJSONObject(i).get("eba004").toString();//送货日期
                                String a_address=datas.getJSONObject(i).get("ebaaddr").toString();//详细地址
                                String a_ebastatus=datas.getJSONObject(i).get("ebastus").toString();//单据状态 N.未审核 Y.已审核 S.已过账 X.已作废
                                String a_ebaexno=datas.getJSONObject(i).get("ebaexno").toString();//快递单号

                                //未审核及作废的单据过滤掉
                                if (a_ebastatus != null && (a_ebastatus.equals("N")|| a_ebastatus.equals("X")))
                                {
                                    billToSale_ok=true;
                                    continue;
                                }
                                //快递单号为空的也过滤掉
                                if (Check.Null(a_ebaexno) || a_ebaexno.equals("null"))
                                {
                                    billToSale_ok=true;
                                    continue;
                                }

                                String belfirm="";
                                if (map_belfirm.containsKey(a_shop))
                                {
                                    belfirm=map_belfirm.get(a_shop).toString();
                                }
                                else
                                {
                                    String sql="select BELFIRM from dcp_org where eid='"+erp_eid+"' and organizationno='"+a_shop+"' ";
                                    List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
                                    if (sqllist != null && sqllist.size()>0)
                                    {
                                        belfirm=sqllist.get(0).get("BELFIRM").toString();
                                    }
                                    map_belfirm.put(a_shop,belfirm);
                                }

                                //取入账日期-销售单
                                String accountDate = PosPub.getAccountDate_SMS(StaticInfo.dao, erp_eid, a_shop);

                                SALE sale =new SALE();
                                sale.setIsMerPay("N");
                                sale.setEraseAmt("0");
                                sale.setApproval("");//钉钉审批id
                                sale.setAuthorizerOpno("");//授权人编码
                                sale.setGetMode("1");//配送方式
                                sale.setGetShop("");//
                                sale.setEvaluate("0");//是否评价0-否1-是
                                sale.setEcsDate("");//订单结案日期
                                sale.setEcsFlg("");//订单结案否Y/N
                                sale.setCardAmount("0");//初始余额
                                sale.setCardTypeId("");//卡类型编码
                                //桌台相关
                                sale.setTableKind("");//桌台类型
                                sale.setTableNO("");//桌台号
                                sale.setOpenerId("");//开台人
                                sale.setDinnerDate("");//就餐日期
                                sale.setDinnerSign("");//就餐牌号码
                                sale.setDinnerTime("");//就餐时间
                                sale.setDinnerType("");//餐饮单据状态
                                sale.setChildNumber("0");//儿童数量
                                sale.setServCharge("0");//餐厅服务费
                                sale.setGuestNum("0");//桌台人数
                                //发票相关
                                sale.setTaxRegNumber("");//纳税人识别号
                                sale.setFreeCode("");//零税证号-只有外交官才有的凭证--零税的其中一种凭证
                                sale.setPassport("");//护照编号-零税的其中一种凭证
                                sale.setIsInvoice("");//是否需要发票Y/N
                                sale.setIsInvoiceMakeOut("");//是否已开票
                                sale.setInvCount("0");//发票数量
                                sale.setInvoiceAccount("");//发票开户账号
                                sale.setInvoiceAddr("");//发票公司地址
                                sale.setInvoiceBank("");//发票开户行
                                sale.setInvoiceTel("");//发票联系方式
                                sale.setInvoiceTitle("");//发票抬头
                                sale.setInvSplitType("");//发票开票拆分类型：1不拆分 2按商品拆分 3按金额拆分
                                sale.setoInvstartno("");//原发票号码
                                //旅游团相关
                                sale.setTourCountryCode("");//国家编码
                                sale.setTourGroupNO("");//团号
                                sale.setTourGuideNO("");//导游编号
                                sale.setTourPeopleNum("0");//进店人数
                                sale.setTourTravelNO("");//旅行社编码
                                //外卖
                                sale.setTakeAway("");//外卖平台
                                sale.setIsTakeout("N");//是否外卖订单
                                sale.setWxOpenId("");//第三方用户id
                                sale.setWmExtraFee("0");//外卖：平台其他费用(每单捐赠1分钱支持环保之类的活动)
                                sale.setWmUserPaid("0");//外卖：用户实际支付
                                sale.setDeliveryFeeShop("0");//外卖：门店承担配送费
                                sale.setOrderSn("");//外卖订单流水号
                                //
                                sale.setIsUploaded("");//是否已传第三方（例如商场）Y已上传，N未上传
                                sale.setLegalper("");//法人
                                sale.setIsReturn("N");//是否已退Y/N
                                sale.setOrderAmount("0");//订金金额
                                sale.setOrder_appType("");//下单应用类型
                                sale.setoBdate(sdf_Date.format(sdf_oms.parse(a_createDate)));//原单营业日期
                                sale.setoMachine(machineno);//原单机台号
                                sale.setoOrder_appType("OMS");//来源应用类型
                                sale.setoOrder_channelId("OMS001");//来源渠道编码
                                sale.setoOrder_companyId(belfirm);//来源公司编码
                                sale.setTeriminalID("");//终端ID
                                sale.setTotChanged("0");//找零金额
                                sale.setOrderReturn("");////订金退款类型：0:沒有訂金1:部份訂金2:全部訂金【CM专用】
                                sale.setOrderShop(a_orderShop);//下订门店
                                sale.setoShop(a_shop);//原单门店
                                sale.setOtrno("");//原单流水号
                                sale.setoType("");//原单类型
                                sale.setPayDisc("0");//付款折扣金额
                                sale.setProductionMode("");//生产方式：0本店制作1异店制作2总部制作
                                sale.setProductionShop("");//生产门店
                                sale.setRemainPoint("0");//剩余积分
                                sale.setRepastType("0");//就餐类型：0堂食1打包2外卖
                                sale.setRsvId("");//预订ID
                                //
                                sale.setIsBuff("N");//是否暂存
                                sale.setIsBuff_timeout("");//暂存超时时间
                                sale.setbDate(sdf_Date.format(sdf_oms.parse(a_createDate)));
                                sale.setCardNO("");
                                sale.setContMan(a_getman);
                                sale.setContTel(a_gettel);
                                sale.setCustomerName("");
                                sale.setCustomerNO("");
                                sale.setDeliveryFeeUser("0");
                                sale.setDistribution("");
                                sale.setMealNumber("0");
                                sale.setBsno("");
                                sale.seteId(erp_eid);
                                sale.setEcCustomerNO("");
                                sale.setgDate(sdf_Date.format(sdf_oms.parse(a_shipdate)));
                                sale.setgTime(sdf_Time.format(sdf_oms.parse(a_shipdate)));
                                sale.setGetMan(a_getman);
                                sale.setGetManTel(a_gettel);
                                sale.setMachine(machineno);
                                sale.setManualNO("");
                                sale.setMemberId("");
                                sale.setMemberName("");
                                sale.setMemo(a_memo);
                                sale.setOfNO(a_billno);
                                sale.setOpNO(a_confirmNo);
                                sale.setOrder_channelId("OMS001");
                                sale.setOrder_companyId(belfirm);
                                sale.setOrderID("");
                                sale.setPackageFee("0");
                                sale.setPlatformDisc("0");
                                sale.setPlatformFee("0");
                                sale.setsDate(sdf_Date.format(sdf_ms.parse(curTime)));
                                sale.setSellCredit("N");
                                sale.setReturnUserId("");
                                sale.setSendMemo("");
                                sale.setShipAdd(a_address);
                                sale.setShippingFee("0");
                                sale.setDeliveryFeeShop("0");
                                sale.setShopId(a_shop);
                                sale.setSourceSubOrderno("");
                                sale.setSquadNO("");
                                sale.setStatus("100");
                                sale.setsTime(sdf_Time.format(sdf_ms.parse(curTime)));
                                sale.setType("0");
                                sale.setTypeName("销售单");
                                sale.setVerNum("OMS");
                                sale.setWorkNO("");
                                sale.setTran_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                                sale.setWaimaiMerreceiveMode("0");
                                //源于付款+抹零
                                sale.setTotDisc_merReceive("0");
                                sale.setTotDisc_custPayReal("0");

                                //源于付款
                                sale.setTot_Amt_merReceive(a_totAmt);
                                sale.setTot_Amt_custPayReal(a_totAmt);
                                //刷新销售单头字段
                                sale.setTotOldAmt(a_totAmt);
                                sale.setTotAmt(a_totAmt);
                                sale.setTotDisc("0");
                                sale.setPayAmt(a_totAmt);
                                sale.setSaleDisc("0");//销售折扣金额
                                sale.setSellerDisc("0");
                                sale.setShopIncome(a_totAmt);
                                sale.setPointQty("0");
                                sale.setMemberOrderno("");//调用积分memberpay的orderno
                                sale.setPays(new ArrayList<>());
                                sale.setDetails(new ArrayList<>());
                                BigDecimal bdm_qty=new BigDecimal("0");

                                //OMS过账接口使用
                                JSONArray eaiList=new JSONArray();

                                JSONArray ebb_list=datas.getJSONObject(i).getJSONArray("ebb_list");
                                for (int i1 = 0; i1 < ebb_list.length(); i1++)
                                {
                                    String b_item=ebb_list.getJSONObject(i1).get("ebbseq").toString();//项次
                                    String b_pluno=ebb_list.getJSONObject(i1).get("ebbgoods").toString();//商品编号
                                    String b_pluname=b_pluno;
                                    if (ebb_list.getJSONObject(i1).has("caggoods_n1"))
                                    {
                                        b_pluname=ebb_list.getJSONObject(i1).get("caggoods_n1").toString();//商品名称
                                    }

                                /*OMS不给就自己查,不过这样影响效能
                                String sql_goods="select PLU_NAME from dcp_goods_lang where eid='"+erp_eid+"' and pluno='"+b_pluno+"' and lang_type='"+langtype+"' ";
                                List<Map<String, Object>> listGoods=this.doQueryData(sql_goods, null);
                                if (listGoods != null && listGoods.size()>0)
                                {
                                    b_pluname=listGoods.get(0).get("PLU_NAME").toString();
                                }
                                */
                                    String b_spec="";
                                    if (ebb_list.getJSONObject(i1).has("cag001_n1"))
                                    {
                                        b_spec=ebb_list.getJSONObject(i1).get("cag001_n1").toString();//规格
                                    }
                                    String b_featureNo=" ";//ebb_list.getJSONObject(i1).get("ebbgchr").toString();//特征
//                                    if (Check.Null(b_featureNo) || b_featureNo.equals("null"))
//                                    {
//                                        b_featureNo=" ";
//                                    }
                                    String b_qty=ebb_list.getJSONObject(i1).get("ebbqty").toString();//数量
                                    String b_unit=ebb_list.getJSONObject(i1).get("ebbtunit").toString();//交易单位单位
                                    String b_price=ebb_list.getJSONObject(i1).get("ebbprice").toString();//单价
                                    String b_amt=ebb_list.getJSONObject(i1).get("ebbtamt").toString();//含税金额
                                    String b_namt=ebb_list.getJSONObject(i1).get("ebbnamt").toString();//未税金额
                                    String b_warehouse=ebb_list.getJSONObject(i1).get("ebbwh").toString();//仓库
                                    String b_discRate=ebb_list.getJSONObject(i1).get("ebb011").toString();//折扣率100
                                    bdm_qty=bdm_qty.add(new BigDecimal(b_qty));//累加数量

                                    SALE.Detail detail=sale.new Detail();
                                    detail.setAccno("");
                                    detail.setAmt(b_amt);
                                    detail.setbDate(sale.getbDate());
                                    detail.setBsno("");
                                    detail.setClerkName("");
                                    detail.setClerkNo("");
                                    detail.setCounterNo("");
                                    detail.setCouponCode("");
                                    detail.setCouponType("");
                                    detail.setDetailItem("0");
                                    detail.setDisc("0");
                                    detail.seteId(erp_eid);
                                    detail.setFeatureNo(b_featureNo);
                                    detail.setGiftReason("");
                                    detail.setIsGift("");
                                    detail.setPackageMaster("N");
                                    detail.setIsPackage("N");
                                    detail.setIsStuff("N");//是否加料商品Y/N
                                    detail.setItem(b_item);
                                    detail.setInclTax("Y");
                                    detail.setOldAMT(b_amt);
                                    detail.setOldPrice(b_price);
                                    detail.setPackageAmount("0");//餐盒数量
                                    detail.setPackagePrice("0");
                                    detail.setPackageFee("0");//餐盒总价
                                    detail.setShopId(a_shop);
                                    detail.setsDate(sale.getsDate());
                                    detail.setsTime(sale.getsTime());
                                    detail.setPluBarcode(b_pluno);
                                    detail.setPluNo(b_pluno);
                                    detail.setpName(b_pluname);
                                    detail.setPrice(b_price);
                                    detail.setQty(b_qty);
                                    detail.setTaxCode(a_taxcode);
                                    detail.setTaxRate(a_taxRate);
                                    detail.setTaxType("");
                                    detail.setUnit(b_unit);
                                    detail.setUpItem("0");
                                    detail.setWarehouse(b_warehouse);
                                    detail.setStatus("100");//有效否100-有效0-无效
                                    detail.setVirtual("N");
                                    detail.setAmt_merReceive(b_amt);
                                    detail.setAmt_custPayReal(b_amt);
                                    detail.setDisc_merReceive("0");
                                    detail.setDisc_custPayReal("0");
                                    detail.setBaseUnit(b_unit);//基准单位
                                    detail.setAdditionalPrice("0");//加价
                                    detail.setAttr01("");//商品特性1
                                    detail.setAttr02("");//商品特性2
                                    detail.setBatchNo("");//批次号
                                    detail.setCakeBlessing("");//蛋糕祝福
                                    detail.setCanBack("");//是否可退Y/N
                                    detail.setConfirmOpno("");//确认人
                                    detail.setConfirmTime("");//确认时间
                                    detail.setCounterAmt("0");//专柜成交金额
                                    detail.setDealType("1");//商品交易类型：1销售2退货3赠送11免单
                                    detail.setDishesStatus("");//菜品状态
                                    detail.setFlavorStuffDetail("");//口味加料细节
                                    detail.setGiftOpno("");//赠送人编码
                                    detail.setGiftTime("");//赠送时间
                                    detail.setIsExchange("");//是否换购品Y/N
                                    detail.setIsPickGoods("");////是否现提商品Y/N
                                    detail.setMaterials("");//蛋糕坯子
                                    detail.setMemo("");//备注
                                    detail.setMno("");//商场码
                                    detail.setoItem(b_item);//来源项次
                                    detail.setOrderRateAmount("0");//订金分摊金额
                                    detail.setOrderTime("");//预定时间
                                    detail.setPackageAmt("0");//套餐商品金额
                                    detail.setPackageQty("0");//套餐商品数量
                                    detail.setPayDisc("0");//付款折扣
                                    detail.setPointQty("0");//积分
                                    detail.setPrice2("0");//底价
                                    detail.setPrice3("0");//最高退价
                                    detail.setPromCouponNo("");//促销赠券券号
                                    detail.setRepastType("");//就餐类型：0堂食1打包2外卖
                                    detail.setRefundOpno("");//退菜人编号
                                    detail.setReturnTableNo("");//退货桌号
                                    detail.setRefundTime("");//退菜时间
                                    detail.setReturnUserId("");//退货人id
                                    detail.setrQty("0");//退货数量
                                    detail.setSaleDisc("0");//销售折扣金额
                                    detail.setScanNo("");//扫描码
                                    detail.setServCharge("0");//餐厅服务费
                                    detail.setShareAmt("0");//套餐分摊金额
                                    detail.setSocalled("");//等叫类型
                                    detail.setSrackNo("");//货架编号
                                    detail.setTableNo("");//桌台号
                                    detail.setTgCategoryNo("");//团务分类编号
                                    detail.setUnitRatio(1d);
                                    detail.setBaseQty(new BigDecimal(detail.getQty()).doubleValue());

                                    detail.setDetailAgios(new ArrayList<>());
                                    sale.getDetails().add(detail);

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
                                            new DataValue(erp_eid, Types.VARCHAR),
                                            new DataValue(a_shop, Types.VARCHAR),
                                            new DataValue(a_billno, Types.VARCHAR),
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

                                    //扣库存
                                    String procedure="SP_DCP_StockChange";
                                    Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                                    inputParameter.put(1,erp_eid);                                       //--企业ID
                                    inputParameter.put(2,a_shop);                                    //--组织
                                    inputParameter.put(3,"20");                                      //--单据类型
                                    inputParameter.put(4,a_billno);	                                 //--单据号
                                    inputParameter.put(5,detail.getItem());            //--单据行号
                                    inputParameter.put(6,"-1");                                      //--异动方向 1=加库存 -1=减库存
                                    inputParameter.put(7,sale.getbDate());           //--营业日期 yyyy-MM-dd
                                    inputParameter.put(8,detail.getPluNo());           //--品号
                                    inputParameter.put(9,detail.getFeatureNo());       //--特征码
                                    inputParameter.put(10,detail.getWarehouse());                                //--仓库
                                    inputParameter.put(11,detail.getBatchNo());       //--批号
                                    inputParameter.put(12,detail.getUnit());          //--交易单位
                                    inputParameter.put(13,detail.getQty());           //--交易数量
                                    inputParameter.put(14,detail.getBaseUnit());       //--基准单位
                                    inputParameter.put(15,detail.getQty());        //--基准数量
                                    inputParameter.put(16,1);     //--换算比例
                                    inputParameter.put(17,detail.getPrice());          //--零售价
                                    inputParameter.put(18,detail.getAmt());            //--零售金额
                                    inputParameter.put(19,"0");    //--进货价
                                    inputParameter.put(20,"0");      //--进货金额
                                    inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                                    inputParameter.put(22,"");      //--批号的生产日期 yyyy-MM-dd
                                    inputParameter.put(23,sale.getsDate());                                  //--单据日期
                                    inputParameter.put(24,"");                                       //--异动原因
                                    inputParameter.put(25,"OMS扣库存");                                //--异动描述
                                    inputParameter.put(26,"");                                //--操作员

                                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                    data.add(new DataProcessBean(pdb));

                                    //OMS电商出货单过账接口数据组装
                                    JSONObject eba=new JSONObject();
                                    eba.put("eaiseq",detail.getItem());
                                    eba.put("eaigoods",detail.getPluNo());
                                    eba.put("eaiqty",detail.getQty());
                                    eaiList.put(eba);

                                }

                                //OMS电商出货单过账接口数据组装
                                if (eaiList != null && eaiList.length()>0 && a_ebastatus != null && !a_ebastatus.equals("S"))
                                {
                                    //OMS电商出货单过账接口
                                    JSONObject eai_head=new JSONObject();
                                    JSONObject eai_param=new JSONObject();

                                    JSONObject eai_requestparam=new JSONObject();
                                    eai_requestparam.put("ebaent",oms_eid);
                                    eai_requestparam.put("ebadocno",a_billno);
                                    eai_requestparam.put("eai_list",eaiList);

                                    eai_param.put("requestinterface","IOpenApiRequestService");
                                    eai_param.put("requestmethod","auditEba2Fx");
                                    eai_param.put("requestmodel","I");
                                    eai_param.put("requestparam",eai_requestparam);
                                    eai_head.put("param",eai_param);

                                    logger.info("\r\n*********获取分销OMS系统电商出货单OMSShip定时调用请求:"+eai_head.toString()+"************\r\n");

                                    String eai_resBody= HttpSend.Sendhttp("POST", eai_head.toString(), oms_url);
                                    eai_requestparam=null;
                                    eai_param=null;
                                    eai_head=null;

                                    if (Check.Null(eai_resBody))
                                    {
                                        logger.error("\r\n*********获取分销OMS系统电商出货单OMSShip "+a_billno+"过账操作请求分销系统报错，返回为空************\r\n");
                                        billToSale_ok=false;
                                        error="获取分销OMS系统电商出货单OMSShip "+a_billno+"过账操作请求分销系统报错，返回为空************";
                                        continue;
                                    }
                                    else
                                    {
                                        JSONObject eai_res=new JSONObject(eai_resBody);
                                        String eai_resStatus=eai_res.get("status").toString();
                                        if (eai_resStatus.equals("200"))
                                        {
                                            JSONObject eai_response=eai_res.getJSONObject("response");
                                            String eai_Status=eai_response.get("Status").toString();
                                            //成功
                                            if (eai_Status.equals("0"))
                                            {
                                                //省略
                                            }
                                            else
                                            {
                                                String Message=eai_response.get("Message").toString();
                                                logger.error("\r\n*********获取分销OMS系统电商出货单OMSShip "+a_billno+"过账操作请求分销系统报错，返回内容:"+Message+"************\r\n");
                                                billToSale_ok=false;
                                                error="*********获取分销OMS系统电商出货单OMSShip "+a_billno+"过账操作请求分销系统报错，返回内容:"+Message+"************";
                                                continue;
                                            }
                                        }
                                        else
                                        {
                                            logger.error("\r\n*********获取分销OMS系统电商出货单OMSShip "+a_billno+"过账操作请求分销系统报错，返回内容:"+resBody+"************\r\n");
                                            billToSale_ok=false;
                                            error="*********获取分销OMS系统电商出货单OMSShip "+a_billno+"过账操作请求分销系统报错************";
                                            continue;
                                        }
                                    }
                                }

                                sale.setTotQty(bdm_qty.toPlainString());

                                SALE.Payment pay=sale.new Payment();
                                pay.setIsTurnOver("N");//是否纳入营业额Y/N
                                pay.setPayShop("");//支付门店编号
                                pay.setPayType("OMS");//第三方支付方式
                                pay.setPosPay("0");//POS支付金额
                                pay.setPrePayBillNo("");//订金收款单号
                                pay.setReturnRate("0");//退货吸收比率
                                pay.setFuncNo("");//功能编码
                                pay.setsDate(sale.getsDate());
                                pay.seteId(erp_eid);
                                pay.setShopId(a_shop);
                                pay.setStatus("100");
                                pay.setsTime(sale.getsTime());
                                pay.setAuthCode("");
                                pay.setbDate(sale.getbDate());
                                pay.setCardNo("");
                                //卡付款后
                                pay.setCardAmtBefore("0");
                                pay.setCardRemainAmt("0");
                                pay.setCardSendPay("0");
                                pay.setChanged("0");
                                pay.setCouponQty("0");
                                pay.setCtType("");
                                pay.setDescore("0");
                                pay.setExtra("0");
                                pay.setIsDeposit("N");//是否订金Y/N【CM专用】
                                pay.setIsOrderPay("N");//是否订金冲销Y/N
                                pay.setIsVerifycation("N");
                                pay.setItem("1");
                                pay.setPay(sale.getPayAmt());
                                pay.setPaycode("OMS");
                                pay.setPaycodeErp("OMS");
                                pay.setPayDocType("");//支付平台类型：POS-4
                                pay.setPayName("OMS");
                                pay.setPayserNum("");
                                pay.setRefNo("");
                                pay.setSerialNo("");
                                pay.setTeriminalNo("");
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
                                        new DataValue(erp_eid, Types.VARCHAR),
                                        new DataValue(a_shop, Types.VARCHAR),
                                        new DataValue(a_billno, Types.VARCHAR),
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
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(pay.getPay(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(pay.getPay(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(sale.getbDate(), Types.NUMERIC),//分区字段
                                };
                                InsBean ib_DCP_SALE_PAY = new InsBean("DCP_SALE_PAY", Columns_DCP_SALE_PAY);//分区字段已处理
                                ib_DCP_SALE_PAY.addValues(insValue_DCP_SALE_PAY);
                                data.add(new DataProcessBean(ib_DCP_SALE_PAY));

                                //取trno
                                String trno=getTRNO(erp_eid, a_shop, sale.getbDate(), a_billno);


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
                                        new DataValue(a_billno, Types.VARCHAR),
                                        new DataValue(trno, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(sale.getVerNum(), Types.VARCHAR),
                                        new DataValue(sale.getLegalper(), Types.VARCHAR),
                                        new DataValue(machineno, Types.VARCHAR),
                                        new DataValue(sale.getType(), Types.VARCHAR),
                                        new DataValue(sale.getTypeName(), Types.VARCHAR),
                                        new DataValue(sale.getbDate(), Types.VARCHAR),
                                        new DataValue(sale.getSquadNO(), Types.VARCHAR),
                                        new DataValue(sale.getWorkNO(), Types.VARCHAR),
                                        new DataValue(sale.getOpNO(), Types.VARCHAR),
                                        new DataValue(sale.getAuthorizerOpno(), Types.VARCHAR),
                                        new DataValue(sale.getTeriminalID(), Types.VARCHAR),
                                        new DataValue(sale.getoShop(), Types.VARCHAR),
                                        new DataValue(machineno, Types.VARCHAR),
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

                                    logger.error("\r\n******获取分销OMS系统电商出货单OMSShip报错单号:"+a_billno + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                                    //errors.toString()这个内容太相信太多，不能写reids

                                    StringBuffer sb_errors=new StringBuffer();
                                    String[] p_error=errors.toString().split("\n");
                                    for (String sp : p_error)
                                    {
                                        if (sp.contains("com.dsc") || sp.contains("Caused by"))
                                        {
                                            sb_errors.append(sp+"\n");
                                        }
                                    }

                                    error="******获取分销OMS系统电商出货单OMSShip报错单号:"+a_billno + e.getMessage()+" " + sb_errors.toString() + "******";

                                    sb_errors.setLength(0);

                                    pw=null;
                                    errors=null;
                                }
                                catch (IOException e1)
                                {
                                    logger.error("\r\n******获取分销OMS系统电商出货单OMSShip报错单号:"+a_billno + e.getMessage() + "******\r\n");
                                    error="******获取分销OMS系统电商出货单OMSShip报错单号:"+a_billno +"\r\n"+ e.getMessage() + "******";
                                }
                            }
                            finally
                            {
                                //处理失败，保存失败的单号缓存记录
                                if (!billToSale_ok)
                                {
                                    JSONObject jsonExceptionRedis=new JSONObject();
                                    jsonExceptionRedis.put("shipNo",a_billno);
                                    jsonExceptionRedis.put("eid",oms_eid);
                                    jsonExceptionRedis.put("error",error);
                                    jsonExceptionRedis.put("oms",resBody);
                                    redis.setHashMap("OMS_Ship_Exception",oms_eid+"_"+a_billno,jsonExceptionRedis.toString());
                                }
                            }
                        }

                        //处理成功更新缓存

                        //所有数据拉取完成时间开始时间更新成结束时间
                        if (rowCount<= pageNumber)
                        {
                            jsonRedis.put("startTime",endTime);
                            jsonRedis.put("endTime",endTime);
                            jsonRedis.put("pageSize",pageSize);
                            jsonRedis.put("pageNumber","0");
                            jsonRedis.put("pageCount","1");
                        }
                        else
                        {
                            //继续拉取剩余页
                            jsonRedis.put("startTime",startTime);
                            jsonRedis.put("endTime",endTime);
                            jsonRedis.put("pageSize",pageSize);
                            jsonRedis.put("pageNumber",pageNumber);
                            jsonRedis.put("pageCount",rowCount);
                        }

                        redis.setString("OMS_Ship",jsonRedis.toString());
                    }
                    else
                    {
                        String Message="";
                        if (response.has("Message"))
                        {
                            Message=response.get("Message").toString();
                        }
                        logger.error("\r\n*********获取分销OMS系统电商出货单OMSShip 请求分销系统报错，返回为"+Message+"************\r\n");
                    }
                }
                else
                {
                    logger.error("\r\n*********获取分销OMS系统电商出货单OMSShip 请求分销系统报错，返回为空"+resBody+"************\r\n");
                }
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

                logger.error("\r\n******获取分销OMS系统电商出货单OMSShip报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******获取分销OMS系统电商出货单OMSShip报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********获取分销OMS系统电商出货单OMSShip定时调用End:************\r\n");
        }
        redis.Close();
        return sReturnInfo;
    }


    /**
     * 取当天最大流水号,F_POS_TRNO函数 1个单号只能调一次，不然序号不连续
     * @param eId
     * @param shopId
     * @param bDate
     * @return
     */
    private String getTRNO(String eId,String shopId,String bDate,String saleno)
    {
        RedisPosPub redis=new RedisPosPub();

        String trno="";
        try
        {
            //F_POS_TRNO 调一次加一次，只能控制1单只能调1次，不然序号不连续

            //先取缓存
            String key="SALE:"+eId+":"+shopId+":"+bDate;
            trno= redis.getHashMap(key,saleno);
            if (Check.Null(trno))
            {
                //取库
                String sql_trno = " select F_POS_TRNO('"+eId+"','"+shopId+"','"+bDate+"') TRNO FROM dual ";

                List<Map<String, Object>> getData_trno=StaticInfo.dao.executeQuerySQL(sql_trno, null);
                if(getData_trno==null || getData_trno.isEmpty())
                {
                    trno="1";
                }
                else
                {
                    if (Check.Null(getData_trno.get(0).get("TRNO").toString()))
                    {
                        trno="1";
                    }
                    else
                    {
                        trno=getData_trno.get(0).get("TRNO").toString();
                    }
                }
                redis.setHashMap(key,saleno,trno);
            }
        }
        catch (Exception e)
        {
            trno="";
        }
        finally
        {
            redis.Close();
        }
        return trno;
    }


}
