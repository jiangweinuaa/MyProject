package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DataRatioQueryReq;
import com.dsc.spos.json.cust.res.DCP_DataRatioQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_DataRatioQuery extends SPosBasicService<DCP_DataRatioQueryReq, DCP_DataRatioQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DataRatioQueryReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getD_year()))
        {
            errCt++;
            errMsg.append("d_year年份不能为空值，");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DataRatioQueryReq> getRequestType() {
        return new TypeToken<DCP_DataRatioQueryReq>(){};
    }

    @Override
    protected DCP_DataRatioQueryRes getResponseType() {
        return new DCP_DataRatioQueryRes();
    }

    @Override
    protected DCP_DataRatioQueryRes processJson(DCP_DataRatioQueryReq req) throws Exception {

        if (req.getPageNumber()<=0)
        {
            req.setPageNumber(1);
        }
        if (req.getPageSize()<=0)
        {
            req.setPageSize(10);
        }
        int totalRecords = 0;								//总笔数
        int totalPages = 0;
        DCP_DataRatioQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
        if (getQDataDetail!=null&&!getQDataDetail.isEmpty())
        {
            String num = getQDataDetail.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put("SHOPID", true);
            List<Map<String, Object>> headerDatas= MapDistinct.getMap(getQDataDetail, condition);
            for(Map<String, Object> oneData : headerDatas)
            {
                DCP_DataRatioQueryRes.level1Elm oneLv1 = res.new level1Elm();
                oneLv1.setDataRatioList(new ArrayList<>());
                String shopId = oneData.get("SHOPID").toString();
                String shopName = oneData.get("ORG_NAME").toString();
                oneLv1.setShopId(shopId);
                oneLv1.setShopName(shopName);
                for (Map<String, Object> oneData2 : getQDataDetail)
                {
                    String shopId_detail = oneData2.get("SHOPID").toString();
                    String d_year = oneData2.get("D_YEAR").toString();
                    String d_month = oneData2.get("D_MONTH").toString();
                    String dataRatio = oneData2.get("DATARATIO").toString();
                    if (shopId_detail==null||shopId_detail.isEmpty())
                    {
                        continue;
                    }
                    if (!shopId_detail.equals(shopId))
                    {
                        continue;
                    }
                    if (dataRatio==null||dataRatio.isEmpty())
                    {
                        dataRatio = "100";
                    }
                    DCP_DataRatioQueryRes.level2Elm oneLv2 = res.new level2Elm();
                    oneLv2.setD_year(d_year);
                    oneLv2.setD_month(d_month);
                    oneLv2.setDataRatio(dataRatio);
                    oneLv1.getDataRatioList().add(oneLv2);
                }

                res.getDatas().add(oneLv1);

            }
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_DataRatioQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        String eId = req.geteId();
        String d_year = req.getRequest().getD_year();
        String shopId_sqlCon = "";
        if (req.getRequest().getShopList()!=null&&req.getRequest().getShopList().length>0)
        {
            shopId_sqlCon = PosPub.getArrayStrSQLIn(req.getRequest().getShopList());
        }
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        int endRow = startRow + pageSize;
        String langType = req.getLangType();
        if (langType==null||langType.isEmpty())
        {
            langType = "zh_CN";
        }

        sqlbuf.append("SELECT *  FROM ("
                + " select count(distinct a.shopid) over() num, dense_rank() over (order  by a.shopid) rn,a.*,b.ORG_NAME from dcp_dataratio a "
                + " left join dcp_org_lang b on a.eid=b.eid and a.shopid=b.organizationno and b.lang_type='"+langType+"' "
                + " WHERE a.EID = '"+eId+"' " );

        if (d_year != null && !d_year.trim().isEmpty())
        {
            sqlbuf.append("and a.D_YEAR='"+d_year+"' ");
        }
        if (shopId_sqlCon != null && !shopId_sqlCon.trim().isEmpty())
        {
            sqlbuf.append("and a.SHOPID in ("+shopId_sqlCon+") ");
        }

        sqlbuf.append( " ) ");
        sqlbuf.append( " where rn > "+startRow+" and rn <= "+endRow+ "  ");
        sqlbuf.append( "  order by SHOPID,D_YEAR,D_MONTH ");

        return sqlbuf.toString();
    }
}
