package account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryResponse {
    private String name;
    private String lastname;
    private String period;
    private String salary;
}
