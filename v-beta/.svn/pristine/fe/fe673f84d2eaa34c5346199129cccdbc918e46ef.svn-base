package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FaPiaoPlatformQueryReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoPlatformQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_FaPiaoPlatformQuery
 * 服务说明：发票平台查询
 *
 * @author wangzyc
 * @since 2021-1-27
 */
public class DCP_FaPiaoPlatformQuery extends SPosBasicService<DCP_FaPiaoPlatformQueryReq, DCP_FaPiaoPlatformQueryRes> {


    @Override
    protected boolean isVerifyFail(DCP_FaPiaoPlatformQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoPlatformQueryReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoPlatformQueryReq>() {
        };
    }

    @Override
    protected DCP_FaPiaoPlatformQueryRes getResponseType() {
        return new DCP_FaPiaoPlatformQueryRes();
    }

    @Override
    protected DCP_FaPiaoPlatformQueryRes processJson(DCP_FaPiaoPlatformQueryReq req) throws Exception {
        DCP_FaPiaoPlatformQueryRes res = this.getResponseType();
        String sql = "";
        sql = this.getQuerySql(req);
        res.setDatas(new ArrayList<DCP_FaPiaoPlatformQueryRes.level1ELm>());

        try {
            List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(queryDatas)) {
                // 过滤
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                condition.put("PLATFORMTYPE", true);
                // 调用过滤函数
                List<Map<String, Object>> getHeader = MapDistinct.getMap(queryDatas, condition);

                for (Map<String, Object> map : getHeader) {

                    DCP_FaPiaoPlatformQueryRes.level1ELm level1ELm = new DCP_FaPiaoPlatformQueryRes.level1ELm().toBuilder()
                            .platformType(map.get("PLATFORMTYPE").toString())
                            .platformName(map.get("PLATFORMNAME").toString())
                            .sortId(map.get("SORTID").toString())
                            .params(new ArrayList<DCP_FaPiaoPlatformQueryRes.level2ELm>()).build();

                    // params
                    for (Map<String, Object> map2 : queryDatas) {
                        // 过滤此单头的明细
                        if (!map.get("PLATFORMTYPE").toString().equals(map2.get("PLATFORMTYPE").toString())) {
                            continue;
                        }
                        DCP_FaPiaoPlatformQueryRes.level2ELm level2ELm = new DCP_FaPiaoPlatformQueryRes.level2ELm().toBuilder()
                                .param(map2.get("PARAM").toString())
                                .name(map2.get("NAME").toString())
                                .value("")
                                .memo(map2.get("MEMO").toString())
                                .sortId(map2.get("PSORTID").toString())
                                .groupId(map2.get("GROUPID").toString()).build();
                        level1ELm.getParams().add(level2ELm);
                    }

                    res.getDatas().add(level1ELm);
                }
            }
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败：" + e.getMessage());
        }
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_FaPiaoPlatformQueryReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.platformType, a.PLATFORMNAME, a.SORTID, c.PARAM, c.NAME , c.MEMO, c.SORTID AS pSortid, c.GROUPID " +
                " FROM DCP_FAPIAO_PLATFORM a " +
                " LEFT JOIN DCP_FAPIAO_PLATFORM_PARAM b ON a.PLATFORMTYPE = b.PLATFORMTYPE " +
                " LEFT JOIN DCP_FAPIAO_PARAM c ON b.PARAM = c.PARAM " +
                " ORDER BY a.SORTID, c.SORTID, c.GROUPID ");
        sql = sqlbuf.toString();
        return sql;
    }
}
