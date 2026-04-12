package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerTagGroupCreateReq;
import com.dsc.spos.json.cust.req.DCP_CustomerTagGroupCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_CustomerTagGroupCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 客户标签
 * @author yuanyy
 *
 */
public class DCP_CustomerTagGroupCreate extends SPosAdvanceService<DCP_CustomerTagGroupCreateReq, DCP_CustomerTagGroupCreateRes> {

	@Override
	protected void processDUID(DCP_CustomerTagGroupCreateReq req, DCP_CustomerTagGroupCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		try {
			String tagGroupNo = req.getRequest().getTagGroupNo();
			String tagGroupName = req.getRequest().getTagGroupName();
			String mutualExclusion = req.getRequest().getMutualExclusion();
			String status = req.getRequest().getStatus();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime =  matter.format(dt);
			
			boolean isRepeat = false;
			isRepeat = this.checkRepeat(eId, tagGroupNo);
			
			if(isRepeat){
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("标签组编码"+tagGroupNo+"已存在！");
			}else{
				
				List<level2Elm> children = req.getRequest().getChildren();
				if(children != null && children.size() > 0){
					
					for (level2Elm lv2 : children) {
						String tagNo = lv2.getTagNo();
						String tagName = lv2.getTagName();
						String deStatus = lv2.getStatus();
						
						String[] columns_hms ={"EID","TAGGROUPNO","TAGNO","TAGNAME","STATUS","CREATETIME","LASTMODITIME"};
						DataValue[] insValue_hms = new DataValue[] 
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(tagGroupNo, Types.VARCHAR), 
							new DataValue(tagNo, Types.VARCHAR),
							new DataValue(tagName, Types.VARCHAR),
							new DataValue(deStatus, Types.VARCHAR),
							new DataValue(createTime, Types.DATE),
							new DataValue(createTime, Types.DATE)
							
						};
						
						InsBean ib_hms = new InsBean("DCP_CUSTOMERTAGGROUP_DETAIL", columns_hms);
						ib_hms.addValues(insValue_hms);
						this.addProcessData(new DataProcessBean(ib_hms));
						
					}
				}
	
				String[] columns_hm ={"EID","TAGGROUPNO","TAGGROUPNAME","MUTUALEXCLUSION","STATUS" ,
						"CREATETIME","LASTMODITIME"
				};
				DataValue[] insValue_hm = new DataValue[] 
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(tagGroupNo, Types.VARCHAR), 
								new DataValue(tagGroupName, Types.VARCHAR),
								new DataValue(mutualExclusion, Types.VARCHAR),
								new DataValue(status ,Types.VARCHAR),
								new DataValue(createTime , Types.DATE),
								new DataValue(createTime, Types.DATE)
						};
				
				InsBean ib_hm = new InsBean("DCP_CUSTOMERTAGGROUP", columns_hm);
				ib_hm.addValues(insValue_hm);
				this.addProcessData(new DataProcessBean(ib_hm)); 
				
				this.doExecuteDataToDB();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！");
			res.setSuccess(false);
			res.setServiceStatus("200");
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CustomerTagGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CustomerTagGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CustomerTagGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CustomerTagGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String tagGroupNo = req.getRequest().getTagGroupNo();
		String mutualExclusion = req.getRequest().getMutualExclusion();

		if (Check.Null(tagGroupNo)) {
			errCt++;
			errMsg.append("标签组编码不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(mutualExclusion)) {
			errCt++;
			errMsg.append("是否组内互斥不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_CustomerTagGroupCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CustomerTagGroupCreateReq>(){};
	}

	@Override
	protected DCP_CustomerTagGroupCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CustomerTagGroupCreateRes();
	}
	
	/**
	 * 验证标签组是否已存在
	 * @param eId
	 * @param tagGroupNo
	 * @return
	 * @throws Exception 
	 */
	private boolean checkRepeat(String eId, String tagGroupNo) throws Exception{
		String sql = "";
	    boolean temp = false;
		sql = "select * from DCP_CUSTOMERTAGGROUP "
			+ " where  EID = '"+eId+"' and tagGroupNo = '"+tagGroupNo+"' ";
		
		List<Map<String, Object>> reDatas = this.doQueryData(sql, null);
		if(reDatas != null && reDatas.size() > 0){
			temp = true;
		}
		
		return temp;
	}
	
	
	
}
