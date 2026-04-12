package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CardRwRuleQueryReq;
import com.dsc.spos.json.cust.res.DCP_CardRwRuleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CardRwRuleQuery extends SPosBasicService<DCP_CardRwRuleQueryReq, DCP_CardRwRuleQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_CardRwRuleQueryReq req) throws Exception
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
    protected TypeToken<DCP_CardRwRuleQueryReq> getRequestType()
    {
        return new TypeToken<DCP_CardRwRuleQueryReq>(){};
    }

    @Override
    protected DCP_CardRwRuleQueryRes getResponseType()
    {
        return new DCP_CardRwRuleQueryRes();
    }

    @Override
    protected DCP_CardRwRuleQueryRes processJson(DCP_CardRwRuleQueryReq req) throws Exception
    {
        DCP_CardRwRuleQueryRes res=this.getResponseType();
        res.setDatas(new ArrayList<>());

        String sql = null;
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

        Map<String, Boolean> cond=new HashMap<>();
        cond.put("RULEID",true);
        List<Map<String, Object>> getHeader= MapDistinct.getMap(getQDataDetail, cond);

        if (getHeader != null && getHeader.size()>0)
        {
            for (Map<String, Object> rule : getHeader)
            {
                DCP_CardRwRuleQueryRes.level1Elm lv1=res.new level1Elm();

                lv1.setMediaType(rule.get("MEDIATYPE").toString());
                lv1.setMemo(rule.get("MEMO").toString());
                lv1.setPriority(rule.get("PRIORITY").toString());
                lv1.setRuleId(rule.get("RULEID").toString());
                lv1.setRuleName(rule.get("RULENAME").toString());
                lv1.setRuleType(rule.get("RULETYPE").toString());
                lv1.setRwType(rule.get("RWTYPE").toString());
                lv1.setStatus(rule.get("STATUS").toString());
                lv1.setShopList(new ArrayList<>());

                for (Map<String, Object> shopinfo : getQDataDetail)
                {
                    if (shopinfo.get("RULEID").toString().equals(lv1.getRuleId()))
                    {
                        DCP_CardRwRuleQueryRes.shopInfo info=res.new shopInfo();

                        info.setShopId(shopinfo.get("SHOPID").toString());
                        info.setShopName(shopinfo.get("SHOPNAME").toString());
                        info.setSerialNo(shopinfo.get("SERIALNO").toString());
                        lv1.getShopList().add(info);
                    }
                }
                res.getDatas().add(lv1);
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_CardRwRuleQueryReq req) throws Exception
    {
        String status=req.getRequest().getStatus();
        String shopId=req.getRequest().getShopId();


        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("select * from  ( ");

        sqlbuf.append("select a.eid,a.ruleid,a.rulename,a.mediatype,a.rwtype,a.priority,a.status,a.memo,a.ruletype,b.shopid,N'' shopname,b.serialno from DCP_CARDRWRULE a  " +
                              "left join DCP_CARDRWRULE_SHOP b on a.eid=b.eid and a.ruleid=b.ruleid " +
                              "where a.eid='"+req.geteId()+"' and a.ruletype='1' " );
        if(status!=null && status.length()>0)
        {
            sqlbuf.append("and a.status='"+status+"' " );
        }

        sqlbuf.append("union all " +
                              "select a.eid,a.ruleid,a.rulename,a.mediatype,a.rwtype,a.priority,a.status,a.memo,a.ruletype,b.shopid,c.org_name shopname,b.serialno from DCP_CARDRWRULE a  " +
                              "inner join DCP_CARDRWRULE_SHOP b on a.eid=b.eid and a.ruleid=b.ruleid " +
                              "left join DCP_ORG_LANG c on b.eid=c.eid and b.shopid=c.organizationno and c.lang_type='"+req.getLangType()+"' " +
                              "where a.eid='"+req.geteId()+"' and a.ruletype='2' " );

        if(status!=null && status.length()>0)
        {
            sqlbuf.append("and a.status='"+status+"' " );
        }

        if(shopId!=null && shopId.length()>0)
        {
            sqlbuf.append("and b.shopid='"+shopId+"' ");
        }

        sqlbuf.append(" ) order by priority ");

        sql = sqlbuf.toString();
        return sql;
    }


}
