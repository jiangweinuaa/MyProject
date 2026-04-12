package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaMiniQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaMiniQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComMediaMiniQuery
 * 服务说明：小程序页面路径查询
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaMiniQuery extends SPosBasicService<DCP_ISVWeComMediaMiniQueryReq, DCP_ISVWeComMediaMiniQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaMiniQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null) {
            errMsg.append("request不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComMediaMiniQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaMiniQueryReq>() {
        };
    }

    @Override
    protected DCP_ISVWeComMediaMiniQueryRes getResponseType() {
        return new DCP_ISVWeComMediaMiniQueryRes();
    }

    @Override
    protected DCP_ISVWeComMediaMiniQueryRes processJson(DCP_ISVWeComMediaMiniQueryReq req) throws Exception {
        DCP_ISVWeComMediaMiniQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            int totalRecords = 0;                                //总笔数
            int totalPages = 0;                                //总页数

            if (!CollectionUtils.isEmpty(getQData)) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                String DomainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";

                String imageUrl = "";
                if (DomainName.endsWith("/")) {
                    imageUrl = httpStr + DomainName + "resource/image/";
                } else {
                    imageUrl = httpStr + DomainName + "/resource/image/";
                }

                for (Map<String, Object> oneData : getQData) {

                    DCP_ISVWeComMediaMiniQueryRes.Datas datas = res.new Datas();

                    datas.setMiniId(oneData.get("MINIID").toString());
                    datas.setTitle(oneData.get("TITLE").toString());
                    datas.setAppId(oneData.get("APPID").toString());
                    datas.setMiniUrl(oneData.get("MINIURL").toString());
                    datas.setMediaUrl(imageUrl + oneData.get("MEDIAID").toString());
                    datas.setMediaId(oneData.get("MEDIAID").toString());
                    datas.setCreateTime(oneData.get("CREATETIME").toString());
                    datas.setLastModiTime(oneData.get("LASTMODITIME").toString());
                    datas.setWeComPicUrl(oneData.get("WECOMPICURL").toString());



                    res.getDatas().add(datas);
                }
            }


            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;


        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComMediaMiniQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();

        //分页处理
        int pageSize = req.getPageSize();
        int startRow = (req.getPageNumber() - 1) * req.getPageSize();

        sqlbuf.append(" select * from ("
                + " select count(*) over() num,row_number() over (order by a.createtime desc) rn,"
                + " a.miniid,a.title,a.appid,a.miniurl,b.mediaid,b.wecompicurl,"
                + " to_char(a.createtime ,'yyyy-MM-dd HH24:mi:ss') as createtime,"
                + " to_char(a.lastmoditime ,'yyyy-MM-dd HH24:mi:ss') as lastmoditime"
                + " from dcp_isvwecom_media_mini a"
                + " left join crm_media b on a.mediaid=b.mediaid"
                + " where a.eid='" + eId + "' ");
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and a.title like '%" + keyTxt + "%' ");
        }
        sqlbuf.append(" ) where rn>'" + startRow + "' and rn<='" + (startRow + pageSize) + "' order by rn ");

        return sqlbuf.toString();
    }
}
