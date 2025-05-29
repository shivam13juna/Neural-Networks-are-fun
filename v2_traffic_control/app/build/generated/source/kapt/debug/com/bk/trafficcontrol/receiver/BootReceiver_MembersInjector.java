package com.bk.trafficcontrol.receiver;

import com.bk.trafficcontrol.domain.repository.AudioRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<AudioRepository> repositoryProvider;

  public BootReceiver_MembersInjector(Provider<AudioRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<BootReceiver> create(Provider<AudioRepository> repositoryProvider) {
    return new BootReceiver_MembersInjector(repositoryProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.bk.trafficcontrol.receiver.BootReceiver.repository")
  public static void injectRepository(BootReceiver instance, AudioRepository repository) {
    instance.repository = repository;
  }
}
