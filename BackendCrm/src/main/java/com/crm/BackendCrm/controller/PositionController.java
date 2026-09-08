package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.Request.PositionRequestDTO;
import com.crm.BackendCrm.dto.Response.PositionResponseDTO;
import com.crm.BackendCrm.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public ResponseEntity<List<PositionResponseDTO>> getAll() {
        return ResponseEntity.ok(positionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(positionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PositionResponseDTO> create(@RequestBody PositionRequestDTO dto) {
        return ResponseEntity.ok(positionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionResponseDTO> update(@PathVariable Long id, @RequestBody PositionRequestDTO dto) {
        return ResponseEntity.ok(positionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        positionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
