package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PStockInTaskQueryReq;
import com.dsc.spos.json.cust.res.DCP_PStockInTaskQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

public class DCP_PStockInTaskQuery extends SPosBasicService<DCP_PStockInTaskQueryReq, DCP_PStockInTaskQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_PStockInTaskQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(Check.Null(req.getRequest().getPDate()))
        {
            errMsg.append("pDate不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PStockInTaskQueryReq> getRequestType()
    {
        return new TypeToken<DCP_PStockInTaskQueryReq>(){};
    }

    @Override
    protected DCP_PStockInTaskQueryRes getResponseType()
    {
        return new DCP_PStockInTaskQueryRes();
    }

    @Override
    protected DCP_PStockInTaskQueryRes processJson(DCP_PStockInTaskQueryReq req) throws Exception
    {
        DCP_PStockInTaskQueryRes res=this.getResponseType();

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
                DCP_PStockInTaskQueryRes.level2Elm lv2=res.new level2Elm();
                lv2.setBaseUnit(map.get("BASEUNIT").toString());
                lv2.setDistriPrice(map.get("DISTRIPRICE").toString());
                lv2.setDtBeginTime(map.get("BEGIN_TIME").toString());
                lv2.setDtEndTime(map.get("END_TIME").toString());
                lv2.setDtName(map.get("DTNAME").toString());
                lv2.setDtNo(map.get("DTNO").toString());
                lv2.setFeatureName(map.get("FEATURENAME").toString());
                lv2.setFeatureNo(map.get("FEATURENO").toString());
                lv2.setItem(map.get("ITEM").toString());
                lv2.setMaterialWarehouseNo(map.get("MATERIALWAREHOUSE").toString());
                lv2.setMemo(map.get("MEMO").toString());
                lv2.setMulQty(map.get("MUL_QTY").toString());
                lv2.setPDate(map.get("PDATE").toString());
                lv2.setPluName(map.get("PLU_NAME").toString());
                lv2.setPluNo(map.get("PLUNO").toString());
                lv2.setPqty(map.get("PQTY").toString());
                lv2.setPrice(map.get("PRICE").toString());
                lv2.setProcessPlanNo(map.get("PROCESSPLANNO").toString());
                lv2.setProcessTaskNo(map.get("PROCESSTASKNO").toString());
                lv2.setPStockInQty(map.get("PSTOCKIN_QTY").toString());
                lv2.setPStockInStatus(map.get("PSTOCKINSTATUS").toString());
                lv2.setPTemplateNo(map.get("PTEMPLATENO").toString());
                lv2.setPunit(map.get("PUNIT").toString());
                lv2.setPunitName(map.get("UNAME").toString());
                lv2.setBaseUnitUdLength(map.get("BASEUNITUDLENGTH").toString());
                lv2.setPunitUdLength(map.get("UDLENGTH").toString());
                lv2.setTask0No(map.get("TASK0NO").toString());
                lv2.setUnitRatio(map.get("UNIT_RATIO").toString());
                lv2.setWarehouse(map.get("WAREHOUSE").toString());

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
    protected String getQuerySql(DCP_PStockInTaskQueryReq req) throws Exception
    {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer sb=new StringBuffer();

        sb.append("select * from (" +
                          "select count(*) over() num,rownum rn,a.processtaskno,a.memo,a.ptemplateno,a.pdate,a.warehouse,a.materialwarehouse,a.dtno,c.dtname,c.begin_time,c.end_time,a.processplanno,a.task0no, " +
                          "b.item,b.pluno,d.plu_name,b.punit,e.uname,f.udlength,b.pqty,b.price,b.distriprice,b.mul_qty,b.baseunit,h.uname BASEUNITNAME,b.unit_ratio,b.featureno, " +
                          "nvl(b.pstockin_qty,0) pstockin_qty,case when b.pqty-nvl(b.pstockin_qty,0)>0 then 'N' ELSE 'Y' END AS PSTOCKINSTATUS,g.udlength baseUnitUdLength,i.featurename " +
                          "from dcp_processtask a " +
                          "inner join dcp_processtask_detail b on a.eid=b.eid and a.shopid=b.shopid and a.processtaskno=b.processtaskno " +
                          "left join dcp_dinnertime c on c.eid=a.eid and c.shopid=a.shopid and c.dtno=a.dtno " +
                          "left join dcp_goods_lang d on d.eid=b.eid and d.pluno=b.pluno and d.lang_type='"+req.getLangType()+"' " +
                          "left join dcp_unit_lang e on e.eid=b.eid and e.unit=b.punit and e.lang_type='"+req.getLangType()+"' " +
                          "left join dcp_unit f on f.eid=b.eid and f.unit=b.punit " +
                          "left join dcp_unit g on g.eid=b.eid and g.unit=b.baseunit " +
                          "left join dcp_unit_lang h on h.eid=b.eid and h.unit=b.baseunit and h.lang_type='"+req.getLangType()+"' " +
                          "left join dcp_goods_feature_lang i on a.eid=i.eid and b.pluno=i.pluno and b.featureno=i.featureno and i.lang_type='"+req.getLangType()+"' " +
                          "where a.eid='"+req.geteId()+"' " +
                          "and a.shopid='"+req.getOrganizationNO()+"' " +
                          "and a.pdate='"+req.getRequest().getPDate()+"' " );


        if (!Check.Null(req.getRequest().getKeyTxt()))
        {
            sb.append("and a.processtaskno like '%%"+req.getRequest().getKeyTxt()+"%%' " );
        }
        if (!Check.Null(req.getRequest().getDtNo()))
        {
            sb.append("and a.dtno='"+req.getRequest().getDtNo()+"' " );
        }
        if (!Check.Null(req.getRequest().getIsPStockIn()))
        {
            if (req.getRequest().getIsPStockIn().equals("Y"))
            {
                sb.append("and b.pqty-nvl(b.pstockin_qty,0)<=0 ");
            }
            else
            {
                sb.append("and b.pqty-nvl(b.pstockin_qty,0)>0 ");
            }
        }

        sb.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

        return sb.toString();
    }






}
