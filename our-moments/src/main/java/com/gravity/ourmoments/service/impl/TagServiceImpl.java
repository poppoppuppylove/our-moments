package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.Tag;
import com.gravity.ourmoments.mapper.TagMapper;
import com.gravity.ourmoments.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.findAll();
    }

    @Override
    public Tag getTagById(Long tagId) {
        return tagMapper.findById(tagId);
    }

    @Override
    public Tag createTag(Tag tag) {
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public void deleteTag(Long tagId) {
        tagMapper.deleteById(tagId);
    }
}
