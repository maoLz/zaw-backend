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
@Table(name = "game_chapter_character")
@Getter
@Setter
public class GameChapterCharacter extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private GameChapter chapter;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "identity_or_faction", length = 128)
    private String identityOrFaction;

    @Column(name = "actions_in_chapter", columnDefinition = "TEXT")
    private String actionsInChapter;

    @Column(name = "state_change", length = 128)
    private String stateChange;
}
