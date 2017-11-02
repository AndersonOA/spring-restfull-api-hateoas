package br.com.makersweb.hateoas.api.resource.person;

import br.com.makersweb.hateoas.api.entity.Person;
import br.com.makersweb.hateoas.api.rest.membership.GymMembershipController;
import br.com.makersweb.hateoas.api.rest.person.PersonController;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author anderson.aristides
 */
@Getter
public class PersonResource extends ResourceSupport {

    private Person person;

    public PersonResource(Person person) {
        this.person = person;
        long id = person.getId();
        add(linkTo(PersonController.class).withRel("people"));
        add(linkTo(methodOn(GymMembershipController.class).all(id)).withRel("memberships"));
        add(linkTo(methodOn(PersonController.class).get(id)).withSelfRel());
    }
}
