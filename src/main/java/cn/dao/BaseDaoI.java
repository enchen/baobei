package cn.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;


public interface BaseDaoI<T> {

	/**
	 * ����һ������
	 * 
	 * @param o
	 *            ����
	 */
	public void save(T o);

	
	/**
	 * ������� �־ö������ѹܶ���
	 */
	public void evict(T o);

	/**
	 * ����һ������
	 * 
	 * @param o
	 *            ����
	 */
	public void update(T o);

	/**
	 * �������¶���
	 * 
	 * @param o
	 *            ����
	 */
	public void saveOrUpdate(T o);

	/**
	 * �ϲ�һ������
	 * 
	 * @param o
	 *            ����
	 */
	public void merge(T o);

	/**
	 * ɾ��һ������
	 * 
	 * @param o
	 *            ����
	 */
	public void delete(T o);

	/**
	 * ���Ҷ��󼯺�
	 * 
	 * @param hql
	 * @param param
	 * @return List<T>
	 */
	public List<T> find(String hql, Object... param);

	/**
	 * ���Ҷ��󼯺�
	 * 
	 * @param hql
	 * @param param
	 * @return List<T>
	 */
	public List<T> find(String hql, List<Object> param);

	/**
	 * ���Ҷ��󼯺�,����ҳ
	 * 
	 * @param hql
	 * @param page
	 *            ��ǰҳ
	 * @param rows
	 *            ÿҳ��ʾ��¼��
	 * @param param
	 * @return ��ҳ���List<T>
	 */
	public List<T> find(String hql, int page, int rows, List<Object> param);

	/**
	 * ���Ҷ��󼯺�,����ҳ
	 * 
	 * @param hql
	 * @param page
	 *            ��ǰҳ
	 * @param rows
	 *            ÿҳ��ʾ��¼��
	 * @param param
	 * @return ��ҳ���List<T>
	 */
	public List<T> find(String hql, int page, int rows, Object... param);

	/**
	 * ���һ������
	 * 
	 * @param c
	 *            ��������
	 * @param id
	 * @return Object
	 */
	public T get(Class<T> c, Serializable id);

	/**
	 * ���һ������
	 * 
	 * @param hql
	 * @param param
	 * @return Object
	 */
	public T get(String hql, Object... param);

	/**
	 * ���һ������
	 * 
	 * @param hql
	 * @param param
	 * @return Object
	 */
	public T get(String hql, List<Object> param);

	/**
	 * select count(*) from ��
	 * 
	 * @param hql
	 * @param param
	 * @return Long
	 */
	public Long count(String hql, Object... param);
	
	
	/**
	 * select count(*) from ��
	 * 
	 * @param hql
	 * @param param
	 * @return Long
	 */
	public Integer countByJdbc(String sql, Object... param);

	/**
	 * select count(*) from ��
	 * 
	 * @param hql
	 * @param param
	 * @return Long
	 */
	public Long count(String hql, List<Object> param);

	/**
	 * ִ��HQL���
	 * 
	 * @param hql
	 * @return ��Ӧ��Ŀ
	 */
	public Integer executeHql(String hql);

	/**
	 * ִ��HQL���
	 * 
	 * @param hql
	 * @param param
	 * @return
	 */
	public Integer executeHql(String hql, Object... param);

	/**
	 * ִ��HQL���
	 * 
	 * @param hql
	 * @param param
	 * @return
	 */
	public Integer executeHql(String hql, List<Object> param);
	
	
	/**
	 * ִ��SQL
	 */
	public Integer executeSql(String sql, List<Object> param);
	
	/**
	 * ִ��SQL
	 */
	public Integer executeSql(String sql, Object... param);
	
	/**
	 * ���� JdbcTemplate
	 * @return
	 */
	public JdbcTemplate getJdbcTemplate();
	
	/**
	 * ͨ��JDBC���Ҷ��󼯺�
	 * ʹ��ָ���ļ�����׼�������ݷ�������
	 */
	public List<Map<String, Object>> findForJdbc(String sql,Object... objs);
	
	
	/**
	 * ͨ��JDBC���Ҷ��󼯺�
	 * ʹ��ָ���ļ�����׼�������ݷ�������
	 */
	public Map<String, Object> findOneForJdbc(String sql,Object... objs);
	
	/**
	 * ͨ��JDBC���Ҷ��󼯺�,����ҳ
	 * ʹ��ָ���ļ�����׼�������ݲ���ҳ��������
	 */
	public List<Map<String, Object>> findForJdbc(String sql, int page, int rows);
	
	/**
	 * ͨ��JDBC��ѯ�б�
	 * @param sql
	 * @param clazz
	 * @param objs
	 * @return
	 */
	public List<T> findObjForJdbc(String sql,Class<T> clazz,Object... objs);
	
	/**
	 * ͨ��JDBC���Ҷ��󼯺�,����ҳ
	 * ʹ��ָ���ļ�����׼�������ݲ���ҳ��������
	 */
	public List<T> findObjForJdbc(String sql, int page, int rows,Class<T> clazz) ;
	
	
	/**
	 * ʹ��ָ���ļ�����׼�������ݲ���ҳ��������-����Ԥ����ʽ
	 * 
	 * @param criteria
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DataAccessException
	 */
	public List<Map<String, Object>> findForJdbcParam(String  sql,  int page, int rows,Object... objs);
	
	/**
	 * ʹ��ָ���ļ�����׼�������ݲ���ҳ��������For JDBC
	 */
	public Long getCountForJdbc(String  sql) ;
	/**
	 * ʹ��ָ���ļ�����׼�������ݲ���ҳ��������For JDBC-����Ԥ����ʽ
	 * 
	 */
	public Long getCountForJdbcParam(String  sql,Object[] objs);
	
}
