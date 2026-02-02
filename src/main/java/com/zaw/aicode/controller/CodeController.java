package com.zaw.aicode.controller;



import com.zaw.aicode.dto.AskRequest;
import com.zaw.aicode.dto.FileAddRequest;
import com.zaw.aicode.dto.FilePatchDTO;
import com.zaw.aicode.dto.FilePatchRequest;
import com.zaw.aicode.tool.*;
import com.zaw.aicode.web.FileContentRequest;
import com.zaw.aicode.web.FileNode;
import com.zaw.aicode.web.FileTreeRequest;
import com.zaw.common.web.R;

import org.aspectj.util.FileUtil;
import org.springframework.validation.annotation.Validated;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/2
 */
@RestController
@RequestMapping("/code")
@Validated
public class CodeController {


    @PostMapping("/tree")
    public R<String> getFileTree( @RequestBody FileTreeRequest request) {
        return R.ok(JSON.toJSONString(DirectoryScanner.scan(request.getPath(), request.getNameFilters())));
    }

    @PostMapping("askMock")
    public JSONObject askMock() throws Exception {
        String mockFilePath = "/Users/zhanglizhong/personalProjects/zaw-project/zaw/.doc/mock.json";
        String content = FileUtil.readAsString(new File(mockFilePath));
        return JSONObject.parseObject(content);
    }
    

    @PostMapping("/content")
    public R<String> getFileContents(
            @RequestBody FileContentRequest request) {
        FileContentRequest.Request requestObj = JSON.parseObject(request.getRequestStr(), FileContentRequest.Request.class);
        return R.ok(JSON.toJSONString(FileContentService.readFiles(
                requestObj.getProjectPath(),
                requestObj.getRelativePaths()
        )));
    }

    @PostMapping("/contentByFilter")
    public R<String> contentByFilter(
            @RequestBody FileTreeRequest request) {
        FileNode fileNode = DirectoryScanner.scan(request.getPath(), request.getNameFilters());
        List<String> result = new ArrayList<>();
        collect(fileNode, result);
        return R.ok(JSON.toJSONString(FileContentService.readFiles(
                request.getPath(),
                result
        )));
    }

    @PostMapping("/ask")
    public R<JSONObject> ask(@RequestBody AskRequest request) {
        return R.ok(DeepSeekClient.send(request.getMessages()));
    }
    @PostMapping("/applyPatches")
    public R<String> applyPatches(@RequestBody FilePatchRequest request) {
        System.out.println(request.getPatchStr().replace("\\n","\n"));
        List<FilePatchDTO> patchDTOS = JSONArray.parseArray(request.getPatchStr(), FilePatchDTO.class);
        try {
            FilePatchService.applyAll(Path.of(request.getProjectPath()), patchDTOS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.ok("success");
    }

    @PostMapping("/addFile")
    public R<String> postMethodName(@RequestBody FileAddRequest request) {
        if (request == null || request.getBasePath() == null || request.getBasePath().isBlank()) {
            return R.fail("basePath is required");
        }
        List<FileAddRequest.FileAddPatch> patches = request.getPatchDTOS();
        if ((patches == null || patches.isEmpty()) && request.getPatchStr() != null && !request.getPatchStr().isBlank()) {
            patches = JSONArray.parseArray(request.getPatchStr(), FileAddRequest.FileAddPatch.class);
        }
        if (patches == null || patches.isEmpty()) {
            return R.fail("patchDTOS is required");
        }
        try {
            Path basePath = Path.of(request.getBasePath());
            for (FileAddRequest.FileAddPatch patch : patches) {
                if (patch == null || patch.getFileName() == null || patch.getFileName().isBlank()) {
                    return R.fail("patch name is required");
                }
                Path filePath = basePath.resolve(patch.getFileName());
                Path parent = filePath.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                String content = patch.getContent() == null ? "" : patch.getContent();
                Files.writeString(filePath, content, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("addFile failed: " + e.getMessage());
        }
        return R.ok("success");
    }
    

    private static void collect(FileNode node, List<String> result) {
    if (node == null) {
        return;
    }

    if ("file".equals(node.getType())) {
        result.add(node.getPath());
        return;
    }

    if (node.getChildren() != null) {
        for (FileNode child : node.getChildren()) {
            collect(child, result);
        }
    }
}

}
