import React, { useState, useEffect } from 'react';
import { IoClose } from 'react-icons/io5';
import { useThresholds } from '../../hooks/thresholds/useThresholds.tsx';
import { useChangeThresholds } from '../../hooks/thresholds/useChangeThresholds.tsx';
import { notificationService } from '../../services/NotificationService.ts';

interface NetworkMetricsModalProps {
    isOpen: boolean;
    service: string;
    onClose: () => void;
}

interface Thresholds {
    httpRequestCount?: number;
    httpRequestDuration?: number;
}

export const NetworkMetricsModal = ({ service, isOpen, onClose }: NetworkMetricsModalProps) => {
    const [thresholds, setThresholds] = useState({});
    const allThresholds = useThresholds(service);
    const { changeThresholds } = useChangeThresholds();

    useEffect(() => {
        const newThresholds: Thresholds = {};
        for (const threshold of allThresholds) {
            if (threshold.name === 'http_requests_total') {
                newThresholds.httpRequestCount = threshold.max;
            }
            if (threshold.name === 'http_request_duration_seconds') {
                newThresholds.httpRequestDuration = threshold.max;
            }
        }
        setThresholds(newThresholds);
    }, [allThresholds]);

    if (!isOpen) return null;

    const handleThresholdChange = (metric: keyof typeof thresholds, value: number) => {
        setThresholds((prev) => ({
            ...prev,
            [metric]: value,
        }));
    };

    const handleSave = async () => {
        try {
            const newRules = [
                { metricName: 'http_requests_total', value: thresholds.httpRequestCount || 0 },
                { metricName: 'http_request_duration_seconds', value: thresholds.httpRequestDuration || 0 },
            ];

            // Emit notifications for each changed threshold
            for (const threshold of allThresholds) {
                if (threshold.name === 'http_requests_total' && threshold.max !== thresholds.httpRequestCount) {
                    notificationService.notifyMetricChange(
                        service,
                        'HTTP Request Count',
                        threshold.max,
                        thresholds.httpRequestCount || 0,
                    );
                }
                if (
                    threshold.name === 'http_request_duration_seconds' &&
                    threshold.max !== thresholds.httpRequestDuration
                ) {
                    notificationService.notifyMetricChange(
                        service,
                        'HTTP Request Duration',
                        threshold.max,
                        thresholds.httpRequestDuration || 0,
                    );
                }
            }

            await changeThresholds(service, newRules);
            onClose();
        } catch (error) {
            console.error('Error saving thresholds:', error);
            // You might want to show an error message to the user here
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl">
                <div className="flex items-center justify-between p-4 border-b border-stone-200">
                    <h2 className="text-xl font-semibold text-stone-800">{service} Metrics Thresholds</h2>
                    <button onClick={onClose} className="text-stone-500 hover:text-stone-700 transition-colors">
                        <IoClose size={24} />
                    </button>
                </div>
                <div className="p-6 space-y-6">
                    <div className="space-y-4">
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">HTTP Request Count Threshold (%)</label>
                            <input
                                type="number"
                                value={thresholds.httpRequestCount}
                                onChange={(e) => handleThresholdChange('httpRequestCount', parseInt(e.target.value))}
                                className="w-24 px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-500"
                            />
                        </div>
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">HTTP Request Duration Threshold (%)</label>
                            <input
                                type="number"
                                value={thresholds.httpRequestDuration}
                                onChange={(e) => handleThresholdChange('httpRequestDuration', parseInt(e.target.value))}
                                className="w-24 px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-500"
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-3 pt-4 border-t border-stone-200">
                        <button
                            onClick={onClose}
                            className="px-4 py-2 text-stone-600 hover:text-stone-800 transition-colors"
                        >
                            Cancel
                        </button>
                        <button
                            onClick={handleSave}
                            className="px-4 py-2 bg-violet-600 text-white rounded-lg hover:bg-violet-700 transition-colors"
                        >
                            Save Changes
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};
