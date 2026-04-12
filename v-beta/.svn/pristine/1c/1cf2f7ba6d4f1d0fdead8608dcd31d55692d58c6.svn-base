package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurTemplateQuery extends SPosBasicService<DCP_PurTemplateQueryReq, DCP_PurTemplateQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurTemplateQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PurTemplateQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurTemplateQueryReq>(){};
    }

    @Override
    protected DCP_PurTemplateQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurTemplateQueryRes();
    }

    @Override
    protected DCP_PurTemplateQueryRes processJson(DCP_PurTemplateQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PurTemplateQueryRes res = this.getResponse();
        try
        {
            int totalRecords;		//总笔数
            int totalPages;
            //单头查询
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            res.setDatas(new ArrayList<DCP_PurTemplateQueryRes.DataList>());
            if (getQData != null && getQData.isEmpty() == false)
            {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                String purTemplateNo="";

                for (Map<String, Object> oneData : getQData)
                {
                    DCP_PurTemplateQueryRes.DataList oneLv1 = res.new DataList();

                    oneLv1.setPurTemplateNo(oneData.get("PURTEMPLATENO").toString());
                    oneLv1.setPurTemplateName(oneData.get("PURTEMPLATENAME").toString());
                    oneLv1.setSupplier(oneData.get("SUPPLIERNO").toString());
                    oneLv1.setSupplierName(oneData.get("SUPPLIER_NAME").toString());
                    oneLv1.setPurType(oneData.get("PURTYPE").toString());
                    oneLv1.setPurCenter(oneData.get("PURCENTER").toString());
                    oneLv1.setOrgName(oneData.get("ORG_NAME").toString());
                    oneLv1.setTimeType(oneData.get("TIME_TYPE").toString());
                    oneLv1.setTimeValue(oneData.get("TIME_VALUE").toString());
                    oneLv1.setPreDays(oneData.get("PRE_DAY").toString());
                    oneLv1.setMemo(oneData.get("MEMO").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());
                    oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
                    oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
                    oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
                    oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
                    oneLv1.setCreate_datetime(oneData.get("CREATETIME").toString());
                    oneLv1.setLastmodifyID(oneData.get("LASTMODIOPID").toString());
                    oneLv1.setLastmodifyName(oneData.get("LASTMODIOPNAME").toString());
                    oneLv1.setLastmodify_datetime(oneData.get("LASTMODITIME").toString());
                    oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
                    oneLv1.setTaxName(oneData.get("TAXNAME").toString());
                    oneLv1.setTaxRate(oneData.get("TAXRATE").toString());
                    oneLv1.setInclTax(oneData.get("INCLTAX").toString());
                    oneLv1.setBeginDate(oneData.get("BEGINDATE").toString());
                    oneLv1.setEndDate(oneData.get("ENDDATE").toString());
                    //lang_list  plucnt orgcnt
                    purTemplateNo+="'"+oneData.get("PURTEMPLATENO").toString()+"',";
                    oneLv1.setLang_list(new ArrayList<>());

                    res.getDatas().add(oneLv1);
                    oneLv1=null;
                }

                if(purTemplateNo.length()>0){
                    purTemplateNo=purTemplateNo.substring(0, purTemplateNo.length()-1);

                    String langSql=this.getLangSql(purTemplateNo,req.geteId());
                    List<Map<String, Object>> getLangData=this.doQueryData(langSql, null);
                    for (Map<String, Object> oneData : getLangData)
                    {
                        for(DCP_PurTemplateQueryRes.DataList oneLv1:res.getDatas())
                        {
                            if(oneLv1.getPurTemplateNo().equals(oneData.get("PURTEMPLATENO").toString()))
                            {
                                DCP_PurTemplateQueryRes.Lang oneLang=res.new Lang();
                                oneLang.setLangType(oneData.get("LANG_TYPE").toString());
                                oneLang.setName(oneData.get("NAME").toString());
                                oneLv1.getLang_list().add(oneLang);
                            }
                        }
                    }

                    String goodSql = this.getGoodSql(purTemplateNo, req.geteId());
                    List<Map<String, Object>> getGoodData=this.doQueryData(goodSql, null);
                    for (Map<String, Object> oneData : getGoodData)
                    {
                        for(DCP_PurTemplateQueryRes.DataList oneLv1:res.getDatas())
                        {
                            if(oneLv1.getPurTemplateNo().equals(oneData.get("PURTEMPLATENO").toString()))
                            {
                                oneLv1.setPlucnt(oneData.get("PLUCNT").toString());
                            }
                        }
                    }

                    String orgSql = this.getOrgSql(purTemplateNo, req.geteId());
                    List<Map<String, Object>> getOrgData=this.doQueryData(orgSql, null);
                    for (Map<String, Object> oneData : getOrgData)
                    {
                        for(DCP_PurTemplateQueryRes.DataList oneLv1:res.getDatas())
                        {
                            if(oneLv1.getPurTemplateNo().equals(oneData.get("PURTEMPLATENO").toString()))
                            {
                                oneLv1.setOrgcnt(oneData.get("ORGCNT").toString());
                            }
                        }
                    }

                }

            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;
        }
        catch (Exception e)
        {
            // TODO: handle exception
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PurTemplateQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        DCP_PurTemplateQueryReq.levelElm request = req.getRequest();
        String status = request.getStatus();
        String purType = request.getPurType();
        String supplier = request.getSupplier();
        String plu = request.getPlu();

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");
        sqlbuf.append(" select ptemplate.* from (");
        sqlbuf.append(" "
                + " select distinct a.*,d.fname as supplier_name,e.org_name,dd.departname as CREATEDEPTNAME,em1.op_name as CREATEOPNAME,em2.op_name as LASTMODIOPNAME,a1.name as PURTEMPLATENAME,f.taxcode,g.taxname,f.taxrate,f.INCLTAX," +
                " to_char(d.begindate,'yyyy-MM-dd') beginDate,to_char(d.enddate,'yyyy-MM-dd') as enddate  " +//c.plu_name
                " from DCP_PURCHASETEMPLATE a " +
                " left join DCP_PURCHASETEMPLATE_LANG a1 on a.eid=a1.eid and a1.lang_type='"+req.getLangType()+"' and a.PURTEMPLATENO =a1.PURTEMPLATENO "
                + " inner join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO "
                + " left join dcp_goods_lang c  on b.pluno=c.pluno and b.eid=c.eid  and c.lang_type = '"+langType+"' "
                + " left join DCP_BIZPARTNER d  on d.BIZPARTNERNO=a.SUPPLIERNO and d.eid=a.eid  "
                + " left join dcp_org_lang e on e.eid=a.eid and a.purcenter=e.organizationno  and e.lang_type = '"+langType+"' "
                + " left join PLATFORM_STAFFS_LANG em1 on em1.eid=a.eid and em1.opno=a.CREATEOPID and em1.lang_type='"+langType+"' "
                + " left join PLATFORM_STAFFS_LANG em2 on em2.eid=a.eid and em2.opno=a.LASTMODIOPID and em2.lang_type='"+langType+"' "
                + " left join dcp_department_lang dd on dd.eid=a.eid and dd.departno=a.createdeptid and dd.lang_type='"+req.getLangType()+"'  "
                + " left join DCP_TAXCATEGORY f on f.eid=d.eid and f.taxcode=d.taxcode "
                + " left join DCP_TAXCATEGORY_lang g on g.eid=d.eid and g.taxcode=d.taxcode and g.lang_type='"+req.getLangType()+"' "
                + " where  a.eid= '"+ eId +"' "
                + " "
                + " ");

        if(!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if(!Check.Null(purType)){
            sqlbuf.append(" and a.purtype='"+purType+"' ");
        }
        if(!Check.Null(supplier)){
            sqlbuf.append(" and (a.SUPPLIERNO like '%%"+supplier+"%%' or d.sname like '%%"+supplier+"%%' or d.fname like '%%"+supplier+"%%') ");
        }
        if(!Check.Null(plu)){
            sqlbuf.append(" and (b.pluno='"+plu+"' or c.plu_name like '%%"+ plu +"%%') ");
        }


        sqlbuf.append(" ) ptemplate");


        sqlbuf.append(" "
                + " order by ptemplate.PURTEMPLATENO"
                + " )ac"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");

        return sqlbuf.toString();
    }

    private String getLangSql(String nos,String eid){
        String sql="select * from DCP_PURCHASETEMPLATE_LANG where eid='"+eid+"'" +
                " and PURTEMPLATENO in ("+nos+") ";
        return sql;
    }


    private String getGoodSql(String nos,String eid){
        String sql="select PURTEMPLATENO,count(PURTEMPLATENO) as PLUCNT from DCP_PURCHASETEMPLATE_GOODS where eid='"+eid+"'" +
                " and PURTEMPLATENO in ("+nos+") group by PURTEMPLATENO ";
        return sql;
    }
    private String getOrgSql(String nos,String eid){
        String sql="select PURTEMPLATENO,count(PURTEMPLATENO) as orgcnt from DCP_PURCHASETEMPLATE_ORG where eid='"+eid+"'" +
                " and PURTEMPLATENO in ("+nos+") group by PURTEMPLATENO ";
        return sql;
    }

}



