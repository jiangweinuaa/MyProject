package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CostLevelDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_CostLevelDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CostLevelDetailQuery extends SPosBasicService<DCP_CostLevelDetailQueryReq, DCP_CostLevelDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CostLevelDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostLevelDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_CostLevelDetailQueryReq>(){};
    }

    @Override
    protected DCP_CostLevelDetailQueryRes getResponseType() {
        return new DCP_CostLevelDetailQueryRes();
    }

    @Override
    protected DCP_CostLevelDetailQueryRes processJson(DCP_CostLevelDetailQueryReq req) throws Exception {
        DCP_CostLevelDetailQueryRes res = this.getResponseType();

        List<Map<String, Object>> qData = this.doQueryData(getQuerySql(req),null);

        res.setDatas(res.new Datas());
        if (CollectionUtils.isNotEmpty(qData)){

            Map<String,Boolean> distinct = new HashMap<>();
            distinct.put("CORP", true);
            distinct.put("ACCOUNTID", true);
            distinct.put("YEAR", true);
            distinct.put("PERIOD", true);

            List<Map<String, Object>> master = MapDistinct.getMap(qData, distinct);

            DCP_CostLevelDetailQueryRes.Datas oneData = res.getDatas();
            Map<String, Object> data = master.get(0);

            oneData.setCorp(data.get("CORP").toString());
            oneData.setAccountID(data.get("ACCOUNTID").toString());
            oneData.setAccount(data.get("ACCOUNT").toString());
            oneData.setStatus(data.get("STATUS").toString());
            oneData.setYear(data.get("YEAR").toString());
            oneData.setPeriod(data.get("PERIOD").toString());

            Map<String,Object> condition = new HashMap<>();
            condition.put("CORP", data.get("CORP").toString());
            condition.put("ACCOUNTID", data.get("ACCOUNTID").toString());
            condition.put("YEAR", data.get("YEAR").toString());
            condition.put("PERIOD", data.get("PERIOD").toString());

           List<Map<String, Object>> detail = MapDistinct.getWhereMap(qData,condition,true);

            oneData.setLevelList(new ArrayList<>());
            for (Map<String, Object> q : detail){

                DCP_CostLevelDetailQueryRes.LevelList oneLevel = res.new LevelList();

                oneLevel.setCostLevel(q.get("COSTLEVEL").toString());
                oneLevel.setBaseUnit(q.get("BASEUNIT").toString());
                oneLevel.setBaseUnitName(q.get("BASEUNITNAME").toString());
                oneLevel.setItem(q.get("ITEM").toString());
                oneLevel.setPluNo(q.get("PLUNO").toString());
                oneLevel.setPluName(q.get("PLU_NAME").toString());
                oneLevel.setMaterialSource(q.get("MATERIALSOURCE").toString());
                oneLevel.setPluType(q.get("PLUTYPE").toString());
                oneLevel.setIsJointProd(q.get("ISJOINTPROD").toString());

                oneLevel.setFeatureNo(q.get("FEATURENO").toString());
                oneLevel.setMaterialBom(q.get("MATERIALBOM").toString());
                oneLevel.setCostGroupingId(q.get("COSTGROUPINGID").toString());
                oneLevel.setCostGroupingId_Name(q.get("COSTGROUPINGNAME").toString());

                oneLevel.setCategory(q.get("CATEGORY").toString());
                oneLevel.setCategoryName(q.get("CATEGORY_NAME").toString());

                oneData.getLevelList().add(oneLevel);
            }

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
    protected String getQuerySql(DCP_CostLevelDetailQueryReq req) throws Exception {
        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.COSTGROUPINGID DESC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.*,gl1.PLU_NAME,ul1.UNAME BASEUNITNAME" +
                        " ,cl1.CATEGORY_NAME,cl2.CATEGORY_NAME COSTGROUPINGNAME  ")
                .append(" FROM DCP_COSTLEVELDETAIL  a ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=a.eid and a.PLUNO=gl1.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_UNIT_LANG ul1 on ul1.eid=a.eid and ul1.UNIT=a.BASEUNIT and ul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl1 on cl1.eid=a.eid and cl1.CATEGORY=a.CATEGORY and cl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_CATEGORY_LANG cl2 on cl2.eid=a.eid and cl2.CATEGORY=a.COSTGROUPINGID and cl2.LANG_TYPE='").append(req.getLangType()).append("'")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");


        if (StringUtils.isNotEmpty(req.getRequest().getYear())){
            sb.append(" AND a.YEAR='").append(req.getRequest().getYear()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())){
            sb.append(" AND a.PERIOD='").append(req.getRequest().getPeriod()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())){
            sb.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY rn ");

        return sb.toString();
    }
}
