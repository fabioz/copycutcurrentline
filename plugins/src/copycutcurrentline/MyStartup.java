package copycutcurrentline;

import java.lang.reflect.*;

import org.eclipse.core.commands.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.commands.*;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.*;

public class MyStartup implements IStartup {
	ICommandService cmdService;
	Command copyCommand;
	Command cutCommand;
	Command pasteCommand;
	MyHandler myHandler;

	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// hook the startup editor if any, it doesn't get notified via a
				// normal event
				IEditorPart startupEditorPart = Activator.getDefault()
						.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActiveEditor();
				if (startupEditorPart != null) {
					activated(Activator.getDefault().getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getActivePart());
				}
				MyHandler.clipboard = new Clipboard(Activator.getDefault()
						.getWorkbench().getDisplay());

				Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.addPartListener(new IPartListener() {

							@Override
							public void partActivated(IWorkbenchPart part) {
								activated(part);
							}

							@Override
							public void partBroughtToTop(IWorkbenchPart part) {
								activated(part);
							}

							@Override
							public void partClosed(IWorkbenchPart part) {
								deactivated(part);
							}

							@Override
							public void partDeactivated(IWorkbenchPart part) {
								deactivated(part);
							}

							@Override
							public void partOpened(IWorkbenchPart part) {
							}
						});
			}
		});
	}

	private void deactivated(IWorkbenchPart part) {
		if (!(part instanceof EditorPart))
			return;

		if (cmdService == null) {
			cmdService = (ICommandService) part.getSite().getService(
					ICommandService.class);
			copyCommand = cmdService
					.getCommand("CopyCutCurrentLine.MyCopyCommand");
			cutCommand = cmdService
					.getCommand("CopyCutCurrentLine.MyCutCommand");
			pasteCommand = cmdService
					.getCommand("CopyCutCurrentLine.MyPasteCommand");
		}

		copyCommand.setHandler(null);
		pasteCommand.setHandler(null);
		cutCommand.setHandler(null);
	}

	private void activated(IWorkbenchPart part) {
		if (!(part instanceof EditorPart))
			return;

		try {
			IEditorPart editorPart = Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();

			ITextViewer textViewer = callGetSourceViewer((AbstractTextEditor) editorPart);
			MyHandler.tw = (StyledText) textViewer.getTextWidget();

			if (cmdService == null) {
				cmdService = (ICommandService) part.getSite().getService(
						ICommandService.class);
				copyCommand = cmdService
						.getCommand("CopyCutCurrentLine.MyCopyCommand");
				cutCommand = cmdService
						.getCommand("CopyCutCurrentLine.MyCutCommand");
				pasteCommand = cmdService
						.getCommand("CopyCutCurrentLine.MyPasteCommand");
			}

			if (!copyCommand.isHandled())
				copyCommand.setHandler(MyHandler.THIS);
			if (!pasteCommand.isHandled())
				pasteCommand.setHandler(MyHandler.THIS);
			if (!cutCommand.isHandled())
				cutCommand.setHandler(MyHandler.THIS);
		} catch (Exception err) {
			deactivated(part);
		}

	}

	/**
	 * Calls AbstractTextEditor.getSourceViewer() through reflection, as that
	 * method is normally protected (for some ungodly reason).
	 *
	 * @param AbstractTextEditor
	 *            to run reflection on
	 */
	private ITextViewer callGetSourceViewer(AbstractTextEditor editor)
			throws Exception {
		try {
			Method method = AbstractTextEditor.class
					.getDeclaredMethod("getSourceViewer");
			method.setAccessible(true);

			return (ITextViewer) method.invoke(editor);
		} catch (NullPointerException npe) {
			return null;
		}
	}
}
