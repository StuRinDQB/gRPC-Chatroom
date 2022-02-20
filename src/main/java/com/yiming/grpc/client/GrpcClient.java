package com.yiming.grpc.client;

import com.yiming.grpc.*;
import com.yiming.grpc.messageSts.MessageVo;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.sun.xml.internal.ws.resources.UtilMessages;

public class GrpcClient {
    private static Scanner input;
    private String username;
    private ManagedChannel channel;
    private String message;
    private int id;
    private static SendResponse[] sendResponse1;

    public static Scanner getInput() {
        return input;
    }

    public static void setInput(Scanner input) {
        GrpcClient.input = input;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static SendResponse[] getSendResponse1() {
        return sendResponse1;
    }

    public static void setSendResponse1(SendResponse[] sendResponse1) {
        GrpcClient.sendResponse1 = sendResponse1;
    }

    public GrpcClient(){

    }
    public GrpcClient(int id) throws Exception {
        this.id = id;
        System.out.println("Please input your name");
        input = new Scanner(System.in);
        username = input.next();
        channel = ManagedChannelBuilder.forAddress("localhost", 8082)
                .usePlaintext()
                .build();
        System.out.println("The Client " + id + " start");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Please input the sequence of client ");

        Scanner scanner = new Scanner(System.in);
        // Only input numbers
        int id = scanner.nextInt();
        int k = 0;
        final GrpcClient grpcClient = new GrpcClient(id);
        final HelloServiceGrpc.HelloServiceBlockingStub stub
                = HelloServiceGrpc.newBlockingStub(grpcClient.channel);


        getClientName(grpcClient, stub);
        System.out.println("Please sure  send messages 1 YES 2 NO");

        int n = input.nextInt();
        /**
         *
         * when n == 1,  we continue to execute, or we end this client
         */
        if (n == 1) {
            ExecutorService executorService = Executors.newCachedThreadPool();

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, MessageVo> hashMap = new HashMap<String, MessageVo>();
                    for (int i = 0; i < 1000000000; i++) {
                        try {
                            String receiveMessageFromServer = receiveMessageFromServer(grpcClient, stub);
                            MessageVo messageVo = new MessageVo();
                            String[] split = receiveMessageFromServer.split("<>");
                            messageVo.setMessage(split[0]);
                            messageVo.setTime(split[1]);
                            messageVo.setSts("0");
                            for (String key : hashMap.keySet()) {
                                if (key.equals(grpcClient.id + receiveMessageFromServer)) {
                                    messageVo.setSts("1");
                                }
                            }
                            hashMap.put(grpcClient.id + receiveMessageFromServer, messageVo);

                            if (messageVo.getSts().equals("0")) {
                                System.out.println("receive new message：" + split[0]);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
            });
            // when we have another message input, we send message to server
            while (input.hasNext()) {
                grpcClient.message = input.next();
                String A = sendMessageToServer(grpcClient, stub).toString();
                System.out.println(A);
            }
        } else {

            System.out.println("client end!：");
            System.exit(100);
        }

    }


    public static void getClientName(GrpcClient grpcClient, HelloServiceGrpc.HelloServiceBlockingStub stub) {

        HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
                .setName(grpcClient.username)
                .build());
        System.out.println(helloResponse.toString().substring(10));
    }

    public static String sendMessageToServer(GrpcClient grpcClient, HelloServiceGrpc.HelloServiceBlockingStub stub) {

        SendResponse sendResponse = stub.sendMessageToServer(SendRequest.newBuilder()
                .setMessage(grpcClient.message + "<>" + System.currentTimeMillis()).setId(grpcClient.id)
                .build());
        String receiveFromServer = "server response: " + sendResponse.getMessage();
        return receiveFromServer;
    }

    public static String receiveMessageFromServer(GrpcClient grpcClient, HelloServiceGrpc.HelloServiceBlockingStub stub) {
//        ClientReceive clientReceive = stub.receiveMessageFromServer(SendFromServer.newBuilder().setMessage("").build());
//        return clientReceive.getMessage();
        ClientReceive clientReceive = stub.receiveMessageFromServer(SendFromServer.newBuilder().setId(grpcClient.id).build());
        return clientReceive.getMessage();
    }

}
