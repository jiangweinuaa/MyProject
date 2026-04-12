package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrgOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_OrgOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_OrgOpenQry extends SPosBasicService<DCP_OrgOpenQryReq, DCP_OrgOpenQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_OrgOpenQryReq req) throws Exception {
        boolean isFail = false;
        DCP_OrgOpenQryReq.levelElm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer("");
        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrgOpenQryReq> getRequestType() {
        return new TypeToken<DCP_OrgOpenQryReq>() {
        };
    }

    @Override
    protected DCP_OrgOpenQryRes getResponseType() {
        return new DCP_OrgOpenQryRes();
    }

    @Override
    protected DCP_OrgOpenQryRes processJson(DCP_OrgOpenQryReq req) throws Exception {
        DCP_OrgOpenQryRes res = null;
        res = this.getResponse();

        int totalRecords = 0;                                //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<DCP_OrgOpenQryRes.level1Elm>());
        //try {
            String sql ="";
            sql = this.getQuerySql(req);
            List<Map<String, Object>> getOrgList = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getOrgList)){

                String num = getOrgList.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);

                // 计算页数
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> orgInfo : getOrgList) {
                    DCP_OrgOpenQryRes.level1Elm level1Elm = res.new level1Elm();
                    String orgNo = orgInfo.get("ORGANIZATIONNO").toString();
                    String sname = orgInfo.get("SNAME").toString();
                    String fullname = orgInfo.get("FULLNAME").toString();
                    String status = orgInfo.get("STATUS").toString();
                    String org_from = orgInfo.get("ORG_FORM").toString();
                    String iscorp = orgInfo.get("IS_CORP").toString();
                    String corp = orgInfo.get("CORP").toString();
                    String corpName = orgInfo.get("CORPNAME").toString();
                    level1Elm.setOrgNo(orgNo);
                    level1Elm.setSName(sname);
                    level1Elm.setFullName(fullname);
                    level1Elm.setStatus(status);
                    level1Elm.setOrg_form(org_from);
                    level1Elm.setIsCorp(iscorp);
                    level1Elm.setCorp(corp);
                    level1Elm.setCorpName(corpName);
                    level1Elm.setContact(StringUtils.toString(orgInfo.get("CONTACT"),""));
                    level1Elm.setContactName(StringUtils.toString(orgInfo.get("CONTACTNAME"),""));
                    level1Elm.setAddress(StringUtils.toString(orgInfo.get("ADDRESS"),""));
                    level1Elm.setPhone(StringUtils.toString(orgInfo.get("PHONE"),""));
                    res.getDatas().add(level1Elm);
                }
            }

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        // } catch (Exception e) {
        //     res.setSuccess(false);
        //    res.setServiceStatus("200");
        //    res.setServiceDescription("服务执行失败!"+e.getMessage());
        //}
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_OrgOpenQryReq req) throws Exception {
        String langType = req.getLangType();
        String eid = req.geteId();
        DCP_OrgOpenQryReq.levelElm request = req.getRequest();
        String keyTxt = request.getKeyTxt();
        String status = request.getStatus();
        String supplier = request.getSupplier();
        String corp = request.getCorp();
        String isCorp = request.getIsCorp();
        List<String> org_form = request.getOrg_Form();
        String orgFormString="";
        if(!CollectionUtils.isEmpty(org_form)){
            for(String orgForm:org_form){
                orgFormString+="'"+orgForm+"',";
            }
            orgFormString = orgFormString.substring(0,orgFormString.length()-1);
        }


        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");


        sqlbuf.append("SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT ORGANIZATIONNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY ORGANIZATIONNO) AS rn , a.* FROM ( " +
                " SELECT a.*,b.ORG_NAME as fullname,c.name as CONTACTNAME,e.org_name as corpname " +
                " FROM DCP_ORG a " +
                " LEFT JOIN DCP_ORG_lang b ON a.EID = b.EID AND a.ORGANIZATIONNO = b.ORGANIZATIONNO AND b.LANG_TYPE = '"+langType+"'  " +
                " LEFT JOIN DCP_EMPLOYEE c ON c.EID=a.EID and c.EMPLOYEENO=a.CONTACT " +
                " ");

        if(!Check.Null(supplier)){
            sqlbuf.append(" inner join (" +
                    " select distinct a.eid, a.ORGANIZATIONNO from DCP_BIZPARTNER_ORG a " +
                    " inner join   DCP_BIZPARTNER b on a.eid=b.eid and a.BIZPARTNERNO=b.BIZPARTNERNO " +
                    " where b.BIZTYPE='1' and b.BIZPARTNERNO='"+supplier+"' and a.status='100' and b.status='100' " +
                    ") d on d.eid=a.eid and d.ORGANIZATIONNO=a.corp ");
        }
        sqlbuf.append(" left join dcp_org_lang e on e.eid=a.eid and e.organizationno=a.corp and e.lang_type='"+req.getLangType()+"' ");

        sqlbuf.append(   " WHERE a.EID = '"+eid+"'  " );


        sqlbuf.append(" AND a.status = '100' ");
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" AND (b.ORGANIZATIONNO LIKE '%%"+keyTxt+"%%' " +
                    " or b.ORG_NAME LIKE '%%"+keyTxt+"%%' " +
                    " or a.sname LIKE '%%"+keyTxt+"%%' " +
                    ")");
        }

        if(!Check.Null(corp)){
            sqlbuf.append(" AND a.corp = '"+corp+"'");
        }
        //if(!Check.Null(org_form)){
          //  sqlbuf.append(" AND a.org_form = '"+org_form+"'");
        //}

        if(orgFormString.length()>0){
            sqlbuf.append(" AND a.org_form in ("+orgFormString+")");
        }

        if(!Check.Null(isCorp)){
            sqlbuf.append(" AND a.is_Corp = '"+isCorp+"'");
        }
        sqlbuf.append("  ) a ) WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + " ORDER BY ORGANIZATIONNO DESC ");

        sql = sqlbuf.toString();
        return sql;
    }

}
