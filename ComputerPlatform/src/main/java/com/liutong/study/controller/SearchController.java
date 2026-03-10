package com.liutong.study.controller;

import com.liutong.study.common.Result;
import com.liutong.study.entity.Info;
import com.liutong.study.entity.NoteDoc;
import com.liutong.study.entity.User;
import com.liutong.study.repository.NoteSearchRepository;
import com.liutong.study.service.IInfoService;
import com.liutong.study.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private NoteSearchRepository noteSearchRepository;
    @Autowired
    private IInfoService infoService;
    @Autowired
    private IUserService userService;

    /**
     * 🔍 极客风搜索接口 (支持关键字 + 分类组合过滤)
     */
    @GetMapping("/note")
    public Result<List<NoteDoc>> searchNote(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {

        List<NoteDoc> list = new ArrayList<>();

        // 场景 1：既有关键字，又有分类
        if (keyword != null && !keyword.trim().isEmpty() && categoryId != null) {
            // 先通过搜索引擎查出所有相关关键字的笔记，再在内存里精准过滤分类
            List<NoteDoc> searchResult = noteSearchRepository.findByTitleMatchesOrContentMatchesOrTagsMatches(keyword, keyword, keyword);
            list = searchResult.stream()
                    .filter(doc -> categoryId.equals(doc.getCategoryId()))
                    .collect(Collectors.toList());
        }
        // 场景 2：只有关键字，没有分类 (全局搜索)
        else if (keyword != null && !keyword.trim().isEmpty()) {
            list = noteSearchRepository.findByTitleMatchesOrContentMatchesOrTagsMatches(keyword, keyword, keyword);
        }
        // 场景 3：只有分类，没有关键字 (点击左侧菜单)
        else if (categoryId != null) {
            list = noteSearchRepository.findByCategoryId(categoryId);
        }

        return Result.success(list);
    }

    /**
     * 🔄 全量同步接口
     */
    @RequestMapping(value = "/sync", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> syncAll() {
        List<Info> allNotes = infoService.list();

        List<NoteDoc> docs = new ArrayList<>();
        for (Info info : allNotes) {

            // 🛑 核心修正：严格限制只有 status = 2 (已发布) 的笔记才能进搜索引擎
            if (info.getStatus() != 2) {
                continue;
            }

            NoteDoc doc = new NoteDoc();
            doc.setId(info.getNoteId());
            doc.setTitle(info.getTitle());

            // 👇 新增：同步分类和标签数据
            doc.setCategoryId(info.getCategoryId());
            doc.setTags(info.getTags());

            String contentHtml = info.getContentHtml();
            if (contentHtml != null) {
                doc.setContent(contentHtml.replaceAll("<[^>]*>", ""));
            } else {
                doc.setContent("");
            }

            doc.setCreateTime(System.currentTimeMillis());

            // 补全作者
            if (info.getUserId() != null) {
                User user = userService.getById(info.getUserId());
                if (user != null) {
                    doc.setAuthorName(user.getNickname());
                    doc.setAuthorAvatar(user.getAvatar());
                }
            }
            docs.add(doc);
        }

        noteSearchRepository.deleteAll();
        if (!docs.isEmpty()) {
            noteSearchRepository.saveAll(docs);
        }

        log.info("同步完成，共导入 {} 条【已发布】笔记", docs.size());
        return Result.success("同步完成，已导入 " + docs.size() + " 条已发布笔记");
    }
}