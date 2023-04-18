package com.stc.petlove.controllers;

import com.stc.petlove.entities.TaiKhoan;
import com.stc.petlove.repositories.TaiKhoanRepository;
import com.stc.petlove.utils.PropertyUtils;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/taikhoan")
public class TaiKhoanController {
    private final TaiKhoanRepository taiKhoanRepository;

    public TaiKhoanController(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(taiKhoanRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (taiKhoanRepository.findById(id)).map(taiKhoan -> new ResponseEntity<>(taiKhoan, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody TaiKhoan newTaiKhoan) {
        val taiKhoanOptional = taiKhoanRepository.findById(id);
        if (taiKhoanOptional.isPresent()) {
            val taiKhoan = taiKhoanOptional.get();
            if (!Objects.equals(taiKhoan.getEmail(), newTaiKhoan.getEmail())
                    && taiKhoanRepository.existsByEmail(newTaiKhoan.getEmail())) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

            newTaiKhoan.setId(id);
            BeanUtils.copyProperties(newTaiKhoan, taiKhoan, PropertyUtils.getNullPropertyNames(newTaiKhoan));
            try {
                taiKhoanRepository.save(taiKhoan);
                return new ResponseEntity<>(taiKhoan, HttpStatus.OK);
            } catch(Exception ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody TaiKhoan newTaiKhoan) {
        if (taiKhoanRepository.existsByEmail(newTaiKhoan.getEmail())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        val savedTaiKhoan = taiKhoanRepository.save(newTaiKhoan);
        return new ResponseEntity<>(savedTaiKhoan, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (taiKhoanRepository.existsById(id)) {
            taiKhoanRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        return (taiKhoanRepository.findByEmail(email)).map(taiKhoan -> new ResponseEntity<>(taiKhoan, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
}
