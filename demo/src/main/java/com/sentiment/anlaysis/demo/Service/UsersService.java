package com.sentiment.anlaysis.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sentiment.anlaysis.demo.Database.UserDatabase;
import com.sentiment.anlaysis.demo.Module.Users;

@Service
public class UsersService {

    @Autowired
	UserDatabase userDatabase;
	
	public Users Regis(String name, String email, String pass) {
		Users users = new Users(null, name, email, pass);
		if(pass.length() < 7) {
			return null;
		}
		return userDatabase.save(users);
	}
	
	public Users Logi(String email, String pass) {
		Users users = userDatabase.findByEmail(email);
		if(users != null && users.getPass().equals(pass)) {
			return users;
		}
		return null;
	}
}
