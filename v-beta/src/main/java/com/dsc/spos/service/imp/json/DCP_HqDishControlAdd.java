package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HqDishControlAddReq;
import com.dsc.spos.json.cust.res.DCP_HqDishControlAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_HqDishControlAdd extends SPosAdvanceService<DCP_HqDishControlAddReq, DCP_HqDishControlAddRes>
{

    @Override
    protected void processDUID(DCP_HqDishControlAddReq req, DCP_HqDishControlAddRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        DCP_HqDishControlAddReq.level1Elm request = req.getRequest();
        String opId = request.getOpId();
        List<DCP_HqDishControlAddReq.level2Elm> itemList=request.getItemList();

        for (DCP_HqDishControlAddReq.level2Elm level2Elm : itemList)
        {
            String sql = "select * from DCP_HQKDSDISHES_CONTROL  where EID = '"+eId+"' and ID='"+ level2Elm.getId()+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && getQData.isEmpty() == false)
            {

                String tips=level2Elm.getGoodsType().equals("1")?"品类:":"商品:";
                res.setSuccess(false);
                res.setServiceStatus("100");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, tips+level2Elm.getId()+"已存在！");
            }

            String[] columns = {"EID", "ID", "NAME","GOODSTYPE","UNSIDE","UNCOOK","UNCALL"
                    ,"LASTMODIOPID","LASTMODITIME"};
            DataValue[] insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(level2Elm.getId(), Types.VARCHAR),
                    new DataValue(level2Elm.getName(), Types.VARCHAR),
                    new DataValue(level2Elm.getGoodsType(), Types.VARCHAR),
                    new DataValue(level2Elm.getUnSide(), Types.VARCHAR),
                    new DataValue(level2Elm.getUnCook(), Types.VARCHAR),
                    new DataValue(level2Elm.getUnCall(), Types.VARCHAR),
                    new DataValue(opId, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };

            InsBean ib1 = new InsBean("DCP_HQKDSDISHES_CONTROL", columns);
            ib1.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }



    @Override
    protected List<InsBean> prepareInsertData(DCP_HqDishControlAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HqDishControlAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HqDishControlAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_HqDishControlAddReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_HqDishControlAddReq.level1Elm request = req.getRequest();
        String opId = request.getOpId();
        List<DCP_HqDishControlAddReq.level2Elm> itemList=request.getItemList();

        if (itemList==null || itemList.size()==0)
        {
            errMsg.append("itemList不可为空值, ");
            isFail = true;
        }
        else
        {
            for (DCP_HqDishControlAddReq.level2Elm level2Elm : itemList)
            {
                if (Check.Null(level2Elm.getGoodsType()))
                {
                    errMsg.append("goodsType不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getId()))
                {
                    errMsg.append("id不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getName()))
                {
                    errMsg.append("name不可为空值, ");
                    isFail = true;
                }

            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_HqDishControlAddReq> getRequestType()
    {
        return new TypeToken<DCP_HqDishControlAddReq>(){};
    }

    @Override
    protected DCP_HqDishControlAddRes getResponseType()
    {
        return new DCP_HqDishControlAddRes();
    }


}
