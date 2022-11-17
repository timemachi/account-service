package account.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Salary implements Comparable<Salary> {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate period;
    private long salary;

    @Override
    public int compareTo(Salary o) {
        return period.compareTo(o.getPeriod());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Salary salary1 = (Salary) o;

        if (salary != salary1.salary) return false;
        return period.equals(salary1.period);
    }

    @Override
    public int hashCode() {
        int result = period.hashCode();
        result = 31 * result + (int) (salary ^ (salary >>> 32));
        return result;
    }
}
