package com.PPVIS.view;

import com.PPVIS.model.Vertex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class NameVertexWindow {
    private Display display;
    private Shell shell;
    private Vertex vertex;
    private Color defaultColor;

    public NameVertexWindow(Display display, Vertex vertex, int x, int y) {
        this.display = display;
        this.shell = new Shell(display);
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 8;
        gridLayout.numColumns = 2;
        defaultColor = new Color(null, 255, 247, 247);
        shell.setLayout(gridLayout);
        shell.setText("Add name");
        shell.setImage(new Image(display, "img/iconLogo.png"));
        shell.setSize(200, 150);
        shell.setModified(false);
        shell.setBackground(defaultColor);
        shell.setLocation(x,y);
        this.vertex = vertex;
        initWindow();
        shell.setFocus();
        shell.open();
    }

    private void initWindow() {
        Label labelSurname = new Label(shell, SWT.NULL);
        labelSurname.setText("Название");
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
                    vertex.setName(name.getText());
                    shell.close();
                }
            }
        });

    }
}
