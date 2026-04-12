package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_EstClearGoodsUpdateReq;
import com.dsc.spos.json.cust.req.DCP_EstClearGoodsUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_EstClearGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_EstClearGoodsUpdate extends SPosAdvanceService<DCP_EstClearGoodsUpdateReq,DCP_EstClearGoodsUpdateRes>
{

	@Override
	protected void processDUID(DCP_EstClearGoodsUpdateReq req, DCP_EstClearGoodsUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId=req.geteId();
		String shopId=req.getShopId();
		String comtime= new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
		String isclear="Y";
		if(req.getDocType().equals("0"))
		{
			isclear="Y";
		}
		else
		{
			isclear="N";
		}
		for (level1Elm goods : req.getDatas()) 
		{
			String pluno=goods.getPluno();
			//存在即更新，不存在就插入
		  //开始插入DCP_GOODS_CLEAR表
			String mergesql=" merge into DCP_GOODS_CLEAR T1 "
				+ " using( select "+eId+" EID ,"+shopId+" SHOPID ,"+pluno+" PLUNO from dual ) T2 "
				+ " on (T1.EID=T2.EID and T1.SHOPID=T2.SHOPID and T1.PLUNO=T2.PLUNO )  "
				+ " when matched then "
				+ " update set T1.ISCLEAR = '"+isclear+"',T1.UPDATE_TIME='"+comtime+"' "
				+ " when not matched then  "
				+ " INSERT (EID,SHOPID,PLUNO,FEATURENO,UNIT,QTY,STATUS,ISCLEAR  ) VALUES "
				+ " ('"+eId+"','"+shopId+"','"+pluno+"',' ','"+""+"','','100','"+isclear+"'  )  ";
			ExecBean exb=new ExecBean(mergesql);
			this.pData.add(new DataProcessBean(exb));
	  }
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_EstClearGoodsUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_EstClearGoodsUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_EstClearGoodsUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_EstClearGoodsUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_EstClearGoodsUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_EstClearGoodsUpdateReq>(){};
	}

	@Override
	protected DCP_EstClearGoodsUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_EstClearGoodsUpdateRes();
	}

}


