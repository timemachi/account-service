package account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PasswordChangeRequest {
    @JsonProperty("new_password")
    @NotBlank
    private String newPassword;
}
