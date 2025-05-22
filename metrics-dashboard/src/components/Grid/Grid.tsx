import React from 'react';
import { StatCards } from '../StatCards/StatCards.tsx';
import { ActivityGraph } from '../ActivityGraph/ActivityGraph.tsx';
export const Grid = ({ metricType }) => {
    return (
        <div className="px-4 grid gap-3 grid-cols-12">
            <StatCards metricType={metricType} />
            {/* <ActivityGraph /> */}
        </div>
    );
};
