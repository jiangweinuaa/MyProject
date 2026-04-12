package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MachKdsQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_MachKdsQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 服務函數：DCP_MachKdsQuery_Open
 * 服务说明：KDS信息查询
 * @author wangzyc
 * @since  2021/4/13
 */
public class DCP_MachKdsQuery_Open extends SPosBasicService<DCP_MachKdsQuery_OpenReq, DCP_MachKdsQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_MachKdsQuery_OpenReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_MachKdsQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_MachKdsQuery_OpenReq>(){};
    }

    @Override
    protected DCP_MachKdsQuery_OpenRes getResponseType() {
        return new DCP_MachKdsQuery_OpenRes();
    }

    @Override
    protected DCP_MachKdsQuery_OpenRes processJson(DCP_MachKdsQuery_OpenReq req) throws Exception {
        DCP_MachKdsQuery_OpenRes res = null;
        res = this.getResponse();

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);

            Map<String, Boolean> condition_EID= new HashMap<String, Boolean>(); //查詢條件
            condition_EID.put("EID", true);
            //调用过滤函数 过滤 企业ID
            List<Map<String, Object>> getEIDs= MapDistinct.getMap(datas, condition_EID);

            condition_EID.put("ORGANIZATIONNO", true);
            //调用过滤函数 过滤 企业ID 组织ID
            List<Map<String, Object>> getOrganizationnos= MapDistinct.getMap(datas, condition_EID);

            condition_EID.put("STALLID", true);
            //调用过滤函数 过滤 企业ID 组织ID 档口列表
            List<Map<String, Object>> getStallIds= MapDistinct.getMap(datas, condition_EID);

            String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
            String httpStr=ISHTTPS.equals("1")?"https://":"http://";
            String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");

            if(!CollectionUtils.isEmpty(getEIDs)){
                for (Map<String, Object> getEID : getEIDs) {
                    DCP_MachKdsQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();
                    String eid = getEID.get("EID").toString();
                    level1Elm.setPosService(PosPub.getPOS_URL(eid));
                    level1Elm.setMachShopList(new ArrayList<DCP_MachKdsQuery_OpenRes.level2Elm>());

                    if(!CollectionUtils.isEmpty(getOrganizationnos)){
                        for (Map<String, Object> getOrganizationno : getOrganizationnos) {
                            String eid1 = getOrganizationno.get("EID").toString();

                            // 过滤同一企业下的数据
                            if(eid.equals(eid1)){
                                DCP_MachKdsQuery_OpenRes.level2Elm level2Elm = res.new level2Elm();
                                String organizationno = getOrganizationno.get("ORGANIZATIONNO").toString();
                                String orgName = getOrganizationno.get("ORG_NAME").toString();
                                String kds = getOrganizationno.get("KDS").toString();
                                String completeImage = getOrganizationno.get("COMPLETEIMAGE").toString();
                                String isQrcode = getOrganizationno.get("ISQRCODE").toString();
                                if(Check.Null(kds)){
                                    kds = "N";
                                }
                                level2Elm.setKds(kds);
                                level2Elm.setMachShopId(organizationno);
                                level2Elm.setMachShopName(orgName);
                                level2Elm.setCompleteImage(completeImage);
                                level2Elm.setIsQrcode(isQrcode);
                                level2Elm.setStallList(new ArrayList<DCP_MachKdsQuery_OpenRes.level3Elm>());


                                // 查询对应组织下的图片
                                level2Elm.setImageList(new ArrayList<DCP_MachKdsQuery_OpenRes.level6Elm>());
                                sql = "SELECT * FROM DCP_ORG_ORDERSET_IMAGE WHERE EID = '"+eid+"' AND ORGANIZATIONNO = '"+organizationno+"'";
                                List<Map<String, Object>> getOrgImages = this.doQueryData(sql, null);
                                if(!CollectionUtils.isEmpty(getOrgImages)){
                                    // 拼接返回图片路径

                                    for (Map<String, Object> getOrgImage : getOrgImages) {
                                        DCP_MachKdsQuery_OpenRes.level6Elm level6Elm = res.new level6Elm();
                                        String imageName = getOrgImage.get("IMAGENAME").toString();


                                        if (DomainName.endsWith("/"))
                                        {
                                            level6Elm.setImageUrl(httpStr+DomainName+"resource/image/" +imageName);
                                        }
                                        else
                                        {
                                            level6Elm.setImageUrl(httpStr+DomainName+"/resource/image/" +imageName);
                                        }
                                        level2Elm.getImageList().add(level6Elm);
                                    }
                                }
                                if(!CollectionUtils.isEmpty(getStallIds)){
                                    for (Map<String, Object> getStallId : getStallIds) {
                                        String organizationno1 = getStallId.get("ORGANIZATIONNO").toString();

                                        // 过滤同一组织下的数据
                                        if(organizationno1.equals(organizationno)){
                                            DCP_MachKdsQuery_OpenRes.level3Elm level3Elm = res.new level3Elm();
                                            DCP_MachKdsQuery_OpenRes.level5Elm level5Elm = res.new level5Elm();

                                            String stallid = getStallId.get("STALLID").toString();

                                            if(Check.Null(stallid)){
                                                continue;
                                            }
                                            String stallname = getStallId.get("STALLNAME").toString();
                                            String tagtype = getStallId.get("TAGTYPE").toString();
                                            String achievements = getStallId.get("ACHIEVEMENTS").toString();
                                            String employee = getStallId.get("EMPLOYEE").toString();
                                            String goodsdetails = getStallId.get("GOODSDETAILS").toString();
                                            String isorder = getStallId.get("ISORDER").toString();

                                            level3Elm.setStallId(stallid);
                                            level3Elm.setStall(stallname);
                                            level3Elm.setTagType(tagtype);
                                            if(!Check.Null(achievements)||!Check.Null(employee)||!Check.Null(goodsdetails)||!Check.Null(isorder)){
                                                level5Elm.setAchievements(achievements);
                                                level5Elm.setEmployee(employee);
                                                level5Elm.setGoodsDetails(goodsdetails);
                                                level5Elm.setIsOrder(isorder);
                                            }
                                            level3Elm.setFuncList(level5Elm);
                                            level3Elm.setSubTagList(new ArrayList<DCP_MachKdsQuery_OpenRes.level4Elm>());

                                            if(!CollectionUtils.isEmpty(datas)){
                                                for (Map<String, Object> data : datas) {
                                                    String stallid1 = data.get("STALLID").toString();
                                                    // 过滤同一档口的数据
                                                    if(stallid1.equals(stallid)){
                                                        DCP_MachKdsQuery_OpenRes.level4Elm level4Elm = res.new level4Elm();
                                                        String tagno = data.get("TAGNO").toString();
                                                        if(Check.Null(tagno)){
                                                            continue;
                                                        }
                                                        level4Elm.setTagNo(tagno);
                                                        level3Elm.getSubTagList().add(level4Elm);
                                                    }
                                                }
                                            }
                                            level2Elm.getStallList().add(level3Elm);
                                        }
                                    }
                                }
                                level1Elm.getMachShopList().add(level2Elm);
                            }

                        }
                    }
                    res.setDatas(level1Elm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败:"+e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_MachKdsQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer();
        String kds = req.getRequest().getKds();

        sqlbuf.append(" SELECT A.EID, A.ORGANIZATIONNO, B.ORG_NAME, A.KDS, c.STALLID , c.STALLNAME, c.TAGTYPE, C.ISORDER," +
                " C.ACHIEVEMENTS, c.EMPLOYEE , c.GOODSDETAILS, d.TAGNO,A.COMPLETEIMAGE,A.ISQRCODE " +
                " FROM DCP_ORG_ORDERSET A " +
                " LEFT JOIN DCP_ORG_LANG B ON A.EID = b.EID AND A.ORGANIZATIONNO = b.ORGANIZATIONNO AND b.STATUS = '100' AND B.LANG_TYPE = '"+req.getLangType()+"' " +
                " LEFT JOIN DCP_ORG_ORDERSET_KDS C ON A.EID = c.EID AND a.ORGANIZATIONNO = c.ORGANIZATIONNO " +
                " LEFT JOIN DCP_ORG_ORDERSET_KDS_TAG d ON a.EID = d.EID AND a.ORGANIZATIONNO = d.ORGANIZATIONNO AND c.STALLID = d.STALLID " +
                " WHERE a.EID = '"+req.geteId()+"' ");
        if(!Check.Null(kds)){
            sqlbuf.append(" AND (A.KDS = '"+kds+"'");
            if(kds.equals("N")){
                sqlbuf.append(" OR a.KDS is null");
            }
            sqlbuf.append(")");
        }
        sqlbuf.append(" ORDER BY EID,ORGANIZATIONNO,STALLID,TAGNO");
        sql = sqlbuf.toString();
        return sql;
    }
}
