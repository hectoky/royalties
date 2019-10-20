package com.example.royalties.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.royalties.entity.EpisodeEntity;

public interface EpisodeRepository extends CrudRepository<EpisodeEntity, Long> {

	Optional<EpisodeEntity> findByReference(String reference);

}