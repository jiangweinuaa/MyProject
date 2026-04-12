package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsMapQueryReq;
import com.dsc.spos.json.cust.req.DCP_AbnormalGoodsMapQueryReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_AbnormalGoodsMapQueryRes;
import com.dsc.spos.json.cust.res.DCP_AbnormalGoodsMapQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_AbnormalGoodsMapQuery extends SPosBasicService<DCP_AbnormalGoodsMapQueryReq, DCP_AbnormalGoodsMapQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_AbnormalGoodsMapQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelRequest request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
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
    protected TypeToken<DCP_AbnormalGoodsMapQueryReq> getRequestType() {
        return new TypeToken<DCP_AbnormalGoodsMapQueryReq>(){};
    }

    @Override
    protected DCP_AbnormalGoodsMapQueryRes getResponseType() {
        return new DCP_AbnormalGoodsMapQueryRes();
    }

    @Override
    protected DCP_AbnormalGoodsMapQueryRes processJson(DCP_AbnormalGoodsMapQueryReq req) throws Exception {

        DCP_AbnormalGoodsMapQueryRes res = this.getResponse();
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

        for (Map<String, Object> map : getQData)
        {
            level1Elm oneLv1 = res.new level1Elm();
            oneLv1.setBarcode(map.getOrDefault("GOODBARCODE","").toString());
            oneLv1.setGoodName(map.getOrDefault("GOODNAME","").toString());
            oneLv1.setLoadDocType(map.getOrDefault("LOADDOCTYPE","").toString());
            oneLv1.setAppName(map.getOrDefault("APPNAME","").toString());
            oneLv1.setChannelId(map.getOrDefault("CHANNELID","").toString());
            oneLv1.setChannelIdName(map.getOrDefault("CHANNELIDNAME","").toString());

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
    protected String getQuerySql(DCP_AbnormalGoodsMapQueryReq req) throws Exception {

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



        sqlBuffer.append(" select * from (");//括号1

        sqlBuffer.append(" select count(*) over() NUM,rownum  rn,a.* from (");//括号2

        sqlBuffer.append(" select * from  dcp_abnormalgood_mapping ");

        sqlBuffer.append(" where  to_char(MAP_DATE,'yyyymmdd')>='"+startTime+"' and to_char(MAP_DATE,'yyyymmdd')<='"+endTime+"' ");

        if (!Check.Null(loadDocType))
        {
            sqlBuffer.append(" and loaddoctype='"+loadDocType+"'");
        }
        if (!Check.Null(channelId))
        {
            sqlBuffer.append(" and channelid='"+channelId+"'");
        }
        if (!Check.Null(keyTxt))
        {
            sqlBuffer.append(" and GOODNAME like '%%"+keyTxt+"%%'");
        }
        sqlBuffer.append("  order by GOODNAME,loaddoctype");
        sqlBuffer.append(" ) a" );//对应括号2

        sqlBuffer.append(" )" );//对应括号1
        if (isPage)
        {
            sqlBuffer.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));
        }

       sql = sqlBuffer.toString();
        return sql;
    }
}
