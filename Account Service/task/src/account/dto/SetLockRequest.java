package account.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SetLockRequest {
    @NotBlank
    private String user;

    @NotBlank
    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;
}
