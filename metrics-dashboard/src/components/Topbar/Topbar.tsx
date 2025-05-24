import React, { useState } from 'react';
import { FaEdit } from 'react-icons/fa';
import { SystemMetricsModal } from '../Modals/SystemMetricsModal.tsx';
import { NetworkMetricsModal } from '../Modals/NetworkMetricsModal.tsx';
import { DatabaseMetricsModal } from '../Modals/DatabaseMetricsModal.tsx';
export const Topbar = ({ service, metricType }) => {
    const [isModalOpen, setIsModalOpen] = useState(false);

    return (
        <div className="border-b px-4 mb-4 mt-2 pb-4 border-stone-200 flex items-center justify-between">
            <h1 className="text-lg font-medium text-stone-700">Metrics Overview</h1>
            <button
                className="flex items-center gap-2 px-4 py-2 bg-violet-600 text-white rounded-lg hover:bg-violet-700 transition-colors"
                onClick={() => setIsModalOpen(true)}
            >
                <FaEdit size={16} />
                <span>View and edit thresholds</span>
            </button>
            {metricType === 'system-metrics' && (
                <SystemMetricsModal service={service} isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
            )}
            {metricType === 'network-metrics' && (
                <NetworkMetricsModal service={service} isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
            )}
            {metricType === 'database-metrics' && (
                <DatabaseMetricsModal service={service} isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
            )}
        </div>
    );
};
