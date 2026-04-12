package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveOrderStatusUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ReserveOrderStatusUpdate_OpenRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 预约单状态变更
 * @author: wangzyc
 * @create: 2021-08-03
 */
public class DCP_ReserveOrderStatusUpdate_Open extends SPosAdvanceService<DCP_ReserveOrderStatusUpdate_OpenReq, DCP_ReserveOrderStatusUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_ReserveOrderStatusUpdate_OpenReq req, DCP_ReserveOrderStatusUpdate_OpenRes res) throws Exception {
        DCP_ReserveOrderStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String reserveNo = request.getReserveNo();
        String loadDocType = request.getLoadDocType();
        String shopId = request.getShopId();
        String status = request.getStatus(); // 单据状态 0待审核 1待服务 2已服务 3已取消

        try {
            // 先查下预约单存不存在
            String checkSql = "select * from DCP_RESERVE where eid = '" + eId + "' and SHOPID = '" + shopId + "' and RESERVENO = '" + reserveNo + "' and LOADDOCTYPE = '" + loadDocType + "'";
            List<Map<String, Object>> checkReserveNo = this.doQueryData(checkSql, null);
            if (CollectionUtils.isEmpty(checkReserveNo)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前订单不存在，请检查您的信息是否录入正确");
            }


            String couponCode = "";
            if (status.equals("3")) {
                //  若status变更为3.已取消，则判断当前时间是否超出服务开始时间与DCP_RESERVEPARAMETER.CANCELTIME的值 若超过则报错提示：超过预约时间30分钟内，不可取消；
                // 查询下门店的参数
                StringBuffer sqlbuf = new StringBuffer("");
                sqlbuf.append("SELECT a.\"TIME\", a.BDATE as \"date\", b.CANCELTYPE, b.CANCELTIME,a.COUPONCODE,a.status FROM DCP_RESERVE a " +
                        " LEFT JOIN DCP_RESERVEPARAMETER b ON a.EID = b.eid AND a.SHOPID = b.shopid " +
                        " WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' AND a.RESERVENO = '" + reserveNo + "' and a.LOADDOCTYPE = '" + loadDocType + "'");
                List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), null);
                if (!CollectionUtils.isEmpty(data)) {
                    Map<String, Object> map = data.get(0);
                    String ostatus = map.get("STATUS").toString();
                    if("2".equals(ostatus)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "历史单据已结案，不可取消!");
                    }
                    String canceltime = map.get("CANCELTIME").toString(); // 取消预约时间限制(分钟)
                    String canceltype = map.get("CANCELTYPE").toString(); // 取消预约限制规则Y/N 默认不限制
                    if(Check.Null(canceltype)){
                        canceltype = "Y";
                    }
                    String date = map.get("DATE").toString(); // 预约的日期
                    couponCode = map.get("COUPONCODE").toString(); // 预约的日期
                    // 截取 年 月 日
                    String year = date.substring(0, 4);
                    String mone = date.substring(5, 7);
                    String day = date.substring(8, 10);
                    String time = map.get("TIME").toString(); // 预约时间
                    String substring = time.substring(0, time.indexOf("-"));
                    String houre = substring.substring(0, 2);
                    String mine = substring.substring(3, 5);

                    // 查看当前时间 + 取消预约时间限制分钟 是否> 预约时间段开始时间
                    SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Calendar cal = Calendar.getInstance();
                    String currentDate = sdr.format(cal.getTime());
                    cal.set(Integer.parseInt(year), Integer.parseInt(mone) - 1, Integer.parseInt(day), Integer.parseInt(houre), Integer.parseInt(mine));
                    String dbDate = sdr.format(cal.getTime());
                    Date parse1 = sdr.parse(dbDate);

