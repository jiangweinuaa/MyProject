package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_KDNShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_KDNShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_KDNShopQuery extends SPosBasicService<DCP_KDNShopQueryReq, DCP_KDNShopQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_KDNShopQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_KDNShopQueryReq> getRequestType() {
        return new TypeToken<DCP_KDNShopQueryReq>(){};
    }

    @Override
    protected DCP_KDNShopQueryRes getResponseType() {
        return new DCP_KDNShopQueryRes();
    }

    @Override
    protected DCP_KDNShopQueryRes processJson(DCP_KDNShopQueryReq req) throws Exception {
        DCP_KDNShopQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        if (req.getPageSize()==0)
        {
            req.setPageSize(20);
        }
        if (req.getPageNumber()==0)
        {
            req.setPageNumber(1);
        }
        int totalRecords = 0;
        int totalPages = 0;
        String sql = this.getQuerySql(req);
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&!getQData.isEmpty())
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : getQData)
            {
                DCP_KDNShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
                oneLv1.setShopId(oneData.get("SHOPID").toString());
                oneLv1.setShopName(oneData.get("ORG_NAME").toString());
                oneLv1.setPlatformStore(oneData.get("PLATFORMID").toString());
                oneLv1.setPlatformStatus(oneData.get("PLATFORMSTATUS").toString());
                oneLv1.setIdCard(oneData.get("IDCARD").toString());
                oneLv1.setIdCardName(oneData.get("IDCARDNAME").toString());
                oneLv1.setIdCardFront(oneData.get("IDCARDFRONT_IMAGE").toString());
                oneLv1.setIdCardBack(oneData.get("IDCARDBACK_IMAGE").toString());
                oneLv1.setLicense(oneData.get("LICENSE_IMAGE").toString());
                oneLv1.setCreditCode(oneData.get("CREDITCODE").toString());
                oneLv1.setShopPicture(oneData.get("SHOP_IMAGE").toString());
                oneLv1.setContactName(oneData.get("CONTACTNAME").toString());
                oneLv1.setCreateOpId(oneData.get("CREATEOPID").toString());
                oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
                oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                oneLv1.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
                oneLv1.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
                oneLv1.setLastModiTime(oneData.get("LASTMODITIME").toString());
                oneLv1.setUploadStatus(oneData.get("UPLOADSTATUS").toString());

                oneLv1.setProvince(oneData.get("PROVINCE").toString());
                oneLv1.setCity(oneData.get("CITY").toString());
                oneLv1.setCounty(oneData.get("COUNTY").toString());
                oneLv1.setAddress(oneData.get("ADDRESS").toString());
                oneLv1.setPhone(oneData.get("PHONE").toString());
                oneLv1.setLongitude(oneData.get("LONGITUDE").toString());
                oneLv1.setLatitude(oneData.get("LATITUDE").toString());

                res.getDatas().add(oneLv1);
            }
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalPages(totalPages);
        res.setTotalRecords(totalRecords);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_KDNShopQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;
        int endRow = startRow + pageSize;

        String eId = req.geteId();
        String keyTxt = "";
        String uploadStatus = "";
        String platformStatus = "";
        if (req.getRequest() != null) {
            if (!Check.Null(req.getRequest().getKeyTxt())) {
                keyTxt = req.getRequest().getKeyTxt();
            }
            if (!Check.Null(req.getRequest().getUploadStatus())) {
                uploadStatus = req.getRequest().getUploadStatus();
            }
            if (!Check.Null(req.getRequest().getPlatformStatus())) {
                platformStatus = req.getRequest().getPlatformStatus();
            }
        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from (");
        sqlbuf.append(" select count(*) over() num,row_number() over (order by A.SHOPID ) rn,A.*," +
                " B.PROVINCE,B.CITY,B.COUNTY,B.ADDRESS,B.PHONE,B.LONGITUDE,B.LATITUDE,BL.ORG_NAME from dcp_kdnshopmapping A ");
        sqlbuf.append(" left join dcp_org B  on A.EID=B.EID AND A.SHOPID=B.ORGANIZATIONNO ");
        sqlbuf.append(" left join dcp_org_lang BL  on A.EID=BL.EID AND A.SHOPID=BL.ORGANIZATIONNO AND BL.LANG_TYPE='zh_CN' ");
        sqlbuf.append(" where A.EID='" + eId + "' ");
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (A.SHOPID LIKE '%%" + keyTxt + "%%' OR BL.ORG_NAME LIKE '%%" + keyTxt + "%%' ) ");
        }
        if (!Check.Null(uploadStatus)) {
            sqlbuf.append(" and A.UPLOADSTATUS='" + uploadStatus + "' ");
        }
        if (!Check.Null(platformStatus)) {
            sqlbuf.append(" and A.PLATFORMSTATUS='" + platformStatus + "' ");
        }

        sqlbuf.append(" ) ");
        sqlbuf.append(" where rn>" + startRow + " and rn<=" + endRow);

        return sqlbuf.toString();
    }
}
