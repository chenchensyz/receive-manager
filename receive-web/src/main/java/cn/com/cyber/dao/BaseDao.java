package cn.com.cyber.dao;
import java.util.List;

public  interface BaseDao<T>{
	public T selectByPrimaryKey(Long id);

	public int deleteByPrimaryKey(Long id);

	public int insertSelective(T t);

	public int updateByPrimaryKeySelective(T t);

	public List<T> getList(T t);

	// 获取数量
	public int getCountSelective(T t);

	/**
	 * 
	 * @Title: findPage
	 * @Description: TODO()
	 * @param page
	 *            分页参数
	 * @param sql
	 *            mybatis sql语句
	 * @param values
	 *            命名参数,按名称绑定
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 * @author xumin
	 * @date 2016年9月7日 下午5:30:28
	 */
//	public PageInfo<T> findPage(final PageInfo<T> page, final String sql, final Map<String, Object> values);
}