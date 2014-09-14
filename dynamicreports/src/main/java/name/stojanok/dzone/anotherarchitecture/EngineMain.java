package name.stojanok.dzone.anotherarchitecture;

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
	
	@SuppressWarnings("deprecation")
	public void runReport(ReportTypeEnum manipulate) throws EngineException {
		IReportEngine engine = null;
		EngineConfig config = null;

		// Configure the Engine and start the Platform
		try {
			config = new EngineConfig();
			config.setLogConfig("birtLogs", Level.WARNING);
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
		design = MyDesignHandler.createReport(design, engine, manipulate);

		// Create task to run and render the report,
		IRunAndRenderTask task = engine.createRunAndRenderTask(design);

		PDFRenderOption pdfRenderOption = new PDFRenderOption();
		pdfRenderOption.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
		pdfRenderOption.setOutputFileName("_" + manipulate.toString() + "_output.pdf");
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
			for (ReportTypeEnum reportTypeEnum : ReportTypeEnum.values()) {
			engineMain.runReport(reportTypeEnum);
			}
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
