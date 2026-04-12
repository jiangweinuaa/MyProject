package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CardReaderTypeQueryReq;
import com.dsc.spos.json.cust.res.DCP_CardReaderTypeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_CardReaderTypeQuery
 * 服务说明：读卡器类型查询
 * @author wangzyc 
 * @since  2020-12-8
 */
public class DCP_CardReaderTypeQuery extends SPosBasicService<DCP_CardReaderTypeQueryReq,DCP_CardReaderTypeQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_CardReaderTypeQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_CardReaderTypeQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_CardReaderTypeQueryReq>(){};
	}

	@Override
	protected DCP_CardReaderTypeQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_CardReaderTypeQueryRes();
	}

	@Override
	protected DCP_CardReaderTypeQueryRes processJson(DCP_CardReaderTypeQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_CardReaderTypeQueryRes res = null;
		res = this.getResponse();
		try{
			String sql = null;
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_CardReaderTypeQueryRes.level1Elm>());
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
				for (Map<String, Object> oneData : getQDataDetail) {
					DCP_CardReaderTypeQueryRes.level1Elm  oneLv1 = res.new level1Elm();
					// 取出数据
					String readerType = oneData.get("READERTYPE").toString();
					String readerName = oneData.get("READERNAME").toString();
					String mediaType = oneData.get("MEDIATYPE").toString();
					String baud = oneData.get("BAUD").toString();
					String port = oneData.get("PORT").toString();
					String sortId = oneData.get("SORTID").toString();
					String memo = oneData.get("MEMO").toString();
					String status = oneData.get("STATUS").toString();
					
					// 设置响应
					oneLv1.setReaderType(readerType);
					oneLv1.setReaderName(readerName);
					oneLv1.setMediaType(mediaType);
					oneLv1.setBaud(baud);
					oneLv1.setPort(port);
					oneLv1.setSortId(sortId);
					oneLv1.setMemo(memo);
					oneLv1.setStatus(status);
					res.getDatas().add(oneLv1);
				}
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) {
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected String getQuerySql(DCP_CardReaderTypeQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql = null;
		String keyTxt = req.getRequest().getKeyTxt();
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT READERTYPE, READERNAME,MEDIATYPE, BAUD, PORT, SORTID , MEMO, status FROM DCP_CARDREADERTYPE  " +
				" where 1=1");
		if(!Check.Null(keyTxt)){
			sqlbuf.append("and (READERTYPE like '%%"+keyTxt+"%%' or READERNAME like '%%"+keyTxt+"%%')");
		}
		sqlbuf.append(" ORDER BY SORTID");
		sql = sqlbuf.toString();
		return sql;
	}

}
