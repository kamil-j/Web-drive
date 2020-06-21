package pl.edu.agh.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.user.domain.User;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by Kamil on 2017-07-14.
 */
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findOneByCloudId(String cloudId);
    Page<User> findAllByOrderByEmailAsc(Pageable pageable);
}
