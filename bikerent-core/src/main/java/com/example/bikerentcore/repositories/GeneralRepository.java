package com.example.bikerentcore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface GeneralRepository<T, UUID> extends Repository<T, UUID> {
    <S extends T> S save(S entity);

    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Iterable<T> findAllById(Iterable<UUID> ids);

    Optional<T> findById(UUID id);

    boolean existsById(UUID id);

    Long count();
}
