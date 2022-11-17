package account.controller;

import account.dto.*;
import account.entity.User;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK) ;
    }

    @PutMapping("/user/role")
    public ResponseEntity<UserDto> setRole(@Valid @RequestBody(required = false) SetRoleRequest setRoleRequest) {
        return new ResponseEntity<>(userService.setRole(setRoleRequest.getUser(),
                setRoleRequest.getRole(), setRoleRequest.getOperation()), HttpStatus.OK);
    }

    @PutMapping("/user/access")
    public ResponseEntity<StatusResponse> setLockUser(@Valid @RequestBody SetLockRequest setLockRequest) {
        User user = userService.getUserByName(setLockRequest.getUser().toLowerCase());
        userService.setLockUser(user, setLockRequest.getOperation());
        return new ResponseEntity<>(new StatusResponse("User " + user.getUsername() + " " + setLockRequest.getOperation().toLowerCase() + "ed!"), HttpStatus.OK);
    }

    @DeleteMapping(value = {"/user/{email}", "/user/"})
    public ResponseEntity<UserStatusResponse> deleteUser(@PathVariable (required = false) String email) {
        User user = userService.getUserByName(email.toLowerCase());
        userService.deleteUser(user);
        return new ResponseEntity<>(new UserStatusResponse(user.getUsername(),"Deleted successfully!"), HttpStatus.OK);
    }
}
