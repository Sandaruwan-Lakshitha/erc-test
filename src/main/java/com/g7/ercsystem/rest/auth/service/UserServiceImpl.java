package com.g7.ercsystem.rest.auth.service;

import com.g7.ercsystem.interfaces.UserService;
import com.g7.ercsystem.repository.RoleRepository;
import com.g7.ercsystem.repository.UserRepository;
import com.g7.ercsystem.rest.auth.model.EnumRole;
import com.g7.ercsystem.rest.auth.model.Role;
import com.g7.ercsystem.rest.auth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void deleteUserById(String id) {
        refreshTokenService.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(String id, String token_id) {

        if(Objects.equals(id,token_id)){
            return userRepository.findById(token_id).get();
        }else{
            User user = userRepository.findById(id).get();
            User user_req = userRepository.findById(id).get();
            if(user_req.getRoles().stream().anyMatch(role -> role.getName() == EnumRole.ROLE_ADMIN)
                   || user_req.getRoles().stream().anyMatch(role -> role.getName() == EnumRole.ROLE_CLERK)
                    || user_req.getRoles().stream().anyMatch(role -> role.getName() == EnumRole.ROLE_SECRETARY)
            ){
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUser(String id) {
        return userRepository.findById(id).get();
    }

    @Override
    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(String newPassword, String oldPassword, String id) throws Exception {
        User user = userRepository.findById(id).get();
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new Exception("Password not match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void updateEmail(String email, String id) {
        User user = userRepository.findById(id).get();
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void updateRoles(Set<String> roles,String id) {
        User user = userRepository.findById(id).get();
        user.setRoles(getRoles(roles,"abc"));
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public List<User> getAllReviewers() {
        return null;
    }

    @Override
    public List<User> getAllApplicants() {
        return null;
    }

    @Override
    @Transactional
    public User updateUser(User user,String id) {
        User user_db = userRepository.findById(id).get();
        user_db.setMemberDetails(user.getMemberDetails());
        return userRepository.save(user_db);
    }

    public Boolean existById(String id){
        return userRepository.existsById(id);
    }

    public Set<Role> getRoles(Set<String> strRoles, String email){
        Set<Role> roles = new HashSet<>();
        strRoles.forEach(role ->{
            switch (role){
                case "admin":
                    Role superAdminRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
                            .orElseThrow(()->{
                                log.error("Error: ROLE_ADMIN is not found from DB and requested by username {}",email);
                                return new RuntimeException("Error: Role is not found.");
                            });
                    roles.add(superAdminRole);
                    break;
                case "secretary":
                    Role secretaryRole = roleRepository.findByName(EnumRole.ROLE_SECRETARY)
                            .orElseThrow(()->{
                                log.error("Error: ROLE_ADMIN is not found from DB and requested by username {}",email);
                                return new RuntimeException("Error: Role is not found.");
                            });
                    roles.add(secretaryRole);
                    break;

                case "clerk":
                    Role clerkRole = roleRepository.findByName(EnumRole.ROLE_CLERK)
                            .orElseThrow(()->{
                                log.error("Error: ROLE_STUDENT is not found from DB and requested by username {}",email);
                                return new RuntimeException("Error: Role is not found.");
                            });
                    roles.add(clerkRole);
                    break;
                case "reviewer":
                    Role reviewerRole = roleRepository.findByName(EnumRole.ROLE_REVIEWER)
                            .orElseThrow(()-> {
                                log.error("Error: ROLE_COMPANY is not found from DB and requested by username {}",email);
                                return new RuntimeException("Error: Role is not found.");
                            });
                    roles.add(reviewerRole);
                    break;

                case "applicant":
                    Role applicantRole = roleRepository.findByName(EnumRole.ROLE_APPLICANT)
                            .orElseThrow(()-> {
                                log.error("Error: ROLE_COMPANY is not found from DB and requested by username {}",email);
                                return new RuntimeException("Error: Role is not found.");
                            });
                    roles.add(applicantRole);
                    break;
            }
        });
        return roles;
    }

    public Set<String> setRoles(Set<Role> strRoles){
        Set<String> roles = new HashSet<>();
        strRoles.forEach(role ->{
            switch (role.getName()){
                case ROLE_ADMIN:
                    roles.add("admin");
                    break;
                case ROLE_SECRETARY:
                    roles.add("secretary");
                    break;
                case ROLE_CLERK:
                    roles.add("clerk");
                    break;
                case ROLE_REVIEWER:
                    roles.add("reviewer");
                    break;
                case ROLE_APPLICANT:
                    roles.add("applicant");
            }
        });
        return roles;
    }
}
