package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.Request.PositionRequestDTO;
import com.crm.BackendCrm.dto.Response.PositionResponseDTO;
import com.crm.BackendCrm.entity.Position;
import com.crm.BackendCrm.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;

    public List<PositionResponseDTO> getAll() {
        return positionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PositionResponseDTO getById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Position"));
        return toDTO(position);
    }

    public PositionResponseDTO create(PositionRequestDTO dto) {
        Position position = new Position();
        position.setNamePosition(dto.namePosition());
        return toDTO(positionRepository.save(position));
    }

    public PositionResponseDTO update(Long id, PositionRequestDTO dto) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Position"));
        position.setNamePosition(dto.namePosition());
        return toDTO(positionRepository.save(position));
    }

    public void delete(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Position");
        }
        positionRepository.deleteById(id);
    }

    private PositionResponseDTO toDTO(Position p) {
        return new PositionResponseDTO(p.getId(), p.getNamePosition());
    }
}
