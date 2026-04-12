package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.json.cust.req.DCP_ProcessTask0ListQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTask0ListQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ProcessTask0ListQuery extends SPosBasicService<DCP_ProcessTask0ListQueryReq, DCP_ProcessTask0ListQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_ProcessTask0ListQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(Check.Null(req.getRequest().geteId()))
        {
            errMsg.append("eId不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getOrganizationNo()))
        {
            errMsg.append("organizationNo不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getDateType()))
        {
            errMsg.append("dateType不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getBeginDate()))
        {
            errMsg.append("beginDate不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getEndDate()))
        {
            errMsg.append("endDate不可为空值, ");
            isFail = true;
        }


        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessTask0ListQueryReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessTask0ListQueryReq>(){};
    }

    @Override
    protected DCP_ProcessTask0ListQueryRes getResponseType()
    {
        return new DCP_ProcessTask0ListQueryRes();
    }

    @Override
    protected DCP_ProcessTask0ListQueryRes processJson(DCP_ProcessTask0ListQueryReq req) throws Exception
    {
        DCP_ProcessTask0ListQueryRes res=this.getResponseType();

        int totalRecords = 0; //总笔数
        int totalPages = 0;

        //查詢資料
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        res.setDatas(res.new level1Elm());
        res.getDatas().setDataList(new ArrayList<>());

        if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            for (Map<String, Object> map : getQData)
            {
                DCP_ProcessTask0ListQueryRes.level2Elm lv2 = res.new level2Elm();
                lv2.setbDate(map.get("BDATE").toString());
                lv2.setMaterialWarehouse(map.get("MATERIALWAREHOUSE").toString());
                lv2.setMaterialWarehouseName(map.get("MATERIALWAREHOUSENAME").toString());
                lv2.setMemo(map.get("MEMO").toString());
                lv2.setpDate(map.get("PDATE").toString());
                lv2.setProcessTaskNo(map.get("PROCESSTASKNO").toString());
                lv2.setProcessPlanNo(map.get("PROCESSPLANNO").toString());
                lv2.setpTemplateName(map.get("PTEMPLATE_NAME").toString());
                lv2.setpTemplateNo(map.get("PTEMPLATENO").toString());
                lv2.setStatus(map.get("STATUS").toString());
                lv2.setTotAmt(Convert.toDouble(map.get("TOT_AMT"), 0d));
                lv2.setTotCQty(Convert.toDouble(map.get("TOT_CQTY"),0d));
                lv2.setTotDistriAmt(Convert.toDouble(map.get("TOT_DISTRIAMT"),0d));
                lv2.setTotPQty(Convert.toDouble(map.get("TOT_PQTY"),0d));
                lv2.setWarehouse(map.get("WAREHOUSE").toString());
                lv2.setWarehouseName(map.get("WAREHOUSE_NAME").toString());


                res.getDatas().getDataList().add(lv2);
            }
        }


        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_ProcessTask0ListQueryReq req) throws Exception
    {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer("");


        sqlbuf.append("SELECT * FROM ( " +
                              "select count(*) over() num, row_number() over( order by a.processtaskno) rn,a.*,b.WAREHOUSE_NAME,c.warehouse_name MATERIALWAREHOUSENAME,d.ptemplate_name from DCP_PROCESSTASK0 a " +
                              "left join dcp_warehouse_lang b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse and b.lang_type='"+req.getLangType()+"' " +
                              "left join dcp_warehouse_lang c on a.eid=c.eid and a.organizationno=c.organizationno and a.warehouse=c.warehouse and c.lang_type='"+req.getLangType()+"' " +
                              "left join dcp_ptemplate d on a.eid=d.eid and a.ptemplateno=d.ptemplateno " +
                              "where a.eid='"+req.getRequest().geteId()+"' " +
                              "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' ");

        if (!Check.Null(req.getRequest().getKeyTxt()))
        {
            sqlbuf.append("and (a.processtaskno like '%%"+req.getRequest().getKeyTxt()+"%%' or a.processplanno like '%%"+req.getRequest().getKeyTxt()+"%%' ) ");
        }

        if (!Check.Null(req.getRequest().getStatus()))
        {
            sqlbuf.append("and a.status='"+req.getRequest().getStatus()+"' " );
        }

        //日期类型（0.单据日期 1.生产日期 ）默认1
        if (req.getRequest().getDateType().equals("0"))
        {
            sqlbuf.append("and a.bdate>="+req.getRequest().getBeginDate()+" " );
            sqlbuf.append("and a.bdate<="+req.getRequest().getEndDate()+" " );
        }

        if (req.getRequest().getDateType().equals("1"))
        {
            sqlbuf.append("and a.pdate>="+req.getRequest().getBeginDate()+" " );
            sqlbuf.append("and a.pdate<="+req.getRequest().getEndDate()+" " );
        }


        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));


        return sqlbuf.toString();
    }


}
