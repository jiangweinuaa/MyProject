package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_UnitMsgQueryReq;
import com.dsc.spos.json.cust.res.DCP_UnitMsgQueryRes;
import com.dsc.spos.json.cust.res.DCP_UnitMsgQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_UnitMsgQuery extends SPosBasicService<DCP_UnitMsgQueryReq, DCP_UnitMsgQueryRes> {

  @Override
  protected boolean isVerifyFail(DCP_UnitMsgQueryReq req) throws Exception {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected TypeToken<DCP_UnitMsgQueryReq> getRequestType() {
    // TODO Auto-generated method stub
    return new TypeToken<DCP_UnitMsgQueryReq>() {
    };
  }

  @Override
  protected DCP_UnitMsgQueryRes getResponseType() {
    // TODO Auto-generated method stub
    return new DCP_UnitMsgQueryRes();
  }

  @Override
  protected DCP_UnitMsgQueryRes processJson(DCP_UnitMsgQueryReq req) throws Exception {
    // TODO Auto-generated method stub
    String sql = null;

    DCP_UnitMsgQueryRes res = this.getResponse();
    int totalRecords = 0;                //总笔数
    int totalPages = 0;
    String cur_langType = req.getLangType();
    //查询原因码信息
    sql = this.getQuerySql(req);

    String[] conditionValues1 = {}; //查詢條件

    List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
    if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
      //总页数
      String num = getQDataDetail.get(0).get("NUM").toString();
      totalRecords = Integer.parseInt(num);

      //算總頁數
      totalPages = totalRecords / req.getPageSize();
      totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

      res.setPageNumber(req.getPageNumber());
      res.setPageSize(req.getPageSize());
      res.setTotalRecords(totalRecords);
      res.setTotalPages(totalPages);

      //单头主键字段
      Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
      condition.put("UNIT", true);
      //调用过滤函数
      List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);

      res.setDatas(new ArrayList<DCP_UnitMsgQueryRes.level1Elm>());

      for (Map<String, Object> oneData : getQHeader) {
        DCP_UnitMsgQueryRes.level1Elm oneLv1 = res.new level1Elm();
        oneLv1.setUnitName_lang(new ArrayList<DCP_UnitMsgQueryRes.level2Elm>());
        // 取出第一层
        String unit = oneData.get("UNIT").toString();
        String udLength = oneData.get("UDLENGTH").toString();
        String status = oneData.get("STATUS").toString();
        oneLv1.setUnit(unit);
        oneLv1.setUdLength(udLength);
        oneLv1.setStatus(status);
        oneLv1.setCreate_datetime(String.valueOf(oneData.get("CREATETIME")));
        oneLv1.setCreatorID(String.valueOf(oneData.get("CREATEOPID")));
        oneLv1.setCreatorDeptID(StringUtils.toString(oneData.get("CREATEDEPTID"),""));
        oneLv1.setCreatorDeptName(StringUtils.toString(oneData.get("CREATEDEPTNAME"),""));
        oneLv1.setCreatorName(String.valueOf(oneData.get("CREATEOPNAME")));
        oneLv1.setLastmodifyID(String.valueOf(oneData.get("LASTMODIOPID")));
        oneLv1.setLastmodifyName(String.valueOf(oneData.get("LASTMODIOPNAME")));
        oneLv1.setLastmodify_datetime(String.valueOf(oneData.get("LASTMODITIME")));
        oneLv1.setCreate_datetime(String.valueOf(oneData.get("CREATETIME")));
        oneLv1.setUnitType(String.valueOf(oneData.get("UNITTYPE")));
        oneLv1.setRoundType(String.valueOf(oneData.get("ROUNDTYPE")));

        for (Map<String, Object> oneData2 : getQDataDetail) {
          //过滤属于此单头的明细
          if (unit.equals(oneData2.get("UNIT")) == false)
            continue;

          DCP_UnitMsgQueryRes.level2Elm oneLv2 = res.new level2Elm();
          String unitName = oneData2.get("UNITNAME").toString();
          String langType = oneData2.get("LANGTYPE").toString();
          if (cur_langType.equals(langType)) {
            oneLv1.setUnitName(unitName);
          }
          ;
          oneLv2.setName(unitName);
          oneLv2.setLangType(langType);

          oneLv1.getUnitName_lang().add(oneLv2);
          oneLv2 = null;
        }
        res.getDatas().add(oneLv1);
        oneLv1 = null;
      }
    } else {
      res.setDatas(new ArrayList<level1Elm>());
    }
    return res;
  }

  @Override
  protected void processRow(Map<String, Object> row) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  protected String getQuerySql(DCP_UnitMsgQueryReq req) throws Exception {
    // TODO Auto-generated method stub
    String sql = null;

    int pageNumber = req.getPageNumber();
    int pageSize = req.getPageSize();

    //計算起啟位置
    int startRow = (pageNumber - 1) * pageSize;


    String langType = req.getLangType();
    String status = null;
    String unit = null;// req.getUnit();
    String uName = null;//req.getuName();
    String keyTxt = null;// req.getKeyTxt();
    if (req.getRequest() != null) {
      status = req.getRequest().getStatus();
      unit = req.getRequest().getUnit();
      uName = req.getRequest().getUnitName();
      keyTxt = req.getRequest().getKeyTxt();
    }

    String eId = req.geteId();

    StringBuffer sqlbuf = new StringBuffer("");
    sqlbuf.append("SELECT *  FROM ("
        + " SELECT COUNT(DISTINCT a.unit ) OVER() NUM ,dense_rank() over(ORDER BY a.unit) rn,  "
        + " unit , status , udLength , lUnit , langType , unitName ,EID,unitType,roundType,"
        + " CREATEOPID,CREATEOPNAME,CREATETIME,LASTMODIOPID,LASTMODIOPNAME,LASTMODITIME,CREATEDEPTID,CREATEDEPTNAME "
        + "  FROM ("
        + "  SELECT a.unit ,a.status ,a.unitType,a.roundType, a.udlength ,a.EID ,"
        + "  b.unit AS lUnit, b.lang_type AS langType , b.uname AS unitName,  "
        + " a.CREATEOPID,e1.name as CREATEOPNAME,a.LASTMODIOPID,e2.name as LASTMODIOPNAME, "
        + " a.CREATEDEPTID,c.DEPARTNAME CREATEDEPTNAME,  "
        + " TO_CHAR(a.CREATETIME, 'YYYY-MM-DD HH24:MI:SS') AS CREATETIME ,TO_CHAR(a.LASTMODITIME, 'YYYY-MM-DD HH24:MI:SS') AS LASTMODITIME"
        + " FROM dcp_unit a LEFT JOIN dcp_unit_lang b ON a.EID = b.EID AND a.unit = b.unit "
        + " LEFT JOIN DCP_DEPARTMENT_LANG c ON c.DEPARTNO=a.CREATEDEPTID AND c.LANG_TYPE=b.LANG_TYPE "
        +" LEFT join DCP_EMPLOYEE e1 on e1.eid=a.eid and e1.employeeno=a.CREATEOPID "
        +" LEFT join DCP_EMPLOYEE e2 on e2.eid=a.eid and e2.employeeno=a.LASTMODIOPID "
        + "  ) a WHERE EID = '" + eId + "'  ");

    if (status != null && status.length() > 0)
      sqlbuf.append(" AND  status = '" + status + "' ");
    if (unit != null && unit.length() > 0)
      sqlbuf.append(" AND  unit = '" + unit + "' ");
    if (uName != null && uName.length() > 0)
      sqlbuf.append(" AND  unitName like  '%%" + uName + "%%' ");

    if (keyTxt != null && keyTxt.length() > 0)
      sqlbuf.append(" AND ( unitName like  '%%" + keyTxt + "%%'  or unit like '%%" + keyTxt + "%%' ) "
          + " AND langType = '" + langType + "' ");

    sqlbuf.append("  )  a "
        + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
        + " ORDER BY unit ");

    sql = sqlbuf.toString();
    return sql;
  }


}
