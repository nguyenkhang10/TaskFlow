package com.example.taskflow.service;

import com.example.taskflow.dto.AppealRequest;
import com.example.taskflow.dto.AppealResponse;

import java.util.List;

public interface AppealService {

    AppealResponse create(AppealRequest request);

    AppealResponse getById(Long id);

    List<AppealResponse> search(String username, String keyword);

    void delete(Long id);
}
