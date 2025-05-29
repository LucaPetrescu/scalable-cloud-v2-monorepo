export interface Notification {
    alertType: AlertType;
    service: string;
    message: string;
    timestamp: number;
}

export enum AlertType {
    CRITICAL = 'CRITICAL',
    WARNING = 'WARNING',
    INFO = 'INFO',
}
