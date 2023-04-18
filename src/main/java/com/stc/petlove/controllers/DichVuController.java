package com.stc.petlove.controllers;

import com.stc.petlove.entities.DichVu;
import com.stc.petlove.repositories.DichVuRepository;
import com.stc.petlove.repositories.LoaiThuCungRepository;
import com.stc.petlove.utils.PropertyUtils;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dich-vu")
public class DichVuController {
    private final DichVuRepository dichVuRepository;
    private final LoaiThuCungRepository loaiThuCungRepository;

    public DichVuController(DichVuRepository dichVuRepository, LoaiThuCungRepository loaiThuCungRepository) {
        this.dichVuRepository = dichVuRepository;
        this.loaiThuCungRepository = loaiThuCungRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(dichVuRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (dichVuRepository.findById(id)).map(dichVu -> new ResponseEntity<>(dichVu, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody DichVu newDichVu) {
        val validationOptional = validateDichVu(newDichVu);
        if (validationOptional.isPresent()) {
            return validationOptional.get();
        }

        val dichVuOptional = dichVuRepository.findById(id);
        if (dichVuOptional.isPresent()) {
            val dichVu = dichVuOptional.get();
            if (!Objects.equals(dichVu.getMaDichVu(), newDichVu.getMaDichVu())
                    && dichVuRepository.existsByMaDichVu(newDichVu.getMaDichVu())) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

            newDichVu.setId(id);
            BeanUtils.copyProperties(newDichVu, dichVu, PropertyUtils.getNullPropertyNames(newDichVu));
            try {
                dichVuRepository.save(dichVu);
                return new ResponseEntity<>(dichVu, HttpStatus.OK);
            } catch(Exception ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody DichVu newDichVu) {
        val validationOptional = validateDichVu(newDichVu);
        if (validationOptional.isPresent()) {
            return validationOptional.get();
        }

        if (dichVuRepository.existsByMaDichVu(newDichVu.getMaDichVu())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        val savedDichVu = dichVuRepository.save(newDichVu);
        return new ResponseEntity<>(savedDichVu, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (dichVuRepository.existsById(id)) {
            dichVuRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/ma/{maDichVu}")
    public ResponseEntity<?> getByMaDichVu(@PathVariable String maDichVu) {
        return (dichVuRepository.findByMaDichVu(maDichVu)).map(dichVu -> new ResponseEntity<>(dichVu, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    private Optional<ResponseEntity<?>> validateDichVu(DichVu dichVu) {
        for (val gia: dichVu.getGiaDichVus()) {
            if (!loaiThuCungRepository.existsByMaLoaiThuCung(gia.getLoaiThuCung())) {
                return Optional.of(new ResponseEntity<>(String.format("loai thu cung %s khong ton tai", gia.getLoaiThuCung()), HttpStatus.UNPROCESSABLE_ENTITY));
            }
        }
        return Optional.empty();
    }
}
