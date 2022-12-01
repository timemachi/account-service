package account.service;

import account.entity.Log;
import account.entity.logEvent;
import account.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Service
public class LogService {
    @Autowired
    LogRepository logRepository;

    @Autowired
    HttpServletRequest request;

    public void addEvent(logEvent action, String subject, String object, String path) {
        Log log = new Log(action, subject, object, path);
        logRepository.save(log);
    }

    public void addEvent(logEvent action, String subject, String object) {
        String path = request.getRequestURI();
        addEvent(action, subject, object, path);
    }

    public void addEvent(logEvent action, String object) {
        Principal principal = request.getUserPrincipal();
        String subject = principal == null? "Anonymous": principal.getName();
        addEvent(action, subject, object);
    }

    @Async
    public List<Log> getAllEvents() {
        return logRepository.findAll();
    }
}
