package com.ranji.VMCreator.controllers;

import com.ranji.VMCreator.model.Vm;
import com.ranji.VMCreator.service.AzureVMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.Collection;

@Controller
public class VMController {

    @Autowired
    AzureVMService vmService;

    private static final Logger log = LoggerFactory.getLogger(VMController.class);

    @RequestMapping(value = "/VM", method = { RequestMethod.POST })
    public String createVM(Model model, Principal principal, @ModelAttribute Vm vm) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Collection<? extends GrantedAuthority> authorities = token.getAuthorities();
        String role = "";
        if (!authorities.isEmpty()) {
            GrantedAuthority firstAuthority = authorities.iterator().next();
            role = firstAuthority.getAuthority();
        }
        String ipAddress = vmService.createAndDestroyVM(vm);
        return "vm";
    }


}
