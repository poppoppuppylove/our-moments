package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TagMapper {
    Tag findById(Long tagId);
    Tag findByName(String name);
    List<Tag> findAll();
    List<Tag> findByPostId(Long postId);
    int insert(Tag tag);
    int deleteById(Long tagId);
}
