import React, { useState, useEffect } from 'react';
import { IoClose } from 'react-icons/io5';
import { useThresholds } from '../../hooks/thresholds/useThresholds.tsx';

interface DatabaseMetricsModalProps {
    isOpen: boolean;
    service: string;
    onClose: () => void;
}

interface Thresholds {
    mongoConnectionPoolSize?: number;
    mongoActiveConnections?: number;
    mongoAvailableConnections?: number;
    mongoQueryTime?: number;
    mongoMemoryUsage?: number;
}

export const DatabaseMetricsModal = ({ service, isOpen, onClose }: DatabaseMetricsModalProps) => {
    const [thresholds, setThresholds] = useState<Thresholds>({});
    const allThresholds = useThresholds(service);

    useEffect(() => {
        const newThresholds: Thresholds = {};
        for (const threshold of allThresholds) {
            if (threshold.name === 'mongo_connection_pool_size') {
                newThresholds.mongoConnectionPoolSize = threshold.max;
            }
            if (threshold.name === 'mongo_active_connections') {
                newThresholds.mongoActiveConnections = threshold.max;
            }
            if (threshold.name === 'mongo_available_connections') {
                newThresholds.mongoAvailableConnections = threshold.max;
            }
            if (threshold.name === 'mongo_query_time_seconds') {
                newThresholds.mongoQueryTime = threshold.max;
            }
            if (threshold.name === 'mongo_memory_usage_bytes') {
                newThresholds.mongoMemoryUsage = threshold.max;
            }
        }
        setThresholds(newThresholds);
    }, [allThresholds]);

    if (!isOpen) return null;

    const handleThresholdChange = (metric: keyof Thresholds, value: number) => {
        setThresholds((prev) => ({
            ...prev,
            [metric]: value,
        }));
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
                            <label className="text-stone-700 font-medium">Connection Pool Size (%)</label>
                            <input
                                type="text"
                                value={thresholds.mongoConnectionPoolSize}
                                onChange={(e) =>
                                    handleThresholdChange('mongoConnectionPoolSize', parseInt(e.target.value))
                                }
                                className="w-24 px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-500"
                            />
                        </div>
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">Active Connections (%)</label>
                            <input
                                type="text"
                                value={thresholds.mongoActiveConnections}
                                onChange={(e) =>
                                    handleThresholdChange('mongoActiveConnections', parseInt(e.target.value))
                                }
                                className="w-24 px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-500"
                            />
                        </div>
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">Available Connections (%)</label>
                            <input
                                type="text"
                                value={thresholds.mongoAvailableConnections}
                                onChange={(e) =>
                                    handleThresholdChange('mongoAvailableConnections', parseInt(e.target.value))
                                }
                                className="w-24 px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-500"
                            />
                        </div>
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">Query Time (%)</label>
                            <input
                                type="text"
                                value={thresholds.mongoQueryTime}
                                onChange={(e) => handleThresholdChange('mongoQueryTime', parseInt(e.target.value))}
                                className="w-24 px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-500"
                            />
                        </div>
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">Memory Usage (%)</label>
                            <input
                                type="text"
                                value={thresholds.mongoMemoryUsage}
                                onChange={(e) => handleThresholdChange('mongoMemoryUsage', parseInt(e.target.value))}
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
                            onClick={() => {
                                console.log('Saving thresholds:', thresholds);
                                onClose();
                            }}
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
