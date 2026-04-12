package com.dsc.spos.progress;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.JsonBasicReq;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 进度服务类
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"type", "progressName"})
public abstract class ProgressService<T extends JsonBasicReq> implements Runnable, Callable<String> {

    @Getter
    public enum ProgressType {

        ProgressType_A("A", "库存关帐"),
        ProgressType_B("B", "库存月结"),
        ProgressType_C("C", "低阶码计算"),
        ProgressType_D("D", "成本阶计算"),
        ProgressType_E("E", "工单自动结案"),
        ProgressType_F("F", "工单返工自动设置作业"),
        ProgressType_G("G", "工单生成报工统计"),
        ProgressType_H("H", "工时费用统计表"),
        ProgressType_I("I", "工时费用明细表"),
        ProgressType_J("J", "成本计算"),
        ProgressType_K("K", "期初成本开帐"),
        ProgressType_L("L", "杂项入库维护"),
        ProgressType_M("M", "本期成本调整单"),
        ProgressType_N("N", "调拨成本维护");

        final String type;
        final String name;

        ProgressType(String type, String name) {
            this.name = name;
            this.type = type;
        }

        public ProgressType getProgressType(String type) {
            for (ProgressType progressType : ProgressType.values()) {
                if (progressType.type.equals(type)) {
                    return progressType;
                }
            }
            return null;
        }

    }

    @Getter
    public enum ProgressState {

        PROGRESS_READY(0),
        PROGRESS_RUNNING(1),
        PROGRESS_DONE(100),
        PROGRESS_FAILED(-1);

        final int state;

        ProgressState(int state) {
            this.state = state;
        }
    }

    public ProgressService(T req) {
        this.req = req;
    }

    public abstract void runProgress() throws Exception;

    public abstract void beforeRun();

    public abstract void afterRun();

    //等待返回结果的调用这个
    public String call() {
        String result = "";
        try {
            this.setProgressState(ProgressState.PROGRESS_READY);
            startTime = System.currentTimeMillis();
            this.beforeRun();
            this.setProgressState(ProgressState.PROGRESS_RUNNING);
            this.runProgress();
            this.afterRun();
            this.setProgressState(ProgressState.PROGRESS_DONE);
            endTime = System.currentTimeMillis();
        } catch (Exception e) {
            this.setProgressState(ProgressState.PROGRESS_FAILED);
            setStepDescription(e.getMessage());
            result = e.getMessage();
        }
        return result;
    }

    //无需结果的多线程执行调用这个
    public void run() {
        try {
            this.setProgressState(ProgressState.PROGRESS_READY);
            startTime = System.currentTimeMillis();
            this.beforeRun();
            this.setProgressState(ProgressState.PROGRESS_RUNNING);
            this.runProgress();
            this.afterRun();
            this.setProgressState(ProgressState.PROGRESS_DONE);
            endTime = System.currentTimeMillis();
        } catch (Exception e) {
            this.setProgressState(ProgressState.PROGRESS_FAILED);
            setStepDescription(e.getMessage());
        }
    }

    private T req;

    private ProgressType type;
    private String progressName;
    private int maxStep;
    private int nowStep;
    private String stepDescription;
    private long startTime;
    private long endTime;

    private ProgressState progressState;
    private List<DataProcessBean> pData = new ArrayList<>();

    /*
     * 推荐使用incStep(String stepDescription)
     */
    public void incStep() {
        nowStep++;
    }

    public void incStep(String stepDescription) {
        nowStep++;
        setStepDescription(stepDescription);
    }

    public boolean finished() {
        return this.progressState == ProgressState.PROGRESS_DONE
                || this.progressState == ProgressState.PROGRESS_FAILED;
    }

    public boolean isFailed() {
        return this.progressState == ProgressState.PROGRESS_FAILED;
    }

    //设置名称时增加当前进度类名
//    public void setProgressName(String progressName) {
//        this.progressName = this.getClass().getName() + "." + progressName;
//    }

    public void addProcessBean(DataProcessBean dataProcessBean) {
        this.pData.add(dataProcessBean);
    }

}
