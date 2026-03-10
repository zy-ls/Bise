package com.liutong.study.repository;

import com.liutong.study.entity.NoteDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteSearchRepository extends ElasticsearchRepository<NoteDoc, Long> {

    /**
     * 1. 关键字全局搜索 (标题 OR 内容 OR 标签)
     * Matches 会自动调用你实体类里配置的 IK 分词器
     */
    List<NoteDoc> findByTitleMatchesOrContentMatchesOrTagsMatches(String title, String content, String tags);

    /**
     * 2. 仅按分类精准查询 (当用户没有输入关键字，只点了左侧菜单时触发)
     */
    List<NoteDoc> findByCategoryId(Long categoryId);
}