package com.ranji.VMCreator.controllers;

import com.ranji.VMCreator.model.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collection;


@Controller
public class LoginController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@GetMapping("/")
	public String home(Model model, Principal principal) {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
		Collection<? extends GrantedAuthority> authorities = token.getAuthorities();
		String role = "";
		if (!authorities.isEmpty()) {
			GrantedAuthority firstAuthority = authorities.iterator().next();
			role = firstAuthority.getAuthority();
		}
		model.addAttribute("username", token.getName());
		model.addAttribute("role", role);
		model.addAttribute("vm", new Vm());
		return "home";
	}


}