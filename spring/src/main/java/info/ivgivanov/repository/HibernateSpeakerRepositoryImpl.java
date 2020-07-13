package info.ivgivanov.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import info.ivgivanov.model.Speaker;

@Repository("speakerRepository")
public class HibernateSpeakerRepositoryImpl implements SpeakerRepository {
	
	public List<Speaker> findAll() {
		
		List<Speaker> speakers = new ArrayList<Speaker>();
		
		Speaker speaker = new Speaker();
		speaker.setFirstName("Ivaylo");
		speaker.setLastName("Ivanov");
		
		speakers.add(speaker);
		
		return speakers;
	}

}
