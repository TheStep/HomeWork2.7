package ru.geekbrains.home.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> clients; //список пользователей

    public Server() {
        try {
            this.clients = new ArrayList<>();
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен ожидаем подключеник:  .. ");
            while (true) {
                Socket socket = serverSocket.accept(); //ждем подключения
                System.out.println("Новый клиент подключился ");
                new ClientHandler(this,socket);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler c) {
       clients.add(c); //добавление юзера
       broadCastMessage("Подключился новый юзер " + c.getUserName()); //оповещение входа юзера
    }

    public synchronized void unSubscribe(ClientHandler c) {
        clients.remove(c); //удаление юзера
        broadCastMessage(c.getUserName() + " отключился"); //оповещение выхода юзера
    }

    public synchronized void broadCastMessage(String message) { //отправка сообщений всем юзерам
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }
}
