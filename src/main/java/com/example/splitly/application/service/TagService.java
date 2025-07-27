package com.example.splitly.application.service;

import com.example.splitly.application.mapper.TagMapper;
import com.example.splitly.application.serviceInterface.IRoleService;
import com.example.splitly.application.serviceInterface.ITagService;
import com.example.splitly.domain.repository.PermissionRepository;
import com.example.splitly.domain.repository.TagRepository;
import com.example.splitly.presentation.dto.request.RoleRequest;
import com.example.splitly.presentation.dto.request.TagRequest;
import com.example.splitly.presentation.dto.response.RoleResponse;
import com.example.splitly.presentation.dto.response.TagResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService implements ITagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagResponse create(TagRequest tagRequest) {
        var tag = tagMapper.toTag(tagRequest);
        tag.setDeleted(false);

        tag = tagRepository.save(tag);

        return tagMapper.toTagResponse(tag);
    }

    @Override
    public List<TagResponse> getAll() {
        return tagRepository.findAll()
                .stream()
                .map(tagMapper::toTagResponse)
                .toList();
    }

    @Override
    public List<TagResponse> getAllActive() {
        return tagRepository.findAll()
                .stream()
                .filter(tag -> !tag.isDeleted())
                .map(tagMapper::toTagResponse)
                .toList();
    }

    @Override
    public TagResponse findTagById(Integer tagId) {
        return tagRepository.findById(tagId)
                .map(tagMapper::toTagResponse)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + tagId));
    }

    @Override
    @Transactional
    public void delete(Integer tagId) {
        var tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + tagId));

        tag.setDeleted(true);
        tagRepository.save(tag);
    }
}
