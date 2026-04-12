package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DualPlayTimeQueryReq;
import com.dsc.spos.json.cust.res.DCP_DualPlayTimeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;


/**
 * 服務函數：DualPlayGetDCP
 *   說明：双屏播放查询DCP
 * 服务说明：双屏播放查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayTimeQuery extends SPosBasicService<DCP_DualPlayTimeQueryReq,DCP_DualPlayTimeQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_DualPlayTimeQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String dualPlayID = req.getRequest().getDualPlayID();
		if(Check.Null(dualPlayID)){
			errMsg.append("双屏播放ID不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_DualPlayTimeQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DualPlayTimeQueryReq>(){};
	}

	@Override
	protected DCP_DualPlayTimeQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DualPlayTimeQueryRes();
	}

	@Override
	protected DCP_DualPlayTimeQueryRes processJson(DCP_DualPlayTimeQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_DualPlayTimeQueryRes res = null;
		res = this.getResponse();

		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_DualPlayTimeQueryRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_DualPlayTimeQueryRes.level1Elm oneLv1 = res.new level1Elm();
					
					String item = oneData.get("ITEM").toString();
					String lbDate = oneData.get("LBDATE").toString();
					String leDate = oneData.get("LEDATE").toString();
					String lbTime = oneData.get("LBTIME").toString();
					String leTime = oneData.get("LETIME").toString();
					String status = oneData.get("STATUS").toString();

					//设置响应
					oneLv1.setItem(item);
					oneLv1.setLbDate(lbDate);
					oneLv1.setLeDate(leDate);
					oneLv1.setLbTime(lbTime);
					oneLv1.setLeTime(leTime);
					oneLv1.setStatus(status);

					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DualPlayTimeQueryReq req) throws Exception {
		String sql=null;			
		String eId = req.geteId();
    String dualPlayID = req.getRequest().getDualPlayID();
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( 
				" select ITEM,LBDATE,LEDATE,LBTIME,LETIME,status from DCP_DUALPLAY_TIME "
						+ "where EID='"+eId+"' and DUALPLAYID = '"+dualPlayID+"' "
							+ "order by item  " ) ;

		sql = sqlbuf.toString();
		return sql;

	}

}


