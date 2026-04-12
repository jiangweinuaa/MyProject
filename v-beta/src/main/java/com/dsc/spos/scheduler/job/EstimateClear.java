package com.dsc.spos.scheduler.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.utils.MapDistinct;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EstimateClear extends InitJob{
	Logger logger = LogManager.getLogger(EstimateClear.class.getName());
	static boolean bRun = false;// 标记此服务是否正在执行中
	
	public String doExe() throws Exception {
	  // 此服务是否正在执行中
		if (bRun) {
			logger.info("\r\n*********估清EstimateClear正在执行中,本次调用取消:************\r\n");
			return "";
		}
		bRun = true;//
		logger.info("\r\n*********估清EstimateClear正在执行中,本次调用开始:************\r\n");
		
		SimpleDateFormat simdate=new SimpleDateFormat("yyyyMMdd");
		String sdate=simdate.format(Calendar.getInstance().getTime());
		
		//先取TB_BOOM表和TB_BOOM_SHOP表
		String sqlshop = "select * from DCP_ORG where ORG_FORM='2' AND status='100'  ";
		List<Map<String, Object>> slshop = this.doQueryData(sqlshop, null);
		for (Map<String, Object> shopdetail : slshop) {
		  // 这里重新定义listdate用于一次事务执行
			List<DataProcessBean> data = new ArrayList<DataProcessBean>();
			
			String eId = shopdetail.get("EID").toString();
			String eShopId = shopdetail.get("ORGANIZATIONNO").toString();
			//查询boom 表和   boomshop表
			String boomlist=getMaterialpstockinSQL(eId, eShopId, sdate);
			List<Map<String, Object>> boomdatalist=this.doQueryData(boomlist, null);
			if(boomdatalist!=null&&!boomdatalist.isEmpty())
			{
				//查询本店实时库存
				String sskc=GetPLNEW_SQL( eId,  eShopId, sdate, sdate);
				List<Map<String, Object>> listkc=this.doQueryData(sskc, null);
				if(boomdatalist!=null&&!boomdatalist.isEmpty())
				{
					//先通过PLUNO过滤一下boom表
				  //单头主键字段
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
					condition.put("PLUNO", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader = MapDistinct.getMap(boomdatalist, condition);
					//开始循环getQHeader
					for (Map<String, Object> map : getQHeader) 
					{
						//开始从所有的boom表中找到pluno的boom表
						String pluno=map.get("PLUNO").toString();
						String wunit=map.get("WUNIT").toString();
						//先查一下主商品的库存，如果有库存说明可以继续卖
						String spqty="0";
						List<Map<String, Object>> kcheadlist=getpluboom(pluno, listkc);
						if(kcheadlist!=null&&!kcheadlist.isEmpty())
						{
							 spqty = kcheadlist.get(0).get("PQTY").toString();
						}
						double pqty=Double.parseDouble(spqty);
						
						List<Map<String, Object>> pluboomlist=getpluboom(pluno, boomdatalist);
						
						//可生产数量
						double canpqty=0;
						int i=0;
						for (Map<String, Object> map2 : pluboomlist) 
						{
							//开始查找取得的子商品的库存
							double detailcanqty=0;
							String detailpluno=map2.get("MATERIAL_PLUNO").toString();
							List<Map<String, Object>> kcdetaillist=getpluboom(detailpluno, listkc);
							if(kcdetaillist!=null&&!kcdetaillist.isEmpty())
							{
								String kcdetail=kcdetaillist.get(0).get("WQTY").toString();
								double dkcdetail=Double.parseDouble(kcdetail);
								//生产成品所需要的库存原料
								double ratedetail=Double.parseDouble(kcdetaillist.get(0).get("PQTY").toString());
								if(dkcdetail>0)
								{
								  detailcanqty=dkcdetail/ratedetail;
								  //取整数
								  detailcanqty=Math.floor(detailcanqty);
								}
								else
								{
									detailcanqty=0;
								}
							}
							else
							{
								detailcanqty=0;
							}
							if(i==0)
							{
								canpqty=detailcanqty;
							}
							else
							{
								if(detailcanqty<canpqty)
								{
									canpqty=detailcanqty;
								}
							}
							i++;
			      }
						//计算可以售卖的数量
						pqty+=canpqty;
						String comtime= new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
						//开始插入DCP_GOODS_CLEAR表
						String mergesql=" merge into DCP_GOODS_CLEAR T1 "
							+ " using( select "+eId+" EID ,"+eShopId+" SHOPID ,"+pluno+" PLUNO from dual ) T2 "
							+ " on (T1.eId=T2.eId and T1.SHOPID=T2.SHOPID and T1.PLUNO=T2.PLUNO )  "
							+ " when matched then "
							+ " update set T1.QTY = "+pqty+",T1.UPDATE_TIME='"+comtime+"' "
							+ " when not matched then  "
							+ " INSERT (EID,SHOPID,PLUNO,UNIT,QTY,STATUS,ISCLEAR  ) VALUES "
							+ " ('"+eId+"','"+eShopId+"','"+pluno+"',' ','"+wunit+"',"+pqty+",'100','N'  )  ";
						ExecBean exb=new ExecBean(mergesql);
						data.add(new DataProcessBean(exb));
			    }
					
				}
				else 
				{
					//本门店没有库存流水，继续下一个门店
					logger.info("\r\n*********估清EstimateClear门店:"+eShopId +"没有库存流水，继续下一个门店点************\r\n");
					continue;
		    }
			}
			else 
			{
				//本门店没有bom，继续下一个门店
				logger.info("\r\n*********估清EstimateClear门店:"+eShopId +"没有boom************\r\n");
				continue;
		  }
			//一个门店执行一次
			StaticInfo.dao.useTransactionProcessData(data);
			
			logger.info("\r\n*********估清EstimateClear门店:"+eShopId +"执行完毕************\r\n");
		}
		logger.info("\r\n*********估清EstimateClear正在执行中,本次调用结束:************\r\n");
		
		return "";
	}
	
	//查找库存
	protected List<Map<String, Object>> getkcpluno(String pluno ,List<Map<String, Object>> boomkclist)
	{
		List<Map<String, Object>> kclist=new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : boomkclist) 
		{
			if(map.get("PLUNO").toString().equals(pluno))
			{
				kclist.add(map);
			}
	  }
		return kclist;
	}
	
	protected List<Map<String, Object>> getpluboom(String pluno ,List<Map<String, Object>> boomdatalist)
	{
		List<Map<String, Object>> plubomlist=new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : boomdatalist) 
		{
			if(map.get("PLUNO").toString().equals(pluno))
			{
				plubomlist.add(map);
			}
	  }
		return plubomlist;
	}
	
	protected String getMaterialpstockinSQL(String eId, String organizationno, String Sysdate) throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		String sql = "";

		sqlbuf.append("select pluno,MATERIAL_PLUNO,MATERIAL_UNIT ,PQTY from " + "( "
			+ "select pluno,material_pluno,material_unit ,sum(mqty) as pqty from  " + " (  "
			+ "select a.pluno,a.unit,material_pluno,case when qty=0 then 0 else (qty*(1+loss_rate/100)/material_qty) end as mqty, material_unit,material_qty,loss_rate,qty,is_replace "
			+ " from ( select P2.* from DCP_BOM_shop p2  "
			+ " inner join (select EID,organizationno,pluno,unit,material_pluno,material_unit,max(item) A1,max(material_bdate) as mbdate  "
			+ " from DCP_BOM_shop  " + "where EID='" + eId + "'   " + " and organizationNO ='" + organizationno
			+ "'   "
			// +"and effdate<='"+Sysdate+"' "
			+ "and material_bdate<='" + Sysdate + "'  " + " and (material_edate>='" + Sysdate
			+ "' or material_edate is null)  "
			+ "group by eId,organizationno,pluno,unit,material_pluno,material_unit "
			+ ") P1  "
			+ " on p1.pluno=p2.pluno and p1.eId=p2.eId and p1.organizationNO=p2.organizationNO "
			+ " and p1.A1=p2.item  " + "and p2.material_pluno=p1.material_pluno  "
			+ "  and p2.material_unit=p1.material_unit  "
			+ "  and p2.material_bdate=p1.mbdate  " + "   ) a  " + "  where a.eId='" + eId
			+ "' and a.organizationno='" + organizationno + "' " 
			+ "   ) group by pluno,material_pluno,material_unit  "

		+ "union all  " + "select pluno,material_pluno,material_unit ,sum(mqty) as pqty from " + "  ( "
			+ "  select a.pluno,a.unit,material_pluno,case when qty=0 then 0 else (qty*(1+loss_rate/100)/material_qty) end as mqty, material_unit,material_qty,loss_rate,qty,is_replace "
			+ " from ( select P2.* from DCP_BOM p2  "
			+ " inner join (select eId,pluno,unit,material_pluno,material_unit,max(item) A1,max(material_bdate) as mbdate "
			+ "  from DCP_BOM  " + " where eId='" + eId + "'  "
			// +" and effdate<='"+Sysdate+"' "
			+ " and material_bdate<='" + Sysdate + "'  " + "  and (material_edate>='" + Sysdate
			+ "' or material_edate is null ) and status='100'  "
			+ "  group by eId,pluno,unit,material_pluno,material_unit   " + "  ) P1  "
			+ "  on p1.pluno=p2.pluno and p1.eId=p2.eId "
			+ "  and p1.A1=p2.item  " + "  and p2.material_pluno=p1.material_pluno  "
			+ " and p2.material_unit=p1.material_unit   "
			+ " and p2.material_bdate=p1.mbdate  " + "  ) a  " + "  where a.eId='" + eId + "' "
			 + "  and a.pluno not in ( select distinct pluno from DCP_BOM_shop  " + " where eId='"
			+ eId + "'  " + " and organizationNO ='" + organizationno + "'  "
			// +" and effdate<='"+Sysdate+"' "
			+ " and material_bdate<='" + Sysdate + "'  " + " and (material_edate>='" + Sysdate
			+ "' or material_edate is null )  " + " )  " + "  ) group by pluno,material_pluno,material_unit "
			+ " )");

		sql = sqlbuf.toString();

		return sql;
		}
	  
	/**
	 * 查盈亏，查实时库存用于计算销售倒扣料
	 *
	 * @return
	 * @throws Exception
	 */
	protected String GetPLNEW_SQL(String eId, String organizationNO, String bDate, String sysDate)
			throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("select pluNO,wunit " + " ,wqty ,pqty ,plQty,plamt  "
				+ " ,price  ,oitem ,eId,organizationno,Warehouse from ("
				+ " select distinct a.eId,a.organizationno,a.pluno,b.wunit as wunit "
				+ " ,a.wqty,a.pqty,a.plqty,a.plqty*NVL(g.price1,0) as plamt  " 
				+ " ,NVL(g.price1,0) as price , oitem,Warehouse  " + " from ( "
				+ " select a.pluno,a.organizationno,a.eId,sum(a.wqty) as wqty,sum(a.pqty) as pqty,sum(a.pqty)-sum(a.wqty) as plqty ,max(oitem) as oitem,Warehouse  "
				+ " from ( "
				+ " select a.pluno,a.qty as wqty ,0 as pqty,a.organizationno,a.eId ,0 as oitem,Warehouse  "
				+ " from DCP_Stock_Day a " + " where  a.eId='" + eId + "' and a.organizationno='" + organizationNO
				+ "'  " + " union all "
				+ " select a.pluno,(case when a.stock_type='0'  then a.wqty  else  -a.wqty end) as wqty, 0 as pqty,a.organizationno,a.eId , 0 as oitem,Warehouse "
				+ " from DCP_STOCK_DETAIL a " + " where a.eId='" + eId + "' and a.organizationno='" + organizationNO
				+ "' and doc_type in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')  "
				+ " and a.account_date >='" + bDate + "'  and  (is_scrap != 'Y'  or is_scrap is null)   "
				+ "  ) a   "
				);

		sqlbuf.append("" + " group by a.pluno,a.organizationno,a.eId,a.Warehouse )a "
				+ " inner join DCP_GOODS b on a.eId=b.eId and a.pluno=b.pluno "
				+ " left join ( select P2.eId,p2.pluno,p2.unit,max(p2.price1) as price1  from DCP_PRICE p2 "
				+ "             inner join (select eId,pluno,unit,max(item) A1  "
				+ "             from DCP_PRICE " + "  where eId='" + eId + "' "
				+ " and effdate<='" + sysDate + "'  and (LEDate>='" + sysDate
				+ "' or LEDate is null) AND status='100' "
				+ "             group by eId,pluno,unit ) P1 on p1.pluno=p2.pluno and p1.eId=p2.eId and p1.A1=p2.item "
				+ " group by P2.eId,p2.pluno,p2.unit ) g  on a.PLUNO=g.PLUNO AND  a.eId=g.eId ");

		sqlbuf.append("  )tbl   order by oitem ");
		//sqlbuf.append("  )tbl  where wqty<0   order by oitem ");

		sql = sqlbuf.toString();
		return sql;
	}
	
	
	
}
