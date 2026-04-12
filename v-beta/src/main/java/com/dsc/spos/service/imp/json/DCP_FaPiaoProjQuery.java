package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FaPiaoProjQueryReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoProjQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_FaPiaoProjQuery extends SPosBasicService<DCP_FaPiaoProjQueryReq, DCP_FaPiaoProjQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_FaPiaoProjQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoProjQueryReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoProjQueryReq>(){};
    }

    @Override
    protected DCP_FaPiaoProjQueryRes getResponseType() {
        return new DCP_FaPiaoProjQueryRes();
    }

    @Override
    protected DCP_FaPiaoProjQueryRes processJson(DCP_FaPiaoProjQueryReq req) throws Exception {

        DCP_FaPiaoProjQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<DCP_FaPiaoProjQueryRes.level1Elm>());
        int totalRecords=0;								//总笔数
        int totalPages=0;
        if (req.getPageNumber()==0)
        {
            req.setPageNumber(1);
        }
        if (req.getPageSize()==0)
        {
            req.setPageSize(10);
        }
        String langType_cur = req.getLangType();
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (!CollectionUtils.isEmpty(getQData))
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("PROJECTID", true);

            List<Map<String, Object>> catDatas= MapDistinct.getMap(getQData, condition);
            for (Map<String, Object> oneData : catDatas)
            {
                DCP_FaPiaoProjQueryRes.level1Elm oneLv1 = res.new level1Elm();
                oneLv1.setProjectName_lang(new ArrayList<DCP_FaPiaoProjQueryRes.projectName>());
                String projectId = oneData.get("PROJECTID").toString();
                String projectName = oneData.get("PROJECTNAME").toString();
                oneLv1.setProjectId(projectId);
                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                oneLv1.setTaxRate(oneData.get("TAXRATE").toString());
                oneLv1.setType(oneData.get("TYPE").toString());

                for (Map<String, Object> langDatas : getQData)
                {
                    String projectId_detail= langDatas.get("PROJECTID").toString();
                    if (projectId_detail.isEmpty())
                    {
                        continue;
                    }
                    if (!projectId_detail.equals(projectId))
                    {
                        continue;
                    }
                    DCP_FaPiaoProjQueryRes.projectName oneLv2 = res.new projectName();

                    String langType = langDatas.get("LANG_TYPE").toString();
                    String name = langDatas.get("PROJECTNAME").toString();
                    if (langType_cur.equals(langType))
                    {
                        projectName = name;
                    }
                    oneLv2.setLangType(langType);
                    oneLv2.setName(name);
                    oneLv1.getProjectName_lang().add(oneLv2);

                }
                oneLv1.setProjectName(projectName);
                res.getDatas().add(oneLv1);
            }
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_FaPiaoProjQueryReq req) throws Exception {
        String eId = req.geteId();
        String keyTxt = "";
        String status = "";
        String type = "";
        if (req.getRequest()!=null)
        {
            keyTxt = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            type = req.getRequest().getType();
        }
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //分页起始位置
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("");
        sqlbuf.append(" SELECT * FROM (");
        sqlbuf.append(" select COUNT(DISTINCT A.PROJECTID ) OVER() NUM ,dense_rank() over(ORDER BY A.PROJECTID) rn,A.*,B.LANG_TYPE,B.PROJECTNAME ");
        sqlbuf.append(" from DCP_FAPIAO_PROJ A ");
        sqlbuf.append(" left join DCP_FAPIAO_PROJ_LANG B on A.EID=B.EID AND A.PROJECTID=B.PROJECTID ");
        sqlbuf.append(" WHERE A.EID='"+eId+"' ");
        if (status!=null&&!status.isEmpty())
        {
            sqlbuf.append(" and A.status='"+status+"' ");
        }
        if (type!=null&&!type.isEmpty())
        {
            sqlbuf.append(" and A.type='"+type+"' ");
        }
        if (keyTxt!=null&&!keyTxt.isEmpty())
        {
            sqlbuf.append(" and (A.PROJECTID like '%%"+keyTxt+"%%' or B.PROJECTNAME like '%%"+keyTxt+"%%' )");
        }
        sqlbuf.append( " )   WHERE rn >  "+startRow+" and rn <="+(startRow + pageSize)+" " );
        String sql = sqlbuf.toString();
        return sql;
    }
}
