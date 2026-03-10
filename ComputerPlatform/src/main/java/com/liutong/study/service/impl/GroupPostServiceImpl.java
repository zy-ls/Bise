package com.liutong.study.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liutong.study.entity.GroupPost;
import com.liutong.study.mapper.GroupPostMapper;
import com.liutong.study.service.IGroupPostService;
import org.springframework.stereotype.Service;

@Service
public class GroupPostServiceImpl extends ServiceImpl<GroupPostMapper, GroupPost> implements IGroupPostService {}