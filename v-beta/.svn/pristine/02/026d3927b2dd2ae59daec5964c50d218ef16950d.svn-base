package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MemberServiceQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_MemberServiceQuery_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 服务记录查询
 * @author: wangzyc
 * @create: 2021-08-10
 */
public class DCP_MemberServiceQuery_Open extends SPosBasicService<DCP_MemberServiceQuery_OpenReq, DCP_MemberServiceQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_MemberServiceQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_MemberServiceQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getMemberId())) {
            errMsg.append("会员号不能为空");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_MemberServiceQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_MemberServiceQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_MemberServiceQuery_OpenRes getResponseType() {
        return new DCP_MemberServiceQuery_OpenRes();
    }

    @Override
    protected DCP_MemberServiceQuery_OpenRes processJson(DCP_MemberServiceQuery_OpenReq req) throws Exception {
        DCP_MemberServiceQuery_OpenRes res = this.getResponseType();
        int totalRecords = 0; // 总笔数
        int totalPages = 0;
        res.setDatas(new ArrayList<DCP_MemberServiceQuery_OpenRes.level1Elm>());
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getDatas)) {
                String num = getDatas.get(0).getOrDefault("NUM", "0").toString();
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
                for (Map<String, Object> oneData : getDatas) {
                    String itemsno = oneData.get("ITEMSNO").toString();
                    String itemsName = oneData.get("ITEMSNAME").toString();
                    String listimage = oneData.get("LISTIMAGE").toString();
                    if (!Check.Null(listimage)) {
                        listimage = domainName + listimage;
                    }
                    String servicetime = oneData.get("SERVICETIME").toString();
                    String shopid = oneData.get("SHOPID").toString();
                    String memberid = oneData.get("MEMBERID").toString();
                    String opno = oneData.get("OPNO").toString();
                    String opname = oneData.get("OPNAME").toString();
                    String professionalname = oneData.get("PROFESSIONALNAME").toString();
                    String date = oneData.get("DATE").toString();
                    String time = oneData.get("TIME").toString();
                    String memo = oneData.get("MEMO").toString();
                    String createtime = oneData.get("CREATETIME").toString();
                    String shopName = oneData.get("ORG_NAME").toString();

                    DCP_MemberServiceQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setItemsNo(itemsno);
                    lv1.setItemsName(itemsName);
                    lv1.setImageUrl(listimage);
                    lv1.setServiceTime(servicetime);
                    lv1.setShopId(shopid);
                    lv1.setMemberId(memberid);
                    lv1.setOpNo(opno);
                    lv1.setOpName(opname);
                    lv1.setProfessionalName(professionalname);
                    lv1.setDate(sf.format(sf.parse(date)));
                    lv1.setTime(time);
                    lv1.setMemo(memo);
                    lv1.setCreateTime(sfs.format(sfs.parse(createtime)));
                    lv1.setShopName(shopName);
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
    protected String getQuerySql(DCP_MemberServiceQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        // 計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sqlbuf.append(" SELECT * FROM (  " +
                " SELECT count(a.RESERVENO) OVER () AS NUM, DENSE_RANK() OVER (ORDER BY a.SHOPID, a.RESERVENO) AS rn , " +
                " a.ITEMSNO, b.ITEMSNAME, c.LISTIMAGE, b.SERVICETIME, a.SHOPID , a.MEMBERID, a.OPNO, d.OPNAME, e.PROFESSIONALNAME,  " +
                " f.BDATE as  \"date\" , f.\"TIME\", f.MEMO, a.CREATETIME,g.ORG_NAME " +
                " FROM DCP_MEMBERSERVICE a " +
                " LEFT JOIN DCP_SERVICEITEMS b ON a.EID = b.EID AND a.ITEMSNO = b.ITEMSNO " +
                " LEFT JOIN DCP_GOODSIMAGE c ON a.EID = c.EID AND a.ITEMSNO = c.PLUNO AND c.APPTYPE = 'ALL' " +
                " LEFT JOIN DCP_ADVISORSET d ON a.EID = d.EID AND a.OPNO = d.OPNO " +
                " LEFT JOIN DCP_PROFESSIONALGRADE e ON d.PROFESSIONAL = e.PROFESSIONALID " +
                " LEFT JOIN DCP_RESERVE f ON a.EID = f.EID AND a.ITEMSNO = f.ITEMSNO AND a.SHOPID = f.SHOPID AND a.RESERVENO = f.RESERVENO " +
                " LEFT JOIn DCP_ORG_LANG g ON a.EID = g.EID AND g.ORGANIZATIONNO =  a.SHOPID AND g.LANG_TYPE = '"+req.getLangType()+"' AND g.STATUS = '100'" +
                " WHERE a.EID = '" + req.geteId() + "' and a.MEMBERID = '" + req.getRequest().getMemberId() + "'");
        sqlbuf.append(" ) WHERE rn > " + startRow + " AND rn <= " + (startRow + pageSize) + " ");
        sql = sqlbuf.toString();
        return sql;
    }
}
