package name.stojanok.dzone.dynamicreports;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.DesignFileException;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.LibraryHandle;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScriptDataSetHandle;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.ContentException;
import org.eclipse.birt.report.model.api.command.NameException;
import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.elements.OdaDataSet;
import org.eclipse.birt.report.model.elements.OdaDataSource;
import org.eclipse.birt.report.model.elements.ScriptDataSet;
import org.eclipse.birt.report.model.elements.SimpleMasterPage;

public class MyDesignHandler {
	
	private static final String REPORT_PATH = "report" + File.separator;

	public static IReportRunnable createReport(IReportRunnable design, IReportEngine engine, ReportTypeEnum reportTypeEnum) throws EngineException {
		try {
			//Step 1 - loading the library
			SessionHandle session = DesignEngine.newSession(null);
			ReportDesignHandle reportDesignHandle = session.createDesign();
			LibraryHandle defaultLibraryHandle = session.openLibrary(getLibraryPath(reportTypeEnum.DEFAULT));
			setDefaultLibraryElements(reportDesignHandle, defaultLibraryHandle);
			design = engine.openReportDesign(reportDesignHandle);
			if (!ReportTypeEnum.DEFAULT.equals(reportTypeEnum)) {
				setExtraLibraryElements(reportTypeEnum, reportDesignHandle);
			}
			orderSections(reportDesignHandle);
		} catch (DesignFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return design;
	}

	private static void orderSections(ReportDesignHandle reportDesignHandle) {
		List<String> gridSectionNames = Arrays.asList("ZeroSectionGrid",
				"FirstSectionGrid", "SecondSectionGrid");
		
		Map<String, DesignElement> map = new HashMap<String, DesignElement>();
		for (Object bodyContent : reportDesignHandle.getBody().getContents()) {
			if (bodyContent instanceof GridHandle) {
				GridHandle gridHandle = (GridHandle)bodyContent;
				String name = gridHandle.getName();
				DesignElement designElement = (DesignElement)gridHandle.copy();
				map.put(name, designElement);
			}
		}
		
		for (int i = 0; i < gridSectionNames.size(); i++) {
			if (map.containsKey(gridSectionNames.get(i))) {
				try {
					reportDesignHandle.findElement(gridSectionNames.get(i)).drop();
					reportDesignHandle.getBody().add(map.get(gridSectionNames.get(i)), i);
				} catch (SemanticException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	private static void setExtraLibraryElements(ReportTypeEnum reportTypeEnum,
			ReportDesignHandle reportDesignHandle) throws DesignFileException {
		//Step 2 - loading the extra library
		SessionHandle extraSession = DesignEngine.newSession(null);
		LibraryHandle extratLibraryHandle = extraSession.openLibrary(getLibraryPath(reportTypeEnum));
		//Step 3 - copy the data sets
		replaceCopyDataSet(reportDesignHandle, extratLibraryHandle);
		//Step 3 - copy the master pages
		replaceCopyMasterPage(reportDesignHandle, extratLibraryHandle);
		//Step 4 - copy the grids
		replaceCopyComponents(reportDesignHandle, extratLibraryHandle);
	}
	
	@SuppressWarnings("deprecation")
	private static void setDefaultLibraryElements(
			final ReportDesignHandle reportDesignHandle,
			final LibraryHandle defaultLibraryHandle) {
		
		for (Object defaultScriptDataSourceHandle : defaultLibraryHandle.getDataSources().getContents()) {
			if (defaultScriptDataSourceHandle instanceof OdaDataSourceHandle) {
				try {
					reportDesignHandle.getDataSources().add((OdaDataSource)((OdaDataSourceHandle)defaultScriptDataSourceHandle).copy());
				} catch (ContentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Object defaultScriptDataSetHandle : defaultLibraryHandle.getDataSets().getContents()) {
			if (defaultScriptDataSetHandle instanceof OdaDataSetHandle) {
				try {
					reportDesignHandle.getDataSets().add((OdaDataSet)((OdaDataSetHandle)defaultScriptDataSetHandle).copy());
				} catch (ContentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Object defaultMasterPageHandle : defaultLibraryHandle.getMasterPages().getContents()) {
			if (defaultMasterPageHandle instanceof SimpleMasterPageHandle) {
				try {
					reportDesignHandle.getMasterPages().add((SimpleMasterPage)((SimpleMasterPageHandle)defaultMasterPageHandle).copy());
				} catch (ContentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for (Object defaultGridHandle : defaultLibraryHandle.getComponents().getContents()) {
			if (defaultGridHandle instanceof GridHandle) {
				try {
					reportDesignHandle.getBody().add((DesignElement)((GridHandle)defaultGridHandle).copy());
				} catch (ContentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void replaceCopyComponents(
			ReportDesignHandle reportDesignHandle,
			LibraryHandle extratLibraryHandle) {
		try {
			for (Object extraGridHandleObject : extratLibraryHandle.getComponents().getContents()) {
				if (extraGridHandleObject instanceof GridHandle) {
					for (Object defaultGridHandleObject : reportDesignHandle.getBody().getContents()) {
						if (extraGridHandleObject instanceof GridHandle) {
							if (
								((GridHandle)extraGridHandleObject).getName().equals( ((GridHandle)defaultGridHandleObject).getName())
								) {
									int pos = reportDesignHandle.getBody().findPosn((DesignElementHandle)(defaultGridHandleObject)); 
									reportDesignHandle.getBody().drop((DesignElementHandle)defaultGridHandleObject);
									reportDesignHandle.getBody().add((DesignElement)((GridHandle)extraGridHandleObject).copy(), pos);
									extratLibraryHandle.getComponents().drop((DesignElementHandle)extraGridHandleObject);
							}
						}
					}
				}
			}
			for (Object extraGridHandleObject : extratLibraryHandle.getComponents().getContents()) {
				if (extraGridHandleObject instanceof GridHandle) {
						reportDesignHandle.getBody().add((DesignElement)((GridHandle)extraGridHandleObject).copy());
				}
			}
		} catch (SemanticException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void replaceCopyMasterPage(
			ReportDesignHandle reportDesignHandle,
			LibraryHandle extratLibraryHandle) {
		try {
			for (Object extraSimpleMasterPageHandleObject : extratLibraryHandle.getMasterPages().getContents()) {
				if (extraSimpleMasterPageHandleObject instanceof SimpleMasterPageHandle) {
					for (Object defaultSimpleMasterPageHandleObject : reportDesignHandle.getMasterPages().getContents()) {
						if (extraSimpleMasterPageHandleObject instanceof SimpleMasterPageHandle) {
							if (
								((SimpleMasterPageHandle)extraSimpleMasterPageHandleObject).getName().equals( ((SimpleMasterPageHandle)defaultSimpleMasterPageHandleObject).getName())
								) {
									reportDesignHandle.getMasterPages().drop((DesignElementHandle)defaultSimpleMasterPageHandleObject);
									reportDesignHandle.getMasterPages().add((SimpleMasterPage)((SimpleMasterPageHandle)extraSimpleMasterPageHandleObject).copy());
									extratLibraryHandle.getMasterPages().drop((DesignElementHandle)extraSimpleMasterPageHandleObject);
							}
						}
					}
				}
			}
			for (Object extraSimpleMasterPageHandleObject : extratLibraryHandle.getMasterPages().getContents()) {
				if (extraSimpleMasterPageHandleObject instanceof SimpleMasterPageHandle) {
						reportDesignHandle.getMasterPages().add((SimpleMasterPage)((SimpleMasterPageHandle)extraSimpleMasterPageHandleObject).copy());
				}
			}
		} catch (SemanticException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void replaceCopyDataSet(
			ReportDesignHandle reportDesignHandle,
			LibraryHandle extratLibraryHandle) {
		try {
			for (Object extraOdaDataSetHandleObject : extratLibraryHandle.getDataSets().getContents()) {
				if (extraOdaDataSetHandleObject instanceof OdaDataSetHandle) {
					for (Object defaultOdaDataSetHandleObject : reportDesignHandle.getDataSets().getContents()) {
						if (extraOdaDataSetHandleObject instanceof OdaDataSetHandle) {
							if (
								((OdaDataSetHandle)extraOdaDataSetHandleObject).getName().equals( ((OdaDataSetHandle)defaultOdaDataSetHandleObject).getName())
								) {
									reportDesignHandle.getDataSets().drop((DesignElementHandle)defaultOdaDataSetHandleObject);
									reportDesignHandle.getDataSets().add((OdaDataSet)((OdaDataSetHandle)extraOdaDataSetHandleObject).copy());
									extratLibraryHandle.getDataSets().drop((DesignElementHandle)extraOdaDataSetHandleObject);
							}
						}
					}
				}
			}
			for (Object extraOdaDataSetHandleObject : extratLibraryHandle.getDataSets().getContents()) {
				if (extraOdaDataSetHandleObject instanceof OdaDataSetHandle) {
						reportDesignHandle.getDataSets().add((OdaDataSet)((OdaDataSetHandle)extraOdaDataSetHandleObject).copy());
				}
			}
		} catch (SemanticException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getLibraryPath(ReportTypeEnum reportTypeEnum) {
		String outputString = REPORT_PATH + "default.rptlibrary";
		if (ReportTypeEnum.FIRST.equals(reportTypeEnum)) {
			outputString = REPORT_PATH + "first.rptlibrary";
		} else if (ReportTypeEnum.SECOND.equals(reportTypeEnum)) {
			outputString = REPORT_PATH + "second.rptlibrary";
		}
		return outputString;
	}
}
