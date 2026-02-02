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
@Table(name = "game_chapter_event")
@Getter
@Setter
public class GameChapterEvent extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private GameChapter chapter;

    @Column(name = "event_name", nullable = false, length = 128)
    private String eventName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "immediate_result", columnDefinition = "TEXT")
    private String immediateResult;
}
