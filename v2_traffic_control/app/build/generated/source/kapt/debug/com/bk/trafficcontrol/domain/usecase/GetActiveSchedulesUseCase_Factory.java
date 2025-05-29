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
public final class GetActiveSchedulesUseCase_Factory implements Factory<GetActiveSchedulesUseCase> {
  private final Provider<AudioRepository> repositoryProvider;

  public GetActiveSchedulesUseCase_Factory(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetActiveSchedulesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetActiveSchedulesUseCase_Factory create(
      Provider<AudioRepository> repositoryProvider) {
    return new GetActiveSchedulesUseCase_Factory(repositoryProvider);
  }

  public static GetActiveSchedulesUseCase newInstance(AudioRepository repository) {
    return new GetActiveSchedulesUseCase(repository);
  }
}
