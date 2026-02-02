package com.zaw.game.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zaw.game.entity.GameChapter;
import com.zaw.game.entity.GameChapterCharacter;
import com.zaw.game.entity.GameChapterEvent;
import com.zaw.game.entity.GameChapterHiddenInfo;
import com.zaw.game.entity.GameChapterItem;
import com.zaw.game.entity.GameChapterKeyword;
import com.zaw.game.entity.GameChapterPlot;
import com.zaw.game.repository.GameChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameChapterService {

    private final GameChapterRepository chapterRepository;

    @Transactional
    public GameChapter saveFromContent(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        String normalized = content.replace("```json", "")
                .replace("```", "")
                .trim();

        JSONObject root;
        try {
            root = JSONObject.parseObject(normalized);
        } catch (Exception e) {
            return null;
        }

        GameChapter chapter = new GameChapter();
        chapter.setSummary(root.getString("summary"));

        JSONObject timeAndPlace = root.getJSONObject("time_and_place");
        if (timeAndPlace != null) {
            chapter.setTimeLabel(timeAndPlace.getString("time"));
            chapter.setPlaceLabel(timeAndPlace.getString("place"));
        }

        JSONArray keywords = root.getJSONArray("keywords");
        if (keywords != null) {
            for (int i = 0; i < keywords.size(); i++) {
                String keyword = keywords.getString(i);
                if (keyword == null || keyword.isBlank()) {
                    continue;
                }
                GameChapterKeyword entity = new GameChapterKeyword();
                entity.setKeyword(keyword);
                entity.setChapter(chapter);
                chapter.getKeywords().add(entity);
            }
        }

        JSONArray plots = root.getJSONArray("detailed_plot");
        if (plots != null) {
            for (int i = 0; i < plots.size(); i++) {
                String plotText = plots.getString(i);
                if (plotText == null || plotText.isBlank()) {
                    continue;
                }
                GameChapterPlot entity = new GameChapterPlot();
                entity.setPlotText(plotText);
                entity.setChapter(chapter);
                chapter.getDetailedPlots().add(entity);
            }
        }

        JSONObject entities = root.getJSONObject("entities");
        if (entities != null) {
            JSONArray characters = entities.getJSONArray("characters");
            if (characters != null) {
                for (int i = 0; i < characters.size(); i++) {
                    JSONObject character = characters.getJSONObject(i);
                    if (character == null) {
                        continue;
                    }
                    GameChapterCharacter entity = new GameChapterCharacter();
                    entity.setName(character.getString("name"));
                    entity.setIdentityOrFaction(character.getString("identity_or_faction"));
                    entity.setActionsInChapter(character.getString("actions_in_chapter"));
                    entity.setStateChange(character.getString("state_change"));
                    entity.setChapter(chapter);
                    chapter.getCharacters().add(entity);
                }
            }

            JSONArray events = entities.getJSONArray("events");
            if (events != null) {
                for (int i = 0; i < events.size(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    if (event == null) {
                        continue;
                    }
                    GameChapterEvent entity = new GameChapterEvent();
                    entity.setEventName(event.getString("event_name"));
                    entity.setDescription(event.getString("description"));
                    entity.setImmediateResult(event.getString("immediate_result"));
                    entity.setChapter(chapter);
                    chapter.getEvents().add(entity);
                }
            }

            JSONArray items = entities.getJSONArray("items");
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    if (item == null) {
                        continue;
                    }
                    GameChapterItem entity = new GameChapterItem();
                    entity.setName(item.getString("name"));
                    entity.setType(item.getString("type"));
                    entity.setRoleInChapter(item.getString("role_in_chapter"));
                    entity.setStateChange(item.getString("state_change"));
                    entity.setChapter(chapter);
                    chapter.getItems().add(entity);
                }
            }
        }

        JSONArray hiddenInfo = root.getJSONArray("hidden_information");
        if (hiddenInfo != null) {
            for (int i = 0; i < hiddenInfo.size(); i++) {
                String text = hiddenInfo.getString(i);
                if (text == null || text.isBlank()) {
                    JSONObject obj = hiddenInfo.getJSONObject(i);
                    if (obj != null) {
                        text = obj.getString("text");
                        if (text == null || text.isBlank()) {
                            text = obj.getString("content");
                        }
                        if (text == null || text.isBlank()) {
                            text = obj.getString("hidden_text");
                        }
                    }
                }
                if (text == null || text.isBlank()) {
                    continue;
                }
                GameChapterHiddenInfo entity = new GameChapterHiddenInfo();
                entity.setHiddenText(text);
                entity.setChapter(chapter);
                chapter.getHiddenInformation().add(entity);
            }
        }

        return chapterRepository.save(chapter);
    }
}
