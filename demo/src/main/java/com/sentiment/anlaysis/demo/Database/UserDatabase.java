package com.sentiment.anlaysis.demo.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sentiment.anlaysis.demo.Module.Users;

@Repository
public interface UserDatabase extends JpaRepository<Users, Long>{

    public Users findByEmail(String email);
}
