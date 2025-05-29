package com.bk.trafficcontrol.domain.usecase;

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
public final class ScheduleTrackUseCase_Factory implements Factory<ScheduleTrackUseCase> {
  private final Provider<AudioRepository> repositoryProvider;

  public ScheduleTrackUseCase_Factory(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ScheduleTrackUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ScheduleTrackUseCase_Factory create(Provider<AudioRepository> repositoryProvider) {
    return new ScheduleTrackUseCase_Factory(repositoryProvider);
  }

  public static ScheduleTrackUseCase newInstance(AudioRepository repository) {
    return new ScheduleTrackUseCase(repository);
  }
}
