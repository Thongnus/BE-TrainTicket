package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.NewfeedDto;
import com.example.betickettrain.entity.Newfeed;
import com.example.betickettrain.mapper.NewfeedMapper;
import com.example.betickettrain.repository.NewfeedRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.NewFeedService;
import com.example.betickettrain.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewFeedServiceImpl implements NewFeedService {

    private final NewfeedRepository newfeedRepository;
    private final NewfeedMapper newfeedMapper;
    private final GenericCacheService cacheService;


    private static final String ALL_FEEDS_KEY = "all";

    @Override
    public List<NewfeedDto> getAllNewfeeds() {
        // Kiểm tra cache trước
        List<NewfeedDto> cached = cacheService.get(Constants.Cache.CACHE_NEWFEED, ALL_FEEDS_KEY);
        if (cached != null) {
            return cached;
        }

        // Nếu chưa có trong cache => lấy từ DB và cache lại
        List<Newfeed> newfeeds = newfeedRepository.findAll();
        List<NewfeedDto> dtos = newfeeds.stream()
                .map(newfeedMapper::toDto)
                .toList();

        cacheService.put(Constants.Cache.CACHE_NEWFEED, ALL_FEEDS_KEY, dtos);
        return dtos;
    }

    @Override
    @LogAction(action = Constants.Action.CREATE,entity = "NewFeed", description = " Create a newfeed")
    public NewfeedDto createNewfeed(NewfeedDto newfeedDto) {
        Newfeed newfeed = newfeedMapper.toEntity(newfeedDto);
        newfeed = newfeedRepository.save(newfeed);
        cacheService.clearCache(Constants.Cache.CACHE_NEWFEED); // clear toàn bộ cache newfeed
        return newfeedMapper.toDto(newfeed);
    }

    @Override
    @LogAction(action = Constants.Action.UPDATE,entity = "NewFeed", description = " Update a newfeed")
    public NewfeedDto updateNewfeed(Long id, NewfeedDto newfeedDto) {
        NewfeedDto updated = newfeedRepository.findById(id)
                .map(existingNewfeed -> {
                    Newfeed updatedEntity = newfeedMapper.partialUpdate(newfeedDto, existingNewfeed);
                    return newfeedMapper.toDto(newfeedRepository.save(updatedEntity));
                })
                .orElseThrow(() -> new RuntimeException("Newfeed not found with id: " + id));

        // Cập nhật cache: xóa cả danh sách và từng phần tử
        cacheService.remove(Constants.Cache.CACHE_NEWFEED, ALL_FEEDS_KEY);
        cacheService.remove(Constants.Cache.CACHE_NEWFEED, id);
        return updated;
    }

    @Override
    public NewfeedDto getNewfeedById(Long id) {
        NewfeedDto cached = cacheService.get(Constants.Cache.CACHE_NEWFEED, id);
        if (cached != null) return cached;

        NewfeedDto dto = newfeedRepository.findById(id)
                .map(newfeedMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Newfeed not found with id: " + id));

        cacheService.put(Constants.Cache.CACHE_NEWFEED, id, dto);
        return dto;
    }

    @Override
    @LogAction(action = Constants.Action.DELETE,entity = "NewFeed", description = " Delete a newfeed")
    public void deleteNewfeed(Long id) {
        newfeedRepository.deleteById(id);
        cacheService.remove(Constants.Cache.CACHE_NEWFEED, id);
        cacheService.remove(Constants.Cache.CACHE_NEWFEED, ALL_FEEDS_KEY); // cập nhật lại danh sách sau khi xóa
    }
}
