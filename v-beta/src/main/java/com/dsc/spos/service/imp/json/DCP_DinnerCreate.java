package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_DinnerCreateReq;
import com.dsc.spos.json.cust.res.DCP_DinnerCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_DinnerCreate extends SPosAdvanceService<DCP_DinnerCreateReq, DCP_DinnerCreateRes> {

    @Override
    protected void processDUID(DCP_DinnerCreateReq req, DCP_DinnerCreateRes res) throws Exception 
    {
         String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    	 String eId = req.geteId();
        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(eId);
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
        PosPub.clearTableBaseInfoCache(posUrl, apiUserCode, apiUserKey,eId,req.getShopId());
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String mySysTime = df.format(cal.getTime());
        String dinnerGroup = req.getRequest().getDinnerGroup();
       
        String shopId = req.getShopId();

        String sql = null;
        sql = this.getDINNERNO_SQL(req);

        List<Map<String, Object>> getQData_check = this.doQueryData(sql, null);

        String maxPrioritySql = " SELECT  nvl(MAX(priority) + 1 , '1') AS maxPriority FROM DCP_DINNERTable "
                + " WHERE EID = '" + eId + "' AND organizationNo = '" + shopId + "'  AND dinnerGroup = '" + dinnerGroup + "'  ";

        List<Map<String, Object>> priorityDatas = this.doQueryData(maxPrioritySql, null);
        String maxPriority = "1";

        if (priorityDatas != null && priorityDatas.size() > 0) {
            maxPriority = priorityDatas.get(0).get("MAXPRIORITY").toString();
        }

        if (getQData_check == null || getQData_check.isEmpty()) {

            //DCP_DINNERTABLE
            String[] columnsModular =
                    {
                            "EID",
                            "ORGANIZATIONNO",
                            "DINNERGROUP",
                            "DINNERNO",
                            "GUESTNUM",
                            "PRIORITY",
                            "TEAPLUNO",
                            "STATUS",
                            "UPDATE_TIME",
                            "DINNERCLASS",

                            "TISSUEPLUNO",
                            "TISSUEQTY",
                            "RICEPLUNO",
                            "RICEQTY",
                            "TEAQTY",
                            "USETYPE",
                            "MAXGUESTNUM"
                    };

            String tissueQty = req.getRequest().getTissueQty() == null ? "0" : req.getRequest().getTissueQty();
            String teaQty = req.getRequest().getTeaQty() == null ? "0" : req.getRequest().getTeaQty();
            String riceQty = req.getRequest().getRiceQty() == null ? "0" : req.getRequest().getRiceQty();
            String useType = req.getRequest().getUseType() == null ? "0" : req.getRequest().getUseType();
            int  maxGuestNum= req.getRequest().getMaxGuestNum();

            DataValue[] insValue1 = null;

            insValue1 = new DataValue[]
                    {
                            new DataValue(req.geteId(), Types.VARCHAR),
                            new DataValue(req.getShopId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getDinnerGroup(), Types.VARCHAR),
                            new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getGuestNum(), Types.VARCHAR),
                            new DataValue(maxPriority, Types.VARCHAR),
                            new DataValue(req.getRequest().getTeaPluNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getStatus(), Types.VARCHAR),
                            new DataValue(mySysTime, Types.VARCHAR),
                            new DataValue(req.getRequest().getDinnerClass(), Types.VARCHAR),

                            new DataValue(req.getRequest().getTissuePluNo(), Types.VARCHAR),
                            new DataValue(tissueQty, Types.VARCHAR),
                            new DataValue(req.getRequest().getRicePluNo(), Types.VARCHAR),
                            new DataValue(riceQty, Types.VARCHAR),
                            new DataValue(teaQty, Types.VARCHAR),
                            new DataValue(useType, Types.VARCHAR),
                            new DataValue(maxGuestNum, Types.INTEGER),
                    };

            UptBean ub1 = new UptBean("DCP_DINNERTABLE");
            ub1.addCondition("PRIORITY", new DataValue(maxPriority, Types.VARCHAR, DataExpression.GreaterEQ));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            ub1.addCondition("DINNERGROUP", new DataValue(req.getRequest().getDinnerGroup(), Types.VARCHAR));

            ub1.addUpdateValue("PRIORITY", new DataValue(1, Types.INTEGER, DataExpression.UpdateSelf));
            this.addProcessData(new DataProcessBean(ub1));


            InsBean ib1 = new InsBean("DCP_DINNERTABLE", columnsModular);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            //DCP_DINNERTABLE_MEAL默认菜品
            List<DCP_DinnerCreateReq.levelElmMeal> mealList=req.getRequest().getMealList();
            if (mealList != null)
            {
                String[] columnsMeal =
                        {
                                "EID",
                                "ORGANIZATIONNO",
                                "DINNERNO",
                                "PLUNO",
                                "UNITID",
                                "QTY",
                                "DEFMODE",
                                "LASTMODIOPID",
                                "LADTMODITIME"
                        };
                for (DCP_DinnerCreateReq.levelElmMeal Meal : mealList)
                {
                    DataValue[] insMealValue1 = new DataValue[]
                            {
                                    new DataValue(req.geteId(), Types.VARCHAR),
                                    new DataValue(req.getShopId(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR),
                                    new DataValue(Meal.getPluNo(), Types.VARCHAR),
                                    new DataValue(Meal.getUnitId(), Types.VARCHAR),
                                    new DataValue(Meal.getQty(), Types.VARCHAR),
                                    new DataValue(Meal.getDefMode(), Types.VARCHAR),
                                    new DataValue(req.getOpNO(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE),
                            };

                    InsBean ib1_meal = new InsBean("DCP_DINNERTABLE_MEAL", columnsMeal);
                    ib1_meal.addValues(insMealValue1);
                    this.addProcessData(new DataProcessBean(ib1_meal)); // 新增單頭
                }
            }


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("桌台号已经存在！");
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DinnerCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DinnerCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DinnerCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DinnerCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        String DINNERNO = req.getRequest().getDinnerNo();
        String DINNERGROUP = req.getRequest().getDinnerGroup();
        String GUESTNUM = req.getRequest().getGuestNum();
//		String PRIORITY =req.getPriority();
        String status = req.getRequest().getStatus();

        if (Check.Null(DINNERNO)) {
            errMsg.append("桌台号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(DINNERGROUP)) {
            errMsg.append("所属区域不可为空值, ");
            isFail = true;
        }

        if (Check.Null(GUESTNUM)) {
            errMsg.append("座位数不可为空值, ");
            isFail = true;
        }

//		if (Check.Null(PRIORITY)) 
//		{
//			errMsg.append("优先级排序不可为空值, ");
//			isFail = true;
//		} 

        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DinnerCreateReq> getRequestType() {
        return new TypeToken<DCP_DinnerCreateReq>() {
        };
    }

    @Override
    protected DCP_DinnerCreateRes getResponseType() {
        return new DCP_DinnerCreateRes();
    }


    protected String getDINNERNO_SQL(DCP_DinnerCreateReq req) {
        String sql = null;

        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("SELECT * FROM DCP_DINNERTABLE WHERE EID='" + req.geteId() + "' AND ORGANIZATIONNO='" + req.getShopId() + "' AND DINNERNO='" + req.getRequest().getDinnerNo() + "' ");

        sql = sqlbuf.toString();

        return sql;
    }


}
