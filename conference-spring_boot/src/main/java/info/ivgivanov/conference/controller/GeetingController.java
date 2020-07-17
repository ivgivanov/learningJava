package info.ivgivanov.conference.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeetingController {
	
	@GetMapping("greeting")
	public String greetingMethod(Map<String, Object> model) {
		
		model.put("message", "Hello there!");
		return "greeting";
		
	}

}
