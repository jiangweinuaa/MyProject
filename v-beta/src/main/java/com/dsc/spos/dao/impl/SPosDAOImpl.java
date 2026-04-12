package com.dsc.spos.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.PosPub;

import com.dsc.spos.dao.DsmDAO;

@Repository("sposDao")
@Transactional("spos")
public class SPosDAOImpl implements DsmDAO {

    private Logger loger = LogManager.getLogger(SPosDAOImpl.class.getName());
    private static HibernateTemplate hibernateTemplate = new HibernateTemplate();

    @SuppressWarnings("static-access")
    @Autowired
    @Required
    public void setSessionFactory(@Qualifier("spos") SessionFactory sessionFactory) {
        this.hibernateTemplate.setSessionFactory(sessionFactory);
    }

    //	public  List<Map<String, String>> executeQuerySQLtest(final String sql, final String[] values) throws Exception{
    //		final List<Map<String, String>> result = new ArrayList<Map<String,String>>();
    //		hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work() {
    //
    //			@Override
    //			public void execute(Connection conn) throws SQLException {
    //				List<Map<String, String>> queryData;
    //				try {
    //					queryData = DsmNoSpringDAO.getInstance().doSelectResultToMap(conn, sql, values);
    //					result.addAll(queryData);
    //				} catch (Exception e) {
    //					throw new SQLException(e.getMessage());
    //				}
    //			}
    //		});
    //		return result;
    //	}

    @Override
    public List<Map<String, Object>> executeQuerySQL(String sql, String[] conditionValues) throws Exception {
        return this.executeQuerySQL(sql, conditionValues, Boolean.FALSE);
    }

    @Override
    public List<Map<String, Object>> executeQuerySQL(String sql, String[] conditionValues, boolean isShowSql) throws Exception
    {
        final Result r = new Result();
        final String parseSql = this.parseSql(sql, conditionValues, isShowSql); //加上查詢條件

        //System.out.println(parseSql);

        hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work()
        {
            @Override
            public void execute(Connection conn) throws SQLException
            {
                List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(); //要傳回的格式

                try
                {
                    result=SPosDAOImpl.this.GetNativeSQL(conn, parseSql);
                    r.setDatas(result);
                    result=null;
                }
                catch(Exception e)
                {
                    r.setDatas(result);
                    result=null;
                    throw new SQLException(e.toString());
                }
            }

        });

