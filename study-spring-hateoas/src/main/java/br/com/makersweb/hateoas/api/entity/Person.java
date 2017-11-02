package br.com.makersweb.hateoas.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author anderson.aristides
 */
@Getter
@Setter
@Entity
@Table(name = "people")
@NoArgsConstructor
public class Person implements Serializable {

    private static final long serialVersionUID = 4672870924259279902L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String secondName;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dateOfBirth;

    private String profession;

    private BigDecimal salary;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<GymMembership> memberships;

    public Person(Person person) {
        this.firstName = person.getFirstName();
        this.secondName = person.getSecondName();
        this.dateOfBirth = person.getDateOfBirth();
        this.profession = person.getProfession();
        this.salary = person.getSalary();
        this.memberships = person.getMemberships();
    }

    public Person(Person person, long id) {
        this.id = id;
        this.firstName = person.getFirstName();
        this.secondName = person.getSecondName();
        this.dateOfBirth = person.getDateOfBirth();
        this.profession = person.getProfession();
        this.salary = person.getSalary();
        this.memberships = person.getMemberships();
    }
}
