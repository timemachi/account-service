package account.controller;

import account.entity.Log;
import account.service.LogService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    LogService logService;

    @GetMapping("/events")
    public ResponseEntity<List<Log>> getAllEvents() {
        return new ResponseEntity<>(logService.getAllEvents(), HttpStatus.OK);
    }

}
