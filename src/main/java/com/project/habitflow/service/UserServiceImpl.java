package com.project.habitflow.service;

import com.project.habitflow.entity.User;
import com.project.habitflow.exception.UserNotFoundException;
import com.project.habitflow.repository.UserRepository;
import com.project.habitflow.request.PasswordUpdateRequest;
import com.project.habitflow.request.UserUpdateRequest;
import com.project.habitflow.response.UserResponse;
import com.project.habitflow.response.UserStatsResponse;
import com.project.habitflow.util.FindAuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, FindAuthenticatedUser findAuthenticatedUser, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserInfo() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        return UserMapper.toResponse(user);
    }

    @Override
    public void deleteUser() {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (isLastAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot delete itself");
        }

        userRepository.delete(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }


    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        updateFields(user, request);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUserById(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        updateFields(user, request);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse changeUserRole(Long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(role);
        return toResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> searchUsersByEmailOrName(String query) {
        return userRepository.findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatsResponse getUserStatistics() {
        long totalUsers = userRepository.count();
        LocalDate latestUser = userRepository.findTopByOrderByCreatedAtDesc()
                .map(User::getCreatedAt)
                .orElse(null);
        return new UserStatsResponse(totalUsers, latestUser);
    }

    // --- Utility methods ---
    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole());
    }

    private void updateFields(User user, UserUpdateRequest request) {
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
    }

    @Override
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (!isOldPasswordCorrect(user.getPassword(), passwordUpdateRequest.getOldPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (!isNewPasswordConfirmed(passwordUpdateRequest.getNewPassword(),
                passwordUpdateRequest.getNewPassword2())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        if (!isNewPasswordDifferent(passwordUpdateRequest.getOldPassword(),
                passwordUpdateRequest.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old and new passwords must be different");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

    private boolean isOldPasswordCorrect(String currentPassword, String oldPassword) {
        return passwordEncoder.matches(oldPassword, currentPassword);
    }

    private boolean isNewPasswordConfirmed(String newPassword, String newPasswordConfirmation) {
        return newPassword.equals(newPasswordConfirmation);
    }

    private boolean isNewPasswordDifferent(String oldPassword, String newPassword) {
        return !oldPassword.equals(newPassword);
    }

    private boolean isLastAdmin(User user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (isAdmin) {
            long adminCount = userRepository.countAdminUsers();
            return adminCount <= 1;
        }

        return false;
    }

    public class UserMapper {
        public static UserResponse toResponse(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole()
            );
        }
    }

}
