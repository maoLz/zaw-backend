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
@Table(name = "game_chapter_item")
@Getter
@Setter
public class GameChapterItem extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private GameChapter chapter;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 64)
    private String type;

    @Column(name = "role_in_chapter", columnDefinition = "TEXT")
    private String roleInChapter;

    @Column(name = "state_change", length = 128)
    private String stateChange;
}
