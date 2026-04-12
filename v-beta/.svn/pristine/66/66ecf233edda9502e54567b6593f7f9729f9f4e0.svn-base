package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReserveCouponQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ReserveCouponQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ESBUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 会员体验券查询
 * @author: wangzyc
 * @create: 2021-08-03
 */
public class DCP_ReserveCouponQuery_Open extends SPosBasicService<DCP_ReserveCouponQuery_OpenReq, DCP_ReserveCouponQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReserveCouponQuery_OpenReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ReserveCouponQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveCouponQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_ReserveCouponQuery_OpenRes getResponseType() {
        return new DCP_ReserveCouponQuery_OpenRes();
    }

    @Override
    protected DCP_ReserveCouponQuery_OpenRes processJson(DCP_ReserveCouponQuery_OpenReq req) throws Exception {
        DCP_ReserveCouponQuery_OpenRes res = this.getResponseType();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String eId = req.geteId();

        int totalRecords = 0; // 总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<DCP_ReserveCouponQuery_OpenRes.level1Elm>());
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> maps = this.doQueryData(sql, null);
            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("COUPONCODE", true);
            condition.put("COUPONTYPEID", true);
            // 调用过滤函数
            List<Map<String, Object>> getHeader = MapDistinct.getMap(maps, condition);

            condition.clear();
            condition.put("COUPONCODE", true);
            condition.put("COUPONTYPEID", true);
            condition.put("ITEMSNO", true);
            // 调用过滤函数
            List<Map<String, Object>> getItems = MapDistinct.getMap(maps, condition);

            condition.clear();
            condition.put("COUPONCODE", true);
            condition.put("COUPONTYPEID", true);
            condition.put("ITEMSNO", true);
            condition.put("SHOPID", true);
            // 调用过滤函数
            List<Map<String, Object>> getShops = MapDistinct.getMap(maps, condition);

            if (!CollectionUtils.isEmpty(getHeader)) {
                String num = getHeader.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                // 拼接返回图片路径
                String isHttps = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr = isHttps.equals("1") ? "https://" : "http://";
                String domainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                } else {
                    domainName = httpStr + domainName + "/resource/image/";
                }
                for (Map<String, Object> map : getHeader) {
                    DCP_ReserveCouponQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    int couponKind = Integer.parseInt(map.get("COUPONKIND").toString());
                    String coupontypeid = map.get("COUPONTYPEID").toString();
                    String couponcode = map.get("COUPONCODE").toString();

                    lv1.setCouponCode(couponcode);
                    lv1.setCouponName(map.get("COUPONTYPENAME").toString());
                    lv1.setCouponType(coupontypeid);
                    lv1.setDescription2(Check.Null(map.get("DESCRIPTION").toString()) ? "" : map.get("DESCRIPTION").toString());
                    lv1.setBeginDate(!Check.Null(map.get("BEGINDATE").toString()) ? sf.format(sf.parse(map.get("BEGINDATE").toString())) : "");
                    lv1.setEndDate(!Check.Null(map.get("ENDDATE").toString()) ? sf.format(sf.parse(map.get("ENDDATE").toString())) : "");
                    String allowGift = Check.Null(map.get("ALLOWGIFT").toString()) ? "0" : map.get("ALLOWGIFT").toString();
                    lv1.setAllowGift(allowGift);
                    lv1.setCouponEverytime(Check.Null(map.get("COUPONEVERYTIME").toString()) ? "" : map.get("COUPONEVERYTIME").toString());

                    BigDecimal faceAmount = new BigDecimal(0);
                    if (!Check.Null(map.get("FACEAMOUNT").toString())) {
                        faceAmount = new BigDecimal(map.get("FACEAMOUNT").toString());
                    }
                    String des = "销售价值" + faceAmount + "元的服务项目";
                    lv1.setDescription(des);

                    String today = sf.format(sf.parse(map.get("TODAY").toString()));
                    String retstatus = "";
                    String status = map.get("STATUS").toString();
                    //原逻辑不动
                    if (status.equals("4")) {
                        Date parse = sf.parse(lv1.getEndDate());
                        Date parse1 = sf.parse(today);
                        int i = parse.compareTo(parse1);
                        if (i < 0) {
                            retstatus = "9";
                        } else {
                            retstatus = "2";
                        }

                    } else if (status.equals("9")) {
                        retstatus = "1";
                    } else if (status.equals("6")) {
                        retstatus = "9";
                    } else {
                        retstatus = "2";
                    }
                    lv1.setStatus(retstatus);

                    String url = map.get("COVERIMG").toString();
                    url = getMediaUrl(eId, url);
                    lv1.setPicUrl(url);
                    String memberid = map.get("MEMBERID").toString();

                    lv1.setMemberId(memberid);
                    lv1.setName(map.get("MEMBERNAME").toString());
                    lv1.setMobile(map.get("MOBILEPHONE").toString());

                    lv1.setItemList(new ArrayList<DCP_ReserveCouponQuery_OpenRes.level2Elm>());
                    if (!CollectionUtils.isEmpty(getItems)) {
                        for (Map<String, Object> getItem : getItems) {
                            String coupontypeid2 = getItem.get("COUPONTYPEID").toString();
                            String couponcode2 = getItem.get("COUPONCODE").toString();

                            if (!coupontypeid2.equals(coupontypeid) || !couponcode2.equals(couponcode)) {
                                continue;
                            }

                            DCP_ReserveCouponQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                            String itemsno = getItem.get("ITEMSNO").toString();
                            if(Check.Null(itemsno)){
                                continue;
                            }
                            String itemsname = getItem.get("ITEMSNAME").toString();
                            String listimage = getItem.get("LISTIMAGE").toString();
                            if (!Check.Null(listimage)) {
                                listimage = domainName + listimage;
                            }
                            String servicetime = getItem.get("SERVICETIME").toString();
                            String serviceintroduction = getItem.get("SERVICEINTRODUCTION").toString();
                            lv2.setItemsNo(itemsno);
                            lv2.setItemsName(itemsname);
                            lv2.setImageUrl(listimage);
                            lv2.setServiceTime(servicetime);
                            lv2.setServiceIntroduction(serviceintroduction);

                            lv2.setShopList(new ArrayList<DCP_ReserveCouponQuery_OpenRes.level3Elm>());

                            if (!CollectionUtils.isEmpty(getShops)) {
                                for (Map<String, Object> getShop : getShops) {
                                    String coupontypeid3 = getShop.get("COUPONTYPEID").toString();
                                    String couponcode3 = getShop.get("COUPONCODE").toString();
                                    String itemsno2 = getShop.get("ITEMSNO").toString();

                                    if (!coupontypeid2.equals(coupontypeid3) || !couponcode2.equals(couponcode3) || !itemsno2.equals(itemsno)) {
                                        continue;
                                    }
                                    DCP_ReserveCouponQuery_OpenRes.level3Elm lv3 = res.new level3Elm();
                                    String shopid = getShop.get("SHOPID").toString();
                                    if(Check.Null(shopid)){
                                        continue;
                                    }
                                    lv3.setShopId(shopid);

                                    String shopdistribution = getShop.get("SHOPDISTRIBUTION").toString();
                                    if(Check.Null(shopdistribution)){
                                        shopdistribution = "Y"; // 默认到店分配为Y
                                    }
                                    lv3.setShopDistribution(shopdistribution);
                                    lv2.getShopList().add(lv3);
                                }
                            }
                            lv1.getItemList().add(lv2);

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
    protected String getQuerySql(DCP_ReserveCouponQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        DCP_ReserveCouponQuery_OpenReq.level1Elm request = req.getRequest();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String status = request.getStatus();
        String memberId = request.getMemberId();
        String couponCode = request.getCouponCode();
        String shopId = request.getShopId();
        String keyTxt = request.getKeyTxt();

        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sqlbuf.append("  SELECT * FROM (" +
                " SELECT count(DISTINCT a.COUPONCODE) OVER () AS num, DENSE_RANK() OVER (ORDER BY a.COUPONCODE,a.CREATETIME) AS rn ," +
                " a.COUPONCODE,a.COUPONTYPEID,a.FACEAMOUNT,a.BEGINDATE,a.ENDDATE,SYSDATE as TODAY,a.STATUS,b.COUPONTYPENAME,b.COUPONKIND,b.DESCRIPTION,b.AMOUNT1,b.AMOUNT2,b.COVERIMG,a.STRIKEPRICE,b.RESTRICTCHANNEL,b.RESTRICTSHOP,b.RESTRICTGOODS,a.DISCRATE, " +
                " b.ALLOWGIFT,b.AUTOUSE,b.COUPONEVERYTIME,b.DISCOUNTTYPE, c.MEMBERNAME ,c.MOBILEPHONE ,d.ITEMSNO ,d.ITEMSNAME ,e.SHOPID ,a.MEMBERID, " +
                " f.LISTIMAGE,d.SERVICETIME,d.SERVICEINTRODUCTION,g.SHOPDISTRIBUTION" +
                " from CRM_COUPON a join CRM_COUPONTYPE b on a.COUPONTYPEID = b.COUPONTYPEID and a.EID = b.EID and b.COUPONKIND <> 4 and b.COUPONKIND <> 8 " +
                " LEFT JOIN CRM_MEMBER c ON  a.eid = c.eid AND a.MEMBERID  = c.MEMBERID " +
//                " LEFT JOIN CRM_COUPONGOODS a2 on a.eid = a2.eid and a.COUPONTYPEID = a2.COUPONTYPEID and a2.GOODSTYPE = '2' " +
                " LEFT JOIN DCP_SERVICEITEMS d ON a.EID  = d.EID  AND a.COUPONTYPEID = d.COUPONTYPEID  " +
                " LEFT JOIN DCP_RESERVEITEMS e ON a.EID  = e.EID  AND d.ITEMSNO  = e.ITEMSNO " +
                " LEFT JOIN DCP_GOODSIMAGE f ON a.EID = f.EID AND d.ITEMSNO = f.PLUNO AND f.APPTYPE = 'ALL' " +
                " LEFT JOIN DCP_RESERVEPARAMETER g ON a.EID = g.EID AND e.SHOPID = g.SHOPID  " +
                " LEFT JOIN DCP_RESERVE h ON a.EID  = b.EID  AND a.MEMBERID  = h.MEMBERID  AND a.COUPONCODE  = h.COUPONCODE " +
                " where a.EID = '" + req.geteId() + "' AND a.MEMBERID is not null AND a.SHARED <> 1 AND b.COUPONKIND = '5' " +
                " ");

        if (!Check.Null(status)) {
            if (status.equals("1")) {
                sqlbuf.append(" AND (a.STATUS = 9 and TRUNC(a.USEDATE) > TRUNC(SYSDATE)-90)");
            } else if (status.equals("9")) {
                sqlbuf.append(" AND ((a.STATUS = 4 AND TRUNC(a.ENDDATE) < TRUNC(SYSDATE) AND TRUNC(a.ENDDATE) > TRUNC(SYSDATE)-90) OR (a.STATUS = 6 AND TRUNC(a.ENDDATE) > TRUNC(SYSDATE)-90))");
            } else if (status.equals("2")) {
                sqlbuf.append(" AND (a.STATUS = 4 and TRUNC(a.ENDDATE) >= TRUNC(SYSDATE) AND TRUNC(a.BEGINDATE) <= TRUNC(SYSDATE) )");
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "无效的查询参数status:" + status);
            }
        } else {
            sqlbuf.append(" and (");
            sqlbuf.append(" (a.STATUS = 9 and TRUNC(a.USEDATE) > TRUNC(SYSDATE)-90)");
            sqlbuf.append(" or (a.STATUS = 4 AND TRUNC(a.ENDDATE) > TRUNC(SYSDATE)-90)");
            sqlbuf.append(" or (a.STATUS = 6 AND TRUNC(a.ENDDATE) > TRUNC(SYSDATE)-90)");
            sqlbuf.append(")");
        }
        sqlbuf.append(" AND (h.STATUS in('3','4')OR h.STATUS IS NULL)");
        if (!Check.Null(couponCode)) {
            sqlbuf.append(" AND a.COUPONCODE='" + couponCode + "' ");
        }
        if (!Check.Null(memberId)) {
            sqlbuf.append(" AND a.MEMBERID = '" + memberId + "'");
        }
        if (!Check.Null(shopId)) {
            sqlbuf.append(" AND e.SHOPID = '" + shopId + "'");
        }
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" AND (c.MOBILEPHONE LIKE '%%" + keyTxt + "%%' OR c.MEMBERID LIKE '%%" + keyTxt + "%%' OR c.MEMBERNAME LIKE '%%" + keyTxt + "%%')");
        }
        sqlbuf.append(" AND  a.COUPONCODE NOT IN (SELECT DISTINCT COUPONCODE FROM DCP_RESERVE WHERE eid = '"+req.geteId()+"' AND MEMBERID = '"+memberId+"' AND STATUS IN ('0','1','2') AND COUPONCODE IS NOT NULL)");
        sqlbuf.append(" order by a.CREATETIME");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }

    public String getMediaUrl(String EID, String mediaid) throws Exception {
        if (Check.Null(mediaid)) {
            return mediaid;
        }


        if (mediaid.toLowerCase().startsWith("http")) {
            return mediaid;
        }

        String sql = "select MEDIAID as MSGID,TYPE,NAME,MEDIAURL,WXMEDIAID from CRM_MEDIA where EID=? and MEDIAID = ?";
        String[] params = new String[]{EID, mediaid};
        List<Map<String, Object>> maps = this.doQueryData(sql, params);
        if (maps.size() > 0) {
            String url = maps.get(0).get("MEDIAURL").toString();
            return url;
        }
        return "";
    }

}
