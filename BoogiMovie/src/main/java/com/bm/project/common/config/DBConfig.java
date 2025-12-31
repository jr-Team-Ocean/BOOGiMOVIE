package com.bm.project.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@MapperScan("com.bm.project.**.model.dao")
public class DBConfig {
   
    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            ApplicationContext applicationContext
    ) throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(
                applicationContext.getResources("classpath:/mappers/**/*.xml")
        );
//        factoryBean.setTypeAliasesPackage("com.bm.project");
        factoryBean.setConfigLocation(
                applicationContext.getResource("classpath:mybatis-config.xml")
        );

        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(
            SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}



//@Configuration
//@MapperScan("com.bm.project.chatting.model.dao")
//public class DBConfig {
//
//	
//	@Autowired
//	private ApplicationContext applicationContext; 
//	
//	@Bean
//	@ConfigurationProperties(prefix="spring.datasource.hikari")
//	public HikariConfig hikariConfig() {
//		return new HikariConfig();
//	}
//	
//	@Bean
//	public DataSource dataSource(HikariConfig config) {
//
//		DataSource dataSource = new HikariDataSource(config);
//
//		return dataSource;
//	}
//	
//	//SqlSessionFactory : SqlSession을 만드는 객체
//	@Bean
//	@ConfigurationProperties(prefix="mybatis")
//	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception{
//		
//		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//		sessionFactoryBean.setDataSource(dataSource);
//		
//		// SqlSession 객체 반환
//		return sessionFactoryBean.getObject();
//	}
//	
//	// SqlSessionTemplate : 기본 SQL 실행 + 트랜잭션 처리
//	@Bean
//	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sessionFactory) {
//		return new SqlSessionTemplate(sessionFactory);
//	}
//	
////	// DataSourceTransactionManager : 트랜잭션 매니저
////	@Bean
////	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
////		return new DataSourceTransactionManager(dataSource);
////	}
//
//}
