package org.pjia.wrvs.plugins.ntp.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import lombok.Data;

/**
 * 报文模型
 * 
 * @author pjia
 *
 */
@Data
public class Message {
	
	private String issueId;
	
	/**
	 * Message Name
	 */
	private String name;
	
	/**
	 * Message ID
	 */
	private String id;
	
	/**
	 * Cycle time [ms]
	 */
	private String cycleTime;
	
	/**
	 * Send Type
	 */
	private String sendType;
	
	/**
	 * Message Length [Byte]
	 */
	private String messageLength;
	
	/**
	 * message row
	 */
	private Row row;
	
	/**
	 * 包含的信号
	 */
	private List<Signal> signals = new ArrayList<Signal>(10);

	public Message(String messageName) {
		this.name = messageName;
	}

	public void addSignal(Signal signal) {
		signals.add(signal);
	}

	/**
	 * 获取最后一个 signal
	 * 
	 * @return
	 */
	public Signal getLast() {
		return signals.size() == 0? null: signals.get(signals.size() -1);
	}
	
	/**
	 * 兼容数值型
	 * 
	 * @return
	 */
	public String getMessageLength() {
		try {
			Double valueOf = Double.valueOf(messageLength);
			// 数值型
			return String.valueOf(valueOf.intValue());
		}catch (NumberFormatException e) {
			// 非数值型
			return messageLength;
		}
	}
	
	/**
	 * 兼容数值型
	 * 
	 * @return
	 */
	public String getCycleTime() {
		try {
			Double valueOf = Double.valueOf(cycleTime);
			// 数值型
			return String.valueOf(valueOf.intValue());
		}catch (NumberFormatException e) {
			// 非数值型
			return cycleTime;
		}
	}
}
