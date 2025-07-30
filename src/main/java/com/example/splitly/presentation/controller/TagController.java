package com.example.splitly.presentation.controller;

import com.example.splitly.application.serviceInterface.ITagService;
import com.example.splitly.presentation.dto.request.TagRequest;
import com.example.splitly.presentation.dto.response.ResponseData;
import com.example.splitly.presentation.dto.response.TagResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {
    private final ITagService tagService;

    public TagController(ITagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseData<?> create(@RequestBody TagRequest tagRequest) {
        TagResponse createdTag = tagService.create(tagRequest);
        return new ResponseData<>(HttpStatus.OK.value(), "Tag saved successfully", createdTag);
    }

    @GetMapping
    public ResponseData<?> getAll() {
        List<TagResponse> tags = tagService.getAll();
        return new ResponseData<>(HttpStatus.OK.value(), "Get all tag successfully", tags);
    }

    @GetMapping("/active")
    public ResponseData<?> getAllActive() {
        List<TagResponse> activeTags = tagService.getAllActive();
        return new ResponseData<>(HttpStatus.OK.value(), "Get all active tag successfully", activeTags);
    }

    @GetMapping("/{id}")
    public ResponseData<?> findById(@PathVariable Integer id) {
        TagResponse tag = tagService.findTagById(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Get tag by id successfully", tag);
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Integer id) {
        tagService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Delete tag successfully");
    }
}
