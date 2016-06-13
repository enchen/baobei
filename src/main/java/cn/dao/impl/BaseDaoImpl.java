package cn.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.dao.BaseDaoI;

@Transactional
@SuppressWarnings("unchecked")
@Repository("baseDao")
public class BaseDaoImpl<T> implements BaseDaoI<T> {
	
    private JdbcTemplate jdbcTemplate;
    
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Session getCurrentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Long count(String hql, Object... param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return (Long) q.uniqueResult();
	}

	public Long count(String hql, List<Object> param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return (Long) q.uniqueResult();
	}

	public Integer countByJdbc(String sql, Object... param) {
		return this.jdbcTemplate.queryForObject(sql, param,Integer.class);
	}

	public void delete(Object o) {
		Session session=sessionFactory.openSession();
		Transaction ts=session.beginTransaction();
		try{
			session.delete(o);
			ts.commit();
			session.close();
		}catch(Exception e){
			e.printStackTrace();
			ts.rollback();
		}
	}

	public void evict(Object o) {
		this.getCurrentSession().evict(o);
	}

	public Integer executeHql(String hql) {
		Query q=this.getCurrentSession().createQuery(hql);
		return q.executeUpdate();
	}

	public Integer executeHql(String hql, Object... param) {
		Query q=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.length>0){
			for(int i=0;i<param.length;i++){
				q.setParameter(i, param[i]);
			}
		}
		return q.executeUpdate();
	}

	public Integer executeHql(String hql, List<Object> param) {
		Query q=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.size()>0){
			for(int i=0;i<param.size();i++){
				q.setParameter(i, param.get(i));
			}
		}
		return q.executeUpdate();
	}

	public Integer executeSql(String sql, List<Object> param) {
		return this.jdbcTemplate.update(sql, param);
	}

	public Integer executeSql(String sql, Object... param) {
		return this.jdbcTemplate.update(sql, param);
	}

	public List find(String hql, Object... param) {
		Query query=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.length>0){
			for(int i=0;i<param.length;i++){
				query.setParameter(i, param[i]);
			}
		}
		return query.list();
	}

	
	public List<T> find(String hql, List<Object> param) {
		Query query=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.size()>0){
			for(int i=0;i<param.size();i++){
				query.setParameter(i, param.get(i));
			}
		}
		return query.list();
	}

	public List find(String hql, int page, int rows, List<Object> param) {
		Query query=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.size()>0){
			for(int i=0;i<param.size();i++){
				query.setParameter(i, param.get(i));
			}
		}
		return query.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	public List find(String hql, int page, int rows, Object... param) {
		Query query=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.length>0){
			for(int i=0;i<param.length;i++){
				query.setParameter(i, param[i]);
			}
		}
		return query.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	public List<Map<String, Object>> findForJdbc(String sql, Object... objs) {
		return this.jdbcTemplate.queryForList(sql, objs);
	}

	/**
	 * δ�����ҳ������ʱ����(2013.6.10
	 */
	public List<Map<String, Object>> findForJdbc(String sql, int page, int rows) {
		return this.jdbcTemplate.queryForList(sql);
	}

	/**
	 * δ�����ҳ������ʱ����(2013.6.10
	 */
	public List<Map<String, Object>> findForJdbcParam(String sql, int page, int rows, Object... objs) {
		
		return this.jdbcTemplate.queryForList(sql, objs);
	}

	public List<T> findObjForJdbc(String sql, int page, int rows, Class<T> clazz) {
		return this.jdbcTemplate.queryForList(sql, clazz);
	}
	
	public List<T> findObjForJdbc(String sql,Class<T> clazz,Object... objs){
		return this.jdbcTemplate.queryForList(sql, clazz, objs);
	}

	public Map<String,Object> findOneForJdbc(String sql, Object... objs) {
		return this.jdbcTemplate.queryForMap(sql, objs);
	}

	public T get(Class<T> c, Serializable id) {
		return (T)this.getCurrentSession().get(c, id);
	}

	public T get(String hql, Object... param) {
		Query query=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.length>0){
			for(int i=0;i<param.length;i++){
				query.setParameter(i, param[i]);
			}
		}
		return (T)query.uniqueResult();
	}

	public T get(String hql, List<Object> param) {
		Query query=this.getCurrentSession().createQuery(hql);
		if(param!=null&&param.size()>0){
			for(int i=0;i<param.size();i++){
				query.setParameter(i, param.get(i));
			}
		}
		return (T)query.uniqueResult();
	}

	public Long getCountForJdbc(String sql) {
		return this.jdbcTemplate.queryForObject(sql,Long.class);
	}

	public Long getCountForJdbcParam(String sql, Object[] objs) {
		return this.jdbcTemplate.queryForObject(sql, objs,Long.class);
	}

	

	public void merge(Object o) {
		this.getCurrentSession().merge(o);
	}

	public void save(Object o) {
		this.getCurrentSession().save(o);
	}

	public void saveOrUpdate(Object o) {
		this.getCurrentSession().saveOrUpdate(o);
	}

	public void update(Object o) {
		this.getCurrentSession().update(o);
	}

	

}