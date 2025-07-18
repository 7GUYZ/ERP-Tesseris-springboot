package com.jakdang.labs.api.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FileEnum {
    image("image/"),
    file("file/"),
    video("video/"),
    audio("audio/");

    private final String filePath;

    public static FileEnum fromContentType(String contentType) {
        if (contentType == null) {
            return file;
        }
        return Arrays.stream(values())
                .filter(fileEnum ->  contentType.startsWith(fileEnum.getFilePath()))
                .findFirst()
                .orElse(file);
    }
}
