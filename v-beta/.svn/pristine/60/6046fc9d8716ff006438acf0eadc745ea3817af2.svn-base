package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComLbsPageQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComLbsPageQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComLbsPageQueryRes.Datas;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;


import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComLbsPageQuery
 * 服务说明：中台LBS页面通用设置查询
 * @author jinzma
 * @since  2024-03-14
 */
public class DCP_ISVWeComLbsPageQuery extends SPosBasicService<DCP_ISVWeComLbsPageQueryReq, DCP_ISVWeComLbsPageQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComLbsPageQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getPageType())){
                errMsg.append("pageType不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComLbsPageQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComLbsPageQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComLbsPageQueryRes getResponseType() {
        return new DCP_ISVWeComLbsPageQueryRes();
    }

    @Override
    protected DCP_ISVWeComLbsPageQueryRes processJson(DCP_ISVWeComLbsPageQueryReq req) throws Exception {
        DCP_ISVWeComLbsPageQueryRes res = this.getResponse();
        Datas datas = res.new Datas();
        String eId = req.geteId();
        try{
            String pageType = req.getRequest().getPageType();

            String sql = " select a.*,b.opname from dcp_isvwecom_lbspage a"
                    + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.userid=b.userid"
                    + " where a.eid='"+eId+"' and a.pagetype='"+pageType+"' ";
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getQData)){

                ////图片地址参数获取
                String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                String httpStr = isHttps.equals("1") ? "https://" : "http://";
                String domainName = PosPub.getPARA_SMS(dao, eId, "", "DomainName");
                String imagePath = httpStr + domainName + "/resource/image/";
                if (domainName.endsWith("/")) {
                    imagePath = httpStr + domainName + "resource/image/";
                }

                datas.setPageType(getQData.get(0).get("PAGETYPE").toString());
                datas.setName(getQData.get(0).get("NAME").toString());
                datas.setText1(getQData.get(0).get("TEXT1").toString());
                datas.setText2(getQData.get(0).get("TEXT2").toString());
                datas.setBackground(getQData.get(0).get("BACKGROUND").toString());
                datas.setBackgroundUrl(imagePath+getQData.get(0).get("BACKGROUND").toString());
                datas.setUserId(getQData.get(0).get("USERID").toString());
                datas.setUserName(getQData.get(0).get("OPNAME").toString());
            }


            res.setDatas(datas);
            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComLbsPageQueryReq req) throws Exception {
        return "";
    }
}
