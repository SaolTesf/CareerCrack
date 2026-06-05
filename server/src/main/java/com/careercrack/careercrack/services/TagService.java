package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.TagResponse;
import com.careercrack.careercrack.mappers.TagMapper;
import com.careercrack.careercrack.models.Tag;
import com.careercrack.careercrack.repositories.TagRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    public List<TagResponse> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tagMapper.toDto(tags);
    }

    public Tag findOrCreate(String name) {
        return tagRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            try{
                Tag tag = new Tag();
                tag.setName(name.toLowerCase());
                return tagRepository.save(tag);
            }
            catch (DataIntegrityViolationException e) {
                // If another thread saved it right before this one, just find it again
                return tagRepository.findByNameIgnoreCase(name).orElseThrow(() -> new RuntimeException("Failed to find or create tag with same " + name));
            }

        });
    }

}