                    // 取消预约时间限制 Y == 不限制  N == 限制
                    if (canceltype.equals("N")) {

                        cal = Calendar.getInstance();
                        cal.add(cal.MINUTE, Integer.parseInt(canceltime));
                        Date parse2 = sdr.parse(sdr.format(cal.getTime()));
                        if (parse2.compareTo(parse1) > 0) {
                            // 当前时间超过 预约时间
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "超过预约时间" + canceltime + "分钟内，不可取消；");
                        }
                    }
                }
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 修改 预约单
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_RESERVE");
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(request.getOperatorId(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(request.getOperatorName(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(df.format(new Date()), Types.DATE));
            if (!Check.Null(request.getOpNo())) {
                ub1.addUpdateValue("OPNO", new DataValue(request.getOpNo(), Types.VARCHAR));
            }

            ub1.addCondition("RESERVENO", new DataValue(reserveNo, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
            if (!Check.Null(shopId)) {
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            }

            this.addProcessData(new DataProcessBean(ub1));

            // 若满足上述条件，则检查该预约单的券是否是体验券赠送获取的，如果是体验券渠道，则取消预约单后把该券撤销，如果是其他渠道，则该券不做改变（体验券默认只赠送一张）
            if (!Check.Null(couponCode) && status.equals("3")) {
                StringBuffer sqlbuf = new StringBuffer("");
                sqlbuf.append("SELECT b.COUPONCODE FROM CRM_COUPONGAIN a " +
                        " LEFT JOIN CRM_COUPONGAINITEM b ON a.eid = b.EID  AND a.BILLNO  = b.BILLNO " +
                        " left join CRM_COUPONTYPE c on c.COUPONTYPEID = b.COUPONTYPEID AND a.EID = b.EID " +
                        " WHERE a.THIRDTRANSNO = ? AND b.COUPONCODE  = ? and a.eid = ?  and c.COUPONKIND = '5' ");
                List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), new String[]{reserveNo, couponCode, eId});
                if(!CollectionUtils.isEmpty(data)){
                   String memberUrl = PosPub.getCRM_INNER_URL(eId);
                    if (memberUrl.trim().equals("")) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
                    }
                    // 如果该卡券是体验券类型 则执行体验券撤销
                    // 若预约单创建失败则调用CouponPromSendReverse撤销发放；
                    JSONObject CouponPromSendReverseReq = new JSONObject();
                    CouponPromSendReverseReq.put("serviceId", "CouponPromSendReverse");

                    JSONObject cpsrReq = new JSONObject();
                    cpsrReq.put("orderNo", reserveNo);  //  商户唯一订单号
                    cpsrReq.put("trade_no", "");

                    CouponPromSendReverseReq.put("request", cpsrReq);

                    String cpsrReqStr = cpsrReq.toString();
                    String cpsrSign = PosPub.encodeMD5(cpsrReqStr + req.getApiUser().getUserKey());

                    JSONObject cpsrSignJson = new JSONObject();
                    cpsrSignJson.put("sign", cpsrSign);
                    cpsrSignJson.put("key", req.getApiUserCode());

                    CouponPromSendReverseReq.put("sign", cpsrSignJson);

                    //********** 已经准备好 CouponPromSendReverse 的json，开始调用 *************
                    String s2 = CouponPromSendReverseReq.toString();
                    String cpsrResStr = HttpSend.Sendcom(CouponPromSendReverseReq.toString(), memberUrl).trim();
                    HelpTools.writelog_fileName("***********预约单取消 DCP_ReserveOrderStatusUpdate_Open 调用 CouponPromSendReverse 接口，请求json：" + CouponPromSendReverseReq.toString()
                            + "*************", "DCP_ReserveOrderStatusUpdate_Open");

                    JSONObject cpsrResJson = new JSONObject();
                    cpsrResJson = JSON.parseObject(cpsrResStr);//String转json

                    HelpTools.writelog_fileName("***********预约单取消 DCP_ReserveOrderStatusUpdate_Open 调用 CouponPromSendReverse 接口返回信息：" + cpsrResStr + "*************", "DCP_ReserveOrderStatusUpdate_Open");
                    String cpsrSuccess = cpsrResJson.getString("success").toUpperCase(); // TRUE 或 FALSE

                    if (!Check.Null(cpsrSuccess) && cpsrSuccess.equals("TRUE")) {

                    }else{
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "取消预约单失败,请重新发起取消！");
                    }
                }

            }


            // 如果变更预约单状态status=2&opNo不为空时，通过reserveNo查询预约单信息生成服务记录；
            if("2".equals(status)&&!Check.Null(request.getOpNo())){
                String[] column = {"EID", "SHOPID", "MEMBERID", "RESERVENO", "ITEMSNO", "OPNO", "LOADDOCTYPE", "COUPONCODE",
                        "TRADENO", "CREATEOPID", "CREATEOPNAME", "CREATETIME"};
                String sql = "select * from DCP_RESERVE where eid = '" + eId + "' and SHOPID = '" + shopId + "' and RESERVENO = '" + reserveNo + "'";
                List<Map<String, Object>> data = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(data)) {
                    Map<String, Object> map = data.get(0);
                    String loaddoctype = map.get("LOADDOCTYPE").toString();
                    String itemsno = map.get("ITEMSNO").toString();
                    String MEMBERID = map.get("MEMBERID").toString();
                    couponCode = map.get("COUPONCODE").toString();

                    Date time = new Date();
                    String lastmoditime = df.format(time);

                    DataValue[] insValuele = {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(MEMBERID, Types.VARCHAR),
                            new DataValue(reserveNo, Types.VARCHAR),
                            new DataValue(itemsno, Types.VARCHAR),
                            new DataValue(request.getOpNo(), Types.VARCHAR),
                            new DataValue(loaddoctype, Types.VARCHAR),
                            new DataValue(couponCode, Types.VARCHAR),
                            new DataValue("", Types.VARCHAR), // 券消费单号
                            new DataValue(request.getOperatorId(), Types.VARCHAR),
                            new DataValue(request.getOperatorName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)
                    };
                    InsBean ins = new InsBean("DCP_MEMBERSERVICE", column);
                    ins.addValues(insValuele);
                    this.addProcessData(new DataProcessBean(ins));
                }
            }


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveOrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveOrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveOrderStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveOrderStatusUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveOrderStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getReserveNo())) {
            errMsg.append("预约单号不能为空");
            isFail = true;
        }
        if (Check.Null(request.getOperatorId())) {
            errMsg.append("操作人编号不能为空");
            isFail = true;
        }
//        if (Check.Null(request.getOperatorName())) {
//            errMsg.append("操作人名称不能为空");
//            isFail = true;
//        }
        if (Check.Null(request.getLoadDocType())) {
            errMsg.append("来源渠道类型不能为空");
            isFail = true;
        }
        if (Check.Null(request.getStatus())) {
            errMsg.append("单据状态不能为空");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveOrderStatusUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveOrderStatusUpdate_OpenReq>() {
        };
    }

    @Override
    protected DCP_ReserveOrderStatusUpdate_OpenRes getResponseType() {
        return new DCP_ReserveOrderStatusUpdate_OpenRes();
    }
}
