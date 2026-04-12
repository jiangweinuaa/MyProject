package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BefUserInfoQueryReq;
import com.dsc.spos.json.cust.res.DCP_BefUserInfoQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class DCP_BefUserInfoQuery extends SPosAdvanceService<DCP_BefUserInfoQueryReq, DCP_BefUserInfoQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BefUserInfoQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_BefUserInfoQueryReq> getRequestType() {
        return new TypeToken<DCP_BefUserInfoQueryReq>(){};
    }
    
    @Override
    protected DCP_BefUserInfoQueryRes getResponseType() {
        return new DCP_BefUserInfoQueryRes();
    }
    
    @Override
    protected void processDUID(DCP_BefUserInfoQueryReq req, DCP_BefUserInfoQueryRes res) throws Exception {
        
        //单头总数
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        int totalRecords; // 总笔数
        int totalPages;
        res.setDatas(new ArrayList<>());
        if(getQData!=null && !getQData.isEmpty()) {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            // 算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            
            for (Map<String, Object> oneData1 : getQData) {
                DCP_BefUserInfoQueryRes.level1Elm lv1=res.new level1Elm();
                lv1.seteId(oneData1.get("EID").toString());
                lv1.setOpName(oneData1.get("OP_NAME").toString());
                lv1.setOpNo(oneData1.get("OPNO").toString());
                lv1.setStatus(oneData1.get("STATUS").toString());
                res.getDatas().add(lv1);
            }
        } else {
            totalRecords = 0;
            totalPages = 0;
        }
        
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_BefUserInfoQueryReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_BefUserInfoQueryReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_BefUserInfoQueryReq req) throws Exception {
        return null;
    }
    
    @Override
    protected String getQuerySql(DCP_BefUserInfoQueryReq req) throws Exception {
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        String eId    = req.getRequest().geteId();

        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" "
                + " Select * From ("
                + " ");
            sqlbuf.append("SELECT count(*) over() as num,ROWNUM rn,A.EID,A.OPNO,B.OP_NAME,A.STATUS "
            		+ " FROM PLATFORM_STAFFS A  "
                    + " LEFT JOIN PLATFORM_STAFFS_LANG B ON A.EID=B.EID AND A.OPNO=B.OPNO AND B.LANG_TYPE='"+req.getLangType()+"' "
                    + " WHERE 1=1 ");
            if(!Check.Null(eId))
            {
            	sqlbuf.append(" AND A.EID='"+eId+"' " );
            }
            
            if (status != null && status.length()>0) {
                sqlbuf.append( " AND A.status= "+status);
            }             
        if (keyTxt != null && keyTxt.length()>0) {
            sqlbuf.append( " AND (A.OPNO like '%"+keyTxt+"%' or B.OP_NAME like '%"+keyTxt+"%'  ) ");
        }
        sqlbuf.append("ORDER BY A.EID,A.OPNO,B.OP_NAME ");
        sqlbuf.append(")tbl where rn > " + startRow + " AND rn <= " + (startRow+pageSize) );
        return sqlbuf.toString();
    }
}
