package account.mapper;

import account.dto.SalaryResponse;
import account.dto.UserDto;
import account.entity.Salary;
import account.entity.User;
import account.util.DateUtils;
import account.util.NumberUtils;

import java.util.TreeSet;
import java.util.stream.Collectors;

public class Mappers {

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getUsername());
        userDto.setRoles(user.getUserGroups().stream()
                .map(group -> group.getName()).collect(Collectors.toCollection(TreeSet::new)));
        return userDto;
    }

    public static User fromUserDto(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getEmail().toLowerCase());
        user.setPassword(userDto.getPassword());
        user.setName(userDto.getName());
        user.setLastName(userDto.getLastName());
        return user;
    }

    public static SalaryResponse fromSalary(Salary salary, User user) {
        SalaryResponse response = new SalaryResponse();
        response.setName(user.getName());
        response.setLastname(user.getLastName());
        response.setPeriod(DateUtils.formatDateAsMonthYear(salary.getPeriod()));
        response.setSalary(NumberUtils.formatAsMoney(salary.getSalary()));
        return response;
    }
}
