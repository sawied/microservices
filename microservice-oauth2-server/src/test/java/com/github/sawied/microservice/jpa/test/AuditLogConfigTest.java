package com.github.sawied.microservice.jpa.test;

import java.util.Properties;
import java.util.Random;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.envers.configuration.EnversSettings;
import org.hibernate.envers.strategy.ValidityAuditStrategy;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.sawied.microservice.oauth2.UserContext;
import com.github.sawied.microservice.oauth2.jpa.AuthoritieRepository;
import com.github.sawied.microservice.oauth2.jpa.GroupRepository;
import com.github.sawied.microservice.oauth2.jpa.UserRepository;
import com.github.sawied.microservice.oauth2.jpa.entity.Authoritie;
import com.github.sawied.microservice.oauth2.jpa.entity.Group;
import com.github.sawied.microservice.oauth2.jpa.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaConfig.class })
public class AuditLogConfigTest {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private AuthoritieRepository authoriteRepository;
	
	@Test
	@Ignore
	public void setup(){
		repository.findAll();
	}

	@Test
	@Ignore
	public void accountAdd() {
		User user = new User();
		user.setName(generateName());
		user.setPassword("{password}");
		user.setEnabled(Boolean.TRUE);
		user.setEmail("danan.2008@hotmail.com");
		user.setAddress("district");
		repository.createUser(user);
		Assert.assertNotNull("create User id is null ", user.getId());
	}
	
	@Test
	@Ignore
	public void groupAdd() {
		Group group = new Group();
		String generateName = generateName();
	    group.setEntitlementName(generateName);
	    group.setEntitlementDescription(generateName);
	    Authoritie authoritie = new Authoritie();
	    authoritie.setId(1);
	    group.getAuthorities().add(authoritie);
	    groupRepository.save(group);
	    Assert.assertNotNull("create User id is null ", group.getId());
	}

	@Test
	//@Ignore
	public void accountModify() {
		User account = repository.getUserByName("wfpf");
		Integer id = account.getId();
		User u = new User();
		u.setId(id);
		u.setName("wfpf");
		u.setPassword("password");
		u.setEmail("sawied.2002@gmail.com");
		u.setEnabled(true);
		Group group2 = new Group();group2.setId(2);
		Group group3 = new Group();group3.setId(3);
		u.getGroups().add(group2);
		u.getGroups().add(group3);
		repository.modifyUserByProperties(u);
	}

	
	@Test
	@Ignore
	public void authorizationAdd() {
		Authoritie authoritie = new Authoritie();
		authoritie.setAuthority("admin");
		System.out.println(authoriteRepository.save(authoritie).getId());
	}

	private String generateName() {
		// 65-122;
		Random random = new Random();
		int lenght = 4;
		char[] chars = new char[lenght];
		for (int i = 0; i < lenght; i++) {
			int r = random.nextInt(26);
			r += 97;
			chars[i] = (char) r;
		}
		return String.valueOf(chars);

	}

}

@Configuration
@EnableJpaRepositories(basePackages = { "com.github.sawied.microservice.oauth2.jpa" })
@EnableTransactionManagement
@EnableJpaAuditing
class JpaConfig {
	/**
	 * @Bean public DataSource dataSource() { EmbeddedDatabaseBuilder builder = new
	 *       EmbeddedDatabaseBuilder(); return
	 *       builder.setType(EmbeddedDatabaseType.DERBY).build(); }
	 **/

	@Bean
	public DataSource dataSource() {

		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		driverManagerDataSource.setUrl(
				"jdbc:mysql://localhost:3306/trade?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=GMT%2B8");
		driverManagerDataSource.setUsername("trade");
		driverManagerDataSource.setPassword("password");
		return driverManagerDataSource;
	}

	@Bean
	public LocalSessionFactoryBean entityManagerFactory(DataSource dataSource,
			ConfigurableListableBeanFactory beanFactory) {
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", true);
		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
		//properties.put("hibernate.hbm2ddl.auto", "create-drop");//
		properties.put("hibernate.hbm2ddl.auto", "update");//
		properties.put("org.hibernate.envers.revision_on_collection_change", true);
		properties.put(EnversSettings.AUDIT_STRATEGY,ValidityAuditStrategy.class.getName());
		properties.put(AvailableSettings.BEAN_CONTAINER,new SpringBeanContainer(beanFactory));
		LocalSessionFactoryBean localSessionFactory = new LocalSessionFactoryBean();
		localSessionFactory.setHibernateProperties(properties);
		localSessionFactory.setDataSource(dataSource);
		localSessionFactory.setPackagesToScan("com.github.sawied.microservice.oauth2.jpa.entity");
		//localSessionFactory.setBeanFactory(beanFactory);
		return localSessionFactory;
	}

	/**
	 * @Bean public LocalContainerEntityManagerFactoryBean
	 *       entityManagerFactory(DataSource
	 *       dataSource,ConfigurableListableBeanFactory beanFactory) {
	 *       HibernateJpaVendorAdapter vendorAdapter = new
	 *       HibernateJpaVendorAdapter(); vendorAdapter.setGenerateDdl(true);
	 *       vendorAdapter.getJpaPropertyMap().put(AvailableSettings.BEAN_CONTAINER,new
	 *       SpringBeanContainer(beanFactory));
	 * 
	 *       LocalContainerEntityManagerFactoryBean factory = new
	 *       LocalContainerEntityManagerFactoryBean();
	 *       factory.setJpaVendorAdapter(vendorAdapter);
	 *       factory.setPackagesToScan("com.github.sawied.microservice.oauth2.jpa.entity");
	 *       factory.setDataSource(dataSource);
	 * 
	 * 
	 * 
	 *       Properties properties = new Properties();
	 *       properties.put(EnversSettings.AUDIT_STRATEGY,
	 *       ValidityAuditStrategy.class.getName());
	 *       properties.put("hibernate.show_sql", true);
	 *       //properties.put(AvailableSettings.BEAN_CONTAINER,new
	 *       SpringBeanContainer(beanFactory)); //properties.put(EnversSettings.,
	 *       value)
	 * 
	 *       factory.setJpaProperties(properties);
	 * 
	 * 
	 *       return factory; }
	 **/

	@Bean
	public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);
		return txManager;
	}

	
	@Bean
	public UserContext userContext(){
		return new UserContext();
		
	}
	/**
	@Bean
	public AccountRevisionEntityListener accountRevisionEntityListener() {
		AccountRevisionEntityListener accountRevisionEntityListener=new AccountRevisionEntityListener().userContext(new UserContext());
		System.out.println(accountRevisionEntityListener.toString());
		return accountRevisionEntityListener;
	}**/

}