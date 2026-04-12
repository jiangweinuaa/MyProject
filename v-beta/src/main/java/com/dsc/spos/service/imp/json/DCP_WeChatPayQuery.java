package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_WeChatPayQueryReq;
import com.dsc.spos.json.cust.res.DCP_CreditQRcodeQueryRes;
import com.dsc.spos.json.cust.res.DCP_WeChatPayQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_WeChatPayQuery extends SPosBasicService<DCP_WeChatPayQueryReq, DCP_WeChatPayQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_WeChatPayQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getShopId()))
        {
            errMsg.append("门店编号不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_WeChatPayQueryReq> getRequestType()
    {
        return new TypeToken<DCP_WeChatPayQueryReq>(){};
    }

    @Override
    protected DCP_WeChatPayQueryRes getResponseType()
    {
        return new DCP_WeChatPayQueryRes();
    }

    @Override
    protected DCP_WeChatPayQueryRes processJson(DCP_WeChatPayQueryReq req) throws Exception
    {
        DCP_WeChatPayQueryRes res = null;
        res = this.getResponse();
        res.setDatas(new ArrayList<>());

        int totalRecords;								//总笔数
        int totalPages;									//总页数


        String sqlDef = this.getQuerySql(req);
        List<Map<String, Object>>  getQData = this.doQueryData(sqlDef, null);
        String thirdShop="";
        if (getQData != null && getQData.isEmpty() == false)
        {
            thirdShop=getQData.get(0).get("THIRD_SHOP").toString();
        }
        getQData=null;
        if( Check.Null(thirdShop) || thirdShop.isEmpty() || thirdShop.trim().equals("") || thirdShop.trim().equals(" ") )
        {
            res.setServiceDescription( "组织信息-本门店加盟支付店号未配置!");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "组织信息-本门店加盟支付店号未配置!");
        }


        sqlDef = this.getSQL(req,thirdShop);
        getQData = this.doQueryData(sqlDef, null);

        if (getQData != null && getQData.size()>0)
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> map : getQData)
            {
                DCP_WeChatPayQueryRes.level1Elm lv1=res.new level1Elm();
                lv1.setShopId(map.get("SHOPID").toString());
                lv1.setMchId(map.get("MCHID").toString());
                lv1.setOrderId(map.get("ORDERID").toString());
                lv1.setAmount(map.get("AMOUNT").toString());
                lv1.setCreateTime(map.get("CREATETIME").toString());
                lv1.setUploadTime(map.get("UPLOADTIME").toString());
                lv1.setProcess_status(map.get("PROCESS_STATUS").toString());
                res.getDatas().add(lv1);
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
    protected String getQuerySql(DCP_WeChatPayQueryReq req) throws Exception
    {
        String sql="select NVL(THIRD_SHOP,' ') AS THIRD_SHOP FROM DCP_ORG where EID='"+req.geteId()+"' AND ORGANIZATIONNO='"+req.getRequest().getShopId()+"' ";

        return sql;
    }


    protected String getSQL(DCP_WeChatPayQueryReq req,String thirdShop) throws Exception
    {
        String m_status=req.getRequest().getStatus();
        if (Check.Null(m_status))
        {
            m_status="1";
        }

        if (PosPub.isNumeric(m_status)==false)
        {
            m_status="1";
        }
        int iStatus=Integer.parseInt(m_status);

        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        //分页起始位置
        int startRow=(pageNumber-1) * pageSize;


       // String sql="select * from ( select count(*) over() num,row_number() over (order by createTime desc) rn,SHOPID,ORDERID,AMOUNT,MCHID,CREATETIME from PAY_LIST where eid='"+req.geteId()+"' and shopid='"+thirdShop+"' and status="+iStatus+" and POSID='888888' and PAYTYPE='#P1' ) where rn>"+startRow+" and rn<="+(startRow+pageSize);
        String sql="select * from ( select count(*) over() num,row_number() over (order by createTime desc) rn,SHOPID,ORDERID,AMOUNT,MCHID,CREATETIME,UPLOADTIME,PROCESS_STATUS from DCP_CREDIT_PAY where eid='"+req.geteId()+"' and shopid='"+req.getShopId()+"' and status=2 and POSID='888888' and PAYTYPE='#P1' ) where rn>"+startRow+" and rn<="+(startRow+pageSize);
        return sql;
    }
}
