package com.stc.petlove.controllers;

import com.stc.petlove.entities.DatCho;
import com.stc.petlove.entities.DichVu;
import com.stc.petlove.repositories.DatChoRepository;
import com.stc.petlove.repositories.DichVuRepository;
import com.stc.petlove.utils.PropertyUtils;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dat-cho")
public class DatChoController {
    private final DatChoRepository datChoRepository;
    private final DichVuRepository dichVuRepository;

    public DatChoController(DatChoRepository datChoRepository, DichVuRepository dichVuRepository) {
        this.datChoRepository = datChoRepository;
        this.dichVuRepository = dichVuRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(datChoRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (datChoRepository.findById(id)).map(datCho -> new ResponseEntity<>(datCho, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody DatCho newDatCho) {
        val validationOptional = validateDatCho(newDatCho);
        if (validationOptional.isPresent()) {
            return validationOptional.get();
        }

        val datChoOptional = datChoRepository.findById(id);
        if (datChoOptional.isPresent()) {
            val datCho = datChoOptional.get();
            newDatCho.setId(id);
            BeanUtils.copyProperties(newDatCho, datCho, PropertyUtils.getNullPropertyNames(newDatCho));
            try {
                datChoRepository.save(datCho);
                return new ResponseEntity<>(datCho, HttpStatus.OK);
            } catch(Exception ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody DatCho newDatCho) {
        val validationOptional = validateDatCho(newDatCho);
        if (validationOptional.isPresent()) {
            return validationOptional.get();
        }

        val savedDatCho = datChoRepository.save(newDatCho);
        return new ResponseEntity<>(savedDatCho, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (datChoRepository.existsById(id)) {
            datChoRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    private Optional<ResponseEntity<?>> validateDatCho(DatCho datCho) {
        for (val thongTin: datCho.getThongTinDatChos()) {
            if (!dichVuRepository.existsByMaDichVu(thongTin.getDichVu())) {
                return Optional.of(new ResponseEntity<>(String.format("dich vu %s khong ton tai", thongTin.getDichVu()), HttpStatus.UNPROCESSABLE_ENTITY));
            }
        }
        return Optional.empty();
    }
}
