package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_POrderERPEcsflgReq;
import com.dsc.spos.json.cust.res.DCP_POrderERPEcsflgRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_POrderERPEcsflg
 * 服务说明：要货单结案（ERP下发）
 * @author jinzma
 * @since  2023-07-03
 */
public class DCP_POrderERPEcsflg extends SPosAdvanceService<DCP_POrderERPEcsflgReq, DCP_POrderERPEcsflgRes> {
    @Override
    protected void processDUID(DCP_POrderERPEcsflgReq req, DCP_POrderERPEcsflgRes res) throws Exception {
        try{
            String eId = req.geteId();
            String shopId = req.getShopId();
            String loadDocNo = req.getLoadDocNo();
            
            //【ID1036035】【潮品-3.0】要货单结案传中台报错：对应的要货单不存在  by jinzma 20230915
            //检查loadDocNo
            //【ID1037062】【潮品-3.0】要货单在T100已经结案了，但中台对应的要货单状态没变成结案。 by jinzma 20231103
            String sql = " select status,porderno from dcp_porder "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' "
                    + " and (process_erp_no='"+loadDocNo+"' or ofno='"+loadDocNo+"') "
                    + " and otype='7' " ;
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (!CollectionUtils.isEmpty(getQData)){
                String porderNo = getQData.get(0).get("PORDERNO").toString();
                
                //修改 DCP_PORDER
                UptBean ub1 = new UptBean("DCP_PORDER");
                ub1.addUpdateValue("STATUS",new DataValue("7", Types.VARCHAR));  //状态status 7-已结案 龙海让我这样干的，我们很多作业判断条件都是用2
                
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("PORDERNO", new DataValue(porderNo, Types.VARCHAR));
                
                this.addProcessData(new DataProcessBean(ub1));
            }
            
            this.doExecuteDataToDB();
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);
            
            pw.flush();
            pw.close();
            
            errors.flush();
            errors.close();
            
            this.pData.clear();
            
            //修改失败
            res.setSuccess(false);
            res.setServiceStatus("000");
            res.setServiceDescription(e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_POrderERPEcsflgReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_POrderERPEcsflgReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_POrderERPEcsflgReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_POrderERPEcsflgReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if (Check.Null(req.getShopId())){
            errMsg.append("门店(shop_no)不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getLoadDocNo())){
            errMsg.append("来源单号(load_doc_no)不可为空值, ");
            isFail = true;
        }
        
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_POrderERPEcsflgReq> getRequestType() {
        return new TypeToken<DCP_POrderERPEcsflgReq>(){};
    }
    
    @Override
    protected DCP_POrderERPEcsflgRes getResponseType() {
        return new DCP_POrderERPEcsflgRes();
    }
}
