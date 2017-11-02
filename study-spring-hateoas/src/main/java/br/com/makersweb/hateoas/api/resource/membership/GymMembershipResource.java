package br.com.makersweb.hateoas.api.resource.membership;

import br.com.makersweb.hateoas.api.entity.GymMembership;
import br.com.makersweb.hateoas.api.rest.membership.GymMembershipController;
import br.com.makersweb.hateoas.api.rest.person.PersonController;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author anderson.aristides
 */
@Getter
public class GymMembershipResource extends ResourceSupport {

    private GymMembership gymMembership;

    public GymMembershipResource(GymMembership gymMembership) {
        this.gymMembership = gymMembership;
        long membershipId = gymMembership.getId();
        long personId = gymMembership.getOwner().getId();
        add(new Link(String.valueOf(membershipId), "membership-id"));
        add(linkTo(methodOn(GymMembershipController.class).all(personId)).withRel("memberships"));
        add(linkTo(methodOn(PersonController.class).get(personId)).withRel("owner"));
        add(linkTo(methodOn(GymMembershipController.class).get(personId, membershipId)).withSelfRel());
    }

}
