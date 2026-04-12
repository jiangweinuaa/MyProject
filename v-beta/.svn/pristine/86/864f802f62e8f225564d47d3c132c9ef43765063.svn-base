package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_CompanyQueryReq;
import com.dsc.spos.json.cust.res.DCP_CompanyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CompanyQuery extends SPosBasicService<DCP_CompanyQueryReq,DCP_CompanyQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CompanyQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_CompanyQueryReq> getRequestType() {
        return new TypeToken<DCP_CompanyQueryReq>(){};
    }

    @Override
    protected DCP_CompanyQueryRes getResponseType() {
        return new DCP_CompanyQueryRes();
    }

    @Override
    protected DCP_CompanyQueryRes processJson(DCP_CompanyQueryReq req) throws Exception {
        DCP_CompanyQueryRes res=this.getResponse();
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        res.setDatas(new ArrayList<DCP_CompanyQueryRes.level1Elm>());
        if(getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> map : getQData) {
                DCP_CompanyQueryRes.level1Elm lv1=res.new level1Elm();
                lv1.setCompanyId(map.get("ORGANIZATIONNO").toString());
                lv1.setCompanyName(map.get("ORG_NAME").toString());
                lv1.setCompanySName(map.get("SNAME").toString());
                lv1.setStatus(map.get("STATUS").toString());

                res.getDatas().add(lv1);
            }
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {}

    @Override
    protected String getQuerySql(DCP_CompanyQueryReq req) throws Exception {
        String eId = req.geteId();
        String langtype = req.getLangType();
        String alldata = req.getRequest().getAllData(); //如果传入参数allData=0或不传，则返回当前用户隶属公司或当前用户管控公司
        String companyId = req.getRequest().getCompanyId();
        String keyTxt = req.getRequest().getSearchString();
        String status = req.getRequest().getStatus();

        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer sqlBuff = new StringBuffer();
        if (Check.Null(alldata)|| alldata.equals("0") ) {
            sqlBuff.append(" select * from ("
                    + " select count(*) over() num, row_number() over (order by a.organizationno) rn,"
                    + " a.organizationno,b.org_name,a.sname,a.status from dcp_org a "
                    + " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+langtype+"' "
                    + " WHERE a.EID='"+eId+"' and a.org_form='0' ");
        } else {
            sqlBuff.append(" select * from ("
                    + " select count(*) over() num,row_number() over (order by a.organizationno) rn,"
                    + " a.organizationno,b.org_name,a.sname,a.status from dcp_org a "
                    + " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+langtype+"' "
                    + " INNER JOIN PLATFORM_STAFFS_SHOP c on a.EID = c.EID and a.ORGANIZATIONNO = c.SHOPID and c.opNO = '"+req.getOpNO()+"' "
                    + " WHERE a.EID='"+eId+"' and a.org_form='0' AND a.BELFIRM ='" +req.getBELFIRM() +"' ");
        }

        if (keyTxt != null && keyTxt.length()>0) {
            sqlBuff.append( " AND (a.ORGANIZATIONNO like '%%"+keyTxt+"%%' or b.ORG_NAME like '%%"+keyTxt+"%%' or a.sname like '%%"+keyTxt+"%%'  ) ");
        }

        if (companyId != null && companyId.length()>0) {
            sqlBuff.append( " AND a.ORGANIZATIONNO = '"+companyId+"' ");
        }

        if (status != null && status.length()>0) {
            sqlBuff.append( " AND a.status = '"+status+"' ");
        }
        sqlBuff.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
        return sqlBuff.toString();
    }




}
