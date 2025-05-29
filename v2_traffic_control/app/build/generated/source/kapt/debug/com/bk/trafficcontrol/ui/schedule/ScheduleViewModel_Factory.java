package com.bk.trafficcontrol.ui.schedule;

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
public final class ScheduleViewModel_Factory implements Factory<ScheduleViewModel> {
  private final Provider<AudioRepository> repositoryProvider;

  public ScheduleViewModel_Factory(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ScheduleViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ScheduleViewModel_Factory create(Provider<AudioRepository> repositoryProvider) {
    return new ScheduleViewModel_Factory(repositoryProvider);
  }

  public static ScheduleViewModel newInstance(AudioRepository repository) {
    return new ScheduleViewModel(repository);
  }
}
