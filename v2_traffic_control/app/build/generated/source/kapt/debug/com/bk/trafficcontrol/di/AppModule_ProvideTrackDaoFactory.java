package com.bk.trafficcontrol.di;

import com.bk.trafficcontrol.data.db.AppDatabase;
import com.bk.trafficcontrol.data.db.dao.TrackDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideTrackDaoFactory implements Factory<TrackDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideTrackDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public TrackDao get() {
    return provideTrackDao(databaseProvider.get());
  }

  public static AppModule_ProvideTrackDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideTrackDaoFactory(databaseProvider);
  }

  public static TrackDao provideTrackDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTrackDao(database));
  }
}
