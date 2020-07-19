package info.ivgivanov.conference.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import info.ivgivanov.conference.model.Registration;
import info.ivgivanov.conference.model.User;

@RestController
public class UserController {

	@GetMapping("/user")
	public User getUser(@RequestParam(value = "firstname", defaultValue="Ivaylo") String firstname,
			@RequestParam(value = "lastname", defaultValue="Ivanov") String lastname,
			@RequestParam(value = "age", defaultValue="30") int age) {
		
		User user = new User();
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setAge(age);
		
		return user;
		
	}
	
	@PostMapping("/user")
	public User postUser(User user) {
		
		System.out.println("User first name: " + user.getFirstname());
		
		return user;
	}
	
}
