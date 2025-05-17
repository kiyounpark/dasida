package com.bonju.review.image.storage.provider;

import com.bonju.review.image.storage.objectkey.ObjectKey;

public interface ImagePublicUrlProvider {

  String getPublicUrl(ObjectKey objectKey);
}
