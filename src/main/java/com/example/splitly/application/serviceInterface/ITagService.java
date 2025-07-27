package com.example.splitly.application.serviceInterface;

import com.example.splitly.presentation.dto.request.TagRequest;
import com.example.splitly.presentation.dto.response.TagResponse;

import java.util.List;

public interface ITagService {
    public TagResponse create(TagRequest tagRequest);

    public List<TagResponse> getAll();

    public List<TagResponse> getAllActive();

    public TagResponse findTagById(Integer tagId);

    public void delete(Integer tagId);
}
