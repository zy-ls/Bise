package com.liutong.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liutong.study.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 1. 查询过去 365 天的活跃度 (联合查询：笔记 + 待办 + 评论)
     * 💡 修正点：把原来的 sys_comment 换成了你数据库里真正的表名 comment
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM (" +
            "SELECT create_time FROM note_info WHERE user_id = #{userId} " +
            "UNION ALL " +
            "SELECT create_time FROM sys_todo WHERE user_id = #{userId} " +
            "UNION ALL " +
            "SELECT create_time FROM comment WHERE user_id = #{userId}" +
            ") as activity_log " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 365 DAY) " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getUserActivityStats(@Param("userId") Long userId);

    /**
     * 2. 查询能力雷达图分布 (统计该用户在各分类下的发文数)
     * 你的 sys_category 和 note_info 完美匹配，这段没问题！
     */
    @Select("SELECT c.name as name, COUNT(n.note_id) as count " +
            "FROM sys_category c " +
            "LEFT JOIN note_info n ON c.category_id = n.category_id AND n.user_id = #{userId} AND n.status = 2 " +
            "GROUP BY c.category_id, c.name")
    List<Map<String, Object>> getUserRadarStats(@Param("userId") Long userId);
}