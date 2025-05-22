import React from 'react';

export const Card = ({ title, value, period }) => {
    return (
        <div className="p-4 col-span-4 rounded border border-stone-300">
            <h3 className="text-lg font-semibold">{title}</h3>
            <p className="text-2xl font-bold">{value}</p>
            <p className="text-sm text-gray-400">{period}</p>
        </div>
    );
};
