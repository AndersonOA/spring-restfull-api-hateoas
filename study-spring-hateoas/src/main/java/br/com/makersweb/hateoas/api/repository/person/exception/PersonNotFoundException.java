package br.com.makersweb.hateoas.api.repository.person.exception;

import lombok.Getter;

/**
 * @author anderson.aristides
 */
@Getter
public class PersonNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 7034266460267735339L;

    private final Long id;

    public PersonNotFoundException(final long id) {
        super("A pessoa não pôde ser encontrada com id: " + id);
        this.id = id;
    }
}
