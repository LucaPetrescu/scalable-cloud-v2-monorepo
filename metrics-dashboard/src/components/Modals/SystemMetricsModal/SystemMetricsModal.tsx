import React, { useState } from 'react';
import { IoClose } from 'react-icons/io5';

interface SystemMetricsModalProps {
    isOpen: boolean;
    onClose: () => void;
}

export const SystemMetricsModal = ({ isOpen, onClose }: SystemMetricsModalProps) => {
    const [thresholds, setThresholds] = useState({
        cpu: 80,
        memory: 85,
        disk: 90,
    });

    if (!isOpen) return null;

    const handleThresholdChange = (metric: keyof typeof thresholds, value: number) => {
        setThresholds((prev) => ({
            ...prev,
            [metric]: value,
        }));
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl">
                <div className="flex items-center justify-between p-4 border-b border-stone-200">
                    <h2 className="text-xl font-semibold text-stone-800">System Metrics Thresholds</h2>
                    <button onClick={onClose} className="text-stone-500 hover:text-stone-700 transition-colors">
                        <IoClose size={24} />
                    </button>
                </div>
                <div className="p-6 space-y-6">
                    <div className="space-y-4">
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">CPU Usage Threshold (%)</label>
                            <input
                                type="text"
                                value={thresholds.cpu}
                                onChange={(e) => handleThresholdChange('cpu', parseInt(e.target.value))}
                                className="w-24 px-3 py-2 border border-stone-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-violet-500"
                            />
                        </div>
                        <div className="flex items-center justify-between">
                            <label className="text-stone-700 font-medium">Memory Usage Threshold (%)</label>
                            <input
                                type="text"
                                value={thresholds.memory}
                                onChange={(e) => handleThresholdChange('memory', parseInt(e.target.value))}
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
