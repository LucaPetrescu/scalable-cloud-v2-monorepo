import React, { useState, useEffect } from 'react';
import { useSystemMetrics } from '../../hooks/metrics/useSystemMetrics.tsx';
import { Card } from '../StatCards/Card.tsx';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

interface MetricData {
    timestamp: number;
    displayTime: string;
    cpu: number;
    ram: number;
}

export const SystemMetricsViews = () => {
    const metrics = useSystemMetrics();
    const [historicalData, setHistoricalData] = useState<MetricData[]>([]);

    useEffect(() => {
        if (metrics.length > 0) {
            const newData = metrics
                .map((metric) => {
                    const localTimestamp = Date.now();
                    const displayTime = new Date(localTimestamp).toLocaleTimeString();

                    console.log(metric);

                    if (metric.metricName === 'cpu_usage_percent') {
                        return {
                            timestamp: localTimestamp,
                            displayTime,
                            cpu: metric.metricValue,
                            ram: historicalData[historicalData.length - 1]?.ram || 0,
                        };
                    } else if (metric.metricName === 'ram_usage_percent') {
                        return {
                            timestamp: localTimestamp,
                            displayTime,
                            ram: metric.metricValue,
                            cpu: historicalData[historicalData.length - 1]?.cpu || 0,
                        };
                    }
                    return null;
                })
                .filter(Boolean);

            setHistoricalData((prev) => {
                const updated = [...prev, ...newData];
                // Keep only last 20 data points
                return updated.slice(-20);
            });
        }
    }, [metrics]);

    const formatValue = (metric) => {
        return `${metric.metricValue.toFixed(2)}%`;
    };

    const getTitle = (metricName) => {
        switch (metricName) {
            case 'cpu_usage_percent':
                return 'CPU Usage';
            case 'ram_usage_percent':
                return 'Memory Usage';
            default:
                return metricName;
        }
    };

    const formatXAxis = (timestamp: number) => {
        const date = new Date(timestamp);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    };

    const CustomTooltip = ({ active, payload, label }: any) => {
        if (active && payload && payload.length) {
            return (
                <div className="bg-white p-3 border border-gray-200 rounded shadow">
                    <p className="text-sm font-medium text-gray-700">
                        {new Date(label).toLocaleTimeString([], {
                            hour: '2-digit',
                            minute: '2-digit',
                            second: '2-digit',
                            hour12: false,
                        })}
                    </p>
                    {payload.map((entry: any, index: number) => (
                        <p key={index} className="text-sm" style={{ color: entry.color }}>
                            {entry.name}: {entry.value.toFixed(2)}%
                        </p>
                    ))}
                </div>
            );
        }
        return null;
    };

    return (
        <>
            {/* Current Metrics Cards */}
            {metrics.map((metric) => (
                <div key={metric.metricName} className="col-span-4">
                    <Card
                        title={getTitle(metric.metricName)}
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

            {/* Historical Data Graphs */}
            <div className="col-span-12 mt-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="bg-white p-6 rounded-lg shadow">
                        <h3 className="text-lg font-semibold text-stone-800 mb-4">CPU Usage Over Time</h3>
                        <div className="h-[300px]">
                            <ResponsiveContainer width="100%" height="100%">
                                <LineChart data={historicalData}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis
                                        dataKey="timestamp"
                                        tickFormatter={formatXAxis}
                                        interval="preserveStartEnd"
                                        tick={{ fontSize: 12 }}
                                        angle={-45}
                                        textAnchor="end"
                                        height={60}
                                    />
                                    <YAxis
                                        domain={[0, 100]}
                                        tickFormatter={(value) => `${value}%`}
                                        tick={{ fontSize: 12 }}
                                    />
                                    <Tooltip content={<CustomTooltip />} />
                                    <Line type="monotone" dataKey="cpu" stroke="#8884d8" name="CPU Usage" dot={false} />
                                </LineChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    <div className="bg-white p-6 rounded-lg shadow">
                        <h3 className="text-lg font-semibold text-stone-800 mb-4">Memory Usage Over Time</h3>
                        <div className="h-[300px]">
                            <ResponsiveContainer width="100%" height="100%">
                                <LineChart data={historicalData}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis
                                        dataKey="timestamp"
                                        tickFormatter={formatXAxis}
                                        interval="preserveStartEnd"
                                        tick={{ fontSize: 12 }}
                                        angle={-45}
                                        textAnchor="end"
                                        height={60}
                                    />
                                    <YAxis
                                        domain={[0, 100]}
                                        tickFormatter={(value) => `${value}%`}
                                        tick={{ fontSize: 12 }}
                                    />
                                    <Tooltip content={<CustomTooltip />} />
                                    <Line
                                        type="monotone"
                                        dataKey="ram"
                                        stroke="#82ca9d"
                                        name="Memory Usage"
                                        dot={false}
                                    />
                                </LineChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};
