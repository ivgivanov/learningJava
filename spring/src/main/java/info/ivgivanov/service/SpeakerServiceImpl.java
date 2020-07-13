package info.ivgivanov.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.ivgivanov.model.Speaker;
import info.ivgivanov.repository.SpeakerRepository;

@Service("speakerService")
public class SpeakerServiceImpl implements SpeakerService {
	
	private SpeakerRepository repository;
	
	
	public SpeakerServiceImpl() {
		System.out.println("SpeakerServiceImpl no args constructor");
	}
	
	
	public void setRepository(SpeakerRepository repository) {
		System.out.println("SpeakerServiceImpl setter");
		this.repository = repository;
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
