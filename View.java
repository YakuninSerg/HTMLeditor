package com.javarush.task.task32.task3209;

import com.javarush.task.task32.task3209.listeners.FrameListener;
import com.javarush.task.task32.task3209.listeners.TabbedPaneChangeListener;
import com.javarush.task.task32.task3209.listeners.UndoListener;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame implements ActionListener {
    private Controller controller;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextPane htmlTextPane = new JTextPane();
    private JEditorPane plainTextPane = new JEditorPane();
    private UndoManager undoManager = new UndoManager();
    private UndoListener undoListener = new UndoListener(undoManager);


    public void update() {//обновляем документ, вставляем его из контролера
        htmlTextPane.setDocument(controller.getDocument());

    }

    public void showAbout() {
        JOptionPane.showMessageDialog(
                this,
                "Программу создал Якунин Сергей, обучаясь на JAVARUSH",
                "Информация о программе",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void selectHtmlTab() {
        tabbedPane.setSelectedIndex(0);//выбираем ХТМЛ вкладку
        resetUndo();//обнуляем менеджер изменений

    }

    public boolean isHtmlTabSelected() {//выбрана ли ХТМЛ вкладка
        if (tabbedPane.getSelectedIndex() == 0) return true;
        else return false;
    }

    public void resetUndo() {//обнуляем менеджер изменений
        undoManager.discardAllEdits();
    }

    public boolean canUndo() {
        return undoManager.canUndo();//можно ли отменить действие
    }

    public UndoListener getUndoListener() {//слушатель менеджера изменений
        return undoListener;
    }

    public boolean canRedo() {//можно ли повторить операцию
        return undoManager.canRedo();

    }

    public void undo() { //отмена последней операции
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            ExceptionHandler.log(e);
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    public void redo() {//повтор последней операции
        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            ExceptionHandler.log(e);
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }

    }

    public View() {
        try {
            String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
            // устанавливаем LookAndFeel
            UIManager.setLookAndFeel(systemLookAndFeelClassName);
        } catch (UnsupportedLookAndFeelException e) {
            ExceptionHandler.log(e);
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    public void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        MenuHelper.initFileMenu(this, menuBar);
        MenuHelper.initEditMenu(this, menuBar);
        MenuHelper.initStyleMenu(this, menuBar);
        MenuHelper.initAlignMenu(this, menuBar);
        MenuHelper.initColorMenu(this, menuBar);
        MenuHelper.initFontMenu(this, menuBar);
        MenuHelper.initHelpMenu(this, menuBar);
        getContentPane().add(menuBar, BorderLayout.NORTH);

    }

    public void initEditor() {
        htmlTextPane.setContentType("text/html");
        JScrollPane scrollPaneHtml = new JScrollPane(htmlTextPane);
        tabbedPane.addTab("HTML", scrollPaneHtml);
        JScrollPane scrollPaneText = new JScrollPane(plainTextPane);
        tabbedPane.addTab("Текст", scrollPaneText);
        Dimension size = new Dimension(500, 500);
        tabbedPane.setPreferredSize(size);
        TabbedPaneChangeListener tabbedPaneChangeListener = new TabbedPaneChangeListener(this);
        tabbedPane.addChangeListener(tabbedPaneChangeListener);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);


    }

    public void initGui() {
        initMenuBar();
        initEditor();
        pack();
    }


    public void selectedTabChanged() {
        if (tabbedPane.getSelectedIndex() == 0) {
            String text = plainTextPane.getText();
            controller.setPlainText(text);
        } else {
            String text = controller.getPlainText();
            plainTextPane.setText(text);
        }
        resetUndo();
    }

    public void exit() {
        controller.exit();
    }

    public void init() {
        initGui();
        FrameListener frameListener = new FrameListener(this);
        addWindowListener(frameListener);
        setVisible(true);
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Новый".equals(command)) controller.createNewDocument();
        if ("Открыть".equals(command)) controller.openDocument();
        if ("Сохранить".equals(command)) controller.saveDocument();
        if ("Сохранить как...".equals(command)) controller.saveDocumentAs();
        if ("Выход".equals(command)) controller.exit();
        if ("О программе".equals(command)) showAbout();
    }


}
