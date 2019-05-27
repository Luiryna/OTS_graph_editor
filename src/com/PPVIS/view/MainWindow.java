package com.PPVIS.view;
import com.PPVIS.model.Product;

import com.PPVIS.algoritm.Algorithm;
import com.PPVIS.controller.Controller;
import com.PPVIS.Main;
import com.PPVIS.model.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainWindow {
    private Display display;
    private Shell shell;
    private TabFolder tabFolder;
    private Color defaultColor;
    private Menu arcMenu;
    private Menu vertexMenu;
    private Map<TabItem, Graph> mapTabItem = new HashMap<>();
    private Canvas canvas;
    private TypeOperation typeOperation;
    private Graph graph;
    private Vertex ingoing;
    private Vertex outgoing;
    private Arc arcCreate;
    private boolean hasSelectVertex = false;
    private Vertex start;

    public MainWindow() {
        display = new Display();
        shell = new Shell(display);
        defaultColor = new Color(null, 255, 247, 247);
        shell.setModified(false);
        shell.setSize(1920, 1080);
        shell.setBackground(defaultColor);
        shell.setText("GraphEditor");
        shell.setImage(new Image(display, "img/iconLogo.png"));
        initLayout();
        initMenuBar();
        initToolBar();
        vertexMenuCanvas();
        arcMenuCanvas();
        tabFolder = new TabFolder(shell, SWT.BORDER);
        GridData gridDataTab = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridDataTab.grabExcessHorizontalSpace = true;
        tabFolder.setLayoutData(gridDataTab);
        initMenuTab();
        tabFolder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (mapTabItem.size() != 0)
                    changeTab(tabFolder.getSelection()[0]);
            }
        });
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public void addTab(String name) {
        TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
        initCanvas();
        tabItem.setControl(canvas);
        Graph graph = new Graph(name, canvas);
        this.graph = graph;
        mapTabItem.put(tabItem, graph);
        tabItem.setText(graph.getName());
        typeOperation = TypeOperation.CLICK;
        hasSelectVertex = false;
        ingoing = null;
        outgoing = null;
        arcCreate = null;
        tabFolder.setSelection(tabItem);

    }

    public void addTab(Graph graph) {
        TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
        tabItem.setControl(canvas);
        this.graph = graph;
        mapTabItem.put(tabItem, graph);
        tabItem.setText(graph.getName());
        typeOperation = TypeOperation.CLICK;
        hasSelectVertex = false;
        ingoing = null;
        outgoing = null;
        arcCreate = null;
        tabFolder.setSelection(tabItem);
    }

    private void changeTab(TabItem tabItem) {
        if (tabItem != null) {
            graph = mapTabItem.get(tabItem);
            typeOperation = TypeOperation.CLICK;
            hasSelectVertex = false;
            ingoing = null;
            outgoing = null;
            arcCreate = null;
            canvas = graph.getCanvas();
            start = null;
        }
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

        MenuItem menuItemAdd = new MenuItem(fileMenuDrop, SWT.PUSH);
        menuItemAdd.setText("Add graph");
        menuItemAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                Rectangle rectangle = shell.getBounds();
                new GraphWindow(display, MainWindow.this, (rectangle.x + rectangle.width) / 2, (rectangle.y + rectangle.height) / 2);
            }
        });

        MenuItem saveItem = new MenuItem(fileMenuDrop, SWT.PUSH);
        saveItem.setText("Save");
        saveItem.setAccelerator(SWT.CTRL + 'S');

        saveItem.addSelectionListener(new SelectionAdapter() {
            MessageBox messageBox = new MessageBox(shell);

            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                File file;
                try {
                    FileDialog fileDialog = openFileDialog("Save");
                    fileDialog.setFileName(graph.getName());
                    file = new File(fileDialog.open());
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
                    tabFolder.getItem(tabFolder.getSelectionIndex()).setText(file.getName().substring(0, file.getName().indexOf('.')));
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
                    FileDialog fileDialog = openFileDialog("Open");
                    file = new File(fileDialog.open());
                } catch (NullPointerException ex) {
                    return;
                }
                Canvas canvas = MainWindow.this.canvas;
                initCanvas();
                Graph graph = Controller.getInstance().open(file, MainWindow.this.canvas);
                if (graph == null) {
                    messageBox.setMessage("Ошибка чтения");
                    messageBox.open();
                    MainWindow.this.canvas = canvas;
                } else {
                    addTab(graph);
                    graph.getCanvas().redraw();
                }
            }
        });

        MenuItem menuItemExit = new MenuItem(fileMenuDrop, SWT.PUSH);
        menuItemExit.setText("Exit");
        menuItemExit.addListener(SWT.Selection, event -> {
            shell.close();
            display.dispose();
        });

        MenuItem menuItemEdit = new MenuItem(menuBar, SWT.CASCADE);
        menuItemEdit.setText("Edit");

        Menu menuEdit = new Menu(shell, SWT.DROP_DOWN);
        menuItemEdit.setMenu(menuEdit);

        MenuItem menuItemClick = new MenuItem(menuEdit, SWT.PUSH);
        menuItemClick.setText("Click");
        menuItemClick.addListener(SWT.Selection, event -> {
            typeOperation = TypeOperation.CLICK;
        });

        MenuItem menuItemAddVertex = new MenuItem(menuEdit, SWT.PUSH);
        menuItemAddVertex.setText("Add vertex");
        menuItemAddVertex.addListener(SWT.Selection, event -> {
            typeOperation = TypeOperation.ADD_VERTEX;
        });

        MenuItem menuItemAddArc = new MenuItem(menuEdit, SWT.PUSH);
        menuItemAddArc.setText("Add arc");
        menuItemAddArc.addListener(SWT.Selection, event -> {
            typeOperation = TypeOperation.ADD_ARC;
        });

        MenuItem menuItemAlgo = new MenuItem(menuBar, SWT.CASCADE);
        menuItemAlgo.setText("Algorithm");

        Menu menuAlgo = new Menu(shell, SWT.DROP_DOWN);
        menuItemAlgo.setMenu(menuAlgo);

        MenuItem menuItemFindShort = new MenuItem(menuAlgo, SWT.CASCADE);
        menuItemFindShort.setText("Distance");

        Menu menuFindShort = new Menu(shell, SWT.DROP_DOWN);
        menuItemFindShort.setMenu(menuFindShort);

        MenuItem menuItemStart = new MenuItem(menuFindShort, SWT.PUSH);
        menuItemStart.setText("Start vertex");
        menuItemStart.addListener(SWT.Selection, event -> {
            typeOperation = TypeOperation.ADD_START_VERTEX;
        });

        MenuItem menuItemFind = new MenuItem(menuFindShort, SWT.PUSH);
        menuItemFind.setText("Find");
        menuItemFind.addListener(SWT.Selection, event -> {
            if (start != null && graph != null) {
                Map<Vertex, Integer> map = Algorithm.findDistance(graph, start);
                for (Map.Entry<Vertex, Integer> entry : map.entrySet()) {
                    entry.getKey().setDistance(map.get(entry.getKey()));
                }
            }
        });

        MenuItem menuItemInfo = new MenuItem(menuBar, SWT.CASCADE);
        menuItemInfo.setText("Info");

        Menu menuInfo = new Menu(shell, SWT.DROP_DOWN);
        menuItemInfo.setMenu(menuInfo);

        MenuItem menuItemInfoGraph = new MenuItem(menuInfo, SWT.PUSH);
        menuItemInfoGraph.setText("Info graph");
        menuItemInfoGraph.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                if (graph != null) {
                    MessageBox messageBox = new MessageBox(shell, SWT.OK);
                    messageBox.setText("Info graph");
                    messageBox.setMessage("Graph name " + graph.getName() + '\n' + "Arcs " + graph.getArcs().size() + '\n' + "Vertices " + graph.getVertices().size());
                    messageBox.open();
                }
            }
        });

        MenuItem menuItemInfoMatrice = new MenuItem(menuInfo, SWT.PUSH);
        menuItemInfoMatrice.setText("Матрица смежности");
        menuItemInfoMatrice.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                if (graph != null) {
                    MessageBox messageBox = new MessageBox(shell, SWT.OK);
                    int[][] matrix = graph.getAdjacencyMatrix();
                    String matrixString = "";
                    for (int i  = 0; i < matrix.length; i++) {
                        for (int j = 0; j < matrix[0].length; j++) {
                            matrixString = matrixString.concat(Integer.toString(matrix[i][j]));
                        }
                        matrixString = matrixString.concat("\n");
                    }
                    matrixString = matrixString.concat("Диаметр графа:\n");
                    matrixString = matrixString.concat("Радиус графа: \n");
                    HamiltonianCycle cycle = new HamiltonianCycle();
                    cycle.findHamiltonianCycle(matrix);
                    messageBox.setText("Матрица смежности");
                    messageBox.setMessage(matrixString);
                    messageBox.open();
                }
            }
        });

        MenuItem menuItemHamiltonianCycle = new MenuItem(menuInfo, SWT.PUSH);
        menuItemHamiltonianCycle.setText("Найти гамильтонов цикл");
        menuItemHamiltonianCycle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                if (graph != null) {
                    MessageBox messageBox = new MessageBox(shell, SWT.OK);
                    int[][] matrix = graph.getAdjacencyMatrix();
                    HamiltonianCycle cycle = new HamiltonianCycle();
                    messageBox.setText("Гамильтонов цикл");
                    messageBox.setMessage(cycle.findHamiltonianCycle(matrix));
                    messageBox.open();
                }
            }
        });

        MenuItem createSvyaznyGraph = new MenuItem(menuInfo, SWT.PUSH);
        createSvyaznyGraph.setText("Привести граф к связному");
        createSvyaznyGraph.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                if (graph != null) {
                    for (int i = 0; i < graph.getVertices().size(); i++) {
                        Vertex vertex = graph.getVertices().get(i);
                        Vertex next;
                        if (i != graph.getVertices().size() - 1) {
                            next = graph.getVertices().get(i + 1);
                        } else {
                            next = graph.getVertices().get(0);
                        }
                        if (graph.getArcs().stream().anyMatch(t -> (t.getIngoing().getID() == vertex.getID()) && (t.getOutgoing().getID() == next.getID()))) {

                        } else {
                            graph.addArc(vertex, next);
                        }
                    }
                    MessageBox messageBox = new MessageBox(shell, SWT.OK);
                    messageBox.setText("Преобразование графа к связному");
                    messageBox.setMessage("Граф преобразован");
                    messageBox.open();
                }
            }
        });

        MenuItem openTwoGraphs = new MenuItem(menuInfo, SWT.PUSH);
        openTwoGraphs.setText("Открыть два графа для произведения");
        openTwoGraphs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                FileDialog dlg = new FileDialog(shell, SWT.MULTI);
                Collection files = new ArrayList();
                if (dlg.open() != null) {
                    String[] names = dlg.getFileNames();
                    Canvas canvas = MainWindow.this.canvas;
                    initCanvas();
                    File file1 = new File(names[0]);
                    File file2 = new File(names[1]);
                    Graph first = Controller.getInstance().open(file1, MainWindow.this.canvas);
                    Graph second = Controller.getInstance().open(file2, MainWindow.this.canvas);
                    Product product = new Product(first, second);
                    graph = product.createCopyGraph();
                    addTab(graph);
                    graph.getCanvas().redraw();

//                    for (int i = 0, n = names.length; i < n; i++) {
//                        StringBuffer buf = new StringBuffer(dlg.getFilterPath());
//                        if (buf.charAt(buf.length() - 1) != File.separatorChar)
//                            buf.append(File.separatorChar);
//                        buf.append(names[i]);
//                        files.add(buf.toString());
//                    }

                }
            }
        });

