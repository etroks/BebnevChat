package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private JTextField userUnputText;
    private JTextArea chatWindow; //информация
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket serverSocket;
    private Socket connection; //ip и port
    private String massage = "";

    public Server() {
        super("Серверная часть.");
        userUnputText = new JTextField();
        userUnputText.setEditable(false);
        userUnputText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userUnputText.setText("");
                    }
                }
        );
        add(userUnputText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 600);
        setVisible(true);
    }//настройка и запуск серврной части программы




        public void startServer () {
            try {
                serverSocket = new ServerSocket(7777, 10);
                while (true) {
                    try {
                        waitForConnection();
                        setupStreams();
                        whileChatting();
                    } catch (Exception exception) {
                        showMessage("\nРазрыв соединения.");
                    } finally {
                        closeConnection();
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }//ожидание соединения






    private void waitForConnection() throws IOException{
        showMessage("Ожидание подключения...\n");
        connection = serverSocket.accept();
        showMessage("Подключен клиент " + connection.getInetAddress().getHostName());
    }//настройка соединения



    private void setupStreams() throws IOException{
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\nПоток установлен");
    }//установка потоков

    private void whileChatting() throws IOException{
        massage = "Клиент " + connection.getInetAddress().getHostName() + " подключен.";
        sendMessage(massage);
        readyToType(true);
        do {
            try{
                massage = (String)inputStream.readObject();
                showMessage("\n" + massage);
            }
            catch (ClassNotFoundException classNotFoundException){
                showMessage("\nОшибка ввода");
            }
        }
        while (!massage.equals("КЛИЕНТ- *"));
    }//обработка сообщений


    private void closeConnection(){
        showMessage("\nКонец общения.");
        readyToType(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }//Конец общения



    private void sendMessage(String message){
        try{
            outputStream.writeObject("СЕРВЕР- " + message);
            outputStream.flush();
            showMessage("\nСЕРВЕР- " + message);
        }
        catch(IOException ioexception){
            chatWindow.append("\nОШИБКА ОТПРАВКИ");
        }
    }//отправка сообщений



    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        chatWindow.append(text);
                    }
                }
        );
    }//Обновление окна чата


    private void readyToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        userUnputText.setEditable(tof);
                    }
                }
        );
    }//Готовность к вводу
}
