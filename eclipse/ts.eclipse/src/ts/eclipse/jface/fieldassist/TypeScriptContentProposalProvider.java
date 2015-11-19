package ts.eclipse.jface.fieldassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import ts.TSException;
import ts.resources.ITypeScriptFile;
import ts.resources.ITypeScriptProject;

public class TypeScriptContentProposalProvider implements IContentProposalProvider {

	private final String fileName;
	private final ITypeScriptProject project;

	public TypeScriptContentProposalProvider(String fileName, ITypeScriptProject project) {
		this.fileName = fileName;
		this.project = project;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		ITypeScriptFile file = project.getOpenedFile(fileName);
		ContentProposalCollector collector = new ContentProposalCollector();
		try {
			project.completions(file, position, collector);
		} catch (TSException e) {
			e.printStackTrace();
		}
		return collector.toArray(ContentProposalCollector.EMPTY_PROPOSAL);

		// ITypeScriptServiceClient client = doc.getClient();
		// try {
		// // Update file content
		// client.updateFile(doc.getName(), contents);
		// // Total
		// List<Integer> lines = readLines(contents);
		// int offset = position;
		// int current = position;
		// int line = 0;
		// for (Integer lineOffset : lines) {
		// if (line > 0) {
		// current -= "\r\n".length();
		// }
		// if (current <= lineOffset) {
		// offset = current;
		// break;
		// } else {
		// current -= lineOffset;
		// }
		// line++;
		// }
		//
		// String prefix = null;
		// ICompletionInfo completion =
		// client.getCompletionsAtLineOffset(doc.getName(), line + 1, offset +
		// 1, prefix);
		// List<IContentProposal> proposals = new ArrayList<IContentProposal>();
		// ICompletionEntry[] entries = completion.getEntries();
		// for (int i = 0; i < entries.length; i++) {
		// final ICompletionEntry entry = entries[i];
		// proposals.add(new IContentProposal() {
		//
		// @Override
		// public String getLabel() {
		// // TODO Auto-generated method stub
		// return entry.getName();
		// }
		//
		// @Override
		// public String getDescription() {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public int getCursorPosition() {
		// // TODO Auto-generated method stub
		// return 0;
		// }
		//
		// @Override
		// public String getContent() {
		// // TODO Auto-generated method stub
		// return entry.getName();
		// }
		// });
		// }
		// return proposals.toArray(new IContentProposal[0]);
		// } catch (TSException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return null;
	}

	public static List<Integer> readLines(final String input) {
		final List<Integer> list = new ArrayList<Integer>();
		try {
			final BufferedReader reader = new BufferedReader(new StringReader(input));
			String line = reader.readLine();
			while (line != null) {
				if (list.size() > 0) {
					list.add(line.length());// "\r\n".length());
				} else {
					list.add(line.length());
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}