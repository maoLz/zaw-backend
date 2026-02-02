package com.zaw.game.entity;

import com.zaw.common.entity.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "game_chapter_hidden_info")
@Getter
@Setter
public class GameChapterHiddenInfo extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private GameChapter chapter;

    @Column(name = "hidden_text", columnDefinition = "TEXT")
    private String hiddenText;
}
