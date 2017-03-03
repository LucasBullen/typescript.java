/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Springrbua - TypeScript project wizard
 *
 */
package ts.eclipse.ide.internal.ui.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ts.cmd.tsc.CompilerOptions;
import ts.eclipse.ide.internal.ui.TypeScriptUIMessages;
import ts.resources.jsonconfig.TsconfigJson;
import ts.utils.StringUtils;

/**
 * tsconfig.json wizard page
 *
 */
public class TSConfigWizardPage extends WizardPage implements Listener {

	// General
	private Combo cbModule;
	private Combo cbModuleResolution;
	private Combo cbTarget;
	private Text txtOutDir;

	// Generation
	private Button chkDeclaration;
	private Button chkSourceMap;
	private Button chkRemoveComments;

	// Decorators
	private Button chkEmitDecoratorMetadata;
	private Button chkExperimentalDecorators;

	// Validation
	private Button chkNoFallthroughCasesInSwitch;
	private Button chkNoImplicitAny;
	private Button chkNoImplicitReturns;
	private Button chkStrictNullChecks;

	protected TSConfigWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		// top level group
		Composite topLevel = new Composite(parent, SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		topLevel.setFont(parent.getFont());

		// Text tsconfigText = new Text(parent, style)

		createParamsControl(topLevel);

		// initialize page with default values
		initializeDefaultValues();

		validatePage();
		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);
	}

	protected void createParamsControl(Composite parent) {
		Font font = parent.getFont();

		// params group
		Composite paramsGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		paramsGroup.setLayout(layout);
		paramsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		paramsGroup.setFont(font);

		// general settings
		Composite subGroup = new Composite(paramsGroup, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		subGroup.setLayout(layout);
		subGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		subGroup.setFont(font);

		// Module
		Label label = new Label(subGroup, SWT.NONE);
		label.setText(TypeScriptUIMessages.TSConfigWizardPage_module);
		label.setFont(font);

		// Combobox for module
		cbModule = new Combo(subGroup, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cbModule.addListener(SWT.Modify, this);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		cbModule.setLayoutData(data);

		// Data for Combobox module
		cbModule.add("None");
		cbModule.add("CommonJS");
		cbModule.add("AMD");
		cbModule.add("System");
		cbModule.add("UMD");
		cbModule.add("ES6");
		cbModule.add("ES2015");

		// Module resolution
		label = new Label(subGroup, SWT.NONE);
		label.setText(TypeScriptUIMessages.TSConfigWizardPage_moduleResolution);
		label.setFont(font);

		// Combobox for module resolution
		cbModuleResolution = new Combo(subGroup, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cbModuleResolution.addListener(SWT.Modify, this);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		cbModuleResolution.setLayoutData(data);

		// Data for Combobox moduele resolution
		cbModuleResolution.add("Node");
		cbModuleResolution.add("Classic");

		// Target
		label = new Label(subGroup, SWT.NONE);
		label.setText(TypeScriptUIMessages.TSConfigWizardPage_target);
		label.setFont(font);

		// Combobox for target
		cbTarget = new Combo(subGroup, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cbTarget.addListener(SWT.Modify, this);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		cbTarget.setLayoutData(data);

		// Data for Combobox target
		cbTarget.add("ES3");
		cbTarget.add("ES5");
		cbTarget.add("ES6");
		cbTarget.add("ES2016");
		cbTarget.add("ES2017");
		cbTarget.add("ESNext");

		// outDir
		label = new Label(subGroup, SWT.NONE);
		label.setText(TypeScriptUIMessages.TSConfigWizardPage_outDir);
		label.setFont(font);

		// Text for outDir
		txtOutDir = new Text(subGroup, SWT.BORDER);
		txtOutDir.addListener(SWT.Modify, this);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		txtOutDir.setLayoutData(data);
		txtOutDir.setFont(font);

		// Separator
		Label line = new Label(paramsGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		line.setLayoutData(data);

		// generation settings
		subGroup = new Composite(paramsGroup, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 0;
		subGroup.setLayout(layout);
		subGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		subGroup.setFont(font);

		// Declaration
		chkDeclaration = new Button(subGroup, SWT.CHECK);
		chkDeclaration.addListener(SWT.Selection, this);
		chkDeclaration.setText(TypeScriptUIMessages.TSConfigWizardPage_declaration);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkDeclaration.setLayoutData(data);

		// Source map
		chkSourceMap = new Button(subGroup, SWT.CHECK);
		chkSourceMap.addListener(SWT.Selection, this);
		chkSourceMap.setText(TypeScriptUIMessages.TSConfigWizardPage_sourceMap);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkSourceMap.setLayoutData(data);

		// Remove comments
		chkRemoveComments = new Button(subGroup, SWT.CHECK);
		chkRemoveComments.addListener(SWT.Selection, this);
		chkRemoveComments.setText(TypeScriptUIMessages.TSConfigWizardPage_removeComments);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkRemoveComments.setLayoutData(data);

		// Separator
		line = new Label(paramsGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		line.setLayoutData(data);

		// decorators settings
		subGroup = new Composite(paramsGroup, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		subGroup.setLayout(layout);
		subGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		subGroup.setFont(font);

		// Emit decorator metadata
		chkEmitDecoratorMetadata = new Button(subGroup, SWT.CHECK);
		chkEmitDecoratorMetadata.addListener(SWT.Selection, this);
		chkEmitDecoratorMetadata.setText(TypeScriptUIMessages.TSConfigWizardPage_emitDecoratorMetadata);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkEmitDecoratorMetadata.setLayoutData(data);

		// Experimental decorators
		chkExperimentalDecorators = new Button(subGroup, SWT.CHECK);
		chkExperimentalDecorators.addListener(SWT.Selection, this);
		chkExperimentalDecorators.setText(TypeScriptUIMessages.TSConfigWizardPage_experimentalDecorators);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkExperimentalDecorators.setLayoutData(data);

		// Separator
		line = new Label(paramsGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		line.setLayoutData(data);

		// validation settings
		subGroup = new Composite(paramsGroup, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		subGroup.setLayout(layout);
		subGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		subGroup.setFont(font);

		// No fallthrough cases in switch
		chkNoFallthroughCasesInSwitch = new Button(subGroup, SWT.CHECK);
		chkNoFallthroughCasesInSwitch.addListener(SWT.Selection, this);
		chkNoFallthroughCasesInSwitch.setText(TypeScriptUIMessages.TSConfigWizardPage_noFallthroughCasesInSwitch);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkNoFallthroughCasesInSwitch.setLayoutData(data);

		// No implicit any
		chkNoImplicitAny = new Button(subGroup, SWT.CHECK);
		chkNoImplicitAny.addListener(SWT.Selection, this);
		chkNoImplicitAny.setText(TypeScriptUIMessages.TSConfigWizardPage_noImplicitAny);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkNoImplicitAny.setLayoutData(data);

		// No implicit returns
		chkNoImplicitReturns = new Button(subGroup, SWT.CHECK);
		chkNoImplicitReturns.addListener(SWT.Selection, this);
		chkNoImplicitReturns.setText(TypeScriptUIMessages.TSConfigWizardPage_noImplicitReturns);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkNoImplicitReturns.setLayoutData(data);

		// Strict null checks
		chkStrictNullChecks = new Button(subGroup, SWT.CHECK);
		chkStrictNullChecks.addListener(SWT.Selection, this);
		chkStrictNullChecks.setText(TypeScriptUIMessages.TSConfigWizardPage_strictNullChecks);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkStrictNullChecks.setLayoutData(data);

	}

	private void initializeDefaultValues() {
		cbModule.select(cbModule.indexOf("None"));
		cbModuleResolution.select(cbModuleResolution.indexOf("Node"));
		cbTarget.select(cbTarget.indexOf("ES3"));
	}

	@Override
	public void handleEvent(Event event) {
		setPageComplete(validatePage());
	}

	protected boolean validatePage() {
		boolean valid = true;

		return valid;
	}

	public IPath getPath() {
		// TODO: manage this path with text field.
		return new Path("tsconfig.json");
	}

	public void addContents(TsconfigJson tsconfig) {
		CompilerOptions options = tsconfig.getCompilerOptions();
		if (options == null) {
			options = new CompilerOptions();
			tsconfig.setCompilerOptions(options);
		}
		options.setModule(cbModule.getText());
		options.setModuleResolution(cbModuleResolution.getText());
		options.setTarget(cbTarget.getText());
		String outDir = txtOutDir.getText();
		if (!StringUtils.isEmpty(outDir)) {
			options.setOutDir(outDir);
		}
		options.setDeclaration(chkDeclaration.getSelection());
		options.setSourceMap(chkSourceMap.getSelection());
		options.setRemoveComments(chkRemoveComments.getSelection());
		options.setEmitDecoratorMetadata(chkEmitDecoratorMetadata.getSelection());
		options.setExperimentalDecorators(chkExperimentalDecorators.getSelection());
		options.setNoFallthroughCasesInSwitch(chkNoFallthroughCasesInSwitch.getSelection());
		options.setNoImplicitAny(chkNoImplicitAny.getSelection());
		options.setNoImplicitReturns(chkNoImplicitReturns.getSelection());
		options.setStrictNullChecks(chkStrictNullChecks.getSelection());
	}

}