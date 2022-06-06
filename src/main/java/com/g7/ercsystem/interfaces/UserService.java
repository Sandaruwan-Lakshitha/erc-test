package com.g7.ercsystem.interfaces;

import com.g7.ercsystem.rest.auth.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    void deleteUserById(String id);
    User getUserById(String id, String token_id);
    User getUser(String id);
    User addUser(User user);
    void updatePassword(String newPassword, String oldPassword, String id) throws Exception;
    void updateEmail(String email,String id);
    void updateRoles(Set<String> roles,String id);

    List<User> getAllUsers();
    List<User> getAllReviewers();
    List<User> getAllApplicants();

    User updateUser(User user,String id);
}
