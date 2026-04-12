package com.dsc.spos.progress;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.*;

public class ProgressServiceFactory {

    @Getter
    @Setter
    /*
      任务完成后 间隔多少秒清除该执行任务，防止任务列表过多占用
     */
    private long removeProgressInterval = 15 * 60 * 1000;

    private static final int CORE_POOL_SIZE = 0;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int QUEUE_CAPACITY = 50;
    private static final long KEEP_ALIVE = 60 * 1000L;

    private static volatile ProgressServiceFactory factory;
    private final ThreadPoolExecutor executor;
    @Getter
    @Setter
    private volatile List<ProgressService<?>> progressServices;

    private ProgressServiceFactory() {
        executor =
                new ThreadPoolExecutor(
                        CORE_POOL_SIZE,
                        MAXIMUM_POOL_SIZE,
                        KEEP_ALIVE,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                        new ThreadPoolExecutor.CallerRunsPolicy() {
                        }
                );

        progressServices = Lists.newArrayList();

    }

    /**
     * 静态工厂方法 获取当前对象实例 多线程安全单例模式(使用双重同步锁)
     */
    public static synchronized ProgressServiceFactory getInstance() {
        if (factory == null) {
            synchronized (ThreadPoolExecutor.class) {
                if (factory == null) {
                    try {
                        factory = new ProgressServiceFactory();
                    } catch (Exception e) {
                        factory = null;
                    }
                }
            }
        }
        return factory;
    }

    public Future<String> submitNewProgress(ProgressService<?> progressService) throws Exception {
        if (progressService == null) {
            return null;
        }

        if (progressServices.contains(progressService)) {
            progressService = progressServices.get(progressServices.indexOf(progressService));
            if (!progressService.finished()) {
                throw new Exception("当前任务已提交运行！请勿重复提交！");
            }
        }


        progressServices.add(progressService);
        return executor.submit((Callable<String>) progressService);
    }

    public void startNewProgress(ProgressService<?> progressService) throws Exception {
//        removeFinishedProgress();//每次新增任务时删除过期已完成任务

        if (progressService == null) {
            return;
        }

        if (progressServices.contains(progressService)) {
            progressService = progressServices.get(progressServices.indexOf(progressService));
            if (!progressService.finished()) {
                throw new Exception("当前任务已提交运行！请勿重复提交！");
            }
        }


        progressServices.add(progressService);
        executor.execute(progressService);

    }

    public ProgressService<?> getProgress(String type, String progressName) {

        for (ProgressService<?> progressService : progressServices) {
            if (progressService.getProgressName().equals(progressName)
                    && progressService.getType().getType().equals(type)
            ) {
                return progressService;
            }
        }
        return null;
    }

    public ProgressService<?> getProgress(String progressName) {
        for (ProgressService<?> progressService : progressServices) {
            if (progressName.equals(progressService.getProgressName())) {
                return progressService;
            }
        }
        return null;
    }

    public List<ProgressService<?>> getProgressListByType(String type) {
        List<ProgressService<?>> list = Lists.newArrayList();

        for (ProgressService<?> progressService : progressServices) {
            if (progressService.getType().getType().equals(type)) {
                list.add(progressService);
            }
        }
        return list;
    }


    @Deprecated
    public void removeFinishedProgress() {
        if (!progressServices.isEmpty()) {
            progressServices.removeIf(progressService -> progressService.finished()
                    && progressService.getEndTime() >= System.currentTimeMillis() - getRemoveProgressInterval());

            progressServices.removeIf(ProgressService::isFailed);

        }
    }

}
