import React from 'react';
import { SystemMetricsViews } from '../Views/SystemMetricsViews.tsx';
import { NetworkMetricsView } from '../Views/NetworkMetricsView.tsx';
import { DatabaseMetricsViews } from '../Views/DatabaseMetricsViews.tsx';

export const StatCards = ({ metricType }) => {
    return (
        <div className="col-span-12 grid grid-cols-12 gap-4">
            {metricType === 'system-metrics' && <SystemMetricsViews />}
            {metricType === 'network-metrics' && <NetworkMetricsView />}
            {metricType === 'database-metrics' && <DatabaseMetricsViews />}
        </div>
    );
};
