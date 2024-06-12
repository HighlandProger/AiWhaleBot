package ru.rusguardian.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() {
            @Override
            public void execute(Runnable task) {
                logThreadPoolStatus("Before Execute");
                super.execute(task);
                logThreadPoolStatus("After Execute");
            }

            @Override
            public Future<?> submit(Runnable task) {
                logThreadPoolStatus("Before Submit");
                Future<?> future = super.submit(task);
                logThreadPoolStatus("After Submit");
                return future;
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                logThreadPoolStatus("Before Submit");
                Future<T> future = super.submit(task);
                logThreadPoolStatus("After Submit");
                return future;
            }

            private void logThreadPoolStatus(String phase) {
                log.info("ThreadPool Status - {}: Active Threads: {}, Pool Size: {}, Queue Size: {}",
                        phase, this.getActiveCount(), this.getPoolSize(), this.getThreadPoolExecutor().getQueue().size());
            }
        };

        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();

        return executor;
    }
}
