package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_JobDetailSetUpdateReq;
import com.dsc.spos.json.cust.req.DCP_JobDetailSetUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_JobDetailSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * JOB 执行时间设置 
 * 设置JOB 执行时间点， 如 12：00：00 ， 20：00：00
 * 不同于轮询时间
 * @author yuanyy 2019-06-24 
 *
 */
public class DCP_JobDetailSetUpdate extends SPosAdvanceService<DCP_JobDetailSetUpdateReq, DCP_JobDetailSetUpdateRes> {

	@Override
	protected void processDUID(DCP_JobDetailSetUpdateReq req, DCP_JobDetailSetUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
			
		String jobName = req.getRequest().getJobName();
		List<level1Elm> getDatas = req.getRequest().getDatas();

        //先删除原来单身
        DelBean db1 = new DelBean("JOB_QUARTZ_DETAIL");
        db1.addCondition("JOB_NAME", new DataValue(jobName, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        if (getDatas!=null&&getDatas.size()>0)
        {
            for (level1Elm lv1 : getDatas) {
                int insColCt = 0;
                String item = lv1.getItem();
                String beginTime = lv1.getBeginTime();
                String endTime = lv1.getEndTime();

                String[] columnsName = {
                        "JOB_NAME", "ITEM", "BEGIN_TIME" , "END_TIME" ,"STATUS"
                };
                DataValue[] columnsVal = new DataValue[columnsName.length];
                for (int i = 0; i < columnsVal.length; i++) {
                    String keyVal = null;
                    switch (i) {
                        case 0:
                            keyVal = jobName;
                            break;
                        case 1:
                            keyVal = item;
                            break;
                        case 2:
                            keyVal = beginTime;
                            break;
                        case 3:
                            keyVal = endTime;
                            break;
                        case 4:
                            keyVal = "100";
                            break;
                        default:
                            break;
                    }

                    if (keyVal != null) {
                        insColCt++;

                        columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);

                    }
                    else {
                        columnsVal[i] = null;
                    }
                }

                String[] columns2  = new String[insColCt];
                DataValue[] insValue2 = new DataValue[insColCt];
                // 依照傳入參數組譯要insert的欄位與數值；
                insColCt = 0;

                for (int i=0;i<columnsVal.length;i++){
                    if (columnsVal[i] != null){
                        columns2[insColCt] = columnsName[i];
                        insValue2[insColCt] = columnsVal[i];
                        insColCt ++;
                        if (insColCt >= insValue2.length)
                            break;
                    }
                }
                InsBean ib2 = new InsBean("JOB_QUARTZ_DETAIL", columns2);
                ib2.addValues(insValue2);
                this.addProcessData(new DataProcessBean(ib2));

            }
        }

        this.doExecuteDataToDB();

        if (res.isSuccess())
        {
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }

    }

	@Override
	protected List<InsBean> prepareInsertData(DCP_JobDetailSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_JobDetailSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_JobDetailSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_JobDetailSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    
	    //必传值不为空
	    String jobName = req.getRequest().getJobName();
	    
	    if(Check.Null(jobName)){
			errCt++;
			errMsg.append("任务名称不可为空值！！！ ");
		  	isFail = true;
		}
	    
	    if (isFail){
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }    
	    return isFail;
	}

	@Override
	protected TypeToken<DCP_JobDetailSetUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_JobDetailSetUpdateReq>(){};
	}

	@Override
	protected DCP_JobDetailSetUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_JobDetailSetUpdateRes();
	}

}
