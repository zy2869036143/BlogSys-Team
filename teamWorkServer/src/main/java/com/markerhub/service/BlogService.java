package com.markerhub.service;

import com.markerhub.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface BlogService extends IService<Blog> {
    List<Blog> getblogByKey(String key);
    List<Blog> getblogs();
}
