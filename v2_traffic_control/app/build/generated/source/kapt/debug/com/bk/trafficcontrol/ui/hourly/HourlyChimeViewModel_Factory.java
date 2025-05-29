package com.bk.trafficcontrol.ui.hourly;

import com.bk.trafficcontrol.domain.repository.AudioRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class HourlyChimeViewModel_Factory implements Factory<HourlyChimeViewModel> {
  private final Provider<AudioRepository> repositoryProvider;

  public HourlyChimeViewModel_Factory(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public HourlyChimeViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static HourlyChimeViewModel_Factory create(Provider<AudioRepository> repositoryProvider) {
    return new HourlyChimeViewModel_Factory(repositoryProvider);
  }

  public static HourlyChimeViewModel newInstance(AudioRepository repository) {
    return new HourlyChimeViewModel(repository);
  }
}
