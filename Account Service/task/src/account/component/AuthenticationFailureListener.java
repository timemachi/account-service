package account.component;

import account.entity.logEvent;
import account.service.LogService;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    @Autowired
    HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        String path = request.getRequestURI();

        if (userService.ifUserExist(username)) {
            logService.addEvent(logEvent.LOGIN_FAILED, username, path);
            userService.failedLogin(username);
            throw new BadCredentialsException("wrong password");
        }
        logService.addEvent(logEvent.LOGIN_FAILED, username, path);
    }
}
