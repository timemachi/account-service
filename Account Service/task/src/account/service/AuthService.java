package account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class AuthService {
    @Autowired
    PasswordEncoder passwordEncoder;

    private final Set<String> leakedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void ensurePasswordIsOK(String password) {
        if (password.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password length must be 12 chars minimum!");
        }
        if (leakedPasswords.contains(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The password is in the hacker's database!");
        }
    }

    public boolean passwordIsTheSame(String oldPassword, String newPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }
}
