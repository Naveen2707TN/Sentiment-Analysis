package com.sentiment.anlaysis.demo.Module;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "User_file_store")
public class FileModule {

@Id
	private Long id;
	@Column
	private String path;
	@Column
	private String name;
	@Column
	private String type;
	
	public FileModule() {
	
	}
	public FileModule(Long id, String path, String name, String type) {
		this.id = id;
		this.path = path;
		this.name = name;
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}

