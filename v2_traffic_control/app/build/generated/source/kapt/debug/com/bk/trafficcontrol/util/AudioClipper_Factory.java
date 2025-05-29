package com.bk.trafficcontrol.util;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AudioClipper_Factory implements Factory<AudioClipper> {
  @Override
  public AudioClipper get() {
    return newInstance();
  }

  public static AudioClipper_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioClipper newInstance() {
    return new AudioClipper();
  }

  private static final class InstanceHolder {
    private static final AudioClipper_Factory INSTANCE = new AudioClipper_Factory();
  }
}
