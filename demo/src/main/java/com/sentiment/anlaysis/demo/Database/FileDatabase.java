package com.sentiment.anlaysis.demo.Database;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sentiment.anlaysis.demo.Module.FileModule;

public interface FileDatabase extends JpaRepository<FileModule, Long>{

}
