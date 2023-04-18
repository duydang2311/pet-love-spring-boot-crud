package com.stc.petlove.repositories;

import com.stc.petlove.entities.LoaiThuCung;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LoaiThuCungRepository extends MongoRepository<LoaiThuCung, String> {
    Optional<LoaiThuCung> findByMaLoaiThuCung(String maLoaiThuCung);
    boolean existsByMaLoaiThuCung(String maLoaiThuCung);
}
