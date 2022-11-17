package account.component;

import account.entity.Log;
import account.entity.logEvent;
import account.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    LogService logService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logService.addEvent(logEvent.ACCESS_DENIED, auth.getName(), request.getRequestURI(), request.getRequestURI());
        }

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getOutputStream().println(new ObjectMapper().writeValueAsString(Map.of(
                "timestamp", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()),
                "status", 403,
                "error", "Forbidden",
                "message", "Access Denied!",
                "path", request.getRequestURI()
        )));
    }
}