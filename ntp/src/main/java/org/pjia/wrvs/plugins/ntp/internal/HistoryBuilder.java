package org.pjia.wrvs.plugins.ntp.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 历史记录构造者
 * 
 * @author pjia
 *
 */
public class HistoryBuilder {
	
	private HistoryBuilder() {
		
	}
	
	public static HistoryBuilder create() {
		return new HistoryBuilder();
	}
	
	/**
	 * 构造历史记录 sheet
	 * 
	 * @param document
	 * @param sheet
	 */
	public void build(Document document, Sheet sheet) {
		Elements trs = document.getElementsByTag("tr");
		for(int i = 0; i < trs.size(); i++) {
			Element tr = trs.get(i);
			Row row = sheet.createRow(i);
			buildRow(row, tr);
		}
	}

	private void buildRow(Row row, Element tr) {
		Elements tds = tr.getElementsByTag("td");
		int cIndex = 0;
		for(int i = 0; i < tds.size(); i++) {
			Element td = tds.get(i);
			List<Cell> cells = new ArrayList<>(10);
			// 处理 colspan
			int colspan = getColSpan(td);
			for(int n = 0; n < colspan; n++) {
				cells.add(row.createCell(cIndex++, CellType.STRING));
			}
			if(cells.size() > 0) {
				cells.get(0).setCellValue(td.text());
			}
			// 合并单元格
			if(cells.size() > 1) {
				CellRangeAddress region = new CellRangeAddress(
						cells.get(0).getRow().getRowNum(), cells.get(0).getRow().getRowNum(), 
						cells.get(0).getColumnIndex(), cells.get(cells.size() - 1).getColumnIndex());
				row.getSheet().addMergedRegion(region);
			}

		}
		
	}

	private int getColSpan(Element td) {
		return td.hasAttr("colspan")?Integer.valueOf(td.attr("colspan")):1;
	}
}
