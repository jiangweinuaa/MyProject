package com.dsc.spos.service;

/**
 *  用來執行 execute 之後的工作, 直接與 Runnable 共用.
 * @author Xavier
 *
 */
public abstract class AfterExecuteRunner implements Runnable {
	
	@Override
	public void run() {
		try {
			this.afterExecute();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected abstract void afterExecute() throws Exception;
}
