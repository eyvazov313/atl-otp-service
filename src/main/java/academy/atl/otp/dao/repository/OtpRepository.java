package academy.atl.otp.dao.repository;

import academy.atl.otp.dao.entity.OtpEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

  Optional<OtpEntity> findByMsisdn(String msisdn);
}
