package com.bk.trafficcontrol.data.repository;

import com.bk.trafficcontrol.data.db.dao.PlaylistDao;
import com.bk.trafficcontrol.data.db.dao.ScheduleDao;
import com.bk.trafficcontrol.data.db.dao.TrackDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AudioRepositoryImpl_Factory implements Factory<AudioRepositoryImpl> {
  private final Provider<PlaylistDao> playlistDaoProvider;

  private final Provider<TrackDao> trackDaoProvider;

  private final Provider<ScheduleDao> scheduleDaoProvider;

  public AudioRepositoryImpl_Factory(Provider<PlaylistDao> playlistDaoProvider,
      Provider<TrackDao> trackDaoProvider, Provider<ScheduleDao> scheduleDaoProvider) {
    this.playlistDaoProvider = playlistDaoProvider;
    this.trackDaoProvider = trackDaoProvider;
    this.scheduleDaoProvider = scheduleDaoProvider;
  }

  @Override
  public AudioRepositoryImpl get() {
    return newInstance(playlistDaoProvider.get(), trackDaoProvider.get(), scheduleDaoProvider.get());
  }

  public static AudioRepositoryImpl_Factory create(Provider<PlaylistDao> playlistDaoProvider,
      Provider<TrackDao> trackDaoProvider, Provider<ScheduleDao> scheduleDaoProvider) {
    return new AudioRepositoryImpl_Factory(playlistDaoProvider, trackDaoProvider, scheduleDaoProvider);
  }

  public static AudioRepositoryImpl newInstance(PlaylistDao playlistDao, TrackDao trackDao,
      ScheduleDao scheduleDao) {
    return new AudioRepositoryImpl(playlistDao, trackDao, scheduleDao);
  }
}
