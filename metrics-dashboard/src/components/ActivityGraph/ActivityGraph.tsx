import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export const ActivityGraph = ({ metricType, historicalData, timestamp }) => {
    const formatTime = (timestamp: number) => {
        return new Date(timestamp).toLocaleTimeString();
    };

    return (
        // <div className="col-span-12 grid grid-cols-12 gap-4">
        //     <div className="col-span-6 p-4 rounded border border-stone-300">
        //         <h3 className="text-lg font-semibold mb-4">RAM Usage</h3>
        //         <div className="h-[300px]">
        //             <ResponsiveContainer width="100%" height="100%">
        //                 <LineChart data={[1]}>
        //                     <CartesianGrid strokeDasharray="3 3" />
        //                     <XAxis dataKey="timestamp" tickFormatter={formatTime} tick={{ fontSize: 12 }} />
        //                     <YAxis domain={[0, 100]} tickFormatter={(value) => `${value}%`} tick={{ fontSize: 12 }} />
        //                     <Tooltip
        //                         labelFormatter={formatTime}
        //                         formatter={(value: number) => [`${value.toFixed(2)}%`, 'RAM Usage']}
        //                     />
        //                     <Line type="monotone" dataKey="value" stroke="#82ca9d" dot={false} strokeWidth={2} />
        //                 </LineChart>
        //             </ResponsiveContainer>
        //         </div>
        //     </div>
        // </div>

        <div className="col-span-12 grid grid-cols-12 gap-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-white p-6 rounded-lg shadow">
                    <h3>
                        <h3 className="text-lg font-semibold text-stone-800 mb-4">{metricType}</h3>
                    </h3>
                </div>
            </div>
        </div>
    );
};
