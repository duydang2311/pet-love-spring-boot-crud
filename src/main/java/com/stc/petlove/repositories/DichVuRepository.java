package com.stc.petlove.repositories;

import com.stc.petlove.entities.DichVu;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DichVuRepository extends MongoRepository<DichVu, String> {
    Optional<DichVu> findByMaDichVu(String maDichVu);
    boolean existsByMaDichVu(String maDichVu);
}
