package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.GroupMember;
import com.liutong.study.entity.StudyGroup;
import com.liutong.study.service.IGroupMemberService;
import com.liutong.study.service.IStudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/study-group")
public class StudyGroupController {

    @Autowired
    private IStudyGroupService studyGroupService;

    @Autowired
    private IGroupMemberService groupMemberService;


    @Autowired
    private com.liutong.study.service.IGroupPostService groupPostService;

    /**
     * 1. 获取学习小组大厅列表
     */
    @GetMapping("/list")
    public Result<List<StudyGroup>> getGroupList() {
        // 按照创建时间倒序返回所有小组
        List<StudyGroup> list = studyGroupService.list(
                new LambdaQueryWrapper<StudyGroup>().orderByDesc(StudyGroup::getCreateTime)
        );
        return Result.success(list);
    }

    /**
     * 2. 创建学习小组 (附带事务：创建成功后，自动将创建者设为群主)
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class) // 开启事务保证数据一致性
    public Result<String> createGroup(@RequestBody StudyGroup studyGroup) {
        if (studyGroup.getName() == null || studyGroup.getCreatorId() == null) {
            return Result.error("小组名称和创建者不能为空");
        }

        studyGroup.setCreateTime(new Date());
        // 保存小组主表
        studyGroupService.save(studyGroup);

        // 自动将创建者加入成员表，角色设为 1 (群主)
        GroupMember member = new GroupMember();
        member.setGroupId(studyGroup.getGroupId());
        member.setUserId(studyGroup.getCreatorId());
        member.setRole(1);
        member.setJoinTime(new Date());
        groupMemberService.save(member);

        return Result.success("小组创建成功");
    }

    /**
     * 3. 加入学习小组
     */
    @PostMapping("/join")
    public Result<String> joinGroup(@RequestParam Long groupId, @RequestParam Long userId) {
        // 校验是否已经加入过了
        long count = groupMemberService.count(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId));

        if (count > 0) {
            return Result.error("您已经在这个小组里啦");
        }

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(0); // 普通成员
        member.setJoinTime(new Date());

        boolean success = groupMemberService.save(member);
        return success ? Result.success("加入成功") : Result.error("加入失败");
    }

    @GetMapping("/detail/{id}")
    public Result<StudyGroup> getDetail(@PathVariable Long id) {
        return Result.success(studyGroupService.getById(id));
    }

    /**
     * 4. 解散圈子 (仅限群主)
     */
    @DeleteMapping("/delete/{id}")
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证要删全删，要么全不删
    public Result<String> deleteGroup(@PathVariable Long id, @RequestParam Long userId) {
        StudyGroup group = studyGroupService.getById(id);
        if (group == null) {
            return Result.error("该圈子不存在");
        }

        // 🚨 权限校验：验证当前操作人是不是群主
        if (!group.getCreatorId().equals(userId)) {
            return Result.error("Permission Denied: 只有群主才能解散圈子");
        }

        // 🧹 开始级联清理 (Cascade Delete)
        // 1. 清理该圈子下的所有讨论帖子
        groupPostService.remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.liutong.study.entity.GroupPost>()
                .eq(com.liutong.study.entity.GroupPost::getGroupId, id));

        // 2. 清理该圈子下的所有成员关联记录
        groupMemberService.remove(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, id));

        // 3. 最后删除圈子本体
        studyGroupService.removeById(id);

        return Result.success("Process finished: 圈子已成功解散");
    }

}