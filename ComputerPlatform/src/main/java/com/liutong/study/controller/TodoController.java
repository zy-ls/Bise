package com.liutong.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liutong.study.common.Result;
import com.liutong.study.entity.Todo;
import com.liutong.study.service.ITodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    private ITodoService todoService;

    // 获取当前用户的所有待办事项
    @GetMapping("/list")
    public Result<List<Todo>> getList(@RequestParam Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return Result.success(todoService.list(queryWrapper));
    }

    // 新增或修改待办
    @PostMapping("/save")
    public Result<String> save(@RequestBody Todo todo) {
        if (todo.getTodoId() == null) {
            todo.setIsCompleted(0); // 默认未完成
        }
        todoService.saveOrUpdate(todo);
        return Result.success("保存成功");
    }

    // 切换完成状态
    @PostMapping("/toggle/{id}")
    public Result<String> toggleStatus(@PathVariable Long id) {
        Todo todo = todoService.getById(id);
        if (todo != null) {
            todo.setIsCompleted(todo.getIsCompleted() == 0 ? 1 : 0);
            todoService.updateById(todo);
            return Result.success("状态更新成功");
        }
        return Result.error("任务不存在");
    }

    // 删除待办
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        todoService.removeById(id);
        return Result.success("删除成功");
    }
}