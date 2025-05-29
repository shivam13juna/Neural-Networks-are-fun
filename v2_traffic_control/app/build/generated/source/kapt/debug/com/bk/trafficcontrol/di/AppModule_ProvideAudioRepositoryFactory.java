package com.bk.trafficcontrol.di;

import com.bk.trafficcontrol.data.db.dao.PlaylistDao;
import com.bk.trafficcontrol.data.db.dao.ScheduleDao;
import com.bk.trafficcontrol.data.db.dao.TrackDao;
import com.bk.trafficcontrol.domain.repository.AudioRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AppModule_ProvideAudioRepositoryFactory implements Factory<AudioRepository> {
  private final Provider<PlaylistDao> playlistDaoProvider;

  private final Provider<TrackDao> trackDaoProvider;

  private final Provider<ScheduleDao> scheduleDaoProvider;

  public AppModule_ProvideAudioRepositoryFactory(Provider<PlaylistDao> playlistDaoProvider,
      Provider<TrackDao> trackDaoProvider, Provider<ScheduleDao> scheduleDaoProvider) {
    this.playlistDaoProvider = playlistDaoProvider;
    this.trackDaoProvider = trackDaoProvider;
    this.scheduleDaoProvider = scheduleDaoProvider;
  }

  @Override
  public AudioRepository get() {
    return provideAudioRepository(playlistDaoProvider.get(), trackDaoProvider.get(), scheduleDaoProvider.get());
  }

  public static AppModule_ProvideAudioRepositoryFactory create(
      Provider<PlaylistDao> playlistDaoProvider, Provider<TrackDao> trackDaoProvider,
      Provider<ScheduleDao> scheduleDaoProvider) {
    return new AppModule_ProvideAudioRepositoryFactory(playlistDaoProvider, trackDaoProvider, scheduleDaoProvider);
  }

  public static AudioRepository provideAudioRepository(PlaylistDao playlistDao, TrackDao trackDao,
      ScheduleDao scheduleDao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAudioRepository(playlistDao, trackDao, scheduleDao));
  }
}
