package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CardRwParamQueryReq;
import com.dsc.spos.json.cust.res.DCP_CardRwParamQueryRes;
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

public class DCP_CardRwParamQuery extends SPosBasicService<DCP_CardRwParamQueryReq, DCP_CardRwParamQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_CardRwParamQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String mediaType=req.getRequest().getMediaType();

        if(Check.Null(mediaType))
        {
            errMsg.append("mediaType不能为空值， ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CardRwParamQueryReq> getRequestType()
    {
        return new TypeToken<DCP_CardRwParamQueryReq>(){};
    }

    @Override
    protected DCP_CardRwParamQueryRes getResponseType()
    {
        return new DCP_CardRwParamQueryRes();
    }

    @Override
    protected DCP_CardRwParamQueryRes processJson(DCP_CardRwParamQueryReq req) throws Exception
    {
        DCP_CardRwParamQueryRes res=this.getResponseType();
        res.setDatas(new ArrayList<>());

        String sql = null;
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

        Map<String, Boolean> cond=new HashMap<>();
        cond.put("GROUPID",true);

        List<Map<String, Object>> getHeader=MapDistinct.getMap(getQDataDetail,cond);
        for (Map<String, Object> head : getHeader)
        {
            DCP_CardRwParamQueryRes.level1Elm lv1=res.new level1Elm();
            lv1.setGroupId(head.get("GROUPID").toString());
            if (req.getLangType().equals("zh_TW"))
            {
                lv1.setGroupName(head.get("GROUPNAME_TW").toString());
            }
            else if (req.getLangType().equals("zh_EN"))
            {
                lv1.setGroupName(head.get("GROUPNAME_EN").toString());
            }
            else
            {
                lv1.setGroupName(head.get("GROUPNAME_CN").toString());
            }

            lv1.setParamList(new ArrayList<>());

            for (Map<String, Object> detail : getQDataDetail)
            {
                if (detail.get("GROUPID").toString().equals(lv1.getGroupId()))
                {
                    DCP_CardRwParamQueryRes.level2Elm lv2=res.new level2Elm();
                    lv2.setAlterateValue(detail.get("ALTERATEVALUE").toString());
                    lv2.setConType(detail.get("CONTYPE").toString());
                    lv2.setCurValue("");
                    lv2.setDefValue(detail.get("DEFVALUE").toString());
                    lv2.setMemo(detail.get("MEMO").toString());
                    if (req.getLangType().equals("zh_TW"))
                    {
                        lv2.setName(detail.get("NAME_TW").toString());
                    }
                    else if (req.getLangType().equals("zh_EN"))
                    {
                        lv2.setName(detail.get("NAME_EN").toString());
                    }
                    else
                    {
                        lv2.setName(detail.get("NAME_CN").toString());
                    }
                    lv2.setParam(detail.get("PARAM").toString());
                    lv2.setSortId(detail.get("SORTID").toString());
                    lv1.getParamList().add(lv2);
                }
            }
            res.getDatas().add(lv1);
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
    protected String getQuerySql(DCP_CardRwParamQueryReq req) throws Exception
    {
        String sql = null;
        String mediaType=req.getRequest().getMediaType();

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
