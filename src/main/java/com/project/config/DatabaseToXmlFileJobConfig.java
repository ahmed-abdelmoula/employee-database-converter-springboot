package com.project.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.project.model.User;

@Configuration
@EnableBatchProcessing
@PersistenceContext

public class DatabaseToXmlFileJobConfig {

	/*
	 * @Autowired private JobBuilderFactory jobBuilderFactory;
	 * 
	 * @Autowired private StepBuilderFactory stepBuilderFactory;
	 */

	@Autowired
	private DataSource dataSource;

	/*
	 * @Bean public StaxEventItemReader<User> reader(){
	 * 
	 * StaxEventItemReader<User> reader = new StaxEventItemReader<User>();
	 * reader.setResource(new ClassPathResource("users.xml"));
	 * //reader.setResource(new FileSystemResource(UserController.ch));
	 * reader.setFragmentRootElementName("user"); Map<String,String> aliasesMap =new
	 * HashMap<String,String>(); aliasesMap.put("user","com.project.model.User");
	 * XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
	 * xStreamMarshaller.setAliases(aliasesMap);
	 * 
	 * reader.setUnmarshaller(xStreamMarshaller);
	 * 
	 * return reader; }
	 * 
	 * @Bean public JdbcBatchItemWriter<User> writer(){ JdbcBatchItemWriter<User>
	 * writer = new JdbcBatchItemWriter<User>(); writer.setDataSource(dataSource);
	 * writer.setSql("INSERT INTO user(nom,prenom,cin) VALUES(?,?,?)");
	 * writer.setItemPreparedStatementSetter(new UserItemPreparedStmSetter());
	 * return writer; }
	 * 
	 * private class UserItemPreparedStmSetter implements
	 * ItemPreparedStatementSetter<User>{
	 * 
	 * @Override public void setValues(User user, PreparedStatement ps) throws
	 * SQLException { //ps.setInt(1, user.getId()); ps.setString(1, user.getNom());
	 * ps.setString(2, user.getPrenom()); ps.setString(3, user.getCin());
	 * 
	 * }
	 * 
	 * }
	 */
	@Bean
	public JdbcCursorItemReader<User> reader() {
		JdbcCursorItemReader<User> cursorItemReader = new JdbcCursorItemReader<>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setSql("SELECT id,nom,prenom,cin FROM user");
		cursorItemReader.setRowMapper(new UserRowMaper());
		return cursorItemReader;
	}

	public class UserRowMaper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User u = new User();
			u.setId(rs.getInt("id"));
			u.setNom(rs.getString("nom"));
			u.setPrenom(rs.getString("prenom"));
			u.setCin(rs.getString("cin"));
			return u;
		}

	}

	/*
	 * @Bean public UserItemProcessor processor(){ return new UserItemProcessor(); }
	 */

	@Bean
	public StaxEventItemWriter<User> writer() {
		StaxEventItemWriter<User> writer = new StaxEventItemWriter<User>();
		writer.setResource(new ClassPathResource("etud.xml"));

		Map<String, String> aliasesMap = new HashMap<String, String>();
		aliasesMap.put("user", "com.project.model.User");
		XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAliases(aliasesMap);
		writer.setMarshaller(marshaller);
		writer.setRootTagName("users");
		writer.setOverwriteOutput(true);
		return writer;
	}

	/*
	 * @Bean public ThreadPoolTaskExecutor taskExecutor() { ThreadPoolTaskExecutor
	 * taskExecutor = new ThreadPoolTaskExecutor();
	 * taskExecutor.setCorePoolSize(15); taskExecutor.setMaxPoolSize(20);
	 * taskExecutor.setQueueCapacity(30); return taskExecutor; }
	 */

	/*
	 * @Bean public JobLauncher jobLaunche(JobRepository jobRepository) {
	 * SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
	 * jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
	 * jobLauncher.setJobRepository(jobRepository); return jobLauncher; }
	 */

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemWriter<User> writer, ItemReader<User> reader) {
		return stepBuilderFactory.get("step1").<User, User>chunk(100).reader(reader).writer(writer).build();
	}

	@Bean
	public Job exportUserJob(JobBuilderFactory jobs, Step s1) {

		return jobs.get("exportUserJob").incrementer(new RunIdIncrementer()).flow(s1).end().build();

	}

}
