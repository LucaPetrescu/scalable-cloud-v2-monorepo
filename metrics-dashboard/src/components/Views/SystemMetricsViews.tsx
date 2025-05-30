import React, { useState, useEffect } from 'react';
import { useSystemMetrics } from '../../hooks/metrics/useSystemMetrics.tsx';
import { Card } from '../StatCards/Card.tsx';
import { ActivityGraph } from '../ActivityGraph/ActivityGraph.tsx';
import { MetricData } from '../../types/MetricData.ts';
import { formatMetric } from '../../utils/metricFormatter.ts';
import { metricColors } from '../../utils/graphLineColor.ts';
import { NotificationsTable } from '../Notifications/NotificationsTable.tsx';
import { useMetrics } from '../../context/MetricsContext.tsx';

export const SystemMetricsViews = () => {
    const [historicalData, setHistoricalData] = useState([]);
    const { systemMetrics } = useMetrics();

    useEffect(() => {
        if (systemMetrics.length > 0) {
            const newData = systemMetrics
                .map((metric) => {
                    const localTimestamp = Date.now();
                    const displayTime = new Date(localTimestamp).toLocaleTimeString();

                    return {
                        metricName: metric.metricName,
                        timestamp: localTimestamp,
                        value: metric.metricValue,
                        displayTime,
                    };
                })
                .filter(Boolean);

            setHistoricalData((prev) => {
                const updated = [...prev, ...newData];

                return updated.slice(-20);
            });
        }
    }, [systemMetrics]);

    const groupedMetrics = historicalData.reduce<Record<string, MetricData[]>>((acc, data) => {
        if (!acc[data.metricName]) {
            acc[data.metricName] = [];
        }
        acc[data.metricName].push(data);
        return acc;
    }, {});

    const formatValue = (metric) => {
        return `${metric.metricValue.toFixed(2)}%`;
    };

    return (
        <>
            {/* Current Metrics Cards */}
            {systemMetrics.map((metric) => (
                <div key={metric.metricName} className="col-span-4">
                    <Card
                        title={formatMetric(metric.metricName)}
                        value={formatValue(metric)}
                        period={`Last updated: ${new Date(Date.now()).toLocaleTimeString([], {
                            hour: '2-digit',
                            minute: '2-digit',
                            second: '2-digit',
                            hour12: false,
                        })}`}
                    />
                </div>
            ))}

            {/* System Metrics Graphs */}
            <div className="col-span-12 mt-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* CPU Graph */}
                    {groupedMetrics['cpu_usage_percent'] && (
                        <ActivityGraph
                            isPercentage={true}
                            title={formatMetric('cpu_usage_percent')}
                            metrics={[
                                {
                                    name: 'cpu_usage_percent',
                                    data: groupedMetrics['cpu_usage_percent'].map((d) => ({
                                        timestamp: d.timestamp,
                                        value: d.value,
                                        displayTime: d.displayTime,
                                    })),
                                    color: metricColors['cpu_usage_percent'],
                                },
                            ]}
                        />
                    )}

                    {/* RAM Graph */}
                    {groupedMetrics['ram_usage_percent'] && (
                        <ActivityGraph
                            isPercentage={true}
                            title={formatMetric('ram_usage_percent')}
                            metrics={[
                                {
                                    name: 'ram_usage_percent',
                                    data: groupedMetrics['ram_usage_percent'].map((d) => ({
                                        timestamp: d.timestamp,
                                        value: d.value,
                                        displayTime: d.displayTime,
                                    })),
                                    color: metricColors['ram_usage_percent'],
                                },
                            ]}
                        />
                    )}
                </div>
            </div>
            <NotificationsTable />
        </>
    );
};
