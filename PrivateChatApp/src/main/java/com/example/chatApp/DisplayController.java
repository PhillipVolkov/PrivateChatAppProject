package com.example.chatApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpSession;

//controller for mapping all pages

@Controller
public class DisplayController {
    @Autowired
    private DatabaseRepo dataBaseRepo;
    
    private ExecutorService nonBlockingService = Executors
      .newCachedThreadPool();
    
    @GetMapping("/sse")
    public SseEmitter handleSse(HttpSession session) {
		SseEmitter emitter = new SseEmitter();
		
		nonBlockingService.execute(() -> {
			try {
				String send = session.getAttribute("username").toString();
				
				List<Message> messages = dataBaseRepo.getUnreadMessage(Long.parseLong(session.getAttribute("userId").toString()), Long.parseLong(session.getAttribute("selectedFriend").toString()));
				for (Message message : messages) {
					send += message.toString();
				}
				
				List<Friend> unreadFriends = dataBaseRepo.getUnread(Long.parseLong(session.getAttribute("userId").toString()));
				for (Friend friend : unreadFriends) {
					send += friend.toString();
				}
				
				emitter.send(send);
				emitter.complete();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				for (StackTraceElement s : e.getStackTrace()) {
					System.out.println(s.toString());
				}
				System.out.println();
			}
		});
		return emitter;
	}
    
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Typing send(Typing typing) throws Exception {
        return new Typing(typing.getUser(), typing.getRecipient(), typing.isTyping());
    }
    
    //DISPLAY WHEN ONLINE
    //IMPROVE REFRESH
    
    //get mapping for the overview page
	@GetMapping("/")
    public String mainPage(@RequestParam(name = "friendSelect", required = false) String friendSelect, HttpSession session, Model model) {
		User user = null;
		List<Message> messages = null;
		List<User> friends = null;
		List<Integer> unreadMessages = null;
		boolean followed = false;
		List<User> friendRequests = null;
		Long friendId = null;
		
		if (session.getAttribute("username") != null) {
			user = dataBaseRepo.getUser(session.getAttribute("username").toString());
			
			friends = new ArrayList<User>();
			unreadMessages = new ArrayList<Integer>();
			for (Friend friend : dataBaseRepo.getFriends(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId())) {
				friends.add(dataBaseRepo.getUserById(friend.getFriend()));
				unreadMessages.add(friend.getUnread());
			}
			
			if (friendSelect == null && friends.size() != 0) {
				friendSelect = friends.get(0).getUsername();
			}
			
			if (friendSelect != null) {
				boolean isFriend = false;
				for (User friend : friends) {
					if (friend.getUsername().equals(friendSelect)) {
						friendId = friend.getId();
						session.setAttribute("selectedFriend", friendId);
						isFriend = true;
						break;
					}
				}
				
				if (isFriend) {
					List<Message> tempMessages = dataBaseRepo.getMessages(user.getId(), dataBaseRepo.getUser(friendSelect).getId());
					
					messages = new ArrayList<Message>();
					for (int i = tempMessages.size()-1; i >= 0; i--) {
						if (tempMessages.get(i).getContents().contains("\n")) {
							Scanner scan = new Scanner(tempMessages.get(i).getContents()).useDelimiter("\n");
							String message = "";
							
							while (scan.hasNext()) {
								message += scan.next();
								
								if (scan.hasNext()) message += " <br> ";
							}
							
							tempMessages.get(i).setContents(message);
						}
						
						messages.add(tempMessages.get(i));
					}
					
					try {
						dataBaseRepo.getFriend(dataBaseRepo.getUser(friendSelect).getId(), user.getId());
						followed = true;
					}
					catch (Exception e) {}
					
					for (Message message : messages) {
						if (message.getRead() == null && message.getSender() != user.getId()) {
							dataBaseRepo.setRead(message.getId());
						}
					}
				}
			}

			friendRequests = new ArrayList<User>();
			for (Friend friend : dataBaseRepo.getFriendRequests(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId())) {
				try {
					friendRequests.add(dataBaseRepo.getUserById(friend.getId()));
				}
				catch (Exception e) {}
			}
			
			if (friendId != null) dataBaseRepo.removeUnread(Long.parseLong(session.getAttribute("userId").toString()), friendId);
		}
		
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("friendId", friendId);
        model.addAttribute("unreadMessages", unreadMessages);
        model.addAttribute("friendSelect", friendSelect);
        model.addAttribute("followed", followed);
        model.addAttribute("friendRequests", friendRequests);
        model.addAttribute("messages", messages);
        return "main";
    }
	
