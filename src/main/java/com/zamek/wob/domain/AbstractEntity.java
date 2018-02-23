package com.zamek.wob.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.zamek.wob.util.HasLogger;

@MappedSuperclass
public abstract class AbstractEntity implements HasLogger {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="generator")
	
	private Long id; 
}
