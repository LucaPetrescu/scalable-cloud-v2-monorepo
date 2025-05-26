import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { formatMetric } from '../../utils/metricFormatter.ts';

interface ActivityGraphProps {
    metrics: {
        name: string;
        data: Array<{
            timestamp: number;
            value: number;
            displayTime: string;
        }>;
        color: string;
    }[];
    title: string;
    isPercentage: boolean;
}

export const ActivityGraph = ({ metrics, title, isPercentage }: ActivityGraphProps) => {
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
        <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-lg font-semibold text-stone-800 mb-4">{title}</h3>
            <div className="h-[300px]">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart>
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
                        {isPercentage ? (
                            <YAxis domain={[0, 100]} tickFormatter={(value) => `${value}%`} tick={{ fontSize: 12 }} />
                        ) : (
                            <YAxis domain={[0, 1000]} tickFormatter={(value) => `${value}`} tick={{ fontSize: 12 }} />
                        )}

                        <Tooltip content={<CustomTooltip />} />
                        {metrics.map((metric) => (
                            <Line
                                key={metric.name}
                                type="monotone"
                                data={metric.data}
                                dataKey="value"
                                stroke={metric.color}
                                dot={false}
                                strokeWidth={2}
                                name={formatMetric(metric.name)}
                            />
                        ))}
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};
