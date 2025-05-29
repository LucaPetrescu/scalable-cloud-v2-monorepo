import { EventEmitter } from 'events';
import { Notification, AlertType } from '../types/Notification.ts';

class NotificationService extends EventEmitter {
    private static instance: NotificationService;

    private constructor() {
        super();
    }

    public static getInstance(): NotificationService {
        if (!NotificationService.instance) {
            NotificationService.instance = new NotificationService();
        }
        return NotificationService.instance;
    }

    public notifyMetricChange(service: string, metricName: string, oldValue: number, newValue: number) {
        const alertType = this.determineAlertType(oldValue, newValue);
        const notification: Notification = {
            service,
            alertType,
            message: `${metricName} threshold changed from ${oldValue} to ${newValue} for ${service}`,
            timestamp: Date.now(),
        };
        this.emit('notification', notification);
    }

    private determineAlertType(oldValue: number, newValue: number): AlertType {
        const difference = Math.abs(newValue - oldValue);
        if (difference > 50) return AlertType.CRITICAL;
        if (difference > 20) return AlertType.WARNING;
        return AlertType.INFO;
    }
}

export const notificationService = NotificationService.getInstance();
