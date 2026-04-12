package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FaPiaoTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_FaPiaoTemplateQuery
 * 服务说明：发票模板查询
 *
 * @author wangzyc
 * @since 2021-1-27
 */
public class DCP_FaPiaoTemplateQuery extends SPosBasicService<DCP_FaPiaoTemplateQueryReq, DCP_FaPiaoTemplateQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_FaPiaoTemplateQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoTemplateQueryReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoTemplateQueryReq>() {
        };
    }

    @Override
    protected DCP_FaPiaoTemplateQueryRes getResponseType() {
        return new DCP_FaPiaoTemplateQueryRes();
    }

    @Override
    protected DCP_FaPiaoTemplateQueryRes processJson(DCP_FaPiaoTemplateQueryReq req) throws Exception {
        DCP_FaPiaoTemplateQueryRes res = null;
        res = this.getResponse();

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> datas = this.doQueryData(sql, null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int totalRecords = 0;                                //总笔数
        int totalPages = 0;                                    //总页数

        res.setDatas(new ArrayList<DCP_FaPiaoTemplateQueryRes.level1Elm>());
        if (!CollectionUtils.isEmpty(datas)) {
            String num = datas.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            // 计算页数
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> data : datas) {
                DCP_FaPiaoTemplateQueryRes.level1Elm level1Elm = new DCP_FaPiaoTemplateQueryRes.level1Elm().toBuilder()
                        .templateId(data.get("TEMPLATEID").toString())
                        .templateName(data.get("TEMPLATENAME").toString())
                        .platformType(data.get("PLATFORMTYPE").toString())
                        .platformName(data.get("PLATFORMNAME").toString())
                        .templateType(data.get("TEMPLATETYPE").toString())
                        .memo(data.get("MEMO").toString())
                        .status(data.get("STATUS").toString())
                        .createTime(data.get("CREATETIME").toString())
                        .build();
                res.getDatas().add(level1Elm);
            }

        }

        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_FaPiaoTemplateQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        DCP_FaPiaoTemplateQueryReq.level1Elm request = req.getRequest();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;
        sqlbuf.append("SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT TEMPLATEID) OVER () AS num, DENSE_RANK() OVER (ORDER BY TEMPLATEID) AS rn , a.* FROM ( " +
                " SELECT DISTINCT a.TEMPLATEID, a.TEMPLATENAME, a.PLATFORMTYPE, b.PLATFORMNAME, a.TEMPLATETYPE , a.MEMO, a.STATUS, a.CREATETIME " +
                " FROM DCP_FAPIAO_TEMPLATE a" +
                " LEFT JOIN DCP_FAPIAO_PLATFORM b ON a.PLATFORMTYPE = b.PLATFORMTYPE " +
                " LEFT JOIN DCP_FAPIAO_TEMPLATE_SHOP c ON a.EID = c.EID AND a.TEMPLATEID = c.TEMPLATEID" +
                " WHERE a.EID = '" + req.geteId() + "'");

        if (request != null) {
            if (!Check.Null(request.getStatus())) {
                sqlbuf.append(" and a.STATUS = '" + request.getStatus() + "'");
            }

            if (!Check.Null(request.getKeyTxt())) {
                sqlbuf.append(" and (a.TEMPLATEID LIKE '%%" + request.getKeyTxt() + "%%' OR a.TEMPLATENAME LIKE '%%" + request.getKeyTxt() + "%%')");
            }

            if (!Check.Null(request.getShopId())) {
                sqlbuf.append(" and c.SHOPID = '" + request.getShopId() + "'");
            }
        }

        sqlbuf.append("  ) a ) WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + " ORDER BY CREATETIME DESC ");

        return sqlbuf.toString();
    }
}
