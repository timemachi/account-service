package account.service;

import account.dto.SalaryRequest;
import account.dto.SalaryResponse;
import account.entity.Salary;
import account.entity.User;
import account.mapper.Mappers;
import account.repository.UserRepository;
import account.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepo;

    @Transactional
    public void addSalary(List<SalaryRequest> list) {
        if (!salaryListIsUnique(list)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee-period pairs must be unique!");
        }

        Map<String, List<SalaryRequest>> salaryByEmails = list.stream()
                .collect(Collectors.groupingBy(SalaryRequest::getEmployee, Collectors.toList()));
        for (var email : salaryByEmails.keySet()) {
            User user = userService.getUserByName(email.toLowerCase());
            for (var salaryDto : salaryByEmails.get(email)) {
                Salary salary = new Salary();
                salary.setPeriod(DateUtils.parseDateFromMonthYear(salaryDto.getPeriod()));
                salary.setSalary(salaryDto.getSalary());
                if (user.getSalary().contains(salary)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't add same salary!");
                }
                user.getSalary().add(salary);
            }
            userRepo.save(user);
        }
    }

    @Transactional
    public void updateSalary(SalaryRequest salaryRequest) {
        User user = userService.getUserByName(salaryRequest.getEmployee());
        LocalDate period = DateUtils.parseDateFromMonthYear(salaryRequest.getPeriod());

        Optional<Salary> existing = user.getSalary().stream()
                .filter(i -> i.getPeriod().equals(period))
                .findAny();
        Salary salary;
        if (existing.isPresent()) {
            salary = existing.get();
            salary.setSalary(salaryRequest.getSalary());
        } else {
            salary = new Salary();
            salary.setPeriod(period);
            salary.setSalary(salaryRequest.getSalary());
            user.getSalary().add(salary);
        }
        userRepo.save(user);
    }

    @Transactional
    public List<SalaryResponse> getSalary(String email) {
        User user = userService.getUserByName(email);
        return user.getSalary().stream()
                .sorted(Comparator.reverseOrder())
                .map(i -> Mappers.fromSalary(i, user))
                .toList();
    }

    @Transactional
    public SalaryResponse getSalary(String email, String rawDate) {
        User user = userService.getUserByName(email);
        LocalDate period = DateUtils.parseDateFromMonthYear(rawDate);

        Optional<Salary> salary = user.getSalary().stream()
                .filter(i -> i.getPeriod().equals(period))
                .findAny();
        if (salary.isEmpty()) {
            return null;
        }
        return Mappers.fromSalary(salary.get(), user);
    }

    private boolean salaryListIsUnique(List<SalaryRequest> list) {
        return list.stream().allMatch(new HashSet<>()::add);
    }
}
