package com.stc.petlove.controllers;

import com.stc.petlove.entities.LoaiThuCung;
import com.stc.petlove.repositories.LoaiThuCungRepository;
import com.stc.petlove.utils.PropertyUtils;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/loai-thu-cung")
public class LoaiThuCungController {
    private final LoaiThuCungRepository loaiThuCungRepository;

    public LoaiThuCungController(LoaiThuCungRepository loaiThuCungRepository) {
        this.loaiThuCungRepository = loaiThuCungRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(loaiThuCungRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (loaiThuCungRepository.findById(id)).map(loaiThuCung -> new ResponseEntity<>(loaiThuCung, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody LoaiThuCung newLoaiThuCung) {
        val loaiThuCungOptional = loaiThuCungRepository.findById(id);
        if (loaiThuCungOptional.isPresent()) {
            val loaiThuCung = loaiThuCungOptional.get();
            if (!Objects.equals(loaiThuCung.getMaLoaiThuCung(), newLoaiThuCung.getMaLoaiThuCung())
                    && loaiThuCungRepository.existsByMaLoaiThuCung(newLoaiThuCung.getMaLoaiThuCung())) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

            newLoaiThuCung.setId(id);
            BeanUtils.copyProperties(newLoaiThuCung, loaiThuCung, PropertyUtils.getNullPropertyNames(newLoaiThuCung));
            try {
                loaiThuCungRepository.save(loaiThuCung);
                return new ResponseEntity<>(loaiThuCung, HttpStatus.OK);
            } catch (Exception ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody LoaiThuCung newLoaiThuCung) {
        if (loaiThuCungRepository.existsByMaLoaiThuCung(newLoaiThuCung.getMaLoaiThuCung())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        val savedLoaiThuCung = loaiThuCungRepository.save(newLoaiThuCung);
        return new ResponseEntity<>(savedLoaiThuCung, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (loaiThuCungRepository.existsById(id)) {
            loaiThuCungRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/ma/{maLoaiThuCung}")
    public ResponseEntity<?> getByMaLoaiThuCung(@PathVariable String maLoaiThuCung) {
        return (loaiThuCungRepository.findByMaLoaiThuCung(maLoaiThuCung)).map(loaiThuCung -> new ResponseEntity<>(loaiThuCung, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
}
