package com.liutong.study.mapper;

import com.liutong.study.entity.Info;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 笔记信息主表 Mapper 接口
 * </p>
 */
@Mapper
public interface InfoMapper extends BaseMapper<Info> {

    /**
     * 💡 新增：供管理员大屏使用的近 7 天发帖趋势查询
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, COUNT(*) as count " +
            "FROM note_info " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d') " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getNoteTrendDays();

}