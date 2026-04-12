package com.dsc.spos.service.imp.json;

import cn.hutool.core.date.DateUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsExtUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsExtUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: xingdong
 * @time: 2021/1/21 下午5:31
 */
public class DCP_GoodsExtUpdate extends SPosAdvanceService<DCP_GoodsExtUpdateReq, DCP_GoodsExtUpdateRes> {

    @Override
    protected void processDUID(DCP_GoodsExtUpdateReq req, DCP_GoodsExtUpdateRes res) throws Exception {
        String sql =null;
        String pluNo = req.getRequest().getPluNo();
        String eId = req.geteId();
        String lastmoditime = DateUtil.format(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        //校验是否存在该商品描述
        try
        {
            sql = isRepeat(pluNo,eId);
            List<Map<String, Object>> goodExtMap = this.doQueryData(sql, null);
            if(goodExtMap.isEmpty()){

                String[] columns_hm ={"EID","PLUNO","DESCRIPTION","LASTMODITIME"
                    };
                    DataValue[] insValue_hm = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(req.getRequest().getDescription(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE)
                            };

                    InsBean ib_hm = new InsBean("DCP_GOODS_EXT", columns_hm);
                    ib_hm.addValues(insValue_hm);
                    this.addProcessData(new DataProcessBean(ib_hm));

                    this.doExecuteDataToDB();
                }else{

                    UptBean ub1 = null;
                    ub1 = new UptBean("DCP_GOODS_EXT");
                    //add Value
                    ub1.addUpdateValue("DESCRIPTION", new DataValue(req.getRequest().getDescription(), Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

                    //condition
                    ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));
                    this.doExecuteDataToDB();
                }
                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功");

            }
                catch (Exception e)
                {
                    // TODO: handle exception
                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription("服务执行异常:"+e.getMessage());

                }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_GoodsExtUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_GoodsExtUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_GoodsExtUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_GoodsExtUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String pluNo = req.getRequest().getPluNo();

        if(Check.Null(pluNo)){
            errMsg.append("商品编号不能为空 ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_GoodsExtUpdateReq> getRequestType() {
        return new TypeToken<DCP_GoodsExtUpdateReq>(){};
    }

    @Override
    protected DCP_GoodsExtUpdateRes getResponseType() {
        return new DCP_GoodsExtUpdateRes();
    }

    /**
     * 判断描述是否存在
     * @param pluNO
     * @param eId
     * @return
     */
    private  String isRepeat(String pluNO , String eId){
        String sql = null;
        sql = "select * from DCP_GOODS_EXT "
                + " where pluNO = '"+pluNO +"' "
                + " and EID = '"+eId+"'";
        return sql;
    }
}
