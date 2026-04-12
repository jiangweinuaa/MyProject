package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetCreateReq;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetCreateReq.level3Elm;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetCreateReq.level4Elm;
import com.dsc.spos.json.cust.res.DCP_TakeOutOrderBaseSetCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外卖基础设置新增
 *
 * @author yuanyy
 */
public class DCP_TakeOutOrderBaseSetCreate extends SPosAdvanceService<DCP_TakeOutOrderBaseSetCreateReq, DCP_TakeOutOrderBaseSetCreateRes> {

    @Override
    protected void processDUID(DCP_TakeOutOrderBaseSetCreateReq req, DCP_TakeOutOrderBaseSetCreateRes res)
            throws Exception {
        // TODO Auto-generated method stub
        String eId = req.geteId();
        DCP_TakeOutOrderBaseSetCreateReq.level1Elm request = req.getRequest();
        try {
            String baseSetNo = this.getBaseSetNo(req);
            String baseSetName = request.getBaseSetName();
            String choosableTime = request.getChoosableTime();
            String prepareTime = request.getPrepareTime();
            String deliveryTime = request.getDeliveryTime();
            String lowestMoney = request.getLowestMoney();
            String freightWay = request.getFreightWay();
            String freight = request.getFreight();
            String status = request.getStatus();
            String restrictShop = request.getRestrictShop();
            String isPayCard = request.getPromSet().getIsPayCard();
            String freeShippingPrice = request.getFreeShippingPrice();
            if (freeShippingPrice==null||freeShippingPrice.isEmpty())
            {
                freeShippingPrice = "0";
            }


            level3Elm promSet = request.getPromSet();
            
            //【ID1035591】扫码点餐及外卖点单加详情配置项-服务  by jinzma 20230828
            String isGoodsDetailDisplay = promSet.getIsGoodsDetailDisplay();
            if (Check.Null(isGoodsDetailDisplay)){
                isGoodsDetailDisplay="0";
            }

            List<level2Elm> cdfDatas = request.getCityDeliverFreight();
            List<DCP_TakeOutOrderBaseSetCreateReq.level5Elm> paySet = request.getPaySet();
            List<DCP_TakeOutOrderBaseSetCreateReq.level6Elm> shopDatas = request.getRangeList();
            List<level4Elm> orderTimesDatas = request.getOrderTimes();

            String createBy = req.getOpNO();
            String createByName = req.getOpName();

            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createTime = df.format(cal.getTime());

            //**************** 管控：不允许一个门店出现多种配置信息 ***********

            String shopStr = "''";
            if (shopDatas != null && restrictShop.equals("1")) { // 适用门店：0-所有门店1-指定门店2-排除门店

                for (DCP_TakeOutOrderBaseSetCreateReq.level6Elm lv6 : shopDatas) {
                    String shopId = lv6.getShopId();
                    shopStr = shopStr + ",'" + shopId + "'";
                }

            }

            StringBuffer shopSqlBuffer = new StringBuffer();
            String shopSql = "";

            shopSqlBuffer.append(""
                    + " SELECT DISTINCT  a.basesetno, a.RESTRICTSHOP, b.SHOPID "
                    + " from dcp_takeout_baseset a "
                    + " left join dcp_takeout_baseset_detail b "
                    + " on a.EID = b.EID "
                    + " and a.basesetno = b.basesetno "
                    + " where a.EID = '" + eId + "' ");

            shopSql = shopSqlBuffer.toString();

            List<Map<String, Object>> repeatDatas = this.doQueryData(shopSql, null);

            Map<String, Object> condition = new HashMap<String, Object>(); //查询条件
            condition.put("RESTRICTSHOP", "0");
            //调用过滤函数
            List<Map<String, Object>> allTypeDatas = MapDistinct.getWhereMap(repeatDatas, condition, true);

            Map<String, Object> condition2 = new HashMap<String, Object>(); //查询条件
            condition2.put("RESTRICTSHOP", "1");
            //调用过滤函数
            List<Map<String, Object>> assignTypeDatas = MapDistinct.getWhereMap(repeatDatas, condition2, true);

            if (allTypeDatas != null && !allTypeDatas.isEmpty()) { // 已存在的全部门店信息
                String repeatSetNo = allTypeDatas.get(0).get("BASESETNO").toString();
                res.setSuccess(false);
                res.setServiceStatus("200");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在配置信息" + repeatSetNo + "适用于全部门店,不可重复添加");
            }

            if (assignTypeDatas != null && !assignTypeDatas.isEmpty()) { //已存在的指定门店信息

                if (restrictShop.equals("0")) {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在配置信息适用于指定门店,不可重复添加");
                }

                if (restrictShop.equals("1")) {
                    String evShopStr = "";
                    for (Map<String, Object> map : assignTypeDatas) {
                        String shopId = map.get("SHOPID").toString();
                        if (Check.Null(shopId)) {
                            continue;
                        }
                        for (DCP_TakeOutOrderBaseSetCreateReq.level6Elm par : shopDatas) {
                            if (shopId.equals(par.getShopId())) {
                                evShopStr = evShopStr + shopId + " ";
                            }
                        }
                    }

                    if (evShopStr.trim().length() > 0) {
                        res.setSuccess(false);
                        res.setServiceStatus("200");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在适用于以下门店的配置信息" + evShopStr);
                    }

                }

            }

            //***************** 管控结束 **************

            String[] columns1 = {
                    "EID", "BASESETNO", "BASESETNAME", "CHOOSABLETIME",
                    "PREPARETIME", "DELIVERYTIME", "LOWESTMONEY", "FREIGHTWAY", "FREIGHT",
                    "PACKPRICETYPE", "PACKPRICE", "ISTABLEWARE", "ISREGISTER",
                    "COUPON", "INTEGRAL", "RESTRICTREGISTER", "RESTRICTSHOP",
                    "STATUS", "LASTMODIOPID", "LASTMODIOPNAME",
                    "LASTMODITIME","CREATEOPID","CREATEOPNAME","CREATETIME","PAYCOUNTDOWN",
                    "ISAUTOREGISTER","ISAUTOPROM","ISAUTOFOLD","BEFORORDER","ISEVALUATEREMIND","REMINDTIME",
                    "ISPAYCARD","ISGOODSDETAILDISPLAY","FREESHIPPINGPRICE"
            };
            DataValue[] insValue = null;

            /**
             * 插入同城距离配送费用
             */
            if (cdfDatas != null && !cdfDatas.isEmpty()) {
                for (level2Elm par : cdfDatas) {
                    String[] detailCol = {
                            "EID", "BASESETNO", "ITEM", "MAXDISTANCE", "FREIGHT","LOWESTMONEY","FREESHIPPINGPRICE","DELIVERYTIME"
                    };
                    DataValue[] detailVal = null;
                    detailVal = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(baseSetNo, Types.VARCHAR),
                            new DataValue(par.getSerialNo(), Types.VARCHAR),
                            new DataValue(par.getMaxDistance(), Types.VARCHAR),
                            new DataValue(par.getFreight(), Types.VARCHAR),
                            new DataValue(par.getLowestMoney(), Types.VARCHAR),
                            new DataValue(par.getFreeShippingPrice(), Types.VARCHAR),
                            new DataValue(par.getDeliveryTime(), Types.VARCHAR)
                    };

                    InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_FREIGHT", detailCol);
                    ib1.addValues(detailVal);
                    this.addProcessData(new DataProcessBean(ib1));

                }
            }


            /**
             * 插入适用门店
             */
            if (shopDatas != null && !shopDatas.isEmpty() && (restrictShop.equals("1") || restrictShop.equals("2"))) {
                for (DCP_TakeOutOrderBaseSetCreateReq.level6Elm par : shopDatas) {
                    String[] detailCol = {
                            "EID", "BASESETNO", "SHOPID", "SHOPNAME"
                    };
                    DataValue[] detailVal = null;
                    detailVal = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(baseSetNo, Types.VARCHAR),
                            new DataValue(par.getShopId(), Types.VARCHAR),
                            new DataValue(par.getName(), Types.VARCHAR)
                    };

                    InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_DETAIL", detailCol);
                    ib1.addValues(detailVal);
                    this.addProcessData(new DataProcessBean(ib1));

                }
            }

