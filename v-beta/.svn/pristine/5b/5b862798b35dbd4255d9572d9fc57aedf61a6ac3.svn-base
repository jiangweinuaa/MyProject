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
import com.dsc.spos.json.cust.req.DCP_TagDetailAddReq;
import com.dsc.spos.json.cust.req.DCP_TagDetailAddReq.levelTypeDetail;
import com.dsc.spos.json.cust.res.DCP_TagDetailAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

public class DCP_TagDetailAdd extends SPosAdvanceService<DCP_TagDetailAddReq,DCP_TagDetailAddRes> {
	
	@Override
	protected void processDUID(DCP_TagDetailAddReq req, DCP_TagDetailAddRes res) throws Exception {
		
		try {
			String eId = req.geteId();
			String tagNo = req.getRequest().getTagNo();
			String tagGroupType = req.getRequest().getTagGroupType();
			String tagGroupNo = req.getRequest().getTagGroupNo();
			List<levelTypeDetail> idList = req.getRequest().getIdList();
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			String[] columns = {
					"EID", "TAGGROUPTYPE", "TAGGROUPNO", "TAGNO", "ID", "NAME", "LASTMODITIME","ONLILASORTING"
			};
			for (levelTypeDetail detail : idList) {
				//【ID1033446】【3.0 中台标签添加】添加重复的商品，报错优化  by jinzma 20230530
				String sql = " select id from dcp_tagtype_detail "   //主键 EID, TAGGROUPTYPE, TAGNO, ID
						+ " where eid='"+eId+"' and taggrouptype='"+tagGroupType+"' and tagno='"+tagNo+"' and id='"+detail.getId()+"' ";
				List<Map<String , Object>> getQData=this.doQueryData(sql, null);
				if (!CollectionUtils.isEmpty(getQData)){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, detail.getId()+" "+detail.getName() + "已存在,无法新增");
				}
				
				//【ID1033123】【标准产品3.0】商品标签及商城副标题展示---中台服务  by jinzma 20230530
				String onlilasorting = "";
				if (tagGroupType.equals("GOODS")){
					onlilasorting = "1";
					sql = " select max(onlilasorting) as onlilasorting from dcp_tagtype_detail "
							+ " where eid='"+eId+"' and taggrouptype='GOODS' and id='"+detail.getId()+"' ";
					getQData=this.doQueryData(sql, null);
					if (PosPub.isNumeric(getQData.get(0).get("ONLILASORTING").toString())){
						onlilasorting = String.valueOf( Integer.parseInt(getQData.get(0).get("ONLILASORTING").toString())+1);
					}
				}
				
				DataValue[] insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(tagGroupType, Types.VARCHAR),
						new DataValue(tagGroupNo, Types.VARCHAR),
						new DataValue(tagNo, Types.VARCHAR),
						new DataValue(detail.getId(), Types.VARCHAR),
						new DataValue(detail.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE),
						new DataValue(onlilasorting, Types.VARCHAR)
				};
				
				InsBean ib = new InsBean("DCP_TAGTYPE_DETAIL", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib));
			}
			
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			
		}catch (Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_TagDetailAddReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_TagDetailAddReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_TagDetailAddReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_TagDetailAddReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest() == null) {
			isFail = true;
			errMsg.append("request不能为空 ");
		}else {
			List<levelTypeDetail> idList = req.getRequest().getIdList();
			if (Check.Null(req.getRequest().getTagGroupType())) {
				errMsg.append("标签类型不能为空值 ");
				isFail = true;
			}
			if (Check.Null(req.getRequest().getTagNo())) {
				errMsg.append("标签编码不能为空值 ");
				isFail = true;
			}
			if (Check.Null(req.getRequest().getTagGroupNo())) {
				errMsg.append("标签组别不能为空值 ");
				isFail = true;
			}
			if (CollectionUtils.isEmpty(idList)){
				errMsg.append("资料列表不能为空值 ");
				isFail = true;
			}else {
				for (levelTypeDetail par : idList) {
					if (Check.Null(par.getId())) {
						errMsg.append("资料编号不能为空值 ");
						isFail = true;
					}
					if (Check.Null(par.getName())) {
						errMsg.append("资料名称不能为空值 ");
						isFail = true;
					}
					
					if (isFail) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
					}
				}
			}
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_TagDetailAddReq> getRequestType() {
		return new TypeToken<DCP_TagDetailAddReq>(){};
	}
	
	@Override
	protected DCP_TagDetailAddRes getResponseType() {
		return new DCP_TagDetailAddRes();
	}
	
	
	
	
}
