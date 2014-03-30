package name.stojanok.dzone.tablehandler;

import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

public class EngineMain {
	
	public void runReport(ManipulationLevelEnum manipulate) throws EngineException {
		runReport(manipulate, 0);
	}
	
	@SuppressWarnings("deprecation")
	public void runReport(ManipulationLevelEnum manipulate, int posn) throws EngineException {
		IReportEngine engine = null;
		EngineConfig config = null;

		// Configure the Engine and start the Platform
		try {
			config = new EngineConfig();
//			config.setEngineHome("C:/birt-runtime-2_1_1/birt-runtime-2_1_1/ReportEngine");
//			config.setLogConfig(null, Level.FINE);
			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			engine = factory.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		IReportRunnable design = null;

		// Open and manipulate the report
		// the column which will be deleted
		design = MyDesignHandler.manipulateReport(design, engine, posn, manipulate);

		// Create task to run and render the report,
		IRunAndRenderTask task = engine.createRunAndRenderTask(design);

		PDFRenderOption pdfRenderOption = new PDFRenderOption();
		pdfRenderOption.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
		pdfRenderOption.setOutputFileName(posn + "_" + manipulate.toString() + "_output.pdf");
		task.setRenderOption(pdfRenderOption);

		// run the report and destroy the engine
		// Note - If the program stays resident do not shutdown the Platform or
		// the Engine
		task.run();
		task.close();
		engine.shutdown();
		Platform.shutdown();
		System.out.println("Finished");
	}

	public static void main(String[] args) {
		EngineMain engineMain = new EngineMain();
		try {
			engineMain.runReport(ManipulationLevelEnum.NONE);
			engineMain.runReport(ManipulationLevelEnum.INIT);
			for (int i = 0; i < 5; i++) {
				engineMain.runReport(ManipulationLevelEnum.ALL, i);
			}
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
