package com.liutong.study.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liutong.study.entity.StudyGroup;
import com.liutong.study.mapper.StudyGroupMapper;
import com.liutong.study.service.IStudyGroupService;
import org.springframework.stereotype.Service;

@Service
public class StudyGroupServiceImpl extends ServiceImpl<StudyGroupMapper, StudyGroup> implements IStudyGroupService {}