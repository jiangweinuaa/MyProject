package com.dsc.spos.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * 20241205 add by 11217
 * 用于查询数据库的实体数据映射为对应的对象模型
 */
@Repository("entityDao")
@Transactional
public class TableEntityDao {

    private Logger loger = LogManager.getLogger(TableEntityDao.class.getName());
    private static HibernateTemplate hibernateTemplate = new HibernateTemplate();

    @Qualifier("spos")
    @Resource(name = "sposSessionFactory")
    private SessionFactory sessionFactory;


    private <T>PersistentClass getPersistentClass(Class<T> clazz) {
        synchronized (TableEntityDao.class) {
            LocalSessionFactoryBean sessionFactoryBean = (LocalSessionFactoryBean) sessionFactory;
            Configuration configuration = sessionFactoryBean.getConfiguration();

            PersistentClass pc = configuration.getClassMapping(clazz.getSimpleName());
            if (pc == null) {
                configuration.addClass(clazz);
                pc = configuration.getClassMapping(clazz.getSuperclass().getSimpleName());
            }
            return pc;
        }
    }

    public <T> List<T> queryList(Class<T> clazz) throws Exception{

        Session session = sessionFactory.openSession();
        try {
            String sql = " SELECT * FROM %s ";
            sql = String.format(sql,getPersistentClass(clazz).getTable().getName());
            Query query = session.createSQLQuery(sql).addEntity(clazz);
//            query.setResultTransformer(Transformers.aliasToBean(clazz));
            return (List<T>) query.list();
        } catch (Exception e) {
            throw new SQLException("查询信息出错" + e.getClass() + e.getMessage());
        }
    }

    public <T> List<T> queryList(String sql, Class<T> clazz) throws Exception{
        Session session = sessionFactory.openSession();
        try {

            Query query = session.createSQLQuery(sql).addEntity(clazz);
//            query.setResultTransformer(Transformers.aliasToBean(clazz));
            return (List<T>) query.list();
        } catch (Exception e) {
            throw new SQLException("查询信息出错" + e.getClass() + e.getMessage());
        }
    }

    public <T> T queryOne(String sql, Class<T> clazz) throws Exception{
        try {
            List<?> list = queryList(sql, clazz);
            if(list != null && !list.isEmpty()){
                return (T)list.get(0);
            }
            return null;
        } catch (Exception e) {
            throw new SQLException("查询信息出错" + e.getClass() + e.getMessage());
        }
    }










}
