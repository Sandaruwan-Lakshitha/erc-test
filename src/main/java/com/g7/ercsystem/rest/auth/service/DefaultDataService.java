package com.g7.ercsystem.rest.auth.service;

import com.g7.ercsystem.assembler.AuthAssembler;
import com.g7.ercsystem.payload.requests.SignUpRequest;
import com.g7.ercsystem.repository.RoleRepository;
import com.g7.ercsystem.repository.UserRepository;
import com.g7.ercsystem.rest.auth.model.EnumRole;
import com.g7.ercsystem.rest.auth.model.MemberDetails;
import com.g7.ercsystem.rest.auth.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class DefaultDataService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthAssembler authAssembler;

    @Autowired
    private UserRepository userRepository;

    public void addRoles(){
        if(roleRepository.findAll().isEmpty()){
			Role role1 = new Role(EnumRole.ROLE_APPLICANT);
			Role role2 = new Role(EnumRole.ROLE_REVIEWER);
			Role role3 = new Role(EnumRole.ROLE_CLERK);
			Role role4 = new Role(EnumRole.ROLE_SECRETARY);
			Role role5 = new Role(EnumRole.ROLE_ADMIN);

			roleRepository.save(role1);
			roleRepository.save(role2);
			roleRepository.save(role3);
			roleRepository.save(role4);
			roleRepository.save(role5);
		}

		if(userRepository.findAll().isEmpty()){
			SignUpRequest signUpRequest = new SignUpRequest();
			Set<String> roles = new HashSet<>();
			roles.add("admin");
			roles.add("secretary");
			roles.add("applicant");
			roles.add("clerk");
			roles.add("reviewer");
			signUpRequest.setEmail("admin@gmail.com");
			signUpRequest.setPassword("12345678");
			signUpRequest.setRoles(roles);
			signUpRequest.setMemberDetails(new MemberDetails("Sandaruwan lakshitha","Matara","987654321V"));
			userRepository.save(authAssembler.SignUpRequestToUserEntity(signUpRequest));
		}

    }

}
