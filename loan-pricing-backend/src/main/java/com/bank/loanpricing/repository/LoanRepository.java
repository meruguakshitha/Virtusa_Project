package com.bank.loanpricing.repository;

import com.bank.loanpricing.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanRepository extends MongoRepository<Loan, String> {

    Optional<Loan> findByIdAndDeletedFalse(String id);

    Page<Loan> findByDeletedFalse(Pageable pageable);
}