            /**
             * 插入接单时间
             */
            if (orderTimesDatas != null && !orderTimesDatas.isEmpty()) {
                for (level4Elm par : orderTimesDatas) {
                    String[] detailCol = {
                            "EID", "BASESETNO", "ITEM", "STARTTIME", "ENDTIME"
                    };
                    DataValue[] detailVal = null;
                    detailVal = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(baseSetNo, Types.VARCHAR),
                            new DataValue(par.getItem(), Types.VARCHAR),
                            new DataValue(par.getStartTime(), Types.VARCHAR),
                            new DataValue(par.getEndTime(), Types.VARCHAR)
                    };

                    InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_ORDERTIME", detailCol);
                    ib1.addValues(detailVal);
                    this.addProcessData(new DataProcessBean(ib1));

                }
            }

            /**
             * 插入支付设置
             */
            if (paySet != null && !paySet.isEmpty()) {

                for (DCP_TakeOutOrderBaseSetCreateReq.level5Elm level5Elm : paySet) {
                    String[] detailCol = {
                            "EID", "BASESETNO", "SORTID", "PAYTYPE", "PAYNAME"
                    };
                    DataValue[] detailVal = null;
                    detailVal = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(baseSetNo, Types.VARCHAR),
                            new DataValue(level5Elm.getSortId(), Types.VARCHAR),
                            new DataValue(level5Elm.getPayType(), Types.VARCHAR),
                            new DataValue(level5Elm.getPayName(), Types.VARCHAR)
                    };

                    InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_PAYTYPE", detailCol);
                    ib1.addValues(detailVal);
                    this.addProcessData(new DataProcessBean(ib1));

                }
            }


            insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(baseSetNo, Types.VARCHAR),
                    new DataValue(baseSetName, Types.VARCHAR),
                    new DataValue(choosableTime, Types.VARCHAR),

                    new DataValue(prepareTime, Types.VARCHAR),
                    new DataValue(deliveryTime, Types.VARCHAR),
                    new DataValue(lowestMoney, Types.VARCHAR),
                    new DataValue(freightWay, Types.VARCHAR),

                    new DataValue(freight, Types.VARCHAR),
                    new DataValue(request.getPackPriceType(), Types.VARCHAR),
                    new DataValue(request.getPackPrice(), Types.VARCHAR),
