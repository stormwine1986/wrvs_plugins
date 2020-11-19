package org.pjia.wrvs.plugins.ntp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mks.api.MultiValue;

import lombok.Data;

/**
 * 信号模型
 * 
 * @author pjia
 *
 */
@Data
public class Signal {
	
	/**
	 * 所属的 message
	 * 
	 */
	private Message message;
	
	private String issueId;
	
	/**
	 * Signal Name
	 */
	private String name;
	
	/**
	 * Byte Number
	 */
	private String byteNumber;
	
	/**
	 * Bit Number
	 */
	private String bitNumber;
	
	/**
	 * Signal Length [Bit]
	 */
	private String signalLength;
	
	/**
	 * Start Bit-No
	 */
	private String startBitNo;
	
	/**
	 * Event of signal
	 */
	private String eventOfSignal;
	
	/**
	 * Signal Description
	 */
	private String signalDescription;
	
	/**
	 * Signal Initial
	 */
	private String signalInitial;
	
	/**
	 * Signal Initial Remark
	 */
	private String signalInitialRemark;
	
	/**
	 * Invalid Value
	 */
	private String invalidValue;
	
	/**
	 * Physical Range
	 */
	private String physicalRange;
	
	/**
	 * Normal
	 */
	private String normal;
	
	/**
	 * Physical Resolution
	 */
	private String physicalResolution;
	
	/**
	 * bit matrix
	 */
	private JsonArray bitMatrix;
	
	/**
	 * 发送方/接收方信息
	 */
	private JsonObject rsInfo;
	
	/**
	 * External Conditions
	 */
	private String externalConditions;
	
	/**
	 * Invalid Value Remark
	 */
	private String invalidValueRemark;
	
	private List<Row> rowScope = new ArrayList<>(10);
	private List<Integer> rowIndexScope = new ArrayList<>(10);

	public Signal(String signalName) {
		this.name = signalName;
	}

	public void addRow(Row row) {
		rowScope.add(row);
		rowIndexScope.add(row.getRowNum());
	}
	
	/**
	 * 兼容 Excel 数值型单元格
	 * @return
	 */
	public String getBitNumber() {
		try {
			Double valueOf = Double.valueOf(bitNumber);
			// 数值类型
			return String.valueOf(valueOf.intValue());
		}catch (NumberFormatException e) {
			// 文本类型，直接返回
			return bitNumber;
		}
	}
	
	/**
	 * 兼容数值类型单元格
	 * 
	 * @return
	 */
	public String getSignalLength() {
		try {
			Double valueOf = Double.valueOf(signalLength);
			// 数值类型
			return String.valueOf(valueOf.intValue());
		}catch (NumberFormatException e) {
			// 文本类型，直接返回
			return signalLength;
		}
	}
	
	public String getStartBitNo() {
		try {
			Double valueOf = Double.valueOf(startBitNo);
			// 数值类型
			return String.valueOf(valueOf.intValue());
		}catch (NumberFormatException e) {
			// 文本类型，直接返回
			return startBitNo;
		}
	}
	
	public String getEventOfSignal() {
		if(StringUtils.isNotBlank(eventOfSignal)) {
			return "Y".equals(eventOfSignal)?"true":"false";
		} else {
			return "";
		}
	}

	public String getSignalSender() {
		if(rsInfo == null) { return ""; }
		MultiValue value = new MultiValue(",");
		Set<String> keySet = rsInfo.keySet();
		// key 是领域名
		for(String key :keySet) {
			String flag = rsInfo.get(key).getAsString();
			// s 是 sender
			if("s".equals(flag)) {
				value.add(key);
			}
		}
		return value.toString();
	}

	public String getSignalReceiver() {
		if(rsInfo == null) { return ""; }
		MultiValue value = new MultiValue(",");
		Set<String> keySet = rsInfo.keySet();
		// key 是领域名
		for(String key :keySet) {
			String flag = rsInfo.get(key).getAsString();
			// r 是 receiver
			if("r".equals(flag)) {
				value.add(key);
			}
		}
		return value.toString();
	}
	
	/**
	 * 获取最后一行
	 * 
	 * @return
	 */
	public Row getLastestRow() {
		if(CollectionUtils.isEmpty(rowScope)) {
			return null;
		}
		return rowScope.get(rowScope.size() - 1);
	}
}
