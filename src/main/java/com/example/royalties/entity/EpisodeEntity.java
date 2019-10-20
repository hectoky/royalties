package com.example.royalties.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "episode")
public class EpisodeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String reference;
	private String name;
	private String rightsowner;

	// TODO: not really necessary this relationship in the code
	// @ManyToOne
	// @JoinColumn(name = "rightsowner", referencedColumnName = "reference",
	// nullable = false)
	// private StudioEntity rightsowner;

}
