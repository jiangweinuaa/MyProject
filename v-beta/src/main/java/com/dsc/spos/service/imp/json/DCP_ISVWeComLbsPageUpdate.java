package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComLbsPageUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComLbsPageUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;


/**
 * 服务函数：DCP_ISVWeComLbsPageUpdate
 * 服务说明：中台LBS页面通用设置更新
 * @author jinzma
 * @since  2024-03-14
 */
public class DCP_ISVWeComLbsPageUpdate extends SPosAdvanceService<DCP_ISVWeComLbsPageUpdateReq, DCP_ISVWeComLbsPageUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComLbsPageUpdateReq req, DCP_ISVWeComLbsPageUpdateRes res) throws Exception {

        try{

            String eId = req.geteId();
            String pageType = req.getRequest().getPageType();
            boolean isNew = true;
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            //资料检查
            {
                String sql = "select * from dcp_isvwecom_lbspage where eid='"+eId+"' and pagetype='"+pageType+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (!CollectionUtil.isEmpty(getQData)) {
                    isNew = false;
                }
            }
            if (isNew){

                //DCP_ISVWECOM_LBSPAGE 新增
                String[] columns = {"EID","PAGETYPE","NAME","TEXT1","TEXT2","BACKGROUND","USERID","CREATETIME"};

                InsBean ib = new InsBean("DCP_ISVWECOM_LBSPAGE", columns);
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(pageType, Types.VARCHAR),
                        new DataValue(req.getRequest().getName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getText1(), Types.VARCHAR),
                        new DataValue(req.getRequest().getText2(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBackground(), Types.VARCHAR),
                        new DataValue(req.getRequest().getUserId(), Types.VARCHAR),
                        new DataValue(sDate, Types.DATE),
                };
                ib.addValues(insValue);

                this.addProcessData(new DataProcessBean(ib));


            }else{
                // DCP_ISVWECOM_LBSPAGE 修改
                UptBean ub = new UptBean("DCP_ISVWECOM_LBSPAGE");
                ub.addUpdateValue("NAME", new DataValue(req.getRequest().getName(), Types.VARCHAR));
                ub.addUpdateValue("TEXT1", new DataValue(req.getRequest().getText1(), Types.VARCHAR));
                ub.addUpdateValue("TEXT2", new DataValue(req.getRequest().getText2(), Types.VARCHAR));
                ub.addUpdateValue("BACKGROUND", new DataValue(req.getRequest().getBackground(), Types.VARCHAR));
                ub.addUpdateValue("USERID", new DataValue(req.getRequest().getUserId(), Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME", new DataValue(sDate, Types.DATE));


                //条件
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("PAGETYPE", new DataValue(pageType, Types.VARCHAR));


                this.addProcessData(new DataProcessBean(ub));

            }


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");



        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComLbsPageUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComLbsPageUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComLbsPageUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComLbsPageUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getPageType())) {
                errMsg.append("pageType不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getName())) {
                errMsg.append("name不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getText1())) {
                errMsg.append("text1不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getText2())) {
                errMsg.append("text2不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getBackground())) {
                errMsg.append("background不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComLbsPageUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComLbsPageUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWeComLbsPageUpdateRes getResponseType() {
        return new DCP_ISVWeComLbsPageUpdateRes();
    }
}
