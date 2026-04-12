package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_DinnerDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DinnerDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

public class DCP_DinnerDelete extends SPosAdvanceService<DCP_DinnerDeleteReq,DCP_DinnerDeleteRes>
{

	@Override
	protected void processDUID(DCP_DinnerDeleteReq req, DCP_DinnerDeleteRes res) throws Exception 
	{

        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        String apiUserCode = "";
        String apiUserKey = "";
        if (result != null && result.size() == 2) {
            for (Map<String, Object> map : result) {
                if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                    apiUserCode = map.get("ITEMVALUE").toString();
                } else {
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }
        PosPub.clearTableBaseInfoCache(posUrl, apiUserCode, apiUserKey,req.geteId(),req.getShopId());

        List<String> dinnerNos = req.getRequest().getDinnerNo();
        String eId = req.geteId();
        String shopId = req.getShopId();

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT DINNERNO,ORGANIZATIONNO,EID,PRIORITY,DINNERGROUP " +
                " FROM DCP_DINNERTABLE " +
                " WHERE EID = '"+eId+"' AND ORGANIZATIONNO  = '"+shopId+"'");
        sqlbuf.append(" AND  DINNERNO in ( ");
        for (String dinnerNo : dinnerNos) {
            sqlbuf.append(" '" + dinnerNo + "' ,");
        }
        sqlbuf.deleteCharAt(sqlbuf.length() - 1);
        sqlbuf.append(" ) ");
        List<Map<String, Object>> doQueryData = this.doQueryData(sqlbuf.toString(), null);

        if(!CollectionUtils.isEmpty(doQueryData)){
            for (Map<String, Object> doQueryDatum : doQueryData) {
                String dinnerno = doQueryDatum.get("DINNERNO").toString();
                String dinnergroup = doQueryDatum.get("DINNERGROUP").toString();
                String priority = doQueryDatum.get("PRIORITY").toString();

                //DCP_DINNERTABLE
                DelBean db1 = new DelBean("DCP_DINNERTABLE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("DINNERNO", new DataValue(dinnerno, Types.VARCHAR));
                db1.addCondition("DINNERGROUP", new DataValue(dinnergroup, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1)); //

                UptBean ub1 = new UptBean("DCP_DINNERTABLE");
                ub1.addCondition("PRIORITY",new DataValue( priority, Types.VARCHAR,DataExpression.GreaterEQ));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("DINNERGROUP", new DataValue(dinnergroup, Types.VARCHAR));
                ub1.addUpdateValue("PRIORITY",new DataValue(1,Types.INTEGER,DataExpression.SubSelf));
                this.addProcessData(new DataProcessBean(ub1));


                //DCP_DINNERTABLE_MEAL
                DelBean db1_meal = new DelBean("DCP_DINNERTABLE_MEAL");
                db1_meal.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1_meal.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                db1_meal.addCondition("DINNERNO", new DataValue(dinnerno, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(db1_meal)); //


            }
            this.doExecuteDataToDB();
        }
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DinnerDeleteReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerDeleteReq req) throws Exception 
	{
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        List<String> dinnerNos = req.getRequest().getDinnerNo();

        if (CollectionUtils.isEmpty(dinnerNos))
        {
            errMsg.append("桌台号数组不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
	}

	@Override
	protected TypeToken<DCP_DinnerDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_DinnerDeleteReq>(){};
	}

	@Override
	protected DCP_DinnerDeleteRes getResponseType() 
	{
		return new DCP_DinnerDeleteRes();
	}



}
