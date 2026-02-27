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
     * 🔍 搜索接口
     */
    @GetMapping("/note")
    public Result<List<NoteDoc>> searchNote(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 🟢 调用上面改回来的标准方法
        // 此时 OpenSearch 会把 "驱蚊" 拆成 "驱" 和 "蚊" 去库里找，就能找到了！
        List<NoteDoc> list = noteSearchRepository.findByTitleOrContent(keyword, keyword);

        return Result.success(list);
    }
    /**
     * 🔄 同步接口
     */
    @RequestMapping(value = "/sync", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> syncAll() {
        List<Info> allNotes = infoService.list();

        List<NoteDoc> docs = new ArrayList<>();
        for (Info info : allNotes) {

            // 🛑🛑🛑 核心修正点在这里 🛑🛑🛑
            // 之前是 != 1 (导致已发布的进不去)
            // 现在改为 != 2 (只允许 status=2 已发布的笔记进入)
            if (info.getStatus() != 2) {
                continue;
            }

            NoteDoc doc = new NoteDoc();
            doc.setId(info.getNoteId());
            doc.setTitle(info.getTitle());

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