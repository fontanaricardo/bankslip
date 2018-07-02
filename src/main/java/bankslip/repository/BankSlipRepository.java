package bankslip.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import bankslip.model.BankSlip;

@Repository
public interface BankSlipRepository extends CrudRepository<BankSlip, Long> {
	BankSlip findById(UUID id);
}