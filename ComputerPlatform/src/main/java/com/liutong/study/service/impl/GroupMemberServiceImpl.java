package com.liutong.study.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liutong.study.entity.GroupMember;
import com.liutong.study.mapper.GroupMemberMapper;
import com.liutong.study.service.IGroupMemberService;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements IGroupMemberService {}