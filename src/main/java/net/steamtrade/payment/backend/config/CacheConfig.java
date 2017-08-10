package net.steamtrade.payment.backend.config;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sasha on 23.06.17.
 */
@Configuration
public class CacheConfig {

    @Bean
    public Config hazelcastConfig() {
        Config config = new Config().setInstanceName("hazelcast-instance");

        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setPort(5701).setPortAutoIncrement(false).setJoin(
                new JoinConfig()
                        .setMulticastConfig(new MulticastConfig()
                                .setEnabled(false))
                        .setTcpIpConfig(new TcpIpConfig()
                                .setEnabled(true)
                                .addMember("127.0.0.1")));
        config.setNetworkConfig(networkConfig);
        return config;
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance instance) {
        return new HazelcastCacheManager(instance);
    }

}
