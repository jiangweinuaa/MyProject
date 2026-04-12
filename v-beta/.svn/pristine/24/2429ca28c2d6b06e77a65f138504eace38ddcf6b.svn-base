package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DemandToProdQueryReq;
import com.dsc.spos.json.cust.res.DCP_DemandToProdQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_DemandToProdQuery extends SPosBasicService<DCP_DemandToProdQueryReq, DCP_DemandToProdQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DemandToProdQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DemandToProdQueryReq> getRequestType() {
        return new TypeToken<DCP_DemandToProdQueryReq>() {
        };
    }

    @Override
    protected DCP_DemandToProdQueryRes getResponseType() {
        return new DCP_DemandToProdQueryRes();
    }

    @Override
    protected DCP_DemandToProdQueryRes processJson(DCP_DemandToProdQueryReq req) throws Exception {

        DCP_DemandToProdQueryRes res = this.getResponseType();

        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            for (Map<String, Object> data : getData) {
                DCP_DemandToProdQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setRDate(data.get("RDATE").toString());
                oneData.setOrderType(data.get("ORDERTYPE").toString());
                oneData.setOrderNo(data.get("ORDERNO").toString());
                oneData.setObjectType(data.get("OBJECTTYPE").toString());
                oneData.setObjectId(data.get("OBJECTID").toString());
                oneData.setObjectName(data.get("OBJECTNAME").toString());

            }
        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_DemandToProdQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.RDATE ,a.ORDERNO ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.* ")
                .append(" ,CASE WHEN OBJECTTYPE='1' THEN d.ORG_NAME ELSE e.SNAME END AS OBJECTNAME ")
                .append(" FROM (")
                .append("   SELECT DISTINCT a.EID, a.RDATE,a.ORDERTYPE,a.ORDERNO,a.OBJECTTYPE,a.OBJECTID  ")
                .append("   FROM DCP_DEMAND a ")
                .append("   LEFT JOIN DCP_GOODS b on a.eid=b.eid and a.PLUNO=b.PLUNO  ")
        ;

//        1.料件补给方式=“自制”，DCP_GOODS.SOURCETYPE='1'
//        2.料件供货对象=“当前组织”，DCP_DEMAND.DELIVERORGNO=当前请求组织
//        3.结束码为未结束，且状态码为已核准，DCP_DEMAND.CLOSESTATUS=0 AND DCP_DEMAND.STATUS=1
//        4.入参isDeductStock为N，过滤生产需求量PQTY-已排产量PRODUCEQTY>0 的数据
//        入参isDeductStock为Y，过滤生产需求量PQTY-已排产量PRODUCEQTY>库存数-锁定数-预留数 的数据（单位转换一致后做减法）
//        查询结果去重获取需求对象，需求类型，需求单号
        sb.append(" WHERE a.EID='").append(req.geteId()).append("' ");
        sb.append(" AND b.SOURCETYPE='1' ");
        sb.append(" AND a.DELIVERYORGNO='").append(req.getOrganizationNO()).append("' ");
        sb.append(" AND a.CLOSESTATUS='0' and a.STATUS='1' ");
        sb.append(" AND NOT EXISTS( " +
                " SELECT EID,ORGANIZATIONNO,BILLNO FROM DCP_PRODSCHEDULE_SOURCE g WHERE g.eid=a.eid and g.ORDERNO=a.ORDERNO and a.organizationno=g.organizationno "+
                " ) ");

        if (StringUtils.isNotEmpty(req.getRequest().getIsDeductStock())) {
            if ("N".equals(req.getRequest().getIsDeductStock())) {
                sb.append(" AND a.PQTY-a.PRODUCEQTY >0 ");
            } else {

            }
        }

        if (CollectionUtils.isNotEmpty(req.getRequest().getOrderType())) {
            sb.append(" AND ( 1=2");
            for (String orderType : req.getRequest().getOrderType()) {
                sb.append(" OR a.ORDERTYPE='").append(orderType).append("'");
            }
            sb.append(")");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
            sb.append(" AND a.RDATE >= '").append(DateFormatUtils.getPlainDate(req.getRequest().getBeginDate())).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
            sb.append(" AND a.RDATE <= '").append(DateFormatUtils.getPlainDate(req.getRequest().getEndDate())).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
            sb.append(" AND ( a.PLUNO LIKE '%%").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(" OR a.OBJECTID LIKE '%%").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(")");
        }
        sb.append(")a");
        sb.append(" left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.objectid and a.OBJECTTYPE='1' and d.lang_type='").append(req.getLangType()).append("' ");
        sb.append(" left join dcp_bizpartner e on e.eid=a.eid and e.BIZPARTNERNO=a.objectid and a.OBJECTTYPE='2' ");

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
