package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TareQueryReq;
import com.dsc.spos.json.cust.req.DCP_TareUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TareQueryRes;
import com.dsc.spos.json.cust.res.DCP_TaxCategoryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_TareQuery extends SPosBasicService<DCP_TareQueryReq, DCP_TareQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_TareQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_TareQueryReq> getRequestType()
    {
        return new TypeToken<DCP_TareQueryReq>(){};
    }

    @Override
    protected DCP_TareQueryRes getResponseType()
    {
        return new DCP_TareQueryRes();
    }

    @Override
    protected DCP_TareQueryRes processJson(DCP_TareQueryReq req) throws Exception
    {
        String sql = null;
        DCP_TareQueryRes res = null;
        res = this.getResponse();

        int totalRecords;								//总笔数
        int totalPages;									//总页数
        res.setDatas(new ArrayList<>());

        sql = this.getQuerySql(req);
        List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
        if(!getDatas.isEmpty() && getDatas != null )
        {
            String num = getDatas.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            StringBuffer sJoinTareId=new StringBuffer("");
            StringBuffer sJoinEid=new StringBuffer("");

            for (Map<String, Object> oneData : getDatas)
            {
                DCP_TareQueryRes.level1Elm lv1=res. new level1Elm();
                lv1.setPrice(new BigDecimal(PosPub.isNumericType(oneData.get("PRICE").toString())==false?"0":oneData.get("PRICE").toString()));
                lv1.setTare(new BigDecimal(PosPub.isNumericType(oneData.get("TARE").toString())==false?"0":oneData.get("TARE").toString()));
                lv1.setRestrictShop(oneData.get("RESTRICTSHOP").toString());
                lv1.setStatus(oneData.get("STATUS").toString());
                lv1.setTareId(oneData.get("TAREID").toString());
                lv1.setTareName(oneData.get("TARENAME").toString());
                lv1.setUnitType(oneData.get("UNITTYPE").toString());
                lv1.setShopList(new ArrayList<>());
                res.getDatas().add(lv1);

                if (lv1.getRestrictShop().equals("0")==false)
                {
                    sJoinTareId.append(oneData.get("TAREID").toString()+",");
                    sJoinEid.append(oneData.get("EID").toString()+",");
                }
            }

            //
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("TAREID", sJoinTareId.toString());
            mapOrder.put("EID", sJoinEid.toString());
            //
            MyCommon cm=new MyCommon();
            String withasSql=cm.getFormatSourceMultiColWith(mapOrder);
            mapOrder=null;

            if (withasSql.equals("")==false)
            {
                StringBuffer sqlbuf=new StringBuffer(" with a AS ( "
                                                             + withasSql + " ) "
                                                             + "select b.* from a "
                                                             + "inner join dcp_tareset_shop b on a.eid=b.eid and a.TAREID=b.TAREID ");
                List<Map<String, Object>> getMulti = this.doQueryData(sqlbuf.toString(), null);
                if (getMulti != null && getMulti.size()>0)
                {
                    for (Map<String, Object> map : getMulti)
                    {
                        String tareid=map.get("TAREID").toString();
                        String shopid=map.get("SHOPID").toString();
                        String shopname=map.get("SHOPNAME").toString();

                        //门店列表加进来
                        List<DCP_TareQueryRes.level1Elm> datas=res.getDatas().stream().filter(p->p.getTareId().equals(tareid)).collect(Collectors.toList());
                        if (datas != null && datas.size()>0)
                        {
                            DCP_TareQueryRes.levelShop shopV=res. new levelShop();
                            shopV.setShopId(shopid);
                            shopV.setShopName(shopname);
                            datas.get(0).getShopList().add(shopV);
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

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_TareQueryReq req) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();

        String keyTxt = null;
        String status = null;
        if(req.getRequest()!=null)
        {
            keyTxt = req.getRequest().getKeytxt();
            status = req.getRequest().getStatus();
        }

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = ((pageNumber - 1) * pageSize);
        startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        //传分页才处理分页
        if (pageSize>0)
        {
            sqlbuf.append(" SELECT * FROM (");
        }

        sqlbuf.append(" select count(*) over() num,A.*, row_number() over (order by A.TAREID) rn from dcp_tareset A where A.EID='"+eId+"' " );

        if (keyTxt != null && keyTxt.length() > 0)
        {
            sqlbuf.append(" AND ( A.TAREID like '%%"+keyTxt+"%%' or A.TARENAME like '%%"+keyTxt+"%%' ) ");
        }
        if (status != null && status.length() > 0)
        {
            sqlbuf.append(" AND A.STATUS = '"+status+"' ");
        }

        //传分页才处理分页
        if (pageSize>0)
        {
            sqlbuf.append( " ) WHERE rn > " + startRow + " AND rn <= " + (startRow+pageSize));
        }

        sql= sqlbuf.toString();
        return sql;
    }
}
