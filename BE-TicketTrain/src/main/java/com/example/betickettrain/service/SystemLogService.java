package com.example.betickettrain.service;

import com.example.betickettrain.dto.SystemLogDto;
import com.example.betickettrain.entity.SystemLog;

import java.util.List;

public interface SystemLogService {
    List<SystemLogDto> FindAllLogs();
    void logAction(SystemLog log);
}
