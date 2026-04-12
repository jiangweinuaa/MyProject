package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComMediaLinkQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMediaLinkQueryRes;
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
 * 服务函数：DCP_ISVWeComMediaLinkQuery
 * 服务说明：网页链接查询
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMediaLinkQuery extends SPosBasicService<DCP_ISVWeComMediaLinkQueryReq, DCP_ISVWeComMediaLinkQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMediaLinkQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComMediaLinkQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMediaLinkQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComMediaLinkQueryRes getResponseType() {
        return new DCP_ISVWeComMediaLinkQueryRes();
    }

    @Override
    protected DCP_ISVWeComMediaLinkQueryRes processJson(DCP_ISVWeComMediaLinkQueryReq req) throws Exception {
        DCP_ISVWeComMediaLinkQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try {
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords=0;								//总笔数
            int totalPages=0;								//总页数
            if (!CollectionUtils.isEmpty(getQData)){
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                String DomainName = PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=ISHTTPS.equals("1")?"https://":"http://";

                String imageUrl = "";
                if (DomainName.endsWith("/")) {
                    imageUrl = httpStr+DomainName+"resource/image/";
                } else {
                    imageUrl = httpStr+DomainName+"/resource/image/";
                }

                for (Map<String, Object> oneData : getQData) {
                    DCP_ISVWeComMediaLinkQueryRes.Datas datas = res.new Datas();
                    String mediaId = oneData.get("MEDIAID").toString();

                    datas.setLinkId(oneData.get("LINKID").toString());
                    datas.setTitle(oneData.get("TITLE").toString());
                    datas.setDesc(oneData.get("DESCRIPTION").toString());
                    datas.setLinkUrl(oneData.get("LINKURL").toString());
                    datas.setMediaUrl(imageUrl+mediaId);
                    datas.setCreateTime(oneData.get("CREATETIME").toString());
                    datas.setLastModiTime(oneData.get("LASTMODITIME").toString());
                    datas.setMediaId(mediaId);

                    res.getDatas().add(datas);
                }
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComMediaLinkQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlbuf.append(" select * from ("
                + " select count(*) over() num,row_number() over (order by a.createtime desc) rn,"
                + " a.linkid,a.description,a.linkurl,a.title,b.mediaid,b.wecompicurl,"
                + " to_char(a.createtime ,'yyyy-MM-dd HH24:mi:ss') as createtime,"
                + " to_char(a.lastmoditime ,'yyyy-MM-dd HH24:mi:ss') as lastmoditime"
                + " from dcp_isvwecom_media_link a"
                + " left join crm_media b on a.mediaid=b.mediaid"
                + " where a.eid='"+eId+"'"
                + " ");
        if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.title like '%"+keyTxt+"%' or a.description like '%"+keyTxt+"%')");
        }
        sqlbuf.append(" ) where rn>'"+startRow+"' and rn<='"+(startRow+pageSize)+"' order by rn ");

        return sqlbuf.toString();
    }
}
