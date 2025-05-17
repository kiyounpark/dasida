package com.bonju.review.util;

public class FileExtensionExtractor {

  private FileExtensionExtractor() {}

  public static String extract(String originalFilename) {
    int index = originalFilename.lastIndexOf('.');
    return originalFilename.substring(index);
  }
}