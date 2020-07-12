package info.ivgivanov.service;

import java.util.List;

import info.ivgivanov.model.Speaker;
import info.ivgivanov.repository.SpeakerRepository;

public class SpeakerServiceImpl implements SpeakerService {
	
	private SpeakerRepository repository;
	
	
	public void setRepository(SpeakerRepository repository) {
		this.repository = repository;
	}

	
	public SpeakerServiceImpl(SpeakerRepository speakerRepository) {
		
		repository = speakerRepository;
		
	}


	public List<Speaker> findAll() {
		
		return repository.findAll();
		
	}

}
