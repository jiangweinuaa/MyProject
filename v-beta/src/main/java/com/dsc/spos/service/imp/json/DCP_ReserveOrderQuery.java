package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.req.DCP_ReserveOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReserveOrderQueryRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 预约列表查询
 * @author: wangzyc
 * @create: 2021-07-28
 */
public class DCP_ReserveOrderQuery extends SPosBasicService<DCP_ReserveOrderQueryReq, DCP_ReserveOrderQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReserveOrderQueryReq req) throws Exception {
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveOrderQueryReq.level1Elm request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ReserveOrderQueryReq> getRequestType() {
        return new TypeToken<DCP_ReserveOrderQueryReq>() {
        };
    }

    @Override
    protected DCP_ReserveOrderQueryRes getResponseType() {
        return new DCP_ReserveOrderQueryRes();
    }

    @Override
    protected DCP_ReserveOrderQueryRes processJson(DCP_ReserveOrderQueryReq req) throws Exception {
        DCP_ReserveOrderQueryRes res = this.getResponseType();
        String eId = req.geteId();
        DCP_ReserveOrderQueryReq.level1Elm request = req.getRequest();
        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        res.setDatas(new ArrayList<DCP_ReserveOrderQueryRes.level1Elm>());

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getDatas)) {
                String num = getDatas.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dfh = new SimpleDateFormat("HH:mm");
                SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String currentDate = df.format(new Date());
                String currentTime = dfh.format(new Date());
                Date parseCurrentDate = df.parse(currentDate);
                Date parseCurrentTime = dfh.parse(currentTime);
                for (Map<String, Object> getData : getDatas) {
                    DCP_ReserveOrderQueryRes.level1Elm lv1 = res.new level1Elm();
                    String reserveno = getData.get("RESERVENO").toString();
                    String loaddoctype = getData.get("LOADDOCTYPE").toString();
                    String loadDocName = getData.get("APPNAME").toString();
                    String shopId = getData.get("SHOPID").toString();
                    String shopName = getData.get("ORG_NAME").toString();
                    String phone = getData.get("PHONE").toString();
                    String itemsNo = getData.get("ITEMSNO").toString();
                    String itemsName = getData.get("ITEMSNAME").toString();
                    String listimage = getData.get("LISTIMAGE").toString();
                    // 拼接图片
                    if (!Check.Null(listimage)) {
                        if (DomainName.endsWith("/")) {
                            lv1.setImageUrl(httpStr + DomainName + "resource/image/" + listimage);
                        } else {
                            lv1.setImageUrl(httpStr + DomainName + "/resource/image/" + listimage);
                        }
                    }else{
                        lv1.setImageUrl("");
                    }

                    String serviceTime = getData.get("SERVICETIME").toString();
                    String shopDistribution = getData.get("SHOPDISTRIBUTION").toString();
                    String opNo = getData.get("OPNO").toString();
                    String opName = getData.get("OPNAME").toString();
                    String professionalName = getData.get("PROFESSIONALNAME").toString();
                    String headImage = getData.get("HEADIMAGE").toString();
                    // 拼接图片
                    if (!Check.Null(headImage)) {
                        if (DomainName.endsWith("/")) {
                            lv1.setHeadImage(httpStr + DomainName + "resource/image/" + headImage);
                        } else {
                            lv1.setHeadImage(httpStr + DomainName + "/resource/image/" + headImage);
                        }
                    }else{
                        lv1.setHeadImage("");
                    }
                    String memberId = getData.get("MEMBERID").toString();
                    String name = getData.get("NAME").toString();
                    String mobile = getData.get("MOBILE").toString();
                    String date = getData.get("DATE").toString();
                    String time = getData.get("TIME").toString();
                    String memo = getData.get("MEMO").toString();
                    String isEvaluate = getData.get("ISEVALUATE").toString();
                    if (Check.Null(isEvaluate)) {
                        isEvaluate = "N";
                    }
                    String createOpId = getData.get("CREATEOPID").toString();
                    String createOpName = getData.get("CREATEOPNAME").toString();
                    String createTime = getData.get("CREATETIME").toString();
                    String couponcode = getData.get("COUPONCODE").toString();

                    String status = getData.get("STATUS").toString();
                    Date parse = df.parse(date);

                    String qrCode = "";
                    // 外部接口的时候 根据couponCode生成券二维码
                    if(req.getApiUser()!=null){
                        String appid = req.getApiUserCode();
                        if(status.equals("1")){

                            String memberUrl = PosPub.getCRM_INNER_URL(eId);
                            if (memberUrl.trim().equals("")) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口参数未设置!");
                            }
                            // 针对status==1.待服务的预约单，根据couponCode生成券二维码，券二维码生成规则参照CRM接口GetMemberQrCode；
                            JSONObject memberQrCodeReq = new JSONObject();
                            memberQrCodeReq.put("serviceId", "GetMemberQrCode");

                            JSONObject mqcReq = new JSONObject();
                            mqcReq.put("memberId", memberId);       //  会员号
                            mqcReq.put("couponNo", couponcode);             // 卡號
                            mqcReq.put("cardNo", "");       // 券號

                            memberQrCodeReq.put("request", mqcReq);

                            String mqcReqStr = mqcReq.toString();
                            String mqcSign = PosPub.encodeMD5(mqcReqStr + req.getApiUser().getUserKey());

                            JSONObject mqcSignJson = new JSONObject();
                            mqcSignJson.put("sign", mqcSign);
                            mqcSignJson.put("key", req.getApiUserCode());

                            memberQrCodeReq.put("sign", mqcSignJson);
                            //********** 已经准备好 GetMemberQrCode 的json，开始调用 *************
                            String s = memberQrCodeReq.toString();
                            String mqcResStr = HttpSend.Sendcom(memberQrCodeReq.toString(), memberUrl).trim();
                            HelpTools.writelog_fileName("***********预约单查询 DCP_ReserveOrderQuery_Open 调用 GetMemberQrCode 接口，请求json：" + memberQrCodeReq.toString()
                                    + "*************", "DCP_ReserveOrderCreate");

                            JSONObject mqcResJson = new JSONObject();
                            mqcResJson = JSON.parseObject(mqcResStr);//String转json
                            HelpTools.writelog_fileName("***********预约单查询 DCP_ReserveOrderQuery_Open 调用 GetMemberQrCode 接口返回信息：" + mqcResStr + "*************", "DCP_ReserveOrderCreate");
                            String mqcSuccess = mqcResJson.getString("success").toUpperCase(); // TRUE 或 FALSE

                            if (!Check.Null(mqcSuccess) && mqcSuccess.equals("TRUE")) {
                                JSONObject std_data_res = mqcResJson.getJSONObject("datas");
                                 qrCode = std_data_res.getString("qrCode");
                            }
                        }
                    }

                    lv1.setReserveNo(reserveno);
                    lv1.setLoadDocType(loaddoctype);
                    lv1.setLoadDocName(loadDocName);
                    lv1.setStatus(status);
                    lv1.setShopId(shopId);
                    lv1.setShopName(shopName);
                    lv1.setPhone(phone);
                    lv1.setItemsNo(itemsNo);
                    lv1.setItemsName(itemsName);
                    lv1.setServiceTime(serviceTime);
                    lv1.setShopDistribution(shopDistribution);
                    lv1.setOpNo(opNo);
                    lv1.setOpName(opName);
                    lv1.setProfessionalName(professionalName);
                    lv1.setCouponCode(couponcode);
                    lv1.setMemberId(memberId);
                    lv1.setName(name);
                    lv1.setMobile(mobile);
                    lv1.setDate(df.format(parse));
                    lv1.setTime(time);
                    lv1.setMemo(memo);
                    lv1.setIsEvaluate(isEvaluate);
                    lv1.setCreateOpId(createOpId);
                    lv1.setCreateOpName(createOpName);
                    lv1.setCreateTime(dfs.format(dfs.parse(createTime)));
                    lv1.setQrCode(qrCode);

                    res.getDatas().add(lv1);
                }
            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ReserveOrderQueryReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        DCP_ReserveOrderQueryReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String reserveNo = request.getReserveNo();
        String status = request.getStatus();
        String createOpId = request.getCreateOpId();
        String keyTxt = request.getKeyTxt();
        String memberKeyTxt = request.getMemberKeyTxt();
        String advisorKeyTxt = request.getAdvisorKeyTxt();
        String loadDocType = request.getLoadDocType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sqlbuf.append("SELECT * FROM ( " +
                " SELECT count(a.RESERVENO) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.CREATETIME desc) AS rn , " +
                " a.RESERVENO, a.LOADDOCTYPE, d.APPNAME, a.STATUS, a.SHOPID , e.ORG_NAME, h.PHONE, a.ITEMSNO,  " +
                " b.ITEMSNAME, f.LISTIMAGE , b.SERVICETIME, a.SHOPDISTRIBUTION, a.OPNO, c.OPNAME, g.PROFESSIONALNAME , c.HEADIMAGE,  " +
                " a.MEMBERID, a.NAME, a.MOBILE, a.BDATE as \"date\" , a.\"TIME\", a.MEMO, a.ISEVALUATE, a.CREATEOPID, a.COUPONCODE , a.CREATEOPNAME, " +
                " a.CREATETIME " +
                " FROM DCP_RESERVE a " +
                " LEFT JOIN DCP_SERVICEITEMS b ON a.EID = b.EID AND a.ITEMSNO = b.ITEMSNO " +
                " LEFT JOIN DCP_ADVISORSET c ON a.EID = c.EID AND a.OPNO = c.OPNO " +
                " LEFT JOIN PLATFORM_APP d ON a.LOADDOCTYPE = d.APPNO " +
                " LEFT JOIN DCP_ORG_LANG e ON a.EID = e.EID AND a.SHOPID = e.ORGANIZATIONNO AND e.LANG_TYPE = 'zh_CN' " +
                " LEFT JOIN DCP_GOODSIMAGE f ON a.EID = f.EID AND a.ITEMSNO = f.PLUNO AND f.APPTYPE = 'ALL' " +
                " LEFT JOIN DCP_PROFESSIONALGRADE g ON c.PROFESSIONAL = g.PROFESSIONALID " +
                " LEFT JOIN DCP_ORG h ON a.EID = h.EID AND a.SHOPID = h.ORGANIZATIONNO " +
                " WHERE a.EID = '" + req.geteId() + "'");

        if (!Check.Null(shopId)) {
            sqlbuf.append(" AND a.SHOPID = '" + shopId + "'");
        }
        if (!Check.Null(reserveNo)) {
            sqlbuf.append(" AND a.RESERVENO = '" + reserveNo + "'");
        }
        if (!Check.Null(status)) {
            if (status.equals("3")) {
                sqlbuf.append(" AND a.STATUS in ('3','4')");
            }else {
                sqlbuf.append(" AND a.STATUS = '" + status + "'");
            }

        }
        if (!Check.Null(createOpId)) {
            sqlbuf.append(" AND a.CREATEOPID = '" + createOpId + "'");
        }
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" AND (b.ITEMSNAME LIKE '%%" + keyTxt + "%%' OR a.ITEMSNO '%%" + keyTxt + "%%' OR a.RESERVENO like '%%" + keyTxt + "%%')");
        }
        if (!Check.Null(memberKeyTxt)) {
            sqlbuf.append(" AND (a.MEMBERID LIKE '%%" + memberKeyTxt + "%%' OR a.NAME LIKE '%%" + memberKeyTxt + "%%' OR a.MOBILE LIKE '%%" + memberKeyTxt + "%%')");
        }
        if (!Check.Null(advisorKeyTxt)) {
            sqlbuf.append(" AND (a.OPNO LIKE '%%" + advisorKeyTxt + "%%' OR c.OPNAME LIKE '%%" + advisorKeyTxt + "%%')");
        }
        if (!Check.Null(loadDocType)) {
            sqlbuf.append(" AND a.LOADDOCTYPE = '" + loadDocType + "'");
        }
        if (!Check.Null(beginDate) && Check.Null(endDate)) {
            sqlbuf.append(" AND a.BDATE >= to_date('" + beginDate + "','YYYY-MM-DD')");
        }
        if (!Check.Null(endDate) && Check.Null(beginDate)) {
            sqlbuf.append(" AND a.BDATE <= to_date('" + endDate + "','YYYY-MM-DD')");
        }
        if (!Check.Null(endDate) && !Check.Null(beginDate)) {
            sqlbuf.append(" AND (a.BDATE >= to_date('" + beginDate + "','YYYY-MM-DD') AND a.BDATE <= to_date('" + endDate + "','YYYY-MM-DD'))");
        }
        sqlbuf.append(" ORDER BY a.CREATETIME DESC");

        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
