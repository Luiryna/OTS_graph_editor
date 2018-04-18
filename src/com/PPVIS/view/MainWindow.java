package com.PPVIS.view;

import com.PPVIS.Controller.Controller;
import com.PPVIS.model.Arc;
import com.PPVIS.model.Graph;
import com.PPVIS.model.TypeOperation;
import com.PPVIS.model.Vertex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindow {
    private Display display;
    private Shell shell;
    private Color defaultColor;
    private Menu arcMenu;
    private Menu vertexMenu;
    private Canvas canvas;
    private TypeOperation typeOperation;
    private Graph graph;
    private Vertex ingoing;
    private Vertex outgoing;
    private Arc arcCreate;
    private boolean hasSelectVertex = false;

    public MainWindow() {
        display = new Display();
        shell = new Shell(display);
        defaultColor = new Color(null, 255, 247, 247);
        shell.setModified(false);
        shell.setSize(1920, 1080);
        shell.setBackground(defaultColor);
        shell.setText("GraphEditor");
        initLayout();
        initMenuBar();
        initToolBar();
        initCanvas();
        vertexMenuCanvas();
        arcMenuCanvas();
//        TabFolder tabFolder= new TabFolder(shell,SWT.BORDER);
//        TabItem tabItem = new TabItem(tabFolder,SWT.NULL);
//        TabItem tabItem2 = new TabItem(tabFolder,SWT.NULL);
//        tabItem.setControl(canvas);

        graph = new Graph("graph1", canvas);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    private void initLayout() {
        GridLayout gridLayoutShell = new GridLayout();
        gridLayoutShell.numColumns = 1;
        gridLayoutShell.verticalSpacing = 8;
        gridLayoutShell.marginLeft = 5;
        gridLayoutShell.marginRight = 5;
        gridLayoutShell.marginTop = 5;
        gridLayoutShell.marginBottom = 5;
        shell.setLayout(gridLayoutShell);
    }

    private void initMenuBar() {
        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);

        MenuItem fileMenu = new MenuItem(menuBar, SWT.CASCADE);
        fileMenu.setText("File");

        Menu fileMenuDrop = new Menu(shell, SWT.DROP_DOWN);
        fileMenu.setMenu(fileMenuDrop);

        MenuItem saveItem = new MenuItem(fileMenuDrop, SWT.PUSH);
        saveItem.setText("Save");
        saveItem.setAccelerator(SWT.CTRL + 'S');
        saveItem.addSelectionListener(new SelectionAdapter() {
            MessageBox messageBox = new MessageBox(shell);

            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                File file;
                try {
                    file = new File(openFileDialog("Save"));
                } catch (NullPointerException ex) {
                    return;
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    messageBox.setMessage("Ошибка при сохранении");
                    messageBox.open();
                    return;
                }
                if (!Controller.getInstance().save(file, graph)) {
                    messageBox.setMessage("Ошибка при сохранении");
                    messageBox.open();
                } else {
                    messageBox.setMessage("Сохранено");
                    messageBox.open();
                }
            }
        });

        MenuItem menuItemOpen = new MenuItem(fileMenuDrop, SWT.PUSH);
        menuItemOpen.setText("Open");
        menuItemOpen.setAccelerator(SWT.CTRL + 'O');
        menuItemOpen.addSelectionListener(new SelectionAdapter() {
            MessageBox messageBox = new MessageBox(shell);

            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                File file;
                try {
                    file = new File(openFileDialog("Open"));
                } catch (NullPointerException ex) {
                    return;
                }
                if (graph != null)
                    for (Vertex vertex : new ArrayList<>(graph.getVertices()))
                        graph.delete(vertex);
                Graph graph = Controller.getInstance().open(file, canvas);
                if (graph == null) {
                    messageBox.setMessage("Ошибка чтения");
                    messageBox.open();
                } else {
                    MainWindow.this.graph = graph;
                    canvas.redraw();
                }
            }
        });
    }

    private void initToolBar() {
        ToolBar toolBar = new ToolBar(shell, SWT.BORDER);

        ToolItem toolItemAddVertex = new ToolItem(toolBar, SWT.PUSH);
        toolItemAddVertex.setText("Add vertex");
        toolItemAddVertex.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                typeOperation = TypeOperation.ADD_VERTEX;
            }
        });

        ToolItem separator = new ToolItem(toolBar, SWT.SEPARATOR);

        ToolItem toolItemClick = new ToolItem(toolBar, SWT.PUSH);
        toolItemClick.setText("Click");
        toolItemClick.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                typeOperation = TypeOperation.CLICK;
            }
        });

        ToolItem separator1 = new ToolItem(toolBar, SWT.SEPARATOR);

        ToolItem toolItemArc = new ToolItem(toolBar, SWT.PUSH);
        toolItemArc.setText("Add arc");
        toolItemArc.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                typeOperation = TypeOperation.ADD_ARC;
            }
        });

        toolBar.pack();
    }

    private void initCanvas() {
        canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
        GridData gridDataCanvas = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridDataCanvas.grabExcessHorizontalSpace = true;
        canvas.setLayoutData(gridDataCanvas);
        canvas.setBackground(new Color(null, 255, 255, 255));
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent) {
                if (typeOperation == TypeOperation.ADD_VERTEX && mouseEvent.button == 1) {
                    graph.addVertex(mouseEvent.x, mouseEvent.y);
                }
            }

            @Override
            public void mouseDown(MouseEvent mouseEvent) {
                if(mouseEvent.button==3){
                    graph.select(graph.findVertex(mouseEvent.x, mouseEvent.y));
                    if (graph.getSelectVertex() == null) {
                        graph.select(graph.findArc(mouseEvent.x, mouseEvent.y));
                        if(graph.getSelectArc() != null)
                            canvas.setMenu(arcMenu);
                    }
                    else {
                        canvas.setMenu(vertexMenu);
                    }
                } else {
                    if (typeOperation == TypeOperation.CLICK) {
                        graph.select(graph.findVertex(mouseEvent.x, mouseEvent.y));
                        if (graph.getSelectVertex() == null)
                            graph.select(graph.findArc(mouseEvent.x, mouseEvent.y));
                        else hasSelectVertex = true;
                    } else {
                        if (typeOperation == TypeOperation.ADD_ARC) {
                            outgoing = graph.findVertex(mouseEvent.x, mouseEvent.y);
                            if (outgoing != null)
                                arcCreate = graph.addArc(outgoing, outgoing.getX(), outgoing.getY());
                        }
                    }
                }
            }

            @Override
            public void mouseUp(MouseEvent mouseEvent) {
                canvas.setMenu(null);
                if (typeOperation == TypeOperation.CLICK) {
                    hasSelectVertex = false;
                } else {
                    if (typeOperation == TypeOperation.ADD_ARC) {
                        if (arcCreate != null) {
                            ingoing = graph.findVertex(mouseEvent.x, mouseEvent.y);
                            if (ingoing != null) {
                                arcCreate.setIngoing(ingoing);
                                arcCreate = null;
                            } else {
                                graph.delete(arcCreate);
                                arcCreate = null;
                            }
                        }
                    }
                }
            }
        });
        canvas.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent mouseEvent) {
                if (typeOperation == TypeOperation.CLICK) {
                    Rectangle rect = canvas.getBounds();
                    if (hasSelectVertex) {
                        if (rect.y - 40 <= mouseEvent.y && rect.x <= mouseEvent.x && rect.x + rect.width - 23 >= mouseEvent.x && rect.y + rect.height - 80 >= mouseEvent.y)
                            graph.getSelectVertex().move(mouseEvent.x, mouseEvent.y);
                    }
                } else {
                    if (typeOperation == TypeOperation.ADD_ARC) {
                        if (arcCreate != null)
                            arcCreate.setXY(mouseEvent.x, mouseEvent.y);
                    }
                }
            }
        });
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                System.out.println(keyEvent.keyCode);
                if (keyEvent.keyCode == 127) {
                    graph.deleteSelected();
                    return;
                }
                if (keyEvent.keyCode == 105) {
                    if (graph.getSelectVertex() != null)
                        new NameVertexWindow(display, graph.getSelectVertex());
//                    if (graph.getSelectArc() != null)
                    return;
                }
                if (keyEvent.keyCode == 49) {
                    typeOperation = TypeOperation.ADD_VERTEX;
                    return;
                }
                if (keyEvent.keyCode == 50) {
                    typeOperation = TypeOperation.CLICK;
                    return;
                }
                if (keyEvent.keyCode == 51) {
                    typeOperation = TypeOperation.ADD_ARC;
                    return;
                }
                if (keyEvent.keyCode == 115) {
                    if (graph.getSelectArc() != null)
                        graph.getSelectArc().changeOrientation();
                    return;
                }
            }
        });
    }

    private void vertexMenuCanvas() {
        Menu menu = new Menu(shell, SWT.POP_UP);

        MenuItem setTextItem = new MenuItem(menu, SWT.PUSH);
        setTextItem.setText("Set text");

        setTextItem.addListener(SWT.Selection, event -> {
            new NameVertexWindow(display, graph.getSelectVertex());
        });

        MenuItem delItem = new MenuItem(menu, SWT.PUSH);
        delItem.setText("Delete");

        delItem.addListener(SWT.Selection, event -> {
            graph.deleteSelected();
        });

        vertexMenu = menu;
    }

    private void arcMenuCanvas() {
        Menu menu = new Menu(shell, SWT.POP_UP);

        MenuItem setTextItem = new MenuItem(menu, SWT.PUSH);
        setTextItem.setText("Set weight");

        setTextItem.addListener(SWT.Selection, event -> {
//            new NameVertexWindow(display, graph.getSelectVertex());
        });

        MenuItem changeItem = new MenuItem(menu,SWT.PUSH);
        changeItem.setText("Change orientation");

        changeItem.addListener(SWT.Selection, event -> {
            graph.getSelectArc().changeOrientation();
        });

        MenuItem deleteOrientationItem = new MenuItem(menu,SWT.PUSH);
        deleteOrientationItem.setText("Set unoriented");

        deleteOrientationItem.addListener(SWT.Selection, event -> {
            graph.getSelectArc().setOriented(false);
        });

        MenuItem setOrientedItem = new MenuItem(menu,SWT.PUSH);
        setOrientedItem.setText("Set oriented");

        setOrientedItem.addListener(SWT.Selection, event -> {
            graph.getSelectArc().setOriented(true);
        });

        MenuItem delItem = new MenuItem(menu, SWT.PUSH);
        delItem.setText("Delete");

        delItem.addListener(SWT.Selection, event -> {
            graph.deleteSelected();
        });

        arcMenu = menu;
    }

    private String openFileDialog(String type) {
        FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
        fileDialog.setText(type);
        fileDialog.setFilterPath("C:/");
        String[] filterExst = new String[1];
        filterExst[0] = "*.xml";
        fileDialog.setFilterExtensions(filterExst);
        return fileDialog.open();
    }

}
