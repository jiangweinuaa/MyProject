package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsStuffCreateDCPReq;
import com.dsc.spos.json.cust.req.DCP_GoodsStuffCreateDCPReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsStuffCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 新增 统一加料信息
 * @author yuanyy
 *
 */
public class DCP_GoodsStuffCreate extends SPosAdvanceService<DCP_GoodsStuffCreateDCPReq, DCP_GoodsStuffCreateRes> {

	@Override
	protected void processDUID(DCP_GoodsStuffCreateDCPReq req, DCP_GoodsStuffCreateRes res) throws Exception {
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
        PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,req.geteId());

	    // TODO Auto-generated method stub
		String sql = null;
		try 
		{
			//定义一个字符串 用来 记录没有插入的加料编码；  如果中间某一条不能插入， 记录下来，不影响后面的加料信息继续录入
			String stuffNOList = "";
			String eId = req.geteId(); 
			String getMaxPrioritySql = this.getMaxPriority( eId);
			String[] conditions = {}; //查詢條件
			List<Map<String, Object>> maxPriorityList = this.doQueryData(getMaxPrioritySql,conditions);
			String maxPriority = "1";
			if(maxPriorityList !=null &&  !maxPriorityList.isEmpty())
			{					
				maxPriority = maxPriorityList.get(0).get("PRIORITY").toString();
			}
			String priority = "0";
			List<level1Elm> datas = req.getRequest().getDatas();
			int k = 0 ;
			for (level1Elm par : datas) {
				int insColCt = 0;
				String[] columnsName = {
						"STUFFNO","STUFFNAME","PRIORITY","STATUS","EID" 
				};
				DataValue[] columnsVal = new DataValue[columnsName.length];
				String stuffNO = par.getStuffNo();
				String stuffName = par.getStuffName();

				sql = this.isRepeat(stuffNO, eId);
				List<Map<String, Object>> stuffDatas = this.doQueryData(sql, null);

				if(stuffDatas.isEmpty()){
					if( k == 0){
						priority = maxPriority; // i==0 时获取最大priority ，以后的自动加 1 即可 
					}
					else{
						int nextPriority = Integer.parseInt(priority) + 1 ;
						priority = String.valueOf(nextPriority); 
					}

					for (int i = 0; i < columnsVal.length; i++) { 
						String keyVal = null;
						switch (i) { 
						case 0:
							keyVal = stuffNO;
							break;
						case 1:
							keyVal = stuffName;
							break;
						case 2:
							keyVal = priority;
							break;
						case 3:
							keyVal = "100"; 
							break;
						case 4:  
							keyVal = eId;
							break;
						default:
							break;

						}
						if (keyVal != null) 
						{
							insColCt++;
							columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
						} 
						else 
						{
							columnsVal[i] = null;
						}

					}
					String[] columnsDetail = new String[insColCt];
					DataValue[] insValueDetail = new DataValue[insColCt];

					insColCt = 0;

					for (int i = 0; i < columnsVal.length; i++) 
					{
						if (columnsVal[i] != null) 
						{
							columnsDetail[insColCt] = columnsName[i];
							insValueDetail[insColCt] = columnsVal[i];
							insColCt++;
							if (insColCt >= insValueDetail.length)
								break;
						}
					}

					k = k+1;
					InsBean ib2 = new InsBean("DCP_STUFF", columnsDetail);
					ib2.addValues(insValueDetail);
					this.addProcessData(new DataProcessBean(ib2));	

				}
				else
				{
					stuffNOList = stuffNOList +"  "+ stuffNO  ;
					continue;
				}

			}

			if(stuffNOList.length() > 0){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"编码为" +stuffNOList+" 的加料信息 已存在，请勿重复添加");
			}

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsStuffCreateDCPReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsStuffCreateDCPReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsStuffCreateDCPReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsStuffCreateDCPReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		List<level1Elm> datas = req.getRequest().getDatas();

		for(level1Elm par : datas){

			if (Check.Null(par.getStuffNo())) 
			{
				errMsg.append("加料编码不能为空值 ");
				isFail = true;
			}

		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsStuffCreateDCPReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsStuffCreateDCPReq>(){};
	}

	@Override
	protected DCP_GoodsStuffCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsStuffCreateRes();
	}

	/**
	 * 判断商品加料信息是否已存在或重复
	 * @param pluNO
	 * @param stuffNO
	 * @param eId
	 * @return
	 */
	private String isRepeat( String stuffNO , String eId ){
		String sql = null;
		sql = "select * from DCP_STUFF "
				+ " where  stuffNO = '"+stuffNO +"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}

	/**
	 * 
	 * @param eId
	 * @return
	 */
	protected String getMaxPriority(String eId)
	{ 
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select NVL (MAX(priority)+1, 1  ) AS priority  from DCP_STUFF "
				+ "WHERE EID= '"+eId +"'");

		sql = sqlbuf.toString();

		return sql;	
	}



}
