package com.bk.trafficcontrol.scheduler;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.bk.trafficcontrol.domain.repository.AudioRepository;
import dagger.internal.DaggerGenerated;
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
public final class ScheduleWorker_Factory {
  private final Provider<AudioRepository> repositoryProvider;

  public ScheduleWorker_Factory(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public ScheduleWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, repositoryProvider.get());
  }

  public static ScheduleWorker_Factory create(Provider<AudioRepository> repositoryProvider) {
    return new ScheduleWorker_Factory(repositoryProvider);
  }

  public static ScheduleWorker newInstance(Context context, WorkerParameters params,
      AudioRepository repository) {
    return new ScheduleWorker(context, params, repository);
  }
}
