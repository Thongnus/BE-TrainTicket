package com.example.betickettrain.service;

import com.example.betickettrain.dto.NewfeedDto;
import com.example.betickettrain.entity.Newfeed;
import com.example.betickettrain.mapper.NewfeedMapper;
import com.example.betickettrain.repository.NewfeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NewFeedServiceImpl implements NewFeedService{

    @Autowired
    private NewfeedRepository newfeedRepository;
    @Autowired
    private NewfeedMapper newfeedMapper;
    @Override
    public List<NewfeedDto> getAllNewfeeds() {
        List<Newfeed> newfeeds = newfeedRepository.findAll();
        // Convert Newfeed entities to NewfeedDto objects
        return newfeeds.stream()
                .map(newfeedMapper::toDto)
                .toList();
    }
    // Tạo newfeed mới
    public NewfeedDto createNewfeed(NewfeedDto newfeedDto) {
        Newfeed newfeed = newfeedMapper.toEntity(newfeedDto);
        newfeed = newfeedRepository.save(newfeed);
        return newfeedMapper.toDto(newfeed);
    }

    // Cập nhật newfeed
    public NewfeedDto updateNewfeed(Long id, NewfeedDto newfeedDto) {
        return newfeedRepository.findById(id)
                .map(existingNewfeed -> {
                    Newfeed updated = newfeedMapper.partialUpdate(newfeedDto, existingNewfeed);
                    return newfeedMapper.toDto(newfeedRepository.save(updated));
                })
                .orElseThrow(() -> new RuntimeException("Newfeed not found with id: " + id));
    }

    // Lấy newfeed theo ID
    public NewfeedDto getNewfeedById(Long id) {
        return newfeedRepository.findById(id)
                .map(newfeedMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Newfeed not found with id: " + id));
    }

    // Xóa newfeed
    public void deleteNewfeed(Long id) {
        newfeedRepository.deleteById(id);
    }

    @Override
    public List<NewfeedDto> searchNewfeeds(String keyword) {
        return List.of();
    }

    @Override
    public List<NewfeedDto> getNewfeedsByCategory(String category) {
        return List.of();
    }
}
