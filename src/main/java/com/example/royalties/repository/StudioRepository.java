package com.example.royalties.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.royalties.entity.StudioEntity;

public interface StudioRepository extends CrudRepository<StudioEntity, Long> {

	Optional<StudioEntity> findByReference(String reference);

	@Query("UPDATE StudioEntity s SET s.views =:number where s.reference=:reference")
	@Modifying
	@Transactional
	void updateView(long number, String reference);

}