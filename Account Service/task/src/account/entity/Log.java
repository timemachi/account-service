package account.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@JsonPropertyOrder({"id", "date", "action", "subject", "object", "path"})
@Getter
@Setter
@NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
    @Column
    private LocalDateTime date;
    @Column
    private String action;
    @Column
    private String subject;
    @Column
    private String object;
    @Column
    private String path;

    public Log(logEvent action, String subject, String object, String path) {
        this.date = LocalDateTime.now();
        this.action = action.name();
        this.subject = subject;
        this.object = object;
        this.path = path;
    }
}
