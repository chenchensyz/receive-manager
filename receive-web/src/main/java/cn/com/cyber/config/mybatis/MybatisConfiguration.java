package cn.com.cyber.config.mybatis;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class MybatisConfiguration implements TransactionManagementConfigurer {

	@Autowired
	private DataSource dataSource;

	// 提供SqlSeesion
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactoryBean() {
		try {
			SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
			sessionFactoryBean.setDataSource(dataSource);
			// 手写配置
			// 配置类型别名
			sessionFactoryBean.setTypeAliasesPackage("cn.com.cyber.model");

			// 配置mapper的扫描，找到所有的mapper.xml映射文件
			Resource[] resources = new PathMatchingResourcePatternResolver()
					.getResources("classpath:cn/com/cyber/mapper/*.xml");
			sessionFactoryBean.setMapperLocations(resources);

			// 加载全局的配置文件
			sessionFactoryBean
					.setConfigLocation(new DefaultResourceLoader().getResource("classpath:mybatis-config.xml"));

			// 添加插件
//			sessionFactoryBean.setPlugins(new Interceptor[] { pageHelper() });

			return sessionFactoryBean.getObject();
		} catch (IOException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Bean
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public PageHelper pageHelper() {
		// 分页插件
		PageHelper pageHelper = new PageHelper();
		Properties properties = new Properties();
		properties.setProperty("offsetAsPageNum", "true");
		properties.setProperty("rowBoundsWithCount", "true");
		properties.setProperty("reasonable", "true");
		properties.setProperty("supportMethodsArguments", "true");
		properties.setProperty("returnPageInfo", "check");
		properties.setProperty("params", "count=countSql");
		pageHelper.setProperties(properties);
		return pageHelper;
	}

}