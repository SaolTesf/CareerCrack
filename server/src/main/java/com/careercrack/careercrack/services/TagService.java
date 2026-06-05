package com.careercrack.careercrack.services;

import com.careercrack.careercrack.models.Tag;
import com.careercrack.careercrack.repositories.TagRepository;
import org.springframework.dao.DataIntegrityViolationException;
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
