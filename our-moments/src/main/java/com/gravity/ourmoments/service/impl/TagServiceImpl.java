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
    public Tag getTagByName(String name) {
        return tagMapper.findByName(name);
    }

    @Override
    public Tag getOrCreateTag(String name) {
        // 先查找是否已存在
        Tag existingTag = tagMapper.findByName(name);
        if (existingTag != null) {
            return existingTag;
        }

        // 如果不存在，创建新标签
        Tag newTag = new Tag();
        newTag.setName(name);
        tagMapper.insert(newTag);
        return newTag;
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
