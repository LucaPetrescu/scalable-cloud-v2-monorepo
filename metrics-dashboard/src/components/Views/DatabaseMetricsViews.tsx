import React, { useState, useEffect } from 'react';
import { useDatabaseMetrics } from '../../hooks/metrics/useDatabaseMetrics.tsx';
import { Card } from '../StatCards/Card.tsx';
import { ActivityGraph } from '../ActivityGraph/ActivityGraph.tsx';
import { MetricData } from '../../types/MetricData.ts';
import { formatMetric } from '../../utils/metricFormatter.ts';
import { metricColors } from '../../utils/graphLineColor.ts';

export const DatabaseMetricsViews = () => {
    const metrics = useDatabaseMetrics();
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

    const formatValueForCardMetrics = (metric) => {
        switch (metric.metricName) {
            case 'mongo_connection_pool_size':
            case 'mongo_active_connections':
                return metric.metricValue.toLocaleString();
            case 'mongo_available_connections':
                return `${metric.metricValue}`;
            default:
                return metric.metricValue.toString();
        }
    };

    const groupedMetricsForGraphs = historicalData.reduce<Record<string, MetricData[]>>((acc, data) => {
        if (data.metricName === 'mongo_query_time_seconds' || data.metricName === 'mongo_memory_usage_bytes') {
            if (!acc[data.metricName]) {
                acc[data.metricName] = [];
            }
            acc[data.metricName].push(data);
        }
        return acc;
    }, {});

    return (
        <>
            {/* Current Metrics Cards */}
            {metrics
                .filter(
                    (metric) =>
                        metric.metricName !== 'mongo_query_time_seconds' &&
                        metric.metricName !== 'mongo_memory_usage_bytes',
                )
                .map((metric) => (
                    <div key={metric.metricName} className="col-span-4">
                        <Card
                            title={formatMetric(metric.metricName)}
                            value={formatValueForCardMetrics(metric)}
                            period={`Last updated: ${new Date(metric.timestamp).toLocaleTimeString()}`}
                        />
                    </div>
                ))}

            {/* Database Metrics Graphs */}
            <div className="col-span-12 mt-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Query Time Graph */}
                    {groupedMetricsForGraphs['mongo_query_time_seconds'] && (
                        <ActivityGraph
                            title="Query Time"
                            metrics={[
                                {
                                    name: 'mongo_query_time_seconds',
                                    data: groupedMetricsForGraphs['mongo_query_time_seconds'].map((d) => ({
                                        timestamp: d.timestamp,
                                        value: d.value,
                                        displayTime: d.displayTime,
                                    })),
                                    color: metricColors['mongo_query_time_seconds'],
                                },
                            ]}
                            isPercentage={false}
                        />
                    )}

                    {/* Memory Usage Graph */}
                    {groupedMetricsForGraphs['mongo_memory_usage_bytes'] && (
                        <ActivityGraph
                            title="Memory Usage"
                            metrics={[
                                {
                                    name: 'mongo_memory_usage_bytes',
                                    data: groupedMetricsForGraphs['mongo_memory_usage_bytes'].map((d) => ({
                                        timestamp: d.timestamp,
                                        value: d.value,
                                        displayTime: d.displayTime,
                                    })),
                                    color: metricColors['mongo_memory_usage_bytes'],
                                },
                            ]}
                            isPercentage={false}
                        />
                    )}
                </div>
            </div>
        </>
    );
};
