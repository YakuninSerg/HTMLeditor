package com.javarush.task.task32.task3209;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

public class Controller {
    private View view;
    private HTMLDocument document;
    private File currentFile;


    public String getPlainText() {
        String result = null;//строка результата
        try (StringWriter stringWriter = new StringWriter()) {//Буфер для записи строки
            HTMLEditorKit htmlEditorKit = new HTMLEditorKit();//Хрень для работы с HTML текстом
            try {
                htmlEditorKit.write(stringWriter, document, 0, document.getLength());//запись в StringWriter документа от
                //начала и до конца
                result = stringWriter.toString();//получение резальтата
            } catch (IOException e) {
                ExceptionHandler.log(e);//логирование ошибок
            } catch (BadLocationException e) {
                ExceptionHandler.log(e);
            }
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
        return result; //возвращаем строку
    }

    public HTMLDocument getDocument() {//возвращает текущий документ
        return document;
    }

    public void setPlainText(String text) {//устанавливаем обычный текст
        resetDocument();//перезагружаем документ
        try (StringReader stringReader = new StringReader(text)) {//помещаем текст в буфер
            HTMLEditorKit htmlKit = new HTMLEditorKit();//хрень для работы с HTML
            try {
                htmlKit.read(stringReader, document, 0);//устанавливаем данные из буфера в документ с
                //самого начала документа
            } catch (IOException e) {//логируем ошибки
                ExceptionHandler.log(e);
            } catch (BadLocationException e) {
                ExceptionHandler.log(e);
            }
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }
//создаем новый документ
    public void createNewDocument() {
        view.selectHtmlTab(); //переходим на HTML вкладку
        resetDocument(); //перезагружаем документ
        view.setTitle("HTML редактор"); //заголовок окна
        view.resetUndo(); //перегружаем
        currentFile = null; //обнуляем текущий файл


    }
//открываем существующий документ
    public void openDocument() {
        //выбираем вкладку с HTML
        view.selectHtmlTab();
        JFileChooser fileChooser = new JFileChooser();//создаем окно для выбора файла
        fileChooser.setFileFilter(new HTMLFileFilter()); //фильтр для HTML файлов
        int index = fileChooser.showOpenDialog(view);//результат работы файл чузера
        if (index == JFileChooser.APPROVE_OPTION) {//если файл был выбран
            try {
                currentFile = fileChooser.getSelectedFile();//имя текущего файла
                resetDocument();//перезагружаем документ
                view.setTitle(currentFile.getName());//заголовок окна делаем именем файла
                try (FileReader fileReader = new FileReader(currentFile)) {//создаем поток для чтения из файла
                    HTMLEditorKit htmlKit = new HTMLEditorKit();//создаем хрень для работы с ХТМЛ
                    htmlKit.read(fileReader, document, 0);//считываем инфу из потока в документ
                    //с начальной позиции
                    view.resetUndo();//перезагружаем
                } catch (IOException e) {
                    ExceptionHandler.log(e);//логируем ошибки
                } catch (BadLocationException e) {
                    ExceptionHandler.log(e);
                }
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }

        }

    }
//сохраняем документ
    public void saveDocument() {
        view.selectHtmlTab();//открываем вкладку с ХТМЛ
        if (currentFile == null) saveDocumentAs();//если файл новый то сохраняем как
        else {
            try (FileWriter fileWriter = new FileWriter(currentFile)) {//создаем поток для записи в файл
                try {

                    HTMLEditorKit htmlEditorKit = new HTMLEditorKit();//хрень для работы с HTML
                    htmlEditorKit.write(fileWriter, document, 0, document.getLength());//записываем документ в
                    //файловый поток от начала и до конца
                } catch (IOException e) {//логируем ошибки
                    ExceptionHandler.log(e);
                } catch (BadLocationException e) {
                    ExceptionHandler.log(e);
                }
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }

        }

    }
//сохранить документ как
    public void saveDocumentAs() {
        view.selectHtmlTab();//переходим на вкладку ХТМЛ
        JFileChooser fileChooser = new JFileChooser();//создаем окно выбора файла
        fileChooser.setFileFilter(new HTMLFileFilter());//устанавливаем фильтр для файл чузера
        int index = fileChooser.showSaveDialog(view);//результат работы файл чузера
        if (index == JFileChooser.APPROVE_OPTION) {//если файл выбран
            currentFile = fileChooser.getSelectedFile();//устанавливаем имя текущего файла
            view.setTitle(currentFile.getName());//заголовок окна делаем как имя файла


            try (FileWriter fileWriter = new FileWriter(currentFile)) {//создаем поток для записи в файл
                try {
                    HTMLEditorKit htmlEditorKit = new HTMLEditorKit();//хрень для работы с ХТМЛ
                    htmlEditorKit.write(fileWriter, document, 0, document.getLength());//записываем в
                    //filewriter документ от начала и до конца
                } catch (IOException | BadLocationException e) {//логируем ошибки
                    ExceptionHandler.log(e);
                }
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }

    }
//перезагрузка документа
    public void resetDocument() {
        //если документ не пустой то удаляем текущий листенер изменений с документа
        if (document != null) document.removeUndoableEditListener(view.getUndoListener());
        HTMLEditorKit htmlKit = new HTMLEditorKit();//создаем хрень для работы с HTML
        document = (HTMLDocument) htmlKit.createDefaultDocument();//создаем новый HTML документ по умолчанию
        //добавляем слушатель редактирования текста на документ
        document.addUndoableEditListener(view.getUndoListener());
        view.update();

    }

    public Controller(View view) {//конструктор контролера, который связывает его с отображением
        this.view = view;
    }

    public void init() {//запуск - создание нового документа
        createNewDocument();
    }

    public void exit() {//выход из программы
        System.exit(0);
    }

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);
        view.init();
        controller.init();

    }
}
