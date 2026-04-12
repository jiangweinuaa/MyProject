package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComJsApiTicketQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComJsApiTicketQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComJsApiTicketQueryRes.Datas;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComJsApiTicketQuery
 * 服务说明：查询 jsapi_ticket
 * @author jinzma
 * @since  2024-03-18
 */
public class DCP_ISVWeComJsApiTicketQuery extends SPosBasicService<DCP_ISVWeComJsApiTicketQueryReq, DCP_ISVWeComJsApiTicketQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComJsApiTicketQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComJsApiTicketQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComJsApiTicketQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComJsApiTicketQueryRes getResponseType() {
        return new DCP_ISVWeComJsApiTicketQueryRes();
    }

    @Override
    protected DCP_ISVWeComJsApiTicketQueryRes processJson(DCP_ISVWeComJsApiTicketQueryReq req) throws Exception {
        DCP_ISVWeComJsApiTicketQueryRes res = this.getResponse();
        Datas datas = res.new Datas();
        try {

            String sql = " select agentid from dcp_isvwecom_empower ";
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (CollectionUtils.isNotEmpty(getQData)){
                String agentId = getQData.get(0).get("AGENTID").toString();
                if (!Check.Null(agentId)){
                    datas.setAgentId(agentId);

                    //调企微接口获取jsapi_ticket
                    DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                    String jsapi_ticket = dcpWeComUtils.get_jsapi_ticket(dao);
                    datas.setJsApiTicket(jsapi_ticket);

                }
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
    protected String getQuerySql(DCP_ISVWeComJsApiTicketQueryReq req) throws Exception {
        return null;
    }
}
