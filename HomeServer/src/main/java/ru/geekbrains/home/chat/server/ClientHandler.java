package ru.geekbrains.home.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String userName;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream()); //входящее
            this.out = new DataOutputStream(socket.getOutputStream()); //выходящее
            new Thread(() -> {
                try {
                    while (true) {
                        String inputMessage = in.readUTF();
                        if (inputMessage.startsWith("/auth ")) {
                            userName = inputMessage.split("\\s")[1]; //определение никнейма при авторизации
                            sendMessage("/authok "); //подверждение авторизации
                            sendMessage("Вы зашли в чат по именем: " + userName);
                            server.subscribe(this); //добавление в список подписчиков
                            break;
                        } else {
                            sendMessage("Server : Необходима авторизация");
                        }
                    }
                    while (true) {  //отключаем возможность вызова команд
                        String inputMessage = in.readUTF();
                        if (inputMessage.startsWith("/")) {
                            continue;

                        }
                        server.broadCastMessage(userName + ": " + inputMessage); //отображение сообщения всем юзерам
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unSubscribe(this);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message); //отправка сообщений
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() { //геттер для вызова имени в сервере
        return userName;
    }
}
