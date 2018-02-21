package com.pawelgorny.transrest.dao;

import com.pawelgorny.transrest.model.EntityExample;
import com.pawelgorny.transrest.repository.NoTransactionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityExampleDao extends NoTransactionRepository<EntityExample, Long> {

}
