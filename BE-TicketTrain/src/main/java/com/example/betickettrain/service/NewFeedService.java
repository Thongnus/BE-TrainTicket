package com.example.betickettrain.service;

import com.example.betickettrain.dto.NewfeedDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface NewFeedService {
    List<NewfeedDto> getAllNewfeeds();
    NewfeedDto getNewfeedById(Long id);

    // Các phương thức nhận tham số là DTO
    NewfeedDto createNewfeed(NewfeedDto createDto);
    NewfeedDto updateNewfeed(Long id, NewfeedDto updateDto);
    void deleteNewfeed(Long id);

    // Các phương thức tìm kiếm, lọc cũng trả về DTO
    List<NewfeedDto> searchNewfeeds(String keyword);
    List<NewfeedDto> getNewfeedsByCategory(String category);
}

