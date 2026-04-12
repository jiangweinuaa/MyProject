package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CardRwRuleDetailReq;
import com.dsc.spos.json.cust.res.DCP_CardRwRuleDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_CardRwRuleDetail extends SPosBasicService<DCP_CardRwRuleDetailReq, DCP_CardRwRuleDetailRes>
{


    @Override
    protected boolean isVerifyFail(DCP_CardRwRuleDetailReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String ruleId=req.getRequest().getRuleId();
        if(Check.Null(ruleId))
        {
            errMsg.append("请求参数ruleId不能为空值， ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CardRwRuleDetailReq> getRequestType()
    {
        return new TypeToken<DCP_CardRwRuleDetailReq>(){};
    }

    @Override
    protected DCP_CardRwRuleDetailRes getResponseType()
    {
        return new DCP_CardRwRuleDetailRes();
    }

    @Override
    protected DCP_CardRwRuleDetailRes processJson(DCP_CardRwRuleDetailReq req) throws Exception
    {
        DCP_CardRwRuleDetailRes res=this.getResponseType();
        res.setDatas(res.new level1Elm());

        String ruleId=req.getRequest().getRuleId();

        String sql = null;
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

        if (getQDataDetail != null && getQDataDetail.size()>0)
        {
            //只有1笔数据，不会影响效能
            for (Map<String, Object> rule : getQDataDetail)
            {
                DCP_CardRwRuleDetailRes.level1Elm lv1=res.new level1Elm();
                lv1.setMediaType(rule.get("MEDIATYPE").toString());
                lv1.setMemo(rule.get("MEMO").toString());
                lv1.setPriority(rule.get("PRIORITY").toString());
                lv1.setRuleId(rule.get("RULEID").toString());
                lv1.setRuleName(rule.get("RULENAME").toString());
                lv1.setRwType(rule.get("RWTYPE").toString());
                lv1.setStatus(rule.get("STATUS").toString());
                lv1.setRuleType(rule.get("RULETYPE").toString());
                lv1.setShopList(new ArrayList<>());
                lv1.setParams(new ArrayList<>());

                //门店列表
                sql = this.getQuerySql_shop(req);
                List<Map<String, Object>> getQShop = this.doQueryData(sql, null);
                if (getQShop != null && getQShop.size()>0)
                {
                    for (Map<String, Object> shopinfo : getQShop)
                    {
                        DCP_CardRwRuleDetailRes.shopInfo info=res.new shopInfo();
                        info.setShopId(shopinfo.get("SHOPID").toString());
                        info.setShopName(shopinfo.get("ORG_NAME").toString());
                        info.setSerialNo(shopinfo.get("SERIALNO").toString());
                        lv1.getShopList().add(info);
                    }
                }

                //参数列表
                sql = this.getQuerySql_params(req,lv1.getMediaType());
                List<Map<String, Object>> getQParams = this.doQueryData(sql, null);
                if (getQParams != null && getQParams.size()>0)
                {

                    Map<String, Boolean> cond=new HashMap<>();
                    cond.put("GROUPID",true);

                    List<Map<String, Object>> getHeader= MapDistinct.getMap(getQParams, cond);

                    for (Map<String, Object> head : getHeader)
                    {
                        DCP_CardRwRuleDetailRes.paramInfo para=res.new paramInfo();
                        para.setGroupId(head.get("GROUPID").toString());
                        if (req.getLangType().equals("zh_TW"))
                        {
                            para.setGroupName(head.get("GROUPNAME_TW").toString());
                        }
                        else if (req.getLangType().equals("zh_EN"))
                        {
                            para.setGroupName(head.get("GROUPNAME_EN").toString());
                        }
                        else
                        {
                            para.setGroupName(head.get("GROUPNAME_CN").toString());
                        }
                        para.setParamList(new ArrayList<>());

                        for (Map<String, Object> detail : getQParams)
                        {
                            if (detail.get("GROUPID").toString().equals(para.getGroupId()))
                            {
                                DCP_CardRwRuleDetailRes.paramListInfo p3=res.new paramListInfo();
                                p3.setAlterateValue(detail.get("ALTERATEVALUE").toString());
                                p3.setConType(detail.get("CONTYPE").toString());
                                p3.setDefValue(detail.get("DEFVALUE").toString());
                                p3.setMemo(detail.get("MEMO").toString());
                                if (req.getLangType().equals("zh_TW"))
                                {
                                    p3.setName(detail.get("NAME_TW").toString());
                                }
                                else if (req.getLangType().equals("zh_EN"))
                                {
                                    p3.setName(detail.get("NAME_EN").toString());
                                }
                                else
                                {
                                    p3.setName(detail.get("NAME_CN").toString());
                                }
                                p3.setParam(detail.get("PARAM").toString());
                                p3.setSortId(detail.get("SORTID").toString());

                                //优先返回 DCP_CARDRWRULE.param中参数编码对应的值，没有的话，则返回空串

                                p3.setCurValue("");

                                String v_params=rule.get("PARAMS").toString();
                                if (Check.Null(v_params)==false)
                                {
                                    JSONObject json=new JSONObject(v_params);
                                    if (json.has(p3.getParam()))
                                    {
                                        //DCP_CARDRWRULE.param中参数编码对应的值
                                        p3.setCurValue(json.get(p3.getParam()).toString());
                                    }
                                }
                                para.getParamList().add(p3);
                            }
                        }

                        //
                        lv1.getParams().add(para);
                    }
                }
                //
                res.setDatas(lv1);
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
    protected String getQuerySql(DCP_CardRwRuleDetailReq req) throws Exception
    {
        String sql = null;
        String ruleId=req.getRequest().getRuleId();

        StringBuffer sqlbuf = new StringBuffer("select * from dcp_cardrwrule where eid='"+req.geteId()+"' and ruleid='"+ruleId+"'");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getQuerySql_shop(DCP_CardRwRuleDetailReq req) throws Exception
    {
        String sql = null;
        String ruleId=req.getRequest().getRuleId();

        StringBuffer sqlbuf = new StringBuffer("select a.*,b.org_name from dcp_cardrwrule_shop a " +
                                                       "left join dcp_org_lang b on a.eid=b.eid and a.shopid=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                                                       "where a.eid='"+req.geteId()+"' and a.ruleid='"+ruleId+"' " +
                                                       "order by a.SERIALNO ");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getQuerySql_params(DCP_CardRwRuleDetailReq req,String mediaType) throws Exception
    {
        String sql = null;
        String ruleId=req.getRequest().getRuleId();

        StringBuffer sqlbuf = new StringBuffer("select a.*,b.groupname_cn,b.groupname_tw,b.groupname_en from DCP_CARDRWPARAM a " +
                                                       "left join DCP_CARDRWPARAM_GROUP b on a.groupid=b.groupid " +
                                                       "where 1=1 " );

        if(mediaType.equals("RF"))
        {
            sqlbuf.append("and a.rfuse='Y' " );
        }
        else
        {
            sqlbuf.append("and a.icuse='Y' " );
        }

        sqlbuf.append("order by a.groupid,a.sortid ");

        sql = sqlbuf.toString();
        return sql;
    }


}
