package com.example.betickettrain.controller;

import com.example.betickettrain.dto.NewfeedDto;
import com.example.betickettrain.dto.Response;
import com.example.betickettrain.service.NewFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newfeeds")
public class NewFeedController {
    @Autowired
    private NewFeedService newFeedService;

    @GetMapping
    public Response<List<NewfeedDto>> getAllNewFeeds() {
        return new Response<>(newFeedService.getAllNewfeeds());
    }

    @GetMapping("/{id}")
    public Response<NewfeedDto> getNewFeedById(@PathVariable Long id) {
        return new Response<>(newFeedService.getNewfeedById(id));
    }

    @PostMapping("/create")
    public Response<NewfeedDto> createNewFeed(@RequestBody NewfeedDto newfeedDto) {
        return new Response<>(newFeedService.createNewfeed(newfeedDto));
    }

    @PutMapping("/{id}")
    public Response<NewfeedDto> updateNewFeed(@PathVariable Long id, @RequestBody NewfeedDto newfeedDto) {
        return new Response<>(newFeedService.updateNewfeed(id, newfeedDto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteNewFeed(@PathVariable Long id) {
        newFeedService.deleteNewfeed(id);
        return Response.of(null, "Xóa bài đăng thành công");
    }

}
