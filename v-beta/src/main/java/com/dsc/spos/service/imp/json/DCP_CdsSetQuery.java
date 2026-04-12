package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CdsSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_CdsSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @description: CDS叫号屏设置查询
 * @author: wangzyc
 * @create: 2022-05-25
 */
public class DCP_CdsSetQuery extends SPosBasicService<DCP_CdsSetQueryReq, DCP_CdsSetQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CdsSetQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CdsSetQueryReq> getRequestType() {
        return new TypeToken<DCP_CdsSetQueryReq>(){};
    }

    @Override
    protected DCP_CdsSetQueryRes getResponseType() {
        return new DCP_CdsSetQueryRes();
    }

    @Override
    protected DCP_CdsSetQueryRes processJson(DCP_CdsSetQueryReq req) throws Exception {
        DCP_CdsSetQueryRes res = this.getResponseType();
        String eId = req.geteId();

        int totalRecords = 0;// 总笔数
        int totalPages = 0;

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> cdsDataDetails = this.doQueryData(sql, null);

            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("TEMPLATEID", true);
            condition.put("EID", true);
            // 调用过滤函数
            List<Map<String, Object>> cdsSetDatas = MapDistinct.getMap(cdsDataDetails, condition);
            condition.clear();

            // 过滤  门店
            Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
            condition2.put("TEMPLATEID", true);
            condition2.put("EID", true);
            condition2.put("SHOPID", true);
            // 调用过滤函数
            List<Map<String, Object>> cdsSetShopDatas = MapDistinct.getMap(cdsDataDetails, condition2);
            condition.clear();

            // 过滤 图片
            Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); // 查詢條件
            condition3.put("TEMPLATEID", true);
            condition3.put("EID", true);
            condition3.put("SORTID", true);
            // 调用过滤函数
            List<Map<String, Object>> cdsSetFileDatas = MapDistinct.getMap(cdsDataDetails, condition3);
            condition.clear();

            res.setDatas(new ArrayList<>());

            if(!CollectionUtils.isEmpty(cdsSetDatas)){
                String num = cdsSetDatas.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);

                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                ////图片地址参数获取
                String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                String httpStr = isHttps.equals("1") ? "https://" : "http://";
                String domainName = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
                String imagePath = "";
                if (domainName.endsWith("/")) {
                    imagePath = httpStr + domainName + "resource/image/";
                } else {
                    imagePath = httpStr + domainName + "/resource/image/";
                }

                for (Map<String, Object> cdsSetData : cdsSetDatas) {
                    String templateid = cdsSetData.get("TEMPLATEID").toString();
                    String status = cdsSetData.get("STATUS").toString();
                    String filetype = cdsSetData.get("FILETYPE").toString();
                    String restrictshop = cdsSetData.get("RESTRICTSHOP").toString();
                    String memo = cdsSetData.get("MEMO").toString();
                    String updatetime = cdsSetData.get("UPDATETIME").toString();
                    String iscustomcolour = cdsSetData.get("ISCUSTOMCOLOUR").toString();
                    String themecolor = cdsSetData.get("THEMECOLOR").toString();
                    String subcolor = cdsSetData.get("SUBCOLOR").toString();
                    String fontcolortype = cdsSetData.get("FONTCOLORTYPE").toString();
                    String voicetext = cdsSetData.get("VOICETEXT").toString();
                    String voicetimes = cdsSetData.get("VOICETIMES").toString();
                    String rolltime = cdsSetData.get("ROLLTIME").toString();
                    String showwaimai = cdsSetData.get("SHOWWAIMAI").toString();
                    String serviceurl = cdsSetData.get("SERVICEURL").toString();
                    String updateurl = cdsSetData.get("UPDATEURL").toString();
                    String colourid = cdsSetData.get("COLOURID").toString();

                    DCP_CdsSetQueryRes.level1Elm lv1 = res.new level1Elm();
                    lv1.setBaseSetNo(templateid);
                    lv1.setStatus(status);
                    lv1.setFileType(filetype);
                    lv1.setRestrictShop(restrictshop);
                    lv1.setMemo(memo);
                    lv1.setUpdateTime(updatetime);
                    lv1.setIsCustomColour(iscustomcolour);
                    lv1.setThemeColor(themecolor);
                    lv1.setSubColor(subcolor);
                    lv1.setFontColorType(fontcolortype);
                    lv1.setVoiceText(voicetext);
                    lv1.setVoiceTimes(voicetimes);
                    lv1.setRollTime(rolltime);
                    lv1.setShowWaimai(showwaimai);
                    lv1.setServiceUrl(serviceurl);
                    lv1.setUpdateUrl(updateurl);
                    lv1.setColourId(colourid);

                    lv1.setRangeList(new ArrayList<>());


                    if(!CollectionUtils.isEmpty(cdsSetShopDatas)){
                        for (Map<String, Object> cdsSetShopData : cdsSetShopDatas) {
                            String templateid2 = cdsSetShopData.get("TEMPLATEID").toString();

                            if(!templateid.equals(templateid2)){
                                continue;
                            }
                            String shopid = cdsSetShopData.get("SHOPID").toString();

                            if(Check.Null(shopid)){
                                continue;
                            }
                            String org_name = cdsSetShopData.get("ORG_NAME").toString();
                            DCP_CdsSetQueryRes.level3Elm lv3 = res.new level3Elm();
                            lv3.setShopId(shopid);
                            lv3.setShopName(org_name);
                            lv1.getRangeList().add(lv3);

                        }

                    }


                    lv1.setFileList(new ArrayList<>());
                    if(!CollectionUtils.isEmpty(cdsSetFileDatas)){
                        for (Map<String, Object> cdsSetFileData : cdsSetFileDatas) {
                            String templateid2 = cdsSetFileData.get("TEMPLATEID").toString();

                            if(!templateid.equals(templateid2)){
                                continue;
                            }
                            String sortid = cdsSetFileData.get("SORTID").toString();

                            String filename = cdsSetFileData.get("FILENAME").toString();
                            if(Check.Null(filename)){
                                continue;
                            }
                            String fileUrl = "";

                            if (!Check.Null(filename)){
                                fileUrl = imagePath+filename;
                            }

                            DCP_CdsSetQueryRes.level2Elm lv2 = res.new level2Elm();
                            lv2.setItem(sortid);
                            lv2.setFileName(filename);
                            lv2.setFileUrl(fileUrl);
                            lv1.getFileList().add(lv2);

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
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CdsSetQueryReq req) throws Exception {
        String langType = req.getLangType();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;

        sqlbuf.append("select * from ( " +
                " SELECT count(DISTINCT a.TEMPLATEID) OVER() AS NUM ,dense_rank() over (order BY a.TEMPLATEID  ) rn, " +
                " a.*, b.shopId, c.ORG_NAME,d.SORTID,d.FILENAME " +
                " FROM DCP_CDSSET a " +
                " LEFT JOIN DCP_CDSSET_SHOP b ON a.eid = b.eid AND a.TEMPLATEID = b.TEMPLATEID " +
                " LEFT JOIN DCP_ORG_LANG c ON a.eid = c.eid AND b.shopid = c.ORGANIZATIONNO AND c.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_CDSSET_ADVERT d ON a.eid = d.eid AND a.TEMPLATEID = d.TEMPLATEID " +
                " WHERE a.eid = '"+req.geteId()+"' " +
                " ORDER BY a.TEMPLATEID,d.SORTID");
        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize)  + "" );
        sql = sqlbuf.toString();
        return sql;
    }
}
