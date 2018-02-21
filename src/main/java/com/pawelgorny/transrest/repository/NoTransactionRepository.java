package com.pawelgorny.transrest.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface NoTransactionRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID> {


    <S extends T> S saveAndFlushNoTransaction(S entity);
    void deleteNoTransaction(T entity);
}
