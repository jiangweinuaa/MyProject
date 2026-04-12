package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StockChannelOrderQueryReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelOrderQueryReq.ChannelList;
import com.dsc.spos.json.cust.res.DCP_StockChannelOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

/**
 * 渠道库存分配单据列表查询
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelOrderQuery extends SPosBasicService<DCP_StockChannelOrderQueryReq, DCP_StockChannelOrderQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_StockChannelOrderQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelOrderQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelOrderQueryReq>(){};
	}

	@Override
	protected DCP_StockChannelOrderQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelOrderQueryRes();
	}

	@Override
	protected DCP_StockChannelOrderQueryRes processJson(DCP_StockChannelOrderQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_StockChannelOrderQueryRes res = null;
		res = this.getResponse();
		
		try {
			
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

            Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
            condition1.put("EID", true);
            condition1.put("ORGANIZATIONNO", true);
            condition1.put("BILLNO", true);
            condition1.put("BILLTYPE", true);
            condition1.put("CHANNELID", true);
            //调用过滤函数
            List<Map<String, Object>> datas= MapDistinct.getMap(queryDatas, condition1);
			
			int totalRecords = 0;
			int totalPages = 0;
			
			DCP_StockChannelOrderQueryRes.levelRes lvRes = res.new  levelRes();
			
			lvRes.setBillList( new ArrayList<DCP_StockChannelOrderQueryRes.BillList>());
			if(queryDatas.size() > 0 && !queryDatas.isEmpty()){
				
				String num = queryDatas.get(0).get("NUM").toString();

				if(req.getPageSize() != 0 && req.getPageNumber() != 0){
					totalRecords=Integer.parseInt(num);
					totalPages = totalRecords / req.getPageSize();
					totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				}
				
				for (Map<String, Object> map : datas) {
					DCP_StockChannelOrderQueryRes.BillList lvBill = res.new BillList();
					String billNo = map.get("BILLNO").toString();
					String channelId = map.get("CHANNELID").toString();
					String channelName = map.getOrDefault("CHANNELNAME","").toString();
//					String totAmt = map.getOrDefault("TOTAMT","0").toString();
					
					String totCQty = map.get("TOTCQTY").toString();
					String totPQty = map.get("TOTPQTY").toString();
					String createOpId = map.get("CREATEOPID").toString();
					String createOpName = map.get("CREATEOPNAME").toString();
					String createTime = map.get("CREATETIME").toString();
					String memo = map.get("MEMO").toString();
					String dealType = map.get("DEALTYPE") == null? "0": map.get("DEALTYPE").toString() ;
					
					lvBill.setBillNo(billNo);
					lvBill.setChannelId(channelId);
					lvBill.setChannelName(channelName);
//					lvBill.setTotAmt(totAmt);
					lvBill.setTotCQty(totCQty);
					lvBill.setTotPQty(totPQty);
					lvBill.setCreateOpId(createOpId);
					lvBill.setCreateOpName(createOpName);
					lvBill.setCreateTime(createTime);
					lvBill.setMemo(memo);
					lvBill.setDealType(dealType);
					
					lvRes.getBillList().add(lvBill);
					
				}
				
			}
			
			res.setDatas(lvRes);
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
			
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StockChannelOrderQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
				
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		if(pageNumber ==0 || pageSize == 0 ){
			pageNumber = 1;
			pageSize = 99999;
		}
		
		// 計算起啟位置
		int startRow = (pageNumber - 1) * pageSize;
		String langType = req.getLangType();
		
		String billNo = req.getRequest().getBillNo();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String keyTxt = req.getRequest().getKeyTxt();
        List<ChannelList> channelList = req.getRequest().getChannelList();
        String ChannelStr ="";
		if(!CollectionUtils.isEmpty(channelList)){
            String[] ChannelArr = new String[channelList.size()] ;
            int i=0;
            for (ChannelList lv1 : req.getRequest().getChannelList())
            {
                String ChannelId = "";
                if(!Check.Null(lv1.getChannelId())){
                    ChannelId = lv1.getChannelId();
                }
                ChannelArr[i] = ChannelId;
                i++;
            }

            ChannelStr = getString(ChannelArr);
        }

		
		sqlbuf.append(""
				+ " SELECT * from ( "
				+ " SELECT "
				+ " count(distinct a.billNo ) OVER() AS NUM,  dense_rank() over (order BY  a.billno ) rn, a.*,d.channelName "
				+ " FROM DCP_stock_channel_Bill a " +
                " LEFT JOIN DCP_STOCK_CHANNEL_BILLGOODS b " +
                " ON a.BILLNO  = b.BILLNO AND a.EID  = b.EID AND a.BILLTYPE  = b.BILLTYPE AND a.CHANNELID  = b.CHANNELID " +
                " LEFT JOIN DCP_GOODS_LANG c ON a.EID = b.EID AND b.PLUNO  = c.PLUNO  AND c.LANG_TYPE ='"+req.getLangType()+"'  " +
                " LEFT JOIN Crm_Channel d ON a.eid = d.eid AND a.channelid = d.channelid"
				+ " WHERE a.eId = '"+eId+"' "  );
		
		/**
		 * + " AND a.channelid IN ('1' , '2') 
				+ " AND ( a.billNO LIKE '%%%单号%%%'   )
				+ " AND  to_date( to_char(a.createTime   ,'yyyy-MM-dd' ), 'YYYY-MM-dd' )  BETWEEN   to_Date('20200501', 'yyyy-MM-dd')  AND  to_Date('20200501', 'yyyy-MM-dd') 
				+ " ORDER BY a.createtime DESC 
		 */
		
		if(!Check.Null(ChannelStr) && ChannelStr.length() >2 && !ChannelStr.toUpperCase().equals("'ALL'") ){
			sqlbuf.append(" AND a.channelid in("+ ChannelStr +") ");
		}
		
		if(!Check.Null(keyTxt)){
			sqlbuf.append(" AND ( b.pluno  like  '%%"+ keyTxt +"%%' OR c.PLU_NAME like '%%"+keyTxt+"%%' ) ");
		}
		
		if(!Check.Null(billNo)){
			sqlbuf.append(" AND   a.billNo  = '"+ billNo +"'   ");
		}

		if(!Check.Null(beginDate) && !Check.Null(endDate)){
			sqlbuf.append(" AND  to_date( to_char(a.createTime ,'yyyy-MM-dd' ), 'YYYY-MM-dd' )  BETWEEN   to_Date('"+beginDate+"', 'yyyy-MM-dd')  AND  to_Date('"+endDate+"', 'yyyy-MM-dd') ");
		}
		sqlbuf.append(" order by a.createTime desc , a.channelId  "
				+ " )  t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) +""
				+ " order by createTime desc , channelId ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	
	
	protected String getString(String[] str)
	{
		String str2 = "";
		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		return str2;
	}
}
