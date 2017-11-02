package br.com.makersweb.hateoas.api.repository.membership;

import br.com.makersweb.hateoas.api.entity.GymMembership;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author anderson.aristides
 */
public interface IGymMembershipRepository extends JpaRepository<GymMembership, Long> {
}
