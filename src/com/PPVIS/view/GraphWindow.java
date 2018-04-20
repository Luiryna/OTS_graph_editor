package com.PPVIS.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class GraphWindow {
    private Display display;
    private Shell shell;
    private Color defaultColor;
    private MainWindow mainWindow;

    public GraphWindow(Display display,MainWindow mainWindow, int x, int y) {
        this.display = display;
        this.shell = new Shell(display);
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 8;
        gridLayout.numColumns = 2;
        defaultColor = new Color(null, 255, 247, 247);
        shell.setLayout(gridLayout);
        shell.setText("Add graph");
        shell.setSize(200, 150);
        shell.setModified(false);
        shell.setImage(new Image(display, "img/iconLogo.png"));
        shell.setBackground(defaultColor);
        shell.setLocation(x,y);
        initWindow();
        this.mainWindow = mainWindow;
        shell.setFocus();
        shell.open();
    }

    private void initWindow() {
        Label labelSurname = new Label(shell, SWT.NULL);
        labelSurname.setText("Имя графа");
        labelSurname.setBackground(defaultColor);

        Text name = new Text(shell, SWT.SINGLE | SWT.BORDER);
        GridData gridDataSurname = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gridDataSurname.horizontalSpan = 5;
        name.setLayoutData(gridDataSurname);

        Button button = new Button(shell, SWT.PUSH);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        button.setLayoutData(gridData);
        button.setText("Add");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                if (!name.getText().equals("")) {
                    mainWindow.addTab(name.getText());
                    shell.close();
                }
            }
        });

    }
}
