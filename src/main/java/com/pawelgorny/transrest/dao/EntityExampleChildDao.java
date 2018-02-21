package com.pawelgorny.transrest.dao;

import com.pawelgorny.transrest.model.EntityExampleChild;
import com.pawelgorny.transrest.repository.NoTransactionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityExampleChildDao extends NoTransactionRepository<EntityExampleChild, Long> {
}
