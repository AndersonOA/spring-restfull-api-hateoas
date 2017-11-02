package br.com.makersweb.hateoas.api.rest.person;

import br.com.makersweb.hateoas.api.entity.Person;
import br.com.makersweb.hateoas.api.repository.person.IPersonRepository;
import br.com.makersweb.hateoas.api.repository.person.exception.PersonNotFoundException;
import br.com.makersweb.hateoas.api.resource.person.PersonResource;
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
@RequestMapping(value = "/people", produces = "application/hal+json")
public class PersonController {

    private IPersonRepository personRepository;

    public PersonController(IPersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public ResponseEntity<Resources<PersonResource>> all() {
        List<PersonResource> collection = personRepository.findAll().stream().map(PersonResource::new).collect(Collectors.toList());
        Resources<PersonResource> resources = new Resources<>(collection);
        String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        resources.add(new Link(uriString, "self"));
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResource> get(@PathVariable final long id) {
        return personRepository
                .findById(id)
                .map(p -> ResponseEntity.ok(new PersonResource(p)))
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<PersonResource> post(@RequestBody final Person personFromRequest) {
        final Person person = personRepository.save(new Person(personFromRequest));
        final URI uri =
                MvcUriComponentsBuilder.fromController(getClass())
                        .path("/{id}")
                        .buildAndExpand(person.getId())
                        .toUri();
        return ResponseEntity.created(uri).body(new PersonResource(person));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResource> put(
            @PathVariable("id") final long id, @RequestBody Person personFromRequest) {
        final Person person = new Person(personFromRequest, id);
        personRepository.save(person);
        final PersonResource resource = new PersonResource(person);
        final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") final long id) {
        return personRepository
                .findById(id)
                .map(
                        p -> {
                            personRepository.deleteById(id);
                            return ResponseEntity.noContent().build();
                        })
                .orElseThrow(() -> new PersonNotFoundException(id));
    }
}
