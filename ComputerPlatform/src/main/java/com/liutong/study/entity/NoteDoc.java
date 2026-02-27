package com.liutong.study.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "note_index") // 索引名字，相当于数据库的表名
public class NoteDoc {

    @Id
    private Long id; // 对应笔记 ID

    // analyzer = "ik_max_word" 需要安装 IK 分词器插件
    // 如果你没装插件，为了不报错，我们先暂时不写 analyzer，用默认分词
    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content; // 笔记内容

    @Field(type = FieldType.Keyword)
    private String authorName;

    @Field(type = FieldType.Keyword)
    private String authorAvatar;

    @Field(type = FieldType.Long)
    private Long createTime;
}