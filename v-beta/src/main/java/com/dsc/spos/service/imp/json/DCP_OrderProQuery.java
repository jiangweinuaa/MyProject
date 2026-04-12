package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.json.cust.req.DCP_OrderProQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderProQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.checkerframework.checker.units.qual.A;

public class DCP_OrderProQuery extends SPosBasicService<DCP_OrderProQueryReq,DCP_OrderProQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderProQueryReq req) throws Exception
	{
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderProQueryReq> getRequestType()
	{
		return new TypeToken<DCP_OrderProQueryReq>(){};
	}

	@Override
	protected DCP_OrderProQueryRes getResponseType()
	{
		return new DCP_OrderProQueryRes();
	}

	@Override
	protected DCP_OrderProQueryRes processJson(DCP_OrderProQueryReq req) throws Exception
	{
		DCP_OrderProQueryRes res=this.getResponse();

		String sqlOrder=getQuerySql(req);		
		List<Map<String, Object>> getOrder = this.doQueryData(sqlOrder, null);

		res.setDatas(res.new level1Elm());
		res.getDatas().setProList(new ArrayList<DCP_OrderProQueryRes.pros>());

		if (getOrder!=null && getOrder.isEmpty()==false)
		{
			for (Map<String, Object> map : getOrder)
			{
				DCP_OrderProQueryRes.pros pro=res.new pros();

				String startDate=map.get("STARTDATE").toString();
				String endDate=map.get("ENDDATE").toString();
				if (startDate.length()>=8)startDate=startDate.substring(0,8);			
				if (endDate.length()>=8)endDate=endDate.substring(0,8);

				pro.setStartDate(startDate);
				pro.setEndDate(endDate);
				pro.setProName(map.get("PRONAME").toString());
				pro.setQty(map.get("QTY").toString());

				res.getDatas().getProList().add(pro);
			}			
		}


		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderProQueryReq req) throws Exception
	{
		String eId=req.geteId();

		String orgType = req.getOrg_Form();
		String orgNo = req.getOrganizationNO();
		String sql_condition = "";
		String sql_leftjoin = "";
        Calendar calendar = Calendar.getInstance();
        String sdate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
        calendar.add(Calendar.MONTH,-2);//2月前的订单
        String dateStartFormat = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime())+"000000000";

        String createDate_sql = " and A.create_datetime>='"+dateStartFormat+"'";
        String AbnormalOrderEffectDays = PosPub.getPARA_SMS(this.dao,eId,"","AbnormalOrderEffectDays");
        String errorCountDate_sql = "";
        if (AbnormalOrderEffectDays!=null&&!AbnormalOrderEffectDays.trim().isEmpty())
		{
			try {
				int spwnDays = Integer.parseInt(AbnormalOrderEffectDays);
				//大于0.才有意义
				if (spwnDays>0)
				{
					calendar = Calendar.getInstance();
					calendar.add(Calendar.DATE,0-spwnDays);//2月前的订单
					String bdate_format = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
					errorCountDate_sql = " and A.BDATE>='"+bdate_format+"'";
				}


			}
			catch (Exception e)
			{

			}
		}

        //没有设置，就默认之前逻辑。
        if (errorCountDate_sql.isEmpty())
		{
			errorCountDate_sql = createDate_sql;
		}

        if (orgType.equals("0"))//公司
		{
		    sql_leftjoin = " left join dcp_org g1 on g1.eid=a.eid and g1.ORGANIZATIONNO=a.shop "
                    + " left join dcp_org g2 on g2.eid=a.eid and g2.ORGANIZATIONNO=a.shippingshop "
                    + " left join dcp_org g3 on g3.eid=a.eid and g3.ORGANIZATIONNO=a.machshop ";

			sql_condition = " and A.billtype='1' and (A.BELFIRM='"+orgNo+"'"
                          + " or a.shop='"+orgNo+"' or a.MACHSHOP='"+orgNo+"' or a.SHIPPINGSHOP='"+orgNo+"' "
                          + " or g1.BELFIRM='"+orgNo+"' or g2.BELFIRM='"+orgNo+"' or g3.BELFIRM='"+orgNo+"' "
                          + " or A.BELFIRM is null or A.BELFIRM='')";
		}
		else if (orgType.equals("2"))//门店
		{
			sql_condition = " and A.billtype='1' and (A.shop='"+orgNo+"' or A.MACHSHOP='"+orgNo+"' or A.SHIPPINGSHOP='"+orgNo+"') ";
		}

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer("");
		/*StringBuffer sqlbuf=new StringBuffer(""
				+ "select QTY,min(create_datetime) STARTDATE,max(create_datetime) ENDDATE, 'schedulingCount' PRONAME from " //待审核 
				+ "( "
				+ "select count(*) over() QTY,A.ORDERNO,A.create_datetime from dcp_order A  "
				+ "where create_datetime>= "
				+ "( "
				+ "select min(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.status='0' and A.EID=B.EID and A.STATUS=B.STATUS "
				+ ") "
				+ "and create_datetime<= "
				+ "( "
				+ "select max(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.status='0' and A.EID=B.EID and A.STATUS=B.STATUS "
				+ ") "
				+ ") group by QTY "

				+ "union all "

				+ "select QTY,min(create_datetime) STARTDATE,max(create_datetime) ENDDATE, 'applyRefundCount' PRONAME from " //申请退单
				+ "( "
				+ "select count(*) over() QTY,A.ORDERNO,A.create_datetime from dcp_order A  "
				+ "where create_datetime>= "
				+ "( "
				+ "select min(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.Refundstatus='2' and A.EID=B.EID and A.STATUS=B.STATUS "
				+ ") "
				+ "and create_datetime<= "
				+ "( "
				+ "select max(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.Refundstatus='2' and A.EID=B.EID and A.STATUS=B.STATUS "
				+ ") "
				+ ") group by QTY "

				+ "union all "

                + "select QTY,min(create_datetime) STARTDATE,max(create_datetime) ENDDATE, 'errorCount' PRONAME from " //异常订单
                + "( "
                + "select count(*) over() QTY,A.ORDERNO,A.create_datetime from dcp_order A  "
                + "where create_datetime>= "
                + "( "
                + "select min(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.exceptionStatus='Y' and A.EID=B.EID and A.STATUS=B.STATUS "
                + ") "
                + "and create_datetime<= "
                + "( "
                + "select max(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.exceptionStatus='Y' and A.EID=B.EID and A.STATUS=B.STATUS "
                + ") "
                + ") group by QTY "

                + "union all "

                + "select QTY,min(create_datetime) STARTDATE,max(create_datetime) ENDDATE, 'proRejectCount' PRONAME from " //生产拒单
                + "( "
                + "select count(*) over() QTY,A.ORDERNO,A.create_datetime from dcp_order A  "
                + "where create_datetime>= "
                + "( "
                + "select min(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.productStatus='5' and A.EID=B.EID and A.STATUS=B.STATUS "
                + ") "
                + "and create_datetime<= "
                + "( "
                + "select max(create_datetime) from dcp_order B where  B.eid='"+eId+"' and B.productStatus='5' and A.EID=B.EID and A.STATUS=B.STATUS "
                + ") "
                + ") group by QTY ");*/
		//待审核
		sqlbuf.append(" select 'schedulingCount' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where A.status='0'  and A.EID='"+eId+"' "+createDate_sql +sql_condition );
		
		//待处理
		sqlbuf.append(" union all ");
		sqlbuf.append(" select 'errorCount' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where A.exceptionStatus='Y'  and A.EID='"+eId+"' "+errorCountDate_sql +sql_condition );
		
		//申请退单
		sqlbuf.append(" union all ");
		sqlbuf.append(" select 'applyRefundCount' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where (A.Refundstatus='2' or A.Refundstatus='7' or A.Refundstatus='21' or A.Refundstatus='22') and  A.status<>'12' and A.status<>'3'   and A.EID='"+eId+"' "+createDate_sql +sql_condition );
		
		//拒绝出货 deliveryStatus：-2拒绝出货
		sqlbuf.append(" union all ");
		sqlbuf.append(" select 'rejectStockOut' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where A.deliverystatus='-2'  and A.EID='"+eId+"' "+createDate_sql +sql_condition );

		//未打印unPrintCount，目前只放在门店显示，总部返回空
         if (orgType.equals("2"))
        {
			/*Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH,-2);//2月前的订单
			String dateStartFormat = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime())+"000000000";
*/
            sqlbuf.append(" union all ");
            sqlbuf.append(" select 'unPrintCount' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where A.PRINTCOUNT=0  and A.EID='"+eId+"' "+createDate_sql +sql_condition );

            //订单状态  status =1 订单开立 或  status = 2 已接单且生产状态productStatus = 6 //带调拨
            //且生产门店=当前门店且配送门店不等于当前门店
            sqlbuf.append(" union all ");
            sqlbuf.append(" select 'orderTransferCount' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where  (A.status='1' or A.status='2') and  A.productStatus='6' and A.MACHSHOP='"+orgNo+"'  and  a.SHIPPINGSHOP<>'"+orgNo+"'  and A.EID='"+eId+"' "+createDate_sql +sql_condition );

            //订单状态  status =1 订单开立 或  status = 2 已接单且生产状态productStatus = 4 待完工
            //且生产门店=当前门店
            sqlbuf.append(" union all ");
            sqlbuf.append(" select 'orderProductionFinishedCount' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where  (A.status='1' or A.status='2') and  A.productStatus='4' and A.MACHSHOP='"+orgNo+"' and A.EID='"+eId+"' "+createDate_sql +sql_condition );

            //订单状态  status =1 订单开立 或  status = 2 已接单且生产状态productStatus = 为空 待生产
            //且生产门店=当前门店且配送日期=当天
            sqlbuf.append(" union all ");
            sqlbuf.append(" select 'orderNeedProductionCount' PRONAME,min(A.create_datetime) STARTDATE,max(A.create_datetime) ENDDATE,count(*) QTY from DCP_ORDER A "+sql_leftjoin+" where  (A.status='1' or A.status='2') and  A.productStatus is null and A.MACHSHOP='"+orgNo+"' and A.SHIPDATE='"+sdate+"' and A.EID='"+eId+"' "+createDate_sql +sql_condition );
        }
         else
         {
             sqlbuf.append(" union all ");
             sqlbuf.append(" select 'unPrintCount' PRONAME,N'' STARTDATE,N'' ENDDATE,0  QTY from dual" );

             sqlbuf.append(" union all ");
             sqlbuf.append(" select 'orderTransferCount' PRONAME,N'' STARTDATE,N'' ENDDATE,0  QTY from dual" );

             sqlbuf.append(" union all ");
             sqlbuf.append(" select 'orderProductionFinishedCount' PRONAME,N'' STARTDATE,N'' ENDDATE,0  QTY from dual" );

             sqlbuf.append(" union all ");
             sqlbuf.append(" select 'orderNeedProductionCount' PRONAME,N'' STARTDATE,N'' ENDDATE,0  QTY from dual" );
         }
		sql = sqlbuf.toString();
		return sql;		
	}




}
