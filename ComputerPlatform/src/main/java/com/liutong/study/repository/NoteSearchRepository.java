package com.liutong.study.repository;

import com.liutong.study.entity.NoteDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteSearchRepository extends ElasticsearchRepository<NoteDoc, Long> {

    /**
     * 🟢 改回标准匹配
     * 不要用 Containing 了，直接用 TitleOrContent。
     * 这会自动生成 Match Query，它知道怎么处理中文分词。
     */
    List<NoteDoc> findByTitleOrContent(String title, String content);
}