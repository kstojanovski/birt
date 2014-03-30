package name.stojanok.dzone.tablehandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.TableGroupHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;

public class MyDesignHandler {
	
	public static IReportRunnable manipulateReport(IReportRunnable design, IReportEngine engine, int posn, ManipulationLevelEnum manipulate) {
		//Step 1 - converting the XML file into java object
		try {
			design = engine.openReportDesign("report" + File.separator
					+ "simpletable.rptdesign");
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!ManipulationLevelEnum.NONE.equals(manipulate)) {
			//Step 2 - find the table
			TableHandle customerTable = (TableHandle)((ReportDesignHandle) design.getDesignHandle()).findElement("CustomerTable");
			
			List<Integer> columnWidths = new ArrayList<Integer>(Arrays.asList(new Integer[]{50, 150, 150, 150, 250}));
			
			if (ManipulationLevelEnum.ALL.equals(manipulate)) {
				//Step 3 - find and drop group header and footer
				for (int i = 0; i < customerTable.getGroups().getCount(); i++) {
					TableGroupHandle tableGroupHandle = (TableGroupHandle) customerTable
							.getGroups().get(i);
					iterateAndDeleteFrom(tableGroupHandle.getHeader(), posn);
					iterateAndDeleteFrom(tableGroupHandle.getFooter(), posn);			
				}
				
				//Step 4 - find and drop the header, detail and footer
				iterateAndDeleteFrom(customerTable.getHeader(), posn);
				iterateAndDeleteFrom(customerTable.getDetail(), posn);
				iterateAndDeleteFrom(customerTable.getFooter(), posn);
				
				//Step 5 - delete the column
				try {
					if (customerTable.getColumns().get(posn) != null) {
						customerTable.getColumns().get(posn).drop();
						//Step 6 - recalculating the widths			
						recalculatingWidths(columnWidths, posn); 
					}
				} catch (SemanticException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
			
//			System.out.println(manipulate + " " + posn + " " + columnWidths);
			
			//Step 7 - setting the new with to the table
			for (int i = 0; i < columnWidths.size(); i++) {
				if (customerTable.getColumns().get(i) != null) {
					try {
						customerTable.getColumns().get(i).setProperty("width", columnWidths.get(i) + "px");
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
		}
		
		return design;
	}

	/**
	 * specification:
	 * first idx=0 and last column idx=4 can not be resized, they have static width.
	 * the inner three columns are dynamic and will get the width of the deleted columns
	 * 
	 * @param columnWidths
	 * @param posn
	 */
	private static void recalculatingWidths(List<Integer> columnWidths, int posn) {
		int distribution = 2;
		if (posn == 0 || posn == 4) {
			distribution = 3;
		}
		int additionalWidth = Math.round(columnWidths.get(posn) / distribution);
		for (int i = 0; i < columnWidths.size(); i++) {
			if (i != 0 && i != 4 && i != posn) {
				columnWidths.set(i, columnWidths.get(i) + additionalWidth);
			}
		}		
		columnWidths.remove(posn);
	}

	public static void iterateAndDeleteFrom(SlotHandle slothandle, int columnNumber) {
		for (int j = 0; j < slothandle.getCount(); j++) {
			dropCell(slothandle.get(j), columnNumber);
		}
	}
	
	public static void dropCell(DesignElementHandle designElementHandle,
			int posn) {
		if (designElementHandle != null) {
			if (designElementHandle instanceof RowHandle) {
				RowHandle rowHandle = (RowHandle) designElementHandle;
				if (rowHandle != null && rowHandle.getCells() != null
						&& rowHandle.getCells().get(posn) != null) {
					try {
						rowHandle.getCells().get(posn).drop();
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
