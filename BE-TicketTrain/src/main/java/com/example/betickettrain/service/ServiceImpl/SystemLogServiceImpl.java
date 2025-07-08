package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.SystemLogDto;
import com.example.betickettrain.entity.SystemLog;
import com.example.betickettrain.mapper.SystemLogMapper;
import com.example.betickettrain.repository.SystemLogRepository;
import com.example.betickettrain.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemLogServiceImpl implements SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public List<SystemLogDto> FindAllLogs() {
        return systemLogRepository.findTop10ByOrderByLogTimeDesc().stream().map(systemLogMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void logAction(SystemLog log) {
        SystemLogDto systemLogDto = systemLogMapper.toDto(systemLogRepository.save(log)); // Lưu vào DB
        messagingTemplate.convertAndSend("/topic/system-logs", systemLogDto); // Gửi tới WebSocket
    }
}
