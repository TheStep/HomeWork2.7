package ru.geekbrains.home.chat.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Controller {
    @FXML
    TextArea mainTextArea;
    @FXML
    TextField mainTextField, userNameField;
    @FXML
    HBox authPanel, msgPanel;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    public void clickMeBtnAction() {
        if (!mainTextField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(mainTextField.getText());
                mainTextField.clear();
                mainTextField.requestFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void tryToAuth() {
        connect();
        try {
            out.writeUTF("/auth " + userNameField.getText());
            userNameField.clear();
        } catch (IOException e) {
            showError("Невозможно установить соединение с сервером");
        }
    }

    public void connect() {
        if (socket != null && !socket.isClosed()) {
            return;
        }
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String inputMessage = in.readUTF();
                        if (inputMessage.startsWith("/authok ")) {
                            authPanel.setVisible(false);
                            authPanel.setManaged(false);
                            msgPanel.setVisible(true);
                            msgPanel.setManaged(true);

                            break;
                        }
                        mainTextArea.appendText(inputMessage + "\n");
                    }
                    while (true) {
                        String inputMessage = in.readUTF();
                        mainTextArea.appendText(inputMessage + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();
        } catch (IOException e) {
            showError("Сервер не отвечает....");
        }
    }

    public void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
