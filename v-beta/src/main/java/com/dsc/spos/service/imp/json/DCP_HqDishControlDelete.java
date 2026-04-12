package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HqDishControlDeleteReq;
import com.dsc.spos.json.cust.res.DCP_HqDishControlDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_HqDishControlDelete extends SPosAdvanceService<DCP_HqDishControlDeleteReq, DCP_HqDishControlDeleteRes>
{


    @Override
    protected void processDUID(DCP_HqDishControlDeleteReq req, DCP_HqDishControlDeleteRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        DCP_HqDishControlDeleteReq.level1Elm request = req.getRequest();
        List<DCP_HqDishControlDeleteReq.level2Elm> itemList=request.getItemList();

        for (DCP_HqDishControlDeleteReq.level2Elm level2Elm : itemList)
        {
            DelBean db1 = new DelBean("DCP_HQKDSDISHES_CONTROL");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("GOODSTYPE", new DataValue(level2Elm.getGoodsType(), Types.VARCHAR));
            db1.addCondition("ID", new DataValue(level2Elm.getId(), Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HqDishControlDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HqDishControlDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HqDishControlDeleteReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_HqDishControlDeleteReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_HqDishControlDeleteReq.level1Elm request = req.getRequest();
        List<DCP_HqDishControlDeleteReq.level2Elm> itemList=request.getItemList();

        if (itemList==null || itemList.size()==0)
        {
            errMsg.append("itemList不可为空值, ");
            isFail = true;
        }
        else
        {
            for (DCP_HqDishControlDeleteReq.level2Elm level2Elm : itemList)
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

            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_HqDishControlDeleteReq> getRequestType()
    {
        return new TypeToken<DCP_HqDishControlDeleteReq>(){};
    }

    @Override
    protected DCP_HqDishControlDeleteRes getResponseType()
    {
        return new DCP_HqDishControlDeleteRes();
    }

}
