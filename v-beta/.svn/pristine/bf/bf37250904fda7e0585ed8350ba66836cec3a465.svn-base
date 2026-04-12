package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AbnormalOrderGoodsQueryReq;
import com.dsc.spos.json.cust.req.DCP_AbnormalOrderGoodsQueryReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_AbnormalOrderGoodsQueryRes;
import com.dsc.spos.json.cust.res.DCP_AbnormalOrderGoodsQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_AbnormalOrderGoodsQuery extends SPosBasicService<DCP_AbnormalOrderGoodsQueryReq, DCP_AbnormalOrderGoodsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AbnormalOrderGoodsQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelRequest request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (Check.Null(req.getRequest().geteId())) {
            errMsg.append("企业ID不能为空值 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getStartTime())) {
            errMsg.append("开始日期不能为空值 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getEndTime())) {
            errMsg.append("结束日期不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AbnormalOrderGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_AbnormalOrderGoodsQueryReq>(){};
    }

    @Override
    protected DCP_AbnormalOrderGoodsQueryRes getResponseType() {
        return new DCP_AbnormalOrderGoodsQueryRes();
    }

    @Override
    protected DCP_AbnormalOrderGoodsQueryRes processJson(DCP_AbnormalOrderGoodsQueryReq req) throws Exception {

        DCP_AbnormalOrderGoodsQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<level1Elm>());
        String eId = req.geteId();
        String abnormalType = "goodsNotFound";//默认类型
        String keyTxt = req.getRequest().getKeyTxt();
        String startTime = req.getRequest().getStartTime();
        String endTime = req.getRequest().getEndTime();
        String loadDocType = req.getRequest().getLoadDocType();
        String channelId = req.getRequest().getChannelId();

        int totalRecords = 0; //总笔数
        int totalPages = 0;
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        res.setPageNumber(pageNumber);
        res.setPageSize(pageSize);
        boolean isPage = false;//是否分页
        if (pageSize>0&&pageNumber>0)
        {
            isPage = true;
        }

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData==null||getQData.isEmpty())
        {
            return res;
        }

        if (isPage)
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
        }
        String abnormalCause = "未维护商品或维护商品错误";
        for (Map<String, Object> map : getQData)
        {
            DCP_AbnormalOrderGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
            oneLv1.setAbnormalCause(abnormalCause);
            oneLv1.setGoodName(map.getOrDefault("PLUNAME","").toString());
            oneLv1.setLoadDocType(map.getOrDefault("LOADDOCTYPE","").toString());
            oneLv1.setAppName(map.getOrDefault("APPNAME","").toString());
            oneLv1.setChannelId(map.getOrDefault("CHANNELID","").toString());
            oneLv1.setChannelIdName(map.getOrDefault("CHANNELNAME","").toString());

            res.getDatas().add(oneLv1);
        }

        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        res.setPageNumber(pageNumber);
        res.setPageSize(pageSize);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AbnormalOrderGoodsQueryReq req) throws Exception {

        String sql ="";
        StringBuffer sqlBuffer = new StringBuffer("");
        String eId = req.geteId();
        String abnormalType = "goodsNotFound";//默认类型
        String keyTxt = req.getRequest().getKeyTxt();
        String startTime = req.getRequest().getStartTime();
        String endTime = req.getRequest().getEndTime();
        String loadDocType = req.getRequest().getLoadDocType();
        String channelId = req.getRequest().getChannelId();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow= 0;
        boolean isPage = false;//是否分页
        if (pageSize>0&&pageNumber>0)
        {
            isPage = true;
            startRow=(pageNumber-1) * pageSize;
        }

        sqlBuffer.append(" with  p as (");//括号0

        sqlBuffer.append(" select * from (");//括号1

        sqlBuffer.append(" select count(*) over() NUM,rownum  rn,a.* from (");//括号2
        sqlBuffer.append(" select distinct pluname,loaddoctype,channelid from (");//括号3

        sqlBuffer.append(" select a.*,c.pluname,b.loaddoctype,b.channelid from dcp_order_abnormalinfo_detail a "
                        + " inner join dcp_order b on a.eid=b.eid and a.orderno=b.orderno "
                        + " inner join dcp_order_detail c on a.eid=c.eid and a.orderno=c.orderno and a.oitem=c.item "
                        + " inner join dcp_order_abnormalinfo d on  a.eid=d.eid and a.orderno=d.orderno and a.abnormaltype=d.abnormaltype ");
        sqlBuffer.append(" where a.status='0' and a.abnormaltype='"+abnormalType+"' and b.exceptionstatus='Y' and b.shop<>' ' and b.shop is not null "
                        + " and b.bdate>='"+startTime+"' and b.bdate<='"+endTime+"' ");

        if (!Check.Null(loadDocType))
        {
            sqlBuffer.append(" and b.loaddoctype='"+loadDocType+"'");
        }
        if (!Check.Null(channelId))
        {
            sqlBuffer.append(" and b.channelid='"+channelId+"'");
        }
        if (!Check.Null(keyTxt))
        {
            sqlBuffer.append(" and c.pluname like '%%"+keyTxt+"%%'");
        }
        sqlBuffer.append(" ) order by pluname,loaddoctype");//括号3

        sqlBuffer.append(" ) a");//对应括号2

        sqlBuffer.append(" )" );//对应括号1
        if (isPage)
        {
            sqlBuffer.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));
        }

        sqlBuffer.append(" ) ");//对应括号0

        sqlBuffer.append(" select p.*,a.appname,b.channelname from p left join platform_app a on p.loaddoctype=a.appno ");
        sqlBuffer.append(" left join crm_channel b on p.channelid=b.channelid and b.eid='"+eId+"'");
        sql = sqlBuffer.toString();
        return sql;
    }
}
