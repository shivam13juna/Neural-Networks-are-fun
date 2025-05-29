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
public final class UpdateScheduleUseCase_Factory implements Factory<UpdateScheduleUseCase> {
  private final Provider<AudioRepository> repositoryProvider;

  public UpdateScheduleUseCase_Factory(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateScheduleUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateScheduleUseCase_Factory create(Provider<AudioRepository> repositoryProvider) {
    return new UpdateScheduleUseCase_Factory(repositoryProvider);
  }

  public static UpdateScheduleUseCase newInstance(AudioRepository repository) {
    return new UpdateScheduleUseCase(repository);
  }
}
