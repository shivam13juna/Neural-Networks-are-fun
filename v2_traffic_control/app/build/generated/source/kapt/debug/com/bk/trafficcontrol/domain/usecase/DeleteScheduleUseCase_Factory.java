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
public final class DeleteScheduleUseCase_Factory implements Factory<DeleteScheduleUseCase> {
  private final Provider<AudioRepository> repositoryProvider;

  public DeleteScheduleUseCase_Factory(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DeleteScheduleUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static DeleteScheduleUseCase_Factory create(Provider<AudioRepository> repositoryProvider) {
    return new DeleteScheduleUseCase_Factory(repositoryProvider);
  }

  public static DeleteScheduleUseCase newInstance(AudioRepository repository) {
    return new DeleteScheduleUseCase(repository);
  }
}
