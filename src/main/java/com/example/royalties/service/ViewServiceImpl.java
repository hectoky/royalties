package com.example.royalties.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.royalties.entity.EpisodeEntity;
import com.example.royalties.entity.StudioEntity;
import com.example.royalties.model.ViewRequest;
import com.example.royalties.repository.EpisodeRepository;
import com.example.royalties.repository.StudioRepository;

@Service
public class ViewServiceImpl implements ViewService {

	private static Logger logger = LoggerFactory.getLogger(ViewServiceImpl.class);

	private Map<String, AtomicLong> viewsInMemory;

	private StudioRepository studioRepository;

	private EpisodeRepository episodeRepository;

	@Autowired
	public ViewServiceImpl(EpisodeRepository episodeRepository, StudioRepository studioRepository) {
		this.episodeRepository = episodeRepository;
		this.studioRepository = studioRepository;
		// Create a map in memory with the keys number of reference of studio in DB and
		// Value an AtomicLong in order to calculate the number of views
		viewsInMemory = new HashMap<String, AtomicLong>();
		Iterable<StudioEntity> data= studioRepository.findAll();
		StreamSupport.stream(data.spliterator(), false)
				.forEach(entity -> viewsInMemory.put(entity.getReference(), new AtomicLong(entity.getViews())));

	}

	@Override
	public void view(String episodeReference) {
		Optional<EpisodeEntity> episodeOptional = episodeRepository.findByReference(episodeReference);
		if (!episodeOptional.isPresent())
			throw new ValidationException("Not found a resource for the episode" + episodeReference);
		EpisodeEntity episode = episodeOptional.get();
		studioRepository.updateView(viewsInMemory.get(episode.getRightsowner()).incrementAndGet(),
				episode.getRightsowner());
		logger.info("View incremented for owner " + episode.getRightsowner());

	}

	@Override
	public void reset() {
		viewsInMemory.entrySet().stream()
				.forEach(x -> studioRepository.updateView(x.getValue().updateAndGet(currentValue -> 0), x.getKey()));
		logger.info("Internal status has been reseted");

	}

	@Override
	public void validation(ViewRequest request) throws ValidationException {
		if (StringUtils.isEmpty(request.getCustomer()) || StringUtils.isEmpty(request.getEpisode()))
			throw new ValidationException("Not found a resource for the customer" + request.getCustomer()
					+ " and episode " + request.getEpisode());
	}

}
