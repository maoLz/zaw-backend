package com.zaw.aicode.tool;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.zaw.aicode.dto.FilePatchDTO;
import com.zaw.aicode.dto.FilePatchRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

@Slf4j
public class FilePatchService {

    public static void applyFilePatch(Path projectRoot, FilePatchDTO patch) throws Exception {
        Path filePath = projectRoot.resolve(patch.getPath());
        log.info("ApplyFile path:{},isNew:{}", filePath.toString(), patch.isNew());
        if (patch.isNew() || patch.getEdits().get(0).getType().equals("new_file")) {
            FileUtil.writeString(patch.getEdits().get(0).getContent(), filePath.toString(), StandardCharsets.UTF_8);
            return;
        }

        String original = Files.readString(filePath);

        String modified;
        try {
            modified = PatchApplier.applyEdits(original, patch.getEdits());
        } catch (Exception e) {
            // 失败直接回滚
            throw new RuntimeException("Patch failed for " + patch.getPath(), e);
        }

        Files.writeString(filePath, modified);
    }

    public static void applyAll(Path projectRoot, List<FilePatchDTO> patches) throws Exception {
        for (FilePatchDTO patch : patches) {
            applyFilePatch(projectRoot, patch);
        }
    }

    // public static void main(String[] args) {
    //     System.out.println(new Date(1798799568446L));

    //     String patchStr = FileUtil.readString("/Users/zhanglizhong/personalProjects/zaw-project/zaw/.temp/temp.json",
    //             StandardCharsets.UTF_8);
    //     FilePatchRequest patch = JSONObject.parseObject(patchStr, FilePatchRequest.class);
    //     List<FilePatchDTO> patchDTOS = patch.getPatchDTOS();
    //     if (CollectionUtil.isEmpty(patchDTOS)) {
    //         patchDTOS = JSONArray.parseArray(patch.getPatchStr(), FilePatchDTO.class);
    //     }
    //     try {
    //         applyAll(Path.of(patch.getProjectPath()), patchDTOS);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