//        MenuItem createCartesianProduct = new MenuItem(menuInfo, SWT.PUSH);
//        createCartesianProduct.setText("Декартово произведение графа");
//        createCartesianProduct.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent selectionEvent) {
//                Product product = new Product();
//                Graph cartesianProduct = product.cartesianProduct(graph, graph);
//                graph = cartesianProduct;
//                System.out.println(cartesianProduct);
//            }
//
//        });
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
        canvas = new Canvas(tabFolder, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
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
                if (start != null) {
                    start.deselect();
                    for (Vertex vertex : graph.getVertices())
                        vertex.setDistance(-1);
                    start = null;
                }
                if (mouseEvent.button == 3) {
                    graph.select(graph.findVertex(mouseEvent.x, mouseEvent.y));
                    if (graph.getSelectVertex() == null) {
                        graph.select(graph.findArc(mouseEvent.x, mouseEvent.y));
                        if (graph.getSelectArc() != null)
                            canvas.setMenu(arcMenu);
                    } else {
                        canvas.setMenu(vertexMenu);
                    }
                } else {
                    if (typeOperation == TypeOperation.CLICK) {
                        graph.select(graph.findVertex(mouseEvent.x, mouseEvent.y));
                        if (graph.getSelectVertex() == null)
                            graph.select(graph.findArc(mouseEvent.x, mouseEvent.y));
                        else hasSelectVertex = true;
                    } else {
//                        вот тут добавляется ребро
                        if (typeOperation == TypeOperation.ADD_ARC) {
                            outgoing = graph.findVertex(mouseEvent.x, mouseEvent.y);
                            if (outgoing != null)
                                arcCreate = graph.addArc(outgoing, outgoing.getX(), outgoing.getY());
                        } else {
                            if (typeOperation == TypeOperation.ADD_START_VERTEX) {
                                start = graph.findVertex(mouseEvent.x, mouseEvent.y);
                                if (start != null) start.setDefaultColor(new Color(null, 255, 10, 10));
                            }
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
                        if (rect.y - 25 <= mouseEvent.y && rect.x <= mouseEvent.x && rect.x + rect.width - 15 >= mouseEvent.x && rect.y + rect.height - 50 >= mouseEvent.y)
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
                if (keyEvent.keyCode == 127) {
                    graph.deleteSelected();
                    return;
                }
                if (keyEvent.keyCode == 105) {
                    Rectangle rectangle = canvas.getBounds();
                    if (graph.getSelectVertex() != null)
                        new NameVertexWindow(display, graph.getSelectVertex(), (rectangle.x + rectangle.width) / 2, (rectangle.y + rectangle.height) / 2);
                    if (graph.getSelectArc() != null)
                        new WeightArcWindow(display, graph.getSelectArc(), (rectangle.x + rectangle.width) / 2, (rectangle.y + rectangle.height) / 2);
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
                if (keyEvent.keyCode == 99) {
                    if (graph.getSelectArc() != null)
                        graph.getSelectArc().setOriented(!graph.getSelectArc().isOriented());
                }
            }
        });
    }

    private void vertexMenuCanvas() {
        Menu menu = new Menu(shell, SWT.POP_UP);

        MenuItem setTextItem = new MenuItem(menu, SWT.PUSH);
        setTextItem.setText("Set text");

        setTextItem.addListener(SWT.Selection, event -> {
            Rectangle rectangle = shell.getBounds();
            new NameVertexWindow(display, graph.getSelectVertex(), (rectangle.x + rectangle.width) / 2, (rectangle.y + rectangle.height) / 2);
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
            Rectangle rectangle = shell.getBounds();
            new WeightArcWindow(display, graph.getSelectArc(), (rectangle.x + rectangle.width) / 2, (rectangle.y + rectangle.height) / 2);
        });

        MenuItem changeItem = new MenuItem(menu, SWT.PUSH);
        changeItem.setText("Change orientation");

        changeItem.addListener(SWT.Selection, event -> {
            graph.getSelectArc().changeOrientation();
        });

        MenuItem deleteOrientationItem = new MenuItem(menu, SWT.PUSH);
        deleteOrientationItem.setText("Set unoriented");

        deleteOrientationItem.addListener(SWT.Selection, event -> {
            graph.getSelectArc().setOriented(false);
        });

        MenuItem setOrientedItem = new MenuItem(menu, SWT.PUSH);
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

    private void initMenuTab() {
        Menu menu = new Menu(shell, SWT.POP_UP);

        MenuItem setTextItem = new MenuItem(menu, SWT.PUSH);
        setTextItem.setText("Add");

        setTextItem.addListener(SWT.Selection, event -> {
            Rectangle rectangle = shell.getBounds();
            new GraphWindow(display, MainWindow.this, (rectangle.x + rectangle.width) / 2, (rectangle.y + rectangle.height) / 2);
        });

        MenuItem delItem = new MenuItem(menu, SWT.PUSH);
        delItem.setText("Delete");

        delItem.addListener(SWT.Selection, event -> {
            TabItem tabItem = tabFolder.getSelection()[0];
            mapTabItem.remove(tabItem);
            canvas = null;
            graph = null;
            typeOperation = TypeOperation.CLICK;
            hasSelectVertex = false;
            ingoing = null;
            outgoing = null;
            arcCreate = null;
            start = null;
            tabItem.dispose();
            if (mapTabItem.size() > 0)
                changeTab(tabFolder.getItem(0));
        });
        tabFolder.setMenu(menu);

    }

    private FileDialog openFileDialog(String type) {
        FileDialog fileDialog = new FileDialog(shell, "Save".equals(type) ? SWT.SAVE : SWT.OPEN);
        fileDialog.setText(type);
        fileDialog.setFilterPath("C:/");
        String[] filterExst = new String[1];
        filterExst[0] = "*.xml";
        fileDialog.setFilterExtensions(filterExst);
        return fileDialog;
    }

}