	@PostMapping("/")
    public RedirectView updateMain(@RequestParam(name = "friendName", required = false) String friendName, @RequestParam(name = "message", required = false) String message, 
    		@RequestParam(name = "friendSelectHidden", required = false) String friendSelect, @RequestParam(name = "remove", required = false) String removeFriend, HttpSession session, Model model) {
		String url = "";
		
		if (friendName != null) {
			try {
				dataBaseRepo.getFriend(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId(), dataBaseRepo.getUser(friendName).getId());
			}
			catch(Exception e) {
				try {
					dataBaseRepo.insertFriend(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId(), dataBaseRepo.getUser(friendName).getId());
					if (!friendSelect.equals("")) url += "?friendSelect="+friendSelect;
				}
				catch(Exception ee) {}
			}
		}
		else if (message != null) {
			if (!message.equals("")) {
				dataBaseRepo.insertMessage(dataBaseRepo.getUser(session.getAttribute("username").toString()).getId(), dataBaseRepo.getUser(friendSelect).getId(), message, new java.sql.Timestamp(System.currentTimeMillis()));
				dataBaseRepo.increaseUnread(1, dataBaseRepo.getUser(friendSelect).getId(), dataBaseRepo.getUser(session.getAttribute("username").toString()).getId());
			}
			
			url += "?friendSelect="+friendSelect;
		}
		else if (removeFriend != null) {
			Long id = Long.parseLong(removeFriend);
			
			dataBaseRepo.removeFriend(Long.parseLong(session.getAttribute("userId").toString()), id);
		}
		
        return new RedirectView("/" + url);
    }
	
	@GetMapping("/signup")
    public String signup(Model model) {
        return "signup";
        
    }
	
	@PostMapping("/signup")
    public String newUser(@RequestParam(name = "name", required = true) String name, @RequestParam(name = "email", required = true) String email, 
    		@RequestParam(name = "conpass", required = true) String conpass, @RequestParam(name = "username", required = true) String userName, 
    		@RequestParam(name = "password", required = true) String password, HttpSession session, Model model) {
		
        if (password.contentEquals(conpass)){
        	try {
        		dataBaseRepo.insertUser(userName, password, name, email);
        	}
        	catch (Exception e) {
        		model.addAttribute("message", e.getMessage());   
    	        return "signup";
        	}
        	
            return "main";
        }
        
        model.addAttribute("message", "Passwords do not match");        
        return "signup";

    }   
    
    	@GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        model.addAttribute("message", "Before Loading Profile"); 
        // Get Name & Email from database
        // Populate the text fields with the values from DB
        
        User user = null;
		
		try {
			if (session.getAttribute("username") != null) {
                            user = dataBaseRepo.getUser(session.getAttribute("username").toString());
                            model.addAttribute("user",user);
                        }
		}
		catch (Exception e) {}
		
		if (user != null) {
               
                    model.addAttribute("message","Current Username: " + user.getUsername()+ "--------" + "Old Name: " + user.getPname() + "--------" + "Old Email: " + user.getEmail());
                                            
                }
		
		
        
        return "profile";
        
    }
    
    @PostMapping("/profile")
    public String editProfile(@RequestParam(name = "username", required = true) String username, @RequestParam(name = "npname", required = true) String npname, @RequestParam(name = "nemail", required = true) String nemail, @RequestParam(name = "npassword", required = true) String npassword, HttpSession session, Model model) {
	// Take the new Name, email
        // Save to DB
        // GO back to main page?
        
        //db.updateUser -- new function
        
        dataBaseRepo.editUser(username, npname, nemail, npassword );
        //model.addAttribute("message", npname+" "+nemail+" "+ username); 
        return "login";
                
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
				session.setAttribute("userId", user.getId());
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