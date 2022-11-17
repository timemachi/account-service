package account.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SetRoleRequest {
    @NotBlank
    private String user;

    @NotBlank
    private String role;

    @NotBlank
    @Pattern(regexp = "GRANT|REMOVE")
    private String operation;
}
