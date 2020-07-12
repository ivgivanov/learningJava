package info.ivgivanov.repository;

import java.util.List;

import info.ivgivanov.model.Speaker;

public interface SpeakerRepository {

	List<Speaker> findAll();

}