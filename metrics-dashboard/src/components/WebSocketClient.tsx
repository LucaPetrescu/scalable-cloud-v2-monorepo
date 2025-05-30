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
            const wsUrl = process.env.REACT_APP_WS_URL || 'http://localhost:8085/ws';
            const socket = new SockJS(wsUrl);
            const stompClient = new Client({
                webSocketFactory: () => socket,
                onConnect: () => {
                    console.log('Connected to WebSocket');
                    setIsConnected(true);
                    setRetryCount(0);

                    stompClient.subscribe('/topic/alerts', (message) => {
                        try {
                            const data = JSON.parse(message.body);
                            console.log('Received alert:', data);

                            // Create a notification with CRITICAL type for dummy alerts
                            const notification = {
                                alertType: AlertType.CRITICAL,
                                service: data.serviceName || 'Unknown Service',
                                message: data.alert?.reason || 'Dummy Alert',
                                timestamp: Date.now(),
                            };

                            notificationService.emit('notification', notification);
                        } catch (error) {
                            console.error('Error parsing WebSocket message:', error);
                        }
                    });
                },
                onDisconnect: () => {
                    console.log('Disconnected from WebSocket');
                    setIsConnected(false);

                    // Attempt to reconnect if we haven't exceeded max retries
                    if (retryCount < MAX_RETRIES) {
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
                    setIsConnected(false);
                },
                connectHeaders: {
                    // Add any authentication headers if needed
                },
                debug: (str) => {
                    if (process.env.NODE_ENV === 'development') {
                        console.log('STOMP Debug:', str);
                    }
                },
            });

            stompClient.activate();

            return () => {
                stompClient.deactivate();
            };
        };

        const cleanup = connectWebSocket();
        return cleanup;
    }, [retryCount]);

    return null;
};

export default WebSocketClient;
