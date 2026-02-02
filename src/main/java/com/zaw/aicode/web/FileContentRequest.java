package com.zaw.aicode.web;

import lombok.Data;

import java.util.List;

@Data
public class FileContentRequest {

    private String requestStr;

    public static class Request {
        private String projectPath;
        private List<String> relativePaths;

        public String getProjectPath() {
            return projectPath;
        }

        public void setProjectPath(String projectPath) {
            this.projectPath = projectPath;
        }

        public List<String> getRelativePaths() {
            return relativePaths;
        }

        public void setRelativePaths(List<String> relativePaths) {
            this.relativePaths = relativePaths;
        }
    }
}
