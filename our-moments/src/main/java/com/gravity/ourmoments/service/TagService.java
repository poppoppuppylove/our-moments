package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.Tag;
import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(Long tagId);
    Tag getTagByName(String name);
    Tag getOrCreateTag(String name);
    Tag createTag(Tag tag);
    void deleteTag(Long tagId);
}
