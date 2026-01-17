package com.bank.loan.repository;

import com.bank.loan.model.Loan;
import com.bank.loan.model.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanRepository extends MongoRepository<Loan, String> {

    Page<Loan> findByDeletedFalse(Pageable pageable);

    Page<Loan> findByDeletedFalseAndStatus(LoanStatus status, Pageable pageable);

    Page<Loan> findByDeletedFalseAndCreatedBy(String createdBy, Pageable pageable);
}
