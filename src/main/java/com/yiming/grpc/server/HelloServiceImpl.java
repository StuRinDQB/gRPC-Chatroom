package com.yiming.grpc.server;

import com.yiming.grpc.*;
import com.yiming.grpc.HelloServiceGrpc.HelloServiceImplBase;

import com.yiming.grpc.client.GrpcClient;
import io.grpc.stub.StreamObserver;
import io.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HelloServiceImpl extends HelloServiceImplBase {
    //    private List<String> message = new ArrayList<>();
//    private List<String> record = new ArrayList<>();
//    private Map<Boolean,Map<Integer, String>> map = new HashMap<>();
    private Map<Integer, String> map = new HashMap<>();
    HashMap<Pair<Integer, String>, Boolean> store = new HashMap<>();

    int k = 1;

    @Override
    public void hello(
            HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        System.out.println("User online " + request.toString());
        String greeting = new StringBuilder().append("Hello, ")
                .append(request.getName())
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sendMessageToServer(SendRequest request, StreamObserver<SendResponse> responseObserver) {
        System.out.println("Message received from client:\n" + request);
        map.put(request.getId(), new StringBuilder().append(request.getMessage()).toString());


        for (int A : map.keySet()) {
            if (A == request.getId()) {
                Pair<Integer, String> pair = new Pair<>(request.getId(), map.get(request.getId()));
                store.put(pair, true);
            } else {
                Pair<Integer, String> pair = new Pair<>(request.getId(), map.get(request.getId()));
                store.put(pair, false);
            }
        }


        SendResponse response = SendResponse.newBuilder()
                .setMessage(store.toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void receiveMessageFromServer(SendFromServer request, StreamObserver<ClientReceive> responseObserver) {
        int id = request.getId();

        for (Integer clientID : map.keySet()) {
            if (clientID != id) {
                ClientReceive clientReceive = ClientReceive.newBuilder().setMessage(map.get(clientID)).build();
                responseObserver.onNext(clientReceive);
                responseObserver.onCompleted();
//                try {
//                    GrpcClient grpcClient = new GrpcClient();
//                    grpcClient.setId(clientID);
//                    HelloServiceGrpc.HelloServiceBlockingStub stub
//                            = HelloServiceGrpc.newBlockingStub(grpcClient.getChannel());
//                    stub.receiveMessageFromServer(SendFromServer.newBuilder().setId(clientID).build());
//                } catch (Exception e) {
//
//                }

            }
        }
    }
}