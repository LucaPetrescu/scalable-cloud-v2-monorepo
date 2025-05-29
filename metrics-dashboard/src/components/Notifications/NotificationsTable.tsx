import React, { useState, useEffect } from 'react';
import { GoAlertFill } from 'react-icons/go';
import { Notification as NotificationType, AlertType } from '../../types/Notification.ts';
import { Notification } from './Notification.tsx';
import { notificationService } from '../../services/NotificationService.ts';

const STORAGE_KEY = 'metric_notifications';
const MAX_NOTIFICATIONS = 50;

export const NotificationsTable = () => {
    const [notifications, setNotifications] = useState(() => {
        const stored = localStorage.getItem(STORAGE_KEY);
        return (stored ? JSON.parse(stored) : []) as NotificationType[];
    });
    const [newNotificationIds, setNewNotificationIds] = useState(() => new Set<string>());

    useEffect(() => {
        const handleNotification = (notification: NotificationType) => {
            setNotifications((prev) => {
                const updated = [notification, ...prev].slice(0, MAX_NOTIFICATIONS);
                localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
                return updated;
            });
            // Add the new notification to the set of new notifications
            setNewNotificationIds((prev) => new Set([notification.timestamp.toString(), ...prev]));
            // Remove the notification from the new set after animation completes
            setTimeout(() => {
                setNewNotificationIds((prev) => {
                    const updated = new Set(prev);
                    updated.delete(notification.timestamp.toString());
                    return updated;
                });
            }, 300); // Match the animation duration
        };

        notificationService.on('notification', handleNotification);

        return () => {
            notificationService.off('notification', handleNotification);
        };
    }, []);

    const handleClearAll = () => {
        setNotifications([]);
        localStorage.removeItem(STORAGE_KEY);
        setNewNotificationIds(new Set());
    };

    return (
        <div className="col-span-12 p-4 rounded border border-stone-300">
            <div className="mb-4 flex items-center justify-between">
                <h3 className="flex items-center gap-1.5 font-medium">
                    <GoAlertFill /> Alerts
                </h3>
                <div className="flex items-center gap-2">
                    <button onClick={handleClearAll} className="text-sm text-stone-500 hover:text-stone-700">
                        Clear all
                    </button>
                </div>
            </div>
            <div className="overflow-x-auto">
                <table className="w-full">
                    <thead>
                        <tr className="border-b border-stone-200">
                            <th className="px-4 py-2 text-left text-sm font-medium text-stone-600">Alert Type</th>
                            <th className="px-4 py-2 text-left text-sm font-medium text-stone-600">Service</th>
                            <th className="px-4 py-2 text-left text-sm font-medium text-stone-600">Message</th>
                            <th className="px-4 py-2 text-left text-sm font-medium text-stone-600">Timestamp</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-stone-200">
                        {notifications.map((notification) => (
                            <Notification
                                key={notification.timestamp}
                                notification={notification}
                                isNew={newNotificationIds.has(notification.timestamp.toString())}
                            />
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
