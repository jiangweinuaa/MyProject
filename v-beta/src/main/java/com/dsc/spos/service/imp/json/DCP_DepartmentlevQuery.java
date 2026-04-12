package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DepartmentlevQueryReq;
import com.dsc.spos.json.cust.res.DCP_DepartmentlevQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_DepartmentlevQuery extends SPosBasicService<DCP_DepartmentlevQueryReq, DCP_DepartmentlevQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_DepartmentlevQueryReq req) throws Exception {
        StringBuilder errMsg = new StringBuilder();
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_DepartmentlevQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DepartmentlevQueryReq>() {
        };
    }

    @Override
    protected DCP_DepartmentlevQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DepartmentlevQueryRes();
    }

    private List<Map<String, Object>> alldepart;
    private List<Map<String, Object>> alldepartlang;

    @Override
    protected DCP_DepartmentlevQueryRes processJson(DCP_DepartmentlevQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        //开始查询所有部门的列表
        DCP_DepartmentlevQueryRes res = new DCP_DepartmentlevQueryRes();
        //判断keyTxt，如果是TEXT的要显示TEXT的一个上级，以及TEXT的所有上级
        int totalRecords;                //总笔数
        int totalPages;

        String sql = this.getQuerySql(req);
        alldepart = this.doQueryData(sql, null);

        String sqllang = "select * from DCP_DEPARTMENT_LANG where  EID='" + req.geteId() + "'";
        alldepartlang = this.doQueryData(sqllang, null);

        List<Map<String, Object>> curlistdepart = getlistdepart(alldepart, "");
        if (curlistdepart != null && !curlistdepart.isEmpty()) {
            String num = curlistdepart.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setDatas(new ArrayList<>());
            for (Map<String, Object> mapdeprt : curlistdepart) {
                DCP_DepartmentlevQueryRes.level1Elm lv1 = res.new level1Elm();
                lv1.setDepartNo(mapdeprt.get("DEPARTNO").toString());
                lv1.setDepartName(mapdeprt.get("DEPARTNAME").toString());
                lv1.setMemo(StringUtils.toString(mapdeprt.get("MEMO"), ""));
                lv1.setStatus(mapdeprt.get("STATUS").toString());
                lv1.setUpDepartNo(mapdeprt.get("UPDEPARTNO").toString());
                lv1.setUpDepartName(mapdeprt.get("UPDEPARTNAME").toString());
                lv1.setOrgNo(mapdeprt.get("ORGANIZATIONNO").toString());
                lv1.setOrgName(StringUtils.toString(mapdeprt.get("ORG_NAME"), ""));
                lv1.setManager(mapdeprt.get("MANAGER").toString());
                lv1.setManagerName(StringUtils.toString(mapdeprt.get("MANAGERNAME"), ""));
                lv1.setCreatorID(StringUtils.toString(mapdeprt.get("CREATEBY"), ""));
                lv1.setCreatorName(StringUtils.toString(mapdeprt.get("CREATEOPNAME"), ""));
                lv1.setCreatorDeptID(StringUtils.toString(mapdeprt.get("CREATEDEPTID"), ""));
                lv1.setCreatorDeptName(StringUtils.toString(mapdeprt.get("CREATEDEPTNAME"), ""));
                lv1.setCreate_datetime(StringUtils.toString(mapdeprt.get("CREATE_DTIME"), ""));
                lv1.setLastmodifyID(StringUtils.toString(mapdeprt.get("MODIFYBY"), ""));
                lv1.setLastmodifyName(StringUtils.toString(mapdeprt.get("LASTMODIOPNAME"), ""));
                lv1.setLastmodify_datetime(StringUtils.toString(mapdeprt.get("MODIFY_DTIME"), ""));
                lv1.setResponsibilityCenterType(StringUtils.toString(mapdeprt.get("RESPONSIBILITYCENTERTYPE"), ""));
                lv1.setRespCenter(StringUtils.toString(mapdeprt.get("RESPCENTER"), ""));
                lv1.setIsProductGroup(StringUtils.toString(mapdeprt.get("ISPRODUCTGROUP"), ""));

                lv1.setBelCorp(StringUtils.toString(mapdeprt.get("CORP"), ""));
                lv1.setBelCorpName(StringUtils.toString(mapdeprt.get("CORP_NAME"), ""));
                lv1.setStaffCnt(StringUtils.toInteger(mapdeprt.get("staffCnt")));

                //多语言设置
                List<Map<String, Object>> curlistdepartlang = getlistdepartlang(alldepartlang, mapdeprt.get("DEPARTNO").toString());
                if (curlistdepartlang != null && !curlistdepartlang.isEmpty()) {
                    lv1.setDepart_list(new ArrayList<DCP_DepartmentlevQueryRes.DepartLang>());
                    lv1.setDeptFname_list(new ArrayList<DCP_DepartmentlevQueryRes.DepartFLang>());

                    for (Map<String, Object> mapdeprtlang : curlistdepartlang) {
                        DCP_DepartmentlevQueryRes.DepartLang lang = new DCP_DepartmentlevQueryRes.DepartLang();
                        DCP_DepartmentlevQueryRes.DepartFLang fLang = new DCP_DepartmentlevQueryRes.DepartFLang();

                        lang.setLangType(mapdeprtlang.get("LANG_TYPE").toString());
                        lang.setName(mapdeprtlang.get("DEPARTNAME").toString());

                        fLang.setLangType(mapdeprtlang.get("LANG_TYPE").toString());
                        fLang.setName(mapdeprtlang.get("FULLNAME").toString());

                        lv1.getDepart_list().add(lang);
                        lv1.getDeptFname_list().add(fLang);
                    }
                }
                if (mapdeprt.get("DEPARTNO").toString().equals(mapdeprt.get("UPDEPARTNO").toString())) {
                    continue;
                }

                setchildren(lv1);
                res.getDatas().add(lv1);

            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    private void setchildren(DCP_DepartmentlevQueryRes.level1Elm curlv1) {

        List<Map<String, Object>> curlistdepart = getlistdepart(alldepart, curlv1.getDepartNo());
        if (curlistdepart != null && !curlistdepart.isEmpty()) {
            curlv1.setChildren(new ArrayList<>());
            for (Map<String, Object> mapdeprt : curlistdepart) {
                DCP_DepartmentlevQueryRes.level1Elm lv1 = new DCP_DepartmentlevQueryRes().new level1Elm();

                lv1.setDepartNo(mapdeprt.get("DEPARTNO").toString());
                lv1.setDepartName(mapdeprt.get("DEPARTNAME").toString());
                lv1.setMemo(StringUtils.toString(mapdeprt.get("MEMO"), ""));
                lv1.setStatus(mapdeprt.get("STATUS").toString());
                lv1.setUpDepartNo(mapdeprt.get("UPDEPARTNO").toString());
                lv1.setUpDepartName(mapdeprt.get("UPDEPARTNAME").toString());
                lv1.setOrgNo(mapdeprt.get("ORGANIZATIONNO").toString());
                lv1.setOrgName(StringUtils.toString(mapdeprt.get("ORG_NAME"), ""));
                lv1.setManager(mapdeprt.get("MANAGER").toString());
                lv1.setManagerName(StringUtils.toString(mapdeprt.get("MANAGERNAME"), ""));
                lv1.setCreatorID(StringUtils.toString(mapdeprt.get("CREATEBY"), ""));
                lv1.setCreatorName(StringUtils.toString(mapdeprt.get("CREATEOPNAME"), ""));
                lv1.setCreatorDeptID(StringUtils.toString(mapdeprt.get("CREATEDEPTID"), ""));
                lv1.setCreatorDeptName(StringUtils.toString(mapdeprt.get("CREATEDEPTNAME"), ""));
                lv1.setCreate_datetime(StringUtils.toString(mapdeprt.get("CREATE_DTIME"), ""));
                lv1.setLastmodifyID(StringUtils.toString(mapdeprt.get("MODIFYBY"), ""));
                lv1.setLastmodifyName(StringUtils.toString(mapdeprt.get("LASTMODIOPNAME"), ""));
                lv1.setLastmodify_datetime(StringUtils.toString(mapdeprt.get("MODIFY_DTIME"), ""));
                lv1.setResponsibilityCenterType(StringUtils.toString(mapdeprt.get("RESPONSIBILITYCENTERTYPE"), ""));
                lv1.setRespCenter(StringUtils.toString(mapdeprt.get("RESPCENTER"), ""));
                lv1.setBelCorp(StringUtils.toString(mapdeprt.get("CORP"), ""));
                lv1.setBelCorpName(StringUtils.toString(mapdeprt.get("CORP_NAME"), ""));
                lv1.setStaffCnt(StringUtils.toInteger(mapdeprt.get("staffCnt")));
                lv1.setIsProductGroup(StringUtils.toString(mapdeprt.get("ISPRODUCTGROUP"), ""));

                //多语言设置
                List<Map<String, Object>> curlistdepartlang = getlistdepartlang(alldepartlang, mapdeprt.get("DEPARTNO").toString());
                if (curlistdepartlang != null && !curlistdepartlang.isEmpty()) {
                    lv1.setDepart_list(new ArrayList<DCP_DepartmentlevQueryRes.DepartLang>());
                    lv1.setDeptFname_list(new ArrayList<DCP_DepartmentlevQueryRes.DepartFLang>());

                    for (Map<String, Object> mapdeprtlang : curlistdepartlang) {
                        DCP_DepartmentlevQueryRes.DepartLang lang = new DCP_DepartmentlevQueryRes.DepartLang();
                        DCP_DepartmentlevQueryRes.DepartFLang fLang = new DCP_DepartmentlevQueryRes.DepartFLang();

                        lang.setLangType(mapdeprtlang.get("LANG_TYPE").toString());
                        lang.setName(mapdeprtlang.get("DEPARTNAME").toString());

                        fLang.setLangType(mapdeprtlang.get("LANG_TYPE").toString());
                        fLang.setName(mapdeprtlang.get("FULLNAME").toString());

                        lv1.getDepart_list().add(lang);
                        lv1.getDeptFname_list().add(fLang);
                    }
                }
                if (mapdeprt.get("DEPARTNO").toString().equals(mapdeprt.get("UPDEPARTNO").toString())) {
                    continue;
                }

                setchildren(lv1);
                curlv1.getChildren().add(lv1);

            }
        }

    }

    private List<Map<String, Object>> getlistdepartlang(List<Map<String, Object>> alldepart, String departno) {
        List<Map<String, Object>> curlistdepart = new ArrayList<Map<String, Object>>();
        if (alldepart != null && !alldepart.isEmpty()) {
            for (Map<String, Object> map : alldepart) {
                if (map.get("DEPARTNO").equals(departno)) {
                    curlistdepart.add(map);
                }
            }
        }
        return curlistdepart;

    }


    private List<Map<String, Object>> getlistdepart(List<Map<String, Object>> alldepart, String departno) {
        List<Map<String, Object>> curlistdepart = new ArrayList<>();
        if (alldepart != null && !alldepart.isEmpty()) {
            for (Map<String, Object> map : alldepart) {
                if (map.get("UPDEPARTNO").equals(departno)) {
                    curlistdepart.add(map);
                }
            }
        }
        return curlistdepart;

    }


    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_DepartmentlevQueryReq req) throws Exception {
        String sql = null;

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;


        String langType = req.getLangType();
        String status = null;

        String keyTxt = null;// req.getKeyTxt();
        if (req.getRequest() != null) {
            status = req.getRequest().getStatus();
            keyTxt = req.getRequest().getKeytxt();
        }

        String eId = req.geteId();


        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT a.*," +
                "(SELECT COUNT(1) staffCnt FROM DCP_DEPARTMENT WHERE status=100 ) as staffCnt FROM( ").append(
                "  SELECT COUNT(DISTINCT a.DEPARTNO ) OVER() NUM ,dense_rank() over(ORDER BY a.DEPARTNO) rn,  " +
                        "   a.EID,a.DEPARTNO,a.UPDEPARTNO," +
                        "   a.MANAGER,a.MEMO,a.STATUS,a.CREATEBY,a.CREATE_DTIME,a.MODIFYBY,a.MODIFY_DTIME, " +
                        "   a.TRAN_TIME,a.PROCESS_STATUS,a.CREATEDEPTID, " +
                        "   b.DEPARTNAME as DEPARTNAME,c.DEPARTNAME as UPDEPARTNAME, " +
                        "   e.OPNAME AS CREATEOPNAME,f.OPNAME AS LASTMODIOPNAME, " +
                        "   g.DEPARTNAME AS CREATEDEPTNAME,a.ORGANIZATIONNO,h.ORG_NAME,i.CORP," +
                        "   ol2.ORG_NAME CORP_NAME,a.RESPONSIBILITYCENTERTYPE,a.RESPCENTER " +
                        "   ,ee0.NAME as MANAGERNAME,a.ISPRODUCTGROUP  " +
                        "    from DCP_DEPARTMENT a " +
                        "    left join DCP_DEPARTMENT_LANG b on a.EID=b.EID and a.DEPARTNO=b.DEPARTNO " +
                        "    left join DCP_DEPARTMENT_LANG c on a.EID=c.EID and a.UPDEPARTNO=c.DEPARTNO and b.LANG_TYPE=c.LANG_TYPE  " +
                        "    left join DCP_EMPLOYEE ee0 on a.EID=ee0.eid and ee0.EMPLOYEENO=MANAGER  " +
                        "    LEFT JOIN PLATFORM_STAFFS e ON e.EID=a.EID and e.OPNO=a.CREATEBY  " +
                        "    LEFT JOIN PLATFORM_STAFFS f ON f.EID=a.EID and f.OPNO=a.MODIFYBY  " +
                        "    LEFT JOIN DCP_DEPARTMENT_LANG g on g.EID=b.EID AND g.DEPARTNO=a.CREATEDEPTID AND g.LANG_TYPE=b.LANG_TYPE " +
                        "    LEFT JOIN DCP_ORG i ON a.eid=i.eid and i.ORGANIZATIONNO=a.ORGANIZATIONNO  " +
                        "    LEFT JOIN DCP_ORG_LANG h on a.eid=h.eid and h.ORGANIZATIONNO=a.ORGANIZATIONNO AND h.LANG_TYPE='" + req.getLangType() + "'" +
                        "    LEFT JOIN DCP_ORG_LANG ol2 on i.eid=ol2.eid and ol2.ORGANIZATIONNO=i.CORP AND ol2.LANG_TYPE='" + req.getLangType() + "'" +
                        "    LEFT JOIN DCP_BIZPARTNER k on i.EID=k.EID and i.CORP=k.BIZPARTNERNO  "

        ).append(" WHERE A.EID='" + req.geteId() + "' and b.LANG_TYPE='" + req.getLangType() + "'");

        if (StringUtils.isNotEmpty(keyTxt)) {
            builder.append(" and (A.DEPARTNO like '%").append(keyTxt).append("%' or B.DEPARTNAME like '%").append(keyTxt).append("%' )  ");
        }
        if (StringUtils.isNotEmpty(keyTxt)) {
            builder.append(" AND (a.status= ").append(status).append(")");
        }
        if(Check.NotNull(req.getRequest().getCorp())){
            builder.append(" and i.corp='"+req.getRequest().getCorp()+"' ");
        }

        builder.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY DEPARTNO ");

        return builder.toString();
    }

}
