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
@Table(name = "game_chapter_plot")
@Getter
@Setter
public class GameChapterPlot extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private GameChapter chapter;

    @Column(name = "plot_text", columnDefinition = "TEXT")
    private String plotText;
}
