package com.example.taskflow.service.impl;

import com.example.taskflow.domain.Appeal;
import com.example.taskflow.dto.AppealRequest;
import com.example.taskflow.dto.AppealResponse;
import com.example.taskflow.exception.ResourceNotFoundException;
import com.example.taskflow.mapper.AppealMapper;
import com.example.taskflow.service.AppealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppealServiceImpl implements AppealService {

    private final AppealMapper appealMapper;

    public AppealServiceImpl(AppealMapper appealMapper) {
        this.appealMapper = appealMapper;
    }

    @Override
    @Transactional
    public AppealResponse create(AppealRequest request) {
        Appeal appeal = request.toDomain();
        appealMapper.insert(appeal);
        return getById(appeal.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public AppealResponse getById(Long id) {
        Appeal appeal = appealMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy appeal id=" + id));
        return AppealResponse.from(appeal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppealResponse> search(String username, String keyword) {
        return appealMapper.search(username, keyword)
                .stream()
                .map(AppealResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = appealMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Không tìm thấy appeal id=" + id);
        }
    }
}
