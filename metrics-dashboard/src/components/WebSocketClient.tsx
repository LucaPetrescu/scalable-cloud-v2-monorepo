import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { notificationService } from '../services/NotificationService.ts';
import { AlertType } from '../types/Notification.ts';

const WebSocketClient: React.FC = () => {
    const [isConnected, setIsConnected] = useState(false);
    const [retryCount, setRetryCount] = useState(0);
    const MAX_RETRIES = 5;
    const RETRY_DELAY = 3000; // 3 seconds

    useEffect(() => {
        const connectWebSocket = () => {
            console.log('Attempting to connect to WebSocket...');
            const wsUrl = 'http://localhost:8085/ws';
            const socket = new SockJS(wsUrl);
            const stompClient = new Client({
                webSocketFactory: () => socket,
                onConnect: () => {
                    console.log('Successfully connected to WebSocket');
                    setIsConnected(true);
                    setRetryCount(0);

                    console.log('Subscribing to /topic/alerts...');
                    stompClient.subscribe('/topic/alerts', (message) => {
                        try {
                            const data = JSON.parse(message.body);
                            console.log('Received WebSocket alert:', data);

                            const notification = {
                                alertType: AlertType.CRITICAL,
                                service: data.serviceName || 'Unknown Service',
                                message: `${data.alert?.metricName || 'Unknown Metric'} threshold exceeded (${data.alert?.metricValue || 'Unknown Value'}) for ${data.serviceName || 'Unknown Service'}`,
                                timestamp: Date.now(),
                            };

                            console.log('Emitting notification:', notification);
                            notificationService.emit('notification', notification);
                        } catch (error) {
                            console.error('Error parsing WebSocket message:', error);
                            console.error('Raw message:', message.body);
                        }
                    });
                    console.log('Successfully subscribed to /topic/alerts');
                },
                onDisconnect: () => {
                    console.log('WebSocket disconnected');
                    setIsConnected(false);

                    if (retryCount < MAX_RETRIES) {
                        console.log(`Attempting to reconnect (${retryCount + 1}/${MAX_RETRIES})...`);
                        setTimeout(() => {
                            setRetryCount((prev) => prev + 1);
                            connectWebSocket();
                        }, RETRY_DELAY);
                    } else {
                        console.error('Max reconnection attempts reached');
                    }
                },
                onStompError: (frame) => {
                    console.error('STOMP error:', frame);
                    console.error('STOMP error headers:', frame.headers);
                    console.error('STOMP error body:', frame.body);
                    setIsConnected(false);
                },
                connectHeaders: {
                    // Add any authentication headers if needed
                },
                debug: (str) => {
                    console.log('STOMP Debug:', str);
                },
            });

            console.log('Activating STOMP client...');
            stompClient.activate();

            return () => {
                console.log('Deactivating STOMP client...');
                stompClient.deactivate();
            };
        };

        const cleanup = connectWebSocket();
        return cleanup;
    }, [retryCount]);

    return null;
};

export default WebSocketClient;
