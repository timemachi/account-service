package account.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SortNatural;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonPropertyOrder({"id", "name", "lastname", "email", "roles"})
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    @NotBlank @JsonProperty("name")
    private String name;
    @NotBlank @JsonProperty("lastname")
    private String lastName;
    @NotBlank @Pattern(regexp = ".+@acme\\.com$")
    private String email;
    @NotBlank @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @SortNatural
    private Set<String> roles;
}
