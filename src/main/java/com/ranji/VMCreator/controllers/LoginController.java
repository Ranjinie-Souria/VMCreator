package com.ranji.VMCreator.controllers;

import com.ranji.VMCreator.configuration.SpringSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.Collection;


@Controller
public class LoginController {
	String AZURE_TENANT_ID;
	String AZURE_CLIENT_ID;
	String AZURE_CLIENT_SECRET;
	String AZURE_SUBSCRIPTION_ID;

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@GetMapping("/")
	public String getResource(Model model, Principal principal) {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
		Collection<? extends GrantedAuthority> authorities = token.getAuthorities();
		String role = "";
		if (!authorities.isEmpty()) {
			GrantedAuthority firstAuthority = authorities.iterator().next();
			role = firstAuthority.getAuthority();
		}
		log.debug(AZURE_TENANT_ID);
		model.addAttribute("username", token.getName());
		model.addAttribute("role", role);
		return "home";
	}


}