package com.markerhub.Dao;

import com.markerhub.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogDao {
    List<Blog> getblogByKey(String key);
    List<Blog> getblogs();
    void updateblog(Blog blog);
    List<Blog> getpraiseByid(int id);
    List<Blog> getfavoriteByid(int id);
}