        return r.getDatas();
    }

    public List<Map<String, Object>> GetNativeSQL(Connection conn,String sql) throws Exception
    {
        //
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        ResultSet rs = null;
        Statement pst = null;
        long begin = System.currentTimeMillis();
        try
        {
            //
            pst = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);

            pst.setQueryTimeout(PosPub.iTimeoutTime);//30秒超时
            conn.setAutoCommit(Boolean.FALSE);
            
            rs = pst.executeQuery(sql);

            conn.commit();

            rs.setFetchSize(PosPub.iFetchSize);

            ResultSetMetaData md=rs.getMetaData();

            Map<String, Object> m=null;
            while(rs.next())
            {
                // 2000笔不满足业务实际需求，先放开 BY JZMA 20200121
                //				//最多2000
                //				if (rs.getRow()>2000)
                //				{
                //					break;
                //				}

                m = new HashMap<String, Object>();
                Object value;
                for(int i = 0 ; i < md.getColumnCount() ; i++)
                {
                    //解决返回LONG类型值不是null的情况下会出现 ====流关闭报错
                    if(md.getColumnTypeName(i + 1).equals("LONG"))
                    {
                        value=rs.getString(md.getColumnName(i + 1));
                        if (value==null)
                        {
                            value="";
                        }
                    }
                    else if(md.getColumnTypeName(i + 1).equals("DATE"))
                    {
                        value=rs.getDate(md.getColumnName(i + 1));
                        if (value!=null &&value.toString().trim().equals("")==false)
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            value=sdf.format(rs.getDate(md.getColumnName(i + 1)));
                            sdf=null;
                        }
                        else
                        {
                            value="";
                        }
                    }
                    else if (rs.getObject(md.getColumnName(i + 1))==null)
                    {
                        value="";
                    }
                    else if(rs.getObject(md.getColumnName(i + 1)).getClass().getTypeName().equals("byte[]"))
                    {
                        byte[] xx = (byte[])rs.getObject(md.getColumnName(i + 1));
                        StringBuffer GUIDraw=new StringBuffer("");
                        for (byte b : xx)
                        {
                            String hex = Integer.toHexString(b & 0xFF);
                            if (hex.length() == 1)
                            {
                                hex = '0' + hex;
                            }
                            GUIDraw.append(hex.toUpperCase());
                        }
                        value=GUIDraw.toString();
                    }
                    else if(md.getColumnTypeName(i + 1).equals("CLOB"))
                    {
                        value=rs.getString(md.getColumnName(i + 1));
                    }
                    else
                    {
                        value=rs.getObject(md.getColumnName(i + 1));
                    }

                    m.put(md.getColumnName(i+1).toUpperCase(), value);
                }

                value=null;

                result.add(m);
            }

            md=null;

            //清理
            m=null;

            return result;

        }
        catch (SQLTimeoutException e) //SQL超时
        {
            conn.rollback();

            loger.error("\r\n**********SQLTimeoutExceptionSQL执行超时*********\r\n" + sql + "\r\n" + e.getMessage());

            throw new SQLException("执行超时!");
        }
        catch (SQLException e) //SQL异常
        {
            conn.rollback();

            loger.error("\r\n**********SQLException服务执行SQL查询失败*********\r\n" + sql + "\r\n" + e.getMessage());

            throw new SQLException("服务执行SQL查询失败，"+ e.getMessage()+"!");
        }
        catch (Exception e)
        {
            conn.rollback();

            loger.error("\r\n**********Exception服务执行SQL查询失败*********\r\n" + sql + "\r\n" + e.getMessage());

            throw new Exception("服务执行SQL查询失败，"+ e.getMessage()+"!");
        }
        finally
        {

            if(pst != null)
            {
                pst.close();
                pst = null;
            }

            if(rs != null)
            {
                rs.close();
                rs = null;
            }

            result=null;
            long consumeTimesMs = System.currentTimeMillis() - begin;
            if (consumeTimesMs > 5000) {
                loger.info("【SlowSQL】sql: [ " + sql + " ]");
                loger.info("【SlowSQL】耗时：[" + consumeTimesMs + "] MS");
            }
        }
    }


    /**
     * //將 ？ 號變成 %s, 再用轉換,變成正常的  sql.
     * @param sql
     * @param values
     * @param isShowSql
     * @return
     */
    private String parseSql(String sql, String[] values, boolean isShowSql)
    {
        //將 ？ 號變成 %s, 再用轉換,變成正常的  sql.
        sql = sql.replace("?", "'%s'");

        if (values != null)
        {
            Object[] otcs = new Object[values.length];
            for (int i = 0; i <otcs.length; i++)
            {
                otcs[i] = values[i].replaceAll("--", "").replaceAll("'","''"); //預防 sql injection 的攻擊
            }
            sql = String.format(sql, otcs);

            //
            otcs=null;
        }

        if (isShowSql)
        {
            //System.out.println("Use Statement do query sql:" + sql);
        }
        return sql;
    }


    @Override
    public void closeDAO() {
        // TODO Auto-generated method stub

    }

    public boolean doDelete(Connection conn, String tableName, Map<String, DataValue> conditions) throws Exception
    {
        PreparedStatement pst = null;

        String sql ="";

        //
        if (errSql==null)
        {
            errSql=new StringBuffer("");
        }
        long begin = System.currentTimeMillis();
        try
        {
            if (conditions == null || conditions.size() == 0)
            {
                return Boolean.TRUE;//刪除條件為空時, 不執行任何動作
            }
            //String sql = "delete from " + tableName + " where 1=1 ";
            StringBuffer sb = new StringBuffer("delete from " + tableName + " where 1=1 ");
            String[] keys = conditions.keySet().toArray(new String[0]);
            for (String key : keys)
            {
                //sql += " and " + key + " = ? ";
                DataValue v = conditions.get(key);
                SPosDAOImpl.this.getExpression(key, v, sb);
            }

            sql=sb.toString();
            pst = conn.prepareStatement(sql);
            pst.setQueryTimeout(PosPub.iTimeoutTime);//30秒超时

            int i = 1;
            for (String key : keys)
            {
                //这是处理SQL 中的 IN 语句(in 后面的整个字符串 外面不需要'')
                //select * from TA_PAGE_MENU where PAGE_MENU_ID
                //in
                //(
                // select PAGE_MENU_ID from TA_PAGE_MENU where PAGE_MENU_ID ='2C7FA3389CAC44129B6D34397E772F26'
                //)
                if (conditions.get(key).getEp()!=DataExpression.IN)
                {
                    DataValue v = conditions.get(key);

                    //处理null
                    Object sgetValue;
                    if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                    {
                        sgetValue=v.getValue()==null?"0":v.getValue();
                    }
                    else
                    {
                        sgetValue=v.getValue()==null?"":v.getValue();
                    }

                    pst.setObject(i, sgetValue, v.getDataType());
                    i++;
                }
            }

            //记录
            errSql.append("\r\n" + sql + "\r\n");

            boolean bOk=pst.execute();

            errSql.setLength(0);


            return bOk;
        }
        catch (SQLTimeoutException e) //SQL超时
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            loger.error("\r\n**********Delete SQL执行超时*********\r\n" + sql + "\r\n" + e.getMessage());

            throw new SQLException("Delete执行超时!");
        }
        catch(SQLException e) //SQL异常
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            loger.error("\r\n**********Delete SQL执行异常*********\r\n" + sql + "\r\n" + e.getMessage());

            throw e;
        }
        catch(Exception e)
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            loger.error("\r\n**********Delete SQL执行失败*********\r\n" + sql + "\r\n" + e.getMessage());

            throw e;
        }
        finally
        {
            if (pst != null)
            {
                pst.close();
            }
            long consumeTimesMs = System.currentTimeMillis() - begin;
            if (consumeTimesMs > 1000) {
                loger.info("【SlowSQL】sql: [ " + sql + " ]");
                loger.info("【SlowSQL】耗时：[" + consumeTimesMs + "] MS");
            }
        }
    }

    @Override
    public boolean doDelete(final String tableName, final Map<String, DataValue> conditions) throws Exception {
        final Result r = new Result();
        hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work() {

            @Override
            public void execute(Connection conn) throws SQLException {
                boolean b;
                try {
                    b = SPosDAOImpl.this.doDelete(conn, tableName, conditions);
                    r.setResult(b);
                } catch (SQLException e) {
                    r.setResult(Boolean.FALSE);
                    throw e;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    r.setResult(Boolean.FALSE);
                }
            }

        });
        return r.isResult();
    }

    @Override
    public int insert(Connection conn, String tableName, String[] columns, DataValue[] values) throws Exception
    {
        PreparedStatement pst = null;
        String sql ="";
        String replaceSQL="";
        int itemp=0;

        //
        if (errSql==null)
        {
            errSql=new StringBuffer("");
        }

        StringBuffer sb = new StringBuffer();

        try
        {
            sb.append("insert into " + tableName + " ("); //id,name) values(?,?)";

            //columns
            for (String c : columns)
            {
                sb.append(c + ",");
            }
            sb.deleteCharAt(sb.length() - 1);

            sb.append(") values (");

            //value
            for (int i = 0; i < columns.length; i++)
            {
                sb.append("?,");
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");

            sql=sb.toString();

            pst = conn.prepareStatement(sql);
            pst.setQueryTimeout(PosPub.iTimeoutTime);//30秒超时

            for (int i = 0; i < values.length; i++)
            {
                itemp=i;
                DataValue v = values[i];
                if (Types.DATE == v.getDataType())
                {
                    //等于null不做默认处理，容易日期搞错就过了
                    if (v.getValue()!=null)
                    {
                        //2019-12-18 日期格式
                        if (v.getValue().toString().length()==10)
                        {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = simpleDateFormat.parse(v.getValue().toString());
                            pst.setDate(i+1 , new java.sql.Date(date.getTime()));
                        }
                        //2019-12-18 16:15:29日期时间格式
                        else if (v.getValue().toString().length()==19)
                        {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = simpleDateFormat.parse(v.getValue().toString());
                            pst.setDate(i+1 , new java.sql.Date(date.getTime()));
                        }
                        else
                        {
                            //pst.setDate(i+1 , (java.sql.Date)v.getValue());//暂不处理
                            pst.setDate(i+1 , null);//暂不处理
                        }
                    }
                    else
                    {
                        //pst.setDate(i+1 , (java.sql.Date)v.getValue());//暂不处理
                        pst.setDate(i+1 , null);//暂不处理
                    }
                }
                else if (Types.BLOB == v.getDataType())
                {
                    if(v.getValue()==null ||v.getValue().toString().length()==0)
                    {
                        pst.setBlob(i+1 ,null, v.getDataType());
                    }
                    else
                    {
                        InputStream in_withcode=new ByteArrayInputStream(v.getValue().toString().getBytes("UTF-8"));
                        pst.setBlob(i+1 ,in_withcode, in_withcode.available());
                        in_withcode.close();
                        in_withcode=null;
                    }
                }
                else if (Types.CLOB == v.getDataType())
                {
                    if(v.getValue()==null ||v.getValue().toString().length()==0)
                    {
                        pst.setClob(i+1 ,null, v.getDataType());
                    }
                    else
                    {
                        Reader reader = new StringReader(v.getValue().toString());
                        pst.setClob(i+1 ,reader);
                        reader.close();
                        reader=null;
                    }
                }
                else
                {
                    //处理null
                    Object sgetValue;
                    if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                    {
                        sgetValue=v.getValue()==null||v.getValue().toString().isEmpty()?0:v.getValue();
                    }
                    else
                    {
                        sgetValue=v.getValue()==null?"":v.getValue().toString();
                    }

                    pst.setObject(i+1 , sgetValue, v.getDataType());
                }
            }

            //將 ？ 號變成 %s, 再用轉換,變成正常的  sql.
            replaceSQL = sql.replace("?", "'%s'");
            if (values != null)
            {
                Object[] otcs = new Object[values.length];
                for (int i = 0; i <otcs.length; i++)
                {
                    otcs[i] = values[i].getValue();
                }
                replaceSQL = String.format(replaceSQL, otcs);
                //
                otcs=null;
            }

            //System.out.println("打印SQL		"+replaceSQL);

			/*
	        pst.setInt(1,20);
	        pst.setString(2,"2");
	        pst.setString(3,"3");
	        pst.setString(4,"4");
			 */

            //记录
            errSql.append("\r\n" + replaceSQL + "\r\n");

            int rows = pst.executeUpdate();

            errSql.setLength(0);

            //conn.commit();

            return rows;
        }
        catch (SQLTimeoutException e) //SQL超时
        {
            //將 ？ 號變成 %s, 再用轉換,變成正常的  sql.
            sql = sql.replace("?", "'%s'");
            if (values != null)
            {
                Object[] otcs = new Object[values.length];
                for (int i = 0; i <otcs.length; i++)
                {
                    otcs[i] = values[i].getValue();
                }
                replaceSQL = String.format(sql, otcs);

                //
                otcs=null;
            }

            //记录
            errSql.append("\r\n" + replaceSQL + "\r\n");


            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            loger.error("\r\n**********Insert SQL执行超时*********\r\n" + replaceSQL +"\r\n"+ errors.toString());

            pw=null;
            errors=null;

            throw new SQLException("执行超时!");
        }
        catch(SQLException e)
        {
            //將 ？ 號變成 %s, 再用轉換,變成正常的  sql.
            sql = sql.replace("?", "'%s'");
            if (values != null)
            {
                Object[] otcs = new Object[values.length];
                for (int i = 0; i <otcs.length; i++)
                {
                    otcs[i] = values[i].getValue();
                }
                replaceSQL = String.format(sql, otcs);

                //
                otcs=null;
            }

            //记录
            errSql.append("\r\n" + replaceSQL + "\r\n");

            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            loger.error("\r\n**********Insert SQL失败*********\r\n" + replaceSQL +"\r\n"+ errors.toString());

            pw=null;
            errors=null;

            throw e;
        }
        catch(Exception e)
        {
            //將 ？ 號變成 %s, 再用轉換,變成正常的  sql.
            sql = sql.replace("?", "'%s'");
            if (values != null)
            {
                Object[] otcs = new Object[values.length];
                for (int i = 0; i <otcs.length; i++)
                {
                    otcs[i] = values[i].getValue();
                }
                replaceSQL = String.format(sql, otcs);

                //
                otcs=null;
            }

            //记录
            errSql.append("\r\n" + replaceSQL + "\r\n");


            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            loger.error("\r\n**********Insert SQL失败*********\r\n" + replaceSQL+ " 项次报错："+itemp+ "\r\n"+ errors.toString() );

            pw=null;
            errors=null;

            throw e;
        }
        finally
        {
            if (sb!=null)
            {
                sb.setLength(0);
                sb=null;
            }

            if (pst != null)
            {
                pst.close();
            }
        }
    }


    @Override
    public int exec(Connection conn, String tableName) throws Exception
    {
        PreparedStatement pst = null;
        String sql ="";

        //
        if (errSql==null)
        {
            errSql=new StringBuffer("");
        }

        try
        {
            sql = tableName;
            pst = conn.prepareStatement(sql);
            pst.setQueryTimeout(PosPub.iTimeoutTime);//30秒超时
            //System.out.println("打印SQL		"+sql);
			/*
	        pst.setInt(1,20);
	        pst.setString(2,"2");
	        pst.setString(3,"3");
	        pst.setString(4,"4");
			 */

            //记录
            errSql.append("\r\n" + sql + "\r\n");

            int rows = pst.executeUpdate();

            errSql.setLength(0);

            //conn.commit();



            return rows;
        }
        catch (SQLTimeoutException e) //SQL超时
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            loger.error("\r\n**********Insert SQL执行超时*********\r\n" + sql +"\r\n"+ errors.toString());
            pw=null;
            errors=null;

            throw new SQLException("执行超时!");
        }
        catch(SQLException e)
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            loger.error("\r\n**********Insert SQL失败*********\r\n" + sql +"\r\n"+ errors.toString());
            pw=null;
            errors=null;

            throw e;
        }
        catch(Exception e)
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");


            StringWriter errors = new StringWriter();
            PrintWriter pw=new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            loger.error("\r\n**********Insert SQL失败*********\r\n" + sql+"\r\n"+ errors.toString() );
            pw=null;
            errors=null;

            throw e;
        }
        finally
        {
            if (pst != null)
            {
                pst.close();
            }
        }
    }

    @Override
    public int exec(final String tableName) throws Exception {
        final Result r = new Result();
        hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work(){

            @Override
            public void execute(Connection conn) throws SQLException {
                try {
                    int rows = SPosDAOImpl.this.exec(conn, tableName);
                    r.setRows(rows);
                    r.setResult(Boolean.TRUE);
                } catch (SQLException e) {
                    r.setResult(Boolean.FALSE);
                    throw e;
                } catch (Exception e)
                {
                    r.setResult(Boolean.FALSE);
                }
            }

        });
        return r.getRows();
    }



    @Override
    public int insert(final String tableName, final String[] columns, final DataValue[] values) throws Exception {
        final Result r = new Result();
        hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work(){

            @Override
            public void execute(Connection conn) throws SQLException {
                try {
                    int rows = SPosDAOImpl.this.insert(conn, tableName, columns, values);
                    r.setRows(rows);
                    r.setResult(Boolean.TRUE);
                } catch (SQLException e) {
                    r.setResult(Boolean.FALSE);
                    throw e;
                } catch (Exception e)
                {
                    r.setResult(Boolean.FALSE);
                }
            }

        });
        return r.getRows();
    }

    @Override
    public boolean update(Connection conn, String tableName, Map<String, DataValue> values, Map<String, DataValue> conditions) throws Exception
    {
        PreparedStatement pst = null;
        String sql = "";

        //
        if (errSql==null)
        {
            errSql=new StringBuffer("");
        }

        StringBuffer sb = new StringBuffer();

        InputStream in_withcode=null;
        Reader reader =null;
        long begin = System.currentTimeMillis() ;
        try
        {
            if (values == null || values.size() == 0)
            {
                //				r.setResult(Boolean.FALSE);
                return Boolean.TRUE; //刪除條件為空時, 不執行任何動作
            }
            sb.append("update " + tableName);

            //設定要更新的值
            sb.append(" set ");

            String[] vkeys = values.keySet().toArray(new String[0]);
            for (String key : vkeys)
            {
                //取得条件
                DataValue v = values.get(key);

                //处理null
                Object sgetValue;
                if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                {
                    sgetValue=v.getValue()==null?0:v.getValue();
                }
                else
                {
                    sgetValue=v.getValue()==null?"":v.getValue().toString();
                }

                //数据库字段+1
                if(v.getEp()==DataExpression.UpdateSelf)
                {
                    String skeytemp=" nvl("+key+",0) ";
                    sb.append(" " + key + " = (" + skeytemp + "+" + sgetValue + "),");
                }
                //2018-10-30 添加SubSelf key = key - 1
                else if(v.getEp()==DataExpression.SubSelf)
                {
                    String skeytemp=" nvl("+key+",0) ";
                    sb.append(" " + key + " = (" + skeytemp + "-" + sgetValue + "),");
                }
                //A字段=B字段
                else if(v.getEp()==DataExpression.OtherFieldname)
                {
                    sb.append(" " + key + " = " + sgetValue + ",");
                }
                else
                {
                    sb.append(" " + key + " = ?,");
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            if (conditions != null&&conditions.size() != 0)
            {
            	sb.append(" where 1=1 ");
            	
            	String[] keys = conditions.keySet().toArray(new String[0]);
            	for (String key : keys)
            	{
            		//						sql += " and " + key + "= ?";
            		DataValue v = conditions.get(key);
            		SPosDAOImpl.this.getExpression(key, v, sb);
            	}
            }else{
            	if("CRM_ORDER".equalsIgnoreCase(tableName)||"DCP_ORDER".equalsIgnoreCase(tableName)){
            		sb.append(" where 1=2 ");
            	}
            }

            sql = sb.toString();

            pst = conn.prepareStatement(sql);
            pst.setQueryTimeout(PosPub.iTimeoutTime);//30秒超时

            int i = 1;
            for (String vkey : vkeys)
            {
                DataValue v = values.get(vkey);

                //处理null
                Object sgetValue;
                if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                {
                    sgetValue=v.getValue()==null?0:v.getValue();
                }
                else
                {
                    sgetValue=v.getValue()==null?"":v.getValue();
                }


                //数据库字段+1
                if(v.getEp()==DataExpression.UpdateSelf)
                {
                    continue;//这个字段要跳过,不当参数处理
                }
                if(v.getEp()==DataExpression.SubSelf)
                {
                    continue;//这个字段要跳过,不当参数处理
                }
                if(v.getEp()==DataExpression.OtherFieldname)
                {
                    continue;//这个字段要跳过,不当参数处理
                }
                else
                {
                    if (Types.BLOB == v.getDataType())
                    {
                        in_withcode=new ByteArrayInputStream(sgetValue.toString().getBytes("UTF-8"));
                        pst.setBlob(i ,in_withcode, in_withcode.available());
                    }
                    else if (Types.CLOB == v.getDataType())
                    {
                        reader = new StringReader(sgetValue.toString());
                        pst.setClob(i+1 ,reader);

                    }
                    else if (Types.DATE == v.getDataType())
                    {
                        if(sgetValue!=null && !sgetValue.equals("")){

                            //2019-12-18 日期格式
                            if (sgetValue.toString().length()==10)
                            {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = simpleDateFormat.parse(sgetValue.toString());
                                pst.setDate(i , new java.sql.Date(date.getTime()));
                            }
                            //2019-12-18 16:15:29日期时间格式
                            else if (sgetValue.toString().length()==19)
                            {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = simpleDateFormat.parse(sgetValue.toString());
                                pst.setDate(i , new java.sql.Date(date.getTime()));
                            }
                            else
                            {
                                //pst.setDate(i , (java.sql.Date)sgetValue);//暂不处理
                                pst.setDate(i , null);//暂不处理
                            }
                        }
                        else
                        {
                            //pst.setDate(i , (java.sql.Date)v.getValue());//暂不处理
                            pst.setDate(i , null);//暂不处理
                        }

                    }
                    else
                    {
                        pst.setObject(i, sgetValue, v.getDataType());
                    }
                    i++;

                    //显示SQL
                    //参考SQL：
                    if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                    {
                        sql=sql.replaceFirst("\\?", Matcher.quoteReplacement(sgetValue.toString()));
                    }
                    else if (Types.DATE == v.getDataType())
                    {
                        if(sgetValue!=null && !sgetValue.equals("")){
                            //2019-12-18 日期格式
                            if (sgetValue.toString().length()==10)
                            {
                                sql=sql.replaceFirst("\\?", "to_date('" +Matcher.quoteReplacement(sgetValue.toString())+"','yyyy-mm-dd')");
                            }
                            //2019-12-18 16:15:29日期时间格式
                            else if (sgetValue.toString().length()==19)
                            {
                                sql=sql.replaceFirst("\\?", "to_date('" +Matcher.quoteReplacement(sgetValue.toString())+"','yyyy-mm-dd hh24:mi:ss')");
                            }
                            else
                            {
                                sql=sql.replaceFirst("\\?", "'"+Matcher.quoteReplacement(sgetValue.toString())+"'");
                            }
                        }
                        else{
                            sql=sql.replaceFirst("\\?", "'"+Matcher.quoteReplacement(sgetValue.toString())+"'");
                        }
                    }
                    else
                    {
                        sql=sql.replaceFirst("\\?", "'"+Matcher.quoteReplacement(sgetValue.toString())+"'");
                    }
                }
            }
            if (conditions != null)
            {
                String[] ckeys = conditions.keySet().toArray(new String[0]);
                for (String ckey : ckeys)
                {
                    DataValue v = conditions.get(ckey);

                    //处理null
                    Object sgetValue;
                    if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                    {
                        sgetValue=v.getValue()==null?"0":v.getValue();
                    }
                    else
                    {
                        sgetValue=v.getValue()==null?"":v.getValue();
                    }

                    //2018-10-31 增加 if 判断， 如果 为 in 不做参数处理 ，否则 会将传入的 value  作为 " ？ "，导致缺少参数报错
                    if(v.getEp()==DataExpression.IN)
                    {
                        continue;//这个字段要跳过,不当参数处理
                    }
                    else if (Types.BLOB == v.getDataType())
                    {
                        in_withcode=new ByteArrayInputStream(sgetValue.toString().getBytes("UTF-8"));
                        pst.setBlob(i ,in_withcode, in_withcode.available());
                    }
                    else if (Types.CLOB == v.getDataType())
                    {
                        reader = new StringReader(sgetValue.toString());
                        pst.setClob(i+1 ,reader);

                    }
                    else if (Types.DATE == v.getDataType())
                    {
                        //2019-12-18 日期格式
                        if (sgetValue.toString().length()==10)
                        {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = simpleDateFormat.parse(sgetValue.toString());
                            pst.setDate(i , new java.sql.Date(date.getTime()));
                        }
                        //2019-12-18 16:15:29日期时间格式
                        else if (sgetValue.toString().length()==19)
                        {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = simpleDateFormat.parse(sgetValue.toString());
                            pst.setDate(i , new java.sql.Date(date.getTime()));
                        }
                        else
                        {
                            pst.setDate(i , (java.sql.Date)sgetValue);//暂不处理
                        }
                    }
                    else
                    {
                        pst.setObject(i, sgetValue, v.getDataType());
                    }
                    i++;

                    //显示SQL
                    //参考SQL：
                    if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                    {
                        sql=sql.replaceFirst("\\?", Matcher.quoteReplacement(sgetValue.toString()));
                    }
                    else if (Types.DATE == v.getDataType())
                    {
                        //2019-12-18 日期格式
                        if (sgetValue.toString().length()==10)
                        {
                            sql=sql.replaceFirst("\\?", "to_date('" +Matcher.quoteReplacement(sgetValue.toString())+"','yyyy-mm-dd')");
                        }
                        //2019-12-18 16:15:29日期时间格式
                        else if (sgetValue.toString().length()==19)
                        {
                            sql=sql.replaceFirst("\\?", "to_date('" +Matcher.quoteReplacement(sgetValue.toString())+"','yyyy-mm-dd hh24:mi:ss')");
                        }
                        else
                        {
                            sql=sql.replaceFirst("\\?", "'"+Matcher.quoteReplacement(sgetValue.toString())+"'");
                        }
                    }
                    else
                    {
                        sql=sql.replaceFirst("\\?", "'"+Matcher.quoteReplacement(sgetValue.toString())+"'");
                    }
                }
            }

            //记录
            errSql.append("\r\n" + sql + "\r\n");

            boolean bOK=pst.execute();

            errSql.setLength(0);

            return bOK;
        }
        catch (SQLTimeoutException e) //SQL超时
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            loger.error("\r\n**********Update SQL执行超时*********\r\n" + sql+"\r\n"+ e.getMessage());

            throw new SQLException("执行超时!");
        }
        catch (SQLException e) //SQL异常
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            loger.error("\r\n**********Update SQL异常*********\r\n" + sql+"\r\n"+ e.getMessage());

            throw e;
        }
        catch(Exception e)
        {
            //记录
            errSql.append("\r\n" + sql + "\r\n");

            loger.error("\r\n**********Update SQL失败*********\r\n" + sql+"\r\n"+ e.getMessage());

            throw e;
        }
        finally
        {
            if (sb!=null)
            {
                sb.setLength(0);
                sb=null;
            }

            if (in_withcode!=null)
            {
                in_withcode.close();
                in_withcode=null;
            }

            if (reader!=null)
            {
                reader.close();
                reader=null;
            }

            if (pst != null)
            {
                pst.close();
                pst=null;
            }
            long consumeTimesMs = System.currentTimeMillis() - begin;
            if (consumeTimesMs > 1000) {
                loger.info("【SlowSQL】sql: [ " + sql + " ]");
                loger.info("【SlowSQL】耗时：[" + consumeTimesMs + "] MS");
            }
        }
    }

    @Override
    public boolean update(final String tableName, final Map<String, DataValue> values, final Map<String, DataValue> conditions) throws Exception {
        final Result r = new Result();
        hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work(){

            @Override
            public void execute(Connection conn) throws SQLException {
                try {
                    boolean b = SPosDAOImpl.this.update(conn, tableName, values, conditions);
                    r.setResult(b);
                } catch (SQLException e) {
                    r.setResult(Boolean.FALSE);
                    throw e;
                } catch(Exception e) {
                    r.setResult(Boolean.FALSE);
                }
            }

        });
        return r.isResult();
    }


    /**
     * 取得條件的 sql.
     * @return
     */
    private void getExpression(String key, DataValue v, StringBuffer sb) {
        switch(v.getEp()) {
            case EQ:
                sb.append(" and " + key + " = ? ");
                break;
            case NE:
                sb.append(" and " + key + " <> ? ");
                break;
            case OR:
                sb.append(" and " + key + " or ? ");
                break;
            case IN://这里直接赋值啦,不用?
                sb.append(" and " + key + " in ( "+ v.getValue().toString()+" )");
                break;
            case Less://
                sb.append(" and " + key + " < ? ");
                break;
            case LessEQ://
                sb.append(" and " + key + " <= ? ");
                break;
            case Greater://
                sb.append(" and " + key + " > ? ");
                break;
            case GreaterEQ://
                sb.append(" and " + key + " >= ? ");
                break;
            //2018-10-30 增加 UpdateSelf 和 SubSelf
            case UpdateSelf://
                sb.append(" and " + key + " = "+key+" +1 ");
                break;
            case SubSelf://
                sb.append(" and " + key + " = "+key+" -1 ");
                break;
            case OtherFieldname://
                sb.append(" and " + key + " = "+v.getValue().toString()+" ");
                break;
            default:
                break;
        }
    }

    private class Result {
        private boolean result;
        private int rows;
        private List<Map<String, Object>> datas;

        public void setResult(boolean result) {
            this.result = result;
        }
        public void setRows(int rows) {
            this.rows = rows;
        }
        public boolean isResult() {
            return result;
        }
        public int getRows() {
            return rows;
        }
        public List<Map<String, Object>> getDatas()
        {
            return datas;
        }
        public void setDatas(List<Map<String, Object>> datas)
        {
            this.datas = datas;
        }

    }


    //记录整个错误SQL
    StringBuffer errSql = null;

    @Override
    public boolean useTransactionProcessData(final List<DataProcessBean> data) throws Exception
    {
        final Result r = new Result();

        Session session = hibernateTemplate.getSessionFactory().openSession();

        session.getTransaction().setTimeout(15);//15秒

        session.beginTransaction();

        session.doWork(new Work()
        {

            public void execute(Connection conn) throws SQLException
            {
                //hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work(){

                //@Override
                //public void execute(Connection conn) throws SQLException {
                try
                {
                    conn.setAutoCommit(Boolean.FALSE);
                    //事物隔离级别
                    //conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                    //初始化
                    errSql=new StringBuffer("");

                    for (DataProcessBean dpb : data)
                    {
                        switch(dpb.getProcessType())
                        {
                            case INSERT:
                                //FIXME 這邊要再改成可以批次處理的方式
                                for (DataValue[] row : dpb.getDatas())
                                {
                                    SPosDAOImpl.this.insert(conn, dpb.getTableName(), dpb.getColumns(), row);
                                }
                                break;
                            case UPDATE:
                                SPosDAOImpl.this.update(conn, dpb.getTableName(), dpb.getValues(), dpb.getConditions());
                                break;
                            case DELETE:
                                SPosDAOImpl.this.doDelete(conn, dpb.getTableName(), dpb.getConditions());
                                break;
                            case EXEC:
                                SPosDAOImpl.this.exec(conn, dpb.getTableName());
                                break;
                            case PROCEDURE:
                                SPosDAOImpl.this.storedProcedureProcess(conn,dpb.getProcedure(),dpb.getInputParameter());
                                break;

                        }
                    }
                    conn.commit();
                }
                catch (SQLTimeoutException e) //SQL超时
                {
                    String msg="";
                    try
                    {
                        StringWriter errors = new StringWriter();
                        PrintWriter pw=new PrintWriter(errors);
                        e.printStackTrace(pw);

                        pw.flush();
                        pw.close();

                        errors.flush();
                        errors.close();

                        msg=errors.toString();
                        loger.error("\r\n**********useTransactionProcessData服务执行SQL超时*********" + e.getMessage()+"\r\n" + msg + "******\r\n");

                        pw=null;
                        errors=null;
                    }
                    catch (IOException e1)
                    {
                        loger.error("\r\n**********useTransactionProcessData服务执行SQL超时*********" + e.getMessage() + "******\r\n");
                    }

                    //记录完整错误SQL日志
                    loger.error("\r\n**********useTransactionProcessData错误SQL语句："+errSql.toString()+"*********\r\n");

                    conn.rollback();
                    r.setResult(Boolean.FALSE);
                    throw new SQLException("执行超时!"+msg);
                }
                catch (SQLException e)
                {
                    String msg="";
                    try
                    {
                        StringWriter errors = new StringWriter();
                        PrintWriter pw=new PrintWriter(errors);
                        e.printStackTrace(pw);

                        pw.flush();
                        pw.close();

                        errors.flush();
                        errors.close();

                        msg=errors.toString();
                        loger.error("\r\n**********useTransactionProcessData服务执行SQL异常*********" + e.getMessage()+"\r\n" + msg + "******\r\n");

                        pw=null;
                        errors=null;
                    }
                    catch (IOException e1)
                    {
                        loger.error("\r\n**********useTransactionProcessData服务执行SQL异常*********" + e.getMessage() + "******\r\n");
                    }

                    //记录完整错误SQL日志
                    loger.error("\r\n**********useTransactionProcessData错误SQL语句："+errSql.toString()+"*********\r\n");

                    conn.rollback();
                    r.setResult(Boolean.FALSE);
                    throw new SQLException(e.getMessage()+","+msg);
                }
                catch(Exception e)
                {
                    String msg="";
                    try
                    {
                        StringWriter errors = new StringWriter();
                        PrintWriter pw=new PrintWriter(errors);
                        e.printStackTrace(pw);

                        pw.flush();
                        pw.close();

                        errors.flush();
                        errors.close();

                        msg=errors.toString();
                        loger.error("\r\n**********useTransactionProcessData服务执行SQL失败*********" + e.getMessage()+"\r\n" + msg + "******\r\n");

                        pw=null;
                        errors=null;
                    }
                    catch (IOException e1)
                    {
                        loger.error("\r\n**********useTransactionProcessData服务执行SQL失败*********" + e.getMessage() + "******\r\n");
                    }

                    //记录完整错误SQL日志
                    loger.error("\r\n**********useTransactionProcessData错误SQL语句："+errSql.toString()+"*********\r\n");

                    conn.rollback();
                    r.setResult(Boolean.FALSE);
                    throw new SQLException(e.toString() +","+msg);
                }
                finally
                {
                    try
                    {
                        if (errSql.length()!=0)
                        {
                            PosPub.WriteETLJOBLog("useTransactionProcessData錯誤SQL語句 "+errSql.toString());
                        }
                    }
                    catch (IOException e)
                    {

                    }

                    conn.setAutoCommit(Boolean.TRUE); //要還原本 connection 的樣子
                    conn.close();

                    //最后清空
                    errSql.setLength(0);
                }
            }

        });
        session.flush();//这是并发的关键
        session.close();
        return r.isResult();
    }

    @Override
    public Map<Integer,Object> storedProcedure (final String procedure, final Map<Integer,Object> inputParameter,final Map<Integer,Integer> outParameter) throws Exception{
        final storedProcedureResult r = new storedProcedureResult();
        hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work(){
            @Override
            public void execute(Connection conn) throws SQLException
            {
                Map<Integer, Object> map = new HashMap<Integer, Object>(); //要傳回的格式

                StringBuffer sb = new StringBuffer();

                try
                {
                    int ipNum =  inputParameter.size() ;
                    int opNum =  outParameter.size() ;
                    int totNum = ipNum + opNum ;
                    //String quest="";
                    for (int i=0 ; i<totNum; i++)
                    {
                        sb.append("?,");
                    }

                    sb.deleteCharAt(sb.length() - 1);

                    //quest="{call " +procedure+ "(" +quest + ")}";

                    sb.insert(0, "{call "+procedure+ "(");

                    sb.append(")}");

                    String quest=sb.toString();

                    CallableStatement cs=conn.prepareCall(quest);
                    for (Map.Entry<Integer, Object> entry : inputParameter.entrySet())
                    {
                        cs.setObject(entry.getKey(), entry.getValue());
                    }

                    for (Map.Entry<Integer, Integer> entry : outParameter.entrySet())
                    {
                        cs.registerOutParameter(entry.getKey()+ipNum, entry.getValue());
                    }

                    cs.execute();//执行


                    for (Map.Entry<Integer, Integer> entry : outParameter.entrySet())
                    {
                        map.put(entry.getKey(),cs.getObject(entry.getKey()+ipNum ) );
                    }
                    r.setMap(map);
                    r.setResult(true);
                    map=null;
                }
                catch(Exception e)
                {
                    r.setResult(false);
                    map=null;
                    r.setMap(map);
                    throw new SQLException(e.toString());
                }
                finally
                {
                    if (sb!=null)
                    {
                        sb.setLength(0);
                        sb=null;
                    }
                }
            }
        });
        return r.getMap();
    }

    private class storedProcedureResult {
        private boolean result;
        private Map<Integer, Object> map;

        public boolean isResult() {
            return result;
        }
        public void setResult(boolean result) {
            this.result = result;
        }
        public Map<Integer, Object> getMap() {
            return map;
        }
        public void setMap(Map<Integer, Object> map) {
            this.map = map;
        }

    }

    @Override
    public boolean storedProcedureProcess(Connection conn, String procedure,final Map<Integer,Object> inputParameter) throws Exception {
        // TODO 自动生成的方法存根
        //		final storedProcedureResult r = new storedProcedureResult();
        //Map<Integer, Object> map = new HashMap<Integer, Object>(); //要傳回的格式
        StringBuffer sb = new StringBuffer();
        String storedString ="";
        CallableStatement cs = null;
        try
        {
            int ipNum =  inputParameter.size() ;
            for (int i=0 ; i<ipNum; i++)
            {
                sb.append("?,");
            }

            sb.deleteCharAt(sb.length() - 1);

            sb.insert(0, "{call "+procedure+ "(");

            sb.append(")}");

            String quest=sb.toString();

            cs=conn.prepareCall(quest);

            storedString="call "+procedure+ "(";
            for (Map.Entry<Integer, Object> entry : inputParameter.entrySet())
            {
                cs.setObject(entry.getKey(), entry.getValue());
                storedString=storedString+"'";
                storedString=storedString+entry.getValue();
                storedString=storedString+"'";
                storedString=storedString+",";
            }

            if (storedString.length()>0) {
                storedString = storedString.substring(0, storedString.length() - 1);
            }
            storedString=storedString+")";

            //注释输出，以减少日志占用 by jinzma 20210922
            //loger.info("\r\n**********存储过程执行语句*********\r\n" + storedString+"\r\n");

            boolean bOK = cs.execute(); //有异常会抛出且回滚 BY JZMA 20201209
            cs.close();

            return true;
        }

        catch (SQLTimeoutException e) //SQL超时
        {
            //记录
            errSql.append("\r\n" + storedString + "\r\n");
            loger.error("\r\n**********存储过程执行超时*********\r\n" + storedString+"\r\n"+ e.getMessage());
            throw new SQLException("执行超时!");
        }
        catch (SQLException e) //SQL异常
        {
            //记录
            errSql.append("\r\n" + storedString + "\r\n");
            loger.error("\r\n**********存储过程(storedProcedureProcess)发生异常*********\r\n" + storedString +"\r\n"+ e.getMessage());
            throw e;
        }
        catch(Exception e)
        {
            errSql.append("\r\n" + storedString + "\r\n");
            loger.error("\r\n**********存储过程(storedProcedureProcess)发生异常*********\r\n" + storedString +"\r\n"+ e.getMessage());
            throw new SQLException(e.toString());
        }

        finally
        {
            if (sb!=null) {
                sb.setLength(0);
                sb=null;
            }
            if (cs!=null){
                cs.close();
                cs = null;
            }
        }
    }

    /**
     * 绑定变量SQL的写法(注意仅适用于简单SQL语句，较复杂的SQL不要用)
     * @param sql
     * @param values
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> executeQuerySQL_BindSQL(String sql, List<DataValue> values) throws Exception
    {
        final Result r = new Result();

        hibernateTemplate.getSessionFactory().getCurrentSession().doWork(new Work()
        {
            @Override
            public void execute(Connection conn) throws SQLException
            {
                List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(); //要傳回的格式

                try
                {
                    result=SPosDAOImpl.this.GetNativeSQL_BindSQL(conn,sql, values);
                    r.setDatas(result);
                    result=null;
                }
                catch(Exception e)
                {
                    r.setDatas(result);
                    result=null;
                    throw new SQLException(e.toString());
                }
            }

        });

        return r.getDatas();
    }

    private List<Map<String, Object>> GetNativeSQL_BindSQL(Connection conn,String sql, List<DataValue> values) throws Exception
    {
        //
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try
        {
            //
            pst=conn.prepareStatement(sql);

            pst.setQueryTimeout(PosPub.iTimeoutTime);//30秒超时

            conn.setAutoCommit(Boolean.FALSE);

            try
            {
                for (int i = 0; i < values.size(); i++)
                {
                    DataValue v = values.get(i);
                    if (Types.DATE == v.getDataType())
                    {
                        //等于null不做默认处理，容易日期搞错就过了
                        if (v.getValue()!=null)
                        {
                            //2019-12-18 日期格式
                            if (v.getValue().toString().length()==10)
                            {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = simpleDateFormat.parse(v.getValue().toString());
                                pst.setDate(i+1 , new java.sql.Date(date.getTime()));
                            }
                            //2019-12-18 16:15:29日期时间格式
                            else if (v.getValue().toString().length()==19)
                            {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = simpleDateFormat.parse(v.getValue().toString());
                                pst.setDate(i+1 , new java.sql.Date(date.getTime()));
                            }
                            else
                            {
                                //pst.setDate(i+1 , (java.sql.Date)v.getValue());//暂不处理
                                pst.setDate(i+1 , null);//暂不处理
                            }
                        }
                        else
                        {
                            //pst.setDate(i+1 , (java.sql.Date)v.getValue());//暂不处理
                            pst.setDate(i+1 , null);//暂不处理
                        }
                    }
                    else if (Types.BLOB == v.getDataType())
                    {
                        if(v.getValue()==null ||v.getValue().toString().length()==0)
                        {
                            pst.setBlob(i+1 ,null, v.getDataType());
                        }
                        else
                        {
                            InputStream in_withcode=new ByteArrayInputStream(v.getValue().toString().getBytes("UTF-8"));
                            pst.setBlob(i+1 ,in_withcode, in_withcode.available());
                            in_withcode.close();
                            in_withcode=null;
                        }
                    }
                    else if (Types.CLOB == v.getDataType())
                    {
                        if(v.getValue()==null ||v.getValue().toString().length()==0)
                        {
                            pst.setClob(i+1 ,null, v.getDataType());
                        }
                        else
                        {
                            Reader reader = new StringReader(v.getValue().toString());
                            pst.setClob(i+1 ,reader);
                            reader.close();
                            reader=null;
                        }
                    }
                    else
                    {
                        //处理null
                        Object sgetValue;
                        if (Types.INTEGER == v.getDataType() || Types.DECIMAL == v.getDataType()||Types.FLOAT == v.getDataType()||Types.DOUBLE == v.getDataType())
                        {
                            sgetValue=v.getValue()==null||v.getValue().toString().isEmpty()?0:v.getValue();
                        }
                        else
                        {
                            sgetValue=v.getValue()==null?"":v.getValue().toString();
                        }

                        pst.setObject(i+1 , sgetValue, v.getDataType());
                    }
                }
            }
            catch (Exception e)
            {
                loger.error("\r\n**********GetNativeSQL_BindSQL服务执行SQL查询参数设置失败*********\r\n" + sql + "\r\n" + e.getMessage());
            }

            rs = pst.executeQuery();

            conn.commit();

            rs.setFetchSize(PosPub.iFetchSize);

            ResultSetMetaData md=rs.getMetaData();

            Map<String, Object> m=null;
            while(rs.next())
            {
                m = new HashMap<String, Object>();
                Object value;
                for(int i = 0 ; i < md.getColumnCount() ; i++)
                {
                    //解决返回LONG类型值不是null的情况下会出现 ====流关闭报错
                    if(md.getColumnTypeName(i + 1).equals("LONG"))
                    {
                        value=rs.getString(md.getColumnName(i + 1));
                        if (value==null)
                        {
                            value="";
                        }
                    }
                    else if(md.getColumnTypeName(i + 1).equals("DATE"))
                    {
                        value=rs.getDate(md.getColumnName(i + 1));
                        if (value!=null &&value.toString().trim().equals("")==false)
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            value=sdf.format(rs.getDate(md.getColumnName(i + 1)));
                            sdf=null;
                        }
                        else
                        {
                            value="";
                        }
                    }
                    else if (rs.getObject(md.getColumnName(i + 1))==null)
                    {
                        value="";
                    }
                    else if(rs.getObject(md.getColumnName(i + 1)).getClass().getTypeName().equals("byte[]"))
                    {
                        byte[] xx = (byte[])rs.getObject(md.getColumnName(i + 1));
                        StringBuffer GUIDraw=new StringBuffer("");
                        for (byte b : xx)
                        {
                            String hex = Integer.toHexString(b & 0xFF);
                            if (hex.length() == 1)
                            {
                                hex = '0' + hex;
                            }
                            GUIDraw.append(hex.toUpperCase());
                        }
                        value=GUIDraw.toString();
                    }
                    else if(md.getColumnTypeName(i + 1).equals("CLOB"))
                    {
                        value=rs.getString(md.getColumnName(i + 1));
                    }
                    else
                    {
                        value=rs.getObject(md.getColumnName(i + 1));
                    }

                    m.put(md.getColumnName(i+1).toUpperCase(), value);
                }

                value=null;

                result.add(m);
            }

            md=null;

            //清理
            m=null;

            return result;

        }
        catch (SQLTimeoutException e) //SQL超时
        {
            conn.rollback();

            loger.error("\r\n**********SQLTimeoutExceptionSQL执行超时*********\r\n" + sql + "\r\n" + e.getMessage());

            throw new SQLException("执行超时!");
        }
        catch (SQLException e) //SQL异常
        {
            conn.rollback();

            loger.error("\r\n**********SQLException服务执行SQL查询失败*********\r\n" + sql + "\r\n" + e.getMessage());

            throw new SQLException("服务执行SQL查询失败，"+ e.getMessage()+"!");
        }
        catch (Exception e)
        {
            conn.rollback();

            loger.error("\r\n**********Exception服务执行SQL查询失败*********\r\n" + sql + "\r\n" + e.getMessage());

            throw new Exception("服务执行SQL查询失败，"+ e.getMessage()+"!");
        }
        finally
        {

            if(pst != null)
            {
                pst.clearParameters();
                pst.close();
                pst = null;
            }

            if(rs != null)
            {
                rs.close();
                rs = null;
            }

            result=null;
            sql=null;
            values=null;

        }

    }



}
