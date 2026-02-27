package com.liutong.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liutong.study.entity.Literature;
import com.liutong.study.mapper.LiteratureMapper;
import com.liutong.study.service.ILiteratureService;
import org.springframework.stereotype.Service;

@Service
public class LiteratureServiceImpl  extends ServiceImpl<LiteratureMapper, Literature> implements ILiteratureService {
}
