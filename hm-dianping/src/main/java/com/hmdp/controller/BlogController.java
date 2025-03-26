package com.hmdp.controller;

import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogRepository;
import com.hmdp.mapper.UserRepository;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private BlogRepository blogRepository;
    @Resource
    private UserRepository userRepository;

    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 保存探店博文
        blogRepository.save(blog);
        // 返回id
        return Result.ok(blog.getId());
    }

    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 修改点赞数量
        Blog blog = blogRepository.findById(id).orElse(null);
        blog.setLiked(blog.getLiked() + 1);
        blogRepository.save(blog);
        return Result.ok();
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        // 根据用户查询
        Pageable pageable = PageRequest.of(current - 1, SystemConstants.MAX_PAGE_SIZE);
        Page<Blog> page =blogRepository.findAllByUserId(user.getId(), pageable);
        // 获取当前页数据
        List<Blog> records = page.get().collect(Collectors.toList());
        return Result.ok(records);
    }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 根据用户查询
        Sort sort = Sort.by(Sort.Direction.DESC, "liked");
        Pageable pageable = PageRequest.of(current - 1, SystemConstants.MAX_PAGE_SIZE, sort);
        Page<Blog> page = blogRepository.findAll(pageable);
//
//        Page<Blog> page = blogService.query()
//                .orderByDesc("liked")
//                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.get().collect(Collectors.toList());
        // 查询用户
        records.forEach(blog ->{
            Long userId = blog.getUserId();
            User user = userRepository.findById(userId).get();
            blog.setName(user.getNickName());
            blog.setIcon(user.getIcon());
        });
        return Result.ok(records);
    }
}
