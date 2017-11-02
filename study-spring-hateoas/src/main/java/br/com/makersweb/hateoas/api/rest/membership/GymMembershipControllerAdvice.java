package br.com.makersweb.hateoas.api.rest.membership;

import br.com.makersweb.hateoas.api.repository.membership.exception.GymMembershipNotFoundException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * @author anderson.aristides
 */
@ControllerAdvice(assignableTypes = GymMembershipController.class)
@RequestMapping(produces = "application/vnd.error+json")
public class GymMembershipControllerAdvice {

    @ExceptionHandler(GymMembershipNotFoundException.class)
    public ResponseEntity<VndErrors> notFoundException(GymMembershipNotFoundException e) {
        return error(e, HttpStatus.NOT_FOUND, String.valueOf(e.getId()));
    }

    private <E extends Exception> ResponseEntity<VndErrors> error(
            final E exception, final HttpStatus httpStatus, final String logRef) {
        final String message =
                Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
        return new ResponseEntity<>(new VndErrors(logRef, message), httpStatus);
    }

}
