package br.com.makersweb.hateoas.api.repository.membership.exception;

import lombok.Getter;

/**
 * @author anderson.aristides
 */
@Getter
public class GymMembershipNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -77013770899062815L;

    private final long id;

    public GymMembershipNotFoundException(final long id) {
        super("O Membership no Gym não pôde ser encontrado com id: " + id);
        this.id = id;
    }
}
