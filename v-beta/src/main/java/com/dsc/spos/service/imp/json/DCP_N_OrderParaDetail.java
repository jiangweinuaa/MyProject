package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_N_OrderParaDetailReq;
import com.dsc.spos.json.cust.res.DCP_N_OrderParaDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

/**
 * 服务函数：DCP_N_OrderParaDetail
 * 服务说明：N_订单参数明细 新增一个参数isDinerIn 其余代码全部继承DCP_OrderParaDetail
 * @author jinzma
 * @since  2024-05-23
 */
public class DCP_N_OrderParaDetail extends SPosBasicService<DCP_N_OrderParaDetailReq, DCP_N_OrderParaDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_N_OrderParaDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(req.getRequest()==null) {
            errMsg.append("requset不能为空值 ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getOrganizationNo())) {
                errMsg.append("组织编码不能为空值 ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_N_OrderParaDetailReq> getRequestType() {
        return new TypeToken<DCP_N_OrderParaDetailReq>(){};
    }

    @Override
    protected DCP_N_OrderParaDetailRes getResponseType() {
        return new DCP_N_OrderParaDetailRes();
    }

    @Override
    protected DCP_N_OrderParaDetailRes processJson(DCP_N_OrderParaDetailReq req) throws Exception {

        DCP_N_OrderParaDetailRes res = this.getResponse();
        DCP_N_OrderParaDetailRes.responseDatas datas = res.new responseDatas();

        try {

            String langType = req.getLangType();

            String eId = req.geteId();
            String sql = "";
            String orgNo = req.getRequest().getOrganizationNo();
            StringBuilder sqlBuffer = new StringBuilder();

            sqlBuffer.append(" select C.EID,C.ORGANIZATIONNO, C.ORG_FORM,C.SHOPBEGINTIME,C.SHOPENDTIME,C.PROVINCE,C.CITY,C.COUNTY,C.STREET,C.ADDRESS,C.LONGITUDE,C.LATITUDE,C.PHONE,C.RANGE_TYPE,C.RANGE,CL.ORG_NAME ORGNAME,"
                    + " A.ISSELFPICK, A.SELFBEGINTIME, A.SELFENDTIME, A.ISCITYDELIVERY, A.CHAOQUQUANJIASHOP, A.CHAOQUOKSHOP, A.CHAOQUSEVENSHOP, A.CHAOQULAIERFUSHOP, A.ISPRODUCTION, A.RADIATETYPE,"
                    + "B.LOADDOCTYPE,B.CITYDELIVERYTYPE,B.NATIONALDELIVERYTYPE,B.ISAUTODELIVERY,A.KDS,A.EVERYPIECE ,A.WORTHSCORE,A.COMPLETEIMAGE,A.ISQRCODE, "
                    + " A.DELIVERSHOP,DOL.ORG_NAME AS DELIVERYSHOPNAME,A.ISSPARE,A.ISDINERIN  "
                    + " from DCP_org C ");
            sqlBuffer.append(" left join DCP_ORG_ORDERSET A on A.EID=C.EID and A.ORGANIZATIONNO=C.ORGANIZATIONNO");
            sqlBuffer.append(" left join DCP_ORG_ORDERTAKESET B on A.EID = B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO ");
            sqlBuffer.append(
                    " left join DCP_org_LANG CL on A.EID=CL.EID and A.ORGANIZATIONNO=CL.ORGANIZATIONNO and CL.Lang_Type='" + langType + "' ");
            sqlBuffer.append(" LEFT JOIN DCP_ORG_LANG DOL ON A.EID = DOL.EID AND A.DELIVERSHOP = DOL.ORGANIZATIONNO AND DOL.LANG_TYPE = '" + langType + "' ");
            sqlBuffer.append(" WHERE C.EID ='" + eId + "' AND C.ORGANIZATIONNO='" + orgNo + "' ");
            sql = sqlBuffer.toString();
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);

            // 查询KDS相关的配置信息
            sqlBuffer = new StringBuilder();
            sqlBuffer.append("SELECT A.EID ,A.ORGANIZATIONNO, A.STALLID, A.STALLNAME, A.TAGTYPE, A.ACHIEVEMENTS , A.EMPLOYEE, A.GOODSDETAILS, A.ISORDER, B.TAGNO,C.TAGNAME " +
                    " FROM DCP_ORG_ORDERSET_KDS A " +
                    " LEFT JOIN DCP_ORG_ORDERSET_KDS_TAG B " +
                    " ON a.EID = b.EID AND a.ORGANIZATIONNO = b.ORGANIZATIONNO AND a.STALLID = b.STALLID  " +
                    " LEFT JOIN DCP_TAGTYPE_LANG C ON c.EID  = a.EID  AND c.TAGNO  = b.TAGNO  AND  c.TAGGROUPTYPE  = 'GOODS_PROD' AND c.LANG_TYPE  = '" + req.getLangType() + "'" +
                    " WHERE A.ORGANIZATIONNO = '" + orgNo + "' AND a.EID = '" + eId + "'");
            List<Map<String, Object>> getKdsData = this.doQueryData(sqlBuffer.toString(), null);

            Map<String, Boolean> condition_kds = new HashMap<String, Boolean>(); //查詢條件
            condition_kds.put("EID", true);
            condition_kds.put("ORGANIZATIONNO", true);
            condition_kds.put("STALLID", true);
            //调用过滤函数
            List<Map<String, Object>> kdsHeader = MapDistinct.getMap(getKdsData, condition_kds);

            if (getQData != null && !getQData.isEmpty()) {
                datas.setAutoTakeList(new ArrayList<>());
                datas.setRadiateShippingOrgList(new ArrayList<>());

                datas.setOrganizationNo(getQData.get(0).get("ORGANIZATIONNO").toString());
                datas.setOrganizationName(getQData.get(0).get("ORGNAME").toString());
                datas.setOrgForm(getQData.get(0).get("ORG_FORM").toString());
                datas.setShopBeginTime(getQData.get(0).get("SHOPBEGINTIME").toString());
                datas.setShopEndTime(getQData.get(0).get("SHOPENDTIME").toString());
                datas.setProvince(getQData.get(0).get("PROVINCE").toString());
                datas.setCity(getQData.get(0).get("CITY").toString());
                datas.setCounty(getQData.get(0).get("COUNTY").toString());
                datas.setStreet(getQData.get(0).get("STREET").toString());
                datas.setAddress(getQData.get(0).get("ADDRESS").toString());
                datas.setPhone(getQData.get(0).get("PHONE").toString());
                datas.setIsSelfPick(getQData.get(0).get("ISSELFPICK").toString());
                datas.setSelfBeginTime(getQData.get(0).get("SELFBEGINTIME").toString());
                datas.setSelfEndTime(getQData.get(0).get("SELFENDTIME").toString());
                datas.setIsCityDelivery(getQData.get(0).get("ISCITYDELIVERY").toString());
                datas.setIsProduction(getQData.get(0).get("ISPRODUCTION").toString());
                //datas.setRadiateShippingShop(getQData.get(0).get("RADIATESHIPPINGSHOP").toString());
                datas.setRange_type(getQData.get(0).get("RANGE_TYPE").toString());
                datas.setRange(getQData.get(0).get("RANGE").toString());
                datas.setChaoQuQuanJiaShop(getQData.get(0).get("CHAOQUQUANJIASHOP").toString());
                datas.setChaoQuOkShop(getQData.get(0).get("CHAOQUOKSHOP").toString());
                datas.setChaoQuSevenShop(getQData.get(0).get("CHAOQUSEVENSHOP").toString());
                datas.setChaoQuLaiErFuShop(getQData.get(0).get("CHAOQULAIERFUSHOP").toString());
                datas.setLongitude(getQData.get(0).get("LONGITUDE").toString());
                datas.setLatitude(getQData.get(0).get("LATITUDE").toString());
                //ID 20240513016 【乐沙儿3.4.0.4】指定发货机构调整为备货机构——服务  by jinzma 20240514
                if (Check.Null(getQData.get(0).get("ISSPARE").toString())) {
                    datas.setIsSpare("1");  //和王辉确认，为空全部默认成1
                } else {
                    datas.setIsSpare(getQData.get(0).get("ISSPARE").toString());
                }

                datas.setIsDinerIn(getQData.get(0).get("ISDINERIN").toString());


                for (Map<String, Object> map : getQData) {
                    DCP_N_OrderParaDetailRes.level1Elm oneLv1 = res.new level1Elm();
                    String loadDocType = map.get("LOADDOCTYPE").toString();
                    if (loadDocType == null || loadDocType.isEmpty()) {
                        continue;
                    }

                    oneLv1.setLoadDocType(map.get("LOADDOCTYPE").toString());
                    oneLv1.setIsAutoDelivery(map.get("ISAUTODELIVERY").toString());
                    oneLv1.setCityDeliveryType(map.get("CITYDELIVERYTYPE").toString());
                    oneLv1.setNationalDeliveryType(map.get("NATIONALDELIVERYTYPE").toString());
                    datas.getAutoTakeList().add(oneLv1);

                }

                String radiateType = getQData.get(0).get("RADIATETYPE").toString();//辐射配送机构类型（0全部；1指定机构；2不提供辐射）
                if (radiateType == null || radiateType.isEmpty()) {
                    radiateType = "2";
                }
                datas.setRadiateType(radiateType);

                if (radiateType.equals("1")) {
                    sql = "";
                    sql = " select A.RADIATESHIPPINGSHOP,CL.ORG_NAME RADIATESHIPPINGSHOPNAME from DCP_ORG_ORDERSET_RADIATEORG A "
                            + " left join  DCP_org_LANG CL on A.EID=CL.EID and A.RADIATESHIPPINGSHOP=CL.ORGANIZATIONNO and CL.Lang_Type='" + langType + "'"
                            + " WHERE A.EID ='" + eId + "' AND A.ORGANIZATIONNO='" + orgNo + "'";

                    List<Map<String, Object>> getData_radiateShippingOrg = this.doQueryData(sql, null);
                    if (getData_radiateShippingOrg != null && !getData_radiateShippingOrg.isEmpty()) {
                        for (Map<String, Object> mapOrg : getData_radiateShippingOrg) {
                            DCP_N_OrderParaDetailRes.level1ShippingOrg oneLv1_shippingOrg = res.new level1ShippingOrg();
                            oneLv1_shippingOrg.setRadiateShippingShop(mapOrg.get("RADIATESHIPPINGSHOP").toString());
                            oneLv1_shippingOrg.setRadiateShippingShopName(mapOrg.get("RADIATESHIPPINGSHOPNAME").toString());
                            datas.getRadiateShippingOrgList().add(oneLv1_shippingOrg);
                        }
                    }

                }


                // 返回KDSset 数据
                DCP_N_OrderParaDetailRes.level2Elm kdsSet = res.new level2Elm();
                String kds = getQData.get(0).get("KDS").toString();
                if (Check.Null(kds)) {
                    kds = "N";
                }
                kdsSet.setKds(kds);
                kdsSet.setEveryPiece(getQData.get(0).get("EVERYPIECE").toString());
                kdsSet.setWorthScore(getQData.get(0).get("WORTHSCORE").toString());
                kdsSet.setCompleteImage(getQData.get(0).get("COMPLETEIMAGE").toString());
                kdsSet.setIsQrcode(getQData.get(0).get("ISQRCODE").toString());
                datas.setDeliverShop(getQData.get(0).get("DELIVERSHOP") == null ? "" : getQData.get(0).get("DELIVERSHOP").toString());
                datas.setDeliverShopName(getQData.get(0).get("DELIVERYSHOPNAME") == null ? "" : getQData.get(0).get("DELIVERYSHOPNAME").toString());
                kdsSet.setStallList(new ArrayList<DCP_N_OrderParaDetailRes.level3Elm>());
                if (!CollectionUtils.isEmpty(kdsHeader)) {
                    for (Map<String, Object> kdsData : kdsHeader) {
                        DCP_N_OrderParaDetailRes.level3Elm level3Elm = res.new level3Elm();
                        DCP_N_OrderParaDetailRes.func func = res.new func();
                        String stallid = kdsData.get("STALLID").toString();
                        String stallname = kdsData.get("STALLNAME").toString();
                        String tagtype = kdsData.get("TAGTYPE").toString();
                        String achievements = kdsData.get("ACHIEVEMENTS").toString();
                        String employee = kdsData.get("EMPLOYEE").toString();
                        String goodsdetails = kdsData.get("GOODSDETAILS").toString();
                        String isorder = kdsData.get("ISORDER").toString();

                        level3Elm.setSubTagList(new ArrayList<DCP_N_OrderParaDetailRes.subTag>());
                        if (!CollectionUtils.isEmpty(getKdsData)) {
                            for (Map<String, Object> kdsTag : getKdsData) {
                                String stallid1 = kdsTag.get("STALLID").toString();
                                if (stallid1.equals(stallid)) {
                                    DCP_N_OrderParaDetailRes.subTag subTag = res.new subTag();
                                    String tagno = kdsTag.get("TAGNO").toString();
                                    String tagName = kdsTag.get("TAGNAME").toString();
                                    if (Check.Null(tagno)) {
                                        continue;
                                    }
                                    subTag.setTagNo(tagno);
                                    subTag.setTagName(tagName);
                                    level3Elm.getSubTagList().add(subTag);
                                }

                            }
                        }
                        if (!Check.Null(achievements) || !Check.Null(employee) || !Check.Null(goodsdetails) || !Check.Null(isorder)) {
                            func.setAchievements(achievements);
                            func.setEmployee(employee);
                            func.setGoodsDetails(goodsdetails);
                            func.setIsOrder(isorder);
                        }

                        level3Elm.setStallId(stallid);
                        level3Elm.setStallName(stallname);
                        level3Elm.setTagType(tagtype);
                        level3Elm.setFuncList(func);

                        kdsSet.getStallList().add(level3Elm);
                    }
                }
                kdsSet.setImageList(new ArrayList<DCP_N_OrderParaDetailRes.level4Elm>());
                // 获取组织订单完成小票插图
                sql = "SELECT * FROM DCP_ORG_ORDERSET_IMAGE WHERE EID = '" + eId + "' AND ORGANIZATIONNO = '" + orgNo + "'";
                List<Map<String, Object>> getOrgImages = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getOrgImages)) {
                    // 拼接返回图片路径
                    String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                    String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                    String DomainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                    for (Map<String, Object> getOrgImage : getOrgImages) {
                        DCP_N_OrderParaDetailRes.level4Elm level4Elm = res.new level4Elm();
                        String imageName = getOrgImage.get("IMAGENAME").toString();
                        level4Elm.setImageName(imageName);
                        if (DomainName.endsWith("/")) {
                            level4Elm.setImageUrl(httpStr + DomainName + "resource/image/" + imageName);
                        } else {
                            level4Elm.setImageUrl(httpStr + DomainName + "/resource/image/" + imageName);
                        }
                        kdsSet.getImageList().add(level4Elm);
                    }
                }
                datas.setKdsSet(kdsSet);

            }

            res.setDatas(datas);

            return res;


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }


    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_N_OrderParaDetailReq req) throws Exception {
        return "";
    }
}
