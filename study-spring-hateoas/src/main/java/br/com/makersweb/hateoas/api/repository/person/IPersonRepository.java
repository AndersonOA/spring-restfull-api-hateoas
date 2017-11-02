package br.com.makersweb.hateoas.api.repository.person;

import br.com.makersweb.hateoas.api.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author anderson.aristides
 */
public interface IPersonRepository extends JpaRepository<Person, Long> {
}
