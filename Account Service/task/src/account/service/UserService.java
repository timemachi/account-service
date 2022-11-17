package account.service;

import account.dto.UserDto;
import account.entity.Group;
import account.entity.User;
import account.entity.logEvent;
import account.mapper.Mappers;
import account.repository.GroupRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    AuthService authService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    LogService logService;

    @Autowired
    HttpServletRequest request;

    private final List<String> allRoles = List.of("ROLE_USER","ROLE_ADMINISTRATOR","ROLE_ACCOUNTANT","ROLE_AUDITOR");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    synchronized public UserDto addUser(UserDto userDto) {
        if (userRepo.existsByUsername(userDto.getEmail().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        authService.ensurePasswordIsOK(userDto.getPassword());

        userDto.setPassword(authService.encodePassword(userDto.getPassword()));
        User user = Mappers.fromUserDto(userDto);
        String role;
        if (userRepo.count() == 0) {
            role = "ROLE_ADMINISTRATOR";
        } else {
            role = "ROLE_USER";
        }
        addUserGroup(user, role);
        user.setLoginAttempt(0);
        user.setNotLocked(true);
        userRepo.save(user);
        logService.addEvent(logEvent.CREATE_USER, user.getUsername());
        return Mappers.fromUser(user);
    }

    synchronized public UserDto setRole(String username, String role, String operation) {
        User user = getUserByName(username.toLowerCase());
        String newRole = "ROLE_" + role;
        if (!allRoles.contains(newRole)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        switch (operation) {
            case "GRANT" -> addUserGroup(user, newRole);
            case "REMOVE" -> removeUserGroup(user, newRole);
        }
        userRepo.save(user);
        return Mappers.fromUser(user);
    }
     private void addUserGroup(User user, String role) {
        Group group = groupRepository.findByName(role).get();
        Set<Group> groups = user.getUserGroups();
        if (userRepo.isAdministrator(user) && (role.equals("ROLE_ACCOUNTANT") || role.equals("ROLE_USER") || role.equals("ROLE_AUDITOR"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }
        if (groups.stream().anyMatch(anyGroup ->(anyGroup.getName().equals("ROLE_USER")
                                               ||anyGroup.getName().equals("ROLE_ACCOUNTANT")
                                               || anyGroup.getName().equals("ROLE_AUDITOR")) && role.equals("ROLE_ADMINISTRATOR"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }
        groups.add(group);
        String roleName = role.substring(5);
        if (!roleName.equals("USER" ) && !roleName.equals("ADMINISTRATOR")) {
            logService.addEvent(logEvent.GRANT_ROLE,
                    String.format("Grant role %s to %s", roleName, user.getUsername()));
        }
    }
    private void removeUserGroup(User user, String role) {
        Group group = groupRepository.findByName(role).get();
        Set<Group> groups = user.getUserGroups();
        if (groups.stream().noneMatch(anyGroup -> anyGroup.getName().equals(role))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }
        if (role.equals("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        if (groups.size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }
        groups.remove(group);
        String roleName = role.substring(5);
        logService.addEvent(logEvent.REMOVE_ROLE,
                String.format("Remove role %s from %s", roleName, user.getUsername()));
    }

    synchronized public void setLockUser (User user, String operation) {
        if (userRepo.isAdministrator(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }
        switch (operation) {
            case "LOCK" -> {
                if (!user.isNotLocked()) {
                    return;
                }
                user.setNotLocked(false);
                if (request.getUserPrincipal() != null) {
                    logService.addEvent(logEvent.LOCK_USER,
                            String.format("Lock user %s", user.getUsername()));
                } else {
                    logService.addEvent(logEvent.LOCK_USER, user.getUsername(), String.format("Lock user %s", user.getUsername()));
                }
            }
            case "UNLOCK" -> {
                if (user.isNotLocked()) {
                    return;
                }
                user.setNotLocked(true);
                user.setLoginAttempt(0);
                logService.addEvent(logEvent.UNLOCK_USER,
                        String.format("Unlock user %s", user.getUsername()));
            }
        }
        userRepo.save(user);
    }


    public User getUserByName(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
    }

    public boolean ifUserExist(String username) {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return false;
        }
        return true;
    }

    public List<UserDto> getAllUser() {
        return userRepo.findAll().stream().map(Mappers::fromUser).sorted((a, b) -> (int) (a.getId() - b.getId())).collect(Collectors.toList());
    }

    synchronized public void changePassword(User user, String newPassword) {
        if (authService.passwordIsTheSame(user.getPassword(), newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
        authService.ensurePasswordIsOK(newPassword);

        user.setPassword(authService.encodePassword(newPassword));
        userRepo.save(user);
        logService.addEvent(logEvent.CHANGE_PASSWORD, user.getUsername());
    }

    synchronized public void deleteUser(User user) {
        if (userRepo.isAdministrator(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userRepo.delete(user);
        logService.addEvent(logEvent.DELETE_USER, user.getUsername());
    }

    synchronized public void failedLogin(String username) {
        User user = getUserByName(username);
        if (user.getLoginAttempt() < 4) {
            user.setLoginAttempt(user.getLoginAttempt() + 1);
            userRepo.save(user);
        }
        else {
            logService.addEvent(logEvent.BRUTE_FORCE,username, request.getRequestURI());
            if (!userRepo.isAdministrator(user)) {
                setLockUser(user, "LOCK");
                user.setLoginAttempt(0);
            } else {

                user.setLoginAttempt(0);
                logService.addEvent(logEvent.LOCK_USER, user.getUsername(), String.format("Lock user %s", user.getUsername()));
            }
            userRepo.save(user);
        }
    }

    synchronized public void resetLoginAttempt(String user) {
        User thisUser = getUserByName(user);
        if (thisUser.getLoginAttempt() != 0) {
            thisUser.setLoginAttempt(0);
            userRepo.save(thisUser);
        }
    }
}
