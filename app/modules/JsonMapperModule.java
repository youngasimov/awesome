package modules;

import com.google.inject.AbstractModule;

public class JsonMapperModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JsonMapper.class).asEagerSingleton();
    }
}
