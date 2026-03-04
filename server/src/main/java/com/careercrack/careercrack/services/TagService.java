package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.Tag;
import com.careercrack.careercrack.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getOrCreate(String name) {
        return tagRepository.findByNameIgnoreCase(name
        ).orElseGet(() -> {
            Tag tag = new Tag();
            tag.setName(name.toLowerCase());
            return tagRepository.save(tag);
        });
    }

}
