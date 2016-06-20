package ts.eclipse.ide.terminal.interpreter.internal.commands;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;

import ts.eclipse.ide.terminal.interpreter.ICommandInterpreter;
import ts.eclipse.ide.terminal.interpreter.ICommandInterpreterFactory;

public class CdCommandInterpreterFactory implements ICommandInterpreter, ICommandInterpreterFactory {

	@Override
	public void process(List<String> parameters, String workingDir) {
		if (parameters.size() < 1) {
			return;
		}
		String path = parameters.get(0);
		try {
			final IContainer[] c = ResourcesPlugin.getWorkspace().getRoot()
					.findContainersForLocation(new Path(workingDir + "/" + path));
			if (c != null && c.length > 0) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
				final IViewPart view = page.findView(IPageLayout.ID_PROJECT_EXPLORER);

				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						((ISetSelectionTarget) view).selectReveal(new StructuredSelection(c));
					}
				});
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addLine(String line) {
		// Do nothing
	}

	@Override
	public ICommandInterpreter create() {
		return this;
	}

}
