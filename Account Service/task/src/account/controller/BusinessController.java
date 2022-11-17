package account.controller;

import account.dto.SalaryRequest;
import account.dto.StatusResponse;
import account.entity.User;
import account.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class BusinessController {


    @Autowired
    private SalaryService salaryService;

    @PostMapping("/api/acct/payments")
    public StatusResponse getPayments(@RequestBody @Size(min = 1) List<@Valid SalaryRequest> body) {
        salaryService.addSalary(body);
        return new StatusResponse("Added successfully!");
    }

    @PutMapping("api/acct/payments")
    public StatusResponse updatePayment(@Valid @RequestBody SalaryRequest body) {
        salaryService.updateSalary(body);
        return new StatusResponse("Updated successfully!");
    }

    @GetMapping("api/empl/payment")
    public ResponseEntity<Object> getPayment(@RequestParam @Nullable String period, @AuthenticationPrincipal User user) {
        String email = user.getUsername();
        Object result = period == null
                ? salaryService.getSalary(email)
                : salaryService.getSalary(email, period);
        return new ResponseEntity<>(result == null ? Map.of() : result, HttpStatus.OK);
    }
}
