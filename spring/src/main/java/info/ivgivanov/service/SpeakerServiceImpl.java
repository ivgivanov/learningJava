package info.ivgivanov.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import info.ivgivanov.model.Speaker;
import info.ivgivanov.repository.SpeakerRepository;

@Service("speakerService")
@Profile("dev")
public class SpeakerServiceImpl implements SpeakerService {
	
	private SpeakerRepository repository;
	
	
	public SpeakerServiceImpl() {
		System.out.println("SpeakerServiceImpl no args constructor");
	}
	
	
	public void setRepository(SpeakerRepository repository) {
		System.out.println("SpeakerServiceImpl setter");
		this.repository = repository;
	}
	
	@PostConstruct
	private void initialize() {
		System.out.println("Called after constructors");
	}

	@Autowired
	public SpeakerServiceImpl(SpeakerRepository speakerRepository) {
		System.out.println("SpeakerServiceImpl repository constructor");
		repository = speakerRepository;
		
	}


	public List<Speaker> findAll() {
		
		return repository.findAll();
		
	}

}
