package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.entity.SystemLog;
import com.example.betickettrain.repository.SystemLogRepository;
import com.example.betickettrain.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemLogServiceImpl implements SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void logAction(SystemLog log) {
       // systemLogRepository.save(log); // Lưu vào DB
        messagingTemplate.convertAndSend("/topic/system-logs", log); // Gửi tới WebSocket
    }
}
