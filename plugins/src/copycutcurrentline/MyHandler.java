package copycutcurrentline;

import org.eclipse.core.commands.*;
import org.eclipse.core.commands.common.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.*;

public class MyHandler extends AbstractHandler {
	public static Clipboard clipboard;
	public static StyledText tw;
	public static boolean hasSomethingToPaste = false;
	public static MyHandler THIS;

	public MyHandler() {
		if (THIS == null)
			THIS = this;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			if (event.getCommand().getName()
					.equals("CopyCutCurrentLine.MyCopyCommand")) {
				if (!tw.getSelectionText().isEmpty()) {
					tw.copy();
					hasSomethingToPaste = false;
				} else {
					String copyString = tw.getLine(tw.getLineAtOffset(tw
							.getCaretOffset()));
					clipboard.setContents(new Object[] { copyString },
							new Transfer[] { TextTransfer.getInstance() });
					hasSomethingToPaste = true;
				}
			} else if (event.getCommand().getName()
					.equals("CopyCutCurrentLine.MyPasteCommand")) {
				if (hasSomethingToPaste) {
					tw.invokeAction(ST.LINE_START);
					String copyString = (String) clipboard
							.getContents(TextTransfer.getInstance());
					if (!copyString.endsWith(tw.getLineDelimiter()))
						copyString += tw.getLineDelimiter();
					clipboard.setContents(new Object[] { copyString },
							new Transfer[] { TextTransfer.getInstance() });
					tw.paste();
					tw.invokeAction(ST.LINE_START);
				} else {
					tw.paste();
				}
			} else if (event.getCommand().getName()
					.equals("CopyCutCurrentLine.MyCutCommand")) {
				if (!tw.getSelectionText().isEmpty()) {
					tw.cut();
					hasSomethingToPaste = false;
				} else {
					tw.invokeAction(ST.LINE_START);
					tw.invokeAction(ST.SELECT_LINE_DOWN);
					tw.cut();
					hasSomethingToPaste = true;
				}
			}
		} catch (NotDefinedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
