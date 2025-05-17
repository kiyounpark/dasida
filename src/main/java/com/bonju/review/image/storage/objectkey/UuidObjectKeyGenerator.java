package com.bonju.review.image.storage.objectkey;

import com.bonju.review.util.FileExtensionExtractor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidObjectKeyGenerator implements ObjectKeyGenerator {

  public static final String IMAGE_DIRECTORY_PREFIX = "images/";

  @Override
  public String generate(String originalFilename) {
    String uuid = UUID.randomUUID().toString();
    String extract = FileExtensionExtractor.extract(originalFilename);

    return IMAGE_DIRECTORY_PREFIX + uuid + extract;
  }
}
