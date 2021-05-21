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

//controller for mapping all pages

@Controller
public class DisplayController {
    @Autowired
    private DatabaseRepo dataBaseRepo;
    
    //get mapping for the overview page
	@GetMapping("/")
    public String mainPage(@RequestParam(name = "test", required = false) String test, Model model) {
		List<User> users = dataBaseRepo.getUsers();
		
		model.addAttribute("test", test);
        model.addAttribute("users", users);
        return "main";
    }
}