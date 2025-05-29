package com.bk.trafficcontrol.di;

import com.bk.trafficcontrol.data.db.AppDatabase;
import com.bk.trafficcontrol.data.db.dao.ScheduleDao;
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
public final class AppModule_ProvideScheduleDaoFactory implements Factory<ScheduleDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideScheduleDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ScheduleDao get() {
    return provideScheduleDao(databaseProvider.get());
  }

  public static AppModule_ProvideScheduleDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideScheduleDaoFactory(databaseProvider);
  }

  public static ScheduleDao provideScheduleDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideScheduleDao(database));
  }
}
