package com.stc.petlove.repositories;

import com.stc.petlove.entities.TaiKhoan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TaiKhoanRepository extends MongoRepository<TaiKhoan, String> {
    Optional<TaiKhoan> findByEmail(String email);
    boolean existsByEmail(String email);
}
