import java.util.Calendar;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import info.ivgivanov.repository.HibernateSpeakerRepositoryImpl;
import info.ivgivanov.repository.SpeakerRepository;
import info.ivgivanov.service.SpeakerService;
import info.ivgivanov.service.SpeakerServiceImpl;
import info.ivgivanov.util.CalendarFactory;

@Configuration
@ComponentScan({"info.ivgivanov"})
public class AppConfig {
	
	
	@Bean(name="cal")
	public CalendarFactory calFactory() {
		
		CalendarFactory factory = new CalendarFactory();
		factory.addDays(2);
		return factory;
	}
	
	@Bean
	public Calendar cal() throws Exception {
		
		return calFactory().getObject();
		
	}
//	
//	@Bean(name = "speakerService")
//	@Scope(value=BeanDefinition.SCOPE_SINGLETON)
//	public SpeakerService getSpeakerService() {
//		//SpeakerServiceImpl service = new SpeakerServiceImpl(getSpeakerRepository());
//		SpeakerServiceImpl service = new SpeakerServiceImpl();
//		//service.setRepository(getSpeakerRepository());
//		return service;
//		
//	}

//	@Bean(name = "speakerRepostitory")
//	public SpeakerRepository getSpeakerRepository() {
//		return new HibernateSpeakerRepositoryImpl();
//	}
}
