package com.sentiment.anlaysis.demo.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sentiment.anlaysis.demo.Module.Users;
import com.sentiment.anlaysis.demo.Service.FileService;
import com.sentiment.anlaysis.demo.Service.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersControl {

    @Autowired
	UsersService userService;

    @Autowired
    FileService fileService;

    @PostMapping("/register")
	public String regsiterString(@RequestParam String username,@RequestParam String useremail, @RequestParam String password, HttpSession session) {
		Users users = userService.Regis(username, useremail, password);
		if(users != null) {
			session.setAttribute("users", users.getId());
			return "redirect:/dashboard";
		}
		return "redirect:/dashboard";
	}
	
	@PostMapping("/login")
	public String loginString(@RequestParam String username, @RequestParam String password, HttpSession session) {
		Users us = userService.Logi(username, password);
		if(us != null) {
			session.setAttribute("users", us.getId());
			return "redirect:/dashboard";
		}
		return "login";
	}
    @PostMapping("/dashboard")
	public String upload(@RequestParam MultipartFile csvFile, Long user_id, HttpSession session) throws IOException {
	    if (csvFile.isEmpty()) {
	        return "redirect:/dashboard?error=No file uploaded";
	    }
	    if (!csvFile.getOriginalFilename().endsWith(".csv")) {
	        return "redirect:/dashboard?error=Invalid file type";
	    }
	    user_id = (Long) session.getAttribute("users");
	    fileService.UploadFile(csvFile, user_id);
	    return "redirect:/read";
	}
}
