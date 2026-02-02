package com.zaw.aicode.tool;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import com.zaw.aicode.web.FileNode;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DirectoryScanner {

    public static FileNode scan(String rootPath, List<String> filters) {
        rootPath = URLDecoder.decode(rootPath);
        File root = new File(rootPath);
        if (!root.exists()) {
            throw new IllegalArgumentException("路径不存在: " + rootPath);
        }
        return buildTree(root, rootPath, filters);
    }
    
    public static FileNode scan(String rootPath, String filter) {
        List<String> filters = null;
        if (filter != null && !filter.isEmpty()) {
            filters = List.of(filter);
        }
        return scan(rootPath, filters);
    }

    private static FileNode buildTree(File file, String rootPath, List<String> filters) {
        FileNode node = new FileNode(
                file.getName(),
                file.getAbsolutePath().replace(rootPath, "."),
                file.isDirectory() ? "directory" : "file"
        );
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if(child.isFile() && !child.getName().endsWith(".java")){
                        continue;
                    }
                    if (child.isFile() && filters != null && !filters.isEmpty()) {
                        boolean matches = true;
                        for (String filter : filters) {
                            if (!isFileMatchFilter(child.getName(), filter)) {
                                matches = false;
                                break;
                            }
                        }
                        if (!matches) {
                            continue;
                        }
                    }
                    FileNode childNode = buildTree(child, rootPath, filters);
                    if(childNode != null){
                        node.addChild(childNode);
                    }
                }
            }
            if(CollectionUtil.isEmpty(node.getChildren())){
                return null;
            }
        }
        return node;
    }
    
    /**
     * 检查文件名是否匹配过滤条件
     * 支持类似SQL LIKE的模糊匹配，如 %device% 或 %.java
     */
    private static boolean isFileMatchFilter(String fileName, String filter) {
        if (filter == null || fileName == null) {
            return true;
        }
        
        // 如果过滤器包含通配符 % 或 _，则转换为正则表达式进行匹配
        if (filter.contains("%") || filter.contains("_")) {
            String regex = convertLikeToRegex(filter);
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(fileName).matches();
        } else {
            // 普通包含匹配（不区分大小写）
            return fileName.toLowerCase().contains(filter.toLowerCase());
        }
    }
    
    /**
     * 将SQL LIKE语法转换为正则表达式
     * % 匹配任意字符序列
     * _ 匹配单个字符
     */
    private static String convertLikeToRegex(String likePattern) {
        StringBuilder sb = new StringBuilder();
        sb.append(".*"); // 开始匹配任意字符
        
        for (int i = 0; i < likePattern.length(); i++) {
            char c = likePattern.charAt(i);
            switch (c) {
                case '%':
                    sb.append(".*"); // % 匹配任意字符序列
                    break;
                case '_':
                    sb.append("."); // _ 匹配单个字符
                    break;
                case '*':
                    sb.append("\\*"); // 转义特殊字符
                    break;
                case '.':
                    sb.append("\\."); // 转义特殊字符
                    break;
                case '?':
                    sb.append("\\?"); // 转义特殊字符
                    break;
                case '+':
                    sb.append("\\+"); // 转义特殊字符
                    break;
                case '^':
                    sb.append("\\^"); // 转义特殊字符
                    break;
                case '$':
                    sb.append("\\$"); // 转义特殊字符
                    break;
                case '[':
                    sb.append("\\["); // 转义特殊字符
                    break;
                case ']':
                    sb.append("\\]"); // 转义特殊字符
                    break;
                case '(':
                    sb.append("\\("); // 转义特殊字符
                    break;
                case ')':
                    sb.append("\\)"); // 转义特殊字符
                    break;
                case '{':
                    sb.append("\\{"); // 转义特殊字符
                    break;
                case '}':
                    sb.append("\\}"); // 转义特殊字符
                    break;
                case '|':
                    sb.append("\\|"); // 转义特殊字符
                    break;
                case '\\':
                    sb.append("\\\\"); // 转义特殊字符
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        
        sb.append(".*"); // 结束匹配任意字符
        return sb.toString();
    }
}