//                    new DataValue(request.getPluNo(), Types.VARCHAR),

                    new DataValue(request.getIsTableware(), Types.VARCHAR),
                    new DataValue(promSet.getIsRegister(), Types.VARCHAR),
                    new DataValue(promSet.getCoupon(), Types.VARCHAR),
                    new DataValue(promSet.getIntegral(), Types.VARCHAR),
                    new DataValue(promSet.getRestrictRegister(), Types.VARCHAR),

                    new DataValue(restrictShop, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),

                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(createByName, Types.VARCHAR),
                    new DataValue(createTime, Types.DATE),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(createByName, Types.VARCHAR),
                    new DataValue(createTime, Types.DATE),
                    new DataValue(request.getPayCountdown(), Types.VARCHAR),

                    // Add 2021-06-07 王欢 新增isAutoProm、isAutoRegister字段
                    new DataValue(promSet.getIsAutoRegister(), Types.VARCHAR),
                    new DataValue(promSet.getIsAutoProm(), Types.VARCHAR),
                    new DataValue(promSet.getIsAutoFold(), Types.VARCHAR),
                    new DataValue(request.getBeforOrder(), Types.VARCHAR),
                    new DataValue(promSet.getIsEvaluateRemind(), Types.VARCHAR),
                    new DataValue(promSet.getRemindTime(), Types.VARCHAR),
                    new DataValue(isPayCard, Types.VARCHAR),
                    new DataValue(isGoodsDetailDisplay, Types.VARCHAR),
                    new DataValue(freeShippingPrice, Types.VARCHAR)
                    

            };

            InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET", columns1);
            ib1.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib1));

            this.doExecuteDataToDB();

//			}

        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setServiceDescription("服务执行失败！" + e.getMessage());
            res.setServiceStatus("200");
            res.setSuccess(false);
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TakeOutOrderBaseSetCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TakeOutOrderBaseSetCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TakeOutOrderBaseSetCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TakeOutOrderBaseSetCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
//		StringBuffer errMsg = new StringBuffer("");
//		String baseSetNo = req.getRequest().getBaseSetNo();
//		
//		if (Check.Null(baseSetNo)) 
//		{
//			errMsg.append("编号不可为空值");
//			isFail = true;
//		}
//		
//		if (isFail)
//		{
//			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//		}
        return isFail;
    }

    @Override
    protected TypeToken<DCP_TakeOutOrderBaseSetCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_TakeOutOrderBaseSetCreateReq>() {
        };
    }

    @Override
    protected DCP_TakeOutOrderBaseSetCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_TakeOutOrderBaseSetCreateRes();
    }


    /**
     * 主键重复验证
     *
     * @param eId
     * @param baseSetNo
     * @return
     * @throws Exception
     */
    private boolean checkRepeat(String eId, String baseSetNo) throws Exception {
        String sql = "";
        boolean temp = false;
        sql = "select * from DCP_TAKEOUT_BASESET "
                + " where  EID = '" + eId + "'"
                + " and baseSetNo = '" + baseSetNo + "' ";

        List<Map<String, Object>> reDatas = this.doQueryData(sql, null);
        if (reDatas != null && reDatas.size() > 0) {
            temp = true;
        }

        return temp;
    }


    private String getBaseSetNo(DCP_TakeOutOrderBaseSetCreateReq req) throws Exception {

        String sql = "";
        String eId = req.geteId();
        Calendar cal = Calendar.getInstance();// 获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String bDate = df.format(cal.getTime());
        String baseSetNo = "";
        sql = "select MAX(baseSetNo) AS baseSetNo from DCP_TAKEOUT_BASESET where EID = '" + eId + "'  and baseSetNo like '%%" + bDate + "%%'";
        List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
        if (getDatas.size() > 0) {
            baseSetNo = (String) getDatas.get(0).get("BASESETNO");

            if (baseSetNo != null && baseSetNo.length() > 0) {
                long i;
                baseSetNo = baseSetNo.substring(4, baseSetNo.length());
                i = Long.parseLong(baseSetNo) + 1;
                baseSetNo = i + "";
                baseSetNo = "WMDC" + baseSetNo;

            } else {
                baseSetNo = "WMDC" + bDate + "00001";
            }

        }

        return baseSetNo;

    }


}
