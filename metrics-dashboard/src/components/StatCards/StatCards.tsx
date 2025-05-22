import React from 'react';

export const StatCards = ({ metricType }) => {
    return (
        <div className="col-span-12 grid grid-cols-12 gap-4">
            <Card title="Total Users" value="1,234" period="Last 30 days" />
            <Card title="Active Sessions" value="567" period="Last 24 hours" />
            <Card title="Revenue" value="$12,345" period="Last 7 days" />
        </div>
    );
};

const Card = ({ title, value, period }) => {
    return (
        <div className="p-4  col-span-4 rounded border border-stone-300">
            <h3 className="text-lg font-semibold">{title}</h3>
            <p className="text-2xl font-bold">{value}</p>
            <p className="text-sm text-gray-400">{period}</p>
        </div>
    );
};
