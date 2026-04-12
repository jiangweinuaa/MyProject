package com.dsc.spos.service.imp.json;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_AdvisorQueryReq;
import com.dsc.spos.json.cust.res.DCP_AdvisorQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 顾问查询
 * @author: wangzyc
 * @create: 2021-07-19
 */
public class DCP_AdvisorQuery extends SPosBasicService<DCP_AdvisorQueryReq, DCP_AdvisorQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AdvisorQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_AdvisorQueryReq.level1Elm request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_AdvisorQueryReq> getRequestType() {
        return new TypeToken<DCP_AdvisorQueryReq>() {
        };
    }

    @Override
    protected DCP_AdvisorQueryRes getResponseType() {
        return new DCP_AdvisorQueryRes();
    }

    @Override
    protected DCP_AdvisorQueryRes processJson(DCP_AdvisorQueryReq req) throws Exception {
        DCP_AdvisorQueryRes res = this.getResponseType();
        String eId = req.geteId();
        res.setDatas(new ArrayList<DCP_AdvisorQueryRes.level1Elm>());

        int totalRecords = 0; // 总笔数
        int totalPages = 0;

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getDatas)) {

                String num = getDatas.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                condition.put("OPNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(getDatas, condition);

                condition.clear();
                condition.put("OPNO", true);
                condition.put("SHOPID", true);
                condition.put("BITEM", true);
                //调用过滤函数
                List<Map<String, Object>> getSchedulings = MapDistinct.getMap(getDatas, condition);

                condition.clear();
                condition.put("OPNO", true);
                condition.put("SHOPID", true);
                condition.put("CITEM", true);
                //调用过滤函数
                List<Map<String, Object>> getResttimes = MapDistinct.getMap(getDatas, condition);
                condition.clear();

                String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=ISHTTPS.equals("1")?"https://":"http://";
                String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");

                for (Map<String, Object> oneData : getQHeader) {
                    DCP_AdvisorQueryRes.level1Elm lv1 = res.new level1Elm();
                    String opNo = oneData.get("OPNO").toString();
                    String opName = oneData.get("OPNAME").toString();
                    String headImage = oneData.get("HEADIMAGE").toString();
                    String professional = oneData.get("PROFESSIONAL").toString();
                    String professionalName = oneData.get("PROFESSIONALNAME").toString();
                    String ability = oneData.get("ABILITY").toString();
                    String status = oneData.get("STATUS").toString();

                    lv1.setOpNo(opNo);
                    lv1.setOpName(opName);
                    lv1.setHeadImageName(headImage);
                    // 拼接图片
                    if(!Check.Null(headImage)){
                        if (DomainName.endsWith("/"))
                        {
                            lv1.setHeadImageUrl(httpStr+DomainName+"resource/image/" +headImage);
                        }
                        else
                        {
                            lv1.setHeadImageUrl(httpStr+DomainName+"/resource/image/" +headImage);
                        }
                    }

                    lv1.setProfessionalId(professional);
                    lv1.setProfessionalName(professionalName);
                    lv1.setAbility(ability);
                    lv1.setStatus(status);

                    lv1.setSchedulingList(new ArrayList<DCP_AdvisorQueryRes.level2Elm>());
                    lv1.setRestTimeList(new ArrayList<DCP_AdvisorQueryRes.level3Elm>());

                    if (!CollectionUtils.isEmpty(getSchedulings)) {
                        for (Map<String, Object> twoData : getSchedulings) {
                            String opNo2 = twoData.get("OPNO").toString();
                            if (!opNo2.equals(opNo)) {
                                continue;
                            }
                            DCP_AdvisorQueryRes.level2Elm lv2 = res.new level2Elm();
                            lv2.setShopId(twoData.get("BSHOP").toString());
                            lv2.setCycle(twoData.get("CYCLE").toString());
                            lv2.setItem(twoData.get("BITEM").toString());
                            lv2.setTimeInterval(twoData.get("TIMEINTERVAL").toString());
                            lv2.setAppointments(twoData.get("APPOINTMENTS").toString());
                            if(Check.Null(lv2.getItem())){
                                continue;
                            }
                            lv1.getSchedulingList().add(lv2);
                        }
                    }

                    if (!CollectionUtils.isEmpty(getResttimes)) {
                        for (Map<String, Object> threeData : getResttimes) {
                            String opNo2 = threeData.get("OPNO").toString();
                            if (!opNo2.equals(opNo)) {
                                continue;
                            }
                            DCP_AdvisorQueryRes.level3Elm lv3 = res.new level3Elm();
                            lv3.setShopId(threeData.get("CSHOP").toString());
                            lv3.setItem(threeData.get("CITEM").toString());
                            lv3.setCycleType(threeData.get("CYCLETYPE").toString());
                            lv3.setRestTime(threeData.get("RESTTIME").toString());
                            lv3.setStatus(threeData.get("CSTATUS").toString());
                            if(Check.Null(lv3.getItem())){
                                continue;
                            }
                            lv1.getRestTimeList().add(lv3);
                        }
                    }
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
    protected String getQuerySql(DCP_AdvisorQueryReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        DCP_AdvisorQueryReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String keyTxt = request.getKeyTxt();
        String status = request.getStatus();
        String eId = req.geteId();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sqlbuf.append(" SELECT * FROM ( " +
                " SELECT count(DISTINCT a.OPNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.OPNO DESC) AS rn , a.OPNO, a.OPNAME, " +
                " a.HEADIMAGE, a.PROFESSIONAL, d.PROFESSIONALNAME , a.ABILITY, a.STATUS, b.SHOPID AS bshop, b.\"CYCLE\"," +
                " b.TIMEINTERVAL ,b.ITEM bItem,b.APPOINTMENTS, c.SHOPID AS cshop, c.ITEM cITEM,c.CYCLETYPE,  c.RESTTIME, c.STATUS AS cstatus " +
                " FROM DCP_ADVISORSET a " +
                " left join (SELECT a.opno ,a.ORGANIZATIONNO AS shopid FROM PLATFORM_STAFFS a WHERE a.EID  = '"+eId+"' and a.status = '100' " +
                " UNION  " +
                " SELECT b.opno ,b.SHOPID FROM PLATFORM_STAFFS_SHOP b where b.EID  = '"+eId+"' and b.status = '100') f on  a.opno = f.opno " +
                " LEFT JOIN DCP_ADVISORSET_SCHEDULING b ON a.eid = b.eid AND a.opno = b.OPNO and f.shopid = b.shopid " +
                " LEFT JOIN DCP_ADVISORSET_RESTTIME c ON a.EID = c.EID AND a.OPNO = c.OPNO and f.shopid = c.shopid" +
                " LEFT JOIN DCP_PROFESSIONALGRADE d ON a.PROFESSIONAL = d.PROFESSIONALID " +

                " WHERE a.EID = '" + eId + "'");
        if (!Check.Null(shopId)) {
            sqlbuf.append(" and f.SHOPID = '" + shopId + "'");
        }

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.opNO = '"+keyTxt+"' or a.opNAME like '%%"+keyTxt+"%%')");
        }

        if (!Check.Null(status)) {
            sqlbuf.append(" and a.status = '" + status + "'");
        }
        sqlbuf.append(" ORDER BY a.opno,b.item,c.item");

        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");

        sql = sqlbuf.toString();
        return sql;
    }
}
