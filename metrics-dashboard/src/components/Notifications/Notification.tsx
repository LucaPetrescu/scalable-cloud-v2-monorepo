import React from 'react';
import { Notification as NotificationType, AlertType } from '../../types/Notification.ts';
import { GoAlertFill } from 'react-icons/go';

interface NotificationProps {
    notification: NotificationType;
    isNew?: boolean;
    key?: string | number;
}

const getAlertTypeColor = (type: AlertType): { text: string; bg: string } => {
    switch (type) {
        case AlertType.CRITICAL:
            return { text: 'text-red-500', bg: 'bg-red-50' };
        case AlertType.WARNING:
            return { text: 'text-yellow-500', bg: 'bg-yellow-50' };
        case AlertType.INFO:
            return { text: 'text-blue-500', bg: 'bg-blue-50' };
        default:
            return { text: 'text-gray-500', bg: 'bg-gray-50' };
    }
};

export const Notification = ({ notification, isNew = false }: NotificationProps) => {
    const { alertType, message, timestamp, service } = notification;
    const formattedDate = new Date(timestamp).toLocaleString();
    const colors = getAlertTypeColor(alertType);

    return (
        <tr className={`hover:bg-opacity-80 transition-colors ${colors.bg} ${isNew ? 'animate-fly-in' : ''}`}>
            <td className="px-4 py-3">
                <div className="flex items-center gap-2">
                    <div className={colors.text}>
                        <GoAlertFill size={16} />
                    </div>
                    <span className="capitalize">{alertType.toLowerCase()}</span>
                </div>
            </td>
            <td className="px-4 py-3 text-stone-600">{service}</td>
            <td className="px-4 py-3 text-stone-600">{message}</td>
            <td className="px-4 py-3 text-stone-500 text-sm">{formattedDate}</td>
        </tr>
    );
};
