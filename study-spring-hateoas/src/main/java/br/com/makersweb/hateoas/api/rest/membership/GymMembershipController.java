package br.com.makersweb.hateoas.api.rest.membership;

import br.com.makersweb.hateoas.api.entity.GymMembership;
import br.com.makersweb.hateoas.api.entity.Person;
import br.com.makersweb.hateoas.api.repository.membership.IGymMembershipRepository;
import br.com.makersweb.hateoas.api.repository.membership.exception.GymMembershipNotFoundException;
import br.com.makersweb.hateoas.api.repository.person.IPersonRepository;
import br.com.makersweb.hateoas.api.repository.person.exception.PersonNotFoundException;
import br.com.makersweb.hateoas.api.resource.membership.GymMembershipResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author anderson.aristides
 */
@RestController
@RequestMapping("/people/{personId}/memberships")
public class GymMembershipController {

    private IPersonRepository personRepository;
    private IGymMembershipRepository gymMembershipRepository;

    public GymMembershipController(IPersonRepository personRepository, IGymMembershipRepository gymMembershipRepository) {
        this.personRepository = personRepository;
        this.gymMembershipRepository = gymMembershipRepository;
    }

    @GetMapping
    public ResponseEntity<Resources<GymMembershipResource>> all(@PathVariable long personId) {
        List<GymMembershipResource> collection = getMembershipsForPerson(personId);
        Resources<GymMembershipResource> resources = new Resources<>(collection);
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        resources.add(new Link(uriString, "self"));
        return ResponseEntity.ok(resources);
    }

    private List<GymMembershipResource> getMembershipsForPerson(long personId) {
        return personRepository
                .findById(personId)
                .map(
                        p ->
                                p.getMemberships()
                                        .stream()
                                        .map(GymMembershipResource::new)
                                        .collect(Collectors.toList()))
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }

    private void validatePerson(long personId) {
        personRepository.findById(personId).orElseThrow(() -> new PersonNotFoundException(personId));
    }

    @GetMapping("/{membershipId}")
    public ResponseEntity<GymMembershipResource> get(
            @PathVariable long personId, @PathVariable long membershipId) {
        return personRepository
                .findById(personId)
                .map(
                        p ->
                                p.getMemberships()
                                        .stream()
                                        .filter(m -> m.getId().equals(membershipId))
                                        .findAny()
                                        .map(m -> ResponseEntity.ok(new GymMembershipResource(m)))
                                        .orElseThrow(() -> new GymMembershipNotFoundException(membershipId)))
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }

    @PostMapping
    public ResponseEntity<GymMembershipResource> post(
            @PathVariable long personId, @RequestBody GymMembership inputMembership) {
        return personRepository
                .findById(personId)
                .map(
                        p -> {
                            final GymMembership membership = saveMembership(p, inputMembership);
                            final URI uri = createPostUri(membership);
                            return ResponseEntity.created(uri).body(new GymMembershipResource(membership));
                        })
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }

    private GymMembership saveMembership(Person person, final GymMembership membership) {
        return gymMembershipRepository.save(
                new GymMembership(person, membership.getName(), membership.getCost()));
    }

    private URI createPostUri(GymMembership membership) {
        return MvcUriComponentsBuilder.fromController(getClass())
                .path("/{membershipId}")
                .buildAndExpand(membership.getOwner().getId(), membership.getId())
                .toUri();
    }

    @PutMapping("/{membershipId}")
    public ResponseEntity<GymMembershipResource> put(
            @PathVariable long personId,
            @PathVariable long membershipId,
            @RequestBody GymMembership inputMembership) {
        return personRepository
                .findById(personId)
                .map(
                        p -> {
                            final GymMembership membership = updateMembership(p, membershipId, inputMembership);
                            final URI uri =
                                    URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
                            return ResponseEntity.created(uri).body(new GymMembershipResource(membership));
                        })
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }

    private GymMembership updateMembership(
            Person person, long id, GymMembership membership) {
        return gymMembershipRepository.save(
                new GymMembership(id, person, membership.getName(), membership.getCost()));
    }

    @DeleteMapping("/{membershipId}")
    public ResponseEntity<?> delete(
            @PathVariable long personId, @PathVariable long membershipId) {
        return personRepository
                .findById(personId)
                .map(
                        p ->
                                p.getMemberships()
                                        .stream()
                                        .filter(m -> m.getId().equals(membershipId))
                                        .findAny()
                                        .map(
                                                m -> {
                                                    gymMembershipRepository.delete(m);
                                                    return ResponseEntity.noContent().build();
                                                })
                                        .orElseThrow(() -> new GymMembershipNotFoundException(membershipId)))
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }

}
