import React from 'react';
import { StatCards } from '../StatCards/StatCards.tsx';

export const Grid = ({ metricType }) => {
    return (
        <div className="px-4 grid gap-3 grid-cols-12">
            <StatCards metricType={metricType} />
        </div>
    );
};
