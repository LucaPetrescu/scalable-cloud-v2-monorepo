import React, { useState, useEffect } from 'react';
import { useNetworkMetrics } from '../../hooks/metrics/useNetworkMetrics.tsx';
import { Card } from '../StatCards/Card.tsx';
import { ActivityGraph } from '../ActivityGraph/ActivityGraph.tsx';
import { MetricData } from '../../types/MetricData.ts';
import { formatMetric } from '../../utils/metricFormatter.ts';
import { metricColors } from '../../utils/graphLineColor.ts';

export const NetworkMetricsView = () => {
    const metrics = useNetworkMetrics();
    const [historicalData, setHistoricalData] = useState<MetricData[]>([]);

    useEffect(() => {
        if (metrics.length > 0) {
            const newData = metrics
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
    }, [metrics]);

    const groupedMetrics = historicalData.reduce<Record<string, MetricData[]>>((acc, data) => {
        if (!acc[data.metricName]) {
            acc[data.metricName] = [];
        }
        acc[data.metricName].push(data);
        return acc;
    }, {});

    const formatValue = (metric) => {
        return `${metric.metricValue}`;
    };

    return (
        <>
            {/* Current Metrics Cards */}
            {metrics.map((metric) => (
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

            {/* Network Metrics Graphs */}
            <div className="col-span-12 mt-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* HTTP Requests Graph */}
                    {groupedMetrics['http_requests_total'] && (
                        <ActivityGraph
                            isPercentage={false}
                            title="HTTP Requests"
                            metrics={[
                                {
                                    name: 'http_requests_total',
                                    data: groupedMetrics['http_requests_total'].map((d) => ({
                                        timestamp: d.timestamp,
                                        value: d.value,
                                        displayTime: d.displayTime,
                                    })),
                                    color: metricColors['http_requests_total'],
                                },
                            ]}
                        />
                    )}

                    {/* HTTP Request Duration Graph */}
                    {groupedMetrics['http_request_duration_seconds'] && (
                        <ActivityGraph
                            isPercentage={false}
                            title="HTTP Request Duration"
                            metrics={[
                                {
                                    name: 'http_request_duration_seconds',
                                    data: groupedMetrics['http_request_duration_seconds'].map((d) => ({
                                        timestamp: d.timestamp,
                                        value: d.value,
                                        displayTime: d.displayTime,
                                    })),
                                    color: metricColors['http_request_duration_seconds'],
                                },
                            ]}
                        />
                    )}
                </div>
            </div>
        </>
    );
};
