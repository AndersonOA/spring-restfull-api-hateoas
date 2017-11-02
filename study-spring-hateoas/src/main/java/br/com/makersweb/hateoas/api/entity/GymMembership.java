package br.com.makersweb.hateoas.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author anderson.aristides
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "memberships")
@AllArgsConstructor
@NoArgsConstructor
public class GymMembership implements Serializable {

    private static final long serialVersionUID = 4093443236047967837L;

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Person owner;

    private String name;

    private long cost;

    public GymMembership(Person owner, String name, long cost) {
        this.owner = owner;
        this.name = name;
        this.cost = cost;
    }
}
