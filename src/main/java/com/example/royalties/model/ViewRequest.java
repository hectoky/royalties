package com.example.royalties.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewRequest {
	private String episode;
	private String customer;

	@Override
	public String toString() {
		return " {episode:" + episode + ", customer:" + customer + "}";
	}

}
