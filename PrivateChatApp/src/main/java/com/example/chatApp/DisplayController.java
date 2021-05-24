package com.example.chatApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpSession;

//controller for mapping all pages

@Controller
public class DisplayController {
    @Autowired
    private DatabaseRepo dataBaseRepo;
    
    //get mapping for the overview page
	@GetMapping("/")
    public String mainPage(@RequestParam(name = "friendSelect", required = false) String friendSelect, HttpSession session, Model model) {
		User user = null;
		List<Message> messages = null;
		List<User> friends = null;
		
		if (session.getAttribute("username") != null) {
			try {
				user = dataBaseRepo.getUser(session.getAttribute("username").toString());
				
				friends = new ArrayList<User>();
				for (Friend friend : dataBaseRepo.getFriends(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId())) {
					friends.add(dataBaseRepo.getUserById(friend.getFriend()));
					System.out.println(friend.getFriend());
				}
				
				if (friendSelect == null && friends.size() != 0) {
					friendSelect = friends.get(0).getUsername();
				}
				
				if (friendSelect != null) {
					messages = dataBaseRepo.getMessages(dataBaseRepo.getUser(friendSelect).getId());
				}
			}
			catch(Exception e) {
				messages = null;
			}
		}
		
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("friendSelect", friendSelect);
        model.addAttribute("messages", messages);
        return "main";
    }
	
	@PostMapping("/")
    public RedirectView updateMain(@RequestParam(name = "friendName", required = false) String friendName, @RequestParam(name = "message", required = false) String message, 
    		@RequestParam(name = "friendSelectHidden", required = false) String friendSelect, HttpSession session, Model model) {
		String url = "";
		
		if (friendName != null) {
			dataBaseRepo.insertFriend(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId(), dataBaseRepo.getUser(friendName).getId());
		}
		else if (message != null) {
			dataBaseRepo.insertMessage(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId(), dataBaseRepo.getUser(friendSelect).getId(), message);
			url += "?friendSelect="+friendSelect;
		}
		
        return new RedirectView("/" + url);
    }
	
	@GetMapping("/signup")
    public String signup(Model model) {
        return "signup";
    }
	
	@PostMapping("/signup")
    public RedirectView newUser(@RequestParam(name = "username", required = true) String userName, @RequestParam(name = "password", required = true) String password, HttpSession session, Model model) {
		dataBaseRepo.insertUser(userName, password);
		
        return new RedirectView("/");
    }
	
	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
	
	@PostMapping("/login")
    public String verifyLogin(@RequestParam(name = "username", required = true) String userName, @RequestParam(name = "password", required = true) String password, HttpSession session, Model model) {
		User user = null;
		
		try {
			user = dataBaseRepo.getUser(userName);
		}
		catch (Exception e) {}
		
		if (user != null) {
			if (user.getPassword().equals(password)) {
				model.addAttribute("message", "success");
				session.setAttribute("username", userName);
		        return "login";
			}
		}
		
		model.addAttribute("message", "Username or password incorrect");
		
        return "login";
    }
	
	@GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
		session.removeAttribute("username");
        return "main";
    }
}