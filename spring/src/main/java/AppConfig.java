import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import info.ivgivanov.repository.HibernateSpeakerRepositoryImpl;
import info.ivgivanov.repository.SpeakerRepository;
import info.ivgivanov.service.SpeakerService;
import info.ivgivanov.service.SpeakerServiceImpl;

@Configuration
public class AppConfig {
	
	@Bean(name = "speakerService")
	public SpeakerService getSpeakerService() {
		SpeakerServiceImpl service = new SpeakerServiceImpl(getSpeakerRepository());
		//service.setRepository(getSpeakerRepository());
		return service;
		
	}

	@Bean(name = "speakerRepostitory")
	public SpeakerRepository getSpeakerRepository() {
		return new HibernateSpeakerRepositoryImpl();
	}
}
