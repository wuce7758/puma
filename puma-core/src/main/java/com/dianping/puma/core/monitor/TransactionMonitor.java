package com.dianping.puma.core.monitor;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TransactionMonitor extends AbstractMonitor {

	private ConcurrentMap<String, Transaction> transactions = new ConcurrentHashMap<String, Transaction>();

	public void recordBegin(String name) {
		if (!isStopped()) {
			startCountingIfNeeded(name);
			incrCountingIfExists(name);
			if (checkCountingIfExists(name)) {
				transactions.put(name, Cat.newTransaction(getType(), name));
			}
		}
	}

	public void recordEnd(String name) {
		if (!isStopped()) {
			if (checkCountingIfExists(name)) {
				resetCountingIfExists(name);
				Transaction transaction = transactions.get(name);
				if (transaction != null) {
					transaction.complete();
					transactions.remove(name);
				}
			}
		}
	}

	@Override
	public void record(String name, String status) {
		if (isStopped()) {
			if (checkCountingIfExists(name)) {
				Transaction transaction = transactions.get(name);
				if (transaction != null) {
					transaction.setStatus(status);
				}
			}
		}
	}

	@Override
	protected void doStart() {}

	@Override
	protected void doStop() {}
}